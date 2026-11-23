package com.pockyl.handson_delight.client;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

/** Native checks that do not rely on texture attributes being saved by glPushAttrib. */
public final class RenderStateChecks {
    private static void require(boolean value,String text){if(!value)throw new AssertionError(text);}
    private static void noError(String step){int error=GL11.glGetError();require(error==GL11.GL_NO_ERROR,step+": OpenGL error "+error);}
    private static int env(int name){return GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV,name);}
    private static float[] matrix(){FloatBuffer b=BufferUtils.createFloatBuffer(16);GL11.glGetFloat(GL11.GL_TEXTURE_MATRIX,b);float[] out=new float[16];b.get(out);return out;}
    private static void near(float[] a,float[] b,String message){for(int i=0;i<a.length;i++)require(Math.abs(a[i]-b[i])<.0001F,message);}
    public static int run(Minecraft mc){
        RenderState enclosing=RenderState.capture();
        try{
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,GL13.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_COMBINE_ALPHA,GL13.GL_INTERPOLATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_SOURCE1_ALPHA,GL13.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_OPERAND0_RGB,GL11.GL_ONE_MINUS_SRC_COLOR);
            try(RenderState.Texture opacity=RenderState.fade()){
                require(env(GL13.GL_SOURCE1_ALPHA)==GL13.GL_CONSTANT,"Original cookbook opacity not configured");
            }
            require(env(GL11.GL_TEXTURE_ENV_MODE)==GL13.GL_COMBINE&&env(GL13.GL_COMBINE_ALPHA)==GL13.GL_INTERPOLATE&&env(GL13.GL_SOURCE1_ALPHA)==GL13.GL_PREVIOUS&&env(GL13.GL_OPERAND0_RGB)==GL11.GL_ONE_MINUS_SRC_COLOR,"Faded icon leaked its texture combiner");

            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);GL11.glMatrixMode(GL11.GL_TEXTURE);GL11.glLoadIdentity();GL11.glTranslatef(.2F,.3F,0);float[] light=matrix();
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);GL11.glLoadIdentity();GL11.glScalef(.75F,.8F,1);float[] base=matrix();GL11.glMatrixMode(GL11.GL_MODELVIEW);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA,GL11.GL_ONE,GL11.GL_ZERO);
            try(RenderState scope=RenderState.capture()){
                OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);GL11.glMatrixMode(GL11.GL_TEXTURE);GL11.glLoadIdentity();GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,GL11.GL_REPLACE);
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);GL11.glLoadIdentity();GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ZERO);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            }
            require(GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE)==OpenGlHelper.defaultTexUnit&&GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE)==OpenGlHelper.lightmapTexUnit,"Texture units leaked");
            require(GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA)==GL11.GL_ONE&&GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA)==GL11.GL_ZERO,"Separate alpha blending leaked");
            near(base,matrix(),"Default texture matrix leaked");OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);near(light,matrix(),"Lightmap texture matrix leaked");

            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);int before=env(GL13.GL_SOURCE1_ALPHA);
            try{try(RenderState scope=RenderState.capture()){try(RenderState.Texture fade=RenderState.fade()){throw new IllegalStateException("fixture");}}}catch(IllegalStateException expected){}
            require(env(GL13.GL_SOURCE1_ALPHA)==before,"Exception cleanup leaked opacity");
        }finally{enclosing.close();}
        pixelCheck(mc);return 4+FoodTextureChecks.run(mc);
    }
    private static void pixelCheck(Minecraft mc){
        int originalFramebuffer=GL11.glGetInteger(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_BINDING);
        RenderState enclosing=RenderState.capture();Framebuffer target=null;
        GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glPushMatrix();GL11.glLoadIdentity();GL11.glOrtho(0,192,64,0,-100,100);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPushMatrix();GL11.glLoadIdentity();
        try{
            target=new Framebuffer(192,64,true);target.bindFramebuffer(true);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glMatrixMode(GL11.GL_TEXTURE);GL11.glLoadIdentity();GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glDisable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_LIGHTING);GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);GL11.glAlphaFunc(GL11.GL_GREATER,.1F);GL11.glEnable(GL11.GL_BLEND);OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA,GL11.GL_ONE,GL11.GL_ZERO);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,GL11.GL_MODULATE);
            draw(mc);byte[] reference=draw(mc);
            try(RenderState scope=RenderState.capture()){try(RenderState.Texture opacity=RenderState.fade()){RenderHelper.enableGUIStandardItemLighting();new RenderItem().renderItemAndEffectIntoGUI(mc.fontRenderer,mc.getTextureManager(),new ItemStack(Items.beef),8,30);RenderHelper.disableStandardItemLighting();}}
            byte[] after=draw(mc);require(Arrays.equals(reference,after),"Cookbook rendering changed subsequent native font/hotbar pixels");
            require(GL11.glGetError()==GL11.GL_NO_ERROR,"Render-state checks raised an OpenGL error");
        }finally{
            if(target!=null)target.deleteFramebuffer();noError("Deleting font-check framebuffer");OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e,originalFramebuffer);noError("Restoring original framebuffer");
            GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPopMatrix();noError("Restoring font-check model matrix");GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glPopMatrix();noError("Restoring font-check projection matrix");enclosing.close();noError("Closing font-check render scope");
        }
    }
    private static byte[] draw(Minecraft mc){
        GL11.glClearColor(.15F,.2F,.25F,1);GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);GL11.glColor4f(1,1,1,1);
        mc.fontRenderer.drawString("Minecraft Singleplayer",5,5,0xffffff);mc.fontRenderer.drawString("123  Hotbar",5,18,0xffffff);
        RenderHelper.enableGUIStandardItemLighting();new RenderItem().renderItemAndEffectIntoGUI(mc.fontRenderer,mc.getTextureManager(),new ItemStack(Items.beef),8,34);RenderHelper.disableStandardItemLighting();
        ByteBuffer pixels=BufferUtils.createByteBuffer(192*64*4);GL11.glReadPixels(0,0,192,64,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,pixels);byte[] out=new byte[pixels.capacity()];pixels.get(out);return out;
    }
}
