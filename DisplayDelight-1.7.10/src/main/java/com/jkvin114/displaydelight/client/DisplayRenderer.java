package com.jkvin114.displaydelight.client;

import java.io.*;import java.util.*;import java.util.zip.GZIPInputStream;
import com.google.gson.*;import com.jkvin114.displaydelight.*;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;import net.minecraft.client.renderer.*;import net.minecraft.util.IIcon;import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import org.lwjgl.opengl.GL11;

/** Preconverted upstream quads rendered through the native terrain atlas and chunk renderer. */
public final class DisplayRenderer implements ISimpleBlockRenderingHandler {
    public static final class Quad { public String texture,side;public double[][] vertices; }
    private final Map<String,Map<String,Quad[]>> meshes=new HashMap<String,Map<String,Quad[]>>();
    private final Map<String,IIcon> icons=new HashMap<String,IIcon>();
    public DisplayRenderer(){
        try{
            Reader reader=new InputStreamReader(new GZIPInputStream(getClass().getResourceAsStream("/assets/displaydelight/legacy/meshes.json.gz")),"UTF-8");
            JsonObject data=new JsonParser().parse(reader).getAsJsonObject();reader.close();Gson gson=new Gson();
            for(Map.Entry<String,JsonElement> type:data.entrySet()){
                Map<String,Quad[]> states=new HashMap<String,Quad[]>();
                for(Map.Entry<String,JsonElement> state:type.getValue().getAsJsonObject().entrySet()){
                    Quad[] quads=gson.fromJson(state.getValue(),Quad[].class);states.put(state.getKey(),quads);
                    for(Quad q:quads)icons.put(q.texture,null);
                }
                meshes.put(type.getKey(),states);
            }
        }catch(IOException e){throw new IllegalStateException("Cannot load native food meshes",e);}
    }
    @SubscribeEvent public void stitch(TextureStitchEvent.Pre event){
        if(event.map.getTextureType()!=0)return;
        for(String name:icons.keySet())icons.put(name,event.map.registerIcon(nativeIcon(name)));
    }
    public static String nativeIcon(String name){return name.replace(":block/",":").replace(":blocks/",":");}
    private Quad[] geometry(DisplayBlock b,int count,boolean hidden,boolean support){
        Map<String,Quad[]> states=meshes.get(b.definition.id);
        Quad[] q=states.get(Math.max(1,Math.min(b.definition.max,count))+":"+(b.definition.plated()&&hidden?1:0)+":"+(support?1:0));
        if(q==null)throw new IllegalStateException("Missing display mesh "+b.definition.id);return q;
    }
    private void draw(DisplayBlock b,int count,boolean hidden,boolean support,int facing,double ox,double oy,double oz,boolean inventory){
        Tessellator t=Tessellator.instance;double angle=-facing*Math.PI/2,c=Math.cos(angle),s=Math.sin(angle);
        for(Quad quad:geometry(b,count,hidden,support)){
            IIcon icon=icons.get(quad.texture);if(icon==null)throw new IllegalStateException("Unstitched food texture "+quad.texture);
            double[][] v=quad.vertices;double ax=v[1][0]-v[0][0],ay=v[1][1]-v[0][1],az=v[1][2]-v[0][2];
            double bx=v[2][0]-v[0][0],by=v[2][1]-v[0][1],bz=v[2][2]-v[0][2];
            double nx=ay*bz-az*by,ny=az*bx-ax*bz,nz=ax*by-ay*bx,length=Math.sqrt(nx*nx+ny*ny+nz*nz);if(length<1E-10)continue;
            double rx=(nx*c+nz*s)/length,ry=ny/length,rz=(-nx*s+nz*c)/length;
            if(inventory)t.setNormal((float)rx,(float)ry,(float)rz);
            else{float light=(float)(0.6+0.4*Math.max(0,ry)-0.1*Math.max(0,-ry));t.setColorOpaque_F(light,light,light);}
            for(double[] p:v){double x=p[0]-.5,z=p[2]-.5;t.addVertexWithUV(ox+.5+x*c+z*s,oy+p[1],oz+.5-x*s+z*c,icon.getInterpolatedU(p[3]),icon.getInterpolatedV(p[4]));}
        }
    }
    @Override public void renderInventoryBlock(Block raw,int meta,int modelId,RenderBlocks renderer){
        DisplayBlock b=(DisplayBlock)raw;GL11.glPushMatrix();GL11.glTranslated(-.5,-.3,-.5);
        Tessellator.instance.startDrawingQuads();draw(b,b.definition.max,false,false,0,0,0,0,true);Tessellator.instance.draw();GL11.glPopMatrix();
    }
    @Override public boolean renderWorldBlock(IBlockAccess w,int x,int y,int z,Block raw,int modelId,RenderBlocks renderer){
        DisplayBlock b=(DisplayBlock)raw;Tessellator.instance.setBrightness(b.getMixedBrightnessForBlock(w,x,y,z));
        draw(b,b.count(w,x,y,z),b.hidden(w,x,y,z),b.support(w,x,y,z),w.getBlockMetadata(x,y,z)&3,x,y,z,false);return true;
    }
    @Override public boolean shouldRender3DInInventory(int modelId){return true;}
    @Override public int getRenderId(){return DisplayBlock.renderId;}
    public int displayTypes(){return meshes.size();}
}
