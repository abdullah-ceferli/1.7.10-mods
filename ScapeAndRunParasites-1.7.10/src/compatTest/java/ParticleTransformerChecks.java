package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.jar.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
import com.dhanantry.scapeandrunparasites.backport.particle.core.ParticleFactoryTransformer;
import com.dhanantry.scapeandrunparasites.backport.particle.ParticleRenderContext;
public final class ParticleTransformerChecks {
    static final String TARGET="net.minecraft.client.renderer.RenderGlobal",HOOK="com/dhanantry/scapeandrunparasites/backport/particle/ParticleRenderContext";
    static byte[] read(String jar)throws Exception{try(JarFile z=new JarFile(jar);InputStream in=z.getInputStream(z.getJarEntry(TARGET.replace('.','/')+".class"))){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1)out.write(b,0,n);return out.toByteArray();}}
    static byte[] rename(byte[] bytes,boolean corrupt){ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);for(Object o:c.methods){MethodNode m=(MethodNode)o;if(m.name.equals("doSpawnParticle"))m.name=corrupt?"unexpected_factory":"func_72726_b";for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof FieldInsnNode){FieldInsnNode f=(FieldInsnNode)i;if(f.name.equals("particleSetting"))f.name="field_74362_aa";}}ClassWriter w=new ClassWriter(0);c.accept(w);return w.toByteArray();}
    public static void main(String[] args)throws Exception {
        ParticleFactoryTransformer t=new ParticleFactoryTransformer();byte[] original=read(args[0]);List<String> report=new ArrayList<String>();
        for(boolean srg:new boolean[]{false,true}){byte[] input=srg?rename(original,false):original,out=t.transform("",TARGET,input);ClassNode c=new ClassNode();new ClassReader(out).accept(c,0);int calls=0;for(Object o:c.methods)for(AbstractInsnNode i=((MethodNode)o).instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode&&((MethodInsnNode)i).owner.equals(HOOK))calls++;if(calls!=2||t.transform("",TARGET,out)!=out)throw new AssertionError("Factory hooks/idempotence");}
        report.add("PASS: Actual MCP/SRG factory receives exactly two idempotent scoped filter hooks");
        boolean rejected=false;try{t.transform("",TARGET,rename(original,true));}catch(IllegalStateException expected){rejected=true;}if(!rejected||t.transform("","other",original)!=original)throw new AssertionError("Factory shape protection");report.add("PASS: Changed native factory signatures fail and unrelated classes remain byte-identical");
        if(ParticleRenderContext.range(16)!=16||ParticleRenderContext.setting(2)!=2)throw new AssertionError("Ordinary native calls changed");boolean first=ParticleRenderContext.enter();boolean nested=ParticleRenderContext.enter();ParticleRenderContext.leave(nested);if(ParticleRenderContext.setting(2)!=0||!Double.isInfinite(ParticleRenderContext.range(16)))throw new AssertionError("Nested scope lost");ParticleRenderContext.leave(first);if(ParticleRenderContext.setting(2)!=2||ParticleRenderContext.range(16)!=16)throw new AssertionError("Scope leaked");report.add("PASS: Nested modern factory scope restores native distance/settings decisions exactly");report.add("Failed: 0");Files.write(Paths.get(args[1]),report,StandardCharsets.UTF_8);for(String line:report)System.out.println(line);
    }
}
