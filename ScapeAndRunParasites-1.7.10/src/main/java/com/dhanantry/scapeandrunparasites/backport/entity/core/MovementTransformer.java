package com.dhanantry.scapeandrunparasites.backport.entity.core;
import net.minecraft.launchwrapper.IClassTransformer;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import java.util.*;
/** Native state, movement callback, root NBT and narrowly selected gravity subtraction boundaries. */
public final class MovementTransformer implements IClassTransformer {
    private static final String P="com/dhanantry/scapeandrunparasites/backport/entity/movement/",STATE=P+"EntityMotionState",MARK="srp$motionPatched";
    private static boolean named(String name,String mcp,String srg){return name.equals(mcp)||name.equals(srg);}
    private static MethodNode find(ClassNode c,String mcp,String srg,String desc){for(Object o:c.methods){MethodNode m=(MethodNode)o;if(named(m.name,mcp,srg)&&m.desc.equals(desc))return m;}throw new IllegalStateException("SRP motion: native method changed: "+mcp+desc);}
    private static int calls(MethodNode m,String owner,String name){int n=0;for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;if(call.owner.equals(owner)&&call.name.equals(name))n++;}return n;}
    private static void accessor(ClassNode c,String suffix,String descriptor){
        String field="srp$"+suffix;c.fields.add(new FieldNode(Opcodes.ACC_PRIVATE|Opcodes.ACC_SYNTHETIC,field,descriptor,null,null));Type t=Type.getType(descriptor);
        MethodNode get=new MethodNode(Opcodes.ACC_PUBLIC|Opcodes.ACC_FINAL|Opcodes.ACC_SYNTHETIC,"srp$get"+suffix,"()"+descriptor,null,null);
        get.instructions.add(new VarInsnNode(Opcodes.ALOAD,0));get.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,c.name,field,descriptor));get.instructions.add(new InsnNode(t.getOpcode(Opcodes.IRETURN)));c.methods.add(get);
        MethodNode set=new MethodNode(Opcodes.ACC_PUBLIC|Opcodes.ACC_FINAL|Opcodes.ACC_SYNTHETIC,"srp$set"+suffix,"("+descriptor+")V",null,null);
        set.instructions.add(new VarInsnNode(Opcodes.ALOAD,0));set.instructions.add(new VarInsnNode(t.getOpcode(Opcodes.ILOAD),1));set.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD,c.name,field,descriptor));set.instructions.add(new InsnNode(Opcodes.RETURN));c.methods.add(set);
    }
    private static void nbtHook(MethodNode m,String name,String callback){
        int count=0;for(AbstractInsnNode i=m.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;
            if(call.name.equals(callback)&&call.desc.equals("(Lnet/minecraft/nbt/NBTTagCompound;)V")){
                InsnList code=new InsnList();code.add(new VarInsnNode(Opcodes.ALOAD,0));code.add(new VarInsnNode(Opcodes.ALOAD,1));code.add(new MethodInsnNode(Opcodes.INVOKESTATIC,P+"GravityAccess",name,"(Lnet/minecraft/entity/Entity;Lnet/minecraft/nbt/NBTTagCompound;)V",false));m.instructions.insertBefore(i,code);count++;
            }
        }if(count!=1)throw new IllegalStateException("SRP gravity: native NBT callback changed: "+name+" "+count);
    }
    @Override public byte[] transform(String name,String transformedName,byte[] bytes){
        if(bytes==null)return null;boolean entity="net.minecraft.entity.Entity".equals(transformedName);
        int expected="net.minecraft.entity.EntityLivingBase".equals(transformedName)?3:
            Arrays.asList("net.minecraft.entity.item.EntityItem","net.minecraft.entity.item.EntityXPOrb","net.minecraft.entity.item.EntityTNTPrimed","net.minecraft.entity.item.EntityFallingBlock","net.minecraft.entity.projectile.EntityArrow","net.minecraft.entity.projectile.EntityThrowable").contains(transformedName)?1:0;
        if(!entity&&expected==0)return bytes;
        ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);boolean exists=false;for(Object o:c.fields)if(((FieldNode)o).name.equals(MARK))exists=true;
        if(entity){
            MethodNode move=find(c,"moveEntity","func_70091_d","(DDD)V"),write=find(c,"writeToNBT","func_70109_d","(Lnet/minecraft/nbt/NBTTagCompound;)V"),read=find(c,"readFromNBT","func_70020_e","(Lnet/minecraft/nbt/NBTTagCompound;)V");
            if(exists){int access=0;for(Object o:c.methods)if(((MethodNode)o).name.startsWith("srp$"))access++;if(!c.interfaces.contains(STATE)||access!=15||calls(move,P+"MovementAccess","dispatch")!=1||calls(write,P+"GravityAccess","write")!=1||calls(read,P+"GravityAccess","read")!=1)throw new IllegalStateException("SRP motion: partial native hooks");return bytes;}
            MethodNode blocks=find(c,"func_145775_I","func_145775_I","()V"),writeChild=find(c,"writeEntityToNBT","func_70014_b","(Lnet/minecraft/nbt/NBTTagCompound;)V"),readChild=find(c,"readEntityFromNBT","func_70037_a","(Lnet/minecraft/nbt/NBTTagCompound;)V");
            c.interfaces.add(STATE);accessor(c,"NoGravity","Z");accessor(c,"GravityRevision","J");accessor(c,"MoveDepth","I");accessor(c,"BypassMove","Z");accessor(c,"MoverType","L"+P+"MoverType;");accessor(c,"PistonDeltas","[D");accessor(c,"PistonTick","J");
            MethodNode bridge=new MethodNode(Opcodes.ACC_PUBLIC|Opcodes.ACC_FINAL|Opcodes.ACC_SYNTHETIC,"srp$blockCollisions","()V",null,null);bridge.instructions.add(new VarInsnNode(Opcodes.ALOAD,0));bridge.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,c.name,blocks.name,"()V",false));bridge.instructions.add(new InsnNode(Opcodes.RETURN));c.methods.add(bridge);
            int sneaks=0;for(AbstractInsnNode i=move.instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;if(named(call.name,"isSneaking","func_70093_af")&&call.desc.equals("()Z")){call.setOpcode(Opcodes.INVOKESTATIC);call.owner=P+"MovementAccess";call.name="sneaking";call.desc="(Lnet/minecraft/entity/Entity;)Z";call.itf=false;sneaks++;}}
            if(sneaks!=1)throw new IllegalStateException("SRP motion: native sneak gate changed: "+sneaks);
            InsnList dispatch=new InsnList();LabelNode nativeBody=new LabelNode();dispatch.add(new VarInsnNode(Opcodes.ALOAD,0));dispatch.add(new VarInsnNode(Opcodes.DLOAD,1));dispatch.add(new VarInsnNode(Opcodes.DLOAD,3));dispatch.add(new VarInsnNode(Opcodes.DLOAD,5));dispatch.add(new MethodInsnNode(Opcodes.INVOKESTATIC,P+"MovementAccess","dispatch","(Lnet/minecraft/entity/Entity;DDD)Z",false));dispatch.add(new JumpInsnNode(Opcodes.IFEQ,nativeBody));dispatch.add(new InsnNode(Opcodes.RETURN));dispatch.add(nativeBody);dispatch.add(new FrameNode(Opcodes.F_SAME,0,null,0,null));move.instructions.insert(dispatch);
            nbtHook(write,"write",writeChild.name);nbtHook(read,"read",readChild.name);
        }else{
            MethodNode travel=expected==3?find(c,"moveEntityWithHeading","func_70612_e","(FF)V"):find(c,"onUpdate","func_70071_h_","()V");
            if(exists){if(calls(travel,P+"GravityAccess","subtract")!=expected)throw new IllegalStateException("SRP gravity: partial physics hooks");return bytes;}
            int count=0;for(AbstractInsnNode i=travel.instructions.getFirst();i!=null;){AbstractInsnNode next=i.getNext();
                if(i.getOpcode()==Opcodes.DSUB){AbstractInsnNode field=next;while(field!=null&&(field.getOpcode()<0||field.getOpcode()==Opcodes.DUP2_X1))field=field.getNext();
                    if(field instanceof FieldInsnNode&&field.getOpcode()==Opcodes.PUTFIELD&&named(((FieldInsnNode)field).name,"motionY","field_70181_x")&&((FieldInsnNode)field).desc.equals("D")){
                        travel.instructions.insertBefore(i,new VarInsnNode(Opcodes.ALOAD,0));travel.instructions.set(i,new MethodInsnNode(Opcodes.INVOKESTATIC,P+"GravityAccess","subtract","(DDLnet/minecraft/entity/Entity;)D",false));count++;
                    }
                }i=next;
            }if(count!=expected)throw new IllegalStateException("SRP gravity: native subtraction sites changed for "+transformedName+": "+count);
        }
        c.fields.add(new FieldNode(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC|Opcodes.ACC_FINAL|Opcodes.ACC_SYNTHETIC,MARK,"Z",null,Boolean.TRUE));
        ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);return writer.toByteArray();
    }
}
