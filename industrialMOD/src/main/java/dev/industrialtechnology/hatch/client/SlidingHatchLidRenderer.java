package dev.industrialtechnology.hatch.client;

import dev.industrialtechnology.hatch.*;
import java.util.*;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/** One small native-atlas draw for the visible cover. No custom shaders or idle ticks. */
public final class SlidingHatchLidRenderer extends TileEntitySpecialRenderer {
    @Override public void renderTileEntityAt(TileEntity raw,double rx,double ry,double rz,float partial){
        TileSlidingHatch tile=(TileSlidingHatch)raw;if(tile.getWorldObj()==null)return;
        boolean anchor=tile.xCoord==tile.baseX&&tile.zCoord==tile.baseZ;
        if(!anchor&&BlockSlidingHatch.loaded(tile.getWorldObj(),tile.baseX,tile.baseY,tile.baseZ))return;
        float progress=tile.progress(partial);List<HatchGeometry.Box> boxes=HatchGeometry.lid(tile.offsetX,tile.offsetZ,tile.upper,progress);if(boxes.isEmpty())return;
        GL11.glPushMatrix();GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_LIGHTING_BIT);
        try{GL11.glTranslated(rx,ry,rz);GL11.glDisable(GL11.GL_LIGHTING);bindTexture(TextureMap.locationBlocksTexture);Tessellator t=Tessellator.instance;t.startDrawingQuads();
            int light=tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord,tile.yCoord+1,tile.zCoord,0);t.setBrightness(light);
            int index=0;for(HatchGeometry.Box original:boxes){HatchGeometry.Box b=anchor?original:original.clip(tile.xCoord-tile.baseX,tile.zCoord-tile.baseZ);if(b!=null){double ox=anchor?0:tile.xCoord-tile.baseX,oz=anchor?0:tile.zCoord-tile.baseZ;draw(t,b,(index==0?1:-1)*.4375*progress+ox,oz,tile.offsetX?.5:0,tile.offsetZ?.5:0);}index++;}t.draw();
        }finally{GL11.glPopAttrib();GL11.glPopMatrix();}
    }
    private static void draw(Tessellator t,HatchGeometry.Box b,double shift,double oz,double startX,double startZ){
        double x=b.x,a=b.a,y=b.y,h=b.b,z=b.z,c=b.c;
        face(t,.5F,0,-1,0,new double[][]{{x,y,c},{x,y,z},{a,y,z},{a,y,c}},shift,oz,startX,startZ,0);
        face(t,1,0,1,0,new double[][]{{a,h,c},{a,h,z},{x,h,z},{x,h,c}},shift,oz,startX,startZ,0);
        face(t,.8F,0,0,-1,new double[][]{{a,h,z},{a,y,z},{x,y,z},{x,h,z}},shift,oz,startX,startZ,1);
        face(t,.8F,0,0,1,new double[][]{{x,h,c},{x,y,c},{a,y,c},{a,h,c}},shift,oz,startX,startZ,1);
        face(t,.6F,-1,0,0,new double[][]{{x,h,z},{x,y,z},{x,y,c},{x,h,c}},shift,oz,startX,startZ,2);
        face(t,.6F,1,0,0,new double[][]{{a,h,c},{a,y,c},{a,y,z},{a,h,z}},shift,oz,startX,startZ,2);
    }
    private static void face(Tessellator t,float shade,float nx,float ny,float nz,double[][] vertices,double shift,double oz,double startX,double startZ,int axis){
        IIcon icon=Blocks.iron_block.getIcon(0,0);t.setNormal(nx,ny,nz);t.setColorOpaque_F(.48F*shade,.50F*shade,.51F*shade);
        for(double[] v:vertices){double u=axis==2?v[2]+oz-startZ:v[0]+shift-startX;double vv=axis==0?v[2]+oz-startZ:1-v[1];t.addVertexWithUV(v[0],v[1],v[2],icon.getInterpolatedU(u*16),icon.getInterpolatedV(vv*16));}
    }
}
