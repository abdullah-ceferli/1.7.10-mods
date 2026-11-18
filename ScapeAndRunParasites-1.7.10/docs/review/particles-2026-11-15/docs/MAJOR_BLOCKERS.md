# Major remaining blockers — 2026-11-15

The full mod still fails: 6,448 captured diagnostics and abnormal javac exit 4. Counts are not exhaustive.

| Source area | Diagnostics |
| --- | ---: |
| entity | 2099 |
| client | 1680 |
| block | 935 |
| init | 447 |
| item | 265 |
| bestiary | 261 |
| world | 183 |
| tileentity | 162 |
| compatibility | 102 |
| util | 81 |
| advancements | 62 |
| container | 47 |
| events | 37 |
| potion | 31 |
| network | 18 |
| gui | 13 |
| feature | 9 |
| recipes | 6 |
| proxy | 6 |
| mixins | 3 |
| fluid | 1 |
| backport | 0 |

Port native sound constants, playback categories and sound packets directly. Continue entity AI/navigation/equipment/combat, original SRP custom particles and rendering, specialized blocks/world generation, persistence and the abnormal compiler exit. HUD visibility, potion items/types/brewing and missing effects remain. No playable full mod exists.

Armor and the vanilla particle compatibility boundary now pass actual native checks. backport is the helper package; compatibility is the original cross-mod integration package. See PARTICLE_PORT.md, ARMOR_PORT.md and ALL_REMAINING_ERRORS.md.
