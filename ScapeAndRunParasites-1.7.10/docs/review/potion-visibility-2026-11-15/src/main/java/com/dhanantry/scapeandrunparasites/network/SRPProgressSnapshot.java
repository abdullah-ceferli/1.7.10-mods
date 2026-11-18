/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public final class SRPProgressSnapshot {
    public int dimension;
    public int phase;
    public int udl;
    public boolean phaseUnlocked;
    public boolean udUnlocked;
    public double phasePassiveGain;
    public double phaseReinforcementChance;
    public double phaseCothSpawnChance;
    public double phaseCropStuntChance;
    public int phaseSleepPenalty;
    public double beckonStageIGrowPenalty;
    public double beckonStageIIGrowPenalty;
    public double beckonStageIIIGrowPenalty;
    public int udDislodgmentLevel;
    public int udMergeLevel;
    public int udCollectiveConsciousnessLevel;
    public int udScentLevel;
    public int udVectorlessLevel;
    public int udNestsLevel;
    public int udVariantsLevel;
    public int udColoniesLevel;
    public int udHivesLevel;
    public int udNodesLevel;
    public double udMobChance;

    private SRPProgressSnapshot() {
    }

    public static SRPProgressSnapshot collect(World world, EntityPlayer player) {
        SRPProgressSnapshot out = new SRPProgressSnapshot();
        int dim = world.provider.dimensionId;
        SRPSaveData data = SRPSaveData.get(world, 9001);
        out.dimension = dim;
        out.phase = data == null ? 0 : (int)data.getEvolutionPhase(dim);
        out.udl = data == null ? 0 : data.getDeveLevel();
        IBestiaryProgress prog = player == null ? null : (IBestiaryProgress)player.getCapability(BestiaryCapability.CAP, null);
        out.phaseUnlocked = prog != null && prog.hasUnlockedProgressPhase();
        out.udUnlocked = prog != null && prog.hasUnlockedProgressUD();
        out.phasePassiveGain = SRPProgressSnapshot.getPhasePassiveGain(out.phase);
        out.phaseReinforcementChance = SRPProgressSnapshot.getPhaseReinforcementChance(out.phase);
        out.phaseCothSpawnChance = SRPProgressSnapshot.getPhaseCothSpawnChance(out.phase);
        out.phaseCropStuntChance = SRPProgressSnapshot.getPhaseCropStuntChance(out.phase);
        out.phaseSleepPenalty = SRPProgressSnapshot.getPhaseSleepPenalty(out.phase);
        out.beckonStageIGrowPenalty = SRPProgressSnapshot.getBeckonStageIGrowPenalty(out.phase);
        out.beckonStageIIGrowPenalty = SRPProgressSnapshot.getBeckonStageIIGrowPenalty(out.phase);
        out.beckonStageIIIGrowPenalty = SRPProgressSnapshot.getBeckonStageIIIGrowPenalty(out.phase);
        out.udDislodgmentLevel = SRPConfigSystems.deveDisloUse;
        out.udMergeLevel = SRPConfigSystems.deveMergeUse;
        out.udCollectiveConsciousnessLevel = SRPConfigSystems.deveOnemindUse;
        out.udScentLevel = SRPConfigSystems.deveScentUse;
        out.udVectorlessLevel = SRPConfigSystems.deveOriginlessUse;
        out.udNestsLevel = SRPConfigSystems.deveNestsUse;
        out.udVariantsLevel = SRPConfigSystems.deveAlwaysVariantUse;
        out.udColoniesLevel = SRPConfigSystems.deveColoniesUse;
        out.udHivesLevel = SRPConfigSystems.deveHivesUse;
        out.udNodesLevel = SRPConfigSystems.deveNodesUse;
        out.udMobChance = SRPConfigSystems.deveMobChance;
        return out;
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("dimension", this.dimension);
        tag.setInteger("phase", this.phase);
        tag.setInteger("udl", this.udl);
        tag.setBoolean("phaseUnlocked", this.phaseUnlocked);
        tag.setBoolean("udUnlocked", this.udUnlocked);
        tag.setDouble("phasePassiveGain", this.phasePassiveGain);
        tag.setDouble("phaseReinforcementChance", this.phaseReinforcementChance);
        tag.setDouble("phaseCothSpawnChance", this.phaseCothSpawnChance);
        tag.setDouble("phaseCropStuntChance", this.phaseCropStuntChance);
        tag.setInteger("phaseSleepPenalty", this.phaseSleepPenalty);
        tag.setDouble("beckonStageIGrowPenalty", this.beckonStageIGrowPenalty);
        tag.setDouble("beckonStageIIGrowPenalty", this.beckonStageIIGrowPenalty);
        tag.setDouble("beckonStageIIIGrowPenalty", this.beckonStageIIIGrowPenalty);
        tag.setInteger("udDislodgmentLevel", this.udDislodgmentLevel);
        tag.setInteger("udMergeLevel", this.udMergeLevel);
        tag.setInteger("udCollectiveConsciousnessLevel", this.udCollectiveConsciousnessLevel);
        tag.setInteger("udScentLevel", this.udScentLevel);
        tag.setInteger("udVectorlessLevel", this.udVectorlessLevel);
        tag.setInteger("udNestsLevel", this.udNestsLevel);
        tag.setInteger("udVariantsLevel", this.udVariantsLevel);
        tag.setInteger("udColoniesLevel", this.udColoniesLevel);
        tag.setInteger("udHivesLevel", this.udHivesLevel);
        tag.setInteger("udNodesLevel", this.udNodesLevel);
        tag.setDouble("udMobChance", this.udMobChance);
        return tag;
    }

    private static double getPhasePassiveGain(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.phaseKillCountPlusOne;
            }
            case 2: {
                return SRPConfigSystems.phaseKillCountPlusTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseKillCountPlusThree;
            }
            case 4: {
                return SRPConfigSystems.phaseKillCountPlusFour;
            }
            case 5: {
                return SRPConfigSystems.phaseKillCountPlusFive;
            }
            case 6: {
                return SRPConfigSystems.phaseKillCountPlusSix;
            }
            case 7: {
                return SRPConfigSystems.phaseKillCountPlusSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseKillCountPlusEight;
            }
            case 9: {
                return SRPConfigSystems.phaseKillCountPlusNine;
            }
            case 10: {
                return SRPConfigSystems.phaseKillCountPlusTen;
            }
        }
        return 0.0;
    }

    private static double getPhaseReinforcementChance(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.reinforcementSystemChanceOne;
            }
            case 2: {
                return SRPConfigSystems.reinforcementSystemChanceTwo;
            }
            case 3: {
                return SRPConfigSystems.reinforcementSystemChanceThree;
            }
            case 4: {
                return SRPConfigSystems.reinforcementSystemChanceFour;
            }
            case 5: {
                return SRPConfigSystems.reinforcementSystemChanceFive;
            }
            case 6: {
                return SRPConfigSystems.reinforcementSystemChanceSix;
            }
            case 7: {
                return SRPConfigSystems.reinforcementSystemChanceSeven;
            }
            case 8: {
                return SRPConfigSystems.reinforcementSystemChanceEight;
            }
            case 9: {
                return SRPConfigSystems.reinforcementSystemChanceNine;
            }
            case 10: {
                return SRPConfigSystems.reinforcementSystemChanceTen;
            }
        }
        return 0.0;
    }

    private static double getPhaseCothSpawnChance(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.mobSpawningCOTHChanceOne;
            }
            case 2: {
                return SRPConfigSystems.mobSpawningCOTHChanceTwo;
            }
            case 3: {
                return SRPConfigSystems.mobSpawningCOTHChanceThree;
            }
            case 4: {
                return SRPConfigSystems.mobSpawningCOTHChanceFour;
            }
            case 5: {
                return SRPConfigSystems.mobSpawningCOTHChanceFive;
            }
            case 6: {
                return SRPConfigSystems.mobSpawningCOTHChanceSix;
            }
            case 7: {
                return SRPConfigSystems.mobSpawningCOTHChanceSeven;
            }
            case 8: {
                return SRPConfigSystems.mobSpawningCOTHChanceEight;
            }
            case 9: {
                return SRPConfigSystems.mobSpawningCOTHChanceNine;
            }
            case 10: {
                return SRPConfigSystems.mobSpawningCOTHChanceTen;
            }
        }
        return 0.0;
    }

    private static double getPhaseCropStuntChance(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.cropGrowStunnedOne;
            }
            case 2: {
                return SRPConfigSystems.cropGrowStunnedTwo;
            }
            case 3: {
                return SRPConfigSystems.cropGrowStunnedThree;
            }
            case 4: {
                return SRPConfigSystems.cropGrowStunnedFour;
            }
            case 5: {
                return SRPConfigSystems.cropGrowStunnedFive;
            }
            case 6: {
                return SRPConfigSystems.cropGrowStunnedSix;
            }
            case 7: {
                return SRPConfigSystems.cropGrowStunnedSeven;
            }
            case 8: {
                return SRPConfigSystems.cropGrowStunnedEight;
            }
            case 9: {
                return SRPConfigSystems.cropGrowStunnedNine;
            }
            case 10: {
                return SRPConfigSystems.cropGrowStunnedTen;
            }
        }
        return 0.0;
    }

    private static int getPhaseSleepPenalty(int phase) {
        switch (phase) {
            case 0: {
                return SRPConfigSystems.sleepPenaltyZero;
            }
            case 1: {
                return SRPConfigSystems.sleepPenaltyOne;
            }
            case 2: {
                return SRPConfigSystems.sleepPenaltyTwo;
            }
            case 3: {
                return SRPConfigSystems.sleepPenaltyThree;
            }
            case 4: {
                return SRPConfigSystems.sleepPenaltyFour;
            }
            case 5: {
                return SRPConfigSystems.sleepPenaltyFive;
            }
            case 6: {
                return SRPConfigSystems.sleepPenaltySix;
            }
            case 7: {
                return SRPConfigSystems.sleepPenaltySeven;
            }
            case 8: {
                return SRPConfigSystems.sleepPenaltyEight;
            }
            case 9: {
                return SRPConfigSystems.sleepPenaltyNine;
            }
            case 10: {
                return SRPConfigSystems.sleepPenaltyTen;
            }
        }
        return 0;
    }

    private static double getBeckonStageIGrowPenalty(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.beckonStageIGrowPenaltyOne;
            }
            case 2: {
                return SRPConfigSystems.beckonStageIGrowPenaltyTwo;
            }
            case 3: {
                return SRPConfigSystems.beckonStageIGrowPenaltyThree;
            }
            case 4: {
                return SRPConfigSystems.beckonStageIGrowPenaltyFour;
            }
            case 5: {
                return SRPConfigSystems.beckonStageIGrowPenaltyFive;
            }
            case 6: {
                return SRPConfigSystems.beckonStageIGrowPenaltySix;
            }
            case 7: {
                return SRPConfigSystems.beckonStageIGrowPenaltySeven;
            }
            case 8: {
                return SRPConfigSystems.beckonStageIGrowPenaltyEight;
            }
            case 9: {
                return SRPConfigSystems.beckonStageIGrowPenaltyNine;
            }
            case 10: {
                return SRPConfigSystems.beckonStageIGrowPenaltyTen;
            }
        }
        return 1.0;
    }

    private static double getBeckonStageIIGrowPenalty(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyOne;
            }
            case 2: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyTwo;
            }
            case 3: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyThree;
            }
            case 4: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyFour;
            }
            case 5: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyFive;
            }
            case 6: {
                return SRPConfigSystems.beckonStageIIGrowPenaltySix;
            }
            case 7: {
                return SRPConfigSystems.beckonStageIIGrowPenaltySeven;
            }
            case 8: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyEight;
            }
            case 9: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyNine;
            }
            case 10: {
                return SRPConfigSystems.beckonStageIIGrowPenaltyTen;
            }
        }
        return 1.0;
    }

    private static double getBeckonStageIIIGrowPenalty(int phase) {
        switch (phase) {
            case 1: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyOne;
            }
            case 2: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyTwo;
            }
            case 3: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyThree;
            }
            case 4: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyFour;
            }
            case 5: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyFive;
            }
            case 6: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltySix;
            }
            case 7: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltySeven;
            }
            case 8: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyEight;
            }
            case 9: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyNine;
            }
            case 10: {
                return SRPConfigSystems.beckonStageIIIGrowPenaltyTen;
            }
        }
        return 1.0;
    }
}

