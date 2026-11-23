package dev.industrialtechnology.hatch.client;

import dev.industrialtechnology.hatch.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

/** Read-only texture context: Chisel sees the retained floor and its metadata. */
final class HatchFloorAccess implements IBlockAccess {
    private final IBlockAccess source;
    HatchFloorAccess(IBlockAccess source){this.source=source;}
    public Block getBlock(int x,int y,int z){TileSlidingHatch t=BlockSlidingHatch.tile(source,x,y,z);return t==null?source.getBlock(x,y,z):t.backing;}
    public int getBlockMetadata(int x,int y,int z){TileSlidingHatch t=BlockSlidingHatch.tile(source,x,y,z);return t==null?source.getBlockMetadata(x,y,z):t.backingMeta;}
    public TileEntity getTileEntity(int x,int y,int z){TileEntity t=source.getTileEntity(x,y,z);return t instanceof TileSlidingHatch?null:t;}
    public int getLightBrightnessForSkyBlocks(int x,int y,int z,int minimum){return source.getLightBrightnessForSkyBlocks(x,y,z,minimum);}
    public int isBlockProvidingPowerTo(int x,int y,int z,int side){return source.isBlockProvidingPowerTo(x,y,z,side);}
    public boolean isAirBlock(int x,int y,int z){return getBlock(x,y,z)==Blocks.air;}
    public BiomeGenBase getBiomeGenForCoords(int x,int z){return source.getBiomeGenForCoords(x,z);}
    public int getHeight(){return source.getHeight();}
    public boolean extendedLevelsInChunkCache(){return source.extendedLevelsInChunkCache();}
    public boolean isSideSolid(int x,int y,int z,ForgeDirection side,boolean fallback){return getBlock(x,y,z).isSideSolid(this,x,y,z,side);}
}
