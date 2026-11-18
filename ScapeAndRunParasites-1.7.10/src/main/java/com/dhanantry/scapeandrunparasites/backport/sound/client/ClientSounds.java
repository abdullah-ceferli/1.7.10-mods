package com.dhanantry.scapeandrunparasites.backport.sound.client;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.relauncher.*;
import net.minecraft.client.Minecraft;import net.minecraft.client.audio.*;import net.minecraft.entity.player.EntityPlayer;import net.minecraft.world.World;
import com.dhanantry.scapeandrunparasites.backport.sound.*;
@SideOnly(Side.CLIENT)
public final class ClientSounds implements SoundAccess.Client {
    private static boolean initialized;
    public static synchronized void initialize(){if(initialized)return;initialized=true;ClientSounds client=new ClientSounds();SoundAccess.CLIENT=client;FMLCommonHandler.instance().bus().register(client);}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;Minecraft mc=Minecraft.getMinecraft();SoundMessage message;
        while((message=SoundNetwork.INBOX.poll())!=null)if(mc.theWorld!=null)local(mc.theWorld,message.sound,message.category,SoundMath.coordinate(message.x),SoundMath.coordinate(message.y),SoundMath.coordinate(message.z),message.volume,message.pitch,false);
    }
    @Override public void player(World world,EntityPlayer player,String sound,ModernSoundCategory category,double x,double y,double z,float volume,float pitch){
        Minecraft mc=Minecraft.getMinecraft();if(player==mc.thePlayer)local(world,sound,category,x,y,z,volume,pitch,false);
    }
    @Override public void local(World world,String sound,ModernSoundCategory category,double x,double y,double z,float volume,float pitch,boolean delayed){
        Minecraft mc=Minecraft.getMinecraft();if(world!=mc.theWorld||mc.renderViewEntity==null)return;
        SoundRecords record=new SoundRecords(sound,category,volume,pitch,x,y,z,ISound.AttenuationType.LINEAR);
        int ticks=SoundMath.delay(mc.renderViewEntity.getDistanceSq(x,y,z),delayed);
        if(ticks>0)mc.getSoundHandler().playDelayedSound(record,ticks);else mc.getSoundHandler().playSound(record);
    }
}
