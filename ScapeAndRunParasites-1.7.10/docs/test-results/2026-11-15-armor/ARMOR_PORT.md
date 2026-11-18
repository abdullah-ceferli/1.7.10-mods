# Native armor and toughness — 2026-11-15

The supplied formula and attribute bridge are integrated, compiled against actual Forge 1.7.10 and tested through real damage, NBT/restart and client packets. The complete mod still does not compile.

## Implementation

ArmorMath.java is byte-for-byte identical to the supplied file. Its output matches original Minecraft 1.12.2 CombatRules bytecode bit-for-bit in 30,432 boundary/random cases.

ModernAttributes retains the supplied generic.armor (0–30) and generic.armorToughness (0–20) names/ranges and watch flags. A narrow runtime transformer registers both during native EntityLivingBase.applyEntityAttributes, before subclass setup and NBT reads. LegacyEntityMob also ensures registration for environments without the transformer. Native zombie base armor 2 and wither base armor 4 are mapped to the modern attribute bases. Players and ordinary native living entities start at zero. Runtime values such as Nogla's are never reset by equipment synchronization.

LegacyEntityMob overrides getTotalArmorValue and applyArmorCalculations. Real native damageEntity and public attackEntityFrom dispatch through that override once. It calls the armor-wear extension point and the original modern formula for blockable damage, with native potion damage/absorption/event processing retained. Unblockable sources skip armor and wear.

Equipment is represented by stable per-slot transient attribute modifiers, rather than adding native equipment to an attribute already containing it. This keeps equipment out of base-stat copying, preserves native attribute modifier ordering and caps, and prevents saved equipment modifiers from being counted again after reload. Correctly slotted native ItemArmor contributes its armor value; diamond contributes 2 toughness per piece. Held armor is excluded. Explicit native item armor/toughness modifiers take precedence over the corresponding fallback and are applied by the native equipment update path. Unchanged amounts preserve modifier identity. ArmorAttributeEvents updates the equipment view on living ticks; LegacyEntityMob also refreshes before armor queries/damage.

All 149 code references across 128 files are migrated: 146 ARMOR and three ARMOR_TOUGHNESS. Strings, comments and original provenance headers are untouched. The audit is armor-attribute-calls.json. Both properties are real registered native attributes for arbitrary living victims, so the base-armor check in EntityParasiteBase no longer fails merely because a victim is a player or vanilla mob.

## Player and vanilla policy

The modern damage formula is applied to LegacyEntityMob descendants. Native players retain Forge's ISpecialArmor damage path, and other native mobs retain their native damage calculations. Registering and mirroring attributes supplies the modern query/save/network contract; it does not replace those native combat paths. A real native player fixture proves special-armor absorption and wear still run. This avoids silently bypassing third-party special armor.

Native vanilla intrinsic armor is mapped only for verified zombie/wither behavior. Third-party intrinsic armor overrides and custom modern SRP armor-material toughness still need explicit integration in their respective content ports. The full modern equipment/hand API and original SRP armor items remain unported. This stage does not claim every original parasite or custom armor item is playable.

## Verification

31 PASS records cover 21 distinct new boundaries: one original-bytecode formula comparison, three actual MCP/SRG transformer checks, eleven dedicated-server checks repeated after a genuine second-JVM reload (the save and restart cases are distinct), and five actual client/integrated-server checks.

Native checks prove early attribute registration for SRP mobs, players and vanilla entities; watched names/ranges; real damage and armor-wear dispatch; runtime armor/toughness; unblockable and Forge hurt/cancel ordering; equipment counted once, removal and multiplier ordering; explicit item modifiers; victim base-stat queries; preserved ISpecialArmor and native mob damage; named NBT/modifiers; coexistence with the production potion transformer; and genuine restart.

The real client uses a registered custom LegacyEntityMob fixture. Actual spawn/equipment/attribute/health packets deliver one equipment contribution, runtime toughness and reduced health. The owning client player has the attribute names. Tracking loss and re-entry reconstruct a fresh correct client entity. There is no custom armor packet or full original-mob claim.

Two fixture mistakes were corrected: native DamageSource.generic bypasses armor, so ordinary hits now use a blockable source; the first 80-damage toughness comparison put both variants on the same minimum-armor plateau, so the test now uses 40 damage. Earlier failed logs are preserved. Production helper bytecode is unchanged across those fixture corrections.

All 51 backport source units compile. The complete probe still fails: 6,683 captured diagnostics (down from 6,827), exact entity package 2,285, EntityParasiteBase 98, helper package zero. No ARMOR/ARMOR_TOUGHNESS missing-symbol diagnostics remain. Abnormal compiler exit 4 prevents an exhaustive count or completion percentage. All 1,094 original Java units and 4,501 unchanged assets remain. No installed mods or user saves were changed.

Commands:

    python tools/verify_native_armor.py
    python tools/verify_armor_client.py
    python tools/verify_workspace.py
    python tools/compile_probe.py --limit 30000 --name compile-full-inventory
    python tools/write_remaining_errors.py

The original supplied files and hashes are preserved under upstream/armor-user-files-2026-11-15. Source-matched review: review/armor-2026-11-15.zip. Complete current captured errors: ALL_REMAINING_ERRORS.md.
