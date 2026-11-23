package dev.industrialtechnology.hatch;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class HatchGeometryTest {
    @Test public void retainedFloorExactlyFillsSpaceOutsideOneBlockOpening(){for(boolean ox:new boolean[]{false,true})for(boolean oz:new boolean[]{false,true}){List<HatchGeometry.Box> all=new ArrayList<HatchGeometry.Box>();double volume=0;for(int dx=0;dx<=(ox?1:0);dx++)for(int dz=0;dz<=(oz?1:0);dz++)for(HatchGeometry.Box b:HatchGeometry.floor(ox,oz,dx,dz)){volume+=(b.a-b.x)*(b.c-b.z);for(HatchGeometry.Box a:all)assertFalse(Math.min(a.a,b.a)>Math.max(a.x,b.x)+1e-8&&Math.min(a.c,b.c)>Math.max(a.z,b.z)+1e-8);all.add(b);}assertEquals((ox?2:1)*(oz?2:1)-1,volume,1e-8);}}
    @Test public void motionHasStableEndpointsAndContinuousReversal(){assertEquals(0,HatchGeometry.progress(0,true,0),0);assertEquals(1,HatchGeometry.progress(0,true,12),0);assertEquals(.5,HatchGeometry.progress(0,true,6),1e-7);float mid=HatchGeometry.progress(0,true,4);assertEquals(mid,HatchGeometry.progress(mid,false,0),0);assertEquals(0,HatchGeometry.progress(mid,false,100),0);assertEquals(1,HatchGeometry.progress(1,false,-1),0);}
    @Test public void openApertureHasNoLidAndPanelsRemainInsideRails(){for(boolean upper:new boolean[]{false,true})for(boolean ox:new boolean[]{false,true})for(boolean oz:new boolean[]{false,true}){assertTrue(HatchGeometry.lid(ox,oz,upper,1).isEmpty());double last=1;for(int step=0;step<=12;step++){double width=0;for(HatchGeometry.Box b:HatchGeometry.lid(ox,oz,upper,step/12F)){assertTrue(b.a>b.x&&b.c>b.z&&b.b>b.y);assertTrue(b.x>=(ox?.5:0)+.0625&&b.a<=(ox?.5:0)+.9375);width+=b.a-b.x;}assertTrue(width<=last+1e-6);last=width;}}}
}
