# Item properties and a complete compiler diagnostic pass — 2026-11-16

The Java 8 compiler crash is resolved. The current source snapshot produces a normal error exit (1), with all 8,132 printed diagnostics parsed and matched to the compiler's 8,132-error footer. The full mod still fails to compile and is not playable.

## Why the count increased

The earlier 5,549 diagnostics were an interrupted inventory. The melee goal resolved 14 of them, leaving 5,535 before the same compiler crash. Those were partial counts, not complete totals.

Enabling Java 8's development diagnostics exposed the internal failure:

    java.lang.AssertionError: annotation tree hasn't been attributed yet: @SideOnly(value = Side.CLIENT)

The compiler failed while checking ItemCompass's anonymous item-property callback, whose 1.12 IItemPropertyGetter interface was absent from the native API. Its normal output suppressed the compiler stack trace because source errors were already present. The full diagnostic tail and pre-change source snapshot are preserved in this stage's evidence.

Porting the callback interface and its storage boundary lets attribution proceed through the remaining source. Compared with the immediately preceding 5,535-record snapshot, ten earlier diagnostic records are resolved/replaced and 2,607 additional records are now captured. The net increase is 2,597. This comparison identifies records by source path, unchanged line number, message and named symbol; a diagnostic record is not an independent bug.

The additional records are in world (862), util (804), item (513), network (177), potion (99), proxy (84), tileentity (52), recipes (14), SRPMain (1) and mixins (1). The two new helpers have no diagnostics. Most of this code was beyond the earlier compiler stop. The count change is increased visibility into unfinished code, not a measure of work added or completion lost.

## Property boundary

IItemPropertyGetter retains the original apply(ItemStack, World, EntityLivingBase) contract. World and entity contexts can be null. Callers receive the original objects, and stateful anonymous callbacks retain their instance state.

ItemProperties stores a separate native RegistrySimple for each Item identity. It supports native item subclasses and armor without changing their inheritance. Namespaced ResourceLocation keys, missing-key null results and duplicate-key replacement match the original registration behavior. Null registration inputs are rejected before mutation, as in the original 1.12 registry. Registration and lookup do not evaluate the callback.

A syntax-tree migration changes 17 boundaries across eight original files: seven interface imports, nine registrations and one lookup. Existing callback bodies, annotations, argument order and line counts remain intact. The original compass, clocks, weapons and pearl calculations have not been replaced with constant values.

This implements callback storage and explicit evaluation. Native 1.7 icon/texture selection and the original 1.12 model-property rendering are separate unfinished work. Default modern vanilla property providers, off-hand/cooldown APIs and full original compass/clock/weapon rendering are not supplied by this stage. The original callback bodies still have platform errors that are now visible to javac.

Two helper units bring the helper total to 94 and the main Java source total to 1,188. No runtime transformer or access rule was added.

## Verification

Three native dedicated-server groups pass:

1. Actual Item/ItemArmor instances retain separate registries, even when item subclasses compare equal; namespaces and key replacement remain distinct.
2. Null inputs reject before registration changes, and absent/null lookup keys return no callback.
3. A registered native item stores a callback whose client-only fields and apply method are stripped by Forge on the dedicated server, without trying to execute them.

Two actual client/integrated-server groups pass:

1. Forge retains the client-only fields and method, state persists between calls, and nullable contexts reach the callback unchanged.
2. Callback lookup uses the actual client world/player and original stack, and observes subsequent stack changes.

All 158 production helper/core-plugin class files are identical in the dedicated and client test modules. Tests use disposable .toolchain modules, profiles and worlds. User mods and saves are unchanged.

The diagnostic probe now includes development diagnostics so an internal compiler failure cannot silently hide its explanation. Schema 4 records the compiler's footer total, parser agreement and whether the diagnostic pass completed without a cap or abnormal exit. Seven parser/source-snapshot regression checks pass, covering normal failures, capped output, mismatched parser totals, abnormal compiler exceptions and successful empty output.

Recorded verification totals: 257 compatibility/native groups plus seven separate diagnostic-parser/source-snapshot checks. The same user turn also completed the preceding melee stage's 15 groups; see MELEE_AI_PORT.md and its immutable source review for those results.

Commands:

    python tools/verify_native_item_properties.py
    python tools/verify_item_properties_client.py
    python tools/test_compile_probe.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory
    python tools/verify_workspace.py
    python tools/write_remaining_errors.py

## Remaining work

The full pass contains 1,672 client diagnostics, 1,457 entity diagnostics, 1,042 world diagnostics, 879 util diagnostics and 799 block diagnostics, with the other areas listed in MAJOR_BLOCKERS.md. EntityParasiteBase still has 87. Helpers have zero diagnostics; the original cross-mod compatibility package has 102.

These are all printed errors for this source snapshot, not a guarantee that fixing them exposes no further defects. Missing parents and APIs can hide later type errors and cause cascades. No complete original SRP mob has been validated end to end. Modern navigation/NoAI, equipment/custom combat, rendering, world generation, persistence/progression, potion brewing/HUD and original registration remain incomplete. A successful full build and HBM modpack validation are still required.

Original 1,094 source units and 4,501 assets remain. Original credits and the small Zahin port credit are retained. No playable release JAR exists, and none has been installed or published.

Evidence: test-results/2026-11-16-item-properties/manifest.json. Matching source review: review/item-properties-2026-11-16.zip. The complete printed diagnostics are in ALL_REMAINING_ERRORS.md.
