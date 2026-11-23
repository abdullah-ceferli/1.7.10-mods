# Sliding Hatch — Industrial Technology 2.15.0

The hatch has a dark gray frame, recessed metal cover, corner fasteners and narrow yellow/black safety strips. Two cover panels slide sideways into the rails over 12 ticks (about 0.6 seconds). Every placement variant animates both opening and closing. Right-click the frame or cover with an empty hand or a tool. Clicking again during motion reverses from the current position. A living entity in the opening prevents closing; entering during closing reopens it.

## Centering it between floor blocks

Craft two hatches with three iron ingots across the top row, then piston / redstone / piston in the second row. Put one hatch alone in a personal crafting grid or crafting table to cycle normal, edge centered, corner centered, then normal again.

- **Normal:** one block wide, aligned to one floor cell.
- **Edge Centered:** one block wide across the join of two floor cells. Click the top of an ordinary solid floor block near the edge you want to cross. The nearest edge chooses both the axis and the neighboring cell.
- **Corner Centered:** one block wide across four cells. Click near their shared corner on the top of a floor block.

The hatch is inset flush into the floor, rather than placed as a plate above it. Original floor material remains in the space outside the hatch opening. Two different Chisel floor variants can meet under an edge-centered hatch: each side retains its own block and metadata. This does not make the hatch two or four blocks wide. Remove any extra blocks deeper beneath the opening if you need a shaft.

Existing hatches placed into empty space still work. Hold an ordinary solid building block and right-click an empty offset part to fill the floor around that part. Survival consumes one block per filled part. A filled part allows normal adjacent block placement when a building block is held, instead of toggling the cover. Unsupported or incompatible building blocks follow normal placement behavior.

Inset/fill accepts inert opaque full cubes, including the tested Chisel technical blocks. Inventories, tile entities, machines with tile entities, unbreakable blocks, falling blocks, random-ticking blocks, power-providing blocks and TNT cannot be used as backing floor. Their normal blocks are never carved into hatch parts. Placement beside/under unsuitable supporting blocks retains the ordinary thin-hatch behavior when the required cells are empty.

Breaking any part removes the whole hatch and restores the original floor blocks in place. Survival drops one hatch of the same variant, with no extra floor-item drops. If an external mod replaces a hatch cell directly, its replacement is retained and that cell's saved floor is returned as an item; other loaded group parts restore their floors. Native explosion removal restores the group before normal explosion processing continues.

## Multiplayer, saves and performance

Client and server require the same Industrial Technology version. Native Forge multi-place cancellation restores all original floor blocks and refunds the hatch. Carving, interaction, infill and group removal honor protection events. Incomplete or unloaded groups refuse normal interaction/removal until available; placement never forces neighboring chunks to load. Ordinary full floor blocks cannot coexist in one native cell, so the hatch stores its retained floor internally and renders/collides with the remaining floor pieces.

Old hatch NBT remains compatible: missing floor data means empty space, and old open/closed state loads at its saved endpoint. Existing offset hatches can receive floor infill. Each part saves its original floor block name and metadata. Animation start time and starting position are saved and synchronized once per toggle/reversal; no per-frame packets are sent.

There is no GUI, power requirement or ticking tile entity. Frames and floor pieces stay in cached native chunk geometry. A visible closed/moving cover uses one small native-atlas tile-render draw; a fully open cover draws no panels. Only a moving group schedules short safety checks, which stop once it settles. No custom shader, global scan or desktop-only input is required.

## Building Stick liquids

Sneak-right-click water or lava with the Building Stick to select it. Select **Floor** from the Alt wheel and choose the height and opposite corners. The same sheet can be placed overhead; native sources fall and spread like bucket placements. Each accepted Survival source spends one matching filled bucket and returns one empty bucket. Existing sources, other liquids, solid/protected/unloaded positions and Nether water are skipped. Existing shared scan and placement budgets are unchanged.

## Installation and validation

Replace the older Industrial Technology JAR with `IndustrialTechnology-1.7.10-2.15.0.jar` on both client and server. Keep Baubles; OpenComputers remains bundled. The 32 colored lamps and all unrelated gameplay remain unchanged.

Native verification uses disposable Forge worlds with actual HBM X5771, Chisel 2.9.5.11 and client NEI 2.3.54. It covers Survival material conservation, real Chisel metadata, Forge cancellation, partial-floor collision, safe closing/reversal, fresh-JVM reload and real client right-click/description packets. World, opening, closing and inventory screenshots are stored under `docs/test-results/2.15.0/`. The unit suite also verifies floor geometry and motion. Android/Zalith device confirmation remains pending. User saves and installed game JARs are not modified.
