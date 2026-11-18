package com.pockyl.handson_delight.client;

import java.nio.FloatBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

/** Explicit texture restoration for desktop OpenGL and GL4ES's partial attribute stack. */
final class RenderState implements AutoCloseable {
    private static final int[] ENV={GL11.GL_TEXTURE_ENV_MODE,GL13.GL_COMBINE_RGB,GL13.GL_COMBINE_ALPHA,
        GL13.GL_SOURCE0_RGB,GL13.GL_SOURCE1_RGB,GL13.GL_SOURCE2_RGB,
        GL13.GL_SOURCE0_ALPHA,GL13.GL_SOURCE1_ALPHA,GL13.GL_SOURCE2_ALPHA,
        GL13.GL_OPERAND0_RGB,GL13.GL_OPERAND1_RGB,GL13.GL_OPERAND2_RGB,
        GL13.GL_OPERAND0_ALPHA,GL13.GL_OPERAND1_ALPHA,GL13.GL_OPERAND2_ALPHA,
        GL13.GL_RGB_SCALE,GL11.GL_ALPHA_SCALE};
    static final class Texture implements AutoCloseable {
        final int unit,binding;final boolean enabled;
        final int[] values=new int[ENV.length];
        final FloatBuffer color=BufferUtils.createFloatBuffer(4),matrix=BufferUtils.createFloatBuffer(16);
        Texture(int unit){
            this.unit=unit;OpenGlHelper.setActiveTexture(unit);
            binding=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);enabled=GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
            for(int i=0;i<ENV.length;i++)values[i]=GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV,ENV[i]);
            GL11.glGetTexEnv(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_COLOR,color);
            GL11.glGetFloat(GL11.GL_TEXTURE_MATRIX,matrix);
        }
        public void close(){
            int mode=GL11.glGetInteger(GL11.GL_MATRIX_MODE);OpenGlHelper.setActiveTexture(unit);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,binding);
            if(enabled)GL11.glEnable(GL11.GL_TEXTURE_2D);else GL11.glDisable(GL11.GL_TEXTURE_2D);
            // Invoke the public setters: copying GL4ES's shadow state does not rebuild its shader state.
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,GL11.GL_REPLACE);
            for(int i=1;i<ENV.length;i++)GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,ENV[i],values[i]);
            color.rewind();GL11.glTexEnv(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_COLOR,color);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,values[0]);
            GL11.glMatrixMode(GL11.GL_TEXTURE);matrix.rewind();GL11.glLoadMatrix(matrix);GL11.glMatrixMode(mode);
        }
    }
    private final int active=GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE),client=GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE);
    private final int matrixMode=GL11.glGetInteger(GL11.GL_MATRIX_MODE);
    private final int src=GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB),dst=GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
    private final int srcAlpha=GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA),dstAlpha=GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
    private final Texture base,lightmap,extra;
    private boolean closed;
    private RenderState(){
        base=new Texture(OpenGlHelper.defaultTexUnit);lightmap=new Texture(OpenGlHelper.lightmapTexUnit);
        extra=active!=base.unit&&active!=lightmap.unit?new Texture(active):null;
        OpenGlHelper.setActiveTexture(active);
        // GL4ES does not save the combine sources/operands in GL_TEXTURE_BIT.
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS&~GL11.GL_TEXTURE_BIT);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
    static RenderState capture(){return new RenderState();}
    public void close(){if(closed)return;closed=true;
        GL11.glPopAttrib();base.close();lightmap.close();if(extra!=null)extra.close();
        OpenGlHelper.glBlendFunc(src,dst,srcAlpha,dstAlpha);
        OpenGlHelper.setActiveTexture(active);OpenGlHelper.setClientActiveTexture(client);GL11.glMatrixMode(matrixMode);
    }
    /** The source cookbook's one-third opacity, without depending on glPopAttrib for combiners. */
    static Texture fade(){
        Texture saved=new Texture(OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_MODE,GL13.GL_COMBINE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_COMBINE_RGB,GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_SOURCE0_RGB,GL11.GL_TEXTURE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_SOURCE1_RGB,GL13.GL_PRIMARY_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_OPERAND0_RGB,GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_OPERAND1_RGB,GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_COMBINE_ALPHA,GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_SOURCE0_ALPHA,GL11.GL_TEXTURE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_SOURCE1_ALPHA,GL13.GL_CONSTANT);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_OPERAND0_ALPHA,GL11.GL_SRC_ALPHA);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_OPERAND1_ALPHA,GL11.GL_SRC_ALPHA);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL13.GL_RGB_SCALE,1);GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,GL11.GL_ALPHA_SCALE,1);
        FloatBuffer color=BufferUtils.createFloatBuffer(4);color.put(new float[]{1,1,1,.333333F}).flip();
        GL11.glTexEnv(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_COLOR,color);return saved;
    }
}
