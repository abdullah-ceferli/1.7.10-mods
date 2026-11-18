package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityHiBlaze extends dev.srpbackport.tests.FlightActor {
public EntityHiBlaze(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityHiBlaze vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityHiBlaze.this.posX;
                double d1 = this.posY - EntityHiBlaze.this.posY;
                double d2 = this.posZ - EntityHiBlaze.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityHiBlaze.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityHiBlaze.this.motionX *= 0.5;
                    EntityHiBlaze.this.motionY *= 0.5;
                    EntityHiBlaze.this.motionZ *= 0.5;
                } else {
                    EntityHiBlaze.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityHiBlaze.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityHiBlaze.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityHiBlaze.this.getAttackTarget() == null) {
                        EntityHiBlaze.this.renderYawOffset = EntityHiBlaze.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityHiBlaze.this.motionX, (double)EntityHiBlaze.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityHiBlaze.this.getAttackTarget().posX - EntityHiBlaze.this.posX;
                        double d5 = EntityHiBlaze.this.getAttackTarget().posZ - EntityHiBlaze.this.posZ;
                        EntityHiBlaze.this.renderYawOffset = EntityHiBlaze.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
