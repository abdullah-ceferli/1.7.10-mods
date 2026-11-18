/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import java.util.Random;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEscaBulb
extends BlockBase {
    public BlockEscaBulb(String name) {
        this(name, true);
    }

    public BlockEscaBulb(String name, boolean creative) {
        super(Material.glass, name, 0.0f, creative, false);
        this.setStepSound(SRPSoundTypes.FLESH_LIGHT);
        this.setLightLevel(1.0f);
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextFloat() < 0.5f) {
            double x = (double)pos.getX() + 0.2 + rand.nextDouble() * 0.6;
            double y = (double)pos.getY() + 3.05 + rand.nextDouble() * 0.9;
            double z = (double)pos.getZ() + 0.2 + rand.nextDouble() * 0.6;
            ParticleSpawner.spawnParticle(SRPEnumParticle.BLOOD, x, y, z, 0.0, 0.0, 0.0, 0, 0, 0);
        }
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState state) {
        return false;
    }
}

