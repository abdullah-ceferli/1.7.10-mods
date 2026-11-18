# Native particle bridge — 2026-11-15

Implemented directly in the project, including effects absent from 1.7.10, without dropping original source/content. The vanilla particle compatibility boundary passes original-bytecode, dedicated-server and real-client checks. The complete mod still does not compile.

## Catalog and source integration

All 49 original EnumParticleTypes names, IDs, argument counts and ignore-range flags match the original 1.12.2 bytecode. The catalog keeps saved-name/ID lookup, including EntityToxicCloud's dynamically restored particle selection.

A syntax-tree rewrite routes 119 typed spawnParticle calls and five spawnAlwaysVisibleParticle calls through ParticleAccess. There are 124 converted call sites in 54 calling files; 81 original source units change particle imports/calls. Source receivers and argument expressions remain in their original order and are evaluated once. Native string calls and SRPEnumParticle custom-particle calls remain separate. Both post-migration audits are empty. See particle-conversion.json and the per-call TSV files.

World's local overload remains local; the distinct WorldServer batch overload sends packets. The always-visible integer-ID overload has its own original minimal-settings sampling and range behavior. It does not inherit the enum's force flag. This distinction matters for toxic clouds.

SRPMain initializes a separate Forge channel. Server initialization links no client renderer classes. The Netty handler only queues decoded messages; ClientTick END performs actual particle creation on the client thread. Dimension checks discard stale cross-dimension messages. Packet failures stop/log that batch as in the original handler. Existing typed entity-data networking and potion/armor hooks are preserved.

## Protocol, math and filters

Server packet values retain original float rounding. Count zero emits one directed particle with float-by-float offset/speed products before widening to double. Positive counts consume six Gaussian samples per particle in original order. Required block/item arguments are kept and unused trailing arguments are ignored. Legacy block names split the low 12 block-ID bits and high metadata bits rather than treating the packed state ID as an old block ID.

Recipient selection uses the existing verified entity-position helper and the original inclusive radii: 32 blocks normally and 512 with the long-distance flag. Client normal range is 32 blocks. Original force flags bypass range and particle-setting suppression. The minimal-setting integer overload retains its original nextInt(10), then nextInt(3), decisions.

A narrow RenderGlobal transformer scopes the native factory's range/settings bypass to calls already checked by the modern dispatcher. Its ordinary calls retain the original 1.7 filters. Scoped nesting restores the previous state; target-shape changes fail explicitly. Actual MCP/SRG bytecode and real-client ordinary-call regressions pass.

## Actual effects and resources

All 48 factories registered by original ParticleManager create actual effects through the native client. ITEM_TAKE (ID 40) remains intentionally unregistered, matching the original rather than inventing an effect.

Native-supported effects reuse 1.7 EntityFX constructors and rendering. Ten missing effects have native implementations following recovered original behavior: barrier, end rod, dragon breath, damage indicator, sweep attack, water wake, totem, spit, falling dust and elder-guardian appearance. Water drops use the existing native rain effect. The elder appearance ports original guardian model geometry, extended spines, tail pose and camera-facing presentation without requiring a nonexistent 1.7 guardian entity.

Four textures are copied byte-for-byte from the verified local official 1.12.2 client archive into a distinct backport namespace: particle atlas, barrier, sweep and elder guardian. Original SRP assets are unchanged. Source/archive/resource hashes are in particle-texture-provenance.json. Each resource is also loaded through Minecraft's resource manager and hash-checked in the native client.

New effects render in their own native lit-particle layer with explicit texture/UV/alpha handling and scoped GL state. Falling dust retains its billboard rotation. Native block metadata/colors and target-engine particle behavior remain the backend for legacy effects. Reused 1.7 effects have not been re-simulated against 1.12 tick by tick, and the pixel checks do not claim pixel-perfect cross-version output.

## Verification

18 distinct passing boundaries are recorded:

- Two original-bytecode comparisons: every catalog entry, and original packet expansion in 2,450 cases. The comparison executes the original handler bytecode with only thread scheduling and the world output sink redirected. Original packet getters, branches, float arithmetic, Gaussian order and error blocks are retained; this is not a second handwritten expansion formula.
- Three actual MCP/SRG factory-transformer checks: exact/idempotent hooks, changed-signature rejection, nested context restoration.
- Five dedicated Forge checks: no client linkage, real Netty codec round-trips, invalid/trailing payload rejection, actual native block/item argument naming, and inclusive range/negative-position behavior.
- Eight real client/integrated-server checks: all 26 fixed-call-site types; all 48 restored-name factories; native filter preservation; always-visible/minimal-setting RNG; ten nonblank rendered effects with source textures and selected GL-state restoration; animation/movement/lifetimes; actual count-zero/positive-count server delivery on the client thread; and actual long-distance delivery under minimal settings.

The render-readback gallery is particle-visual.png. It samples each effect at a visible phase; the appearance-model capture includes the camera-height transform the effect expects. The first five-effect client run passed before the additional saved-name/always-visible paths were found and completed. Final evidence corresponds to the full source snapshot, not that earlier subset.

Commands:

    python tools/verify_native_particles.py
    python tools/verify_particles_client.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory
    python tools/write_remaining_errors.py

## Current limits

The probe captures 6,448 diagnostics, down from 6,683 before this stage (and 6,827 before armor). The exact entity package has 2,099 and EntityParasiteBase 92. The complete 61-unit helper package compiles and has zero diagnostics. There are 1,155 main source units, including all 1,094 recovered originals. All 4,501 original SRP assets remain unchanged. Abnormal javac exit 4 prevents an exhaustive inventory or completion percentage.

This does not finish original SRP custom particle classes, entity AI/navigation/equipment, sound categories/packets, original mobs, rendering, specialized blocks/world generation, persistence, HUD visibility, potion items/types or brewing. Recovered-source narrowing issues such as EntityToxicCloud's byte-valued color getter remain separate; its upstream method really returns byte, so the particle port did not silently change it to int. No full playable mod JAR has been built or installed. Tests use disposable worlds and modules; user mods/saves are untouched.

Source-matched review: review/particles-2026-11-15.zip. Complete captured errors: ALL_REMAINING_ERRORS.md.
