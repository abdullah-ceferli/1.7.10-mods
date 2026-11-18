package com.dhanantry.scapeandrunparasites.backport.entity;

/** The 1.12.2 CombatRules armour formula, separated so it can be tested without the game. */
public final class ArmorMath {
    private ArmorMath() {}

    /** Damage left after armour: armour counts for at least 20% and at most 20 points, toughness raises the damage scale. */
    public static float afterAbsorb(float damage, float totalArmor, float toughness) {
        float f = 2.0f + toughness / 4.0f;
        float f1 = clamp(totalArmor - damage / f, totalArmor * 0.2f, 20.0f);
        return damage * (1.0f - f1 / 25.0f);
    }

    private static float clamp(float v, float lo, float hi) { return v < lo ? lo : (v > hi ? hi : v); }
}
