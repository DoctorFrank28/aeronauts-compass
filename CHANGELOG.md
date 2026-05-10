# Changelog — Aeronaut's Compass

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
