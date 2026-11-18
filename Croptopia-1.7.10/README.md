# Croptopia for Minecraft 1.7.10

Native Java 8 / Forge 10.13.4.1614 adaptation of the pinned original 4.0.1 source.

Requires the tested Farmer's Delight Classic 1.1.1 build and UniMixins in `vendor/runtime` (exact hashes and addon API requirements are recorded in `docs/runtime-dependencies.json`). Original registry names, assets, food values and supported recipes are retained. No replacement recipe-book UI is added.

Build: `python tools/build.py`. Runtime prerequisites and toolchain are recorded in `.toolchain/toolchain.json`.

The current build is an initial tested compatibility build, not a complete reproduction of every modern integration. See `docs/STATUS.md` and the generated `config/croptopia/compatibility.json`.

Backport maintenance: Zahin. Original authors and licenses are preserved under `licenses/`.
