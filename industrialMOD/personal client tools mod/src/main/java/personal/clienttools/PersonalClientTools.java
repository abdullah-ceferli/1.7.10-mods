package personal.clienttools;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import java.io.File;
import personal.blocklines.ClientCounter;
import personal.controlpresets.ControlsClient;

@Mod(modid = "personalclienttools", name = "Personal Client Tools", version = "1.1.0",
        acceptableRemoteVersions = "*")
public final class PersonalClientTools {
    private File presetsDirectory;

    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        // Keep existing presets at the same path when replacing the separate mods.
        presetsDirectory = new File(event.getModConfigurationDirectory(), "controlpresets");
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            ClientCounter.initialize();
            ControlsClient.initialize(presetsDirectory);
        }
    }
}
