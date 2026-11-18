/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.item.VengeanceGrappleHandler;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBookOfVengeance
extends Item {
    private static final int RANGE = 32;
    private static final int COOLDOWN_TICKS = 60;
    private static final int KB_COOLDOWN_TICKS = 80;
    private static final String NBT_KB_NEXT = "srp_kb_next";
    private static final double KB_RADIUS = 5.0;
    private static final double KB_STRENGTH = 2.2;
    private static final double KB_UP = 0.4;
    private static final double KB_SELF_JUMP_Y = 0.42;
    private static final boolean KB_CANCEL_DOWNWARD = true;

    public ItemBookOfVengeance() {
        RegistryNames.set(this, "book_of_vengeance");
        this.setUnlocalizedName("srparasites.book_of_vengeance");
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setMaxStackSize(1);
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        String base = "item.srparasites.book_of_vengeance.";
        tooltip.add(I18n.format((String)(base + "vengeance0"), (Object[])new Object[0]));
        tooltip.add(I18n.format((String)(base + "vengeance1"), (Object[])new Object[0]));
        tooltip.add(I18n.format((String)(base + "vengeance3"), (Object[])new Object[0]));
        tooltip.add(I18n.format((String)(base + "vengeance5"), (Object[])new Object[0]));
        tooltip.add(I18n.format((String)(base + "vengeance6"), (Object[])new Object[0]));
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (!world.isRemote) {
                if (!(world instanceof WorldServer)) {
                    return new ActionResult(EnumActionResult.FAIL, (Object)stack);
                }
                WorldServer ws = (WorldServer)world;
                if (ItemBookOfVengeance.isKnockbackOnCooldown(stack, ws.getTotalWorldTime())) {
                    return new ActionResult(EnumActionResult.FAIL, (Object)stack);
                }
                ItemBookOfVengeance.doKnockbackBurst(ws, player);
                ItemBookOfVengeance.setKnockbackCooldown(stack, ws.getTotalWorldTime() + 80L);
                player.swingArm(hand);
            }
            return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
        }
        if (player.getCooldownTracker().hasCooldown((Item)this)) {
            return new ActionResult(EnumActionResult.FAIL, (Object)stack);
        }
        if (!world.isRemote) {
            EntityLivingBase target = ItemBookOfVengeance.findLookTargetLiving(player, 32.0);
            if (target == null) {
                return new ActionResult(EnumActionResult.FAIL, (Object)stack);
            }
            if (!player.canEntityBeSeen((Entity)target)) {
                return new ActionResult(EnumActionResult.FAIL, (Object)stack);
            }
            VengeanceGrappleHandler.start(player, target);
            player.getCooldownTracker().setCooldown((Item)this, 60);
            player.swingArm(hand);
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    private static void doKnockbackBurst(WorldServer ws, EntityPlayer player) {
        ws.playSound(null, player.posX, player.posY, player.posZ, SRPSounds.VENGEANCE_CHAIN_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        ws.playSound(null, player.posX, player.posY, player.posZ, SRPSounds.VENGEANCE_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        ItemBookOfVengeance.spawnPinkBurst(ws, new Vec3d(player.posX, player.posY + 0.9, player.posZ), 26);
        AxisAlignedBB box = Boxes.grow(Boxes.entityBox(player), 5.0, 1.5, 5.0);
        List ents = EntityQueries.entities(ws, EntityLivingBase.class, box);
        for (EntityLivingBase ent : ents) {
            if (ent == null || ent.isDead || ent == player) continue;
            double dx = ent.posX - player.posX;
            double dz = ent.posZ - player.posZ;
            double distSq = dx * dx + dz * dz;
            if (distSq < 1.0E-4) {
                dx = (ws.rand.nextDouble() - 0.5) * 0.2;
                dz = (ws.rand.nextDouble() - 0.5) * 0.2;
                distSq = dx * dx + dz * dz;
            }
            double dist = Math.sqrt(distSq);
            double nx = dx / dist;
            double nz = dz / dist;
            double falloff = 1.0 - Math.min(1.0, dist / 5.0);
            double push = 2.2 * (0.45 + 0.55 * falloff);
            ent.motionX += nx * push;
            ent.motionZ += nz * push;
            ent.motionY = Math.max(ent.motionY, 0.4 + 0.15 * falloff);
            ent.velocityChanged = true;
        }
        player.motionY = Math.max(player.motionY, 0.42);
        player.velocityChanged = true;
        player.onGround = false;
        player.fallDistance = 0.0f;
        ws.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.6f, 1.6f);
        ws.spawnParticle(EnumParticleTypes.CLOUD, player.posX, player.posY + 0.1, player.posZ, 8, 0.25, 0.05, 0.25, 0.02, new int[0]);
    }

    private static void spawnPinkBurst(WorldServer ws, Vec3d center, int points) {
        double r = 1.0;
        double g = 0.25;
        double b = 0.85;
        for (int i = 0; i < points; ++i) {
            double ox = (ws.rand.nextDouble() - 0.5) * 1.1;
            double oy = (ws.rand.nextDouble() - 0.5) * 0.6;
            double oz = (ws.rand.nextDouble() - 0.5) * 1.1;
            ws.spawnParticle(EnumParticleTypes.REDSTONE, center.xCoord + ox, center.yCoord + oy, center.zCoord + oz, 0, 1.0, 0.25, 0.85, 1.0, new int[0]);
        }
    }

    private static boolean isKnockbackOnCooldown(ItemStack stack, long worldTime) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return false;
        }
        return tag.getLong(NBT_KB_NEXT) > worldTime;
    }

    private static void setKnockbackCooldown(ItemStack stack, long nextUseTick) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        tag.setLong(NBT_KB_NEXT, nextUseTick);
        stack.setTagCompound(tag);
    }

    private static EntityLivingBase findLookTargetLiving(EntityPlayer player, double range) {
        World world = player.worldObj;
        Vec3d eye = player.getPositionEyes(1.0f);
        Vec3d look = player.getLook(1.0f);
        Vec3d end = eye.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
        RayTraceResult blockHit = world.rayTraceBlocks(eye, end, false, true, false);
        if (blockHit != null && blockHit.typeOfHit == RayTraceResult.Type.BLOCK) {
            end = blockHit.hitVec;
        }
        AxisAlignedBB searchBox = Boxes.grow(Boxes.expand(Boxes.entityBox(player), look.xCoord * range, look.yCoord * range, look.zCoord * range), 1.0);
        List entities = world.getEntitiesWithinAABBExcludingEntity((Entity)player, searchBox);
        EntityLivingBase best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity e : entities) {
            double dist;
            AxisAlignedBB bb;
            RayTraceResult hit;
            EntityLivingBase living;
            if (!(e instanceof EntityLivingBase) || !e.canBeCollidedWith() || (living = (EntityLivingBase)e) == player || living.isDead || (hit = Boxes.calculateIntercept((bb = Boxes.grow(Boxes.entityBox(living), 0.3)), eye, end)) == null || !((dist = eye.distanceTo(hit.hitVec)) < bestDist)) continue;
            bestDist = dist;
            best = living;
        }
        if (best != null && !player.canEntityBeSeen(best)) {
            return null;
        }
        return best;
    }
}

