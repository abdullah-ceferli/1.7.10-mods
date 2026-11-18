# ChunkShift Engine — Minecraft Java 1.0 in Rust, vanilla only

Build the first native Rust game engine for my ChunkShift project. The target is the original **Minecraft Java Edition release 1.0 from 2011**, not Alpha 1.0, Minecraft 1.7.10, a modern release, or merely an engine whose own version number happens to be 1.0.

This is an implementation task. Inspect the existing workspace, write the engine, build and run it, and validate the result. Do not stop at an architecture proposal or generate a generic voxel demo and call it Minecraft 1.0.

## Scope and identity

- Vanilla game only. Do not implement Forge, Fabric, NeoForge, a Java/JNI mod bridge, a scripting/WASM plugin loader, or a custom game mod API. No mod compatibility is required for this stage.
- Write the game simulation, world systems, rendering integration, persistence, and gameplay in Rust. Ordinary licensed Rust libraries, native platform dependencies, and GPU shaders are fine. The playable engine must not run Java Minecraft underneath, invoke a hidden JVM, stream somebody else's game, or wrap the Java client and claim it is a Rust rewrite.
- Keep ChunkShift's existing C#/Avalonia launcher and its UI. Add a separate Rust executable/backend, preserving the existing Java launch option. Do not redesign or rewrite the launcher while working on the engine.
- Keep the target game version (`1.0`) separate from our engine version. Use development/preview engine versions until release criteria are met. Do not label incomplete work a full, compatible 1.0 recreation.
- Work only inside this project's workspace. Do not alter my installed modpack, Java installations, driver settings, or real Minecraft worlds. Use disposable worlds and copied fixtures.

## Technology and architecture

Use supported stable Rust, Cargo, and Bevy as the initial Rust framework, with custom chunk meshes and version-specific game systems. Verify current APIs against official documentation and pin the selected toolchain/dependencies with rust-toolchain.toml and Cargo.lock. Use a released Bevy version; do not pull a moving Git main branch. Internal framework modules are fine and do not imply a game mod system. Document any concrete technical reason for choosing a different Rust rendering foundation before committing to it; do not spend the task rewriting an entire graphics/windowing framework.

Start with Windows x64. Keep platform-specific code isolated. Separate world/game rules, rendering/input, storage/assets, and launcher integration sufficiently to test the simulation without opening a GPU window. Avoid a large speculative framework for future versions or mods.

The simulation has an authoritative fixed tick; verify and reproduce the target version's timing. Render independently with interpolation where appropriate. Put suitable world generation, meshing, lighting work, and disk operations on bounded worker queues. Apply results safely and deterministically. Do not run every game rule concurrently and introduce race-dependent behavior merely to show high CPU utilization.

## Establish the exact version contract first

Use the official version metadata and a lawfully available vanilla 1.0 installation as references. Record the exact target artifacts and hashes used for testing. Write an executable feature/compatibility matrix covering the actual blocks, item IDs/metadata, recipes, entities, dimensions, world format, world height, mechanics, UI, and network protocol of this release.

Distinguish observed behavior from assumptions. Do not copy modern behavior into the old version because it is easier to remember. Verify historical details before coding them. Keep version rules in an explicit vanilla_1_0 module/data set rather than scattering version checks throughout the engine.

Matching a seed requires matching the actual PRNG, noise, numeric behavior, ordering, and generation rules. Implement needed Java-compatible arithmetic/PRNG behavior in Rust and test it against reference fixtures. Do not use an unrelated terrain generator and claim identical Minecraft generation because it also has grass, trees, and caves.

Write original implementation code. Do not commit proprietary Java/decompiled source or redistribute Minecraft JARs, textures, music, sounds, or other proprietary game assets in our release. Import required assets from the user's legitimate installation or use the existing launcher's legitimate asset acquisition flow. Read the applicable terms and document asset provenance. A development fallback asset pack must be our own or appropriately licensed and clearly identified; a fallback preview is not evidence of visual parity. Do not claim that importing assets automatically establishes legal clearance for every intended distribution.

## Rendering and chunk performance

Implement actual chunk-based rendering. Never create one scene entity, mesh, and draw call per ordinary block.

- Cull hidden faces, handle opaque/cutout/translucent surfaces correctly, and batch compatible geometry. Add greedy meshing only where texture UVs, lighting, and block shapes remain correct.
- Use correct version-specific block models, textures, nearest-neighbor sampling, texture-atlas boundaries, lighting, fog, sky, sun/moon, particles, fluids, entity animation, and first-person rendering. Aim for the original game's appearance; do not introduce PBR materials or cinematic effects.
- Implement camera/frustum culling without disappearing visible chunks. Handle transparent geometry deliberately.
- Stream chunks around the player, prioritize nearby work, bound generation/meshing/upload queues, and cancel obsolete work when moving away.
- Track dirty regions and coalesce repeated invalidations. Editing one block must not rebuild the entire loaded world. Rebuild neighboring geometry only when its visible result can change.
- Keep the previous valid mesh visible until a replacement is ready; commit completed work safely. Use generation/revision identifiers so late worker results cannot overwrite newer edits or reintroduce unloaded chunks.
- Put a budget on main-thread GPU uploads and other expensive commits. Avoid creating a frame-time spike by applying every queued result at once.
- Load/generate through the chunk manager deliberately. Queries, collision, pathfinding, and rendering must not accidentally force unbounded synchronous chunk loads.

Rust does not guarantee better performance by itself. Profile bottlenecks, record actual measurements, and optimize confirmed costs without silently changing game behavior or reducing comparison quality.

## Implement the actual vanilla game

Build and maintain a complete target-version feature inventory rather than relying only on this high-level checklist:

- Version-correct player movement, collision, gravity, stepping, jumping, swimming, sprint/sneak behavior where present, camera controls, reach/raycasting, block selection, placement, breaking, tools, durability, and item drops.
- The actual release's game modes, inventory and hotbar, item stacks, containers, crafting, furnaces, food/hunger/health, damage, armor, death/respawn, XP, enchanting, brewing, and survival progression where present.
- Actual blocks and their states/metadata: non-cube shapes, stairs/slabs, doors, fences, beds, light sources, plants/crops, falling blocks, water/lava and interactions, explosions/fire, redstone, pistons, rails and other machinery present in this release. Match update ordering and timing using reference tests.
- Version-correct passive/hostile mobs, spawning/despawning rules, AI/pathfinding, combat, projectiles, drops, entity collisions, and environmental interactions.
- Seeded terrain, biomes, caves, ores, vegetation, structures, weather, day/night, light propagation, dimensions, portals, and the target release's Nether/End progression and boss behavior.
- Faithful in-game menus, inventory/container screens, HUD, chat, key bindings, options, sound/music behavior, pause rules, world selection, and create/delete world behavior. The launcher follows Legacy's UI; the game follows Minecraft 1.0's UI. Do not confuse the two designs.

For every omitted or approximate mechanic, mark its exact status in the matrix. A creative flight scene with a handful of block types is a preview, not a completed survival game.

## Persistence and compatibility

Implement correct negative coordinates, chunk boundaries, world/region addressing, IDs/metadata, entity and container state, player inventory, dimension state, and time. Read the actual target-version world format rather than assuming a modern format is compatible.

Use crash-resistant save operations with appropriate transactional/atomic replacement, bounded async I/O, and explicit error reporting. Handle incomplete saves and unsupported data without silently discarding it. Keep save-format versions distinct from Minecraft version labels.

Native engine saves can be used during initial development, but label them honestly. Import/export compatibility with vanilla 1.0 is a separate verified requirement before advertising interchangeable worlds. Imports operate on copies; export to a new destination with backups and validation. Never open my main world for testing or overwrite a source world.

## Multiplayer

Implement and verify single-player first. Then add a separate milestone for the actual vanilla 1.0 client/server protocol and a native headless server using the shared Rust simulation. Verify packet layouts, framing/compression where applicable, state transitions, authentication/session requirements, and timing for this exact version instead of borrowing modern protocol code.

Test at least two actual clients, movement/block/container synchronization, dimension changes, reconnects, malformed input, and authoritative inventory/world changes. Treat compatibility with the original vanilla server and compatibility between our own Rust clients/server as separate claims. Do not bypass online authentication or fabricate login success. If a historical external service prevents a particular interoperability test, document that exact limitation while completing independent tests.

Do not advertise complete Minecraft 1.0 compatibility while multiplayer or substantial vanilla systems remain missing. A single-player preview is allowed as an intermediate deliverable and must be labeled as such.

## ChunkShift launcher integration

Expose a native engine launch entry such as `chunkshift-engine --launch-config <path>`. Define and version a small JSON launch contract with the game version, asset location, isolated data/world directory, player profile, resolution, and runtime options. Validate it before opening the world. Do not put credentials or refresh tokens in command-line arguments, logs, or an unprotected configuration file. A local single-player profile does not need the launcher to hand the game its Microsoft refresh token.

Support `--version`, clear exit codes, per-session log files, and structured or consistently parseable stdout/stderr. Let ChunkShift show real startup progress, errors, and process exit status. Keep the Java and Rust options distinguishable in the launcher. Missing engine builds or missing assets must produce useful errors rather than launching an unrelated Java version.

Only expose headless-server and compatibility options once they work; do not generate working-looking buttons or flags backed by stubs.

## Milestones: implement, run, verify, continue

1. **Native foundation:** reproducible Rust build, actual rendered window, imported/identified assets, input/camera, initial chunk renderer, structured logs, and a headless simulation test harness. Prove the running game process is our native engine.
2. **Playable preview:** world creation, deterministic chunk streaming, movement/collision, selection, breaking/placing, hotbar, lighting, and save/reload. Run a manual playtest and keep screenshots. Label this a preview.
3. **Vanilla single-player:** complete the target feature matrix, correct terrain generation and gameplay systems, dimensions/progression, menus/audio, and copied-world compatibility tests. Continue implementing missing systems instead of declaring completion at milestone 2.
4. **Multiplayer and launcher:** implement the native server and target protocol work, exercise real multi-client synchronization, and launch both backends correctly from ChunkShift.
5. **Release validation:** close substantive parity gaps, fix corruption/crash/flicker issues, run benchmarks and a sustained playtest, package the engine, and publish an honest compatibility report alongside the build. No publishing/uploading is authorized by this prompt; prepare the release artifacts for me locally.

Build small working increments and keep progressing. Do not spend the whole session on architecture documents or placeholder modules. If context/session limits interrupt the work, preserve the implementation and write HANDOFF.md with completed behavior, actual test commands/results, known failures, and the next concrete task. Never mark the full objective complete just because a session is ending.

## Testing and performance evidence

Use meaningful unit/integration tests and golden behavior fixtures, including Java-compatible RNG/arithmetic, negative coordinates, world/chunk boundaries, save round trips and crash recovery, stale mesh rejection, raycasts/collision, inventories/recipes, light propagation, block-update ordering, fluids/redstone, and protocol parsing when implemented. Add tests as systems become real; do not fill the project with tests that only assert placeholder values.

Compare carefully prepared small fixtures against the exact vanilla 1.0 reference. Generation parity needs sampled block/biome results from fixed seeds, not screenshots alone. Save compatibility needs actual import/export and reopen tests, not merely a filename ending in the expected extension. Visual tests need real screenshots; simulation tests do not prove rendering is correct.

Include an optional debug overlay with separately labeled FPS, frame times, simulation TPS/tick duration, loaded chunks, dirty chunks, mesh jobs completed per second, queued worker jobs, uploads, and save/I/O timings. Do not confuse chunk rebuild counters with FPS.

Benchmark release builds with documented hardware, GPU backend, resolution, view distance, world/seed, warm-up, VSync/frame cap, and matched visual settings. Record median and tail frame times, chunk-streaming/editing spikes, CPU/RAM/GPU usage where measurable, and sustained behavior. Compare with Java 1.0 under equivalent conditions when possible. Do not invent FPS improvements, claim all-core scaling from CPU percentage alone, or compare an empty Rust preview against a fully featured Java world.

## Required deliverables

- Runnable native Windows x64 executable and repeatable build/package commands.
- Source, locked dependencies, necessary build configuration, useful logs, and license/asset notices; no bundled proprietary game assets or personal data.
- Actual gameplay screenshots/video if available, and the launcher integration change.
- Feature/compatibility matrix, tests actually executed, benchmark methodology/results, and known limitations.
- A precise status: native foundation, playable preview, partial single-player, or validated release. Only claim a full vanilla 1.0 recreation when the documented release requirements are actually met.

Start now by inspecting the existing ChunkShift repository and available toolchain/reference installation, record the exact target in a short TARGET.md, then immediately implement and run milestone 1. Continue into subsequent milestones as they become possible. Make routine implementation decisions autonomously and keep progress updates concise.

## Primary starting references

- Minecraft release history: https://www.minecraft.net/en-us/15th-anniversary
- Official version metadata: https://piston-meta.mojang.com/mc/game/version_manifest_v2.json
- Bevy introduction and license: https://bevy.org/learn/quick-start/introduction/
- Bevy setup/build guidance: https://bevy.org/learn/quick-start/getting-started/setup/
- Rust documentation: https://doc.rust-lang.org/
- Minecraft terms: https://www.minecraft.net/en-us/eula

These sources do not establish every historical mechanic or every interoperability detail. Validate those against the exact reference version and clearly documented, appropriately licensed specifications rather than filling gaps with guesses.
