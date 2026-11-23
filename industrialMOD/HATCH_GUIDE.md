# Sliding Hatch — Industrial Technology 2.13.0

The hatch uses a dark gray square frame, a recessed metal cover, corner fasteners and narrow yellow/black safety strips along both sides, following the supplied reference. The cover retracts horizontally into the side rails. The open center has no lid collision; click the frame to close it again. It does not rotate upward.

Craft two hatches with three iron ingots in the top row, then piston / redstone / piston in the second row. Find Sliding Hatch in NEI or the Industrial Technology tab.

Put one hatch alone in either the personal 2×2 grid or a crafting table to cycle its placement variant:

- **Normal:** a one-block hatch aligned to one grid cell.
- **Edge Centered:** the same one-block hatch shifted half a block across a cell boundary. Turn 90 degrees before placement to change which horizontal axis is offset.
- **Corner Centered:** the same one-block hatch shifted half a block on both axes, centered at the join of four cells.

The cover does not become two or four blocks wide. Offset variants reserve two or four native cells for their separate collision/render parts. Those cells must be empty; this build does not cut a hole through existing solid cubes. Prepare the opening first. Place against the upper half of a side, or a block's underside, for a top-mounted hatch; placement on a block's top produces a bottom-mounted hatch. Remove the supporting blocks beneath the cover if you need a passage downward.

Every part opens and closes together and synchronizes to other players. Breaking one part removes the group and drops one hatch of the same variant. Placement uses native Forge multi-block protection and rollback, and interaction/removal checks every part. Incomplete or unloaded groups refuse an operation until their chunks are available. Nothing forces chunks to load.

There is no GUI, power requirement, ticking tile or per-frame tile renderer. The static model is compiled into ordinary chunk geometry. Opening switches the model to its retracted state; this version does not interpolate an opening animation.

## Building Stick liquids

Sneak-right-click water or lava with the Building Stick to select that liquid. Select **Floor** from the Alt wheel for a horizontal sheet; choose its height and opposite corners with the usual two clicks. The same shape can be placed overhead as a roof. Native sources then fall and spread just like bucket placements. Other existing shapes also accept the liquid material.

In Survival, each accepted source requires one matching filled bucket in the inventory and returns one empty bucket. Out-of-stock builds stop. Creative needs no buckets. Existing sources, other liquids, solid blocks, protected positions and unsafe unloaded edges are skipped. Nether water placement is rejected without spending a bucket. Cancellation refunds the attempted source. Shared shape/placement budgets remain 2,048 candidate checks and 64 placement attempts per server tick; native flowing-liquid simulation has its normal game cost.

## Installation and validation

Install IndustrialTechnology-1.7.10-2.13.0.jar on both sides, replacing the older Industrial Technology JAR. Keep Baubles; OpenComputers remains bundled. Tests use disposable Forge worlds, actual item-use/placement events, liquid flow, client description packets, rendering and fresh-process saves. Android/Zalith device confirmation remains pending. User saves and installed game JARs were not changed.
