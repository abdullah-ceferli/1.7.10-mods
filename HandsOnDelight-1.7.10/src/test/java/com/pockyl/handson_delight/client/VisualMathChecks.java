package com.pockyl.handson_delight.client;

import org.joml.Vector3f;

/** Invariants for the original source's physical and pose algorithms, executed by the real client fixture. */
public final class VisualMathChecks {
    private static void require(boolean yes,String message){if(!yes)throw new AssertionError(message);}
    public static int run(){
        LiquidSim sim=new LiquidSim();sim.setColorOverride(0xa03f76e4);sim.update(0,.58F,0);require(Math.abs(sim.fraction()-.58F)<.0001,"Initial broth level differs from source");sim.impulse(.3F,.6F,.5F);require(sim.isRippling(),"Ripple did not start");
        for(int tick=0;tick<900;tick++){sim.update(1F/60,.94F,0);float sum=0;for(int row=0;row<LiquidSim.GRID;row++)for(int col=0;col<LiquidSim.GRID;col++){float h=sim.height(col/(LiquidSim.GRID-1F),row/(LiquidSim.GRID-1F));require(Float.isFinite(h)&&Math.abs(h)<=1.401F,"Unbounded liquid wave");sum+=h;}require(Math.abs(sum/(LiquidSim.GRID*LiquidSim.GRID))<.001F,"Waves changed liquid volume");}
        require(!sim.isRippling()&&Math.abs(sim.fraction()-.94F)<.001,"Liquid failed to settle at target volume");
        sim.leaveWake(.4F,.5F,.07F,.8F);require(sim.hasWake(),"Spoon wake did not appear");for(int i=0;i<1800;i++)sim.update(1F/60,.94F,0);require(!sim.hasWake(),"Spoon wake never faded");
        float previous=-1;for(int i=0;i<=100;i++){float t=i/100F,k=Easing.flight(t);require(k>=previous&&k>=0&&k<=1,"Camera flight is not monotonic");require(Math.abs(Easing.flightInverse(k)-t)<.003,"Camera easing does not round-trip");previous=k;}
        for(ToolStroke stroke:ToolStroke.values())if(stroke!=ToolStroke.CUT)for(int frame=0;frame<=100;frame++){ToolStroke.Pose pose=stroke.pose(new Vector3f(0,5,0),frame/100F*stroke.length,-4,4);Vector3f position=pose.nodePosition(stroke.icon),angles=pose.degrees();require(Float.isFinite(position.x)&&Float.isFinite(position.y)&&Float.isFinite(position.z)&&Float.isFinite(angles.x)&&Float.isFinite(angles.y)&&Float.isFinite(angles.z),"Invalid "+stroke+" pose");require(Math.abs(pose.rotation.lengthSquared()-1)<.005,"Tool orientation lost normalization");}
        SourceParticles particles=new SourceParticles();particles.spawn(SourceParticles.STEAM,0,0,0,0,1,0,501,0);require(particles.size()==400,"Station particle limit failed");particles.update(10,8,null);require(particles.size()==0,"Expired particles survived");return 6;
    }
}
