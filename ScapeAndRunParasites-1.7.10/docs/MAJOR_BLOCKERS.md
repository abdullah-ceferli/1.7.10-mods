# Major remaining blockers — 2026-11-16

The full mod fails with **8,034 errors**. The compiler completes its diagnostic pass normally; all printed records match its footer. Navigation removes 98 diagnostics from the previous complete 8,132-record pass, with no new records. Missing APIs can still cause cascades or hide later defects.

| Source area | Diagnostics |
| --- | ---: |
| client | 1672 |
| entity | 1379 |
| world | 1042 |
| util | 862 |
| block | 796 |
| item | 739 |
| init | 387 |
| bestiary | 256 |
| tileentity | 211 |
| network | 191 |
| potion | 128 |
| compatibility | 102 |
| proxy | 89 |
| advancements | 52 |
| container | 47 |
| events | 33 |
| recipes | 20 |
| gui | 13 |
| feature | 9 |
| mixins | 4 |
| fluid | 1 |
| SRPMain.java | 1 |
| backport | 0 |

Finish the remaining shared entity lifecycle/NoAI, hands/equipment and custom combat, then validate an original mob end to end. The four requested navigation implementations now pass isolated native checks; original-content integration remains unverified. Rendering/model properties, world generation, stack/capability APIs, saved-data persistence/progression, potion brewing/HUD and full registration still need work. Build the complete mod and validate it with HBM. No playable full mod exists.

EntityParasiteBase has 81 diagnostics. All navigation helpers compile independently and the original status-gated climber source has no diagnostics in this pass. This does not prove a complete original mob works in game. The original compatibility package still has 102 errors; backport helpers have zero.

Read NAVIGATION_PORT.md for the scoped change and native tests. ALL_REMAINING_ERRORS.md includes every current printed error.
