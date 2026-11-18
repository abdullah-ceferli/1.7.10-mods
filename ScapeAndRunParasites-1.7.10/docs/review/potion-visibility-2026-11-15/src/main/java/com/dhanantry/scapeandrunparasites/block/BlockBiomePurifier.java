/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Biomes
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.fml.common.network.NetworkRegistry$TargetPoint
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedRubble;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedStain;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.MsgSpawnPureParticles;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.handlers.BiomeUpdateQueue;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class BlockBiomePurifier
extends BlockBase {
    public BlockBiomePurifier(String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
        super(Material.sponge, name, hardness, creative, tickRandom, resistance);
        this.setStepSound(SRPSoundTypes.PURIFIER);
        this.setDefaultState(this.blockState.getBaseState());
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean flag = super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        if (worldIn.isRemote) {
            return flag;
        }
        ItemStack head = new ItemStack(playerIn.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem());
        if (head.getItem() != Items.AIR) {
            return flag;
        }
        worldIn.playSound(null, pos, SRPSounds.PURIFIER_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        BlockBiomePurifier.killBiome(worldIn, pos, 16);
        BlockStates.set(worldIn, pos, BlockStates.defaultState(Blocks.air));
        double ax = (double)pos.getX() + 0.5;
        double ay = (double)pos.getY() + 0.1;
        double az = (double)pos.getZ() + 0.5;
        SRPNetwork.CHANNEL.sendToAllAround((IMessage)new MsgSpawnPureParticles(ax, ay, az, 24, 0), new NetworkRegistry.TargetPoint(worldIn.provider.dimensionId, ax, ay, az, 64.0));
        double bx = (double)pos.getX() + 0.5;
        double by = (double)pos.getY() + 0.9;
        double bz = (double)pos.getZ() + 0.5;
        SRPNetwork.CHANNEL.sendToAllAround((IMessage)new MsgSpawnPureParticles(bx, by, bz, 24, 1), new NetworkRegistry.TargetPoint(worldIn.provider.dimensionId, bx, by, bz, 64.0));
        double cx = (double)pos.getX() + 0.5;
        double cy = (double)pos.getY() + 0.5;
        double cz = (double)pos.getZ() + 0.5;
        ParticleSpawner.spawnParticle(SRPEnumParticle.FLASH, cx, cy, cz, 0.0, 0.0, 0.0, 0, 20, 20);
        return flag;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        this.onBlockHarvested(world, pos, state, player);
        return BlockStates.set(world, pos, BlockStates.defaultState(Blocks.air), world.isRemote ? 11 : 3);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }
        if (!WorldAccess.isAreaLoaded(worldIn, pos, 3)) {
            return;
        }
        ParasiteEventWorld.setDisloWorldPhase(worldIn, SRPAttributes.EVENTPARAPURIFIER, SRPConfigSystems.chanceEventParaPurifier, 0, null);
        int raan = 32;
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), (double)raan);
        List moblist = EntityQueries.entities(worldIn, EntityParasiteBase.class, axisalignedbb);
        boolean oneTime = true;
        if (!moblist.isEmpty()) {
            for (EntityParasiteBase mob : moblist) {
                if (oneTime && mob instanceof EntityPStationaryArchitect) {
                    mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(MobEffects.GLOWING, 200, 0, false, false));
                    oneTime = false;
                }
                mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.RAGE_E, 600, 1, false, false));
            }
        }
        int i1 = pos.getY();
        double l1 = pos.getX();
        double i2 = pos.getZ();
        int BGrange = raan = 5;
        int BGheight = raan;
        for (int k2 = -1 * BGrange; k2 <= BGrange; ++k2) {
            for (int l2 = -1 * BGrange; l2 <= BGrange; ++l2) {
                for (int j = -1 * BGheight; j <= BGheight; ++j) {
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = new BlockPos(i3, k, l);
                    IBlockState iblockstate = BlockStates.get(worldIn, blockpos);
                    Block block = iblockstate.getBlock();
                    if (block == SRPBlocks.InfestedStain) {
                        BlockStates.set(worldIn, blockpos, BlockStates.defaultState(SRPBlocks.InfestedStain).withProperty((IProperty)BlockInfestedStain.STAGE, (Comparable)Integer.valueOf(5)));
                        worldIn.updateBlockTick(blockpos, BlockStates.defaultState(SRPBlocks.InfestedStain).withProperty((IProperty)BlockInfestedStain.STAGE, (Comparable)Integer.valueOf(5)).getBlock(), 40, 5);
                    }
                    if (block != SRPBlocks.InfestedRubble) continue;
                    BlockStates.set(worldIn, blockpos, BlockStates.defaultState(SRPBlocks.InfestedRubble).withProperty((IProperty)BlockInfestedRubble.STAGE, (Comparable)Integer.valueOf(5)));
                    worldIn.updateBlockTick(blockpos, BlockStates.defaultState(SRPBlocks.InfestedRubble).withProperty((IProperty)BlockInfestedRubble.STAGE, (Comparable)Integer.valueOf(5)).getBlock(), 40, 5);
                }
            }
        }
    }

    public static void killBiome(World worldIn, BlockPos pos, int range) {
        SRPWorldData data = SRPWorldData.get(worldIn);
        int worldTime = 0;
        for (int x = pos.getX() - range; x <= pos.getX() + range; ++x) {
            for (int z = pos.getZ() - range; z <= pos.getZ() + range; ++z) {
                int age = data.nearestHeartAge(pos, true, worldTime);
                int distance = data.getDistanceSpreadByAge(age, false);
                BlockPos convert = new BlockPos(x, pos.getY(), z);
                if (!(worldIn.getBiome(convert) instanceof BiomeParasiteBase)) continue;
                Biome originalBiome = worldIn.getWorldChunkManager().getBiome(pos, Biomes.plains);
                BlockBiomePurifier.positionToBiome(worldIn, convert, Biome.getIdForBiome((Biome)originalBiome));
                BiomeUpdateQueue.enqueue(convert.getX(), convert.getY(), convert.getZ(), false, Biome.getIdForBiome((Biome)originalBiome), worldIn.provider.dimensionId);
            }
        }
    }

    public static void positionToBiome(World worldIn, BlockPos pos, int type) {
        int inChunkX = pos.getX() & 0xF;
        int inChunkZ = pos.getZ() & 0xF;
        WorldAccess.getChunk(worldIn, (BlockPos)pos).getBiomeArray()[inChunkZ << 4 | inChunkX] = (byte)type;
        WorldAccess.getChunk(worldIn, pos).setChunkModified();
        worldIn.markBlocksDirtyVertical(pos.getX(), pos.getZ(), 0, pos.getY());
    }
}

