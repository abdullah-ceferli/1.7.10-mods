package com.dhanantry.scapeandrunparasites.backport.util;
public enum Rotation {
    NONE,CLOCKWISE_90,CLOCKWISE_180,COUNTERCLOCKWISE_90;
    public Rotation add(Rotation other){return values()[(ordinal()+other.ordinal())&3];}
    public EnumFacing rotate(EnumFacing facing){if(facing.getAxis()==EnumFacing.Axis.Y)return facing;switch(this){case CLOCKWISE_90:return facing.rotateY();case CLOCKWISE_180:return facing.getOpposite();case COUNTERCLOCKWISE_90:return facing.rotateYCCW();default:return facing;}}
    public int rotate(int rotation,int fullTurn){return this==NONE?rotation:(rotation+ordinal()*fullTurn/4)%fullTurn;}
}
