package com.dhanantry.scapeandrunparasites.backport.util;
public enum Mirror {
    NONE,LEFT_RIGHT,FRONT_BACK;
    public Rotation toRotation(EnumFacing facing){EnumFacing.Axis a=facing.getAxis();return this==LEFT_RIGHT&&a==EnumFacing.Axis.Z||this==FRONT_BACK&&a==EnumFacing.Axis.X?Rotation.CLOCKWISE_180:Rotation.NONE;}
    public EnumFacing mirror(EnumFacing facing){return toRotation(facing)==Rotation.CLOCKWISE_180?facing.getOpposite():facing;}
    public int mirrorRotation(int rotation,int fullTurn){int half=fullTurn/2;int signed=rotation>half?rotation-fullTurn:rotation;switch(this){case FRONT_BACK:return (half-signed+fullTurn)%fullTurn;case LEFT_RIGHT:return (fullTurn-signed)%fullTurn;default:return rotation;}}
}
