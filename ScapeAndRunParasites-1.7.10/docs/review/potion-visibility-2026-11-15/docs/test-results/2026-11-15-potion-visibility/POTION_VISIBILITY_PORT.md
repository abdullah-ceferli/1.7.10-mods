# Native potion particle visibility — 2026-11-15

The four supplied Java files are integrated. Production native hooks, persistence and real client tracker behavior pass. This completes the tested server particle-color boundary; HUD visibility and potion items/brewing remain unfinished. The complete mod still does not compile.

## Integration and changes to the submitted files

Original Downloads files are unchanged. Exact copies and SHA256 provenance are in upstream/potion-visibility-user-files-2026-11-15. The pre-integration checkpoint is D:/IndustrialTechnology/ScapeAndRunParasites-1.7.10/.toolchain/checkpoints/potion-visibility-20261115-180305.

PotionFxEvents registers Forge EntityConstructing and FML WorldTick END listeners once. Construction initializes per-entity properties before the first application; lazy registration also explicitly initializes properties for early entities. PotionVisibility persists its hidden ID set under its own namespaced tag and prunes only after native ActiveEffects restoration. Empty visible lists write color zero; watcher 8 retains the native ambient calculation over all effects. The actual gameplay effects, durations and attributes remain native.

SRPPotionEffect supports Potion and numeric-ID constructor forms, ambient state and a mutable show-particles flag. Native combine retains its existing object, so combine copies the incoming visibility even if its strength or duration is weaker. Its copy constructor explicitly preserves ambient/show flags. Entity hidden state remains authoritative when the existing effect is a native base object or was reconstructed by native NBT.

PotionFxLoadingPlugin installs PotionFxTransformer after deobfuscation (sorting index 1001). The build manifest declares the plugin and the existing access-transformer file. SRPMain.preInit registers the listeners. The preserved original mixin bootstrap is not repurposed or claimed complete.

## Hook placement and native findings

EntityLivingBase.addPotionEffect has a shared final return for accepted and rejected applications. Hooking that return would incorrectly accept rejected effects. The transformer inserts two applied sites: after native map insertion/combination, immediately before each onNew/onChanged virtual callback. Exactly one site executes for an accepted application and neither executes after rejection. Before-callback placement lets a nested newer application win. The finish hook runs at the beginning of native onFinishedPotionEffect, covering removal, clearing, expiry and milk cure. Native player overrides delegate through the base callback.

Actual native NBT restoration writes base PotionEffect objects directly into the map; it does not call addPotionEffect. No NBT-read suppression is needed. Extended-property load runs before ActiveEffects restoration, so loading must not prune the hidden set.

Server WorldTickEvent.END occurs after entity updates and before EntityTracker broadcasts. Recoloring there survives native metadata updates. The client receives watcher 7 through ordinary native packets. Ongoing effect-removal packets for native players target their owner; mobs receive their current effect list when tracking begins. The first client fixture incorrectly waited for a mob removal packet and timed out. Its correction checks real server removal and client metadata separately; production code did not change. The failed fixture log is retained with the evidence.

The transformer supports actual MCP and SRG method names, preserves stack frames and computes maximum stack depth only. Structural tests prove idempotence and explicit failure on unexpected or partial target shapes. It does not claim compatibility with every third-party coremod.

## Source migration

The javac syntax-tree rewrite changed 294 constructor expressions across 102 files: 217 five-argument, 42 three-argument and 35 two-argument calls. Five-argument visibility values are 165 false, 51 true and 1 computed value. The earlier 164-call ZIP was a subset. All arguments are retained. Native PotionEffect variable types and callback signatures remain native. The post-migration constructor audit is empty; strings and comments were not rewritten. See potion-effect-calls.tsv and potion-effect-conversion.json.

## Verification

All 47 backport source units compile against the actual access-transformed Forge 1.7.10 API. A reobfuscated test module loads the production plugin in actual Forge, without manually simulating the hooks. There are 27 PASS records across 19 distinct structural/server/client boundaries:

- Three structural checks against real MCP/SRG native bytecode and target-shape/idempotence safeguards.
- Nine dedicated-server checks repeated in a genuine second JVM: construction, hidden/visible/mixed amplifier-weighted colors, weaker accepted applications, rejected applications, nested callbacks, removal/expiry/clearing/milk, preserved attributes/ambient state, native NBT ordering and persisted reload. The save and restart rows are distinct, so these cover ten unique boundaries.
- Six actual client/integrated-server checks: native spawn/tracker packets, weaker plain-native reapplication, hidden reapplication, stable hidden-only zero color, tracking loss and fresh-entity tracking re-entry. These inspect separate client entities and real packets; no custom HUD packet is used.

Server and client fixture JARs contain the same 84 production helper class files byte for byte; only the client fixture expectation was corrected. Test worlds and JARs are disposable under .toolchain. No installed mod or user world was modified. Workspace verification preserves all 1,094 original source units and 4,501 original assets.

Commands:

    python tools/verify_native_potion_fx.py
    python tools/verify_potion_fx_client.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory
    python tools/write_remaining_errors.py

For client-fixture-only rebuilds, verify_native_potion_fx.py --build-only compiles/remaps a new disposable JAR without repeating the server runs.

## Remaining work and compiler state

The owning client still lacks the hidden-ID set required for original HUD visibility. A packet and client HUD implementation are deferred as requested. Custom particles emitted by individual effect logic are untouched. PotionType entries, potion items, original brewing recipes, glowing/levitation and the custom effect gameplay/rendering still need conversion and validation. Remote client/server potion-ID configuration handshake is still unverified. No complete original SRP mob has yet passed an end-to-end test.

The fresh probe captures 6,827 diagnostics, down from 7,056. The exact entity package has 2,429, EntityParasiteBase has 104, and the backport helper package has zero. All 1,141 source hashes and printed diagnostic lines match the converted sources. Abnormal javac exit 4 means this count is not exhaustive or a completion percentage. No playable full-mod JAR exists.

The matched review bundle is review/potion-visibility-2026-11-15.zip. The full line-matched inventory is ALL_REMAINING_ERRORS.md.
