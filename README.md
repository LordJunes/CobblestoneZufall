# CobblestoneZufall - Minecraft_CEO Edition

> **A unique Hytale generator mod with tier-based progression and integrated economy.**



## Quick Start Instructions

### For Lazy People
1. Download this project as **ZIP** and extract it.
2. Double-click **`BUILD_MOD.bat`**. 
   *(The script will automatically try to find your Hytale installation and build the mod for you!)*

### For Normal People
1. Clone the repository: `git clone <repo_url>`
2. Place your `HytaleServer.jar` into the `/server` directory to provide the Hytale API dependencies.
3. Run the build command:
   ```powershell
   ./gradlew shadowJar

# CobblestoneZufall

CobblestoneZufall is a full cobblestone-generator progression mod for Hytale:
- tier-based drop progression
- in-game admin UI for full balancing control
- built-in economy (no Vault dependency)
- player upgrade flow
- advanced auto-collect with range + time profile (Teleport/100-5000ms)
- BetterScoreboard placeholder bridge

## Core Features

### Generator System
- Detection via `BreakBlockEvent` + lava/water neighborhood.
- Supported generator blocks: `Rock_Stone_Cobble` plus configured drop block IDs.
- Configurable regeneration delay (`Block Delay`).
- Natural lava/water cobblestone generation is overridden.

### Drops, Money, Repair
- Configurable per drop entry:
  - `Drop Amount`
  - `Pay Amount`
  - `Repair Chance %`
  - `Repair Amount %`
- Values are resolved from the actually mined visible drop profile (no global cross-mixing).

### Auto-Collect
- `Collect Mode`: `VANILLA` or `RANGE 1..32`.
- `RangeTyp`:
  - `Teleport`
  - `100ms .. 5000ms` (100ms steps)
- Behavior:
  - `Teleport`: instant inventory credit, source entity removed.
  - Timed mode: visual travel over configured duration, then server-side guaranteed inventory credit.

### Ore Mapping (Block -> Item)
- `Ore_Copper_Stone -> Ore_Copper`
- `Ore_Iron_Stone -> Ore_Iron`
- `Ore_Gold_Stone -> Ore_Gold`
- `Ore_Silver_Stone -> Ore_Silver`
- `Ore_Cobalt_Shale -> Ore_Cobalt`
- `Ore_Thorium_Mud -> Ore_Thorium`
- `Ore_Adamantite_Magma -> Ore_Adamantite`
- `Ore_Mithril_Stone -> Ore_Mithril`

## Commands

### Player
- `/cob` - opens upgrade UI.
- `/money` - shows balance.
- `/money pay <player> <amount>` - pays another player.

### Admin
- `/cobadmin` - opens admin config UI.
- `/cobadmin curve` - opens curve model editor.
- `/ecoadmin give <player|me> <amount>`
- `/ecoadmin set <player|me> <amount>`
- `/ecoadmin take <player|me> <amount>`
- `/ecoadmin balance <player|me>`

## Placeholders (BetterScoreboard)

- `{cob_tier}`
- `{cob_next_tier}`
- `{cob_max_tier}`
- `{money}`
- `{avg_blocks_sec}`
- `{avg_blocks_min}`
- `{est_blocks_hr}`
- `{avg_blocks_hr}`
- `{total_blocks}`

Formatting:
- Stat values use 2 decimal places.
- `{total_blocks}` uses thousand separators.

## Data and Persistence

- `cobble_config.json` - main configuration.
- `CobPlayerSaveTable.csv` - PlayerUUID -> tier.
- `CobMiningTotals.csv` - persistent lifetime mining totals.

## Build and Deploy

### Build
```powershell
.\gradlew.bat clean build -x test
```

### Deploy to Hytale Mods
```powershell
.\gradlew.bat deployToHytaleMods
```

The deploy task copies the current JAR to:
- `%APPDATA%\Hytale\UserData\Mods`

and moves old `cobblestonezufall-*.jar` files to:
- `%APPDATA%\Hytale\UserData\Mods\Backup`

## Technical Entry Points

- Entry point: `CobblestoneZufallPlugin`
- Generator logic: `CobblestoneGeneratorSystem`
- Config backend: `ConfigManager`
- Placeholder bridge: `ScoreboardPlaceholderBridge`

For complete technical details, see:
- `PROJECT_CONTEXT.md`



