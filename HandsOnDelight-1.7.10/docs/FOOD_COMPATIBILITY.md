# Food compatibility — Hands-On Delight 1.5.1-backport.7

This update fixes knife recipe conflicts, restores food recipes whose modern ingredients were missing, and adds a native sheep-meat acquisition route. Install this JAR on client and server, replacing the previous Hands-On Delight JAR. Farmer's Delight Classic and the existing food-port JARs remain separate dependencies.

## Knives

Farmer's Delight knives use two of their blade material across the top row and a stick under the left blade: `BB` / `S `. Blade materials are flint, iron, gold or diamond. Croptopia's utensil knife has a separate pattern: ` I` / `SI`, using two iron ingots and a stick. The overlapping old knife patterns are removed, preserving Chisel's recipes. The outputs are verified through the real player workbench/output slot with Chisel installed. Croptopia's utensil remains its original recipe ingredient; use Farmer's Delight knives for cutting-board tools.

## Food ingredients and crafting stations

The original recipes and output quantities are retained, with these explicit native equivalents only when a modern ingredient provider is absent:

| Missing modern ingredient | Native equivalent |
| --- | --- |
| Seagrass | Croptopia sea lettuce, obtained from its fishing table |
| Dried kelp | Dried Sea Lettuce: smelt Croptopia sea lettuce |
| Beetroot | Croptopia radish |
| Sweet berries | Croptopia raspberry |
| Honey bottle | Croptopia molasses |
| Crimson / warped fungus | Native red / brown mushroom |
| Raw / cooked mutton | This update's separate raw / cooked mutton items |

An installed Et Futurum provider retains its own availability/configuration rules. Existing resolved items are never replaced. No modern item is registered under the minecraft namespace.

This restores the 16 formerly unavailable Croptopia/Cultural Delights recipes checked by the fixture, including sushi, tuna and other rolls, mutton sandwich, rice balls, beetroot salad, borscht, candied kumquats, mead and several desserts/stews. Eligible assembled dishes belong on the **cutting board**, using the original Hands-On cookbook and the NEI assembly category. Their absence from the personal crafting grid is intentional. Smelting and heated cooking-pot recipes retain their normal stations. The stove recipe continues to use iron, brick blocks and a furnace.

## Sheep meat

Adult sheep drop 1–2 raw mutton, plus the normal random Looting bonus. Burning sheep provide cooked mutton. Baby sheep and canceled drop events produce none. Existing mutton drops from another mod are kept without adding a second serving. Mutton cooks in a furnace and can feed the native Farmer's Delight cutting/cooking recipes.

The meat icons reuse Farmer's Delight's original meat artwork. Dried Sea Lettuce currently uses the native reed icon; it is identified by its own item name. Custom modern-mob acquisitions, Patchouli, optional modern machine integrations and glow effects remain outside this fix.

## Evidence

The dedicated native server fixture loads both food ports plus Chisel, checks all 16 restored recipes, real knife crafting, native drops, furnace results and board assembly. The regression suite verifies 239 eligible assembly recipes and fresh-JVM board/skillet persistence. The client fixture checks all 239 NEI assembly entries, native right-clicks, the original 3D cookbook and world/menu rendering after exit. The shipped main-class/resource payload is compared with that tested fixture. Existing user saves are never opened. Android/Zalith device validation remains pending.
