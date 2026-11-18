/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleFootStep
extends Particle {
    private static final ResourceLocation field_110126_a = new ResourceLocation("textures/particle/footprint.png");
    private int field_70576_a;
    private final int field_70578_aq;
    private final TextureManager field_70577_ar;

    protected ParticleFootStep(TextureManager textureManager, World world, double d, double d2, double d3) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_70577_ar = textureManager;
        this.field_187129_i = 0.0;
        this.field_187130_j = 0.0;
        this.field_187131_k = 0.0;
        this.field_70578_aq = 200;
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_70576_a + f) / (float)this.field_70578_aq;
        if ((\u2603 = 2.0f - (\u2603 *= \u2603) * 2.0f) > 1.0f) {
            \u2603 = 1.0f;
        }
        \u2603 *= 0.2f;
        GlStateManager.func_179140_f();
        \u2603 = 0.125f;
        \u2603 = (float)(this.field_187126_f - field_70556_an);
        \u2603 = (float)(this.field_187127_g - field_70554_ao);
        \u2603 = (float)(this.field_187128_h - field_70555_ap);
        \u2603 = this.field_187122_b.func_175724_o(new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h));
        this.field_70577_ar.func_110577_a(field_110126_a);
        GlStateManager.func_179147_l();
        GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        bufferBuilder.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        bufferBuilder.func_181662_b(\u2603 - 0.125f, \u2603, \u2603 + 0.125f).func_187315_a(0.0, 1.0).func_181666_a(\u2603, \u2603, \u2603, \u2603).func_181675_d();
        bufferBuilder.func_181662_b(\u2603 + 0.125f, \u2603, \u2603 + 0.125f).func_187315_a(1.0, 1.0).func_181666_a(\u2603, \u2603, \u2603, \u2603).func_181675_d();
        bufferBuilder.func_181662_b(\u2603 + 0.125f, \u2603, \u2603 - 0.125f).func_187315_a(1.0, 0.0).func_181666_a(\u2603, \u2603, \u2603, \u2603).func_181675_d();
        bufferBuilder.func_181662_b(\u2603 - 0.125f, \u2603, \u2603 - 0.125f).func_187315_a(0.0, 0.0).func_181666_a(\u2603, \u2603, \u2603, \u2603).func_181675_d();
        Tessellator.func_178181_a().func_78381_a();
        GlStateManager.func_179084_k();
        GlStateManager.func_179145_e();
    }

    @Override
    public void func_189213_a() {
        ++this.field_70576_a;
        if (this.field_70576_a == this.field_70578_aq) {
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
            return new ParticleFootStep(Minecraft.func_71410_x().func_110434_K(), world, d, d2, d3);
        }
    }
}

