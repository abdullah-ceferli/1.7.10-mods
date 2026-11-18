# Review of the entity-error analysis — 2026-11-15

Latest update: the spawn boundary and initial potion ID/native field mappings are implemented; see [POTION_ID_PORT.md](POTION_ID_PORT.md). Current base-entity diagnostic count: 113. Earlier sections remain historical analysis.

Current update: difficulty/spawn migration is now implemented and verified; see [DIFFICULTY_PORT.md](DIFFICULTY_PORT.md). The full probe now reports 7,145 diagnostics (117 base entity). The earlier analysis below describes the pre-difficulty snapshot.

The grouping identified useful shared causes. Source inspection confirms the access fields, position mismatch and raw native lists. Several other suggestions require more than constructor/name adaptation to preserve original behavior.

## Implemented and verified

The compiler JSON now captures complete diagnostic details, symbol, location, required/found arguments and reasons. Two parser regression checks pass. Rank actual missing entity types with:

    python tools/rank_diagnostics.py --area entity --kind class --limit 30

Six narrow SRG access rules expose EntityLiving.moveHelper, EntityMoveHelper's three coordinates/speed and GuiSlot.mc. A real FML transformer builds a private compile API copy; exactly three native classes change and all final flags remain intact. ForgeGradle discovers the resource and the JAR manifest supplies it at runtime. Actual server movement and client GUI tests pass. The shared native cache is unchanged.

Modern entity getPosition returns floor(x), floor(y+0.5), floor(z). It is not the old client-only interpolated vector method. The helper matches actual Minecraft 1.12 bytecode over 5,000 cases; 273 calls now use it.

196 native query calls and 69 loaded/player-list reads now retain modern generic types. Native list identity is preserved, predicates run through the real query, and null predicates retain native results. Tests execute converted source in a genuine Forge server.

Thirteen malformed recovered negative Object casts were repaired after checking DataParameter<Integer> declarations and original Integer.valueOf bytecode. This is not a blanket int-to-byte conversion.

There are 95 aggregate verification groups, plus two diagnostic-parser regression tests. All original source units/assets remain.

## Remaining proposals

| Proposal | Source review |
| --- | --- |
| HurtByTarget constructor | The third argument filters recruited helpers. Modern recruitment bounds and tame-owner behavior also differ. Discarding the array is insufficient. |
| PotionEffect factory | Native integer IDs can bridge constructors, but the fifth show-particles flag has no equivalent in the old constructor. Persistence and client visibility need explicit support. |
| Vec3d rename | The verified Vec3d adapter already exists. Remaining native Vec3 mismatches need conversions at API boundaries; globally renaming it would undo modern immutable-vector behavior. |
| fall(distance,multiplier) | The multiplier affects damage calculation. Dropping it or blindly scaling distance changes threshold/rounding behavior. |
| BossInfo | Native BossStatus has a different global-bar model. Removing bars would lose requested content; preserving modern tracked bars needs a real UI/network port. |
| int/byte mismatches | Need field/key types and original bytecode checks. The thirteen negative-boxing corrections do not address every narrowing diagnostic. |
| PathNavigateClimber | Climbing navigation needs its stored-target/fallback movement behavior, not an empty class with the old name. |
| Base class first | Still the right priority, but EntityParasiteBase remains unbuildable with 124 reported diagnostics. No complete original SRP mob is validated yet. |

Incremental syntax-tree migrations preserve the existing port work. The original preparation script remains a one-time migration.

## Current reported missing types in the entity package

| Type | Reported occurrences |
| --- | ---: |
| DifficultyInstance | 163 |
| EnumParticleTypes | 83 |
| MobEffects | 55 |
| MoverType | 52 |
| SoundEvents | 44 |
| BossInfoServer | 40 |
| EnumHand | 22 |
| EntityAreaEffectCloud | 18 |
| SoundCategory | 15 |
| EntityAIAttackMelee | 14 |
| PathNavigateGround | 12 |
| BlockEndGateway | 10 |

The latest full probe reports 7,341 diagnostics, including 2,864 in the entity package. Before this step the corresponding reported counts were 7,869 and 3,372. Private-access diagnostics fell from 264 to zero. These counts are not exhaustive because javac still exits abnormally with code 4.

The major remaining work is spawning/difficulty and AI, movement/equipment APIs, effects, particles/rendering, specialized blocks and persistence. The full mod is still not playable.

Evidence: docs/test-results/2026-11-15-entity-common/manifest.json and docs/entity-common-diagnostic-comparison.json. Native checks: tools/verify_native_entity_common.py and tools/verify_entity_common_client.py. Reference check: tools/verify_entity_positions.py after tools/verify_foundation.py. Rebuild the private compile API with tools/prepare_compile_access.py when access rules change.
