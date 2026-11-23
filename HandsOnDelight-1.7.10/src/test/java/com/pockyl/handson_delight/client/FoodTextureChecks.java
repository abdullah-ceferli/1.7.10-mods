package com.pockyl.handson_delight.client;

import com.google.gson.*;
import com.pockyl.handson_delight.compat.nei.AssemblyHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.*;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/** Read back actual native atlas and recipe pixels, including the reported color-key textures. */
public final class FoodTextureChecks {
    private static final String[] ITEMS=("ajvar artichoke_seed asparagus_seed basil_seed blackbean_seed cauliflower_seed chocolate chocolate_ice_cream churros coffee_seed corn_seed cranberry_seed cucumber greenonion_seed kiwi_seed knife nether_wart_stew peanut_butter_with_celery potato_soup ratatouille ravioli rhubarb rice rice_seed saguaro_seed strawberry_seed sushi zucchini").split(" ");
    private static final String[] BLOCKS=("cherry_ripe cucumber_crop_stage2 eggplant_crop_stage2 eggplant_crop_stage3 lemon_ripe lime_ripe lime_unripe nutmeg_ripe nutmeg_unripe pink_tree_bud plum_ripe saguaro_crop_stage1 white_tree_bud yellow_tree_bud zucchini_crop_stage2 zucchini_crop_stage3").split(" ");
    private static void require(boolean value,String text){if(!value)throw new AssertionError(text);}
    private static void noError(String step){int error=GL11.glGetError();require(error==GL11.GL_NO_ERROR,step+": OpenGL error "+error);}
    private static BufferedImage image(Minecraft mc,String id)throws IOException{
        try(InputStream in=mc.getResourceManager().getResource(new ResourceLocation(id)).getInputStream()){return ImageIO.read(in);}
    }
    public static int run(Minecraft mc){
        int checked=0;noError("Starting texture checks");
        try(RenderState saved=RenderState.capture()){
            if(Loader.isModLoaded("croptopia")){checked+=atlas(mc,"items",ITEMS,TextureMap.locationItemsTexture);noError("Item atlas");checked+=atlas(mc,"blocks",BLOCKS,TextureMap.locationBlocksTexture);noError("Block atlas");}
            checked+=recipe(mc);noError("Recipe framebuffer");
            if(Loader.isModLoaded("croptopia")){checked+=inventory(mc);noError("Inventory framebuffer");}
            require(GL11.glGetError()==GL11.GL_NO_ERROR,"Food texture checks raised an OpenGL error");
            return checked;
        }catch(IOException error){throw new RuntimeException(error);}
    }
    private static int atlas(Minecraft mc,String folder,String[] names,ResourceLocation location)throws IOException{
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);mc.getTextureManager().bindTexture(location);
        int width=GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D,0,GL11.GL_TEXTURE_WIDTH),height=GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D,0,GL11.GL_TEXTURE_HEIGHT);
        ByteBuffer data=BufferUtils.createByteBuffer(width*height*4);GL11.glGetTexImage(GL11.GL_TEXTURE_2D,0,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,data);
        TextureMap atlas=(TextureMap)mc.getTextureManager().getTexture(location);
        for(String name:names){
            BufferedImage source=image(mc,"croptopia:textures/"+folder+"/"+name+".png");int transparent=0;
            IIcon icon=atlas.getAtlasSprite("croptopia:"+name);require(!icon.getIconName().equals("missingno"),"Missing native sprite "+name);
            int left=(int)(icon.getMinU()*width+.5F),top=(int)(icon.getMinV()*height+.5F);
            for(int y=0;y<source.getHeight();y++)for(int x=0;x<source.getWidth();x++)if((source.getRGB(x,y)>>>24)==0){
                transparent++;int alpha=data.get(((top+y)*width+left+x)*4+3)&255;
                require(alpha==0,"Opaque GPU atlas background in "+name+" at "+x+","+y);
            }
            require(transparent>0,"Java 8 lost the original PNG transparency in "+name);
        }
        return names.length;
    }
    private static void setup(){
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glMatrixMode(GL11.GL_TEXTURE);GL11.glLoadIdentity();GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glDisable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_LIGHTING);GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);GL11.glAlphaFunc(GL11.GL_GREATER,.1F);GL11.glDisable(GL11.GL_BLEND);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,GL11.GL_MODULATE);GL11.glColor4f(1,1,1,1);
    }
    private static void projection(int width,int height){
        GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glPushMatrix();GL11.glLoadIdentity();GL11.glOrtho(0,width,height,0,-2000,2000);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPushMatrix();GL11.glLoadIdentity();
    }
    private static void restore(Minecraft mc,Framebuffer target,int originalFramebuffer){
        target.deleteFramebuffer();OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e,originalFramebuffer);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPopMatrix();GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glPopMatrix();GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
    private static int recipe(Minecraft mc)throws IOException{
        int originalFramebuffer=GL11.glGetInteger(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_BINDING);
        Framebuffer target=new Framebuffer(192,96,true);projection(192,96);
        try{
            target.bindFramebuffer(true);setup();GL11.glClearColor(.1F,.2F,.3F,1);GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
            AssemblyHandler handler=new AssemblyHandler();handler.loadCraftingRecipes(AssemblyHandler.UID);require(handler.numRecipes()>0,"No native assembly recipes");handler.drawBackground(0);
            BufferedImage original=image(mc,"handson_delight:textures/gui/assembly.png");
            int[][] regions={{4,29,4,29,40,22},{109,19,48,21,22,16},{136,18,0,58,18,18}};
            for(int[] region:regions){
                int sampleX=-1,sampleY=-1,expected=0;
                for(int y=2;y<region[5]-2&&sampleX<0;y++)for(int x=2;x<region[4]-2;x++){
                    int color=original.getRGB(region[2]+x,region[3]+y);
                    if((color>>>24)==255&&(color&255)>40){sampleX=region[0]+x;sampleY=region[1]+y;expected=color;break;}
                }
                require(sampleX>=0,"No opaque original recipe sample");
                ByteBuffer pixel=BufferUtils.createByteBuffer(4);GL11.glReadPixels(sampleX,95-sampleY,1,1,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,pixel);
                for(int channel=0;channel<3;channel++)require(Math.abs((pixel.get(channel)&255)-((expected>>(16-channel*8))&255))<=1,"NEI recipe texture is tinted gray in region "+region[0]);
            }
            FloatBuffer color=BufferUtils.createFloatBuffer(16);GL11.glGetFloat(GL11.GL_CURRENT_COLOR,color);
            require(Math.abs(color.get(0)-1)<.0001F&&Math.abs(color.get(1)-1)<.0001F&&Math.abs(color.get(2)-1)<.0001F,"NEI recipe leaked gray drawing color");
            ScreenShotHelper.saveScreenshot(mc.mcDataDir,"recipe-color-check.png",192,96,target);
        }finally{restore(mc,target,originalFramebuffer);}
        return 4;
    }
    private static int inventory(Minecraft mc)throws IOException{
        int originalFramebuffer=GL11.glGetInteger(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_BINDING);
        Framebuffer target=new Framebuffer(512,256,true);projection(512,256);int drawn=0;
        try{
            target.bindFramebuffer(true);setup();GL11.glClearColor(.35F,.45F,.55F,1);GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
            RenderItem renderer=new RenderItem();RenderHelper.enableGUIStandardItemLighting();
            for(String name:ITEMS){Item item=GameRegistry.findItem("croptopia",name);require(item!=null,"Missing food fixture "+name);renderer.renderItemAndEffectIntoGUI(mc.fontRenderer,mc.getTextureManager(),new ItemStack(item),8+drawn%16*30,8+drawn/16*30);drawn++;}
            JsonObject catalog;
            try(InputStream in=FoodTextureChecks.class.getResourceAsStream("/croptopia/catalog.json");Reader reader=new InputStreamReader(in,"UTF-8")){catalog=new JsonParser().parse(reader).getAsJsonObject();}
            for(JsonElement entry:catalog.getAsJsonArray("trees")){String name=entry.getAsJsonObject().get("leaves").getAsString();Block block=GameRegistry.findBlock("croptopia",name);renderer.renderItemAndEffectIntoGUI(mc.fontRenderer,mc.getTextureManager(),new ItemStack(block),8+drawn%16*30,8+drawn/16*30);drawn++;}
            RenderHelper.disableStandardItemLighting();ScreenShotHelper.saveScreenshot(mc.mcDataDir,"fruit-food-inventory-check.png",512,256,target);
        }finally{restore(mc,target,originalFramebuffer);}
        return drawn;
    }
}
