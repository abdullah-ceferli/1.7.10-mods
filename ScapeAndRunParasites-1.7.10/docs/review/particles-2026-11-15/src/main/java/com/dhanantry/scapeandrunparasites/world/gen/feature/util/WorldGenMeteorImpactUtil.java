/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockLog
 *  net.minecraft.block.BlockNewLeaf
 *  net.minecraft.block.BlockNewLog
 *  net.minecraft.block.BlockOldLeaf
 *  net.minecraft.block.BlockOldLog
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.BlockVine
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.world.gen.feature.util;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public final class WorldGenMeteorImpactUtil {
    private static final int MIN_CARVE_Y = 5;
    private static final Map<Integer, List<PendingStructure>> PENDING = new HashMap<Integer, List<PendingStructure>>();
    private static final Map<Integer, Set<Long>> MAIN_METEOR_CENTERS = new HashMap<Integer, Set<Long>>();

    private WorldGenMeteorImpactUtil() {
    }

    public static void tickPendingStructures(World world) {
        int dim = world.provider.dimensionId;
        List<PendingStructure> list = PENDING.get(dim);
        if (list == null || list.isEmpty()) {
            return;
        }
        long now = world.getTotalWorldTime();
        Iterator<PendingStructure> it = list.iterator();
        while (it.hasNext()) {
            PendingStructure p = it.next();
            if (now < p.executeAt) continue;
            Random r = new Random(p.seed);
            WorldGenCustomStructures.generateInPosition(new WorldGenStructure(p.name), r, world, p.origin, p.offX, p.offY, p.offZ);
            it.remove();
        }
        if (list.isEmpty()) {
            PENDING.remove(dim);
        }
    }

    public static void scheduleDelayedStructure(World world, Random rand, String name, BlockPos origin, int offX, int offY, int offZ, int delayTicks) {
        int dim = world.provider.dimensionId;
        List<PendingStructure> list = PENDING.get(dim);
        if (list == null) {
            list = new ArrayList<PendingStructure>();
            PENDING.put(dim, list);
        }
        long at = world.getTotalWorldTime() + (long)Math.max(1, delayTicks);
        list.add(new PendingStructure(name, origin, offX, offY, offZ, at, rand.nextLong()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void markMainMeteor(World world, BlockPos center) {
        int dim = world.provider.dimensionId;
        Map<Integer, Set<Long>> map = MAIN_METEOR_CENTERS;
        synchronized (map) {
            Set<Long> set = MAIN_METEOR_CENTERS.get(dim);
            if (set == null) {
                set = new HashSet<Long>();
                MAIN_METEOR_CENTERS.put(dim, set);
            }
            set.add(center.toLong());
            if (set.size() > 512) {
                set.clear();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isNearMainMeteor(World world, BlockPos pos, int minDist) {
        Set<Long> set;
        int dim = world.provider.dimensionId;
        Map<Integer, Set<Long>> map = MAIN_METEOR_CENTERS;
        synchronized (map) {
            set = MAIN_METEOR_CENTERS.get(dim);
        }
        if (set == null || set.isEmpty()) {
            return false;
        }
        int minDistSq = minDist * minDist;
        for (Long l : set) {
            int dz;
            BlockPos c = BlockPos.fromLong((long)l);
            int dx = c.getX() - pos.getX();
            if (dx * dx + (dz = c.getZ() - pos.getZ()) * dz > minDistSq) continue;
            return true;
        }
        return false;
    }

    public static void carveCraterBowl(World world, Random rand, BlockPos surface, int radius, int depth, float steepness, IBlockState rim, IBlockState stain, IBlockState cooked) {
        int cx = surface.getX();
        int cz = surface.getZ();
        int coreR = Math.max(2, (int)((float)radius * 0.28f));
        int coreRR = coreR * coreR;
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                BlockPos p;
                int dx = x;
                int dz = z;
                int d2 = dx * dx + dz * dz;
                if (d2 > radius * radius) continue;
                BlockPos colTop = WorldAccess.getTopSolidOrLiquidBlock(world, new BlockPos(cx + x, surface.getY(), cz + z));
                int topY = colTop.getY();
                double dist = Math.sqrt(d2);
                double t = dist / (double)radius;
                double bowl = 1.0 - t;
                double curve = bowl * bowl;
                int cut = topY - (int)Math.round((double)depth * curve);
                for (int y = topY; y > cut && y > 5 && WorldAccess.isBlockLoaded(world, p = new BlockPos(cx + x, y, cz + z)); --y) {
                    IBlockState s = BlockStates.get(world, p);
                    Material m = s.getMaterial();
                    if (m == Material.air || p.getY() <= 5) continue;
                    BlockStates.set(world, p, BlockStates.defaultState(Blocks.air), 2);
                }
                BlockPos top = WorldAccess.getTopSolidOrLiquidBlock(world, new BlockPos(cx + x, surface.getY(), cz + z)).down();
                if (!WorldAccess.isBlockLoaded(world, top)) continue;
                if (d2 <= coreRR) {
                    if (rand.nextInt(3) == 0) {
                        BlockStates.set(world, top, stain, 2);
                        continue;
                    }
                    BlockStates.set(world, top, cooked, 2);
                    continue;
                }
                if (dist >= (double)radius - 1.5) {
                    BlockStates.set(world, top, rim, 2);
                    continue;
                }
                if (!(dist >= (double)radius * 0.55) || rand.nextInt(4) != 0) continue;
                BlockStates.set(world, top, stain, 2);
            }
        }
    }

    public static void scorchRings(World world, Random rand, BlockPos surface, int radius, IBlockState stain) {
        int cx = surface.getX();
        int cz = surface.getZ();
        int ring1 = (int)((float)radius * 1.15f);
        int ring2 = (int)((float)radius * 1.45f);
        for (int x = -ring2; x <= ring2; ++x) {
            for (int z = -ring2; z <= ring2; ++z) {
                BlockPos top;
                int dx = x;
                int dz = z;
                int d2 = dx * dx + dz * dz;
                if (d2 < ring1 * ring1 || d2 > ring2 * ring2 || rand.nextInt(3) != 0 || !WorldAccess.isBlockLoaded(world, top = WorldAccess.getTopSolidOrLiquidBlock(world, new BlockPos(cx + x, surface.getY(), cz + z)).down())) continue;
                BlockStates.set(world, top, stain, 2);
            }
        }
    }

    public static void spawnEjecta(World world, Random rand, BlockPos surface, int radius, double dirX, double dirZ, IBlockState rubble, IBlockState stain) {
        int cx = surface.getX();
        int cz = surface.getZ();
        int max = (int)((float)radius * 3.2f);
        int min = (int)((float)radius * 1.1f);
        for (int i = 0; i < radius * 30; ++i) {
            double dist = min + rand.nextInt(Math.max(1, max - min));
            double spread = (rand.nextDouble() - 0.5) * 1.25;
            double px = dirX * dist + -dirZ * dist * spread;
            double pz = dirZ * dist + dirX * dist * spread;
            int x = cx + (int)Math.round(px);
            int z = cz + (int)Math.round(pz);
            BlockPos top = WorldAccess.getTopSolidOrLiquidBlock(world, new BlockPos(x, surface.getY(), z)).down();
            if (!WorldAccess.isBlockLoaded(world, top)) continue;
            if (rand.nextInt(5) == 0) {
                BlockStates.set(world, top, stain, 2);
                continue;
            }
            BlockStates.set(world, top, rubble, 2);
        }
    }

    public static void microCraters(World world, Random rand, BlockPos surface, int radius, double dirX, double dirZ, IBlockState stain) {
        int cx = surface.getX();
        int cz = surface.getZ();
        int count = Math.max(3, radius / 3);
        for (int i = 0; i < count; ++i) {
            double dist = (double)radius * (1.2 + rand.nextDouble() * 2.0);
            double spread = (rand.nextDouble() - 0.5) * 1.6;
            double px = dirX * dist + -dirZ * dist * spread;
            double pz = dirZ * dist + dirX * dist * spread;
            int x0 = cx + (int)Math.round(px);
            int z0 = cz + (int)Math.round(pz);
            int r = 2 + rand.nextInt(3);
            int rr = r * r;
            for (int x = -r; x <= r; ++x) {
                for (int z = -r; z <= r; ++z) {
                    BlockPos top;
                    int d2 = x * x + z * z;
                    if (d2 > rr || !WorldAccess.isBlockLoaded(world, top = WorldAccess.getTopSolidOrLiquidBlock(world, new BlockPos(x0 + x, surface.getY(), z0 + z)).down()) || rand.nextInt(3) != 0) continue;
                    BlockStates.set(world, top, stain, 2);
                }
            }
        }
    }

    public static TunnelResult carveAngledTunnel(World world, BlockPos center, int radius, int length, double dirX, double dirY, double dirZ) {
        double mag = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (mag < 1.0E-6) {
            return new TunnelResult(false, 0, 0, 0, 0);
        }
        dirX /= mag;
        dirY /= mag;
        dirZ /= mag;
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        boolean any = false;
        int firstY = 0;
        int lastY = 0;
        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;
        for (int t = 0; t <= length; ++t) {
            int px = cx + (int)Math.round(dirX * (double)t);
            int py = cy + (int)Math.round(dirY * (double)t);
            int pz = cz + (int)Math.round(dirZ * (double)t);
            TunnelResultSlice slice = WorldGenMeteorImpactUtil.carveSphereCount(world, new BlockPos(px, py, pz), radius);
            if (!slice.brokeAny) continue;
            if (!any) {
                any = true;
                firstY = slice.firstBrokenY;
            }
            lastY = slice.lastBrokenY;
            if (slice.lowestY < low) {
                low = slice.lowestY;
            }
            if (slice.highestY <= high) continue;
            high = slice.highestY;
        }
        if (!any) {
            return new TunnelResult(false, 0, 0, 0, 0);
        }
        return new TunnelResult(true, firstY, lastY, low, high);
    }

    public static void clearVegetationInArea(World world, BlockPos center, int radius, int yMin, int yMax) {
        int cx = center.getX();
        int cz = center.getZ();
        int rr = radius * radius;
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                int d2 = x * x + z * z;
                if (d2 > rr) continue;
                int ax = cx + x;
                int az = cz + z;
                for (int y = yMin; y <= yMax; ++y) {
                    IBlockState s;
                    BlockPos p = new BlockPos(ax, y, az);
                    if (!WorldAccess.isBlockLoaded(world, p) || (s = BlockStates.get(world, p)).getMaterial() == Material.air || !WorldGenMeteorImpactUtil.isVegetation(s) || p.getY() <= 5) continue;
                    BlockStates.set(world, p, BlockStates.defaultState(Blocks.air), 2);
                }
            }
        }
    }

    private static TunnelResultSlice carveSphereCount(World world, BlockPos center, int radius) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        int rr = radius * radius;
        boolean any = false;
        int firstY = 0;
        int lastY = 0;
        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    IBlockState s;
                    BlockPos p;
                    int d2 = x * x + y * y + z * z;
                    if (d2 > rr || !WorldAccess.isBlockLoaded(world, p = new BlockPos(cx + x, cy + y, cz + z)) || (s = BlockStates.get(world, p)).getMaterial() == Material.air || p.getY() <= 5) continue;
                    BlockStates.set(world, p, BlockStates.defaultState(Blocks.air), 2);
                    if (!any) {
                        any = true;
                        firstY = p.getY();
                    }
                    lastY = p.getY();
                    if (p.getY() < low) {
                        low = p.getY();
                    }
                    if (p.getY() <= high) continue;
                    high = p.getY();
                }
            }
        }
        if (!any) {
            return new TunnelResultSlice(false, 0, 0, 0, 0);
        }
        return new TunnelResultSlice(true, firstY, lastY, low, high);
    }

    private static boolean isVegetation(IBlockState s) {
        Block b = s.getBlock();
        Material m = s.getMaterial();
        if (m == Material.leaves || m == Material.wood || m == Material.vine) {
            return true;
        }
        if (b == Blocks.leaves || b == Blocks.leaves2 || b == Blocks.log || b == Blocks.log2 || b == Blocks.vine || b == Blocks.torch || b == Blocks.redstone_torch || b == Blocks.unlit_redstone_torch || b == Blocks.snow_layer || b == Blocks.ice || b == Blocks.packed_ice || b == Blocks.FROSTED_ICE || b == Blocks.fire || b == Blocks.tallgrass || b == Blocks.deadbush || b == Blocks.yellow_flower || b == Blocks.red_flower || b == Blocks.brown_mushroom || b == Blocks.red_mushroom || b == Blocks.double_plant) {
            return true;
        }
        if (b instanceof BlockOldLeaf || b instanceof BlockNewLeaf) {
            return true;
        }
        if (b instanceof BlockOldLog || b instanceof BlockNewLog) {
            return true;
        }
        if (b instanceof BlockLog) {
            return true;
        }
        return b instanceof BlockVine;
    }

    public static void updateWaterAfterImpact(World world, BlockPos surface, int radius, int depth) {
        int yMax;
        int cx = surface.getX();
        int cy = surface.getY();
        int cz = surface.getZ();
        int r = Math.max(8, (int)((float)radius * 1.8f) + 12);
        int yMin = cy - depth - 6;
        if (yMin < 1) {
            yMin = 1;
        }
        if ((yMax = cy + 24) > 255) {
            yMax = 255;
        }
        BlockStaticLiquid still = Blocks.water;
        BlockDynamicLiquid flowing = Blocks.flowing_water;
        for (int x = -r; x <= r; ++x) {
            for (int z = -r; z <= r; ++z) {
                for (int y = yMin; y <= yMax; ++y) {
                    BlockPos p = new BlockPos(cx + x, y, cz + z);
                    if (!WorldAccess.isBlockLoaded(world, p)) continue;
                    IBlockState s = BlockStates.get(world, p);
                    Block b = s.getBlock();
                    if (b == still || b == flowing) {
                        WorldAccess.scheduleUpdate(world, p, b, 1);
                        world.notifyNeighborsOfStateChange(p, b, true);
                        continue;
                    }
                    if (s.getMaterial() != Material.air) continue;
                    BlockPos p2 = p.up();
                    IBlockState s2 = BlockStates.get(world, p2);
                    Block b2 = s2.getBlock();
                    if (b2 == still || b2 == flowing) {
                        WorldAccess.scheduleUpdate(world, p2, b2, 1);
                        world.notifyNeighborsOfStateChange(p2, b2, true);
                    }
                    if ((b2 = (s2 = BlockStates.get(world, p2 = p.down())).getBlock()) == still || b2 == flowing) {
                        WorldAccess.scheduleUpdate(world, p2, b2, 1);
                        world.notifyNeighborsOfStateChange(p2, b2, true);
                    }
                    if ((b2 = (s2 = BlockStates.get(world, p2 = p.north())).getBlock()) == still || b2 == flowing) {
                        WorldAccess.scheduleUpdate(world, p2, b2, 1);
                        world.notifyNeighborsOfStateChange(p2, b2, true);
                    }
                    if ((b2 = (s2 = BlockStates.get(world, p2 = p.south())).getBlock()) == still || b2 == flowing) {
                        WorldAccess.scheduleUpdate(world, p2, b2, 1);
                        world.notifyNeighborsOfStateChange(p2, b2, true);
                    }
                    if ((b2 = (s2 = BlockStates.get(world, p2 = p.west())).getBlock()) == still || b2 == flowing) {
                        WorldAccess.scheduleUpdate(world, p2, b2, 1);
                        world.notifyNeighborsOfStateChange(p2, b2, true);
                    }
                    if ((b2 = (s2 = BlockStates.get(world, p2 = p.east())).getBlock()) != still && b2 != flowing) continue;
                    WorldAccess.scheduleUpdate(world, p2, b2, 1);
                    world.notifyNeighborsOfStateChange(p2, b2, true);
                }
            }
        }
    }

    private static final class TunnelResultSlice {
        final boolean brokeAny;
        final int firstBrokenY;
        final int lastBrokenY;
        final int lowestY;
        final int highestY;

        TunnelResultSlice(boolean brokeAny, int firstBrokenY, int lastBrokenY, int lowestY, int highestY) {
            this.brokeAny = brokeAny;
            this.firstBrokenY = firstBrokenY;
            this.lastBrokenY = lastBrokenY;
            this.lowestY = lowestY;
            this.highestY = highestY;
        }
    }

    public static final class TunnelResult {
        public final boolean anyBroken;
        public final int firstBrokenY;
        public final int lastBrokenY;
        public final int lowestY;
        public final int highestY;

        public TunnelResult(boolean anyBroken, int firstBrokenY, int lastBrokenY, int lowestY, int highestY) {
            this.anyBroken = anyBroken;
            this.firstBrokenY = firstBrokenY;
            this.lastBrokenY = lastBrokenY;
            this.lowestY = lowestY;
            this.highestY = highestY;
        }
    }

    private static final class PendingStructure {
        final String name;
        final BlockPos origin;
        final int offX;
        final int offY;
        final int offZ;
        final long executeAt;
        final long seed;

        PendingStructure(String name, BlockPos origin, int offX, int offY, int offZ, long executeAt, long seed) {
            this.name = name;
            this.origin = origin;
            this.offX = offX;
            this.offY = offY;
            this.offZ = offZ;
            this.executeAt = executeAt;
            this.seed = seed;
        }
    }
}

