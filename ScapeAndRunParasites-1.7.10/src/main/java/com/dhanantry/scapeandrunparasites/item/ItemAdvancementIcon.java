/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import net.minecraft.item.Item;

public class ItemAdvancementIcon
extends Item {
    public ItemAdvancementIcon(String name) {
        RegistryNames.set(this, name);
        this.setUnlocalizedName("srparasites." + name);
        this.setCreativeTab(null);
        this.maxStackSize = 1;
    }
}

