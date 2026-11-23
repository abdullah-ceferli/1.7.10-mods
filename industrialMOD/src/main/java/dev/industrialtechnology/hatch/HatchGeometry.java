package dev.industrialtechnology.hatch;

import java.util.*;

/** One-block hatch, optionally shifted half a cell across an edge or a corner. */
public final class HatchGeometry {
    public static final class Box {
        public final double x,y,z,a,b,c;public final int material;
        Box(double x,double y,double z,double a,double b,double c,int material){this.x=x;this.y=y;this.z=z;this.a=a;this.b=b;this.c=c;this.material=material;}
        public boolean solid(){return material==0||material==3;}
        public Box clip(int dx,int dz){double X=Math.max(0,x-dx),Z=Math.max(0,z-dz),A=Math.min(1,a-dx),C=Math.min(1,c-dz);return A-X>1e-6&&C-Z>1e-6?new Box(X,y,Z,A,b,C,material):null;}
    }
    public static List<Box> boxes(boolean offsetX,boolean offsetZ,boolean upper,boolean open){
        ArrayList<Box> out=new ArrayList<Box>();double x=offsetX?.5:0,z=offsetZ?.5:0,y=upper?.8125:0,top=y+.1875,t=.0625;
        out.add(new Box(x,y,z,x+t,top,z+1,0));out.add(new Box(x+1-t,y,z,x+1,top,z+1,0));
        out.add(new Box(x+t,y,z,x+1-t,top,z+t,0));out.add(new Box(x+t,y,z+1-t,x+1-t,top,z+1,0));
        if(!open){out.add(new Box(x+t,y+.03125,z+t,x+1-t,top-.03125,z+1-t,3));}
        else {out.add(new Box(x+.0078125,y+.03125,z+t,x+t,top-.03125,z+1-t,0));out.add(new Box(x+1-t,y+.03125,z+t,x+.9921875,top-.03125,z+1-t,3));}
        for(int i=0;i<16;i++){int material=(i&1)==0?1:2;out.add(new Box(x,top,z+i/16.,x+t,top+.0005,z+(i+1)/16.,material));out.add(new Box(x+1-t,top,z+i/16.,x+1,top+.0005,z+(i+1)/16.,material));}
        for(double bx:new double[]{x+.015625,x+.953125})for(double bz:new double[]{z+.015625,z+.953125})out.add(new Box(bx,top+.001,bz,bx+.03125,top+.008,bz+.03125,4));
        return out;
    }
    private HatchGeometry(){}
}
