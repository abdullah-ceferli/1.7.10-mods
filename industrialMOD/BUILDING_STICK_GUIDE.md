# Building Stick for Minecraft 1.7.10

Version **2.12.0** adds `industrialtech:building_stick` to Industrial Technology. Its Alt wheel uses the **original Effortless Building block-mode icons**, copied unchanged from the upstream atlas. The shape engine and native 1.7.10 placement service are implemented in this mod.

## Use the stick

1. Craft the stick with an **iron ingot**, **redstone dust** and a **stick** in a vertical column, in that order. Find **Building Stick** in NEI or the Industrial Technology creative tab.
2. Hold the tool and **sneak-right-click an existing ordinary building block** to pick its material. Survival placement takes matching, untagged block items from your inventory. Creative uses the chosen material without consuming inventory.
3. **Hold Left Alt**, move the pointer to a mode and release Alt to select it. You can also click a mode. The key can be rebound in Controls. **Right-click air before selecting a start point** to open the same wheel with click selection, useful for touch controls.
4. Right-click a block face to choose the first placement cell beside it. Aim at the end and right-click again. The outline/ghost cells preview the shape. Single mode places immediately after one click.
5. With a start point selected, aiming into air uses a nearby virtual endpoint five blocks along your look direction. Right-click air to finish there, allowing walls/lines without a background support wall.

**Sneak-right-click air** or use **Stop / cancel** in the wheel to clear the selection and stop pending work. Already placed blocks remain. Escape closes the wheel without selecting another mode. Picking another material or changing mode also stops the previous queued build.

## Carpenter's Blocks with Chisel covers (2.16.0)

Sneak-right-click a configured Carpenter's Block with the Building Stick. The stick saves its shape, orientation, cover block and metadata, individual face covers, dyes, overlays, illumination and carved design. The material line also names its main cover. Choose Line (or another existing shape), then select the start and end as usual. Copies keep the original orientation even when the player faces another way. The source block is read without removing, rotating or modifying its covers.

Supported decorative single-block types are **Carpenter's Block** (including its slabs), **Slope**, **Stairs**, **Collapsible Block** and **Barrier** from Carpenter's Blocks 3.3.8.2. Safes, doors, beds, garage doors, flower pots and other specialized/container or multi-block tiles remain excluded. This is optional support: ordinary block/liquid building still works without Carpenter's Blocks installed.

In Survival, each copy consumes one matching Carpenter block item plus the exact cover/decoration items represented in the original. Have the same Chisel variant in your inventory; dyes, overlays, side covers and illumination each need their own matching items. Rotated log covers use normal log inventory items while retaining the saved cover axis. Missing any material stops the remaining build without spending a partial set. Creative needs no materials.

New copies have their own coordinates and the placing player's normal Carpenter ownership. Existing blocks and inventories are never replaced. A location that would strip an adjacent Carpenter side cover is skipped. Native Carpenter placement reshaping is bypassed, so copying a slope does not transform the source or neighboring slopes. Forge cancellation restores the target and all reserved materials. Completed copies and stick templates persist normally; queued jobs remain transient. Selecting another ordinary block/liquid clears the Carpenter template.

Only the listed appearance fields are copied; arbitrary tile NBT, inventories, owners and original coordinates are never cloned. The template uses namespaced item identifiers. All work shares the existing 2,048-candidate/64-placement-attempt server budget, 4,096-cell limit, chunk margin checks and 16-job cap. No extra global scan, ticking tile or renderer is added. Native Carpenter and Chisel handle the resulting block models and textures.

Carpenter's Blocks intercepts sneak-use before normal item use. The stick handles that specific selection through a standard C08 block-use packet and a read-only server interaction hook, preventing Carpenter's alternate activation from editing the original. Shape/configuration packets and their validation remain unchanged. Android/Zalith confirmation remains pending.

## Modes

| Mode | Selection behavior |
| --- | --- |
| Single | One placement cell |
| Line | Straight line on the largest selected X, Y or Z span |
| Diagonal line | Integer 3D line joining both selected points |
| Wall | Vertical rectangle, aligned to the larger horizontal span |
| Floor | Horizontal rectangle at the first point's height |
| Box | Cuboid between the two points; Hollow selects its surface |
| Frame | Only the edges of that cuboid |
| Diagonal wall | Vertical extrusion of a diagonal X/Z line |
| Slope floor | Rectangle with height interpolated along its larger horizontal span |
| Circle | Horizontal disc centered at the first point; Hollow makes a ring |
| Cylinder | Circle radius in X/Z, height between the two selected Y values; Hollow makes a closed shell |
| Sphere | Ball centered at the first point, radius set by the second point; Hollow makes a shell |

The round-shape radius is rounded to a whole block. **Hollow** applies to Box, Circle, Cylinder and Sphere. The other modes keep their natural geometry. The original atlas's skeleton-cube option supplies the Frame icon.

## Placement and limits

- Maximum span: **33 blocks on each axis**. Round shapes support radius up to **16**.
- Maximum accepted shape: **4,096 placement cells**. Oversized plans are rejected before changing the world or spending materials.
- Existing solid blocks, machines, containers, entity collisions, protected cells and unloaded cells are skipped. The tool only fills air/replaceable positions; it does not mine or replace existing structures.
- Materials must be ordinary `ItemBlock` items without stored NBT or tile-entity state, except the explicitly supported decorative Carpenter templates above. Water and lava are supported separately as bucket-paid materials in 2.13.0; machines, other fluids, containers and tagged block stacks remain excluded. See HATCH_GUIDE.md. Special item behavior that redirects a placement outside its intended cell, or creates a tile entity, is canceled.
- The real player's permissions, spawn protection, adventure editing checks and Forge placement events are respected. Canceled placements restore reserved Survival material. One accepted ordinary placement costs one block, using native item placement/orientation behavior.
- Running out of material stops the remaining work. Placed blocks remain. Switching away from the tool, disconnecting or changing dimension also stops the job.
- Shape preparation visits at most **2,048 candidates per server tick**, at most **1,024 per job visit**. Placement attempts share **64 per server tick**, at most **32 per job visit**, including blocked/canceled attempts. Up to 16 players can have jobs in progress, with a rotating visit order.
- The preview is limited to **128 ghost cells** plus the shape's bounding outline, and uses a fixed preparation budget. No work runs while the tool is inactive. The tool requests no chunk tickets and does not force neighboring chunks to load. Native block behavior and neighbor updates retain their normal Minecraft costs.

The selected material, mode and first point are saved on the stick. The first point is bound to its selecting player/dimension. Queued builds are transient and stop on server shutdown; already placed world blocks remain saved normally.

## Installation and verification

Replace the previous Industrial Technology JAR with **IndustrialTechnology-1.7.10-2.16.0.jar** on **both client and server**, keeping only one version. Keep Baubles. Complete original OpenComputers 1.8.10 remains bundled, so do not install a second OpenComputers JAR.

Native test reports are in `docs/test-results/2.12.0/`. They cover real Survival/Creative placements, protection refunds, out-of-stock behavior, limits, packet validation and the radial menu/preview through actual client/server packets in disposable worlds. Android/Zalith input and frame-rate results remain to be tested on a device.

This stick provides the modes listed above. Effortless Building's original source and icon credits are retained in [THIRD_PARTY.md](THIRD_PARTY.md) and `development-assets/builder/effortless-assets.json`. The GUI integration and original icon atlas carry LGPL notices; the corresponding source is supplied in the sources JAR. Original project: https://www.curseforge.com/minecraft/mc-mods/effortless-building .
