# Movement dispatch and gravity — 2026-11-16

Typed movement, block-contact callbacks, gravity controls, root NBT and client synchronization now compile against Forge 10.13.4.1614. Eighteen new verification groups pass: one transformer group, ten dedicated-server groups, one process-restart group and six actual client/integrated-server groups.

## Implemented boundaries

- MoverType retains the original five names. Native three-coordinate movement reaches the converted mob's typed override once; its super call enters native collision physics without recursively redispatching. Reentrant movement from block callbacks still reaches the converted override. Scoped state is restored after exceptions.
- LegacyEntityMob supplies the typed extension point used by the original 24 mob overrides. Four explicit projectile calls use MovementAccess. The native protected block-contact method is exposed through a narrow generated bridge, preserving the explicit callbacks needed by no-clip mobs.
- Explicit PISTON moves retain the original per-axis, per-world-tick 0.51 displacement cap, tiny-movement rejection, axis priority and no-clip bypass. SELF and PLAYER retain native player sneak/ledge protection; forced shulker moves bypass that protection. Native 1.7 piston callers continue to use their native movement API.
- Gravity state lives in compact synthetic Entity fields. It does not allocate DataWatcher IDs or change existing typed entity-data keys.
- Narrow native physics hooks suppress gravity subtraction while retaining motion, drag and other native behavior. Covered paths are living-entity land/water/lava movement and native item, XP orb, TNT, falling-block, arrow and throwable updates. The original custom projectile gravity checks also use the shared flag.
- NoGravity is saved at the original root NBT key only when true. Loading an absent key clears the flag. Read/write hooks run before subclass persistence callbacks. Movement scopes, piston bookkeeping and synchronization revisions are transient.
- The initial SRP mob flag and revision travel in native FML additional spawn data, before the client's first physics update. This prevents the first-tick drift found during client testing. Later changes and tracking snapshots use a registered Forge channel, with dimension, entity ID, server identity, class and revision checks.
- Player classes are normalized at the wire boundary because native server players and client player entities have different implementation classes. Client handlers enqueue changes for application on the client tick. Missing snapshots request an authenticated refresh from the server; leaving tracking range or disconnecting clears bindings.

Ten new helper units bring the helper total to 88. The parsed source migration covers 116 boundaries in 34 original files: 28 imports, 57 gravity setters, three gravity reads, 24 block-contact calls and four typed movement calls. The original typed super calls remain in place. Comments, literals and unrelated statements were retained.

No new access-transformer field rules were needed. One new runtime transformer handles native Entity state/dispatch/NBT and the seven gravity-bearing class families.

## Evidence

The transformer checks prove that all eight target classes transform, repeated transformation is idempotent, and partial pre-existing hooks are rejected.

The ten dedicated-server groups prove:

1. Native movement invokes the typed override once and preserves its native super call.
2. Reentrant block contact and explicit no-clip contact callbacks.
3. Scope restoration after a movement exception.
4. Gravity toggles with native drag across 600 land/water/lava cases.
5. Real native item, XP, TNT, falling-block, arrow and throwable updates honor the flag.
6. Explicit piston limits, tick reset and no-clip bypass.
7. Player ledge protection depends on movement type.
8. Root NBT, subclass callback ordering, missing-key clearing and omitted false flags.
9. Gravity packet identity, kind, revision and flag round-trips, with trailing-data rejection.
10. Snapshots arriving before entity creation, deltas, removal and stale revisions.

A second dedicated-server process reloads the saved entity, verifies its gravity-free physics and then restores gravity.

Six actual client groups prove initial flag delivery to a distinct client entity without drift, live toggling and falling, tracking removal, fresh tracking re-entry, repair of a missing snapshot through the real resynchronization channel, and delivery to the owning player despite native player-class differences.

The final server and client test modules contain identical bytes for all 152 production helper/core-plugin classes, including the initial spawn-data fix. Test modules and worlds are disposable; user mods and saves were not modified.

The earlier flight suite was also rerun against the new native hooks. Its six groups still pass, including all 25 original controller comparisons over 24,000 updates and 50,169 steering inputs. Those are regression checks, not six additional unique verification groups.

Commands:

    python tools/verify_native_movement.py
    python tools/verify_movement_client.py
    python tools/verify_native_flight.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory
    python tools/write_remaining_errors.py

## Scope and remaining work

Captured diagnostics decreased from 5,705 to 5,565. The entity package has 1,487 diagnostics; EntityParasiteBase still has 87. All 88 helpers compile with zero helper diagnostics. There are 1,182 main Java units, including all 1,094 recovered originals. All 4,501 original assets remain unchanged.

The complete mod still does not compile or run. Javac exits abnormally with code 4, so the inventory remains non-exhaustive.

This bridge retains native 1.7 collision, stepping and fluid mechanics rather than replacing the entire engine with 1.12 physics. Other native entity families with custom gravity code, complete modern navigation, original mob gameplay, equipment/combat, rendering/custom particles, specialized blocks/world generation, progression/persistence and the remaining potion/UI systems are unfinished. Initial FML spawn-state delivery is installed on LegacyEntityMob; other entity types use the tracking snapshot channel. Full original mobs and HBM modpack compatibility have not been validated.

The small Zahin port credit and original author/audio credits remain. No release JAR has been installed or published.

Evidence: test-results/2026-11-16-movement/manifest.json. Current source review: review/movement-2026-11-16.zip. Full captured diagnostics: ALL_REMAINING_ERRORS.md.
