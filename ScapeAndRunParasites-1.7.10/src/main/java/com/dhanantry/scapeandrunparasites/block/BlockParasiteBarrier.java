/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.EnumPushReaction
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumBlockRenderType
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.Style
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.TileEntityParasiteBarrier;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockParasiteBarrier
extends BlockBase {
    public BlockParasiteBarrier() {
        super(Material.BARRIER, "parasite_barrier", -1.0f, true, true, 6000000.0f);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0f);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.STONE);
        this.setLightOpacity(255);
    }

    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityParasiteBarrier();
    }

    public void onBlockAdded(World w, BlockPos pos, IBlockState state) {
        TileEntity te;
        super.onBlockAdded(w, pos, state);
        if (!w.isRemote && (te = WorldAccess.getTileEntity(w, pos)) instanceof TileEntityParasiteBarrier) {
            TileEntityParasiteBarrier b = (TileEntityParasiteBarrier)te;
            b.initCenterFromPos(pos);
            b.scrubMobsAroundField();
        }
    }

    public void breakBlock(World w, BlockPos pos, IBlockState state) {
        super.breakBlock(w, pos, state);
        WorldAccess.removeTileEntity(w, pos);
    }

    public boolean onBlockActivated(World w, BlockPos pos, IBlockState st, EntityPlayer pl, EnumHand hand, EnumFacing f, float hitX, float hitY, float hitZ) {
        if (w.isRemote) {
            return true;
        }
        if (!BlockParasiteBarrier.canConfigure(pl)) {
            if (pl instanceof EntityPlayerMP) {
                ChatComponentTranslation msg = new ChatComponentTranslation("msg.srparasites.barrier.no_permission", new Object[0]);
                msg.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED));
                ((EntityPlayerMP)pl).addChatMessage((IChatComponent)msg);
            }
            return true;
        }
        TileEntity te = WorldAccess.getTileEntity(w, pos);
        if (te instanceof TileEntityParasiteBarrier) {
            TileEntityParasiteBarrier b = (TileEntityParasiteBarrier)te;
            int r = b.getRadiusChunks();
            r = r % 10 + 1;
            b.setRadiusChunks(r);
            if (pl instanceof EntityPlayerMP) {
                ChatComponentTranslation msg = new ChatComponentTranslation("msg.srparasites.barrier.radius_set", new Object[]{r});
                msg.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN));
                ((EntityPlayerMP)pl).addChatMessage((IChatComponent)msg);
            }
        }
        return true;
    }

    private static boolean canConfigure(EntityPlayer pl) {
        if (pl.capabilities.isCreativeMode) {
            return true;
        }
        if (pl instanceof EntityPlayerMP) {
            return ((EntityPlayerMP)pl).canCommandSenderUseCommand(2, "srpbarrier");
        }
        return false;
    }

    public boolean isOpaqueCube(IBlockState s) {
        return true;
    }

    public boolean renderAsNormalBlock(IBlockState s) {
        return true;
    }

    public boolean isNormalCube(IBlockState s, IBlockAccess w, BlockPos p) {
        return true;
    }

    public boolean isTopSolid(IBlockState s) {
        return true;
    }

    public boolean isPassable(IBlockAccess w, BlockPos p) {
        return false;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState s, IBlockAccess w, BlockPos p) {
        return FULL_BLOCK_AABB;
    }

    public EnumBlockRenderType getRenderType(IBlockState s) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState adj = BlockStates.get(world, pos.offset(side));
        return adj.getBlock() != this || super.shouldSideBeRendered(state, world, pos, side);
    }

    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        return -1.0f;
    }

    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    public boolean canDropFromExplosion(Explosion explosionIn) {
        return false;
    }

    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
    }

    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    public static class ItemBlockParasiteBarrier
    extends ItemBlock {
        public ItemBlockParasiteBarrier(Block block) {
            super(block);
        }

        @SideOnly(value=Side.CLIENT)
        public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
            tooltip.add(EnumChatFormatting.RED + I18n.format((String)"tooltip.srparasites.parasite_barrier.creative_only", (Object[])new Object[0]));
            tooltip.add(EnumChatFormatting.WHITE + I18n.format((String)"tooltip.srparasites.parasite_barrier.desc1", (Object[])new Object[0]));
            tooltip.add(EnumChatFormatting.WHITE + I18n.format((String)"tooltip.srparasites.parasite_barrier.desc2", (Object[])new Object[0]));
        }
    }
}

