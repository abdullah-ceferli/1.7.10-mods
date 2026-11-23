package dev.zahin.zalithrender.render;

import org.lwjgl.opengl.GL15;

final class MeshCache {
    private final IntTable<Mesh> entries = new IntTable<Mesh>();
    private Mesh oldest, newest;
    private long bytes;

    Mesh get(int list) {
        Mesh mesh = entries.get(list);
        if (mesh != null && newest != mesh) { unlink(mesh); append(mesh); }
        return mesh;
    }
    int count() { return entries.size(); }
    long bytes() { return bytes; }
    void put(Mesh mesh) {
        remove(mesh.list, true);
        while (oldest != null && (bytes + mesh.bytes > Settings.cacheBytes || entries.size() >= Settings.maxMeshes)) {
            remove(oldest.list, true);
        }
        entries.put(mesh.list, mesh); append(mesh); bytes += mesh.bytes;
    }
    void remove(int list, boolean delete) {
        Mesh mesh = entries.remove(list);
        if (mesh == null) return;
        unlink(mesh); bytes -= mesh.bytes;
        if (delete) GL15.glDeleteBuffers(mesh.buffer);
    }
    void clear(boolean delete) {
        Mesh mesh = oldest;
        while (mesh != null) {
            if (delete) GL15.glDeleteBuffers(mesh.buffer);
            mesh = mesh.newer;
        }
        entries.clear(); oldest = newest = null; bytes = 0;
    }
    private void unlink(Mesh mesh) {
        if (mesh.older == null) oldest = mesh.newer; else mesh.older.newer = mesh.newer;
        if (mesh.newer == null) newest = mesh.older; else mesh.newer.older = mesh.older;
        mesh.older = mesh.newer = null;
    }
    private void append(Mesh mesh) {
        mesh.older = newest; mesh.newer = null;
        if (newest == null) oldest = mesh; else newest.newer = mesh;
        newest = mesh;
    }
}
