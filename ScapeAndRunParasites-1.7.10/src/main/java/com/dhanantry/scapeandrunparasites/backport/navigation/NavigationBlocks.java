package com.dhanantry.scapeandrunparasites.backport.navigation;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;

/** Native metadata, shape and collision boundary used only by navigation. */
public final class NavigationBlocks {
    private NavigationBlocks() {}
    public static State state(IBlockAccess world, BlockPos pos) { return new State(world, pos); }
    public static boolean passable(IBlockAccess world, BlockPos pos) {
        return world.getBlock(pos.getX(),pos.getY(),pos.getZ()).getBlocksMovement(world,pos.getX(),pos.getY(),pos.getZ());
    }
    public static boolean canSeeSky(World world, BlockPos pos) { return world.canBlockSeeTheSky(pos.getX(),pos.getY(),pos.getZ()); }
    public static ChunkCache cache(World world, BlockPos pos, int range) {
        return new ChunkCache(world,pos.getX()-range,pos.getY()-range,pos.getZ()-range,pos.getX()+range,pos.getY()+range,pos.getZ()+range,0);
    }
    public static boolean collides(World world, AxisAlignedBB box) {
        List<AxisAlignedBB> boxes=new ArrayList<AxisAlignedBB>();
        for(int x=MathHelper.floor_double(box.minX);x<MathHelper.floor_double(box.maxX+1);x++)
            for(int z=MathHelper.floor_double(box.minZ);z<MathHelper.floor_double(box.maxZ+1);z++) {
                if(!world.blockExists(x,64,z)) continue;
                for(int y=MathHelper.floor_double(box.minY)-1;y<MathHelper.floor_double(box.maxY+1);y++) {
                    world.getBlock(x,y,z).addCollisionBoxesToList(world,x,y,z,box,boxes,null);
                    if(!boxes.isEmpty()) return true;
                }
            }
        return false;
    }
    /** Swept body test prevents shortcuts/diagonal edges from crossing walls or dry gaps. */
    public static boolean clearSegment(World world,net.minecraft.entity.EntityLiving entity,net.minecraft.util.Vec3 from,net.minecraft.util.Vec3 to,boolean water) {
        double half=entity.width/2.0;
        AxisAlignedBB body=AxisAlignedBB.getBoundingBox(from.xCoord-half,from.yCoord,from.zCoord-half,from.xCoord+half,from.yCoord+entity.height,from.zCoord+half);
        double dx=to.xCoord-from.xCoord,dy=to.yCoord-from.yCoord,dz=to.zCoord-from.zCoord;
        AxisAlignedBB swept=body.addCoord(dx,dy,dz);
        List<AxisAlignedBB> boxes=new ArrayList<AxisAlignedBB>();
        for(int x=MathHelper.floor_double(swept.minX);x<MathHelper.floor_double(swept.maxX+1);x++)
            for(int z=MathHelper.floor_double(swept.minZ);z<MathHelper.floor_double(swept.maxZ+1);z++) {
                if(!world.blockExists(x,64,z))return false;
                for(int y=MathHelper.floor_double(swept.minY)-1;y<MathHelper.floor_double(swept.maxY+1);y++) {
                    Block b=world.getBlock(x,y,z);
                    if(water && b.getMaterial()!=Material.water && intersectsSweep(body,AxisAlignedBB.getBoundingBox(x,y,z,x+1,y+1,z+1),dx,dy,dz))return false;
                    boxes.clear();b.addCollisionBoxesToList(world,x,y,z,swept,boxes,null);
                    for(AxisAlignedBB box:boxes)if(intersectsSweep(body,box,dx,dy,dz))return false;
                }
            }
        return true;
    }
    private static boolean intersectsSweep(AxisAlignedBB a,AxisAlignedBB b,double dx,double dy,double dz) {
        double[] lo={a.minX,a.minY,a.minZ},hi={a.maxX,a.maxY,a.maxZ},blo={b.minX,b.minY,b.minZ},bhi={b.maxX,b.maxY,b.maxZ},v={dx,dy,dz};
        double enter=0,exit=1;
        for(int i=0;i<3;i++) {
            if(Math.abs(v[i])<1E-12) { if(hi[i]<=blo[i]+1E-7||lo[i]>=bhi[i]-1E-7)return false; }
            else { double t0=(blo[i]+1E-7-hi[i])/v[i],t1=(bhi[i]-1E-7-lo[i])/v[i];enter=Math.max(enter,Math.min(t0,t1));exit=Math.min(exit,Math.max(t0,t1));if(enter>=exit)return false; }
        }
        return enter<exit;
    }
    public static PathNodeType type(IBlockAccess world,int x,int y,int z) {
        Block block=world.getBlock(x,y,z); Material material=block.getMaterial();
        if(material==Material.air) return PathNodeType.OPEN;
        if(block instanceof BlockTrapDoor || block==Blocks.waterlily) return PathNodeType.TRAPDOOR;
        if(block==Blocks.fire) return PathNodeType.DAMAGE_FIRE;
        if(block==Blocks.cactus) return PathNodeType.DAMAGE_CACTUS;
        if(block instanceof BlockDoor) {
            if(((BlockDoor)block).func_150015_f(world,x,y,z)) return PathNodeType.DOOR_OPEN;
            return material==Material.wood ? PathNodeType.DOOR_WOOD_CLOSED : PathNodeType.DOOR_IRON_CLOSED;
        }
        if(block instanceof BlockRailBase) return PathNodeType.RAIL;
        if(block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockFenceGate && (world.getBlockMetadata(x,y,z)&4)==0) return PathNodeType.FENCE;
        if(material==Material.water) return PathNodeType.WATER;
        if(material==Material.lava) return PathNodeType.LAVA;
        return block.getBlocksMovement(world,x,y,z) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
    }
    public static final class State {
        private final IBlockAccess world; private final BlockPos pos; private final Block block;
        State(IBlockAccess world,BlockPos pos) { this.world=world;this.pos=pos.toImmutable();this.block=world.getBlock(pos.getX(),pos.getY(),pos.getZ()); }
        public Block getBlock() { return block; }
        public Material getMaterial() { return block.getMaterial(); }
        public AxisAlignedBB getBoundingBox(IBlockAccess ignored,BlockPos ignoredPos) {
            // Native blocks store mutable bounds; restore them after reading this state's shape.
            double a=block.getBlockBoundsMinX(),b=block.getBlockBoundsMinY(),c=block.getBlockBoundsMinZ();
            double d=block.getBlockBoundsMaxX(),e=block.getBlockBoundsMaxY(),f=block.getBlockBoundsMaxZ();
            try {
                block.setBlockBoundsBasedOnState(world,pos.getX(),pos.getY(),pos.getZ());
                return AxisAlignedBB.getBoundingBox(block.getBlockBoundsMinX(),block.getBlockBoundsMinY(),block.getBlockBoundsMinZ(),block.getBlockBoundsMaxX(),block.getBlockBoundsMaxY(),block.getBlockBoundsMaxZ());
            } finally { block.setBlockBounds((float)a,(float)b,(float)c,(float)d,(float)e,(float)f); }
        }
        public boolean isFullBlock() {
            AxisAlignedBB b=getBoundingBox(world,pos);
            return block.renderAsNormalBlock() && b.minX==0 && b.minY==0 && b.minZ==0 && b.maxX==1 && b.maxY==1 && b.maxZ==1;
        }
        public boolean isTopSolid() { return block.isSideSolid(world,pos.getX(),pos.getY(),pos.getZ(),ForgeDirection.UP); }
    }
}
