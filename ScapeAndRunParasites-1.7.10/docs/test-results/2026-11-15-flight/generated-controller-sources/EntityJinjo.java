package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityJinjo extends dev.srpbackport.tests.FlightActor {
public EntityJinjo(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityJinjo vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityJinjo.this.posX;
                double d1 = this.posY - EntityJinjo.this.posY;
                double d2 = this.posZ - EntityJinjo.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityJinjo.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityJinjo.this.motionX *= 0.5;
                    EntityJinjo.this.motionY *= 0.5;
                    EntityJinjo.this.motionZ *= 0.5;
                } else {
                    EntityJinjo.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityJinjo.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityJinjo.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityJinjo.this.getAttackTarget() == null) {
                        EntityJinjo.this.renderYawOffset = EntityJinjo.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityJinjo.this.motionX, (double)EntityJinjo.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityJinjo.this.getAttackTarget().posX - EntityJinjo.this.posX;
                        double d5 = EntityJinjo.this.getAttackTarget().posZ - EntityJinjo.this.posZ;
                        EntityJinjo.this.renderYawOffset = EntityJinjo.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
