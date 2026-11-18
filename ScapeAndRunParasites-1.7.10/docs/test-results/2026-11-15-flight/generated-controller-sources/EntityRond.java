package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityRond extends dev.srpbackport.tests.FlightActor {
public EntityRond(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityRond vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityRond.this.posX;
                double d1 = this.posY - EntityRond.this.posY;
                double d2 = this.posZ - EntityRond.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityRond.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityRond.this.motionX *= 0.5;
                    EntityRond.this.motionY *= 0.5;
                    EntityRond.this.motionZ *= 0.5;
                } else {
                    EntityRond.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityRond.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityRond.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityRond.this.getAttackTarget() == null) {
                        EntityRond.this.renderYawOffset = EntityRond.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityRond.this.motionX, (double)EntityRond.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityRond.this.getAttackTarget().posX - EntityRond.this.posX;
                        double d5 = EntityRond.this.getAttackTarget().posZ - EntityRond.this.posZ;
                        EntityRond.this.renderYawOffset = EntityRond.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
