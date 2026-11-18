package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityLumAdapted extends dev.srpbackport.tests.FlightActor {
public EntityLumAdapted(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityLumAdapted vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityLumAdapted.this.posX;
                double d1 = this.posY - EntityLumAdapted.this.posY;
                double d2 = this.posZ - EntityLumAdapted.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityLumAdapted.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityLumAdapted.this.motionX *= 0.5;
                    EntityLumAdapted.this.motionY *= 0.5;
                    EntityLumAdapted.this.motionZ *= 0.5;
                } else {
                    EntityLumAdapted.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityLumAdapted.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityLumAdapted.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityLumAdapted.this.getAttackTarget() == null) {
                        EntityLumAdapted.this.renderYawOffset = EntityLumAdapted.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityLumAdapted.this.motionX, (double)EntityLumAdapted.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityLumAdapted.this.getAttackTarget().posX - EntityLumAdapted.this.posX;
                        double d5 = EntityLumAdapted.this.getAttackTarget().posZ - EntityLumAdapted.this.posZ;
                        EntityLumAdapted.this.renderYawOffset = EntityLumAdapted.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
