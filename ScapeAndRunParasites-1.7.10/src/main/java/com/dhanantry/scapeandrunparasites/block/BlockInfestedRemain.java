/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumBlockRenderType
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPResidueFireManager;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyBool;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockInfestedRemain
extends BlockBush {
    protected static final AxisAlignedBB TALL_GRASS_AABB = Boxes.create(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
    public static final PropertyInteger SOURCE = PropertyInteger.create((String)"source", (int)0, (int)1);
    public static final PropertyBool INFESTED_BASE = PropertyBool.create((String)"infested_base");

    public BlockInfestedRemain(String name) {
        super(Material.vine);
        RegistryNames.set(this, name);
        this.setBlockName("srparasites." + name);
        this.setStepSound(SRPSoundTypes.VOMIT);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)SOURCE, (Comparable)Integer.valueOf(0)).withProperty((IProperty)INFESTED_BASE, (Comparable)Boolean.FALSE));
        SRPBlocks.SRP_BLOCKS.add((Block)this);
        SRPItems.SRP_ITEMS.add((Item)RegistryNames.set(new ItemBlock((Block)this), Objects.requireNonNull(RegistryNames.get(this))));
        this.setHarvestLevel("shovel", 0);
        this.slipperiness = 0.52f;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return TALL_GRASS_AABB;
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (BlockStates.meta(this, state) == 1) {
            return BlockStates.get(worldIn, pos.down()).isFullBlock();
        }
        return this.canSustainBush(BlockStates.get(worldIn, pos.down()));
    }

    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == SRPBlocks.optionalDirt || state.getBlock() == SRPBlocks.optionalRub;
    }

    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityWalk(worldIn, pos, entityIn);
        if (entityIn.onGround && entityIn instanceof EntityLivingBase && !entityIn.isSneaking()) {
            entityIn.motionX *= 0.84;
            entityIn.motionZ *= 0.84;
        }
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        boolean offIsShovel;
        EntityPlayer harvester = (EntityPlayer)this.harvesters.get();
        if (harvester == null) {
            return;
        }
        if (harvester.capabilities.isCreativeMode) {
            return;
        }
        ItemStack main = harvester.getHeldItemMainhand();
        ItemStack off = harvester.getHeldItemOffhand();
        boolean mainIsShovel = !main.isEmpty() && main.getItem().getToolClasses(main).contains("shovel");
        boolean bl = offIsShovel = !off.isEmpty() && off.getItem().getToolClasses(off).contains("shovel");
        if (mainIsShovel || offIsShovel) {
            drops.add((Object)new ItemStack(Item.getItemFromBlock((Block)this)));
        }
    }

    private void scheduleInclineNeighbors(World world, BlockPos pos) {
        if (world.isRemote) {
            return;
        }
        for (EnumFacing f : new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST}) {
            BlockPos pSame = pos.offset(f);
            BlockPos pUp = pSame.up();
            BlockPos pDown = pSame.down();
            if (BlockStates.get(world, pSame).getBlock() == this) {
                WorldAccess.scheduleUpdate(world, pSame, (Block)this, 1);
            }
            if (BlockStates.get(world, pSame).isFullCube() && BlockStates.get(world, pUp).getBlock() == this) {
                WorldAccess.scheduleUpdate(world, pUp, (Block)this, 1);
            }
            if (BlockStates.get(world, pDown).getBlock() != this) continue;
            WorldAccess.scheduleUpdate(world, pDown, (Block)this, 1);
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            if (SRPConfigWorld.residueFlammableWave && (BlockStates.get(worldIn, pos.north()).getBlock() == Blocks.fire || BlockStates.get(worldIn, pos.south()).getBlock() == Blocks.fire || BlockStates.get(worldIn, pos.east()).getBlock() == Blocks.fire || BlockStates.get(worldIn, pos.west()).getBlock() == Blocks.fire || BlockStates.get(worldIn, pos.up()).getBlock() == Blocks.fire || BlockStates.get(worldIn, pos.down()).getBlock() == Blocks.fire)) {
                BlockStates.set(worldIn, pos, BlockStates.defaultState(Blocks.fire), 3);
                SRPResidueFireManager.lightAndTrack(worldIn, pos, 6);
                this.scheduleInclineNeighbors(worldIn, pos);
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(worldIn, null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.BLOCKS, 0.7f, 1.1f + worldIn.rand.nextFloat() * 0.2f);
                if (worldIn instanceof WorldServer) {
                    com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)worldIn), EnumParticleTypes.SMOKE_NORMAL, (double)pos.getX() + 0.5, (double)pos.getY() + 0.6, (double)pos.getZ() + 0.5, 1, 0.0, 0.02, 0.0, 0.0, new int[0]);
                }
                return;
            }
            if (DifficultyAccess.difficulty(worldIn) == EnumDifficulty.PEACEFUL) {
                return;
            }
            if (!BlockStates.get(worldIn, pos.down()).isFullCube()) {
                BlockStates.set(worldIn, pos, BlockStates.defaultState(Blocks.air));
                return;
            }
            if (rand.nextDouble() < 0.5) {
                if (worldIn.getBiome(pos) instanceof BiomeParasiteBase) {
                    BlockParasiteSpreading.spreadBiomeBlockStain(worldIn, pos, rand);
                    return;
                }
                int heart = ParasiteEventWorld.canBiomeStillExist(worldIn, pos, true);
                if (heart > 0) {
                    BlockParasiteSpreading.SpreadBiome(worldIn, pos, heart, ParasiteEventWorld.canBiomeStillExistType(worldIn, pos, true));
                }
            }
            if (!SRPConfig.allowMobs) {
                return;
            }
            List serverList = EntityQueries.loaded(worldIn);
            int count = 0;
            for (int x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityPBeckon) || ++count <= SRPConfig.nexusVenkrolCap && !(this.getDistanceSq(pos, (Entity)serverList.get(x)) < (double)(SRPConfig.nexusVenkrolDis * SRPConfig.nexusVenkrolDis))) continue;
                return;
            }
            if (SRPConfigSystems.rsResidueY <= pos.getY()) {
                if (SRPConfigSystems.useEvolution) {
                    int chance = this.getEvoResidue(SRPSaveData.get(worldIn, 5).getEvolutionPhase(worldIn.provider.dimensionId));
                    if (chance <= 0) {
                        return;
                    }
                    if (rand.nextInt(chance) == 0) {
                        if (SRPConfigSystems.rsSkyResidue && !WorldAccess.canSeeSky(worldIn, new BlockPos((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5))) {
                            return;
                        }
                        if (ParasiteEventEntity.getRSchance(worldIn) > 0.0) {
                            this.spawnVenkorl(pos, worldIn);
                        }
                    }
                } else {
                    if (SRPConfigSystems.rsVenkrolChance <= 0) {
                        return;
                    }
                    if (rand.nextInt(SRPConfigSystems.rsVenkrolChance) == 0) {
                        if (SRPConfigSystems.rsSkyResidue && !WorldAccess.canSeeSky(worldIn, new BlockPos((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5))) {
                            return;
                        }
                        this.spawnVenkorl(pos, worldIn);
                    }
                }
            }
        }
    }

    private double getDistanceSq(BlockPos pos, Entity entityIn) {
        double d0 = (double)pos.getX() - entityIn.posX;
        double d1 = (double)pos.getY() - entityIn.posY;
        double d2 = (double)pos.getZ() - entityIn.posZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return SRPConfigWorld.residueFlammableWave;
    }

    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return SRPConfigWorld.residueFlammableWave ? 200 : 0;
    }

    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return SRPConfigWorld.residueFlammableWave ? 300 : 0;
    }

    private void spawnVenkorl(BlockPos pos, World worldIn) {
        EntityVenkrol entityOut = new EntityVenkrol(worldIn);
        entityOut.setPosition((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5);
        if (!entityOut.worldObj.getCollisionBoxes((Entity)entityOut, Boxes.entityBox(entityOut)).isEmpty()) {
            entityOut.setDead();
            return;
        }
        worldIn.spawnEntityInWorld((Entity)entityOut);
        if (SRPConfigSystems.rsSounds) {
            if (SRPConfigSystems.disloGrowlNoise) {
                if (SRPSaveData.get(entityOut.worldObj, 7).getCurrentCode(entityOut.worldObj.provider.dimensionId, 15) == 0) {
                    com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(entityOut, SRPSounds.VENKROLSI, 4.0f, 1.0f);
                }
            } else {
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(entityOut, SRPSounds.VENKROLSI, 4.0f, 1.0f);
            }
        }
    }

    private void igniteIfPossible(World world, BlockPos pos) {
        if (WorldAccess.isAirBlock(world, pos.up())) {
            BlockStates.set(world, pos.up(), BlockStates.defaultState(Blocks.fire), 3);
        }
    }

    private boolean anyNeighborIsFire(World world, BlockPos pos) {
        return BlockStates.get(world, pos.north()).getBlock() == Blocks.fire || BlockStates.get(world, pos.south()).getBlock() == Blocks.fire || BlockStates.get(world, pos.east()).getBlock() == Blocks.fire || BlockStates.get(world, pos.west()).getBlock() == Blocks.fire || BlockStates.get(world, pos.up()).getBlock() == Blocks.fire || BlockStates.get(world, pos.down()).getBlock() == Blocks.fire;
    }

    private void sizzle(World world, BlockPos pos) {
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(world, null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.BLOCKS, 0.7f, 1.1f + world.rand.nextFloat() * 0.2f);
        double x = (double)pos.getX() + 0.5;
        double y = (double)pos.getY() + 0.6;
        double z = (double)pos.getZ() + 0.5;
        if (world.isRemote) {
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(world, EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0, 0.02, 0.0, new int[0]);
        } else if (world instanceof WorldServer) {
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(((WorldServer)world), EnumParticleTypes.SMOKE_NORMAL, x, y, z, 1, 0.0, 0.02, 0.0, 0.0, new int[0]);
        }
    }

    private void igniteShort(World world, BlockPos pos) {
        int TTL = 5;
        SRPResidueFireManager.lightAndTrack(world, pos.up(), 5);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        super.neighborChanged(state, world, pos, neighborBlock, neighborPos);
        if (world.isRemote) {
            return;
        }
        if (!SRPConfigWorld.residueFlammableWave) {
            return;
        }
        if (this.anyNeighborIsFire(world, pos)) {
            WorldAccess.scheduleUpdate(world, pos, (Block)this, 1);
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        BlockPos up = pos.up();
        if (BlockStates.get(world, up).getBlock() == Blocks.fire) {
            WorldAccess.setBlockToAir(world, up);
        }
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (world.isRemote) {
            return;
        }
        if (!SRPConfigWorld.residueFlammableWave) {
            return;
        }
        if (this.anyNeighborIsFire(world, pos)) {
            WorldAccess.scheduleUpdate(world, pos, (Block)this, 1);
        }
    }

    private int getEvoResidue(byte in) {
        int q = -5;
        switch (in) {
            case 1: {
                q = SRPConfigSystems.phaseResidueOne;
                break;
            }
            case 2: {
                q = SRPConfigSystems.phaseResidueTwo;
                break;
            }
            case 3: {
                q = SRPConfigSystems.phaseResidueThree;
                break;
            }
            case 4: {
                q = SRPConfigSystems.phaseResidueFour;
                break;
            }
            case 5: {
                q = SRPConfigSystems.phaseResidueFive;
                break;
            }
            case 6: {
                q = SRPConfigSystems.phaseResidueSix;
                break;
            }
            case 7: {
                q = SRPConfigSystems.phaseResidueSeven;
                break;
            }
            case 8: {
                q = SRPConfigSystems.phaseResidueEight;
            }
        }
        return q;
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        boolean underIsInfestedNormal = false;
        IBlockState below = BlockStates.get(world, pos.down());
        Block b = below.getBlock();
        ResourceLocation rl = RegistryNames.get(b);
        if (rl != null) {
            String domain = rl.getResourceDomain();
            String path = rl.getResourcePath();
            boolean idMatch = "srparasites".equals(domain) && path.startsWith("infested");
            boolean normalGeom = b.getRenderType(below) == EnumBlockRenderType.MODEL && b.renderAsNormalBlock(below) && b.isOpaqueCube(below);
            underIsInfestedNormal = idMatch && normalGeom;
        }
        return state.withProperty((IProperty)INFESTED_BASE, (Comparable)Boolean.valueOf(underIsInfestedNormal));
    }

    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!worldIn.isRemote) {
            EntityLivingBase target;
            if (worldIn.rand.nextDouble() < 0.5 && entityIn.ticksExisted % 20 != 0) {
                return;
            }
            if (!(entityIn instanceof EntityParasiteBase) && entityIn instanceof EntityLivingBase && !(target = (EntityLivingBase)entityIn).isPotionActive(SRPPotions.COTH_E) && !target.isPotionActive(SRPPotions.EPEL_E)) {
                target.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
        }
        if (entityIn.onGround && entityIn instanceof EntityLivingBase && !entityIn.isSneaking()) {
            entityIn.motionX *= 0.86;
            entityIn.motionZ *= 0.86;
        }
        super.onEntityCollision(worldIn, pos, state, entityIn);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return false;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return false;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{SOURCE, INFESTED_BASE});
    }

    public int getMetaFromState(IBlockState state) {
        return (Integer)state.getValue((IProperty)SOURCE);
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this).withProperty((IProperty)SOURCE, (Comparable)Integer.valueOf(meta & 1));
    }
}

