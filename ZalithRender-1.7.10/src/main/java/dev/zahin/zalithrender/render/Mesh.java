package dev.zahin.zalithrender.render;

final class Mesh {
    final int list, buffer, bytes, vertices, flags, x, y, z;
    Mesh older, newer;
    Mesh(int list, int buffer, int bytes, int vertices, int flags, int x, int y, int z) {
        this.list = list; this.buffer = buffer; this.bytes = bytes; this.vertices = vertices;
        this.flags = flags; this.x = x; this.y = y; this.z = z;
    }
}
