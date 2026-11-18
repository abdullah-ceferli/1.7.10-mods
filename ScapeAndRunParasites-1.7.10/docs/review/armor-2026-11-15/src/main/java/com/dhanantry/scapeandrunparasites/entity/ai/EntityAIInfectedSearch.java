/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityAIInfectedSearch
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private World world;
    private int parentID;
    private double moveSpeed;
    private EntityParasiteBase[] array = new EntityParasiteBase[4];
    private int delay;

    public EntityAIInfectedSearch(EntityParasiteBase animal, double speedIn) {
        this.parent = animal;
        this.world = animal.worldObj;
        this.moveSpeed = speedIn;
        this.parentID = animal.getEntityId();
    }

    public boolean shouldExecute() {
        if (this.parent.ticksExisted % 40 == 0) {
            return false;
        }
        if (this.parent.getAttackTarget() != null) {
            return false;
        }
        return this.LeshCount();
    }

    public void resetTask() {
    }

    public void updateTask() {
        if (this.parent.ticksExisted % 20 != 0 || this.parent.getKillC() < 0.0) {
            return;
        }
        int count = 0;
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0, this.parent.posY + 1.0, this.parent.posZ + 1.0), this.parent.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue());
        List moblist = EntityQueries.entities(this.parent.worldObj, EntityPInfected.class, axisalignedbb);
        List moblist2 = EntityQueries.entities(this.parent.worldObj, EntityLesh.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist2) {
            if (mob == null) continue;
            ++count;
        }
        if (count >= 3) {
            EntityCanMelt meh = (EntityCanMelt)((Object)this.parent);
            meh.melt();
            return;
        }
        for (EntityParasiteBase mob : moblist) {
            EntityCanMelt meh;
            if (mob == null || mob == this.parent || !(mob instanceof EntityCanMelt) || !this.parent.canEntityBeSeen((Entity)mob) || mob.getAttackTarget() != null || (meh = (EntityCanMelt)((Object)mob)).isMelting()) continue;
            meh.melt();
            if (++count < 3) continue;
            EntityCanMelt moh = (EntityCanMelt)((Object)this.parent);
            moh.melt();
            return;
        }
        EntityCanMelt out = (EntityCanMelt)((Object)this.parent);
        out.melt();
    }

    protected boolean LeshCount() {
        int countL = 0;
        int countI = 0;
        boolean more = false;
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0, this.parent.posY + 1.0, this.parent.posZ + 1.0), this.parent.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue());
        List moblist = EntityQueries.entities(this.parent.worldObj, EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            Object meh;
            if (mob == null) continue;
            if (mob instanceof EntityLesh && this.parent.canEntityBeSeen((Entity)mob)) {
                meh = (EntityLesh)mob;
                countL += ((EntityLesh)meh).getTimes();
            }
            if (mob == this.parent || !this.parent.canEntityBeSeen((Entity)mob) || !(mob instanceof EntityCanMelt) || mob.getAttackTarget() != null || (meh = (EntityCanMelt)((Object)mob)).isMelting()) continue;
            ++countI;
        }
        if (countL >= 1 && countL <= 3) {
            EntityCanMelt meh = (EntityCanMelt)((Object)this.parent);
            meh.melt();
            return false;
        }
        return countI >= 3;
    }
}

