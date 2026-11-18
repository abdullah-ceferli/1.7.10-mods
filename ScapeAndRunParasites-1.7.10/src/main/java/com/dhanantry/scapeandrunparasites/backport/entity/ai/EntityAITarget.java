package com.dhanantry.scapeandrunparasites.backport.entity.ai;
import java.util.UUID;
import net.minecraft.entity.*;import net.minecraft.entity.player.EntityPlayer;import net.minecraft.pathfinding.*;import net.minecraft.util.MathHelper;
/** Modern targeting state and eligibility, using native entity, sensing and pathfinding operations. */
public abstract class EntityAITarget extends net.minecraft.entity.ai.EntityAITarget {
    private final boolean nearbyOnly;
    private int targetSearchStatus,targetSearchDelay,targetUnseenTicks;
    protected EntityLivingBase target;
    protected int unseenMemoryTicks=60;
    public EntityAITarget(EntityCreature owner,boolean sight){this(owner,sight,false);}
    public EntityAITarget(EntityCreature owner,boolean sight,boolean nearby){super(owner,sight,nearby);nearbyOnly=nearby;}
    @Override public boolean continueExecuting(){
        EntityLivingBase candidate=taskOwner.getAttackTarget();if(candidate==null)candidate=target;
        if(candidate==null||!candidate.isEntityAlive())return false;
        net.minecraft.scoreboard.Team team=taskOwner.getTeam();if(team!=null&&candidate.getTeam()==team)return false;
        double range=getTargetDistance();if(taskOwner.getDistanceSqToEntity(candidate)>range*range)return false;
        if(shouldCheckSight){if(taskOwner.getEntitySenses().canSee(candidate))targetUnseenTicks=0;else if(++targetUnseenTicks>unseenMemoryTicks)return false;}
        if(candidate instanceof EntityPlayer&&((EntityPlayer)candidate).capabilities.disableDamage)return false;
        taskOwner.setAttackTarget(candidate);return true;
    }
    @Override public void startExecuting(){targetSearchStatus=0;targetSearchDelay=0;targetUnseenTicks=0;}
    @Override public void resetTask(){taskOwner.setAttackTarget(null);target=null;}
    /** Owner UUIDs use native string storage; compare valid UUIDs by identity as in the modern API. */
    private static Object ownerId(IEntityOwnable entity){
        String id=entity.func_152113_b();if(id==null||id.isEmpty())return null;
        try{return UUID.fromString(id);}catch(IllegalArgumentException invalid){return id;}
    }
    public static boolean isSuitableTarget(EntityLiving owner,EntityLivingBase candidate,boolean allowInvulnerable,boolean sight){
        if(candidate==null||candidate==owner||!candidate.isEntityAlive()||!owner.canAttackClass(candidate.getClass())||owner.isOnSameTeam(candidate))return false;
        Object id=owner instanceof IEntityOwnable?ownerId((IEntityOwnable)owner):null;
        if(id!=null){
            if(candidate instanceof IEntityOwnable&&id.equals(ownerId((IEntityOwnable)candidate)))return false;
            if(candidate==((IEntityOwnable)owner).getOwner())return false;
        }else if(candidate instanceof EntityPlayer&&!allowInvulnerable&&((EntityPlayer)candidate).capabilities.disableDamage)return false;
        return !sight||owner.getEntitySenses().canSee(candidate);
    }
    @Override protected boolean isSuitableTarget(EntityLivingBase candidate,boolean allowInvulnerable){
        if(!isSuitableTarget(taskOwner,candidate,allowInvulnerable,shouldCheckSight))return false;
        // The original new BlockPos(entity) floors posY directly, unlike Entity.getPosition().
        if(!taskOwner.isWithinHomeDistance(MathHelper.floor_double(candidate.posX),MathHelper.floor_double(candidate.posY),MathHelper.floor_double(candidate.posZ)))return false;
        if(nearbyOnly){if(--targetSearchDelay<=0)targetSearchStatus=0;if(targetSearchStatus==0)targetSearchStatus=canEasilyReach(candidate)?1:2;if(targetSearchStatus==2)return false;}
        return true;
    }
    private boolean canEasilyReach(EntityLivingBase candidate){
        targetSearchDelay=10+taskOwner.getRNG().nextInt(5);
        PathEntity path=taskOwner.getNavigator().getPathToEntityLiving(candidate);if(path==null)return false;
        PathPoint end=path.getFinalPathPoint();if(end==null)return false;
        int dx=end.xCoord-MathHelper.floor_double(candidate.posX),dz=end.zCoord-MathHelper.floor_double(candidate.posZ);
        return (double)(dx*dx+dz*dz)<=2.25;
    }
    public EntityAITarget setUnseenMemoryTicks(int ticks){unseenMemoryTicks=ticks;return this;}
}
