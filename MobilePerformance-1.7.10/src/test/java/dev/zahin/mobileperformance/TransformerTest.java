package dev.zahin.mobileperformance;

import dev.zahin.mobileperformance.core.MobileTransformer;
import java.io.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

public class TransformerTest {
    private byte[] resource(String type)throws IOException{try(InputStream in=getClass().getClassLoader().getResourceAsStream(type.replace('.','/')+".class")){assertNotNull(type,in);ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1)bytes.write(b,0,n);return bytes.toByteArray();}}
    @Test public void allFourNativeHooksVerifyAndAreIdempotent()throws Exception{
        for(String type:new String[]{"net.minecraft.client.renderer.entity.RenderManager","net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher","net.minecraft.client.particle.EffectRenderer","net.minecraft.client.renderer.texture.TextureMap"}){
            byte[] original=resource(type);MobileTransformer t=new MobileTransformer();byte[] patched=t.transform(type,type,original);assertNotSame(type,original,patched);
            ClassNode c=new ClassNode();new ClassReader(patched).accept(c,0);int hooks=0;
            for(Object o:c.methods){MethodNode m=(MethodNode)o;boolean added=false;for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode&&((MethodInsnNode)n).owner.equals("dev/zahin/mobileperformance/client/Hooks")){hooks++;added=true;}if(added)new Analyzer(new BasicVerifier()).analyze(c.name,m);}
            assertEquals(type,1,hooks);assertSame(patched,t.transform(type,type,patched));
        }
    }
    @Test public void unrelatedClassesRemainByteIdentical()throws Exception{byte[] b=resource("net.minecraft.entity.player.EntityPlayer");assertSame(b,new MobileTransformer().transform("x","net.minecraft.entity.player.EntityPlayer",b));assertNull(new MobileTransformer().transform("x","x",null));}
}
