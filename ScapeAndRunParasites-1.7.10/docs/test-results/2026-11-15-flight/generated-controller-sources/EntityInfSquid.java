package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityInfSquid extends dev.srpbackport.tests.FlightActor {
public EntityInfSquid(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityInfSquid vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityInfSquid.this.posX;
                double d1 = this.posY - EntityInfSquid.this.posY;
                double d2 = this.posZ - EntityInfSquid.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityInfSquid.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityInfSquid.this.motionX *= 0.5;
                    EntityInfSquid.this.motionY *= 0.5;
                    EntityInfSquid.this.motionZ *= 0.5;
                } else {
                    EntityInfSquid.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityInfSquid.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityInfSquid.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityInfSquid.this.getAttackTarget() == null) {
                        EntityInfSquid.this.renderYawOffset = EntityInfSquid.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityInfSquid.this.motionX, (double)EntityInfSquid.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityInfSquid.this.getAttackTarget().posX - EntityInfSquid.this.posX;
                        double d5 = EntityInfSquid.this.getAttackTarget().posZ - EntityInfSquid.this.posZ;
                        EntityInfSquid.this.renderYawOffset = EntityInfSquid.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
