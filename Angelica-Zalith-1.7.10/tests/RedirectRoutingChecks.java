import com.gtnewhorizons.angelica.glsm.loading.ClassDump;
import com.gtnewhorizons.angelica.glsm.loading.ModRedirector;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class RedirectRoutingChecks {
    private static final String SERVICE = "org.taumc.celeritas.lwjgl.lwjgl3.LWJGL3Service";
    private static byte[] serviceBytes() throws Exception {
        try (InputStream input = RedirectRoutingChecks.class.getClassLoader().getResourceAsStream(SERVICE.replace('.', '/') + ".class")) {
            if (input == null) throw new AssertionError("Service bytecode missing");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int n;
            while ((n = input.read(buffer)) != -1) output.write(buffer, 0, n);
            return output.toByteArray();
        }
    }
    private static boolean routesFenceToGlsm(byte[] bytes) {
        ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, 0);
        for (MethodNode method : node.methods) {
            if (!method.name.equals("glFenceSync") && !method.name.equals("fence")) continue;
            for (AbstractInsnNode insn : method.instructions.toArray()) {
                if (insn instanceof MethodInsnNode && ((MethodInsnNode) insn).owner.endsWith("/GLSMBuffers")
                    && ((MethodInsnNode) insn).name.equals("glFenceSync")) return true;
            }
        }
        return false;
    }
    public static void main(String[] args) throws Exception {
        boolean patched = args[0].equals("patched");
        ModRedirector redirector = new ModRedirector(ClassDump.DISABLED);
        byte[] service = serviceBytes();
        byte[] result = redirector.transform(SERVICE, service, RedirectRoutingChecks.class);
        if (patched) {
            if (result != service) throw new AssertionError("Native service was redirected");
            if (routesFenceToGlsm(result)) throw new AssertionError("Native fence still feeds back into GLSM");
        } else if (!routesFenceToGlsm(result)) {
            throw new AssertionError("Original recursive route was not reproduced");
        }
        // Ordinary callers must still be tracked; this is not a global redirector bypass.
        ClassWriter probe = new ClassWriter(0);
        probe.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "example/TrackedFence", null, "java/lang/Object", null);
        MethodVisitor method = probe.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "fence", "(II)J", null, null);
        method.visitCode();
        method.visitVarInsn(Opcodes.ILOAD, 0);
        method.visitVarInsn(Opcodes.ILOAD, 1);
        method.visitMethodInsn(Opcodes.INVOKESTATIC, "org/lwjgl/opengl/GL32C", "glFenceSync", "(II)J", false);
        method.visitInsn(Opcodes.LRETURN);
        method.visitMaxs(2, 2);
        method.visitEnd();
        probe.visitEnd();
        if (!routesFenceToGlsm(redirector.transform("example.TrackedFence", probe.toByteArray(), RedirectRoutingChecks.class))) {
            throw new AssertionError("Ordinary caller tracking was disabled");
        }
        System.out.println(patched ? "PASS: native service bypasses redirector; ordinary callers remain tracked"
            : "PASS: reproduced original recursive native-service route");
    }
}
