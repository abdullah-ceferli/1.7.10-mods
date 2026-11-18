package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.common.registry.GameRegistry;import cpw.mods.fml.relauncher.*;
import net.minecraft.item.*;import net.minecraft.entity.EntityLivingBase;import net.minecraft.world.World;import net.minecraft.util.ResourceLocation;import net.minecraft.server.MinecraftServer;
import com.dhanantry.scapeandrunparasites.backport.item.*;

@Mod(modid="srp_item_property_checks",name="Item property compatibility checks",version="1")
public final class NativeItemPropertyChecks {
    public static final ResourceLocation ANGLE=new ResourceLocation("angle");
    public static FixtureItem item;public static int calls;public static ItemStack lastStack;public static World lastWorld;public static EntityLivingBase lastEntity;
    final List<String> report=new ArrayList<String>();int ticks,failed;boolean done;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event){item=new FixtureItem();GameRegistry.registerItem(item,"property_fixture");}
    @Mod.EventHandler public void init(FMLInitializationEvent event)throws Exception{if(event.getSide().isClient())Class.forName("dev.srpbackport.tests.NativeItemPropertyClientChecks").getMethod("initialize").invoke(null);else FMLCommonHandler.instance().bus().register(this);}
    public static final class FixtureItem extends Item {
        public FixtureItem(){ItemProperties.add(this,ANGLE,new IItemPropertyGetter(){
            @SideOnly(Side.CLIENT) double rotation;
            @SideOnly(Side.CLIENT) @Override public float apply(ItemStack stack,World world,EntityLivingBase entity){
                calls++;lastStack=stack;lastWorld=world;lastEntity=entity;rotation++;
                return(float)(stack.stackSize+rotation+(world==null?0:world.isRemote?100:200)+(entity==null?0:entity.getEyeHeight()));
            }
        });}
    }
    static final class EqualItem extends Item {
        @Override public boolean equals(Object other){return other instanceof EqualItem;}
        @Override public int hashCode(){return 1;}
    }
    static void check(boolean value,String why){if(!value)throw new AssertionError(why);}
    interface Check{void run()throws Exception;}
    void test(String name,Check body){try{body.run();report.add("PASS: "+name);}catch(Throwable t){failed++;report.add("FAIL: "+name+" :: "+t);t.printStackTrace();}}
    void tests(){
        test("Native items and armor retain isolated callback registries, namespaced keys and replacement semantics",()->{
            Item a=new EqualItem(),b=new EqualItem(),armor=new ItemArmor(ItemArmor.ArmorMaterial.IRON,0,0);
            IItemPropertyGetter one=(s,w,e)->1,two=(s,w,e)->2;
            ItemProperties.add(a,new ResourceLocation("angle"),one);ItemProperties.add(b,new ResourceLocation("minecraft","angle"),two);ItemProperties.add(armor,new ResourceLocation("angle"),two);
            check(ItemProperties.get(a,ANGLE)==one&&ItemProperties.get(b,ANGLE)==two&&ItemProperties.get(armor,ANGLE)==two,"Item identity/inheritance");
            ItemProperties.add(a,new ResourceLocation("srparasites","angle"),two);check(ItemProperties.get(a,ANGLE)==one,"Namespace collision");
            ItemProperties.add(a,new ResourceLocation("minecraft","angle"),two);check(ItemProperties.get(a,ANGLE)==two&&ItemProperties.get(b,ANGLE)==two,"Replacement lost");
            check(ItemProperties.get(new Item(),ANGLE)==null&&ItemProperties.get(a,new ResourceLocation("missing"))==null,"Missing getter fabricated");
        });
        test("Null registration rejects before mutation and absent/null lookup keys return no callback",()->{
            Item a=new Item();IItemPropertyGetter one=(s,w,e)->1;
            int rejected=0;for(int mode=0;mode<3;mode++)try{ItemProperties.add(mode==0?null:a,mode==1?null:ANGLE,mode==2?null:one);}catch(NullPointerException expected){rejected++;}
            check(rejected==3&&ItemProperties.get(a,ANGLE)==null,"Null registration mutated registry");
            ItemProperties.add(a,ANGLE,one);check(ItemProperties.get(a,null)==null&&ItemProperties.get(a,ANGLE)==one,"Null lookup changed registry");
        });
        test("Dedicated-server registration preserves the callback object without invoking client-only members stripped by Forge",()->{
            IItemPropertyGetter getter=ItemProperties.get(item,ANGLE);check(getter!=null&&calls==0,"Eager callback invocation");
            boolean field=false,method=false;for(java.lang.reflect.Field f:getter.getClass().getDeclaredFields())field|=f.getName().equals("rotation");for(java.lang.reflect.Method m:getter.getClass().getDeclaredMethods())method|=m.getName().equals("apply");
            check(!field&&!method,"Forge did not strip fixture client members");check(Item.itemRegistry.getObject("srp_item_property_checks:property_fixture")==item,"Native item registration");
        });
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(done||event.phase!=TickEvent.Phase.END||++ticks!=20)return;try{tests();}finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get("item-properties-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}MinecraftServer.getServer().initiateShutdown();}}
}
