package dev.industrialtechnology.builder.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.industrialtechnology.builder.*;
import dev.industrialtechnology.core.IndustrialTechnology;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public final class BuilderClient {
    public static final KeyBinding menuKey=new KeyBinding("key.industrialtech.builder_menu",Keyboard.KEY_LMENU,"Industrial Technology");
    private BuildShape shape;private BuildPos oldA,oldB;private BuildMode oldMode;private boolean oldHollow;private String error;private boolean clickHeld;
    private final ArrayList<BuildPos> preview=new ArrayList<BuildPos>();
    public static void install(){ClientRegistry.registerKeyBinding(menuKey);BuilderClient client=new BuilderClient();FMLCommonHandler.instance().bus().register(client);MinecraftForge.EVENT_BUS.register(client);}
    public static void air(){Minecraft mc=Minecraft.getMinecraft();if(!BuilderData.held(mc.thePlayer))return;if(BuilderData.anchor(mc.thePlayer,mc.thePlayer.getHeldItem())==null)mc.displayGuiScreen(new GuiBuildWheel(false));else{BuildPos end=target();if(end!=null)send(BuildCommand.AIR_END,BuilderData.mode(mc.thePlayer.getHeldItem()),BuilderData.hollow(mc.thePlayer.getHeldItem()),end);}}
    public static void send(int action,BuildMode mode,boolean hollow,BuildPos end){Minecraft mc=Minecraft.getMinecraft();if(BuilderData.held(mc.thePlayer))IndustrialTechnology.network.sendToServer(new BuildCommand(action,mc.thePlayer.inventory.currentItem,mode,hollow,end));}
    public static BuildPos target(){Minecraft mc=Minecraft.getMinecraft();if(mc.thePlayer==null||mc.theWorld==null)return null;MovingObjectPosition hit=mc.objectMouseOver;if(hit!=null&&hit.typeOfHit==MovingObjectPosition.MovingObjectType.BLOCK){int x=hit.blockX,y=hit.blockY,z=hit.blockZ;if(mc.theWorld.getBlock(x,y,z).isReplaceable(mc.theWorld,x,y,z))return new BuildPos(x,y,z);ForgeDirection f=ForgeDirection.getOrientation(hit.sideHit);return new BuildPos(x+f.offsetX,y+f.offsetY,z+f.offsetZ);}Vec3 look=mc.thePlayer.getLookVec();return new BuildPos((int)Math.floor(mc.thePlayer.posX+look.xCoord*5),(int)Math.floor(mc.thePlayer.posY+mc.thePlayer.getEyeHeight()+look.yCoord*5),(int)Math.floor(mc.thePlayer.posZ+look.zCoord*5));}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){if(event.phase!=TickEvent.Phase.END)return;Minecraft mc=Minecraft.getMinecraft();boolean openMenu=menuKey.isPressed();if(!mc.gameSettings.keyBindUseItem.getIsKeyPressed())clickHeld=false;if(!BuilderData.held(mc.thePlayer)||mc.theWorld==null){error=null;shape=null;oldA=oldB=null;preview.clear();return;}
        if(mc.currentScreen==null&&openMenu){mc.displayGuiScreen(new GuiBuildWheel(true));return;}
        ItemStack tool=mc.thePlayer.getHeldItem();BuildPos a=BuilderData.anchor(mc.thePlayer,tool),b=target();if(a==null||b==null){error=null;shape=null;oldA=null;preview.clear();return;}BuildMode mode=BuilderData.mode(tool);boolean hollow=BuilderData.hollow(tool);
        if(!a.equals(oldA)||!b.equals(oldB)||mode!=oldMode||hollow!=oldHollow){oldA=a;oldB=b;oldMode=mode;oldHollow=hollow;preview.clear();error=null;try{shape=new BuildShape(mode,hollow,a,b);}catch(IllegalArgumentException e){shape=null;error=e.getMessage();}}
        if(shape!=null)for(int i=0;i<1024&&shape.hasNext()&&preview.size()<128;i++){BuildPos p=shape.next();if(p!=null)preview.add(p);}
    }
    @SubscribeEvent(priority=cpw.mods.fml.common.eventhandler.EventPriority.HIGHEST) public void interact(net.minecraftforge.event.entity.player.PlayerInteractEvent event){if(!event.entityPlayer.worldObj.isRemote||!BuilderData.held(event.entityPlayer)||BuilderData.mode(event.entityPlayer.getHeldItem())==BuildMode.SINGLE)return;if(event.action==net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK||event.action==net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR){if(clickHeld)event.setCanceled(true);else clickHeld=true;}}
    @SubscribeEvent public void hud(RenderGameOverlayEvent.Post event){if(event.type!=RenderGameOverlayEvent.ElementType.ALL)return;Minecraft mc=Minecraft.getMinecraft();if(!BuilderData.held(mc.thePlayer)||mc.currentScreen!=null)return;ItemStack tool=mc.thePlayer.getHeldItem(),material=BuilderData.material(tool);String top="Building Stick | "+BuilderData.mode(tool).label+(BuilderData.hollow(tool)?" | Hollow":" | Filled");mc.fontRenderer.drawStringWithShadow(top,8,8,0x79ddff);mc.fontRenderer.drawStringWithShadow("Material: "+(material==null?"sneak-right-click a block":material.getDisplayName()),8,20,0xffffff);if(error!=null)mc.fontRenderer.drawStringWithShadow(error,8,32,0xff7777);else mc.fontRenderer.drawStringWithShadow(oldA==null?"Hold Alt: modes | Right-click: start":"Right-click: build | Sneak-right-click air: cancel | Preview up to 128 blocks",8,32,0xdddddd);}
    @SubscribeEvent public void world(RenderWorldLastEvent event){Minecraft mc=Minecraft.getMinecraft();if(!BuilderData.held(mc.thePlayer)||shape==null||oldA==null)return;double x=mc.renderViewEntity.lastTickPosX+(mc.renderViewEntity.posX-mc.renderViewEntity.lastTickPosX)*event.partialTicks,y=mc.renderViewEntity.lastTickPosY+(mc.renderViewEntity.posY-mc.renderViewEntity.lastTickPosY)*event.partialTicks,z=mc.renderViewEntity.lastTickPosZ+(mc.renderViewEntity.posZ-mc.renderViewEntity.lastTickPosZ)*event.partialTicks;
        GL11.glPushMatrix();GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);try{GL11.glTranslated(-x,-y,-z);GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glDisable(GL11.GL_LIGHTING);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);GL11.glDepthMask(false);GL11.glLineWidth(1);Tessellator t=Tessellator.instance;t.startDrawing(GL11.GL_LINES);t.setColorRGBA(70,200,255,170);box(t,shape.minX,shape.minY,shape.minZ,shape.maxX+1,shape.maxY+1,shape.maxZ+1);for(BuildPos p:preview){if(!BuilderService.loaded(mc.theWorld,p.x,p.y,p.z))continue;boolean clear=mc.theWorld.getBlock(p.x,p.y,p.z).isReplaceable(mc.theWorld,p.x,p.y,p.z);t.setColorRGBA(clear?90:255,clear?235:100,clear?200:100,180);box(t,p.x+.02,p.y+.02,p.z+.02,p.x+.98,p.y+.98,p.z+.98);}t.draw();}finally{GL11.glPopAttrib();GL11.glPopMatrix();}}
    private static void edge(Tessellator t,double x,double y,double z,double a,double b,double c){t.addVertex(x,y,z);t.addVertex(a,b,c);}
    private static void box(Tessellator t,double x,double y,double z,double a,double b,double c){edge(t,x,y,z,a,y,z);edge(t,x,y,z,x,b,z);edge(t,x,y,z,x,y,c);edge(t,a,b,c,x,b,c);edge(t,a,b,c,a,y,c);edge(t,a,b,c,a,b,z);edge(t,a,y,z,a,b,z);edge(t,a,y,z,a,y,c);edge(t,x,b,z,a,b,z);edge(t,x,b,z,x,b,c);edge(t,x,y,c,a,y,c);edge(t,x,y,c,x,b,c);}
}
