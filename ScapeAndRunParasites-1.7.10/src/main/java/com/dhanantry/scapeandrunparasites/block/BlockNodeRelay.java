/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumBlockRenderType
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityNodeRelay;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockNodeRelay
extends Block {
    private static final AxisAlignedBB NODE_AABB = FULL_BLOCK_AABB;

    public BlockNodeRelay(String name) {
        super(Material.iron);
        this.setHardness(3.0f);
        this.setResistance(10.0f);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setLightOpacity(0);
        this.setCreativeTab(null);
        SRPBlocks.SRP_BLOCKS.add(this);
    }

    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityNodeRelay();
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return NODE_AABB;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return NODE_AABB;
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return true;
    }

    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return 3.0f;
    }

    public float getExplosionResistance(Entity exploder) {
        return 10.0f;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        BlockPos controllerPos;
        if (world.isRemote) {
            return true;
        }
        TileEntity te = WorldAccess.getTileEntity(world, pos);
        if (te instanceof TileEntityNodeRelay && (controllerPos = ((TileEntityNodeRelay)te).getControllerPos()) != null) {
            player.openGui((Object)"srparasites", 0, world, controllerPos.getX(), controllerPos.getY(), controllerPos.getZ());
            return true;
        }
        return false;
    }

    private void tryDismantle(World world, BlockPos nodePos, @Nullable EntityPlayer player) {
        TileEntity cte;
        if (world.isRemote) {
            return;
        }
        TileEntity te = WorldAccess.getTileEntity(world, nodePos);
        if (!(te instanceof TileEntityNodeRelay)) {
            return;
        }
        BlockPos ctrlPos = ((TileEntityNodeRelay)te).getControllerPos();
        TileEntityRelayController controller = null;
        if (ctrlPos != null && WorldAccess.isBlockLoaded(world, ctrlPos) && (cte = WorldAccess.getTileEntity(world, ctrlPos)) instanceof TileEntityRelayController) {
            controller = (TileEntityRelayController)cte;
        }
        if (controller == null) {
            controller = this.findNearbyController(world, nodePos, 8);
        }
        if (controller != null) {
            controller.dismantle();
        } else if (player != null) {
            player.addChatComponentMessage((IChatComponent)new ChatComponentText("Relay controller missing or unloaded."), true);
        }
    }

    @Nullable
    private TileEntityRelayController findNearbyController(World world, BlockPos origin, int radius) {
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    TileEntity te;
                    BlockPos p = origin.add(dx, dy, dz);
                    if (!WorldAccess.isBlockLoaded(world, p) || !((te = WorldAccess.getTileEntity(world, p)) instanceof TileEntityRelayController)) continue;
                    return (TileEntityRelayController)te;
                }
            }
        }
        return null;
    }

    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        this.tryDismantle(world, pos, player);
        super.onBlockHarvested(world, pos, state, player);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        this.tryDismantle(worldIn, pos, null);
        super.breakBlock(worldIn, pos, state);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return ItemStack.EMPTY;
    }

    public int quantityDropped(Random random) {
        return 0;
    }
}

