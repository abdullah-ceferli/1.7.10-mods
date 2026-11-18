package com.baisylia.culturaldelights.legacy;
import com.google.gson.*;
import java.util.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.*;
import net.minecraft.util.*;
public final class GardenBlock extends Block implements IGrowable,net.minecraftforge.common.IShearable {
    public static int renderId=-1;
    public final String id,kind;public final JsonObject info;public final int maxAge;
    public GardenBlock(String name,String type,JsonObject data){super(type.equals("leaves")?Material.leaves:type.equals("wild")||type.equals("sapling")||type.equals("pit")?Material.plants:type.equals("ore")?Material.rock:Material.wood);id=name;kind=type;info=data;maxAge=Catalog.num(data,"maxAge",3);
        setBlockName(Catalog.MOD+"."+id);setCreativeTab(Port.CONTENT.tab);setHardness(kind.equals("ore")?3:kind.equals("wood")||kind.equals("solid")?2:kind.equals("leaves")?.2F:0);setTickRandomly(kind.equals("leaves")||kind.equals("sapling")||kind.equals("pit"));setStepSound(kind.equals("ore")?soundTypeStone:kind.equals("wood")||kind.equals("solid")?soundTypeWood:soundTypeGrass);setLightOpacity(isOpaqueCube()?255:kind.equals("leaves")?1:0);
        if(kind.equals("sapling")||kind.equals("wild")||kind.equals("pit"))setBlockBounds(.1F,0,.1F,.9F,.8F,.9F);
        if(kind.equals("feast"))setBlockBounds(.125F,0,.125F,.875F,.625F,.875F);
        if(kind.equals("potted"))setBlockBounds(.25F,0,.25F,.75F,.7F,.75F);
    }
    public boolean isOpaqueCube(){return "solid".equals(kind)||"wood".equals(kind)||"ore".equals(kind);}
    public boolean renderAsNormalBlock(){return isOpaqueCube();}
    public int getRenderType(){return renderId;}
    public boolean isLeaves(IBlockAccess w,int x,int y,int z){return kind.equals("leaves");}
    public boolean isWood(IBlockAccess w,int x,int y,int z){return kind.equals("wood");}
    public boolean canSustainLeaves(IBlockAccess w,int x,int y,int z){return kind.equals("wood");}
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World w,int x,int y,int z){return kind.equals("wild")||kind.equals("sapling")||kind.equals("pit")?null:super.getCollisionBoundingBoxFromPool(w,x,y,z);}
    public boolean canPlaceBlockAt(World w,int x,int y,int z){return super.canPlaceBlockAt(w,x,y,z)&&(!kind.equals("sapling")&&!kind.equals("pit")&&!kind.equals("wild")||soil(w.getBlock(x,y-1,z)));}
    private boolean soil(Block b){return b==Blocks.grass||b==Blocks.dirt||b==Blocks.farmland;}
    public void onNeighborBlockChange(World w,int x,int y,int z,Block other){if((kind.equals("wild")||kind.equals("sapling")||kind.equals("pit"))&&!soil(w.getBlock(x,y-1,z))){dropBlockAsItem(w,x,y,z,w.getBlockMetadata(x,y,z),0);w.setBlockToAir(x,y,z);}else if(kind.equals("feast")&&!World.doesBlockHaveSolidTopSurface(w,x,y-1,z)){dropBlockAsItem(w,x,y,z,0,0);w.setBlockToAir(x,y,z);}}
    public void onBlockPlacedBy(World w,int x,int y,int z,EntityLivingBase p,ItemStack s){if(kind.equals("leaves"))w.setBlockMetadataWithNotify(x,y,z,(s.getItemDamage()&7)|8,2);else if(kind.equals("feast")){GardenTile t=(GardenTile)w.getTileEntity(x,y,z);t.facing=(MathHelper.floor_double(p.rotationYaw*4/360F+.5D)+2)&3;t.changed();}}
    public int onBlockPlaced(World w,int x,int y,int z,int side,float a,float b,float c,int meta){return kind.equals("leaves")?(meta&7)|8:kind.equals("wood")?(side==0||side==1?0:side<4?8:4):0;}
    public boolean hasTileEntity(int meta){return kind.equals("feast");}
    public boolean removedByPlayer(World w,EntityPlayer p,int x,int y,int z,boolean harvest){return kind.equals("feast")&&harvest||super.removedByPlayer(w,p,x,y,z,harvest);}
    public void harvestBlock(World w,EntityPlayer p,int x,int y,int z,int meta){super.harvestBlock(w,p,x,y,z,meta);if(kind.equals("feast"))w.setBlockToAir(x,y,z);}
    public TileEntity createTileEntity(World w,int meta){GardenTile t=new GardenTile();t.remaining=id.equals("exotic_roll_medley")?8:4;return t;}
    public boolean hasComparatorInputOverride(){return kind.equals("feast");}
    public int getComparatorInputOverride(World w,int x,int y,int z,int side){TileEntity t=w.getTileEntity(x,y,z);return t instanceof GardenTile?((GardenTile)t).remaining:0;}
    public void updateTick(World w,int x,int y,int z,Random r){int meta=w.getBlockMetadata(x,y,z);
        if(kind.equals("sapling")||kind.equals("pit")){if(w.getBlockLightValue(x,y+1,z)>=9&&r.nextInt(7)==0)func_149853_b(w,r,x,y,z);return;}
        if(!kind.equals("leaves")||(meta&8)!=0&&Catalog.MOD.equals("croptopia"))return;
        if((meta&8)==0&&w.checkChunksExist(x-6,y-6,z-6,x+6,y+6,z+6)&&!connected(w,x,y,z)){dropBlockAsItem(w,x,y,z,meta,0);w.setBlockToAir(x,y,z);return;}
        if(id.equals("avocado_leaves")||id.equals("cinnamon_leaves"))return;
        int age=meta&7;if(age<maxAge&&w.getBlockLightValue(x,y,z)>=9&&(!Catalog.MOD.equals("croptopia")||r.nextInt(20)==0))w.setBlockMetadataWithNotify(x,y,z,(meta&8)|age+1,2);
    }
    private boolean connected(World w,int x,int y,int z){Set<String> seen=new HashSet<String>();List<int[]> queue=new ArrayList<int[]>();queue.add(new int[]{x,y,z,0});seen.add(x+","+y+","+z);for(int i=0;i<queue.size();i++){int[] p=queue.get(i);for(int[] d:DIR){int X=p[0]+d[0],Y=p[1]+d[1],Z=p[2]+d[2];Block b=w.getBlock(X,Y,Z);if(b.canSustainLeaves(w,X,Y,Z))return true;if(p[3]<5&&b.isLeaves(w,X,Y,Z)&&seen.add(X+","+Y+","+Z))queue.add(new int[]{X,Y,Z,p[3]+1});}}return false;}
    private static final int[][] DIR={{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}};
    public boolean func_149851_a(World w,int x,int y,int z,boolean client){return kind.equals("sapling")||kind.equals("pit")||kind.equals("leaves")&&!id.equals("avocado_leaves")&&!id.equals("cinnamon_leaves")&&(w.getBlockMetadata(x,y,z)&7)<maxAge&&(!Catalog.MOD.equals("croptopia")||(w.getBlockMetadata(x,y,z)&8)==0);}
    public boolean func_149852_a(World w,Random r,int x,int y,int z){return !kind.equals("sapling")||r.nextFloat()<.45F;}
    public void func_149853_b(World w,Random r,int x,int y,int z){if(kind.equals("pit")){w.setBlock(x,y,z,Port.CONTENT.blocks.get("avocado_sapling"),0,3);return;}if(kind.equals("sapling")){Gardens.tree(w,r,x,y,z,info);return;}if(func_149851_a(w,x,y,z,false)){int m=w.getBlockMetadata(x,y,z);w.setBlockMetadataWithNotify(x,y,z,(m&8)|Math.min(maxAge,(m&7)+1),2);}}
    public boolean onBlockActivated(World w,int x,int y,int z,EntityPlayer p,int side,float a,float b,float c){int meta=w.getBlockMetadata(x,y,z);ItemStack held=p.getCurrentEquippedItem();
        if(kind.equals("feast"))return serve(w,x,y,z,p);
        if(kind.equals("wood")&&id.startsWith("cinnamon")&&held!=null&&held.getItem().getToolClasses(held).contains("axe")){if(!w.isRemote){w.setBlock(x,y,z,Port.CONTENT.blocks.get("stripped_"+id),meta,3);dropBlockAsItem(w,x,y,z,Port.CONTENT.stack("cinnamon"));held.damageItem(1,p);}return true;}
        if(kind.equals("leaves")&&!id.equals("avocado_leaves")&&!id.equals("cinnamon_leaves")&&(meta&7)==maxAge&&(!Catalog.MOD.equals("croptopia")||(meta&8)==0)){
            if(!w.isRemote){if(Catalog.MOD.equals("culturaldelights")){ItemStack fruit=Port.CONTENT.stack("avocado");fruit.stackSize=2+w.rand.nextInt(2);dropBlockAsItem(w,x,y,z,fruit);}else for(ItemStack s:Loot.drops(id,meta,0,held,w.rand))dropBlockAsItem(w,x,y,z,s);w.setBlockMetadataWithNotify(x,y,z,meta&8,2);w.playSoundEffect(x+.5,y+.5,z+.5,"dig.grass",1,.8F+w.rand.nextFloat()*.4F);}return true;
        }return false;
    }
    private boolean serve(World w,int x,int y,int z,EntityPlayer p){GardenTile t=(GardenTile)w.getTileEntity(x,y,z);if(t==null)return false;ItemStack held=p.getCurrentEquippedItem();boolean medley=id.equals("exotic_roll_medley");
        if(t.remaining==0){if(!w.isRemote){dropBlockAsItem(w,x,y,z,new ItemStack(net.minecraft.init.Items.bowl));w.setBlockToAir(x,y,z);}return true;}
        if(!medley&&(held==null||held.getItem()!=net.minecraft.init.Items.bowl))return false;
        if(!w.isRemote){String[] rolls={"pufferfish_roll","pufferfish_roll","tropical_roll","tropical_roll","tropical_roll","chicken_roll_slice","chicken_roll_slice","chicken_roll_slice"};ItemStack serving=Port.CONTENT.stack(medley?rolls[t.remaining-1]:"eggplant_parmesan");if(serving==null)return false;if(!medley&&!p.capabilities.isCreativeMode&&--held.stackSize==0)p.inventory.setInventorySlotContents(p.inventory.currentItem,null);if(!p.inventory.addItemStackToInventory(serving))p.dropPlayerItemWithRandomChoice(serving,false);t.remaining--;t.changed();}return true;
    }
    public ArrayList<ItemStack> getDrops(World w,int x,int y,int z,int meta,int fortune){EntityPlayer p=harvesters.get();if(kind.equals("feast")){TileEntity te=w.getTileEntity(x,y,z);ArrayList<ItemStack> drops=new ArrayList<ItemStack>();if(te instanceof GardenTile&&((GardenTile)te).remaining==(id.equals("exotic_roll_medley")?8:4))drops.add(new ItemStack(this));else if(!id.equals("exotic_roll_medley"))drops.add(new ItemStack(net.minecraft.init.Items.bowl));return drops;}return Loot.drops(id,meta,fortune,p==null?null:p.getCurrentEquippedItem(),w.rand);}
    public int damageDropped(int meta){return 0;}
    public boolean isShearable(ItemStack tool,IBlockAccess w,int x,int y,int z){return kind.equals("leaves")||kind.equals("wild");}
    public ArrayList<ItemStack> onSheared(ItemStack tool,IBlockAccess w,int x,int y,int z,int fortune){return Loot.drops(id,w.getBlockMetadata(x,y,z),fortune,tool,new Random());}
    @SideOnly(Side.CLIENT) public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister r){JsonObject textures=Port.CONTENT.data.getAsJsonObject("blockTextures");String texture=Catalog.str(textures,id,Catalog.MOD+":"+id);if(texture.startsWith("minecraft:"))texture="minecraft:"+(kind.equals("leaves")?"leaves_jungle":kind.equals("wood")?"log_jungle":"dirt");blockIcon=r.registerIcon(texture);}
}
