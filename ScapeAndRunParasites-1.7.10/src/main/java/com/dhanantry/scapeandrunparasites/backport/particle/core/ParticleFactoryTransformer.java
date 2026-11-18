package com.dhanantry.scapeandrunparasites.backport.particle.core;
import net.minecraft.launchwrapper.IClassTransformer;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
/** Reuse native constructors while scoped modern calls supply their own distance/settings decisions. */
public final class ParticleFactoryTransformer implements IClassTransformer {
    private static final String HOOK="com/dhanantry/scapeandrunparasites/backport/particle/ParticleRenderContext";
    @Override public byte[] transform(String name,String transformedName,byte[] bytes) {
        if(bytes==null||!"net.minecraft.client.renderer.RenderGlobal".equals(transformedName))return bytes;
        ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);MethodNode method=null;
        for(Object o:node.methods){MethodNode m=(MethodNode)o;if((m.name.equals("doSpawnParticle")||m.name.equals("func_72726_b"))&&m.desc.equals("(Ljava/lang/String;DDDDDD)Lnet/minecraft/client/particle/EntityFX;"))method=m;}
        if(method==null)throw new IllegalStateException("SRP particles: native factory signature changed");
        int existing=0,settings=0,ranges=0;
        for(AbstractInsnNode i=method.instructions.getFirst();i!=null;i=i.getNext()) {
            if(i instanceof MethodInsnNode&&((MethodInsnNode)i).owner.equals(HOOK))existing++;
        }
        if(existing==2)return bytes;
        if(existing!=0)throw new IllegalStateException("SRP particles: partial factory hooks");
        for(AbstractInsnNode i=method.instructions.getFirst();i!=null;i=i.getNext()) {
            if(i instanceof FieldInsnNode){FieldInsnNode field=(FieldInsnNode)i;
                if(field.owner.equals("net/minecraft/client/settings/GameSettings")&&(field.name.equals("particleSetting")||field.name.equals("field_74362_aa"))&&field.desc.equals("I")){
                    method.instructions.insert(i,new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"setting","(I)I",false));settings++;
                }
            }
            if(i instanceof LdcInsnNode&&Double.valueOf(16.0).equals(((LdcInsnNode)i).cst)){
                method.instructions.insert(i,new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,"range","(D)D",false));ranges++;
            }
        }
        if(settings!=1||ranges!=1)throw new IllegalStateException("SRP particles: native factory filters changed: "+settings+"/"+ranges);
        ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);node.accept(writer);return writer.toByteArray();
    }
}
