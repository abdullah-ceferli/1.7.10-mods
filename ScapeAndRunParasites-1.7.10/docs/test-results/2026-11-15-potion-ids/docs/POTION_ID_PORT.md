# Native spawn closure and potion ID groundwork — 2026-11-15

The three native EntityLiving spawn calls are converted and tested. The next potion family is partially implemented: fixed native IDs, table capacity, resource-name lookup, the native constructor/attribute boundary and mappings for existing vanilla effects. Particle visibility, newer effects and potion types/brewing are still unported. This is not a completed potion family or a playable mod.

## Completed spawn boundary

DifficultyAccess.initialSpawn dispatches LegacyEntityMob through its converted onInitialSpawn override, retaining the supplied difficulty and group data. A native EntityLiving outside that hierarchy uses onSpawnWithEgg. EntityRof and both EntityBiomass calls now use this helper.

A real native server check exercises both branches through EntityLiving variables, verifies single dispatch and preserves group-data identity. The full compiler snapshot has no remaining onInitialSpawn missing-method diagnostics.

## Effect ID and constructor groundwork

All 37 custom effect definitions are retained. NativePotionRegistry declares an explicit name-to-ID table, with fixed defaults 64–100. Defaults are data, not an incrementing construction-order counter. The generated file is config/srparasites-potion-ids.cfg; client/server configurations must agree. Configured IDs are restricted to 24–127, duplicates are rejected, and an occupied native slot causes an explicit error rather than silent renumbering.

The existing access-transformer pipeline now changes seven fields in four classes. Only Potion.potionTypes loses final. The native table is expanded to at least 128 entries before SRP effect construction; existing entries are copied and larger tables are not shrunk. The shared Forge cache is unchanged. Actual FML transformation and native startup verify this rule.

NativePotionBase calls the real Potion(int id, boolean bad, int color) constructor, which populates the native table. SRPEffectBase now extends this boundary. Name-to-effect and identity-to-name maps replace the custom registry-name dependency. Existing vanilla names are mapped by their verified native identities. Unknown external effects do not acquire invented names.

WorldSavedData records name-to-ID assignments under data/srp_native_potion_ids.dat. On world load, saved assignments must match the configured values. New names can be appended, but an old name cannot silently acquire a different ID or disappear. This is a guard against reinterpreting existing saves, not an automatic numeric-ID migration.

Four existing attribute registrations used UUID.randomUUID on every launch. They now use stable UUIDs derived from UTF-8 canonical effect name plus attribute name. NativePotionBase bridges registerPotionAttributeModifier to the real native attribute method. Native checks verify application, removal and removal after a second-JVM NBT reload.

The source migration also replaces 134 MobEffects references in 64 files with real existing native Potion fields. GLOWING and LEVITATION remain explicit unresolved references; they were not mapped to unrelated effects.

## Native API findings

- The default native table has 32 entries and vanilla effects occupy IDs 1–23. All 37 SRP definitions therefore need additional capacity.
- PotionEffect writes Id as a byte, and native NBT reconstruction reads a signed byte and rejects negative IDs. The native effect packet also exposes a signed-byte ID to its client handler. Keeping SRP IDs at or below 127 avoids that loss.
- The test deliberately configures one fixture effect to ID 127 and exercises native effect NBT and actual packet write/read/re-encoding.
- Packet getter methods used by the native client are stripped from the dedicated server. Tests inspect the actual packet wire fields instead of calling those client-only getters on the server.
- Forge 1.7.10 already has renderInventoryEffect(int,int,PotionEffect,Minecraft), matching the existing SRP method signature. The HUD hook and its client integration remain separate work.

## Verification and its scope

Eleven PASS records were produced across initial, restart and occupied-ID native Forge runs, covering seven distinct boundaries:

1. The 37 effect names extracted from SRPPotions match the configured catalog, with fixed unique IDs, correct native slots/name lookups and unchanged vanilla identities.
2. Invalid ranges, duplicate IDs and changed saved-world bindings are rejected.
3. Native attribute modifiers apply and remove the expected value.
4. Native effect NBT and effect-packet serialization preserve every configured ID, including 127.
5. Native entity effect NBT and the ID map are written for a restart.
6. A genuine second JVM reloads the native entity effects with reversed construction order; the saved modifier can still be removed.
7. An occupied configured ID is rejected before table expansion or SRP construction.

These ID tests construct native fixture effects using the same 37 source names. They do not establish the gameplay of all original SRP effect subclasses, actual client rendering or a client/server configuration handshake. The complete 41-unit backport helper package compiles for these native tests.

The five difficulty/spawn checks pass. Original archive hashes, all 4,501 original assets and all 1,094 original source units are preserved. No installed mods or user saves were changed.

Commands:

    python tools/prepare_compile_access.py
    python tools/verify_native_difficulty.py
    python tools/verify_native_potions.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory

## Remaining potion work

Five-argument PotionEffect calls were intentionally not rewritten to a four-argument constructor. A subclass or side flag alone would not survive native effect combination, the engine's static NBT reconstruction and client packet reconstruction. Those paths need an explicit tested implementation before the original false show-particles arguments can be migrated safely.

All 33 PotionType definitions and the original brewing recipes remain present. Potion items will be preserved. Native bottle/splash/lingering behavior, the actual brewing-stand integration and missing modern ingredients need implementations; no recipe or potion item was deleted to reduce diagnostics. Glowing and levitation also need their real behaviors.

The native inventory rendering hook is available, but original effect icons/HUD still require client checks. Stable configuration exists; a remote client/server ID-handshake check is still outstanding. The full SRPPotions class remains dependent on unported PotionType/brewing APIs, so the ID groundwork does not make the original mod loadable.

## Compiler state

The full probe reports 7,056 captured diagnostics, down from 7,145. The exact entity package reports 2,603 and EntityParasiteBase 113. There are zero diagnostics under the backport helper package. The original compatibility package has 102 mod-integration diagnostics; it is a different package from backport and should not be used as the helper-error count.

Javac still exits abnormally with code 4. Counts are not exhaustive, independent bug counts or completion percentages. The full mod is still unbuildable and not playable. See ALL_REMAINING_ERRORS.md for the current complete captured inventory.
