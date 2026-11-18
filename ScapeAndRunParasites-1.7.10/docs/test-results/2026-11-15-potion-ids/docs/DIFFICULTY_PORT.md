# Difficulty and native spawning bridge — 2026-11-15

Update: the three native EntityLiving calls listed in the earlier snapshot below are now converted and tested. See [POTION_ID_PORT.md](POTION_ID_PORT.md) for the current spawn closure and 7,056-diagnostic snapshot.

The requested difficulty migration is implemented. Both new helpers compile against actual Forge 1.7.10. All 79 DifficultyInstance imports, 44 local-difficulty queries and 21 global-difficulty getters were converted in 108 original source files. The native shared mob base also now connects onSpawnWithEgg to converted onInitialSpawn overrides.

## Confirmed native API

The private access-transformed compile API exposes all four names without needing additional access rules:

| Native name | Declaration |
| --- | --- |
| World.difficultySetting | public EnumDifficulty field |
| WorldProvider.moonPhaseFactors | public static final float[] field |
| WorldProvider.getMoonPhase(long) | public method returning int |
| Chunk.inhabitedTime | public long field |

## Implementation

- backport/world/DifficultyInstance.java retains the original local calculation, its float operation order, clamped additional difficulty and strict isHarderThan comparisons.
- backport/world/DifficultyAccess.java reads global difficulty directly. Local lookup combines the original day time, loaded chunk inhabited time and provider moon factor. It reuses WorldAccess.isBlockLoaded and does not generate unloaded server chunks.
- The original calculation uses World.getWorldTime(), not World.getTotalWorldTime(). This distinction was checked against original Minecraft 1.12.2 bytecode and exercised on the native server.
- The requested entity-position conversions reuse EntityQueries.position; existing BlockPos arguments stay intact. EntityPInfected retains this as the position receiver, as in its original call.
- LegacyEntityMob.onSpawnWithEgg computes local difficulty and dispatches to onInitialSpawn once. Calling onInitialSpawn directly retains the supplied difficulty and delegates native initialization through super.onSpawnWithEgg without re-entering that dispatcher.
- Syntax-tree edits preserve strings/comments and record each old expression and replacement in difficulty-calls.tsv. The after-preview contains zero remaining migration matches. Existing Vec3d and typed entity-data transport were retained.

The conversion audit is docs/difficulty-conversion.json. The pre-change sources and compiler inventory are checkpointed under the path recorded by .toolchain/difficulty-checkpoint.txt.

## Verification

The reference comparison matches actual original Minecraft 1.12.2 results bit for bit for 23,200 boundary/random cases. It covers all four difficulty levels, time/inhabited-time saturation, negative/extreme long values, moon factors, NaN/infinities, clamped values and strict threshold comparisons.

Four reobfuscated checks pass in a fresh disposable native Forge 1.7.10 dedicated server:

1. Native global difficulty, day-time selection and all eight provider moon phases feed the local calculation.
2. An unloaded chunk contributes zero inhabited time/moon factor and remains unloaded.
3. Signed entity coordinates select the correct negative-coordinate chunk.
4. Native and explicit modern spawning dispatch once and retain the supplied group data.

The complete 39-unit compatibility package compiles for both checks. Original input hashes, all 4,501 original assets and all 1,094 original source units remain preserved. No installed mod or user save was changed.

Commands:

    python tools/verify_difficulty.py
    python tools/verify_native_difficulty.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory

## Current limits

The full probe now reports 7,145 diagnostics, down from 7,341. The exact entity package reports 2,676, down from 2,864; EntityParasiteBase reports 117, down from 124. There are no reported compatibility-package diagnostics in this snapshot. These are captured messages, not independent bug counts or completion percentages. Javac still exits abnormally with code 4, so more errors may remain.

Three modern spawn calls are still made through native EntityLiving variables: EntityRof.java:157 and EntityBiomass.java:301,332. Those require an explicit native/converted-entity spawn boundary; the current bridge covers descendants of LegacyEntityMob.

Shared AI/navigation, combat armor attributes, equipment/effects, rendering, specialized blocks/world generation, persistence and complete registration/gameplay still require work. No complete original SRP mob or playable full mod is claimed by these fixture checks.

The current full report is ALL_REMAINING_ERRORS.md. Older review ZIPs and test-result folders retain their original source snapshots and line numbers.
