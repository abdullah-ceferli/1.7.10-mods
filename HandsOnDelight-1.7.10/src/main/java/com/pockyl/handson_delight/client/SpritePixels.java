package com.pockyl.handson_delight.client;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

/** Resource-pack pixels remain readable when OptiFine releases static atlas frame data. */
final class SpritePixels {
    final int width,height;final int[] argb;
    private static final Map<String,SpritePixels> CACHE=new HashMap<String,SpritePixels>();
    private static Object generation;
    private SpritePixels(int width,int height,int[] argb){this.width=width;this.height=height;this.argb=argb;}
    static SpritePixels read(ItemStack stack){
        Minecraft mc=Minecraft.getMinecraft();IIcon icon=stack.getIconIndex();TextureMap atlas=(TextureMap)mc.getTextureManager().getTexture(stack.getItemSpriteNumber()==0?TextureMap.locationBlocksTexture:TextureMap.locationItemsTexture);
        Object missing=((TextureMap)mc.getTextureManager().getTexture(TextureMap.locationItemsTexture)).getAtlasSprite("missingno");if(missing!=generation){CACHE.clear();generation=missing;}
        String key=stack.getItemSpriteNumber()+"/"+icon.getIconName();SpritePixels cached=CACHE.get(key);if(cached!=null)return cached;
        TextureAtlasSprite sprite=atlas.getAtlasSprite(icon.getIconName());int width=Math.max(1,sprite.getIconWidth()),height=Math.max(1,sprite.getIconHeight());int[] data=null;
        ResourceLocation id=new ResourceLocation(icon.getIconName());String path=id.getResourcePath();if(!path.startsWith("textures/"))path="textures/"+(path.startsWith("items/")||path.startsWith("blocks/")?"":stack.getItemSpriteNumber()==0?"blocks/":"items/")+path;
        ResourceLocation resource=new ResourceLocation(id.getResourceDomain(),path+".png");
        try(InputStream in=mc.getResourceManager().getResource(resource).getInputStream()){
            BufferedImage image=ImageIO.read(in);if(image!=null){width=image.getWidth();height=Math.min(image.getHeight(),height);data=image.getRGB(0,0,width,height,null,0,width);}
        }catch(IOException unavailable){if(sprite.getFrameCount()>0)data=sprite.getFrameTextureData(0)[0];}
        if(data==null){width=height=1;data=new int[1];}
        SpritePixels pixels=new SpritePixels(width,height,data);CACHE.put(key,pixels);return pixels;
    }
}
