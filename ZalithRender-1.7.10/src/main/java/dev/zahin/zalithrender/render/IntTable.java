package dev.zahin.zalithrender.render;

import java.util.Arrays;

/** Primitive keys avoid allocating Integer objects for every chunk draw. */
public final class IntTable<V> {
    private int[] keys = new int[16];
    private Object[] values = new Object[16];
    private byte[] states = new byte[16];
    private int size, occupied;

    private static int hash(int key) {
        key ^= key >>> 16;
        key *= 0x7feb352d;
        key ^= key >>> 15;
        key *= 0x846ca68b;
        return key ^ (key >>> 16);
    }

    @SuppressWarnings("unchecked")
    public V get(int key) {
        int slot = hash(key) & (keys.length - 1);
        while (states[slot] != 0) {
            if (states[slot] == 1 && keys[slot] == key) return (V) values[slot];
            slot = (slot + 1) & (keys.length - 1);
        }
        return null;
    }

    public void put(int key, V value) {
        if (value == null) throw new NullPointerException("value");
        if ((occupied + 1) * 3 >= keys.length * 2) rehash(size * 3 < keys.length ? keys.length : keys.length * 2);
        int slot = hash(key) & (keys.length - 1), deleted = -1;
        while (states[slot] != 0) {
            if (states[slot] == 1 && keys[slot] == key) { values[slot] = value; return; }
            if (states[slot] == 2 && deleted < 0) deleted = slot;
            slot = (slot + 1) & (keys.length - 1);
        }
        if (deleted >= 0) slot = deleted;
        else occupied++;
        states[slot] = 1;
        keys[slot] = key;
        values[slot] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    public V remove(int key) {
        int slot = hash(key) & (keys.length - 1);
        while (states[slot] != 0) {
            if (states[slot] == 1 && keys[slot] == key) {
                V value = (V) values[slot];
                states[slot] = 2;
                values[slot] = null;
                size--;
                return value;
            }
            slot = (slot + 1) & (keys.length - 1);
        }
        return null;
    }

    public int size() { return size; }
    public void clear() { Arrays.fill(states, (byte) 0); Arrays.fill(values, null); size = occupied = 0; }

    @SuppressWarnings("unchecked")
    private void rehash(int capacity) {
        int[] oldKeys = keys;
        Object[] oldValues = values;
        byte[] oldStates = states;
        keys = new int[capacity]; values = new Object[capacity]; states = new byte[capacity];
        size = occupied = 0;
        for (int i = 0; i < oldKeys.length; i++) if (oldStates[i] == 1) put(oldKeys[i], (V) oldValues[i]);
    }
}
