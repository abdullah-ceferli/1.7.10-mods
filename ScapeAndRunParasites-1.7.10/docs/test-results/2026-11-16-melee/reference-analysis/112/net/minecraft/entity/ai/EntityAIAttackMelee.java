/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIAttackMelee
extends EntityAIBase {
    World field_75443_a;
    protected EntityCreature field_75441_b;
    protected int field_75439_d;
    double field_75440_e;
    boolean field_75437_f;
    Path field_75438_g;
    private int field_75445_i;
    private double field_151497_i;
    private double field_151495_j;
    private double field_151496_k;
    protected final int field_188493_g = 20;

    public EntityAIAttackMelee(EntityCreature entityCreature, double d, boolean bl) {
        this.field_75441_b = entityCreature;
        this.field_75443_a = entityCreature.field_70170_p;
        this.field_75440_e = d;
        this.field_75437_f = bl;
        this.func_75248_a(3);
    }

    @Override
    public boolean func_75250_a() {
        EntityLivingBase entityLivingBase = this.field_75441_b.func_70638_az();
        if (entityLivingBase == null) {
            return false;
        }
        if (!entityLivingBase.func_70089_S()) {
            return false;
        }
        this.field_75438_g = this.field_75441_b.func_70661_as().func_75494_a(entityLivingBase);
        if (this.field_75438_g != null) {
            return true;
        }
        return this.func_179512_a(entityLivingBase) >= this.field_75441_b.func_70092_e(entityLivingBase.field_70165_t, entityLivingBase.func_174813_aQ().field_72338_b, entityLivingBase.field_70161_v);
    }

    @Override
    public boolean func_75253_b() {
        EntityLivingBase entityLivingBase = this.field_75441_b.func_70638_az();
        if (entityLivingBase == null) {
            return false;
        }
        if (!entityLivingBase.func_70089_S()) {
            return false;
        }
        if (!this.field_75437_f) {
            return !this.field_75441_b.func_70661_as().func_75500_f();
        }
        if (!this.field_75441_b.func_180485_d(new BlockPos(entityLivingBase))) {
            return false;
        }
        return !(entityLivingBase instanceof EntityPlayer) || !((EntityPlayer)entityLivingBase).func_175149_v() && !((EntityPlayer)entityLivingBase).func_184812_l_();
    }

    @Override
    public void func_75249_e() {
        this.field_75441_b.func_70661_as().func_75484_a(this.field_75438_g, this.field_75440_e);
        this.field_75445_i = 0;
    }

    @Override
    public void func_75251_c() {
        EntityLivingBase entityLivingBase = this.field_75441_b.func_70638_az();
        if (entityLivingBase instanceof EntityPlayer && (((EntityPlayer)entityLivingBase).func_175149_v() || ((EntityPlayer)entityLivingBase).func_184812_l_())) {
            this.field_75441_b.func_70624_b(null);
        }
        this.field_75441_b.func_70661_as().func_75499_g();
    }

    @Override
    public void func_75246_d() {
        EntityLivingBase entityLivingBase = this.field_75441_b.func_70638_az();
        this.field_75441_b.func_70671_ap().func_75651_a(entityLivingBase, 30.0f, 30.0f);
        double \u26032 = this.field_75441_b.func_70092_e(entityLivingBase.field_70165_t, entityLivingBase.func_174813_aQ().field_72338_b, entityLivingBase.field_70161_v);
        --this.field_75445_i;
        if ((this.field_75437_f || this.field_75441_b.func_70635_at().func_75522_a(entityLivingBase)) && this.field_75445_i <= 0 && (this.field_151497_i == 0.0 && this.field_151495_j == 0.0 && this.field_151496_k == 0.0 || entityLivingBase.func_70092_e(this.field_151497_i, this.field_151495_j, this.field_151496_k) >= 1.0 || this.field_75441_b.func_70681_au().nextFloat() < 0.05f)) {
            this.field_151497_i = entityLivingBase.field_70165_t;
            this.field_151495_j = entityLivingBase.func_174813_aQ().field_72338_b;
            this.field_151496_k = entityLivingBase.field_70161_v;
            this.field_75445_i = 4 + this.field_75441_b.func_70681_au().nextInt(7);
            if (\u26032 > 1024.0) {
                this.field_75445_i += 10;
            } else if (\u26032 > 256.0) {
                this.field_75445_i += 5;
            }
            if (!this.field_75441_b.func_70661_as().func_75497_a(entityLivingBase, this.field_75440_e)) {
                this.field_75445_i += 15;
            }
        }
        this.field_75439_d = Math.max(this.field_75439_d - 1, 0);
        this.func_190102_a(entityLivingBase, \u26032);
    }

    protected void func_190102_a(EntityLivingBase entityLivingBase, double d) {
        \u2603 = this.func_179512_a(entityLivingBase);
        if (d <= \u2603 && this.field_75439_d <= 0) {
            this.field_75439_d = 20;
            this.field_75441_b.func_184609_a(EnumHand.MAIN_HAND);
            this.field_75441_b.func_70652_k(entityLivingBase);
        }
    }

    protected double func_179512_a(EntityLivingBase entityLivingBase) {
        return this.field_75441_b.field_70130_N * 2.0f * (this.field_75441_b.field_70130_N * 2.0f) + entityLivingBase.field_70130_N;
    }
}

