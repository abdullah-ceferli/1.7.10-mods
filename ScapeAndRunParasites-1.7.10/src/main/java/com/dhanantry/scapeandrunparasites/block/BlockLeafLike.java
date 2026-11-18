/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemShears
 *  net.minecraft.item.ItemStack
 *  net.minecraft.stats.StatList
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IShearable
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.fx.ClientSRPParticles;
import com.dhanantry.scapeandrunparasites.client.fx.ParticleInfestedLeaf;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLeafLike
extends Block
implements IShearable {
    public static final PropertyInteger DECAY_AGE = PropertyInteger.create((String)"decay_age", (int)0, (int)5);
    private static final int BASE_FALSE_APPLE_CHANCE = 40;

    public BlockLeafLike(String regName) {
        super(Material.leaves);
        RegistryNames.set(this, regName);
        this.setBlockName("srparasites." + regName);
        this.setHardness(0.2f);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.PLANT);
        this.setLightOpacity(0);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)DECAY_AGE, (Comparable)Integer.valueOf(0)));
        SRPBlocks.SRP_BLOCKS.add(this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), RegistryNames.get(this)));
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }
        if (this.touchingAnySRP(world, pos)) {
            int age = (Integer)state.getValue((IProperty)DECAY_AGE);
            if (age > 0 && rand.nextInt(6) == 0) {
                BlockStates.set(world, pos, state.withProperty((IProperty)DECAY_AGE, (Comparable)Integer.valueOf(age - 1)), 2);
            }
            return;
        }
        int age = (Integer)state.getValue((IProperty)DECAY_AGE);
        if (rand.nextInt(12) == 0) {
            if (age < 5) {
                BlockStates.set(world, pos, state.withProperty((IProperty)DECAY_AGE, (Comparable)Integer.valueOf(age + 1)), 2);
            } else {
                WorldAccess.destroyBlock(world, pos, true);
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (!world.isRemote) {
            return;
        }
        if (!WorldAccess.isAirBlock(world, pos.down())) {
            return;
        }
        if (rand.nextInt(14) != 0) {
            return;
        }
        if (!ClientSRPParticles.canSpawn()) {
            return;
        }
        double x = (double)pos.getX() + 0.2 + rand.nextDouble() * 0.6;
        double y = (double)pos.getY() + 0.95;
        double z = (double)pos.getZ() + 0.2 + rand.nextDouble() * 0.6;
        ParticleInfestedLeaf p = new ParticleInfestedLeaf(world, x, y, z);
        ClientSRPParticles.fx().addEffect((Particle)p);
        ClientSRPParticles.onSpawn();
    }

    private boolean touchingAnySRP(World world, BlockPos pos) {
        for (EnumFacing f : EnumFacing.faceList) {
            BlockPos n = pos.offset(f);
            IBlockState s = BlockStates.get(world, n);
            Block b = s.getBlock();
            ResourceLocation key = (ResourceLocation)Block.blockRegistry.getNameForObject((Object)b);
            if (key == null || !"srparasites".equals(key.getResourceDomain())) continue;
            return true;
        }
        return false;
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        Random rand;
        int chance = Math.max(1, 40 - 8 * fortune);
        Random random = rand = world instanceof World ? ((World)world).rand : new Random();
        if (rand.nextInt(chance) == 0) {
            drops.add((Object)new ItemStack(SRPItems.falseapple));
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public int quantityDroppedWithBonus(int fortune, Random random) {
        return 0;
    }

    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }

    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
        return item.getItem() instanceof ItemShears || item.getItem() == Items.shears;
    }

    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return true;
    }

    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 60;
    }

    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 30;
    }

    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(Item.getItemFromBlock((Block)this), 1, BlockStates.meta(this, BlockStates.get(((World)world), pos))));
        return ret;
    }

    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack tool) {
        if (!world.isRemote && tool != null && this.isShearable(tool, (IBlockAccess)world, pos)) {
            for (ItemStack drop : this.onSheared(tool, (IBlockAccess)world, pos, 0)) {
                BlockLeafLike.spawnAsEntity((World)world, (BlockPos)pos, (ItemStack)drop);
            }
            if (tool.getItem() instanceof ItemShears) {
                tool.damageItem(1, (EntityLivingBase)player);
            }
            player.triggerAchievement(StatList.getBlockStats((Block)this));
            WorldAccess.setBlockToAir(world, pos);
        } else {
            super.harvestBlock(world, player, pos, state, te, tool);
        }
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{DECAY_AGE});
    }

    public IBlockState getStateFromMeta(int meta) {
        int age = Math.max(0, Math.min(5, meta & 7));
        return BlockStates.defaultState(this).withProperty((IProperty)DECAY_AGE, (Comparable)Integer.valueOf(age));
    }

    public int getMetaFromState(IBlockState state) {
        return (Integer)state.getValue((IProperty)DECAY_AGE);
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState neighbor = BlockStates.get(world, pos.offset(side));
        return neighbor.getBlock() != this || !neighbor.isOpaqueCube();
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }
}

