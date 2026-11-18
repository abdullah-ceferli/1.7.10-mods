/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockRotatedPillar
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.EntityVillager
 *  net.minecraft.init.Biomes
 *  net.minecraft.init.Blocks
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.chunk.IChunkProvider
 *  net.minecraft.world.gen.IChunkGenerator
 *  net.minecraft.world.gen.structure.template.PlacementSettings
 *  net.minecraft.world.gen.structure.template.Template
 *  net.minecraft.world.gen.structure.template.Template$BlockInfo
 *  net.minecraft.world.gen.structure.template.TemplateManager
 *  net.minecraft.world.storage.loot.LootContext
 *  net.minecraft.world.storage.loot.LootContext$Builder
 *  net.minecraft.world.storage.loot.LootTable
 *  net.minecraft.world.storage.loot.LootTableList
 *  net.minecraftforge.fml.common.IWorldGenerator
 *  net.minecraftforge.fml.relauncher.ReflectionHelper
 */
package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.star.SRPColdVillageWallGenerator;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.util.Mirror;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.util.Rotation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class SRPColdVillageGenerator
implements IWorldGenerator {
    private static final int VILLAGE_DISTANCE = 20;
    private static final int VILLAGE_SEPARATION = 5;
    private static final int VILLAGE_SALT = 10387312;
    private static final ResourceLocation WELL = new ResourceLocation("srparasites", "dh_village_well");
    private static final ResourceLocation BLACKSMITH = new ResourceLocation("srparasites", "dh_village_blacksmith");
    private static final ResourceLocation CHURCH = new ResourceLocation("srparasites", "dh_village_church");
    private static final ResourceLocation FARM = new ResourceLocation("srparasites", "dh_village_farm");
    private static final ResourceLocation MEDIUM_HOUSE = new ResourceLocation("srparasites", "dh_village_medium_house");
    private static final ResourceLocation MEDIUM_HOUSE_1 = new ResourceLocation("srparasites", "dh_village_medium_house_1");
    private static final ResourceLocation SMALL_1 = new ResourceLocation("srparasites", "dh_village_small1");
    private static final ResourceLocation SMALL_2 = new ResourceLocation("srparasites", "dh_village_small2");

    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world == null || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        if (SRPWorldEntitySpawner.starType != 1) {
            return;
        }
        if (!this.isVillageChunk(world, chunkX, chunkZ)) {
            return;
        }
        int centerX = (chunkX << 4) + 8;
        int centerZ = (chunkZ << 4) + 8;
        BlockPos center = this.findTerrainSurface(world, new BlockPos(centerX, 0, centerZ));
        if (center == null) {
            return;
        }
        if (!this.isValidColdVillageBiome(world, center)) {
            return;
        }
        if (this.isBadBuildArea(world, center, 12)) {
            return;
        }
        this.generateVillage(world, random, center);
    }

    private boolean isVillageChunk(World world, int chunkX, int chunkZ) {
        int regionX = chunkX;
        int regionZ = chunkZ;
        if (regionX < 0) {
            regionX -= 19;
        }
        if (regionZ < 0) {
            regionZ -= 19;
        }
        int gridX = regionX / 20;
        int gridZ = regionZ / 20;
        Random rand = world.setRandomSeed(gridX, gridZ, 10387312);
        gridX *= 20;
        gridZ *= 20;
        return chunkX == (gridX += rand.nextInt(15)) && chunkZ == (gridZ += rand.nextInt(15));
    }

    private boolean isValidColdVillageBiome(World world, BlockPos center) {
        int radius = 20;
        int checked = 0;
        int valid = 0;
        for (int x = -radius; x <= radius; x += 8) {
            for (int z = -radius; z <= radius; z += 8) {
                ++checked;
                if (!this.isColdVillageBiome(world.getBiome(center.add(x, 0, z)))) continue;
                ++valid;
            }
        }
        return checked > 0 && valid >= Math.max(1, checked * 2 / 3);
    }

    private boolean isColdVillageBiome(Biome biome) {
        return biome == Biomes.icePlains || biome == Biomes.coldTaiga || biome == Biomes.coldTaigaHills || biome == Biomes.coldBeach || biome == Biomes.frozenRiver;
    }

    private void generateVillage(World world, Random random, BlockPos center) {
        Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
        VillageStyle style = this.pickVillageStyle(random);
        ArrayList<SRPColdVillageWallGenerator.WallExclusion> wallExclusions = new ArrayList<SRPColdVillageWallGenerator.WallExclusion>();
        int buildingCount = 0;
        BlockPos wellPos = center.down(7);
        if (this.placeAnchored(world, WELL, wellPos, rotation, false)) {
            this.addWallExclusion(world, wallExclusions, WELL, wellPos, rotation, 7);
            ++buildingCount;
        }
        if ((buildingCount += this.placePatternBuildings(world, random, center, rotation, style, wallExclusions)) >= 2) {
            SRPColdVillageWallGenerator.generate(world, random, center, buildingCount, wallExclusions);
            this.scatterLogPiles(world, random, center, wallExclusions, buildingCount, style);
        }
        this.spawnVillagers(world, random, center, Math.max(2, buildingCount + style.extraVillagers));
    }

    private VillageStyle pickVillageStyle(Random random) {
        int roll = random.nextInt(100);
        if (roll < 28) {
            return new VillageStyle(86, -15, 0, 0);
        }
        if (roll < 74) {
            return new VillageStyle(100, 0, 1, 1);
        }
        return new VillageStyle(116, 15, 2, 2);
    }

    private int placePatternBuildings(World world, Random random, BlockPos center, Rotation rotation, VillageStyle style, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions) {
        int count = 0;
        if (this.placeAnchoredScaled(world, wallExclusions, SMALL_1, center, this.scaleOffset(-16, style.spreadPercent), 0, rotation, false)) {
            ++count;
        }
        if (this.placeAnchoredScaled(world, wallExclusions, SMALL_2, center, this.scaleOffset(16, style.spreadPercent), 0, rotation, false)) {
            ++count;
        }
        if (this.placeAnchoredScaled(world, wallExclusions, MEDIUM_HOUSE, center, this.scaleOffset(-8, style.spreadPercent), this.scaleOffset(-18, style.spreadPercent), rotation, false)) {
            ++count;
        }
        if (this.shouldPlaceOptional(random, 70 + style.optionalBias) && this.placeAnchoredScaled(world, wallExclusions, MEDIUM_HOUSE_1, center, this.scaleOffset(8, style.spreadPercent), this.scaleOffset(18, style.spreadPercent), rotation, false)) {
            ++count;
        }
        if (this.shouldPlaceOptional(random, 58 + style.optionalBias) && this.placeAnchoredScaled(world, wallExclusions, BLACKSMITH, center, this.scaleOffset(22, style.spreadPercent), this.scaleOffset(-12, style.spreadPercent), rotation, true)) {
            ++count;
        }
        if (this.shouldPlaceOptional(random, 48 + style.optionalBias) && this.placeAnchoredScaled(world, wallExclusions, CHURCH, center, this.scaleOffset(-22, style.spreadPercent), this.scaleOffset(14, style.spreadPercent), rotation, false)) {
            ++count;
        }
        if (this.shouldPlaceOptional(random, 66 + style.optionalBias) && this.placeAnchoredScaled(world, wallExclusions, FARM, center, this.scaleOffset(22, style.spreadPercent), this.scaleOffset(18, style.spreadPercent), rotation, false)) {
            ++count;
        }
        if (style.spreadPercent >= 110 && this.shouldPlaceOptional(random, 45) && this.placeAnchoredScaled(world, wallExclusions, random.nextBoolean() ? SMALL_1 : SMALL_2, center, this.scaleOffset(-24, style.spreadPercent), this.scaleOffset(18, style.spreadPercent), rotation, false)) {
            ++count;
        }
        return count;
    }

    private boolean shouldPlaceOptional(Random random, int percent) {
        percent = Math.max(0, Math.min(100, percent));
        return random.nextInt(100) < percent;
    }

    private int scaleOffset(int value, int percent) {
        return (int)Math.round((double)value * ((double)percent / 100.0));
    }

    private boolean placeAnchoredScaled(World world, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions, ResourceLocation structure, BlockPos center, int offsetX, int offsetZ, Rotation rotation, boolean blacksmith) {
        BlockPos offset = this.rotateOffset(offsetX, offsetZ, rotation);
        BlockPos doorPos = center.add(offset.getX(), 0, offset.getZ());
        if (this.isBadBuildArea(world, doorPos, 8)) {
            return false;
        }
        if (this.isIceLakeArea(world, doorPos, 7)) {
            return false;
        }
        BlockPos surfaceDoorPos = this.findTerrainSurface(world, doorPos);
        if (surfaceDoorPos == null) {
            return false;
        }
        if (this.placeAnchored(world, structure, surfaceDoorPos, rotation, blacksmith)) {
            this.addWallExclusion(world, wallExclusions, structure, surfaceDoorPos, rotation, 6);
            return true;
        }
        return false;
    }

    private void addWallExclusion(World world, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions, ResourceLocation structure, BlockPos origin, Rotation rotation, int padding) {
        if (world.getMinecraftServer() == null || wallExclusions == null) {
            return;
        }
        TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
        Template template = manager.getTemplate(world.getMinecraftServer(), structure);
        if (template == null) {
            return;
        }
        BlockPos size = template.getSize();
        int maxLocalX = Math.max(0, size.getX() - 1);
        int maxLocalZ = Math.max(0, size.getZ() - 1);
        BlockPos a = this.transformLocal(origin, 0, 0, 0, size, rotation);
        BlockPos b = this.transformLocal(origin, maxLocalX, 0, 0, size, rotation);
        BlockPos c = this.transformLocal(origin, 0, 0, maxLocalZ, size, rotation);
        BlockPos d = this.transformLocal(origin, maxLocalX, 0, maxLocalZ, size, rotation);
        int minX = Math.min(Math.min(a.getX(), b.getX()), Math.min(c.getX(), d.getX()));
        int maxX = Math.max(Math.max(a.getX(), b.getX()), Math.max(c.getX(), d.getX()));
        int minZ = Math.min(Math.min(a.getZ(), b.getZ()), Math.min(c.getZ(), d.getZ()));
        int maxZ = Math.max(Math.max(a.getZ(), b.getZ()), Math.max(c.getZ(), d.getZ()));
        wallExclusions.add(new SRPColdVillageWallGenerator.WallExclusion(minX, maxX, minZ, maxZ, padding));
    }

    private BlockPos rotateOffset(int x, int z, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_90: {
                return new BlockPos(-z, 0, x);
            }
            case CLOCKWISE_180: {
                return new BlockPos(-x, 0, -z);
            }
            case COUNTERCLOCKWISE_90: {
                return new BlockPos(z, 0, -x);
            }
        }
        return new BlockPos(x, 0, z);
    }

    private boolean placeAnchored(World world, ResourceLocation structure, BlockPos entrancePos, Rotation rotation, boolean blacksmith) {
        if (world.getMinecraftServer() == null) {
            return false;
        }
        TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
        Template template = manager.getTemplate(world.getMinecraftServer(), structure);
        if (template == null) {
            return false;
        }
        if (this.isBadBuildArea(world, entrancePos, 6)) {
            return false;
        }
        this.clearTreeBlocksForTemplate(world, template, entrancePos, rotation);
        PlacementSettings settings = new PlacementSettings().setMirror(Mirror.NONE).setRotation(rotation).setIgnoreEntities(false).setChunk(null).setReplacedBlock(null).setIgnoreStructureBlock(false);
        template.addBlocksToWorld(world, entrancePos, settings);
        this.buildRubbleSupports(world, template, entrancePos, rotation);
        if (blacksmith) {
            this.fillBlacksmithChests(world, entrancePos, template, rotation);
        }
        return true;
    }

    private boolean isIceLakeArea(World world, BlockPos center, int radius) {
        int bad = 0;
        int checked = 0;
        for (int x = -radius; x <= radius; x += 4) {
            for (int z = -radius; z <= radius; z += 4) {
                BlockPos surface = this.findTerrainSurface(world, center.add(x, 0, z));
                ++checked;
                if (surface != null && !this.isBadVillageSurface(BlockStates.get(world, surface.down()))) continue;
                ++bad;
            }
        }
        return checked > 0 && bad > Math.max(4, checked / 2);
    }

    private boolean isBadBuildArea(World world, BlockPos center, int radius) {
        radius = Math.max(3, Math.min(radius, 12));
        int bad = 0;
        int checked = 0;
        int minY = 255;
        int maxY = 0;
        for (int x = -radius; x <= radius; x += 4) {
            for (int z = -radius; z <= radius; z += 4) {
                BlockPos surface = this.findTerrainSurface(world, center.add(x, 0, z));
                ++checked;
                if (surface == null || this.isBadVillageSurface(BlockStates.get(world, surface.down()))) {
                    ++bad;
                    continue;
                }
                minY = Math.min(minY, surface.getY());
                maxY = Math.max(maxY, surface.getY());
            }
        }
        if (checked <= 0) {
            return true;
        }
        if (bad > Math.max(2, checked / 3)) {
            return true;
        }
        return maxY - minY > 8;
    }

    private boolean isBadVillageSurface(IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.water || block == Blocks.flowing_water || block == Blocks.ice || block == Blocks.packed_ice || block == Blocks.FROSTED_ICE || material == Material.water;
    }

    private BlockPos findTerrainSurface(World world, BlockPos pos) {
        BlockPos p = WorldAccess.getHeight(world, new BlockPos(pos.getX(), 0, pos.getZ())).down();
        while (p.getY() > 1) {
            IBlockState state = BlockStates.get(world, p);
            if (!this.isSurfaceJunk(state)) {
                if (state.getBlock().isAir(state, (IBlockAccess)world, p)) {
                    p = p.down();
                    continue;
                }
                if (state.isSideSolid((IBlockAccess)world, p, EnumFacing.UP)) {
                    return p.up();
                }
            }
            p = p.down();
        }
        return null;
    }

    private boolean isSurfaceJunk(IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.air || block == Blocks.snow_layer || block == Blocks.leaves || block == Blocks.leaves2 || block == Blocks.log || block == Blocks.log2 || block == Blocks.tallgrass || block == Blocks.deadbush || material == Material.plants || material == Material.vine || material == Material.leaves || material == Material.snow;
    }

    private void clearTreeBlocksForTemplate(World world, Template template, BlockPos origin, Rotation rotation) {
        BlockPos size = template.getSize();
        int sizeX = size.getX();
        int sizeY = Math.min(size.getY() + 3, 18);
        int sizeZ = size.getZ();
        for (int localX = -1; localX <= sizeX; ++localX) {
            for (int localZ = -1; localZ <= sizeZ; ++localZ) {
                for (int localY = 0; localY <= sizeY; ++localY) {
                    BlockPos p = this.transformLocal(origin, localX, localY, localZ, size, rotation);
                    IBlockState state = BlockStates.get(world, p);
                    if (!this.isTreeBlock(state)) continue;
                    WorldAccess.setBlockToAir(world, p);
                }
            }
        }
    }

    private boolean isTreeBlock(IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.leaves || block == Blocks.leaves2 || block == Blocks.log || block == Blocks.log2 || material == Material.leaves;
    }

    private void buildRubbleSupports(World world, Template template, BlockPos origin, Rotation rotation) {
        List<Template.BlockInfo> blocks = this.getTemplateBlocks(template);
        if (blocks == null || blocks.isEmpty()) {
            return;
        }
        BlockPos size = template.getSize();
        int foundationY = this.findTemplateFoundationLayer(blocks);
        if (foundationY < 0) {
            return;
        }
        IBlockState averageSupportState = this.findAverageSupportState(world, blocks, origin, size, rotation, foundationY);
        for (Template.BlockInfo info : blocks) {
            BlockPos sourcePos;
            IBlockState sourceState;
            if (info == null || info.pos == null || info.blockState == null || info.pos.getY() != foundationY || !this.isValidFoundationSource(info.blockState) || !this.isValidFoundationSource(sourceState = BlockStates.get(world, sourcePos = this.transformLocal(origin, info.pos.getX(), info.pos.getY(), info.pos.getZ(), size, rotation)))) continue;
            IBlockState supportState = this.isGoodSupportCube(sourceState) ? sourceState : averageSupportState;
            this.duplicateFoundationDown(world, sourcePos, supportState);
        }
    }

    private IBlockState findAverageSupportState(World world, List<Template.BlockInfo> blocks, BlockPos origin, BlockPos size, Rotation rotation, int foundationY) {
        HashMap<IBlockState, Integer> counts = new HashMap<IBlockState, Integer>();
        IBlockState bestState = BlockStates.defaultState(Blocks.dirt);
        int bestCount = 0;
        for (Template.BlockInfo info : blocks) {
            BlockPos worldPos;
            IBlockState worldState;
            if (info == null || info.pos == null || info.blockState == null || info.pos.getY() != foundationY || !this.isValidFoundationSource(info.blockState) || !this.isValidFoundationSource(worldState = BlockStates.get(world, worldPos = this.transformLocal(origin, info.pos.getX(), info.pos.getY(), info.pos.getZ(), size, rotation))) || !this.isGoodSupportCube(worldState)) continue;
            Integer count = (Integer)counts.get(worldState);
            int newCount = count == null ? 1 : count + 1;
            counts.put(worldState, newCount);
            if (newCount <= bestCount) continue;
            bestCount = newCount;
            bestState = worldState;
        }
        return bestState;
    }

    private boolean isGoodSupportCube(IBlockState state) {
        if (state == null) {
            return false;
        }
        Block block = state.getBlock();
        Material material = state.getMaterial();
        if (block == Blocks.air || block == Blocks.STRUCTURE_VOID) {
            return false;
        }
        if (material == Material.air || material == Material.plants || material == Material.vine || material == Material.leaves || material == Material.snow || material == Material.water || material == Material.lava) {
            return false;
        }
        return state.isFullCube();
    }

    private int findTemplateFoundationLayer(List<Template.BlockInfo> blocks) {
        int y;
        int[] counts = new int[256];
        int maxCount = 0;
        for (Template.BlockInfo info : blocks) {
            int y2;
            if (info == null || info.pos == null || info.blockState == null || !this.isValidFoundationSource(info.blockState) || (y2 = info.pos.getY()) < 0 || y2 >= counts.length) continue;
            int n = y2;
            counts[n] = counts[n] + 1;
            maxCount = Math.max(maxCount, counts[y2]);
        }
        if (maxCount <= 0) {
            return -1;
        }
        int minimumUsefulCount = Math.max(6, maxCount / 3);
        for (y = 0; y < counts.length; ++y) {
            if (counts[y] < minimumUsefulCount) continue;
            return y;
        }
        for (y = 0; y < counts.length; ++y) {
            if (counts[y] <= 0) continue;
            return y;
        }
        return -1;
    }

    private List<Template.BlockInfo> getTemplateBlocks(Template template) {
        try {
            Field field = ReflectionHelper.findField(Template.class, (String)"blocks", (String)"field_186270_a");
            field.setAccessible(true);
            return (List)field.get(template);
        }
        catch (Throwable throwable) {
            return Collections.emptyList();
        }
    }

    private void duplicateFoundationDown(World world, BlockPos sourcePos, IBlockState sourceState) {
        BlockPos pos = sourcePos.down();
        int placed = 0;
        while (pos.getY() > 1 && placed < 32) {
            IBlockState state = BlockStates.get(world, pos);
            Block block = state.getBlock();
            Material material = state.getMaterial();
            if (block == Blocks.snow_layer || material == Material.snow) {
                BlockStates.set(world, pos, sourceState, 2);
                pos = pos.down();
                ++placed;
                continue;
            }
            if (!this.shouldFillSupport(world, pos)) break;
            BlockStates.set(world, pos, sourceState, 2);
            pos = pos.down();
            ++placed;
        }
    }

    private boolean isValidFoundationSource(IBlockState state) {
        if (state == null) {
            return false;
        }
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block != Blocks.air && block != Blocks.STRUCTURE_VOID && block != Blocks.snow_layer && block != Blocks.tallgrass && block != Blocks.deadbush && block != Blocks.leaves && block != Blocks.leaves2 && block != Blocks.log && block != Blocks.log2 && material != Material.air && material != Material.plants && material != Material.vine && material != Material.leaves && material != Material.snow && material != Material.water && material != Material.lava;
    }

    private boolean shouldFillSupport(World world, BlockPos pos) {
        IBlockState state = BlockStates.get(world, pos);
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.air || block == Blocks.snow_layer || block == Blocks.tallgrass || block == Blocks.deadbush || block == Blocks.leaves || block == Blocks.leaves2 || block == Blocks.log || block == Blocks.log2 || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.ice || block == Blocks.packed_ice || block == Blocks.FROSTED_ICE || material == Material.plants || material == Material.vine || material == Material.leaves || material == Material.snow || material == Material.water;
    }

    private BlockPos transformLocal(BlockPos origin, int localX, int localY, int localZ, BlockPos size, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_90: {
                return origin.add(size.getZ() - 1 - localZ, localY, localX);
            }
            case CLOCKWISE_180: {
                return origin.add(size.getX() - 1 - localX, localY, size.getZ() - 1 - localZ);
            }
            case COUNTERCLOCKWISE_90: {
                return origin.add(localZ, localY, size.getX() - 1 - localX);
            }
        }
        return origin.add(localX, localY, localZ);
    }

    private void scatterLogPiles(World world, Random random, BlockPos center, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions, int buildingCount, VillageStyle style) {
        if (world == null || wallExclusions == null || wallExclusions.isEmpty()) {
            return;
        }
        Bounds bounds = this.computeExclusionBounds(wallExclusions, center);
        bounds.minX -= 10;
        bounds.maxX += 10;
        bounds.minZ -= 10;
        bounds.maxZ += 10;
        int pileCount = 1 + random.nextInt(2 + Math.max(0, style.logPileBias));
        if (buildingCount >= 6 && random.nextBoolean()) {
            ++pileCount;
        }
        for (int i = 0; i < pileCount; ++i) {
            BlockPos surface;
            int z;
            int x;
            for (int tries = 0; tries < 64 && (this.isInsideExclusionWithPadding(x = this.randomBetween(random, bounds.minX + 8, bounds.maxX - 8), z = this.randomBetween(random, bounds.minZ + 8, bounds.maxZ - 8), wallExclusions, 8) || (surface = this.findTerrainSurface(world, new BlockPos(x, 0, z))) == null || this.isBadVillageSurface(BlockStates.get(world, surface.down())) || !this.placeLogPile(world, random, surface)); ++tries) {
            }
        }
    }

    private Bounds computeExclusionBounds(List<SRPColdVillageWallGenerator.WallExclusion> exclusions, BlockPos fallbackCenter) {
        Bounds bounds = new Bounds();
        bounds.minX = Integer.MAX_VALUE;
        bounds.maxX = Integer.MIN_VALUE;
        bounds.minZ = Integer.MAX_VALUE;
        bounds.maxZ = Integer.MIN_VALUE;
        for (SRPColdVillageWallGenerator.WallExclusion exclusion : exclusions) {
            if (exclusion == null) continue;
            bounds.minX = Math.min(bounds.minX, exclusion.minX);
            bounds.maxX = Math.max(bounds.maxX, exclusion.maxX);
            bounds.minZ = Math.min(bounds.minZ, exclusion.minZ);
            bounds.maxZ = Math.max(bounds.maxZ, exclusion.maxZ);
        }
        if (bounds.minX == Integer.MAX_VALUE) {
            bounds.minX = fallbackCenter.getX() - 16;
            bounds.maxX = fallbackCenter.getX() + 16;
            bounds.minZ = fallbackCenter.getZ() - 16;
            bounds.maxZ = fallbackCenter.getZ() + 16;
        }
        return bounds;
    }

    private boolean isInsideExclusionWithPadding(int x, int z, List<SRPColdVillageWallGenerator.WallExclusion> exclusions, int extraPadding) {
        for (SRPColdVillageWallGenerator.WallExclusion exclusion : exclusions) {
            if (exclusion == null || x < exclusion.minX - extraPadding || x > exclusion.maxX + extraPadding || z < exclusion.minZ - extraPadding || z > exclusion.maxZ + extraPadding) continue;
            return true;
        }
        return false;
    }

    private int randomBetween(Random random, int min, int max) {
        if (max <= min) {
            return min;
        }
        return min + random.nextInt(max - min + 1);
    }

    private boolean placeLogPile(World world, Random random, BlockPos surface) {
        BlockPos base;
        BlockPos target;
        int i;
        boolean axisX = random.nextBoolean();
        int length = 2 + random.nextInt(3);
        int dir = random.nextBoolean() ? 1 : -1;
        IBlockState logState = BlockStates.defaultState(SRPBlocks.ParasiteTrunk).withProperty(BlockParasiteTrunk.VARIANT, (Comparable)((Object)BlockParasiteTrunk.EnumType.DEADHEAD)).withProperty((IProperty)BlockRotatedPillar.AXIS, (Comparable)(axisX ? EnumFacing.Axis.X : EnumFacing.Axis.Z));
        for (i = 0; i < length; ++i) {
            target = axisX ? surface.add(i * dir, 0, 0) : surface.add(0, 0, i * dir);
            base = this.findTerrainSurface(world, target);
            if (base != null && this.canPlaceLogPileAt(world, base)) continue;
            return false;
        }
        for (i = 0; i < length; ++i) {
            target = axisX ? surface.add(i * dir, 0, 0) : surface.add(0, 0, i * dir);
            base = this.findTerrainSurface(world, target);
            if (base == null) continue;
            BlockStates.set(world, base, logState, 2);
        }
        if (length >= 3 && random.nextBoolean()) {
            BlockPos topTarget;
            BlockPos blockPos = topTarget = axisX ? surface.add(dir, 1, 0) : surface.add(0, 1, dir);
            if (WorldAccess.isAirBlock(world, topTarget)) {
                BlockStates.set(world, topTarget, logState, 2);
            }
        }
        return true;
    }

    private boolean canPlaceLogPileAt(World world, BlockPos pos) {
        if (pos == null || pos.getY() <= 1) {
            return false;
        }
        IBlockState below = BlockStates.get(world, pos.down());
        if (this.isBadVillageSurface(below)) {
            return false;
        }
        IBlockState state = BlockStates.get(world, pos);
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return block == Blocks.air || block == Blocks.snow_layer || block == Blocks.tallgrass || block == Blocks.deadbush || material == Material.plants || material == Material.vine || material == Material.snow || state.getBlock().isReplaceable((IBlockAccess)world, pos);
    }

    private void spawnVillagers(World world, Random random, BlockPos center, int count) {
        if (world.isRemote) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            BlockPos pos = this.findVillagerSpawnPos(world, random, center);
            if (pos == null) continue;
            EntityVillager villager = new EntityVillager(world);
            villager.setPosition((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5);
            villager.onInitialSpawn(DifficultyAccess.at(world, pos), null);
            world.spawnEntityInWorld((Entity)villager);
        }
    }

    private BlockPos findVillagerSpawnPos(World world, Random random, BlockPos center) {
        for (int tries = 0; tries < 32; ++tries) {
            int z;
            int x = center.getX() + random.nextInt(45) - 22;
            BlockPos pos = this.findTerrainSurface(world, new BlockPos(x, 0, z = center.getZ() + random.nextInt(45) - 22));
            if (pos == null || !WorldAccess.isAirBlock(world, pos) || !WorldAccess.isAirBlock(world, pos.up()) || !BlockStates.get(world, pos.down()).isSideSolid((IBlockAccess)world, pos.down(), EnumFacing.UP)) continue;
            return pos;
        }
        return null;
    }

    private void fillBlacksmithChests(World world, BlockPos origin, Template template, Rotation rotation) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }
        BlockPos size = template.getSize();
        int maxX = Math.max(size.getX(), size.getZ()) + 4;
        int maxY = size.getY() + 4;
        int maxZ = Math.max(size.getX(), size.getZ()) + 4;
        for (int x = -2; x <= maxX; ++x) {
            for (int y = -2; y <= maxY; ++y) {
                for (int z = -2; z <= maxZ; ++z) {
                    BlockPos pos = origin.add(x, y, z);
                    TileEntity te = WorldAccess.getTileEntity(world, pos);
                    if (!(te instanceof TileEntityChest)) continue;
                    this.fillChest((WorldServer)world, (TileEntityChest)te, world.rand);
                }
            }
        }
    }

    private void fillChest(WorldServer world, TileEntityChest chest, Random random) {
        for (int i = 0; i < chest.getSizeInventory(); ++i) {
            chest.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        LootTable table = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_VILLAGE_BLACKSMITH);
        LootContext context = new LootContext.Builder(world).build();
        List loot = table.generateLootForPools(random, context);
        Collections.shuffle(loot, random);
        for (ItemStack stack : loot) {
            this.addRandomStack((IInventory)chest, stack, random);
        }
        int berries = 2 + random.nextInt(4);
        this.addRandomStack((IInventory)chest, new ItemStack(SRPItems.itemThornshadeBerry, berries), random);
    }

    private void addRandomStack(IInventory inventory, ItemStack stack, Random random) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        for (int tries = 0; tries < 80; ++tries) {
            int slot = random.nextInt(inventory.getSizeInventory());
            if (!inventory.getStackInSlot(slot).isEmpty()) continue;
            inventory.setInventorySlotContents(slot, stack.copy());
            return;
        }
    }

    private static final class Bounds {
        int minX;
        int maxX;
        int minZ;
        int maxZ;

        private Bounds() {
        }
    }

    private static final class VillageStyle {
        final int spreadPercent;
        final int optionalBias;
        final int extraVillagers;
        final int logPileBias;

        VillageStyle(int spreadPercent, int optionalBias, int extraVillagers, int logPileBias) {
            this.spreadPercent = spreadPercent;
            this.optionalBias = optionalBias;
            this.extraVillagers = extraVillagers;
            this.logPileBias = logPileBias;
        }
    }
}

