package dev.industrialtechnology.builder.client;

public final class WheelLayout {
    public static int sector(double dx,double dy,double inner,double outer,int count){double r=dx*dx+dy*dy;if(r<inner*inner||r>outer*outer)return -1;double a=Math.atan2(dy,dx)+Math.PI/2+Math.PI/count;a=(a+Math.PI*2)%(Math.PI*2);return (int)(a/(Math.PI*2/count));}
    public static double angle(int sector,int count){return -Math.PI/2+sector*Math.PI*2/count;}
    private WheelLayout(){}
}
