# Major remaining blockers — 2026-11-15

The full mod still fails: 6,683 captured diagnostics and abnormal javac exit 4. This is not exhaustive.

| Source area | Diagnostics |
| --- | ---: |
| entity | 2285 |
| client | 1691 |
| block | 958 |
| init | 447 |
| item | 274 |
| bestiary | 261 |
| world | 184 |
| tileentity | 162 |
| compatibility | 102 |
| util | 83 |
| advancements | 62 |
| container | 47 |
| events | 37 |
| potion | 33 |
| network | 19 |
| gui | 13 |
| feature | 9 |
| recipes | 6 |
| proxy | 6 |
| mixins | 3 |
| fluid | 1 |
| backport | 0 |

Continue the particle API conversion directly, then native sound playback/categories. HUD visibility, potion items/types/brewing, missing effects, entity AI/equipment/combat beyond armor, rendering, specialized blocks/world generation and persistence remain. Resolve the abnormal compiler exit before full-mod build/play validation.

The backport helper package and original compatibility cross-mod package are different. Armor/toughness, native damage, equipment mapping, restart and real client tracking now pass. See ARMOR_PORT.md and ALL_REMAINING_ERRORS.md. No full playable mod exists.
