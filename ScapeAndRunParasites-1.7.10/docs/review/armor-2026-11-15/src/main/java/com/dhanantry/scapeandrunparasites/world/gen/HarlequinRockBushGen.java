/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.chunk.IChunkProvider
 *  net.minecraft.world.gen.IChunkGenerator
 *  net.minecraft.world.gen.structure.template.PlacementSettings
 *  net.minecraft.world.gen.structure.template.Template
 *  net.minecraft.world.gen.structure.template.TemplateManager
 *  net.minecraftforge.fml.common.IWorldGenerator
 */
package com.dhanantry.scapeandrunparasites.world.gen;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import java.util.Random;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.util.Mirror;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.util.Rotation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import cpw.mods.fml.common.IWorldGenerator;

public class HarlequinRockBushGen
implements IWorldGenerator {
    private static final String[] ROCKS = new String[]{"harlequin_rock_01", "harlequin_rock_02", "harlequin_rock_03", "harlequin_rock_04", "harlequin_rock_05", "harlequin_rock_06", "harlequin_rock_07"};
    private static final String[] BUSHES = new String[]{"harlequin_bush_00", "harlequin_bush_01", "harlequin_bush_02"};
    private static final int ROCKS_CHANCE_PER_CHUNK = 10;
    private static final int BUSH_CLUSTER_CHANCE = 8;
    private static final int BUSH_CLUSTER_MIN = 3;
    private static final int BUSH_CLUSTER_MAX = 6;
    private static final int BUSH_CLUSTER_RADIUS = 7;
    private static final int MAX_SLOPE = 3;

    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        int bz;
        if (world.isRemote) {
            return;
        }
        WorldServer ws = (WorldServer)world;
        if (ws.provider.dimensionId != 0) {
            return;
        }
        int bx = (chunkX << 4) + 8 + rand.nextInt(6) - 3;
        BlockPos base = new BlockPos(bx, WorldAccess.getHeight(ws, new BlockPos(bx, 0, bz = (chunkZ << 4) + 8 + rand.nextInt(6) - 3)).getY(), bz);
        if (ws.getBiome(base) != SRPBiomes.biomeHarlequin) {
            return;
        }
        if (rand.nextInt(10) == 0) {
            this.tryPlaceTemplateSurface(ws, base, ROCKS[rand.nextInt(ROCKS.length)], rand);
        }
        if (rand.nextInt(8) == 0) {
            int count = 3 + rand.nextInt(4);
            BlockPos anchor = this.findSurface(ws, base);
            if (anchor != null) {
                for (int i = 0; i < count; ++i) {
                    BlockPos around = anchor.add(rand.nextInt(15) - 7, 0, rand.nextInt(15) - 7);
                    this.tryPlaceTemplateSurface(ws, around, BUSHES[rand.nextInt(BUSHES.length)], rand);
                }
            }
        }
    }

    private void tryPlaceTemplateSurface(WorldServer ws, BlockPos near, String name, Random rand) {
        BlockPos surface = this.findSurface(ws, near);
        if (surface == null) {
            return;
        }
        if (!this.flatEnough(ws, surface, 7, 3)) {
            return;
        }
        int sink = name.startsWith("harlequin_rock_") ? rand.nextInt(2) : 0;
        BlockPos placeAt = surface.down(sink);
        Rotation rot = Rotation.values()[rand.nextInt(Rotation.values().length)];
        Mirror mir = rand.nextBoolean() ? Mirror.NONE : Mirror.FRONT_BACK;
        Template tpl = this.getTemplate(ws, new ResourceLocation("srparasites", name));
        if (tpl == null) {
            return;
        }
        PlacementSettings settings = new PlacementSettings().setRotation(rot).setMirror(mir).setIgnoreEntities(true).setIgnoreStructureBlock(true);
        tpl.addBlocksToWorld((World)ws, placeAt, settings);
    }

    private BlockPos findSurface(WorldServer ws, BlockPos xz) {
        int y = WorldAccess.getHeight(ws, xz).getY();
        BlockPos pos = new BlockPos(xz.getX(), y, xz.getZ());
        Block below = BlockStates.get(ws, pos.down()).getBlock();
        if (!this.isGoodGround(below)) {
            return null;
        }
        if (this.isBadTop(ws, pos)) {
            return null;
        }
        return pos;
    }

    private boolean isGoodGround(Block b) {
        String id;
        if (b == Blocks.sand || b == Blocks.sandstone || b == Blocks.RED_SANDSTONE || b == Blocks.grass || b == Blocks.dirt) {
            return true;
        }
        return RegistryNames.get(b) != null && ((id = RegistryNames.get(b).toString()).equals("srparasites:harlequinn_grass") || id.equals("srparasites:harleskinn_block"));
    }

    private boolean isBadTop(WorldServer ws, BlockPos pos) {
        IBlockState state = BlockStates.get(ws, pos);
        Block block = state.getBlock();
        if (block == Blocks.water || block == Blocks.flowing_water || block == Blocks.lava || block == Blocks.flowing_lava) {
            return true;
        }
        return block.isLeaves(state, (IBlockAccess)ws, pos);
    }

    private boolean flatEnough(WorldServer ws, BlockPos center, int radius, int maxDelta) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int dx = -radius; dx <= radius; dx += radius) {
            for (int dz = -radius; dz <= radius; dz += radius) {
                int y = WorldAccess.getHeight(ws, center.add(dx, 0, dz)).getY();
                min = Math.min(min, y);
                max = Math.max(max, y);
            }
        }
        return max - min <= maxDelta;
    }

    private Template getTemplate(WorldServer ws, ResourceLocation rl) {
        TemplateManager mgr = ws.getSaveHandler().getStructureTemplateManager();
        return mgr.getTemplate(ws.getMinecraftServer(), rl);
    }
}

