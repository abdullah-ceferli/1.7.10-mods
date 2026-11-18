package dev.srpbackport.tests;
import java.io.*;import java.lang.reflect.*;import java.util.*;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.entity.Entity;import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.*;import net.minecraft.event.ClickEvent;
public final class SoundCatalogChecks {
    static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    static Method inherited(Class<?> type,String name,Class<?>... args)throws Exception{for(Class<?> c=type;c!=null;c=c.getSuperclass())try{return c.getDeclaredMethod(name,args);}catch(NoSuchMethodException ignored){}throw new NoSuchMethodException(name);}
    public static void main(String[] args)throws Exception{
        Properties expected=new Properties();try(InputStream input=new FileInputStream(args[0])){expected.load(input);}
        SRPSounds.init();int values=0,nulls=0;
        for(String name:expected.stringPropertyNames()){
            Field field=SRPSounds.class.getField(name);check(field.getType()==String.class,"Sound catalog must use 1.7 string IDs: "+name);String value=(String)field.get(null);String key=expected.getProperty(name);
            if(key.equals("__UPSTREAM_NULL__")){check(value==null,"Originally unassigned field was changed: "+name);nulls++;}
            else{check(key.equals(value),"Original sound ID changed: "+name);values++;}
        }
        check(SRPSounds.class.getFields().length==expected.size(),"Sound catalog field list changed");
        check(inherited(Entity.class,"playSound",String.class,float.class,float.class).getReturnType()==void.class,"1.7 entity sound API");
        for(String name:new String[]{"getLivingSound","getHurtSound","getDeathSound"})check(inherited(EntityMob.class,name).getReturnType()==String.class,"1.7 sound callback type "+name);
        IChatComponent text=new ChatComponentText("SRP target").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/srp-test")));
        String json=IChatComponent.Serializer.func_150696_a(text);IChatComponent copy=IChatComponent.Serializer.func_150699_a(json);check(copy.getUnformattedText().equals("SRP target")&&copy.getChatStyle().getColor()==EnumChatFormatting.RED&&copy.getChatStyle().getChatClickEvent().getValue().equals("/srp-test"),"Native 1.7 chat style/command JSON round trip");
        List<String> report=Arrays.asList("PASS: all "+values+" original custom sound IDs use native 1.7.10 string identifiers","PASS: "+nulls+" originally uninitialized sound fields preserved without invented IDs","PASS: actual 1.7.10 playSound and ambient/hurt/death callback signatures","PASS: actual 1.7.10 styled clickable text survives JSON serialization","Failed: 0");
        for(String line:report)System.out.println(line);try(PrintWriter writer=new PrintWriter(args[1],"UTF-8")){for(String line:report)writer.println(line);}
    }
}
