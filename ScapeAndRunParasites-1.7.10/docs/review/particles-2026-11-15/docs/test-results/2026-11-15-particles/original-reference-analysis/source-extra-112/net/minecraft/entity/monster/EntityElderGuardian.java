/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityElderGuardian
extends EntityGuardian {
    public EntityElderGuardian(World world) {
        super(world);
        this.func_70105_a(this.field_70130_N * 2.35f, this.field_70131_O * 2.35f);
        this.func_110163_bv();
        if (this.field_175481_bq != null) {
            this.field_175481_bq.func_179479_b(400);
        }
    }

    @Override
    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.3f);
        this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(8.0);
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(80.0);
    }

    public static void func_190768_b(DataFixer dataFixer) {
        EntityLiving.func_189752_a(dataFixer, EntityElderGuardian.class);
    }

    @Override
    @Nullable
    protected ResourceLocation func_184647_J() {
        return LootTableList.field_186441_w;
    }

    @Override
    public int func_175464_ck() {
        return 60;
    }

    public void func_190767_di() {
        this.field_175486_bm = this.field_175485_bl = 1.0f;
    }

    @Override
    protected SoundEvent func_184639_G() {
        return this.func_70090_H() ? SoundEvents.field_187512_aB : SoundEvents.field_187513_aC;
    }

    @Override
    protected SoundEvent func_184601_bQ(DamageSource damageSource) {
        return this.func_70090_H() ? SoundEvents.field_187517_aG : SoundEvents.field_187518_aH;
    }

    @Override
    protected SoundEvent func_184615_bR() {
        return this.func_70090_H() ? SoundEvents.field_187515_aE : SoundEvents.field_187516_aF;
    }

    @Override
    protected SoundEvent func_190765_dj() {
        return SoundEvents.field_191240_aK;
    }

    @Override
    protected void func_70619_bc() {
        super.func_70619_bc();
        int n = 1200;
        if ((this.field_70173_aa + this.func_145782_y()) % 1200 == 0) {
            Potion potion = MobEffects.field_76419_f;
            List<EntityPlayerMP> \u26032 = this.field_70170_p.func_175661_b(EntityPlayerMP.class, new Predicate<EntityPlayerMP>(){

                public boolean apply(@Nullable EntityPlayerMP entityPlayerMP) {
                    return EntityElderGuardian.this.func_70068_e(entityPlayerMP) < 2500.0 && entityPlayerMP.field_71134_c.func_180239_c();
                }

                public /* synthetic */ boolean apply(@Nullable Object object) {
                    return this.apply((EntityPlayerMP)object);
                }
            });
            int \u26033 = 2;
            int \u26034 = 6000;
            int \u26035 = 1200;
            for (EntityPlayerMP entityPlayerMP : \u26032) {
                if (entityPlayerMP.func_70644_a(potion) && entityPlayerMP.func_70660_b(potion).func_76458_c() >= 2 && entityPlayerMP.func_70660_b(potion).func_76459_b() >= 1200) continue;
                entityPlayerMP.field_71135_a.func_147359_a(new SPacketChangeGameState(10, 0.0f));
                entityPlayerMP.func_70690_d(new PotionEffect(potion, 6000, 2));
            }
        }
        if (!this.func_110175_bO()) {
            this.func_175449_a(new BlockPos(this), 16);
        }
    }
}

