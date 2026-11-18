/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedStain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteFog;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityPStationaryArchitect
extends EntityPStationary
implements EntityCanSummon {
    protected int totalP;
    protected int actualP;
    protected boolean convert = true;
    protected int[] mobID;
    protected int[] mobPT;
    protected byte stage;
    protected float body;
    public int neededTime;
    protected int actualTime;
    protected int topParticles;
    private boolean canGrowTo;
    private int bombC;

    public EntityPStationaryArchitect(World worldIn) {
        super(worldIn);
        this.killcount = -10.0;
        this.canD = SRPConfig.rsDespawn;
        this.canGrowTo = true;
        this.borderOrb = -1;
    }

    public void setCanGrowTo(boolean in) {
        this.canGrowTo = in;
    }

    public void setConvert(boolean in) {
        this.convert = in;
    }

    public int setGT(int minGrowTime, int maxGrowTime) {
        int atm = maxGrowTime - minGrowTime + 1;
        if (atm <= 0 || maxGrowTime <= minGrowTime) {
            return 1;
        }
        return this.rand.nextInt(atm) + minGrowTime;
    }

    public void setActualT(int in) {
        if (this.canGrowTo) {
            this.actualTime = in;
        }
    }

    public int getActualT() {
        return this.actualTime;
    }

    public int getNeededTime() {
        return this.neededTime;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.topParticles > 0) {
            --this.topParticles;
        }
        if (this.bombC > 0) {
            --this.bombC;
        }
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (hand != EnumHand.MAIN_HAND || this.worldObj.isRemote) {
            return super.processInteract(player, hand);
        }
        Item wea = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem();
        if (wea == SRPItems.itemEvolve) {
            this.canChangeVariant = true;
            this.setActualT(100000000);
            this.setActualT(100000000);
            return true;
        }
        if (wea == SRPItems.itembase) {
            this.generateStructure();
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!this.worldObj.isRemote) {
            int meta;
            BlockPos floor;
            Block bbb;
            int i1 = EntityQueries.position(this).getY();
            double l1 = EntityQueries.position(this).getX();
            double i2 = EntityQueries.position(this).getZ();
            int BGrange = 2;
            for (int k2 = -1 * BGrange; k2 <= 1 * BGrange; ++k2) {
                for (int l2 = -1 * BGrange; l2 <= 1 * BGrange; ++l2) {
                    for (int j = -1 * BGrange; j <= 1 * BGrange; ++j) {
                        double i3 = l1 + (double)k2;
                        double k = i1 + j;
                        double l = i2 + (double)l2;
                        BlockPos blockpos = new BlockPos(i3, k, l);
                        IBlockState iblockstate = BlockStates.get(this.worldObj, blockpos);
                        Block block = iblockstate.getBlock();
                        if (block != SRPBlocks.ParasiteFog) continue;
                        BlockStates.set(this.worldObj, blockpos, BlockStates.defaultState(SRPBlocks.ParasiteFog).withProperty((IProperty)BlockParasiteFog.STAGE, (Comparable)Integer.valueOf(2)));
                    }
                }
            }
            if (SRPConfigWorld.originActivated) {
                return;
            }
            if ((this.convert || ParasiteEventEntity.getRSchance(this.worldObj) == 0.0 || !SRPConfigSystems.useEvolution && (double)SRPConfigSystems.rschance == 0.0) && ((bbb = BlockStates.get(this.worldObj, floor = new BlockPos((Entity)this).down()).getBlock()) == SRPBlocks.InfestedStain || bbb == SRPBlocks.InfestedRubble) && ((meta = BlockStates.meta(bbb, BlockStates.get(this.worldObj, floor))) <= SRPConfigSystems.rsBlockRevertStage && meta <= this.stage || ParasiteEventEntity.getRSchance(this.worldObj) == 0.0)) {
                BlockStates.set(this.worldObj, floor, BlockStates.defaultState(SRPBlocks.InfestedStain).withProperty((IProperty)BlockInfestedStain.STAGE, (Comparable)Integer.valueOf(5)));
                this.worldObj.updateBlockTick(floor, BlockStates.defaultState(SRPBlocks.InfestedStain).withProperty((IProperty)BlockInfestedStain.STAGE, (Comparable)Integer.valueOf(5)).getBlock(), 40, 5);
            }
        }
    }

    public float getBODY() {
        return this.body;
    }

    public byte getStageV() {
        return this.stage;
    }

    @Override
    public void addID(int id, int points) {
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] != -777) continue;
            this.mobID[i] = id;
            this.mobPT[i] = points;
            return;
        }
    }

    @Override
    public int IDable() {
        int flag = 0;
        for (int j : this.mobID) {
            if (j != -777) continue;
            ++flag;
        }
        if (flag > this.totalP) {
            flag = this.totalP;
        }
        return flag;
    }

    @Override
    public void checkID() {
        for (int i = 0; i < this.mobID.length; ++i) {
            Entity flag;
            if (this.mobID[i] <= 0 || (flag = this.worldObj.getEntityByID(this.mobID[i])) != null) continue;
            this.mobID[i] = -777;
            int negative = this.mobPT[i] * -1;
            this.setActualParasites(negative);
        }
    }

    @Override
    public int[] getIDList() {
        return this.mobID;
    }

    @Override
    public int[] getPointList() {
        return this.mobPT;
    }

    @Override
    public int getTotalParasites() {
        return this.totalP;
    }

    @Override
    public int getActualParasites() {
        return this.actualP;
    }

    @Override
    public void setActualParasites(int i) {
        this.actualP += i;
    }

    public abstract float getBombDamage();

    public abstract void generateStructure();

    @Override
    public boolean canDespawn() {
        if (this.worldObj.getBiome(EntityQueries.position(this)) instanceof BiomeParasiteBase) {
            return true;
        }
        return super.canDespawn();
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if (source == DamageSource.magic && this.isPotionActive(net.minecraft.potion.Potion.poison) && amount == 1.0f) {
            this.heal(1.0f * this.genePoisonHealing);
            return false;
        }
        float red = this.getParasiteStatus() == 0 ? 0.4f : 1.0f;
        boolean flag = super.attackEntityFrom(source, amount * red);
        if (flag && this.bombC == 0 && source != DamageSource.outOfWorld) {
            if (source.getEntity() instanceof EntityLivingBase) {
                if (source.getEntity().getDistanceSqToEntity((Entity)this) > 36.0) {
                    return flag;
                }
            } else {
                return flag;
            }
            for (int i = 0; i < this.stage * 2; ++i) {
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
                double d7 = 0.5 / (d6 / 4.0 + 0.1);
                d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.65f));
                d4 = d4 * d7 * (double)this.stage * 3.0;
                d5 *= d7;
                EntityBomb bomb = new EntityBomb(this.worldObj, this, false);
                bomb.setFuse(60);
                bomb.setStren(0.0f);
                bomb.setSkin(1);
                bomb.setDamage(this.getBombDamage(), this.stage);
                bomb.rotationPitch -= -20.0f;
                bomb.copyLocationAndAnglesFrom((Entity)this);
                bomb.setMotion(d3, d4, d5, 0.35, (float)this.stage / 10.0f * 2.0f);
                this.worldObj.spawnEntityInWorld((Entity)bomb);
                bomb.updateSTR();
            }
            this.bombC = 80;
        }
        return flag;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("tickse", this.actualTime);
        compound.setInteger("neededtime", this.neededTime);
        compound.setBoolean("cangrowto", this.canGrowTo);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("tickse", 99)) {
            this.actualTime = compound.getInteger("tickse");
        }
        if (compound.hasKey("neededtime", 99)) {
            this.neededTime = compound.getInteger("neededtime");
        }
        if (compound.hasKey("cangrowto", 99)) {
            this.canGrowTo = compound.getBoolean("cangrowto");
        }
    }

    @Override
    public void handleHealthUpdate(byte id) {
        if (id == 12) {
            this.topParticles = 20;
        } else {
            super.handleHealthUpdate(id);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticlesTop(SRPEnumParticle particleType, int r, int g, int b) {
        double d0 = this.rand.nextGaussian() * 0.02;
        double d1 = this.rand.nextGaussian() * 0.02;
        double d2 = this.rand.nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + (double)this.height + (double)this.rand.nextFloat() * ((double)(this.height - this.getEyeHeight()) + 0.5), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d0, d1, d2, r, g, b);
    }
}

