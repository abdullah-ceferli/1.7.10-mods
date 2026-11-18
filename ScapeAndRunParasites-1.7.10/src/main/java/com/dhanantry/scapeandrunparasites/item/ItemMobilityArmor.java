/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.client.model.ModelMobilityArmor;
import javax.annotation.Nullable;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMobilityArmor
extends ItemArmor {
    private static final String VANILLA_ARMOR_FALLBACK = "minecraft:textures/models/armor/leather_layer_1.png";

    public ItemMobilityArmor(String registryName, ItemArmor.ArmorMaterial material, EntityEquipmentSlot slot) {
        super(material, 1, slot);
        RegistryNames.set(this, new ResourceLocation("srparasites", registryName));
        this.setUnlocalizedName("srparasites." + registryName);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return VANILLA_ARMOR_FALLBACK;
    }

    @Nullable
    @SideOnly(value=Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
        return ClientModels.get(armorSlot, defaultModel);
    }

    @SideOnly(value=Side.CLIENT)
    private static final class ClientModels {
        private static final ModelMobilityArmor MODEL = new ModelMobilityArmor();

        private ClientModels() {
        }

        private static ModelBiped get(EntityEquipmentSlot slot, ModelBiped defaultModel) {
            MODEL.setModelAttributes((ModelBase)defaultModel);
            return MODEL.setVisibleForSlot(slot);
        }
    }
}

