package com.jkvin114.displaydelight.tests;

import com.jkvin114.displaydelight.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.*;import net.minecraft.item.*;import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.enchantment.Enchantment;import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.util.FakePlayerFactory;import net.minecraftforge.common.util.FakePlayer;import com.mojang.authlib.GameProfile;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;import net.minecraftforge.common.MinecraftForge;
import java.util.*;import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;

@Mod(modid="displaydelight_qa",name="Display Delight native checks",version="1",dependencies="required-after:displaydelight")
public final class NativeDisplayChecks {
    public static final List<String> report=new ArrayList<String>();public static int failed;
    int ticks;boolean done;
    interface Case{void run()throws Exception;}
    static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    static void test(String title,Case body){try{body.run();report.add("PASS: "+title);}catch(Throwable t){failed++;report.add("FAIL: "+title+" :: "+t);t.printStackTrace();}}
    @Mod.EventHandler public void init(FMLInitializationEvent e)throws Exception{
        if(e.getSide().isClient())Class.forName("com.jkvin114.displaydelight.tests.NativeDisplayClientChecks").getMethod("initialize").invoke(null);
        else FMLCommonHandler.instance().bus().register(this);
    }
    static FakePlayer player(WorldServer w){FakePlayer p=FakePlayerFactory.get(w,new GameProfile(UUID.fromString("676536bd-d552-497d-90c7-7150331cd170"),"FoodChecks"));p.capabilities.isCreativeMode=false;p.capabilities.allowEdit=true;p.inventory.clearInventory(null,-1);p.setSneaking(false);p.setPosition(1.5,80,1.5);return p;}
    static ItemStack hand(EntityPlayerMP p,Item item,int count){ItemStack s=new ItemStack(item,count);p.inventory.setInventorySlotContents(p.inventory.currentItem,s);return s;}
    static int amount(EntityPlayerMP p,Item item){int n=0;for(ItemStack s:p.inventory.mainInventory)if(s!=null&&s.getItem()==item)n+=s.stackSize;return n;}
    static void tile(WorldServer w,DisplayBlock block,int count,boolean hidden){check(DisplayBlock.place(w,4,80,4,block,0,count,hidden),"Placement failed");}
    void cases(WorldServer w){
        final DisplayBlock plate=Catalog.plate(false),cookie=Catalog.blocks.get("plated_cookie"),small=Catalog.plate(true);
        test("All 382 source displays register without optional food mods and vanilla links resolve",()->{
            check(Catalog.blocks.size()==382,"Registry count");check(cookie.food.getItem()==Items.cookie,"Cookie link");check(Catalog.blocks.get("mushroom_stew").food.getItem()==Items.mushroom_stew,"Stew link");
            check(Catalog.blocks.get("beef_stew").food==null,"Missing food mod falsely present");
            for(DisplayBlock b:Catalog.blocks.values())check(Item.getItemFromBlock(b)!=null,"Missing block item "+b.definition.id);
        });
        test("Real food goes onto plates one at a time and sneak insertion respects capacity and inventory",()->{
            FakePlayer p=player(w);tile(w,plate,1,false);hand(p,Items.cookie,10);check(Interactions.use(w,4,80,4,p),"Initial insert");check(w.getBlock(4,80,4)==cookie&&cookie.count(w,4,80,4)==1&&p.getHeldItem().stackSize==9,"Single insertion state");
            p.setSneaking(true);check(Interactions.use(w,4,80,4,p),"Batch insert");check(cookie.count(w,4,80,4)==4&&p.getHeldItem().stackSize==6,"Capacity or consumption");check(!Interactions.use(w,4,80,4,p),"Full plate accepted more");
        });
        test("Taking one or all returns the native edible food and leaves the correct empty plate",()->{
            FakePlayer p=player(w);tile(w,cookie,4,false);check(Interactions.use(w,4,80,4,p),"Take one");check(cookie.count(w,4,80,4)==3&&amount(p,Items.cookie)==1,"Single retrieval");
            p.inventory.setInventorySlotContents(p.inventory.currentItem,null);p.setSneaking(true);check(Interactions.use(w,4,80,4,p),"Take all");check(amount(p,Items.cookie)==3&&w.getBlock(4,80,4)==plate,"Batch retrieval");check(Items.cookie instanceof ItemFood,"Returned cookie isn't edible");
        });
        test("Hiding and replacing plates preserves food count and consumes or returns one plate",()->{
            FakePlayer p=player(w);tile(w,cookie,3,false);hand(p,Items.iron_axe,1);check(Interactions.use(w,4,80,4,p),"Hide");check(cookie.hidden(w,4,80,4)&&cookie.count(w,4,80,4)==3&&amount(p,Item.getItemFromBlock(plate))==1,"Hide state/drop");
            p.inventory.clearInventory(null,-1);hand(p,Item.getItemFromBlock(plate),2);check(Interactions.use(w,4,80,4,p),"Restore");check(!cookie.hidden(w,4,80,4)&&p.getHeldItem().stackSize==1,"Plate replacement consumed wrong quantity");
            tile(w,cookie,1,true);p.inventory.clearInventory(null,-1);check(Interactions.use(w,4,80,4,p)&&w.isAirBlock(4,80,4),"Hidden last food left a free plate");
        });
        test("Small plates accept configured subtype food mappings and preserve native item damage",()->{
            DisplayBlock b=Catalog.blocks.get("small_plated_cake_slice");String original=Settings.config.get("foodMappings",b.definition.id,b.definition.food).getString();
            try {Settings.config.get("foodMappings",b.definition.id,b.definition.food).set("minecraft:dye@3");Catalog.bindFoods();FakePlayer p=player(w);tile(w,small,1,false);ItemStack s=hand(p,Items.dye,2);s.setItemDamage(3);check(Interactions.use(w,4,80,4,p)&&w.getBlock(4,80,4)==b&&s.stackSize==1,"Subtype insertion");p.inventory.clearInventory(null,-1);check(Interactions.use(w,4,80,4,p)&&p.getHeldItem().getItemDamage()==3,"Subtype retrieval");}
            finally{Settings.config.get("foodMappings",b.definition.id,b.definition.food).set(original);Catalog.bindFoods();}
        });
        test("Uninstalled foods remain decorative and cannot be converted into invented edible items",()->{
            FakePlayer p=player(w);DisplayBlock b=Catalog.blocks.get("plated_hamburger");tile(w,b,1,false);check(!Interactions.use(w,4,80,4,p),"Invented missing food");ArrayList<ItemStack> drops=b.drops(w,4,80,4,false);check(drops.size()==1&&drops.get(0).getItem()==Item.getItemFromBlock(b),"Missing-mod fallback");
        });
        test("Sneak use places native mushroom stew through the real Forge interaction event",()->{
            FakePlayer p=player(w);p.setSneaking(true);hand(p,Items.mushroom_stew,1);w.setBlock(6,79,6,Blocks.stone);w.setBlockToAir(6,80,6);
            PlayerInteractEvent e=new PlayerInteractEvent(p,PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,6,79,6,1,w);MinecraftForge.EVENT_BUS.post(e);
            check(e.isCanceled()&&w.getBlock(6,80,6)==Catalog.blocks.get("mushroom_stew")&&p.getHeldItem()==null,"Direct placement event");
        });
        test("Native block harvesting returns exact edible counts and plate; Silk Touch keeps a full display",()->{
            FakePlayer p=player(w);tile(w,cookie,3,false);ArrayList<ItemStack> drops=cookie.drops(w,4,80,4,false);check(drops.size()==2&&drops.get(0).getItem()==Items.cookie&&drops.get(0).stackSize==3,"Normal drops");
            check(cookie.drops(w,4,80,4,true).get(0).getItem()==Items.cookie,"Partial silk minted a full display");tile(w,cookie,4,false);check(cookie.drops(w,4,80,4,true).get(0).getItem()==Item.getItemFromBlock(cookie),"Full silk drop");
            p.theItemInWorldManager.setGameType(WorldSettings.GameType.SURVIVAL);check(p.theItemInWorldManager.tryHarvestBlock(4,80,4)&&w.isAirBlock(4,80,4),"Native harvest failed");
        });
        test("Count and hidden state serialize, clamp invalid values, and never tick",()->{
            tile(w,cookie,3,true);DisplayTile t=DisplayTile.at(w,4,80,4);NBTTagCompound n=new NBTTagCompound();t.writeToNBT(n);DisplayTile restored=new DisplayTile();restored.readFromNBT(n);check(restored.count==3&&restored.hidden&&!restored.canUpdate(),"State serialization");n.setInteger("FoodCount",200);restored.readFromNBT(n);check(restored.count==10,"NBT count unbounded");
            t.set(200,true);check(t.count==4,"Block-specific limit");
        });
        test("Support tables react to native support blocks and preserve source exceptions",()->{
            w.setBlockToAir(4,79,4);check(cookie.support(w,4,80,4),"Floating display has no table");w.setBlock(4,79,4,Blocks.stone);check(!cookie.support(w,4,80,4),"Solid top has table");w.setBlock(4,79,4,Blocks.oak_stairs);check(!cookie.support(w,4,80,4),"Source stair exception");
        });
        test("Adventure permissions reject food interactions without changing stacks or the plate",()->{
            FakePlayer p=player(w);tile(w,plate,1,false);hand(p,Items.cookie,5);p.capabilities.allowEdit=false;
            check(!Interactions.use(w,4,80,4,p)&&w.getBlock(4,80,4)==plate&&p.getHeldItem().stackSize==5,"Disallowed interaction changed state");p.capabilities.allowEdit=true;
        });
        test("Farmer villager offers replace absent wandering traders and prioritize missing-addon displays",()->{
            MerchantRecipeList recipes=new MerchantRecipeList();new DisplayTrades().manipulateTradesForVillager(new net.minecraft.entity.passive.EntityVillager(w),recipes,new Random(1));check(recipes.size()==3,"Trade count");
            for(Object raw:recipes){net.minecraft.village.MerchantRecipe recipe=(net.minecraft.village.MerchantRecipe)raw;check(recipe.getItemToBuy().getItem()==Items.emerald,"Trade currency");}
        });
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent e){if(done||e.phase!=TickEvent.Phase.END||++ticks!=20)return;done=true;WorldServer w=MinecraftServer.getServer().worldServerForDimension(0);
        if("restart".equals(System.getProperty("display.qa.phase"))){test("Actual dedicated-server restart preserves count, hidden plate and facing",()->{DisplayBlock b=Catalog.blocks.get("plated_cookie");check(w.getBlock(12,80,12)==b&&b.count(w,12,80,12)==3&&b.hidden(w,12,80,12)&&w.getBlockMetadata(12,80,12)==2,"Saved display changed");});}
        else{cases(w);DisplayBlock.place(w,12,80,12,Catalog.blocks.get("plated_cookie"),2,3,true);}
        report.add("Failed: "+failed);try{Files.write(Paths.get("checks-"+System.getProperty("display.qa.phase","initial")+".txt"),report,StandardCharsets.UTF_8);}catch(IOException ex){throw new RuntimeException(ex);}MinecraftServer.getServer().initiateShutdown();
    }
}
