/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.pathfinding;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class PathWorldListener
implements IWorldEventListener {
    private final List<PathNavigate> navigations = Lists.newArrayList();

    @Override
    public void notifyBlockUpdate(World world, BlockPos blockPos, IBlockState iBlockState, IBlockState iBlockState2, int n) {
        if (!this.didBlockChange(world, blockPos, iBlockState, iBlockState2)) {
            return;
        }
        int n2 = this.navigations.size();
        for (int i = 0; i < n2; ++i) {
            int n3;
            Path path;
            PathNavigate pathNavigate = this.navigations.get(i);
            if (pathNavigate == null || pathNavigate.canUpdatePathOnTimeout() || (path = pathNavigate.getPath()) == null || path.isFinished() || path.getCurrentPathLength() == 0) continue;
            PathPoint pathPoint = pathNavigate.currentPath.getFinalPathPoint();
            double d = blockPos.distanceSq(((double)pathPoint.x + pathNavigate.entity.posX) / 2.0, ((double)pathPoint.y + pathNavigate.entity.posY) / 2.0, ((double)pathPoint.z + pathNavigate.entity.posZ) / 2.0);
            if (!(d < (double)(n3 = (path.getCurrentPathLength() - path.getCurrentPathIndex()) * (path.getCurrentPathLength() - path.getCurrentPathIndex())))) continue;
            pathNavigate.updatePath();
        }
    }

    protected boolean didBlockChange(World world, BlockPos blockPos, IBlockState iBlockState, IBlockState iBlockState2) {
        AxisAlignedBB axisAlignedBB;
        AxisAlignedBB axisAlignedBB2 = iBlockState.getCollisionBoundingBox(world, blockPos);
        return axisAlignedBB2 != (axisAlignedBB = iBlockState2.getCollisionBoundingBox(world, blockPos)) && (axisAlignedBB2 == null || !axisAlignedBB2.equals(axisAlignedBB));
    }

    @Override
    public void notifyLightSet(BlockPos blockPos) {
    }

    @Override
    public void markBlockRangeForRenderUpdate(int n, int n2, int n3, int n4, int n5, int n6) {
    }

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer entityPlayer, SoundEvent soundEvent, SoundCategory soundCategory, double d, double d2, double d3, float f, float f2) {
    }

    @Override
    public void spawnParticle(int n, boolean bl, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
    }

    @Override
    public void spawnParticle(int n, boolean bl, boolean bl2, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
    }

    @Override
    public void onEntityAdded(Entity entity) {
        if (entity instanceof EntityLiving) {
            this.navigations.add(((EntityLiving)entity).getNavigator());
        }
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        if (entity instanceof EntityLiving) {
            this.navigations.remove(((EntityLiving)entity).getNavigator());
        }
    }

    @Override
    public void playRecord(SoundEvent soundEvent, BlockPos blockPos) {
    }

    @Override
    public void broadcastSound(int n, BlockPos blockPos, int n2) {
    }

    @Override
    public void playEvent(EntityPlayer entityPlayer, int n, BlockPos blockPos, int n2) {
    }

    @Override
    public void sendBlockBreakProgress(int n, BlockPos blockPos, int n2) {
    }
}

