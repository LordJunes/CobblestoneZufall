# PROJECT_CONTEXT

This file is the single source of truth for the current CobblestoneZufall implementation.

## 1) Project Identity

- Project: `CobblestoneZufall`
- Branding: `Minecraft_CEO Edition`
- Type: Hytale server mod (Java)
- Primary package namespace: `com.minecraft_ceo.cobblestonezufall`
- Build toolchain: Gradle + ShadowJar

## 2) Runtime Architecture

### Plugin entry
- Main class: `com.minecraft_ceo.cobblestonezufall.CobblestoneZufallPlugin`
- Manifest main entry (`src/main/resources/manifest.json`):
  - `"Main": "com.minecraft_ceo.cobblestonezufall.CobblestoneZufallPlugin"`

### Registered systems
- `CobblestoneGeneratorSystem`
- `CobbleNaturalGenerationOverrideSystem`
<<<<<<< HEAD
=======
- `CobbleNaturalSuppressionTickSystem`
>>>>>>> cd2cc2b (Prepare clean project state)

### Registered commands
- `/cob` (player upgrade UI)
- `/cobadmin` (admin balancing UI)
- `/money` (player economy)
- `/ecoadmin` (admin economy)

### Setup lifecycle
- `ConfigManager.load()`
- `MiningRateTracker.load()`
- `EconomyBridge.load()`
- `ScoreboardPlaceholderBridge.register()`

### Shutdown lifecycle
- `ScoreboardPlaceholderBridge.unregister()`
- `EconomyBridge.save()`
- `MiningRateTracker.save()`

## 3) Generator Logic (Core Gameplay)

### Trigger
- `BreakBlockEvent` in `CobblestoneGeneratorSystem`

### Generator detection
A block is managed when all checks pass:
- Block is `Rock_Stone_Cobble`, or
- Block matches a configured tier drop block id, and
- Lava + water neighborhood requirement is valid.

### Regen protection
- Per-position pending regen table: `PENDING_REGEN_UNTIL`
- Break events inside active regen window are canceled.

### Regeneration
- Delay controlled by config (`regenDelayMs`)
- Replacement block resolved via tier drop random
- Fallback to first valid block asset when needed

<<<<<<< HEAD
=======
### Natural generation suppression
- `FluidCollisionOverride` patches runtime `DefaultFluidTicker` collision outputs for `Lava*` + `Water*` to `Air`, disabling engine-level natural cobble creation at source.
- `CobbleNaturalGenerationOverrideSystem` hard-stops natural lava+water cobble creation.
- Natural cobble at fluid-contact positions is converted to air.
- Multiple delayed neighborhood sweeps are executed to catch delayed fluid updates.
- `CobbleNaturalSuppressionTickSystem` performs periodic suppression scans near players as event-independent fallback.
- `CobblestoneGeneratorSystem` adds a per-break position guard that repeatedly clears natural cobble at the exact generator block position during the configured delay window.
- Only plugin-managed placements from scheduled regeneration are allowed.

>>>>>>> cd2cc2b (Prepare clean project state)
## 4) Drop Profile, Reward, and Repair

Final behavior:
- `Drop Amount`, `Pay Amount`, `Repair Chance`, `Repair Amount` are resolved from the actually mined visible drop profile.
- This avoids value mixing between cobble and ore profiles.
<<<<<<< HEAD
=======
- Core cobble/ore ids have non-zero default Pay/Repair templates (pre-filled in admin editor and applied as fallback).
>>>>>>> cd2cc2b (Prepare clean project state)

Resolution order:
1. Direct block-id match against tier drops (`brokenBlockId`)
2. Block -> item mapping fallback
3. Fallback to currently selected random tier drop

Reward application:
- Economy payment (`payAmount`) per valid break of resolved profile
- Tool repair:
  - Chance in percent
  - Restore amount as percent of max durability

## 5) Built-in Ore Block -> Ore Item Mapping

- `Ore_Copper_Stone` -> `Ore_Copper`
- `Ore_Iron_Stone` -> `Ore_Iron`
- `Ore_Gold_Stone` -> `Ore_Gold`
- `Ore_Silver_Stone` -> `Ore_Silver`
- `Ore_Cobalt_Shale` -> `Ore_Cobalt`
- `Ore_Thorium_Mud` -> `Ore_Thorium`
- `Ore_Adamantite_Magma` -> `Ore_Adamantite`
- `Ore_Mithril_Stone` -> `Ore_Mithril`

This mapping defines credited item ids.

<<<<<<< HEAD
## 6) Collect System (Range + RangeType)

### Collect Range
Admin UI toggles:
- `VANILLA` (range = 0)
- `RANGE 1..64 BLOCKS`

### RangeType
Admin UI toggles:
- `Teleport` (internally `-1`)
- `100ms .. 5000ms` in `100ms` steps

### Behavior
- On break trace, nearby dropped entity is searched around source.
- Target stack is forced to configured `Drop Amount`.

Teleport mode:
=======
## 6) Collect System (BLOCK TRAVELTIME)

### BLOCK TRAVELTIME
Admin UI button:
- `0ms` (`NO DELAY`)
- `100ms .. 5000ms` in `100ms` steps

### Behavior
- Collection is no longer range-based.
- On valid generator break, nearby dropped entity is processed from source position.
- Target stack is forced to configured `Drop Amount`.

No-delay mode (`0ms`):
>>>>>>> cd2cc2b (Prepare clean project state)
- Instant inventory grant
- Hard entity removal (`ItemStack.EMPTY`, pickup removed, entity removed)
- Prevents visual leftovers and duplicate loot

Timed mode (`100..5000ms`):
- Visual travel using `PickupItemComponent(..., travelSeconds)`
- After delay: server-side guaranteed inventory grant + cleanup safety removal

## 7) UI and Commands

### `/cobadmin` (`CobbleConfigPage`)
- Tier info
<<<<<<< HEAD
- Block delay (`regenDelayMs`)
- Expected BPS
- Collect range toggle
- RangeType toggle (left/right click)
=======
- Block delay button (`+/-100ms`, middle click reset, default `1000ms`)
- Estimated mined blocks/sec button (`-0.1/+0.1`, middle click reset, default `1.0`)
- BLOCK TRAVELTIME button (`+/-100ms`, middle click reset, default `100ms`)
- DEBUG button (`DEBUG: ON/OFF`) enabling verbose runtime diagnostics
>>>>>>> cd2cc2b (Prepare clean project state)
- Per-drop list (up to 12 visible rows, sorted by chance)
- Links:
  - Curve model (`CobbleCurvePage`)
  - Drop editor (`CobbleDropEditPage`)

<<<<<<< HEAD
=======
### `/cobadmin curve` (`CobbleCurvePage`)
- Chance/Cost mode cycle buttons support middle-click reset to default mode `POWER`.

>>>>>>> cd2cc2b (Prepare clean project state)
### Drop editor (`CobbleDropEditPage`)
Per drop:
- `Drop Amount` (int >= 1)
- `Pay Amount` (double >= 0)
- `Repair Chance` (0..100)
- `Repair Amount %` (>= 0)

### Curve model (`CobbleCurvePage` + preview)
Tier progression tuning:
- Max tier
- Max upgrade cost
- Chance curve and cost curve
- Exponents/modes
- Start/end chances per item
- Apply across tiers

### `/cob` (`CobUpgradePage`)
- Player upgrade flow with confirmation
<<<<<<< HEAD
- Current and next values
=======
- Current tier vs viewed tier values (projection arrows)
>>>>>>> cd2cc2b (Prepare clean project state)
- Economy balance/cost interaction

## 8) Economy

- Implementation: `EconomyBridge` (internal currency, no Vault dependency)

Commands:
- `/money`
  - balance
  - `pay <player> <amount>`
- `/ecoadmin`
  - `give|set|take|balance <player|me> <amount?>`

Admin permissions:
- `cob.admin` or `money.admin` or `op` or `*`

## 9) BetterScoreboard Placeholder Bridge

- Bridge class: `ScoreboardPlaceholderBridge`
- Reflection-based optional integration (no hard compile dependency)

Registered placeholders:
- Tier: `{cob_tier}`, `{cob_next_tier}`, `{cob_max_tier}`
- Economy: `{money}`
- Mining: `{avg_blocks_sec}`, `{avg_blocks_min}`, `{est_blocks_hr}`, `{avg_blocks_hr}`, `{total_blocks}`

Calculations:
- `avg_blocks_sec`: fast early-phase seconds-based extrapolation (capped by minute window logic)
- `avg_blocks_min`: `avg_blocks_sec * 60`
- `est_blocks_hr`: `avg_blocks_min * 60`
- `avg_blocks_hr`: rolling real last 60 minutes
- `total_blocks`: persistent lifetime total

Formatting:
- Stat values: 2 decimals
- `total_blocks`: thousand separators, no decimals

Token normalization:
- Known BetterScoreboard token variants are normalized when saved.

## 10) Persistent Data

Main files:
- `cobble_config.json`
<<<<<<< HEAD
=======
- `cobble_ui_config.json`
>>>>>>> cd2cc2b (Prepare clean project state)
- `CobPlayerSaveTable.csv`
- `CobMiningTotals.csv`

Stored content:
- tiers, drops, upgrades
- curve model
- player tier assignments
<<<<<<< HEAD
- collect range / range type
- regen delay / expected BPS
- mining lifetime totals
=======
- block travel time
- regen delay / expected BPS
- mining lifetime totals
- UI display settings (curve rows per page, `/cob` visible rows)
>>>>>>> cd2cc2b (Prepare clean project state)

Economy storage:
- Internal persistence in `EconomyBridge`

## 11) Build, Release, Deployment

### Toolchain
- Java toolchain in Gradle: `25`
- `gradle.properties` currently contains:
  - `modVersion=1.2.5`

### Artifact
- `build/libs/cobblestonezufall-<version>.jar`
- `shadowJar` enabled, default `jar` task disabled

### Important Gradle tasks
- `build`
- `shadowJar`
- `deployToHytaleMods`
  - deploys to `%APPDATA%/Hytale/UserData/Mods`
  - moves older `cobblestonezufall-*.jar` to `Mods/Backup`
- `bumpModVersion`
- `releaseMod`

### BUILD_MOD.bat behavior
`BUILD_MOD.bat` is a one-click helper script that:
1. Auto-detects Hytale server jar at `%APPDATA%/Hytale/install/release/package/game/latest/Server/HytaleServer.jar`
2. Copies it to local `server/HytaleServer.jar` when missing
3. Runs `gradlew.bat clean shadowJar`
4. Auto-installs built jar into `%APPDATA%/Hytale/UserData/Mods`
<<<<<<< HEAD
=======
5. Exports example config files into `build/config_bundle`
>>>>>>> cd2cc2b (Prepare clean project state)

## 12) Resources / UI Files

Under `src/main/resources/Common/UI/Custom/Pages`:
- `CobbleConfig.ui`
- `CobbleCurveModel.ui`
- `CobbleCurvePreview.ui`
- `CobbleDropEdit.ui`
- `CobUpgrade.ui`

Other resource:
- `src/main/resources/data/cob_levels.csv` (default tier data)

## 13) Key Classes

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

## 14) Operational Notes

<<<<<<< HEAD
- Generator debug logging is disabled by default (`DEBUG_VERBOSE = false`).
=======
- Global debug logging is toggled at runtime via `/cobadmin` (`DEBUG: ON/OFF`).
>>>>>>> cd2cc2b (Prepare clean project state)
- `README.md` should stay aligned with this file.
- Known build warning: deprecation around `Player.getPlayerRef()`, currently non-blocking.

## 15) Status Summary

Current implementation is stable with:
- separated per-drop values,
<<<<<<< HEAD
- reliable range collect including teleport without duplicate loot,
=======
- reliable timed/no-delay collect without duplicate loot,
>>>>>>> cd2cc2b (Prepare clean project state)
- correct ore block/item mapping,
- persistent mining statistics and full placeholder bridge,
- production-ready build/deploy workflow.
