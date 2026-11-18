/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.CombatRules;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EntityLivingBase
extends Entity {
    private static final Logger field_190632_a = LogManager.getLogger();
    private static final UUID field_110156_b = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier field_110157_c = new AttributeModifier(field_110156_b, "Sprinting speed boost", 0.3f, 2).func_111168_a(false);
    protected static final DataParameter<Byte> field_184621_as = EntityDataManager.func_187226_a(EntityLivingBase.class, DataSerializers.field_187191_a);
    private static final DataParameter<Float> field_184632_c = EntityDataManager.func_187226_a(EntityLivingBase.class, DataSerializers.field_187193_c);
    private static final DataParameter<Integer> field_184633_f = EntityDataManager.func_187226_a(EntityLivingBase.class, DataSerializers.field_187192_b);
    private static final DataParameter<Boolean> field_184634_g = EntityDataManager.func_187226_a(EntityLivingBase.class, DataSerializers.field_187198_h);
    private static final DataParameter<Integer> field_184635_h = EntityDataManager.func_187226_a(EntityLivingBase.class, DataSerializers.field_187192_b);
    private AbstractAttributeMap field_110155_d;
    private final CombatTracker field_94063_bt = new CombatTracker(this);
    private final Map<Potion, PotionEffect> field_70713_bf = Maps.newHashMap();
    private final NonNullList<ItemStack> field_184630_bs = NonNullList.func_191197_a(2, ItemStack.field_190927_a);
    private final NonNullList<ItemStack> field_184631_bt = NonNullList.func_191197_a(4, ItemStack.field_190927_a);
    public boolean field_82175_bq;
    public EnumHand field_184622_au;
    public int field_110158_av;
    public int field_70720_be;
    public int field_70737_aN;
    public int field_70738_aO;
    public float field_70739_aP;
    public int field_70725_aQ;
    public float field_70732_aI;
    public float field_70733_aJ;
    protected int field_184617_aD;
    public float field_184618_aE;
    public float field_70721_aZ;
    public float field_184619_aG;
    public int field_70771_an = 20;
    public float field_70727_aS;
    public float field_70726_aT;
    public float field_70769_ao;
    public float field_70770_ap;
    public float field_70761_aq;
    public float field_70760_ar;
    public float field_70759_as;
    public float field_70758_at;
    public float field_70747_aH = 0.02f;
    protected EntityPlayer field_70717_bb;
    protected int field_70718_bc;
    protected boolean field_70729_aU;
    protected int field_70708_bq;
    protected float field_70768_au;
    protected float field_110154_aX;
    protected float field_70764_aw;
    protected float field_70763_ax;
    protected float field_70741_aB;
    protected int field_70744_aE;
    protected float field_110153_bc;
    protected boolean field_70703_bu;
    public float field_70702_br;
    public float field_70701_bs;
    public float field_191988_bg;
    public float field_70704_bt;
    protected int field_70716_bi;
    protected double field_184623_bh;
    protected double field_184624_bi;
    protected double field_184625_bj;
    protected double field_184626_bk;
    protected double field_70709_bj;
    private boolean field_70752_e = true;
    private EntityLivingBase field_70755_b;
    private int field_70756_c;
    private EntityLivingBase field_110150_bn;
    private int field_142016_bo;
    private float field_70746_aG;
    private int field_70773_bE;
    private float field_110151_bq;
    protected ItemStack field_184627_bm = ItemStack.field_190927_a;
    protected int field_184628_bn;
    protected int field_184629_bo;
    private BlockPos field_184620_bC;
    private DamageSource field_189750_bF;
    private long field_189751_bG;

    @Override
    public void func_174812_G() {
        this.func_70097_a(DamageSource.field_76380_i, Float.MAX_VALUE);
    }

    public EntityLivingBase(World world) {
        super(world);
        this.func_110147_ax();
        this.func_70606_j(this.func_110138_aP());
        this.field_70156_m = true;
        this.field_70770_ap = (float)((Math.random() + 1.0) * (double)0.01f);
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.field_70769_ao = (float)Math.random() * 12398.0f;
        this.field_70759_as = this.field_70177_z = (float)(Math.random() * 6.2831854820251465);
        this.field_70138_W = 0.6f;
    }

    @Override
    protected void func_70088_a() {
        this.field_70180_af.func_187214_a(field_184621_as, (byte)0);
        this.field_70180_af.func_187214_a(field_184633_f, 0);
        this.field_70180_af.func_187214_a(field_184634_g, false);
        this.field_70180_af.func_187214_a(field_184635_h, 0);
        this.field_70180_af.func_187214_a(field_184632_c, Float.valueOf(1.0f));
    }

    protected void func_110147_ax() {
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111267_a);
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111266_c);
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111263_d);
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_188791_g);
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_189429_h);
    }

    @Override
    protected void func_184231_a(double d, boolean bl, IBlockState iBlockState, BlockPos blockPos) {
        if (!this.func_70090_H()) {
            this.func_70072_I();
        }
        if (!this.field_70170_p.field_72995_K && this.field_70143_R > 3.0f && bl) {
            float f = MathHelper.func_76123_f(this.field_70143_R - 3.0f);
            if (iBlockState.func_185904_a() != Material.field_151579_a) {
                double d2 = Math.min((double)(0.2f + f / 15.0f), 2.5);
                int \u26032 = (int)(150.0 * d2);
                ((WorldServer)this.field_70170_p).func_175739_a(EnumParticleTypes.BLOCK_DUST, this.field_70165_t, this.field_70163_u, this.field_70161_v, \u26032, 0.0, 0.0, 0.0, 0.15f, Block.func_176210_f(iBlockState));
            }
        }
        super.func_184231_a(d, bl, iBlockState, blockPos);
    }

    public boolean func_70648_aU() {
        return false;
    }

    @Override
    public void func_70030_z() {
        this.field_70732_aI = this.field_70733_aJ;
        super.func_70030_z();
        this.field_70170_p.field_72984_F.func_76320_a("livingEntityBaseTick");
        boolean bl = this instanceof EntityPlayer;
        if (this.func_70089_S()) {
            if (this.func_70094_T()) {
                this.func_70097_a(DamageSource.field_76368_d, 1.0f);
            } else if (bl && !this.field_70170_p.func_175723_af().func_177743_a(this.func_174813_aQ()) && (\u2603 = this.field_70170_p.func_175723_af().func_177745_a(this) + this.field_70170_p.func_175723_af().func_177742_m()) < 0.0 && (\u2603 = this.field_70170_p.func_175723_af().func_177727_n()) > 0.0) {
                this.func_70097_a(DamageSource.field_76368_d, Math.max(1, MathHelper.func_76128_c(-\u2603 * \u2603)));
            }
        }
        if (this.func_70045_F() || this.field_70170_p.field_72995_K) {
            this.func_70066_B();
        }
        boolean bl2 = \u2603 = bl && ((EntityPlayer)this).field_71075_bZ.field_75102_a;
        if (this.func_70089_S()) {
            BlockPos blockPos;
            if (this.func_70055_a(Material.field_151586_h)) {
                if (!(this.func_70648_aU() || this.func_70644_a(MobEffects.field_76427_o) || \u2603)) {
                    this.func_70050_g(this.func_70682_h(this.func_70086_ai()));
                    if (this.func_70086_ai() == -20) {
                        this.func_70050_g(0);
                        for (int i = 0; i < 8; ++i) {
                            float f = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                            \u2603 = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                            \u2603 = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                            this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_BUBBLE, this.field_70165_t + (double)f, this.field_70163_u + (double)\u2603, this.field_70161_v + (double)\u2603, this.field_70159_w, this.field_70181_x, this.field_70179_y, new int[0]);
                        }
                        this.func_70097_a(DamageSource.field_76369_e, 2.0f);
                    }
                }
                if (!this.field_70170_p.field_72995_K && this.func_184218_aH() && this.func_184187_bx() instanceof EntityLivingBase) {
                    this.func_184210_p();
                }
            } else {
                this.func_70050_g(300);
            }
            if (!this.field_70170_p.field_72995_K && !Objects.equal((Object)this.field_184620_bC, (Object)(blockPos = new BlockPos(this)))) {
                this.field_184620_bC = blockPos;
                this.func_184594_b(blockPos);
            }
        }
        if (this.func_70089_S() && this.func_70026_G()) {
            this.func_70066_B();
        }
        this.field_70727_aS = this.field_70726_aT;
        if (this.field_70737_aN > 0) {
            --this.field_70737_aN;
        }
        if (this.field_70172_ad > 0 && !(this instanceof EntityPlayerMP)) {
            --this.field_70172_ad;
        }
        if (this.func_110143_aJ() <= 0.0f) {
            this.func_70609_aI();
        }
        if (this.field_70718_bc > 0) {
            --this.field_70718_bc;
        } else {
            this.field_70717_bb = null;
        }
        if (this.field_110150_bn != null && !this.field_110150_bn.func_70089_S()) {
            this.field_110150_bn = null;
        }
        if (this.field_70755_b != null) {
            if (!this.field_70755_b.func_70089_S()) {
                this.func_70604_c(null);
            } else if (this.field_70173_aa - this.field_70756_c > 100) {
                this.func_70604_c(null);
            }
        }
        this.func_70679_bo();
        this.field_70763_ax = this.field_70764_aw;
        this.field_70760_ar = this.field_70761_aq;
        this.field_70758_at = this.field_70759_as;
        this.field_70126_B = this.field_70177_z;
        this.field_70127_C = this.field_70125_A;
        this.field_70170_p.field_72984_F.func_76319_b();
    }

    protected void func_184594_b(BlockPos blockPos) {
        int n = EnchantmentHelper.func_185284_a(Enchantments.field_185301_j, this);
        if (n > 0) {
            EnchantmentFrostWalker.func_185266_a(this, this.field_70170_p, blockPos, n);
        }
    }

    public boolean func_70631_g_() {
        return false;
    }

    protected void func_70609_aI() {
        ++this.field_70725_aQ;
        if (this.field_70725_aQ == 20) {
            if (!this.field_70170_p.field_72995_K && (this.func_70684_aJ() || this.field_70718_bc > 0 && this.func_146066_aG() && this.field_70170_p.func_82736_K().func_82766_b("doMobLoot"))) {
                int n;
                for (i = this.func_70693_a(this.field_70717_bb); i > 0; i -= n) {
                    n = EntityXPOrb.func_70527_a(i);
                    this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, n));
                }
            }
            this.func_70106_y();
            for (int i = 0; i < 20; ++i) {
                double d = this.field_70146_Z.nextGaussian() * 0.02;
                \u2603 = this.field_70146_Z.nextGaussian() * 0.02;
                \u2603 = this.field_70146_Z.nextGaussian() * 0.02;
                this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_NORMAL, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0f) - (double)this.field_70130_N, this.field_70163_u + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0f) - (double)this.field_70130_N, d, \u2603, \u2603, new int[0]);
            }
        }
    }

    protected boolean func_146066_aG() {
        return !this.func_70631_g_();
    }

    protected int func_70682_h(int n) {
        \u2603 = EnchantmentHelper.func_185292_c(this);
        if (\u2603 > 0 && this.field_70146_Z.nextInt(\u2603 + 1) > 0) {
            return n;
        }
        return n - 1;
    }

    protected int func_70693_a(EntityPlayer entityPlayer) {
        return 0;
    }

    protected boolean func_70684_aJ() {
        return false;
    }

    public Random func_70681_au() {
        return this.field_70146_Z;
    }

    @Nullable
    public EntityLivingBase func_70643_av() {
        return this.field_70755_b;
    }

    public int func_142015_aE() {
        return this.field_70756_c;
    }

    public void func_70604_c(@Nullable EntityLivingBase entityLivingBase) {
        this.field_70755_b = entityLivingBase;
        this.field_70756_c = this.field_70173_aa;
    }

    public EntityLivingBase func_110144_aD() {
        return this.field_110150_bn;
    }

    public int func_142013_aG() {
        return this.field_142016_bo;
    }

    public void func_130011_c(Entity entity) {
        this.field_110150_bn = entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
        this.field_142016_bo = this.field_70173_aa;
    }

    public int func_70654_ax() {
        return this.field_70708_bq;
    }

    protected void func_184606_a_(ItemStack itemStack) {
        if (itemStack.func_190926_b()) {
            return;
        }
        SoundEvent soundEvent = SoundEvents.field_187719_p;
        Item \u26032 = itemStack.func_77973_b();
        if (\u26032 instanceof ItemArmor) {
            soundEvent = ((ItemArmor)\u26032).func_82812_d().func_185017_b();
        } else if (\u26032 == Items.field_185160_cR) {
            soundEvent = SoundEvents.field_191258_p;
        }
        this.func_184185_a(soundEvent, 1.0f, 1.0f);
    }

    @Override
    public void func_70014_b(NBTTagCompound nBTTagCompound2) {
        NBTTagCompound nBTTagCompound2;
        ItemStack itemStack;
        nBTTagCompound2.func_74776_a("Health", this.func_110143_aJ());
        nBTTagCompound2.func_74777_a("HurtTime", (short)this.field_70737_aN);
        nBTTagCompound2.func_74768_a("HurtByTimestamp", this.field_70756_c);
        nBTTagCompound2.func_74777_a("DeathTime", (short)this.field_70725_aQ);
        nBTTagCompound2.func_74776_a("AbsorptionAmount", this.func_110139_bj());
        for (EntityEquipmentSlot entityEquipmentSlot : EntityEquipmentSlot.values()) {
            itemStack = this.func_184582_a(entityEquipmentSlot);
            if (itemStack.func_190926_b()) continue;
            this.func_110140_aT().func_111148_a(itemStack.func_111283_C(entityEquipmentSlot));
        }
        nBTTagCompound2.func_74782_a("Attributes", SharedMonsterAttributes.func_111257_a(this.func_110140_aT()));
        for (EntityEquipmentSlot entityEquipmentSlot : EntityEquipmentSlot.values()) {
            itemStack = this.func_184582_a(entityEquipmentSlot);
            if (itemStack.func_190926_b()) continue;
            this.func_110140_aT().func_111147_b(itemStack.func_111283_C(entityEquipmentSlot));
        }
        if (!this.field_70713_bf.isEmpty()) {
            NBTTagList nBTTagList = new NBTTagList();
            for (PotionEffect potionEffect : this.field_70713_bf.values()) {
                nBTTagList.func_74742_a(potionEffect.func_82719_a(new NBTTagCompound()));
            }
            nBTTagCompound2.func_74782_a("ActiveEffects", nBTTagList);
        }
        nBTTagCompound2.func_74757_a("FallFlying", this.func_184613_cA());
    }

    @Override
    public void func_70037_a(NBTTagCompound nBTTagCompound) {
        int \u26033;
        Object object;
        this.func_110149_m(nBTTagCompound.func_74760_g("AbsorptionAmount"));
        if (nBTTagCompound.func_150297_b("Attributes", 9) && this.field_70170_p != null && !this.field_70170_p.field_72995_K) {
            SharedMonsterAttributes.func_151475_a(this.func_110140_aT(), nBTTagCompound.func_150295_c("Attributes", 10));
        }
        if (nBTTagCompound.func_150297_b("ActiveEffects", 9)) {
            object = nBTTagCompound.func_150295_c("ActiveEffects", 10);
            for (\u26033 = 0; \u26033 < ((NBTTagList)object).func_74745_c(); ++\u26033) {
                NBTTagCompound nBTTagCompound2 = ((NBTTagList)object).func_150305_b(\u26033);
                PotionEffect \u26032 = PotionEffect.func_82722_b(nBTTagCompound2);
                if (\u26032 == null) continue;
                this.field_70713_bf.put(\u26032.func_188419_a(), \u26032);
            }
        }
        if (nBTTagCompound.func_150297_b("Health", 99)) {
            this.func_70606_j(nBTTagCompound.func_74760_g("Health"));
        }
        this.field_70737_aN = nBTTagCompound.func_74765_d("HurtTime");
        this.field_70725_aQ = nBTTagCompound.func_74765_d("DeathTime");
        this.field_70756_c = nBTTagCompound.func_74762_e("HurtByTimestamp");
        if (nBTTagCompound.func_150297_b("Team", 8)) {
            object = nBTTagCompound.func_74779_i("Team");
            \u26033 = this.field_70170_p.func_96441_U().func_151392_a(this.func_189512_bd(), (String)object) ? 1 : 0;
            if (\u26033 == 0) {
                field_190632_a.warn("Unable to add mob to team \"" + (String)object + "\" (that team probably doesn't exist)");
            }
        }
        if (nBTTagCompound.func_74767_n("FallFlying")) {
            this.func_70052_a(7, true);
        }
    }

    protected void func_70679_bo() {
        Iterator<Potion> iterator = this.field_70713_bf.keySet().iterator();
        try {
            while (iterator.hasNext()) {
                Potion potion = iterator.next();
                PotionEffect \u26032 = this.field_70713_bf.get(potion);
                if (!\u26032.func_76455_a(this)) {
                    if (this.field_70170_p.field_72995_K) continue;
                    iterator.remove();
                    this.func_70688_c(\u26032);
                    continue;
                }
                if (\u26032.func_76459_b() % 600 != 0) continue;
                this.func_70695_b(\u26032, false);
            }
        }
        catch (ConcurrentModificationException potion) {
            // empty catch block
        }
        if (this.field_70752_e) {
            if (!this.field_70170_p.field_72995_K) {
                this.func_175135_B();
            }
            this.field_70752_e = false;
        }
        int n = this.field_70180_af.func_187225_a(field_184633_f);
        boolean \u26033 = this.field_70180_af.func_187225_a(field_184634_g);
        if (n > 0) {
            boolean bl = this.func_82150_aj() ? this.field_70146_Z.nextInt(15) == 0 : this.field_70146_Z.nextBoolean();
            if (\u26033) {
                bl &= this.field_70146_Z.nextInt(5) == 0;
            }
            if (bl && n > 0) {
                double d = (double)(n >> 16 & 0xFF) / 255.0;
                \u2603 = (double)(n >> 8 & 0xFF) / 255.0;
                \u2603 = (double)(n >> 0 & 0xFF) / 255.0;
                this.field_70170_p.func_175688_a(\u26033 ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB, this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5) * (double)this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5) * (double)this.field_70130_N, d, \u2603, \u2603, new int[0]);
            }
        }
    }

    protected void func_175135_B() {
        if (this.field_70713_bf.isEmpty()) {
            this.func_175133_bi();
            this.func_82142_c(false);
        } else {
            Collection<PotionEffect> collection = this.field_70713_bf.values();
            this.field_70180_af.func_187227_b(field_184634_g, EntityLivingBase.func_184593_a(collection));
            this.field_70180_af.func_187227_b(field_184633_f, PotionUtils.func_185181_a(collection));
            this.func_82142_c(this.func_70644_a(MobEffects.field_76441_p));
        }
    }

    public static boolean func_184593_a(Collection<PotionEffect> collection) {
        for (PotionEffect potionEffect : collection) {
            if (potionEffect.func_82720_e()) continue;
            return false;
        }
        return true;
    }

    protected void func_175133_bi() {
        this.field_70180_af.func_187227_b(field_184634_g, false);
        this.field_70180_af.func_187227_b(field_184633_f, 0);
    }

    public void func_70674_bp() {
        if (this.field_70170_p.field_72995_K) {
            return;
        }
        Iterator<PotionEffect> iterator = this.field_70713_bf.values().iterator();
        while (iterator.hasNext()) {
            this.func_70688_c(iterator.next());
            iterator.remove();
        }
    }

    public Collection<PotionEffect> func_70651_bq() {
        return this.field_70713_bf.values();
    }

    public Map<Potion, PotionEffect> func_193076_bZ() {
        return this.field_70713_bf;
    }

    public boolean func_70644_a(Potion potion) {
        return this.field_70713_bf.containsKey(potion);
    }

    @Nullable
    public PotionEffect func_70660_b(Potion potion) {
        return this.field_70713_bf.get(potion);
    }

    public void func_70690_d(PotionEffect potionEffect) {
        if (!this.func_70687_e(potionEffect)) {
            return;
        }
        \u2603 = this.field_70713_bf.get(potionEffect.func_188419_a());
        if (\u2603 == null) {
            this.field_70713_bf.put(potionEffect.func_188419_a(), potionEffect);
            this.func_70670_a(potionEffect);
        } else {
            \u2603.func_76452_a(potionEffect);
            this.func_70695_b(\u2603, true);
        }
    }

    public boolean func_70687_e(PotionEffect potionEffect) {
        return this.func_70668_bt() != EnumCreatureAttribute.UNDEAD || (\u2603 = potionEffect.func_188419_a()) != MobEffects.field_76428_l && \u2603 != MobEffects.field_76436_u;
    }

    public boolean func_70662_br() {
        return this.func_70668_bt() == EnumCreatureAttribute.UNDEAD;
    }

    @Nullable
    public PotionEffect func_184596_c(@Nullable Potion potion) {
        return this.field_70713_bf.remove(potion);
    }

    public void func_184589_d(Potion potion) {
        PotionEffect potionEffect = this.func_184596_c(potion);
        if (potionEffect != null) {
            this.func_70688_c(potionEffect);
        }
    }

    protected void func_70670_a(PotionEffect potionEffect) {
        this.field_70752_e = true;
        if (!this.field_70170_p.field_72995_K) {
            potionEffect.func_188419_a().func_111185_a(this, this.func_110140_aT(), potionEffect.func_76458_c());
        }
    }

    protected void func_70695_b(PotionEffect potionEffect, boolean bl) {
        this.field_70752_e = true;
        if (bl && !this.field_70170_p.field_72995_K) {
            Potion potion = potionEffect.func_188419_a();
            potion.func_111187_a(this, this.func_110140_aT(), potionEffect.func_76458_c());
            potion.func_111185_a(this, this.func_110140_aT(), potionEffect.func_76458_c());
        }
    }

    protected void func_70688_c(PotionEffect potionEffect) {
        this.field_70752_e = true;
        if (!this.field_70170_p.field_72995_K) {
            potionEffect.func_188419_a().func_111187_a(this, this.func_110140_aT(), potionEffect.func_76458_c());
        }
    }

    public void func_70691_i(float f) {
        \u2603 = this.func_110143_aJ();
        if (\u2603 > 0.0f) {
            this.func_70606_j(\u2603 + f);
        }
    }

    public final float func_110143_aJ() {
        return this.field_70180_af.func_187225_a(field_184632_c).floatValue();
    }

    public void func_70606_j(float f) {
        this.field_70180_af.func_187227_b(field_184632_c, Float.valueOf(MathHelper.func_76131_a(f, 0.0f, this.func_110138_aP())));
    }

    @Override
    public boolean func_70097_a(DamageSource damageSource2, float f2) {
        DamageSource damageSource2;
        if (this.func_180431_b(damageSource2)) {
            return false;
        }
        if (this.field_70170_p.field_72995_K) {
            return false;
        }
        this.field_70708_bq = 0;
        if (this.func_110143_aJ() <= 0.0f) {
            return false;
        }
        if (damageSource2.func_76347_k() && this.func_70644_a(MobEffects.field_76426_n)) {
            return false;
        }
        \u2603 = f2;
        if (!(damageSource2 != DamageSource.field_82728_o && damageSource2 != DamageSource.field_82729_p || this.func_184582_a(EntityEquipmentSlot.HEAD).func_190926_b())) {
            this.func_184582_a(EntityEquipmentSlot.HEAD).func_77972_a((int)(f2 * 4.0f + this.field_70146_Z.nextFloat() * f2 * 2.0f), this);
            f2 *= 0.75f;
        }
        boolean \u26032 = false;
        if (f2 > 0.0f && this.func_184583_d(damageSource2)) {
            this.func_184590_k(f2);
            float f2 = 0.0f;
            if (!damageSource2.func_76352_a() && (\u2603 = damageSource2.func_76364_f()) instanceof EntityLivingBase) {
                this.func_190629_c((EntityLivingBase)\u2603);
            }
            \u26032 = true;
        }
        this.field_70721_aZ = 1.5f;
        boolean bl = true;
        if ((float)this.field_70172_ad > (float)this.field_70771_an / 2.0f) {
            if (f2 <= this.field_110153_bc) {
                return false;
            }
            this.func_70665_d(damageSource2, f2 - this.field_110153_bc);
            this.field_110153_bc = f2;
            bl = false;
        } else {
            this.field_110153_bc = f2;
            this.field_70172_ad = this.field_70771_an;
            this.func_70665_d(damageSource2, f2);
            this.field_70737_aN = this.field_70738_aO = 10;
        }
        this.field_70739_aP = 0.0f;
        Entity \u26033 = damageSource2.func_76346_g();
        if (\u26033 != null) {
            if (\u26033 instanceof EntityLivingBase) {
                this.func_70604_c((EntityLivingBase)\u26033);
            }
            if (\u26033 instanceof EntityPlayer) {
                this.field_70718_bc = 100;
                this.field_70717_bb = (EntityPlayer)\u26033;
            } else if (\u26033 instanceof EntityWolf && ((EntityTameable)(object = (EntityWolf)\u26033)).func_70909_n()) {
                this.field_70718_bc = 100;
                this.field_70717_bb = null;
            }
        }
        if (bl) {
            if (\u26032) {
                this.field_70170_p.func_72960_a(this, (byte)29);
            } else if (damageSource2 instanceof EntityDamageSource && ((EntityDamageSource)damageSource2).func_180139_w()) {
                this.field_70170_p.func_72960_a(this, (byte)33);
            } else {
                byte by = damageSource2 == DamageSource.field_76369_e ? (byte)36 : (damageSource2.func_76347_k() ? (byte)37 : 2);
                this.field_70170_p.func_72960_a(this, by);
            }
            if (damageSource2 != DamageSource.field_76369_e && (!\u26032 || f2 > 0.0f)) {
                this.func_70018_K();
            }
            if (\u26033 != null) {
                double d = \u26033.field_70165_t - this.field_70165_t;
                \u2603 = \u26033.field_70161_v - this.field_70161_v;
                while (d * d + \u2603 * \u2603 < 1.0E-4) {
                    d = (Math.random() - Math.random()) * 0.01;
                    \u2603 = (Math.random() - Math.random()) * 0.01;
                }
                this.field_70739_aP = (float)(MathHelper.func_181159_b(\u2603, d) * 57.2957763671875 - (double)this.field_70177_z);
                this.func_70653_a(\u26033, 0.4f, d, \u2603);
            } else {
                this.field_70739_aP = (int)(Math.random() * 2.0) * 180;
            }
        }
        if (this.func_110143_aJ() <= 0.0f) {
            if (!this.func_190628_d(damageSource2)) {
                Object object = this.func_184615_bR();
                if (bl && object != null) {
                    this.func_184185_a((SoundEvent)object, this.func_70599_aP(), this.func_70647_i());
                }
                this.func_70645_a(damageSource2);
            }
        } else if (bl) {
            this.func_184581_c(damageSource2);
        }
        boolean bl2 = \u2603 = !\u26032 || f2 > 0.0f;
        if (\u2603) {
            this.field_189750_bF = damageSource2;
            this.field_189751_bG = this.field_70170_p.func_82737_E();
        }
        if (this instanceof EntityPlayerMP) {
            CriteriaTriggers.field_192128_h.func_192200_a((EntityPlayerMP)this, damageSource2, \u2603, f2, \u26032);
        }
        if (\u26033 instanceof EntityPlayerMP) {
            CriteriaTriggers.field_192127_g.func_192220_a((EntityPlayerMP)\u26033, this, damageSource2, \u2603, f2, \u26032);
        }
        return \u2603;
    }

    protected void func_190629_c(EntityLivingBase entityLivingBase) {
        entityLivingBase.func_70653_a(this, 0.5f, this.field_70165_t - entityLivingBase.field_70165_t, this.field_70161_v - entityLivingBase.field_70161_v);
    }

    private boolean func_190628_d(DamageSource damageSource) {
        if (damageSource.func_76357_e()) {
            return false;
        }
        ItemStack itemStack = null;
        for (EnumHand enumHand : EnumHand.values()) {
            ItemStack itemStack2 = this.func_184586_b(enumHand);
            if (itemStack2.func_77973_b() != Items.field_190929_cY) continue;
            itemStack = itemStack2.func_77946_l();
            itemStack2.func_190918_g(1);
            break;
        }
        if (itemStack != null) {
            if (this instanceof EntityPlayerMP) {
                EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this;
                entityPlayerMP.func_71029_a(StatList.func_188057_b(Items.field_190929_cY));
                CriteriaTriggers.field_193130_A.func_193187_a(entityPlayerMP, itemStack);
            }
            this.func_70606_j(1.0f);
            this.func_70674_bp();
            this.func_70690_d(new PotionEffect(MobEffects.field_76428_l, 900, 1));
            this.func_70690_d(new PotionEffect(MobEffects.field_76444_x, 100, 1));
            this.field_70170_p.func_72960_a(this, (byte)35);
        }
        return itemStack != null;
    }

    @Nullable
    public DamageSource func_189748_bU() {
        if (this.field_70170_p.func_82737_E() - this.field_189751_bG > 40L) {
            this.field_189750_bF = null;
        }
        return this.field_189750_bF;
    }

    protected void func_184581_c(DamageSource damageSource) {
        SoundEvent soundEvent = this.func_184601_bQ(damageSource);
        if (soundEvent != null) {
            this.func_184185_a(soundEvent, this.func_70599_aP(), this.func_70647_i());
        }
    }

    private boolean func_184583_d(DamageSource damageSource) {
        if (!damageSource.func_76363_c() && this.func_184585_cz() && (\u2603 = damageSource.func_188404_v()) != null) {
            Vec3d vec3d = this.func_70676_i(1.0f);
            \u2603 = \u2603.func_72444_a(new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v)).func_72432_b();
            \u2603 = new Vec3d(\u2603.field_72450_a, 0.0, \u2603.field_72449_c);
            if (\u2603.func_72430_b(vec3d) < 0.0) {
                return true;
            }
        }
        return false;
    }

    public void func_70669_a(ItemStack itemStack) {
        this.func_184185_a(SoundEvents.field_187635_cQ, 0.8f, 0.8f + this.field_70170_p.field_73012_v.nextFloat() * 0.4f);
        for (int i = 0; i < 5; ++i) {
            Vec3d vec3d = new Vec3d(((double)this.field_70146_Z.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.func_178789_a(-this.field_70125_A * ((float)Math.PI / 180));
            vec3d = vec3d.func_178785_b(-this.field_70177_z * ((float)Math.PI / 180));
            double \u26032 = (double)(-this.field_70146_Z.nextFloat()) * 0.6 - 0.3;
            \u2603 = new Vec3d(((double)this.field_70146_Z.nextFloat() - 0.5) * 0.3, \u26032, 0.6);
            \u2603 = \u2603.func_178789_a(-this.field_70125_A * ((float)Math.PI / 180));
            \u2603 = \u2603.func_178785_b(-this.field_70177_z * ((float)Math.PI / 180));
            \u2603 = \u2603.func_72441_c(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
            this.field_70170_p.func_175688_a(EnumParticleTypes.ITEM_CRACK, \u2603.field_72450_a, \u2603.field_72448_b, \u2603.field_72449_c, vec3d.field_72450_a, vec3d.field_72448_b + 0.05, vec3d.field_72449_c, Item.func_150891_b(itemStack.func_77973_b()));
        }
    }

    public void func_70645_a(DamageSource damageSource) {
        if (this.field_70729_aU) {
            return;
        }
        Entity entity = damageSource.func_76346_g();
        EntityLivingBase \u26032 = this.func_94060_bK();
        if (this.field_70744_aE >= 0 && \u26032 != null) {
            \u26032.func_191956_a(this, this.field_70744_aE, damageSource);
        }
        if (entity != null) {
            entity.func_70074_a(this);
        }
        this.field_70729_aU = true;
        this.func_110142_aN().func_94549_h();
        if (!this.field_70170_p.field_72995_K) {
            int n = 0;
            if (entity instanceof EntityPlayer) {
                n = EnchantmentHelper.func_185283_h((EntityLivingBase)entity);
            }
            if (this.func_146066_aG() && this.field_70170_p.func_82736_K().func_82766_b("doMobLoot")) {
                boolean bl = this.field_70718_bc > 0;
                this.func_184610_a(bl, n, damageSource);
            }
        }
        this.field_70170_p.func_72960_a(this, (byte)3);
    }

    protected void func_184610_a(boolean bl, int n, DamageSource damageSource) {
        this.func_70628_a(bl, n);
        this.func_82160_b(bl, n);
    }

    protected void func_82160_b(boolean bl, int n) {
    }

    public void func_70653_a(Entity entity, float f, double d, double d2) {
        if (this.field_70146_Z.nextDouble() < this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111126_e()) {
            return;
        }
        this.field_70160_al = true;
        float f2 = MathHelper.func_76133_a(d * d + d2 * d2);
        this.field_70159_w /= 2.0;
        this.field_70179_y /= 2.0;
        this.field_70159_w -= d / (double)f2 * (double)f;
        this.field_70179_y -= d2 / (double)f2 * (double)f;
        if (this.field_70122_E) {
            this.field_70181_x /= 2.0;
            this.field_70181_x += (double)f;
            if (this.field_70181_x > (double)0.4f) {
                this.field_70181_x = 0.4f;
            }
        }
    }

    @Nullable
    protected SoundEvent func_184601_bQ(DamageSource damageSource) {
        return SoundEvents.field_187543_bD;
    }

    @Nullable
    protected SoundEvent func_184615_bR() {
        return SoundEvents.field_187661_by;
    }

    protected SoundEvent func_184588_d(int n) {
        if (n > 4) {
            return SoundEvents.field_187655_bw;
        }
        return SoundEvents.field_187545_bE;
    }

    protected void func_70628_a(boolean bl, int n) {
    }

    public boolean func_70617_f_() {
        int n = MathHelper.func_76128_c(this.field_70165_t);
        \u2603 = MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);
        \u2603 = MathHelper.func_76128_c(this.field_70161_v);
        if (this instanceof EntityPlayer && ((EntityPlayer)this).func_175149_v()) {
            return false;
        }
        BlockPos \u26032 = new BlockPos(n, \u2603, \u2603);
        IBlockState \u26033 = this.field_70170_p.func_180495_p(\u26032);
        Block \u26034 = \u26033.func_177230_c();
        if (\u26034 == Blocks.field_150468_ap || \u26034 == Blocks.field_150395_bd) {
            return true;
        }
        return \u26034 instanceof BlockTrapDoor && this.func_184604_a(\u26032, \u26033);
    }

    private boolean func_184604_a(BlockPos blockPos, IBlockState iBlockState) {
        return iBlockState.func_177229_b(BlockTrapDoor.field_176283_b) != false && (\u2603 = this.field_70170_p.func_180495_p(blockPos.func_177977_b())).func_177230_c() == Blocks.field_150468_ap && \u2603.func_177229_b(BlockLadder.field_176382_a) == iBlockState.func_177229_b(BlockTrapDoor.field_176284_a);
    }

    @Override
    public boolean func_70089_S() {
        return !this.field_70128_L && this.func_110143_aJ() > 0.0f;
    }

    @Override
    public void func_180430_e(float f, float f2) {
        super.func_180430_e(f, f2);
        PotionEffect potionEffect = this.func_70660_b(MobEffects.field_76430_j);
        float \u26032 = potionEffect == null ? 0.0f : (float)(potionEffect.func_76458_c() + 1);
        int \u26033 = MathHelper.func_76123_f((f - 3.0f - \u26032) * f2);
        if (\u26033 > 0) {
            this.func_184185_a(this.func_184588_d(\u26033), 1.0f, 1.0f);
            this.func_70097_a(DamageSource.field_76379_h, \u26033);
            int n = MathHelper.func_76128_c(this.field_70165_t);
            \u2603 = MathHelper.func_76128_c(this.field_70163_u - (double)0.2f);
            \u2603 = MathHelper.func_76128_c(this.field_70161_v);
            IBlockState \u26034 = this.field_70170_p.func_180495_p(new BlockPos(n, \u2603, \u2603));
            if (\u26034.func_185904_a() != Material.field_151579_a) {
                SoundType soundType = \u26034.func_177230_c().func_185467_w();
                this.func_184185_a(soundType.func_185842_g(), soundType.func_185843_a() * 0.5f, soundType.func_185847_b() * 0.75f);
            }
        }
    }

    @Override
    public void func_70057_ab() {
        this.field_70737_aN = this.field_70738_aO = 10;
        this.field_70739_aP = 0.0f;
    }

    public int func_70658_aO() {
        IAttributeInstance iAttributeInstance = this.func_110148_a(SharedMonsterAttributes.field_188791_g);
        return MathHelper.func_76128_c(iAttributeInstance.func_111126_e());
    }

    protected void func_70675_k(float f) {
    }

    protected void func_184590_k(float f) {
    }

    protected float func_70655_b(DamageSource damageSource, float f) {
        if (!damageSource.func_76363_c()) {
            this.func_70675_k(f);
            f = CombatRules.func_189427_a(f, this.func_70658_aO(), (float)this.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
        }
        return f;
    }

    protected float func_70672_c(DamageSource damageSource, float \u260332) {
        float \u260332;
        if (damageSource.func_151517_h()) {
            return \u260332;
        }
        if (this.func_70644_a(MobEffects.field_76429_m) && damageSource != DamageSource.field_76380_i) {
            int n = (this.func_70660_b(MobEffects.field_76429_m).func_76458_c() + 1) * 5;
            \u2603 = 25 - n;
            float \u26032 = \u260332 * (float)\u2603;
            \u260332 = \u26032 / 25.0f;
        }
        if (\u260332 <= 0.0f) {
            return 0.0f;
        }
        n = EnchantmentHelper.func_77508_a(this.func_184193_aE(), damageSource);
        if (n > 0) {
            \u260332 = CombatRules.func_188401_b(\u260332, n);
        }
        return \u260332;
    }

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
        \u2603 = this.func_110143_aJ();
        this.func_70606_j(\u2603 - f);
        this.func_110142_aN().func_94547_a(damageSource, \u2603, f);
        this.func_110149_m(this.func_110139_bj() - f);
    }

    public CombatTracker func_110142_aN() {
        return this.field_94063_bt;
    }

    @Nullable
    public EntityLivingBase func_94060_bK() {
        if (this.field_94063_bt.func_94550_c() != null) {
            return this.field_94063_bt.func_94550_c();
        }
        if (this.field_70717_bb != null) {
            return this.field_70717_bb;
        }
        if (this.field_70755_b != null) {
            return this.field_70755_b;
        }
        return null;
    }

    public final float func_110138_aP() {
        return (float)this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111126_e();
    }

    public final int func_85035_bI() {
        return this.field_70180_af.func_187225_a(field_184635_h);
    }

    public final void func_85034_r(int n) {
        this.field_70180_af.func_187227_b(field_184635_h, n);
    }

    private int func_82166_i() {
        if (this.func_70644_a(MobEffects.field_76422_e)) {
            return 6 - (1 + this.func_70660_b(MobEffects.field_76422_e).func_76458_c());
        }
        if (this.func_70644_a(MobEffects.field_76419_f)) {
            return 6 + (1 + this.func_70660_b(MobEffects.field_76419_f).func_76458_c()) * 2;
        }
        return 6;
    }

    public void func_184609_a(EnumHand enumHand) {
        if (!this.field_82175_bq || this.field_110158_av >= this.func_82166_i() / 2 || this.field_110158_av < 0) {
            this.field_110158_av = -1;
            this.field_82175_bq = true;
            this.field_184622_au = enumHand;
            if (this.field_70170_p instanceof WorldServer) {
                ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new SPacketAnimation(this, enumHand == EnumHand.MAIN_HAND ? 0 : 3));
            }
        }
    }

    @Override
    public void func_70103_a(byte by2) {
        byte by2;
        boolean bl = by2 == 33;
        \u2603 = by2 == 36;
        boolean bl2 = \u2603 = by2 == 37;
        if (by2 == 2 || bl || \u2603 || \u2603) {
            this.field_70721_aZ = 1.5f;
            this.field_70172_ad = this.field_70771_an;
            this.field_70737_aN = this.field_70738_aO = 10;
            this.field_70739_aP = 0.0f;
            if (bl) {
                this.func_184185_a(SoundEvents.field_187903_gc, this.func_70599_aP(), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f + 1.0f);
            }
            if ((\u2603 = this.func_184601_bQ(\u2603 = \u2603 ? DamageSource.field_76370_b : (\u2603 ? DamageSource.field_76369_e : DamageSource.field_76377_j))) != null) {
                this.func_184185_a(\u2603, this.func_70599_aP(), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f + 1.0f);
            }
            this.func_70097_a(DamageSource.field_76377_j, 0.0f);
        } else if (by2 == 3) {
            SoundEvent soundEvent = this.func_184615_bR();
            if (soundEvent != null) {
                this.func_184185_a(soundEvent, this.func_70599_aP(), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f + 1.0f);
            }
            this.func_70606_j(0.0f);
            this.func_70645_a(DamageSource.field_76377_j);
        } else if (by2 == 30) {
            this.func_184185_a(SoundEvents.field_187769_eM, 0.8f, 0.8f + this.field_70170_p.field_73012_v.nextFloat() * 0.4f);
        } else if (by2 == 29) {
            this.func_184185_a(SoundEvents.field_187767_eL, 1.0f, 0.8f + this.field_70170_p.field_73012_v.nextFloat() * 0.4f);
        } else {
            super.func_70103_a(by2);
        }
    }

    @Override
    protected void func_70076_C() {
        this.func_70097_a(DamageSource.field_76380_i, 4.0f);
    }

    protected void func_82168_bl() {
        int n = this.func_82166_i();
        if (this.field_82175_bq) {
            ++this.field_110158_av;
            if (this.field_110158_av >= n) {
                this.field_110158_av = 0;
                this.field_82175_bq = false;
            }
        } else {
            this.field_110158_av = 0;
        }
        this.field_70733_aJ = (float)this.field_110158_av / (float)n;
    }

    public IAttributeInstance func_110148_a(IAttribute iAttribute) {
        return this.func_110140_aT().func_111151_a(iAttribute);
    }

    public AbstractAttributeMap func_110140_aT() {
        if (this.field_110155_d == null) {
            this.field_110155_d = new AttributeMap();
        }
        return this.field_110155_d;
    }

    public EnumCreatureAttribute func_70668_bt() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    public ItemStack func_184614_ca() {
        return this.func_184582_a(EntityEquipmentSlot.MAINHAND);
    }

    public ItemStack func_184592_cb() {
        return this.func_184582_a(EntityEquipmentSlot.OFFHAND);
    }

    public ItemStack func_184586_b(EnumHand enumHand) {
        if (enumHand == EnumHand.MAIN_HAND) {
            return this.func_184582_a(EntityEquipmentSlot.MAINHAND);
        }
        if (enumHand == EnumHand.OFF_HAND) {
            return this.func_184582_a(EntityEquipmentSlot.OFFHAND);
        }
        throw new IllegalArgumentException("Invalid hand " + (Object)((Object)enumHand));
    }

    public void func_184611_a(EnumHand enumHand, ItemStack itemStack) {
        if (enumHand == EnumHand.MAIN_HAND) {
            this.func_184201_a(EntityEquipmentSlot.MAINHAND, itemStack);
        } else if (enumHand == EnumHand.OFF_HAND) {
            this.func_184201_a(EntityEquipmentSlot.OFFHAND, itemStack);
        } else {
            throw new IllegalArgumentException("Invalid hand " + (Object)((Object)enumHand));
        }
    }

    public boolean func_190630_a(EntityEquipmentSlot entityEquipmentSlot) {
        return !this.func_184582_a(entityEquipmentSlot).func_190926_b();
    }

    @Override
    public abstract Iterable<ItemStack> func_184193_aE();

    public abstract ItemStack func_184582_a(EntityEquipmentSlot var1);

    @Override
    public abstract void func_184201_a(EntityEquipmentSlot var1, ItemStack var2);

    @Override
    public void func_70031_b(boolean bl) {
        super.func_70031_b(bl);
        IAttributeInstance iAttributeInstance = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
        if (iAttributeInstance.func_111127_a(field_110156_b) != null) {
            iAttributeInstance.func_111124_b(field_110157_c);
        }
        if (bl) {
            iAttributeInstance.func_111121_a(field_110157_c);
        }
    }

    protected float func_70599_aP() {
        return 1.0f;
    }

    protected float func_70647_i() {
        if (this.func_70631_g_()) {
            return (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f + 1.5f;
        }
        return (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f + 1.0f;
    }

    protected boolean func_70610_aX() {
        return this.func_110143_aJ() <= 0.0f;
    }

    public void func_110145_l(Entity entity) {
        double \u260313;
        double \u260312;
        double \u260311;
        if (entity instanceof EntityBoat || entity instanceof AbstractHorse) {
            double d = (double)(this.field_70130_N / 2.0f + entity.field_70130_N / 2.0f) + 0.4;
            float \u26032 = entity instanceof EntityBoat ? 0.0f : 1.5707964f * (float)(this.func_184591_cq() == EnumHandSide.RIGHT ? -1 : 1);
            float \u26033 = -MathHelper.func_76126_a(-this.field_70177_z * ((float)Math.PI / 180) - (float)Math.PI + \u26032);
            float \u26034 = -MathHelper.func_76134_b(-this.field_70177_z * ((float)Math.PI / 180) - (float)Math.PI + \u26032);
            \u2603 = Math.abs(\u26033) > Math.abs(\u26034) ? d / (double)Math.abs(\u26033) : d / (double)Math.abs(\u26034);
            \u2603 = this.field_70165_t + (double)\u26033 * \u2603;
            \u2603 = this.field_70161_v + (double)\u26034 * \u2603;
            this.func_70107_b(\u2603, entity.field_70163_u + (double)entity.field_70131_O + 0.001, \u2603);
            if (!this.field_70170_p.func_184143_b(this.func_174813_aQ())) {
                return;
            }
            this.func_70107_b(\u2603, entity.field_70163_u + (double)entity.field_70131_O + 1.001, \u2603);
            if (!this.field_70170_p.func_184143_b(this.func_174813_aQ())) {
                return;
            }
            this.func_70107_b(entity.field_70165_t, entity.field_70163_u + (double)this.field_70131_O + 0.001, entity.field_70161_v);
            return;
        }
        \u260311 = entity.field_70165_t;
        \u260312 = entity.func_174813_aQ().field_72338_b + (double)entity.field_70131_O;
        \u260313 = entity.field_70161_v;
        EnumFacing enumFacing = entity.func_184172_bi();
        if (enumFacing != null) {
            \u2603 = enumFacing.func_176746_e();
            int[][] nArrayArray = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
            double \u26035 = Math.floor(this.field_70165_t) + 0.5;
            double \u26036 = Math.floor(this.field_70161_v) + 0.5;
            double \u26037 = this.func_174813_aQ().field_72336_d - this.func_174813_aQ().field_72340_a;
            double \u26038 = this.func_174813_aQ().field_72334_f - this.func_174813_aQ().field_72339_c;
            AxisAlignedBB \u26039 = new AxisAlignedBB(\u26035 - \u26037 / 2.0, entity.func_174813_aQ().field_72338_b, \u26036 - \u26038 / 2.0, \u26035 + \u26037 / 2.0, Math.floor(entity.func_174813_aQ().field_72338_b) + (double)this.field_70131_O, \u26036 + \u26038 / 2.0);
            for (int[] nArray : nArrayArray) {
                double d = enumFacing.func_82601_c() * nArray[0] + \u2603.func_82601_c() * nArray[1];
                \u2603 = enumFacing.func_82599_e() * nArray[0] + \u2603.func_82599_e() * nArray[1];
                \u2603 = \u26035 + d;
                \u2603 = \u26036 + \u2603;
                AxisAlignedBB \u260310 = \u26039.func_72317_d(d, 0.0, \u2603);
                if (!this.field_70170_p.func_184143_b(\u260310)) {
                    if (this.field_70170_p.func_180495_p(new BlockPos(\u2603, this.field_70163_u, \u2603)).func_185896_q()) {
                        this.func_70634_a(\u2603, this.field_70163_u + 1.0, \u2603);
                        return;
                    }
                    BlockPos blockPos = new BlockPos(\u2603, this.field_70163_u - 1.0, \u2603);
                    if (!this.field_70170_p.func_180495_p(blockPos).func_185896_q() && this.field_70170_p.func_180495_p(blockPos).func_185904_a() != Material.field_151586_h) continue;
                    \u260311 = \u2603;
                    \u260312 = this.field_70163_u + 1.0;
                    \u260313 = \u2603;
                    continue;
                }
                if (this.field_70170_p.func_184143_b(\u260310.func_72317_d(0.0, 1.0, 0.0)) || !this.field_70170_p.func_180495_p(new BlockPos(\u2603, this.field_70163_u + 1.0, \u2603)).func_185896_q()) continue;
                \u260311 = \u2603;
                \u260312 = this.field_70163_u + 2.0;
                \u260313 = \u2603;
            }
        }
        this.func_70634_a(\u260311, \u260312, \u260313);
    }

    @Override
    public boolean func_94059_bO() {
        return this.func_174833_aM();
    }

    protected float func_175134_bD() {
        return 0.42f;
    }

    protected void func_70664_aZ() {
        this.field_70181_x = this.func_175134_bD();
        if (this.func_70644_a(MobEffects.field_76430_j)) {
            this.field_70181_x += (double)((float)(this.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.1f);
        }
        if (this.func_70051_ag()) {
            float f = this.field_70177_z * ((float)Math.PI / 180);
            this.field_70159_w -= (double)(MathHelper.func_76126_a(f) * 0.2f);
            this.field_70179_y += (double)(MathHelper.func_76134_b(f) * 0.2f);
        }
        this.field_70160_al = true;
    }

    protected void func_70629_bd() {
        this.field_70181_x += (double)0.04f;
    }

    protected void func_180466_bG() {
        this.field_70181_x += (double)0.04f;
    }

    protected float func_189749_co() {
        return 0.8f;
    }

    public void func_191986_a(float f, float f2, float f3) {
        if (this.func_70613_aW() || this.func_184186_bw()) {
            if (!(!this.func_70090_H() || this instanceof EntityPlayer && ((EntityPlayer)this).field_71075_bZ.field_75100_b)) {
                double d = this.field_70163_u;
                float \u26032 = this.func_189749_co();
                float \u26033 = 0.02f;
                float \u26034 = EnchantmentHelper.func_185294_d(this);
                if (\u26034 > 3.0f) {
                    \u26034 = 3.0f;
                }
                if (!this.field_70122_E) {
                    \u26034 *= 0.5f;
                }
                if (\u26034 > 0.0f) {
                    \u26032 += (0.54600006f - \u26032) * \u26034 / 3.0f;
                    \u26033 += (this.func_70689_ay() - \u26033) * \u26034 / 3.0f;
                }
                this.func_191958_b(f, f2, f3, \u26033);
                this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                this.field_70159_w *= (double)\u26032;
                this.field_70181_x *= (double)0.8f;
                this.field_70179_y *= (double)\u26032;
                if (!this.func_189652_ae()) {
                    this.field_70181_x -= 0.02;
                }
                if (this.field_70123_F && this.func_70038_c(this.field_70159_w, this.field_70181_x + (double)0.6f - this.field_70163_u + d, this.field_70179_y)) {
                    this.field_70181_x = 0.3f;
                }
            } else if (!(!this.func_180799_ab() || this instanceof EntityPlayer && ((EntityPlayer)this).field_71075_bZ.field_75100_b)) {
                \u2603 = this.field_70163_u;
                this.func_191958_b(f, f2, f3, 0.02f);
                this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                this.field_70159_w *= 0.5;
                this.field_70181_x *= 0.5;
                this.field_70179_y *= 0.5;
                if (!this.func_189652_ae()) {
                    this.field_70181_x -= 0.02;
                }
                if (this.field_70123_F && this.func_70038_c(this.field_70159_w, this.field_70181_x + (double)0.6f - this.field_70163_u + \u2603, this.field_70179_y)) {
                    this.field_70181_x = 0.3f;
                }
            } else if (this.func_184613_cA()) {
                if (this.field_70181_x > -0.5) {
                    this.field_70143_R = 1.0f;
                }
                Vec3d \u26035 = this.func_70040_Z();
                float \u26036 = this.field_70125_A * ((float)Math.PI / 180);
                \u2603 = Math.sqrt(\u26035.field_72450_a * \u26035.field_72450_a + \u26035.field_72449_c * \u26035.field_72449_c);
                \u2603 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
                \u2603 = \u26035.func_72433_c();
                float \u26037 = MathHelper.func_76134_b(\u26036);
                \u26037 = (float)((double)\u26037 * ((double)\u26037 * Math.min(1.0, \u2603 / 0.4)));
                this.field_70181_x += -0.08 + (double)\u26037 * 0.06;
                if (this.field_70181_x < 0.0 && \u2603 > 0.0) {
                    \u2603 = this.field_70181_x * -0.1 * (double)\u26037;
                    this.field_70181_x += \u2603;
                    this.field_70159_w += \u26035.field_72450_a * \u2603 / \u2603;
                    this.field_70179_y += \u26035.field_72449_c * \u2603 / \u2603;
                }
                if (\u26036 < 0.0f) {
                    \u2603 = \u2603 * (double)(-MathHelper.func_76126_a(\u26036)) * 0.04;
                    this.field_70181_x += \u2603 * 3.2;
                    this.field_70159_w -= \u26035.field_72450_a * \u2603 / \u2603;
                    this.field_70179_y -= \u26035.field_72449_c * \u2603 / \u2603;
                }
                if (\u2603 > 0.0) {
                    this.field_70159_w += (\u26035.field_72450_a / \u2603 * \u2603 - this.field_70159_w) * 0.1;
                    this.field_70179_y += (\u26035.field_72449_c / \u2603 * \u2603 - this.field_70179_y) * 0.1;
                }
                this.field_70159_w *= (double)0.99f;
                this.field_70181_x *= (double)0.98f;
                this.field_70179_y *= (double)0.99f;
                this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                if (this.field_70123_F && !this.field_70170_p.field_72995_K && (\u2603 = (float)((\u2603 = \u2603 - (\u2603 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y))) * 10.0 - 3.0)) > 0.0f) {
                    this.func_184185_a(this.func_184588_d((int)\u2603), 1.0f, 1.0f);
                    this.func_70097_a(DamageSource.field_188406_j, \u2603);
                }
                if (this.field_70122_E && !this.field_70170_p.field_72995_K) {
                    this.func_70052_a(7, false);
                }
            } else {
                float \u26038 = 0.91f;
                BlockPos.PooledMutableBlockPos \u26039 = BlockPos.PooledMutableBlockPos.func_185345_c(this.field_70165_t, this.func_174813_aQ().field_72338_b - 1.0, this.field_70161_v);
                if (this.field_70122_E) {
                    \u26038 = this.field_70170_p.func_180495_p((BlockPos)\u26039).func_177230_c().field_149765_K * 0.91f;
                }
                float \u260310 = 0.16277136f / (\u26038 * \u26038 * \u26038);
                float \u260311 = this.field_70122_E ? this.func_70689_ay() * \u260310 : this.field_70747_aH;
                this.func_191958_b(f, f2, f3, \u260311);
                \u26038 = 0.91f;
                if (this.field_70122_E) {
                    \u26038 = this.field_70170_p.func_180495_p((BlockPos)\u26039.func_189532_c((double)this.field_70165_t, (double)(this.func_174813_aQ().field_72338_b - 1.0), (double)this.field_70161_v)).func_177230_c().field_149765_K * 0.91f;
                }
                if (this.func_70617_f_()) {
                    float f4 = 0.15f;
                    this.field_70159_w = MathHelper.func_151237_a(this.field_70159_w, -0.15f, 0.15f);
                    this.field_70179_y = MathHelper.func_151237_a(this.field_70179_y, -0.15f, 0.15f);
                    this.field_70143_R = 0.0f;
                    if (this.field_70181_x < -0.15) {
                        this.field_70181_x = -0.15;
                    }
                    boolean bl = \u2603 = this.func_70093_af() && this instanceof EntityPlayer;
                    if (\u2603 && this.field_70181_x < 0.0) {
                        this.field_70181_x = 0.0;
                    }
                }
                this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                if (this.field_70123_F && this.func_70617_f_()) {
                    this.field_70181_x = 0.2;
                }
                if (this.func_70644_a(MobEffects.field_188424_y)) {
                    this.field_70181_x += (0.05 * (double)(this.func_70660_b(MobEffects.field_188424_y).func_76458_c() + 1) - this.field_70181_x) * 0.2;
                } else {
                    \u26039.func_189532_c(this.field_70165_t, 0.0, this.field_70161_v);
                    if (!this.field_70170_p.field_72995_K || this.field_70170_p.func_175667_e(\u26039) && this.field_70170_p.func_175726_f(\u26039).func_177410_o()) {
                        if (!this.func_189652_ae()) {
                            this.field_70181_x -= 0.08;
                        }
                    } else {
                        this.field_70181_x = this.field_70163_u > 0.0 ? -0.1 : 0.0;
                    }
                }
                this.field_70181_x *= (double)0.98f;
                this.field_70159_w *= (double)\u26038;
                this.field_70179_y *= (double)\u26038;
                \u26039.func_185344_t();
            }
        }
        this.field_184618_aE = this.field_70721_aZ;
        double d = this.field_70165_t - this.field_70169_q;
        \u2603 = this instanceof EntityFlying ? this.field_70163_u - this.field_70167_r : 0.0;
        float \u260312 = MathHelper.func_76133_a(d * d + \u2603 * \u2603 + (\u2603 = this.field_70161_v - this.field_70166_s) * \u2603) * 4.0f;
        if (\u260312 > 1.0f) {
            \u260312 = 1.0f;
        }
        this.field_70721_aZ += (\u260312 - this.field_70721_aZ) * 0.4f;
        this.field_184619_aG += this.field_70721_aZ;
    }

    public float func_70689_ay() {
        return this.field_70746_aG;
    }

    public void func_70659_e(float f) {
        this.field_70746_aG = f;
    }

    public boolean func_70652_k(Entity entity) {
        this.func_130011_c(entity);
        return false;
    }

    public boolean func_70608_bn() {
        return false;
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        this.func_184608_ct();
        if (!this.field_70170_p.field_72995_K) {
            int n = this.func_85035_bI();
            if (n > 0) {
                if (this.field_70720_be <= 0) {
                    this.field_70720_be = 20 * (30 - n);
                }
                --this.field_70720_be;
                if (this.field_70720_be <= 0) {
                    this.func_85034_r(n - 1);
                }
            }
            block8: for (EntityEquipmentSlot entityEquipmentSlot2 : EntityEquipmentSlot.values()) {
                ItemStack \u26032;
                switch (entityEquipmentSlot2.func_188453_a()) {
                    case HAND: {
                        \u26032 = this.field_184630_bs.get(entityEquipmentSlot2.func_188454_b());
                        break;
                    }
                    case ARMOR: {
                        EntityEquipmentSlot entityEquipmentSlot2;
                        \u26032 = this.field_184631_bt.get(entityEquipmentSlot2.func_188454_b());
                        break;
                    }
                    default: {
                        continue block8;
                    }
                }
                ItemStack itemStack = this.func_184582_a(entityEquipmentSlot2);
                if (ItemStack.func_77989_b(itemStack, \u26032)) continue;
                ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new SPacketEntityEquipment(this.func_145782_y(), entityEquipmentSlot2, itemStack));
                if (!\u26032.func_190926_b()) {
                    this.func_110140_aT().func_111148_a(\u26032.func_111283_C(entityEquipmentSlot2));
                }
                if (!itemStack.func_190926_b()) {
                    this.func_110140_aT().func_111147_b(itemStack.func_111283_C(entityEquipmentSlot2));
                }
                switch (entityEquipmentSlot2.func_188453_a()) {
                    case HAND: {
                        this.field_184630_bs.set(entityEquipmentSlot2.func_188454_b(), itemStack.func_190926_b() ? ItemStack.field_190927_a : itemStack.func_77946_l());
                        continue block8;
                    }
                    case ARMOR: {
                        this.field_184631_bt.set(entityEquipmentSlot2.func_188454_b(), itemStack.func_190926_b() ? ItemStack.field_190927_a : itemStack.func_77946_l());
                    }
                }
            }
            if (this.field_70173_aa % 20 == 0) {
                this.func_110142_aN().func_94549_h();
            }
            if (!this.field_184238_ar) {
                boolean bl = this.func_70644_a(MobEffects.field_188423_x);
                if (this.func_70083_f(6) != bl) {
                    this.func_70052_a(6, bl);
                }
            }
        }
        this.func_70636_d();
        double d = this.field_70165_t - this.field_70169_q;
        \u2603 = this.field_70161_v - this.field_70166_s;
        float \u26033 = (float)(d * d + \u2603 * \u2603);
        float \u26034 = this.field_70761_aq;
        float \u26035 = 0.0f;
        this.field_70768_au = this.field_110154_aX;
        float \u26036 = 0.0f;
        if (\u26033 > 0.0025000002f) {
            \u26036 = 1.0f;
            \u26035 = (float)Math.sqrt(\u26033) * 3.0f;
            float f = (float)MathHelper.func_181159_b(\u2603, d) * 57.295776f - 90.0f;
            \u2603 = MathHelper.func_76135_e(MathHelper.func_76142_g(this.field_70177_z) - f);
            \u26034 = 95.0f < \u2603 && \u2603 < 265.0f ? f - 180.0f : f;
        }
        if (this.field_70733_aJ > 0.0f) {
            \u26034 = this.field_70177_z;
        }
        if (!this.field_70122_E) {
            \u26036 = 0.0f;
        }
        this.field_110154_aX += (\u26036 - this.field_110154_aX) * 0.3f;
        this.field_70170_p.field_72984_F.func_76320_a("headTurn");
        \u26035 = this.func_110146_f(\u26034, \u26035);
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("rangeChecks");
        while (this.field_70177_z - this.field_70126_B < -180.0f) {
            this.field_70126_B -= 360.0f;
        }
        while (this.field_70177_z - this.field_70126_B >= 180.0f) {
            this.field_70126_B += 360.0f;
        }
        while (this.field_70761_aq - this.field_70760_ar < -180.0f) {
            this.field_70760_ar -= 360.0f;
        }
        while (this.field_70761_aq - this.field_70760_ar >= 180.0f) {
            this.field_70760_ar += 360.0f;
        }
        while (this.field_70125_A - this.field_70127_C < -180.0f) {
            this.field_70127_C -= 360.0f;
        }
        while (this.field_70125_A - this.field_70127_C >= 180.0f) {
            this.field_70127_C += 360.0f;
        }
        while (this.field_70759_as - this.field_70758_at < -180.0f) {
            this.field_70758_at -= 360.0f;
        }
        while (this.field_70759_as - this.field_70758_at >= 180.0f) {
            this.field_70758_at += 360.0f;
        }
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70764_aw += \u26035;
        this.field_184629_bo = this.func_184613_cA() ? ++this.field_184629_bo : 0;
    }

    protected float func_110146_f(float f, float f2) {
        \u2603 = MathHelper.func_76142_g(f - this.field_70761_aq);
        this.field_70761_aq += \u2603 * 0.3f;
        \u2603 = MathHelper.func_76142_g(this.field_70177_z - this.field_70761_aq);
        boolean bl = \u2603 = \u2603 < -90.0f || \u2603 >= 90.0f;
        if (\u2603 < -75.0f) {
            \u2603 = -75.0f;
        }
        if (\u2603 >= 75.0f) {
            \u2603 = 75.0f;
        }
        this.field_70761_aq = this.field_70177_z - \u2603;
        if (\u2603 * \u2603 > 2500.0f) {
            this.field_70761_aq += \u2603 * 0.2f;
        }
        if (\u2603) {
            f2 *= -1.0f;
        }
        return f2;
    }

    public void func_70636_d() {
        if (this.field_70773_bE > 0) {
            --this.field_70773_bE;
        }
        if (this.field_70716_bi > 0 && !this.func_184186_bw()) {
            double d = this.field_70165_t + (this.field_184623_bh - this.field_70165_t) / (double)this.field_70716_bi;
            \u2603 = this.field_70163_u + (this.field_184624_bi - this.field_70163_u) / (double)this.field_70716_bi;
            \u2603 = this.field_70161_v + (this.field_184625_bj - this.field_70161_v) / (double)this.field_70716_bi;
            \u2603 = MathHelper.func_76138_g(this.field_184626_bk - (double)this.field_70177_z);
            this.field_70177_z = (float)((double)this.field_70177_z + \u2603 / (double)this.field_70716_bi);
            this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70709_bj - (double)this.field_70125_A) / (double)this.field_70716_bi);
            --this.field_70716_bi;
            this.func_70107_b(d, \u2603, \u2603);
            this.func_70101_b(this.field_70177_z, this.field_70125_A);
        } else if (!this.func_70613_aW()) {
            this.field_70159_w *= 0.98;
            this.field_70181_x *= 0.98;
            this.field_70179_y *= 0.98;
        }
        if (Math.abs(this.field_70159_w) < 0.003) {
            this.field_70159_w = 0.0;
        }
        if (Math.abs(this.field_70181_x) < 0.003) {
            this.field_70181_x = 0.0;
        }
        if (Math.abs(this.field_70179_y) < 0.003) {
            this.field_70179_y = 0.0;
        }
        this.field_70170_p.field_72984_F.func_76320_a("ai");
        if (this.func_70610_aX()) {
            this.field_70703_bu = false;
            this.field_70702_br = 0.0f;
            this.field_191988_bg = 0.0f;
            this.field_70704_bt = 0.0f;
        } else if (this.func_70613_aW()) {
            this.field_70170_p.field_72984_F.func_76320_a("newAi");
            this.func_70626_be();
            this.field_70170_p.field_72984_F.func_76319_b();
        }
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("jump");
        if (this.field_70703_bu) {
            if (this.func_70090_H()) {
                this.func_70629_bd();
            } else if (this.func_180799_ab()) {
                this.func_180466_bG();
            } else if (this.field_70122_E && this.field_70773_bE == 0) {
                this.func_70664_aZ();
                this.field_70773_bE = 10;
            }
        } else {
            this.field_70773_bE = 0;
        }
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("travel");
        this.field_70702_br *= 0.98f;
        this.field_191988_bg *= 0.98f;
        this.field_70704_bt *= 0.9f;
        this.func_184616_r();
        this.func_191986_a(this.field_70702_br, this.field_70701_bs, this.field_191988_bg);
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("push");
        this.func_85033_bc();
        this.field_70170_p.field_72984_F.func_76319_b();
    }

    private void func_184616_r() {
        boolean bl = this.func_70083_f(7);
        if (bl && !this.field_70122_E && !this.func_184218_aH()) {
            ItemStack itemStack = this.func_184582_a(EntityEquipmentSlot.CHEST);
            if (itemStack.func_77973_b() == Items.field_185160_cR && ItemElytra.func_185069_d(itemStack)) {
                bl = true;
                if (!this.field_70170_p.field_72995_K && (this.field_184629_bo + 1) % 20 == 0) {
                    itemStack.func_77972_a(1, this);
                }
            } else {
                bl = false;
            }
        } else {
            bl = false;
        }
        if (!this.field_70170_p.field_72995_K) {
            this.func_70052_a(7, bl);
        }
    }

    protected void func_70626_be() {
    }

    protected void func_85033_bc() {
        List<Entity> list = this.field_70170_p.func_175674_a(this, this.func_174813_aQ(), EntitySelectors.func_188442_a(this));
        if (!list.isEmpty()) {
            int n = this.field_70170_p.func_82736_K().func_180263_c("maxEntityCramming");
            if (n > 0 && list.size() > n - 1 && this.field_70146_Z.nextInt(4) == 0) {
                \u2603 = 0;
                for (\u2603 = 0; \u2603 < list.size(); ++\u2603) {
                    if (list.get(\u2603).func_184218_aH()) continue;
                    ++\u2603;
                }
                if (\u2603 > n - 1) {
                    this.func_70097_a(DamageSource.field_191291_g, 6.0f);
                }
            }
            for (\u2603 = 0; \u2603 < list.size(); ++\u2603) {
                Entity entity = list.get(\u2603);
                this.func_82167_n(entity);
            }
        }
    }

    protected void func_82167_n(Entity entity) {
        entity.func_70108_f(this);
    }

    @Override
    public void func_184210_p() {
        Entity entity = this.func_184187_bx();
        super.func_184210_p();
        if (entity != null && entity != this.func_184187_bx() && !this.field_70170_p.field_72995_K) {
            this.func_110145_l(entity);
        }
    }

    @Override
    public void func_70098_U() {
        super.func_70098_U();
        this.field_70768_au = this.field_110154_aX;
        this.field_110154_aX = 0.0f;
        this.field_70143_R = 0.0f;
    }

    @Override
    public void func_180426_a(double d, double d2, double d3, float f, float f2, int n, boolean bl) {
        this.field_184623_bh = d;
        this.field_184624_bi = d2;
        this.field_184625_bj = d3;
        this.field_184626_bk = f;
        this.field_70709_bj = f2;
        this.field_70716_bi = n;
    }

    public void func_70637_d(boolean bl) {
        this.field_70703_bu = bl;
    }

    public void func_71001_a(Entity entity, int n) {
        if (!entity.field_70128_L && !this.field_70170_p.field_72995_K) {
            EntityTracker entityTracker = ((WorldServer)this.field_70170_p).func_73039_n();
            if (entity instanceof EntityItem || entity instanceof EntityArrow || entity instanceof EntityXPOrb) {
                entityTracker.func_151247_a(entity, new SPacketCollectItem(entity.func_145782_y(), this.func_145782_y(), n));
            }
        }
    }

    public boolean func_70685_l(Entity entity) {
        return this.field_70170_p.func_147447_a(new Vec3d(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v), new Vec3d(entity.field_70165_t, entity.field_70163_u + (double)entity.func_70047_e(), entity.field_70161_v), false, true, false) == null;
    }

    @Override
    public Vec3d func_70676_i(float f) {
        if (f == 1.0f) {
            return this.func_174806_f(this.field_70125_A, this.field_70759_as);
        }
        \u2603 = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * f;
        \u2603 = this.field_70758_at + (this.field_70759_as - this.field_70758_at) * f;
        return this.func_174806_f(\u2603, \u2603);
    }

    public float func_70678_g(float f) {
        \u2603 = this.field_70733_aJ - this.field_70732_aI;
        if (\u2603 < 0.0f) {
            \u2603 += 1.0f;
        }
        return this.field_70732_aI + \u2603 * f;
    }

    public boolean func_70613_aW() {
        return !this.field_70170_p.field_72995_K;
    }

    @Override
    public boolean func_70067_L() {
        return !this.field_70128_L;
    }

    @Override
    public boolean func_70104_M() {
        return this.func_70089_S() && !this.func_70617_f_();
    }

    @Override
    protected void func_70018_K() {
        this.field_70133_I = this.field_70146_Z.nextDouble() >= this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111126_e();
    }

    @Override
    public float func_70079_am() {
        return this.field_70759_as;
    }

    @Override
    public void func_70034_d(float f) {
        this.field_70759_as = f;
    }

    @Override
    public void func_181013_g(float f) {
        this.field_70761_aq = f;
    }

    public float func_110139_bj() {
        return this.field_110151_bq;
    }

    public void func_110149_m(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.field_110151_bq = f;
    }

    public void func_152111_bt() {
    }

    public void func_152112_bu() {
    }

    protected void func_175136_bO() {
        this.field_70752_e = true;
    }

    public abstract EnumHandSide func_184591_cq();

    public boolean func_184587_cr() {
        return (this.field_70180_af.func_187225_a(field_184621_as) & 1) > 0;
    }

    public EnumHand func_184600_cs() {
        return (this.field_70180_af.func_187225_a(field_184621_as) & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    protected void func_184608_ct() {
        if (this.func_184587_cr()) {
            ItemStack itemStack = this.func_184586_b(this.func_184600_cs());
            if (itemStack == this.field_184627_bm) {
                if (this.func_184605_cv() <= 25 && this.func_184605_cv() % 4 == 0) {
                    this.func_184584_a(this.field_184627_bm, 5);
                }
                if (--this.field_184628_bn == 0 && !this.field_70170_p.field_72995_K) {
                    this.func_71036_o();
                }
            } else {
                this.func_184602_cy();
            }
        }
    }

    public void func_184598_c(EnumHand enumHand) {
        ItemStack itemStack = this.func_184586_b(enumHand);
        if (itemStack.func_190926_b() || this.func_184587_cr()) {
            return;
        }
        this.field_184627_bm = itemStack;
        this.field_184628_bn = itemStack.func_77988_m();
        if (!this.field_70170_p.field_72995_K) {
            int n = 1;
            if (enumHand == EnumHand.OFF_HAND) {
                n |= 2;
            }
            this.field_70180_af.func_187227_b(field_184621_as, (byte)n);
        }
    }

    @Override
    public void func_184206_a(DataParameter<?> dataParameter) {
        super.func_184206_a(dataParameter);
        if (field_184621_as.equals(dataParameter) && this.field_70170_p.field_72995_K) {
            if (this.func_184587_cr() && this.field_184627_bm.func_190926_b()) {
                this.field_184627_bm = this.func_184586_b(this.func_184600_cs());
                if (!this.field_184627_bm.func_190926_b()) {
                    this.field_184628_bn = this.field_184627_bm.func_77988_m();
                }
            } else if (!this.func_184587_cr() && !this.field_184627_bm.func_190926_b()) {
                this.field_184627_bm = ItemStack.field_190927_a;
                this.field_184628_bn = 0;
            }
        }
    }

    protected void func_184584_a(ItemStack itemStack, int n) {
        if (itemStack.func_190926_b() || !this.func_184587_cr()) {
            return;
        }
        if (itemStack.func_77975_n() == EnumAction.DRINK) {
            this.func_184185_a(SoundEvents.field_187664_bz, 0.5f, this.field_70170_p.field_73012_v.nextFloat() * 0.1f + 0.9f);
        }
        if (itemStack.func_77975_n() == EnumAction.EAT) {
            for (\u2603 = 0; \u2603 < n; ++\u2603) {
                Vec3d vec3d = new Vec3d(((double)this.field_70146_Z.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
                vec3d = vec3d.func_178789_a(-this.field_70125_A * ((float)Math.PI / 180));
                vec3d = vec3d.func_178785_b(-this.field_70177_z * ((float)Math.PI / 180));
                double \u26032 = (double)(-this.field_70146_Z.nextFloat()) * 0.6 - 0.3;
                \u2603 = new Vec3d(((double)this.field_70146_Z.nextFloat() - 0.5) * 0.3, \u26032, 0.6);
                \u2603 = \u2603.func_178789_a(-this.field_70125_A * ((float)Math.PI / 180));
                \u2603 = \u2603.func_178785_b(-this.field_70177_z * ((float)Math.PI / 180));
                \u2603 = \u2603.func_72441_c(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
                if (itemStack.func_77981_g()) {
                    this.field_70170_p.func_175688_a(EnumParticleTypes.ITEM_CRACK, \u2603.field_72450_a, \u2603.field_72448_b, \u2603.field_72449_c, vec3d.field_72450_a, vec3d.field_72448_b + 0.05, vec3d.field_72449_c, Item.func_150891_b(itemStack.func_77973_b()), itemStack.func_77960_j());
                    continue;
                }
                this.field_70170_p.func_175688_a(EnumParticleTypes.ITEM_CRACK, \u2603.field_72450_a, \u2603.field_72448_b, \u2603.field_72449_c, vec3d.field_72450_a, vec3d.field_72448_b + 0.05, vec3d.field_72449_c, Item.func_150891_b(itemStack.func_77973_b()));
            }
            this.func_184185_a(SoundEvents.field_187537_bA, 0.5f + 0.5f * (float)this.field_70146_Z.nextInt(2), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f + 1.0f);
        }
    }

    protected void func_71036_o() {
        if (!this.field_184627_bm.func_190926_b() && this.func_184587_cr()) {
            this.func_184584_a(this.field_184627_bm, 16);
            this.func_184611_a(this.func_184600_cs(), this.field_184627_bm.func_77950_b(this.field_70170_p, this));
            this.func_184602_cy();
        }
    }

    public ItemStack func_184607_cu() {
        return this.field_184627_bm;
    }

    public int func_184605_cv() {
        return this.field_184628_bn;
    }

    public int func_184612_cw() {
        if (this.func_184587_cr()) {
            return this.field_184627_bm.func_77988_m() - this.func_184605_cv();
        }
        return 0;
    }

    public void func_184597_cx() {
        if (!this.field_184627_bm.func_190926_b()) {
            this.field_184627_bm.func_77974_b(this.field_70170_p, this, this.func_184605_cv());
        }
        this.func_184602_cy();
    }

    public void func_184602_cy() {
        if (!this.field_70170_p.field_72995_K) {
            this.field_70180_af.func_187227_b(field_184621_as, (byte)0);
        }
        this.field_184627_bm = ItemStack.field_190927_a;
        this.field_184628_bn = 0;
    }

    public boolean func_184585_cz() {
        if (!this.func_184587_cr() || this.field_184627_bm.func_190926_b()) {
            return false;
        }
        Item item = this.field_184627_bm.func_77973_b();
        if (item.func_77661_b(this.field_184627_bm) != EnumAction.BLOCK) {
            return false;
        }
        return item.func_77626_a(this.field_184627_bm) - this.field_184628_bn >= 5;
    }

    public boolean func_184613_cA() {
        return this.func_70083_f(7);
    }

    public int func_184599_cB() {
        return this.field_184629_bo;
    }

    public boolean func_184595_k(double d, double d2, double d3) {
        int \u26036;
        \u2603 = this.field_70165_t;
        \u2603 = this.field_70163_u;
        \u2603 = this.field_70161_v;
        this.field_70165_t = d;
        this.field_70163_u = d2;
        this.field_70161_v = d3;
        boolean bl = false;
        BlockPos \u26032 = new BlockPos(this);
        World \u26033 = this.field_70170_p;
        Random \u26034 = this.func_70681_au();
        if (\u26033.func_175667_e(\u26032)) {
            \u26036 = 0;
            while (\u26036 == 0 && \u26032.func_177956_o() > 0) {
                BlockPos blockPos = \u26032.func_177977_b();
                IBlockState \u26035 = \u26033.func_180495_p(blockPos);
                if (\u26035.func_185904_a().func_76230_c()) {
                    \u26036 = 1;
                    continue;
                }
                this.field_70163_u -= 1.0;
                \u26032 = blockPos;
            }
            if (\u26036 != 0) {
                this.func_70634_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
                if (\u26033.func_184144_a(this, this.func_174813_aQ()).isEmpty() && !\u26033.func_72953_d(this.func_174813_aQ())) {
                    bl = true;
                }
            }
        }
        if (!bl) {
            this.func_70634_a(\u2603, \u2603, \u2603);
            return false;
        }
        \u26036 = 128;
        for (int i = 0; i < 128; ++i) {
            double d4 = (double)i / 127.0;
            float \u26037 = (\u26034.nextFloat() - 0.5f) * 0.2f;
            float \u26038 = (\u26034.nextFloat() - 0.5f) * 0.2f;
            float \u26039 = (\u26034.nextFloat() - 0.5f) * 0.2f;
            \u2603 = \u2603 + (this.field_70165_t - \u2603) * d4 + (\u26034.nextDouble() - 0.5) * (double)this.field_70130_N * 2.0;
            \u2603 = \u2603 + (this.field_70163_u - \u2603) * d4 + \u26034.nextDouble() * (double)this.field_70131_O;
            \u2603 = \u2603 + (this.field_70161_v - \u2603) * d4 + (\u26034.nextDouble() - 0.5) * (double)this.field_70130_N * 2.0;
            \u26033.func_175688_a(EnumParticleTypes.PORTAL, \u2603, \u2603, \u2603, \u26037, \u26038, \u26039, new int[0]);
        }
        if (this instanceof EntityCreature) {
            ((EntityCreature)this).func_70661_as().func_75499_g();
        }
        return true;
    }

    public boolean func_184603_cC() {
        return true;
    }

    public boolean func_190631_cK() {
        return true;
    }

    public void func_191987_a(BlockPos blockPos, boolean bl) {
    }
}

