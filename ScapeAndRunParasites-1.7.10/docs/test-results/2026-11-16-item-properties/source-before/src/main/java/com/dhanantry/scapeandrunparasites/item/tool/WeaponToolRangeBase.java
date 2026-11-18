/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.entity.projectile.EntityArrow$PickupStatus
 *  net.minecraft.entity.projectile.EntityTippedArrow
 *  net.minecraft.init.Enchantments
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArrow
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.stats.StatList
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WeaponToolRangeBase
extends ItemBow {
    private int bonus;
    private int damage;
    private int damageCap;
    private boolean calling;
    private byte idTool;

    public WeaponToolRangeBase(String name, int durability, int bonus, int damageC, int damage, boolean fear, byte id) {
        RegistryNames.set(this, "weapon_" + name);
        this.setUnlocalizedName("srparasites.weapon_" + name);
        this.maxStackSize = 1;
        this.bonus = bonus;
        this.damageCap = damageC;
        this.damage = damage;
        this.calling = fear;
        this.idTool = id;
        this.setMaxDamage(durability);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter(){

            @SideOnly(value=Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (entityIn == null) {
                    return 0.0f;
                }
                return !(entityIn.getActiveItemStack().getItem() instanceof WeaponToolRangeBase) ? 0.0f : (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 50.0f;
            }
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter(){

            @SideOnly(value=Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0f : 0.0f;
            }
        });
        this.addPropertyOverride(new ResourceLocation("vinni"), new IItemPropertyGetter(){

            @SideOnly(value=Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return 0.0f;
            }
        });
    }

    protected ItemStack findAmmo(EntityPlayer player) {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if (!this.isArrow(itemstack)) continue;
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    protected boolean isArrow(ItemStack stack) {
        return stack.getItem() instanceof ItemArrow;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.INFINITY, (ItemStack)stack) > 0;
            ItemStack itemstack = this.findAmmo(entityplayer);
            int i = this.getMaxItemUseDuration(stack) - timeLeft;
            if ((i = ForgeEventFactory.onArrowLoose((ItemStack)stack, (World)worldIn, (EntityPlayer)entityplayer, (int)i, (!itemstack.isEmpty() || flag ? 1 : 0) != 0)) < 0) {
                return;
            }
            double seconds = (72000 - timeLeft) / 20;
            double bonusD = seconds * (double)this.bonus;
            if (!itemstack.isEmpty() || flag) {
                float f;
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.arrow);
                }
                if ((double)(f = WeaponToolRangeBase.getArrowVelocity((int)i)) >= 0.1) {
                    boolean flag1;
                    boolean bl = flag1 = entityplayer.capabilities.isCreativeMode || itemstack.getItem() instanceof ItemArrow && ((ItemArrow)itemstack.getItem()).isInfinite(itemstack, stack, entityplayer);
                    if (!worldIn.isRemote) {
                        int k;
                        int j;
                        ItemArrow itemarrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.arrow);
                        EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, (EntityLivingBase)entityplayer);
                        if (entityarrow instanceof EntityTippedArrow) {
                            EntityTippedArrow tipped = (EntityTippedArrow)entityarrow;
                            tipped.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.BLEED_E, 200, 0, false, true));
                            tipped.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.DOD_SMOKE_TRAIL_E, 200, 0, false, true));
                        }
                        entityarrow.shoot((Entity)entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0f, f * 4.4f, 0.0f);
                        if (f == 1.0f) {
                            entityarrow.setIsCritical(true);
                        }
                        if ((j = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.POWER, (ItemStack)stack)) > 0) {
                            entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5 + 0.5);
                        }
                        if ((k = EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.PUNCH, (ItemStack)stack)) > 0) {
                            entityarrow.setKnockbackStrength(k);
                        }
                        if (EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.FLAME, (ItemStack)stack) > 0) {
                            entityarrow.setFire(100);
                        }
                        stack.damageItem(1, (EntityLivingBase)entityplayer);
                        if (flag1 || entityplayer.capabilities.isCreativeMode && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                            entityarrow.canBePickedUp = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        }
                        if (bonusD > (double)(this.damage * this.damageCap)) {
                            bonusD = this.damage * this.damageCap;
                        }
                        entityarrow.setDamage(entityarrow.getDamage() * bonusD);
                        entityarrow.setDamage(entityarrow.getDamage() + (double)this.damage);
                        worldIn.spawnEntityInWorld((Entity)entityarrow);
                    }
                    com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(worldIn, (EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.PLAYERS, 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
                    if (!flag1 && !entityplayer.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            entityplayer.inventory.deleteStack(itemstack);
                        }
                    }
                    entityplayer.triggerAchievement(StatList.getObjectUseStats((Item)this));
                }
            }
        }
    }

    public int getItemEnchantability() {
        return 10;
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote && this.calling && SRPConfigSystems.useScent && worldIn.rand.nextInt(10) == 0 && entityIn.ticksExisted % 40 == 0 && SRPSaveData.get(worldIn, 222).getDeveLevel() >= SRPConfigSystems.deveScentUse) {
            ((EntityLivingBase)entityIn).addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.PREY_E, 1200, 0, false, false));
        }
    }

    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        super.onCreated(stack, worldIn, playerIn);
    }

    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(EnumChatFormatting.YELLOW + I18n.format((String)("tootip.srparasites.weaponr." + this.idTool), (Object[])new Object[0]));
        tooltip.add(EnumChatFormatting.RED + I18n.format((String)("tootip.srparasites.weaponr." + this.idTool * 10), (Object[])new Object[0]));
        if (this.calling && SRPConfigSystems.useScent) {
            tooltip.add(EnumChatFormatting.BLACK + I18n.format((String)("tootip.srparasites.weaponr." + this.idTool * 100), (Object[])new Object[0]));
        }
    }
}

