package dev.srpbackport.tests;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.world.World;
/** Test host for the real extracted SRP controller bodies; this is not a substitute for an original mob. */
public class FlightActor extends EntityLiving {
    public Entity target;public boolean moveM;
    public FlightActor(World world){super(world);}
    public void controller(EntityMoveHelper helper){moveHelper=helper;}
    public void resize(float width,float height){setSize(width,height);}
}
