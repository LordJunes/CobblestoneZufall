package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.logger.HytaleLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class CobbleBlockPickerPage extends InteractiveCustomUIPage<CobbleBlockPickerPage.Data> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final int ROWS = 12;

    public static final class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("Back", Codec.STRING), (d, v) -> d.back = v, d -> d.back).add()
                .append(new KeyedCodec<>("Close", Codec.STRING), (d, v) -> d.close = v, d -> d.close).add()
                .append(new KeyedCodec<>("AddIndex", Codec.STRING), (d, v) -> d.addIndex = v, d -> d.addIndex).add()
                .append(new KeyedCodec<>("ScrollUp", Codec.STRING), (d, v) -> d.scrollUp = v, d -> d.scrollUp).add()
                .append(new KeyedCodec<>("ScrollDown", Codec.STRING), (d, v) -> d.scrollDown = v, d -> d.scrollDown).add()
                .append(new KeyedCodec<>("LiveSearch", Codec.STRING), (d, v) -> d.liveSearch = v, d -> d.liveSearch).add()
                .append(new KeyedCodec<>("ApplySearch", Codec.STRING), (d, v) -> d.applySearch = v, d -> d.applySearch).add()
                .append(new KeyedCodec<>("ClearSearch", Codec.STRING), (d, v) -> d.clearSearch = v, d -> d.clearSearch).add()
                .append(new KeyedCodec<>("DbgInputValueChanged", Codec.STRING), (d, v) -> d.dbgInputValueChanged = v, d -> d.dbgInputValueChanged).add()
                .append(new KeyedCodec<>("DbgInputKeyDown", Codec.STRING), (d, v) -> d.dbgInputKeyDown = v, d -> d.dbgInputKeyDown).add()
                .append(new KeyedCodec<>("DbgInputFocusGained", Codec.STRING), (d, v) -> d.dbgInputFocusGained = v, d -> d.dbgInputFocusGained).add()
                .append(new KeyedCodec<>("DbgInputFocusLost", Codec.STRING), (d, v) -> d.dbgInputFocusLost = v, d -> d.dbgInputFocusLost).add()
                .append(new KeyedCodec<>("DbgInputValidating", Codec.STRING), (d, v) -> d.dbgInputValidating = v, d -> d.dbgInputValidating).add()
                .append(new KeyedCodec<>("@SearchInput", Codec.STRING), (d, v) -> d.searchInput = v, d -> d.searchInput).add()
                .build();

        public String back;
        public String close;
        public String addIndex;
        public String scrollUp;
        public String scrollDown;
        public String liveSearch;
        public String applySearch;
        public String clearSearch;
        public String dbgInputValueChanged;
        public String dbgInputKeyDown;
        public String dbgInputFocusGained;
        public String dbgInputFocusLost;
        public String dbgInputValidating;
        public String searchInput;
    }

    private final PlayerRef playerRef;
    private final ConfigManager configManager;
    private final int returnTier;
    private final List<String> allBlocks;
    private final List<String> visible = new ArrayList<>();
    private String search = "";
    private int offset = 0;
    private String status = "";
    private boolean syncSearchInput = true;
    private long debugEventCounter = 0L;

    public CobbleBlockPickerPage(PlayerRef playerRef, ConfigManager configManager, int returnTier) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
        this.playerRef = playerRef;
        this.configManager = configManager;
        this.returnTier = returnTier;
        this.allBlocks = new ArrayList<>(configManager.getAllBlockIds());
    }

    public static void open(Ref<EntityStore> entityRef,
                            Store<EntityStore> store,
                            PlayerRef playerRef,
                            ConfigManager configManager,
                            int returnTier) {
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            return;
        }
        player.getPageManager().openCustomPage(entityRef, store, new CobbleBlockPickerPage(playerRef, configManager, returnTier));
    }

    @Override
    public void build(Ref<EntityStore> entityRef, UICommandBuilder c, UIEventBuilder e, Store<EntityStore> store) {
        c.append("Pages/CobbleBlockPicker.ui");
        if (syncSearchInput) {
            c.set("#SearchInput.Value", search == null ? "" : search);
            syncSearchInput = false;
        }

        List<String> filtered = new ArrayList<>();
        String lowered = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        for (String id : allBlocks) {
            if (id == null || id.isBlank()) {
                continue;
            }
            if (lowered.isBlank() || id.toLowerCase(Locale.ROOT).contains(lowered)) {
                filtered.add(id);
            }
        }

        offset = Math.max(0, Math.min(offset, Math.max(0, filtered.size() - ROWS)));
        int from = filtered.isEmpty() ? 0 : offset + 1;
        int to = Math.min(filtered.size(), offset + ROWS);
        c.set("#CountInfo.Text", "Results: " + filtered.size() + " | Showing " + from + "-" + to + " (Scroll)");
        c.set("#StatusInfo.Text", status == null ? "" : status);
        debug("build search='" + search + "' filtered=" + filtered.size() + " offset=" + offset);

        visible.clear();
        for (int i = 0; i < ROWS; i++) {
            String row = "#Entry" + i;
            int idx = offset + i;
            if (idx >= filtered.size()) {
                c.set(row + ".Visible", false);
                continue;
            }
            String id = filtered.get(idx);
            visible.add(id);
            c.set(row + ".Visible", true);
            c.set(row + "Icon.ItemId", id);
            c.set(row + "Name.Text", id);
            c.set(row + "Add.Text", "ADD");
            e.addEventBinding(CustomUIEventBindingType.Activating, row + "Add", payload(EventData.of("AddIndex", String.valueOf(i))));
        }

        e.addEventBinding(CustomUIEventBindingType.Activating, "#BackButton", EventData.of("Back", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton", EventData.of("Close", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ClearButton", EventData.of("ClearSearch", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ApplySearchButton", payload(EventData.of("ApplySearch", "1")));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ResultUpButton", EventData.of("ScrollUp", "1"));
        e.addEventBinding(CustomUIEventBindingType.Activating, "#ResultDownButton", EventData.of("ScrollDown", "1"));
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> entityRef, Store<EntityStore> store, Data d) {
        if (d == null) {
            return;
        }
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            close();
            return;
        }

        if (isTruthy(d.close)) {
            close();
            return;
        }
        if (isTruthy(d.back)) {
            CobbleConfigPage.open(entityRef, store, playerRef, configManager, returnTier);
            return;
        }
        if (isTruthy(d.clearSearch)) {
            search = "";
            offset = 0;
            syncSearchInput = true;
            debug("clear search");
            rebuild();
            return;
        }

        status = "";

        logInputDebugEvents(d);

        if (isTruthy(d.liveSearch)) {
            debug("liveSearch ignored (disabled for stability)");
            return;
        }

        if (isTruthy(d.applySearch)) {
            String next = d.searchInput == null ? "" : d.searchInput;
            if (!next.equals(search)) {
                search = next;
                offset = 0;
                syncSearchInput = false;
                debug("applySearch -> '" + next + "'");
                rebuild();
            }
            return;
        }

        if (d.searchInput != null && !d.searchInput.equals(search)) {
            search = d.searchInput;
            offset = 0;
            debug("event search update -> '" + search + "'");
        }
        if (isTruthy(d.scrollUp)) {
            offset = Math.max(0, offset - 1);
            rebuild();
            return;
        }
        if (isTruthy(d.scrollDown)) {
            offset = offset + 1;
            rebuild();
            return;
        }
        if (isTruthy(d.addIndex)) {
            int index = parseIntSafe(d.addIndex, -1);
            if (index >= 0 && index < visible.size()) {
                String id = visible.get(index);
                boolean changed = configManager.addManagedBlock(id);
                if (changed) {
                    status = "Added: " + id;
                    player.sendMessage(Message.raw("[CobblestoneZufall] Added block: " + id));
                } else {
                    status = id + " already exists.";
                }
                syncSearchInput = false;
                rebuild();
            }
        }
    }

    private static EventData payload(EventData d) {
        return d.append("@SearchInput", "#SearchInput.Value");
    }

    private static void bindInputChange(UIEventBuilder e, String selector, EventData payload) {}

    private static void bindDebugInputEvents(UIEventBuilder e, String selector) {}

    private static void bindOptionalInputEvent(UIEventBuilder e, String selector, String eventName, String dataKey) {}

    private void logInputDebugEvents(Data d) {
        if (!configManager.isDebugEnabled()) {
            return;
        }
        debugLogInputEvent("ValueChanged", d.dbgInputValueChanged, d.searchInput);
        debugLogInputEvent("KeyDown", d.dbgInputKeyDown, d.searchInput);
        debugLogInputEvent("FocusGained", d.dbgInputFocusGained, d.searchInput);
        debugLogInputEvent("FocusLost", d.dbgInputFocusLost, d.searchInput);
        debugLogInputEvent("Validating", d.dbgInputValidating, d.searchInput);
    }

    private void debugLogInputEvent(String type, String flag, String input) {
        if (!isTruthy(flag)) {
            return;
        }
        debugEventCounter++;
        long ts = System.currentTimeMillis();
        String normalized = input == null ? "" : input;
        debug("inputEvent #" + debugEventCounter
                + " type=" + type
                + " ts=" + ts
                + " len=" + normalized.length()
                + " value='" + normalized.replace("\n", "\\n").replace("\r", "\\r") + "'");
    }

    private static boolean isTruthy(String value) {
        return value != null && !value.isBlank() && !"0".equals(value.trim());
    }

    private static int parseIntSafe(String raw, int fallback) {
        try {
            return Integer.parseInt(raw == null ? "" : raw.trim());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private void debug(String message) {
        if (!configManager.isDebugEnabled()) {
            return;
        }
        LOGGER.at(Level.INFO).log("[CobbleBlockPicker][DBG] " + message);
    }
}
