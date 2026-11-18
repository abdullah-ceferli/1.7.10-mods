/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockCarpet
 *  net.minecraft.block.BlockCrops
 *  net.minecraft.block.BlockDoublePlant
 *  net.minecraft.block.BlockFence
 *  net.minecraft.block.BlockFlower
 *  net.minecraft.block.BlockGlass
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockLog
 *  net.minecraft.block.BlockPane
 *  net.minecraft.block.BlockPlanks
 *  net.minecraft.block.BlockReed
 *  net.minecraft.block.BlockSandStone
 *  net.minecraft.block.BlockSandStone$EnumType
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.BlockStem
 *  net.minecraft.block.BlockVine
 *  net.minecraft.block.BlockWall
 *  net.minecraft.block.IGrowable
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.common.registry.ForgeRegistries
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockThornshade;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWall;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.registry.ForgeRegistries;

public class ItemInfestedBonemeal
extends Item {
    private static final ResourceLocation RL_INFESTEDBUSH = new ResourceLocation("srparasites", "infestedbush");
    private static final ResourceLocation RL_PARASITEBUSH = new ResourceLocation("srparasites", "parasitebush");
    private static final ResourceLocation RL_TRESSES = new ResourceLocation("srparasites", "tresses_hair");
    private static final ResourceLocation RL_HIRSUTE = new ResourceLocation("srparasites", "hirsute_hair");
    private static final ResourceLocation RL_LIPOMA = new ResourceLocation("srparasites", "lipoma_mass");
    private static final ResourceLocation RL_INFESTED_STAIN = new ResourceLocation("srparasites", "infestedstain");
    private static final ResourceLocation RL_INFESTED_SAND = new ResourceLocation("srparasites", "infestedsand");
    private static final ResourceLocation RL_INFESTED_TRUNK = new ResourceLocation("srparasites", "infestedtrunk");
    private static final ResourceLocation RL_INFESTED_PLANKS = new ResourceLocation("srparasites", "infested_planks");
    private static final ResourceLocation RL_INFESTED_LEAVES = new ResourceLocation("srparasites", "infested_leaves");
    private static final ResourceLocation RL_INFESTED_STONEBRICK = new ResourceLocation("srparasites", "infested_stone_bricks");
    private static final ResourceLocation RL_INFESTED_WALL = new ResourceLocation("srparasites", "infestedrubble_wall");
    private static final ResourceLocation RL_INFESTED_FENCE = new ResourceLocation("srparasites", "infested_fence");
    private static final ResourceLocation RL_INFESTED_GLASS = new ResourceLocation("srparasites", "infested_glass");
    private static final ResourceLocation RL_INFESTED_GLASS_PANE = new ResourceLocation("srparasites", "infested_glass_pane");
    private static final ResourceLocation RL_INFESTED_RUBBLE = new ResourceLocation("srparasites", "infestedrubble");
    private static final ResourceLocation RL_INFESTED_COBBLE = new ResourceLocation("srparasites", "infested_cobblestone");
    private static final ResourceLocation RL_INFESTED_PLANKS_STAIRS = new ResourceLocation("srparasites", "infested_planks_stairs");
    private static final ResourceLocation RL_INFESTED_STONE_STAIRS = new ResourceLocation("srparasites", "infested_stone_stairs");
    private static final ResourceLocation RL_INFESTED_SANDSTONE_STAIRS = new ResourceLocation("srparasites", "infested_sandstone_stairs");
    private static final ResourceLocation RL_INFESTED_REMAIN = new ResourceLocation("srparasites", "infestedremain");
    private static final ResourceLocation RL_INF_SS = new ResourceLocation("srparasites", "inf_ss");
    private static final ResourceLocation RL_INF_SS_CHISELED = new ResourceLocation("srparasites", "inf_ss_chiseled");
    private static final ResourceLocation RL_PARASITE_RUBBLE = new ResourceLocation("srparasites", "parasiterubble");
    private static final ResourceLocation RL_INFESTED_ORE = new ResourceLocation("srparasites", "infestedore");
    private static final int RADIUS = 3;
    private static final int SAFE_SET_FLAGS = 18;

    public ItemInfestedBonemeal(String name) {
        RegistryNames.set(this, "srparasites", name);
        this.setUnlocalizedName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }

    public static boolean boneMealEffect(World world, BlockPos pos, EnumFacing facing) {
        boolean any = false;
        Random r = world.rand;
        int r2 = 9;
        for (int dx = -3; dx <= 3; ++dx) {
            for (int dy = -3; dy <= 3; ++dy) {
                for (int dz = -3; dz <= 3; ++dz) {
                    IBlockState lip;
                    String ap;
                    IBlockState s;
                    Block b;
                    BlockPos p;
                    if (dx * dx + dy * dy + dz * dz > r2 || WorldAccess.getTileEntity(world, p = pos.add(dx, dy, dz)) != null || ItemInfestedBonemeal.isLiquid(world, p) || ItemInfestedBonemeal.hasPendingTick(world, p, b = (s = BlockStates.get(world, p)).getBlock()) || b == Blocks.air) continue;
                    ResourceLocation key = (ResourceLocation)Block.blockRegistry.getNameForObject((Object)b);
                    String domain = key.getResourceDomain();
                    String path = key.getResourcePath();
                    boolean converted = false;
                    if (b == Blocks.lit_redstone_ore) {
                        BlockStates.set(world, p, BlockStates.defaultState(Blocks.redstone_ore), 18);
                        ((WorldServer)world).addScheduledTask(() -> ItemInfestedBonemeal.setBlockMeta(world, p, RL_INFESTED_ORE, 6));
                        ItemInfestedBonemeal.smoke(world, p);
                        any = true;
                        continue;
                    }
                    Integer oreMeta = ItemInfestedBonemeal.oreMetaFor(b, path);
                    if (oreMeta != null) {
                        converted = ItemInfestedBonemeal.setBlockMeta(world, p, RL_INFESTED_ORE, oreMeta);
                    }
                    if (!converted && (ItemInfestedBonemeal.isFlower(b, path) || ItemInfestedBonemeal.isVine(b, path) || ItemInfestedBonemeal.isCrop(b, path))) {
                        converted = ItemInfestedBonemeal.setAir(world, p);
                    }
                    if (!converted) {
                        if (ItemInfestedBonemeal.isGrassPath(b, path)) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_SAND);
                        } else if (b == Blocks.cobblestone || path.contains("cobblestone")) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_COBBLE);
                        } else if (b == Blocks.farmland || path.contains("farmland")) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_SAND);
                        } else if (ItemInfestedBonemeal.isWool(b, path)) {
                            converted = ItemInfestedBonemeal.setBlockMeta(world, p, RL_PARASITE_RUBBLE, 8);
                        } else if (b == Blocks.sandstone || path.contains("sandstone")) {
                            converted = ItemInfestedBonemeal.isChiseledSandstone(s, b, path) ? ItemInfestedBonemeal.setBlock(world, p, RL_INF_SS_CHISELED) : ItemInfestedBonemeal.setBlock(world, p, RL_INF_SS);
                        } else if (ItemInfestedBonemeal.isSandstoneStairs(b, path)) {
                            converted = ItemInfestedBonemeal.setStairs(world, p, s, RL_INFESTED_SANDSTONE_STAIRS);
                        } else if (ItemInfestedBonemeal.isWoodStairs(b, s, path)) {
                            converted = ItemInfestedBonemeal.setStairs(world, p, s, RL_INFESTED_PLANKS_STAIRS);
                        } else if (ItemInfestedBonemeal.isStoneStairs(b, s, path)) {
                            converted = ItemInfestedBonemeal.setStairs(world, p, s, RL_INFESTED_STONE_STAIRS);
                        } else if (b == Blocks.stone || path.equals("stone")) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_RUBBLE);
                        } else if (b == Blocks.dirt || path.contains("dirt")) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_STAIN);
                        } else if (b == Blocks.grass) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_STAIN);
                        } else if (b == Blocks.sand) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_SAND);
                        } else if (ItemInfestedBonemeal.isAnyLog(b, path)) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_TRUNK);
                        } else if (ItemInfestedBonemeal.isAnyPlanks(b, path)) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_PLANKS);
                        } else if (ItemInfestedBonemeal.isAnyLeaves(b, path)) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_LEAVES);
                        } else if (ItemInfestedBonemeal.isAnyStoneBrick(b, path)) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_STONEBRICK);
                        } else if (ItemInfestedBonemeal.isAnyWall(b, path)) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_WALL);
                        } else if (ItemInfestedBonemeal.isAnyFence(b, path)) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_FENCE);
                        } else if (ItemInfestedBonemeal.isAnyGlassPane(b, path)) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_GLASS_PANE);
                        } else if (ItemInfestedBonemeal.isAnyGlassBlock(b, path)) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_GLASS);
                        } else if (b instanceof BlockCarpet || path.contains("carpet")) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_REMAIN);
                        } else if (b == Blocks.clay || path.contains("clay")) {
                            converted = ItemInfestedBonemeal.setBlock(world, p, RL_INFESTED_SAND);
                        }
                    }
                    if (converted) {
                        ItemInfestedBonemeal.smoke(world, p);
                        any = true;
                    }
                    s = BlockStates.get(world, p);
                    b = s.getBlock();
                    key = (ResourceLocation)Block.blockRegistry.getNameForObject((Object)b);
                    domain = key.getResourceDomain();
                    path = key.getResourcePath();
                    if ("srparasites".equals(domain)) {
                        BlockPos up;
                        if (path.startsWith("infested") && !path.equals("infestedsand")) {
                            up = p.up();
                            IBlockState grow = ItemInfestedBonemeal.pickInfestedPlant(r);
                            if (grow != null && ItemInfestedBonemeal.canPlaceClean(world, up, grow) && BlockStates.set(world, up, grow, 18)) {
                                ItemInfestedBonemeal.smoke(world, up);
                                any = true;
                            }
                        }
                        if (path.startsWith("harleskinn") || path.startsWith("poland_skin_block")) {
                            if (facing == EnumFacing.DOWN) {
                                BlockPos under = p.down();
                                IBlockState lip2 = ItemInfestedBonemeal.stateOf(RL_LIPOMA, 0);
                                if (lip2 != null && ItemInfestedBonemeal.canPlaceClean(world, under, lip2) && BlockStates.set(world, under, lip2, 18)) {
                                    ItemInfestedBonemeal.smoke(world, under);
                                    any = true;
                                }
                            } else {
                                IBlockState hair;
                                up = p.up();
                                IBlockState iBlockState = hair = r.nextBoolean() ? ItemInfestedBonemeal.stateOf(RL_TRESSES, 0) : ItemInfestedBonemeal.stateOf(RL_HIRSUTE, 0);
                                if (hair != null && ItemInfestedBonemeal.canPlaceClean(world, up, hair) && BlockStates.set(world, up, hair, 18)) {
                                    ItemInfestedBonemeal.smoke(world, up);
                                    any = true;
                                }
                            }
                        }
                    }
                    if (any) continue;
                    IBlockState above = BlockStates.get(world, p.up());
                    Block ab = above.getBlock();
                    ResourceLocation ak = (ResourceLocation)Block.blockRegistry.getNameForObject((Object)ab);
                    String ad = ak == null ? "" : ak.getResourceDomain();
                    String string = ap = ak == null ? "" : ak.getResourcePath();
                    if (!"srparasites".equals(ad) || !ap.startsWith("harleskinn") && !ap.startsWith("poland_skin_block") || (lip = ItemInfestedBonemeal.stateOf(RL_LIPOMA, 0)) == null || !ItemInfestedBonemeal.canPlaceClean(world, p, lip) || !BlockStates.set(world, p, lip, 18)) continue;
                    ItemInfestedBonemeal.smoke(world, p);
                    any = true;
                }
            }
        }
        return any;
    }

    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        IBlockState clickedState = BlockStates.get(world, pos);
        Block clickedBlock = clickedState.getBlock();
        if (clickedBlock instanceof BlockThornshade) {
            BlockThornshade thorn = (BlockThornshade)clickedBlock;
            int hook = ForgeEventFactory.onApplyBonemeal((EntityPlayer)player, (World)world, (BlockPos)pos, (IBlockState)clickedState, (ItemStack)stack, (EnumHand)hand);
            if (hook > 0) {
                ItemInfestedBonemeal.spawnSaplingUseParticles(world, pos, world.rand);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                return EnumActionResult.SUCCESS;
            }
            if (hook < 0) {
                return EnumActionResult.PASS;
            }
            if (thorn.canGrow(world, pos, clickedState, false)) {
                thorn.grow(world, world.rand, pos, clickedState);
                ItemInfestedBonemeal.spawnSaplingUseParticles(world, pos, world.rand);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.PASS;
        }
        if (clickedBlock == SRPBlocks.ParasiteSapling) {
            IGrowable growable;
            int hook = ForgeEventFactory.onApplyBonemeal((EntityPlayer)player, (World)world, (BlockPos)pos, (IBlockState)clickedState, (ItemStack)stack, (EnumHand)hand);
            if (hook > 0) {
                ItemInfestedBonemeal.spawnSaplingUseParticles(world, pos, world.rand);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                return EnumActionResult.SUCCESS;
            }
            if (hook < 0) {
                return EnumActionResult.PASS;
            }
            if (clickedBlock instanceof IGrowable && (growable = (IGrowable)clickedBlock).canGrow(world, pos, clickedState, world.isRemote)) {
                if (!world.isRemote) {
                    if (growable.canUseBonemeal(world, world.rand, pos, clickedState)) {
                        growable.grow(world, world.rand, pos, clickedState);
                        ItemInfestedBonemeal.spawnSaplingUseParticles(world, pos, world.rand);
                    }
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                }
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.PASS;
        }
        boolean any = ItemInfestedBonemeal.boneMealEffect(world, pos, facing);
        if (any && !player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return any ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    private static void spawnSaplingUseParticles(World world, BlockPos pos, Random rand) {
        double oz;
        double oy;
        double ox;
        int i;
        if (world.isRemote) {
            return;
        }
        WorldServer ws = (WorldServer)world;
        double x = (double)pos.getX() + 0.5;
        double y = (double)pos.getY() + 0.6;
        double z = (double)pos.getZ() + 0.5;
        for (i = 0; i < 4; ++i) {
            ox = (rand.nextDouble() - 0.5) * 0.4;
            oy = rand.nextDouble() * 0.2 + 0.05;
            oz = (rand.nextDouble() - 0.5) * 0.4;
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(ws, EnumParticleTypes.SMOKE_LARGE, x + ox, y, z + oz, 1, 0.0, 0.02, 0.0, 0.0, new int[0]);
        }
        for (i = 0; i < 6; ++i) {
            ox = (rand.nextDouble() - 0.5) * 0.6;
            oy = rand.nextDouble() * 0.3 + 0.1;
            oz = (rand.nextDouble() - 0.5) * 0.6;
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(ws, EnumParticleTypes.VILLAGER_HAPPY, x + ox, y, z + oz, 1, 0.0, 0.02, 0.0, 0.0, new int[0]);
        }
    }

    private static boolean isAnyLog(Block b, String path) {
        return b instanceof BlockLog || path.contains("log");
    }

    private static boolean isAnyPlanks(Block b, String path) {
        return b instanceof BlockPlanks || path.contains("plank");
    }

    private static boolean isAnyLeaves(Block b, String path) {
        return b instanceof BlockLeaves || path.contains("leaves") || path.contains("leaf");
    }

    private static boolean isAnyStoneBrick(Block b, String path) {
        return path.contains("stonebrick") || path.contains("stone_brick");
    }

    private static boolean isAnyWall(Block b, String path) {
        return b instanceof BlockWall || path.endsWith("_wall") || path.contains("wall");
    }

    private static boolean isAnyFence(Block b, String path) {
        return b instanceof BlockFence || path.endsWith("_fence") || path.contains("fence");
    }

    private static boolean isAnyGlassBlock(Block b, String path) {
        return b instanceof BlockGlass && !(b instanceof BlockPane) || path.contains("glass") && !path.contains("pane");
    }

    private static boolean isAnyGlassPane(Block b, String path) {
        return b instanceof BlockPane || path.contains("pane");
    }

    private static boolean isSandstoneStairs(Block b, String path) {
        return b instanceof BlockStairs && path.contains("sandstone") && path.contains("stairs");
    }

    private static boolean isWoodStairs(Block b, IBlockState s, String path) {
        return b instanceof BlockStairs && s.getMaterial() == Material.wood;
    }

    private static boolean isStoneStairs(Block b, IBlockState s, String path) {
        if (!(b instanceof BlockStairs)) {
            return false;
        }
        if (ItemInfestedBonemeal.isSandstoneStairs(b, path)) {
            return false;
        }
        return s.getMaterial() == Material.rock;
    }

    private static boolean isChiseledSandstone(IBlockState s, Block b, String path) {
        if (b == Blocks.sandstone && s.getProperties().containsKey((Object)BlockSandStone.TYPE)) {
            return s.getValue((IProperty)BlockSandStone.TYPE) == BlockSandStone.EnumType.CHISELED;
        }
        return path.contains("chiseled") && path.contains("sandstone");
    }

    private static boolean isFlower(Block b, String path) {
        return b instanceof BlockFlower || b instanceof BlockDoublePlant || path.contains("flower");
    }

    private static boolean isVine(Block b, String path) {
        return b instanceof BlockVine || path.contains("vine");
    }

    private static boolean isCrop(Block b, String path) {
        return b instanceof BlockCrops || b instanceof BlockStem || b instanceof BlockReed || path.contains("wart");
    }

    private static boolean isGrassPath(Block b, String path) {
        return b == Blocks.GRASS_PATH || path.contains("grass_path") || path.endsWith("_path");
    }

    private static boolean isWool(Block b, String path) {
        return b == Blocks.wool || path.contains("wool");
    }

    private static Integer oreMetaFor(Block b, String path) {
        if (b == Blocks.coal_ore) {
            return 0;
        }
        if (b == Blocks.diamond_ore) {
            return 1;
        }
        if (b == Blocks.emerald_ore) {
            return 2;
        }
        if (b == Blocks.gold_ore) {
            return 3;
        }
        if (b == Blocks.iron_ore) {
            return 4;
        }
        if (b == Blocks.lapis_ore) {
            return 5;
        }
        if (b == Blocks.redstone_ore) {
            return 6;
        }
        if (path != null && path.contains("ore")) {
            return 7;
        }
        return null;
    }

    private static boolean setAir(World w, BlockPos p) {
        return BlockStates.set(w, p, BlockStates.defaultState(Blocks.air), 18);
    }

    private static boolean setBlock(World w, BlockPos p, ResourceLocation rl) {
        IBlockState st = ItemInfestedBonemeal.stateOf(rl, 0);
        return st != null && BlockStates.set(w, p, st, 18);
    }

    private static boolean setBlockMeta(World w, BlockPos p, ResourceLocation rl, int meta) {
        IBlockState st = ItemInfestedBonemeal.stateOf(rl, meta);
        return st != null && BlockStates.set(w, p, st, 18);
    }

    private static boolean hasPendingTick(World world, BlockPos p, Block b) {
        return world instanceof WorldServer && ((WorldServer)world).isBlockTickPending(p, b);
    }

    private static boolean setStairs(World w, BlockPos p, IBlockState srcStairsState, ResourceLocation targetRL) {
        IBlockState tgt = ItemInfestedBonemeal.stateOf(targetRL, 0);
        if (tgt == null) {
            return false;
        }
        tgt = ItemInfestedBonemeal.copyStairProps(srcStairsState, tgt);
        return BlockStates.set(w, p, tgt, 18);
    }

    private static IBlockState copyStairProps(IBlockState from, IBlockState to) {
        to = ItemInfestedBonemeal.copyPropByName(from, to, "facing");
        to = ItemInfestedBonemeal.copyPropByName(from, to, "half");
        to = ItemInfestedBonemeal.copyPropByName(from, to, "shape");
        return to;
    }

    private static IBlockState copyPropByName(IBlockState from, IBlockState to, String propName) {
        IProperty<?> src = ItemInfestedBonemeal.findProp(from, propName);
        IProperty<?> tgt = ItemInfestedBonemeal.findProp(to, propName);
        if (src == null || tgt == null) {
            return to;
        }
        Comparable val = from.getValue(src);
        String name = src.getName(val);
        for (Object cand : tgt.getAllowedValues()) {
            if (!tgt.getName((Comparable)cand).equals(name)) continue;
            to = to.withProperty(tgt, (Comparable)cand);
            break;
        }
        return to;
    }

    private static IProperty<?> findProp(IBlockState st, String name) {
        for (IProperty p : st.getPropertyKeys()) {
            if (!p.getName().equals(name)) continue;
            return p;
        }
        return null;
    }

    private static boolean canPlaceClean(World w, BlockPos p, IBlockState stateToPlace) {
        if (!ItemInfestedBonemeal.canReplace(w, p)) {
            return false;
        }
        if (ItemInfestedBonemeal.isLiquid(w, p)) {
            return false;
        }
        return stateToPlace.getBlock().canPlaceBlockAt(w, p);
    }

    private static boolean canReplace(World w, BlockPos p) {
        IBlockState s = BlockStates.get(w, p);
        return WorldAccess.isAirBlock(w, p) || s.getBlock().isReplaceable((IBlockAccess)w, p);
    }

    private static boolean isLiquid(World w, BlockPos p) {
        return BlockStates.get(w, p).getMaterial().isLiquid();
    }

    private static void smoke(World w, BlockPos p) {
        if (w instanceof WorldServer) {
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)w), EnumParticleTypes.SMOKE_LARGE, (double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5, 6, 0.3, 0.3, 0.3, 0.01, new int[0]);
        }
    }

    private static IBlockState stateOf(ResourceLocation rl, int meta) {
        Block b = (Block)ForgeRegistries.BLOCKS.getValue(rl);
        return b == null ? null : BlockStates.fromMeta(b, meta);
    }

    private static IBlockState pickInfestedPlant(Random r) {
        switch (r.nextInt(5)) {
            case 0: {
                return ItemInfestedBonemeal.stateOf(RL_INFESTEDBUSH, 0);
            }
            case 1: {
                return ItemInfestedBonemeal.stateOf(RL_INFESTEDBUSH, 1);
            }
            case 2: {
                return ItemInfestedBonemeal.stateOf(RL_INFESTEDBUSH, 2);
            }
            case 3: {
                return ItemInfestedBonemeal.stateOf(RL_INFESTEDBUSH, 5);
            }
        }
        return ItemInfestedBonemeal.stateOf(RL_PARASITEBUSH, 4);
    }
}

