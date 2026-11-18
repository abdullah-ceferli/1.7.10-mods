package dev.srpbackport.tests;
import java.util.UUID;import net.minecraft.entity.*;import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
/** Test-only native storage/coordinate boundaries for the unmodified original AI decision instructions. */
public final class RetaliationReferenceAccess {
    public static boolean home(EntityCreature entity,BlockPos pos){return entity.isWithinHomeDistance(pos.getX(),pos.getY(),pos.getZ());}
    public static UUID ownerId(IEntityOwnable entity){String id=entity.func_152113_b();return id==null||id.isEmpty()?null:UUID.fromString(id);}
    public static EntityLivingBase owner(IEntityOwnable entity){return (EntityLivingBase)entity.getOwner();}
}
