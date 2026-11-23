package dev.zahin.mobileperformance;

/** Pure distance policy. Unknown mod renderers and combat effects are preserved. */
public final class RenderPolicy {
    public static int bound(int value,int min,int max){return Math.max(min,Math.min(max,value));}
    public static double distanceSq(double x,double y,double z,double cx,double cy,double cz){double dx=x-cx,dy=y-cy,dz=z-cz;return dx*dx+dy*dy+dz*dz;}
    public static boolean inRange(double squared,int range){return !Double.isFinite(squared)||squared<=0||squared<=(double)range*range;}
    public static boolean ordinaryMob(String type){return type.startsWith("net.minecraft.entity.monster.")||type.startsWith("net.minecraft.entity.passive.");}
    public static boolean decorativeTile(String type){
        return type.equals("net.minecraft.tileentity.TileEntityChest")||type.equals("net.minecraft.tileentity.TileEntityEnderChest")||type.equals("net.minecraft.tileentity.TileEntitySign")||type.equals("net.minecraft.tileentity.TileEntitySkull")
            ||type.equals("com.jkvin114.displaydelight.DisplayTile")||type.equals("com.epherical.croptopia.legacy.GardenTile")||type.startsWith("vectorwing.farmersdelight.legacy.");
    }
    public static boolean cosmeticParticle(String type){
        return type.equals("net.minecraft.client.particle.EntitySmokeFX")||type.equals("net.minecraft.client.particle.EntityReddustFX")||type.equals("net.minecraft.client.particle.EntitySpellParticleFX")||type.equals("net.minecraft.client.particle.EntitySuspendFX")||type.equals("net.minecraft.client.particle.EntityCloudFX")||type.equals("net.minecraft.client.particle.EntityPortalFX")||type.equals("net.minecraft.client.particle.EntityRainFX");
    }
    public static boolean animationTick(long tick,int interval){return interval<=1||tick%interval==0;}
    private RenderPolicy(){}
}
