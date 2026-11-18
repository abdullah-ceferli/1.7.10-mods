/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.effect.EntityLightningBolt
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.item.tool.IHaveReach;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class WeaponToolMeleeBase
extends ItemSword
implements IHaveReach {
    private float addReach;
    private float attackDamage;
    private byte effectHit;
    protected double attackSpeed;
    protected boolean calling;
    protected byte idTool;
    protected final UUID ATTACK_RANGE_MODIFIER = UUID.randomUUID();

    public WeaponToolMeleeBase(Item.ToolMaterial material, String name, double attackspeed, float range, float attackD, boolean fear, byte id) {
        super(material);
        RegistryNames.set(this, "weapon_" + name);
        this.setUnlocalizedName("srparasites.weapon_" + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.attackSpeed = attackspeed;
        this.addReach = range;
        this.attackDamage = attackD;
        this.calling = fear;
        this.idTool = id;
    }

    @Override
    public float getReach() {
        return this.addReach;
    }

    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap multimap = super.getItemAttributeModifiers(equipmentSlot);
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.clear();
            multimap.put((Object)SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), (Object)new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, 0));
            multimap.put((Object)SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), (Object)new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed, 0));
            if (SRPConfig.weaponCancelPacket) {
                multimap.put((Object)EntityPlayer.REACH_DISTANCE.getAttributeUnlocalizedName(), (Object)new AttributeModifier(this.ATTACK_RANGE_MODIFIER, "custom_reach", (double)this.addReach, 0));
            }
        }
        return multimap;
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        boolean flag = super.hitEntity(stack, target, attacker);
        if (flag && target.getHealth() <= 0.0f) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null) {
                compound = new NBTTagCompound();
            }
            if (compound.hasKey("srpkills")) {
                int key = (int)((float)compound.getInteger("srpkills") + target.getMaxHealth());
                compound.setInteger("srpkills", key);
            } else {
                compound.setInteger("srpkills", (int)target.getMaxHealth());
            }
            stack.setTagCompound(compound);
        }
        return flag;
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote) {
            if (this.calling && SRPConfigSystems.useScent && worldIn.rand.nextInt(100) == 0 && entityIn.ticksExisted % 40 == 0 && SRPSaveData.get(worldIn, 222).getDeveLevel() >= SRPConfigSystems.deveScentUse) {
                ((EntityLivingBase)entityIn).addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.PREY_E, 1200, 0, false, false));
            }
            if (entityIn.ticksExisted % 80 == 0) {
                int key = 0;
                NBTTagCompound compound = stack.getTagCompound();
                if (compound != null && this.getNext() != null) {
                    if (compound.hasKey("srpkills")) {
                        key = compound.getInteger("srpkills");
                    }
                    if (key > SRPConfig.weapon_livingSentient_HP_needed) {
                        compound.setInteger("srpkills", 0);
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
    }

    public Item getNext() {
        return null;
    }

    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        super.onCreated(stack, worldIn, playerIn);
    }

    public EnumRarity getRarity(ItemStack stack) {
        return super.getRarity(stack);
    }

    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            tooltip.add(EnumChatFormatting.BLUE + "---> " + compound.getInteger("srpkills"));
            tooltip.add(EnumChatFormatting.BLUE + "  ");
        }
    }
}

