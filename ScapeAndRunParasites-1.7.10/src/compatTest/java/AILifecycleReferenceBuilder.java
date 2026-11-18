package dev.srpbackport.tests;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Extract only the original constructor's server/null-world callback gate, not the whole entity. */
public final class AILifecycleReferenceBuilder {
    public static void main(String[] args) throws Exception {
        ClassNode original = new ClassNode();
        try (JarFile jar = new JarFile(args[0]); InputStream in = jar.getInputStream(jar.getJarEntry("net/minecraft/entity/EntityLiving.class"))) {
            new ClassReader(in).accept(original, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }
        MethodNode constructor = null, empty = null, tick = null;
        for (Object entry : original.methods) {
            MethodNode m = (MethodNode)entry;
            if (m.name.equals("<init>") && m.desc.equals("(Lnet/minecraft/world/World;)V")) constructor = m;
            if (m.name.equals("func_70619_bc") && m.desc.equals("()V")) tick = m;
            if (m.name.equals("func_184651_r") && m.desc.equals("()V")) empty = m;
        }
        if (constructor == null || empty == null || tick == null) throw new AssertionError("Original lifecycle methods missing");
        if (instructions(tick).size() != 1 || instructions(tick).get(0).getOpcode() != Opcodes.RETURN) throw new AssertionError("Original base mob tick not empty");
        List<AbstractInsnNode> body = instructions(empty);
        if (body.size() != 1 || body.get(0).getOpcode() != Opcodes.RETURN) throw new AssertionError("Original base callback no longer empty");
        List<AbstractInsnNode> code = instructions(constructor);
        int start;
        int[] expected = {Opcodes.ALOAD, Opcodes.IFNULL, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.IFNE, Opcodes.ALOAD, Opcodes.INVOKEVIRTUAL, Opcodes.RETURN};
        // The tail contains eight executable instructions; both branches target its sole RETURN.
        start = code.size() - expected.length;
        for (int n = 0; n < expected.length; n++) if (code.get(start+n).getOpcode() != expected[n]) throw new AssertionError("Original constructor tail changed at " + n);
        checkVar(code.get(start), 1); checkVar(code.get(start+2), 1); checkVar(code.get(start+5), 0);
        FieldInsnNode field = (FieldInsnNode)code.get(start+3);
        MethodInsnNode call = (MethodInsnNode)code.get(start+6);
        if (!field.owner.equals("net/minecraft/world/World") || !field.name.equals("field_72995_K") || !field.desc.equals("Z")
            || !call.owner.equals("net/minecraft/entity/EntityLiving") || !call.name.equals("func_184651_r") || !call.desc.equals("()V")) throw new AssertionError("Original gate boundary changed");
        AbstractInsnNode first = code.get(start), last = code.get(code.size()-1);
        for (int n : new int[]{start+1, start+4}) {
            AbstractInsnNode target = ((JumpInsnNode)code.get(n)).label;
            while (target != null && target.getOpcode() < 0) target = target.getNext();
            if (target != last) throw new AssertionError("Original gate target changed");
        }
        String iface = "com/dhanantry/scapeandrunparasites/backport/entity/ai/EntityAIInitializer";
        ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        out.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "dev/srpbackport/tests/reference/OriginalAILifecycleGate", null, "java/lang/Object", null);
        MethodNode gate = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "constructed", "(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/world/World;)V", null, null);
        Map<LabelNode,LabelNode> labels = new IdentityHashMap<LabelNode,LabelNode>();
        for (AbstractInsnNode i = first; i != null; i = i.getNext()) if (i instanceof LabelNode) labels.put((LabelNode)i, new LabelNode());
        for (AbstractInsnNode i = first; i != null; i = i.getNext()) {
            if (i == call) {
                gate.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, iface));
                gate.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, iface, "srp$initializeAI", "()V", true));
            } else {
                AbstractInsnNode copy = i.clone(labels);
                if (i == field) ((FieldInsnNode)copy).name = "isRemote";
                gate.instructions.add(copy);
            }
        }
        gate.accept(out); out.visitEnd();
        Path destination = Paths.get(args[1], "dev/srpbackport/tests/reference/OriginalAILifecycleGate.class");
        Files.createDirectories(destination.getParent()); Files.write(destination, out.toByteArray());
        System.out.println("Retained original eight-instruction constructor gate; adapted isRemote and callback owner only. Original base callback is empty.");
    }
    static List<AbstractInsnNode> instructions(MethodNode method) {
        List<AbstractInsnNode> result = new ArrayList<AbstractInsnNode>();
        for (AbstractInsnNode i = method.instructions.getFirst(); i != null; i = i.getNext()) if (i.getOpcode() >= 0) result.add(i);
        return result;
    }
    static void checkVar(AbstractInsnNode i, int expected) { if (((VarInsnNode)i).var != expected) throw new AssertionError("Original gate local changed"); }
}
