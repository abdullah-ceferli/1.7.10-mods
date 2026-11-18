package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.*;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.common.registry.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.*;import net.minecraft.entity.*;import net.minecraft.entity.passive.EntityPig;import net.minecraft.entity.monster.EntityZombie;import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.ai.attributes.*;import net.minecraft.item.*;import net.minecraft.init.Items;import net.minecraft.nbt.*;import net.minecraft.potion.Potion;import net.minecraft.server.MinecraftServer;import net.minecraft.util.DamageSource;
import net.minecraftforge.common.*;import net.minecraftforge.common.util.FakePlayer;import net.minecraftforge.event.entity.living.LivingHurtEvent;
import com.dhanantry.scapeandrunparasites.backport.entity.*;
import com.dhanantry.scapeandrunparasites.backport.potion.*;

@Mod(modid="srp_armor_checks",name="Disposable native armor checks",version="1")
public final class NativeArmorChecks {
    static final DamageSource HIT=new DamageSource("srp_armor_fixture");
    static final UUID MODIFIER=UUID.fromString("90a0927b-5b20-46ec-88db-13a83e33ffd2");
    static final Path SAVE=Paths.get("armor-fixture.dat");static Item special;
    final List<String> report=new ArrayList<String>();int ticks,failed;boolean done,restart;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
        ArmorAttributeEvents.register();ArmorAttributeEvents.register();PotionFxEvents.register();
        special=new SpecialArmor();GameRegistry.registerItem(special,"fixture_special_armor");
        EntityRegistry.registerModEntity(Actor.class,"armor_actor",0,this,64,1,true);restart=Files.exists(SAVE);
    }
    @Mod.EventHandler public void init(FMLInitializationEvent event)throws Exception {
        if(event.getSide().isClient())Class.forName("dev.srpbackport.tests.NativeArmorClientChecks").getMethod("initialize").invoke(null);
        else FMLCommonHandler.instance().bus().register(this);
    }
    public static class Actor extends LegacyEntityMob {
        int armorCalls,wearCalls;public Actor(World w){super(w);}
        @Override protected void applyEntityAttributes(){super.applyEntityAttributes();getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100);getEntityAttribute(ModernAttributes.ARMOR).setBaseValue(20);}
        @Override protected float applyArmorCalculations(DamageSource source,float damage){armorCalls++;return super.applyArmorCalculations(source,damage);}
        @Override protected void damageArmor(float damage){wearCalls++;}
        public void hit(DamageSource source,float damage){damageEntity(source,damage);}
    }
    static class Pig extends EntityPig {Pig(World w){super(w);}void hit(float damage){damageEntity(HIT,damage);}}
    static class Player extends FakePlayer {
        Player(WorldServer w){super(w,new GameProfile(UUID.fromString("cfbab508-9b24-4ef0-ab23-c0f4a9b01949"),"SRPArmorQA"));}
        @Override public boolean isEntityInvulnerable(){return false;}
        void hit(float damage){damageEntity(HIT,damage);}
    }
    static class SpecialArmor extends Item implements ISpecialArmor {
        int queries,wear;SpecialArmor(){setMaxStackSize(1);setMaxDamage(1000);}
        public ArmorProperties getProperties(EntityLivingBase e,ItemStack s,DamageSource d,double damage,int slot){queries++;return new ArmorProperties(0,0.5,100000);}
        public int getArmorDisplay(net.minecraft.entity.player.EntityPlayer e,ItemStack s,int slot){return 0;}
        public void damageArmor(EntityLivingBase e,ItemStack s,DamageSource d,int damage,int slot){wear++;}
    }
    public static class HurtHook {
        Actor target;int mode;
        @SubscribeEvent public void hurt(LivingHurtEvent event){if(event.entityLiving==target){if(mode==1)event.ammount*=0.5F;if(mode==2)event.setCanceled(true);}}
    }
    interface Check {void run()throws Exception;}
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    static void near(double a,double b,String why){check(Math.abs(a-b)<0.00003,why+": "+a+" != "+b);}
    static IAttributeInstance armor(EntityLivingBase e){return e.getEntityAttribute(ModernAttributes.ARMOR);}
    static IAttributeInstance tough(EntityLivingBase e){return e.getEntityAttribute(ModernAttributes.ARMOR_TOUGHNESS);}
    static void diamond(Actor a){a.setCurrentItemOrArmor(1,new ItemStack(Items.diamond_boots));a.setCurrentItemOrArmor(2,new ItemStack(Items.diamond_leggings));a.setCurrentItemOrArmor(3,new ItemStack(Items.diamond_chestplate));a.setCurrentItemOrArmor(4,new ItemStack(Items.diamond_helmet));}
    void test(String name,Check body){try{body.run();report.add("PASS: "+name);}catch(Throwable problem){failed++;report.add("FAIL: "+name+" :: "+problem);problem.printStackTrace();}}
    static NBTTagCompound saved(Actor actor){NBTTagCompound tag=new NBTTagCompound();actor.writeToNBT(tag);return tag;}
    void tests(WorldServer w) {
        test("Native watched attributes register before subclass setup and NBT on SRP mobs, players and vanilla living entities",()->{
            Actor a=new Actor(w);near(20,armor(a).getBaseValue(),"Subclass base setup");
            for(EntityLivingBase entity:Arrays.asList(a,new EntityPig(w),new Player(w),new EntityZombie(w),new EntityWither(w)))check(armor(entity)!=null&&tough(entity)!=null,"Global native registration missing for "+entity.getClass());
            check(ModernAttributes.ARMOR.getAttributeUnlocalizedName().equals("generic.armor")&&ModernAttributes.ARMOR_TOUGHNESS.getAttributeUnlocalizedName().equals("generic.armorToughness"),"Names changed");
            check(ModernAttributes.ARMOR.getShouldWatch()&&ModernAttributes.ARMOR_TOUGHNESS.getShouldWatch(),"Not watched");armor(a).setBaseValue(100);tough(a).setBaseValue(100);near(30,armor(a).getAttributeValue(),"Armor cap");near(20,tough(a).getAttributeValue(),"Toughness cap");
        });
        test("Native damageEntity and public attack dispatch use modern armor once with preserved armor-wear callback",()->{
            Actor a=new Actor(w);a.hit(HIT,10);near(96,a.getHealth(),"Ten damage at armor 20");check(a.armorCalls==1&&a.wearCalls==1,"Armor path bypassed or doubled");
            a.setHealth(100);a.hurtResistantTime=0;check(a.attackEntityFrom(HIT,1),"Public attack rejected");near(99.78,a.getHealth(),"One damage at armor 20");check(a.armorCalls==2&&a.wearCalls==2,"Public attack bypassed armor");
        });
        test("Runtime armor and toughness changes affect subsequent native damage rather than storing unused numbers",()->{
            Actor a=new Actor(w);a.hit(HIT,40);float noTough=100-a.getHealth();check(ArmorMath.afterAbsorb(40,20,12)<ArmorMath.afterAbsorb(40,20,0),"Fixture is at the armor floor");a.setHealth(100);tough(a).setBaseValue(12);a.hit(HIT,40);float highTough=100-a.getHealth();check(highTough<noTough,"Toughness had no effect");near(ArmorMath.afterAbsorb(40,20,12),highTough,"Runtime toughness formula");
            armor(a).setBaseValue(5);a.setHealth(100);a.hit(HIT,10);near(ArmorMath.afterAbsorb(10,5,12),100-a.getHealth(),"Runtime armor formula");
        });
        test("Armor-bypassing damage skips reduction and wear and Forge hurt adjustments/cancellation remain upstream",()->{
            Actor a=new Actor(w);a.hit(DamageSource.magic,10);near(90,a.getHealth(),"Unblockable reduced");check(a.wearCalls==0,"Unblockable damaged armor");
            HurtHook h=new HurtHook();h.target=a;MinecraftForge.EVENT_BUS.register(h);try{a.setHealth(100);h.mode=1;a.hit(HIT,10);near(100-ArmorMath.afterAbsorb(5,20,0),a.getHealth(),"Hurt event ordering");h.mode=2;a.setHealth(100);int before=a.armorCalls;a.hit(HIT,10);near(100,a.getHealth(),"Cancelled hit changed health");check(a.armorCalls==before,"Cancelled hit reached armor");}finally{MinecraftForge.EVENT_BUS.unregister(h);}
        });
        test("Native equipment contributes exactly once through transient slot modifiers including diamond toughness and removal",()->{
            Actor a=new Actor(w);armor(a).setBaseValue(3);diamond(a);near(23,a.getTotalArmorValue(),"Equipment double counted");near(3,armor(a).getBaseValue(),"Equipment polluted base armor");near(8,tough(a).getAttributeValue(),"Diamond toughness");
            int size=armor(a).func_111122_c().size();for(int n=0;n<4;n++)a.getTotalArmorValue();check(armor(a).func_111122_c().size()==size,"Equipment modifiers duplicated");
            a.setCurrentItemOrArmor(3,null);near(15,a.getTotalArmorValue(),"Removed chest armor stale");near(6,tough(a).getAttributeValue(),"Removed chest toughness stale");
            a.setCurrentItemOrArmor(0,new ItemStack(Items.diamond_chestplate));near(15,a.getTotalArmorValue(),"Held armor counted as worn");
            armor(a).applyModifier(new AttributeModifier(MODIFIER,"fixture multiplier",0.5,1));near(22,a.getTotalArmorValue(),"Equipment did not participate in native modifier order and floor");
        });
        test("Explicit item armor/toughness modifiers pass through native equipment updates without additive fallback duplication",()->{
            Actor a=new Actor(w);armor(a).setBaseValue(5);ItemStack helmet=new ItemStack(Items.diamond_helmet);NBTTagCompound tag=new NBTTagCompound();NBTTagList mods=new NBTTagList();
            for(int n=0;n<2;n++){NBTTagCompound m=new NBTTagCompound();UUID id=new UUID(18221,n+1);m.setString("AttributeName",n==0?"generic.armor":"generic.armorToughness");m.setString("Name","fixture explicit armor");m.setLong("UUIDMost",id.getMostSignificantBits());m.setLong("UUIDLeast",id.getLeastSignificantBits());m.setDouble("Amount",n==0?7:4);m.setInteger("Operation",0);mods.appendTag(m);}tag.setTag("AttributeModifiers",mods);helmet.setTagCompound(tag);a.setCurrentItemOrArmor(4,helmet);a.onUpdate();near(12,a.getTotalArmorValue(),"Explicit armor counted twice");near(4,tough(a).getAttributeValue(),"Explicit toughness counted twice");
        });
        test("Victim base-stat queries retain native zombie/wither intrinsic armor and exclude equipped armor",()->{
            EntityZombie zombie=new EntityZombie(w);zombie.setCurrentItemOrArmor(3,new ItemStack(Items.diamond_chestplate));ModernAttributes.syncEquipment(zombie);near(2,armor(zombie).getBaseValue(),"Zombie intrinsic base");near(10,armor(zombie).getAttributeValue(),"Zombie equipped view");near(4,armor(new EntityWither(w)).getBaseValue(),"Wither intrinsic base");Player player=new Player(w);player.inventory.armorInventory[2]=new ItemStack(Items.diamond_chestplate);ModernAttributes.syncEquipment(player);near(0,armor(player).getBaseValue(),"Player equipment became copied base stat");near(8,armor(player).getAttributeValue(),"Player equipment view");
        });
        test("Native player ISpecialArmor remains active and native vanilla mob damage is not silently modernized",()->{
            Player p=new Player(w);p.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100);p.setHealth(100);armor(p).setBaseValue(30);tough(p).setBaseValue(20);p.inventory.armorInventory[0]=new ItemStack(special);SpecialArmor item=(SpecialArmor)special;int queries=item.queries;p.hit(10);near(95,p.getHealth(),"Player Forge armor path changed");check(item.queries>queries&&item.wear>0,"ISpecialArmor bypassed");
            Pig pig=new Pig(w);pig.setCurrentItemOrArmor(4,new ItemStack(Items.diamond_helmet));ModernAttributes.syncEquipment(pig);float before=pig.getHealth();pig.hit(10);near(8.8,before-pig.getHealth(),"Vanilla pig native damage changed");near(3,armor(pig).getAttributeValue(),"Pig attribute view");
        });
        test("Native entity NBT retains named bases and saved modifiers while equipment modifiers are rebuilt once",()->{
            Actor a=new Actor(w);armor(a).setBaseValue(7);tough(a).setBaseValue(6);a.setCurrentItemOrArmor(4,new ItemStack(Items.diamond_helmet));armor(a).applyModifier(new AttributeModifier(MODIFIER,"saved fixture armor",2,0));near(12,a.getTotalArmorValue(),"Save fixture");NBTTagCompound tag=saved(a);Actor loaded=new Actor(w);loaded.readFromNBT(tag);near(7,armor(loaded).getBaseValue(),"Saved base lost");near(12,loaded.getTotalArmorValue(),"Equipment persisted twice");near(8,tough(loaded).getAttributeValue(),"Toughness save/reload");check(armor(loaded).getModifier(MODIFIER)!=null,"Saved modifier lost");
        });
        test("Armor registration composes with production potion application hooks on the same native entity class",()->{
            Actor a=new Actor(w);a.addPotionEffect(new SRPPotionEffect(Potion.moveSlowdown,200,0,false,false));a.onUpdate();FMLCommonHandler.instance().onPostWorldTick(w);check(a.isPotionActive(Potion.moveSlowdown)&&a.getDataWatcher().getWatchableObjectInt(7)==0,"Potion native hook chain failed");a.hit(HIT,10);near(96,a.getHealth(),"Armor hook chain failed");
        });
        test(restart?"Armor names, equipment and modifiers survive a genuine second-JVM reload with runtime toughness still effective":"Armor names, equipment and modifiers are saved for a genuine second-JVM reload",()->{
            if(!restart){Actor a=new Actor(w);armor(a).setBaseValue(7);tough(a).setBaseValue(6);a.setCurrentItemOrArmor(4,new ItemStack(Items.diamond_helmet));armor(a).applyModifier(new AttributeModifier(MODIFIER,"saved fixture armor",2,0));a.getTotalArmorValue();try(OutputStream out=Files.newOutputStream(SAVE)){CompressedStreamTools.writeCompressed(saved(a),out);}}
            else{NBTTagCompound tag;try(InputStream in=Files.newInputStream(SAVE)){tag=CompressedStreamTools.readCompressed(in);}Actor a=new Actor(w);a.readFromNBT(tag);near(7,armor(a).getBaseValue(),"Restart base");near(12,a.getTotalArmorValue(),"Restart total");near(8,tough(a).getAttributeValue(),"Restart toughness");a.setHealth(100);a.hit(HIT,40);near(ArmorMath.afterAbsorb(40,12,8),100-a.getHealth(),"Reloaded effective formula");armor(a).removeModifier(armor(a).getModifier(MODIFIER));near(10,a.getTotalArmorValue(),"Saved modifier removal");}
        });
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event) {
        if(done||event.phase!=TickEvent.Phase.END||++ticks!=20)return;
        try{tests(MinecraftServer.getServer().worldServerForDimension(0));}catch(Throwable problem){failed++;report.add("FAIL: setup :: "+problem);problem.printStackTrace();}
        finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get(restart?"armor-restart.txt":"armor-initial.txt"),report,StandardCharsets.UTF_8);}catch(IOException problem){throw new RuntimeException(problem);}MinecraftServer.getServer().initiateShutdown();}
    }
}
