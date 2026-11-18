# Original interface sources

The compatibility layer reads the original authored scene JSON. The release includes byte-identical copies of the four base station scenes and the pot/skillet JSON models from Hands-On Delight. No replacement panel artwork is added.

| Interface part | Original implementation | Native adapter |
| --- | --- | --- |
| Camera, work planes, hit boxes, book transforms, drawer colours | `assets/handson_delight/diegesis/scenes/{cooking_pot,cutting_board,skillet,stove}.json` | `client/SourceScene.java` |
| Transparent compact drawer, padding 3, pitch 18, nine columns, stable usable-slot ordering | Diegesis `impl/client/focus/FocusScreen.java`; defaults in `api/scene/InventoryDrawer.java` | `client/InventoryDrawer.java` |
| Lying folding book, 72×96 pages, tabs, search, filters and screen fitting | Hands-On `client/RecipeBook.java`, `client/BookTabs.java`, `BookMath.java`, `art/models/recipe_book/recipe_book.bbmodel` | `client/FoldingBook.java`, `client/RecipeBookModel.java` |
| Dish badge, cut-corner plate, star and flame pixels, palette | Hands-On `client/PotBehavior.java` | `client/PotBadge.java` |
| Hollow pot, ladle, pan and handle geometry/UVs | Hands-On `models/diegesis/pot/` and `models/diegesis/skillet/` | `client/SceneModels.java` |
| Flat food icons with raised pixel edges | Hands-On `client/ItemVoxels.java` | `client/ItemVoxels.java` |
| Food and broth colour sampling | Hands-On `client/FoodColors.java` | `client/FoodColors.java`; `SpritePixels.java` reads native resource-pack images, including with OptiFine |
| Pan food placements, sizes, stagger, yaw and jerk | Hands-On `client/SkilletBehavior.java` | `client/StationRenderer.java` |
| Liquid waves, wakes, colour, foam and surface shading | Diegesis `LiquidSim.java`, `LiquidRenderer.java` | `client/LiquidSim.java`, `client/LiquidSurface.java` |
| Tool strokes and cut-mark snapping | Hands-On `ToolStroke.java`, `BoardBehavior.java` (SNAP 1.4) | `client/ToolStroke.java`, `StationRenderer.java`, `StationScreen.java` |
| Assembly finishing motions | Hands-On `BoardBehavior.java` | `client/AssemblyAnimation.java` |
| Steam, bubbles, surface pops, oil pops and heat | Diegesis `DiegesisParticles.java`, `SceneParticles.java`; Hands-On `HandsOnDelightClient.java` and original sprites | `client/SourceParticles.java`, `StationVisuals.java` |
| Flight easing, duration, arc and neighbouring-station controls | Diegesis `Easing.java`, `FocusController.java`, `FocusScreen.java` | `client/Easing.java`, `KitchenCamera.java`, `Neighbors.java` |
| Assembly recipe positions and board/arrow artwork | Hands-On JEI assembly layout; Farmer's Delight original cutting-board JEI texture | `compat/nei/AssemblyHandler.java` |

`upstream.json` records repository revisions. `asset-sources.json` records the original Farmer's Delight artwork revision and hash. `ui-assets.json` records included source-scene/model hashes. All original MIT notices are included in the JAR and source archive. Native adaptations use Minecraft 1.7.10's inventory, rendering and Forge network APIs.

Native board placement/removal audio maps modern vanilla `block.wood.place` / `block.wood.hit` to native `dig.wood` / `step.wood`. These are the original Farmer's Delight event mappings, adapted to the 1.7.10 sound registry. JOML 1.10.5 preserves the original quaternion/matrix pose calculations and is shaded into `com.pockyl.handson_delight.math.joml`.
