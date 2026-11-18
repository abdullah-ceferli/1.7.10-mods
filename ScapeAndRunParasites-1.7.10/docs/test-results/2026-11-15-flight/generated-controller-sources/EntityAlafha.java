package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityAlafha extends dev.srpbackport.tests.FlightActor {
public EntityAlafha(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityAlafha vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityAlafha.this.posX;
                double d1 = this.posY - EntityAlafha.this.posY;
                double d2 = this.posZ - EntityAlafha.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityAlafha.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityAlafha.this.motionX *= 0.5;
                    EntityAlafha.this.motionY *= 0.5;
                    EntityAlafha.this.motionZ *= 0.5;
                } else {
                    EntityAlafha.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityAlafha.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityAlafha.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityAlafha.this.getAttackTarget() == null) {
                        EntityAlafha.this.renderYawOffset = EntityAlafha.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityAlafha.this.motionX, (double)EntityAlafha.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityAlafha.this.getAttackTarget().posX - EntityAlafha.this.posX;
                        double d5 = EntityAlafha.this.getAttackTarget().posZ - EntityAlafha.this.posZ;
                        EntityAlafha.this.renderYawOffset = EntityAlafha.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
