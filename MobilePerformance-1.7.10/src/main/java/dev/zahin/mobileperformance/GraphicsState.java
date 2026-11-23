package dev.zahin.mobileperformance;

import com.google.gson.*;
import java.io.IOException;

/** Validated JSON snapshot; malformed backups cannot partially overwrite settings. */
public final class GraphicsState {
    public final int distance,particles,ao,fps,mipmaps,animation;
    public final boolean fancy,clouds,vsync;
    public GraphicsState(int d,int p,int a,int f,int m,int animation,boolean fancy,boolean clouds,boolean vsync)throws IOException{
        if(d<1||d>64||p<0||p>2||a<0||a>2||f<0||f>1000||m<0||m>4||animation<1||animation>4)throw new IOException("Graphics backup values are invalid; original file retained");
        distance=d;particles=p;ao=a;fps=f;mipmaps=m;this.animation=animation;this.fancy=fancy;this.clouds=clouds;this.vsync=vsync;
    }
    public JsonObject json(){JsonObject n=new JsonObject();n.addProperty("format",1);n.addProperty("renderDistance",distance);n.addProperty("particles",particles);n.addProperty("ao",ao);n.addProperty("fps",fps);n.addProperty("mipmaps",mipmaps);n.addProperty("animationInterval",animation);n.addProperty("fancy",fancy);n.addProperty("clouds",clouds);n.addProperty("vsync",vsync);return n;}
    private static int integer(JsonObject n,String k)throws IOException{JsonElement e=n.get(k);if(e==null||!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isNumber()||e.getAsDouble()!=e.getAsInt())throw new IOException("Invalid graphics backup field: "+k);return e.getAsInt();}
    private static boolean bool(JsonObject n,String k)throws IOException{JsonElement e=n.get(k);if(e==null||!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isBoolean())throw new IOException("Invalid graphics backup field: "+k);return e.getAsBoolean();}
    public static GraphicsState read(JsonObject n)throws IOException{try{if(integer(n,"format")!=1)throw new IOException("Unsupported graphics backup format");return new GraphicsState(integer(n,"renderDistance"),integer(n,"particles"),integer(n,"ao"),integer(n,"fps"),integer(n,"mipmaps"),integer(n,"animationInterval"),bool(n,"fancy"),bool(n,"clouds"),bool(n,"vsync"));}catch(RuntimeException e){throw new IOException("Invalid graphics backup; original file retained",e);}}
}
