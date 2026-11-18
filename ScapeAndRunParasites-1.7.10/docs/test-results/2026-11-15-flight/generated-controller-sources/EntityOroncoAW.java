package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityOroncoAW extends dev.srpbackport.tests.FlightActor {
public EntityOroncoAW(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityOroncoAW vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityOroncoAW.this.posX;
                double d1 = this.posY - EntityOroncoAW.this.posY;
                double d2 = this.posZ - EntityOroncoAW.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityOroncoAW.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityOroncoAW.this.motionX *= 0.5;
                    EntityOroncoAW.this.motionY *= 0.5;
                    EntityOroncoAW.this.motionZ *= 0.5;
                } else {
                    EntityOroncoAW.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityOroncoAW.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityOroncoAW.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityOroncoAW.this.getAttackTarget() == null) {
                        EntityOroncoAW.this.renderYawOffset = EntityOroncoAW.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityOroncoAW.this.motionX, (double)EntityOroncoAW.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityOroncoAW.this.getAttackTarget().posX - EntityOroncoAW.this.posX;
                        double d5 = EntityOroncoAW.this.getAttackTarget().posZ - EntityOroncoAW.this.posZ;
                        EntityOroncoAW.this.renderYawOffset = EntityOroncoAW.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
