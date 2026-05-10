# Aeronaut's Compass

**Point your compass anywhere. No Lodestone required.**

Aeronaut's Compass turns a vanilla compass into a precision navigation tool. Set any destination — by typing coordinates, clicking on the map, or selecting a waypoint — and your compass points there instantly.

Whether you're crossing continents on foot or charting a course for your airship, your compass always knows the way.

---

## Features

### Navigator's Table *(standalone — no other mods required)*
- Craft with a **Cartography Table + Compass** (shapeless, any crafting grid)
- Insert a compass, type target **X** and **Z** coordinates, give it a name, hit **Apply**
- Inserting an already-configured compass **auto-fills** its saved coordinates and name
- Break it with an **axe** to pick it back up

### JourneyMap Integration *(optional)*
- Right-click anywhere on the fullscreen map → **"Set Compass"**
- Right-click a waypoint pin → compass points there, name applied automatically

### General
- Named compass — labelled with your destination name or coordinates
- Reset compass — place a configured compass alone in any crafting grid to restore a vanilla compass
- Multiplayer ready — all item changes are handled server-side
- Dimension aware — Overworld, Nether, End and modded dimensions

---

## Create: Aeronautics — One-Click Autopilot

Create: Aeronautics' Navigation Table normally requires a **Lodestone placed physically at the destination** to plot a course. Want to route to a distant base? You'd have to travel there first just to drop a Lodestone.

Aeronaut's Compass removes that constraint entirely:

1. Open JourneyMap (`J`) and right-click your destination on the map *(or type coordinates in the Navigator's Table)*
2. Your compass is set instantly — no Lodestone, no travel required
3. Insert it into the Create: Aeronautics Navigation Table
4. Your airship flies itself there

---

## Requirements

| Dependency | Version | Type |
|---|---|---|
| Minecraft | 1.21.1 | Required |
| NeoForge | 21.1.x | Required |
| JourneyMap | 6.0.0-beta+ | Optional |
| Create: Aeronautics | any | Optional |

---

## Building from source

```bash
./gradlew build
```

Output JAR: `build/libs/aeronautscompass-1.0.0.jar`

---

## License

MIT — see [LICENSE](LICENSE)
