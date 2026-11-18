# Personal Client Tools - Minecraft 1.7.10

**One client mod and one JAR containing both Block Line Counter and Control Presets.** Requires Forge for 1.7.10. The server does not need it.

Install `PersonalClientTools-1.7.10-1.1.0.jar` in `mods`. Remove the older separate `BlockLineCounter` and `ControlPresets` JARs so their features are not registered twice. `install.ps1` does this with backups. Offline Skins is independent and is not changed.

## Block Line Counter

Press **G**, then right-click the first and last blocks of a straight horizontal row. Both endpoints must share the same height Y and either X or Z. The result appears at the top right as **Blocks: N**. It includes both endpoints and all non-air blocks between them; air gaps are skipped. Different block types and fluids count together.

Press G again to hide and clear the result. After a result, right-click a new first point to start another measurement. An invalid second point keeps the first point selected. While measuring, right-click selects instead of placing blocks or opening machines. Turn it off to use normal interactions. Rebind under **Controls -> Block Line Counter** if G conflicts with another mod.

The scan visits at most 256 already-loaded client block positions per tick, never loads chunks and runs only for a requested measurement. An unloaded region reports an error instead of a misleading count. Maximum line length: 65,536 positions. Results are not continuously rescanned. Menus/chat do not select blocks, and changing/leaving the world clears the selection.

## Saved Controls

Open **Options -> Controls -> Saved Controls**. Type a name and click **Save current**, or choose a preset and click **Load selected**. All vanilla/mod key bindings registered in Controls are included, including mouse buttons and unbound keys, along with sensitivity, invert mouse and touchscreen.

Presets remain in **config/controlpresets/presets.json**, compatible with the previous separate Control Presets mod. No conversion is needed. Copy that file to transfer presets between installations. Unknown mod keys are skipped and new mod keys keep their current assignments. Custom controls outside Minecraft's key-binding system are not included. Intentional key conflicts are preserved.

The first visit saves **Initial controls**. Each load first saves **Before last load** for undo. Successful file writes also keep `presets.json.bak`; replacing/deleting presets asks for confirmation. Invalid files are kept unchanged and show an error. File access happens only in the preset menu, with no network traffic or world scans.

## Build

Use Java 8 and `./build.ps1`. Output: `build/libs/PersonalClientTools-1.7.10-1.1.0.jar`. The source JAR and both sets of unit tests are built in the same project.
