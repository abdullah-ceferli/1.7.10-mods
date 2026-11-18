# Block state compatibility — 2026-11-14

The shared property/state graph and metadata bridge compile against real Forge 1.7.10. They are connected to the recovered SRP source. This is a tested part of an unfinished full port; it is not a runnable SRP release.

## Implemented behavior

- Boolean, integer, enum and direction properties retain allowed-value order, serialized names, parsing, range validation and duplicate enum-name checks. Integer property iteration follows the original HashSet ordering, including ranges that wrap hash buckets.
- BlockStateContainer creates immutable canonical states from the sorted property Cartesian product. Changing or cycling a property selects an existing state; invalid values fail explicitly. Duplicate property names follow the original last-entry behavior.
- IBlockState retains the original referenced API contract. All 210 originally used descriptors across the shared foundation now link against the real 1.7 compatibility code.
- StatefulBlock extends the actual 1.7 Block. BlockBase now inherits it. Native hooks decode metadata for additions, ticks, breaking, player removal, light, collision, side solidity, drops, picking, entity contact, harvesting and tile creation. Breaking uses the old metadata supplied by the engine, even after the world block has changed.
- BlockStates reads/writes native chunks through block IDs and four-bit metadata. Unknown codecs and values outside 0..15 fail explicitly. State IDs retain the original low 12-bit block ID plus metadata shifted by 12.
- A Java syntax-tree migration routed 1,740 calls in 222 original source units to these boundaries. Counts: 684 reads, 307 writes, 625 default-state requests, 70 metadata encodes, 33 decodes, 20 state-ID encodes and one decode. Nested calls, argument evaluation order, comments and string literals are preserved. Source checkpoints and the per-call audit are retained.

## Validation

`tools/verify_foundation.py` compiles the compatibility code against Forge 1.7.10 and checks original member descriptors and the existing coordinate/vector/direction behavior. `tools/verify_states.py` then compares properties and a complete 32-state graph directly with the official Minecraft 1.12.2 JVM implementations. Reference Minecraft and Guava archives are hash-verified and used only by tests.

`tools/verify_native_states.py` builds a separate test-only mod, reobfuscates it, and launches genuine Forge 1.7.10 dedicated servers in a fresh disposable world. It validates every metadata value, native callbacks, drops/picking, collision offsets, light/solidity, tile NBT, the converted-call fixture and a genuine second-JVM world reload. It never installs a mod or opens the user's world. Generated test JARs stay under `.toolchain/block-tests`, outside the release artifact directory.

Evidence is in `docs/test-results/2026-11-14-block-states`. Across this work and the previously verified unchanged rendering/sound code, there are 40 passing groups. The full source compilation still fails at its 2,000-diagnostic reporting cap; that cap is not the number of errors remaining.

## Required remaining work

- Native vanilla blocks currently retain raw metadata without invented 1.12 properties. Their default-state differences, named variants, derived shapes, rotation and mirroring need explicit codecs. Unimplemented derived-state/rotation operations report that requirement instead of silently leaving directional metadata unchanged. Ordinary raw shape/material queries use native 1.7 behavior and are not a complete 1.12 vanilla-state emulation.
- Specialized SRP block parents (stairs, slabs, fences, falling blocks, plants, doors and other native subclasses) still need state-aware inheritance and their original behavior connected to native hooks. Only the common BlockBase hierarchy is connected so far.
- Neighbor notifications need the actual source position. The 1.7 callback does not supply it; passing the receiving position would be incorrect. This bridge is deliberately still pending.
- Read-only IBlockAccess collision/drop queries need additional native adapters. Current legacy collision/drop defaults require a real World because that is the native 1.7 callback signature.
- Registry events, modern sounds/packets, ItemStack/hand rules, tile/network APIs, entity data synchronization, capabilities/bestiary persistence, advancements, biome/world generation, client rendering/assets and the coremod/mixins still require conversion and full-game validation.

No playable 1.7.10 SRP JAR has been produced or installed. Original art/audio and all 1,094 recovered source units remain preserved.
