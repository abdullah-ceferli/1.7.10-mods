package com.dhanantry.scapeandrunparasites.backport.sound;
import java.util.concurrent.ConcurrentLinkedQueue;import java.io.File;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.network.NetworkRegistry;import cpw.mods.fml.common.network.simpleimpl.*;import cpw.mods.fml.relauncher.Side;
import net.minecraft.network.Packet;import net.minecraft.entity.player.*;import net.minecraft.world.WorldServer;import net.minecraftforge.common.config.Configuration;
public final class SoundNetwork {
    private static SimpleNetworkWrapper channel;public static volatile float voiceVolume=1;
    public static final ConcurrentLinkedQueue<SoundMessage> INBOX=new ConcurrentLinkedQueue<SoundMessage>();
    public static synchronized void initialize(File config){
        if(channel!=null)return;
        Configuration settings=new Configuration(config);voiceVolume=(float)settings.get("volume","voice",1.0,"Volume for the modern voice category, absent from the native 1.7 sound options.",0,1).getDouble(1);if(settings.hasChanged())settings.save();
        channel=NetworkRegistry.INSTANCE.newSimpleChannel("SRPSounds17");channel.registerMessage(Handler.class,SoundMessage.class,0,Side.CLIENT);
        if(FMLCommonHandler.instance().getSide().isClient())try{Class.forName("com.dhanantry.scapeandrunparasites.backport.sound.client.ClientSounds").getMethod("initialize").invoke(null);}catch(ReflectiveOperationException e){throw new IllegalStateException("Sound client initialization",e);}
    }
    public static Packet packet(SoundMessage message){if(channel==null)throw new IllegalStateException("Sound channel is not initialized");return channel.getPacketFrom(message);}
    public static void send(WorldServer world,EntityPlayer excluded,SoundMessage message,double x,double y,double z){
        if(channel==null)throw new IllegalStateException("Sound channel is not initialized");
        for(Object o:world.playerEntities)if(o instanceof EntityPlayerMP&&o!=excluded){EntityPlayerMP p=(EntityPlayerMP)o;if(SoundMath.inRange(p.posX,p.posY,p.posZ,x,y,z,message.volume))channel.sendTo(message,p);}
    }
    public static final class Handler implements IMessageHandler<SoundMessage,IMessage>{@Override public IMessage onMessage(SoundMessage sound,MessageContext context){INBOX.add(sound);return null;}}
}
