/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.item;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItem
extends Entity {
    private static final Logger logger = LogManager.getLogger();
    public int age;
    public int delayBeforeCanPickup;
    private int health = 5;
    private String field_145801_f;
    private String field_145802_g;
    public float hoverStart = (float)(Math.random() * Math.PI * 2.0);
    private static final String __OBFID = "CL_00001669";
    public int lifespan = 6000;

    public EntityItem(World p_i1709_1_, double p_i1709_2_, double p_i1709_4_, double p_i1709_6_) {
        super(p_i1709_1_);
        this.setSize(0.25f, 0.25f);
        this.yOffset = this.height / 2.0f;
        this.setPosition(p_i1709_2_, p_i1709_4_, p_i1709_6_);
        this.rotationYaw = (float)(Math.random() * 360.0);
        this.motionX = (float)(Math.random() * (double)0.2f - (double)0.1f);
        this.motionY = 0.2f;
        this.motionZ = (float)(Math.random() * (double)0.2f - (double)0.1f);
    }

    public EntityItem(World p_i1710_1_, double p_i1710_2_, double p_i1710_4_, double p_i1710_6_, ItemStack p_i1710_8_) {
        this(p_i1710_1_, p_i1710_2_, p_i1710_4_, p_i1710_6_);
        this.setEntityItemStack(p_i1710_8_);
        this.lifespan = p_i1710_8_.getItem() == null ? 6000 : p_i1710_8_.getItem().getEntityLifespan(p_i1710_8_, p_i1710_1_);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    public EntityItem(World p_i1711_1_) {
        super(p_i1711_1_);
        this.setSize(0.25f, 0.25f);
        this.yOffset = this.height / 2.0f;
    }

    @Override
    protected void entityInit() {
        this.getDataWatcher().addObjectByDataType(10, 5);
    }

    @Override
    public void onUpdate() {
        ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null && stack.getItem().onEntityItemUpdate(this)) {
            return;
        }
        if (this.getEntityItem() == null) {
            this.setDead();
        } else {
            boolean flag;
            super.onUpdate();
            if (this.delayBeforeCanPickup > 0) {
                --this.delayBeforeCanPickup;
            }
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= (double)0.04f;
            this.noClip = this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0, this.posZ);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            boolean bl = flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;
            if (flag || this.ticksExisted % 25 == 0) {
                if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial() == Material.lava) {
                    this.motionY = 0.2f;
                    this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f;
                    this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f;
                    this.playSound("random.fizz", 0.4f, 2.0f + this.rand.nextFloat() * 0.4f);
                }
                if (!this.worldObj.isRemote) {
                    this.searchForOtherItemsNearby();
                }
            }
            float f = 0.98f;
            if (this.onGround) {
                f = this.worldObj.getBlock((int)MathHelper.floor_double((double)this.posX), (int)(MathHelper.floor_double((double)this.boundingBox.minY) - 1), (int)MathHelper.floor_double((double)this.posZ)).slipperiness * 0.98f;
            }
            this.motionX *= (double)f;
            this.motionY *= (double)0.98f;
            this.motionZ *= (double)f;
            if (this.onGround) {
                this.motionY *= -0.5;
            }
            ++this.age;
            ItemStack item = this.getDataWatcher().getWatchableObjectItemStack(10);
            if (!this.worldObj.isRemote && this.age >= this.lifespan) {
                if (item != null) {
                    ItemExpireEvent event = new ItemExpireEvent(this, item.getItem() == null ? 6000 : item.getItem().getEntityLifespan(item, this.worldObj));
                    if (MinecraftForge.EVENT_BUS.post(event)) {
                        this.lifespan += event.extraLife;
                    } else {
                        this.setDead();
                    }
                } else {
                    this.setDead();
                }
            }
            if (item != null && item.stackSize <= 0) {
                this.setDead();
            }
        }
    }

    private void searchForOtherItemsNearby() {
        for (EntityItem entityitem : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.5, 0.0, 0.5))) {
            this.combineItems(entityitem);
        }
    }

    public boolean combineItems(EntityItem p_70289_1_) {
        if (p_70289_1_ == this) {
            return false;
        }
        if (p_70289_1_.isEntityAlive() && this.isEntityAlive()) {
            ItemStack itemstack = this.getEntityItem();
            ItemStack itemstack1 = p_70289_1_.getEntityItem();
            if (itemstack1.getItem() != itemstack.getItem()) {
                return false;
            }
            if (itemstack1.hasTagCompound() ^ itemstack.hasTagCompound()) {
                return false;
            }
            if (itemstack1.hasTagCompound() && !itemstack1.getTagCompound().equals(itemstack.getTagCompound())) {
                return false;
            }
            if (itemstack1.getItem() == null) {
                return false;
            }
            if (itemstack1.getItem().getHasSubtypes() && itemstack1.getItemDamage() != itemstack.getItemDamage()) {
                return false;
            }
            if (itemstack1.stackSize < itemstack.stackSize) {
                return p_70289_1_.combineItems(this);
            }
            if (itemstack1.stackSize + itemstack.stackSize > itemstack1.getMaxStackSize()) {
                return false;
            }
            itemstack1.stackSize += itemstack.stackSize;
            p_70289_1_.delayBeforeCanPickup = Math.max(p_70289_1_.delayBeforeCanPickup, this.delayBeforeCanPickup);
            p_70289_1_.age = Math.min(p_70289_1_.age, this.age);
            p_70289_1_.setEntityItemStack(itemstack1);
            this.setDead();
            return true;
        }
        return false;
    }

    public void setAgeToCreativeDespawnTime() {
        this.age = 4800;
    }

    @Override
    public boolean handleWaterMovement() {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
    }

    @Override
    protected void dealFireDamage(int p_70081_1_) {
        this.attackEntityFrom(DamageSource.inFire, p_70081_1_);
    }

    @Override
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        if (this.isEntityInvulnerable()) {
            return false;
        }
        if (this.getEntityItem() != null && this.getEntityItem().getItem() == Items.nether_star && p_70097_1_.isExplosion()) {
            return false;
        }
        this.setBeenAttacked();
        this.health = (int)((float)this.health - p_70097_2_);
        if (this.health <= 0) {
            this.setDead();
        }
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        p_70014_1_.setShort("Health", (byte)this.health);
        p_70014_1_.setShort("Age", (short)this.age);
        p_70014_1_.setInteger("Lifespan", this.lifespan);
        if (this.func_145800_j() != null) {
            p_70014_1_.setString("Thrower", this.field_145801_f);
        }
        if (this.func_145798_i() != null) {
            p_70014_1_.setString("Owner", this.field_145802_g);
        }
        if (this.getEntityItem() != null) {
            p_70014_1_.setTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        this.health = p_70037_1_.getShort("Health") & 0xFF;
        this.age = p_70037_1_.getShort("Age");
        if (p_70037_1_.hasKey("Owner")) {
            this.field_145802_g = p_70037_1_.getString("Owner");
        }
        if (p_70037_1_.hasKey("Thrower")) {
            this.field_145801_f = p_70037_1_.getString("Thrower");
        }
        NBTTagCompound nbttagcompound1 = p_70037_1_.getCompoundTag("Item");
        this.setEntityItemStack(ItemStack.loadItemStackFromNBT(nbttagcompound1));
        ItemStack item = this.getDataWatcher().getWatchableObjectItemStack(10);
        if (item == null || item.stackSize <= 0) {
            this.setDead();
        }
        if (p_70037_1_.hasKey("Lifespan")) {
            this.lifespan = p_70037_1_.getInteger("Lifespan");
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
        if (!this.worldObj.isRemote) {
            if (this.delayBeforeCanPickup > 0) {
                return;
            }
            EntityItemPickupEvent event = new EntityItemPickupEvent(p_70100_1_, this);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return;
            }
            ItemStack itemstack = this.getEntityItem();
            int i = itemstack.stackSize;
            if (this.delayBeforeCanPickup <= 0 && (this.field_145802_g == null || this.lifespan - this.age <= 200 || this.field_145802_g.equals(p_70100_1_.getCommandSenderName())) && (event.getResult() == Event.Result.ALLOW || i <= 0 || p_70100_1_.inventory.addItemStackToInventory(itemstack))) {
                EntityPlayer entityplayer1;
                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.log)) {
                    p_70100_1_.triggerAchievement(AchievementList.mineWood);
                }
                if (itemstack.getItem() == Item.getItemFromBlock(Blocks.log2)) {
                    p_70100_1_.triggerAchievement(AchievementList.mineWood);
                }
                if (itemstack.getItem() == Items.leather) {
                    p_70100_1_.triggerAchievement(AchievementList.killCow);
                }
                if (itemstack.getItem() == Items.diamond) {
                    p_70100_1_.triggerAchievement(AchievementList.diamonds);
                }
                if (itemstack.getItem() == Items.blaze_rod) {
                    p_70100_1_.triggerAchievement(AchievementList.blazeRod);
                }
                if (itemstack.getItem() == Items.diamond && this.func_145800_j() != null && (entityplayer1 = this.worldObj.getPlayerEntityByName(this.func_145800_j())) != null && entityplayer1 != p_70100_1_) {
                    entityplayer1.triggerAchievement(AchievementList.field_150966_x);
                }
                FMLCommonHandler.instance().firePlayerItemPickupEvent(p_70100_1_, this);
                this.worldObj.playSoundAtEntity(p_70100_1_, "random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                p_70100_1_.onItemPickup(this, i);
                if (itemstack.stackSize <= 0) {
                    this.setDead();
                }
            }
        }
    }

    @Override
    public String getCommandSenderName() {
        return StatCollector.translateToLocal("item." + this.getEntityItem().getUnlocalizedName());
    }

    @Override
    public boolean canAttackWithItem() {
        return false;
    }

    @Override
    public void travelToDimension(int p_71027_1_) {
        super.travelToDimension(p_71027_1_);
        if (!this.worldObj.isRemote) {
            this.searchForOtherItemsNearby();
        }
    }

    public ItemStack getEntityItem() {
        ItemStack itemstack = this.getDataWatcher().getWatchableObjectItemStack(10);
        return itemstack == null ? new ItemStack(Blocks.stone) : itemstack;
    }

    public void setEntityItemStack(ItemStack p_92058_1_) {
        this.getDataWatcher().updateObject(10, p_92058_1_);
        this.getDataWatcher().setObjectWatched(10);
    }

    public String func_145798_i() {
        return this.field_145802_g;
    }

    public void func_145797_a(String p_145797_1_) {
        this.field_145802_g = p_145797_1_;
    }

    public String func_145800_j() {
        return this.field_145801_f;
    }

    public void func_145799_b(String p_145799_1_) {
        this.field_145801_f = p_145799_1_;
    }
}

