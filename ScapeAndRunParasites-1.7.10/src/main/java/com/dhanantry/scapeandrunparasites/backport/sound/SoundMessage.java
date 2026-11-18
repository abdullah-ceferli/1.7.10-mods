package com.dhanantry.scapeandrunparasites.backport.sound;
import cpw.mods.fml.common.network.ByteBufUtils;import cpw.mods.fml.common.network.simpleimpl.IMessage;import io.netty.buffer.ByteBuf;
public final class SoundMessage implements IMessage {
    public String sound;public ModernSoundCategory category;public int x,y,z;public float volume,pitch;
    public SoundMessage(){}
    public SoundMessage(String sound,ModernSoundCategory category,double x,double y,double z,float volume,float pitch){this.sound=java.util.Objects.requireNonNull(SoundIds.canonical(sound),"sound");this.category=java.util.Objects.requireNonNull(category,"category");this.x=SoundMath.coordinate(x);this.y=SoundMath.coordinate(y);this.z=SoundMath.coordinate(z);this.volume=volume;this.pitch=pitch;}
    @Override public void toBytes(ByteBuf b){ByteBufUtils.writeUTF8String(b,sound);b.writeByte(category.ordinal());b.writeInt(x);b.writeInt(y);b.writeInt(z);b.writeFloat(volume);b.writeFloat(pitch);}
    @Override public void fromBytes(ByteBuf b){sound=ByteBufUtils.readUTF8String(b);int type=b.readUnsignedByte();if(type>=ModernSoundCategory.values().length)throw new IllegalArgumentException("Unknown sound category");category=ModernSoundCategory.values()[type];x=b.readInt();y=b.readInt();z=b.readInt();volume=b.readFloat();pitch=b.readFloat();if(b.isReadable())throw new IllegalArgumentException("Trailing sound bytes");}
}
