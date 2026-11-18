/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLiving$SpawnPlacementType
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.IModelSRP;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDeadheadLeaves
extends Block
implements IModelSRP {
    public static final PropertyBool DECAYABLE = PropertyBool.create((String)"decayable");
    public static final PropertyBool CHECK_DECAY = PropertyBool.create((String)"check_decay");
    public static final PropertyBool SNOWY = PropertyBool.create((String)"snowy");
    private int[] surroundings;

    public BlockDeadheadLeaves(String name) {
        super(Material.leaves);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setHardness(0.2f);
        this.setLightOpacity(1);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.PLANT);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)DECAYABLE, (Comparable)Boolean.valueOf(true)).withProperty((IProperty)CHECK_DECAY, (Comparable)Boolean.valueOf(false)).withProperty((IProperty)SNOWY, (Comparable)Boolean.valueOf(false)));
        SRPBlocks.SRP_BLOCKS.add(this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) {
            return;
        }
        if (!((Boolean)state.getValue((IProperty)CHECK_DECAY)).booleanValue() || !((Boolean)state.getValue((IProperty)DECAYABLE)).booleanValue()) {
            return;
        }
        int decayDistance = 7;
        int checkRadius = decayDistance + 1;
        int arraySize = 32;
        int arrayArea = arraySize * arraySize;
        int arrayCenter = arraySize / 2;
        if (this.surroundings == null) {
            this.surroundings = new int[arraySize * arraySize * arraySize];
        }
        if (!WorldAccess.isAreaLoaded(world, pos.add(-checkRadius, -checkRadius, -checkRadius), pos.add(checkRadius, checkRadius, checkRadius))) {
            return;
        }
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = -decayDistance; x <= decayDistance; ++x) {
            for (int y = -decayDistance; y <= decayDistance; ++y) {
                for (int z = -decayDistance; z <= decayDistance; ++z) {
                    mutablePos.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    IBlockState checkState = BlockStates.get(world, (BlockPos)mutablePos);
                    Block checkBlock = checkState.getBlock();
                    int index = (x + arrayCenter) * arrayArea + (y + arrayCenter) * arraySize + z + arrayCenter;
                    this.surroundings[index] = checkBlock.canSustainLeaves(checkState, (IBlockAccess)world, (BlockPos)mutablePos) ? 0 : (checkBlock.isLeaves(checkState, (IBlockAccess)world, (BlockPos)mutablePos) ? -2 : -1);
                }
            }
        }
        for (int distance = 1; distance <= decayDistance; ++distance) {
            for (int x = -decayDistance; x <= decayDistance; ++x) {
                for (int y = -decayDistance; y <= decayDistance; ++y) {
                    for (int z = -decayDistance; z <= decayDistance; ++z) {
                        int index = (x + arrayCenter) * arrayArea + (y + arrayCenter) * arraySize + z + arrayCenter;
                        if (this.surroundings[index] != distance - 1) continue;
                        if (this.surroundings[index - arrayArea] == -2) {
                            this.surroundings[index - arrayArea] = distance;
                        }
                        if (this.surroundings[index + arrayArea] == -2) {
                            this.surroundings[index + arrayArea] = distance;
                        }
                        if (this.surroundings[index - arraySize] == -2) {
                            this.surroundings[index - arraySize] = distance;
                        }
                        if (this.surroundings[index + arraySize] == -2) {
                            this.surroundings[index + arraySize] = distance;
                        }
                        if (this.surroundings[index - 1] == -2) {
                            this.surroundings[index - 1] = distance;
                        }
                        if (this.surroundings[index + 1] != -2) continue;
                        this.surroundings[index + 1] = distance;
                    }
                }
            }
        }
        int centerIndex = arrayCenter * arrayArea + arrayCenter * arraySize + arrayCenter;
        if (this.surroundings[centerIndex] >= 0) {
            BlockStates.set(world, pos, state.withProperty((IProperty)CHECK_DECAY, (Comparable)Boolean.valueOf(false)), 4);
        } else {
            WorldAccess.destroyBlock(world, pos, true);
        }
    }

    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos) {
        if (((Boolean)state.getValue((IProperty)DECAYABLE)).booleanValue() && !((Boolean)state.getValue((IProperty)CHECK_DECAY)).booleanValue()) {
            BlockStates.set(world, pos, state.withProperty((IProperty)CHECK_DECAY, (Comparable)Boolean.valueOf(true)), 4);
        }
    }

    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return true;
    }

    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState above = BlockStates.get(world, pos.up());
        boolean snowy = above.getBlock() == Blocks.snow || above.getBlock() == Blocks.snow_layer;
        return state.withProperty((IProperty)SNOWY, (Comparable)Boolean.valueOf(snowy));
    }

    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (((Boolean)state.getValue((IProperty)DECAYABLE)).booleanValue()) {
            meta |= 1;
        }
        if (((Boolean)state.getValue((IProperty)CHECK_DECAY)).booleanValue()) {
            meta |= 2;
        }
        return meta;
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this).withProperty((IProperty)DECAYABLE, (Comparable)Boolean.valueOf((meta & 1) != 0)).withProperty((IProperty)CHECK_DECAY, (Comparable)Boolean.valueOf((meta & 2) != 0));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{DECAYABLE, CHECK_DECAY, SNOWY});
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.setTextureName((String)"srparasites:parasitesapling");
    }

    public int quantityDropped(Random random) {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    public int damageDropped(IBlockState state) {
        return 4;
    }

    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return type == EntityLiving.SpawnPlacementType.ON_GROUND;
    }

    public boolean canSilkHarvest() {
        return true;
    }

    @Override
    public void registerModels() {
        SRPMain.proxy.modelReg(Item.getItemFromBlock((Block)this), 0, "inventory");
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
}

