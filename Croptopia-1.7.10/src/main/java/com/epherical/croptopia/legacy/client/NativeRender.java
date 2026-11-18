package com.epherical.croptopia.legacy.client;
import com.epherical.croptopia.legacy.*;
import com.google.gson.*;
import java.util.*;
import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import org.lwjgl.opengl.GL11;
/** Original JSON geometry and textures rendered through the native block atlas. */
public final class NativeRender implements ISimpleBlockRenderingHandler {
    public final int id=RenderingRegistry.getNextAvailableRenderId();
    private final JsonObject models=Catalog.read("native_models"),states=Port.CONTENT.data.getAsJsonObject("blockstates");
    private final Map<String,IIcon> icons=new LinkedHashMap<String,IIcon>();
    public NativeRender(){Set<String> used=new HashSet<String>();Set<String> ids=new HashSet<String>(Port.CONTENT.blocks.keySet());ids.addAll(Port.CONTENT.crops.keySet());for(String block:ids){if(!states.has(block))continue;JsonObject definition=states.getAsJsonObject(block);if(!definition.has("variants"))continue;for(Map.Entry<String,JsonElement> variant:definition.getAsJsonObject("variants").entrySet()){JsonElement value=variant.getValue();if(value.isJsonArray())for(JsonElement v:value.getAsJsonArray())used.add(v.getAsJsonObject().get("model").getAsString());else used.add(value.getAsJsonObject().get("model").getAsString());}}for(Map.Entry<String,JsonElement> model:models.entrySet()){if(!used.contains(model.getKey()))continue;JsonObject j=model.getValue().getAsJsonObject();if(!j.has("textures"))continue;for(Map.Entry<String,JsonElement> tex:j.getAsJsonObject("textures").entrySet()){String s=tex.getValue().getAsString();if(!s.startsWith("#"))icons.put(texture(s),null);}}}
    public static String texture(String s){s=s.replace(":block/",":").replace(":blocks/",":");if(s.startsWith("minecraft:")){String n=s.substring(10);for(String wood:new String[]{"oak","spruce","birch","jungle","acacia","dark_oak"}){if(n.equals(wood+"_log"))n="log_"+(wood.equals("dark_oak")?"big_oak":wood);else if(n.equals(wood+"_log_top"))n="log_"+(wood.equals("dark_oak")?"big_oak":wood)+"_top";else if(n.equals(wood+"_leaves"))n="leaves_"+(wood.equals("dark_oak")?"big_oak":wood);}if(n.equals("flower_pot"))n="flower_pot";s="minecraft:"+n;}return s;}
    @SubscribeEvent public void stitch(TextureStitchEvent.Pre event){if(event.map.getTextureType()==0)for(String name:icons.keySet())icons.put(name,event.map.registerIcon(name));}
    public int checkSprites(){IIcon missing=net.minecraft.client.Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("missingno");for(Map.Entry<String,IIcon> e:icons.entrySet()){IIcon icon=e.getValue();if(icon==null||icon.getMinU()==missing.getMinU()&&icon.getMaxU()==missing.getMaxU()&&icon.getMinV()==missing.getMinV()&&icon.getMaxV()==missing.getMaxV())throw new AssertionError("Missing original block texture "+e.getKey());}return icons.size();}
    public int getRenderId(){return id;}public boolean shouldRender3DInInventory(int id){return true;}
    public void renderInventoryBlock(Block b,int meta,int type,RenderBlocks rb){GL11.glPushMatrix();GL11.glTranslatef(-.5F,-.5F,-.5F);Tessellator.instance.startDrawingQuads();draw(null,0,0,0,b,meta,true);Tessellator.instance.draw();GL11.glPopMatrix();}
    public boolean renderWorldBlock(IBlockAccess w,int x,int y,int z,Block b,int type,RenderBlocks rb){Tessellator.instance.setBrightness(b.getMixedBrightnessForBlock(w,x,y,z));draw(w,x,y,z,b,w.getBlockMetadata(x,y,z),false);return true;}
    private void draw(IBlockAccess w,int x,int y,int z,Block block,int meta,boolean inventory){String name=block instanceof Plant?((Plant)block).id:((GardenBlock)block).id;if(!states.has(name))return;JsonObject state=states.getAsJsonObject(name);if(!state.has("variants"))return;JsonObject selected=null;for(Map.Entry<String,JsonElement> e:state.getAsJsonObject("variants").entrySet()){if(!match(e.getKey(),w,x,y,z,block,meta,inventory))continue;JsonElement value=e.getValue();selected=value.isJsonArray()?value.getAsJsonArray().get(0).getAsJsonObject():value.getAsJsonObject();break;}if(selected==null)return;String model=Catalog.str(selected,"model","");if(!models.has(model))return;JsonObject json=models.getAsJsonObject(model);if(!json.has("elements"))return;
        int turns=Catalog.num(selected,"y",0)/90;JsonObject textures=json.getAsJsonObject("textures");
        for(JsonElement e:json.getAsJsonArray("elements")){JsonObject box=e.getAsJsonObject();double[] f=vec(box.getAsJsonArray("from")),t=vec(box.getAsJsonArray("to"));for(Map.Entry<String,JsonElement> face:box.getAsJsonObject("faces").entrySet()){JsonObject q=face.getValue().getAsJsonObject();String key=Catalog.str(q,"texture","");for(int i=0;key.startsWith("#")&&i<16;i++)key=textures.get(key.substring(1)).getAsString();IIcon icon=icons.get(texture(key));if(icon==null)continue;double[][] points=face(face.getKey(),f,t);for(double[] p:points){if(box.has("rotation"))rotate(p,box.getAsJsonObject("rotation"));for(int turn=0;turn<turns;turn++){double X=p[0];p[0]=1-p[2];p[2]=X;}}
                float shade=face.getKey().equals("up")?1:face.getKey().equals("down")?.5F:face.getKey().equals("north")||face.getKey().equals("south")?.8F:.6F;Tessellator tess=Tessellator.instance;
                if(inventory){tess.setNormal(face.getKey().equals("east")?1:face.getKey().equals("west")?-1:0,face.getKey().equals("up")?1:face.getKey().equals("down")?-1:0,face.getKey().equals("south")?1:face.getKey().equals("north")?-1:0);tess.setColorOpaque_F(1,1,1);}else{if(block.isOpaqueCube()){int X=x,Y=y,Z=z;String side=face.getKey();if(side.equals("up"))Y++;else if(side.equals("down"))Y--;else if(side.equals("east"))X++;else if(side.equals("west"))X--;else if(side.equals("south"))Z++;else Z--;tess.setBrightness(block.getMixedBrightnessForBlock(w,X,Y,Z));}int color=q.has("tintindex")?w.getBiomeGenForCoords(x,z).getBiomeFoliageColor(x,y,z):0xffffff;tess.setColorOpaque_F(((color>>16)&255)/255F*shade,((color>>8)&255)/255F*shade,(color&255)/255F*shade);}
                JsonArray uv=q.getAsJsonArray("uv");double u0=uv==null?0:uv.get(0).getAsDouble(),v0=uv==null?0:uv.get(1).getAsDouble(),u1=uv==null?16:uv.get(2).getAsDouble(),v1=uv==null?16:uv.get(3).getAsDouble();int spin=Catalog.num(q,"rotation",0)/90;for(int i=0;i<4;i++){int corner=(i+spin)&3;tess.addVertexWithUV(x+points[i][0],y+points[i][1],z+points[i][2],icon.getInterpolatedU(corner<2?u0:u1),icon.getInterpolatedV(corner==0||corner==3?v0:v1));}
        }}
    }
    private boolean match(String key,IBlockAccess w,int x,int y,int z,Block b,int meta,boolean inventory){for(String term:key.split(",")){if(term.isEmpty())continue;String[] p=term.split("=");if(p.length!=2)continue;String actual="";
        if(p[0].equals("age"))actual=""+(meta&7);
        else if(p[0].equals("persistent"))actual=""+((meta&8)!=0);
        else if(p[0].equals("distance"))actual="1";
        else if(p[0].equals("waterlogged"))actual="false";
        else if(p[0].equals("stage"))actual="0";
        else if(p[0].equals("axis"))actual=(meta&12)==4?"x":(meta&12)==8?"z":"y";
        else if(p[0].equals("supporting"))actual=""+(w!=null&&w.getBlock(x,y+1,z)==Port.CONTENT.crops.get("corn_upper"));
        else if(p[0].equals("facing")||p[0].equals("servings")){GardenTile tile=w==null?null:(w.getTileEntity(x,y,z) instanceof GardenTile?(GardenTile)w.getTileEntity(x,y,z):null);if(p[0].equals("facing"))actual=new String[]{"north","east","south","west"}[tile==null?0:tile.facing];else actual=""+(tile==null?((GardenBlock)b).id.equals("exotic_roll_medley")?8:4:tile.remaining);}
        else continue;if(!actual.equals(p[1]))return false;
    }return true;}
    private static double[] vec(JsonArray a){return new double[]{a.get(0).getAsDouble()/16,a.get(1).getAsDouble()/16,a.get(2).getAsDouble()/16};}
    private static void rotate(double[] p,JsonObject r){double[] o=vec(r.getAsJsonArray("origin"));String axis=r.get("axis").getAsString();int a=axis.equals("x")?1:axis.equals("y")?2:0,b=(a+1)%3;double angle=Math.toRadians(r.get("angle").getAsDouble()),c=Math.cos(angle),s=Math.sin(angle),X=p[a]-o[a],Y=p[b]-o[b],scale=Catalog.yes(r,"rescale")?1/c:1;p[a]=o[a]+(X*c-Y*s)*scale;p[b]=o[b]+(X*s+Y*c)*scale;}
    private static double[][] face(String n,double[] f,double[] t){double x=f[0],y=f[1],z=f[2],X=t[0],Y=t[1],Z=t[2];if(n.equals("down"))return new double[][]{{x,y,Z},{x,y,z},{X,y,z},{X,y,Z}};if(n.equals("up"))return new double[][]{{x,Y,z},{x,Y,Z},{X,Y,Z},{X,Y,z}};if(n.equals("north"))return new double[][]{{X,Y,z},{X,y,z},{x,y,z},{x,Y,z}};if(n.equals("south"))return new double[][]{{x,Y,Z},{x,y,Z},{X,y,Z},{X,Y,Z}};if(n.equals("west"))return new double[][]{{x,Y,z},{x,y,z},{x,y,Z},{x,Y,Z}};return new double[][]{{X,Y,Z},{X,y,Z},{X,y,z},{X,Y,z}};}
}
