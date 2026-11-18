/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleMobAppearance
extends Particle {
    private EntityLivingBase field_174844_a;

    protected ParticleMobAppearance(World world, double d, double d2, double d3) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.field_70552_h = 1.0f;
        this.field_70553_i = 1.0f;
        this.field_70551_j = 1.0f;
        this.field_187129_i = 0.0;
        this.field_187130_j = 0.0;
        this.field_187131_k = 0.0;
        this.field_70545_g = 0.0f;
        this.field_70547_e = 30;
    }

    @Override
    public int func_70537_b() {
        return 3;
    }

    @Override
    public void func_189213_a() {
        super.func_189213_a();
        if (this.field_174844_a == null) {
            EntityElderGuardian entityElderGuardian = new EntityElderGuardian(this.field_187122_b);
            entityElderGuardian.func_190767_di();
            this.field_174844_a = entityElderGuardian;
        }
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        if (this.field_174844_a == null) {
            return;
        }
        RenderManager renderManager = Minecraft.func_71410_x().func_175598_ae();
        renderManager.func_178628_a(Particle.field_70556_an, Particle.field_70554_ao, Particle.field_70555_ap);
        float \u26032 = 0.42553192f;
        float \u26033 = ((float)this.field_70546_d + f) / (float)this.field_70547_e;
        GlStateManager.func_179132_a(true);
        GlStateManager.func_179147_l();
        GlStateManager.func_179126_j();
        GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        float \u26034 = 240.0f;
        OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0f, 240.0f);
        GlStateManager.func_179094_E();
        float \u26035 = 0.05f + 0.5f * MathHelper.func_76126_a(\u26033 * (float)Math.PI);
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, \u26035);
        GlStateManager.func_179109_b(0.0f, 1.8f, 0.0f);
        GlStateManager.func_179114_b(180.0f - entity.field_70177_z, 0.0f, 1.0f, 0.0f);
        GlStateManager.func_179114_b(60.0f - 150.0f * \u26033 - entity.field_70125_A, 1.0f, 0.0f, 0.0f);
        GlStateManager.func_179109_b(0.0f, -0.4f, -1.5f);
        GlStateManager.func_179152_a(0.42553192f, 0.42553192f, 0.42553192f);
        this.field_174844_a.field_70177_z = 0.0f;
        this.field_174844_a.field_70759_as = 0.0f;
        this.field_174844_a.field_70126_B = 0.0f;
        this.field_174844_a.field_70758_at = 0.0f;
        renderManager.func_188391_a(this.field_174844_a, 0.0, 0.0, 0.0, 0.0f, f, false);
        GlStateManager.func_179121_F();
        GlStateManager.func_179126_j();
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public Particle func_178902_a(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
            return new ParticleMobAppearance(world, d, d2, d3);
        }
    }
}

