package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityHeblu extends dev.srpbackport.tests.FlightActor {
public EntityHeblu(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityHeblu vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityHeblu.this.posX;
                double d1 = this.posY - EntityHeblu.this.posY;
                double d2 = this.posZ - EntityHeblu.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityHeblu.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityHeblu.this.motionX *= 0.5;
                    EntityHeblu.this.motionY *= 0.5;
                    EntityHeblu.this.motionZ *= 0.5;
                } else {
                    EntityHeblu.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityHeblu.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityHeblu.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityHeblu.this.getAttackTarget() == null) {
                        EntityHeblu.this.renderYawOffset = EntityHeblu.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityHeblu.this.motionX, (double)EntityHeblu.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityHeblu.this.getAttackTarget().posX - EntityHeblu.this.posX;
                        double d5 = EntityHeblu.this.getAttackTarget().posZ - EntityHeblu.this.posZ;
                        EntityHeblu.this.renderYawOffset = EntityHeblu.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
