package dev.zahin.zalithrender.render;

import dev.zahin.zalithrender.core.ChunkAccess;
import dev.zahin.zalithrender.core.RenderTransformer;
import dev.zahin.zalithrender.core.TessAccess;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLContext;

/** Original chunk mesh cache. Minecraft still compiles its complete display lists for fallback. */
public final class RenderHooks {
    private static final MeshCache CACHE = new MeshCache();
    private static final UploadBudget UPLOAD_BUDGET = new UploadBudget();
    private static final ClassValue<Boolean> VANILLA_BLOCK = new ClassValue<Boolean>() {
        @Override protected Boolean computeValue(Class<?> type) { return RenderTypePolicy.standard(type); }
    };
    private static Object context, updating;
    private static Capture capture;
    private static ByteBuffer scratch;
    private static boolean ready, failed, logged, validatedFrame;
    private static volatile boolean resetRequested, worldHooks;
    private static volatile String blocked = "";
    private static String reason = "Waiting for a graphics context";
    private static int accelerated, nativeLists;

    private RenderHooks() {}

    public static void block(String message) { blocked = message; resetRequested = true; }
    public static void resetLater() { resetRequested = true; }
    public static boolean pacingAllowed() { return blocked.isEmpty(); }
    public static void worldHooks(boolean active) {
        if (worldHooks != active) { worldHooks = active; resetRequested = true; }
    }
    public static void frameStart() {
        capture = null; updating = null;
        accelerated = nativeLists = 0;
        UPLOAD_BUDGET.reset(Settings.uploadsPerFrame, Settings.uploadBudgetNanos);
        validatedFrame = false;
        if (resetRequested) {
            try { CACHE.clear(context != null && GLContext.getCapabilities() == context); }
            catch (RuntimeException ex) { CACHE.clear(false); }
            resetRequested = false;
        }
    }
    public static void enterUpdate(Object renderer) { capture = null; updating = renderer; }
    public static void leaveUpdate() { capture = null; updating = null; }

    public static void begin(Object renderer, int pass) {
        capture = null;
        if (!(renderer instanceof ChunkAccess) || pass < 0 || pass > 1) return;
        ChunkAccess access = (ChunkAccess) renderer;
        if (access.zr$list() < 0) return;
        int id = access.zr$list() + pass;
        if (!usable()) return;
        boolean previousSafe = CACHE.get(id) != null;
        CACHE.remove(id, true);
        if (!UPLOAD_BUDGET.available() || (updating != renderer && !previousSafe)) return;
        capture = new Capture(renderer, id, access.zr$x(), access.zr$y(), access.zr$z());
    }

    public static void checkBlock(Object block) {
        if (capture != null && (block == null || !VANILLA_BLOCK.get(block.getClass()))) capture.unsafe = true;
    }

    public static void capture(Object tessellator) {
        Capture active = capture;
        if (active == null || active.unsafe) return;
        if (!(tessellator instanceof TessAccess) || ++active.draws != 1) { active.unsafe = true; return; }
        TessAccess access = (TessAccess) tessellator;
        int count = access.zr$count(), index = access.zr$index();
        int[] raw = access.zr$raw();
        long bytes = (long) index * 4;
        if (access.zr$mode() != GL11.GL_QUADS || count <= 0 || count % 4 != 0
            || (long) count * 8 != index || raw == null || index > raw.length || bytes > Settings.maxMeshBytes
            || bytes > Settings.cacheBytes) { active.unsafe = true; return; }
        long started = System.nanoTime();
        try {
            if (scratch == null || scratch.capacity() < bytes) {
                int capacity = Math.min(65536, Settings.maxMeshBytes);
                while (capacity < bytes) capacity = Math.min(Settings.maxMeshBytes, capacity * 2);
                scratch = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
            }
            scratch.clear(); scratch.limit((int) bytes);
            scratch.asIntBuffer().put(raw, 0, index);
            active.bytes = (int) bytes; active.vertices = count; active.flags = access.zr$flags();
        } catch (RuntimeException ex) { active.unsafe = true; fail(ex); }
        finally { UPLOAD_BUDGET.charge(System.nanoTime() - started); }
    }

    public static void finish(Object renderer) {
        Capture active = capture;
        capture = null;
        if (active == null || active.owner != renderer || active.unsafe || active.draws != 1 || active.bytes == 0 || !UPLOAD_BUDGET.available()) return;
        long started = System.nanoTime();
        int previous = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING), buffer = 0;
        try {
            buffer = GL15.glGenBuffers();
            if (buffer == 0) throw new IllegalStateException("Renderer did not allocate a vertex buffer");
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
            scratch.position(0); scratch.limit(active.bytes);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, scratch, GL15.GL_STATIC_DRAW);
            if (GL15.glGetBufferParameteri(GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE) != active.bytes) {
                throw new IllegalStateException("Renderer did not upload the complete chunk mesh");
            }
            CACHE.put(new Mesh(active.list, buffer, active.bytes, active.vertices, active.flags, active.x, active.y, active.z));
            buffer = 0; UPLOAD_BUDGET.uploaded(); reason = "Vertex buffers";
        } catch (RuntimeException ex) { fail(ex); }
        finally {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, previous);
            if (buffer != 0) GL15.glDeleteBuffers(buffer);
            UPLOAD_BUDGET.charge(System.nanoTime() - started);
        }
    }

    public static void release(Object renderer) {
        if (!(renderer instanceof ChunkAccess) || context == null) return;
        if (capture != null && capture.owner == renderer) capture = null;
        int id = ((ChunkAccess) renderer).zr$list();
        try {
            boolean delete = GLContext.getCapabilities() == context;
            CACHE.remove(id, delete); CACHE.remove(id + 1, delete);
        } catch (RuntimeException ex) { CACHE.remove(id, false); CACHE.remove(id + 1, false); }
    }

    public static void callLists(IntBuffer lists) {
        if (lists == null) throw new NullPointerException("lists");
        if (CACHE.count() == 0 || !usable() || GL11.glGetInteger(GL11.GL_LIST_BASE) != 0) {
            nativeLists += lists.remaining(); GL11.glCallLists(lists); return;
        }
        int start = lists.position(), end = lists.limit(), nativeStart = start;
        IntBuffer span = lists.duplicate();
        int previousBuffer = 0;
        boolean arrays = false;
        try {
            for (int index = start; index < end; index++) {
                Mesh mesh = CACHE.get(lists.get(index));
                if (mesh == null) {
                    if (arrays) { arrays = false; endArrays(previousBuffer); }
                    continue;
                }
                if (!validatedFrame) {
                    if (!GL15.glIsBuffer(mesh.buffer)) {
                        CACHE.clear(false);
                        reason = "Vertex cache reloading after graphics reset";
                        nativeLists += lists.remaining();
                        GL11.glCallLists(lists);
                        return;
                    }
                    validatedFrame = true;
                }
                if (index > nativeStart) { nativeSpan(span, nativeStart, index); }
                if (!arrays) {
                    previousBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
                    GL11.glPushClientAttrib(GL11.GL_CLIENT_VERTEX_ARRAY_BIT);
                    GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
                    arrays = true;
                }
                draw(mesh); accelerated++;
                nativeStart = index + 1;
            }
            if (arrays) { arrays = false; endArrays(previousBuffer); }
            if (nativeStart < end) nativeSpan(span, nativeStart, end);
        } finally {
            if (arrays) endArrays(previousBuffer);
        }
    }

    private static void endArrays(int previousBuffer) {
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, previousBuffer);
    }

    private static void nativeSpan(IntBuffer span, int start, int end) {
        span.clear(); span.limit(end); span.position(start);
        GL11.glCallLists(span); nativeLists += end - start;
    }

    private static void draw(Mesh mesh) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.buffer);
        GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
        pointer(GL11.GL_TEXTURE_COORD_ARRAY, (mesh.flags & 1) != 0);
        if ((mesh.flags & 1) != 0) GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 12L);
        GL13.glClientActiveTexture(GL13.GL_TEXTURE1);
        pointer(GL11.GL_TEXTURE_COORD_ARRAY, (mesh.flags & 8) != 0);
        if ((mesh.flags & 8) != 0) GL11.glTexCoordPointer(2, GL11.GL_SHORT, 32, 28L);
        GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
        pointer(GL11.GL_COLOR_ARRAY, (mesh.flags & 2) != 0);
        if ((mesh.flags & 2) != 0) GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 32, 20L);
        pointer(GL11.GL_NORMAL_ARRAY, (mesh.flags & 4) != 0);
        if ((mesh.flags & 4) != 0) GL11.glNormalPointer(GL11.GL_BYTE, 32, 24L);
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0L);
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(mesh.x, mesh.y, mesh.z);
            GL11.glTranslatef(-8, -8, -8);
            GL11.glScalef(1.000001F, 1.000001F, 1.000001F);
            GL11.glTranslatef(8, 8, 8);
            GL11.glDrawArrays(GL11.GL_QUADS, 0, mesh.vertices);
        } finally { GL11.glPopMatrix(); }
    }
    private static void pointer(int state, boolean enabled) {
        if (enabled) GL11.glEnableClientState(state); else GL11.glDisableClientState(state);
    }

    private static boolean usable() {
        if (!Settings.enabled || failed || !blocked.isEmpty() || worldHooks || !RenderTransformer.complete()) return false;
        try {
            Object current = GLContext.getCapabilities();
            if (current != context) { CACHE.clear(false); context = current; ready = false; }
            if (!ready) {
                if (!GLContext.getCapabilities().OpenGL15 || !GLContext.getCapabilities().OpenGL13) {
                    reason = "Renderer has no OpenGL 1.5 vertex-buffer support"; return false;
                }
                ready = true; reason = "Vertex buffers";
                if (!logged) { logged = true; System.out.println("[ZalithRender] Vertex-buffer cache enabled; " + GL11.glGetString(GL11.GL_VERSION)); }
            }
            return true;
        } catch (RuntimeException ex) { reason = "No current graphics context"; return false; }
    }
    private static void fail(RuntimeException ex) {
        failed = true; reason = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        System.err.println("[ZalithRender] Using Minecraft rendering after: " + reason);
        resetRequested = true;
    }
    public static int cachedMeshes() { return CACHE.count(); }
    public static long cachedBytes() { return CACHE.bytes(); }
    public static String status() {
        if (!Settings.enabled) return "ZalithRender: disabled in config";
        if (!blocked.isEmpty()) return "ZalithRender: native rendering (" + blocked + ")";
        if (worldHooks) return "ZalithRender: native rendering (custom world hooks)";
        if (!RenderTransformer.complete()) return "ZalithRender: waiting for vanilla render hooks";
        return "ZalithRender: " + accelerated + " VBO / " + nativeLists + " native; " + CACHE.count()
            + " cached; " + CACHE.bytes() / (1024 * 1024) + "/" + Settings.cacheBytes / (1024 * 1024) + " MiB; " + reason;
    }
    private static final class Capture {
        final Object owner;
        final int list, x, y, z;
        boolean unsafe;
        int draws, bytes, vertices, flags;
        Capture(Object owner, int list, int x, int y, int z) { this.owner = owner; this.list = list; this.x = x; this.y = y; this.z = z; }
    }
}
