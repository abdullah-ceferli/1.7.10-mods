/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockCactus
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Plane
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.PotionEffect;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class BlockParasiteCactus
extends BlockCactus {
    private static final String NBT_PCACTUS_LAST_PUSH = "srp_pcactus_last_push";
    private static final double PUSH_H = 0.35;
    private static final double PUSH_Y = 0.08;
    private static final long PUSH_CD = 8L;

    public BlockParasiteCactus(String name, float hardness, boolean creative, float resistance) {
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setHardness(hardness);
        this.setResistance(resistance);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.CLOTH);
        this.setTickRandomly(true);
        this.setHarvestLevel("axe", 0);
        if (creative) {
            this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        }
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        ItemBlock itemBlock = this instanceof IMetaName ? ((IMetaName)((Object)this)).getItemBlock() : new ItemBlock((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(itemBlock, Objects.requireNonNull(RegistryNames.get(this))));
    }

    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote) {
            int goo;
            ParasiteEventWorld.setDisloWorldPhase(world, SRPAttributes.EVENTPARABLOCKBR, SRPConfigSystems.chanceEventParaBlockB, 0, null);
            SRPSaveData dataLol = SRPSaveData.get(world, 1);
            int n = goo = SRPConfigSystems.disloParasiteBlock ? dataLol.getCurrentCode(world.provider.dimensionId, 25) : 0;
            if (goo != 0 && world.rand.nextDouble() < SRPConfigSystems.disloParasiteBlockChance) {
                List serverList = EntityQueries.loaded(world);
                int count = 0;
                for (int x = 0; x < serverList.size(); ++x) {
                    if (!(serverList.get(x) instanceof EntityParasiteBase)) continue;
                    ++count;
                }
                if (count > SRPConfig.worldMobCap) {
                    return super.removedByPlayer(state, world, pos, player, willHarvest);
                }
                EntityParasiteBase halo = ParasiteEventEntity.getRandomFeral(world);
                if (goo >= SRPConfigSystems.disloParasiteBlockValue1) {
                    halo = ParasiteEventEntity.getRandomPrimitive(world);
                }
                if (goo >= SRPConfigSystems.disloParasiteBlockValue2) {
                    halo = ParasiteEventEntity.getRandomAdapted(world);
                }
                if (goo >= SRPConfigSystems.disloParasiteBlockValue3) {
                    halo = ParasiteEventEntity.getRandomPure(world);
                }
                halo.setPosition((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
                halo.onInitialSpawn(DifficultyAccess.at(world, EntityQueries.position(halo)), null);
                world.spawnEntityInWorld((Entity)halo);
                world.playEvent(null, 1026, new BlockPos((Entity)halo), 0);
                halo.particleStatus((byte)7);
                halo.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.EPEL_E, 600, 0, false, false));
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    public boolean canEntitySpawn(IBlockState state, Entity entityIn) {
        if (!(entityIn instanceof EntityParasiteBase)) {
            return false;
        }
        return super.canEntitySpawn(state, entityIn);
    }

    public boolean canBlockStay(World worldIn, BlockPos pos) {
        IBlockState downState = BlockStates.get(worldIn, pos.down());
        if (downState.getBlock() == Blocks.sand || downState.getBlock() == Blocks.RED_SANDSTONE || downState.getBlock() == SRPBlocks.InfestedSand || downState.getBlock() == this) {
            for (EnumFacing f : EnumFacing.Plane.HORIZONTAL) {
                IBlockState side = BlockStates.get(worldIn, pos.offset(f));
                if (!side.getMaterial().isSolid() || side.getBlock() == this) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        long last;
        super.onEntityCollision(worldIn, pos, state, entityIn);
        if (worldIn.isRemote) {
            return;
        }
        if (!(entityIn instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)entityIn;
        if (player.isDead) {
            return;
        }
        long now = worldIn.getTotalWorldTime();
        if (now - (last = player.getEntityData().getLong(NBT_PCACTUS_LAST_PUSH)) < 8L) {
            return;
        }
        player.getEntityData().setLong(NBT_PCACTUS_LAST_PUSH, now);
        double cx = (double)pos.getX() + 0.5;
        double cz = (double)pos.getZ() + 0.5;
        double dx = player.posX - cx;
        double dz = player.posZ - cz;
        double mag = Math.sqrt(dx * dx + dz * dz);
        if (mag < 1.0E-4) {
            dx = 0.0;
            dz = 1.0;
            mag = 1.0;
        }
        player.addVelocity((dx /= mag) * 0.35, 0.08, (dz /= mag) * 0.35);
        player.velocityChanged = true;
    }
}

