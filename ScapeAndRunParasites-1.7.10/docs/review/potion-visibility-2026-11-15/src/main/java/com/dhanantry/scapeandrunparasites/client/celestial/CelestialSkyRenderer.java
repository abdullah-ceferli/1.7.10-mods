/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 */
package com.dhanantry.scapeandrunparasites.client.celestial;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.net.BestiaryNetwork;
import com.dhanantry.scapeandrunparasites.bestiary.net.PacketBestiarySeenCelestial;
import com.dhanantry.scapeandrunparasites.client.celestial.BlackSkyClient;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectDefinition;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialPhaseClient;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="srparasites", value={Side.CLIENT})
public final class CelestialSkyRenderer {
    private static final double SKY_RADIUS = 180.0;
    private static final float ORBIT_PERIOD_TICKS = 12000.0f;
    private static final boolean DEBUG = false;
    private static boolean warnedOptifineDarkDays = false;
    private static final Map<String, Long> ORBIT_STARTS = new HashMap<String, Long>();
    private static long lastDayTime = 0L;
    private static final Set<String> SENT = new HashSet<String>();

    private CelestialSkyRenderer() {
    }

    private static boolean hasFieldGuide(EntityPlayer p) {
        if (p == null) {
            return false;
        }
        Item guide = SRPItems.SRP_FIELD_GUIDE;
        for (ItemStack st : p.inventory.mainInventory) {
            if (st.isEmpty() || st.getItem() != guide) continue;
            return true;
        }
        for (ItemStack st : p.inventory.offHandInventory) {
            if (st.isEmpty() || st.getItem() != guide) continue;
            return true;
        }
        return false;
    }

    private static void recordSeenOnce(Minecraft mc, String id) {
        if (id == null) {
            return;
        }
        if (mc == null || mc.thePlayer == null) {
            return;
        }
        if (!CelestialSkyRenderer.hasFieldGuide((EntityPlayer)mc.thePlayer)) {
            return;
        }
        IBestiaryProgress prog = (IBestiaryProgress)mc.thePlayer.getCapability(BestiaryCapability.CAP, null);
        if (prog != null && prog.hasSeenCelestial(id)) {
            return;
        }
        if (SENT.contains(id)) {
            return;
        }
        SENT.add(id);
        BestiaryNetwork.CH.sendToServer((IMessage)new PacketBestiarySeenCelestial(id));
    }

    public static void clearSeenCacheClient() {
        SENT.clear();
    }

    private static Vec3d dirFromYawPitch(float yawDeg, float pitchDeg) {
        float yaw = (float)Math.toRadians(yawDeg);
        float pitch = (float)Math.toRadians(pitchDeg);
        float x = -MathHelper.sin((float)yaw) * MathHelper.cos((float)pitch);
        float y = MathHelper.sin((float)pitch);
        float z = MathHelper.cos((float)yaw) * MathHelper.cos((float)pitch);
        return new Vec3d((double)x, (double)y, (double)z);
    }

    private static float getCelestialMovementSpeedMultiplier() {
        return MathHelper.clamp_float((float)((float)SRPConfigWorld.celestialMovementSpeedMultiplier), (float)0.01f, (float)100.0f);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent e) {
        if (!SRPConfigWorld.enableCelestialObjects) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.theWorld;
        EntityPlayerSP player = mc.thePlayer;
        if (world == null || player == null) {
            return;
        }
        if (!world.provider.isSurfaceWorld()) {
            return;
        }
        float partialTicks = e.getPartialTicks();
        long totalTime = world.getWorldTime();
        long dayTime = totalTime % 24000L;
        if (BlackSkyClient.isBlackSkyActive()) {
            CelestialSkyRenderer.recordSeenOnce(mc, "dark_days");
            if (CelestialSkyRenderer.isSRPDarkDaysShaderPackActive()) {
                return;
            }
            if (CelestialSkyRenderer.isOptifineInstalled()) {
                if (!warnedOptifineDarkDays && mc.thePlayer != null) {
                    ChatComponentTranslation msg = new ChatComponentTranslation("message.srparasites.dark_days.optifine_visuals_disabled", new Object[0]);
                    msg.getChatStyle().setColor(EnumChatFormatting.DARK_PURPLE);
                    mc.thePlayer.addChatMessage((IChatComponent)msg);
                    warnedOptifineDarkDays = true;
                }
                return;
            }
            CelestialSkyRenderer.renderBlackSkyDome(mc);
            return;
        }
        warnedOptifineDarkDays = false;
        if (dayTime < lastDayTime) {
            ORBIT_STARTS.clear();
        }
        lastDayTime = dayTime;
        int dim = world.provider.dimensionId;
        int phase = CelestialPhaseClient.getPhase(dim);
        float celestialAngle = world.getCelestialAngle(partialTicks);
        float starBrightness = world.getStarBrightness(partialTicks);
        if (starBrightness <= 0.0f) {
            return;
        }
        float rainStrength = world.getRainStrength(partialTicks);
        if (rainStrength > 0.0f || world.isThundering()) {
            return;
        }
        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;
        long worldTime = world.getTotalWorldTime();
        float movementSpeed = CelestialSkyRenderer.getCelestialMovementSpeedMultiplier();
        double motionTime = ((float)worldTime + partialTicks) * movementSpeed;
        GlStateManager.pushMatrix();
        try {
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc((int)516, (float)0.1f);
            GlStateManager.enableDepth();
            GlStateManager.depthMask((boolean)false);
            GlStateManager.depthFunc((int)515);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            for (CelestialObjectDefinition def : CelestialObjectRegistry.getObjects()) {
                float alpha;
                float period;
                boolean isForced = CelestialPhaseClient.isForcedTonight(dim, def.id);
                if (!isForced && !def.isPhaseAllowed(phase) || !isForced && !CelestialPhaseClient.isActiveTonight(dim, def.id)) continue;
                CelestialSkyRenderer.recordSeenOnce(mc, def.id);
                float yawDeg = def.yawDeg;
                float pitchDeg = def.pitchDeg;
                float f = period = def.orbitPeriodTicks > 0.0f ? def.orbitPeriodTicks : 12000.0f;
                if (def.orbitPath != CelestialObjectDefinition.OrbitPath.NONE) {
                    float t;
                    String orbitKey = world.provider.dimensionId + ":" + def.id;
                    if (def.oneShotOrbit) {
                        long startTime = ORBIT_STARTS.computeIfAbsent(orbitKey, k -> worldTime);
                        t = (float)((double)(((float)(worldTime - startTime) + partialTicks) * movementSpeed) / (double)period);
                        if (t >= 1.0f) {
                            continue;
                        }
                    } else {
                        t = (float)(motionTime % (double)period / (double)period);
                    }
                    switch (def.orbitPath) {
                        case RING: {
                            yawDeg = def.yawDeg + def.orbitYawRangeDeg * t;
                            pitchDeg = def.orbitPitchMinDeg;
                            break;
                        }
                        case ARC: {
                            yawDeg = def.yawDeg + def.orbitYawRangeDeg * t;
                            float arc = (float)Math.sin(Math.PI * (double)t);
                            pitchDeg = def.orbitPitchMinDeg + (def.orbitPitchMaxDeg - def.orbitPitchMinDeg) * arc;
                            break;
                        }
                    }
                } else if (def.fastStreak) {
                    float t = (float)(motionTime % 12000.0 / 12000.0);
                    yawDeg += t * 360.0f;
                }
                if (def.followsStars) {
                    float starAngleDeg = celestialAngle * 360.0f;
                    yawDeg += starAngleDeg;
                }
                if (def.rotationSpeedDeg != 0.0f) {
                    float spin = (float)(motionTime / 20.0 * (double)def.rotationSpeedDeg);
                    yawDeg += spin;
                }
                Vec3d dir = CelestialSkyRenderer.dirFromYawPitch(yawDeg, pitchDeg);
                double wx = camX + dir.xCoord * 180.0;
                double wy = camY + dir.yCoord * 180.0;
                double wz = camZ + dir.zCoord * 180.0;
                mc.getTextureManager().bindTexture(def.texture);
                float u0 = 0.0f;
                float u1 = 1.0f;
                float v0 = 0.0f;
                float v1 = 1.0f;
                if (def.animated && def.frameCount > 1) {
                    int frame = (int)(worldTime / (long)def.frameTimeTicks % (long)def.frameCount);
                    float frameH = 1.0f / (float)def.frameCount;
                    v0 = (float)frame * frameH;
                    v1 = v0 + frameH;
                }
                if ((alpha = MathHelper.clamp_float((float)(def.baseOpacity * starBrightness), (float)0.0f, (float)1.0f)) <= 0.001f) continue;
                float size = def.size;
                Vec3d worldUp = new Vec3d(0.0, 1.0, 0.0);
                Vec3d side = worldUp.crossProduct(dir);
                if (side.lengthSquared() < 1.0E-4) {
                    worldUp = new Vec3d(0.0, 0.0, 1.0);
                    side = worldUp.crossProduct(dir);
                }
                side = side.normalize();
                Vec3d upVec = dir.crossProduct(side).normalize();
                double half = size;
                Vec3d center = new Vec3d(wx, wy, wz);
                Vec3d p0 = center.add(side.scale(-half)).add(upVec.scale(-half));
                Vec3d p1 = center.add(side.scale(half)).add(upVec.scale(-half));
                Vec3d p2 = center.add(side.scale(half)).add(upVec.scale(half));
                Vec3d p3 = center.add(side.scale(-half)).add(upVec.scale(half));
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
                Tessellator tess = Tessellator.getInstance();
                BufferBuilder buf = tess.getBuffer();
                float prevLmX = OpenGlHelper.lastBrightnessX;
                float prevLmY = OpenGlHelper.lastBrightnessY;
                OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
                GlStateManager.disableAlpha();
                buf.begin(7, DefaultVertexFormats.POSITION_TEX);
                buf.pos(p0.xCoord - camX, p0.yCoord - camY, p0.zCoord - camZ).tex((double)u0, (double)v1).endVertex();
                buf.pos(p1.xCoord - camX, p1.yCoord - camY, p1.zCoord - camZ).tex((double)u1, (double)v1).endVertex();
                buf.pos(p2.xCoord - camX, p2.yCoord - camY, p2.zCoord - camZ).tex((double)u1, (double)v0).endVertex();
                buf.pos(p3.xCoord - camX, p3.yCoord - camY, p3.zCoord - camZ).tex((double)u0, (double)v0).endVertex();
                tess.draw();
                GlStateManager.enableAlpha();
                GlStateManager.alphaFunc((int)516, (float)0.1f);
                OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)prevLmX, (float)prevLmY);
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
                GlStateManager.enableDepth();
                GlStateManager.depthMask((boolean)false);
                GlStateManager.depthFunc((int)515);
                GlStateManager.disableCull();
                GlStateManager.disableLighting();
            }
        }
        finally {
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc((int)516, (float)0.1f);
            GlStateManager.disableBlend();
            GlStateManager.tryBlendFuncSeparate((int)1, (int)0, (int)1, (int)0);
            GlStateManager.enableDepth();
            GlStateManager.depthMask((boolean)true);
            GlStateManager.depthFunc((int)515);
            GlStateManager.enableCull();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    private static boolean isOptifineInstalled() {
        try {
            Class.forName("net.optifine.Config");
            return true;
        }
        catch (Throwable throwable) {
            try {
                Class.forName("optifine.Config");
                return true;
            }
            catch (Throwable throwable2) {
                try {
                    Class.forName("optifine.OptiFineClassTransformer");
                    return true;
                }
                catch (Throwable throwable3) {
                    try {
                        Class.forName("net.optifine.shaders.Shaders");
                        return true;
                    }
                    catch (Throwable throwable4) {
                        return false;
                    }
                }
            }
        }
    }

    public static boolean isSRPDarkDaysShaderPackActive() {
        try {
            Class<?> shaders = Class.forName("net.optifine.shaders.Shaders");
            Object v = shaders.getField("currentShaderName").get(null);
            if (v instanceof String) {
                String s = ((String)v).toLowerCase(Locale.ROOT);
                return s.contains("srp_darkdays_lite") || s.contains("srp darkdays lite") || s.contains("srp_dark_days_lite") || s.contains("srp dark days lite") || s.contains("srp_darkdays") || s.contains("srp darkdays");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void renderBlackSkyDome(Minecraft mc) {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableDepth();
            GlStateManager.depthMask((boolean)false);
            GlStateManager.depthFunc((int)515);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
            GlStateManager.color((float)0.0f, (float)0.0f, (float)0.0f, (float)0.9f);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            float renderDistanceBlocks = (float)mc.gameSettings.renderDistanceChunks * 16.0f;
            float r = renderDistanceBlocks - 12.0f;
            if (r < 24.0f) {
                r = 24.0f;
            }
            if (r > 180.0f) {
                r = 180.0f;
            }
            buf.begin(7, DefaultVertexFormats.POSITION);
            buf.pos((double)(-r), (double)(-r), (double)(-r)).endVertex();
            buf.pos((double)r, (double)(-r), (double)(-r)).endVertex();
            buf.pos((double)r, (double)r, (double)(-r)).endVertex();
            buf.pos((double)(-r), (double)r, (double)(-r)).endVertex();
            buf.pos((double)r, (double)(-r), (double)r).endVertex();
            buf.pos((double)(-r), (double)(-r), (double)r).endVertex();
            buf.pos((double)(-r), (double)r, (double)r).endVertex();
            buf.pos((double)r, (double)r, (double)r).endVertex();
            buf.pos((double)(-r), (double)(-r), (double)r).endVertex();
            buf.pos((double)(-r), (double)(-r), (double)(-r)).endVertex();
            buf.pos((double)(-r), (double)r, (double)(-r)).endVertex();
            buf.pos((double)(-r), (double)r, (double)r).endVertex();
            buf.pos((double)r, (double)(-r), (double)(-r)).endVertex();
            buf.pos((double)r, (double)(-r), (double)r).endVertex();
            buf.pos((double)r, (double)r, (double)r).endVertex();
            buf.pos((double)r, (double)r, (double)(-r)).endVertex();
            buf.pos((double)(-r), (double)r, (double)(-r)).endVertex();
            buf.pos((double)r, (double)r, (double)(-r)).endVertex();
            buf.pos((double)r, (double)r, (double)r).endVertex();
            buf.pos((double)(-r), (double)r, (double)r).endVertex();
            tess.draw();
        }
        finally {
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.depthFunc((int)515);
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }
    }
}

