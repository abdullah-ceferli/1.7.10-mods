/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class EntityAITarget
extends EntityAIBase {
    protected final EntityCreature field_75299_d;
    protected boolean field_75297_f;
    private final boolean field_75303_a;
    private int field_75301_b;
    private int field_75302_c;
    private int field_75298_g;
    protected EntityLivingBase field_188509_g;
    protected int field_188510_h = 60;

    public EntityAITarget(EntityCreature entityCreature, boolean bl) {
        this(entityCreature, bl, false);
    }

    public EntityAITarget(EntityCreature entityCreature, boolean bl, boolean bl2) {
        this.field_75299_d = entityCreature;
        this.field_75297_f = bl;
        this.field_75303_a = bl2;
    }

    @Override
    public boolean func_75253_b() {
        EntityLivingBase entityLivingBase = this.field_75299_d.func_70638_az();
        if (entityLivingBase == null) {
            entityLivingBase = this.field_188509_g;
        }
        if (entityLivingBase == null) {
            return false;
        }
        if (!entityLivingBase.func_70089_S()) {
            return false;
        }
        Team \u26032 = this.field_75299_d.func_96124_cp();
        Team \u26033 = entityLivingBase.func_96124_cp();
        if (\u26032 != null && \u26033 == \u26032) {
            return false;
        }
        double \u26034 = this.func_111175_f();
        if (this.field_75299_d.func_70068_e(entityLivingBase) > \u26034 * \u26034) {
            return false;
        }
        if (this.field_75297_f) {
            if (this.field_75299_d.func_70635_at().func_75522_a(entityLivingBase)) {
                this.field_75298_g = 0;
            } else if (++this.field_75298_g > this.field_188510_h) {
                return false;
            }
        }
        if (entityLivingBase instanceof EntityPlayer && ((EntityPlayer)entityLivingBase).field_71075_bZ.field_75102_a) {
            return false;
        }
        this.field_75299_d.func_70624_b(entityLivingBase);
        return true;
    }

    protected double func_111175_f() {
        IAttributeInstance iAttributeInstance = this.field_75299_d.func_110148_a(SharedMonsterAttributes.field_111265_b);
        return iAttributeInstance == null ? 16.0 : iAttributeInstance.func_111126_e();
    }

    @Override
    public void func_75249_e() {
        this.field_75301_b = 0;
        this.field_75302_c = 0;
        this.field_75298_g = 0;
    }

    @Override
    public void func_75251_c() {
        this.field_75299_d.func_70624_b(null);
        this.field_188509_g = null;
    }

    public static boolean func_179445_a(EntityLiving entityLiving, @Nullable EntityLivingBase entityLivingBase, boolean bl, boolean bl2) {
        if (entityLivingBase == null) {
            return false;
        }
        if (entityLivingBase == entityLiving) {
            return false;
        }
        if (!entityLivingBase.func_70089_S()) {
            return false;
        }
        if (!entityLiving.func_70686_a(entityLivingBase.getClass())) {
            return false;
        }
        if (entityLiving.func_184191_r(entityLivingBase)) {
            return false;
        }
        if (entityLiving instanceof IEntityOwnable && ((IEntityOwnable)((Object)entityLiving)).func_184753_b() != null) {
            if (entityLivingBase instanceof IEntityOwnable && ((IEntityOwnable)((Object)entityLiving)).func_184753_b().equals(((IEntityOwnable)((Object)entityLivingBase)).func_184753_b())) {
                return false;
            }
            if (entityLivingBase == ((IEntityOwnable)((Object)entityLiving)).func_70902_q()) {
                return false;
            }
        } else if (entityLivingBase instanceof EntityPlayer && !bl && ((EntityPlayer)entityLivingBase).field_71075_bZ.field_75102_a) {
            return false;
        }
        return !bl2 || entityLiving.func_70635_at().func_75522_a(entityLivingBase);
    }

    protected boolean func_75296_a(@Nullable EntityLivingBase entityLivingBase, boolean bl) {
        if (!EntityAITarget.func_179445_a(this.field_75299_d, entityLivingBase, bl, this.field_75297_f)) {
            return false;
        }
        if (!this.field_75299_d.func_180485_d(new BlockPos(entityLivingBase))) {
            return false;
        }
        if (this.field_75303_a) {
            if (--this.field_75302_c <= 0) {
                this.field_75301_b = 0;
            }
            if (this.field_75301_b == 0) {
                int n = this.field_75301_b = this.func_75295_a(entityLivingBase) ? 1 : 2;
            }
            if (this.field_75301_b == 2) {
                return false;
            }
        }
        return true;
    }

    private boolean func_75295_a(EntityLivingBase entityLivingBase) {
        this.field_75302_c = 10 + this.field_75299_d.func_70681_au().nextInt(5);
        Path path = this.field_75299_d.func_70661_as().func_75494_a(entityLivingBase);
        if (path == null) {
            return false;
        }
        PathPoint \u26032 = path.func_75870_c();
        if (\u26032 == null) {
            return false;
        }
        int \u26033 = \u26032.field_75839_a - MathHelper.func_76128_c(entityLivingBase.field_70165_t);
        return (double)(\u26033 * \u26033 + (\u2603 = \u26032.field_75838_c - MathHelper.func_76128_c(entityLivingBase.field_70161_v)) * \u2603) <= 2.25;
    }

    public EntityAITarget func_190882_b(int n) {
        this.field_188510_h = n;
        return this;
    }
}

