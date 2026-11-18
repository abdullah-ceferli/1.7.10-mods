package personal.truehorror.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;
import personal.truehorror.CommonProxy;
import personal.truehorror.core.CreatureKind;
import personal.truehorror.entity.Monsters;
import personal.truehorror.network.EffectMessage;

public final class ClientProxy extends CommonProxy {
    public static final HorrorEffects effects=new HorrorEffects();
    @Override public void init(){
        Class[] entities={Monsters.Herobrine.class,Monsters.Watcher.class,Monsters.Mimic.class,Monsters.Crawler.class,Monsters.PaleMan.class,Monsters.Shadow.class,Monsters.Dweller.class};
        for(int i=0;i<entities.length;i++)RenderingRegistry.registerEntityRenderingHandler(entities[i],new CreatureRenderer(CreatureKind.values()[i]));
        FMLCommonHandler.instance().bus().register(effects);MinecraftForge.EVENT_BUS.register(effects);
    }
    @Override public void enqueue(EffectMessage effect){effects.enqueue(effect);}
}
