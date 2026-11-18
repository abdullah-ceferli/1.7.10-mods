package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityIki extends dev.srpbackport.tests.FlightActor {
public EntityIki(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityIki vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityIki.this.posX;
                double d1 = this.posY - EntityIki.this.posY;
                double d2 = this.posZ - EntityIki.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityIki.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityIki.this.motionX *= 0.5;
                    EntityIki.this.motionY *= 0.5;
                    EntityIki.this.motionZ *= 0.5;
                } else {
                    EntityIki.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityIki.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityIki.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityIki.this.getAttackTarget() == null) {
                        EntityIki.this.renderYawOffset = EntityIki.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityIki.this.motionX, (double)EntityIki.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityIki.this.getAttackTarget().posX - EntityIki.this.posX;
                        double d5 = EntityIki.this.getAttackTarget().posZ - EntityIki.this.posZ;
                        EntityIki.this.renderYawOffset = EntityIki.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
