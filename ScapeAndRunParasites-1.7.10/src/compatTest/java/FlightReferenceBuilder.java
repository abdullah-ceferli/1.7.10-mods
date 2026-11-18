package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.util.*;import java.util.jar.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.commons.*;
/** Original steering math, command state and all 25 original controller bodies with only native boundary adaptation. */
public final class FlightReferenceBuilder {
    static final String P="dev/srpbackport/tests/flight/reference/",HELPER="net/minecraft/entity/ai/EntityMoveHelper",MATH="net/minecraft/util/math/MathHelper";
    static byte[] bytes(JarFile jar,String name)throws Exception{
        try(InputStream in=jar.getInputStream(jar.getJarEntry(name+".class"))){ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] a=new byte[8192];int n;while((n=in.read(a))!=-1)out.write(a,0,n);return out.toByteArray();}
    }
    public static void main(String[] args)throws Exception{
        final Properties names=new Properties(),actors=new Properties();try(InputStream in=new FileInputStream(args[2])){names.load(in);}try(InputStream in=new FileInputStream(args[3])){actors.load(in);}
        final Set<String> controllers=new HashSet<String>();for(String actor:actors.stringPropertyNames())controllers.add(actor+"$AIMoveControl");
        Remapper mapping=new Remapper(){
            @Override public String map(String type){
                if(type.equals("native/EntityMoveHelper"))return HELPER;
                if(type.equals(HELPER))return P+"MoveHelper";if(type.equals(HELPER+"$Action"))return P+"MoveHelper$Action";
                if(type.equals(MATH))return P+"Math";if(type.equals("net/minecraft/util/math/AxisAlignedBB"))return "net/minecraft/util/AxisAlignedBB";
                if(actors.containsKey(type))return actors.getProperty(type);
                if(controllers.contains(type))return P+type.substring(type.lastIndexOf('/')+1).replace("$AIMoveControl","Controller");
                return type;
            }
            @Override public String mapFieldName(String owner,String name,String desc){return names.getProperty(name,name);}
            @Override public String mapMethodName(String owner,String name,String desc){return names.getProperty(name,name);}
        };
        try(JarFile game=new JarFile(args[0]);JarFile mod=new JarFile(args[1])){
            List<String> inputs=new ArrayList<String>(controllers);Collections.sort(inputs);inputs.add(HELPER);inputs.add(HELPER+"$Action");inputs.add(MATH);
            for(String name:inputs){
                ClassNode node=new ClassNode();new ClassReader(bytes(name.startsWith("com/")?mod:game,name)).accept(node,ClassReader.SKIP_DEBUG|ClassReader.EXPAND_FRAMES);
                if(name.equals(HELPER)){
                    node.superName="native/EntityMoveHelper";node.access|=Opcodes.ACC_ABSTRACT;
                    Set<String> keep=new HashSet<String>(Arrays.asList("<init>","func_75640_a","func_75638_b","func_75642_a","func_179917_d","func_179919_e","func_179918_f"));
                    for(Iterator<?> it=node.methods.iterator();it.hasNext();){MethodNode m=(MethodNode)it.next();if(!keep.contains(m.name))it.remove();}
                    for(Object o:node.methods){MethodNode m=(MethodNode)o;if(m.name.equals("<init>")){
                        for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode&&((MethodInsnNode)i).owner.equals("java/lang/Object")){
                            m.instructions.insertBefore(i,new VarInsnNode(Opcodes.ALOAD,1));((MethodInsnNode)i).owner="native/EntityMoveHelper";((MethodInsnNode)i).desc="(Lnet/minecraft/entity/EntityLiving;)V";
                        }
                    }}
                    node.methods.add(new MethodNode(Opcodes.ACC_PUBLIC|Opcodes.ACC_ABSTRACT,"func_75641_c","()V",null,null));
                }else if(name.equals(MATH)){
                    Set<String> keep=new HashSet<String>(Arrays.asList("<clinit>","func_181159_b","func_181161_i","func_76129_c","func_76133_a"));
                    for(Iterator<?> it=node.methods.iterator();it.hasNext();)if(!keep.contains(((MethodNode)it.next()).name))it.remove();
                }
                for(Object o:node.methods){MethodNode m=(MethodNode)o;
                    for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;
                        if(call.name.equals("func_174813_aQ")){call.setOpcode(Opcodes.INVOKESTATIC);call.owner="dev/srpbackport/tests/FlightReferenceAccess";call.name="box";call.desc="(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/AxisAlignedBB;";call.itf=false;}
                    }
                }
                ClassWriter out=new ClassWriter(ClassWriter.COMPUTE_MAXS);node.accept(new RemappingClassAdapter(out,mapping));Path target=Paths.get(args[4],mapping.map(name)+".class");Files.createDirectories(target.getParent());Files.write(target,out.toByteArray());
            }
        }
        System.out.println("Retained original math, helper command state and "+controllers.size()+" SRP controller class bodies.");
    }
}
