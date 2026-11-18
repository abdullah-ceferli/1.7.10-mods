package personal.truehorror;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import personal.truehorror.core.CreatureKind;

public final class HorrorConfig {
    public static Configuration config;
    public static boolean enabled,worldChanges,screenEffects,soundEffects,flashing,affectCreative,allowDaylight;
    public static boolean[] creatures=new boolean[7];
    public static int firstMinutes,eventMinSeconds,eventMaxSeconds,scareCooldownSeconds,nearbyLimit,worldLimit,lifetimeSeconds;
    public static double eventFrequency,spawnFrequency,scareFrequency,intensity,volume,damageScale,progressionSpeed;
    public static void load(File file) {
        config=new Configuration(file); config.load(); read(); if(config.hasChanged())config.save();
    }
    public static void reload() { config.load(); read(); if(config.hasChanged())config.save(); }
    private static void read() {
        enabled=config.getBoolean("enabled","director",true,"Enable automatic horror events.");
        firstMinutes=config.getInt("firstMajorEncounterMinutes","director",10,0,240,"Per-player active gameplay grace period, saved across sessions.");
        progressionSpeed=config.getFloat("progressionSpeed","director",1,0.1F,10,"Progression multiplier; does not change normal server tick rate.");
        eventFrequency=config.getFloat("eventFrequency","director",1,0,5,"0 disables automatic events; higher values reduce randomized waits.");
        eventMinSeconds=config.getInt("minimumEventSpacingSeconds","director",45,10,3600,"Minimum quiet period after an event.");
        eventMaxSeconds=config.getInt("maximumEventSpacingSeconds","director",150,15,7200,"Upper range for randomized quiet periods.");
        if(eventMaxSeconds<eventMinSeconds)eventMaxSeconds=eventMinSeconds;
        spawnFrequency=config.getFloat("creatureEncounterChance","director",0.6F,0,1,"Chance to allow a creature event instead of an atmospheric event.");
        nearbyLimit=config.getInt("maximumCreaturesNearPlayers","director",1,1,3,"Limit in a 48-block region, shared by nearby players.");
        worldLimit=config.getInt("maximumLoadedCreaturesPerDimension","director",8,1,32,"Hard cap for director-spawned loaded horror creatures.");
        lifetimeSeconds=config.getInt("maximumEncounterSeconds","director",90,15,300,"Temporary entities fade out when this expires.");
        affectCreative=config.getBoolean("affectCreativePlayers","director",false,"Automatic events in creative; operator preview commands always work.");
        allowDaylight=config.getBoolean("allowDaylightSightings","director",true,"Daylight sightings stay non-hostile unless deep underground.");
        for(CreatureKind kind:CreatureKind.values())creatures[kind.ordinal()]=config.getBoolean(kind.id,"creatures",true,"Enable "+kind.title+".");
        damageScale=config.getFloat("damageMultiplier","creatures",1,0,5,"Creature damage scale; 0 gives atmospheric encounters without damage.");
        scareFrequency=config.getFloat("jumpscareChance","jumpscares",0.16F,0,1,"Probability gate for context-sensitive scare events.");
        scareCooldownSeconds=config.getInt("minimumJumpscareCooldownSeconds","jumpscares",180,30,3600,"Per-player major-scare cooldown, persisted across sessions.");
        intensity=config.getFloat("intensity","jumpscares",0.65F,0,1,"Strength of short-lived darkness and peripheral effects.");
        screenEffects=config.getBoolean("screenEffects","client",true,"Allow bounded vignette/fog effects. No camera rotation or forced movement.");
        soundEffects=config.getBoolean("soundEffects","client",true,"Enable custom positional audio.");
        volume=config.getFloat("volume","client",0.65F,0,1,"Custom horror audio volume multiplier.");
        flashing=config.getBoolean("flashingEffects","client",false,"Optional subtle single light pulse, disabled by default; no strobing.");
        worldChanges=config.getBoolean("worldModifications","world",false,"Optional reversible doors/signs/leaves/cairns/tunnels. Never touches stored items; OFF protects builds by default.");
    }
}
