package com.baisylia.culturaldelights.legacy;
import com.google.gson.JsonObject;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.*;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.*;
public final class Plant extends BlockCrops {
    public final String id,seed,food;public final int maxAge;public final boolean upper,doubled;
    public final JsonObject definition;
    public Plant(String name,JsonObject data,boolean top){id=name;definition=data;seed=Catalog.str(data,"seed","");food=Catalog.str(data,"food","");maxAge=Catalog.num(data,"maxAge",7);upper=top;doubled=Catalog.yes(data,"double");setBlockName(Catalog.MOD+"."+id);setHardness(0);setTickRandomly(true);setStepSound(soundTypeGrass);setLightOpacity(0);}
    protected Item func_149866_i(){return Port.CONTENT.items.get(seed);}
    protected Item func_149865_P(){return Port.CONTENT.items.get(food);}
    protected boolean canPlaceBlockOn(Block b){return Catalog.MOD.equals("croptopia")?(b==Blocks.farmland||b==Blocks.grass||b==Blocks.dirt||b==Blocks.sand):b==Blocks.farmland;}
    public boolean canBlockStay(World w,int x,int y,int z){return upper?w.getBlock(x,y-1,z)==Port.CONTENT.crops.get("corn"):canPlaceBlockOn(w.getBlock(x,y-1,z));}
    public void updateTick(World w,int x,int y,int z,Random r){
        if(!canBlockStay(w,x,y,z)){dropBlockAsItem(w,x,y,z,w.getBlockMetadata(x,y,z),0);w.setBlockToAir(x,y,z);return;}
        if(w.getBlockLightValue(x,y+1,z)<(doubled?6:9))return;
        int age=w.getBlockMetadata(x,y,z)&7;
        if(age<maxAge && r.nextInt(doubled?3:Math.max(2,(int)(25/growth(w,x,y,z))+1))==0)w.setBlockMetadataWithNotify(x,y,z,age+1,2);
        else if(doubled&&!upper&&age==maxAge&&w.isAirBlock(x,y+1,z))w.setBlock(x,y+1,z,Port.CONTENT.crops.get("corn_upper"),0,3);
    }
    private float growth(World w,int x,int y,int z){float f=1;for(int a=-1;a<=1;a++)for(int b=-1;b<=1;b++){Block floor=w.getBlock(x+a,y-1,z+b);float add=floor==Blocks.farmland?(w.getBlockMetadata(x+a,y-1,z+b)>0?3:1):0;if(a!=0||b!=0)add/=4;f+=add;}if(w.getBlock(x-1,y,z)==this||w.getBlock(x+1,y,z)==this){if(w.getBlock(x,y,z-1)==this||w.getBlock(x,y,z+1)==this)f/=2;}else if(w.getBlock(x-1,y,z-1)==this||w.getBlock(x+1,y,z+1)==this||w.getBlock(x-1,y,z+1)==this||w.getBlock(x+1,y,z-1)==this)f/=2;return f;}
    public boolean func_149851_a(World w,int x,int y,int z,boolean client){return (w.getBlockMetadata(x,y,z)&7)<maxAge||doubled&&!upper&&(w.isAirBlock(x,y+1,z)||w.getBlock(x,y+1,z)==Port.CONTENT.crops.get("corn_upper")&&w.getBlockMetadata(x,y+1,z)<3);}
    public boolean func_149852_a(World w,Random r,int x,int y,int z){return true;}
    public void func_149853_b(World w,Random r,int x,int y,int z){int growth=doubled?(upper?1:1+r.nextInt(4)):2+r.nextInt(4);int age=w.getBlockMetadata(x,y,z)+growth;if(doubled&&!upper&&age>maxAge){w.setBlockMetadataWithNotify(x,y,z,maxAge,2);Block top=w.getBlock(x,y+1,z);if(top==Port.CONTENT.crops.get("corn_upper"))((Plant)top).func_149853_b(w,r,x,y+1,z);else if(w.isAirBlock(x,y+1,z))w.setBlock(x,y+1,z,Port.CONTENT.crops.get("corn_upper"),Math.min(3,age-maxAge-1),3);}else w.setBlockMetadataWithNotify(x,y,z,Math.min(maxAge,age),2);}
    public boolean onBlockActivated(World w,int x,int y,int z,EntityPlayer p,int side,float a,float b,float c){if(!Catalog.MOD.equals("croptopia")||w.getBlockMetadata(x,y,z)!=maxAge)return false;if(!w.isRemote){for(ItemStack drop:Loot.drops(id,w.getBlockMetadata(x,y,z),0,p.getCurrentEquippedItem(),w.rand))dropBlockAsItem(w,x,y,z,drop);w.setBlockMetadataWithNotify(x,y,z,0,2);p.addExhaustion(.005F);}return true;}
    public ArrayList<ItemStack> getDrops(World w,int x,int y,int z,int meta,int fortune){EntityPlayer p=harvesters.get();return Loot.drops(id,meta,fortune,p==null?null:p.getCurrentEquippedItem(),w.rand);}
    public Item getItem(World w,int x,int y,int z){return func_149866_i();}
    public int getRenderType(){return GardenBlock.renderId;}
    public void setBlockBoundsBasedOnState(IBlockAccess w,int x,int y,int z){int age=Math.min(7,w.getBlockMetadata(x,y,z)&7);int[] heights=doubled?new int[]{8,10,12,16,16,16,16,16}:Catalog.MOD.equals("croptopia")?new int[]{2,3,4,5,8,10,12,16}:id.equals("cucumbers")?new int[]{2,3,5,7,8,9,9,10}:new int[]{2,3,5,7,8,9,14,16};setBlockBounds(0,0,0,1,heights[age]/16F,1);}
    @SideOnly(Side.CLIENT) public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister r){blockIcon=r.registerIcon(Catalog.str(Port.CONTENT.data.getAsJsonObject("blockTextures"),id,Catalog.MOD+":"+id));}
    @SideOnly(Side.CLIENT) public IIcon getIcon(int side,int meta){return blockIcon;}
}
