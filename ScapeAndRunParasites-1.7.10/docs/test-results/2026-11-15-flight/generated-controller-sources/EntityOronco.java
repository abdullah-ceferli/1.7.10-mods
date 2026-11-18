package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityOronco extends dev.srpbackport.tests.FlightActor {
public EntityOronco(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityOronco vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityOronco.this.posX;
                double d1 = this.posY - EntityOronco.this.posY;
                double d2 = this.posZ - EntityOronco.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityOronco.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityOronco.this.motionX *= 0.5;
                    EntityOronco.this.motionY *= 0.5;
                    EntityOronco.this.motionZ *= 0.5;
                } else {
                    EntityOronco.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityOronco.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityOronco.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityOronco.this.getAttackTarget() == null) {
                        EntityOronco.this.renderYawOffset = EntityOronco.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityOronco.this.motionX, (double)EntityOronco.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityOronco.this.getAttackTarget().posX - EntityOronco.this.posX;
                        double d5 = EntityOronco.this.getAttackTarget().posZ - EntityOronco.this.posZ;
                        EntityOronco.this.renderYawOffset = EntityOronco.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
