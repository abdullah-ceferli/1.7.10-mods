# Native sound compatibility — 2026-11-15

The sound bridge compiles against Forge 10.13.4.1614 and passes six dedicated-server checks and twelve real client/integrated-server checks. The complete SRP port still fails compilation and is not playable.

## Implemented

- Thirteen new helper source units cover the modern categories, vanilla sound constants, material sounds, playback, packets, UI records, native callbacks and client dispatch.
- The syntax-tree migration converted 383 call expressions across the original sources. Including import/type changes, 216 original source units changed. Comments and string literals were retained. The conversion audit is sound-source-conversion.tsv.
- All 49 vanilla constants referenced by the original code and the twelve five-sound material families use their original 1.12.2 event names. The resulting 104 required events and 206 original audio clips live in the separate srpvanilla112 resource namespace. Every clip's size and SHA1 matches Mojang's original 1.12 asset index; SHA256 hashes are recorded in vanilla-sound-resources.json.
- All 432 original SRP sound definitions and 930 original audio files remain unchanged, along with the rest of the 4,501 original assets.
- Each playback category gets a separate alias pointing at the original event pool. This supports simultaneous music/hostile/player playback with independent native volume controls. Resource reloads clear the native registry and rebuild aliases against the current resource pack.
- Native Entity.playSound callbacks intercept this port's resource names; ordinary native sounds keep their original implementation. Explicit converted world calls preserve the modern originating-player exclusion rule. Native entity categories, including the hostile homing-projectile override, are preserved.
- A dedicated Forge message retains sound identity, category, coordinates quantized by truncation toward zero, and full float volume/pitch. Existing direct sound packets now use the registered FML packet. Netty handlers enqueue; playback occurs on the client tick.
- Client UI, music and spatial record factories retain their category, volume, pitch and attenuation choices. Local distance-delayed sounds use the native scheduler. The three-argument getRecord factory uses MASTER, as the original code does.
- The native audio normalizers use modern float multiplication for category aliases and keep the original double multiplication for ordinary native events.
- Native material families map to their modern equivalents; custom native sound types retain a fallback using the available native break/step/place sounds.

VOICE has no native 1.7 slider. Its optional volume is stored in srparasites-sounds.cfg; it uses MASTER with the additional gain applied when creating a record. Existing SRP call sites do not request VOICE.

## Evidence

The final disposable test module contains all current helpers and production core transformers, reobfuscated to the actual Forge runtime. Native dedicated-server checks prove initialization without client linkage, codec precision over 10,000 payload cases, malformed/trailing payload rejection, entity category selection, recipient/delay boundaries and material parameters.

Twelve actual client checks prove:

1. Resource-manager loading and exact hashes for all 206 imported clips and the registry.
2. Original weighted pools for all 104 imported events.
3. Independent aliases for all ten categories without mutating the base event.
4. Exact UI/music/record factory parameters.
5. Actual transformed normalizers over 10,000 cases, while ordinary native normalization remains unchanged.
6. Originating-player local playback and null-origin suppression.
7. Use of the native delayed-sound scheduler.
8. Real server broadcast/direct-packet delivery, client-thread playback, exclusion and recipient range.
9. Native entity callback delivery through the new network.
10. Original clips decoding and playing on actual OpenAL sources.
11. Live music volume changes affecting the music source while the hostile source retains its own volume.
12. Reload rebuilding fresh aliases, with original SRP custom audio still available.

The test harness serializes native asynchronous audio startup through SoundLoadEvent to avoid the old engine starting two OpenAL contexts during rapid startup reloads. This is fixture code, not a modification to SRP production audio. The final tests passed with Failed: 0. Full logs and source hashes are retained under test-results/2026-11-15-sounds.

Commands:

    python tools/verify_native_sounds.py
    python tools/verify_sounds_client.py
    python tools/audit_sound_assets.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory
    python tools/write_remaining_errors.py

## Remaining scope and limits

Captured full-project diagnostics fell from 6,448 to 5,997. The compiler still exits abnormally with code 4, so the inventory is not exhaustive and cannot establish a completion percentage. All 74 helper units compile, with zero helper diagnostics in the full probe. The project retains all 1,094 original source units, plus the helpers, for 1,168 main source units.

The original subtitle overlay/listener API remains unported. The sound bridge does not complete original mobs, AI/navigation, equipment/combat, rendering/custom SRP particles, state-dependent native block interactions, world generation, progression, persistence, potion HUD visibility or potion items/types/brewing. No original SRP mob has yet been validated end to end in a complete mod build.

Audio auditing found 52 file declarations referring to 51 unique files already absent from the original 1.12.2 SRP archive. See original-sound-audit.json for every event and path. These original defects were preserved explicitly; the bridge supplies no guessed replacement audio. All newly imported vanilla clips are present.

The imported events use native weighted pool selection; the current selected vanilla definitions contain no nested event references. Arbitrary resource-pack nested-event semantics and every external mod's sound behavior have not been proven equivalent to 1.12.2. The category-aware native boundary was tested; the complete original mod remains unbuilt.

Review snapshot: review/sounds-2026-11-15.zip. Full captured diagnostics and explanations: ALL_REMAINING_ERRORS.md. No release JAR was produced or installed.
