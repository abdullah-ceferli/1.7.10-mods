# Native navigation — 2026-11-16

The requested `PathNavigateGround`, `PathNavigateFlying`, `PathNavigateSwimmer` and `PathNavigateClimber` are implemented on Forge 1.7.10. Independent helper compilation and native fixtures pass. The complete mod still fails to compile and is not playable.

## Scope

Only navigation production code changed: 16 new helper units, the navigator factory/path-priority methods in `LegacyEntityMob`, one transformer registration in the existing loading plugin, and navigation imports/call sites in 23 original source files. Existing resources, recipes, unrelated item/combat/potion logic and credits are unchanged. `navigation-scope-audit.json` and `navigation-source-changes.diff` record the exact existing-file changes.

Native `EntityLiving` owns a private navigator and its AI scheduler calls that field directly. Merely overriding a getter would leave the scheduler on the old ground navigator. `NavigationTransformer` replaces only its constructor allocation with an opt-in factory, at the same initialization boundary as the modern navigator. The native field receives the result before senses and server AI goal setup. Ordinary native mobs keep the native navigator. Original `createNavigator(World)` overrides now dispatch; ordinary ported mobs default to ground navigation. No new flying navigator is forced onto mobs whose original code does not select one.

Native goals and the new navigators share actual `PathEntity`/`PathPoint` values and the same path cursor. Seven original consumers of modern `Path` now use native `PathEntity`; the native class named `Path` is a search heap, not a route. Native clear/set/speed calls operate on the new navigator's state. The original `PathNavigateClimberStatus` status threshold and fallback remain intact; its inherited fields and distance helper now resolve.

## Implemented behavior

| Navigator | Behavior |
| --- | --- |
| Ground | Costed walking search; body dimensions; step/fall limits; water priorities; doors; hazards; sunlight avoidance; cauldron adjustment; native movement commands and stuck handling. |
| Flying | Three-dimensional node search, dimension-aware edges, vertical following and body-sized obstacle checks for shortcuts. |
| Swimmer | Six-direction water-only search over the entity's occupied volume, submerged obstacles, vertical following, dry/lava rejection and water-only shortcuts. |
| Climber | Ground routes when available and retained target-directed steering when no ground route exists; climbing physics remains the mob's ladder/contact behavior. |

The search heap, point costs, priority defaults, 200-iteration loop bound and node-expansion rules were inspected from the locally verified 1.12.2 reference. Native block metadata/shape access replaces block states. Door checks combine upper/lower metadata, and the processor's opening/entering flags affect the actual search. Path priorities are per entity and shared with converted callers; temporary water-priority state is restored after a search. Native custom blocks are queried through their movement/collision/shape APIs. Native full-block and top-solid queries are approximated through shape/render and Forge side-solid contracts; arbitrary modded shapes still require integration testing.

This is a native adaptation, not a claim of exact vanilla 1.12 navigation equivalence. Two deliberate corrections were required by the obstacle tests: the original flying shortcut routine did not test blocks, and its ground-style follower could advance to a horizontal waypoint while still rising beside a wall. Flying/swimming edges and shortcuts now sweep the whole body. Their follower uses three-dimensional arrival, checks the next segment before advancing, and centers the body vertically within the processor's reserved cells. Swim shortcut checks also prevent crossing dry gaps. Blocks absent from 1.7.10 (such as vanilla magma/iron trapdoors) are not invented.

## Verification

- 15 dedicated-server groups: all four constructor choices, native-mob opt-out, ground detours, stalled routes, door metadata/flags, per-entity water priorities, native shapes/hazards, vertical flying routes, corner/width clearance, submerged routes, dry/lava rejection, climbing fallback, actual wall climbing, shared native path state, and real ground/flying/swimming collision travel. Several related assertions share a group.
- 2 exact-native-bytecode transformer groups: installation order/idempotence/opt-out and rejection of changed constructor/hook shapes.
- 2 actual Forge client groups: all four tracked client navigator types without client AI initialization; server-driven flying arrival around an obstacle delivered through native entity tracking.
- Existing melee checks rerun: 13 groups including original bytecode comparisons and actual native pathfinding/damage.
- Existing lifecycle checks rerun: 12 groups including transformer checks, initialization order and saved-entity reconstruction after a dedicated-server restart. These are regressions, not new groups.

Ground/climbing fixtures use native movement physics. Flying/swimming fixtures use a small test movement controller through the production controller interface, native gravity handling and native collision travel. These are test entities, not compiled original SRP mobs. The swimmer fixture uses the infected squid's 0.9-by-0.9 dimensions; an initial 0.6-high fixture exposed vanilla water-detection behavior and was corrected without changing product water detection.

An initial client attempt crashed in the native `OpenAL64.dll` while creating the audio context, before gameplay tests. The successful navigation-only client run uses `ALSOFT_DRIVERS=null` in that test process. No user audio settings, shared native libraries or product audio code were changed. This stage does not retest sound hardware.

## Compiler result and limits

Both complete compiler passes use the same diagnostic settings: **8,132 → 8,034**, **98 removed, zero added**. Entity-package diagnostics are **1,457 → 1,379**; `EntityParasiteBase` is **87 → 81**; helpers remain at zero. The status-gated climber now has zero diagnostics. All 1,204 main source hashes match the report.

Remaining full-mod work includes entity hands/equipment/NoAI, custom combat, rendering, world generation, inventories/persistence, brewing/HUD and original registration. General modern world path-listener/debug infrastructure is not supplied here. No original SRP mob, complete modpack or HBM compatibility has been verified end to end. No release JAR was produced or installed.

Run `python tools/verify_native_navigation.py`, then `python tools/verify_navigation_client.py` for the isolated tests. Run the full compile probe before regenerating the remaining-error report. See `test-results/2026-11-16-navigation/manifest.json` for artifact hashes and retained logs; `review/navigation-2026-11-16.zip` contains the matching source snapshot.
