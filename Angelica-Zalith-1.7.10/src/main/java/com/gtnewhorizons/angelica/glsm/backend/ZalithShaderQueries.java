package com.gtnewhorizons.angelica.glsm.backend;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.StandardCharsets;
import org.lwjgl.opengl.GL20;

/** Separate output buffers avoid the combined size/type overload in mobile LWJGL bridges. */
public final class ZalithShaderQueries {
    private static final ThreadLocal<Scratch> SCRATCH = new ThreadLocal<Scratch>() {
        @Override
        protected Scratch initialValue() {
            return new Scratch();
        }
    };

    private ZalithShaderQueries() {}

    public static String getActiveUniform(int program, int index, int maxLength, IntBuffer sizeType) {
        Scratch scratch = prepare(maxLength, sizeType);
        GL20.glGetActiveUniform(program, index, scratch.length, scratch.size, scratch.type, scratch.name);
        return finish(scratch, sizeType);
    }

    public static String getActiveAttrib(int program, int index, int maxLength, IntBuffer sizeType) {
        Scratch scratch = prepare(maxLength, sizeType);
        GL20.glGetActiveAttrib(program, index, scratch.length, scratch.size, scratch.type, scratch.name);
        return finish(scratch, sizeType);
    }

    private static Scratch prepare(int maxLength, IntBuffer sizeType) {
        if (sizeType == null) throw new NullPointerException("sizeType");
        if (sizeType.remaining() < 2) throw new IllegalArgumentException("sizeType needs two remaining integers");
        if (sizeType.isReadOnly()) throw new ReadOnlyBufferException();
        if (maxLength < 1) throw new IllegalArgumentException("maxLength must be positive");
        Scratch scratch = SCRATCH.get();
        scratch.length.clear();
        scratch.size.clear();
        scratch.type.clear();
        scratch.length.put(0, 0);
        scratch.size.put(0, 0);
        scratch.type.put(0, 0);
        if (scratch.name.capacity() < maxLength) {
            scratch.name = directBytes(maxLength);
        }
        scratch.name.clear();
        scratch.name.limit(maxLength);
        return scratch;
    }

    private static String finish(Scratch scratch, IntBuffer sizeType) {
        int length = scratch.length.get(0);
        if (length < 0 || length >= scratch.name.limit()) {
            throw new IllegalStateException("Invalid shader name length returned by renderer: " + length);
        }
        int offset = sizeType.position();
        sizeType.put(offset, scratch.size.get(0));
        sizeType.put(offset + 1, scratch.type.get(0));
        byte[] bytes = new byte[length];
        scratch.name.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static ByteBuffer directBytes(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    private static final class Scratch {
        final IntBuffer length = directBytes(4).asIntBuffer();
        final IntBuffer size = directBytes(4).asIntBuffer();
        final IntBuffer type = directBytes(4).asIntBuffer();
        ByteBuffer name = directBytes(256);
    }
}
