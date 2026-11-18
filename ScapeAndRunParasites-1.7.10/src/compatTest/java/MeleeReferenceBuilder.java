package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.util.*;import java.util.jar.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.commons.*;

/** Keep the original melee decision instructions; substitute only documented native boundaries. */
public final class MeleeReferenceBuilder {
    static final String OWNER="net/minecraft/entity/ai/EntityAIAttackMelee";
    static final String RESULT="dev/srpbackport/tests/reference/EntityAIAttackMelee";
    static final String BRIDGE="dev/srpbackport/tests/MeleeReferenceAccess";
    public static void main(String[] args) throws Exception {
        final Properties names=new Properties();try(InputStream in=new FileInputStream(args[1])){names.load(in);}
        final Map<String,String> fields=new HashMap<String,String>();
        String[] fieldNames={"field_75443_a","world","field_75441_b","attacker","field_75439_d","attackTick","field_75440_e","speedTowardsTarget","field_75437_f","longMemory","field_75438_g","path","field_75445_i","delayCounter","field_151497_i","targetX","field_151495_j","targetY","field_151496_k","targetZ","field_188493_g","attackInterval"};
        for(int i=0;i<fieldNames.length;i+=2)fields.put(fieldNames[i],fieldNames[i+1]);
        Remapper mapping=new Remapper(){
            @Override public String map(String name){
                if(name.equals(OWNER))return RESULT;
                if(name.equals("net/minecraft/pathfinding/Path"))return "net/minecraft/pathfinding/PathEntity";
                if(name.equals("net/minecraft/util/math/AxisAlignedBB"))return "net/minecraft/util/AxisAlignedBB";
                if(name.equals("net/minecraft/util/math/BlockPos"))return "com/dhanantry/scapeandrunparasites/backport/math/BlockPos";
                return name;
            }
            @Override public String mapFieldName(String owner,String name,String desc){
                if(owner.equals(OWNER))return fields.containsKey(name)?fields.get(name):name;
                String mapped=names.getProperty(name,name);return mapped.equals("world")?"worldObj":mapped;
            }
            @Override public String mapMethodName(String owner,String name,String desc){
                if(owner.equals(OWNER)&&name.equals("func_190102_a"))return "checkAndPerformAttack";
                if(owner.equals(OWNER)&&name.equals("func_179512_a"))return "getAttackReachSqr";
                String value=names.getProperty(name,name);
                if(owner.equals("net/minecraft/pathfinding/PathNavigate")&&value.equals("clearPath"))return "clearPathEntity";
                return value.equals("shouldContinueExecuting")?"continueExecuting":value;
            }
        };
        ClassNode node=new ClassNode();
        try(JarFile jar=new JarFile(args[0]);InputStream in=jar.getInputStream(jar.getJarEntry(OWNER+".class"))){new ClassReader(in).accept(node,ClassReader.SKIP_DEBUG|ClassReader.EXPAND_FRAMES);}
        Map<String,Integer> substitutions=new TreeMap<String,Integer>();
        for(Object item:node.methods){MethodNode method=(MethodNode)item;
            for(AbstractInsnNode instruction=method.instructions.getFirst();instruction!=null;){
                AbstractInsnNode next=instruction.getNext();
                if(instruction instanceof FieldInsnNode&&((FieldInsnNode)instruction).owner.equals("net/minecraft/util/EnumHand")){
                    FieldInsnNode field=(FieldInsnNode)instruction;
                    if(field.getOpcode()!=Opcodes.GETSTATIC||!field.name.equals("MAIN_HAND"))throw new AssertionError("Unexpected swing hand");
                    method.instructions.remove(instruction);count(substitutions,"main-hand operand");
                }else if(instruction instanceof MethodInsnNode){
                    MethodInsnNode call=(MethodInsnNode)instruction;
                    if(call.name.equals("func_180485_d"))staticCall(call,"home","(Lnet/minecraft/entity/EntityCreature;Lnet/minecraft/util/math/BlockPos;)Z",substitutions);
                    else if(call.name.equals("func_174813_aQ"))staticCall(call,"box","(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/AxisAlignedBB;",substitutions);
                    else if(call.name.equals("func_175149_v"))staticCall(call,"damageImmuneNonCreative","(Lnet/minecraft/entity/player/EntityPlayer;)Z",substitutions);
                    else if(call.name.equals("func_184812_l_"))staticCall(call,"creative","(Lnet/minecraft/entity/player/EntityPlayer;)Z",substitutions);
                    else if(call.name.equals("func_184609_a")){
                        call.name="swingItem";call.desc="()V";count(substitutions,"native swing");
                    }
                }
                instruction=next;
            }
        }
        if(!substitutions.toString().equals("{box=3, creative=2, damageImmuneNonCreative=2, home=1, main-hand operand=1, native swing=1}"))throw new AssertionError("Original melee boundaries changed: "+substitutions);
        ClassWriter out=new ClassWriter(ClassWriter.COMPUTE_MAXS);node.accept(new RemappingClassAdapter(out,mapping));
        Path target=Paths.get(args[2],RESULT+".class");Files.createDirectories(target.getParent());Files.write(target,out.toByteArray());
        System.out.println("Retained original melee decision instructions; native boundary substitutions: "+substitutions);
    }
    static void count(Map<String,Integer> values,String key){values.put(key,values.containsKey(key)?values.get(key)+1:1);}
    static void staticCall(MethodInsnNode call,String name,String desc,Map<String,Integer> counts){call.setOpcode(Opcodes.INVOKESTATIC);call.owner=BRIDGE;call.name=name;call.desc=desc;call.itf=false;count(counts,name);}
}
