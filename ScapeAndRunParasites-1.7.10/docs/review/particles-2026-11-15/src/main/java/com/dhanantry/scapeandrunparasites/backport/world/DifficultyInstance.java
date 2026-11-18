package com.dhanantry.scapeandrunparasites.backport.world;

import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;

/** Local spawn difficulty, retaining the original 1.12 float operation order. */
public final class DifficultyInstance {
    private final EnumDifficulty difficulty;
    private final float additionalDifficulty;

    public DifficultyInstance(EnumDifficulty difficulty, long dayTime, long inhabitedTime, float moonFactor) {
        this.difficulty = difficulty;
        this.additionalDifficulty = calculate(difficulty, dayTime, inhabitedTime, moonFactor);
    }

    public EnumDifficulty getDifficulty() { return difficulty; }
    public float getAdditionalDifficulty() { return additionalDifficulty; }
    public boolean isHarderThan(float threshold) { return additionalDifficulty > threshold; }
    public float getClampedAdditionalDifficulty() {
        if (additionalDifficulty < 2.0F) return 0.0F;
        if (additionalDifficulty > 4.0F) return 1.0F;
        return (additionalDifficulty - 2.0F) / 2.0F;
    }

    private static float calculate(EnumDifficulty difficulty, long dayTime, long inhabitedTime, float moonFactor) {
        if (difficulty == EnumDifficulty.PEACEFUL) return 0.0F;
        boolean hard = difficulty == EnumDifficulty.HARD;
        float result = 0.75F;
        float age = MathHelper.clamp_float(((float)dayTime + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
        result += age;
        float local = 0.0F;
        local += MathHelper.clamp_float((float)inhabitedTime / 3600000.0F, 0.0F, 1.0F) * (hard ? 1.0F : 0.75F);
        local += MathHelper.clamp_float(moonFactor * 0.25F, 0.0F, age);
        if (difficulty == EnumDifficulty.EASY) local *= 0.5F;
        result += local;
        return (float)difficulty.getDifficultyId() * result;
    }
}
