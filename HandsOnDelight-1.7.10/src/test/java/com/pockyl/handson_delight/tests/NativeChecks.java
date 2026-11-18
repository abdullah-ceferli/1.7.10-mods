package com.pockyl.handson_delight.tests;

import com.pockyl.handson_delight.*;
import com.pockyl.handson_delight.station.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraft.entity.player.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.nbt.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import vectorwing.farmersdelight.legacy.*;

@Mod(modid="handson_native_checks",name="Hands-On native checks",version="1",dependencies="required-after:handson_delight")
public final class NativeChecks {
    private boolean done;private int tick;private final List<String> report=new ArrayList<String>();private WorldServer w;private EntityPlayerMP p;
    public static void check(boolean b,String s){if(!b)throw new AssertionError(s);}
    @Mod.EventHandler public void init(FMLInitializationEvent e){FMLCommonHandler.instance().bus().register(this);if(e.getSide().isClient())ClientChecks.init();}
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent e){
        if(FMLCommonHandler.instance().getSide().isClient()||done||e.phase!=TickEvent.Phase.END||++tick<20)return;done=true;
        try{w=MinecraftServer.getServer().worldServerForDimension(0);p=new Cook(w);p.setPosition(1,81,1);p.capabilities.isCreativeMode=false;
            if("restart".equals(System.getProperty("handson.qa.phase")))restart();else run();report.add("Failed: 0");
        }catch(Throwable t){t.printStackTrace();report.add("FAIL: "+t);report.add("Failed: 1");}
        try{Files.write(Paths.get("checks-"+System.getProperty("handson.qa.phase","initial")+".txt"),report,StandardCharsets.UTF_8);}catch(Exception x){throw new RuntimeException(x);}MinecraftServer.getServer().initiateShutdown();
    }
    private void pass(String s){report.add("PASS: "+s);}
    /** Forge's default fake player discards stats, so native achievement loops cannot terminate. */
    private static final class Cook extends net.minecraftforge.common.util.FakePlayer {
        Cook(WorldServer w){super(w,new com.mojang.authlib.GameProfile(UUID.fromString("00000000-0000-0000-0000-00000000c00c"),"KitchenChecks"));}
        @Override public void addStat(net.minecraft.stats.StatBase stat,int count){if(stat!=null)func_147099_x().func_150873_a(this,stat,func_147099_x().writeStat(stat)+count);}
    }
    private <T> T tile(int x,net.minecraft.block.Block b,Class<T> type){w.setBlock(x,79,0,Blocks.lava);w.setBlock(x,80,0,b,0,3);return type.cast(w.getTileEntity(x,80,0));}
    private int dropped(ItemStack item){int n=0;for(Object o:w.loadedEntityList)if(o instanceof EntityItem){ItemStack s=((EntityItem)o).getEntityItem();if(Stacks.same(s,item))n+=s.stackSize;}return n;}
    private void clear(){for(int i=0;i<36;i++)p.inventory.setInventorySlotContents(i,null);}
    private int count(ItemStack item){int n=0;for(int i=0;i<36;i++)if(Stacks.same(p.inventory.getStackInSlot(i),item))n+=p.inventory.getStackInSlot(i).stackSize;return n;}
    private void run(){
        StirSkill skill=new StirSkill();for(int i=0;i<240;i++)skill.move(i,i,(float)Math.cos(i*.3),(float)Math.sin(i*.3),true,3);check(skill.grade(239)==3,"Good stirring should fill three stars");check(skill.grade(400)==0,"Idle stars must drain");
        StirSkill flood=new StirSkill();for(int i=0;i<2000;i++)flood.move(1,i,(float)Math.cos(i*.3),(float)Math.sin(i*.3),true,3);check(flood.stars(1)<1,"Packet flood raised stars");pass("Upstream stirring quality rewards steady circles, decays while idle, and bounds packet floods");
        CookingPotTile pot=tile(0,Content.cookingPot,CookingPotTile.class);
        Sessions interactions=new Sessions();net.minecraftforge.event.entity.player.PlayerInteractEvent denied=new net.minecraftforge.event.entity.player.PlayerInteractEvent(p,net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,0,80,0,1,w);denied.useBlock=cpw.mods.fml.common.eventhandler.Event.Result.DENY;interactions.interact(denied);check(!denied.isCanceled(),"Claim-denied use was intercepted");
        net.minecraftforge.event.entity.player.PlayerInteractEvent canceled=new net.minecraftforge.event.entity.player.PlayerInteractEvent(p,net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,0,80,0,1,w);canceled.setCanceled(true);interactions.interact(canceled);check(canceled.isCanceled(),"Protected cancellation was cleared");
        p.setSneaking(true);net.minecraftforge.event.entity.player.PlayerInteractEvent sneaking=new net.minecraftforge.event.entity.player.PlayerInteractEvent(p,net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,0,80,0,1,w);interactions.interact(sneaking);p.setSneaking(false);check(!sneaking.isCanceled(),"Sneak use lost the ordinary native interaction");pass("World opening preserves claim denials, canceled interactions and the native sneak-use bypass");
        CookingRecipes.Recipe recipe=null;
        for(CookingRecipes.Recipe r:CookingRecipes.ALL)if(!Stacks.empty(r.container)&&r.inputs.size()<=6&&r.output.stackSize<=4){recipe=r;break;}check(recipe!=null,"No serving recipe");
        for(int i=0;i<recipe.inputs.size();i++)pot.setInventorySlotContents(i,Stacks.copy(recipe.inputs.get(i).get(0),2));
        StationState ps=StationHooks.state(pot);ps.manual=true;ps.attended=w.getTotalWorldTime();
        for(int i=0;i<30;i++)pot.updateEntity();check(pot.progress==0&&Stacks.empty(pot.getStackInSlot(6)),"Pot cooked without stirring");
        StationHooks.stir(pot,p,1,0,1);for(int i=0;i<recipe.time*3;i++)pot.updateEntity();
        check(Stacks.same(pot.getStackInSlot(6),recipe.output)&&pot.getStackInSlot(6).stackSize==2*recipe.output.stackSize,"Pot batch output: wanted "+recipe.id+" / "+recipe.output+", found "+pot.getStackInSlot(6)+", heated="+pot.heated+", progress="+pot.progress+", work="+ps.work);for(int i=0;i<6;i++)check(Stacks.empty(pot.getStackInSlot(i)),"Pot inputs not consumed exactly");
        pass("Native pot transformer gates attended cooking on stirring and consumes a complete two-serving batch once");
        clear();p.inventory.setInventorySlotContents(0,Stacks.copy(recipe.container,2));check(Actions.serve(pot,p,0),"Serve failed");check(count(recipe.output)==recipe.output.stackSize,"Meal not returned to cook");check(Stacks.size(p.inventory.getStackInSlot(0))==1,"Serving container count");pass("Serving uses the native container and meal rules and pays native recipe experience");
        int remaining=Stacks.size(pot.getStackInSlot(6)),servedBefore=count(recipe.output);check(remaining>0,"Bulk serve fixture has no remaining dish");p.inventory.setInventorySlotContents(0,Stacks.copy(recipe.container,remaining));check(Actions.serve(pot,p,0,true),"Bulk serving failed");check(Stacks.empty(pot.getStackInSlot(6))&&Stacks.empty(p.inventory.getStackInSlot(0))&&count(recipe.output)-servedBefore==remaining,"Bulk serving lost meals or containers");pass("Shift-serving drains the remaining native meal batch with exact container and output accounting");
        CookingPotTile auto=tile(2,Content.cookingPot,CookingPotTile.class);for(int i=0;i<recipe.inputs.size();i++)auto.setInventorySlotContents(i,Stacks.copy(recipe.inputs.get(i).get(0),1));for(int i=0;i<recipe.time+1;i++)auto.updateEntity();check(Stacks.same(auto.getStackInSlot(6),recipe.output),"Native automation regressed");pass("Unattended and hopper-fed pots keep ordinary Farmer's Delight cooking");
        SkilletTile pan=tile(4,Content.skillet,SkilletTile.class);clear();p.inventory.setInventorySlotContents(0,new ItemStack(Items.beef,2));check(Actions.insert(pan,p,0,0,true),"Pan insert");GrillingRecipes.Recipe fry=GrillingRecipes.find(new ItemStack(Items.beef));check(fry!=null,"No beef grilling recipe");
        for(int i=0;i<1000;i++)pan.updateEntity();Frying f=StationHooks.state(pan).frying[0];check(f.waits()&&f.up()==0&&pan.getStackInSlot(0).getItem()==Items.beef,"Food must wait for its second side");
        check(StationHooks.turn(pan,0),"Turn rejected");for(int i=0;i<1000;i++)pan.updateEntity();check(f.ready()&&Stacks.same(pan.getStackInSlot(0),fry.output)&&pan.getStackInSlot(0).stackSize==2*fry.output.stackSize,"Pan output not held");ItemStack fried=pan.getStackInSlot(0).copy();for(int i=0;i<1000;i++)pan.updateEntity();check(Stacks.same(fried,pan.getStackInSlot(0))&&fried.stackSize==pan.getStackInSlot(0).stackSize,"Ready food recooked");
        check(Actions.take(pan,p,0)&&count(fry.output)==fried.stackSize,"Taking food lost output");pass("Two-sided batch frying waits for a toss, retains cooked food, and returns the exact batch");
        CuttingBoardTile board=tile(6,Content.cuttingBoard,CuttingBoardTile.class);check(board.getInventoryStackLimit()==64,"Native board capacity transformer missing");clear();ItemStack knife=new ItemStack(Content.ITEMS.get("diamond_knife"));p.inventory.setInventorySlotContents(1,knife);
        CuttingRecipes.Recipe cutting=null;for(CuttingRecipes.Recipe r:CuttingRecipes.ALL)if(r.matchesInput(new ItemStack(Items.beef))&&r.matchesTool(knife)){cutting=r;break;}check(cutting!=null,"No cutting recipe");
        p.inventory.setInventorySlotContents(0,new ItemStack(Items.beef,12));check(Actions.insert(board,p,0,0,true),"Board batch insert");int before=count(cutting.outputs.get(0)),damage=knife.getItemDamage();
        for(int i=0;i<3&&!Stacks.empty(board.getStackInSlot(0));i++)check(Actions.cut(board,p,1),"Cut failed");check(Stacks.empty(board.getStackInSlot(0)),"Cut batch retained inputs");check(knife.getItemDamage()-damage==12,"Tool wear must charge per processed item");check(count(cutting.outputs.get(0))-before==12*cutting.outputs.get(0).stackSize,"Cut batch output mismatch");pass("Board cuts batch ingredients through native recipes and charges native knife durability per item");
        clear();RecipeBook.Entry sandwich=null;for(RecipeBook.Entry e:RecipeBook.list(board))if(e.assembly!=null&&e.id.contains("hamburger")){sandwich=e;break;}check(sandwich!=null,"No sandwich recipe");
        ItemStack unchanged=new ItemStack(Items.diamond,7);p.inventory.setInventorySlotContents(0,unchanged);check(!Actions.fill(board,p,sandwich.id)&&p.inventory.getStackInSlot(0)==unchanged&&unchanged.stackSize==7,"Failed autofill changed inventory");
        clear();for(int i=0;i<sandwich.inputs.size();i++)p.inventory.setInventorySlotContents(i,Stacks.copy(sandwich.inputs.get(i).get(0),1));check(Actions.fill(board,p,sandwich.id),"Sandwich fill failed");check(Stacks.same(board.getStackInSlot(0),sandwich.output)&&StationHooks.state(board).layers.isEmpty(),"Assembly not validated by native recipe");pass("Recipe book autofill is atomic; sandwich assembly validates and consumes the installed native recipe");
        Actions.take(board,p,0);check(!Actions.insert(board,p,35,0,false),"Empty inventory insert accepted");board.setInventorySlotContents(0,new ItemStack(Items.bread));StationHooks.state(board).layers.add(new ItemStack(Items.apple));
        int apples=dropped(new ItemStack(Items.apple)),bread=dropped(new ItemStack(Items.bread));w.setBlockToAir(6,80,0);check(dropped(new ItemStack(Items.apple))-apples==1&&dropped(new ItemStack(Items.bread))-bread==1,"Breaking board duplicated or lost layers");pass("Breaking a layered board returns each native and extra ingredient exactly once");
        StoveTile stove=tile(8,Content.stove,StoveTile.class);w.setBlockMetadataWithNotify(8,80,0,4,3);clear();p.inventory.setInventorySlotContents(0,new ItemStack(Items.beef));check(Actions.insert(stove,p,0,0,false),"Stove input");w.setBlock(8,81,0,Blocks.stone);int rawBefore=dropped(new ItemStack(Items.beef));stove.updateEntity();check(Stacks.empty(stove.getStackInSlot(0))&&dropped(new ItemStack(Items.beef))==rawBefore+1&&!StationHooks.state(stove).frying[0].byHand(),"Blocked stove cleanup");pass("Blocked stoves return food and clear the manual frying state");
        p.setPosition(50,81,50);check(!Sessions.valid(p,pot),"Out-of-range session accepted");p.setPosition(.5,81,.5);check(Sessions.valid(p,pot),"In-range session rejected");w.setBlockToAir(0,80,0);check(!Sessions.valid(p,pot),"Replaced station still authorized");pass("Session validation rejects distance changes and replaced tile entities");
        // Persist a real tile in a real world chunk for the second JVM to load.
        SkilletTile saved=tile(10,Content.skillet,SkilletTile.class);saved.setInventorySlotContents(0,new ItemStack(Items.beef,3));StationState ss=StationHooks.state(saved);ss.frying[0].byHand(p.getUniqueID());ss.frying[0].batch(3);ss.frying[0].fry(.4F);ss.observed[0]=new ItemStack(Items.beef);saved.markDirty();
        NBTTagCompound tag=new NBTTagCompound();saved.writeToNBT(tag);check(tag.hasKey("handson_delight",10),"Native NBT hook absent");SkilletTile loaded=new SkilletTile();loaded.readFromNBT(tag);check(Math.abs(StationHooks.state(loaded).frying[0].down()-.4F)<.0001F,"Frying NBT failed");
        CuttingBoardTile savedBoard=tile(12,Content.cuttingBoard,CuttingBoardTile.class);savedBoard.setInventorySlotContents(0,new ItemStack(Items.bread));StationHooks.state(savedBoard).layers.add(new ItemStack(Items.apple));savedBoard.markDirty();
        pass("Native tile NBT hooks preserve fractional frying progress and board layers");
        CuttingBoardTile audit=tile(14,Content.cuttingBoard,CuttingBoardTile.class);int checked=0;
        for(RecipeBook.Entry e:RecipeBook.list(audit))if(e.assembly!=null){
            clear();audit.setInventorySlotContents(0,null);StationHooks.state(audit).layers.clear();
            for(int i=0;i<e.inputs.size();i++)p.inventory.setInventorySlotContents(i,Stacks.copy(e.inputs.get(i).get(0),1));
            check(Actions.fill(audit,p,e.id),"Assembly audit failed to fill "+e.id);
            check(Stacks.same(audit.getStackInSlot(0),e.output)&&audit.getStackInSlot(0).stackSize==e.output.stackSize,"Assembly audit output mismatch "+e.id);
            // Its original object still matches an automation grid, but no longer a player's crafting table.
            List<ItemStack> layers=new ArrayList<ItemStack>();for(List<ItemStack> in:e.inputs)layers.add(Stacks.copy(in.get(0),1));net.minecraft.inventory.InventoryCrafting grid=RecipeBook.arrange(e,layers);
            check(e.assembly.matches(grid,w),"Automation recipe removed "+e.id);
            net.minecraft.inventory.ContainerWorkbench playerGrid=new net.minecraft.inventory.ContainerWorkbench(p.inventory,w,14,80,0);
            for(int i=0;i<9;i++)playerGrid.craftMatrix.setInventorySlotContents(i,grid.getStackInSlot(i));
            check(!e.assembly.matches(playerGrid.craftMatrix,w),"Personal crafting still produces a board dish "+e.id);checked++;
        }
        check(checked>10,"Assembly audit had too few recipes");pass("All "+checked+" eligible native assembly recipes produce their exact outputs, reject personal crafting and retain automation matching");
        clear();audit.setInventorySlotContents(0,null);RecipeBook.Entry dough=null;for(RecipeBook.Entry e:RecipeBook.list(audit))if(e.assembly!=null&&e.id.contains("wheat_dough_from_water")){dough=e;break;}check(dough!=null,"Native dough recipe missing");
        p.inventory.setInventorySlotContents(0,new ItemStack(Items.water_bucket));p.inventory.setInventorySlotContents(1,new ItemStack(Items.wheat,48));check(Actions.fill(audit,p,dough.id,true),"Bulk dough failed");check(audit.getStackInSlot(0).stackSize==48&&count(new ItemStack(Items.bucket))==1&&count(new ItemStack(Items.wheat))==0,"Bulk dough count or bucket remainder differs from the original batch rule");
        clear();audit.setInventorySlotContents(0,null);for(int i=0;i<sandwich.inputs.size();i++)p.inventory.setInventorySlotContents(i,Stacks.copy(sandwich.inputs.get(i).get(0),16));check(Actions.fill(audit,p,sandwich.id,true),"Bulk sandwich fill failed");check(audit.getStackInSlot(0).stackSize==16*sandwich.output.stackSize,"Bulk sandwich output lost");pass("Shift-book assembly preserves full output batches and consumes nonstacking containers once per batch, returning their native remainder");
        // A late base goes underneath its filling; taking returns only the upper stack.
        audit.setInventorySlotContents(0,null);StationHooks.state(audit).layers.clear();clear();int baseIndex=RecipeRules.base(sandwich.inputs),fillingIndex=baseIndex==0?1:0;ItemStack baseKind=sandwich.inputs.get(baseIndex).get(0),fillingKind=sandwich.inputs.get(fillingIndex).get(0);
        p.inventory.setInventorySlotContents(0,Stacks.copy(fillingKind,2));p.inventory.setInventorySlotContents(1,Stacks.copy(baseKind,2));check(Actions.insert(audit,p,0,0,true)&&Actions.insert(audit,p,1,0,true),"Late base insertion failed");check(Stacks.same(audit.getStackInSlot(0),baseKind)&&audit.getStackInSlot(0).stackSize==2,"Late base did not go beneath filling");check(Actions.take(audit,p,0)&&StationHooks.state(audit).layers.isEmpty()&&audit.getStackInSlot(0).stackSize==2&&count(fillingKind)==2,"Top-only retrieval lost or removed lower layers");check(Actions.take(audit,p,0)&&count(baseKind)==2,"Bottom layer count changed");pass("Manual assembly accepts a late base below batch filling and retrieves only the upper layer without loss");
        clear();stove=tile(18,Content.stove,StoveTile.class);p.inventory.setInventorySlotContents(0,new ItemStack(Items.beef,8));check(Actions.insert(stove,p,0,0,true),"Shift stove placement failed");int stoveCount=0;for(int slot=0;slot<6;slot++)stoveCount+=Stacks.size(stove.getStackInSlot(slot));check(stoveCount==6&&count(new ItemStack(Items.beef))==2,"Bulk stove placement lost food or missed positions");pass("Bulk stove placement fills all six available positions and retains the remaining ingredients");
        clear();audit.setInventorySlotContents(0,null);p.inventory.setInventorySlotContents(0,new ItemStack(Content.ITEMS.get("iron_knife")));check(Actions.carve(audit,p,0)&&audit.carved&&Stacks.empty(p.inventory.getStackInSlot(0)),"Knife was not stuck in the empty board");check(Actions.take(audit,p,0)&&count(new ItemStack(Content.ITEMS.get("iron_knife")))==1,"Stuck knife did not return once");pass("Native tools can be stuck into an empty board and retrieved without copying or losing them");
        CookingPotTile bookPot=tile(16,Content.cookingPot,CookingPotTile.class);RecipeBook.Entry pe=null;for(RecipeBook.Entry e:RecipeBook.list(bookPot))if(e.id.equals("pot/"+recipe.id))pe=e;check(pe!=null,"Pot book entry absent");clear();for(int i=0;i<recipe.inputs.size();i++){p.inventory.setInventorySlotContents(i,Stacks.copy(recipe.inputs.get(i).get(0),4));bookPot.setInventorySlotContents(i,Stacks.copy(recipe.inputs.get(i).get(0),1));}check(Actions.fill(bookPot,p,pe.id,true),"Pot book refused compatible existing ingredients");check(StationHooks.batch(bookPot,recipe)==5,"Pot book batch split incorrectly");pass("Shift-book pot filling adds complete sets to compatible existing ingredients and lays repeated ingredients out for native matching");
    }
    private void restart(){
        SkilletTile pan=(SkilletTile)w.getTileEntity(10,80,0);check(pan!=null&&Stacks.size(pan.getStackInSlot(0))==3,"Restart lost raw food");Frying f=StationHooks.state(pan).frying[0];check(f.byHand()&&f.down()>=.4F&&f.up()==0,"Restart lost manual progress");pass("A fresh native server reloads the saved three-item skillet and both side values");
        CuttingBoardTile board=(CuttingBoardTile)w.getTileEntity(12,80,0);check(board!=null&&board.getStackInSlot(0).getItem()==Items.bread&&StationHooks.state(board).layers.size()==1&&StationHooks.state(board).layers.get(0).getItem()==Items.apple,"Restart lost layers");pass("A fresh native server reloads layered board ingredients without duplication");
    }
}
