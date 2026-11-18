/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.boss;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseList;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathHeap;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.feature.WorldGenEndPodium;
import net.minecraft.world.storage.loot.LootTableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityDragon
extends EntityLiving
implements IEntityMultiPart,
IMob {
    private static final Logger field_184675_bH = LogManager.getLogger();
    public static final DataParameter<Integer> field_184674_a = EntityDataManager.func_187226_a(EntityDragon.class, DataSerializers.field_187192_b);
    public double[][] field_70979_e = new double[64][3];
    public int field_70976_f = -1;
    public MultiPartEntityPart[] field_70977_g;
    public MultiPartEntityPart field_70986_h;
    public MultiPartEntityPart field_184673_bv;
    public MultiPartEntityPart field_70987_i;
    public MultiPartEntityPart field_70985_j;
    public MultiPartEntityPart field_70984_by;
    public MultiPartEntityPart field_70982_bz;
    public MultiPartEntityPart field_70983_bA;
    public MultiPartEntityPart field_70990_bB;
    public float field_70991_bC;
    public float field_70988_bD;
    public boolean field_70994_bF;
    public int field_70995_bG;
    public EntityEnderCrystal field_70992_bH;
    private final DragonFightManager field_184676_bI;
    private final PhaseManager field_184677_bJ;
    private int field_184678_bK = 200;
    private int field_184679_bL;
    private final PathPoint[] field_184680_bM = new PathPoint[24];
    private final int[] field_184681_bN = new int[24];
    private final PathHeap field_184682_bO = new PathHeap();

    public EntityDragon(World world) {
        super(world);
        this.field_70986_h = new MultiPartEntityPart(this, "head", 6.0f, 6.0f);
        this.field_184673_bv = new MultiPartEntityPart(this, "neck", 6.0f, 6.0f);
        this.field_70987_i = new MultiPartEntityPart(this, "body", 8.0f, 8.0f);
        this.field_70985_j = new MultiPartEntityPart(this, "tail", 4.0f, 4.0f);
        this.field_70984_by = new MultiPartEntityPart(this, "tail", 4.0f, 4.0f);
        this.field_70982_bz = new MultiPartEntityPart(this, "tail", 4.0f, 4.0f);
        this.field_70983_bA = new MultiPartEntityPart(this, "wing", 4.0f, 4.0f);
        this.field_70990_bB = new MultiPartEntityPart(this, "wing", 4.0f, 4.0f);
        this.field_70977_g = new MultiPartEntityPart[]{this.field_70986_h, this.field_184673_bv, this.field_70987_i, this.field_70985_j, this.field_70984_by, this.field_70982_bz, this.field_70983_bA, this.field_70990_bB};
        this.func_70606_j(this.func_110138_aP());
        this.func_70105_a(16.0f, 8.0f);
        this.field_70145_X = true;
        this.field_70178_ae = true;
        this.field_184678_bK = 100;
        this.field_70158_ak = true;
        this.field_184676_bI = !world.field_72995_K && world.field_73011_w instanceof WorldProviderEnd ? ((WorldProviderEnd)world.field_73011_w).func_186063_s() : null;
        this.field_184677_bJ = new PhaseManager(this);
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(200.0);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.func_184212_Q().func_187214_a(field_184674_a, PhaseList.field_188751_k.func_188740_b());
    }

    public double[] func_70974_a(int n, float f) {
        if (this.func_110143_aJ() <= 0.0f) {
            f = 0.0f;
        }
        f = 1.0f - f;
        int n2 = this.field_70976_f - n & 0x3F;
        \u2603 = this.field_70976_f - n - 1 & 0x3F;
        double[] \u26032 = new double[3];
        double \u26033 = this.field_70979_e[n2][0];
        double \u26034 = MathHelper.func_76138_g(this.field_70979_e[\u2603][0] - \u26033);
        \u26032[0] = \u26033 + \u26034 * (double)f;
        \u26033 = this.field_70979_e[n2][1];
        \u26034 = this.field_70979_e[\u2603][1] - \u26033;
        \u26032[1] = \u26033 + \u26034 * (double)f;
        \u26032[2] = this.field_70979_e[n2][2] + (this.field_70979_e[\u2603][2] - this.field_70979_e[n2][2]) * (double)f;
        return \u26032;
    }

    @Override
    public void func_70636_d() {
        int n;
        float \u26035;
        float \u26032;
        float f;
        if (this.field_70170_p.field_72995_K) {
            this.func_70606_j(this.func_110143_aJ());
            if (!this.func_174814_R()) {
                f = MathHelper.func_76134_b(this.field_70988_bD * ((float)Math.PI * 2));
                \u2603 = MathHelper.func_76134_b(this.field_70991_bC * ((float)Math.PI * 2));
                if (\u2603 <= -0.3f && f >= -0.3f) {
                    this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187524_aN, this.func_184176_by(), 5.0f, 0.8f + this.field_70146_Z.nextFloat() * 0.3f, false);
                }
                if (!this.field_184677_bJ.func_188756_a().func_188654_a() && --this.field_184678_bK < 0) {
                    this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187525_aO, this.func_184176_by(), 2.5f, 0.8f + this.field_70146_Z.nextFloat() * 0.3f, false);
                    this.field_184678_bK = 200 + this.field_70146_Z.nextInt(200);
                }
            }
        }
        this.field_70991_bC = this.field_70988_bD;
        if (this.func_110143_aJ() <= 0.0f) {
            f = (this.field_70146_Z.nextFloat() - 0.5f) * 8.0f;
            \u2603 = (this.field_70146_Z.nextFloat() - 0.5f) * 4.0f;
            \u2603 = (this.field_70146_Z.nextFloat() - 0.5f) * 8.0f;
            this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_LARGE, this.field_70165_t + (double)f, this.field_70163_u + 2.0 + (double)\u2603, this.field_70161_v + (double)\u2603, 0.0, 0.0, 0.0, new int[0]);
            return;
        }
        this.func_70969_j();
        f = 0.2f / (MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y) * 10.0f + 1.0f);
        this.field_70988_bD = this.field_184677_bJ.func_188756_a().func_188654_a() ? (this.field_70988_bD += 0.1f) : (this.field_70994_bF ? (this.field_70988_bD += f * 0.5f) : (this.field_70988_bD += (f *= (float)Math.pow(2.0, this.field_70181_x))));
        this.field_70177_z = MathHelper.func_76142_g(this.field_70177_z);
        if (this.func_175446_cd()) {
            this.field_70988_bD = 0.5f;
            return;
        }
        if (this.field_70976_f < 0) {
            for (int i = 0; i < this.field_70979_e.length; ++i) {
                this.field_70979_e[i][0] = this.field_70177_z;
                this.field_70979_e[i][1] = this.field_70163_u;
            }
        }
        if (++this.field_70976_f == this.field_70979_e.length) {
            this.field_70976_f = 0;
        }
        this.field_70979_e[this.field_70976_f][0] = this.field_70177_z;
        this.field_70979_e[this.field_70976_f][1] = this.field_70163_u;
        if (this.field_70170_p.field_72995_K) {
            if (this.field_70716_bi > 0) {
                double d = this.field_70165_t + (this.field_184623_bh - this.field_70165_t) / (double)this.field_70716_bi;
                d = this.field_70163_u + (this.field_184624_bi - this.field_70163_u) / (double)this.field_70716_bi;
                \u2603 = this.field_70161_v + (this.field_184625_bj - this.field_70161_v) / (double)this.field_70716_bi;
                \u2603 = MathHelper.func_76138_g(this.field_184626_bk - (double)this.field_70177_z);
                this.field_70177_z = (float)((double)this.field_70177_z + \u2603 / (double)this.field_70716_bi);
                this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70709_bj - (double)this.field_70125_A) / (double)this.field_70716_bi);
                --this.field_70716_bi;
                this.func_70107_b(d, d, \u2603);
                this.func_70101_b(this.field_70177_z, this.field_70125_A);
            }
            this.field_184677_bJ.func_188756_a().func_188657_b();
        } else {
            IPhase iPhase = this.field_184677_bJ.func_188756_a();
            iPhase.func_188659_c();
            if (this.field_184677_bJ.func_188756_a() != iPhase) {
                iPhase = this.field_184677_bJ.func_188756_a();
                iPhase.func_188659_c();
            }
            if ((\u2603 = iPhase.func_188650_g()) != null) {
                double d = \u2603.field_72450_a - this.field_70165_t;
                \u2603 = \u2603.field_72448_b - this.field_70163_u;
                \u2603 = \u2603.field_72449_c - this.field_70161_v;
                \u2603 = d * d + \u2603 * \u2603 + \u2603 * \u2603;
                \u26032 = iPhase.func_188651_f();
                \u2603 = MathHelper.func_151237_a(\u2603 / (double)MathHelper.func_76133_a(d * d + \u2603 * \u2603), -\u26032, \u26032);
                this.field_70181_x += \u2603 * (double)0.1f;
                this.field_70177_z = MathHelper.func_76142_g(this.field_70177_z);
                \u2603 = MathHelper.func_151237_a(MathHelper.func_76138_g(180.0 - MathHelper.func_181159_b(d, \u2603) * 57.2957763671875 - (double)this.field_70177_z), -50.0, 50.0);
                Vec3d \u26033 = new Vec3d(\u2603.field_72450_a - this.field_70165_t, \u2603.field_72448_b - this.field_70163_u, \u2603.field_72449_c - this.field_70161_v).func_72432_b();
                Vec3d \u26034 = new Vec3d(MathHelper.func_76126_a(this.field_70177_z * ((float)Math.PI / 180)), this.field_70181_x, -MathHelper.func_76134_b(this.field_70177_z * ((float)Math.PI / 180))).func_72432_b();
                \u26035 = Math.max(((float)\u26034.func_72430_b(\u26033) + 0.5f) / 1.5f, 0.0f);
                this.field_70704_bt *= 0.8f;
                this.field_70704_bt = (float)((double)this.field_70704_bt + \u2603 * (double)iPhase.func_188653_h());
                this.field_70177_z += this.field_70704_bt * 0.1f;
                float \u26036 = (float)(2.0 / (\u2603 + 1.0));
                float \u26037 = 0.06f;
                this.func_191958_b(0.0f, 0.0f, -1.0f, 0.06f * (\u26035 * \u26036 + (1.0f - \u26036)));
                if (this.field_70994_bF) {
                    this.func_70091_d(MoverType.SELF, this.field_70159_w * (double)0.8f, this.field_70181_x * (double)0.8f, this.field_70179_y * (double)0.8f);
                } else {
                    this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                }
                Vec3d \u26038 = new Vec3d(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_72432_b();
                float \u26039 = ((float)\u26038.func_72430_b(\u26034) + 1.0f) / 2.0f;
                \u26039 = 0.8f + 0.15f * \u26039;
                this.field_70159_w *= (double)\u26039;
                this.field_70179_y *= (double)\u26039;
                this.field_70181_x *= (double)0.91f;
            }
        }
        this.field_70761_aq = this.field_70177_z;
        this.field_70986_h.field_70130_N = 1.0f;
        this.field_70986_h.field_70131_O = 1.0f;
        this.field_184673_bv.field_70130_N = 3.0f;
        this.field_184673_bv.field_70131_O = 3.0f;
        this.field_70985_j.field_70130_N = 2.0f;
        this.field_70985_j.field_70131_O = 2.0f;
        this.field_70984_by.field_70130_N = 2.0f;
        this.field_70984_by.field_70131_O = 2.0f;
        this.field_70982_bz.field_70130_N = 2.0f;
        this.field_70982_bz.field_70131_O = 2.0f;
        this.field_70987_i.field_70131_O = 3.0f;
        this.field_70987_i.field_70130_N = 5.0f;
        this.field_70983_bA.field_70131_O = 2.0f;
        this.field_70983_bA.field_70130_N = 4.0f;
        this.field_70990_bB.field_70131_O = 3.0f;
        this.field_70990_bB.field_70130_N = 4.0f;
        Vec3d[] vec3dArray = new Vec3d[this.field_70977_g.length];
        for (int i = 0; i < this.field_70977_g.length; ++i) {
            vec3dArray[i] = new Vec3d(this.field_70977_g[i].field_70165_t, this.field_70977_g[i].field_70163_u, this.field_70977_g[i].field_70161_v);
        }
        float f2 = (float)(this.func_70974_a(5, 1.0f)[1] - this.func_70974_a(10, 1.0f)[1]) * 10.0f * ((float)Math.PI / 180);
        \u2603 = MathHelper.func_76134_b(f2);
        \u2603 = MathHelper.func_76126_a(f2);
        \u2603 = this.field_70177_z * ((float)Math.PI / 180);
        \u2603 = MathHelper.func_76126_a(\u2603);
        \u2603 = MathHelper.func_76134_b(\u2603);
        this.field_70987_i.func_70071_h_();
        this.field_70987_i.func_70012_b(this.field_70165_t + (double)(\u2603 * 0.5f), this.field_70163_u, this.field_70161_v - (double)(\u2603 * 0.5f), 0.0f, 0.0f);
        this.field_70983_bA.func_70071_h_();
        this.field_70983_bA.func_70012_b(this.field_70165_t + (double)(\u2603 * 4.5f), this.field_70163_u + 2.0, this.field_70161_v + (double)(\u2603 * 4.5f), 0.0f, 0.0f);
        this.field_70990_bB.func_70071_h_();
        this.field_70990_bB.func_70012_b(this.field_70165_t - (double)(\u2603 * 4.5f), this.field_70163_u + 2.0, this.field_70161_v - (double)(\u2603 * 4.5f), 0.0f, 0.0f);
        if (!this.field_70170_p.field_72995_K && this.field_70737_aN == 0) {
            this.func_70970_a(this.field_70170_p.func_72839_b(this, this.field_70983_bA.func_174813_aQ().func_72314_b(4.0, 2.0, 4.0).func_72317_d(0.0, -2.0, 0.0)));
            this.func_70970_a(this.field_70170_p.func_72839_b(this, this.field_70990_bB.func_174813_aQ().func_72314_b(4.0, 2.0, 4.0).func_72317_d(0.0, -2.0, 0.0)));
            this.func_70971_b(this.field_70170_p.func_72839_b(this, this.field_70986_h.func_174813_aQ().func_186662_g(1.0)));
            this.func_70971_b(this.field_70170_p.func_72839_b(this, this.field_184673_bv.func_174813_aQ().func_186662_g(1.0)));
        }
        double[] \u260310 = this.func_70974_a(5, 1.0f);
        \u2603 = MathHelper.func_76126_a(this.field_70177_z * ((float)Math.PI / 180) - this.field_70704_bt * 0.01f);
        \u2603 = MathHelper.func_76134_b(this.field_70177_z * ((float)Math.PI / 180) - this.field_70704_bt * 0.01f);
        this.field_70986_h.func_70071_h_();
        this.field_184673_bv.func_70071_h_();
        \u26032 = this.func_184662_q(1.0f);
        this.field_70986_h.func_70012_b(this.field_70165_t + (double)(\u2603 * 6.5f * \u2603), this.field_70163_u + (double)\u26032 + (double)(\u2603 * 6.5f), this.field_70161_v - (double)(\u2603 * 6.5f * \u2603), 0.0f, 0.0f);
        this.field_184673_bv.func_70012_b(this.field_70165_t + (double)(\u2603 * 5.5f * \u2603), this.field_70163_u + (double)\u26032 + (double)(\u2603 * 5.5f), this.field_70161_v - (double)(\u2603 * 5.5f * \u2603), 0.0f, 0.0f);
        for (n = 0; n < 3; ++n) {
            MultiPartEntityPart multiPartEntityPart = null;
            if (n == 0) {
                multiPartEntityPart = this.field_70985_j;
            }
            if (n == 1) {
                multiPartEntityPart = this.field_70984_by;
            }
            if (n == 2) {
                multiPartEntityPart = this.field_70982_bz;
            }
            double[] \u260311 = this.func_70974_a(12 + n * 2, 1.0f);
            float \u260312 = this.field_70177_z * ((float)Math.PI / 180) + this.func_70973_b(\u260311[0] - \u260310[0]) * ((float)Math.PI / 180);
            float \u260313 = MathHelper.func_76126_a(\u260312);
            float \u260314 = MathHelper.func_76134_b(\u260312);
            float \u260315 = 1.5f;
            \u26035 = (float)(n + 1) * 2.0f;
            multiPartEntityPart.func_70071_h_();
            multiPartEntityPart.func_70012_b(this.field_70165_t - (double)((\u2603 * 1.5f + \u260313 * \u26035) * \u2603), this.field_70163_u + (\u260311[1] - \u260310[1]) - (double)((\u26035 + 1.5f) * \u2603) + 1.5, this.field_70161_v + (double)((\u2603 * 1.5f + \u260314 * \u26035) * \u2603), 0.0f, 0.0f);
        }
        if (!this.field_70170_p.field_72995_K) {
            this.field_70994_bF = this.func_70972_a(this.field_70986_h.func_174813_aQ()) | this.func_70972_a(this.field_184673_bv.func_174813_aQ()) | this.func_70972_a(this.field_70987_i.func_174813_aQ());
            if (this.field_184676_bI != null) {
                this.field_184676_bI.func_186099_b(this);
            }
        }
        for (n = 0; n < this.field_70977_g.length; ++n) {
            this.field_70977_g[n].field_70169_q = vec3dArray[n].field_72450_a;
            this.field_70977_g[n].field_70167_r = vec3dArray[n].field_72448_b;
            this.field_70977_g[n].field_70166_s = vec3dArray[n].field_72449_c;
        }
    }

    private float func_184662_q(float f) {
        double \u26032;
        if (this.field_184677_bJ.func_188756_a().func_188654_a()) {
            \u26032 = -1.0;
        } else {
            double[] dArray = this.func_70974_a(5, 1.0f);
            \u2603 = this.func_70974_a(0, 1.0f);
            \u26032 = dArray[1] - \u2603[1];
        }
        return (float)\u26032;
    }

    private void func_70969_j() {
        if (this.field_70992_bH != null) {
            if (this.field_70992_bH.field_70128_L) {
                this.field_70992_bH = null;
            } else if (this.field_70173_aa % 10 == 0 && this.func_110143_aJ() < this.func_110138_aP()) {
                this.func_70606_j(this.func_110143_aJ() + 1.0f);
            }
        }
        if (this.field_70146_Z.nextInt(10) == 0) {
            List<EntityEnderCrystal> list = this.field_70170_p.func_72872_a(EntityEnderCrystal.class, this.func_174813_aQ().func_186662_g(32.0));
            EntityEnderCrystal \u26032 = null;
            double \u26033 = Double.MAX_VALUE;
            for (EntityEnderCrystal entityEnderCrystal : list) {
                double d = entityEnderCrystal.func_70068_e(this);
                if (!(d < \u26033)) continue;
                \u26033 = d;
                \u26032 = entityEnderCrystal;
            }
            this.field_70992_bH = \u26032;
        }
    }

    private void func_70970_a(List<Entity> list) {
        double d = (this.field_70987_i.func_174813_aQ().field_72340_a + this.field_70987_i.func_174813_aQ().field_72336_d) / 2.0;
        \u2603 = (this.field_70987_i.func_174813_aQ().field_72339_c + this.field_70987_i.func_174813_aQ().field_72334_f) / 2.0;
        for (Entity entity : list) {
            if (!(entity instanceof EntityLivingBase)) continue;
            double d2 = entity.field_70165_t - d;
            \u2603 = entity.field_70161_v - \u2603;
            \u2603 = d2 * d2 + \u2603 * \u2603;
            entity.func_70024_g(d2 / \u2603 * 4.0, 0.2f, \u2603 / \u2603 * 4.0);
            if (this.field_184677_bJ.func_188756_a().func_188654_a() || ((EntityLivingBase)entity).func_142015_aE() >= entity.field_70173_aa - 2) continue;
            entity.func_70097_a(DamageSource.func_76358_a(this), 5.0f);
            this.func_174815_a(this, entity);
        }
    }

    private void func_70971_b(List<Entity> list) {
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (!(entity instanceof EntityLivingBase)) continue;
            entity.func_70097_a(DamageSource.func_76358_a(this), 10.0f);
            this.func_174815_a(this, entity);
        }
    }

    private float func_70973_b(double d) {
        return (float)MathHelper.func_76138_g(d);
    }

    private boolean func_70972_a(AxisAlignedBB axisAlignedBB) {
        int n = MathHelper.func_76128_c(axisAlignedBB.field_72340_a);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72338_b);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72339_c);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72336_d);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72337_e);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72334_f);
        boolean \u26032 = false;
        boolean \u26033 = false;
        for (\u2603 = n; \u2603 <= \u2603; ++\u2603) {
            for (\u2603 = \u2603; \u2603 <= \u2603; ++\u2603) {
                for (\u2603 = \u2603; \u2603 <= \u2603; ++\u2603) {
                    BlockPos blockPos = new BlockPos(\u2603, \u2603, \u2603);
                    IBlockState \u26034 = this.field_70170_p.func_180495_p(blockPos);
                    Block \u26035 = \u26034.func_177230_c();
                    if (\u26034.func_185904_a() == Material.field_151579_a || \u26034.func_185904_a() == Material.field_151581_o) continue;
                    if (!this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
                        \u26032 = true;
                        continue;
                    }
                    if (\u26035 == Blocks.field_180401_cv || \u26035 == Blocks.field_150343_Z || \u26035 == Blocks.field_150377_bs || \u26035 == Blocks.field_150357_h || \u26035 == Blocks.field_150384_bq || \u26035 == Blocks.field_150378_br) {
                        \u26032 = true;
                        continue;
                    }
                    if (\u26035 == Blocks.field_150483_bI || \u26035 == Blocks.field_185776_dc || \u26035 == Blocks.field_185777_dd || \u26035 == Blocks.field_150411_aY || \u26035 == Blocks.field_185775_db) {
                        \u26032 = true;
                        continue;
                    }
                    \u26033 = this.field_70170_p.func_175698_g(blockPos) || \u26033;
                }
            }
        }
        if (\u26033) {
            double d = axisAlignedBB.field_72340_a + (axisAlignedBB.field_72336_d - axisAlignedBB.field_72340_a) * (double)this.field_70146_Z.nextFloat();
            \u2603 = axisAlignedBB.field_72338_b + (axisAlignedBB.field_72337_e - axisAlignedBB.field_72338_b) * (double)this.field_70146_Z.nextFloat();
            \u2603 = axisAlignedBB.field_72339_c + (axisAlignedBB.field_72334_f - axisAlignedBB.field_72339_c) * (double)this.field_70146_Z.nextFloat();
            this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_LARGE, d, \u2603, \u2603, 0.0, 0.0, 0.0, new int[0]);
        }
        return \u26032;
    }

    @Override
    public boolean func_70965_a(MultiPartEntityPart multiPartEntityPart, DamageSource damageSource, float f) {
        f = this.field_184677_bJ.func_188756_a().func_188656_a(multiPartEntityPart, damageSource, f);
        if (multiPartEntityPart != this.field_70986_h) {
            f = f / 4.0f + Math.min(f, 1.0f);
        }
        if (f < 0.01f) {
            return false;
        }
        if (damageSource.func_76346_g() instanceof EntityPlayer || damageSource.func_94541_c()) {
            \u2603 = this.func_110143_aJ();
            this.func_82195_e(damageSource, f);
            if (this.func_110143_aJ() <= 0.0f && !this.field_184677_bJ.func_188756_a().func_188654_a()) {
                this.func_70606_j(1.0f);
                this.field_184677_bJ.func_188758_a(PhaseList.field_188750_j);
            }
            if (this.field_184677_bJ.func_188756_a().func_188654_a()) {
                this.field_184679_bL = (int)((float)this.field_184679_bL + (\u2603 - this.func_110143_aJ()));
                if ((float)this.field_184679_bL > 0.25f * this.func_110138_aP()) {
                    this.field_184679_bL = 0;
                    this.field_184677_bJ.func_188758_a(PhaseList.field_188745_e);
                }
            }
        }
        return true;
    }

    @Override
    public boolean func_70097_a(DamageSource damageSource, float f) {
        if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).func_180139_w()) {
            this.func_70965_a(this.field_70987_i, damageSource, f);
        }
        return false;
    }

    protected boolean func_82195_e(DamageSource damageSource, float f) {
        return super.func_70097_a(damageSource, f);
    }

    @Override
    public void func_174812_G() {
        this.func_70106_y();
        if (this.field_184676_bI != null) {
            this.field_184676_bI.func_186099_b(this);
            this.field_184676_bI.func_186096_a(this);
        }
    }

    @Override
    protected void func_70609_aI() {
        if (this.field_184676_bI != null) {
            this.field_184676_bI.func_186099_b(this);
        }
        ++this.field_70995_bG;
        if (this.field_70995_bG >= 180 && this.field_70995_bG <= 200) {
            float f = (this.field_70146_Z.nextFloat() - 0.5f) * 8.0f;
            \u2603 = (this.field_70146_Z.nextFloat() - 0.5f) * 4.0f;
            \u2603 = (this.field_70146_Z.nextFloat() - 0.5f) * 8.0f;
            this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_HUGE, this.field_70165_t + (double)f, this.field_70163_u + 2.0 + (double)\u2603, this.field_70161_v + (double)\u2603, 0.0, 0.0, 0.0, new int[0]);
        }
        boolean bl = this.field_70170_p.func_82736_K().func_82766_b("doMobLoot");
        int \u26032 = 500;
        if (this.field_184676_bI != null && !this.field_184676_bI.func_186102_d()) {
            \u26032 = 12000;
        }
        if (!this.field_70170_p.field_72995_K) {
            if (this.field_70995_bG > 150 && this.field_70995_bG % 5 == 0 && bl) {
                this.func_184668_a(MathHelper.func_76141_d((float)\u26032 * 0.08f));
            }
            if (this.field_70995_bG == 1) {
                this.field_70170_p.func_175669_a(1028, new BlockPos(this), 0);
            }
        }
        this.func_70091_d(MoverType.SELF, 0.0, 0.1f, 0.0);
        this.field_70177_z += 20.0f;
        this.field_70761_aq = this.field_70177_z;
        if (this.field_70995_bG == 200 && !this.field_70170_p.field_72995_K) {
            if (bl) {
                this.func_184668_a(MathHelper.func_76141_d((float)\u26032 * 0.2f));
            }
            if (this.field_184676_bI != null) {
                this.field_184676_bI.func_186096_a(this);
            }
            this.func_70106_y();
        }
    }

    private void func_184668_a(int n) {
        while (n > 0) {
            \u2603 = EntityXPOrb.func_70527_a(n);
            n -= \u2603;
            this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, \u2603));
        }
    }

    public int func_184671_o() {
        if (this.field_184680_bM[0] == null) {
            for (int i = 0; i < 24; ++i) {
                \u2603 = 5;
                \u2603 = i;
                if (i < 12) {
                    \u2603 = (int)(60.0f * MathHelper.func_76134_b(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)\u2603)));
                    \u2603 = (int)(60.0f * MathHelper.func_76126_a(2.0f * ((float)(-Math.PI) + 0.2617994f * (float)\u2603)));
                } else if (i < 20) {
                    \u2603 = (int)(40.0f * MathHelper.func_76134_b(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)(\u2603 -= 12))));
                    \u2603 = (int)(40.0f * MathHelper.func_76126_a(2.0f * ((float)(-Math.PI) + 0.3926991f * (float)\u2603)));
                    \u2603 += 10;
                } else {
                    \u2603 = (int)(20.0f * MathHelper.func_76134_b(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)(\u2603 -= 20))));
                    \u2603 = (int)(20.0f * MathHelper.func_76126_a(2.0f * ((float)(-Math.PI) + 0.7853982f * (float)\u2603)));
                }
                \u2603 = Math.max(this.field_70170_p.func_181545_F() + 10, this.field_70170_p.func_175672_r(new BlockPos(\u2603, 0, \u2603)).func_177956_o() + \u2603);
                this.field_184680_bM[i] = new PathPoint(\u2603, \u2603, \u2603);
            }
            this.field_184681_bN[0] = 6146;
            this.field_184681_bN[1] = 8197;
            this.field_184681_bN[2] = 8202;
            this.field_184681_bN[3] = 16404;
            this.field_184681_bN[4] = 32808;
            this.field_184681_bN[5] = 32848;
            this.field_184681_bN[6] = 65696;
            this.field_184681_bN[7] = 131392;
            this.field_184681_bN[8] = 131712;
            this.field_184681_bN[9] = 263424;
            this.field_184681_bN[10] = 526848;
            this.field_184681_bN[11] = 525313;
            this.field_184681_bN[12] = 1581057;
            this.field_184681_bN[13] = 3166214;
            this.field_184681_bN[14] = 2138120;
            this.field_184681_bN[15] = 6373424;
            this.field_184681_bN[16] = 4358208;
            this.field_184681_bN[17] = 12910976;
            this.field_184681_bN[18] = 9044480;
            this.field_184681_bN[19] = 9706496;
            this.field_184681_bN[20] = 15216640;
            this.field_184681_bN[21] = 0xD0E000;
            this.field_184681_bN[22] = 11763712;
            this.field_184681_bN[23] = 0x7E0000;
        }
        return this.func_184663_l(this.field_70165_t, this.field_70163_u, this.field_70161_v);
    }

    public int func_184663_l(double d, double d2, double d3) {
        float f = 10000.0f;
        int \u26032 = 0;
        PathPoint \u26033 = new PathPoint(MathHelper.func_76128_c(d), MathHelper.func_76128_c(d2), MathHelper.func_76128_c(d3));
        int \u26034 = 0;
        if (this.field_184676_bI == null || this.field_184676_bI.func_186092_c() == 0) {
            \u26034 = 12;
        }
        for (int i = \u26034; i < 24; ++i) {
            if (this.field_184680_bM[i] == null || !((\u2603 = this.field_184680_bM[i].func_75832_b(\u26033)) < f)) continue;
            f = \u2603;
            \u26032 = i;
        }
        return \u26032;
    }

    @Nullable
    public Path func_184666_a(int n3, int n2, @Nullable PathPoint pathPoint) {
        int n3;
        PathPoint \u26033;
        for (int i = 0; i < 24; ++i) {
            \u26033 = this.field_184680_bM[i];
            \u26033.field_75842_i = false;
            \u26033.field_75834_g = 0.0f;
            \u26033.field_75836_e = 0.0f;
            \u26033.field_75833_f = 0.0f;
            \u26033.field_75841_h = null;
            \u26033.field_75835_d = -1;
        }
        PathPoint \u26032 = this.field_184680_bM[n3];
        \u26033 = this.field_184680_bM[n2];
        \u26032.field_75836_e = 0.0f;
        \u26032.field_75834_g = \u26032.field_75833_f = \u26032.func_75829_a(\u26033);
        this.field_184682_bO.func_75848_a();
        this.field_184682_bO.func_75849_a(\u26032);
        PathPoint \u26034 = \u26032;
        \u2603 = 0;
        if (this.field_184676_bI == null || this.field_184676_bI.func_186092_c() == 0) {
            \u2603 = 12;
        }
        while (!this.field_184682_bO.func_75845_e()) {
            int n4;
            PathPoint pathPoint2 = this.field_184682_bO.func_75844_c();
            if (pathPoint2.equals(\u26033)) {
                if (pathPoint != null) {
                    pathPoint.field_75841_h = \u26033;
                    \u26033 = pathPoint;
                }
                return this.func_184669_a(\u26032, \u26033);
            }
            if (pathPoint2.func_75829_a(\u26033) < \u26034.func_75829_a(\u26033)) {
                \u26034 = pathPoint2;
            }
            pathPoint2.field_75842_i = true;
            int \u26035 = 0;
            for (n4 = 0; n4 < 24; ++n4) {
                if (this.field_184680_bM[n4] != pathPoint2) continue;
                \u26035 = n4;
                break;
            }
            for (n4 = \u2603; n4 < 24; ++n4) {
                if ((this.field_184681_bN[\u26035] & 1 << n4) <= 0) continue;
                PathPoint pathPoint3 = this.field_184680_bM[n4];
                if (pathPoint3.field_75842_i) continue;
                float \u26036 = pathPoint2.field_75836_e + pathPoint2.func_75829_a(pathPoint3);
                if (pathPoint3.func_75831_a() && !(\u26036 < pathPoint3.field_75836_e)) continue;
                pathPoint3.field_75841_h = pathPoint2;
                pathPoint3.field_75836_e = \u26036;
                pathPoint3.field_75833_f = pathPoint3.func_75829_a(\u26033);
                if (pathPoint3.func_75831_a()) {
                    this.field_184682_bO.func_75850_a(pathPoint3, pathPoint3.field_75836_e + pathPoint3.field_75833_f);
                    continue;
                }
                pathPoint3.field_75834_g = pathPoint3.field_75836_e + pathPoint3.field_75833_f;
                this.field_184682_bO.func_75849_a(pathPoint3);
            }
        }
        if (\u26034 == \u26032) {
            return null;
        }
        field_184675_bH.debug("Failed to find path from {} to {}", (Object)n3, (Object)n2);
        if (pathPoint != null) {
            pathPoint.field_75841_h = \u26034;
            \u26034 = pathPoint;
        }
        return this.func_184669_a(\u26032, \u26034);
    }

    private Path func_184669_a(PathPoint pathPoint, PathPoint pathPoint2) {
        int n = 1;
        PathPoint \u26032 = pathPoint2;
        while (\u26032.field_75841_h != null) {
            ++n;
            \u26032 = \u26032.field_75841_h;
        }
        PathPoint[] \u26033 = new PathPoint[n];
        \u26032 = pathPoint2;
        \u26033[--n] = \u26032;
        while (\u26032.field_75841_h != null) {
            \u26032 = \u26032.field_75841_h;
            \u26033[--n] = \u26032;
        }
        return new Path(\u26033);
    }

    public static void func_189755_b(DataFixer dataFixer) {
        EntityLiving.func_189752_a(dataFixer, EntityDragon.class);
    }

    @Override
    public void func_70014_b(NBTTagCompound nBTTagCompound) {
        super.func_70014_b(nBTTagCompound);
        nBTTagCompound.func_74768_a("DragonPhase", this.field_184677_bJ.func_188756_a().func_188652_i().func_188740_b());
    }

    @Override
    public void func_70037_a(NBTTagCompound nBTTagCompound) {
        super.func_70037_a(nBTTagCompound);
        if (nBTTagCompound.func_74764_b("DragonPhase")) {
            this.field_184677_bJ.func_188758_a(PhaseList.func_188738_a(nBTTagCompound.func_74762_e("DragonPhase")));
        }
    }

    @Override
    protected void func_70623_bb() {
    }

    @Override
    public Entity[] func_70021_al() {
        return this.field_70977_g;
    }

    @Override
    public boolean func_70067_L() {
        return false;
    }

    @Override
    public World func_82194_d() {
        return this.field_70170_p;
    }

    @Override
    public SoundCategory func_184176_by() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent func_184639_G() {
        return SoundEvents.field_187521_aK;
    }

    @Override
    protected SoundEvent func_184601_bQ(DamageSource damageSource) {
        return SoundEvents.field_187526_aP;
    }

    @Override
    protected float func_70599_aP() {
        return 5.0f;
    }

    @Override
    @Nullable
    protected ResourceLocation func_184647_J() {
        return LootTableList.field_191189_ay;
    }

    public float func_184667_a(int n, double[] dArray, double[] dArray2) {
        double \u26034;
        IPhase iPhase = this.field_184677_bJ.func_188756_a();
        PhaseList<? extends IPhase> \u26032 = iPhase.func_188652_i();
        if (\u26032 == PhaseList.field_188744_d || \u26032 == PhaseList.field_188745_e) {
            BlockPos blockPos = this.field_70170_p.func_175672_r(WorldGenEndPodium.field_186139_a);
            float \u26033 = Math.max(MathHelper.func_76133_a(this.func_174831_c(blockPos)) / 4.0f, 1.0f);
            \u26034 = (float)n / \u26033;
        } else {
            \u26034 = iPhase.func_188654_a() ? (double)n : (n == 6 ? 0.0 : dArray2[1] - dArray[1]);
        }
        return (float)\u26034;
    }

    public Vec3d func_184665_a(float f) {
        Vec3d \u26037;
        IPhase iPhase = this.field_184677_bJ.func_188756_a();
        PhaseList<? extends IPhase> \u26032 = iPhase.func_188652_i();
        if (\u26032 == PhaseList.field_188744_d || \u26032 == PhaseList.field_188745_e) {
            BlockPos blockPos = this.field_70170_p.func_175672_r(WorldGenEndPodium.field_186139_a);
            float \u26033 = Math.max(MathHelper.func_76133_a(this.func_174831_c(blockPos)) / 4.0f, 1.0f);
            float \u26034 = 6.0f / \u26033;
            float \u26035 = this.field_70125_A;
            float \u26036 = 1.5f;
            this.field_70125_A = -\u26034 * 1.5f * 5.0f;
            \u26037 = this.func_70676_i(f);
            this.field_70125_A = \u26035;
        } else if (iPhase.func_188654_a()) {
            float \u26038 = this.field_70125_A;
            float \u26039 = 1.5f;
            this.field_70125_A = -45.0f;
            \u26037 = this.func_70676_i(f);
            this.field_70125_A = \u26038;
        } else {
            \u26037 = this.func_70676_i(f);
        }
        return \u26037;
    }

    public void func_184672_a(EntityEnderCrystal entityEnderCrystal, BlockPos blockPos, DamageSource damageSource) {
        EntityPlayer entityPlayer = damageSource.func_76346_g() instanceof EntityPlayer ? (EntityPlayer)damageSource.func_76346_g() : this.field_70170_p.func_184139_a(blockPos, 64.0, 64.0);
        if (entityEnderCrystal == this.field_70992_bH) {
            this.func_70965_a(this.field_70986_h, DamageSource.func_188405_b(entityPlayer), 10.0f);
        }
        this.field_184677_bJ.func_188756_a().func_188655_a(entityEnderCrystal, blockPos, damageSource, entityPlayer);
    }

    @Override
    public void func_184206_a(DataParameter<?> dataParameter) {
        if (field_184674_a.equals(dataParameter) && this.field_70170_p.field_72995_K) {
            this.field_184677_bJ.func_188758_a(PhaseList.func_188738_a(this.func_184212_Q().func_187225_a(field_184674_a)));
        }
        super.func_184206_a(dataParameter);
    }

    public PhaseManager func_184670_cT() {
        return this.field_184677_bJ;
    }

    @Nullable
    public DragonFightManager func_184664_cU() {
        return this.field_184676_bI;
    }

    @Override
    public void func_70690_d(PotionEffect potionEffect) {
    }

    @Override
    protected boolean func_184228_n(Entity entity) {
        return false;
    }

    @Override
    public boolean func_184222_aU() {
        return false;
    }
}

