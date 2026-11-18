/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.CriteriaTriggers
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.material.MaterialLiquid
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.stats.StatList
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumBlockRenderType
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.BlockFluidClassic
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.IFluidBlock
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.compatibility.ModCompatibility;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import com.dhanantry.scapeandrunparasites.backport.state.BlockFaceShape;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFluid
extends BlockFluidClassic {
    private final boolean pushesEntity;
    private static final Material DEADBLOOD_MATERIAL = new MaterialLiquid(MapColor.netherrackColor);

    public BlockFluid(String name, Fluid fluid, Material material, boolean pushEntities) {
        super(fluid, DEADBLOOD_MATERIAL);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.pushesEntity = pushEntities;
        SRPBlocks.SRP_BLOCKS.add((Block)this);
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public boolean getPushesEntity() {
        return this.pushesEntity;
    }

    public Vec3d modifyAcceleration(World world, BlockPos pos, Entity entity, Vec3d vec) {
        return this.getPushesEntity() ? super.modifyAcceleration(world, pos, entity, vec) : vec;
    }

    public boolean causesDownwardCurrent(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = BlockStates.get(worldIn, pos);
        Block block = iblockstate.getBlock();
        Material material = iblockstate.getMaterial();
        if (material == this.blockMaterial) {
            return false;
        }
        if (side == EnumFacing.UP) {
            return true;
        }
        if (material == Material.ice) {
            return false;
        }
        boolean flag = BlockFluid.isExceptBlockForAttachWithPiston((Block)block) || block instanceof BlockStairs;
        return !flag && iblockstate.getBlockFaceShape(worldIn, pos, side) == BlockFaceShape.SOLID;
    }

    @SideOnly(value=Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        super.randomDisplayTick(state, world, pos, rand);
        if (this.isCalmDeadBloodSurface(world, pos) && rand.nextInt(90) == 0) {
            double x = (double)pos.getX() + 0.25 + rand.nextDouble() * 0.5;
            double y = (double)pos.getY() + 1.02;
            double z = (double)pos.getZ() + 0.25 + rand.nextDouble() * 0.5;
            double dx = (rand.nextDouble() - 0.5) * 0.01;
            double dy = 0.02 + rand.nextDouble() * 0.01;
            double dz = (rand.nextDouble() - 0.5) * 0.01;
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.CLOUD.getParticleID(), x, y, z, dx, dy, dz, new int[0]);
            if (rand.nextInt(3) == 0) {
                float r = 0.12f;
                float g = 0.28f;
                float b = 0.1f;
                Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID(), x, y + 0.02, z, (double)r, (double)g, (double)b, new int[0]);
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    private boolean isCalmDeadBloodSurface(World world, BlockPos pos) {
        if (!this.isSourceBlock((IBlockAccess)world, pos)) {
            return false;
        }
        int same = 0;
        for (EnumFacing f : EnumFacing.HORIZONTALS) {
            if (this.getQuantaValue((IBlockAccess)world, pos.offset(f)) <= 0 || ++same < 3) continue;
            return true;
        }
        return false;
    }

    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
        Boolean collides = super.isEntityInsideMaterial(world, blockpos, iblockstate, entity, yToTest, materialIn == Material.water ? iblockstate.getMaterial() : materialIn, testingHead);
        return collides == null ? materialIn == Material.water : collides;
    }

    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollision(world, pos, state, entity);
        if (entity instanceof EntityParasiteBase) {
            ((EntityLivingBase)entity).heal(1.0f);
            return;
        }
        entity.motionX *= 0.85;
        entity.motionZ *= 0.85;
        if (entity.motionY < 0.0) {
            entity.motionY *= 0.92;
        }
        entity.fallDistance = 0.0f;
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            PotionEffect curVira;
            this.attackEntityAsMobMinimum((EntityLivingBase)entity, 0.1f);
            if (!ModCompatibility.FLUIDLOGGED_API && !this.isHeadInDeadBlood(world, entity)) {
                return;
            }
            EntityLivingBase mob = (EntityLivingBase)entity;
            int CORRO_DUR = 100;
            int VIRA_DUR = 200;
            Potion CORRO = SRPPotions.CORRO_E;
            Potion VIRA = SRPPotions.VIRA_E;
            PotionEffect curCorro = mob.getActivePotionEffect(CORRO);
            if (curCorro == null || curCorro.getDuration() < 20) {
                mob.addPotionEffect(new PotionEffect(CORRO, 100, 0, false, false));
            }
            if ((curVira = mob.getActivePotionEffect(VIRA)) == null || curVira.getDuration() < 20) {
                mob.addPotionEffect(new PotionEffect(VIRA, 200, 1, false, false));
            }
        }
    }

    private boolean isHeadInDeadBlood(World world, Entity entity) {
        if (entity == null) {
            return false;
        }
        double eyeY = entity.posY + (double)entity.getEyeHeight();
        BlockPos headPos = new BlockPos(entity.posX, eyeY, entity.posZ);
        IBlockState state = BlockStates.get(world, headPos);
        Block b = state.getBlock();
        if (b instanceof IFluidBlock) {
            return ((IFluidBlock)b).getFluid() == SRPFluids.DEADBLOOD_FLUID;
        }
        return false;
    }

    public boolean attackEntityAsMobMinimum(EntityLivingBase target, float MinimumDamage) {
        if (MinimumDamage <= 0.0f) {
            return false;
        }
        float f1 = target.getHealth();
        if (f1 <= 0.0f) {
            return false;
        }
        if (target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode) {
            return false;
        }
        DamageSource s = DamageSource.wither;
        float damage = 0.0f;
        if (target.isPotionActive(SRPPotions.VIRA_E)) {
            damage = MinimumDamage * (float)(target.getActivePotionEffect(SRPPotions.VIRA_E).getAmplifier() + 1);
        }
        damage += MinimumDamage;
        try {
            target.getCombatTracker().trackDamage(s, f1, damage);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (target.getAbsorptionAmount() > 0.0f) {
            target.setHealth(f1 - damage / 2.0f);
            target.setAbsorptionAmount(target.getAbsorptionAmount() - damage / 2.0f);
        } else {
            target.setHealth(f1 - damage);
        }
        target.worldObj.setEntityState((Entity)target, (byte)2);
        if (target.getHealth() <= 0.0f) {
            ItemStack itemstack = null;
            for (EnumHand enumhand : EnumHand.values()) {
                ItemStack itemstack1 = target.getHeldItem(enumhand);
                if (itemstack1.getItem() != Items.TOTEM_OF_UNDYING) continue;
                itemstack = itemstack1.copy();
                itemstack1.shrink(1);
                break;
            }
            if (itemstack != null) {
                if (target instanceof EntityPlayerMP) {
                    EntityPlayerMP entityplayermp = (EntityPlayerMP)target;
                    entityplayermp.triggerAchievement(StatList.getObjectUseStats((Item)Items.TOTEM_OF_UNDYING));
                    CriteriaTriggers.USED_TOTEM.trigger(entityplayermp, itemstack);
                }
                target.setHealth(1.0f);
                target.clearActivePotions();
                target.addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.regeneration, 900, 1));
                target.addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.field_76444_x, 100, 1));
                target.worldObj.setEntityState((Entity)target, (byte)35);
            } else {
                target.onDeath(s);
            }
        }
        return true;
    }
}

