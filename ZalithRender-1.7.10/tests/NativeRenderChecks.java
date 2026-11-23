package dev.zahin.zalithrender.test;

import dev.zahin.zalithrender.core.ChunkAccess;
import dev.zahin.zalithrender.core.TessAccess;
import dev.zahin.zalithrender.core.FrameAccess;
import dev.zahin.zalithrender.core.RenderTransformer;
import dev.zahin.zalithrender.render.RenderHooks;
import dev.zahin.zalithrender.render.Settings;
import dev.zahin.zalithrender.render.FrameHooks;
import dev.zahin.zalithrender.render.UploadBudget;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.Arrays;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

public final class NativeRenderChecks {
    private static int checks;
    private static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);}
    private static IntBuffer ints(int... values){IntBuffer b=ByteBuffer.allocateDirect(values.length*4).order(ByteOrder.nativeOrder()).asIntBuffer();b.put(values);b.flip();return b;}
    private static int color(int r,int g,int b,int a){return ByteOrder.nativeOrder()==ByteOrder.LITTLE_ENDIAN?r|(g<<8)|(b<<16)|(a<<24):(r<<24)|(g<<16)|(b<<8)|a;}
    private static final class Chunk implements ChunkAccess {
        final int id;final int x,y,z;
        Chunk(int x,int y,int z){id=GL11.glGenLists(3);this.x=x;this.y=y;this.z=z;}
        public int zr$list(){return id;}public int zr$x(){return x;}public int zr$y(){return y;}public int zr$z(){return z;}
    }
    private static final class Tess implements TessAccess {
        final int flags;final int[] raw=new int[32];
        Tess(int flags){this.flags=flags;
            float[] x={3,11,11,3},y={3,3,11,11},u={0,1,1,0},v={0,0,1,1};
            for(int i=0;i<4;i++){int p=i*8;raw[p]=Float.floatToRawIntBits(x[i]);raw[p+1]=Float.floatToRawIntBits(y[i]);raw[p+2]=Float.floatToRawIntBits(0);
                raw[p+3]=Float.floatToRawIntBits(u[i]);raw[p+4]=Float.floatToRawIntBits(v[i]);raw[p+5]=color(80+i*40,190-i*20,90+i*25,170);raw[p+6]=0x007f0000;raw[p+7]=(int)u[i]|((int)v[i]<<16);}
        }
        public int[] zr$raw(){return raw;}public int zr$index(){return 32;}public int zr$count(){return 4;}public int zr$mode(){return GL11.GL_QUADS;}public int zr$flags(){return flags;}
    }
    private static void geometry(Tess tess) {
        GL11.glBegin(GL11.GL_QUADS);
        for(int i=0;i<4;i++){int p=i*8;int c=tess.raw[p+5];
            if((tess.flags&1)!=0)GL11.glTexCoord2f(Float.intBitsToFloat(tess.raw[p+3]),Float.intBitsToFloat(tess.raw[p+4]));
            if((tess.flags&8)!=0)GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1,tess.raw[p+7]&65535,tess.raw[p+7]>>>16);
            if((tess.flags&2)!=0){if(ByteOrder.nativeOrder()==ByteOrder.LITTLE_ENDIAN)GL11.glColor4ub((byte)c,(byte)(c>>>8),(byte)(c>>>16),(byte)(c>>>24));else GL11.glColor4ub((byte)(c>>>24),(byte)(c>>>16),(byte)(c>>>8),(byte)c);}
            if((tess.flags&4)!=0)GL11.glNormal3f(0,0,1);
            GL11.glVertex3f(Float.intBitsToFloat(tess.raw[p]),Float.intBitsToFloat(tess.raw[p+1]),Float.intBitsToFloat(tess.raw[p+2]));
        }
        GL11.glEnd();
    }
    private static void compile(Chunk chunk,Tess tess,int pass,int mode) {
        RenderHooks.enterUpdate(chunk);RenderHooks.begin(chunk,pass);
        GL11.glNewList(chunk.id+pass,GL11.GL_COMPILE);
        GL11.glPushMatrix();GL11.glTranslatef(chunk.x,chunk.y,chunk.z);GL11.glTranslatef(-8,-8,-8);GL11.glScalef(1.000001F,1.000001F,1.000001F);GL11.glTranslatef(8,8,8);
        if(mode==1)RenderHooks.checkBlock(new Object());
        RenderHooks.capture(tess);if(mode==2)RenderHooks.capture(tess);
        geometry(tess);GL11.glPopMatrix();GL11.glEndList();RenderHooks.finish(chunk);RenderHooks.leaveUpdate();
    }
    private static byte[] pixels(){ByteBuffer b=ByteBuffer.allocateDirect(32*32*4);GL11.glReadPixels(0,0,32,32,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,b);byte[] a=new byte[b.capacity()];b.get(a);return a;}
    private static void clear(){GL11.glClearColor(0.07F,0.11F,0.17F,1);GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);GL11.glColor4f(0.8F,0.9F,0.7F,0.65F);}
    private static void compare(IntBuffer lists,String label) {
        clear();GL11.glCallLists(lists);byte[] normal=pixels();clear();int p=lists.position(),l=lists.limit();RenderHooks.callLists(lists);byte[] cached=pixels();
        check(Arrays.equals(normal,cached),"Framebuffer mismatch: "+label);
        check(lists.position()==p&&lists.limit()==l,"Display-list input cursor changed: "+label);
        check(GL11.glGetError()==GL11.GL_NO_ERROR,"GL error: "+label);
    }
    private static int texture(int unit){GL13.glActiveTexture(unit);int id=GL11.glGenTextures();GL11.glBindTexture(GL11.GL_TEXTURE_2D,id);
        ByteBuffer data=ByteBuffer.allocateDirect(16);data.put(new byte[]{(byte)255,60,60,(byte)255,60,(byte)255,60,(byte)255,60,60,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255});data.flip();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,0,GL11.GL_RGBA,2,2,0,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,data);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_NEAREST);GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST);GL11.glEnable(GL11.GL_TEXTURE_2D);return id;}
    private static void actualTessellator(java.nio.file.Path api)throws Exception {
        String name="net.minecraft.client.renderer.Tessellator";
        byte[] source;
        try(java.util.zip.ZipFile zip=new java.util.zip.ZipFile(api.toFile());java.io.InputStream input=zip.getInputStream(zip.getEntry(name.replace('.','/')+".class"));java.io.ByteArrayOutputStream output=new java.io.ByteArrayOutputStream()) {
            byte[] data=new byte[8192];int n;while((n=input.read(data))!=-1)output.write(data,0,n);source=output.toByteArray();
        }
        final byte[] transformed=new RenderTransformer().transform(name,name,source);
        ClassLoader loader=new ClassLoader(NativeRenderChecks.class.getClassLoader()) {
            @Override protected synchronized Class<?> loadClass(String type,boolean resolve)throws ClassNotFoundException {
                if(!type.equals("net.minecraft.client.renderer.Tessellator"))return super.loadClass(type,resolve);
                Class<?> result=findLoadedClass(type);if(result==null)result=defineClass(type,transformed,0,transformed.length);
                if(resolve)resolveClass(result);return result;
            }
        };
        Class<?> type=loader.loadClass(name);Object tess=type.getConstructor().newInstance();
        check(tess instanceof TessAccess,"Real Minecraft tessellator accessor did not link");
        Chunk chunk=new Chunk(0,0,0);int before=RenderHooks.cachedMeshes();
        RenderHooks.enterUpdate(chunk);RenderHooks.begin(chunk,0);GL11.glNewList(chunk.id,GL11.GL_COMPILE);
        GL11.glPushMatrix();GL11.glTranslatef(-8,-8,-8);GL11.glScalef(1.000001F,1.000001F,1.000001F);GL11.glTranslatef(8,8,8);
        type.getMethod("startDrawingQuads").invoke(tess);type.getMethod("setColorRGBA",int.class,int.class,int.class,int.class).invoke(tess,170,220,90,180);
        java.lang.reflect.Method vertex=type.getMethod("addVertexWithUV",double.class,double.class,double.class,double.class,double.class);
        vertex.invoke(tess,3.0,3.0,0.0,0.0,0.0);vertex.invoke(tess,11.0,3.0,0.0,1.0,0.0);vertex.invoke(tess,11.0,11.0,0.0,1.0,1.0);vertex.invoke(tess,3.0,11.0,0.0,0.0,1.0);
        type.getMethod("draw").invoke(tess);GL11.glPopMatrix();GL11.glEndList();RenderHooks.finish(chunk);RenderHooks.leaveUpdate();
        check(RenderHooks.cachedMeshes()==before+1,"Real Minecraft tessellator did not produce a cached mesh");compare(ints(chunk.id),"real Minecraft Tessellator.draw");RenderHooks.release(chunk);
    }
    private static void loseBuffer(int list)throws Exception {
        java.lang.reflect.Field field=RenderHooks.class.getDeclaredField("CACHE");field.setAccessible(true);Object cache=field.get(null);
        java.lang.reflect.Method get=cache.getClass().getDeclaredMethod("get",int.class);get.setAccessible(true);Object mesh=get.invoke(cache,list);
        java.lang.reflect.Field buffer=mesh.getClass().getDeclaredField("buffer");buffer.setAccessible(true);GL15.glDeleteBuffers(buffer.getInt(mesh));
    }
    private static final class GameplayFrame implements FrameAccess {
        public Object zr$world(){return this;}
        public int zr$limit(){return 260;}
        public boolean zr$vsync(){return false;}
        public boolean zr$skip(){return false;}
    }
    private static void pacedFrames(long window) {
        GameplayFrame client=new GameplayFrame();
        int previous=Settings.targetFps;
        for(int fps:new int[]{60,70}) {
            Settings.targetFps=fps;
            long started=System.nanoTime();
            for(int frame=0;frame<8;frame++) {
                FrameHooks.begin(client);clear();GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3f(3,3,0);GL11.glVertex3f(11,3,0);GL11.glVertex3f(11,11,0);GL11.glVertex3f(3,11,0);GL11.glEnd();
                FrameHooks.present();GLFW.glfwSwapBuffers(window);GLFW.glfwPollEvents();FrameHooks.vanillaSync(260);FrameHooks.end();
                check(GL11.glGetError()==GL11.GL_NO_ERROR,"Paced complete frame introduced a GL error");
            }
            long elapsed=System.nanoTime()-started;
            check(elapsed>=8L*1000000000L/fps,"Pacer counted multiple frames without the requested real interval");
            System.out.println("Complete-frame pacing smoke: target "+fps+", frames 8, elapsed "+elapsed/1000000.0+" ms");
        }
        Settings.targetFps=previous;
    }
    public static void main(String[] args)throws Exception {
        Checks.prepareHooks(Paths.get(args[1]));
        check(GLFW.glfwInit(),"GLFW initialization failed");GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE,GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR,2);GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR,1);
        long window=GLFW.glfwCreateWindow(32,32,"Chunk rendering checks",0,0);check(window!=0,"Compatibility context unavailable");
        try {
            GLFW.glfwMakeContextCurrent(window);GL.createCapabilities();
            org.lwjgl.opengl.GLContext.initCapabilities();
            System.out.println("Library: "+args[0]+"; GL: "+GL11.glGetString(GL11.GL_VERSION));
            GL11.glViewport(0,0,32,32);GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glLoadIdentity();GL11.glOrtho(0,16,0,16,-2,2);GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glLoadIdentity();
            GL11.glDisable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_CULL_FACE);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
            int texture0=texture(GL13.GL_TEXTURE0),texture1=texture(GL13.GL_TEXTURE1);GL13.glActiveTexture(GL13.GL_TEXTURE0);
            Settings.uploadBudgetNanos=0;Settings.uploadsPerFrame=32;RenderHooks.frameStart();
            Chunk[] chunks=new Chunk[16];
            for(int flags=0;flags<16;flags++) {
                chunks[flags]=new Chunk(flags%2,flags%3-1,0);compile(chunks[flags],new Tess(flags),0,0);
                check(RenderHooks.cachedMeshes()==flags+1,"Standard chunk was not cached: "+flags+"; "+RenderHooks.status());
                IntBuffer list=ints(-99,chunks[flags].id,-99);list.position(1);list.limit(2);compare(list,"attribute flags "+flags);
            }
            Chunk custom=new Chunk(0,0,0);compile(custom,new Tess(15),0,1);check(RenderHooks.cachedMeshes()==16,"Custom block must use vanilla rendering");compare(ints(custom.id),"custom fallback");
            Chunk multiple=new Chunk(0,0,0);compile(multiple,new Tess(15),0,2);check(RenderHooks.cachedMeshes()==16,"Multiple tessellator draws must use vanilla rendering");compare(ints(multiple.id),"multiple-draw fallback");
            compare(ints(custom.id,chunks[3].id,multiple.id,chunks[15].id),"mixed native and cached lists");
            compile(chunks[2],new Tess(2),1,0);compare(ints(chunks[2].id+1),"translucent pass");
            RenderHooks.begin(chunks[2],1);GL11.glNewList(chunks[2].id+1,GL11.GL_COMPILE);GL11.glPushMatrix();GL11.glTranslatef(chunks[2].x,chunks[2].y,0);GL11.glTranslatef(-8,-8,-8);GL11.glScalef(1.000001F,1.000001F,1.000001F);GL11.glTranslatef(8,8,8);RenderHooks.capture(new Tess(15));geometry(new Tess(15));GL11.glPopMatrix();GL11.glEndList();RenderHooks.finish(chunks[2]);compare(ints(chunks[2].id+1),"sorted pass replacement");
            int previousCount=RenderHooks.cachedMeshes();RenderHooks.release(chunks[2]);check(RenderHooks.cachedMeshes()==previousCount-2,"Chunk release did not remove both passes");
            GL11.glListBase(custom.id);compare(ints(0),"nonzero display-list base");GL11.glListBase(0);
            RenderHooks.resetLater();RenderHooks.frameStart();check(RenderHooks.cachedMeshes()==0&&RenderHooks.cachedBytes()==0,"World unload did not release cache");
            Settings.cacheBytes=256;Settings.maxMeshes=10;
            for(int i=0;i<3;i++){Chunk c=new Chunk(0,0,0);compile(c,new Tess(2),0,0);compare(ints(c.id),"bounded-memory cache "+i);}
            check(RenderHooks.cachedBytes()<=256&&RenderHooks.cachedMeshes()==2,"GPU cache exceeded byte bound");
            RenderHooks.resetLater();RenderHooks.frameStart();Settings.cacheBytes=32*1024*1024;Settings.maxMeshes=1;
            for(int i=0;i<3;i++){Chunk c=new Chunk(0,0,0);compile(c,new Tess(2),0,0);check(RenderHooks.cachedMeshes()==1,"GPU cache exceeded entry bound");compare(ints(c.id),"entry limit "+i);}
            RenderHooks.resetLater();Settings.maxMeshes=4096;Settings.uploadsPerFrame=1;RenderHooks.frameStart();
            Chunk first=new Chunk(0,0,0),second=new Chunk(0,0,0);compile(first,new Tess(2),0,0);compile(second,new Tess(2),0,0);
            check(RenderHooks.cachedMeshes()==1,"Per-frame upload bound ignored");compare(ints(first.id,second.id),"upload-bound fallback");
            RenderHooks.resetLater();Settings.uploadsPerFrame=8;Settings.uploadBudgetNanos=2000000L;RenderHooks.frameStart();
            java.lang.reflect.Field budgetField=RenderHooks.class.getDeclaredField("UPLOAD_BUDGET");budgetField.setAccessible(true);
            ((UploadBudget)budgetField.get(null)).charge(2000000L);
            Chunk exhausted=new Chunk(0,0,0);compile(exhausted,new Tess(15),0,0);
            check(RenderHooks.cachedMeshes()==0,"Time-exhausted frame still uploaded cache geometry");compare(ints(exhausted.id),"time-budget fallback retains geometry");
            Settings.uploadBudgetNanos=0;RenderHooks.frameStart();compile(exhausted,new Tess(15),0,0);
            check(RenderHooks.cachedMeshes()==1,"Cache allowance did not resume in the next frame");compare(ints(exhausted.id),"time-budget recovery");
            RenderHooks.worldHooks(true);RenderHooks.frameStart();check(RenderHooks.cachedMeshes()==0,"Custom world hooks did not clear accelerated geometry");compare(ints(first.id),"custom world-hook fallback");RenderHooks.worldHooks(false);RenderHooks.frameStart();
            Settings.uploadsPerFrame=8;Chunk state=new Chunk(0,0,0);compile(state,new Tess(15),0,0);
            int foreign=GL15.glGenBuffers();GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,foreign);GL15.glBufferData(GL15.GL_ARRAY_BUFFER,128,GL15.GL_STATIC_DRAW);GL13.glClientActiveTexture(GL13.GL_TEXTURE1);
            RenderHooks.callLists(ints(state.id));check(GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING)==foreign,"Array-buffer binding leaked");check(GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE)==GL13.GL_TEXTURE1,"Client texture unit leaked");
            GL13.glClientActiveTexture(GL13.GL_TEXTURE0);GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);GL15.glDeleteBuffers(foreign);
            loseBuffer(state.id);RenderHooks.frameStart();compare(ints(state.id),"lost graphics buffer fallback");check(RenderHooks.cachedMeshes()==0,"Lost buffer did not invalidate the cache");
            actualTessellator(Paths.get(args[1]));
            pacedFrames(window);
            RenderHooks.resetLater();RenderHooks.frameStart();GL11.glDeleteTextures(texture0);GL11.glDeleteTextures(texture1);
            check(GL11.glGetError()==GL11.GL_NO_ERROR,"Final GL state has an error");
            System.out.println("PASS: "+checks+" native pixel, fallback, sorting, memory and state checks");
        }finally{GLFW.glfwMakeContextCurrent(0);GLFW.glfwDestroyWindow(window);GLFW.glfwTerminate();}
    }
}
