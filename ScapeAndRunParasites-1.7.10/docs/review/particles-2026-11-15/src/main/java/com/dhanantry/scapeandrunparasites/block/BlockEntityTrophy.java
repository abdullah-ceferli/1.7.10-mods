/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.ITileEntityProvider
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityTrophy;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class BlockEntityTrophy
extends Block
implements ITileEntityProvider {
    private final ResourceLocation mobId;
    private final boolean animate;
    private final TrophyTextureMode textureMode;
    private final boolean hasSounds;
    private final float yOffset;
    private final float scale;

    public BlockEntityTrophy(Material material, String registryName, String mobId, boolean animate, TrophyTextureMode textureMode, boolean hasSounds, float yOffset, float scale) {
        super(material);
        this.mobId = new ResourceLocation(mobId);
        this.animate = animate;
        this.textureMode = textureMode;
        this.hasSounds = hasSounds;
        this.yOffset = yOffset;
        this.scale = scale;
        RegistryNames.set(this, registryName);
        this.setBlockName("srparasites." + registryName);
        this.setHardness(2.0f);
        this.setResistance(10.0f);
        this.setStepSound(SoundType.STONE);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        SRPBlocks.SRP_BLOCKS.add(this);
        ItemBlock itemBlock = this instanceof IMetaName ? ((IMetaName)((Object)this)).getItemBlock() : new ItemBlock((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(itemBlock, Objects.requireNonNull(RegistryNames.get(this))));
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTrophy();
    }

    public ResourceLocation getMobId() {
        return this.mobId;
    }

    public boolean isAnimate() {
        return this.animate;
    }

    public TrophyTextureMode getTextureMode() {
        return this.textureMode;
    }

    public boolean hasSounds() {
        return this.hasSounds;
    }

    public float getYOffset() {
        return this.yOffset;
    }

    public float getScale() {
        return this.scale;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        if (!this.hasSounds) {
            return true;
        }
        Entity e = EntityList.createEntityByIDFromName((ResourceLocation)this.mobId, (World)worldIn);
        if (e instanceof EntityLiving) {
            EntityLiving living = (EntityLiving)e;
            living.playLivingSound();
        }
        return true;
    }

    public static enum TrophyTextureMode {
        DEFAULT,
        STONE;

    }
}

