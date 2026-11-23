package dev.zahin.mobileperformance.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Early exits add no GL calls and leave renderer state unchanged on the skipped path. */
public final class MobileTransformer implements IClassTransformer,Opcodes {
    private static final String HOOK="dev/zahin/mobileperformance/client/Hooks";
    public byte[] transform(String name,String target,byte[] original){
        if(original==null)return null;
        String hook=null,method=null,suffix=null;boolean result=false;
        if(target.equals("net.minecraft.client.renderer.entity.RenderManager")){hook="entity";method="func_147939_a";suffix=";DDDFFZ)Z";result=true;}
        else if(target.equals("net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher")){hook="tile";method="func_147544_a";suffix=";F)V";}
        else if(target.equals("net.minecraft.client.particle.EffectRenderer")){hook="particle";method="func_78873_a";suffix=";)V";}
        else if(target.equals("net.minecraft.client.renderer.texture.TextureMap")){hook="animation";method="func_94248_c";suffix="()V";}
        else return original;
        try{
            ClassNode c=new ClassNode();new ClassReader(original).accept(c,0);int changed=0;
            for(Object o:c.methods){MethodNode m=(MethodNode)o;
                String deobf=hook.equals("tile")?"renderTileEntity":hook.equals("particle")?"addEffect":hook.equals("animation")?"updateAnimations":method;
                if(!(m.name.equals(method)||m.name.equals(deobf))||!m.desc.endsWith(suffix))continue;
                for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode&&((MethodInsnNode)n).owner.equals(HOOK)&&((MethodInsnNode)n).name.equals(hook))return original;
                InsnList code=new InsnList();if(!hook.equals("animation"))code.add(new VarInsnNode(ALOAD,1));
                code.add(new MethodInsnNode(INVOKESTATIC,HOOK,hook,hook.equals("animation")?"()Z":"(Ljava/lang/Object;)Z",false));
                LabelNode keep=new LabelNode();code.add(new JumpInsnNode(IFNE,keep));if(result)code.add(new InsnNode(ICONST_1));code.add(new InsnNode(result?IRETURN:RETURN));code.add(keep);code.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(code);changed++;
            }
            if(changed!=1){System.err.println("[MobilePerformance] Hook unavailable for "+target+"; native rendering retained");return original;}
            ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);System.out.println("[MobilePerformance] Hooked "+target);return writer.toByteArray();
        }catch(RuntimeException e){System.err.println("[MobilePerformance] Native rendering retained for "+target+": "+e);return original;}
    }
}
