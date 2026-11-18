# Release verification - 2026-11-09

Release SHA-256: `46d8cc407e735a72a7784848e01f4a6cce4381864726313a8e1e03bf7e55d4a9`.

## Automated core tests

18 tests passed: exact progression boundaries, activity/time scaling, randomized quiet periods, recent-event memory, warmup exclusion of creatures/scares, configuration/context event gates, distinct creature policies, gaze/caution, reveal/chase/retreat behavior, chase bounds, scare probability and cooldown semantics, and strict bounded effect packet decoding.

## Actual Minecraft checks

- Java 8u51, Minecraft 1.7.10, Forge 10.13.4.1614.
- A full current-modpack integrated client passed: seven rendered/registered entities, 30 forced event operations in suitable fixture terrain, real torch/window/animal/terrain effects, conditional saved undo, container/item protection, subsequent-player-edit preservation, region encounter limits, actual hunting navigation, Watcher stillness under gaze and movement after looking away, client effect receipt/expiry, actual death/respawn and full world reload persistence.
- A separate production dedicated server and two real Forge clients passed the same checks, including independent per-player progression and synchronized models/effects. The tested production bytes match in all four fixture installations.
- Runtime screenshots are in `build/client-safe-final/screenshots/` and `build/dedicated-safe-final/screenshots/`. The first model review used an independent renderer of the actual meshes/UV textures; final checks use Minecraft's legacy renderer.
- Navigation uses local waypoints with a loaded-area guard around vanilla ChunkCache, and gaze ray tests also reject unloaded areas. A far-away missing-chunk navigation request was tested without increasing the server's loaded chunk count.
- 38 original Vorbis files decode successfully; model UV bounds, parent references, skin byte identity and Java 8 bytecode were audited. The release contains no harness/Minecraft classes.

## Scope and limitations

The tests use accelerated state setup and prepared disposable terrain; they are not a multi-hour natural survival/balance playtest. The ten-minute grace/progression boundaries and random selection were checked directly, not by waiting forty real minutes in every world. Audio files and references were validated; subjective listening quality and physical Android/Zalith behavior have not been validated. Test client master audio was muted. Legacy Forge/OptiFine version checks, OpenAL initialization and reconnects can emit upstream warnings in the minimal two-client fixture; both clients still completed and exited. No permanent screen effect or mod crash occurred in the final tests.

No main save was opened. Optional block changes default off. User-supplied Herobrine and replacement standard-player Shadow skins are preserved unchanged; Watcher is 4 blocks tall and Pale Man 3.5. See ASSET_CREDITS.md.

Raw evidence: `build/client-safe-final/RESULT.json`, `build/dedicated-safe-final/RESULT.json`, corresponding console logs and `build/asset-audit.json`.
