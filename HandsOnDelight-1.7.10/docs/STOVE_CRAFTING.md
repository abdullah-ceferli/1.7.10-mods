# Stove crafting — 18 November 2026

Hands-On Delight **1.5.1-backport.6** adds a native stove recipe when no stove recipe is registered. It keeps the source iron-and-brick pattern, using a furnace in place of the modern campfire that Minecraft 1.7.10 does not provide.

Use a crafting table:

```text
Iron ingot   Iron ingot   Iron ingot
Brick block  Empty        Brick block
Brick block  Furnace      Brick block
```

This consumes 3 iron ingots, 4 brick blocks and 1 furnace, producing 1 stove. Loose brick items are not brick blocks. NEI shows the recipe in the normal crafting category. Existing stove recipes are kept, and the fallback does not require a new world.

The build also includes the earlier NEI assembly-color fix. Original UI artwork is unchanged.
