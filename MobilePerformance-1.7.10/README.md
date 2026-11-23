# Mobile Performance for Minecraft 1.7.10

A separate client mod for reducing rendering work on lower-powered devices, including Zalith. Maintained by **Zahin**. Requires **Minecraft 1.7.10, Forge 10.13.4.1614 and Java 8**. First Android test build: the desktop Forge and modpack checks are recorded below; performance on a phone depends on its hardware, renderer and scene.

## Install and use

Put `MobilePerformance-1.7.10-1.0.0.jar` in your **client's** `mods` folder and restart Minecraft. Keep your existing gameplay mods. The server and your friends do not need this mod. It can also run on a desktop client, and does not replace OptiFine or the launcher's renderer.

The rendering budgets are enabled on installation. Existing graphics settings are retained until you choose the phone preset.

```text
/mobileperf preset   Apply the low graphics preset and save previous settings
/mobileperf restore  Restore those previous graphics settings
/mobileperf off      Disable rendering budgets and restore an active preset
/mobileperf on       Enable rendering budgets
/mobileperf status   Show recent FPS/frame times and skipped-render counters
/mobileperf help     Show the controls
```

The phone preset uses **4 chunks**, fast graphics, minimal particles, no ambient occlusion, no clouds, no mipmaps, VSync off and a 60 FPS limit. World texture animations update every other tick, so they play at half rate. Menus retain the native animation update rate. Applying or restoring graphics may take a moment while textures/chunks reload.

The original values are saved once in `config/mobileperformance-before-preset.json`. Reapplying preserves that original snapshot. Restore changes only the fields the preset owns and removes the snapshot after success. Invalid snapshots are retained without applying their values; `/mobileperf off` still disables the rendering budgets. Key bindings, world data and launcher renderer settings are not changed.

## Rendering trade-offs

| Rendering work | Default |
| --- | --- |
| Ordinary vanilla mobs | Draw within 48 blocks |
| Dropped item models | Draw within 24 blocks |
| Chests, signs, skulls and supported food displays/stations | Draw within 32 blocks |
| Selected vanilla cosmetic particles | 16-block range; up to 64 spawns per client tick outside the nearby area |
| Nearby particles | Preserved within 8 blocks |
| Texture animation rate | Native rate until the phone preset is applied |

Objects beyond these distances can visibly disappear. They remain in the world, continue receiving updates and reappear when approached. This reduces distant visual detail; it does not stop server simulation, remove items or change machine logic.

Players, bosses, projectiles, unknown mod entity renderers, custom HBM machine renderers and unknown/custom particles are retained. Fire, explosions and block-breaking feedback are excluded from the cosmetic particle budget. GUI/inventory entity previews bypass the world distance checks.

Supported food tile namespaces are the Display Delight, Farmer's Delight Classic and Croptopia backports in this repository. General HBM and Industrial Technology tile renderers keep their existing behavior. The mod issues no occlusion queries or custom shaders and requires no additional OpenGL extension.

Adjust the distances, particle cap and animation interval in `config/mobileperformance.cfg` while Minecraft is closed. Removing the mod leaves any applied vanilla graphics settings in place; use `restore` before uninstalling to recover the stored values.

## Validation and FPS comparison

The test-only mod exercises actual renderer/particle/animation methods in disposable native Forge clients, with and without the existing OptiFine E7, HBM X5771, NEI and food-mod pack. It checks preset backups/restoration, GUI transitions, protected renderers, skipped render paths and GL errors. The production JAR is also loaded on a dedicated server to verify that its client hooks stay inactive there. No player world is opened.

Reports and desktop frame samples are in [docs/tests](docs/tests). The scene deliberately contains many distant chests, items and mobs. Its frame times describe that Windows test machine and scene; phone gains remain to be measured.

On the phone, compare `/mobileperf off` and `/mobileperf on` at the same position and view, with identical graphics settings. Allow the scene to settle before checking `status`. Compare the optional preset separately, since it changes visual settings. A scene dominated by terrain, custom HBM rendering, driver overhead or memory pressure may respond differently from the test scene.

## Build

Use a Java 8 JDK and `./gradlew test testModJar build sourcesJar -PintegrationTest`. `build.ps1` can reuse the sibling Industrial Technology Java/Gradle cache or `JAVA_HOME`. Output is in `build/libs`. The runtime JAR contains only this mod; test code is packaged separately.

`tools/verify_native.py` runs a disposable client; add `--pack --mods-dir <your mods directory>` for the pack, or `--server` for a dedicated-server smoke check. The local verification launcher uses the sibling project's prepared Forge runtime/assets. The optional renderer changes offered by other performance mods require their own compatibility checks.

Original Minecraft, Forge and mod content retains its original authors and licenses. This mod's original code is under the [MIT license](LICENSE).
