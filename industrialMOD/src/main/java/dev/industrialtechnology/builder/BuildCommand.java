package dev.industrialtechnology.builder;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;

/** Only own-tool settings and a reach-checked air endpoint cross the wire. */
public final class BuildCommand implements IMessage {
    public static final int CONFIGURE=0,CANCEL=1,AIR_END=2;
    int action=-1,slot,mode,x,y,z;boolean hollow;
    public BuildCommand(){}
    public BuildCommand(int action,int slot,BuildMode mode,boolean hollow,BuildPos pos){this.action=action;this.slot=slot;this.mode=mode.ordinal();this.hollow=hollow;if(pos!=null){x=pos.x;y=pos.y;z=pos.z;}}
    public void toBytes(ByteBuf b){b.writeByte(action);b.writeByte(slot);b.writeByte(mode);b.writeBoolean(hollow);b.writeInt(x);b.writeInt(y);b.writeInt(z);}
    public void fromBytes(ByteBuf b){if(b.readableBytes()!=16)return;action=b.readUnsignedByte();slot=b.readUnsignedByte();mode=b.readUnsignedByte();hollow=b.readBoolean();x=b.readInt();y=b.readInt();z=b.readInt();}
    public static final class Handler implements IMessageHandler<BuildCommand,IMessage>{public IMessage onMessage(BuildCommand packet,MessageContext context){BuilderService.enqueue(context.getServerHandler().playerEntity,packet);return null;}}
}
