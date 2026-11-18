# Retaliation and targeting compatibility — 2026-11-15

The modern revenge goal and targeting predicate now compile against Forge 10.13.4.1614. Fourteen scenario groups pass on a real dedicated server, each executed against both the original 1.12.2 AI decision bytecode and the port.

## Changes

Two helpers, EntityAIHurtByTarget and EntityAITarget, retain the original goal state and decisions while calling native entity, sensing, scoreboard, owner and pathfinding APIs. The 125 original revenge-goal constructors now import the ported class. Two attack-goal files use the ported static suitability predicate. Only the exact import declarations were rewritten in these 127 source files; their constructor arguments and goal bodies were preserved.

The revenge goal checks a new revenge timestamp and the attacker’s suitability before starting. It sets the attack target and remembers it as a fallback, uses mutex bit 1, and gives an unseen attacker 300 ticks of memory. Reset clears the target without replaying an old injury.

Optional helper recruitment preserves the same-species class query, current follow range, ten-block vertical expansion, the original one-block origin box, busy-helper rejection, teams and resolved tame-owner identity. Exclusions compare exact helper classes; they do not become an instanceof filter or reject the attacker.

The base goal preserves target survival, range, team and invulnerability checks; sight memory and refresh; fallback restoration; initial home-distance checks; optional path endpoint tolerance and retry caching; and configurable unseen memory. Home-position conversion floors the target's actual Y, matching new BlockPos(entity), rather than the half-block adjustment used by Entity.getPosition().

Native owner IDs are stored as strings. Valid UUIDs compare as UUID identities, including case differences. Legacy non-UUID owner strings retain native string equality as an additional compatibility fallback.

## Verification

The reference builder reads the original class bytes from minecraft-1.12.2-srg-reference.jar. It retains AI branches, state fields, timers and decisions. It substitutes native class/member names, native bounding-box allocation, owner UUID access and coordinate-based home queries. It does not replace the original decision methods with the new implementation.

Both implementations then execute on native Forge entities and the same controlled fixtures. The fourteen groups cover:

1. Revenge timestamps, target assignment, mutex bits and reset.
2. Sight, attack-class veto, home distance and negative/fractional position rounding.
3. Exactly 300 unseen ticks and memory refresh.
4. Fallback restoration, target death and range.
5. Teams during initial selection, continuation and helper recruitment.
6. Help disabled/enabled, busy allies and unrelated species.
7. Exact-class exclusions, subclasses and attacker selection.
8. Horizontal/vertical helper-query bounds and the one-block origin box.
9. Shared/different resolved tame owners and unresolved owner equality.
10. Owner UUIDs, own owner, self, null and dead candidate rejection.
11. Initial player invulnerability and changes during continuation.
12. Null/empty paths, endpoint availability and cached retry behavior.
13. Endpoint tolerance, configurable sight memory and reset.
14. Execution through the actual native AI task scheduler.

The disposable production fixture is reobfuscated to the real Forge runtime and includes all 76 helpers and the existing core plugins. Final result: 14 passing groups, 28 original/port scenario executions, zero failed groups. Native path results are controlled in endpoint tests; this verifies targeting decisions, not equivalence of the complete modern navigation engine.

Commands:

    python tools/verify_native_retaliation.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory
    python tools/write_remaining_errors.py

## Current limits

Captured diagnostics decreased from 5,997 to 5,870. All 125 revenge-constructor errors and both static suitability errors are cleared. The entity package has 1,791 captured diagnostics; EntityParasiteBase still has 87. All 76 helper units compile, with zero helper diagnostics.

Javac still exits abnormally with code 4, so the inventory is not exhaustive. The full mod remains unbuildable and unplayable. No original SRP mob has been validated end to end. Movement/controllers, modern navigation, hands/equipment, remaining combat, rendering/custom particles, specialized blocks, world generation, persistence/progression, subtitles, potion HUD and potion items/types/brewing remain incomplete. HBM modpack compatibility has not yet been tested.

All 1,094 original Java units and 4,501 original assets remain. Public metadata now includes the small “Minecraft 1.7.10 port by Zahin” credit while retaining Dhanantry and the original sound credits. No release JAR has been installed or published.

Evidence: test-results/2026-11-15-retaliation/manifest.json. Current source review: review/retaliation-2026-11-15.zip. Full captured diagnostics: ALL_REMAINING_ERRORS.md.
