package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.jar.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
import com.dhanantry.scapeandrunparasites.backport.entity.core.MovementTransformer;
public final class MovementTransformerChecks {
    public static void main(String[] args)throws Exception{
        String[] targets={"net.minecraft.entity.Entity","net.minecraft.entity.EntityLivingBase","net.minecraft.entity.item.EntityItem","net.minecraft.entity.item.EntityXPOrb","net.minecraft.entity.item.EntityTNTPrimed","net.minecraft.entity.item.EntityFallingBlock","net.minecraft.entity.projectile.EntityArrow","net.minecraft.entity.projectile.EntityThrowable"};
        List<String> report=new ArrayList<String>();MovementTransformer transformer=new MovementTransformer();
        try(JarFile jar=new JarFile(args[0])){for(String target:targets){
            byte[] original;try(InputStream in=jar.getInputStream(jar.getJarEntry(target.replace('.','/')+".class"))){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1)out.write(b,0,n);original=out.toByteArray();}
            byte[] changed=transformer.transform(target,target,original);if(Arrays.equals(changed,original)||!Arrays.equals(changed,transformer.transform(target,target,changed)))throw new AssertionError("Transform/idempotence "+target);
            ClassNode node=new ClassNode();new ClassReader(changed).accept(node,0);boolean removed=false;
            for(Object o:node.methods){MethodNode m=(MethodNode)o;for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode&&((MethodInsnNode)i).owner.endsWith("/GravityAccess")){m.instructions.remove(i);removed=true;break;}if(removed)break;}
            ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);node.accept(w);boolean rejected=false;try{transformer.transform(target,target,w.toByteArray());}catch(IllegalStateException expected){rejected=true;}if(!rejected)throw new AssertionError("Partial hooks accepted: "+target);
        }}
        report.add("PASS: All eight native target classes transform once, are idempotent and reject partial pre-existing hooks");report.add("Failed: 0");Files.write(Paths.get(args[1]),report,StandardCharsets.UTF_8);for(String line:report)System.out.println(line);
    }
}
