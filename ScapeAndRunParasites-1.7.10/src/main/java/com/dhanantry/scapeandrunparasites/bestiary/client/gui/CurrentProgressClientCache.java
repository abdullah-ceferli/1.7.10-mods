/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 */
package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import net.minecraft.nbt.NBTTagCompound;

public final class CurrentProgressClientCache {
    public static boolean hasData = false;
    public static int dimension = 0;
    public static int phase = 0;
    public static int udl = 0;
    public static boolean phaseUnlocked = false;
    public static boolean udUnlocked = false;
    public static double phasePassiveGain = 0.0;
    public static double phaseReinforcementChance = 0.0;
    public static double phaseCothSpawnChance = 0.0;
    public static double phaseCropStuntChance = 0.0;
    public static int phaseSleepPenalty = 0;
    public static double beckonStageIGrowPenalty = 1.0;
    public static double beckonStageIIGrowPenalty = 1.0;
    public static double beckonStageIIIGrowPenalty = 1.0;
    public static int udDislodgmentLevel = 1;
    public static int udMergeLevel = 1;
    public static int udCollectiveConsciousnessLevel = 2;
    public static int udScentLevel = 2;
    public static int udVectorlessLevel = 2;
    public static int udNestsLevel = 3;
    public static int udVariantsLevel = 3;
    public static int udColoniesLevel = 4;
    public static int udHivesLevel = 4;
    public static int udNodesLevel = 4;
    public static double udMobChance = 0.5;

    private CurrentProgressClientCache() {
    }

    public static void read(NBTTagCompound tag) {
        if (tag == null) {
            return;
        }
        dimension = tag.getInteger("dimension");
        phase = tag.getInteger("phase");
        udl = tag.getInteger("udl");
        phaseUnlocked = tag.getBoolean("phaseUnlocked");
        udUnlocked = tag.getBoolean("udUnlocked");
        phasePassiveGain = tag.getDouble("phasePassiveGain");
        phaseReinforcementChance = tag.getDouble("phaseReinforcementChance");
        phaseCothSpawnChance = tag.getDouble("phaseCothSpawnChance");
        phaseCropStuntChance = tag.getDouble("phaseCropStuntChance");
        phaseSleepPenalty = tag.getInteger("phaseSleepPenalty");
        beckonStageIGrowPenalty = tag.getDouble("beckonStageIGrowPenalty");
        beckonStageIIGrowPenalty = tag.getDouble("beckonStageIIGrowPenalty");
        beckonStageIIIGrowPenalty = tag.getDouble("beckonStageIIIGrowPenalty");
        udDislodgmentLevel = tag.getInteger("udDislodgmentLevel");
        udMergeLevel = tag.getInteger("udMergeLevel");
        udCollectiveConsciousnessLevel = tag.getInteger("udCollectiveConsciousnessLevel");
        udScentLevel = tag.getInteger("udScentLevel");
        udVectorlessLevel = tag.getInteger("udVectorlessLevel");
        udNestsLevel = tag.getInteger("udNestsLevel");
        udVariantsLevel = tag.getInteger("udVariantsLevel");
        udColoniesLevel = tag.getInteger("udColoniesLevel");
        udHivesLevel = tag.getInteger("udHivesLevel");
        udNodesLevel = tag.getInteger("udNodesLevel");
        udMobChance = tag.getDouble("udMobChance");
        hasData = true;
    }
}

