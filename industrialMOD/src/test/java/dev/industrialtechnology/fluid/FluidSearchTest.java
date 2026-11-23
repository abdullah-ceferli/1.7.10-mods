package dev.industrialtechnology.fluid;
import org.junit.Test;
import static org.junit.Assert.*;
public class FluidSearchTest {
    @Test public void floodIsFiniteAndDeduplicated(){FluidSearch search=new FluidSearch();assertTrue(search.offer(1,0,0));assertFalse(search.offer(1,0,0));assertFalse(search.offer(17,0,0));int n=0,key;while((key=search.poll())>=0){assertTrue(Math.abs(FluidSearch.x(key))<=16);assertTrue(Math.abs(FluidSearch.y(key))<=16);assertTrue(Math.abs(FluidSearch.z(key))<=16);search.expand(key);assertTrue(search.queued()<=FluidSearch.MAX_QUEUE);assertTrue(++n<=FluidSearch.VOLUME);}assertEquals(FluidSearch.VOLUME,n);assertEquals(n,search.visited());assertEquals(-1,search.poll());}
    @Test public void noExpansionCannotCrossASolidCell(){FluidSearch search=new FluidSearch();search.offer(0,0,1);assertTrue(search.poll()>=0);assertTrue(search.isEmpty());assertEquals(1,search.visited());}
    @Test public void coordinatesRoundTripAtEveryBoundary(){for(int x:new int[]{-16,0,16})for(int y:new int[]{-16,0,16})for(int z:new int[]{-16,0,16}){FluidSearch search=new FluidSearch();search.offer(x,y,z);int k=search.poll();assertEquals(x,FluidSearch.x(k));assertEquals(y,FluidSearch.y(k));assertEquals(z,FluidSearch.z(k));}}
}
