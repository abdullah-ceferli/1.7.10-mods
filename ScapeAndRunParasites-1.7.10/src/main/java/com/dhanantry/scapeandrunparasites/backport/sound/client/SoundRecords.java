package com.dhanantry.scapeandrunparasites.backport.sound.client;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.audio.*;
import com.dhanantry.scapeandrunparasites.backport.sound.*;
/** Exact modern record factory parameters, backed by native non-repeating sound records. */
@SideOnly(Side.CLIENT)
public final class SoundRecords extends PositionedSound {
    public final ModernSoundCategory category;
    public SoundRecords(String sound,ModernSoundCategory category,float volume,float pitch,double x,double y,double z,ISound.AttenuationType attenuation){
        super(new net.minecraft.util.ResourceLocation(SoundIds.category(sound,category)));this.category=category;
        this.volume=volume*(category==ModernSoundCategory.VOICE?SoundNetwork.voiceVolume:1);
        this.field_147663_c=pitch;this.xPosF=(float)x;this.yPosF=(float)y;this.zPosF=(float)z;this.field_147666_i=attenuation;
    }
    public static SoundRecords getRecord(String sound,float pitch,float volume){return new SoundRecords(sound,ModernSoundCategory.MASTER,volume,pitch,0,0,0,ISound.AttenuationType.NONE);}
    public static SoundRecords getMasterRecord(String sound,float pitch){return getRecord(sound,pitch,0.25F);}
    public static SoundRecords getMusicRecord(String sound){return new SoundRecords(sound,ModernSoundCategory.MUSIC,1,1,0,0,0,ISound.AttenuationType.NONE);}
    public static SoundRecords getRecord(String sound,float x,float y,float z){return new SoundRecords(sound,ModernSoundCategory.RECORDS,4,1,x,y,z,ISound.AttenuationType.LINEAR);}
}
