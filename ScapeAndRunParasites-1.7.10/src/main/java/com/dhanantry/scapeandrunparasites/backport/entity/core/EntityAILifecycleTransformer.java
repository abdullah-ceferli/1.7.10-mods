package com.dhanantry.scapeandrunparasites.backport.entity.core;
import net.minecraft.launchwrapper.IClassTransformer;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
/** Run the retained modern callback at the native EntityLiving constructor boundary, before subclass initializers. */
public final class EntityAILifecycleTransformer implements IClassTransformer {
    private static final String HOOK="com/dhanantry/scapeandrunparasites/backport/entity/ai/EntityAILifecycle";
    @Override public byte[] transform(String name,String transformedName,byte[] bytes){
        if(bytes==null||!"net.minecraft.entity.EntityLiving".equals(transformedName))return bytes;
        ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);MethodNode constructor=null;
        for(Object o:node.methods){MethodNode m=(MethodNode)o;if(m.name.equals("<init>")&&m.desc.equals("(Lnet/minecraft/world/World;)V"))constructor=m;}
        if(constructor==null)throw new IllegalStateException("SRP lifecycle: native constructor signature changed");
        int present=0,returns=0;for(AbstractInsnNode i=constructor.instructions.getFirst();i!=null;i=i.getNext()){if(i instanceof MethodInsnNode&&((MethodInsnNode)i).owner.equals(HOOK)){MethodInsnNode call=(MethodInsnNode)i;if(call.getOpcode()!=Opcodes.INVOKESTATIC||!call.name.equals("constructed")||!call.desc.equals("(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/world/World;)V"))throw new IllegalStateException("SRP lifecycle: existing callback signature changed");present++;}if(i.getOpcode()==Opcodes.RETURN)returns++;}
        if(present==1&&returns==1)return bytes;if(present!=0||returns!=1)throw new IllegalStateException("SRP lifecycle: constructor callback shape changed");
        for(AbstractInsnNode i=constructor.instructions.getFirst();i!=null;i=i.getNext())if(i.getOpcode()==Opcodes.RETURN){
            InsnList code=new InsnList();code.add(new VarInsnNode(Opcodes.ALOAD,0));code.add(new VarInsnNode(Opcodes.ALOAD,1));code.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"constructed","(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/world/World;)V",false));constructor.instructions.insertBefore(i,code);
        }
        ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);node.accept(writer);return writer.toByteArray();
    }
}
