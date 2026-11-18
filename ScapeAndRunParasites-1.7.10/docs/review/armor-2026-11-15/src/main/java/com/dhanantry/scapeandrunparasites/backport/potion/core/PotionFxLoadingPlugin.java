package com.dhanantry.scapeandrunparasites.backport.potion.core;

import java.util.Map;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name("SRP Potion Visibility")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"com.dhanantry.scapeandrunparasites.backport.potion.core.","com.dhanantry.scapeandrunparasites.backport.entity.core."})
public final class PotionFxLoadingPlugin implements IFMLLoadingPlugin {
    @Override public String[] getASMTransformerClass(){return new String[]{"com.dhanantry.scapeandrunparasites.backport.potion.core.PotionFxTransformer","com.dhanantry.scapeandrunparasites.backport.entity.core.ArmorAttributeTransformer"};}
    @Override public String getModContainerClass(){return null;}
    @Override public String getSetupClass(){return null;}
    @Override public void injectData(Map<String,Object> data){}
    @Override public String getAccessTransformerClass(){return null;}
}
