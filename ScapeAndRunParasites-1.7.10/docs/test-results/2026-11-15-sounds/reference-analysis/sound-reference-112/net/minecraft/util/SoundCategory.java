/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;

public enum SoundCategory {
    MASTER("master"),
    MUSIC("music"),
    RECORDS("record"),
    WEATHER("weather"),
    BLOCKS("block"),
    HOSTILE("hostile"),
    NEUTRAL("neutral"),
    PLAYERS("player"),
    AMBIENT("ambient"),
    VOICE("voice");

    private static final Map<String, SoundCategory> field_187961_k;
    private final String field_187962_l;

    private SoundCategory(String string2) {
        this.field_187962_l = string2;
    }

    public String func_187948_a() {
        return this.field_187962_l;
    }

    public static SoundCategory func_187950_a(String string) {
        return field_187961_k.get(string);
    }

    public static Set<String> func_187949_b() {
        return field_187961_k.keySet();
    }

    static {
        field_187961_k = Maps.newHashMap();
        for (SoundCategory soundCategory : SoundCategory.values()) {
            if (field_187961_k.containsKey(soundCategory.func_187948_a())) {
                throw new Error("Clash in Sound Category name pools! Cannot insert " + (Object)((Object)soundCategory));
            }
            field_187961_k.put(soundCategory.func_187948_a(), soundCategory);
        }
    }
}

