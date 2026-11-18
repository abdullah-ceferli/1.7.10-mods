package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityLum extends dev.srpbackport.tests.FlightActor {
public EntityLum(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityLum vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityLum.this.posX;
                double d1 = this.posY - EntityLum.this.posY;
                double d2 = this.posZ - EntityLum.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityLum.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityLum.this.motionX *= 0.5;
                    EntityLum.this.motionY *= 0.5;
                    EntityLum.this.motionZ *= 0.5;
                } else {
                    EntityLum.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityLum.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityLum.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityLum.this.getAttackTarget() == null) {
                        EntityLum.this.renderYawOffset = EntityLum.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityLum.this.motionX, (double)EntityLum.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityLum.this.getAttackTarget().posX - EntityLum.this.posX;
                        double d5 = EntityLum.this.getAttackTarget().posZ - EntityLum.this.posZ;
                        EntityLum.this.renderYawOffset = EntityLum.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
