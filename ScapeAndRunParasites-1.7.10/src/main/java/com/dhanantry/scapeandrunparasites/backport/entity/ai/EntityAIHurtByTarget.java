package com.dhanantry.scapeandrunparasites.backport.entity.ai;
import net.minecraft.entity.*;import net.minecraft.entity.passive.EntityTameable;import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;
/** Revenge-triggered targeting, modern sight memory and same-species helper recruitment. */
public class EntityAIHurtByTarget extends EntityAITarget {
    private final boolean callsForHelp;private int revengeTimerOld;private final Class<?>[] excludedHelpers;
    public EntityAIHurtByTarget(EntityCreature owner,boolean help,Class<?>... excluded){
        super(owner,true);callsForHelp=help;excludedHelpers=excluded;setMutexBits(1);
    }
    @Override public boolean shouldExecute(){
        EntityLivingBase attacker=taskOwner.getAITarget();
        return taskOwner.func_142015_aE()!=revengeTimerOld&&attacker!=null&&isSuitableTarget(attacker,false);
    }
    @Override public void startExecuting(){
        taskOwner.setAttackTarget(taskOwner.getAITarget());target=taskOwner.getAttackTarget();
        revengeTimerOld=taskOwner.func_142015_aE();unseenMemoryTicks=300;
        if(callsForHelp)alertOthers();super.startExecuting();
    }
    protected void alertOthers(){
        double range=getTargetDistance();AxisAlignedBB bounds=AxisAlignedBB.getBoundingBox(taskOwner.posX,taskOwner.posY,taskOwner.posZ,taskOwner.posX+1,taskOwner.posY+1,taskOwner.posZ+1).expand(range,10,range);
        for(EntityCreature helper:EntityQueries.entities(taskOwner.worldObj,taskOwner.getClass(),bounds)){
            if(helper==taskOwner||helper.getAttackTarget()!=null||
                taskOwner instanceof EntityTameable&&((EntityTameable)taskOwner).getOwner()!=((EntityTameable)helper).getOwner()||
                helper.isOnSameTeam(taskOwner.getAITarget()))continue;
            boolean excluded=false;for(Class<?> type:excludedHelpers)if(helper.getClass()==type){excluded=true;break;}
            if(!excluded)setEntityAttackTarget(helper,taskOwner.getAITarget());
        }
    }
    protected void setEntityAttackTarget(EntityCreature helper,EntityLivingBase attacker){helper.setAttackTarget(attacker);}
}
