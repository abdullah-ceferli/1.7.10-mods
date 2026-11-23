package dev.industrialtechnology.builder;

/** Shared integer geometry. The caller budgets candidate visits, including empty sphere cells. */
public final class BuildShape {
    public static final int MAX_SIDE=33,MAX_BLOCKS=4096;
    public final BuildMode mode;public final boolean hollow;public final BuildPos a,b;
    public final int minX,minY,minZ,maxX,maxY,maxZ,nx,ny,nz,radius,steps,candidates;
    private int index;
    public BuildShape(BuildMode mode,boolean hollow,BuildPos start,BuildPos end){
        this.mode=mode;this.hollow=hollow;a=start;int dx=end.x-a.x,dy=end.y-a.y,dz=end.z-a.z;
        if(Math.abs((long)end.x-a.x)>MAX_SIDE-1||Math.abs((long)end.y-a.y)>MAX_SIDE-1||Math.abs((long)end.z-a.z)>MAX_SIDE-1)throw new IllegalArgumentException("Maximum span is 33 blocks per axis");
        int bx=end.x,by=end.y,bz=end.z;
        if(mode==BuildMode.SINGLE){bx=a.x;by=a.y;bz=a.z;}
        if(mode==BuildMode.LINE){if(Math.abs(dx)>=Math.abs(dy)&&Math.abs(dx)>=Math.abs(dz)){by=a.y;bz=a.z;}else if(Math.abs(dy)>=Math.abs(dz)){bx=a.x;bz=a.z;}else{bx=a.x;by=a.y;}}
        if(mode==BuildMode.WALL){if(Math.abs(dx)>=Math.abs(dz))bz=a.z;else bx=a.x;}
        if(mode==BuildMode.FLOOR||mode==BuildMode.CIRCLE)by=a.y;
        if(mode==BuildMode.SLOPE_FLOOR&&dx==0&&dz==0)by=a.y;
        b=new BuildPos(bx,by,bz);dx=bx-a.x;dy=by-a.y;dz=bz-a.z;
        radius=mode.round()?(int)Math.round(Math.sqrt((double)dx*dx+(double)dz*dz+(mode==BuildMode.SPHERE?(double)dy*dy:0))):0;
        minX=mode.round()?a.x-radius:Math.min(a.x,b.x);maxX=mode.round()?a.x+radius:Math.max(a.x,b.x);
        minZ=mode.round()?a.z-radius:Math.min(a.z,b.z);maxZ=mode.round()?a.z+radius:Math.max(a.z,b.z);
        minY=mode==BuildMode.SPHERE?a.y-radius:Math.min(a.y,b.y);maxY=mode==BuildMode.SPHERE?a.y+radius:Math.max(a.y,b.y);
        nx=maxX-minX+1;ny=maxY-minY+1;nz=maxZ-minZ+1;
        if(nx>MAX_SIDE||ny>MAX_SIDE||nz>MAX_SIDE)throw new IllegalArgumentException("Round shapes support radius up to 16 blocks");
        if(minY<0||maxY>255||Math.abs((long)minX)>29999980||Math.abs((long)maxX)>29999980||Math.abs((long)minZ)>29999980||Math.abs((long)maxZ)>29999980)throw new IllegalArgumentException("Shape crosses the world boundary");
        steps=mode==BuildMode.DIAGONAL_WALL?Math.max(Math.abs(dx),Math.abs(dz)):Math.max(Math.max(Math.abs(dx),Math.abs(dy)),Math.abs(dz));
        candidates=mode==BuildMode.DIAGONAL_LINE?steps+1:mode==BuildMode.DIAGONAL_WALL?(steps+1)*ny:mode==BuildMode.SLOPE_FLOOR?nx*nz:nx*ny*nz;
    }
    public boolean hasNext(){return index<candidates;}
    public int visited(){return index;}
    private int lerp(int from,int to,int step){return steps==0?from:from+(int)Math.round((double)(to-from)*step/steps);}
    public BuildPos next(){
        if(!hasNext())throw new IllegalStateException("End of shape");int i=index++,x,y,z;
        if(mode==BuildMode.DIAGONAL_LINE)return new BuildPos(lerp(a.x,b.x,i),lerp(a.y,b.y,i),lerp(a.z,b.z,i));
        if(mode==BuildMode.DIAGONAL_WALL){int step=i%(steps+1);return new BuildPos(lerp(a.x,b.x,step),minY+i/(steps+1),lerp(a.z,b.z,step));}
        x=minX+i%nx;z=minZ+(i/nx)%nz;y=minY+i/(nx*nz);
        if(mode==BuildMode.SLOPE_FLOOR){double t=Math.abs(b.x-a.x)>=Math.abs(b.z-a.z)?(b.x==a.x?0:(double)(x-a.x)/(b.x-a.x)):(double)(z-a.z)/(b.z-a.z);return new BuildPos(x,a.y+(int)Math.round((b.y-a.y)*t),z);}
        if(mode==BuildMode.FRAME){int edges=(x==minX||x==maxX?1:0)+(y==minY||y==maxY?1:0)+(z==minZ||z==maxZ?1:0);return edges>=2?new BuildPos(x,y,z):null;}
        if(mode==BuildMode.BOX&&hollow&&x!=minX&&x!=maxX&&y!=minY&&y!=maxY&&z!=minZ&&z!=maxZ)return null;
        if(mode.round()){
            int ox=x-a.x,oy=mode==BuildMode.SPHERE?y-a.y:0,oz=z-a.z;int d=ox*ox+oy*oy+oz*oz,r=radius*radius;if(d>r)return null;
            if(hollow){boolean surface=(ox+1)*(ox+1)+oy*oy+oz*oz>r||(ox-1)*(ox-1)+oy*oy+oz*oz>r||ox*ox+oy*oy+(oz+1)*(oz+1)>r||ox*ox+oy*oy+(oz-1)*(oz-1)>r;
                if(mode==BuildMode.SPHERE)surface|=ox*ox+(oy+1)*(oy+1)+oz*oz>r||ox*ox+(oy-1)*(oy-1)+oz*oz>r;
                if(mode==BuildMode.CYLINDER)surface|=y==minY||y==maxY;
                if(!surface)return null;
            }
        }
        return new BuildPos(x,y,z);
    }
}
