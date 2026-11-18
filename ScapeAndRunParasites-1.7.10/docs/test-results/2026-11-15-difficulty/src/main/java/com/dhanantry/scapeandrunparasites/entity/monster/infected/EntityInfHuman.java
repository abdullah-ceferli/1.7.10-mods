/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.BlockDoor$EnumDoorHalf
 *  net.minecraft.block.BlockFenceGate
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityAIOpenDoor
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.monster.AbstractSkeleton
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathNavigateGround
 *  net.minecraft.pathfinding.PathNodeType
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.pathfinding.WalkNodeProcessor
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityAICircleGroup;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfHumanHead;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3i;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityInfHuman
extends EntityPInfected
implements EntityCanMelt {
    private static final DataParameter<Float> HEIGH = EntityDataManager.createKey(EntityInfHuman.class, (DataSerializer)DataSerializers.FLOAT);
    private static final DataParameter<Boolean> MELTING = EntityDataManager.createKey(EntityInfHuman.class, (DataSerializer)DataSerializers.BOOLEAN);
    private float aSize;
    private int sound;
    private int host;
    private BlockPos barrierGoal = null;
    private int barrierRetryTicks = 0;
    private double _lastX = 0.0;
    private double _lastZ = 0.0;
    private int _stuckTicks = 0;
    private int _shoveTicks = 0;
    private BlockPos lastHeardSoundPos;
    private int soundMemoryTicks;
    private static final UUID SHOVE_SPEED_UUID = UUID.fromString("8f5b3f68-7aa5-4c2c-8d9e-2e4a5f5583c3");
    private static final AttributeModifier SHOVE_SPEED_MOD = new AttributeModifier(SHOVE_SPEED_UUID, "gate_shove_boost", 0.6, 2);

    public EntityInfHuman(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 1.95f);
        this.aSize = 1.0f;
        this.sound = 0;
        this.canModRender = 1;
        this.type = (byte)11;
        this.thisMelting = true;
        if (this.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround nav = (PathNavigateGround)this.getNavigator();
            nav.setEnterDoors(true);
            if (nav.getNodeProcessor() instanceof WalkNodeProcessor) {
                WalkNodeProcessor proc = (WalkNodeProcessor)nav.getNodeProcessor();
                proc.setCanEnterDoors(true);
            }
        }
        this.setPathPriority(PathNodeType.DOOR_WOOD_CLOSED, 0.0f);
        this.setPathPriority(PathNodeType.DOOR_OPEN, 0.0f);
        this.setPathPriority(PathNodeType.DOOR_IRON_CLOSED, -1.0f);
    }

    @Override
    public int getParasiteIDRegister() {
        return 6;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infhumanCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.08));
        this.tasks.addTask(1, (EntityAIBase)new EntityAIOpenDoor((EntityLiving)this, true));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.tasks.addTask(4, (EntityAIBase)new EntityAICircleGroup((EntityCreature)this, 1.15, 8, 4.0, 10.0, 16, (Predicate<? super Entity>)((Predicate)e -> e instanceof EntityInfHuman)));
        this.tasks.addTask(5, (EntityAIBase)new EntityAIGetFollowers(this, 1, 16));
        this.tasks.addTask(6, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.INFHUMAN_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.INFHUMAN_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.230000004172325);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.INFHUMAN_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.INFHUMAN_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.infectedFollow);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(HEIGH, (Object)Float.valueOf(0.0f));
        EntityDataManager.of(this).register(MELTING, (Object)Boolean.FALSE);
    }

    @Override
    public void onLivingUpdate() {
        double dz;
        double dx;
        super.onLivingUpdate();
        this.melting();
        if (this.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround)this.getNavigator()).setEnterDoors(true);
        }
        if (this.getSkin() == 111) {
            this.tickSoundMemory();
            if (this.getHeardSoundPos() == null) {
                AxisAlignedBB scanBox = Boxes.grow(Boxes.entityBox(this), 16.0, 4.0, 16.0);
                List nearby = EntityQueries.entities(this.worldObj, EntityPlayer.class, scanBox, p -> !p.isSpectator() && !p.capabilities.isCreativeMode && p.isEntityAlive());
                EntityPlayer loudest = null;
                double loudestSpeedSq = 0.0;
                for (EntityPlayer p2 : nearby) {
                    boolean moving;
                    float walkedThisTick = p2.distanceWalkedModified - p2.prevDistanceWalkedModified;
                    boolean bl = moving = walkedThisTick > 0.01f || p2.isSprinting();
                    if (!moving) continue;
                    double loudness = walkedThisTick;
                    if (p2.isSprinting()) {
                        loudness *= 3.0;
                    }
                    if (!(loudness > loudestSpeedSq)) continue;
                    loudestSpeedSq = loudness;
                    loudest = p2;
                }
                if (loudest != null) {
                    BlockPos soundPos = new BlockPos(loudest.posX, loudest.posY, loudest.posZ);
                    this.notifyHeardSound(soundPos, 60);
                    this.setAttackTarget((EntityLivingBase)loudest);
                }
            }
            if (this.getHeardSoundPos() == null && this.getAITarget() == null && this.getAttackTarget() != null) {
                this.setAttackTarget(null);
            }
        } else {
            this.clearHeardSound();
        }
        this._stuckTicks = (dx = this.posX - this._lastX) * dx + (dz = this.posZ - this._lastZ) * dz < 0.0025 ? ++this._stuckTicks : 0;
        this._lastX = this.posX;
        this._lastZ = this.posZ;
        if (this.getAttackTarget() != null) {
            double d2;
            if (this.getNavigator().noPath() || this._stuckTicks > 20) {
                if (this.barrierRetryTicks <= 0 && this.retargetToNearestEntrance(12)) {
                    this.barrierRetryTicks = 40;
                }
            } else if (this.barrierRetryTicks > 0) {
                --this.barrierRetryTicks;
            }
            if (this.barrierGoal != null && (d2 = this.getDistanceSq(this.barrierGoal)) <= 4.0) {
                IBlockState st = BlockStates.get(this.worldObj, this.barrierGoal);
                this.openBarrierAt(this.barrierGoal, st);
                this.barrierGoal = null;
                this.getNavigator().clearPathEntity();
                this.getNavigator().tryMoveToEntityLiving((Entity)this.getAttackTarget(), 1.5);
            }
            if (!this.getNavigator().noPath()) {
                this.tryOpenNextPathBarrier();
            }
            this.tryOpenNearbyBarriers();
            if (this._shoveTicks == 0 && this._stuckTicks >= 15 && this.nearOpenBarrier(1.5)) {
                this._shoveTicks = 8;
                this._stuckTicks = 0;
            }
            if (this._shoveTicks > 0) {
                IAttributeInstance speedAttr = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                if (!speedAttr.hasModifier(SHOVE_SPEED_MOD)) {
                    speedAttr.applyModifier(SHOVE_SPEED_MOD);
                }
                Vec3d look = this.getLookVec();
                this.addVelocity(look.xCoord * 0.15, 0.0, look.zCoord * 0.15);
                if (this.onGround && this._shoveTicks == 8) {
                    this.motionY += 0.05;
                }
                List crowd = EntityQueries.entities(this.worldObj, EntityLivingBase.class, Boxes.grow(Boxes.entityBox(this), 0.6, 0.2, 0.6), e -> e != this);
                for (EntityLivingBase e2 : crowd) {
                    double ex = e2.posX - this.posX;
                    double ez = e2.posZ - this.posZ;
                    double dSq = ex * ex + ez * ez + 1.0E-4;
                    e2.addVelocity(ex / dSq * 0.08, 0.0, ez / dSq * 0.08);
                }
                --this._shoveTicks;
                if (this._shoveTicks == 0 && speedAttr.hasModifier(SHOVE_SPEED_MOD)) {
                    speedAttr.removeModifier(SHOVE_SPEED_MOD);
                }
            }
        }
    }

    @Override
    public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
        EntityPlayer player;
        if (entitylivingbaseIn instanceof EntityPlayer && ((player = (EntityPlayer)entitylivingbaseIn).isSpectator() || player.capabilities.isCreativeMode)) {
            super.setAttackTarget(null);
            return;
        }
        super.setAttackTarget(entitylivingbaseIn);
    }

    private boolean nearOpenBarrier(double radius) {
        BlockPos base = new BlockPos((Entity)this);
        int r = (int)Math.ceil(radius);
        for (int y = 0; y <= 1; ++y) {
            for (int dx = -r; dx <= r; ++dx) {
                for (int dz = -r; dz <= r; ++dz) {
                    BlockDoor d;
                    BlockPos p = base.add(dx, y, dz);
                    IBlockState st = BlockStates.get(this.worldObj, p);
                    if (!(st.getBlock() instanceof BlockDoor ? (d = (BlockDoor)st.getBlock()).getMaterial(st) == Material.wood && (st.getValue((IProperty)BlockDoor.HALF) != BlockDoor.EnumDoorHalf.UPPER || (st = BlockStates.get(this.worldObj, p = p.down())).getBlock() instanceof BlockDoor) && (Boolean)st.getValue((IProperty)BlockDoor.OPEN) != false : st.getBlock() instanceof BlockFenceGate && st.getMaterial() == Material.wood && (Boolean)st.getValue((IProperty)BlockFenceGate.OPEN) != false)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean retargetToNearestEntrance(int radius) {
        BlockPos me = new BlockPos((Entity)this);
        BlockPos bestBarrier = null;
        double bestDist2 = Double.MAX_VALUE;
        for (int dy = 0; dy <= 1; ++dy) {
            for (int dx = -radius; dx <= radius; ++dx) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    double d2;
                    BlockPos p = me.add(dx, dy, dz);
                    IBlockState st = BlockStates.get(this.worldObj, p);
                    if (!EntityInfHuman.isWoodDoor(st) && !EntityInfHuman.isWoodGate(st)) continue;
                    if (EntityInfHuman.isWoodDoor(st)) {
                        p = this.normalizeDoorPos(p, st);
                    }
                    if (!((d2 = p.distanceSq((Vec3i)me)) < bestDist2)) continue;
                    bestDist2 = d2;
                    bestBarrier = p;
                }
            }
        }
        if (bestBarrier == null) {
            return false;
        }
        List<BlockPos> around = Arrays.asList(bestBarrier.north(), bestBarrier.south(), bestBarrier.east(), bestBarrier.west());
        around.sort(Comparator.comparingDouble(pos -> pos.distanceSq(this.posX, this.posY, this.posZ)));
        for (BlockPos ap : around) {
            boolean ok;
            if (!BlockStates.get(this.worldObj, ap).getMaterial().isReplaceable() || !(ok = this.getNavigator().tryMoveToXYZ((double)ap.getX() + 0.5, (double)ap.getY() + 0.0, (double)ap.getZ() + 0.5, 1.5))) continue;
            this.barrierGoal = bestBarrier;
            return true;
        }
        return false;
    }

    private static boolean isWoodDoor(IBlockState s) {
        if (!(s.getBlock() instanceof BlockDoor)) {
            return false;
        }
        BlockDoor d = (BlockDoor)s.getBlock();
        return d.getMaterial(s) == Material.wood;
    }

    private static boolean isWoodGate(IBlockState s) {
        if (!(s.getBlock() instanceof BlockFenceGate)) {
            return false;
        }
        return s.getMaterial() == Material.wood;
    }

    private BlockPos normalizeDoorPos(BlockPos pos, IBlockState state) {
        if (state.getBlock() instanceof BlockDoor && state.getValue((IProperty)BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            return pos.down();
        }
        return pos;
    }

    private void openBarrierAt(BlockPos pos, IBlockState state) {
        if (EntityInfHuman.isWoodDoor(state)) {
            if (((Boolean)(state = BlockStates.get(this.worldObj, pos = this.normalizeDoorPos(pos, state))).getValue((IProperty)BlockDoor.OPEN)).booleanValue()) {
                return;
            }
            ((BlockDoor)state.getBlock()).toggleDoor(this.worldObj, pos, true);
            return;
        }
        if (EntityInfHuman.isWoodGate(state)) {
            if (((Boolean)state.getValue((IProperty)BlockFenceGate.OPEN)).booleanValue()) {
                return;
            }
            BlockStates.set(this.worldObj, pos, state.withProperty((IProperty)BlockFenceGate.OPEN, (Comparable)Boolean.valueOf(true)), 10);
        }
    }

    private void tryOpenNextPathBarrier() {
        Path path = this.getNavigator().getPath();
        if (path == null) {
            return;
        }
        int i = path.getCurrentPathIndex();
        if (i >= path.getCurrentPathLength()) {
            return;
        }
        PathPoint next = path.getPathPointFromIndex(i);
        if (next == null) {
            return;
        }
        BlockPos pos = new BlockPos(next.xCoord, next.yCoord, next.zCoord);
        this.openBarrierAt(pos, BlockStates.get(this.worldObj, pos));
    }

    private void tryOpenNearbyBarriers() {
        BlockPos[] spots;
        BlockPos base = new BlockPos((Entity)this);
        EnumFacing face = this.getHorizontalFacing();
        for (BlockPos p : spots = new BlockPos[]{base, base.up(), base.offset(face), base.offset(face).up(), base.offset(face.rotateY()), base.offset(face.rotateY()).up(), base.offset(face.rotateYCCW()), base.offset(face.rotateYCCW()).up()}) {
            IBlockState st = BlockStates.get(this.worldObj, p);
            if (!EntityInfHuman.isWoodDoor(st) && !EntityInfHuman.isWoodGate(st)) continue;
            this.openBarrierAt(p, st);
        }
    }

    @Override
    protected void onDeathUpdate() {
        super.onDeathUpdate();
        if (this.getTHeigh() < 1.57f && !this.worldObj.isRemote) {
            this.setTHeigh(0.13f);
        }
        if (this.deathTime == 20 && !this.worldObj.isRemote && this.rand.nextDouble() <= SRPAttributes.INFHUMAN_HEADCHANCE) {
            EntityInfHumanHead out = new EntityInfHumanHead(this.worldObj);
            out.copyLocationAndAnglesFrom((Entity)this);
            out.onInitialSpawn(DifficultyAccess.at(this.worldObj, EntityQueries.position(this)), null);
            out.setSkin(this.getSkin());
            out.cannotDespawn(false);
            this.worldObj.spawnEntityInWorld((Entity)out);
        }
    }

    public void notifyHeardSound(BlockPos pos, int lifeTicks) {
        if (this.getSkin() != 111) {
            return;
        }
        this.lastHeardSoundPos = pos;
        this.soundMemoryTicks = lifeTicks;
    }

    public BlockPos getHeardSoundPos() {
        return this.lastHeardSoundPos;
    }

    public void tickSoundMemory() {
        if (this.soundMemoryTicks > 0) {
            --this.soundMemoryTicks;
            if (this.soundMemoryTicks <= 0) {
                this.lastHeardSoundPos = null;
            }
        }
    }

    public void clearHeardSound() {
        this.lastHeardSoundPos = null;
        this.soundMemoryTicks = 0;
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        if (!this.worldObj.isRemote) {
            if (SRPConfigMobs.hostEnabled && entityLivingIn instanceof AbstractSkeleton) {
                ++this.host;
                if (this.host >= SRPConfigMobs.hostSkele) {
                    this.particleStatus((byte)7);
                    EntityHost host = new EntityHost(this.worldObj);
                    host.copyLocationAndAnglesFrom((Entity)this);
                    host.onInitialSpawn(DifficultyAccess.at(this.worldObj, EntityQueries.position(this)), null);
                    this.worldObj.spawnEntityInWorld((Entity)host);
                    host.particleStatus((byte)7);
                    this.setDead();
                }
            } else {
                super.onKillEntity(entityLivingIn);
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag && this.rand.nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof EntityLivingBase) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 100, 0);
        }
        return flag;
    }

    @Override
    public void melt() {
        this.setWait(1000);
        EntityDataManager.of(this).set(HEIGH, (Object)Float.valueOf(1.95f));
        EntityDataManager.of(this).set(MELTING, (Object)Boolean.TRUE);
    }

    @Override
    public void melting() {
        if (this.isMelting()) {
            if (this.sound % 20 == 0) {
                this.playSound(SRPSounds.INFECTED_MELT, 1.0f, 1.0f);
            }
            ++this.sound;
            if ((double)this.getTHeigh() > 0.7) {
                this.setaSize(-0.005f);
                this.setTHeigh(-0.01f);
                this.setSize(this.width, this.getTHeigh());
            }
            if (!this.worldObj.isRemote) {
                if ((double)this.getTHeigh() <= 0.7 || this.sound >= 127) {
                    EntityLesh out = new EntityLesh(this.worldObj);
                    out.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                    if (this.getCustomNameTag() != null) {
                        out.setMinecartName(this.getCustomNameTag());
                    }
                    this.setDead();
                    this.worldObj.spawnEntityInWorld((Entity)out);
                    out.setLegs(SRPAttributes.INFHUMAN_V, false);
                }
            } else {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
        }
    }

    public boolean canEntityBeSeen(Entity entityIn) {
        if (this.getSkin() == 111) {
            double dSq = this.getDistanceSqToEntity(entityIn);
            return dSq <= 4.0;
        }
        return super.canEntityBeSeen(entityIn);
    }

    @Override
    public boolean isMelting() {
        return (Boolean)EntityDataManager.of(this).get(MELTING);
    }

    @Override
    public float getTHeigh() {
        return ((Float)EntityDataManager.of(this).get(HEIGH)).floatValue();
    }

    @Override
    public void setTHeigh(float in) {
        EntityDataManager.of(this).set(HEIGH, (Object)Float.valueOf(in += this.getTHeigh()));
    }

    @Override
    public float getaSize() {
        return this.aSize;
    }

    @Override
    public void setaSize(float in) {
        this.aSize += in;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public float getSelfeFlashIntensity2() {
        return this.aSize;
    }

    public float getEyeHeight() {
        return 1.73f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.INFECTEDHUMAN_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDHUMAN_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.INFECTEDHUMAN_DEATH;
    }

    protected String getStepSound() {
        return SoundEvents.ENTITY_ZOMBIE_STEP;
    }

    @Override
    public EntityPFeral getFeral(World in) {
        return new EntityFerHuman(in);
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("parasitehost", this.host);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("parasitehost", 99)) {
            this.host = compound.getInteger("parasitehost");
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            this.setSkin(this.worldObj.rand.nextInt(3) + 1);
            if (this.rand.nextDouble() < (double)0.01f) {
                IAttributeInstance moveAttr;
                this.setSkin(111);
                if (this.getEntityAttribute(SharedMonsterAttributes.followRange) != null) {
                    this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(12.0);
                }
                if ((moveAttr = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed)) != null) {
                    moveAttr.setBaseValue(0.32);
                }
            }
        } else {
            this.setSkin(0);
        }
        return data;
    }
}

