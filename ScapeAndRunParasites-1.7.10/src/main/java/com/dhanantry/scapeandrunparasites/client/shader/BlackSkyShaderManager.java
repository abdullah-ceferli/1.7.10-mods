/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.EntityRenderer
 *  net.minecraft.client.shader.Shader
 *  net.minecraft.client.shader.ShaderGroup
 *  net.minecraft.client.shader.ShaderUniform
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.relauncher.ReflectionHelper
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.shader;

import com.dhanantry.scapeandrunparasites.client.celestial.BlackSkyClient;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public final class BlackSkyShaderManager {
    private static final ResourceLocation POST = new ResourceLocation("srparasites", "shaders/post/black_sky_darkness.json");
    private static final BlackSkyShaderManager INSTANCE = new BlackSkyShaderManager();
    private boolean registered = false;
    private boolean applied = false;
    private int lastW = -1;
    private int lastH = -1;
    private long startNs = 0L;
    private long nextRetryMs = 0L;
    private static Field F_LIST_SHADERS;

    public static BlackSkyShaderManager get() {
        return INSTANCE;
    }

    private BlackSkyShaderManager() {
    }

    public void register() {
        if (!this.registered) {
            MinecraftForge.EVENT_BUS.register((Object)this);
            this.registered = true;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.thePlayer == null) {
            this.applied = false;
            return;
        }
        EntityRenderer er = mc.entityRenderer;
        ShaderGroup current = er.getShaderGroup();
        boolean shouldEnable = BlackSkyClient.isBlackSkyActive();
        if (!shouldEnable) {
            if (current != null && this.applied) {
                er.stopUseShader();
            }
            this.applied = false;
            return;
        }
        if (!this.applied || current == null) {
            long now = System.currentTimeMillis();
            if (now < this.nextRetryMs) {
                return;
            }
            try {
                this.startNs = System.nanoTime();
                er.loadShader(POST);
                ShaderGroup sg = er.getShaderGroup();
                if (sg != null) {
                    int w = mc.displayWidth;
                    int h = mc.displayHeight;
                    sg.createBindFramebuffers(w, h);
                    this.lastW = w;
                    this.lastH = h;
                    this.applied = true;
                } else {
                    this.applied = false;
                }
            }
            catch (Throwable t) {
                this.nextRetryMs = System.currentTimeMillis() + 2000L;
                this.applied = false;
            }
            return;
        }
        ShaderGroup sg = er.getShaderGroup();
        if (sg == null) {
            this.applied = false;
            return;
        }
        int w = mc.displayWidth;
        int h = mc.displayHeight;
        if (w != this.lastW || h != this.lastH) {
            sg.createBindFramebuffers(w, h);
            this.lastW = w;
            this.lastH = h;
        }
        float timeSec = (float)((double)(System.nanoTime() - this.startNs) / 1.0E9);
        BlackSkyShaderManager.setUniformAll(sg, "SRP_Time", timeSec);
        BlackSkyShaderManager.setUniformAll(sg, "Darkness", 0.85f);
    }

    private static List<Shader> getShaders(ShaderGroup sg) {
        try {
            if (F_LIST_SHADERS == null) {
                F_LIST_SHADERS = ReflectionHelper.findField(ShaderGroup.class, (String)"listShaders", (String)"field_148031_d");
                F_LIST_SHADERS.setAccessible(true);
            }
            return (List)F_LIST_SHADERS.get(sg);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static boolean isSRPDarkDaysShaderPackActive() {
        try {
            Class<?> shaders = Class.forName("net.optifine.shaders.Shaders");
            try {
                Object v = shaders.getField("currentShaderName").get(null);
                if (v instanceof String) {
                    String s = ((String)v).toLowerCase(Locale.ROOT);
                    return s.contains("srp_darkdays_lite") || s.contains("srp darkdays lite") || s.contains("srp_dark_days_lite") || s.contains("srp dark days lite");
                }
            }
            catch (Throwable throwable) {}
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return false;
    }

    private static void setUniformAll(ShaderGroup sg, String name, float value) {
        try {
            List<Shader> shaders = BlackSkyShaderManager.getShaders(sg);
            if (shaders == null) {
                return;
            }
            for (Shader shader : shaders) {
                ShaderUniform uniform;
                if (shader == null || (uniform = shader.getShaderManager().getShaderUniform(name)) == null) continue;
                uniform.set(value);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

