/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public abstract class EntityLiving
extends EntityLivingBase {
    private static final DataParameter<Byte> AI_FLAGS = EntityDataManager.createKey(EntityLiving.class, DataSerializers.BYTE);
    public int livingSoundTime;
    protected int experienceValue;
    private final EntityLookHelper lookHelper;
    protected EntityMoveHelper moveHelper;
    protected EntityJumpHelper jumpHelper;
    private final EntityBodyHelper bodyHelper;
    protected PathNavigate navigator;
    protected final EntityAITasks tasks;
    protected final EntityAITasks targetTasks;
    private EntityLivingBase attackTarget;
    private final EntitySenses senses;
    private final NonNullList<ItemStack> inventoryHands = NonNullList.withSize(2, ItemStack.EMPTY);
    protected float[] inventoryHandsDropChances = new float[2];
    private final NonNullList<ItemStack> inventoryArmor = NonNullList.withSize(4, ItemStack.EMPTY);
    protected float[] inventoryArmorDropChances = new float[4];
    private boolean canPickUpLoot;
    private boolean persistenceRequired;
    private final Map<PathNodeType, Float> mapPathPriority = Maps.newEnumMap(PathNodeType.class);
    private ResourceLocation deathLootTable;
    private long deathLootTableSeed;
    private boolean isLeashed;
    private Entity leashHolder;
    private NBTTagCompound leashNBTTag;

    public EntityLiving(World world) {
        super(world);
        this.tasks = new EntityAITasks(world == null || world.profiler == null ? null : world.profiler);
        this.targetTasks = new EntityAITasks(world == null || world.profiler == null ? null : world.profiler);
        this.lookHelper = new EntityLookHelper(this);
        this.moveHelper = new EntityMoveHelper(this);
        this.jumpHelper = new EntityJumpHelper(this);
        this.bodyHelper = this.createBodyHelper();
        this.navigator = this.createNavigator(world);
        this.senses = new EntitySenses(this);
        Arrays.fill(this.inventoryArmorDropChances, 0.085f);
        Arrays.fill(this.inventoryHandsDropChances, 0.085f);
        if (world != null && !world.isRemote) {
            this.initEntityAI();
        }
    }

    protected void initEntityAI() {
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0);
    }

    protected PathNavigate createNavigator(World world) {
        return new PathNavigateGround(this, world);
    }

    public float getPathPriority(PathNodeType pathNodeType) {
        Float f = this.mapPathPriority.get((Object)pathNodeType);
        return f == null ? pathNodeType.getPriority() : f.floatValue();
    }

    public void setPathPriority(PathNodeType pathNodeType, float f) {
        this.mapPathPriority.put(pathNodeType, Float.valueOf(f));
    }

    protected EntityBodyHelper createBodyHelper() {
        return new EntityBodyHelper(this);
    }

    public EntityLookHelper getLookHelper() {
        return this.lookHelper;
    }

    public EntityMoveHelper getMoveHelper() {
        return this.moveHelper;
    }

    public EntityJumpHelper getJumpHelper() {
        return this.jumpHelper;
    }

    public PathNavigate getNavigator() {
        return this.navigator;
    }

    public EntitySenses getEntitySenses() {
        return this.senses;
    }

    @Nullable
    public EntityLivingBase getAttackTarget() {
        return this.attackTarget;
    }

    public void setAttackTarget(@Nullable EntityLivingBase entityLivingBase) {
        this.attackTarget = entityLivingBase;
    }

    public boolean canAttackClass(Class<? extends EntityLivingBase> clazz) {
        return clazz != EntityGhast.class;
    }

    public void eatGrassBonus() {
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(AI_FLAGS, (byte)0);
    }

    public int getTalkInterval() {
        return 80;
    }

    public void playLivingSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        this.world.profiler.startSection("mobBaseTick");
        if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
            this.applyEntityAI();
            this.playLivingSound();
        }
        this.world.profiler.endSection();
    }

    @Override
    protected void playHurtSound(DamageSource damageSource) {
        this.applyEntityAI();
        super.playHurtSound(damageSource);
    }

    private void applyEntityAI() {
        this.livingSoundTime = -this.getTalkInterval();
    }

    @Override
    protected int getExperiencePoints(EntityPlayer entityPlayer) {
        if (this.experienceValue > 0) {
            int n;
            int n2 = this.experienceValue;
            for (n = 0; n < this.inventoryArmor.size(); ++n) {
                if (this.inventoryArmor.get(n).isEmpty() || !(this.inventoryArmorDropChances[n] <= 1.0f)) continue;
                n2 += 1 + this.rand.nextInt(3);
            }
            for (n = 0; n < this.inventoryHands.size(); ++n) {
                if (this.inventoryHands.get(n).isEmpty() || !(this.inventoryHandsDropChances[n] <= 1.0f)) continue;
                n2 += 1 + this.rand.nextInt(3);
            }
            return n2;
        }
        return this.experienceValue;
    }

    public void spawnExplosionParticle() {
        if (this.world.isRemote) {
            for (int i = 0; i < 20; ++i) {
                double d = this.rand.nextGaussian() * 0.02;
                double d2 = this.rand.nextGaussian() * 0.02;
                double d3 = this.rand.nextGaussian() * 0.02;
                double d4 = 10.0;
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width - d * 10.0, this.posY + (double)(this.rand.nextFloat() * this.height) - d2 * 10.0, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width - d3 * 10.0, d, d2, d3, new int[0]);
            }
        } else {
            this.world.setEntityState(this, (byte)20);
        }
    }

    @Override
    public void handleStatusUpdate(byte by) {
        if (by == 20) {
            this.spawnExplosionParticle();
        } else {
            super.handleStatusUpdate(by);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.world.isRemote) {
            this.updateLeashedState();
            if (this.ticksExisted % 5 == 0) {
                boolean bl = !(this.getControllingPassenger() instanceof EntityLiving);
                boolean bl2 = !(this.getRidingEntity() instanceof EntityBoat);
                this.tasks.setControlFlag(1, bl);
                this.tasks.setControlFlag(4, bl && bl2);
                this.tasks.setControlFlag(2, bl);
            }
        }
    }

    @Override
    protected float updateDistance(float f, float f2) {
        this.bodyHelper.updateRenderAngles();
        return f2;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    protected Item getDropItem() {
        return null;
    }

    @Override
    protected void dropFewItems(boolean bl, int n) {
        Item item = this.getDropItem();
        if (item != null) {
            int n2 = this.rand.nextInt(3);
            if (n > 0) {
                n2 += this.rand.nextInt(n + 1);
            }
            for (int i = 0; i < n2; ++i) {
                this.dropItem(item, 1);
            }
        }
    }

    public static void registerFixesMob(DataFixer dataFixer, Class<?> clazz) {
        dataFixer.registerWalker(FixTypes.ENTITY, new ItemStackDataLists(clazz, "ArmorItems", "HandItems"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nBTTagCompound) {
        super.writeEntityToNBT(nBTTagCompound);
        nBTTagCompound.setBoolean("CanPickUpLoot", this.canPickUpLoot());
        nBTTagCompound.setBoolean("PersistenceRequired", this.persistenceRequired);
        NBTTagList nBTTagList = new NBTTagList();
        for (ItemStack object22 : this.inventoryArmor) {
            NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
            if (!object22.isEmpty()) {
                object22.writeToNBT(nBTTagCompound2);
            }
            nBTTagList.appendTag(nBTTagCompound2);
        }
        nBTTagCompound.setTag("ArmorItems", nBTTagList);
        NBTTagList nBTTagList2 = new NBTTagList();
        for (ItemStack itemStack : this.inventoryHands) {
            NBTTagCompound nBTTagCompound3 = new NBTTagCompound();
            if (!itemStack.isEmpty()) {
                itemStack.writeToNBT(nBTTagCompound3);
            }
            nBTTagList2.appendTag(nBTTagCompound3);
        }
        nBTTagCompound.setTag("HandItems", nBTTagList2);
        NBTTagList nBTTagList3 = new NBTTagList();
        for (float f : this.inventoryArmorDropChances) {
            nBTTagList3.appendTag(new NBTTagFloat(f));
        }
        nBTTagCompound.setTag("ArmorDropChances", nBTTagList3);
        NBTTagList nBTTagList4 = new NBTTagList();
        for (float f : this.inventoryHandsDropChances) {
            nBTTagList4.appendTag(new NBTTagFloat(f));
        }
        nBTTagCompound.setTag("HandDropChances", nBTTagList4);
        nBTTagCompound.setBoolean("Leashed", this.isLeashed);
        if (this.leashHolder != null) {
            Object object = new NBTTagCompound();
            if (this.leashHolder instanceof EntityLivingBase) {
                UUID uUID = this.leashHolder.getUniqueID();
                ((NBTTagCompound)object).setUniqueId("UUID", uUID);
            } else if (this.leashHolder instanceof EntityHanging) {
                BlockPos blockPos = ((EntityHanging)this.leashHolder).getHangingPosition();
                ((NBTTagCompound)object).setInteger("X", blockPos.getX());
                ((NBTTagCompound)object).setInteger("Y", blockPos.getY());
                ((NBTTagCompound)object).setInteger("Z", blockPos.getZ());
            }
            nBTTagCompound.setTag("Leash", (NBTBase)object);
        }
        nBTTagCompound.setBoolean("LeftHanded", this.isLeftHanded());
        if (this.deathLootTable != null) {
            nBTTagCompound.setString("DeathLootTable", this.deathLootTable.toString());
            if (this.deathLootTableSeed != 0L) {
                nBTTagCompound.setLong("DeathLootTableSeed", this.deathLootTableSeed);
            }
        }
        if (this.isAIDisabled()) {
            nBTTagCompound.setBoolean("NoAI", this.isAIDisabled());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nBTTagCompound) {
        int n;
        NBTTagList nBTTagList;
        super.readEntityFromNBT(nBTTagCompound);
        if (nBTTagCompound.hasKey("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(nBTTagCompound.getBoolean("CanPickUpLoot"));
        }
        this.persistenceRequired = nBTTagCompound.getBoolean("PersistenceRequired");
        if (nBTTagCompound.hasKey("ArmorItems", 9)) {
            nBTTagList = nBTTagCompound.getTagList("ArmorItems", 10);
            for (n = 0; n < this.inventoryArmor.size(); ++n) {
                this.inventoryArmor.set(n, new ItemStack(nBTTagList.getCompoundTagAt(n)));
            }
        }
        if (nBTTagCompound.hasKey("HandItems", 9)) {
            nBTTagList = nBTTagCompound.getTagList("HandItems", 10);
            for (n = 0; n < this.inventoryHands.size(); ++n) {
                this.inventoryHands.set(n, new ItemStack(nBTTagList.getCompoundTagAt(n)));
            }
        }
        if (nBTTagCompound.hasKey("ArmorDropChances", 9)) {
            nBTTagList = nBTTagCompound.getTagList("ArmorDropChances", 5);
            for (n = 0; n < nBTTagList.tagCount(); ++n) {
                this.inventoryArmorDropChances[n] = nBTTagList.getFloatAt(n);
            }
        }
        if (nBTTagCompound.hasKey("HandDropChances", 9)) {
            nBTTagList = nBTTagCompound.getTagList("HandDropChances", 5);
            for (n = 0; n < nBTTagList.tagCount(); ++n) {
                this.inventoryHandsDropChances[n] = nBTTagList.getFloatAt(n);
            }
        }
        this.isLeashed = nBTTagCompound.getBoolean("Leashed");
        if (this.isLeashed && nBTTagCompound.hasKey("Leash", 10)) {
            this.leashNBTTag = nBTTagCompound.getCompoundTag("Leash");
        }
        this.setLeftHanded(nBTTagCompound.getBoolean("LeftHanded"));
        if (nBTTagCompound.hasKey("DeathLootTable", 8)) {
            this.deathLootTable = new ResourceLocation(nBTTagCompound.getString("DeathLootTable"));
            this.deathLootTableSeed = nBTTagCompound.getLong("DeathLootTableSeed");
        }
        this.setNoAI(nBTTagCompound.getBoolean("NoAI"));
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return null;
    }

    @Override
    protected void dropLoot(boolean bl, int n, DamageSource damageSource) {
        ResourceLocation resourceLocation = this.deathLootTable;
        if (resourceLocation == null) {
            resourceLocation = this.getLootTable();
        }
        if (resourceLocation != null) {
            LootTable lootTable = this.world.getLootTableManager().getLootTableFromLocation(resourceLocation);
            this.deathLootTable = null;
            LootContext.Builder builder = new LootContext.Builder((WorldServer)this.world).withLootedEntity(this).withDamageSource(damageSource);
            if (bl && this.attackingPlayer != null) {
                builder = builder.withPlayer(this.attackingPlayer).withLuck(this.attackingPlayer.getLuck());
            }
            List<ItemStack> list = lootTable.generateLootForPools(this.deathLootTableSeed == 0L ? this.rand : new Random(this.deathLootTableSeed), builder.build());
            for (ItemStack itemStack : list) {
                this.entityDropItem(itemStack, 0.0f);
            }
            this.dropEquipment(bl, n);
        } else {
            super.dropLoot(bl, n, damageSource);
        }
    }

    public void setMoveForward(float f) {
        this.moveForward = f;
    }

    public void setMoveVertical(float f) {
        this.moveVertical = f;
    }

    public void setMoveStrafing(float f) {
        this.moveStrafing = f;
    }

    @Override
    public void setAIMoveSpeed(float f) {
        super.setAIMoveSpeed(f);
        this.setMoveForward(f);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.world.profiler.startSection("looting");
        if (!this.world.isRemote && this.canPickUpLoot() && !this.dead && this.world.getGameRules().getBoolean("mobGriefing")) {
            List<EntityItem> list = this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().grow(1.0, 0.0, 1.0));
            for (EntityItem entityItem : list) {
                if (entityItem.isDead || entityItem.getItem().isEmpty() || entityItem.cannotPickup()) continue;
                this.updateEquipmentIfNeeded(entityItem);
            }
        }
        this.world.profiler.endSection();
    }

    protected void updateEquipmentIfNeeded(EntityItem entityItem) {
        ItemStack itemStack = entityItem.getItem();
        EntityEquipmentSlot entityEquipmentSlot = EntityLiving.getSlotForItemStack(itemStack);
        boolean bl = true;
        ItemStack itemStack2 = this.getItemStackFromSlot(entityEquipmentSlot);
        if (!itemStack2.isEmpty()) {
            Item item;
            if (entityEquipmentSlot.getSlotType() == EntityEquipmentSlot.Type.HAND) {
                if (itemStack.getItem() instanceof ItemSword && !(itemStack2.getItem() instanceof ItemSword)) {
                    bl = true;
                } else if (itemStack.getItem() instanceof ItemSword && itemStack2.getItem() instanceof ItemSword) {
                    item = (ItemSword)itemStack.getItem();
                    ItemSword itemSword = (ItemSword)itemStack2.getItem();
                    bl = ((ItemSword)item).getAttackDamage() == itemSword.getAttackDamage() ? itemStack.getMetadata() > itemStack2.getMetadata() || itemStack.hasTagCompound() && !itemStack2.hasTagCompound() : ((ItemSword)item).getAttackDamage() > itemSword.getAttackDamage();
                } else {
                    bl = itemStack.getItem() instanceof ItemBow && itemStack2.getItem() instanceof ItemBow ? itemStack.hasTagCompound() && !itemStack2.hasTagCompound() : false;
                }
            } else if (itemStack.getItem() instanceof ItemArmor && !(itemStack2.getItem() instanceof ItemArmor)) {
                bl = true;
            } else if (itemStack.getItem() instanceof ItemArmor && itemStack2.getItem() instanceof ItemArmor && !EnchantmentHelper.hasBindingCurse(itemStack2)) {
                item = (ItemArmor)itemStack.getItem();
                ItemArmor itemArmor = (ItemArmor)itemStack2.getItem();
                bl = ((ItemArmor)item).damageReduceAmount == itemArmor.damageReduceAmount ? itemStack.getMetadata() > itemStack2.getMetadata() || itemStack.hasTagCompound() && !itemStack2.hasTagCompound() : ((ItemArmor)item).damageReduceAmount > itemArmor.damageReduceAmount;
            } else {
                bl = false;
            }
        }
        if (bl && this.canEquipItem(itemStack)) {
            double d;
            switch (entityEquipmentSlot.getSlotType()) {
                case HAND: {
                    d = this.inventoryHandsDropChances[entityEquipmentSlot.getIndex()];
                    break;
                }
                case ARMOR: {
                    d = this.inventoryArmorDropChances[entityEquipmentSlot.getIndex()];
                    break;
                }
                default: {
                    d = 0.0;
                }
            }
            if (!itemStack2.isEmpty() && (double)(this.rand.nextFloat() - 0.1f) < d) {
                this.entityDropItem(itemStack2, 0.0f);
            }
            this.setItemStackToSlot(entityEquipmentSlot, itemStack);
            switch (entityEquipmentSlot.getSlotType()) {
                case HAND: {
                    this.inventoryHandsDropChances[entityEquipmentSlot.getIndex()] = 2.0f;
                    break;
                }
                case ARMOR: {
                    this.inventoryArmorDropChances[entityEquipmentSlot.getIndex()] = 2.0f;
                }
            }
            this.persistenceRequired = true;
            this.onItemPickup(entityItem, itemStack.getCount());
            entityItem.setDead();
        }
    }

    protected boolean canEquipItem(ItemStack itemStack) {
        return true;
    }

    protected boolean canDespawn() {
        return true;
    }

    protected void despawnEntity() {
        if (this.persistenceRequired) {
            this.idleTime = 0;
            return;
        }
        EntityPlayer entityPlayer = this.world.getClosestPlayerToEntity(this, -1.0);
        if (entityPlayer != null) {
            double d = entityPlayer.posX - this.posX;
            double d2 = entityPlayer.posY - this.posY;
            double d3 = entityPlayer.posZ - this.posZ;
            double d4 = d * d + d2 * d2 + d3 * d3;
            if (this.canDespawn() && d4 > 16384.0) {
                this.setDead();
            }
            if (this.idleTime > 600 && this.rand.nextInt(800) == 0 && d4 > 1024.0 && this.canDespawn()) {
                this.setDead();
            } else if (d4 < 1024.0) {
                this.idleTime = 0;
            }
        }
    }

    @Override
    protected final void updateEntityActionState() {
        ++this.idleTime;
        this.world.profiler.startSection("checkDespawn");
        this.despawnEntity();
        this.world.profiler.endSection();
        this.world.profiler.startSection("sensing");
        this.senses.clearSensingCache();
        this.world.profiler.endSection();
        this.world.profiler.startSection("targetSelector");
        this.targetTasks.onUpdateTasks();
        this.world.profiler.endSection();
        this.world.profiler.startSection("goalSelector");
        this.tasks.onUpdateTasks();
        this.world.profiler.endSection();
        this.world.profiler.startSection("navigation");
        this.navigator.onUpdateNavigation();
        this.world.profiler.endSection();
        this.world.profiler.startSection("mob tick");
        this.updateAITasks();
        this.world.profiler.endSection();
        if (this.isRiding() && this.getRidingEntity() instanceof EntityLiving) {
            EntityLiving entityLiving = (EntityLiving)this.getRidingEntity();
            entityLiving.getNavigator().setPath(this.getNavigator().getPath(), 1.5);
            entityLiving.getMoveHelper().read(this.getMoveHelper());
        }
        this.world.profiler.startSection("controls");
        this.world.profiler.startSection("move");
        this.moveHelper.onUpdateMoveHelper();
        this.world.profiler.endStartSection("look");
        this.lookHelper.onUpdateLook();
        this.world.profiler.endStartSection("jump");
        this.jumpHelper.doJump();
        this.world.profiler.endSection();
        this.world.profiler.endSection();
    }

    protected void updateAITasks() {
    }

    public int getVerticalFaceSpeed() {
        return 40;
    }

    public int getHorizontalFaceSpeed() {
        return 10;
    }

    public void faceEntity(Entity entity, float f, float f2) {
        double d;
        double d2 = entity.posX - this.posX;
        double d3 = entity.posZ - this.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            d = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
        } else {
            d = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (this.posY + (double)this.getEyeHeight());
        }
        double d4 = MathHelper.sqrt(d2 * d2 + d3 * d3);
        float f3 = (float)(MathHelper.atan2(d3, d2) * 57.2957763671875) - 90.0f;
        float f4 = (float)(-(MathHelper.atan2(d, d4) * 57.2957763671875));
        this.rotationPitch = this.updateRotation(this.rotationPitch, f4, f2);
        this.rotationYaw = this.updateRotation(this.rotationYaw, f3, f);
    }

    private float updateRotation(float f, float f2, float f3) {
        float f4 = MathHelper.wrapDegrees(f2 - f);
        if (f4 > f3) {
            f4 = f3;
        }
        if (f4 < -f3) {
            f4 = -f3;
        }
        return f + f4;
    }

    public boolean getCanSpawnHere() {
        IBlockState iBlockState = this.world.getBlockState(new BlockPos(this).down());
        return iBlockState.canEntitySpawn(this);
    }

    public boolean isNotColliding() {
        return !this.world.containsAnyLiquid(this.getEntityBoundingBox()) && this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
    }

    public float getRenderSizeModifier() {
        return 1.0f;
    }

    public int getMaxSpawnedInChunk() {
        return 4;
    }

    @Override
    public int getMaxFallHeight() {
        if (this.getAttackTarget() == null) {
            return 3;
        }
        int n = (int)(this.getHealth() - this.getMaxHealth() * 0.33f);
        if ((n -= (3 - this.world.getDifficulty().getId()) * 4) < 0) {
            n = 0;
        }
        return n + 3;
    }

    @Override
    public Iterable<ItemStack> getHeldEquipment() {
        return this.inventoryHands;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return this.inventoryArmor;
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot entityEquipmentSlot) {
        switch (entityEquipmentSlot.getSlotType()) {
            case HAND: {
                return this.inventoryHands.get(entityEquipmentSlot.getIndex());
            }
            case ARMOR: {
                return this.inventoryArmor.get(entityEquipmentSlot.getIndex());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {
        switch (entityEquipmentSlot.getSlotType()) {
            case HAND: {
                this.inventoryHands.set(entityEquipmentSlot.getIndex(), itemStack);
                break;
            }
            case ARMOR: {
                this.inventoryArmor.set(entityEquipmentSlot.getIndex(), itemStack);
            }
        }
    }

    @Override
    protected void dropEquipment(boolean bl, int n) {
        for (EntityEquipmentSlot entityEquipmentSlot : EntityEquipmentSlot.values()) {
            boolean bl2;
            double d;
            ItemStack itemStack = this.getItemStackFromSlot(entityEquipmentSlot);
            switch (entityEquipmentSlot.getSlotType()) {
                case HAND: {
                    d = this.inventoryHandsDropChances[entityEquipmentSlot.getIndex()];
                    break;
                }
                case ARMOR: {
                    d = this.inventoryArmorDropChances[entityEquipmentSlot.getIndex()];
                    break;
                }
                default: {
                    d = 0.0;
                }
            }
            boolean bl3 = bl2 = d > 1.0;
            if (itemStack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemStack) || !bl && !bl2 || !((double)(this.rand.nextFloat() - (float)n * 0.01f) < d)) continue;
            if (!bl2 && itemStack.isItemStackDamageable()) {
                itemStack.setItemDamage(itemStack.getMaxDamage() - this.rand.nextInt(1 + this.rand.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
            }
            this.entityDropItem(itemStack, 0.0f);
        }
    }

    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficultyInstance) {
        if (this.rand.nextFloat() < 0.15f * difficultyInstance.getClampedAdditionalDifficulty()) {
            float f;
            int n = this.rand.nextInt(2);
            float f2 = f = this.world.getDifficulty() == EnumDifficulty.HARD ? 0.1f : 0.25f;
            if (this.rand.nextFloat() < 0.095f) {
                ++n;
            }
            if (this.rand.nextFloat() < 0.095f) {
                ++n;
            }
            if (this.rand.nextFloat() < 0.095f) {
                ++n;
            }
            boolean bl = true;
            for (EntityEquipmentSlot entityEquipmentSlot : EntityEquipmentSlot.values()) {
                Item item;
                if (entityEquipmentSlot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) continue;
                ItemStack itemStack = this.getItemStackFromSlot(entityEquipmentSlot);
                if (!bl && this.rand.nextFloat() < f) break;
                bl = false;
                if (!itemStack.isEmpty() || (item = EntityLiving.getArmorByChance(entityEquipmentSlot, n)) == null) continue;
                this.setItemStackToSlot(entityEquipmentSlot, new ItemStack(item));
            }
        }
    }

    public static EntityEquipmentSlot getSlotForItemStack(ItemStack itemStack) {
        if (itemStack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN) || itemStack.getItem() == Items.SKULL) {
            return EntityEquipmentSlot.HEAD;
        }
        if (itemStack.getItem() instanceof ItemArmor) {
            return ((ItemArmor)itemStack.getItem()).armorType;
        }
        if (itemStack.getItem() == Items.ELYTRA) {
            return EntityEquipmentSlot.CHEST;
        }
        if (itemStack.getItem() == Items.SHIELD) {
            return EntityEquipmentSlot.OFFHAND;
        }
        return EntityEquipmentSlot.MAINHAND;
    }

    @Nullable
    public static Item getArmorByChance(EntityEquipmentSlot entityEquipmentSlot, int n) {
        switch (entityEquipmentSlot) {
            case HEAD: {
                if (n == 0) {
                    return Items.LEATHER_HELMET;
                }
                if (n == 1) {
                    return Items.GOLDEN_HELMET;
                }
                if (n == 2) {
                    return Items.CHAINMAIL_HELMET;
                }
                if (n == 3) {
                    return Items.IRON_HELMET;
                }
                if (n == 4) {
                    return Items.DIAMOND_HELMET;
                }
            }
            case CHEST: {
                if (n == 0) {
                    return Items.LEATHER_CHESTPLATE;
                }
                if (n == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                }
                if (n == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                }
                if (n == 3) {
                    return Items.IRON_CHESTPLATE;
                }
                if (n == 4) {
                    return Items.DIAMOND_CHESTPLATE;
                }
            }
            case LEGS: {
                if (n == 0) {
                    return Items.LEATHER_LEGGINGS;
                }
                if (n == 1) {
                    return Items.GOLDEN_LEGGINGS;
                }
                if (n == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                }
                if (n == 3) {
                    return Items.IRON_LEGGINGS;
                }
                if (n == 4) {
                    return Items.DIAMOND_LEGGINGS;
                }
            }
            case FEET: {
                if (n == 0) {
                    return Items.LEATHER_BOOTS;
                }
                if (n == 1) {
                    return Items.GOLDEN_BOOTS;
                }
                if (n == 2) {
                    return Items.CHAINMAIL_BOOTS;
                }
                if (n == 3) {
                    return Items.IRON_BOOTS;
                }
                if (n != 4) break;
                return Items.DIAMOND_BOOTS;
            }
        }
        return null;
    }

    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficultyInstance) {
        float f = difficultyInstance.getClampedAdditionalDifficulty();
        if (!this.getHeldItemMainhand().isEmpty() && this.rand.nextFloat() < 0.25f * f) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int)(5.0f + f * (float)this.rand.nextInt(18)), false));
        }
        for (EntityEquipmentSlot entityEquipmentSlot : EntityEquipmentSlot.values()) {
            ItemStack itemStack;
            if (entityEquipmentSlot.getSlotType() != EntityEquipmentSlot.Type.ARMOR || (itemStack = this.getItemStackFromSlot(entityEquipmentSlot)).isEmpty() || !(this.rand.nextFloat() < 0.5f * f)) continue;
            this.setItemStackToSlot(entityEquipmentSlot, EnchantmentHelper.addRandomEnchantment(this.rand, itemStack, (int)(5.0f + f * (float)this.rand.nextInt(18)), false));
        }
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficultyInstance, @Nullable IEntityLivingData iEntityLivingData) {
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05, 1));
        if (this.rand.nextFloat() < 0.05f) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }
        return iEntityLivingData;
    }

    public boolean canBeSteered() {
        return false;
    }

    public void enablePersistence() {
        this.persistenceRequired = true;
    }

    public void setDropChance(EntityEquipmentSlot entityEquipmentSlot, float f) {
        switch (entityEquipmentSlot.getSlotType()) {
            case HAND: {
                this.inventoryHandsDropChances[entityEquipmentSlot.getIndex()] = f;
                break;
            }
            case ARMOR: {
                this.inventoryArmorDropChances[entityEquipmentSlot.getIndex()] = f;
            }
        }
    }

    public boolean canPickUpLoot() {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean bl) {
        this.canPickUpLoot = bl;
    }

    public boolean isNoDespawnRequired() {
        return this.persistenceRequired;
    }

    @Override
    public final boolean processInitialInteract(EntityPlayer entityPlayer, EnumHand enumHand) {
        if (this.getLeashed() && this.getLeashHolder() == entityPlayer) {
            this.clearLeashed(true, !entityPlayer.capabilities.isCreativeMode);
            return true;
        }
        ItemStack itemStack = entityPlayer.getHeldItem(enumHand);
        if (itemStack.getItem() == Items.LEAD && this.canBeLeashedTo(entityPlayer)) {
            this.setLeashHolder(entityPlayer, true);
            itemStack.shrink(1);
            return true;
        }
        if (this.processInteract(entityPlayer, enumHand)) {
            return true;
        }
        return super.processInitialInteract(entityPlayer, enumHand);
    }

    protected boolean processInteract(EntityPlayer entityPlayer, EnumHand enumHand) {
        return false;
    }

    protected void updateLeashedState() {
        if (this.leashNBTTag != null) {
            this.recreateLeash();
        }
        if (!this.isLeashed) {
            return;
        }
        if (!this.isEntityAlive()) {
            this.clearLeashed(true, true);
        }
        if (this.leashHolder == null || this.leashHolder.isDead) {
            this.clearLeashed(true, true);
            return;
        }
    }

    public void clearLeashed(boolean bl, boolean bl2) {
        if (this.isLeashed) {
            this.isLeashed = false;
            this.leashHolder = null;
            if (!this.world.isRemote && bl2) {
                this.dropItem(Items.LEAD, 1);
            }
            if (!this.world.isRemote && bl && this.world instanceof WorldServer) {
                ((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketEntityAttach(this, null));
            }
        }
    }

    public boolean canBeLeashedTo(EntityPlayer entityPlayer) {
        return !this.getLeashed() && !(this instanceof IMob);
    }

    public boolean getLeashed() {
        return this.isLeashed;
    }

    public Entity getLeashHolder() {
        return this.leashHolder;
    }

    public void setLeashHolder(Entity entity, boolean bl) {
        this.isLeashed = true;
        this.leashHolder = entity;
        if (!this.world.isRemote && bl && this.world instanceof WorldServer) {
            ((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketEntityAttach(this, this.leashHolder));
        }
        if (this.isRiding()) {
            this.dismountRidingEntity();
        }
    }

    @Override
    public boolean startRiding(Entity entity, boolean bl) {
        boolean bl2 = super.startRiding(entity, bl);
        if (bl2 && this.getLeashed()) {
            this.clearLeashed(true, true);
        }
        return bl2;
    }

    private void recreateLeash() {
        if (this.isLeashed && this.leashNBTTag != null) {
            if (this.leashNBTTag.hasUniqueId("UUID")) {
                UUID uUID = this.leashNBTTag.getUniqueId("UUID");
                List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(10.0));
                for (EntityLivingBase entityLivingBase : list) {
                    if (!entityLivingBase.getUniqueID().equals(uUID)) continue;
                    this.setLeashHolder(entityLivingBase, true);
                    break;
                }
            } else if (this.leashNBTTag.hasKey("X", 99) && this.leashNBTTag.hasKey("Y", 99) && this.leashNBTTag.hasKey("Z", 99)) {
                BlockPos blockPos = new BlockPos(this.leashNBTTag.getInteger("X"), this.leashNBTTag.getInteger("Y"), this.leashNBTTag.getInteger("Z"));
                EntityLeashKnot entityLeashKnot = EntityLeashKnot.getKnotForPosition(this.world, blockPos);
                if (entityLeashKnot == null) {
                    entityLeashKnot = EntityLeashKnot.createKnot(this.world, blockPos);
                }
                this.setLeashHolder(entityLeashKnot, true);
            } else {
                this.clearLeashed(false, true);
            }
        }
        this.leashNBTTag = null;
    }

    @Override
    public boolean replaceItemInInventory(int n, ItemStack itemStack) {
        EntityEquipmentSlot entityEquipmentSlot;
        if (n == 98) {
            entityEquipmentSlot = EntityEquipmentSlot.MAINHAND;
        } else if (n == 99) {
            entityEquipmentSlot = EntityEquipmentSlot.OFFHAND;
        } else if (n == 100 + EntityEquipmentSlot.HEAD.getIndex()) {
            entityEquipmentSlot = EntityEquipmentSlot.HEAD;
        } else if (n == 100 + EntityEquipmentSlot.CHEST.getIndex()) {
            entityEquipmentSlot = EntityEquipmentSlot.CHEST;
        } else if (n == 100 + EntityEquipmentSlot.LEGS.getIndex()) {
            entityEquipmentSlot = EntityEquipmentSlot.LEGS;
        } else if (n == 100 + EntityEquipmentSlot.FEET.getIndex()) {
            entityEquipmentSlot = EntityEquipmentSlot.FEET;
        } else {
            return false;
        }
        if (itemStack.isEmpty() || EntityLiving.isItemStackInSlot(entityEquipmentSlot, itemStack) || entityEquipmentSlot == EntityEquipmentSlot.HEAD) {
            this.setItemStackToSlot(entityEquipmentSlot, itemStack);
            return true;
        }
        return false;
    }

    @Override
    public boolean canPassengerSteer() {
        return this.canBeSteered() && super.canPassengerSteer();
    }

    public static boolean isItemStackInSlot(EntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {
        EntityEquipmentSlot entityEquipmentSlot2 = EntityLiving.getSlotForItemStack(itemStack);
        return entityEquipmentSlot2 == entityEquipmentSlot || entityEquipmentSlot2 == EntityEquipmentSlot.MAINHAND && entityEquipmentSlot == EntityEquipmentSlot.OFFHAND || entityEquipmentSlot2 == EntityEquipmentSlot.OFFHAND && entityEquipmentSlot == EntityEquipmentSlot.MAINHAND;
    }

    @Override
    public boolean isServerWorld() {
        return super.isServerWorld() && !this.isAIDisabled();
    }

    public void setNoAI(boolean bl) {
        byte by = this.dataManager.get(AI_FLAGS);
        this.dataManager.set(AI_FLAGS, bl ? (byte)(by | 1) : (byte)(by & 0xFFFFFFFE));
    }

    public void setLeftHanded(boolean bl) {
        byte by = this.dataManager.get(AI_FLAGS);
        this.dataManager.set(AI_FLAGS, bl ? (byte)(by | 2) : (byte)(by & 0xFFFFFFFD));
    }

    public boolean isAIDisabled() {
        return (this.dataManager.get(AI_FLAGS) & 1) != 0;
    }

    public boolean isLeftHanded() {
        return (this.dataManager.get(AI_FLAGS) & 2) != 0;
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return this.isLeftHanded() ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
    }

    public static enum SpawnPlacementType {
        ON_GROUND,
        IN_AIR,
        IN_WATER;

    }
}

