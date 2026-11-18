/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockParasiteMouth
extends BlockBase {
    private static final float DAMAGE_PER_HIT = 1.0f;
    private static final int APPLY_COOLDOWN_TICKS = 10;
    private static final String NBT_KEY_NEXT_APPLY = "srp_mouth_next_apply";
    private static final DamageSource SRC_PARASITE_MOUTH = new DamageSource("parasite_mouth").setDamageBypassesArmor();

    public BlockParasiteMouth(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
        super(material, name, hardness, creative, tickRandom);
        this.setDefaultState(this.blockState.getBaseState());
        this.setStepSound(SRPSoundTypes.FLESH);
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }

    public int damageDropped(IBlockState state) {
        return BlockStates.meta(this, state);
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (!world.isRemote && this.isOnTop(pos, entity)) {
            this.tryAffect(world, pos, entity);
        }
        super.onEntityWalk(world, pos, entity);
    }

    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote && this.isOnTop(pos, entity)) {
            this.tryAffect(world, pos, entity);
        }
        super.onEntityCollision(world, pos, state, entity);
    }

    private boolean isOnTop(BlockPos pos, Entity e) {
        return Boxes.entityBox(e).minY >= (double)pos.getY() + 0.999 && e.posX >= (double)pos.getX() && e.posX < (double)(pos.getX() + 1) && e.posZ >= (double)pos.getZ() && e.posZ < (double)(pos.getZ() + 1);
    }

    private void tryAffect(World world, BlockPos pos, Entity entity) {
        long nextAllowed;
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }
        if (entity instanceof EntityParasiteBase) {
            return;
        }
        if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) {
            return;
        }
        EntityLivingBase mob = (EntityLivingBase)entity;
        long now = world.getTotalWorldTime();
        if (now < (nextAllowed = mob.getEntityData().getLong(NBT_KEY_NEXT_APPLY))) {
            return;
        }
        mob.getEntityData().setLong(NBT_KEY_NEXT_APPLY, now + 10L);
        mob.attackEntityFrom(SRC_PARASITE_MOUTH, 1.0f);
        mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.CORRO_E, 100, 0, false, false));
        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 200, 1);
    }
}

