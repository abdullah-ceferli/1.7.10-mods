/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.ITileEntityProvider
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityDod;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.Random;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDod
extends BlockBase
implements ITileEntityProvider {
    private static final AxisAlignedBB DOD_AABB = Boxes.create(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);
    private static final DamageSource DOD_DAMAGE = new DamageSource("srp_dod_block").setDamageBypassesArmor().setDamageIsAbsolute();
    private static final String NBT_DOD_LAST_HIT = "srp_dod_last_hit";

    public BlockDod(String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
        super(Material.sponge, name, hardness, creative, tickRandom, resistance);
        this.setDefaultState(this.blockState.getBaseState());
        this.setStepSound(SRPSoundTypes.FLESH);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }
        if (!WorldAccess.isAreaLoaded(worldIn, pos, 3)) {
            return;
        }
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDod();
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        WorldAccess.removeTileEntity(worldIn, pos);
        super.breakBlock(worldIn, pos, state);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return DOD_AABB;
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return DOD_AABB;
    }

    private void pushAndHit(World worldIn, BlockPos hitPos, EntityPlayer player) {
        if (player.isDead) {
            return;
        }
        float yawRad = (float)Math.toRadians(player.rotationYaw);
        double lookX = -MathHelper.sin((float)yawRad);
        double lookZ = MathHelper.cos((float)yawRad);
        double softStrength = 0.3;
        double softX = -lookX * softStrength;
        double softZ = -lookZ * softStrength;
        player.addVelocity(softX, 0.05, softZ);
        player.velocityChanged = true;
        long now = worldIn.getTotalWorldTime();
        long lastHit = player.getEntityData().getLong(NBT_DOD_LAST_HIT);
        if (now - lastHit < 20L) {
            return;
        }
        player.getEntityData().setLong(NBT_DOD_LAST_HIT, now);
        double hitStrength = 1.2;
        double hitX = -lookX * hitStrength;
        double hitZ = -lookZ * hitStrength;
        player.addVelocity(hitX, 0.25, hitZ);
        player.velocityChanged = true;
        float damage = 4.0f;
        player.attackEntityFrom(DOD_DAMAGE, damage);
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(worldIn, null, hitPos, SRPSounds.ADAPTATION_P, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.BLOCKS, 1.0f, 1.0f);
        player.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.DOD_SMOKE_TRAIL_E, 20, 0, false, false));
    }

    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        super.onEntityCollision(worldIn, pos, state, entityIn);
        if (worldIn.isRemote) {
            return;
        }
        if (!(entityIn instanceof EntityPlayer)) {
            return;
        }
        this.pushAndHit(worldIn, pos, (EntityPlayer)entityIn);
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityWalk(worldIn, pos, entityIn);
        if (worldIn.isRemote) {
            return;
        }
        if (!(entityIn instanceof EntityPlayer)) {
            return;
        }
        this.pushAndHit(worldIn, pos, (EntityPlayer)entityIn);
    }
}

