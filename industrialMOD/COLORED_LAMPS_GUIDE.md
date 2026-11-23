# Colored block lamps — Industrial Technology 2.14.0

The mod adds full, smooth-faced colored lamp blocks in all 16 dye colors. White, blue, green, red, yellow and the other native dye colors each have two types:

- **Lamp:** always emits native block-light level 15, without power or fuel.
- **Redstone Lamp:** emits level 15 while powered. It switches on immediately and switches off after four game ticks, like the native redstone lamp.

Their block surfaces are colored. Minecraft 1.7.10's ordinary light propagation remains uncolored; this update does not add RGB lighting or require a renderer/shader mod. The existing Green Redstone Lamp keeps its original identity, texture and recipe.

## Crafting

Craft **four always-on lamps** using this pattern:

```text
Glass       Glowstone dust   Glass
Glowstone   Matching dye     Glowstone
  dust                        dust
Glass       Glowstone dust   Glass
```

The center dye selects the color: bone meal for white, lapis lazuli for blue, cactus green for green, etc. NEI displays each exact recipe.

Combine **one colored always-on lamp + one redstone dust** in either crafting grid to get its redstone-controlled version. Combine a lamp with another dye to recolor it; always-on lamps remain always-on and redstone lamps retain their redstone type. Recoloring returns one lamp and consumes one dye.

The Industrial Technology creative tab and NEI contain all 32 item variants. The internal powered state is not a separate obtainable item. Breaking, Silk Touch and middle-click picking retain the lamp color and return the ordinary redstone item when appropriate. Levers, torches and other normal attachments work on every face.

## Performance and installation

These are ordinary full blocks rendered through the native block renderer using a native quartz texture and metadata colors. They have no tile entities, custom tick loop, per-frame renderer, dynamic-light scanning or custom network packets. Always-on lamps have no scheduled work. Redstone lamps schedule a single delayed off check when power disappears; Minecraft handles ordinary neighbor and lighting updates.

Replace the old Industrial Technology JAR with **IndustrialTechnology-1.7.10-2.14.0.jar** on both client and server. Keep Baubles and do not add a second OpenComputers JAR. Existing user worlds and installed game files were not changed during development.

The production JAR passes 141 unit tests and native Forge client/server checks. The HBM server fixture checks all 64 recipes, all colors, Survival placement, attached levers, on/off transitions, drops, collision and a fresh-JVM reload. The client fixture checks all 32 names/icons, world rendering, synchronized native light, inventory rendering and actual NEI recipe handlers. Evidence and gallery images are under docs/test-results/2.14.0. Android/Zalith device confirmation remains pending.
