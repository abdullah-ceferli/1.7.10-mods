package com.dhanantry.scapeandrunparasites.backport.sound;
public enum ModernSoundCategory {
    MASTER("master"),MUSIC("music"),RECORDS("record"),WEATHER("weather"),BLOCKS("block"),HOSTILE("hostile"),NEUTRAL("neutral"),PLAYERS("player"),AMBIENT("ambient"),VOICE("voice");
    private final String name;ModernSoundCategory(String name){this.name=name;}
    public String getName(){return name;}
    public static ModernSoundCategory getByName(String name){for(ModernSoundCategory c:values())if(c.name.equals(name))return c;return null;}
    public String nativeName(){return this==VOICE?"master":name;}
}
