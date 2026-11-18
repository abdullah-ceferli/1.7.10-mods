package com.dhanantry.scapeandrunparasites.backport.state;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.util.*;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.*;
public interface IBlockState {
    Block getBlock();
    Collection<IProperty<?>> getPropertyKeys();
    <T extends Comparable<T>> T getValue(IProperty<T> property);
    // Comparable preserves the original erased API and accepts recovered CFR casts; values are checked at runtime.
    IBlockState withProperty(IProperty<?> property,Comparable<?> value);
    <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property);
    ImmutableMap<IProperty<?>,Comparable<?>> getProperties();
    Material getMaterial();
    boolean isFullBlock();boolean isFullCube();boolean isOpaqueCube();boolean isNormalCube();boolean isBlockNormalCube();
    boolean canProvidePower();boolean canEntitySpawn(Entity entity);int getLightValue();
    float getBlockHardness(World world,BlockPos pos);
    boolean isSideSolid(IBlockAccess access,BlockPos pos,EnumFacing side);
    BlockFaceShape getBlockFaceShape(IBlockAccess access,BlockPos pos,EnumFacing side);
    AxisAlignedBB getCollisionBoundingBox(IBlockAccess access,BlockPos pos);
    IBlockState getActualState(IBlockAccess access,BlockPos pos);
    IBlockState withRotation(Rotation rotation);IBlockState withMirror(Mirror mirror);
}
