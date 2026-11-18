package com.dhanantry.scapeandrunparasites.backport.world;

import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

/** Native world inputs for local difficulty; reuse the established loaded-chunk boundary. */
public final class DifficultyAccess {
    private DifficultyAccess() {}

    public static EnumDifficulty difficulty(World world) { return world.difficultySetting; }

    public static DifficultyInstance at(World world, BlockPos pos) {
        long inhabitedTime = 0L;
        float moonFactor = 0.0F;
        if (WorldAccess.isBlockLoaded(world, pos)) {
            moonFactor = WorldProvider.moonPhaseFactors[world.provider.getMoonPhase(world.getWorldTime())];
            inhabitedTime = world.getChunkFromBlockCoords(pos.getX(), pos.getZ()).inhabitedTime;
        }
        // The original uses day time, not total elapsed world time.
        return new DifficultyInstance(difficulty(world), world.getWorldTime(), inhabitedTime, moonFactor);
    }
}
