/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerSheep;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityInfSheep
extends EntityPInfected
implements EntityCanMelt {
    private static final DataParameter<Float> HEIGH = EntityDataManager.createKey(EntityInfSheep.class, (DataSerializer)DataSerializers.FLOAT);
    private static final DataParameter<Boolean> MELTING = EntityDataManager.createKey(EntityInfSheep.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TEX_VARIANT = EntityDataManager.createKey(EntityInfSheep.class, (DataSerializer)DataSerializers.VARINT);
    private float aSize;
    private int sound;

    public EntityInfSheep(World worldIn) {
        super(worldIn);
        this.setSize(0.9f, 1.3f);
        this.aSize = 1.0f;
        this.canModRender = 1;
        this.type = (byte)12;
        this.fuseTime = 40;
        this.thisMelting = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 14;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infsheepCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.08));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIGetFollowers(this, 1, 16));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.INFSHEEP_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.INFSHEEP_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue((double)0.23f);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.INFSHEEP_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.INFSHEEP_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.infectedFollow);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(HEIGH, (Object)Float.valueOf(0.0f));
        EntityDataManager.of(this).register(MELTING, (Object)false);
        EntityDataManager.of(this).register(TEX_VARIANT, (Object)0);
    }

    public int getTextureVariant() {
        return (Integer)EntityDataManager.of(this).get(TEX_VARIANT);
    }

    public void setTextureVariant(int v) {
        if (v < 0) {
            v = 0;
        }
        if (v > 2) {
            v = 2;
        }
        EntityDataManager.of(this).set(TEX_VARIANT, (Object)v);
    }

    private void rollTextureVariantWeighted() {
        float r = this.rand.nextFloat();
        if (r < 0.1f) {
            this.setTextureVariant(2);
        } else if (r < 0.4f) {
            this.setTextureVariant(1);
        } else {
            this.setTextureVariant(0);
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.melting();
    }

    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        if (!this.worldObj.isRemote) {
            this.rollTextureVariantWeighted();
        }
        return livingdata;
    }

    @Override
    protected void onDeathUpdate() {
        super.onDeathUpdate();
        if (this.getTHeigh() < 1.57f && !this.worldObj.isRemote) {
            this.setTHeigh(0.17f);
        }
        if (this.deathTime == 20 && !this.worldObj.isRemote && this.rand.nextDouble() <= SRPAttributes.INFSHEEP_HEADCHANCE) {
            ParasiteSummon.spawnM(this, new String[]{"srparasites:sim_sheephead;1;1"}, 0, false, this.getCustomNameTag());
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("TextureVariant", this.getTextureVariant());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("TextureVariant", 3)) {
            this.setTextureVariant(compound.getInteger("TextureVariant"));
        }
    }

    @Override
    public void melt() {
        this.setWait(1000);
        EntityDataManager.of(this).set(HEIGH, (Object)Float.valueOf(1.3f));
        EntityDataManager.of(this).set(MELTING, (Object)true);
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
                if ((double)this.getTHeigh() <= 0.7 || this.sound >= 63) {
                    EntityLesh out = new EntityLesh(this.worldObj);
                    out.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                    if (this.getCustomNameTag() != null) {
                        out.setMinecartName(this.getCustomNameTag());
                    }
                    this.setDead();
                    this.worldObj.spawnEntityInWorld((Entity)out);
                    out.setLegs(SRPAttributes.INFSHEEP_V, false);
                }
            } else {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
        }
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

    @Override
    protected void selfExplode() {
        super.selfExplode();
        ParasiteSummon.spawnM(this, new String[]{SRPConfigMobs.infsheepmob}, 0, false, this.getCustomNameTag());
    }

    public float getEyeHeight() {
        return 0.95f * this.height;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.INFECTEDSHEEP_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDSHEEP_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.INFECTEDSHEEP_DEATH;
    }

    @Override
    public EntityPFeral getFeral(World in) {
        return new EntityFerSheep(in);
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15f, 1.0f);
    }

    private void cycleTextureVariant() {
        int v = this.getTextureVariant();
        v = (v + 1) % 3;
        this.setTextureVariant(v);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        int before;
        ItemStack stack = player.getHeldItem(hand);
        if (super.processInteract(player, hand)) {
            return true;
        }
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() != Items.dye) {
            return false;
        }
        int desired = this.variantFromDyeMeta(stack.getItemDamage());
        if (desired < 0) {
            return false;
        }
        if (!this.worldObj.isRemote && (before = this.getTextureVariant()) != desired) {
            this.setTextureVariant(desired);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return true;
    }

    private int variantFromDyeMeta(int meta) {
        if (meta == EnumDyeColor.WHITE.getDyeDamage()) {
            return 0;
        }
        if (meta == EnumDyeColor.GRAY.getDyeDamage()) {
            return 1;
        }
        if (meta == EnumDyeColor.SILVER.getDyeDamage()) {
            return 1;
        }
        if (meta == EnumDyeColor.BLACK.getDyeDamage()) {
            return 2;
        }
        return -1;
    }
}

