package com.dhanantry.scapeandrunparasites.backport.state;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.util.*;
import java.util.Random;
import net.minecraft.block.Block;import net.minecraft.block.material.Material;import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;import net.minecraft.util.ResourceLocation;import net.minecraft.world.*;
/** State storage and legacy hooks for modern-style blocks backed by real 1.7 block metadata. */
public class StatefulBlock extends Block {
    public static final AxisAlignedBB FULL_BLOCK_AABB=AxisAlignedBB.getBoundingBox(0,0,0,1,1,1);
    public static final AxisAlignedBB NULL_AABB=null;
    protected final BlockStateContainer blockState;
    private IBlockState defaultState;
    private ResourceLocation registryName;
    private final boolean fullBlock;
    public StatefulBlock(Material material){super(material);blockState=createBlockState();defaultState=blockState.getBaseState();fullBlock=isOpaqueCube(defaultState);}
    protected BlockStateContainer createBlockState(){return new BlockStateContainer(this);}
    public BlockStateContainer getBlockState(){return blockState;}
    public IBlockState getDefaultState(){return defaultState;}
    protected final void setDefaultState(IBlockState state){if(state.getBlock()!=this)throw new IllegalArgumentException("Default state belongs to another block");defaultState=state;}
    public IBlockState getStateFromMeta(int meta){return getDefaultState();}
    public int getMetaFromState(IBlockState state){return 0;}
    public StatefulBlock setRegistryName(String name){registryName=name.indexOf(':')>=0?new ResourceLocation(name):new ResourceLocation("srparasites",name);return this;}
    public StatefulBlock setRegistryName(ResourceLocation name){registryName=name;return this;}
    public ResourceLocation getRegistryName(){return registryName;}
    public com.dhanantry.scapeandrunparasites.backport.sound.SoundType getSoundType(IBlockState state,World world,BlockPos pos,Entity entity){return com.dhanantry.scapeandrunparasites.backport.sound.SoundType.of(this);}
    public Material getMaterial(IBlockState state){return super.getMaterial();}
    public boolean isFullBlock(IBlockState state){return fullBlock;}
    public boolean isFullCube(IBlockState state){return true;}
    public boolean isOpaqueCube(IBlockState state){return super.isOpaqueCube();}
    public boolean isBlockNormalCube(IBlockState state){return getMaterial(state).blocksMovement()&&isFullCube(state);}
    public boolean isNormalCube(IBlockState state){return getMaterial(state).isOpaque()&&isFullCube(state)&&!canProvidePower(state);}
    public boolean canProvidePower(IBlockState state){return super.canProvidePower();}
    public boolean canEntitySpawn(IBlockState state,Entity entity){return true;}
    public int getLightValue(IBlockState state){return super.getLightValue();}
    public float getBlockHardness(IBlockState state,World world,BlockPos pos){return super.getBlockHardness(world,pos.getX(),pos.getY(),pos.getZ());}
    public BlockFaceShape getBlockFaceShape(IBlockAccess access,IBlockState state,BlockPos pos,EnumFacing side){return BlockFaceShape.SOLID;}
    public boolean isSideSolid(IBlockState state,IBlockAccess access,BlockPos pos,EnumFacing side){return getBlockFaceShape(access,state,pos,side)==BlockFaceShape.SOLID;}
    public IBlockState getActualState(IBlockState state,IBlockAccess access,BlockPos pos){return state;}
    public IBlockState withRotation(IBlockState state,Rotation rotation){return state;}
    public IBlockState withMirror(IBlockState state,Mirror mirror){return state;}
    public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess access,BlockPos pos){return AxisAlignedBB.getBoundingBox(getBlockBoundsMinX(),getBlockBoundsMinY(),getBlockBoundsMinZ(),getBlockBoundsMaxX(),getBlockBoundsMaxY(),getBlockBoundsMaxZ());}
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state,IBlockAccess access,BlockPos pos){return getBoundingBox(state,access,pos);}
    public void onBlockAdded(World world,BlockPos pos,IBlockState state){super.onBlockAdded(world,pos.getX(),pos.getY(),pos.getZ());}
    public void updateTick(World world,BlockPos pos,IBlockState state,Random random){super.updateTick(world,pos.getX(),pos.getY(),pos.getZ(),random);}
    public void breakBlock(World world,BlockPos pos,IBlockState state){super.breakBlock(world,pos.getX(),pos.getY(),pos.getZ(),this,BlockStates.meta(state));}
    public boolean removedByPlayer(IBlockState state,World world,BlockPos pos,EntityPlayer player,boolean harvest){return super.removedByPlayer(world,player,pos.getX(),pos.getY(),pos.getZ(),harvest);}
    public boolean hasTileEntity(IBlockState state){return super.hasTileEntity(BlockStates.meta(state));}
    public TileEntity createTileEntity(World world,IBlockState state){return super.createTileEntity(world,BlockStates.meta(state));}
    public int damageDropped(IBlockState state){return super.damageDropped(BlockStates.meta(state));}
    public net.minecraft.item.Item getItemDropped(IBlockState state,Random random,int fortune){return super.getItemDropped(BlockStates.meta(state),random,fortune);}
    public void getDrops(NonNullList<net.minecraft.item.ItemStack> drops,IBlockAccess access,BlockPos pos,IBlockState state,int fortune){
        if(!(access instanceof World))throw new IllegalArgumentException("Legacy drop queries require a World");
        drops.addAll(super.getDrops((World)access,pos.getX(),pos.getY(),pos.getZ(),BlockStates.meta(state),fortune));
    }
    public boolean canPlaceBlockAt(World world,BlockPos pos){return super.canPlaceBlockAt(world,pos.getX(),pos.getY(),pos.getZ());}
    public boolean isReplaceable(IBlockAccess access,BlockPos pos){return super.isReplaceable(access,pos.getX(),pos.getY(),pos.getZ());}
    public void onEntityWalk(World world,BlockPos pos,Entity entity){super.onEntityWalking(world,pos.getX(),pos.getY(),pos.getZ(),entity);}
    public void onEntityCollision(World world,BlockPos pos,IBlockState state,Entity entity){super.onEntityCollidedWithBlock(world,pos.getX(),pos.getY(),pos.getZ(),entity);}
    public void onBlockHarvested(World world,BlockPos pos,IBlockState state,EntityPlayer player){super.onBlockHarvested(world,pos.getX(),pos.getY(),pos.getZ(),BlockStates.meta(state),player);}
    public void getSubBlocks(net.minecraft.creativetab.CreativeTabs tab,NonNullList<net.minecraft.item.ItemStack> items){super.getSubBlocks(net.minecraft.item.Item.getItemFromBlock(this),tab,items);}
    public net.minecraft.item.ItemStack getPickBlock(IBlockState state,com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult hit,World world,BlockPos pos,EntityPlayer player){return new net.minecraft.item.ItemStack(this,1,damageDropped(state));}
    @Override public int damageDropped(int meta){return damageDropped(getStateFromMeta(meta));}
    @Override public net.minecraft.item.Item getItemDropped(int meta,Random random,int fortune){return getItemDropped(getStateFromMeta(meta),random,fortune);}
    @Override public java.util.ArrayList<net.minecraft.item.ItemStack> getDrops(World world,int x,int y,int z,int meta,int fortune){NonNullList<net.minecraft.item.ItemStack> drops=NonNullList.create();getDrops(drops,world,new BlockPos(x,y,z),getStateFromMeta(meta),fortune);return new java.util.ArrayList<net.minecraft.item.ItemStack>(drops);}
    @Override public boolean canPlaceBlockAt(World world,int x,int y,int z){return canPlaceBlockAt(world,new BlockPos(x,y,z));}
    @Override public boolean isReplaceable(IBlockAccess access,int x,int y,int z){return isReplaceable(access,new BlockPos(x,y,z));}
    @Override public void onEntityWalking(World world,int x,int y,int z,Entity entity){onEntityWalk(world,new BlockPos(x,y,z),entity);}
    @Override public void onEntityCollidedWithBlock(World world,int x,int y,int z,Entity entity){BlockPos pos=new BlockPos(x,y,z);onEntityCollision(world,pos,BlockStates.get(world,pos),entity);}
    @Override public void onBlockHarvested(World world,int x,int y,int z,int meta,EntityPlayer player){onBlockHarvested(world,new BlockPos(x,y,z),getStateFromMeta(meta),player);}
    @Override @SuppressWarnings({"unchecked","rawtypes"}) public void getSubBlocks(net.minecraft.item.Item item,net.minecraft.creativetab.CreativeTabs tab,java.util.List items){NonNullList<net.minecraft.item.ItemStack> values=NonNullList.create();getSubBlocks(tab,values);items.addAll(values);}
    @Override public net.minecraft.item.ItemStack getPickBlock(net.minecraft.util.MovingObjectPosition hit,World world,int x,int y,int z,EntityPlayer player){BlockPos pos=new BlockPos(x,y,z);return getPickBlock(BlockStates.get(world,pos),com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult.fromLegacy(hit),world,pos,player);}
    @Override public boolean isOpaqueCube(){return defaultState==null?super.isOpaqueCube():isOpaqueCube(defaultState);}
    @Override public boolean isBlockNormalCube(){return defaultState==null?super.isBlockNormalCube():isBlockNormalCube(defaultState);}
    @Override public boolean isNormalCube(){return defaultState==null?super.isNormalCube():isNormalCube(defaultState);}
    @Override public boolean isNormalCube(IBlockAccess access,int x,int y,int z){return isNormalCube(BlockStates.get(access,new BlockPos(x,y,z)));}
    @Override public int getLightValue(IBlockAccess access,int x,int y,int z){return getLightValue(BlockStates.get(access,new BlockPos(x,y,z)));}
    @Override public boolean isSideSolid(IBlockAccess access,int x,int y,int z,net.minecraftforge.common.util.ForgeDirection side){return side!=net.minecraftforge.common.util.ForgeDirection.UNKNOWN&&isSideSolid(BlockStates.get(access,new BlockPos(x,y,z)),access,new BlockPos(x,y,z),EnumFacing.getFront(side.ordinal()));}
    @Override public AxisAlignedBB getCollisionBoundingBoxFromPool(World world,int x,int y,int z){BlockPos pos=new BlockPos(x,y,z);AxisAlignedBB box=getCollisionBoundingBox(BlockStates.get(world,pos),world,pos);return box==null?null:box.getOffsetBoundingBox(x,y,z);}
    @Override public void onBlockAdded(World world,int x,int y,int z){BlockPos pos=new BlockPos(x,y,z);onBlockAdded(world,pos,BlockStates.get(world,pos));}
    @Override public void updateTick(World world,int x,int y,int z,Random random){BlockPos pos=new BlockPos(x,y,z);updateTick(world,pos,BlockStates.get(world,pos),random);}
    @Override public void breakBlock(World world,int x,int y,int z,Block old,int meta){breakBlock(world,new BlockPos(x,y,z),getStateFromMeta(meta));}
    @Override public boolean removedByPlayer(World world,EntityPlayer player,int x,int y,int z,boolean harvest){BlockPos pos=new BlockPos(x,y,z);return removedByPlayer(BlockStates.get(world,pos),world,pos,player,harvest);}
    @Override public boolean hasTileEntity(int meta){return defaultState==null?super.hasTileEntity(meta):hasTileEntity(getStateFromMeta(meta));}
    @Override public TileEntity createTileEntity(World world,int meta){return createTileEntity(world,getStateFromMeta(meta));}
}
