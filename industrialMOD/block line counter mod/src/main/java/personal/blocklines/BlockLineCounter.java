package personal.blocklines;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "blocklinecounter", name = "Block Line Counter", version = "1.0.0",
        acceptableRemoteVersions = "*")
public final class BlockLineCounter {
    @Mod.EventHandler public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) ClientCounter.initialize();
    }
}
