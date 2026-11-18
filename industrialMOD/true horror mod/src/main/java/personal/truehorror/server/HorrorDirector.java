package personal.truehorror.server;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import personal.truehorror.HorrorConfig;
import personal.truehorror.core.*;
import personal.truehorror.entity.DreadEntity;

public final class HorrorDirector {
    private final Map<UUID,PlayerRecord> records=new HashMap<UUID,PlayerRecord>();
    private final Map<Integer,Set<UUID>> creatures=new HashMap<Integer,Set<UUID>>();
    public PlayerRecord record(EntityPlayer player){
        PlayerRecord result=records.get(player.getUniqueID());
        if(result==null){result=new PlayerRecord(player);records.put(player.getUniqueID(),result);}
        return result;
    }
    @SubscribeEvent public void playerTick(TickEvent.PlayerTickEvent event){
        if(event.phase!=TickEvent.Phase.END||!(event.player instanceof EntityPlayerMP)||event.player.ticksExisted%20!=0)return;
        EntityPlayerMP player=(EntityPlayerMP)event.player;PlayerRecord record=record(player);PlayerProgress p=record.progress;
        long now=player.worldObj.getTotalWorldTime();
        float turn=Math.abs(MathHelper.wrapAngleTo180_float(player.rotationYaw-record.yaw));
        if(player.getDistanceSq(record.x,record.y,record.z)>.02||turn>3||player.isUsingItem()||record.miningUntil>now)record.lastActivity=now;
        if(turn>110)p.caution=Math.min(6,p.caution+1);
        else if(player.ticksExisted%1200==0)p.caution=Math.max(0,p.caution-1);
        record.x=player.posX;record.y=player.posY;record.z=player.posZ;record.yaw=player.rotationYaw;
        boolean active=player.isEntityAlive()&&!player.isPlayerSleeping()&&now-record.lastActivity<1200
                &&(!player.capabilities.isCreativeMode||HorrorConfig.affectCreative);
        p.advance(20,active);
        if(active&&HorrorConfig.enabled&&HorrorConfig.eventFrequency>0){
            if(p.nextEvent==0)p.schedule(player.getRNG(),35,100,1);
            if(p.activeTicks>=p.nextEvent){
                Environment context=new Environment(player,record);
                boolean creaturesAllowed=player.getRNG().nextDouble()<HorrorConfig.spawnFrequency&&canEncounter(player);
                boolean scareAllowed=p.activeTicks>=p.nextScare&&player.getRNG().nextDouble()<HorrorConfig.scareFrequency;
                EventType selected=EventType.select(player.getRNG(),p.stage(HorrorConfig.firstMinutes,HorrorConfig.progressionSpeed),context.flags,
                        creaturesAllowed,scareAllowed,HorrorConfig.worldChanges,p);
                if(selected!=null&&EventEngine.execute(player,record,context,selected,false)){
                    p.remember(selected.name());p.encounters++;record.woke=false;
                    p.schedule(player.getRNG(),HorrorConfig.eventMinSeconds,HorrorConfig.eventMaxSeconds,HorrorConfig.eventFrequency);
                    if(selected.creature)p.nextEvent+=200;
                }else p.nextEvent=p.activeTicks+240+player.getRNG().nextInt(241);
            }
        }
        int x=MathHelper.floor_double(player.posX),y=MathHelper.floor_double(player.posY),z=MathHelper.floor_double(player.posZ);
        if(player.worldObj.getBlockLightValue(x,y,z)<7)record.darkSeconds=Math.min(600,record.darkSeconds+1);else record.darkSeconds=0;
        record.save(player);
    }
    public boolean canEncounter(EntityPlayerMP player){
        Set<UUID> dimension=creatures.get(player.dimension);
        if(dimension!=null&&dimension.size()>=HorrorConfig.worldLimit)return false;
        int nearby=0;
        for(Object value:player.worldObj.getEntitiesWithinAABB(DreadEntity.class,player.boundingBox.expand(48,32,48)))if(!((DreadEntity)value).isDead)nearby++;
        return nearby<HorrorConfig.nearbyLimit;
    }
    @SubscribeEvent public void joined(EntityJoinWorldEvent event){
        if(!event.world.isRemote&&event.entity instanceof DreadEntity){
            int dim=event.world.provider.dimensionId;Set<UUID> set=creatures.get(dim);
            if(set==null){set=new HashSet<UUID>();creatures.put(dim,set);}set.add(event.entity.getUniqueID());
        }
    }
    public void gone(DreadEntity entity){Set<UUID> set=creatures.get(entity.dimension);if(set!=null)set.remove(entity.getUniqueID());}
    @SubscribeEvent public void chunkUnloaded(net.minecraftforge.event.world.ChunkEvent.Unload event){
        if(event.world.isRemote)return;
        for(List list:event.getChunk().entityLists)for(Object entity:list)if(entity instanceof DreadEntity)gone((DreadEntity)entity);
    }
    @SubscribeEvent public void worldUnloaded(WorldEvent.Unload event){if(!event.world.isRemote)creatures.remove(event.world.provider.dimensionId);}
    @SubscribeEvent public void worldTick(TickEvent.WorldTickEvent event){
        if(event.phase==TickEvent.Phase.END&&!event.world.isRemote&&event.world.getTotalWorldTime()%20==0)WorldEdits.get(event.world).restoreExpired(event.world);
    }
    @SubscribeEvent public void logout(PlayerLoggedOutEvent event){PlayerRecord r=records.remove(event.player.getUniqueID());if(r!=null)r.save(event.player);}
    @SubscribeEvent public void clone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event){
        if(event.entityPlayer.worldObj.isRemote)return;
        PlayerRecord old=records.remove(event.original.getUniqueID());if(old!=null)old.save(event.original);
        PlayerRecord.data(event.entityPlayer);
        event.entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
                .setTag("TrueHorror",PlayerRecord.data(event.original).copy());
    }
    @SubscribeEvent public void mined(BlockEvent.BreakEvent event){if(!event.world.isRemote)record(event.getPlayer()).miningUntil=event.world.getTotalWorldTime()+140;}
    @SubscribeEvent public void interacted(net.minecraftforge.event.entity.player.PlayerInteractEvent event){
        if(event.world.isRemote)return;
        PlayerRecord record=record(event.entityPlayer);record.lastActivity=event.world.getTotalWorldTime();
        if(event.action==net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)record.miningUntil=record.lastActivity+140;
    }
    @SubscribeEvent public void woke(PlayerWakeUpEvent event){if(!event.entityPlayer.worldObj.isRemote)record(event.entityPlayer).woke=true;}
    @SubscribeEvent public void animalSound(PlaySoundAtEntityEvent event){
        if(!event.entity.worldObj.isRemote&&event.entity.getEntityData().getLong("TrueHorrorHushUntil")>event.entity.worldObj.getTotalWorldTime())event.setCanceled(true);
    }
    @SubscribeEvent public void animalLook(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent event){
        if(event.entityLiving.worldObj.isRemote||event.entityLiving.ticksExisted%10!=0||!(event.entityLiving instanceof net.minecraft.entity.passive.EntityAnimal))return;
        net.minecraft.entity.passive.EntityAnimal animal=(net.minecraft.entity.passive.EntityAnimal)event.entityLiving;
        if(animal.getEntityData().getLong("TrueHorrorStareUntil")>animal.worldObj.getTotalWorldTime()){
            EntityPlayer target=animal.worldObj.getPlayerEntityByName(animal.getEntityData().getString("TrueHorrorStareAt"));
            if(target!=null&&animal.getDistanceSqToEntity(target)<256)animal.getLookHelper().setLookPositionWithEntity(target,15,10);
        }
    }
    public void clearRuntime(){records.clear();creatures.clear();}
}
