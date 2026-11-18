/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityGuardian
extends EntityMob {
    private static final DataParameter<Boolean> field_190766_bz = EntityDataManager.func_187226_a(EntityGuardian.class, DataSerializers.field_187198_h);
    private static final DataParameter<Integer> field_184723_b = EntityDataManager.func_187226_a(EntityGuardian.class, DataSerializers.field_187192_b);
    protected float field_175482_b;
    protected float field_175484_c;
    protected float field_175483_bk;
    protected float field_175485_bl;
    protected float field_175486_bm;
    private EntityLivingBase field_175478_bn;
    private int field_175479_bo;
    private boolean field_175480_bp;
    protected EntityAIWander field_175481_bq;

    public EntityGuardian(World world) {
        super(world);
        this.field_70728_aV = 10;
        this.func_70105_a(0.85f, 0.85f);
        this.field_70765_h = new GuardianMoveHelper(this);
        this.field_175484_c = this.field_175482_b = this.field_70146_Z.nextFloat();
    }

    @Override
    protected void func_184651_r() {
        EntityAIMoveTowardsRestriction entityAIMoveTowardsRestriction = new EntityAIMoveTowardsRestriction(this, 1.0);
        this.field_175481_bq = new EntityAIWander(this, 1.0, 80);
        this.field_70714_bg.func_75776_a(4, new AIGuardianAttack(this));
        this.field_70714_bg.func_75776_a(5, entityAIMoveTowardsRestriction);
        this.field_70714_bg.func_75776_a(7, this.field_175481_bq);
        this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityGuardian.class, 12.0f, 0.01f));
        this.field_70714_bg.func_75776_a(9, new EntityAILookIdle(this));
        this.field_175481_bq.func_75248_a(3);
        entityAIMoveTowardsRestriction.func_75248_a(3);
        this.field_70715_bh.func_75776_a(1, new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class, 10, true, false, new GuardianTargetSelector(this)));
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(6.0);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5);
        this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(16.0);
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(30.0);
    }

    public static void func_189766_b(DataFixer dataFixer) {
        EntityLiving.func_189752_a(dataFixer, EntityGuardian.class);
    }

    @Override
    protected PathNavigate func_175447_b(World world) {
        return new PathNavigateSwimmer(this, world);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_187214_a(field_190766_bz, false);
        this.field_70180_af.func_187214_a(field_184723_b, 0);
    }

    public boolean func_175472_n() {
        return this.field_70180_af.func_187225_a(field_190766_bz);
    }

    private void func_175476_l(boolean bl) {
        this.field_70180_af.func_187227_b(field_190766_bz, bl);
    }

    public int func_175464_ck() {
        return 80;
    }

    private void func_175463_b(int n) {
        this.field_70180_af.func_187227_b(field_184723_b, n);
    }

    public boolean func_175474_cn() {
        return this.field_70180_af.func_187225_a(field_184723_b) != 0;
    }

    @Nullable
    public EntityLivingBase func_175466_co() {
        if (!this.func_175474_cn()) {
            return null;
        }
        if (this.field_70170_p.field_72995_K) {
            if (this.field_175478_bn != null) {
                return this.field_175478_bn;
            }
            Entity entity = this.field_70170_p.func_73045_a(this.field_70180_af.func_187225_a(field_184723_b));
            if (entity instanceof EntityLivingBase) {
                this.field_175478_bn = (EntityLivingBase)entity;
                return this.field_175478_bn;
            }
            return null;
        }
        return this.func_70638_az();
    }

    @Override
    public void func_184206_a(DataParameter<?> dataParameter) {
        super.func_184206_a(dataParameter);
        if (field_184723_b.equals(dataParameter)) {
            this.field_175479_bo = 0;
            this.field_175478_bn = null;
        }
    }

    @Override
    public int func_70627_aG() {
        return 160;
    }

    @Override
    protected SoundEvent func_184639_G() {
        return this.func_70090_H() ? SoundEvents.field_187670_cb : SoundEvents.field_187672_cc;
    }

    @Override
    protected SoundEvent func_184601_bQ(DamageSource damageSource) {
        return this.func_70090_H() ? SoundEvents.field_187687_ch : SoundEvents.field_187690_ci;
    }

    @Override
    protected SoundEvent func_184615_bR() {
        return this.func_70090_H() ? SoundEvents.field_187678_ce : SoundEvents.field_187681_cf;
    }

    @Override
    protected boolean func_70041_e_() {
        return false;
    }

    @Override
    public float func_70047_e() {
        return this.field_70131_O * 0.5f;
    }

    @Override
    public float func_180484_a(BlockPos blockPos) {
        if (this.field_70170_p.func_180495_p(blockPos).func_185904_a() == Material.field_151586_h) {
            return 10.0f + this.field_70170_p.func_175724_o(blockPos) - 0.5f;
        }
        return super.func_180484_a(blockPos);
    }

    @Override
    public void func_70636_d() {
        if (this.field_70170_p.field_72995_K) {
            Object object;
            this.field_175484_c = this.field_175482_b;
            if (!this.func_70090_H()) {
                this.field_175483_bk = 2.0f;
                if (this.field_70181_x > 0.0 && this.field_175480_bp && !this.func_174814_R()) {
                    this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.func_190765_dj(), this.func_184176_by(), 1.0f, 1.0f, false);
                }
                this.field_175480_bp = this.field_70181_x < 0.0 && this.field_70170_p.func_175677_d(new BlockPos(this).func_177977_b(), false);
            } else {
                this.field_175483_bk = this.func_175472_n() ? (this.field_175483_bk < 0.5f ? 4.0f : (this.field_175483_bk += (0.5f - this.field_175483_bk) * 0.1f)) : (this.field_175483_bk += (0.125f - this.field_175483_bk) * 0.2f);
            }
            this.field_175482_b += this.field_175483_bk;
            this.field_175486_bm = this.field_175485_bl;
            this.field_175485_bl = !this.func_70090_H() ? this.field_70146_Z.nextFloat() : (this.func_175472_n() ? (this.field_175485_bl += (0.0f - this.field_175485_bl) * 0.25f) : (this.field_175485_bl += (1.0f - this.field_175485_bl) * 0.06f));
            if (this.func_175472_n() && this.func_70090_H()) {
                object = this.func_70676_i(0.0f);
                for (int i = 0; i < 2; ++i) {
                    this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_BUBBLE, this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5) * (double)this.field_70130_N - ((Vec3d)object).field_72450_a * 1.5, this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O - ((Vec3d)object).field_72448_b * 1.5, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5) * (double)this.field_70130_N - ((Vec3d)object).field_72449_c * 1.5, 0.0, 0.0, 0.0, new int[0]);
                }
            }
            if (this.func_175474_cn()) {
                if (this.field_175479_bo < this.func_175464_ck()) {
                    ++this.field_175479_bo;
                }
                if ((object = this.func_175466_co()) != null) {
                    this.func_70671_ap().func_75651_a((Entity)object, 90.0f, 90.0f);
                    this.func_70671_ap().func_75649_a();
                    double d = this.func_175477_p(0.0f);
                    \u2603 = ((EntityLivingBase)object).field_70165_t - this.field_70165_t;
                    \u2603 = ((EntityLivingBase)object).field_70163_u + (double)(((EntityLivingBase)object).field_70131_O * 0.5f) - (this.field_70163_u + (double)this.func_70047_e());
                    \u2603 = ((EntityLivingBase)object).field_70161_v - this.field_70161_v;
                    \u2603 = Math.sqrt(\u2603 * \u2603 + \u2603 * \u2603 + \u2603 * \u2603);
                    \u2603 /= \u2603;
                    \u2603 /= \u2603;
                    \u2603 /= \u2603;
                    \u2603 = this.field_70146_Z.nextDouble();
                    while (\u2603 < \u2603) {
                        this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_BUBBLE, this.field_70165_t + \u2603 * (\u2603 += 1.8 - d + this.field_70146_Z.nextDouble() * (1.7 - d)), this.field_70163_u + \u2603 * \u2603 + (double)this.func_70047_e(), this.field_70161_v + \u2603 * \u2603, 0.0, 0.0, 0.0, new int[0]);
                    }
                }
            }
        }
        if (this.field_70171_ac) {
            this.func_70050_g(300);
        } else if (this.field_70122_E) {
            this.field_70181_x += 0.5;
            this.field_70159_w += (double)((this.field_70146_Z.nextFloat() * 2.0f - 1.0f) * 0.4f);
            this.field_70179_y += (double)((this.field_70146_Z.nextFloat() * 2.0f - 1.0f) * 0.4f);
            this.field_70177_z = this.field_70146_Z.nextFloat() * 360.0f;
            this.field_70122_E = false;
            this.field_70160_al = true;
        }
        if (this.func_175474_cn()) {
            this.field_70177_z = this.field_70759_as;
        }
        super.func_70636_d();
    }

    protected SoundEvent func_190765_dj() {
        return SoundEvents.field_187684_cg;
    }

    public float func_175471_a(float f) {
        return this.field_175484_c + (this.field_175482_b - this.field_175484_c) * f;
    }

    public float func_175469_o(float f) {
        return this.field_175486_bm + (this.field_175485_bl - this.field_175486_bm) * f;
    }

    public float func_175477_p(float f) {
        return ((float)this.field_175479_bo + f) / (float)this.func_175464_ck();
    }

    @Override
    @Nullable
    protected ResourceLocation func_184647_J() {
        return LootTableList.field_186440_v;
    }

    @Override
    protected boolean func_70814_o() {
        return true;
    }

    @Override
    public boolean func_70058_J() {
        return this.field_70170_p.func_72917_a(this.func_174813_aQ(), this) && this.field_70170_p.func_184144_a(this, this.func_174813_aQ()).isEmpty();
    }

    @Override
    public boolean func_70601_bi() {
        return (this.field_70146_Z.nextInt(20) == 0 || !this.field_70170_p.func_175710_j(new BlockPos(this))) && super.func_70601_bi();
    }

    @Override
    public boolean func_70097_a(DamageSource damageSource2, float f) {
        DamageSource damageSource2;
        if (!this.func_175472_n() && !damageSource2.func_82725_o() && damageSource2.func_76364_f() instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)damageSource2.func_76364_f();
            if (!damageSource2.func_94541_c()) {
                entityLivingBase.func_70097_a(DamageSource.func_92087_a(this), 2.0f);
            }
        }
        if (this.field_175481_bq != null) {
            this.field_175481_bq.func_179480_f();
        }
        return super.func_70097_a(damageSource2, f);
    }

    @Override
    public int func_70646_bf() {
        return 180;
    }

    @Override
    public void func_191986_a(float f, float f2, float f3) {
        if (this.func_70613_aW() && this.func_70090_H()) {
            this.func_191958_b(f, f2, f3, 0.1f);
            this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
            this.field_70159_w *= (double)0.9f;
            this.field_70181_x *= (double)0.9f;
            this.field_70179_y *= (double)0.9f;
            if (!this.func_175472_n() && this.func_70638_az() == null) {
                this.field_70181_x -= 0.005;
            }
        } else {
            super.func_191986_a(f, f2, f3);
        }
    }

    static class GuardianMoveHelper
    extends EntityMoveHelper {
        private final EntityGuardian field_179930_g;

        public GuardianMoveHelper(EntityGuardian entityGuardian) {
            super(entityGuardian);
            this.field_179930_g = entityGuardian;
        }

        @Override
        public void func_75641_c() {
            if (this.field_188491_h != EntityMoveHelper.Action.MOVE_TO || this.field_179930_g.func_70661_as().func_75500_f()) {
                this.field_179930_g.func_70659_e(0.0f);
                this.field_179930_g.func_175476_l(false);
                return;
            }
            double d = this.field_75646_b - this.field_179930_g.field_70165_t;
            \u2603 = this.field_75647_c - this.field_179930_g.field_70163_u;
            \u2603 = this.field_75644_d - this.field_179930_g.field_70161_v;
            \u2603 = MathHelper.func_76133_a(d * d + \u2603 * \u2603 + \u2603 * \u2603);
            \u2603 /= \u2603;
            float \u26032 = (float)(MathHelper.func_181159_b(\u2603, d) * 57.2957763671875) - 90.0f;
            this.field_179930_g.field_70761_aq = this.field_179930_g.field_70177_z = this.func_75639_a(this.field_179930_g.field_70177_z, \u26032, 90.0f);
            float \u26033 = (float)(this.field_75645_e * this.field_179930_g.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e());
            this.field_179930_g.func_70659_e(this.field_179930_g.func_70689_ay() + (\u26033 - this.field_179930_g.func_70689_ay()) * 0.125f);
            \u2603 = Math.sin((double)(this.field_179930_g.field_70173_aa + this.field_179930_g.func_145782_y()) * 0.5) * 0.05;
            \u2603 = Math.cos(this.field_179930_g.field_70177_z * ((float)Math.PI / 180));
            \u2603 = Math.sin(this.field_179930_g.field_70177_z * ((float)Math.PI / 180));
            this.field_179930_g.field_70159_w += \u2603 * \u2603;
            this.field_179930_g.field_70179_y += \u2603 * \u2603;
            \u2603 = Math.sin((double)(this.field_179930_g.field_70173_aa + this.field_179930_g.func_145782_y()) * 0.75) * 0.05;
            this.field_179930_g.field_70181_x += \u2603 * (\u2603 + \u2603) * 0.25;
            this.field_179930_g.field_70181_x += (double)this.field_179930_g.func_70689_ay() * \u2603 * 0.1;
            EntityLookHelper \u26034 = this.field_179930_g.func_70671_ap();
            \u2603 = this.field_179930_g.field_70165_t + d / \u2603 * 2.0;
            \u2603 = (double)this.field_179930_g.func_70047_e() + this.field_179930_g.field_70163_u + \u2603 / \u2603;
            \u2603 = this.field_179930_g.field_70161_v + \u2603 / \u2603 * 2.0;
            \u2603 = \u26034.func_180423_e();
            \u2603 = \u26034.func_180422_f();
            \u2603 = \u26034.func_180421_g();
            if (!\u26034.func_180424_b()) {
                \u2603 = \u2603;
                \u2603 = \u2603;
                \u2603 = \u2603;
            }
            this.field_179930_g.func_70671_ap().func_75650_a(\u2603 + (\u2603 - \u2603) * 0.125, \u2603 + (\u2603 - \u2603) * 0.125, \u2603 + (\u2603 - \u2603) * 0.125, 10.0f, 40.0f);
            this.field_179930_g.func_175476_l(true);
        }
    }

    static class AIGuardianAttack
    extends EntityAIBase {
        private final EntityGuardian field_179456_a;
        private int field_179455_b;
        private final boolean field_190881_c;

        public AIGuardianAttack(EntityGuardian entityGuardian) {
            this.field_179456_a = entityGuardian;
            this.field_190881_c = entityGuardian instanceof EntityElderGuardian;
            this.func_75248_a(3);
        }

        @Override
        public boolean func_75250_a() {
            EntityLivingBase entityLivingBase = this.field_179456_a.func_70638_az();
            return entityLivingBase != null && entityLivingBase.func_70089_S();
        }

        @Override
        public boolean func_75253_b() {
            return super.func_75253_b() && (this.field_190881_c || this.field_179456_a.func_70068_e(this.field_179456_a.func_70638_az()) > 9.0);
        }

        @Override
        public void func_75249_e() {
            this.field_179455_b = -10;
            this.field_179456_a.func_70661_as().func_75499_g();
            this.field_179456_a.func_70671_ap().func_75651_a(this.field_179456_a.func_70638_az(), 90.0f, 90.0f);
            this.field_179456_a.field_70160_al = true;
        }

        @Override
        public void func_75251_c() {
            this.field_179456_a.func_175463_b(0);
            this.field_179456_a.func_70624_b(null);
            this.field_179456_a.field_175481_bq.func_179480_f();
        }

        @Override
        public void func_75246_d() {
            EntityLivingBase entityLivingBase = this.field_179456_a.func_70638_az();
            this.field_179456_a.func_70661_as().func_75499_g();
            this.field_179456_a.func_70671_ap().func_75651_a(entityLivingBase, 90.0f, 90.0f);
            if (!this.field_179456_a.func_70685_l(entityLivingBase)) {
                this.field_179456_a.func_70624_b(null);
                return;
            }
            ++this.field_179455_b;
            if (this.field_179455_b == 0) {
                this.field_179456_a.func_175463_b(this.field_179456_a.func_70638_az().func_145782_y());
                this.field_179456_a.field_70170_p.func_72960_a(this.field_179456_a, (byte)21);
            } else if (this.field_179455_b >= this.field_179456_a.func_175464_ck()) {
                float f = 1.0f;
                if (this.field_179456_a.field_70170_p.func_175659_aa() == EnumDifficulty.HARD) {
                    f += 2.0f;
                }
                if (this.field_190881_c) {
                    f += 2.0f;
                }
                entityLivingBase.func_70097_a(DamageSource.func_76354_b(this.field_179456_a, this.field_179456_a), f);
                entityLivingBase.func_70097_a(DamageSource.func_76358_a(this.field_179456_a), (float)this.field_179456_a.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e());
                this.field_179456_a.func_70624_b(null);
            }
            super.func_75246_d();
        }
    }

    static class GuardianTargetSelector
    implements Predicate<EntityLivingBase> {
        private final EntityGuardian field_179916_a;

        public GuardianTargetSelector(EntityGuardian entityGuardian) {
            this.field_179916_a = entityGuardian;
        }

        public boolean apply(@Nullable EntityLivingBase entityLivingBase) {
            return (entityLivingBase instanceof EntityPlayer || entityLivingBase instanceof EntitySquid) && entityLivingBase.func_70068_e(this.field_179916_a) > 9.0;
        }

        public /* synthetic */ boolean apply(@Nullable Object object) {
            return this.apply((EntityLivingBase)object);
        }
    }
}

