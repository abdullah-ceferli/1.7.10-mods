/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public abstract class EntityLiving
extends EntityLivingBase {
    private static final DataParameter<Byte> field_184654_a = EntityDataManager.func_187226_a(EntityLiving.class, DataSerializers.field_187191_a);
    public int field_70757_a;
    protected int field_70728_aV;
    private final EntityLookHelper field_70749_g;
    protected EntityMoveHelper field_70765_h;
    protected EntityJumpHelper field_70767_i;
    private final EntityBodyHelper field_70762_j;
    protected PathNavigate field_70699_by;
    protected final EntityAITasks field_70714_bg;
    protected final EntityAITasks field_70715_bh;
    private EntityLivingBase field_70696_bz;
    private final EntitySenses field_70723_bA;
    private final NonNullList<ItemStack> field_184656_bv = NonNullList.func_191197_a(2, ItemStack.field_190927_a);
    protected float[] field_82174_bp = new float[2];
    private final NonNullList<ItemStack> field_184657_bw = NonNullList.func_191197_a(4, ItemStack.field_190927_a);
    protected float[] field_184655_bs = new float[4];
    private boolean field_82172_bs;
    private boolean field_82179_bU;
    private final Map<PathNodeType, Float> field_184658_bz = Maps.newEnumMap(PathNodeType.class);
    private ResourceLocation field_184659_bA;
    private long field_184653_bB;
    private boolean field_110169_bv;
    private Entity field_110168_bw;
    private NBTTagCompound field_110170_bx;

    public EntityLiving(World world) {
        super(world);
        this.field_70714_bg = new EntityAITasks(world == null || world.field_72984_F == null ? null : world.field_72984_F);
        this.field_70715_bh = new EntityAITasks(world == null || world.field_72984_F == null ? null : world.field_72984_F);
        this.field_70749_g = new EntityLookHelper(this);
        this.field_70765_h = new EntityMoveHelper(this);
        this.field_70767_i = new EntityJumpHelper(this);
        this.field_70762_j = this.func_184650_s();
        this.field_70699_by = this.func_175447_b(world);
        this.field_70723_bA = new EntitySenses(this);
        Arrays.fill(this.field_184655_bs, 0.085f);
        Arrays.fill(this.field_82174_bp, 0.085f);
        if (world != null && !world.field_72995_K) {
            this.func_184651_r();
        }
    }

    protected void func_184651_r() {
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111265_b).func_111128_a(16.0);
    }

    protected PathNavigate func_175447_b(World world) {
        return new PathNavigateGround(this, world);
    }

    public float func_184643_a(PathNodeType pathNodeType) {
        Float f = this.field_184658_bz.get((Object)pathNodeType);
        return f == null ? pathNodeType.func_186289_a() : f.floatValue();
    }

    public void func_184644_a(PathNodeType pathNodeType, float f) {
        this.field_184658_bz.put(pathNodeType, Float.valueOf(f));
    }

    protected EntityBodyHelper func_184650_s() {
        return new EntityBodyHelper(this);
    }

    public EntityLookHelper func_70671_ap() {
        return this.field_70749_g;
    }

    public EntityMoveHelper func_70605_aq() {
        return this.field_70765_h;
    }

    public EntityJumpHelper func_70683_ar() {
        return this.field_70767_i;
    }

    public PathNavigate func_70661_as() {
        return this.field_70699_by;
    }

    public EntitySenses func_70635_at() {
        return this.field_70723_bA;
    }

    @Nullable
    public EntityLivingBase func_70638_az() {
        return this.field_70696_bz;
    }

    public void func_70624_b(@Nullable EntityLivingBase entityLivingBase) {
        this.field_70696_bz = entityLivingBase;
    }

    public boolean func_70686_a(Class<? extends EntityLivingBase> clazz) {
        return clazz != EntityGhast.class;
    }

    public void func_70615_aA() {
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_187214_a(field_184654_a, (byte)0);
    }

    public int func_70627_aG() {
        return 80;
    }

    public void func_70642_aH() {
        SoundEvent soundEvent = this.func_184639_G();
        if (soundEvent != null) {
            this.func_184185_a(soundEvent, this.func_70599_aP(), this.func_70647_i());
        }
    }

    @Override
    public void func_70030_z() {
        super.func_70030_z();
        this.field_70170_p.field_72984_F.func_76320_a("mobBaseTick");
        if (this.func_70089_S() && this.field_70146_Z.nextInt(1000) < this.field_70757_a++) {
            this.func_175456_n();
            this.func_70642_aH();
        }
        this.field_70170_p.field_72984_F.func_76319_b();
    }

    @Override
    protected void func_184581_c(DamageSource damageSource) {
        this.func_175456_n();
        super.func_184581_c(damageSource);
    }

    private void func_175456_n() {
        this.field_70757_a = -this.func_70627_aG();
    }

    @Override
    protected int func_70693_a(EntityPlayer entityPlayer) {
        if (this.field_70728_aV > 0) {
            int n = this.field_70728_aV;
            for (\u2603 = 0; \u2603 < this.field_184657_bw.size(); ++\u2603) {
                if (this.field_184657_bw.get(\u2603).func_190926_b() || !(this.field_184655_bs[\u2603] <= 1.0f)) continue;
                n += 1 + this.field_70146_Z.nextInt(3);
            }
            for (\u2603 = 0; \u2603 < this.field_184656_bv.size(); ++\u2603) {
                if (this.field_184656_bv.get(\u2603).func_190926_b() || !(this.field_82174_bp[\u2603] <= 1.0f)) continue;
                n += 1 + this.field_70146_Z.nextInt(3);
            }
            return n;
        }
        return this.field_70728_aV;
    }

    public void func_70656_aK() {
        if (this.field_70170_p.field_72995_K) {
            for (int i = 0; i < 20; ++i) {
                double d = this.field_70146_Z.nextGaussian() * 0.02;
                \u2603 = this.field_70146_Z.nextGaussian() * 0.02;
                \u2603 = this.field_70146_Z.nextGaussian() * 0.02;
                \u2603 = 10.0;
                this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_NORMAL, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0f) - (double)this.field_70130_N - d * 10.0, this.field_70163_u + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O) - \u2603 * 10.0, this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0f) - (double)this.field_70130_N - \u2603 * 10.0, d, \u2603, \u2603, new int[0]);
            }
        } else {
            this.field_70170_p.func_72960_a(this, (byte)20);
        }
    }

    @Override
    public void func_70103_a(byte by) {
        if (by == 20) {
            this.func_70656_aK();
        } else {
            super.func_70103_a(by);
        }
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        if (!this.field_70170_p.field_72995_K) {
            this.func_110159_bB();
            if (this.field_70173_aa % 5 == 0) {
                boolean bl = !(this.func_184179_bs() instanceof EntityLiving);
                \u2603 = !(this.func_184187_bx() instanceof EntityBoat);
                this.field_70714_bg.func_188527_a(1, bl);
                this.field_70714_bg.func_188527_a(4, bl && \u2603);
                this.field_70714_bg.func_188527_a(2, bl);
            }
        }
    }

    @Override
    protected float func_110146_f(float f, float f2) {
        this.field_70762_j.func_75664_a();
        return f2;
    }

    @Nullable
    protected SoundEvent func_184639_G() {
        return null;
    }

    @Nullable
    protected Item func_146068_u() {
        return null;
    }

    @Override
    protected void func_70628_a(boolean bl, int n) {
        Item item = this.func_146068_u();
        if (item != null) {
            int n2 = this.field_70146_Z.nextInt(3);
            if (n > 0) {
                n2 += this.field_70146_Z.nextInt(n + 1);
            }
            for (\u2603 = 0; \u2603 < n2; ++\u2603) {
                this.func_145779_a(item, 1);
            }
        }
    }

    public static void func_189752_a(DataFixer dataFixer, Class<?> clazz) {
        dataFixer.func_188258_a(FixTypes.ENTITY, new ItemStackDataLists(clazz, "ArmorItems", "HandItems"));
    }

    @Override
    public void func_70014_b(NBTTagCompound nBTTagCompound2) {
        super.func_70014_b(nBTTagCompound2);
        nBTTagCompound2.func_74757_a("CanPickUpLoot", this.func_98052_bS());
        nBTTagCompound2.func_74757_a("PersistenceRequired", this.field_82179_bU);
        NBTTagList nBTTagList = new NBTTagList();
        for (ItemStack itemStack : this.field_184657_bw) {
            NBTTagCompound nBTTagCompound = new NBTTagCompound();
            if (!itemStack.func_190926_b()) {
                itemStack.func_77955_b(nBTTagCompound);
            }
            nBTTagList.func_74742_a(nBTTagCompound);
        }
        nBTTagCompound2.func_74782_a("ArmorItems", nBTTagList);
        NBTTagList \u26032 = new NBTTagList();
        for (ItemStack itemStack : this.field_184656_bv) {
            NBTTagCompound nBTTagCompound3 = new NBTTagCompound();
            if (!itemStack.func_190926_b()) {
                itemStack.func_77955_b(nBTTagCompound3);
            }
            \u26032.func_74742_a(nBTTagCompound3);
        }
        nBTTagCompound2.func_74782_a("HandItems", \u26032);
        NBTTagList nBTTagList2 = new NBTTagList();
        for (float object : this.field_184655_bs) {
            nBTTagList2.func_74742_a(new NBTTagFloat(object));
        }
        nBTTagCompound2.func_74782_a("ArmorDropChances", nBTTagList2);
        NBTTagList nBTTagList3 = new NBTTagList();
        for (float f : this.field_82174_bp) {
            nBTTagList3.func_74742_a(new NBTTagFloat(f));
        }
        nBTTagCompound2.func_74782_a("HandDropChances", nBTTagList3);
        nBTTagCompound2.func_74757_a("Leashed", this.field_110169_bv);
        if (this.field_110168_bw != null) {
            NBTTagCompound nBTTagCompound = new NBTTagCompound();
            if (this.field_110168_bw instanceof EntityLivingBase) {
                UUID uUID = this.field_110168_bw.func_110124_au();
                nBTTagCompound.func_186854_a("UUID", uUID);
            } else if (this.field_110168_bw instanceof EntityHanging) {
                BlockPos blockPos = ((EntityHanging)this.field_110168_bw).func_174857_n();
                nBTTagCompound.func_74768_a("X", blockPos.func_177958_n());
                nBTTagCompound.func_74768_a("Y", blockPos.func_177956_o());
                nBTTagCompound.func_74768_a("Z", blockPos.func_177952_p());
            }
            nBTTagCompound2.func_74782_a("Leash", nBTTagCompound);
        }
        nBTTagCompound2.func_74757_a("LeftHanded", this.func_184638_cS());
        if (this.field_184659_bA != null) {
            nBTTagCompound2.func_74778_a("DeathLootTable", this.field_184659_bA.toString());
            if (this.field_184653_bB != 0L) {
                nBTTagCompound2.func_74772_a("DeathLootTableSeed", this.field_184653_bB);
            }
        }
        if (this.func_175446_cd()) {
            nBTTagCompound2.func_74757_a("NoAI", this.func_175446_cd());
        }
    }

    @Override
    public void func_70037_a(NBTTagCompound nBTTagCompound2) {
        NBTTagCompound nBTTagCompound2;
        int n;
        NBTTagList nBTTagList;
        super.func_70037_a(nBTTagCompound2);
        if (nBTTagCompound2.func_150297_b("CanPickUpLoot", 1)) {
            this.func_98053_h(nBTTagCompound2.func_74767_n("CanPickUpLoot"));
        }
        this.field_82179_bU = nBTTagCompound2.func_74767_n("PersistenceRequired");
        if (nBTTagCompound2.func_150297_b("ArmorItems", 9)) {
            nBTTagList = nBTTagCompound2.func_150295_c("ArmorItems", 10);
            for (n = 0; n < this.field_184657_bw.size(); ++n) {
                this.field_184657_bw.set(n, new ItemStack(nBTTagList.func_150305_b(n)));
            }
        }
        if (nBTTagCompound2.func_150297_b("HandItems", 9)) {
            nBTTagList = nBTTagCompound2.func_150295_c("HandItems", 10);
            for (n = 0; n < this.field_184656_bv.size(); ++n) {
                this.field_184656_bv.set(n, new ItemStack(nBTTagList.func_150305_b(n)));
            }
        }
        if (nBTTagCompound2.func_150297_b("ArmorDropChances", 9)) {
            nBTTagList = nBTTagCompound2.func_150295_c("ArmorDropChances", 5);
            for (n = 0; n < nBTTagList.func_74745_c(); ++n) {
                this.field_184655_bs[n] = nBTTagList.func_150308_e(n);
            }
        }
        if (nBTTagCompound2.func_150297_b("HandDropChances", 9)) {
            nBTTagList = nBTTagCompound2.func_150295_c("HandDropChances", 5);
            for (n = 0; n < nBTTagList.func_74745_c(); ++n) {
                this.field_82174_bp[n] = nBTTagList.func_150308_e(n);
            }
        }
        this.field_110169_bv = nBTTagCompound2.func_74767_n("Leashed");
        if (this.field_110169_bv && nBTTagCompound2.func_150297_b("Leash", 10)) {
            this.field_110170_bx = nBTTagCompound2.func_74775_l("Leash");
        }
        this.func_184641_n(nBTTagCompound2.func_74767_n("LeftHanded"));
        if (nBTTagCompound2.func_150297_b("DeathLootTable", 8)) {
            this.field_184659_bA = new ResourceLocation(nBTTagCompound2.func_74779_i("DeathLootTable"));
            this.field_184653_bB = nBTTagCompound2.func_74763_f("DeathLootTableSeed");
        }
        this.func_94061_f(nBTTagCompound2.func_74767_n("NoAI"));
    }

    @Nullable
    protected ResourceLocation func_184647_J() {
        return null;
    }

    @Override
    protected void func_184610_a(boolean bl2, int n, DamageSource damageSource) {
        boolean bl2;
        ResourceLocation resourceLocation = this.field_184659_bA;
        if (resourceLocation == null) {
            resourceLocation = this.func_184647_J();
        }
        if (resourceLocation != null) {
            LootTable lootTable = this.field_70170_p.func_184146_ak().func_186521_a(resourceLocation);
            this.field_184659_bA = null;
            LootContext.Builder \u26032 = new LootContext.Builder((WorldServer)this.field_70170_p).func_186472_a(this).func_186473_a(damageSource);
            if (bl2 && this.field_70717_bb != null) {
                \u26032 = \u26032.func_186470_a(this.field_70717_bb).func_186469_a(this.field_70717_bb.func_184817_da());
            }
            List<ItemStack> \u26033 = lootTable.func_186462_a(this.field_184653_bB == 0L ? this.field_70146_Z : new Random(this.field_184653_bB), \u26032.func_186471_a());
            for (ItemStack itemStack : \u26033) {
                this.func_70099_a(itemStack, 0.0f);
            }
            this.func_82160_b(bl2, n);
        } else {
            super.func_184610_a(bl2, n, damageSource);
        }
    }

    public void func_191989_p(float f) {
        this.field_191988_bg = f;
    }

    public void func_70657_f(float f) {
        this.field_70701_bs = f;
    }

    public void func_184646_p(float f) {
        this.field_70702_br = f;
    }

    @Override
    public void func_70659_e(float f) {
        super.func_70659_e(f);
        this.func_191989_p(f);
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
        this.field_70170_p.field_72984_F.func_76320_a("looting");
        if (!this.field_70170_p.field_72995_K && this.func_98052_bS() && !this.field_70729_aU && this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
            List<EntityItem> list = this.field_70170_p.func_72872_a(EntityItem.class, this.func_174813_aQ().func_72314_b(1.0, 0.0, 1.0));
            for (EntityItem entityItem : list) {
                if (entityItem.field_70128_L || entityItem.func_92059_d().func_190926_b() || entityItem.func_174874_s()) continue;
                this.func_175445_a(entityItem);
            }
        }
        this.field_70170_p.field_72984_F.func_76319_b();
    }

    protected void func_175445_a(EntityItem entityItem) {
        ItemStack itemStack = entityItem.func_92059_d();
        EntityEquipmentSlot \u26032 = EntityLiving.func_184640_d(itemStack);
        boolean \u26033 = true;
        itemStack2 = this.func_184582_a(\u26032);
        if (!itemStack2.func_190926_b()) {
            if (\u26032.func_188453_a() == EntityEquipmentSlot.Type.HAND) {
                if (itemStack.func_77973_b() instanceof ItemSword && !(itemStack2.func_77973_b() instanceof ItemSword)) {
                    \u26033 = true;
                } else if (itemStack.func_77973_b() instanceof ItemSword && itemStack2.func_77973_b() instanceof ItemSword) {
                    Item item = (ItemSword)itemStack.func_77973_b();
                    ItemSword \u26034 = (ItemSword)itemStack2.func_77973_b();
                    \u26033 = ((ItemSword)item).func_150931_i() == \u26034.func_150931_i() ? itemStack.func_77960_j() > itemStack2.func_77960_j() || itemStack.func_77942_o() && !itemStack2.func_77942_o() : ((ItemSword)item).func_150931_i() > \u26034.func_150931_i();
                } else {
                    \u26033 = itemStack.func_77973_b() instanceof ItemBow && itemStack2.func_77973_b() instanceof ItemBow ? itemStack.func_77942_o() && !itemStack2.func_77942_o() : false;
                }
            } else if (itemStack.func_77973_b() instanceof ItemArmor && !(itemStack2.func_77973_b() instanceof ItemArmor)) {
                \u26033 = true;
            } else if (itemStack.func_77973_b() instanceof ItemArmor && itemStack2.func_77973_b() instanceof ItemArmor && !EnchantmentHelper.func_190938_b(itemStack2)) {
                item = (ItemArmor)itemStack.func_77973_b();
                ItemArmor itemArmor = (ItemArmor)itemStack2.func_77973_b();
                \u26033 = ((ItemArmor)item).field_77879_b == itemArmor.field_77879_b ? itemStack.func_77960_j() > itemStack2.func_77960_j() || itemStack.func_77942_o() && !itemStack2.func_77942_o() : ((ItemArmor)item).field_77879_b > itemArmor.field_77879_b;
            } else {
                \u26033 = false;
            }
        }
        if (\u26033 && this.func_175448_a(itemStack)) {
            ItemStack itemStack2;
            switch (\u26032.func_188453_a()) {
                case HAND: {
                    double d = this.field_82174_bp[\u26032.func_188454_b()];
                    break;
                }
                case ARMOR: {
                    double d = this.field_184655_bs[\u26032.func_188454_b()];
                    break;
                }
                default: {
                    double d = 0.0;
                }
            }
            if (!itemStack2.func_190926_b() && (double)(this.field_70146_Z.nextFloat() - 0.1f) < d) {
                this.func_70099_a(itemStack2, 0.0f);
            }
            this.func_184201_a(\u26032, itemStack);
            switch (\u26032.func_188453_a()) {
                case HAND: {
                    this.field_82174_bp[\u26032.func_188454_b()] = 2.0f;
                    break;
                }
                case ARMOR: {
                    this.field_184655_bs[\u26032.func_188454_b()] = 2.0f;
                }
            }
            this.field_82179_bU = true;
            this.func_71001_a(entityItem, itemStack.func_190916_E());
            entityItem.func_70106_y();
        }
    }

    protected boolean func_175448_a(ItemStack itemStack) {
        return true;
    }

    protected boolean func_70692_ba() {
        return true;
    }

    protected void func_70623_bb() {
        if (this.field_82179_bU) {
            this.field_70708_bq = 0;
            return;
        }
        EntityPlayer entityPlayer = this.field_70170_p.func_72890_a(this, -1.0);
        if (entityPlayer != null) {
            double d = entityPlayer.field_70165_t - this.field_70165_t;
            \u2603 = entityPlayer.field_70163_u - this.field_70163_u;
            \u2603 = entityPlayer.field_70161_v - this.field_70161_v;
            \u2603 = d * d + \u2603 * \u2603 + \u2603 * \u2603;
            if (this.func_70692_ba() && \u2603 > 16384.0) {
                this.func_70106_y();
            }
            if (this.field_70708_bq > 600 && this.field_70146_Z.nextInt(800) == 0 && \u2603 > 1024.0 && this.func_70692_ba()) {
                this.func_70106_y();
            } else if (\u2603 < 1024.0) {
                this.field_70708_bq = 0;
            }
        }
    }

    @Override
    protected final void func_70626_be() {
        ++this.field_70708_bq;
        this.field_70170_p.field_72984_F.func_76320_a("checkDespawn");
        this.func_70623_bb();
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("sensing");
        this.field_70723_bA.func_75523_a();
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("targetSelector");
        this.field_70715_bh.func_75774_a();
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("goalSelector");
        this.field_70714_bg.func_75774_a();
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("navigation");
        this.field_70699_by.func_75501_e();
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76320_a("mob tick");
        this.func_70619_bc();
        this.field_70170_p.field_72984_F.func_76319_b();
        if (this.func_184218_aH() && this.func_184187_bx() instanceof EntityLiving) {
            EntityLiving entityLiving = (EntityLiving)this.func_184187_bx();
            entityLiving.func_70661_as().func_75484_a(this.func_70661_as().func_75505_d(), 1.5);
            entityLiving.func_70605_aq().func_188487_a(this.func_70605_aq());
        }
        this.field_70170_p.field_72984_F.func_76320_a("controls");
        this.field_70170_p.field_72984_F.func_76320_a("move");
        this.field_70765_h.func_75641_c();
        this.field_70170_p.field_72984_F.func_76318_c("look");
        this.field_70749_g.func_75649_a();
        this.field_70170_p.field_72984_F.func_76318_c("jump");
        this.field_70767_i.func_75661_b();
        this.field_70170_p.field_72984_F.func_76319_b();
        this.field_70170_p.field_72984_F.func_76319_b();
    }

    protected void func_70619_bc() {
    }

    public int func_70646_bf() {
        return 40;
    }

    public int func_184649_cE() {
        return 10;
    }

    public void func_70625_a(Entity entity, float f, float f2) {
        double \u26032;
        double d = entity.field_70165_t - this.field_70165_t;
        \u2603 = entity.field_70161_v - this.field_70161_v;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            \u26032 = entityLivingBase.field_70163_u + (double)entityLivingBase.func_70047_e() - (this.field_70163_u + (double)this.func_70047_e());
        } else {
            \u26032 = (entity.func_174813_aQ().field_72338_b + entity.func_174813_aQ().field_72337_e) / 2.0 - (this.field_70163_u + (double)this.func_70047_e());
        }
        double d2 = MathHelper.func_76133_a(d * d + \u2603 * \u2603);
        float \u26033 = (float)(MathHelper.func_181159_b(\u2603, d) * 57.2957763671875) - 90.0f;
        float \u26034 = (float)(-(MathHelper.func_181159_b(\u26032, d2) * 57.2957763671875));
        this.field_70125_A = this.func_70663_b(this.field_70125_A, \u26034, f2);
        this.field_70177_z = this.func_70663_b(this.field_70177_z, \u26033, f);
    }

    private float func_70663_b(float f, float f2, float f3) {
        \u2603 = MathHelper.func_76142_g(f2 - f);
        if (\u2603 > f3) {
            \u2603 = f3;
        }
        if (\u2603 < -f3) {
            \u2603 = -f3;
        }
        return f + \u2603;
    }

    public boolean func_70601_bi() {
        IBlockState iBlockState = this.field_70170_p.func_180495_p(new BlockPos(this).func_177977_b());
        return iBlockState.func_189884_a(this);
    }

    public boolean func_70058_J() {
        return !this.field_70170_p.func_72953_d(this.func_174813_aQ()) && this.field_70170_p.func_184144_a(this, this.func_174813_aQ()).isEmpty() && this.field_70170_p.func_72917_a(this.func_174813_aQ(), this);
    }

    public float func_70603_bj() {
        return 1.0f;
    }

    public int func_70641_bl() {
        return 4;
    }

    @Override
    public int func_82143_as() {
        if (this.func_70638_az() == null) {
            return 3;
        }
        int n = (int)(this.func_110143_aJ() - this.func_110138_aP() * 0.33f);
        if ((n -= (3 - this.field_70170_p.func_175659_aa().func_151525_a()) * 4) < 0) {
            n = 0;
        }
        return n + 3;
    }

    @Override
    public Iterable<ItemStack> func_184214_aD() {
        return this.field_184656_bv;
    }

    @Override
    public Iterable<ItemStack> func_184193_aE() {
        return this.field_184657_bw;
    }

    @Override
    public ItemStack func_184582_a(EntityEquipmentSlot entityEquipmentSlot) {
        switch (entityEquipmentSlot.func_188453_a()) {
            case HAND: {
                return this.field_184656_bv.get(entityEquipmentSlot.func_188454_b());
            }
            case ARMOR: {
                return this.field_184657_bw.get(entityEquipmentSlot.func_188454_b());
            }
        }
        return ItemStack.field_190927_a;
    }

    @Override
    public void func_184201_a(EntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {
        switch (entityEquipmentSlot.func_188453_a()) {
            case HAND: {
                this.field_184656_bv.set(entityEquipmentSlot.func_188454_b(), itemStack);
                break;
            }
            case ARMOR: {
                this.field_184657_bw.set(entityEquipmentSlot.func_188454_b(), itemStack);
            }
        }
    }

    @Override
    protected void func_82160_b(boolean bl, int n) {
        for (EntityEquipmentSlot entityEquipmentSlot2 : EntityEquipmentSlot.values()) {
            double \u26032;
            ItemStack itemStack = this.func_184582_a(entityEquipmentSlot2);
            switch (entityEquipmentSlot2.func_188453_a()) {
                case HAND: {
                    \u26032 = this.field_82174_bp[entityEquipmentSlot2.func_188454_b()];
                    break;
                }
                case ARMOR: {
                    EntityEquipmentSlot entityEquipmentSlot2;
                    \u26032 = this.field_184655_bs[entityEquipmentSlot2.func_188454_b()];
                    break;
                }
                default: {
                    \u26032 = 0.0;
                }
            }
            boolean bl2 = \u2603 = \u26032 > 1.0;
            if (itemStack.func_190926_b() || EnchantmentHelper.func_190939_c(itemStack) || !bl && !\u2603 || !((double)(this.field_70146_Z.nextFloat() - (float)n * 0.01f) < \u26032)) continue;
            if (!\u2603 && itemStack.func_77984_f()) {
                itemStack.func_77964_b(itemStack.func_77958_k() - this.field_70146_Z.nextInt(1 + this.field_70146_Z.nextInt(Math.max(itemStack.func_77958_k() - 3, 1))));
            }
            this.func_70099_a(itemStack, 0.0f);
        }
    }

    protected void func_180481_a(DifficultyInstance difficultyInstance) {
        if (this.field_70146_Z.nextFloat() < 0.15f * difficultyInstance.func_180170_c()) {
            int n = this.field_70146_Z.nextInt(2);
            float f = \u2603 = this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD ? 0.1f : 0.25f;
            if (this.field_70146_Z.nextFloat() < 0.095f) {
                ++n;
            }
            if (this.field_70146_Z.nextFloat() < 0.095f) {
                ++n;
            }
            if (this.field_70146_Z.nextFloat() < 0.095f) {
                ++n;
            }
            boolean \u26032 = true;
            for (EntityEquipmentSlot entityEquipmentSlot : EntityEquipmentSlot.values()) {
                if (entityEquipmentSlot.func_188453_a() != EntityEquipmentSlot.Type.ARMOR) continue;
                ItemStack itemStack = this.func_184582_a(entityEquipmentSlot);
                if (!\u26032 && this.field_70146_Z.nextFloat() < \u2603) break;
                \u26032 = false;
                if (!itemStack.func_190926_b() || (\u2603 = EntityLiving.func_184636_a(entityEquipmentSlot, n)) == null) continue;
                this.func_184201_a(entityEquipmentSlot, new ItemStack(\u2603));
            }
        }
    }

    public static EntityEquipmentSlot func_184640_d(ItemStack itemStack) {
        if (itemStack.func_77973_b() == Item.func_150898_a(Blocks.field_150423_aK) || itemStack.func_77973_b() == Items.field_151144_bL) {
            return EntityEquipmentSlot.HEAD;
        }
        if (itemStack.func_77973_b() instanceof ItemArmor) {
            return ((ItemArmor)itemStack.func_77973_b()).field_77881_a;
        }
        if (itemStack.func_77973_b() == Items.field_185160_cR) {
            return EntityEquipmentSlot.CHEST;
        }
        if (itemStack.func_77973_b() == Items.field_185159_cQ) {
            return EntityEquipmentSlot.OFFHAND;
        }
        return EntityEquipmentSlot.MAINHAND;
    }

    @Nullable
    public static Item func_184636_a(EntityEquipmentSlot entityEquipmentSlot, int n) {
        switch (entityEquipmentSlot) {
            case HEAD: {
                if (n == 0) {
                    return Items.field_151024_Q;
                }
                if (n == 1) {
                    return Items.field_151169_ag;
                }
                if (n == 2) {
                    return Items.field_151020_U;
                }
                if (n == 3) {
                    return Items.field_151028_Y;
                }
                if (n == 4) {
                    return Items.field_151161_ac;
                }
            }
            case CHEST: {
                if (n == 0) {
                    return Items.field_151027_R;
                }
                if (n == 1) {
                    return Items.field_151171_ah;
                }
                if (n == 2) {
                    return Items.field_151023_V;
                }
                if (n == 3) {
                    return Items.field_151030_Z;
                }
                if (n == 4) {
                    return Items.field_151163_ad;
                }
            }
            case LEGS: {
                if (n == 0) {
                    return Items.field_151026_S;
                }
                if (n == 1) {
                    return Items.field_151149_ai;
                }
                if (n == 2) {
                    return Items.field_151022_W;
                }
                if (n == 3) {
                    return Items.field_151165_aa;
                }
                if (n == 4) {
                    return Items.field_151173_ae;
                }
            }
            case FEET: {
                if (n == 0) {
                    return Items.field_151021_T;
                }
                if (n == 1) {
                    return Items.field_151151_aj;
                }
                if (n == 2) {
                    return Items.field_151029_X;
                }
                if (n == 3) {
                    return Items.field_151167_ab;
                }
                if (n != 4) break;
                return Items.field_151175_af;
            }
        }
        return null;
    }

    protected void func_180483_b(DifficultyInstance difficultyInstance) {
        float f = difficultyInstance.func_180170_c();
        if (!this.func_184614_ca().func_190926_b() && this.field_70146_Z.nextFloat() < 0.25f * f) {
            this.func_184201_a(EntityEquipmentSlot.MAINHAND, EnchantmentHelper.func_77504_a(this.field_70146_Z, this.func_184614_ca(), (int)(5.0f + f * (float)this.field_70146_Z.nextInt(18)), false));
        }
        for (EntityEquipmentSlot entityEquipmentSlot : EntityEquipmentSlot.values()) {
            if (entityEquipmentSlot.func_188453_a() != EntityEquipmentSlot.Type.ARMOR || (\u2603 = this.func_184582_a(entityEquipmentSlot)).func_190926_b() || !(this.field_70146_Z.nextFloat() < 0.5f * f)) continue;
            this.func_184201_a(entityEquipmentSlot, EnchantmentHelper.func_77504_a(this.field_70146_Z, \u2603, (int)(5.0f + f * (float)this.field_70146_Z.nextInt(18)), false));
        }
    }

    @Nullable
    public IEntityLivingData func_180482_a(DifficultyInstance difficultyInstance, @Nullable IEntityLivingData iEntityLivingData) {
        this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111121_a(new AttributeModifier("Random spawn bonus", this.field_70146_Z.nextGaussian() * 0.05, 1));
        if (this.field_70146_Z.nextFloat() < 0.05f) {
            this.func_184641_n(true);
        } else {
            this.func_184641_n(false);
        }
        return iEntityLivingData;
    }

    public boolean func_82171_bF() {
        return false;
    }

    public void func_110163_bv() {
        this.field_82179_bU = true;
    }

    public void func_184642_a(EntityEquipmentSlot entityEquipmentSlot, float f) {
        switch (entityEquipmentSlot.func_188453_a()) {
            case HAND: {
                this.field_82174_bp[entityEquipmentSlot.func_188454_b()] = f;
                break;
            }
            case ARMOR: {
                this.field_184655_bs[entityEquipmentSlot.func_188454_b()] = f;
            }
        }
    }

    public boolean func_98052_bS() {
        return this.field_82172_bs;
    }

    public void func_98053_h(boolean bl) {
        this.field_82172_bs = bl;
    }

    public boolean func_104002_bU() {
        return this.field_82179_bU;
    }

    @Override
    public final boolean func_184230_a(EntityPlayer entityPlayer, EnumHand enumHand) {
        if (this.func_110167_bD() && this.func_110166_bE() == entityPlayer) {
            this.func_110160_i(true, !entityPlayer.field_71075_bZ.field_75098_d);
            return true;
        }
        ItemStack itemStack = entityPlayer.func_184586_b(enumHand);
        if (itemStack.func_77973_b() == Items.field_151058_ca && this.func_184652_a(entityPlayer)) {
            this.func_110162_b(entityPlayer, true);
            itemStack.func_190918_g(1);
            return true;
        }
        if (this.func_184645_a(entityPlayer, enumHand)) {
            return true;
        }
        return super.func_184230_a(entityPlayer, enumHand);
    }

    protected boolean func_184645_a(EntityPlayer entityPlayer, EnumHand enumHand) {
        return false;
    }

    protected void func_110159_bB() {
        if (this.field_110170_bx != null) {
            this.func_110165_bF();
        }
        if (!this.field_110169_bv) {
            return;
        }
        if (!this.func_70089_S()) {
            this.func_110160_i(true, true);
        }
        if (this.field_110168_bw == null || this.field_110168_bw.field_70128_L) {
            this.func_110160_i(true, true);
            return;
        }
    }

    public void func_110160_i(boolean bl, boolean bl2) {
        if (this.field_110169_bv) {
            this.field_110169_bv = false;
            this.field_110168_bw = null;
            if (!this.field_70170_p.field_72995_K && bl2) {
                this.func_145779_a(Items.field_151058_ca, 1);
            }
            if (!this.field_70170_p.field_72995_K && bl && this.field_70170_p instanceof WorldServer) {
                ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new SPacketEntityAttach(this, null));
            }
        }
    }

    public boolean func_184652_a(EntityPlayer entityPlayer) {
        return !this.func_110167_bD() && !(this instanceof IMob);
    }

    public boolean func_110167_bD() {
        return this.field_110169_bv;
    }

    public Entity func_110166_bE() {
        return this.field_110168_bw;
    }

    public void func_110162_b(Entity entity, boolean bl) {
        this.field_110169_bv = true;
        this.field_110168_bw = entity;
        if (!this.field_70170_p.field_72995_K && bl && this.field_70170_p instanceof WorldServer) {
            ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new SPacketEntityAttach(this, this.field_110168_bw));
        }
        if (this.func_184218_aH()) {
            this.func_184210_p();
        }
    }

    @Override
    public boolean func_184205_a(Entity entity, boolean bl) {
        \u2603 = super.func_184205_a(entity, bl);
        if (\u2603 && this.func_110167_bD()) {
            this.func_110160_i(true, true);
        }
        return \u2603;
    }

    private void func_110165_bF() {
        if (this.field_110169_bv && this.field_110170_bx != null) {
            if (this.field_110170_bx.func_186855_b("UUID")) {
                UUID uUID = this.field_110170_bx.func_186857_a("UUID");
                List<EntityLivingBase> \u26032 = this.field_70170_p.func_72872_a(EntityLivingBase.class, this.func_174813_aQ().func_186662_g(10.0));
                for (EntityLivingBase entityLivingBase : \u26032) {
                    if (!entityLivingBase.func_110124_au().equals(uUID)) continue;
                    this.func_110162_b(entityLivingBase, true);
                    break;
                }
            } else if (this.field_110170_bx.func_150297_b("X", 99) && this.field_110170_bx.func_150297_b("Y", 99) && this.field_110170_bx.func_150297_b("Z", 99)) {
                BlockPos blockPos = new BlockPos(this.field_110170_bx.func_74762_e("X"), this.field_110170_bx.func_74762_e("Y"), this.field_110170_bx.func_74762_e("Z"));
                EntityLeashKnot \u26033 = EntityLeashKnot.func_174863_b(this.field_70170_p, blockPos);
                if (\u26033 == null) {
                    \u26033 = EntityLeashKnot.func_174862_a(this.field_70170_p, blockPos);
                }
                this.func_110162_b(\u26033, true);
            } else {
                this.func_110160_i(false, true);
            }
        }
        this.field_110170_bx = null;
    }

    @Override
    public boolean func_174820_d(int n2, ItemStack itemStack2) {
        ItemStack itemStack2;
        int n2;
        EntityEquipmentSlot entityEquipmentSlot;
        if (n2 == 98) {
            entityEquipmentSlot = EntityEquipmentSlot.MAINHAND;
        } else if (n2 == 99) {
            entityEquipmentSlot = EntityEquipmentSlot.OFFHAND;
        } else if (n2 == 100 + EntityEquipmentSlot.HEAD.func_188454_b()) {
            entityEquipmentSlot = EntityEquipmentSlot.HEAD;
        } else if (n2 == 100 + EntityEquipmentSlot.CHEST.func_188454_b()) {
            entityEquipmentSlot = EntityEquipmentSlot.CHEST;
        } else if (n2 == 100 + EntityEquipmentSlot.LEGS.func_188454_b()) {
            entityEquipmentSlot = EntityEquipmentSlot.LEGS;
        } else if (n2 == 100 + EntityEquipmentSlot.FEET.func_188454_b()) {
            entityEquipmentSlot = EntityEquipmentSlot.FEET;
        } else {
            return false;
        }
        if (itemStack2.func_190926_b() || EntityLiving.func_184648_b(entityEquipmentSlot, itemStack2) || entityEquipmentSlot == EntityEquipmentSlot.HEAD) {
            this.func_184201_a(entityEquipmentSlot, itemStack2);
            return true;
        }
        return false;
    }

    @Override
    public boolean func_184186_bw() {
        return this.func_82171_bF() && super.func_184186_bw();
    }

    public static boolean func_184648_b(EntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {
        EntityEquipmentSlot entityEquipmentSlot2 = EntityLiving.func_184640_d(itemStack);
        return entityEquipmentSlot2 == entityEquipmentSlot || entityEquipmentSlot2 == EntityEquipmentSlot.MAINHAND && entityEquipmentSlot == EntityEquipmentSlot.OFFHAND || entityEquipmentSlot2 == EntityEquipmentSlot.OFFHAND && entityEquipmentSlot == EntityEquipmentSlot.MAINHAND;
    }

    @Override
    public boolean func_70613_aW() {
        return super.func_70613_aW() && !this.func_175446_cd();
    }

    public void func_94061_f(boolean bl) {
        byte by = this.field_70180_af.func_187225_a(field_184654_a);
        this.field_70180_af.func_187227_b(field_184654_a, bl ? (byte)(by | 1) : (byte)(by & 0xFFFFFFFE));
    }

    public void func_184641_n(boolean bl) {
        byte by = this.field_70180_af.func_187225_a(field_184654_a);
        this.field_70180_af.func_187227_b(field_184654_a, bl ? (byte)(by | 2) : (byte)(by & 0xFFFFFFFD));
    }

    public boolean func_175446_cd() {
        return (this.field_70180_af.func_187225_a(field_184654_a) & 1) != 0;
    }

    public boolean func_184638_cS() {
        return (this.field_70180_af.func_187225_a(field_184654_a) & 2) != 0;
    }

    @Override
    public EnumHandSide func_184591_cq() {
        return this.func_184638_cS() ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
    }

    public static enum SpawnPlacementType {
        ON_GROUND,
        IN_AIR,
        IN_WATER;

    }
}

