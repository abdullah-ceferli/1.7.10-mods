/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.Tuple
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.storage.MapStorage
 *  net.minecraft.world.storage.WorldSavedData
 *  net.minecraftforge.common.DimensionManager
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.network.SRPCommandDislodgment;
import com.dhanantry.scapeandrunparasites.network.SRPCommandEvolution;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMusicTrackCancelUpdateClientEvoPhase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SRPSaveData
extends WorldSavedData {
    private static final Logger SRP_LOG = LogManager.getLogger((String)"srparasites");
    private static boolean warnedNoWorld = false;
    private int choice;
    private ArrayList<Integer> dimEPid = new ArrayList();
    private ArrayList<Integer> dimEPtotalKills = new ArrayList();
    private ArrayList<Byte> dimEPevolution = new ArrayList();
    private ArrayList<Integer> dimEPtimeEvolution = new ArrayList();
    private ArrayList<Boolean> dimEPcanGainPoints = new ArrayList();
    private ArrayList<Boolean> dimEPcanLossPoints = new ArrayList();
    private ArrayList<String> dimEPcurrentCodes = new ArrayList();
    private ArrayList<String> dimEPcurrentCodesDur = new ArrayList();
    private ArrayList<Byte> dimGeneration = new ArrayList();
    private ArrayList<Integer> dimGenerationTime = new ArrayList();
    private ArrayList<Integer> dimUpdates = new ArrayList();
    private ArrayList<Integer> dimEIVHealth = new ArrayList();
    private ArrayList<Integer> dimEIVArea = new ArrayList();
    private static final String DATA_NAME = "srparasites_global_data";
    private static final String DISLO_R = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private final Multimap<Integer, Tuple<Byte, Integer>> lockedParas = HashMultimap.create();
    private final Map<Integer, Integer> assimCounts = new HashMap<Integer, Integer>();
    public static int falseLevel = 0;

    @Nullable
    private static WorldServer getOverworldOrNull() {
        MinecraftServer srv = FMLCommonHandler.instance().getMinecraftServerInstance();
        return srv != null ? srv.worldServerForDimension(0) : null;
    }

    private static void warnNoWorldOnce(String where) {
        if (!warnedNoWorld) {
            warnedNoWorld = true;
            SRP_LOG.warn("[SRPSaveData] {} called with null world; skipping this tick until worlds are ready.", (Object)where);
        }
    }

    @Nullable
    private static WorldServer getWorldByDim(int dimId) {
        MinecraftServer srv = FMLCommonHandler.instance().getMinecraftServerInstance();
        return srv != null ? srv.worldServerForDimension(dimId) : null;
    }

    private static boolean debugPointConsole() {
        return SRPConfigSystems.debugEvolutionPointsConsole;
    }

    private static boolean debugPointCsv() {
        return SRPConfigSystems.debugEvolutionPointsCsv;
    }

    private static boolean debugPointCallerTrace() {
        return SRPConfigSystems.debugEvolutionPointsCallerTrace;
    }

    private static String getPointSourceName(int srcID) {
        switch (srcID) {
            case 1: {
                return "EIV_DAILY_GROWTH";
            }
            case 2: {
                return "DIM_START_DEFAULT";
            }
            case 3: {
                return "DIM_START_OUTBREAK";
            }
            case 4: {
                return "KILL";
            }
            case 5: {
                return "ASSIMILATION";
            }
            case 6: {
                return "COMMAND";
            }
            case 7: {
                return "EVENT";
            }
            case 8: {
                return "COLONY";
            }
            case 9: {
                return "PENALTY_OR_LOSS";
            }
            case 52: {
                return "COTH_DURATION_REFRESH";
            }
        }
        return "UNKNOWN_" + srcID;
    }

    private static String[] getPointCallerInfo() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        boolean foundPointMutationMethod = false;
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            String methodName = element.getMethodName();
            if (className.endsWith("SRPSaveData") && ("setTotalKills".equals(methodName) || "setCurrentCode".equals(methodName))) {
                foundPointMutationMethod = true;
                continue;
            }
            if (!foundPointMutationMethod || className.endsWith("SRPSaveData")) continue;
            return new String[]{className, methodName, String.valueOf(element.getLineNumber())};
        }
        return new String[]{"unknown", "unknown", "-1"};
    }

    private static String csvSafe(String in) {
        if (in == null) {
            return "";
        }
        return "\"" + in.replace("\"", "\"\"") + "\"";
    }

    private static File getPointDebugFile(World worldIn, int dimID) {
        File folder;
        File root = DimensionManager.getCurrentSaveRootDirectory();
        if (root == null && worldIn != null && worldIn.getSaveHandler() != null) {
            root = worldIn.getSaveHandler().getWorldDirectory();
        }
        if (root == null) {
            root = new File(".");
        }
        if (!(folder = new File(root, "srparasites_debug")).exists()) {
            folder.mkdirs();
        }
        return new File(folder, "evolution_points_dim" + dimID + ".csv");
    }

    private static void writePointCsv(World worldIn, int dimID, int srcID, String srcName, boolean plus, int requestedAmount, int processedAmount, int appliedDelta, int before, int after, int phaseBefore, int phaseAfter, boolean canChangePhase, boolean isFromSleep, String result) {
        if (!SRPSaveData.debugPointCsv()) {
            return;
        }
        File file = SRPSaveData.getPointDebugFile(worldIn, dimID);
        boolean writeHeader = !file.exists() || file.length() == 0L;
        long worldTime = worldIn != null ? worldIn.getTotalWorldTime() : -1L;
        long day = worldTime >= 0L ? worldTime / 24000L : -1L;
        String callerClass = "";
        String callerMethod = "";
        String callerLine = "";
        if (SRPSaveData.debugPointCallerTrace()) {
            String[] caller = SRPSaveData.getPointCallerInfo();
            callerClass = caller[0];
            callerMethod = caller[1];
            callerLine = caller[2];
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));){
            if (writeHeader) {
                writer.write("real_time_ms,world_time,day,dim,src_id,src_name,caller_class,caller_method,caller_line,plus,requested_amount,processed_amount,applied_delta,before,after,phase_before,phase_after,can_change_phase,is_from_sleep,result");
                writer.newLine();
            }
            writer.write(System.currentTimeMillis() + "," + worldTime + "," + day + "," + dimID + "," + srcID + "," + SRPSaveData.csvSafe(srcName) + "," + SRPSaveData.csvSafe(callerClass) + "," + SRPSaveData.csvSafe(callerMethod) + "," + callerLine + "," + plus + "," + requestedAmount + "," + processedAmount + "," + appliedDelta + "," + before + "," + after + "," + phaseBefore + "," + phaseAfter + "," + canChangePhase + "," + isFromSleep + "," + SRPSaveData.csvSafe(result));
            writer.newLine();
        }
        catch (IOException e) {
            SRP_LOG.warn("[SRP EP DEBUG] Failed to write point CSV for dim {} src {}.", (Object)dimID, (Object)srcName, (Object)e);
        }
    }

    private static void logPointChange(World worldIn, int dimID, int srcID, boolean plus, int requestedAmount, int processedAmount, int before, int after, int phaseBefore, int phaseAfter, boolean canChangePhase, boolean isFromSleep, String result) {
        String srcName = SRPSaveData.getPointSourceName(srcID);
        int appliedDelta = after - before;
        String callerClass = "";
        String callerMethod = "";
        String callerLine = "";
        if (SRPSaveData.debugPointCallerTrace()) {
            String[] caller = SRPSaveData.getPointCallerInfo();
            callerClass = caller[0];
            callerMethod = caller[1];
            callerLine = caller[2];
        }
        if (SRPSaveData.debugPointConsole()) {
            long worldTime = worldIn != null ? worldIn.getTotalWorldTime() : -1L;
            long day = worldTime >= 0L ? worldTime / 24000L : -1L;
            SRP_LOG.debug("[SRP EP DEBUG] result={} dim={} day={} time={} src={}({}) caller={}.{}:{} plus={} requested={} processed={} appliedDelta={} before={} after={} phase={}=>{} canChangePhase={} sleepBypass={}", new Object[]{result, dimID, day, worldTime, srcID, srcName, callerClass, callerMethod, callerLine, plus, requestedAmount, processedAmount, appliedDelta, before, after, phaseBefore, phaseAfter, canChangePhase, isFromSleep});
        }
        SRPSaveData.writePointCsv(worldIn, dimID, srcID, srcName, plus, requestedAmount, processedAmount, appliedDelta, before, after, phaseBefore, phaseAfter, canChangePhase, isFromSleep, result);
    }

    private static void logPointRejected(World worldIn, int dimID, int srcID, boolean plus, int requestedAmount, int processedAmount, int before, int phaseBefore, boolean canChangePhase, boolean isFromSleep, String reason) {
        SRPSaveData.logPointChange(worldIn, dimID, srcID, plus, requestedAmount, processedAmount, before, before, phaseBefore, phaseBefore, canChangePhase, isFromSleep, "REJECTED_" + reason);
    }

    public SRPSaveData(World world, int id) {
        super(DATA_NAME);
        this.createData(world, id);
    }

    public SRPSaveData(String name) {
        super(name);
    }

    public static SRPSaveData get(World world, int id) {
        if (world == null) {
            return null;
        }
        MapStorage storage = world.getMapStorage();
        SRPSaveData instance = (SRPSaveData)storage.loadData(SRPSaveData.class, DATA_NAME);
        if (instance == null) {
            instance = new SRPSaveData(world, id);
            storage.setData(DATA_NAME, (WorldSavedData)instance);
        }
        return instance;
    }

    public void resetInstance(World world) {
        world.getMapStorage().setData(DATA_NAME, (WorldSavedData)new SRPSaveData(world, -999));
    }

    private void createData(World world, int iddd) {
        String[] split;
        SRPMain.logger.debug("creating SRPSaveData from call with id {}, {}", (Object)iddd, (Object)(world.isRemote ? "on client side" : ""));
        this.resetLock();
        for (String line : SRPConfigSystems.evolutionDimStart) {
            split = line.split(";");
            try {
                int dim = Integer.parseInt(split[0].trim());
                byte phase = Byte.parseByte(split[1].trim());
                int points = Integer.parseInt(split[2].trim());
                this.addDim(dim);
                this.setEvolutionPhase(dim, phase, true, world);
                switch (phase) {
                    case -1: {
                        this.setTotalKills(dim, -points, false, world, true, 3);
                        break;
                    }
                    case -2: {
                        this.setGaining(false, dim);
                        this.setLoss(false, dim);
                        break;
                    }
                    default: {
                        this.setTotalKills(dim, points, false, world, true, 2);
                        break;
                    }
                }
            }
            catch (Exception e) {
                SRPMain.logger.warn("Config line in \"Evolution Phases Dimension Starting Phase List\" is malformed, skipping ({})", (Object)line);
            }
        }
        for (String line : SRPConfigSystems.generationDimStart) {
            split = line.split(";");
            try {
                int dim = Integer.parseInt(split[0].trim());
                byte gen = Byte.parseByte(split[1].trim());
                this.addDim(dim);
                this.setGeneration(gen, dim);
            }
            catch (Exception e) {
                SRPMain.logger.warn("Config line in \"Generation Dimension Starting List\" is malformed, skipping ({})", (Object)line);
            }
        }
        this.markDirty();
    }

    private void addDim(int id) {
        if (this.dimEPid.contains(id)) {
            return;
        }
        this.dimEPid.add(id);
        this.dimEPcanGainPoints.add(SRPConfigSystems.evolutionDimGainInverted == Arrays.stream(SRPConfigSystems.evolutionDimGain).anyMatch(dim -> dim == id));
        this.dimEPcanLossPoints.add(SRPConfigSystems.evolutionDimLossInverted == Arrays.stream(SRPConfigSystems.evolutionDimLoss).anyMatch(dim -> dim == id));
        this.dimEPevolution.add(SRPConfigSystems.defaultEvoPhase);
        this.dimEPtimeEvolution.add(0);
        this.dimEPtotalKills.add(SRPConfigSystems.defaultEvoPoints);
        this.dimEPcurrentCodes.add(DISLO_R);
        this.dimEPcurrentCodesDur.add(DISLO_R);
        this.dimGeneration.add(SRPConfigSystems.generationDefa);
        this.dimGenerationTime.add(0);
        this.dimUpdates.add(0);
        this.dimEIVHealth.add(0);
        this.dimEIVArea.add(0);
        this.choice = SRPWorldEntitySpawner.choiceNUMBER;
        this.markDirty();
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList lockedParasTagList;
        if (compound.hasKey("srpchoice")) {
            this.choice = compound.getInteger("srpchoice");
        }
        if (compound.hasKey("srpparasitepoints")) {
            NBTTagList kills = compound.getTagList("srpparasitepoints", 10);
            NBTTagList time = compound.getTagList("srpparasitetimeevoworld", 10);
            NBTTagList evolution = compound.getTagList("srpparasiteevolution", 10);
            NBTTagList gaining = compound.getTagList("srpevolutiongaining", 10);
            NBTTagList loss = compound.getTagList("srpevolutionloss", 10);
            NBTTagList dimid = compound.getTagList("srpevolutiondimid", 10);
            NBTTagList currentC = compound.getTagList("srpevolutioncodes", 10);
            NBTTagList currentCDur = compound.getTagList("srpevolutioncodesdur", 10);
            NBTTagList gene = compound.getTagList("srpgeneration", 10);
            NBTTagList genetime = compound.getTagList("srpgenerationtime", 10);
            NBTTagList updates = compound.getTagList("srpworldupdates", 10);
            NBTTagList eivH = compound.getTagList("srpeivhealth", 10);
            NBTTagList eivA = compound.getTagList("srpeivarea", 10);
            if (dimid.tagCount() != evolution.tagCount() || dimid.tagCount() != kills.tagCount() || dimid.tagCount() != gaining.tagCount() || dimid.tagCount() != loss.tagCount() || dimid.tagCount() != time.tagCount() || dimid.tagCount() != currentC.tagCount() || dimid.tagCount() != currentCDur.tagCount() || dimid.tagCount() != gene.tagCount() || dimid.tagCount() != genetime.tagCount() || dimid.tagCount() != updates.tagCount() || dimid.tagCount() != eivH.tagCount() || dimid.tagCount() != eivA.tagCount()) {
                SRPMain.logger.log(Level.ERROR, "Problem while reading Evolution Phases from the World NBT");
            } else {
                for (int i = 0; i < dimid.tagCount(); ++i) {
                    NBTTagCompound tagA = kills.getCompoundTagAt(i);
                    int k = tagA.getInteger("kills" + i);
                    this.dimEPtotalKills.add(i, k);
                    NBTTagCompound tagB = time.getCompoundTagAt(i);
                    int t = tagB.getInteger("time" + i);
                    this.dimEPtimeEvolution.add(i, t);
                    NBTTagCompound tagC = evolution.getCompoundTagAt(i);
                    int e = tagC.getInteger("ev" + i);
                    this.dimEPevolution.add(i, (byte)e);
                    NBTTagCompound tagD = gaining.getCompoundTagAt(i);
                    boolean g = tagD.getBoolean("gain" + i);
                    this.dimEPcanGainPoints.add(i, g);
                    NBTTagCompound tagE = loss.getCompoundTagAt(i);
                    boolean l = tagE.getBoolean("canLose" + i);
                    this.dimEPcanLossPoints.add(i, l);
                    NBTTagCompound tagF = dimid.getCompoundTagAt(i);
                    int d = tagF.getInteger("dimid" + i);
                    this.dimEPid.add(i, d);
                    NBTTagCompound tagG = currentC.getCompoundTagAt(i);
                    String h = tagG.getString("code" + i);
                    this.dimEPcurrentCodes.add(i, h);
                    NBTTagCompound tagH = currentCDur.getCompoundTagAt(i);
                    String j = tagH.getString("codedur" + i);
                    this.dimEPcurrentCodesDur.add(i, j);
                    NBTTagCompound tagX = gene.getCompoundTagAt(i);
                    byte ix = tagX.getByte("generation" + i);
                    this.dimGeneration.add(i, ix);
                    NBTTagCompound tagY = genetime.getCompoundTagAt(i);
                    int iy = tagY.getInteger("generationtime" + i);
                    this.dimGenerationTime.add(i, iy);
                    NBTTagCompound tagZ = updates.getCompoundTagAt(i);
                    int iz = tagZ.getInteger("update" + i);
                    this.dimUpdates.add(i, iz);
                    NBTTagCompound tagEIVH = eivH.getCompoundTagAt(i);
                    int eivh = tagEIVH.getInteger("eivh" + i);
                    this.dimEIVHealth.add(i, eivh);
                    NBTTagCompound tagEIVA = eivA.getCompoundTagAt(i);
                    int eiva = tagEIVA.getInteger("eiva" + i);
                    this.dimEIVArea.add(i, eiva);
                }
            }
        }
        if (compound.hasKey("lockedParasites")) {
            lockedParasTagList = compound.getTagList("lockedParasites", 10);
            for (NBTBase b : lockedParasTagList) {
                NBTTagCompound tag = (NBTTagCompound)b;
                int dimId = tag.getInteger("dimId");
                byte phase = tag.getByte("phase");
                int id = tag.getInteger("parasiteId");
                this.lockedParas.put((Object)dimId, (Object)new Tuple((Object)phase, (Object)id));
            }
        }
        if (compound.hasKey("srplockedparasites")) {
            lockedParasTagList = compound.getTagList("srplockedparasites", 10);
            for (int i = 0; i < lockedParasTagList.tagCount(); ++i) {
                NBTTagCompound tag = lockedParasTagList.getCompoundTagAt(i);
                int paraId = tag.getInteger("parasiteid" + i);
                boolean found = false;
                for (String line : SRPConfigSystems.evolutionParasiteLock) {
                    String[] split = line.split(";");
                    try {
                        int configParaId = Integer.parseInt(split[2].trim());
                        if (configParaId != paraId) continue;
                        int dimId = Integer.parseInt(split[0].trim());
                        byte phase = Byte.parseByte(split[1].trim());
                        this.lockedParas.put((Object)dimId, (Object)new Tuple((Object)phase, (Object)paraId));
                        found = true;
                        break;
                    }
                    catch (NumberFormatException e) {
                        SRPMain.logger.warn("Config line in \"Evolution Parasite Lock List\" is malformed, skipping ({})", (Object)line);
                    }
                }
                if (found) continue;
                SRPMain.logger.warn("Error when parsing old SRPSaveData from file: Could not find matching unlocking rule for parasite with id {}, unlocking unconditionally", (Object)paraId);
            }
        }
        if (compound.hasKey("srpassimilatedtotal")) {
            NBTTagList tagListX = compound.getTagList("srpassimilatedtotal", 10);
            NBTTagList tagListY = compound.getTagList("srpassimilatedtotaltimes", 10);
            for (int i = 0; i < tagListX.tagCount(); ++i) {
                NBTTagCompound tagX = tagListX.getCompoundTagAt(i);
                int ix = tagX.getInteger("srpassimilatedtotalid" + i);
                NBTTagCompound tagY = tagListY.getCompoundTagAt(i);
                int iy = tagY.getInteger("srpassimilatedtotalidtimes" + i);
                this.assimCounts.put(ix, iy);
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("srpchoice", this.choice);
        compound.setString("srpversion", "1.10.9");
        if (this.dimEPid.size() != this.dimEPevolution.size() || this.dimEPid.size() != this.dimEPtotalKills.size() || this.dimEPid.size() != this.dimEPcanGainPoints.size() || this.dimEPid.size() != this.dimEPcanLossPoints.size() || this.dimEPid.size() != this.dimEPtimeEvolution.size() || this.dimEPid.size() != this.dimEPcurrentCodes.size() || this.dimEPid.size() != this.dimEPcurrentCodesDur.size() || this.dimEPid.size() != this.dimEIVHealth.size() || this.dimEPid.size() != this.dimEIVArea.size()) {
            SRPMain.logger.log(Level.ERROR, "Problem while writing Evolution Phases in the World NBT");
        } else {
            NBTTagList kills = new NBTTagList();
            NBTTagList time = new NBTTagList();
            NBTTagList evolution = new NBTTagList();
            NBTTagList gaining = new NBTTagList();
            NBTTagList loss = new NBTTagList();
            NBTTagList dimid = new NBTTagList();
            NBTTagList currentC = new NBTTagList();
            NBTTagList currentCDur = new NBTTagList();
            NBTTagList tagListXX = new NBTTagList();
            NBTTagList tagListXY = new NBTTagList();
            NBTTagList tagListXZ = new NBTTagList();
            NBTTagList tagListEIVH = new NBTTagList();
            NBTTagList tagListEIVA = new NBTTagList();
            for (int i = 0; i < this.dimEPid.size(); ++i) {
                NBTTagCompound tagA = new NBTTagCompound();
                tagA.setInteger("kills" + i, this.dimEPtotalKills.get(i).intValue());
                kills.appendTag((NBTBase)tagA);
                NBTTagCompound tagB = new NBTTagCompound();
                tagB.setInteger("time" + i, this.dimEPtimeEvolution.get(i).intValue());
                time.appendTag((NBTBase)tagB);
                NBTTagCompound tagC = new NBTTagCompound();
                tagC.setInteger("ev" + i, (int)this.dimEPevolution.get(i).byteValue());
                evolution.appendTag((NBTBase)tagC);
                NBTTagCompound tagD = new NBTTagCompound();
                tagD.setBoolean("gain" + i, this.dimEPcanGainPoints.get(i).booleanValue());
                gaining.appendTag((NBTBase)tagD);
                NBTTagCompound tagE = new NBTTagCompound();
                tagE.setBoolean("canLose" + i, this.dimEPcanLossPoints.get(i).booleanValue());
                loss.appendTag((NBTBase)tagE);
                NBTTagCompound tagF = new NBTTagCompound();
                tagF.setInteger("dimid" + i, this.dimEPid.get(i).intValue());
                dimid.appendTag((NBTBase)tagF);
                NBTTagCompound tagG = new NBTTagCompound();
                tagG.setString("code" + i, this.dimEPcurrentCodes.get(i));
                currentC.appendTag((NBTBase)tagG);
                NBTTagCompound tagH = new NBTTagCompound();
                tagH.setString("codedur" + i, this.dimEPcurrentCodesDur.get(i));
                currentCDur.appendTag((NBTBase)tagH);
                NBTTagCompound tagX = new NBTTagCompound();
                tagX.setByte("generation" + i, this.dimGeneration.get(i).byteValue());
                tagListXX.appendTag((NBTBase)tagX);
                NBTTagCompound tagY = new NBTTagCompound();
                tagY.setInteger("generationtime" + i, this.dimGenerationTime.get(i).intValue());
                tagListXY.appendTag((NBTBase)tagY);
                NBTTagCompound tagZ = new NBTTagCompound();
                tagZ.setInteger("update" + i, this.dimUpdates.get(i).intValue());
                tagListXZ.appendTag((NBTBase)tagZ);
                NBTTagCompound tagEIVH = new NBTTagCompound();
                tagEIVH.setInteger("eivh" + i, this.dimEIVHealth.get(i).intValue());
                tagListEIVH.appendTag((NBTBase)tagEIVH);
                NBTTagCompound tagEIVA = new NBTTagCompound();
                tagEIVA.setInteger("eiva" + i, this.dimEIVArea.get(i).intValue());
                tagListEIVA.appendTag((NBTBase)tagEIVA);
            }
            compound.setTag("srpparasitepoints", (NBTBase)kills);
            compound.setTag("srpparasitetimeevoworld", (NBTBase)time);
            compound.setTag("srpparasiteevolution", (NBTBase)evolution);
            compound.setTag("srpevolutiongaining", (NBTBase)gaining);
            compound.setTag("srpevolutionloss", (NBTBase)loss);
            compound.setTag("srpevolutiondimid", (NBTBase)dimid);
            compound.setTag("srpevolutioncodes", (NBTBase)currentC);
            compound.setTag("srpevolutioncodesdur", (NBTBase)currentCDur);
            compound.setTag("srpgeneration", (NBTBase)tagListXX);
            compound.setTag("srpgenerationtime", (NBTBase)tagListXY);
            compound.setTag("srpworldupdates", (NBTBase)tagListXZ);
            compound.setTag("srpeivhealth", (NBTBase)tagListEIVH);
            compound.setTag("srpeivarea", (NBTBase)tagListEIVA);
        }
        NBTTagList lockedParasTagList = new NBTTagList();
        for (Map.Entry entry : this.lockedParas.entries()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dimensionId", ((Integer)entry.getKey()).intValue());
            tag.setByte("phase", ((Byte)((Tuple)entry.getValue()).getFirst()).byteValue());
            tag.setInteger("parasiteId", ((Integer)((Tuple)entry.getValue()).getSecond()).intValue());
            lockedParasTagList.appendTag((NBTBase)tag);
        }
        compound.setTag("lockedParasites", (NBTBase)lockedParasTagList);
        NBTTagList tagListXX = new NBTTagList();
        NBTTagList tagListYY = new NBTTagList();
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : this.assimCounts.entrySet()) {
            NBTTagCompound tagX = new NBTTagCompound();
            tagX.setInteger("srpassimilatedtotalid" + i, entry.getKey().intValue());
            tagListXX.appendTag((NBTBase)tagX);
            NBTTagCompound tagY = new NBTTagCompound();
            tagY.setInteger("srpassimilatedtotalidtimes" + i, entry.getValue().intValue());
            tagListYY.appendTag((NBTBase)tagY);
            ++i;
        }
        compound.setTag("srpassimilatedtotal", (NBTBase)tagListXX);
        compound.setTag("srpassimilatedtotaltimes", (NBTBase)tagListYY);
        return compound;
    }

    public int getChoice() {
        return this.choice;
    }

    public boolean checkParasiteID(int paraId) {
        return this.lockedParas.values().stream().anyMatch(pair -> (Integer)pair.getSecond() == paraId);
    }

    public List<Integer> getLockedList() {
        return this.lockedParas.values().stream().map(Tuple::getSecond).collect(Collectors.toList());
    }

    public boolean unlockParasite(int paraId) {
        boolean wasRemoved = this.lockedParas.values().removeIf(pair -> (Integer)pair.getSecond() == paraId);
        this.markDirty();
        return wasRemoved;
    }

    public void unlockAllParasite() {
        this.lockedParas.clear();
        this.markDirty();
    }

    public void resetLock() {
        this.lockedParas.clear();
        for (String line : SRPConfigSystems.evolutionParasiteLock) {
            String[] split = line.split(";");
            try {
                int dim = Integer.parseInt(split[0].trim());
                byte phase = Byte.parseByte(split[1].trim());
                int paraId = Integer.parseInt(split[2].trim());
                this.lockedParas.put((Object)dim, (Object)new Tuple((Object)phase, (Object)paraId));
            }
            catch (Exception e) {
                SRPMain.logger.warn("Config line in \"Evolution Parasite Lock List\" is malformed, skipping ({})", (Object)line);
            }
        }
        this.markDirty();
    }

    public int getEIVHealth(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            return this.dimEIVHealth.get(i);
        }
        return 0;
    }

    public int getEIVArea(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            return this.dimEIVArea.get(i);
        }
        return 0;
    }

    public void setEIVHealthArea(int id, int health, int area) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            this.dimEIVHealth.set(i, health);
            this.dimEIVArea.set(i, area);
            this.markDirty();
        }
    }

    public void addUpdateNumber(int in) {
        if (SRPConfigWorld.originActivated) {
            for (int i = 0; i < this.dimEPid.size(); ++i) {
                int current = this.dimUpdates.get(i) + in;
                if (current <= 0) {
                    current = 0;
                }
                this.dimUpdates.set(i, current);
                int health = this.dimEIVHealth.get(i);
                int size = this.dimEIVArea.get(i);
                long newHealth = health;
                long newSize = size;
                newHealth = (long)((double)newHealth + (double)size * (SRPConfigWorld.originDailyHealth + SRPWorldData.getPhaseHealth(this.dimEPevolution.get(i).byteValue())));
                newSize = (long)((double)newSize + (double)size * (SRPConfigWorld.originDailySize + SRPWorldData.getPhaseSize(this.dimEPevolution.get(i).byteValue())));
                health = newHealth > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)newHealth;
                size = newSize > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)newSize;
                this.dimEIVHealth.set(i, Math.min(health, SRPConfigWorld.originHealthCap));
                this.dimEIVArea.set(i, Math.min(size, SRPConfigWorld.originRadiusCap));
                if (!SRPSaveData.debugPointCsv()) {
                    SRP_LOG.debug("[SRP EP DEBUG] VECTOR_DAY dim={} phase={} eivHealth={} eivArea={}", (Object)this.dimEPid.get(i), (Object)this.dimEPevolution.get(i), (Object)this.dimEIVHealth.get(i), (Object)this.dimEIVArea.get(i));
                }
                long points = this.dimEPevolution.get(i) == -1 ? (long)((int)((double)this.dimEIVHealth.get(i).intValue() * SRPConfigWorld.originDailyEPPointsOutBreak)) : (long)((int)((double)this.dimEIVHealth.get(i).intValue() * SRPConfigWorld.originDailyEPPoints));
                int pointToAdd = points > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)points;
                pointToAdd = Math.min(pointToAdd, SRPCommandEvolution.getVectorPointCap(this.dimEPevolution.get(i).byteValue()));
                this.setTotalKills(this.dimEPid.get(i), pointToAdd, true, (World)DimensionManager.getWorld((int)this.dimEPid.get(i)), true, true, 1);
            }
        }
        this.markDirty();
    }

    public int getUpdateNumber(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            int u = this.dimUpdates.get(i);
            this.dimUpdates.set(i, 0);
            this.markDirty();
            return u;
        }
        this.addDim(id);
        return this.getGeneration(id);
    }

    public void setGaining(boolean in, int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            this.dimEPcanGainPoints.set(i, in);
            this.markDirty();
        }
    }

    public boolean getCanGain(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            return this.dimEPcanGainPoints.get(i);
        }
        this.addDim(id);
        return this.getCanGain(id);
    }

    public void setLoss(boolean in, int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            this.dimEPcanLossPoints.set(i, in);
            this.markDirty();
        }
    }

    public boolean getCanLoss(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            return this.dimEPcanLossPoints.get(i);
        }
        this.addDim(id);
        return this.getCanLoss(id);
    }

    public byte getEvolutionPhase(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            return this.dimEPevolution.get(i);
        }
        this.addDim(id);
        return this.getEvolutionPhase(id);
    }

    public void setCooldown(int in, World worldIn, int id, boolean adding) {
        int currentWT = (int)worldIn.getTotalWorldTime();
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            int timer = this.dimEPtimeEvolution.get(i);
            timer = currentWT - this.getDelay(this.getEvolutionPhase(id)) * 20 + in * 20;
            if (adding) {
                this.dimEPtimeEvolution.set(i, timer + this.getCooldown(worldIn, id) * 20);
            } else {
                this.dimEPtimeEvolution.set(i, timer);
            }
            this.markDirty();
        }
    }

    public int getCooldown(World worldIn, int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            if (this.dimEPtimeEvolution.get(i) == 0) {
                return 0;
            }
            int currentWT = (int)worldIn.getTotalWorldTime();
            return Math.max((this.getDelay(this.getEvolutionPhase(id)) * 20 - (currentWT - this.dimEPtimeEvolution.get(i))) / 20, 0);
        }
        return 0;
    }

    public int getTotalKills(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            return this.dimEPtotalKills.get(i);
        }
        this.addDim(id);
        return this.getTotalKills(id);
    }

    public boolean setTotalKills(int dimID, int in, boolean plus, World worldIn, boolean canChangePhase, int srcID) {
        return this.setTotalKills(dimID, in, plus, worldIn, canChangePhase, false, srcID);
    }

    public boolean setTotalKills(int dimID, int in, boolean plus, World worldIn, boolean canChangePhase, boolean isFromSleep, int srcID) {
        if (!SRPConfigSystems.useEvolution) {
            return false;
        }
        int requestedAmount = in;
        boolean requestedPlus = plus;
        boolean requestedCanChangePhase = canChangePhase;
        boolean change = true;
        if (in > 0) {
            switch (this.choice) {
                case 0: {
                    in = (int)((double)in * 0.5);
                    break;
                }
                case 2: {
                    in *= 3;
                    isFromSleep = true;
                    break;
                }
                case 3: {
                    in *= 10;
                    isFromSleep = true;
                }
            }
        }
        if (worldIn != null) {
            if (SRPConfigSystems.evolutionNoPlayerMultipler && EntityQueries.players(worldIn).isEmpty()) {
                return false;
            }
            int difficultyCap = DifficultyAccess.difficulty(worldIn).getDifficultyId() / 2 * SRPConfigSystems.evolutionPointCap;
            if (!isFromSleep && plus) {
                in = Math.min(in, difficultyCap);
            }
        }
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != dimID) continue;
            if (!this.dimEPcanGainPoints.get(i).booleanValue() && plus && in > 0) {
                SRPSaveData.logPointRejected(worldIn, dimID, srcID, plus, requestedAmount, in, this.dimEPtotalKills.get(i), this.dimEPevolution.get(i).byteValue(), canChangePhase, isFromSleep, "GAIN_DISABLED");
                return false;
            }
            if (!this.dimEPcanLossPoints.get(i).booleanValue() && plus && in < 0) {
                SRPSaveData.logPointRejected(worldIn, dimID, srcID, plus, requestedAmount, in, this.dimEPtotalKills.get(i), this.dimEPevolution.get(i).byteValue(), canChangePhase, isFromSleep, "LOSS_DISABLED");
                return false;
            }
            if (this.dimEPevolution.get(i) == -2) {
                SRPSaveData.logPointRejected(worldIn, dimID, srcID, plus, requestedAmount, in, this.dimEPtotalKills.get(i), this.dimEPevolution.get(i).byteValue(), canChangePhase, isFromSleep, "PHASE_NEGATIVE_TWO");
                return false;
            }
            if (this.getDelay(this.dimEPevolution.get(i)) != 0 && worldIn != null && this.choice != 3 && this.getCooldown(worldIn, dimID) != 0 && plus) {
                SRPSaveData.logPointRejected(worldIn, dimID, srcID, plus, requestedAmount, in, this.dimEPtotalKills.get(i), this.dimEPevolution.get(i).byteValue(), canChangePhase, isFromSleep, "COOLDOWN");
                return false;
            }
            change = false;
            if (worldIn != null) {
                this.checkGeneration(dimID, worldIn);
            }
            int beforePoints = this.dimEPtotalKills.get(i);
            byte phaseBefore = this.dimEPevolution.get(i);
            if (plus) {
                int lower;
                boolean top;
                int kills = this.dimEPtotalKills.get(i);
                byte phase = this.dimEPevolution.get(i);
                boolean bl = top = kills >= 0 && in > 0;
                if (in < 0 && phase < 0) {
                    return false;
                }
                kills += in;
                if (SRPConfigSystems.debugEvolutionPointsConsole) {
                    SRP_LOG.debug("[SRP EP DEBUG] calculated points dim={} src={} before={} change={} after={} phase={} canChangePhase={} sleepBypass={}", (Object)dimID, (Object)srcID, (Object)beforePoints, (Object)in, (Object)kills, (Object)phase, (Object)canChangePhase, (Object)isFromSleep);
                }
                if (top && kills < 0) {
                    kills = this.dimEPtotalKills.get(i);
                }
                if (!canChangePhase && phase >= 0 && kills < (lower = SRPCommandEvolution.getNeededPoints(this.getEvolutionPhase(dimID)))) {
                    kills = lower;
                }
                if (kills < 0 && phase >= 0) {
                    kills = 0;
                }
                if (kills > SRPConfigSystems.phaseTenTotalPoints) {
                    kills = SRPConfigSystems.phaseTenTotalPoints;
                }
                this.checkKills(dimID, kills, worldIn, this.dimEPevolution.get(i));
                this.dimEPtotalKills.set(i, kills);
                byte phaseAfter = this.dimEPevolution.get(i);
                SRPSaveData.logPointChange(worldIn, dimID, srcID, requestedPlus, requestedAmount, in, beforePoints, kills, phaseBefore, phaseAfter, requestedCanChangePhase, isFromSleep, "APPLIED");
                continue;
            }
            this.dimEPtotalKills.set(i, in);
            byte phaseAfter = this.dimEPevolution.get(i);
            SRPSaveData.logPointChange(worldIn, dimID, srcID, requestedPlus, requestedAmount, in, beforePoints, in, phaseBefore, phaseAfter, requestedCanChangePhase, isFromSleep, "SET_DIRECT");
        }
        if (change) {
            this.addDim(dimID);
        }
        this.markDirty();
        return true;
    }

    private int getDelay(byte in) {
        switch (in) {
            case 1: {
                return SRPConfigSystems.phaseDelayTicksOne;
            }
            case 2: {
                return SRPConfigSystems.phaseDelayTicksTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseDelayTicksThree;
            }
            case 4: {
                return SRPConfigSystems.phaseDelayTicksFour;
            }
            case 5: {
                return SRPConfigSystems.phaseDelayTicksFive;
            }
            case 6: {
                return SRPConfigSystems.phaseDelayTicksSix;
            }
            case 7: {
                return SRPConfigSystems.phaseDelayTicksSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseDelayTicksEight;
            }
            case 9: {
                return SRPConfigSystems.phaseDelayTicksNine;
            }
            case 10: {
                return SRPConfigSystems.phaseDelayTicksTen;
            }
        }
        return 0;
    }

    private boolean checkKills(int id, int in, World worldIn, byte evoPhase) {
        boolean flag = false;
        boolean changed = false;
        switch (evoPhase) {
            case -1: {
                if (in <= 0 || !this.setEvolutionPhase(id, (byte)0, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningZero, 0);
                changed = true;
                break;
            }
            case 0: {
                if (in <= SRPConfigSystems.phaseKillsOne || !this.setEvolutionPhase(id, (byte)1, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningOne, 1);
                changed = true;
                break;
            }
            case 1: {
                if (in > SRPConfigSystems.phaseKillsTwo) {
                    if (!this.setEvolutionPhase(id, (byte)2, false, worldIn)) break;
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningTwo, 2);
                    changed = true;
                    break;
                }
                if (in >= SRPConfigSystems.phaseKillsOne || !this.setEvolutionPhase(id, (byte)0, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
                break;
            }
            case 2: {
                if (in > SRPConfigSystems.phaseKillsThree) {
                    if (!this.setEvolutionPhase(id, (byte)3, false, worldIn)) break;
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningThree, 3);
                    changed = true;
                    break;
                }
                if (in >= SRPConfigSystems.phaseKillsTwo || !this.setEvolutionPhase(id, (byte)1, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
                break;
            }
            case 3: {
                if (in > SRPConfigSystems.phaseKillsFour) {
                    if (!this.setEvolutionPhase(id, (byte)4, false, worldIn)) break;
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningFour, 4);
                    changed = true;
                    break;
                }
                if (in >= SRPConfigSystems.phaseKillsThree || !this.setEvolutionPhase(id, (byte)2, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
                break;
            }
            case 4: {
                if (in > SRPConfigSystems.phaseKillsFive) {
                    if (!this.setEvolutionPhase(id, (byte)5, false, worldIn)) break;
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningFive, 5);
                    changed = true;
                    break;
                }
                if (in >= SRPConfigSystems.phaseKillsFour || !this.setEvolutionPhase(id, (byte)3, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
                break;
            }
            case 5: {
                if (in > SRPConfigSystems.phaseKillsSix) {
                    if (!this.setEvolutionPhase(id, (byte)6, false, worldIn)) break;
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningSix, 6);
                    changed = true;
                    break;
                }
                if (in >= SRPConfigSystems.phaseKillsFive || !this.setEvolutionPhase(id, (byte)4, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
                break;
            }
            case 6: {
                if (in > SRPConfigSystems.phaseKillsSeven) {
                    if (!this.setEvolutionPhase(id, (byte)7, false, worldIn)) break;
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningSeven, 7);
                    changed = true;
                    break;
                }
                if (in >= SRPConfigSystems.phaseKillsSix || !this.setEvolutionPhase(id, (byte)5, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
                break;
            }
            case 7: {
                if (in > SRPConfigSystems.phaseKillsEight) {
                    if (!this.setEvolutionPhase(id, (byte)8, false, worldIn)) break;
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningEight, 8);
                    changed = true;
                    break;
                }
                if (in >= SRPConfigSystems.phaseKillsSeven || !this.setEvolutionPhase(id, (byte)6, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
                break;
            }
            case 8: {
                if (in > SRPConfigSystems.phaseKillsNine) {
                    if (!this.setEvolutionPhase(id, (byte)9, false, worldIn)) break;
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningNine, 9);
                    changed = true;
                    break;
                }
                if (in >= SRPConfigSystems.phaseKillsEight || !this.setEvolutionPhase(id, (byte)7, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
                break;
            }
            case 9: {
                if (in > SRPConfigSystems.phaseKillsTen) {
                    if (!this.setEvolutionPhase(id, (byte)10, false, worldIn)) break;
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningTen, 10);
                    changed = true;
                    break;
                }
                if (in >= SRPConfigSystems.phaseKillsNine || !this.setEvolutionPhase(id, (byte)8, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
                break;
            }
            case 10: {
                if (in >= SRPConfigSystems.phaseKillsTen || !this.setEvolutionPhase(id, (byte)9, false, worldIn)) break;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
            }
        }
        if (in > 0 && changed && evoPhase > -3 && evoPhase < 11) {
            return this.checkKills(id, in, worldIn, (byte)(evoPhase + 1));
        }
        flag = true;
        return flag;
    }

    public boolean setEvolutionPhase(int id, byte in, boolean override, World worldIn) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            SRPMain.network.sendToDimension((IMessage)new SRPPacketMusicTrackCancelUpdateClientEvoPhase(in), id);
            if (override) {
                this.dimEPevolution.set(i, in);
                this.checkPhase(id, in, worldIn);
            } else {
                this.dimEPevolution.set(i, in);
            }
            if (worldIn != null) {
                this.dimEPtimeEvolution.set(i, (int)worldIn.getTotalWorldTime());
            }
            this.checkForUnlock(this.getEvolutionPhase(id), id, worldIn);
        }
        this.markDirty();
        return true;
    }

    private void checkForUnlock(byte phase, int dimId, World world) {
        Collection pairs = this.lockedParas.get((Object)dimId);
        Iterator it = pairs.iterator();
        while (it.hasNext()) {
            Tuple pair = (Tuple)it.next();
            if (phase != (Byte)pair.getFirst()) continue;
            it.remove();
            ParasiteEventEntity.alertAllPlayerSer(world, SRPConfigSystems.evolutionParasiteLockMessage);
        }
        this.markDirty();
    }

    private void checkPhase(int id, byte in, World worldIn) {
        switch (in) {
            case -1: {
                this.setTotalKills(id, -10, false, worldIn, true, 4);
                break;
            }
            case 0: {
                this.setTotalKills(id, 0, false, worldIn, true, 5);
                break;
            }
            case 1: {
                this.setTotalKills(id, SRPConfigSystems.phaseKillsOne, false, worldIn, true, 6);
                break;
            }
            case 2: {
                this.setTotalKills(id, SRPConfigSystems.phaseKillsTwo, false, worldIn, true, 7);
                break;
            }
            case 3: {
                this.setTotalKills(id, SRPConfigSystems.phaseKillsThree, false, worldIn, true, 8);
                break;
            }
            case 4: {
                this.setTotalKills(id, SRPConfigSystems.phaseKillsFour, false, worldIn, true, 9);
                break;
            }
            case 5: {
                this.setTotalKills(id, SRPConfigSystems.phaseKillsFive, false, worldIn, true, 10);
                break;
            }
            case 6: {
                this.setTotalKills(id, SRPConfigSystems.phaseKillsSix, false, worldIn, true, 11);
                break;
            }
            case 7: {
                this.setTotalKills(id, SRPConfigSystems.phaseKillsSeven, false, worldIn, true, 12);
                break;
            }
            case 8: {
                this.setTotalKills(id, SRPConfigSystems.phaseKillsEight, false, worldIn, true, 13);
                break;
            }
            case 9: {
                this.setTotalKills(id, SRPConfigSystems.phaseKillsNine, false, worldIn, true, 14);
                break;
            }
            case 10: {
                this.setTotalKills(id, SRPConfigSystems.phaseKillsTen, false, worldIn, true, 15);
            }
        }
    }

    public int getNumberIDDataSpawn(int id) {
        return this.assimCounts.getOrDefault(id, 0);
    }

    public void addNumberIDDataSpawn(int id) {
        int count = this.assimCounts.getOrDefault(id, 0);
        this.assimCounts.put(id, ++count);
    }

    public String getCurrentCodeU(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            return this.dimEPcurrentCodes.get(i);
        }
        this.addDim(id);
        return DISLO_R;
    }

    public String getCurrentCodeUD(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            return this.dimEPcurrentCodesDur.get(i);
        }
        this.addDim(id);
        return DISLO_R;
    }

    public int getCurrentCode(int id, int position) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            String[] here = new String[30];
            here = this.dimEPcurrentCodes.get(i).split(";");
            return Integer.parseInt(here[position]);
        }
        this.addDim(id);
        return 0;
    }

    public int[] getDisloValues(int id) {
        int[] values = new int[30];
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            String[] here = new String[30];
            here = this.dimEPcurrentCodes.get(i).split(";");
            for (int k = 0; k < here.length; ++k) {
                values[k] = Integer.parseInt(here[k]);
            }
        }
        return values;
    }

    public int getCurrentCodeDuration(int id, int position) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            String[] here = new String[30];
            here = this.dimEPcurrentCodesDur.get(i).split(";");
            return Integer.parseInt(here[position]);
        }
        this.addDim(id);
        return 0;
    }

    public boolean setCurrentCode(int id, int position, int code, int time, World worldIn, boolean start, int pointCost) {
        if (position > 29 || position < 0) {
            return false;
        }
        if (code < 0 || time < 0 || pointCost < 0) {
            return false;
        }
        if (code == 0) {
            time = 0;
        }
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            if (this.getCurrentCodeDuration(id, position) != 0) {
                return false;
            }
            if (pointCost > 0 && SRPConfigSystems.useEvolution) {
                int beforePoints = this.dimEPtotalKills.get(i);
                int point = beforePoints - pointCost;
                if (point < SRPCommandEvolution.getNeededPoints(this.dimEPevolution.get(i))) {
                    return false;
                }
                byte phaseBefore = this.dimEPevolution.get(i);
                this.dimEPtotalKills.set(i, point);
                byte phaseAfter = this.dimEPevolution.get(i);
                SRPSaveData.logPointChange(worldIn, id, 70 + position, true, -pointCost, -pointCost, beforePoints, point, phaseBefore, phaseAfter, false, false, "DISLODGEMENT_COST");
            }
            String[] here = new String[30];
            here = this.dimEPcurrentCodes.get(i).split(";");
            here[position] = String.valueOf(code);
            this.dimEPcurrentCodes.set(i, this.fixArray(here));
            here = this.dimEPcurrentCodesDur.get(i).split(";");
            here[position] = String.valueOf(time);
            this.dimEPcurrentCodesDur.set(i, this.fixArray(here));
            if (worldIn == null) continue;
            ParasiteEventEntity.alertAllPlayerDim(worldIn, this.getMessageStartEnd(position, start), 200 + position);
            if (!start) continue;
            this.startDislo(i, position, code, time, worldIn);
        }
        this.markDirty();
        return true;
    }

    public void reduceCodesCooldown(int id, int amount, World worldIn) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            String[] here = new String[30];
            here = this.dimEPcurrentCodesDur.get(i).split(";");
            for (int k = 0; k < here.length; ++k) {
                boolean positivee;
                int cool = Integer.parseInt(here[k]);
                if (cool == 0) continue;
                boolean bl = positivee = cool > 0;
                if (positivee) {
                    this.midDislo(id, k, this.getCurrentCode(id, k), worldIn);
                    cool = Math.max(cool - amount, 0);
                } else {
                    cool = Math.min(cool + amount, 0);
                }
                here[k] = String.valueOf(cool);
                if (cool != 0 || !positivee) continue;
                int cooldown = -SRPCommandDislodgment.getDisloCooldown(k);
                here[k] = String.valueOf(cooldown *= (int)SRPCommandDislodgment.getDisloPhaseCooldown(this.dimEPevolution.get(i)));
                String[] nohere = new String[30];
                nohere = this.dimEPcurrentCodes.get(i).split(";");
                nohere[k] = String.valueOf(0);
                this.dimEPcurrentCodes.set(i, this.fixArray(nohere));
                if (worldIn == null) continue;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, this.getMessageStartEnd(k, false), 200 + k);
                this.endDislo(k, worldIn);
            }
            this.dimEPcurrentCodesDur.set(i, this.fixArray(here));
        }
        this.markDirty();
    }

    private String fixArray(String[] list) {
        String atm = "";
        for (int k = 0; k < list.length; ++k) {
            atm = k == list.length - 1 ? atm + list[k] : atm + list[k] + ";";
        }
        return atm;
    }

    private String getMessageStartEnd(int code, boolean start) {
        switch (code) {
            case 0: {
                if (start) {
                    return SRPConfigSystems.disloCOTHIgnoreAmpStartMess;
                }
                return SRPConfigSystems.disloCOTHIgnoreAmpEndMess;
            }
            case 1: {
                if (start) {
                    return SRPConfigSystems.disloCOTHTiersStartMess;
                }
                return SRPConfigSystems.disloCOTHTiersEndMess;
            }
            case 2: {
                if (start) {
                    return SRPConfigSystems.disloSummonByDeathStartMess;
                }
                return SRPConfigSystems.disloSummonByDeathEndMess;
            }
            case 3: {
                if (start) {
                    return SRPConfigSystems.disloPotiEffStartMess;
                }
                return SRPConfigSystems.disloPotiEffEndMess;
            }
            case 4: {
                if (start) {
                    return SRPConfigSystems.disloStatsStartMess;
                }
                return SRPConfigSystems.disloStatsEndMess;
            }
            case 5: {
                if (start) {
                    return SRPConfigSystems.disloDeathRaidS;
                }
                return SRPConfigSystems.disloDeathRaidE;
            }
            case 6: {
                if (start) {
                    return SRPConfigSystems.disloItemDuraS;
                }
                return SRPConfigSystems.disloItemDuraE;
            }
            case 7: {
                if (start) {
                    return SRPConfigSystems.disloHealingDeathS;
                }
                return SRPConfigSystems.disloHealingDeathE;
            }
            case 8: {
                if (start) {
                    return SRPConfigSystems.disloDamageDeathS;
                }
                return SRPConfigSystems.disloDamageDeathE;
            }
            case 9: {
                if (start) {
                    return SRPConfigSystems.disloFoodDeathS;
                }
                return SRPConfigSystems.disloFoodDeathE;
            }
            case 10: {
                if (start) {
                    return SRPConfigSystems.disloDeathHighVerionsS;
                }
                return SRPConfigSystems.disloDeathHighVerionsE;
            }
            case 11: {
                if (start) {
                    return SRPConfigSystems.disloParasiteNoPotionS;
                }
                return SRPConfigSystems.disloParasiteNoPotionE;
            }
            case 12: {
                if (start) {
                    return SRPConfigSystems.disloHealthDrainingS;
                }
                return SRPConfigSystems.disloHealthDrainingE;
            }
            case 13: {
                if (start) {
                    return SRPConfigSystems.disloFoodDrainingS;
                }
                return SRPConfigSystems.disloFoodDrainingE;
            }
            case 14: {
                if (start) {
                    return SRPConfigSystems.disloNextPhaseLS;
                }
                return SRPConfigSystems.disloNextPhaseLE;
            }
            case 15: {
                if (start) {
                    return SRPConfigSystems.disloGrowlNoiseS;
                }
                return SRPConfigSystems.disloGrowlNoiseE;
            }
            case 16: {
                if (start) {
                    return SRPConfigSystems.disloWalkNoiseS;
                }
                return SRPConfigSystems.disloWalkNoiseE;
            }
            case 17: {
                if (start) {
                    return SRPConfigSystems.disloShieldFoodS;
                }
                return SRPConfigSystems.disloShieldFoodE;
            }
            case 18: {
                if (start) {
                    return SRPConfigSystems.disloLootXpCancS;
                }
                return SRPConfigSystems.disloLootXpCancE;
            }
            case 19: {
                if (start) {
                    return SRPConfigSystems.disloKillcountIncS;
                }
                return SRPConfigSystems.disloKillcountIncE;
            }
            case 20: {
                if (start) {
                    return SRPConfigSystems.disloGiveBodiesS;
                }
                return SRPConfigSystems.disloGiveBodiesE;
            }
            case 21: {
                if (start) {
                    return SRPConfigSystems.disloBurningDeathS;
                }
                return SRPConfigSystems.disloBurningDeathE;
            }
            case 22: {
                if (start) {
                    return SRPConfigSystems.disloSameVersionDyeingS;
                }
                return SRPConfigSystems.disloSameVersionDyeingE;
            }
            case 23: {
                if (start) {
                    return SRPConfigSystems.disloColonyNoLimitS;
                }
                return SRPConfigSystems.disloColonyNoLimitE;
            }
            case 24: {
                if (start) {
                    return SRPConfigSystems.disloNexusGrowthS;
                }
                return SRPConfigSystems.disloNexusGrowthE;
            }
            case 25: {
                if (start) {
                    return SRPConfigSystems.disloParasiteBlockS;
                }
                return SRPConfigSystems.disloParasiteBlockE;
            }
        }
        return "missing dislo message";
    }

    private void startDislo(int id, int position, int code, int time, World world) {
        List serverList = EntityQueries.loaded(world);
        block19: for (int x = 0; x < serverList.size(); ++x) {
            boolean flag = serverList.get(x) instanceof EntityParasiteBase;
            switch (position) {
                case 2: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberTwo = true;
                    continue block19;
                }
                case 3: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberThree = true;
                    continue block19;
                }
                case 4: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberFour = true;
                    continue block19;
                }
                case 5: {
                    if (!flag) continue block19;
                    ((Entity)serverList.get(x)).attackEntityFrom(DamageSource.outOfWorld, 10000.0f);
                    continue block19;
                }
                case 6: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberSix = true;
                    continue block19;
                }
                case 7: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberSeven = true;
                    continue block19;
                }
                case 8: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberEight = true;
                    continue block19;
                }
                case 9: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberNine = true;
                    continue block19;
                }
                case 11: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberEleven = time * 20 + 50;
                    continue block19;
                }
                case 15: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberFifteen = time * 20 + 50;
                    ((EntityParasiteBase)serverList.get(x)).setDislo15(true);
                    continue block19;
                }
                case 16: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberSixteen = time * 20 + 50;
                    ((EntityParasiteBase)serverList.get((int)x)).distanceWalkedOnStepModifiedDislo = ((Entity)serverList.get((int)x)).distanceWalkedOnStepModified;
                    continue block19;
                }
                case 17: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberSeventeen = time * 20 + 50;
                    continue block19;
                }
                case 18: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberEighteen = true;
                    continue block19;
                }
                case 19: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberNineteen = time * 20 + 50;
                    continue block19;
                }
                case 20: {
                    if (!flag) continue block19;
                    if (serverList.get(x) instanceof EntityInhooM) {
                        ((EntityInhooM)serverList.get((int)x)).disloNumberTwenty = true;
                    }
                    if (!(serverList.get(x) instanceof EntityInhooS)) continue block19;
                    ((EntityInhooS)serverList.get((int)x)).disloNumberTwenty = true;
                    continue block19;
                }
                case 21: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberTwentyone = true;
                    continue block19;
                }
                case 22: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberTwentytwo = true;
                    continue block19;
                }
                default: {
                    return;
                }
            }
        }
    }

    private void midDislo(int id, int position, int code, World world) {
        switch (position) {
            case 12: {
                List serverList39 = EntityQueries.loaded(world);
                for (int x = 0; x < serverList39.size(); ++x) {
                    if (!(serverList39.get(x) instanceof EntityLivingBase) || serverList39.get(x) instanceof EntityParasiteBase) continue;
                    ((Entity)serverList39.get(x)).attackEntityFrom(DamageSource.wither, (float)code * (float)SRPConfigSystems.disloSeconds);
                }
                break;
            }
            case 13: {
                List serverList3 = EntityQueries.players(world);
                for (EntityPlayer pla : serverList3) {
                    pla.addExhaustion((float)code * (float)SRPConfigSystems.disloSeconds);
                }
                break;
            }
        }
    }

    private void endDislo(int position, World world) {
        List serverList = EntityQueries.loaded(world);
        block19: for (int x = 0; x < serverList.size(); ++x) {
            boolean flag = serverList.get(x) instanceof EntityParasiteBase;
            switch (position) {
                case 2: {
                    ParasiteEventEntity.disloNumber2(world);
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberTwo = false;
                    continue block19;
                }
                case 3: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberThree = false;
                    continue block19;
                }
                case 4: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberFour = false;
                    continue block19;
                }
                case 5: {
                    ParasiteEventEntity.disloNumber5(null, world);
                    continue block19;
                }
                case 6: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberSix = false;
                    continue block19;
                }
                case 7: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberSeven = false;
                    continue block19;
                }
                case 8: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberEight = false;
                    continue block19;
                }
                case 9: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberNine = false;
                    continue block19;
                }
                case 11: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberEleven = 0;
                    continue block19;
                }
                case 15: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberFifteen = 0;
                    ((EntityParasiteBase)serverList.get(x)).setDislo15(false);
                    continue block19;
                }
                case 16: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberSixteen = 0;
                    continue block19;
                }
                case 17: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberSeventeen = 0;
                    continue block19;
                }
                case 18: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberEighteen = false;
                    continue block19;
                }
                case 19: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberNineteen = 0;
                    continue block19;
                }
                case 20: {
                    if (!flag) continue block19;
                    if (serverList.get(x) instanceof EntityInhooM) {
                        ((EntityInhooM)serverList.get((int)x)).disloNumberTwenty = false;
                    }
                    if (!(serverList.get(x) instanceof EntityInhooS)) continue block19;
                    ((EntityInhooS)serverList.get((int)x)).disloNumberTwenty = false;
                    continue block19;
                }
                case 21: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberTwentyone = false;
                    continue block19;
                }
                case 22: {
                    if (!flag) continue block19;
                    ((EntityParasiteBase)serverList.get((int)x)).disloNumberTwentytwo = false;
                    continue block19;
                }
                default: {
                    return;
                }
            }
        }
    }

    public void setGeneration(byte in, int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            this.dimGeneration.set(i, in);
        }
        this.markDirty();
    }

    public byte getGeneration(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            return this.dimGeneration.get(i);
        }
        this.addDim(id);
        return this.getGeneration(id);
    }

    public void setGenerationTime(int in, int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            this.dimGenerationTime.set(i, in);
        }
        this.markDirty();
    }

    public int getGenerationTime(int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            return this.dimGenerationTime.get(i);
        }
        this.addDim(id);
        return this.getGenerationTime(id);
    }

    public int getGenerationNeededTime(World worldIn, int id) {
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            int currentWT = (int)worldIn.getTotalWorldTime();
            int timer = currentWT - this.dimGenerationTime.get(i);
            int needed = this.getGenerationNeededTime(this.dimGeneration.get(i));
            if (!this.generationPhaseNeeded(this.dimGeneration.get(i), i)) {
                needed = (int)((float)needed * SRPConfigSystems.generationPhasePenalty);
            }
            return Math.max(needed - timer, 0);
        }
        return 0;
    }

    public boolean checkGeneration(int id, World worldIn) {
        boolean change = true;
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            change = false;
            if (this.dimGeneration.get(i) == 5) {
                return false;
            }
            int currentWT = (int)worldIn.getTotalWorldTime();
            int timer = currentWT - this.dimGenerationTime.get(i);
            int needed = this.getGenerationNeededTime(this.dimGeneration.get(i));
            if (!this.generationPhaseNeeded(this.dimGeneration.get(i), i)) {
                needed = (int)((float)needed * SRPConfigSystems.generationPhasePenalty);
            }
            if (timer <= needed) continue;
            byte gene = (byte)(this.dimGeneration.get(i) + 1);
            this.dimGeneration.set(i, gene);
            this.dimGenerationTime.set(i, currentWT);
        }
        if (change) {
            this.addDim(id);
        }
        this.markDirty();
        return true;
    }

    private boolean generationPhaseNeeded(byte gene, int id) {
        switch (gene) {
            case 0: {
                for (int i = 0; i < SRPConfigSystems.generationPhases1.length; ++i) {
                    if (SRPConfigSystems.generationPhases1[i] != this.dimEPevolution.get(id)) continue;
                    return true;
                }
                break;
            }
            case 1: {
                for (int i = 0; i < SRPConfigSystems.generationPhases2.length; ++i) {
                    if (SRPConfigSystems.generationPhases2[i] != this.dimEPevolution.get(id)) continue;
                    return true;
                }
                break;
            }
            case 2: {
                for (int i = 0; i < SRPConfigSystems.generationPhases3.length; ++i) {
                    if (SRPConfigSystems.generationPhases3[i] != this.dimEPevolution.get(id)) continue;
                    return true;
                }
                break;
            }
            case 3: {
                for (int i = 0; i < SRPConfigSystems.generationPhases4.length; ++i) {
                    if (SRPConfigSystems.generationPhases4[i] != this.dimEPevolution.get(id)) continue;
                    return true;
                }
                break;
            }
            case 4: {
                for (int i = 0; i < SRPConfigSystems.generationPhases5.length; ++i) {
                    if (SRPConfigSystems.generationPhases5[i] != this.dimEPevolution.get(id)) continue;
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private int getGenerationNeededTime(byte in) {
        double bonus = 1.0;
        switch (this.choice) {
            case 0: {
                bonus *= 0.5;
                break;
            }
            case 2: {
                bonus *= 3.0;
                break;
            }
            case 3: {
                bonus *= 10.0;
            }
        }
        if (bonus <= 0.0) {
            bonus = 1.0;
        }
        switch (in) {
            case 0: {
                return (int)Math.max(1L, Math.round((double)SRPConfigSystems.generationTime1 / bonus));
            }
            case 1: {
                return (int)Math.max(1L, Math.round((double)SRPConfigSystems.generationTime2 / bonus));
            }
            case 2: {
                return (int)Math.max(1L, Math.round((double)SRPConfigSystems.generationTime3 / bonus));
            }
            case 3: {
                return (int)Math.max(1L, Math.round((double)SRPConfigSystems.generationTime4 / bonus));
            }
            case 4: {
                return (int)Math.max(1L, Math.round((double)SRPConfigSystems.generationTime5 / bonus));
            }
        }
        return 0;
    }

    public boolean[] getGeneModi(int id) {
        boolean[] loot = new boolean[]{true};
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            switch (this.dimGeneration.get(i)) {
                case 0: {
                    return new boolean[]{SRPConfigSystems.generationMiniDamage0, SRPConfigSystems.generationDamageCap0, SRPConfigSystems.generationLookWalls0, SRPConfigSystems.generationSprinting0, SRPConfigSystems.generationWaterLeap0, SRPConfigSystems.generationSpecialM0, SRPConfigSystems.generationAdaptation0, SRPConfigSystems.generationBlockSearch0, SRPConfigSystems.generationResidue0, SRPConfigSystems.generationOrbbox0};
                }
                case 1: {
                    return new boolean[]{SRPConfigSystems.generationMiniDamage1, SRPConfigSystems.generationDamageCap1, SRPConfigSystems.generationLookWalls1, SRPConfigSystems.generationSprinting1, SRPConfigSystems.generationWaterLeap1, SRPConfigSystems.generationSpecialM1, SRPConfigSystems.generationAdaptation1, SRPConfigSystems.generationBlockSearch1, SRPConfigSystems.generationResidue1, SRPConfigSystems.generationOrbbox1};
                }
                case 2: {
                    return new boolean[]{SRPConfigSystems.generationMiniDamage2, SRPConfigSystems.generationDamageCap2, SRPConfigSystems.generationLookWalls2, SRPConfigSystems.generationSprinting2, SRPConfigSystems.generationWaterLeap2, SRPConfigSystems.generationSpecialM2, SRPConfigSystems.generationAdaptation2, SRPConfigSystems.generationBlockSearch2, SRPConfigSystems.generationResidue2, SRPConfigSystems.generationOrbbox2};
                }
                case 3: {
                    return new boolean[]{SRPConfigSystems.generationMiniDamage3, SRPConfigSystems.generationDamageCap3, SRPConfigSystems.generationLookWalls3, SRPConfigSystems.generationSprinting3, SRPConfigSystems.generationWaterLeap3, SRPConfigSystems.generationSpecialM3, SRPConfigSystems.generationAdaptation3, SRPConfigSystems.generationBlockSearch3, SRPConfigSystems.generationResidue3, SRPConfigSystems.generationOrbbox3};
                }
                case 4: {
                    return new boolean[]{SRPConfigSystems.generationMiniDamage4, SRPConfigSystems.generationDamageCap4, SRPConfigSystems.generationLookWalls4, SRPConfigSystems.generationSprinting4, SRPConfigSystems.generationWaterLeap4, SRPConfigSystems.generationSpecialM4, SRPConfigSystems.generationAdaptation4, SRPConfigSystems.generationBlockSearch4, SRPConfigSystems.generationResidue4, SRPConfigSystems.generationOrbbox4};
                }
                case 5: {
                    return new boolean[]{SRPConfigSystems.generationMiniDamage5, SRPConfigSystems.generationDamageCap5, SRPConfigSystems.generationLookWalls5, SRPConfigSystems.generationSprinting5, SRPConfigSystems.generationWaterLeap5, SRPConfigSystems.generationSpecialM5, SRPConfigSystems.generationAdaptation5, SRPConfigSystems.generationBlockSearch5, SRPConfigSystems.generationResidue5, SRPConfigSystems.generationOrbbox5};
                }
            }
            return new boolean[]{true, true, true, true, true, true, true, true, true, true};
        }
        return loot;
    }

    public float[] getGeneModi2(int id) {
        float[] loot = new float[]{0.0f};
        for (int i = 0; i < this.dimEPid.size(); ++i) {
            if (this.dimEPid.get(i) != id) continue;
            switch (this.dimGeneration.get(i)) {
                case 0: {
                    return new float[]{SRPConfigSystems.generationPoisonHeal0, SRPConfigSystems.generationMobHealing0, SRPConfigSystems.generationAttackSpeed0};
                }
                case 1: {
                    return new float[]{SRPConfigSystems.generationPoisonHeal1, SRPConfigSystems.generationMobHealing1, SRPConfigSystems.generationAttackSpeed1};
                }
                case 2: {
                    return new float[]{SRPConfigSystems.generationPoisonHeal2, SRPConfigSystems.generationMobHealing2, SRPConfigSystems.generationAttackSpeed2};
                }
                case 3: {
                    return new float[]{SRPConfigSystems.generationPoisonHeal3, SRPConfigSystems.generationMobHealing3, SRPConfigSystems.generationAttackSpeed3};
                }
                case 4: {
                    return new float[]{SRPConfigSystems.generationPoisonHeal4, SRPConfigSystems.generationMobHealing4, SRPConfigSystems.generationAttackSpeed4};
                }
                case 5: {
                    return new float[]{SRPConfigSystems.generationPoisonHeal5, SRPConfigSystems.generationMobHealing5, SRPConfigSystems.generationAttackSpeed5};
                }
            }
            return new float[]{2.5f, 3.0f, 0.5f};
        }
        return loot;
    }

    public int[] getDeveDimsPhases() {
        int points = 0;
        int dims = 0;
        for (int i = 0; i < this.dimEPevolution.size(); ++i) {
            if (this.dimEPevolution.get(i) <= 0) continue;
            points += this.dimEPevolution.get(i).byteValue();
            ++dims;
        }
        return new int[]{points, dims};
    }

    public int getDeveLevel() {
        if (falseLevel != 0) {
            return falseLevel;
        }
        int[] check = this.getDeveDimsPhases();
        int value = check[0];
        int dims = check[1];
        if (value >= SRPConfigSystems.devePointsFour && dims >= SRPConfigSystems.deveMiniDimsFour) {
            return 4;
        }
        if (value >= SRPConfigSystems.devePointsThree && dims >= SRPConfigSystems.deveMiniDimsThree) {
            return 3;
        }
        if (value >= SRPConfigSystems.devePointsTwo && dims >= SRPConfigSystems.deveMiniDimsTwo) {
            return 2;
        }
        if (value >= SRPConfigSystems.devePointsOne && dims >= SRPConfigSystems.deveMiniDimsOne) {
            return 1;
        }
        return 0;
    }

    public ArrayList<Integer> getColonies(String i) {
        if (i.equals("d")) {
            return this.dimEPid;
        }
        if (i.equals("k")) {
            return this.dimEPtotalKills;
        }
        if (i.equals("p")) {
            ArrayList<Integer> eee = new ArrayList<Integer>();
            for (byte b : this.dimEPevolution) {
                eee.add(Integer.valueOf(b));
            }
            return eee;
        }
        if (i.equals("g")) {
            ArrayList<Integer> eee = new ArrayList<Integer>();
            for (byte b : this.dimGeneration) {
                eee.add(Integer.valueOf(b));
            }
            return eee;
        }
        return null;
    }
}

