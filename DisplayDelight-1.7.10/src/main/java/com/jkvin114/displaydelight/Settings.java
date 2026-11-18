package com.jkvin114.displaydelight;
import java.io.File;
import net.minecraftforge.common.config.Configuration;
public final class Settings {
    public static Configuration config;
    public static boolean vanilla,creative,trays,farmerTrades,takeAll,placeAll,silk;
    public static void load(File file){
        config=new Configuration(file);config.load();
        vanilla=config.get("general","vanillaFoodPlacement",true).getBoolean(true);
        creative=config.get("general","creativeTab",true).getBoolean(true);
        trays=config.get("general","supportTables",true).getBoolean(true);
        farmerTrades=config.get("general","farmerVillagerTrades",true,"1.7.10 has no wandering traders. Farmer villagers offer decorative food displays instead.").getBoolean(true);
        takeAll=config.get("general","sneakTakeAll",true).getBoolean(true);
        placeAll=config.get("general","sneakPlaceAll",true).getBoolean(true);
        silk=config.get("general","silkTouchDisplayDrops",true).getBoolean(true);
        config.save();
    }
}
