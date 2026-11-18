/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.MusicTicker$MusicType
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$FogMode
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIAvoidEntity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAINearestAttackableTarget
 *  net.minecraft.entity.ai.attributes.IAttribute
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.Style
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.GameRules
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.WorldType
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogColors
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogDensity
 *  net.minecraftforge.client.event.GuiOpenEvent
 *  net.minecraftforge.client.event.MouseEvent
 *  net.minecraftforge.client.event.TextureStitchEvent$Pre
 *  net.minecraftforge.client.event.sound.PlayStreamingSourceEvent
 *  net.minecraftforge.event.entity.EntityJoinWorldEvent
 *  net.minecraftforge.event.entity.EntityMountEvent
 *  net.minecraftforge.event.entity.EntityStruckByLightningEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.entity.living.LivingEntityUseItemEvent$Start
 *  net.minecraftforge.event.entity.living.LivingHealEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.ItemFishedEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteractSpecific
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.entity.player.PlayerWakeUpEvent
 *  net.minecraftforge.event.world.BlockEvent$CropGrowEvent$Pre
 *  net.minecraftforge.event.world.BlockEvent$NeighborNotifyEvent
 *  net.minecraftforge.event.world.BlockEvent$PlaceEvent
 *  net.minecraftforge.event.world.WorldEvent$Unload
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fml.client.FMLClientHandler
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$PlayerTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ServerTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.registry.ForgeRegistries
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.logging.log4j.Level
 *  org.lwjgl.input.Keyboard
 */
package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockDiseasedSponge;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleBiomass;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleBlood;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleCoolerFog;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleDot;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleEen;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleFlash;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleFog;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleMultipleGore;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleRHappy;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleRage;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpore;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleWind;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPMusic;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.item.tool.IHaveReach;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolArmorBase;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolMeleeBase;
import com.dhanantry.scapeandrunparasites.network.SRPPacketFog;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMeleeRange;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMovingSound;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.network.SRPPacketRequestEvoPhaseClient;
import com.dhanantry.scapeandrunparasites.util.EIVUtil;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPReference;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.convert.BeckonBlockInfestation;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldParasiteSpawner;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.registry.ForgeRegistries;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

public class SRPEventHandlerBus {
    private static final Map<UUID, FogProperties> playerFogProperties = new HashMap<UUID, FogProperties>();
    private ArrayList<ISound> musicToRemove = new ArrayList();
    public static byte clientCurrentEvoPhase;
    public static int clientScent;
    public static int clientVector;
    public static int musicTimer;
    private byte lockedMu = 0;
    public static float fog;
    public static float fogRed;
    public static float fogGreen;
    public static float fogBlue;
    private static final Map<Integer, Long> RICARDO_DEATH_RULE_RESTORE;
    private static boolean srpSoakGuard;
    private boolean closeG = false;
    private int counerW = 0;
    private int blockInfestedCountCooldown;
    private int blockParasiteCountCooldown;
    private int moo = 0;
    private ArrayList<Integer> worldsChecked = new ArrayList();
    private int meteor;
    private int heart;

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return;
        }
        if (mc.thePlayer.isPotionActive(SRPPotions.INDEAF_E)) {
            int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
            KeyBinding cfr_ignored_0 = mc.gameSettings.keyBindForward;
            KeyBinding.setKeyBindState((int)i, (boolean)false);
            KeyBinding cfr_ignored_1 = mc.gameSettings.keyBindBack;
            KeyBinding.setKeyBindState((int)i, (boolean)false);
            KeyBinding cfr_ignored_2 = mc.gameSettings.keyBindLeft;
            KeyBinding.setKeyBindState((int)i, (boolean)false);
            KeyBinding cfr_ignored_3 = mc.gameSettings.keyBindRight;
            KeyBinding.setKeyBindState((int)i, (boolean)false);
            KeyBinding cfr_ignored_4 = mc.gameSettings.keyBindJump;
            KeyBinding.setKeyBindState((int)i, (boolean)false);
        }
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void soundTwo(PlayStreamingSourceEvent event) {
        SRPMain.logger.debug("phase: {} s: {} ticks: {}", (Object)clientCurrentEvoPhase, (Object)event.getName(), (Object)musicTimer);
        if (!SRPConfig.musicTrue) {
            return;
        }
        if (!(event.getName().contains("srparasites") || clientCurrentEvoPhase <= 0 && clientScent <= 0)) {
            if (SRPConfigWorld.originActivated && clientScent <= 0 && clientVector <= 0) {
                return;
            }
            this.musicToRemove.add(event.getSound());
            SRPMain.logger.debug("added {}", (Object)event.getName());
        }
    }

    public static void resetSouncTicker(int in) {
        int goo;
        if (in > 0) {
            if (musicTimer < in) {
                return;
            }
            musicTimer = in;
            return;
        }
        Random rand = new Random();
        int atm2 = SRPConfig.musicMax - SRPConfig.musicMin + 1;
        if (atm2 <= 0 || SRPConfig.musicMax <= SRPConfig.musicMin) {
            int n = SRPConfig.musicMax + 1000;
        }
        if (musicTimer < (goo = rand.nextInt(atm2) + SRPConfig.musicMin) && musicTimer > 0) {
            return;
        }
        musicTimer = goo;
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void soundThree(TickEvent.PlayerTickEvent event) {
        if (!SRPConfig.musicTrue) {
            return;
        }
        if (event.phase == TickEvent.Phase.END) {
            return;
        }
        if (event.side != Side.CLIENT) {
            return;
        }
        if (Minecraft.getMinecraft() == null) {
            return;
        }
        if (event.player.ticksExisted % 20 == 0) {
            SRPMain.network.sendToServer((IMessage)new SRPPacketRequestEvoPhaseClient());
        }
        int prev = clientVector;
        if (clientVector > -10) {
            --clientVector;
        }
        if (SRPConfigWorld.originActivated && clientVector <= 0) {
            if (prev > 0) {
                Minecraft.getMinecraft().getSoundHandler().stop("", SoundCategory.MUSIC);
            }
            return;
        }
        if (!this.musicToRemove.isEmpty()) {
            for (int i = 0; i < this.musicToRemove.size(); ++i) {
                if (Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(this.musicToRemove.get(i))) {
                    Minecraft.getMinecraft().getSoundHandler().stopSound(this.musicToRemove.get(i));
                    this.musicToRemove.remove(i);
                    --i;
                    continue;
                }
                this.lockedMu = (byte)(this.lockedMu + 1);
            }
            if (this.lockedMu >= 100) {
                this.musicToRemove = new ArrayList();
            }
        }
        if (musicTimer >= 0) {
            --musicTimer;
        }
        if (clientScent >= 0) {
            --clientScent;
        }
        if (clientScent == -1) {
            Minecraft.getMinecraft().getSoundHandler().stop("", SoundCategory.MUSIC);
            clientScent = -10;
        }
        if (musicTimer <= 0 && this.musicToRemove.isEmpty() && clientCurrentEvoPhase > 0) {
            SRPEventHandlerBus.resetSouncTicker(0);
            MusicTicker.MusicType evoPhaseMusic = this.getMusicPhase();
            if (evoPhaseMusic == null) {
                return;
            }
            if (clientScent <= 0) {
                Minecraft.getMinecraft().getSoundHandler().stop("", SoundCategory.MUSIC);
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getMusicRecord((String)evoPhaseMusic.getMusicLocation()));
            }
        } else {
            SRPEventHandlerBus.resetSouncTicker(0);
        }
    }

    @SideOnly(value=Side.CLIENT)
    private MusicTicker.MusicType getMusicPhase() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player.worldObj.getBiome(EntityQueries.position(player)) instanceof BiomeParasiteBase) {
            return SRPMusic.BIOME_MUSIC;
        }
        switch (clientCurrentEvoPhase) {
            case 1: {
                return SRPMusic.EVPHASE_1_MUSIC;
            }
            case 2: {
                return SRPMusic.EVPHASE_2_MUSIC;
            }
            case 3: {
                return SRPMusic.EVPHASE_3_MUSIC;
            }
            case 4: {
                return SRPMusic.EVPHASE_4_MUSIC;
            }
            case 5: {
                return SRPMusic.EVPHASE_5_MUSIC;
            }
            case 6: {
                return SRPMusic.EVPHASE_6_MUSIC;
            }
            case 7: {
                return SRPMusic.EVPHASE_7_MUSIC;
            }
            case 8: {
                return SRPMusic.EVPHASE_8_MUSIC;
            }
            case 9: {
                return SRPMusic.EVPHASE_9_MUSIC;
            }
            case 10: {
                return SRPMusic.EVPHASE_10_MUSIC;
            }
        }
        return null;
    }

    @SideOnly(value=Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onWorldLoad(WorldEvent.Unload event) {
        clientCurrentEvoPhase = 0;
        clientScent = 0;
        fog = 0.0f;
        musicTimer = 1000;
        SRPSaveData.falseLevel = 0;
    }

    @SideOnly(value=Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EntityViewRenderEvent.FogDensity event) {
        Entity entity = event.getEntity();
        if (entity.isInsideOfMaterial(SRPBlocks.PARASITEBLOOD)) {
            GlStateManager.setFog((GlStateManager.FogMode)GlStateManager.FogMode.EXP);
            event.setDensity(1.0f);
            event.setCanceled(true);
            return;
        }
        if (fog > 0.0f && !Minecraft.getMinecraft().thePlayer.isPotionActive(MobEffects.blindness)) {
            GlStateManager.setFog((GlStateManager.FogMode)GlStateManager.FogMode.EXP);
            event.setDensity(fog);
            event.setCanceled(true);
        }
    }

    @SideOnly(value=Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(EntityViewRenderEvent.FogColors event) {
        Entity entity = event.getEntity();
        if (entity.isInsideOfMaterial(SRPBlocks.PARASITEBLOOD)) {
            event.setRed(0.1f);
            event.setGreen(0.6f);
            event.setBlue(0.2f);
            return;
        }
        if ((double)fog > (double)SRPConfigWorld.biomeFogDensity * 0.2 && !Minecraft.getMinecraft().thePlayer.isPotionActive(MobEffects.blindness)) {
            event.setRed(fogRed);
            event.setGreen(fogGreen);
            event.setBlue(fogBlue);
        }
    }

    @SubscribeEvent
    public void cropGrow(BlockEvent.CropGrowEvent.Pre event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }
        if (!SRPConfigWorld.nodesActivated && !SRPConfigSystems.useEvolution) {
            return;
        }
        SRPWorldData data = SRPWorldData.get(event.getWorld());
        int nodeAge = data.nearestHeartAge(event.getPos(), false, 0);
        double[] nodeChance = new double[]{0.0, SRPConfigWorld.nodeCropStopNodeOne, SRPConfigWorld.nodeCropStopNodeTwo, SRPConfigWorld.nodeCropStopNodeThree};
        if (nodeAge >= 1 && nodeAge <= 3 && event.getWorld().rand.nextDouble() < nodeChance[nodeAge]) {
            event.setResult(Event.Result.DENY);
            return;
        }
        byte phase = SRPSaveData.get(event.getWorld(), 79).getEvolutionPhase(event.getWorld().provider.dimensionId);
        double[] phaseChance = new double[]{0.0, SRPConfigSystems.cropGrowStunnedOne, SRPConfigSystems.cropGrowStunnedTwo, SRPConfigSystems.cropGrowStunnedThree, SRPConfigSystems.cropGrowStunnedFour, SRPConfigSystems.cropGrowStunnedFive, SRPConfigSystems.cropGrowStunnedSix, SRPConfigSystems.cropGrowStunnedSeven, SRPConfigSystems.cropGrowStunnedEight};
        if (phase >= 1 && phase < phaseChance.length && event.getWorld().rand.nextDouble() < phaseChance[phase]) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        Entity src = event.getSource().getEntity();
        if (!(src instanceof EntityNogla)) {
            return;
        }
        EntityNogla nogla = (EntityNogla)src;
        if (!nogla.isRicardoVariant()) {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.getEntity();
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        WorldServer ws = (WorldServer)player.worldObj;
        GameRules rules = ws.getGameRules();
        boolean wasShowing = rules.getGameRuleBooleanValue("showDeathMessages");
        if (wasShowing) {
            rules.setOrCreateGameRule("showDeathMessages", "false");
            RICARDO_DEATH_RULE_RESTORE.put(ws.provider.dimensionId, ws.getTotalWorldTime() + 1L);
        }
        server.getPlayerList().sendChatMsg((IChatComponent)new ChatComponentTranslation("death.attack.srparasites.ricardo", new Object[]{player.getDisplayName()}));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.world.isRemote) {
            return;
        }
        Integer dim = e.world.provider.dimensionId;
        Long when = RICARDO_DEATH_RULE_RESTORE.get(dim);
        if (when != null && e.world.getTotalWorldTime() >= when) {
            ((WorldServer)e.world).getGameRules().setOrCreateGameRule("showDeathMessages", "true");
            RICARDO_DEATH_RULE_RESTORE.remove(dim);
        }
    }

    private static Block getDeadBloodBlock() {
        Fluid f = FluidRegistry.getFluid((String)"deadblood");
        if (f == null) {
            return Blocks.air;
        }
        Block b = f.getBlock();
        return b == null ? Blocks.air : b;
    }

    private static Block getDiseasedSpongeBlock() {
        return SRPBlocks.diseasedSponge;
    }

    private static boolean isSponge(Block b) {
        return b == Blocks.sponge;
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent e) {
        if (e.getWorld().isRemote) {
            return;
        }
        SRPEventHandlerBus.tryConvertSponge(e.getWorld(), e.getPos(), e.getPlacedBlock());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent
    public void onNeighborNotify(BlockEvent.NeighborNotifyEvent e) {
        block20: {
            if (e.getWorld().isRemote) {
                return;
            }
            World world = e.getWorld();
            BlockPos pos = e.getPos();
            Block deadBlood = SRPEventHandlerBus.getDeadBloodBlock();
            Block changed = e.getState().getBlock();
            IBlockState changedState = e.getState();
            if (!(SRPEventHandlerBus.isSponge(changed) || changed == deadBlood || SRPEventHandlerBus.isWater(changedState) || SRPEventHandlerBus.isLava(changedState))) {
                return;
            }
            if (srpSoakGuard) {
                return;
            }
            srpSoakGuard = true;
            try {
                if (changed == deadBlood) {
                    for (EnumFacing f : EnumFacing.values()) {
                        IBlockState rubble;
                        BlockPos n = pos.offset(f);
                        IBlockState st = BlockStates.get(world, n);
                        if (SRPEventHandlerBus.isSponge(st.getBlock())) {
                            SRPEventHandlerBus.tryConvertSponge(world, n, st);
                            continue;
                        }
                        if (SRPEventHandlerBus.isWater(st)) {
                            IBlockState stain = SRPEventHandlerBus.getParasiteStainState();
                            if (stain.getBlock() == Blocks.air) continue;
                            BlockStates.set(world, n, stain, 2);
                            continue;
                        }
                        if (!SRPEventHandlerBus.isLava(st) || (rubble = SRPEventHandlerBus.getParasiteRubbleState()).getBlock() == Blocks.air) continue;
                        BlockStates.set(world, n, rubble, 2);
                    }
                    return;
                }
                if (SRPEventHandlerBus.isWater(changedState) || SRPEventHandlerBus.isLava(changedState)) {
                    boolean touchesDeadBlood = false;
                    for (EnumFacing f : EnumFacing.values()) {
                        if (BlockStates.get(world, pos.offset(f)).getBlock() != deadBlood) continue;
                        touchesDeadBlood = true;
                        break;
                    }
                    if (touchesDeadBlood) {
                        if (SRPEventHandlerBus.isWater(changedState)) {
                            IBlockState stain = SRPEventHandlerBus.getParasiteStainState();
                            if (stain.getBlock() != Blocks.air) {
                                BlockStates.set(world, pos, stain, 2);
                            }
                        } else {
                            IBlockState rubble = SRPEventHandlerBus.getParasiteRubbleState();
                            if (rubble.getBlock() != Blocks.air) {
                                BlockStates.set(world, pos, rubble, 2);
                            }
                        }
                    }
                    return;
                }
                if (!SRPEventHandlerBus.isSponge(changed)) break block20;
                for (EnumFacing f : EnumFacing.values()) {
                    if (BlockStates.get(world, pos.offset(f)).getBlock() != deadBlood) continue;
                    SRPEventHandlerBus.tryConvertSponge(world, pos, changedState);
                    break;
                }
            }
            finally {
                srpSoakGuard = false;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void tryConvertSponge(World world, BlockPos spongePos, IBlockState spongeState) {
        if (srpSoakGuard) {
            return;
        }
        if (!SRPEventHandlerBus.isSponge(spongeState.getBlock())) {
            return;
        }
        Block deadBlood = SRPEventHandlerBus.getDeadBloodBlock();
        Block diseased = SRPEventHandlerBus.getDiseasedSpongeBlock();
        if (deadBlood == Blocks.air || diseased == Blocks.air) {
            return;
        }
        boolean touching = false;
        for (EnumFacing f : EnumFacing.values()) {
            if (BlockStates.get(world, spongePos.offset(f)).getBlock() != deadBlood) continue;
            touching = true;
            break;
        }
        if (!touching) {
            return;
        }
        srpSoakGuard = true;
        try {
            boolean absorbed = BlockDiseasedSponge.absorbDeadBlood(world, spongePos, deadBlood);
            if (absorbed) {
                BlockStates.set(world, spongePos, BlockStates.defaultState(diseased), 2);
            }
        }
        finally {
            srpSoakGuard = false;
        }
    }

    private static IBlockState getParasiteStainState() {
        Block b = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation("srparasites", "parasitestain"));
        if (b == null || b == Blocks.air) {
            return BlockStates.defaultState(Blocks.air);
        }
        return BlockStates.fromMeta(b, 1);
    }

    private static IBlockState getParasiteRubbleState() {
        Block b = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation("srparasites", "parasiterubble"));
        if (b == null || b == Blocks.air) {
            return BlockStates.defaultState(Blocks.air);
        }
        return BlockStates.fromMeta(b, 7);
    }

    private static boolean isWater(IBlockState st) {
        Material m = st.getMaterial();
        return m == Material.water;
    }

    private static boolean isLava(IBlockState st) {
        Material m = st.getMaterial();
        return m == Material.lava;
    }

    @SubscribeEvent
    public void entityHeal(LivingHealEvent event) {
        if (event.isCanceled()) {
            return;
        }
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || entity.worldObj.isRemote) {
            return;
        }
        if (entity.worldObj.getBiome(EntityQueries.position(entity)) instanceof BiomeParasiteBase) {
            float penaltyH;
            switch (ParasiteEventWorld.canBiomeStillExistType(entity.worldObj, EntityQueries.position(entity), true)) {
                case 2: {
                    penaltyH = SRPConfigWorld.biomeTwoHealPenalty;
                    break;
                }
                case 3: {
                    penaltyH = SRPConfigWorld.biomeThreeHealPenalty;
                    break;
                }
                case 4: {
                    penaltyH = SRPConfigWorld.biomeFourHealPenalty;
                    break;
                }
                default: {
                    penaltyH = SRPConfigWorld.biomeOneHealPenalty;
                }
            }
            if (entity instanceof EntityParasiteBase) {
                return;
            }
            if (entity instanceof EntityPlayer) {
                event.setAmount(event.getAmount() * penaltyH);
            } else {
                boolean flag = ParasiteEventEntity.checkName(EntityList.getKey((Entity)entity).toString(), SRPConfigWorld.biomeHealPenaltyBlackList, SRPConfigWorld.biomeHealPenaltyBlackListWhite);
                if (flag) {
                    return;
                }
                event.setAmount(event.getAmount() * penaltyH);
            }
        }
        if (SRPConfigSystems.useEvolution && SRPSaveData.get(entity.worldObj, 80).getEvolutionPhase(entity.worldObj.provider.dimensionId) >= SRPConfigSystems.evolutionNoParasiteHealing) {
            event.setAmount(event.getAmount() * SRPConfigSystems.evolutionNoParasiteHealingValue);
        }
    }

    @SubscribeEvent
    public void entityHurt(LivingHurtEvent event) {
        EntityLivingBase mob;
        float damage;
        float amp;
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || entity.worldObj.isRemote) {
            return;
        }
        if (entity.isPotionActive(SRPPotions.VIRA_E) && SRPConfigSystems.viralEnable) {
            amp = entity.getActivePotionEffect(SRPPotions.VIRA_E).getAmplifier() + 1;
            damage = event.getAmount();
            event.setAmount(damage + damage * (amp * SRPConfigSystems.viralAmount));
        }
        if (entity.isPotionActive(SRPPotions.OVERHEATING_E) && (event.getSource() == DamageSource.onFire || event.getSource() == DamageSource.inFire)) {
            amp = entity.getActivePotionEffect(SRPPotions.OVERHEATING_E).getAmplifier() + 1;
            damage = event.getAmount();
            event.getSource().setDamageIsAbsolute();
            event.setAmount(damage + damage * (amp * 1.0f));
        }
        if (event.getSource().getEntity() instanceof EntityLivingBase && (mob = (EntityLivingBase)event.getSource().getEntity()).isPotionActive(SRPPotions.MUSCLEOUT_E)) {
            float amp2 = (float)mob.getActivePotionEffect(SRPPotions.MUSCLEOUT_E).getAmplifier() + 1.0f;
            event.setAmount(event.getAmount() * (SRPConfigSystems.muscleoutDamageOut * amp2));
        }
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            event.setAmount(this.playerArmor(player, event.getAmount(), event.getSource()));
        }
    }

    private float playerArmor(EntityPlayer player, float damageV, DamageSource source) {
        float red = 0.0f;
        String damage = "";
        byte type = 0;
        boolean needCheckP = true;
        boolean paraA = false;
        for (ItemStack itemstack : player.inventory.armorInventory) {
            int i;
            NBTTagList allResI;
            NBTTagList allResS;
            if (!(itemstack.getItem() instanceof WeaponToolArmorBase)) continue;
            paraA = true;
            if (needCheckP && source.getSourceOfDamage() != null) {
                if (source == DamageSource.inFire || source == DamageSource.onFire || player.isBurning()) {
                    return damageV * SRPConfig.firemultyplier;
                }
                if (source.getSourceOfDamage() instanceof EntityPlayer) {
                    damage = source.getSourceOfDamage().getCommandSenderName();
                } else if (source.getSourceOfDamage() instanceof EntityLivingBase) {
                    damage = EntityList.getKey((Entity)source.getSourceOfDamage()).toString();
                } else {
                    damage = source.damageType;
                    type = 2;
                }
                needCheckP = false;
            }
            ArrayList<String> resistanceS = new ArrayList<String>();
            ArrayList<Integer> resistanceI = new ArrayList<Integer>();
            NBTTagCompound compound = itemstack.getTagCompound();
            boolean lag = ((WeaponToolArmorBase)itemstack.getItem()).canCall();
            if (compound == null) {
                compound = new NBTTagCompound();
            }
            if (compound.hasKey("sprresistanceb", 99)) {
                lag = compound.getBoolean("sprresistanceb");
            }
            if (compound.hasKey("sprresistances")) {
                allResS = compound.getTagList("sprresistances", 10);
                allResI = compound.getTagList("sprresistancei", 10);
                if (allResS.tagCount() != allResI.tagCount()) {
                    return damageV;
                }
                for (i = 0; i < allResS.tagCount(); ++i) {
                    NBTTagCompound resT = allResS.getCompoundTagAt(i);
                    String res = resT.getString("resistance" + i);
                    resistanceS.add(i, res);
                    NBTTagCompound resU = allResI.getCompoundTagAt(i);
                    int resi = resU.getInteger("resistance" + i);
                    resistanceI.add(i, resi);
                }
            }
            red += (float)this.hasResistance(damage, resistanceS, resistanceI, lag, player.worldObj.rand, type) * (lag ? SRPConfig.sentientPointReduction : SRPConfig.livingPointReduction);
            if (resistanceS.size() != resistanceI.size()) {
                return damageV;
            }
            allResS = new NBTTagList();
            allResI = new NBTTagList();
            for (i = 0; i < resistanceS.size(); ++i) {
                String res = resistanceS.get(i);
                NBTTagCompound resT = new NBTTagCompound();
                resT.setString("resistance" + i, res);
                allResS.appendTag((NBTBase)resT);
                int resi = resistanceI.get(i);
                NBTTagCompound resU = new NBTTagCompound();
                resU.setInteger("resistance" + i, resi);
                allResI.appendTag((NBTBase)resU);
            }
            compound.setTag("sprresistances", (NBTBase)allResS);
            compound.setTag("sprresistancei", (NBTBase)allResI);
            compound.setBoolean("sprresistanceb", lag);
            if (compound.hasKey("srphits")) {
                int key = (int)((float)compound.getInteger("srphits") + damageV);
                compound.setInteger("srphits", key);
            } else {
                compound.setInteger("srphits", (int)damageV);
            }
            itemstack.setTagCompound(compound);
        }
        red *= damageV;
        damageV = Math.max(damageV - red, 0.0f);
        if (source.getSourceOfDamage() instanceof EntityLivingBase && paraA && SRPConfig.armorCoth) {
            SRPPotions.applyStackPotion(SRPPotions.COTH_E, (EntityLivingBase)source.getSourceOfDamage(), 400, 2);
        }
        return damageV;
    }

    private int hasResistance(String damage, ArrayList<String> resistanceS, ArrayList<Integer> resistanceI, boolean stage, Random rand, byte type) {
        double getChanceLearn;
        if (this.checkList(damage, type)) {
            return 0;
        }
        double d = getChanceLearn = stage ? SRPConfig.sentientChanceLe : SRPConfig.livingChanceLe;
        if (rand.nextDouble() < getChanceLearn) {
            this.addResistance(damage, resistanceS, resistanceI, stage);
        }
        for (int i = 0; i < resistanceS.size(); ++i) {
            if (!resistanceS.get(i).equals(damage)) continue;
            int tage = SRPConfig.livingPointCap;
            if (stage) {
                tage = SRPConfig.sentientPointCap;
            }
            return Math.min(resistanceI.get(i), tage);
        }
        return 0;
    }

    private void addResistance(String damage, ArrayList<String> resistanceS, ArrayList<Integer> resistanceI, boolean stage) {
        boolean flag = true;
        for (int i = 0; i < resistanceS.size(); ++i) {
            if (!resistanceS.get(i).equals(damage)) continue;
            int iiii = resistanceI.get(i) + 1;
            resistanceI.set(i, iiii);
            flag = false;
            break;
        }
        if (flag) {
            int lim = SRPConfig.livingDamageCap;
            if (stage) {
                lim = SRPConfig.sentientDamageCap;
            }
            if (resistanceS.size() >= lim) {
                return;
            }
            resistanceS.add(damage);
            resistanceI.add(1);
        }
    }

    private boolean checkList(String damage, byte type) {
        switch (type) {
            case 0: {
                if (ParasiteEventEntity.checkName(damage, SRPConfig.armorDamageTypeBlackListMob, SRPConfig.armorDamageTypeBlackListWhite)) {
                    return true;
                }
            }
            case 2: {
                if (!ParasiteEventEntity.checkName(damage, SRPConfig.armorDamageTypeBlackListElse, SRPConfig.armorDamageTypeBlackListWhite)) break;
                return true;
            }
        }
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    @SubscribeEvent
    public void stitchEventPre(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(ParticleFog.PARTICLES_TEXTURE);
        event.getMap().registerSprite(ParticleEen.RED);
        event.getMap().registerSprite(ParticleDot.DOT);
        event.getMap().registerSprite(ParticleRHappy.REDHAPPY);
        event.getMap().registerSprite(ParticleFlash.FLASH);
        event.getMap().registerSprite(ParticleBlood.BLOOD1);
        event.getMap().registerSprite(ParticleBlood.BLOOD2);
        event.getMap().registerSprite(ParticleBlood.BLOOD_LAND);
        event.getMap().registerSprite(ParticleSpore.WHITE1);
        event.getMap().registerSprite(ParticleSpore.WHITE2);
        event.getMap().registerSprite(ParticleSpore.WHITE3);
        event.getMap().registerSprite(ParticleSpore.WHITE4);
        event.getMap().registerSprite(ParticleMultipleGore.ASSIMILATED1);
        event.getMap().registerSprite(ParticleMultipleGore.ASSIMILATED2);
        event.getMap().registerSprite(ParticleMultipleGore.ASSIMILATED3);
        event.getMap().registerSprite(ParticleMultipleGore.PURE1);
        event.getMap().registerSprite(ParticleMultipleGore.PURE2);
        event.getMap().registerSprite(ParticleMultipleGore.PURE3);
        event.getMap().registerSprite(ParticleMultipleGore.PRIMITIVE1);
        event.getMap().registerSprite(ParticleMultipleGore.PRIMITIVE2);
        event.getMap().registerSprite(ParticleMultipleGore.PRIMITIVE3);
        event.getMap().registerSprite(ParticleMultipleGore.ADAPTED1);
        event.getMap().registerSprite(ParticleMultipleGore.ADAPTED2);
        event.getMap().registerSprite(ParticleMultipleGore.ADAPTED3);
        event.getMap().registerSprite(ParticleMultipleGore.VOMIT1);
        event.getMap().registerSprite(ParticleMultipleGore.VOMIT2);
        event.getMap().registerSprite(ParticleMultipleGore.VOMIT3);
        event.getMap().registerSprite(ParticleMultipleGore.VOMIT4);
        event.getMap().registerSprite(ParticleMultipleGore.VOMIT5);
        event.getMap().registerSprite(ParticleMultipleGore.VOMIT6);
        event.getMap().registerSprite(ParticleBiomass.BIOMASS1);
        event.getMap().registerSprite(ParticleBiomass.BIOMASS2);
        event.getMap().registerSprite(ParticleBiomass.BIOMASS3);
        event.getMap().registerSprite(ParticleBiomass.BIOMASS4);
        event.getMap().registerSprite(ParticleWind.WIND1);
        event.getMap().registerSprite(ParticleWind.WIND2);
        event.getMap().registerSprite(ParticleWind.WIND3);
        event.getMap().registerSprite(ParticleWind.WIND4);
        event.getMap().registerSprite(ParticleWind.WIND5);
        event.getMap().registerSprite(ParticleWind.WIND6);
        event.getMap().registerSprite(ParticleWind.WIND7);
        event.getMap().registerSprite(ParticleWind.WIND8);
        event.getMap().registerSprite(ParticleWind.WIND9);
        event.getMap().registerSprite(ParticleWind.WIND10);
        event.getMap().registerSprite(ParticleWind.WIND11);
        event.getMap().registerSprite(ParticleWind.WIND12);
        event.getMap().registerSprite(ParticleWind.WIND13);
        event.getMap().registerSprite(ParticleCoolerFog.FOG_INTRO1);
        event.getMap().registerSprite(ParticleCoolerFog.FOG_INTRO2);
        event.getMap().registerSprite(ParticleCoolerFog.FOG_INTRO3);
        event.getMap().registerSprite(ParticleCoolerFog.FOG_INTRO4);
        event.getMap().registerSprite(ParticleCoolerFog.FOG_INTRO5);
        event.getMap().registerSprite(ParticleCoolerFog.FOG1);
        event.getMap().registerSprite(ParticleCoolerFog.FOG2);
        event.getMap().registerSprite(ParticleCoolerFog.FOG3);
        event.getMap().registerSprite(ParticleCoolerFog.FOG4);
        event.getMap().registerSprite(ParticleRage.RAGE1);
        event.getMap().registerSprite(ParticleRage.RAGE2);
        event.getMap().registerSprite(ParticleRage.RAGE3);
    }

    @SubscribeEvent
    public void playerFishing(ItemFishedEvent event) {
        if (!event.getEntity().worldObj.isRemote && SRPConfigSystems.useEvolution) {
            SRPWorldData data = SRPWorldData.get(event.getEntity().worldObj);
            if (SRPSaveData.get(event.getEntity().worldObj, 81).getEvolutionPhase(event.getEntity().worldObj.provider.dimensionId) >= SRPConfigSystems.evolutionStopFishing) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void itemEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof EntityLivingBase && !event.getWorld().isRemote) {
            ItemStack stack = event.getItemStack();
            String item = RegistryNames.get(stack.getItem()).toString();
            String[] atm = new String[3];
            for (int i = 0; i < SRPConfigSystems.COTHItemPrevent.length; ++i) {
                atm = SRPConfigSystems.COTHItemPrevent[i].split(";");
                if (!atm[0].equals(item)) continue;
                int dur = Integer.parseInt(atm[2]);
                if (!event.getEntityPlayer().capabilities.isCreativeMode) {
                    stack.shrink(1);
                    double chance = Double.parseDouble(atm[1]);
                    if (!(event.getWorld().rand.nextDouble() < chance)) continue;
                    ((EntityLivingBase)event.getTarget()).addPotionEffect(new PotionEffect(SRPPotions.EPEL_E, dur * 20, 0));
                    SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(event.getTarget().posX, event.getTarget().posY, event.getTarget().posZ, event.getTarget().width, event.getTarget().height, 3));
                    continue;
                }
                ((EntityLivingBase)event.getTarget()).addPotionEffect(new PotionEffect(SRPPotions.EPEL_E, dur * 20, 0));
                SRPMain.network.sendToAll((IMessage)new SRPPacketParticle(event.getTarget().posX, event.getTarget().posY, event.getTarget().posZ, event.getTarget().width, event.getTarget().height, 3));
            }
        }
    }

    @SubscribeEvent
    public void itemPlayer(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.getEntityLiving();
        if (player == null) {
            return;
        }
        if (player.isPotionActive(SRPPotions.FEAR_E) && SRPConfigSystems.fearActive) {
            if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() >= 1 && player.worldObj.rand.nextDouble() < (double)SRPConfigSystems.fearItemChance && !ParasiteEventEntity.checkName(RegistryNames.get(player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem()).toString(), SRPConfigSystems.fearItemBlackList, SRPConfigSystems.fearItemBlackListWhite)) {
                player.addChatComponentMessage(new ChatComponentTranslation("message.srparasites.fearitem", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)), true);
                event.setDuration(-1);
                event.setCanceled(true);
                return;
            }
            if (event.getItem().getItem() instanceof ItemBlock) {
                // empty if block
            }
        }
    }

    @SubscribeEvent
    public void blockPlayer(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null) {
            return;
        }
        if (player.isPotionActive(SRPPotions.FEAR_E) && SRPConfigSystems.fearActive) {
            if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() >= 2 && player.worldObj.rand.nextDouble() < (double)SRPConfigSystems.fearBlockChance) {
                player.addChatComponentMessage(new ChatComponentTranslation("message.srparasites.fearblock", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)), true);
                event.setUseBlock(Event.Result.DENY);
            } else if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() >= 1 && player.worldObj.rand.nextDouble() < (double)SRPConfigSystems.fearItemChance) {
                player.addChatComponentMessage(new ChatComponentTranslation("message.srparasites.fearitem", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)), true);
                event.setUseItem(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void entityPlayer(PlayerInteractEvent.EntityInteractSpecific event) {
    }

    @SideOnly(value=Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void guiPlayer(GuiOpenEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player == null || event.getGui() == null) {
            return;
        }
        if (player.isCreative() || event.getGui().doesGuiPauseGame()) {
            return;
        }
        if (player.isPotionActive(SRPPotions.FEAR_E) && SRPConfigSystems.fearActive && !player.capabilities.disableDamage && player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() >= 3 && player.worldObj.rand.nextDouble() < (double)SRPConfigSystems.fearInvChance) {
            player.addChatComponentMessage((IChatComponent)new ChatComponentTranslation("message.srparasites.feargui", new Object[0]), true);
            this.closeG = true;
        }
    }

    @SubscribeEvent
    public void mobFear(LivingDamageEvent event) {
        boolean trueAirborne;
        boolean inFluidOrSupport;
        EntityLivingBase in = event.getEntityLiving();
        if (in == null || !SRPConfigSystems.fearActive) {
            return;
        }
        if (!in.isPotionActive(SRPPotions.FEAR_E)) {
            return;
        }
        int amp = in.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() + 1;
        if (event.getSource() == DamageSource.fall && SRPConfigSystems.fearFallDamage != 0.0f) {
            event.setAmount(event.getAmount() * (SRPConfigSystems.fearFallDamage * (float)amp));
        }
        if (SRPConfigSystems.fearAirDamage == 0.0f) {
            return;
        }
        if (SRPConfigSystems.fearUnfair) {
            if (!in.onGround) {
                event.setAmount(event.getAmount() * (SRPConfigSystems.fearAirDamage * (float)amp));
            }
            return;
        }
        boolean bl = inFluidOrSupport = in.isInWater() || in.isInLava() || in.isOnLadder() || in.isRiding();
        if (inFluidOrSupport) {
            return;
        }
        boolean creativeFlying = false;
        boolean elytraFlying = false;
        if (in instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer)in;
            creativeFlying = p.capabilities.isFlying;
            elytraFlying = p.isElytraFlying();
        }
        float MIN_FALL_DISTANCE = 2.0f;
        boolean descending = in.motionY < 0.0;
        boolean bl2 = trueAirborne = creativeFlying || elytraFlying || !in.onGround && descending && in.fallDistance >= 2.0f;
        if (trueAirborne) {
            event.setAmount(event.getAmount() * (SRPConfigSystems.fearAirDamage * (float)amp));
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        if (event.getEntity() instanceof EntityLivingBase && !event.getEntity().worldObj.isRemote) {
            String mobname;
            if (event.getEntity() instanceof EntityPlayer) {
                return;
            }
            try {
                mobname = EntityList.getKey((Entity)event.getEntity()).toString();
            }
            catch (Exception e) {
                SRPMain.logger.log(Level.ERROR, "Problem with spawning entity");
                return;
            }
            NBTTagCompound tags = event.getEntity().getEntityData();
            boolean parasite = event.getEntity() instanceof EntityParasiteBase;
            boolean flagNC = SRPConfigWorld.nodesActivated || SRPConfigWorld.coloniesActivated || SRPConfigSystems.useEvolution;
            SRPWorldData data = null;
            if (flagNC) {
                data = SRPWorldData.get(event.getWorld());
            }
            if (SRPConfigWorld.nodesActivated && SRPConfigSystems.cothActive && data.nearestHeartAge(event.getEntity().getPosition(), false, 0) != -1) {
                ((EntityLivingBase)event.getEntity()).addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            if (!tags.hasKey("srpcothimmunity") && SRPConfigSystems.cothActive && !parasite) {
                this.writeCOTHTag((EntityLivingBase)event.getEntity(), mobname, tags);
            }
            if (parasite) {
                this.setNewParasiteTask((EntityParasiteBase)event.getEntity(), mobname, flagNC, data);
            } else if (event.getEntity() instanceof EntityCreature) {
                this.setNewCreatureTask((EntityCreature)event.getEntity(), mobname);
            }
        }
    }

    private void writeCOTHTag(EntityLivingBase in, String mobname, NBTTagCompound tags) {
        if (in instanceof EntityArmorStand) {
            tags.setInteger("srpcothimmunity", 0);
        } else if (ParasiteEventEntity.checkName(mobname, SRPConfigSystems.COTHImmuneList, SRPConfigSystems.COTHImmuneListWhite)) {
            tags.setInteger("srpcothimmunity", 0);
        } else {
            tags.setInteger("srpcothimmunity", 1);
        }
        this.setCOTH(in, SRPSaveData.get(in.worldObj, 84).getEvolutionPhase(in.worldObj.provider.dimensionId));
    }

    private void setNewParasiteTask(EntityParasiteBase entity, String mobname, boolean flagNC, SRPWorldData data) {
        SRPSaveData dataS = SRPSaveData.get(entity.worldObj, 82);
        if (!entity.spawnedByColo) {
            EntityCanHaveBodies head;
            entity.spawnedByColo = true;
            switch (dataS.getChoice()) {
                case 2: {
                    this.changeAttribute(entity, SharedMonsterAttributes.maxHealth, 3.0);
                    this.changeAttribute(entity, SharedMonsterAttributes.ARMOR, 3.0);
                    this.changeAttribute(entity, SharedMonsterAttributes.attackDamage, 3.0);
                    this.changeAttribute(entity, SharedMonsterAttributes.knockbackResistance, 3.0);
                    break;
                }
                case 3: {
                    this.changeAttribute(entity, SharedMonsterAttributes.maxHealth, 10.0);
                    this.changeAttribute(entity, SharedMonsterAttributes.ARMOR, 10.0);
                    this.changeAttribute(entity, SharedMonsterAttributes.attackDamage, 10.0);
                    this.changeAttribute(entity, SharedMonsterAttributes.knockbackResistance, 10.0);
                }
            }
            entity.applyBonuses(dataS, entity.worldObj);
            this.applyDislo(entity, dataS, entity.worldObj.provider.dimensionId);
            this.applyColony(entity, data.totalColonyPoints(0));
            this.applyNode(entity, data.totalNodePoints(0));
            if (entity instanceof EntityPMalleable) {
                EntityPMalleable uwu = (EntityPMalleable)entity;
                String damage = data.getMostCommonDamageS();
                if (damage != null) {
                    for (int times = data.getMostCommonDamageI(); times > 0; --times) {
                        uwu.addResistance(damage);
                    }
                    uwu.increaseDamageCap(1);
                    uwu.colonySpawned = true;
                }
            }
            if (entity instanceof EntityCanHaveBodies && (head = (EntityCanHaveBodies)((Object)entity)).getCanF()) {
                int len = head.getBodyLength();
                EntityCanHaveBodies current = head;
                for (int i = 0; i < len; ++i) {
                    EntityCanHaveBodies entityWithBodies = head.getAnotherBody(entity.worldObj);
                    entityWithBodies.setCanF(false);
                    entityWithBodies.setFollowing(current);
                    entityWithBodies.copyCopy(current);
                    entityWithBodies.onSpawn(DifficultyAccess.at(entity.worldObj, EntityQueries.position(entity)), null);
                    entity.worldObj.spawnEntityInWorld(entityWithBodies.getEntity());
                    entityWithBodies.setBodyNumber(i + 1);
                    if (len - 1 == i) {
                        entityWithBodies.setBodyTail(true);
                    }
                    current = entityWithBodies;
                }
            }
        }
        if (SRPConfig.parasiteGriefing.length != 0) {
            String[] task = new String[4];
            for (int i = 0; i < SRPConfig.parasiteGriefing.length; ++i) {
                if (SRPConfig.parasiteGriefing[i] == null || !(task = SRPConfig.parasiteGriefing[i].split(";"))[0].equals(mobname)) continue;
                if (entity instanceof EntityPStationary) {
                    entity.setSkillBreakBlocksValues(Float.parseFloat(task[1]), MathHelper.ceiling_float_int((float)entity.height), Integer.parseInt(task[3]));
                    break;
                }
                entity.setSkillBreakBlocksValues(Float.parseFloat(task[1]), MathHelper.ceiling_float_int((float)entity.height), Integer.parseInt(task[3]));
                entity.tasks.addTask(9, (EntityAIBase)new EntityAISkill(entity, Integer.parseInt(task[2]), 64, false, 13));
                break;
            }
        }
        if (entity instanceof EntityPInfected && SRPConfigSystems.generationUse) {
            entity.setHealth(entity.getHealth() * this.getSimCOTHMod(SRPSaveData.get(entity.worldObj, 83), entity.worldObj));
        }
    }

    private void applyDislo(EntityParasiteBase in, SRPSaveData dataTwo, int id) {
        int[] killa = dataTwo.getDisloValues(id);
        if (SRPConfigSystems.disloSummonByDeath && !in.disloNumberTwo && killa[2] > 0) {
            in.disloNumberTwo = true;
        }
        if (SRPConfigSystems.disloPotiEff && !in.disloNumberThree && killa[3] > 0) {
            in.disloNumberThree = true;
            int amp = dataTwo.getCurrentCode(id, 3);
            String here = SRPConfigSystems.disloPotiEffEffects[in.worldObj.rand.nextInt(SRPConfigSystems.disloPotiEffEffects.length)];
            Potion potion = Potion.getPotionFromResourceLocation((String)here);
            if (potion != null) {
                in.addPotionEffect(new PotionEffect(potion, dataTwo.getCurrentCodeDuration(id, 3) * 20 + 50, amp));
            }
        }
        if (SRPConfigSystems.dislostats && !in.disloNumberFour && killa[4] > 0) {
            in.disloNumberFour = true;
            int multip = dataTwo.getCurrentCode(id, 4);
            in.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(in.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() * (double)(1 + multip));
            in.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(in.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue() * (double)(1 + multip));
            in.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(in.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() * (double)(1 + multip));
        }
        if (SRPConfigSystems.disloItemDura && !in.disloNumberSix && killa[6] > 0) {
            in.disloNumberSix = true;
        }
        if (SRPConfigSystems.disloHealingDeath && !in.disloNumberSeven && killa[7] > 0) {
            in.disloNumberSeven = true;
        }
        if (SRPConfigSystems.disloDamageDeath && !in.disloNumberEight && killa[8] > 0) {
            in.disloNumberEight = true;
        }
        if (SRPConfigSystems.disloFoodDeath && !in.disloNumberNine && killa[9] > 0) {
            in.disloNumberNine = true;
        }
        if (SRPConfigSystems.disloParasiteNoPotion && in.disloNumberEleven <= 0 && killa[11] > 0) {
            in.disloNumberEleven = dataTwo.getCurrentCodeDuration(id, 11) * 20 + 50;
        }
        if (SRPConfigSystems.disloGrowlNoise && in.disloNumberFifteen <= 0 && killa[15] > 0) {
            in.disloNumberFifteen = dataTwo.getCurrentCodeDuration(id, 15) * 20 + 50;
            EntityDataManager.of(in).set(EntityParasiteBase.DISLO15, (Object)true);
        }
        if (SRPConfigSystems.disloWalkNoise && in.disloNumberSixteen <= 0 && killa[16] > 0) {
            in.disloNumberSixteen = dataTwo.getCurrentCodeDuration(id, 16) * 20 + 50;
        }
        if (SRPConfigSystems.disloShieldFood && in.disloNumberSeventeen <= 0 && killa[17] > 0) {
            in.disloNumberSeventeen = dataTwo.getCurrentCodeDuration(id, 17) * 20 + 50;
        }
        if (SRPConfigSystems.disloLootXpCanc && !in.disloNumberEighteen && killa[18] > 0) {
            in.disloNumberEighteen = true;
        }
        if (SRPConfigSystems.disloKillcountInc && in.disloNumberNineteen <= 0 && killa[19] > 0) {
            in.disloNumberNineteen = dataTwo.getCurrentCodeDuration(id, 19) * 20 + 50;
            in.disloNumberNineteenValue = killa[19];
        }
        if (SRPConfigSystems.disloGiveBodies && killa[20] > 0) {
            if (in instanceof EntityInhooM) {
                ((EntityInhooM)in).disloNumberTwenty = true;
            }
            if (in instanceof EntityInhooS) {
                ((EntityInhooS)in).disloNumberTwenty = true;
            }
        }
        if (SRPConfigSystems.disloBurningDeath && !in.disloNumberTwentyone && killa[21] > 0) {
            in.disloNumberTwentyone = true;
        }
        if (SRPConfigSystems.disloSameVersionDyeing && !in.disloNumberTwentytwo && killa[22] > 0) {
            in.disloNumberTwentytwo = true;
        }
    }

    private void applyColony(EntityParasiteBase in, int totalColonyPoints) {
        if (totalColonyPoints == 0) {
            return;
        }
        double bonus = (float)totalColonyPoints / SRPConfigWorld.colonyExtraHealthPoint * SRPConfigWorld.colonyExtraHealthValue;
        this.changeAttribute(in, SharedMonsterAttributes.maxHealth, bonus);
        in.setHealth((float)in.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue());
        bonus = (float)totalColonyPoints / SRPConfigWorld.colonyExtraArmorPoint * SRPConfigWorld.colonyExtraArmorValue;
        this.changeAttribute(in, SharedMonsterAttributes.ARMOR, bonus);
        bonus = (float)totalColonyPoints / SRPConfigWorld.colonyExtraDamagePoint * SRPConfigWorld.colonyExtraDamageValue;
        this.changeAttribute(in, SharedMonsterAttributes.attackDamage, bonus);
        bonus = (float)totalColonyPoints / SRPConfigWorld.colonyExtraKDResPoint * SRPConfigWorld.colonyExtraKDResValue;
        this.changeAttribute(in, SharedMonsterAttributes.knockbackResistance, bonus);
        bonus = (float)totalColonyPoints / SRPConfigWorld.colonyDamageCapPoint * SRPConfigWorld.colonyDamageCapValue;
        in.damageCap = (int)((double)in.damageCap + (double)in.damageCap * bonus);
    }

    private void changeAttribute(EntityParasiteBase in, IAttribute stat, double bonus) {
        double base = in.getEntityAttribute(stat).getBaseValue();
        in.getEntityAttribute(stat).setBaseValue(base + base * bonus);
    }

    private void applyNode(EntityParasiteBase in, int totalPoints) {
        if (totalPoints == 0) {
            return;
        }
        String[] here = new String[3];
        for (String i : SRPConfigWorld.potionEffectForNodes) {
            here = i.split(";");
            int level = Integer.parseInt(here[0]);
            if (totalPoints < level) continue;
            int amp = Integer.parseInt(here[2]);
            Potion potion = Potion.getPotionFromResourceLocation((String)here[1]);
            if (potion == null) continue;
            in.addPotionEffect(new PotionEffect(potion, 7777, amp));
        }
    }

    private float getSimCOTHMod(SRPSaveData data, World world) {
        switch (data.getGeneration(world.provider.dimensionId)) {
            case 0: {
                return SRPConfigSystems.generationCOTH0;
            }
            case 1: {
                return SRPConfigSystems.generationCOTH1;
            }
            case 2: {
                return SRPConfigSystems.generationCOTH2;
            }
            case 3: {
                return SRPConfigSystems.generationCOTH3;
            }
            case 4: {
                return SRPConfigSystems.generationCOTH4;
            }
            case 5: {
                return SRPConfigSystems.generationCOTH5;
            }
        }
        return 1.0f;
    }

    @SubscribeEvent
    public void onEntityMount(EntityMountEvent event) {
        if (event.getEntityBeingMounted() == null || event.getEntityMounting() == null) {
            return;
        }
        if (!event.getEntityBeingMounted().isEntityAlive()) {
            return;
        }
        if (event.getEntityMounting() instanceof EntityPlayer && event.getEntityBeingMounted() instanceof EntityHiGolem) {
            EntityPlayer player = (EntityPlayer)event.getEntityMounting();
            if (!player.capabilities.disableDamage && player.getHealth() > 0.0f && event.isDismounting()) {
                event.setCanceled(true);
            }
        }
    }

    private void setNewCreatureTask(EntityCreature entity, String mobname) {
        if (ParasiteEventEntity.checkName(mobname, SRPConfig.entitiesWillAttack, SRPConfig.entitiesWillAttackWhite)) {
            entity.targetTasks.addTask(5, (EntityAIBase)new EntityAINearestAttackableTarget(entity, EntityParasiteBase.class, true));
            return;
        }
        if (ParasiteEventEntity.checkName(mobname, SRPConfig.entitiesWillAvoid, SRPConfig.entitiesWillAvoidWhite)) {
            entity.tasks.addTask(5, (EntityAIBase)new EntityAIAvoidEntity(entity, EntityParasiteBase.class, 12.0f, 0.8, 0.8));
        }
    }

    private void setCOTH(EntityLivingBase target, byte evo) {
        if (target.isChild()) {
            return;
        }
        Random rand = new Random();
        switch (evo) {
            case 1: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceOne)) break;
                target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 2: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceTwo)) break;
                target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 3: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceThree)) break;
                target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 4: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceFour)) break;
                target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 5: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceFive)) break;
                target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 6: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceSix)) break;
                target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 7: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceSeven)) break;
                target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 8: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceEight)) break;
                target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }
        if (BeckonBlockInfestation.blockInfestedCount > SRPConfig.BlockInfestedLimit) {
            ++this.blockInfestedCountCooldown;
            if (this.blockInfestedCountCooldown > SRPConfig.BlockInfestedLimitCD) {
                this.blockInfestedCountCooldown = 0;
                BeckonBlockInfestation.blockInfestedCount = 0;
            }
        }
        if (BlockParasiteSpreading.blockParasiteCount > SRPConfig.BlockParasiteLimit) {
            ++this.blockParasiteCountCooldown;
            if (this.blockParasiteCountCooldown > SRPConfig.BlockParasiteLimitCD) {
                this.blockParasiteCountCooldown = 0;
                BlockParasiteSpreading.blockParasiteCount = 0;
            }
        }
        ++this.counerW;
        if (SRPConfigWorld.originWorldCheckDebugSpeed > 0) {
            this.counerW += SRPConfigWorld.originWorldCheckDebugSpeed;
        }
        --ParasiteEventWorld.disloCool;
        if (this.counerW > SRPConfig.dayTickValue && (SRPConfigWorld.nodesActivated || SRPConfigWorld.coloniesActivated || SRPConfigSystems.useEvolution || SRPConfigWorld.originActivated)) {
            MinecraftServer ser = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (ser.worldServers.length > 0) {
                SRPSaveData dat = SRPSaveData.get((World)ser.worldServers[0], 89);
                dat.addUpdateNumber(1);
            }
            this.worldsChecked = new ArrayList();
            this.counerW = -150;
        }
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void worldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }
        if (!event.world.isRemote) {
            ++this.moo;
            if (SRPConfigSystems.useEvolution && SRPConfigSystems.phaseCustomSpawner) {
                if (event.world.getMinecraftServer() == null) {
                    return;
                }
                this.tickSpawn(event.world.getMinecraftServer().worldServerForDimension(event.world.provider.dimensionId));
            }
            if (this.moo >= 20 * SRPConfigSystems.disloSeconds) {
                this.moo = 0;
                SRPSaveData.get(event.world, 88).reduceCodesCooldown(event.world.provider.dimensionId, SRPConfigSystems.disloSeconds, event.world);
            }
            if (!SRPConfigWorld.meteorActive) {
                this.meteor = 0;
            } else {
                ++this.meteor;
                if (this.meteor > SRPConfigWorld.meteorTick) {
                    SRPWorldData data;
                    this.meteor = 0;
                    if (event.world.rand.nextDouble() < SRPConfigWorld.meteorChance && SRPConfig.spawnDays <= (int)event.world.getTotalWorldTime() && (data = SRPWorldData.get(event.world)).getTriggerMet() && SRPSaveData.get(event.world, -549).getEvolutionPhase(event.world.provider.dimensionId) >= 0) {
                        if (SRPConfigWorld.meteorVectorless) {
                            if (data.getorigins("x").isEmpty()) {
                                this.spawningMet(event.world);
                            }
                        } else {
                            this.spawningMet(event.world);
                        }
                    }
                }
            }
            if (this.counerW < 0 && (SRPConfigWorld.nodesActivated || SRPConfigWorld.coloniesActivated || SRPConfigSystems.useEvolution || SRPConfigWorld.originActivated)) {
                int id = event.world.provider.dimensionId;
                for (int i : this.worldsChecked) {
                    if (i != id) continue;
                    return;
                }
                ParasiteEventWorld.checkColonyStatus(event.world);
                ParasiteEventWorld.checkNodeStatus(event.world);
                SRPMain.logger.debug("[EIV DEBUG] worldTick is calling createRandomOrigin. dim={} time={} counerW={} worldsChecked={} originActivated={} min={} max={}", (Object)event.world.provider.dimensionId, (Object)event.world.getTotalWorldTime(), (Object)this.counerW, this.worldsChecked, (Object)SRPConfigWorld.originActivated, (Object)SRPConfigWorld.originCreatingDistanceMin, (Object)SRPConfigWorld.originCreatingDistanceMax);
                EIVUtil.createRandomOrigin(event.world, SRPConfigWorld.originCreatingDistanceMin, SRPConfigWorld.originCreatingDistanceMax);
                this.worldsChecked.add(id);
            }
        }
    }

    private void spawningMet(World world) {
        block2: {
            EntityPlayer mob;
            ArrayList mobs = Lists.newArrayList();
            mobs.addAll(EntityQueries.players(world));
            boolean flag = true;
            if (mobs.size() == 0) break block2;
            Iterator iterator = mobs.iterator();
            while (iterator.hasNext()) {
                mob = (EntityPlayer)iterator.next();
                if (!world.canBlockSeeSky(EntityQueries.position(mob))) continue;
                ParasiteSummon.spawnMeteor(EntityQueries.position(mob), world.rand.nextInt(SRPConfigWorld.meteorRadius), SRPConfigWorld.meteorMinRadius, world);
                flag = false;
                break;
            }
            if (flag && (iterator = mobs.iterator()).hasNext()) {
                mob = (EntityPlayer)iterator.next();
                ParasiteSummon.spawnMeteor(EntityQueries.position(mob), world.rand.nextInt(SRPConfigWorld.meteorRadius), SRPConfigWorld.meteorMinRadius, world);
            }
        }
    }

    private void tickSpawn(WorldServer server) {
        if (server.getGameRules().getGameRuleBooleanValue("doMobSpawning") && server.getWorldInfo().getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
            SRPWorldParasiteSpawner.findChunksForSpawning(server, true, false, server.getWorldInfo().getWorldTotalTime() % 400L == 0L);
        }
    }

    @SubscribeEvent
    public void setLoot(LivingDropsEvent event) {
        if (event.getEntityLiving() instanceof EntityParasiteBase) {
            if (!event.getEntityLiving().worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                return;
            }
            if (event.getEntityLiving().isPotionActive(SRPPotions.DEBAR_E)) {
                return;
            }
            EntityParasiteBase mob = (EntityParasiteBase)event.getEntityLiving();
            if (mob.disloNumberEighteen && SRPSaveData.get(event.getEntityLiving().worldObj, 87).getCurrentCode(event.getEntityLiving().worldObj.provider.dimensionId, 18) > 0) {
                return;
            }
            switch (mob.getParasiteIDRegister()) {
                case 1: {
                    this.loot(event, SRPConfigMobs.shycoLoot);
                    return;
                }
                case 51: {
                    this.loot(event, SRPConfigMobs.shycoadaptedloot);
                    return;
                }
                case 10: {
                    this.loot(event, SRPConfigMobs.noglaLoot);
                    return;
                }
                case 54: {
                    this.loot(event, SRPConfigMobs.noglaadaptedloot);
                    return;
                }
                case 4: {
                    this.loot(event, SRPConfigMobs.emanaLoot);
                    return;
                }
                case 55: {
                    this.loot(event, SRPConfigMobs.emanaadaptedloot);
                    return;
                }
                case 7: {
                    this.loot(event, SRPConfigMobs.hullLoot);
                    return;
                }
                case 52: {
                    this.loot(event, SRPConfigMobs.hulladaptedloot);
                    return;
                }
                case 8: {
                    this.loot(event, SRPConfigMobs.canraLoot);
                    return;
                }
                case 53: {
                    this.loot(event, SRPConfigMobs.canraadaptedloot);
                    return;
                }
                case 38: {
                    this.loot(event, SRPConfigMobs.arachnidaLoot);
                    return;
                }
                case 58: {
                    this.loot(event, SRPConfigMobs.arachnidaadaptedloot);
                    return;
                }
                case 37: {
                    this.loot(event, SRPConfigMobs.shycoLoot);
                    return;
                }
                case 57: {
                    this.loot(event, SRPConfigMobs.shycoLoot);
                    return;
                }
                case 66: {
                    this.loot(event, SRPConfigMobs.lumLoot);
                    return;
                }
                case 81: {
                    this.loot(event, SRPConfigMobs.lumadaptedloot);
                    return;
                }
                case 17: {
                    this.loot(event, SRPConfigMobs.zetmoLoot);
                    return;
                }
                case 56: {
                    this.loot(event, SRPConfigMobs.zetmoadaptedloot);
                    return;
                }
                case 2: {
                    this.loot(event, SRPConfigMobs.dorpaLoot);
                    return;
                }
                case 49: {
                    this.loot(event, SRPConfigMobs.infbearLoot);
                    return;
                }
                case 13: {
                    this.loot(event, SRPConfigMobs.infcowLoot);
                    return;
                }
                case 28: {
                    this.loot(event, SRPConfigMobs.infcowheadLoot);
                    return;
                }
                case 64: {
                    this.loot(event, SRPConfigMobs.infdragoneLoot);
                    return;
                }
                case 70: {
                    this.loot(event, SRPConfigMobs.infdragoneheadLoot);
                    return;
                }
                case 59: {
                    this.loot(event, SRPConfigMobs.infendermanLoot);
                    return;
                }
                case 69: {
                    this.loot(event, SRPConfigMobs.infendermanheadLoot);
                    return;
                }
                case 44: {
                    this.loot(event, SRPConfigMobs.infhorseLoot);
                    return;
                }
                case 45: {
                    this.loot(event, SRPConfigMobs.infhorseheadLoot);
                    return;
                }
                case 6: {
                    this.loot(event, SRPConfigMobs.infhumanLoot);
                    return;
                }
                case 46: {
                    this.loot(event, SRPConfigMobs.infhumanheadLoot);
                    return;
                }
                case 26: {
                    this.loot(event, SRPConfigMobs.infpigLoot);
                    return;
                }
                case 31: {
                    this.loot(event, SRPConfigMobs.infpigheadLoot);
                    return;
                }
                case 40: {
                    this.loot(event, SRPConfigMobs.infadventurerLoot);
                    return;
                }
                case 71: {
                    this.loot(event, SRPConfigMobs.infadventurerheadLoot);
                    return;
                }
                case 14: {
                    this.loot(event, SRPConfigMobs.infsheepLoot);
                    return;
                }
                case 22: {
                    this.loot(event, SRPConfigMobs.infsheepheadLoot);
                    return;
                }
                case 27: {
                    this.loot(event, SRPConfigMobs.infvillagerLoot);
                    return;
                }
                case 32: {
                    this.loot(event, SRPConfigMobs.infvillagerheadLoot);
                    return;
                }
                case 15: {
                    this.loot(event, SRPConfigMobs.infwolfLoot);
                    return;
                }
                case 21: {
                    this.loot(event, SRPConfigMobs.infwolfheadLoot);
                    return;
                }
                case 306: {
                    this.loot(event, SRPConfigMobs.ferbearLoot);
                    return;
                }
                case 93: {
                    this.loot(event, SRPConfigMobs.fercowLoot);
                    return;
                }
                case 94: {
                    this.loot(event, SRPConfigMobs.ferendermanLoot);
                    return;
                }
                case 95: {
                    this.loot(event, SRPConfigMobs.ferhorseLoot);
                    return;
                }
                case 96: {
                    this.loot(event, SRPConfigMobs.ferhumanLoot);
                    return;
                }
                case 97: {
                    this.loot(event, SRPConfigMobs.ferpigLoot);
                    return;
                }
                case 98: {
                    this.loot(event, SRPConfigMobs.fersheepLoot);
                    return;
                }
                case 99: {
                    this.loot(event, SRPConfigMobs.fervillagerLoot);
                    return;
                }
                case 300: {
                    this.loot(event, SRPConfigMobs.ferwolfLoot);
                    return;
                }
                case 324: {
                    this.loot(event, SRPConfigMobs.marhumanLoot);
                    return;
                }
                case 330: {
                    this.loot(event, SRPConfigMobs.marbearLoot);
                    return;
                }
                case 322: {
                    this.loot(event, SRPConfigMobs.marcowLoot);
                    return;
                }
                case 329: {
                    this.loot(event, SRPConfigMobs.marsheepLoot);
                    return;
                }
                case 321: {
                    this.loot(event, SRPConfigMobs.marendermanLoot);
                    return;
                }
                case 323: {
                    this.loot(event, SRPConfigMobs.marvillagerLoot);
                    return;
                }
                case 302: {
                    this.loot(event, SRPConfigMobs.hiblazeLoot);
                    return;
                }
                case 301: {
                    this.loot(event, SRPConfigMobs.higolemLoot);
                    return;
                }
                case 303: {
                    this.loot(event, SRPConfigMobs.hiskeletonLoot);
                    return;
                }
                case 9: {
                    this.loot(event, SRPConfigMobs.alafhaLoot);
                    return;
                }
                case 25: {
                    this.loot(event, SRPConfigMobs.angedLoot);
                    return;
                }
                case 50: {
                    this.loot(event, SRPConfigMobs.esorLoot);
                    return;
                }
                case 60: {
                    this.loot(event, SRPConfigMobs.flogLoot);
                    return;
                }
                case 33: {
                    this.loot(event, SRPConfigMobs.ganroLoot);
                    return;
                }
                case 47: {
                    this.loot(event, SRPConfigMobs.ombooLoot);
                    return;
                }
                case 82: {
                    this.loot(event, SRPConfigMobs.ombooLoot);
                    return;
                }
                case 65: {
                    this.loot(event, SRPConfigMobs.jinjoLoot);
                    return;
                }
                case 85: {
                    this.loot(event, SRPConfigMobs.elviaLoot);
                    return;
                }
                case 86: {
                    this.loot(event, SRPConfigMobs.lenciaLoot);
                    return;
                }
                case 88: {
                    this.loot(event, SRPConfigMobs.vestaLoot);
                    return;
                }
                case 87: {
                    this.loot(event, SRPConfigMobs.pheonLoot);
                    return;
                }
                case 11: {
                    this.loot(event, SRPConfigMobs.butholLoot);
                    return;
                }
                case 36: {
                    this.loot(event, SRPConfigMobs.kolLoot);
                    return;
                }
                case 23: {
                    this.loot(event, SRPConfigMobs.kolLoot);
                    return;
                }
                case 5: {
                    this.loot(event, SRPConfigMobs.LodoLoot);
                    return;
                }
                case 12: {
                    this.loot(event, SRPConfigMobs.mudoLoot);
                    return;
                }
                case 76: {
                    this.loot(event, SRPConfigMobs.nuuhLoot);
                    return;
                }
                case 3: {
                    this.loot(event, SRPConfigMobs.ratholLoot);
                    return;
                }
                case 91: {
                    this.loot(event, SRPConfigMobs.ataLoot);
                    return;
                }
                case 334: {
                    this.loot(event, SRPConfigMobs.viinLoot);
                    return;
                }
                case 74: {
                    this.loot(event, SRPConfigMobs.ratholLoot);
                    return;
                }
                case 72: {
                    this.loot(event, SRPConfigMobs.nakLoot);
                    return;
                }
                case 29: {
                    this.loot(event, SRPConfigMobs.tonroLoot);
                    return;
                }
                case 30: {
                    this.loot(event, SRPConfigMobs.unvoLoot);
                    return;
                }
                case 16: {
                    this.loot(event, SRPConfigMobs.venkrolLoot);
                    return;
                }
                case 18: {
                    this.loot(event, SRPConfigMobs.venkrolsiiLoot);
                    return;
                }
                case 19: {
                    this.loot(event, SRPConfigMobs.venkrolsiiiLoot);
                    return;
                }
                case 41: {
                    this.loot(event, SRPConfigMobs.venkrolsivLoot);
                    return;
                }
                case 73: {
                    this.loot(event, SRPConfigMobs.dodsiLoot);
                    return;
                }
                case 77: {
                    this.loot(event, SRPConfigMobs.dodsiiLoot);
                    return;
                }
                case 78: {
                    this.loot(event, SRPConfigMobs.dodsiiiLoot);
                    return;
                }
                case 79: {
                    this.loot(event, SRPConfigMobs.dodsivLoot);
                    return;
                }
                case 62: {
                    this.loot(event, SRPConfigMobs.cruxaLoot);
                    return;
                }
                case 63: {
                    this.loot(event, SRPConfigMobs.heedLoot);
                    return;
                }
                case 48: {
                    this.loot(event, SRPConfigMobs.hostLoot);
                    return;
                }
                case 75: {
                    this.loot(event, SRPConfigMobs.herdLoot);
                    return;
                }
                case 39: {
                    this.loot(event, SRPConfigMobs.inhooSLoot);
                    return;
                }
                case 43: {
                    this.loot(event, SRPConfigMobs.inhooMLoot);
                    return;
                }
                case 80: {
                    this.loot(event, SRPConfigMobs.thrallLoot);
                    return;
                }
                case 24: {
                    this.loot(event, SRPConfigMobs.oroncoLoot);
                    return;
                }
                case 20: {
                    this.loot(event, SRPConfigMobs.terlaLoot);
                    return;
                }
                case 309: {
                    this.loot(event, SRPConfigMobs.hebluLoot);
                    return;
                }
                case 67: {
                    this.loot(event, SRPConfigMobs.kirinLoot);
                    return;
                }
                case 34: {
                    this.loot(event, SRPConfigMobs.pod1Loot);
                }
            }
        } else if (event.getEntity() instanceof EntityLivingBase && !(event.getEntity() instanceof EntityPlayer) && SRPConfigSystems.cothActive) {
            if (event.getSource().getEntity() instanceof EntityParasiteBase && SRPConfig.mobsKilledDropLoot) {
                event.setCanceled(true);
                return;
            }
            if (((EntityLivingBase)event.getEntity()).isPotionActive(SRPPotions.COTH_E)) {
                int key;
                NBTTagCompound tags;
                if (SRPConfigSystems.useEvolution) {
                    int key2;
                    NBTTagCompound tags2;
                    if (SRPSaveData.get(event.getEntity().worldObj, 86).getEvolutionPhase(event.getEntity().worldObj.provider.dimensionId) >= SRPConfigSystems.evolutionCothStopLoot && (tags2 = event.getEntity().getEntityData()).hasKey("srpcothimmunity") && (key2 = tags2.getInteger("srpcothimmunity")) != 0) {
                        event.setCanceled(true);
                    }
                } else if (SRPConfigSystems.cothLootDisable && (tags = event.getEntity().getEntityData()).hasKey("srpcothimmunity") && (key = tags.getInteger("srpcothimmunity")) != 0) {
                    event.setCanceled(true);
                }
            }
        }
    }

    private void loot(LivingDropsEvent event, String[] drop) {
        block8: {
            try {
                int realquantity;
                int rng;
                int chance;
                int quantity;
                if (drop.length == 0) break block8;
                String[] dropping = new String[4];
                String[] dropped = new String[drop.length];
                Random rand = new Random();
                int totalFalse = 0;
                for (String s : drop) {
                    dropping = s.split(";");
                    boolean always = Boolean.parseBoolean(dropping[3]);
                    quantity = Integer.parseInt(dropping[2]);
                    chance = Integer.parseInt(dropping[1]);
                    if (always) {
                        rng = rand.nextInt(100);
                        if (rng > chance - 1) continue;
                        Item item = Item.setTextureName((String)dropping[0]);
                        realquantity = rand.nextInt(quantity);
                        for (int j = 0; j <= realquantity && item != null; ++j) {
                            BlockPos pos = event.getEntity().getPosition();
                            event.getDrops().add(new EntityItem(event.getEntity().getEntityWorld(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), new ItemStack(item)));
                        }
                        continue;
                    }
                    dropped[totalFalse] = s;
                    ++totalFalse;
                }
                if (totalFalse != 0) {
                    int n = rand.nextInt(totalFalse);
                    String[] stringItem = dropped[n].split(";");
                    quantity = Integer.parseInt(stringItem[2]);
                    chance = Integer.parseInt(stringItem[1]);
                    rng = rand.nextInt(100);
                    if (rng <= chance - 1) {
                        Item item = Item.setTextureName((String)stringItem[0]);
                        realquantity = rand.nextInt(quantity);
                        for (int j = 0; j <= realquantity && item != null; ++j) {
                            BlockPos pos = event.getEntity().getPosition();
                            event.getDrops().add(new EntityItem(event.getEntity().getEntityWorld(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), new ItemStack(item)));
                        }
                    }
                }
            }
            catch (Exception e) {
                SRPMain.logger.log(Level.ERROR, "Problem with loot event", (Throwable)e);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            EntityPlayer thePlayer = event.player;
            if (thePlayer.worldObj.isRemote) {
                if (this.closeG) {
                    if (thePlayer.openContainer != null) {
                        thePlayer.closeScreen();
                    }
                    this.closeG = false;
                }
            } else {
                SRPWorldData data;
                int age;
                boolean isParaBiome = thePlayer.worldObj.getBiome(EntityQueries.position(thePlayer)) instanceof BiomeParasiteBase;
                if (ParasiteEventWorld.canBiomeStillExist(thePlayer.worldObj, EntityQueries.position(thePlayer), false) >= 1 || isParaBiome) {
                    BiomeParasiteBase biomeChecked = isParaBiome ? (BiomeParasiteBase)thePlayer.worldObj.getBiome(EntityQueries.position(thePlayer)) : SRPReference.getBiomeFromInt(ParasiteEventWorld.canBiomeStillExistType(thePlayer.worldObj, EntityQueries.position(thePlayer), false));
                    fog = Math.min(fog + 4.5E-4f, SRPConfigWorld.biomeFogDensity);
                    fogRed = biomeChecked.getRedValue();
                    fogGreen = biomeChecked.getGreenValue();
                    fogBlue = biomeChecked.getBlueValue();
                    if (fog < SRPConfigWorld.biomeFogDensity) {
                        SRPMain.network.sendTo((IMessage)new SRPPacketFog(fog, fogRed, fogGreen, fogBlue), (EntityPlayerMP)thePlayer);
                    }
                } else if (fog > 0.0f) {
                    fog = Math.max(fog - 8.0E-4f, 0.0f);
                    fogRed = 0.0f;
                    fogGreen = 0.0f;
                    fogBlue = 0.0f;
                    SRPMain.network.sendTo((IMessage)new SRPPacketFog(fog, fogRed, fogGreen, fogBlue), (EntityPlayerMP)thePlayer);
                }
                ++this.heart;
                if (this.heart < SRPConfigWorld.biomeHeartFreq) {
                    return;
                }
                this.heart = 0;
                if (isParaBiome && (age = (data = SRPWorldData.get(thePlayer.worldObj)).nearestHeartAge(EntityQueries.position(thePlayer), true, 0)) > 0) {
                    int totalS = data.getDistanceSpreadByAge(age, true);
                    float vol = ((float)data.isInRangeOfHeart(EntityQueries.position(thePlayer), totalS) / (float)totalS - 1.0f) * -1.0f;
                    SRPMain.network.sendTo((IMessage)new SRPPacketMovingSound(-1, vol), (EntityPlayerMP)thePlayer);
                }
            }
        }
    }

    @SubscribeEvent
    public void playerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntity();
            if (SRPConfigSystems.cothActive && SRPConfigMobs.infadventurerEnabled && SRPConfigMobs.infadventurerSpawnBy && player.isPotionActive(SRPPotions.COTH_E)) {
                int amp = player.getActivePotionEffect(SRPPotions.COTH_E).getAmplifier();
                if (amp >= 2) {
                    EntityInfPlayer out = new EntityInfPlayer(player.worldObj);
                    ItemStack head = new ItemStack(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem());
                    ItemStack legs = new ItemStack(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem());
                    ItemStack feet = new ItemStack(player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem());
                    if (head.getItem() != Items.AIR) {
                        out.setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
                        out.setHelmetSlot(true);
                    }
                    out.setItemStackToSlot(EntityEquipmentSlot.LEGS, legs);
                    out.setItemStackToSlot(EntityEquipmentSlot.FEET, feet);
                    out.copyLocationAndAnglesFrom((Entity)player);
                    out.onInitialSpawn(DifficultyAccess.at(player.worldObj, EntityQueries.position(out)), null);
                    player.worldObj.spawnEntityInWorld((Entity)out);
                    player.worldObj.playEvent((EntityPlayer)null, 1026, new BlockPos((Entity)out), 0);
                    out.setMinecartName(player.getCommandSenderName());
                    out.setAlwaysRenderNameTag(true);
                    out.particleStatus((byte)7);
                    out.cannotDespawn(false);
                } else if (amp == 1) {
                    EntityInhooM out = new EntityInhooM(player.worldObj);
                    out.copyLocationAndAnglesFrom((Entity)player);
                    out.onInitialSpawn(DifficultyAccess.at(player.worldObj, EntityQueries.position(out)), null);
                    player.worldObj.spawnEntityInWorld((Entity)out);
                    player.worldObj.playEvent((EntityPlayer)null, 1026, new BlockPos((Entity)out), 0);
                    out.setMinecartName(player.getCommandSenderName());
                    out.setAlwaysRenderNameTag(true);
                    out.particleStatus((byte)7);
                    out.cannotDespawn(false);
                }
            }
        }
    }

    @SubscribeEvent
    public void playerUp(PlayerWakeUpEvent event) {
        if (!SRPConfigSystems.useEvolution || event.getEntity().worldObj.isRemote) {
            return;
        }
        SRPSaveData data = SRPSaveData.get(event.getEntityPlayer().worldObj, 85);
        World world = event.getEntityPlayer().worldObj;
        if (event.getEntity().worldObj.getWorldTime() % (long)SRPConfig.dayTickValue < 13000L) {
            int bonus = 1;
            if (data.getEvolutionPhase(world.provider.dimensionId) >= SRPConfigSystems.evolutionSleepDenied) {
                bonus = 5;
            }
            data.setTotalKills(world.provider.dimensionId, this.getSleepPointP(data.getEvolutionPhase(world.provider.dimensionId)) * bonus, true, world, true, true, 55);
        }
    }

    private int getSleepPointP(byte phase) {
        switch (phase) {
            case 0: {
                return SRPConfigSystems.sleepPenaltyZero;
            }
            case 1: {
                return SRPConfigSystems.sleepPenaltyOne;
            }
            case 2: {
                return SRPConfigSystems.sleepPenaltyTwo;
            }
            case 3: {
                return SRPConfigSystems.sleepPenaltyThree;
            }
            case 4: {
                return SRPConfigSystems.sleepPenaltyFour;
            }
            case 5: {
                return SRPConfigSystems.sleepPenaltyFive;
            }
            case 6: {
                return SRPConfigSystems.sleepPenaltySix;
            }
            case 7: {
                return SRPConfigSystems.sleepPenaltySeven;
            }
            case 8: {
                return SRPConfigSystems.sleepPenaltyEight;
            }
            case 9: {
                return SRPConfigSystems.sleepPenaltyNine;
            }
            case 10: {
                return SRPConfigSystems.sleepPenaltyTen;
            }
        }
        return 0;
    }

    @SubscribeEvent
    public void light(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof EntityParasiteBase) {
            ((EntityParasiteBase)event.getEntity()).setKillC(1000000.0);
        }
    }

    @SideOnly(value=Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(MouseEvent event) {
        if (SRPConfig.weaponCancelPacket) {
            return;
        }
        if (event.getButton() == 0 && event.isButtonstate()) {
            float reach;
            RayTraceResult mov;
            IHaveReach ieri;
            ItemStack itemstack;
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayerSP thePlayer = mc.thePlayer;
            if (thePlayer != null && (itemstack = thePlayer.getHeldItemMainhand()) != null && (ieri = itemstack.getItem() instanceof IHaveReach ? (IHaveReach)itemstack.getItem() : null) != null && (mov = this.getMouseOverExtended(reach = ieri.getReach())) != null && mov.entityHit != null && mov.entityHit.hurtResistantTime == 0 && mov.entityHit != thePlayer) {
                SRPMain.network.sendToServer((IMessage)new SRPPacketMeleeRange(mov.entityHit.getEntityId()));
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        if (event.getButton() != 0 || !event.isButtonstate()) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.thePlayer;
        if (p == null) {
            return;
        }
        Item item = p.getHeldItemMainhand().getItem();
        if (!(item instanceof WeaponToolMeleeBase)) {
            return;
        }
        if (p.getCooldownTracker().hasCooldown(item)) {
            event.setCanceled(true);
        }
    }

    private RayTraceResult getMouseOverExtended(float dist) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        Entity theRenderViewEntity = mc.getRenderViewEntity();
        assert (theRenderViewEntity != null);
        AxisAlignedBB theViewBoundingBox = Boxes.create(theRenderViewEntity.posX - 0.5, theRenderViewEntity.posY - 0.0, theRenderViewEntity.posZ - 0.5, theRenderViewEntity.posX + 0.5, theRenderViewEntity.posY + 1.5, theRenderViewEntity.posZ + 0.5);
        RayTraceResult returnMOP = null;
        if (mc.theWorld != null) {
            double var2 = dist;
            returnMOP = theRenderViewEntity.rayTrace(var2, 0.0f);
            double calcdist = var2;
            Vec3d pos = theRenderViewEntity.getPositionEyes(0.0f);
            var2 = calcdist;
            if (returnMOP != null) {
                calcdist = returnMOP.hitVec.distanceTo(pos);
            }
            Vec3d lookvec = theRenderViewEntity.getLook(0.0f);
            Vec3d var8 = pos.addVector(lookvec.xCoord * var2, lookvec.yCoord * var2, lookvec.zCoord * var2);
            Entity pointedEntity = null;
            float var9 = 1.0f;
            List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(theRenderViewEntity, Boxes.grow(Boxes.expand(theViewBoundingBox, lookvec.xCoord * var2, lookvec.yCoord * var2, lookvec.zCoord * var2), (double)var9, (double)var9, (double)var9));
            double d = calcdist;
            for (Entity entity : list) {
                double d1;
                if (!entity.canBeCollidedWith()) continue;
                float bordersize = entity.getCollisionBorderSize();
                AxisAlignedBB aabb = Boxes.create(entity.posX - (double)(entity.width / 2.0f), entity.posY, entity.posZ - (double)(entity.width / 2.0f), entity.posX + (double)(entity.width / 2.0f), entity.posY + (double)entity.height, entity.posZ + (double)(entity.width / 2.0f));
                Boxes.expand(aabb, (double)bordersize, (double)bordersize, (double)bordersize);
                RayTraceResult mop0 = Boxes.calculateIntercept(aabb, pos, var8);
                if (Boxes.contains(aabb, pos)) {
                    if (!(0.0 < d) && d != 0.0) continue;
                    pointedEntity = entity;
                    d = 0.0;
                    continue;
                }
                if (mop0 == null || !((d1 = pos.distanceTo(mop0.hitVec)) < d) && d != 0.0) continue;
                pointedEntity = entity;
                d = d1;
            }
            if (pointedEntity != null && (d < calcdist || returnMOP == null)) {
                returnMOP = new RayTraceResult(pointedEntity);
            }
        }
        return returnMOP;
    }

    static {
        clientScent = 0;
        clientVector = 0;
        musicTimer = 1000;
        fog = 0.0f;
        fogRed = 0.0f;
        fogGreen = 0.0f;
        fogBlue = 0.0f;
        RICARDO_DEATH_RULE_RESTORE = new HashMap<Integer, Long>();
        srpSoakGuard = false;
    }

    public static class FogProperties {
        public float density;
        public float red;
        public float green;
        public float blue = 0.0f;

        public FogProperties(float red, float green, float blue, float density) {
            this.density = density;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public FogProperties() {
        }
    }
}

