package com.pockyl.handson_delight.client;

import com.pockyl.handson_delight.*;
import java.util.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.*;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import vectorwing.farmersdelight.legacy.*;

/** Native input for the original Hands-On scenes and Diegesis compact drawer. */
public final class StationScreen extends GuiScreen {
    public final InventoryTile tile;public final long token;
    public FoldingBook book;public SourceScene scene;public InventoryDrawer drawer;
    private int clock,lastX,lastY;private EntityLivingBase previous,camera;
    private Neighbors neighbors;private long drawn=System.nanoTime();private boolean spoon,dragged,spoonPressed;private long spoonSince;
    public static StationScreen visiting;private boolean recipeView;
    public StationScreen(InventoryTile tile,long token){this.tile=tile;this.token=token;}
    @Override public boolean doesGuiPauseGame(){return false;}
    @Override public void initGui(){
        recipeView=false;if(visiting==this)visiting=null;
        if(scene==null)scene=new SourceScene(tile);
        if(book==null)book=new FoldingBook(this);
        if(drawer==null)drawer=new InventoryDrawer(this);
        scene.reshape(width,height);if(neighbors==null)neighbors=new Neighbors(this);KitchenCamera.begin(this);

    }
    void send(int action,int a,int b,float x,float z,String id){HandsOnDelight.NETWORK.sendToServer(new Packets.Action(token,action,a,b,x,z,clock,id));}
    public int clock(){return clock;}
    public float appear(){float t=KitchenCamera.arrived();return NativeMath.clamp((t-.25F)/.5F,0,1);}
    public boolean spoonHeld(){return spoon;}
    public double[] cursor(float plane){return scene.plane(lastX,lastY,width,height,plane);}
    public void fillRecipe(String id,boolean bulk){send(Actions.FILL,0,0,bulk?1:0,0,id);}
    public void tooltip(ItemStack stack,int x,int y){renderToolTip(stack,x,y);}
    public boolean showRecipe(ItemStack stack,boolean usages){
        if(Stacks.empty(stack)||!cpw.mods.fml.common.Loader.isModLoaded("NotEnoughItems"))return false;
        recipeView=true;visiting=this;boolean opened=com.pockyl.handson_delight.compat.nei.NEIHooks.view(stack,usages);if(!opened){recipeView=false;visiting=null;}return opened;
    }
    public void leaveRecipeViewer(){if(mc.thePlayer!=null&&mc.theWorld!=null)send(Actions.CLOSE,0,0,0,0,"");if(visiting==this)visiting=null;}
    @Override public void updateScreen(){
        if(mc.thePlayer==null||mc.theWorld==null||mc.theWorld.getTileEntity(tile.xCoord,tile.yCoord,tile.zCoord)!=tile||mc.thePlayer.getHealth()<=0||mc.thePlayer.getDistanceSq(tile.xCoord+.5,tile.yCoord+.5,tile.zCoord+.5)>64){mc.displayGuiScreen(null);return;}
        clock++;book.tick();
        if(spoon){double[] p=scene.plane(lastX,lastY,width,height,8.5);float x=(float)(p[0]-8)/2.3F,z=(float)(p[2]-8)/2.3F,length=(float)Math.hypot(x,z);if(length>1){x/=length;z/=length;}send(Actions.STIR,0,0,x,z,"");}
    }
    @Override public void onGuiClosed(){
        spoon=false;if(book!=null)book.release();
        if(!recipeView&&mc.thePlayer!=null&&mc.theWorld!=null)send(Actions.CLOSE,0,0,0,0,"");
        KitchenCamera.end(this,!recipeView);

    }
    @Override public void drawScreen(int mx,int my,float partial){
        lastX=mx;lastY=my;long now=System.nanoTime();float dt=Math.min(.1F,(now-drawn)/1e9F);drawn=now;
        if(tile instanceof CookingPotTile)PotBadge.draw(this);
        book.draw(mx,my,partial);drawer.draw(mx,my);neighbors.draw(mx,my,dt,!spoon&&Stacks.empty(mc.thePlayer.inventory.getItemStack())&&KitchenCamera.arrived()>=1&&!book.ownsPointer(mx,my));
        super.drawScreen(mx,my,partial);
    }
    public int targetSlot(ItemStack stack){
        if(tile instanceof CuttingBoardTile||tile instanceof SkilletTile)return 0;
        if(tile instanceof StoveTile){for(int i=0;i<6;i++)if(hit("piece_"+i,lastX,lastY))return i;}
        int slots=tile instanceof CookingPotTile?6:tile.getSizeInventory();for(int i=0;i<slots;i++)if(Stacks.same(tile.getStackInSlot(i),stack)&&tile.getStackInSlot(i).stackSize<Math.min(64,stack.getMaxStackSize()))return i;
        for(int i=0;i<slots;i++)if(Stacks.empty(tile.getStackInSlot(i)))return i;return 0;
    }
    public void fromInventory(int slot,boolean bulk){ItemStack held=slot<0?mc.thePlayer.inventory.getItemStack():mc.thePlayer.inventory.getStackInSlot(slot);
        if(tile instanceof CuttingBoardTile&&CuttingRecipes.find(tile.getStackInSlot(0),held)!=null){if(slot<0&&overNextCut())send(Actions.CUT,slot,StationHooks.state(tile).cuts,0,0,"");}
        else if(tile instanceof CookingPotTile&&((CookingPotTile)tile).isContainer(held))send(Actions.SERVE,slot,0,bulk?1:0,0,"");
        else if(tile instanceof CuttingBoardTile&&Stacks.empty(tile.getStackInSlot(0))&&bulk&&!Stacks.empty(held)&&(held.getItem() instanceof net.minecraft.item.ItemTool||held.getItem() instanceof net.minecraft.item.ItemSword||held.getItem() instanceof net.minecraft.item.ItemShears))send(Actions.CARVE,slot,0,0,0,"");
        else send(Actions.INSERT,slot,targetSlot(held),bulk?1:0,0,"");
    }
    boolean hit(String id,int mx,int my){return scene.hit(id,mx,my,width,height);}
    public void clickAt(int mx,int my,int button){mouseClicked(mx,my,button);}
    public void keyAt(char c,int key){keyTyped(c,key);}
    public boolean overStation(){return !book.ownsPointer(lastX,lastY)&&!drawer.contains(lastX,lastY)&&hit("mouth",lastX,lastY);}
    @Override protected void mouseClicked(int mx,int my,int button){
        lastX=mx;lastY=my;dragged=false;
        if(KitchenCamera.arrived()<1)return;
        if(spoon){spoon=false;return;}
        if(drawer.click(mx,my,button))return;
        if(book.click(mx,my,button))return;
        if(!Stacks.empty(mc.thePlayer.inventory.getItemStack())){if(hit("mouth",mx,my))fromInventory(-1,button==0);return;}
        if(tile instanceof CookingPotTile){
            if(hit("ladle",mx,my)&&button==0){spoon=true;spoonPressed=true;spoonSince=System.nanoTime();return;}
            StationVisuals visual=StationVisuals.of(tile);double[] cursor=scene.plane(mx,my,width,height,visual.level());
            for(int i=0;i<6;i++)if(!Stacks.empty(tile.getStackInSlot(i))&&Math.hypot(cursor[0]-8-visual.itemX[i],cursor[2]-8-visual.itemZ[i])<1.6){send(Actions.TAKE,i,0,0,0,"");return;}
            if(hit("soup",mx,my)){if(button==0&&!Stacks.empty(tile.getStackInSlot(6)))serve();else if(button==1)for(int i=5;i>=0;i--)if(!Stacks.empty(tile.getStackInSlot(i))){send(Actions.TAKE,i,0,0,0,"");break;}}
        }else if(tile instanceof CuttingBoardTile){if(hit("surface",mx,my)){if(button==1||((CuttingBoardTile)tile).carved)send(Actions.TAKE,0,0,0,0,"");else if(!StationHooks.state(tile).layers.isEmpty())send(Actions.ASSEMBLE,0,0,0,0,"");else cut();}}
        else if(tile instanceof SkilletTile){if(hit("handle",mx,my)&&button==0)send(Actions.TURN,0,0,0,0,"");else if(hit("food",mx,my)){if(button==1||StationHooks.state(tile).frying[0].ready())send(Actions.TAKE,0,0,0,0,"");else send(Actions.TURN,0,0,0,0,"");}}
        else for(int i=0;i<6;i++)if(hit("piece_"+i,mx,my)&&!Stacks.empty(tile.getStackInSlot(i))){send(button==1||StationHooks.state(tile).frying[i].ready()?Actions.TAKE:Actions.TURN,i,0,0,0,"");break;}
    }
    public double[] nextCutPoint(){ItemStack input=tile.getStackInSlot(0);if(Stacks.empty(input))return null;CuttingRecipes.Recipe recipe=null;for(CuttingRecipes.Recipe candidate:CuttingRecipes.ALL)if(candidate.matchesInput(input)){recipe=candidate;break;}if(recipe==null)return null;int marks=CutPlan.marks(input,recipe);ItemVoxels.Line line=ItemVoxels.lines(input,10,marks,CutPlan.style(input))[Math.min(marks-1,StationHooks.state(tile).cuts)];double r=Math.toRadians(-90*(tile.getBlockMetadata()&3)),x=(-1.5+line.x)/16,z=line.z/16;return scene.local(tile.xCoord+.5+x*Math.cos(r)+z*Math.sin(r),tile.yCoord+(1.01+10F/16)/16,tile.zCoord+.5-x*Math.sin(r)+z*Math.cos(r),tile);}
    private boolean overNextCut(){
        ItemStack input=tile.getStackInSlot(0);if(Stacks.empty(input))return false;
        CuttingRecipes.Recipe recipe=null;for(CuttingRecipes.Recipe r:CuttingRecipes.ALL)if(r.matchesInput(input)){recipe=r;break;}if(recipe==null)return false;
        float top=input.getItem() instanceof net.minecraft.item.ItemBlock?5.2F:10F/16;
        double[] local=scene.plane(lastX,lastY,width,height,1.01+top),world=scene.world(tile,local);
        double dx=(world[0]-tile.xCoord-.5)*16,dz=(world[2]-tile.zCoord-.5)*16,r=Math.toRadians(90*(tile.getBlockMetadata()&3));
        float x=(float)(dx*Math.cos(r)+dz*Math.sin(r))+1.5F,z=(float)(-dx*Math.sin(r)+dz*Math.cos(r));
        if(input.getItem() instanceof net.minecraft.item.ItemBlock)return Math.abs(x)<=2.6F&&Math.abs(z)<=2.6F;
        int marks=CutPlan.marks(input,recipe);ItemVoxels.Line line=ItemVoxels.lines(input,10,marks,CutPlan.style(input))[Math.min(marks-1,StationHooks.state(tile).cuts)];
        return Math.abs(x)<=5.7F&&Math.abs(z)<=5.7F&&Math.abs(line.distance(x,z))<1.4F;
    }
    private void cut(){if(!overNextCut())return;for(int i=0;i<36;i++)if(CuttingRecipes.find(tile.getStackInSlot(0),mc.thePlayer.inventory.getStackInSlot(i))!=null){send(Actions.CUT,i,StationHooks.state(tile).cuts,0,0,"");return;}send(Actions.TAKE,0,0,0,0,"");}
    private void serve(){for(int i=0;i<36;i++)if(((CookingPotTile)tile).isContainer(mc.thePlayer.inventory.getStackInSlot(i))){send(Actions.SERVE,i,0,isShiftKeyDown()?1:0,0,"");break;}}
    @Override protected void mouseClickMove(int x,int y,int button,long elapsed){dragged=true;}
    @Override protected void mouseMovedOrUp(int x,int y,int button){if(button>=0){if(spoonPressed){if((System.nanoTime()-spoonSince)/1e9>.3)spoon=false;spoonPressed=false;}book.release();if(dragged&&!drawer.contains(x,y)&&hit("mouth",x,y)&&!Stacks.empty(mc.thePlayer.inventory.getItemStack()))fromInventory(-1,button==0);dragged=false;}}
    @Override public void handleMouseInput(){super.handleMouseInput();int wheel=Mouse.getEventDWheel();if(wheel!=0&&book.ownsPointer(lastX,lastY))book.wheel(wheel>0?-1:1);}
    @Override protected void keyTyped(char c,int key){if(book.key(c,key))return;if(!spoon&&Stacks.empty(mc.thePlayer.inventory.getItemStack())&&KitchenCamera.arrived()>=1&&neighbors.key(key))return;ItemStack pointed=drawer.pointed(lastX,lastY);if(Stacks.empty(pointed))pointed=book.pointed();if((key==Keyboard.KEY_R||key==Keyboard.KEY_U)&&!Stacks.empty(pointed)){showRecipe(pointed,key==Keyboard.KEY_U);return;}super.keyTyped(c,key);}
}
