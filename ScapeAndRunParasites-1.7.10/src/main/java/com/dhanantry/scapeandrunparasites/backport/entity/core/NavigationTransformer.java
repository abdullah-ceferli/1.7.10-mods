package com.dhanantry.scapeandrunparasites.backport.entity.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Replace only the native navigator construction, before senses and AI goal initialization. */
public final class NavigationTransformer implements IClassTransformer {
    private static final String NAV="net/minecraft/pathfinding/PathNavigate";
    private static final String ACCESS="com/dhanantry/scapeandrunparasites/backport/navigation/NavigationAccess";
    private static final String ARGS="(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/world/World;)";
    private static AbstractInsnNode next(AbstractInsnNode node) { do {node=node.getNext();}while(node!=null&&node.getOpcode()<0);return node; }
    @Override public byte[] transform(String name,String transformedName,byte[] bytes) {
        if(bytes==null||!"net.minecraft.entity.EntityLiving".equals(transformedName))return bytes;
        ClassNode type=new ClassNode();new ClassReader(bytes).accept(type,0);MethodNode ctor=null;
        for(Object o:type.methods){MethodNode m=(MethodNode)o;if(m.name.equals("<init>")&&m.desc.equals("(Lnet/minecraft/world/World;)V"))ctor=m;}
        if(ctor==null)throw new IllegalStateException("Navigation constructor missing");
        int hooks=0,allocations=0;TypeInsnNode allocation=null;
        for(AbstractInsnNode i=ctor.instructions.getFirst();i!=null;i=i.getNext()) {
            if(i instanceof MethodInsnNode&&((MethodInsnNode)i).owner.equals(ACCESS)) {
                MethodInsnNode m=(MethodInsnNode)i;
                if(m.getOpcode()!=Opcodes.INVOKESTATIC||!m.name.equals("create")||!m.desc.equals(ARGS+"L"+NAV+";"))throw new IllegalStateException("Navigation hook signature changed");
                hooks++;
            }
            if(i.getOpcode()==Opcodes.NEW&&((TypeInsnNode)i).desc.equals(NAV)){allocation=(TypeInsnNode)i;allocations++;}
        }
        if(hooks==1&&allocations==0)return bytes;
        if(hooks!=0||allocations!=1)throw new IllegalStateException("Navigation allocation shape changed");
        AbstractInsnNode dup=next(allocation),owner=next(dup),world=next(owner),call=next(world);
        if(dup.getOpcode()!=Opcodes.DUP||!(owner instanceof VarInsnNode)||owner.getOpcode()!=Opcodes.ALOAD||((VarInsnNode)owner).var!=0||!(world instanceof VarInsnNode)||world.getOpcode()!=Opcodes.ALOAD||((VarInsnNode)world).var!=1||!(call instanceof MethodInsnNode))throw new IllegalStateException("Navigation constructor arguments changed");
        MethodInsnNode old=(MethodInsnNode)call;
        if(old.getOpcode()!=Opcodes.INVOKESPECIAL||!old.owner.equals(NAV)||!old.name.equals("<init>")||!old.desc.equals(ARGS+"V"))throw new IllegalStateException("Navigation constructor call changed");
        ctor.instructions.remove(allocation);ctor.instructions.remove(dup);
        ctor.instructions.set(call,new MethodInsnNode(Opcodes.INVOKESTATIC,ACCESS,"create",ARGS+"L"+NAV+";",false));
        ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);type.accept(writer);return writer.toByteArray();
    }
}
