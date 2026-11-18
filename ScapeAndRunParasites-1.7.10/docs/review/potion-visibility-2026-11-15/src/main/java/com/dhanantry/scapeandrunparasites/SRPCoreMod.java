/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.MixinExtrasBootstrap
 *  net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
 *  net.minecraftforge.fml.relauncher.IFMLLoadingPlugin$MCVersion
 *  org.spongepowered.asm.launch.MixinBootstrap
 *  org.spongepowered.asm.mixin.Mixins
 */
package com.dhanantry.scapeandrunparasites;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import java.util.Map;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

@IFMLLoadingPlugin.MCVersion(value="1.7.10")
public class SRPCoreMod
implements IFMLLoadingPlugin {
    public SRPCoreMod() {
        MixinBootstrap.init();
        MixinExtrasBootstrap.init();
        Mixins.addConfiguration((String)"mixins.srparasites.json");
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
    }

    public String getAccessTransformerClass() {
        return null;
    }
}

