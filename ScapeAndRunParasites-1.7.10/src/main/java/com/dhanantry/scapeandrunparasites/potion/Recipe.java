/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionType
 *  net.minecraft.potion.PotionUtils
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.common.brewing.BrewingRecipeRegistry
 *  net.minecraftforge.common.brewing.IBrewingRecipe
 *  net.minecraftforge.fml.common.registry.ForgeRegistries
 */
package com.dhanantry.scapeandrunparasites.potion;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import cpw.mods.fml.common.registry.ForgeRegistries;

public final class Recipe {
    private Recipe() {
    }

    public static void init() {
        String MODID = "srparasites";
        Item alveolar = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("srparasites", "alveolar_fluid"));
        Item diseasedSponge = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("srparasites", "diseased_sponge"));
        Item deadblood = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("srparasites", "deadblood_fluid"));
        PotionType FEAR = (PotionType)ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation("srparasites", "fear"));
        PotionType WATER = (PotionType)ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation("minecraft", "water"));
        PotionType AWKWARD = (PotionType)ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation("minecraft", "awkward"));
        Item thornshadeBerry = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("srparasites", "thornshade_berry"));
        Item thornshadeDecanter = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("srparasites", "thornshade_decanter"));
        if (alveolar == null) {
            System.err.println("[SRPBrewing] Missing item: srparasites:alveolar_fluid");
            return;
        }
        if (FEAR == null) {
            System.err.println("[SRPBrewing] Missing potion type: srparasites:fear");
            return;
        }
        if (WATER == null || AWKWARD == null) {
            System.err.println("[SRPBrewing] Missing vanilla potion types: minecraft:water or minecraft:awkward");
            return;
        }
        if (diseasedSponge == null) {
            System.err.println("[SRPBrewing] Missing item: srparasites:diseased_sponge");
            return;
        }
        if (deadblood == null) {
            System.err.println("[SRPBrewing] Missing item: srparasites:deadblood_fluid");
            return;
        }
        if (thornshadeBerry == null) {
            System.err.println("[SRPBrewing] Missing item: srparasites:thornshade_berry");
            return;
        }
        if (thornshadeDecanter == null) {
            System.err.println("[SRPBrewing] Missing item: srparasites:thornshade_decanter");
            return;
        }
        BrewingRecipeRegistry.addRecipe((IBrewingRecipe)new BaseToFearRecipe(alveolar, FEAR));
        BrewingRecipeRegistry.addRecipe((IBrewingRecipe)new FearToSplashRecipe(FEAR));
        BrewingRecipeRegistry.addRecipe((IBrewingRecipe)new FearSplashToLingeringRecipe(FEAR));
        BrewingRecipeRegistry.addRecipe((IBrewingRecipe)new SpongeToDeadbloodRecipe(diseasedSponge, deadblood, WATER, AWKWARD));
        BrewingRecipeRegistry.addRecipe((IBrewingRecipe)new BerryToThornshadeDecanterRecipe(thornshadeBerry, thornshadeDecanter, WATER, AWKWARD));
    }

    private static class FearSplashToLingeringRecipe
    implements IBrewingRecipe {
        private final PotionType fear;

        private FearSplashToLingeringRecipe(PotionType fear) {
            this.fear = fear;
        }

        public boolean isInput(ItemStack input) {
            return !input.isEmpty() && input.getItem() == Items.SPLASH_POTION && PotionUtils.getPotionFromItem((ItemStack)input) == this.fear;
        }

        public boolean isIngredient(ItemStack ingredient) {
            return !ingredient.isEmpty() && ingredient.getItem() == Items.DRAGON_BREATH;
        }

        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (this.isInput(input) && this.isIngredient(ingredient)) {
                return PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.LINGERING_POTION), (PotionType)this.fear);
            }
            return ItemStack.EMPTY;
        }
    }

    private static class FearToSplashRecipe
    implements IBrewingRecipe {
        private final PotionType fear;

        private FearToSplashRecipe(PotionType fear) {
            this.fear = fear;
        }

        public boolean isInput(ItemStack input) {
            return !input.isEmpty() && input.getItem() == Items.potionitem && PotionUtils.getPotionFromItem((ItemStack)input) == this.fear;
        }

        public boolean isIngredient(ItemStack ingredient) {
            return !ingredient.isEmpty() && ingredient.getItem() == Items.gunpowder;
        }

        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (this.isInput(input) && this.isIngredient(ingredient)) {
                return PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.SPLASH_POTION), (PotionType)this.fear);
            }
            return ItemStack.EMPTY;
        }
    }

    private static class BaseToFearRecipe
    implements IBrewingRecipe {
        private final Item baseItem;
        private final PotionType fear;

        private BaseToFearRecipe(Item baseItem, PotionType fear) {
            this.baseItem = baseItem;
            this.fear = fear;
        }

        public boolean isInput(ItemStack input) {
            return !input.isEmpty() && input.getItem() == this.baseItem;
        }

        public boolean isIngredient(ItemStack ingredient) {
            return !ingredient.isEmpty() && ingredient.getItem() == Items.flint;
        }

        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (this.isInput(input) && this.isIngredient(ingredient)) {
                return PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.potionitem), (PotionType)this.fear);
            }
            return ItemStack.EMPTY;
        }
    }

    private static class BerryToThornshadeDecanterRecipe
    implements IBrewingRecipe {
        private final Item ingredientItem;
        private final Item outItem;
        private final PotionType water;
        private final PotionType awkward;

        private BerryToThornshadeDecanterRecipe(Item ingredientItem, Item outItem, PotionType water, PotionType awkward) {
            this.ingredientItem = ingredientItem;
            this.outItem = outItem;
            this.water = water;
            this.awkward = awkward;
        }

        public boolean isInput(ItemStack input) {
            if (input.isEmpty()) {
                return false;
            }
            if (input.getItem() != Items.potionitem) {
                return false;
            }
            PotionType p = PotionUtils.getPotionFromItem((ItemStack)input);
            return p == this.water || p == this.awkward;
        }

        public boolean isIngredient(ItemStack ingredient) {
            return !ingredient.isEmpty() && ingredient.getItem() == this.ingredientItem;
        }

        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (this.isInput(input) && this.isIngredient(ingredient)) {
                return new ItemStack(this.outItem, 1);
            }
            return ItemStack.EMPTY;
        }
    }

    private static class SpongeToDeadbloodRecipe
    implements IBrewingRecipe {
        private final Item ingredientItem;
        private final Item outItem;
        private final PotionType water;
        private final PotionType awkward;

        private SpongeToDeadbloodRecipe(Item ingredientItem, Item outItem, PotionType water, PotionType awkward) {
            this.ingredientItem = ingredientItem;
            this.outItem = outItem;
            this.water = water;
            this.awkward = awkward;
        }

        public boolean isInput(ItemStack input) {
            if (input.isEmpty()) {
                return false;
            }
            if (input.getItem() != Items.potionitem) {
                return false;
            }
            PotionType p = PotionUtils.getPotionFromItem((ItemStack)input);
            return p == this.water || p == this.awkward;
        }

        public boolean isIngredient(ItemStack ingredient) {
            return !ingredient.isEmpty() && ingredient.getItem() == this.ingredientItem;
        }

        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (this.isInput(input) && this.isIngredient(ingredient)) {
                return new ItemStack(this.outItem, 1);
            }
            return ItemStack.EMPTY;
        }
    }
}

