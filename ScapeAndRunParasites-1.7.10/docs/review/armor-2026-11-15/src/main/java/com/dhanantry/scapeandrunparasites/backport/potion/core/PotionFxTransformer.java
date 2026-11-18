package com.dhanantry.scapeandrunparasites.backport.potion.core;

import java.util.*;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Post-deobfuscation native hooks. Never attach visibility to the shared rejected/accepted return. */
public final class PotionFxTransformer implements IClassTransformer {
    private static final String TARGET="net.minecraft.entity.EntityLivingBase";
    private static final String HOOK="com/dhanantry/scapeandrunparasites/backport/potion/PotionFxHooks";
    private static final String EFFECT="Lnet/minecraft/potion/PotionEffect;";
    private static final String DESC="(Lnet/minecraft/entity/EntityLivingBase;"+EFFECT+")V";

    private static boolean named(String actual,String mcp,String srg){return actual.equals(mcp)||actual.equals(srg);}
    private static InsnList hook(String name){
        InsnList code=new InsnList();code.add(new VarInsnNode(Opcodes.ALOAD,0));code.add(new VarInsnNode(Opcodes.ALOAD,1));
        code.add(new MethodInsnNode(Opcodes.INVOKESTATIC,HOOK,name,DESC,false));return code;
    }
    @Override public byte[] transform(String name,String transformedName,byte[] bytes) {
        if(bytes==null||!TARGET.equals(transformedName))return bytes;
        ClassNode type=new ClassNode();new ClassReader(bytes).accept(type,0);
        MethodNode add=null,finished=null;
        for(Object item:type.methods){MethodNode method=(MethodNode)item;
            if(named(method.name,"addPotionEffect","func_70690_d")&&method.desc.equals("("+EFFECT+")V"))add=method;
            if(named(method.name,"onFinishedPotionEffect","func_70688_c")&&method.desc.equals("("+EFFECT+")V"))finished=method;
        }
        if(add==null||finished==null)throw new IllegalStateException("SRP potion visibility: native target signatures changed");
        int appliedExisting=0,finishedExisting=0;
        for(AbstractInsnNode i=add.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;if(call.owner.equals(HOOK)&&call.name.equals("applied"))appliedExisting++;}
        for(AbstractInsnNode i=finished.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;if(call.owner.equals(HOOK)&&call.name.equals("finished"))finishedExisting++;}
        if(appliedExisting==2&&finishedExisting==1)return bytes;
        if(appliedExisting!=0||finishedExisting!=0)throw new IllegalStateException("SRP potion visibility: partial pre-existing native hooks");
        List<MethodInsnNode> callbacks=new ArrayList<MethodInsnNode>();int added=0,changed=0;
        for(AbstractInsnNode i=add.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){
            MethodInsnNode call=(MethodInsnNode)i;
            if(!call.owner.equals(TARGET.replace('.','/')))continue;
            if(named(call.name,"onNewPotionEffect","func_70670_a")&&call.desc.equals("("+EFFECT+")V")){callbacks.add(call);added++;}
            if(named(call.name,"onChangedPotionEffect","func_70695_b")&&call.desc.equals("("+EFFECT+"Z)V")){callbacks.add(call);changed++;}
        }
        if(added!=1||changed!=1)throw new IllegalStateException("SRP potion visibility: expected one accepted insert and one accepted combine callback, got "+added+"/"+changed);
        // Map insertion/combination has completed here. The pending callback arguments stay on the stack.
        // Updating before virtual callbacks matches modern state visibility and preserves nested application order.
        for(MethodInsnNode callback:callbacks)add.instructions.insertBefore(callback,hook("applied"));
        finished.instructions.insert(hook("finished"));
        // The added snippets have zero net stack effect and no new control-flow joins.
        // Preserve existing frames; COMPUTE_FRAMES could load the class recursively during transformation.
        ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);type.accept(writer);
        System.out.println("[SRP] Potion visibility: installed two accepted-application hooks and one finish hook");
        return writer.toByteArray();
    }
}
