# Croptopia native backport status

Initial tested compatibility build: 4.0.1-backport.1. This is not a claim of complete modern-version parity.

## Implemented and verified

446 item identities, 300 foods, 58 crops, 27 tree families, 292 available crafting recipes. Native eating, source food effects and animation, container returns, reusable utensils, seed use, bonemeal growth, harvesting, fruit-tree generation and native recipe matching are exercised. Cultural Delights cooking and cutting recipes run through the native Farmer's Delight tiles; feast servings and crop metadata survive save/reload.

The original textures and model data are retained. The client checks all 387 registered block-texture sprites, all 318 available crafting outputs through NEI, the garden models, and return to the title screen without OpenGL errors. Original upstream assets match their recorded SHA-256 hashes. The platter dependency texture is documented separately.

## Remaining differences

The original Patchouli guide is not implemented. Modern-only ingredients such as honey, beetroot, kelp, seagrass and Nether fungi leave some recipes unavailable. Cod/salmon/tropical-fish roe, ravager meat and glowing calamari do not yet have their modern-mob acquisition routes. Optional Botany Pots, modern Immersive Engineering cloches and Thermal insolators are deferred. The special fireproof property of the transcendental breakfast item entity is not emulated.

Tree shape and world-generation density use native 1.7.10 implementations and are not identical to the modern generator. Modern village levels, pricing/progression and advancements are not reproduced exactly. See `tested-compatibility.json` for individual unavailable recipes. Registered ingredient identities do not by themselves establish a survival acquisition route.

Native furnaces handle the modern smoker/blast-furnace cooking recipes; Farmer's Delight handles grilling. No replacement cookbook interface was invented.

## Validation and dependencies

Dedicated-server gameplay and fresh-process reload checks passed. The dedicated modpack test excluded client-only EasyLAN, which cannot start on a dedicated server. The complete client-modpack test included EasyLAN, HBM, Hands-On Delight and the other installed client mods. Fresh disposable defaults were used; existing user saves were never opened. Android/Zalith was not tested.

The production classes and assets match the tested JAR payloads; test helpers are excluded. Runtime dependency JARs are in `vendor/runtime`, with exact hashes and required addon API methods in `runtime-dependencies.json`. Both ports require this tested Farmer's Delight Classic build and UniMixins.

These builds have not been installed into the live game mods folder.

## Sources and credits

Original Croptopia 4.0.1: https://github.com/ExcessiveAmountsOfZombies/Croptopia at `bae8e837dbb1743fd6b73a93cc320f0c0800fc14`. Original Croptopia MIT license and authors are retained. Backport maintenance: Zahin.
