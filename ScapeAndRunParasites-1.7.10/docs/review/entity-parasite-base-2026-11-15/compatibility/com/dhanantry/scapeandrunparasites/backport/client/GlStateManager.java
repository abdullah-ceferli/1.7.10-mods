package com.dhanantry.scapeandrunparasites.backport.client;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
/** Direct fixed-function rendering operations for 1.7.10's LWJGL/OpenGL renderer. */
public final class GlStateManager {
    private GlStateManager() {}
    public enum SourceFactor {
        ZERO(0),ONE(1),SRC_COLOR(768),ONE_MINUS_SRC_COLOR(769),SRC_ALPHA(770),ONE_MINUS_SRC_ALPHA(771),DST_ALPHA(772),ONE_MINUS_DST_ALPHA(773),DST_COLOR(774),ONE_MINUS_DST_COLOR(775),SRC_ALPHA_SATURATE(776),CONSTANT_COLOR(32769),ONE_MINUS_CONSTANT_COLOR(32770),CONSTANT_ALPHA(32771),ONE_MINUS_CONSTANT_ALPHA(32772);
        public final int factor;SourceFactor(int n){factor=n;}
    }
    public enum DestFactor {
        ZERO(0),ONE(1),SRC_COLOR(768),ONE_MINUS_SRC_COLOR(769),SRC_ALPHA(770),ONE_MINUS_SRC_ALPHA(771),DST_ALPHA(772),ONE_MINUS_DST_ALPHA(773),DST_COLOR(774),ONE_MINUS_DST_COLOR(775),CONSTANT_COLOR(32769),ONE_MINUS_CONSTANT_COLOR(32770),CONSTANT_ALPHA(32771),ONE_MINUS_CONSTANT_ALPHA(32772);
        public final int factor;DestFactor(int n){factor=n;}
    }
    public enum FogMode { LINEAR(9729),EXP(2048),EXP2(2049);public final int capabilityId;FogMode(int n){capabilityId=n;} }
    /** The only blend profile used by the original SRP classes. */
    public enum Profile { TRANSPARENT_MODEL }
    public static void pushMatrix(){GL11.glPushMatrix();}public static void popMatrix(){GL11.glPopMatrix();}
    public static void matrixMode(int mode){GL11.glMatrixMode(mode);}public static void loadIdentity(){GL11.glLoadIdentity();}
    public static void scale(float x,float y,float z){GL11.glScalef(x,y,z);}public static void scale(double x,double y,double z){GL11.glScaled(x,y,z);}
    public static void translate(float x,float y,float z){GL11.glTranslatef(x,y,z);}public static void translate(double x,double y,double z){GL11.glTranslated(x,y,z);}
    public static void rotate(float angle,float x,float y,float z){GL11.glRotatef(angle,x,y,z);}
    public static void color(float r,float g,float b,float a){GL11.glColor4f(r,g,b,a);}public static void color(float r,float g,float b){color(r,g,b,1);}
    public static void glNormal3f(float x,float y,float z){GL11.glNormal3f(x,y,z);}
    public static void enableBlend(){GL11.glEnable(GL11.GL_BLEND);}public static void disableBlend(){GL11.glDisable(GL11.GL_BLEND);}
    public static void enableDepth(){GL11.glEnable(GL11.GL_DEPTH_TEST);}public static void disableDepth(){GL11.glDisable(GL11.GL_DEPTH_TEST);}
    public static void depthMask(boolean mask){GL11.glDepthMask(mask);}public static void depthFunc(int function){GL11.glDepthFunc(function);}
    public static void enableCull(){GL11.glEnable(GL11.GL_CULL_FACE);}public static void disableCull(){GL11.glDisable(GL11.GL_CULL_FACE);}
    public static void enableLighting(){GL11.glEnable(GL11.GL_LIGHTING);}public static void disableLighting(){GL11.glDisable(GL11.GL_LIGHTING);}
    public static void enableAlpha(){GL11.glEnable(GL11.GL_ALPHA_TEST);}public static void disableAlpha(){GL11.glDisable(GL11.GL_ALPHA_TEST);}
    public static void alphaFunc(int function,float reference){GL11.glAlphaFunc(function,reference);}
    public static void enableTexture2D(){GL11.glEnable(GL11.GL_TEXTURE_2D);}public static void disableTexture2D(){GL11.glDisable(GL11.GL_TEXTURE_2D);}
    public static void enableColorMaterial(){GL11.glEnable(GL11.GL_COLOR_MATERIAL);}public static void disableColorMaterial(){GL11.glDisable(GL11.GL_COLOR_MATERIAL);}
    public static void enableRescaleNormal(){GL11.glEnable(GL12.GL_RESCALE_NORMAL);}public static void disableRescaleNormal(){GL11.glDisable(GL12.GL_RESCALE_NORMAL);}
    public static void blendFunc(int source,int destination){GL11.glBlendFunc(source,destination);}
    public static void blendFunc(SourceFactor source,DestFactor destination){blendFunc(source.factor,destination.factor);}
    public static void tryBlendFuncSeparate(int sr,int dr,int sa,int da){ContextCapabilities c=GLContext.getCapabilities();if(c.OpenGL14)GL14.glBlendFuncSeparate(sr,dr,sa,da);else if(c.GL_EXT_blend_func_separate)EXTBlendFuncSeparate.glBlendFuncSeparateEXT(sr,dr,sa,da);else GL11.glBlendFunc(sr,dr);}
    public static void tryBlendFuncSeparate(SourceFactor sr,DestFactor dr,SourceFactor sa,DestFactor da){tryBlendFuncSeparate(sr.factor,dr.factor,sa.factor,da.factor);}
    public static void setActiveTexture(int unit){if(GLContext.getCapabilities().OpenGL13)GL13.glActiveTexture(unit);else ARBMultitexture.glActiveTextureARB(unit);}
    public static void bindTexture(int texture){GL11.glBindTexture(GL11.GL_TEXTURE_2D,texture);}
    public static void glTexParameteri(int target,int name,int value){GL11.glTexParameteri(target,name,value);}
    public static void glTexEnvi(int target,int name,int value){GL11.glTexEnvi(target,name,value);}
    public static void glTexEnv(int target,int name,FloatBuffer value){GL11.glTexEnv(target,name,value);}
    public static void setFog(FogMode mode){GL11.glFogi(GL11.GL_FOG_MODE,mode.capabilityId);}
    public static void setFogDensity(float value){GL11.glFogf(GL11.GL_FOG_DENSITY,value);}public static void setFogStart(float value){GL11.glFogf(GL11.GL_FOG_START,value);}public static void setFogEnd(float value){GL11.glFogf(GL11.GL_FOG_END,value);}
    public static void clear(int mask){GL11.glClear(mask);}
    public static void enableBlendProfile(Profile profile){color(1,1,1,0.15f);depthMask(false);enableBlend();blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);alphaFunc(GL11.GL_GREATER,1.0f/255.0f);}
    public static void disableBlendProfile(Profile profile){disableBlend();alphaFunc(GL11.GL_GREATER,0.1f);depthMask(true);}
    public static void enableOutlineMode(int argb){
        FloatBuffer color=BufferUtils.createFloatBuffer(4);color.put(new float[]{((argb>>16)&255)/255.0f,((argb>>8)&255)/255.0f,(argb&255)/255.0f,((argb>>>24)&255)/255.0f}).flip();
        glTexEnv(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_COLOR,color);glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,GL13.GL_COMBINE);
        glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_COMBINE_RGB,GL11.GL_REPLACE);glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_SOURCE0_RGB,GL13.GL_CONSTANT);glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_OPERAND0_RGB,GL11.GL_SRC_COLOR);
        glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_COMBINE_ALPHA,GL11.GL_REPLACE);glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_SOURCE0_ALPHA,GL11.GL_TEXTURE);glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_OPERAND0_ALPHA,GL11.GL_SRC_ALPHA);
    }
    public static void disableOutlineMode(){glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,GL11.GL_MODULATE);glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_COMBINE_RGB,GL11.GL_MODULATE);glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_COMBINE_ALPHA,GL11.GL_MODULATE);glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_SOURCE0_RGB,GL11.GL_TEXTURE);glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_SOURCE0_ALPHA,GL11.GL_TEXTURE);glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_OPERAND0_RGB,GL11.GL_SRC_COLOR);glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_OPERAND0_ALPHA,GL11.GL_SRC_ALPHA);}
}
