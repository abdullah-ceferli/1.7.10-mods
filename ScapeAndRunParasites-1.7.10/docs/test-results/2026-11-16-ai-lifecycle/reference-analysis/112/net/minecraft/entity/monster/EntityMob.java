/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.monster;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public abstract class EntityMob
extends EntityCreature
implements IMob {
    public EntityMob(World world) {
        super(world);
        this.field_70728_aV = 5;
    }

    @Override
    public SoundCategory func_184176_by() {
        return SoundCategory.HOSTILE;
    }

    @Override
    public void func_70636_d() {
        this.func_82168_bl();
        float f = this.func_70013_c();
        if (f > 0.5f) {
            this.field_70708_bq += 2;
        }
        super.func_70636_d();
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
            this.func_70106_y();
        }
    }

    @Override
    protected SoundEvent func_184184_Z() {
        return SoundEvents.field_187593_cC;
    }

    @Override
    protected SoundEvent func_184181_aa() {
        return SoundEvents.field_187591_cB;
    }

    @Override
    public boolean func_70097_a(DamageSource damageSource, float f) {
        if (this.func_180431_b(damageSource)) {
            return false;
        }
        return super.func_70097_a(damageSource, f);
    }

    @Override
    protected SoundEvent func_184601_bQ(DamageSource damageSource) {
        return SoundEvents.field_187741_cz;
    }

    @Override
    protected SoundEvent func_184615_bR() {
        return SoundEvents.field_187738_cy;
    }

    @Override
    protected SoundEvent func_184588_d(int n) {
        if (n > 4) {
            return SoundEvents.field_187735_cx;
        }
        return SoundEvents.field_187589_cA;
    }

    @Override
    public boolean func_70652_k(Entity entity2) {
        boolean bl;
        float f = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
        int \u26032 = 0;
        if (entity2 instanceof EntityLivingBase) {
            f += EnchantmentHelper.func_152377_a(this.func_184614_ca(), ((EntityLivingBase)entity2).func_70668_bt());
            \u26032 += EnchantmentHelper.func_77501_a(this);
        }
        if (bl = entity2.func_70097_a(DamageSource.func_76358_a(this), f)) {
            Entity entity2;
            if (\u26032 > 0 && entity2 instanceof EntityLivingBase) {
                ((EntityLivingBase)entity2).func_70653_a(this, (float)\u26032 * 0.5f, MathHelper.func_76126_a(this.field_70177_z * ((float)Math.PI / 180)), -MathHelper.func_76134_b(this.field_70177_z * ((float)Math.PI / 180)));
                this.field_70159_w *= 0.6;
                this.field_70179_y *= 0.6;
            }
            if ((\u2603 = EnchantmentHelper.func_90036_a(this)) > 0) {
                entity2.func_70015_d(\u2603 * 4);
            }
            if (entity2 instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer)entity2;
                ItemStack \u26033 = this.func_184614_ca();
                ItemStack itemStack = \u2603 = entityPlayer.func_184587_cr() ? entityPlayer.func_184607_cu() : ItemStack.field_190927_a;
                if (!\u26033.func_190926_b() && !\u2603.func_190926_b() && \u26033.func_77973_b() instanceof ItemAxe && \u2603.func_77973_b() == Items.field_185159_cQ) {
                    float f2 = 0.25f + (float)EnchantmentHelper.func_185293_e(this) * 0.05f;
                    if (this.field_70146_Z.nextFloat() < f2) {
                        entityPlayer.func_184811_cZ().func_185145_a(Items.field_185159_cQ, 100);
                        this.field_70170_p.func_72960_a(entityPlayer, (byte)30);
                    }
                }
            }
            this.func_174815_a(this, entity2);
        }
        return bl;
    }

    @Override
    public float func_180484_a(BlockPos blockPos) {
        return 0.5f - this.field_70170_p.func_175724_o(blockPos);
    }

    protected boolean func_70814_o() {
        BlockPos blockPos = new BlockPos(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v);
        if (this.field_70170_p.func_175642_b(EnumSkyBlock.SKY, blockPos) > this.field_70146_Z.nextInt(32)) {
            return false;
        }
        int \u26032 = this.field_70170_p.func_175671_l(blockPos);
        if (this.field_70170_p.func_72911_I()) {
            int n = this.field_70170_p.func_175657_ab();
            this.field_70170_p.func_175692_b(10);
            \u26032 = this.field_70170_p.func_175671_l(blockPos);
            this.field_70170_p.func_175692_b(n);
        }
        return \u26032 <= this.field_70146_Z.nextInt(8);
    }

    @Override
    public boolean func_70601_bi() {
        return this.field_70170_p.func_175659_aa() != EnumDifficulty.PEACEFUL && this.func_70814_o() && super.func_70601_bi();
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e);
    }

    @Override
    protected boolean func_146066_aG() {
        return true;
    }

    public boolean func_191990_c(EntityPlayer entityPlayer) {
        return true;
    }
}

