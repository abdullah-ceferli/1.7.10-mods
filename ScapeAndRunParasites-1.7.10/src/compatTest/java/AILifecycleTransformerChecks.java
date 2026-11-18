package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.jar.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
import com.dhanantry.scapeandrunparasites.backport.entity.core.EntityAILifecycleTransformer;

public final class AILifecycleTransformerChecks {
    static final String TARGET = "net.minecraft.entity.EntityLiving";
    static byte[] write(ClassNode node) { ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS); node.accept(w); return w.toByteArray(); }
    static ClassNode read(byte[] bytes) { ClassNode n = new ClassNode(); new ClassReader(bytes).accept(n, 0); return n; }
    static MethodNode constructor(ClassNode node) {
        for (Object item : node.methods) { MethodNode m = (MethodNode)item; if (m.name.equals("<init>") && m.desc.equals("(Lnet/minecraft/world/World;)V")) return m; }
        throw new AssertionError("Fixture constructor absent");
    }
    static void rejects(EntityAILifecycleTransformer t, byte[] bytes) {
        try { t.transform(TARGET, TARGET, bytes); } catch (IllegalStateException expected) { return; }
        throw new AssertionError("Changed constructor accepted");
    }
    public static void main(String[] args) throws Exception {
        byte[] original;
        try (JarFile jar = new JarFile(args[0]); InputStream in = jar.getInputStream(jar.getJarEntry(TARGET.replace('.', '/') + ".class"))) {
            ByteArrayOutputStream b = new ByteArrayOutputStream(); byte[] buffer = new byte[8192]; int n;
            while ((n = in.read(buffer)) != -1) b.write(buffer, 0, n); original = b.toByteArray();
        }
        EntityAILifecycleTransformer t = new EntityAILifecycleTransformer();
        byte[] changed = t.transform(TARGET, TARGET, original);
        if (Arrays.equals(original, changed) || !Arrays.equals(changed, t.transform(TARGET, TARGET, changed))) throw new AssertionError("Transform/idempotence");
        if (t.transform("other", "other", original) != original || t.transform(TARGET, TARGET, null) != null) throw new AssertionError("Unrelated class changed");
        List<String> report = new ArrayList<String>();
        report.add("PASS: Native constructor receives one idempotent callback and unrelated classes are unchanged");
        ClassNode node = read(original); constructor(node).instructions.insertBefore(constructor(node).instructions.getLast(), new InsnNode(Opcodes.RETURN)); rejects(t, write(node));
        node = read(original); node.methods.remove(constructor(node)); rejects(t, write(node));
        node = read(changed); MethodNode m = constructor(node);
        for (AbstractInsnNode i = m.instructions.getFirst(); i != null; i = i.getNext()) if (i instanceof MethodInsnNode && ((MethodInsnNode)i).name.equals("constructed")) { ((MethodInsnNode)i).name = "wrongCallback"; break; }
        rejects(t, write(node));
        node = read(changed); m = constructor(node);
        for (AbstractInsnNode i = m.instructions.getFirst(); i != null; i = i.getNext()) if (i instanceof MethodInsnNode && ((MethodInsnNode)i).name.equals("constructed")) { m.instructions.insert(i, i.clone(new HashMap<LabelNode,LabelNode>())); break; }
        rejects(t, write(node));
        report.add("PASS: Missing constructors, multiple returns, incorrect callbacks and duplicate hooks are rejected");
        report.add("Failed: 0"); Files.write(Paths.get(args[1]), report, StandardCharsets.UTF_8);
        for (String line : report) System.out.println(line);
    }
}
