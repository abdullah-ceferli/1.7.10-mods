package com.dhanantry.scapeandrunparasites.backport.entity.movement;
import java.util.UUID;import net.minecraft.entity.Entity;import io.netty.buffer.ByteBuf;import cpw.mods.fml.common.network.ByteBufUtils;import cpw.mods.fml.common.network.simpleimpl.IMessage;
public final class GravityPacket implements IMessage {
    public static final int FULL=0,DELTA=1,REMOVE=2;
    public int dimension,entityId,kind;public UUID identity;public String entityClass;public long revision;public boolean noGravity;
    public GravityPacket(){}
    public GravityPacket(Entity entity,int kind){dimension=entity.worldObj.provider.dimensionId;entityId=entity.getEntityId();identity=entity.getUniqueID();entityClass=entity instanceof net.minecraft.entity.player.EntityPlayer?net.minecraft.entity.player.EntityPlayer.class.getName():entity.getClass().getName();this.kind=kind;revision=((EntityMotionState)entity).srp$getGravityRevision();noGravity=GravityAccess.get(entity);}
    @Override public void toBytes(ByteBuf b){b.writeInt(dimension);b.writeInt(entityId);b.writeLong(identity.getMostSignificantBits());b.writeLong(identity.getLeastSignificantBits());ByteBufUtils.writeUTF8String(b,entityClass);b.writeByte(kind);b.writeLong(revision);b.writeBoolean(noGravity);}
    @Override public void fromBytes(ByteBuf b){dimension=b.readInt();entityId=b.readInt();identity=new UUID(b.readLong(),b.readLong());entityClass=ByteBufUtils.readUTF8String(b);kind=b.readUnsignedByte();revision=b.readLong();noGravity=b.readBoolean();if(kind>REMOVE||revision<0||entityClass.length()>256||b.isReadable())throw new IllegalArgumentException("Invalid gravity packet");}
}
