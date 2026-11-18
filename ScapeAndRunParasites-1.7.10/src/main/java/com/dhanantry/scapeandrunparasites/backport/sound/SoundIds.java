package com.dhanantry.scapeandrunparasites.backport.sound;
import net.minecraft.util.ResourceLocation;
/** Category aliases leave the original registry and event resource definitions intact. */
public final class SoundIds {
    public static final String DOMAIN="srp_sound_bridge";
    private SoundIds(){}
    public static boolean isAlias(String id){return id!=null&&id.startsWith(DOMAIN+":");}
    public static boolean isPorted(String id){return id!=null&&(isAlias(id)||id.startsWith("srparasites:")||id.startsWith("srpvanilla112:"));}
    public static String canonical(String id){
        if(id==null)return null;
        if(isAlias(id)){String[] parts=new ResourceLocation(id).getResourcePath().split("/",3);if(parts.length!=3)throw new IllegalArgumentException("Malformed sound alias");id=parts[1]+":"+parts[2];}
        ResourceLocation location=new ResourceLocation(id);
        if(location.getResourceDomain().equals("minecraft")&&SoundEvents.isImported(location.getResourcePath()))return "srpvanilla112:"+location.getResourcePath();
        return location.toString();
    }
    public static String category(String id,ModernSoundCategory category){
        ResourceLocation base=new ResourceLocation(canonical(id));
        return DOMAIN+":"+category.name().toLowerCase(java.util.Locale.ROOT)+"/"+base.getResourceDomain()+"/"+base.getResourcePath();
    }
    public static ModernSoundCategory aliasCategory(String alias){return ModernSoundCategory.valueOf(new ResourceLocation(alias).getResourcePath().split("/",2)[0].toUpperCase(java.util.Locale.ROOT));}
}
