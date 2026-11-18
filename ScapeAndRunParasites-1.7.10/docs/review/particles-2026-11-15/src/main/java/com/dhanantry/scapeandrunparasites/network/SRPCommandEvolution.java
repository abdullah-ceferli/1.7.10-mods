/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class SRPCommandEvolution
implements ICommand {
    private final List aliases = new ArrayList();

    private static void msg(ICommandSender sender, String key, Object ... args) {
        sender.addChatMessage((IChatComponent)new ChatComponentTranslation(key, args));
    }

    public SRPCommandEvolution() {
        this.aliases.add("srpevolution");
    }

    public int compareTo(ICommand arg0) {
        return 0;
    }

    public String getCommandName() {
        return "srpevolution";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "srpevolution <text>";
    }

    public List<String> getCommandAliases() {
        return this.aliases;
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] argString) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) {
            if (!SRPConfigSystems.useEvolution) {
                SRPCommandEvolution.msg(sender, "command.srpevolution.error.evolution_disabled", new Object[0]);
                return;
            }
            if (argString.length == 0) {
                SRPCommandEvolution.msg(sender, "command.srpevolution.error.invalid_argument", new Object[0]);
                return;
            }
            if (argString[0].equals("set_evolutionloss")) {
                SRPSaveData data = SRPSaveData.get(world, 57);
                if (argString[1].equals("true")) {
                    data.setLoss(true, world.provider.dimensionId);
                    SRPCommandEvolution.msg(sender, "command.srpevolution.evolutionloss.set.true", new Object[0]);
                } else if (argString[1].equals("false")) {
                    data.setLoss(false, world.provider.dimensionId);
                    SRPCommandEvolution.msg(sender, "command.srpevolution.evolutionloss.set.false", new Object[0]);
                } else {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.error.invalid_arg", new Object[0]);
                }
                return;
            }
            if (argString[0].equals("set_evolutiongaining")) {
                SRPSaveData data = SRPSaveData.get(world, 58);
                if (argString[1].equals("true")) {
                    data.setGaining(true, world.provider.dimensionId);
                    SRPCommandEvolution.msg(sender, "command.srpevolution.evolutiongaining.set.true", new Object[0]);
                } else if (argString[1].equals("false")) {
                    data.setGaining(false, world.provider.dimensionId);
                    SRPCommandEvolution.msg(sender, "command.srpevolution.evolutiongaining.set.false", new Object[0]);
                } else {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.error.invalid_arg", new Object[0]);
                }
                return;
            }
            if (argString[0].equals("getphase")) {
                int x;
                List serverList = EntityQueries.loaded(world);
                int count = 0;
                int coth = 0;
                for (x = 0; x < serverList.size(); ++x) {
                    if (!(serverList.get(x) instanceof EntityParasiteBase)) continue;
                    if (serverList.get(x) instanceof EntityCanHaveBodies) {
                        EntityCanHaveBodies bodies = (EntityCanHaveBodies)serverList.get(x);
                        if (bodies.getBodyNumber() != 0) continue;
                        ++count;
                        continue;
                    }
                    ++count;
                }
                for (x = 0; x < serverList.size(); ++x) {
                    if (!(serverList.get(x) instanceof EntityLivingBase) || !((EntityLivingBase)serverList.get(x)).isPotionActive(SRPPotions.COTH_E)) continue;
                    ++coth;
                }
                SRPSaveData data = SRPSaveData.get(world, 59);
                int players = EntityQueries.players(world).size();
                players *= SRPConfig.worldMobCapPlusPlayer;
                int id = world.provider.dimensionId;
                ArrayList<Integer> halo = SRPWorldData.get(world).getorigins("x");
                String[] here = new String[20];
                here = data.getCurrentCodeU(id).split(";");
                String atm = "";
                for (int k = 0; k < here.length; ++k) {
                    atm = k == here.length - 1 ? atm + here[k] : atm + here[k] + " ";
                }
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.header", new Object[0]);
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.dimension", world.provider.dimensionId);
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.phase", data.getEvolutionPhase(id));
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.total_points", data.getTotalKills(id));
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.points_next", SRPCommandEvolution.getNeededPoints((byte)(data.getEvolutionPhase(id) + 1)));
                double progress = (double)data.getTotalKills(id) / (double)SRPCommandEvolution.getNeededPoints((byte)(data.getEvolutionPhase(id) + 1)) * 100.0;
                String progStr = String.format(Locale.ROOT, "%.1f", progress);
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.progress", progStr);
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.cooldown", data.getCooldown(world, id));
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.gaining", String.valueOf(data.getCanGain(id)));
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.loss", String.valueOf(data.getCanLoss(id)));
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.dislodgement", new Object[0]);
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.dislodgement.value", atm);
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.mobcap", SRPConfig.worldMobCap + players);
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.generation", data.getGeneration(id));
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.gen_ticks", data.getGenerationNeededTime(world, id));
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.udl", data.getDeveLevel());
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.parasite_count", count);
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.coth_count", coth);
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.eivs", halo == null ? 0 : halo.size());
                if (sender instanceof EntityPlayer) {
                    boolean flag;
                    EntityPlayer playerIn = (EntityPlayer)sender;
                    BlockPos pos = SRPWorldData.get(world).nearestInfectionPosition(false, EntityQueries.position(playerIn));
                    boolean bl = flag = pos != null;
                    if (flag) {
                        SRPCommandEvolution.msg(sender, " -> WITHIN VECTOR: " + flag, new Object[0]);
                    } else if (halo.size() > 0) {
                        pos = SRPWorldData.get(world).nearestInfectionPosition(true, EntityQueries.position(playerIn));
                        int dis = (int)playerIn.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                        int area = SRPWorldData.get(world).nearestInfectionValueArea(EntityQueries.position(playerIn), false);
                        SRPCommandEvolution.msg(sender, " -> WITHIN VECTOR: " + flag, new Object[0]);
                        SRPCommandEvolution.msg(sender, " -> Distance to nearest EIV: " + (dis - area), new Object[0]);
                    }
                }
                SRPCommandEvolution.msg(sender, "command.srpevolution.getphase.footer", new Object[0]);
                return;
            }
            if (argString[0].equals("evolutionlock_reset")) {
                SRPSaveData data = SRPSaveData.get(world, 60);
                data.resetLock();
                SRPCommandEvolution.msg(sender, "command.srpevolution.evolutionlock.reset", new Object[0]);
                return;
            }
            if (argString[0].equals("evolutionlock_unlockall")) {
                SRPSaveData data = SRPSaveData.get(world, 60);
                data.unlockAllParasite();
                SRPCommandEvolution.msg(sender, "command.srpevolution.evolutionlock.unlockall", new Object[0]);
                return;
            }
            if (argString[0].equals("evolutionlock_getlist")) {
                SRPSaveData data = SRPSaveData.get(world, 65);
                List<Integer> paraIds = data.getLockedList();
                StringBuilder sb = new StringBuilder();
                for (Integer x : paraIds) {
                    sb.append(x).append(" ");
                }
                SRPCommandEvolution.msg(sender, "command.srpevolution.evolutionlock.getlist", sb.toString());
                return;
            }
            if (argString[0].equals("addpoints")) {
                try {
                    String nani = argString[1];
                    int paraIds = Integer.parseInt(nani);
                }
                catch (NumberFormatException nfe) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.error.invalid_arg", new Object[0]);
                    return;
                }
                SRPSaveData data = SRPSaveData.get(world, 61);
                int id = world.provider.dimensionId;
                if (data.getEvolutionPhase(id) == -2) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.addpoints.error.phase_minus2", new Object[0]);
                    return;
                }
                if (data.getCooldown(world, id) != 0) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.addpoints.error.cooldown", new Object[0]);
                    return;
                }
                String nani = argString[1];
                int option = Integer.parseInt(nani);
                if (!data.getCanGain(id) && option > 0) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.addpoints.error.cannot_gain", new Object[0]);
                    return;
                }
                if (!data.getCanLoss(id) && option < 0) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.addpoints.error.cannot_lose", new Object[0]);
                    return;
                }
                boolean flagg = data.setTotalKills(world.provider.dimensionId, option, true, world, true, true, 47);
                if (flagg) {
                    if (Math.abs(option) == 1) {
                        SRPCommandEvolution.msg(sender, "command.srpevolution.addpoints.success.one", option);
                    } else {
                        SRPCommandEvolution.msg(sender, "command.srpevolution.addpoints.success.many", option);
                    }
                    return;
                }
                SRPCommandEvolution.msg(sender, "command.srpevolution.error.generic", new Object[0]);
                return;
            }
            if (argString[0].equals("setcooldown")) {
                String nani;
                try {
                    nani = argString[1];
                    int id = Integer.parseInt(nani);
                }
                catch (NumberFormatException nfe) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.error.invalid_arg", new Object[0]);
                    return;
                }
                nani = argString[1];
                int option = Integer.parseInt(nani);
                if (option < 0) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.cooldown.error.negative", new Object[0]);
                    return;
                }
                SRPSaveData data = SRPSaveData.get(world, 62);
                SRPCommandEvolution.msg(sender, "command.srpevolution.cooldown.set", option);
                data.setCooldown(option, world, world.provider.dimensionId, false);
                return;
            }
            if (argString[0].equals("addcooldown")) {
                int option;
                String nani;
                try {
                    nani = argString[1];
                    option = Integer.parseInt(nani);
                }
                catch (NumberFormatException nfe) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.error.invalid_arg", new Object[0]);
                    return;
                }
                nani = argString[1];
                option = Integer.parseInt(nani);
                if (option < 0) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.cooldown.error.negative", new Object[0]);
                    return;
                }
                SRPSaveData data = SRPSaveData.get(world, 64);
                SRPCommandEvolution.msg(sender, "command.srpevolution.cooldown.set", option);
                data.setCooldown(option, world, world.provider.dimensionId, true);
                return;
            }
            if (argString[0].equals("setphase")) {
                int option;
                String nani;
                try {
                    nani = argString[1];
                    option = Integer.parseInt(nani);
                }
                catch (NumberFormatException nfe) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.error.invalid_arg", new Object[0]);
                    return;
                }
                nani = argString[1];
                option = Integer.parseInt(nani);
                SRPSaveData data = SRPSaveData.get(world, 63);
                if (option >= 11) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.setphase.error.phase_too_high", new Object[0]);
                    return;
                }
                if (option <= -3) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.setphase.error.phase_too_low", new Object[0]);
                    return;
                }
                if (option == -1) {
                    data.setEvolutionPhase(world.provider.dimensionId, (byte)option, true, world);
                    data.setTotalKills(world.provider.dimensionId, 100 * option, false, world, true, 48);
                } else if (option == -2) {
                    data.setTotalKills(world.provider.dimensionId, 100 * option, false, world, true, 49);
                    data.setEvolutionPhase(world.provider.dimensionId, (byte)option, true, world);
                } else {
                    data.setEvolutionPhase(world.provider.dimensionId, (byte)option, true, world);
                }
                if (argString.length == 2) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.setphase.changed_phase", argString[1]);
                    return;
                }
                try {
                    nani = argString[2];
                    option = Integer.parseInt(nani);
                }
                catch (NumberFormatException nfe) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.setphase.error.invalid_arg_evo", new Object[0]);
                    return;
                }
                nani = argString[2];
                option = Integer.parseInt(nani);
                if (option >= 6) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.setphase.changed_phase", argString[1]);
                    SRPCommandEvolution.msg(sender, "command.srpevolution.setphase.error.generation_too_high", new Object[0]);
                    return;
                }
                if (option <= -1) {
                    SRPCommandEvolution.msg(sender, "command.srpevolution.setphase.changed_phase", argString[1]);
                    SRPCommandEvolution.msg(sender, "command.srpevolution.setphase.error.generation_too_low", new Object[0]);
                    return;
                }
                data.setGeneration((byte)option, world.provider.dimensionId);
                data.setGenerationTime(0, world.provider.dimensionId);
                SRPCommandEvolution.msg(sender, "command.srpevolution.setphase.changed_phase", argString[1]);
                SRPCommandEvolution.msg(sender, "command.srpevolution.setphase.changed_generation", argString[2]);
                return;
            }
            SRPCommandEvolution.msg(sender, "command.srpevolution.error.invalid_command", new Object[0]);
        }
    }

    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canCommandSenderUseCommand(2, this.getCommandName());
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        for (int i = 0; i < args.length; ++i) {
        }
        ArrayList<String> atm = new ArrayList<String>();
        atm.add("evolutionlock_reset");
        atm.add("evolutionlock_unlockall");
        atm.add("evolutionlock_getlist");
        atm.add("toggle_evolutiongaining");
        atm.add("toggle_evolutionloss");
        atm.add("addpoints");
        atm.add("setcooldown");
        atm.add("getphase");
        atm.add("setphase");
        atm.add("addcooldown");
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord((String[])args, atm);
        }
        return atm;
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    public static int getNeededPoints(byte in) {
        switch (in) {
            case 1: {
                return SRPConfigSystems.phaseKillsOne;
            }
            case 2: {
                return SRPConfigSystems.phaseKillsTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseKillsThree;
            }
            case 4: {
                return SRPConfigSystems.phaseKillsFour;
            }
            case 5: {
                return SRPConfigSystems.phaseKillsFive;
            }
            case 6: {
                return SRPConfigSystems.phaseKillsSix;
            }
            case 7: {
                return SRPConfigSystems.phaseKillsSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseKillsEight;
            }
            case 9: {
                return SRPConfigSystems.phaseKillsNine;
            }
            case 10: {
                return SRPConfigSystems.phaseKillsTen;
            }
        }
        return 0;
    }

    public static int getVectorPointCap(int in) {
        switch (in) {
            case -1: {
                return SRPConfigSystems.phaseVectorPointCapZero / 2;
            }
            case 0: {
                return SRPConfigSystems.phaseVectorPointCapZero;
            }
            case 1: {
                return SRPConfigSystems.phaseVectorPointCapOne;
            }
            case 2: {
                return SRPConfigSystems.phaseVectorPointCapTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseVectorPointCapThree;
            }
            case 4: {
                return SRPConfigSystems.phaseVectorPointCapFour;
            }
            case 5: {
                return SRPConfigSystems.phaseVectorPointCapFive;
            }
            case 6: {
                return SRPConfigSystems.phaseVectorPointCapSix;
            }
            case 7: {
                return SRPConfigSystems.phaseVectorPointCapSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseVectorPointCapEight;
            }
            case 9: {
                return SRPConfigSystems.phaseVectorPointCapNine;
            }
        }
        return Integer.MAX_VALUE;
    }

    public static int getVectorHealthBonus(int in) {
        switch (in) {
            case -1: {
                return SRPConfigSystems.phaseVectorMultBonusMinusOne;
            }
            case 0: {
                return SRPConfigSystems.phaseVectorMultBonusZero;
            }
            case 1: {
                return SRPConfigSystems.phaseVectorMultBonusOne;
            }
            case 2: {
                return SRPConfigSystems.phaseVectorMultBonusTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseVectorMultBonusThree;
            }
            case 4: {
                return SRPConfigSystems.phaseVectorMultBonusFour;
            }
            case 5: {
                return SRPConfigSystems.phaseVectorMultBonusFive;
            }
            case 6: {
                return SRPConfigSystems.phaseVectorMultBonusSix;
            }
            case 7: {
                return SRPConfigSystems.phaseVectorMultBonusSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseVectorMultBonusEight;
            }
            case 9: {
                return SRPConfigSystems.phaseVectorMultBonusNine;
            }
        }
        return SRPConfigSystems.phaseVectorMultBonusTen;
    }
}

