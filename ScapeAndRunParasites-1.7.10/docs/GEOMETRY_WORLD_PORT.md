# Geometry and world-query integration — 2026-11-14

This is verified subsystem work in an unfinished complete SRP backport. The complete source still fails compilation; no playable full-mod JAR has been built or installed.

## Geometry boundary

599 original expressions across 181 source units now use native 1.7 boxes through immutable modern operations. Constructors normalize coordinates; growth/shrinkage and directional expansion/contraction retain their original meanings. Native entity getters return snapshots; setters copy into the final native bounding-box object. Native movement keeps its mutable API.

The source's preferred SRG names matter: its three-coordinate expand means symmetric growth, while addCoord means directional expansion. Scoped type resolution retains ItemStack growth/shrinkage. The source migration is idempotent.

Original Minecraft 1.12.2 comparisons pass for 1,500 boxes and 1,500 ray segments (85,098 assertions). Four real Forge server groups cover the converted source fixture, snapshots/final boxes, falling collisions against stone, and query isolation from entity movement.

## World-position boundary

331 expressions across 134 original source units now use native Forge queries, tile operations, destruction and scheduled updates. World and chunk height overloads retain different return types. Native air, leaves/foliage, solidity, weather and tile hooks remain involved.

The top-surface scan includes y=0 and retains the modern -1 empty-column result. Native Chunk.getBlock cannot accept y=-1, so that boundary explicitly supplies air.

Block-loaded checks ignore y. Area checks retain original vertical boundaries, signed chunk rounding, traversal order and reversed ranges. Server lookups check existence without providing chunks. Original clients accept empty placeholders when allowEmpty is true; strict checks inspect the placeholder without loading terrain.

Explosion density retains horizontal sample offsets and float iteration precision. Direct execution of original 1.12 world methods matches 1,600 query cases and all sample coordinates/results for 350 random boxes (10,301 assertions). The comparison caught an incorrect NaN assumption: the original samples rather than rejecting the box. The final helper preserves that behavior.

Six Forge dedicated-server groups verify the native boundaries, terrain scans, absent chunks, tile coordinates, ray collisions and scheduled ticks. Two more groups run in a real production Minecraft client and integrated server, checking placeholder differences and unchanged chunk counts. The existing five client entity synchronization checks also pass.

Forge-specific behavior was checked against the official [1.12 World patches](https://github.com/MinecraftForge/MinecraftForge/blob/1.12.x/patches/minecraft/net/minecraft/world/World.java.patch), cached native Forge source, and original Minecraft bytecode.

## Verification and limits

Evidence: docs/test-results/2026-11-14-geometry-world/manifest.json. This stage adds 16 groups, bringing the aggregate to 77. All 1,094 original sources and 4,501 assets remain preserved. There are 34 compatibility units and 1,128 main Java units.

Commands: tools/verify_foundation.py, tools/verify_geometry.py, tools/verify_native_geometry.py, tools/verify_world_positions.py, tools/verify_native_world_positions.py, tools/verify_workspace.py. Compile the foundation before reference comparisons. For client checks, run tools/verify_native_datasync.py --build-only and tools/verify_datasync_client.py --world-positions. All native test worlds/JARs stay in disposable .toolchain directories.

The expanded compiler run reports 8,336 diagnostics with a requested limit of 30,000, then exits abnormally with code 4. This is not an exhaustive count or completion percentage. Native registries, specialized block/state codecs, entity/item/client APIs, persistence integrations and mixins remain required for a playable complete build.
