/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAINearestAttackableTarget<T extends EntityLivingBase>
extends EntityAITarget {
    protected final Class<T> field_75307_b;
    private final int field_75308_c;
    protected final Sorter field_75306_g;
    protected final Predicate<? super T> field_82643_g;
    protected T field_75309_a;

    public EntityAINearestAttackableTarget(EntityCreature entityCreature, Class<T> clazz, boolean bl) {
        this(entityCreature, clazz, bl, false);
    }

    public EntityAINearestAttackableTarget(EntityCreature entityCreature, Class<T> clazz, boolean bl, boolean bl2) {
        this(entityCreature, clazz, 10, bl, bl2, null);
    }

    public EntityAINearestAttackableTarget(EntityCreature entityCreature, Class<T> clazz, int n, boolean bl, boolean bl2, final @Nullable Predicate<? super T> predicate) {
        super(entityCreature, bl, bl2);
        this.field_75307_b = clazz;
        this.field_75308_c = n;
        this.field_75306_g = new Sorter(entityCreature);
        this.func_75248_a(1);
        this.field_82643_g = new Predicate<T>(){

            public boolean apply(@Nullable T t) {
                if (t == null) {
                    return false;
                }
                if (predicate != null && !predicate.apply(t)) {
                    return false;
                }
                if (!EntitySelectors.field_180132_d.apply(t)) {
                    return false;
                }
                return EntityAINearestAttackableTarget.this.func_75296_a((EntityLivingBase)t, false);
            }

            public /* synthetic */ boolean apply(@Nullable Object object) {
                return this.apply((T)((EntityLivingBase)object));
            }
        };
    }

    @Override
    public boolean func_75250_a() {
        if (this.field_75308_c > 0 && this.field_75299_d.func_70681_au().nextInt(this.field_75308_c) != 0) {
            return false;
        }
        if (this.field_75307_b == EntityPlayer.class || this.field_75307_b == EntityPlayerMP.class) {
            this.field_75309_a = this.field_75299_d.field_70170_p.func_184150_a(this.field_75299_d.field_70165_t, this.field_75299_d.field_70163_u + (double)this.field_75299_d.func_70047_e(), this.field_75299_d.field_70161_v, this.func_111175_f(), this.func_111175_f(), new Function<EntityPlayer, Double>(){

                @Nullable
                public Double apply(@Nullable EntityPlayer entityPlayer) {
                    ItemStack itemStack = entityPlayer.func_184582_a(EntityEquipmentSlot.HEAD);
                    if (itemStack.func_77973_b() == Items.field_151144_bL) {
                        int n = itemStack.func_77952_i();
                        boolean \u26032 = EntityAINearestAttackableTarget.this.field_75299_d instanceof EntitySkeleton && n == 0;
                        boolean \u26033 = EntityAINearestAttackableTarget.this.field_75299_d instanceof EntityZombie && n == 2;
                        boolean bl = \u2603 = EntityAINearestAttackableTarget.this.field_75299_d instanceof EntityCreeper && n == 4;
                        if (\u26032 || \u26033 || \u2603) {
                            return 0.5;
                        }
                    }
                    return 1.0;
                }

                @Nullable
                public /* synthetic */ Object apply(@Nullable Object object) {
                    return this.apply((EntityPlayer)object);
                }
            }, this.field_82643_g);
            return this.field_75309_a != null;
        }
        List<T> list = this.field_75299_d.field_70170_p.func_175647_a(this.field_75307_b, this.func_188511_a(this.func_111175_f()), this.field_82643_g);
        if (list.isEmpty()) {
            return false;
        }
        Collections.sort(list, this.field_75306_g);
        this.field_75309_a = (EntityLivingBase)list.get(0);
        return true;
    }

    protected AxisAlignedBB func_188511_a(double d) {
        return this.field_75299_d.func_174813_aQ().func_72314_b(d, 4.0, d);
    }

    @Override
    public void func_75249_e() {
        this.field_75299_d.func_70624_b((EntityLivingBase)this.field_75309_a);
        super.func_75249_e();
    }

    public static class Sorter
    implements Comparator<Entity> {
        private final Entity field_75459_b;

        public Sorter(Entity entity) {
            this.field_75459_b = entity;
        }

        @Override
        public int compare(Entity entity, Entity entity2) {
            double d = this.field_75459_b.func_70068_e(entity);
            if (d < (\u2603 = this.field_75459_b.func_70068_e(entity2))) {
                return -1;
            }
            if (d > \u2603) {
                return 1;
            }
            return 0;
        }

        @Override
        public /* synthetic */ int compare(Object object, Object object2) {
            return this.compare((Entity)object, (Entity)object2);
        }
    }
}

