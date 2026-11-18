# Native verification

The fixture builds against the native Forge 10.13.4.1614 API with Java 8, reobfuscates the output, then launches the actual game. Reports alongside this file record successful dedicated-server, fresh-JVM reload and real-client checks. Each run uses a disposable world; user worlds are never opened.

Dedicated checks cover stirring quality and idle decay; action-rate bounds; attended/manual and unattended/native pot cooking; batch consumption; serving containers and native recipe experience; Shift-serving all remaining meals; two-sided frying and exact retained results; native knife wear and output rules; atomic book filling; all 17 eligible assembly recipes; personal-crafting restrictions with automation retained; late-base placement, top-layer retrieval and batch assembly; bulk insertion into all six stove positions; embedded-tool retrieval; break/drop accounting; blocked stove cleanup; distance and replaced-tile validation; tile NBT and fresh-process save/reload.

Real-client checks use the user's modpack JARs, including HBM X5771, Industrial Technology 2.10.1, Display Delight, OptiFine E7, NEI and Farmer's Delight Classic, with fresh default configuration. They verify camera attachment/restoration, rejected forged tokens, synchronized ingredients, the original compact drawer, native cursor pickup and 3D placement, skillet handle interactions, 3D book ray picking, real recipe filling, search, all assembly entries in NEI, NEI return to the existing kitchen session, stove piece hit targets and safe cursor return on closing; the original cut-mark hit target and synchronized knife stroke; nearby-station travel with rejected out-of-range offsets; animated exit and field-of-view restoration.

Six numerical invariants check liquid boundedness, settling and volume conservation, wake fading, flight easing and inversion, original tool quaternion poses, and capped particle lifetimes. The client fixture captures the pot, skillet, board cookbook, knife marks/stroke, NEI, stove and neighbouring-station screens and checks for OpenGL errors. Original scene/model files are also hashed and compared with their pinned upstream copies. A process-only null OpenAL backend is used for unattended verification. Production game sound settings are unchanged.

Passing these checks demonstrates the recorded native behaviours. The tests exercise this four-station release. They do not exhaust every recipe combination, resource pack, shader setup or remote-server latency case. Optional modern addon kitchens and complete modern-engine visual parity are not included.

## World-click regression (backport.3)

Backport.2 canceled the client Forge right-click event. Native Minecraft checks that event before sending its use-block packet, so the server never received a normal click. Earlier tests opened sessions directly and missed this entry path.

Backport.3 intercepts supported station clicks only on the server. Client checks now invoke Minecraft's actual right-click method with a block hit, then require the real network response to open both a pot and cutting board. The same run continues through cutting, cookbook use and station travel. Dedicated checks preserve previously canceled events, block-use DENY results and ordinary sneak-use behavior.

## Rendering-state regression (backport.4)

Four rendering invariants cover explicit texture-combiner isolation, default/lightmap matrices and separate alpha blending, exception cleanup, and matching native font/item pixels after faded cookbook rendering. World HUD and main-menu screenshots are captured after closing the kitchen. Texture attributes are not delegated to glPushAttrib in this path. See MOBILE-RENDERING.md for the Android validation limit.
