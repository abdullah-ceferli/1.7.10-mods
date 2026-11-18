package com.dhanantry.scapeandrunparasites.backport.potion;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

/** Native numeric registration with a stable resource-name lookup for existing SRP effects. */
public class NativePotionBase extends Potion {
    public NativePotionBase(String name,boolean bad,int color) {
        super(NativePotionRegistry.id(name),bad,color);
        NativePotionRegistry.bind(name,this);
        setPotionName("mob_effect."+getRegistryName());
    }
    public NativePotionBase registerPotionAttributeModifier(net.minecraft.entity.ai.attributes.IAttribute attribute,String uuid,double amount,int operation){
        super.func_111184_a(attribute,uuid,amount,operation);return this;
    }
    public ResourceLocation getRegistryName(){return NativePotionRegistry.name(this);}
}
