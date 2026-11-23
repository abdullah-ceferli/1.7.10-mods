package dev.zahin.mobileperformance.core;

import cpw.mods.fml.relauncher.*;
import java.util.Map;

@IFMLLoadingPlugin.Name("MobilePerformance")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1002)
@IFMLLoadingPlugin.TransformerExclusions({"dev.zahin.mobileperformance."})
public final class MobileCore implements IFMLLoadingPlugin {
    public String[] getASMTransformerClass(){return FMLLaunchHandler.side()==Side.CLIENT?new String[]{"dev.zahin.mobileperformance.core.MobileTransformer"}:new String[0];}
    public String getModContainerClass(){return null;}
    public String getSetupClass(){return null;}
    public String getAccessTransformerClass(){return null;}
    public void injectData(Map<String,Object> data){}
}
