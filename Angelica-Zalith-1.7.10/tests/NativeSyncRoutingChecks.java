import com.gtnewhorizons.angelica.glsm.backend.Lwjgl2GLRenderBackend;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32C;

/** Exercise real GPU fences through the same backend and native service as the phone crash. */
public final class NativeSyncRoutingChecks {
    public static void main(String[] args) {
        if (!GLFW.glfwInit()) throw new AssertionError("GLFW initialization failed");
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        long window = GLFW.glfwCreateWindow(16, 16, "Native fence checks", 0, 0);
        if (window == 0) throw new AssertionError("Could not create hidden GL context");
        try {
            GLFW.glfwMakeContextCurrent(window);
            GL.createCapabilities();
            Lwjgl2GLRenderBackend backend = new Lwjgl2GLRenderBackend();
            IntBuffer length = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
            for (int i = 0; i < 128; i++) {
                long sync = backend.fenceSync(GL32C.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
                if (sync == 0 || !GL32C.glIsSync(sync)) throw new AssertionError("No native GPU fence created");
                try {
                    int wait = backend.clientWaitSync(sync, GL32C.GL_SYNC_FLUSH_COMMANDS_BIT, 1_000_000_000L);
                    if (wait != GL32C.GL_ALREADY_SIGNALED && wait != GL32C.GL_CONDITION_SATISFIED) {
                        throw new AssertionError("GPU fence did not signal: " + wait);
                    }
                    backend.waitSync(sync, 0, GL32C.GL_TIMEOUT_IGNORED);
                    if (backend.getSynci(sync, GL32C.GL_SYNC_STATUS, length) != GL32C.GL_SIGNALED) {
                        throw new AssertionError("Incorrect native GPU fence status");
                    }
                } finally {
                    backend.deleteSync(sync);
                }
                if (GL32C.glIsSync(sync)) throw new AssertionError("GPU fence was not deleted");
            }
            if (GL11.glGetError() != GL11.GL_NO_ERROR) throw new AssertionError("Native GL error");
            System.out.println("PASS: 128 real GPU fence create/wait/status/delete cycles through Angelica's backend (" + args[0] + ")");
        } finally {
            GLFW.glfwMakeContextCurrent(0);
            GLFW.glfwDestroyWindow(window);
            GLFW.glfwTerminate();
        }
    }
}
