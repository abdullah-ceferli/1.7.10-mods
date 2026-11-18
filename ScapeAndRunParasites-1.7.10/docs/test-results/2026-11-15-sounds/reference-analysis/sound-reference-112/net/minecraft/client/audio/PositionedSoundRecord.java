/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class PositionedSoundRecord
extends PositionedSound {
    public PositionedSoundRecord(SoundEvent soundEvent, SoundCategory soundCategory, float f, float f2, BlockPos blockPos) {
        this(soundEvent, soundCategory, f, f2, (float)blockPos.func_177958_n() + 0.5f, (float)blockPos.func_177956_o() + 0.5f, (float)blockPos.func_177952_p() + 0.5f);
    }

    public static PositionedSoundRecord func_184371_a(SoundEvent soundEvent, float f) {
        return PositionedSoundRecord.func_194007_a(soundEvent, f, 0.25f);
    }

    public static PositionedSoundRecord func_194007_a(SoundEvent soundEvent, float f, float f2) {
        return new PositionedSoundRecord(soundEvent, SoundCategory.MASTER, f2, f, false, 0, ISound.AttenuationType.NONE, 0.0f, 0.0f, 0.0f);
    }

    public static PositionedSoundRecord func_184370_a(SoundEvent soundEvent) {
        return new PositionedSoundRecord(soundEvent, SoundCategory.MUSIC, 1.0f, 1.0f, false, 0, ISound.AttenuationType.NONE, 0.0f, 0.0f, 0.0f);
    }

    public static PositionedSoundRecord func_184372_a(SoundEvent soundEvent, float f, float f2, float f3) {
        return new PositionedSoundRecord(soundEvent, SoundCategory.RECORDS, 4.0f, 1.0f, false, 0, ISound.AttenuationType.LINEAR, f, f2, f3);
    }

    public PositionedSoundRecord(SoundEvent soundEvent, SoundCategory soundCategory, float f, float f2, float f3, float f4, float f5) {
        this(soundEvent, soundCategory, f, f2, false, 0, ISound.AttenuationType.LINEAR, f3, f4, f5);
    }

    private PositionedSoundRecord(SoundEvent soundEvent, SoundCategory soundCategory, float f, float f2, boolean bl, int n, ISound.AttenuationType attenuationType, float f3, float f4, float f5) {
        this(soundEvent.func_187503_a(), soundCategory, f, f2, bl, n, attenuationType, f3, f4, f5);
    }

    public PositionedSoundRecord(ResourceLocation resourceLocation, SoundCategory soundCategory, float f, float f2, boolean bl, int n, ISound.AttenuationType attenuationType, float f3, float f4, float f5) {
        super(resourceLocation, soundCategory);
        this.field_147662_b = f;
        this.field_147663_c = f2;
        this.field_147660_d = f3;
        this.field_147661_e = f4;
        this.field_147658_f = f5;
        this.field_147659_g = bl;
        this.field_147665_h = n;
        this.field_147666_i = attenuationType;
    }
}

