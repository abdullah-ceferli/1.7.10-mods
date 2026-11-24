# Industrial Technology 2.16.0

The Building Stick can copy configured decorative Carpenter's Blocks, including Chisel-covered slopes, while retaining shape, facing, cover metadata and per-face decoration. Selecting and costing the copy never calls mutating drop helpers on the original. Normal native placement reshaping is bypassed, preserving the chosen orientation and neighboring slopes.

Survival pays the Carpenter item plus all cover/decoration materials, with complete-set checks and cancellation refunds. New coordinates and placer ownership replace source identity. Containers and multi-block Carpenter objects remain excluded. Locations that would remove an existing Carpenter side cover are skipped. Ordinary block/liquid behavior and work budgets remain unchanged.

The optional integration adds no bundled Carpenter/Chisel code or textures, renderer, ticking tile or custom packet format. A specific native sneak-use handler prevents Carpenter's alternate activation from consuming the selection. Existing originals remain untouched. See [the Building Stick guide](../BUILDING_STICK_GUIDE.md) and [release evidence](test-results/2.16.0/release.json).

Validation uses actual Carpenter's Blocks 3.3.8.2, Chisel 2.9.5.11, HBM X5771 and client NEI in disposable worlds, with fresh-JVM reloads. Android testing remains pending. Install matching Industrial Technology 2.16.0 on both client and server; game installation is manual.
