# Vanilla melee goal — 2026-11-16

The modern vanilla melee goal now compiles against Forge 10.13.4.1614 and is connected to its seven original SRP callers. Thirteen dedicated-server groups and two actual client/integrated-server groups pass. The full mod still does not build or run.

## Implemented behavior

EntityAIAttackMelee uses native EntityCreature, PathNavigate, PathEntity, sensing, look control, swing and damage operations. It retains the original three-argument constructor, mutex bits, protected attack/reach extension points and twenty-update attack cooldown.

The old collision-attack goal is not a direct replacement. Its activation/path retry behavior differs, it lacks the modern fallback for an unreachable target already within attack reach, and its attack cooldown condition differs. The new implementation preserves the modern goal's own decision order rather than inheriting those differences.

Activation rejects missing/dead targets, queries a path on each attempt and falls back to reach when no path exists. Short-memory continuation follows native path state; long-memory continuation checks the home boundary using floor(posX), floor(posY), floor(posZ). Starting supplies the selected path and configured speed. Reset clears navigation and drops excluded player targets.

Updates retain the original target-coordinate cache, sight/long-memory gates, movement threshold, random retry, distance-based delay additions and failed-path delay. Attack distance uses the target bounding box's minimum Y. Reaching a target triggers a swing even with an empty hand, followed by native attack dispatch. A failed damage attempt still consumes the cooldown. The original float evaluation order is retained for reach.

Native player policy excludes creative or damage-immune players during long-memory continuation and reset. Minecraft 1.7.10 has no spectator game mode; this port does not add one. The original spectator query is represented in the comparison fixture by non-creative native damage immunity, and its creative query by native creative capabilities. This is an explicit target-platform policy, not evidence of a complete spectator implementation or exact behavior for every other mod's player state.

Only seven imports changed. Constructor arguments, task priorities and original mob logic remain intact in EntityWave, EntityWaveShock, EntityUnvo, EntityLesh, EntityBano, EntityRathol and EntityGothol. The syntax-tree migration audit is melee-source-conversion.tsv. One helper unit brings the total to 92 helpers and 1,186 main Java units.

## Native evidence

The test reference retains the original 1.12.2 melee class instructions. Its builder adapts the class/path/position names, home and bounding-box access, player policy and single-hand native swing. It validates the expected substitution counts and uses native APIs through a test-only boundary independent of the port goal. The decision instructions and random branches remain from the original class.

Eleven groups execute against both the original instruction body and the port, for 22 paired scenario executions:

1. Missing/dead targets, mutex bits and unthrottled activation path queries.
2. Close-range activation and attack without a path.
3. Short/long memory, home bounds and negative/fractional coordinate rounding.
4. Native creative/damage-immunity combinations, target retention and reset.
5. Selected path, movement speed, retry-counter reset and navigation clearing.
6. Attacks and empty-hand swings at updates 0, 20, 40 and 60.
7. Rejected damage consuming cooldown and distant targets clamping it at zero.
8. Sight affecting repathing without adding a new close-range attack veto.
9. Bounding-box minimum Y used for activation, attack and recorded path coordinates.
10. Ten thousand randomized reach cases per implementation with matching floating-point results.
11. Twenty thousand updates per implementation with matching retry, target-position, attack and swing state: 40,000 combined updates.

Two further dedicated-server groups use native living updates, real damage, real pathfinding, move helpers and collision travel. They prove constructor goal registration executes through the earlier lifecycle bridge, repeated attacks reduce a normally ticking victim's health, and an attacker reaches a distant target.

Two real client groups prove server-only goal execution, native chasing/damage synchronized to a distinct client victim, and empty-hand swing animations delivered to the client attacker. The final dedicated and client modules contain identical bytes for all 156 production helper/core-plugin classes and for the combat fixture entity classes. Test modules differ only in test-side changes made while completing the dedicated checks.

The recorded total is 252 verification groups, with the three diagnostic-parser checks counted separately. Earlier groups were not rerun or counted again without a new concern requiring them. Tests use disposable .toolchain worlds and profiles; no user game mods or saves are changed.

Commands:

    python tools/verify_native_melee.py
    python tools/verify_melee_client.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory
    python tools/write_remaining_errors.py

## Remaining limits

The full source probe captures 5,535 diagnostics, down from 5,549. The exact entity area falls from 1,471 to 1,457. EntityParasiteBase still has 87, and helpers have zero captured diagnostics. javac still exits abnormally with code 4; the count is not exhaustive and is not a completion percentage.

No complete original SRP mob has been compiled and tested end to end. The original seven callers still depend on unfinished shared entity, equipment, rendering and other APIs. Custom SRP melee/status/AOE goals are separate implementations and remain unfinished. This stage does not replace them with the vanilla goal.

Full modern navigation, NoAI state, equipment/combat boundaries, rendering, specialized blocks/world generation, progression/persistence, potion brewing/HUD and other integration remain incomplete. HBM modpack compatibility has not been tested. There is no playable release JAR, and none has been installed or published.

Evidence: test-results/2026-11-16-melee/manifest.json. Matching source review: review/melee-2026-11-16.zip. Full captured diagnostics: ALL_REMAINING_ERRORS.md.
