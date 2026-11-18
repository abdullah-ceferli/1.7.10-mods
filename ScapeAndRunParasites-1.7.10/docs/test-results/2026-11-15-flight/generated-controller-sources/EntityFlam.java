package dev.srpbackport.tests.flight.samples;
import net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;
public class EntityFlam extends dev.srpbackport.tests.FlightActor {
public EntityFlam(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}
public static boolean access$000(EntityFlam actor){return actor.moveM;}
class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityFlam vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO && !EntityFlam.this.moveM) {
                double d0 = this.posX - EntityFlam.this.posX;
                double d1 = this.posY - EntityFlam.this.posY;
                double d2 = this.posZ - EntityFlam.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityFlam.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityFlam.this.motionX *= 0.5;
                    EntityFlam.this.motionY *= 0.5;
                    EntityFlam.this.motionZ *= 0.5;
                } else {
                    EntityFlam.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityFlam.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityFlam.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityFlam.this.getAttackTarget() == null) {
                        EntityFlam.this.renderYawOffset = EntityFlam.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityFlam.this.motionX, (double)EntityFlam.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityFlam.this.getAttackTarget().posX - EntityFlam.this.posX;
                        double d5 = EntityFlam.this.getAttackTarget().posZ - EntityFlam.this.posZ;
                        EntityFlam.this.renderYawOffset = EntityFlam.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}
