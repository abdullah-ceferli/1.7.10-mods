/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.common.ObfuscationReflectionHelper
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.client.ClientQlipShake;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.item.ItemBase;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBough
extends ItemBase {
    private static final int USE_TICKS = 20;
    private static final int COOLDOWN_TICKS = 60;
    private static final float VOL_START = 3.5f;
    private static final float VOL_HURT = 3.0f;
    private static final float VOL_MID = 3.5f;
    private static final float VOL_DEATH = 3.5f;
    private static final String NBT_ACTIVE = "srp_bough_active";
    private static final String NBT_PREV_NODMG = "srp_bough_prev_nodmg";
    private static final String NBT_ADAPT_PLAYED = "srp_bough_adapt_played";
    private static final DamageSource SEPEKU_SRC = new DamageSource("sepeku").setDamageBypassesArmor().setDamageIsAbsolute().setDamageAllowedInCreativeMode();
    private static final Field F_LAST_DAMAGE = ItemBough.findLastDamageField();

    public ItemBough(String name, int maxStack, byte id) {
        super(name, maxStack, id);
        this.addPropertyOverride(new ResourceLocation("stab"), new IItemPropertyGetter(){

            public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
                if (entityIn == null) {
                    return 0.0f;
                }
                return entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0f : 0.0f;
            }
        });
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 20;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.getCooldownTracker().hasCooldown((Item)this)) {
            return new ActionResult(EnumActionResult.FAIL, (Object)stack);
        }
        playerIn.setActiveHand(handIn);
        playerIn.getCooldownTracker().setCooldown((Item)this, 60);
        playerIn.getEntityData().setBoolean(NBT_ADAPT_PLAYED, false);
        playerIn.playSound(SRPSounds.ADAPTED_V, 3.5f, 1.0f);
        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 3.0f, 1.0f);
        if (worldIn.isRemote && playerIn == Minecraft.getMinecraft().thePlayer) {
            ItemBough.triggerShakeClient();
        }
        if (!worldIn.isRemote && playerIn instanceof EntityPlayerMP) {
            ItemBough.setHoldInvuln((EntityPlayerMP)playerIn, true);
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    public void onUsingTick(ItemStack stack, EntityLivingBase living, int count) {
        if (!(living instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)living;
        if (player.worldObj.isRemote) {
            if (player == Minecraft.getMinecraft().thePlayer) {
                if ((count & 1) == 0) {
                    ItemBough.triggerShakeClient();
                }
                player.motionX = 0.0;
                player.motionZ = 0.0;
            }
            return;
        }
        player.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 6, 255, false, false));
        player.motionX = 0.0;
        player.motionY = 0.0;
        player.motionZ = 0.0;
        player.velocityChanged = true;
        if (SRPPotions.RAGE_E != null) {
            player.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.RAGE_E, 6, 0, false, false));
        }
        if (count == 1 && !player.getEntityData().getBoolean(NBT_ADAPT_PLAYED)) {
            player.playSound(SRPSounds.ADAPTATION_P, 3.5f, 1.0f);
            player.getEntityData().setBoolean(NBT_ADAPT_PLAYED, true);
        }
        if (player.worldObj instanceof WorldServer && (count & 1) == 0) {
            ItemBough.spawnBloodSpray((WorldServer)player.worldObj, player);
        }
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase living, int timeLeft) {
        if (!worldIn.isRemote && living instanceof EntityPlayerMP) {
            EntityPlayerMP p = (EntityPlayerMP)living;
            ItemBough.setHoldInvuln(p, false);
            p.removePotionEffect(net.minecraft.potion.Potion.moveSlowdown);
            if (SRPPotions.RAGE_E != null) {
                p.removePotionEffect(SRPPotions.RAGE_E);
            }
        }
    }

    public ItemStack onEaten(ItemStack stack, World worldIn, EntityLivingBase living) {
        if (worldIn.isRemote) {
            return stack;
        }
        if (!(living instanceof EntityPlayerMP)) {
            return stack;
        }
        EntityPlayerMP p = (EntityPlayerMP)living;
        p.removePotionEffect(net.minecraft.potion.Potion.moveSlowdown);
        if (SRPPotions.RAGE_E != null) {
            p.removePotionEffect(SRPPotions.RAGE_E);
        }
        p.playSound(SRPSounds.ALAFHA_SHOOTING, 3.5f, 1.0f);
        p.playSound(SRPSounds.ALAFHA_HURT, 3.5f, 1.0f);
        ItemBough.spawnReplacement(worldIn, (EntityPlayer)p);
        if (worldIn instanceof WorldServer) {
            ItemBough.spawnBloodBurst((WorldServer)worldIn, (EntityPlayer)p);
        }
        ItemBough.grantSepekuAdvancement(p);
        ItemBough.setHoldInvuln(p, false);
        if (!p.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        ItemBough.killPlayerSepeku(p);
        return stack;
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int slot, boolean isSelected) {
        boolean stillUsing;
        if (worldIn.isRemote) {
            return;
        }
        if (!(entityIn instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP p = (EntityPlayerMP)entityIn;
        if (!p.getEntityData().getBoolean(NBT_ACTIVE)) {
            return;
        }
        boolean bl = stillUsing = p.isHandActive() && !p.getActiveItemStack().isEmpty() && p.getActiveItemStack().getItem() == this;
        if (!stillUsing) {
            ItemBough.setHoldInvuln(p, false);
            p.removePotionEffect(net.minecraft.potion.Potion.moveSlowdown);
            if (SRPPotions.RAGE_E != null) {
                p.removePotionEffect(SRPPotions.RAGE_E);
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    private static void triggerShakeClient() {
        ClientQlipShake.INSTANCE.triggerDelayed(20, 0, true, true, 4.0f);
    }

    private static void setHoldInvuln(EntityPlayerMP p, boolean enable) {
        if (enable) {
            p.getEntityData().setBoolean(NBT_ACTIVE, true);
            p.getEntityData().setBoolean(NBT_PREV_NODMG, p.capabilities.disableDamage);
            p.capabilities.disableDamage = true;
            p.sendPlayerAbilities();
        } else {
            boolean prev;
            if (!p.getEntityData().getBoolean(NBT_ACTIVE)) {
                return;
            }
            p.getEntityData().setBoolean(NBT_ACTIVE, false);
            p.capabilities.disableDamage = prev = p.getEntityData().getBoolean(NBT_PREV_NODMG);
            p.sendPlayerAbilities();
        }
    }

    private static void spawnReplacement(World world, EntityPlayer player) {
        ResourceLocation id;
        if (world.isRemote) {
            return;
        }
        if (!SRPConfigWorld.boughSpawnReplacement) {
            return;
        }
        String raw = SRPConfigWorld.boughReplacementMobId;
        if (raw == null) {
            return;
        }
        if ((raw = raw.trim()).isEmpty() || "none".equalsIgnoreCase(raw)) {
            return;
        }
        try {
            id = raw.indexOf(58) >= 0 ? new ResourceLocation(raw) : new ResourceLocation("srparasites", raw);
        }
        catch (Exception e) {
            id = new ResourceLocation("srparasites", "sim_adventurer");
        }
        Entity ent = EntityList.createEntityByIDFromName((ResourceLocation)id, (World)world);
        if (ent == null && (ent = EntityList.createEntityByIDFromName((ResourceLocation)(id = new ResourceLocation("srparasites", "sim_adventurer")), (World)world)) == null) {
            return;
        }
        ent.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        world.spawnEntityInWorld(ent);
    }

    private static void grantSepekuAdvancement(EntityPlayerMP p) {
        MinecraftServer srv = p.getServer();
        if (srv == null) {
            return;
        }
        ResourceLocation id = new ResourceLocation("srparasites", "sepeku");
        Advancement adv = srv.getAdvancementManager().getAdvancement(id);
        if (adv == null) {
            return;
        }
        p.getAdvancements().grantCriterion(adv, "done");
    }

    private static void spawnBloodSpray(WorldServer world, EntityPlayer player) {
        IBlockState red = BlockStates.defaultState(Blocks.redstone_block);
        int stateId = BlockStates.stateId((IBlockState)red);
        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(world, EnumParticleTypes.BLOCK_CRACK, player.posX, player.posY + (double)player.height * 0.55, player.posZ, 12, 0.25, 0.2, 0.25, 0.18, new int[]{stateId});
    }

    private static void spawnBloodBurst(WorldServer world, EntityPlayer player) {
        IBlockState red = BlockStates.defaultState(Blocks.redstone_block);
        int stateId = BlockStates.stateId((IBlockState)red);
        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(world, EnumParticleTypes.BLOCK_CRACK, player.posX, player.posY + (double)player.height * 0.6, player.posZ, 90, 0.35, 0.35, 0.35, 0.35, new int[]{stateId});
        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(world, EnumParticleTypes.DAMAGE_INDICATOR, player.posX, player.posY + (double)player.height * 0.7, player.posZ, 25, 0.25, 0.25, 0.25, 0.1, new int[0]);
    }

    private static void killPlayerSepeku(EntityPlayerMP p) {
        boolean wasCreative = p.capabilities.isCreativeMode;
        boolean wasNoDmg = p.capabilities.disableDamage;
        p.capabilities.isCreativeMode = false;
        p.capabilities.disableDamage = false;
        p.sendPlayerAbilities();
        p.hurtResistantTime = 0;
        p.hurtTime = 0;
        p.maxHurtTime = 0;
        ItemBough.resetLastDamage((EntityLivingBase)p);
        p.getCombatTracker().reset();
        boolean applied = p.attackEntityFrom(SEPEKU_SRC, Float.MAX_VALUE);
        if (!applied || !p.isDead && p.getHealth() > 0.0f) {
            try {
                float hpBefore = p.getHealth();
                p.getCombatTracker().trackDamage(SEPEKU_SRC, Float.MAX_VALUE, hpBefore);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            p.setHealth(0.0f);
            p.onDeath(SEPEKU_SRC);
            p.setDead();
        }
        if (!p.isDead && p.getHealth() > 0.0f) {
            p.capabilities.isCreativeMode = wasCreative;
            p.capabilities.disableDamage = wasNoDmg;
            p.sendPlayerAbilities();
        }
    }

    private static Field findLastDamageField() {
        try {
            Field f = ObfuscationReflectionHelper.findField(EntityLivingBase.class, (String)"field_110153_bc");
            f.setAccessible(true);
            return f;
        }
        catch (Exception f) {
            try {
                Field f2 = ObfuscationReflectionHelper.findField(EntityLivingBase.class, (String)"lastDamage");
                f2.setAccessible(true);
                return f2;
            }
            catch (Exception exception) {
                return null;
            }
        }
    }

    private static void resetLastDamage(EntityLivingBase e) {
        if (F_LAST_DAMAGE == null) {
            return;
        }
        try {
            F_LAST_DAMAGE.setFloat(e, 0.0f);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(EnumChatFormatting.YELLOW + I18n.format((String)"tooltip.srparasites.bough.line1", (Object[])new Object[0]));
        tooltip.add(EnumChatFormatting.GOLD + I18n.format((String)"tooltip.srparasites.bough.line2", (Object[])new Object[0]));
    }
}

