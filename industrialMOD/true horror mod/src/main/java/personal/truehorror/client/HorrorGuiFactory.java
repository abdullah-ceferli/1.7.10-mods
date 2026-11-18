package personal.truehorror.client;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import personal.truehorror.HorrorConfig;

public final class HorrorGuiFactory implements IModGuiFactory {
    @Override public void initialize(Minecraft mc){ }
    @Override public Class<? extends GuiScreen> mainConfigGuiClass(){return Screen.class;}
    @Override public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){return null;}
    @Override public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element){return null;}
    public static final class Screen extends GuiConfig {
        public Screen(GuiScreen parent){super(parent,elements(),"truehorror",false,true,"True Horror - restart or /truehorror reload after server setting changes");}
        private static List<IConfigElement> elements(){
            List<IConfigElement> list=new ArrayList<IConfigElement>();
            for(String category:Arrays.asList("director","creatures","jumpscares","client","world"))
                list.add(new ConfigElement(HorrorConfig.config.getCategory(category)));
            return list;
        }
        @Override public void onGuiClosed(){super.onGuiClosed();HorrorConfig.reload();}
    }
}
