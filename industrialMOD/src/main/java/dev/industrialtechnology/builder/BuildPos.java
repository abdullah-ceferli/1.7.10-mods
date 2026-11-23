package dev.industrialtechnology.builder;

public final class BuildPos {
    public final int x,y,z;
    public BuildPos(int x,int y,int z){this.x=x;this.y=y;this.z=z;}
    @Override public boolean equals(Object o){if(!(o instanceof BuildPos))return false;BuildPos p=(BuildPos)o;return p.x==x&&p.y==y&&p.z==z;}
    @Override public int hashCode(){return (x*31+y)*31+z;}
}
