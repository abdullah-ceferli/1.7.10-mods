package dev.srpbackport.tests;
import java.io.*;import java.lang.reflect.*;import java.nio.*;import java.util.*;
import org.lwjgl.BufferUtils;import org.lwjgl.opengl.*;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
/** Real offscreen GL contexts, original 1.12 state implementation versus the 1.7 adapter. */
public final class RenderingParityChecks {
    interface Step {void run(boolean original)throws Exception;}
    static List<Step> steps=new ArrayList<Step>();static List<String> labels=new ArrayList<String>();
    static void step(String name,Step step){labels.add(name);steps.add(step);}
    static void call(boolean original,String name,String srg,Object... args)throws Exception{
        Class<?>[] types=new Class<?>[args.length];Object[] values=args.clone();
        for(int i=0;i<args.length;i++){
            Object value=args[i];
            if(value instanceof Integer)types[i]=int.class;else if(value instanceof Float)types[i]=float.class;else if(value instanceof Double)types[i]=double.class;else if(value instanceof Boolean)types[i]=boolean.class;else if(value instanceof FloatBuffer)types[i]=FloatBuffer.class;
            else if(value instanceof Enum){Class<?> own=((Enum<?>)value).getDeclaringClass();if(original){Class<?> nativeType=FoundationParityChecks.ref("GlStateManager$"+own.getSimpleName());types[i]=nativeType;values[i]=null;for(Object e:nativeType.getEnumConstants())if(((Enum<?>)e).name().equals(((Enum<?>)value).name()))values[i]=e;if(values[i]==null)throw new AssertionError("Missing original enum "+value);}else types[i]=own;}
            else throw new AssertionError("Unknown GL argument "+value);
        }
        if(original)FoundationParityChecks.call(FoundationParityChecks.ref("GlStateManager"),srg,types,values);else GlStateManager.class.getMethod(name,types).invoke(null,values);
    }
    static String floats(int key,int count){FloatBuffer data=BufferUtils.createFloatBuffer(16);GL11.glGetFloat(key,data);StringBuilder b=new StringBuilder();for(int i=0;i<count;i++)b.append(Float.floatToIntBits(data.get(i))).append(',');return b.toString();}
    static Map<String,String> snapshot(){
        Map<String,String> values=new TreeMap<String,String>();
        for(int key:new int[]{GL11.GL_BLEND,GL11.GL_ALPHA_TEST,GL11.GL_DEPTH_TEST,GL11.GL_CULL_FACE,GL11.GL_LIGHTING,GL11.GL_COLOR_MATERIAL,GL11.GL_TEXTURE_2D,GL12.GL_RESCALE_NORMAL})values.put("enabled-"+key,Boolean.toString(GL11.glIsEnabled(key)));
        for(int key:new int[]{GL11.GL_MATRIX_MODE,GL13.GL_ACTIVE_TEXTURE,GL14.GL_BLEND_SRC_RGB,GL14.GL_BLEND_DST_RGB,GL14.GL_BLEND_SRC_ALPHA,GL14.GL_BLEND_DST_ALPHA,GL11.GL_ALPHA_TEST_FUNC,GL11.GL_DEPTH_FUNC,GL11.GL_FOG_MODE,GL11.GL_TEXTURE_BINDING_2D})values.put("integer-"+key,Integer.toString(GL11.glGetInteger(key)));
        values.put("depth-mask",Boolean.toString(GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)));
        for(int key:new int[]{GL11.GL_ALPHA_TEST_REF,GL11.GL_FOG_DENSITY,GL11.GL_FOG_START,GL11.GL_FOG_END})values.put("float-"+key,floats(key,1));
        values.put("color",floats(GL11.GL_CURRENT_COLOR,4));values.put("normal",floats(GL11.GL_CURRENT_NORMAL,3));values.put("modelview",floats(GL11.GL_MODELVIEW_MATRIX,16));
        for(int key:new int[]{GL11.GL_TEXTURE_ENV_MODE,GL13.GL_COMBINE_RGB,GL13.GL_COMBINE_ALPHA,GL13.GL_SOURCE0_RGB,GL13.GL_SOURCE0_ALPHA,GL13.GL_OPERAND0_RGB,GL13.GL_OPERAND0_ALPHA})values.put("texture-env-"+key,Integer.toString(GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV,key)));
        FloatBuffer color=BufferUtils.createFloatBuffer(16);GL11.glGetTexEnv(GL11.GL_TEXTURE_ENV,GL11.GL_TEXTURE_ENV_COLOR,color);for(int i=0;i<4;i++)values.put("outline-color-"+i,Integer.toString(Float.floatToIntBits(color.get(i))));
        int error=GL11.glGetError();if(error!=0)throw new AssertionError("GL error "+error);return values;
    }
    static List<Map<String,String>> sequence(boolean original)throws Exception{
        Pbuffer context=new Pbuffer(32,32,new PixelFormat(8,24,0),null,null);List<Map<String,String>> states=new ArrayList<Map<String,String>>();
        try{context.makeCurrent();if(original)Class.forName("cii").getMethod("a").invoke(null);for(Step s:steps){s.run(original);states.add(snapshot());}}finally{context.destroy();}return states;
    }
    public static void main(String[] args)throws Exception{
        try(InputStream input=new FileInputStream(args[0])){FoundationParityChecks.mappings.load(input);}
        step("depth/alpha/cull/lighting",o->{call(o,"enableDepth","func_179126_j");call(o,"depthMask","func_179132_a",false);call(o,"depthFunc","func_179143_c",GL11.GL_LEQUAL);call(o,"enableAlpha","func_179141_d");call(o,"alphaFunc","func_179092_a",GL11.GL_GREATER,0.2f);call(o,"enableCull","func_179089_o");call(o,"enableLighting","func_179145_e");call(o,"enableRescaleNormal","func_179091_B");call(o,"enableColorMaterial","func_179142_g");});
        step("matrix transformations",o->{call(o,"pushMatrix","func_179094_E");call(o,"translate","func_179109_b",0.25f,-0.5f,0.75f);call(o,"scale","func_179152_a",0.5f,0.75f,1.25f);call(o,"rotate","func_179114_b",38.7f,0.0f,1.0f,0.0f);});
        step("colors/normals/separate blending",o->{call(o,"color","func_179131_c",0.1f,0.7f,0.4f,0.8f);call(o,"glNormal3f","func_187432_a",0.3f,0.4f,0.5f);call(o,"enableBlend","func_179147_l");call(o,"tryBlendFuncSeparate","func_179120_a",GL11.GL_ONE,GL11.GL_ZERO,GL11.GL_ONE,GL11.GL_ZERO);});
        step("transparent-model profile",o->call(o,"enableBlendProfile","func_187408_a",GlStateManager.Profile.TRANSPARENT_MODEL));
        step("transparent-model profile restore",o->call(o,"disableBlendProfile","func_187440_b",GlStateManager.Profile.TRANSPARENT_MODEL));
        step("fog",o->{call(o,"setFog","func_187430_a",GlStateManager.FogMode.EXP2);call(o,"setFogDensity","func_179095_a",0.12f);call(o,"setFogStart","func_179102_b",3.0f);call(o,"setFogEnd","func_179153_c",27.0f);});
        step("colored outline",o->call(o,"enableOutlineMode","func_187431_e",0xAADD4477));
        step("outline restore",o->call(o,"disableOutlineMode","func_187417_n"));
        step("texture units/binding/parameters",o->{call(o,"setActiveTexture","func_179138_g",GL13.GL_TEXTURE1);call(o,"enableTexture2D","func_179098_w");call(o,"bindTexture","func_179144_i",2001);call(o,"glTexParameteri","func_187421_b",GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_NEAREST);});
        step("matrix and state restoration",o->{call(o,"setActiveTexture","func_179138_g",GL13.GL_TEXTURE0);call(o,"popMatrix","func_179121_F");call(o,"disableRescaleNormal","func_179101_C");call(o,"disableLighting","func_179140_f");call(o,"disableColorMaterial","func_179119_h");call(o,"disableCull","func_179129_p");call(o,"disableAlpha","func_179118_c");call(o,"disableDepth","func_179097_i");});
        List<Map<String,String>> original=sequence(true),own=sequence(false);List<String> report=new ArrayList<String>();int failed=0;
        for(int i=0;i<steps.size();i++){if(original.get(i).equals(own.get(i)))report.add("PASS: original 1.12.2 rendering state parity — "+labels.get(i));else{failed++;report.add("FAIL: "+labels.get(i));for(String k:original.get(i).keySet())if(!Objects.equals(original.get(i).get(k),own.get(i).get(k)))report.add("  "+k+": original="+original.get(i).get(k)+" port="+own.get(i).get(k));}}
        report.add("Offscreen contexts: real LWJGL 1.7.10 natives, 32x32 RGBA/depth buffers");report.add("Failed: "+failed);for(String line:report)System.out.println(line);try(PrintWriter writer=new PrintWriter(args[1],"UTF-8")){for(String line:report)writer.println(line);}if(failed!=0)System.exit(1);
    }
}
