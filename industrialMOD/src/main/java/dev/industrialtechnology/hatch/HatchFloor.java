package dev.industrialtechnology.hatch;

import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.util.AxisAlignedBB;

/** Only inert full floor blocks can be retained; inventories and machines stay intact. */
public final class HatchFloor {
    public static boolean accepts(World w,int x,int y,int z,Block b,int meta){
        if(b==Blocks.air)return true;
        if(b==HatchRegistry.block||b.hasTileEntity(meta)||!b.isOpaqueCube()||b.getTickRandomly()||b.canProvidePower()||b instanceof BlockFalling||b==Blocks.tnt||b.getBlockHardness(w,x,y,z)<0)return false;
        AxisAlignedBB a=b.getCollisionBoundingBoxFromPool(w,x,y,z);
        return a!=null&&a.minX==x&&a.minY==y&&a.minZ==z&&a.maxX==x+1&&a.maxY==y+1&&a.maxZ==z+1;
    }
    private HatchFloor(){}
}
