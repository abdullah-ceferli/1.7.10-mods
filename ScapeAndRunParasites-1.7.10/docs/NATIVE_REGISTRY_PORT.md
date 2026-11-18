# Native block/item registration — 2026-11-15

309 original expressions across 131 source units now use pending registry names on real native blocks/items and native tile IDs. The original block and item registry event handlers now run explicitly after configuration/preInit setup: construct/register blocks, register specialized ItemBlocks, then construct/register the remaining items. All 140 explicit original item registrations remain present.

Names follow the original [Forge registry-entry contract](https://github.com/MinecraftForge/MinecraftForge/blob/1.12.x/src/main/java/net/minecraftforge/registries/IForgeRegistryEntry.java): pending names are available before native registration, registered delegates are authoritative, setters retain fluent identity, and repeated assignment is rejected. Native Forge allocates and remaps the real numeric IDs.

Block registration creates no automatic item. Original custom ItemBlock instances are registered later, preserving metadata, slabs and other specialized item behavior. Explicit foreign namespaces use the same native GameData path while bypassing only 1.7's active-mod prefix conversion. Native client tests verify those names survive the registry handshake.

The source audit compares 132 checkpointed files and preserves every original literal and construction sequence. Unsupported entity/biome/potion registry events remain visible as unfinished code; no substitute entries are supplied for them.

254 provider dimension getters across 84 files now read the equivalent native public dimensionId field. The original [Forge provider getter](https://github.com/MinecraftForge/MinecraftForge/blob/1.12.x/patches/minecraft/net/minecraft/world/WorldProvider.java.patch) returns that stored ID; source evaluation order is preserved by direct field access.

## Verification

Five initial dedicated-server groups and five second-JVM groups cover pending/native/vanilla names, fluent/inherited source rewrites, custom item identity, shared block/item IDs, namespaced registration, duplicate rejection, all 16 item metadata variants, native stack NBT, block drops and world/tile persistence.

Two actual production-client groups cover the native client/server registry handshake and an actual right-click packet placing the custom metadata block on the integrated server and synchronizing it back.

Evidence: docs/test-results/2026-11-15-native-registries/manifest.json. Run tools/verify_native_registries.py for a fresh server/restart test; use --build-only and then tools/verify_registry_client.py for client validation.

The aggregate is 89 verification groups. There are 36 compatibility source units and 1,130 main Java units, with all 1,094 original units and 4,501 assets preserved.

The complete mod still fails compilation. These isolated test modules do not establish that every original SRP block/item constructor or model works. Entity/biome/potion registries, specialized blocks, remaining entity/item/client APIs, persistence integrations and mixins still need porting. No playable complete JAR has been produced or installed.
