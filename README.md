# CobblestoneZufall Mod

This mod allows you to customize the drops from cobblestone generators (lava + water).

## Configuration

The configuration is stored in `cobble_config.json`. You can manually edit this file to add drops.

Example `cobble_config.json`:
```json
[
  {
    "itemId": "hytale:gold_ore",
    "chance": 0.1
  },
  {
    "itemId": "hytale:diamond_ore",
    "chance": 0.05
  }
]
```
- `itemId`: The ID of the item/block to drop.
- `chance`: The percentage chance (e.g., 0.1 means 0.1%).

## Commands

- `/cob`: Starts the in-game chat UI flow for the item/block currently held in your hand.
  1. Hold the target item/block in your hand.
  2. Run `/cob`.
  3. Enter a chance in chat, e.g. `0.1%`, `0,1%`, or `1/10000`.
  4. Type `cancel` to abort.

## Building

Run `gradlew build` to build the mod. The output JAR will be in `build/libs`.
