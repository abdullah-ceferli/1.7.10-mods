/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleItemPickup
extends Particle {
    private final Entity field_174840_a;
    private final Entity field_174843_ax;
    private int field_70594_ar;
    private final int field_70593_as;
    private final float field_174841_aA;
    private final RenderManager field_174842_aB = Minecraft.func_71410_x().func_175598_ae();

    public ParticleItemPickup(World world, Entity entity, Entity entity2, float f) {
        super(world, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, entity.field_70159_w, entity.field_70181_x, entity.field_70179_y);
        this.field_174840_a = entity;
        this.field_174843_ax = entity2;
        this.field_70593_as = 3;
        this.field_174841_aA = f;
    }

    @Override
    public void func_180434_a(BufferBuilder bufferBuilder, Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        \u2603 = ((float)this.field_70594_ar + f) / (float)this.field_70593_as;
        \u2603 *= \u2603;
        double d = this.field_174840_a.field_70165_t;
        \u2603 = this.field_174840_a.field_70163_u;
        \u2603 = this.field_174840_a.field_70161_v;
        \u2603 = this.field_174843_ax.field_70142_S + (this.field_174843_ax.field_70165_t - this.field_174843_ax.field_70142_S) * (double)f;
        \u2603 = this.field_174843_ax.field_70137_T + (this.field_174843_ax.field_70163_u - this.field_174843_ax.field_70137_T) * (double)f + (double)this.field_174841_aA;
        \u2603 = this.field_174843_ax.field_70136_U + (this.field_174843_ax.field_70161_v - this.field_174843_ax.field_70136_U) * (double)f;
        \u2603 = d + (\u2603 - d) * (double)\u2603;
        \u2603 = \u2603 + (\u2603 - \u2603) * (double)\u2603;
        \u2603 = \u2603 + (\u2603 - \u2603) * (double)\u2603;
        int \u26032 = this.func_189214_a(f);
        int \u26033 = \u26032 % 65536;
        int \u26034 = \u26032 / 65536;
        OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, \u26033, \u26034);
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.func_179145_e();
        this.field_174842_aB.func_188391_a(this.field_174840_a, \u2603 -= field_70556_an, \u2603 -= field_70554_ao, \u2603 -= field_70555_ap, this.field_174840_a.field_70177_z, f, false);
    }

    @Override
    public void func_189213_a() {
        ++this.field_70594_ar;
        if (this.field_70594_ar == this.field_70593_as) {
            this.func_187112_i();
        }
    }

    @Override
    public int func_70537_b() {
        return 3;
    }
}

