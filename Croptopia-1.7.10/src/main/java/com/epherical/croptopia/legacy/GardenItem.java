package com.epherical.croptopia.legacy;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
public final class GardenItem extends ItemBlock {public GardenItem(Block b){super(b);setHasSubtypes(true);}public int getMetadata(int damage){return damage&7;}}
