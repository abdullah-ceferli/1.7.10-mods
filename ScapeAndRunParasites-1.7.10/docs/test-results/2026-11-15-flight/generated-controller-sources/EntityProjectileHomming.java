package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityProjectileHomming extends dev.srpbackport.tests.FlightActor {
public EntityProjectileHomming(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
public static Entity access$000(EntityProjectileHomming actor){return actor.target;}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityProjectileHomming vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityProjectileHomming.this.posX;
                double d1 = this.posY - EntityProjectileHomming.this.posY;
                double d2 = this.posZ - EntityProjectileHomming.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityProjectileHomming.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityProjectileHomming.this.motionX *= 0.5;
                    EntityProjectileHomming.this.motionY *= 0.5;
                    EntityProjectileHomming.this.motionZ *= 0.5;
                } else {
                    EntityProjectileHomming.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityProjectileHomming.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityProjectileHomming.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityProjectileHomming.this.target == null) {
                        EntityProjectileHomming.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityProjectileHomming.this.motionX, (double)EntityProjectileHomming.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = ((EntityProjectileHomming)EntityProjectileHomming.this).target.posX - EntityProjectileHomming.this.posX;
                        double d5 = ((EntityProjectileHomming)EntityProjectileHomming.this).target.posZ - EntityProjectileHomming.this.posZ;
                        EntityProjectileHomming.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
