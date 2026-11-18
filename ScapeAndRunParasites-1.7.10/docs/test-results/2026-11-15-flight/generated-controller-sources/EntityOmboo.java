package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityOmboo extends dev.srpbackport.tests.FlightActor {
public EntityOmboo(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityOmboo vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityOmboo.this.posX;
                double d1 = this.posY - EntityOmboo.this.posY;
                double d2 = this.posZ - EntityOmboo.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityOmboo.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityOmboo.this.motionX *= 0.5;
                    EntityOmboo.this.motionY *= 0.5;
                    EntityOmboo.this.motionZ *= 0.5;
                } else {
                    EntityOmboo.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityOmboo.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityOmboo.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityOmboo.this.getAttackTarget() == null) {
                        EntityOmboo.this.renderYawOffset = EntityOmboo.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityOmboo.this.motionX, (double)EntityOmboo.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityOmboo.this.getAttackTarget().posX - EntityOmboo.this.posX;
                        double d5 = EntityOmboo.this.getAttackTarget().posZ - EntityOmboo.this.posZ;
                        EntityOmboo.this.renderYawOffset = EntityOmboo.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
