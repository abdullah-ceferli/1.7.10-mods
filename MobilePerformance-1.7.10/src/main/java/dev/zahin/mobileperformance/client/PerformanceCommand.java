package dev.zahin.mobileperformance.client;

import java.util.*;
import net.minecraft.command.*;
import net.minecraft.util.ChatComponentText;

public final class PerformanceCommand extends CommandBase {
    private final ClientRuntime runtime;
    public PerformanceCommand(ClientRuntime runtime){this.runtime=runtime;}
    public String getCommandName(){return "mobileperf";}
    public String getCommandUsage(ICommandSender s){return "/mobileperf <status|preset|restore|on|off>";}
    public int getRequiredPermissionLevel(){return 0;}
    public boolean canCommandSenderUseCommand(ICommandSender s){return true;}
    private void say(ICommandSender s,String text){s.addChatMessage(new ChatComponentText(text));}
    public void processCommand(ICommandSender sender,String[] args){String action=args.length==0?"status":args[0].toLowerCase(Locale.ROOT);try{
        if(action.equals("on")){runtime.config.enabled=true;runtime.config.save();runtime.resetCounters();say(sender,"Mobile Performance rendering budgets enabled.");}
        else if(action.equals("off")){runtime.config.enabled=false;runtime.config.save();runtime.resetCounters();runtime.preset.restore(runtime.config);say(sender,"Mobile Performance disabled; any saved graphics preset restored.");}
        else if(action.equals("preset")){runtime.preset.apply(runtime.config);runtime.config.enabled=true;runtime.config.save();runtime.resetCounters();say(sender,"Phone preset applied: 4 chunks, fast graphics, fewer particles, no mipmaps, 10 Hz texture animations. Undo: /mobileperf restore");}
        else if(action.equals("restore")){say(sender,runtime.preset.restore(runtime.config)?"Your previous graphics settings were restored.":"No graphics preset backup exists.");}
        else if(action.equals("status")){say(sender,String.format(Locale.ROOT,"Mobile Performance %s | %.1f FPS, %.1f ms average, %.1f ms p95 (%d frames)",runtime.config.enabled?"ON":"OFF",runtime.frames.fps(),runtime.frames.averageMs(),runtime.frames.p95Ms(),runtime.frames.samples()));say(sender,"Skipped since join/reset: entities "+runtime.entitySkipped+", decorative tiles "+runtime.tileSkipped+", cosmetic particles "+runtime.particlesDropped+", texture updates "+runtime.animationsSkipped+". /mobileperf help");}
        else say(sender,"/mobileperf preset: low graphics + half-rate world textures; restore: undo preset; on/off: rendering budgets. Status: recent frame times. Players, bosses, custom HBM renderers and machine logic are retained.");
    }catch(Exception e){say(sender,"Mobile Performance: "+e.getMessage());}}
    @SuppressWarnings("rawtypes") public List addTabCompletionOptions(ICommandSender sender,String[] args){return args.length==1?getListOfStringsMatchingLastWord(args,"status","preset","restore","on","off","help"):null;}
}
