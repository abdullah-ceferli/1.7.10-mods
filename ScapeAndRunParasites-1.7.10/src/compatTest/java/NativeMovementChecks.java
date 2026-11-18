package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.common.registry.*;
import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.entity.*;import net.minecraft.entity.item.*;import net.minecraft.entity.projectile.*;import net.minecraft.entity.player.*;import net.minecraft.block.*;import net.minecraft.block.material.Material;import net.minecraft.init.*;import net.minecraft.item.ItemStack;import net.minecraft.nbt.*;import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.FakePlayer;import com.mojang.authlib.GameProfile;import io.netty.buffer.*;
import com.dhanantry.scapeandrunparasites.backport.entity.LegacyEntityMob;import com.dhanantry.scapeandrunparasites.backport.entity.movement.*;
@Mod(modid="srp_movement_checks",name="Movement compatibility checks",version="1")
public final class NativeMovementChecks {
    final List<String> report=new ArrayList<String>();int ticks,failed;boolean done,restart;static Contact contact;
    @Mod.EventHandler public void preInit(FMLPreInitializationEvent event){
        GravityNetwork.initialize();GravityNetwork.initialize();EntityRegistry.registerModEntity(Actor.class,"motion_actor",0,this,64,1,true);
        contact=new Contact();GameRegistry.registerBlock(contact,"motion_contact");restart=Files.exists(Paths.get("movement-fixture.dat"));
    }
    @Mod.EventHandler public void init(FMLInitializationEvent e)throws Exception{if(e.getSide().isClient())Class.forName("dev.srpbackport.tests.NativeMovementClientChecks").getMethod("initialize").invoke(null);else FMLCommonHandler.instance().bus().register(this);}
    public static class Actor extends LegacyEntityMob {
        public int moves,contacts,fluid;public MoverType lastType;public boolean reenter,reentered,fail,readSawGravity,writeSawGravity;
        public Actor(World world){super(world);}
        @Override public void moveEntity(MoverType type,double x,double y,double z){moves++;lastType=type;if(fail)throw new IllegalStateException("movement fixture");super.moveEntity(type,x,y,z);MovementAccess.blocks(this);}
        @Override public boolean isInWater(){return fluid==1;}
        @Override public boolean handleLavaMovement(){return fluid==2;}
        @Override public boolean isOnLadder(){return false;}
        @Override public void writeEntityToNBT(NBTTagCompound tag){writeSawGravity=tag.getBoolean("NoGravity");super.writeEntityToNBT(tag);}
        @Override public void readEntityFromNBT(NBTTagCompound tag){readSawGravity=GravityAccess.get(this);super.readEntityFromNBT(tag);}
    }
    static class Contact extends Block {
        Contact(){super(Material.plants);setBlockName("motion_contact");setBlockTextureName("stone");}
        @Override public AxisAlignedBB getCollisionBoundingBoxFromPool(World w,int x,int y,int z){return null;}
        @Override public boolean isOpaqueCube(){return false;}@Override public boolean renderAsNormalBlock(){return false;}
        @Override public void onEntityCollidedWithBlock(World w,int x,int y,int z,Entity entity){if(entity instanceof Actor){Actor a=(Actor)entity;a.contacts++;if(a.reenter&&!a.reentered){a.reentered=true;a.moveEntity(0.125,0,0);}}}
    }
    interface Check{void run()throws Exception;}static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    static void equal(double a,double b,String why){if(Double.doubleToLongBits(a)!=Double.doubleToLongBits(b))throw new AssertionError(why+": "+a+" != "+b);}
    void test(String name,Check body){try{body.run();report.add("PASS: "+name);}catch(Throwable e){failed++;report.add("FAIL: "+name+" :: "+e);e.printStackTrace();}}
    static void clean(EntityMotionState s){check(s.srp$getMoveDepth()==0&&!s.srp$getBypassMove()&&s.srp$getMoverType()==null,"Leaked movement scope");}
    void initial(WorldServer w){
        test("Native movement dispatch reaches the converted override once and preserves its native super call",()->{
            Actor a=new Actor(w);a.setPosition(0,80,0);a.noClip=true;a.moveEntity(0.25,0.5,-0.75);check(a.moves==1&&a.lastType==MoverType.SELF,"Callback count/type");equal(a.posX,0.25,"X");equal(a.posY,80.5,"Y");equal(a.posZ,-0.75,"Z");clean((EntityMotionState)a);
            MovementAccess.move(a,MoverType.SHULKER_BOX,0.25,0,0);check(a.moves==2&&a.lastType==MoverType.SHULKER_BOX,"Explicit type");clean((EntityMotionState)a);
        });
        test("Reentrant block-contact movement is dispatched, and no-clip mobs retain explicit contact callbacks",()->{
            w.setBlock(0,80,0,contact,0,3);Actor a=new Actor(w);a.setPosition(0.5,80,0.5);a.reenter=true;a.moveEntity(0.25,0,0);check(a.moves==2&&a.reentered&&a.contacts>=2,"Reentrant callbacks");equal(a.posX,0.875,"Nested displacement");clean((EntityMotionState)a);
            Actor ghost=new Actor(w);ghost.noClip=true;ghost.setPosition(0.5,80,0.5);ghost.moveEntity(0.01,0,0);check(ghost.moves==1&&ghost.contacts==1,"No-clip explicit block callback");w.setBlockToAir(0,80,0);
        });
        test("Movement exceptions restore dispatch state instead of disabling future callbacks",()->{
            Actor a=new Actor(w);a.fail=true;boolean threw=false;try{a.moveEntity(0.1,0,0);}catch(IllegalStateException expected){threw=true;}check(threw,"Missing fixture exception");clean((EntityMotionState)a);a.fail=false;a.noClip=true;a.setPosition(0,80,0);a.moveEntity(0.1,0,0);check(a.moves==2,"Future callback lost");clean((EntityMotionState)a);
        });
        test("Living gravity toggles actual land, water and lava acceleration while retaining native drag",()->{
            Random random=new Random(1710);for(int fluid=0;fluid<3;fluid++)for(boolean noGravity:new boolean[]{false,true})for(int i=0;i<100;i++){
                Actor a=new Actor(w);a.fluid=fluid;a.noClip=true;a.setPosition(2,80,2);double velocity=random.nextDouble()*2-1;a.motionY=velocity;GravityAccess.set(a,noGravity);a.moveEntityWithHeading(0,0);
                double expected=fluid==0?(noGravity?velocity:velocity-0.08)*(double)0.98F:fluid==1?velocity*(double)0.8F-(noGravity?0:0.02):velocity*0.5-(noGravity?0:0.02);
                equal(a.motionY,expected,"Physics fluid="+fluid+" flag="+noGravity);check(a.moves==1,"Travel callback duplicated");
            }
        });
        test("Native item, XP, TNT, falling block, arrow and throwable gravity sites load and honor the flag",()->{
            List<Entity> entities=Arrays.asList(new EntityItem(w,4,80,4,new ItemStack(Items.stick)),new EntityXPOrb(w,6,80,4,1),new EntityTNTPrimed(w),new EntityFallingBlock(w,10,80,4,Blocks.sand,0),new EntityArrow(w),new EntitySnowball(w));
            for(Entity e:entities){e.noClip=true;e.setPosition(4,80,4);e.motionX=e.motionY=e.motionZ=0;if(e instanceof EntityTNTPrimed)((EntityTNTPrimed)e).fuse=40;GravityAccess.set(e,true);e.onUpdate();equal(e.motionY,0,"Native no-gravity "+e.getClass());check(GravityAccess.get(e),"Flag missing");}
        });
        test("Explicit piston displacement clamps per axis per tick and no-clip movement bypasses the limit",()->{
            EntitySnowball e=new EntitySnowball(w);e.setPosition(4,80,4);long time=w.getTotalWorldTime();MovementAccess.move(e,MoverType.PISTON,0.4,0,0);MovementAccess.move(e,MoverType.PISTON,0.4,0,0);check(Math.abs(e.posX-4.51)<1E-12,"Piston aggregate cap");double x=e.posX;MovementAccess.move(e,MoverType.PISTON,0.000001,0,0);equal(e.posX,x,"Tiny piston request");
            w.getWorldInfo().incrementTotalWorldTime(time+1);MovementAccess.move(e,MoverType.PISTON,0.4,0,0);check(Math.abs(e.posX-4.91)<1E-12,"Tick reset");e.noClip=true;x=e.posX;MovementAccess.move(e,MoverType.PISTON,1,0,0);equal(e.posX,x+1,"No-clip piston bypass");w.getWorldInfo().incrementTotalWorldTime(time);
        });
        test("Player ledge protection applies to SELF/PLAYER while forced shulker movement bypasses it",()->{
            w.setBlock(10,80,0,Blocks.stone,0,3);double[] moved=new double[3];MoverType[] types={MoverType.SELF,MoverType.PLAYER,MoverType.SHULKER_BOX};
            for(int i=0;i<types.length;i++){FakePlayer p=new FakePlayer(w,new GameProfile(UUID.randomUUID(),"MotionQA"));p.yOffset=0;p.setPosition(10.5,81,0.5);p.setSneaking(true);p.onGround=true;MovementAccess.move(p,types[i],2,0,0);moved[i]=p.posX-10.5;clean((EntityMotionState)p);}
            check(moved[0]<2&&moved[1]==moved[0]&&moved[2]==2,"Mover-type sneak gate "+Arrays.toString(moved));w.setBlockToAir(10,80,0);
        });
        test("NoGravity root NBT is visible before subclass callbacks and an absent key clears constructor state",()->{
            Actor a=new Actor(w);GravityAccess.set(a,true);NBTTagCompound tag=new NBTTagCompound();a.writeToNBT(tag);check(tag.getBoolean("NoGravity")&&a.writeSawGravity,"Write order");Actor b=new Actor(w);b.readFromNBT(tag);check(GravityAccess.get(b)&&b.readSawGravity,"Read order");tag.removeTag("NoGravity");b.readFromNBT(tag);check(!GravityAccess.get(b)&&!b.readSawGravity,"Absent flag");check(!((EntityMotionState)b).srp$getBypassMove(),"Transient state persisted");GravityAccess.set(a,false);NBTTagCompound normal=new NBTTagCompound();a.writeToNBT(normal);check(!normal.hasKey("NoGravity"),"False flag must be omitted");
            GravityAccess.set(a,true);NBTTagCompound saved=new NBTTagCompound();a.writeToNBT(saved);try(DataOutputStream out=new DataOutputStream(new FileOutputStream("movement-fixture.dat"))){CompressedStreamTools.write(saved,out);}
        });
        test("Gravity codec preserves identity, kind, revision and flag, rejecting trailing or invalid data",()->{
            Actor a=new Actor(w);GravityAccess.set(a,true);for(int kind=0;kind<3;kind++){ByteBuf bytes=Unpooled.buffer();try{new GravityPacket(a,kind).toBytes(bytes);GravityPacket p=new GravityPacket();p.fromBytes(bytes);check(p.kind==kind&&p.noGravity&&p.identity.equals(a.getUniqueID())&&p.entityClass.equals(a.getClass().getName())&&p.revision==1,"Gravity packet loss");bytes.clear();new GravityPacket(a,kind).toBytes(bytes);bytes.writeByte(1);boolean bad=false;try{new GravityPacket().fromBytes(bytes);}catch(IllegalArgumentException expected){bad=true;}check(bad,"Trailing payload accepted");}finally{bytes.release();}}
        });
        test("Delayed snapshots, deltas, removals and stale revisions preserve entity gravity state",()->{
            GravityInbox inbox=new GravityInbox();Actor a=new Actor(w);a.setPosition(5,80,5);a.forceSpawn=true;GravityAccess.set(a,true);GravityPacket full=new GravityPacket(a,GravityPacket.FULL),old=new GravityPacket(a,GravityPacket.DELTA);GravityAccess.set(a,false);GravityPacket newer=new GravityPacket(a,GravityPacket.DELTA);inbox.enqueue(full);inbox.enqueue(newer);inbox.tick(w,(d,id)->{});check(w.spawnEntityInWorld(a),"Spawn delayed actor");GravityAccess.receive(a,true,0);inbox.tick(w,(d,id)->{});check(!GravityAccess.get(a)&&inbox.boundEntities()==1,"Delayed merge");inbox.enqueue(old);inbox.tick(w,(d,id)->{});check(!GravityAccess.get(a),"Stale delta applied");inbox.enqueue(new GravityPacket(a,GravityPacket.REMOVE));inbox.tick(w,(d,id)->{});check(inbox.boundEntities()==0,"Removal binding");w.removeEntity(a);
        });
    }
    void restarted(WorldServer w){
        test("Gravity survives an actual dedicated-server process restart",()->{NBTTagCompound tag;try(DataInputStream in=new DataInputStream(new FileInputStream("movement-fixture.dat"))){tag=CompressedStreamTools.read(in);}Actor a=new Actor(w);a.readFromNBT(tag);check(GravityAccess.get(a)&&a.readSawGravity,"Reloaded gravity");a.noClip=true;a.setPosition(0,80,0);a.motionY=0;a.moveEntityWithHeading(0,0);equal(a.motionY,0,"Reloaded physics");GravityAccess.set(a,false);a.moveEntityWithHeading(0,0);check(a.motionY<0,"Gravity did not resume");});
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent e){if(done||e.phase!=TickEvent.Phase.END||++ticks!=20)return;try{if(restart)restarted(MinecraftServer.getServer().worldServerForDimension(0));else initial(MinecraftServer.getServer().worldServerForDimension(0));}catch(Throwable t){failed++;report.add("FAIL: setup :: "+t);t.printStackTrace();}finally{done=true;report.add("Failed: "+failed);try{Files.write(Paths.get("movement-"+(restart?"restart":"initial")+".txt"),report,StandardCharsets.UTF_8);}catch(IOException t){throw new RuntimeException(t);}MinecraftServer.getServer().initiateShutdown();}}
}
