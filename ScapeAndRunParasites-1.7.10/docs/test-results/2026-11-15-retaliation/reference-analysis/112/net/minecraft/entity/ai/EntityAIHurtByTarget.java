/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIHurtByTarget
extends EntityAITarget {
    private final boolean field_75312_a;
    private int field_142052_b;
    private final Class<?>[] field_179447_c;

    public EntityAIHurtByTarget(EntityCreature entityCreature, boolean bl, Class<?> ... classArray) {
        super(entityCreature, true);
        this.field_75312_a = bl;
        this.field_179447_c = classArray;
        this.func_75248_a(1);
    }

    @Override
    public boolean func_75250_a() {
        int n = this.field_75299_d.func_142015_aE();
        EntityLivingBase \u26032 = this.field_75299_d.func_70643_av();
        return n != this.field_142052_b && \u26032 != null && this.func_75296_a(\u26032, false);
    }

    @Override
    public void func_75249_e() {
        this.field_75299_d.func_70624_b(this.field_75299_d.func_70643_av());
        this.field_188509_g = this.field_75299_d.func_70638_az();
        this.field_142052_b = this.field_75299_d.func_142015_aE();
        this.field_188510_h = 300;
        if (this.field_75312_a) {
            this.func_190105_f();
        }
        super.func_75249_e();
    }

    protected void func_190105_f() {
        double d = this.func_111175_f();
        List<?> \u26032 = this.field_75299_d.field_70170_p.func_72872_a(this.field_75299_d.getClass(), new AxisAlignedBB(this.field_75299_d.field_70165_t, this.field_75299_d.field_70163_u, this.field_75299_d.field_70161_v, this.field_75299_d.field_70165_t + 1.0, this.field_75299_d.field_70163_u + 1.0, this.field_75299_d.field_70161_v + 1.0).func_72314_b(d, 10.0, d));
        for (EntityCreature entityCreature : \u26032) {
            if (this.field_75299_d == entityCreature || entityCreature.func_70638_az() != null || this.field_75299_d instanceof EntityTameable && ((EntityTameable)this.field_75299_d).func_70902_q() != ((EntityTameable)entityCreature).func_70902_q() || entityCreature.func_184191_r(this.field_75299_d.func_70643_av())) continue;
            boolean bl = false;
            for (Class<?> clazz : this.field_179447_c) {
                if (entityCreature.getClass() != clazz) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            this.func_179446_a(entityCreature, this.field_75299_d.func_70643_av());
        }
    }

    protected void func_179446_a(EntityCreature entityCreature, EntityLivingBase entityLivingBase) {
        entityCreature.func_70624_b(entityLivingBase);
    }
}

