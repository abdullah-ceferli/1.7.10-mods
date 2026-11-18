package com.dhanantry.scapeandrunparasites.backport.entity.ai;
import net.minecraft.entity.EntityLiving;import net.minecraft.world.World;
public final class EntityAILifecycle {
    private EntityAILifecycle(){}
    public static void constructed(EntityLiving entity,World world){
        if(world!=null&&!world.isRemote&&entity instanceof EntityAIInitializer)((EntityAIInitializer)entity).srp$initializeAI();
    }
}
