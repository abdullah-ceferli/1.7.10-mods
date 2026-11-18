/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.player;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityPlayer
extends EntityLivingBase {
    private static final DataParameter<Float> field_184829_a = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_187193_c);
    private static final DataParameter<Integer> field_184830_b = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_187192_b);
    protected static final DataParameter<Byte> field_184827_bp = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_187191_a);
    protected static final DataParameter<Byte> field_184828_bq = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_187191_a);
    protected static final DataParameter<NBTTagCompound> field_192032_bt = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_192734_n);
    protected static final DataParameter<NBTTagCompound> field_192033_bu = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_192734_n);
    public InventoryPlayer field_71071_by = new InventoryPlayer(this);
    protected InventoryEnderChest field_71078_a = new InventoryEnderChest();
    public Container field_71069_bz;
    public Container field_71070_bA;
    protected FoodStats field_71100_bB = new FoodStats();
    protected int field_71101_bC;
    public float field_71107_bF;
    public float field_71109_bG;
    public int field_71090_bL;
    public double field_71091_bM;
    public double field_71096_bN;
    public double field_71097_bO;
    public double field_71094_bP;
    public double field_71095_bQ;
    public double field_71085_bR;
    protected boolean field_71083_bS;
    public BlockPos field_71081_bT;
    private int field_71076_b;
    public float field_71079_bU;
    public float field_71082_cx;
    public float field_71089_bV;
    private BlockPos field_71077_c;
    private boolean field_82248_d;
    public PlayerCapabilities field_71075_bZ = new PlayerCapabilities();
    public int field_71068_ca;
    public int field_71067_cb;
    public float field_71106_cc;
    protected int field_175152_f;
    protected float field_71102_ce = 0.02f;
    private int field_82249_h;
    private final GameProfile field_146106_i;
    private boolean field_175153_bG;
    private ItemStack field_184831_bT = ItemStack.field_190927_a;
    private final CooldownTracker field_184832_bU = this.func_184815_l();
    @Nullable
    public EntityFishHook field_71104_cf;

    protected CooldownTracker func_184815_l() {
        return new CooldownTracker();
    }

    public EntityPlayer(World world, GameProfile gameProfile) {
        super(world);
        this.func_184221_a(EntityPlayer.func_146094_a(gameProfile));
        this.field_146106_i = gameProfile;
        this.field_71070_bA = this.field_71069_bz = new ContainerPlayer(this.field_71071_by, !world.field_72995_K, this);
        BlockPos blockPos = world.func_175694_M();
        this.func_70012_b((double)blockPos.func_177958_n() + 0.5, blockPos.func_177956_o() + 1, (double)blockPos.func_177952_p() + 0.5, 0.0f, 0.0f);
        this.field_70741_aB = 180.0f;
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0);
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.1f);
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_188790_f);
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_188792_h);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_187214_a(field_184829_a, Float.valueOf(0.0f));
        this.field_70180_af.func_187214_a(field_184830_b, 0);
        this.field_70180_af.func_187214_a(field_184827_bp, (byte)0);
        this.field_70180_af.func_187214_a(field_184828_bq, (byte)1);
        this.field_70180_af.func_187214_a(field_192032_bt, new NBTTagCompound());
        this.field_70180_af.func_187214_a(field_192033_bu, new NBTTagCompound());
    }

    @Override
    public void func_70071_h_() {
        this.field_70145_X = this.func_175149_v();
        if (this.func_175149_v()) {
            this.field_70122_E = false;
        }
        if (this.field_71090_bL > 0) {
            --this.field_71090_bL;
        }
        if (this.func_70608_bn()) {
            ++this.field_71076_b;
            if (this.field_71076_b > 100) {
                this.field_71076_b = 100;
            }
            if (!this.field_70170_p.field_72995_K) {
                if (!this.func_175143_p()) {
                    this.func_70999_a(true, true, false);
                } else if (this.field_70170_p.func_72935_r()) {
                    this.func_70999_a(false, true, true);
                }
            }
        } else if (this.field_71076_b > 0) {
            ++this.field_71076_b;
            if (this.field_71076_b >= 110) {
                this.field_71076_b = 0;
            }
        }
        super.func_70071_h_();
        if (!this.field_70170_p.field_72995_K && this.field_71070_bA != null && !this.field_71070_bA.func_75145_c(this)) {
            this.func_71053_j();
            this.field_71070_bA = this.field_71069_bz;
        }
        if (this.func_70027_ad() && this.field_71075_bZ.field_75102_a) {
            this.func_70066_B();
        }
        this.func_184820_o();
        if (!this.field_70170_p.field_72995_K) {
            this.field_71100_bB.func_75118_a(this);
            this.func_71029_a(StatList.field_188097_g);
            if (this.func_70089_S()) {
                this.func_71029_a(StatList.field_188098_h);
            }
            if (this.func_70093_af()) {
                this.func_71029_a(StatList.field_188099_i);
            }
        }
        int n = 29999999;
        double \u26032 = MathHelper.func_151237_a(this.field_70165_t, -2.9999999E7, 2.9999999E7);
        double \u26033 = MathHelper.func_151237_a(this.field_70161_v, -2.9999999E7, 2.9999999E7);
        if (\u26032 != this.field_70165_t || \u26033 != this.field_70161_v) {
            this.func_70107_b(\u26032, this.field_70163_u, \u26033);
        }
        ++this.field_184617_aD;
        ItemStack \u26034 = this.func_184614_ca();
        if (!ItemStack.func_77989_b(this.field_184831_bT, \u26034)) {
            if (!ItemStack.func_185132_d(this.field_184831_bT, \u26034)) {
                this.func_184821_cY();
            }
            this.field_184831_bT = \u26034.func_190926_b() ? ItemStack.field_190927_a : \u26034.func_77946_l();
        }
        this.field_184832_bU.func_185144_a();
        this.func_184808_cD();
    }

    private void func_184820_o() {
        this.field_71091_bM = this.field_71094_bP;
        this.field_71096_bN = this.field_71095_bQ;
        this.field_71097_bO = this.field_71085_bR;
        double d = this.field_70165_t - this.field_71094_bP;
        \u2603 = this.field_70163_u - this.field_71095_bQ;
        \u2603 = this.field_70161_v - this.field_71085_bR;
        \u2603 = 10.0;
        if (d > 10.0) {
            this.field_71091_bM = this.field_71094_bP = this.field_70165_t;
        }
        if (\u2603 > 10.0) {
            this.field_71097_bO = this.field_71085_bR = this.field_70161_v;
        }
        if (\u2603 > 10.0) {
            this.field_71096_bN = this.field_71095_bQ = this.field_70163_u;
        }
        if (d < -10.0) {
            this.field_71091_bM = this.field_71094_bP = this.field_70165_t;
        }
        if (\u2603 < -10.0) {
            this.field_71097_bO = this.field_71085_bR = this.field_70161_v;
        }
        if (\u2603 < -10.0) {
            this.field_71096_bN = this.field_71095_bQ = this.field_70163_u;
        }
        this.field_71094_bP += d * 0.25;
        this.field_71085_bR += \u2603 * 0.25;
        this.field_71095_bQ += \u2603 * 0.25;
    }

    protected void func_184808_cD() {
        float f;
        if (this.func_184613_cA()) {
            f = 0.6f;
            \u2603 = 0.6f;
        } else if (this.func_70608_bn()) {
            f = 0.2f;
            \u2603 = 0.2f;
        } else if (this.func_70093_af()) {
            f = 0.6f;
            \u2603 = 1.65f;
        } else {
            f = 0.6f;
            \u2603 = 1.8f;
        }
        if (f != this.field_70130_N || \u2603 != this.field_70131_O) {
            AxisAlignedBB axisAlignedBB = this.func_174813_aQ();
            axisAlignedBB = new AxisAlignedBB(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, axisAlignedBB.field_72340_a + (double)f, axisAlignedBB.field_72338_b + (double)\u2603, axisAlignedBB.field_72339_c + (double)f);
            if (!this.field_70170_p.func_184143_b(axisAlignedBB)) {
                this.func_70105_a(f, \u2603);
            }
        }
    }

    @Override
    public int func_82145_z() {
        return this.field_71075_bZ.field_75102_a ? 1 : 80;
    }

    @Override
    protected SoundEvent func_184184_Z() {
        return SoundEvents.field_187808_ef;
    }

    @Override
    protected SoundEvent func_184181_aa() {
        return SoundEvents.field_187806_ee;
    }

    @Override
    public int func_82147_ab() {
        return 10;
    }

    @Override
    public void func_184185_a(SoundEvent soundEvent, float f, float f2) {
        this.field_70170_p.func_184148_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, soundEvent, this.func_184176_by(), f, f2);
    }

    @Override
    public SoundCategory func_184176_by() {
        return SoundCategory.PLAYERS;
    }

    @Override
    protected int func_190531_bD() {
        return 20;
    }

    @Override
    public void func_70103_a(byte by) {
        if (by == 9) {
            this.func_71036_o();
        } else if (by == 23) {
            this.field_175153_bG = false;
        } else if (by == 22) {
            this.field_175153_bG = true;
        } else {
            super.func_70103_a(by);
        }
    }

    @Override
    protected boolean func_70610_aX() {
        return this.func_110143_aJ() <= 0.0f || this.func_70608_bn();
    }

    protected void func_71053_j() {
        this.field_71070_bA = this.field_71069_bz;
    }

    @Override
    public void func_70098_U() {
        if (!this.field_70170_p.field_72995_K && this.func_70093_af() && this.func_184218_aH()) {
            this.func_184210_p();
            this.func_70095_a(false);
            return;
        }
        double d = this.field_70165_t;
        \u2603 = this.field_70163_u;
        \u2603 = this.field_70161_v;
        float \u26032 = this.field_70177_z;
        float \u26033 = this.field_70125_A;
        super.func_70098_U();
        this.field_71107_bF = this.field_71109_bG;
        this.field_71109_bG = 0.0f;
        this.func_71015_k(this.field_70165_t - d, this.field_70163_u - \u2603, this.field_70161_v - \u2603);
        if (this.func_184187_bx() instanceof EntityPig) {
            this.field_70125_A = \u26033;
            this.field_70177_z = \u26032;
            this.field_70761_aq = ((EntityPig)this.func_184187_bx()).field_70761_aq;
        }
    }

    @Override
    public void func_70065_x() {
        this.func_70105_a(0.6f, 1.8f);
        super.func_70065_x();
        this.func_70606_j(this.func_110138_aP());
        this.field_70725_aQ = 0;
    }

    @Override
    protected void func_70626_be() {
        super.func_70626_be();
        this.func_82168_bl();
        this.field_70759_as = this.field_70177_z;
    }

    @Override
    public void func_70636_d() {
        if (this.field_71101_bC > 0) {
            --this.field_71101_bC;
        }
        if (this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL && this.field_70170_p.func_82736_K().func_82766_b("naturalRegeneration")) {
            if (this.func_110143_aJ() < this.func_110138_aP() && this.field_70173_aa % 20 == 0) {
                this.func_70691_i(1.0f);
            }
            if (this.field_71100_bB.func_75121_c() && this.field_70173_aa % 10 == 0) {
                this.field_71100_bB.func_75114_a(this.field_71100_bB.func_75116_a() + 1);
            }
        }
        this.field_71071_by.func_70429_k();
        this.field_71107_bF = this.field_71109_bG;
        super.func_70636_d();
        IAttributeInstance iAttributeInstance = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
        if (!this.field_70170_p.field_72995_K) {
            iAttributeInstance.func_111128_a(this.field_71075_bZ.func_75094_b());
        }
        this.field_70747_aH = this.field_71102_ce;
        if (this.func_70051_ag()) {
            this.field_70747_aH = (float)((double)this.field_70747_aH + (double)this.field_71102_ce * 0.3);
        }
        this.func_70659_e((float)iAttributeInstance.func_111126_e());
        float \u26032 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
        float \u26033 = (float)(Math.atan(-this.field_70181_x * (double)0.2f) * 15.0);
        if (\u26032 > 0.1f) {
            \u26032 = 0.1f;
        }
        if (!this.field_70122_E || this.func_110143_aJ() <= 0.0f) {
            \u26032 = 0.0f;
        }
        if (this.field_70122_E || this.func_110143_aJ() <= 0.0f) {
            \u26033 = 0.0f;
        }
        this.field_71109_bG += (\u26032 - this.field_71109_bG) * 0.4f;
        this.field_70726_aT += (\u26033 - this.field_70726_aT) * 0.8f;
        if (this.func_110143_aJ() > 0.0f && !this.func_175149_v()) {
            AxisAlignedBB axisAlignedBB = this.func_184218_aH() && !this.func_184187_bx().field_70128_L ? this.func_174813_aQ().func_111270_a(this.func_184187_bx().func_174813_aQ()).func_72314_b(1.0, 0.0, 1.0) : this.func_174813_aQ().func_72314_b(1.0, 0.5, 1.0);
            List<Entity> \u26034 = this.field_70170_p.func_72839_b(this, axisAlignedBB);
            for (int i = 0; i < \u26034.size(); ++i) {
                Entity entity = \u26034.get(i);
                if (entity.field_70128_L) continue;
                this.func_71044_o(entity);
            }
        }
        this.func_192028_j(this.func_192023_dk());
        this.func_192028_j(this.func_192025_dl());
        if (!this.field_70170_p.field_72995_K && (this.field_70143_R > 0.5f || this.func_70090_H() || this.func_184218_aH()) || this.field_71075_bZ.field_75100_b) {
            this.func_192030_dh();
        }
    }

    private void func_192028_j(@Nullable NBTTagCompound nBTTagCompound) {
        if ((nBTTagCompound != null && !nBTTagCompound.func_74764_b("Silent") || !nBTTagCompound.func_74767_n("Silent")) && (\u2603 = nBTTagCompound.func_74779_i("id")).equals(EntityList.func_191306_a(EntityParrot.class).toString())) {
            EntityParrot.func_192005_a(this.field_70170_p, this);
        }
    }

    private void func_71044_o(Entity entity) {
        entity.func_70100_b_(this);
    }

    public int func_71037_bA() {
        return this.field_70180_af.func_187225_a(field_184830_b);
    }

    public void func_85040_s(int n) {
        this.field_70180_af.func_187227_b(field_184830_b, n);
    }

    public void func_85039_t(int n) {
        \u2603 = this.func_71037_bA();
        this.field_70180_af.func_187227_b(field_184830_b, \u2603 + n);
    }

    @Override
    public void func_70645_a(DamageSource damageSource) {
        super.func_70645_a(damageSource);
        this.func_70105_a(0.2f, 0.2f);
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.field_70181_x = 0.1f;
        if ("Notch".equals(this.func_70005_c_())) {
            this.func_146097_a(new ItemStack(Items.field_151034_e, 1), true, false);
        }
        if (!this.field_70170_p.func_82736_K().func_82766_b("keepInventory") && !this.func_175149_v()) {
            this.func_190776_cN();
            this.field_71071_by.func_70436_m();
        }
        if (damageSource != null) {
            this.field_70159_w = -MathHelper.func_76134_b((this.field_70739_aP + this.field_70177_z) * ((float)Math.PI / 180)) * 0.1f;
            this.field_70179_y = -MathHelper.func_76126_a((this.field_70739_aP + this.field_70177_z) * ((float)Math.PI / 180)) * 0.1f;
        } else {
            this.field_70159_w = 0.0;
            this.field_70179_y = 0.0;
        }
        this.func_71029_a(StatList.field_188069_A);
        this.func_175145_a(StatList.field_188098_h);
        this.func_70066_B();
        this.func_70052_a(0, false);
    }

    protected void func_190776_cN() {
        for (int i = 0; i < this.field_71071_by.func_70302_i_(); ++i) {
            ItemStack itemStack = this.field_71071_by.func_70301_a(i);
            if (itemStack.func_190926_b() || !EnchantmentHelper.func_190939_c(itemStack)) continue;
            this.field_71071_by.func_70304_b(i);
        }
    }

    @Override
    protected SoundEvent func_184601_bQ(DamageSource damageSource) {
        if (damageSource == DamageSource.field_76370_b) {
            return SoundEvents.field_193806_fH;
        }
        if (damageSource == DamageSource.field_76369_e) {
            return SoundEvents.field_193805_fG;
        }
        return SoundEvents.field_187800_eb;
    }

    @Override
    protected SoundEvent func_184615_bR() {
        return SoundEvents.field_187798_ea;
    }

    @Nullable
    public EntityItem func_71040_bB(boolean bl) {
        return this.func_146097_a(this.field_71071_by.func_70298_a(this.field_71071_by.field_70461_c, bl && !this.field_71071_by.func_70448_g().func_190926_b() ? this.field_71071_by.func_70448_g().func_190916_E() : 1), false, true);
    }

    @Nullable
    public EntityItem func_71019_a(ItemStack itemStack, boolean bl) {
        return this.func_146097_a(itemStack, false, bl);
    }

    @Nullable
    public EntityItem func_146097_a(ItemStack itemStack, boolean bl, boolean bl2) {
        float f;
        if (itemStack.func_190926_b()) {
            return null;
        }
        double d = this.field_70163_u - (double)0.3f + (double)this.func_70047_e();
        EntityItem \u26032 = new EntityItem(this.field_70170_p, this.field_70165_t, d, this.field_70161_v, itemStack);
        \u26032.func_174867_a(40);
        if (bl2) {
            \u26032.func_145799_b(this.func_70005_c_());
        }
        if (bl) {
            f = this.field_70146_Z.nextFloat() * 0.5f;
            \u2603 = this.field_70146_Z.nextFloat() * ((float)Math.PI * 2);
            \u26032.field_70159_w = -MathHelper.func_76126_a(\u2603) * f;
            \u26032.field_70179_y = MathHelper.func_76134_b(\u2603) * f;
            \u26032.field_70181_x = 0.2f;
        } else {
            f = 0.3f;
            \u26032.field_70159_w = -MathHelper.func_76126_a(this.field_70177_z * ((float)Math.PI / 180)) * MathHelper.func_76134_b(this.field_70125_A * ((float)Math.PI / 180)) * f;
            \u26032.field_70179_y = MathHelper.func_76134_b(this.field_70177_z * ((float)Math.PI / 180)) * MathHelper.func_76134_b(this.field_70125_A * ((float)Math.PI / 180)) * f;
            \u26032.field_70181_x = -MathHelper.func_76126_a(this.field_70125_A * ((float)Math.PI / 180)) * f + 0.1f;
            \u2603 = this.field_70146_Z.nextFloat() * ((float)Math.PI * 2);
            f = 0.02f * this.field_70146_Z.nextFloat();
            \u26032.field_70159_w += Math.cos(\u2603) * (double)f;
            \u26032.field_70181_x += (double)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.1f);
            \u26032.field_70179_y += Math.sin(\u2603) * (double)f;
        }
        ItemStack itemStack2 = this.func_184816_a(\u26032);
        if (bl2) {
            if (!itemStack2.func_190926_b()) {
                this.func_71064_a(StatList.func_188058_e(itemStack2.func_77973_b()), itemStack.func_190916_E());
            }
            this.func_71029_a(StatList.field_75952_v);
        }
        return \u26032;
    }

    protected ItemStack func_184816_a(EntityItem entityItem) {
        this.field_70170_p.func_72838_d(entityItem);
        return entityItem.func_92059_d();
    }

    public float func_184813_a(IBlockState iBlockState) {
        float f = this.field_71071_by.func_184438_a(iBlockState);
        if (f > 1.0f) {
            int n = EnchantmentHelper.func_185293_e(this);
            ItemStack \u26032 = this.func_184614_ca();
            if (n > 0 && !\u26032.func_190926_b()) {
                f += (float)(n * n + 1);
            }
        }
        if (this.func_70644_a(MobEffects.field_76422_e)) {
            f *= 1.0f + (float)(this.func_70660_b(MobEffects.field_76422_e).func_76458_c() + 1) * 0.2f;
        }
        if (this.func_70644_a(MobEffects.field_76419_f)) {
            float f2;
            switch (this.func_70660_b(MobEffects.field_76419_f).func_76458_c()) {
                case 0: {
                    f2 = 0.3f;
                    break;
                }
                case 1: {
                    f2 = 0.09f;
                    break;
                }
                case 2: {
                    f2 = 0.0027f;
                    break;
                }
                default: {
                    f2 = 8.1E-4f;
                }
            }
            f *= f2;
        }
        if (this.func_70055_a(Material.field_151586_h) && !EnchantmentHelper.func_185287_i(this)) {
            f /= 5.0f;
        }
        if (!this.field_70122_E) {
            f /= 5.0f;
        }
        return f;
    }

    public boolean func_184823_b(IBlockState iBlockState) {
        return this.field_71071_by.func_184432_b(iBlockState);
    }

    public static void func_189806_a(DataFixer dataFixer) {
        dataFixer.func_188258_a(FixTypes.PLAYER, new IDataWalker(){

            @Override
            public NBTTagCompound func_188266_a(IDataFixer iDataFixer, NBTTagCompound nBTTagCompound, int n) {
                DataFixesManager.func_188278_b(iDataFixer, nBTTagCompound, n, "Inventory");
                DataFixesManager.func_188278_b(iDataFixer, nBTTagCompound, n, "EnderItems");
                if (nBTTagCompound.func_150297_b("ShoulderEntityLeft", 10)) {
                    nBTTagCompound.func_74782_a("ShoulderEntityLeft", iDataFixer.func_188251_a(FixTypes.ENTITY, nBTTagCompound.func_74775_l("ShoulderEntityLeft"), n));
                }
                if (nBTTagCompound.func_150297_b("ShoulderEntityRight", 10)) {
                    nBTTagCompound.func_74782_a("ShoulderEntityRight", iDataFixer.func_188251_a(FixTypes.ENTITY, nBTTagCompound.func_74775_l("ShoulderEntityRight"), n));
                }
                return nBTTagCompound;
            }
        });
    }

    @Override
    public void func_70037_a(NBTTagCompound nBTTagCompound) {
        super.func_70037_a(nBTTagCompound);
        this.func_184221_a(EntityPlayer.func_146094_a(this.field_146106_i));
        NBTTagList nBTTagList = nBTTagCompound.func_150295_c("Inventory", 10);
        this.field_71071_by.func_70443_b(nBTTagList);
        this.field_71071_by.field_70461_c = nBTTagCompound.func_74762_e("SelectedItemSlot");
        this.field_71083_bS = nBTTagCompound.func_74767_n("Sleeping");
        this.field_71076_b = nBTTagCompound.func_74765_d("SleepTimer");
        this.field_71106_cc = nBTTagCompound.func_74760_g("XpP");
        this.field_71068_ca = nBTTagCompound.func_74762_e("XpLevel");
        this.field_71067_cb = nBTTagCompound.func_74762_e("XpTotal");
        this.field_175152_f = nBTTagCompound.func_74762_e("XpSeed");
        if (this.field_175152_f == 0) {
            this.field_175152_f = this.field_70146_Z.nextInt();
        }
        this.func_85040_s(nBTTagCompound.func_74762_e("Score"));
        if (this.field_71083_bS) {
            this.field_71081_bT = new BlockPos(this);
            this.func_70999_a(true, true, false);
        }
        if (nBTTagCompound.func_150297_b("SpawnX", 99) && nBTTagCompound.func_150297_b("SpawnY", 99) && nBTTagCompound.func_150297_b("SpawnZ", 99)) {
            this.field_71077_c = new BlockPos(nBTTagCompound.func_74762_e("SpawnX"), nBTTagCompound.func_74762_e("SpawnY"), nBTTagCompound.func_74762_e("SpawnZ"));
            this.field_82248_d = nBTTagCompound.func_74767_n("SpawnForced");
        }
        this.field_71100_bB.func_75112_a(nBTTagCompound);
        this.field_71075_bZ.func_75095_b(nBTTagCompound);
        if (nBTTagCompound.func_150297_b("EnderItems", 9)) {
            \u2603 = nBTTagCompound.func_150295_c("EnderItems", 10);
            this.field_71078_a.func_70486_a(\u2603);
        }
        if (nBTTagCompound.func_150297_b("ShoulderEntityLeft", 10)) {
            this.func_192029_h(nBTTagCompound.func_74775_l("ShoulderEntityLeft"));
        }
        if (nBTTagCompound.func_150297_b("ShoulderEntityRight", 10)) {
            this.func_192031_i(nBTTagCompound.func_74775_l("ShoulderEntityRight"));
        }
    }

    @Override
    public void func_70014_b(NBTTagCompound nBTTagCompound) {
        super.func_70014_b(nBTTagCompound);
        nBTTagCompound.func_74768_a("DataVersion", 1343);
        nBTTagCompound.func_74782_a("Inventory", this.field_71071_by.func_70442_a(new NBTTagList()));
        nBTTagCompound.func_74768_a("SelectedItemSlot", this.field_71071_by.field_70461_c);
        nBTTagCompound.func_74757_a("Sleeping", this.field_71083_bS);
        nBTTagCompound.func_74777_a("SleepTimer", (short)this.field_71076_b);
        nBTTagCompound.func_74776_a("XpP", this.field_71106_cc);
        nBTTagCompound.func_74768_a("XpLevel", this.field_71068_ca);
        nBTTagCompound.func_74768_a("XpTotal", this.field_71067_cb);
        nBTTagCompound.func_74768_a("XpSeed", this.field_175152_f);
        nBTTagCompound.func_74768_a("Score", this.func_71037_bA());
        if (this.field_71077_c != null) {
            nBTTagCompound.func_74768_a("SpawnX", this.field_71077_c.func_177958_n());
            nBTTagCompound.func_74768_a("SpawnY", this.field_71077_c.func_177956_o());
            nBTTagCompound.func_74768_a("SpawnZ", this.field_71077_c.func_177952_p());
            nBTTagCompound.func_74757_a("SpawnForced", this.field_82248_d);
        }
        this.field_71100_bB.func_75117_b(nBTTagCompound);
        this.field_71075_bZ.func_75091_a(nBTTagCompound);
        nBTTagCompound.func_74782_a("EnderItems", this.field_71078_a.func_70487_g());
        if (!this.func_192023_dk().func_82582_d()) {
            nBTTagCompound.func_74782_a("ShoulderEntityLeft", this.func_192023_dk());
        }
        if (!this.func_192025_dl().func_82582_d()) {
            nBTTagCompound.func_74782_a("ShoulderEntityRight", this.func_192025_dl());
        }
    }

    @Override
    public boolean func_70097_a(DamageSource damageSource, float f) {
        if (this.func_180431_b(damageSource)) {
            return false;
        }
        if (this.field_71075_bZ.field_75102_a && !damageSource.func_76357_e()) {
            return false;
        }
        this.field_70708_bq = 0;
        if (this.func_110143_aJ() <= 0.0f) {
            return false;
        }
        if (this.func_70608_bn() && !this.field_70170_p.field_72995_K) {
            this.func_70999_a(true, true, false);
        }
        this.func_192030_dh();
        if (damageSource.func_76350_n()) {
            if (this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
                f = 0.0f;
            }
            if (this.field_70170_p.func_175659_aa() == EnumDifficulty.EASY) {
                f = Math.min(f / 2.0f + 1.0f, f);
            }
            if (this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD) {
                f = f * 3.0f / 2.0f;
            }
        }
        if (f == 0.0f) {
            return false;
        }
        return super.func_70097_a(damageSource, f);
    }

    @Override
    protected void func_190629_c(EntityLivingBase entityLivingBase) {
        super.func_190629_c(entityLivingBase);
        if (entityLivingBase.func_184614_ca().func_77973_b() instanceof ItemAxe) {
            this.func_190777_m(true);
        }
    }

    public boolean func_96122_a(EntityPlayer entityPlayer) {
        Team team = this.func_96124_cp();
        \u2603 = entityPlayer.func_96124_cp();
        if (team == null) {
            return true;
        }
        if (!team.func_142054_a(\u2603)) {
            return true;
        }
        return team.func_96665_g();
    }

    @Override
    protected void func_70675_k(float f) {
        this.field_71071_by.func_70449_g(f);
    }

    @Override
    protected void func_184590_k(float f) {
        if (f >= 3.0f && this.field_184627_bm.func_77973_b() == Items.field_185159_cQ) {
            int n = 1 + MathHelper.func_76141_d(f);
            this.field_184627_bm.func_77972_a(n, this);
            if (this.field_184627_bm.func_190926_b()) {
                EnumHand enumHand = this.func_184600_cs();
                if (enumHand == EnumHand.MAIN_HAND) {
                    this.func_184201_a(EntityEquipmentSlot.MAINHAND, ItemStack.field_190927_a);
                } else {
                    this.func_184201_a(EntityEquipmentSlot.OFFHAND, ItemStack.field_190927_a);
                }
                this.field_184627_bm = ItemStack.field_190927_a;
                this.func_184185_a(SoundEvents.field_187769_eM, 0.8f, 0.8f + this.field_70170_p.field_73012_v.nextFloat() * 0.4f);
            }
        }
    }

    public float func_82243_bO() {
        int n = 0;
        for (ItemStack itemStack : this.field_71071_by.field_70460_b) {
            if (itemStack.func_190926_b()) continue;
            ++n;
        }
        return (float)n / (float)this.field_71071_by.field_70460_b.size();
    }

    @Override
    protected void func_70665_d(DamageSource damageSource, float f) {
        if (this.func_180431_b(damageSource)) {
            return;
        }
        f = this.func_70655_b(damageSource, f);
        \u2603 = f = this.func_70672_c(damageSource, f);
        f = Math.max(f - this.func_110139_bj(), 0.0f);
        this.func_110149_m(this.func_110139_bj() - (\u2603 - f));
        if (f == 0.0f) {
            return;
        }
        this.func_71020_j(damageSource.func_76345_d());
        \u2603 = this.func_110143_aJ();
        this.func_70606_j(this.func_110143_aJ() - f);
        this.func_110142_aN().func_94547_a(damageSource, \u2603, f);
        if (f < 3.4028235E37f) {
            this.func_71064_a(StatList.field_188112_z, Math.round(f * 10.0f));
        }
    }

    public void func_175141_a(TileEntitySign tileEntitySign) {
    }

    public void func_184809_a(CommandBlockBaseLogic commandBlockBaseLogic) {
    }

    public void func_184824_a(TileEntityCommandBlock tileEntityCommandBlock) {
    }

    public void func_189807_a(TileEntityStructure tileEntityStructure) {
    }

    public void func_180472_a(IMerchant iMerchant) {
    }

    public void func_71007_a(IInventory iInventory) {
    }

    public void func_184826_a(AbstractHorse abstractHorse, IInventory iInventory) {
    }

    public void func_180468_a(IInteractionObject iInteractionObject) {
    }

    public void func_184814_a(ItemStack itemStack, EnumHand enumHand) {
    }

    public EnumActionResult func_190775_a(Entity entity, EnumHand enumHand) {
        if (this.func_175149_v()) {
            if (entity instanceof IInventory) {
                this.func_71007_a((IInventory)((Object)entity));
            }
            return EnumActionResult.PASS;
        }
        ItemStack itemStack = this.func_184586_b(enumHand);
        ItemStack itemStack2 = \u2603 = itemStack.func_190926_b() ? ItemStack.field_190927_a : itemStack.func_77946_l();
        if (entity.func_184230_a(this, enumHand)) {
            if (this.field_71075_bZ.field_75098_d && itemStack == this.func_184586_b(enumHand) && itemStack.func_190916_E() < \u2603.func_190916_E()) {
                itemStack.func_190920_e(\u2603.func_190916_E());
            }
            return EnumActionResult.SUCCESS;
        }
        if (!itemStack.func_190926_b() && entity instanceof EntityLivingBase) {
            if (this.field_71075_bZ.field_75098_d) {
                itemStack = \u2603;
            }
            if (itemStack.func_111282_a(this, (EntityLivingBase)entity, enumHand)) {
                if (itemStack.func_190926_b() && !this.field_71075_bZ.field_75098_d) {
                    this.func_184611_a(enumHand, ItemStack.field_190927_a);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public double func_70033_W() {
        return -0.35;
    }

    @Override
    public void func_184210_p() {
        super.func_184210_p();
        this.field_184245_j = 0;
    }

    public void func_71059_n(Entity entity2) {
        if (!entity2.func_70075_an()) {
            return;
        }
        if (entity2.func_85031_j(this)) {
            return;
        }
        float f = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
        \u2603 = entity2 instanceof EntityLivingBase ? EnchantmentHelper.func_152377_a(this.func_184614_ca(), ((EntityLivingBase)entity2).func_70668_bt()) : EnchantmentHelper.func_152377_a(this.func_184614_ca(), EnumCreatureAttribute.UNDEFINED);
        \u2603 = this.func_184825_o(0.5f);
        \u2603 *= \u2603;
        this.func_184821_cY();
        if ((f *= 0.2f + \u2603 * \u2603 * 0.8f) > 0.0f || \u2603 > 0.0f) {
            boolean bl = \u2603 > 0.9f;
            \u2603 = false;
            int \u26032 = 0;
            \u26032 += EnchantmentHelper.func_77501_a(this);
            if (this.func_70051_ag() && bl) {
                this.field_70170_p.func_184148_a(null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187721_dT, this.func_184176_by(), 1.0f, 1.0f);
                ++\u26032;
                \u2603 = true;
            }
            \u2603 = bl && this.field_70143_R > 0.0f && !this.field_70122_E && !this.func_70617_f_() && !this.func_70090_H() && !this.func_70644_a(MobEffects.field_76440_q) && !this.func_184218_aH() && entity2 instanceof EntityLivingBase;
            boolean bl2 = \u2603 = \u2603 && !this.func_70051_ag();
            if (\u2603) {
                f *= 1.5f;
            }
            f += \u2603;
            \u2603 = false;
            double \u26033 = this.field_70140_Q - this.field_70141_P;
            if (bl && !\u2603 && !\u2603 && this.field_70122_E && \u26033 < (double)this.func_70689_ay() && (\u2603 = this.func_184586_b(EnumHand.MAIN_HAND)).func_77973_b() instanceof ItemSword) {
                \u2603 = true;
            }
            float \u26034 = 0.0f;
            bl3 = false;
            int \u26035 = EnchantmentHelper.func_90036_a(this);
            if (entity2 instanceof EntityLivingBase) {
                \u26034 = ((EntityLivingBase)entity2).func_110143_aJ();
                if (\u26035 > 0 && !entity2.func_70027_ad()) {
                    bl3 = true;
                    entity2.func_70015_d(1);
                }
            }
            double \u26036 = entity2.field_70159_w;
            double \u26037 = entity2.field_70181_x;
            double \u26038 = entity2.field_70179_y;
            \u2603 = entity2.func_70097_a(DamageSource.func_76365_a(this), f);
            if (\u2603) {
                Entity entity2;
                Object \u26039;
                if (\u26032 > 0) {
                    if (entity2 instanceof EntityLivingBase) {
                        ((EntityLivingBase)entity2).func_70653_a(this, (float)\u26032 * 0.5f, MathHelper.func_76126_a(this.field_70177_z * ((float)Math.PI / 180)), -MathHelper.func_76134_b(this.field_70177_z * ((float)Math.PI / 180)));
                    } else {
                        entity2.func_70024_g(-MathHelper.func_76126_a(this.field_70177_z * ((float)Math.PI / 180)) * (float)\u26032 * 0.5f, 0.1, MathHelper.func_76134_b(this.field_70177_z * ((float)Math.PI / 180)) * (float)\u26032 * 0.5f);
                    }
                    this.field_70159_w *= 0.6;
                    this.field_70179_y *= 0.6;
                    this.func_70031_b(false);
                }
                if (\u2603) {
                    float f2 = 1.0f + EnchantmentHelper.func_191527_a(this) * f;
                    \u26039 = this.field_70170_p.func_72872_a(EntityLivingBase.class, entity2.func_174813_aQ().func_72314_b(1.0, 0.25, 1.0));
                    Iterator<EntityLivingBase> iterator = \u26039.iterator();
                    while (iterator.hasNext()) {
                        EntityLivingBase entityLivingBase = iterator.next();
                        if (entityLivingBase == this || entityLivingBase == entity2 || this.func_184191_r(entityLivingBase) || !(this.func_70068_e(entityLivingBase) < 9.0)) continue;
                        entityLivingBase.func_70653_a(this, 0.4f, MathHelper.func_76126_a(this.field_70177_z * ((float)Math.PI / 180)), -MathHelper.func_76134_b(this.field_70177_z * ((float)Math.PI / 180)));
                        entityLivingBase.func_70097_a(DamageSource.func_76365_a(this), f2);
                    }
                    this.field_70170_p.func_184148_a(null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187730_dW, this.func_184176_by(), 1.0f, 1.0f);
                    this.func_184810_cG();
                }
                if (entity2 instanceof EntityPlayerMP && entity2.field_70133_I) {
                    ((EntityPlayerMP)entity2).field_71135_a.func_147359_a(new SPacketEntityVelocity(entity2));
                    entity2.field_70133_I = false;
                    entity2.field_70159_w = \u26036;
                    entity2.field_70181_x = \u26037;
                    entity2.field_70179_y = \u26038;
                }
                if (\u2603) {
                    this.field_70170_p.func_184148_a(null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187718_dS, this.func_184176_by(), 1.0f, 1.0f);
                    this.func_71009_b(entity2);
                }
                if (!\u2603 && !\u2603) {
                    if (bl) {
                        this.field_70170_p.func_184148_a(null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187727_dV, this.func_184176_by(), 1.0f, 1.0f);
                    } else {
                        this.field_70170_p.func_184148_a(null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187733_dX, this.func_184176_by(), 1.0f, 1.0f);
                    }
                }
                if (\u2603 > 0.0f) {
                    this.func_71047_c(entity2);
                }
                this.func_130011_c(entity2);
                if (entity2 instanceof EntityLivingBase) {
                    EnchantmentHelper.func_151384_a((EntityLivingBase)entity2, this);
                }
                EnchantmentHelper.func_151385_b(this, entity2);
                ItemStack \u260310 = this.func_184614_ca();
                \u26039 = entity2;
                if (entity2 instanceof MultiPartEntityPart && (\u2603 = ((MultiPartEntityPart)entity2).field_70259_a) instanceof EntityLivingBase) {
                    \u26039 = (EntityLivingBase)((Object)\u2603);
                }
                if (!\u260310.func_190926_b() && \u26039 instanceof EntityLivingBase) {
                    \u260310.func_77961_a((EntityLivingBase)\u26039, this);
                    if (\u260310.func_190926_b()) {
                        this.func_184611_a(EnumHand.MAIN_HAND, ItemStack.field_190927_a);
                    }
                }
                if (entity2 instanceof EntityLivingBase) {
                    float f3 = \u26034 - ((EntityLivingBase)entity2).func_110143_aJ();
                    this.func_71064_a(StatList.field_188111_y, Math.round(f3 * 10.0f));
                    if (\u26035 > 0) {
                        entity2.func_70015_d(\u26035 * 4);
                    }
                    if (this.field_70170_p instanceof WorldServer && f3 > 2.0f) {
                        int n = (int)((double)f3 * 0.5);
                        ((WorldServer)this.field_70170_p).func_175739_a(EnumParticleTypes.DAMAGE_INDICATOR, entity2.field_70165_t, entity2.field_70163_u + (double)(entity2.field_70131_O * 0.5f), entity2.field_70161_v, n, 0.1, 0.0, 0.1, 0.2, new int[0]);
                    }
                }
                this.func_71020_j(0.1f);
            } else {
                boolean bl3;
                this.field_70170_p.func_184148_a(null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187724_dU, this.func_184176_by(), 1.0f, 1.0f);
                if (bl3) {
                    entity2.func_70066_B();
                }
            }
        }
    }

    public void func_190777_m(boolean bl) {
        float f = 0.25f + (float)EnchantmentHelper.func_185293_e(this) * 0.05f;
        if (bl) {
            f += 0.75f;
        }
        if (this.field_70146_Z.nextFloat() < f) {
            this.func_184811_cZ().func_185145_a(Items.field_185159_cQ, 100);
            this.func_184602_cy();
            this.field_70170_p.func_72960_a(this, (byte)30);
        }
    }

    public void func_71009_b(Entity entity) {
    }

    public void func_71047_c(Entity entity) {
    }

    public void func_184810_cG() {
        double d = -MathHelper.func_76126_a(this.field_70177_z * ((float)Math.PI / 180));
        \u2603 = MathHelper.func_76134_b(this.field_70177_z * ((float)Math.PI / 180));
        if (this.field_70170_p instanceof WorldServer) {
            ((WorldServer)this.field_70170_p).func_175739_a(EnumParticleTypes.SWEEP_ATTACK, this.field_70165_t + d, this.field_70163_u + (double)this.field_70131_O * 0.5, this.field_70161_v + \u2603, 0, d, 0.0, \u2603, 0.0, new int[0]);
        }
    }

    public void func_71004_bE() {
    }

    @Override
    public void func_70106_y() {
        super.func_70106_y();
        this.field_71069_bz.func_75134_a(this);
        if (this.field_71070_bA != null) {
            this.field_71070_bA.func_75134_a(this);
        }
    }

    @Override
    public boolean func_70094_T() {
        return !this.field_71083_bS && super.func_70094_T();
    }

    public boolean func_175144_cb() {
        return false;
    }

    public GameProfile func_146103_bH() {
        return this.field_146106_i;
    }

    public SleepResult func_180469_a(BlockPos blockPos) {
        EnumFacing enumFacing = this.field_70170_p.func_180495_p(blockPos).func_177229_b(BlockHorizontal.field_185512_D);
        if (!this.field_70170_p.field_72995_K) {
            if (this.func_70608_bn() || !this.func_70089_S()) {
                return SleepResult.OTHER_PROBLEM;
            }
            if (!this.field_70170_p.field_73011_w.func_76569_d()) {
                return SleepResult.NOT_POSSIBLE_HERE;
            }
            if (this.field_70170_p.func_72935_r()) {
                return SleepResult.NOT_POSSIBLE_NOW;
            }
            if (!this.func_190774_a(blockPos, enumFacing)) {
                return SleepResult.TOO_FAR_AWAY;
            }
            double d = 8.0;
            \u2603 = 5.0;
            List<EntityMob> \u26032 = this.field_70170_p.func_175647_a(EntityMob.class, new AxisAlignedBB((double)blockPos.func_177958_n() - 8.0, (double)blockPos.func_177956_o() - 5.0, (double)blockPos.func_177952_p() - 8.0, (double)blockPos.func_177958_n() + 8.0, (double)blockPos.func_177956_o() + 5.0, (double)blockPos.func_177952_p() + 8.0), new SleepEnemyPredicate(this));
            if (!\u26032.isEmpty()) {
                return SleepResult.NOT_SAFE;
            }
        }
        if (this.func_184218_aH()) {
            this.func_184210_p();
        }
        this.func_192030_dh();
        this.func_70105_a(0.2f, 0.2f);
        if (this.field_70170_p.func_175667_e(blockPos)) {
            float \u26033 = 0.5f + (float)enumFacing.func_82601_c() * 0.4f;
            float \u26034 = 0.5f + (float)enumFacing.func_82599_e() * 0.4f;
            this.func_175139_a(enumFacing);
            this.func_70107_b((float)blockPos.func_177958_n() + \u26033, (float)blockPos.func_177956_o() + 0.6875f, (float)blockPos.func_177952_p() + \u26034);
        } else {
            this.func_70107_b((float)blockPos.func_177958_n() + 0.5f, (float)blockPos.func_177956_o() + 0.6875f, (float)blockPos.func_177952_p() + 0.5f);
        }
        this.field_71083_bS = true;
        this.field_71076_b = 0;
        this.field_71081_bT = blockPos;
        this.field_70159_w = 0.0;
        this.field_70181_x = 0.0;
        this.field_70179_y = 0.0;
        if (!this.field_70170_p.field_72995_K) {
            this.field_70170_p.func_72854_c();
        }
        return SleepResult.OK;
    }

    private boolean func_190774_a(BlockPos blockPos, EnumFacing enumFacing) {
        if (Math.abs(this.field_70165_t - (double)blockPos.func_177958_n()) <= 3.0 && Math.abs(this.field_70163_u - (double)blockPos.func_177956_o()) <= 2.0 && Math.abs(this.field_70161_v - (double)blockPos.func_177952_p()) <= 3.0) {
            return true;
        }
        BlockPos blockPos2 = blockPos.func_177972_a(enumFacing.func_176734_d());
        return Math.abs(this.field_70165_t - (double)blockPos2.func_177958_n()) <= 3.0 && Math.abs(this.field_70163_u - (double)blockPos2.func_177956_o()) <= 2.0 && Math.abs(this.field_70161_v - (double)blockPos2.func_177952_p()) <= 3.0;
    }

    private void func_175139_a(EnumFacing enumFacing) {
        this.field_71079_bU = -1.8f * (float)enumFacing.func_82601_c();
        this.field_71089_bV = -1.8f * (float)enumFacing.func_82599_e();
    }

    public void func_70999_a(boolean bl, boolean bl22, boolean bl3) {
        boolean bl22;
        this.func_70105_a(0.6f, 1.8f);
        IBlockState iBlockState = this.field_70170_p.func_180495_p(this.field_71081_bT);
        if (this.field_71081_bT != null && iBlockState.func_177230_c() == Blocks.field_150324_C) {
            this.field_70170_p.func_180501_a(this.field_71081_bT, iBlockState.func_177226_a(BlockBed.field_176471_b, false), 4);
            BlockPos blockPos = BlockBed.func_176468_a(this.field_70170_p, this.field_71081_bT, 0);
            if (blockPos == null) {
                blockPos = this.field_71081_bT.func_177984_a();
            }
            this.func_70107_b((float)blockPos.func_177958_n() + 0.5f, (float)blockPos.func_177956_o() + 0.1f, (float)blockPos.func_177952_p() + 0.5f);
        }
        this.field_71083_bS = false;
        if (!this.field_70170_p.field_72995_K && bl22) {
            this.field_70170_p.func_72854_c();
        }
        int n = this.field_71076_b = bl ? 0 : 100;
        if (bl3) {
            this.func_180473_a(this.field_71081_bT, false);
        }
    }

    private boolean func_175143_p() {
        return this.field_70170_p.func_180495_p(this.field_71081_bT).func_177230_c() == Blocks.field_150324_C;
    }

    @Nullable
    public static BlockPos func_180467_a(World world2, BlockPos blockPos, boolean bl) {
        World world2;
        Block block = world2.func_180495_p(blockPos).func_177230_c();
        if (block != Blocks.field_150324_C) {
            if (!bl) {
                return null;
            }
            boolean bl2 = block.func_181623_g();
            \u2603 = world2.func_180495_p(blockPos.func_177984_a()).func_177230_c().func_181623_g();
            if (bl2 && \u2603) {
                return blockPos;
            }
            return null;
        }
        return BlockBed.func_176468_a(world2, blockPos, 0);
    }

    public float func_71051_bG() {
        if (this.field_71081_bT != null) {
            EnumFacing enumFacing = this.field_70170_p.func_180495_p(this.field_71081_bT).func_177229_b(BlockHorizontal.field_185512_D);
            switch (enumFacing) {
                case SOUTH: {
                    return 90.0f;
                }
                case WEST: {
                    return 0.0f;
                }
                case NORTH: {
                    return 270.0f;
                }
                case EAST: {
                    return 180.0f;
                }
            }
        }
        return 0.0f;
    }

    @Override
    public boolean func_70608_bn() {
        return this.field_71083_bS;
    }

    public boolean func_71026_bH() {
        return this.field_71083_bS && this.field_71076_b >= 100;
    }

    public int func_71060_bI() {
        return this.field_71076_b;
    }

    public void func_146105_b(ITextComponent iTextComponent, boolean bl) {
    }

    public BlockPos func_180470_cg() {
        return this.field_71077_c;
    }

    public boolean func_82245_bX() {
        return this.field_82248_d;
    }

    public void func_180473_a(BlockPos blockPos, boolean bl) {
        if (blockPos != null) {
            this.field_71077_c = blockPos;
            this.field_82248_d = bl;
        } else {
            this.field_71077_c = null;
            this.field_82248_d = false;
        }
    }

    public void func_71029_a(StatBase statBase) {
        this.func_71064_a(statBase, 1);
    }

    public void func_71064_a(StatBase statBase, int n) {
    }

    public void func_175145_a(StatBase statBase) {
    }

    public void func_192021_a(List<IRecipe> list) {
    }

    public void func_193102_a(ResourceLocation[] resourceLocationArray) {
    }

    public void func_192022_b(List<IRecipe> list) {
    }

    @Override
    public void func_70664_aZ() {
        super.func_70664_aZ();
        this.func_71029_a(StatList.field_75953_u);
        if (this.func_70051_ag()) {
            this.func_71020_j(0.2f);
        } else {
            this.func_71020_j(0.05f);
        }
    }

    @Override
    public void func_191986_a(float f5, float f2, float f3) {
        double d = this.field_70165_t;
        \u2603 = this.field_70163_u;
        \u2603 = this.field_70161_v;
        if (this.field_71075_bZ.field_75100_b && !this.func_184218_aH()) {
            \u2603 = this.field_70181_x;
            float f4 = this.field_70747_aH;
            this.field_70747_aH = this.field_71075_bZ.func_75093_a() * (float)(this.func_70051_ag() ? 2 : 1);
            super.func_191986_a(f5, f2, f3);
            this.field_70181_x = \u2603 * 0.6;
            this.field_70747_aH = f4;
            this.field_70143_R = 0.0f;
            this.func_70052_a(7, false);
        } else {
            float f5;
            super.func_191986_a(f5, f2, f3);
        }
        this.func_71000_j(this.field_70165_t - d, this.field_70163_u - \u2603, this.field_70161_v - \u2603);
    }

    @Override
    public float func_70689_ay() {
        return (float)this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e();
    }

    public void func_71000_j(double d4, double d2, double d3) {
        if (this.func_184218_aH()) {
            return;
        }
        if (this.func_70055_a(Material.field_151586_h)) {
            int n = Math.round(MathHelper.func_76133_a(d4 * d4 + d2 * d2 + d3 * d3) * 100.0f);
            if (n > 0) {
                this.func_71064_a(StatList.field_188105_q, n);
                this.func_71020_j(0.01f * (float)n * 0.01f);
            }
        } else if (this.func_70090_H()) {
            double d4;
            int \u26032 = Math.round(MathHelper.func_76133_a(d4 * d4 + d3 * d3) * 100.0f);
            if (\u26032 > 0) {
                this.func_71064_a(StatList.field_75946_m, \u26032);
                this.func_71020_j(0.01f * (float)\u26032 * 0.01f);
            }
        } else if (this.func_70617_f_()) {
            if (d2 > 0.0) {
                this.func_71064_a(StatList.field_188103_o, (int)Math.round(d2 * 100.0));
            }
        } else if (this.field_70122_E) {
            int \u26033 = Math.round(MathHelper.func_76133_a(d4 * d4 + d3 * d3) * 100.0f);
            if (\u26033 > 0) {
                if (this.func_70051_ag()) {
                    this.func_71064_a(StatList.field_188102_l, \u26033);
                    this.func_71020_j(0.1f * (float)\u26033 * 0.01f);
                } else if (this.func_70093_af()) {
                    this.func_71064_a(StatList.field_188101_k, \u26033);
                    this.func_71020_j(0.0f * (float)\u26033 * 0.01f);
                } else {
                    this.func_71064_a(StatList.field_188100_j, \u26033);
                    this.func_71020_j(0.0f * (float)\u26033 * 0.01f);
                }
            }
        } else if (this.func_184613_cA()) {
            int \u26034 = Math.round(MathHelper.func_76133_a(d4 * d4 + d2 * d2 + d3 * d3) * 100.0f);
            this.func_71064_a(StatList.field_188110_v, \u26034);
        } else {
            int \u26035 = Math.round(MathHelper.func_76133_a(d4 * d4 + d3 * d3) * 100.0f);
            if (\u26035 > 25) {
                this.func_71064_a(StatList.field_188104_p, \u26035);
            }
        }
    }

    private void func_71015_k(double d, double d2, double d3) {
        if (this.func_184218_aH() && (\u2603 = Math.round(MathHelper.func_76133_a(d * d + d2 * d2 + d3 * d3) * 100.0f)) > 0) {
            if (this.func_184187_bx() instanceof EntityMinecart) {
                this.func_71064_a(StatList.field_188106_r, \u2603);
            } else if (this.func_184187_bx() instanceof EntityBoat) {
                this.func_71064_a(StatList.field_188107_s, \u2603);
            } else if (this.func_184187_bx() instanceof EntityPig) {
                this.func_71064_a(StatList.field_188108_t, \u2603);
            } else if (this.func_184187_bx() instanceof AbstractHorse) {
                this.func_71064_a(StatList.field_188109_u, \u2603);
            }
        }
    }

    @Override
    public void func_180430_e(float f, float f2) {
        if (this.field_71075_bZ.field_75101_c) {
            return;
        }
        if (f >= 2.0f) {
            this.func_71064_a(StatList.field_75943_n, (int)Math.round((double)f * 100.0));
        }
        super.func_180430_e(f, f2);
    }

    @Override
    protected void func_71061_d_() {
        if (!this.func_175149_v()) {
            super.func_71061_d_();
        }
    }

    @Override
    protected SoundEvent func_184588_d(int n) {
        if (n > 4) {
            return SoundEvents.field_187736_dY;
        }
        return SoundEvents.field_187804_ed;
    }

    @Override
    public void func_70074_a(EntityLivingBase entityLivingBase) {
        EntityList.EntityEggInfo entityEggInfo = EntityList.field_75627_a.get(EntityList.func_191301_a(entityLivingBase));
        if (entityEggInfo != null) {
            this.func_71029_a(entityEggInfo.field_151512_d);
        }
    }

    @Override
    public void func_70110_aj() {
        if (!this.field_71075_bZ.field_75100_b) {
            super.func_70110_aj();
        }
    }

    public void func_71023_q(int n) {
        this.func_85039_t(n);
        \u2603 = Integer.MAX_VALUE - this.field_71067_cb;
        if (n > \u2603) {
            n = \u2603;
        }
        this.field_71106_cc += (float)n / (float)this.func_71050_bK();
        this.field_71067_cb += n;
        while (this.field_71106_cc >= 1.0f) {
            this.field_71106_cc = (this.field_71106_cc - 1.0f) * (float)this.func_71050_bK();
            this.func_82242_a(1);
            this.field_71106_cc /= (float)this.func_71050_bK();
        }
    }

    public int func_175138_ci() {
        return this.field_175152_f;
    }

    public void func_192024_a(ItemStack itemStack, int n) {
        this.field_71068_ca -= n;
        if (this.field_71068_ca < 0) {
            this.field_71068_ca = 0;
            this.field_71106_cc = 0.0f;
            this.field_71067_cb = 0;
        }
        this.field_175152_f = this.field_70146_Z.nextInt();
    }

    public void func_82242_a(int n) {
        this.field_71068_ca += n;
        if (this.field_71068_ca < 0) {
            this.field_71068_ca = 0;
            this.field_71106_cc = 0.0f;
            this.field_71067_cb = 0;
        }
        if (n > 0 && this.field_71068_ca % 5 == 0 && (float)this.field_82249_h < (float)this.field_70173_aa - 100.0f) {
            float f = this.field_71068_ca > 30 ? 1.0f : (float)this.field_71068_ca / 30.0f;
            this.field_70170_p.func_184148_a(null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187802_ec, this.func_184176_by(), f * 0.75f, 1.0f);
            this.field_82249_h = this.field_70173_aa;
        }
    }

    public int func_71050_bK() {
        if (this.field_71068_ca >= 30) {
            return 112 + (this.field_71068_ca - 30) * 9;
        }
        if (this.field_71068_ca >= 15) {
            return 37 + (this.field_71068_ca - 15) * 5;
        }
        return 7 + this.field_71068_ca * 2;
    }

    public void func_71020_j(float f) {
        if (this.field_71075_bZ.field_75102_a) {
            return;
        }
        if (!this.field_70170_p.field_72995_K) {
            this.field_71100_bB.func_75113_a(f);
        }
    }

    public FoodStats func_71024_bL() {
        return this.field_71100_bB;
    }

    public boolean func_71043_e(boolean bl) {
        return (bl || this.field_71100_bB.func_75121_c()) && !this.field_71075_bZ.field_75102_a;
    }

    public boolean func_70996_bM() {
        return this.func_110143_aJ() > 0.0f && this.func_110143_aJ() < this.func_110138_aP();
    }

    public boolean func_175142_cm() {
        return this.field_71075_bZ.field_75099_e;
    }

    public boolean func_175151_a(BlockPos blockPos, EnumFacing enumFacing, ItemStack itemStack) {
        if (this.field_71075_bZ.field_75099_e) {
            return true;
        }
        if (itemStack.func_190926_b()) {
            return false;
        }
        BlockPos blockPos2 = blockPos.func_177972_a(enumFacing.func_176734_d());
        Block \u26032 = this.field_70170_p.func_180495_p(blockPos2).func_177230_c();
        return itemStack.func_179547_d(\u26032) || itemStack.func_82835_x();
    }

    @Override
    protected int func_70693_a(EntityPlayer entityPlayer) {
        if (this.field_70170_p.func_82736_K().func_82766_b("keepInventory") || this.func_175149_v()) {
            return 0;
        }
        int n = this.field_71068_ca * 7;
        if (n > 100) {
            return 100;
        }
        return n;
    }

    @Override
    protected boolean func_70684_aJ() {
        return true;
    }

    @Override
    public boolean func_94059_bO() {
        return true;
    }

    @Override
    protected boolean func_70041_e_() {
        return !this.field_71075_bZ.field_75100_b;
    }

    public void func_71016_p() {
    }

    public void func_71033_a(GameType gameType) {
    }

    @Override
    public String func_70005_c_() {
        return this.field_146106_i.getName();
    }

    public InventoryEnderChest func_71005_bN() {
        return this.field_71078_a;
    }

    @Override
    public ItemStack func_184582_a(EntityEquipmentSlot entityEquipmentSlot) {
        if (entityEquipmentSlot == EntityEquipmentSlot.MAINHAND) {
            return this.field_71071_by.func_70448_g();
        }
        if (entityEquipmentSlot == EntityEquipmentSlot.OFFHAND) {
            return this.field_71071_by.field_184439_c.get(0);
        }
        if (entityEquipmentSlot.func_188453_a() == EntityEquipmentSlot.Type.ARMOR) {
            return this.field_71071_by.field_70460_b.get(entityEquipmentSlot.func_188454_b());
        }
        return ItemStack.field_190927_a;
    }

    @Override
    public void func_184201_a(EntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {
        if (entityEquipmentSlot == EntityEquipmentSlot.MAINHAND) {
            this.func_184606_a_(itemStack);
            this.field_71071_by.field_70462_a.set(this.field_71071_by.field_70461_c, itemStack);
        } else if (entityEquipmentSlot == EntityEquipmentSlot.OFFHAND) {
            this.func_184606_a_(itemStack);
            this.field_71071_by.field_184439_c.set(0, itemStack);
        } else if (entityEquipmentSlot.func_188453_a() == EntityEquipmentSlot.Type.ARMOR) {
            this.func_184606_a_(itemStack);
            this.field_71071_by.field_70460_b.set(entityEquipmentSlot.func_188454_b(), itemStack);
        }
    }

    public boolean func_191521_c(ItemStack itemStack) {
        this.func_184606_a_(itemStack);
        return this.field_71071_by.func_70441_a(itemStack);
    }

    @Override
    public Iterable<ItemStack> func_184214_aD() {
        return Lists.newArrayList((Object[])new ItemStack[]{this.func_184614_ca(), this.func_184592_cb()});
    }

    @Override
    public Iterable<ItemStack> func_184193_aE() {
        return this.field_71071_by.field_70460_b;
    }

    public boolean func_192027_g(NBTTagCompound nBTTagCompound) {
        if (this.func_184218_aH() || !this.field_70122_E || this.func_70090_H()) {
            return false;
        }
        if (this.func_192023_dk().func_82582_d()) {
            this.func_192029_h(nBTTagCompound);
            return true;
        }
        if (this.func_192025_dl().func_82582_d()) {
            this.func_192031_i(nBTTagCompound);
            return true;
        }
        return false;
    }

    protected void func_192030_dh() {
        this.func_192026_k(this.func_192023_dk());
        this.func_192029_h(new NBTTagCompound());
        this.func_192026_k(this.func_192025_dl());
        this.func_192031_i(new NBTTagCompound());
    }

    private void func_192026_k(@Nullable NBTTagCompound nBTTagCompound) {
        if (!this.field_70170_p.field_72995_K && !nBTTagCompound.func_82582_d()) {
            Entity entity = EntityList.func_75615_a(nBTTagCompound, this.field_70170_p);
            if (entity instanceof EntityTameable) {
                ((EntityTameable)entity).func_184754_b(this.field_96093_i);
            }
            entity.func_70107_b(this.field_70165_t, this.field_70163_u + (double)0.7f, this.field_70161_v);
            this.field_70170_p.func_72838_d(entity);
        }
    }

    @Override
    public boolean func_98034_c(EntityPlayer entityPlayer) {
        if (!this.func_82150_aj()) {
            return false;
        }
        if (entityPlayer.func_175149_v()) {
            return false;
        }
        Team team = this.func_96124_cp();
        return team == null || entityPlayer == null || entityPlayer.func_96124_cp() != team || !team.func_98297_h();
    }

    public abstract boolean func_175149_v();

    public abstract boolean func_184812_l_();

    @Override
    public boolean func_96092_aw() {
        return !this.field_71075_bZ.field_75100_b;
    }

    public Scoreboard func_96123_co() {
        return this.field_70170_p.func_96441_U();
    }

    @Override
    public Team func_96124_cp() {
        return this.func_96123_co().func_96509_i(this.func_70005_c_());
    }

    @Override
    public ITextComponent func_145748_c_() {
        TextComponentString textComponentString = new TextComponentString(ScorePlayerTeam.func_96667_a(this.func_96124_cp(), this.func_70005_c_()));
        textComponentString.func_150256_b().func_150241_a(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.func_70005_c_() + " "));
        textComponentString.func_150256_b().func_150209_a(this.func_174823_aP());
        textComponentString.func_150256_b().func_179989_a(this.func_70005_c_());
        return textComponentString;
    }

    @Override
    public float func_70047_e() {
        float f = 1.62f;
        if (this.func_70608_bn()) {
            f = 0.2f;
        } else if (this.func_70093_af() || this.field_70131_O == 1.65f) {
            f -= 0.08f;
        } else if (this.func_184613_cA() || this.field_70131_O == 0.6f) {
            f = 0.4f;
        }
        return f;
    }

    @Override
    public void func_110149_m(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.func_184212_Q().func_187227_b(field_184829_a, Float.valueOf(f));
    }

    @Override
    public float func_110139_bj() {
        return this.func_184212_Q().func_187225_a(field_184829_a).floatValue();
    }

    public static UUID func_146094_a(GameProfile gameProfile) {
        UUID uUID = gameProfile.getId();
        if (uUID == null) {
            uUID = EntityPlayer.func_175147_b(gameProfile.getName());
        }
        return uUID;
    }

    public static UUID func_175147_b(String string) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + string).getBytes(StandardCharsets.UTF_8));
    }

    public boolean func_175146_a(LockCode lockCode) {
        if (lockCode.func_180160_a()) {
            return true;
        }
        ItemStack itemStack = this.func_184614_ca();
        if (!itemStack.func_190926_b() && itemStack.func_82837_s()) {
            return itemStack.func_82833_r().equals(lockCode.func_180159_b());
        }
        return false;
    }

    public boolean func_175148_a(EnumPlayerModelParts enumPlayerModelParts) {
        return (this.func_184212_Q().func_187225_a(field_184827_bp) & enumPlayerModelParts.func_179327_a()) == enumPlayerModelParts.func_179327_a();
    }

    @Override
    public boolean func_174792_t_() {
        return this.func_184102_h().field_71305_c[0].func_82736_K().func_82766_b("sendCommandFeedback");
    }

    @Override
    public boolean func_174820_d(int n, ItemStack itemStack) {
        if (n >= 0 && n < this.field_71071_by.field_70462_a.size()) {
            this.field_71071_by.func_70299_a(n, itemStack);
            return true;
        }
        EntityEquipmentSlot entityEquipmentSlot = n == 100 + EntityEquipmentSlot.HEAD.func_188454_b() ? EntityEquipmentSlot.HEAD : (n == 100 + EntityEquipmentSlot.CHEST.func_188454_b() ? EntityEquipmentSlot.CHEST : (n == 100 + EntityEquipmentSlot.LEGS.func_188454_b() ? EntityEquipmentSlot.LEGS : (n == 100 + EntityEquipmentSlot.FEET.func_188454_b() ? EntityEquipmentSlot.FEET : null)));
        if (n == 98) {
            this.func_184201_a(EntityEquipmentSlot.MAINHAND, itemStack);
            return true;
        }
        if (n == 99) {
            this.func_184201_a(EntityEquipmentSlot.OFFHAND, itemStack);
            return true;
        }
        if (entityEquipmentSlot != null) {
            if (!itemStack.func_190926_b() && (itemStack.func_77973_b() instanceof ItemArmor || itemStack.func_77973_b() instanceof ItemElytra ? EntityLiving.func_184640_d(itemStack) != entityEquipmentSlot : entityEquipmentSlot != EntityEquipmentSlot.HEAD)) {
                return false;
            }
            this.field_71071_by.func_70299_a(entityEquipmentSlot.func_188454_b() + this.field_71071_by.field_70462_a.size(), itemStack);
            return true;
        }
        int \u26032 = n - 200;
        if (\u26032 >= 0 && \u26032 < this.field_71078_a.func_70302_i_()) {
            this.field_71078_a.func_70299_a(\u26032, itemStack);
            return true;
        }
        return false;
    }

    public boolean func_175140_cp() {
        return this.field_175153_bG;
    }

    public void func_175150_k(boolean bl) {
        this.field_175153_bG = bl;
    }

    @Override
    public EnumHandSide func_184591_cq() {
        return this.field_70180_af.func_187225_a(field_184828_bq) == 0 ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
    }

    public void func_184819_a(EnumHandSide enumHandSide) {
        this.field_70180_af.func_187227_b(field_184828_bq, (byte)(enumHandSide != EnumHandSide.LEFT ? 1 : 0));
    }

    public NBTTagCompound func_192023_dk() {
        return this.field_70180_af.func_187225_a(field_192032_bt);
    }

    protected void func_192029_h(NBTTagCompound nBTTagCompound) {
        this.field_70180_af.func_187227_b(field_192032_bt, nBTTagCompound);
    }

    public NBTTagCompound func_192025_dl() {
        return this.field_70180_af.func_187225_a(field_192033_bu);
    }

    protected void func_192031_i(NBTTagCompound nBTTagCompound) {
        this.field_70180_af.func_187227_b(field_192033_bu, nBTTagCompound);
    }

    public float func_184818_cX() {
        return (float)(1.0 / this.func_110148_a(SharedMonsterAttributes.field_188790_f).func_111126_e() * 20.0);
    }

    public float func_184825_o(float f) {
        return MathHelper.func_76131_a(((float)this.field_184617_aD + f) / this.func_184818_cX(), 0.0f, 1.0f);
    }

    public void func_184821_cY() {
        this.field_184617_aD = 0;
    }

    public CooldownTracker func_184811_cZ() {
        return this.field_184832_bU;
    }

    @Override
    public void func_70108_f(Entity entity) {
        if (!this.func_70608_bn()) {
            super.func_70108_f(entity);
        }
    }

    public float func_184817_da() {
        return (float)this.func_110148_a(SharedMonsterAttributes.field_188792_h).func_111126_e();
    }

    public boolean func_189808_dh() {
        return this.field_71075_bZ.field_75098_d && this.func_70003_b(2, "");
    }

    static class SleepEnemyPredicate
    implements Predicate<EntityMob> {
        private final EntityPlayer field_192387_a;

        private SleepEnemyPredicate(EntityPlayer entityPlayer) {
            this.field_192387_a = entityPlayer;
        }

        public boolean apply(@Nullable EntityMob entityMob) {
            return entityMob.func_191990_c(this.field_192387_a);
        }

        public /* synthetic */ boolean apply(@Nullable Object object) {
            return this.apply((EntityMob)object);
        }
    }

    public static enum SleepResult {
        OK,
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW,
        TOO_FAR_AWAY,
        OTHER_PROBLEM,
        NOT_SAFE;

    }

    public static enum EnumChatVisibility {
        FULL(0, "options.chat.visibility.full"),
        SYSTEM(1, "options.chat.visibility.system"),
        HIDDEN(2, "options.chat.visibility.hidden");

        private static final EnumChatVisibility[] field_151432_d;
        private final int field_151433_e;
        private final String field_151430_f;

        private EnumChatVisibility(int n2, String string2) {
            this.field_151433_e = n2;
            this.field_151430_f = string2;
        }

        public int func_151428_a() {
            return this.field_151433_e;
        }

        public static EnumChatVisibility func_151426_a(int n) {
            return field_151432_d[n % field_151432_d.length];
        }

        public String func_151429_b() {
            return this.field_151430_f;
        }

        static {
            field_151432_d = new EnumChatVisibility[EnumChatVisibility.values().length];
            EnumChatVisibility[] enumChatVisibilityArray = EnumChatVisibility.values();
            int n = enumChatVisibilityArray.length;
            for (int i = 0; i < n; ++i) {
                EnumChatVisibility enumChatVisibility;
                EnumChatVisibility.field_151432_d[enumChatVisibility.field_151433_e] = enumChatVisibility = enumChatVisibilityArray[i];
            }
        }
    }
}

