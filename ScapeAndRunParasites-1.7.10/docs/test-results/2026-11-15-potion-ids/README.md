# Spawn closure and potion ID foundation

Snapshot: 2026-11-15. These are the current converted files, preserved byte for byte with original project-relative paths. The complete mod still fails compilation.

Start with docs/POTION_ID_PORT.md. It states exactly which parts are implemented, the actual native checks, and what remains unported.

Main source entries:

- src/main/java/com/dhanantry/scapeandrunparasites/backport/world/DifficultyAccess.java: native/converted static spawn dispatch.
- src/main/java/com/dhanantry/scapeandrunparasites/backport/potion/NativePotionRegistry.java: explicit configurable IDs, table capacity, native/name lookups, stable modifier UUIDs and saved-world binding checks.
- src/main/java/com/dhanantry/scapeandrunparasites/backport/potion/NativePotionBase.java: native constructor and attribute registration boundary.
- src/main/java/com/dhanantry/scapeandrunparasites/init/SRPPotions.java: original 37 effect definitions and 33 PotionType definitions, with stable attribute UUIDs.
- src/main/java/com/dhanantry/scapeandrunparasites/potion/SRPEffectBase.java: native constructor boundary.
- src/main/java/com/dhanantry/scapeandrunparasites/potion/Recipe.java: retained original recipes; brewing is still unported.

All 41 backport helpers, changed original source files, other effect subclasses, matched compiler details, native test sources and access-transformer tools are included. This selection is review material, not a complete standalone build project.

Five-argument effect constructors, glowing/levitation, potion types/items and brewing remain explicit unfinished work. No show-particles flag was discarded and no recipe was cut. ID tests use native fixture effects for the 37 names and do not claim that every original custom effect or the complete mod runs.

The current EntityParasiteBase has 113 diagnostics. Older review ZIPs retain their earlier source snapshots and counts. Retained CFR headers do not distinguish converted files from untouched references.

The backport helper package has zero captured diagnostics. The separate original compatibility package has 102 cross-mod integration diagnostics. Do not attribute those 102 to the helpers.

Full captured inventory: 7,056 diagnostics; abnormal javac exit 4 makes the count non-exhaustive. manifest.json hashes the bundle, and selected-source-errors.json contains only included-source diagnostics. No files have been uploaded to Claude.
