# Port status

## Completed preparation

- Separate project folder created as requested, outside Industrial Technology's source tree.
- Latest official release verified as SRP 1.10.9 alpha, published 2026-09-01.
- Official 116,798,138-byte archive downloaded from CurseForge CDN and checked against Modrinth's published SHA512.
- 1,596 original mod classes, 1,094 recovered Java source units (315,276 lines), and 4,501 original asset files inventoried.
- Public GitHub link checked: it is an issue tracker and contains no mod source project. No GitHub application installation is needed.

## Implementation requirements

1. Replace 1.12 registry events/entity builders, spawn eggs, sound events and biome registration with Forge 1.7 equivalents while preserving the original content IDs.
2. Convert block states/properties/positions, new biome/world generation, structures, celestial rendering and post-processing to 1.7's block metadata and rendering APIs.
3. Complete remaining entity attributes, AI/path navigation, damage and evolution/infestation integration. Typed entity metadata now uses a transport separate from the limited native DataWatcher namespace.
4. Convert capabilities, modern ItemStack/hand/equipment rules, potion effects, recipes and advancement/bestiary persistence to 1.7 counterparts.
5. Adapt networking and all three Minecraft mixins. Do not silence missing systems with no-op shims or remove content to make a build pass.
6. Compile, reobfuscate and audit the complete JAR; verify fresh dedicated servers, genuine restarts, original client models/audio, and multiplayer in disposable test worlds before installing anything.

The source conversion and compiler diagnostics are development material. A changed version label or successful empty mod bootstrap would not count as completing this port.

## Initial compiler result

The automatic name conversion replaced 113,167 shared SRG identifiers, preserving Java strings and original assets. This is only an initial source migration. A real javac probe against Forge 1.7.10 inspected all 1,094 Java source units and failed, reaching its 2,000-error reporting limit. A separate class-reference audit found 236 absent platform types among 653 referenced Minecraft/Forge types, after known package relocations. Existing type names can also have incompatible member signatures.

The most widely used missing types are BlockPos (411 mod classes), IBlockState (255), SoundEvent (203), IRenderFactory (159), GlStateManager (150), and block property types (112). EntityDataManager/DataParameter occur in 82 mod classes. These need real behavior-preserving implementations/conversions; dummy classes would conceal incomplete functionality.

Verification passed for the official SHA256/SHA512, all 4,501 original asset hashes, all 1,094 recovered source units, and absence of original 1.12 mod classes in the extracted third-party compile library. No JAR was produced or installed. The original local archive remains a 1.12.2 mod and must not be mistaken for a backport.

## Shared compatibility implementation — 2026-11-14

Implemented ten real compatibility source units: integer/vector coordinates, mutable block positions, original coordinate serialization, ray-hit conversion to the native 1.7 object, directions/axes/planes, rotations/mirrors, non-null list semantics, serialized enum names and the fixed-function rendering adapter. The original source now imports those classes. An additional 17,435 remaining SRG identifiers were normalized with official 1.12 MCP mappings, preserving strings/comments. Original sources are checkpointed in `.toolchain/checkpoints`.

The foundation was compiled against actual Forge 1.7.10 and compared with the official original Minecraft 1.12.2 implementations: 2,500 signed position-packing cases, 200 inclusive/reversed-corner box traversals, 1,200 vector cases including float-root/trig precision, mutable snapshot rules, direction ordering/rotations, collection behavior, and an actual 1.7 ray-hit boundary. All 173 originally referenced math/render-state member descriptors link to the compatibility implementation.

Real 32×32 offscreen LWJGL contexts compare original 1.12 rendering behavior with the adapter for depth/alpha/culling/lighting, transforms, colors/normals, blending, transparent models, fog, colored outlines and restoration, texture unit/binding parameters, and final state restoration. All ten comparisons pass. This validates the shared state adapter, not complete entity/model rendering or the whole client.

The original 432 assigned sound identifiers now use native 1.7 string values. Original ambient callbacks are bound to getLivingSound, death callbacks retain native string signatures, and the hurt callback bridge retains DamageSource context for existing overrides. All seven originally uninitialized sound fields remain uninitialized. The catalog, actual native sound callback signatures, and styled clickable text serialization pass four checks. Full-mod gameplay for these callback changes is not yet available. Five original sound IDs absent from the unchanged upstream sounds.json are recorded separately; no replacement IDs or audio were invented.

Evidence: `docs/test-results/2026-11-14-compatibility/manifest.json`. There are 20 passing verification groups. The full compile still reaches its 2,000-diagnostic limit and fails; the count is capped and is not a total remaining-error count. Leading blockers include block states/properties, modern registries, entity synchronization, capabilities and advancements. No playable JAR has been produced or installed.

Commands: `tools/verify_foundation.py`, `tools/verify_rendering.py`, `tools/verify_sounds.py`, `tools/compile_probe.py`, `tools/verify_workspace.py`. Rendering verification uses real offscreen contexts; no user world is opened. Recompile the foundation before running rendering checks after future helper changes.

## Property/state graph and native metadata bridge — 2026-11-14

Implemented eleven additional compatibility source units for six property types, canonical block states, shape categories, a state-aware native block base and explicit metadata boundaries. The project now contains 1,115 Java source units: all 1,094 recovered originals plus 21 compatibility source units. BlockBase inherits the real native bridge. Java syntax-tree edits connect 1,740 original calls across 222 source units; checkpoints and a per-call audit are preserved.

Original-reference property/state comparisons pass, including a complete 32-state transition graph and an integer-order edge case caught and fixed by comparison. All 210 referenced foundation descriptors link. Real reobfuscated Forge 1.7.10 server checks pass for all 16 metadata values, light/side solidity, collision, callbacks, drops/picking, state IDs, generated nested-call evaluation, tile creation/NBT and a genuine second-JVM world reload.

There are now 40 passing groups across the compatibility reports. The complete mod still fails compilation at the 2,000-diagnostic reporting limit. This is not a total remaining-error count. No playable release has been built or installed. The block-state work remains incomplete for native vanilla codecs, specialized block inheritance and neighbor source positions; those limitations and the next integration work are listed in `docs/BLOCK_STATE_PORT.md`.

Evidence: `docs/test-results/2026-11-14-block-states/manifest.json`. Run the foundation checks before original-reference state comparisons; the native-state command compiles and reobfuscates its own test-only module. No user world is used.


## Current integration — 2026-11-14

Entity synchronization is implemented and verified with 21 additional groups, including actual client spawning, deltas, resync and tracking loss/re-entry. Geometry and world queries add 16 groups. The aggregate is now 77, with 34 compatibility source units, 1,128 main Java units, and original sources/assets preserved.

The latest work routes 599 geometry expressions in 181 files and 331 world-position expressions in 134 files. Original 1.12 bytecode comparisons and native Forge tests cover immutable queries versus mutable engine collisions, tile coordinates, bottom-layer terrain scans, scheduled ticks and client/server loading differences.

The full build remains unsuccessful: the expanded run reports 8,336 diagnostics with a 30,000 requested limit, then exits abnormally with javac code 4. This is not an exhaustive inventory or completion percentage. No playable JAR has been built or installed. Native registries, specialized/vanilla state codecs, entity/item/rendering APIs, persistence and mixins remain.

Current detail: docs/GEOMETRY_WORLD_PORT.md, docs/ENTITY_SYNC_PORT.md, docs/port-status.json and docs/test-results/2026-11-14-geometry-world/manifest.json. Earlier sections/evidence are historical stage snapshots.


## Native registration integration — 2026-11-15

Converted 309 registry expressions in 131 original files and retained all 140 explicit item registrations. Blocks/items now register through an explicit native lifecycle. Twelve new groups pass across dedicated servers, a real JVM restart and an actual client/server handshake plus metadata block placement. There are now 89 aggregate groups, 36 compatibility units and 1,130 main Java units. Another 254 provider dimension getter calls now use the native field.

The complete mod remains unbuildable: the expanded probe reports 7,869 diagnostics and exits abnormally with code 4. Entity behavior, client rendering and specialized blocks are the largest remaining reported areas. See docs/MAJOR_BLOCKERS.md and docs/NATIVE_REGISTRY_PORT.md. No playable complete JAR has been built or installed.


## Shared entity API review — 2026-11-15

Captured full compiler symbol/location/overload details. Six runtime/compiler access rules, 273 exact block-position calls, 196 typed queries and 69 live-list reads now pass native/reference checks. Repaired thirteen verified Integer boxing syntax errors. There are 95 aggregate groups plus two parser regression checks. The full probe still fails with 7,341 reported diagnostics (2,864 entity; 124 base entity), and abnormal compiler termination prevents an exhaustive count. Details: docs/ENTITY_REVIEW.md.


## Local difficulty and native spawning — 2026-11-15

Verified the four requested native API names. Converted 79 imports, 44 local difficulty queries and 21 global getters in 108 files, preserving original local difficulty inputs and reusing existing position/loading helpers. The shared native spawn callback now dispatches converted onInitialSpawn overrides once.

The calculation matches original 1.12 bytecode across 23,200 cases; four native-server integration groups pass. There are now 100 aggregate compatibility/native groups plus 3 parser/source-snapshot tests, 39 compatibility units and 1,133 main Java units. All original assets/source units remain preserved.

The complete mod still fails: 7,145 captured diagnostics, including 2,676 entity and 117 base-entity messages; javac code 4 prevents an exhaustive count. Three EntityLiving-typed spawn calls remain separate integration work. See docs/DIFFICULTY_PORT.md and docs/ALL_REMAINING_ERRORS.md. Earlier sections are historical stage snapshots.


## Spawn closure and native potion ID groundwork — 2026-11-15

The three EntityLiving-typed spawn calls now use a tested native/converted dispatch helper. Potion groundwork adds explicit configurable defaults for 37 effect IDs, native table expansion, per-world name/ID checks, native constructor/name/attribute boundaries, four stable modifier UUIDs and 134 existing-vanilla effect mappings.

Native tests cover packet/NBT serialization through ID 127, a real second-JVM reload, stable modifier removal and occupied-ID rejection. The helper package contains 41 units; all 1,094 original source units and 4,501 assets remain retained. There are 108 aggregate distinct verification boundaries plus 3 parser/source-snapshot checks; potion runs produced 11 PASS records across seven distinct boundaries.

This does not complete potion visibility, newer effects, client rendering, potion types/items or brewing. No original recipes/content were cut. The full mod still fails: 7,056 captured diagnostics, 2,603 in the exact entity package and 113 in the base entity; abnormal exit 4 prevents an exhaustive count. See docs/POTION_ID_PORT.md.


## Native potion particle visibility — 2026-11-15

Integrated the four supplied files, registered both event buses and added production coremod hooks to accepted native effect applications and the finish path. Preserved visibility through native combination and NBT restoration. Converted 294 constructor calls across 102 original files, including all 217 five-argument applications.

Actual native checks pass: three structural, nine server checks repeated after a genuine second-JVM restart, and six client/tracker checks. This is 27 PASS records across 19 distinct new boundaries, bringing the aggregate to 127 plus three parser/source-snapshot checks. A client fixture packet expectation was corrected; its earlier failed run is retained in evidence. Production helper bytecode matches between server and client fixtures.

There are 47 backport source units and 1,141 main Java units. The full probe still fails: 6,827 captured diagnostics, 2,429 exact entity and 104 base entity, with abnormal exit 4 and no exhaustive count. HUD visibility, potion items/types/brewing, missing effects and wider entity/rendering/block integration remain. No playable full mod has been produced or installed. See docs/POTION_VISIBILITY_PORT.md.


## Native armor/toughness — 2026-11-15

Integrated supplied ArmorMath and ModernAttributes, native early registration, equipment modifiers and the LegacyEntityMob damage boundary. Converted 149 references across 128 files. Original-bytecode, native server/restart and real client tests produce 31 PASS records across 21 distinct new boundaries, for 148 aggregate boundaries plus three parser/source-snapshot checks. Native player ISpecialArmor remains active. See ARMOR_PORT.md for policy and fixture corrections.

The complete mod remains unbuildable: 6,683 captured diagnostics, 2,285 exact entity, 98 base entity and zero helper diagnostics; abnormal exit 4 means no exhaustive count. There are 51 helpers and 1,145 main source units. All original source units/assets remain preserved. No full playable JAR was produced or installed.


## Vanilla particle bridge — 2026-11-15

Implemented the original catalog, local/server/always-visible overloads, packet expansion, queued Forge transport, scoped native factory bridge and ten absent effects directly. Converted 124 calls in 54 calling files, with particle import/call changes across 81 files. Four original Minecraft textures are separately hashed. All 48 original factories, including dynamically saved toxic-cloud names, work in the native client; original ITEM_TAKE has no factory.

Two original-bytecode, three transformer, five dedicated-server and eight real-client boundaries pass. The aggregate is 166 plus three parser checks. All 61 helpers compile. The full mod remains unbuildable with 6,448 captured diagnostics, 2,099 exact entity and 92 base entity; abnormal exit 4 prevents an exhaustive count. All original source/assets are retained; no user mods or saves changed. See PARTICLE_PORT.md.
