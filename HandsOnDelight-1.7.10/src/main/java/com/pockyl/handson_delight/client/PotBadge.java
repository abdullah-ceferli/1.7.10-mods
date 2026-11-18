package com.pockyl.handson_delight.client;

import com.pockyl.handson_delight.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vectorwing.farmersdelight.legacy.*;

/** PotBehavior's cut-corner plate, pixel stars and progress/flame glyphs. */
final class PotBadge {
    private static final String[] STAR={"..#..",".###.","#####",".###.",".#.#."},FLAME={"..#..",".##.#","#####","#####",".###."};
    private static final RenderItem ITEMS=new RenderItem();
    static void draw(StationScreen screen){
        CookingPotTile pot=(CookingPotTile)screen.tile;net.minecraft.item.ItemStack[] inputs=new net.minecraft.item.ItemStack[6];for(int i=0;i<6;i++)inputs[i]=pot.getStackInSlot(i);CookingRecipes.Recipe recipe=CookingRecipes.find(inputs);ItemStack dish=pot.getStackInSlot(6);if(Stacks.empty(dish)&&recipe!=null)dish=recipe.output;if(Stacks.empty(dish))return;
        Minecraft mc=Minecraft.getMinecraft();StationState state=StationHooks.state(pot);
        RenderState saved=RenderState.capture();screen.scene.begin(screen.width,screen.height);
        try{
            screen.scene.node("badge");GL11.glScalef(-.125F,.125F,-.125F);GL11.glRotatef(90,1,0,0);
            GL11.glDisable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_CULL_FACE);GL11.glDisable(GL11.GL_LIGHTING);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
            rect(-10,-16,20,28,0x8c1e140c);rect(-9,-17,18,1,0x8c1e140c);rect(-9,12,18,1,0x8c1e140c);
            GL11.glEnable(GL11.GL_TEXTURE_2D);mc.getTextureManager().bindTexture(dish.getItemSpriteNumber()==0?net.minecraft.client.renderer.texture.TextureMap.locationBlocksTexture:net.minecraft.client.renderer.texture.TextureMap.locationItemsTexture);
            net.minecraft.util.IIcon icon=dish.getIconIndex();net.minecraft.client.renderer.Tessellator tess=net.minecraft.client.renderer.Tessellator.instance;tess.startDrawingQuads();tess.addVertexWithUV(-8,-8,0,icon.getMinU(),icon.getMinV());tess.addVertexWithUV(8,-8,0,icon.getMaxU(),icon.getMinV());tess.addVertexWithUV(8,8,0,icon.getMaxU(),icon.getMaxV());tess.addVertexWithUV(-8,8,0,icon.getMinU(),icon.getMaxV());tess.draw();
            boolean running=Stacks.empty(pot.getStackInSlot(6));for(int i=0;i<6;i++)if(!Stacks.empty(pot.getStackInSlot(i)))running=true;
            if(running){int made=ClientProxy.practice.getInteger(StationHooks.key(dish)),open=made>=32?3:made>=8?2:1,rows=Math.min(open*5,Math.round(state.stars*5));
                for(int star=0;star<open;star++){float x=-(open*6-1)/2F+star*6;int top=open==3&&star==1?-15:-14;glyph(STAR,x,top,0x50fff2dc,5);glyph(STAR,x,top,0xffffcb6b,Math.max(0,Math.min(5,rows-star*5)));}
                if(Heat.heated(pot.getWorldObj(),pot.xCoord,pot.yCoord,pot.zCoord,false)){rect(-7,10,14,1,0x50fff2dc);rect(-7,10,Math.round(14F*pot.progress/Math.max(1,pot.total)),1,0xffffcb6b);}else glyph(FLAME,-2,8,0x50fff2dc,5);
            }
        }finally{screen.scene.end();saved.close();}
    }
    private static void glyph(String[] glyph,float x,float y,int color,int rows){for(int r=glyph.length-rows;r<glyph.length;r++)for(int c=0;c<glyph[r].length();c++)if(glyph[r].charAt(c)=='#')rect(x+c,y+r,1,1,color);}
    private static void rect(float x,float y,float w,float h,int argb){GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glColor4f((argb>>16&255)/255F,(argb>>8&255)/255F,(argb&255)/255F,(argb>>>24)/255F);GL11.glBegin(GL11.GL_QUADS);GL11.glVertex3f(x,y,.1F);GL11.glVertex3f(x+w,y,.1F);GL11.glVertex3f(x+w,y+h,.1F);GL11.glVertex3f(x,y+h,.1F);GL11.glEnd();GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glColor4f(1,1,1,1);}
}
