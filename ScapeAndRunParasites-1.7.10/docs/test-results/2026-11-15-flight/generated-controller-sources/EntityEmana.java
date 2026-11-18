package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityEmana extends dev.srpbackport.tests.FlightActor {
public EntityEmana(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityEmana vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityEmana.this.posX;
                double d1 = this.posY - EntityEmana.this.posY;
                double d2 = this.posZ - EntityEmana.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityEmana.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityEmana.this.motionX *= 0.5;
                    EntityEmana.this.motionY *= 0.5;
                    EntityEmana.this.motionZ *= 0.5;
                } else {
                    EntityEmana.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityEmana.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityEmana.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityEmana.this.getAttackTarget() == null) {
                        EntityEmana.this.renderYawOffset = EntityEmana.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityEmana.this.motionX, (double)EntityEmana.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityEmana.this.getAttackTarget().posX - EntityEmana.this.posX;
                        double d5 = EntityEmana.this.getAttackTarget().posZ - EntityEmana.this.posZ;
                        EntityEmana.this.renderYawOffset = EntityEmana.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
