package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityEmanaAdapted extends dev.srpbackport.tests.FlightActor {
public EntityEmanaAdapted(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityEmanaAdapted vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityEmanaAdapted.this.posX;
                double d1 = this.posY - EntityEmanaAdapted.this.posY;
                double d2 = this.posZ - EntityEmanaAdapted.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityEmanaAdapted.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityEmanaAdapted.this.motionX *= 0.5;
                    EntityEmanaAdapted.this.motionY *= 0.5;
                    EntityEmanaAdapted.this.motionZ *= 0.5;
                } else {
                    EntityEmanaAdapted.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityEmanaAdapted.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityEmanaAdapted.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityEmanaAdapted.this.getAttackTarget() == null) {
                        EntityEmanaAdapted.this.renderYawOffset = EntityEmanaAdapted.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityEmanaAdapted.this.motionX, (double)EntityEmanaAdapted.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityEmanaAdapted.this.getAttackTarget().posX - EntityEmanaAdapted.this.posX;
                        double d5 = EntityEmanaAdapted.this.getAttackTarget().posZ - EntityEmanaAdapted.this.posZ;
                        EntityEmanaAdapted.this.renderYawOffset = EntityEmanaAdapted.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
