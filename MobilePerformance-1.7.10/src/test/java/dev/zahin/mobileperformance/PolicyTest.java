package dev.zahin.mobileperformance;

import com.google.gson.*;
import dev.zahin.mobileperformance.client.FrameStats;
import org.junit.Test;
import static org.junit.Assert.*;

public class PolicyTest {
    @Test public void nearObjectsAndUnknownCoordinatesStayVisible(){assertTrue(RenderPolicy.inRange(64,8));assertFalse(RenderPolicy.inRange(64.1,8));assertTrue(RenderPolicy.inRange(Double.NaN,8));assertTrue(RenderPolicy.inRange(Double.POSITIVE_INFINITY,8));assertEquals(25,RenderPolicy.distanceSq(3,0,4,0,0,0),0);}
    @Test public void specialRenderersAndCombatEffectsAreExcluded(){for(String s:new String[]{"com.hbm.entity.projectile.EntityBullet","com.hbm.tileentity.machine.TileEntityMachineBattery","dev.industrialtechnology.fluid.TileFluidMachine","net.minecraft.entity.player.EntityPlayerMP","net.minecraft.client.particle.EntityHugeExplodeFX","net.minecraft.client.particle.EntityFlameFX","net.minecraft.client.particle.EntityDiggingFX"}){assertFalse(RenderPolicy.ordinaryMob(s));assertFalse(RenderPolicy.decorativeTile(s));assertFalse(RenderPolicy.cosmeticParticle(s));}}
    @Test public void onlyIntendedCosmeticTypesAreBudgeted(){assertTrue(RenderPolicy.cosmeticParticle("net.minecraft.client.particle.EntitySmokeFX"));assertFalse(RenderPolicy.cosmeticParticle("com.hbm.particle.EntitySmokeFX"));assertTrue(RenderPolicy.decorativeTile("com.jkvin114.displaydelight.DisplayTile"));assertTrue(RenderPolicy.ordinaryMob("net.minecraft.entity.monster.EntityZombie"));}
    @Test public void nativeRateAndSlowAnimationIntervalsAreExact(){for(int interval=1;interval<=4;interval++){int count=0;for(int i=0;i<120;i++)if(RenderPolicy.animationTick(i,interval))count++;assertEquals(120/interval,count);}assertEquals(8,RenderPolicy.bound(-100,8,64));assertEquals(64,RenderPolicy.bound(9999,8,64));}
    @Test public void backupRoundTripsEveryField()throws Exception{GraphicsState n=new GraphicsState(16,0,2,260,4,1,true,true,true);JsonObject j=n.json();GraphicsState copy=GraphicsState.read(j);assertEquals(j,copy.json());assertEquals(16,copy.distance);assertTrue(copy.vsync);}
    @Test public void malformedOrUnsupportedBackupsAreRejected()throws Exception{GraphicsState n=new GraphicsState(16,0,2,260,4,1,true,true,true);JsonObject j=n.json();j.addProperty("renderDistance",0);assertThrows(java.io.IOException.class,()->GraphicsState.read(j));j.addProperty("renderDistance",4.5);assertThrows(java.io.IOException.class,()->GraphicsState.read(j));j.addProperty("renderDistance",4);j.addProperty("fancy","false");assertThrows(java.io.IOException.class,()->GraphicsState.read(j));j.addProperty("fancy",false);j.remove("particles");assertThrows(java.io.IOException.class,()->GraphicsState.read(j));}
    @Test public void framesKeepRecentWindowAndExcludePausedTime(){FrameStats s=new FrameStats();for(int i=1;i<=650;i++)s.frame(i*20_000_000L);assertEquals(600,s.samples());assertEquals(50,s.fps(),.001);assertEquals(20,s.p95Ms(),.001);s.pause();s.frame(100_000_000_000L);assertEquals(20,s.averageMs(),.001);s.reset();assertEquals(0,s.samples());}
}
