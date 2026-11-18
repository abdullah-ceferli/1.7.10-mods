package com.dhanantry.scapeandrunparasites.backport.potion.core;

import java.util.Map;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name("SRP Potion Visibility")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"com.dhanantry.scapeandrunparasites.backport.potion.core.","com.dhanantry.scapeandrunparasites.backport.entity.core.","com.dhanantry.scapeandrunparasites.backport.particle.core.","com.dhanantry.scapeandrunparasites.backport.sound.core."})
public final class PotionFxLoadingPlugin implements IFMLLoadingPlugin {
    @Override public String[] getASMTransformerClass(){return new String[]{"com.dhanantry.scapeandrunparasites.backport.potion.core.PotionFxTransformer","com.dhanantry.scapeandrunparasites.backport.entity.core.ArmorAttributeTransformer","com.dhanantry.scapeandrunparasites.backport.particle.core.ParticleFactoryTransformer","com.dhanantry.scapeandrunparasites.backport.sound.core.SoundTransformer","com.dhanantry.scapeandrunparasites.backport.entity.core.MovementTransformer","com.dhanantry.scapeandrunparasites.backport.entity.core.EntityAILifecycleTransformer"};}
    @Override public String getModContainerClass(){return null;}
    @Override public String getSetupClass(){return null;}
    @Override public void injectData(Map<String,Object> data){}
    @Override public String getAccessTransformerClass(){return null;}
}
