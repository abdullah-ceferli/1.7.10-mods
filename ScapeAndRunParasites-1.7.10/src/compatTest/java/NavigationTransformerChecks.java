package dev.srpbackport.tests;
import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.jar.*;import java.io.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
import com.dhanantry.scapeandrunparasites.backport.entity.core.NavigationTransformer;
public final class NavigationTransformerChecks {
    static void check(boolean b,String why){if(!b)throw new AssertionError(why);}
    static ClassNode read(byte[] b){ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);return c;}
    static MethodNode ctor(ClassNode c){for(Object o:c.methods){MethodNode m=(MethodNode)o;if(m.name.equals("<init>")&&m.desc.equals("(Lnet/minecraft/world/World;)V"))return m;}throw new AssertionError("Constructor missing");}
    static byte[] write(ClassNode c){ClassWriter w=new ClassWriter(0);c.accept(w);return w.toByteArray();}
    public static void main(String[] args)throws Exception {
        byte[] original;try(JarFile jar=new JarFile(args[0]);InputStream in=jar.getInputStream(jar.getJarEntry("net/minecraft/entity/EntityLiving.class"));ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);original=out.toByteArray();}
        NavigationTransformer transformer=new NavigationTransformer();String target="net.minecraft.entity.EntityLiving";byte[] changed=transformer.transform(target,target,original);
        check(!Arrays.equals(original,changed),"No patch");check(transformer.transform(target,target,changed)==changed,"Not idempotent");check(transformer.transform("other","other",original)==original&&transformer.transform(target,target,null)==null,"Unrelated classes changed");
        MethodNode constructor=ctor(read(changed));int factory=-1,store=-1,senses=-1,index=0,count=0;
        for(AbstractInsnNode i=constructor.instructions.getFirst();i!=null;i=i.getNext(),index++) {
            if(i instanceof MethodInsnNode&&((MethodInsnNode)i).owner.endsWith("/NavigationAccess")){factory=index;count++;}
            if(i instanceof FieldInsnNode&&i.getOpcode()==Opcodes.PUTFIELD&&((FieldInsnNode)i).desc.equals("Lnet/minecraft/pathfinding/PathNavigate;"))store=index;
            if(i instanceof TypeInsnNode&&i.getOpcode()==Opcodes.NEW&&((TypeInsnNode)i).desc.equals("net/minecraft/entity/ai/EntitySenses"))senses=index;
        }
        check(count==1&&factory>=0&&store>factory&&senses>store,"Factory installation order");
        List<String> report=new ArrayList<String>();report.add("PASS: Exact native constructor replacement is idempotent, opt-in and stores the selected navigator before senses/AI initialization");
        int rejected=0;
        for(int mode=0;mode<3;mode++) {
            ClassNode node=read(mode==2?changed:original);MethodNode m=ctor(node);
            if(mode==0)node.methods.remove(m);
            if(mode==1)for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i.getOpcode()==Opcodes.NEW&&((TypeInsnNode)i).desc.endsWith("/PathNavigate")){((TypeInsnNode)i).desc="net/minecraft/pathfinding/Unexpected";break;}
            if(mode==2)for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode&&((MethodInsnNode)i).owner.endsWith("/NavigationAccess")){((MethodInsnNode)i).name="unexpected";break;}
            try{transformer.transform(target,target,write(node));}catch(IllegalStateException expected){rejected++;}
        }
        check(rejected==3,"Malformed transform silently accepted");report.add("PASS: Missing constructor, changed allocation and incompatible existing hook fail explicitly");report.add("Failed: 0");Files.write(Paths.get(args[1]),report,StandardCharsets.UTF_8);for(String line:report)System.out.println(line);
    }
}
