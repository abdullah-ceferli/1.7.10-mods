package dev.srpbackport.tests;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;

/** Native boundary substitutions for the original instruction comparison, independent of the port goal. */
public final class MeleeReferenceAccess {
    public static boolean home(EntityCreature entity, BlockPos position) {
        return entity.isWithinHomeDistance(position.getX(), position.getY(), position.getZ());
    }
    public static AxisAlignedBB box(Entity entity) { return entity.boundingBox; }
    public static boolean creative(EntityPlayer player) { return player.capabilities.isCreativeMode; }
    // Target-native policy: absent spectator mode maps to non-creative native damage immunity.
    public static boolean damageImmuneNonCreative(EntityPlayer player) {
        return player.capabilities.disableDamage && !player.capabilities.isCreativeMode;
    }
}
