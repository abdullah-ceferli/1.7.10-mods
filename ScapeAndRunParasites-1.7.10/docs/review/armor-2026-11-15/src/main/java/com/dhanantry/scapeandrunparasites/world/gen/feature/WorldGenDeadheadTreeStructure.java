/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.gen.feature.WorldGenerator
 *  net.minecraft.world.gen.structure.template.PlacementSettings
 *  net.minecraft.world.gen.structure.template.Template
 *  net.minecraft.world.gen.structure.template.TemplateManager
 */
package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.util.Mirror;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.util.Rotation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class WorldGenDeadheadTreeStructure
extends WorldGenerator {
    private static final int SOURCE_SEARCH_RADIUS = 6;
    private static final int LOWER_TRUNK_SEARCH_RADIUS = 5;
    private static final int LOWER_TRUNK_SEARCH_DEPTH = 96;
    private static final int ROOT_MAX_DEPTH = 48;
    private static final int ROOT_LEAF_START_DEPTH = 5;
    private static final float ROOT_WANDER_CHANCE = 0.22f;
    private static final float ROOT_LEAF_CHANCE = 0.28f;
    private static final EnumFacing[] ROOT_DIRECTIONS = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
    private final boolean mushroomMode;
    private final boolean rootMode;
    private static final TreeTemplate[] TREES = new TreeTemplate[]{new TreeTemplate("deadhead_tree_large_1", new BlockPos(4, 0, 5)), new TreeTemplate("deadhead_tree_large_2", new BlockPos(5, 0, 5)), new TreeTemplate("deadhead_tree_large_3", new BlockPos(4, 0, 5)), new TreeTemplate("deadhead_tree_large_4", new BlockPos(6, 0, 6))};

    public WorldGenDeadheadTreeStructure(boolean notify) {
        this(notify, false, false);
    }

    public WorldGenDeadheadTreeStructure(boolean notify, boolean mushroomMode) {
        this(notify, mushroomMode, false);
    }

    public WorldGenDeadheadTreeStructure(boolean notify, boolean mushroomMode, boolean rootMode) {
        super(notify);
        this.mushroomMode = mushroomMode;
        this.rootMode = rootMode;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (!(worldIn instanceof WorldServer)) {
            return false;
        }
        WorldServer worldServer = (WorldServer)worldIn;
        MinecraftServer server = worldServer.getMinecraftServer();
        if (server == null) {
            return false;
        }
        boolean stackingOnDeadheadLeaves = this.mushroomMode && this.isDeadheadLeaves(BlockStates.get(worldIn, position));
        TreeTemplate selected = TREES[rand.nextInt(TREES.length)];
        Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
        TemplateManager templateManager = worldServer.getStructureTemplateManager();
        Template template = templateManager.getTemplate(server, new ResourceLocation("srparasites", selected.name));
        if (template == null) {
            return false;
        }
        PlacementSettings settings = new PlacementSettings().setMirror(Mirror.NONE).setRotation(rotation).setIgnoreEntities(true).setIgnoreStructureBlock(true);
        BlockPos transformedAnchor = Template.transformedBlockPos((PlacementSettings)settings, (BlockPos)selected.saplingAnchor);
        BlockPos origin = position.subtract((Vec3i)transformedAnchor);
        BlockPos[] bounds = this.getTransformedBounds(template, settings, origin);
        BlockPos min = bounds[0];
        BlockPos max = bounds[1];
        if (min.getY() < 0 || max.getY() >= worldIn.getHeight()) {
            return false;
        }
        if (!WorldAccess.isAreaLoaded(worldIn, min, max)) {
            return false;
        }
        List<PreservedBlock> preservedIce = this.capturePreservedIce(worldIn, min, max);
        template.addBlocksToWorld(worldIn, origin, settings, 3);
        this.restorePreservedIce(worldIn, preservedIce);
        if (this.rootMode && !this.mushroomMode) {
            this.growFloatingRoots(worldIn, rand, min, max);
        }
        if (stackingOnDeadheadLeaves) {
            this.connectStackedTree(worldIn, position, min, max);
        }
        return true;
    }

    private List<PreservedBlock> capturePreservedIce(World world, BlockPos min, BlockPos max) {
        ArrayList<PreservedBlock> preserved = new ArrayList<PreservedBlock>();
        for (int x = min.getX(); x <= max.getX(); ++x) {
            for (int y = min.getY(); y <= max.getY(); ++y) {
                for (int z = min.getZ(); z <= max.getZ(); ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = BlockStates.get(world, pos);
                    if (!this.isPreservedIce(state)) continue;
                    preserved.add(new PreservedBlock(pos, state));
                }
            }
        }
        return preserved;
    }

    private void restorePreservedIce(World world, List<PreservedBlock> preserved) {
        for (PreservedBlock entry : preserved) {
            BlockStates.set(world, entry.pos, entry.state, 2);
        }
    }

    private boolean isPreservedIce(IBlockState state) {
        return state.getBlock() == Blocks.ice || state.getBlock() == Blocks.packed_ice || state.getBlock() == Blocks.FROSTED_ICE;
    }

    private void growFloatingRoots(World world, Random rand, BlockPos min, BlockPos max) {
        List<BlockPos> starts = this.findLowestTrunkLayer(world, min, max);
        for (BlockPos start : starts) {
            this.growSingleRoot(world, rand, start);
        }
    }

    private List<BlockPos> findLowestTrunkLayer(World world, BlockPos min, BlockPos max) {
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        for (int y = min.getY(); y <= max.getY(); ++y) {
            result.clear();
            for (int x = min.getX(); x <= max.getX(); ++x) {
                for (int z = min.getZ(); z <= max.getZ(); ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!this.isDeadheadTrunk(BlockStates.get(world, pos))) continue;
                    result.add(pos);
                }
            }
            if (result.isEmpty()) continue;
            return new ArrayList<BlockPos>(result);
        }
        return result;
    }

    private void growSingleRoot(World world, Random rand, BlockPos start) {
        BlockPos below;
        BlockPos cursor = start;
        ArrayList<BlockPos> rootBlocks = new ArrayList<BlockPos>();
        int verticalDepth = 0;
        for (int i = 0; i < 48 && !this.isRootGround(world, below = cursor.down()) && this.canRootReplace(world, below); ++i) {
            EnumFacing direction;
            BlockPos side;
            this.placeRootTrunk(world, below);
            rootBlocks.add(below);
            cursor = below;
            ++verticalDepth;
            if (!(rand.nextFloat() < 0.22f) || !this.canRootReplace(world, side = cursor.offset(direction = ROOT_DIRECTIONS[rand.nextInt(ROOT_DIRECTIONS.length)])) || this.isRootGround(world, side.down())) continue;
            this.placeRootTrunk(world, side);
            rootBlocks.add(side);
            cursor = side;
        }
        if (verticalDepth > 5) {
            this.addRootLeaves(world, rand, rootBlocks);
        }
    }

    private void addRootLeaves(World world, Random rand, List<BlockPos> rootBlocks) {
        if (rootBlocks.size() <= 5) {
            return;
        }
        IBlockState leaves = BlockStates.defaultState(SRPBlocks.DeadheadLeaves);
        for (int i = 5; i < rootBlocks.size(); ++i) {
            if (rand.nextFloat() >= 0.28f) continue;
            BlockPos rootPos = rootBlocks.get(i);
            int attempts = 1 + rand.nextInt(2);
            for (int j = 0; j < attempts; ++j) {
                EnumFacing direction = ROOT_DIRECTIONS[rand.nextInt(ROOT_DIRECTIONS.length)];
                BlockPos leafPos = rootPos.offset(direction);
                if (!this.canPlaceRootLeaf(world, leafPos)) continue;
                BlockStates.set(world, leafPos, leaves, 2);
            }
        }
    }

    private boolean canPlaceRootLeaf(World world, BlockPos pos) {
        if (pos.getY() <= 0 || pos.getY() >= world.getHeight()) {
            return false;
        }
        IBlockState state = BlockStates.get(world, pos);
        if (this.isDeadheadTrunk(state)) {
            return false;
        }
        if (state.getBlock().isLeaves(state, (IBlockAccess)world, pos)) {
            return false;
        }
        return WorldAccess.isAirBlock(world, pos) || state.getBlock().isReplaceable((IBlockAccess)world, pos);
    }

    private boolean isRootGround(World world, BlockPos pos) {
        if (pos.getY() <= 0) {
            return true;
        }
        IBlockState state = BlockStates.get(world, pos);
        if (this.isDeadheadTrunk(state)) {
            return true;
        }
        if (state.getBlock().isLeaves(state, (IBlockAccess)world, pos)) {
            return false;
        }
        if (WorldAccess.isAirBlock(world, pos) || state.getBlock().isReplaceable((IBlockAccess)world, pos)) {
            return false;
        }
        return state.getMaterial().isSolid() || state.getMaterial().isLiquid();
    }

    private boolean canRootReplace(World world, BlockPos pos) {
        if (pos.getY() <= 0 || pos.getY() >= world.getHeight()) {
            return false;
        }
        IBlockState state = BlockStates.get(world, pos);
        if (WorldAccess.isAirBlock(world, pos)) {
            return true;
        }
        if (state.getBlock().isLeaves(state, (IBlockAccess)world, pos)) {
            return true;
        }
        return state.getBlock().isReplaceable((IBlockAccess)world, pos);
    }

    private void placeRootTrunk(World world, BlockPos pos) {
        BlockStates.set(world, pos, this.getDeadheadTrunkState(), 2);
    }

    private void connectStackedTree(World world, BlockPos anchor, BlockPos min, BlockPos max) {
        BlockPos upperTrunk = this.findLowestUpperTrunk(world, anchor, min, max);
        if (upperTrunk == null) {
            return;
        }
        BlockPos lowerTrunk = this.findHighestLowerTrunk(world, upperTrunk);
        if (lowerTrunk == null) {
            return;
        }
        List<BlockPos> path = this.buildDownwardPath(upperTrunk, lowerTrunk);
        if (!this.canCarveConnection(world, path, lowerTrunk)) {
            return;
        }
        IBlockState trunkState = this.getDeadheadTrunkState();
        for (BlockPos pos : path) {
            if (this.isDeadheadTrunk(BlockStates.get(world, pos))) continue;
            BlockStates.set(world, pos, trunkState, 2);
        }
    }

    private BlockPos findLowestUpperTrunk(World world, BlockPos anchor, BlockPos min, BlockPos max) {
        int minX = Math.max(min.getX(), anchor.getX() - 6);
        int maxX = Math.min(max.getX(), anchor.getX() + 6);
        int minZ = Math.max(min.getZ(), anchor.getZ() - 6);
        int maxZ = Math.min(max.getZ(), anchor.getZ() + 6);
        int minY = Math.max(min.getY(), anchor.getY());
        int maxY = max.getY();
        BlockPos best = null;
        long bestHorizontalDistance = Long.MAX_VALUE;
        for (int y = minY; y <= maxY; ++y) {
            BlockPos bestAtY = null;
            long bestAtYDistance = Long.MAX_VALUE;
            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    long dz;
                    long dx;
                    long distance;
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = BlockStates.get(world, pos);
                    if (!this.isDeadheadTrunk(state) || (distance = (dx = (long)(x - anchor.getX())) * dx + (dz = (long)(z - anchor.getZ())) * dz) >= bestAtYDistance) continue;
                    bestAtYDistance = distance;
                    bestAtY = pos;
                }
            }
            if (bestAtY == null) continue;
            best = bestAtY;
            bestHorizontalDistance = bestAtYDistance;
            break;
        }
        return best;
    }

    private BlockPos findHighestLowerTrunk(World world, BlockPos upperTrunk) {
        int minY = Math.max(1, upperTrunk.getY() - 96);
        for (int y = upperTrunk.getY() - 1; y >= minY; --y) {
            BlockPos bestAtY = null;
            long bestDistance = Long.MAX_VALUE;
            for (int x = upperTrunk.getX() - 5; x <= upperTrunk.getX() + 5; ++x) {
                for (int z = upperTrunk.getZ() - 5; z <= upperTrunk.getZ() + 5; ++z) {
                    long dz;
                    long dx;
                    long distance;
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = BlockStates.get(world, pos);
                    if (!this.isDeadheadTrunk(state) || (distance = (dx = (long)(x - upperTrunk.getX())) * dx + (dz = (long)(z - upperTrunk.getZ())) * dz) >= bestDistance) continue;
                    bestDistance = distance;
                    bestAtY = pos;
                }
            }
            if (bestAtY == null) continue;
            return bestAtY;
        }
        return null;
    }

    private List<BlockPos> buildDownwardPath(BlockPos upper, BlockPos lower) {
        ArrayList<BlockPos> path = new ArrayList<BlockPos>();
        BlockPos cursor = upper;
        int totalDrop = Math.max(1, upper.getY() - lower.getY());
        int step = 1;
        while (cursor.getY() > lower.getY()) {
            cursor = cursor.down();
            this.addUnique(path, cursor);
            double progress = Math.min(1.0, (double)step / (double)totalDrop);
            int desiredX = (int)Math.round((double)upper.getX() + (double)(lower.getX() - upper.getX()) * progress);
            int desiredZ = (int)Math.round((double)upper.getZ() + (double)(lower.getZ() - upper.getZ()) * progress);
            while (cursor.getX() != desiredX) {
                cursor = cursor.add(Integer.compare(desiredX, cursor.getX()), 0, 0);
                this.addUnique(path, cursor);
            }
            while (cursor.getZ() != desiredZ) {
                cursor = cursor.add(0, 0, Integer.compare(desiredZ, cursor.getZ()));
                this.addUnique(path, cursor);
            }
            ++step;
        }
        while (cursor.getX() != lower.getX()) {
            cursor = cursor.add(Integer.compare(lower.getX(), cursor.getX()), 0, 0);
            this.addUnique(path, cursor);
        }
        while (cursor.getZ() != lower.getZ()) {
            cursor = cursor.add(0, 0, Integer.compare(lower.getZ(), cursor.getZ()));
            this.addUnique(path, cursor);
        }
        return path;
    }

    private boolean canCarveConnection(World world, List<BlockPos> path, BlockPos targetTrunk) {
        for (BlockPos pos : path) {
            IBlockState state = BlockStates.get(world, pos);
            if (pos.equals((Object)targetTrunk) || this.isDeadheadTrunk(state) || WorldAccess.isAirBlock(world, pos) || state.getBlock().isLeaves(state, (IBlockAccess)world, pos) || state.getBlock().isReplaceable((IBlockAccess)world, pos)) continue;
            return false;
        }
        return true;
    }

    private void addUnique(List<BlockPos> path, BlockPos pos) {
        if (path.isEmpty() || !path.get(path.size() - 1).equals((Object)pos)) {
            path.add(pos);
        }
    }

    private boolean isDeadheadLeaves(IBlockState state) {
        return state.getBlock() == SRPBlocks.DeadheadLeaves;
    }

    private boolean isDeadheadTrunk(IBlockState state) {
        return state.getBlock() == SRPBlocks.ParasiteTrunk && state.getValue(BlockParasiteTrunk.VARIANT) == BlockParasiteTrunk.EnumType.DEADHEAD;
    }

    private IBlockState getDeadheadTrunkState() {
        return BlockStates.defaultState(SRPBlocks.ParasiteTrunk).withProperty(BlockParasiteTrunk.VARIANT, (Comparable)((Object)BlockParasiteTrunk.EnumType.DEADHEAD)).withProperty((IProperty)BlockParasiteTrunk.AXIS, (Comparable)EnumFacing.Axis.Y);
    }

    private BlockPos[] getTransformedBounds(Template template, PlacementSettings settings, BlockPos origin) {
        BlockPos size = template.getSize();
        int x1 = Math.max(0, size.getX() - 1);
        int y1 = Math.max(0, size.getY() - 1);
        int z1 = Math.max(0, size.getZ() - 1);
        BlockPos[] corners = new BlockPos[]{new BlockPos(0, 0, 0), new BlockPos(x1, 0, 0), new BlockPos(0, y1, 0), new BlockPos(0, 0, z1), new BlockPos(x1, y1, 0), new BlockPos(x1, 0, z1), new BlockPos(0, y1, z1), new BlockPos(x1, y1, z1)};
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos corner : corners) {
            BlockPos transformed = Template.transformedBlockPos((PlacementSettings)settings, (BlockPos)corner).add((Vec3i)origin);
            minX = Math.min(minX, transformed.getX());
            minY = Math.min(minY, transformed.getY());
            minZ = Math.min(minZ, transformed.getZ());
            maxX = Math.max(maxX, transformed.getX());
            maxY = Math.max(maxY, transformed.getY());
            maxZ = Math.max(maxZ, transformed.getZ());
        }
        return new BlockPos[]{new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)};
    }

    private static class TreeTemplate {
        private final String name;
        private final BlockPos saplingAnchor;

        private TreeTemplate(String name, BlockPos saplingAnchor) {
            this.name = name;
            this.saplingAnchor = saplingAnchor;
        }
    }

    private static class PreservedBlock {
        private final BlockPos pos;
        private final IBlockState state;

        private PreservedBlock(BlockPos pos, IBlockState state) {
            this.pos = pos;
            this.state = state;
        }
    }
}

