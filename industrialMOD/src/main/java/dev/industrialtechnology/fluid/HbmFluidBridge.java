package dev.industrialtechnology.fluid;

import com.hbm.inventory.fluid.*;
import com.hbm.items.ModItems;
import com.hbm.items.machine.IItemFluidIdentifier;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

final class HbmFluidBridge {
    static void register(){GameRegistry.registerTileEntity(TileHbmFluidMachine.Sucker.class,"industrialtech:fluid_sucker");GameRegistry.registerTileEntity(TileHbmFluidMachine.Puller.class,"industrialtech:fluid_puller");}
    static TileFluidMachine create(boolean sucker){return sucker?new TileHbmFluidMachine.Sucker():new TileHbmFluidMachine.Puller();}
    static boolean identifier(ItemStack s){return s!=null&&s.stackSize>0&&s.getItem()==ModItems.fluid_identifier_multi&&s.getItem() instanceof IItemFluidIdentifier;}
    static int identified(World w,int x,int y,int z,ItemStack s){if(!identifier(s))return -1;FluidType type=((IItemFluidIdentifier)s.getItem()).getType(w,x,y,z,s);return type==Fluids.WATER?TileFluidMachine.WATER:type==Fluids.LAVA?TileFluidMachine.LAVA:type==Fluids.NONE?TileFluidMachine.NONE:-1;}
    private HbmFluidBridge(){}
}
