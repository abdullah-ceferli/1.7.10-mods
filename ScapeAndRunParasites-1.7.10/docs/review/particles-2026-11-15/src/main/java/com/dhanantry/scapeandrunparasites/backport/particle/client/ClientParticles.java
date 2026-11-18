package com.dhanantry.scapeandrunparasites.backport.particle.client;
import java.util.Random;
import cpw.mods.fml.common.FMLCommonHandler;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.relauncher.*;
import net.minecraft.client.Minecraft;import net.minecraft.client.particle.EntityFX;import net.minecraft.world.World;
import com.dhanantry.scapeandrunparasites.backport.particle.*;
@SideOnly(Side.CLIENT)
public final class ClientParticles implements ParticleAccess.Client {
    private static boolean initialized;private final Random networkRandom=new Random();
    public static synchronized void initialize(){if(initialized)return;initialized=true;ClientParticles client=new ClientParticles();ParticleAccess.CLIENT=client;FMLCommonHandler.instance().bus().register(client);}
    @SubscribeEvent public void tick(TickEvent.ClientTickEvent event){
        if(event.phase!=TickEvent.Phase.END)return;final Minecraft mc=Minecraft.getMinecraft();ParticleBatch batch;
        while((batch=ParticleNetwork.INBOX.poll())!=null)if(mc.theWorld!=null&&mc.theWorld.provider.dimensionId==batch.dimension)try{
            batch.expand(networkRandom,(type,force,x,y,z,vx,vy,vz,args)->spawn(mc.theWorld,type,force||type.getShouldIgnoreRange(),x,y,z,vx,vy,vz,args));
        }catch(Throwable problem){cpw.mods.fml.common.FMLLog.getLogger().warn("Could not spawn particle effect "+batch.type,problem);}
    }
    @Override public void spawn(World world,EnumParticleTypes type,boolean force,double x,double y,double z,double vx,double vy,double vz,int[] args) {
        create(world,type,force,x,y,z,vx,vy,vz,args);
    }
    /** Applies the original 1.12 range/settings decision, then uses native constructors or the ported newer effect. */
    public static EntityFX create(World world,EnumParticleTypes type,boolean force,double x,double y,double z,double vx,double vy,double vz,int... args) {
        return filtered(world,type,force||type.getShouldIgnoreRange(),false,x,y,z,vx,vy,vz,args);
    }
    @Override public void alwaysVisible(World world,EnumParticleTypes type,double x,double y,double z,double vx,double vy,double vz,int[] args){
        createAlwaysVisible(world,type,x,y,z,vx,vy,vz,args);
    }
    public static EntityFX createAlwaysVisible(World world,EnumParticleTypes type,double x,double y,double z,double vx,double vy,double vz,int... args){
        return filtered(world,type,false,true,x,y,z,vx,vy,vz,args);
    }
    private static EntityFX filtered(World world,EnumParticleTypes type,boolean force,boolean minimal,double x,double y,double z,double vx,double vy,double vz,int... args){
        Minecraft mc=Minecraft.getMinecraft();if(mc.renderViewEntity==null||mc.effectRenderer==null||world!=mc.theWorld)return null;
        int level=mc.gameSettings.particleSetting;
        if(minimal&&level==2&&world.rand.nextInt(10)==0)level=1;
        if(level==1&&world.rand.nextInt(3)==0)level=2;
        double dx=mc.renderViewEntity.posX-x,dy=mc.renderViewEntity.posY-y,dz=mc.renderViewEntity.posZ-z;
        if(!force&&(dx*dx+dy*dy+dz*dz>1024.0||level>1))return null;
        args=ParticleBatch.required(type,args);
        EntityFX effect;
        switch(type){
            case ITEM_TAKE:return null; // The original 1.12 ParticleManager intentionally registers no factory for ID 40.
            case WATER_WAKE:effect=new ExtraParticleFX.Wake(world,x,y,z,vx,vy,vz);break;
            case WATER_DROP:effect=new net.minecraft.client.particle.EntityRainFX(world,x,y,z);break;
            case TOTEM:effect=new ExtraParticleFX.Totem(world,x,y,z,vx,vy,vz);break;
            case SPIT:effect=new ExtraParticleFX.Spit(world,x,y,z,vx,vy,vz);break;
            case MOB_APPEARANCE:effect=new ExtraParticleFX.Appearance(world,x,y,z);break;
            case FALLING_DUST:
                net.minecraft.block.Block block=net.minecraft.block.Block.getBlockById(args[0]&4095);int meta=(args[0]>>12)&15;
                if(block==null)block=net.minecraft.init.Blocks.air;
                if(block!=net.minecraft.init.Blocks.air&&block.getRenderType()==-1)return null;
                int color=block.colorMultiplier(world,net.minecraft.util.MathHelper.floor_double(x),net.minecraft.util.MathHelper.floor_double(y),net.minecraft.util.MathHelper.floor_double(z));
                if(block==net.minecraft.init.Blocks.sand)color=meta==1?0xFFA95821:-2370656;
                else if(block==net.minecraft.init.Blocks.gravel)color=-8356741;
                else if(block instanceof net.minecraft.block.BlockFalling)color=0xFF000000;
                else if(block==net.minecraft.init.Blocks.wool)color=block.getRenderColor(meta);
                effect=new ExtraParticleFX.FallingDust(world,x,y,z,color);break;
            case BARRIER:effect=new ModernParticleFX.Barrier(world,x,y,z);break;
            case END_ROD:effect=new ModernParticleFX.EndRod(world,x,y,z,vx,vy,vz);break;
            case DRAGON_BREATH:effect=new ModernParticleFX.DragonBreath(world,x,y,z,vx,vy,vz);break;
            case DAMAGE_INDICATOR:effect=new ModernParticleFX.DamageIndicator(world,x,y,z,vx,vy,vz);break;
            case SWEEP_ATTACK:effect=new ModernParticleFX.Sweep(world,x,y,z,vx);break;
            default:
                boolean previous=ParticleRenderContext.enter();
                try{effect=mc.renderGlobal.doSpawnParticle(ParticleAccess.legacyName(type,args),x,y,z,vx,vy,vz);}
                finally{ParticleRenderContext.leave(previous);}
                return effect; // The native factory already registers the effect with EffectRenderer.
        }
        mc.effectRenderer.addEffect(effect);return effect;
    }
}
