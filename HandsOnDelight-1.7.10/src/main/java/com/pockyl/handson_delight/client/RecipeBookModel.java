package com.pockyl.handson_delight.client;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** The upstream Blockbench mesh and UVs, retaining fractional cubes that ModelRenderer cannot represent. */
final class RecipeBookModel {
    static final ResourceLocation TEXTURE=new ResourceLocation("handson_delight:textures/entity/recipe_book.png");
    private final JsonArray parts;
    RecipeBookModel(){
        try(InputStream in=RecipeBookModel.class.getResourceAsStream("/assets/handson_delight/models/recipe_book.json")){
            parts=new JsonParser().parse(new InputStreamReader(in,StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("parts");
        }catch(IOException e){throw new IllegalStateException("Cannot load original cookbook model",e);}
    }
    void render(float angle){
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);GL11.glColor4f(1,1,1,1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glEnable(GL11.GL_ALPHA_TEST);GL11.glAlphaFunc(GL11.GL_GREATER,.1F);GL11.glDisable(GL11.GL_CULL_FACE);
        for(JsonElement element:parts){JsonObject part=element.getAsJsonObject();String name=part.get("name").getAsString();boolean front=name.startsWith("front"),back=name.startsWith("back");
            GL11.glPushMatrix();
            if((front||back)&&!name.contains("cover"))GL11.glTranslatef((float)Math.sin(angle),0,0);
            float pivot=name.contains("cover")?(front?1:-1):0;GL11.glTranslatef(0,0,pivot);if(front||back)GL11.glRotatef((front?-angle:angle)*180F/(float)Math.PI,0,1,0);GL11.glTranslatef(0,0,-pivot);
            for(JsonElement cube:part.getAsJsonArray("cubes"))cube(cube.getAsJsonObject());GL11.glPopMatrix();
        }
    }
    private static void cube(JsonObject c){
        JsonArray from=c.getAsJsonArray("from"),to=c.getAsJsonArray("to");float inflate=c.has("inflate")?c.get("inflate").getAsFloat():0;
        double x=from.get(0).getAsDouble()-inflate,y=from.get(1).getAsDouble()-24-inflate,z=from.get(2).getAsDouble()-inflate;
        double X=to.get(0).getAsDouble()+inflate,Y=to.get(1).getAsDouble()-24+inflate,Z=to.get(2).getAsDouble()+inflate;
        double[][][] p={{{X,Y,z},{X,y,z},{x,y,z},{x,Y,z}},{{x,Y,Z},{x,y,Z},{X,y,Z},{X,Y,Z}},{{x,Y,z},{x,y,z},{x,y,Z},{x,Y,Z}},{{X,Y,Z},{X,y,Z},{X,y,z},{X,Y,z}},{{x,Y,z},{x,Y,Z},{X,Y,Z},{X,Y,z}},{{x,y,Z},{x,y,z},{X,y,z},{X,y,Z}}};
        String[] names={"north","south","west","east","up","down"};
        for(int face=0;face<6;face++){
            JsonArray uv=c.getAsJsonObject("faces").getAsJsonObject(names[face]).getAsJsonArray("uv");
            double u=uv.get(0).getAsDouble()/64,v=uv.get(1).getAsDouble()/32,U=uv.get(2).getAsDouble()/64,V=uv.get(3).getAsDouble()/32;
            Tessellator t=Tessellator.instance;t.startDrawingQuads();double[][] q=p[face];double[][] tex={{u,v},{u,V},{U,V},{U,v}};
            for(int j=0;j<4;j++)t.addVertexWithUV(q[j][0],q[j][1],q[j][2],tex[j][0],tex[j][1]);t.draw();
        }
    }
}
