/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.util.ResourceLocation;

@SideOnly(value=Side.CLIENT)
public class SoundEventAccessorComposite
implements ISoundEventAccessor {
    private final List soundPool = Lists.newArrayList();
    private final Random rnd = new Random();
    private final ResourceLocation field_148735_c;
    private final SoundCategory field_148732_d;
    private double eventPitch;
    private double eventVolume;
    private static final String __OBFID = "CL_00001146";

    public SoundEventAccessorComposite(ResourceLocation p_i45120_1_, double p_i45120_2_, double p_i45120_4_, SoundCategory p_i45120_6_) {
        this.field_148735_c = p_i45120_1_;
        this.eventVolume = p_i45120_4_;
        this.eventPitch = p_i45120_2_;
        this.field_148732_d = p_i45120_6_;
    }

    @Override
    public int func_148721_a() {
        int i = 0;
        for (ISoundEventAccessor isoundeventaccessor : this.soundPool) {
            i += isoundeventaccessor.func_148721_a();
        }
        return i;
    }

    @Override
    public SoundPoolEntry func_148720_g() {
        int i = this.func_148721_a();
        if (!this.soundPool.isEmpty() && i != 0) {
            ISoundEventAccessor isoundeventaccessor;
            int j = this.rnd.nextInt(i);
            Iterator iterator = this.soundPool.iterator();
            do {
                if (iterator.hasNext()) continue;
                return SoundHandler.missing_sound;
            } while ((j -= (isoundeventaccessor = (ISoundEventAccessor)iterator.next()).func_148721_a()) >= 0);
            SoundPoolEntry soundpoolentry = (SoundPoolEntry)isoundeventaccessor.func_148720_g();
            soundpoolentry.setPitch(soundpoolentry.getPitch() * this.eventPitch);
            soundpoolentry.setVolume(soundpoolentry.getVolume() * this.eventVolume);
            return soundpoolentry;
        }
        return SoundHandler.missing_sound;
    }

    public void addSoundToEventPool(ISoundEventAccessor p_148727_1_) {
        this.soundPool.add(p_148727_1_);
    }

    public ResourceLocation getSoundEventLocation() {
        return this.field_148735_c;
    }

    public SoundCategory getSoundCategory() {
        return this.field_148732_d;
    }
}

