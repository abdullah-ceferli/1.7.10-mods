package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityLeer extends dev.srpbackport.tests.FlightActor {
public EntityLeer(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityLeer vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityLeer.this.posX;
                double d1 = this.posY - EntityLeer.this.posY;
                double d2 = this.posZ - EntityLeer.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityLeer.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityLeer.this.motionX *= 0.5;
                    EntityLeer.this.motionY *= 0.5;
                    EntityLeer.this.motionZ *= 0.5;
                } else {
                    EntityLeer.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityLeer.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityLeer.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityLeer.this.getAttackTarget() == null) {
                        EntityLeer.this.renderYawOffset = EntityLeer.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityLeer.this.motionX, (double)EntityLeer.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityLeer.this.getAttackTarget().posX - EntityLeer.this.posX;
                        double d5 = EntityLeer.this.getAttackTarget().posZ - EntityLeer.this.posZ;
                        EntityLeer.this.renderYawOffset = EntityLeer.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
