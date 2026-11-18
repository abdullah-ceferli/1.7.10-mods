package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityInfDragonE extends dev.srpbackport.tests.FlightActor {
public EntityInfDragonE(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityInfDragonE vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityInfDragonE.this.posX;
                double d1 = this.posY - EntityInfDragonE.this.posY;
                double d2 = this.posZ - EntityInfDragonE.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityInfDragonE.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityInfDragonE.this.motionX *= 0.5;
                    EntityInfDragonE.this.motionY *= 0.5;
                    EntityInfDragonE.this.motionZ *= 0.5;
                } else {
                    EntityInfDragonE.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityInfDragonE.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityInfDragonE.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityInfDragonE.this.getAttackTarget() == null) {
                        EntityInfDragonE.this.renderYawOffset = EntityInfDragonE.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityInfDragonE.this.motionX, (double)EntityInfDragonE.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityInfDragonE.this.getAttackTarget().posX - EntityInfDragonE.this.posX;
                        double d5 = EntityInfDragonE.this.getAttackTarget().posZ - EntityInfDragonE.this.posZ;
                        EntityInfDragonE.this.renderYawOffset = EntityInfDragonE.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
