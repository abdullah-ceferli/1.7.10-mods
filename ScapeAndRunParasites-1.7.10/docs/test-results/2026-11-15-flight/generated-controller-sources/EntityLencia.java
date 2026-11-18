package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityLencia extends dev.srpbackport.tests.FlightActor {
public EntityLencia(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityLencia vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityLencia.this.posX;
                double d1 = this.posY - EntityLencia.this.posY;
                double d2 = this.posZ - EntityLencia.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityLencia.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityLencia.this.motionX *= 0.5;
                    EntityLencia.this.motionY *= 0.5;
                    EntityLencia.this.motionZ *= 0.5;
                } else {
                    EntityLencia.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityLencia.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityLencia.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityLencia.this.getAttackTarget() == null) {
                        EntityLencia.this.renderYawOffset = EntityLencia.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityLencia.this.motionX, (double)EntityLencia.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityLencia.this.getAttackTarget().posX - EntityLencia.this.posX;
                        double d5 = EntityLencia.this.getAttackTarget().posZ - EntityLencia.this.posZ;
                        EntityLencia.this.renderYawOffset = EntityLencia.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
