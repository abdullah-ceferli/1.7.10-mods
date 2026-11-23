package com.pockyl.handson_delight.launch;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Hooks the four native station types without replacing their registered blocks or inventories. */
public final class StationTransformer implements IClassTransformer, Opcodes {
    private static final String P="vectorwing.farmersdelight.legacy.", H="com/pockyl/handson_delight/StationHooks";
    public byte[] transform(String name,String mapped,byte[] bytes){
        if(bytes==null)return bytes;
        if(mapped.equals(P+"Stacks")){
            ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);int changes=0;
            for(Object o:c.methods){MethodNode m=(MethodNode)o;if(m.name.equals("resolve")&&m.desc.equals("(Ljava/lang/String;)Lnet/minecraft/item/ItemStack;"))for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext())if(n.getOpcode()==ARETURN){InsnList i=new InsnList();i.add(new VarInsnNode(ALOAD,0));i.add(new MethodInsnNode(INVOKESTATIC,"com/pockyl/handson_delight/FoodCompatibility","resolved","(Lnet/minecraft/item/ItemStack;Ljava/lang/String;)Lnet/minecraft/item/ItemStack;",false));m.instructions.insertBefore(n,i);changes++;}}
            if(changes==0)throw new IllegalStateException("Missing native ingredient resolver");ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);return w.toByteArray();
        }
        boolean nei=mapped.equals("codechicken.nei.recipe.ShapedRecipeHandler")||mapped.equals("codechicken.nei.recipe.ShapelessRecipeHandler")||mapped.equals(P+"integration.nei.FoundationHandler");
        if(nei){ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);for(Object o:c.methods){MethodNode m=(MethodNode)o;if(m.name.equals("loadCraftingRecipes")||m.name.equals("loadUsageRecipes"))for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext())if(n.getOpcode()==RETURN){InsnList ins=new InsnList();ins.add(new VarInsnNode(ALOAD,0));ins.add(new MethodInsnNode(INVOKESTATIC,"com/pockyl/handson_delight/compat/nei/NEIHooks","filter","(Ljava/lang/Object;)V",false));m.instructions.insertBefore(n,ins);}}ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);return w.toByteArray();}
        if(mapped.equals(P+"client.NativeModels")){
            ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);int hooks=0;
            for(Object o:c.methods){MethodNode m=(MethodNode)o;if(m.name.equals("renderWorldBlock")){
                LabelNode keep=new LabelNode();InsnList i=new InsnList();i.add(new VarInsnNode(ALOAD,5));i.add(new MethodInsnNode(INVOKESTATIC,"com/pockyl/handson_delight/client/SceneModels","replaces","(Lnet/minecraft/block/Block;)Z",false));i.add(new JumpInsnNode(IFEQ,keep));i.add(new InsnNode(ICONST_1));i.add(new InsnNode(IRETURN));i.add(keep);i.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(i);hooks++;
            }}
            if(hooks!=1)throw new IllegalStateException("Unsupported native station model renderer");ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);return w.toByteArray();
        }
        if(!mapped.startsWith(P))return bytes;
        String simple=mapped.substring(P.length());
        if(!simple.equals("InventoryTile")&&!simple.equals("CookingPotTile")&&!simple.equals("GrillingTile")&&!simple.equals("CuttingBoardTile")&&!simple.equals("CuttingBoard")&&!simple.equals("SourceCrafting")&&!simple.equals("NativeShapeless"))return bytes;
        ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);int changes=0;
        for(Object o:c.methods){MethodNode m=(MethodNode)o;
            if((simple.equals("SourceCrafting")||simple.equals("NativeShapeless"))&&is(m,"matches","func_77569_a")){
                LabelNode run=new LabelNode();InsnList ins=new InsnList();ins.add(new VarInsnNode(ALOAD,0));ins.add(new VarInsnNode(ALOAD,1));ins.add(new MethodInsnNode(INVOKESTATIC,"com/pockyl/handson_delight/RecipeRules","allowed","(Ljava/lang/Object;Lnet/minecraft/inventory/InventoryCrafting;)Z",false));ins.add(new JumpInsnNode(IFNE,run));ins.add(new InsnNode(ICONST_0));ins.add(new InsnNode(IRETURN));ins.add(run);ins.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(ins);changes++;
            }
            if(simple.equals("InventoryTile") && (is(m,"readFromNBT","func_145839_a")||is(m,"writeToNBT","func_145841_b"))){
                String hook=is(m,"readFromNBT","func_145839_a")?"read":"write";
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext())if(n.getOpcode()==RETURN){
                    InsnList i=new InsnList();i.add(new VarInsnNode(ALOAD,0));i.add(new VarInsnNode(ALOAD,1));
                    i.add(new MethodInsnNode(INVOKESTATIC,H,hook,"(Ljava/lang/Object;Lnet/minecraft/nbt/NBTTagCompound;)V",false));m.instructions.insertBefore(n,i);
                }changes++;
            }
            if((simple.equals("CookingPotTile")||simple.equals("GrillingTile")) && is(m,"updateEntity","func_145845_h")){
                LabelNode run=new LabelNode();InsnList i=new InsnList();i.add(new VarInsnNode(ALOAD,0));
                i.add(new MethodInsnNode(INVOKESTATIC,H,simple.equals("CookingPotTile")?"pot":"grill","(Ljava/lang/Object;)Z",false));
                i.add(new JumpInsnNode(IFEQ,run));i.add(new InsnNode(RETURN));i.add(run);i.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(i);changes++;
            }
            if(simple.equals("CuttingBoardTile")&&m.name.equals("drop")){
                LabelNode nativeDrop=new LabelNode();InsnList i=new InsnList();i.add(new VarInsnNode(ALOAD,0));i.add(new VarInsnNode(ALOAD,1));i.add(new MethodInsnNode(INVOKESTATIC,H,"boardDrop","(Ljava/lang/Object;Lnet/minecraft/item/ItemStack;)Z",false));i.add(new JumpInsnNode(IFEQ,nativeDrop));i.add(new InsnNode(RETURN));i.add(nativeDrop);i.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(i);changes++;
            }
            if(simple.equals("CuttingBoardTile") && is(m,"getInventoryStackLimit","func_70297_j_")){
                for(AbstractInsnNode n=m.instructions.getFirst();n!=null;n=n.getNext())if(n.getOpcode()==ICONST_1){m.instructions.set(n,new IntInsnNode(BIPUSH,64));break;}changes++;
            }
            if(simple.equals("CuttingBoard") && is(m,"breakBlock","func_149749_a")){
                InsnList i=new InsnList();i.add(new VarInsnNode(ALOAD,1));for(int v=2;v<=4;v++)i.add(new VarInsnNode(ILOAD,v));
                i.add(new MethodInsnNode(INVOKESTATIC,H,"breakBoard","(Lnet/minecraft/world/World;III)V",false));m.instructions.insert(i);changes++;
            }
        }
        int expected=simple.equals("InventoryTile")||simple.equals("CuttingBoardTile")?2:1;
        if(changes!=expected)throw new IllegalStateException("Unsupported Farmer's Delight station bytecode: "+mapped+" ("+changes+"/"+expected+")");
        ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);return w.toByteArray();
    }
    private static boolean is(MethodNode m,String a,String b){return m.name.equals(a)||m.name.equals(b);}
}
