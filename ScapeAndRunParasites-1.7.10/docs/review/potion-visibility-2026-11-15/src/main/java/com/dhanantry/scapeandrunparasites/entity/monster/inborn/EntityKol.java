/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockColonyStructure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanColony;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityKol
extends EntityParasiteBase
implements EntityCanColony {
    EntityAIFindingSpotBlock build;

    public EntityKol(World worldIn) {
        super(worldIn);
        this.setSize(0.65f, 0.65f);
        this.experienceValue = SRPAttributes.XP_LiTTLE;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.type = (byte)7;
        this.killcount = -10.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 36;
    }

    public EntityKol(World worldIn, BlockPos origin, int distanceBuilding) {
        this(worldIn);
        this.setTask(origin, distanceBuilding);
    }

    public void setTask(BlockPos origin, int distanceBuilding) {
        this.build = new EntityAIFindingSpotBlock(this, distanceBuilding);
        this.tasks.addTask(3, (EntityAIBase)this.build);
        this.setOrigin(origin);
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.LODO_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.LODO_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.LODO_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.LODO_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && this.srpTicks == 10 && this.rand.nextInt(7) == 0) {
            if (!SRPConfigWorld.coloniesActivated) {
                return;
            }
            SRPWorldData data = SRPWorldData.get(this.worldObj);
            if (data == null) {
                return;
            }
            BlockPos origin = data.nearestColonyPosition(EntityQueries.position(this), false);
            if (origin != null) {
                this.setTask(origin, data.getColonyDistanceSpreadByPosition(origin, false));
            }
        }
    }

    public void fall(float distance, float damageMultiplier) {
        if (distance > 50.0f) {
            super.fall(distance, damageMultiplier);
        }
    }

    public float getEyeHeight() {
        return 0.5f;
    }

    public void setOrigin(BlockPos pos) {
        this.build.setOrigin(pos.getX(), pos.getY(), pos.getZ());
    }

    public void setOrigin(int x, int y, int z) {
        this.build.setOrigin(x, y, z);
    }

    @Override
    public boolean getCanSpawnHere() {
        IBlockState iblockstate = BlockStates.get(this.worldObj, new BlockPos((Entity)this).down());
        return iblockstate.canEntitySpawn((Entity)this) && DifficultyAccess.difficulty(this.worldObj) != EnumDifficulty.PEACEFUL && this.isValidLightLevelTwo() && SRPConfig.spawnDays <= (int)this.worldObj.getTotalWorldTime();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (this.build != null) {
            compound.setInteger("parasiteoriginx", this.build.getOrigin(1));
            compound.setInteger("parasiteoriginy", this.build.getOrigin(2));
            compound.setInteger("parasiteoriginz", this.build.getOrigin(3));
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        int x = 0;
        int y = 0;
        int z = 0;
        if (compound.hasKey("parasiteoriginx", 99)) {
            x = compound.getInteger("parasiteoriginx");
        }
        if (compound.hasKey("parasiteoriginy", 99)) {
            y = compound.getInteger("parasiteoriginy");
        }
        if (compound.hasKey("parasiteoriginz", 99)) {
            z = compound.getInteger("parasiteoriginz");
        }
        this.build = new EntityAIFindingSpotBlock(this, 10);
        this.tasks.addTask(3, (EntityAIBase)this.build);
        this.setOrigin(x, y, z);
    }

    @Override
    public boolean onlySpawnInside() {
        return true;
    }

    public class EntityAIFindingSpotBlock
    extends EntityAIBase {
        protected final EntityParasiteBase entity;
        private int building;
        private int defence;
        private int tiick;
        private int originX;
        private int originY;
        private int originZ;
        private int maxDistance;

        public EntityAIFindingSpotBlock(EntityKol creatureIn, int maxDistance) {
            this.entity = creatureIn;
            this.building = 26;
            this.defence = this.building / 2;
            this.tiick = 0;
            this.maxDistance = maxDistance * maxDistance;
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            ++this.tiick;
            return this.tiick >= 200 && this.entity.getParasiteStatus() == 0;
        }

        public boolean continueExecuting() {
            return this.entity.getNavigator().noPath() && this.entity.getParasiteStatus() == 0 && this.tiick != 0;
        }

        public void resetTask() {
            this.tiick = 0;
        }

        public void updateTask() {
            BlockPos flag = EntityQueries.position(this.entity);
            int range = 25;
            for (int x = flag.getX() - range; x <= flag.getX() + range; ++x) {
                for (int z = flag.getZ() - range; z <= flag.getZ() + range; ++z) {
                    BlockPos spot;
                    if (this.checkPosition(x, z) == this.defence) {
                        spot = this.checkBlock(EntityKol.this.worldObj, new BlockPos(x, flag.getY(), z));
                        if (spot == null) continue;
                        BlockStates.set(this.entity.worldObj, spot.down(), BlockStates.defaultState(SRPBlocks.ParasiteStructure).withProperty((IProperty)BlockColonyStructure.ACTIVE, (Comparable)Integer.valueOf(2)));
                        this.resetTask();
                        return;
                    }
                    if (this.checkPosition(x, z) != this.building || (spot = this.checkBlock(EntityKol.this.worldObj, new BlockPos(x, flag.getY(), z))) == null) continue;
                    BlockStates.set(this.entity.worldObj, spot.down(), BlockStates.defaultState(SRPBlocks.ParasiteStructure).withProperty((IProperty)BlockColonyStructure.ACTIVE, (Comparable)Integer.valueOf(1)));
                    this.resetTask();
                    return;
                }
            }
            this.resetTask();
        }

        private BlockPos checkBlock(World world, BlockPos pos) {
            if (pos.getY() <= 2) {
                return null;
            }
            if (this.getDistanceSqFromOrigin(pos)) {
                return null;
            }
            if ((pos = ParasiteEventEntity.getFloor(world, pos, 5)) != null) {
                if (this.checkArea(world, pos)) {
                    return null;
                }
                return pos;
            }
            return null;
        }

        private boolean checkArea(World world, BlockPos pos) {
            int a = 30;
            int yy = pos.getY();
            for (int i = yy - a; i <= yy + a; ++i) {
                Block block = BlockStates.get(world, new BlockPos(pos.getX(), i, pos.getZ())).getBlock();
                if (block != SRPBlocks.ParasiteStructure && block != SRPBlocks.ColonyHeart && block != SRPBlocks.BiomeHeart) continue;
                return true;
            }
            return false;
        }

        private int checkPosition(int posX, int posZ) {
            if (posX % this.defence == 0 && posX % this.building != 0 && posZ % this.defence == 0 && posZ % this.building != 0) {
                return this.defence;
            }
            if (posX % this.building == 0 && posZ % this.building == 0) {
                return this.building;
            }
            return 0;
        }

        private boolean getDistanceSqFromOrigin(BlockPos pos) {
            double d2;
            double d1;
            double d0 = this.originX - pos.getX();
            return d0 * d0 + (d1 = (double)(this.originY - pos.getY())) * d1 + (d2 = (double)(this.originZ - pos.getZ())) * d2 > (double)this.maxDistance;
        }

        public void setOrigin(int x, int y, int z) {
            this.originX = x;
            this.originY = y;
            this.originZ = z;
        }

        public int getOrigin(int in) {
            switch (in) {
                case 1: {
                    return this.originX;
                }
                case 2: {
                    return this.originY;
                }
                case 3: {
                    return this.originZ;
                }
            }
            return 0;
        }
    }
}

