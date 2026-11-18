/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockSand
 *  net.minecraft.block.BlockSandStone
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.init.Blocks
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.convert.HarlequinBlockConverter;
import java.util.HashSet;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandHarlequinConvert
extends CommandBase {
    public String getCommandName() {
        return "harlequin_convert";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/harlequin_convert [radius] [sampleChance] [depth] [blotchChance] [blotchMinDiam] [blotchMaxDiam]";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int z;
        int x;
        int dz;
        int dx;
        World world = sender.getEntityWorld();
        if (world.isRemote) {
            sender.addChatMessage((IChatComponent)new ChatComponentText("[HarlequinConvert] (client) \u2013 no changes made"));
            return;
        }
        int radius = args.length >= 1 ? CommandHarlequinConvert.parseInt((String)args[0], (int)1, (int)256) : 16;
        int sampleChance = args.length >= 2 ? CommandHarlequinConvert.parseInt((String)args[1], (int)1, (int)128) : 1;
        int depth = args.length >= 3 ? CommandHarlequinConvert.parseInt((String)args[2], (int)1, (int)256) : 32;
        int blotchChance = args.length >= 4 ? CommandHarlequinConvert.parseInt((String)args[3], (int)1, (int)256) : 18;
        int blotchMinDiam = args.length >= 5 ? CommandHarlequinConvert.parseInt((String)args[4], (int)1, (int)64) : 3;
        int blotchMaxDiam = args.length >= 6 ? CommandHarlequinConvert.parseInt((String)args[5], (int)1, (int)64) : 5;
        BlockPos center = sender.getPosition();
        Random rand = world.rand;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(center);
        HarlequinBlockConverter.Config cfg = new HarlequinBlockConverter.Config(SRPBlocks.HarleskinnBlock, SRPBlocks.HarlequinnGrass, SRPBlocks.Alveoli, SRPBlocks.AlveoliGrowth, SRPBlocks.LipomaMass, SRPBlocks.TressesHair, SRPBlocks.HirsuteHair);
        int changed = 0;
        HashSet<Long> blotchXZ = new HashSet<Long>();
        int blotchesSeeded = 0;
        int blotchCellsMarked = 0;
        int blotchCellsConverted = 0;
        for (dx = -radius; dx <= radius; ++dx) {
            for (dz = -radius; dz <= radius; ++dz) {
                boolean seedThisColumn;
                x = center.getX() + dx;
                z = center.getZ() + dz;
                boolean bl = seedThisColumn = blotchChance <= 1 || rand.nextInt(Math.max(2, blotchChance)) == 0;
                if (!seedThisColumn) continue;
                int minD = Math.min(blotchMinDiam, blotchMaxDiam);
                int maxD = Math.max(blotchMinDiam, blotchMaxDiam);
                int diameter = minD + rand.nextInt(maxD - minD + 1);
                int r = diameter / 2;
                boolean anyMarked = false;
                for (int ox = -r; ox <= r; ++ox) {
                    for (int oz = -r; oz <= r; ++oz) {
                        boolean wouldBecomeHarlequinn;
                        int sz;
                        int sx;
                        int ySurf;
                        if (ox * ox + oz * oz > r * r || (ySurf = WorldAccess.getHeight(world, new BlockPos(sx = x + ox, 0, sz = z + oz)).getY() - 1) < 0) continue;
                        BlockPos top = new BlockPos(sx, ySurf, sz);
                        IBlockState sTop = BlockStates.get(world, top);
                        Block topB = sTop.getBlock();
                        boolean bl2 = wouldBecomeHarlequinn = WorldAccess.canSeeSky(world, top.up()) && (sTop.getBlock() instanceof BlockSand || sTop.getBlock() instanceof BlockSandStone || topB == Blocks.RED_SANDSTONE || topB == Blocks.stone);
                        if (topB != Blocks.grass && !wouldBecomeHarlequinn || !blotchXZ.add(CommandHarlequinConvert.keyXZ(sx, sz))) continue;
                        ++blotchCellsMarked;
                        anyMarked = true;
                    }
                }
                if (!anyMarked) continue;
                ++blotchesSeeded;
            }
        }
        for (dx = -radius; dx <= radius; ++dx) {
            for (dz = -radius; dz <= radius; ++dz) {
                x = center.getX() + dx;
                int yTop = WorldAccess.getHeight(world, new BlockPos(x, 0, z = center.getZ() + dz)).getY() - 1;
                if (yTop < 0) continue;
                for (int y = yTop; y >= Math.max(5, yTop - depth); --y) {
                    BlockPos below;
                    Block replacedWith;
                    Material mat;
                    pos.setPos(x, y, z);
                    IBlockState state = BlockStates.get(world, (BlockPos)pos);
                    Block b = state.getBlock();
                    boolean blotchHere = blotchXZ.contains(CommandHarlequinConvert.keyXZ(x, z));
                    if (!blotchHere && !(b instanceof BlockLeaves) && rand.nextInt(Math.max(1, sampleChance)) != 0 || (mat = state.getMaterial()) == Material.air || mat == Material.water || mat == Material.lava || (replacedWith = HarlequinBlockConverter.convert(world, (BlockPos)pos, state, blotchHere, rand, cfg)) == null) continue;
                    if (replacedWith != cfg.ALVEOLI && rand.nextInt(100) < 30 && WorldAccess.isAirBlock(world, below = pos.down())) {
                        BlockStates.set(world, below, BlockStates.defaultState(cfg.LIPOMA), 2);
                    }
                    ++changed;
                    if (!blotchHere || state.getBlock() != Blocks.grass || replacedWith != cfg.HARLESKINN) continue;
                    ++blotchCellsConverted;
                }
            }
        }
        sender.addChatMessage((IChatComponent)new ChatComponentText("Harlequin convert: changed ~" + changed + " | blotches: " + blotchesSeeded + " | cells marked: " + blotchCellsMarked + " | cells converted: " + blotchCellsConverted + " | radius " + radius + " | 1-in-" + sampleChance + " | depth " + depth + " | blotchChance 1-in-" + Math.max(1, blotchChance) + " | blotchDiam " + Math.min(blotchMinDiam, blotchMaxDiam) + ".." + Math.max(blotchMinDiam, blotchMaxDiam)));
    }

    private static long keyXZ(int x, int z) {
        return (long)x << 32 ^ (long)z & 0xFFFFFFFFL;
    }
}

