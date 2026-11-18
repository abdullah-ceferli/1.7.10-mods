package com.dhanantry.scapeandrunparasites.backport.util;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3i;
import com.google.common.base.Predicate;
import java.util.*;
import net.minecraft.util.MathHelper;
/** Original six-direction indexes, horizontal ordering, rotations and serialized names. */
public enum EnumFacing implements IStringSerializable {
    DOWN(0,-1,0,0,-1,0,Axis.Y,AxisDirection.NEGATIVE),
    UP(1,-1,0,0,1,0,Axis.Y,AxisDirection.POSITIVE),
    NORTH(2,2,0,0,0,-1,Axis.Z,AxisDirection.NEGATIVE),
    SOUTH(3,0,0,0,0,1,Axis.Z,AxisDirection.POSITIVE),
    WEST(4,1,0,-1,0,0,Axis.X,AxisDirection.NEGATIVE),
    EAST(5,3,0,1,0,0,Axis.X,AxisDirection.POSITIVE);
    public static final EnumFacing[] faceList=values();
    public static final EnumFacing[] HORIZONTALS={SOUTH,WEST,NORTH,EAST};
    private final int index,horizontalIndex,x,y,z;private final Axis axis;private final AxisDirection axisDirection;private final Vec3i vector;
    EnumFacing(int index,int horizontalIndex,int unused,int x,int y,int z,Axis axis,AxisDirection direction){this.index=index;this.horizontalIndex=horizontalIndex;this.x=x;this.y=y;this.z=z;this.axis=axis;this.axisDirection=direction;this.vector=new Vec3i(x,y,z);}
    public int getIndex(){return index;} public int getHorizontalIndex(){return horizontalIndex;}
    public int getFrontOffsetX(){return x;} public int getFrontOffsetY(){return y;} public int getFrontOffsetZ(){return z;}
    public Axis getAxis(){return axis;} public AxisDirection getAxisDirection(){return axisDirection;} public Vec3i getDirectionVec(){return vector;}
    public EnumFacing getOpposite(){return faceList[index^1];}
    public EnumFacing rotateY(){switch(this){case NORTH:return EAST;case EAST:return SOUTH;case SOUTH:return WEST;case WEST:return NORTH;default:throw new IllegalStateException("Cannot rotate vertical facing around Y: "+this);}}
    public EnumFacing rotateYCCW(){switch(this){case NORTH:return WEST;case WEST:return SOUTH;case SOUTH:return EAST;case EAST:return NORTH;default:throw new IllegalStateException("Cannot rotate vertical facing around Y: "+this);}}
    public static EnumFacing getFront(int index){return faceList[Math.abs(index%faceList.length)];}
    public static EnumFacing byHorizontalIndex(int index){return HORIZONTALS[Math.abs(index%HORIZONTALS.length)];}
    public static EnumFacing fromAngle(double angle){return byHorizontalIndex(MathHelper.floor_double(angle/90.0+0.5)&3);}
    public static EnumFacing random(Random random){return faceList[random.nextInt(faceList.length)];}
    public static EnumFacing byName(String name){if(name==null)return null;for(EnumFacing f:faceList)if(f.getName().equals(name.toLowerCase(Locale.ROOT)))return f;return null;}
    public static EnumFacing getFacingFromVector(float x,float y,float z){EnumFacing best=NORTH;float dot=Float.MIN_VALUE;for(EnumFacing f:faceList){float n=x*f.x+y*f.y+z*f.z;if(n>dot){dot=n;best=f;}}return best;}
    public String getName(){return name().toLowerCase(Locale.ROOT);}@Override public String toString(){return getName();}
    public enum Axis implements IStringSerializable { X,Y,Z;
        public boolean isVertical(){return this==Y;} public boolean isHorizontal(){return this!=Y;}
        public Plane getPlane(){return isVertical()?Plane.VERTICAL:Plane.HORIZONTAL;}
        public String getName(){return name().toLowerCase(Locale.ROOT);}@Override public String toString(){return getName();}
    }
    public enum AxisDirection { POSITIVE(1),NEGATIVE(-1);private final int offset;AxisDirection(int n){offset=n;}public int getOffset(){return offset;} }
    public enum Plane implements Iterable<EnumFacing>,Predicate<EnumFacing> { HORIZONTAL,VERTICAL;
        public EnumFacing[] facings(){return this==HORIZONTAL?new EnumFacing[]{NORTH,EAST,SOUTH,WEST}:new EnumFacing[]{UP,DOWN};}
        public EnumFacing random(Random random){EnumFacing[] choices=facings();return choices[random.nextInt(choices.length)];}
        public boolean apply(EnumFacing facing){return facing!=null&&facing.getAxis().getPlane()==this;}
        public Iterator<EnumFacing> iterator(){return Arrays.asList(facings()).iterator();}
    }
}
