package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityTenn extends dev.srpbackport.tests.FlightActor {
public EntityTenn(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityTenn vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityTenn.this.posX;
                double d1 = this.posY - EntityTenn.this.posY;
                double d2 = this.posZ - EntityTenn.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityTenn.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityTenn.this.motionX *= 0.5;
                    EntityTenn.this.motionY *= 0.5;
                    EntityTenn.this.motionZ *= 0.5;
                } else {
                    EntityTenn.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityTenn.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityTenn.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityTenn.this.getAttackTarget() == null) {
                        EntityTenn.this.renderYawOffset = EntityTenn.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityTenn.this.motionX, (double)EntityTenn.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityTenn.this.getAttackTarget().posX - EntityTenn.this.posX;
                        double d5 = EntityTenn.this.getAttackTarget().posZ - EntityTenn.this.posZ;
                        EntityTenn.this.renderYawOffset = EntityTenn.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
