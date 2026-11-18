/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(value=Side.CLIENT)
public class SoundHandler
implements IResourceManagerReloadListener,
IUpdatePlayerListBox {
    private static final Logger logger = LogManager.getLogger();
    private static final Gson field_147699_c = new GsonBuilder().registerTypeAdapter(SoundList.class, (Object)new SoundListSerializer()).create();
    private static final ParameterizedType field_147696_d = new ParameterizedType(){
        private static final String __OBFID = "CL_00001148";

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class, SoundList.class};
        }

        @Override
        public Type getRawType() {
            return Map.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };
    public static final SoundPoolEntry missing_sound = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0, 0.0, false);
    private final SoundRegistry sndRegistry = new SoundRegistry();
    private final SoundManager sndManager;
    private final IResourceManager mcResourceManager;
    private static final String __OBFID = "CL_00001147";

    public SoundHandler(IResourceManager p_i45122_1_, GameSettings p_i45122_2_) {
        this.mcResourceManager = p_i45122_1_;
        this.sndManager = new SoundManager(this, p_i45122_2_);
    }

    @Override
    public void onResourceManagerReload(IResourceManager p_110549_1_) {
        this.sndManager.reloadSoundSystem();
        this.sndRegistry.func_148763_c();
        for (String s : p_110549_1_.getResourceDomains()) {
            try {
                List list = p_110549_1_.getAllResources(new ResourceLocation(s, "sounds.json"));
                for (IResource iresource : list) {
                    try {
                        Map map = (Map)field_147699_c.fromJson((Reader)new InputStreamReader(iresource.getInputStream()), (Type)field_147696_d);
                        for (Map.Entry entry : map.entrySet()) {
                            this.loadSoundResource(new ResourceLocation(s, (String)entry.getKey()), (SoundList)entry.getValue());
                        }
                    }
                    catch (RuntimeException runtimeexception) {
                        logger.warn("Invalid sounds.json", (Throwable)runtimeexception);
                    }
                }
            }
            catch (IOException iOException) {
            }
        }
    }

    private void loadSoundResource(ResourceLocation p_147693_1_, SoundList p_147693_2_) {
        SoundEventAccessorComposite soundeventaccessorcomposite;
        if (this.sndRegistry.containsKey(p_147693_1_) && !p_147693_2_.canReplaceExisting()) {
            soundeventaccessorcomposite = (SoundEventAccessorComposite)this.sndRegistry.getObject(p_147693_1_);
        } else {
            logger.debug("Registered/replaced new sound event location {}", new Object[]{p_147693_1_});
            soundeventaccessorcomposite = new SoundEventAccessorComposite(p_147693_1_, 1.0, 1.0, p_147693_2_.getSoundCategory());
            this.sndRegistry.registerSound(soundeventaccessorcomposite);
        }
        block7: for (final SoundList.SoundEntry soundentry : p_147693_2_.getSoundList()) {
            ISoundEventAccessor object;
            String s = soundentry.getSoundEntryName();
            ResourceLocation resourcelocation1 = new ResourceLocation(s);
            final String s1 = s.contains(":") ? resourcelocation1.getResourceDomain() : p_147693_1_.getResourceDomain();
            switch (SwitchType.field_148765_a[soundentry.getSoundEntryType().ordinal()]) {
                case 1: {
                    ResourceLocation resourcelocation2 = new ResourceLocation(s1, "sounds/" + resourcelocation1.getResourcePath() + ".ogg");
                    try {
                        this.mcResourceManager.getResource(resourcelocation2);
                    }
                    catch (FileNotFoundException filenotfoundexception) {
                        logger.warn("File {} does not exist, cannot add it to event {}", new Object[]{resourcelocation2, p_147693_1_});
                        continue block7;
                    }
                    catch (IOException ioexception) {
                        logger.warn("Could not load sound file " + resourcelocation2 + ", cannot add it to event " + p_147693_1_, (Throwable)ioexception);
                        continue block7;
                    }
                    object = new SoundEventAccessor(new SoundPoolEntry(resourcelocation2, soundentry.getSoundEntryPitch(), soundentry.getSoundEntryVolume(), soundentry.isStreaming()), soundentry.getSoundEntryWeight());
                    break;
                }
                case 2: {
                    object = new ISoundEventAccessor(){
                        final ResourceLocation field_148726_a;
                        private static final String __OBFID = "CL_00001149";
                        {
                            this.field_148726_a = new ResourceLocation(s1, soundentry.getSoundEntryName());
                        }

                        @Override
                        public int func_148721_a() {
                            SoundEventAccessorComposite soundeventaccessorcomposite1 = (SoundEventAccessorComposite)SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                            return soundeventaccessorcomposite1 == null ? 0 : soundeventaccessorcomposite1.func_148721_a();
                        }

                        @Override
                        public SoundPoolEntry func_148720_g() {
                            SoundEventAccessorComposite soundeventaccessorcomposite1 = (SoundEventAccessorComposite)SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                            return soundeventaccessorcomposite1 == null ? missing_sound : soundeventaccessorcomposite1.func_148720_g();
                        }
                    };
                    break;
                }
                default: {
                    throw new IllegalStateException("IN YOU FACE");
                }
            }
            soundeventaccessorcomposite.addSoundToEventPool(object);
        }
    }

    public SoundEventAccessorComposite getSound(ResourceLocation p_147680_1_) {
        return (SoundEventAccessorComposite)this.sndRegistry.getObject(p_147680_1_);
    }

    public void playSound(ISound p_147682_1_) {
        this.sndManager.playSound(p_147682_1_);
    }

    public void playDelayedSound(ISound p_147681_1_, int p_147681_2_) {
        this.sndManager.addDelayedSound(p_147681_1_, p_147681_2_);
    }

    public void setListener(EntityPlayer p_147691_1_, float p_147691_2_) {
        this.sndManager.setListener(p_147691_1_, p_147691_2_);
    }

    public void pauseSounds() {
        this.sndManager.pauseAllSounds();
    }

    public void stopSounds() {
        this.sndManager.stopAllSounds();
    }

    public void unloadSounds() {
        this.sndManager.unloadSoundSystem();
    }

    @Override
    public void update() {
        this.sndManager.updateAllSounds();
    }

    public void resumeSounds() {
        this.sndManager.resumeAllSounds();
    }

    public void setSoundLevel(SoundCategory p_147684_1_, float p_147684_2_) {
        if (p_147684_1_ == SoundCategory.MASTER && p_147684_2_ <= 0.0f) {
            this.stopSounds();
        }
        this.sndManager.setSoundCategoryVolume(p_147684_1_, p_147684_2_);
    }

    public void stopSound(ISound p_147683_1_) {
        this.sndManager.stopSound(p_147683_1_);
    }

    public SoundEventAccessorComposite getRandomSoundFromCategories(SoundCategory ... p_147686_1_) {
        ArrayList arraylist = Lists.newArrayList();
        for (ResourceLocation resourcelocation : this.sndRegistry.getKeys()) {
            SoundEventAccessorComposite soundeventaccessorcomposite = (SoundEventAccessorComposite)this.sndRegistry.getObject(resourcelocation);
            if (!ArrayUtils.contains((Object[])p_147686_1_, (Object)((Object)soundeventaccessorcomposite.getSoundCategory()))) continue;
            arraylist.add(soundeventaccessorcomposite);
        }
        if (arraylist.isEmpty()) {
            return null;
        }
        return (SoundEventAccessorComposite)arraylist.get(new Random().nextInt(arraylist.size()));
    }

    public boolean isSoundPlaying(ISound p_147692_1_) {
        return this.sndManager.isSoundPlaying(p_147692_1_);
    }

    @SideOnly(value=Side.CLIENT)
    static final class SwitchType {
        static final int[] field_148765_a = new int[SoundList.SoundEntry.Type.values().length];
        private static final String __OBFID = "CL_00001150";

        SwitchType() {
        }

        static {
            try {
                SwitchType.field_148765_a[SoundList.SoundEntry.Type.FILE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                SwitchType.field_148765_a[SoundList.SoundEntry.Type.SOUND_EVENT.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
        }
    }
}

