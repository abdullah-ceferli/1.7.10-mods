# Combined release verification - 2026-11-09

PersonalClientTools-1.7.10-1.1.0.jar contains one Forge mod entry and both features. It replaces the separate BlockLineCounter and ControlPresets JARs. Key identifiers and config/controlpresets/presets.json are unchanged.

All 13 unit tests passed. In an isolated Minecraft 1.7.10 / Forge 1614 instance using Java 8u51 and copies of the installed modpack, the combined JAR alone passed the complete feature checks: Controls menu, saved vanilla/mod/mouse/unbound keys and mouse settings, options persistence, key hash refresh, undo, reordered bindings, block selection, inclusive gap counting, G reset, rebound Use Item, menu/repeat filtering, unloaded-chunk rejection and world-exit cleanup. The client exited normally. Evidence: build/integration-final/RESULT.json, console.log and screenshots/.

Input was exercised through Forge mouse-event dispatch and the shared key handler; physical input and Android were not automated. No main save was opened. The installed JAR matches the tested JAR, both separate JARs were backed up outside mods, and existing options, presets and Offline Skins files were verified unchanged during installation.
