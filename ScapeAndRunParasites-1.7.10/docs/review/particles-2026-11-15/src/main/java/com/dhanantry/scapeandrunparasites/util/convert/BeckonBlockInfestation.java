/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.BlockFenceGate
 *  net.minecraft.block.BlockFurnace
 *  net.minecraft.block.BlockGlass
 *  net.minecraft.block.BlockGrassPath
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockPane
 *  net.minecraft.block.BlockPlanks
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.dhanantry.scapeandrunparasites.util.convert;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.block.IStagedBlock;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBush;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BeckonBlockInfestation {
    private static final Logger INFEST_LOG = LogManager.getLogger((String)"SRP-Infest");
    private static final boolean DEBUG = false;
    public static int blockInfestedCount;
    private static final HashMap<Material, IBlockState> conversionCache;
    private static final HashMap<BlockMetaKey, IBlockState> customConvertCache;
    private static int customConvertHash;

    public static void beckonInfestation(World worldIn, BlockPos pos, Random rand, int stage, boolean fromVenkrol) {
        int rangeY;
        int range;
        if (blockInfestedCount > SRPConfig.BlockInfestedLimit) {
            return;
        }
        if (DifficultyAccess.difficulty(worldIn) == EnumDifficulty.PEACEFUL) {
            return;
        }
        int pivot = 0;
        switch (stage) {
            case 1: {
                range = SRPConfigMobs.venkrolRange - 1;
                rangeY = SRPConfigMobs.venkrolRangeY;
                break;
            }
            case 2: {
                range = SRPConfigMobs.venkrolsiiRange - 1;
                rangeY = SRPConfigMobs.venkrolsiiRangeY;
                break;
            }
            default: {
                range = SRPConfigMobs.venkrolsiiiRange - 1;
                rangeY = SRPConfigMobs.venkrolsiiiRangeY;
            }
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), (double)range, (double)rangeY, (double)range);
        List moblist = EntityQueries.entities(worldIn, EntityParasiteBase.class, axisalignedbb);
        if (fromVenkrol) {
            if (moblist.isEmpty() && stage < 4) {
                BeckonBlockInfestation.spawnBeckonFromInfestation(worldIn, pos, stage);
                return;
            }
            if (!moblist.isEmpty() && stage < 4) {
                int flagV = 0;
                for (EntityParasiteBase mob : moblist) {
                    if (flagV == 2) break;
                    if (mob.isPotionActive(SRPPotions.PIVOT_E)) {
                        pivot = Math.max(mob.getActivePotionEffect(SRPPotions.PIVOT_E).getAmplifier() + 1, pivot);
                    }
                    flagV = 0;
                    BlockPos venPos = EntityQueries.position(mob);
                    if (worldIn.getLightBrightness(venPos.up()) < (float)SRPConfigSystems.rsBlockLight / 15.0f) {
                        ++flagV;
                    }
                    if (worldIn.getLightFor(EnumSkyBlock.BLOCK, venPos.up()) >= SRPConfigSystems.rsBlockLight) continue;
                    ++flagV;
                }
                if (flagV < 2) {
                    return;
                }
            }
        }
        int covertedBlocks = BeckonBlockInfestation.blockConversions(worldIn, pos, rand, stage);
        if (stage >= 4) {
            if (BlockStates.get(worldIn, pos).getBlock() == SRPBlocks.InfestedStain) {
                BlockStates.set(worldIn, pos, SRPBlocks.optionalDirt);
            } else if (BlockStates.get(worldIn, pos).getBlock() == SRPBlocks.InfestedRubble) {
                BlockStates.set(worldIn, pos, SRPBlocks.optionalRub);
            }
            if (BlockStates.get(worldIn, pos.up()).getBlock() == Blocks.air || BlockStates.get(worldIn, pos.up()).getBlock() == SRPBlocks.ParasiteBush) {
                BlockStates.set(worldIn, pos.up(), BlockStates.defaultState(SRPBlocks.InfestRemain));
            }
        } else if (covertedBlocks != 0) {
            blockInfestedCount += covertedBlocks;
            if (SRPConfigSystems.useEvolution) {
                if (pivot != 0) {
                    SRPSaveData.get(worldIn, 75).setTotalKills(worldIn.provider.dimensionId, SRPConfigSystems.valueBlock * covertedBlocks * (SRPConfigSystems.pivotPointMultiplier * pivot), true, worldIn, true, 53);
                } else {
                    SRPSaveData.get(worldIn, 74).setTotalKills(worldIn.provider.dimensionId, SRPConfigSystems.valueBlock * covertedBlocks, true, worldIn, true, 54);
                }
            }
        }
    }

    private static void generateInfestationFeatures(World worldIn, BlockPos pos, Random rand) {
        BeckonBlockInfestation.spawnGenFeatureInfested(worldIn, pos.up(), rand);
        BeckonBlockInfestation.spawnGenRoofInfested(worldIn, pos.down(), rand);
    }

    public static void spawnGenFeatureInfested(World worldIn, BlockPos pos, Random rand) {
        WorldGenParasiteBush gen;
        double bonus = 0.0;
        if (BlockStates.get(worldIn, pos).getBlock() != Blocks.air && BlockStates.get(worldIn, pos).getMaterial() == Material.water) {
            pos = ParasiteEventEntity.getFloor(worldIn, pos, 10);
            bonus = 3.0E-5;
            if (pos == null) {
                return;
            }
        }
        if (rand.nextInt(20) == 0) {
            gen = new WorldGenParasiteBush(false, BlockInfestedBush.EnumType.SPINE, 4);
            gen.generate(worldIn, rand, pos);
        }
        if (rand.nextInt(20) == 0) {
            if (rand.nextInt(2) == 0) {
                gen = new WorldGenParasiteBush(false, BlockInfestedBush.EnumType.ARC, 2);
                gen.generate(worldIn, rand, pos);
            } else {
                gen = new WorldGenParasiteBush(false, BlockInfestedBush.EnumType.GRASS1, 1);
                gen.generate(worldIn, rand, pos);
            }
        }
    }

    public static void spawnGenRoofInfested(World worldIn, BlockPos pos, Random rand) {
        if (BlockStates.get(worldIn, pos).getBlock() != Blocks.air) {
            return;
        }
        if (rand.nextInt(5) == 0) {
            BlockStates.set(worldIn, pos, BlockStates.defaultState(SRPBlocks.InfestedBush).withProperty(BlockInfestedBush.VARIANT, (Comparable)((Object)BlockInfestedBush.EnumType.VINE)));
            pos = pos.down();
            if (rand.nextInt(4) == 0) {
                if (BlockStates.get(worldIn, pos).getBlock() != Blocks.air) {
                    return;
                }
                BlockStates.set(worldIn, pos, BlockStates.defaultState(SRPBlocks.InfestedBush).withProperty(BlockInfestedBush.VARIANT, (Comparable)((Object)BlockInfestedBush.EnumType.VINE)));
                pos = pos.down();
                if (rand.nextInt(3) == 0) {
                    if (BlockStates.get(worldIn, pos).getBlock() != Blocks.air) {
                        return;
                    }
                    BlockStates.set(worldIn, pos, BlockStates.defaultState(SRPBlocks.InfestedBush).withProperty(BlockInfestedBush.VARIANT, (Comparable)((Object)BlockInfestedBush.EnumType.VINE)));
                }
            }
        }
    }

    private static void rebuildCustomConvertCacheIfNeeded() {
        String[] list = SRPConfigWorld.infestationConvertBlocks;
        int h = 1;
        if (list != null) {
            for (String s : list) {
                if (s == null) continue;
                h = 31 * h + s.hashCode();
            }
        }
        if (h == customConvertHash) {
            return;
        }
        customConvertHash = h;
        customConvertCache.clear();
        if (list == null) {
            return;
        }
        for (String entry : list) {
            String[] parts;
            if (entry == null || (entry = entry.trim()).isEmpty() || (parts = entry.split(";", 2)).length != 2) continue;
            String left = parts[0].trim();
            String right = parts[1].trim();
            ParsedState src = BeckonBlockInfestation.parseStateToken(left);
            ParsedState dst = BeckonBlockInfestation.parseStateToken(right);
            if (src == null || dst == null || dst.block == Blocks.air && !"minecraft:air".equals(right)) continue;
            customConvertCache.put(new BlockMetaKey(src.block, src.meta), dst.state);
        }
    }

    private static ParsedState parseStateToken(String token) {
        ResourceLocation rl;
        if (token == null || token.isEmpty()) {
            return null;
        }
        String id = token.trim();
        int meta = -1;
        String[] split = id.split(":");
        if (split.length == 3) {
            id = split[0] + ":" + split[1];
            try {
                meta = Integer.parseInt(split[2]);
            }
            catch (Exception e) {
                meta = -1;
            }
        }
        try {
            rl = new ResourceLocation(id);
        }
        catch (Exception e) {
            return null;
        }
        if (!Block.blockRegistry.containsKey((Object)rl)) {
            return null;
        }
        Block b = (Block)Block.blockRegistry.getObject((Object)rl);
        if (b == null) {
            return null;
        }
        if (b == Blocks.air && !"minecraft:air".equals(id)) {
            return null;
        }
        if (meta < 0 && b instanceof BlockLiquid) {
            meta = 0;
        }
        IBlockState st = meta >= 0 ? BlockStates.fromMeta(b, meta) : BlockStates.defaultState(b);
        return new ParsedState(b, meta, st);
    }

    private static IBlockState getCustomConvertedState(Block lookingBlock, IBlockState lookingState, int stage) {
        if (stage >= 4) {
            return null;
        }
        BeckonBlockInfestation.rebuildCustomConvertCacheIfNeeded();
        int meta = BlockStates.meta(lookingBlock, lookingState);
        IBlockState dst = customConvertCache.get(new BlockMetaKey(lookingBlock, meta));
        if (dst == null) {
            dst = customConvertCache.get(new BlockMetaKey(lookingBlock, -1));
        }
        if (dst == null) {
            return null;
        }
        return BeckonBlockInfestation.createStagedState(dst, stage);
    }

    private static IBlockState createStagedState(IBlockState baseState, int stage) {
        if (stage < 0) {
            stage = 0;
        }
        if (stage > 5) {
            stage = 5;
        }
        if (baseState.getBlock() instanceof IStagedBlock) {
            return ((IStagedBlock)baseState.getBlock()).withStage(baseState, stage);
        }
        return baseState;
    }

    private static int blockConversions(World worldIn, BlockPos pos, Random rand, int stage) {
        int convertedBlocks = 0;
        if (stage < 0) {
            stage = 0;
        }
        if (stage > 5) {
            stage = 5;
        }
        for (int dir = 0; dir <= 5; ++dir) {
            IBlockState custom;
            BlockPos helper = BlockParasiteSpreading.directionToSpread(pos, dir);
            IBlockState lookingState = BlockStates.get(worldIn, helper);
            Block lookingBlock = lookingState.getBlock();
            Material mat = lookingState.getMaterial();
            if (lookingBlock instanceof IMetaName || BeckonBlockInfestation.isSrpInfestedBlock(lookingBlock)) continue;
            if (stage < 4 && !BeckonBlockInfestation.isSrpBlock(lookingBlock) && (custom = BeckonBlockInfestation.getCustomConvertedState(lookingBlock, lookingState, stage)) != null) {
                BlockStates.set(worldIn, helper, custom, 3);
                ++convertedBlocks;
                BeckonBlockInfestation.generateInfestationFeatures(worldIn, helper, rand);
                continue;
            }
            if (stage <= 3) {
                IBlockState copy;
                IBlockState dst;
                if (BeckonBlockInfestation.isCobbleBlock(lookingBlock, lookingState)) {
                    ++convertedBlocks;
                    BlockStates.set(worldIn, helper, BeckonBlockInfestation.createStagedState(BlockStates.defaultState(SRPBlocks.InfestedCobblestone), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isPathBlock(lookingBlock)) {
                    ++convertedBlocks;
                    BlockStates.set(worldIn, helper, BeckonBlockInfestation.createStagedState(BlockStates.defaultState(SRPBlocks.InfestedSand), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isFenceBlock(lookingBlock)) {
                    ++convertedBlocks;
                    BlockStates.set(worldIn, helper, BeckonBlockInfestation.createStagedState(BlockStates.defaultState(SRPBlocks.InfestedFence), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isAnyPlanks(lookingBlock, lookingState)) {
                    ++convertedBlocks;
                    BlockStates.set(worldIn, helper, BeckonBlockInfestation.createStagedState(BlockStates.defaultState(SRPBlocks.InfestedPlanks), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isGlassBlock(lookingBlock, lookingState)) {
                    ++convertedBlocks;
                    BlockStates.set(worldIn, helper, BeckonBlockInfestation.createStagedState(BlockStates.defaultState(SRPBlocks.InfestedGlass), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isGlassPaneBlock(lookingBlock)) {
                    ++convertedBlocks;
                    BlockStates.set(worldIn, helper, BeckonBlockInfestation.createStagedState(BlockStates.defaultState(SRPBlocks.INFESTED_GLASS_PANE), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isFurnaceBlock(lookingBlock)) {
                    ++convertedBlocks;
                    BlockStates.set(worldIn, helper, BeckonBlockInfestation.createStagedState(BlockStates.defaultState(SRPBlocks.INFESTED_FURNACE), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isStoneStairs(lookingBlock, lookingState)) {
                    dst = BeckonBlockInfestation.createStagedState(BlockStates.defaultState(SRPBlocks.InfestedStoneStairs), stage);
                    copy = BeckonBlockInfestation.copyCommonProps(lookingState, dst);
                    ++convertedBlocks;
                    BlockStates.set(worldIn, helper, copy, 3);
                    continue;
                }
                if (BeckonBlockInfestation.isWoodStairs(lookingBlock, lookingState)) {
                    dst = BeckonBlockInfestation.createStagedState(BlockStates.defaultState(SRPBlocks.InfestedPlanksStairs), stage);
                    copy = BeckonBlockInfestation.copyCommonProps(lookingState, dst);
                    ++convertedBlocks;
                    BlockStates.set(worldIn, helper, copy, 3);
                    continue;
                }
                if (lookingBlock.isWood((IBlockAccess)worldIn, helper)) {
                    ++convertedBlocks;
                    BlockStates.set(worldIn, helper, BeckonBlockInfestation.createStagedState(BlockStates.defaultState(SRPBlocks.InfestedTrunk), stage), 3);
                    continue;
                }
            }
            if (ParasiteEventWorld.blockException(worldIn, helper, lookingBlock, lookingState, SRPConfigSystems.blockBList, SRPConfigSystems.blockBListWhite, SRPConfigSystems.rsBlockIMaxH) && stage <= 3) continue;
            if (lookingBlock instanceof BlockBase) {
                if (lookingBlock == SRPBlocks.BiomeHeart || lookingBlock == SRPBlocks.ColonyHeart || BeckonBlockInfestation.isSrpInfestedBlock(lookingBlock)) continue;
                int lookM = BlockStates.meta(lookingBlock, lookingState);
                if (!conversionCache.containsKey(mat)) continue;
                IBlockState newState = BeckonBlockInfestation.createStagedState(conversionCache.get(mat), stage);
                if (stage >= 4 && (lookM <= 1 || stage == 5)) {
                    BlockStates.set(worldIn, helper, newState, 3);
                    worldIn.updateBlockTick(helper, newState.getBlock(), 40, 5);
                    continue;
                }
                if (lookM >= stage || stage >= 4) continue;
                BlockStates.set(worldIn, helper, newState, 3);
                ++convertedBlocks;
                BeckonBlockInfestation.generateInfestationFeatures(worldIn, helper, rand);
                continue;
            }
            if (stage >= 4 || !conversionCache.containsKey(mat) || BeckonBlockInfestation.isSrpBlock(lookingBlock)) continue;
            IBlockState infestedState = BeckonBlockInfestation.createStagedState(conversionCache.get(mat), stage);
            BlockStates.set(worldIn, helper, infestedState, 3);
            ++convertedBlocks;
            BeckonBlockInfestation.generateInfestationFeatures(worldIn, helper, rand);
        }
        return convertedBlocks;
    }

    private static void spawnBeckonFromInfestation(World worldIn, BlockPos pos, int stage) {
        if (worldIn.rand.nextDouble() < SRPConfigSystems.rsVenkrolEmpty && stage != 1 && (BlockStates.get(worldIn, pos.up()).getBlock() == Blocks.air || BlockStates.get(worldIn, pos.up()).getBlock() instanceof BlockBush)) {
            if (worldIn.getLightBrightness(pos.up()) > 0.46666667f || worldIn.getLightFor(EnumSkyBlock.BLOCK, pos.up()) > 7) {
                return;
            }
            List serverList = EntityQueries.loaded(worldIn);
            int count = 0;
            for (Entity entity : serverList) {
                if (!(entity instanceof EntityPBeckon) || ++count <= SRPConfig.nexusVenkrolCap && !(BeckonBlockInfestation.getDistanceSq(pos, entity) < (double)(SRPConfig.nexusVenkrolDis * SRPConfig.nexusVenkrolDis))) continue;
                return;
            }
            EntityVenkrol out = new EntityVenkrol(worldIn);
            out.setLocationAndAngles((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, 0.0f, 0.0f);
            worldIn.spawnEntityInWorld((Entity)out);
            out.cannotDespawn(true);
        }
    }

    private static double getDistanceSq(BlockPos pos, Entity entityIn) {
        double d0 = (double)pos.getX() - entityIn.posX;
        double d1 = (double)pos.getY() - entityIn.posY;
        double d2 = (double)pos.getZ() - entityIn.posZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    private static boolean isSrpInfestedBlock(Block b) {
        ResourceLocation rl = RegistryNames.get(b);
        if (rl == null) {
            return false;
        }
        if (!"srparasites".equals(rl.getResourceDomain())) {
            return false;
        }
        String path = rl.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("infest");
    }

    private static boolean isSrpBlock(Block b) {
        ResourceLocation rl = RegistryNames.get(b);
        return rl != null && "srparasites".equals(rl.getResourceDomain());
    }

    private static boolean isPathBlock(Block b) {
        if (b == Blocks.GRASS_PATH || b instanceof BlockGrassPath) {
            return true;
        }
        ResourceLocation rl = RegistryNames.get(b);
        if (rl == null) {
            return false;
        }
        String path = rl.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("path");
    }

    private static boolean isFenceBlock(Block b) {
        if (b instanceof BlockFence) {
            return true;
        }
        if (b instanceof BlockFenceGate) {
            return false;
        }
        ResourceLocation rl = RegistryNames.get(b);
        if (rl == null) {
            return false;
        }
        String path = rl.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("fence") && !path.contains("gate");
    }

    private static boolean isAnyPlanks(Block b, IBlockState s) {
        if (b instanceof BlockPlanks) {
            return true;
        }
        ResourceLocation rl = RegistryNames.get(b);
        if (rl == null) {
            return false;
        }
        String path = rl.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("planks") || path.endsWith("_plank") || path.contains("wood_plank");
    }

    private static boolean isGlassBlock(Block b, IBlockState s) {
        if (b instanceof BlockGlass) {
            return true;
        }
        if (s.getMaterial() == Material.glass && !(b instanceof BlockPane)) {
            return true;
        }
        ResourceLocation rl = RegistryNames.get(b);
        if (rl == null) {
            return false;
        }
        String path = rl.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("glass") && !path.contains("pane");
    }

    private static boolean isGlassPaneBlock(Block b) {
        if (b instanceof BlockPane) {
            return true;
        }
        ResourceLocation rl = RegistryNames.get(b);
        if (rl == null) {
            return false;
        }
        String path = rl.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("pane");
    }

    private static boolean isFurnaceBlock(Block b) {
        return b == Blocks.furnace || b == Blocks.lit_furnace || b instanceof BlockFurnace;
    }

    private static boolean isStoneStairs(Block b, IBlockState s) {
        if (b instanceof BlockStairs && s.getMaterial() == Material.rock) {
            return true;
        }
        ResourceLocation rl = RegistryNames.get(b);
        if (rl == null) {
            return false;
        }
        String path = rl.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("stairs") && (path.contains("stone") || path.contains("brick") || path.contains("sandstone"));
    }

    private static boolean isWoodStairs(Block b, IBlockState s) {
        if (b instanceof BlockStairs && s.getMaterial() == Material.wood) {
            return true;
        }
        ResourceLocation rl = RegistryNames.get(b);
        if (rl == null) {
            return false;
        }
        String path = rl.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("stairs") && (path.contains("oak") || path.contains("spruce") || path.contains("birch") || path.contains("jungle") || path.contains("acacia") || path.contains("dark_oak") || path.contains("wood"));
    }

    private static IBlockState copyCommonProps(IBlockState from, IBlockState to) {
        IBlockState out = to;
        for (IProperty propTo : to.getPropertyKeys()) {
            if (!from.getPropertyKeys().contains(propTo)) continue;
            try {
                IProperty raw = propTo;
                Comparable val = from.getValue(raw);
                if (val == null || !raw.getAllowedValues().contains(val)) continue;
                out = out.withProperty(raw, val);
            }
            catch (Exception exception) {}
        }
        return out;
    }

    private static boolean isCobbleBlock(Block b, IBlockState s) {
        if (b == Blocks.cobblestone) {
            return true;
        }
        try {
            if (b == Blocks.mossy_cobblestone) {
                return true;
            }
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        ResourceLocation rl = RegistryNames.get(b);
        if (rl == null) {
            return false;
        }
        String path = rl.getResourcePath().toLowerCase(Locale.ROOT);
        return path.contains("cobblestone") && !path.contains("wall") && !path.contains("stairs");
    }

    static {
        conversionCache = new HashMap();
        conversionCache.put(Material.ground, BlockStates.defaultState(SRPBlocks.InfestedStain));
        conversionCache.put(Material.grass, BlockStates.defaultState(SRPBlocks.InfestedStain));
        conversionCache.put(Material.sand, BlockStates.defaultState(SRPBlocks.InfestedSand));
        conversionCache.put(Material.rock, BlockStates.defaultState(SRPBlocks.InfestedRubble));
        customConvertCache = new HashMap();
        customConvertHash = 0;
    }

    private static class ParsedState {
        private final Block block;
        private final int meta;
        private final IBlockState state;

        private ParsedState(Block block, int meta, IBlockState state) {
            this.block = block;
            this.meta = meta;
            this.state = state;
        }
    }

    private static class BlockMetaKey {
        private final Block block;
        private final int meta;

        private BlockMetaKey(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof BlockMetaKey)) {
                return false;
            }
            BlockMetaKey other = (BlockMetaKey)obj;
            return this.block == other.block && this.meta == other.meta;
        }

        public int hashCode() {
            return System.identityHashCode(this.block) * 31 + this.meta;
        }
    }
}

