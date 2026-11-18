package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.util.*;import java.util.jar.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.commons.*;
/** Retain the original AI instructions, adapting only native names, types and unavailable call boundaries. */
public final class RetaliationReferenceBuilder {
    static final String ORIGINAL="net/minecraft/entity/ai/",RESULT="dev/srpbackport/tests/reference/",BRIDGE="dev/srpbackport/tests/RetaliationReferenceAccess";
    public static void main(String[] args)throws Exception{
        final Properties names=new Properties();try(InputStream in=new FileInputStream(args[1])){names.load(in);}
        Remapper mapping=new Remapper(){
            @Override public String map(String type){
                if(type.equals(ORIGINAL+"EntityAITarget"))return RESULT+"EntityAITarget";
                if(type.equals(ORIGINAL+"EntityAIHurtByTarget"))return RESULT+"EntityAIHurtByTarget";
                if(type.equals("net/minecraft/util/math/AxisAlignedBB"))return "net/minecraft/util/AxisAlignedBB";
                if(type.equals("net/minecraft/util/math/MathHelper"))return "net/minecraft/util/MathHelper";
                if(type.equals("net/minecraft/util/math/BlockPos"))return "com/dhanantry/scapeandrunparasites/backport/math/BlockPos";
                if(type.equals("net/minecraft/pathfinding/Path"))return "net/minecraft/pathfinding/PathEntity";
                return type;
            }
            @Override public String mapFieldName(String owner,String name,String desc){
                String value=names.getProperty(name,name);if(value.equals("world"))return "worldObj";
                if(owner.equals("net/minecraft/entity/SharedMonsterAttributes")&&value.equals("FOLLOW_RANGE"))return "followRange";
                if(owner.equals("net/minecraft/pathfinding/PathPoint")){if(value.equals("x"))return "xCoord";if(value.equals("y"))return "yCoord";if(value.equals("z"))return "zCoord";}
                return value;
            }
            @Override public String mapMethodName(String owner,String name,String desc){
                String value=names.getProperty(name,name);
                if(owner.equals("net/minecraft/util/math/MathHelper")&&name.equals("func_76128_c"))return "floor_double";
                if(value.equals("getRevengeTimer"))return "func_142015_aE";
                if(value.equals("getRevengeTarget"))return "getAITarget";
                if(value.equals("shouldContinueExecuting"))return "continueExecuting";
                if(value.equals("getDistanceSq")&&desc.equals("(Lnet/minecraft/entity/Entity;)D"))return "getDistanceSqToEntity";
                if(owner.equals("net/minecraft/util/math/AxisAlignedBB")&&name.equals("func_72314_b"))return "expand";
                return value;
            }
        };
        try(JarFile jar=new JarFile(args[0])){
            for(String simple:new String[]{"EntityAITarget","EntityAIHurtByTarget"}){
                byte[] bytes;try(InputStream in=jar.getInputStream(jar.getJarEntry(ORIGINAL+simple+".class"))){ByteArrayOutputStream b=new ByteArrayOutputStream();byte[] a=new byte[8192];int n;while((n=in.read(a))!=-1)b.write(a,0,n);bytes=b.toByteArray();}
                ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,ClassReader.SKIP_DEBUG|ClassReader.EXPAND_FRAMES);
                for(Object obj:c.methods){MethodNode method=(MethodNode)obj;for(AbstractInsnNode i=method.instructions.getFirst();i!=null;){
                    AbstractInsnNode next=i.getNext();
                    if(i instanceof TypeInsnNode&&i.getOpcode()==Opcodes.NEW&&((TypeInsnNode)i).desc.equals("net/minecraft/util/math/AxisAlignedBB")){
                        if(next.getOpcode()!=Opcodes.DUP)throw new AssertionError("Original bounding-box allocation changed");next=next.getNext();method.instructions.remove(i.getNext());method.instructions.remove(i);
                    }else if(i instanceof MethodInsnNode){
                        MethodInsnNode call=(MethodInsnNode)i;
                        if(call.owner.equals("net/minecraft/util/math/AxisAlignedBB")&&call.name.equals("<init>")){
                            call.setOpcode(Opcodes.INVOKESTATIC);call.owner="net/minecraft/util/AxisAlignedBB";call.name="getBoundingBox";call.desc="(DDDDDD)Lnet/minecraft/util/AxisAlignedBB;";call.itf=false;
                        }else if(call.name.equals("func_180485_d")){
                            call.setOpcode(Opcodes.INVOKESTATIC);call.owner=BRIDGE;call.name="home";call.desc="(Lnet/minecraft/entity/EntityCreature;Lnet/minecraft/util/math/BlockPos;)Z";call.itf=false;
                        }else if(call.owner.equals("net/minecraft/entity/IEntityOwnable")&&call.name.equals("func_184753_b")){
                            call.setOpcode(Opcodes.INVOKESTATIC);call.owner=BRIDGE;call.name="ownerId";call.desc="(Lnet/minecraft/entity/IEntityOwnable;)Ljava/util/UUID;";call.itf=false;
                        }else if(call.owner.equals("net/minecraft/entity/IEntityOwnable")&&call.name.equals("func_70902_q")){
                            call.setOpcode(Opcodes.INVOKESTATIC);call.owner=BRIDGE;call.name="owner";call.desc="(Lnet/minecraft/entity/IEntityOwnable;)Lnet/minecraft/entity/EntityLivingBase;";call.itf=false;
                        }else if(call.name.equals("func_184191_r"))call.desc="(Lnet/minecraft/entity/EntityLivingBase;)Z";
                    }
                    i=next;
                }}
                ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(new RemappingClassAdapter(w,mapping));Path out=Paths.get(args[2],RESULT+simple+".class");Files.createDirectories(out.getParent());Files.write(out,w.toByteArray());
            }
        }
        ClassWriter probe=new ClassWriter(ClassWriter.COMPUTE_MAXS);String parent=RESULT+"EntityAITarget",child=RESULT+"TargetProbe";probe.visit(Opcodes.V1_8,Opcodes.ACC_PUBLIC,child,null,parent,null);
        MethodVisitor ctor=probe.visitMethod(Opcodes.ACC_PUBLIC,"<init>","(Lnet/minecraft/entity/EntityCreature;ZZ)V",null,null);ctor.visitCode();ctor.visitVarInsn(Opcodes.ALOAD,0);ctor.visitVarInsn(Opcodes.ALOAD,1);ctor.visitVarInsn(Opcodes.ILOAD,2);ctor.visitVarInsn(Opcodes.ILOAD,3);ctor.visitMethodInsn(Opcodes.INVOKESPECIAL,parent,"<init>","(Lnet/minecraft/entity/EntityCreature;ZZ)V",false);ctor.visitInsn(Opcodes.RETURN);ctor.visitMaxs(0,0);ctor.visitEnd();
        MethodVisitor run=probe.visitMethod(Opcodes.ACC_PUBLIC,"shouldExecute","()Z",null,null);run.visitCode();run.visitInsn(Opcodes.ICONST_1);run.visitInsn(Opcodes.IRETURN);run.visitMaxs(0,0);run.visitEnd();
        MethodVisitor suitable=probe.visitMethod(Opcodes.ACC_PUBLIC,"suitable","(Lnet/minecraft/entity/EntityLivingBase;Z)Z",null,null);suitable.visitCode();suitable.visitVarInsn(Opcodes.ALOAD,0);suitable.visitVarInsn(Opcodes.ALOAD,1);suitable.visitVarInsn(Opcodes.ILOAD,2);suitable.visitMethodInsn(Opcodes.INVOKESPECIAL,parent,"isSuitableTarget","(Lnet/minecraft/entity/EntityLivingBase;Z)Z",false);suitable.visitInsn(Opcodes.IRETURN);suitable.visitMaxs(0,0);suitable.visitEnd();probe.visitEnd();Files.write(Paths.get(args[2],child+".class"),probe.toByteArray());
        System.out.println("Original targeting and retaliation bytecode retained with native boundary substitutions.");
    }
}
