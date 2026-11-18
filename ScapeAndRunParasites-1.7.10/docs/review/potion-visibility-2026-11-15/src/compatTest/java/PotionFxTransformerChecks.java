package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.util.*;import java.util.jar.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
import com.dhanantry.scapeandrunparasites.backport.potion.core.PotionFxTransformer;

/** Structural contract checks on actual native bytecode in both compile and runtime naming modes. */
public final class PotionFxTransformerChecks {
    static final String TARGET="net.minecraft.entity.EntityLivingBase",HOOK="com/dhanantry/scapeandrunparasites/backport/potion/PotionFxHooks";
    static byte[] nativeClass(String jar)throws Exception{try(JarFile z=new JarFile(jar);InputStream in=z.getInputStream(z.getJarEntry(TARGET.replace('.','/')+".class"))){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1)out.write(b,0,n);return out.toByteArray();}}
    static byte[] names(byte[] bytes,boolean breakShape){
        Map<String,String> names=new HashMap<String,String>();names.put("addPotionEffect","func_70690_d");names.put("onFinishedPotionEffect","func_70688_c");names.put("onNewPotionEffect","func_70670_a");names.put("onChangedPotionEffect","func_70695_b");
        ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);
        for(Object value:c.methods){MethodNode m=(MethodNode)value;if(names.containsKey(m.name))m.name=names.get(m.name);for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;if(names.containsKey(call.name)&&call.owner.equals(TARGET.replace('.','/')))call.name=names.get(call.name);if(breakShape&&call.name.equals("func_70695_b"))call.name="changed_native_shape";}}
        ClassWriter w=new ClassWriter(0);c.accept(w);return w.toByteArray();
    }
    static void check(byte[] transformed){ClassNode c=new ClassNode();new ClassReader(transformed).accept(c,0);int applied=0,finished=0;for(Object value:c.methods){MethodNode m=(MethodNode)value;for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;if(call.owner.equals(HOOK)){if(call.name.equals("applied"))applied++;if(call.name.equals("finished"))finished++;}}}if(applied!=2||finished!=1)throw new AssertionError("Native hook counts: "+applied+"/"+finished);}
    public static void main(String[] args)throws Exception{
        PotionFxTransformer t=new PotionFxTransformer();byte[] original=nativeClass(args[0]);List<String> report=new ArrayList<String>();
        for(boolean srg:new boolean[]{false,true}){byte[] input=srg?names(original,false):original;byte[] output=t.transform("irrelevant",TARGET,input);check(output);if(t.transform("irrelevant",TARGET,output)!=output)throw new AssertionError("Transformer is not idempotent");report.add("PASS: Actual native "+(srg?"SRG":"MCP")+" bytecode receives exactly two accepted-path hooks and one finish hook, idempotently");}
        if(t.transform("other","other",original)!=original||t.transform("other",TARGET,null)!=null)throw new AssertionError("Unrelated/null bytes changed");
        boolean refused=false;try{t.transform("irrelevant",TARGET,names(original,true));}catch(IllegalStateException expected){refused=true;}if(!refused)throw new AssertionError("Unexpected native control-flow shape accepted");
        report.add("PASS: Unrelated classes stay byte-identical and unexpected target shapes fail explicitly");report.add("Failed: 0");Files.write(Paths.get(args[1]),report,java.nio.charset.StandardCharsets.UTF_8);for(String line:report)System.out.println(line);
    }
}
