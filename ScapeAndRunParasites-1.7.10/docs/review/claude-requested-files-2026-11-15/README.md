# Requested source files for Claude

Project: Scape and Run: Parasites 1.10.9, converted toward Minecraft 1.7.10 / Forge 10.13.4.1614. Snapshot: 2026-11-15.

These are the current converted files. CFR headers are preserved provenance, not evidence that a file is the untouched 1.12 reference. Original relative paths and source bytes are preserved in this bundle.

The compatibility package is named backport, not compat. It contains 37 Java files; the current full compiler snapshot reports 102 diagnostics in that package. The full mod does not compile and is not playable.

## Start with these files

| Purpose | File relative to the bundle root |
| --- | --- |
| EntityParasiteBase.java | [src/main/java/com/dhanantry/scapeandrunparasites/entity/ai/misc/EntityParasiteBase.java](src/main/java/com/dhanantry/scapeandrunparasites/entity/ai/misc/EntityParasiteBase.java) |
| EntityQueries.java | [src/main/java/com/dhanantry/scapeandrunparasites/backport/entity/EntityQueries.java](src/main/java/com/dhanantry/scapeandrunparasites/backport/entity/EntityQueries.java) |
| Vec3d.java | [src/main/java/com/dhanantry/scapeandrunparasites/backport/math/Vec3d.java](src/main/java/com/dhanantry/scapeandrunparasites/backport/math/Vec3d.java) |
| BlockPos.java | [src/main/java/com/dhanantry/scapeandrunparasites/backport/math/BlockPos.java](src/main/java/com/dhanantry/scapeandrunparasites/backport/math/BlockPos.java) |
| EntityDataManager.java | [src/main/java/com/dhanantry/scapeandrunparasites/backport/datasync/EntityDataManager.java](src/main/java/com/dhanantry/scapeandrunparasites/backport/datasync/EntityDataManager.java) |
| NativeEntityDataSync.java | [src/main/java/com/dhanantry/scapeandrunparasites/backport/datasync/NativeEntityDataSync.java](src/main/java/com/dhanantry/scapeandrunparasites/backport/datasync/NativeEntityDataSync.java) |
| EntityLodo.java | [src/main/java/com/dhanantry/scapeandrunparasites/entity/monster/inborn/EntityLodo.java](src/main/java/com/dhanantry/scapeandrunparasites/entity/monster/inborn/EntityLodo.java) |
| RenderLodo.java | [src/main/java/com/dhanantry/scapeandrunparasites/client/renderer/entity/inborn/RenderLodo.java](src/main/java/com/dhanantry/scapeandrunparasites/client/renderer/entity/inborn/RenderLodo.java) |
| ModelLodo.java | [src/main/java/com/dhanantry/scapeandrunparasites/client/model/entity/inborn/ModelLodo.java](src/main/java/com/dhanantry/scapeandrunparasites/client/model/entity/inborn/ModelLodo.java) |
| ModelSRP.java | [src/main/java/com/dhanantry/scapeandrunparasites/client/model/ModelSRP.java](src/main/java/com/dhanantry/scapeandrunparasites/client/model/ModelSRP.java) |
| SRPPotions.java | [src/main/java/com/dhanantry/scapeandrunparasites/init/SRPPotions.java](src/main/java/com/dhanantry/scapeandrunparasites/init/SRPPotions.java) |
| SRPSounds.java | [src/main/java/com/dhanantry/scapeandrunparasites/init/SRPSounds.java](src/main/java/com/dhanantry/scapeandrunparasites/init/SRPSounds.java) |

## Included context

- All existing backport classes, including position/vector/state helpers and typed entity-data transport.
- All entity/ai classes and custom potion classes, plus SRPAttributes, SRPEntities, main/proxy lifecycle files and EntityMudo, which EntityLodo references.
- The full 7,341-diagnostic JSON/log and selected-source-errors.json containing diagnostics only for included source files.
- Real compatibility test sources for entity data, entity queries/access and position parity, for understanding existing tested boundaries.
- The original sounds.json and original sound identifier audit; Lodo texture and loot table.
- Current build.gradle, access transformer, parser/ranking tools and review/status documents.

## Mob example

EntityLodo is a relatively small 240-line direct subclass of EntityParasiteBase. It includes retaliation/swimming/avoidance AI, growth and NBT behavior. RenderLodo and ModelLodo are included together with ModelSRP. This is a starting review example, not a validated running mob or a self-contained build project.

RenderLodo also references srparasites:textures/entity/monster/slodo.png for skin 120. That file is absent from the current preserved resource tree; it was not fabricated or substituted in this bundle.

## Important existing boundaries

Reuse EntityQueries.position and the tested Vec3d adapter. Entity synchronization uses a separate typed manager and Forge transport; do not replace it with a blanket fixed-ID native DataWatcher conversion. See docs/ENTITY_SYNC_PORT.md and docs/CLAUDE_ANALYSIS_REVIEW.md.

Global difficulty getters can map to the old field, but local DifficultyInstance/spawn behavior needs its full inputs and native lifecycle bridge. Equipment-slot enums do not solve SharedMonsterAttributes.ARMOR. Preserve original gameplay behavior rather than deleting unsupported systems.

## Diagnostics and verification

All 139 included main Java files match compiler source hashes. Every one of the 757 selected diagnostics matches its exact printed source line. EntityParasiteBase has 124 diagnostics. The complete compiler exited abnormally with code 4, so the total is not guaranteed exhaustive.

manifest.json records SHA256 hashes for every other bundle file. No source or installed game files were changed while packaging. No files have been uploaded or sent to Claude.

