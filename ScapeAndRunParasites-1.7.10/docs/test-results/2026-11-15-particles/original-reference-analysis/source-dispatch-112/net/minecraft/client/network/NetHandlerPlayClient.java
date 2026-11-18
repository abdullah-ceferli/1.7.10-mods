/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.util.concurrent.FutureCallback
 *  com.google.common.util.concurrent.Futures
 *  com.mojang.authlib.GameProfile
 *  io.netty.buffer.Unpooled
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.renderer.debug.DebugRendererNeighborsUpdate;
import net.minecraft.client.renderer.debug.DebugRendererPathfinding;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient
implements INetHandlerPlayClient {
    private static final Logger field_147301_d = LogManager.getLogger();
    private final NetworkManager field_147302_e;
    private final GameProfile field_175107_d;
    private final GuiScreen field_147307_j;
    private Minecraft field_147299_f;
    private WorldClient field_147300_g;
    private boolean field_147309_h;
    private final Map<UUID, NetworkPlayerInfo> field_147310_i = Maps.newHashMap();
    public int field_147304_c = 20;
    private boolean field_147308_k;
    private final ClientAdvancementManager field_191983_k;
    private final Random field_147306_l = new Random();

    public NetHandlerPlayClient(Minecraft minecraft, GuiScreen guiScreen, NetworkManager networkManager, GameProfile gameProfile) {
        this.field_147299_f = minecraft;
        this.field_147307_j = guiScreen;
        this.field_147302_e = networkManager;
        this.field_175107_d = gameProfile;
        this.field_191983_k = new ClientAdvancementManager(minecraft);
    }

    public void func_147296_c() {
        this.field_147300_g = null;
    }

    @Override
    public void func_147282_a(SPacketJoinGame sPacketJoinGame) {
        PacketThreadUtil.func_180031_a(sPacketJoinGame, this, this.field_147299_f);
        this.field_147299_f.field_71442_b = new PlayerControllerMP(this.field_147299_f, this);
        this.field_147300_g = new WorldClient(this, new WorldSettings(0L, sPacketJoinGame.func_149198_e(), false, sPacketJoinGame.func_149195_d(), sPacketJoinGame.func_149196_i()), sPacketJoinGame.func_149194_f(), sPacketJoinGame.func_149192_g(), this.field_147299_f.field_71424_I);
        this.field_147299_f.field_71474_y.field_74318_M = sPacketJoinGame.func_149192_g();
        this.field_147299_f.func_71403_a(this.field_147300_g);
        this.field_147299_f.field_71439_g.field_71093_bK = sPacketJoinGame.func_149194_f();
        this.field_147299_f.func_147108_a(new GuiDownloadTerrain());
        this.field_147299_f.field_71439_g.func_145769_d(sPacketJoinGame.func_149197_c());
        this.field_147304_c = sPacketJoinGame.func_149193_h();
        this.field_147299_f.field_71439_g.func_175150_k(sPacketJoinGame.func_179744_h());
        this.field_147299_f.field_71442_b.func_78746_a(sPacketJoinGame.func_149198_e());
        this.field_147299_f.field_71474_y.func_82879_c();
        this.field_147302_e.func_179290_a(new CPacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).func_180714_a(ClientBrandRetriever.getClientModName())));
    }

    @Override
    public void func_147235_a(SPacketSpawnObject sPacketSpawnObject2) {
        SPacketSpawnObject sPacketSpawnObject2;
        Entity[] entityArray;
        PacketThreadUtil.func_180031_a(sPacketSpawnObject2, this, this.field_147299_f);
        double d = sPacketSpawnObject2.func_186880_c();
        \u2603 = sPacketSpawnObject2.func_186882_d();
        \u2603 = sPacketSpawnObject2.func_186881_e();
        Entity \u26032 = null;
        if (sPacketSpawnObject2.func_148993_l() == 10) {
            \u26032 = EntityMinecart.func_184263_a(this.field_147300_g, d, \u2603, \u2603, EntityMinecart.Type.func_184955_a(sPacketSpawnObject2.func_149009_m()));
        } else if (sPacketSpawnObject2.func_148993_l() == 90) {
            entityArray = this.field_147300_g.func_73045_a(sPacketSpawnObject2.func_149009_m());
            if (entityArray instanceof EntityPlayer) {
                \u26032 = new EntityFishHook(this.field_147300_g, (EntityPlayer)entityArray, d, \u2603, \u2603);
            }
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 60) {
            \u26032 = new EntityTippedArrow(this.field_147300_g, d, \u2603, \u2603);
        } else if (sPacketSpawnObject2.func_148993_l() == 91) {
            \u26032 = new EntitySpectralArrow(this.field_147300_g, d, \u2603, \u2603);
        } else if (sPacketSpawnObject2.func_148993_l() == 61) {
            \u26032 = new EntitySnowball(this.field_147300_g, d, \u2603, \u2603);
        } else if (sPacketSpawnObject2.func_148993_l() == 68) {
            \u26032 = new EntityLlamaSpit(this.field_147300_g, d, \u2603, \u2603, (double)sPacketSpawnObject2.func_149010_g() / 8000.0, (double)sPacketSpawnObject2.func_149004_h() / 8000.0, (double)sPacketSpawnObject2.func_148999_i() / 8000.0);
        } else if (sPacketSpawnObject2.func_148993_l() == 71) {
            \u26032 = new EntityItemFrame(this.field_147300_g, new BlockPos(d, \u2603, \u2603), EnumFacing.func_176731_b(sPacketSpawnObject2.func_149009_m()));
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 77) {
            \u26032 = new EntityLeashKnot(this.field_147300_g, new BlockPos(MathHelper.func_76128_c(d), MathHelper.func_76128_c(\u2603), MathHelper.func_76128_c(\u2603)));
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 65) {
            \u26032 = new EntityEnderPearl(this.field_147300_g, d, \u2603, \u2603);
        } else if (sPacketSpawnObject2.func_148993_l() == 72) {
            \u26032 = new EntityEnderEye(this.field_147300_g, d, \u2603, \u2603);
        } else if (sPacketSpawnObject2.func_148993_l() == 76) {
            \u26032 = new EntityFireworkRocket(this.field_147300_g, d, \u2603, \u2603, ItemStack.field_190927_a);
        } else if (sPacketSpawnObject2.func_148993_l() == 63) {
            \u26032 = new EntityLargeFireball(this.field_147300_g, d, \u2603, \u2603, (double)sPacketSpawnObject2.func_149010_g() / 8000.0, (double)sPacketSpawnObject2.func_149004_h() / 8000.0, (double)sPacketSpawnObject2.func_148999_i() / 8000.0);
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 93) {
            \u26032 = new EntityDragonFireball(this.field_147300_g, d, \u2603, \u2603, (double)sPacketSpawnObject2.func_149010_g() / 8000.0, (double)sPacketSpawnObject2.func_149004_h() / 8000.0, (double)sPacketSpawnObject2.func_148999_i() / 8000.0);
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 64) {
            \u26032 = new EntitySmallFireball(this.field_147300_g, d, \u2603, \u2603, (double)sPacketSpawnObject2.func_149010_g() / 8000.0, (double)sPacketSpawnObject2.func_149004_h() / 8000.0, (double)sPacketSpawnObject2.func_148999_i() / 8000.0);
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 66) {
            \u26032 = new EntityWitherSkull(this.field_147300_g, d, \u2603, \u2603, (double)sPacketSpawnObject2.func_149010_g() / 8000.0, (double)sPacketSpawnObject2.func_149004_h() / 8000.0, (double)sPacketSpawnObject2.func_148999_i() / 8000.0);
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 67) {
            \u26032 = new EntityShulkerBullet(this.field_147300_g, d, \u2603, \u2603, (double)sPacketSpawnObject2.func_149010_g() / 8000.0, (double)sPacketSpawnObject2.func_149004_h() / 8000.0, (double)sPacketSpawnObject2.func_148999_i() / 8000.0);
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 62) {
            \u26032 = new EntityEgg(this.field_147300_g, d, \u2603, \u2603);
        } else if (sPacketSpawnObject2.func_148993_l() == 79) {
            \u26032 = new EntityEvokerFangs(this.field_147300_g, d, \u2603, \u2603, 0.0f, 0, null);
        } else if (sPacketSpawnObject2.func_148993_l() == 73) {
            \u26032 = new EntityPotion(this.field_147300_g, d, \u2603, \u2603, ItemStack.field_190927_a);
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 75) {
            \u26032 = new EntityExpBottle(this.field_147300_g, d, \u2603, \u2603);
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 1) {
            \u26032 = new EntityBoat(this.field_147300_g, d, \u2603, \u2603);
        } else if (sPacketSpawnObject2.func_148993_l() == 50) {
            \u26032 = new EntityTNTPrimed(this.field_147300_g, d, \u2603, \u2603, null);
        } else if (sPacketSpawnObject2.func_148993_l() == 78) {
            \u26032 = new EntityArmorStand(this.field_147300_g, d, \u2603, \u2603);
        } else if (sPacketSpawnObject2.func_148993_l() == 51) {
            \u26032 = new EntityEnderCrystal(this.field_147300_g, d, \u2603, \u2603);
        } else if (sPacketSpawnObject2.func_148993_l() == 2) {
            \u26032 = new EntityItem(this.field_147300_g, d, \u2603, \u2603);
        } else if (sPacketSpawnObject2.func_148993_l() == 70) {
            \u26032 = new EntityFallingBlock(this.field_147300_g, d, \u2603, \u2603, Block.func_176220_d(sPacketSpawnObject2.func_149009_m() & 0xFFFF));
            sPacketSpawnObject2.func_149002_g(0);
        } else if (sPacketSpawnObject2.func_148993_l() == 3) {
            \u26032 = new EntityAreaEffectCloud(this.field_147300_g, d, \u2603, \u2603);
        }
        if (\u26032 != null) {
            EntityTracker.func_187254_a(\u26032, d, \u2603, \u2603);
            \u26032.field_70125_A = (float)(sPacketSpawnObject2.func_149008_j() * 360) / 256.0f;
            \u26032.field_70177_z = (float)(sPacketSpawnObject2.func_149006_k() * 360) / 256.0f;
            entityArray = \u26032.func_70021_al();
            if (entityArray != null) {
                int n = sPacketSpawnObject2.func_149001_c() - \u26032.func_145782_y();
                for (Entity entity : entityArray) {
                    entity.func_145769_d(entity.func_145782_y() + n);
                }
            }
            \u26032.func_145769_d(sPacketSpawnObject2.func_149001_c());
            \u26032.func_184221_a(sPacketSpawnObject2.func_186879_b());
            this.field_147300_g.func_73027_a(sPacketSpawnObject2.func_149001_c(), \u26032);
            if (sPacketSpawnObject2.func_149009_m() > 0) {
                if ((sPacketSpawnObject2.func_148993_l() == 60 || sPacketSpawnObject2.func_148993_l() == 91) && (\u2603 = this.field_147300_g.func_73045_a(sPacketSpawnObject2.func_149009_m() - 1)) instanceof EntityLivingBase && \u26032 instanceof EntityArrow) {
                    ((EntityArrow)\u26032).field_70250_c = \u2603;
                }
                \u26032.func_70016_h((double)sPacketSpawnObject2.func_149010_g() / 8000.0, (double)sPacketSpawnObject2.func_149004_h() / 8000.0, (double)sPacketSpawnObject2.func_148999_i() / 8000.0);
            }
        }
    }

    @Override
    public void func_147286_a(SPacketSpawnExperienceOrb sPacketSpawnExperienceOrb) {
        PacketThreadUtil.func_180031_a(sPacketSpawnExperienceOrb, this, this.field_147299_f);
        double d = sPacketSpawnExperienceOrb.func_186885_b();
        \u2603 = sPacketSpawnExperienceOrb.func_186886_c();
        \u2603 = sPacketSpawnExperienceOrb.func_186884_d();
        EntityXPOrb \u26032 = new EntityXPOrb(this.field_147300_g, d, \u2603, \u2603, sPacketSpawnExperienceOrb.func_148986_g());
        EntityTracker.func_187254_a(\u26032, d, \u2603, \u2603);
        \u26032.field_70177_z = 0.0f;
        \u26032.field_70125_A = 0.0f;
        \u26032.func_145769_d(sPacketSpawnExperienceOrb.func_148985_c());
        this.field_147300_g.func_73027_a(sPacketSpawnExperienceOrb.func_148985_c(), \u26032);
    }

    @Override
    public void func_147292_a(SPacketSpawnGlobalEntity sPacketSpawnGlobalEntity) {
        PacketThreadUtil.func_180031_a(sPacketSpawnGlobalEntity, this, this.field_147299_f);
        double d = sPacketSpawnGlobalEntity.func_186888_b();
        \u2603 = sPacketSpawnGlobalEntity.func_186889_c();
        \u2603 = sPacketSpawnGlobalEntity.func_186887_d();
        EntityLightningBolt \u26032 = null;
        if (sPacketSpawnGlobalEntity.func_149053_g() == 1) {
            \u26032 = new EntityLightningBolt(this.field_147300_g, d, \u2603, \u2603, false);
        }
        if (\u26032 != null) {
            EntityTracker.func_187254_a(\u26032, d, \u2603, \u2603);
            \u26032.field_70177_z = 0.0f;
            \u26032.field_70125_A = 0.0f;
            \u26032.func_145769_d(sPacketSpawnGlobalEntity.func_149052_c());
            this.field_147300_g.func_72942_c(\u26032);
        }
    }

    @Override
    public void func_147288_a(SPacketSpawnPainting sPacketSpawnPainting) {
        PacketThreadUtil.func_180031_a(sPacketSpawnPainting, this, this.field_147299_f);
        EntityPainting entityPainting = new EntityPainting(this.field_147300_g, sPacketSpawnPainting.func_179837_b(), sPacketSpawnPainting.func_179836_c(), sPacketSpawnPainting.func_148961_h());
        entityPainting.func_184221_a(sPacketSpawnPainting.func_186895_b());
        this.field_147300_g.func_73027_a(sPacketSpawnPainting.func_148965_c(), entityPainting);
    }

    @Override
    public void func_147244_a(SPacketEntityVelocity sPacketEntityVelocity) {
        PacketThreadUtil.func_180031_a(sPacketEntityVelocity, this, this.field_147299_f);
        Entity entity = this.field_147300_g.func_73045_a(sPacketEntityVelocity.func_149412_c());
        if (entity == null) {
            return;
        }
        entity.func_70016_h((double)sPacketEntityVelocity.func_149411_d() / 8000.0, (double)sPacketEntityVelocity.func_149410_e() / 8000.0, (double)sPacketEntityVelocity.func_149409_f() / 8000.0);
    }

    @Override
    public void func_147284_a(SPacketEntityMetadata sPacketEntityMetadata) {
        PacketThreadUtil.func_180031_a(sPacketEntityMetadata, this, this.field_147299_f);
        Entity entity = this.field_147300_g.func_73045_a(sPacketEntityMetadata.func_149375_d());
        if (entity != null && sPacketEntityMetadata.func_149376_c() != null) {
            entity.func_184212_Q().func_187218_a(sPacketEntityMetadata.func_149376_c());
        }
    }

    @Override
    public void func_147237_a(SPacketSpawnPlayer sPacketSpawnPlayer) {
        PacketThreadUtil.func_180031_a(sPacketSpawnPlayer, this, this.field_147299_f);
        double d = sPacketSpawnPlayer.func_186898_d();
        \u2603 = sPacketSpawnPlayer.func_186897_e();
        \u2603 = sPacketSpawnPlayer.func_186899_f();
        float \u26032 = (float)(sPacketSpawnPlayer.func_148941_i() * 360) / 256.0f;
        float \u26033 = (float)(sPacketSpawnPlayer.func_148945_j() * 360) / 256.0f;
        EntityOtherPlayerMP \u26034 = new EntityOtherPlayerMP(this.field_147299_f.field_71441_e, this.func_175102_a(sPacketSpawnPlayer.func_179819_c()).func_178845_a());
        \u26034.field_70169_q = d;
        \u26034.field_70142_S = d;
        \u26034.field_70167_r = \u2603;
        \u26034.field_70137_T = \u2603;
        \u26034.field_70166_s = \u2603;
        \u26034.field_70136_U = \u2603;
        EntityTracker.func_187254_a(\u26034, d, \u2603, \u2603);
        \u26034.func_70080_a(d, \u2603, \u2603, \u26032, \u26033);
        this.field_147300_g.func_73027_a(sPacketSpawnPlayer.func_148943_d(), \u26034);
        List<EntityDataManager.DataEntry<?>> \u26035 = sPacketSpawnPlayer.func_148944_c();
        if (\u26035 != null) {
            \u26034.func_184212_Q().func_187218_a(\u26035);
        }
    }

    @Override
    public void func_147275_a(SPacketEntityTeleport sPacketEntityTeleport) {
        PacketThreadUtil.func_180031_a(sPacketEntityTeleport, this, this.field_147299_f);
        Entity entity = this.field_147300_g.func_73045_a(sPacketEntityTeleport.func_149451_c());
        if (entity == null) {
            return;
        }
        double \u26032 = sPacketEntityTeleport.func_186982_b();
        double \u26033 = sPacketEntityTeleport.func_186983_c();
        double \u26034 = sPacketEntityTeleport.func_186981_d();
        EntityTracker.func_187254_a(entity, \u26032, \u26033, \u26034);
        if (!entity.func_184186_bw()) {
            float f = (float)(sPacketEntityTeleport.func_149450_g() * 360) / 256.0f;
            \u2603 = (float)(sPacketEntityTeleport.func_149447_h() * 360) / 256.0f;
            if (Math.abs(entity.field_70165_t - \u26032) >= 0.03125 || Math.abs(entity.field_70163_u - \u26033) >= 0.015625 || Math.abs(entity.field_70161_v - \u26034) >= 0.03125) {
                entity.func_180426_a(\u26032, \u26033, \u26034, f, \u2603, 3, true);
            } else {
                entity.func_180426_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, f, \u2603, 0, true);
            }
            entity.field_70122_E = sPacketEntityTeleport.func_179697_g();
        }
    }

    @Override
    public void func_147257_a(SPacketHeldItemChange sPacketHeldItemChange) {
        PacketThreadUtil.func_180031_a(sPacketHeldItemChange, this, this.field_147299_f);
        if (InventoryPlayer.func_184435_e(sPacketHeldItemChange.func_149385_c())) {
            this.field_147299_f.field_71439_g.field_71071_by.field_70461_c = sPacketHeldItemChange.func_149385_c();
        }
    }

    @Override
    public void func_147259_a(SPacketEntity sPacketEntity) {
        PacketThreadUtil.func_180031_a(sPacketEntity, this, this.field_147299_f);
        Entity entity = sPacketEntity.func_149065_a(this.field_147300_g);
        if (entity == null) {
            return;
        }
        entity.field_70118_ct += (long)sPacketEntity.func_186952_a();
        entity.field_70117_cu += (long)sPacketEntity.func_186953_b();
        entity.field_70116_cv += (long)sPacketEntity.func_186951_c();
        double \u26032 = (double)entity.field_70118_ct / 4096.0;
        double \u26033 = (double)entity.field_70117_cu / 4096.0;
        double \u26034 = (double)entity.field_70116_cv / 4096.0;
        if (!entity.func_184186_bw()) {
            float f = sPacketEntity.func_149060_h() ? (float)(sPacketEntity.func_149066_f() * 360) / 256.0f : entity.field_70177_z;
            \u2603 = sPacketEntity.func_149060_h() ? (float)(sPacketEntity.func_149063_g() * 360) / 256.0f : entity.field_70125_A;
            entity.func_180426_a(\u26032, \u26033, \u26034, f, \u2603, 3, false);
            entity.field_70122_E = sPacketEntity.func_179742_g();
        }
    }

    @Override
    public void func_147267_a(SPacketEntityHeadLook sPacketEntityHeadLook) {
        PacketThreadUtil.func_180031_a(sPacketEntityHeadLook, this, this.field_147299_f);
        Entity entity = sPacketEntityHeadLook.func_149381_a(this.field_147300_g);
        if (entity == null) {
            return;
        }
        float \u26032 = (float)(sPacketEntityHeadLook.func_149380_c() * 360) / 256.0f;
        entity.func_70034_d(\u26032);
    }

    @Override
    public void func_147238_a(SPacketDestroyEntities sPacketDestroyEntities) {
        PacketThreadUtil.func_180031_a(sPacketDestroyEntities, this, this.field_147299_f);
        for (int i = 0; i < sPacketDestroyEntities.func_149098_c().length; ++i) {
            this.field_147300_g.func_73028_b(sPacketDestroyEntities.func_149098_c()[i]);
        }
    }

    @Override
    public void func_184330_a(SPacketPlayerPosLook sPacketPlayerPosLook) {
        PacketThreadUtil.func_180031_a(sPacketPlayerPosLook, this, this.field_147299_f);
        EntityPlayerSP entityPlayerSP = this.field_147299_f.field_71439_g;
        double \u26032 = sPacketPlayerPosLook.func_148932_c();
        double \u26033 = sPacketPlayerPosLook.func_148928_d();
        double \u26034 = sPacketPlayerPosLook.func_148933_e();
        float \u26035 = sPacketPlayerPosLook.func_148931_f();
        float \u26036 = sPacketPlayerPosLook.func_148930_g();
        if (sPacketPlayerPosLook.func_179834_f().contains((Object)SPacketPlayerPosLook.EnumFlags.X)) {
            \u26032 += entityPlayerSP.field_70165_t;
        } else {
            entityPlayerSP.field_70159_w = 0.0;
        }
        if (sPacketPlayerPosLook.func_179834_f().contains((Object)SPacketPlayerPosLook.EnumFlags.Y)) {
            \u26033 += entityPlayerSP.field_70163_u;
        } else {
            entityPlayerSP.field_70181_x = 0.0;
        }
        if (sPacketPlayerPosLook.func_179834_f().contains((Object)SPacketPlayerPosLook.EnumFlags.Z)) {
            \u26034 += entityPlayerSP.field_70161_v;
        } else {
            entityPlayerSP.field_70179_y = 0.0;
        }
        if (sPacketPlayerPosLook.func_179834_f().contains((Object)SPacketPlayerPosLook.EnumFlags.X_ROT)) {
            \u26036 += entityPlayerSP.field_70125_A;
        }
        if (sPacketPlayerPosLook.func_179834_f().contains((Object)SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
            \u26035 += entityPlayerSP.field_70177_z;
        }
        entityPlayerSP.func_70080_a(\u26032, \u26033, \u26034, \u26035, \u26036);
        this.field_147302_e.func_179290_a(new CPacketConfirmTeleport(sPacketPlayerPosLook.func_186965_f()));
        this.field_147302_e.func_179290_a(new CPacketPlayer.PositionRotation(entityPlayerSP.field_70165_t, entityPlayerSP.func_174813_aQ().field_72338_b, entityPlayerSP.field_70161_v, entityPlayerSP.field_70177_z, entityPlayerSP.field_70125_A, false));
        if (!this.field_147309_h) {
            this.field_147299_f.field_71439_g.field_70169_q = this.field_147299_f.field_71439_g.field_70165_t;
            this.field_147299_f.field_71439_g.field_70167_r = this.field_147299_f.field_71439_g.field_70163_u;
            this.field_147299_f.field_71439_g.field_70166_s = this.field_147299_f.field_71439_g.field_70161_v;
            this.field_147309_h = true;
            this.field_147299_f.func_147108_a(null);
        }
    }

    @Override
    public void func_147287_a(SPacketMultiBlockChange sPacketMultiBlockChange) {
        PacketThreadUtil.func_180031_a(sPacketMultiBlockChange, this, this.field_147299_f);
        for (SPacketMultiBlockChange.BlockUpdateData blockUpdateData : sPacketMultiBlockChange.func_179844_a()) {
            this.field_147300_g.func_180503_b(blockUpdateData.func_180090_a(), blockUpdateData.func_180088_c());
        }
    }

    @Override
    public void func_147263_a(SPacketChunkData sPacketChunkData) {
        PacketThreadUtil.func_180031_a(sPacketChunkData, this, this.field_147299_f);
        if (sPacketChunkData.func_149274_i()) {
            this.field_147300_g.func_73025_a(sPacketChunkData.func_149273_e(), sPacketChunkData.func_149271_f(), true);
        }
        this.field_147300_g.func_73031_a(sPacketChunkData.func_149273_e() << 4, 0, sPacketChunkData.func_149271_f() << 4, (sPacketChunkData.func_149273_e() << 4) + 15, 256, (sPacketChunkData.func_149271_f() << 4) + 15);
        Chunk chunk = this.field_147300_g.func_72964_e(sPacketChunkData.func_149273_e(), sPacketChunkData.func_149271_f());
        chunk.func_186033_a(sPacketChunkData.func_186946_a(), sPacketChunkData.func_149276_g(), sPacketChunkData.func_149274_i());
        this.field_147300_g.func_147458_c(sPacketChunkData.func_149273_e() << 4, 0, sPacketChunkData.func_149271_f() << 4, (sPacketChunkData.func_149273_e() << 4) + 15, 256, (sPacketChunkData.func_149271_f() << 4) + 15);
        if (!sPacketChunkData.func_149274_i() || !(this.field_147300_g.field_73011_w instanceof WorldProviderSurface)) {
            chunk.func_76613_n();
        }
        for (NBTTagCompound nBTTagCompound : sPacketChunkData.func_189554_f()) {
            BlockPos blockPos = new BlockPos(nBTTagCompound.func_74762_e("x"), nBTTagCompound.func_74762_e("y"), nBTTagCompound.func_74762_e("z"));
            TileEntity \u26032 = this.field_147300_g.func_175625_s(blockPos);
            if (\u26032 == null) continue;
            \u26032.func_145839_a(nBTTagCompound);
        }
    }

    @Override
    public void func_184326_a(SPacketUnloadChunk sPacketUnloadChunk) {
        PacketThreadUtil.func_180031_a(sPacketUnloadChunk, this, this.field_147299_f);
        this.field_147300_g.func_73025_a(sPacketUnloadChunk.func_186940_a(), sPacketUnloadChunk.func_186941_b(), false);
    }

    @Override
    public void func_147234_a(SPacketBlockChange sPacketBlockChange) {
        PacketThreadUtil.func_180031_a(sPacketBlockChange, this, this.field_147299_f);
        this.field_147300_g.func_180503_b(sPacketBlockChange.func_179827_b(), sPacketBlockChange.func_180728_a());
    }

    @Override
    public void func_147253_a(SPacketDisconnect sPacketDisconnect) {
        this.field_147302_e.func_150718_a(sPacketDisconnect.func_149165_c());
    }

    @Override
    public void func_147231_a(ITextComponent iTextComponent) {
        this.field_147299_f.func_71403_a(null);
        if (this.field_147307_j != null) {
            if (this.field_147307_j instanceof GuiScreenRealmsProxy) {
                this.field_147299_f.func_147108_a(new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.field_147307_j).func_154321_a(), "disconnect.lost", iTextComponent).getProxy());
            } else {
                this.field_147299_f.func_147108_a(new GuiDisconnected(this.field_147307_j, "disconnect.lost", iTextComponent));
            }
        } else {
            this.field_147299_f.func_147108_a(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", iTextComponent));
        }
    }

    public void func_147297_a(Packet<?> packet) {
        this.field_147302_e.func_179290_a(packet);
    }

    @Override
    public void func_147246_a(SPacketCollectItem sPacketCollectItem) {
        PacketThreadUtil.func_180031_a(sPacketCollectItem, this, this.field_147299_f);
        Entity entity = this.field_147300_g.func_73045_a(sPacketCollectItem.func_149354_c());
        EntityLivingBase \u26032 = (EntityLivingBase)this.field_147300_g.func_73045_a(sPacketCollectItem.func_149353_d());
        if (\u26032 == null) {
            \u26032 = this.field_147299_f.field_71439_g;
        }
        if (entity != null) {
            if (entity instanceof EntityXPOrb) {
                this.field_147300_g.func_184134_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, SoundEvents.field_187604_bf, SoundCategory.PLAYERS, 0.1f, (this.field_147306_l.nextFloat() - this.field_147306_l.nextFloat()) * 0.35f + 0.9f, false);
            } else {
                this.field_147300_g.func_184134_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, SoundEvents.field_187638_cR, SoundCategory.PLAYERS, 0.2f, (this.field_147306_l.nextFloat() - this.field_147306_l.nextFloat()) * 1.4f + 2.0f, false);
            }
            if (entity instanceof EntityItem) {
                ((EntityItem)entity).func_92059_d().func_190920_e(sPacketCollectItem.func_191208_c());
            }
            this.field_147299_f.field_71452_i.func_78873_a(new ParticleItemPickup((World)this.field_147300_g, entity, \u26032, 0.5f));
            this.field_147300_g.func_73028_b(sPacketCollectItem.func_149354_c());
        }
    }

    @Override
    public void func_147251_a(SPacketChat sPacketChat) {
        PacketThreadUtil.func_180031_a(sPacketChat, this, this.field_147299_f);
        this.field_147299_f.field_71456_v.func_191742_a(sPacketChat.func_192590_c(), sPacketChat.func_148915_c());
    }

    @Override
    public void func_147279_a(SPacketAnimation sPacketAnimation2) {
        SPacketAnimation sPacketAnimation2;
        PacketThreadUtil.func_180031_a(sPacketAnimation2, this, this.field_147299_f);
        Entity entity = this.field_147300_g.func_73045_a(sPacketAnimation2.func_148978_c());
        if (entity == null) {
            return;
        }
        if (sPacketAnimation2.func_148977_d() == 0) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            entityLivingBase.func_184609_a(EnumHand.MAIN_HAND);
        } else if (sPacketAnimation2.func_148977_d() == 3) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            entityLivingBase.func_184609_a(EnumHand.OFF_HAND);
        } else if (sPacketAnimation2.func_148977_d() == 1) {
            entity.func_70057_ab();
        } else if (sPacketAnimation2.func_148977_d() == 2) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            entityPlayer.func_70999_a(false, false, false);
        } else if (sPacketAnimation2.func_148977_d() == 4) {
            this.field_147299_f.field_71452_i.func_178926_a(entity, EnumParticleTypes.CRIT);
        } else if (sPacketAnimation2.func_148977_d() == 5) {
            this.field_147299_f.field_71452_i.func_178926_a(entity, EnumParticleTypes.CRIT_MAGIC);
        }
    }

    @Override
    public void func_147278_a(SPacketUseBed sPacketUseBed) {
        PacketThreadUtil.func_180031_a(sPacketUseBed, this, this.field_147299_f);
        sPacketUseBed.func_149091_a(this.field_147300_g).func_180469_a(sPacketUseBed.func_179798_a());
    }

    @Override
    public void func_147281_a(SPacketSpawnMob sPacketSpawnMob2) {
        PacketThreadUtil.func_180031_a(sPacketSpawnMob2, this, this.field_147299_f);
        double d = sPacketSpawnMob2.func_186891_e();
        \u2603 = sPacketSpawnMob2.func_186892_f();
        \u2603 = sPacketSpawnMob2.func_186893_g();
        float \u26032 = (float)(sPacketSpawnMob2.func_149028_l() * 360) / 256.0f;
        float \u26033 = (float)(sPacketSpawnMob2.func_149030_m() * 360) / 256.0f;
        EntityLivingBase \u26034 = (EntityLivingBase)EntityList.func_75616_a(sPacketSpawnMob2.func_149025_e(), this.field_147299_f.field_71441_e);
        if (\u26034 != null) {
            SPacketSpawnMob sPacketSpawnMob2;
            EntityTracker.func_187254_a(\u26034, d, \u2603, \u2603);
            \u26034.field_70761_aq = (float)(sPacketSpawnMob2.func_149032_n() * 360) / 256.0f;
            \u26034.field_70759_as = (float)(sPacketSpawnMob2.func_149032_n() * 360) / 256.0f;
            Entity[] entityArray = \u26034.func_70021_al();
            if (entityArray != null) {
                int n = sPacketSpawnMob2.func_149024_d() - \u26034.func_145782_y();
                for (Entity entity : entityArray) {
                    entity.func_145769_d(entity.func_145782_y() + n);
                }
            }
            \u26034.func_145769_d(sPacketSpawnMob2.func_149024_d());
            \u26034.func_184221_a(sPacketSpawnMob2.func_186890_c());
            \u26034.func_70080_a(d, \u2603, \u2603, \u26032, \u26033);
            \u26034.field_70159_w = (float)sPacketSpawnMob2.func_149026_i() / 8000.0f;
            \u26034.field_70181_x = (float)sPacketSpawnMob2.func_149033_j() / 8000.0f;
            \u26034.field_70179_y = (float)sPacketSpawnMob2.func_149031_k() / 8000.0f;
            this.field_147300_g.func_73027_a(sPacketSpawnMob2.func_149024_d(), \u26034);
            List<EntityDataManager.DataEntry<?>> \u26035 = sPacketSpawnMob2.func_149027_c();
            if (\u26035 != null) {
                \u26034.func_184212_Q().func_187218_a(\u26035);
            }
        } else {
            field_147301_d.warn("Skipping Entity with id {}", (Object)sPacketSpawnMob2.func_149025_e());
        }
    }

    @Override
    public void func_147285_a(SPacketTimeUpdate sPacketTimeUpdate) {
        PacketThreadUtil.func_180031_a(sPacketTimeUpdate, this, this.field_147299_f);
        this.field_147299_f.field_71441_e.func_82738_a(sPacketTimeUpdate.func_149366_c());
        this.field_147299_f.field_71441_e.func_72877_b(sPacketTimeUpdate.func_149365_d());
    }

    @Override
    public void func_147271_a(SPacketSpawnPosition sPacketSpawnPosition) {
        PacketThreadUtil.func_180031_a(sPacketSpawnPosition, this, this.field_147299_f);
        this.field_147299_f.field_71439_g.func_180473_a(sPacketSpawnPosition.func_179800_a(), true);
        this.field_147299_f.field_71441_e.func_72912_H().func_176143_a(sPacketSpawnPosition.func_179800_a());
    }

    @Override
    public void func_184328_a(SPacketSetPassengers sPacketSetPassengers) {
        PacketThreadUtil.func_180031_a(sPacketSetPassengers, this, this.field_147299_f);
        Entity entity = this.field_147300_g.func_73045_a(sPacketSetPassengers.func_186972_b());
        if (entity == null) {
            field_147301_d.warn("Received passengers for unknown entity");
            return;
        }
        boolean \u26032 = entity.func_184215_y(this.field_147299_f.field_71439_g);
        entity.func_184226_ay();
        for (int n : sPacketSetPassengers.func_186971_a()) {
            Entity entity2 = this.field_147300_g.func_73045_a(n);
            if (entity2 == null) continue;
            entity2.func_184205_a(entity, true);
            if (entity2 != this.field_147299_f.field_71439_g || \u26032) continue;
            this.field_147299_f.field_71456_v.func_110326_a(I18n.func_135052_a("mount.onboard", GameSettings.func_74298_c(this.field_147299_f.field_71474_y.field_74311_E.func_151463_i())), false);
        }
    }

    @Override
    public void func_147243_a(SPacketEntityAttach sPacketEntityAttach) {
        PacketThreadUtil.func_180031_a(sPacketEntityAttach, this, this.field_147299_f);
        Entity entity = this.field_147300_g.func_73045_a(sPacketEntityAttach.func_149403_d());
        \u2603 = this.field_147300_g.func_73045_a(sPacketEntityAttach.func_149402_e());
        if (entity instanceof EntityLiving) {
            if (\u2603 != null) {
                ((EntityLiving)entity).func_110162_b(\u2603, false);
            } else {
                ((EntityLiving)entity).func_110160_i(false, false);
            }
        }
    }

    @Override
    public void func_147236_a(SPacketEntityStatus sPacketEntityStatus) {
        PacketThreadUtil.func_180031_a(sPacketEntityStatus, this, this.field_147299_f);
        Entity entity = sPacketEntityStatus.func_149161_a(this.field_147300_g);
        if (entity != null) {
            if (sPacketEntityStatus.func_149160_c() == 21) {
                this.field_147299_f.func_147118_V().func_147682_a(new GuardianSound((EntityGuardian)entity));
            } else if (sPacketEntityStatus.func_149160_c() == 35) {
                int n = 40;
                this.field_147299_f.field_71452_i.func_191271_a(entity, EnumParticleTypes.TOTEM, 30);
                this.field_147300_g.func_184134_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, SoundEvents.field_191263_gW, entity.func_184176_by(), 1.0f, 1.0f, false);
                if (entity == this.field_147299_f.field_71439_g) {
                    this.field_147299_f.field_71460_t.func_190565_a(new ItemStack(Items.field_190929_cY));
                }
            } else {
                entity.func_70103_a(sPacketEntityStatus.func_149160_c());
            }
        }
    }

    @Override
    public void func_147249_a(SPacketUpdateHealth sPacketUpdateHealth) {
        PacketThreadUtil.func_180031_a(sPacketUpdateHealth, this, this.field_147299_f);
        this.field_147299_f.field_71439_g.func_71150_b(sPacketUpdateHealth.func_149332_c());
        this.field_147299_f.field_71439_g.func_71024_bL().func_75114_a(sPacketUpdateHealth.func_149330_d());
        this.field_147299_f.field_71439_g.func_71024_bL().func_75119_b(sPacketUpdateHealth.func_149331_e());
    }

    @Override
    public void func_147295_a(SPacketSetExperience sPacketSetExperience) {
        PacketThreadUtil.func_180031_a(sPacketSetExperience, this, this.field_147299_f);
        this.field_147299_f.field_71439_g.func_71152_a(sPacketSetExperience.func_149397_c(), sPacketSetExperience.func_149396_d(), sPacketSetExperience.func_149395_e());
    }

    @Override
    public void func_147280_a(SPacketRespawn sPacketRespawn2) {
        SPacketRespawn sPacketRespawn2;
        PacketThreadUtil.func_180031_a(sPacketRespawn2, this, this.field_147299_f);
        if (sPacketRespawn2.func_149082_c() != this.field_147299_f.field_71439_g.field_71093_bK) {
            this.field_147309_h = false;
            Scoreboard scoreboard = this.field_147300_g.func_96441_U();
            this.field_147300_g = new WorldClient(this, new WorldSettings(0L, sPacketRespawn2.func_149083_e(), false, this.field_147299_f.field_71441_e.func_72912_H().func_76093_s(), sPacketRespawn2.func_149080_f()), sPacketRespawn2.func_149082_c(), sPacketRespawn2.func_149081_d(), this.field_147299_f.field_71424_I);
            this.field_147300_g.func_96443_a(scoreboard);
            this.field_147299_f.func_71403_a(this.field_147300_g);
            this.field_147299_f.field_71439_g.field_71093_bK = sPacketRespawn2.func_149082_c();
            this.field_147299_f.func_147108_a(new GuiDownloadTerrain());
        }
        this.field_147299_f.func_71354_a(sPacketRespawn2.func_149082_c());
        this.field_147299_f.field_71442_b.func_78746_a(sPacketRespawn2.func_149083_e());
    }

    @Override
    public void func_147283_a(SPacketExplosion sPacketExplosion) {
        PacketThreadUtil.func_180031_a(sPacketExplosion, this, this.field_147299_f);
        Explosion explosion = new Explosion(this.field_147299_f.field_71441_e, null, sPacketExplosion.func_149148_f(), sPacketExplosion.func_149143_g(), sPacketExplosion.func_149145_h(), sPacketExplosion.func_149146_i(), sPacketExplosion.func_149150_j());
        explosion.func_77279_a(true);
        this.field_147299_f.field_71439_g.field_70159_w += (double)sPacketExplosion.func_149149_c();
        this.field_147299_f.field_71439_g.field_70181_x += (double)sPacketExplosion.func_149144_d();
        this.field_147299_f.field_71439_g.field_70179_y += (double)sPacketExplosion.func_149147_e();
    }

    @Override
    public void func_147265_a(SPacketOpenWindow sPacketOpenWindow2) {
        SPacketOpenWindow sPacketOpenWindow2;
        PacketThreadUtil.func_180031_a(sPacketOpenWindow2, this, this.field_147299_f);
        EntityPlayerSP entityPlayerSP = this.field_147299_f.field_71439_g;
        if ("minecraft:container".equals(sPacketOpenWindow2.func_148902_e())) {
            entityPlayerSP.func_71007_a(new InventoryBasic(sPacketOpenWindow2.func_179840_c(), sPacketOpenWindow2.func_148898_f()));
            entityPlayerSP.field_71070_bA.field_75152_c = sPacketOpenWindow2.func_148901_c();
        } else if ("minecraft:villager".equals(sPacketOpenWindow2.func_148902_e())) {
            entityPlayerSP.func_180472_a(new NpcMerchant(entityPlayerSP, sPacketOpenWindow2.func_179840_c()));
            entityPlayerSP.field_71070_bA.field_75152_c = sPacketOpenWindow2.func_148901_c();
        } else if ("EntityHorse".equals(sPacketOpenWindow2.func_148902_e())) {
            Entity entity = this.field_147300_g.func_73045_a(sPacketOpenWindow2.func_148897_h());
            if (entity instanceof AbstractHorse) {
                entityPlayerSP.func_184826_a((AbstractHorse)entity, new ContainerHorseChest(sPacketOpenWindow2.func_179840_c(), sPacketOpenWindow2.func_148898_f()));
                entityPlayerSP.field_71070_bA.field_75152_c = sPacketOpenWindow2.func_148901_c();
            }
        } else if (!sPacketOpenWindow2.func_148900_g()) {
            entityPlayerSP.func_180468_a(new LocalBlockIntercommunication(sPacketOpenWindow2.func_148902_e(), sPacketOpenWindow2.func_179840_c()));
            entityPlayerSP.field_71070_bA.field_75152_c = sPacketOpenWindow2.func_148901_c();
        } else {
            ContainerLocalMenu containerLocalMenu = new ContainerLocalMenu(sPacketOpenWindow2.func_148902_e(), sPacketOpenWindow2.func_179840_c(), sPacketOpenWindow2.func_148898_f());
            entityPlayerSP.func_71007_a(containerLocalMenu);
            entityPlayerSP.field_71070_bA.field_75152_c = sPacketOpenWindow2.func_148901_c();
        }
    }

    @Override
    public void func_147266_a(SPacketSetSlot sPacketSetSlot2) {
        PacketThreadUtil.func_180031_a(sPacketSetSlot2, this, this.field_147299_f);
        EntityPlayerSP entityPlayerSP = this.field_147299_f.field_71439_g;
        ItemStack \u26032 = sPacketSetSlot2.func_149174_e();
        int \u26033 = sPacketSetSlot2.func_149173_d();
        this.field_147299_f.func_193032_ao().func_193301_a(\u26032);
        if (sPacketSetSlot2.func_149175_c() == -1) {
            entityPlayerSP.field_71071_by.func_70437_b(\u26032);
        } else if (sPacketSetSlot2.func_149175_c() == -2) {
            entityPlayerSP.field_71071_by.func_70299_a(\u26033, \u26032);
        } else {
            SPacketSetSlot sPacketSetSlot2;
            boolean bl = false;
            if (this.field_147299_f.field_71462_r instanceof GuiContainerCreative) {
                Object object = (GuiContainerCreative)this.field_147299_f.field_71462_r;
                boolean bl2 = bl = ((GuiContainerCreative)object).func_147056_g() != CreativeTabs.field_78036_m.func_78021_a();
            }
            if (sPacketSetSlot2.func_149175_c() == 0 && sPacketSetSlot2.func_149173_d() >= 36 && \u26033 < 45) {
                if (!\u26032.func_190926_b() && (((ItemStack)(object = entityPlayerSP.field_71069_bz.func_75139_a(\u26033).func_75211_c())).func_190926_b() || ((ItemStack)object).func_190916_E() < \u26032.func_190916_E())) {
                    \u26032.func_190915_d(5);
                }
                entityPlayerSP.field_71069_bz.func_75141_a(\u26033, \u26032);
            } else if (!(sPacketSetSlot2.func_149175_c() != entityPlayerSP.field_71070_bA.field_75152_c || sPacketSetSlot2.func_149175_c() == 0 && bl)) {
                entityPlayerSP.field_71070_bA.func_75141_a(\u26033, \u26032);
            }
        }
    }

    @Override
    public void func_147239_a(SPacketConfirmTransaction sPacketConfirmTransaction) {
        PacketThreadUtil.func_180031_a(sPacketConfirmTransaction, this, this.field_147299_f);
        Container container = null;
        EntityPlayerSP \u26032 = this.field_147299_f.field_71439_g;
        if (sPacketConfirmTransaction.func_148889_c() == 0) {
            container = \u26032.field_71069_bz;
        } else if (sPacketConfirmTransaction.func_148889_c() == \u26032.field_71070_bA.field_75152_c) {
            container = \u26032.field_71070_bA;
        }
        if (container != null && !sPacketConfirmTransaction.func_148888_e()) {
            this.func_147297_a(new CPacketConfirmTransaction(sPacketConfirmTransaction.func_148889_c(), sPacketConfirmTransaction.func_148890_d(), true));
        }
    }

    @Override
    public void func_147241_a(SPacketWindowItems sPacketWindowItems) {
        PacketThreadUtil.func_180031_a(sPacketWindowItems, this, this.field_147299_f);
        EntityPlayerSP entityPlayerSP = this.field_147299_f.field_71439_g;
        if (sPacketWindowItems.func_148911_c() == 0) {
            entityPlayerSP.field_71069_bz.func_190896_a(sPacketWindowItems.func_148910_d());
        } else if (sPacketWindowItems.func_148911_c() == entityPlayerSP.field_71070_bA.field_75152_c) {
            entityPlayerSP.field_71070_bA.func_190896_a(sPacketWindowItems.func_148910_d());
        }
    }

    @Override
    public void func_147268_a(SPacketSignEditorOpen sPacketSignEditorOpen) {
        PacketThreadUtil.func_180031_a(sPacketSignEditorOpen, this, this.field_147299_f);
        TileEntity tileEntity = this.field_147300_g.func_175625_s(sPacketSignEditorOpen.func_179777_a());
        if (!(tileEntity instanceof TileEntitySign)) {
            tileEntity = new TileEntitySign();
            tileEntity.func_145834_a(this.field_147300_g);
            tileEntity.func_174878_a(sPacketSignEditorOpen.func_179777_a());
        }
        this.field_147299_f.field_71439_g.func_175141_a((TileEntitySign)tileEntity);
    }

    @Override
    public void func_147273_a(SPacketUpdateTileEntity sPacketUpdateTileEntity) {
        PacketThreadUtil.func_180031_a(sPacketUpdateTileEntity, this, this.field_147299_f);
        if (this.field_147299_f.field_71441_e.func_175667_e(sPacketUpdateTileEntity.func_179823_a())) {
            TileEntity tileEntity = this.field_147299_f.field_71441_e.func_175625_s(sPacketUpdateTileEntity.func_179823_a());
            int \u26032 = sPacketUpdateTileEntity.func_148853_f();
            boolean bl = \u2603 = \u26032 == 2 && tileEntity instanceof TileEntityCommandBlock;
            if (\u26032 == 1 && tileEntity instanceof TileEntityMobSpawner || \u2603 || \u26032 == 3 && tileEntity instanceof TileEntityBeacon || \u26032 == 4 && tileEntity instanceof TileEntitySkull || \u26032 == 5 && tileEntity instanceof TileEntityFlowerPot || \u26032 == 6 && tileEntity instanceof TileEntityBanner || \u26032 == 7 && tileEntity instanceof TileEntityStructure || \u26032 == 8 && tileEntity instanceof TileEntityEndGateway || \u26032 == 9 && tileEntity instanceof TileEntitySign || \u26032 == 10 && tileEntity instanceof TileEntityShulkerBox || \u26032 == 11 && tileEntity instanceof TileEntityBed) {
                tileEntity.func_145839_a(sPacketUpdateTileEntity.func_148857_g());
            }
            if (\u2603 && this.field_147299_f.field_71462_r instanceof GuiCommandBlock) {
                ((GuiCommandBlock)this.field_147299_f.field_71462_r).func_184075_a();
            }
        }
    }

    @Override
    public void func_147245_a(SPacketWindowProperty sPacketWindowProperty) {
        PacketThreadUtil.func_180031_a(sPacketWindowProperty, this, this.field_147299_f);
        EntityPlayerSP entityPlayerSP = this.field_147299_f.field_71439_g;
        if (entityPlayerSP.field_71070_bA != null && entityPlayerSP.field_71070_bA.field_75152_c == sPacketWindowProperty.func_149182_c()) {
            entityPlayerSP.field_71070_bA.func_75137_b(sPacketWindowProperty.func_149181_d(), sPacketWindowProperty.func_149180_e());
        }
    }

    @Override
    public void func_147242_a(SPacketEntityEquipment sPacketEntityEquipment) {
        PacketThreadUtil.func_180031_a(sPacketEntityEquipment, this, this.field_147299_f);
        Entity entity = this.field_147300_g.func_73045_a(sPacketEntityEquipment.func_149389_d());
        if (entity != null) {
            entity.func_184201_a(sPacketEntityEquipment.func_186969_c(), sPacketEntityEquipment.func_149390_c());
        }
    }

    @Override
    public void func_147276_a(SPacketCloseWindow sPacketCloseWindow) {
        PacketThreadUtil.func_180031_a(sPacketCloseWindow, this, this.field_147299_f);
        this.field_147299_f.field_71439_g.func_175159_q();
    }

    @Override
    public void func_147261_a(SPacketBlockAction sPacketBlockAction) {
        PacketThreadUtil.func_180031_a(sPacketBlockAction, this, this.field_147299_f);
        this.field_147299_f.field_71441_e.func_175641_c(sPacketBlockAction.func_179825_a(), sPacketBlockAction.func_148868_c(), sPacketBlockAction.func_148869_g(), sPacketBlockAction.func_148864_h());
    }

    @Override
    public void func_147294_a(SPacketBlockBreakAnim sPacketBlockBreakAnim) {
        PacketThreadUtil.func_180031_a(sPacketBlockBreakAnim, this, this.field_147299_f);
        this.field_147299_f.field_71441_e.func_175715_c(sPacketBlockBreakAnim.func_148845_c(), sPacketBlockBreakAnim.func_179821_b(), sPacketBlockBreakAnim.func_148846_g());
    }

    @Override
    public void func_147252_a(SPacketChangeGameState sPacketChangeGameState) {
        PacketThreadUtil.func_180031_a(sPacketChangeGameState, this, this.field_147299_f);
        EntityPlayerSP entityPlayerSP = this.field_147299_f.field_71439_g;
        int \u26032 = sPacketChangeGameState.func_149138_c();
        float \u26033 = sPacketChangeGameState.func_149137_d();
        int \u26034 = MathHelper.func_76141_d(\u26033 + 0.5f);
        if (\u26032 >= 0 && \u26032 < SPacketChangeGameState.field_149142_a.length && SPacketChangeGameState.field_149142_a[\u26032] != null) {
            ((EntityPlayer)entityPlayerSP).func_146105_b(new TextComponentTranslation(SPacketChangeGameState.field_149142_a[\u26032], new Object[0]), false);
        }
        if (\u26032 == 1) {
            this.field_147300_g.func_72912_H().func_76084_b(true);
            this.field_147300_g.func_72894_k(0.0f);
        } else if (\u26032 == 2) {
            this.field_147300_g.func_72912_H().func_76084_b(false);
            this.field_147300_g.func_72894_k(1.0f);
        } else if (\u26032 == 3) {
            this.field_147299_f.field_71442_b.func_78746_a(GameType.func_77146_a(\u26034));
        } else if (\u26032 == 4) {
            if (\u26034 == 0) {
                this.field_147299_f.field_71439_g.field_71174_a.func_147297_a(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
                this.field_147299_f.func_147108_a(new GuiDownloadTerrain());
            } else if (\u26034 == 1) {
                this.field_147299_f.func_147108_a(new GuiWinGame(true, () -> this.field_147299_f.field_71439_g.field_71174_a.func_147297_a(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN))));
            }
        } else if (\u26032 == 5) {
            GameSettings gameSettings = this.field_147299_f.field_71474_y;
            if (\u26033 == 0.0f) {
                this.field_147299_f.func_147108_a(new GuiScreenDemo());
            } else if (\u26033 == 101.0f) {
                this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(new TextComponentTranslation("demo.help.movement", GameSettings.func_74298_c(gameSettings.field_74351_w.func_151463_i()), GameSettings.func_74298_c(gameSettings.field_74370_x.func_151463_i()), GameSettings.func_74298_c(gameSettings.field_74368_y.func_151463_i()), GameSettings.func_74298_c(gameSettings.field_74366_z.func_151463_i())));
            } else if (\u26033 == 102.0f) {
                this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(new TextComponentTranslation("demo.help.jump", GameSettings.func_74298_c(gameSettings.field_74314_A.func_151463_i())));
            } else if (\u26033 == 103.0f) {
                this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(new TextComponentTranslation("demo.help.inventory", GameSettings.func_74298_c(gameSettings.field_151445_Q.func_151463_i())));
            }
        } else if (\u26032 == 6) {
            this.field_147300_g.func_184148_a(entityPlayerSP, entityPlayerSP.field_70165_t, entityPlayerSP.field_70163_u + (double)entityPlayerSP.func_70047_e(), entityPlayerSP.field_70161_v, SoundEvents.field_187734_u, SoundCategory.PLAYERS, 0.18f, 0.45f);
        } else if (\u26032 == 7) {
            this.field_147300_g.func_72894_k(\u26033);
        } else if (\u26032 == 8) {
            this.field_147300_g.func_147442_i(\u26033);
        } else if (\u26032 == 10) {
            this.field_147300_g.func_175688_a(EnumParticleTypes.MOB_APPEARANCE, entityPlayerSP.field_70165_t, entityPlayerSP.field_70163_u, entityPlayerSP.field_70161_v, 0.0, 0.0, 0.0, new int[0]);
            this.field_147300_g.func_184148_a(entityPlayerSP, entityPlayerSP.field_70165_t, entityPlayerSP.field_70163_u, entityPlayerSP.field_70161_v, SoundEvents.field_187514_aD, SoundCategory.HOSTILE, 1.0f, 1.0f);
        }
    }

    @Override
    public void func_147264_a(SPacketMaps sPacketMaps2) {
        SPacketMaps sPacketMaps2;
        PacketThreadUtil.func_180031_a(sPacketMaps2, this, this.field_147299_f);
        MapItemRenderer mapItemRenderer = this.field_147299_f.field_71460_t.func_147701_i();
        MapData \u26032 = ItemMap.func_150912_a(sPacketMaps2.func_149188_c(), this.field_147299_f.field_71441_e);
        if (\u26032 == null) {
            String string = "map_" + sPacketMaps2.func_149188_c();
            \u26032 = new MapData(string);
            if (mapItemRenderer.func_191205_a(string) != null && (\u2603 = mapItemRenderer.func_191207_a(mapItemRenderer.func_191205_a(string))) != null) {
                \u26032 = \u2603;
            }
            this.field_147299_f.field_71441_e.func_72823_a(string, \u26032);
        }
        sPacketMaps2.func_179734_a(\u26032);
        mapItemRenderer.func_148246_a(\u26032);
    }

    @Override
    public void func_147277_a(SPacketEffect sPacketEffect) {
        PacketThreadUtil.func_180031_a(sPacketEffect, this, this.field_147299_f);
        if (sPacketEffect.func_149244_c()) {
            this.field_147299_f.field_71441_e.func_175669_a(sPacketEffect.func_149242_d(), sPacketEffect.func_179746_d(), sPacketEffect.func_149241_e());
        } else {
            this.field_147299_f.field_71441_e.func_175718_b(sPacketEffect.func_149242_d(), sPacketEffect.func_179746_d(), sPacketEffect.func_149241_e());
        }
    }

    @Override
    public void func_191981_a(SPacketAdvancementInfo sPacketAdvancementInfo) {
        PacketThreadUtil.func_180031_a(sPacketAdvancementInfo, this, this.field_147299_f);
        this.field_191983_k.func_192799_a(sPacketAdvancementInfo);
    }

    @Override
    public void func_194022_a(SPacketSelectAdvancementsTab sPacketSelectAdvancementsTab) {
        PacketThreadUtil.func_180031_a(sPacketSelectAdvancementsTab, this, this.field_147299_f);
        ResourceLocation resourceLocation = sPacketSelectAdvancementsTab.func_194154_a();
        if (resourceLocation == null) {
            this.field_191983_k.func_194230_a(null, false);
        } else {
            Advancement advancement = this.field_191983_k.func_194229_a().func_192084_a(resourceLocation);
            this.field_191983_k.func_194230_a(advancement, false);
        }
    }

    @Override
    public void func_147293_a(SPacketStatistics sPacketStatistics) {
        PacketThreadUtil.func_180031_a(sPacketStatistics, this, this.field_147299_f);
        for (Map.Entry<StatBase, Integer> entry : sPacketStatistics.func_148974_c().entrySet()) {
            StatBase statBase = entry.getKey();
            int \u26032 = entry.getValue();
            this.field_147299_f.field_71439_g.func_146107_m().func_150873_a(this.field_147299_f.field_71439_g, statBase, \u26032);
        }
        this.field_147308_k = true;
        if (this.field_147299_f.field_71462_r instanceof IProgressMeter) {
            ((IProgressMeter)((Object)this.field_147299_f.field_71462_r)).func_193026_g();
        }
    }

    @Override
    public void func_191980_a(SPacketRecipeBook sPacketRecipeBook2) {
        PacketThreadUtil.func_180031_a(sPacketRecipeBook2, this, this.field_147299_f);
        RecipeBook recipeBook = this.field_147299_f.field_71439_g.func_192035_E();
        recipeBook.func_192813_a(sPacketRecipeBook2.func_192593_c());
        recipeBook.func_192810_b(sPacketRecipeBook2.func_192594_d());
        SPacketRecipeBook.State \u26032 = sPacketRecipeBook2.func_194151_e();
        switch (\u26032) {
            case REMOVE: {
                for (IRecipe iRecipe2 : sPacketRecipeBook2.func_192595_a()) {
                    recipeBook.func_193831_b(iRecipe2);
                }
                break;
            }
            case INIT: {
                SPacketRecipeBook sPacketRecipeBook2;
                sPacketRecipeBook2.func_192595_a().forEach(recipeBook::func_194073_a);
                sPacketRecipeBook2.func_193644_b().forEach(recipeBook::func_193825_e);
                break;
            }
            case ADD: {
                SPacketRecipeBook sPacketRecipeBook2;
                sPacketRecipeBook2.func_192595_a().forEach(iRecipe -> {
                    recipeBook.func_194073_a((IRecipe)iRecipe);
                    recipeBook.func_193825_e((IRecipe)iRecipe);
                    RecipeToast.func_193665_a(this.field_147299_f.func_193033_an(), iRecipe);
                });
            }
        }
        RecipeBookClient.field_194087_f.forEach(recipeList -> recipeList.func_194214_a(recipeBook));
        if (this.field_147299_f.field_71462_r instanceof IRecipeShownListener) {
            ((IRecipeShownListener)((Object)this.field_147299_f.field_71462_r)).func_192043_J_();
        }
    }

    @Override
    public void func_147260_a(SPacketEntityEffect sPacketEntityEffect) {
        PacketThreadUtil.func_180031_a(sPacketEntityEffect, this, this.field_147299_f);
        Entity entity = this.field_147300_g.func_73045_a(sPacketEntityEffect.func_149426_d());
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }
        Potion \u26032 = Potion.func_188412_a(sPacketEntityEffect.func_149427_e());
        if (\u26032 == null) {
            return;
        }
        PotionEffect \u26033 = new PotionEffect(\u26032, sPacketEntityEffect.func_180755_e(), sPacketEntityEffect.func_149428_f(), sPacketEntityEffect.func_186984_g(), sPacketEntityEffect.func_179707_f());
        \u26033.func_100012_b(sPacketEntityEffect.func_149429_c());
        ((EntityLivingBase)entity).func_70690_d(\u26033);
    }

    @Override
    public void func_175098_a(SPacketCombatEvent sPacketCombatEvent) {
        PacketThreadUtil.func_180031_a(sPacketCombatEvent, this, this.field_147299_f);
        if (sPacketCombatEvent.field_179776_a == SPacketCombatEvent.Event.ENTITY_DIED && (\u2603 = this.field_147300_g.func_73045_a(sPacketCombatEvent.field_179774_b)) == this.field_147299_f.field_71439_g) {
            this.field_147299_f.func_147108_a(new GuiGameOver(sPacketCombatEvent.field_179773_e));
        }
    }

    @Override
    public void func_175101_a(SPacketServerDifficulty sPacketServerDifficulty) {
        PacketThreadUtil.func_180031_a(sPacketServerDifficulty, this, this.field_147299_f);
        this.field_147299_f.field_71441_e.func_72912_H().func_176144_a(sPacketServerDifficulty.func_179831_b());
        this.field_147299_f.field_71441_e.func_72912_H().func_180783_e(sPacketServerDifficulty.func_179830_a());
    }

    @Override
    public void func_175094_a(SPacketCamera sPacketCamera) {
        PacketThreadUtil.func_180031_a(sPacketCamera, this, this.field_147299_f);
        Entity entity = sPacketCamera.func_179780_a(this.field_147300_g);
        if (entity != null) {
            this.field_147299_f.func_175607_a(entity);
        }
    }

    @Override
    public void func_175093_a(SPacketWorldBorder sPacketWorldBorder) {
        PacketThreadUtil.func_180031_a(sPacketWorldBorder, this, this.field_147299_f);
        sPacketWorldBorder.func_179788_a(this.field_147300_g.func_175723_af());
    }

    @Override
    public void func_175099_a(SPacketTitle sPacketTitle) {
        PacketThreadUtil.func_180031_a(sPacketTitle, this, this.field_147299_f);
        SPacketTitle.Type type = sPacketTitle.func_179807_a();
        String \u26032 = null;
        String \u26033 = null;
        String \u26034 = sPacketTitle.func_179805_b() != null ? sPacketTitle.func_179805_b().func_150254_d() : "";
        switch (type) {
            case TITLE: {
                \u26032 = \u26034;
                break;
            }
            case SUBTITLE: {
                \u26033 = \u26034;
                break;
            }
            case ACTIONBAR: {
                this.field_147299_f.field_71456_v.func_110326_a(\u26034, false);
                return;
            }
            case RESET: {
                this.field_147299_f.field_71456_v.func_175178_a("", "", -1, -1, -1);
                this.field_147299_f.field_71456_v.func_175177_a();
                return;
            }
        }
        this.field_147299_f.field_71456_v.func_175178_a(\u26032, \u26033, sPacketTitle.func_179806_c(), sPacketTitle.func_179804_d(), sPacketTitle.func_179803_e());
    }

    @Override
    public void func_175096_a(SPacketPlayerListHeaderFooter sPacketPlayerListHeaderFooter) {
        this.field_147299_f.field_71456_v.func_175181_h().func_175244_b(sPacketPlayerListHeaderFooter.func_179700_a().func_150254_d().isEmpty() ? null : sPacketPlayerListHeaderFooter.func_179700_a());
        this.field_147299_f.field_71456_v.func_175181_h().func_175248_a(sPacketPlayerListHeaderFooter.func_179701_b().func_150254_d().isEmpty() ? null : sPacketPlayerListHeaderFooter.func_179701_b());
    }

    @Override
    public void func_147262_a(SPacketRemoveEntityEffect sPacketRemoveEntityEffect) {
        PacketThreadUtil.func_180031_a(sPacketRemoveEntityEffect, this, this.field_147299_f);
        Entity entity = sPacketRemoveEntityEffect.func_186967_a(this.field_147300_g);
        if (entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).func_184596_c(sPacketRemoveEntityEffect.func_186968_a());
        }
    }

    @Override
    public void func_147256_a(SPacketPlayerListItem sPacketPlayerListItem) {
        PacketThreadUtil.func_180031_a(sPacketPlayerListItem, this, this.field_147299_f);
        for (SPacketPlayerListItem.AddPlayerData addPlayerData : sPacketPlayerListItem.func_179767_a()) {
            if (sPacketPlayerListItem.func_179768_b() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                this.field_147310_i.remove(addPlayerData.func_179962_a().getId());
                continue;
            }
            NetworkPlayerInfo networkPlayerInfo = this.field_147310_i.get(addPlayerData.func_179962_a().getId());
            if (sPacketPlayerListItem.func_179768_b() == SPacketPlayerListItem.Action.ADD_PLAYER) {
                networkPlayerInfo = new NetworkPlayerInfo(addPlayerData);
                this.field_147310_i.put(networkPlayerInfo.func_178845_a().getId(), networkPlayerInfo);
            }
            if (networkPlayerInfo == null) continue;
            switch (sPacketPlayerListItem.func_179768_b()) {
                case ADD_PLAYER: {
                    networkPlayerInfo.func_178839_a(addPlayerData.func_179960_c());
                    networkPlayerInfo.func_178838_a(addPlayerData.func_179963_b());
                    break;
                }
                case UPDATE_GAME_MODE: {
                    networkPlayerInfo.func_178839_a(addPlayerData.func_179960_c());
                    break;
                }
                case UPDATE_LATENCY: {
                    networkPlayerInfo.func_178838_a(addPlayerData.func_179963_b());
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    networkPlayerInfo.func_178859_a(addPlayerData.func_179961_d());
                }
            }
        }
    }

    @Override
    public void func_147272_a(SPacketKeepAlive sPacketKeepAlive) {
        this.func_147297_a(new CPacketKeepAlive(sPacketKeepAlive.func_149134_c()));
    }

    @Override
    public void func_147270_a(SPacketPlayerAbilities sPacketPlayerAbilities) {
        PacketThreadUtil.func_180031_a(sPacketPlayerAbilities, this, this.field_147299_f);
        EntityPlayerSP entityPlayerSP = this.field_147299_f.field_71439_g;
        entityPlayerSP.field_71075_bZ.field_75100_b = sPacketPlayerAbilities.func_149106_d();
        entityPlayerSP.field_71075_bZ.field_75098_d = sPacketPlayerAbilities.func_149103_f();
        entityPlayerSP.field_71075_bZ.field_75102_a = sPacketPlayerAbilities.func_149112_c();
        entityPlayerSP.field_71075_bZ.field_75101_c = sPacketPlayerAbilities.func_149105_e();
        entityPlayerSP.field_71075_bZ.func_75092_a(sPacketPlayerAbilities.func_149101_g());
        entityPlayerSP.field_71075_bZ.func_82877_b(sPacketPlayerAbilities.func_149107_h());
    }

    @Override
    public void func_147274_a(SPacketTabComplete sPacketTabComplete) {
        PacketThreadUtil.func_180031_a(sPacketTabComplete, this, this.field_147299_f);
        Object[] objectArray = sPacketTabComplete.func_149630_c();
        Arrays.sort(objectArray);
        if (this.field_147299_f.field_71462_r instanceof ITabCompleter) {
            ((ITabCompleter)((Object)this.field_147299_f.field_71462_r)).func_184072_a((String[])objectArray);
        }
    }

    @Override
    public void func_184327_a(SPacketSoundEffect sPacketSoundEffect) {
        PacketThreadUtil.func_180031_a(sPacketSoundEffect, this, this.field_147299_f);
        this.field_147299_f.field_71441_e.func_184148_a(this.field_147299_f.field_71439_g, sPacketSoundEffect.func_149207_d(), sPacketSoundEffect.func_149211_e(), sPacketSoundEffect.func_149210_f(), sPacketSoundEffect.func_186978_a(), sPacketSoundEffect.func_186977_b(), sPacketSoundEffect.func_149208_g(), sPacketSoundEffect.func_149209_h());
    }

    @Override
    public void func_184329_a(SPacketCustomSound sPacketCustomSound) {
        PacketThreadUtil.func_180031_a(sPacketCustomSound, this, this.field_147299_f);
        this.field_147299_f.func_147118_V().func_147682_a(new PositionedSoundRecord(new ResourceLocation(sPacketCustomSound.func_186930_a()), sPacketCustomSound.func_186929_b(), sPacketCustomSound.func_186927_f(), sPacketCustomSound.func_186928_g(), false, 0, ISound.AttenuationType.LINEAR, (float)sPacketCustomSound.func_186932_c(), (float)sPacketCustomSound.func_186926_d(), (float)sPacketCustomSound.func_186925_e()));
    }

    @Override
    public void func_175095_a(SPacketResourcePackSend sPacketResourcePackSend) {
        final String string = sPacketResourcePackSend.func_179783_a();
        \u2603 = sPacketResourcePackSend.func_179784_b();
        if (!this.func_189688_b(string)) {
            return;
        }
        if (string.startsWith("level://")) {
            try {
                unsupportedEncodingException = URLDecoder.decode(string.substring("level://".length()), StandardCharsets.UTF_8.toString());
                File file = new File(this.field_147299_f.field_71412_D, "saves");
                \u2603 = new File(file, unsupportedEncodingException);
                if (\u2603.isFile()) {
                    this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                    Futures.addCallback(this.field_147299_f.func_110438_M().func_177319_a(\u2603), this.func_189686_f());
                    return;
                }
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
            this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            return;
        }
        ServerData serverData = this.field_147299_f.func_147104_D();
        if (serverData != null && serverData.func_152586_b() == ServerData.ServerResourceMode.ENABLED) {
            this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
            Futures.addCallback(this.field_147299_f.func_110438_M().func_180601_a(string, \u2603), this.func_189686_f());
        } else if (serverData == null || serverData.func_152586_b() == ServerData.ServerResourceMode.PROMPT) {
            this.field_147299_f.func_152344_a(new Runnable(){

                @Override
                public void run() {
                    NetHandlerPlayClient.this.field_147299_f.func_147108_a(new GuiYesNo(new GuiYesNoCallback(){

                        @Override
                        public void func_73878_a(boolean bl, int n) {
                            NetHandlerPlayClient.this.field_147299_f = Minecraft.func_71410_x();
                            ServerData serverData = NetHandlerPlayClient.this.field_147299_f.func_147104_D();
                            if (bl) {
                                if (serverData != null) {
                                    serverData.func_152584_a(ServerData.ServerResourceMode.ENABLED);
                                }
                                NetHandlerPlayClient.this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                                Futures.addCallback(NetHandlerPlayClient.this.field_147299_f.func_110438_M().func_180601_a(string, \u2603), (FutureCallback)NetHandlerPlayClient.this.func_189686_f());
                            } else {
                                if (serverData != null) {
                                    serverData.func_152584_a(ServerData.ServerResourceMode.DISABLED);
                                }
                                NetHandlerPlayClient.this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
                            }
                            ServerList.func_147414_b(serverData);
                            NetHandlerPlayClient.this.field_147299_f.func_147108_a(null);
                        }
                    }, I18n.func_135052_a("multiplayer.texturePrompt.line1", new Object[0]), I18n.func_135052_a("multiplayer.texturePrompt.line2", new Object[0]), 0));
                }
            });
        } else {
            this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
        }
    }

    private boolean func_189688_b(String string) {
        try {
            URI uRI = new URI(string);
            String \u26032 = uRI.getScheme();
            boolean \u26033 = "level".equals(\u26032);
            if (!("http".equals(\u26032) || "https".equals(\u26032) || \u26033)) {
                throw new URISyntaxException(string, "Wrong protocol");
            }
            if (\u26033 && (string.contains("..") || !string.endsWith("/resources.zip"))) {
                throw new URISyntaxException(string, "Invalid levelstorage resourcepack path");
            }
        }
        catch (URISyntaxException uRISyntaxException) {
            this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            return false;
        }
        return true;
    }

    private FutureCallback<Object> func_189686_f() {
        return new FutureCallback<Object>(){

            public void onSuccess(@Nullable Object object) {
                NetHandlerPlayClient.this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            }

            public void onFailure(Throwable throwable) {
                NetHandlerPlayClient.this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }
        };
    }

    @Override
    public void func_184325_a(SPacketUpdateBossInfo sPacketUpdateBossInfo) {
        PacketThreadUtil.func_180031_a(sPacketUpdateBossInfo, this, this.field_147299_f);
        this.field_147299_f.field_71456_v.func_184046_j().func_184055_a(sPacketUpdateBossInfo);
    }

    @Override
    public void func_184324_a(SPacketCooldown sPacketCooldown) {
        PacketThreadUtil.func_180031_a(sPacketCooldown, this, this.field_147299_f);
        if (sPacketCooldown.func_186922_b() == 0) {
            this.field_147299_f.field_71439_g.func_184811_cZ().func_185142_b(sPacketCooldown.func_186920_a());
        } else {
            this.field_147299_f.field_71439_g.func_184811_cZ().func_185145_a(sPacketCooldown.func_186920_a(), sPacketCooldown.func_186922_b());
        }
    }

    @Override
    public void func_184323_a(SPacketMoveVehicle sPacketMoveVehicle) {
        PacketThreadUtil.func_180031_a(sPacketMoveVehicle, this, this.field_147299_f);
        Entity entity = this.field_147299_f.field_71439_g.func_184208_bv();
        if (entity != this.field_147299_f.field_71439_g && entity.func_184186_bw()) {
            entity.func_70080_a(sPacketMoveVehicle.func_186957_a(), sPacketMoveVehicle.func_186955_b(), sPacketMoveVehicle.func_186956_c(), sPacketMoveVehicle.func_186959_d(), sPacketMoveVehicle.func_186958_e());
            this.field_147302_e.func_179290_a(new CPacketVehicleMove(entity));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void func_147240_a(SPacketCustomPayload sPacketCustomPayload2) {
        SPacketCustomPayload sPacketCustomPayload2;
        PacketThreadUtil.func_180031_a(sPacketCustomPayload2, this, this.field_147299_f);
        if ("MC|TrList".equals(sPacketCustomPayload2.func_149169_c())) {
            PacketBuffer packetBuffer = sPacketCustomPayload2.func_180735_b();
            try {
                int n = packetBuffer.readInt();
                GuiScreen \u26032 = this.field_147299_f.field_71462_r;
                if (\u26032 == null || !(\u26032 instanceof GuiMerchant) || n != this.field_147299_f.field_71439_g.field_71070_bA.field_75152_c) return;
                IMerchant \u26033 = ((GuiMerchant)\u26032).func_147035_g();
                MerchantRecipeList \u26034 = MerchantRecipeList.func_151390_b(packetBuffer);
                \u26033.func_70930_a(\u26034);
                return;
            }
            catch (IOException iOException) {
                field_147301_d.error("Couldn't load trade info", (Throwable)iOException);
                return;
            }
            finally {
                packetBuffer.release();
            }
        } else if ("MC|Brand".equals(sPacketCustomPayload2.func_149169_c())) {
            this.field_147299_f.field_71439_g.func_175158_f(sPacketCustomPayload2.func_180735_b().func_150789_c(Short.MAX_VALUE));
            return;
        } else if ("MC|BOpen".equals(sPacketCustomPayload2.func_149169_c())) {
            EnumHand enumHand = sPacketCustomPayload2.func_180735_b().func_179257_a(EnumHand.class);
            ItemStack itemStack = \u2603 = enumHand == EnumHand.OFF_HAND ? this.field_147299_f.field_71439_g.func_184592_cb() : this.field_147299_f.field_71439_g.func_184614_ca();
            if (\u2603.func_77973_b() != Items.field_151164_bB) return;
            this.field_147299_f.func_147108_a(new GuiScreenBook(this.field_147299_f.field_71439_g, \u2603, false));
            return;
        } else if ("MC|DebugPath".equals(sPacketCustomPayload2.func_149169_c())) {
            PacketBuffer packetBuffer = sPacketCustomPayload2.func_180735_b();
            int \u26035 = packetBuffer.readInt();
            float \u26036 = packetBuffer.readFloat();
            Path \u26037 = Path.func_186311_b(packetBuffer);
            ((DebugRendererPathfinding)this.field_147299_f.field_184132_p.field_188286_a).func_188289_a(\u26035, \u26037, \u26036);
            return;
        } else if ("MC|DebugNeighborsUpdate".equals(sPacketCustomPayload2.func_149169_c())) {
            \u2603 = sPacketCustomPayload2.func_180735_b();
            long \u26038 = \u2603.func_179260_f();
            BlockPos \u26039 = \u2603.func_179259_c();
            ((DebugRendererNeighborsUpdate)this.field_147299_f.field_184132_p.field_191557_f).func_191553_a(\u26038, \u26039);
            return;
        } else {
            if (!"MC|StopSound".equals(sPacketCustomPayload2.func_149169_c())) return;
            \u2603 = sPacketCustomPayload2.func_180735_b();
            String \u260310 = \u2603.func_150789_c(Short.MAX_VALUE);
            String \u260311 = \u2603.func_150789_c(256);
            this.field_147299_f.func_147118_V().func_189520_a(\u260311, SoundCategory.func_187950_a(\u260310));
        }
    }

    @Override
    public void func_147291_a(SPacketScoreboardObjective sPacketScoreboardObjective) {
        PacketThreadUtil.func_180031_a(sPacketScoreboardObjective, this, this.field_147299_f);
        Scoreboard scoreboard = this.field_147300_g.func_96441_U();
        if (sPacketScoreboardObjective.func_149338_e() == 0) {
            ScoreObjective scoreObjective = scoreboard.func_96535_a(sPacketScoreboardObjective.func_149339_c(), IScoreCriteria.field_96641_b);
            scoreObjective.func_96681_a(sPacketScoreboardObjective.func_149337_d());
            scoreObjective.func_178767_a(sPacketScoreboardObjective.func_179817_d());
        } else {
            ScoreObjective \u26032 = scoreboard.func_96518_b(sPacketScoreboardObjective.func_149339_c());
            if (sPacketScoreboardObjective.func_149338_e() == 1) {
                scoreboard.func_96519_k(\u26032);
            } else if (sPacketScoreboardObjective.func_149338_e() == 2) {
                \u26032.func_96681_a(sPacketScoreboardObjective.func_149337_d());
                \u26032.func_178767_a(sPacketScoreboardObjective.func_179817_d());
            }
        }
    }

    @Override
    public void func_147250_a(SPacketUpdateScore sPacketUpdateScore2) {
        SPacketUpdateScore sPacketUpdateScore2;
        PacketThreadUtil.func_180031_a(sPacketUpdateScore2, this, this.field_147299_f);
        Scoreboard scoreboard = this.field_147300_g.func_96441_U();
        ScoreObjective \u26032 = scoreboard.func_96518_b(sPacketUpdateScore2.func_149321_d());
        if (sPacketUpdateScore2.func_180751_d() == SPacketUpdateScore.Action.CHANGE) {
            Score score = scoreboard.func_96529_a(sPacketUpdateScore2.func_149324_c(), \u26032);
            score.func_96647_c(sPacketUpdateScore2.func_149323_e());
        } else if (sPacketUpdateScore2.func_180751_d() == SPacketUpdateScore.Action.REMOVE) {
            if (StringUtils.func_151246_b(sPacketUpdateScore2.func_149321_d())) {
                scoreboard.func_178822_d(sPacketUpdateScore2.func_149324_c(), null);
            } else if (\u26032 != null) {
                scoreboard.func_178822_d(sPacketUpdateScore2.func_149324_c(), \u26032);
            }
        }
    }

    @Override
    public void func_147254_a(SPacketDisplayObjective sPacketDisplayObjective) {
        PacketThreadUtil.func_180031_a(sPacketDisplayObjective, this, this.field_147299_f);
        Scoreboard scoreboard = this.field_147300_g.func_96441_U();
        if (sPacketDisplayObjective.func_149370_d().isEmpty()) {
            scoreboard.func_96530_a(sPacketDisplayObjective.func_149371_c(), null);
        } else {
            ScoreObjective scoreObjective = scoreboard.func_96518_b(sPacketDisplayObjective.func_149370_d());
            scoreboard.func_96530_a(sPacketDisplayObjective.func_149371_c(), scoreObjective);
        }
    }

    @Override
    public void func_147247_a(SPacketTeams sPacketTeams2) {
        SPacketTeams sPacketTeams2;
        PacketThreadUtil.func_180031_a(sPacketTeams2, this, this.field_147299_f);
        Scoreboard scoreboard = this.field_147300_g.func_96441_U();
        ScorePlayerTeam \u26032 = sPacketTeams2.func_149307_h() == 0 ? scoreboard.func_96527_f(sPacketTeams2.func_149312_c()) : scoreboard.func_96508_e(sPacketTeams2.func_149312_c());
        if (sPacketTeams2.func_149307_h() == 0 || sPacketTeams2.func_149307_h() == 2) {
            \u26032.func_96664_a(sPacketTeams2.func_149306_d());
            \u26032.func_96666_b(sPacketTeams2.func_149311_e());
            \u26032.func_96662_c(sPacketTeams2.func_149309_f());
            \u26032.func_178774_a(TextFormatting.func_175744_a(sPacketTeams2.func_179813_h()));
            \u26032.func_98298_a(sPacketTeams2.func_149308_i());
            Team.EnumVisible enumVisible = Team.EnumVisible.func_178824_a(sPacketTeams2.func_179814_i());
            if (enumVisible != null) {
                \u26032.func_178772_a(enumVisible);
            }
            if ((object = Team.CollisionRule.func_186686_a(sPacketTeams2.func_186975_j())) != null) {
                \u26032.func_186682_a((Team.CollisionRule)((Object)object));
            }
        }
        if (sPacketTeams2.func_149307_h() == 0 || sPacketTeams2.func_149307_h() == 3) {
            for (Object object : sPacketTeams2.func_149310_g()) {
                scoreboard.func_151392_a((String)object, sPacketTeams2.func_149312_c());
            }
        }
        if (sPacketTeams2.func_149307_h() == 4) {
            for (Object object : sPacketTeams2.func_149310_g()) {
                scoreboard.func_96512_b((String)object, \u26032);
            }
        }
        if (sPacketTeams2.func_149307_h() == 1) {
            scoreboard.func_96511_d(\u26032);
        }
    }

    @Override
    public void func_147289_a(SPacketParticles sPacketParticles) {
        PacketThreadUtil.func_180031_a(sPacketParticles, this, this.field_147299_f);
        if (sPacketParticles.func_149222_k() == 0) {
            double d = sPacketParticles.func_149227_j() * sPacketParticles.func_149221_g();
            \u2603 = sPacketParticles.func_149227_j() * sPacketParticles.func_149224_h();
            \u2603 = sPacketParticles.func_149227_j() * sPacketParticles.func_149223_i();
            try {
                this.field_147300_g.func_175682_a(sPacketParticles.func_179749_a(), sPacketParticles.func_179750_b(), sPacketParticles.func_149220_d(), sPacketParticles.func_149226_e(), sPacketParticles.func_149225_f(), d, \u2603, \u2603, sPacketParticles.func_179748_k());
            }
            catch (Throwable \u26032) {
                field_147301_d.warn("Could not spawn particle effect {}", (Object)sPacketParticles.func_179749_a());
            }
        } else {
            for (int i = 0; i < sPacketParticles.func_149222_k(); ++i) {
                double d = this.field_147306_l.nextGaussian() * (double)sPacketParticles.func_149221_g();
                \u2603 = this.field_147306_l.nextGaussian() * (double)sPacketParticles.func_149224_h();
                \u2603 = this.field_147306_l.nextGaussian() * (double)sPacketParticles.func_149223_i();
                \u2603 = this.field_147306_l.nextGaussian() * (double)sPacketParticles.func_149227_j();
                \u2603 = this.field_147306_l.nextGaussian() * (double)sPacketParticles.func_149227_j();
                \u2603 = this.field_147306_l.nextGaussian() * (double)sPacketParticles.func_149227_j();
                try {
                    this.field_147300_g.func_175682_a(sPacketParticles.func_179749_a(), sPacketParticles.func_179750_b(), sPacketParticles.func_149220_d() + d, sPacketParticles.func_149226_e() + \u2603, sPacketParticles.func_149225_f() + \u2603, \u2603, \u2603, \u2603, sPacketParticles.func_179748_k());
                    continue;
                }
                catch (Throwable \u26033) {
                    field_147301_d.warn("Could not spawn particle effect {}", (Object)sPacketParticles.func_179749_a());
                    return;
                }
            }
        }
    }

    @Override
    public void func_147290_a(SPacketEntityProperties sPacketEntityProperties) {
        PacketThreadUtil.func_180031_a(sPacketEntityProperties, this, this.field_147299_f);
        Entity entity = this.field_147300_g.func_73045_a(sPacketEntityProperties.func_149442_c());
        if (entity == null) {
            return;
        }
        if (!(entity instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
        }
        AbstractAttributeMap \u26032 = ((EntityLivingBase)entity).func_110140_aT();
        for (SPacketEntityProperties.Snapshot snapshot : sPacketEntityProperties.func_149441_d()) {
            IAttributeInstance iAttributeInstance = \u26032.func_111152_a(snapshot.func_151409_a());
            if (iAttributeInstance == null) {
                iAttributeInstance = \u26032.func_111150_b(new RangedAttribute(null, snapshot.func_151409_a(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
            }
            iAttributeInstance.func_111128_a(snapshot.func_151410_b());
            iAttributeInstance.func_142049_d();
            for (AttributeModifier attributeModifier : snapshot.func_151408_c()) {
                iAttributeInstance.func_111121_a(attributeModifier);
            }
        }
    }

    @Override
    public void func_194307_a(SPacketPlaceGhostRecipe sPacketPlaceGhostRecipe) {
        PacketThreadUtil.func_180031_a(sPacketPlaceGhostRecipe, this, this.field_147299_f);
        Container container = this.field_147299_f.field_71439_g.field_71070_bA;
        if (container.field_75152_c != sPacketPlaceGhostRecipe.func_194313_b() || !container.func_75129_b(this.field_147299_f.field_71439_g)) {
            return;
        }
        if (this.field_147299_f.field_71462_r instanceof IRecipeShownListener) {
            GuiRecipeBook guiRecipeBook = ((IRecipeShownListener)((Object)this.field_147299_f.field_71462_r)).func_194310_f();
            guiRecipeBook.func_193951_a(sPacketPlaceGhostRecipe.func_194311_a(), container.field_75151_b);
        }
    }

    public NetworkManager func_147298_b() {
        return this.field_147302_e;
    }

    public Collection<NetworkPlayerInfo> func_175106_d() {
        return this.field_147310_i.values();
    }

    public NetworkPlayerInfo func_175102_a(UUID uUID) {
        return this.field_147310_i.get(uUID);
    }

    @Nullable
    public NetworkPlayerInfo func_175104_a(String string) {
        for (NetworkPlayerInfo networkPlayerInfo : this.field_147310_i.values()) {
            if (!networkPlayerInfo.func_178845_a().getName().equals(string)) continue;
            return networkPlayerInfo;
        }
        return null;
    }

    public GameProfile func_175105_e() {
        return this.field_175107_d;
    }

    public ClientAdvancementManager func_191982_f() {
        return this.field_191983_k;
    }
}

