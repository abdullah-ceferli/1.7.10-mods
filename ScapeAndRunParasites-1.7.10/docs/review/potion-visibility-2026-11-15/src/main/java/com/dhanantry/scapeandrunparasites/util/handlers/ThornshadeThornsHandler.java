/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.living.PotionEvent$PotionApplicableEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public class ThornshadeThornsHandler {
    private static final String TAG_ROOT = "srp_thornshade_thorns";
    private static final String TAG_USES = "Uses";
    private static final String TAG_COOLDOWN_UNTIL = "CooldownUntil";
    private static final String TAG_EXPLODE_DELAY = "ExplodeDelay";
    private static final float MAX_HP_ALLOWED = 120.0f;
    private static final String TAG_HAS_EXPLODED = "HasExplodedOnce";

    @SubscribeEvent
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (living == null || living.worldObj.isRemote) {
            return;
        }
        PotionEffect incoming = event.getPotionEffect();
        if (incoming == null || incoming.getPotion() != SRPPotions.THORNSHADE_THORNS_E) {
            return;
        }
        if (living instanceof EntityParasiteBase) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (living.getMaxHealth() > 120.0f) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (living.isPotionActive(SRPPotions.THORNSHADE_THORNS_E)) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (ThornshadeThornsHandler.isInfiniteDuration(incoming)) {
            event.setResult(Event.Result.DENY);
            return;
        }
        World world = living.worldObj;
        long now = world.getTotalWorldTime();
        NBTTagCompound data = ThornshadeThornsHandler.getThornshadeData(living);
        int uses = data.getInteger(TAG_USES);
        if (uses >= 2) {
            event.setResult(Event.Result.DENY);
            if (!data.hasKey(TAG_EXPLODE_DELAY)) {
                ThornshadeThornsHandler.scheduleExplosion(living, data);
            }
            ThornshadeThornsHandler.setThornshadeData(living, data);
            return;
        }
        long cooldownUntil = data.getLong(TAG_COOLDOWN_UNTIL);
        if (cooldownUntil > now) {
            event.setResult(Event.Result.DENY);
            return;
        }
        data.setInteger(TAG_USES, ++uses);
        int durationTicks = incoming.getDuration();
        long extraCooldownTicks = (long)durationTicks / 2L;
        data.setLong(TAG_COOLDOWN_UNTIL, now + extraCooldownTicks);
        ThornshadeThornsHandler.setThornshadeData(living, data);
        event.setResult(Event.Result.DEFAULT);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (living == null || living.worldObj.isRemote) {
            return;
        }
        NBTTagCompound data = ThornshadeThornsHandler.getThornshadeData(living);
        if (!data.hasKey(TAG_EXPLODE_DELAY)) {
            return;
        }
        int delay = data.getInteger(TAG_EXPLODE_DELAY);
        if (delay > 0) {
            ThornshadeThornsHandler.spawnBloodParticles(living.worldObj, living, 15);
            data.setInteger(TAG_EXPLODE_DELAY, --delay);
            ThornshadeThornsHandler.setThornshadeData(living, data);
            return;
        }
        data.removeTag(TAG_EXPLODE_DELAY);
        ThornshadeThornsHandler.setThornshadeData(living, data);
        ThornshadeThornsHandler.doExplosion(living);
    }

    private static void spawnBloodParticles(World world, EntityLivingBase entity, int count) {
        if (!(world instanceof WorldServer)) {
            return;
        }
        WorldServer ws = (WorldServer)world;
        double x = entity.posX;
        double y = entity.posY + (double)entity.height * 0.5;
        double z = entity.posZ;
        int redDustId = BlockStates.stateId((IBlockState)BlockStates.defaultState(Blocks.redstone_block));
        for (int i = 0; i < count; ++i) {
            double offsetX = (world.rand.nextDouble() - 0.5) * 0.6;
            double offsetY = world.rand.nextDouble() * 0.8;
            double offsetZ = (world.rand.nextDouble() - 0.5) * 0.6;
            double motionX = (world.rand.nextDouble() - 0.5) * 0.3;
            double motionY = world.rand.nextDouble() * 0.4 + 0.1;
            double motionZ = (world.rand.nextDouble() - 0.5) * 0.3;
            ws.spawnParticle(EnumParticleTypes.BLOCK_DUST, x + offsetX, y + offsetY, z + offsetZ, 0, motionX, motionY, motionZ, 0.0, new int[]{redDustId});
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target == null || target.worldObj.isRemote) {
            return;
        }
        PotionEffect eff = target.getActivePotionEffect(SRPPotions.THORNSHADE_THORNS_E);
        if (eff == null) {
            return;
        }
        if (ThornshadeThornsHandler.isInfiniteDuration(eff)) {
            return;
        }
        Entity trueSourceEntity = event.getSource().getEntity();
        if (!(trueSourceEntity instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase attacker = (EntityLivingBase)trueSourceEntity;
        float incoming = event.getAmount();
        if (incoming <= 0.0f) {
            return;
        }
        NBTTagCompound data = ThornshadeThornsHandler.getThornshadeData(target);
        int uses = data.getInteger(TAG_USES);
        float reflectFactor = uses <= 1 ? 0.25f : 0.5f;
        float reflected = incoming * reflectFactor;
        if (reflected <= 0.0f) {
            return;
        }
        attacker.attackEntityFrom(DamageSource.causeThornsDamage((Entity)target), reflected);
    }

    private static void scheduleExplosion(EntityLivingBase living, NBTTagCompound data) {
        if (data.getBoolean(TAG_HAS_EXPLODED)) {
            return;
        }
        data.setInteger(TAG_EXPLODE_DELAY, 20);
        World world = living.worldObj;
        world.playSound(null, living.posX, living.posY, living.posZ, SRPSounds.ADAPTATION_P, SoundCategory.PLAYERS, 1.5f, 0.8f + world.rand.nextFloat() * 0.4f);
    }

    private static void doExplosion(EntityLivingBase center) {
        double distSq;
        EntityPlayerMP player;
        Advancement adv;
        World world = center.worldObj;
        if (world.isRemote) {
            return;
        }
        NBTTagCompound centerData = ThornshadeThornsHandler.getThornshadeData(center);
        centerData.setBoolean(TAG_HAS_EXPLODED, true);
        if (center instanceof EntityPlayerMP && (adv = (player = (EntityPlayerMP)center).getServerForPlayer().getMinecraftServer().getAdvancementManager().getAdvancement(new ResourceLocation("srparasites", "thornshade_self_destruct"))) != null) {
            player.getAdvancements().grantCriterion(adv, "exploded");
        }
        ThornshadeThornsHandler.setThornshadeData(center, centerData);
        double x = center.posX;
        double y = center.posY;
        double z = center.posZ;
        float innerRadius = 3.0f;
        float outerRadius = 10.0f;
        AxisAlignedBB outerBox = Boxes.create(x - (double)outerRadius, y - (double)outerRadius, z - (double)outerRadius, x + (double)outerRadius, y + (double)outerRadius, z + (double)outerRadius);
        Explosion explosion = new Explosion(world, null, x, y, z, 3.0f, false, false);
        explosion.doExplosionA();
        world.playSound(null, x, y, z, SRPSounds.BUTHOL_BOOM, SoundCategory.PLAYERS, 2.0f, 1.0f);
        ThornshadeThornsHandler.spawnRadialParticles(world, x, y + (double)center.height * 0.5, z, innerRadius, 50, EnumParticleTypes.CLOUD);
        ThornshadeThornsHandler.spawnRadialParticles(world, x, y + (double)center.height * 0.5, z, outerRadius, 120, EnumParticleTypes.SPELL_WITCH);
        center.attackEntityFrom(DamageSource.magic.setDamageBypassesArmor().setDamageIsAbsolute(), Float.MAX_VALUE);
        AxisAlignedBB innerBox = Boxes.create(x - (double)innerRadius, y - (double)innerRadius, z - (double)innerRadius, x + (double)innerRadius, y + (double)innerRadius, z + (double)innerRadius);
        for (EntityLivingBase other : EntityQueries.entities(world, EntityLivingBase.class, innerBox)) {
            NBTTagCompound data;
            if (other == center || !other.isEntityAlive() || other instanceof EntityParasiteBase || (distSq = other.getDistanceSq(x, y, z)) > (double)(innerRadius * innerRadius) || !other.isPotionActive(SRPPotions.THORNSHADE_THORNS_E) || other.getMaxHealth() > 120.0f || (data = ThornshadeThornsHandler.getThornshadeData(other)).getBoolean(TAG_HAS_EXPLODED) || data.hasKey(TAG_EXPLODE_DELAY)) continue;
            data.setInteger(TAG_USES, Math.max(2, data.getInteger(TAG_USES)));
            ThornshadeThornsHandler.scheduleExplosion(other, data);
            ThornshadeThornsHandler.setThornshadeData(other, data);
        }
        for (EntityLivingBase other : EntityQueries.entities(world, EntityLivingBase.class, outerBox)) {
            if (other == center || !other.isEntityAlive() || other instanceof EntityParasiteBase || (distSq = other.getDistanceSq(x, y, z)) <= (double)(innerRadius * innerRadius) || other.getMaxHealth() > 120.0f || other.isPotionActive(SRPPotions.THORNSHADE_THORNS_E)) continue;
            other.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.THORNSHADE_THORNS_E, 600, 0, false, true));
            ThornshadeThornsHandler.spawnRadialParticles(world, other.posX, other.posY + (double)other.height * 0.5, other.posZ, 1.0f, 20, EnumParticleTypes.SPELL_WITCH);
        }
    }

    private static void spawnRadialParticles(World world, double x, double y, double z, float radius, int count, EnumParticleTypes type) {
        if (!(world instanceof WorldServer)) {
            return;
        }
        WorldServer ws = (WorldServer)world;
        int bloodStateId = BlockStates.stateId((IBlockState)BlockStates.defaultState(Blocks.redstone_block));
        for (int i = 0; i < count; ++i) {
            double angle = world.rand.nextDouble() * 2.0 * Math.PI;
            double ringRadius = (double)radius * (0.7 + world.rand.nextDouble() * 0.3);
            double px = x + ringRadius * Math.cos(angle);
            double pz = z + ringRadius * Math.sin(angle);
            double py = y + (world.rand.nextDouble() - 0.5) * ((double)radius * 0.2);
            double dirX = px - x;
            double dirZ = pz - z;
            double len = Math.sqrt(dirX * dirX + dirZ * dirZ);
            if (len == 0.0) {
                dirX = 1.0;
                dirZ = 0.0;
                len = 1.0;
            }
            dirX /= len;
            dirZ /= len;
            double baseOut = 0.4 + world.rand.nextDouble() * 0.5;
            double mistVy = 0.05 + world.rand.nextDouble() * 0.15;
            if (type == EnumParticleTypes.SPELL_WITCH) {
                ws.spawnParticle(EnumParticleTypes.BLOCK_DUST, px, py, pz, 0, dirX * baseOut * 0.6, mistVy, dirZ * baseOut * 0.6, 0.0, new int[]{bloodStateId});
            } else {
                ws.spawnParticle(type, px, py, pz, 1, dirX * baseOut * 0.3, mistVy, dirZ * baseOut * 0.3, 0.1, new int[0]);
            }
            double chunkScale = 0.6 + world.rand.nextDouble() * 0.8;
            double gx = dirX * chunkScale;
            double gz = dirZ * chunkScale;
            double gy = 0.25 + world.rand.nextDouble() * 0.6;
            ws.spawnParticle(EnumParticleTypes.BLOCK_DUST, px, py, pz, 0, gx, gy, gz, 0.0, new int[]{bloodStateId});
        }
    }

    private static NBTTagCompound getThornshadeData(EntityLivingBase entity) {
        NBTTagCompound root = entity.getEntityData();
        if (!root.hasKey(TAG_ROOT, 10)) {
            NBTTagCompound data = new NBTTagCompound();
            root.setTag(TAG_ROOT, (NBTBase)data);
            return data;
        }
        return root.getCompoundTag(TAG_ROOT);
    }

    private static void setThornshadeData(EntityLivingBase entity, NBTTagCompound data) {
        entity.getEntityData().setTag(TAG_ROOT, (NBTBase)data);
    }

    private static boolean isInfiniteDuration(PotionEffect effect) {
        return effect.getIsPotionDurationMax() || effect.getDuration() >= 72000 || effect.getDuration() == Integer.MAX_VALUE;
    }
}

