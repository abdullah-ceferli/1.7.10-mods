/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.EntityRenderer
 *  net.minecraft.client.shader.Shader
 *  net.minecraft.client.shader.ShaderGroup
 *  net.minecraft.client.shader.ShaderUniform
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.shader.star;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.client.world.SRPClientStarWorldState;
import com.dhanantry.scapeandrunparasites.mixins.accessor.AccessorShaderGroup;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.List;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public final class StarWorldShaderManager {
    private static final ResourceLocation COLD_SHADER = new ResourceLocation("srparasites", "shaders/post/star_cold.json");
    private static final StarWorldShaderManager INSTANCE = new StarWorldShaderManager();
    private boolean registered = false;
    private boolean applied = false;
    private ResourceLocation activeShader = null;
    private int lastW = -1;
    private int lastH = -1;
    private long startNs = 0L;
    private long nextRetryMs = 0L;
    private float heldLightFade = 0.0f;
    private float fade = 0.0f;
    private boolean fadingOut = false;
    private ResourceLocation fadeOutShader = null;
    private World lastWorld = null;

    public static StarWorldShaderManager get() {
        return INSTANCE;
    }

    private StarWorldShaderManager() {
    }

    public void register() {
        if (!this.registered) {
            MinecraftForge.EVENT_BUS.register((Object)this);
            this.registered = true;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        ShaderGroup sg;
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (this.lastWorld != mc.theWorld) {
            this.lastWorld = mc.theWorld;
            this.stop(mc);
        }
        ResourceLocation wanted = this.getWantedShader(mc);
        float heldLightTarget = this.isHoldingLightSource(mc) ? 1.0f : 0.0f;
        this.heldLightFade += (heldLightTarget - this.heldLightFade) * 0.085f;
        this.heldLightFade = Math.max(0.0f, Math.min(1.0f, this.heldLightFade));
        if (wanted == null) {
            this.fadeOutOrStop(mc);
            return;
        }
        this.fadingOut = false;
        this.fadeOutShader = null;
        EntityRenderer er = mc.entityRenderer;
        ShaderGroup current = er.getShaderGroup();
        if (!wanted.equals((Object)this.activeShader) || !this.applied || current == null) {
            this.load(mc, wanted);
            return;
        }
        this.fade = Math.min(1.0f, this.fade + 0.035f);
        int w = mc.displayWidth;
        int h = mc.displayHeight;
        if ((w != this.lastW || h != this.lastH) && er.getShaderGroup() != null) {
            er.getShaderGroup().createBindFramebuffers(w, h);
            this.lastW = w;
            this.lastH = h;
        }
        if ((sg = er.getShaderGroup()) != null) {
            float timeSec = (float)((double)(System.nanoTime() - this.startNs) / 1.0E9);
            float exposure = this.getExposure(mc);
            StarWorldShaderManager.setUniformAll(sg, "SRP_Time", timeSec);
            StarWorldShaderManager.setUniformAll(sg, "SRP_Exposure", exposure);
            StarWorldShaderManager.setUniformAll(sg, "SRP_Fade", this.fade);
            StarWorldShaderManager.setUniformAll(sg, "SRP_HandLight", this.heldLightFade);
        }
    }

    private void fadeOutOrStop(Minecraft mc) {
        if (mc == null || mc.entityRenderer == null) {
            return;
        }
        ShaderGroup sg = mc.entityRenderer.getShaderGroup();
        if (!this.applied || sg == null) {
            this.fade = 0.0f;
            this.fadingOut = false;
            this.fadeOutShader = null;
            this.stop(mc);
            return;
        }
        this.fadingOut = true;
        this.fadeOutShader = this.activeShader;
        this.fade = Math.max(0.0f, this.fade - 0.05f);
        float timeSec = (float)((double)(System.nanoTime() - this.startNs) / 1.0E9);
        this.heldLightFade += (0.0f - this.heldLightFade) * 0.085f;
        this.heldLightFade = Math.max(0.0f, Math.min(1.0f, this.heldLightFade));
        StarWorldShaderManager.setUniformAll(sg, "SRP_Time", timeSec);
        StarWorldShaderManager.setUniformAll(sg, "SRP_Exposure", 1.0f);
        StarWorldShaderManager.setUniformAll(sg, "SRP_Fade", this.fade);
        StarWorldShaderManager.setUniformAll(sg, "SRP_HandLight", this.heldLightFade);
        if (this.fade <= 0.0f) {
            this.fadingOut = false;
            this.fadeOutShader = null;
            this.stop(mc);
        }
    }

    private ResourceLocation getWantedShader(Minecraft mc) {
        if (!SRPConfigWorld.enableStarWorldShaders) {
            return null;
        }
        if (mc.theWorld == null || mc.thePlayer == null || mc.theWorld.provider == null) {
            return null;
        }
        if (mc.theWorld.provider.dimensionId != 0) {
            return null;
        }
        if (!SRPClientStarWorldState.isCold()) {
            return null;
        }
        if (!SRPConfigWorld.enableColdStarFogShader) {
            return null;
        }
        return this.isOutside(mc) ? COLD_SHADER : null;
    }

    private boolean isExposedToSun(Minecraft mc) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return false;
        }
        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        return mc.theWorld.isDaytime() && !mc.theWorld.isRaining() && WorldAccess.canSeeSky(mc.theWorld, pos) && mc.theWorld.getLightBrightness(pos) > 0.75f;
    }

    private boolean isOutside(Minecraft mc) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return false;
        }
        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        return WorldAccess.canSeeSky(mc.theWorld, pos);
    }

    private float getExposure(Minecraft mc) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return 0.0f;
        }
        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        if (!WorldAccess.canSeeSky(mc.theWorld, pos)) {
            return 0.0f;
        }
        return Math.max(0.0f, Math.min(1.0f, mc.theWorld.getLightBrightness(pos)));
    }

    private boolean isHoldingLightSource(Minecraft mc) {
        if (mc == null || mc.thePlayer == null || mc.theWorld == null) {
            return false;
        }
        return this.getHeldLightStrength(mc, mc.thePlayer.getHeldItemMainhand()) > 0.0f || this.getHeldLightStrength(mc, mc.thePlayer.getHeldItemOffhand()) > 0.0f;
    }

    private float getHeldLightStrength(Minecraft mc, ItemStack stack) {
        Block block;
        if (stack == null || stack.isEmpty()) {
            return 0.0f;
        }
        if (stack.getItem() == Items.lava_bucket) {
            return 1.0f;
        }
        if (stack.getItem() instanceof ItemBlock && (block = ((ItemBlock)stack.getItem()).getBlock()) != null) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
            IBlockState state = BlockStates.defaultState(block);
            int light = block.getLightValue(state, (IBlockAccess)mc.theWorld, pos);
            return Math.max(0.0f, Math.min(1.0f, (float)light / 15.0f));
        }
        return 0.0f;
    }

    private void load(Minecraft mc, ResourceLocation shader) {
        long now = System.currentTimeMillis();
        if (now < this.nextRetryMs) {
            return;
        }
        try {
            EntityRenderer er = mc.entityRenderer;
            if (er.getShaderGroup() != null) {
                er.stopUseShader();
            }
            er.loadShader(shader);
            ShaderGroup sg = er.getShaderGroup();
            if (sg != null) {
                int w = mc.displayWidth;
                int h = mc.displayHeight;
                sg.createBindFramebuffers(w, h);
                this.lastW = w;
                this.lastH = h;
                this.applied = true;
                this.activeShader = shader;
                this.startNs = System.nanoTime();
                if (!this.fadingOut || this.fadeOutShader == null || !this.fadeOutShader.equals((Object)shader)) {
                    this.fade = Math.max(this.fade, 0.0f);
                }
            } else {
                this.applied = false;
                this.activeShader = null;
            }
        }
        catch (Throwable t) {
            this.nextRetryMs = System.currentTimeMillis() + 2000L;
            this.applied = false;
            this.activeShader = null;
        }
    }

    private void stop(Minecraft mc) {
        if (mc == null || mc.entityRenderer == null) {
            return;
        }
        ShaderGroup current = mc.entityRenderer.getShaderGroup();
        if (current != null && this.applied) {
            mc.entityRenderer.stopUseShader();
        }
        this.applied = false;
        this.activeShader = null;
        this.fade = 0.0f;
        this.heldLightFade = 0.0f;
        this.fadingOut = false;
        this.fadeOutShader = null;
    }

    private static List<Shader> getShaders(ShaderGroup shaderGroup) {
        if (shaderGroup == null) {
            return null;
        }
        return ((AccessorShaderGroup)shaderGroup).srp$getShaders();
    }

    private static void setUniformAll(ShaderGroup shaderGroup, String name, float value) {
        List<Shader> shaders = StarWorldShaderManager.getShaders(shaderGroup);
        if (shaders == null) {
            return;
        }
        for (Shader shader : shaders) {
            ShaderUniform uniform;
            if (shader == null || (uniform = shader.getShaderManager().getShaderUniform(name)) == null) continue;
            uniform.set(value);
        }
    }
}

