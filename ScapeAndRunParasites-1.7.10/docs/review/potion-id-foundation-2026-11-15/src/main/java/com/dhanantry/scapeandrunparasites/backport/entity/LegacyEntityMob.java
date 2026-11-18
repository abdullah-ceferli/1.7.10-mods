package com.dhanantry.scapeandrunparasites.backport.entity;
import net.minecraft.world.World;import net.minecraft.entity.IEntityLivingData;import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;import com.dhanantry.scapeandrunparasites.backport.datasync.*;
/** Native 1.7 mob with the original modern Entity callback extension point. */
public abstract class LegacyEntityMob extends net.minecraft.entity.monster.EntityMob implements EntityDataListener {
    public LegacyEntityMob(World world){super(world);}
    /** Native spawning dispatches to the converted modern override exactly once. */
    @Override public IEntityLivingData onSpawnWithEgg(IEntityLivingData data){
        return onInitialSpawn(DifficultyAccess.at(worldObj,EntityQueries.position(this)),data);
    }
    /** Direct modern calls retain native initialization without re-entering the dispatcher. */
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty,IEntityLivingData data){
        return super.onSpawnWithEgg(data);
    }
    // The original vanilla Entity callback is empty; subclasses supply model/state reactions.
    public void notifyDataManagerChange(DataParameter<?> key){}
}
