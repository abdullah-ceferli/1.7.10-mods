# Release verification - 2026-11-09

Tested both client mods together in a separate Minecraft 1.7.10 / Forge 10.13.4.1614 instance on Java 8u51, with copies of the current installed modpack including HBM, OptiFine E7, AE2, NEI, ProjectE, Industrial Technology/OpenComputers and Offline Skins. Used a new disposable test world; no real save was opened.

- 7 line-scan tests and 6 preset-file tests passed.
- In-game Controls button and Saved Controls screen worked; screenshots were visually checked at an 854x480 window size.
- Presets restored vanilla/mod keys, mouse buttons, unbound keys, sensitivity and invert mouse. Verified options.txt persistence, refreshed key lookup, automatic undo and reordered bindings.
- Line selection counted 6 non-air blocks across 8 positions with 2 gaps, including endpoints and mixed block types. Tested Forge mouse-event dispatch, toggle/reset, ignored menus/key repeat, rebound Use Item, unloaded-region rejection and world-unload cleanup.
- G and keyboard-rebound selection used the shared input handler. Physical keyboard/mouse input and an Android device were not automated.
- Test exited normally. Installed JARs match tested JARs, use Java 8 bytecode and exclude the test harness and Minecraft classes.

Evidence: ../block line counter mod/build/integration-final/RESULT.json, console.log and screenshots/. Hashes: SHA256SUMS.txt and installed-1.0.0.json.
