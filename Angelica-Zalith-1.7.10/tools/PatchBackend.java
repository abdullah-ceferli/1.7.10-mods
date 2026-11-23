import java.nio.file.Files;
import java.nio.file.Paths;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/** Build-time patch; adds no transformer to the running game. */
public final class PatchBackend {
    private static final String QUERY = "(IIILjava/nio/IntBuffer;)Ljava/lang/String;";
    private static final String HELPER = "com/gtnewhorizons/angelica/glsm/backend/ZalithShaderQueries";

    public static void main(String[] args) throws Exception {
        final int[] changed = new int[2];
        ClassReader reader = new ClassReader(Files.readAllBytes(Paths.get(args[0])));
        ClassWriter writer = new ClassWriter(0);
        reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, final String method, String desc, String sig, String[] exc) {
                MethodVisitor delegate = super.visitMethod(access, method, desc, sig, exc);
                final boolean target = QUERY.equals(desc)
                    && (method.equals("getActiveUniform") || method.equals("getActiveAttrib"));
                return new MethodVisitor(Opcodes.ASM9, delegate) {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        if (target && opcode == Opcodes.INVOKESTATIC && owner.equals("org/lwjgl/opengl/GL20")
                            && name.equals("gl" + Character.toUpperCase(method.charAt(0)) + method.substring(1))
                            && QUERY.equals(desc)) {
                            changed[method.equals("getActiveUniform") ? 0 : 1]++;
                            super.visitMethodInsn(opcode, HELPER, method, desc, false);
                        } else {
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        }
                    }
                };
            }
        }, 0);
        if (changed[0] != 1 || changed[1] != 1) {
            throw new IllegalStateException("Unexpected upstream bytecode: " + changed[0] + ", " + changed[1]);
        }
        Files.write(Paths.get(args[1]), writer.toByteArray());
    }
}
