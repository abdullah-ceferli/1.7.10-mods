package com.dhanantry.scapeandrunparasites.backport.particle;
import java.util.Random;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
/** Modern particle packet values and expansion, including the count-zero directed-particle case. */
public final class ParticleBatch implements IMessage {
    public EnumParticleTypes type;public boolean longDistance;public int dimension,count;
    public float x,y,z,dx,dy,dz,speed;public int[] arguments;
    public ParticleBatch(){}
    public ParticleBatch(int dimension,EnumParticleTypes type,boolean distant,double x,double y,double z,int count,double dx,double dy,double dz,double speed,int... args) {
        this.dimension=dimension;this.type=type;longDistance=distant;this.x=(float)x;this.y=(float)y;this.z=(float)z;this.count=count;
        this.dx=(float)dx;this.dy=(float)dy;this.dz=(float)dz;this.speed=(float)speed;arguments=required(type,args);
    }
    public static int[] required(EnumParticleTypes type,int[] args) {
        if(type==null||args==null||args.length<type.getArgumentCount())throw new IllegalArgumentException("Missing particle type/arguments");
        return java.util.Arrays.copyOf(args,type.getArgumentCount());
    }
    @Override public void toBytes(ByteBuf out) {
        out.writeInt(dimension);out.writeByte(type.getParticleID());out.writeBoolean(longDistance);
        out.writeFloat(x);out.writeFloat(y);out.writeFloat(z);out.writeFloat(dx);out.writeFloat(dy);out.writeFloat(dz);out.writeFloat(speed);out.writeInt(count);
        for(int argument:arguments)out.writeInt(argument);
    }
    @Override public void fromBytes(ByteBuf in) {
        dimension=in.readInt();type=EnumParticleTypes.getParticleFromId(in.readUnsignedByte());
        if(type==null)throw new IllegalArgumentException("Unknown particle ID");longDistance=in.readBoolean();
        x=in.readFloat();y=in.readFloat();z=in.readFloat();dx=in.readFloat();dy=in.readFloat();dz=in.readFloat();speed=in.readFloat();count=in.readInt();
        arguments=new int[type.getArgumentCount()];for(int i=0;i<arguments.length;i++)arguments[i]=in.readInt();
        if(in.isReadable())throw new IllegalArgumentException("Trailing particle packet bytes");
    }
    public interface Sink {void spawn(EnumParticleTypes type,boolean distant,double x,double y,double z,double vx,double vy,double vz,int[] args);}
    public void expand(Random random,Sink sink) {
        if(count==0){sink.spawn(type,longDistance,x,y,z,(double)(speed*dx),(double)(speed*dy),(double)(speed*dz),arguments);return;}
        for(int i=0;i<count;i++) {
            double px=x+random.nextGaussian()*(double)dx,py=y+random.nextGaussian()*(double)dy,pz=z+random.nextGaussian()*(double)dz;
            double vx=random.nextGaussian()*(double)speed,vy=random.nextGaussian()*(double)speed,vz=random.nextGaussian()*(double)speed;
            sink.spawn(type,longDistance,px,py,pz,vx,vy,vz,arguments);
        }
    }
    public static boolean inRange(int playerX,int playerY,int playerZ,double x,double y,double z,boolean distant) {
        double dx=playerX-x,dy=playerY-y,dz=playerZ-z,d=dx*dx+dy*dy+dz*dz;
        return d<=1024.0||distant&&d<=262144.0;
    }
}
