package com.pockyl.handson_delight.launch;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({"com.pockyl.handson_delight.launch."})
public final class LoadingPlugin implements IFMLLoadingPlugin {
    public String[] getASMTransformerClass(){return new String[]{"com.pockyl.handson_delight.launch.StationTransformer"};}
    public String getModContainerClass(){return null;}
    public String getSetupClass(){return null;}
    public void injectData(Map<String,Object> data){}
    public String getAccessTransformerClass(){return null;}
}
