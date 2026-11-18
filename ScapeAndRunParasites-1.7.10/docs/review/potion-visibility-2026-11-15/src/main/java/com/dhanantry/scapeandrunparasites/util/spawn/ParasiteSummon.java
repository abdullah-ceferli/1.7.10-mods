/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.util.spawn;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityDropPod;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityMeteor;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.network.MsgQlipShake;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ParasiteSummon {
    public static void spawnM(EntityParasiteBase entityin, String[] out, int particle, boolean cannotDespawn, String name) {
        if (!entityin.worldObj.isRemote) {
            Random rand = new Random();
            double x = entityin.posX;
            double y = entityin.posY;
            double z = entityin.posZ;
            for (String s : out) {
                if (s == null) continue;
                String[] entityC = s.split(";");
                int max = Integer.parseInt(entityC[1]);
                int min = Integer.parseInt(entityC[2]);
                int total = min == max ? min : rand.nextInt(max - min + 1) + min;
                ResourceLocation entityR = new ResourceLocation(entityC[0]);
                for (int count = 0; count < total; ++count) {
                    EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName((ResourceLocation)entityR, (World)entityin.worldObj);
                    if (entityout == null) {
                        return;
                    }
                    entityout.setLocationAndAngles(x, y + (double)(entityin.height / 2.0f) + 0.5, z, entityin.rotationYaw, entityin.rotationPitch);
                    entityout.onInitialSpawn(DifficultyAccess.at(entityin.worldObj, EntityQueries.position(entityout)), null);
                    if (entityout instanceof EntityParasiteBase && cannotDespawn) {
                        EntityParasiteBase parasite = (EntityParasiteBase)entityout;
                        parasite.cannotDespawn(SRPConfig.convertedDespawn);
                        entityin.worldObj.spawnEntityInWorld((Entity)parasite);
                        continue;
                    }
                    entityin.worldObj.spawnEntityInWorld((Entity)entityout);
                }
            }
        }
    }

    public static boolean SummonM(EntityLivingBase entityin, String[] out, int minRange, int maxRange, @Nullable EntityLivingBase target) {
        if (!entityin.worldObj.isRemote) {
            Random rand = new Random();
            double x = entityin.posX;
            double y = entityin.posY;
            double z = entityin.posZ;
            double randomx = rand.nextInt(maxRange - minRange + 1) + minRange;
            double randomz = rand.nextInt(maxRange - minRange + 1) + minRange;
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
                        randomx = rand.nextInt(maxRange - minRange + 1) + minRange;
                        randomz = rand.nextInt(maxRange - minRange + 1) + minRange;
                        if (rand.nextBoolean()) {
                            randomx *= -1.0;
                        }
                        if (rand.nextBoolean()) {
                            randomz *= -1.0;
                        }
                        if (BlockStates.get(entityin.worldObj, new BlockPos(x + randomx, y, z + randomz)).getBlock() == Blocks.air && BlockStates.get(entityin.worldObj, new BlockPos(x + randomx, y - 1.0, z + randomz)).getBlock() != Blocks.air) {
                            EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(entityC[0]), (World)entityin.worldObj);
                            if (entityout == null) {
                                return false;
                            }
                            entityout.setLocationAndAngles(x + randomx, y, z + randomz, entityin.rotationYaw, entityin.rotationPitch);
                            if (!entityin.worldObj.getCollisionBoxes((Entity)entityout, Boxes.entityBox(entityout)).isEmpty()) {
                                entityout.setDead();
                                ++index;
                                continue;
                            }
                            if (!entityin.worldObj.getEntitiesInAABBexcluding((Entity)entityout, Boxes.grow(Boxes.entityBox(entityout), 0.25), EntitySelectors.selectAnything).isEmpty()) {
                                entityout.setDead();
                                ++index;
                                continue;
                            }
                            entityout.onInitialSpawn(DifficultyAccess.at(entityin.worldObj, EntityQueries.position(entityout)), null);
                            if (entityin instanceof EntityCanSummon) {
                                EntityCanSummon father = (EntityCanSummon)entityin;
                                int points = Integer.parseInt(entityC[2]);
                                if (father.getTotalParasites() - father.getActualParasites() < points) {
                                    entityout.setDead();
                                    ++index;
                                    continue;
                                }
                                father.setActualParasites(points);
                                father.addID(entityout.getEntityId(), points);
                            }
                            if (entityout instanceof EntityPStationaryArchitect) {
                                if (SRPConfigSystems.rsSky && !WorldAccess.canSeeSky(entityout.worldObj, new BlockPos(entityout.posX, entityout.posY + (double)entityout.getEyeHeight(), entityout.posZ))) {
                                    entityout.setDead();
                                    ++index;
                                    continue;
                                }
                                switch (((EntityPStationaryArchitect)entityout).getParasiteIDRegister()) {
                                    case 16: {
                                        EntityVenkrol vOut = (EntityVenkrol)entityout;
                                        entityin.worldObj.spawnEntityInWorld((Entity)vOut);
                                        if (target != null) {
                                            vOut.setAttackTarget(target);
                                        }
                                        entityin.worldObj.setEntityState((Entity)vOut, (byte)50);
                                        break;
                                    }
                                    case 18: {
                                        EntityVenkrolSII vOutii = (EntityVenkrolSII)entityout;
                                        entityin.worldObj.spawnEntityInWorld((Entity)vOutii);
                                        if (target != null) {
                                            vOutii.setAttackTarget(target);
                                        }
                                        entityin.worldObj.setEntityState((Entity)vOutii, (byte)50);
                                        break;
                                    }
                                    case 19: {
                                        EntityVenkrolSIII vOutiii = (EntityVenkrolSIII)entityout;
                                        entityin.worldObj.spawnEntityInWorld((Entity)vOutiii);
                                        if (target != null) {
                                            vOutiii.setAttackTarget(target);
                                        }
                                        entityin.worldObj.setEntityState((Entity)vOutiii, (byte)50);
                                        break;
                                    }
                                    default: {
                                        entityin.worldObj.spawnEntityInWorld((Entity)entityout);
                                        if (target == null) break;
                                        entityout.setAttackTarget(target);
                                    }
                                }
                                return true;
                            }
                            entityin.worldObj.spawnEntityInWorld((Entity)entityout);
                            if (target != null) {
                                entityout.setAttackTarget(target);
                            }
                            flag = false;
                            return true;
                        }
                    }
                }
                ++index;
            }
        }
        return false;
    }

    public static boolean SummonM(EntityParasiteBase entityin, String[] out, int range, double tx, double ty, double tz, @Nullable EntityLivingBase target, boolean pod) {
        if (!entityin.worldObj.isRemote) {
            Random rand = new Random();
            double randomx = rand.nextInt(range);
            double randomz = rand.nextInt(range);
            if (rand.nextBoolean()) {
                randomx *= -1.0;
            }
            if (rand.nextBoolean()) {
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
                        if (BlockStates.get(entityin.worldObj, new BlockPos(tx + randomx, ty, tz + randomz)).getBlock() == Blocks.air && BlockStates.get(entityin.worldObj, new BlockPos(tx + randomx, ty - 1.0, tz + randomz)).getBlock() != Blocks.air || pod) {
                            if (pod) {
                                EntityDropPod entityout2 = new EntityDropPod(entityin.worldObj);
                                entityout2.setLocationAndAngles(tx + randomx, ty, tz + randomz, entityin.rotationYaw, entityin.rotationPitch);
                                entityout2.setOwner(entityin.getParasiteType());
                                entityin.worldObj.spawnEntityInWorld((Entity)entityout2);
                                flag = false;
                                return true;
                            }
                            EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(entityC[0]), (World)entityin.worldObj);
                            if (entityout == null) {
                                return false;
                            }
                            entityout.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
                            entityout.setLocationAndAngles(tx + randomx, ty, tz + randomz, entityin.rotationYaw, entityin.rotationPitch);
                            entityout.onInitialSpawn(DifficultyAccess.at(entityin.worldObj, EntityQueries.position(entityout)), null);
                            if (entityin instanceof EntityCanSummon) {
                                EntityCanSummon father = (EntityCanSummon)((Object)entityin);
                                int points = Integer.parseInt(entityC[2]);
                                if (father.getTotalParasites() - father.getActualParasites() < points) {
                                    ++index;
                                    continue;
                                }
                                father.setActualParasites(points);
                                father.addID(entityout.getEntityId(), points);
                            }
                            if (entityin.getParasiteIDRegister() == 41) {
                                if (entityout instanceof EntityPBeckon) {
                                    EntityPBeckon out2 = (EntityPBeckon)entityout;
                                    out2.setCanGrowTo(false);
                                    out2.setLifeB(300);
                                    out2.worldObj.setEntityState((Entity)out2, (byte)50);
                                }
                            } else if (entityout instanceof EntityVenkrol) {
                                Block looking = BlockStates.get(entityin.worldObj, EntityQueries.position(entityout).down()).getBlock();
                                if (looking == SRPBlocks.InfestedStain) {
                                    return false;
                                }
                                entityout.worldObj.setEntityState((Entity)entityout, (byte)50);
                            }
                            entityin.worldObj.spawnEntityInWorld((Entity)entityout);
                            if (target != null) {
                                entityout.setAttackTarget(target);
                            }
                            flag = false;
                            if (entityout instanceof EntityParasiteBase) {
                                EntityParasiteBase eOut = (EntityParasiteBase)entityout;
                                eOut.setParasiteToFollow(entityin);
                            }
                            return true;
                        }
                        randomx = rand.nextInt(range);
                        randomz = rand.nextInt(range);
                        if (rand.nextBoolean()) {
                            randomx *= -1.0;
                        }
                        if (rand.nextBoolean()) {
                            randomz *= -1.0;
                        }
                    }
                }
                ++index;
            }
        }
        return false;
    }

    public static void spawnMeteor(BlockPos pos, int rad, int minRad, World world) {
        if (!SRPConfigWorld.meteorActive) {
            return;
        }
        if (world == null || world.isRemote) {
            return;
        }
        ParasiteSummon.spawnMeteor(pos.getX(), pos.getY(), pos.getZ(), rad, minRad, world);
    }

    public static void spawnMeteor(int oX, int oY, int oZ, int rad, int minRad, World world) {
        if (rad > SRPConfigWorld.meteorRadius) {
            rad = SRPConfigWorld.meteorRadius;
        }
        if (minRad < SRPConfigWorld.meteorMinRadius) {
            minRad = SRPConfigWorld.meteorMinRadius;
        }
        if (rad < minRad) {
            rad = minRad + 1;
        }
        int min = minRad;
        int max = rad;
        int origenX = world.rand.nextInt(max - min + 1) + min;
        if (world.rand.nextBoolean()) {
            origenX *= -1;
        }
        int origenY = world.getActualHeight();
        int origenZ = world.rand.nextInt(max - min + 1) + min;
        if (world.rand.nextBoolean()) {
            origenZ *= -1;
        }
        int targetX = world.rand.nextInt(max - min + 1) + min;
        if (world.rand.nextBoolean()) {
            targetX *= -1;
        }
        int targetZ = world.rand.nextInt(max - min + 1) + min;
        if (world.rand.nextBoolean()) {
            targetZ *= -1;
        }
        ParasiteSummon.spawnMeteor(oX + origenX, origenY, oZ + origenZ, oX + targetX, oY, oZ + targetZ, world);
    }

    public static void spawnMeteor(int oX, int oY, int oZ, int tX, int tY, int tZ, World world) {
        double tarX = tX;
        double tarY = tY;
        double tarZ = tZ;
        double dx = tarX - (double)oX;
        double dy = tarY - (double)oY;
        double dz = tarZ - (double)oZ;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double speed = 10.5;
        double motionX = (dx /= len) * speed;
        double motionY = (dy /= len) * speed;
        double motionZ = (dz /= len) * speed;
        EntityMeteor meteor = new EntityMeteor(world, oX, oY, oZ, motionX, motionY, motionZ);
        world.spawnEntityInWorld((Entity)meteor);
        for (EntityPlayer mob : EntityQueries.players(world)) {
            SRPNetwork.CHANNEL.sendTo((IMessage)new MsgQlipShake(0, 0, true, false, 0.0f), (EntityPlayerMP)mob);
        }
    }
}

