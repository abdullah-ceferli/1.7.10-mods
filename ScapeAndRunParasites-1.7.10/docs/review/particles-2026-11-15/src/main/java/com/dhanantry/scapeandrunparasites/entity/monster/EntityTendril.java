/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class EntityTendril
extends EntityParasiteBase {
    public EntityTendril(World worldIn) {
        super(worldIn);
        this.setSize(1.0f, 1.0f);
        this.tasks.removeTask((EntityAIBase)this.aiWander);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.killcount = -10.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 202;
    }

    public EntityTendril(World world, float width, float height) {
        super(world);
        this.setSize(width, height);
    }

    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public void setSkin(int texture) {
        super.setSkin(texture);
    }

    @Override
    public int getSkin() {
        return super.getSkin();
    }
}

