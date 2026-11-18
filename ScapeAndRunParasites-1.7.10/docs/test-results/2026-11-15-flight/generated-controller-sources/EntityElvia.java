package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityElvia extends dev.srpbackport.tests.FlightActor {
public EntityElvia(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityElvia vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityElvia.this.posX;
                double d1 = this.posY - EntityElvia.this.posY;
                double d2 = this.posZ - EntityElvia.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityElvia.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityElvia.this.motionX *= 0.5;
                    EntityElvia.this.motionY *= 0.5;
                    EntityElvia.this.motionZ *= 0.5;
                } else {
                    EntityElvia.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityElvia.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityElvia.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityElvia.this.getAttackTarget() == null) {
                        EntityElvia.this.renderYawOffset = EntityElvia.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityElvia.this.motionX, (double)EntityElvia.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityElvia.this.getAttackTarget().posX - EntityElvia.this.posX;
                        double d5 = EntityElvia.this.getAttackTarget().posZ - EntityElvia.this.posZ;
                        EntityElvia.this.renderYawOffset = EntityElvia.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
