package dev.zahin.zalithrender.core;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

@IFMLLoadingPlugin.Name("ZalithRender")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({"dev.zahin.zalithrender.core."})
public final class RenderCore implements IFMLLoadingPlugin {
    @Override public String[] getASMTransformerClass() { return new String[] {"dev.zahin.zalithrender.core.RenderTransformer"}; }
    @Override public String getModContainerClass() { return null; }
    @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> data) {}
    @Override public String getAccessTransformerClass() { return null; }
}
