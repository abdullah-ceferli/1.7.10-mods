package dev.industrialtechnology.builder;

import java.util.*;
import java.lang.reflect.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.*;
import net.minecraftforge.event.world.BlockEvent;

/** Optional Carpenter's Blocks 3.3.8.2 support. Only decorative single-cell state is copied. */
public final class BuilderCarpenters {
    public static final String TAG="BuildCarpenter";
    private static final String PREFIX="com.carpentersblocks.block.BlockCarpenters";
    private static final String BASE="com.carpentersblocks.tileentity.TEBase";
    private static Method attributeDrop,attributePresent;private static Constructor<?> protectedOwner;
    public static boolean supported(Block b){if(b==null)return false;String n=b.getClass().getName();return n.equals(PREFIX+"Block")||n.equals(PREFIX+"Slope")||n.equals(PREFIX+"Stairs")||n.equals(PREFIX+"CollapsibleBlock")||n.equals(PREFIX+"Barrier");}
    public static boolean selected(ItemStack tool,ItemStack material){if(tool==null||!tool.hasTagCompound()||material==null)return false;NBTTagCompound n=tool.getTagCompound().getCompoundTag(TAG);Block b=Block.getBlockFromItem(material.getItem());return n.getInteger("Version")==1&&supported(b)&&n.getString("Block").equals(Block.blockRegistry.getNameForObject(b));}
    public static String label(ItemStack tool,ItemStack material){String text=material==null?"not selected":material.getDisplayName();if(selected(tool,material)){NBTTagList a=tool.getTagCompound().getCompoundTag(TAG).getTagList("Attributes",10);for(int i=0;i<a.tagCount();i++){NBTTagCompound c=a.getCompoundTagAt(i);if(c.getByte("Slot")==6){Item item=(Item)Item.itemRegistry.getObject(c.getString("Item"));if(item!=null)return text+" / "+new ItemStack(item,1,c.getInteger("Damage")).getDisplayName();}}return text+" (saved shape)";}return text;}
    /** Pure read: never calls Carpenter's getDrops/getAttributeForDrop on the original tile. */
    public static NBTTagCompound capture(World w,int x,int y,int z){
        Block b=w.getBlock(x,y,z);TileEntity t=w.getTileEntity(x,y,z);if(!supported(b)||t==null||!BASE.equals(t.getClass().getName()))return null;
        NBTTagCompound raw=new NBTTagCompound();t.writeToNBT(raw);NBTTagCompound n=new NBTTagCompound();n.setInteger("Version",1);n.setString("Block",Block.blockRegistry.getNameForObject(b));n.setInteger("Meta",w.getBlockMetadata(x,y,z));n.setInteger("Shape",raw.getInteger("cbMetadata"));n.setString("Design",raw.getString("cbDesign"));
        for(int i=0;i<7;i++)n.setString("Chisel"+i,raw.getString("cbChiselDesign_"+i));
        NBTTagList attrs=new NBTTagList(),source=raw.getTagList("cbAttrList",10);if(source.tagCount()>22)return null;boolean[] used=new boolean[22];
        for(int i=0;i<source.tagCount();i++){NBTTagCompound c=source.getCompoundTagAt(i);int slot=c.getByte("cbAttribute")&255;ItemStack s=ItemStack.loadItemStackFromNBT(c);if(slot>21||used[slot]||s==null||s.hasTagCompound())return null;used[slot]=true;NBTTagCompound a=new NBTTagCompound();a.setByte("Slot",(byte)slot);a.setString("Item",Item.itemRegistry.getNameForObject(s.getItem()));a.setInteger("Damage",s.getItemDamage());attrs.appendTag(a);}
        // Stable ordering makes equality independent of the upstream HashMap iteration order.
        NBTTagList ordered=new NBTTagList();for(int slot=0;slot<22;slot++)for(int i=0;i<attrs.tagCount();i++)if((attrs.getCompoundTagAt(i).getByte("Slot")&255)==slot)ordered.appendTag(attrs.getCompoundTagAt(i));n.setTag("Attributes",ordered);return valid(n,b)?n:null;
    }
    private static boolean valid(NBTTagCompound n,Block b){
        if(!supported(b)||n.getInteger("Version")!=1||n.getInteger("Meta")<0||n.getInteger("Meta")>15||n.getString("Design").length()>128)return false;
        String type=b.getClass().getName().substring(PREFIX.length());int data=n.getInteger("Shape");
        if(type.equals("Block")&&(data<0||data>6)||type.equals("Slope")&&(data<0||data>64)||type.equals("Stairs")&&(data<0||data>27)||type.equals("Barrier")&&((data&~31)!=0||(data&15)>6))return false;
        if(type.equals("CollapsibleBlock")){if((data>>>23)!=0||(data&7)>5)return false;for(int shift=3;shift<=18;shift+=5)if((data>>shift&31)>16)return false;}
        for(int i=0;i<7;i++)if(n.getString("Chisel"+i).length()>128)return false;
        NBTTagList list=n.getTagList("Attributes",10);if(list.tagCount()>22)return false;boolean[] used=new boolean[22];
        for(int i=0;i<list.tagCount();i++){NBTTagCompound c=list.getCompoundTagAt(i);int slot=c.getByte("Slot")&255;String id=c.getString("Item");Item item=(Item)Item.itemRegistry.getObject(id);if(slot>21||used[slot]||id.length()>256||item==null||c.getInteger("Damage")<0||c.getInteger("Damage")>32767)return false;used[slot]=true;if(slot<7&&!BuilderData.ordinary(new ItemStack(item,1,c.getInteger("Damage"))))return false;}
        return true;
    }
    public static Template read(ItemStack tool,World world){
        ItemStack material=BuilderData.material(tool);if(!selected(tool,material))return null;NBTTagCompound n=(NBTTagCompound)tool.getTagCompound().getCompoundTag(TAG).copy();Block b=Block.getBlockFromItem(material.getItem());if(!valid(n,b))return null;
        try{return new Template(n,b,world);}catch(ReflectiveOperationException e){throw new IllegalArgumentException("This Carpenter's Blocks version cannot copy its saved appearance.",e);}
    }
    private static void api(TileEntity t)throws ReflectiveOperationException{if(attributeDrop==null){attributeDrop=t.getClass().getMethod("getAttributeForDrop",byte.class);attributePresent=t.getClass().getMethod("hasAttribute",byte.class);protectedOwner=Class.forName("com.carpentersblocks.util.protection.ProtectedObject").getConstructor(EntityPlayer.class);}}
    private static NBTTagCompound state(NBTTagCompound template,TileEntity tile,String owner){
        NBTTagCompound n=new NBTTagCompound();tile.writeToNBT(n);n.setInteger("cbMetadata",template.getInteger("Shape"));n.setString("cbDesign",template.getString("Design"));n.setString("cbOwner",owner);
        for(int i=0;i<7;i++)n.setString("cbChiselDesign_"+i,template.getString("Chisel"+i));
        NBTTagList list=new NBTTagList(),attrs=template.getTagList("Attributes",10);for(int i=0;i<attrs.tagCount();i++){NBTTagCompound a=attrs.getCompoundTagAt(i),c=new NBTTagCompound();Item item=(Item)Item.itemRegistry.getObject(a.getString("Item"));new ItemStack(item,1,a.getInteger("Damage")).writeToNBT(c);c.setString("cbUniqueId",a.getString("Item"));c.setByte("cbAttribute",a.getByte("Slot"));list.appendTag(c);}n.setTag("cbAttrList",list);return n;
    }
    public static final class Template {
        private final NBTTagCompound appearance;public final Block block;private final List<ItemStack> costs=new ArrayList<ItemStack>();
        private Template(NBTTagCompound n,Block b,World world)throws ReflectiveOperationException{
            appearance=n;block=b;TileEntity detached=b.createTileEntity(world,n.getInteger("Meta"));if(detached==null||!BASE.equals(detached.getClass().getName()))throw new IllegalArgumentException("Unsupported Carpenter's tile");api(detached);detached.setWorldObj(null);detached.readFromNBT(state(n,detached,""));detached.setWorldObj(world);
            costs.add(new ItemStack(b,1,b.damageDropped(n.getInteger("Meta"))));NBTTagList attrs=n.getTagList("Attributes",10);
            for(int i=0;i<attrs.tagCount();i++){ItemStack c=(ItemStack)attributeDrop.invoke(detached,attrs.getCompoundTagAt(i).getByte("Slot"));if(c==null||c.hasTagCompound())throw new IllegalArgumentException("Unsupported stored cover item");c=c.copy();c.stackSize=1;costs.add(c);}
        }
        public boolean matches(World w,BuildPos p){return w.getBlock(p.x,p.y,p.z)==block&&appearance.equals(capture(w,p.x,p.y,p.z));}
        private boolean covered(int side){NBTTagList a=appearance.getTagList("Attributes",10);for(int i=0;i<a.tagCount();i++)if((a.getCompoundTagAt(i).getByte("Slot")&255)==side)return true;return false;}
        private boolean safeNeighbors(World w,BuildPos p){
            for(ForgeDirection f:ForgeDirection.VALID_DIRECTIONS){int x=p.x+f.offsetX,y=p.y+f.offsetY,z=p.z+f.offsetZ;if(y<0||y>=w.getHeight())continue;Block neighbor=w.getBlock(x,y,z);boolean own=block.isSideSolid(w,p.x,p.y,p.z,f),other=neighbor.isSideSolid(w,x,y,z,f.getOpposite());
                if(own&&other){if(covered(f.ordinal()))return false;TileEntity t=w.getTileEntity(x,y,z);if(t!=null&&attributePresent.getDeclaringClass().isInstance(t)){try{if((Boolean)attributePresent.invoke(t,(byte)f.getOpposite().ordinal()))return false;}catch(ReflectiveOperationException e){throw new IllegalStateException(e);}}}
            }return true;
        }
        /** -1 out of stock, 0 skipped, 1 paid placement. Uses the normal builder budget. */
        @SuppressWarnings({"rawtypes","unchecked"}) public int place(EntityPlayerMP p,BuildPos pos,int side){
            World w=p.worldObj;Block before=w.getBlock(pos.x,pos.y,pos.z);ItemStack base=costs.get(0);
            if(w.getTileEntity(pos.x,pos.y,pos.z)!=null||!before.isReplaceable(w,pos.x,pos.y,pos.z)||!w.canMineBlock(p,pos.x,pos.y,pos.z)||MinecraftServer.getServer().isBlockProtected(w,pos.x,pos.y,pos.z,p)||!p.canPlayerEdit(pos.x,pos.y,pos.z,side,base))return 0;
            Payment paid=p.capabilities.isCreativeMode?new Payment():Payment.reserve(p,costs);if(paid==null)return -1;
            BlockSnapshot snapshot=BlockSnapshot.getBlockSnapshot(w,pos.x,pos.y,pos.z);boolean accepted=false;TileEntity installed=null;
            try{
                if(!w.setBlock(pos.x,pos.y,pos.z,block,appearance.getInteger("Meta"),2))return 0;installed=w.getTileEntity(pos.x,pos.y,pos.z);if(installed==null||!BASE.equals(installed.getClass().getName()))return 0;
                String owner;try{owner=protectedOwner.newInstance(p).toString();}catch(ReflectiveOperationException e){throw new IllegalStateException(e);}
                installed.readFromNBT(state(appearance,installed,owner));installed.markDirty();
                List<AxisAlignedBB> boxes=new ArrayList<AxisAlignedBB>();block.addCollisionBoxesToList(w,pos.x,pos.y,pos.z,AxisAlignedBB.getBoundingBox(pos.x-.25,pos.y-.25,pos.z-.25,pos.x+1.25,pos.y+1.5,pos.z+1.25),boxes,null);for(AxisAlignedBB box:boxes)if(!w.checkNoEntityCollision(box))return 0;
                if(!safeNeighbors(w,pos))return 0;
                if(MinecraftForge.EVENT_BUS.post(new BlockEvent.PlaceEvent(snapshot,before,p))||!matches(w,pos))return 0;
                accepted=true;w.markBlockForUpdate(pos.x,pos.y,pos.z);w.notifyBlocksOfNeighborChange(pos.x,pos.y,pos.z,block);w.playSoundEffect(pos.x+.5,pos.y+.5,pos.z+.5,block.stepSound.func_150496_b(),.8F,1);return 1;
            }finally{
                if(!accepted){boolean restoring=w.restoringBlockSnapshots;w.restoringBlockSnapshots=true;try{snapshot.restore(true,false);}finally{w.restoringBlockSnapshots=restoring;paid.refund(p);}}
            }
        }
    }
    private static final class Payment {
        private final List<ItemStack> taken=new ArrayList<ItemStack>();
        static Payment reserve(EntityPlayerMP p,List<ItemStack> costs){int[] take=new int[p.inventory.mainInventory.length];for(ItemStack need:costs){boolean found=false;for(int i=0;i<take.length;i++){ItemStack s=p.inventory.mainInventory[i];if(s!=null&&!s.hasTagCompound()&&s.getItem()==need.getItem()&&s.getItemDamage()==need.getItemDamage()&&s.stackSize>take[i]){take[i]++;found=true;break;}}if(!found)return null;}Payment payment=new Payment();for(int i=0;i<take.length;i++)if(take[i]>0)payment.taken.add(p.inventory.decrStackSize(i,take[i]));return payment;}
        void refund(EntityPlayerMP p){for(ItemStack s:taken)if(!p.inventory.addItemStackToInventory(s))p.entityDropItem(s,0);}
    }
    private BuilderCarpenters(){}
}
