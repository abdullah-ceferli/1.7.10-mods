package dev.industrialtechnology.hatch.client;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import dev.industrialtechnology.hatch.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/** Static chunk geometry: no tile ticking, per-frame tile rendering or custom shader. */
public final class SlidingHatchRenderer implements ISimpleBlockRenderingHandler {
    private static IIcon icon(int m){return m==1||m==2?Blocks.wool.getIcon(0,m==1?4:15):Blocks.iron_block.getIcon(0,0);}
    private static float[] color(int m){return m==0?new float[]{.22F,.24F,.25F}:m==3?new float[]{.48F,.50F,.51F}:m==4?new float[]{.30F,.32F,.33F}:new float[]{1,1,1};}
    @Override public boolean renderWorldBlock(IBlockAccess w,int x,int y,int z,Block block,int id,RenderBlocks r){boolean faces=r.renderAllFaces;IIcon override=r.overrideBlockTexture;try{r.renderAllFaces=true;for(HatchGeometry.Box b:BlockSlidingHatch.boxes(w,x,y,z)){r.setRenderBounds(b.x,b.y,b.z,b.a,b.b,b.c);r.setOverrideBlockTexture(icon(b.material));float[] color=color(b.material);r.renderStandardBlockWithColorMultiplier(block,x,y,z,color[0],color[1],color[2]);}return true;}finally{r.renderAllFaces=faces;r.overrideBlockTexture=override;r.setRenderBounds(0,0,0,1,1,1);}}
    @Override public void renderInventoryBlock(Block block,int metadata,int id,RenderBlocks r){GL11.glPushMatrix();try{GL11.glTranslatef(-.5F,-.5F,-.5F);for(HatchGeometry.Box b:HatchGeometry.boxes(false,false,true,false)){r.setRenderBounds(b.x,b.y,b.z,b.a,b.b,b.c);IIcon icon=icon(b.material);Tessellator t=Tessellator.instance;for(int face=0;face<6;face++){t.startDrawingQuads();float[] color=color(b.material);t.setColorOpaque_F(color[0],color[1],color[2]);switch(face){case 0:t.setNormal(0,-1,0);r.renderFaceYNeg(block,0,0,0,icon);break;case 1:t.setNormal(0,1,0);r.renderFaceYPos(block,0,0,0,icon);break;case 2:t.setNormal(0,0,-1);r.renderFaceZNeg(block,0,0,0,icon);break;case 3:t.setNormal(0,0,1);r.renderFaceZPos(block,0,0,0,icon);break;case 4:t.setNormal(-1,0,0);r.renderFaceXNeg(block,0,0,0,icon);break;default:t.setNormal(1,0,0);r.renderFaceXPos(block,0,0,0,icon);}t.draw();}}}finally{r.setRenderBounds(0,0,0,1,1,1);GL11.glPopMatrix();}}
    @Override public boolean shouldRender3DInInventory(int id){return true;}
    @Override public int getRenderId(){return BlockSlidingHatch.renderId;}
}
