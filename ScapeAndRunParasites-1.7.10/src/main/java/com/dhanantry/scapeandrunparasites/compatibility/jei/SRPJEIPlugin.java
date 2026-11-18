/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.IModPlugin
 *  mezz.jei.api.IModRegistry
 *  mezz.jei.api.JEIPlugin
 *  mezz.jei.api.recipe.IRecipeCategory
 *  mezz.jei.api.recipe.IRecipeCategoryRegistration
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionType
 *  net.minecraft.potion.PotionUtils
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.common.registry.ForgeRegistries
 */
package com.dhanantry.scapeandrunparasites.compatibility.jei;

import com.dhanantry.scapeandrunparasites.compatibility.jei.InfuserFurnaceJEICategory;
import com.dhanantry.scapeandrunparasites.compatibility.jei.InfuserFurnaceJEIRecipe;
import com.dhanantry.scapeandrunparasites.compatibility.jei.SRPBrewingCategory;
import com.dhanantry.scapeandrunparasites.compatibility.jei.SRPBrewingJEIRecipe;
import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipe;
import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipes;
import java.util.ArrayList;
import java.util.Arrays;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.registry.ForgeRegistries;

@JEIPlugin
public class SRPJEIPlugin
implements IModPlugin {
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new IRecipeCategory[]{new InfuserFurnaceJEICategory(registry.getJeiHelpers().getGuiHelper())});
        registry.addRecipeCategories(new IRecipeCategory[]{new SRPBrewingCategory(registry.getJeiHelpers().getGuiHelper())});
    }

    public void register(IModRegistry registry) {
        ItemStack awkwardPotion;
        ItemStack waterPotion;
        ItemStack infuserStack;
        ArrayList<InfuserFurnaceJEIRecipe> jeiRecipes = new ArrayList<InfuserFurnaceJEIRecipe>();
        for (InfuserFurnaceRecipe r : InfuserFurnaceRecipes.all()) {
            jeiRecipes.add(new InfuserFurnaceJEIRecipe(r));
        }
        registry.addRecipes(jeiRecipes, "srparasites.infuser_furnace");
        Block infuser = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation("srparasites", "infuser_furnace"));
        if (infuser != null && infuser != Blocks.air && !(infuserStack = new ItemStack(infuser)).isEmpty()) {
            registry.addRecipeCatalyst((Object)infuserStack, new String[]{"srparasites.infuser_furnace"});
        }
        String MODID = "srparasites";
        Item alveolar = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("srparasites", "alveolar_fluid"));
        Item diseasedSponge = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("srparasites", "diseased_sponge"));
        Item deadblood = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("srparasites", "deadblood_fluid"));
        Item thornshadeBerry = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("srparasites", "thornshade_berry"));
        Item thornshadeDecanter = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("srparasites", "thornshade_decanter"));
        PotionType FEAR = (PotionType)ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation("srparasites", "fear"));
        PotionType WATER = (PotionType)ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation("minecraft", "water"));
        PotionType AWKWARD = (PotionType)ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation("minecraft", "awkward"));
        ArrayList<SRPBrewingJEIRecipe> brewing = new ArrayList<SRPBrewingJEIRecipe>();
        if (alveolar != null && FEAR != null) {
            brewing.add(new SRPBrewingJEIRecipe(Arrays.asList(new ItemStack(alveolar)), Arrays.asList(new ItemStack(Items.flint)), PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.potionitem), (PotionType)FEAR)));
            brewing.add(new SRPBrewingJEIRecipe(Arrays.asList(PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.potionitem), (PotionType)FEAR)), Arrays.asList(new ItemStack(Items.gunpowder)), PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.SPLASH_POTION), (PotionType)FEAR)));
            brewing.add(new SRPBrewingJEIRecipe(Arrays.asList(PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.SPLASH_POTION), (PotionType)FEAR)), Arrays.asList(new ItemStack(Items.DRAGON_BREATH)), PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.LINGERING_POTION), (PotionType)FEAR)));
        }
        if (diseasedSponge != null && deadblood != null && WATER != null && AWKWARD != null) {
            waterPotion = PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.potionitem), (PotionType)WATER);
            awkwardPotion = PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.potionitem), (PotionType)AWKWARD);
            brewing.add(new SRPBrewingJEIRecipe(Arrays.asList(waterPotion, awkwardPotion), Arrays.asList(new ItemStack(diseasedSponge)), new ItemStack(deadblood)));
        }
        if (thornshadeBerry != null && thornshadeDecanter != null && WATER != null && AWKWARD != null) {
            waterPotion = PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.potionitem), (PotionType)WATER);
            awkwardPotion = PotionUtils.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.potionitem), (PotionType)AWKWARD);
            brewing.add(new SRPBrewingJEIRecipe(Arrays.asList(waterPotion, awkwardPotion), Arrays.asList(new ItemStack(thornshadeBerry)), new ItemStack(thornshadeDecanter)));
        }
        if (!brewing.isEmpty()) {
            registry.addRecipes(brewing, "srparasites.srp_brewing");
            ItemStack brewingStandStack = new ItemStack(Items.brewing_stand);
            if (!brewingStandStack.isEmpty()) {
                registry.addRecipeCatalyst((Object)brewingStandStack, new String[]{"srparasites.srp_brewing"});
            }
        }
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "dark_days_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "adapted_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "primitive_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "crude_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "pure_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "hunt_season_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "guerilla_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "ecstasy_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "enemy_of_enemy_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "fog_nullifier_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "self_destruct_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "potion_columbus_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "potion_stolas_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "hellfire_chemical_warfare_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "cosmic_structural_failure_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "roots_icon");
    }

    private static void blacklistAdvancementIcon(IModRegistry registry, String itemName) {
        ResourceLocation id = new ResourceLocation("srparasites", itemName);
        Item item = (Item)ForgeRegistries.ITEMS.getValue(id);
        if (item == null || item == Items.AIR) {
            System.out.println("[SRP][JEI] Missing advancement icon item: " + id);
            return;
        }
        ItemStack stack = new ItemStack(item);
        if (stack.isEmpty()) {
            System.out.println("[SRP][JEI] Empty advancement icon stack: " + id);
            return;
        }
        try {
            registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist((Object)stack);
            System.out.println("[SRP][JEI] Blacklisted advancement icon: " + id);
        }
        catch (Throwable t) {
            System.out.println("[SRP][JEI] Failed to blacklist advancement icon: " + id);
            t.printStackTrace();
        }
    }
}

