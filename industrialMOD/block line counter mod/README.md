# Block Line Counter — Minecraft 1.7.10

Client-only Forge mod, separate from Offline Skins. No server installation is needed.

Press **G**, then **right-click the first and last block** of a straight horizontal row. Both blocks must have the same Y and share either X or Z. The result appears in the upper-right corner as **Blocks: N**. Both endpoints are included. All non-air blocks in that one row count; air gaps do not. Diagonal rows and rectangles are not supported. Different block types count together, including fluids.

Press **G** again to hide the result and clear the selection. After a result, right-click a new first block to measure another row. A second point at the wrong height/alignment is rejected; the original first point stays selected. Leaving/changing the world clears and disables measurement.

While measurement mode is on, right-click selects rather than placing blocks or opening machines. Turn it off with G for normal interactions. Change **Toggle / clear block-line counter** under **Options → Controls → Block Line Counter** if G conflicts with another mod. A rebound Use Item button/key is supported. Menus and chat do not select blocks.

The scan reads only already loaded client chunks, at most 256 block positions per game tick. It never loads/generates chunks, modifies blocks, sends packets or writes saves. If any part is unloaded it reports that instead of treating it as air. The maximum range is 65,536 positions. Results are a one-time measurement, not a live rescan. Gaps do not stop the scan.

Build using Java 8: `./build.ps1`. Release: `build/libs/BlockLineCounter-1.7.10-1.0.0.jar`.
