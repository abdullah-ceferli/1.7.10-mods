/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  org.apache.logging.log4j.Level
 */
package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteLoot;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityParasiteLoot;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

public abstract class WorldGenParasiteColonyBase
extends WorldGenParasiteGenAbstract {
    protected int type;
    protected IBlockState floor = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.DIRT));
    protected IBlockState tacle = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FEELER));
    protected IBlockState wall = BlockStates.defaultState(SRPBlocks.ParasiteRubbleDense).withProperty(BlockParasiteRubbleDense.VARIANT, (Comparable)((Object)BlockParasiteRubbleDense.EnumType.WALL));
    protected IBlockState floorColony = BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.FLESH));

    public WorldGenParasiteColonyBase(boolean notify, int stage) {
        super(notify);
        this.type = stage;
    }

    protected void placeBlock(World worldIn, BlockPos pos, IBlockState state) {
        this.setBlockAndNotifyAdequately(worldIn, pos, state);
    }

    protected void placeVine(World worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteBush).withProperty(BlockParasiteBush.VARIANT, (Comparable)((Object)BlockParasiteBush.EnumType.BINE)));
    }

    protected void placeReplacement(World worldIn, BlockPos pos, IBlockState in) {
        this.setBlockAndNotifyAdequately(worldIn, pos, in);
    }

    protected BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
        switch (direction) {
            case 0: {
                return center.north(times);
            }
            case 1: {
                return center.east(times);
            }
            case 2: {
                return center.south(times);
            }
        }
        return center.west(times);
    }

    protected BlockPos placeColumn(World worldIn, BlockPos pos, int in, Random rand, double extraChance, IBlockState state) {
        int current;
        int atm = current = pos.getY();
        int times = 0;
        BlockPos newPos = pos;
        while (current < atm + in) {
            this.placeBlock(worldIn, newPos, state);
            newPos = extraChance == 1.0 ? newPos.up() : newPos.down();
            ++current;
            ++times;
        }
        this.placeBlock(worldIn, newPos, state);
        return newPos;
    }

    protected BlockPos directionToGrow(BlockPos atm, int choice, boolean sideCurse) {
        if (sideCurse) {
            switch (choice) {
                case 0: {
                    atm = atm.north();
                    atm = atm.east();
                    break;
                }
                case 1: {
                    atm = atm.north();
                    atm = atm.west();
                    break;
                }
                case 10: {
                    atm = atm.east();
                    atm = atm.south();
                    break;
                }
                case 11: {
                    atm = atm.east();
                    atm = atm.north();
                    break;
                }
                case 20: {
                    atm = atm.south();
                    atm = atm.west();
                    break;
                }
                case 21: {
                    atm = atm.south();
                    atm = atm.east();
                    break;
                }
                case 30: {
                    atm = atm.west();
                    atm = atm.north();
                    break;
                }
                default: {
                    atm = atm.west();
                    atm = atm.south();
                }
            }
            return atm;
        }
        switch (choice) {
            case 0: {
                atm = atm.north();
                break;
            }
            case 1: {
                atm = atm.east();
                break;
            }
            case 3: {
                atm = atm.west();
                break;
            }
            default: {
                atm = atm.south();
            }
        }
        return atm;
    }

    protected void addVines(World worldIn, BlockPos position, Random rand, int longer) {
        if (BlockStates.get(worldIn, position).getBlock() != Blocks.air) {
            return;
        }
        this.placeVine(worldIn, position);
        int chance = longer;
        while (BlockStates.get(worldIn, position.down()).getBlock() == Blocks.air && BlockStates.get(worldIn, position.down(2)).getBlock() == Blocks.air) {
            if (rand.nextInt(chance) == 0) {
                return;
            }
            position = position.down();
            this.placeVine(worldIn, position);
            --chance;
        }
    }

    protected void replaceLayer(World worldIn, BlockPos position, int range, IBlockState toReplace, IBlockState in) {
        int xx = position.getX();
        int zz = position.getZ();
        int yy = position.getY();
        for (int x = xx - range; x <= xx + range; ++x) {
            for (int z = zz - range; z <= zz + range; ++z) {
                BlockPos neww = new BlockPos(x, yy, z);
                if (BlockStates.get(worldIn, neww) != toReplace) continue;
                this.placeReplacement(worldIn, neww, in);
            }
        }
    }

    protected void addFloor(World worldIn, BlockPos position, int range, boolean fill) {
        BlockPos atm;
        if (fill) {
            this.placeReplacement(worldIn, position, this.floorColony);
        }
        int offsetN = 2;
        BlockPos atm2 = position.north();
        while (!(BlockStates.get(worldIn, atm2).getBlock() instanceof BlockBase)) {
            this.placeReplacement(worldIn, atm2, this.floorColony);
            this.genFloorFloor(worldIn, atm2.down(), 15, fill);
            this.placeReplacement(worldIn, atm2.south(offsetN), this.floorColony);
            this.genFloorFloor(worldIn, atm2.south(offsetN).down(), 15, fill);
            atm2 = atm2.north();
            offsetN += 2;
        }
        BlockPos here = position.east(1);
        while (!(BlockStates.get(worldIn, here).getBlock() instanceof BlockBase)) {
            int offsetN2 = 2;
            atm = here.north();
            while (!(BlockStates.get(worldIn, atm).getBlock() instanceof BlockBase)) {
                this.placeReplacement(worldIn, atm, this.floorColony);
                this.genFloorFloor(worldIn, atm.down(), 15, fill);
                this.placeReplacement(worldIn, atm.south(offsetN2), this.floorColony);
                this.genFloorFloor(worldIn, atm.south(offsetN2).down(), 15, fill);
                atm = atm.north();
                offsetN2 += 2;
            }
            this.placeReplacement(worldIn, here, this.floorColony);
            this.genFloorFloor(worldIn, here.down(), 15, fill);
            here = here.east();
        }
        here = position.west(1);
        while (!(BlockStates.get(worldIn, here).getBlock() instanceof BlockBase)) {
            int offsetN3 = 2;
            atm = here.north();
            while (!(BlockStates.get(worldIn, atm).getBlock() instanceof BlockBase)) {
                this.placeReplacement(worldIn, atm, this.floorColony);
                this.genFloorFloor(worldIn, atm.down(), 15, fill);
                this.placeReplacement(worldIn, atm.south(offsetN3), this.floorColony);
                this.genFloorFloor(worldIn, atm.south(offsetN3).down(), 15, fill);
                atm = atm.north();
                offsetN3 += 2;
            }
            this.placeReplacement(worldIn, here, this.floorColony);
            this.genFloorFloor(worldIn, here.down(), 15, fill);
            here = here.west();
        }
        if (!fill) {
            this.addFloorSpace(worldIn, position);
        }
    }

    protected void genFloorFloor(World worldIn, BlockPos position, int range, boolean fill) {
        if (fill) {
            BlockPos filler = position;
            while ((BlockStates.get(worldIn, filler).getBlock() == Blocks.air || BlockStates.get(worldIn, filler).getBlock() == SRPBlocks.ParasiteBush || BlockStates.get(worldIn, filler).getBlock() instanceof BlockLeaves || BlockStates.get(worldIn, filler).getBlock() instanceof BlockBush) && range > 0) {
                this.placeReplacement(worldIn, filler, this.floor);
                filler = filler.down();
                --range;
            }
        }
    }

    protected void addFloorSpace(World worldIn, BlockPos position) {
        int xx = position.getX();
        int zz = position.getZ();
        int yy = position.getY();
        int range = 1;
        for (int x = xx - range; x <= xx + range; ++x) {
            for (int z = zz - range; z <= zz + range; ++z) {
                BlockPos neww = new BlockPos(x, yy, z);
                if (BlockStates.get(worldIn, neww) != this.floorColony) continue;
                this.placeReplacement(worldIn, neww, BlockStates.defaultState(Blocks.air));
            }
        }
    }

    protected void addMobSpawner(World worldIn, BlockPos position, int type, double chance) {
    }

    protected void generateSphere(World worldIn, BlockPos posss, int innerHeight, int outerHeight, Random rand, boolean invertedTip, int valueStarting, boolean random, int heightBelow, int heightAbove, int tip, IBlockState state1, IBlockState state2, IBlockState state3, int incomplete) {
        int heig;
        int xx = valueStarting;
        int zz = valueStarting;
        int test = innerHeight;
        int ticc = 2;
        while (test > 0) {
            --test;
            heig = heightBelow;
            while (heig > 0) {
                --heig;
                this.generateCircle(state1, state2, worldIn, worldIn.rand, posss, xx, zz, 1, incomplete, 6);
                this.generateCircle(state3, state3, worldIn, worldIn.rand, posss, xx - ticc, zz - ticc, 1, 50000, 6);
                posss = posss.up();
            }
            xx = rand.nextBoolean() && random ? (xx += 2) : ++xx;
            if (rand.nextBoolean() && random) {
                zz += 2;
                continue;
            }
            ++zz;
        }
        test = outerHeight;
        while (test > 0) {
            --test;
            this.generateCircle(state1, state2, worldIn, worldIn.rand, posss, xx, zz, 1, incomplete, 0);
            this.generateCircle(state3, state3, worldIn, worldIn.rand, posss, xx - 2, zz - 2, 1, 50000, 0);
            posss = posss.up();
        }
        test = valueStarting + tip;
        if (invertedTip) {
            tip = 0;
            heightAbove = (int)((double)heightAbove * 0.5);
        }
        while (test > 0) {
            --test;
            heig = heightAbove;
            while (heig > 0) {
                --heig;
                this.generateCircle(state1, state2, worldIn, worldIn.rand, posss, xx, zz, 1, incomplete, invertedTip ? 9 : 0);
                this.generateCircle(state3, state3, worldIn, worldIn.rand, posss, xx - ticc, zz - ticc, 1, 50000, invertedTip ? 9 : 0);
                posss = posss.up();
            }
            if (invertedTip) {
                xx = rand.nextBoolean() && random ? (xx += 2) : ++xx;
                if (rand.nextBoolean() && random) {
                    zz += 2;
                    continue;
                }
                ++zz;
                continue;
            }
            xx = rand.nextBoolean() && random ? (xx -= 2) : --xx;
            if (rand.nextBoolean() && random) {
                zz -= 2;
                continue;
            }
            --zz;
        }
    }

    protected boolean generateCircle(IBlockState state, IBlockState state2, World world, Random rand, BlockPos pos, int radiusX, int radiusZ, int height, int incomplete, int veins) {
        if (pos.getY() <= 2 || pos.getY() >= 240) {
            return false;
        }
        for (int y = 0; y < height; ++y) {
            BlockPos layerPos = pos.up(y);
            for (int x = -radiusX; x <= radiusX; ++x) {
                for (int z = -radiusZ; z <= radiusZ; ++z) {
                    boolean flagAir;
                    double normalizedX = (double)x / (double)radiusX;
                    double normalizedZ = (double)z / (double)radiusZ;
                    if (!(normalizedX * normalizedX + normalizedZ * normalizedZ <= 1.0)) continue;
                    BlockPos newBlockPos = layerPos.add(x, 0, z);
                    boolean bl = flagAir = state.getBlock() == Blocks.air && state2.getBlock() == Blocks.air;
                    if (BlockStates.get(world, newBlockPos).getBlock() != Blocks.air && !(BlockStates.get(world, newBlockPos).getBlock() instanceof BlockBase) && !flagAir || rand.nextInt(incomplete) == 0) continue;
                    if ((x == radiusX || z == radiusZ || x == -radiusX || z == -radiusZ || x + 1 == radiusX || z + 1 == radiusZ || x - 1 == -radiusX || z - 1 == -radiusZ) && rand.nextInt(60) == 0) {
                        if (rand.nextInt(4) == 0) {
                            if (flagAir) continue;
                            BlockStates.set(world, newBlockPos, BlockStates.defaultState(SRPBlocks.DeadBlood));
                            if (veins <= 0) continue;
                            this.addVines(world, newBlockPos.down(), rand, veins);
                            continue;
                        }
                        if (flagAir) continue;
                        if (rand.nextInt(10) == 0) {
                            this.placeLoot(world, newBlockPos, SRPConfigWorld.blockLootRare, BlockStates.defaultState(SRPBlocks.ParasiteLoot).withProperty(BlockParasiteLoot.VARIANT, (Comparable)((Object)BlockParasiteLoot.EnumType.RARE)));
                        } else if (rand.nextInt(4) == 0) {
                            this.placeLoot(world, newBlockPos, SRPConfigWorld.blockLootUncommon, BlockStates.defaultState(SRPBlocks.ParasiteLoot).withProperty(BlockParasiteLoot.VARIANT, (Comparable)((Object)BlockParasiteLoot.EnumType.UNCOMMON)));
                        } else {
                            this.placeLoot(world, newBlockPos, SRPConfigWorld.blockLootCommon, BlockStates.defaultState(SRPBlocks.ParasiteLoot).withProperty(BlockParasiteLoot.VARIANT, (Comparable)((Object)BlockParasiteLoot.EnumType.COMMON)));
                        }
                        if (veins <= 0) continue;
                        this.addVines(world, newBlockPos.down(), rand, veins);
                        continue;
                    }
                    if (rand.nextBoolean()) {
                        BlockStates.set(world, newBlockPos, state);
                        if (veins <= 0 || flagAir) continue;
                        this.addVines(world, newBlockPos.down(), rand, veins);
                        continue;
                    }
                    BlockStates.set(world, newBlockPos, state2);
                    if (veins <= 0 || flagAir) continue;
                    this.addVines(world, newBlockPos.down(), rand, veins);
                }
            }
        }
        return true;
    }

    protected void placeLoot(World world, BlockPos pos, String[] list, IBlockState state) {
        BlockStates.set(world, pos, state);
        TileEntity tileentity = WorldAccess.getTileEntity(world, pos);
        if (tileentity instanceof TileEntityParasiteLoot) {
            TileEntityParasiteLoot cyst = (TileEntityParasiteLoot)tileentity;
            for (int i = 0; i < cyst.getSizeInventory(); ++i) {
                if (world.rand.nextInt(2) == 0) continue;
                cyst.setInventorySlotContents(i, new ItemStack(this.loot(world.rand, list)));
            }
        }
    }

    private Item loot(Random rand, String[] drop) {
        try {
            Item item;
            if (drop.length != 0 && (item = Item.setTextureName((String)drop[rand.nextInt(drop.length)])) != null) {
                return item;
            }
        }
        catch (Exception e) {
            SRPMain.logger.log(Level.ERROR, "Problem with loot event", (Throwable)e);
        }
        return null;
    }

    protected boolean generateDNAHelix(IBlockState state, World world, Random rand, BlockPos pos, double radius, int numTurns, double pitch) {
        if (pos.getY() <= 2 || pos.getY() >= 240) {
            return false;
        }
        double tStep = 0.1;
        for (double t = 0.0; t <= (double)(numTurns * 2) * Math.PI; t += tStep) {
            double yOffset = pitch / (Math.PI * 2) * t;
            int y = pos.getY() + (int)Math.round(yOffset);
            int x1 = pos.getX() + (int)Math.round(radius * Math.cos(t));
            int z1 = pos.getZ() + (int)Math.round(radius * Math.sin(t));
            int x2 = pos.getX() + (int)Math.round(radius * Math.cos(t + Math.PI));
            int z2 = pos.getZ() + (int)Math.round(radius * Math.sin(t + Math.PI));
            BlockPos pos1 = new BlockPos(x1, y, z1);
            BlockPos pos2 = new BlockPos(x2, y, z2);
            BlockStates.set(world, pos1, state);
            BlockStates.set(world, pos2, state);
            if ((int)Math.round(t / Math.PI) % 2 != 0) continue;
            int midX = (x1 + x2) / 2;
            int midZ = (z1 + z2) / 2;
            BlockPos blockPos = new BlockPos(midX, y, midZ);
        }
        return true;
    }

    public BlockPos getCirclePoint(BlockPos center, int radius, double theta) {
        int x = center.getX() + (int)Math.round(Math.cos(theta) * (double)radius);
        int z = center.getZ() + (int)Math.round(Math.sin(theta) * (double)radius);
        return new BlockPos(x, center.getY(), z);
    }

    public List<BlockPos> getCirclePoints(BlockPos center, int radius, int steps) {
        ArrayList<BlockPos> points = new ArrayList<BlockPos>();
        double cx = center.getX();
        double cz = center.getZ();
        double cy = center.getY();
        for (int i = 0; i < steps; ++i) {
            double theta = Math.PI * 2 * (double)i / (double)steps;
            int x = center.getX() + (int)Math.round(Math.cos(theta) * (double)radius);
            int z = center.getZ() + (int)Math.round(Math.sin(theta) * (double)radius);
            points.add(new BlockPos(x, (int)cy, z));
        }
        return points;
    }

    public void generatePillar(World world, BlockPos basePos, int height, IBlockState blockState, IBlockState blockState2) {
        for (int dy = 0; dy < height; ++dy) {
            BlockPos pos = basePos.up(dy);
            if (world.rand.nextBoolean()) {
                BlockStates.set(world, pos, blockState);
                continue;
            }
            BlockStates.set(world, pos, blockState2);
        }
    }

    public void replaceCircleGround(World world, BlockPos center, int radius, IBlockState targetBlock) {
        int cx = center.getX();
        int cz = center.getZ();
        int cy = center.getY();
        if (cy <= 2 || cy >= 240) {
            return;
        }
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                BlockPos pos;
                IBlockState current;
                if (dx * dx + dz * dz > radius * radius || (current = BlockStates.get(world, pos = new BlockPos(cx + dx, cy, cz + dz))).getBlock() instanceof BlockBase || current.getBlock() == Blocks.air) continue;
                BlockStates.set(world, pos, targetBlock);
            }
        }
    }

    public void generateVerticalCircle(World world, BlockPos center, int radius, boolean useXZPlane) {
        double step = Math.PI / (double)(radius * 4);
        for (double theta = 0.0; theta < Math.PI * 2; theta += step) {
            int dy = (int)Math.round((double)radius * Math.sin(theta));
            int dPrimary = (int)Math.round((double)radius * Math.cos(theta));
            BlockPos target = useXZPlane ? center.add(dPrimary, dy, 0) : center.add(0, dy, dPrimary);
            BlockStates.set(world, target, BlockStates.defaultState(Blocks.stone));
        }
    }

    public void generateFilledVerticalDisk(World world, BlockPos center, int radius, boolean useXZPlane) {
        int rSq = radius * radius;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                BlockPos target;
                if (dx * dx + dy * dy > rSq || BlockStates.get(world, target = useXZPlane ? center.add(dx, dy, 0) : center.add(0, dy, dx)).getBlock() == SRPBlocks.DeadBlood || BlockStates.get(world, target).getBlock() == SRPBlocks.ParasiteBush) continue;
                BlockStates.set(world, target, BlockStates.defaultState(Blocks.air));
            }
        }
    }

    public void addEntrance(World worldIn, Random rand, BlockPos position, int entrance) {
        int direction = rand.nextInt(4);
        int offset = 3;
        while (entrance > 0) {
            if (offset > 0) {
                position = this.directionToGrow(position, direction, false);
                --offset;
                continue;
            }
            --entrance;
            position = this.directionToGrow(position, direction, false);
            this.generateFilledVerticalDisk(worldIn, position.up(1), 3, direction != 1 && direction != 3);
        }
    }
}

