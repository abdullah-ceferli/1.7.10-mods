package personal.truehorror;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import personal.truehorror.entity.Monsters;
import personal.truehorror.network.EffectMessage;
import personal.truehorror.server.HorrorCommand;
import personal.truehorror.server.HorrorDirector;
import org.apache.logging.log4j.Logger;

@Mod(modid=TrueHorror.ID,name="True Horror",version="1.0.0",guiFactory="personal.truehorror.client.HorrorGuiFactory")
public final class TrueHorror {
    public static final String ID="truehorror";
    @Mod.Instance(ID) public static TrueHorror instance;
    @SidedProxy(clientSide="personal.truehorror.client.ClientProxy",serverSide="personal.truehorror.CommonProxy")
    public static CommonProxy proxy;
    public static Logger log;
    public static SimpleNetworkWrapper network;
    public static HorrorDirector director;

    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        log=event.getModLog(); HorrorConfig.load(event.getSuggestedConfigurationFile());
        network=NetworkRegistry.INSTANCE.newSimpleChannel(ID);
        network.registerMessage(EffectMessage.Handler.class,EffectMessage.class,0,Side.CLIENT);
        director=new HorrorDirector();
        FMLCommonHandler.instance().bus().register(director);
        MinecraftForge.EVENT_BUS.register(director);
        Class[] types={Monsters.Herobrine.class,Monsters.Watcher.class,Monsters.Mimic.class,
                Monsters.Crawler.class,Monsters.PaleMan.class,Monsters.Shadow.class,Monsters.Dweller.class};
        String[] names={"herobrine","watcher","mimic","crawler","pale_man","shadow","dweller"};
        for(int i=0;i<types.length;i++)EntityRegistry.registerModEntity(types[i],names[i],i,this,96,3,true);
        proxy.preInit();
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event) { proxy.init(); }
    @Mod.EventHandler public void serverStarting(FMLServerStartingEvent event) { event.registerServerCommand(new HorrorCommand()); }
    @Mod.EventHandler public void serverStopped(FMLServerStoppedEvent event) { director.clearRuntime(); }
}
