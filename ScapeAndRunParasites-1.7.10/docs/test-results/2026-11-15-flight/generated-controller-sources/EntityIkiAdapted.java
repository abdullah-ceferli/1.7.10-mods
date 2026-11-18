package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityIkiAdapted extends dev.srpbackport.tests.FlightActor {
public EntityIkiAdapted(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityIkiAdapted vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityIkiAdapted.this.posX;
                double d1 = this.posY - EntityIkiAdapted.this.posY;
                double d2 = this.posZ - EntityIkiAdapted.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityIkiAdapted.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityIkiAdapted.this.motionX *= 0.5;
                    EntityIkiAdapted.this.motionY *= 0.5;
                    EntityIkiAdapted.this.motionZ *= 0.5;
                } else {
                    EntityIkiAdapted.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityIkiAdapted.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityIkiAdapted.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityIkiAdapted.this.getAttackTarget() == null) {
                        EntityIkiAdapted.this.renderYawOffset = EntityIkiAdapted.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityIkiAdapted.this.motionX, (double)EntityIkiAdapted.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityIkiAdapted.this.getAttackTarget().posX - EntityIkiAdapted.this.posX;
                        double d5 = EntityIkiAdapted.this.getAttackTarget().posZ - EntityIkiAdapted.this.posZ;
                        EntityIkiAdapted.this.renderYawOffset = EntityIkiAdapted.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
