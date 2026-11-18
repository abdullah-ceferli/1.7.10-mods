# Entity tracked-data port — 2026-11-14

The modern tracked fields now have a real Forge 1.7.10 transport. This is one implemented subsystem of the unfinished complete SRP backport.

## Integration

Routed 656 field/getter expressions in 82 original source files through a typed manager. Boolean, byte, integer, float and BlockPos serializers cover all original SRP uses. Inherited keys have a separate namespace from the native DataWatcher, preventing its 5-bit IDs from colliding with inherited parasite fields. An identity-based weak cache remains stable when native clients assign server entity IDs after construction.

Forge tracking starts send complete snapshots, server ticks send changed values only, tracking stops remove bindings, and client ticks apply messages on the main thread. A validated/throttled client request restores missing snapshots. Entity IDs, dimensions, class names, generation UUIDs, revisions and serializer IDs are checked. The native 1.7 protocol does not transmit ordinary entity UUIDs; the bridge binds its own server generation token instead of assuming the client-generated UUID matches.

Original callbacks are preserved through the native entity base adapters or cached public callback dispatch. Fifty-one decompiler byte-literal boxing losses were repaired using declared DataParameter<Byte> types. Other scalar types are not narrowed.

## Verification

- Original Minecraft 1.12 metadata bytes match for all five used serializers across 200 random/signed edge cases, including NaN and signed positions. Empty/dirty/clean rules, value-copy behavior and inherited IDs are compared directly with the original implementation.
- Reobfuscated native Forge tests cover 105 inherited fields, legacy watcher isolation, mutable native entity IDs, callbacks, schema errors, genuine Forge custom-payload encoding, pre-spawn snapshots, delta ordering, main-thread receipt, resync throttling, and NBT/chunk persistence across a genuine second JVM.
- A real production Minecraft 1.7.10 client and integrated server verify distinct server/client entities, initial spawn state, later updates, main-thread callbacks, actual client-to-server resync, loss of tracking and a fresh client entity with changes made while untracked.

The first live tracking fixture moved the player far enough to unload the server actor's chunk. The final fixture keeps the actor loaded while leaving its tracking range; all five live checks pass. Test worlds, logs and JARs are confined to .toolchain/entity-data-tests and .toolchain/entity-data-client-tests. The existing game installation and saved worlds are not used.

Evidence: docs/test-results/2026-11-14-entity-sync/manifest.json. Commands: tools/verify_foundation.py, tools/verify_datasync.py, tools/verify_native_datasync.py, tools/verify_datasync_client.py. The native build command supports --build-only for a changed client fixture.

The full SRP mod still does not compile. Most original entity behavior depends on additional 1.12 APIs, and registries, specialized blocks, items, rendering/world code, persistence integrations and mixins remain unfinished. No playable full-mod JAR has been produced or installed.
