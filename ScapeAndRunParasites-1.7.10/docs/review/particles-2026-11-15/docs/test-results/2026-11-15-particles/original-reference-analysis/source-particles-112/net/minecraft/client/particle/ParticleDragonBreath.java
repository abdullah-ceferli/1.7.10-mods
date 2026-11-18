/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleDragonBreath
extends Particle {
    private final float field_187143_a;
    private boolean field_187144_G;

    protected ParticleDragonBreath(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.field_187129_i = d4;
        this.field_187130_j = d5;
        this.field_187131_k = d6;
        this.field_70552_h = MathHelper.func_151240_a(this.field_187136_p, 0.7176471f, 0.8745098f);
        this.field_70553_i = MathHelper.func_151240_a(this.field_187136_p, 0.0f, 0.0f);
        this.field_70551_j = MathHelper.func_151240_a(this.field_187136_p, 0.8235294f, 0.9764706f);
        this.field_70544_f *= 0.75f;
        this.field_187143_a = this.field_70544_f;
        this.field_70547_e = (int)(20.0 / ((double)this.field_187136_p.nextFloat() * 0.8 + 0.2));
        this.field_187144_G = false;
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        if (this.field_70546_d++ >= this.field_70547_e) {
            this.func_187112_i();
            return;
        }
        this.func_70536_a(3 * this.field_70546_d / this.field_70547_e + 5);
        if (this.field_187132_l) {
            this.field_187130_j = 0.0;
            this.field_187144_G = true;
        }
        if (this.field_187144_G) {
            this.field_187130_j += 0.002;
        }
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        if (this.field_187127_g == this.field_187124_d) {
            this.field_187129_i *= 1.1;
            this.field_187131_k *= 1.1;
        }
        this.field_187129_i *= (double)0.96f;
        this.field_187131_k *= (double)0.96f;
        if (this.field_187144_G) {
            this.field_187130_j *= (double)0.96f;
        }
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.field_70544_f = this.field_187143_a * MathHelper.func_76131_a(((float)this.field_70546_d + f) / (float)this.field_70547_e * 32.0f, 0.0f, 1.0f);
        super.func_180434_a(bufferBuilder, entity, f, f2, f3, f4, f5, f6);
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleDragonBreath(world, d, d2, d3, d4, d5, d6);
        }
    }
}

