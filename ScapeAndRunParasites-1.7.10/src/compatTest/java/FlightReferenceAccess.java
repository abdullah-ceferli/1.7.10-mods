package dev.srpbackport.tests;
import net.minecraft.entity.Entity;import net.minecraft.util.AxisAlignedBB;
public final class FlightReferenceAccess {
    public static AxisAlignedBB box(Entity entity){return entity.boundingBox;}
}
