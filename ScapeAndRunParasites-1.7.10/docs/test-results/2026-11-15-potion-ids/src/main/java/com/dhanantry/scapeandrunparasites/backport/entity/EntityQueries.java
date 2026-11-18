package com.dhanantry.scapeandrunparasites.backport.entity;
import java.util.List;import com.google.common.base.Predicate;import net.minecraft.entity.Entity;import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.command.IEntitySelector;import net.minecraft.util.AxisAlignedBB;import net.minecraft.world.World;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
/** Modern block positions and typed native engine collections, without changing list identity. */
public final class EntityQueries {
 private EntityQueries(){}
 public static BlockPos position(Entity entity){return new BlockPos(entity.posX,entity.posY+0.5,entity.posZ);}
 @SuppressWarnings("unchecked") public static <T extends Entity>List<T> entities(World world,Class<? extends T> type,AxisAlignedBB bounds){return (List<T>)(List<?>)world.getEntitiesWithinAABB(type,bounds);}
 @SuppressWarnings("unchecked") public static <T extends Entity>List<T> entities(World world,final Class<? extends T> type,AxisAlignedBB bounds,final Predicate<? super T> predicate){
  if(predicate==null)return entities(world,type,bounds);
  return (List<T>)(List<?>)world.selectEntitiesWithinAABB(type,bounds,new IEntitySelector(){public boolean isEntityApplicable(Entity entity){return predicate.apply(type.cast(entity));}});
 }
 @SuppressWarnings("unchecked") public static List<Entity> loaded(World world){return (List<Entity>)(List<?>)world.loadedEntityList;}
 @SuppressWarnings("unchecked") public static List<EntityPlayer> players(World world){return (List<EntityPlayer>)(List<?>)world.playerEntities;}
}
