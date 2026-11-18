/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.ISoundEventListener
 *  net.minecraft.client.audio.SoundEventAccessor
 *  net.minecraft.client.gui.GuiSubtitleOverlay
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.client.gui.ScaledResolution;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;

public class DistortedGuiSubtitleOverlay
extends GuiSubtitleOverlay {
    private final Minecraft client;
    private final List<Subtitle> subtitles = Lists.newArrayList();
    private boolean enabled;

    public DistortedGuiSubtitleOverlay(Minecraft clientIn) {
        super(clientIn);
        this.client = clientIn;
    }

    public void renderSubtitles(ScaledResolution resolution) {
        if (!this.enabled && this.client.gameSettings.showSubtitles) {
            this.client.getSoundHandler().addListener((ISoundEventListener)this);
            this.enabled = true;
        } else if (this.enabled && !this.client.gameSettings.showSubtitles) {
            this.client.getSoundHandler().removeListener((ISoundEventListener)this);
            this.enabled = false;
        }
        if (this.enabled && !this.subtitles.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            Vec3d vec3d = new Vec3d(this.client.thePlayer.posX, this.client.thePlayer.posY + (double)this.client.thePlayer.getEyeHeight(), this.client.thePlayer.posZ);
            Vec3d vec3d1 = new Vec3d(0.0, 0.0, -1.0).rotatePitch(-this.client.thePlayer.rotationPitch * ((float)Math.PI / 180)).rotateYaw(-this.client.thePlayer.rotationYaw * ((float)Math.PI / 180));
            Vec3d vec3d2 = new Vec3d(0.0, 1.0, 0.0).rotatePitch(-this.client.thePlayer.rotationPitch * ((float)Math.PI / 180)).rotateYaw(-this.client.thePlayer.rotationYaw * ((float)Math.PI / 180));
            Vec3d vec3d3 = vec3d1.crossProduct(vec3d2);
            int i = 0;
            int j = 0;
            Iterator<Subtitle> iterator = this.subtitles.iterator();
            while (iterator.hasNext()) {
                Subtitle subtitle = iterator.next();
                if (subtitle.getStartTime() + 3000L <= Minecraft.getSystemTime()) {
                    iterator.remove();
                    continue;
                }
                String measure = GuiDistortionHelper.shouldDistortSubtitles(this.client) ? GuiDistortionHelper.jamText(subtitle.getString()) : subtitle.getString();
                j = Math.max(j, this.client.fontRenderer.getStringWidth(measure));
            }
            j = j + this.client.fontRenderer.getStringWidth("<") + this.client.fontRenderer.getStringWidth(" ") + this.client.fontRenderer.getStringWidth(">") + this.client.fontRenderer.getStringWidth(" ");
            for (Subtitle subtitle : this.subtitles) {
                String s = subtitle.getString();
                if (GuiDistortionHelper.shouldDistortSubtitles(this.client)) {
                    s = GuiDistortionHelper.jamText(s);
                }
                Vec3d vec3d4 = subtitle.getLocation().subtract(vec3d).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -vec3d1.dotProduct(vec3d4);
                boolean flag = d1 > 0.5;
                int l = j / 2;
                int i1 = this.client.fontRenderer.FONT_HEIGHT;
                int j1 = i1 / 2;
                int k1 = this.client.fontRenderer.getStringWidth(s);
                int l1 = MathHelper.floor_double((double)MathHelper.denormalizeClamp((double)255.0, (double)75.0, (double)((float)(Minecraft.getSystemTime() - subtitle.getStartTime()) / 3000.0f)));
                int i2 = l1 << 16 | l1 << 8 | l1;
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)((float)resolution.getScaledWidth() - (float)l - 2.0f), (float)((float)(resolution.getScaledHeight() - 30) - (float)(i * (i1 + 1))), (float)0.0f);
                DistortedGuiSubtitleOverlay.drawRect((int)(-l - 1), (int)(-j1 - 1), (int)(l + 1), (int)(j1 + 1), (int)-872415232);
                GlStateManager.enableBlend();
                if (!flag) {
                    if (d0 > 0.0) {
                        this.client.fontRenderer.drawString(">", l - this.client.fontRenderer.getStringWidth(">"), -j1, i2 + -16777216);
                    } else if (d0 < 0.0) {
                        this.client.fontRenderer.drawString("<", -l, -j1, i2 + -16777216);
                    }
                }
                this.client.fontRenderer.drawString(s, -k1 / 2, -j1, i2 + -16777216);
                GlStateManager.popMatrix();
                ++i;
            }
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public void soundPlay(ISound soundIn, SoundEventAccessor accessor) {
        if (accessor.getSubtitle() != null) {
            String s = accessor.getSubtitle().getFormattedText();
            for (Subtitle subtitle : this.subtitles) {
                if (!subtitle.getString().equals(s)) continue;
                subtitle.refresh(new Vec3d((double)soundIn.getXPosF(), (double)soundIn.getYPosF(), (double)soundIn.getZPosF()));
                return;
            }
            this.subtitles.add(new Subtitle(s, new Vec3d((double)soundIn.getXPosF(), (double)soundIn.getYPosF(), (double)soundIn.getZPosF())));
        }
    }

    private static class Subtitle {
        private final String subtitle;
        private long startTime;
        private Vec3d location;

        public Subtitle(String subtitleIn, Vec3d locationIn) {
            this.subtitle = subtitleIn;
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }

        public String getString() {
            return this.subtitle;
        }

        public long getStartTime() {
            return this.startTime;
        }

        public Vec3d getLocation() {
            return this.location;
        }

        public void refresh(Vec3d locationIn) {
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }
    }
}

