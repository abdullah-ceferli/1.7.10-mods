package com.jkvin114.displaydelight;

import java.util.*;
import net.minecraft.block.*;import net.minecraft.block.material.Material;import net.minecraft.world.*;
import net.minecraft.item.*;import net.minecraft.init.Blocks;import net.minecraft.entity.*;import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;import net.minecraft.tileentity.TileEntity;import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.*;

public final class DisplayBlock extends BlockContainer {
    public static int renderId;public final Catalog.Definition definition;public ItemStack food;
    public DisplayBlock(Catalog.Definition d){
        super(Material.wood);definition=d;setBlockName("displaydelight."+d.id);setHardness(0);setResistance(0);setLightOpacity(0);
        setStepSound(d.kind.equals("drink")?soundTypeGlass:soundTypeWood);if(Settings.creative)setCreativeTab(DisplayDelight.TAB);bounds();
    }
    private void bounds(){
        float a=3,b=13,h=6;String k=definition.kind;
        if(k.equals("drink")){a=4;b=12;h=10;}else if(k.equals("wide")){a=1;b=15;h=4;}
        else if(k.equals("plate")||k.equals("empty")){a=1;b=15;h=k.equals("empty")?2:6;}
        else if(k.equals("small")||k.equals("empty_small")){a=4;b=12;h=k.equals("empty_small")?1:5;}
        setBlockBounds(a/16,0,a/16,b/16,h/16,b/16);
    }
    @Override public void setBlockBoundsBasedOnState(IBlockAccess w,int x,int y,int z){bounds();}
    @Override public void setBlockBoundsForItemRender(){bounds();}
    @Override public boolean isOpaqueCube(){return false;}
    @Override public boolean renderAsNormalBlock(){return false;}
    @Override public int getRenderType(){return renderId;}
    @Override public int getMobilityFlag(){return 1;}
    @Override public TileEntity createNewTileEntity(World w,int meta){return new DisplayTile();}
    @Override public boolean onBlockActivated(World w,int x,int y,int z,EntityPlayer p,int side,float hx,float hy,float hz){return Interactions.use(w,x,y,z,p);}
    @Override public void onBlockPlacedBy(World w,int x,int y,int z,EntityLivingBase e,ItemStack stack){
        int facing=net.minecraft.util.MathHelper.floor_double(e.rotationYaw*4/360+0.5)&3;w.setBlockMetadataWithNotify(x,y,z,new int[]{0,1,2,3}[facing],2);
        DisplayTile t=DisplayTile.at(w,x,y,z);if(t!=null){NBTTagCompound n=stack.getTagCompound();t.set(n!=null&&n.hasKey("FoodCount")?n.getInteger("FoodCount"):definition.max,n!=null&&n.getBoolean("PlateHidden"));}
    }
    public static boolean place(World w,int x,int y,int z,DisplayBlock b,int facing,int count,boolean hidden){
        if(w.getBlock(x,y,z)!=b){if(!w.setBlock(x,y,z,b,facing&3,3))return false;}else w.setBlockMetadataWithNotify(x,y,z,facing&3,2);DisplayTile t=DisplayTile.at(w,x,y,z);if(t==null)throw new IllegalStateException("Missing display state");t.set(count,hidden);return true;
    }
    public boolean support(IBlockAccess w,int x,int y,int z){
        if(!Settings.trays)return false;Block b=w.getBlock(x,y-1,z);
        if(b instanceof BlockDoor||b instanceof BlockStairs||b instanceof BlockFenceGate||b instanceof BlockLeaves||b==Blocks.farmland||b==Blocks.cauldron||b==Blocks.hopper||b==Blocks.flower_pot)return false;
        if(b instanceof BlockTrapDoor&&(w.getBlockMetadata(x,y-1,z)&4)!=0)return false;
        return b instanceof DisplayBlock||!b.isSideSolid(w,x,y-1,z,ForgeDirection.UP);
    }
    @Override public void onNeighborBlockChange(World w,int x,int y,int z,Block b){w.markBlockForUpdate(x,y,z);}
    public int count(IBlockAccess w,int x,int y,int z){DisplayTile t=DisplayTile.at(w,x,y,z);return t==null?1:Math.max(1,Math.min(definition.max,t.count));}
    public boolean hidden(IBlockAccess w,int x,int y,int z){DisplayTile t=DisplayTile.at(w,x,y,z);return t!=null&&t.hidden;}
    public ItemStack displayStack(int count,boolean hidden){ItemStack s=new ItemStack(this);NBTTagCompound n=new NBTTagCompound();n.setInteger("FoodCount",count);n.setBoolean("PlateHidden",hidden);s.setTagCompound(n);return s;}
    public ArrayList<ItemStack> drops(World w,int x,int y,int z,boolean silk){
        ArrayList<ItemStack> result=new ArrayList<ItemStack>();int count=count(w,x,y,z);boolean hidden=hidden(w,x,y,z);
        if(definition.empty())result.add(new ItemStack(this));
        else if(food==null)result.add(displayStack(count,hidden));
        else if(Settings.silk&&silk&&(!definition.plated()||!hidden&&count==definition.max))result.add(displayStack(count,hidden));
        else {ItemStack f=food.copy();f.stackSize=count;result.add(f);if(definition.plated()&&!hidden)result.add(new ItemStack(Catalog.plate(definition.small())));}
        return result;
    }
    @Override public ArrayList<ItemStack> getDrops(World w,int x,int y,int z,int meta,int fortune){return drops(w,x,y,z,false);}
    @Override public boolean removedByPlayer(World w,EntityPlayer p,int x,int y,int z,boolean harvest){return harvest||super.removedByPlayer(w,p,x,y,z,false);}
    @Override public void harvestBlock(World w,EntityPlayer p,int x,int y,int z,int meta){
        if(!w.isRemote){for(ItemStack stack:drops(w,x,y,z,EnchantmentHelper.getSilkTouchModifier(p)))dropBlockAsItem(w,x,y,z,stack);w.setBlockToAir(x,y,z);}
    }
    @Override public ItemStack getPickBlock(MovingObjectPosition hit,World w,int x,int y,int z,EntityPlayer p){return food==null?displayStack(count(w,x,y,z),hidden(w,x,y,z)):food.copy();}
    @Override @SideOnly(Side.CLIENT) public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register){blockIcon=register.registerIcon("displaydelight:tray_top");}
    @Override @SideOnly(Side.CLIENT) public void randomDisplayTick(World w,int x,int y,int z,Random r){
        if(definition.steam&&r.nextInt(4)==0)w.spawnParticle("smoke",x+.3+r.nextFloat()*.4,y+.65,z+.3+r.nextFloat()*.4,0,.015,0);
        if(definition.fiery&&r.nextInt(4)==0)w.spawnParticle("flame",x+.3+r.nextFloat()*.4,y+.3,z+.3+r.nextFloat()*.4,0,.01,0);
    }
}
