package com.dhanantry.scapeandrunparasites.backport.entity.ai;
import net.minecraft.entity.EntityLiving;
/** Native controller interface with the modern state used by SRP's custom flying controllers. */
public abstract class FlightMoveHelper extends net.minecraft.entity.ai.EntityMoveHelper {
    public enum Action {WAIT,MOVE_TO,STRAFE,JUMPING}
    protected Action action=Action.WAIT;
    protected final EntityLiving entity;
    protected FlightMoveHelper(EntityLiving entity){super(entity);this.entity=entity;posX=posY=posZ=0;}
    @Override public boolean isUpdating(){return action==Action.MOVE_TO;}
    @Override public void setMoveTo(double x,double y,double z,double speed){
        posX=x;posY=y;posZ=z;this.speed=speed;action=Action.MOVE_TO;
    }
    public double getX(){return posX;}public double getY(){return posY;}public double getZ(){return posZ;}
    /** Subclasses supply the original SRP flight update; ground movement is not substituted here. */
    @Override public abstract void onUpdateMoveHelper();
}
