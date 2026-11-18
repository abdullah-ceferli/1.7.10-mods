package com.dhanantry.scapeandrunparasites.backport.entity;
import net.minecraft.world.World;import net.minecraft.entity.IEntityLivingData;import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;import com.dhanantry.scapeandrunparasites.backport.datasync.*;
/** Native 1.7 mob with the original modern Entity callback extension point. */
public abstract class LegacyEntityMob extends net.minecraft.entity.monster.EntityMob implements com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIInitializer, cpw.mods.fml.common.registry.IEntityAdditionalSpawnData, com.dhanantry.scapeandrunparasites.backport.entity.movement.TypedMovement, EntityDataListener, com.dhanantry.scapeandrunparasites.backport.sound.SoundCategoryProvider {
    public com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory getSoundCategory(){return com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE;}
    public void moveEntity(com.dhanantry.scapeandrunparasites.backport.entity.movement.MoverType type,double x,double y,double z){com.dhanantry.scapeandrunparasites.backport.entity.movement.MovementAccess.nativeMove(this,type,x,y,z);}
    // FML applies this initial flag before the client's first living-entity physics update.
    public void writeSpawnData(io.netty.buffer.ByteBuf buffer){buffer.writeBoolean(com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.get(this));buffer.writeLong(((com.dhanantry.scapeandrunparasites.backport.entity.movement.EntityMotionState)this).srp$getGravityRevision());}
    public void readSpawnData(io.netty.buffer.ByteBuf buffer){boolean value=buffer.readBoolean();long revision=buffer.readLong();com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.receive(this,value,revision);}
    @Override public final void srp$initializeAI(){initEntityAI();}
    /** The original vanilla callback is empty; converted mob overrides register their own goals. */
    protected void initEntityAI(){}
    /** Select native task-based scheduling instead of the old random-action AI path. */
    @Override protected boolean isAIEnabled(){return true;}
    public LegacyEntityMob(World world){super(world);}
    /** Native spawning dispatches to the converted modern override exactly once. */
    @Override public IEntityLivingData onSpawnWithEgg(IEntityLivingData data){
        return onInitialSpawn(DifficultyAccess.at(worldObj,EntityQueries.position(this)),data);
    }
    /** Direct modern calls retain native initialization without re-entering the dispatcher. */
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty,IEntityLivingData data){
        return super.onSpawnWithEgg(data);
    }
    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        ModernAttributes.register(this);
    }
    @Override public int getTotalArmorValue() {
        ModernAttributes.syncEquipment(this);
        return net.minecraft.util.MathHelper.floor_double(getEntityAttribute(ModernAttributes.ARMOR).getAttributeValue());
    }
    @Override protected float applyArmorCalculations(net.minecraft.util.DamageSource source,float damage) {
        if(!source.isUnblockable()) {
            damageArmor(damage);
            int armor=getTotalArmorValue();
            damage=ArmorMath.afterAbsorb(damage,(float)armor,
                (float)getEntityAttribute(ModernAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        }
        return damage;
    }
    // The original vanilla Entity callback is empty; subclasses supply model/state reactions.
    public void notifyDataManagerChange(DataParameter<?> key){}
}
