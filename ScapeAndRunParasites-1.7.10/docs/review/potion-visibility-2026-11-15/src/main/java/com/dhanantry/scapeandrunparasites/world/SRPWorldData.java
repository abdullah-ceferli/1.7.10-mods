/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.storage.MapStorage
 *  net.minecraft.world.storage.WorldSavedData
 *  org.apache.logging.log4j.Level
 */
package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockBiomeCore;
import com.dhanantry.scapeandrunparasites.block.BlockColonyCore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import java.util.ArrayList;
import java.util.Arrays;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.Level;

public class SRPWorldData
extends WorldSavedData {
    private static final String DATA_NAME = "srparasites_data";
    private ArrayList<Integer> nodeX = new ArrayList();
    private ArrayList<Integer> nodeY = new ArrayList();
    private ArrayList<Integer> nodeZ = new ArrayList();
    private ArrayList<Integer> nodeA = new ArrayList();
    private ArrayList<Byte> nodeT = new ArrayList();
    private ArrayList<Integer> originX = new ArrayList();
    private ArrayList<Integer> originY = new ArrayList();
    private ArrayList<Integer> originZ = new ArrayList();
    private ArrayList<Integer> originA = new ArrayList();
    private ArrayList<Integer> originH = new ArrayList();
    private ArrayList<Integer> colonyX = new ArrayList();
    private ArrayList<Integer> colonyY = new ArrayList();
    private ArrayList<Integer> colonyZ = new ArrayList();
    private ArrayList<Integer> colonyA = new ArrayList();
    private ArrayList<Integer> resistanceI = new ArrayList();
    private ArrayList<String> resistanceS = new ArrayList();
    private boolean dimMeteor;

    public SRPWorldData(World world) {
        super(DATA_NAME);
        this.create(world);
    }

    public SRPWorldData(String name) {
        super(name);
    }

    public static SRPWorldData get(World world) {
        if (world == null) {
            return null;
        }
        MapStorage storage = world.getPerWorldStorage();
        SRPWorldData instance = (SRPWorldData)storage.loadData(SRPWorldData.class, DATA_NAME);
        if (instance == null) {
            instance = new SRPWorldData(world);
            storage.setData(DATA_NAME, (WorldSavedData)instance);
        } else {
            instance.updateDays(world);
        }
        return instance;
    }

    private void create(World world) {
        SRPMain.logger.debug("Creating SRPWorldData for dim {}", (Object)world.provider.dimensionId);
        boolean meteorIsEnabled = Arrays.stream(SRPConfigWorld.meteorBlacklistDims).noneMatch(dim -> dim == world.provider.dimensionId);
        this.setTriggerMet(meteorIsEnabled && (SRPWorldEntitySpawner.triggerSPAWNING || SRPConfigWorld.meteorActive));
        this.markDirty();
    }

    public void resetInstance(World world) {
        world.getPerWorldStorage().setData(DATA_NAME, (WorldSavedData)new SRPWorldData(world));
    }

    public void readFromNBT(NBTTagCompound compound) {
        int type;
        NBTTagCompound tagT;
        int age;
        NBTTagCompound tagA;
        int coordz;
        NBTTagCompound tagZ;
        int coordy;
        NBTTagCompound tagY;
        int coordx;
        NBTTagCompound tagX;
        int i;
        NBTTagList tagListT;
        NBTTagList tagListA;
        NBTTagList tagListZ;
        NBTTagList tagListY;
        NBTTagList tagListX;
        if (compound.hasKey("srpmeteor")) {
            this.dimMeteor = compound.getBoolean("srpmeteor");
        }
        if (compound.hasKey("srpnodescoordsx")) {
            tagListX = compound.getTagList("srpnodescoordsx", 10);
            tagListY = compound.getTagList("srpnodescoordsy", 10);
            tagListZ = compound.getTagList("srpnodescoordsz", 10);
            tagListA = compound.getTagList("srpnodesages", 10);
            tagListT = compound.getTagList("srpnodestypes", 10);
            if (tagListX.tagCount() != tagListY.tagCount() || tagListX.tagCount() != tagListZ.tagCount() || tagListX.tagCount() != tagListA.tagCount() || tagListX.tagCount() != tagListT.tagCount()) {
                SRPMain.logger.log(Level.ERROR, "Problem while reading nodes coords");
            } else {
                for (i = 0; i < tagListX.tagCount(); ++i) {
                    tagX = tagListX.getCompoundTagAt(i);
                    coordx = tagX.getInteger("nodex" + i);
                    this.nodeX.add(i, coordx);
                    tagY = tagListY.getCompoundTagAt(i);
                    coordy = tagY.getInteger("nodey" + i);
                    this.nodeY.add(i, coordy);
                    tagZ = tagListZ.getCompoundTagAt(i);
                    coordz = tagZ.getInteger("nodez" + i);
                    this.nodeZ.add(i, coordz);
                    tagA = tagListA.getCompoundTagAt(i);
                    age = tagA.getInteger("nodea" + i);
                    this.nodeA.add(i, age);
                    tagT = tagListT.getCompoundTagAt(i);
                    type = tagT.getInteger("nodet" + i);
                    this.nodeT.add(i, (byte)type);
                }
            }
        }
        if (compound.hasKey("srporiginscoordsx")) {
            tagListX = compound.getTagList("srporiginscoordsx", 10);
            tagListY = compound.getTagList("srporiginscoordsy", 10);
            tagListZ = compound.getTagList("srporiginscoordsz", 10);
            tagListA = compound.getTagList("srporiginsareas", 10);
            tagListT = compound.getTagList("srporiginshealths", 10);
            if (tagListX.tagCount() != tagListY.tagCount() || tagListX.tagCount() != tagListZ.tagCount() || tagListX.tagCount() != tagListA.tagCount() || tagListX.tagCount() != tagListT.tagCount()) {
                SRPMain.logger.log(Level.ERROR, "Problem while reading origins coords");
            } else {
                for (i = 0; i < tagListX.tagCount(); ++i) {
                    tagX = tagListX.getCompoundTagAt(i);
                    coordx = tagX.getInteger("originx" + i);
                    this.originX.add(i, coordx);
                    tagY = tagListY.getCompoundTagAt(i);
                    coordy = tagY.getInteger("originy" + i);
                    this.originY.add(i, coordy);
                    tagZ = tagListZ.getCompoundTagAt(i);
                    coordz = tagZ.getInteger("originz" + i);
                    this.originZ.add(i, coordz);
                    tagA = tagListA.getCompoundTagAt(i);
                    age = tagA.getInteger("origina" + i);
                    this.originA.add(i, age);
                    tagT = tagListT.getCompoundTagAt(i);
                    type = tagT.getInteger("originh" + i);
                    this.originH.add(i, type);
                }
            }
        }
        if (compound.hasKey("srpcoloniescoordsx")) {
            tagListX = compound.getTagList("srpcoloniescoordsx", 10);
            tagListY = compound.getTagList("srpcoloniescoordsy", 10);
            tagListZ = compound.getTagList("srpcoloniescoordsz", 10);
            tagListA = compound.getTagList("srpcoloniesages", 10);
            if (tagListX.tagCount() != tagListY.tagCount() || tagListX.tagCount() != tagListZ.tagCount() || tagListX.tagCount() != tagListA.tagCount()) {
                SRPMain.logger.log(Level.ERROR, "Problem while reading colonies coords");
            } else {
                for (int i2 = 0; i2 < tagListX.tagCount(); ++i2) {
                    NBTTagCompound tagX2 = tagListX.getCompoundTagAt(i2);
                    int coordx2 = tagX2.getInteger("colonyx" + i2);
                    this.colonyX.add(i2, coordx2);
                    NBTTagCompound tagY2 = tagListY.getCompoundTagAt(i2);
                    int coordy2 = tagY2.getInteger("colonyy" + i2);
                    this.colonyY.add(i2, coordy2);
                    NBTTagCompound tagZ2 = tagListZ.getCompoundTagAt(i2);
                    int coordz2 = tagZ2.getInteger("colonyz" + i2);
                    this.colonyZ.add(i2, coordz2);
                    NBTTagCompound tagA2 = tagListA.getCompoundTagAt(i2);
                    int age2 = tagA2.getInteger("colonya" + i2);
                    this.colonyA.add(i2, age2);
                }
            }
        }
        if (compound.hasKey("srpcolonyresistances")) {
            tagListX = compound.getTagList("srpcolonyresistancei", 10);
            tagListY = compound.getTagList("srpcolonyresistances", 10);
            if (tagListX.tagCount() != tagListY.tagCount()) {
                SRPMain.logger.log(Level.ERROR, "Problem while reading resistance");
            } else {
                for (int i3 = 0; i3 < tagListX.tagCount(); ++i3) {
                    NBTTagCompound tagX3 = tagListX.getCompoundTagAt(i3);
                    int coordx3 = tagX3.getInteger("resistance" + i3);
                    this.resistanceI.add(i3, coordx3);
                    NBTTagCompound tagY3 = tagListY.getCompoundTagAt(i3);
                    String coordy3 = tagY3.getString("resistance" + i3);
                    this.resistanceS.add(i3, coordy3);
                }
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tagT;
        NBTTagCompound tagA;
        NBTTagCompound tagZ;
        NBTTagCompound tagY;
        NBTTagCompound tagX;
        int type;
        int age;
        int coordz;
        int coordy;
        int coordx;
        int i;
        NBTTagList tagListT;
        NBTTagList tagListA;
        NBTTagList tagListZ;
        NBTTagList tagListY;
        NBTTagList tagListX;
        compound.setString("srpversion", "1.10.9");
        compound.setBoolean("srpmeteor", this.dimMeteor);
        if (this.nodeX.size() != this.nodeY.size() || this.nodeX.size() != this.nodeZ.size() || this.nodeX.size() != this.nodeA.size() || this.nodeX.size() != this.nodeT.size()) {
            SRPMain.logger.log(Level.ERROR, "Problem while writing nodes coords");
        } else {
            tagListX = new NBTTagList();
            tagListY = new NBTTagList();
            tagListZ = new NBTTagList();
            tagListA = new NBTTagList();
            tagListT = new NBTTagList();
            for (i = 0; i < this.nodeX.size(); ++i) {
                coordx = this.nodeX.get(i);
                coordy = this.nodeY.get(i);
                coordz = this.nodeZ.get(i);
                age = this.nodeA.get(i);
                type = this.nodeT.get(i).byteValue();
                tagX = new NBTTagCompound();
                tagX.setInteger("nodex" + i, coordx);
                tagListX.appendTag((NBTBase)tagX);
                tagY = new NBTTagCompound();
                tagY.setInteger("nodey" + i, coordy);
                tagListY.appendTag((NBTBase)tagY);
                tagZ = new NBTTagCompound();
                tagZ.setInteger("nodez" + i, coordz);
                tagListZ.appendTag((NBTBase)tagZ);
                tagA = new NBTTagCompound();
                tagA.setInteger("nodea" + i, age);
                tagListA.appendTag((NBTBase)tagA);
                tagT = new NBTTagCompound();
                tagT.setInteger("nodet" + i, type);
                tagListT.appendTag((NBTBase)tagT);
            }
            compound.setTag("srpnodescoordsx", (NBTBase)tagListX);
            compound.setTag("srpnodescoordsy", (NBTBase)tagListY);
            compound.setTag("srpnodescoordsz", (NBTBase)tagListZ);
            compound.setTag("srpnodesages", (NBTBase)tagListA);
            compound.setTag("srpnodestypes", (NBTBase)tagListT);
        }
        if (this.originX.size() != this.originY.size() || this.originX.size() != this.originZ.size() || this.originX.size() != this.originA.size() || this.originX.size() != this.originH.size()) {
            SRPMain.logger.log(Level.ERROR, "Problem while writing origins coords");
        } else {
            tagListX = new NBTTagList();
            tagListY = new NBTTagList();
            tagListZ = new NBTTagList();
            tagListA = new NBTTagList();
            tagListT = new NBTTagList();
            for (i = 0; i < this.originX.size(); ++i) {
                coordx = this.originX.get(i);
                coordy = this.originY.get(i);
                coordz = this.originZ.get(i);
                age = this.originA.get(i);
                type = this.originH.get(i);
                tagX = new NBTTagCompound();
                tagX.setInteger("originx" + i, coordx);
                tagListX.appendTag((NBTBase)tagX);
                tagY = new NBTTagCompound();
                tagY.setInteger("originy" + i, coordy);
                tagListY.appendTag((NBTBase)tagY);
                tagZ = new NBTTagCompound();
                tagZ.setInteger("originz" + i, coordz);
                tagListZ.appendTag((NBTBase)tagZ);
                tagA = new NBTTagCompound();
                tagA.setInteger("origina" + i, age);
                tagListA.appendTag((NBTBase)tagA);
                tagT = new NBTTagCompound();
                tagT.setInteger("originh" + i, type);
                tagListT.appendTag((NBTBase)tagT);
            }
            compound.setTag("srporiginscoordsx", (NBTBase)tagListX);
            compound.setTag("srporiginscoordsy", (NBTBase)tagListY);
            compound.setTag("srporiginscoordsz", (NBTBase)tagListZ);
            compound.setTag("srporiginsareas", (NBTBase)tagListA);
            compound.setTag("srporiginshealths", (NBTBase)tagListT);
        }
        if (this.colonyX.size() != this.colonyY.size() || this.colonyX.size() != this.colonyZ.size() || this.colonyX.size() != this.colonyA.size()) {
            SRPMain.logger.log(Level.ERROR, "Problem while writing colonies coords");
        } else {
            tagListX = new NBTTagList();
            tagListY = new NBTTagList();
            tagListZ = new NBTTagList();
            tagListA = new NBTTagList();
            for (int i2 = 0; i2 < this.colonyX.size(); ++i2) {
                int coordx2 = this.colonyX.get(i2);
                int coordy2 = this.colonyY.get(i2);
                int coordz2 = this.colonyZ.get(i2);
                int age2 = this.colonyA.get(i2);
                NBTTagCompound tagX2 = new NBTTagCompound();
                tagX2.setInteger("colonyx" + i2, coordx2);
                tagListX.appendTag((NBTBase)tagX2);
                NBTTagCompound tagY2 = new NBTTagCompound();
                tagY2.setInteger("colonyy" + i2, coordy2);
                tagListY.appendTag((NBTBase)tagY2);
                NBTTagCompound tagZ2 = new NBTTagCompound();
                tagZ2.setInteger("colonyz" + i2, coordz2);
                tagListZ.appendTag((NBTBase)tagZ2);
                NBTTagCompound tagA2 = new NBTTagCompound();
                tagA2.setInteger("colonya" + i2, age2);
                tagListA.appendTag((NBTBase)tagA2);
            }
            compound.setTag("srpcoloniescoordsx", (NBTBase)tagListX);
            compound.setTag("srpcoloniescoordsy", (NBTBase)tagListY);
            compound.setTag("srpcoloniescoordsz", (NBTBase)tagListZ);
            compound.setTag("srpcoloniesages", (NBTBase)tagListA);
        }
        if (this.resistanceI.size() != this.resistanceS.size()) {
            SRPMain.logger.log(Level.ERROR, "Problem while writing resistance");
        } else {
            tagListX = new NBTTagList();
            tagListY = new NBTTagList();
            for (int i3 = 0; i3 < this.resistanceI.size(); ++i3) {
                int coordx3 = this.resistanceI.get(i3);
                String coordy3 = this.resistanceS.get(i3);
                NBTTagCompound tagX3 = new NBTTagCompound();
                tagX3.setInteger("resistance" + i3, coordx3);
                tagListX.appendTag((NBTBase)tagX3);
                NBTTagCompound tagY3 = new NBTTagCompound();
                tagY3.setString("resistance" + i3, coordy3);
                tagListY.appendTag((NBTBase)tagY3);
            }
            compound.setTag("srpcolonyresistancei", (NBTBase)tagListX);
            compound.setTag("srpcolonyresistances", (NBTBase)tagListY);
        }
        return compound;
    }

    public void setTriggerMet(boolean in) {
        this.dimMeteor = in;
    }

    public boolean getTriggerMet() {
        return this.dimMeteor;
    }

    private long getDistanceSQ(double rootx, double rooty, double rootz, double standingx, double standingy, double standingz) {
        double d0 = rootx - standingx;
        double d1 = rooty - standingy;
        double d2 = rootz - standingz;
        return (long)(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public void clearOriginList() {
        this.originX = new ArrayList();
        this.originY = new ArrayList();
        this.originZ = new ArrayList();
        this.originA = new ArrayList();
        this.originH = new ArrayList();
        this.markDirty();
    }

    public ArrayList<Integer> getorigins(String i) {
        if (i.equals("x")) {
            return this.originX;
        }
        if (i.equals("y")) {
            return this.originY;
        }
        if (i.equals("z")) {
            return this.originZ;
        }
        if (i.equals("a")) {
            return this.originA;
        }
        if (i.equals("h")) {
            return this.originH;
        }
        return null;
    }

    public int setOrigin(World world, int x, int y, int z, int heatlh, int radius) {
        if (!this.canOriginBeMade(new BlockPos(x, y, z))) {
            return 6;
        }
        SRPSaveData data = SRPSaveData.get(world, 201);
        if (this.originX.size() >= this.getOriginCap(world, data)) {
            return 7;
        }
        int canAdd = 1;
        for (int i = 0; i < this.originX.size(); ++i) {
            if (this.originX.get(i) != x || this.originY.get(i) != y || this.originZ.get(i) != z) continue;
            canAdd = 10;
        }
        if (canAdd == 1) {
            this.originX.add(x);
            this.originY.add(y);
            this.originZ.add(z);
            this.originA.add(radius);
            this.originH.add(heatlh);
            this.markDirty();
            this.setEIVHealthToData(world, data);
        }
        if (data.getEvolutionPhase(world.provider.dimensionId) == -1) {
            canAdd = 2;
        }
        return canAdd;
    }

    private int getOriginCap(World in, SRPSaveData data) {
        if (!SRPConfigSystems.useEvolution) {
            return SRPConfigWorld.originCap;
        }
        switch (data.getEvolutionPhase(in.provider.dimensionId)) {
            case -1: {
                return SRPConfigSystems.phaseOriginMinusOne;
            }
            case 0: {
                return SRPConfigSystems.phaseOriginZero;
            }
            case 1: {
                return SRPConfigSystems.phaseOriginOne;
            }
            case 2: {
                return SRPConfigSystems.phaseOriginTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseOriginThree;
            }
            case 4: {
                return SRPConfigSystems.phaseOriginFour;
            }
            case 5: {
                return SRPConfigSystems.phaseOriginFive;
            }
            case 6: {
                return SRPConfigSystems.phaseOriginSix;
            }
            case 7: {
                return SRPConfigSystems.phaseOriginSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseOriginEight;
            }
            case 9: {
                return SRPConfigSystems.phaseOriginNine;
            }
            case 10: {
                return SRPConfigSystems.phaseOriginTen;
            }
        }
        return 0;
    }

    public boolean canOriginBeMade(BlockPos pos) {
        int distance = SRPConfigWorld.originMinimumDistance * SRPConfigWorld.originMinimumDistance;
        for (int i = 0; i < this.originX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            if (!(distanceSQ <= (double)distance)) continue;
            return false;
        }
        return true;
    }

    public boolean removeOrigin(int x, int y, int z, World worldIn) {
        for (int i = 0; i < this.originX.size(); ++i) {
            if (this.originX.get(i) != x || this.originY.get(i) != y || this.originZ.get(i) != z) continue;
            this.originX.remove(i);
            this.originY.remove(i);
            this.originZ.remove(i);
            this.originA.remove(i);
            this.originH.remove(i);
            this.markDirty();
            this.setEIVHealthToData(worldIn, SRPSaveData.get(worldIn, 260));
            return true;
        }
        return false;
    }

    public int nearestInfectionValue(BlockPos pos, boolean health) {
        int heart = this.nearestInfectionIndex(pos);
        if (heart > -1) {
            if (health) {
                return this.originH.get(heart);
            }
            return this.originA.get(heart);
        }
        return heart;
    }

    public int nearestInfectionValueArea(BlockPos pos, boolean health) {
        int heart = this.nearestInfectionPositionIndex(true, pos);
        if (heart > -1) {
            if (health) {
                return this.originH.get(heart);
            }
            return this.originA.get(heart);
        }
        return heart;
    }

    public BlockPos nearestInfectionPosition(Boolean outside, BlockPos pos) {
        BlockPos heart = null;
        double distance = -1.0;
        for (int i = 0; i < this.originX.size(); ++i) {
            long distanceSQ = this.getDistanceSQ(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            long ageDistance = this.originA.get(i).intValue();
            if (distanceSQ > (ageDistance *= ageDistance) && (!outside.booleanValue() || distance != -1.0)) continue;
            if (distance == -1.0) {
                heart = new BlockPos(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue());
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= (double)distanceSQ)) continue;
            heart = new BlockPos(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue());
            distance = distanceSQ;
        }
        return heart;
    }

    public int nearestInfectionPositionIndex(Boolean outside, BlockPos pos) {
        int heart = -1;
        double distance = -1.0;
        for (int i = 0; i < this.originX.size(); ++i) {
            long distanceSQ = this.getDistanceSQ(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            long ageDistance = this.originA.get(i).intValue();
            if (distanceSQ > (ageDistance *= ageDistance) && (!outside.booleanValue() || distance != -1.0)) continue;
            if (distance == -1.0) {
                heart = i;
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= (double)distanceSQ)) continue;
            heart = i;
            distance = distanceSQ;
        }
        return heart;
    }

    public int nearestInfectionIndex(BlockPos pos) {
        int heart = -1;
        double distance = -1.0;
        for (int i = 0; i < this.originX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int ageDistance = this.originA.get(i);
            if (!(distanceSQ <= (double)(ageDistance *= ageDistance))) continue;
            if (distance == -1.0) {
                heart = i;
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            heart = i;
            distance = distanceSQ;
        }
        return heart;
    }

    public void updateOriginValues(World world, int updates, SRPSaveData data) {
        int dim = world.provider.dimensionId;
        byte phase = data.getEvolutionPhase(dim);
        while (updates > 0) {
            for (int i = 0; i < this.originA.size(); ++i) {
                int size = this.originA.get(i);
                int health = this.originH.get(i);
                size = (int)((double)size + (double)size * (SRPConfigWorld.originDailySize + SRPWorldData.getPhaseSize(phase)));
                health = (int)((double)health + (double)size * (SRPConfigWorld.originDailyHealth + SRPWorldData.getPhaseHealth(phase)));
                this.originA.set(i, Math.min(size, SRPConfigWorld.originRadiusCap));
                this.originH.set(i, Math.min(health, SRPConfigWorld.originHealthCap));
            }
            --updates;
        }
        this.markDirty();
        this.setEIVHealthToData(world, data);
    }

    public void setEIVHealthToData(World in, SRPSaveData data) {
        int health = 0;
        int area = 0;
        for (int i = 0; i < this.originH.size(); ++i) {
            health += this.originH.get(i).intValue();
            area += this.originA.get(i).intValue();
        }
        data.setEIVHealthArea(in.provider.dimensionId, health, area);
    }

    public static double getPhaseSize(int in) {
        switch (in) {
            case 1: {
                return SRPConfigSystems.phaseOriginBonusSizeOne;
            }
            case 2: {
                return SRPConfigSystems.phaseOriginBonusSizeTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseOriginBonusSizeThree;
            }
            case 4: {
                return SRPConfigSystems.phaseOriginBonusSizeFour;
            }
            case 5: {
                return SRPConfigSystems.phaseOriginBonusSizeFive;
            }
            case 6: {
                return SRPConfigSystems.phaseOriginBonusSizeSix;
            }
            case 7: {
                return SRPConfigSystems.phaseOriginBonusSizeSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseOriginBonusSizeEight;
            }
            case 9: {
                return SRPConfigSystems.phaseOriginBonusSizeNine;
            }
            case 10: {
                return SRPConfigSystems.phaseOriginBonusSizeTen;
            }
        }
        return 0.0;
    }

    public static double getPhaseHealth(int in) {
        switch (in) {
            case 1: {
                return SRPConfigSystems.phaseOriginBonusHealthOne;
            }
            case 2: {
                return SRPConfigSystems.phaseOriginBonusHealthTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseOriginBonusHealthThree;
            }
            case 4: {
                return SRPConfigSystems.phaseOriginBonusHealthFour;
            }
            case 5: {
                return SRPConfigSystems.phaseOriginBonusHealthFive;
            }
            case 6: {
                return SRPConfigSystems.phaseOriginBonusHealthSix;
            }
            case 7: {
                return SRPConfigSystems.phaseOriginBonusHealthSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseOriginBonusHealthEight;
            }
            case 9: {
                return SRPConfigSystems.phaseOriginBonusHealthNine;
            }
            case 10: {
                return SRPConfigSystems.phaseOriginBonusHealthTen;
            }
        }
        return 0.0;
    }

    public boolean setOriginHealth(World worldIn, BlockPos pos, int amount, boolean plus) {
        int healthID = -1;
        double distance = -1.0;
        for (int i = 0; i < this.originX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int ageDistance = this.originA.get(i);
            if (!(distanceSQ <= (double)(ageDistance *= ageDistance))) continue;
            if (distance == -1.0) {
                healthID = i;
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            healthID = i;
            distance = distanceSQ;
        }
        if (healthID != -1) {
            SRPSaveData data = SRPSaveData.get(worldIn, 260);
            if (plus) {
                int current;
                if (data.getEvolutionPhase(worldIn.provider.dimensionId) == -1 && amount > 0) {
                    amount = (int)((double)amount * SRPConfigSystems.phaseOriginMinusOnePenalty);
                }
                if ((current = this.originH.get(healthID) + amount) <= 0) {
                    this.originX.remove(healthID);
                    this.originY.remove(healthID);
                    this.originZ.remove(healthID);
                    this.originA.remove(healthID);
                    this.originH.remove(healthID);
                } else {
                    this.originH.set(healthID, current);
                }
            } else if (amount <= 0) {
                this.originX.remove(healthID);
                this.originY.remove(healthID);
                this.originZ.remove(healthID);
                this.originA.remove(healthID);
                this.originH.remove(healthID);
            } else {
                this.originH.set(healthID, amount);
            }
            this.markDirty();
            this.setEIVHealthToData(worldIn, data);
            return true;
        }
        return false;
    }

    public void clearNodeList() {
        this.nodeX = new ArrayList();
        this.nodeY = new ArrayList();
        this.nodeZ = new ArrayList();
        this.nodeA = new ArrayList();
        this.nodeT = new ArrayList();
        this.markDirty();
    }

    public ArrayList<Integer> getNodes(String i) {
        if (i.equals("x")) {
            return this.nodeX;
        }
        if (i.equals("y")) {
            return this.nodeY;
        }
        if (i.equals("z")) {
            return this.nodeZ;
        }
        if (i.equals("a")) {
            return this.nodeA;
        }
        if (i.equals("t")) {
            ArrayList<Integer> eee = new ArrayList<Integer>();
            for (byte b : this.nodeT) {
                eee.add(Integer.valueOf(b));
            }
            return eee;
        }
        return null;
    }

    public int setNode(int x, int y, int z, int type) {
        if (!this.canNodeBeMade(new BlockPos(x, y, z))) {
            return 8;
        }
        if (this.nodeX.size() >= SRPConfigWorld.maximumNumberNodes) {
            return 9;
        }
        int canAdd = 1;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            if (this.nodeX.get(i) != x || this.nodeY.get(i) != y || this.nodeZ.get(i) != z) continue;
            if (this.nodeA.get(i) != 1) {
                this.removeNode(x, y, z);
                this.setNode(x, y, z, type);
            }
            canAdd = 10;
        }
        if (canAdd == 1) {
            this.nodeX.add(x);
            this.nodeY.add(y);
            this.nodeZ.add(z);
            this.nodeA.add(1);
            this.nodeT.add((byte)type);
            this.markDirty();
        }
        return canAdd;
    }

    public boolean canNodeBeMade(BlockPos pos) {
        int distance = SRPConfigWorld.minimumDistanceBetweenNodes * SRPConfigWorld.minimumDistanceBetweenNodes;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            if (!(distanceSQ <= (double)distance)) continue;
            return false;
        }
        return true;
    }

    public boolean removeNode(int x, int y, int z) {
        for (int i = 0; i < this.nodeX.size(); ++i) {
            if (this.nodeX.get(i) != x || this.nodeY.get(i) != y || this.nodeZ.get(i) != z) continue;
            this.nodeX.remove(i);
            this.nodeY.remove(i);
            this.nodeZ.remove(i);
            this.nodeA.remove(i);
            this.nodeT.remove(i);
            this.markDirty();
            return true;
        }
        return false;
    }

    public int isInRangeOfHeart(BlockPos pos, double distance) {
        distance += 1.0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            if (!(distanceSQ < distance * distance)) continue;
            return (int)distanceSQ;
        }
        return -1;
    }

    public int nearestHeartAge(BlockPos pos, boolean spread, int currentDay) {
        int heart = -1;
        double distance = -1.0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            int ageDistance;
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int currentage = ageDistance = this.convertDayToAgeNode(this.nodeA.get(i));
            ageDistance = spread ? this.getDistanceSpreadByAge(ageDistance, true) : this.getDistanceEffectByAge(ageDistance, true);
            if (!(distanceSQ <= (double)ageDistance)) continue;
            if (distance == -1.0) {
                heart = currentage;
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            heart = currentage;
            distance = distanceSQ;
        }
        return heart;
    }

    public int nearestHeartType(BlockPos pos, boolean spread, int currentDay) {
        int type = -1;
        double distance = -1.0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            int ageDistance;
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int currentage = ageDistance = this.convertDayToAgeNode(this.nodeA.get(i));
            ageDistance = spread ? this.getDistanceSpreadByAge(ageDistance, true) : this.getDistanceEffectByAge(ageDistance, true);
            if (!(distanceSQ <= (double)ageDistance)) continue;
            if (distance == -1.0) {
                type = this.nodeT.get(i).byteValue();
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            type = this.nodeT.get(i).byteValue();
            distance = distanceSQ;
        }
        return type;
    }

    public BlockPos nearestHeartAgePosition(BlockPos pos, int currentDay) {
        BlockPos heart = null;
        double distance = -1.0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            int ageDistance;
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int currentage = ageDistance = this.convertDayToAgeNode(this.nodeA.get(i));
            if (!(distanceSQ <= (double)(ageDistance = this.getDistanceEffectByAge(ageDistance, true)))) continue;
            if (distance == -1.0) {
                heart = new BlockPos(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue());
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            heart = new BlockPos(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue());
            distance = distanceSQ;
        }
        return heart;
    }

    private int convertDayToAgeNode(int nodeBirthday) {
        int age = nodeBirthday;
        if (age >= SRPConfigWorld.timeNeedeToNodeThree) {
            return 3;
        }
        if (age >= SRPConfigWorld.timeNeedeToNodeTwo) {
            return 2;
        }
        return 1;
    }

    public int getDistanceSpreadByAge(int age, boolean sq) {
        switch (age) {
            case 1: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeSpreadOne * SRPConfigWorld.nodeRangeSpreadOne;
                }
                return SRPConfigWorld.nodeRangeSpreadOne;
            }
            case 2: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeSpreadTwo * SRPConfigWorld.nodeRangeSpreadTwo;
                }
                return SRPConfigWorld.nodeRangeSpreadTwo;
            }
            case 3: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeSpreadThree * SRPConfigWorld.nodeRangeSpreadThree;
                }
                return SRPConfigWorld.nodeRangeSpreadThree;
            }
        }
        return -1;
    }

    public int getDistanceEffectByAge(int age, boolean sq) {
        switch (age) {
            case 1: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeEffectsOne * SRPConfigWorld.nodeRangeEffectsOne;
                }
                return SRPConfigWorld.nodeRangeEffectsOne;
            }
            case 2: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeEffectsTwo * SRPConfigWorld.nodeRangeEffectsTwo;
                }
                return SRPConfigWorld.nodeRangeEffectsTwo;
            }
            case 3: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeEffectsThree * SRPConfigWorld.nodeRangeEffectsThree;
                }
                return SRPConfigWorld.nodeRangeEffectsThree;
            }
        }
        return -1;
    }

    public void checkHeartExistance(World worldIn) {
        for (int i = this.nodeX.size() - 1; i >= 0; --i) {
            IBlockState state;
            BlockPos pos = new BlockPos(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue());
            if (!WorldAccess.isAreaLoaded(worldIn, pos, 3) || (state = BlockStates.get(worldIn, pos)).getBlock() == SRPBlocks.BiomeHeart && state.getPropertyKeys().contains(BlockBiomeCore.ACTIVE) && (Integer)state.getValue((IProperty)BlockBiomeCore.ACTIVE) > 0) continue;
            this.nodeX.remove(i);
            this.nodeY.remove(i);
            this.nodeZ.remove(i);
            this.nodeA.remove(i);
            this.nodeT.remove(i);
            this.markDirty();
        }
    }

    public int getHeartPocition(BlockPos pos, int currentDay) {
        for (int i = 0; i < this.nodeX.size(); ++i) {
            if (this.nodeX.get(i).intValue() != pos.getX() || this.nodeY.get(i).intValue() != pos.getY() || this.nodeZ.get(i).intValue() != pos.getZ()) continue;
            return this.convertDayToAgeNode(this.nodeA.get(i));
        }
        return -1;
    }

    public int totalNodePoints(int currentDay) {
        int points = 0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            int age = this.convertDayToAgeNode(this.nodeA.get(i));
            points += age;
        }
        return points;
    }

    public void clearColonyList() {
        this.colonyX = new ArrayList();
        this.colonyY = new ArrayList();
        this.colonyZ = new ArrayList();
        this.colonyA = new ArrayList();
        this.markDirty();
    }

    public ArrayList<Integer> getColonies(String i) {
        if (i.equals("x")) {
            return this.colonyX;
        }
        if (i.equals("y")) {
            return this.colonyY;
        }
        if (i.equals("z")) {
            return this.colonyZ;
        }
        if (i.equals("a")) {
            return this.colonyA;
        }
        return null;
    }

    public int setColony(int x, int y, int z) {
        if (!this.canColonyBeMade(new BlockPos(x, y, z))) {
            return 6;
        }
        if (this.colonyX.size() >= SRPConfigWorld.maximumNumberColonies) {
            return 7;
        }
        int canAdd = 1;
        for (int i = 0; i < this.colonyX.size(); ++i) {
            if (this.colonyX.get(i) != x || this.colonyY.get(i) != y || this.colonyZ.get(i) != z) continue;
            if (this.colonyA.get(i) != 1) {
                this.removeColony(x, y, z);
                this.setColony(x, y, z);
            }
            canAdd = 10;
        }
        if (canAdd == 1) {
            this.colonyX.add(x);
            this.colonyY.add(y);
            this.colonyZ.add(z);
            this.colonyA.add(1);
            this.markDirty();
        }
        return canAdd;
    }

    public boolean canColonyBeMade(BlockPos pos) {
        int distance = SRPConfigWorld.minimumDistanceBetweenColonies * SRPConfigWorld.minimumDistanceBetweenColonies;
        for (int i = 0; i < this.colonyX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.colonyX.get(i).intValue(), this.colonyY.get(i).intValue(), this.colonyZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            if (!(distanceSQ <= (double)distance)) continue;
            return false;
        }
        return true;
    }

    public boolean removeColony(int x, int y, int z) {
        for (int i = 0; i < this.colonyX.size(); ++i) {
            if (this.colonyX.get(i) != x || this.colonyY.get(i) != y || this.colonyZ.get(i) != z) continue;
            this.colonyX.remove(i);
            this.colonyY.remove(i);
            this.colonyZ.remove(i);
            this.colonyA.remove(i);
            this.markDirty();
            return true;
        }
        return false;
    }

    public BlockPos nearestColonyPosition(BlockPos pos, boolean effect) {
        BlockPos posColony = null;
        double distance = -1.0;
        for (int i = 0; i < this.colonyX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.colonyX.get(i).intValue(), this.colonyY.get(i).intValue(), this.colonyZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int ageDistance = this.convertDayToPointsColony(this.colonyA.get(i));
            if (!(distanceSQ <= (double)(ageDistance = this.getColonyDistanceSpreadByPoints(ageDistance, true, effect)))) continue;
            if (distance == -1.0) {
                posColony = new BlockPos(this.colonyX.get(i).intValue(), this.colonyY.get(i).intValue(), this.colonyZ.get(i).intValue());
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            posColony = new BlockPos(this.colonyX.get(i).intValue(), this.colonyY.get(i).intValue(), this.colonyZ.get(i).intValue());
            distance = distanceSQ;
        }
        return posColony;
    }

    private int convertDayToPointsColony(int nodeBirthday) {
        int age = nodeBirthday;
        return Math.min(age, SRPConfigWorld.colonyPointCap);
    }

    public int colonunumber() {
        return this.colonyA.size();
    }

    public int getColonyDistanceSpreadByPoints(int points, boolean sq, boolean effect) {
        if (effect) {
            int baseRange = SRPConfigWorld.colonyBaseEffectRadiusValue;
            int a = points / SRPConfigWorld.colonySpreadEffectPoint;
            int b = a * SRPConfigWorld.colonySpreadEffectValue + baseRange;
            if (sq) {
                return b * b;
            }
            return b;
        }
        int baseRange = SRPConfigWorld.colonyBaseRadiusValue;
        int a = points / SRPConfigWorld.colonySpreadPoint;
        int b = a * SRPConfigWorld.colonySpreadValue + baseRange;
        if (sq) {
            return b * b;
        }
        return b;
    }

    public int getColonyDistanceSpreadByPosition(BlockPos pos, boolean sq) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        for (int i = 0; i < this.colonyX.size(); ++i) {
            if (this.colonyX.get(i) != x || this.colonyY.get(i) != y || this.colonyZ.get(i) != z) continue;
            int ageDistance = this.convertDayToPointsColony(this.colonyA.get(i));
            ageDistance = this.getColonyDistanceSpreadByPoints(ageDistance, sq, false);
            return ageDistance;
        }
        return 0;
    }

    public BlockPos isInRangeOfColony(BlockPos pos, double distance) {
        distance += 1.0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            if (!(distanceSQ < distance * distance)) continue;
            return new BlockPos(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue());
        }
        return null;
    }

    public int totalColonyPoints(int currentDay) {
        int points = 0;
        for (int i = 0; i < this.colonyX.size(); ++i) {
            int age = this.convertDayToPointsColony(this.colonyA.get(i));
            points += age;
        }
        return Math.min(points, SRPConfigWorld.colonyTotalPointCap);
    }

    public void checkColonyExistance(World worldIn) {
        for (int i = this.colonyX.size() - 1; i >= 0; --i) {
            IBlockState state;
            BlockPos pos = new BlockPos(this.colonyX.get(i).intValue(), this.colonyY.get(i).intValue(), this.colonyZ.get(i).intValue());
            if (!WorldAccess.isAreaLoaded(worldIn, pos, 3) || ((state = BlockStates.get(worldIn, pos)).getBlock() == SRPBlocks.ColonyHeart || state.getBlock() == SRPBlocks.ColonyOutpost) && state.getPropertyKeys().contains(BlockColonyCore.ACTIVE) && (Integer)state.getValue((IProperty)BlockColonyCore.ACTIVE) > 0) continue;
            this.colonyX.remove(i);
            this.colonyY.remove(i);
            this.colonyZ.remove(i);
            this.colonyA.remove(i);
            this.markDirty();
        }
    }

    public void addGlobalResistance(String damage) {
        boolean flag = true;
        for (int i = 0; i < this.resistanceS.size(); ++i) {
            if (!this.resistanceS.get(i).equals(damage)) continue;
            int iiii = this.resistanceI.get(i) + 1;
            this.resistanceI.set(i, iiii);
            flag = false;
            break;
        }
        if (flag) {
            this.resistanceS.add(damage);
            this.resistanceI.add(1);
        }
        this.markDirty();
    }

    public String getMostCommonDamageS() {
        ArrayList<String> resistanceAAAS = this.resistanceS;
        ArrayList<Integer> resistanceAAAI = this.resistanceI;
        String atm = null;
        int times = 0;
        for (int i = 0; i < resistanceAAAI.size(); ++i) {
            if (resistanceAAAI.get(i) <= times) continue;
            times = resistanceAAAI.get(i);
            atm = resistanceAAAS.get(i);
        }
        return atm;
    }

    public int getMostCommonDamageI() {
        ArrayList<Integer> resistanceAAAI = this.resistanceI;
        int times = 0;
        for (int i = 0; i < resistanceAAAI.size(); ++i) {
            if (resistanceAAAI.get(i) <= times) continue;
            times = resistanceAAAI.get(i);
        }
        return times;
    }

    public void resetGlobalAdaptation() {
        this.resistanceI = new ArrayList();
        this.resistanceS = new ArrayList();
        this.markDirty();
    }

    public ArrayList<String> getAdaptationS() {
        return this.resistanceS;
    }

    public ArrayList<Integer> getAdaptationI() {
        return this.resistanceI;
    }

    public void updateDays(World world) {
        int atm;
        int i;
        SRPSaveData data = SRPSaveData.get(world, 2120);
        int count = data.getUpdateNumber(world.provider.dimensionId);
        if (count == 0) {
            return;
        }
        for (i = 0; i < this.nodeA.size(); ++i) {
            atm = this.nodeA.get(i) + count;
            this.nodeA.set(i, atm);
        }
        for (i = 0; i < this.colonyA.size(); ++i) {
            atm = this.colonyA.get(i) + count;
            this.colonyA.set(i, atm);
        }
        this.markDirty();
        this.updateOriginValues(world, count, data);
    }
}

