/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Particle {
    private static final AxisAlignedBB field_187121_a = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    protected World field_187122_b;
    protected double field_187123_c;
    protected double field_187124_d;
    protected double field_187125_e;
    protected double field_187126_f;
    protected double field_187127_g;
    protected double field_187128_h;
    protected double field_187129_i;
    protected double field_187130_j;
    protected double field_187131_k;
    private AxisAlignedBB field_187120_G = field_187121_a;
    protected boolean field_187132_l;
    protected boolean field_190017_n;
    protected boolean field_187133_m;
    protected float field_187134_n = 0.6f;
    protected float field_187135_o = 1.8f;
    protected Random field_187136_p = new Random();
    protected int field_94054_b;
    protected int field_94055_c;
    protected float field_70548_b;
    protected float field_70549_c;
    protected int field_70546_d;
    protected int field_70547_e;
    protected float field_70544_f;
    protected float field_70545_g;
    protected float field_70552_h;
    protected float field_70553_i;
    protected float field_70551_j;
    protected float field_82339_as = 1.0f;
    protected TextureAtlasSprite field_187119_C;
    protected float field_190014_F;
    protected float field_190015_G;
    public static double field_70556_an;
    public static double field_70554_ao;
    public static double field_70555_ap;
    public static Vec3d field_190016_K;

    protected Particle(World world, double d, double d2, double d3) {
        this.field_187122_b = world;
        this.func_187115_a(0.2f, 0.2f);
        this.func_187109_b(d, d2, d3);
        this.field_187123_c = d;
        this.field_187124_d = d2;
        this.field_187125_e = d3;
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.field_70548_b = this.field_187136_p.nextFloat() * 3.0f;
        this.field_70549_c = this.field_187136_p.nextFloat() * 3.0f;
        this.field_70544_f = (this.field_187136_p.nextFloat() * 0.5f + 0.5f) * 2.0f;
        this.field_70547_e = (int)(4.0f / (this.field_187136_p.nextFloat() * 0.9f + 0.1f));
        this.field_70546_d = 0;
        this.field_190017_n = true;
    }

    public Particle(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        this(world, d, d2, d3);
        this.field_187129_i = d4 + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        this.field_187130_j = d5 + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        this.field_187131_k = d6 + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        float f = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
        \u2603 = MathHelper.func_76133_a(this.field_187129_i * this.field_187129_i + this.field_187130_j * this.field_187130_j + this.field_187131_k * this.field_187131_k);
        this.field_187129_i = this.field_187129_i / (double)\u2603 * (double)f * (double)0.4f;
        this.field_187130_j = this.field_187130_j / (double)\u2603 * (double)f * (double)0.4f + (double)0.1f;
        this.field_187131_k = this.field_187131_k / (double)\u2603 * (double)f * (double)0.4f;
    }

    public Particle func_70543_e(float f) {
        this.field_187129_i *= (double)f;
        this.field_187130_j = (this.field_187130_j - (double)0.1f) * (double)f + (double)0.1f;
        this.field_187131_k *= (double)f;
        return this;
    }

    public Particle func_70541_f(float f) {
        this.func_187115_a(0.2f * f, 0.2f * f);
        this.field_70544_f *= f;
        return this;
    }

    public void func_70538_b(float f, float f2, float f3) {
        this.field_70552_h = f;
        this.field_70553_i = f2;
        this.field_70551_j = f3;
    }

    public void func_82338_g(float f) {
        this.field_82339_as = f;
    }

    public boolean func_187111_c() {
        return false;
    }

    public float func_70534_d() {
        return this.field_70552_h;
    }

    public float func_70542_f() {
        return this.field_70553_i;
    }

    public float func_70535_g() {
        return this.field_70551_j;
    }

    public void func_187114_a(int n) {
        this.field_70547_e = n;
    }

    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        if (this.field_70546_d++ >= this.field_70547_e) {
            this.func_187112_i();
        }
        this.field_187130_j -= 0.04 * (double)this.field_70545_g;
        this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
        this.field_187129_i *= (double)0.98f;
        this.field_187130_j *= (double)0.98f;
        this.field_187131_k *= (double)0.98f;
        if (this.field_187132_l) {
            this.field_187129_i *= (double)0.7f;
            this.field_187131_k *= (double)0.7f;
        }
    }

    public void func_180434_a(BufferBuilder bufferBuilder2, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        BufferBuilder bufferBuilder2;
        \u2603 = (float)this.field_94054_b / 16.0f;
        \u2603 = \u2603 + 0.0624375f;
        \u2603 = (float)this.field_94055_c / 16.0f;
        \u2603 = \u2603 + 0.0624375f;
        \u2603 = 0.1f * this.field_70544_f;
        if (this.field_187119_C != null) {
            \u2603 = this.field_187119_C.func_94209_e();
            \u2603 = this.field_187119_C.func_94212_f();
            \u2603 = this.field_187119_C.func_94206_g();
            \u2603 = this.field_187119_C.func_94210_h();
        }
        \u2603 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)f - field_70556_an);
        \u2603 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)f - field_70554_ao);
        \u2603 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)f - field_70555_ap);
        int n = this.func_189214_a(f);
        \u2603 = n >> 16 & 0xFFFF;
        \u2603 = n & 0xFFFF;
        Vec3d[] \u26032 = new Vec3d[]{new Vec3d(-f2 * \u2603 - f5 * \u2603, -f3 * \u2603, -f4 * \u2603 - f6 * \u2603), new Vec3d(-f2 * \u2603 + f5 * \u2603, f3 * \u2603, -f4 * \u2603 + f6 * \u2603), new Vec3d(f2 * \u2603 + f5 * \u2603, f3 * \u2603, f4 * \u2603 + f6 * \u2603), new Vec3d(f2 * \u2603 - f5 * \u2603, -f3 * \u2603, f4 * \u2603 - f6 * \u2603)};
        if (this.field_190014_F != 0.0f) {
            float f7 = this.field_190014_F + (this.field_190014_F - this.field_190015_G) * f;
            \u2603 = MathHelper.func_76134_b(f7 * 0.5f);
            \u2603 = MathHelper.func_76126_a(f7 * 0.5f) * (float)Particle.field_190016_K.field_72450_a;
            \u2603 = MathHelper.func_76126_a(f7 * 0.5f) * (float)Particle.field_190016_K.field_72448_b;
            \u2603 = MathHelper.func_76126_a(f7 * 0.5f) * (float)Particle.field_190016_K.field_72449_c;
            Vec3d \u26033 = new Vec3d(\u2603, \u2603, \u2603);
            for (int i = 0; i < 4; ++i) {
                \u26032[i] = \u26033.func_186678_a(2.0 * \u26032[i].func_72430_b(\u26033)).func_178787_e(\u26032[i].func_186678_a((double)(\u2603 * \u2603) - \u26033.func_72430_b(\u26033))).func_178787_e(\u26033.func_72431_c(\u26032[i]).func_186678_a(2.0f * \u2603));
            }
        }
        bufferBuilder2.func_181662_b((double)\u2603 + \u26032[0].field_72450_a, (double)\u2603 + \u26032[0].field_72448_b, (double)\u2603 + \u26032[0].field_72449_c).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(\u2603, \u2603).func_181675_d();
        bufferBuilder2.func_181662_b((double)\u2603 + \u26032[1].field_72450_a, (double)\u2603 + \u26032[1].field_72448_b, (double)\u2603 + \u26032[1].field_72449_c).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(\u2603, \u2603).func_181675_d();
        bufferBuilder2.func_181662_b((double)\u2603 + \u26032[2].field_72450_a, (double)\u2603 + \u26032[2].field_72448_b, (double)\u2603 + \u26032[2].field_72449_c).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(\u2603, \u2603).func_181675_d();
        bufferBuilder2.func_181662_b((double)\u2603 + \u26032[3].field_72450_a, (double)\u2603 + \u26032[3].field_72448_b, (double)\u2603 + \u26032[3].field_72449_c).func_187315_a(\u2603, \u2603).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(\u2603, \u2603).func_181675_d();
    }

    public int func_70537_b() {
        return 0;
    }

    public void func_187117_a(TextureAtlasSprite textureAtlasSprite) {
        int n = this.func_70537_b();
        if (n != 1) {
            throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
        }
        this.field_187119_C = textureAtlasSprite;
    }

    public void func_70536_a(int n) {
        if (this.func_70537_b() != 0) {
            throw new RuntimeException("Invalid call to Particle.setMiscTex");
        }
        this.field_94054_b = n % 16;
        this.field_94055_c = n / 16;
    }

    public void func_94053_h() {
        ++this.field_94054_b;
    }

    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.field_187126_f + "," + this.field_187127_g + "," + this.field_187128_h + "), RGBA (" + this.field_70552_h + "," + this.field_70553_i + "," + this.field_70551_j + "," + this.field_82339_as + "), Age " + this.field_70546_d;
    }

    public void func_187112_i() {
        this.field_187133_m = true;
    }

    protected void func_187115_a(float f, float f2) {
        if (f != this.field_187134_n || f2 != this.field_187135_o) {
            this.field_187134_n = f;
            this.field_187135_o = f2;
            AxisAlignedBB axisAlignedBB = this.func_187116_l();
            this.func_187108_a(new AxisAlignedBB(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, axisAlignedBB.field_72340_a + (double)this.field_187134_n, axisAlignedBB.field_72338_b + (double)this.field_187135_o, axisAlignedBB.field_72339_c + (double)this.field_187134_n));
        }
    }

    public void func_187109_b(double d, double d2, double d3) {
        this.field_187126_f = d;
        this.field_187127_g = d2;
        this.field_187128_h = d3;
        float f = this.field_187134_n / 2.0f;
        \u2603 = this.field_187135_o;
        this.func_187108_a(new AxisAlignedBB(d - (double)f, d2, d3 - (double)f, d + (double)f, d2 + (double)\u2603, d3 + (double)f));
    }

    public void func_187110_a(double d5, double d22, double d32) {
        double d4;
        double d5;
        \u2603 = d5;
        d4 = d22;
        \u2603 = d32;
        if (this.field_190017_n) {
            double d32;
            double d22;
            List<AxisAlignedBB> list = this.field_187122_b.func_184144_a(null, this.func_187116_l().func_72321_a(d5, d22, d32));
            for (AxisAlignedBB axisAlignedBB : list) {
                d22 = axisAlignedBB.func_72323_b(this.func_187116_l(), d22);
            }
            this.func_187108_a(this.func_187116_l().func_72317_d(0.0, d22, 0.0));
            for (AxisAlignedBB axisAlignedBB : list) {
                d5 = axisAlignedBB.func_72316_a(this.func_187116_l(), d5);
            }
            this.func_187108_a(this.func_187116_l().func_72317_d(d5, 0.0, 0.0));
            for (AxisAlignedBB axisAlignedBB : list) {
                d32 = axisAlignedBB.func_72322_c(this.func_187116_l(), d32);
            }
            this.func_187108_a(this.func_187116_l().func_72317_d(0.0, 0.0, d32));
        } else {
            this.func_187108_a(this.func_187116_l().func_72317_d(d5, d22, d32));
        }
        this.func_187118_j();
        boolean bl = this.field_187132_l = d4 != d22 && d4 < 0.0;
        if (\u2603 != d5) {
            this.field_187129_i = 0.0;
        }
        if (\u2603 != d32) {
            this.field_187131_k = 0.0;
        }
    }

    protected void func_187118_j() {
        AxisAlignedBB axisAlignedBB = this.func_187116_l();
        this.field_187126_f = (axisAlignedBB.field_72340_a + axisAlignedBB.field_72336_d) / 2.0;
        this.field_187127_g = axisAlignedBB.field_72338_b;
        this.field_187128_h = (axisAlignedBB.field_72339_c + axisAlignedBB.field_72334_f) / 2.0;
    }

    public int func_189214_a(float f) {
        BlockPos blockPos = new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h);
        if (this.field_187122_b.func_175667_e(blockPos)) {
            return this.field_187122_b.func_175626_b(blockPos, 0);
        }
        return 0;
    }

    public boolean func_187113_k() {
        return !this.field_187133_m;
    }

    public AxisAlignedBB func_187116_l() {
        return this.field_187120_G;
    }

    public void func_187108_a(AxisAlignedBB axisAlignedBB) {
        this.field_187120_G = axisAlignedBB;
    }
}

