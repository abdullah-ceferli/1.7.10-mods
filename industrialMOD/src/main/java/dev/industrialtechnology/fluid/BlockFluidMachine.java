package dev.industrialtechnology.fluid;

import cpw.mods.fml.relauncher.*;
import dev.industrialtechnology.core.ModRegistry;
import java.util.ArrayList;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.ForgeDirection;

public final class BlockFluidMachine extends BlockContainer {
    private final boolean sucker;
    public BlockFluidMachine(boolean sucker){super(Material.iron);this.sucker=sucker;setBlockName("industrialtech.fluid_"+(sucker?"sucker":"puller"));setHardness(4);setResistance(15);setStepSound(soundTypeMetal);setCreativeTab(ModRegistry.TAB);}
    @Override public TileEntity createNewTileEntity(World world,int meta){return FluidMachineRegistry.create(sucker);}
    @Override public void onBlockPlacedBy(World world,int x,int y,int z,EntityLivingBase entity,ItemStack stack){
        world.setBlockMetadataWithNotify(x,y,z,BlockPistonBase.determineOrientation(world,x,y,z,entity),2);
        TileEntity raw=world.getTileEntity(x,y,z);if(raw instanceof TileFluidMachine){TileFluidMachine tile=(TileFluidMachine)raw;if(stack.hasTagCompound()&&stack.getTagCompound().hasKey("FluidMachine"))tile.readContents(stack.getTagCompound().getCompoundTag("FluidMachine"));if(entity instanceof EntityPlayer)tile.owner((EntityPlayer)entity);tile.changed();}
    }
    @Override public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int side,float a,float b,float c){
        ItemStack held=player.getCurrentEquippedItem();if(!FluidMachineRegistry.identifier(held))return false;
        if(world.isRemote)return true;
        if(!player.canPlayerEdit(x,y,z,side,held))return true;
        TileEntity raw=world.getTileEntity(x,y,z);if(!(raw instanceof TileFluidMachine))return true;
        TileFluidMachine tile=(TileFluidMachine)raw;int kind=FluidMachineRegistry.identified(world,x,y,z,held);
        if(kind<0){player.addChatMessage(new ChatComponentTranslation("industrialtech.fluid.water_lava_only"));return true;}
        if(!tile.configure(kind)){player.addChatMessage(new ChatComponentTranslation("industrialtech.fluid.empty_first"));return true;}
        if(!tile.hasOwner())tile.owner(player);
        if(!tile.isSucker()){
            if(kind==TileFluidMachine.NONE)PullerSelection.cancel(player);
            else if(!PullerSelection.start(tile,player,side,held,kind))player.addChatMessage(new ChatComponentTranslation("industrialtech.fluid.selection_busy"));
        }
        player.addChatMessage(new ChatComponentTranslation("industrialtech.fluid.configured",new ChatComponentTranslation("industrialtech.fluid.kind."+kind)));return true;
    }
    @Override public boolean rotateBlock(World world,int x,int y,int z,ForgeDirection axis){
        if(axis==ForgeDirection.UNKNOWN)return false;if(world.isRemote)return true;
        if(!world.setBlockMetadataWithNotify(x,y,z,axis.ordinal(),3))return false;
        TileEntity raw=world.getTileEntity(x,y,z);if(raw instanceof TileFluidMachine)((TileFluidMachine)raw).resetSearch();return true;
    }
    @Override public ForgeDirection[] getValidRotations(World world,int x,int y,int z){return ForgeDirection.VALID_DIRECTIONS;}
    @Override public boolean removedByPlayer(World world,EntityPlayer player,int x,int y,int z,boolean harvest){return harvest||super.removedByPlayer(world,player,x,y,z,false);}
    @Override public void harvestBlock(World world,EntityPlayer player,int x,int y,int z,int meta){super.harvestBlock(world,player,x,y,z,meta);world.setBlockToAir(x,y,z);}
    @Override public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int meta,int fortune){
        ArrayList<ItemStack> drops=new ArrayList<ItemStack>();ItemStack stack=new ItemStack(this);TileEntity raw=world.getTileEntity(x,y,z);
        if(raw instanceof TileFluidMachine){net.minecraft.nbt.NBTTagCompound n=new net.minecraft.nbt.NBTTagCompound();n.setTag("FluidMachine",((TileFluidMachine)raw).contents());stack.setTagCompound(n);}drops.add(stack);return drops;
    }
    @SideOnly(Side.CLIENT) @Override public IIcon getIcon(int side,int meta){return side==meta?(sucker?Blocks.hopper.getIcon(2,0):Blocks.dispenser.getIcon(3,3)):Blocks.iron_block.getIcon(side,0);}
    @SideOnly(Side.CLIENT) @Override public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister register){blockIcon=register.registerIcon("minecraft:iron_block");}
}
