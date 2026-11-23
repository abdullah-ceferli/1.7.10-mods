import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/** Keep the native binding used by the backend from being redirected back to that backend. */
public final class PatchRedirectExclusions {
    private static final String PREFIX = "org.taumc.celeritas.lwjgl.lwjgl3.LWJGL3Service";

    private static MethodNode initializer(ClassNode node) {
        for (MethodNode method : node.methods) if (method.name.equals("<clinit>")) return method;
        throw new AssertionError("Missing redirector initializer");
    }

    private static FieldInsnNode store(MethodNode method) {
        FieldInsnNode result = null;
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof FieldInsnNode && insn.getOpcode() == Opcodes.PUTSTATIC) {
                FieldInsnNode field = (FieldInsnNode) insn;
                if (field.name.equals("CORE_EXCLUSIONS") && field.desc.equals("[Ljava/lang/String;")) {
                    if (result != null) throw new AssertionError("Duplicate exclusion assignment");
                    result = field;
                }
            }
        }
        if (result == null) throw new AssertionError("Exclusion assignment missing");
        return result;
    }

    private static AbstractInsnNode arrayLength(FieldInsnNode store) {
        AbstractInsnNode insn = store.getPrevious();
        for (int i = 0; insn != null && i < 40; i++, insn = insn.getPrevious()) {
            if (insn instanceof TypeInsnNode && insn.getOpcode() == Opcodes.ANEWARRAY
                && ((TypeInsnNode) insn).desc.equals("java/lang/String")) return insn.getPrevious();
        }
        throw new AssertionError("Could not locate exclusion array length");
    }

    private static byte[] methodBytes(MethodNode method) {
        ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "MethodProbe", null, "java/lang/Object", null);
        method.accept(writer);
        writer.visitEnd();
        return writer.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        byte[] original = Files.readAllBytes(Paths.get(args[0]));
        ClassNode patched = new ClassNode();
        new ClassReader(original).accept(patched, 0);
        MethodNode init = initializer(patched);
        FieldInsnNode store = store(init);
        AbstractInsnNode length = arrayLength(store);
        if (length.getOpcode() != Opcodes.ICONST_4) throw new AssertionError("Unexpected original exclusion count");
        init.instructions.set(length, new InsnNode(Opcodes.ICONST_5));
        InsnList append = new InsnList();
        append.add(new InsnNode(Opcodes.DUP));
        append.add(new InsnNode(Opcodes.ICONST_4));
        append.add(new LdcInsnNode(PREFIX));
        append.add(new InsnNode(Opcodes.AASTORE));
        init.instructions.insertBefore(store, append);
        ClassWriter writer = new ClassWriter(0);
        patched.accept(writer);
        byte[] output = writer.toByteArray();

        // Undo the single array append and require every method to match the original.
        ClassNode before = new ClassNode(), undone = new ClassNode();
        new ClassReader(original).accept(before, 0);
        new ClassReader(output).accept(undone, 0);
        MethodNode undoInit = initializer(undone);
        FieldInsnNode undoStore = store(undoInit);
        AbstractInsnNode undoLength = arrayLength(undoStore);
        if (undoLength.getOpcode() != Opcodes.ICONST_5) throw new AssertionError("New exclusion count missing");
        AbstractInsnNode arrayStore = undoStore.getPrevious();
        AbstractInsnNode prefix = arrayStore.getPrevious();
        AbstractInsnNode index = prefix.getPrevious();
        AbstractInsnNode dup = index.getPrevious();
        if (arrayStore.getOpcode() != Opcodes.AASTORE || !(prefix instanceof LdcInsnNode)
            || !PREFIX.equals(((LdcInsnNode) prefix).cst) || index.getOpcode() != Opcodes.ICONST_4
            || dup.getOpcode() != Opcodes.DUP) throw new AssertionError("Unexpected exclusion append");
        undoInit.instructions.set(undoLength, new InsnNode(Opcodes.ICONST_4));
        for (AbstractInsnNode insn : new AbstractInsnNode[] {dup, index, prefix, arrayStore}) undoInit.instructions.remove(insn);
        if (before.methods.size() != undone.methods.size()) throw new AssertionError("Method count changed");
        for (int i = 0; i < before.methods.size(); i++) {
            if (!Arrays.equals(methodBytes(before.methods.get(i)), methodBytes(undone.methods.get(i)))) {
                throw new AssertionError("Unrelated redirector code changed: " + before.methods.get(i).name);
            }
        }
        Files.write(Paths.get(args[1]), output);
        System.out.println("PASS: one native-service exclusion added; all " + before.methods.size() + " methods otherwise unchanged");
    }
}
