package com.minecraft_ceo.cobblestonezufall;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PickupItemComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;

import java.math.BigDecimal;
<<<<<<< HEAD
=======
import java.util.Locale;
>>>>>>> cd2cc2b (Prepare clean project state)
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class CobblestoneGeneratorSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final ConcurrentHashMap<String, Long> PENDING_REGEN_UNTIL = new ConcurrentHashMap<>();
<<<<<<< HEAD
=======
    private static final ConcurrentHashMap<String, Long> ALLOW_PLACEMENT_UNTIL = new ConcurrentHashMap<>();
>>>>>>> cd2cc2b (Prepare clean project state)
    private static final ConcurrentHashMap<Long, Boolean> TRACE_COLLECT_APPLIED = new ConcurrentHashMap<>();
    private static final Map<String, String> ORE_BLOCK_TO_ITEM = Map.ofEntries(
            Map.entry("Ore_Copper_Stone", "Ore_Copper"),
            Map.entry("Ore_Iron_Stone", "Ore_Iron"),
            Map.entry("Ore_Gold_Stone", "Ore_Gold"),
            Map.entry("Ore_Silver_Stone", "Ore_Silver"),
            Map.entry("Ore_Cobalt_Shale", "Ore_Cobalt"),
            Map.entry("Ore_Thorium_Mud", "Ore_Thorium"),
            Map.entry("Ore_Adamantite_Magma", "Ore_Adamantite"),
            Map.entry("Ore_Mithril_Stone", "Ore_Mithril")
    );
    private static final AtomicLong BREAK_TRACE_SEQ = new AtomicLong(1L);
<<<<<<< HEAD
    private static final boolean DEBUG_VERBOSE = false;
    private static final double RANGE_SWEEP_SOURCE_RADIUS_BLOCKS = 2.75d;
    private static final int RANGE_SWEEP_TICKS = 50;
=======
    private static final double RANGE_SWEEP_SOURCE_RADIUS_BLOCKS = 2.75d;
    private static final int RANGE_SWEEP_TICKS = 8;
    private static final long NATURAL_GUARD_SWEEP_INTERVAL_MS = 25L;
    private static final long WATCH_MIN_DURATION_MS = 4000L;
    private static final long WATCH_EXTRA_AFTER_REGEN_MS = 4000L;
    private static final ConcurrentHashMap<String, Long> WATCH_BREAK_START_MS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> WATCH_UNTIL_MS = new ConcurrentHashMap<>();
>>>>>>> cd2cc2b (Prepare clean project state)

    private final ConfigManager configManager;

    public CobblestoneGeneratorSystem(ConfigManager configManager) {
        super(BreakBlockEvent.class);
        this.configManager = configManager;
    }

    @Override
    public void handle(int index,
                       ArchetypeChunk<EntityStore> chunk,
                       Store<EntityStore> store,
                       CommandBuffer<EntityStore> commandBuffer,
                       BreakBlockEvent event) {
<<<<<<< HEAD
=======
        // Retry in case setup happened before fluid assets were fully loaded.
        FluidCollisionOverride.apply(isDebug());

>>>>>>> cd2cc2b (Prepare clean project state)
        Vector3i position = event.getTargetBlock();
        BlockType blockType = event.getBlockType();
        long traceId = BREAK_TRACE_SEQ.getAndIncrement();
        if (position == null || blockType == null) {
<<<<<<< HEAD
            if (DEBUG_VERBOSE) {
=======
            if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Break ignored: missing position or blockType");
            }
            return;
        }

        String brokenBlockId = blockType.getId();
        if (!isManagedGeneratorBlockId(brokenBlockId)) {
<<<<<<< HEAD
            if (DEBUG_VERBOSE) {
=======
            if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Break ignored: unmanaged blockId=" + brokenBlockId);
            }
            return;
        }

        EntityStore entityStore = store.getExternalData();
        if (entityStore == null) {
<<<<<<< HEAD
            if (DEBUG_VERBOSE) {
=======
            if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Break ignored: entityStore is null");
            }
            return;
        }

        World world = entityStore.getWorld();
        if (world == null) {
<<<<<<< HEAD
            if (DEBUG_VERBOSE) {
=======
            if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Break ignored: world is null");
            }
            return;
        }

        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        String key = key(x, y, z);
<<<<<<< HEAD
=======
        long regenDelayMs = Math.max(0L, configManager.getRegenDelayMs());
        EngineTraceFinderSystem.watch(x, y, z, regenDelayMs + 8000L, "break");
        EngineTraceFinderSystem.event(x, y, z, "BREAK_SEEN",
                "traceId=" + traceId + " blockId=" + brokenBlockId + " regenDelayMs=" + regenDelayMs);
        armWatchPos(x, y, z, regenDelayMs + WATCH_EXTRA_AFTER_REGEN_MS);
        if (isWatchPos(x, y, z)) {
            WATCH_BREAK_START_MS.put(key, System.currentTimeMillis());
            watchLog(x, y, z, "BREAK_EVENT",
                    "traceId=" + traceId + " blockId=" + brokenBlockId + " cancelled=" + event.isCancelled());
        }

        // Important: if this key is still marked from a previous cycle, guard sweeps would skip it.
        // Clearing here prevents early natural cobble during long regen delays.
        ALLOW_PLACEMENT_UNTIL.remove(key);
        if (isWatchPos(x, y, z)) {
            watchLog(x, y, z, "ALLOW_CLEARED_ON_BREAK", "key=" + key);
        }
>>>>>>> cd2cc2b (Prepare clean project state)

        Long until = PENDING_REGEN_UNTIL.get(key);
        if (until != null && System.currentTimeMillis() < until) {
            event.setCancelled(true);
<<<<<<< HEAD
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][GEN] Blocked break during regen delay at " + key);
            if (DEBUG_VERBOSE) {
=======
            EngineTraceFinderSystem.event(x, y, z, "BREAK_CANCELLED_PENDING", "pendingUntil=" + until);
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][GEN] Blocked break during regen delay at " + key);
            if (isWatchPos(x, y, z)) {
                long now = System.currentTimeMillis();
                watchLog(x, y, z, "BREAK_BLOCKED",
                        "now=" + now + " pendingUntil=" + until + " remainingMs=" + Math.max(0L, until - now));
            }
            if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Break cancelled due to pending regen until=" + until + " key=" + key);
            }
            return;
        }

        boolean hasLava = hasNearbyFluid(world, x, y, z, true);
        boolean hasWater = hasNearbyFluid(world, x, y, z, false);
        Ref<EntityStore> sourceRef = chunk.getReferenceTo(index);
        PlayerRef playerRef = sourceRef == null ? null : store.getComponent(sourceRef, PlayerRef.getComponentType());
        TransformComponent playerTransform = sourceRef == null ? null : store.getComponent(sourceRef, TransformComponent.getComponentType());
        int playerTier = playerRef == null ? 1 : configManager.getPlayerTier(playerRef.getUuid());

        LOGGER.at(Level.INFO).log("[CobblestoneZufall][GEN] Break generator block at " + x + "," + y + "," + z
                + " blockId=" + brokenBlockId + " lava=" + hasLava + " water=" + hasWater + " tier=" + playerTier);
<<<<<<< HEAD
        if (DEBUG_VERBOSE) {
=======
        if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
            String playerPos = playerTransform == null || playerTransform.getPosition() == null
                    ? "null"
                    : playerTransform.getPosition().toString();
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Break context:"
                    + " key=" + key
                    + " sourceRef=" + sourceRef
                    + " playerRef=" + playerRef
                    + " playerUuid=" + (playerRef == null ? "null" : playerRef.getUuid())
                    + " playerPos=" + playerPos);
        }
        if (!hasLava || !hasWater) {
<<<<<<< HEAD
            if (DEBUG_VERBOSE) {
=======
            EngineTraceFinderSystem.event(x, y, z, "BREAK_IGNORED_FLUID_CHECK", "hasLava=" + hasLava + " hasWater=" + hasWater);
            if (isWatchPos(x, y, z)) {
                watchLog(x, y, z, "BREAK_IGNORED_NO_FLUIDS", "hasLava=" + hasLava + " hasWater=" + hasWater);
            }
            if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Break ignored after check: generator missing fluid adjacency");
            }
            return;
        }
<<<<<<< HEAD
=======
        // Arm pending as early as possible so fluid-engine cobble has no visible window.
        PENDING_REGEN_UNTIL.put(key, System.currentTimeMillis() + regenDelayMs + 250L);
        if (isWatchPos(x, y, z)) {
            long now = System.currentTimeMillis();
            watchLog(x, y, z, "PENDING_SET_EARLY",
                    "pendingUntil=" + (now + regenDelayMs + 250L) + " regenDelayMs=" + regenDelayMs);
        }
>>>>>>> cd2cc2b (Prepare clean project state)

        ConfigManager.DropEntry selectedEntry = resolveDropEntryForBrokenBlock(playerTier, brokenBlockId);
        String selectedDropId = selectedEntry == null ? null : selectedEntry.itemId;
        String replacementRandomDropId = configManager.getRandomDropForTier(playerTier);
        String replacementBlockId = resolveReplacementBlockId(replacementRandomDropId, playerTier);
        if (replacementBlockId == null || replacementBlockId.isBlank()) {
<<<<<<< HEAD
            LOGGER.at(Level.WARNING).log("[CobblestoneZufall][GEN] No valid replacement for tier " + playerTier
                    + " at " + x + "," + y + "," + z + " (dropId=" + replacementRandomDropId + ")");
            if (DEBUG_VERBOSE) {
=======
            PENDING_REGEN_UNTIL.remove(key);
            LOGGER.at(Level.WARNING).log("[CobblestoneZufall][GEN] No valid replacement for tier " + playerTier
                    + " at " + x + "," + y + "," + z + " (dropId=" + replacementRandomDropId + ")");
            if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
                LOGGER.at(Level.WARNING).log("[CobblestoneZufall][DBG#" + traceId + "] Replacement resolution failed for dropId=" + replacementRandomDropId);
            }
            return;
        }

        if (selectedEntry == null) {
            selectedDropId = replacementRandomDropId;
            selectedEntry = configManager.getDropForTierById(playerTier, selectedDropId);
        }
        int amount = configManager.getAmountForTierDrop(playerTier, selectedDropId);
        if (amount <= 1 && selectedEntry != null) {
            amount = Math.max(1, selectedEntry.amount);
        }
        String desiredCollectItemId = resolveConfiguredToItemId(selectedDropId);
<<<<<<< HEAD
        if (DEBUG_VERBOSE) {
=======
        if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Drop selection:"
                    + " selectedDropId=" + selectedDropId
                    + " replacementRandomDropId=" + replacementRandomDropId
                    + " replacementBlockId=" + replacementBlockId
                    + " brokenBlockId=" + brokenBlockId
                    + " amount=" + amount
                    + " desiredCollectItemId=" + desiredCollectItemId
                    + " selectedEntryExists=" + (selectedEntry != null));
        }
        if (playerRef != null) {
            MiningRateTracker.getInstance().recordBreak(playerRef.getUuid());
            if (selectedEntry != null) {
                applyEconomyReward(playerRef, selectedEntry);
                applyToolRepair(sourceRef, store, selectedEntry);
            }
        }

<<<<<<< HEAD
        int collectRange = configManager.getAutoCollectRange();
        int rangeTypeMs = configManager.getAutoCollectRangeTypeMs();
        boolean playerWithinRange = sourceRef != null && isPlayerWithinRange(sourceRef, store, x, y, z, collectRange);
        boolean shouldRangeCollect = collectRange > 0 && sourceRef != null && playerWithinRange;
        if (DEBUG_VERBOSE) {
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Range decision:"
                    + " collectRange=" + collectRange
                    + " rangeType=" + (rangeTypeMs <= 0 ? "Teleport" : (rangeTypeMs + "ms"))
                    + " sourceRefNull=" + (sourceRef == null)
                    + " playerWithinRange=" + playerWithinRange
                    + " shouldRangeCollect=" + shouldRangeCollect);
        }
        if (shouldRangeCollect) {
            scheduleRangePickupSweep(world, store, sourceRef, x, y, z, collectRange, rangeTypeMs, amount, desiredCollectItemId, traceId);
        } else if (DEBUG_VERBOSE) {
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Range sweep not scheduled");
        }
=======
        int rangeTypeMs = configManager.getAutoCollectRangeTypeMs();
        boolean shouldRangeCollect = sourceRef != null;
        if (isDebug()) {
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Range decision:"
                    + " rangeType=" + (rangeTypeMs <= 0 ? "NoDelay" : (rangeTypeMs + "ms"))
                    + " sourceRefNull=" + (sourceRef == null)
                    + " shouldRangeCollect=" + shouldRangeCollect);
        }
        if (shouldRangeCollect) {
            scheduleRangePickupSweep(world, store, sourceRef, x, y, z, rangeTypeMs, amount, desiredCollectItemId, traceId);
        } else if (isDebug()) {
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Range sweep not scheduled");
        }
        if (isWatchPos(x, y, z)) {
            watchLog(x, y, z, "REGEN_SCHEDULED",
                    "regenDelayMs=" + regenDelayMs + " replacementBlockId=" + replacementBlockId + " tier=" + playerTier);
        }
        EngineTraceFinderSystem.event(x, y, z, "REGEN_SCHEDULED",
                "regenDelayMs=" + regenDelayMs + " replacement=" + replacementBlockId + " tier=" + playerTier);
        scheduleNaturalCobbleGuard(world, x, y, z, regenDelayMs, traceId);
>>>>>>> cd2cc2b (Prepare clean project state)
        scheduleRegeneration(world, x, y, z, replacementBlockId, playerTier);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }

    private void scheduleRegeneration(World world, int x, int y, int z, String replacementBlockId, int tier) {
<<<<<<< HEAD
        long regenDelayMs = Math.max(1L, configManager.getRegenDelayMs());
        String key = key(x, y, z);
        PENDING_REGEN_UNTIL.put(key, System.currentTimeMillis() + regenDelayMs + 250L);

        CompletableFuture.delayedExecutor(regenDelayMs, TimeUnit.MILLISECONDS).execute(() ->
                world.execute(() -> {
                    String replacement = replacementBlockId;
                    LOGGER.at(Level.INFO).log("[CobblestoneZufall][GEN] Regenerating at " + x + "," + y + "," + z
                            + " replacement=" + replacement + " tier=" + tier);
=======
        long regenDelayMs = Math.max(0L, configManager.getRegenDelayMs());
        String key = key(x, y, z);
        PENDING_REGEN_UNTIL.put(key, System.currentTimeMillis() + regenDelayMs + 250L);
        if (isWatchPos(x, y, z)) {
            long now = System.currentTimeMillis();
            watchLog(x, y, z, "PENDING_SET",
                    "pendingUntil=" + (now + regenDelayMs + 250L) + " regenDelayMs=" + regenDelayMs);
        }

        CompletableFuture.delayedExecutor(regenDelayMs, TimeUnit.MILLISECONDS).execute(() -> {
                try {
                    world.execute(() -> {
                    String replacement = replacementBlockId;
                    if (isWatchPos(x, y, z)) {
                        watchLog(x, y, z, "REGEN_EXECUTE_START", "replacement=" + replacement + " tier=" + tier);
                    }
                    LOGGER.at(Level.INFO).log("[CobblestoneZufall][GEN] Regenerating at " + x + "," + y + "," + z
                            + " replacement=" + replacement + " tier=" + tier);
                    markPluginPlacementAllowed(x, y, z, 350L);
>>>>>>> cd2cc2b (Prepare clean project state)

                    try {
                        world.setBlock(x, y, z, replacement, 0);
                        BlockType after = world.getBlockType(x, y, z);
                        String afterId = after == null ? "null" : after.getId();
<<<<<<< HEAD
                        LOGGER.at(Level.INFO).log("[CobblestoneZufall][GEN] setBlock OK at " + x + "," + y + "," + z
                                + " now=" + afterId);
                    } catch (IllegalArgumentException ex) {
=======
                        if (isWatchPos(x, y, z)) {
                            watchLog(x, y, z, "REGEN_SETBLOCK_OK", "afterBlockId=" + afterId + " replacement=" + replacement);
                        }
                        LOGGER.at(Level.INFO).log("[CobblestoneZufall][GEN] setBlock OK at " + x + "," + y + "," + z
                                + " now=" + afterId);
                    } catch (IllegalArgumentException ex) {
                        if (isWatchPos(x, y, z)) {
                            watchLog(x, y, z, "REGEN_SETBLOCK_FAIL",
                                    "replacement=" + replacement + " error=" + ex.getClass().getSimpleName());
                        }
>>>>>>> cd2cc2b (Prepare clean project state)
                        LOGGER.at(Level.WARNING).log("[CobblestoneZufall][GEN] setBlock failed for " + replacement
                                + " at " + x + "," + y + "," + z + ", trying tier fallback");
                        String fallback = resolveFirstValidTierBlock(tier);
                        if (fallback != null && !fallback.equals(replacement)) {
                            try {
<<<<<<< HEAD
                                world.setBlock(x, y, z, fallback, 0);
                                LOGGER.at(Level.WARNING).log("[CobblestoneZufall][GEN] Applied tier fallback block "
                                        + fallback + " at " + x + "," + y + "," + z);
                            } catch (IllegalArgumentException ignored) {
=======
                                markPluginPlacementAllowed(x, y, z, 350L);
                                world.setBlock(x, y, z, fallback, 0);
                                if (isWatchPos(x, y, z)) {
                                    BlockType after = world.getBlockType(x, y, z);
                                    String afterId = after == null ? "null" : after.getId();
                                    watchLog(x, y, z, "REGEN_FALLBACK_OK", "fallback=" + fallback + " afterBlockId=" + afterId);
                                }
                                LOGGER.at(Level.WARNING).log("[CobblestoneZufall][GEN] Applied tier fallback block "
                                        + fallback + " at " + x + "," + y + "," + z);
                            } catch (IllegalArgumentException ignored) {
                                if (isWatchPos(x, y, z)) {
                                    watchLog(x, y, z, "REGEN_FALLBACK_FAIL", "fallback=" + fallback);
                                }
>>>>>>> cd2cc2b (Prepare clean project state)
                                LOGGER.at(Level.SEVERE).log("[CobblestoneZufall][GEN] Tier fallback also invalid at "
                                        + x + "," + y + "," + z + ", no block placed");
                            }
                        }
                    } finally {
                        PENDING_REGEN_UNTIL.remove(key);
<<<<<<< HEAD
                    }
                })
        );
=======
                        if (isWatchPos(x, y, z)) {
                            watchLog(x, y, z, "PENDING_CLEARED", "key=" + key);
                        }
                    }
                });
                } catch (Exception ignored) {
                }
        });
>>>>>>> cd2cc2b (Prepare clean project state)
    }

    private String resolveReplacementBlockId(String configuredId, int tier) {
        String direct = resolveConfiguredToBlockId(configuredId);
        if (direct != null) {
            return direct;
        }
        return resolveFirstValidTierBlock(tier);
    }

    private String resolveFirstValidTierBlock(int tier) {
        for (ConfigManager.DropEntry entry : configManager.getDropsForTier(tier)) {
            String blockId = resolveConfiguredToBlockId(entry == null ? null : entry.itemId);
            if (blockId != null) {
                return blockId;
            }
        }
        return null;
    }

    private static String resolveConfiguredToBlockId(String configuredId) {
        if (configuredId == null || configuredId.isBlank()) {
            return null;
        }

        BlockType directBlock = BlockType.fromString(configuredId);
        if (directBlock != null && !directBlock.isUnknown()) {
            return directBlock.getId();
        }

        Item item = Item.getAssetMap().getAsset(configuredId);
        if (item != null && item.getBlockId() != null) {
            BlockType itemBlock = BlockType.fromString(item.getBlockId());
            if (itemBlock != null && !itemBlock.isUnknown()) {
                return itemBlock.getId();
            }
        }

        return null;
    }

    private static String resolveConfiguredToItemId(String configuredId) {
        if (configuredId == null || configuredId.isBlank()) {
            return null;
        }

        String mapped = ORE_BLOCK_TO_ITEM.get(configuredId);
        if (mapped != null) {
            return mapped;
        }

        Item directItem = Item.getAssetMap().getAsset(configuredId);
        if (directItem != null && directItem.getId() != null && !directItem.getId().isBlank()) {
            return directItem.getId();
        }

        BlockType blockType = BlockType.fromString(configuredId);
        if (blockType != null && !blockType.isUnknown()) {
            Item blockItem = blockType.getItem();
            if (blockItem != null && blockItem.getId() != null && !blockItem.getId().isBlank()) {
                return blockItem.getId();
            }
            return blockType.getId();
        }

        return configuredId;
    }

    private ConfigManager.DropEntry resolveDropEntryForBrokenBlock(int tier, String brokenBlockId) {
        ConfigManager.DropEntry direct = configManager.getDropForTierById(tier, brokenBlockId);
        if (direct != null) {
            return direct;
        }

        String asItem = resolveConfiguredToItemId(brokenBlockId);
        if (asItem != null && !asItem.isBlank()) {
            ConfigManager.DropEntry mapped = configManager.getDropForTierById(tier, asItem);
            if (mapped != null) {
                return mapped;
            }
        }
        return null;
    }

    private static boolean hasNearbyFluid(World world, int x, int y, int z, boolean lava) {
        int[][] offsets = new int[][]{
                {1, 0, 0},
                {-1, 0, 0},
                {0, 0, 1},
                {0, 0, -1},
                {0, -1, 0},
                {0, 1, 0}
        };

        for (int[] offset : offsets) {
            int fluidId = world.getFluidId(x + offset[0], y + offset[1], z + offset[2]);
            if (isFluidType(fluidId, lava)) {
                return true;
            }
        }

        return false;
    }

<<<<<<< HEAD
=======
    private static boolean isNaturalCobbleAt(World world, int x, int y, int z) {
        BlockType type = world.getBlockType(x, y, z);
        String id = type == null ? null : type.getId();
        if (!isCobbleId(id)) {
            return false;
        }
        return hasNearbyFluid(world, x, y, z, true) && hasNearbyFluid(world, x, y, z, false);
    }

>>>>>>> cd2cc2b (Prepare clean project state)
    private static boolean isFluidType(int fluidId, boolean lava) {
        if (fluidId <= 0) {
            return false;
        }
        try {
            var fluid = com.hypixel.hytale.server.core.asset.type.fluid.Fluid.getAssetMap().getAsset(fluidId);
            if (fluid == null || fluid.getId() == null) {
                return false;
            }
            String fluidKey = fluid.getId().toLowerCase(java.util.Locale.ROOT);
            return lava ? fluidKey.contains("lava") : fluidKey.contains("water");
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean isCobbleId(String id) {
        if (id == null) {
            return false;
        }
        return id.equals("Rock_Stone_Cobble") || id.contains("Cobble") || id.contains("cobble");
    }

<<<<<<< HEAD
=======
    private static void scheduleNaturalCobbleGuard(World world,
                                                   int x,
                                                   int y,
                                                   int z,
                                                   long regenDelayMs,
                                                   long traceId) {
        long guardDurationMs = Math.max(500L, regenDelayMs + 500L);
        int sweeps = (int) Math.max(8L, guardDurationMs / NATURAL_GUARD_SWEEP_INTERVAL_MS);
        if (isWatchPos(x, y, z)) {
            watchLog(x, y, z, "GUARD_SCHEDULED",
                    "traceId=" + traceId + " sweeps=" + sweeps + " intervalMs=" + NATURAL_GUARD_SWEEP_INTERVAL_MS + " durationMs=" + guardDurationMs);
        }
        for (int i = 0; i < sweeps; i++) {
            long delayMs = i * NATURAL_GUARD_SWEEP_INTERVAL_MS;
            int sweepIndex = i + 1;
            CompletableFuture.delayedExecutor(delayMs, TimeUnit.MILLISECONDS).execute(() -> {
                try {
                    world.execute(() -> {
                        if (isWatchPos(x, y, z)) {
                            BlockType before = world.getBlockType(x, y, z);
                            String beforeId = before == null ? "null" : before.getId();
                            boolean lava = hasNearbyFluid(world, x, y, z, true);
                            boolean water = hasNearbyFluid(world, x, y, z, false);
                            watchLog(x, y, z, "GUARD_SWEEP",
                                    "traceId=" + traceId + " sweep=" + sweepIndex + "/" + sweeps + " delayMs=" + delayMs
                                            + " blockBefore=" + beforeId + " lava=" + lava + " water=" + water
                                            + " pluginAllowed=" + isPluginPlacementAllowed(x, y, z));
                        }
                        if (isPluginPlacementAllowed(x, y, z)) {
                            return;
                        }
                        // Hard guard: while regeneration is pending, this slot must stay empty,
                        // regardless of whether the current cobble came from fluid adjacency checks.
                        if (isRegenPending(x, y, z)) {
                            BlockType pendingType = world.getBlockType(x, y, z);
                            String pendingId = pendingType == null ? null : pendingType.getId();
                            if (isCobbleId(pendingId)) {
                                try {
                                    world.setBlock(x, y, z, "Empty", 0);
                                    if (isWatchPos(x, y, z)) {
                                        watchLog(x, y, z, "GUARD_PENDING_FORCE_EMPTY", "removedBlock=" + pendingId);
                                    }
                                } catch (Exception ex) {
                                    if (isWatchPos(x, y, z)) {
                                        watchLog(x, y, z, "GUARD_PENDING_FORCE_EMPTY_FAIL", "error=" + ex.getClass().getSimpleName());
                                    }
                                }
                            }
                            return;
                        }
                        // Outside regen-pending windows we do not force-delete blocks here.
                    });
                } catch (Exception ignored) {
                }
            });
        }
    }

>>>>>>> cd2cc2b (Prepare clean project state)
    private boolean isManagedGeneratorBlockId(String id) {
        return isCobbleId(id) || configManager.containsDropIdAnyTier(id);
    }

    private static String key(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }

<<<<<<< HEAD
=======
    private static boolean isWatchPos(int x, int y, int z) {
        if (!isDebug()) {
            return false;
        }
        String posKey = key(x, y, z);
        long now = System.currentTimeMillis();
        Long until = WATCH_UNTIL_MS.get(posKey);
        if (until == null) {
            return false;
        }
        if (until < now) {
            WATCH_UNTIL_MS.remove(posKey, until);
            WATCH_BREAK_START_MS.remove(posKey);
            return false;
        }
        return true;
    }

    private static void armWatchPos(int x, int y, int z, long durationMs) {
        if (!isDebug()) {
            return;
        }
        long now = System.currentTimeMillis();
        long span = Math.max(WATCH_MIN_DURATION_MS, durationMs);
        String posKey = key(x, y, z);
        WATCH_BREAK_START_MS.put(posKey, now);
        WATCH_UNTIL_MS.put(posKey, now + span);
    }

    private static void watchLog(int x, int y, int z, String phase, String details) {
        if (!isDebug()) {
            return;
        }
        long now = System.currentTimeMillis();
        String posKey = key(x, y, z);
        Long start = WATCH_BREAK_START_MS.get(posKey);
        long delta = start == null ? -1L : now - start;
        String deltaText = delta >= 0L ? ("+" + delta + "ms") : "n/a";
        LOGGER.at(Level.INFO).log("[CobblestoneZufall][WATCH " + x + "," + y + "," + z + "]"
                + " ts=" + now
                + " dt=" + deltaText
                + " phase=" + phase
                + " " + details);
    }

>>>>>>> cd2cc2b (Prepare clean project state)
    public static boolean isRegenPending(int x, int y, int z) {
        Long until = PENDING_REGEN_UNTIL.get(key(x, y, z));
        return until != null && System.currentTimeMillis() < until;
    }

<<<<<<< HEAD
=======
    public static boolean isPluginPlacementAllowed(int x, int y, int z) {
        String posKey = key(x, y, z);
        long now = System.currentTimeMillis();
        Long until = ALLOW_PLACEMENT_UNTIL.get(posKey);
        if (until == null) {
            return false;
        }
        if (until < now) {
            ALLOW_PLACEMENT_UNTIL.remove(posKey, until);
            return false;
        }
        return true;
    }

    private static void markPluginPlacementAllowed(int x, int y, int z, long durationMs) {
        ALLOW_PLACEMENT_UNTIL.put(key(x, y, z), System.currentTimeMillis() + Math.max(250L, durationMs));
    }

>>>>>>> cd2cc2b (Prepare clean project state)
    private static void applyToolRepair(Ref<EntityStore> playerEntityRef,
                                        Store<EntityStore> store,
                                        ConfigManager.DropEntry entry) {
        if (playerEntityRef == null || store == null || entry == null) {
            return;
        }
        if (entry.repairChance <= 0.0d || entry.repairAmountPercent <= 0.0d) {
            return;
        }
        double roll = Math.random() * 100.0d;
        if (roll > entry.repairChance) {
            return;
        }
        var player = store.getComponent(playerEntityRef, com.hypixel.hytale.server.core.entity.entities.Player.getComponentType());
        if (player == null || player.getInventory() == null) {
            return;
        }
        ItemStack inHand = player.getInventory().getItemInHand();
        if (inHand == null || inHand.isEmpty() || inHand.getMaxDurability() <= 0.0d) {
            return;
        }
        double restoreAmount = inHand.getMaxDurability() * (entry.repairAmountPercent / 100.0d);
        if (restoreAmount <= 0.0d) {
            return;
        }
        double maxDurability = inHand.getMaxDurability();
        double currentDurability = inHand.getDurability();
        double newDurability = Math.min(maxDurability, currentDurability + restoreAmount);
        ItemStack repaired = inHand.withDurability(newDurability).withMaxDurability(maxDurability);
        byte active = player.getInventory().getActiveHotbarSlot();
        if (active >= 0) {
            player.getInventory().getHotbar().setItemStackForSlot((short) active, repaired);
        }
    }

    private static void applyEconomyReward(PlayerRef playerRef, ConfigManager.DropEntry entry) {
        if (playerRef == null || entry == null || entry.payAmount <= 0.0d) {
            return;
        }
        CobblestoneZufallPlugin plugin = CobblestoneZufallPlugin.getInstance();
        if (plugin == null) {
            return;
        }
        EconomyBridge economy = plugin.getEconomyBridge();
        if (economy == null) {
            return;
        }
        economy.addBalance(playerRef.getUuid(), BigDecimal.valueOf(entry.payAmount));
    }

<<<<<<< HEAD
    private static boolean isPlayerWithinRange(Ref<EntityStore> playerRef,
                                               Store<EntityStore> store,
                                               int blockX,
                                               int blockY,
                                               int blockZ,
                                               int range) {
        if (playerRef == null || store == null || range <= 0) {
            return false;
        }
        TransformComponent transform = store.getComponent(playerRef, TransformComponent.getComponentType());
        if (transform == null || transform.getPosition() == null) {
            return false;
        }
        double dx = transform.getPosition().getX() - (blockX + 0.5d);
        double dy = transform.getPosition().getY() - (blockY + 0.5d);
        double dz = transform.getPosition().getZ() - (blockZ + 0.5d);
        double maxDistanceSq = (double) range * (double) range;
        return (dx * dx + dy * dy + dz * dz) <= maxDistanceSq;
    }

=======
>>>>>>> cd2cc2b (Prepare clean project state)
    private static void scheduleRangePickupSweep(World world,
                                                 Store<EntityStore> store,
                                                 Ref<EntityStore> playerRef,
                                                 int x,
                                                 int y,
                                                 int z,
<<<<<<< HEAD
                                                 int collectRange,
=======
>>>>>>> cd2cc2b (Prepare clean project state)
                                                 int rangeTypeMs,
                                                 int desiredAmount,
                                                 String desiredItemId,
                                                 long traceId) {
        long intervalMs = computeSweepIntervalMs(rangeTypeMs);
        TRACE_COLLECT_APPLIED.remove(traceId);
        for (int i = 0; i < RANGE_SWEEP_TICKS; i++) {
            final int sweepIndex = i;
            long delay = i * intervalMs;
<<<<<<< HEAD
            if (DEBUG_VERBOSE) {
=======
            if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] Scheduling sweep "
                        + (sweepIndex + 1) + "/" + RANGE_SWEEP_TICKS + " delayMs=" + delay);
            }
            CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS).execute(() ->
<<<<<<< HEAD
                    world.execute(() -> applyPickupToNearbyDrops(world, store, playerRef, x, y, z, collectRange, rangeTypeMs, desiredAmount, desiredItemId, traceId, sweepIndex))
=======
            {
                try {
                    world.execute(() -> applyPickupToNearbyDrops(world, store, playerRef, x, y, z, rangeTypeMs, desiredAmount, desiredItemId, traceId, sweepIndex));
                } catch (Exception ignored) {
                }
            }
>>>>>>> cd2cc2b (Prepare clean project state)
            );
        }
    }

    private static void applyPickupToNearbyDrops(World world,
                                                 Store<EntityStore> store,
                                                 Ref<EntityStore> playerRef,
                                                 int sourceX,
                                                 int sourceY,
                                                 int sourceZ,
<<<<<<< HEAD
                                                 int collectRange,
=======
>>>>>>> cd2cc2b (Prepare clean project state)
                                                 int rangeTypeMs,
                                                 int desiredAmount,
                                                 String desiredItemId,
                                                 long traceId,
                                                 int sweepIndex) {
<<<<<<< HEAD
        if (store == null || playerRef == null || collectRange <= 0) {
            if (DEBUG_VERBOSE) {
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "][SWEEP " + (sweepIndex + 1)
                        + "] skipped: storeNull=" + (store == null)
                        + " playerRefNull=" + (playerRef == null)
                        + " collectRange=" + collectRange);
=======
        if (store == null || playerRef == null) {
            if (isDebug()) {
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "][SWEEP " + (sweepIndex + 1)
                        + "] skipped: storeNull=" + (store == null)
                        + " playerRefNull=" + (playerRef == null));
>>>>>>> cd2cc2b (Prepare clean project state)
            }
            return;
        }
        TransformComponent playerTransform = store.getComponent(playerRef, TransformComponent.getComponentType());
        if (playerTransform == null || playerTransform.getPosition() == null) {
<<<<<<< HEAD
            if (DEBUG_VERBOSE) {
=======
            if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
                LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "][SWEEP " + (sweepIndex + 1)
                        + "] skipped: player transform missing");
            }
            return;
        }

        var itemType = ItemComponent.getComponentType();
        var transformType = TransformComponent.getComponentType();
        var pickupType = PickupItemComponent.getComponentType();
        Query<EntityStore> query = Query.and(itemType, transformType);

        final double sourceCx = sourceX + 0.5d;
        final double sourceCy = sourceY + 0.5d;
        final double sourceCz = sourceZ + 0.5d;
<<<<<<< HEAD
        final double sourceRadius = Math.max(RANGE_SWEEP_SOURCE_RADIUS_BLOCKS, collectRange + 1.5d);
        final double sourceRadiusSq = sourceRadius * sourceRadius;
        final double maxPlayerDistanceSq = (double) collectRange * (double) collectRange;
=======
        final double sourceRadius = RANGE_SWEEP_SOURCE_RADIUS_BLOCKS;
        final double sourceRadiusSq = sourceRadius * sourceRadius;
>>>>>>> cd2cc2b (Prepare clean project state)

        final int[] totalSeen = {0};
        final int[] skippedNull = {0};
        final int[] skippedEmptyStack = {0};
        final int[] skippedCannotPickUp = {0};
        final int[] skippedSourceDistance = {0};
<<<<<<< HEAD
        final int[] skippedPlayerDistance = {0};
=======
>>>>>>> cd2cc2b (Prepare clean project state)
        final int[] movedTowardPlayer = {0};
        final int[] addedPickupComponent = {0};
        final StringBuilder samples = new StringBuilder();
        final int maxSamples = 10;

        store.forEachChunk(query, (chunk, commandBuffer) -> {
            for (int i = 0; i < chunk.size(); i++) {
                Ref<EntityStore> itemRef = chunk.getReferenceTo(i);
                ItemComponent itemComponent = commandBuffer.getComponent(itemRef, itemType);
                TransformComponent transform = commandBuffer.getComponent(itemRef, transformType);
                totalSeen[0]++;
                if (itemRef == null || itemComponent == null || transform == null || transform.getPosition() == null) {
                    skippedNull[0]++;
                    continue;
                }
                if (itemComponent.getItemStack() == null || itemComponent.getItemStack().isEmpty()) {
                    skippedEmptyStack[0]++;
                    continue;
                }
                double itemX = transform.getPosition().getX();
                double itemY = transform.getPosition().getY();
                double itemZ = transform.getPosition().getZ();
                double sdx = itemX - sourceCx;
                double sdy = itemY - sourceCy;
                double sdz = itemZ - sourceCz;
                double sourceDistanceSq = sdx * sdx + sdy * sdy + sdz * sdz;
                if (sourceDistanceSq > sourceRadiusSq) {
                    skippedSourceDistance[0]++;
                    continue;
                }

<<<<<<< HEAD
                double pdx = itemX - playerTransform.getPosition().getX();
                double pdy = itemY - playerTransform.getPosition().getY();
                double pdz = itemZ - playerTransform.getPosition().getZ();
                double playerDistanceSq = pdx * pdx + pdy * pdy + pdz * pdz;
                if (playerDistanceSq > maxPlayerDistanceSq) {
                    skippedPlayerDistance[0]++;
                    continue;
                }

=======
>>>>>>> cd2cc2b (Prepare clean project state)
                if (!itemComponent.canPickUp()) {
                    skippedCannotPickUp[0]++;
                }

                if (samples.length() < 1_500 && movedTowardPlayer[0] <= maxSamples) {
                    String itemId = itemComponent.getItemStack().getItemId();
<<<<<<< HEAD
=======
                    double pdx = itemX - playerTransform.getPosition().getX();
                    double pdy = itemY - playerTransform.getPosition().getY();
                    double pdz = itemZ - playerTransform.getPosition().getZ();
                    double playerDistanceSq = pdx * pdx + pdy * pdy + pdz * pdz;
>>>>>>> cd2cc2b (Prepare clean project state)
                    samples.append(" item=").append(itemId)
                            .append(" srcDist=").append(String.format(java.util.Locale.ROOT, "%.2f", Math.sqrt(sourceDistanceSq)))
                            .append(" playerDist=").append(String.format(java.util.Locale.ROOT, "%.2f", Math.sqrt(playerDistanceSq)));
                }

                if (Boolean.TRUE.equals(TRACE_COLLECT_APPLIED.get(traceId))) {
                    continue;
                }
                if (store.getComponent(itemRef, pickupType) != null) {
                    continue;
                }

                // Ensure the drop amount configured in UI is reflected in the actual collected stack.
                int targetAmount = Math.max(1, desiredAmount);
                String itemId = (desiredItemId != null && !desiredItemId.isBlank())
                        ? desiredItemId
                        : itemComponent.getItemStack().getItemId();
                itemComponent.setPickupDelay(0.0f);
                if (itemId != null && !itemId.isBlank() && itemComponent.getItemStack().getQuantity() != targetAmount) {
                    itemComponent.setItemStack(new ItemStack(itemId, targetAmount));
                }

                float travelSeconds = rangeTypeMs <= 0 ? 0.01f : Math.max(0.1f, rangeTypeMs / 1000.0f);
                if (rangeTypeMs <= 0) {
                    grantItemAndRemoveEntity(commandBuffer, store, playerRef, itemRef, itemId, targetAmount, traceId, "teleport");
                } else {
                    commandBuffer.addComponent(itemRef, pickupType, new PickupItemComponent(playerRef, transform.getPosition().clone(), travelSeconds));
                    CompletableFuture.delayedExecutor(rangeTypeMs, TimeUnit.MILLISECONDS).execute(() ->
                            world.execute(() -> grantItemAndRemoveEntity(null, store, playerRef, itemRef, itemId, targetAmount, traceId, "timed"))
                    );
                }
                TRACE_COLLECT_APPLIED.put(traceId, true);
                movedTowardPlayer[0]++;
                addedPickupComponent[0]++;
            }
        });

<<<<<<< HEAD
        if (DEBUG_VERBOSE) {
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "][SWEEP " + (sweepIndex + 1) + "] "
                    + "source=(" + sourceX + "," + sourceY + "," + sourceZ + ")"
                    + " playerPos=" + playerTransform.getPosition()
                    + " collectRange=" + collectRange
=======
        if (isDebug()) {
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "][SWEEP " + (sweepIndex + 1) + "] "
                    + "source=(" + sourceX + "," + sourceY + "," + sourceZ + ")"
                    + " playerPos=" + playerTransform.getPosition()
>>>>>>> cd2cc2b (Prepare clean project state)
                    + " sourceRadius=" + String.format(java.util.Locale.ROOT, "%.2f", sourceRadius)
                    + " totals: seen=" + totalSeen[0]
                    + " null=" + skippedNull[0]
                    + " empty=" + skippedEmptyStack[0]
                    + " cantPickup=" + skippedCannotPickUp[0]
                    + " sourceOut=" + skippedSourceDistance[0]
<<<<<<< HEAD
                    + " playerOut=" + skippedPlayerDistance[0]
=======
>>>>>>> cd2cc2b (Prepare clean project state)
                    + " moved=" + movedTowardPlayer[0]
                    + " pickupAdded=" + addedPickupComponent[0]
                    + (samples.length() > 0 ? " samples:" + samples : ""));
        }

        if (sweepIndex >= RANGE_SWEEP_TICKS - 1) {
            TRACE_COLLECT_APPLIED.remove(traceId);
        }
    }

    private static void grantItemAndRemoveEntity(CommandBuffer<EntityStore> commandBuffer,
                                                 Store<EntityStore> store,
                                                 Ref<EntityStore> playerRef,
                                                 Ref<EntityStore> itemRef,
                                                 String itemId,
                                                 int amount,
                                                 long traceId,
                                                 String mode) {
        if (store == null || playerRef == null || itemRef == null || itemId == null || itemId.isBlank()) {
            return;
        }
        ItemComponent existingItem = store.getComponent(itemRef, ItemComponent.getComponentType());
        if (existingItem == null || existingItem.getItemStack() == null || existingItem.getItemStack().isEmpty()) {
            return;
        }

        try {
            var player = store.getComponent(playerRef, com.hypixel.hytale.server.core.entity.entities.Player.getComponentType());
            if (player != null) {
                player.giveItem(new ItemStack(itemId, Math.max(1, amount)), playerRef, store);
            }
        } catch (Exception ignored) {
        }

        try {
            existingItem.setItemStack(ItemStack.EMPTY);
            existingItem.setRemovedByPlayerPickup(true);
        } catch (Exception ignored) {
        }

        try {
            if (commandBuffer != null) {
                commandBuffer.removeEntity(itemRef, RemoveReason.REMOVE);
            } else {
                store.removeEntity(itemRef, RemoveReason.REMOVE);
            }
        } catch (Exception ignored) {
        }
<<<<<<< HEAD
        if (DEBUG_VERBOSE) {
=======
        if (isDebug()) {
>>>>>>> cd2cc2b (Prepare clean project state)
            LOGGER.at(Level.INFO).log("[CobblestoneZufall][DBG#" + traceId + "] forcedCollect mode=" + mode
                    + " itemId=" + itemId + " amount=" + Math.max(1, amount));
        }
    }

    private static long computeSweepIntervalMs(int rangeTypeMs) {
        // Sweep quickly to reliably catch the fresh dropped entity near the source block.
        return 50L;
    }

<<<<<<< HEAD
}
=======
    private static boolean isDebug() {
        CobblestoneZufallPlugin plugin = CobblestoneZufallPlugin.getInstance();
        if (plugin == null || plugin.getConfigManager() == null) {
            return false;
        }
        return plugin.getConfigManager().isDebugEnabled();
    }

}

>>>>>>> cd2cc2b (Prepare clean project state)
