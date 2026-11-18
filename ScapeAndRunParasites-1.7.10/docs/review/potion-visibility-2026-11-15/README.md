# Potion visibility: matched converted source and native evidence

This is a development review snapshot for the SRP 1.10.9 backport to Forge 1.7.10, not a playable mod or a release JAR.

Read docs/POTION_VISIBILITY_PORT.md first. It explains the production hook placement, changes to the four supplied files, source migration, native tests and explicit remaining work. The exact inputs and original Downloads hashes are preserved under upstream/potion-visibility-user-files-2026-11-15.

All 1,141 current converted main Java units are included under src/main/java, including all 47 shared backport helpers. Original assets are retained in the project; this source review bundle includes their provenance manifest rather than duplicating them. Original CFR headers remain as provenance and do not identify untouched reference code.

The full compiler snapshot reports 6,827 captured diagnostics and abnormal javac exit 4. Counts are not exhaustive. All included main-source SHA256 values match that snapshot; every diagnostic's printed source line was verified by the Markdown generator. EntityParasiteBase has 104 diagnostics; its exact converted source, numbered copy and contextual errors are also provided at the bundle root.

The production visibility boundary has 27 PASS records across 19 distinct structural/server/client boundaries. Tests use actual reobfuscated Forge servers, a second-JVM entity reload and a real client with ordinary native tracker packets. The first client fixture's failed assumption and corrected final result are both preserved under docs/test-results/2026-11-15-potion-visibility. Server/client fixtures contain identical production helper bytecode.

HUD visibility, potion types/items/brewing, missing effects, original mob/rendering/block integration and the complete build remain unfinished. Test-only JARs are not included as installable artifacts. No user game mods or saves were modified.

Current error inventory: docs/ALL_REMAINING_ERRORS.md. Complete raw output: docs/compile-full-inventory.json and .log. These files must be matched against the converted sources included here, not the earlier original reference or historical ZIPs.
