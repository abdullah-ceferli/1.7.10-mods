package com.dhanantry.scapeandrunparasites.backport.entity.movement;
import java.util.Arrays;import net.minecraft.entity.Entity;
/** Typed callback dispatch retains native collision physics and prevents the super call from redispatching. */
public final class MovementAccess {
    private MovementAccess(){}
    public static boolean dispatch(Entity entity,double x,double y,double z){
        EntityMotionState state=(EntityMotionState)entity;
        if(state.srp$getBypassMove()){state.srp$setBypassMove(false);return false;}
        if(!(entity instanceof TypedMovement))return false;
        int depth=state.srp$getMoveDepth();state.srp$setMoveDepth(depth+1);MoverType old=state.srp$getMoverType();state.srp$setMoverType(MoverType.SELF);
        try{((TypedMovement)entity).moveEntity(MoverType.SELF,x,y,z);}finally{state.srp$setMoveDepth(depth);state.srp$setMoverType(old);}return true;
    }
    public static void move(Entity entity,MoverType type,double x,double y,double z){
        if(entity instanceof TypedMovement)((TypedMovement)entity).moveEntity(type,x,y,z);else nativeMove(entity,type,x,y,z);
    }
    public static void nativeMove(Entity entity,MoverType type,double x,double y,double z){
        EntityMotionState state=(EntityMotionState)entity;
        if(type==MoverType.PISTON&&!entity.noClip){
            double[] deltas=state.srp$getPistonDeltas();if(deltas==null){deltas=new double[3];state.srp$setPistonDeltas(deltas);}
            long tick=entity.worldObj.getTotalWorldTime();if(tick!=state.srp$getPistonTick()){Arrays.fill(deltas,0);state.srp$setPistonTick(tick);}
            int axis=x!=0?0:y!=0?1:z!=0?2:-1;if(axis<0)return;
            double requested=axis==0?x:axis==1?y:z;double total=net.minecraft.util.MathHelper.clamp_double(requested+deltas[axis],-0.51,0.51);
            double amount=total-deltas[axis];deltas[axis]=total;if(Math.abs(amount)<=(double)1.0E-5F)return;
            if(axis==0)x=amount;else if(axis==1)y=amount;else z=amount;
        }
        int depth=state.srp$getMoveDepth();MoverType previous=state.srp$getMoverType();boolean bypass=state.srp$getBypassMove();
        state.srp$setMoveDepth(depth+1);state.srp$setMoverType(type);state.srp$setBypassMove(true);
        try{entity.moveEntity(x,y,z);}finally{state.srp$setMoveDepth(depth);state.srp$setMoverType(previous);state.srp$setBypassMove(bypass);}
    }
    public static boolean sneaking(Entity entity){
        EntityMotionState state=(EntityMotionState)entity;MoverType type=state.srp$getMoverType();
        return entity.isSneaking()&&(state.srp$getMoveDepth()==0||type==MoverType.SELF||type==MoverType.PLAYER);
    }
    public static void blocks(Entity entity){((EntityMotionState)entity).srp$blockCollisions();}
}
