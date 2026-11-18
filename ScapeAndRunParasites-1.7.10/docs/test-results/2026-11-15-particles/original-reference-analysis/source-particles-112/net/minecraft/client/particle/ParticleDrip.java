/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleDrip
extends Particle {
    private final Material field_70563_a;
    private int field_70564_aq;

    protected ParticleDrip(World world, double d, double d2, double d3, Material material) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_187129_i = 0.0;
        this.field_187130_j = 0.0;
        this.field_187131_k = 0.0;
        if (material == Material.field_151586_h) {
            this.field_70552_h = 0.0f;
            this.field_70553_i = 0.0f;
            this.field_70551_j = 1.0f;
        } else {
            this.field_70552_h = 1.0f;
            this.field_70553_i = 0.0f;
            this.field_70551_j = 0.0f;
        }
        this.func_70536_a(113);
        this.func_187115_a(0.01f, 0.01f);
        this.field_70545_g = 0.06f;
        this.field_70563_a = material;
        this.field_70564_aq = 40;
        this.field_70547_e = (int)(64.0 / (Math.random() * 0.8 + 0.2));
        this.field_187129_i = 0.0;
        this.field_187130_j = 0.0;
        this.field_187131_k = 0.0;
    }

    @Override
    public int func_189214_a(float f) {
        if (this.field_70563_a == Material.field_151586_h) {
            return super.func_189214_a(f);
        }
        return 257;
    }

    @Override
    public void func_189213_a() {
        Material material;
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        if (this.field_70563_a == Material.field_151586_h) {
            this.field_70552_h = 0.2f;
            this.field_70553_i = 0.3f;
            this.field_70551_j = 1.0f;
        } else {
            this.field_70552_h = 1.0f;
            this.field_70553_i = 16.0f / (float)(40 - this.field_70564_aq + 16);
            this.field_70551_j = 4.0f / (float)(40 - this.field_70564_aq + 8);
        }
        this.field_187130_j -= (double)this.field_70545_g;
        if (this.field_70564_aq-- > 0) {
            this.field_187129_i *= 0.02;
            this.field_187130_j *= 0.02;
            this.field_187131_k *= 0.02;
            this.func_70536_a(113);
        } else {
            this.func_70536_a(112);
        }
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= (double)0.98f;
        this.field_187130_j *= (double)0.98f;
        this.field_187131_k *= (double)0.98f;
        if (this.field_70547_e-- <= 0) {
            this.func_187112_i();
        }
        if (this.field_187132_l) {
            if (this.field_70563_a == Material.field_151586_h) {
                this.func_187112_i();
                this.field_187122_b.func_175688_a(EnumParticleTypes.WATER_SPLASH, this.field_187126_f, this.field_187127_g, this.field_187128_h, 0.0, 0.0, 0.0, new int[0]);
            } else {
                this.func_70536_a(114);
            }
            this.field_187129_i *= (double)0.7f;
            this.field_187131_k *= (double)0.7f;
        }
        if ((material = (\u2603 = this.field_187122_b.func_180495_p(\u2603 = new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h))).func_185904_a()).func_76224_d() || material.func_76220_a()) {
            double d = 0.0;
            if (\u2603.func_177230_c() instanceof BlockLiquid) {
                d = BlockLiquid.func_149801_b(\u2603.func_177229_b(BlockLiquid.field_176367_b));
            }
            if (this.field_187127_g < (\u2603 = (double)(MathHelper.func_76128_c(this.field_187127_g) + 1) - d)) {
                this.func_187112_i();
            }
        }
    }

    public static class LavaFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleDrip(world, d, d2, d3, Material.field_151587_i);
        }
    }

    public static class WaterFactory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleDrip(world, d, d2, d3, Material.field_151586_h);
        }
    }
}

