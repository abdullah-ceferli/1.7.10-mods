package personal.truehorror.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import personal.truehorror.entity.DreadEntity;

public final class SafeSpawns {
    public static boolean loaded(World world,int x,int z){return world.getChunkProvider().chunkExists(x>>4,z>>4);}
    public static boolean loadedRectangle(World world,int minX,int minZ,int maxX,int maxZ){
        for(int x=minX>>4;x<=(maxX>>4);x++)for(int z=minZ>>4;z<=(maxZ>>4);z++)
            if(!world.getChunkProvider().chunkExists(x,z))return false;
        return true;
    }
    public static boolean loadedArea(World world,int x,int z,int radius){return loadedRectangle(world,x-radius,z-radius,x+radius,z+radius);}
    public static Vec3 window(EntityPlayerMP player,DreadEntity entity){
        World world=player.worldObj;
        net.minecraft.util.ChunkCoordinates point=Nearby.find(world,(int)Math.floor(player.posX),(int)Math.floor(player.posY),(int)Math.floor(player.posZ),8,0,2,
                (block,meta,x,y,z)->Environment.isWindow(block));
        if(point==null)return null;
        double dx=point.posX+.5-player.posX,dz=point.posZ+.5-player.posZ,length=Math.sqrt(dx*dx+dz*dz);
        if(length<.5)return null;
        int x=(int)Math.floor(point.posX+.5+dx/length*2),z=(int)Math.floor(point.posZ+.5+dz/length*2);
        if(!loadedRectangle(world,x-2,z-2,x+2,z+2))return null;
        for(int y=point.posY;y>=point.posY-3&&y>1;y--){
            if(!World.doesBlockHaveSolidTopSurface(world,x,y-1,z))continue;
            entity.setLocationAndAngles(x+.5,y,z+.5,player.rotationYaw+180,0);
            if(world.getCollidingBoundingBoxes(entity,entity.boundingBox.expand(.1,0,.1).addCoord(0,.08,0)).isEmpty()&&!world.isAnyLiquid(entity.boundingBox))
                return Vec3.createVectorHelper(x+.5,y,z+.5);
        }return null;
    }
    public static Vec3 find(EntityPlayerMP player,DreadEntity entity,int min,int max,boolean behind,boolean underground){
        World world=player.worldObj;
        for(int attempt=0;attempt<24;attempt++){
            double angle=(behind?Math.toRadians(player.rotationYaw+90)+Math.PI:world.rand.nextDouble()*Math.PI*2)
                    +(behind?(world.rand.nextDouble()-.5)*1.7:0);
            double radius=min+world.rand.nextInt(Math.max(1,max-min+1));
            int x=MathHelper.floor_double(player.posX+Math.cos(angle)*radius),z=MathHelper.floor_double(player.posZ+Math.sin(angle)*radius);
            if(!loadedRectangle(world,x-2,z-2,x+2,z+2))continue;
            int base=underground?MathHelper.floor_double(player.posY):world.getTopSolidOrLiquidBlock(x,z);
            for(int dy=0;dy<9;dy++){
                int y=underground?base+(dy%2==0?dy/2:-dy/2):base-dy;
                if(y<2||y+entity.height>254||!World.doesBlockHaveSolidTopSurface(world,x,y-1,z))continue;
                entity.setLocationAndAngles(x+.5,y,z+.5,player.rotationYaw+180,0);
                if(world.getCollidingBoundingBoxes(entity,entity.boundingBox.expand(.12,0,.12).addCoord(0,.08,0)).isEmpty()
                        &&!world.isAnyLiquid(entity.boundingBox)&&world.checkNoEntityCollision(entity.boundingBox))
                    return Vec3.createVectorHelper(x+.5,y,z+.5);
            }
        }
        return null;
    }
}
