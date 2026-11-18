# Mob AI initialization and tick callbacks — 2026-11-16

The retained modern AI initialization callback now runs at the native EntityLiving constructor boundary on the server. Converted mobs select task-based AI, and eleven modern mob-tick overrides run at the native callback stage. Fifteen new verification groups pass against Forge 10.13.4.1614, including a real client/integrated server and a separate dedicated-server process restart.

## Behavior

The original 1.12 EntityLiving constructor calls initEntityAI after creating both task schedulers, navigation, senses and helper objects and setting equipment drop chances. It calls the virtual method before subclass field initializers, only when the world exists and is server-side. The original base callback is empty.

Native 1.7.10 has no corresponding initialization call. A narrow transformer adds the call at the end of that constructor. An explicit interface restricts it to LegacyEntityMob descendants. Native mobs and other mods retain their existing constructor behavior. The transformer fails on missing or unexpected constructor shapes, rejects incorrect or duplicated hooks, and is idempotent.

LegacyEntityMob supplies the empty extension point and opts into native task scheduling. Before this change, the converted initEntityAI overrides were never called, and the inherited isAIEnabled result selected the old random-action AI branch. The original shared parasite constructor can still add its own goals after the native constructor returns. Empty subclass overrides remain empty.

The name updateAITasks has a second incompatibility. In 1.12 it is the per-mob callback between navigation and control helpers; in 1.7 it owns the whole scheduler. Eleven declarations and their eleven super calls now use the native updateAITick extension instead. A syntax-tree migration changes only those 22 identifier boundaries. Their existing logic and inheritance remain intact, and omitting a super call in a mob-specific tick cannot suppress the task selectors.

Three new helper units bring the helper total to 91. There are 1,185 main Java source units. A source scan finds 134 original protected initEntityAI overrides that now have a shared dispatch boundary; this is a source count, not proof that those mobs compile or run.

## Verification

Two transformer groups verify native constructor insertion, idempotence, unrelated-class preservation and rejection of changed shapes or hooks.

Nine dedicated-server groups verify:

1. One constructor callback with schedulers, helpers, attributes and drop chances ready.
2. Virtual dispatch before subclass field initialization, with correct super and constructor order.
3. Empty overrides and unchanged native mob AI selection.
4. Both task selectors executing through real living updates, in order, without the old fallback.
5. Mob-tick callbacks after selectors and before controls, including an override without a super call.
6. Constructor-registered modern retaliation executing through living updates.
7. Spawn and repeated NBT reads without duplicated goals.
8. Server and null-world gate decisions matching the original 1.12 constructor instruction slice.
9. Initializer exceptions propagating instead of silently constructing a partly initialized mob.

A second dedicated-server process reads saved NBT, constructs its goals once and executes them.

Three real client/integrated-server groups verify server-only registration and execution on distinct server/client entities, original client-world gating, and tracking re-entry without duplicate server goals. The server and client fixture modules contain identical production class bytes.

The reference builder retains the original constructor's eight executable gating instructions, adapting only the native isRemote name and the virtual callback boundary. It also verifies that the original initialization and mob-tick base callbacks are empty. This is a comparison of the constructor gate, not a complete original entity or scheduler emulation.

The earlier flight suite still passes all six groups, including the 25 original controller bodies over 24,000 updates and 50,169 steering inputs. These reruns are not counted as new groups. The recorded total is 237 groups plus three separate diagnostic-parser checks.

Commands:

    python tools/verify_native_lifecycle.py
    python tools/verify_lifecycle_client.py
    python tools/verify_native_flight.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory
    python tools/write_remaining_errors.py

The native lifecycle runner applies the current production core plugin and helpers to a reobfuscated disposable fixture. Test worlds, client profiles and test JARs stay under .toolchain; user Minecraft mods and saves are not changed.

## Current limits

The full compiler snapshot captures 5,549 diagnostics, down from 5,565. Entity diagnostics fall from 1,487 to 1,471; EntityParasiteBase still has 87. Helpers have zero captured diagnostics. javac still exits abnormally with code 4, so these counts are not exhaustive and do not measure completion.

No complete original SRP mob has been compiled and validated end to end. Native task scheduling does not provide the modern ground/flying/swimming/climbing navigation implementations, melee goals, equipment/combat APIs, boss tracking or the missing original entity methods. The modern NoAI flag, its queries and its persistence are still unported; no constant result is substituted for it. The initialization bridge does not make these missing behaviors work.

Rendering, specialized blocks/world generation, progression/persistence, potion brewing/HUD and other full-mod boundaries remain unfinished. HBM modpack compatibility is unverified. No playable or release JAR exists, and none has been installed or published.

Evidence: test-results/2026-11-16-ai-lifecycle/manifest.json. Matching source review: review/ai-lifecycle-2026-11-16.zip. Complete captured diagnostics: ALL_REMAINING_ERRORS.md.
