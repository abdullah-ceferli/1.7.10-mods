package com.epherical.croptopia.legacy;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
public final class FishTransformer implements IClassTransformer {
    public static int transformedCalls;
    public byte[] transform(String name,String transformed,byte[] bytes){if(bytes==null||!transformed.equals("net.minecraft.entity.projectile.EntityFishHook"))return bytes;ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);int changes=0;for(Object method:node.methods){MethodNode m=(MethodNode)method;for(AbstractInsnNode instruction=m.instructions.getFirst();instruction!=null;instruction=instruction.getNext())if(instruction instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)instruction;if(call.owner.equals("net/minecraftforge/common/FishingHooks")&&call.name.equals("getRandomFishable")){call.owner="com/epherical/croptopia/legacy/HarvestEvents";changes++;}}}if(changes==0)throw new IllegalStateException("Native fishing boundary was not found");transformedCalls+=changes;ClassWriter writer=new ClassWriter(0);node.accept(writer);return writer.toByteArray();}
}
