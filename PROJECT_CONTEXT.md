# PROJECT_CONTEXT

## 1) Projektueberblick

### Name
- `CobblestoneZufall`

### Typ
- Hytale Server-Mod (Java)
- Build ueber Gradle + ShadowJar

### Hauptziel
- Progressionssystem fuer Cobblestone-Generatoren:
  - Tier-basierte Drops
  - Ingame-Admin-Editing fuer Drop-Wahrscheinlichkeiten und Per-Drop-Werte
  - interne Economy
  - Upgrade-Flow fuer Spieler
  - konfigurierbares Einsammelverhalten (`Collect Range` + `RangeTyp`)

---

## 2) Runtime-Architektur

### Plugin Entry
- Hauptklasse: `com.minecraft_ceo.cobblestonezufall.CobblestoneZufallPlugin`
- Manifest-Hauptklasse: `src/main/resources/manifest.json` -> `"Main": "com.minecraft_ceo.cobblestonezufall.CobblestoneZufallPlugin"`

### Registrierte Kernsysteme
- `CobblestoneGeneratorSystem`
- `CobbleNaturalGenerationOverrideSystem`

### Registrierte Commands
- `/cob` (Spieler-Upgrade-UI)
- `/cobadmin` (Admin-UI)
- `/money` (Spieler-Economy)
- `/ecoadmin` (Admin-Economy)

### Beim Setup geladen
- `ConfigManager.load()`
- `MiningRateTracker.load()`
- `EconomyBridge.load()`
- `ScoreboardPlaceholderBridge.register()`

### Beim Shutdown gespeichert
- `EconomyBridge.save()`
- `MiningRateTracker.save()`
- Placeholder unregister

---

## 3) Generator-Logik (Core Gameplay)

### Trigger
- `BreakBlockEvent` in `CobblestoneGeneratorSystem`

### Generator-Erkennung
- Zielblock ist ein â€žmanaged generator blockâ€œ:
  - `Rock_Stone_Cobble`
  - oder eine in den Tier-Drops konfigurierte Block-ID
- Zusaetzlich: Umgebung muss Lava + Wasser-Nachbarschaft haben

### Regen-Schutz
- Fuer jede Generator-Position wird `PENDING_REGEN_UNTIL` gefuehrt
- Breaks im aktiven Regenfenster werden gecancelt

### Regeneration
- Nach konfigurierter Verzoegerung (`regenDelayMs`) wird neuer Block gesetzt
- Ersatzblock wird per Tier-Drop-Random bestimmt
- Fallback auf ersten validen Tier-Block, falls Asset ungueltig

---

## 4) Drop-Profil, Werte-Trennung und Belohnung

### Wichtiges Verhalten (final)
- `Drop Amount`, `Pay Amount`, `Repair Chance`, `Repair Amount` werden ueber das **tatsaechlich abgebaute sichtbare Drop-Profil** aufgeloest.
- Das verhindert Vermischung zwischen Cobble und anderen Ores.

### Aufloesung des Profils
- Zuerst direkte Match-Suche per `brokenBlockId` gegen Tier-Drop-IDs
- Dann Item-Mapping-Fallback (Block -> Item)
- Falls kein Profil gefunden: Fallback auf aktuell random gewaehlten Tier-Drop

### Economy-/Repair-Anwendung
- Economy-Zahlung (`payAmount`) pro Break des aufgeloesten Profils
- Tool-Repair:
  - Chance in %
  - Restore-Betrag in % der maximalen Haltbarkeit

---

## 5) Ore Block -> Ore Item Mapping (explizit)

Folgendes Mapping ist fest eingebaut:
- `Ore_Copper_Stone` -> `Ore_Copper`
- `Ore_Iron_Stone` -> `Ore_Iron`
- `Ore_Gold_Stone` -> `Ore_Gold`
- `Ore_Silver_Stone` -> `Ore_Silver`
- `Ore_Cobalt_Shale` -> `Ore_Cobalt`
- `Ore_Thorium_Mud` -> `Ore_Thorium`
- `Ore_Adamantite_Magma` -> `Ore_Adamantite`
- `Ore_Mithril_Stone` -> `Ore_Mithril`

Dieses Mapping steuert die tatsaechlich gutgeschriebene Item-ID.

---

## 6) Collect-System (Range + RangeTyp)

### Collect Range
- Admin-UI schaltet zwischen:
  - `VANILLA` (0)
  - `RANGE 1..32 BLOCKS`

### RangeTyp
- Schaltbar in Admin-UI:
  - `RangeTyp: Teleport` (intern `-1`)
  - `RangeTyp: 100ms .. 5000ms` (100er Schritte)

### Verhalten im Detail
- Bei aktivem Range-Collect wird pro Break-Trace ein passendes Drop-Entity nahe der Source gesucht.
- Zielstack wird auf konfiguriertes `Drop Amount` gesetzt.

#### Teleport
- Sofortige Inventar-Gutschrift
- Source-Entity wird hart entfernt (`ItemStack.EMPTY`, `removedByPlayerPickup`, Entity remove)
- Ziel: kein visuelles Rest-Item, kein Doppelloot

#### 100..5000ms
- Visuelle Reise ueber `PickupItemComponent(..., travelSeconds)`
- Nach Ablauf der Dauer zusaetzliche serverseitige Inventar-Gutschrift + Entity-Remove als Safety
- Ziel: nie verlorene Einsammlung

---

## 7) Admin-UI / Spieler-UI

### `/cobadmin` Hauptseite (`CobbleConfigPage`)
- Tier-Info
- Block Delay (`regenDelayMs`)
- Expected BPS
- Collect Range Toggle
- RangeTyp Toggle (L/R Click)
- Per-Drop-Liste (max 12 sichtbare Zeilen), sortiert nach Chance
- Einstieg in:
  - Curve Model (`CobbleCurvePage`)
  - Drop Editor (`CobbleDropEditPage`)

### Drop Editor (`CobbleDropEditPage`)
- Felder pro Drop:
  - `Drop Amount` (int >= 1)
  - `Pay Amount` (double >= 0)
  - `Repair Chance` (0..100)
  - `Repair Amount %` (>= 0)

### Curve Model (`CobbleCurvePage` + Preview)
- Modell fuer Tier-Progression:
  - Max Tier
  - Max Upgrade Cost
  - Chance-Kurve
  - Cost-Kurve
  - Exponenten/Modi
  - Start-/End-Chancen pro Item
- Apply auf alle Tiers moeglich

### `/cob` Spielerseite (`CobUpgradePage`)
- Upgrade-Flow mit Bestaetigung
- Anzeige aktueller/naechster Werte
- Balance-/Kosten-Interaktion ueber interne Economy

---

## 8) Economy

### Implementierung
- `EconomyBridge` (interne Waehrung, keine Vault-Pflicht)

### Commands
- `/money`
  - Balance
  - `pay <player> <amount>`
- `/ecoadmin`
  - `give|set|take|balance <player|me> <amount?>`
  - Admin-Permissions:
    - `cob.admin` oder `money.admin` oder `op` oder `*`

---

## 9) Placeholder-System (BetterScoreboard)

### Bridge
- `ScoreboardPlaceholderBridge`
- Reflection-basiert (optional, ohne compile-time hard dependency)

### Registrierte Placeholder
- Tier:
  - `{cob_tier}`
  - `{cob_next_tier}`
  - `{cob_max_tier}`
- Economy:
  - `{money}`
- Mining:
  - `{avg_blocks_sec}`
  - `{avg_blocks_min}`
  - `{est_blocks_hr}`
  - `{avg_blocks_hr}`
  - `{total_blocks}`

### Berechnungen
- `avg_blocks_sec`:
  - Sofort hochrechnender Sekundenwert (fruehe Phase nutzt bisher verstrichene Zeit, capped auf 60s Fenster)
- `avg_blocks_min`:
  - `avg_blocks_sec * 60`
- `est_blocks_hr`:
  - `avg_blocks_min * 60`
- `avg_blocks_hr`:
  - echte rolling letzte 60 Minuten
- `total_blocks`:
  - persistent, lebenszeitbezogen

### Formatierung
- Stat-Werte: 2 Nachkommastellen
- `total_blocks`: Tausendertrennung, keine Nachkommastellen

### Token-Normalisierung
- Bekannte BetterScoreboard-Tokenvarianten werden in Save-Configs normalisiert (z. B. GroÃŸ/Kleinschreibung).

---

## 10) Persistente Daten

### Hauptdateien
- `cobble_config.json`
  - Tiers
  - Drops
  - Upgrades
  - Curve Model
  - Player Tier
  - Collect Range / RangeTyp
  - Regen Delay / Expected BPS

- `CobPlayerSaveTable.csv`
  - Export PlayerUUID -> Tier

- `CobMiningTotals.csv`
  - Persistente Lebenszeit-Mining-Totals fuer Placeholder

### Economy Storage
- Ãœber `EconomyBridge` intern verwaltet (eigene Persistenz)

---

## 11) Build / Release / Deploy

### Toolchain
- Java Toolchain in Gradle: `25`
- `gradle.properties` enthaelt:
  - `modVersion=1.2.5`
  - `org.gradle.java.home` (lokal gesetzt)

### Artefakt
- `build/libs/cobblestonezufall-<version>.jar`
- ShadowJar aktiv, Standard-`jar` deaktiviert

### Wichtige Gradle Tasks
- `build`
- `shadowJar`
- `deployToHytaleMods`
  - kopiert JAR nach `%APPDATA%/Hytale/UserData/Mods`
  - verschiebt alte `cobblestonezufall-*.jar` nach `Mods/Backup`
- `bumpModVersion`
- `releaseMod`

---

## 12) Ressourcen / UI-Dateien

Unter `src/main/resources/Common/UI/Custom/Pages`:
- `CobbleConfig.ui`
- `CobbleCurveModel.ui`
- `CobbleCurvePreview.ui`
- `CobbleDropEdit.ui`
- `CobUpgrade.ui`

Weitere Ressource:
- `src/main/resources/data/cob_levels.csv` (Default-Tier-Daten)

---

## 13) Wichtige Klassen (Aktueller Stand)

- `CobblestoneZufallPlugin`
- `CobblestoneGeneratorSystem`
- `CobbleNaturalGenerationOverrideSystem`
- `ConfigManager`
- `EconomyBridge`
- `MiningRateTracker`
- `ScoreboardPlaceholderBridge`
- `CobCommand`
- `CobAdminCommand`
- `MoneyCommand`
- `EcoAdminCommand`
- `CobbleConfigPage`
- `CobbleCurvePage`
- `CobbleCurvePreviewPage`
- `CobbleDropEditPage`
- `CobUpgradePage`

---

## 14) Operations / Wartung

### Debug
- Generator-Debug ist aktuell standardmaeÃŸig deaktiviert (`DEBUG_VERBOSE = false`).

### Backups
- Vor aggressivem Cleanup wurde ein Vollbackup erzeugt:
  - `C:\Users\Lord Junes\Desktop\Mod Erstellung\CobblestoneZufall_BACKUP_20260225_024331`

### Projekt-Cleanup (bereits durchgefuehrt)
- Temporaere Analyseartefakte (`tmp_*`, etc.) entfernt.
- Unnoetige Alt-/Demo-Strukturen entfernt.
- Unbenutzte/leere Klassen entfernt.

---

## 15) Bekannte Hinweise

- `README.md` ist aktuell historisch und enthaelt nicht mehr den kompletten finalen Stand.
- Primaere aktuelle Referenz ist diese `PROJECT_CONTEXT.md`.
- Beim Build erscheint eine Deprecation-Warnung (`Player.getPlayerRef()`), funktional derzeit unkritisch.

---

## 16) Kurz-Fazit

Die Mod ist im aktuellen Stand funktional stabil mit:
- sauber getrennten Per-Drop-Werten,
- zuverlaessigem RangeCollect inkl. Teleport ohne Doppelloot,
- korrektem Ore-Item-Mapping,
- persistenter Mining-Statistik und vollstaendiger Placeholder-Bridge,
- und produktionsnaher Build/Deploy-Kette.

