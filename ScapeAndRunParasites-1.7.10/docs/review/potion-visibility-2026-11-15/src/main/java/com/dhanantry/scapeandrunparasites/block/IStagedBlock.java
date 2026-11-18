/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.IBlockState
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;

public interface IStagedBlock {
    public PropertyInteger getStageProperty();

    default public IBlockState withStage(IBlockState base, int stage) {
        PropertyInteger prop = this.getStageProperty();
        if (prop != null && base.getPropertyKeys().contains(prop)) {
            return base.withProperty((IProperty)prop, (Comparable)Integer.valueOf(stage));
        }
        return base;
    }
}

