/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityBeacon
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityIki;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class AIDisableBeaconIki
extends EntityAIBase {
    private static final double SCAN_RADIUS_D = 64.0;
    private static final int PICKUP_RADIUS = 6;
    private static final int RESCAN_TICKS = 100;
    private static final double REACH_SQ = 4.0;
    private static final int STALL_TICKS = 40;
    private static final int PHASE_BUDGET = 200;
    private final EntityIki host;
    private IBlockState carried = null;
    private TileEntityBeacon targetBeacon = null;
    private BlockPos beaconPos = null;
    private BlockPos placePos = null;
    private int timeout = 0;
    private int cooldown = 0;
    private Phase phase = Phase.SEARCH;
    private BlockPos pickupPos = null;
    private int progressTicks = 0;
    private double lastDistSq = Double.MAX_VALUE;

    public AIDisableBeaconIki(EntityIki host) {
        this.host = host;
        this.setMutexBits(1);
    }

    public boolean shouldExecute() {
        if (this.host.getAttackTarget() != null) {
            return false;
        }
        return !this.host.worldObj.isRemote && ForgeEventFactory.getMobGriefingEvent((World)this.host.worldObj, (Entity)this.host) && this.host.isEntityAlive();
    }

    public boolean continueExecuting() {
        if (this.host.getAttackTarget() != null) {
            return false;
        }
        return this.shouldExecute();
    }

    public void resetTask() {
        this.targetBeacon = null;
        this.beaconPos = null;
        this.placePos = null;
        this.pickupPos = null;
        this.carried = null;
        this.timeout = 0;
        this.progressTicks = 0;
        this.lastDistSq = Double.MAX_VALUE;
        this.phase = Phase.SEARCH;
    }

    public void updateTask() {
        if (this.host.worldObj.isRemote) {
            return;
        }
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        switch (this.phase) {
            case SEARCH: {
                if (this.cooldown > 0) break;
                this.targetBeacon = this.findNearestEmittingBeacon();
                if (this.targetBeacon != null) {
                    this.beaconPos = this.targetBeacon.getPos();
                    this.placePos = null;
                    this.pickupPos = null;
                    this.phase = this.carried == null ? Phase.PICK_TARGET_BLOCK : Phase.MOVE_TO_BEAM;
                    this.timeout = 200;
                    break;
                }
                this.cooldown = 100;
                break;
            }
            case PICK_TARGET_BLOCK: {
                this.pickupPos = this.findNearbyPickupBlock();
                this.progressTicks = 0;
                this.lastDistSq = Double.MAX_VALUE;
                if (this.pickupPos != null) {
                    this.phase = Phase.MOVE_TO_BLOCK;
                    this.timeout = 200;
                    break;
                }
                this.cooldown = 100;
                this.phase = Phase.COOLDOWN;
                break;
            }
            case MOVE_TO_BLOCK: {
                if (this.pickupPos == null) {
                    this.phase = Phase.PICK_TARGET_BLOCK;
                    break;
                }
                if (!this.isPickupStillValid(this.pickupPos)) {
                    this.phase = Phase.PICK_TARGET_BLOCK;
                    break;
                }
                this.flyTo(this.pickupPos, 1.1);
                double d2 = this.host.getDistanceSq((double)this.pickupPos.getX() + 0.5, (double)this.pickupPos.getY() + 0.5, (double)this.pickupPos.getZ() + 0.5);
                if (d2 + 0.01 < this.lastDistSq) {
                    this.lastDistSq = d2;
                    this.progressTicks = 0;
                } else {
                    ++this.progressTicks;
                }
                if (d2 <= 4.0) {
                    this.phase = Phase.PICKUP;
                    break;
                }
                if (--this.timeout > 0 && this.progressTicks < 40) break;
                this.phase = Phase.PICK_TARGET_BLOCK;
                break;
            }
            case PICKUP: {
                if (this.pickupPos != null && this.isPickupStillValid(this.pickupPos)) {
                    this.carried = BlockStates.get(this.host.worldObj, this.pickupPos);
                    WorldAccess.setBlockToAir(this.host.worldObj, this.pickupPos);
                }
                this.pickupPos = null;
                this.progressTicks = 0;
                this.lastDistSq = Double.MAX_VALUE;
                this.phase = Phase.MOVE_TO_BEAM;
                this.timeout = 200;
                break;
            }
            case MOVE_TO_BEAM: {
                BlockPos alt;
                if (!this.isBeaconEmitting(this.beaconPos)) {
                    this.phase = Phase.SEARCH;
                    break;
                }
                if (this.placePos == null) {
                    this.placePos = this.pickBeamPlacement(this.beaconPos);
                    if (this.placePos == null) {
                        this.cooldown = 100;
                        this.phase = Phase.COOLDOWN;
                        break;
                    }
                    this.progressTicks = 0;
                    this.lastDistSq = Double.MAX_VALUE;
                }
                this.flyTo(this.placePos, 1.1);
                double d2 = this.host.getDistanceSq((double)this.placePos.getX() + 0.5, (double)this.placePos.getY() + 0.5, (double)this.placePos.getZ() + 0.5);
                if (d2 + 0.01 < this.lastDistSq) {
                    this.lastDistSq = d2;
                    this.progressTicks = 0;
                } else {
                    ++this.progressTicks;
                }
                if (this.progressTicks == 20 && (alt = this.pickBeamPlacement(this.beaconPos)) != null && !alt.equals((Object)this.placePos)) {
                    this.placePos = alt;
                    this.progressTicks = 0;
                    this.lastDistSq = Double.MAX_VALUE;
                }
                if (d2 <= 4.0) {
                    this.phase = Phase.PLACE;
                    break;
                }
                if (--this.timeout > 0 && this.progressTicks < 40) break;
                this.cooldown = 100;
                this.phase = Phase.COOLDOWN;
                break;
            }
            case PLACE: {
                if (this.carried != null && this.placePos != null && this.canPlaceAt(this.placePos)) {
                    BlockStates.set(this.host.worldObj, this.placePos, this.carried, 3);
                }
                this.carried = null;
                this.cooldown = 100;
                this.phase = Phase.COOLDOWN;
                break;
            }
            case COOLDOWN: {
                if (this.cooldown > 0) break;
                this.phase = Phase.SEARCH;
            }
        }
    }

    private boolean canPlaceAt(BlockPos pos) {
        if (!WorldAccess.isBlockLoaded(this.host.worldObj, pos)) {
            return false;
        }
        IBlockState st = BlockStates.get(this.host.worldObj, pos);
        return st.getMaterial().isReplaceable() || st.getBlock() == Blocks.air;
    }

    private boolean isBeaconStillActive() {
        if (this.targetBeacon == null) {
            return false;
        }
        TileEntity te = WorldAccess.getTileEntity(this.host.worldObj, this.targetBeacon.getPos());
        if (!(te instanceof TileEntityBeacon)) {
            return false;
        }
        return ((TileEntityBeacon)te).getLevels() > 0;
    }

    private void flyTo(BlockPos pos, double speed) {
        this.host.getMoveHelper().setMoveTo((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, speed);
        this.host.getLookHelper().setLookPosition((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 30.0f, 30.0f);
    }

    private TileEntityBeacon findNearestEmittingBeacon() {
        double bestD2 = 4096.0;
        TileEntityBeacon best = null;
        for (TileEntity te : this.host.worldObj.loadedTileEntityList) {
            BlockPos p;
            double d2;
            if (!(te instanceof TileEntityBeacon) || (d2 = this.host.getDistanceSq((double)(p = te.getPos()).getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5)) > bestD2 || !this.isBeaconEmitting(p)) continue;
            best = (TileEntityBeacon)te;
            bestD2 = d2;
        }
        return best;
    }

    private boolean isPickupStillValid(BlockPos p) {
        if (!WorldAccess.isBlockLoaded(this.host.worldObj, p)) {
            return false;
        }
        if (!WorldAccess.isAirBlock(this.host.worldObj, p.up())) {
            return false;
        }
        IBlockState st = BlockStates.get(this.host.worldObj, p);
        if (WorldAccess.getTileEntity(this.host.worldObj, p) != null) {
            return false;
        }
        return this.isPickupWhitelisted(st.getBlock());
    }

    private boolean isBeaconEmitting(BlockPos beacon) {
        if (beacon == null) {
            return false;
        }
        TileEntity te = WorldAccess.getTileEntity(this.host.worldObj, beacon);
        if (!(te instanceof TileEntityBeacon)) {
            return false;
        }
        if (((TileEntityBeacon)te).getLevels() <= 0) {
            return false;
        }
        int top = this.host.worldObj.getHeight();
        for (int y = beacon.getY() + 1; y < top; ++y) {
            BlockPos q = new BlockPos(beacon.getX(), y, beacon.getZ());
            Block b = BlockStates.get(this.host.worldObj, q).getBlock();
            if (b == Blocks.air || b == Blocks.glass || b == Blocks.stained_glass || b == Blocks.stained_glass_pane) continue;
            return false;
        }
        return true;
    }

    private BlockPos pickBeamPlacement(BlockPos beacon) {
        BlockPos p = this.pickBeamPlacementStrict(beacon);
        if (p == null) {
            p = this.pickBeamPlacementLoose(beacon);
        }
        return p;
    }

    private BlockPos pickBeamPlacementStrict(BlockPos beacon) {
        BlockPos c;
        int y;
        int bx = beacon.getX();
        int by = beacon.getY();
        int bz = beacon.getZ();
        int top = this.host.worldObj.getHeight();
        for (y = Math.min(top - 1, by + 64); y >= by + 1; --y) {
            c = new BlockPos(bx, y, bz);
            if (!this.canPlaceAt(c) || !this.hasAttachSupport(c) || !this.hasLineOfSightTo(c)) continue;
            return c;
        }
        for (y = by + 1; y < Math.min(top, by + 64); ++y) {
            c = new BlockPos(bx, y, bz);
            if (!this.canPlaceAt(c) || !this.hasAttachSupport(c) || !this.hasLineOfSightTo(c)) continue;
            return c;
        }
        return null;
    }

    private boolean hasAttachSupport(BlockPos pos) {
        for (EnumFacing f : EnumFacing.values()) {
            IBlockState ns;
            BlockPos n = pos.offset(f);
            if (!WorldAccess.isBlockLoaded(this.host.worldObj, n) || (ns = BlockStates.get(this.host.worldObj, n)).getBlock() == Blocks.air || !ns.isSideSolid((IBlockAccess)this.host.worldObj, n, f.getOpposite())) continue;
            return true;
        }
        return false;
    }

    private BlockPos pickBeamPlacementLoose(BlockPos beacon) {
        BlockPos c;
        int y;
        int bx = beacon.getX();
        int by = beacon.getY();
        int bz = beacon.getZ();
        int top = this.host.worldObj.getHeight();
        for (y = Math.min(top - 1, by + 64); y >= by + 1; --y) {
            c = new BlockPos(bx, y, bz);
            if (!this.canPlaceAt(c) || !this.hasAttachSupport(c)) continue;
            return c;
        }
        for (y = by + 1; y < Math.min(top, by + 64); ++y) {
            c = new BlockPos(bx, y, bz);
            if (!this.canPlaceAt(c) || !this.hasAttachSupport(c)) continue;
            return c;
        }
        return null;
    }

    private boolean hasLineOfSightTo(BlockPos pos) {
        Vec3d end;
        double ez;
        double ex = this.host.posX;
        double ey = this.host.posY + (double)this.host.getEyeHeight();
        Vec3d start = new Vec3d(ex, ey, ez = this.host.posZ);
        RayTraceResult hit = this.host.worldObj.rayTraceBlocks(start, end = new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5), false, true, false);
        return hit == null || hit.typeOfHit == RayTraceResult.Type.MISS;
    }

    private BlockPos findNearbyPickupBlock() {
        BlockPos origin = new BlockPos((Entity)this.host);
        int r = 6;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -r; dx <= r; ++dx) {
                for (int dz = -r; dz <= r; ++dz) {
                    IBlockState st;
                    Block b;
                    BlockPos p = origin.add(dx, dy, dz);
                    if (!WorldAccess.isBlockLoaded(this.host.worldObj, p) || !this.isPickupWhitelisted(b = (st = BlockStates.get(this.host.worldObj, p)).getBlock()) || WorldAccess.getTileEntity(this.host.worldObj, p) != null || !WorldAccess.isAirBlock(this.host.worldObj, p.up()) || b == Blocks.beacon) continue;
                    return p;
                }
            }
        }
        return null;
    }

    private boolean isPickupWhitelisted(Block b) {
        return b == Blocks.dirt || b == Blocks.grass || b == Blocks.sand || b == Blocks.gravel || b == Blocks.cobblestone || b == Blocks.netherrack;
    }

    private TileEntityBeacon findNearestActiveBeacon() {
        double bestD2 = 4096.0;
        TileEntityBeacon best = null;
        for (TileEntity te : this.host.worldObj.loadedTileEntityList) {
            TileEntityBeacon b;
            BlockPos p;
            double d2;
            if (!(te instanceof TileEntityBeacon) || (d2 = this.host.getDistanceSq((double)(p = te.getPos()).getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5)) > bestD2 || (b = (TileEntityBeacon)te).getLevels() <= 0) continue;
            best = b;
            bestD2 = d2;
        }
        return best;
    }

    private static enum Phase {
        SEARCH,
        PICK_TARGET_BLOCK,
        MOVE_TO_BLOCK,
        PICKUP,
        MOVE_TO_BEAM,
        PLACE,
        COOLDOWN;

    }
}

