/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleSweepAttack
extends Particle {
    private static final ResourceLocation field_187137_a = new ResourceLocation("textures/entity/sweep.png");
    private static final VertexFormat field_187138_G = new VertexFormat().func_181721_a(DefaultVertexFormats.field_181713_m).func_181721_a(DefaultVertexFormats.field_181715_o).func_181721_a(DefaultVertexFormats.field_181714_n).func_181721_a(DefaultVertexFormats.field_181716_p).func_181721_a(DefaultVertexFormats.field_181717_q).func_181721_a(DefaultVertexFormats.field_181718_r);
    private int field_187139_H;
    private final int field_187140_I;
    private final TextureManager field_187141_J;
    private final float field_187142_K;

    protected ParticleSweepAttack(TextureManager textureManager, World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_187141_J = textureManager;
        this.field_187140_I = 4;
        this.field_70552_h = \u2603 = this.field_187136_p.nextFloat() * 0.6f + 0.4f;
        this.field_70553_i = \u2603;
        this.field_70551_j = \u2603;
        this.field_187142_K = 1.0f - (float)d4 * 0.5f;
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        int n = (int)(((float)this.field_187139_H + f) * 3.0f / (float)this.field_187140_I);
        if (n > 7) {
            return;
        }
        this.field_187141_J.func_110577_a(field_187137_a);
        float \u26032 = (float)(n % 4) / 4.0f;
        float \u26033 = \u26032 + 0.24975f;
        float \u26034 = (float)(n / 2) / 2.0f;
        float \u26035 = \u26034 + 0.4995f;
        float \u26036 = 1.0f * this.field_187142_K;
        float \u26037 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)f - field_70556_an);
        float \u26038 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)f - field_70554_ao);
        float \u26039 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)f - field_70555_ap);
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.func_179140_f();
        RenderHelper.func_74518_a();
        bufferBuilder.func_181668_a(7, field_187138_G);
        bufferBuilder.func_181662_b(\u26037 - f2 * \u26036 - f5 * \u26036, \u26038 - f3 * \u26036 * 0.5f, \u26039 - f4 * \u26036 - f6 * \u26036).func_187315_a(\u26033, \u26035).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        bufferBuilder.func_181662_b(\u26037 - f2 * \u26036 + f5 * \u26036, \u26038 + f3 * \u26036 * 0.5f, \u26039 - f4 * \u26036 + f6 * \u26036).func_187315_a(\u26033, \u26034).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        bufferBuilder.func_181662_b(\u26037 + f2 * \u26036 + f5 * \u26036, \u26038 + f3 * \u26036 * 0.5f, \u26039 + f4 * \u26036 + f6 * \u26036).func_187315_a(\u26032, \u26034).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        bufferBuilder.func_181662_b(\u26037 + f2 * \u26036 - f5 * \u26036, \u26038 - f3 * \u26036 * 0.5f, \u26039 + f4 * \u26036 - f6 * \u26036).func_187315_a(\u26032, \u26035).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        Tessellator.func_178181_a().func_78381_a();
        GlStateManager.func_179145_e();
    }

    @Override
    public int func_189214_a(float f) {
        return 61680;
    }

    @Override
    public void func_189213_a() {
        this.field_187123_c = this.field_187126_f;
        this.field_187124_d = this.field_187127_g;
        this.field_187125_e = this.field_187128_h;
        ++this.field_187139_H;
        if (this.field_187139_H == this.field_187140_I) {
            this.func_187112_i();
        }
    }

    @Override
    public int func_70537_b() {
        return 3;
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleSweepAttack(Minecraft.func_71410_x().func_110434_K(), world, d, d2, d3, d4, d5, d6);
        }
    }
}

