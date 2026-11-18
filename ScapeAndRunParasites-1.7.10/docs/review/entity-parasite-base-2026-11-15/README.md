# Matched converted-source review snapshot

This is the current converted EntityParasiteBase, not the preserved original reference source.

Original project path: src/main/java/com/dhanantry/scapeandrunparasites/entity/ai/misc/EntityParasiteBase.java

The CFR header remains as provenance in the converted source. It must not be used to classify the file as original. The actual imports and method calls below the package statement contain the conversion. No lines were removed from the snapshot.

The source SHA256 is df1ebc90785a52ee75e68649c4bf8e9a85fd8856a575bcaea7e5d5379d0d512b.

All 124 diagnostics for this file were checked against the exact source line printed by javac. The raw log and JSON come from the same fresh full compile. Compiler source hashes were identical before and after that run.

Files:

- EntityParasiteBase.java: byte-for-byte copy of the compiled converted file.
- EntityParasiteBase.numbered.txt: human-readable numbered copy; do not compile it.
- EntityParasiteBase.errors.json: only this file's diagnostics, including symbol/location/overload details, exact source lines and context.
- EntityParasiteBase.errors.log: the same filtered raw diagnostics.
- compile-full-inventory.json and .log: full fresh compiler output with source hashes and complete details.
- compatibility/: current shared adapters, included to prevent duplicating or replacing work already implemented.
- ENTITY_REVIEW.md: source-checked assessment of the suggested conversions.

The position, block-state and entity-data boundaries are already connected. Entity data uses a tested typed manager and Forge transport, separate from native DataWatcher's limited ID space. Do not redesign those fields from the original reference file.

Remaining effects, advancement/progression and difficulty code still requires behavior-preserving conversion. Passing potion.id alone loses the fifth particle-visibility flag; the global difficulty enum is not a complete replacement for local difficulty. Keep these limitations visible.

The full mod still fails compilation. The compiler reports 7341 diagnostics and exits abnormally with code 4; the count is not exhaustive. This snapshot is development material, not a playable mod.
