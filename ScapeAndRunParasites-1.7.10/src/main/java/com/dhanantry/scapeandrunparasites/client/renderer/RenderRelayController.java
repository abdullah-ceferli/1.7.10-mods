/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraftforge.client.model.IModel
 *  net.minecraftforge.client.model.obj.OBJLoader
 *  net.minecraftforge.client.model.obj.OBJModel
 *  net.minecraftforge.common.model.IModelState
 *  net.minecraftforge.common.model.TRSRTransformation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.lwjgl.opengl.GL11
 */
package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderRelayController
extends TileEntitySpecialRenderer<TileEntityRelayController> {
    private static final String MAT_MAIN = "m_54acd2a9-1b93-8c80-6bd1-4d05c195c2a9";
    private static final String MTL_TEX_NAME = "pasted.png";
    private static final ResourceLocation TEX_OFF = new ResourceLocation("srparasites", "textures/blocks/arraytowertexture.png");
    private static final ResourceLocation TEX_ON = new ResourceLocation("srparasites", "textures/blocks/arraytowertextureon.png");
    private IBakedModel bakedOff;
    private IBakedModel bakedOn;

    public boolean isGlobalRenderer(TileEntityRelayController te) {
        return true;
    }

    private static String atlasSprite(String texNameNoExt) {
        return "srparasites:blocks/" + texNameNoExt;
    }

    private IBakedModel bakeForceAtlasSprite(String texNameNoExt) throws Exception {
        IModel raw = OBJLoader.INSTANCE.loadModel(new ResourceLocation("srparasites", "models/block/arraytowerlarge.obj"));
        if (raw instanceof OBJModel) {
            raw = ((OBJModel)raw).process(ImmutableMap.of((Object)"flip-v", (Object)"true"));
        }
        String full = RenderRelayController.atlasSprite(texNameNoExt);
        ImmutableMap.Builder map = ImmutableMap.builder();
        map.put((Object)MAT_MAIN, (Object)full);
        map.put((Object)MTL_TEX_NAME, (Object)full);
        map.put((Object)"pasted", (Object)full);
        map.put((Object)"OBJModel.Default.Texture.Name", (Object)full);
        map.put((Object)"OBJModel.White.Texture.Name", (Object)full);
        map.put((Object)"default", (Object)full);
        map.put((Object)"particle", (Object)full);
        map.put((Object)"texture", (Object)full);
        map.put((Object)"tex", (Object)full);
        IModel retextured = raw.retexture(map.build());
        TextureAtlasSprite forced = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(full);
        Function<ResourceLocation, TextureAtlasSprite> sprites = loc -> forced;
        return retextured.bake((IModelState)TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, sprites);
    }

    private void ensureBaked() {
        if (this.bakedOff != null && this.bakedOn != null) {
            return;
        }
        try {
            this.bakedOff = this.bakeForceAtlasSprite("arraytowertexture");
            this.bakedOn = this.bakeForceAtlasSprite("arraytowertextureon");
        }
        catch (Exception e) {
            IBakedModel missing;
            this.bakedOff = missing = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
            this.bakedOn = missing;
            e.printStackTrace();
        }
    }

    public void render(TileEntityRelayController te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || te.getWorldObj() == null) {
            return;
        }
        if (RenderRelayController.isOptifineShadowPass()) {
            return;
        }
        this.ensureBaked();
        boolean cooldownActive = false;
        try {
            cooldownActive = te.getCooldownRemainingTicks() > 0;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        IBakedModel model = cooldownActive ? this.bakedOn : this.bakedOff;
        Minecraft mc = Minecraft.getMinecraft();
        BlockPos pos = te.getPos();
        int blockL = te.getWorldObj().getLightFor(EnumSkyBlock.BLOCK, pos);
        int skyL = te.getWorldObj().getLightFor(EnumSkyBlock.SKY, pos);
        int level = Math.max(blockL, skyL);
        float brightness = 0.35f + (float)level / 15.0f * 0.65f;
        int r = (int)(brightness * 255.0f) & 0xFF;
        int g = (int)(brightness * 255.0f) & 0xFF;
        int b = (int)(brightness * 255.0f) & 0xFF;
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)(x + 0.5), (double)y, (double)(z + 0.5));
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc((int)516, (float)0.1f);
        GlStateManager.disableLighting();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        if (RenderRelayController.isOptifineInstalled()) {
            ResourceLocation tex = cooldownActive ? TEX_ON : TEX_OFF;
            mc.getTextureManager().bindTexture(tex);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            buf.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            List quads = model.getQuads(null, null, 0L);
            for (BakedQuad q : quads) {
                RenderRelayController.drawQuadAsStandalonePNG(buf, q, r, g, b, 255);
            }
            tess.draw();
        } else {
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(model, brightness, 1.0f, 1.0f, 1.0f);
        }
        GlStateManager.enableCull();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
    }

    private static void drawQuadAsStandalonePNG(BufferBuilder buf, BakedQuad q, int r, int g, int b, int a) {
        int[] data = q.getVertexData();
        int stride = data.length / 4;
        TextureAtlasSprite spr = q.getSprite();
        for (int v = 0; v < 4; ++v) {
            int i = v * stride;
            float x = Float.intBitsToFloat(data[i]);
            float y = Float.intBitsToFloat(data[i + 1]);
            float z = Float.intBitsToFloat(data[i + 2]);
            float uSprite = Float.intBitsToFloat(data[i + 4]);
            float vSprite = Float.intBitsToFloat(data[i + 5]);
            float u = uSprite;
            float vv = vSprite;
            if (spr != null) {
                float u0 = spr.getMinU();
                float v0 = spr.getMinV();
                float u1 = spr.getMaxU();
                float v1 = spr.getMaxV();
                float du = u1 - u0;
                float dv = v1 - v0;
                if (du != 0.0f) {
                    u = (uSprite - u0) / du;
                }
                if (dv != 0.0f) {
                    vv = (vSprite - v0) / dv;
                }
            }
            buf.pos((double)x, (double)y, (double)z).tex((double)u, (double)vv).color(r, g, b, a).endVertex();
        }
    }

    private static boolean isOptifineInstalled() {
        try {
            Class.forName("net.optifine.Config");
            return true;
        }
        catch (Throwable throwable) {
            try {
                Class.forName("optifine.OptiFineClassTransformer");
                return true;
            }
            catch (Throwable throwable2) {
                try {
                    Class.forName("net.optifine.shaders.Shaders");
                    return true;
                }
                catch (Throwable throwable3) {
                    return false;
                }
            }
        }
    }

    private static boolean isOptifineShadowPass() {
        try {
            Class<?> shaders = Class.forName("net.optifine.shaders.Shaders");
            return shaders.getField("isShadowPass").getBoolean(null);
        }
        catch (Throwable t) {
            return false;
        }
    }
}

