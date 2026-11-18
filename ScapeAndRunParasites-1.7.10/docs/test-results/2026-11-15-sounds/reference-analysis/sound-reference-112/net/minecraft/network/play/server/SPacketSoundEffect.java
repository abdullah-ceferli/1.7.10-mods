/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import org.apache.commons.lang3.Validate;

public class SPacketSoundEffect
implements Packet<INetHandlerPlayClient> {
    private SoundEvent field_186979_a;
    private SoundCategory field_186980_b;
    private int field_149217_b;
    private int field_149218_c;
    private int field_149215_d;
    private float field_149216_e;
    private float field_149214_f;

    public SPacketSoundEffect() {
    }

    public SPacketSoundEffect(SoundEvent soundEvent, SoundCategory soundCategory, double d, double d2, double d3, float f, float f2) {
        Validate.notNull((Object)soundEvent, (String)"sound", (Object[])new Object[0]);
        this.field_186979_a = soundEvent;
        this.field_186980_b = soundCategory;
        this.field_149217_b = (int)(d * 8.0);
        this.field_149218_c = (int)(d2 * 8.0);
        this.field_149215_d = (int)(d3 * 8.0);
        this.field_149216_e = f;
        this.field_149214_f = f2;
    }

    @Override
    public void func_148837_a(PacketBuffer packetBuffer) throws IOException {
        this.field_186979_a = SoundEvent.field_187505_a.func_148754_a(packetBuffer.func_150792_a());
        this.field_186980_b = packetBuffer.func_179257_a(SoundCategory.class);
        this.field_149217_b = packetBuffer.readInt();
        this.field_149218_c = packetBuffer.readInt();
        this.field_149215_d = packetBuffer.readInt();
        this.field_149216_e = packetBuffer.readFloat();
        this.field_149214_f = packetBuffer.readFloat();
    }

    @Override
    public void func_148840_b(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.func_150787_b(SoundEvent.field_187505_a.func_148757_b(this.field_186979_a));
        packetBuffer.func_179249_a(this.field_186980_b);
        packetBuffer.writeInt(this.field_149217_b);
        packetBuffer.writeInt(this.field_149218_c);
        packetBuffer.writeInt(this.field_149215_d);
        packetBuffer.writeFloat(this.field_149216_e);
        packetBuffer.writeFloat(this.field_149214_f);
    }

    public SoundEvent func_186978_a() {
        return this.field_186979_a;
    }

    public SoundCategory func_186977_b() {
        return this.field_186980_b;
    }

    public double func_149207_d() {
        return (float)this.field_149217_b / 8.0f;
    }

    public double func_149211_e() {
        return (float)this.field_149218_c / 8.0f;
    }

    public double func_149210_f() {
        return (float)this.field_149215_d / 8.0f;
    }

    public float func_149208_g() {
        return this.field_149216_e;
    }

    public float func_149209_h() {
        return this.field_149214_f;
    }

    @Override
    public void func_148833_a(INetHandlerPlayClient iNetHandlerPlayClient) {
        iNetHandlerPlayClient.func_184327_a(this);
    }
}

