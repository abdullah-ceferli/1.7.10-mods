/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityVillager
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIInfectedSearch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSpawn;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityPInfected
extends EntityParasiteBase
implements EntityCanSpawn {
    private String host;
    private int check;
    private int timer;
    protected boolean thisMelting;

    public EntityPInfected(World worldIn) {
        super(worldIn);
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, true, false, null, SRPConfig.infectedSneakPen, SRPConfig.infectedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, true, false, new Predicate<EntityLiving>(){

                public boolean apply(@Nullable EntityLiving entity) {
                    return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.infectedSneakPen, SRPConfig.infectedInviPen));
        }
        this.experienceValue = SRPAttributes.XP_INFECTED;
        this.damageCap = SRPConfig.infectedCap;
        this.canD = SRPConfig.infecteddespawn;
        this.MiniDamage = SRPConfig.infectedMinDamage;
        this.oneMindDeathValue = SRPConfig.infectedOneMindDeathV;
        this.foodSteal = 0.1f;
        this.cothSpread = SRPConfigSystems.cothInfected;
        this.check = 0;
        this.timer = 0;
        this.host = "";
        this.valueEvDeath = SRPConfig.infectedLoosingEPValue;
        this.thisMelting = false;
        this.setScentHPMultiplier(2.5f);
    }

    @Override
    public int getIDSpawn() {
        return this.getParasiteIDRegister();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.srpTicks == 5 && !this.worldObj.isRemote) {
            if (ParasiteEventEntity.canSpawnNext) {
                EntityPFeral out;
                if (this.killcount > SRPConfig.primitiveKills && this.getLevelCreated() >= SRPConfigSystems.deveMergeUse && this.thisMelting) {
                    this.tasks.addTask(3, (EntityAIBase)new EntityAIInfectedSearch(this, 1.1));
                    this.killcount = 0.0;
                }
                if (this.killcount > SRPConfig.feralKills && (out = this.getFeral(this.worldObj)) != null) {
                    ParasiteEventEntity.spawnNext(this, out, true, false);
                    return;
                }
            }
            if (this.host.length() != 0) {
                if (this.check == 0) {
                    EntityLivingBase entityout = (EntityLivingBase)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(this.host), (World)this.worldObj);
                    if (entityout != null && !(entityout instanceof EntityMob) && SRPConfig.infectedAssimilation) {
                        this.check = 2;
                        this.worldObj.setEntityState((Entity)this, (byte)14);
                    } else {
                        this.check = 1;
                        this.worldObj.setEntityState((Entity)this, (byte)15);
                    }
                }
                ++this.timer;
                if (this.check >= 2 && this.timer > 5) {
                    if (SRPConfigSystems.useEvolution) {
                        if (this.phaseCreated < SRPConfigSystems.evolutionAssimilatedDehiding) {
                            this.transform();
                        }
                    } else {
                        this.transform();
                    }
                }
            }
            this.InfectNearby((EntityLivingBase)this, SRPConfigSystems.cothAura);
        }
        if (this.check >= 3 && this.worldObj.isRemote) {
            this.spawnParticles(SRPEnumParticle.GSPLASH, 0, 0, 0);
        }
    }

    @Override
    public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
        super.setAttackTarget(entitylivingbaseIn);
        this.check = 0;
        this.worldObj.setEntityState((Entity)this, (byte)17);
    }

    @Override
    protected void fearPlayer(EntityLivingBase player) {
    }

    protected void fearPlayer(EntityLivingBase player, float damageDealt) {
        if (player == null || player.worldObj.isRemote) {
            return;
        }
        if (damageDealt <= 8.0f) {
            return;
        }
        int level = 1 + Math.max(0, (int)Math.floor((damageDealt - 8.0f) / 4.0f));
        int cap = 3;
        level = Math.min(level, cap);
        int duration = 300 + 40 * (level - 1);
        duration = MathHelper.clamp_int((int)duration, (int)200, (int)500);
        int amplifier = level - 1;
        player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, duration, amplifier, false, true));
    }

    private void transform() {
        if (this.getAttackTarget() != null || this.isPotionActive(SRPPotions.EPEL_E)) {
            this.timer = -1;
            return;
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 10.0, 5.0, 10.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        boolean flag = true;
        for (EntityLivingBase mob : moblist) {
            NBTTagCompound tags;
            EntityPlayer player;
            if (mob == this || mob instanceof EntityParasiteBase || mob instanceof EntityPlayer && (player = (EntityPlayer)mob).isCreative() || (tags = mob.getEntityData()).hasKey("srpcothimmunity") && tags.getInteger("srpcothimmunity") != 0) continue;
            flag = false;
            this.check = 2;
            this.timer = 0;
            this.worldObj.setEntityState((Entity)this, (byte)14);
            break;
        }
        if (flag) {
            ++this.check;
            this.worldObj.setEntityState((Entity)this, (byte)16);
            this.addPotionEffect(new PotionEffect(MobEffects.moveSlowdown, 60, 3, false, false));
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, this.width, this.height, 1));
            if (this.timer < 8) {
                return;
            }
            EntityLivingBase entityout = (EntityLivingBase)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(this.host), (World)this.worldObj);
            entityout.copyLocationAndAnglesFrom((Entity)this);
            this.particleStatus((byte)7);
            this.worldObj.removeEntity((Entity)this);
            if (this.hasCustomInventoryName()) {
                entityout.setMinecartName(this.getCustomNameTag());
                entityout.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
            }
            entityout.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 2, false, false));
            this.worldObj.spawnEntityInWorld((Entity)entityout);
            this.worldObj.playEvent((EntityPlayer)null, 1026, new BlockPos((Entity)entityout), 0);
        }
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            float dealt;
            EntityLivingBase target = (EntityLivingBase)entityIn;
            float before = target.getHealth();
            boolean hit = super.attackEntityAsMob(entityIn);
            if (hit && !this.worldObj.isRemote && (dealt = Math.max(0.0f, before - target.getHealth())) > 0.0f) {
                this.fearPlayer(target, dealt);
            }
            return hit;
        }
        return super.attackEntityAsMob(entityIn);
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        this.timer = -200;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void attackEntityFromEffects(int range, int count) {
        this.particleStatus((byte)51);
        double i1 = MathHelper.floor_double((double)(this.posY + 0.1));
        double l1 = this.posX;
        double i2 = this.posZ;
        int counttt = 0;
        for (int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
            for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                BlockPos blockpos = new BlockPos(i3, i1, l);
                Block block = BlockStates.get(this.worldObj, blockpos).getBlock();
                Block blockDown = BlockStates.get(this.worldObj, blockpos.down()).getBlock();
                if (block != Blocks.air || blockDown == Blocks.air || !BlockStates.get(this.worldObj, blockpos.down()).isFullBlock() || blockDown == SRPBlocks.InfestedStain || this.worldObj.rand.nextInt(4) != 0) continue;
                BlockStates.set(this.worldObj, blockpos, BlockStates.defaultState(SRPBlocks.goreSim).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.FLAT)));
                if (++counttt < count) continue;
                return;
            }
        }
    }

    @Override
    protected void attackEntityFromCap(int go) {
        for (int i = 0; i < go && SRPConfig.paraGore; ++i) {
            double d0 = (float)this.posX + this.worldObj.rand.nextFloat();
            double d1 = (float)this.posY + this.worldObj.rand.nextFloat();
            double d2 = (float)this.posZ + this.worldObj.rand.nextFloat();
            double d3 = d0 - this.posX;
            double d4 = d1 - this.posY;
            double d5 = d2 - this.posZ;
            double d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.8 / (d6 / 4.0 + 0.1);
            d4 = d4 * d7 * 2.0;
            EntityGore bomb = new EntityGore(this.worldObj);
            bomb.setType((byte)1);
            bomb.copyLocationAndAnglesFrom((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)), d4, d5 *= d7, 0.2, 0.8);
            this.worldObj.spawnEntityInWorld((Entity)bomb);
        }
    }

    @Override
    protected void spawnGore() {
        this.attackEntityFromEffects(2, 100);
        if (BlockStates.get(this.worldObj, EntityQueries.position(this).down()).isFullBlock() && (BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() instanceof BlockBush || BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() == Blocks.air)) {
            BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(SRPBlocks.goreSim).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(this.worldObj);
            nnn.setLocationAndAngles((double)EntityQueries.position(this).getX() + 0.5, EntityQueries.position(this).getY(), (double)EntityQueries.position(this).getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(EntityList.getKey((Entity)this).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.infectedRemainValue);
            this.worldObj.spawnEntityInWorld((Entity)nnn);
        }
        this.attackEntityFromCap(3);
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
        if (!this.worldObj.isRemote && this.killcount > SRPConfig.primitiveKills && this.getLevelCreated() >= SRPConfigSystems.deveMergeUse && this.thisMelting) {
            this.tasks.addTask(3, (EntityAIBase)new EntityAIInfectedSearch(this, 1.1));
            this.killcount = 0.0;
        }
    }

    private void InfectNearby(EntityLivingBase entity, int range) {
        if (SRPConfigSystems.cothAura == 0) {
            return;
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0, entity.posY + 1.0, entity.posZ + 1.0), (double)range);
        List moblist = EntityQueries.entities(entity.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (mob == entity || mob.isPotionActive(SRPPotions.COTH_E) || mob.isPotionActive(SRPPotions.EPEL_E)) continue;
            mob.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 4800, 0, false, false));
        }
    }

    public void setHost(String mobname) {
        this.host = mobname;
    }

    public String getHost() {
        return this.host;
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        Item wea = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem();
        if (wea == SRPItems.itemAssimilate && this.host.length() != 0 && hand == EnumHand.MAIN_HAND) {
            this.worldObj.setEntityState((Entity)this, (byte)16);
            this.addPotionEffect(new PotionEffect(MobEffects.moveSlowdown, 60, 3, false, false));
            SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(this.posX, this.posY, this.posZ, this.width, this.height, 1));
            EntityLivingBase entityout = (EntityLivingBase)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(this.host), (World)this.worldObj);
            entityout.copyLocationAndAnglesFrom((Entity)this);
            this.particleStatus((byte)7);
            this.worldObj.removeEntity((Entity)this);
            if (this.hasCustomInventoryName()) {
                entityout.setMinecartName(this.getCustomNameTag());
                entityout.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
            }
            entityout.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 2, false, false));
            this.worldObj.spawnEntityInWorld((Entity)entityout);
            this.worldObj.playEvent((EntityPlayer)null, 1026, new BlockPos((Entity)entityout), 0);
            return true;
        }
        return super.processInteract(player, hand);
    }

    public EntityPFeral getFeral(World in) {
        return null;
    }

    @Override
    public int canSpawnByIDData() {
        return 0;
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        if (!SRPConfigSystems.disloDeathHighVerions) {
            return false;
        }
        int goo = SRPSaveData.get(this.worldObj, 30).getCurrentCode(this.worldObj.provider.dimensionId, 10);
        if (goo != 0 && this.worldObj.rand.nextDouble() < SRPConfigSystems.disloDeathHighVerionsChance) {
            EntityParasiteBase halo = ParasiteEventEntity.getRandomPrimitive(this.worldObj);
            if (goo >= SRPConfigSystems.disloDeathHighVerionsValue1) {
                halo = ParasiteEventEntity.getRandomAdapted(this.worldObj);
            }
            if (goo >= SRPConfigSystems.disloDeathHighVerionsValue2) {
                halo = ParasiteEventEntity.getRandomPure(this.worldObj);
            }
            halo.copyLocationAndAnglesFrom((Entity)this);
            halo.onInitialSpawn(DifficultyAccess.at(this.worldObj, EntityQueries.position(this)), null);
            this.worldObj.spawnEntityInWorld((Entity)halo);
            this.worldObj.playEvent(null, 1026, new BlockPos((Entity)halo), 0);
            halo.particleStatus((byte)7);
            halo.addPotionEffect(new PotionEffect(SRPPotions.EPEL_E, 600, 0, false, false));
        }
        return super.onDeathDislo(cause);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void spawnEffectsGore() {
        int i;
        for (i = 0; i <= 100; ++i) {
            if (i % 5 != 0) continue;
            this.spawnParticlesGore(SRPEnumParticle.GSPLASH, 0, -1, -1);
        }
        for (i = 0; i <= 20; ++i) {
            if (i % 5 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 0, -1, -1);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setString("parasitehost", this.host);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("parasitehost", 8)) {
            this.setHost(compound.getString("parasitehost").toString());
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 14) {
            this.check = 2;
        } else if (id == 15) {
            this.check = 1;
        } else if (id == 16) {
            ++this.check;
        } else if (id == 17) {
            this.check = 0;
        } else {
            super.handleHealthUpdate(id);
        }
    }
}

