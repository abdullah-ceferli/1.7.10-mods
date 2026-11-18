package com.epherical.croptopia.legacy;
import java.util.Map;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({"com.epherical.croptopia.legacy.LoadingPlugin","com.epherical.croptopia.legacy.FishTransformer"})
public final class LoadingPlugin implements IFMLLoadingPlugin {
    public String[] getASMTransformerClass(){return new String[]{"com.epherical.croptopia.legacy.FishTransformer"};}
    public String getModContainerClass(){return null;}public String getSetupClass(){return null;}public void injectData(Map<String,Object> data){}public String getAccessTransformerClass(){return null;}
}
