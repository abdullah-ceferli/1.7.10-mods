/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityXPOrb
extends Entity {
    public int field_70533_a;
    public int field_70531_b;
    public int field_70532_c;
    private int field_70529_d = 5;
    private int field_70530_e;
    private EntityPlayer field_80001_f;
    private int field_80002_g;

    public EntityXPOrb(World world, double d, double d2, double d3, int n) {
        super(world);
        this.func_70105_a(0.5f, 0.5f);
        this.func_70107_b(d, d2, d3);
        this.field_70177_z = (float)(Math.random() * 360.0);
        this.field_70159_w = (float)(Math.random() * (double)0.2f - (double)0.1f) * 2.0f;
        this.field_70181_x = (float)(Math.random() * 0.2) * 2.0f;
        this.field_70179_y = (float)(Math.random() * (double)0.2f - (double)0.1f) * 2.0f;
        this.field_70530_e = n;
    }

    @Override
    protected boolean func_70041_e_() {
        return false;
    }

    public EntityXPOrb(World world) {
        super(world);
        this.func_70105_a(0.25f, 0.25f);
    }

    @Override
    protected void func_70088_a() {
    }

    @Override
    public int func_70070_b() {
        float f = 0.5f;
        f = MathHelper.func_76131_a(f, 0.0f, 1.0f);
        int \u26032 = super.func_70070_b();
        int \u26033 = \u26032 & 0xFF;
        int \u26034 = \u26032 >> 16 & 0xFF;
        if ((\u26033 += (int)(f * 15.0f * 16.0f)) > 240) {
            \u26033 = 240;
        }
        return \u26033 | \u26034 << 16;
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.field_70532_c > 0) {
            --this.field_70532_c;
        }
        this.field_70169_q = this.field_70165_t;
        this.field_70167_r = this.field_70163_u;
        this.field_70166_s = this.field_70161_v;
        if (!this.func_189652_ae()) {
            this.field_70181_x -= (double)0.03f;
        }
        if (this.field_70170_p.func_180495_p(new BlockPos(this)).func_185904_a() == Material.field_151587_i) {
            this.field_70181_x = 0.2f;
            this.field_70159_w = (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f;
            this.field_70179_y = (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f;
            this.func_184185_a(SoundEvents.field_187658_bx, 0.4f, 2.0f + this.field_70146_Z.nextFloat() * 0.4f);
        }
        this.func_145771_j(this.field_70165_t, (this.func_174813_aQ().field_72338_b + this.func_174813_aQ().field_72337_e) / 2.0, this.field_70161_v);
        double d = 8.0;
        if (this.field_80002_g < this.field_70533_a - 20 + this.func_145782_y() % 100) {
            if (this.field_80001_f == null || this.field_80001_f.func_70068_e(this) > 64.0) {
                this.field_80001_f = this.field_70170_p.func_72890_a(this, 8.0);
            }
            this.field_80002_g = this.field_70533_a;
        }
        if (this.field_80001_f != null && this.field_80001_f.func_175149_v()) {
            this.field_80001_f = null;
        }
        if (this.field_80001_f != null && (\u2603 = 1.0 - (\u2603 = Math.sqrt((\u2603 = (this.field_80001_f.field_70165_t - this.field_70165_t) / 8.0) * \u2603 + (\u2603 = (this.field_80001_f.field_70163_u + (double)this.field_80001_f.func_70047_e() / 2.0 - this.field_70163_u) / 8.0) * \u2603 + (\u2603 = (this.field_80001_f.field_70161_v - this.field_70161_v) / 8.0) * \u2603))) > 0.0) {
            \u2603 *= \u2603;
            this.field_70159_w += \u2603 / \u2603 * \u2603 * 0.1;
            this.field_70181_x += \u2603 / \u2603 * \u2603 * 0.1;
            this.field_70179_y += \u2603 / \u2603 * \u2603 * 0.1;
        }
        this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
        float \u26032 = 0.98f;
        if (this.field_70122_E) {
            \u26032 = this.field_70170_p.func_180495_p((BlockPos)new BlockPos((int)MathHelper.func_76128_c((double)this.field_70165_t), (int)(MathHelper.func_76128_c((double)this.func_174813_aQ().field_72338_b) - 1), (int)MathHelper.func_76128_c((double)this.field_70161_v))).func_177230_c().field_149765_K * 0.98f;
        }
        this.field_70159_w *= (double)\u26032;
        this.field_70181_x *= (double)0.98f;
        this.field_70179_y *= (double)\u26032;
        if (this.field_70122_E) {
            this.field_70181_x *= (double)-0.9f;
        }
        ++this.field_70533_a;
        ++this.field_70531_b;
        if (this.field_70531_b >= 6000) {
            this.func_70106_y();
        }
    }

    @Override
    public boolean func_70072_I() {
        return this.field_70170_p.func_72918_a(this.func_174813_aQ(), Material.field_151586_h, this);
    }

    @Override
    protected void func_70081_e(int n) {
        this.func_70097_a(DamageSource.field_76372_a, n);
    }

    @Override
    public boolean func_70097_a(DamageSource damageSource, float f) {
        if (this.func_180431_b(damageSource)) {
            return false;
        }
        this.func_70018_K();
        this.field_70529_d = (int)((float)this.field_70529_d - f);
        if (this.field_70529_d <= 0) {
            this.func_70106_y();
        }
        return false;
    }

    @Override
    public void func_70014_b(NBTTagCompound nBTTagCompound) {
        nBTTagCompound.func_74777_a("Health", (short)this.field_70529_d);
        nBTTagCompound.func_74777_a("Age", (short)this.field_70531_b);
        nBTTagCompound.func_74777_a("Value", (short)this.field_70530_e);
    }

    @Override
    public void func_70037_a(NBTTagCompound nBTTagCompound) {
        this.field_70529_d = nBTTagCompound.func_74765_d("Health");
        this.field_70531_b = nBTTagCompound.func_74765_d("Age");
        this.field_70530_e = nBTTagCompound.func_74765_d("Value");
    }

    @Override
    public void func_70100_b_(EntityPlayer entityPlayer2) {
        if (this.field_70170_p.field_72995_K) {
            return;
        }
        if (this.field_70532_c == 0 && entityPlayer2.field_71090_bL == 0) {
            entityPlayer2.field_71090_bL = 2;
            entityPlayer2.func_71001_a(this, 1);
            ItemStack itemStack = EnchantmentHelper.func_92099_a(Enchantments.field_185296_A, entityPlayer2);
            if (!itemStack.func_190926_b() && itemStack.func_77951_h()) {
                int n = Math.min(this.func_184514_c(this.field_70530_e), itemStack.func_77952_i());
                this.field_70530_e -= this.func_184515_b(n);
                itemStack.func_77964_b(itemStack.func_77952_i() - n);
            }
            if (this.field_70530_e > 0) {
                EntityPlayer entityPlayer2;
                entityPlayer2.func_71023_q(this.field_70530_e);
            }
            this.func_70106_y();
        }
    }

    private int func_184515_b(int n) {
        return n / 2;
    }

    private int func_184514_c(int n) {
        return n * 2;
    }

    public int func_70526_d() {
        return this.field_70530_e;
    }

    public int func_70528_g() {
        if (this.field_70530_e >= 2477) {
            return 10;
        }
        if (this.field_70530_e >= 1237) {
            return 9;
        }
        if (this.field_70530_e >= 617) {
            return 8;
        }
        if (this.field_70530_e >= 307) {
            return 7;
        }
        if (this.field_70530_e >= 149) {
            return 6;
        }
        if (this.field_70530_e >= 73) {
            return 5;
        }
        if (this.field_70530_e >= 37) {
            return 4;
        }
        if (this.field_70530_e >= 17) {
            return 3;
        }
        if (this.field_70530_e >= 7) {
            return 2;
        }
        if (this.field_70530_e >= 3) {
            return 1;
        }
        return 0;
    }

    public static int func_70527_a(int n) {
        if (n >= 2477) {
            return 2477;
        }
        if (n >= 1237) {
            return 1237;
        }
        if (n >= 617) {
            return 617;
        }
        if (n >= 307) {
            return 307;
        }
        if (n >= 149) {
            return 149;
        }
        if (n >= 73) {
            return 73;
        }
        if (n >= 37) {
            return 37;
        }
        if (n >= 17) {
            return 17;
        }
        if (n >= 7) {
            return 7;
        }
        if (n >= 3) {
            return 3;
        }
        return 1;
    }

    @Override
    public boolean func_70075_an() {
        return false;
    }
}

