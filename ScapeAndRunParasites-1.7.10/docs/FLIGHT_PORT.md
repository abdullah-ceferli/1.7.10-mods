# Custom flight controllers and steering math — 2026-11-15

All 25 original SRP custom flying-controller bodies now compile against the native Forge movement interface. Six dedicated-server verification groups pass, including comparison with the actual original controller bytecode over 24,000 update steps and the original steering calculation over 50,169 inputs.

## Changes

FlightMoveHelper bridges the modern action state used by the custom controllers to the native EntityMoveHelper interface. It retains zero-initialized command coordinates, command speed, WAIT/MOVE_TO state transitions and native virtual method dispatch. The original controller bodies continue to handle acceleration, arrival, momentum and yaw. The bridge is abstract, so it cannot silently replace those bodies with native ground movement.

The source migration changes 25 controller parent types and 50 action references. Local variables that hold the native EntityMoveHelper remain compatible. Every migrated controller already overrides onUpdateMoveHelper; the syntax-tree tool checks that requirement.

ModernMath retains the original lookup-table atan2 calculation and its inverse-square-root approximation. Substituting Math.atan2 would produce different steering values. All 65 original missing MathHelper.atan2 calls now use this implementation. There are 140 parsed edits across 35 source units. Comments, string literals and unrelated code were preserved.

The four action enum names are retained, but no general modern ground/strafe navigation engine is supplied by this flight bridge. The original SRP flying bodies use WAIT and MOVE_TO. No global replacement of native controllers or MathHelper was installed.

The existing seven access-transformer field rules remain sufficient. No new access rules or runtime transformers were added.

## Verification

The runner extracts each actual converted AIMoveControl body with the Java syntax tree and compiles it inside a controlled native host entity. These are test hosts, not complete original SRP mobs.

An independent reference builder retains all 25 original controller class bodies from the verified original SRP JAR. It also retains the required original movement-command methods, action enum, steering methods and table initialization from the original Minecraft 1.12.2 class bytes. Only class/member names, host entity access and the native bounding-box accessor are adapted. Original steering instructions, acceleration, branches and state changes remain.

The production test module is reobfuscated and runs on an actual Forge 10.13.4.1614 dedicated server. Six groups prove:

1. atan2 matches original bytecode for 169 edge pairs and 50,000 randomized pairs, including signed zero, non-finite values and extreme magnitudes. Values and exceptional behavior are compared.
2. Native interface calls preserve zero initialization even when creating controllers for entities at nonzero coordinates, command coordinates/speed and updating state.
3. All 25 converted controllers match their original class bytecode over 120 cases and eight updates per case: 24,000 updates. Motion, rotation, body yaw and action state are compared exactly.
4. Arrival changes to WAIT, halves all momentum components and accepts a subsequent native movement command.
5. Flam's movement lock preserves and later releases its pending command.
6. Native PathNavigate delivers a real path command through EntityMoveHelper, setting modern action state and accelerating the custom controller.

Final result: six passing groups, zero failed groups. Evidence includes the source hashes, original reference analysis, generated controller test sources and native server log.

Commands:

    python tools/verify_native_flight.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory
    python tools/write_remaining_errors.py

## Current limits

Captured diagnostics decreased from 5,870 to 5,705: 100 missing controller action diagnostics and 65 missing atan2 calls are cleared. The entity package has 1,626 diagnostics; EntityParasiteBase still has 87. All 78 helper units compile, with zero helper diagnostics. All 1,094 original Java units and 4,501 original assets remain.

The complete mod still does not compile or run. Javac exits abnormally with code 4; the inventory is not exhaustive and does not establish a completion percentage.

Movement-type dispatch, gravity controls and persistence, full flying/ground navigation, collision behavior through the complete original mob lifecycle, equipment/combat, rendering/custom particles, world generation, persistence/progression and remaining potion/UI APIs are unfinished. Tests of controller decisions and native path-command delivery do not prove complete original mob movement, save/reload, client tracking or HBM modpack compatibility.

The small Zahin port credit and original author/audio credits remain. No release JAR was installed or published.

Evidence: test-results/2026-11-15-flight/manifest.json. Current source review: review/flight-2026-11-15.zip. Full captured diagnostics: ALL_REMAINING_ERRORS.md.
