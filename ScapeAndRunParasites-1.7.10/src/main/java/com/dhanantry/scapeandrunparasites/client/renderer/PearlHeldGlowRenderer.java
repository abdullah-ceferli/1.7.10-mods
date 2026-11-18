/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.RenderSpecificHandEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 */
package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.EntityLivingBase;
import com.dhanantry.scapeandrunparasites.backport.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="srparasites", value={Side.CLIENT})
public final class PearlHeldGlowRenderer {
    private static final ResourceLocation KEY_PEARL_STATE = new ResourceLocation("srparasites", "pearl_state");
    private static final float GLOW_BASE_ALPHA = 0.4f;
    private static final float GLOW_R = 0.85f;
    private static final float GLOW_G = 0.95f;
    private static final float GLOW_B = 1.0f;
    private static final int GLOW_LAYERS = 2;
    private static final int GLOW_SAMPLES = 16;
    private static final float GLOW_BASE_RADIUS = 0.0075f;
    private static final float GLOW_RADIUS_STEP = 0.0035f;
    private static final float GLOW_TIME_SPEED = 0.44f;
    private static final float GLOW_ANGLE_WOBBLE = 0.24f;
    private static final float GLOW_RADIUS_JITTER = 0.0032f;
    private static final float VIBE_BASE_AMPLITUDE = 0.0012f;
    private static final float VIBE_FREQ_A = 19.0f;
    private static final float VIBE_FREQ_B = 31.0f;
    private static final float GLITCH_CHANCE_PER_TICK = 0.09f;
    private static final int GLITCH_MIN_FRAMES = 2;
    private static final int GLITCH_MAX_FRAMES = 5;
    private static final int GLITCH_PASSES = 7;
    private static final float GLITCH_ALPHA = 0.6f;
    private static final float GLITCH_OFFSET_MAX = 0.016f;
    private static final float GLITCH_ROT_MAX_DEG = 14.0f;
    private static final float GLITCH_SCALE_MIN = 1.0f;
    private static final float GLITCH_SCALE_MAX = 1.09f;
    private static final float GLITCH_SHAKE_FREQ = 27.0f;
    private static final float GLITCH_SHAKE_BASE = 0.0045f;
    private static int glitchFrames = 0;
    private static int lastTickSeen = -1;

    private static float ampForState(int s) {
        switch (s) {
            case 1: {
                return 1.0f;
            }
            case 2: {
                return 1.65f;
            }
            case 3: {
                return 2.4f;
            }
        }
        return 0.35f;
    }

    @SubscribeEvent
    public static void onRenderSpecificHand(RenderSpecificHandEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return;
        }
        if (mc.gameSettings.thirdPersonView != 0) {
            return;
        }
        ItemStack stack = e.getItemStack();
        if (stack.isEmpty() || stack.getItem() != SRPItems.pearl) {
            return;
        }
        int state = 0;
        IItemPropertyGetter getter = com.dhanantry.scapeandrunparasites.backport.item.ItemProperties.get(SRPItems.pearl, KEY_PEARL_STATE);
        if (getter != null) {
            state = MathHelper.clamp_int((int)((int)getter.apply(stack, (World)mc.theWorld, (EntityLivingBase)mc.thePlayer)), (int)0, (int)3);
        }
        float amp = PearlHeldGlowRenderer.ampForState(state);
        int tick = mc.thePlayer.ticksExisted;
        if (tick != lastTickSeen) {
            lastTickSeen = tick;
            float chance = 0.09f * (0.75f + 0.5f * amp);
            if (glitchFrames <= 0 && mc.theWorld.rand.nextFloat() < chance) {
                glitchFrames = 2 + mc.theWorld.rand.nextInt(4);
            }
        }
        EntityPlayerSP player = mc.thePlayer;
        ItemRenderer ir = mc.getItemRenderer();
        float pt = e.getPartialTicks();
        float pitch = e.getInterpolatedPitch();
        EnumHand hand = e.getHand();
        float swing = e.getSwingProgress();
        float equip = e.getEquipProgress();
        e.setCanceled(true);
        ir.renderItemInFirstPerson((AbstractClientPlayer)player, pt, pitch, hand, swing, stack, equip);
        float t = ((float)player.ticksExisted + pt) * 0.44f;
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc((int)770, (int)1);
        GlStateManager.depthMask((boolean)false);
        GlStateManager.disableDepth();
        for (int layer = 0; layer < 2; ++layer) {
            float baseR = 0.0075f + (float)layer * 0.0035f;
            float layerAlpha = 0.4f * (1.0f - (float)layer * 0.2f);
            for (int i = 0; i < 16; ++i) {
                float a = (float)i / 16.0f * ((float)Math.PI * 2);
                float aW = MathHelper.sin((float)(t * 0.9f + (float)i * 0.7f)) * 0.24f;
                float ang = a + aW;
                float rJ = MathHelper.sin((float)(t * 1.7f + (float)i * 1.3f + (float)layer * 0.8f)) * 0.0032f;
                float r = baseR + rJ;
                float dx = MathHelper.cos((float)ang) * r;
                float dy = MathHelper.sin((float)ang) * r;
                float jx = (MathHelper.sin((float)(t * 19.0f + (float)i * 0.73f)) + MathHelper.cos((float)(t * 31.0f + (float)i * 0.41f))) * 0.5f * (0.0012f * amp);
                float jy = (MathHelper.cos((float)(t * 16.53f + (float)i * 0.31f)) + MathHelper.sin((float)(t * 34.72f + (float)i * 0.27f))) * 0.5f * (0.0012f * amp);
                GlStateManager.pushMatrix();
                GlStateManager.color((float)0.85f, (float)0.95f, (float)1.0f, (float)layerAlpha);
                GlStateManager.translate((float)(dx + jx), (float)(dy + jy), (float)0.0f);
                ir.renderItemInFirstPerson((AbstractClientPlayer)player, pt, pitch, hand, swing, stack, equip);
                GlStateManager.popMatrix();
            }
        }
        if (glitchFrames > 0) {
            float shakeAmp = 0.0045f * (0.75f + 0.6f * amp);
            float shake = MathHelper.sin((float)(((float)player.ticksExisted + pt) * (27.0f * (0.9f + 0.2f * amp)))) * shakeAmp;
            for (int p = 0; p < 7; ++p) {
                float offX = (mc.theWorld.rand.nextFloat() * 2.0f - 1.0f) * (0.016f * (0.6f + 0.6f * amp)) + shake;
                float offY = (mc.theWorld.rand.nextFloat() * 2.0f - 1.0f) * (0.016f * (0.6f + 0.6f * amp)) + shake;
                float rot = (mc.theWorld.rand.nextFloat() * 2.0f - 1.0f) * (14.0f * (0.75f + 0.4f * amp));
                float scl = 1.0f + mc.theWorld.rand.nextFloat() * 0.09000003f;
                GlStateManager.pushMatrix();
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)0.6f);
                GlStateManager.translate((float)offX, (float)offY, (float)0.0f);
                GlStateManager.rotate((float)rot, (float)0.0f, (float)0.0f, (float)1.0f);
                GlStateManager.scale((float)scl, (float)scl, (float)scl);
                ir.renderItemInFirstPerson((AbstractClientPlayer)player, pt, pitch, hand, swing, stack, equip);
                GlStateManager.popMatrix();
            }
            --glitchFrames;
        }
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableDepth();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}

