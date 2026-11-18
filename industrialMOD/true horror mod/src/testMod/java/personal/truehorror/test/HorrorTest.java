package personal.truehorror.test;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import personal.truehorror.HorrorConfig;

@Mod(modid="truehorrortest",name="True Horror Test Harness",version="1",dependencies="required-after:truehorror")
public final class HorrorTest {
    @SidedProxy(clientSide="personal.truehorror.test.ClientProbe",serverSide="personal.truehorror.test.Probe")
    public static Probe proxy;
    @Mod.EventHandler public void init(FMLInitializationEvent event){
        if(System.getProperty("horror.test.root")==null)throw new IllegalStateException("Test harness requires an isolated fixture root");
        HorrorConfig.enabled=false;
        FMLCommonHandler.instance().bus().register(new ServerProbe());
        proxy.init();
    }
}
