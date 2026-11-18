package personal.truehorror.server;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import personal.truehorror.core.EventType;

public final class Environment {
    public final int flags,x,y,z,light;
    public Environment(EntityPlayerMP player,PlayerRecord record){
        World world=player.worldObj;x=MathHelper.floor_double(player.posX);y=MathHelper.floor_double(player.posY);z=MathHelper.floor_double(player.posZ);
        light=world.getBlockLightValue(x,y,z);int f=0;
        if(!world.isDaytime())f|=EventType.NIGHT;
        boolean roof=!world.canBlockSeeTheSky(x,y+1,z);
        if(roof&&(y<48||world.getHeightValue(x,z)-y>12))f|=EventType.CAVE;
        else if(roof)f|=EventType.INDOOR;
        else f|=EventType.OUTSIDE;
        if(light<8)f|=EventType.DARK;
        if(BiomeDictionary.isBiomeOfType(world.getBiomeGenForCoords(x,z),BiomeDictionary.Type.FOREST))f|=EventType.FOREST;
        if(record.miningUntil>world.getTotalWorldTime())f|=EventType.MINING;
        if(record.woke)f|=EventType.WAKE;
        net.minecraft.util.ChunkCoordinates bed=player.getBedLocation(player.dimension);
        if(bed!=null&&player.getDistanceSq(bed.posX,bed.posY,bed.posZ)<256)f|=EventType.BED;
        if((f&EventType.INDOOR)!=0){
            if(Nearby.find(world,x,y,z,8,0,2,(block,meta,xx,yy,zz)->isWindow(block))!=null)f|=EventType.WINDOW;
        }
        flags=f;
    }
    public boolean has(int flag){return(flags&flag)==flag;}
    public static boolean isWindow(Block block){return block==Blocks.glass||block==Blocks.glass_pane||block==Blocks.stained_glass||block==Blocks.stained_glass_pane;}
}
