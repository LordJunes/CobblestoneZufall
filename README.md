# CobblestoneZufall - Minecraft_CEO Edition

CobblestoneZufall is a Hytale server mod focused on cobblestone-generator progression, configurable drop balancing, and built-in player economy.

Primary code namespace: `com.minecraft_ceo.cobblestonezufall`

## For Lazy People

Use `BUILD_MOD.bat` from the project root.

What it does automatically:
1. Detects your Hytale server jar in `%APPDATA%/Hytale/install/release/package/game/latest/Server/HytaleServer.jar`
2. Copies it into local `server/HytaleServer.jar` if needed
3. Builds the mod with `gradlew.bat clean shadowJar`
4. Installs the output jar into `%APPDATA%/Hytale/UserData/Mods`
<<<<<<< HEAD
=======
5. Exports config examples into `build/config_bundle`
>>>>>>> cd2cc2b (Prepare clean project state)

Result: one click build + install workflow for local playtesting.

## For Developers

1. Clone the repository.
2. Place your server dependency jar in `server/` (for `compileOnly` resolution).
3. Build with Gradle:

```powershell
.\gradlew.bat clean shadowJar
```

4. Output jar:

```text
build/libs/cobblestonezufall-<version>.jar
```

Optional deploy task:

```powershell
.\gradlew.bat deployToHytaleMods
```

This deploys to `%APPDATA%/Hytale/UserData/Mods` and moves old mod jars to `Backup`.

## Features

- Tier-based cobblestone generator progression with upgrade flow (`/cob`)
- In-game admin balancing UI (`/cobadmin`) for:
  - Tier values
  - Drop probabilities
  - Per-drop `Drop Amount`, `Pay Amount`, `Repair Chance`, `Repair Amount %`
  - Curve model editing and preview
<<<<<<< HEAD
- Configurable block regeneration delay and expected BPS tuning
- Generator validation with lava/water neighbor checks
=======
  - Curve presets (`COMMON`, `BALANCED`, `RARE`, `LEGENDARY`) and strict/flexible input modes
- Click-based admin controls (left/right/middle click) for:
  - `Block Delay` (`+/-100ms`, middle click = default `1000ms`, `0ms` shown as `NO DELAY`)
  - `Estimated Mined Blocks/Sec` (`-0.1/+0.1`, min `0.0`, max `100.0`, middle click = default `1.0`)
  - `BLOCK TRAVELTIME` (`+/-100ms`, middle click = default `100ms`, `0ms` shown as `NO DELAY`)
  - `DEBUG: ON/OFF` (global high-verbosity debug mode for generator/natural suppression paths)
- Generator validation with lava/water neighbor checks
- Natural lava+water cobblestone generation is suppressed (only plugin-managed generator behavior is used)
  - Runtime fluid-collision override patches `Lava*`/`Water*` collision outputs to `Air` (engine-level natural cobble disable)
  - Event-based suppression + periodic tick fallback scan near players
  - Per-break position guard repeatedly clears natural cobble at the broken generator position until regen window is over
>>>>>>> cd2cc2b (Prepare clean project state)
- Regen protection window to prevent duplicate break handling
- Drop profile resolution based on the actually mined visible block profile
- Built-in economy bridge with player/admin commands:
  - `/money`
  - `/ecoadmin give|set|take|balance`
<<<<<<< HEAD
- Auto-collect system:
  - `VANILLA` or `RANGE 1..64`
  - `RangeType: Teleport` or `100ms..5000ms`
=======
- Auto-collect always applies to valid generator breaks (no range filter)
- Default Pay/Repair values are prefilled for core cobble/ore entries
>>>>>>> cd2cc2b (Prepare clean project state)
- Explicit ore block -> ore item mapping for correct item rewards
- BetterScoreboard placeholder integration (tier, economy, mining rates, totals)
- Persistent storage for config, player tiers, and mining totals

## Commands

Player:
- `/cob`
- `/money`
- `/money pay <player> <amount>`

Admin:
- `/cobadmin`
- `/cobadmin curve`
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

## Versioning and Context

- Mod version is defined in `gradle.properties` (`modVersion`).
- Technical source of truth: `PROJECT_CONTEXT.md`.
<<<<<<< HEAD
=======

## Config Files

Runtime (plugin data directory):
- `cobble_config.json` (tiers, drops, curve model, player tiers, runtime settings)
- `cobble_ui_config.json` (UI row limits: curve page and `/cob` rows)

Repository examples:
- `config/cobble_config.example.json`
- `config/cobble_ui_config.example.json`
- `config/cobble_curve_presets.example.json`
>>>>>>> cd2cc2b (Prepare clean project state)
