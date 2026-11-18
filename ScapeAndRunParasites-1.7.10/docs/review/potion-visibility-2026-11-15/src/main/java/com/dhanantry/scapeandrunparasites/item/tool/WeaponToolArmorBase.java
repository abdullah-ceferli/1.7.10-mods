/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.effect.EntityLightningBolt
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WeaponToolArmorBase
extends ItemArmor {
    private byte effectHit;
    protected boolean calling;
    protected byte idTool;

    public WeaponToolArmorBase(ItemArmor.ArmorMaterial material, String name, int renderI, boolean fear, byte id, EntityEquipmentSlot eq) {
        super(material, renderI, eq);
        RegistryNames.set(this, "armor_" + name);
        this.setUnlocalizedName("srparasites.armor_" + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.calling = fear;
        this.idTool = id;
        this.addPropertyOverride(new ResourceLocation("vinni"), new IItemPropertyGetter(){

            @SideOnly(value=Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return 0.0f;
            }
        });
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote && this.calling && SRPConfigSystems.useScent && worldIn.rand.nextInt(10) == 0 && entityIn.ticksExisted % 40 == 0 && SRPSaveData.get(worldIn, 222).getDeveLevel() >= SRPConfigSystems.deveScentUse) {
            ((EntityLivingBase)entityIn).addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.PREY_E, 1200, 0, false, false));
        }
        if (entityIn.ticksExisted % 80 == 0) {
            int key = 0;
            NBTTagCompound compound = stack.getTagCompound();
            if (compound != null && this.getNext() != null) {
                if (compound.hasKey("srphits")) {
                    key = compound.getInteger("srphits");
                }
                if (key >= SRPConfig.weapon_livingSentient_DAMAGE_needed) {
                    compound.setInteger("srphits", 0);
                    stack.shrink(1);
                    ItemStack stackW = new ItemStack(this.getNext(), 1);
                    EntityItem entityitem = new EntityItem(worldIn, entityIn.posX, entityIn.posY, entityIn.posZ, stackW);
                    entityitem.setDefaultPickupDelay();
                    worldIn.spawnEntityInWorld((Entity)entityitem);
                    if (SRPConfig.thunderEnable) {
                        worldIn.addWeatherEffect((Entity)new EntityLightningBolt(worldIn, entityIn.posX, entityIn.posY, entityIn.posZ, true));
                    }
                    return;
                }
            }
        }
    }

    public boolean canCall() {
        return this.calling;
    }

    public Item getNext() {
        if (this == SRPItems.armor_helmet) {
            return SRPItems.armor_helmetSentient;
        }
        if (this == SRPItems.armor_chest) {
            return SRPItems.armor_chestSentient;
        }
        if (this == SRPItems.armor_pants) {
            return SRPItems.armor_pantsSentient;
        }
        if (this == SRPItems.armor_boots) {
            return SRPItems.armor_bootsSentient;
        }
        return null;
    }

    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
    }

    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        super.onCreated(stack, worldIn, playerIn);
    }

    public EnumRarity getRarity(ItemStack stack) {
        return super.getRarity(stack);
    }

    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        ArrayList<String> resistanceS = new ArrayList<String>();
        ArrayList<Integer> resistanceI = new ArrayList<Integer>();
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        tooltip.add(EnumChatFormatting.BLUE + "---> " + compound.getInteger("srphits"));
        tooltip.add(EnumChatFormatting.BLUE + "  ");
        if (compound.hasKey("sprresistances")) {
            int i;
            NBTTagList allResS = compound.getTagList("sprresistances", 10);
            NBTTagList allResI = compound.getTagList("sprresistancei", 10);
            if (allResS.tagCount() != allResI.tagCount()) {
                return;
            }
            for (i = 0; i < allResS.tagCount(); ++i) {
                NBTTagCompound resT = allResS.getCompoundTagAt(i);
                String res = resT.getString("resistance" + i);
                resistanceS.add(i, res);
                NBTTagCompound resU = allResI.getCompoundTagAt(i);
                int resi = resU.getInteger("resistance" + i);
                resistanceI.add(i, resi);
            }
            tooltip.add(EnumChatFormatting.DARK_PURPLE + "Current Adaptation:");
            for (i = 0; i < resistanceS.size(); ++i) {
                double reduc = Math.min((Integer)resistanceI.get(i), this.calling ? SRPConfig.sentientPointCap : SRPConfig.livingPointCap);
                double d = this.calling ? (double)SRPConfig.sentientPointReduction : (double)SRPConfig.livingPointReduction;
                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                String formatResult = decimalFormat.format((reduc *= d) * 100.0);
                tooltip.add(EnumChatFormatting.YELLOW + "-> " + (String)resistanceS.get(i));
                tooltip.add(EnumChatFormatting.YELLOW + " reduction: " + formatResult + "%");
            }
        }
    }
}

