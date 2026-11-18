package personal.truehorror.entity;

import java.util.UUID;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import personal.truehorror.*;
import personal.truehorror.core.*;
import personal.truehorror.server.*;

public abstract class DreadEntity extends EntityCreature implements IMob {
    private UUID owner;
    private int mode,progression,age,lifetime=1800,stateTicks,attackCooldown,nextSound=100;
    private int lastSequence,clientAttackAge=100,chaseTicks;
    private boolean gallery;
    private final EncounterBrain.Sense senses=new EncounterBrain.Sense();
    public abstract CreatureKind kind();
    public DreadEntity(World world){
        super(world);CreatureKind kind=kind();setSize(kind.width,kind.height);
        getNavigator().setAvoidsWater(true);getNavigator().setCanSwim(true);getNavigator().setBreakDoors(false);
        experienceValue=0;
    }
    @Override protected void entityInit(){
        super.entityInit();dataWatcher.addObject(20,(byte)EncounterState.DORMANT.ordinal());
        dataWatcher.addObject(21,0);dataWatcher.addObject(22,(byte)0);dataWatcher.addObject(23,(byte)0);
    }
    @Override protected void applyEntityAttributes(){
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(kind().health);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(kind().speed);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(12);
    }
    @Override protected boolean isAIEnabled(){return true;}
    @Override protected boolean canDespawn(){return false;}
    @Override protected void dropFewItems(boolean player,int looting){ }
    @Override public boolean canBePushed(){return false;}
    @Override protected boolean canTriggerWalking(){return false;}
    @Override public boolean doesEntityNotTriggerPressurePlate(){return true;}
    @Override protected void fall(float distance){ }
    @Override public boolean isOnLadder(){return (kind()==CreatureKind.CRAWLER||kind()==CreatureKind.DWELLER)&&isCollidedHorizontally||super.isOnLadder();}
    public void bind(EntityPlayerMP target,int mode,int progression,int lifetime){
        owner=target.getUniqueID();this.mode=mode;this.progression=progression;this.lifetime=lifetime;
        state(EncounterState.MANIFESTING);
    }
    public void gallery(boolean enabled){gallery=enabled;state(EncounterState.OBSERVING);}
    public boolean gallery(){return gallery;}
    public EncounterState state(){int id=dataWatcher.getWatchableObjectByte(20);return EncounterState.values()[Math.max(0,Math.min(EncounterState.values().length-1,id))];}
    private void state(EncounterState next){if(state()!=next){dataWatcher.updateObject(20,(byte)next.ordinal());stateTicks=0;}}
    public float fade(){return dataWatcher.getWatchableObjectByte(22)/20F;}
    public float attackPose(float partial){return clientAttackAge<14?(float)Math.sin(Math.PI*(clientAttackAge+partial)/14):0;}
    public int attackVariant(){return dataWatcher.getWatchableObjectByte(23);}
    @Override public void onLivingUpdate(){
        super.onLivingUpdate();
        if(worldObj.isRemote){
            int sequence=dataWatcher.getWatchableObjectInt(21);
            if(sequence!=lastSequence){lastSequence=sequence;clientAttackAge=0;}else if(clientAttackAge<100)clientAttackAge++;
            return;
        }
        age++;stateTicks++;if(attackCooldown>0)attackCooldown--;
        if(state()==EncounterState.HUNTING||state()==EncounterState.ATTACKING)chaseTicks++;
        if(gallery){getNavigator().clearPathEntity();moveForward=0;if(age>6000)setDead();return;}
        if(state()==EncounterState.DISAPPEARING){
            getNavigator().clearPathEntity();motionX*=.4;motionZ*=.4;
            dataWatcher.updateObject(22,(byte)Math.min(20,stateTicks));if(stateTicks>=20)setDead();return;
        }
        if(age>lifetime||!HorrorConfig.creatures[kind().ordinal()]){state(EncounterState.DISAPPEARING);return;}
        if(age%5!=0)return;
        EntityPlayerMP player=owner();
        if(player==null||!player.isEntityAlive()||player.getDistanceSqToEntity(this)>80*80){state(EncounterState.DISAPPEARING);return;}
        getLookHelper().setLookPositionWithEntity(player,kind()==CreatureKind.PALE_MAN?5:20,15);
        senses.distance=getDistanceToEntity(player);senses.age=age;senses.stateTicks=stateTicks;
        senses.watched=watched(player);
        if(kind()==CreatureKind.WATCHER&&!senses.watched)for(Object value:worldObj.playerEntities){
            if(value instanceof EntityPlayerMP&&getDistanceSqToEntity((EntityPlayerMP)value)<1600&&watched((EntityPlayerMP)value)){senses.watched=true;break;}
        }
        int light=worldObj.getBlockLightValue((int)Math.floor(posX),(int)Math.floor(posY+1),(int)Math.floor(posZ));
        senses.dark=light<8;senses.bright=light>10;senses.hunt=mode==2;senses.feint=mode==3;
        senses.hunting=state()==EncounterState.HUNTING||state()==EncounterState.ATTACKING;senses.chaseTicks=chaseTicks;
        senses.injured=getHealth()<getMaxHealth()*.25;
        PlayerRecord record=TrueHorror.director.record(player);senses.cautious=record.progress.caution>=3;senses.darkSeconds=record.darkSeconds;
        EncounterState desired=EncounterBrain.decide(kind(),senses);
        state(desired);
        if(desired==EncounterState.DISAPPEARING)return;
        if(desired==EncounterState.ATTACKING){getNavigator().clearPathEntity();attack(player);}
        else if(desired==EncounterState.HUNTING)navigate(player,mode==3?2.65:(kind()==CreatureKind.PALE_MAN?1.8:1.45));
        else if(desired==EncounterState.STALKING){
            double standOff=kind()==CreatureKind.HEROBRINE?14:(kind()==CreatureKind.WATCHER?6:12);
            if(senses.distance>standOff)navigate(player,kind()==CreatureKind.PALE_MAN?.38:(kind()==CreatureKind.WATCHER?1.1:.7));else stopMoving();
        }else if(desired==EncounterState.FOLLOWING){
            if(kind()==CreatureKind.SHADOW){
                if(age%10==0){double a=Math.toRadians(player.rotationYaw+90);moveTowards(player.posX+Math.sin(a)*7,player.posY,player.posZ-Math.cos(a)*7,1.1);}
            }else if(senses.distance>18)navigate(player,.75);else stopMoving();
        }else stopMoving();
        if((kind()==CreatureKind.CRAWLER||kind()==CreatureKind.DWELLER)&&isCollidedHorizontally&&player.posY>posY+.5)motionY=.16;
        if(age>=nextSound){
            nextSound=age+160+rand.nextInt(180);
            if(kind()==CreatureKind.MIMIC&&desired==EncounterState.OBSERVING&&HorrorConfig.soundEffects){
                String[] lures={"mob.cow.say","mob.pig.say","step.stone","mob.sheep.say"};
                worldObj.playSoundAtEntity(this,lures[rand.nextInt(lures.length)],(float)(.5*HorrorConfig.volume),.87F);
            }else if(kind()!=CreatureKind.WATCHER||desired==EncounterState.HUNTING)
                EventEngine.sound(player,"creatures."+kind().id+(desired==EncounterState.HUNTING?".hunt":".ambient"),posX,posY,posZ,kind()==CreatureKind.WATCHER?.12F:.65F);
        }
    }
    private void navigate(EntityPlayerMP player,double speed){if(age%10==0)moveTowards(player.posX,player.posY,player.posZ,speed);}
    private void moveTowards(double x,double y,double z,double speed){
        // World.getEntityPathToXYZ creates a ChunkCache of (searchRange + 8).
        // Vanilla ChunkCache requests all of those chunks, even absent ones.
        int cacheRadius=(int)(getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue()+8);
        if(!SafeSpawns.loadedArea(worldObj,(int)Math.floor(posX),(int)Math.floor(posZ),cacheRadius)){
            getNavigator().clearPathEntity();return;
        }
        double dx=x-posX,dy=y-posY,dz=z-posZ,distance=Math.sqrt(dx*dx+dy*dy+dz*dz);
        if(distance>10){x=posX+dx*10/distance;y=posY+dy*10/distance;z=posZ+dz*10/distance;}
        getNavigator().tryMoveToXYZ(x,y,z,speed);
    }
    private void stopMoving(){getNavigator().clearPathEntity();moveForward=0;motionX*=.35;motionZ*=.35;}
    private void attack(EntityPlayerMP player){
        if(mode==3||progression<2||kind()==CreatureKind.WATCHER||player.capabilities.isCreativeMode
                ||worldObj.difficultySetting==EnumDifficulty.PEACEFUL||attackCooldown>0||!canEntityBeSeen(player))return;
        double reach=kind()==CreatureKind.DWELLER?2.5:2.0;
        if(getDistanceSqToEntity(player)>reach*reach)return;
        attackCooldown=kind()==CreatureKind.CRAWLER?30:45;
        dataWatcher.updateObject(21,dataWatcher.getWatchableObjectInt(21)+1);
        dataWatcher.updateObject(23,(byte)(1-dataWatcher.getWatchableObjectByte(23)));
        if(HorrorConfig.damageScale>0)player.attackEntityFrom(DamageSource.causeMobDamage(this),(float)(kind().damage*HorrorConfig.damageScale));
        Scares.trigger(player,kind().ordinal()%4,false);
    }
    public boolean watched(EntityPlayerMP player){
        if(!SafeSpawns.loadedRectangle(worldObj,(int)Math.floor(Math.min(posX,player.posX)),(int)Math.floor(Math.min(posZ,player.posZ)),
                (int)Math.floor(Math.max(posX,player.posX)),(int)Math.floor(Math.max(posZ,player.posZ))))return false;
        Vec3 look=player.getLookVec();
        Vec3 delta=Vec3.createVectorHelper(posX-player.posX,posY+height*.75-player.posY-player.getEyeHeight(),posZ-player.posZ);
        double distance=delta.lengthVector();
        return distance>.01&&look.dotProduct(delta.normalize())>.965&&player.canEntityBeSeen(this);
    }
    private EntityPlayerMP owner(){
        if(owner==null)return null;
        for(Object value:worldObj.playerEntities)if(value instanceof EntityPlayerMP&&((EntityPlayerMP)value).getUniqueID().equals(owner))return(EntityPlayerMP)value;
        return null;
    }
    @Override protected String getHurtSound(){return HorrorConfig.soundEffects?"truehorror:creatures."+kind().id+".hurt":null;}
    @Override protected String getDeathSound(){return getHurtSound();}
    @Override protected float getSoundVolume(){return(float)(.5*HorrorConfig.volume);}
    @Override public void setDead(){super.setDead();if(!worldObj.isRemote&&TrueHorror.director!=null)TrueHorror.director.gone(this);}
    @Override public void writeEntityToNBT(NBTTagCompound nbt){
        super.writeEntityToNBT(nbt);if(owner!=null)nbt.setString("HorrorOwner",owner.toString());
        nbt.setInteger("HorrorMode",mode);nbt.setInteger("HorrorStage",progression);nbt.setInteger("HorrorAge",age);
        nbt.setInteger("HorrorLifetime",lifetime);nbt.setBoolean("HorrorGallery",gallery);
    }
    @Override public void readEntityFromNBT(NBTTagCompound nbt){
        super.readEntityFromNBT(nbt);try{owner=UUID.fromString(nbt.getString("HorrorOwner"));}catch(IllegalArgumentException ignored){owner=null;}
        mode=Math.max(0,Math.min(3,nbt.getInteger("HorrorMode")));progression=Math.max(0,Math.min(3,nbt.getInteger("HorrorStage")));
        age=Math.max(0,nbt.getInteger("HorrorAge"));lifetime=Math.max(20,Math.min(6000,nbt.getInteger("HorrorLifetime")));
        gallery=nbt.getBoolean("HorrorGallery");state(EncounterState.MANIFESTING);
    }
}
