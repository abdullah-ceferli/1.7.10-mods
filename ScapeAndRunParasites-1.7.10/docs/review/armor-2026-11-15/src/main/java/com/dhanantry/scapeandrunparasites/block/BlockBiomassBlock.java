/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.Objects;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBiomassBlock
extends Block {
    private static final float DAMAGE_PER_HIT = 1.0f;
    private static final int APPLY_COOLDOWN_TICKS = 20;
    private static final String NBT_KEY_NEXT_APPLY = "srp_biomass_next_apply";
    private static final DamageSource SRC_BIOMASS = new DamageSource("biomass").setDamageBypassesArmor();

    public BlockBiomassBlock(String name) {
        this(name, true);
    }

    public BlockBiomassBlock(String name, boolean creative) {
        super(Material.clay);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setHardness(0.6f);
        this.setStepSound(SoundType.SLIME);
        this.slipperiness = 0.8f;
        this.setLightLevel(0.4f);
        if (creative) {
            this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        }
        SRPBlocks.SRP_BLOCKS.add(this);
        ItemBlock itemBlock = new ItemBlock((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(itemBlock, Objects.requireNonNull(RegistryNames.get(this))));
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityWalk(worldIn, pos, entityIn);
        this.tryAffect(worldIn, pos, entityIn);
    }

    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        this.tryAffect(worldIn, pos, entityIn);
    }

    private void tryAffect(World world, BlockPos pos, Entity entity) {
        NBTTagCompound tag;
        long nextAllowed;
        if (world.isRemote) {
            return;
        }
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
        if (now < (nextAllowed = (tag = mob.getEntityData()).getLong(NBT_KEY_NEXT_APPLY))) {
            return;
        }
        tag.setLong(NBT_KEY_NEXT_APPLY, now + 20L);
        mob.attackEntityFrom(SRC_BIOMASS, 1.0f);
        mob.removePotionEffect(SRPPotions.CORRO_E);
        mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.CORRO_E, 100, 0, false, true));
        mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, 1000, 3, false, true));
        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 200, 1);
    }

    @SideOnly(value=Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        BlockPos below;
        boolean openBelow;
        if (rand.nextFloat() < 0.25f) {
            boolean openAbove;
            double x = (double)pos.getX() + 0.2 + rand.nextDouble() * 0.6;
            double y = (double)pos.getY() + 0.05 + rand.nextDouble() * 0.9;
            double z = (double)pos.getZ() + 0.2 + rand.nextDouble() * 0.6;
            double r = 0.1;
            double g = 0.85;
            double b = 0.2;
            world.spawnParticle(EnumParticleTypes.SPELL_MOB_AMBIENT, x, y, z, r, g, b, new int[0]);
            BlockPos above = pos.up();
            boolean bl = openAbove = WorldAccess.isAirBlock(world, above) || !BlockStates.get(world, above).isSideSolid((IBlockAccess)world, above, EnumFacing.DOWN);
            if (openAbove) {
                ParticleSpawner.spawnParticle(SRPEnumParticle.DOT, x, y, z, 0.0, 0.0, 0.0, 165, 255, 0);
            }
        }
        boolean bl = openBelow = WorldAccess.isAirBlock(world, below = pos.down()) || !BlockStates.get(world, below).isSideSolid((IBlockAccess)world, below, EnumFacing.UP);
        if (openBelow && rand.nextFloat() < 0.1f) {
            double x = (double)pos.getX() + 0.25 + rand.nextDouble() * 0.5;
            double y = (double)pos.getY() + 0.01;
            double z = (double)pos.getZ() + 0.25 + rand.nextDouble() * 0.5;
            double vx = (rand.nextDouble() - 0.5) * 0.02;
            double vy = -0.07 - rand.nextDouble() * 0.03;
            double vz = (rand.nextDouble() - 0.5) * 0.02;
            world.spawnParticle(EnumParticleTypes.SLIME, x, y, z, vx, vy, vz, new int[0]);
        }
    }
}

