/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleFirework {

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            Spark spark = new Spark(world, d, d2, d3, d4, d5, d6, Minecraft.func_71410_x().field_71452_i);
            spark.func_82338_g(0.99f);
            return spark;
        }
    }

    public static class Overlay
    extends Particle {
        protected Overlay(World world, double d, double d2, double d3) {
            super(world, d, d2, d3);
            this.field_70547_e = 4;
        }

        @Override
        public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
            \u2603 = 0.25f;
            \u2603 = 0.5f;
            \u2603 = 0.125f;
            \u2603 = 0.375f;
            \u2603 = 7.1f * MathHelper.func_76126_a(((float)this.field_70546_d + f - 1.0f) * 0.25f * (float)Math.PI);
            this.func_82338_g(0.6f - ((float)this.field_70546_d + f - 1.0f) * 0.25f * 0.5f);
            \u2603 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)f - field_70556_an);
            \u2603 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)f - field_70554_ao);
            \u2603 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)f - field_70555_ap);
            int n = this.func_189214_a(f);
            \u2603 = n >> 16 & 0xFFFF;
            \u2603 = n & 0xFFFF;
            bufferBuilder.func_181662_b(\u2603 - f2 * \u2603 - f5 * \u2603, \u2603 - f3 * \u2603, \u2603 - f4 * \u2603 - f6 * \u2603).func_187315_a(0.5, 0.375).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(\u2603, \u2603).func_181675_d();
            bufferBuilder.func_181662_b(\u2603 - f2 * \u2603 + f5 * \u2603, \u2603 + f3 * \u2603, \u2603 - f4 * \u2603 + f6 * \u2603).func_187315_a(0.5, 0.125).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(\u2603, \u2603).func_181675_d();
            bufferBuilder.func_181662_b(\u2603 + f2 * \u2603 + f5 * \u2603, \u2603 + f3 * \u2603, \u2603 + f4 * \u2603 + f6 * \u2603).func_187315_a(0.25, 0.125).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(\u2603, \u2603).func_181675_d();
            bufferBuilder.func_181662_b(\u2603 + f2 * \u2603 - f5 * \u2603, \u2603 - f3 * \u2603, \u2603 + f4 * \u2603 - f6 * \u2603).func_187315_a(0.25, 0.375).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(\u2603, \u2603).func_181675_d();
        }
    }

    public static class Spark
    extends ParticleSimpleAnimated {
        private boolean field_92054_ax;
        private boolean field_92048_ay;
        private final ParticleManager field_92047_az;
        private float field_92050_aA;
        private float field_92051_aB;
        private float field_92052_aC;
        private boolean field_92053_aD;

        public Spark(World world, double d, double d2, double d3, double d4, double d5, double d6, ParticleManager particleManager) {
            super(world, d, d2, d3, 160, 8, -0.004f);
            this.field_187129_i = d4;
            this.field_187130_j = d5;
            this.field_187131_k = d6;
            this.field_92047_az = particleManager;
            this.field_70544_f *= 0.75f;
            this.field_70547_e = 48 + this.field_187136_p.nextInt(12);
        }

        public void func_92045_e(boolean bl) {
            this.field_92054_ax = bl;
        }

        public void func_92043_f(boolean bl) {
            this.field_92048_ay = bl;
        }

        @Override
        public boolean func_187111_c() {
            return true;
        }

        @Override
        public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
            if (!this.field_92048_ay || this.field_70546_d < this.field_70547_e / 3 || (this.field_70546_d + this.field_70547_e) / 3 % 2 == 0) {
                super.func_180434_a(bufferBuilder, entity, f, f2, f3, f4, f5, f6);
            }
        }

        @Override
        public void func_189213_a() {
            super.func_189213_a();
            if (this.field_92054_ax && this.field_70546_d < this.field_70547_e / 2 && (this.field_70546_d + this.field_70547_e) % 2 == 0) {
                Spark spark = new Spark(this.field_187122_b, this.field_187126_f, this.field_187127_g, this.field_187128_h, 0.0, 0.0, 0.0, this.field_92047_az);
                spark.func_82338_g(0.99f);
                spark.func_70538_b(this.field_70552_h, this.field_70553_i, this.field_70551_j);
                spark.field_70546_d = spark.field_70547_e / 2;
                if (this.field_92053_aD) {
                    spark.field_92053_aD = true;
                    spark.field_92050_aA = this.field_92050_aA;
                    spark.field_92051_aB = this.field_92051_aB;
                    spark.field_92052_aC = this.field_92052_aC;
                }
                spark.field_92048_ay = this.field_92048_ay;
                this.field_92047_az.func_78873_a(spark);
            }
        }
    }

    public static class Starter
    extends Particle {
        private int field_92042_ax;
        private final ParticleManager field_92040_ay;
        private NBTTagList field_92039_az;
        boolean field_92041_a;

        public Starter(World world, double d, double d2, double d3, double d4, double d5, double d6, ParticleManager particleManager, @Nullable NBTTagCompound nBTTagCompound) {
            super(world, d, d2, d3, 0.0, 0.0, 0.0);
            this.field_187129_i = d4;
            this.field_187130_j = d5;
            this.field_187131_k = d6;
            this.field_92040_ay = particleManager;
            this.field_70547_e = 8;
            if (nBTTagCompound != null) {
                this.field_92039_az = nBTTagCompound.func_150295_c("Explosions", 10);
                if (this.field_92039_az.func_82582_d()) {
                    this.field_92039_az = null;
                } else {
                    this.field_70547_e = this.field_92039_az.func_74745_c() * 2 - 1;
                    for (int i = 0; i < this.field_92039_az.func_74745_c(); ++i) {
                        NBTTagCompound nBTTagCompound2 = this.field_92039_az.func_150305_b(i);
                        if (!nBTTagCompound2.func_74767_n("Flicker")) continue;
                        this.field_92041_a = true;
                        this.field_70547_e += 15;
                        break;
                    }
                }
            }
        }

        @Override
        public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        }

        @Override
        public void func_189213_a() {
            if (this.field_92042_ax == 0 && this.field_92039_az != null) {
                int n = this.func_92037_i();
                boolean \u26032 = false;
                if (this.field_92039_az.func_74745_c() >= 3) {
                    \u26032 = true;
                } else {
                    for (\u2603 = 0; \u2603 < this.field_92039_az.func_74745_c(); ++\u2603) {
                        NBTTagCompound nBTTagCompound = this.field_92039_az.func_150305_b(\u2603);
                        if (nBTTagCompound.func_74771_c("Type") != 1) continue;
                        \u26032 = true;
                        break;
                    }
                }
                SoundEvent \u26033 = \u26032 ? (n != 0 ? SoundEvents.field_187628_bn : SoundEvents.field_187625_bm) : (n != 0 ? SoundEvents.field_187622_bl : SoundEvents.field_187619_bk);
                this.field_187122_b.func_184134_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, \u26033, SoundCategory.AMBIENT, 20.0f, 0.95f + this.field_187136_p.nextFloat() * 0.1f, true);
            }
            if (this.field_92042_ax % 2 == 0 && this.field_92039_az != null && this.field_92042_ax / 2 < this.field_92039_az.func_74745_c()) {
                n = this.field_92042_ax / 2;
                NBTTagCompound \u26034 = this.field_92039_az.func_150305_b(n);
                byte \u26035 = \u26034.func_74771_c("Type");
                boolean \u26036 = \u26034.func_74767_n("Trail");
                boolean \u26037 = \u26034.func_74767_n("Flicker");
                int[] \u26038 = \u26034.func_74759_k("Colors");
                int[] \u26039 = \u26034.func_74759_k("FadeColors");
                if (\u26038.length == 0) {
                    \u26038 = new int[]{ItemDye.field_150922_c[0]};
                }
                if (\u26035 == 1) {
                    this.func_92035_a(0.5, 4, \u26038, \u26039, \u26036, \u26037);
                } else if (\u26035 == 2) {
                    this.func_92038_a(0.5, new double[][]{{0.0, 1.0}, {0.3455, 0.309}, {0.9511, 0.309}, {0.3795918367346939, -0.12653061224489795}, {0.6122448979591837, -0.8040816326530612}, {0.0, -0.35918367346938773}}, \u26038, \u26039, \u26036, \u26037, false);
                } else if (\u26035 == 3) {
                    this.func_92038_a(0.5, new double[][]{{0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}}, \u26038, \u26039, \u26036, \u26037, true);
                } else if (\u26035 == 4) {
                    this.func_92036_a(\u26038, \u26039, \u26036, \u26037);
                } else {
                    this.func_92035_a(0.25, 2, \u26038, \u26039, \u26036, \u26037);
                }
                \u2603 = \u26038[0];
                float \u260310 = (float)((\u2603 & 0xFF0000) >> 16) / 255.0f;
                float \u260311 = (float)((\u2603 & 0xFF00) >> 8) / 255.0f;
                float \u260312 = (float)((\u2603 & 0xFF) >> 0) / 255.0f;
                Overlay \u260313 = new Overlay(this.field_187122_b, this.field_187126_f, this.field_187127_g, this.field_187128_h);
                \u260313.func_70538_b(\u260310, \u260311, \u260312);
                this.field_92040_ay.func_78873_a(\u260313);
            }
            ++this.field_92042_ax;
            if (this.field_92042_ax > this.field_70547_e) {
                if (this.field_92041_a) {
                    n = this.func_92037_i() ? 1 : 0;
                    SoundEvent soundEvent = n != 0 ? SoundEvents.field_187640_br : SoundEvents.field_187637_bq;
                    this.field_187122_b.func_184134_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, soundEvent, SoundCategory.AMBIENT, 20.0f, 0.9f + this.field_187136_p.nextFloat() * 0.15f, true);
                }
                this.func_187112_i();
            }
        }

        private boolean func_92037_i() {
            Minecraft minecraft = Minecraft.func_71410_x();
            return minecraft == null || minecraft.func_175606_aa() == null || !(minecraft.func_175606_aa().func_70092_e(this.field_187126_f, this.field_187127_g, this.field_187128_h) < 256.0);
        }

        private void func_92034_a(double d, double d2, double d3, double d4, double d5, double d6, int[] nArray, int[] nArray2, boolean bl, boolean bl2) {
            Spark spark = new Spark(this.field_187122_b, d, d2, d3, d4, d5, d6, this.field_92040_ay);
            spark.func_82338_g(0.99f);
            spark.func_92045_e(bl);
            spark.func_92043_f(bl2);
            int \u26032 = this.field_187136_p.nextInt(nArray.length);
            spark.func_187146_c(nArray[\u26032]);
            if (nArray2 != null && nArray2.length > 0) {
                spark.func_187145_d(nArray2[this.field_187136_p.nextInt(nArray2.length)]);
            }
            this.field_92040_ay.func_78873_a(spark);
        }

        private void func_92035_a(double d, int n, int[] nArray, int[] nArray2, boolean bl, boolean bl2) {
            double d2 = this.field_187126_f;
            \u2603 = this.field_187127_g;
            \u2603 = this.field_187128_h;
            for (int i = -n; i <= n; ++i) {
                for (\u2603 = -n; \u2603 <= n; ++\u2603) {
                    for (\u2603 = -n; \u2603 <= n; ++\u2603) {
                        double d3 = (double)\u2603 + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 0.5;
                        \u2603 = (double)i + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 0.5;
                        \u2603 = (double)\u2603 + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 0.5;
                        \u2603 = (double)MathHelper.func_76133_a(d3 * d3 + \u2603 * \u2603 + \u2603 * \u2603) / d + this.field_187136_p.nextGaussian() * 0.05;
                        this.func_92034_a(d2, \u2603, \u2603, d3 / \u2603, \u2603 / \u2603, \u2603 / \u2603, nArray, nArray2, bl, bl2);
                        if (i == -n || i == n || \u2603 == -n || \u2603 == n) continue;
                        \u2603 += n * 2 - 1;
                    }
                }
            }
        }

        private void func_92038_a(double d, double[][] dArray, int[] nArray, int[] nArray2, boolean bl, boolean bl2, boolean bl3) {
            double d2 = dArray[0][0];
            \u2603 = dArray[0][1];
            this.func_92034_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, d2 * d, \u2603 * d, 0.0, nArray, nArray2, bl, bl2);
            float \u26032 = this.field_187136_p.nextFloat() * (float)Math.PI;
            \u2603 = bl3 ? 0.034 : 0.34;
            for (int i = 0; i < 3; ++i) {
                double d3 = (double)\u26032 + (double)((float)i * (float)Math.PI) * \u2603;
                \u2603 = d2;
                \u2603 = \u2603;
                for (int j = 1; j < dArray.length; ++j) {
                    double d4 = dArray[j][0];
                    \u2603 = dArray[j][1];
                    for (\u2603 = 0.25; \u2603 <= 1.0; \u2603 += 0.25) {
                        \u2603 = (\u2603 + (d4 - \u2603) * \u2603) * d;
                        \u2603 = (\u2603 + (\u2603 - \u2603) * \u2603) * d;
                        \u2603 = \u2603 * Math.sin(d3);
                        \u2603 *= Math.cos(d3);
                        for (\u2603 = -1.0; \u2603 <= 1.0; \u2603 += 2.0) {
                            this.func_92034_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, \u2603 * \u2603, \u2603, \u2603 * \u2603, nArray, nArray2, bl, bl2);
                        }
                    }
                    \u2603 = d4;
                    \u2603 = \u2603;
                }
            }
        }

        private void func_92036_a(int[] nArray, int[] nArray2, boolean bl, boolean bl2) {
            double d = this.field_187136_p.nextGaussian() * 0.05;
            \u2603 = this.field_187136_p.nextGaussian() * 0.05;
            for (int i = 0; i < 70; ++i) {
                double d2 = this.field_187129_i * 0.5 + this.field_187136_p.nextGaussian() * 0.15 + d;
                \u2603 = this.field_187131_k * 0.5 + this.field_187136_p.nextGaussian() * 0.15 + \u2603;
                \u2603 = this.field_187130_j * 0.5 + this.field_187136_p.nextDouble() * 0.5;
                this.func_92034_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, d2, \u2603, \u2603, nArray, nArray2, bl, bl2);
            }
        }

        @Override
        public int func_70537_b() {
            return 0;
        }
    }
}

