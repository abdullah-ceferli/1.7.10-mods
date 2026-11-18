/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.MoverType
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityCanister;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityGore
extends Entity {
    private static final DataParameter<Integer> SKIN = EntityDataManager.createKey(EntityGore.class, (DataSerializer)DataSerializers.VARINT);
    private int groun;
    private byte type;
    public ArrayList<String> inbBlockName;
    public ArrayList<Integer> inbBlockNumber;
    public String entityName;

    public EntityGore(World worldIn) {
        super(worldIn);
        this.isImmuneToFire = true;
        this.setSize(0.4f, 0.4f);
    }

    public EntityGore(World worldIn, double x, double y, double z, EntityLivingBase igniter, float stren) {
        this(worldIn);
        this.setPosition(x, y, z);
        float f = (float)(Math.random() * (Math.PI * 2));
        this.motionX = -((float)Math.sin(f)) * 0.02f;
        this.motionY = 0.2f;
        this.motionZ = -((float)Math.cos(f)) * 0.02f;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.inbBlockName = new ArrayList();
        this.inbBlockNumber = new ArrayList();
    }

    protected void entityInit() {
        EntityDataManager.of(this).register(SKIN, (Object)0);
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public void onUpdate() {
        if (this.ticksExisted % 20 == 0 && BlockStates.get(this.worldObj, EntityQueries.position(this)).getMaterial() == Material.water) {
            this.setDead();
            return;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (!this.hasNoGravity()) {
            this.motionY -= (double)0.04f;
        }
        this.moveEntity(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= (double)0.98f;
        this.motionY *= (double)0.98f;
        this.motionZ *= (double)0.98f;
        if (this.onGround) {
            this.motionX *= (double)0.7f;
            this.motionZ *= (double)0.7f;
        }
        this.handleWaterMovement();
        if (this.worldObj.isRemote && this.ticksExisted % 5 == 0 && !this.onGround) {
            switch (this.getSkin()) {
                case 1: {
                    int i;
                    for (i = 0; i <= 0; ++i) {
                        this.spawnParticles(SRPEnumParticle.GSPLASH, 0, 0, 0);
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                    }
                    break;
                }
                case 2: {
                    int i;
                    for (i = 0; i <= 0; ++i) {
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                    }
                    break;
                }
                case 3: {
                    int i;
                    for (i = 0; i <= 0; ++i) {
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 200, 200, 0);
                        this.spawnParticles(SRPEnumParticle.GSPLASH, 3, -1, -1);
                    }
                    break;
                }
                case 4: {
                    int i;
                    for (i = 0; i <= 0; ++i) {
                        this.spawnParticles(SRPEnumParticle.GSPLASH, 0, 0, 0);
                    }
                    break;
                }
            }
        }
        if (this.onGround) {
            ++this.groun;
        }
        if (this.ticksExisted >= 200) {
            this.setDead();
        }
        if (this.groun >= 1) {
            if (!this.worldObj.isRemote) {
                switch (this.type) {
                    case 1: {
                        this.placeBlood(BlockStates.defaultState(SRPBlocks.goreSim).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 2: {
                        this.placeBlood(BlockStates.defaultState(SRPBlocks.gorePri).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 3: {
                        this.placeBlood(BlockStates.defaultState(SRPBlocks.goreAda).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 4: {
                        this.placeBlood(BlockStates.defaultState(SRPBlocks.gorePur).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 5: {
                        this.placeBlood(BlockStates.defaultState(SRPBlocks.goreFer).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 6: {
                        this.placeBlood(BlockStates.defaultState(SRPBlocks.goreMar).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 10: {
                        Block block = BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock();
                        if (block == Blocks.air || block == Blocks.grass) {
                            BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(SRPBlocks.ParasiteCanisterActive), 3);
                            this.addCystItems(EntityQueries.position(this));
                            break;
                        }
                        BlockPos helper = EntityQueries.position(this);
                        int rand = this.rand.nextInt(4);
                        for (int i = 4; i >= 0; --i) {
                            helper = EntityGore.directionToSpread(EntityQueries.position(this), rand);
                            block = BlockStates.get(this.worldObj, helper).getBlock();
                            if (block == Blocks.air || block == Blocks.grass) {
                                BlockStates.set(this.worldObj, helper, BlockStates.defaultState(SRPBlocks.ParasiteCanisterActive), 3);
                                this.addCystItems(helper);
                                this.setDead();
                                return;
                            }
                            rand = (rand + 1) % 4;
                        }
                        BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(SRPBlocks.ParasiteCanisterActive), 3);
                        this.addCystItems(EntityQueries.position(this));
                        break;
                    }
                    case 11: {
                        Block block = BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock();
                        if (block == Blocks.air || block == Blocks.grass) {
                            BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(SRPBlocks.dodN), 3);
                            break;
                        }
                        BlockPos helper = EntityQueries.position(this);
                        int rand = this.rand.nextInt(4);
                        for (int i = 4; i >= 0; --i) {
                            helper = EntityGore.directionToSpread(EntityQueries.position(this), rand);
                            block = BlockStates.get(this.worldObj, helper).getBlock();
                            if (block == Blocks.air || block == Blocks.grass) {
                                BlockStates.set(this.worldObj, helper, BlockStates.defaultState(SRPBlocks.dodN), 3);
                                this.setDead();
                                return;
                            }
                            rand = (rand + 1) % 4;
                        }
                        break;
                    }
                    case 12: {
                        IBlockState state = BlockStates.fromMeta(SRPBlocks.InfestRemain, 1);
                        Block block22 = BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock();
                        if ((block22 == Blocks.air || block22 instanceof BlockBush) && block22 != SRPBlocks.goreSim) {
                            BlockStates.set(this.worldObj, EntityQueries.position(this), state);
                            break;
                        }
                        BlockPos helper = EntityQueries.position(this);
                        int rand = this.rand.nextInt(4);
                        int ccc = 0;
                        for (int i = 4; i >= 0; --i) {
                            helper = EntityGore.directionToSpread(EntityQueries.position(this), rand);
                            block22 = BlockStates.get(this.worldObj, helper).getBlock();
                            if ((block22 == Blocks.air || block22 instanceof BlockBush) && block22 != SRPBlocks.goreSim) {
                                BlockStates.set(this.worldObj, helper, state);
                                if (++ccc > 2) {
                                    return;
                                }
                                rand = (rand + 1) % 4;
                                continue;
                            }
                            rand = (rand + 1) % 4;
                        }
                        break;
                    }
                    case 111: {
                        if (this.entityName == null || !BlockStates.get(this.worldObj, EntityQueries.position(this).down()).isFullBlock() || !(BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() instanceof BlockBush) && BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() != Blocks.air) break;
                        BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(SRPBlocks.goreSim).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.BIG)));
                        EntityRemain nnn = new EntityRemain(this.worldObj);
                        nnn.setLocationAndAngles((double)EntityQueries.position(this).getX() + 0.5, EntityQueries.position(this).getY(), (double)EntityQueries.position(this).getZ() + 0.5, 0.0f, 0.0f);
                        nnn.setParasite(this.entityName);
                        nnn.setSkin((byte)this.getSkin());
                        nnn.setGoal(20 * SRPConfig.feralRemainValue);
                        this.worldObj.spawnEntityInWorld((Entity)nnn);
                    }
                }
            }
            this.setDead();
            if (this.worldObj.isRemote) {
                for (int k = 0; k < 10; ++k) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                }
            }
        }
    }

    private void addCystItems(BlockPos pos) {
        if (this.inbBlockName == null) {
            return;
        }
        if (this.inbBlockName.isEmpty()) {
            return;
        }
        TileEntity tileentity = WorldAccess.getTileEntity(this.worldObj, pos);
        if (tileentity instanceof TileEntityCanister) {
            TileEntityCanister cyst = (TileEntityCanister)tileentity;
            String[] here = new String[2];
            ArrayList<Object> preDrops = new ArrayList<ItemStack>();
            int loop = 0;
            for (int i = 0; i < this.inbBlockName.size(); ++i) {
                here = this.inbBlockName.get(i).split(";");
                String name = here[0];
                int meta = 0;
                try {
                    meta = Integer.parseInt(here[1]);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                Block block = Block.getBlockFromName((String)name);
                while (loop < this.inbBlockNumber.get(i)) {
                    boolean fat = true;
                    try {
                        preDrops.addAll(block.getDrops((IBlockAccess)this.worldObj, pos, BlockStates.fromMeta(block, meta), 1));
                        fat = false;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    if (fat) {
                        preDrops.add(new ItemStack(block));
                    }
                    ++loop;
                }
                cyst.addStack(preDrops);
                loop = 0;
                preDrops = new ArrayList();
            }
        }
    }

    private void placeBlood(IBlockState state) {
        Block block = BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock();
        if ((block == Blocks.air || block instanceof BlockBush) && !(block instanceof BlockGore)) {
            BlockStates.set(this.worldObj, EntityQueries.position(this), state);
        } else {
            BlockPos helper = EntityQueries.position(this);
            int rand = this.rand.nextInt(4);
            for (int i = 4; i >= 0; --i) {
                helper = EntityGore.directionToSpread(EntityQueries.position(this), rand);
                block = BlockStates.get(this.worldObj, helper).getBlock();
                if ((block == Blocks.air || block instanceof BlockBush) && !(block instanceof BlockGore)) {
                    BlockStates.set(this.worldObj, helper, state);
                    return;
                }
                rand = (rand + 1) % 4;
            }
        }
    }

    private static BlockPos directionToSpread(BlockPos atm, int choice) {
        switch (choice) {
            case 0: {
                atm = atm.north();
                break;
            }
            case 1: {
                atm = atm.east();
                break;
            }
            case 2: {
                atm = atm.west();
                break;
            }
            default: {
                atm = atm.south();
            }
        }
        return atm;
    }

    public void setType(byte in) {
        this.type = in;
        if (this.type == 12) {
            in = (byte)4;
        }
        if (this.type == 111) {
            in = 1;
        }
        this.setSkin(in);
    }

    public void onLivingUpdate() {
    }

    protected void collideWithNearbyEntities() {
    }

    protected void collideWithEntity(Entity entityIn) {
        entityIn.applyEntityCollision((Entity)this);
    }

    public void onEntityUpdate() {
        super.onEntityUpdate();
    }

    public void setMotion(double xSpeedIn, double ySpeedIn, double zSpeedIn, double capX, double capY) {
        xSpeedIn = Math.min(xSpeedIn, capX);
        ySpeedIn = Math.min(ySpeedIn, capY);
        zSpeedIn = Math.min(zSpeedIn, capX);
        this.motionX = xSpeedIn * (Math.random() * 2.0 - 1.0);
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn * (Math.random() * 2.0 - 1.0);
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("parasitetype", this.getSkin());
        compound.setInteger("bloodtype", (int)this.type);
        NBTTagList allResS = new NBTTagList();
        NBTTagList allResI = new NBTTagList();
        if (this.inbBlockName == null) {
            return;
        }
        for (int i = 0; i < this.inbBlockName.size(); ++i) {
            String res = this.inbBlockName.get(i);
            NBTTagCompound resT = new NBTTagCompound();
            resT.setString("block" + i, res);
            allResS.appendTag((NBTBase)resT);
            int resi = this.inbBlockNumber.get(i);
            NBTTagCompound resU = new NBTTagCompound();
            resU.setInteger("block" + i, resi);
            allResI.appendTag((NBTBase)resU);
        }
        compound.setTag("srpinvblocksname", (NBTBase)allResS);
        compound.setTag("srpinvblocksnumber", (NBTBase)allResI);
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("parasitetype", 99)) {
            this.setSkin(compound.getInteger("parasitetype"));
        }
        if (compound.hasKey("bloodtype", 99)) {
            this.type = (byte)compound.getInteger("bloodtype");
        }
        if (compound.hasKey("srpinvblocks")) {
            NBTTagList allResS = compound.getTagList("srpinvblocksname", 10);
            NBTTagList allResI = compound.getTagList("srpinvblocksnumber", 10);
            if (allResS.tagCount() != allResI.tagCount()) {
                return;
            }
            for (int i = 0; i < allResS.tagCount(); ++i) {
                NBTTagCompound resT = allResS.getCompoundTagAt(i);
                String res = resT.getString("block" + i);
                this.inbBlockName.add(i, res);
                NBTTagCompound resU = allResI.getCompoundTagAt(i);
                int resi = resU.getInteger("block" + i);
                this.inbBlockNumber.add(i, resi);
            }
        }
    }

    public float getEyeHeight() {
        return 0.0f;
    }

    public int getSkin() {
        return (Integer)EntityDataManager.of(this).get(SKIN);
    }

    public void setSkin(int texture) {
        EntityDataManager.of(this).set(SKIN, (Object)texture);
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
        double d0 = this.rand.nextGaussian() * 0.02;
        double d1 = this.rand.nextGaussian() * 0.02;
        double d2 = this.rand.nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d0, d1, d2, r, g, b);
    }
}

