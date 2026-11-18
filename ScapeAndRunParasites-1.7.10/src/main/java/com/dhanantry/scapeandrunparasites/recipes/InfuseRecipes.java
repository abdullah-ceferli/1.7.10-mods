/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package com.dhanantry.scapeandrunparasites.recipes;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;

public class InfuseRecipes {
    private static final InfuseRecipes INSTANCE = new InfuseRecipes();
    private final Map<Key, ItemStack> map = new HashMap<Key, ItemStack>();

    public static InfuseRecipes instance() {
        return INSTANCE;
    }

    public void add(ItemStack base, ItemStack reagent, ItemStack result) {
        this.map.put(new Key(base, reagent), result);
    }

    public ItemStack getResult(ItemStack base, ItemStack reagent) {
        for (Map.Entry<Key, ItemStack> e : this.map.entrySet()) {
            Key k = e.getKey();
            if (!ItemStack.areItemsEqual((ItemStack)k.a, (ItemStack)base) || !ItemStack.areItemsEqual((ItemStack)k.b, (ItemStack)reagent)) continue;
            return e.getValue();
        }
        return ItemStack.EMPTY;
    }

    private static class Key {
        private final ItemStack a;
        private final ItemStack b;

        Key(ItemStack a, ItemStack b) {
            this.a = a;
            this.b = b;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Key)) {
                return false;
            }
            Key k = (Key)o;
            return ItemStack.areItemsEqual((ItemStack)this.a, (ItemStack)k.a) && ItemStack.areItemsEqual((ItemStack)this.b, (ItemStack)k.b);
        }

        public int hashCode() {
            return (this.a.getItem().hashCode() * 31 + this.b.getItem().hashCode()) * 31 + (this.a.getItemDamage() * 7 + this.b.getItemDamage());
        }
    }
}

