/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  io.netty.util.internal.ThreadLocalRandom
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 *  paulscode.sound.SoundSystem
 *  paulscode.sound.SoundSystemConfig
 *  paulscode.sound.SoundSystemException
 *  paulscode.sound.SoundSystemLogger
 *  paulscode.sound.Source
 *  paulscode.sound.codecs.CodecJOrbis
 *  paulscode.sound.libraries.LibraryLWJGLOpenAL
 */
package net.minecraft.client.audio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {
    private static final Marker field_148623_a = MarkerManager.getMarker((String)"SOUNDS");
    private static final Logger field_148621_b = LogManager.getLogger();
    private static final Set<ResourceLocation> field_188775_c = Sets.newHashSet();
    private final SoundHandler field_148622_c;
    private final GameSettings field_148619_d;
    private SoundSystemStarterThread field_148620_e;
    private boolean field_148617_f;
    private int field_148618_g;
    private final Map<String, ISound> field_148629_h = HashBiMap.create();
    private final Map<ISound, String> field_148630_i = ((BiMap)this.field_148629_h).inverse();
    private final Multimap<SoundCategory, String> field_188776_k = HashMultimap.create();
    private final List<ITickableSound> field_148625_l = Lists.newArrayList();
    private final Map<ISound, Integer> field_148626_m = Maps.newHashMap();
    private final Map<String, Integer> field_148624_n = Maps.newHashMap();
    private final List<ISoundEventListener> field_188777_o = Lists.newArrayList();
    private final List<String> field_189000_p = Lists.newArrayList();

    public SoundManager(SoundHandler soundHandler, GameSettings gameSettings) {
        this.field_148622_c = soundHandler;
        this.field_148619_d = gameSettings;
        try {
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec((String)"ogg", CodecJOrbis.class);
        }
        catch (SoundSystemException soundSystemException) {
            field_148621_b.error(field_148623_a, "Error linking with the LibraryJavaSound plug-in", (Throwable)soundSystemException);
        }
    }

    public void func_148596_a() {
        field_188775_c.clear();
        for (SoundEvent soundEvent : SoundEvent.field_187505_a) {
            ResourceLocation resourceLocation = soundEvent.func_187503_a();
            if (this.field_148622_c.func_184398_a(resourceLocation) != null) continue;
            field_148621_b.warn("Missing sound for event: {}", (Object)SoundEvent.field_187505_a.func_177774_c(soundEvent));
            field_188775_c.add(resourceLocation);
        }
        this.func_148613_b();
        this.func_148608_i();
    }

    private synchronized void func_148608_i() {
        if (this.field_148617_f) {
            return;
        }
        try {
            new Thread(new Runnable(){

                @Override
                public void run() {
                    SoundSystemConfig.setLogger((SoundSystemLogger)new SoundSystemLogger(){

                        public void message(String string, int n) {
                            if (!string.isEmpty()) {
                                field_148621_b.info(string);
                            }
                        }

                        public void importantMessage(String string, int n) {
                            if (!string.isEmpty()) {
                                field_148621_b.warn(string);
                            }
                        }

                        public void errorMessage(String string, String string2, int n) {
                            if (!string2.isEmpty()) {
                                field_148621_b.error("Error in class '{}'", (Object)string);
                                field_148621_b.error(string2);
                            }
                        }
                    });
                    SoundManager.this.field_148620_e = new SoundSystemStarterThread();
                    SoundManager.this.field_148617_f = true;
                    SoundManager.this.field_148620_e.setMasterVolume(SoundManager.this.field_148619_d.func_186711_a(SoundCategory.MASTER));
                    field_148621_b.info(field_148623_a, "Sound engine started");
                }
            }, "Sound Library Loader").start();
        }
        catch (RuntimeException runtimeException) {
            field_148621_b.error(field_148623_a, "Error starting SoundSystem. Turning off sounds & music", (Throwable)runtimeException);
            this.field_148619_d.func_186712_a(SoundCategory.MASTER, 0.0f);
            this.field_148619_d.func_74303_b();
        }
    }

    private float func_188769_a(SoundCategory soundCategory) {
        if (soundCategory == null || soundCategory == SoundCategory.MASTER) {
            return 1.0f;
        }
        return this.field_148619_d.func_186711_a(soundCategory);
    }

    public void func_188771_a(SoundCategory soundCategory, float f) {
        if (!this.field_148617_f) {
            return;
        }
        if (soundCategory == SoundCategory.MASTER) {
            this.field_148620_e.setMasterVolume(f);
            return;
        }
        for (String string : this.field_188776_k.get((Object)soundCategory)) {
            ISound iSound = this.field_148629_h.get(string);
            float \u26032 = this.func_188770_e(iSound);
            if (\u26032 <= 0.0f) {
                this.func_148602_b(iSound);
                continue;
            }
            this.field_148620_e.setVolume(string, \u26032);
        }
    }

    public void func_148613_b() {
        if (this.field_148617_f) {
            this.func_148614_c();
            this.field_148620_e.cleanup();
            this.field_148617_f = false;
        }
    }

    public void func_148614_c() {
        if (this.field_148617_f) {
            for (String string : this.field_148629_h.keySet()) {
                this.field_148620_e.stop(string);
            }
            this.field_148629_h.clear();
            this.field_148626_m.clear();
            this.field_148625_l.clear();
            this.field_188776_k.clear();
            this.field_148624_n.clear();
        }
    }

    public void func_188774_a(ISoundEventListener iSoundEventListener) {
        this.field_188777_o.add(iSoundEventListener);
    }

    public void func_188773_b(ISoundEventListener iSoundEventListener) {
        this.field_188777_o.remove(iSoundEventListener);
    }

    public void func_148605_d() {
        Map.Entry<ISound, Integer> entry;
        ++this.field_148618_g;
        for (ITickableSound iTickableSound : this.field_148625_l) {
            iTickableSound.func_73660_a();
            if (iTickableSound.func_147667_k()) {
                this.func_148602_b(iTickableSound);
                continue;
            }
            String \u26032 = this.field_148630_i.get(iTickableSound);
            this.field_148620_e.setVolume(\u26032, this.func_188770_e(iTickableSound));
            this.field_148620_e.setPitch(\u26032, this.func_188772_d(iTickableSound));
            this.field_148620_e.setPosition(\u26032, iTickableSound.func_147649_g(), iTickableSound.func_147654_h(), iTickableSound.func_147651_i());
        }
        Iterator<Map.Entry<String, ISound>> iterator = this.field_148629_h.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ISound> entry2 = iterator.next();
            entry = entry2.getKey();
            ISound \u26033 = entry2.getValue();
            if (this.field_148620_e.playing((String)((Object)entry)) || (\u2603 = this.field_148624_n.get(entry).intValue()) > this.field_148618_g) continue;
            int \u26034 = \u26033.func_147652_d();
            if (\u26033.func_147657_c() && \u26034 > 0) {
                this.field_148626_m.put(\u26033, this.field_148618_g + \u26034);
            }
            iterator.remove();
            field_148621_b.debug(field_148623_a, "Removed channel {} because it's not playing anymore", (Object)entry);
            this.field_148620_e.removeSource((String)((Object)entry));
            this.field_148624_n.remove(entry);
            try {
                this.field_188776_k.remove((Object)\u26033.func_184365_d(), (Object)entry);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
            if (!(\u26033 instanceof ITickableSound)) continue;
            this.field_148625_l.remove(\u26033);
        }
        Iterator<Map.Entry<ISound, Integer>> iterator2 = this.field_148626_m.entrySet().iterator();
        while (iterator2.hasNext()) {
            entry = iterator2.next();
            if (this.field_148618_g < (Integer)entry.getValue()) continue;
            ISound \u26035 = (ISound)entry.getKey();
            if (\u26035 instanceof ITickableSound) {
                ((ITickableSound)\u26035).func_73660_a();
            }
            this.func_148611_c(\u26035);
            iterator2.remove();
        }
    }

    public boolean func_148597_a(ISound iSound) {
        if (!this.field_148617_f) {
            return false;
        }
        String string = this.field_148630_i.get(iSound);
        if (string == null) {
            return false;
        }
        return this.field_148620_e.playing(string) || this.field_148624_n.containsKey(string) && this.field_148624_n.get(string) <= this.field_148618_g;
    }

    public void func_148602_b(ISound iSound) {
        if (!this.field_148617_f) {
            return;
        }
        String string = this.field_148630_i.get(iSound);
        if (string != null) {
            this.field_148620_e.stop(string);
        }
    }

    public void func_148611_c(ISound iSound2) {
        ISound iSound2;
        if (!this.field_148617_f) {
            return;
        }
        SoundEventAccessor soundEventAccessor = iSound2.func_184366_a(this.field_148622_c);
        ResourceLocation \u26032 = iSound2.func_147650_b();
        if (soundEventAccessor == null) {
            if (field_188775_c.add(\u26032)) {
                field_148621_b.warn(field_148623_a, "Unable to play unknown soundEvent: {}", (Object)\u26032);
            }
            return;
        }
        if (!this.field_188777_o.isEmpty()) {
            for (ISoundEventListener iSoundEventListener : this.field_188777_o) {
                iSoundEventListener.func_184067_a(iSound2, soundEventAccessor);
            }
        }
        if (this.field_148620_e.getMasterVolume() <= 0.0f) {
            field_148621_b.debug(field_148623_a, "Skipped playing soundEvent: {}, master volume was zero", (Object)\u26032);
            return;
        }
        Sound \u26033 = iSound2.func_184364_b();
        if (\u26033 == SoundHandler.field_147700_a) {
            if (field_188775_c.add(\u26032)) {
                field_148621_b.warn(field_148623_a, "Unable to play empty soundEvent: {}", (Object)\u26032);
            }
            return;
        }
        float \u26034 = iSound2.func_147653_e();
        float \u26035 = 16.0f;
        if (\u26034 > 1.0f) {
            \u26035 *= \u26034;
        }
        SoundCategory \u26036 = iSound2.func_184365_d();
        float \u26037 = this.func_188770_e(iSound2);
        float \u26038 = this.func_188772_d(iSound2);
        if (\u26037 == 0.0f) {
            field_148621_b.debug(field_148623_a, "Skipped playing sound {}, volume was zero.", (Object)\u26033.func_188719_a());
            return;
        }
        boolean \u26039 = iSound2.func_147657_c() && iSound2.func_147652_d() == 0;
        String \u260310 = MathHelper.func_180182_a((Random)ThreadLocalRandom.current()).toString();
        ResourceLocation \u260311 = \u26033.func_188721_b();
        if (\u26033.func_188723_h()) {
            this.field_148620_e.newStreamingSource(false, \u260310, SoundManager.func_148612_a(\u260311), \u260311.toString(), \u26039, iSound2.func_147649_g(), iSound2.func_147654_h(), iSound2.func_147651_i(), iSound2.func_147656_j().func_148586_a(), \u26035);
        } else {
            this.field_148620_e.newSource(false, \u260310, SoundManager.func_148612_a(\u260311), \u260311.toString(), \u26039, iSound2.func_147649_g(), iSound2.func_147654_h(), iSound2.func_147651_i(), iSound2.func_147656_j().func_148586_a(), \u26035);
        }
        field_148621_b.debug(field_148623_a, "Playing sound {} for event {} as channel {}", (Object)\u26033.func_188719_a(), (Object)\u26032, (Object)\u260310);
        this.field_148620_e.setPitch(\u260310, \u26038);
        this.field_148620_e.setVolume(\u260310, \u26037);
        this.field_148620_e.play(\u260310);
        this.field_148624_n.put(\u260310, this.field_148618_g + 20);
        this.field_148629_h.put(\u260310, iSound2);
        this.field_188776_k.put((Object)\u26036, (Object)\u260310);
        if (iSound2 instanceof ITickableSound) {
            this.field_148625_l.add((ITickableSound)iSound2);
        }
    }

    private float func_188772_d(ISound iSound) {
        return MathHelper.func_76131_a(iSound.func_147655_f(), 0.5f, 2.0f);
    }

    private float func_188770_e(ISound iSound) {
        return MathHelper.func_76131_a(iSound.func_147653_e() * this.func_188769_a(iSound.func_184365_d()), 0.0f, 1.0f);
    }

    public void func_148610_e() {
        for (Map.Entry<String, ISound> entry : this.field_148629_h.entrySet()) {
            String string = entry.getKey();
            boolean \u26032 = this.func_148597_a(entry.getValue());
            if (!\u26032) continue;
            field_148621_b.debug(field_148623_a, "Pausing channel {}", (Object)string);
            this.field_148620_e.pause(string);
            this.field_189000_p.add(string);
        }
    }

    public void func_148604_f() {
        for (String string : this.field_189000_p) {
            field_148621_b.debug(field_148623_a, "Resuming channel {}", (Object)string);
            this.field_148620_e.play(string);
        }
        this.field_189000_p.clear();
    }

    public void func_148599_a(ISound iSound, int n) {
        this.field_148626_m.put(iSound, this.field_148618_g + n);
    }

    private static URL func_148612_a(final ResourceLocation resourceLocation) {
        String string = String.format("%s:%s:%s", "mcsounddomain", resourceLocation.func_110624_b(), resourceLocation.func_110623_a());
        URLStreamHandler \u26032 = new URLStreamHandler(){

            @Override
            protected URLConnection openConnection(URL uRL) {
                return new URLConnection(uRL){

                    @Override
                    public void connect() {
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return Minecraft.func_71410_x().func_110442_L().func_110536_a(resourceLocation).func_110527_b();
                    }
                };
            }
        };
        try {
            return new URL(null, string, \u26032);
        }
        catch (MalformedURLException \u26033) {
            throw new Error("TODO: Sanely handle url exception! :D");
        }
    }

    public void func_148615_a(EntityPlayer entityPlayer, float f) {
        if (!this.field_148617_f || entityPlayer == null) {
            return;
        }
        \u2603 = entityPlayer.field_70127_C + (entityPlayer.field_70125_A - entityPlayer.field_70127_C) * f;
        \u2603 = entityPlayer.field_70126_B + (entityPlayer.field_70177_z - entityPlayer.field_70126_B) * f;
        double d = entityPlayer.field_70169_q + (entityPlayer.field_70165_t - entityPlayer.field_70169_q) * (double)f;
        \u2603 = entityPlayer.field_70167_r + (entityPlayer.field_70163_u - entityPlayer.field_70167_r) * (double)f + (double)entityPlayer.func_70047_e();
        \u2603 = entityPlayer.field_70166_s + (entityPlayer.field_70161_v - entityPlayer.field_70166_s) * (double)f;
        float \u26032 = MathHelper.func_76134_b((\u2603 + 90.0f) * ((float)Math.PI / 180));
        float \u26033 = MathHelper.func_76126_a((\u2603 + 90.0f) * ((float)Math.PI / 180));
        float \u26034 = MathHelper.func_76134_b(-\u2603 * ((float)Math.PI / 180));
        float \u26035 = MathHelper.func_76126_a(-\u2603 * ((float)Math.PI / 180));
        float \u26036 = MathHelper.func_76134_b((-\u2603 + 90.0f) * ((float)Math.PI / 180));
        float \u26037 = MathHelper.func_76126_a((-\u2603 + 90.0f) * ((float)Math.PI / 180));
        float \u26038 = \u26032 * \u26034;
        float \u26039 = \u26035;
        float \u260310 = \u26033 * \u26034;
        float \u260311 = \u26032 * \u26036;
        float \u260312 = \u26037;
        float \u260313 = \u26033 * \u26036;
        this.field_148620_e.setListenerPosition((float)d, (float)\u2603, (float)\u2603);
        this.field_148620_e.setListenerOrientation(\u26038, \u26039, \u260310, \u260311, \u260312, \u260313);
    }

    public void func_189567_a(String string2, SoundCategory soundCategory) {
        String string2;
        if (soundCategory != null) {
            for (String string3 : this.field_188776_k.get((Object)soundCategory)) {
                ISound iSound = this.field_148629_h.get(string3);
                if (string2.isEmpty()) {
                    this.func_148602_b(iSound);
                    continue;
                }
                if (!iSound.func_147650_b().equals(new ResourceLocation(string2))) continue;
                this.func_148602_b(iSound);
            }
        } else if (string2.isEmpty()) {
            this.func_148614_c();
        } else {
            for (ISound iSound : this.field_148629_h.values()) {
                if (!iSound.func_147650_b().equals(new ResourceLocation(string2))) continue;
                this.func_148602_b(iSound);
            }
        }
    }

    class SoundSystemStarterThread
    extends SoundSystem {
        private SoundSystemStarterThread() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean playing(String string) {
            Object object = SoundSystemConfig.THREAD_SYNC;
            synchronized (object) {
                if (this.soundLibrary == null) {
                    return false;
                }
                Source source = (Source)this.soundLibrary.getSources().get(string);
                if (source == null) {
                    return false;
                }
                return source.playing() || source.paused() || source.preLoad;
            }
        }
    }
}

