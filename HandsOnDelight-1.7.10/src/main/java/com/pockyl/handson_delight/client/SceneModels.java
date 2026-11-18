package com.pockyl.handson_delight.client;

import com.google.gson.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vectorwing.farmersdelight.legacy.*;

/** Original Diegesis block-model cuboids, rotations, face UVs and textures. */
public final class SceneModels {
    private static final Map<String,JsonObject> MODELS=new HashMap<String,JsonObject>();
    public static boolean replaces(Block block){return block==Content.cookingPot||block==Content.skillet;}
    static void draw(String name){
        JsonObject model=MODELS.get(name);if(model==null){model=SourceScene.read("/assets/handson_delight/models/diegesis/"+name+".json");MODELS.put(name,model);}
        JsonObject textures=model.getAsJsonObject("textures");Minecraft mc=Minecraft.getMinecraft();GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glDisable(GL11.GL_CULL_FACE);GL11.glEnable(GL11.GL_ALPHA_TEST);GL11.glAlphaFunc(GL11.GL_GREATER,.1F);GL11.glColor4f(1,1,1,1);
        for(JsonElement element:model.getAsJsonArray("elements")){
            JsonObject box=element.getAsJsonObject();double[] f=SourceScene.vec(box,"from",null),to=SourceScene.vec(box,"to",null);double x=f[0]-8,y=f[1],z=f[2]-8,X=to[0]-8,Y=to[1],Z=to[2]-8;
            GL11.glPushMatrix();
            if(box.has("rotation")){JsonObject r=box.getAsJsonObject("rotation");double[] o=SourceScene.vec(r,"origin",null);String axis=r.get("axis").getAsString();GL11.glTranslated(o[0]-8,o[1],o[2]-8);GL11.glRotatef(r.get("angle").getAsFloat(),axis.equals("x")?1:0,axis.equals("y")?1:0,axis.equals("z")?1:0);GL11.glTranslated(8-o[0],-o[1],8-o[2]);}
            String[] faces={"north","south","west","east","up","down"};
            double[][][] vertices={{{X,Y,z},{X,y,z},{x,y,z},{x,Y,z}},{{x,Y,Z},{x,y,Z},{X,y,Z},{X,Y,Z}},{{x,Y,z},{x,y,z},{x,y,Z},{x,Y,Z}},{{X,Y,Z},{X,y,Z},{X,y,z},{X,Y,z}},{{x,Y,z},{x,Y,Z},{X,Y,Z},{X,Y,z}},{{x,y,Z},{x,y,z},{X,y,z},{X,y,Z}}};
            JsonObject defs=box.getAsJsonObject("faces");
            for(int n=0;n<faces.length;n++){if(!defs.has(faces[n]))continue;JsonObject face=defs.getAsJsonObject(faces[n]);String texture=face.get("texture").getAsString();for(int i=0;i<16&&texture.startsWith("#");i++)texture=textures.get(texture.substring(1)).getAsString();String[] id=texture.split(":",2);mc.getTextureManager().bindTexture(new ResourceLocation(id[0],"textures/"+id[1].replace("block/","blocks/")+".png"));JsonArray uv=face.getAsJsonArray("uv");double u=uv.get(0).getAsDouble()/16,v=uv.get(1).getAsDouble()/16,U=uv.get(2).getAsDouble()/16,V=uv.get(3).getAsDouble()/16;double[][] tex={{u,v},{u,V},{U,V},{U,v}};int turns=face.has("rotation")?face.get("rotation").getAsInt()/90:0;
                Tessellator t=Tessellator.instance;t.startDrawingQuads();t.setNormal(n==2?-1:n==3?1:0,n==4?1:n==5?-1:0,n==0?-1:n==1?1:0);for(int i=0;i<4;i++){double[] p=vertices[n][i],a=tex[(i+turns)&3];t.addVertexWithUV(p[0],p[1],p[2],a[0],a[1]);}t.draw();
            }
            GL11.glPopMatrix();
        }
    }
}
