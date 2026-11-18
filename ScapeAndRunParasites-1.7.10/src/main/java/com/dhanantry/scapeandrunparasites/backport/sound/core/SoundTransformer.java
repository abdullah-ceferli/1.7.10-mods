package com.dhanantry.scapeandrunparasites.backport.sound.core;
import net.minecraft.launchwrapper.IClassTransformer;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
/** Narrow native callbacks: per-playback event categories, float normalization, and SRP entity sounds. */
public final class SoundTransformer implements IClassTransformer {
    private static final String ROOT="com/dhanantry/scapeandrunparasites/backport/sound/";
    private static final String AUDIO="net/minecraft/client/audio/";
    @Override public byte[] transform(String name,String transformedName,byte[] bytes){
        if(bytes==null)return null;
        boolean handler="net.minecraft.client.audio.SoundHandler".equals(transformedName),manager="net.minecraft.client.audio.SoundManager".equals(transformedName);
        boolean entity="net.minecraft.entity.Entity".equals(transformedName)||"net.minecraft.entity.player.EntityPlayer".equals(transformedName);
        if(!handler&&!manager&&!entity)return bytes;
        ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);
        for(Object o:c.methods)for(AbstractInsnNode i=((MethodNode)o).instructions.getFirst();i!=null;i=i.getNext())
            if(i instanceof MethodInsnNode&&((MethodInsnNode)i).owner.startsWith(ROOT))return bytes;
        int edits=0;
        if(handler){
            String registry=null;for(Object o:c.fields){FieldNode f=(FieldNode)o;if(f.desc.equals("L"+AUDIO+"SoundRegistry;"))registry=f.name;}
            if(registry==null)throw new IllegalStateException("SRP sound registry signature changed");
            for(Object o:c.methods){MethodNode m=(MethodNode)o;if((m.name.equals("getSound")||m.name.equals("func_147680_a"))&&m.desc.equals("(Lnet/minecraft/util/ResourceLocation;)L"+AUDIO+"SoundEventAccessorComposite;"))
                for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i.getOpcode()==Opcodes.ARETURN){
                    InsnList a=new InsnList();a.add(new VarInsnNode(Opcodes.ALOAD,0));a.add(new FieldInsnNode(Opcodes.GETFIELD,c.name,registry,"L"+AUDIO+"SoundRegistry;"));a.add(new VarInsnNode(Opcodes.ALOAD,1));a.add(new VarInsnNode(Opcodes.ALOAD,0));
                    a.add(new MethodInsnNode(Opcodes.INVOKESTATIC,ROOT+"client/SoundAliases","resolve","(L"+AUDIO+"SoundEventAccessorComposite;L"+AUDIO+"SoundRegistry;Lnet/minecraft/util/ResourceLocation;L"+AUDIO+"SoundHandler;)L"+AUDIO+"SoundEventAccessorComposite;",false));m.instructions.insertBefore(i,a);edits++;
                }
            }
            if(edits!=1)throw new IllegalStateException("SRP sound lookup changed: "+edits);
        }else if(manager){
            String gain=null;for(Object o:c.methods){MethodNode m=(MethodNode)o;if(m.desc.equals("(L"+AUDIO+"SoundCategory;)F"))gain=m.name;}
            if(gain==null)throw new IllegalStateException("SRP sound category gain signature changed");
            for(Object o:c.methods){MethodNode m=(MethodNode)o;boolean pitch=m.desc.equals("(L"+AUDIO+"ISound;L"+AUDIO+"SoundPoolEntry;)F");boolean volume=m.desc.equals("(L"+AUDIO+"ISound;L"+AUDIO+"SoundPoolEntry;L"+AUDIO+"SoundCategory;)F");if(!pitch&&!volume)continue;
                String soundValue=null,entryValue=null;for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;if(call.owner.equals(AUDIO+"ISound")&&call.desc.equals("()F"))soundValue=call.name;if(call.owner.equals(AUDIO+"SoundPoolEntry")&&call.desc.equals("()D"))entryValue=call.name;}if(soundValue==null||entryValue==null)throw new IllegalStateException("SRP native sound value access changed");
                InsnList a=new InsnList();LabelNode nativeBody=new LabelNode();a.add(new VarInsnNode(Opcodes.ALOAD,1));a.add(new MethodInsnNode(Opcodes.INVOKESTATIC,ROOT+"client/SoundAliases","isAdapted","(L"+AUDIO+"ISound;)Z",false));a.add(new JumpInsnNode(Opcodes.IFEQ,nativeBody));
                a.add(new VarInsnNode(Opcodes.ALOAD,1));a.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,AUDIO+"ISound",soundValue,"()F",true));
                a.add(new VarInsnNode(Opcodes.ALOAD,2));a.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,AUDIO+"SoundPoolEntry",entryValue,"()D",false));a.add(new InsnNode(Opcodes.D2F));
                if(volume){a.add(new VarInsnNode(Opcodes.ALOAD,0));a.add(new VarInsnNode(Opcodes.ALOAD,3));a.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,c.name,gain,"(L"+AUDIO+"SoundCategory;)F",false));}
                a.add(new MethodInsnNode(Opcodes.INVOKESTATIC,ROOT+"SoundMath",pitch?"pitch":"volume",pitch?"(FF)F":"(FFF)F",false));a.add(new InsnNode(Opcodes.FRETURN));a.add(nativeBody);a.add(new FrameNode(Opcodes.F_SAME,0,null,0,null));m.instructions.insert(a);edits++;
            }if(edits!=2)throw new IllegalStateException("SRP sound normalizers changed: "+edits);
        }else{
            for(Object o:c.methods){MethodNode m=(MethodNode)o;if((m.name.equals("playSound")||m.name.equals("func_85030_a"))&&m.desc.equals("(Ljava/lang/String;FF)V")){
                InsnList a=new InsnList();LabelNode nativeBody=new LabelNode();a.add(new VarInsnNode(Opcodes.ALOAD,0));a.add(new VarInsnNode(Opcodes.ALOAD,1));a.add(new VarInsnNode(Opcodes.FLOAD,2));a.add(new VarInsnNode(Opcodes.FLOAD,3));
                a.add(new MethodInsnNode(Opcodes.INVOKESTATIC,ROOT+"SoundAccess","intercept","(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)Z",false));a.add(new JumpInsnNode(Opcodes.IFEQ,nativeBody));a.add(new InsnNode(Opcodes.RETURN));a.add(nativeBody);a.add(new FrameNode(Opcodes.F_SAME,0,null,0,null));m.instructions.insert(a);edits++;
            }}if(edits!=1&&"net.minecraft.entity.Entity".equals(transformedName))throw new IllegalStateException("SRP entity sound callback changed: "+edits);
        }
        if(edits==0)return bytes;ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);return writer.toByteArray();
    }
}
