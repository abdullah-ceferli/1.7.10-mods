# Matched entity source and diagnostics
+
+Use docs/review/entity-parasite-base-2026-11-15.zip for the immutable review snapshot.
+
+The actual converted source is src/main/java/com/dhanantry/scapeandrunparasites/entity/ai/misc/EntityParasiteBase.java.
+
+Its CFR header is retained as provenance. The header and the original 'Could not load' comment list do not distinguish original reference code from the converted copy. Actual imports/calls, paths and source hashes do.
+
+The bundle contains the exact source bytes, a numbered copy, 124 file-specific diagnostics with source context, the full raw/JSON compiler output and existing compatibility helpers. All 124 printed diagnostic lines match the source. The advancements import is at line 115 in this snapshot.
+
+Compiler JSON schema 3 records SHA256/line counts for every source file and verifies that source bytes did not change during compilation. Symbol/location and overload details remain included. Three parser/source-snapshot regression tests pass.
+
+The preserved original source under .toolchain/reference-1.12.2 is useful for behavior comparisons, not current compiler line numbers.
+
+Position, state and synchronized-data adapters already exist. The complete mod still reports 7,341 diagnostics and exits abnormally; no playable release exists.
+