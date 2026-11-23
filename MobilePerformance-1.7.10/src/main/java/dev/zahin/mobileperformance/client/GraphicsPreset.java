package dev.zahin.mobileperformance.client;

import com.google.gson.*;
import dev.zahin.mobileperformance.GraphicsState;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

/** Owns only the fields it changes. The first snapshot survives repeated applications. */
public final class GraphicsPreset {
    private final File file;
    public GraphicsPreset(File file){this.file=file;}
    private GraphicsState snapshot(GameSettings g,PerformanceConfig c)throws IOException{return new GraphicsState(g.renderDistanceChunks,g.particleSetting,g.ambientOcclusion,g.limitFramerate,g.mipmapLevels,c.animationInterval,g.fancyGraphics,g.clouds,g.enableVsync);}
    private void write(GraphicsState state)throws IOException{Path p=file.toPath();Files.createDirectories(p.getParent());Path temp=p.resolveSibling(p.getFileName()+".tmp");Files.write(temp,new GsonBuilder().setPrettyPrinting().create().toJson(state.json()).getBytes(StandardCharsets.UTF_8));try{Files.move(temp,p,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException e){Files.move(temp,p);}}
    public void apply(PerformanceConfig c)throws IOException{Minecraft mc=Minecraft.getMinecraft();GameSettings g=mc.gameSettings;if(!file.exists())write(snapshot(g,c));else read();boolean reload=g.mipmapLevels!=0||g.fancyGraphics||g.renderDistanceChunks!=4||g.ambientOcclusion!=0;g.renderDistanceChunks=4;g.particleSetting=2;g.fancyGraphics=false;g.ambientOcclusion=0;g.clouds=false;g.enableVsync=false;g.limitFramerate=60;g.mipmapLevels=0;c.animationInterval=2;c.save();g.saveOptions();syncRenderer(mc,reload);}
    private GraphicsState read()throws IOException{try(Reader r=new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8)){return GraphicsState.read(new JsonParser().parse(r).getAsJsonObject());}catch(RuntimeException e){throw new IOException("Invalid graphics backup; original file retained",e);}}
    public boolean restore(PerformanceConfig c)throws IOException{if(!file.exists())return false;GraphicsState n=read();Minecraft mc=Minecraft.getMinecraft();GameSettings g=mc.gameSettings;boolean reload=g.renderDistanceChunks!=n.distance||g.fancyGraphics!=n.fancy||g.mipmapLevels!=n.mipmaps||g.ambientOcclusion!=n.ao;g.renderDistanceChunks=n.distance;g.particleSetting=n.particles;g.fancyGraphics=n.fancy;g.ambientOcclusion=n.ao;g.clouds=n.clouds;g.enableVsync=n.vsync;g.limitFramerate=n.fps;g.mipmapLevels=n.mipmaps;c.animationInterval=n.animation;c.save();g.saveOptions();syncRenderer(mc,reload);Files.delete(file.toPath());return true;}
    private void syncRenderer(Minecraft mc,boolean reload){mc.getTextureMapBlocks().setMipmapLevels(mc.gameSettings.mipmapLevels);org.lwjgl.opengl.Display.setVSyncEnabled(mc.gameSettings.enableVsync);if(reload){mc.refreshResources();if(mc.theWorld!=null)mc.renderGlobal.loadRenderers();}}
    public boolean exists(){return file.exists();}
}
