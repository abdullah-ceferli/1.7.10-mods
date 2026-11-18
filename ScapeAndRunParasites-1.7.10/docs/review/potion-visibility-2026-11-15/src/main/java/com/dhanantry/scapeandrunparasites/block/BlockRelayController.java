/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.block.Block
 *  net.minecraft.block.Block$EnumOffsetType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumBlockRenderType
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Plane
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityNodeRelay;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyDirection;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRelayController
extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create((String)"facing", (Predicate)EnumFacing.Plane.HORIZONTAL);

    public BlockRelayController(String name) {
        super(Material.iron);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setHardness(3.0f);
        this.setResistance(10.0f);
        this.setLightOpacity(0);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH));
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        SRPBlocks.SRP_BLOCKS.add(this);
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{FACING});
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)EnumFacing.byHorizontalIndex((int)(meta & 3)));
    }

    @SideOnly(value=Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue((IProperty)FACING)).getHorizontalIndex();
    }

    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return BlockStates.defaultState(this).withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH);
    }

    private static void spawnBreakSmoke(World world, BlockPos pos) {
        if (world.isRemote) {
            return;
        }
        if (!(world instanceof WorldServer)) {
            return;
        }
        WorldServer ws = (WorldServer)world;
        Random r = world.rand;
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                int puffs = dx == 0 && dz == 0 ? 10 : 6;
                for (int i = 0; i < puffs; ++i) {
                    double x = (double)pos.getX() + 0.5 + (double)dx + (r.nextDouble() - 0.5) * 0.9;
                    double y = (double)pos.getY() + 0.05 + r.nextDouble() * 0.3;
                    double z = (double)pos.getZ() + 0.5 + (double)dz + (r.nextDouble() - 0.5) * 0.9;
                    double vx = (r.nextDouble() - 0.5) * 0.04;
                    double vy = 0.08 + r.nextDouble() * 0.06;
                    double vz = (r.nextDouble() - 0.5) * 0.04;
                    ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 1, vx, vy, vz, 0.0, new int[0]);
                }
            }
        }
        ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, 10, 0.35, 0.45, 0.35, 0.0, new int[0]);
        ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.6, (double)pos.getZ() + 0.5, 6, 0.25, 0.35, 0.25, 0.0, new int[0]);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        BlockRelayController.spawnBreakSmoke(world, pos);
        super.breakBlock(world, pos, state);
    }

    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityRelayController();
    }

    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.NONE;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        BlockPos controllerPos = pos;
        player.openGui((Object)"srparasites", 0, world, controllerPos.getX(), controllerPos.getY(), controllerPos.getZ());
        return true;
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote) {
            return;
        }
        TileEntity te = WorldAccess.getTileEntity(world, pos);
        if (!(te instanceof TileEntityRelayController)) {
            return;
        }
        TileEntityRelayController ctrl = (TileEntityRelayController)te;
        EnumFacing facing = (EnumFacing)state.getValue((IProperty)FACING);
        List<BlockPos> nodes = this.computeNodePositions(pos, facing);
        ArrayList<BlockPos> toClear = new ArrayList<BlockPos>();
        for (BlockPos p : nodes) {
            if (WorldAccess.isAirBlock(world, p)) continue;
            IBlockState s = BlockStates.get(world, p);
            Block b = s.getBlock();
            if (b.isReplaceable((IBlockAccess)world, p) || s.getMaterial().isReplaceable()) {
                toClear.add(p);
                continue;
            }
            if (placer instanceof EntityPlayer) {
                ((EntityPlayer)placer).addChatComponentMessage((IChatComponent)new ChatComponentTranslation("block.srparasites.relay_controller.no_space", new Object[0]), true);
            }
            WorldAccess.destroyBlock(world, pos, true);
            return;
        }
        for (BlockPos p : toClear) {
            WorldAccess.setBlockToAir(world, p);
        }
        for (BlockPos p : nodes) {
            BlockStates.set(world, p, BlockStates.defaultState(SRPBlocks.NODE_RELAY), 2);
            TileEntity nte = WorldAccess.getTileEntity(world, p);
            if (!(nte instanceof TileEntityNodeRelay)) continue;
            ((TileEntityNodeRelay)nte).setControllerPos(pos);
        }
        ctrl.setChildPositions(nodes);
        ctrl.setFormed(true);
        ctrl.markDirty();
    }

    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        TileEntity te;
        if (!world.isRemote && (te = WorldAccess.getTileEntity(world, pos)) instanceof TileEntityRelayController) {
            ((TileEntityRelayController)te).dismantle();
        }
        super.onBlockHarvested(world, pos, state, player);
    }

    private List<BlockPos> computeNodePositions(BlockPos origin, EnumFacing facingIgnored) {
        int y;
        ArrayList<BlockPos> out = new ArrayList<BlockPos>();
        ArrayList<BlockPos> rel = new ArrayList<BlockPos>();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if (dx == 0 && dz == 0) continue;
                rel.add(new BlockPos(dx, 0, dz));
            }
        }
        for (y = 1; y <= 3; ++y) {
            for (int dx = -1; dx <= 1; ++dx) {
                for (int dz = -1; dz <= 1; ++dz) {
                    rel.add(new BlockPos(dx, y, dz));
                }
            }
        }
        for (y = 4; y <= 6; ++y) {
            rel.add(new BlockPos(0, y, 0));
        }
        for (BlockPos r : rel) {
            out.add(origin.add(r.getX(), r.getY(), r.getZ()));
        }
        return out;
    }
}

