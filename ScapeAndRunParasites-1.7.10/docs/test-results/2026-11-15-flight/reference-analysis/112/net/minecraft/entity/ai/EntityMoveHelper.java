/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.MathHelper;

public class EntityMoveHelper {
    protected final EntityLiving field_75648_a;
    protected double field_75646_b;
    protected double field_75647_c;
    protected double field_75644_d;
    protected double field_75645_e;
    protected float field_188489_f;
    protected float field_188490_g;
    public Action field_188491_h = Action.WAIT;

    public EntityMoveHelper(EntityLiving entityLiving) {
        this.field_75648_a = entityLiving;
    }

    public boolean func_75640_a() {
        return this.field_188491_h == Action.MOVE_TO;
    }

    public double func_75638_b() {
        return this.field_75645_e;
    }

    public void func_75642_a(double d, double d2, double d3, double d4) {
        this.field_75646_b = d;
        this.field_75647_c = d2;
        this.field_75644_d = d3;
        this.field_75645_e = d4;
        this.field_188491_h = Action.MOVE_TO;
    }

    public void func_188488_a(float f, float f2) {
        this.field_188491_h = Action.STRAFE;
        this.field_188489_f = f;
        this.field_188490_g = f2;
        this.field_75645_e = 0.25;
    }

    public void func_188487_a(EntityMoveHelper entityMoveHelper) {
        this.field_188491_h = entityMoveHelper.field_188491_h;
        this.field_75646_b = entityMoveHelper.field_75646_b;
        this.field_75647_c = entityMoveHelper.field_75647_c;
        this.field_75644_d = entityMoveHelper.field_75644_d;
        this.field_75645_e = Math.max(entityMoveHelper.field_75645_e, 1.0);
        this.field_188489_f = entityMoveHelper.field_188489_f;
        this.field_188490_g = entityMoveHelper.field_188490_g;
    }

    public void func_75641_c() {
        if (this.field_188491_h == Action.STRAFE) {
            float f = (float)this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e();
            \u2603 = (float)this.field_75645_e * f;
            \u2603 = this.field_188489_f;
            \u2603 = this.field_188490_g;
            \u2603 = MathHelper.func_76129_c(\u2603 * \u2603 + \u2603 * \u2603);
            if (\u2603 < 1.0f) {
                \u2603 = 1.0f;
            }
            \u2603 = \u2603 / \u2603;
            \u2603 = MathHelper.func_76126_a(this.field_75648_a.field_70177_z * ((float)Math.PI / 180));
            \u2603 = MathHelper.func_76134_b(this.field_75648_a.field_70177_z * ((float)Math.PI / 180));
            \u2603 = (\u2603 *= \u2603) * \u2603 - (\u2603 *= \u2603) * \u2603;
            \u2603 = \u2603 * \u2603 + \u2603 * \u2603;
            PathNavigate \u26032 = this.field_75648_a.func_70661_as();
            if (\u26032 != null && (\u2603 = \u26032.func_189566_q()) != null && \u2603.func_186330_a(this.field_75648_a.field_70170_p, MathHelper.func_76128_c(this.field_75648_a.field_70165_t + (double)\u2603), MathHelper.func_76128_c(this.field_75648_a.field_70163_u), MathHelper.func_76128_c(this.field_75648_a.field_70161_v + (double)\u2603)) != PathNodeType.WALKABLE) {
                this.field_188489_f = 1.0f;
                this.field_188490_g = 0.0f;
                \u2603 = f;
            }
            this.field_75648_a.func_70659_e(\u2603);
            this.field_75648_a.func_191989_p(this.field_188489_f);
            this.field_75648_a.func_184646_p(this.field_188490_g);
            this.field_188491_h = Action.WAIT;
        } else if (this.field_188491_h == Action.MOVE_TO) {
            this.field_188491_h = Action.WAIT;
            double \u26033 = this.field_75646_b - this.field_75648_a.field_70165_t;
            double \u26034 = this.field_75647_c - this.field_75648_a.field_70163_u;
            double \u26035 = this.field_75644_d - this.field_75648_a.field_70161_v;
            double \u26036 = \u26033 * \u26033 + \u26034 * \u26034 + \u26035 * \u26035;
            if (\u26036 < 2.500000277905201E-7) {
                this.field_75648_a.func_191989_p(0.0f);
                return;
            }
            \u2603 = (float)(MathHelper.func_181159_b(\u26035, \u26033) * 57.2957763671875) - 90.0f;
            this.field_75648_a.field_70177_z = this.func_75639_a(this.field_75648_a.field_70177_z, \u2603, 90.0f);
            this.field_75648_a.func_70659_e((float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e()));
            if (\u26034 > (double)this.field_75648_a.field_70138_W && \u26033 * \u26033 + \u26035 * \u26035 < (double)Math.max(1.0f, this.field_75648_a.field_70130_N)) {
                this.field_75648_a.func_70683_ar().func_75660_a();
                this.field_188491_h = Action.JUMPING;
            }
        } else if (this.field_188491_h == Action.JUMPING) {
            this.field_75648_a.func_70659_e((float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e()));
            if (this.field_75648_a.field_70122_E) {
                this.field_188491_h = Action.WAIT;
            }
        } else {
            this.field_75648_a.func_191989_p(0.0f);
        }
    }

    protected float func_75639_a(float f, float f2, float f3) {
        \u2603 = MathHelper.func_76142_g(f2 - f);
        if (\u2603 > f3) {
            \u2603 = f3;
        }
        if (\u2603 < -f3) {
            \u2603 = -f3;
        }
        if ((\u2603 = f + \u2603) < 0.0f) {
            \u2603 += 360.0f;
        } else if (\u2603 > 360.0f) {
            \u2603 -= 360.0f;
        }
        return \u2603;
    }

    public double func_179917_d() {
        return this.field_75646_b;
    }

    public double func_179919_e() {
        return this.field_75647_c;
    }

    public double func_179918_f() {
        return this.field_75644_d;
    }

    public static enum Action {
        WAIT,
        MOVE_TO,
        STRAFE,
        JUMPING;

    }
}

