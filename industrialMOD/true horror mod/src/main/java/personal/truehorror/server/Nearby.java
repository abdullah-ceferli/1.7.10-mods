package personal.truehorror.server;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

/** Event-only, bounded nearest-first searches. No scans run every render or game tick. */
public final class Nearby {
    public interface Match {boolean accepts(Block block,int meta,int x,int y,int z);}
    public static ChunkCoordinates find(World world,int x,int y,int z,int radius,int below,int above,Match match){
        int budget=2048;
        for(int r=0;r<=radius;r++)for(int dx=-r;dx<=r;dx++)for(int dz=-r;dz<=r;dz++){
            if(Math.max(Math.abs(dx),Math.abs(dz))!=r||!SafeSpawns.loaded(world,x+dx,z+dz))continue;
            for(int dy=below;dy<=above;dy++){
                if(--budget<0)return null;
                int yy=y+dy;if(yy<1||yy>253)continue;
                if(match.accepts(world.getBlock(x+dx,yy,z+dz),world.getBlockMetadata(x+dx,yy,z+dz),x+dx,yy,z+dz))return new ChunkCoordinates(x+dx,yy,z+dz);
            }
        }return null;
    }
}
