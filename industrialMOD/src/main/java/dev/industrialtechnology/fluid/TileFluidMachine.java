package dev.industrialtechnology.fluid;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Canonical contents are vanilla NBT values; no HBM IDs or network references are persisted. */
public class TileFluidMachine extends TileEntity {
    public static final int NONE=0,WATER=1,LAVA=2,CAPACITY=8000,BUCKET=1000,ACTION_INTERVAL=20,RETRY_INTERVAL=100;
    private final boolean sucker;
    private int selected,amount;
    private UUID ownerId;
    private String ownerName="[FluidMachine]";
    private boolean queued;
    private FluidSearch search;
    private long nextAction,nextNetwork,retry;
    public int lastProbes;
    public boolean lastAttempt;
    public TileFluidMachine(){this(false);}
    protected TileFluidMachine(boolean sucker){this.sucker=sucker;}
    public static final class Sucker extends TileFluidMachine {public Sucker(){super(true);}}
    public static final class Puller extends TileFluidMachine {public Puller(){super(false);}}
    public final boolean isSucker(){return sucker;}
    public final int selected(){return selected;}
    public int amount(){return amount;}
    protected void amount(int value){amount=Math.max(0,Math.min(CAPACITY,value));markDirty();}
    protected void selectionChanged(){}
    public final ForgeDirection front(){ForgeDirection f=ForgeDirection.getOrientation(getBlockMetadata());return f==ForgeDirection.UNKNOWN?ForgeDirection.DOWN:f;}
    public final boolean hasOwner(){return ownerId!=null;}
    public final void owner(EntityPlayer player){ownerId=player.getUniqueID();ownerName=player.getCommandSenderName();markDirty();}
    public final GameProfile ownerProfile(){return new GameProfile(ownerId==null?UUID.nameUUIDFromBytes("industrialtech:fluid-machine".getBytes(java.nio.charset.StandardCharsets.UTF_8)):ownerId,ownerName);}
    public final boolean configure(int kind){
        if(kind<NONE||kind>LAVA||amount()>0&&kind!=selected)return false;
        if(kind!=selected){selected=kind;selectionChanged();resetSearch();changed();}
        if(selected!=NONE)enqueue();return true;
    }
    public final void changed(){markDirty();if(worldObj!=null&&!worldObj.isRemote)worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);}
    @Override public void markDirty(){
        if(worldObj==null||!loaded(worldObj,xCoord,yCoord,zCoord))return;
        blockMetadata=worldObj.getBlockMetadata(xCoord,yCoord,zCoord);
        worldObj.markTileEntityChunkModified(xCoord,yCoord,zCoord,this);
        // Vanilla markDirty probes neighbors (and neighbors behind solid blocks).
        // Save the tile even at a chunk edge, but notify only within loaded terrain.
        for(int cx=(xCoord-2)>>4;cx<=((xCoord+2)>>4);cx++)for(int cz=(zCoord-2)>>4;cz<=((zCoord+2)>>4);cz++)if(!worldObj.getChunkProvider().chunkExists(cx,cz))return;
        if(getBlockType()!=Blocks.air)worldObj.func_147453_f(xCoord,yCoord,zCoord,getBlockType());
    }
    public final void resetSearch(){search=null;retry=0;nextAction=0;nextNetwork=0;}
    public final boolean live(){return worldObj!=null&&!worldObj.isRemote&&!isInvalid()&&loaded(worldObj,xCoord,yCoord,zCoord)&&worldObj.getTileEntity(xCoord,yCoord,zCoord)==this;}
    public static boolean loaded(World world,int x,int y,int z){return world!=null&&y>=0&&y<world.getHeight()&&world.getChunkProvider().chunkExists(x>>4,z>>4);}
    public final int accept(int kind,long offered){
        if(isSucker()||kind!=selected||selected==NONE||offered<=0||!live())return 0;
        int accepted=(int)Math.min((long)CAPACITY-amount(),offered);if(accepted>0){amount(amount()+accepted);}return accepted;
    }
    public final void consume(long used){int taken=(int)Math.min((long)amount(),Math.max(0L,used));if(taken>0){amount(amount()-taken);}}
    private void enqueue(){if(worldObj!=null&&!worldObj.isRemote&&!queued&&selected!=NONE){queued=true;FluidMachineScheduler.add(this);}}
    @Override public void updateEntity(){enqueue();}
    @Override public void validate(){super.validate();queued=false;resetSearch();}
    @Override public void invalidate(){queued=false;if(worldObj!=null&&!worldObj.isRemote)FluidMachineScheduler.remove(this);super.invalidate();}
    @Override public void onChunkUnload(){queued=false;if(worldObj!=null&&!worldObj.isRemote)FluidMachineScheduler.remove(this);search=null;super.onChunkUnload();}
    void dequeued(){queued=false;}
    protected void networkStep(){}
    public final void network(){if(!live()||selected==NONE)return;long now=worldObj.getTotalWorldTime();if(now<nextNetwork)return;nextNetwork=now+20;networkStep();}
    /** Returns whether a world block changed; every inspected cell is counted against the caller's budget. */
    public final boolean work(int probeBudget,boolean allowChange){
        lastProbes=0;lastAttempt=false;if(!live()||selected==NONE||!FluidMachineRegistry.hasHbm||!allowChange)return false;
        long now=worldObj.getTotalWorldTime();if(now<nextAction)return false;
        if(sucker){
            if(CAPACITY-amount()<BUCKET||probeBudget<=0||now<retry)return false;
            if(search==null){ForgeDirection f=front();search=new FluidSearch();search.offer(f.offsetX,f.offsetY,f.offsetZ);}
            while(lastProbes<probeBudget&&!search.isEmpty()){
                int key=search.poll(),x=xCoord+FluidSearch.x(key),y=yCoord+FluidSearch.y(key),z=zCoord+FluidSearch.z(key);lastProbes++;
                if(!loaded(worldObj,x,y,z))continue;
                Block b=worldObj.getBlock(x,y,z);if(!matches(selected,b))continue;
                search.expand(key);
                if(worldObj.getBlockMetadata(x,y,z)!=0)continue;
                nextAction=now+ACTION_INTERVAL;lastAttempt=true;
                if(FluidWorld.drain(this,x,y,z,b))return true;
                return false;
            }
            if(search.isEmpty()){search=null;retry=now+RETRY_INTERVAL;}
            return false;
        }
        if(amount()<BUCKET||probeBudget<=0)return false;
        ForgeDirection f=front();int x=xCoord+f.offsetX,y=yCoord+f.offsetY,z=zCoord+f.offsetZ;lastProbes=1;nextAction=now+ACTION_INTERVAL;
        lastAttempt=true;return FluidWorld.place(this,x,y,z);
    }
    public static boolean matches(int kind,Block b){return kind==WATER?(b==Blocks.water||b==Blocks.flowing_water):kind==LAVA&&(b==Blocks.lava||b==Blocks.flowing_lava);}
    public final NBTTagCompound contents(){NBTTagCompound n=new NBTTagCompound();n.setByte("Selected",(byte)selected);n.setInteger("Amount",amount());return n;}
    public final void readContents(NBTTagCompound n){selected=n.getByte("Selected");if(selected<NONE||selected>LAVA)selected=NONE;selectionChanged();amount(selected==NONE?0:n.getInteger("Amount"));resetSearch();}
    @Override public void writeToNBT(NBTTagCompound n){super.writeToNBT(n);n.setTag("FluidMachine",contents());if(ownerId!=null)n.setString("Owner",ownerId.toString());n.setString("OwnerName",ownerName);}
    @Override public void readFromNBT(NBTTagCompound n){super.readFromNBT(n);readContents(n.getCompoundTag("FluidMachine"));try{ownerId=UUID.fromString(n.getString("Owner"));}catch(IllegalArgumentException invalid){ownerId=null;}String name=n.getString("OwnerName");if(!name.isEmpty()&&name.length()<=16)ownerName=name;}
    @Override public Packet getDescriptionPacket(){NBTTagCompound n=contents();return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,0,n);}
    @Override public void onDataPacket(NetworkManager manager,S35PacketUpdateTileEntity packet){readContents(packet.func_148857_g());}
}
