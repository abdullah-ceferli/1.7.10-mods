/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

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
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.PotionEffect;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class BlockBase
extends com.dhanantry.scapeandrunparasites.backport.state.StatefulBlock {
    public BlockBase(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
        super(material);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setHardness(hardness);
        this.setTickRandomly(tickRandom);
        if (creative) {
            this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        }
        SRPBlocks.SRP_BLOCKS.add(this);
        ItemBlock itemBlock = this instanceof IMetaName ? ((IMetaName)((Object)this)).getItemBlock() : new ItemBlock((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(itemBlock, Objects.requireNonNull(RegistryNames.get(this))));
    }

    public BlockBase(Material material, String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
        this(material, name, hardness, creative, tickRandom);
        this.setResistance(resistance);
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
                    count = 0;
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
                halo.addPotionEffect(new PotionEffect(SRPPotions.EPEL_E, 600, 0, false, false));
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
}

