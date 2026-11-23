/* Building Stick radial GUI integration: LGPL-3.0-or-later.
 * Original icon atlas: Effortless Building, Requios and contributors.
 * See META-INF/effortless-building/LICENSE and development-assets/builder/effortless-assets.json.
 * Native 1.7.10 integration: Zahin. */
package dev.industrialtechnology.builder.client;

import dev.industrialtechnology.builder.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.*;
import org.lwjgl.opengl.GL11;

/** Hold/release Alt or click a segment. Air use opens the same screen for touch controls. */
public class GuiBuildWheel extends GuiScreen {
    private static final net.minecraft.util.ResourceLocation ICONS=new net.minecraft.util.ResourceLocation("industrialtech:textures/gui/effortless_building_icons.png");
    private static final int[][] ICON_CELLS={{1,1},{2,1},{6,1},{3,1},{4,1},{5,1},{6,2},{7,1},{8,1},{9,1},{10,1},{11,1}};
    private final RenderItem itemRenderer=new RenderItem();
    private final boolean hold;private int hover=-1;private boolean hollow;private BuildMode current=BuildMode.LINE;
    public GuiBuildWheel(boolean hold){this.hold=hold;}
    public int centerX(){return width/2;}public int centerY(){return height/2-10;}public int radius(){return Math.max(52,Math.min(106,height/2-44));}
    @Override public void initGui(){if(BuilderData.held(mc.thePlayer)){current=BuilderData.mode(mc.thePlayer.getHeldItem());hollow=BuilderData.hollow(mc.thePlayer.getHeldItem());}if(hold&&Mouse.isCreated())Mouse.setCursorPosition(mc.displayWidth/2,mc.displayHeight/2);buttonList.clear();buttonList.add(new GuiButton(0,8,height-28,114,20,hollow?"Hollow: ON":"Hollow: OFF"));buttonList.add(new GuiButton(1,width-122,height-28,114,20,"Stop / cancel"));}
    protected boolean keyDown(){return BuilderClient.menuKey.getKeyCode()>0&&Keyboard.isKeyDown(BuilderClient.menuKey.getKeyCode());}
    @Override public void updateScreen(){if(!BuilderData.held(mc.thePlayer)){mc.displayGuiScreen(null);return;}if(hold&&!keyDown())choose();}
    @Override public boolean doesGuiPauseGame(){return false;}
    private void choose(){if(hover>=0)BuilderClient.send(BuildCommand.CONFIGURE,BuildMode.at(hover),hollow,null);mc.displayGuiScreen(null);}
    @Override protected void keyTyped(char ch,int key){if(key==Keyboard.KEY_ESCAPE)mc.displayGuiScreen(null);}
    @Override protected void actionPerformed(GuiButton button){if(button.id==0){hollow=!hollow;button.displayString=hollow?"Hollow: ON":"Hollow: OFF";BuilderClient.send(BuildCommand.CONFIGURE,current,hollow,null);}else{BuilderClient.send(BuildCommand.CANCEL,current,hollow,null);mc.displayGuiScreen(null);}}
    @Override protected void mouseClicked(int x,int y,int button){if(button!=0)return;hover=WheelLayout.sector(x-centerX(),y-centerY(),38,radius(),BuildMode.values().length);if(hover>=0){choose();return;}super.mouseClicked(x,y,button);}
    @Override public void drawScreen(int mouseX,int mouseY,float partial){GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);try{
        drawDefaultBackground();int cx=centerX(),cy=centerY(),r=radius(),count=BuildMode.values().length;hover=WheelLayout.sector(mouseX-cx,mouseY-cy,38,r,count);
        GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glDisable(GL11.GL_CULL_FACE);GL11.glDisable(GL11.GL_LIGHTING);GL11.glDisable(GL11.GL_DEPTH_TEST);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        for(int i=0;i<count;i++){double mid=WheelLayout.angle(i,count),half=Math.PI/count-.018;Tessellator t=Tessellator.instance;t.startDrawing(GL11.GL_QUADS);if(i==hover)t.setColorRGBA(45,154,197,235);else if(i==current.ordinal())t.setColorRGBA(43,94,120,225);else t.setColorRGBA(28,34,42,225);for(int k=0;k<12;k++){double a=mid-half+2*half*k/12,b=mid-half+2*half*(k+1)/12;t.addVertex(cx+Math.cos(a)*38,cy+Math.sin(a)*38,0);t.addVertex(cx+Math.cos(a)*r,cy+Math.sin(a)*r,0);t.addVertex(cx+Math.cos(b)*r,cy+Math.sin(b)*r,0);t.addVertex(cx+Math.cos(b)*38,cy+Math.sin(b)*38,0);}t.draw();}
        GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glColor4f(1,1,1,1);for(int i=0;i<count;i++){double a=WheelLayout.angle(i,count);icon(i,cx+(int)(Math.cos(a)*(r+38)/2),cy+(int)(Math.sin(a)*(r+38)/2));}drawCenteredString(fontRendererObj,"Building Stick",cx,Math.max(5,cy-r-19),0xffffff);String selected=BuildMode.at(hover>=0?hover:current.ordinal()).label;fontRendererObj.drawSplitString(selected,cx-32,cy+7,64,0xffffff);
        if(BuilderData.held(mc.thePlayer)){ItemStack material=BuilderData.material(mc.thePlayer.getHeldItem());if(material!=null){GL11.glEnable(GL11.GL_DEPTH_TEST);GL11.glEnable(GL11.GL_CULL_FACE);RenderHelper.enableGUIStandardItemLighting();itemRenderer.renderItemAndEffectIntoGUI(fontRendererObj,mc.getTextureManager(),material,cx-8,cy-18);RenderHelper.disableStandardItemLighting();GL11.glDisable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_CULL_FACE);}}
        drawCenteredString(fontRendererObj,hold?"Release Alt to select | Esc cancels":"Click a mode | Esc closes",cx,height-43,0xdddddd);super.drawScreen(mouseX,mouseY,partial);GL11.glColor4f(1,1,1,1);mc.getTextureManager().bindTexture(ICONS);drawTexturedModalRect(12,height-26,(hollow?3:2)*16,32,16,16);drawTexturedModalRect(width-118,height-26,0,16,16,16);
    }finally{GL11.glPopAttrib();}}
    private void icon(int mode,int x,int y){GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glColor4f(1,1,1,1);mc.getTextureManager().bindTexture(ICONS);drawTexturedModalRect(x-8,y-8,ICON_CELLS[mode][0]*16,ICON_CELLS[mode][1]*16,16,16);}
}
