package com.dhanantry.scapeandrunparasites.backport.entity.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Registers the watched modern names in the native base attribute initialization, before NBT is read. */
public final class ArmorAttributeTransformer implements IClassTransformer {
    private static final String HOOK="com/dhanantry/scapeandrunparasites/backport/entity/ModernAttributes";
    @Override public byte[] transform(String name,String transformedName,byte[] bytes) {
        if(bytes==null||!"net.minecraft.entity.EntityLivingBase".equals(transformedName))return bytes;
        ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);
        MethodNode target=null;
        for(Object object:node.methods) {
            MethodNode method=(MethodNode)object;
            if((method.name.equals("applyEntityAttributes")||method.name.equals("func_110147_ax"))&&method.desc.equals("()V"))target=method;
        }
        if(target==null)throw new IllegalStateException("SRP armor: missing native applyEntityAttributes");
        int existing=0,returns=0;
        for(AbstractInsnNode i=target.instructions.getFirst();i!=null;i=i.getNext()) {
            if(i.getOpcode()==Opcodes.RETURN)returns++;
            if(i instanceof MethodInsnNode&&((MethodInsnNode)i).owner.equals(HOOK)&&((MethodInsnNode)i).name.equals("register"))existing++;
        }
        if(existing==1&&returns==1)return bytes;
        if(existing!=0||returns!=1)throw new IllegalStateException("SRP armor: unexpected native attribute method shape");
        for(AbstractInsnNode i=target.instructions.getFirst();i!=null;i=i.getNext())if(i.getOpcode()==Opcodes.RETURN) {
            InsnList call=new InsnList();call.add(new VarInsnNode(Opcodes.ALOAD,0));
            call.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"register","(Lnet/minecraft/entity/EntityLivingBase;)V",false));
            target.instructions.insertBefore(i,call);
        }
        ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);node.accept(writer);
        System.out.println("[SRP] Armor: registered native living-entity attribute hook");
        return writer.toByteArray();
    }
}
