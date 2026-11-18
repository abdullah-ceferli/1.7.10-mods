package com.dhanantry.scapeandrunparasites.backport.entity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/** Modern saved attribute names; native equipment is represented by transient slot modifiers. */
public final class ModernAttributes {
    private ModernAttributes() {}
    public static final IAttribute ARMOR = create("generic.armor", 0, 0, 30);
    public static final IAttribute ARMOR_TOUGHNESS = create("generic.armorToughness", 0, 0, 20);
    private static final UUID[][] EQUIPMENT = new UUID[2][4];
    static {
        for (int stat=0;stat<2;stat++) for (int slot=0;slot<4;slot++)
            EQUIPMENT[stat][slot]=UUID.nameUUIDFromBytes(("srparasites:native-armor/"+stat+"/"+slot).getBytes(StandardCharsets.UTF_8));
    }
    private static IAttribute create(String name,double def,double min,double max) {
        RangedAttribute attribute=new RangedAttribute(name,def,min,max);
        attribute.setShouldWatch(true);
        return attribute;
    }
    /** Native base applyEntityAttributes calls this before subclass attribute setup or entity NBT loading. */
    public static void register(EntityLivingBase entity) {
        if(entity.getEntityAttribute(ARMOR)==null) {
            IAttributeInstance armor=entity.getAttributeMap().registerAttribute(ARMOR);
            // Native zombie armor is hard-coded to +2 and wither armor to 4; modern vanilla uses these bases.
            armor.setBaseValue(entity instanceof EntityZombie?2:entity instanceof EntityWither?4:0);
        }
        if(entity.getEntityAttribute(ARMOR_TOUGHNESS)==null) entity.getAttributeMap().registerAttribute(ARMOR_TOUGHNESS);
    }
    /** Keep equipment separate from base values, so stat copying never absorbs worn armor. */
    public static void syncEquipment(EntityLivingBase entity) {
        register(entity);
        for(int slot=1;slot<=4;slot++) {
            ItemStack stack=entity.getEquipmentInSlot(slot);
            double armor=0,toughness=0;
            if(stack!=null&&stack.getItem() instanceof ItemArmor) {
                ItemArmor item=(ItemArmor)stack.getItem();
                if(item.armorType==4-slot) {
                    // Native item/NBT attribute modifiers are already applied by the engine. Never add them twice.
                    if(!stack.getAttributeModifiers().containsKey(ARMOR.getAttributeUnlocalizedName())) armor=item.damageReduceAmount;
                    if(!stack.getAttributeModifiers().containsKey(ARMOR_TOUGHNESS.getAttributeUnlocalizedName())
                            &&item.getArmorMaterial()==ItemArmor.ArmorMaterial.DIAMOND) toughness=2;
                }
            }
            update(entity.getEntityAttribute(ARMOR),EQUIPMENT[0][slot-1],armor);
            update(entity.getEntityAttribute(ARMOR_TOUGHNESS),EQUIPMENT[1][slot-1],toughness);
        }
    }
    private static void update(IAttributeInstance attribute,UUID id,double amount) {
        AttributeModifier old=attribute.getModifier(id);
        if(old!=null&&old.getOperation()==0&&old.getAmount()==amount) return;
        if(old!=null) attribute.removeModifier(old);
        if(amount!=0) attribute.applyModifier(new AttributeModifier(id,"SRP native armor equipment",amount,0).setSaved(false));
    }
}
