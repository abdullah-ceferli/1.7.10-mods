package com.pockyl.handson_delight.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.util.IIcon;
import vectorwing.farmersdelight.legacy.*;
import java.awt.Color;
import java.util.*;

/** The colours food gives a broth, its pieces and its juice, read from its icon, so modded food needs no table. */
final class FoodColors {
    private static final Map<String, Integer> CACHE = new HashMap<String,Integer>();
    private static final int FALLBACK = 0xB0A070;
    /** A raw egg stirred in: yolk and white. */
    private static final int EGG = 0xF2DC9A;
    private static final int HUES = 12;
    private static final Map<String, Integer> MAIN = new HashMap<String,Integer>();
    private static final Map<String, Integer> BROTH = new HashMap<String,Integer>();
    private static TextureAtlasSprite readWith;
    /** Where the bowl starts on a dish's icon, as a part of its height, when it is not drawn in a bowl's own colours. */
    private static final float BOWL_TOP = 0.6F;
    private static final float GREY = 0.14F;
    private static final float WARM_HUE = 0.1F;
    private static final float MIN_SATURATION = 0.38F;
    private static final float MIN_BRIGHTNESS = 0.72F;

    private FoodColors() {
    }

    /** RGB of a stack's icon. */
    static int of(ItemStack stack) {
        forgetOnReload();
        // A liquid colours the soup with what is in its container, not the container; an egg with its yolk and white, not its shell.
        return CACHE.computeIfAbsent(key(stack), look -> stack.getItem()==Items.milk_bucket ? 0xFFFFFF : stack.getItem()==Items.egg ? EGG : sample(stack));
    }

    /** Resources were reloaded (the game's fallback model is a new one): the colours read so far may be of other textures. */
    private static void forgetOnReload() {
        TextureAtlasSprite missing = atlas(new ItemStack(Items.apple)).getAtlasSprite("missingno");
        if (missing != readWith) {
            readWith = missing;
            CACHE.clear();
            MAIN.clear();
            BROTH.clear();
        }
    }

    private static int sample(ItemStack stack) {
        SpriteContents contents = contents(stack);
        NativeImage image = contents.image;
        long red = 0;
        long green = 0;
        long blue = 0;
        long weight = 0;
        for (int y = 0; y < contents.height(); y++) {
            for (int x = 0; x < contents.width(); x++) {
                int abgr = image.getPixelRGBA(x, y);
                if ((abgr >>> 24) < 128) {
                    continue;
                }
                int r = abgr & 0xFF;
                int g = abgr >> 8 & 0xFF;
                int b = abgr >> 16 & 0xFF;
                // Colourful pixels say more about food than its grey outline does.
                int vivid = 1 + Math.max(r, Math.max(g, b)) - Math.min(r, Math.min(g, b));
                red += (long) r * vivid;
                green += (long) g * vivid;
                blue += (long) b * vivid;
                weight += vivid;
            }
        }
        return weight == 0 ? FALLBACK : (int) (red / weight) << 16 | (int) (green / weight) << 8 | (int) (blue / weight);
    }

    /**
     * The colour a stack is mostly made of: the biggest family of hues on its icon, brightened a little, so that the green top of a
     * carrot does not turn the carrot brown as an average would.
     */
    static int main(ItemStack stack) {
        forgetOnReload();
        return MAIN.computeIfAbsent(key(stack), look -> sampleMain(stack, 1.0F, Collections.<Integer>emptySet()));
    }

    /**
     * The colour of a soup: of what is in its bowl or bottle. The container's pixels are left out by the colours of its own icon:
     * the wood of a bowl would turn every soup brown, the glass of a bottle every drink blue. A container drawn in other colours
     * leaves the upper part of the icon, where a bowl's contents show.
     */
    static int broth(World level, ItemStack meal) {
        forgetOnReload();
        return BROTH.computeIfAbsent(key(meal), look -> {
            Set<Integer> container = colours(container(level, meal));
            return container.isEmpty() || !drawsAny(meal, container) ? sampleMain(meal, container.isEmpty() ? 1.0F : BOWL_TOP, Collections.<Integer>emptySet())
                    : sampleMain(meal, 1.0F, container);
        });
    }

    /** What the dish is served in: its recipe's container, or what the item leaves behind when eaten. */
    private static ItemStack container(World level, ItemStack meal) {
        for(CookingRecipes.Recipe recipe:CookingRecipes.ALL)if(Stacks.same(recipe.output,meal)&&!Stacks.empty(recipe.container))return recipe.container;
        return CookingRecipes.remainder(meal);
    }

    private static NativeImage image(ItemStack stack) {
        return contents(stack).image;
    }

    private static SpriteContents contents(ItemStack stack) {
        return new SpriteContents(SpritePixels.read(stack));
    }
    private static TextureMap atlas(ItemStack stack){return (TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(stack.getItemSpriteNumber()==0?TextureMap.locationBlocksTexture:TextureMap.locationItemsTexture);}
    private static String key(ItemStack stack){return net.minecraft.item.Item.itemRegistry.getNameForObject(stack.getItem())+"/"+stack.getItemDamage()+"/"+stack.getTagCompound();}
    private static final class SpriteContents {
        final SpritePixels sprite;final NativeImage image;
        SpriteContents(SpritePixels s){sprite=s;image=new NativeImage(s);}
        int width(){return sprite.width;}int height(){return sprite.height;}
    }
    private static final class NativeImage {
        final SpritePixels sprite;
        NativeImage(SpritePixels s){sprite=s;}
        int getPixelRGBA(int x,int y){int c=sprite.argb[y*sprite.width+x];return c&0xff00ff00|(c>>16&255)|(c&255)<<16;}
    }

    /** The colours of the visible pixels of an icon, as {@link NativeImage} keeps them without their alpha. */
    private static Set<Integer> colours(ItemStack stack) {
        Set<Integer> colours = new HashSet<>();
        if (!Stacks.empty(stack)) {
            SpriteContents contents = contents(stack);
            NativeImage image = image(stack);
            for (int y = 0; y < contents.height(); y++) {
                for (int x = 0; x < contents.width(); x++) {
                    int abgr = image.getPixelRGBA(x, y);
                    if ((abgr >>> 24) >= 128) {
                        colours.add(abgr & 0xFFFFFF);
                    }
                }
            }
        }
        return colours;
    }

    private static boolean drawsAny(ItemStack stack, Set<Integer> colours) {
        SpriteContents contents = contents(stack);
        NativeImage image = image(stack);
        for (int y = 0; y < contents.height(); y++) {
            for (int x = 0; x < contents.width(); x++) {
                int abgr = image.getPixelRGBA(x, y);
                if ((abgr >>> 24) >= 128 && colours.contains(abgr & 0xFFFFFF)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int sampleMain(ItemStack stack, float rows, Set<Integer> leftOut) {
        SpriteContents contents = contents(stack);
        NativeImage image = image(stack);
        long[][] hues = new long[HUES][4];
        for (int y = 0; y < contents.height() * rows; y++) {
            for (int x = 0; x < contents.width(); x++) {
                int abgr = image.getPixelRGBA(x, y);
                if ((abgr >>> 24) < 128 || leftOut.contains(abgr & 0xFFFFFF)) {
                    continue;
                }
                int r = abgr & 0xFF;
                int g = abgr >> 8 & 0xFF;
                int b = abgr >> 16 & 0xFF;
                float[] hsb = Color.RGBtoHSB(r, g, b, null);
                // The outline of an icon is its darkest part, and says nothing of what it shows.
                if (hsb[2] < 0.3F) {
                    continue;
                }
                // Colour counts for more than grey: a shadow on meat is no colour of its own.
                int weight = 1 + Math.round(hsb[1] * 4.0F);
                // Red lies at both ends of the hue circle: its last twelfth joins the first, or red split in two loses to a green garnish.
                int hue = Math.min(HUES - 1, (int) (hsb[0] * HUES));
                long[] family = hues[hue == HUES - 1 ? 0 : hue];
                family[0] += (long) r * weight;
                family[1] += (long) g * weight;
                family[2] += (long) b * weight;
                family[3] += weight;
            }
        }
        long[] best = hues[0];
        for (long[] family : hues) {
            best = family[3] > best[3] ? family : best;
        }
        if (best[3] == 0) {
            return FALLBACK;
        }
        float[] hsb = Color.RGBtoHSB((int) (best[0] / best[3]), (int) (best[1] / best[3]), (int) (best[2] / best[3]), null);
        // Food is warm and full of colour: what comes out grey is given the tone of something cooked.
        float hue = hsb[1] < GREY ? WARM_HUE : hsb[0];
        float saturation = Math.max(MIN_SATURATION, Math.min(1.0F, hsb[1] * 1.3F));
        float brightness = Math.max(MIN_BRIGHTNESS, Math.min(1.0F, hsb[2] * 1.2F));
        return Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF;
    }

    static boolean isGreen(int rgb) {
        float hue = Color.RGBtoHSB(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, null)[0];
        return hue > 0.2F && hue < 0.47F;
    }

    /** {@code rgb} moved towards white by {@code amount}, 0 to 1. */
    static int lighter(int rgb, float amount) {
        int result = 0;
        for (int shift = 0; shift < 24; shift += 8) {
            int value = rgb >> shift & 0xFF;
            result |= Math.round(value + (255 - value) * amount) << shift;
        }
        return result;
    }

    /** The average of several RGB colours, or the fallback for none. */
    static int mix(Iterable<ItemStack> stacks) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int count = 0;
        for (ItemStack stack : stacks) {
            if (!Stacks.empty(stack)) {
                int color = of(stack);
                red += color >> 16 & 0xFF;
                green += color >> 8 & 0xFF;
                blue += color & 0xFF;
                count++;
            }
        }
        return count == 0 ? FALLBACK : red / count << 16 | green / count << 8 | blue / count;
    }
}
