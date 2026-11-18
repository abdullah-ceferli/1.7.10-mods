package personal.truehorror.network;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import personal.truehorror.TrueHorror;

/** Small fixed-size message; no client request can spawn monsters or modify a world. */
public final class EffectMessage implements IMessage {
    public int type,duration;
    public float intensity;
    public EffectMessage(){ }
    public EffectMessage(int type,int duration,float intensity){this.type=type;this.duration=duration;this.intensity=intensity;}
    @Override public void toBytes(ByteBuf out){out.writeByte(type);out.writeShort(duration);out.writeFloat(intensity);}
    @Override public void fromBytes(ByteBuf in){
        if(in.readableBytes()!=7)throw new IllegalArgumentException("Invalid effect length");
        type=in.readUnsignedByte();duration=in.readUnsignedShort();intensity=in.readFloat();
        if(type>5||duration>160||Float.isNaN(intensity)||intensity<0||intensity>1)throw new IllegalArgumentException("Invalid effect bounds");
    }
    public static final class Handler implements IMessageHandler<EffectMessage,IMessage>{
        @Override public IMessage onMessage(EffectMessage message,MessageContext context){TrueHorror.proxy.enqueue(message);return null;}
    }
}
