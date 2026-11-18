/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.gen.feature.WorldGenerator
 *  net.minecraft.world.gen.structure.template.PlacementSettings
 *  net.minecraft.world.gen.structure.template.Template
 *  net.minecraft.world.gen.structure.template.TemplateManager
 *  net.minecraftforge.fml.common.FMLCommonHandler
 */
package com.dhanantry.scapeandrunparasites.world.gen.structure;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import java.util.Random;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.util.Mirror;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.util.Rotation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import cpw.mods.fml.common.FMLCommonHandler;

public class WorldGenStructure
extends WorldGenerator {
    public static String structureName;

    public WorldGenStructure(String name) {
        structureName = name;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position) {
        WorldGenStructure.generate(worldIn, position);
        return true;
    }

    public static void generate(World world, BlockPos pos) {
        ResourceLocation location;
        MinecraftServer mcServer = world.getMinecraftServer();
        WorldServer worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
        TemplateManager manager = worldServer.getStructureTemplateManager();
        Template template = manager.get(mcServer, location = new ResourceLocation("srparasites", structureName));
        if (template != null) {
            IBlockState state = BlockStates.get(world, pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            PlacementSettings setting = new PlacementSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE);
            template.addBlocksToWorldChunk(world, pos, setting);
        }
    }
}

