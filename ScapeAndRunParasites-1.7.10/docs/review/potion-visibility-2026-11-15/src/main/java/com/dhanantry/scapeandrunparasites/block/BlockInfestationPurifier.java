/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.common.network.NetworkRegistry$TargetPoint
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.PurifyMappings;
import com.dhanantry.scapeandrunparasites.block.TileEntityInfestationPurifier;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.MsgSpawnPureParticles;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.google.common.base.Predicate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3i;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class BlockInfestationPurifier
extends BlockBase {
    private static final int ENTITY_SEARCH_RADIUS = 200;
    private static final int CONNECT_MAX_NODES = 40000;
    private static final int CONNECT_MAX_DISTANCE = 200;
    private static final String MAPPING_FILE = "srparasites_purify_mappings.txt";
    private static final ResourceLocation[] BECKON_IDS = new ResourceLocation[]{new ResourceLocation("srparasites", "beckon_si"), new ResourceLocation("srparasites", "beckon_sii"), new ResourceLocation("srparasites", "beckon_siii"), new ResourceLocation("srparasites", "beckon_siv")};

    public BlockInfestationPurifier(String name) {
        super(Material.rock, name, 2.0f, true, false);
        this.setTickRandomly(false);
        this.setStepSound(SRPSoundTypes.PURIFIER);
    }

    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityInfestationPurifier();
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItem(hand);
        boolean hasDiamond = !held.isEmpty() && held.getItem() == Items.diamond;
        BlockPos below = pos.down();
        IBlockState belowState = BlockStates.get(world, below);
        PurifyMappings.ensureLoaded(world, MAPPING_FILE);
        if (!hasDiamond) {
            if (!world.isRemote) {
                player.addChatComponentMessage((IChatComponent)new ChatComponentTranslation("message.srparasites.purifier_use_diamond", new Object[0]), true);
            }
            return true;
        }
        if (!PurifyMappings.isSrp(belowState)) {
            if (!world.isRemote) {
                player.addChatComponentMessage((IChatComponent)new ChatComponentTranslation("message.srparasites.purifier_not_over_srp", new Object[0]), true);
            }
            return true;
        }
        if (world.isRemote) {
            return true;
        }
        int connectedCount = this.checkAndGlowConnectedBeckons(world, pos, below);
        if (connectedCount > 0) {
            player.addChatComponentMessage((IChatComponent)new ChatComponentTranslation("message.srparasites.purifier_entities_marked", new Object[]{connectedCount}), true);
            return true;
        }
        TileEntity te = WorldAccess.getTileEntity(world, pos);
        if (te instanceof TileEntityInfestationPurifier) {
            world.playSound(null, pos, SRPSounds.PURIFIER_USE, SoundCategory.BLOCKS, 0.9f, 1.0f);
            double cx = (double)pos.getX() + 0.5;
            double cy = (double)pos.getY() + 0.9;
            double cz = (double)pos.getZ() + 0.5;
            SRPNetwork.CHANNEL.sendToAllAround((IMessage)new MsgSpawnPureParticles(cx, cy, cz, 24, 1), new NetworkRegistry.TargetPoint(world.provider.dimensionId, cx, cy, cz, 64.0));
            this.spawnStartPulse(world, pos);
            ((TileEntityInfestationPurifier)te).startAt(below, player.getUniqueID());
            player.addChatComponentMessage((IChatComponent)new ChatComponentTranslation("message.srparasites.purifier_started", new Object[0]), true);
        }
        return true;
    }

    private int checkAndGlowConnectedBeckons(final World world, BlockPos pos, BlockPos startSrp) {
        AxisAlignedBB box = Boxes.create((double)(pos.getX() - 200), (double)(pos.getY() - 96), (double)(pos.getZ() - 200), (double)(pos.getX() + 200 + 1), (double)(pos.getY() + 96), (double)(pos.getZ() + 200 + 1));
        final HashMap<BlockPos, List<EntityLivingBase>> beckonsByFoot = new HashMap<BlockPos, List<EntityLivingBase>>();
        List _unused = EntityQueries.entities(world, Entity.class, box, (Predicate)new Predicate<Entity>(){

            public boolean apply(Entity e) {
                boolean isBeckon;
                if (!(e instanceof EntityLivingBase)) {
                    return false;
                }
                ResourceLocation id = EntityList.getKey((Entity)e);
                if (id == null) {
                    return false;
                }
                boolean bl = isBeckon = id.equals((Object)new ResourceLocation("srparasites", "beckon_si")) || id.equals((Object)new ResourceLocation("srparasites", "beckon_sii")) || id.equals((Object)new ResourceLocation("srparasites", "beckon_siii")) || id.equals((Object)new ResourceLocation("srparasites", "beckon_siv"));
                if (!isBeckon) {
                    return false;
                }
                BlockPos under = new BlockPos(MathHelper.floor_double((double)e.posX), MathHelper.floor_double((double)e.posY) - 1, MathHelper.floor_double((double)e.posZ));
                if (!PurifyMappings.isSrp(BlockStates.get(world, under))) {
                    return false;
                }
                beckonsByFoot.computeIfAbsent(under, k -> new ArrayList()).add((EntityLivingBase)e);
                return true;
            }
        });
        if (beckonsByFoot.isEmpty()) {
            return 0;
        }
        return this.bfsGlowConnected(world, startSrp, beckonsByFoot);
    }

    private int bfsGlowConnected(World world, BlockPos start, Map<BlockPos, List<EntityLivingBase>> beckonsByFoot) {
        ArrayDeque<BlockPos> q = new ArrayDeque<BlockPos>();
        HashSet<Long> seen = new HashSet<Long>();
        int maxDistSq = 40000;
        q.add(start);
        seen.add(start.toLong());
        HashSet<Long> targets = new HashSet<Long>();
        for (BlockPos bp : beckonsByFoot.keySet()) {
            targets.add(bp.toLong());
        }
        int nodes = 0;
        int connected = 0;
        while (!q.isEmpty() && nodes < 40000 && !targets.isEmpty()) {
            BlockPos cur = (BlockPos)q.poll();
            ++nodes;
            long curKey = cur.toLong();
            if (targets.contains(curKey)) {
                List<EntityLivingBase> here = beckonsByFoot.get(cur);
                if (here != null) {
                    for (EntityLivingBase elb : here) {
                        elb.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(MobEffects.GLOWING, 600, 0, false, true));
                    }
                    connected += here.size();
                }
                targets.remove(curKey);
            }
            for (EnumFacing f : EnumFacing.faceList) {
                BlockPos nxt = cur.offset(f);
                long k = nxt.toLong();
                if (!seen.add(k) || start.distanceSq((Vec3i)nxt) > (double)maxDistSq || !PurifyMappings.isSrp(BlockStates.get(world, nxt))) continue;
                q.add(nxt);
            }
        }
        return connected;
    }

    static IBlockState tryCopyCommonProps(IBlockState from, IBlockState to) {
        IBlockState out = to;
        for (IProperty propTo : to.getPropertyKeys()) {
            if (!from.getPropertyKeys().contains(propTo)) continue;
            try {
                IProperty raw = propTo;
                Comparable val = from.getValue(raw);
                if (val == null || !raw.getAllowedValues().contains(val)) continue;
                out = out.withProperty(raw, val);
            }
            catch (Exception exception) {}
        }
        return out;
    }

    private void spawnStartPulse(World world, BlockPos pos) {
        if (!(world instanceof WorldServer)) {
            return;
        }
        WorldServer ws = (WorldServer)world;
        double cx = (double)pos.getX() + 0.5;
        double cy = (double)pos.getY() + 0.8;
        double cz = (double)pos.getZ() + 0.5;
        for (int i = 0; i < 28; ++i) {
            double angle = Math.PI * 2 * (double)i / 28.0;
            double radius = 0.9;
            double x = cx + Math.cos(angle) * radius;
            double z = cz + Math.sin(angle) * radius;
            ws.spawnParticle(EnumParticleTypes.REDSTONE, x, cy, z, 0, 0.1, 0.35, 1.0, 1.0, new int[0]);
            ws.spawnParticle(EnumParticleTypes.PORTAL, x, cy + 0.05, z, 1, 0.0, 0.01, 0.0, 0.0, new int[0]);
        }
    }
}

