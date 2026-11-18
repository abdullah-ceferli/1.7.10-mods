package com.pockyl.handson_delight.client;

import com.google.gson.*;
import java.io.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import vectorwing.farmersdelight.legacy.*;

/** Reads the authored Hands-On scene, in Diegesis's sixteen-pixel station space. */
public final class SourceScene {
    public final JsonObject definition;
    public final double[] eye,target,right,up,forward;
    public float fov;public final float baseFov,workPlane;
    public final int slotColor,background;
    public final float margin,align;
    public final Map<String,Node> nodes=new LinkedHashMap<String,Node>();
    public final float yaw;
    public static final class Node {
        public final JsonObject source;public final double[] position,rotation,from,to;public final float scale;
        Node(JsonObject s,double[] parent){source=s;position=vec(s,"pos",new double[3]);for(int i=0;i<3;i++)position[i]+=parent[i];rotation=vec(s,"rotation",new double[3]);scale=s.has("scale")?s.get("scale").getAsFloat():1;JsonObject box=s.has("collider")?s.getAsJsonObject("collider"):null;from=box==null?null:vec(box,"from",null);to=box==null?null:vec(box,"to",null);}
    }
    public SourceScene(InventoryTile tile){
        String id=tile instanceof CookingPotTile?"cooking_pot":tile instanceof CuttingBoardTile?"cutting_board":tile instanceof SkilletTile?"skillet":"stove";
        definition=read("/assets/handson_delight/diegesis/scenes/"+id+".json");
        JsonObject camera=definition.getAsJsonObject("camera");eye=vec(camera,"position",null);target=vec(camera,"target",null);baseFov=fov=camera.get("fov").getAsFloat();workPlane=definition.get("work_plane").getAsFloat();
        forward=normal(sub(target,eye));right=normal(cross(forward,new double[]{0,1,0}));up=cross(right,forward);
        JsonObject drawer=definition.getAsJsonObject("inventory_drawer");slotColor=color(drawer,"slot_color",0x66000000);background=color(drawer,"background",0xc0101216);margin=number(drawer,"margin",4);align=number(drawer,"align",.5F);
        collect(definition.getAsJsonArray("nodes"),new double[3]);
        net.minecraft.entity.player.EntityPlayer player=Minecraft.getMinecraft().thePlayer;
        double x=player.posX-tile.xCoord-.5,z=player.posZ-tile.zCoord-.5;
        // StationType.Orientation.PLAYER: north is the scene as authored.
        yaw=tile instanceof GrillingTile?90*(tile.getBlockMetadata()&3):Math.abs(x)>Math.abs(z)?x>0?90:270:z>0?180:0;
    }
    public void reshape(int width,int height){float ratio=(float)width/height;fov=ratio<16F/9?2*(float)Math.toDegrees(Math.atan(Math.tan(Math.toRadians(baseFov/2))*(16F/9)/ratio)):baseFov;}
    private void collect(JsonArray list,double[] parent){for(JsonElement e:list){Node n=new Node(e.getAsJsonObject(),parent);String id=n.source.get("id").getAsString();if(n.source.has("repeat")){JsonObject repeat=n.source.getAsJsonObject("repeat");double[] count=vec(repeat,"count",null),step=vec(repeat,"step",null);int total=(int)(count[0]*count[1]*count[2]);for(int i=0;i<total;i++){JsonObject copy=new JsonParser().parse(n.source.toString()).getAsJsonObject();JsonArray pos=new JsonArray();for(int j=0;j<3;j++)pos.add(new JsonPrimitive(n.position[j]+step[j]*i));copy.add("pos",pos);nodes.put(id+"_"+i,new Node(copy,new double[3]));}}else nodes.put(id,n);if(n.source.has("children"))collect(n.source.getAsJsonArray("children"),n.position);}}
    public static JsonObject read(String path){try(InputStream in=SourceScene.class.getResourceAsStream(path)){if(in==null)throw new IOException(path);return new JsonParser().parse(new InputStreamReader(in,"UTF-8")).getAsJsonObject();}catch(IOException e){throw new IllegalStateException("Cannot load original scene "+path,e);}}
    static double[] vec(JsonObject o,String key,double[] fallback){if(!o.has(key))return fallback;JsonArray a=o.getAsJsonArray(key);return new double[]{a.get(0).getAsDouble(),a.get(1).getAsDouble(),a.get(2).getAsDouble()};}
    private static float number(JsonObject o,String key,float fallback){return o.has(key)?o.get(key).getAsFloat():fallback;}
    private static int color(JsonObject o,String key,int fallback){return o.has(key)?(int)Long.parseLong(o.get(key).getAsString().substring(1),16):fallback;}
    static double[] sub(double[] a,double[] b){return new double[]{a[0]-b[0],a[1]-b[1],a[2]-b[2]};}
    static double dot(double[] a,double[] b){return a[0]*b[0]+a[1]*b[1]+a[2]*b[2];}
    static double[] cross(double[] a,double[] b){return new double[]{a[1]*b[2]-a[2]*b[1],a[2]*b[0]-a[0]*b[2],a[0]*b[1]-a[1]*b[0]};}
    static double[] normal(double[] a){double d=Math.sqrt(dot(a,a));return new double[]{a[0]/d,a[1]/d,a[2]/d};}
    public double[] world(InventoryTile t,double[] point){double r=Math.toRadians(-yaw),x=point[0]/16-.5,z=point[2]/16-.5;return new double[]{t.xCoord+.5+x*Math.cos(r)+z*Math.sin(r),t.yCoord+point[1]/16,t.zCoord+.5-x*Math.sin(r)+z*Math.cos(r)};}
    public double[] local(double x,double y,double z,InventoryTile tile){double dx=(x-tile.xCoord-.5)*16,dz=(z-tile.zCoord-.5)*16,r=Math.toRadians(yaw);return new double[]{8+dx*Math.cos(r)+dz*Math.sin(r),(y-tile.yCoord)*16,8-dx*Math.sin(r)+dz*Math.cos(r)};}
    public int[] project(double x,double y,double z,int width,int height){double[] p=sub(new double[]{x,y,z},eye);double d=dot(p,forward),k=height/(2*Math.tan(Math.toRadians(fov/2)));if(d<=0)return null;return new int[]{(int)Math.round(width/2.+dot(p,right)*k/d),(int)Math.round(height/2.-dot(p,up)*k/d)};}
    public double[] ray(int mx,int my,int width,int height){double k=height/(2*Math.tan(Math.toRadians(fov/2))),x=(mx-width/2.)/k,y=(height/2.-my)/k;return normal(new double[]{forward[0]+right[0]*x+up[0]*y,forward[1]+right[1]*x+up[1]*y,forward[2]+right[2]*x+up[2]*y});}
    public double[] plane(int mx,int my,int width,int height,double plane){double[] ray=ray(mx,my,width,height);double d=(plane-eye[1])/ray[1];return new double[]{eye[0]+ray[0]*d,plane,eye[2]+ray[2]*d};}
    public boolean hit(String id,int mx,int my,int width,int height){Node n=nodes.get(id);if(n==null||n.from==null)return false;double[] ray=ray(mx,my,width,height),origin=eye;
        if(id.equals("ladle")){net.minecraft.client.gui.GuiScreen gui=Minecraft.getMinecraft().currentScreen;if(gui instanceof StationScreen){StationScreen station=(StationScreen)gui;org.joml.Matrix4f inverse=new org.joml.Matrix4f(StationVisuals.of(station.tile).spoonPose).invert();org.joml.Vector3f p=inverse.transformPosition(new org.joml.Vector3f((float)eye[0]-8,(float)eye[1],(float)eye[2]-8)),d=inverse.transformDirection(new org.joml.Vector3f((float)ray[0],(float)ray[1],(float)ray[2]));origin=new double[]{p.x+8,p.y,p.z+8};ray=new double[]{d.x,d.y,d.z};}}
        double near=0,far=Double.POSITIVE_INFINITY;for(int i=0;i<3;i++){double a=n.position[i]+n.from[i],b=n.position[i]+n.to[i];if(Math.abs(ray[i])<1e-9){if(origin[i]<a||origin[i]>b)return false;continue;}double x=(a-origin[i])/ray[i],y=(b-origin[i])/ray[i];near=Math.max(near,Math.min(x,y));far=Math.min(far,Math.max(x,y));}return far>=near;}
    /** Screen corners on the cookbook plane, as RecipeBook.onScreen in the original framework. */
    public float[] bookPlane(int mx,int my,int width,int height){
        Node n=nodes.get("book_open");double[] p=inverse(sub(eye,n.position),n.rotation),d=inverse(ray(mx,my,width,height),n.rotation);
        double t=-p[1]/d[1];return new float[]{(float)((p[0]+d[0]*t)/n.scale),(float)((p[2]+d[2]*t)/n.scale)};
    }
    private static double[] inverse(double[] v,double[] angles){
        double[] a=v.clone();for(int axis=2;axis>=0;axis--){double r=Math.toRadians(-angles[axis]),c=Math.cos(r),s=Math.sin(r);int i=(axis+1)%3,j=(axis+2)%3;double x=a[i],y=a[j];a[i]=x*c-y*s;a[j]=x*s+y*c;}return a;
    }
    void begin(int width,int height){GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glPushMatrix();GL11.glLoadIdentity();GLU.gluPerspective(fov,(float)width/height,.16F,1600);GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPushMatrix();GL11.glLoadIdentity();GLU.gluLookAt((float)eye[0],(float)eye[1],(float)eye[2],(float)target[0],(float)target[1],(float)target[2],0,1,0);}
    void end(){GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPopMatrix();GL11.glMatrixMode(GL11.GL_PROJECTION);GL11.glPopMatrix();GL11.glMatrixMode(GL11.GL_MODELVIEW);}
    void node(String id){Node n=nodes.get(id);GL11.glTranslated(n.position[0],n.position[1],n.position[2]);GL11.glRotated(n.rotation[2],0,0,1);GL11.glRotated(n.rotation[1],0,1,0);GL11.glRotated(n.rotation[0],1,0,0);GL11.glScalef(n.scale,n.scale,n.scale);}
}
