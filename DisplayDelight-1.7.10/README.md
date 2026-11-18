# Display Delight for Minecraft 1.7.10

A native Forge 1.7.10 adaptation of Display Delight 1.7.0 by **jkvin114**. Port maintenance: **Zahin**.

This mod displays food in 3D. It does not add a cooking system or port Farmer's Delight. Real linked food can be placed, taken back and eaten normally. If a source food item is unavailable, its display remains a decoration and cannot produce an invented edible food.

## Install the test build

Use Java 8 and Minecraft Forge **10.13.4.1614** for **1.7.10**. Copy `build/libs/displaydelight-1.7.10-1.7.0-backport.1.jar` into the instance's `mods` directory on both client and server. Do not install the `tests.jar`, sources ZIP or the modern 1.20.1 mod. The build was tested in disposable native worlds; it has not been installed into your existing game or tested alongside HBM/Industrial Technology.

This is the first backport test build. Existing 1.20/1.21 Display Delight worlds cannot be migrated by copying their saves into 1.7.10.

## Use

- Craft **2 Food Plates** with wooden slabs arranged `S S` / ` S ` in a crafting table. Craft **2 Small Food Plates** with `SS` in a row. Any native wooden-slab subtype works.
- Place an empty plate, then right-click it with a linked food. Sneak while inserting to place as many as fit. Cookie plates hold four cookies.
- Empty hand: right-click to retrieve one food; sneak and right-click to retrieve all. The visible empty plate stays behind. Returned vanilla foods retain their normal edible items.
- Right-click with an axe to hide a plate. Right-click with the matching plate item to put it back. Food count is preserved.
- Sneak and right-click a surface with mushroom stew to place its bowl directly. Break it to retrieve the actual stew.
- Breaking linked plated foods returns their food count plus a visible plate. Silk Touch retrieves the display itself at full capacity with a visible plate; partial plates return food normally.
- Missing-mod food displays remain available in the creative tab and through native farmer-villager trades. They drop themselves when broken. They do not supply the missing food mod's items.

Vanilla **cookies**, **pumpkin pie** and **mushroom stew** work immediately. Rabbit stew, beetroot soup and Farmer's Delight/add-on items do not exist in an otherwise vanilla 1.7.10 game. Their decorative models are still included.

## Link other 1.7.10 foods

Start the game once to generate `config/displaydelight.cfg`. Each entry in `foodMappings` links a display to a real item, using `modid:item_name` or `modid:item_name@metadata`. For example, a modpack can map a bowl display to its installed food mod's soup. Set an entry to an empty string to disable that link. A single food can have one direct, one large-plate and one small-plate display. Duplicate links within the same category are rejected with the conflicting display names. Restart after editing.

Mappings determine placement and returned drops. Changing a mapping also changes what existing displays return; back up a world before changing a modpack's mappings. Defaults retain the original modern registry names and activate only if the corresponding item actually exists. No optional modern JAR is bundled or required.

## Port details

- **382 registered display types**, including two empty plates; all source model textures are retained.
- **1,706 preconverted mesh states** handle food counts, hidden plates and support tables. Modern element rotations, UVs and multipart selection are converted to native terrain/inventory rendering. North-facing geometry is shared across four native facing values.
- State uses a **non-ticking tile entity** because 1.7.10 has only four block-metadata bits. Counts/hidden plates persist in NBT and synchronize through native description packets. This differs from the modern mod's blockstate-only design and has not been benchmarked in large modpacks.
- Support tables retain the native equivalents of the original exceptions (stairs, doors, leaves, farmland, etc.). Global support-table rendering is configurable. Full data-pack/tag editing is not available in 1.7.10.
- Native smoke/flame particles replace their modern equivalents. Farmer-villager trades replace wandering-trader offers because 1.7.10 has no wandering traders. Trade quantities/selection use a native adaptation rather than the modern trader leveling system.
- A few upstream faces reference an undefined `#missing` texture. The mesh exporter uses a valid adjacent texture for those faces and records every affected model in `docs/asset-export.json`. Original model JSON files and textures are preserved.
- Texture files are also copied into the native `textures/blocks` layout. Six translations use native locale names and item-name keys.

## Build and evidence

`tools/build_display.py` is the build path used for this artifact: Java 8 compilation against the unmodified Forge 1.7.10 API, then MCP-to-SRG reobfuscation with SpecialSource. Configure `.toolchain/toolchain.json` using `tools/toolchain.example.json` and a prepared Forge SDK. The local project already has that configuration. Run `python tools/build_display.py`, or `./build.ps1 -Python <python.exe>`. A conventional ForgeGradle project is also supplied; its build path was not used to certify this artifact.

`python tools/build_display.py --tests` builds disposable QA code. `python tools/verify_display.py` runs the dedicated-server/restart checks. `python tools/verify_display.py --client` runs an actual client and saves gallery/inventory screenshots. These scripts require the configured native Forge runtime. Tests never run in the user's existing instance.

See `docs/TESTING.md`, `docs/upstream.json`, `docs/asset-export.json` and `docs/release-manifest.json` for the pinned source, checks and artifact hashes. The sources ZIP includes the native implementation, exporter, model inputs and tests. The retained upstream checkout is in `upstream/displaydelight-1.20.1`.

Original source: https://github.com/jkvin114/displaydelight-1.20.1-neoforge (commit recorded in `docs/upstream.json`). Original project: https://www.curseforge.com/minecraft/mc-mods/display-delight.

## Credits and license

Original Display Delight code and models: jkvin114. Derived Farmers 3D models: Pder, under the included MIT notice. Original project credits also name MB-3D Items. 1.7.10 port: Zahin. Keep the original `LICENSE.txt` (LGPLv3 and MIT notice), `COPYING` (GPLv3) and `THIRD_PARTY.md` with redistributed copies and provide corresponding source under the included license terms.
