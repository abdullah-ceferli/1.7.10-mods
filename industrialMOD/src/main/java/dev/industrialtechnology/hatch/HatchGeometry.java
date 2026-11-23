package dev.industrialtechnology.hatch;

import java.util.*;

/** A one-block opening, with frame geometry shared by collision and rendering. */
public final class HatchGeometry {
    public static final int MOTION_TICKS = 12;
    public static final class Box {
        public final double x,y,z,a,b,c; public final int material;
        public Box(double x,double y,double z,double a,double b,double c,int material){this.x=x;this.y=y;this.z=z;this.a=a;this.b=b;this.c=c;this.material=material;}
        public boolean solid(){return material==0||material==3||material==5;}
        public Box clip(int dx,int dz){double X=Math.max(0,x-dx),Z=Math.max(0,z-dz),A=Math.min(1,a-dx),C=Math.min(1,c-dz);return A-X>1e-6&&C-Z>1e-6?new Box(X,y,Z,A,b,C,material):null;}
    }
    public static float progress(float from,boolean open,double elapsed){double t=Math.max(0,Math.min(1,elapsed/MOTION_TICKS));t=t*t*(3-2*t);return (float)(from+((open?1:0)-from)*t);}
    public static List<Box> frame(boolean ox,boolean oz,boolean upper){
        List<Box> out=new ArrayList<Box>();double x=ox?.5:0,z=oz?.5:0,y=upper?.8125:0,top=y+.1875,t=.0625;
        out.add(new Box(x,y,z,x+t,top,z+1,0));out.add(new Box(x+1-t,y,z,x+1,top,z+1,0));
        out.add(new Box(x+t,y,z,x+1-t,top,z+t,0));out.add(new Box(x+t,y,z+1-t,x+1-t,top,z+1,0));
        for(int i=0;i<16;i++){int m=(i&1)==0?1:2;out.add(new Box(x,top,z+i/16.,x+t,top+.0005,z+(i+1)/16.,m));out.add(new Box(x+1-t,top,z+i/16.,x+1,top+.0005,z+(i+1)/16.,m));}
        for(double bx:new double[]{x+.015625,x+.953125})for(double bz:new double[]{z+.015625,z+.953125})out.add(new Box(bx,top+.001,bz,bx+.03125,top+.008,bz+.03125,4));
        return out;
    }
    /** The two panels retract into the side rails; only the exposed portions remain. */
    public static List<Box> lid(boolean ox,boolean oz,boolean upper,float progress){
        List<Box> out=new ArrayList<Box>();double x=ox?.5:0,z=oz?.5:0,y=upper?.8125:0,t=.0625,travel=.4375*Math.max(0,Math.min(1,progress));
        if(travel<.4375-1e-6){out.add(new Box(x+t,y+.03125,z+t,x+.5-travel,y+.15625,z+1-t,3));out.add(new Box(x+.5+travel,y+.03125,z+t,x+1-t,y+.15625,z+1-t,3));}return out;
    }
    public static List<Box> boxes(boolean ox,boolean oz,boolean upper,boolean open){return boxes(ox,oz,upper,open?1:0);}
    public static List<Box> boxes(boolean ox,boolean oz,boolean upper,float progress){List<Box> out=frame(ox,oz,upper);out.addAll(lid(ox,oz,upper,progress));return out;}
    /** Full-height floor pieces outside the aperture, in a particular host cell. */
    public static List<Box> floor(boolean ox,boolean oz,int dx,int dz){
        List<Box> out=new ArrayList<Box>();double x=ox?.5:0,z=oz?.5:0;
        add(out,dx,dz,Math.min(dx+1,x),dz+1);add(out,Math.max(dx,x+1),dz,dx+1,dz+1);
        double lo=Math.max(dx,x),hi=Math.min(dx+1,x+1);
        add(out,lo,dz,hi,Math.min(dz+1,z));add(out,lo,Math.max(dz,z+1),hi,dz+1);return out;
    }
    private static void add(List<Box> out,double x,double z,double a,double c){if(a-x>1e-6&&c-z>1e-6)out.add(new Box(x,0,z,a,1,c,5));}
    private HatchGeometry(){}
}
