/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureAttribute
 *  net.minecraft.entity.IEntityMultiPart
 *  net.minecraft.entity.MultiPartEntityPart
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.stats.StatList
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.event.entity.player.CriticalHitEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.network.msg.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class HitboxHit
extends AbstractPacket<HitboxHit> {
    protected int eID;
    public float dmg;
    public boolean isAttack;

    public HitboxHit() {
    }

    public HitboxHit(EntityLiving entity, float damage, boolean isAttack) {
        this.eID = entity.getEntityId();
        this.dmg = damage;
        this.isAttack = isAttack;
    }

    @Override
    public void clientSide(Minecraft client, HitboxHit message, EntityPlayer player, MessageContext messageContext) {
        Entity entity;
        if (player.worldObj != null && (entity = player.worldObj.getEntityByID(message.eID)) instanceof EntityLivingBase) {
            double dist = player.getDistanceToEntity(entity);
            EntityLivingBase mob = (EntityLivingBase)entity;
            if (dist < 100.0) {
                if (message.dmg > 0.0f) {
                    this.attackTargetEntityWithCurrentItem((Entity)mob, player, this.dmg);
                } else {
                    mob.processInitialInteract(player, EnumHand.MAIN_HAND);
                }
            }
        }
    }

    @Override
    public void serverSide(MinecraftServer minecraftServerInstance, HitboxHit message, EntityPlayerMP player, MessageContext messageContext) {
        Entity entity;
        if (player.worldObj != null && (entity = player.worldObj.getEntityByID(message.eID)) instanceof EntityLivingBase) {
            double dist = player.getDistanceToEntity(entity);
            EntityLivingBase mob = (EntityLivingBase)entity;
            if (dist < 100.0) {
                if (message.dmg > 0.0f) {
                    this.attackTargetEntityWithCurrentItem((Entity)mob, (EntityPlayer)player, this.dmg);
                } else {
                    mob.processInitialInteract((EntityPlayer)player, EnumHand.MAIN_HAND);
                }
            }
        }
    }

    public void fromBytes(ByteBuf buf) {
        this.eID = buf.readInt();
        this.dmg = buf.readFloat();
        this.isAttack = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.eID);
        buf.writeFloat(this.dmg);
        buf.writeBoolean(this.isAttack);
    }

    public void attackTargetEntityWithCurrentItem(Entity targetEntity, EntityPlayer player, float dmg) {
        if (!ForgeHooks.onPlayerAttackTarget((EntityPlayer)player, (Entity)targetEntity)) {
            return;
        }
        if (targetEntity.canAttackWithItem() && !targetEntity.hitByEntity((Entity)player)) {
            float f = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue() * dmg;
            float f1 = targetEntity instanceof EntityLivingBase ? EnchantmentHelper.getModifierForCreature((ItemStack)player.getHeldItemMainhand(), (EnumCreatureAttribute)((EntityLivingBase)targetEntity).getCreatureAttribute()) : EnchantmentHelper.getModifierForCreature((ItemStack)player.getHeldItemMainhand(), (EnumCreatureAttribute)EnumCreatureAttribute.UNDEFINED);
            float f2 = player.getCooledAttackStrength(0.5f);
            f1 *= f2;
            player.resetCooldown();
            if ((f *= 0.2f + f2 * f2 * 0.8f) > 0.0f || f1 > 0.0f) {
                ItemStack itemstack;
                boolean flag = f2 > 0.9f;
                boolean flag1 = false;
                int i = 0;
                i += EnchantmentHelper.getRespiration((EntityLivingBase)player);
                if (player.isSprinting() && flag) {
                    player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0f, 1.0f);
                    ++i;
                    flag1 = true;
                }
                boolean flag2 = flag && player.fallDistance > 0.0f && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(net.minecraft.potion.Potion.blindness) && !player.isRiding() && targetEntity instanceof EntityLivingBase;
                flag2 = flag2 && !player.isSprinting();
                CriticalHitEvent hitResult = ForgeHooks.getCriticalHit((EntityPlayer)player, (Entity)targetEntity, (boolean)flag2, (float)(flag2 ? 1.5f : 1.0f));
                boolean bl = flag2 = hitResult != null;
                if (flag2) {
                    f *= hitResult.getDamageModifier();
                }
                f += f1;
                boolean flag3 = false;
                double d0 = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                if (flag && !flag2 && !flag1 && player.onGround && d0 < (double)player.getAIMoveSpeed() && (itemstack = player.getHeldItem(EnumHand.MAIN_HAND)).getItem() instanceof ItemSword) {
                    flag3 = true;
                }
                float f4 = 0.0f;
                boolean flag4 = false;
                int j = EnchantmentHelper.getFireAspectModifier((EntityLivingBase)player);
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
                boolean flag5 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), f);
                if (flag5) {
                    IEntityMultiPart ientitymultipart;
                    if (i > 0) {
                        if (targetEntity instanceof EntityLivingBase) {
                            ((EntityLivingBase)targetEntity).knockBack((Entity)player, (float)i * 0.5f, (double)MathHelper.sin((float)(player.rotationYaw * ((float)Math.PI / 180))), (double)(-MathHelper.cos((float)(player.rotationYaw * ((float)Math.PI / 180)))));
                        } else {
                            targetEntity.addVelocity((double)(-MathHelper.sin((float)(player.rotationYaw * ((float)Math.PI / 180))) * (float)i * 0.5f), 0.1, (double)(MathHelper.cos((float)(player.rotationYaw * ((float)Math.PI / 180))) * (float)i * 0.5f));
                        }
                        player.motionX *= 0.6;
                        player.motionZ *= 0.6;
                        player.setSprinting(false);
                    }
                    if (flag3) {
                        float f3 = 1.0f + EnchantmentHelper.getSweepingDamageRatio((EntityLivingBase)player) * f;
                        for (EntityLivingBase entitylivingbase : EntityQueries.entities(player.worldObj, EntityLivingBase.class, Boxes.grow(Boxes.entityBox(targetEntity), 1.0, 0.25, 1.0))) {
                            if (entitylivingbase == player || entitylivingbase == targetEntity || player.isOnSameTeam((Entity)entitylivingbase) || !(player.getDistanceSqToEntity((Entity)entitylivingbase) < 9.0)) continue;
                            entitylivingbase.knockBack((Entity)player, 0.4f, (double)MathHelper.sin((float)(player.rotationYaw * ((float)Math.PI / 180))), (double)(-MathHelper.cos((float)(player.rotationYaw * ((float)Math.PI / 180)))));
                            entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), f3);
                        }
                        player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0f, 1.0f);
                        player.spawnSweepParticles();
                    }
                    if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                        ((EntityPlayerMP)targetEntity).playerNetServerHandler.sendPacket((Packet)new SPacketEntityVelocity(targetEntity));
                        targetEntity.velocityChanged = false;
                        targetEntity.motionX = d1;
                        targetEntity.motionY = d2;
                        targetEntity.motionZ = d3;
                    }
                    if (flag2) {
                        player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0f, 1.0f);
                        player.onCriticalHit(targetEntity);
                    }
                    if (!flag2 && !flag3) {
                        if (flag) {
                            player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0f, 1.0f);
                        } else {
                            player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0f, 1.0f);
                        }
                    }
                    if (f1 > 0.0f) {
                        player.onEnchantmentCritical(targetEntity);
                    }
                    player.setLastAttacker(targetEntity);
                    if (targetEntity instanceof EntityLivingBase) {
                        EnchantmentHelper.applyThornEnchantments((EntityLivingBase)((EntityLivingBase)targetEntity), (Entity)player);
                    }
                    EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)player, (Entity)targetEntity);
                    ItemStack itemstack1 = player.getHeldItemMainhand();
                    Entity entity = targetEntity;
                    if (targetEntity instanceof MultiPartEntityPart && (ientitymultipart = ((MultiPartEntityPart)targetEntity).entityDragonObj) instanceof EntityLivingBase) {
                        entity = (EntityLivingBase)ientitymultipart;
                    }
                    if (!itemstack1.isEmpty() && entity instanceof EntityLivingBase) {
                        ItemStack beforeHitCopy = itemstack1.copy();
                        itemstack1.hitEntity((EntityLivingBase)entity, player);
                        if (itemstack1.isEmpty()) {
                            ForgeEventFactory.onPlayerDestroyItem((EntityPlayer)player, (ItemStack)beforeHitCopy, (EnumHand)EnumHand.MAIN_HAND);
                            player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                    if (targetEntity instanceof EntityLivingBase) {
                        float f5 = f4 - ((EntityLivingBase)targetEntity).getHealth();
                        player.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0f));
                        if (j > 0) {
                            targetEntity.setFire(j * 4);
                        }
                        if (player.worldObj instanceof WorldServer && f5 > 2.0f) {
                            int k = (int)((double)f5 * 0.5);
                            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)player.worldObj), EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double)(targetEntity.height * 0.5f), targetEntity.posZ, k, 0.1, 0.0, 0.1, 0.2, new int[0]);
                        }
                    }
                    player.addExhaustion(0.1f);
                } else {
                    player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0f, 1.0f);
                    if (flag4) {
                        targetEntity.extinguish();
                    }
                }
            }
        }
    }
}

