/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItem
extends Entity {
    private static final Logger field_145803_d = LogManager.getLogger();
    private static final DataParameter<ItemStack> field_184533_c = EntityDataManager.func_187226_a(EntityItem.class, DataSerializers.field_187196_f);
    private int field_70292_b;
    private int field_145804_b;
    private int field_70291_e = 5;
    private String field_145801_f;
    private String field_145802_g;
    public float field_70290_d = (float)(Math.random() * Math.PI * 2.0);

    public EntityItem(World world, double d, double d2, double d3) {
        super(world);
        this.func_70105_a(0.25f, 0.25f);
        this.func_70107_b(d, d2, d3);
        this.field_70177_z = (float)(Math.random() * 360.0);
        this.field_70159_w = (float)(Math.random() * (double)0.2f - (double)0.1f);
        this.field_70181_x = 0.2f;
        this.field_70179_y = (float)(Math.random() * (double)0.2f - (double)0.1f);
    }

    public EntityItem(World world, double d, double d2, double d3, ItemStack itemStack) {
        this(world, d, d2, d3);
        this.func_92058_a(itemStack);
    }

    @Override
    protected boolean func_70041_e_() {
        return false;
    }

    public EntityItem(World world) {
        super(world);
        this.func_70105_a(0.25f, 0.25f);
        this.func_92058_a(ItemStack.field_190927_a);
    }

    @Override
    protected void func_70088_a() {
        this.func_184212_Q().func_187214_a(field_184533_c, ItemStack.field_190927_a);
    }

    @Override
    public void func_70071_h_() {
        if (this.func_92059_d().func_190926_b()) {
            this.func_70106_y();
            return;
        }
        super.func_70071_h_();
        if (this.field_145804_b > 0 && this.field_145804_b != Short.MAX_VALUE) {
            --this.field_145804_b;
        }
        this.field_70169_q = this.field_70165_t;
        this.field_70167_r = this.field_70163_u;
        this.field_70166_s = this.field_70161_v;
        double d = this.field_70159_w;
        \u2603 = this.field_70181_x;
        \u2603 = this.field_70179_y;
        if (!this.func_189652_ae()) {
            this.field_70181_x -= (double)0.04f;
        }
        this.field_70145_X = this.field_70170_p.field_72995_K ? false : this.func_145771_j(this.field_70165_t, (this.func_174813_aQ().field_72338_b + this.func_174813_aQ().field_72337_e) / 2.0, this.field_70161_v);
        this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
        boolean bl = \u2603 = (int)this.field_70169_q != (int)this.field_70165_t || (int)this.field_70167_r != (int)this.field_70163_u || (int)this.field_70166_s != (int)this.field_70161_v;
        if (\u2603 || this.field_70173_aa % 25 == 0) {
            if (this.field_70170_p.func_180495_p(new BlockPos(this)).func_185904_a() == Material.field_151587_i) {
                this.field_70181_x = 0.2f;
                this.field_70159_w = (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f;
                this.field_70179_y = (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2f;
                this.func_184185_a(SoundEvents.field_187658_bx, 0.4f, 2.0f + this.field_70146_Z.nextFloat() * 0.4f);
            }
            if (!this.field_70170_p.field_72995_K) {
                this.func_85054_d();
            }
        }
        float \u26032 = 0.98f;
        if (this.field_70122_E) {
            \u26032 = this.field_70170_p.func_180495_p((BlockPos)new BlockPos((int)MathHelper.func_76128_c((double)this.field_70165_t), (int)(MathHelper.func_76128_c((double)this.func_174813_aQ().field_72338_b) - 1), (int)MathHelper.func_76128_c((double)this.field_70161_v))).func_177230_c().field_149765_K * 0.98f;
        }
        this.field_70159_w *= (double)\u26032;
        this.field_70181_x *= (double)0.98f;
        this.field_70179_y *= (double)\u26032;
        if (this.field_70122_E) {
            this.field_70181_x *= -0.5;
        }
        if (this.field_70292_b != Short.MIN_VALUE) {
            ++this.field_70292_b;
        }
        this.func_70072_I();
        if (!this.field_70170_p.field_72995_K && (\u2603 = (\u2603 = this.field_70159_w - d) * \u2603 + (\u2603 = this.field_70181_x - \u2603) * \u2603 + (\u2603 = this.field_70179_y - \u2603) * \u2603) > 0.01) {
            this.field_70160_al = true;
        }
        if (!this.field_70170_p.field_72995_K && this.field_70292_b >= 6000) {
            this.func_70106_y();
        }
    }

    private void func_85054_d() {
        for (EntityItem entityItem : this.field_70170_p.func_72872_a(EntityItem.class, this.func_174813_aQ().func_72314_b(0.5, 0.0, 0.5))) {
            this.func_70289_a(entityItem);
        }
    }

    private boolean func_70289_a(EntityItem entityItem) {
        if (entityItem == this) {
            return false;
        }
        if (!entityItem.func_70089_S() || !this.func_70089_S()) {
            return false;
        }
        ItemStack itemStack = this.func_92059_d();
        \u2603 = entityItem.func_92059_d();
        if (this.field_145804_b == Short.MAX_VALUE || entityItem.field_145804_b == Short.MAX_VALUE) {
            return false;
        }
        if (this.field_70292_b == Short.MIN_VALUE || entityItem.field_70292_b == Short.MIN_VALUE) {
            return false;
        }
        if (\u2603.func_77973_b() != itemStack.func_77973_b()) {
            return false;
        }
        if (\u2603.func_77942_o() ^ itemStack.func_77942_o()) {
            return false;
        }
        if (\u2603.func_77942_o() && !\u2603.func_77978_p().equals(itemStack.func_77978_p())) {
            return false;
        }
        if (\u2603.func_77973_b() == null) {
            return false;
        }
        if (\u2603.func_77973_b().func_77614_k() && \u2603.func_77960_j() != itemStack.func_77960_j()) {
            return false;
        }
        if (\u2603.func_190916_E() < itemStack.func_190916_E()) {
            return entityItem.func_70289_a(this);
        }
        if (\u2603.func_190916_E() + itemStack.func_190916_E() > \u2603.func_77976_d()) {
            return false;
        }
        \u2603.func_190917_f(itemStack.func_190916_E());
        entityItem.field_145804_b = Math.max(entityItem.field_145804_b, this.field_145804_b);
        entityItem.field_70292_b = Math.min(entityItem.field_70292_b, this.field_70292_b);
        entityItem.func_92058_a(\u2603);
        this.func_70106_y();
        return true;
    }

    public void func_70288_d() {
        this.field_70292_b = 4800;
    }

    @Override
    public boolean func_70072_I() {
        if (this.field_70170_p.func_72918_a(this.func_174813_aQ(), Material.field_151586_h, this)) {
            if (!this.field_70171_ac && !this.field_70148_d) {
                this.func_71061_d_();
            }
            this.field_70171_ac = true;
        } else {
            this.field_70171_ac = false;
        }
        return this.field_70171_ac;
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
        if (!this.func_92059_d().func_190926_b() && this.func_92059_d().func_77973_b() == Items.field_151156_bN && damageSource.func_94541_c()) {
            return false;
        }
        this.func_70018_K();
        this.field_70291_e = (int)((float)this.field_70291_e - f);
        if (this.field_70291_e <= 0) {
            this.func_70106_y();
        }
        return false;
    }

    public static void func_189742_a(DataFixer dataFixer) {
        dataFixer.func_188258_a(FixTypes.ENTITY, new ItemStackData(EntityItem.class, "Item"));
    }

    @Override
    public void func_70014_b(NBTTagCompound nBTTagCompound) {
        nBTTagCompound.func_74777_a("Health", (short)this.field_70291_e);
        nBTTagCompound.func_74777_a("Age", (short)this.field_70292_b);
        nBTTagCompound.func_74777_a("PickupDelay", (short)this.field_145804_b);
        if (this.func_145800_j() != null) {
            nBTTagCompound.func_74778_a("Thrower", this.field_145801_f);
        }
        if (this.func_145798_i() != null) {
            nBTTagCompound.func_74778_a("Owner", this.field_145802_g);
        }
        if (!this.func_92059_d().func_190926_b()) {
            nBTTagCompound.func_74782_a("Item", this.func_92059_d().func_77955_b(new NBTTagCompound()));
        }
    }

    @Override
    public void func_70037_a(NBTTagCompound nBTTagCompound) {
        this.field_70291_e = nBTTagCompound.func_74765_d("Health");
        this.field_70292_b = nBTTagCompound.func_74765_d("Age");
        if (nBTTagCompound.func_74764_b("PickupDelay")) {
            this.field_145804_b = nBTTagCompound.func_74765_d("PickupDelay");
        }
        if (nBTTagCompound.func_74764_b("Owner")) {
            this.field_145802_g = nBTTagCompound.func_74779_i("Owner");
        }
        if (nBTTagCompound.func_74764_b("Thrower")) {
            this.field_145801_f = nBTTagCompound.func_74779_i("Thrower");
        }
        \u2603 = nBTTagCompound.func_74775_l("Item");
        this.func_92058_a(new ItemStack(\u2603));
        if (this.func_92059_d().func_190926_b()) {
            this.func_70106_y();
        }
    }

    @Override
    public void func_70100_b_(EntityPlayer entityPlayer) {
        if (this.field_70170_p.field_72995_K) {
            return;
        }
        ItemStack itemStack = this.func_92059_d();
        Item \u26032 = itemStack.func_77973_b();
        int \u26033 = itemStack.func_190916_E();
        if (this.field_145804_b == 0 && (this.field_145802_g == null || 6000 - this.field_70292_b <= 200 || this.field_145802_g.equals(entityPlayer.func_70005_c_())) && entityPlayer.field_71071_by.func_70441_a(itemStack)) {
            entityPlayer.func_71001_a(this, \u26033);
            if (itemStack.func_190926_b()) {
                this.func_70106_y();
                itemStack.func_190920_e(\u26033);
            }
            entityPlayer.func_71064_a(StatList.func_188056_d(\u26032), \u26033);
        }
    }

    @Override
    public String func_70005_c_() {
        if (this.func_145818_k_()) {
            return this.func_95999_t();
        }
        return I18n.func_74838_a("item." + this.func_92059_d().func_77977_a());
    }

    @Override
    public boolean func_70075_an() {
        return false;
    }

    @Override
    @Nullable
    public Entity func_184204_a(int n) {
        Entity entity = super.func_184204_a(n);
        if (!this.field_70170_p.field_72995_K && entity instanceof EntityItem) {
            ((EntityItem)entity).func_85054_d();
        }
        return entity;
    }

    public ItemStack func_92059_d() {
        return this.func_184212_Q().func_187225_a(field_184533_c);
    }

    public void func_92058_a(ItemStack itemStack) {
        this.func_184212_Q().func_187227_b(field_184533_c, itemStack);
        this.func_184212_Q().func_187217_b(field_184533_c);
    }

    public String func_145798_i() {
        return this.field_145802_g;
    }

    public void func_145797_a(String string) {
        this.field_145802_g = string;
    }

    public String func_145800_j() {
        return this.field_145801_f;
    }

    public void func_145799_b(String string) {
        this.field_145801_f = string;
    }

    public int func_174872_o() {
        return this.field_70292_b;
    }

    public void func_174869_p() {
        this.field_145804_b = 10;
    }

    public void func_174868_q() {
        this.field_145804_b = 0;
    }

    public void func_174871_r() {
        this.field_145804_b = Short.MAX_VALUE;
    }

    public void func_174867_a(int n) {
        this.field_145804_b = n;
    }

    public boolean func_174874_s() {
        return this.field_145804_b > 0;
    }

    public void func_174873_u() {
        this.field_70292_b = -6000;
    }

    public void func_174870_v() {
        this.func_174871_r();
        this.field_70292_b = 5999;
    }
}

