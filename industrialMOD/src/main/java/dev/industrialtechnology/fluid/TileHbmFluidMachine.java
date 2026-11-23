package dev.industrialtechnology.fluid;

import api.hbm.fluidmk2.*;
import com.hbm.inventory.fluid.*;
import com.hbm.inventory.fluid.tank.FluidTank;
import net.minecraftforge.common.util.ForgeDirection;

/** Uses HBM's real network providers/receivers. Its native tank is also the saved buffer. */
public class TileHbmFluidMachine extends TileFluidMachine implements IFluidStandardSenderMK2,IFluidStandardReceiverMK2 {
    private static final int[] PRESSURE={0,0};
    private final FluidTank tank=new MachineTank();
    private final class MachineTank extends FluidTank {
        MachineTank(){super(Fluids.NONE,CAPACITY);}
        @Override public void setFill(int value){super.setFill(selected()==NONE?0:Math.max(0,Math.min(CAPACITY,value)));markDirty();}
        @Override public void setTankType(FluidType value){if(value==type())super.setTankType(value);}
        @Override public boolean setType(int slot,net.minecraft.item.ItemStack[] inventory){return false;}
        @Override public boolean setType(int slot,int other,net.minecraft.item.ItemStack[] inventory){return false;}
        @Override public FluidTank withPressure(int value){return this;}
        @Override public int changeTankSize(int value){return 0;}
        @Override public void resetTank(){setFill(0);}
    }
    @Override public int amount(){return tank.getFill();}
    @Override protected void amount(int value){tank.setFill(value);}
    @Override protected void selectionChanged(){tank.setTankType(type());}
    private final FluidTank[] tanks={tank};
    protected TileHbmFluidMachine(boolean sucker){super(sucker);}
    public static final class Sucker extends TileHbmFluidMachine {public Sucker(){super(true);}}
    public static final class Puller extends TileHbmFluidMachine {public Puller(){super(false);}}
    private FluidType type(){return selected()==WATER?Fluids.WATER:selected()==LAVA?Fluids.LAVA:Fluids.NONE;}
    private boolean accepts(FluidType fluid,int pressure){return pressure==0&&selected()!=NONE&&fluid==type()&&live();}
    @Override public boolean isLoaded(){return selected()!=NONE&&live();}
    @Override public boolean canConnect(FluidType fluid,ForgeDirection side){return side!=ForgeDirection.UNKNOWN&&side!=front()&&selected()!=NONE&&fluid==type();}
    @Override public FluidTank[] getAllTanks(){return tanks;}
    @Override public FluidTank[] getSendingTanks(){return isSucker()?getAllTanks():FluidTank.EMPTY_ARRAY;}
    @Override public FluidTank[] getReceivingTanks(){return isSucker()?FluidTank.EMPTY_ARRAY:getAllTanks();}
    @Override public long getFluidAvailable(FluidType fluid,int pressure){return isSucker()&&accepts(fluid,pressure)?amount():0;}
    @Override public void useUpFluid(FluidType fluid,int pressure,long amount){if(isSucker()&&accepts(fluid,pressure))consume(amount);}
    @Override public long getDemand(FluidType fluid,int pressure){return !isSucker()&&accepts(fluid,pressure)?CAPACITY-amount():0;}
    @Override public long transferFluid(FluidType fluid,int pressure,long amount){if(!accepts(fluid,pressure)||isSucker())return amount;return amount-accept(selected(),amount);}
    @Override public int[] getProvidingPressureRange(FluidType fluid){return PRESSURE;}
    @Override public int[] getReceivingPressureRange(FluidType fluid){return PRESSURE;}
    @Override public long getProviderSpeed(FluidType fluid,int pressure){return BUCKET;}
    @Override public long getReceiverSpeed(FluidType fluid,int pressure){return BUCKET;}
    @Override protected void networkStep(){
        if(isSucker()&&amount()==0||!isSucker()&&amount()==CAPACITY)return;
        for(ForgeDirection d:ForgeDirection.VALID_DIRECTIONS){if(d==front())continue;int x=xCoord+d.offsetX,y=yCoord+d.offsetY,z=zCoord+d.offsetZ;if(!loaded(worldObj,x,y,z))continue;
            if(isSucker())tryProvide(type(),0,worldObj,x,y,z,d);else trySubscribe(type(),worldObj,x,y,z,d);
        }
    }
}
