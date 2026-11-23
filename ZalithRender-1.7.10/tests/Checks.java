package dev.zahin.zalithrender.test;

import dev.zahin.zalithrender.core.RenderTransformer;
import dev.zahin.zalithrender.render.IntTable;
import dev.zahin.zalithrender.render.RenderTypePolicy;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.CheckClassAdapter;

public final class Checks {
    private static final class OrdinaryOre extends net.minecraft.block.NativeBlockStub {}
    private static final class SpecialShape extends net.minecraft.block.NativeBlockStub {@Override public int getRenderType(){return 90;}}
    public static int checks;
    public static void check(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
    public static void prepareHooks(Path api) throws Exception {
        RenderTransformer transformer=new RenderTransformer();
        try(ZipFile zip=new ZipFile(api.toFile())) {
            for(String name:new String[]{"WorldRenderer","Tessellator","RenderBlocks","RenderList","Minecraft"}) {
                String type=name.equals("Minecraft")?"net.minecraft.client.Minecraft":"net.minecraft.client.renderer."+name;
                byte[] original=read(zip,type.replace('.','/')+".class");
                byte[] result=transformer.transform(type,type,original);
                check(!Arrays.equals(original,result),"No hook installed: "+type);
                new ClassReader(result).accept(new CheckClassAdapter(new ClassWriter(0),false),0);
                ClassNode before=new ClassNode(),after=new ClassNode();
                new ClassReader(original).accept(before,0);new ClassReader(result).accept(after,0);
                if(name.equals("Tessellator"))check(after.interfaces.contains("dev/zahin/zalithrender/core/TessAccess"),"Tessellator accessor missing");
                if(name.equals("WorldRenderer"))check(after.interfaces.contains("dev/zahin/zalithrender/core/ChunkAccess"),"Chunk accessor missing");
                if(name.equals("Minecraft"))check(after.interfaces.contains("dev/zahin/zalithrender/core/FrameAccess"),"Frame-loop accessors missing");
                int calls=0;
                for(MethodNode method:after.methods)for(AbstractInsnNode insn:method.instructions.toArray()) {
                    if(insn instanceof MethodInsnNode&&(((MethodInsnNode)insn).owner.equals("dev/zahin/zalithrender/render/RenderHooks")||((MethodInsnNode)insn).owner.equals("dev/zahin/zalithrender/render/FrameHooks")))calls++;
                }
                check(calls>0,"Runtime hook calls missing");
                for(MethodNode method:before.methods) {
                    boolean found=false;
                    for(MethodNode changed:after.methods)if(changed.name.equals(method.name)&&changed.desc.equals(method.desc))found=true;
                    check(found,"Original method removed: "+method.name);
                }
                if(name.equals("Minecraft")) {
                    MethodNode oldLoop=null,newLoop=null;
                    for(MethodNode m:before.methods)if(m.name.equals("runGameLoop")||m.name.equals("func_71411_J"))oldLoop=m;
                    for(MethodNode m:after.methods)if(m.name.equals("runGameLoop")||m.name.equals("func_71411_J"))newLoop=m;
                    java.util.List<String> oldCalls=new java.util.ArrayList<String>(),newCalls=new java.util.ArrayList<String>();
                    for(AbstractInsnNode i:oldLoop.instructions.toArray())if(i instanceof MethodInsnNode) {
                        MethodInsnNode m=(MethodInsnNode)i;
                        if(!m.owner.equals("org/lwjgl/opengl/Display")||!m.name.equals("sync"))oldCalls.add(m.owner+"."+m.name+m.desc);
                    }
                    int begins=0,ends=0,syncs=0,presents=0;
                    for(AbstractInsnNode i:newLoop.instructions.toArray())if(i instanceof MethodInsnNode) {
                        MethodInsnNode m=(MethodInsnNode)i;
                        if(m.owner.equals("dev/zahin/zalithrender/render/FrameHooks")) {
                            if(m.name.equals("begin"))begins++;if(m.name.equals("end"))ends++;if(m.name.equals("vanillaSync"))syncs++;
                            if(m.name.equals("present")) {
                                presents++;
                                check(m.getNext() instanceof MethodInsnNode&&((MethodInsnNode)m.getNext()).name.equals("func_147120_f"),"Pacing did not run immediately before native display update");
                            }
                        }else newCalls.add(m.owner+"."+m.name+m.desc);
                    }
                    check(oldCalls.equals(newCalls),"Frame pacing removed or reordered native tick/input/sound/render/display operations");
                    java.util.List<String> oldWrites=new java.util.ArrayList<String>(),newWrites=new java.util.ArrayList<String>();
                    for(AbstractInsnNode i:oldLoop.instructions.toArray())if(i instanceof org.objectweb.asm.tree.FieldInsnNode&&(i.getOpcode()==org.objectweb.asm.Opcodes.PUTFIELD||i.getOpcode()==org.objectweb.asm.Opcodes.PUTSTATIC)) {
                        org.objectweb.asm.tree.FieldInsnNode f=(org.objectweb.asm.tree.FieldInsnNode)i;oldWrites.add(f.owner+"."+f.name+f.desc);
                    }
                    for(AbstractInsnNode i:newLoop.instructions.toArray())if(i instanceof org.objectweb.asm.tree.FieldInsnNode&&(i.getOpcode()==org.objectweb.asm.Opcodes.PUTFIELD||i.getOpcode()==org.objectweb.asm.Opcodes.PUTSTATIC)) {
                        org.objectweb.asm.tree.FieldInsnNode f=(org.objectweb.asm.tree.FieldInsnNode)i;newWrites.add(f.owner+"."+f.name+f.desc);
                    }
                    check(oldWrites.equals(newWrites),"Native FPS counters or tick state were modified");
                    ClassNode nativeSettings=new ClassNode();new ClassReader(read(zip,"net/minecraft/client/settings/GameSettings.class")).accept(nativeSettings,0);
                    for(MethodNode accessor:after.methods)if(accessor.name.equals("zr$vsync"))for(AbstractInsnNode i:accessor.instructions.toArray())if(i instanceof org.objectweb.asm.tree.FieldInsnNode) {
                        org.objectweb.asm.tree.FieldInsnNode f=(org.objectweb.asm.tree.FieldInsnNode)i;
                        if(!f.owner.equals("net/minecraft/client/settings/GameSettings"))continue;
                        boolean exists=false;for(FieldNode nativeField:nativeSettings.fields)if(nativeField.name.equals(f.name)&&nativeField.desc.equals(f.desc)&&(nativeField.access&org.objectweb.asm.Opcodes.ACC_PUBLIC)!=0)exists=true;
                        check(exists,"VSync accessor does not match the native compile/runtime API");
                    }
                    check(begins==1&&ends==1&&syncs==1&&presents==1,"Frame pacing can run twice or skip the original limiter fallback");
                    check(newLoop.instructions.getFirst() instanceof org.objectweb.asm.tree.VarInsnNode,"Frame timing did not begin before native work");
                    ClassNode incompatible=new ClassNode();new ClassReader(original).accept(incompatible,0);
                    for(MethodNode m:incompatible.methods)for(AbstractInsnNode i:m.instructions.toArray())if(i instanceof MethodInsnNode) {
                        MethodInsnNode call=(MethodInsnNode)i;if(call.owner.equals("org/lwjgl/opengl/Display")&&call.name.equals("sync"))call.name="unknownLimiter";
                    }
                    ClassWriter w=new ClassWriter(0);incompatible.accept(w);byte[] changed=w.toByteArray();
                    check(transformer.transform(type,type,changed)==changed,"Unknown frame loop was changed");
                    check(RenderTransformer.complete(),"Optional pacing failure disabled the existing chunk cache");
                }
            }
        }
        check(RenderTransformer.complete(),"Partial hooks must not activate the cache");
    }
    private static byte[] read(ZipFile zip,String name)throws Exception {
        try(java.io.InputStream input=zip.getInputStream(zip.getEntry(name));java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream()) {
            byte[] data=new byte[8192];int n;while((n=input.read(data))!=-1)out.write(data,0,n);return out.toByteArray();
        }
    }
    public static void main(String[] args)throws Exception {
        FrameChecks.run();
        IntTable<Integer> table=new IntTable<Integer>();Map<Integer,Integer> reference=new HashMap<Integer,Integer>();Random random=new Random(1317);
        for(int i=0;i<50000;i++) {
            int key=i%41==0?Integer.MIN_VALUE:(i%43==0?0:random.nextInt(1000)-500);
            switch(random.nextInt(3)) {
                case 0: int value=random.nextInt();table.put(key,value);reference.put(key,value);break;
                case 1: check(java.util.Objects.equals(table.remove(key),reference.remove(key)),"Primitive removal mismatch");break;
                default:check(java.util.Objects.equals(table.get(key),reference.get(key)),"Primitive lookup mismatch");
            }
            check(table.size()==reference.size(),"Primitive table size mismatch");
        }
        table.clear();check(table.size()==0&&table.get(0)==null,"Table clear failed");
        check(RenderTypePolicy.standard(net.minecraft.block.NativeBlockStub.class),"Native blocks must be eligible");
        check(RenderTypePolicy.standard(OrdinaryOre.class),"Ordinary modded ores inheriting vanilla geometry must be eligible");
        check(!RenderTypePolicy.standard(SpecialShape.class),"Custom rendering must use the original display list");
        check(!RenderTypePolicy.standard(Object.class),"Unknown rendering must remain native");
        for(String path:args)prepareHooks(java.nio.file.Paths.get(path));
        // A changed field layout must retain vanilla bytecode and close the activation gate.
        try(ZipFile zip=new ZipFile(args[0])) {
            String type="net.minecraft.client.renderer.Tessellator";
            ClassNode broken=new ClassNode();new ClassReader(read(zip,type.replace('.','/')+".class")).accept(broken,0);
            for(FieldNode field:broken.fields)if(field.name.equals("rawBuffer"))field.name="unexpectedVertexLayout";
            ClassWriter writer=new ClassWriter(0);broken.accept(writer);byte[] bytes=writer.toByteArray();
            check(new RenderTransformer().transform(type,type,bytes)==bytes,"Changed tessellator layout was not preserved");
            check(!RenderTransformer.complete(),"Incomplete transformer must close activation gate");
        }
        System.out.println("PASS: "+checks+" pacing/budget/statistics/table/mapping/transformer checks");
    }
}
