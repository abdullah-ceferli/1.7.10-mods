/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.command.ICommand
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.tileentity.TileEntitySign
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.client.ClientCommandHandler
 *  net.minecraftforge.client.EnumHelperClient
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.client.model.obj.OBJLoader
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.client.registry.ClientRegistry
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.proxy;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.block.BlockBiomePurifier;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.client.DerivedDistortionTextHandler;
import com.dhanantry.scapeandrunparasites.client.DistortedItemHighlightOverlayHandler;
import com.dhanantry.scapeandrunparasites.client.DistortionGuiSwapHandler;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import com.dhanantry.scapeandrunparasites.client.fog.SRPFogHandler;
import com.dhanantry.scapeandrunparasites.client.gui.GuiGameOverEscape;
import com.dhanantry.scapeandrunparasites.client.renderer.MobilityArmorFirstPersonHandler;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderDistortedSign;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderRelayController;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderTrophyTESR;
import com.dhanantry.scapeandrunparasites.client.shader.BlackSkyShaderManager;
import com.dhanantry.scapeandrunparasites.client.shader.BreatheShaderCommand;
import com.dhanantry.scapeandrunparasites.client.shader.star.StarWorldShaderManager;
import com.dhanantry.scapeandrunparasites.client.sky.CommandSkyFlashClient;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityTrophy;
import com.dhanantry.scapeandrunparasites.init.SRPMusic;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.msg.AbstractPacket;
import com.dhanantry.scapeandrunparasites.proxy.CommonProxy;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import com.dhanantry.scapeandrunparasites.util.SRPReference;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus;
import com.dhanantry.scapeandrunparasites.util.handlers.SRPRegistryHandlers;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.Objects;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy
extends CommonProxy {
    public static final Minecraft MC = Minecraft.getMinecraft();

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        com.dhanantry.scapeandrunparasites.backport.client.EntityDataClient.initialize();
        SRPRegistryHandlers.initRenders();
        MinecraftForge.EVENT_BUS.register((Object)new GuiGameOverEscape.OpenHook());
        MinecraftForge.EVENT_BUS.register((Object)new DerivedDistortionTextHandler());
        MinecraftForge.EVENT_BUS.register((Object)new DistortionGuiSwapHandler());
        MinecraftForge.EVENT_BUS.register((Object)new DistortedItemHighlightOverlayHandler());
        BreatheShaderCommand.register();
        MinecraftForge.EVENT_BUS.register((Object)new MobilityArmorFirstPersonHandler());
        BlackSkyShaderManager.get().register();
        StarWorldShaderManager.get().register();
        ClientProxy.initMusic();
        CelestialObjectRegistry.init();
        OBJLoader.INSTANCE.addDomain("srparasites");
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRelayController.class, (TileEntitySpecialRenderer)new RenderRelayController());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySign.class, (TileEntitySpecialRenderer)new RenderDistortedSign());
    }

    public static void initMusic() {
        SRPMusic.EVPHASE_1_MUSIC = EnumHelperClient.addMusicType((String)"srpphaseone", (String)SRPSounds.EVPHASE_1_M, (int)20, (int)200);
        SRPMusic.EVPHASE_2_MUSIC = EnumHelperClient.addMusicType((String)"srpphasetwo", (String)SRPSounds.EVPHASE_2_M, (int)20, (int)200);
        SRPMusic.EVPHASE_3_MUSIC = EnumHelperClient.addMusicType((String)"srpphasethree", (String)SRPSounds.EVPHASE_3_M, (int)20, (int)200);
        SRPMusic.EVPHASE_4_MUSIC = EnumHelperClient.addMusicType((String)"srpphasefour", (String)SRPSounds.EVPHASE_4_M, (int)20, (int)200);
        SRPMusic.EVPHASE_5_MUSIC = EnumHelperClient.addMusicType((String)"srpphasefive", (String)SRPSounds.EVPHASE_5_M, (int)20, (int)200);
        SRPMusic.EVPHASE_6_MUSIC = EnumHelperClient.addMusicType((String)"srpphasesix", (String)SRPSounds.EVPHASE_6_M, (int)20, (int)200);
        SRPMusic.EVPHASE_7_MUSIC = EnumHelperClient.addMusicType((String)"srpphaseseven", (String)SRPSounds.EVPHASE_7_M, (int)20, (int)200);
        SRPMusic.EVPHASE_8_MUSIC = EnumHelperClient.addMusicType((String)"srpphaseeight", (String)SRPSounds.EVPHASE_8_M, (int)20, (int)200);
        SRPMusic.EVPHASE_9_MUSIC = EnumHelperClient.addMusicType((String)"srpphasenine", (String)SRPSounds.EVPHASE_9_M, (int)20, (int)200);
        SRPMusic.EVPHASE_10_MUSIC = EnumHelperClient.addMusicType((String)"srpphaseten", (String)SRPSounds.EVPHASE_10_M, (int)20, (int)200);
        SRPMusic.BIOME_MUSIC = EnumHelperClient.addMusicType((String)"srpbiome", (String)SRPSounds.BIOME_M, (int)20, (int)200);
        SRPMusic.SCENT_MUSIC = EnumHelperClient.addMusicType((String)"srpscent", (String)SRPSounds.SCENT_M, (int)20, (int)200);
        SRPMusic.DISC1 = EnumHelperClient.addMusicType((String)"srpdiscone", (String)SRPSounds.DISC1, (int)20, (int)200);
        SRPMusic.DISC2 = EnumHelperClient.addMusicType((String)"srpdisctwo", (String)SRPSounds.DISC2, (int)20, (int)200);
        SRPMusic.DISC3 = EnumHelperClient.addMusicType((String)"srpdiscthree", (String)SRPSounds.DISC3, (int)20, (int)200);
    }

    @Override
    public void spreadBiome(BlockPos pos, boolean convert, int type) {
        if (Minecraft.getMinecraft().theWorld == null) {
            return;
        }
        Biome current = Minecraft.getMinecraft().theWorld.getBiome(pos);
        if (convert) {
            BiomeParasiteBase target = SRPReference.getBiomeFromInt(type);
            if (target != null && current == target) {
                return;
            }
            BlockParasiteSpreading.positionToParasiteBiome((World)Minecraft.getMinecraft().theWorld, pos, type);
        } else {
            if (!(current instanceof BiomeParasiteBase)) {
                return;
            }
            BlockBiomePurifier.positionToBiome((World)Minecraft.getMinecraft().theWorld, pos, type);
        }
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        ClientCommandHandler.instance.registerCommand((ICommand)new CommandSkyFlashClient());
        SRPFogHandler.register();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophy.class, (TileEntitySpecialRenderer)new RenderTrophyTESR());
    }

    @Override
    public void playMovingSound(int sound, float v) {
        Random rand = new Random();
        switch (sound) {
            case -1: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.BHEART, (float)1.0f, (float)(v * SRPConfigWorld.biomeHeartVol)));
                break;
            }
            case 1: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.EVPHASE_1, (float)1.0f, (float)0.75f));
                break;
            }
            case 2: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.EVPHASE_2, (float)1.0f, (float)0.75f));
                break;
            }
            case 3: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.EVPHASE_3, (float)1.0f, (float)0.75f));
                break;
            }
            case 4: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.EVPHASE_4, (float)1.0f, (float)0.75f));
                break;
            }
            case 5: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.EVPHASE_5, (float)1.0f, (float)0.75f));
                break;
            }
            case 6: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.EVPHASE_6, (float)1.0f, (float)0.75f));
                break;
            }
            case 7: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.EVPHASE_7, (float)1.0f, (float)0.75f));
                break;
            }
            case 8: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.EVPHASE_8, (float)1.0f, (float)0.75f));
                break;
            }
            case 9: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.EVPHASE_9, (float)1.0f, (float)0.75f));
                break;
            }
            case 10: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.EVPHASE_10, (float)1.0f, (float)0.75f));
                break;
            }
            case 100: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.NODE_1, (float)1.0f, (float)0.75f));
                break;
            }
            case 101: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.COLONY_1, (float)1.0f, (float)0.75f));
                break;
            }
            case 102: {
                if (SRPEventHandlerBus.clientScent <= 0) {
                    Minecraft.getMinecraft().getSoundHandler().stop("", SoundCategory.MUSIC);
                    Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getMusicRecord((String)SRPMusic.SCENT_MUSIC.getMusicLocation()));
                }
                SRPEventHandlerBus.musicTimer = 5000;
                SRPEventHandlerBus.clientScent = 5000;
                break;
            }
            case 103: {
                SRPEventHandlerBus.musicTimer = 1000;
                SRPEventHandlerBus.clientScent = 140;
                break;
            }
            case 104: {
                SRPEventHandlerBus.musicTimer = 1000;
                Minecraft.getMinecraft().getSoundHandler().stop("", SoundCategory.MUSIC);
                break;
            }
            case 200: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_0, (float)1.0f, (float)0.45f));
                break;
            }
            case 201: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_1, (float)1.0f, (float)0.45f));
                break;
            }
            case 202: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_2, (float)1.0f, (float)0.45f));
                break;
            }
            case 203: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_3, (float)1.0f, (float)0.45f));
                break;
            }
            case 204: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_4, (float)1.0f, (float)0.45f));
                break;
            }
            case 205: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_5, (float)1.0f, (float)0.45f));
                break;
            }
            case 206: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_6, (float)1.0f, (float)0.45f));
                break;
            }
            case 207: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_7, (float)1.0f, (float)0.45f));
                break;
            }
            case 208: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_8, (float)1.0f, (float)0.45f));
                break;
            }
            case 209: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_9, (float)1.0f, (float)0.45f));
                break;
            }
            case 210: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_10, (float)1.0f, (float)0.45f));
                break;
            }
            case 211: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_11, (float)1.0f, (float)0.45f));
                break;
            }
            case 212: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_12, (float)1.0f, (float)0.45f));
                break;
            }
            case 213: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_13, (float)1.0f, (float)0.45f));
                break;
            }
            case 214: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_14, (float)1.0f, (float)0.45f));
                break;
            }
            case 215: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_15, (float)1.0f, (float)0.45f));
                break;
            }
            case 216: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_16, (float)1.0f, (float)0.45f));
                break;
            }
            case 217: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_17, (float)1.0f, (float)0.45f));
                break;
            }
            case 218: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_18, (float)1.0f, (float)0.45f));
                break;
            }
            case 219: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_19, (float)1.0f, (float)0.45f));
                break;
            }
            case 220: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_20, (float)1.0f, (float)0.45f));
                break;
            }
            case 221: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_21, (float)1.0f, (float)0.45f));
                break;
            }
            case 222: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_22, (float)1.0f, (float)0.45f));
                break;
            }
            case 223: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_23, (float)1.0f, (float)0.45f));
                break;
            }
            case 224: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_24, (float)1.0f, (float)0.45f));
                break;
            }
            case 225: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_25, (float)1.0f, (float)0.45f));
                break;
            }
            case 226: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_26, (float)1.0f, (float)0.45f));
                break;
            }
            case 227: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_27, (float)1.0f, (float)0.45f));
                break;
            }
            case 228: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_28, (float)1.0f, (float)0.45f));
                break;
            }
            case 229: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.DISLO_29, (float)1.0f, (float)0.45f));
                break;
            }
            case 400: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.ORIGINNEW, (float)((rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f), (float)0.75f));
                break;
            }
            case 401: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.ORIGINOUTBREAK, (float)((rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f), (float)0.75f));
                break;
            }
            case 402: {
                Minecraft.getMinecraft().getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((String)SRPSounds.ORIGINDELETED, (float)((rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f), (float)0.75f));
            }
        }
    }

    @Override
    public void modelReg(Item item, int meta, String id) {
        if (item != null) {
            ModelLoader.setCustomModelResourceLocation((Item)item, (int)meta, (ModelResourceLocation)new ModelResourceLocation(Objects.requireNonNull(RegistryNames.get(item)), id));
        }
    }

    @Override
    public <T extends AbstractPacket<T>> void networkMessage(T message, MessageContext messageContext) {
        if (messageContext.side.isServer()) {
            super.networkMessage(message, messageContext);
        } else {
            MC.addScheduledTask(() -> message.clientSide(MC, message, (EntityPlayer)ClientProxy.MC.thePlayer, messageContext));
        }
    }
}

