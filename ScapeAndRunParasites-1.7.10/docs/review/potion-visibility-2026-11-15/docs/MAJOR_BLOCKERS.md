# Major remaining blockers — 2026-11-15

The full mod still fails: 6,827 captured diagnostics and abnormal javac exit 4. Counts are not exhaustive.

| Source area | Reported diagnostics |
| --- | ---: |
| entity | 2429 |
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

backport is the tested helper package. compatibility is the original cross-mod integration package.

Native potion IDs, accepted-application/finish hooks, particle-color filtering, effect-state reload and client tracker synchronization are implemented and tested. HUD visibility, potion items/types and brewing, glowing/levitation and remaining native effect-method signatures are unfinished. Other major areas are entity AI/combat/equipment, client rendering, specialized blocks/world generation and persistence.

See POTION_VISIBILITY_PORT.md, ALL_REMAINING_ERRORS.md and major-blockers.json. No playable full mod exists.
