# Cultural Delights native backport status

Initial tested compatibility build: 0.16.7-backport.1. This is not a claim of complete modern-version parity.

## Implemented and verified

67 item identities, 47 foods, 3 crops, one tree family, 26 available crafting recipes and 7 cooking-pot recipes. Native eating, source food effects and animation, container returns, reusable utensils, seed use, bonemeal growth, harvesting, fruit-tree generation and native recipe matching are exercised. Cultural Delights cooking and cutting recipes run through the native Farmer's Delight tiles; feast servings and crop metadata survive save/reload.

The original textures and model data are retained. The client checks all 387 registered block-texture sprites, all 318 available crafting outputs through NEI, the garden models, and return to the title screen without OpenGL errors. Original upstream assets match their recorded SHA-256 hashes. The platter dependency texture is documented separately.

## Remaining differences

Modern-only ingredients such as dried kelp, beetroot and sweet berries leave some recipes unavailable. Stripped jungle-log and glow-ink outputs require a modern-ingredient provider. Brewin' and Chewin' fermentation is not ported, so the original pickle acquisition route is unavailable. The modern glowing outline effect and glow-squid acquisition route are not implemented.

Tree shape and world-generation density use native 1.7.10 implementations and are not identical to the modern generator. Modern village levels, pricing/progression and advancements are not reproduced exactly. See `tested-compatibility.json` for individual unavailable recipes. Registered ingredient identities do not by themselves establish a survival acquisition route.

Native furnaces handle the modern smoker/blast-furnace cooking recipes; Farmer's Delight handles grilling. No replacement cookbook interface was invented.

## Validation and dependencies

Dedicated-server gameplay and fresh-process reload checks passed. The dedicated modpack test excluded client-only EasyLAN, which cannot start on a dedicated server. The complete client-modpack test included EasyLAN, HBM, Hands-On Delight and the other installed client mods. Fresh disposable defaults were used; existing user saves were never opened. Android/Zalith was not tested.

The production classes and assets match the tested JAR payloads; test helpers are excluded. Runtime dependency JARs are in `vendor/runtime`, with exact hashes and required addon API methods in `runtime-dependencies.json`. Both ports require this tested Farmer's Delight Classic build and UniMixins.

These builds have not been installed into the live game mods folder.

## Sources and credits

Original Cultural Delights 0.16.7: https://github.com/Baisylia/Cultural-Delights-1.20.1 at `9894772609f31635b655a29810db367675348224`. The original project is All Rights Reserved. This local adaptation does not grant permission for public redistribution; no publishing was performed. Backport maintenance: Zahin.
