package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityButhol extends dev.srpbackport.tests.FlightActor {
public EntityButhol(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityButhol vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityButhol.this.posX;
                double d1 = this.posY - EntityButhol.this.posY;
                double d2 = this.posZ - EntityButhol.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityButhol.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityButhol.this.motionX *= 0.5;
                    EntityButhol.this.motionY *= 0.5;
                    EntityButhol.this.motionZ *= 0.5;
                } else {
                    EntityButhol.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityButhol.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityButhol.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityButhol.this.getAttackTarget() == null) {
                        EntityButhol.this.renderYawOffset = EntityButhol.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityButhol.this.motionX, (double)EntityButhol.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityButhol.this.getAttackTarget().posX - EntityButhol.this.posX;
                        double d5 = EntityButhol.this.getAttackTarget().posZ - EntityButhol.this.posZ;
                        EntityButhol.this.renderYawOffset = EntityButhol.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
