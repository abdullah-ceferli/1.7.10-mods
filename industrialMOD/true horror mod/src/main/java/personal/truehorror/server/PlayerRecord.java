package personal.truehorror.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import personal.truehorror.core.PlayerProgress;

public final class PlayerRecord {
    public final PlayerProgress progress=new PlayerProgress();
    public double x,y,z;
    public float yaw;
    public long lastActivity,miningUntil;
    public int darkSeconds;
    public boolean woke;
    public PlayerRecord(EntityPlayer player){
        NBTTagCompound nbt=data(player);
        progress.activeTicks=Math.max(0,nbt.getLong("active"));
        progress.nextEvent=Math.max(0,nbt.getLong("event"));progress.nextScare=Math.max(0,nbt.getLong("scare"));
        progress.encounters=Math.max(0,nbt.getInteger("encounters"));progress.warnings=Math.max(0,nbt.getInteger("warnings"));
        progress.caution=Math.max(0,Math.min(6,nbt.getInteger("caution")));progress.recent=nbt.getString("recent");
        if(progress.recent.length()>160)progress.recent="";
        x=player.posX;y=player.posY;z=player.posZ;yaw=player.rotationYaw;lastActivity=player.worldObj.getTotalWorldTime();
    }
    public void save(EntityPlayer player){
        NBTTagCompound nbt=data(player);nbt.setLong("active",progress.activeTicks);nbt.setLong("event",progress.nextEvent);
        nbt.setLong("scare",progress.nextScare);nbt.setInteger("encounters",progress.encounters);nbt.setInteger("warnings",progress.warnings);
        nbt.setInteger("caution",progress.caution);nbt.setString("recent",progress.recent);
    }
    public static NBTTagCompound data(EntityPlayer player){
        NBTTagCompound root=player.getEntityData();
        if(!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG))root.setTag(EntityPlayer.PERSISTED_NBT_TAG,new NBTTagCompound());
        NBTTagCompound persistent=root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if(!persistent.hasKey("TrueHorror"))persistent.setTag("TrueHorror",new NBTTagCompound());
        return persistent.getCompoundTag("TrueHorror");
    }
}
