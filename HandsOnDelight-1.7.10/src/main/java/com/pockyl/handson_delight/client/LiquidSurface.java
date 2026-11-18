package com.pockyl.handson_delight.client;
import com.pockyl.handson_delight.NativeMath;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
/** Diegesis LiquidRenderer's upright mottled sheet, emitted with the native tessellator. */
final class LiquidSurface {
    private static final int MOTTLE_CELLS = LiquidSim.WAKE_CELLS;
    private static final float SURFACE_PATH=.85F;
    private static final float MOTTLE_UPRIGHT = 0.985F;
    /** The patches: how big, and how many steps of shade, for the look of a texture rather than of a blur. */
    private static final float MOTTLE_SCALE = 0.6F;
    private static final float MOTTLE_DETAIL = 2.2F;
    private static final float MOTTLE_STEPS = 7.0F;
    private static final float MOTTLE_STRENGTH = 0.24F;
    /** Foam along the walls: how many cells deep it reaches, how often a cell changes (per second), the chance of foam by the wall and one further in. */
    private static final int SEETHE_CELLS = 2;
    private static final float SEETHE_BEAT = 5.0F;
    private static final float SEETHE_AT_WALL = 0.5F;
    private static final float SEETHE_INSIDE = 0.2F;
    private static final float SEETHE_LIGHT = 0.32F;
    /** Foam: the size of its patches, the steps of shade in it, how far its thin edge lets the liquid through. */
    private static final float FOAM_SCALE = 0.45F;
    private static final float FOAM_DETAIL = 1.7F;
    private static final float FOAM_SHADE = 0.14F;
    private static final float FOAM_EDGE = 0.07F;
    /** A trail shades the liquid's own colour, in steps, so it stays pixels and stays food; only its lit side has a little gloss. */
    private static final float WAKE_STEPS = 8.0F;
    private static final float WAKE_GLOSS = 0.3F;
    /** Tanks narrower than this (node pixels) are too small for waves to be seen. */
    private static final float MIN_WAVE_SIZE = 3.0F;
    /** The most of its box a liquid fills as drawn. */
    private static final float FULLEST = 0.995F;
    /** How strongly a ring's slope lights a cloudy or foamy liquid's cells. */
    private static final float MOTTLE_WAVE_LIGHT = 6.0F;


    private float red,green,blue,clearness,level;
    void draw(LiquidSim sim){int color=sim.color();red=(color>>16&255)/255F;green=(color>>8&255)/255F;blue=(color&255)/255F;clearness=(float)Math.log(Math.max(.001F,1-(color>>>24)/255F));level=2+6.5F*sim.fraction();
        GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glDisable(GL11.GL_LIGHTING);GL11.glDisable(GL11.GL_CULL_FACE);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);GL11.glDepthMask(false);
        Tessellator.instance.startDrawingQuads();mottled(4,4,sim);Tessellator.instance.draw();GL11.glDepthMask(true);GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glColor4f(1,1,1,1);
    }
    private float thickness(float path){return 1-(float)Math.exp(clearness*path);}
    private void cell(float x,float z,LiquidSim sim,float hx,float hz,float r,float g,float b,float a){Tessellator t=Tessellator.instance;t.setColorRGBA_F(Math.min(1,r),Math.min(1,g),Math.min(1,b),Math.min(1,a));float wave=sim.height(.5F+x/(2*hx),.5F-z/(2*hz));t.addVertex(x,level-wave,z);}
        void mottled(float hx, float hz, LiquidSim sim) {
            int cells = MOTTLE_CELLS;
            boolean waves = sim.isRippling();
            float cos = NativeMath.cos(sim.turned());
            float sin = NativeMath.sin(sim.turned());
            float strength = sim.mottle() * MOTTLE_STRENGTH;
            float a = thickness(SURFACE_PATH);
            for (int row = 0; row < cells; row++) {
                float z0 = -hz + 2.0F * hz * row / cells;
                float z1 = -hz + 2.0F * hz * (row + 1) / cells;
                for (int column = 0; column < cells; column++) {
                    float x0 = -hx + 2.0F * hx * column / cells;
                    float x1 = -hx + 2.0F * hx * (column + 1) / cells;
                    float mx = (x0 + x1) / 2.0F;
                    float mz = (z0 + z1) / 2.0F;
                    // The sheet is read turned back, so that the patches go the way the liquid and what floats on it go.
                    float sx = mx * cos + mz * sin;
                    float sz = mz * cos - mx * sin;
                    float value = 0.65F * noise(sx * MOTTLE_SCALE, sz * MOTTLE_SCALE) + 0.35F * noise(sx * MOTTLE_DETAIL + 7.0F, sz * MOTTLE_DETAIL + 3.0F);
                    float stepped = Math.round(value * MOTTLE_STEPS) / MOTTLE_STEPS - 0.5F;
                    float middle = 1.0F - Math.max(Math.abs(mx) / hx, Math.abs(mz) / hz);
                    float body = 1.0F + stepped * strength * 2.0F + middle * sim.mottle() * 0.12F;
                    boolean rim = row == 0 || column == 0 || row == cells - 1 || column == cells - 1;
                    float r = red * body;
                    float g = green * body;
                    float b = blue * body;
                    if (rim) {
                        r += (1.0F - r) * 0.2F;
                        g += (1.0F - g) * 0.2F;
                        b += (1.0F - b) * 0.2F;
                    }
                    int fromWall = Math.min(Math.min(row, column), Math.min(cells - 1 - row, cells - 1 - column));
                    if (fromWall < SEETHE_CELLS && sim.edgeBoil() > 0.01F) {
                        // Every cell keeps its own beat, so the foam shivers instead of blinking all at once.
                        int spot = row * cells + column;
                        int beat = NativeMath.floor(sim.clock() * SEETHE_BEAT + hash(spot, 5) * 7.0F);
                        float chance = sim.edgeBoil() * (fromWall == 0 ? SEETHE_AT_WALL : SEETHE_INSIDE);
                        if (hash(spot, beat) < chance) {
                            r += (1.0F - r) * SEETHE_LIGHT;
                            g += (1.0F - g) * SEETHE_LIGHT;
                            b += (1.0F - b) * SEETHE_LIGHT;
                        }
                    }
                    float alpha = rim ? a + 0.15F : a;
                    if (sim.foam() > 0.01F) {
                        // Foam holds to the walls first, then lies in patches that turn with the liquid; its thin edge lets the colour through.
                        float inward = fromWall / Math.max(1.0F, (cells - 1) / 2.0F);
                        float lace = 0.6F * noise(sx * FOAM_SCALE + 11.0F, sz * FOAM_SCALE + 5.0F) + 0.4F * (1.0F - inward);
                        float over = lace - (1.0F - sim.foam());
                        if (over > 0.0F) {
                            int foamColor = sim.foamColor();
                            float shade = 1.0F - FOAM_SHADE * (Math.round(noise(sx * FOAM_DETAIL + 3.0F, sz * FOAM_DETAIL + 9.0F) * 3.0F) / 3.0F);
                            float edge = over < FOAM_EDGE ? 0.55F : 1.0F;
                            r = NativeMath.lerp(edge, r, (foamColor >> 16 & 0xFF) / 255.0F * shade);
                            g = NativeMath.lerp(edge, g, (foamColor >> 8 & 0xFF) / 255.0F * shade);
                            b = NativeMath.lerp(edge, b, (foamColor & 0xFF) / 255.0F * shade);
                            alpha = NativeMath.lerp(edge, alpha, (foamColor >>> 24) / 255.0F);
                        }
                    }
                    if (waves) {
                        // A ring's slope turned to the light is brighter and the side turned away darker, in steps as the rest of
                        // the sheet: rings read on a cloudy or foamy liquid too, as they do on a clear one.
                        float u = 0.5F + mx / (2.0F * hx);
                        float v = 0.5F - mz / (2.0F * hz);
                        float e = 0.5F / cells;
                        float slopeX = (sim.height(u + e, v) - sim.height(u - e, v)) / (4.0F * e * hx);
                        float slopeZ = -(sim.height(u, v + e) - sim.height(u, v - e)) / (4.0F * e * hz);
                        float lit = NativeMath.clamp((slopeX * -.6F + slopeZ * -.8F) * MOTTLE_WAVE_LIGHT, -0.3F, 0.45F);
                        lit = Math.round(lit * WAKE_STEPS) / WAKE_STEPS;
                        r = r * (1.0F + Math.min(0.0F, lit)) + (1.0F - r) * Math.max(0.0F, lit);
                        g = g * (1.0F + Math.min(0.0F, lit)) + (1.0F - g) * Math.max(0.0F, lit);
                        b = b * (1.0F + Math.min(0.0F, lit)) + (1.0F - b) * Math.max(0.0F, lit);
                    }
                    float wake = Math.round(sim.wakeShade(column, row) * WAKE_STEPS) / WAKE_STEPS;
                    float gloss = Math.max(0.0F, wake) * WAKE_GLOSS;
                    r = r * (1.0F + wake) + (1.0F - r) * gloss;
                    g = g * (1.0F + wake) + (1.0F - g) * gloss;
                    b = b * (1.0F + wake) + (1.0F - b) * gloss;
                    cell(x0, z0, sim, hx, hz, r, g, b, alpha);
                    cell(x0, z1, sim, hx, hz, r, g, b, alpha);
                    cell(x1, z1, sim, hx, hz, r, g, b, alpha);
                    cell(x1, z0, sim, hx, hz, r, g, b, alpha);
                }
            }
        }

        private static float noise(float x, float z) {
            int ix = NativeMath.floor(x);
            int iz = NativeMath.floor(z);
            float fx = x - ix;
            float fz = z - iz;
            fx = fx * fx * (3.0F - 2.0F * fx);
            fz = fz * fz * (3.0F - 2.0F * fz);
            float near = NativeMath.lerp(fx, hash(ix, iz), hash(ix + 1, iz));
            float far = NativeMath.lerp(fx, hash(ix, iz + 1), hash(ix + 1, iz + 1));
            return NativeMath.lerp(fz, near, far);
        }

        private static float hash(int x, int z) {
            int h = x * 374761393 + z * 668265263;
            h = (h ^ h >>> 13) * 1274126177;
            return ((h ^ h >>> 16) & 0xFFFF) / 65535.0F;
        }

}
