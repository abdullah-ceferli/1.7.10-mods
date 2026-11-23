package dev.zahin.mobileperformance.client;

import dev.zahin.mobileperformance.RenderPolicy;
import java.io.File;
import net.minecraftforge.common.config.Configuration;

public final class PerformanceConfig {
    public boolean enabled=true;
    public int mobDistance=48,itemDistance=24,tileDistance=32,particleDistance=16,particlesPerTick=64,animationInterval=1;
    private final Configuration file;
    public PerformanceConfig(File path){file=new Configuration(path);file.load();enabled=file.get("rendering","enabled",true).getBoolean(true);mobDistance=read("mobDistance",48,16,128);itemDistance=read("itemDistance",24,8,128);tileDistance=read("tileDistance",32,16,128);particleDistance=read("particleDistance",16,8,128);particlesPerTick=read("particlesPerTick",64,8,512);animationInterval=read("animationInterval",1,1,4);save();}
    private int read(String name,int value,int min,int max){return RenderPolicy.bound(file.get("rendering",name,value).getInt(value),min,max);}
    public void save(){file.get("rendering","enabled",true).set(enabled);file.get("rendering","mobDistance",48).set(mobDistance);file.get("rendering","itemDistance",24).set(itemDistance);file.get("rendering","tileDistance",32).set(tileDistance);file.get("rendering","particleDistance",16).set(particleDistance);file.get("rendering","particlesPerTick",64).set(particlesPerTick);file.get("rendering","animationInterval",1).set(animationInterval);file.save();}
}
