# ZalithRender

An original **Minecraft Forge 1.7.10** client renderer by **Zahin**, targeting **Zalith Launcher 1 and Zalith Launcher 2**.

Version **0.2.0** adds complete-frame pacing and a time allowance for optional mesh-cache uploads to the experimental chunk renderer. It is a separate implementation; it does not contain Angelica or Beddium runtime code. It is not affiliated with either Zalith project.

## Downloads

- [ZalithRender-1.7.10-0.2.0.jar](releases/ZalithRender-1.7.10-0.2.0.jar)
- [Complete corresponding source](releases/ZalithRender-1.7.10-0.2.0-sources.zip)
- [Verification report](releases/verification.json)
- [SHA-256 checksums](releases/SHA256SUMS.txt)

**Android device testing and gameplay FPS measurements are still needed.** Both launchers' actual LWJGL Java libraries pass desktop native graphics checks. Those checks use an NVIDIA compatibility context, not an Android Mali driver.

## What it does

Minecraft's original chunk builder still creates the complete geometry and display lists. ZalithRender captures eligible geometry once and uploads it into vertex buffers. During chunk drawing it replays cached meshes through the older fixed-function pipeline. Vanilla visibility, ambient occlusion, lightmaps, transparency sorting, textures and draw order remain in charge.

The implementation uses OpenGL 1.3/1.5 APIs. It does not require OpenGL 3.3 core, compute shaders, native platform binaries or GPU-fence dispatch through a separate service layer.

- Normal render distance, resolution, particles and graphics settings remain unchanged.
- Ordinary modded ores and crops that inherit vanilla rendering can be cached.
- Custom block renderers, unusual tessellator layouts and multiple-draw chunk passes keep Minecraft's original rendering.
- Custom Forge world-render hooks trigger normal rendering so their additional graphics are retained.
- GPU mesh memory defaults to **32 MiB**, with **8 additional uploads per frame**, **2 MiB per mesh** and **4,096 entries** at most. These are configurable bounds, not claims about total Minecraft memory use. A growable CPU staging buffer is separate; original Minecraft display lists remain allocated for fallback.
- Optional cache copying and uploading has a **2 ms per-frame CPU-time allowance**. Once consumed, subsequent eligible chunks retain native rendering. An individual allocation or driver call can exceed this allowance; it is not a hard limit on total frame time or Minecraft's original chunk builder.
- Chunk replacement, translucent re-sorting, world unloading, cache eviction and lost GPU-buffer detection invalidate the relevant cached data.
- The primitive-key cache avoids allocating an Integer for each chunk lookup.

This release focuses on terrain rendering and frame pacing. It does not implement Angelica's shader-pack support, dynamic lights, custom font system, entity batching or other full-suite features. FPS gains on a specific phone are not established.

## Frame pacing

The default target is **60 FPS**. After Minecraft has drawn a frame, the scheduler sleeps until its presentation deadline, immediately before the native display update. Each loop retains normal ticks, input processing, audio, world rendering and display updates. The original FPS counters are unchanged. Small sleep overshoots are compensated on the following deadline; slow render work resets the schedule to avoid catch-up bursts. Waiting uses parking, without a busy-spin loop.

This can reduce unnecessary work when the device can render faster than the target and may reduce heat over longer sessions. It cannot make a 66 ms frame (about 15 FPS) fit into the 16.7 ms needed for 60 FPS. Sustained 60–70 FPS requires measurements and sufficient rendering performance on the actual phone. No minimum FPS is promised.

In `config/zalithrender.cfg`, the `pacing` category contains `enabled` and `targetFps`. Set `targetFps=70` to request 70 FPS; use 60 for a 60 Hz phone. VSync, a lower Minecraft FPS cap, menus, skipped world rendering, or an unsupported modified frame loop retain Minecraft's normal limiter. The mod does not overwrite video settings or raise an existing lower cap. In the F3 screen, the pacing line reports recent completed display-update intervals as FPS and p95 frame time. These measurements include stalls; they do not substitute the target value for observed performance.

To compare pacing alone, use the same world, viewpoint, video settings and FPS cap, and change only the `pacing.enabled` setting between restarts. Allow the device to warm up. Compare the reported FPS and p95 values as well as visible stutter. A larger p95 means more slow frames. The separate Mobile Performance preset changes graphics and should be evaluated separately.

## Install

1. Close Minecraft and use a copy of the instance for the first test.
2. Remove Angelica, including any `angelica-*-zalith.*.jar`, from that test instance. Install one ZalithRender JAR in `mods`.
3. Use **Minecraft 1.7.10, Forge 10.13.4.1614 and Java 8**. Begin with **GL4ES** in either launcher. MobileGlues requires an appropriate fixed-function compatibility layer, such as SFPEW; raw core-profile-only rendering is not the target of this build.
4. Keep the other mods' required dependencies. ZalithRender adds no dependency on GTNHLib, FalsePatternLib or UniMixins. Hodgepodge and BugTorch can stay, although full-pack gameplay needs testing.
5. Open a new test world and press **F3**. The `ZalithRender` line shows cached and native chunks and cache memory. Native fallback is expected around unsupported custom geometry; those chunks are still drawn.

This is client-side work: it adds no items, blocks, recipes or world-save format. It does not need to be added to the LAN server or other players' clients.

When another rendering replacement is detected, it retains normal Minecraft rendering rather than stacking pipelines. Test it separately from Angelica, Beddium, Neodymium, FalseTweaks, OptiFine, FastCraft, BetterFoliage and RPLE.

To uninstall, close Minecraft and remove this JAR. The generated `config/zalithrender.cfg` contains only this mod's options.

## Validation

The checks cover both MCP development and SRG runtime field/method names, five real Minecraft target classes, rejection of unknown layouts, cache lookup and removal, all 16 vertex-attribute combinations, textures, lightmaps, blending, transparency re-sorting, mixed cached/native rendering, custom geometry, memory and upload limits, chunk release, context-buffer loss, and GL state restoration. Timing checks cover 60/70 FPS deadlines, varying work costs, a 15 FPS workload with no added delay, sleep overshoot, suspension, interruption, clock wrap, native limiter/VSync policy, honest interval statistics, and upload time limits. Bytecode checks preserve the native loop's call sequence and all FPS/tick field writes.

Native tests also load the actual transformed Minecraft Tessellator and compare its normal display-list pixels against the captured vertex-buffer result. Complete-frame pacing smoke checks exercise draw, swap and event-poll calls; they are not an Android FPS benchmark or a complete Minecraft gameplay run. They run against the pinned Zalith 1 LWJGL 3.3.6 and Zalith 2 LWJGL 3.3.3 libraries. The report records the tested desktop GL version and explicitly marks physical Android testing as false.

These are renderer and integration fixtures, not a full modpack gameplay benchmark.

## Build

Use a **Java 8 JDK**, Python 3.9+ and a ForgeGradle development cache for Forge 10.13.4.1614.

```powershell
.\build.ps1 -JavaHome 'C:\path\to\jdk8'
```

The script uses a sibling Industrial Technology development cache when present. Otherwise it prepares a local Forge workspace through the Gradle wrapper. You can set `-Cache` explicitly.

For the direct build and native verification:

```powershell
python build.py --java-home 'C:\path\to\jdk8' --cache 'C:\path\to\gradle-user-home'
```

`--offline` uses verified cached test libraries. `--skip-native` builds and performs the mapping checks without claiming native graphics validation. Windows is currently the host for the native checks. Native library inputs and hashes are pinned in `test-dependencies.lock.json` and are not included in the runtime mod.

Build outputs go into `dist`. After reviewing a build, copy its JAR, source ZIP, report and checksums into `releases` for publishing.

## GitHub layout

`src`, `tests`, build scripts, the Gradle wrapper, licenses and documentation are intended to be tracked. `releases` deliberately contains the reviewed build. Generated classes, private game instances, caches, downloaded test libraries and logs are ignored. The folder can live in a monorepo or be uploaded as its own repository.

## License

Original project code is provided under the [MIT license](LICENSE). See [THIRD_PARTY.md](THIRD_PARTY.md) for the unmodified build tools and external APIs used for development and testing.
