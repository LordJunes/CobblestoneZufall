#!/usr/bin/env python3
import json
import os
import queue
import threading
import time
from dataclasses import dataclass, asdict
from datetime import datetime
from pathlib import Path
import tkinter as tk
from tkinter import ttk, messagebox, filedialog


APP_TITLE = "Cobble Debug Companion"
POLL_SECONDS = 0.25
MAX_UI_LINES = 1200


GUIDE_STEPS = [
    "1) Klicke 'Session Start'.",
    "2) Starte Hytale/Testwelt und oeffne /cobadmin.",
    "3) Oeffne 'Add Block' und klicke ins Suchfeld.",
    "4) Tippe langsam: g -> o -> l.",
    "5) Sobald Fehler passiert: klicke 'Marker: Fehler jetzt'.",
    "6) 5-10 Sekunden warten, dann 'Session Stop'.",
    "7) Klicke 'Export Bundle' und sende mir die Datei.",
]


def now_iso() -> str:
    return datetime.now().isoformat(timespec="milliseconds")


def now_file_stamp() -> str:
    return datetime.now().strftime("%Y-%m-%d_%H-%M-%S")


def appdata_hytale_dir() -> Path:
    appdata = os.environ.get("APPDATA", "")
    return Path(appdata) / "Hytale" / "UserData"


def newest_log_file(folder: Path, pattern: str) -> Path | None:
    if not folder.exists():
        return None
    matches = sorted(folder.glob(pattern), key=lambda p: p.stat().st_mtime, reverse=True)
    return matches[0] if matches else None


@dataclass
class Marker:
    timestamp: str
    unix_ms: int
    note: str


class TailWorker(threading.Thread):
    def __init__(self, files: dict[str, Path], out_queue: queue.Queue):
        super().__init__(daemon=True)
        self.files = files
        self.out_queue = out_queue
        self.stop_event = threading.Event()
        self.offsets: dict[str, int] = {}

    def initialize_offsets(self):
        for source, path in self.files.items():
            try:
                self.offsets[source] = path.stat().st_size
            except Exception:
                self.offsets[source] = 0

    def run(self):
        self.initialize_offsets()
        while not self.stop_event.is_set():
            for source, path in self.files.items():
                self._poll_file(source, path)
            time.sleep(POLL_SECONDS)

    def _poll_file(self, source: str, path: Path):
        if not path.exists():
            return
        try:
            current_size = path.stat().st_size
            previous = self.offsets.get(source, 0)
            if current_size < previous:
                previous = 0
            if current_size == previous:
                return
            with path.open("r", encoding="utf-8", errors="replace") as fh:
                fh.seek(previous)
                chunk = fh.read()
                self.offsets[source] = fh.tell()
            if chunk:
                for line in chunk.splitlines():
                    self.out_queue.put((source, line))
        except Exception as exc:
            self.out_queue.put(("system", f"[tail-error] {source}: {exc}"))

    def stop(self):
        self.stop_event.set()


class App:
    def __init__(self, root: tk.Tk):
        self.root = root
        self.root.title(APP_TITLE)
        self.root.geometry("1220x760")

        self.log_queue: queue.Queue = queue.Queue()
        self.tail_worker: TailWorker | None = None
        self.is_running = False

        self.session_started_at: str | None = None
        self.session_lines: list[dict] = []
        self.markers: list[Marker] = []
        self.step_index = 0

        self.client_log_path: Path | None = None
        self.server_log_path: Path | None = None
        self._build_ui()
        self._refresh_paths()
        self.root.after(120, self._drain_queue)

    def _build_ui(self):
        top = ttk.Frame(self.root, padding=10)
        top.pack(fill="x")

        self.start_btn = ttk.Button(top, text="Session Start", command=self.start_session)
        self.stop_btn = ttk.Button(top, text="Session Stop", command=self.stop_session, state="disabled")
        self.marker_btn = ttk.Button(top, text="Marker: Fehler jetzt", command=self.add_marker, state="disabled")
        self.export_btn = ttk.Button(top, text="Export Bundle", command=self.export_bundle)
        self.refresh_btn = ttk.Button(top, text="Log Paths neu laden", command=self._refresh_paths)
        self.next_step_btn = ttk.Button(top, text="Naechster Schritt", command=self.next_step)

        for btn in [self.start_btn, self.stop_btn, self.marker_btn, self.export_btn, self.refresh_btn, self.next_step_btn]:
            btn.pack(side="left", padx=4)

        path_frame = ttk.Frame(self.root, padding=(10, 0, 10, 8))
        path_frame.pack(fill="x")
        self.client_path_var = tk.StringVar(value="Client Log: -")
        self.server_path_var = tk.StringVar(value="Server Log: -")
        ttk.Label(path_frame, textvariable=self.client_path_var).pack(anchor="w")
        ttk.Label(path_frame, textvariable=self.server_path_var).pack(anchor="w")

        guide = ttk.LabelFrame(self.root, text="Gefuehrte Schritte", padding=10)
        guide.pack(fill="x", padx=10, pady=(0, 8))
        self.step_var = tk.StringVar(value=GUIDE_STEPS[0])
        ttk.Label(guide, textvariable=self.step_var, font=("Segoe UI", 10, "bold")).pack(anchor="w")
        ttk.Label(
            guide,
            text="Tipp: Wenn ein Ladebildschirm/Fokusverlust kommt, sofort Marker klicken und normal weitermachen.",
        ).pack(anchor="w")

        body = ttk.Panedwindow(self.root, orient=tk.HORIZONTAL)
        body.pack(fill="both", expand=True, padx=10, pady=(0, 10))

        left = ttk.Frame(body)
        right = ttk.Frame(body)
        body.add(left, weight=4)
        body.add(right, weight=1)

        ttk.Label(left, text="Live Log (Client + Server)").pack(anchor="w")
        self.log_text = tk.Text(left, wrap="none", height=35)
        self.log_text.pack(fill="both", expand=True)
        self.log_text.configure(state="disabled")

        self.log_text.tag_configure("server", foreground="#d8f5d0")
        self.log_text.tag_configure("client", foreground="#d2e6ff")
        self.log_text.tag_configure("system", foreground="#ffd8d8")
        self.log_text.tag_configure("marker", foreground="#fff0a5")

        self.line_count_var = tk.StringVar(value="Lines captured: 0")
        ttk.Label(left, textvariable=self.line_count_var).pack(anchor="w", pady=(4, 0))

        ttk.Label(right, text="Marker").pack(anchor="w")
        self.marker_list = tk.Listbox(right, height=20)
        self.marker_list.pack(fill="both", expand=True)

        ttk.Button(right, text="Marker hinzufuegen", command=self.add_marker).pack(fill="x", pady=(8, 4))
        ttk.Button(right, text="Marker loeschen", command=self.remove_marker).pack(fill="x")

    def _refresh_paths(self):
        user_data = appdata_hytale_dir()
        client_folder = user_data / "Logs"
        server_folder = user_data / "Saves" / "test2" / "logs"

        self.client_log_path = newest_log_file(client_folder, "*_client.log")
        self.server_log_path = newest_log_file(server_folder, "*_server.log")

        self.client_path_var.set(f"Client Log: {self.client_log_path or 'NICHT GEFUNDEN'}")
        self.server_path_var.set(f"Server Log: {self.server_log_path or 'NICHT GEFUNDEN'}")

    def start_session(self):
        self._refresh_paths()
        if not self.client_log_path or not self.server_log_path:
            messagebox.showerror(APP_TITLE, "Client oder Server Log nicht gefunden. Erst Hytale + Welt starten.")
            return
        if self.is_running:
            return

        self.is_running = True
        self.session_started_at = now_iso()
        self.session_lines.clear()
        self.markers.clear()
        self.marker_list.delete(0, tk.END)
        self._clear_log_view()

        files = {"client": self.client_log_path, "server": self.server_log_path}
        self.tail_worker = TailWorker(files, self.log_queue)
        self.tail_worker.start()

        self.start_btn.configure(state="disabled")
        self.stop_btn.configure(state="normal")
        self.marker_btn.configure(state="normal")

        self._append_system_line("Session started.")

    def stop_session(self):
        if not self.is_running:
            return
        self.is_running = False
        if self.tail_worker:
            self.tail_worker.stop()
            self.tail_worker = None

        self.start_btn.configure(state="normal")
        self.stop_btn.configure(state="disabled")
        self.marker_btn.configure(state="disabled")
        self._append_system_line("Session stopped.")

    def add_marker(self):
        note = "Fehler jetzt"
        marker = Marker(timestamp=now_iso(), unix_ms=int(time.time() * 1000), note=note)
        self.markers.append(marker)
        self.marker_list.insert(tk.END, f"{marker.timestamp} | {note}")
        self._append_tagged("marker", f"[MARKER] {marker.timestamp} ms={marker.unix_ms} {note}")

    def remove_marker(self):
        sel = self.marker_list.curselection()
        if not sel:
            return
        idx = sel[0]
        if 0 <= idx < len(self.markers):
            self.markers.pop(idx)
        self.marker_list.delete(idx)

    def next_step(self):
        self.step_index = (self.step_index + 1) % len(GUIDE_STEPS)
        self.step_var.set(GUIDE_STEPS[self.step_index])

    def _drain_queue(self):
        try:
            while True:
                source, line = self.log_queue.get_nowait()
                entry = {"t": now_iso(), "source": source, "line": line}
                self.session_lines.append(entry)
                self._append_tagged(source, f"[{source.upper()}] {line}")
        except queue.Empty:
            pass
        finally:
            self.line_count_var.set(f"Lines captured: {len(self.session_lines)}")
            self.root.after(120, self._drain_queue)

    def _append_system_line(self, text: str):
        self.session_lines.append({"t": now_iso(), "source": "system", "line": text})
        self._append_tagged("system", f"[SYSTEM] {text}")

    def _append_tagged(self, tag: str, text: str):
        self.log_text.configure(state="normal")
        self.log_text.insert("end", text + "\n", tag if tag in ("server", "client", "system", "marker") else "system")
        lines = int(self.log_text.index("end-1c").split(".")[0])
        if lines > MAX_UI_LINES:
            self.log_text.delete("1.0", f"{lines - MAX_UI_LINES}.0")
        self.log_text.see("end")
        self.log_text.configure(state="disabled")

    def _clear_log_view(self):
        self.log_text.configure(state="normal")
        self.log_text.delete("1.0", "end")
        self.log_text.configure(state="disabled")

    def export_bundle(self):
        if not self.session_lines:
            messagebox.showwarning(APP_TITLE, "Keine Daten vorhanden. Erst Session starten.")
            return
        default_name = f"cobble_debug_bundle_{now_file_stamp()}.json"
        out = filedialog.asksaveasfilename(
            title="Debug Bundle speichern",
            defaultextension=".json",
            initialfile=default_name,
            filetypes=[("JSON", "*.json"), ("All files", "*.*")],
        )
        if not out:
            return

        payload = {
            "tool": APP_TITLE,
            "exported_at": now_iso(),
            "session_started_at": self.session_started_at,
            "client_log_path": str(self.client_log_path) if self.client_log_path else "",
            "server_log_path": str(self.server_log_path) if self.server_log_path else "",
            "markers": [asdict(m) for m in self.markers],
            "lines": self.session_lines,
        }
        try:
            with open(out, "w", encoding="utf-8") as fh:
                json.dump(payload, fh, ensure_ascii=True, indent=2)
            messagebox.showinfo(APP_TITLE, f"Export fertig:\n{out}")
        except Exception as exc:
            messagebox.showerror(APP_TITLE, f"Export fehlgeschlagen:\n{exc}")


def main():
    root = tk.Tk()
    style = ttk.Style(root)
    try:
        style.theme_use("clam")
    except Exception:
        pass
    App(root)
    root.mainloop()


if __name__ == "__main__":
    main()
