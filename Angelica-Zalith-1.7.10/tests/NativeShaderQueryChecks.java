import com.gtnewhorizons.angelica.glsm.backend.ZalithShaderQueries;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ReadOnlyBufferException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/** Real shader queries against each launcher's unmodified Java libraries and desktop native GL. */
public final class NativeShaderQueryChecks {
    private static int checks;
    private static IntBuffer ints(int count) {
        return ByteBuffer.allocateDirect(count * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
    }
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static int shader(int kind, String source) {
        int shader = GL20.glCreateShader(kind);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        check(GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) != 0, GL20.glGetShaderInfoLog(shader, 8192));
        return shader;
    }
    private static String directQuery(int program, int index, int maxLength, IntBuffer pair, boolean uniform) {
        IntBuffer length = ints(1), size = ints(1), type = ints(1);
        ByteBuffer name = ByteBuffer.allocateDirect(maxLength);
        if (uniform) GL20.glGetActiveUniform(program, index, length, size, type, name);
        else GL20.glGetActiveAttrib(program, index, length, size, type, name);
        byte[] text = new byte[length.get(0)];
        name.get(text);
        pair.put(0, size.get(0));
        pair.put(1, type.get(0));
        return new String(text, java.nio.charset.StandardCharsets.UTF_8);
    }
    private static void checkQueries(int program, boolean uniform, int maxLength) {
        int count = GL20.glGetProgrami(program, uniform ? GL20.GL_ACTIVE_UNIFORMS : GL20.GL_ACTIVE_ATTRIBUTES);
        check(count >= 2, "Shader unexpectedly optimized away active inputs");
        IntBuffer expected = ints(2);
        for (int offset : new int[] {0, 1}) {
            IntBuffer actual = ints(5);
            for (int i = 0; i < 5; i++) actual.put(i, 100 + i);
            actual.position(offset);
            actual.limit(offset + 2);
            for (int cycle = 0; cycle < 32; cycle++) {
                for (int index = 0; index < count; index++) {
                    String wanted = directQuery(program, index, maxLength, expected, uniform);
                    String got = uniform ? ZalithShaderQueries.getActiveUniform(program, index, maxLength, actual)
                        : ZalithShaderQueries.getActiveAttrib(program, index, maxLength, actual);
                    check(wanted.equals(got), "Shader name mismatch: " + got + " vs " + wanted);
                    check(actual.get(offset) == expected.get(0), "Shader array size was overwritten by its type");
                    check(actual.get(offset + 1) == expected.get(1), "Shader type was lost");
                    check(actual.position() == offset && actual.limit() == offset + 2, "Output buffer cursor changed");
                }
            }
            actual.clear();
            for (int i = 0; i < 5; i++) {
                if (i < offset || i >= offset + 2) check(actual.get(i) == 100 + i, "Output outside requested pair was changed");
            }
        }
    }
    private static void checkOriginalBridge(int program, boolean uniform) {
        IntBuffer pair = ints(2), expected = ints(2);
        String wanted = directQuery(program, 0, 256, expected, uniform);
        boolean corrupt = false, exhausted = false;
        for (int i = 0; i < 4; i++) {
            try {
                String got = uniform ? GL20.glGetActiveUniform(program, 0, 256, pair)
                    : GL20.glGetActiveAttrib(program, 0, 256, pair);
                corrupt |= !wanted.equals(got) || pair.get(0) != expected.get(0) || pair.get(1) != expected.get(1)
                    || pair.position() != 0;
            } catch (IllegalArgumentException | java.nio.BufferOverflowException ex) {
                exhausted = true;
                break;
            }
        }
        check(corrupt && exhausted, "Expected original Zalith bridge regression was not reproduced");
        System.out.println("Reproduced original " + (uniform ? "uniform" : "attribute") + " buffer corruption/exhaustion");
    }
    private interface ThrowingCall { void run(); }
    private static void expect(Class<? extends Throwable> type, ThrowingCall call) {
        try {
            call.run();
            throw new AssertionError("Expected " + type.getSimpleName());
        } catch (Throwable ex) {
            check(type.isInstance(ex), "Unexpected exception: " + ex);
        }
    }
    public static void main(String[] args) {
        check(GLFW.glfwInit(), "GLFW initialization failed");
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        long window = GLFW.glfwCreateWindow(16, 16, "Shader compatibility checks", 0, 0);
        check(window != 0, "Could not create hidden GL context");
        try {
            GLFW.glfwMakeContextCurrent(window);
            GL.createCapabilities();
            System.out.println("Launcher library: " + args[0]);
            System.out.println("Native test GL: " + GL11.glGetString(GL11.GL_VERSION));
            String longName = "u_" + new String(new char[180]).replace('\0', 'x');
            int vertex = shader(GL20.GL_VERTEX_SHADER,
                "#version 330 core\nlayout(location=0) in vec3 a_Position;\nlayout(location=1) in vec2 a_UV;\n"
                + "void main(){gl_Position=vec4(a_Position+vec3(a_UV,0),1);}");
            int fragment = shader(GL20.GL_FRAGMENT_SHADER,
                "#version 330 core\nuniform vec4 u_Colors[2];\nuniform float u_Factor;\nuniform sampler2D u_Texture;\n"
                + "uniform float " + longName + ";\nout vec4 result;\nvoid main(){result=u_Colors[0]*u_Factor"
                + "+u_Colors[1]+texture(u_Texture,vec2(0.5))+vec4(" + longName + ");}");
            int program = GL20.glCreateProgram();
            GL20.glAttachShader(program, vertex);
            GL20.glAttachShader(program, fragment);
            GL20.glLinkProgram(program);
            check(GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) != 0, GL20.glGetProgramInfoLog(program, 8192));
            try {
                checkOriginalBridge(program, true);
                checkOriginalBridge(program, false);
                for (int maxLength : new int[] {1, 8, 128, 256, 512, 8}) {
                    checkQueries(program, true, maxLength);
                    checkQueries(program, false, maxLength);
                }
                final int liveProgram = program;
                expect(IllegalArgumentException.class, () -> ZalithShaderQueries.getActiveUniform(liveProgram, 0, 128, ints(1)));
                expect(IllegalArgumentException.class, () -> ZalithShaderQueries.getActiveAttrib(liveProgram, 0, 128, ints(0)));
                expect(IllegalArgumentException.class, () -> ZalithShaderQueries.getActiveUniform(liveProgram, 0, 0, ints(2)));
                expect(ReadOnlyBufferException.class, () -> ZalithShaderQueries.getActiveUniform(liveProgram, 0, 128, ints(2).asReadOnlyBuffer()));
                expect(NullPointerException.class, () -> ZalithShaderQueries.getActiveAttrib(liveProgram, 0, 128, null));
                check(GL11.glGetError() == GL11.GL_NO_ERROR, "Native GL reported an error");
            } finally {
                GL20.glDeleteProgram(program);
                GL20.glDeleteShader(vertex);
                GL20.glDeleteShader(fragment);
            }
            System.out.println("PASS: " + checks + " checks");
        } finally {
            GLFW.glfwMakeContextCurrent(0);
            GLFW.glfwDestroyWindow(window);
            GLFW.glfwTerminate();
        }
    }
}
