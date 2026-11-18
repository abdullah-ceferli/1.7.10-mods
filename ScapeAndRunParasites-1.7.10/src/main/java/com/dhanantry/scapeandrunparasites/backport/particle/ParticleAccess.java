package com.dhanantry.scapeandrunparasites.backport.particle;
import net.minecraft.world.World;import net.minecraft.world.WorldServer;
/** Preserve the distinct local World and networked WorldServer particle overloads. */
public final class ParticleAccess {
    private ParticleAccess(){}
    public interface Client {void spawn(World world,EnumParticleTypes type,boolean force,double x,double y,double z,double vx,double vy,double vz,int[] arguments);void alwaysVisible(World world,EnumParticleTypes type,double x,double y,double z,double vx,double vy,double vz,int[] arguments);}
    public static Client CLIENT;
    public static void spawn(World world,EnumParticleTypes type,double x,double y,double z,double vx,double vy,double vz,int... args) {
        spawn(world,type,false,x,y,z,vx,vy,vz,args);
    }
    public static void spawn(World world,EnumParticleTypes type,boolean force,double x,double y,double z,double vx,double vy,double vz,int... args) {
        // The original local World overload only notifies world listeners; server batches use the separate overload below.
        if(world.isRemote){if(CLIENT==null)throw new IllegalStateException("Particle client is not initialized");CLIENT.spawn(world,type,force||type.getShouldIgnoreRange(),x,y,z,vx,vy,vz,ParticleBatch.required(type,args));}
    }
    public static void alwaysVisible(World world,int particleId,double x,double y,double z,double vx,double vy,double vz,int... args) {
        if(world.isRemote){EnumParticleTypes type=EnumParticleTypes.getParticleFromId(particleId);if(type==null)return;
            if(CLIENT==null)throw new IllegalStateException("Particle client is not initialized");
            CLIENT.alwaysVisible(world,type,x,y,z,vx,vy,vz,ParticleBatch.required(type,args));}
    }
    public static void spawn(WorldServer world,EnumParticleTypes type,double x,double y,double z,int count,double dx,double dy,double dz,double speed,int... args) {
        spawn(world,type,false,x,y,z,count,dx,dy,dz,speed,args);
    }
    public static void spawn(WorldServer world,EnumParticleTypes type,boolean distant,double x,double y,double z,int count,double dx,double dy,double dz,double speed,int... args) {
        ParticleNetwork.send(world,new ParticleBatch(world.provider.dimensionId,type,distant,x,y,z,count,dx,dy,dz,speed,args),x,y,z);
    }
    public static String legacyName(EnumParticleTypes type,int... args) {
        int[] required=ParticleBatch.required(type,args);
        if(type==EnumParticleTypes.BLOCK_CRACK||type==EnumParticleTypes.BLOCK_DUST)return type.getParticleName()+"_"+(required[0]&4095)+"_"+((required[0]>>12)&15);
        if(type==EnumParticleTypes.ITEM_CRACK)return type.getParticleName()+"_"+required[0]+"_"+required[1];
        return type.getParticleName();
    }
}
