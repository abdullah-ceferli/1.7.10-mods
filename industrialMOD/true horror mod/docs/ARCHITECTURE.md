# True Horror

Standalone Forge 1.7.10 / Java 8 mod; no extra mod dependencies.

- `core`: deterministic progression, weighted contextual event catalogue and creature state decisions; unit tested without a running game.
- `server`: persisted player progress, one-second director updates, region encounter limits, bounded loaded-chunk spawn searches, timed/reversible optional block edits and operator commands.
- `entity`: seven registered monsters, shared locomotion/lifetime/ownership machinery and distinct behavior policies. Navigation is recomputed at most twice a second; gaze tests four times a second. No teleport pursuit.
- `network`: bounded, server-to-client effect messages. The Netty handler only enqueues data; the client applies it on its own tick.
- `client`: original UV-mapped ModelRenderer meshes, per-part animation, emissive eyes and short-lived audiovisual effects. Rendering uses legacy Forge APIs.
- `tools/author_assets.py`: deliberately constructed mesh descriptors and pixel art, using named parts, fixed palettes, face-specific shading and facial details. No random pixel noise or stock mob recolors.
- `tools/author_audio.py`: original layered positional sound designs with envelopes, filtered textures, vocal formants and echo tails. Vorbis files are bundled and audited against sounds.json.
- `testMod` and integration runners: disposable worlds and a dedicated server; never shipped in the release JAR.

Default progression: ten-minute major-encounter grace period, then 10/20/40 minute stages. Active play time, event and scare cooldowns persist per player across death and reload. Safety spacing is shared between nearby players. Player inventory is never edited; world block changes default off. Optional terrain/sign/door events use bounded saved undo records and only restore blocks if the event's result is still unchanged.

Release work checklist is maintained in PROJECT_HANDOFF.md. Features are marked verified only after compilation and relevant runtime/asset checks.


Navigation guard: legacy World.getEntityPathToXYZ constructs a ChunkCache with radius searchRange+8 and otherwise requests chunks even when absent. The mod uses a12-block search range, <=10-block local waypoints and a loaded-rectangle check before each path request. Gaze rays and spawn/block-update envelopes are guarded as well. The far-away no-load invariant is checked on the running dedicated server.
