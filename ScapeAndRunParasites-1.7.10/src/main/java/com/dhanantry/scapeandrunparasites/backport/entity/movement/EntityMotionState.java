package com.dhanantry.scapeandrunparasites.backport.entity.movement;
/** Implemented on native Entity by the movement transformer; state uses no DataWatcher slots. */
public interface EntityMotionState {
    boolean srp$getNoGravity();void srp$setNoGravity(boolean value);
    long srp$getGravityRevision();void srp$setGravityRevision(long value);
    boolean srp$getBypassMove();void srp$setBypassMove(boolean value);
    int srp$getMoveDepth();void srp$setMoveDepth(int value);
    MoverType srp$getMoverType();void srp$setMoverType(MoverType value);
    double[] srp$getPistonDeltas();void srp$setPistonDeltas(double[] value);
    long srp$getPistonTick();void srp$setPistonTick(long value);
    void srp$blockCollisions();
}
