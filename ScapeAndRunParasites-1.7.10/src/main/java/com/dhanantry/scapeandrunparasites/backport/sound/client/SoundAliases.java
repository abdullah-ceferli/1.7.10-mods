package com.dhanantry.scapeandrunparasites.backport.sound.client;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.sound.*;
/** Native 1.7 stores category on the event pool. One alias per category preserves independent playback. */
@SideOnly(Side.CLIENT)
public final class SoundAliases {
    private SoundAliases(){}
    public static boolean isAdapted(ISound sound){return SoundIds.isAlias(sound.getPositionedSoundLocation().toString());}
    public static SoundEventAccessorComposite resolve(SoundEventAccessorComposite existing,SoundRegistry registry,ResourceLocation id,SoundHandler handler){
        if(existing!=null||!SoundIds.isAlias(id.toString()))return existing;
        ModernSoundCategory category=SoundIds.aliasCategory(id.toString());
        final SoundEventAccessorComposite base=handler.getSound(new ResourceLocation(SoundIds.canonical(id.toString())));
        if(base==null)return null;
        SoundCategory nativeCategory=SoundCategory.func_147154_a(category.nativeName());
        if(nativeCategory==null)throw new IllegalStateException("Missing native sound category: "+category);
        SoundEventAccessorComposite alias=new SoundEventAccessorComposite(id,1,1,nativeCategory);
        alias.addSoundToEventPool(new ISoundEventAccessor(){
            @Override public int func_148721_a(){return base.func_148721_a();}
            @Override public SoundPoolEntry func_148720_g(){return base.func_148720_g();}
        });
        registry.registerSound(alias);
        return alias;
    }
}
