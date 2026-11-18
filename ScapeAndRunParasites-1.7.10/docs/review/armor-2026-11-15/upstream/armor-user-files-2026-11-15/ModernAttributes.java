package com.dhanantry.scapeandrunparasites.backport.entity;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

/**
 * Attributes that exist in 1.12.2 but not in 1.7.10. The names are the modern attribute names, because
 * attribute maps are saved to entity NBT by name.
 */
public final class ModernAttributes {
    private ModernAttributes() {}

    public static final IAttribute ARMOR = create("generic.armor", 0.0, 0.0, 30.0);
    public static final IAttribute ARMOR_TOUGHNESS = create("generic.armorToughness", 0.0, 0.0, 20.0);

    private static IAttribute create(String name, double def, double min, double max) {
        RangedAttribute a = new RangedAttribute(name, def, min, max);
        a.setShouldWatch(true);
        return a;
    }
}
