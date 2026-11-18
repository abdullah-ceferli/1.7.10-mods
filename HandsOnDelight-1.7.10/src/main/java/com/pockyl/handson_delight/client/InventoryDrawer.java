package com.pockyl.handson_delight.client;

import com.pockyl.handson_delight.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vectorwing.farmersdelight.legacy.*;

/** Diegesis FocusScreen's minimal compact drawer, using the authored scene colours and defaults. */
public final class InventoryDrawer extends Gui {
    private static final int COMPACT_PAD=3,PITCH=18,COMPACT_COLUMNS=9;
    private final StationScreen screen;private final Minecraft mc=Minecraft.getMinecraft();private final RenderItem items=new RenderItem();
    private final List<Integer> slots=new ArrayList<Integer>();
    private int left,top,width,height;
    InventoryDrawer(StationScreen screen){this.screen=screen;}
    private boolean usable(ItemStack stack){
        if(Stacks.empty(stack))return false;
        if(screen.tile instanceof CookingPotTile){if(((CookingPotTile)screen.tile).isContainer(stack))return true;for(CookingRecipes.Recipe r:CookingRecipes.ALL){if(Stacks.same(r.container,stack))return true;for(List<ItemStack> options:r.inputs)if(Stacks.matches(options,stack))return true;}}
        else if(screen.tile instanceof GrillingTile)return GrillingRecipes.find(stack)!=null;
        else if(screen.tile instanceof CuttingBoardTile){for(CuttingRecipes.Recipe r:CuttingRecipes.ALL)if(r.matchesInput(stack)||r.matchesTool(stack))return true;for(RecipeBook.Entry e:RecipeBook.list(screen.tile))if(e.assembly!=null)for(List<ItemStack> options:e.inputs)if(Stacks.matches(options,stack))return true;}
        return false;
    }
    private void layout(int mx,int my){
        boolean carrying=!Stacks.empty(mc.thePlayer.inventory.getItemStack());
        if(!carrying&&!contains(mx,my)){Iterator<Integer> i=slots.iterator();while(i.hasNext())if(!usable(mc.thePlayer.inventory.getStackInSlot(i.next())))i.remove();}
        for(int i=0;i<36;i++)if(usable(mc.thePlayer.inventory.getStackInSlot(i))&&!slots.contains(i))slots.add(i);
        if(carrying){boolean empty=false;for(int i:slots)if(Stacks.empty(mc.thePlayer.inventory.getStackInSlot(i)))empty=true;if(!empty)for(int i=0;i<36;i++)if(Stacks.empty(mc.thePlayer.inventory.getStackInSlot(i))){slots.add(i);break;}}
        width=slots.isEmpty()?0:Math.min(slots.size(),COMPACT_COLUMNS)*PITCH+2*COMPACT_PAD;height=slots.isEmpty()?0:(slots.size()+8)/9*PITCH+2*COMPACT_PAD;
        left=Math.round((screen.width-width)*screen.scene.align);top=Math.round(screen.height-height-screen.scene.margin);
    }
    public boolean contains(int mx,int my){return width>0&&mx>=left&&mx<left+width&&my>=top&&my<top+height;}
    public int[] slotCenter(int inventorySlot){int index=slots.indexOf(inventorySlot);return index<0?null:new int[]{left+COMPACT_PAD+index%9*PITCH+PITCH/2,top+COMPACT_PAD+index/9*PITCH+PITCH/2};}
    public int size(){return slots.size();}
    private int slotAt(int mx,int my){if(!contains(mx,my))return -1;int col=(mx-left-COMPACT_PAD)/PITCH,row=(my-top-COMPACT_PAD)/PITCH,index=row*9+col;return col>=0&&col<9&&index>=0&&index<slots.size()?slots.get(index):-1;}
    public ItemStack pointed(int mx,int my){int slot=slotAt(mx,my);return slot<0?null:mc.thePlayer.inventory.getStackInSlot(slot);}
    public boolean click(int mx,int my,int button){if(!contains(mx,my))return false;int slot=slotAt(mx,my);if(slot<0||button>1)return true;
        if(GuiScreen.isShiftKeyDown()&&Stacks.empty(mc.thePlayer.inventory.getItemStack()))screen.fromInventory(slot,true);
        else mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,slot<9?36+slot:slot,button,0,mc.thePlayer);
        return true;
    }
    public void draw(int mx,int my){
        layout(mx,my);RenderState saved=RenderState.capture();try{GL11.glDisable(GL11.GL_LIGHTING);GL11.glDisable(GL11.GL_DEPTH_TEST);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawRect(left,top,left+width,top+height,screen.scene.background);
        for(int slot:slots){int[] c=slotCenter(slot);drawRect(c[0]-8,c[1]-8,c[0]+8,c[1]+8,screen.scene.slotColor);drawItem(mc.thePlayer.inventory.getStackInSlot(slot),c[0]-8,c[1]-8);if(slot==slotAt(mx,my))drawRect(c[0]-8,c[1]-8,c[0]+8,c[1]+8,0x80ffffff);}
        ItemStack carry=mc.thePlayer.inventory.getItemStack();if(!Stacks.empty(carry)){GL11.glPushMatrix();GL11.glTranslatef(0,0,400);drawItem(carry,mx-8,my-8);GL11.glPopMatrix();}
        }finally{saved.close();}ItemStack pointed=pointed(mx,my);if(Stacks.empty(mc.thePlayer.inventory.getItemStack())&&!Stacks.empty(pointed))screen.tooltip(pointed,mx,my);
    }
    private void drawItem(ItemStack stack,int x,int y){if(Stacks.empty(stack))return;RenderHelper.enableGUIStandardItemLighting();items.renderItemAndEffectIntoGUI(mc.fontRenderer,mc.getTextureManager(),stack,x,y);items.renderItemOverlayIntoGUI(mc.fontRenderer,mc.getTextureManager(),stack,x,y);RenderHelper.disableStandardItemLighting();}
}
