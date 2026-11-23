# Hands-On Delight вЂ” Minecraft 1.7.10

A native adaptation of Pocky's Hands-On Delight 1.5.1 for Farmer's Delight Classic. Version **1.5.1-backport.7**. Small 1.7.10 maintenance credit: Zahin. Original authors and MIT notices are retained.

Requires Java 8, Forge 10.13.4.1614, Farmer's Delight Classic **1.1.1**, and **UniMixins 0.3.1**. NEI integration is tested with NotEnoughItems **2.3.54-GTNH** and CodeChickenCore **1.4.10**. Install the production Hands-On JAR on the client and server. Keep the leading `+` on the UniMixins filename.

## Cooking

Right-click a pot, cutting board, skillet or stove. The camera flies into the original station scene. The world keeps running. Escape flies back to the player and restores perspective and field of view. The original edge hints and W/A/S/D or arrow keys move to a nearby supported station within three blocks when the cursor is empty. Sneak-right-click retains the ordinary Farmer's Delight interaction, except when extra board layers need the station.

The transparent drawer at the bottom contains usable ingredients, containers and tools. Pick up a stack and click the station to place it; right-click places one. Shift-click a drawer stack places a batch. Dragging a stack from the drawer onto a station also places it. An unfinished cursor stack returns to the inventory when the station closes.

- **Pot:** click the ladle to hold it, move in circles, and click again to release. A longer held drag releases with the mouse button. Heat and a matching recipe are required. Click the soup with a suitable container to serve; Shift serves a batch. The original liquid solver supplies waves, wakes, foam and broth colours, with floating ingredients, melting, steam and bubbles. The original dish badge shows the dish, stars and cooking progress. Cooking 8 and 32 servings of a dish opens further stirring stars. Unattended pots resume native cooking after 40 ticks.
- **Skillet:** click the handle or food to turn it. Right-click food to take it; finished food can be taken with either button. A batch holds up to 16 items.
- **Stove:** click an individual piece to turn it. Right-click takes it; either button takes a finished piece. Each place holds one item. Shift-inserting fills empty places and leaves excess ingredients in the inventory.
- **Board:** click a dashed cutting mark with a suitable inventory tool, or use a tool held on the cursor. The tool uses the original stroke poses. Native cutting recipes provide output, chance, fortune and tool wear; outputs go to inventory with overflow dropped normally. Right-click takes the top ingredient stack back. Eligible food assembly recipes also work on the board, including batches and adding the base under existing filling. A terminal dish assembles automatically; left-click assembles a complete layered dish explicitly. Shift-click a tool into an empty board to leave it embedded, then click it to retrieve it.

The folding cookbook retains the original mesh, paper, page grid, tabs, search, filters, ribbon and screen-fitting rules. Click a recipe to select it, then click it again to fill its ingredients. Shift requests a batch. Pot book filling adds up to 16 complete sets, as in the original. Drag the cover to move the book; its upper-right corner sizes it. The mouse wheel turns pages or scrolls a long ingredient list. R and U over an item open NEI; Escape returns to the kitchen.

NEI retains native cooking, cutting and grilling categories. The assembly category uses the original JEI layout and artwork, translated to NEI. The 17 eligible installed food assembly recipes move from personal crafting into this category. Tool crafting, slice reassembly and automation matching retain their native rules.

## Scope

This release implements the four base Farmer's Delight stations. The original Hands-On and Diegesis scene files, models, textures, colours and interface layouts supply its appearance. The native renderer implements liquid motion, tool strokes, assembly motions, pan tosses, individual stove flips, insertion/taking effects and camera travel using the source implementations and constants. Original audio and particle textures are retained. Modern vanilla sound identifiers are mapped to their 1.7.10 equivalents where needed.

Optional addon kitchens are outside this four-station release. Minecraft 1.7.10 uses its own item icons, lighting and rendering pipeline; this is a native adaptation, and complete pixel-for-pixel parity with the modern engine is not claimed. Tested behaviours and practical limits are listed in TESTING.md.

Station data is stored inside native tiles under `handson_delight`, retaining the installed item and block IDs. Extra unfinished assembly layers are saved by this mod. This release targets Farmer's Delight Classic 1.1.1's bytecode and recipes.

## Source and building

- Hands-On Delight: https://github.com/Pocky-l/handson-delight вЂ” MIT, Pocky.
- Diegesis: https://github.com/Pocky-l/diegesis вЂ” MIT, Pocky.
- Farmer's Delight: https://github.com/vectorwing/FarmersDelight вЂ” MIT, vectorwing.
- Farmer's Delight Classic: https://github.com/GlowingFederal/FarmersDelight-Legacy вЂ” MIT, original and legacy maintainers.

Pinned revisions, artwork hashes and UI provenance are in the adjacent source documents. Game dependencies remain separate JARs. JOML 1.10.5 is included under a private package to isolate it from other mods; its MIT notice and pinned hash are included in the source archive.

`tools/build.py` uses Java 8 and a native Forge 1.7.10 SDK, configured through `.toolchain/toolchain.json`. Required keys are `javac`, `java`, `forgeJar`, `srgToMcp`, `runtimeClasspathFile` and `existingGradleCache`. The existing cache supplies SpecialSource 1.11.1 and its ASM/Guava dependencies. Put unmodified Farmer's Delight Classic, UniMixins, NEI and CodeChickenCore dependencies in `vendor`. The source archive includes `vendor/joml-1.10.5.jar`; the build verifies its pinned SHA-256 before bundling and shading it. The build creates a separate deobfuscated compile dependency, then reobfuscates the release.

Use `python tools/build.py --tests` and `python tools/verify.py` for the dedicated-server fixture and fresh-process reload. `python tools/verify.py --client --modpack` runs the real client with disposable default configuration and copies of the local modpack JARs. Production builds use `python tools/build.py`. The test JAR creates and closes fixture sessions; use only the production JAR in the game.

For the native stove recipe and its required brick blocks, see [stove crafting](STOVE_CRAFTING.md).

For knife conflicts, restored sushi/roll recipes and sheep mutton, see [food compatibility](FOOD_COMPATIBILITY.md).
