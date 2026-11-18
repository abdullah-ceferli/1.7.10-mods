/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.util.LangHelper;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNodeLamp
extends BlockBase {
    public static final PropertyBool POWERED = PropertyBool.create((String)"powered");
    public static final PropertyInteger RANGE_LEVEL = PropertyInteger.create((String)"range_level", (int)0, (int)5);

    public BlockNodeLamp() {
        super(Material.redstoneLight, "node_redstone_lamp", 0.3f, true, true);
        this.setStepSound(com.dhanantry.scapeandrunparasites.backport.sound.SoundType.GLASS);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)POWERED, (Comparable)Boolean.valueOf(false)).withProperty((IProperty)RANGE_LEVEL, (Comparable)Integer.valueOf(0)));
        this.setTickRandomly(true);
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{POWERED, RANGE_LEVEL});
    }

    public IBlockState getStateFromMeta(int meta) {
        boolean powered = (meta & 8) != 0;
        int level = meta & 7;
        return BlockStates.defaultState(this).withProperty((IProperty)POWERED, (Comparable)Boolean.valueOf(powered)).withProperty((IProperty)RANGE_LEVEL, (Comparable)Integer.valueOf(level));
    }

    public int getMetaFromState(IBlockState state) {
        int meta = (Integer)state.getValue((IProperty)RANGE_LEVEL);
        if (((Boolean)state.getValue((IProperty)POWERED)).booleanValue()) {
            meta |= 8;
        }
        return meta;
    }

    public boolean canProvidePower(IBlockState state) {
        return false;
    }

    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return 0;
    }

    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return 0;
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        return (Integer)state.getValue((IProperty)RANGE_LEVEL) * 3;
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            this.updateLampState(world, pos, state);
            WorldAccess.scheduleUpdate(world, pos, (Block)this, 100);
        }
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (!world.isRemote) {
            this.updateLampState(world, pos, state);
            WorldAccess.scheduleUpdate(world, pos, (Block)this, 20);
        }
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!world.isRemote) {
            this.updateLampState(world, pos, state);
        }
    }

    private boolean updateLampState(World world, BlockPos pos, IBlockState state) {
        boolean changed;
        int newLevel = 0;
        List entities = EntityQueries.entities(world, Entity.class, Boxes.grow(Boxes.create(pos), 250.0));
        double nearestDistSq = Double.MAX_VALUE;
        for (Entity e : entities) {
            double distSq;
            if (e == null || !e.isEntityAlive() || !(e instanceof EntityDod) && !(e instanceof EntityDodSII) && !(e instanceof EntityDodSIII) && !(e instanceof EntityDodSIV) || !((distSq = e.getDistanceSq(pos)) < nearestDistSq)) continue;
            nearestDistSq = distSq;
        }
        if (nearestDistSq <= 2500.0) {
            newLevel = 5;
        } else if (nearestDistSq <= 5625.0) {
            newLevel = 4;
        } else if (nearestDistSq <= 10000.0) {
            newLevel = 3;
        } else if (nearestDistSq <= 22500.0) {
            newLevel = 2;
        } else if (nearestDistSq <= 40000.0) {
            newLevel = 1;
        } else if (nearestDistSq <= 62500.0) {
            newLevel = 0;
        }
        boolean powered = newLevel > 0;
        boolean bl = changed = powered != (Boolean)state.getValue((IProperty)POWERED) || newLevel != (Integer)state.getValue((IProperty)RANGE_LEVEL);
        if (changed) {
            BlockStates.set(world, pos, state.withProperty((IProperty)POWERED, (Comparable)Boolean.valueOf(powered)).withProperty((IProperty)RANGE_LEVEL, (Comparable)Integer.valueOf(newLevel)), 3);
            world.checkLight(pos);
            world.updateComparatorOutputLevel(pos, (Block)this);
        }
        return changed;
    }

    private static String distanceColored(int displayTier) {
        switch (displayTier) {
            case 1: {
                return "\u00a7c1";
            }
            case 2: {
                return "\u00a762";
            }
            case 3: {
                return "\u00a7e3";
            }
            case 4: {
                return "\u00a7a4";
            }
            case 5: {
                return "\u00a725";
            }
        }
        return "\u00a770";
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        boolean powered = (Boolean)state.getValue((IProperty)POWERED);
        int maxStrength = 0;
        double nearestDistSq = Double.MAX_VALUE;
        List entities = EntityQueries.entities(world, Entity.class, Boxes.grow(Boxes.create(pos), 250.0));
        for (Entity e : entities) {
            double d;
            if (e == null || !e.isEntityAlive()) continue;
            int s = 0;
            if (e instanceof EntityDod) {
                s = 1;
            } else if (e instanceof EntityDodSII) {
                s = 2;
            } else if (e instanceof EntityDodSIII) {
                s = 3;
            } else if (e instanceof EntityDodSIV) {
                s = 4;
            }
            if (s <= 0) continue;
            if (s > maxStrength) {
                maxStrength = s;
            }
            if (!((d = e.getDistanceSq(pos)) < nearestDistSq)) continue;
            nearestDistSq = d;
        }
        int distanceTierRaw = 0;
        if (nearestDistSq != Double.MAX_VALUE) {
            if (nearestDistSq <= 2500.0) {
                distanceTierRaw = 5;
            } else if (nearestDistSq <= 5625.0) {
                distanceTierRaw = 4;
            } else if (nearestDistSq <= 10000.0) {
                distanceTierRaw = 3;
            } else if (nearestDistSq <= 22500.0) {
                distanceTierRaw = 2;
            } else if (nearestDistSq <= 40000.0) {
                distanceTierRaw = 1;
            } else if (nearestDistSq <= 62500.0) {
                distanceTierRaw = 0;
            }
        }
        if (!powered) {
            maxStrength = 0;
            distanceTierRaw = 0;
        }
        int displayDistanceTier = distanceTierRaw == 0 ? 0 : 6 - distanceTierRaw;
        String strengthLabel = maxStrength == 0 ? "0" : LangHelper.toRoman(maxStrength);
        String distanceLabel = BlockNodeLamp.distanceColored(displayDistanceTier);
        player.addChatMessage((IChatComponent)new ChatComponentTranslation("message.srparasites.node_lamp.strength", new Object[]{strengthLabel}));
        player.addChatMessage((IChatComponent)new ChatComponentTranslation("message.srparasites.node_lamp.distance", new Object[]{distanceLabel}));
        return true;
    }
}

