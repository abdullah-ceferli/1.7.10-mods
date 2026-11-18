/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.effect.EntityLightningBolt
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ServerTickEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.bestiary.net.BestiaryNetwork;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.PacketVengeanceFX;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public final class VengeanceGrappleHandler {
    private static final VengeanceGrappleHandler INSTANCE = new VengeanceGrappleHandler();
    private static final Map<UUID, GrappleData> ACTIVE = new HashMap<UUID, GrappleData>();

    public static VengeanceGrappleHandler get() {
        return INSTANCE;
    }

    private VengeanceGrappleHandler() {
    }

    private static SimpleNetworkWrapper net() {
        return BestiaryNetwork.CH;
    }

    private static void fxAroundTracking(WorldServer ws, Vec3d pos, PacketVengeanceFX pkt) {
        for (EntityPlayerMP p : ws.getMinecraftServer().getPlayerList().getPlayers()) {
            if (p.dimension != ws.provider.dimensionId || p.getDistanceSq(pos.xCoord, pos.yCoord, pos.zCoord) > 4096.0) continue;
            VengeanceGrappleHandler.net().sendTo((IMessage)pkt, p);
        }
    }

    public static void start(EntityPlayer player, EntityLivingBase target) {
        if (player == null || target == null) {
            return;
        }
        if (player.isDead || target.isDead) {
            return;
        }
        if (!(player.worldObj instanceof WorldServer)) {
            return;
        }
        GrappleData d = new GrappleData(player.getUniqueID(), target.getUniqueID(), player.dimension);
        d.stage = Stage.WINDUP;
        d.timer = 20;
        ACTIVE.put(player.getUniqueID(), d);
        WorldServer ws = (WorldServer)player.worldObj;
        ws.playSound(null, player.posX, player.posY, player.posZ, SRPSounds.VENGEANCE_PAPER, SoundCategory.PLAYERS, 2.0f, 1.0f);
    }

    @SubscribeEvent
    public void onPlayerAttacked(LivingAttackEvent e) {
        if (!(e.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer p = (EntityPlayer)e.getEntityLiving();
        GrappleData d = ACTIVE.get(p.getUniqueID());
        if (d == null) {
            return;
        }
        Entity src = e.getSource().getEntity();
        if (src != null && src.getUniqueID().equals(d.targetId)) {
            e.setCanceled(true);
            p.hurtResistantTime = 20;
        }
    }

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent e) {
        if (!(e.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer p = (EntityPlayer)e.getEntityLiving();
        GrappleData d = ACTIVE.get(p.getUniqueID());
        if (d == null) {
            return;
        }
        if (e.getSource() == DamageSource.LIGHTNING_BOLT) {
            e.setCanceled(true);
            return;
        }
        String dt = e.getSource().damageType;
        if ("explosion".equals(dt) || "explosion.player".equals(dt)) {
            e.setCanceled(true);
        }
    }

    private static void applyVengeanceDebuffs(EntityLivingBase ent) {
        if (ent == null || ent.isDead) {
            return;
        }
        int dur = 100;
        if (SRPPotions.DOD_SMOKE_TRAIL_E != null) {
            ent.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.DOD_SMOKE_TRAIL_E, 100, 0, false, true));
        }
        if (SRPPotions.BLEED_E != null) {
            ent.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.BLEED_E, 100, 0, false, true));
        }
        if (SRPPotions.DEBAR_E != null) {
            ent.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.DEBAR_E, 100, 0, false, true));
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        if (ACTIVE.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, GrappleData>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, GrappleData> en = it.next();
            GrappleData d = en.getValue();
            EntityPlayer player = d.getPlayer();
            EntityLivingBase target = d.getTarget();
            if (player == null || target == null) {
                it.remove();
                continue;
            }
            if (player.isDead || target.isDead) {
                it.remove();
                continue;
            }
            if (player.dimension != d.dimension) {
                it.remove();
                continue;
            }
            if (!(player.worldObj instanceof WorldServer)) {
                it.remove();
                continue;
            }
            WorldServer ws = (WorldServer)player.worldObj;
            if (!player.canEntityBeSeen((Entity)target)) {
                ws.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.6f, 1.4f);
                it.remove();
                continue;
            }
            switch (d.stage) {
                case WINDUP: {
                    VengeanceGrappleHandler.spawnChainParticles(ws, player, target, EnumParticleTypes.SPELL_WITCH);
                    if (d.timer % 4 == 0) {
                        Vec3d c = new Vec3d(player.posX, player.posY + 0.2, player.posZ);
                        VengeanceGrappleHandler.fxAroundTracking(ws, c, new PacketVengeanceFX(0, c, 1.2f, 16));
                    }
                    if (d.timer % 6 == 0) {
                        ws.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 0.25f, 1.8f);
                    }
                    --d.timer;
                    if (d.timer > 0) break;
                    ws.playSound(null, target.posX, target.posY + (double)target.height * 0.5, target.posZ, SRPSounds.VENGEANCE_CHAIN_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    d.stage = Stage.PULL;
                    d.ticks = 0;
                    break;
                }
                case PULL: {
                    ++d.ticks;
                    VengeanceGrappleHandler.spawnChainParticles(ws, player, target, EnumParticleTypes.CRIT_MAGIC);
                    VengeanceGrappleHandler.pullPlayerToward(player, target, 1.8);
                    double dist = player.getDistanceToEntity((Entity)target);
                    if (!(dist <= 2.2) && d.ticks <= 40) break;
                    VengeanceGrappleHandler.impact1(ws, player, target, d);
                    break;
                }
                case BOUNCE: {
                    int t;
                    if (d.timer % 4 == 0) {
                        for (UUID id : d.affected) {
                            Entity e2 = ws.getEntityFromUuid(id);
                            if (!(e2 instanceof EntityLivingBase)) continue;
                            EntityLivingBase ent2 = (EntityLivingBase)e2;
                            if (ent2.isDead) continue;
                            Vec3d c = new Vec3d(ent2.posX, ent2.posY + (double)ent2.height * 0.5, ent2.posZ);
                            VengeanceGrappleHandler.fxAroundTracking(ws, c, new PacketVengeanceFX(2, c, 0.0f, 14));
                        }
                    }
                    if ((t = d.timer) >= 12) {
                        player.motionY = Math.max(player.motionY, 0.55);
                        player.motionX *= 0.4;
                        player.motionZ *= 0.4;
                        player.velocityChanged = true;
                    } else if (t >= 6) {
                        player.motionY = Math.max(player.motionY, -0.02);
                        player.motionX *= 0.65;
                        player.motionZ *= 0.65;
                        player.velocityChanged = true;
                    } else {
                        VengeanceGrappleHandler.pullPlayerToward(player, target, 2.35);
                    }
                    player.fallDistance = 0.0f;
                    --d.timer;
                    if (d.timer > 0) break;
                    VengeanceGrappleHandler.impact2AndLightning(ws, player, target, d);
                    it.remove();
                    break;
                }
            }
        }
    }

    private static void pullPlayerToward(EntityPlayer player, EntityLivingBase target, double speed) {
        Vec3d to = new Vec3d(target.posX - player.posX, target.posY + (double)target.getEyeHeight() * 0.5 - (player.posY + (double)player.getEyeHeight()), target.posZ - player.posZ);
        double len = to.lengthVector();
        if (len < 1.0E-4) {
            return;
        }
        Vec3d dir = to.scale(1.0 / len);
        double vx = MathHelper.clamp_double((double)(dir.xCoord * speed), (double)-2.8, (double)2.8);
        double vy = MathHelper.clamp_double((double)(dir.yCoord * speed), (double)-2.2, (double)2.2);
        double vz = MathHelper.clamp_double((double)(dir.zCoord * speed), (double)-2.8, (double)2.8);
        player.motionX = vx;
        player.motionY = vy;
        player.motionZ = vz;
        player.velocityChanged = true;
        player.fallDistance = 0.0f;
    }

    private static void impact1(WorldServer ws, EntityPlayer player, EntityLivingBase target, GrappleData d) {
        ws.playSound(null, target.posX, target.posY, target.posZ, SRPSounds.VENGEANCE_IMPACT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        ws.playSound(null, target.posX, target.posY, target.posZ, SRPSounds.VENGEANCE_ROCK, SoundCategory.PLAYERS, 0.9f, 0.9f);
        Vec3d hitPos = new Vec3d(target.posX, target.posY, target.posZ);
        VengeanceGrappleHandler.fxAroundTracking(ws, hitPos, new PacketVengeanceFX(1, hitPos, 0.0f, 28));
        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(ws, EnumParticleTypes.EXPLOSION_HUGE, target.posX, target.posY + 0.2, target.posZ, 1, 0.0, 0.0, 0.0, 0.0, new int[0]);
        float blastDamage = 14.0f;
        float radius = 5.0f;
        AxisAlignedBB aabb = Boxes.create(target.posX - (double)radius, target.posY - 2.0, target.posZ - (double)radius, target.posX + (double)radius, target.posY + 4.0, target.posZ + (double)radius);
        List list = EntityQueries.entities(ws, EntityLivingBase.class, aabb);
        d.affected.clear();
        for (EntityLivingBase ent : list) {
            double dist;
            if (ent == null || ent.isDead || ent == player || (dist = (double)ent.getDistanceToEntity((Entity)target)) > (double)radius) continue;
            d.affected.add(ent.getUniqueID());
            VengeanceGrappleHandler.applyVengeanceDebuffs(ent);
            if (SRPSounds.VENGEANCE_WHOOSH != null) {
                ws.playSound(null, ent.posX, ent.posY, ent.posZ, SRPSounds.VENGEANCE_WHOOSH, SoundCategory.PLAYERS, 0.8f, 1.2f);
            }
            ent.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), blastDamage);
            Vec3d look = player.getLookVec();
            double l = look.lengthVector();
            if (l > 1.0E-4) {
                look = look.scale(1.0 / l);
            }
            double forward = 1.9;
            double upward = 0.65;
            ent.motionX = look.xCoord * forward;
            ent.motionZ = look.zCoord * forward;
            ent.motionY = Math.max(ent.motionY, look.yCoord * forward + upward);
            ent.velocityChanged = true;
        }
        player.motionX *= 0.15;
        player.motionZ *= 0.15;
        player.fallDistance = 0.0f;
        player.velocityChanged = true;
        d.stage = Stage.BOUNCE;
        d.timer = 14;
    }

    private static void impact2AndLightning(WorldServer ws, EntityPlayer player, EntityLivingBase target, GrappleData d) {
        ws.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.7f, 1.2f);
        Vec3d p = new Vec3d(target.posX, target.posY + 0.1, target.posZ);
        VengeanceGrappleHandler.fxAroundTracking(ws, p, new PacketVengeanceFX(3, p, 0.0f, 0));
        ws.playSound(null, target.posX, target.posY, target.posZ, SRPSounds.VENGEANCE_ROCK, SoundCategory.PLAYERS, 0.9f, 0.9f);
        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(ws, EnumParticleTypes.EXPLOSION_LARGE, target.posX, target.posY + 0.15, target.posZ, 3, 0.15, 0.1, 0.15, 0.0, new int[0]);
        if (!target.isDead) {
            target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)player), 6.0f);
            target.motionY = Math.max(target.motionY, 0.35);
            target.velocityChanged = true;
        }
        float radius = 3.5f;
        float maxDmg = 10.0f;
        AxisAlignedBB aabb = Boxes.create(target.posX - 3.5, target.posY - 1.5, target.posZ - 3.5, target.posX + 3.5, target.posY + 2.5, target.posZ + 3.5);
        List near = EntityQueries.entities(ws, EntityLivingBase.class, aabb);
        for (EntityLivingBase ent : near) {
            double dist;
            if (ent == null || ent.isDead || ent == player || (dist = (double)ent.getDistanceToEntity((Entity)target)) > 3.5) continue;
            float scale = 1.0f - (float)(dist / 3.5);
            float dmg = 10.0f * scale;
            ent.attackEntityFrom(DamageSource.causeExplosionDamage((EntityLivingBase)player), dmg);
            Vec3d push = new Vec3d(ent.posX - target.posX, 0.0, ent.posZ - target.posZ);
            double len = push.lengthVector();
            if (!(len > 1.0E-4)) continue;
            push = push.scale(1.0 / len);
            double kb = 0.85 * (double)scale;
            ent.motionX += push.xCoord * kb;
            ent.motionZ += push.zCoord * kb;
            ent.motionY = Math.max(ent.motionY, 0.25 + 0.2 * (double)scale);
            ent.velocityChanged = true;
        }
        player.fallDistance = 0.0f;
        VengeanceGrappleHandler.lightningStrike(ws, player, d);
    }

    private static void lightningStrike(WorldServer ws, EntityPlayer player, GrappleData d) {
        ws.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.9f, 1.0f);
        for (UUID id : d.affected) {
            Entity e = ws.getEntityFromUuid(id);
            if (!(e instanceof EntityLivingBase)) continue;
            EntityLivingBase ent = (EntityLivingBase)e;
            if (ent.isDead) continue;
            ws.addWeatherEffect((Entity)new EntityLightningBolt((World)ws, ent.posX, ent.posY, ent.posZ, true));
            Vec3d p = new Vec3d(ent.posX, ent.posY, ent.posZ);
            VengeanceGrappleHandler.fxAroundTracking(ws, p, new PacketVengeanceFX(3, p, 0.0f, 0));
            ent.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 16.0f);
            ws.playSound(null, ent.posX, ent.posY, ent.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.HOSTILE, 0.8f, 1.0f);
        }
    }

    private static void spawnChainParticles(WorldServer ws, EntityPlayer player, EntityLivingBase target, EnumParticleTypes type) {
        Vec3d start = new Vec3d(player.posX, player.posY + (double)player.getEyeHeight() - 0.15, player.posZ);
        Vec3d end = new Vec3d(target.posX, target.posY + (double)target.getEyeHeight() * 0.5, target.posZ);
        Vec3d delta = end.subtract(start);
        double len = delta.lengthVector();
        if (len < 1.0E-4) {
            return;
        }
        Vec3d step = delta.scale(1.0 / len);
        int count = MathHelper.clamp_int((int)((int)(len / 0.6)), (int)6, (int)60);
        for (int i = 0; i < count; ++i) {
            Vec3d p = start.add(step.scale((double)i * (len / (double)count)));
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(ws, type, p.xCoord, p.yCoord, p.zCoord, 1, 0.02, 0.02, 0.02, 0.0, new int[0]);
        }
    }

    private static final class GrappleData {
        final UUID playerId;
        final UUID targetId;
        final int dimension;
        Stage stage = Stage.WINDUP;
        int ticks = 0;
        int timer = 0;
        final List<UUID> affected = new ArrayList<UUID>();

        GrappleData(UUID playerId, UUID targetId, int dimension) {
            this.playerId = playerId;
            this.targetId = targetId;
            this.dimension = dimension;
        }

        EntityPlayer getPlayer() {
            if (FMLCommonHandler.instance().getMinecraftServerInstance() == null) {
                return null;
            }
            return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(this.playerId);
        }

        EntityLivingBase getTarget() {
            EntityPlayer p = this.getPlayer();
            if (p == null || !(p.worldObj instanceof WorldServer)) {
                return null;
            }
            Entity e = ((WorldServer)p.worldObj).getEntityFromUuid(this.targetId);
            return e instanceof EntityLivingBase ? (EntityLivingBase)e : null;
        }
    }

    private static enum Stage {
        WINDUP,
        PULL,
        BOUNCE;

    }
}

