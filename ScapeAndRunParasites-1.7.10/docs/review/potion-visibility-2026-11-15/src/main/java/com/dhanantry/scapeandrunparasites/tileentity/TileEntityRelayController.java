/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.InventoryHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.nbt.NBTUtil
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.play.server.SPacketUpdateTileEntity
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.util.text.translation.I18n
 *  net.minecraft.world.World
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.items.CapabilityItemHandler
 *  net.minecraftforge.items.ItemStackHandler
 */
package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.item.ItemDislodgementReport;
import com.dhanantry.scapeandrunparasites.item.ItemModule;
import com.dhanantry.scapeandrunparasites.item.ItemPhaseReport;
import com.dhanantry.scapeandrunparasites.item.ItemVectorMapReport;
import com.dhanantry.scapeandrunparasites.network.SRPCommandEvolution;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityNodeRelay;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityRelayController
extends TileEntity
implements ITickable {
    public boolean formed = false;
    private long nextScanTick = 0L;
    private static final String NBT_NEXT_SCAN = "NextScanTick";
    private boolean scanPending = false;
    private int scanTicks = 0;
    private UUID scanPlayer = null;
    private ItemModule.Kind scanKind = null;
    private final List<BlockPos> childPositions = new ArrayList<BlockPos>();
    private boolean dismantling = false;
    private static final int SCAN_RADIUS = 8;
    private final ItemStackHandler itemHandler = new ItemStackHandler(1){

        public boolean isItemValid(int slot, ItemStack stack) {
            return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemModule;
        }

        protected void onContentsChanged(int slot) {
            TileEntityRelayController.this.markDirty();
        }
    };
    private boolean capsInvalidated = false;

    public void setChildPositions(List<BlockPos> positions) {
        this.childPositions.clear();
        if (positions != null) {
            this.childPositions.addAll(positions);
        }
        this.markDirty();
    }

    private int scannerCooldownTicks() {
        return SRPConfigSystems.getScannerCooldownTicks();
    }

    public boolean canScan() {
        return this.worldObj == null || this.worldObj.getTotalWorldTime() >= this.nextScanTick;
    }

    public int getCooldownRemainingTicks() {
        if (this.worldObj == null) {
            return 0;
        }
        long t = this.nextScanTick - this.worldObj.getTotalWorldTime();
        return t > 0L ? (int)t : 0;
    }

    public int getCooldownTotalTicks() {
        return this.scannerCooldownTicks();
    }

    public void startCooldown() {
        if (this.worldObj == null) {
            return;
        }
        this.nextScanTick = this.worldObj.getTotalWorldTime() + (long)this.scannerCooldownTicks();
        this.markDirty();
        if (!this.worldObj.isRemote) {
            IBlockState s = BlockStates.get(this.worldObj, this.getPos());
            this.worldObj.notifyBlockUpdate(this.getPos(), s, s, 3);
        }
    }

    public void addChild(BlockPos p) {
        if (p != null && !this.childPositions.contains(p)) {
            this.childPositions.add(p);
            this.markDirty();
        }
    }

    public ItemStackHandler getHandler() {
        return this.itemHandler;
    }

    public void dropContents() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        ItemStack stack = this.itemHandler.getStackInSlot(0);
        if (!stack.isEmpty()) {
            InventoryHelper.spawnItemStack((World)this.worldObj, (double)((double)this.getPos().getX() + 0.5), (double)((double)this.getPos().getY() + 0.5), (double)((double)this.getPos().getZ() + 0.5), (ItemStack)stack.copy());
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            this.markDirty();
        }
    }

    public void setFormed(boolean val) {
        if (this.formed == val) {
            return;
        }
        this.formed = val;
        this.markDirty();
        if (this.worldObj != null && !this.worldObj.isRemote) {
            IBlockState s = BlockStates.get(this.worldObj, this.pos);
            this.worldObj.notifyBlockUpdate(this.pos, s, s, 3);
            WorldAccess.markBlockRangeForRenderUpdate(this.worldObj, this.pos, this.pos);
        }
    }

    public void update() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        if (!this.scanPending) {
            return;
        }
        if (--this.scanTicks > 0) {
            return;
        }
        this.scanPending = false;
        EntityPlayerMP player = this.worldObj.getMinecraftServer().getPlayerList().getPlayerByUUID(this.scanPlayer);
        if (player == null) {
            this.markDirty();
            return;
        }
        if (SRPConfigSystems.relayScannerDebugGlow) {
            this.debugGlowParasites();
        }
        if (this.scanKind == ItemModule.Kind.VECTORS) {
            this.giveVectorPaper(player);
        } else if (this.scanKind == ItemModule.Kind.PHASE) {
            this.givePhasePaper(player);
        } else if (this.scanKind == ItemModule.Kind.DISLODGEMENT) {
            this.giveDislodgementPaper(player);
        } else {
            this.giveScanPaper(player, this.scanKind);
        }
        this.worldObj.playSound(null, this.pos, SRPSounds.RELAY_PAPER, SoundCategory.BLOCKS, 1.0f, 1.0f);
        this.markDirty();
    }

    private void debugGlowParasites() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        List all = this.worldObj.getEntities(EntityLivingBase.class, e -> true);
        for (EntityLivingBase e2 : all) {
            ResourceLocation key = EntityList.getKey((Entity)e2);
            if (key == null || !"srparasites".equals(key.getResourceDomain())) continue;
            e2.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(MobEffects.GLOWING, 200, 0, false, false));
        }
    }

    private void giveScanPaper(EntityPlayerMP player, ItemModule.Kind kind) {
        String ratio;
        String percent;
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        ScanRegistry.ModuleProfile profile = ScanRegistry.getProfileFor(kind);
        if (profile == null) {
            return;
        }
        List all = this.worldObj.getEntities(EntityLivingBase.class, e -> true);
        LinkedHashMap<ScanRegistry.Tier, Integer> tierCounts = new LinkedHashMap<ScanRegistry.Tier, Integer>();
        int parasitesTotal = 0;
        for (ScanRegistry.Tier tier : profile.tiers) {
            int c = 0;
            for (EntityLivingBase e2 : all) {
                if (!(e2 instanceof EntityLiving) || !tier.matches(e2)) continue;
                ++c;
            }
            tierCounts.put(tier, c);
            parasitesTotal += c;
        }
        int mobTotal = 0;
        for (EntityLivingBase e3 : all) {
            if (!(e3 instanceof EntityLiving)) continue;
            ++mobTotal;
        }
        int n = Math.max(0, mobTotal - parasitesTotal);
        String string = percent = mobTotal == 0 ? "0.0%" : String.format(Locale.ROOT, "%.1f%%", 100.0 * (double)parasitesTotal / (double)Math.max(1, mobTotal));
        if (parasitesTotal == 0 && n == 0) {
            ratio = "0:0";
        } else {
            int g = TileEntityRelayController.gcd(parasitesTotal, n);
            if (g == 0) {
                g = 1;
            }
            ratio = parasitesTotal / g + ":" + n / g;
        }
        int dim = this.worldObj.provider.dimensionId;
        ItemStack paper = new ItemStack(Items.paper);
        paper.setStackDisplayName(I18n.translateToLocal((String)"item.srparasites.scan_report.name"));
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(EnumChatFormatting.DARK_PURPLE.toString() + I18n.translateToLocal((String)"item.srparasites.scan_report.header"));
        lore.add(" ");
        lore.add(EnumChatFormatting.GRAY.toString() + I18n.translateToLocalFormatted((String)"item.srparasites.scan_report.dimension", (Object[])new Object[]{EnumChatFormatting.GOLD.toString() + dim + EnumChatFormatting.GRAY.toString()}));
        lore.add(EnumChatFormatting.GRAY.toString() + I18n.translateToLocalFormatted((String)"item.srparasites.scan_report.total_mobs", (Object[])new Object[]{EnumChatFormatting.WHITE.toString() + mobTotal + EnumChatFormatting.GRAY.toString()}));
        lore.add(EnumChatFormatting.GRAY.toString() + I18n.translateToLocalFormatted((String)"item.srparasites.scan_report.total_parasites", (Object[])new Object[]{EnumChatFormatting.RED.toString() + parasitesTotal + EnumChatFormatting.GRAY.toString()}));
        lore.add(EnumChatFormatting.GRAY.toString() + I18n.translateToLocalFormatted((String)"item.srparasites.scan_report.percent", (Object[])new Object[]{EnumChatFormatting.YELLOW.toString() + percent + EnumChatFormatting.GRAY.toString()}));
        lore.add(EnumChatFormatting.GRAY.toString() + I18n.translateToLocalFormatted((String)"item.srparasites.scan_report.ratio", (Object[])new Object[]{EnumChatFormatting.AQUA.toString() + ratio + EnumChatFormatting.GRAY.toString()}));
        lore.add(" ");
        lore.add(EnumChatFormatting.DARK_GRAY.toString() + I18n.translateToLocal((String)"item.srparasites.scan_report.tiers"));
        for (Map.Entry e4 : tierCounts.entrySet()) {
            ScanRegistry.Tier t = (ScanRegistry.Tier)e4.getKey();
            int c = (Integer)e4.getValue();
            String tierId = t.id == null ? "" : t.id.toLowerCase(Locale.ROOT);
            EnumChatFormatting color = EnumChatFormatting.WHITE;
            switch (tierId) {
                case "inborn": {
                    color = EnumChatFormatting.GREEN;
                    break;
                }
                case "assimilated": {
                    color = EnumChatFormatting.AQUA;
                    break;
                }
                case "assimara": {
                    color = EnumChatFormatting.DARK_AQUA;
                    break;
                }
                case "hijacked": {
                    color = EnumChatFormatting.DARK_RED;
                    break;
                }
                case "feral": {
                    color = EnumChatFormatting.RED;
                    break;
                }
                case "crude": {
                    color = EnumChatFormatting.DARK_GRAY;
                    break;
                }
                case "primitive": {
                    color = EnumChatFormatting.GRAY;
                    break;
                }
                case "adapted": {
                    color = EnumChatFormatting.GOLD;
                    break;
                }
                case "nexus": {
                    color = EnumChatFormatting.LIGHT_PURPLE;
                    break;
                }
                case "deterrent": {
                    color = EnumChatFormatting.DARK_PURPLE;
                    break;
                }
                case "pure": {
                    color = EnumChatFormatting.BLUE;
                    break;
                }
                case "preeminent": {
                    color = EnumChatFormatting.DARK_GREEN;
                    break;
                }
                case "ancient": {
                    color = EnumChatFormatting.DARK_PURPLE;
                    break;
                }
                case "derived": {
                    color = EnumChatFormatting.DARK_BLUE;
                    break;
                }
                default: {
                    color = EnumChatFormatting.WHITE;
                }
            }
            String tierDisplay = t.getDisplayName();
            String line = color.toString() + tierDisplay + EnumChatFormatting.GRAY.toString() + " : " + EnumChatFormatting.WHITE.toString() + c;
            lore.add(line);
        }
        NBTTagCompound tag = paper.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        NBTTagCompound display = tag.getCompoundTag("display");
        NBTTagList loreTag = new NBTTagList();
        for (String line : lore) {
            loreTag.appendTag((NBTBase)new NBTTagString(line));
        }
        display.setTag("Lore", (NBTBase)loreTag);
        tag.setTag("display", (NBTBase)display);
        paper.setTagCompound(tag);
        if (!player.inventory.addItemStackToInventory(paper)) {
            player.dropPlayerItemWithRandomChoice(paper, false);
        }
    }

    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast((Object)this.itemHandler);
        }
        return (T)super.getCapability(capability, facing);
    }

    public void invalidate() {
        super.invalidate();
        this.capsInvalidated = true;
    }

    public void onChunkUnload() {
        super.onChunkUnload();
        this.capsInvalidated = true;
    }

    public void validate() {
        super.validate();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("Formed", this.formed);
        nbt.setLong(NBT_NEXT_SCAN, this.nextScanTick);
        NBTTagList list = new NBTTagList();
        for (BlockPos p : this.childPositions) {
            if (p == null) continue;
            list.appendTag((NBTBase)NBTUtil.createPosTag((BlockPos)p));
        }
        nbt.setTag("Children", (NBTBase)list);
        nbt.setTag("Inv", (NBTBase)this.itemHandler.serializeNBT());
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.nextScanTick = nbt.getLong(NBT_NEXT_SCAN);
        this.formed = nbt.getBoolean("Formed");
        this.childPositions.clear();
        if (nbt.hasKey("Children", 9)) {
            NBTTagList list = nbt.getTagList("Children", 10);
            for (int i = 0; i < list.tagCount(); ++i) {
                this.childPositions.add(NBTUtil.getPosFromTag((NBTTagCompound)list.getCompoundTagAt(i)));
            }
        }
        if (nbt.hasKey("Inv", 10)) {
            this.itemHandler.deserializeNBT(nbt.getCompoundTag("Inv"));
        }
    }

    public void onLoad() {
        if (this.worldObj == null) {
            return;
        }
        if (!this.worldObj.isRemote) {
            this.relinkChildrenToMe();
            if (this.nextScanTick < 0L) {
                this.nextScanTick = 0L;
                this.markDirty();
            }
            IBlockState s = BlockStates.get(this.worldObj, this.pos);
            this.worldObj.notifyBlockUpdate(this.pos, s, s, 3);
        }
    }

    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new SPacketUpdateTileEntity(this.getPos(), 0, tag);
    }

    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
        if (this.worldObj != null) {
            WorldAccess.markBlockRangeForRenderUpdate(this.worldObj, this.pos, this.pos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dismantle() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        if (this.dismantling) {
            return;
        }
        this.dismantling = true;
        try {
            List<BlockPos> targets = new ArrayList<BlockPos>(this.childPositions);
            if (targets.isEmpty()) {
                targets = this.findChildrenByScan();
            }
            this.formed = false;
            this.dropContents();
            for (BlockPos p : targets) {
                if (p == null || !WorldAccess.isBlockLoaded(this.worldObj, p) || BlockStates.get(this.worldObj, p).getBlock() != SRPBlocks.NODE_RELAY) continue;
                WorldAccess.setBlockToAir(this.worldObj, p);
            }
            this.childPositions.clear();
            this.markDirty();
            if (WorldAccess.isBlockLoaded(this.worldObj, this.getPos()) && BlockStates.get(this.worldObj, this.getPos()).getBlock() == SRPBlocks.RELAY_CONTROLLER) {
                WorldAccess.destroyBlock(this.worldObj, this.getPos(), true);
            }
        }
        finally {
            this.dismantling = false;
        }
    }

    private void relinkChildrenToMe() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        for (BlockPos p : this.childPositions) {
            TileEntity te;
            if (p == null || !WorldAccess.isBlockLoaded(this.worldObj, p) || !((te = WorldAccess.getTileEntity(this.worldObj, p)) instanceof TileEntityNodeRelay)) continue;
            ((TileEntityNodeRelay)te).setControllerPos(this.getPos());
        }
    }

    private List<BlockPos> findChildrenByScan() {
        ArrayList<BlockPos> found = new ArrayList<BlockPos>();
        if (this.worldObj == null) {
            return found;
        }
        BlockPos origin = this.getPos();
        for (int dx = -8; dx <= 8; ++dx) {
            for (int dy = -8; dy <= 8; ++dy) {
                for (int dz = -8; dz <= 8; ++dz) {
                    BlockPos p = origin.add(dx, dy, dz);
                    if (p.equals((Object)origin) || !WorldAccess.isBlockLoaded(this.worldObj, p) || BlockStates.get(this.worldObj, p).getBlock() != SRPBlocks.NODE_RELAY) continue;
                    found.add(p);
                }
            }
        }
        return found;
    }

    public boolean performScan(EntityPlayerMP player) {
        ScanRegistry.ModuleProfile profile;
        if (this.worldObj == null || this.worldObj.isRemote) {
            return false;
        }
        if (!this.formed) {
            player.addChatMessage((IChatComponent)new ChatComponentTranslation("chat.srparasites.relay.not_formed", new Object[0]));
            return false;
        }
        ItemStack stack = this.itemHandler.getStackInSlot(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemModule)) {
            player.addChatMessage((IChatComponent)new ChatComponentTranslation("chat.srparasites.relay.insert_module", new Object[0]));
            return false;
        }
        ItemModule.Kind kind = ((ItemModule)stack.getItem()).getKind(stack);
        if (kind == ItemModule.Kind.VECTORS) {
            if (!SRPConfigWorld.originActivated) {
                player.addChatMessage((IChatComponent)new ChatComponentTranslation("chat.srparasites.vectors.not_activated", new Object[0]));
                return false;
            }
        } else if (kind != ItemModule.Kind.PHASE && kind != ItemModule.Kind.DISLODGEMENT && ((profile = ScanRegistry.getProfileFor(kind)) == null || profile.tiers.isEmpty())) {
            player.addChatMessage((IChatComponent)new ChatComponentTranslation("chat.srparasites.relay.no_profile", new Object[]{kind.name()}));
            return false;
        }
        if (this.scanPending) {
            player.addChatMessage((IChatComponent)new ChatComponentTranslation("chat.srparasites.relay.scan_busy", new Object[0]));
            return false;
        }
        this.scanPending = true;
        this.scanTicks = 110;
        this.scanPlayer = player.getUniqueID();
        this.scanKind = kind;
        this.worldObj.playSound(null, this.pos, SRPSounds.RELAY_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        player.addChatMessage((IChatComponent)new ChatComponentTranslation("chat.srparasites.relay.scan_started", new Object[0]));
        this.startCooldown();
        this.markDirty();
        return true;
    }

    private void giveVectorPaper(EntityPlayerMP player) {
        int total;
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        if (!SRPConfigWorld.originActivated) {
            player.addChatMessage((IChatComponent)new ChatComponentTranslation("chat.srparasites.vectors.disabled", new Object[0]));
            return;
        }
        SRPWorldData data = SRPWorldData.get(this.worldObj);
        if (data == null) {
            return;
        }
        ArrayList<Integer> xs = data.getorigins("x");
        ArrayList<Integer> ys = data.getorigins("y");
        ArrayList<Integer> zs = data.getorigins("z");
        ArrayList<Integer> rs = data.getorigins("a");
        ArrayList<Integer> hs = data.getorigins("h");
        int n = Integer.MAX_VALUE;
        if (xs != null) {
            n = Math.min(n, xs.size());
        }
        if (ys != null) {
            n = Math.min(n, ys.size());
        }
        if (zs != null) {
            n = Math.min(n, zs.size());
        }
        if (rs != null) {
            n = Math.min(n, rs.size());
        }
        if (hs != null) {
            n = Math.min(n, hs.size());
        }
        if (n == Integer.MAX_VALUE) {
            n = 0;
        }
        int day = (int)(this.worldObj.getWorldTime() / 24000L);
        int cx = EntityQueries.position(player).getX();
        int cz = EntityQueries.position(player).getZ();
        int n2 = total = n <= 0 ? 1 : n;
        if (n <= 0) {
            ItemStack mapPaper = new ItemStack(SRPItems.VECTOR_MAP);
            NBTTagCompound t = ItemVectorMapReport.getOrCreate(mapPaper);
            int printDay = (int)(this.worldObj.getWorldTime() / 24000L);
            int printTime = (int)(this.worldObj.getWorldTime() % 24000L);
            t.setInteger("PrintDay", printDay);
            t.setInteger("PrintTime", printTime);
            t.setInteger("CenterX", cx);
            t.setInteger("CenterZ", cz);
            t.setInteger("VectorX", cx);
            t.setInteger("VectorZ", cz);
            t.setInteger("Radius", 0);
            t.setInteger("Day", day);
            t.setInteger("Index", 1);
            t.setInteger("Total", total);
            if (!player.inventory.addItemStackToInventory(mapPaper)) {
                player.dropPlayerItemWithRandomChoice(mapPaper, false);
            }
        } else {
            for (int i = 0; i < n; ++i) {
                ItemStack mapPaper = new ItemStack(SRPItems.VECTOR_MAP);
                NBTTagCompound t = ItemVectorMapReport.getOrCreate(mapPaper);
                int printDay = (int)(this.worldObj.getWorldTime() / 24000L);
                int printTime = (int)(this.worldObj.getWorldTime() % 24000L);
                t.setInteger("PrintDay", printDay);
                t.setInteger("PrintTime", printTime);
                t.setInteger("CenterX", cx);
                t.setInteger("CenterZ", cz);
                t.setInteger("VectorX", xs.get(i).intValue());
                t.setInteger("VectorZ", zs.get(i).intValue());
                t.setInteger("Radius", rs.get(i).intValue());
                t.setInteger("Day", day);
                t.setInteger("Index", i + 1);
                t.setInteger("Total", total);
                if (player.inventory.addItemStackToInventory(mapPaper)) continue;
                player.dropPlayerItemWithRandomChoice(mapPaper, false);
            }
        }
    }

    private void givePhasePaper(EntityPlayerMP player) {
        Entity ent;
        int i;
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        List serverList = EntityQueries.loaded(this.worldObj);
        int parasiteCount = 0;
        int cothCount = 0;
        int totalMobCount = 0;
        for (i = 0; i < serverList.size(); ++i) {
            ent = (Entity)serverList.get(i);
            if (ent instanceof EntityLivingBase && !(ent instanceof EntityPlayer)) {
                ++totalMobCount;
            }
            if (!(ent instanceof EntityParasiteBase)) continue;
            if (ent instanceof EntityCanHaveBodies) {
                EntityCanHaveBodies bodies = (EntityCanHaveBodies)ent;
                if (bodies.getBodyNumber() != 0) continue;
                ++parasiteCount;
                continue;
            }
            ++parasiteCount;
        }
        for (i = 0; i < serverList.size(); ++i) {
            ent = (Entity)serverList.get(i);
            if (!(ent instanceof EntityLivingBase) || ent instanceof EntityParasiteBase || !((EntityLivingBase)ent).isPotionActive(SRPPotions.COTH_E)) continue;
            ++cothCount;
        }
        int dim = this.worldObj.provider.dimensionId;
        SRPSaveData data = SRPSaveData.get(this.worldObj, 59);
        int players = EntityQueries.players(this.worldObj).size();
        players *= SRPConfig.worldMobCapPlusPlayer;
        byte phase = data.getEvolutionPhase(dim);
        int totalPoints = data.getTotalKills(dim);
        int nextPoints = SRPCommandEvolution.getNeededPoints((byte)(phase + 1));
        double progress = nextPoints <= 0 ? 0.0 : (double)totalPoints / (double)nextPoints * 100.0;
        String progStr = String.format(Locale.ROOT, "%.1f", progress);
        ItemStack report = new ItemStack(SRPItems.phase_report);
        NBTTagCompound tag = ItemPhaseReport.getOrCreate(report);
        long worldTime = this.worldObj.getWorldTime();
        int day = (int)(worldTime / 24000L);
        int time = (int)(worldTime % 24000L);
        tag.setInteger("PrintDay", day);
        tag.setInteger("PrintTime", time);
        tag.setInteger("PhaseDimension", dim);
        tag.setInteger("PhaseValue", (int)phase);
        tag.setInteger("PhaseTotalPoints", totalPoints);
        tag.setInteger("PhasePointsNext", nextPoints);
        tag.setString("PhaseProgress", progStr);
        tag.setInteger("PhaseCooldown", data.getCooldown(this.worldObj, dim));
        tag.setBoolean("PhaseCanGain", data.getCanGain(dim));
        tag.setBoolean("PhaseCanLoss", data.getCanLoss(dim));
        tag.setInteger("PhaseMobcap", SRPConfig.worldMobCap + players);
        tag.setInteger("PhaseGeneration", (int)data.getGeneration(dim));
        tag.setInteger("PhaseGenTicks", data.getGenerationNeededTime(this.worldObj, dim));
        tag.setInteger("PhaseParasiteCount", parasiteCount);
        tag.setInteger("PhaseCothCount", cothCount);
        tag.setInteger("PhaseTotalMobs", totalMobCount);
        report.setTagCompound(tag);
        if (!player.inventory.addItemStackToInventory(report)) {
            player.dropPlayerItemWithRandomChoice(report, false);
        }
    }

    private static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a == 0 ? 1 : a;
    }

    private void giveDislodgementPaper(EntityPlayerMP player) {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        int dim = this.worldObj.provider.dimensionId;
        SRPSaveData data = SRPSaveData.get(this.worldObj, 59);
        String raw = data.getCurrentCodeU(dim);
        String code = "";
        if (raw != null && !raw.trim().isEmpty()) {
            String[] vals = raw.split(";");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < vals.length; ++i) {
                int v = 0;
                try {
                    v = Integer.parseInt(vals[i].trim());
                }
                catch (Throwable t) {
                    v = 0;
                }
                if (v == 0) continue;
                int timeSec = data.getCurrentCodeDuration(dim, i);
                if (sb.length() > 0) {
                    sb.append(";");
                }
                sb.append(dim).append(";").append(i).append(";").append(v).append(";").append(timeSec);
            }
            code = sb.toString();
        }
        ItemStack report = new ItemStack(SRPItems.DISLODGEMENT_REPORT);
        NBTTagCompound t = ItemDislodgementReport.getOrCreate(report);
        t.setString("DislodgementCode", code);
        int day = (int)(this.worldObj.getWorldTime() / 24000L);
        int time = (int)(this.worldObj.getWorldTime() % 24000L);
        t.setInteger("PrintDay", day);
        t.setInteger("PrintTime", time);
        if (!player.inventory.addItemStackToInventory(report)) {
            player.dropPlayerItemWithRandomChoice(report, false);
        }
    }

    public static final class ScanRegistry {
        private static boolean INIT = false;
        private static final Map<String, Tier> TIERS = new LinkedHashMap<String, Tier>();
        private static final Map<ItemModule.Kind, ModuleProfile> MODULES = new EnumMap<ItemModule.Kind, ModuleProfile>(ItemModule.Kind.class);

        static void ensureInit() {
            if (INIT) {
                return;
            }
            INIT = true;
            Tier INBORN = ScanRegistry.tier("INBORN");
            Tier ASSIMILATED = ScanRegistry.tier("ASSIMILATED");
            Tier HIJACKED = ScanRegistry.tier("HIJACKED");
            Tier FERAL = ScanRegistry.tier("FERAL");
            Tier CRUDE = ScanRegistry.tier("CRUDE");
            Tier PRIMITIVE = ScanRegistry.tier("PRIMITIVE");
            Tier ADAPTED = ScanRegistry.tier("ADAPTED");
            Tier NEXUS = ScanRegistry.tier("NEXUS");
            Tier DETERRENT = ScanRegistry.tier("DETERRENT");
            Tier PURE = ScanRegistry.tier("PURE");
            Tier PREEMINENT = ScanRegistry.tier("PREEMINENT");
            Tier ANCIENT = ScanRegistry.tier("ANCIENT");
            Tier ASSIMARA = ScanRegistry.tier("ASSIMARA");
            Tier DERIVED = ScanRegistry.tier("DERIVED");
            String MOD = "srparasites";
            Function<String, ResourceLocation> rl = path -> new ResourceLocation("srparasites", path);
            ASSIMILATED.addIds(rl.apply("sim_bigspider"), rl.apply("sim_squid"), rl.apply("sim_human"), rl.apply("sim_cow"), rl.apply("sim_sheep"), rl.apply("sim_wolf"), rl.apply("sim_pig"), rl.apply("sim_villager"), rl.apply("sim_adventurer"), rl.apply("sim_horse"), rl.apply("sim_bear"), rl.apply("sim_enderman"), rl.apply("sim_dragone"), rl.apply("sim_sheephead"), rl.apply("sim_wolfhead"), rl.apply("sim_cowhead"), rl.apply("sim_pighead"), rl.apply("sim_villagerhead"), rl.apply("sim_horsehead"), rl.apply("sim_humanhead"), rl.apply("sim_endermanhead"), rl.apply("sim_dragonehead"), rl.apply("sim_adventurerhead"));
            ASSIMARA.addIds(rl.apply("mar_enderman"), rl.apply("mar_cow"), rl.apply("mar_villager"), rl.apply("mar_human"), rl.apply("mar_sheep"), rl.apply("mar_bear"));
            DERIVED.addIds(rl.apply("draconite"), rl.apply("kirin"));
            FERAL.addIds(rl.apply("fer_bear"), rl.apply("fer_cow"), rl.apply("fer_enderman"), rl.apply("fer_horse"), rl.apply("fer_human"), rl.apply("fer_pig"), rl.apply("fer_sheep"), rl.apply("fer_villager"), rl.apply("fer_wolf"));
            HIJACKED.addIds(rl.apply("hi_blaze"), rl.apply("hi_golem"), rl.apply("hi_skeleton"));
            INBORN.addIds(rl.apply("carrier_heavy"), rl.apply("carrier_light"), rl.apply("buglin"), rl.apply("carrier_flying"), rl.apply("rupter"), rl.apply("movingflesh"), rl.apply("worker"), rl.apply("mangler"), rl.apply("gnat"), rl.apply("lice"));
            DETERRENT.addIds(rl.apply("kyphosis"), rl.apply("sentry"), rl.apply("seizer"), rl.apply("worm"));
            NEXUS.addIds(rl.apply("beckon_si"), rl.apply("beckon_sii"), rl.apply("beckon_siii"), rl.apply("beckon_siv"), rl.apply("dispatcherten"), rl.apply("dispatcher_si"), rl.apply("dispatcher_sii"), rl.apply("dispatcher_siii"), rl.apply("dispatcher_siv"), rl.apply("rooterball"), rl.apply("rooter_si"), rl.apply("rooter_sii"), rl.apply("rooter_siii"), rl.apply("rooter_siv"));
            CRUDE.addIds(rl.apply("incompleteform_small"), rl.apply("incompleteform_medium"), rl.apply("host"), rl.apply("hostii"), rl.apply("heed"), rl.apply("crux"), rl.apply("crux_incomplete"), rl.apply("thrall"), rl.apply("dredge"), rl.apply("airscrew"), rl.apply("carrier_worm"));
            PRIMITIVE.addIds(rl.apply("pri_longarms"), rl.apply("pri_manducater"), rl.apply("pri_reeker"), rl.apply("pri_yelloweye"), rl.apply("pri_summoner"), rl.apply("pri_bolster"), rl.apply("pri_tozoon"), rl.apply("pri_arachnida"), rl.apply("pri_devourer"), rl.apply("pri_vermin"), rl.apply("pri_viscera"), rl.apply("pri_burrower"));
            ADAPTED.addIds(rl.apply("ada_longarms"), rl.apply("ada_manducater"), rl.apply("ada_reeker"), rl.apply("ada_yelloweye"), rl.apply("ada_summoner"), rl.apply("ada_bolster"), rl.apply("ada_tozoon"), rl.apply("ada_arachnida"), rl.apply("ada_devourer"), rl.apply("ada_vermin"), rl.apply("ada_viscera"), rl.apply("ada_burrower"));
            PURE.addIds(rl.apply("overseer"), rl.apply("vigilante"), rl.apply("warden"), rl.apply("bomber_light"), rl.apply("marauder"), rl.apply("monarch"), rl.apply("grunt"));
            PREEMINENT.addIds(rl.apply("bomber_heavy"), rl.apply("wraith"), rl.apply("bogle"), rl.apply("haunter"), rl.apply("carrier_colony"), rl.apply("succor"), rl.apply("seeker"), rl.apply("architect"));
            ANCIENT.addIds(rl.apply("anc_dreadnaut"), rl.apply("anc_overlord"), rl.apply("anc_pod"), rl.apply("anc_dreadnaut_ten"));
            ScanRegistry.map(ItemModule.Kind.INBORN, ScanRegistry.profile("Inborn").add(INBORN));
            ScanRegistry.map(ItemModule.Kind.ASSIMILATED, ScanRegistry.profile("Assimilated").add(ASSIMILATED));
            ScanRegistry.map(ItemModule.Kind.HIJACKED, ScanRegistry.profile("Hijacked").add(HIJACKED));
            ScanRegistry.map(ItemModule.Kind.FERAL, ScanRegistry.profile("Feral").add(FERAL));
            ScanRegistry.map(ItemModule.Kind.CRUDE, ScanRegistry.profile("Crude").add(CRUDE));
            ScanRegistry.map(ItemModule.Kind.PRIMITIVE, ScanRegistry.profile("Primitive").add(PRIMITIVE));
            ScanRegistry.map(ItemModule.Kind.ADAPTED, ScanRegistry.profile("Adapted").add(ADAPTED));
            ScanRegistry.map(ItemModule.Kind.NEXUS, ScanRegistry.profile("Nexus").add(NEXUS));
            ScanRegistry.map(ItemModule.Kind.DETERRENT, ScanRegistry.profile("Deterrent").add(DETERRENT));
            ScanRegistry.map(ItemModule.Kind.PURE, ScanRegistry.profile("Pure").add(PURE));
            ScanRegistry.map(ItemModule.Kind.PREEMINENT, ScanRegistry.profile("Preeminent").add(PREEMINENT));
            ScanRegistry.map(ItemModule.Kind.ANCIENT, ScanRegistry.profile("Ancient").add(ANCIENT));
            ScanRegistry.map(ItemModule.Kind.ASSIMARA, ScanRegistry.profile("Assimara").add(ASSIMARA));
            ScanRegistry.map(ItemModule.Kind.DERIVED, ScanRegistry.profile("Derived").add(DERIVED));
            ScanRegistry.map(ItemModule.Kind.DESMOID, ScanRegistry.profile("Desmoid").add(INBORN, ASSIMARA, ASSIMILATED, HIJACKED));
            ScanRegistry.map(ItemModule.Kind.ESCHAR, ScanRegistry.profile("Eschar").add(FERAL, CRUDE, PRIMITIVE));
            ScanRegistry.map(ItemModule.Kind.RESISTANCE, ScanRegistry.profile("Resistance").add(ADAPTED, NEXUS, DETERRENT));
            ScanRegistry.map(ItemModule.Kind.IDEAL, ScanRegistry.profile("Ideal").add(PURE, PREEMINENT, DERIVED, ANCIENT));
            ScanRegistry.map(ItemModule.Kind.ORIGIN, ScanRegistry.profile("Origin").add(INBORN, ASSIMARA, ASSIMILATED, HIJACKED, FERAL, CRUDE, PRIMITIVE, ADAPTED, NEXUS, DETERRENT, PURE, PREEMINENT, DERIVED, ANCIENT));
        }

        private static Tier tier(String id) {
            Tier t = new Tier(id);
            TIERS.put(id, t);
            return t;
        }

        private static ModuleProfile profile(String name) {
            return new ModuleProfile(name);
        }

        private static void map(ItemModule.Kind kind, ModuleProfile mp) {
            MODULES.put(kind, mp);
        }

        @Nullable
        public static ModuleProfile getProfileFor(ItemModule.Kind kind) {
            ScanRegistry.ensureInit();
            return MODULES.get((Object)kind);
        }

        public static Collection<Tier> getAllTiers() {
            ScanRegistry.ensureInit();
            return Collections.unmodifiableCollection(TIERS.values());
        }

        public static final class ModuleProfile {
            public final String name;
            public final LinkedHashSet<Tier> tiers = new LinkedHashSet();

            ModuleProfile(String name) {
                this.name = name;
            }

            ModuleProfile add(Tier ... ts) {
                Collections.addAll(this.tiers, ts);
                return this;
            }
        }

        public static final class Tier {
            public final String id;
            private final Set<ResourceLocation> entityIds = new LinkedHashSet<ResourceLocation>();
            final String langKey;
            final Set<ResourceLocation> ids = new LinkedHashSet<ResourceLocation>();

            Tier(String id) {
                this.id = id;
                this.langKey = "tier.srparasites." + id.toLowerCase(Locale.ROOT);
            }

            public String getDisplayName() {
                return I18n.translateToLocal((String)this.langKey);
            }

            void addIds(ResourceLocation ... rs) {
                Collections.addAll(this.ids, rs);
                Collections.addAll(this.entityIds, rs);
            }

            boolean matches(EntityLivingBase e) {
                ResourceLocation key = EntityList.getKey((Entity)e);
                return key != null && this.ids.contains(key);
            }

            public Set<ResourceLocation> getEntityIds() {
                return Collections.unmodifiableSet(this.entityIds);
            }

            public String getIdLower() {
                return this.id == null ? "" : this.id.toLowerCase(Locale.ROOT);
            }
        }
    }
}

