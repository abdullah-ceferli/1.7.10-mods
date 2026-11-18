package com.dhanantry.scapeandrunparasites.backport.state;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.google.common.collect.ImmutableMap;
import java.util.*;
import net.minecraft.block.Block;import net.minecraft.block.material.Material;import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;import net.minecraft.world.*;
/** Explicit state/metadata boundaries. Vanilla raw states retain metadata without inventing properties. */
public final class BlockStates {
    private BlockStates(){}
    private static final Map<Block,IBlockState[]> legacy=new IdentityHashMap<Block,IBlockState[]>();
    public static IBlockState get(IBlockAccess access,BlockPos pos){return fromMeta(access.getBlock(pos.getX(),pos.getY(),pos.getZ()),access.getBlockMetadata(pos.getX(),pos.getY(),pos.getZ()));}
    public static IBlockState defaultState(Block block){return block instanceof StatefulBlock?((StatefulBlock)block).getDefaultState():fromMeta(block,0);}
    public static IBlockState fromMeta(Block block,int meta){
        if(meta<0||meta>15)throw new IllegalArgumentException("Legacy block metadata must be 0..15");
        if(block instanceof StatefulBlock)return ((StatefulBlock)block).getStateFromMeta(meta);
        synchronized(legacy){IBlockState[] values=legacy.get(block);if(values==null){values=new IBlockState[16];BlockStateContainer container=new BlockStateContainer(block);for(int i=0;i<16;i++)values[i]=new LegacyState(container,block,i);legacy.put(block,values);}return values[meta];}
    }
    public static int meta(IBlockState state){
        int value;
        if(state instanceof LegacyState)value=((LegacyState)state).metadata;
        else if(state.getBlock() instanceof StatefulBlock)value=((StatefulBlock)state.getBlock()).getMetaFromState(state);
        else throw new IllegalArgumentException("No legacy metadata codec for this state graph");
        if(value<0||value>15)throw new IllegalArgumentException("State cannot be stored in legacy metadata: "+value);
        return value;
    }
    public static boolean set(World world,BlockPos pos,IBlockState state,int flags){return world.setBlock(pos.getX(),pos.getY(),pos.getZ(),state.getBlock(),meta(state),flags);}
    public static boolean set(World world,BlockPos pos,IBlockState state){return set(world,pos,state,3);}
    public static int meta(Block block,IBlockState state){if(state.getBlock()!=block)throw new IllegalArgumentException("State belongs to a different block");return meta(state);}
    // Minecraft 1.12 and the legacy block-break effect both pack metadata above the low 12 block-ID bits.
    public static int stateId(IBlockState state){return Block.getIdFromBlock(state.getBlock())+(meta(state)<<12);}
    public static IBlockState stateById(int id){return fromMeta(Block.getBlockById(id&4095),(id>>12)&15);}
    static Material material(IBlockState s){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).getMaterial(s):s.getBlock().getMaterial();}
    static boolean fullBlock(IBlockState s){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).isFullBlock(s):s.getBlock().isOpaqueCube();}
    static boolean fullCube(IBlockState s){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).isFullCube(s):s.getBlock().renderAsNormalBlock();}
    static boolean opaque(IBlockState s){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).isOpaqueCube(s):s.getBlock().isOpaqueCube();}
    static boolean normalCube(IBlockState s){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).isNormalCube(s):s.getBlock().isNormalCube();}
    static boolean blockNormalCube(IBlockState s){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).isBlockNormalCube(s):s.getBlock().isBlockNormalCube();}
    static boolean providesPower(IBlockState s){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).canProvidePower(s):s.getBlock().canProvidePower();}
    static boolean canEntitySpawn(IBlockState s,Entity entity){return !(s.getBlock() instanceof StatefulBlock)||((StatefulBlock)s.getBlock()).canEntitySpawn(s,entity);}
    static int light(IBlockState s){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).getLightValue(s):s.getBlock().getLightValue();}
    static float hardness(IBlockState s,World world,BlockPos pos){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).getBlockHardness(s,world,pos):s.getBlock().getBlockHardness(world,pos.getX(),pos.getY(),pos.getZ());}
    static boolean sideSolid(IBlockState s,IBlockAccess access,BlockPos pos,EnumFacing side){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).isSideSolid(s,access,pos,side):s.getBlock().isSideSolid(access,pos.getX(),pos.getY(),pos.getZ(),net.minecraftforge.common.util.ForgeDirection.getOrientation(side.ordinal()));}
    static BlockFaceShape face(IBlockState s,IBlockAccess access,BlockPos pos,EnumFacing side){return s.getBlock() instanceof StatefulBlock?((StatefulBlock)s.getBlock()).getBlockFaceShape(access,s,pos,side):sideSolid(s,access,pos,side)?BlockFaceShape.SOLID:BlockFaceShape.UNDEFINED;}
    static AxisAlignedBB collision(IBlockState s,IBlockAccess access,BlockPos pos){if(s.getBlock() instanceof StatefulBlock)return ((StatefulBlock)s.getBlock()).getCollisionBoundingBox(s,access,pos);if(!(access instanceof World))throw new IllegalArgumentException("Legacy collision queries require a World");AxisAlignedBB box=s.getBlock().getCollisionBoundingBoxFromPool((World)access,pos.getX(),pos.getY(),pos.getZ());return box==null?null:box.getOffsetBoundingBox(-pos.getX(),-pos.getY(),-pos.getZ());}
    private static final class LegacyState extends BlockStateContainer.StateImplementation {
        @Override public IBlockState withRotation(com.dhanantry.scapeandrunparasites.backport.util.Rotation rotation){throw new UnsupportedOperationException("Vanilla block rotation requires a metadata codec");}
        @Override public IBlockState withMirror(com.dhanantry.scapeandrunparasites.backport.util.Mirror mirror){throw new UnsupportedOperationException("Vanilla block mirroring requires a metadata codec");}
        @Override public IBlockState getActualState(IBlockAccess access,BlockPos pos){throw new UnsupportedOperationException("Vanilla derived block properties require a state codec");}
        private final int metadata;
        LegacyState(BlockStateContainer container,Block block,int meta){super(container,block,ImmutableMap.<IProperty<?>,Comparable<?>>of());metadata=meta;}
    }
}
