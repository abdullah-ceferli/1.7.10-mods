package com.dhanantry.scapeandrunparasites.backport.datasync;
import java.io.IOException;import net.minecraft.network.PacketBuffer;
public interface DataSerializer<T> {
    void write(PacketBuffer buffer,T value)throws IOException;
    T read(PacketBuffer buffer)throws IOException;
    DataParameter<T> createKey(int id);
    T copyValue(T value);
    T validate(Object value,boolean registration);
}
