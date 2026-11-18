/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ParticleRain
extends Particle {
    protected ParticleRain(World world, double d, double d2, double d3) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_187129_i *= (double)0.3f;
        this.field_187130_j = Math.random() * (double)0.2f + (double)0.1f;
        this.field_187131_k *= (double)0.3f;
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.func_70536_a(19 + this.field_187136_p.nextInt(4));
        this.func_187115_a(0.01f, 0.01f);
        this.field_70545_g = 0.06f;
        this.field_70547_e = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void func_189213_a() {
        Material material;
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        this.field_187130_j -= (double)this.field_70545_g;
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= (double)0.98f;
        this.field_187130_j *= (double)0.98f;
        this.field_187131_k *= (double)0.98f;
        if (this.field_70547_e-- <= 0) {
            this.func_187112_i();
        }
        if (this.field_187132_l) {
            if (Math.random() < 0.5) {
                this.func_187112_i();
            }
            this.field_187129_i *= (double)0.7f;
            this.field_187131_k *= (double)0.7f;
        }
        if ((material = (\u2603 = this.field_187122_b.func_180495_p(\u2603 = new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h))).func_185904_a()).func_76224_d() || material.func_76220_a()) {
            double d = \u2603.func_177230_c() instanceof BlockLiquid ? (double)(1.0f - BlockLiquid.func_149801_b(\u2603.func_177229_b(BlockLiquid.field_176367_b))) : \u2603.func_185900_c((IBlockAccess)this.field_187122_b, (BlockPos)\u2603).field_72337_e;
            \u2603 = (double)MathHelper.func_76128_c(this.field_187127_g) + d;
            if (this.field_187127_g < \u2603) {
                this.func_187112_i();
            }
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleRain(world, d, d2, d3);
        }
    }
}

