# Changelog — Aeronaut's Compass

## [1.1.0] — 2026-05-10

### Added
- **Treasure map support** — place a filled exploration map in the new map slot of the Navigator's Table; X / Z coordinates are read automatically from the map and pre-filled into the fields. Press Apply as usual to set the compass.
  - Map coordinates lock the X / Z fields — they cannot be edited while the map is in the slot
  - If the map has a custom name it is suggested in the Name field (still editable)
  - Map always takes priority: inserting a pre-configured compass does not override the map's coordinates
  - Ghost icons in empty slots hint what item goes where

### Fixed
- Name label no longer overlaps the Name text field

---

## [1.0.0] — 2026-05-10

### Added
- **Navigator's Table** — craftable block (Cartography Table + Compass, shapeless)
  - Insert a vanilla compass, type X / Z coordinates and an optional name, click Apply
  - Inserting a pre-configured compass auto-fills its saved coordinates and name
  - Compass is labelled with the destination name or coordinates
  - Lore line shows coordinates + reset hint
  - Break with an axe to pick the block back up
- **JourneyMap integration** (optional soft dependency)
  - Right-click anywhere on the fullscreen map → "Set Compass"
  - Right-click a waypoint pin → "Set Compass" (waypoint name applied automatically)
- **Reset recipe** — place a configured compass alone in any crafting grid to restore a plain vanilla compass
- **Multiplayer support** — all compass modifications are processed server-side
- **Dimension aware** — works in Overworld, Nether, End and modded dimensions
- **English and Italian** localisation
