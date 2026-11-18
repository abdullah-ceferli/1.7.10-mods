/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ModelGuardian
extends ModelBase {
    private final ModelRenderer field_178710_a;
    private final ModelRenderer field_178708_b;
    private final ModelRenderer[] field_178709_c;
    private final ModelRenderer[] field_178707_d;

    public ModelGuardian() {
        this.field_78090_t = 64;
        this.field_78089_u = 64;
        this.field_178709_c = new ModelRenderer[12];
        this.field_178710_a = new ModelRenderer(this);
        this.field_178710_a.func_78784_a(0, 0).func_78789_a(-6.0f, 10.0f, -8.0f, 12, 12, 16);
        this.field_178710_a.func_78784_a(0, 28).func_78789_a(-8.0f, 10.0f, -6.0f, 2, 12, 12);
        this.field_178710_a.func_78784_a(0, 28).func_178769_a(6.0f, 10.0f, -6.0f, 2, 12, 12, true);
        this.field_178710_a.func_78784_a(16, 40).func_78789_a(-6.0f, 8.0f, -6.0f, 12, 2, 12);
        this.field_178710_a.func_78784_a(16, 40).func_78789_a(-6.0f, 22.0f, -6.0f, 12, 2, 12);
        for (int i = 0; i < this.field_178709_c.length; ++i) {
            this.field_178709_c[i] = new ModelRenderer(this, 0, 0);
            this.field_178709_c[i].func_78789_a(-1.0f, -4.5f, -1.0f, 2, 9, 2);
            this.field_178710_a.func_78792_a(this.field_178709_c[i]);
        }
        this.field_178708_b = new ModelRenderer(this, 8, 0);
        this.field_178708_b.func_78789_a(-1.0f, 15.0f, 0.0f, 2, 2, 1);
        this.field_178710_a.func_78792_a(this.field_178708_b);
        this.field_178707_d = new ModelRenderer[3];
        this.field_178707_d[0] = new ModelRenderer(this, 40, 0);
        this.field_178707_d[0].func_78789_a(-2.0f, 14.0f, 7.0f, 4, 4, 8);
        this.field_178707_d[1] = new ModelRenderer(this, 0, 54);
        this.field_178707_d[1].func_78789_a(0.0f, 14.0f, 0.0f, 3, 3, 7);
        this.field_178707_d[2] = new ModelRenderer(this);
        this.field_178707_d[2].func_78784_a(41, 32).func_78789_a(0.0f, 14.0f, 0.0f, 2, 2, 6);
        this.field_178707_d[2].func_78784_a(25, 19).func_78789_a(1.0f, 10.5f, 3.0f, 1, 9, 9);
        this.field_178710_a.func_78792_a(this.field_178707_d[0]);
        this.field_178707_d[0].func_78792_a(this.field_178707_d[1]);
        this.field_178707_d[1].func_78792_a(this.field_178707_d[2]);
    }

    @Override
    public void func_78088_a(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.func_78087_a(f, f2, f3, f4, f5, f6, entity);
        this.field_178710_a.func_78785_a(f6);
    }

    @Override
    public void func_78087_a(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        EntityGuardian entityGuardian = (EntityGuardian)entity;
        float \u26032 = f3 - (float)entityGuardian.field_70173_aa;
        this.field_178710_a.field_78796_g = f4 * ((float)Math.PI / 180);
        this.field_178710_a.field_78795_f = f5 * ((float)Math.PI / 180);
        float[] \u26033 = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
        float[] \u26034 = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
        float[] \u26035 = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
        float[] \u26036 = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
        float[] \u26037 = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
        float[] \u26038 = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
        float \u26039 = (1.0f - entityGuardian.func_175469_o(\u26032)) * 0.55f;
        for (int i = 0; i < 12; ++i) {
            this.field_178709_c[i].field_78795_f = (float)Math.PI * \u26033[i];
            this.field_178709_c[i].field_78796_g = (float)Math.PI * \u26034[i];
            this.field_178709_c[i].field_78808_h = (float)Math.PI * \u26035[i];
            this.field_178709_c[i].field_78800_c = \u26036[i] * (1.0f + MathHelper.func_76134_b(f3 * 1.5f + (float)i) * 0.01f - \u26039);
            this.field_178709_c[i].field_78797_d = 16.0f + \u26037[i] * (1.0f + MathHelper.func_76134_b(f3 * 1.5f + (float)i) * 0.01f - \u26039);
            this.field_178709_c[i].field_78798_e = \u26038[i] * (1.0f + MathHelper.func_76134_b(f3 * 1.5f + (float)i) * 0.01f - \u26039);
        }
        this.field_178708_b.field_78798_e = -8.25f;
        Entity entity2 = Minecraft.func_71410_x().func_175606_aa();
        if (entityGuardian.func_175474_cn()) {
            entity2 = entityGuardian.func_175466_co();
        }
        if (entity2 != null) {
            Vec3d vec3d = entity2.func_174824_e(0.0f);
            \u2603 = entity.func_174824_e(0.0f);
            double \u260310 = vec3d.field_72448_b - \u2603.field_72448_b;
            this.field_178708_b.field_78797_d = \u260310 > 0.0 ? 0.0f : 1.0f;
            \u2603 = entity.func_70676_i(0.0f);
            \u2603 = new Vec3d(\u2603.field_72450_a, 0.0, \u2603.field_72449_c);
            \u2603 = new Vec3d(\u2603.field_72450_a - vec3d.field_72450_a, 0.0, \u2603.field_72449_c - vec3d.field_72449_c).func_72432_b().func_178785_b(1.5707964f);
            double \u260311 = \u2603.func_72430_b(\u2603);
            this.field_178708_b.field_78800_c = MathHelper.func_76129_c((float)Math.abs(\u260311)) * 2.0f * (float)Math.signum(\u260311);
        }
        this.field_178708_b.field_78806_j = true;
        float f7 = entityGuardian.func_175471_a(\u26032);
        this.field_178707_d[0].field_78796_g = MathHelper.func_76126_a(f7) * (float)Math.PI * 0.05f;
        this.field_178707_d[1].field_78796_g = MathHelper.func_76126_a(f7) * (float)Math.PI * 0.1f;
        this.field_178707_d[1].field_78800_c = -1.5f;
        this.field_178707_d[1].field_78797_d = 0.5f;
        this.field_178707_d[1].field_78798_e = 14.0f;
        this.field_178707_d[2].field_78796_g = MathHelper.func_76126_a(f7) * (float)Math.PI * 0.15f;
        this.field_178707_d[2].field_78800_c = 0.5f;
        this.field_178707_d[2].field_78797_d = 0.5f;
        this.field_178707_d[2].field_78798_e = 6.0f;
    }
}

