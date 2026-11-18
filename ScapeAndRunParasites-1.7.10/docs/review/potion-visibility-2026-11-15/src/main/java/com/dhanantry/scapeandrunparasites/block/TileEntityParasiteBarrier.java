/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.play.server.SPacketUpdateTileEntity
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityParasiteBarrier
extends TileEntity
implements ITickable {
    private int centerChunkX;
    private int centerChunkZ;
    private int radiusChunks = 1;
    private final Map<Integer, Integer> insideTicks = new HashMap<Integer, Integer>();

    public void initCenterFromPos(BlockPos pos) {
        this.centerChunkX = pos.getX() >> 4;
        this.centerChunkZ = pos.getZ() >> 4;
        this.notifySync();
    }

    public int getRadiusChunks() {
        return this.radiusChunks;
    }

    public void setRadiusChunks(int r) {
        if ((r = Math.max(1, Math.min(10, r))) != this.radiusChunks) {
            this.radiusChunks = r;
            this.notifySync();
            this.scrubMobsAroundField();
        }
    }

    public int[] currentWorldBounds() {
        int r = this.radiusChunks;
        int minChunkX = this.centerChunkX - r;
        int maxChunkX = this.centerChunkX + r;
        int minChunkZ = this.centerChunkZ - r;
        int maxChunkZ = this.centerChunkZ + r;
        int minX = minChunkX * 16;
        int maxX = (maxChunkX + 1) * 16 - 1;
        int minZ = minChunkZ * 16;
        int maxZ = (maxChunkZ + 1) * 16 - 1;
        return new int[]{minX, maxX, minZ, maxZ};
    }

    private boolean isInside(int[] bb, Entity e) {
        double x = e.posX;
        double z = e.posZ;
        return x >= (double)bb[0] && x <= (double)bb[1] && z >= (double)bb[2] && z <= (double)bb[3];
    }

    private boolean isSRP(Entity e) {
        String n = e.getClass().getName();
        return n.startsWith("com.dhanantry.scapeandrunparasites") || n.contains(".srparasites.");
    }

    public void update() {
        if (this.worldObj == null) {
            return;
        }
        if (this.worldObj.isRemote) {
            this.spawnBorderParticlesClient();
            return;
        }
        if (this.worldObj.getTotalWorldTime() % 2L != 0L) {
            return;
        }
        int[] bb = this.currentWorldBounds();
        AxisAlignedBB search = Boxes.create((double)(bb[0] - 48), 0.0, (double)(bb[2] - 48), (double)(bb[1] + 48), (double)this.worldObj.getHeight(), (double)(bb[3] + 48));
        List allLiving = EntityQueries.entities(this.worldObj, EntityLiving.class, search);
        for (EntityLiving attacker : allLiving) {
            this.clearCrossFactionAggroIfTargetInside(attacker, bb);
        }
        List list = EntityQueries.entities(this.worldObj, Entity.class, search, this::isSRP);
        if (list.isEmpty()) {
            this.insideTicks.clear();
            return;
        }
        for (Entity e : list) {
            this.clearAggroIfTargetInside(e, bb);
            this.clearPathIfTouchesInside(e, bb);
            if (this.isInside(bb, e)) {
                this.pushOrTeleportOutside(bb, e);
                if (this.isInside(bb, e)) {
                    int t = this.insideTicks.getOrDefault(e.getEntityId(), 0) + 2;
                    if (t >= 100) {
                        this.pushOrTeleportOutside(bb, e);
                        this.pushOrTeleportOutside(bb, e);
                        t = 0;
                    }
                    this.insideTicks.put(e.getEntityId(), t);
                    continue;
                }
                this.insideTicks.remove(e.getEntityId());
                continue;
            }
            this.insideTicks.remove(e.getEntityId());
        }
    }

    public void scrubMobsAroundField() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        int[] bb = this.currentWorldBounds();
        AxisAlignedBB search = Boxes.create((double)(bb[0] - 48), 0.0, (double)(bb[2] - 48), (double)(bb[1] + 48), (double)this.worldObj.getHeight(), (double)(bb[3] + 48));
        List list = EntityQueries.entities(this.worldObj, Entity.class, search, this::isSRP);
        for (Entity e : list) {
            this.clearAggroIfTargetInside(e, bb);
            this.clearPathIfTouchesInside(e, bb);
        }
    }

    private void clearCrossFactionAggroIfTargetInside(EntityLiving attacker, int[] bb) {
        boolean targetInside;
        EntityLivingBase tgt = attacker.getAttackTarget();
        if (tgt == null) {
            tgt = attacker.getAITarget();
        }
        if (tgt == null) {
            return;
        }
        boolean bl = targetInside = tgt.posX >= (double)bb[0] && tgt.posX <= (double)bb[1] && tgt.posZ >= (double)bb[2] && tgt.posZ <= (double)bb[3];
        if (!targetInside) {
            return;
        }
        boolean attackerIsSRP = this.isSRP((Entity)attacker);
        boolean targetIsSRP = this.isSRP((Entity)tgt);
        if (attackerIsSRP || targetIsSRP) {
            attacker.setAttackTarget(null);
            attacker.setRevengeTarget(null);
            if (attacker.getNavigator() != null) {
                attacker.getNavigator().clearPathEntity();
            }
        }
    }

    private void clearAggroIfTargetInside(Entity e, int[] bb) {
        if (!(e instanceof EntityLiving)) {
            return;
        }
        EntityLiving el = (EntityLiving)e;
        EntityLivingBase tgt = el.getAttackTarget();
        if (tgt != null && this.isInside(bb, (Entity)tgt)) {
            el.setAttackTarget(null);
            el.setRevengeTarget(null);
            if (el.getNavigator() != null) {
                el.getNavigator().clearPathEntity();
            }
            return;
        }
        EntityLivingBase rev = el.getAITarget();
        if (rev != null && this.isInside(bb, (Entity)rev)) {
            el.setRevengeTarget(null);
            if (el.getNavigator() != null) {
                el.getNavigator().clearPathEntity();
            }
        }
    }

    private void clearPathIfTouchesInside(Entity e, int[] bb) {
        if (!(e instanceof EntityLiving)) {
            return;
        }
        EntityLiving el = (EntityLiving)e;
        if (el.getNavigator() == null) {
            return;
        }
        Path path = el.getNavigator().getPath();
        if (path == null) {
            return;
        }
        int n = path.getCurrentPathLength();
        for (int i = 0; i < n; ++i) {
            PathPoint p = path.getPathPointFromIndex(i);
            if (p == null || p.xCoord < bb[0] || p.xCoord > bb[1] || p.zCoord < bb[2] || p.zCoord > bb[3]) continue;
            el.getNavigator().clearPathEntity();
            if (el.getAttackTarget() != null && this.isInside(bb, (Entity)el.getAttackTarget())) {
                el.setAttackTarget(null);
                el.setRevengeTarget(null);
            }
            return;
        }
    }

    private void pushOrTeleportOutside(int[] bb, Entity e) {
        double x = e.posX;
        double z = e.posZ;
        double dxMin = Math.abs(x - (double)bb[0]);
        double dxMax = Math.abs((double)bb[1] - x);
        double dzMin = Math.abs(z - (double)bb[2]);
        double dzMax = Math.abs((double)bb[3] - z);
        int edge = 0;
        double best = dxMin;
        if (dxMax < best) {
            best = dxMax;
            edge = 1;
        }
        if (dzMin < best) {
            best = dzMin;
            edge = 2;
        }
        if (dzMax < best) {
            best = dzMax;
            edge = 3;
        }
        double OUT = 0.6;
        double tx = x;
        double tz = z;
        if (edge == 0) {
            tx = (double)bb[0] - 0.6;
        } else if (edge == 1) {
            tx = (double)bb[1] + 0.6;
        } else {
            tz = edge == 2 ? (double)bb[2] - 0.6 : (double)bb[3] + 0.6;
        }
        BlockPos top = WorldAccess.getTopSolidOrLiquidBlock(this.worldObj, new BlockPos(MathHelper.floor_double((double)tx), 0, MathHelper.floor_double((double)tz)));
        double ty = (double)top.getY() + 0.1;
        e.setPosition(tx, ty, tz);
        e.motionX *= 0.1;
        e.motionZ *= 0.1;
        e.motionY = Math.min(e.motionY, 0.0);
        e.fallDistance = 0.0f;
    }

    @SideOnly(value=Side.CLIENT)
    private void spawnBorderParticlesClient() {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        if (p == null) {
            return;
        }
        ItemStack mh = p.getHeldItemMainhand();
        ItemStack oh = p.getHeldItemOffhand();
        Predicate<ItemStack> isBarrierItem = st -> {
            if (st == null || st.isEmpty()) {
                return false;
            }
            ResourceLocation rl = RegistryNames.get(st.getItem());
            return rl != null && "srparasites".equals(rl.getResourceDomain()) && "parasite_barrier".equals(rl.getResourcePath());
        };
        if (!isBarrierItem.test(mh) && !isBarrierItem.test(oh)) {
            return;
        }
        int[] bb = this.currentWorldBounds();
        int minX = bb[0];
        int maxX = bb[1];
        int minZ = bb[2];
        int maxZ = bb[3];
        int R = 48;
        int STEP = 2;
        double y = MathHelper.clamp_double((double)p.posY, (double)2.0, (double)(this.worldObj.getHeight() - 2));
        int px = MathHelper.floor_double((double)p.posX);
        int pz = MathHelper.floor_double((double)p.posZ);
        int fromX = MathHelper.clamp_int((int)(px - 48), (int)minX, (int)maxX);
        int toX = MathHelper.clamp_int((int)(px + 48), (int)minX, (int)maxX);
        int fromZ = MathHelper.clamp_int((int)(pz - 48), (int)minZ, (int)maxZ);
        int toZ = MathHelper.clamp_int((int)(pz + 48), (int)minZ, (int)maxZ);
        double xA = (double)minX + 0.5;
        double xB = (double)maxX + 0.5;
        for (int z = fromZ; z <= toZ; z += 2) {
            double zz = (double)z + 0.5;
            this.worldObj.spawnParticle(EnumParticleTypes.BARRIER, xA, y, zz, 0.0, 0.0, 0.0, new int[0]);
            this.worldObj.spawnParticle(EnumParticleTypes.BARRIER, xB, y, zz, 0.0, 0.0, 0.0, new int[0]);
        }
        double zA = (double)minZ + 0.5;
        double zB = (double)maxZ + 0.5;
        for (int x = fromX; x <= toX; x += 2) {
            double xx = (double)x + 0.5;
            this.worldObj.spawnParticle(EnumParticleTypes.BARRIER, xx, y, zA, 0.0, 0.0, 0.0, new int[0]);
            this.worldObj.spawnParticle(EnumParticleTypes.BARRIER, xx, y, zB, 0.0, 0.0, 0.0, new int[0]);
        }
    }

    private void notifySync() {
        if (this.worldObj == null) {
            return;
        }
        IBlockState st = BlockStates.get(this.worldObj, this.pos);
        this.worldObj.notifyBlockUpdate(this.pos, st, st, 3);
        this.markDirty();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("centerChunkX", this.centerChunkX);
        nbt.setInteger("centerChunkZ", this.centerChunkZ);
        nbt.setInteger("radiusChunks", this.radiusChunks);
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.centerChunkX = nbt.getInteger("centerChunkX");
        this.centerChunkZ = nbt.getInteger("centerChunkZ");
        int r = nbt.getInteger("radiusChunks");
        this.radiusChunks = Math.max(1, Math.min(10, r == 0 ? 1 : r));
    }

    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    public void handleUpdateTag(NBTTagCompound nbt) {
        this.readFromNBT(nbt);
    }

    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
    }

    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.handleUpdateTag(pkt.getNbtCompound());
    }
}

