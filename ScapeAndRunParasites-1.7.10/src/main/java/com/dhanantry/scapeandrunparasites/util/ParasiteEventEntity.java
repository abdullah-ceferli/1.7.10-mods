/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockLog
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.effect.EntityLightningBolt
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.MobEffects
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ClassInheritanceMultiMap
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.util.text.translation.I18n
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  org.apache.logging.log4j.Level
 */
package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCrude;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPHijacked;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityCanraAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityGimAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityRanracAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityWymoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityZaaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityRof;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityTonro;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerBear;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerCow;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerPig;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfBear;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPig;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityGim;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityIki;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityWymo;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityZaa;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAnged;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityFlog;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOmboo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOrch;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityTenn;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileBiomass;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMovingSound;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import com.dhanantry.scapeandrunparasites.world.SRPExplosion;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import org.apache.logging.log4j.Level;

public class ParasiteEventEntity {
    public static boolean canSpawnNext = true;

    public static int entityChunkCount(World world, BlockPos pos, Class<? extends EntityLivingBase> mobC) {
        ClassInheritanceMultiMap[] arrayE = WorldAccess.getChunk(world, pos).getEntityLists();
        int v = 0;
        for (ClassInheritanceMultiMap entities : arrayE) {
            Object[] arrayO;
            if (entities == null) continue;
            for (Object o : arrayO = entities.toArray()) {
                if (o == null || !mobC.isInstance(o)) continue;
                ++v;
            }
        }
        return v;
    }

    public static boolean checkEntity(EntityLivingBase entity, String[] list, boolean inverted) {
        ResourceLocation enti = EntityList.getKey((Entity)entity);
        if (enti != null) {
            return ParasiteEventEntity.checkName(enti.toString(), list, inverted);
        }
        return false;
    }

    public static boolean checkName(String potentialElement, String[] blacklist, boolean isWhitelist) {
        if (potentialElement == null) {
            return false;
        }
        return Arrays.stream(blacklist).anyMatch(potentialElement::contains) != isWhitelist;
    }

    public static void orbApplyEffects(EntityLivingBase target, EntityParasiteBase in, String[] effects, int mobs) {
        for (String i : effects) {
            String[] here = i.split(";");
            try {
                if (here[5] == null) {
                    return;
                }
            }
            catch (Exception e) {
                return;
            }
            Potion potionE = Potion.getPotionFromResourceLocation((String)here[3]);
            if (potionE == null) continue;
            int self = Integer.parseInt(here[0]);
            int duration = Integer.parseInt(here[1]) * 20;
            int amp = Integer.parseInt(here[2]);
            int enemiesA = Integer.parseInt(here[4]);
            int enemiesD = Integer.parseInt(here[5]);
            if (enemiesA != 0) {
                amp += mobs / enemiesA;
            }
            if (enemiesD != 0) {
                duration += mobs / enemiesD * 20;
            }
            if (self == 1) {
                in.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(potionE, duration, amp, false, false));
                continue;
            }
            if (self == 2) {
                if (!(target instanceof EntityParasiteBase)) continue;
                SRPPotions.applyStackPotion(potionE, target, duration, amp);
                continue;
            }
            if (target instanceof EntityParasiteBase) continue;
            SRPPotions.applyStackPotion(potionE, target, duration, amp);
        }
    }

    public static void spawnNext(EntityParasiteBase entityin, EntityParasiteBase entityout, boolean effects, boolean thunder) {
        if (entityin.isDead) {
            return;
        }
        if (entityout == null) {
            return;
        }
        boolean flag = entityin.isBurning();
        entityin.setDead();
        entityout.setLocationAndAngles(entityin.posX, entityin.posY, entityin.posZ, entityin.rotationYaw, entityin.rotationPitch);
        entityout.onInitialSpawn(DifficultyAccess.at(entityin.worldObj, EntityQueries.position(entityout)), null);
        entityout.cannotDespawn(entityin.canDespawn());
        if (entityin.hasCustomInventoryName()) {
            entityout.setMinecartName(entityin.getCustomNameTag());
            entityout.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
        }
        entityin.worldObj.spawnEntityInWorld((Entity)entityout);
        if (entityin instanceof EntityPMalleable && entityout instanceof EntityPMalleable) {
            ((EntityPMalleable)entityout).copyResistancesFrom((EntityPMalleable)entityin);
        }
        if (effects) {
            entityout.particleStatus((byte)7);
        }
        if (thunder && SRPConfig.thunderEnable) {
            entityout.worldObj.addWeatherEffect((Entity)new EntityLightningBolt(entityout.worldObj, entityout.posX, entityout.posY, entityout.posZ, true));
        }
        if (flag) {
            entityout.setHealth(entityout.getMaxHealth() * 0.5f);
            entityout.setFire(8);
        }
    }

    public static void spawnFromList(Entity entityin, String[] out, @Nullable EntityLivingBase target) {
        EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(out[entityin.worldObj.rand.nextInt(out.length)]), (World)entityin.worldObj);
        if (entityout == null) {
            return;
        }
        entityout.copyLocationAndAnglesFrom(entityin);
        entityout.onInitialSpawn(DifficultyAccess.at(entityin.worldObj, EntityQueries.position(entityout)), null);
        entityin.worldObj.spawnEntityInWorld((Entity)entityout);
        if (target != null) {
            entityout.setAttackTarget(target);
        }
    }

    public static boolean spawnBiomassFromProjectile(EntityParasiteBase entityin, String[] out, @Nullable EntityLivingBase target) {
        if (!entityin.worldObj.isRemote) {
            Random rand = new Random();
            int index = rand.nextInt(out.length);
            int limit = 0;
            boolean flag = true;
            while (flag) {
                if (index >= out.length) {
                    index = 0;
                    ++limit;
                }
                if (limit == 2) {
                    return false;
                }
                if (out[index] != null) {
                    String[] entityC = out[index].split(";");
                    double chance = Double.parseDouble(entityC[1]);
                    if (rand.nextDouble() <= chance) {
                        EntityCanSummon father = (EntityCanSummon)((Object)entityin);
                        int points = Integer.parseInt(entityC[2]);
                        if (father.getTotalParasites() - father.getActualParasites() < points) {
                            ++index;
                            continue;
                        }
                        if (target == null) {
                            return false;
                        }
                        Vec3d vec3d = entityin.getLook(1.0f);
                        double d2 = target.posX - (entityin.posX + vec3d.xCoord);
                        double d3 = Boxes.entityBox(target).minY + (double)(target.height / 2.0f) - (0.5 + entityin.posY + (double)(entityin.height / 2.0f));
                        double d4 = target.posZ - (entityin.posZ + vec3d.zCoord);
                        EntityProjectileBiomass entityout = new EntityProjectileBiomass(entityin.worldObj, (EntityLivingBase)entityin, d2, d3, d4);
                        entityout.posX = entityin.posX + vec3d.xCoord;
                        entityout.posY = entityin.posY + (double)entityin.getEyeHeight() - 0.2;
                        entityout.posZ = entityin.posZ + vec3d.zCoord;
                        entityout.setParasite(entityC[0], points, 4);
                        father.setActualParasites(points);
                        father.addID(entityout.getEntityId(), points);
                        entityin.worldObj.spawnEntityInWorld((Entity)entityout);
                        flag = false;
                        return true;
                    }
                }
                ++index;
            }
        }
        return false;
    }

    public static boolean spawnBiomassFromVomit(EntityParasiteBase entityin, String[] out, @Nullable EntityLivingBase target) {
        if (!entityin.worldObj.isRemote) {
            Random rand = new Random();
            int index = rand.nextInt(out.length);
            int limit = 0;
            boolean flag = true;
            while (flag) {
                if (index >= out.length) {
                    index = 0;
                    ++limit;
                }
                if (limit == 2) {
                    return false;
                }
                if (out[index] != null) {
                    String[] entityC = out[index].split(";");
                    double chance = Double.parseDouble(entityC[1]);
                    if (rand.nextDouble() <= chance) {
                        EntityCanSummon father = (EntityCanSummon)((Object)entityin);
                        int points = Integer.parseInt(entityC[2]);
                        if (father.getTotalParasites() - father.getActualParasites() < points) {
                            ++index;
                            continue;
                        }
                        EntityBiomass entityout = new EntityBiomass(entityin.worldObj, entityin, target);
                        entityout.setLocationAndAngles(entityin.posX, entityin.posY, entityin.posZ, entityin.rotationYaw, entityin.rotationPitch);
                        float f19 = MathHelper.sin((float)(entityin.rotationYaw * ((float)Math.PI / 180) - entityin.randomYawVelocity * 0.01f));
                        float f14 = 0.17453292f;
                        float f16 = MathHelper.cos((float)f14);
                        float f4 = MathHelper.cos((float)(entityin.rotationYaw * ((float)Math.PI / 180) - entityin.randomYawVelocity * 0.01f));
                        entityout.rotationYaw = entityin.rotationYaw;
                        if (BlockStates.get(entityout.worldObj, new BlockPos(entityin.posX + -1.0 * (double)(f19 * 3.0f * f16), entityin.posY + (double)entityin.getEyeHeight(), entityin.posZ - -1.0 * (double)(f4 * 3.0f * f16))).getBlock() != Blocks.air) {
                            entityout.setDead();
                            return false;
                        }
                        entityout.setPosition(entityin.posX + -1.0 * (double)(f19 * 3.0f * f16), entityin.posY + (double)entityin.getEyeHeight(), entityin.posZ - -1.0 * (double)(f4 * 3.0f * f16));
                        entityout.setFuse(80);
                        entityout.setParasite(entityC[0], points);
                        if (entityin instanceof EntityCanra) {
                            entityout.setSkin(5);
                        } else {
                            entityout.setSkin(6);
                        }
                        father.setActualParasites(points);
                        father.addID(entityout.getEntityId(), points);
                        if (entityin.isBurning()) {
                            entityout.setFire(8);
                        }
                        entityin.worldObj.spawnEntityInWorld((Entity)entityout);
                        flag = false;
                        return true;
                    }
                }
                ++index;
            }
        }
        return false;
    }

    private static boolean getWorldBeckonSpawnLimit(EntityParasiteBase entityin) {
        int count = 0;
        List entities = entityin.getEntityWorld().loadedEntityList;
        for (Entity entity : entities) {
            if (!(entity instanceof EntityParasiteBase)) continue;
            ++count;
        }
        int players = EntityQueries.players(entityin.worldObj).size();
        return count < SRPConfig.worldMobCap + players * SRPConfig.worldMobCapPlusPlayer + SRPConfig.worldBeckonSpawnsCap;
    }

    public static boolean spawnBiomassFromBeckon(EntityParasiteBase entityin, int stage, EntityLivingBase target, boolean payfather, String[] ground, String[] air) {
        if (!entityin.worldObj.isRemote) {
            String[] mobListG = ground;
            if (entityin.posY + 3.0 <= target.posY) {
                if (stage == 1) {
                    return false;
                }
                mobListG = air;
            }
            Random rand = new Random();
            int index = rand.nextInt(mobListG.length);
            int limit = 0;
            boolean flag = true;
            while (flag) {
                if (index >= mobListG.length) {
                    index = 0;
                    ++limit;
                }
                if (limit == 2) {
                    return false;
                }
                if (mobListG[index] != null) {
                    double k;
                    double d7;
                    double d6;
                    double d5;
                    double d4;
                    double d3;
                    double d2;
                    double d1;
                    double d0;
                    String[] entityC = mobListG[index].split(";");
                    if (entityC.length != 3) {
                        SRPMain.logger.error("Malformed string: " + mobListG[index].toString() + " in the beckon spawn pool configuration, safely exiting loop. Did you forget a semicolon?");
                        return false;
                    }
                    double chance = Double.parseDouble(entityC[1]);
                    EntityCanSummon father = (EntityCanSummon)((Object)entityin);
                    int points = Integer.parseInt(entityC[2]);
                    if (father.getTotalParasites() - father.getActualParasites() < points && payfather) {
                        ++index;
                    }
                    double b = 0.0;
                    if (stage == 3) {
                        b = 0.5;
                    }
                    if (!ParasiteEventEntity.getWorldBeckonSpawnLimit(entityin)) {
                        EntityBomb entityAlt = new EntityBomb(entityin.worldObj, entityin, false);
                        if (entityin.getEntityWorld().rand.nextInt() < 40) {
                            return false;
                        }
                        if (entityin.getAttackTarget() != null) {
                            entityAlt.setLocationAndAngles(entityin.posX, entityin.posY + ((double)entityin.getEyeHeight() + b), entityin.posZ, entityin.rotationYaw, entityin.rotationPitch);
                            d0 = (float)entityin.posX + entityin.worldObj.rand.nextFloat();
                            d1 = (float)entityin.posY + entityin.getEyeHeight() + entityin.worldObj.rand.nextFloat();
                            d2 = (float)entityin.posZ + entityin.worldObj.rand.nextFloat();
                            d3 = d0 - entityin.posX;
                            d4 = d1 - entityin.posY;
                            d5 = d2 - entityin.posZ;
                            d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
                            d3 /= d6;
                            d4 /= d6;
                            d5 /= d6;
                            d7 = 0.5 / (d6 / 4.0 + 0.1);
                            d7 *= (double)(entityin.worldObj.rand.nextFloat() * entityin.worldObj.rand.nextFloat() + 1.7f);
                            k = 3.0;
                            if (stage == 3) {
                                k = 5.0;
                            }
                            d5 = d7 * k;
                            entityAlt.setMotion(d3 *= d7 * k, d4 *= d7 * 2.0, d5, 0.4, 0.5);
                            entityAlt.setFuse(60);
                            entityAlt.setStren(0.0f);
                            entityAlt.setSkin(1);
                            entityAlt.setDamage((float)entityin.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue(), 2);
                            entityAlt.rotationPitch -= -20.0f;
                            entityin.worldObj.spawnEntityInWorld((Entity)entityAlt);
                            entityAlt.updateSTR();
                            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(entityin.posX, entityin.posY, entityin.posZ, 0.5f, 0.5f, 10));
                        }
                    } else {
                        EntityBiomass entityout = new EntityBiomass(entityin.worldObj, entityin, stage, target, payfather);
                        entityout.setLocationAndAngles(entityin.posX, entityin.posY + ((double)entityin.getEyeHeight() + b), entityin.posZ, entityin.rotationYaw, entityin.rotationPitch);
                        d0 = (float)entityin.posX + entityin.worldObj.rand.nextFloat();
                        d1 = (float)entityin.posY + entityin.getEyeHeight() + entityin.worldObj.rand.nextFloat();
                        d2 = (float)entityin.posZ + entityin.worldObj.rand.nextFloat();
                        d3 = d0 - entityin.posX;
                        d4 = d1 - entityin.posY;
                        d5 = d2 - entityin.posZ;
                        d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
                        d3 /= d6;
                        d4 /= d6;
                        d5 /= d6;
                        d7 = 0.5 / (d6 / 4.0 + 0.1);
                        d7 *= (double)(entityin.worldObj.rand.nextFloat() * entityin.worldObj.rand.nextFloat() + 1.7f);
                        k = 3.0;
                        if (stage == 3) {
                            k = 5.0;
                        }
                        d5 = d7 * k;
                        entityout.setMotion(d3 *= d7 * k, d4 *= d7 * 2.0, d5, 0.4, 0.5);
                        entityout.setFuse(80);
                        entityout.setParasite(entityC[0], points);
                        entityout.setSkin(stage);
                        if (entityin.isBurning()) {
                            entityout.setFire(8);
                        }
                        entityin.worldObj.spawnEntityInWorld((Entity)entityout);
                        if (payfather) {
                            father.setActualParasites(points);
                            father.addID(entityout.getEntityId(), points);
                        }
                        flag = false;
                        return true;
                    }
                }
                ++index;
            }
        }
        return false;
    }

    public static void convertEntity(EntityLivingBase entityin, NBTTagCompound tags, boolean ignoreKey, String[] list) {
        if (entityin == null) {
            return;
        }
        World world = entityin.worldObj;
        if (world.isRemote) {
            return;
        }
        if (SRPSaveData.get(entityin.worldObj, 105).getEvolutionPhase(entityin.dimension) >= SRPConfigSystems.evolutionFeralNoSim && ParasiteEventEntity.convertEntityFeral(entityin, tags, true, list)) {
            return;
        }
        if (tags.hasKey("srpcothimmunity")) {
            String mobname;
            int goo;
            int key = tags.getInteger("srpcothimmunity");
            if (key == 0 && !ignoreKey) {
                entityin.removeActivePotionEffect(SRPPotions.COTH_E);
                return;
            }
            entityin.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 100, 3, false, false));
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(entityin.posX, entityin.posY, entityin.posZ, entityin.width, entityin.height, 1));
            tags.setInteger("srpcothimmunity", ++key);
            if (key < 3 && !ignoreKey) {
                return;
            }
            SRPSaveData dataLol = SRPSaveData.get(entityin.worldObj, 103);
            int n = goo = SRPConfigSystems.disloCOTHTiers ? dataLol.getCurrentCode(entityin.worldObj.provider.dimensionId, 1) : 0;
            if (goo != 0) {
                EntityParasiteBase halo = ParasiteEventEntity.getRandomFeral(entityin.worldObj);
                if (goo >= SRPConfigSystems.disloCOTHTiersValue1) {
                    halo = ParasiteEventEntity.getRandomPrimitive(entityin.worldObj);
                }
                if (goo >= SRPConfigSystems.disloCOTHTiersValue2) {
                    halo = ParasiteEventEntity.getRandomAdapted(entityin.worldObj);
                }
                if (goo >= SRPConfigSystems.disloCOTHTiersValue3) {
                    halo = ParasiteEventEntity.getRandomPure(entityin.worldObj);
                }
                halo.copyLocationAndAnglesFrom((Entity)entityin);
                world.removeEntity((Entity)entityin);
                halo.onInitialSpawn(DifficultyAccess.at(world, EntityQueries.position(halo)), null);
                if (entityin.hasCustomInventoryName()) {
                    halo.setMinecartName(entityin.getCustomNameTag());
                    halo.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
                }
                world.spawnEntityInWorld((Entity)halo);
                world.playEvent(null, 1026, new BlockPos((Entity)halo), 0);
                halo.particleStatus((byte)7);
                halo.cannotDespawn(SRPConfig.convertedDespawn);
                if (key >= 10) {
                    halo.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.EPEL_E, 600, 0, false, false));
                }
                return;
            }
            try {
                mobname = EntityList.getKey((Entity)entityin).toString();
            }
            catch (Exception e) {
                SRPMain.logger.log(Level.ERROR, "Problem while converting entity", (Throwable)e);
                ParasiteEventEntity.spawnInsider(entityin, world, tags);
                return;
            }
            boolean flag = true;
            for (String s : list) {
                EntityPInfected entityout;
                String[] here = s.split(";");
                try {
                    if (here[0] == null || here[1] == null) {
                        ParasiteEventEntity.spawnInsider(entityin, world, tags);
                        return;
                    }
                }
                catch (Exception e) {
                    SRPMain.logger.log(Level.ERROR, "Problem while converting entity", (Throwable)e);
                    ParasiteEventEntity.spawnInsider(entityin, world, tags);
                    return;
                }
                if (!here[0].equals(mobname)) continue;
                Entity outOne = EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(here[1]), (World)world);
                if (outOne == null) {
                    ParasiteEventEntity.spawnInsider(entityin, world, tags);
                    return;
                }
                if (outOne instanceof EntityPInfected) {
                    entityout = (EntityPInfected)outOne;
                    SRPSaveData.get(world, 104).addNumberIDDataSpawn(entityout.getParasiteIDRegister());
                    entityout.copyLocationAndAnglesFrom((Entity)entityin);
                    world.removeEntity((Entity)entityin);
                    entityout.setHost(mobname);
                    entityout.onInitialSpawn(DifficultyAccess.at(world, EntityQueries.position(entityout)), null);
                    if (entityin.hasCustomInventoryName()) {
                        entityout.setMinecartName(entityin.getCustomNameTag());
                        entityout.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
                    }
                    world.spawnEntityInWorld((Entity)entityout);
                    world.playEvent(null, 1026, new BlockPos((Entity)entityout), 0);
                    if (SRPConfigSystems.generationUse) {
                        entityout.setHealth(entityout.getHealth() * ParasiteEventEntity.getSimCOTHMod(dataLol, world));
                    }
                    entityout.particleStatus((byte)7);
                    entityout.cannotDespawn(SRPConfig.convertedDespawn);
                    if (key >= 10) {
                        entityout.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.EPEL_E, 600, 0, false, false));
                    }
                    AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(EntityQueries.position(entityout)), 14.0);
                    List moblist = EntityQueries.entities(entityout.worldObj, EntityLivingBase.class, axisalignedbb);
                    for (EntityLivingBase mob : moblist) {
                        if (!mob.isPotionActive(SRPPotions.COTH_E) || !(tags = mob.getEntityData()).hasKey("srpcothimmunity") || (key = tags.getInteger("srpcothimmunity")) != 1 || mob.getActivePotionEffect(SRPPotions.COTH_E).getAmplifier() <= 1) continue;
                        tags.setInteger("srpcothimmunity", ++key);
                    }
                } else if (outOne instanceof EntityLiving) {
                    entityout = (EntityLiving)outOne;
                    entityout.copyLocationAndAnglesFrom((Entity)entityin);
                    world.removeEntity((Entity)entityin);
                    entityout.onInitialSpawn(DifficultyAccess.at(world, EntityQueries.position(entityout)), null);
                    if (entityin.hasCustomInventoryName()) {
                        entityout.setMinecartName(entityin.getCustomNameTag());
                        entityout.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
                    }
                    world.spawnEntityInWorld((Entity)entityout);
                    world.playEvent(null, 1026, new BlockPos((Entity)entityout), 0);
                }
                flag = false;
            }
            if (flag && !ignoreKey) {
                ParasiteEventEntity.spawnInsider(entityin, world, tags);
            }
        }
    }

    public static void spawnInsider(EntityLivingBase entity, World world, NBTTagCompound tags) {
        if (!SRPConfigMobs.inhooSEnabled || !SRPConfigMobs.inhooMEnabled) {
            return;
        }
        List serverList = EntityQueries.loaded(world);
        int count = 0;
        for (Entity value : serverList) {
            if (!(value instanceof EntityInhooM) && !(value instanceof EntityInhooS)) continue;
            ++count;
        }
        if (count > SRPConfig.incompleteCap) {
            world.removeEntity((Entity)entity);
            return;
        }
        if (tags.hasKey("srpcothimmunity")) {
            int key = tags.getInteger("srpcothimmunity");
            if (key == 0) {
                entity.removeActivePotionEffect(SRPPotions.COTH_E);
                return;
            }
            entity.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 100, 3, false, false));
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(entity.posX, entity.posY, entity.posZ, entity.width, entity.height, 1));
            tags.setInteger("srpcothimmunity", ++key);
            if (key < 3) {
                return;
            }
            EntityPCrude out = new EntityInhooS(world);
            float mass = ParasiteEventEntity.getEntityArea(entity);
            if ((double)mass > 0.517) {
                out = new EntityInhooM(world);
            }
            out.copyLocationAndAnglesFrom((Entity)entity);
            world.removeEntity((Entity)entity);
            world.spawnEntityInWorld((Entity)out);
            world.playEvent(null, 1026, new BlockPos((Entity)out), 0);
            out.particleStatus((byte)7);
            out.cannotDespawn(SRPConfig.convertedDespawn);
            if (SRPConfigSystems.generationUse) {
                out.setHealth(out.getHealth() * ParasiteEventEntity.getSimCOTHMod(SRPSaveData.get(world, 102), world));
            }
            int range = 1;
            double i1 = MathHelper.floor_double((double)(out.posY + 0.1));
            double l1 = out.posX;
            double i2 = out.posZ;
            int counttt = 0;
            count = 2;
            for (int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
                for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                    double i3 = l1 + (double)k2;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = new BlockPos(i3, i1, l);
                    Block block = BlockStates.get(out.worldObj, blockpos).getBlock();
                    Block blockDown = BlockStates.get(out.worldObj, blockpos.down()).getBlock();
                    if (block != Blocks.air || blockDown == Blocks.air || !BlockStates.get(world, blockpos.down()).isFullBlock() || blockDown == SRPBlocks.InfestedStain || out.worldObj.rand.nextInt(4) != 0) continue;
                    BlockStates.set(out.worldObj, blockpos, BlockStates.defaultState(SRPBlocks.goreSim).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.FLAT)));
                    if (++counttt < count) continue;
                    return;
                }
            }
        }
    }

    private static float getEntityArea(EntityLivingBase entity) {
        return entity.width * entity.width * entity.height;
    }

    private static float getSimCOTHMod(SRPSaveData data, World world) {
        switch (data.getGeneration(world.provider.dimensionId)) {
            case 0: {
                return SRPConfigSystems.generationCOTH0;
            }
            case 1: {
                return SRPConfigSystems.generationCOTH1;
            }
            case 2: {
                return SRPConfigSystems.generationCOTH2;
            }
            case 3: {
                return SRPConfigSystems.generationCOTH3;
            }
            case 4: {
                return SRPConfigSystems.generationCOTH4;
            }
            case 5: {
                return SRPConfigSystems.generationCOTH5;
            }
        }
        return 1.0f;
    }

    public static boolean convertEntityFeral(EntityLivingBase entityin, NBTTagCompound tags, boolean ignoreKey, String[] list) {
        World world = entityin.worldObj;
        if (world.isRemote) {
            return false;
        }
        if (entityin == null) {
            return false;
        }
        if (tags.hasKey("srpcothimmunity")) {
            String mobname;
            int key = tags.getInteger("srpcothimmunity");
            if (key == 0 && !ignoreKey) {
                entityin.removeActivePotionEffect(SRPPotions.COTH_E);
                return false;
            }
            entityin.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 100, 3, false, false));
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(entityin.posX, entityin.posY, entityin.posZ, entityin.width, entityin.height, 1));
            tags.setInteger("srpcothimmunity", ++key);
            if (key < 3 && !ignoreKey) {
                return false;
            }
            try {
                mobname = EntityList.getKey((Entity)entityin).toString();
            }
            catch (Exception e) {
                SRPMain.logger.log(Level.ERROR, "Problem while converting entity", (Throwable)e);
                ParasiteEventEntity.spawnInsider(entityin, world, tags);
                return false;
            }
            boolean flag = true;
            for (String s : list) {
                String[] here = s.split(";");
                try {
                    if (here[0] == null || here[1] == null) {
                        ParasiteEventEntity.spawnInsider(entityin, world, tags);
                        return false;
                    }
                }
                catch (Exception e) {
                    SRPMain.logger.log(Level.ERROR, "Problem while converting entity", (Throwable)e);
                    ParasiteEventEntity.spawnInsider(entityin, world, tags);
                    return false;
                }
                if (!here[0].equals(mobname)) continue;
                Entity outOne = EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(here[1]), (World)world);
                if (outOne == null) {
                    ParasiteEventEntity.spawnInsider(entityin, world, tags);
                    return false;
                }
                if (outOne instanceof EntityPInfected) {
                    EntityPFeral gaa = ((EntityPInfected)outOne).getFeral(world);
                    if (gaa != null) {
                        outOne.setDead();
                        gaa.copyLocationAndAnglesFrom((Entity)entityin);
                        world.removeEntity((Entity)entityin);
                        gaa.onInitialSpawn(DifficultyAccess.at(world, EntityQueries.position(gaa)), null);
                        if (entityin.hasCustomInventoryName()) {
                            gaa.setMinecartName(entityin.getCustomNameTag());
                            gaa.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
                        }
                        world.spawnEntityInWorld((Entity)gaa);
                        world.playEvent(null, 1026, new BlockPos((Entity)gaa), 0);
                        gaa.particleStatus((byte)7);
                        gaa.cannotDespawn(SRPConfig.convertedDespawn);
                    } else {
                        EntityPInfected entityout = (EntityPInfected)outOne;
                        SRPSaveData.get(world, 101).addNumberIDDataSpawn(entityout.getParasiteIDRegister());
                        entityout.copyLocationAndAnglesFrom((Entity)entityin);
                        world.removeEntity((Entity)entityin);
                        entityout.setHost(mobname);
                        entityout.onInitialSpawn(DifficultyAccess.at(world, EntityQueries.position(entityout)), null);
                        if (entityin.hasCustomInventoryName()) {
                            entityout.setMinecartName(entityin.getCustomNameTag());
                            entityout.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
                        }
                        world.spawnEntityInWorld((Entity)entityout);
                        world.playEvent(null, 1026, new BlockPos((Entity)entityout), 0);
                        entityout.particleStatus((byte)7);
                        entityout.cannotDespawn(SRPConfig.convertedDespawn);
                        if (key >= 10) {
                            entityout.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.EPEL_E, 600, 0, false, false));
                        }
                        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(EntityQueries.position(entityout)), 14.0);
                        List moblist = EntityQueries.entities(entityout.worldObj, EntityLivingBase.class, axisalignedbb);
                        for (EntityLivingBase mob : moblist) {
                            if (!mob.isPotionActive(SRPPotions.COTH_E) || !(tags = mob.getEntityData()).hasKey("srpcothimmunity") || (key = tags.getInteger("srpcothimmunity")) != 1 || mob.getActivePotionEffect(SRPPotions.COTH_E).getAmplifier() <= 1) continue;
                            tags.setInteger("srpcothimmunity", ++key);
                        }
                    }
                    return true;
                }
                if (outOne instanceof EntityLiving) {
                    EntityLiving entityout = (EntityLiving)outOne;
                    entityout.copyLocationAndAnglesFrom((Entity)entityin);
                    world.removeEntity((Entity)entityin);
                    entityout.onInitialSpawn(DifficultyAccess.at(world, EntityQueries.position(entityout)), null);
                    if (entityin.hasCustomInventoryName()) {
                        entityout.setMinecartName(entityin.getCustomNameTag());
                        entityout.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
                    }
                    world.spawnEntityInWorld((Entity)entityout);
                    world.playEvent(null, 1026, new BlockPos((Entity)entityout), 0);
                    return true;
                }
                flag = false;
            }
            if (flag && !ignoreKey) {
                ParasiteEventEntity.spawnInsider(entityin, world, tags);
            }
        }
        return false;
    }

    public static boolean hijackEntity(EntityLivingBase entityin, String[] list) {
        String mobname;
        if (entityin == null) {
            return false;
        }
        World world = entityin.worldObj;
        if (world.isRemote) {
            return false;
        }
        try {
            mobname = EntityList.getKey((Entity)entityin).toString();
        }
        catch (Exception e) {
            SRPMain.logger.log(Level.ERROR, "Problem while converting entity", (Throwable)e);
            return false;
        }
        boolean flag = true;
        for (String s : list) {
            EntityPHijacked entityout;
            String[] here = s.split(";");
            try {
                if (here[0] == null || here[1] == null) {
                    return false;
                }
            }
            catch (Exception e) {
                SRPMain.logger.log(Level.ERROR, "Problem while converting entity", (Throwable)e);
                return false;
            }
            if (!here[0].equals(mobname)) continue;
            Entity outOne = EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(here[1]), (World)world);
            if (outOne == null) {
                return false;
            }
            if (outOne instanceof EntityPHijacked) {
                entityout = (EntityPHijacked)outOne;
                SRPSaveData.get(world, 98).addNumberIDDataSpawn(entityout.getParasiteIDRegister());
                entityout.copyLocationAndAnglesFrom((Entity)entityin);
                world.removeEntity((Entity)entityin);
                entityout.onInitialSpawn(DifficultyAccess.at(world, EntityQueries.position(entityout)), null);
                if (entityin.hasCustomInventoryName()) {
                    entityout.setMinecartName(entityin.getCustomNameTag());
                    entityout.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
                }
                world.spawnEntityInWorld((Entity)entityout);
                world.playEvent(null, 1026, new BlockPos((Entity)entityout), 0);
                entityout.particleStatus((byte)7);
                entityout.cannotDespawn(SRPConfig.convertedDespawn);
            } else if (outOne instanceof EntityLiving) {
                entityout = (EntityLiving)outOne;
                entityout.copyLocationAndAnglesFrom((Entity)entityin);
                world.removeEntity((Entity)entityin);
                entityout.onInitialSpawn(DifficultyAccess.at(world, EntityQueries.position(entityout)), null);
                if (entityin.hasCustomInventoryName()) {
                    entityout.setMinecartName(entityin.getCustomNameTag());
                    entityout.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
                }
                world.spawnEntityInWorld((Entity)entityout);
                world.playEvent(null, 1026, new BlockPos((Entity)entityout), 0);
            }
            flag = false;
        }
        return false;
    }

    public static BlockPos getFloor(World worldIn, BlockPos pos, int loop) {
        if (loop <= 0) {
            return null;
        }
        --loop;
        if (BlockStates.get(worldIn, pos).getBlock() == Blocks.air) {
            if (BlockStates.get(worldIn, pos.down()).getBlock() != Blocks.air) {
                return pos;
            }
            return ParasiteEventEntity.getFloor(worldIn, pos.down(), loop);
        }
        return ParasiteEventEntity.getFloor(worldIn, pos.up(), loop);
    }

    public static BlockPos getFloorBuilding(World worldIn, BlockPos pos, int loop) {
        if (loop <= 0) {
            return null;
        }
        --loop;
        if (!BlockStates.get(worldIn, pos).isFullBlock()) {
            if (BlockStates.get(worldIn, pos.down()).isFullBlock() && !(BlockStates.get(worldIn, pos.down()).getBlock() instanceof BlockBush) && !(BlockStates.get(worldIn, pos.down()).getBlock() instanceof BlockLeaves) && !(BlockStates.get(worldIn, pos.down()).getBlock() instanceof BlockLog)) {
                return pos;
            }
            return ParasiteEventEntity.getFloorBuilding(worldIn, pos.down(), loop);
        }
        if (BlockStates.get(worldIn, pos).getBlock() instanceof BlockBush || BlockStates.get(worldIn, pos).getBlock() instanceof BlockLeaves || BlockStates.get(worldIn, pos).getBlock() instanceof BlockLog) {
            return ParasiteEventEntity.getFloorBuilding(worldIn, pos.down(1), loop);
        }
        return ParasiteEventEntity.getFloorBuilding(worldIn, pos.up(), loop);
    }

    public static boolean spawnTurrets(EntityLivingBase entityin, int range, byte type, int stage) {
        if (stage <= 2) {
            return false;
        }
        if (DifficultyAccess.difficulty(entityin.worldObj) == EnumDifficulty.PEACEFUL) {
            return false;
        }
        Random rand = new Random();
        World world = entityin.worldObj;
        double randomx = rand.nextInt(range);
        double randomz = rand.nextInt(range);
        double negative = rand.nextInt(2);
        if (negative == 0.0) {
            randomx *= -1.0;
        }
        if ((negative = (double)rand.nextInt(2)) == 0.0) {
            randomz *= -1.0;
        }
        int index = 5;
        int limit = 0;
        boolean flag = true;
        while (flag) {
            if (limit >= 5) {
                return false;
            }
            BlockPos floor = ParasiteEventEntity.getFloor(world, new BlockPos(entityin.posX + randomx, entityin.posY, entityin.posZ + randomz), 5);
            if (floor != null && BlockStates.get(world, floor.down()).getBlock() == SRPBlocks.InfestedStain) {
                int flag2 = 0;
                AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create((double)floor.getX(), (double)floor.getY(), (double)floor.getZ(), (double)(floor.getX() + 1), (double)(floor.getY() + 1), (double)(floor.getZ() + 1)), 42.0, 5.0, 42.0);
                List moblist = EntityQueries.entities(world, EntityParasiteBase.class, axisalignedbb);
                for (EntityParasiteBase mob : moblist) {
                    if (!mob.isEntityAlive() || mob.getParasiteType() != 40) continue;
                    ++flag2;
                }
                if (flag2 < 3) {
                    EntityPStationary out;
                    if (type == 1) {
                        if (!SRPConfigMobs.unvoEnabled) {
                            return false;
                        }
                        out = new EntityUnvo(world);
                        out.setLocationAndAngles(floor.getX(), floor.getY(), floor.getZ(), 0.0f, 0.0f);
                        world.spawnEntityInWorld((Entity)out);
                        out.setAttackTarget(entityin);
                        return true;
                    }
                    if (type == 2) {
                        if (!SRPConfigMobs.tonroEnabled) {
                            return false;
                        }
                        out = new EntityTonro(world);
                        out.setLocationAndAngles(floor.getX(), floor.getY(), floor.getZ(), 0.0f, 0.0f);
                        world.spawnEntityInWorld((Entity)out);
                        out.setAttackTarget(entityin);
                        return true;
                    }
                } else {
                    return false;
                }
            }
            randomx = rand.nextInt(range);
            randomz = rand.nextInt(range);
            negative = rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            ++limit;
        }
        return false;
    }

    public static void alertAllPlayerDim(World worldIn, String message, int warning) {
        if (worldIn == null) {
            return;
        }
        List playerEntityList = EntityQueries.players(worldIn);
        SRPMain.network.sendToDimension((IMessage)new SRPPacketMovingSound(warning), worldIn.provider.dimensionId);
        if (!message.equals("")) {
            for (EntityPlayer entityPlayer : playerEntityList) {
                entityPlayer.addChatMessage((IChatComponent)new ChatComponentText(message));
            }
        }
        if (warning == -7 && message.equals("Phase decreased")) {
            List serverList = EntityQueries.loaded(worldIn);
            for (Entity entity : serverList) {
                if (!(entity instanceof EntityParasiteBase)) continue;
                ((EntityParasiteBase)entity).addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.RAGE_E, 2400, 1, false, false));
            }
        }
    }

    public static void alertAllPlayerSer(World w, String message) {
        if (w == null) {
            return;
        }
        List playerEntityList = w.getMinecraftServer().getPlayerList().getPlayers();
        for (EntityPlayerMP entityPlayerMP : playerEntityList) {
            entityPlayerMP.addChatMessage((IChatComponent)new ChatComponentText(message));
        }
    }

    public static void alertAllPlayerSer(World w, String message, int warning) {
        if (w == null) {
            return;
        }
        List playerEntityList = w.getMinecraftServer().getPlayerList().getPlayers();
        SRPMain.network.sendToServer((IMessage)new SRPPacketMovingSound(warning));
        for (EntityPlayerMP entityPlayerMP : playerEntityList) {
            entityPlayerMP.addChatMessage((IChatComponent)new ChatComponentText(message));
        }
    }

    public static boolean spawnFromBlock(World world, String[] out, int range, BlockPos pos) {
        if (!world.isRemote) {
            List serverList = EntityQueries.loaded(world);
            int count = 0;
            int tenn = 0;
            for (int x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityParasiteBase)) continue;
                ++count;
                if (serverList.get(x) instanceof EntityTenn) {
                    ++tenn;
                }
                if (count > SRPConfig.worldMobCap) {
                    return false;
                }
                if (tenn <= 5) continue;
                return false;
            }
            Random rand = new Random();
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            double randomx = rand.nextInt(range);
            double randomz = rand.nextInt(range);
            double negative = rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            int index = rand.nextInt(out.length);
            int limit = 0;
            boolean flag = true;
            while (flag) {
                if (index >= out.length) {
                    index = 0;
                    ++limit;
                }
                if (limit == 2) {
                    return false;
                }
                if (out[index] != null) {
                    String[] entityC = out[index].split(";");
                    double chance = Double.parseDouble(entityC[1]);
                    if (rand.nextDouble() <= chance) {
                        BlockPos helper = ParasiteEventEntity.getFloor(world, new BlockPos(x + randomx, y, z + randomz), 3);
                        if (helper != null) {
                            EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(entityC[0]), (World)world);
                            if (entityout == null) {
                                return false;
                            }
                            entityout.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
                            entityout.setLocationAndAngles((double)helper.getX(), (double)helper.getY(), (double)helper.getZ(), 0.0f, 0.0f);
                            entityout.onInitialSpawn(DifficultyAccess.at(world, helper), null);
                            if (entityout instanceof EntityKol) {
                                EntityKol kol = (EntityKol)entityout;
                                SRPWorldData data = SRPWorldData.get(world);
                                BlockPos origin = data.nearestColonyPosition(helper, false);
                                if (origin != null) {
                                    kol.setTask(origin, data.getColonyDistanceSpreadByPosition(origin, false));
                                } else {
                                    return false;
                                }
                            }
                            world.spawnEntityInWorld((Entity)entityout);
                            flag = false;
                            return true;
                        }
                        randomx = rand.nextInt(range);
                        randomz = rand.nextInt(range);
                        negative = rand.nextInt(2);
                        if (negative == 0.0) {
                            randomx *= -1.0;
                        }
                        if ((negative = (double)rand.nextInt(2)) == 0.0) {
                            randomz *= -1.0;
                        }
                    }
                }
                ++index;
            }
        }
        return false;
    }

    public static void spawnBeckon(World world, DamageSource cause, EntityParasiteBase in) {
        if (in.width <= 1.0f && in.height <= 1.0f) {
            return;
        }
        if (SRPConfigSystems.rsEnabled) {
            List serverList = EntityQueries.loaded(world);
            int count = 0;
            for (Entity entity : serverList) {
                if (!(entity instanceof EntityPBeckon) || ++count <= SRPConfig.nexusVenkrolCap && !(in.getDistanceSqToEntity(entity) < (double)(SRPConfig.nexusVenkrolDis * SRPConfig.nexusVenkrolDis))) continue;
                return;
            }
            SRPWorldData data = SRPWorldData.get(world);
            if (SRPConfigSystems.rsPlayer) {
                if (cause.getEntity() instanceof EntityPlayer) {
                    if (SRPConfigSystems.useEvolution) {
                        ParasiteEventEntity.spawnBeckonE(data, world, in);
                    } else {
                        ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.rschance, world, in);
                    }
                }
            } else if (SRPConfigSystems.useEvolution) {
                ParasiteEventEntity.spawnBeckonE(data, world, in);
            } else {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.rschance, world, in);
            }
        }
    }

    public static void spawnBeckonNE(SRPWorldData data, double chance, World world, EntityParasiteBase in) {
        long worldT = world.getTotalWorldTime();
        long seconds = (worldT - SRPAttributes.lastTimeD1) / 20L;
        Random rand = new Random();
        if (rand.nextDouble() < chance && (long)SRPConfigSystems.rsCooldown < Math.abs(seconds)) {
            if (SRPConfigWorld.originActivated && data.nearestInfectionValue(EntityQueries.position(in), false) == -1) {
                return;
            }
            if (ParasiteSummon.SummonM((EntityLivingBase)in, new String[]{ParasiteEventEntity.getRSColony(data)}, 5, 10, in.getAttackTarget())) {
                if (SRPConfigSystems.rsSounds) {
                    if (SRPConfigSystems.disloGrowlNoise) {
                        if (SRPSaveData.get(world, 94).getCurrentCode(world.provider.dimensionId, 15) == 0) {
                            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(in, SRPSounds.VENKROLSI, 4.0f, 1.0f);
                        }
                    } else {
                        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(in, SRPSounds.VENKROLSI, 4.0f, 1.0f);
                    }
                }
                SRPAttributes.lastTimeD1 = worldT;
            }
        }
    }

    public static String getRSColony(SRPWorldData data) {
        if (SRPConfigWorld.coloniesActivated) {
            int totalColonyPoints = data.totalColonyPoints(0);
            double bonus = (float)totalColonyPoints / SRPConfigWorld.colonyExtraRSChancePoint * SRPConfigWorld.colonyExtraRSChanceValue;
            if (bonus > 2.0) {
                return "srparasites:beckon_siii;1;1";
            }
            if (bonus > 1.0) {
                return "srparasites:beckon_sii;1;1";
            }
        }
        return "srparasites:beckon_si;1;1";
    }

    public static void spawnBeckonE(SRPWorldData data, World world, EntityParasiteBase in) {
        switch (SRPSaveData.get(world, 93).getEvolutionPhase(world.provider.dimensionId)) {
            case 1: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceOne, world, in);
                break;
            }
            case 2: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceTwo, world, in);
                break;
            }
            case 3: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceThree, world, in);
                break;
            }
            case 4: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceFour, world, in);
                break;
            }
            case 5: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceFive, world, in);
                break;
            }
            case 6: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceSix, world, in);
                break;
            }
            case 7: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceSeven, world, in);
                break;
            }
            case 8: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceEight, world, in);
                break;
            }
            case 9: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceNine, world, in);
                break;
            }
            case 10: {
                ParasiteEventEntity.spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceTen, world, in);
            }
        }
    }

    public static double getRSchance(World world) {
        switch (SRPSaveData.get(world, 92).getEvolutionPhase(world.provider.dimensionId)) {
            case 1: {
                return SRPConfigSystems.reinforcementSystemChanceOne;
            }
            case 2: {
                return SRPConfigSystems.reinforcementSystemChanceTwo;
            }
            case 3: {
                return SRPConfigSystems.reinforcementSystemChanceThree;
            }
            case 4: {
                return SRPConfigSystems.reinforcementSystemChanceFour;
            }
            case 5: {
                return SRPConfigSystems.reinforcementSystemChanceFive;
            }
            case 6: {
                return SRPConfigSystems.reinforcementSystemChanceSix;
            }
            case 7: {
                return SRPConfigSystems.reinforcementSystemChanceSeven;
            }
            case 8: {
                return SRPConfigSystems.reinforcementSystemChanceEight;
            }
            case 9: {
                return SRPConfigSystems.reinforcementSystemChanceNine;
            }
            case 10: {
                return SRPConfigSystems.reinforcementSystemChanceTen;
            }
        }
        return 0.0;
    }

    public static boolean alertOthers(EntityParasiteBase pin, EntityLivingBase target, World world, int loop) {
        return false;
    }

    public static void leaveScent(World world, DamageSource cause, EntityParasiteBase in) {
        if (!SRPConfigSystems.useScent) {
            return;
        }
        if (SRPConfigSystems.scentPlayer ? !(cause.getEntity() instanceof EntityPlayer) : !(cause.getEntity() instanceof EntityLivingBase)) {
            return;
        }
        if (world.rand.nextDouble() < SRPConfigSystems.scentDeathSpawning) {
            return;
        }
        if (SRPConfigSystems.useEvolution && in.getPhaseCreated() < SRPConfigSystems.evolutionOneMind && in.getLevelCreated() < SRPConfigSystems.deveOnemindUse) {
            return;
        }
        List serverList = EntityQueries.loaded(world);
        int count = 0;
        for (Entity entity : serverList) {
            if (!(entity instanceof EntityParasiticScent)) continue;
            ++count;
        }
        if (count > SRPConfigSystems.scentCap) {
            return;
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(in.posX, in.posY, in.posZ, in.posX + 1.0, in.posY + 1.0, in.posZ + 1.0), 64.0);
        List moblist1 = EntityQueries.entities(world, EntityParasiticScent.class, axisalignedbb);
        for (EntityParasiticScent mob : moblist1) {
            Entity source;
            if (!in.canEntityBeSeen(mob)) continue;
            mob.increaseDanger(in.getCCDeathValue(), true);
            mob.increaseActivity(1, true);
            mob.setScentLife(mob.getScentLife() + 20 * SRPConfigSystems.scentLifeDeath);
            mob.setScentReaction(ParasiteEventEntity.getScentReactionBonus(in.getPhaseCreated()), false);
            mob.setTargetToKill((EntityLivingBase)cause.getEntity(), true);
            if (cause.getEntity() instanceof EntityPlayer && (source = cause.getEntity()) instanceof EntityPlayer) {
                ((EntityPlayer)source).addChatComponentMessage((IChatComponent)new ChatComponentTranslation("srp.msg.scent.closest_notified", new Object[0]), true);
            }
            return;
        }
        axisalignedbb = Boxes.grow(Boxes.create(in.posX, in.posY, in.posZ, in.posX + 1.0, in.posY + 1.0, in.posZ + 1.0), 64.0);
        List moblist2 = EntityQueries.entities(world, EntityParasiteBase.class, axisalignedbb);
        int dangerValue = in.getCCDeathValue() + ParasiteEventEntity.getScentBonus(in.getPhaseCreated());
        if (moblist2.size() <= 3 && in.getPhaseCreated() >= 0 && in.getCCDeathValue() > 2) {
            EntityParasiticScent nut = new EntityParasiticScent(world, 0, (EntityLivingBase)cause.getEntity());
            nut.copyLocationAndAnglesFrom(cause.getEntity());
            nut.setScentLife(SRPConfigSystems.scentLifeObserver * 20);
            nut.increaseDanger(dangerValue, true);
            nut.setScentReaction(ParasiteEventEntity.getScentReactionBonus(in.getPhaseCreated()), false);
            world.spawnEntityInWorld((Entity)nut);
            nut.warnPlayers(I18n.translateToLocal((String)"srp.msg.scent.deployed_area"));
        }
    }

    public static int getScentBonus(byte in) {
        int q = 1;
        if (SRPConfigSystems.useEvolution) {
            switch (in) {
                case 0: {
                    q = SRPConfigSystems.phaseScentBonusZero;
                    break;
                }
                case 1: {
                    q = SRPConfigSystems.phaseScentBonusOne;
                    break;
                }
                case 2: {
                    q = SRPConfigSystems.phaseScentBonusTwo;
                    break;
                }
                case 3: {
                    q = SRPConfigSystems.phaseScentBonusThree;
                    break;
                }
                case 4: {
                    q = SRPConfigSystems.phaseScentBonusFour;
                    break;
                }
                case 5: {
                    q = SRPConfigSystems.phaseScentBonusFive;
                    break;
                }
                case 6: {
                    q = SRPConfigSystems.phaseScentBonusSix;
                    break;
                }
                case 7: {
                    q = SRPConfigSystems.phaseScentBonusSeven;
                    break;
                }
                case 8: {
                    q = SRPConfigSystems.phaseScentBonusEight;
                }
            }
        }
        return q;
    }

    public static byte getScentReactionBonus(byte in) {
        byte q = SRPConfigSystems.scentGoActive;
        if (SRPConfigSystems.useEvolution) {
            switch (in) {
                case 0: {
                    q = SRPConfigSystems.phaseScentReactionZero;
                    break;
                }
                case 1: {
                    q = SRPConfigSystems.phaseScentReactionOne;
                    break;
                }
                case 2: {
                    q = SRPConfigSystems.phaseScentReactionTwo;
                    break;
                }
                case 3: {
                    q = SRPConfigSystems.phaseScentReactionThree;
                    break;
                }
                case 4: {
                    q = SRPConfigSystems.phaseScentReactionFour;
                    break;
                }
                case 5: {
                    q = SRPConfigSystems.phaseScentReactionFive;
                    break;
                }
                case 6: {
                    q = SRPConfigSystems.phaseScentReactionSix;
                    break;
                }
                case 7: {
                    q = SRPConfigSystems.phaseScentReactionSeven;
                    break;
                }
                case 8: {
                    q = SRPConfigSystems.phaseScentReactionEight;
                }
            }
        }
        return q;
    }

    public static void checkColony(World world, DamageSource cause, EntityPMalleable in) {
        if (in.isBurning() || !SRPConfigWorld.coloniesActivated) {
            return;
        }
        if (ParasiteEventWorld.numberofColonies(world) <= 0) {
            return;
        }
        double chance = 0.0;
        if (in.isPotionActive(SRPPotions.LINK_E)) {
            chance = (double)(in.getActivePotionEffect(SRPPotions.LINK_E).getAmplifier() + 1) * SRPConfigSystems.adapsChance;
        }
        if (ParasiteEventWorld.rangeOfColony(world, EntityQueries.position(in), true) != null || world.rand.nextDouble() < chance) {
            String da;
            SRPWorldData data = SRPWorldData.get(world);
            if (in.colonySpawned) {
                in.removeCommonDamage(data.getMostCommonDamageS(), data.getMostCommonDamageI());
            }
            if ((da = in.getMostCommonDamage()) == null) {
                return;
            }
            data.addGlobalResistance(da);
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(in.posX, in.posY, in.posZ, in.width, in.height, 4));
        }
    }

    public static SRPExplosion createExplosion(World worldIn, @Nullable Entity entityIn, double x, double y, double z, float strength, boolean isSmoking) {
        SRPExplosion explosion = new SRPExplosion(worldIn, entityIn, x, y, z, strength, false, isSmoking);
        if (ForgeEventFactory.onExplosionStart((World)worldIn, (Explosion)explosion)) {
            return explosion;
        }
        explosion.doExplosionA();
        explosion.doExplosionB(false);
        return explosion;
    }

    public static boolean teleportDigging(EntityParasiteBase in, float maxHardness, BlockPos posIn, int range, int mini) {
        if (!in.isPotionActive(net.minecraft.potion.Potion.moveSlowdown)) {
            in.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 80, 4, false, false));
            return false;
        }
        Random rand = new Random();
        double x = posIn.getX();
        double y = posIn.getY();
        double z = posIn.getZ();
        double randomx = rand.nextInt(range) + mini;
        double randomz = rand.nextInt(range) + mini;
        double negative = rand.nextInt(2);
        if (negative == 0.0) {
            randomx *= -1.0;
        }
        if ((negative = (double)rand.nextInt(2)) == 0.0) {
            randomz *= -1.0;
        }
        int limit = 0;
        boolean flag = true;
        while (flag) {
            if (limit >= 5) {
                return false;
            }
            BlockPos pos = new BlockPos(x + randomx, y, z + randomz);
            if ((pos = ParasiteEventEntity.getFloor(in.worldObj, pos, 5)) != null && BlockStates.get(in.worldObj, pos.down()).isFullCube()) {
                float bHard = 0.0f;
                for (int i = 1; i < 4; ++i) {
                    IBlockState state = BlockStates.get(in.worldObj, pos.down(i));
                    float atm = state.getBlockHardness(in.worldObj, pos.down(i));
                    if (atm <= 0.0f) {
                        return false;
                    }
                    bHard += atm;
                }
                if (bHard >= maxHardness) {
                    return false;
                }
                AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), 1.0);
                List moblist = EntityQueries.entities(in.worldObj, EntityNak.class, axisalignedbb);
                if (moblist.isEmpty()) {
                    in.setLocationAndAngles((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, in.rotationYaw, in.rotationPitch);
                    flag = false;
                    return true;
                }
            }
            randomx = rand.nextInt(range) + mini;
            randomz = rand.nextInt(range) + mini;
            negative = rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            ++limit;
        }
        return false;
    }

    public static EntityParasiteBase getRandomAssimilated(World world) {
        switch (world.rand.nextInt(9)) {
            case 0: {
                if (!SRPConfigMobs.infbearEnabled) break;
                return new EntityInfBear(world);
            }
            case 1: {
                if (!SRPConfigMobs.infcowEnabled) break;
                return new EntityInfCow(world);
            }
            case 2: {
                if (!SRPConfigMobs.infendermanEnabled) break;
                return new EntityInfEnderman(world);
            }
            case 3: {
                if (!SRPConfigMobs.infhorseEnabled) break;
                return new EntityInfHorse(world);
            }
            case 4: {
                if (!SRPConfigMobs.infhumanEnabled) break;
                return new EntityInfHuman(world);
            }
            case 5: {
                if (!SRPConfigMobs.infpigEnabled) break;
                return new EntityInfPig(world);
            }
            case 6: {
                if (!SRPConfigMobs.infsheepEnabled) break;
                return new EntityInfSheep(world);
            }
            case 7: {
                if (!SRPConfigMobs.infvillagerEnabled) break;
                return new EntityInfVillager(world);
            }
            case 8: {
                if (!SRPConfigMobs.infwolfEnabled) break;
                return new EntityInfWolf(world);
            }
        }
        return null;
    }

    public static EntityParasiteBase getRandomFeral(World world) {
        switch (world.rand.nextInt(9)) {
            case 0: {
                if (!SRPConfigMobs.ferbearEnabled) break;
                return new EntityFerBear(world);
            }
            case 1: {
                if (!SRPConfigMobs.fercowEnabled) break;
                return new EntityFerCow(world);
            }
            case 2: {
                if (!SRPConfigMobs.ferendermanEnabled) break;
                return new EntityFerEnderman(world);
            }
            case 3: {
                if (!SRPConfigMobs.ferhorseEnabled) break;
                return new EntityFerHorse(world);
            }
            case 4: {
                if (!SRPConfigMobs.ferhumanEnabled) break;
                return new EntityFerHuman(world);
            }
            case 5: {
                if (!SRPConfigMobs.ferpigEnabled) break;
                return new EntityFerPig(world);
            }
            case 6: {
                if (!SRPConfigMobs.fersheepEnabled) break;
                return new EntityFerSheep(world);
            }
            case 7: {
                if (!SRPConfigMobs.fervillagerEnabled) break;
                return new EntityFerVillager(world);
            }
            case 8: {
                if (!SRPConfigMobs.ferwolfEnabled) break;
                return new EntityFerWolf(world);
            }
        }
        return null;
    }

    public static EntityParasiteBase getRandomAssimara(World world) {
        switch (world.rand.nextInt(9)) {
            case 1: {
                if (!SRPConfigMobs.fercowEnabled) break;
                return new EntitySpeCow(world);
            }
            case 2: {
                if (!SRPConfigMobs.ferendermanEnabled) break;
                return new EntitySpeEnderman(world);
            }
            case 4: {
                if (!SRPConfigMobs.ferhumanEnabled) break;
                return new EntitySpeHuman(world);
            }
            case 7: {
                if (!SRPConfigMobs.fervillagerEnabled) break;
                return new EntitySpeVillager(world);
            }
        }
        return null;
    }

    public static EntityParasiteBase getRandomPrimitive(World world) {
        switch (world.rand.nextInt(11)) {
            case 0: {
                if (!SRPConfigMobs.emanaEnabled) break;
                return new EntityEmana(world);
            }
            case 1: {
                if (!SRPConfigMobs.canraEnabled) break;
                return new EntityCanra(world);
            }
            case 2: {
                if (!SRPConfigMobs.zetmoEnabled) break;
                return new EntityBano(world);
            }
            case 3: {
                if (!SRPConfigMobs.shycoEnabled) break;
                return new EntityShyco(world);
            }
            case 4: {
                if (!SRPConfigMobs.arachnidaEnabled) break;
                return new EntityRanrac(world);
            }
            case 5: {
                if (!SRPConfigMobs.noglaEnabled) break;
                return new EntityNogla(world);
            }
            case 6: {
                if (!SRPConfigMobs.hullEnabled) break;
                return new EntityHull(world);
            }
            case 7: {
                if (!SRPConfigMobs.ikiEnabled) break;
                return new EntityIki(world);
            }
            case 8: {
                if (!SRPConfigMobs.wymoEnabled) break;
                return new EntityWymo(world);
            }
            case 9: {
                if (!SRPConfigMobs.zaaEnabled) break;
                return new EntityZaa(world);
            }
            case 10: {
                if (!SRPConfigMobs.gimEnabled) break;
                return new EntityGim(world);
            }
        }
        return null;
    }

    public static EntityParasiteBase getRandomAdapted(World world) {
        switch (world.rand.nextInt(10)) {
            case 0: {
                if (!SRPConfigMobs.emanaEnabled) break;
                return new EntityEmanaAdapted(world);
            }
            case 1: {
                if (!SRPConfigMobs.canraEnabled) break;
                return new EntityCanraAdapted(world);
            }
            case 2: {
                if (!SRPConfigMobs.zetmoEnabled) break;
                return new EntityBanoAdapted(world);
            }
            case 3: {
                if (!SRPConfigMobs.shycoEnabled) break;
                return new EntityShycoAdapted(world);
            }
            case 4: {
                if (!SRPConfigMobs.arachnidaEnabled) break;
                return new EntityRanracAdapted(world);
            }
            case 5: {
                if (!SRPConfigMobs.noglaEnabled) break;
                return new EntityNoglaAdapted(world);
            }
            case 6: {
                if (!SRPConfigMobs.hullEnabled) break;
                return new EntityHullAdapted(world);
            }
            case 7: {
                if (!SRPConfigMobs.wymoEnabled) break;
                return new EntityWymoAdapted(world);
            }
            case 8: {
                if (!SRPConfigMobs.zaaEnabled) break;
                return new EntityZaaAdapted(world);
            }
            case 9: {
                if (!SRPConfigMobs.gimEnabled) break;
                return new EntityGimAdapted(world);
            }
        }
        return null;
    }

    public static EntityParasiteBase getRandomPure(World world) {
        switch (world.rand.nextInt(7)) {
            case 0: {
                if (!SRPConfigMobs.alafhaEnabled) break;
                return new EntityAlafha(world);
            }
            case 1: {
                if (!SRPConfigMobs.angedEnabled) break;
                return new EntityAnged(world);
            }
            case 2: {
                if (!SRPConfigMobs.esorEnabled) break;
                return new EntityEsor(world);
            }
            case 3: {
                if (!SRPConfigMobs.flogEnabled) break;
                return new EntityFlog(world);
            }
            case 4: {
                if (!SRPConfigMobs.ganroEnabled) break;
                return new EntityGanro(world);
            }
            case 5: {
                if (!SRPConfigMobs.ombooEnabled) break;
                return new EntityOmboo(world);
            }
            case 6: {
                if (!SRPConfigMobs.orchEnabled) break;
                return new EntityOrch(world);
            }
        }
        return null;
    }

    public static boolean spawnUnitFromRof(World world, EntityLivingBase target, BlockPos poss, String[] out, int min, int max) {
        if ((poss = ParasiteEventEntity.getFloor(world, poss, 10)) == null) {
            return false;
        }
        EntityRof samuel = new EntityRof(world);
        samuel.setPosition((double)poss.getX() + 0.5, poss.getY(), (double)poss.getZ() + 0.5);
        if (!samuel.worldObj.getCollisionBoxes((Entity)samuel, Boxes.expand(Boxes.entityBox(samuel), 1.0, 7.0, 1.0)).isEmpty()) {
            samuel.setDead();
            return false;
        }
        samuel.setMob(out);
        samuel.maxmob = max;
        samuel.minmob = min;
        samuel.setPeek(true);
        samuel.setBuried();
        samuel.setAttackTarget(target);
        world.spawnEntityInWorld((Entity)samuel);
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(world, null, EntityQueries.position(samuel), SRPSounds.ROF_EMERGE, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, 2.0f, 1.0f);
        world.setEntityState((Entity)samuel, (byte)50);
        samuel.targetScent = target;
        if (target == null) {
            samuel.targetScent = samuel;
        }
        return true;
    }

    public static boolean disloNumber2(World world) {
        List serverList = EntityQueries.loaded(world);
        int count = 0;
        EntityLivingBase in = null;
        for (int i = 0; i < serverList.size(); ++i) {
            int atm;
            if (!(serverList.get(i) instanceof EntityLivingBase) || !((EntityLivingBase)serverList.get(i)).isPotionActive(SRPPotions.JUGG_E) || count > (atm = ((EntityLivingBase)serverList.get(i)).getActivePotionEffect(SRPPotions.JUGG_E).getAmplifier())) continue;
            count = atm;
            in = (EntityLivingBase)serverList.get(i);
        }
        if (in != null && count >= SRPConfigSystems.disloSummonByDeathKilling) {
            Random rand = new Random();
            int count2 = SRPSaveData.get(world, 99).getCurrentCode(world.provider.dimensionId, 2);
            for (int loop = 0; loop < 10; ++loop) {
                double randomx = rand.nextInt(4);
                double randomz = rand.nextInt(4);
                if (rand.nextBoolean()) {
                    randomx *= -1.0;
                }
                if (rand.nextBoolean()) {
                    randomz *= -1.0;
                }
                if (!ParasiteEventEntity.spawnUnitFromRof(world, in, new BlockPos(in.posX + randomx, in.posY, in.posZ + randomz), new String[]{ParasiteEventEntity.disloNumber2A(count2, world.rand)}, 0, 0)) continue;
                SRPSaveData.get(world, 91).setCurrentCode(world.provider.dimensionId, 2, 0, 0, world, false, 0);
                return true;
            }
        }
        return false;
    }

    private static String disloNumber2A(int dama, Random rand) {
        int i;
        String[] here;
        int cc = 0;
        for (int i2 = 0; i2 < SRPConfigSystems.disloSummonByDeathMobs.length; ++i2) {
            int min;
            if (SRPConfigSystems.disloSummonByDeathMobs[i2] == null || (min = Integer.parseInt((here = SRPConfigSystems.disloSummonByDeathMobs[i2].split(";"))[0])) >= dama || min <= cc) continue;
            cc = min;
        }
        ArrayList<String> mobList = new ArrayList<String>();
        for (i = 0; i < SRPConfigSystems.disloSummonByDeathMobs.length; ++i) {
            if (SRPConfigSystems.disloSummonByDeathMobs[i] == null || cc != Integer.parseInt((here = SRPConfigSystems.disloSummonByDeathMobs[i].split(";"))[0])) continue;
            mobList.add(here[1]);
        }
        if (mobList.isEmpty()) {
            for (i = 0; i < SRPConfigSystems.disloSummonByDeathMobs.length; ++i) {
                if (SRPConfigSystems.disloSummonByDeathMobs[i] == null || (here = SRPConfigSystems.disloSummonByDeathMobs[i].split(";")).length <= 1) continue;
                return here[1];
            }
            return "srparasites:warden";
        }
        return (String)mobList.get(rand.nextInt(mobList.size()));
    }

    public static boolean disloNumber5(EntityLivingBase target, World world) {
        return false;
    }
}

