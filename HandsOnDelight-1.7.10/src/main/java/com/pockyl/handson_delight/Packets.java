package com.pockyl.handson_delight;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;

public final class Packets {
    public static final class Action implements IMessage {
        public long token;public int action,a,b;public float x,z,clock;public String recipe="";
        public Action(){}
        public Action(long token,int action,int a,int b,float x,float z,float clock,String recipe){this.token=token;this.action=action;this.a=a;this.b=b;this.x=x;this.z=z;this.clock=clock;this.recipe=recipe;}
        public void toBytes(ByteBuf b){b.writeLong(token);b.writeByte(action);b.writeInt(a);b.writeInt(this.b);b.writeFloat(x);b.writeFloat(z);b.writeFloat(clock);ByteBufUtils.writeUTF8String(b,recipe);}
        public void fromBytes(ByteBuf b){token=b.readLong();action=b.readUnsignedByte();a=b.readInt();this.b=b.readInt();x=b.readFloat();z=b.readFloat();clock=b.readFloat();recipe=ByteBufUtils.readUTF8String(b);if(recipe.length()>256)throw new IllegalArgumentException("Recipe name too long");}
    }
    public static final class Open implements IMessage {
        public long token;public int x,y,z,dimension;
        public Open(){}public Open(long token,int dimension,int x,int y,int z){this.token=token;this.dimension=dimension;this.x=x;this.y=y;this.z=z;}
        public void toBytes(ByteBuf b){b.writeLong(token);b.writeInt(dimension);b.writeInt(x);b.writeInt(y);b.writeInt(z);}
        public void fromBytes(ByteBuf b){token=b.readLong();dimension=b.readInt();x=b.readInt();y=b.readInt();z=b.readInt();}
    }
    public static final class ActionHandler implements IMessageHandler<Action,IMessage>{public IMessage onMessage(final Action a,final MessageContext c){Sessions.enqueue(c.getServerHandler().playerEntity,a);return null;}}
    public static final class OpenHandler implements IMessageHandler<Open,IMessage>{public IMessage onMessage(Open a,MessageContext c){HandsOnDelight.proxy.open(a);return null;}}
    public static final class Practice implements IMessage {
        public net.minecraft.nbt.NBTTagCompound data=new net.minecraft.nbt.NBTTagCompound();public Practice(){}public Practice(net.minecraft.nbt.NBTTagCompound t){data=(net.minecraft.nbt.NBTTagCompound)t.copy();}
        public void toBytes(ByteBuf b){ByteBufUtils.writeTag(b,data);}public void fromBytes(ByteBuf b){data=ByteBufUtils.readTag(b);if(data==null)data=new net.minecraft.nbt.NBTTagCompound();}
    }
    public static final class Filled implements IMessage {
        public long token;public String recipe="";public boolean success;
        public Filled(){}public Filled(long token,String recipe,boolean success){this.token=token;this.recipe=recipe;this.success=success;}
        public void toBytes(ByteBuf b){b.writeLong(token);ByteBufUtils.writeUTF8String(b,recipe);b.writeBoolean(success);}public void fromBytes(ByteBuf b){token=b.readLong();recipe=ByteBufUtils.readUTF8String(b);success=b.readBoolean();}
    }
    public static final class PracticeHandler implements IMessageHandler<Practice,IMessage>{public IMessage onMessage(Practice a,MessageContext c){HandsOnDelight.proxy.practice(a);return null;}}
    public static final class FilledHandler implements IMessageHandler<Filled,IMessage>{public IMessage onMessage(Filled a,MessageContext c){HandsOnDelight.proxy.filled(a);return null;}}
}
