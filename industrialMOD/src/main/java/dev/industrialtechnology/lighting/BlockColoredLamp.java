package dev.industrialtechnology.lighting;

import java.util.*;
import cpw.mods.fml.relauncher.*;
import dev.industrialtechnology.core.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;

/** Native full cubes; the powered block has no item and all colors share its metadata. */
public final class BlockColoredLamp extends Block {
    public static final int ALWAYS=0,OFF=1,ON=2;
    public final int mode;
    public BlockColoredLamp(int mode){super(Material.glass);this.mode=mode;setBlockName("industrialtech."+(mode==ALWAYS?"colored_lamp":"colored_redstone_lamp"));setHardness(.6F);setStepSound(soundTypeGlass);setLightLevel(lit()?1:0);setCreativeTab(mode==ON?null:ModRegistry.TAB);}
    public boolean lit(){return mode!=OFF;}
    public BlockColoredLamp itemBlock(){return mode==ALWAYS?ColoredLampRegistry.always:ColoredLampRegistry.redstone;}
    @Override public int getLightValue(IBlockAccess w,int x,int y,int z){return lit()?15:0;}
    @Override public boolean isSideSolid(IBlockAccess w,int x,int y,int z,ForgeDirection side){return side!=ForgeDirection.UNKNOWN;}
    @Override public boolean canPlaceTorchOnTop(World w,int x,int y,int z){return true;}
    @Override public MapColor getMapColor(int metadata){return Blocks.wool.getMapColor(metadata&15);}
    @Override public void onBlockAdded(World w,int x,int y,int z){refresh(w,x,y,z);}
    @Override public void onNeighborBlockChange(World w,int x,int y,int z,Block neighbor){refresh(w,x,y,z);}
    private void refresh(World w,int x,int y,int z){if(w.isRemote||mode==ALWAYS)return;boolean powered=w.isBlockIndirectlyGettingPowered(x,y,z);if(mode==OFF&&powered)w.setBlock(x,y,z,ColoredLampRegistry.powered,w.getBlockMetadata(x,y,z)&15,2);else if(mode==ON&&!powered)w.scheduleBlockUpdate(x,y,z,this,4);}
    @Override public void updateTick(World w,int x,int y,int z,Random random){if(!w.isRemote&&mode==ON&&w.getBlock(x,y,z)==this&&!w.isBlockIndirectlyGettingPowered(x,y,z))w.setBlock(x,y,z,ColoredLampRegistry.redstone,w.getBlockMetadata(x,y,z)&15,2);}
    @Override public Item getItemDropped(int metadata,Random random,int fortune){return Item.getItemFromBlock(itemBlock());}
    @Override public int damageDropped(int metadata){return metadata&15;}
    @Override protected ItemStack createStackedBlock(int metadata){return new ItemStack(itemBlock(),1,metadata&15);}
    @Override public ItemStack getPickBlock(MovingObjectPosition hit,World w,int x,int y,int z){return new ItemStack(itemBlock(),1,w.getBlockMetadata(x,y,z)&15);}
    @SideOnly(Side.CLIENT) @Override public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register){}
    @SideOnly(Side.CLIENT) @Override public IIcon getIcon(int side,int metadata){return Blocks.quartz_block.getIcon(1,0);}
    @SideOnly(Side.CLIENT) @Override public int getRenderColor(int metadata){return LampColors.color(metadata,lit());}
    @SideOnly(Side.CLIENT) @Override public int colorMultiplier(IBlockAccess w,int x,int y,int z){return getRenderColor(w.getBlockMetadata(x,y,z));}
    @SideOnly(Side.CLIENT) @Override @SuppressWarnings({"rawtypes","unchecked"}) public void getSubBlocks(Item item,CreativeTabs tab,List out){if(mode!=ON)for(int i=0;i<16;i++)out.add(new ItemStack(item,1,i));}
}
