/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureAttribute
 *  net.minecraft.entity.IEntityMultiPart
 *  net.minecraft.entity.MultiPartEntityPart
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 *  net.minecraft.stats.StatList
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.entity.player.CriticalHitEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolMeleeBase;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WeaponMeleeScythe
extends WeaponToolMeleeBase {
    public WeaponMeleeScythe(Item.ToolMaterial material, String name, double attackspeed, float range, float attackD, boolean fear, byte id) {
        super(material, name, attackspeed, range, attackD, fear, id);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        super.onCreated(stack, worldIn, playerIn);
    }

    @Override
    public Item getNext() {
        if (this == SRPItems.weapon_scythe) {
            return SRPItems.weapon_scytheSentient;
        }
        return null;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        boolean flag = super.hitEntity(stack, target, attacker);
        if (flag) {
            int key;
            List moblist;
            AxisAlignedBB axisalignedbb;
            int aoe = 4;
            EntityPlayer player = (EntityPlayer)attacker;
            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null) {
                compound = new NBTTagCompound();
            }
            if (this.calling) {
                aoe = 8;
                axisalignedbb = Boxes.grow(Boxes.create(player.posX, player.posY, player.posZ, player.posX + 1.0, player.posY + 1.0, player.posZ + 1.0), (double)aoe);
                moblist = EntityQueries.entities(target.worldObj, EntityLivingBase.class, axisalignedbb);
                for (EntityLivingBase mob : moblist) {
                    if (mob == target || mob == player) continue;
                    this.hitTarget(stack, (Entity)mob, player);
                    if (!(mob.getHealth() <= 0.0f)) continue;
                    if (compound.hasKey("srpkills")) {
                        key = (int)((float)compound.getInteger("srpkills") + target.getMaxHealth());
                        compound.setInteger("srpkills", key);
                        continue;
                    }
                    compound.setInteger("srpkills", (int)target.getMaxHealth());
                }
            }
            axisalignedbb = Boxes.grow(Boxes.create(target.posX, target.posY, target.posZ, target.posX + 1.0, target.posY + 1.0, target.posZ + 1.0), (double)aoe);
            moblist = EntityQueries.entities(target.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                if (mob == target || mob == player) continue;
                this.hitTarget(stack, (Entity)mob, player);
                if (!(mob.getHealth() <= 0.0f)) continue;
                if (compound.hasKey("srpkills")) {
                    key = (int)((float)compound.getInteger("srpkills") + target.getMaxHealth());
                    compound.setInteger("srpkills", key);
                    continue;
                }
                compound.setInteger("srpkills", (int)target.getMaxHealth());
            }
        }
        return flag;
    }

    private void hitTarget(ItemStack stack, Entity targetEntity, EntityPlayer attacker) {
        if (!ForgeHooks.onPlayerAttackTarget((EntityPlayer)attacker, (Entity)targetEntity)) {
            return;
        }
        if (targetEntity.canAttackWithItem() && !targetEntity.hitByEntity((Entity)attacker)) {
            float f = (float)attacker.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
            float f1 = targetEntity instanceof EntityLivingBase ? EnchantmentHelper.getModifierForCreature((ItemStack)attacker.getHeldItemMainhand(), (EnumCreatureAttribute)((EntityLivingBase)targetEntity).getCreatureAttribute()) : EnchantmentHelper.getModifierForCreature((ItemStack)attacker.getHeldItemMainhand(), (EnumCreatureAttribute)EnumCreatureAttribute.UNDEFINED);
            float f2 = attacker.getCooledAttackStrength(0.5f);
            f1 *= f2;
            attacker.resetCooldown();
            if ((f *= 0.2f + f2 * f2 * 0.8f) > 0.0f || f1 > 0.0f) {
                boolean flag = f2 > 0.9f;
                boolean flag1 = false;
                int i = 0;
                i += EnchantmentHelper.getRespiration((EntityLivingBase)attacker);
                if (attacker.isSprinting() && flag) {
                    attacker.worldObj.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, attacker.getSoundCategory(), 1.0f, 1.0f);
                    ++i;
                    flag1 = true;
                }
                boolean flag2 = flag && attacker.fallDistance > 0.0f && !attacker.onGround && !attacker.isOnLadder() && !attacker.isInWater() && !attacker.isPotionActive(net.minecraft.potion.Potion.blindness) && !attacker.isRiding() && targetEntity instanceof EntityLivingBase;
                flag2 = flag2 && !attacker.isSprinting();
                CriticalHitEvent hitResult = ForgeHooks.getCriticalHit((EntityPlayer)attacker, (Entity)targetEntity, (boolean)flag2, (float)(flag2 ? 1.5f : 1.0f));
                boolean bl = flag2 = hitResult != null;
                if (flag2) {
                    f *= hitResult.getDamageModifier();
                }
                f += f1;
                boolean flag3 = false;
                double d0 = attacker.distanceWalkedModified - attacker.prevDistanceWalkedModified;
                if (flag && !flag2 && !flag1 && attacker.onGround && d0 < (double)attacker.getAIMoveSpeed()) {
                    ItemStack itemstack = attacker.getHeldItem(EnumHand.MAIN_HAND);
                    flag3 = itemstack.getItem() instanceof ItemSword;
                }
                float f4 = 0.0f;
                boolean flag4 = false;
                int j = EnchantmentHelper.getFireAspectModifier((EntityLivingBase)attacker);
                if (targetEntity instanceof EntityLivingBase) {
                    f4 = ((EntityLivingBase)targetEntity).getHealth();
                    if (j > 0 && !targetEntity.isBurning()) {
                        flag4 = true;
                        targetEntity.setFire(1);
                    }
                }
                double d1 = targetEntity.motionX;
                double d2 = targetEntity.motionY;
                double d3 = targetEntity.motionZ;
                boolean flag5 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), f);
                if (flag5) {
                    IEntityMultiPart ientitymultipart;
                    if (i > 0) {
                        if (targetEntity instanceof EntityLivingBase) {
                            ((EntityLivingBase)targetEntity).knockBack((Entity)attacker, (float)i * 0.5f, (double)MathHelper.sin((float)(attacker.rotationYaw * ((float)Math.PI / 180))), (double)(-MathHelper.cos((float)(attacker.rotationYaw * ((float)Math.PI / 180)))));
                        } else {
                            targetEntity.addVelocity((double)(-MathHelper.sin((float)(attacker.rotationYaw * ((float)Math.PI / 180))) * (float)i * 0.5f), 0.1, (double)(MathHelper.cos((float)(attacker.rotationYaw * ((float)Math.PI / 180))) * (float)i * 0.5f));
                        }
                        attacker.motionX *= 0.6;
                        attacker.motionZ *= 0.6;
                        attacker.setSprinting(false);
                    }
                    if (flag3) {
                        float f3 = 1.0f + EnchantmentHelper.getSweepingDamageRatio((EntityLivingBase)attacker) * f;
                        for (EntityLivingBase entitylivingbase : EntityQueries.entities(attacker.worldObj, EntityLivingBase.class, Boxes.grow(Boxes.entityBox(targetEntity), 1.0, 0.25, 1.0))) {
                            if (entitylivingbase == attacker || entitylivingbase == targetEntity || attacker.isOnSameTeam((Entity)entitylivingbase) || !(attacker.getDistanceSqToEntity((Entity)entitylivingbase) < 9.0)) continue;
                            entitylivingbase.knockBack((Entity)attacker, 0.4f, (double)MathHelper.sin((float)(attacker.rotationYaw * ((float)Math.PI / 180))), (double)(-MathHelper.cos((float)(attacker.rotationYaw * ((float)Math.PI / 180)))));
                            entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), f3);
                        }
                        attacker.worldObj.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0f, 1.0f);
                        attacker.spawnSweepParticles();
                    }
                    if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                        ((EntityPlayerMP)targetEntity).playerNetServerHandler.sendPacket((Packet)new SPacketEntityVelocity(targetEntity));
                        targetEntity.velocityChanged = false;
                        targetEntity.motionX = d1;
                        targetEntity.motionY = d2;
                        targetEntity.motionZ = d3;
                    }
                    if (flag2) {
                        attacker.worldObj.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, attacker.getSoundCategory(), 1.0f, 1.0f);
                        attacker.onCriticalHit(targetEntity);
                    }
                    if (!flag2 && !flag3) {
                        if (flag) {
                            attacker.worldObj.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, attacker.getSoundCategory(), 1.0f, 1.0f);
                        } else {
                            attacker.worldObj.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, attacker.getSoundCategory(), 1.0f, 1.0f);
                        }
                    }
                    if (f1 > 0.0f) {
                        attacker.onEnchantmentCritical(targetEntity);
                    }
                    attacker.setLastAttacker(targetEntity);
                    if (targetEntity instanceof EntityLivingBase) {
                        EnchantmentHelper.applyThornEnchantments((EntityLivingBase)((EntityLivingBase)targetEntity), (Entity)attacker);
                    }
                    EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)attacker, (Entity)targetEntity);
                    ItemStack itemstack1 = attacker.getHeldItemMainhand();
                    Entity entity = targetEntity;
                    if (targetEntity instanceof MultiPartEntityPart && (ientitymultipart = ((MultiPartEntityPart)targetEntity).entityDragonObj) instanceof EntityLivingBase) {
                        entity = (EntityLivingBase)ientitymultipart;
                    }
                    if (!itemstack1.isEmpty() && entity instanceof EntityLivingBase) {
                        ItemStack beforeHitCopy = itemstack1.copy();
                        stack.damageItem(1, (EntityLivingBase)attacker);
                        if (itemstack1.isEmpty()) {
                            ForgeEventFactory.onPlayerDestroyItem((EntityPlayer)attacker, (ItemStack)beforeHitCopy, (EnumHand)EnumHand.MAIN_HAND);
                            attacker.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                    if (targetEntity instanceof EntityLivingBase) {
                        float f5 = f4 - ((EntityLivingBase)targetEntity).getHealth();
                        attacker.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0f));
                        if (j > 0) {
                            targetEntity.setFire(j * 4);
                        }
                        if (attacker.worldObj instanceof WorldServer && f5 > 2.0f) {
                            int k = (int)((double)f5 * 0.5);
                            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)attacker.worldObj), EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double)(targetEntity.height * 0.5f), targetEntity.posZ, k, 0.1, 0.0, 0.1, 0.2, new int[0]);
                        }
                    }
                    attacker.addExhaustion(0.1f);
                } else {
                    attacker.worldObj.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, attacker.getSoundCategory(), 1.0f, 1.0f);
                    if (flag4) {
                        targetEntity.extinguish();
                    }
                }
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return super.getRarity(stack);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(EnumChatFormatting.YELLOW + I18n.format((String)("tootip.srparasites.weaponm." + this.idTool), (Object[])new Object[0]));
        tooltip.add(EnumChatFormatting.RED + I18n.format((String)("tootip.srparasites.weaponm." + this.idTool * 10), (Object[])new Object[0]));
        if (this.calling && SRPConfigSystems.useScent) {
            tooltip.add(EnumChatFormatting.BLACK + I18n.format((String)("tootip.srparasites.weaponm." + this.idTool * 100), (Object[])new Object[0]));
        }
    }
}

