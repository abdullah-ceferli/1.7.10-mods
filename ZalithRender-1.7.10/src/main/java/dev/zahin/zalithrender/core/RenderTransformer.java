package dev.zahin.zalithrender.core;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/** Hooks the vanilla pipeline without replacing any original Minecraft method. */
public final class RenderTransformer implements IClassTransformer {
    private static final String HOOK = "dev/zahin/zalithrender/render/RenderHooks";
    private static final String FRAME = "dev/zahin/zalithrender/render/FrameHooks";
    private static final String BASE = "net.minecraft.client.renderer.";
    private static final Set<String> COMPLETE = new HashSet<String>();

    public static synchronized boolean complete() { return COMPLETE.size() == 4; }
    private static synchronized void installed(String name, boolean valid) { if (valid) COMPLETE.add(name); else COMPLETE.remove(name); }

    @Override public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null || transformedName == null) return bytes;
        boolean minecraft = transformedName.equals("net.minecraft.client.Minecraft");
        if (!minecraft && !transformedName.startsWith(BASE)) return bytes;
        String simple = minecraft ? "Minecraft" : transformedName.substring(BASE.length());
        if (!minecraft && !simple.equals("WorldRenderer") && !simple.equals("Tessellator") && !simple.equals("RenderBlocks") && !simple.equals("RenderList")) return bytes;
        try {
            ClassNode node = new ClassNode();
            new ClassReader(bytes).accept(node, 0);
            int changed;
            if (minecraft) changed = minecraft(node);
            else if (simple.equals("WorldRenderer")) changed = world(node);
            else if (simple.equals("Tessellator")) changed = tess(node);
            else if (simple.equals("RenderBlocks")) changed = blocks(node);
            else changed = lists(node);
            int expected = simple.equals("WorldRenderer") ? 4 : 1;
            if (changed != expected) throw new IllegalStateException("Expected " + expected + " hooks; found " + changed);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            if (!minecraft) installed(simple,true);
            System.out.println("[ZalithRender] Prepared " + simple);
            return writer.toByteArray();
        } catch (RuntimeException ex) {
            if (!minecraft) installed(simple,false);
            System.err.println("[ZalithRender] Retaining original " + simple + ": " + ex);
            return bytes;
        }
    }

    private static String field(ClassNode node, String desc, String mcp, String srg) {
        for (FieldNode field : node.fields) if (field.desc.equals(desc) && (field.name.equals(mcp) || field.name.equals(srg))) return field.name;
        throw new IllegalStateException("Field layout differs: " + mcp);
    }
    private static boolean named(MethodNode method, String mcp, String srg) { return method.name.equals(mcp) || method.name.equals(srg); }
    private static MethodInsnNode hook(String name, String desc) { return new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK, name, desc, false); }
    private static InsnList objectHook(String name, int local) {
        InsnList code = new InsnList(); code.add(new VarInsnNode(Opcodes.ALOAD, local)); code.add(hook(name, "(Ljava/lang/Object;)V")); return code;
    }
    private static void getter(ClassNode node, String method, String descriptor, String value) {
        MethodNode getter = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC, method, "()" + descriptor, null, null);
        getter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        getter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, value, descriptor));
        getter.instructions.add(new InsnNode(descriptor.startsWith("[") ? Opcodes.ARETURN : Opcodes.IRETURN));
        node.methods.add(getter);
    }
    private static int minecraft(ClassNode node) {
        if (node.interfaces.contains("dev/zahin/zalithrender/core/FrameAccess")) throw new IllegalStateException("Already transformed");
        String settings = field(node, "Lnet/minecraft/client/settings/GameSettings;", "gameSettings", "field_71474_y");
        String world = field(node, "Lnet/minecraft/client/multiplayer/WorldClient;", "theWorld", "field_71441_e");
        String skip = field(node, "Z", "skipRenderWorld", "field_71454_w");
        String limit = null;
        MethodNode loop = null;
        for (MethodNode method : node.methods) {
            if (named(method, "getLimitFramerate", "func_90020_K") && method.desc.equals("()I")) limit = method.name;
            if (named(method, "runGameLoop", "func_71411_J") && method.desc.equals("()V")) loop = method;
        }
        if (limit == null || loop == null) throw new IllegalStateException("Frame-loop methods differ");
        int syncs = 0, returns = 0, displays = 0;
        for (AbstractInsnNode instruction : loop.instructions.toArray()) {
            if (instruction.getOpcode() == Opcodes.RETURN) returns++;
            if (!(instruction instanceof MethodInsnNode)) continue;
            MethodInsnNode call = (MethodInsnNode) instruction;
            if (call.owner.equals(node.name) && (call.name.equals("func_147120_f")) && call.desc.equals("()V")) displays++;
            if (call.owner.equals("org/lwjgl/opengl/Display") && call.name.equals("sync") && call.desc.equals("(I)V")) syncs++;
        }
        if (syncs != 1 || returns != 1 || displays != 1) throw new IllegalStateException("Frame-loop control flow differs");
        InsnList start = new InsnList();
        start.add(new VarInsnNode(Opcodes.ALOAD, 0));
        start.add(new MethodInsnNode(Opcodes.INVOKESTATIC, FRAME, "begin", "(Ljava/lang/Object;)V", false));
        loop.instructions.insert(start);
        for (AbstractInsnNode instruction : loop.instructions.toArray()) {
            if (instruction.getOpcode() == Opcodes.RETURN) {
                loop.instructions.insertBefore(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, FRAME, "end", "()V", false));
            }
            if (instruction instanceof MethodInsnNode) {
                MethodInsnNode call = (MethodInsnNode) instruction;
                if (call.owner.equals(node.name) && call.name.equals("func_147120_f") && call.desc.equals("()V")) {
                    loop.instructions.insertBefore(call, new MethodInsnNode(Opcodes.INVOKESTATIC, FRAME, "present", "()V", false));
                }
                if (call.owner.equals("org/lwjgl/opengl/Display") && call.name.equals("sync") && call.desc.equals("(I)V")) {
                    call.owner = FRAME; call.name = "vanillaSync";
                }
            }
        }
        node.interfaces.add("dev/zahin/zalithrender/core/FrameAccess");
        getter(node, "zr$skip", "Z", skip);
        MethodNode worldGetter = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC, "zr$world", "()Ljava/lang/Object;", null, null);
        worldGetter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        worldGetter.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, world, "Lnet/minecraft/client/multiplayer/WorldClient;"));
        worldGetter.instructions.add(new InsnNode(Opcodes.ARETURN)); node.methods.add(worldGetter);
        MethodNode limitGetter = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC, "zr$limit", "()I", null, null);
        limitGetter.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        limitGetter.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, node.name, limit, "()I", false));
        limitGetter.instructions.add(new InsnNode(Opcodes.IRETURN)); node.methods.add(limitGetter);
        MethodNode vsync = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC, "zr$vsync", "()Z", null, null);
        vsync.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        vsync.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, settings, "Lnet/minecraft/client/settings/GameSettings;"));
        vsync.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/settings/GameSettings", settings.equals("gameSettings") ? "enableVsync" : "field_74352_v", "Z"));
        vsync.instructions.add(new InsnNode(Opcodes.IRETURN)); node.methods.add(vsync);
        return 1;
    }
    private static int world(ClassNode node) {
        String[] names = {field(node,"I","glRenderList","field_78942_y"), field(node,"I","posXClip","field_78932_i"),
            field(node,"I","posYClip","field_78929_j"), field(node,"I","posZClip","field_78930_k")};
        if (node.interfaces.contains("dev/zahin/zalithrender/core/ChunkAccess")) throw new IllegalStateException("Already transformed");
        int changed = 0;
        for (MethodNode method : node.methods) {
            if (named(method,"preRenderBlocks","func_147890_b") && method.desc.equals("(I)V")) {
                InsnList code = new InsnList(); code.add(new VarInsnNode(Opcodes.ALOAD,0)); code.add(new VarInsnNode(Opcodes.ILOAD,1));
                code.add(hook("begin","(Ljava/lang/Object;I)V")); method.instructions.insert(code); changed++;
            } else if (named(method,"postRenderBlocks","func_147891_a") && method.desc.startsWith("(IL")) {
                for (AbstractInsnNode insn : method.instructions.toArray()) if (insn.getOpcode() == Opcodes.RETURN) method.instructions.insertBefore(insn,objectHook("finish",0));
                changed++;
            } else if (named(method,"updateRenderer","func_147892_a") && method.desc.startsWith("(L")) {
                method.instructions.insert(objectHook("enterUpdate",0));
                for (AbstractInsnNode insn : method.instructions.toArray()) if (insn.getOpcode() == Opcodes.RETURN || insn.getOpcode() == Opcodes.ATHROW) {
                    method.instructions.insertBefore(insn,hook("leaveUpdate","()V"));
                }
                changed++;
            } else if (named(method,"setDontDraw","func_78910_b") && method.desc.equals("()V")) {
                method.instructions.insert(objectHook("release",0)); changed++;
            }
        }
        node.interfaces.add("dev/zahin/zalithrender/core/ChunkAccess");
        String[] getters = {"zr$list","zr$x","zr$y","zr$z"};
        for (int i=0;i<4;i++) getter(node,getters[i],"I",names[i]);
        return changed;
    }
    private static int tess(ClassNode node) {
        String raw=field(node,"[I","rawBuffer","field_78405_h"), index=field(node,"I","rawBufferIndex","field_147569_p"),
            count=field(node,"I","vertexCount","field_78406_i"), mode=field(node,"I","drawMode","field_78409_u");
        String[] flags={field(node,"Z","hasTexture","field_78400_o"),field(node,"Z","hasColor","field_78399_n"),
            field(node,"Z","hasNormals","field_78413_q"),field(node,"Z","hasBrightness","field_78414_p")};
        if (node.interfaces.contains("dev/zahin/zalithrender/core/TessAccess")) throw new IllegalStateException("Already transformed");
        int changed=0;
        for (MethodNode method:node.methods) if(named(method,"draw","func_78381_a")&&method.desc.equals("()I")) {
            method.instructions.insert(objectHook("capture",0));changed++;
        }
        node.interfaces.add("dev/zahin/zalithrender/core/TessAccess");
        getter(node,"zr$raw","[I",raw); getter(node,"zr$index","I",index); getter(node,"zr$count","I",count); getter(node,"zr$mode","I",mode);
        MethodNode flagMethod=new MethodNode(Opcodes.ACC_PUBLIC|Opcodes.ACC_FINAL|Opcodes.ACC_SYNTHETIC,"zr$flags","()I",null,null);
        flagMethod.instructions.add(new InsnNode(Opcodes.ICONST_0));
        for(int i=0;i<4;i++) {
            flagMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD,0));
            flagMethod.instructions.add(new FieldInsnNode(Opcodes.GETFIELD,node.name,flags[i],"Z"));
            flagMethod.instructions.add(new IntInsnNode(Opcodes.BIPUSH,i));
            flagMethod.instructions.add(new InsnNode(Opcodes.ISHL)); flagMethod.instructions.add(new InsnNode(Opcodes.IOR));
        }
        flagMethod.instructions.add(new InsnNode(Opcodes.IRETURN));node.methods.add(flagMethod);return changed;
    }
    private static int blocks(ClassNode node) {
        int changed=0;
        for(MethodNode method:node.methods) if(named(method,"renderBlockByRenderType","func_147805_b")&&method.desc.endsWith(";III)Z")) {
            method.instructions.insert(objectHook("checkBlock",1));changed++;
        }
        return changed;
    }
    private static int lists(ClassNode node) {
        int changed=0;
        for(MethodNode method:node.methods) if(named(method,"callLists","func_78419_a")&&method.desc.equals("()V")) {
            for(AbstractInsnNode insn:method.instructions.toArray()) if(insn instanceof MethodInsnNode) {
                MethodInsnNode call=(MethodInsnNode)insn;
                if(call.owner.equals("org/lwjgl/opengl/GL11")&&call.name.equals("glCallLists")&&call.desc.equals("(Ljava/nio/IntBuffer;)V")) {
                    call.owner=HOOK;call.name="callLists";changed++;
                }
            }
        }
        return changed;
    }
}
