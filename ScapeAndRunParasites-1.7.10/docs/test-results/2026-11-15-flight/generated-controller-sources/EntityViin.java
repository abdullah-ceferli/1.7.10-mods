package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityViin extends dev.srpbackport.tests.FlightActor {
public EntityViin(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityViin vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityViin.this.posX;
                double d1 = this.posY - EntityViin.this.posY;
                double d2 = this.posZ - EntityViin.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityViin.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityViin.this.motionX *= 0.5;
                    EntityViin.this.motionY *= 0.5;
                    EntityViin.this.motionZ *= 0.5;
                } else {
                    EntityViin.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityViin.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityViin.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityViin.this.getAttackTarget() == null) {
                        EntityViin.this.renderYawOffset = EntityViin.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityViin.this.motionX, (double)EntityViin.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityViin.this.getAttackTarget().posX - EntityViin.this.posX;
                        double d5 = EntityViin.this.getAttackTarget().posZ - EntityViin.this.posZ;
                        EntityViin.this.renderYawOffset = EntityViin.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
