package com.dhanantry.scapeandrunparasites.backport.sound;
import net.minecraft.entity.*;import net.minecraft.entity.player.EntityPlayer;import net.minecraft.entity.monster.EntityMob;import net.minecraft.entity.boss.EntityDragon;import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.world.*;import net.minecraft.network.Packet;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
public final class SoundAccess {
    private SoundAccess(){}
    public interface Client {
        void local(World world,String sound,ModernSoundCategory category,double x,double y,double z,float volume,float pitch,boolean delay);
        void player(World world,EntityPlayer player,String sound,ModernSoundCategory category,double x,double y,double z,float volume,float pitch);
    }
    public static Client CLIENT;
    public static ModernSoundCategory category(Entity entity){
        if(entity instanceof SoundCategoryProvider)return ((SoundCategoryProvider)entity).getSoundCategory();
        if(entity instanceof EntityPlayer)return ModernSoundCategory.PLAYERS;
        if(entity instanceof EntityMob||entity instanceof EntityDragon)return ModernSoundCategory.HOSTILE;
        if(entity instanceof EntityAmbientCreature)return ModernSoundCategory.AMBIENT;
        return ModernSoundCategory.NEUTRAL;
    }
    public static void play(Entity entity,String sound,float volume,float pitch){play(entity.worldObj,entity instanceof EntityPlayer?(EntityPlayer)entity:null,entity.posX,entity.posY,entity.posZ,sound,category(entity),volume,pitch);}
    /** Called by the native Entity hook only for this port's resource IDs; ordinary native sounds continue unchanged. */
    public static boolean intercept(Entity entity,String sound,float volume,float pitch){if(!SoundIds.isPorted(sound))return false;play(entity,sound,volume,pitch);return true;}
    public static void play(World world,EntityPlayer excluded,double x,double y,double z,String sound,ModernSoundCategory category,float volume,float pitch){
        if(world.isRemote){if(CLIENT==null)throw new IllegalStateException("Sound client is not initialized");CLIENT.player(world,excluded,sound,category,x,y,z,volume,pitch);}
        else if(world instanceof WorldServer)SoundNetwork.send((WorldServer)world,excluded,new SoundMessage(sound,category,x,y,z,volume,pitch),x,y,z);
    }
    public static void play(World world,EntityPlayer excluded,BlockPos position,String sound,ModernSoundCategory category,float volume,float pitch){play(world,excluded,position.getX()+0.5,position.getY()+0.5,position.getZ()+0.5,sound,category,volume,pitch);}
    public static void play(World world,double x,double y,double z,String sound,ModernSoundCategory category,float volume,float pitch,boolean delayed){if(world.isRemote){if(CLIENT==null)throw new IllegalStateException("Sound client is not initialized");CLIENT.local(world,sound,category,x,y,z,volume,pitch,delayed);}}
    public static void play(World world,BlockPos position,String sound,ModernSoundCategory category,float volume,float pitch,boolean delayed){play(world,position.getX()+0.5,position.getY()+0.5,position.getZ()+0.5,sound,category,volume,pitch,delayed);}
    public static Packet packet(String sound,ModernSoundCategory category,double x,double y,double z,float volume,float pitch){return SoundNetwork.packet(new SoundMessage(sound,category,x,y,z,volume,pitch));}
}
