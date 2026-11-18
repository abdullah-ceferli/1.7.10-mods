/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.Explosion;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity
implements ICommandSender {
    private static final Logger field_184243_a = LogManager.getLogger();
    private static final List<ItemStack> field_190535_b = Collections.emptyList();
    private static final AxisAlignedBB field_174836_a = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static double field_70155_l = 1.0;
    private static int field_70152_a;
    private int field_145783_c = field_70152_a++;
    public boolean field_70156_m;
    private final List<Entity> field_184244_h = Lists.newArrayList();
    protected int field_184245_j;
    private Entity field_184239_as;
    public boolean field_98038_p;
    public World field_70170_p;
    public double field_70169_q;
    public double field_70167_r;
    public double field_70166_s;
    public double field_70165_t;
    public double field_70163_u;
    public double field_70161_v;
    public double field_70159_w;
    public double field_70181_x;
    public double field_70179_y;
    public float field_70177_z;
    public float field_70125_A;
    public float field_70126_B;
    public float field_70127_C;
    private AxisAlignedBB field_70121_D = field_174836_a;
    public boolean field_70122_E;
    public boolean field_70123_F;
    public boolean field_70124_G;
    public boolean field_70132_H;
    public boolean field_70133_I;
    protected boolean field_70134_J;
    private boolean field_174835_g;
    public boolean field_70128_L;
    public float field_70130_N = 0.6f;
    public float field_70131_O = 1.8f;
    public float field_70141_P;
    public float field_70140_Q;
    public float field_82151_R;
    public float field_70143_R;
    private int field_70150_b = 1;
    private float field_191959_ay = 1.0f;
    public double field_70142_S;
    public double field_70137_T;
    public double field_70136_U;
    public float field_70138_W;
    public boolean field_70145_X;
    public float field_70144_Y;
    protected Random field_70146_Z = new Random();
    public int field_70173_aa;
    private int field_190534_ay = -this.func_190531_bD();
    protected boolean field_70171_ac;
    public int field_70172_ad;
    protected boolean field_70148_d = true;
    protected boolean field_70178_ae;
    protected EntityDataManager field_70180_af;
    protected static final DataParameter<Byte> field_184240_ax;
    private static final DataParameter<Integer> field_184241_ay;
    private static final DataParameter<String> field_184242_az;
    private static final DataParameter<Boolean> field_184233_aA;
    private static final DataParameter<Boolean> field_184234_aB;
    private static final DataParameter<Boolean> field_189655_aD;
    public boolean field_70175_ag;
    public int field_70176_ah;
    public int field_70162_ai;
    public int field_70164_aj;
    public long field_70118_ct;
    public long field_70117_cu;
    public long field_70116_cv;
    public boolean field_70158_ak;
    public boolean field_70160_al;
    public int field_71088_bW;
    protected boolean field_71087_bX;
    protected int field_82153_h;
    public int field_71093_bK;
    protected BlockPos field_181016_an;
    protected Vec3d field_181017_ao;
    protected EnumFacing field_181018_ap;
    private boolean field_83001_bt;
    protected UUID field_96093_i = MathHelper.func_180182_a(this.field_70146_Z);
    protected String field_189513_ar = this.field_96093_i.toString();
    private final CommandResultStats field_174837_as = new CommandResultStats();
    protected boolean field_184238_ar;
    private final Set<String> field_184236_aF = Sets.newHashSet();
    private boolean field_184237_aG;
    private final double[] field_191505_aI = new double[]{0.0, 0.0, 0.0};
    private long field_191506_aJ;

    public Entity(World world) {
        this.field_70170_p = world;
        this.func_70107_b(0.0, 0.0, 0.0);
        if (world != null) {
            this.field_71093_bK = world.field_73011_w.func_186058_p().func_186068_a();
        }
        this.field_70180_af = new EntityDataManager(this);
        this.field_70180_af.func_187214_a(field_184240_ax, (byte)0);
        this.field_70180_af.func_187214_a(field_184241_ay, 300);
        this.field_70180_af.func_187214_a(field_184233_aA, false);
        this.field_70180_af.func_187214_a(field_184242_az, "");
        this.field_70180_af.func_187214_a(field_184234_aB, false);
        this.field_70180_af.func_187214_a(field_189655_aD, false);
        this.func_70088_a();
    }

    public int func_145782_y() {
        return this.field_145783_c;
    }

    public void func_145769_d(int n) {
        this.field_145783_c = n;
    }

    public Set<String> func_184216_O() {
        return this.field_184236_aF;
    }

    public boolean func_184211_a(String string) {
        if (this.field_184236_aF.size() >= 1024) {
            return false;
        }
        this.field_184236_aF.add(string);
        return true;
    }

    public boolean func_184197_b(String string) {
        return this.field_184236_aF.remove(string);
    }

    public void func_174812_G() {
        this.func_70106_y();
    }

    protected abstract void func_70088_a();

    public EntityDataManager func_184212_Q() {
        return this.field_70180_af;
    }

    public boolean equals(Object object) {
        if (object instanceof Entity) {
            return ((Entity)object).field_145783_c == this.field_145783_c;
        }
        return false;
    }

    public int hashCode() {
        return this.field_145783_c;
    }

    protected void func_70065_x() {
        if (this.field_70170_p == null) {
            return;
        }
        while (this.field_70163_u > 0.0 && this.field_70163_u < 256.0) {
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            if (this.field_70170_p.func_184144_a(this, this.func_174813_aQ()).isEmpty()) break;
            this.field_70163_u += 1.0;
        }
        this.field_70159_w = 0.0;
        this.field_70181_x = 0.0;
        this.field_70179_y = 0.0;
        this.field_70125_A = 0.0f;
    }

    public void func_70106_y() {
        this.field_70128_L = true;
    }

    public void func_184174_b(boolean bl) {
    }

    protected void func_70105_a(float f, float f2) {
        if (f != this.field_70130_N || f2 != this.field_70131_O) {
            \u2603 = this.field_70130_N;
            this.field_70130_N = f;
            this.field_70131_O = f2;
            if (this.field_70130_N < \u2603) {
                double d = (double)f / 2.0;
                this.func_174826_a(new AxisAlignedBB(this.field_70165_t - d, this.field_70163_u, this.field_70161_v - d, this.field_70165_t + d, this.field_70163_u + (double)this.field_70131_O, this.field_70161_v + d));
                return;
            }
            AxisAlignedBB axisAlignedBB = this.func_174813_aQ();
            this.func_174826_a(new AxisAlignedBB(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, axisAlignedBB.field_72340_a + (double)this.field_70130_N, axisAlignedBB.field_72338_b + (double)this.field_70131_O, axisAlignedBB.field_72339_c + (double)this.field_70130_N));
            if (this.field_70130_N > \u2603 && !this.field_70148_d && !this.field_70170_p.field_72995_K) {
                this.func_70091_d(MoverType.SELF, \u2603 - this.field_70130_N, 0.0, \u2603 - this.field_70130_N);
            }
        }
    }

    protected void func_70101_b(float f, float f2) {
        this.field_70177_z = f % 360.0f;
        this.field_70125_A = f2 % 360.0f;
    }

    public void func_70107_b(double d, double d2, double d3) {
        this.field_70165_t = d;
        this.field_70163_u = d2;
        this.field_70161_v = d3;
        float f = this.field_70130_N / 2.0f;
        \u2603 = this.field_70131_O;
        this.func_174826_a(new AxisAlignedBB(d - (double)f, d2, d3 - (double)f, d + (double)f, d2 + (double)\u2603, d3 + (double)f));
    }

    public void func_70082_c(float f, float f2) {
        \u2603 = this.field_70125_A;
        \u2603 = this.field_70177_z;
        this.field_70177_z = (float)((double)this.field_70177_z + (double)f * 0.15);
        this.field_70125_A = (float)((double)this.field_70125_A - (double)f2 * 0.15);
        this.field_70125_A = MathHelper.func_76131_a(this.field_70125_A, -90.0f, 90.0f);
        this.field_70127_C += this.field_70125_A - \u2603;
        this.field_70126_B += this.field_70177_z - \u2603;
        if (this.field_184239_as != null) {
            this.field_184239_as.func_184190_l(this);
        }
    }

    public void func_70071_h_() {
        if (!this.field_70170_p.field_72995_K) {
            this.func_70052_a(6, this.func_184202_aL());
        }
        this.func_70030_z();
    }

    public void func_70030_z() {
        this.field_70170_p.field_72984_F.func_76320_a("entityBaseTick");
        if (this.func_184218_aH() && this.func_184187_bx().field_70128_L) {
            this.func_184210_p();
        }
        if (this.field_184245_j > 0) {
            --this.field_184245_j;
        }
        this.field_70141_P = this.field_70140_Q;
        this.field_70169_q = this.field_70165_t;
        this.field_70167_r = this.field_70163_u;
        this.field_70166_s = this.field_70161_v;
        this.field_70127_C = this.field_70125_A;
        this.field_70126_B = this.field_70177_z;
        if (!this.field_70170_p.field_72995_K && this.field_70170_p instanceof WorldServer) {
            this.field_70170_p.field_72984_F.func_76320_a("portal");
            if (this.field_71087_bX) {
                MinecraftServer minecraftServer = this.field_70170_p.func_73046_m();
                if (minecraftServer.func_71255_r()) {
                    if (!this.func_184218_aH() && this.field_82153_h++ >= (\u2603 = this.func_82145_z())) {
                        this.field_82153_h = \u2603;
                        this.field_71088_bW = this.func_82147_ab();
                        int n = this.field_70170_p.field_73011_w.func_186058_p().func_186068_a() == -1 ? 0 : -1;
                        this.func_184204_a(n);
                    }
                    this.field_71087_bX = false;
                }
            } else {
                if (this.field_82153_h > 0) {
                    this.field_82153_h -= 4;
                }
                if (this.field_82153_h < 0) {
                    this.field_82153_h = 0;
                }
            }
            this.func_184173_H();
            this.field_70170_p.field_72984_F.func_76319_b();
        }
        this.func_174830_Y();
        this.func_70072_I();
        if (this.field_70170_p.field_72995_K) {
            this.func_70066_B();
        } else if (this.field_190534_ay > 0) {
            if (this.field_70178_ae) {
                this.field_190534_ay -= 4;
                if (this.field_190534_ay < 0) {
                    this.func_70066_B();
                }
            } else {
                if (this.field_190534_ay % 20 == 0) {
                    this.func_70097_a(DamageSource.field_76370_b, 1.0f);
                }
                --this.field_190534_ay;
            }
        }
        if (this.func_180799_ab()) {
            this.func_70044_A();
            this.field_70143_R *= 0.5f;
        }
        if (this.field_70163_u < -64.0) {
            this.func_70076_C();
        }
        if (!this.field_70170_p.field_72995_K) {
            this.func_70052_a(0, this.field_190534_ay > 0);
        }
        this.field_70148_d = false;
        this.field_70170_p.field_72984_F.func_76319_b();
    }

    protected void func_184173_H() {
        if (this.field_71088_bW > 0) {
            --this.field_71088_bW;
        }
    }

    public int func_82145_z() {
        return 1;
    }

    protected void func_70044_A() {
        if (this.field_70178_ae) {
            return;
        }
        this.func_70097_a(DamageSource.field_76371_c, 4.0f);
        this.func_70015_d(15);
    }

    public void func_70015_d(int n) {
        \u2603 = n * 20;
        if (this instanceof EntityLivingBase) {
            \u2603 = EnchantmentProtection.func_92093_a((EntityLivingBase)this, \u2603);
        }
        if (this.field_190534_ay < \u2603) {
            this.field_190534_ay = \u2603;
        }
    }

    public void func_70066_B() {
        this.field_190534_ay = 0;
    }

    protected void func_70076_C() {
        this.func_70106_y();
    }

    public boolean func_70038_c(double d, double d2, double d3) {
        AxisAlignedBB axisAlignedBB = this.func_174813_aQ().func_72317_d(d, d2, d3);
        return this.func_174809_b(axisAlignedBB);
    }

    private boolean func_174809_b(AxisAlignedBB axisAlignedBB) {
        return this.field_70170_p.func_184144_a(this, axisAlignedBB).isEmpty() && !this.field_70170_p.func_72953_d(axisAlignedBB);
    }

    public void func_70091_d(MoverType moverType, double \u260332, double \u260342, double \u260352) {
        double d;
        Object \u26039;
        Object \u26038;
        int n;
        int n2;
        double \u260352;
        double \u260342;
        double \u260332;
        if (this.field_70145_X) {
            this.func_174826_a(this.func_174813_aQ().func_72317_d(\u260332, \u260342, \u260352));
            this.func_174829_m();
            return;
        }
        if (moverType == MoverType.PISTON) {
            double \u26032;
            long l = this.field_70170_p.func_82737_E();
            if (l != this.field_191506_aJ) {
                Arrays.fill(this.field_191505_aI, 0.0);
                this.field_191506_aJ = l;
            }
            if (\u260332 != 0.0) {
                int n3 = EnumFacing.Axis.X.ordinal();
                \u26032 = MathHelper.func_151237_a(\u260332 + this.field_191505_aI[n3], -0.51, 0.51);
                \u260332 = \u26032 - this.field_191505_aI[n3];
                this.field_191505_aI[n3] = \u26032;
                if (Math.abs(\u260332) <= (double)1.0E-5f) {
                    return;
                }
            } else if (\u260342 != 0.0) {
                n3 = EnumFacing.Axis.Y.ordinal();
                \u26032 = MathHelper.func_151237_a(\u260342 + this.field_191505_aI[n3], -0.51, 0.51);
                \u260342 = \u26032 - this.field_191505_aI[n3];
                this.field_191505_aI[n3] = \u26032;
                if (Math.abs(\u260342) <= (double)1.0E-5f) {
                    return;
                }
            } else if (\u260352 != 0.0) {
                n3 = EnumFacing.Axis.Z.ordinal();
                \u26032 = MathHelper.func_151237_a(\u260352 + this.field_191505_aI[n3], -0.51, 0.51);
                \u260352 = \u26032 - this.field_191505_aI[n3];
                this.field_191505_aI[n3] = \u26032;
                if (Math.abs(\u260352) <= (double)1.0E-5f) {
                    return;
                }
            } else {
                return;
            }
        }
        this.field_70170_p.field_72984_F.func_76320_a("move");
        double d2 = this.field_70165_t;
        \u2603 = this.field_70163_u;
        \u2603 = this.field_70161_v;
        if (this.field_70134_J) {
            this.field_70134_J = false;
            \u260332 *= 0.25;
            \u260342 *= (double)0.05f;
            \u260352 *= 0.25;
            this.field_70159_w = 0.0;
            this.field_70181_x = 0.0;
            this.field_70179_y = 0.0;
        }
        d = \u260332;
        \u2603 = \u260342;
        \u2603 = \u260352;
        if ((moverType == MoverType.SELF || moverType == MoverType.PLAYER) && this.field_70122_E && this.func_70093_af() && this instanceof EntityPlayer) {
            \u2603 = 0.05;
            while (\u260332 != 0.0 && this.field_70170_p.func_184144_a(this, this.func_174813_aQ().func_72317_d(\u260332, -this.field_70138_W, 0.0)).isEmpty()) {
                \u260332 = \u260332 < 0.05 && \u260332 >= -0.05 ? 0.0 : (\u260332 > 0.0 ? (\u260332 -= 0.05) : (\u260332 += 0.05));
                d = \u260332;
            }
            while (\u260352 != 0.0 && this.field_70170_p.func_184144_a(this, this.func_174813_aQ().func_72317_d(0.0, -this.field_70138_W, \u260352)).isEmpty()) {
                \u260352 = \u260352 < 0.05 && \u260352 >= -0.05 ? 0.0 : (\u260352 > 0.0 ? (\u260352 -= 0.05) : (\u260352 += 0.05));
                \u2603 = \u260352;
            }
            while (\u260332 != 0.0 && \u260352 != 0.0 && this.field_70170_p.func_184144_a(this, this.func_174813_aQ().func_72317_d(\u260332, -this.field_70138_W, \u260352)).isEmpty()) {
                \u260332 = \u260332 < 0.05 && \u260332 >= -0.05 ? 0.0 : (\u260332 > 0.0 ? (\u260332 -= 0.05) : (\u260332 += 0.05));
                d = \u260332;
                \u260352 = \u260352 < 0.05 && \u260352 >= -0.05 ? 0.0 : (\u260352 > 0.0 ? (\u260352 -= 0.05) : (\u260352 += 0.05));
                \u2603 = \u260352;
            }
        }
        List<AxisAlignedBB> \u26036 = this.field_70170_p.func_184144_a(this, this.func_174813_aQ().func_72321_a(\u260332, \u260342, \u260352));
        AxisAlignedBB \u26037 = this.func_174813_aQ();
        if (\u260342 != 0.0) {
            n2 = \u26036.size();
            for (n = 0; n < n2; ++n) {
                \u260342 = \u26036.get(n).func_72323_b(this.func_174813_aQ(), \u260342);
            }
            this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0, \u260342, 0.0));
        }
        if (\u260332 != 0.0) {
            n2 = \u26036.size();
            for (n = 0; n < n2; ++n) {
                \u260332 = \u26036.get(n).func_72316_a(this.func_174813_aQ(), \u260332);
            }
            if (\u260332 != 0.0) {
                this.func_174826_a(this.func_174813_aQ().func_72317_d(\u260332, 0.0, 0.0));
            }
        }
        if (\u260352 != 0.0) {
            n2 = \u26036.size();
            for (n = 0; n < n2; ++n) {
                \u260352 = \u26036.get(n).func_72322_c(this.func_174813_aQ(), \u260352);
            }
            if (\u260352 != 0.0) {
                this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0, 0.0, \u260352));
            }
        }
        int n4 = n = this.field_70122_E || \u2603 != \u260342 && \u2603 < 0.0 ? 1 : 0;
        if (this.field_70138_W > 0.0f && n != 0 && (d != \u260332 || \u2603 != \u260352)) {
            double d3;
            double d4;
            double d5;
            double d6;
            double d7;
            double d8;
            double d9 = \u260332;
            \u2603 = \u260342;
            \u2603 = \u260352;
            \u26038 = this.func_174813_aQ();
            this.func_174826_a(\u26037);
            \u260332 = d;
            \u260342 = this.field_70138_W;
            \u260352 = \u2603;
            \u26039 = this.field_70170_p.func_184144_a(this, this.func_174813_aQ().func_72321_a(\u260332, \u260342, \u260352));
            AxisAlignedBB \u260310 = this.func_174813_aQ();
            AxisAlignedBB \u260311 = \u260310.func_72321_a(\u260332, 0.0, \u260352);
            d8 = \u260342;
            int \u260312 = \u26039.size();
            for (int i = 0; i < \u260312; ++i) {
                d8 = \u26039.get(i).func_72323_b(\u260311, d8);
            }
            \u260310 = \u260310.func_72317_d(0.0, d8, 0.0);
            d7 = \u260332;
            int \u260313 = \u26039.size();
            for (int i = 0; i < \u260313; ++i) {
                d7 = \u26039.get(i).func_72316_a(\u260310, d7);
            }
            \u260310 = \u260310.func_72317_d(d7, 0.0, 0.0);
            d6 = \u260352;
            int \u260314 = \u26039.size();
            for (int i = 0; i < \u260314; ++i) {
                d6 = ((AxisAlignedBB)\u26039.get(i)).func_72322_c(\u260310, d6);
            }
            \u260310 = \u260310.func_72317_d(0.0, 0.0, d6);
            AxisAlignedBB \u260315 = this.func_174813_aQ();
            d5 = \u260342;
            int \u260316 = \u26039.size();
            for (int i = 0; i < \u260316; ++i) {
                d5 = ((AxisAlignedBB)\u26039.get(i)).func_72323_b(\u260315, d5);
            }
            \u260315 = \u260315.func_72317_d(0.0, d5, 0.0);
            d4 = \u260332;
            int \u260317 = \u26039.size();
            for (int i = 0; i < \u260317; ++i) {
                d4 = ((AxisAlignedBB)\u26039.get(i)).func_72316_a(\u260315, d4);
            }
            \u260315 = \u260315.func_72317_d(d4, 0.0, 0.0);
            d3 = \u260352;
            int \u260318 = \u26039.size();
            for (int i = 0; i < \u260318; ++i) {
                d3 = ((AxisAlignedBB)\u26039.get(i)).func_72322_c(\u260315, d3);
            }
            \u260315 = \u260315.func_72317_d(0.0, 0.0, d3);
            \u2603 = d7 * d7 + d6 * d6;
            \u2603 = d4 * d4 + d3 * d3;
            if (\u2603 > \u2603) {
                \u260332 = d7;
                \u260352 = d6;
                \u260342 = -d8;
                this.func_174826_a(\u260310);
            } else {
                \u260332 = d4;
                \u260352 = d3;
                \u260342 = -d5;
                this.func_174826_a(\u260315);
            }
            int \u260319 = \u26039.size();
            for (int i = 0; i < \u260319; ++i) {
                \u260342 = ((AxisAlignedBB)\u26039.get(i)).func_72323_b(this.func_174813_aQ(), \u260342);
            }
            this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0, \u260342, 0.0));
            if (d9 * d9 + \u2603 * \u2603 >= \u260332 * \u260332 + \u260352 * \u260352) {
                \u260332 = d9;
                \u260342 = \u2603;
                \u260352 = \u2603;
                this.func_174826_a((AxisAlignedBB)\u26038);
            }
        }
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("rest");
        this.func_174829_m();
        this.field_70123_F = d != \u260332 || \u2603 != \u260352;
        this.field_70124_G = \u2603 != \u260342;
        this.field_70122_E = this.field_70124_G && \u2603 < 0.0;
        this.field_70132_H = this.field_70123_F || this.field_70124_G;
        int \u260320 = MathHelper.func_76128_c(this.field_70165_t);
        int \u260321 = MathHelper.func_76128_c(this.field_70163_u - (double)0.2f);
        int \u260322 = MathHelper.func_76128_c(this.field_70161_v);
        BlockPos \u260323 = new BlockPos(\u260320, \u260321, \u260322);
        Object \u260324 = this.field_70170_p.func_180495_p(\u260323);
        if (\u260324.func_185904_a() == Material.field_151579_a && ((\u26039 = (\u26038 = this.field_70170_p.func_180495_p((BlockPos)(\u260325 = \u260323.func_177977_b()))).func_177230_c()) instanceof BlockFence || \u26039 instanceof BlockWall || \u26039 instanceof BlockFenceGate)) {
            \u260324 = \u26038;
            \u260323 = \u260325;
        }
        this.func_184231_a(\u260342, this.field_70122_E, (IBlockState)\u260324, \u260323);
        if (d != \u260332) {
            this.field_70159_w = 0.0;
        }
        if (\u2603 != \u260352) {
            this.field_70179_y = 0.0;
        }
        Object \u260325 = \u260324.func_177230_c();
        if (\u2603 != \u260342) {
            ((Block)\u260325).func_176216_a(this.field_70170_p, this);
        }
        if (!(!this.func_70041_e_() || this.field_70122_E && this.func_70093_af() && this instanceof EntityPlayer || this.func_184218_aH())) {
            \u2603 = this.field_70165_t - d2;
            \u2603 = this.field_70163_u - \u2603;
            d8 = this.field_70161_v - \u2603;
            if (\u260325 != Blocks.field_150468_ap) {
                \u2603 = 0.0;
            }
            if (\u260325 != null && this.field_70122_E) {
                ((Block)\u260325).func_176199_a(this.field_70170_p, \u260323, this);
            }
            this.field_70140_Q = (float)((double)this.field_70140_Q + (double)MathHelper.func_76133_a(\u2603 * \u2603 + d8 * d8) * 0.6);
            this.field_82151_R = (float)((double)this.field_82151_R + (double)MathHelper.func_76133_a(\u2603 * \u2603 + \u2603 * \u2603 + d8 * d8) * 0.6);
            if (this.field_82151_R > (float)this.field_70150_b && \u260324.func_185904_a() != Material.field_151579_a) {
                this.field_70150_b = (int)this.field_82151_R + 1;
                if (this.func_70090_H()) {
                    Entity entity = this.func_184207_aI() && this.func_184179_bs() != null ? this.func_184179_bs() : this;
                    float \u260326 = entity == this ? 0.35f : 0.4f;
                    float \u260327 = MathHelper.func_76133_a(entity.field_70159_w * entity.field_70159_w * (double)0.2f + entity.field_70181_x * entity.field_70181_x + entity.field_70179_y * entity.field_70179_y * (double)0.2f) * \u260326;
                    if (\u260327 > 1.0f) {
                        \u260327 = 1.0f;
                    }
                    this.func_184185_a(this.func_184184_Z(), \u260327, 1.0f + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4f);
                } else {
                    this.func_180429_a(\u260323, (Block)\u260325);
                }
            } else if (this.field_82151_R > this.field_191959_ay && this.func_191957_ae() && \u260324.func_185904_a() == Material.field_151579_a) {
                this.field_191959_ay = this.func_191954_d(this.field_82151_R);
            }
        }
        try {
            this.func_145775_I();
        }
        catch (Throwable throwable) {
            \u26039 = CrashReport.func_85055_a(throwable, "Checking entity block collision");
            CrashReportCategory crashReportCategory = ((CrashReport)\u26039).func_85058_a("Entity being checked for collision");
            this.func_85029_a(crashReportCategory);
            throw new ReportedException((CrashReport)\u26039);
        }
        boolean bl = this.func_70026_G();
        if (this.field_70170_p.func_147470_e(this.func_174813_aQ().func_186664_h(0.001))) {
            this.func_70081_e(1);
            if (!bl) {
                ++this.field_190534_ay;
                if (this.field_190534_ay == 0) {
                    this.func_70015_d(8);
                }
            }
        } else if (this.field_190534_ay <= 0) {
            this.field_190534_ay = -this.func_190531_bD();
        }
        if (bl && this.func_70027_ad()) {
            this.func_184185_a(SoundEvents.field_187541_bC, 0.7f, 1.6f + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4f);
            this.field_190534_ay = -this.func_190531_bD();
        }
        this.field_70170_p.field_72984_F.func_76319_b();
    }

    public void func_174829_m() {
        AxisAlignedBB axisAlignedBB = this.func_174813_aQ();
        this.field_70165_t = (axisAlignedBB.field_72340_a + axisAlignedBB.field_72336_d) / 2.0;
        this.field_70163_u = axisAlignedBB.field_72338_b;
        this.field_70161_v = (axisAlignedBB.field_72339_c + axisAlignedBB.field_72334_f) / 2.0;
    }

    protected SoundEvent func_184184_Z() {
        return SoundEvents.field_187549_bG;
    }

    protected SoundEvent func_184181_aa() {
        return SoundEvents.field_187547_bF;
    }

    protected void func_145775_I() {
        AxisAlignedBB axisAlignedBB = this.func_174813_aQ();
        BlockPos.PooledMutableBlockPos \u26032 = BlockPos.PooledMutableBlockPos.func_185345_c(axisAlignedBB.field_72340_a + 0.001, axisAlignedBB.field_72338_b + 0.001, axisAlignedBB.field_72339_c + 0.001);
        BlockPos.PooledMutableBlockPos \u26033 = BlockPos.PooledMutableBlockPos.func_185345_c(axisAlignedBB.field_72336_d - 0.001, axisAlignedBB.field_72337_e - 0.001, axisAlignedBB.field_72334_f - 0.001);
        BlockPos.PooledMutableBlockPos \u26034 = BlockPos.PooledMutableBlockPos.func_185346_s();
        if (this.field_70170_p.func_175707_a(\u26032, \u26033)) {
            for (int i = \u26032.func_177958_n(); i <= \u26033.func_177958_n(); ++i) {
                for (\u2603 = \u26032.func_177956_o(); \u2603 <= \u26033.func_177956_o(); ++\u2603) {
                    for (\u2603 = \u26032.func_177952_p(); \u2603 <= \u26033.func_177952_p(); ++\u2603) {
                        \u26034.func_181079_c(i, \u2603, \u2603);
                        IBlockState iBlockState = this.field_70170_p.func_180495_p(\u26034);
                        try {
                            iBlockState.func_177230_c().func_180634_a(this.field_70170_p, \u26034, iBlockState, this);
                            this.func_191955_a(iBlockState);
                            continue;
                        }
                        catch (Throwable \u26035) {
                            CrashReport crashReport = CrashReport.func_85055_a(\u26035, "Colliding entity with block");
                            CrashReportCategory \u26036 = crashReport.func_85058_a("Block being collided with");
                            CrashReportCategory.func_175750_a(\u26036, \u26034, iBlockState);
                            throw new ReportedException(crashReport);
                        }
                    }
                }
            }
        }
        \u26032.func_185344_t();
        \u26033.func_185344_t();
        \u26034.func_185344_t();
    }

    protected void func_191955_a(IBlockState iBlockState) {
    }

    protected void func_180429_a(BlockPos blockPos, Block block) {
        SoundType soundType = block.func_185467_w();
        if (this.field_70170_p.func_180495_p(blockPos.func_177984_a()).func_177230_c() == Blocks.field_150431_aC) {
            soundType = Blocks.field_150431_aC.func_185467_w();
            this.func_184185_a(soundType.func_185844_d(), soundType.func_185843_a() * 0.15f, soundType.func_185847_b());
        } else if (!block.func_176223_P().func_185904_a().func_76224_d()) {
            this.func_184185_a(soundType.func_185844_d(), soundType.func_185843_a() * 0.15f, soundType.func_185847_b());
        }
    }

    protected float func_191954_d(float f) {
        return 0.0f;
    }

    protected boolean func_191957_ae() {
        return false;
    }

    public void func_184185_a(SoundEvent soundEvent, float f, float f2) {
        if (!this.func_174814_R()) {
            this.field_70170_p.func_184148_a(null, this.field_70165_t, this.field_70163_u, this.field_70161_v, soundEvent, this.func_184176_by(), f, f2);
        }
    }

    public boolean func_174814_R() {
        return this.field_70180_af.func_187225_a(field_184234_aB);
    }

    public void func_174810_b(boolean bl) {
        this.field_70180_af.func_187227_b(field_184234_aB, bl);
    }

    public boolean func_189652_ae() {
        return this.field_70180_af.func_187225_a(field_189655_aD);
    }

    public void func_189654_d(boolean bl) {
        this.field_70180_af.func_187227_b(field_189655_aD, bl);
    }

    protected boolean func_70041_e_() {
        return true;
    }

    protected void func_184231_a(double d, boolean bl, IBlockState iBlockState, BlockPos blockPos) {
        if (bl) {
            if (this.field_70143_R > 0.0f) {
                iBlockState.func_177230_c().func_180658_a(this.field_70170_p, blockPos, this, this.field_70143_R);
            }
            this.field_70143_R = 0.0f;
        } else if (d < 0.0) {
            this.field_70143_R = (float)((double)this.field_70143_R - d);
        }
    }

    @Nullable
    public AxisAlignedBB func_70046_E() {
        return null;
    }

    protected void func_70081_e(int n) {
        if (!this.field_70178_ae) {
            this.func_70097_a(DamageSource.field_76372_a, n);
        }
    }

    public final boolean func_70045_F() {
        return this.field_70178_ae;
    }

    public void func_180430_e(float f, float f2) {
        if (this.func_184207_aI()) {
            for (Entity entity : this.func_184188_bt()) {
                entity.func_180430_e(f, f2);
            }
        }
    }

    public boolean func_70026_G() {
        if (this.field_70171_ac) {
            return true;
        }
        BlockPos.PooledMutableBlockPos pooledMutableBlockPos = BlockPos.PooledMutableBlockPos.func_185345_c(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        if (!this.field_70170_p.func_175727_C(pooledMutableBlockPos) && !this.field_70170_p.func_175727_C(pooledMutableBlockPos.func_189532_c(this.field_70165_t, this.field_70163_u + (double)this.field_70131_O, this.field_70161_v))) {
            pooledMutableBlockPos.func_185344_t();
            return false;
        }
        pooledMutableBlockPos.func_185344_t();
        return true;
    }

    public boolean func_70090_H() {
        return this.field_70171_ac;
    }

    public boolean func_191953_am() {
        return this.field_70170_p.func_72918_a(this.func_174813_aQ().func_72314_b(0.0, -20.0, 0.0).func_186664_h(0.001), Material.field_151586_h, this);
    }

    public boolean func_70072_I() {
        if (this.func_184187_bx() instanceof EntityBoat) {
            this.field_70171_ac = false;
        } else if (this.field_70170_p.func_72918_a(this.func_174813_aQ().func_72314_b(0.0, -0.4f, 0.0).func_186664_h(0.001), Material.field_151586_h, this)) {
            if (!this.field_70171_ac && !this.field_70148_d) {
                this.func_71061_d_();
            }
            this.field_70143_R = 0.0f;
            this.field_70171_ac = true;
            this.func_70066_B();
        } else {
            this.field_70171_ac = false;
        }
        return this.field_70171_ac;
    }

    protected void func_71061_d_() {
        float f;
        Entity entity = this.func_184207_aI() && this.func_184179_bs() != null ? this.func_184179_bs() : this;
        float \u26032 = entity == this ? 0.2f : 0.9f;
        float \u26033 = MathHelper.func_76133_a(entity.field_70159_w * entity.field_70159_w * (double)0.2f + entity.field_70181_x * entity.field_70181_x + entity.field_70179_y * entity.field_70179_y * (double)0.2f) * \u26032;
        if (\u26033 > 1.0f) {
            \u26033 = 1.0f;
        }
        this.func_184185_a(this.func_184181_aa(), \u26033, 1.0f + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4f);
        float \u26034 = MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);
        int \u26035 = 0;
        while ((float)\u26035 < 1.0f + this.field_70130_N * 20.0f) {
            f = (this.field_70146_Z.nextFloat() * 2.0f - 1.0f) * this.field_70130_N;
            \u2603 = (this.field_70146_Z.nextFloat() * 2.0f - 1.0f) * this.field_70130_N;
            this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_BUBBLE, this.field_70165_t + (double)f, \u26034 + 1.0f, this.field_70161_v + (double)\u2603, this.field_70159_w, this.field_70181_x - (double)(this.field_70146_Z.nextFloat() * 0.2f), this.field_70179_y, new int[0]);
            ++\u26035;
        }
        \u26035 = 0;
        while ((float)\u26035 < 1.0f + this.field_70130_N * 20.0f) {
            f = (this.field_70146_Z.nextFloat() * 2.0f - 1.0f) * this.field_70130_N;
            \u2603 = (this.field_70146_Z.nextFloat() * 2.0f - 1.0f) * this.field_70130_N;
            this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_SPLASH, this.field_70165_t + (double)f, \u26034 + 1.0f, this.field_70161_v + (double)\u2603, this.field_70159_w, this.field_70181_x, this.field_70179_y, new int[0]);
            ++\u26035;
        }
    }

    public void func_174830_Y() {
        if (this.func_70051_ag() && !this.func_70090_H()) {
            this.func_174808_Z();
        }
    }

    protected void func_174808_Z() {
        int n = MathHelper.func_76128_c(this.field_70165_t);
        BlockPos \u26032 = new BlockPos(n, \u2603 = MathHelper.func_76128_c(this.field_70163_u - (double)0.2f), \u2603 = MathHelper.func_76128_c(this.field_70161_v));
        IBlockState \u26033 = this.field_70170_p.func_180495_p(\u26032);
        if (\u26033.func_185901_i() != EnumBlockRenderType.INVISIBLE) {
            this.field_70170_p.func_175688_a(EnumParticleTypes.BLOCK_CRACK, this.field_70165_t + ((double)this.field_70146_Z.nextFloat() - 0.5) * (double)this.field_70130_N, this.func_174813_aQ().field_72338_b + 0.1, this.field_70161_v + ((double)this.field_70146_Z.nextFloat() - 0.5) * (double)this.field_70130_N, -this.field_70159_w * 4.0, 1.5, -this.field_70179_y * 4.0, Block.func_176210_f(\u26033));
        }
    }

    public boolean func_70055_a(Material material) {
        if (this.func_184187_bx() instanceof EntityBoat) {
            return false;
        }
        double d = this.field_70163_u + (double)this.func_70047_e();
        BlockPos \u26032 = new BlockPos(this.field_70165_t, d, this.field_70161_v);
        IBlockState \u26033 = this.field_70170_p.func_180495_p(\u26032);
        if (\u26033.func_185904_a() == material) {
            float f = BlockLiquid.func_149801_b(\u26033.func_177230_c().func_176201_c(\u26033)) - 0.11111111f;
            \u2603 = (float)(\u26032.func_177956_o() + 1) - f;
            boolean bl = \u2603 = d < (double)\u2603;
            if (!\u2603 && this instanceof EntityPlayer) {
                return false;
            }
            return \u2603;
        }
        return false;
    }

    public boolean func_180799_ab() {
        return this.field_70170_p.func_72875_a(this.func_174813_aQ().func_72314_b(-0.1f, -0.4f, -0.1f), Material.field_151587_i);
    }

    public void func_191958_b(float f, float f2, float f3, float f4) {
        \u2603 = f * f + f2 * f2 + f3 * f3;
        if (\u2603 < 1.0E-4f) {
            return;
        }
        if ((\u2603 = MathHelper.func_76129_c(\u2603)) < 1.0f) {
            \u2603 = 1.0f;
        }
        \u2603 = f4 / \u2603;
        \u2603 = MathHelper.func_76126_a(this.field_70177_z * ((float)Math.PI / 180));
        \u2603 = MathHelper.func_76134_b(this.field_70177_z * ((float)Math.PI / 180));
        this.field_70159_w += (double)((f *= \u2603) * \u2603 - (f3 *= \u2603) * \u2603);
        this.field_70181_x += (double)(f2 *= \u2603);
        this.field_70179_y += (double)(f3 * \u2603 + f * \u2603);
    }

    public int func_70070_b() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(MathHelper.func_76128_c(this.field_70165_t), 0, MathHelper.func_76128_c(this.field_70161_v));
        if (this.field_70170_p.func_175667_e(mutableBlockPos)) {
            mutableBlockPos.func_185336_p(MathHelper.func_76128_c(this.field_70163_u + (double)this.func_70047_e()));
            return this.field_70170_p.func_175626_b(mutableBlockPos, 0);
        }
        return 0;
    }

    public float func_70013_c() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(MathHelper.func_76128_c(this.field_70165_t), 0, MathHelper.func_76128_c(this.field_70161_v));
        if (this.field_70170_p.func_175667_e(mutableBlockPos)) {
            mutableBlockPos.func_185336_p(MathHelper.func_76128_c(this.field_70163_u + (double)this.func_70047_e()));
            return this.field_70170_p.func_175724_o(mutableBlockPos);
        }
        return 0.0f;
    }

    public void func_70029_a(World world) {
        this.field_70170_p = world;
    }

    public void func_70080_a(double d, double d2, double d3, float f, float f2) {
        this.field_70165_t = MathHelper.func_151237_a(d, -3.0E7, 3.0E7);
        this.field_70163_u = d2;
        this.field_70161_v = MathHelper.func_151237_a(d3, -3.0E7, 3.0E7);
        this.field_70169_q = this.field_70165_t;
        this.field_70167_r = this.field_70163_u;
        this.field_70166_s = this.field_70161_v;
        f2 = MathHelper.func_76131_a(f2, -90.0f, 90.0f);
        this.field_70177_z = f;
        this.field_70125_A = f2;
        this.field_70126_B = this.field_70177_z;
        this.field_70127_C = this.field_70125_A;
        double d4 = this.field_70126_B - f;
        if (d4 < -180.0) {
            this.field_70126_B += 360.0f;
        }
        if (d4 >= 180.0) {
            this.field_70126_B -= 360.0f;
        }
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.func_70101_b(f, f2);
    }

    public void func_174828_a(BlockPos blockPos, float f, float f2) {
        this.func_70012_b((double)blockPos.func_177958_n() + 0.5, blockPos.func_177956_o(), (double)blockPos.func_177952_p() + 0.5, f, f2);
    }

    public void func_70012_b(double d, double d2, double d3, float f, float f2) {
        this.field_70165_t = d;
        this.field_70163_u = d2;
        this.field_70161_v = d3;
        this.field_70169_q = this.field_70165_t;
        this.field_70167_r = this.field_70163_u;
        this.field_70166_s = this.field_70161_v;
        this.field_70142_S = this.field_70165_t;
        this.field_70137_T = this.field_70163_u;
        this.field_70136_U = this.field_70161_v;
        this.field_70177_z = f;
        this.field_70125_A = f2;
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
    }

    public float func_70032_d(Entity entity) {
        float f = (float)(this.field_70165_t - entity.field_70165_t);
        \u2603 = (float)(this.field_70163_u - entity.field_70163_u);
        \u2603 = (float)(this.field_70161_v - entity.field_70161_v);
        return MathHelper.func_76129_c(f * f + \u2603 * \u2603 + \u2603 * \u2603);
    }

    public double func_70092_e(double d, double d2, double d3) {
        \u2603 = this.field_70165_t - d;
        \u2603 = this.field_70163_u - d2;
        \u2603 = this.field_70161_v - d3;
        return \u2603 * \u2603 + \u2603 * \u2603 + \u2603 * \u2603;
    }

    public double func_174818_b(BlockPos blockPos) {
        return blockPos.func_177954_c(this.field_70165_t, this.field_70163_u, this.field_70161_v);
    }

    public double func_174831_c(BlockPos blockPos) {
        return blockPos.func_177957_d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
    }

    public double func_70011_f(double d, double d2, double d3) {
        \u2603 = this.field_70165_t - d;
        \u2603 = this.field_70163_u - d2;
        \u2603 = this.field_70161_v - d3;
        return MathHelper.func_76133_a(\u2603 * \u2603 + \u2603 * \u2603 + \u2603 * \u2603);
    }

    public double func_70068_e(Entity entity) {
        double d = this.field_70165_t - entity.field_70165_t;
        \u2603 = this.field_70163_u - entity.field_70163_u;
        \u2603 = this.field_70161_v - entity.field_70161_v;
        return d * d + \u2603 * \u2603 + \u2603 * \u2603;
    }

    public void func_70100_b_(EntityPlayer entityPlayer) {
    }

    public void func_70108_f(Entity entity) {
        if (this.func_184223_x(entity)) {
            return;
        }
        if (entity.field_70145_X || this.field_70145_X) {
            return;
        }
        double d = entity.field_70165_t - this.field_70165_t;
        \u2603 = entity.field_70161_v - this.field_70161_v;
        \u2603 = MathHelper.func_76132_a(d, \u2603);
        if (\u2603 >= (double)0.01f) {
            \u2603 = MathHelper.func_76133_a(\u2603);
            d /= \u2603;
            \u2603 /= \u2603;
            \u2603 = 1.0 / \u2603;
            if (\u2603 > 1.0) {
                \u2603 = 1.0;
            }
            d *= \u2603;
            \u2603 *= \u2603;
            d *= (double)0.05f;
            \u2603 *= (double)0.05f;
            d *= (double)(1.0f - this.field_70144_Y);
            \u2603 *= (double)(1.0f - this.field_70144_Y);
            if (!this.func_184207_aI()) {
                this.func_70024_g(-d, 0.0, -\u2603);
            }
            if (!entity.func_184207_aI()) {
                entity.func_70024_g(d, 0.0, \u2603);
            }
        }
    }

    public void func_70024_g(double d, double d2, double d3) {
        this.field_70159_w += d;
        this.field_70181_x += d2;
        this.field_70179_y += d3;
        this.field_70160_al = true;
    }

    protected void func_70018_K() {
        this.field_70133_I = true;
    }

    public boolean func_70097_a(DamageSource damageSource, float f) {
        if (this.func_180431_b(damageSource)) {
            return false;
        }
        this.func_70018_K();
        return false;
    }

    public Vec3d func_70676_i(float f) {
        if (f == 1.0f) {
            return this.func_174806_f(this.field_70125_A, this.field_70177_z);
        }
        \u2603 = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * f;
        \u2603 = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * f;
        return this.func_174806_f(\u2603, \u2603);
    }

    protected final Vec3d func_174806_f(float f, float f2) {
        \u2603 = MathHelper.func_76134_b(-f2 * ((float)Math.PI / 180) - (float)Math.PI);
        \u2603 = MathHelper.func_76126_a(-f2 * ((float)Math.PI / 180) - (float)Math.PI);
        \u2603 = -MathHelper.func_76134_b(-f * ((float)Math.PI / 180));
        \u2603 = MathHelper.func_76126_a(-f * ((float)Math.PI / 180));
        return new Vec3d(\u2603 * \u2603, \u2603, \u2603 * \u2603);
    }

    public Vec3d func_174824_e(float f) {
        if (f == 1.0f) {
            return new Vec3d(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
        }
        double d = this.field_70169_q + (this.field_70165_t - this.field_70169_q) * (double)f;
        \u2603 = this.field_70167_r + (this.field_70163_u - this.field_70167_r) * (double)f + (double)this.func_70047_e();
        \u2603 = this.field_70166_s + (this.field_70161_v - this.field_70166_s) * (double)f;
        return new Vec3d(d, \u2603, \u2603);
    }

    @Nullable
    public RayTraceResult func_174822_a(double d, float f) {
        Vec3d vec3d = this.func_174824_e(f);
        \u2603 = this.func_70676_i(f);
        \u2603 = vec3d.func_72441_c(\u2603.field_72450_a * d, \u2603.field_72448_b * d, \u2603.field_72449_c * d);
        return this.field_70170_p.func_147447_a(vec3d, \u2603, false, false, true);
    }

    public boolean func_70067_L() {
        return false;
    }

    public boolean func_70104_M() {
        return false;
    }

    public void func_191956_a(Entity entity, int n, DamageSource damageSource) {
        if (entity instanceof EntityPlayerMP) {
            CriteriaTriggers.field_192123_c.func_192211_a((EntityPlayerMP)entity, this, damageSource);
        }
    }

    public boolean func_145770_h(double d, double d2, double d3) {
        \u2603 = this.field_70165_t - d;
        \u2603 = this.field_70163_u - d2;
        \u2603 = this.field_70161_v - d3;
        \u2603 = \u2603 * \u2603 + \u2603 * \u2603 + \u2603 * \u2603;
        return this.func_70112_a(\u2603);
    }

    public boolean func_70112_a(double d) {
        \u2603 = this.func_174813_aQ().func_72320_b();
        if (Double.isNaN(\u2603)) {
            \u2603 = 1.0;
        }
        return d < (\u2603 *= 64.0 * field_70155_l) * \u2603;
    }

    public boolean func_184198_c(NBTTagCompound nBTTagCompound) {
        String string = this.func_70022_Q();
        if (this.field_70128_L || string == null) {
            return false;
        }
        nBTTagCompound.func_74778_a("id", string);
        this.func_189511_e(nBTTagCompound);
        return true;
    }

    public boolean func_70039_c(NBTTagCompound nBTTagCompound) {
        String string = this.func_70022_Q();
        if (this.field_70128_L || string == null || this.func_184218_aH()) {
            return false;
        }
        nBTTagCompound.func_74778_a("id", string);
        this.func_189511_e(nBTTagCompound);
        return true;
    }

    public static void func_190533_a(DataFixer dataFixer) {
        dataFixer.func_188258_a(FixTypes.ENTITY, new IDataWalker(){

            @Override
            public NBTTagCompound func_188266_a(IDataFixer iDataFixer, NBTTagCompound nBTTagCompound2, int n) {
                NBTTagCompound nBTTagCompound2;
                if (nBTTagCompound2.func_150297_b("Passengers", 9)) {
                    NBTTagList nBTTagList = nBTTagCompound2.func_150295_c("Passengers", 10);
                    for (int i = 0; i < nBTTagList.func_74745_c(); ++i) {
                        nBTTagList.func_150304_a(i, iDataFixer.func_188251_a(FixTypes.ENTITY, nBTTagList.func_150305_b(i), n));
                    }
                }
                return nBTTagCompound2;
            }
        });
    }

    public NBTTagCompound func_189511_e(NBTTagCompound nBTTagCompound2) {
        try {
            nBTTagCompound2.func_74782_a("Pos", this.func_70087_a(this.field_70165_t, this.field_70163_u, this.field_70161_v));
            nBTTagCompound2.func_74782_a("Motion", this.func_70087_a(this.field_70159_w, this.field_70181_x, this.field_70179_y));
            nBTTagCompound2.func_74782_a("Rotation", this.func_70049_a(this.field_70177_z, this.field_70125_A));
            nBTTagCompound2.func_74776_a("FallDistance", this.field_70143_R);
            nBTTagCompound2.func_74777_a("Fire", (short)this.field_190534_ay);
            nBTTagCompound2.func_74777_a("Air", (short)this.func_70086_ai());
            nBTTagCompound2.func_74757_a("OnGround", this.field_70122_E);
            nBTTagCompound2.func_74768_a("Dimension", this.field_71093_bK);
            nBTTagCompound2.func_74757_a("Invulnerable", this.field_83001_bt);
            nBTTagCompound2.func_74768_a("PortalCooldown", this.field_71088_bW);
            nBTTagCompound2.func_186854_a("UUID", this.func_110124_au());
            if (this.func_145818_k_()) {
                nBTTagCompound2.func_74778_a("CustomName", this.func_95999_t());
            }
            if (this.func_174833_aM()) {
                nBTTagCompound2.func_74757_a("CustomNameVisible", this.func_174833_aM());
            }
            this.field_174837_as.func_179670_b(nBTTagCompound2);
            if (this.func_174814_R()) {
                nBTTagCompound2.func_74757_a("Silent", this.func_174814_R());
            }
            if (this.func_189652_ae()) {
                nBTTagCompound2.func_74757_a("NoGravity", this.func_189652_ae());
            }
            if (this.field_184238_ar) {
                nBTTagCompound2.func_74757_a("Glowing", this.field_184238_ar);
            }
            if (!this.field_184236_aF.isEmpty()) {
                NBTTagList nBTTagList = new NBTTagList();
                for (String string : this.field_184236_aF) {
                    nBTTagList.func_74742_a(new NBTTagString(string));
                }
                nBTTagCompound2.func_74782_a("Tags", nBTTagList);
            }
            this.func_70014_b(nBTTagCompound2);
            if (this.func_184207_aI()) {
                NBTTagList nBTTagList = new NBTTagList();
                for (Entity entity : this.func_184188_bt()) {
                    if (!entity.func_184198_c(\u2603 = new NBTTagCompound())) continue;
                    nBTTagList.func_74742_a(\u2603);
                }
                if (!nBTTagList.func_82582_d()) {
                    nBTTagCompound2.func_74782_a("Passengers", nBTTagList);
                }
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.func_85055_a(throwable, "Saving entity NBT");
            CrashReportCategory crashReportCategory = crashReport.func_85058_a("Entity being saved");
            this.func_85029_a(crashReportCategory);
            throw new ReportedException(crashReport);
        }
        return nBTTagCompound2;
    }

    public void func_70020_e(NBTTagCompound nBTTagCompound2) {
        try {
            NBTTagCompound nBTTagCompound2;
            NBTTagList nBTTagList = nBTTagCompound2.func_150295_c("Pos", 6);
            \u2603 = nBTTagCompound2.func_150295_c("Motion", 6);
            \u2603 = nBTTagCompound2.func_150295_c("Rotation", 5);
            this.field_70159_w = \u2603.func_150309_d(0);
            this.field_70181_x = \u2603.func_150309_d(1);
            this.field_70179_y = \u2603.func_150309_d(2);
            if (Math.abs(this.field_70159_w) > 10.0) {
                this.field_70159_w = 0.0;
            }
            if (Math.abs(this.field_70181_x) > 10.0) {
                this.field_70181_x = 0.0;
            }
            if (Math.abs(this.field_70179_y) > 10.0) {
                this.field_70179_y = 0.0;
            }
            this.field_70165_t = nBTTagList.func_150309_d(0);
            this.field_70163_u = nBTTagList.func_150309_d(1);
            this.field_70161_v = nBTTagList.func_150309_d(2);
            this.field_70142_S = this.field_70165_t;
            this.field_70137_T = this.field_70163_u;
            this.field_70136_U = this.field_70161_v;
            this.field_70169_q = this.field_70165_t;
            this.field_70167_r = this.field_70163_u;
            this.field_70166_s = this.field_70161_v;
            this.field_70177_z = \u2603.func_150308_e(0);
            this.field_70125_A = \u2603.func_150308_e(1);
            this.field_70126_B = this.field_70177_z;
            this.field_70127_C = this.field_70125_A;
            this.func_70034_d(this.field_70177_z);
            this.func_181013_g(this.field_70177_z);
            this.field_70143_R = nBTTagCompound2.func_74760_g("FallDistance");
            this.field_190534_ay = nBTTagCompound2.func_74765_d("Fire");
            this.func_70050_g(nBTTagCompound2.func_74765_d("Air"));
            this.field_70122_E = nBTTagCompound2.func_74767_n("OnGround");
            if (nBTTagCompound2.func_74764_b("Dimension")) {
                this.field_71093_bK = nBTTagCompound2.func_74762_e("Dimension");
            }
            this.field_83001_bt = nBTTagCompound2.func_74767_n("Invulnerable");
            this.field_71088_bW = nBTTagCompound2.func_74762_e("PortalCooldown");
            if (nBTTagCompound2.func_186855_b("UUID")) {
                this.field_96093_i = nBTTagCompound2.func_186857_a("UUID");
                this.field_189513_ar = this.field_96093_i.toString();
            }
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            this.func_70101_b(this.field_70177_z, this.field_70125_A);
            if (nBTTagCompound2.func_150297_b("CustomName", 8)) {
                this.func_96094_a(nBTTagCompound2.func_74779_i("CustomName"));
            }
            this.func_174805_g(nBTTagCompound2.func_74767_n("CustomNameVisible"));
            this.field_174837_as.func_179668_a(nBTTagCompound2);
            this.func_174810_b(nBTTagCompound2.func_74767_n("Silent"));
            this.func_189654_d(nBTTagCompound2.func_74767_n("NoGravity"));
            this.func_184195_f(nBTTagCompound2.func_74767_n("Glowing"));
            if (nBTTagCompound2.func_150297_b("Tags", 9)) {
                this.field_184236_aF.clear();
                \u2603 = nBTTagCompound2.func_150295_c("Tags", 8);
                int n = Math.min(\u2603.func_74745_c(), 1024);
                for (\u2603 = 0; \u2603 < n; ++\u2603) {
                    this.field_184236_aF.add(\u2603.func_150307_f(\u2603));
                }
            }
            this.func_70037_a(nBTTagCompound2);
            if (this.func_142008_O()) {
                this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.func_85055_a(throwable, "Loading entity NBT");
            CrashReportCategory \u26032 = crashReport.func_85058_a("Entity being loaded");
            this.func_85029_a(\u26032);
            throw new ReportedException(crashReport);
        }
    }

    protected boolean func_142008_O() {
        return true;
    }

    @Nullable
    protected final String func_70022_Q() {
        ResourceLocation resourceLocation = EntityList.func_191301_a(this);
        return resourceLocation == null ? null : resourceLocation.toString();
    }

    protected abstract void func_70037_a(NBTTagCompound var1);

    protected abstract void func_70014_b(NBTTagCompound var1);

    protected NBTTagList func_70087_a(double ... dArray) {
        NBTTagList nBTTagList = new NBTTagList();
        for (double d : dArray) {
            nBTTagList.func_74742_a(new NBTTagDouble(d));
        }
        return nBTTagList;
    }

    protected NBTTagList func_70049_a(float ... fArray) {
        NBTTagList nBTTagList = new NBTTagList();
        for (float f : fArray) {
            nBTTagList.func_74742_a(new NBTTagFloat(f));
        }
        return nBTTagList;
    }

    @Nullable
    public EntityItem func_145779_a(Item item, int n) {
        return this.func_145778_a(item, n, 0.0f);
    }

    @Nullable
    public EntityItem func_145778_a(Item item, int n, float f) {
        return this.func_70099_a(new ItemStack(item, n, 0), f);
    }

    @Nullable
    public EntityItem func_70099_a(ItemStack itemStack, float f) {
        if (itemStack.func_190926_b()) {
            return null;
        }
        EntityItem entityItem = new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u + (double)f, this.field_70161_v, itemStack);
        entityItem.func_174869_p();
        this.field_70170_p.func_72838_d(entityItem);
        return entityItem;
    }

    public boolean func_70089_S() {
        return !this.field_70128_L;
    }

    public boolean func_70094_T() {
        if (this.field_70145_X) {
            return false;
        }
        BlockPos.PooledMutableBlockPos pooledMutableBlockPos = BlockPos.PooledMutableBlockPos.func_185346_s();
        for (int i = 0; i < 8; ++i) {
            \u2603 = MathHelper.func_76128_c(this.field_70163_u + (double)(((float)((i >> 0) % 2) - 0.5f) * 0.1f) + (double)this.func_70047_e());
            \u2603 = MathHelper.func_76128_c(this.field_70165_t + (double)(((float)((i >> 1) % 2) - 0.5f) * this.field_70130_N * 0.8f));
            \u2603 = MathHelper.func_76128_c(this.field_70161_v + (double)(((float)((i >> 2) % 2) - 0.5f) * this.field_70130_N * 0.8f));
            if (pooledMutableBlockPos.func_177958_n() == \u2603 && pooledMutableBlockPos.func_177956_o() == \u2603 && pooledMutableBlockPos.func_177952_p() == \u2603) continue;
            pooledMutableBlockPos.func_181079_c(\u2603, \u2603, \u2603);
            if (!this.field_70170_p.func_180495_p(pooledMutableBlockPos).func_191058_s()) continue;
            pooledMutableBlockPos.func_185344_t();
            return true;
        }
        pooledMutableBlockPos.func_185344_t();
        return false;
    }

    public boolean func_184230_a(EntityPlayer entityPlayer, EnumHand enumHand) {
        return false;
    }

    @Nullable
    public AxisAlignedBB func_70114_g(Entity entity) {
        return null;
    }

    public void func_70098_U() {
        Entity entity = this.func_184187_bx();
        if (this.func_184218_aH() && entity.field_70128_L) {
            this.func_184210_p();
            return;
        }
        this.field_70159_w = 0.0;
        this.field_70181_x = 0.0;
        this.field_70179_y = 0.0;
        this.func_70071_h_();
        if (!this.func_184218_aH()) {
            return;
        }
        entity.func_184232_k(this);
    }

    public void func_184232_k(Entity entity) {
        if (!this.func_184196_w(entity)) {
            return;
        }
        entity.func_70107_b(this.field_70165_t, this.field_70163_u + this.func_70042_X() + entity.func_70033_W(), this.field_70161_v);
    }

    public void func_184190_l(Entity entity) {
    }

    public double func_70033_W() {
        return 0.0;
    }

    public double func_70042_X() {
        return (double)this.field_70131_O * 0.75;
    }

    public boolean func_184220_m(Entity entity) {
        return this.func_184205_a(entity, false);
    }

    public boolean func_184205_a(Entity entity, boolean bl) {
        Entity entity2 = entity;
        while (entity2.field_184239_as != null) {
            if (entity2.field_184239_as == this) {
                return false;
            }
            entity2 = entity2.field_184239_as;
        }
        if (!(bl || this.func_184228_n(entity) && entity.func_184219_q(this))) {
            return false;
        }
        if (this.func_184218_aH()) {
            this.func_184210_p();
        }
        this.field_184239_as = entity;
        this.field_184239_as.func_184200_o(this);
        return true;
    }

    protected boolean func_184228_n(Entity entity) {
        return this.field_184245_j <= 0;
    }

    public void func_184226_ay() {
        for (int i = this.field_184244_h.size() - 1; i >= 0; --i) {
            this.field_184244_h.get(i).func_184210_p();
        }
    }

    public void func_184210_p() {
        if (this.field_184239_as != null) {
            Entity entity = this.field_184239_as;
            this.field_184239_as = null;
            entity.func_184225_p(this);
        }
    }

    protected void func_184200_o(Entity entity) {
        if (entity.func_184187_bx() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        if (!this.field_70170_p.field_72995_K && entity instanceof EntityPlayer && !(this.func_184179_bs() instanceof EntityPlayer)) {
            this.field_184244_h.add(0, entity);
        } else {
            this.field_184244_h.add(entity);
        }
    }

    protected void func_184225_p(Entity entity) {
        if (entity.func_184187_bx() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        this.field_184244_h.remove(entity);
        entity.field_184245_j = 60;
    }

    protected boolean func_184219_q(Entity entity) {
        return this.func_184188_bt().size() < 1;
    }

    public void func_180426_a(double d, double d2, double d3, float f, float f2, int n, boolean bl) {
        this.func_70107_b(d, d2, d3);
        this.func_70101_b(f, f2);
    }

    public float func_70111_Y() {
        return 0.0f;
    }

    public Vec3d func_70040_Z() {
        return this.func_174806_f(this.field_70125_A, this.field_70177_z);
    }

    public Vec2f func_189653_aC() {
        return new Vec2f(this.field_70125_A, this.field_70177_z);
    }

    public Vec3d func_189651_aD() {
        return Vec3d.func_189984_a(this.func_189653_aC());
    }

    public void func_181015_d(BlockPos blockPos) {
        if (this.field_71088_bW > 0) {
            this.field_71088_bW = this.func_82147_ab();
            return;
        }
        if (!this.field_70170_p.field_72995_K && !blockPos.equals(this.field_181016_an)) {
            this.field_181016_an = new BlockPos(blockPos);
            BlockPattern.PatternHelper patternHelper = Blocks.field_150427_aO.func_181089_f(this.field_70170_p, this.field_181016_an);
            double \u26032 = patternHelper.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? (double)patternHelper.func_181117_a().func_177952_p() : (double)patternHelper.func_181117_a().func_177958_n();
            double \u26033 = patternHelper.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? this.field_70161_v : this.field_70165_t;
            \u26033 = Math.abs(MathHelper.func_181160_c(\u26033 - (double)(patternHelper.func_177669_b().func_176746_e().func_176743_c() == EnumFacing.AxisDirection.NEGATIVE ? 1 : 0), \u26032, \u26032 - (double)patternHelper.func_181118_d()));
            double \u26034 = MathHelper.func_181160_c(this.field_70163_u - 1.0, patternHelper.func_181117_a().func_177956_o(), patternHelper.func_181117_a().func_177956_o() - patternHelper.func_181119_e());
            this.field_181017_ao = new Vec3d(\u26033, \u26034, 0.0);
            this.field_181018_ap = patternHelper.func_177669_b();
        }
        this.field_71087_bX = true;
    }

    public int func_82147_ab() {
        return 300;
    }

    public void func_70016_h(double d, double d2, double d3) {
        this.field_70159_w = d;
        this.field_70181_x = d2;
        this.field_70179_y = d3;
    }

    public void func_70103_a(byte by) {
    }

    public void func_70057_ab() {
    }

    public Iterable<ItemStack> func_184214_aD() {
        return field_190535_b;
    }

    public Iterable<ItemStack> func_184193_aE() {
        return field_190535_b;
    }

    public Iterable<ItemStack> func_184209_aF() {
        return Iterables.concat(this.func_184214_aD(), this.func_184193_aE());
    }

    public void func_184201_a(EntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {
    }

    public boolean func_70027_ad() {
        boolean bl = this.field_70170_p != null && this.field_70170_p.field_72995_K;
        return !this.field_70178_ae && (this.field_190534_ay > 0 || bl && this.func_70083_f(0));
    }

    public boolean func_184218_aH() {
        return this.func_184187_bx() != null;
    }

    public boolean func_184207_aI() {
        return !this.func_184188_bt().isEmpty();
    }

    public boolean func_70093_af() {
        return this.func_70083_f(1);
    }

    public void func_70095_a(boolean bl) {
        this.func_70052_a(1, bl);
    }

    public boolean func_70051_ag() {
        return this.func_70083_f(3);
    }

    public void func_70031_b(boolean bl) {
        this.func_70052_a(3, bl);
    }

    public boolean func_184202_aL() {
        return this.field_184238_ar || this.field_70170_p.field_72995_K && this.func_70083_f(6);
    }

    public void func_184195_f(boolean bl) {
        this.field_184238_ar = bl;
        if (!this.field_70170_p.field_72995_K) {
            this.func_70052_a(6, this.field_184238_ar);
        }
    }

    public boolean func_82150_aj() {
        return this.func_70083_f(5);
    }

    public boolean func_98034_c(EntityPlayer entityPlayer) {
        if (entityPlayer.func_175149_v()) {
            return false;
        }
        Team team = this.func_96124_cp();
        if (team != null && entityPlayer != null && entityPlayer.func_96124_cp() == team && team.func_98297_h()) {
            return false;
        }
        return this.func_82150_aj();
    }

    @Nullable
    public Team func_96124_cp() {
        return this.field_70170_p.func_96441_U().func_96509_i(this.func_189512_bd());
    }

    public boolean func_184191_r(Entity entity) {
        return this.func_184194_a(entity.func_96124_cp());
    }

    public boolean func_184194_a(Team team) {
        if (this.func_96124_cp() != null) {
            return this.func_96124_cp().func_142054_a(team);
        }
        return false;
    }

    public void func_82142_c(boolean bl) {
        this.func_70052_a(5, bl);
    }

    protected boolean func_70083_f(int n) {
        return (this.field_70180_af.func_187225_a(field_184240_ax) & 1 << n) != 0;
    }

    protected void func_70052_a(int n, boolean bl) {
        byte by = this.field_70180_af.func_187225_a(field_184240_ax);
        if (bl) {
            this.field_70180_af.func_187227_b(field_184240_ax, (byte)(by | 1 << n));
        } else {
            this.field_70180_af.func_187227_b(field_184240_ax, (byte)(by & ~(1 << n)));
        }
    }

    public int func_70086_ai() {
        return this.field_70180_af.func_187225_a(field_184241_ay);
    }

    public void func_70050_g(int n) {
        this.field_70180_af.func_187227_b(field_184241_ay, n);
    }

    public void func_70077_a(EntityLightningBolt entityLightningBolt) {
        this.func_70097_a(DamageSource.field_180137_b, 5.0f);
        ++this.field_190534_ay;
        if (this.field_190534_ay == 0) {
            this.func_70015_d(8);
        }
    }

    public void func_70074_a(EntityLivingBase entityLivingBase) {
    }

    protected boolean func_145771_j(double d, double d2, double d3) {
        BlockPos blockPos = new BlockPos(d, d2, d3);
        double \u26032 = d - (double)blockPos.func_177958_n();
        double \u26033 = d2 - (double)blockPos.func_177956_o();
        double \u26034 = d3 - (double)blockPos.func_177952_p();
        if (!this.field_70170_p.func_184143_b(this.func_174813_aQ())) {
            return false;
        }
        EnumFacing \u26035 = EnumFacing.UP;
        double \u26036 = Double.MAX_VALUE;
        if (!this.field_70170_p.func_175665_u(blockPos.func_177976_e()) && \u26032 < \u26036) {
            \u26036 = \u26032;
            \u26035 = EnumFacing.WEST;
        }
        if (!this.field_70170_p.func_175665_u(blockPos.func_177974_f()) && 1.0 - \u26032 < \u26036) {
            \u26036 = 1.0 - \u26032;
            \u26035 = EnumFacing.EAST;
        }
        if (!this.field_70170_p.func_175665_u(blockPos.func_177978_c()) && \u26034 < \u26036) {
            \u26036 = \u26034;
            \u26035 = EnumFacing.NORTH;
        }
        if (!this.field_70170_p.func_175665_u(blockPos.func_177968_d()) && 1.0 - \u26034 < \u26036) {
            \u26036 = 1.0 - \u26034;
            \u26035 = EnumFacing.SOUTH;
        }
        if (!this.field_70170_p.func_175665_u(blockPos.func_177984_a()) && 1.0 - \u26033 < \u26036) {
            \u26036 = 1.0 - \u26033;
            \u26035 = EnumFacing.UP;
        }
        float \u26037 = this.field_70146_Z.nextFloat() * 0.2f + 0.1f;
        float \u26038 = \u26035.func_176743_c().func_179524_a();
        if (\u26035.func_176740_k() == EnumFacing.Axis.X) {
            this.field_70159_w = \u26038 * \u26037;
            this.field_70181_x *= 0.75;
            this.field_70179_y *= 0.75;
        } else if (\u26035.func_176740_k() == EnumFacing.Axis.Y) {
            this.field_70159_w *= 0.75;
            this.field_70181_x = \u26038 * \u26037;
            this.field_70179_y *= 0.75;
        } else if (\u26035.func_176740_k() == EnumFacing.Axis.Z) {
            this.field_70159_w *= 0.75;
            this.field_70181_x *= 0.75;
            this.field_70179_y = \u26038 * \u26037;
        }
        return true;
    }

    public void func_70110_aj() {
        this.field_70134_J = true;
        this.field_70143_R = 0.0f;
    }

    @Override
    public String func_70005_c_() {
        if (this.func_145818_k_()) {
            return this.func_95999_t();
        }
        String string = EntityList.func_75621_b(this);
        if (string == null) {
            string = "generic";
        }
        return I18n.func_74838_a("entity." + string + ".name");
    }

    @Nullable
    public Entity[] func_70021_al() {
        return null;
    }

    public boolean func_70028_i(Entity entity) {
        return this == entity;
    }

    public float func_70079_am() {
        return 0.0f;
    }

    public void func_70034_d(float f) {
    }

    public void func_181013_g(float f) {
    }

    public boolean func_70075_an() {
        return true;
    }

    public boolean func_85031_j(Entity entity) {
        return false;
    }

    public String toString() {
        return String.format("%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.func_70005_c_(), this.field_145783_c, this.field_70170_p == null ? "~NULL~" : this.field_70170_p.func_72912_H().func_76065_j(), this.field_70165_t, this.field_70163_u, this.field_70161_v);
    }

    public boolean func_180431_b(DamageSource damageSource) {
        return this.field_83001_bt && damageSource != DamageSource.field_76380_i && !damageSource.func_180136_u();
    }

    public boolean func_190530_aW() {
        return this.field_83001_bt;
    }

    public void func_184224_h(boolean bl) {
        this.field_83001_bt = bl;
    }

    public void func_82149_j(Entity entity) {
        this.func_70012_b(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, entity.field_70177_z, entity.field_70125_A);
    }

    private void func_180432_n(Entity entity) {
        NBTTagCompound nBTTagCompound = entity.func_189511_e(new NBTTagCompound());
        nBTTagCompound.func_82580_o("Dimension");
        this.func_70020_e(nBTTagCompound);
        this.field_71088_bW = entity.field_71088_bW;
        this.field_181016_an = entity.field_181016_an;
        this.field_181017_ao = entity.field_181017_ao;
        this.field_181018_ap = entity.field_181018_ap;
    }

    @Nullable
    public Entity func_184204_a(int n) {
        BlockPos \u26037;
        if (this.field_70170_p.field_72995_K || this.field_70128_L) {
            return null;
        }
        this.field_70170_p.field_72984_F.func_76320_a("changeDimension");
        MinecraftServer minecraftServer = this.func_184102_h();
        int \u26032 = this.field_71093_bK;
        WorldServer \u26033 = minecraftServer.func_71218_a(\u26032);
        WorldServer \u26034 = minecraftServer.func_71218_a(n);
        this.field_71093_bK = n;
        if (\u26032 == 1 && n == 1) {
            \u26034 = minecraftServer.func_71218_a(0);
            this.field_71093_bK = 0;
        }
        this.field_70170_p.func_72900_e(this);
        this.field_70128_L = false;
        this.field_70170_p.field_72984_F.func_76320_a("reposition");
        if (n == 1) {
            \u26037 = \u26034.func_180504_m();
        } else {
            double d = this.field_70165_t;
            \u2603 = this.field_70161_v;
            \u2603 = 8.0;
            if (n == -1) {
                d = MathHelper.func_151237_a(d / 8.0, \u26034.func_175723_af().func_177726_b() + 16.0, \u26034.func_175723_af().func_177728_d() - 16.0);
                \u2603 = MathHelper.func_151237_a(\u2603 / 8.0, \u26034.func_175723_af().func_177736_c() + 16.0, \u26034.func_175723_af().func_177733_e() - 16.0);
            } else if (n == 0) {
                d = MathHelper.func_151237_a(d * 8.0, \u26034.func_175723_af().func_177726_b() + 16.0, \u26034.func_175723_af().func_177728_d() - 16.0);
                \u2603 = MathHelper.func_151237_a(\u2603 * 8.0, \u26034.func_175723_af().func_177736_c() + 16.0, \u26034.func_175723_af().func_177733_e() - 16.0);
            }
            d = MathHelper.func_76125_a((int)d, -29999872, 29999872);
            \u2603 = MathHelper.func_76125_a((int)\u2603, -29999872, 29999872);
            float \u26035 = this.field_70177_z;
            this.func_70012_b(d, this.field_70163_u, \u2603, 90.0f, 0.0f);
            Teleporter \u26036 = \u26034.func_85176_s();
            \u26036.func_180620_b(this, \u26035);
            \u26037 = new BlockPos(this);
        }
        \u26033.func_72866_a(this, false);
        this.field_70170_p.field_72984_F.func_76318_c("reloading");
        Entity entity = EntityList.func_191304_a(this.getClass(), \u26034);
        if (entity != null) {
            entity.func_180432_n(this);
            if (\u26032 == 1 && n == 1) {
                BlockPos blockPos = \u26034.func_175672_r(\u26034.func_175694_M());
                entity.func_174828_a(blockPos, entity.field_70177_z, entity.field_70125_A);
            } else {
                entity.func_174828_a(\u26037, entity.field_70177_z, entity.field_70125_A);
            }
            boolean \u26038 = entity.field_98038_p;
            entity.field_98038_p = true;
            \u26034.func_72838_d(entity);
            entity.field_98038_p = \u26038;
            \u26034.func_72866_a(entity, false);
        }
        this.field_70128_L = true;
        this.field_70170_p.field_72984_F.func_76319_b();
        \u26033.func_82742_i();
        \u26034.func_82742_i();
        this.field_70170_p.field_72984_F.func_76319_b();
        return entity;
    }

    public boolean func_184222_aU() {
        return true;
    }

    public float func_180428_a(Explosion explosion, World world, BlockPos blockPos, IBlockState iBlockState) {
        return iBlockState.func_177230_c().func_149638_a(this);
    }

    public boolean func_174816_a(Explosion explosion, World world, BlockPos blockPos, IBlockState iBlockState, float f) {
        return true;
    }

    public int func_82143_as() {
        return 3;
    }

    public Vec3d func_181014_aG() {
        return this.field_181017_ao;
    }

    public EnumFacing func_181012_aH() {
        return this.field_181018_ap;
    }

    public boolean func_145773_az() {
        return false;
    }

    public void func_85029_a(CrashReportCategory crashReportCategory) {
        crashReportCategory.func_189529_a("Entity Type", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return EntityList.func_191301_a(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
            }

            @Override
            public /* synthetic */ Object call() throws Exception {
                return this.call();
            }
        });
        crashReportCategory.func_71507_a("Entity ID", this.field_145783_c);
        crashReportCategory.func_189529_a("Entity Name", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return Entity.this.func_70005_c_();
            }

            @Override
            public /* synthetic */ Object call() throws Exception {
                return this.call();
            }
        });
        crashReportCategory.func_71507_a("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.field_70165_t, this.field_70163_u, this.field_70161_v));
        crashReportCategory.func_71507_a("Entity's Block location", CrashReportCategory.func_184876_a(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)));
        crashReportCategory.func_71507_a("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.field_70159_w, this.field_70181_x, this.field_70179_y));
        crashReportCategory.func_189529_a("Entity's Passengers", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return Entity.this.func_184188_bt().toString();
            }

            @Override
            public /* synthetic */ Object call() throws Exception {
                return this.call();
            }
        });
        crashReportCategory.func_189529_a("Entity's Vehicle", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return Entity.this.func_184187_bx().toString();
            }

            @Override
            public /* synthetic */ Object call() throws Exception {
                return this.call();
            }
        });
    }

    public boolean func_90999_ad() {
        return this.func_70027_ad();
    }

    public void func_184221_a(UUID uUID) {
        this.field_96093_i = uUID;
        this.field_189513_ar = this.field_96093_i.toString();
    }

    public UUID func_110124_au() {
        return this.field_96093_i;
    }

    public String func_189512_bd() {
        return this.field_189513_ar;
    }

    public boolean func_96092_aw() {
        return true;
    }

    public static double func_184183_bd() {
        return field_70155_l;
    }

    public static void func_184227_b(double d) {
        field_70155_l = d;
    }

    @Override
    public ITextComponent func_145748_c_() {
        TextComponentString textComponentString = new TextComponentString(ScorePlayerTeam.func_96667_a(this.func_96124_cp(), this.func_70005_c_()));
        textComponentString.func_150256_b().func_150209_a(this.func_174823_aP());
        textComponentString.func_150256_b().func_179989_a(this.func_189512_bd());
        return textComponentString;
    }

    public void func_96094_a(String string) {
        this.field_70180_af.func_187227_b(field_184242_az, string);
    }

    public String func_95999_t() {
        return this.field_70180_af.func_187225_a(field_184242_az);
    }

    public boolean func_145818_k_() {
        return !this.field_70180_af.func_187225_a(field_184242_az).isEmpty();
    }

    public void func_174805_g(boolean bl) {
        this.field_70180_af.func_187227_b(field_184233_aA, bl);
    }

    public boolean func_174833_aM() {
        return this.field_70180_af.func_187225_a(field_184233_aA);
    }

    public void func_70634_a(double d, double d2, double d3) {
        this.field_184237_aG = true;
        this.func_70012_b(d, d2, d3, this.field_70177_z, this.field_70125_A);
        this.field_70170_p.func_72866_a(this, false);
    }

    public boolean func_94059_bO() {
        return this.func_174833_aM();
    }

    public void func_184206_a(DataParameter<?> dataParameter) {
    }

    public EnumFacing func_174811_aO() {
        return EnumFacing.func_176731_b(MathHelper.func_76128_c((double)(this.field_70177_z * 4.0f / 360.0f) + 0.5) & 3);
    }

    public EnumFacing func_184172_bi() {
        return this.func_174811_aO();
    }

    protected HoverEvent func_174823_aP() {
        NBTTagCompound nBTTagCompound = new NBTTagCompound();
        ResourceLocation \u26032 = EntityList.func_191301_a(this);
        nBTTagCompound.func_74778_a("id", this.func_189512_bd());
        if (\u26032 != null) {
            nBTTagCompound.func_74778_a("type", \u26032.toString());
        }
        nBTTagCompound.func_74778_a("name", this.func_70005_c_());
        return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new TextComponentString(nBTTagCompound.toString()));
    }

    public boolean func_174827_a(EntityPlayerMP entityPlayerMP) {
        return true;
    }

    public AxisAlignedBB func_174813_aQ() {
        return this.field_70121_D;
    }

    public AxisAlignedBB func_184177_bl() {
        return this.func_174813_aQ();
    }

    public void func_174826_a(AxisAlignedBB axisAlignedBB) {
        this.field_70121_D = axisAlignedBB;
    }

    public float func_70047_e() {
        return this.field_70131_O * 0.85f;
    }

    public boolean func_174832_aS() {
        return this.field_174835_g;
    }

    public void func_174821_h(boolean bl) {
        this.field_174835_g = bl;
    }

    public boolean func_174820_d(int n, ItemStack itemStack) {
        return false;
    }

    @Override
    public void func_145747_a(ITextComponent iTextComponent) {
    }

    @Override
    public boolean func_70003_b(int n, String string) {
        return true;
    }

    @Override
    public BlockPos func_180425_c() {
        return new BlockPos(this.field_70165_t, this.field_70163_u + 0.5, this.field_70161_v);
    }

    @Override
    public Vec3d func_174791_d() {
        return new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
    }

    @Override
    public World func_130014_f_() {
        return this.field_70170_p;
    }

    @Override
    public Entity func_174793_f() {
        return this;
    }

    @Override
    public boolean func_174792_t_() {
        return false;
    }

    @Override
    public void func_174794_a(CommandResultStats.Type type, int n) {
        if (this.field_70170_p != null && !this.field_70170_p.field_72995_K) {
            this.field_174837_as.func_184932_a(this.field_70170_p.func_73046_m(), this, type, n);
        }
    }

    @Override
    @Nullable
    public MinecraftServer func_184102_h() {
        return this.field_70170_p.func_73046_m();
    }

    public CommandResultStats func_174807_aT() {
        return this.field_174837_as;
    }

    public void func_174817_o(Entity entity) {
        this.field_174837_as.func_179671_a(entity.func_174807_aT());
    }

    public EnumActionResult func_184199_a(EntityPlayer entityPlayer, Vec3d vec3d, EnumHand enumHand) {
        return EnumActionResult.PASS;
    }

    public boolean func_180427_aV() {
        return false;
    }

    protected void func_174815_a(EntityLivingBase entityLivingBase, Entity entity) {
        if (entity instanceof EntityLivingBase) {
            EnchantmentHelper.func_151384_a((EntityLivingBase)entity, entityLivingBase);
        }
        EnchantmentHelper.func_151385_b(entityLivingBase, entity);
    }

    public void func_184178_b(EntityPlayerMP entityPlayerMP) {
    }

    public void func_184203_c(EntityPlayerMP entityPlayerMP) {
    }

    public float func_184229_a(Rotation rotation) {
        float f = MathHelper.func_76142_g(this.field_70177_z);
        switch (rotation) {
            case CLOCKWISE_180: {
                return f + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return f + 270.0f;
            }
            case CLOCKWISE_90: {
                return f + 90.0f;
            }
        }
        return f;
    }

    public float func_184217_a(Mirror mirror) {
        float f = MathHelper.func_76142_g(this.field_70177_z);
        switch (mirror) {
            case LEFT_RIGHT: {
                return -f;
            }
            case FRONT_BACK: {
                return 180.0f - f;
            }
        }
        return f;
    }

    public boolean func_184213_bq() {
        return false;
    }

    public boolean func_184189_br() {
        boolean bl = this.field_184237_aG;
        this.field_184237_aG = false;
        return bl;
    }

    @Nullable
    public Entity func_184179_bs() {
        return null;
    }

    public List<Entity> func_184188_bt() {
        if (this.field_184244_h.isEmpty()) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(this.field_184244_h);
    }

    public boolean func_184196_w(Entity entity) {
        for (Entity entity2 : this.func_184188_bt()) {
            if (!entity2.equals(entity)) continue;
            return true;
        }
        return false;
    }

    public Collection<Entity> func_184182_bu() {
        HashSet hashSet = Sets.newHashSet();
        this.func_184175_a(Entity.class, hashSet);
        return hashSet;
    }

    public <T extends Entity> Collection<T> func_184180_b(Class<T> clazz) {
        HashSet hashSet = Sets.newHashSet();
        this.func_184175_a(clazz, hashSet);
        return hashSet;
    }

    private <T extends Entity> void func_184175_a(Class<T> clazz, Set<T> set) {
        for (Entity entity : this.func_184188_bt()) {
            if (clazz.isAssignableFrom(entity.getClass())) {
                set.add(entity);
            }
            entity.func_184175_a(clazz, set);
        }
    }

    public Entity func_184208_bv() {
        Entity entity = this;
        while (entity.func_184218_aH()) {
            entity = entity.func_184187_bx();
        }
        return entity;
    }

    public boolean func_184223_x(Entity entity) {
        return this.func_184208_bv() == entity.func_184208_bv();
    }

    public boolean func_184215_y(Entity entity) {
        for (Entity entity2 : this.func_184188_bt()) {
            if (entity2.equals(entity)) {
                return true;
            }
            if (!entity2.func_184215_y(entity)) continue;
            return true;
        }
        return false;
    }

    public boolean func_184186_bw() {
        Entity entity = this.func_184179_bs();
        if (entity instanceof EntityPlayer) {
            return ((EntityPlayer)entity).func_175144_cb();
        }
        return !this.field_70170_p.field_72995_K;
    }

    @Nullable
    public Entity func_184187_bx() {
        return this.field_184239_as;
    }

    public EnumPushReaction func_184192_z() {
        return EnumPushReaction.NORMAL;
    }

    public SoundCategory func_184176_by() {
        return SoundCategory.NEUTRAL;
    }

    protected int func_190531_bD() {
        return 1;
    }

    static {
        field_184240_ax = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187191_a);
        field_184241_ay = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187192_b);
        field_184242_az = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187194_d);
        field_184233_aA = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187198_h);
        field_184234_aB = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187198_h);
        field_189655_aD = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187198_h);
    }
}

