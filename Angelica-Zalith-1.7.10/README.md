# Angelica — Zalith compatibility edition

Minecraft **1.7.10 / Forge 10.13.4.1614**, based on official **Angelica 2.2.46**.
Compatibility changes maintained by **Zahin**. Original Angelica authors, notices and licenses are retained.

This is an **unofficial compatibility test build for Zalith Launcher 1 and Zalith Launcher 2 (yellow logo)**.
Both launchers' actual LWJGL libraries pass the shader-query regression checks on desktop OpenGL.
**Android gameplay, the Mali driver and FPS have not been tested on a physical phone.**

## Download and install

- Mod: [dist/angelica-2.2.46-zalith.2.jar](dist/angelica-2.2.46-zalith.2.jar)
- Corresponding source: [dist/angelica-2.2.46-zalith.2-sources.zip](dist/angelica-2.2.46-zalith.2-sources.zip)
- Checks: [dist/verification.json](dist/verification.json)

1. Close Minecraft. Move the existing Angelica JAR out of this instance's `mods` folder as a backup.
2. Put **one** `angelica-2.2.46-zalith.2.jar` in that client instance's `mods` folder. This replaces Angelica, including `zalith.1`; it is not an additional mod.
3. Keep **Java 8**, **GTNHLib 0.12.10** and **UniMixins 0.3.1** for the first phone test, matching the reported setup. Hodgepodge 2.8.5 and BugTorch 1.3.1 can stay.
4. Select **MobileGlues**, using the renderer that already passed the OpenGL core-profile check in Zalith 2. The selected renderer must provide **OpenGL 3.3 core or later**. The GL4ES 2.1 and Krypton 3.1 configurations in the earlier logs cannot satisfy this requirement.
5. If the previous troubleshooting step changed `config/angelica-modules.cfg`, restore `B:enableFontRenderer=true`. This build repairs the shader query instead of disabling font batching.
6. Launch to the main menu, then test a new world before opening the main save. If it fails, retain that run's `latest_game.log` and newest Minecraft crash report.

For Zalith 1, this build fixes the same broken query API in its bundled LWJGL 3.3.6 bridge. It still needs a compatible renderer; it does not add an Android renderer to the launcher.

Keep upstream incompatibility rules: do not combine Angelica with OptiFine, FastCraft, BetterFPS or OptimizationsAndTweaks.
To roll back, close Minecraft and replace this JAR with the backed-up original.

## What changes

Zalith's combined `glGetActiveUniform` and `glGetActiveAttrib` overloads overwrite the shader array size with its type and advance the caller's buffer position. Repeated calls eventually throw `Number of remaining elements is 0, must be at least 1`.

This edition redirects just those two Angelica backend calls to the existing separate-output-buffer APIs. It returns the actual name, array size and type, keeps the caller's buffer cursor intact and reuses scratch buffers per thread.

Version `zalith.2` also keeps the Celeritas `LWJGL3Service` native binding class out of the GL redirector. Otherwise its `glFenceSync` call is redirected back into `GLSMBuffers`, whose backend calls that same service again until the Java stack overflows. Actual GPU fences, waits, deletion and ordinary caller tracking remain enabled.

The Java 8 and Java 17 variants of the backend and redirector are patched. All other original JAR entries remain byte-identical, including configuration defaults, shaders, Celeritas terrain building, entity batching, fonts, caching and other performance modules. No new runtime transformer is added. The in-game mod version remains upstream `2.2.46`; the edition is identified by the filename and `META-INF/angelica-zalith.properties`.

The separate Android `ErrorActivity` / `Bundle.getParcelable` crash belongs to the launcher's error-report screen. This mod does not modify that Android activity.

## Build and verify

Install Python 3.9+ and a **Java 8 JDK**:

```powershell
python build.py --java-home "C:\path\to\jdk8"
```

Downloads are pinned by SHA-256 in `dependencies.lock.json`. `--offline` uses the verified cache under `upstream/`. Windows builds run real OpenGL tests in a hidden window against the exact Zalith 1 LWJGL 3.3.6 and Zalith 2 LWJGL 3.3.3 libraries. `--skip-native` produces a build whose report explicitly records no native test result.

The build deliberately patches the verified official binary so it can prove the performance code is unchanged. `patches/shader-query-bridge.patch`, `patches/native-service-routing.patch` and the new helper are the equivalent source changes. The source ZIP includes the full original source project with those edits applied, the helper, regression checks, build tools and notices.

The checks reproduce the original failures, then test repeated uniform and attribute queries, arrays, real GL types, names longer than 128 characters, truncated names, buffer growth, zero and nonzero output positions, buffer limits and rejected arguments. Routing checks use Angelica's actual redirector to prove the original native-service feedback and confirm that ordinary callers remain tracked after the fix. Native checks create, wait for, inspect and delete real GPU fences through Angelica's actual backend and Celeritas service using both launchers' libraries. They verify both multi-release backend and redirector classes and compare every untouched release entry.

## Upstream

- [Angelica 2.2.46](https://github.com/GTNewHorizons/Angelica/releases/tag/2.2.46)
- [Zalith Launcher 1](https://github.com/ZalithLauncher/ZalithLauncher)
- [Zalith Launcher 2](https://github.com/ZalithLauncher/ZalithLauncher2)
- [License and third-party details](THIRD_PARTY.md)
