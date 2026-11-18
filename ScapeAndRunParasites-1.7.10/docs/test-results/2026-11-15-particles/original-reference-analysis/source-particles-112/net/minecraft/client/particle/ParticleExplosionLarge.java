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

public class ParticleExplosionLarge
extends Particle {
    private static final ResourceLocation field_110127_a = new ResourceLocation("textures/entity/explosion.png");
    private static final VertexFormat field_181549_az = new VertexFormat().func_181721_a(DefaultVertexFormats.field_181713_m).func_181721_a(DefaultVertexFormats.field_181715_o).func_181721_a(DefaultVertexFormats.field_181714_n).func_181721_a(DefaultVertexFormats.field_181716_p).func_181721_a(DefaultVertexFormats.field_181717_q).func_181721_a(DefaultVertexFormats.field_181718_r);
    private int field_70581_a;
    private final int field_70584_aq;
    private final TextureManager field_70583_ar;
    private final float field_70582_as;

    protected ParticleExplosionLarge(TextureManager textureManager, World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_70583_ar = textureManager;
        this.field_70584_aq = 6 + this.field_187136_p.nextInt(4);
        this.field_70552_h = \u2603 = this.field_187136_p.nextFloat() * 0.6f + 0.4f;
        this.field_70553_i = \u2603;
        this.field_70551_j = \u2603;
        this.field_70582_as = 1.0f - (float)d4 * 0.5f;
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        int n = (int)(((float)this.field_70581_a + f) * 15.0f / (float)this.field_70584_aq);
        if (n > 15) {
            return;
        }
        this.field_70583_ar.func_110577_a(field_110127_a);
        float \u26032 = (float)(n % 4) / 4.0f;
        float \u26033 = \u26032 + 0.24975f;
        float \u26034 = (float)(n / 4) / 4.0f;
        float \u26035 = \u26034 + 0.24975f;
        float \u26036 = 2.0f * this.field_70582_as;
        float \u26037 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)f - field_70556_an);
        float \u26038 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)f - field_70554_ao);
        float \u26039 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)f - field_70555_ap);
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.func_179140_f();
        RenderHelper.func_74518_a();
        bufferBuilder.func_181668_a(7, field_181549_az);
        bufferBuilder.func_181662_b(\u26037 - f2 * \u26036 - f5 * \u26036, \u26038 - f3 * \u26036, \u26039 - f4 * \u26036 - f6 * \u26036).func_187315_a(\u26033, \u26035).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        bufferBuilder.func_181662_b(\u26037 - f2 * \u26036 + f5 * \u26036, \u26038 + f3 * \u26036, \u26039 - f4 * \u26036 + f6 * \u26036).func_187315_a(\u26033, \u26034).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        bufferBuilder.func_181662_b(\u26037 + f2 * \u26036 + f5 * \u26036, \u26038 + f3 * \u26036, \u26039 + f4 * \u26036 + f6 * \u26036).func_187315_a(\u26032, \u26034).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
        bufferBuilder.func_181662_b(\u26037 + f2 * \u26036 - f5 * \u26036, \u26038 - f3 * \u26036, \u26039 + f4 * \u26036 - f6 * \u26036).func_187315_a(\u26032, \u26035).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0f).func_187314_a(0, 240).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
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
        ++this.field_70581_a;
        if (this.field_70581_a == this.field_70584_aq) {
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
            return new ParticleExplosionLarge(Minecraft.func_71410_x().func_110434_K(), world, d, d2, d3, d4, d5, d6);
        }
    }
}

