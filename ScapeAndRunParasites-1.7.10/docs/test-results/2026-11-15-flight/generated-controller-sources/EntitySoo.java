package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntitySoo extends dev.srpbackport.tests.FlightActor {
public EntitySoo(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntitySoo vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntitySoo.this.posX;
                double d1 = this.posY - EntitySoo.this.posY;
                double d2 = this.posZ - EntitySoo.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntitySoo.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntitySoo.this.motionX *= 0.5;
                    EntitySoo.this.motionY *= 0.5;
                    EntitySoo.this.motionZ *= 0.5;
                } else {
                    EntitySoo.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntitySoo.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntitySoo.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntitySoo.this.getAttackTarget() == null) {
                        EntitySoo.this.renderYawOffset = EntitySoo.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntitySoo.this.motionX, (double)EntitySoo.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntitySoo.this.getAttackTarget().posX - EntitySoo.this.posX;
                        double d5 = EntitySoo.this.getAttackTarget().posZ - EntitySoo.this.posZ;
                        EntitySoo.this.renderYawOffset = EntitySoo.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
