import java.nio.file.Files;
import java.nio.file.Paths;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/** Requires every method except the two query bridges to retain its bytecode. */
public final class VerifyBackend {
    private static final String QUERY = "(IIILjava/nio/IntBuffer;)Ljava/lang/String;";
    private static byte[] methodBytes(MethodNode method) {
        org.objectweb.asm.ClassWriter writer = new org.objectweb.asm.ClassWriter(0);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "MethodProbe", null, "java/lang/Object", null);
        method.accept(writer);
        writer.visitEnd();
        return writer.toByteArray();
    }
    public static void main(String[] args) throws Exception {
        ClassNode before = new ClassNode(), after = new ClassNode();
        new ClassReader(Files.readAllBytes(Paths.get(args[0]))).accept(before, 0);
        new ClassReader(Files.readAllBytes(Paths.get(args[1]))).accept(after, 0);
        if (before.methods.size() != after.methods.size()) throw new AssertionError("Method count changed");
        int changed = 0, retained = 0;
        for (int index = 0; index < before.methods.size(); index++) {
            MethodNode original = before.methods.get(index), patched = after.methods.get(index);
            if (!original.name.equals(patched.name) || !original.desc.equals(patched.desc)) {
                throw new AssertionError("Method identity changed");
            }
            boolean target = QUERY.equals(patched.desc)
                && (patched.name.equals("getActiveUniform") || patched.name.equals("getActiveAttrib"));
            if (!target) {
                if (!java.util.Arrays.equals(methodBytes(original), methodBytes(patched))) {
                    throw new AssertionError("Unrelated method changed: " + original.name + original.desc);
                }
                retained++;
                continue;
            }
            int redirected = 0;
            for (AbstractInsnNode node : patched.instructions.toArray()) {
                if (node instanceof MethodInsnNode) {
                    MethodInsnNode call = (MethodInsnNode) node;
                    if (call.owner.equals("com/gtnewhorizons/angelica/glsm/backend/ZalithShaderQueries")
                        && call.name.equals(patched.name) && call.desc.equals(QUERY)) {
                        call.owner = "org/lwjgl/opengl/GL20";
                        call.name = "gl" + Character.toUpperCase(patched.name.charAt(0)) + patched.name.substring(1);
                        redirected++;
                    }
                }
            }
            if (redirected != 1 || !java.util.Arrays.equals(methodBytes(original), methodBytes(patched))) {
                throw new AssertionError("Query change exceeded one call redirect: " + original.name);
            }
            changed++;
        }
        if (changed != 2) throw new AssertionError("Expected two query bridge changes");
        System.out.println("PASS: exactly two call redirects; " + retained + " other methods unchanged");
    }
}
