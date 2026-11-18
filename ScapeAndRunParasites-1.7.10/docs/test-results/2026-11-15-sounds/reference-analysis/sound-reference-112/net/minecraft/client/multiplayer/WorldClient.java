/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

public class WorldClient
extends World {
    private final NetHandlerPlayClient field_73035_a;
    private ChunkProviderClient field_73033_b;
    private final Set<Entity> field_73032_d = Sets.newHashSet();
    private final Set<Entity> field_73036_L = Sets.newHashSet();
    private final Minecraft field_73037_M = Minecraft.func_71410_x();
    private final Set<ChunkPos> field_73038_N = Sets.newHashSet();
    private int field_184158_M = this.field_73012_v.nextInt(12000);
    protected Set<ChunkPos> field_184157_a = Sets.newHashSet();

    public WorldClient(NetHandlerPlayClient netHandlerPlayClient, WorldSettings worldSettings, int n, EnumDifficulty enumDifficulty, Profiler profiler) {
        super(new SaveHandlerMP(), new WorldInfo(worldSettings, "MpServer"), DimensionType.func_186069_a(n).func_186070_d(), profiler, true);
        this.field_73035_a = netHandlerPlayClient;
        this.func_72912_H().func_176144_a(enumDifficulty);
        this.func_175652_B(new BlockPos(8, 64, 8));
        this.field_73011_w.func_76558_a(this);
        this.field_73020_y = this.func_72970_h();
        this.field_72988_C = new SaveDataMemoryStorage();
        this.func_72966_v();
        this.func_72947_a();
    }

    @Override
    public void func_72835_b() {
        super.func_72835_b();
        this.func_82738_a(this.func_82737_E() + 1L);
        if (this.func_82736_K().func_82766_b("doDaylightCycle")) {
            this.func_72877_b(this.func_72820_D() + 1L);
        }
        this.field_72984_F.func_76320_a("reEntryProcessing");
        for (int i = 0; i < 10 && !this.field_73036_L.isEmpty(); ++i) {
            Entity entity = this.field_73036_L.iterator().next();
            this.field_73036_L.remove(entity);
            if (this.field_72996_f.contains(entity)) continue;
            this.func_72838_d(entity);
        }
        this.field_72984_F.func_76318_c("chunkCache");
        this.field_73033_b.func_73156_b();
        this.field_72984_F.func_76318_c("blocks");
        this.func_147456_g();
        this.field_72984_F.func_76319_b();
    }

    public void func_73031_a(int n, int n2, int n3, int n4, int n5, int n6) {
    }

    @Override
    protected IChunkProvider func_72970_h() {
        this.field_73033_b = new ChunkProviderClient(this);
        return this.field_73033_b;
    }

    @Override
    protected boolean func_175680_a(int n, int n2, boolean bl) {
        return bl || !this.func_72863_F().func_186025_d(n, n2).func_76621_g();
    }

    protected void func_184154_a() {
        this.field_184157_a.clear();
        int n = this.field_73037_M.field_71474_y.field_151451_c;
        this.field_72984_F.func_76320_a("buildList");
        \u2603 = MathHelper.func_76128_c(this.field_73037_M.field_71439_g.field_70165_t / 16.0);
        \u2603 = MathHelper.func_76128_c(this.field_73037_M.field_71439_g.field_70161_v / 16.0);
        for (\u2603 = -n; \u2603 <= n; ++\u2603) {
            for (\u2603 = -n; \u2603 <= n; ++\u2603) {
                this.field_184157_a.add(new ChunkPos(\u2603 + \u2603, \u2603 + \u2603));
            }
        }
        this.field_72984_F.func_76319_b();
    }

    @Override
    protected void func_147456_g() {
        this.func_184154_a();
        if (this.field_184158_M > 0) {
            --this.field_184158_M;
        }
        this.field_73038_N.retainAll(this.field_184157_a);
        if (this.field_73038_N.size() == this.field_184157_a.size()) {
            this.field_73038_N.clear();
        }
        int n = 0;
        for (ChunkPos chunkPos : this.field_184157_a) {
            if (this.field_73038_N.contains(chunkPos)) continue;
            int n2 = chunkPos.field_77276_a * 16;
            \u2603 = chunkPos.field_77275_b * 16;
            this.field_72984_F.func_76320_a("getChunk");
            Chunk \u26032 = this.func_72964_e(chunkPos.field_77276_a, chunkPos.field_77275_b);
            this.func_147467_a(n2, \u2603, \u26032);
            this.field_72984_F.func_76319_b();
            this.field_73038_N.add(chunkPos);
            if (++n < 10) continue;
            return;
        }
    }

    public void func_73025_a(int n, int n2, boolean bl) {
        if (bl) {
            this.field_73033_b.func_73158_c(n, n2);
        } else {
            this.field_73033_b.func_73234_b(n, n2);
            this.func_147458_c(n * 16, 0, n2 * 16, n * 16 + 15, 256, n2 * 16 + 15);
        }
    }

    @Override
    public boolean func_72838_d(Entity entity) {
        boolean bl = super.func_72838_d(entity);
        this.field_73032_d.add(entity);
        if (bl) {
            if (entity instanceof EntityMinecart) {
                this.field_73037_M.func_147118_V().func_147682_a(new MovingSoundMinecart((EntityMinecart)entity));
            }
        } else {
            this.field_73036_L.add(entity);
        }
        return bl;
    }

    @Override
    public void func_72900_e(Entity entity) {
        super.func_72900_e(entity);
        this.field_73032_d.remove(entity);
    }

    @Override
    protected void func_72923_a(Entity entity) {
        super.func_72923_a(entity);
        if (this.field_73036_L.contains(entity)) {
            this.field_73036_L.remove(entity);
        }
    }

    @Override
    protected void func_72847_b(Entity entity) {
        super.func_72847_b(entity);
        if (this.field_73032_d.contains(entity)) {
            if (entity.func_70089_S()) {
                this.field_73036_L.add(entity);
            } else {
                this.field_73032_d.remove(entity);
            }
        }
    }

    public void func_73027_a(int n, Entity entity) {
        \u2603 = this.func_73045_a(n);
        if (\u2603 != null) {
            this.func_72900_e(\u2603);
        }
        this.field_73032_d.add(entity);
        entity.func_145769_d(n);
        if (!this.func_72838_d(entity)) {
            this.field_73036_L.add(entity);
        }
        this.field_175729_l.func_76038_a(n, entity);
    }

    @Override
    @Nullable
    public Entity func_73045_a(int n) {
        if (n == this.field_73037_M.field_71439_g.func_145782_y()) {
            return this.field_73037_M.field_71439_g;
        }
        return super.func_73045_a(n);
    }

    public Entity func_73028_b(int n) {
        Entity entity = (Entity)this.field_175729_l.func_76049_d(n);
        if (entity != null) {
            this.field_73032_d.remove(entity);
            this.func_72900_e(entity);
        }
        return entity;
    }

    @Deprecated
    public boolean func_180503_b(BlockPos blockPos, IBlockState iBlockState) {
        int n = blockPos.func_177958_n();
        \u2603 = blockPos.func_177956_o();
        \u2603 = blockPos.func_177952_p();
        this.func_73031_a(n, \u2603, \u2603, n, \u2603, \u2603);
        return super.func_180501_a(blockPos, iBlockState, 3);
    }

    @Override
    public void func_72882_A() {
        this.field_73035_a.func_147298_b().func_150718_a(new TextComponentString("Quitting"));
    }

    @Override
    protected void func_72979_l() {
    }

    @Override
    protected void func_147467_a(int n, int n2, Chunk chunk) {
        super.func_147467_a(n, n2, chunk);
        if (this.field_184158_M == 0) {
            this.field_73005_l = this.field_73005_l * 3 + 1013904223;
            int n3 = this.field_73005_l >> 2;
            \u2603 = n3 & 0xF;
            \u2603 = n3 >> 8 & 0xF;
            \u2603 = n3 >> 16 & 0xFF;
            BlockPos \u26032 = new BlockPos(\u2603 + n, \u2603, \u2603 + n2);
            IBlockState \u26033 = chunk.func_177435_g(\u26032);
            \u2603 += n;
            \u2603 += n2;
            if (\u26033.func_185904_a() == Material.field_151579_a && this.func_175699_k(\u26032) <= this.field_73012_v.nextInt(8) && this.func_175642_b(EnumSkyBlock.SKY, \u26032) <= 0) {
                double d = this.field_73037_M.field_71439_g.func_70092_e((double)\u2603 + 0.5, (double)\u2603 + 0.5, (double)\u2603 + 0.5);
                if (this.field_73037_M.field_71439_g != null && d > 4.0 && d < 256.0) {
                    this.func_184134_a((double)\u2603 + 0.5, (double)\u2603 + 0.5, (double)\u2603 + 0.5, SoundEvents.field_187674_a, SoundCategory.AMBIENT, 0.7f, 0.8f + this.field_73012_v.nextFloat() * 0.2f, false);
                    this.field_184158_M = this.field_73012_v.nextInt(12000) + 6000;
                }
            }
        }
    }

    public void func_73029_E(int n, int n2, int n3) {
        \u2603 = 32;
        Random random = new Random();
        ItemStack \u26032 = this.field_73037_M.field_71439_g.func_184614_ca();
        boolean \u26033 = this.field_73037_M.field_71442_b.func_178889_l() == GameType.CREATIVE && !\u26032.func_190926_b() && \u26032.func_77973_b() == Item.func_150898_a(Blocks.field_180401_cv);
        BlockPos.MutableBlockPos \u26034 = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 667; ++i) {
            this.func_184153_a(n, n2, n3, 16, random, \u26033, \u26034);
            this.func_184153_a(n, n2, n3, 32, random, \u26033, \u26034);
        }
    }

    public void func_184153_a(int n, int n2, int n3, int n4, Random random, boolean bl, BlockPos.MutableBlockPos mutableBlockPos) {
        int n5 = n + this.field_73012_v.nextInt(n4) - this.field_73012_v.nextInt(n4);
        \u2603 = n2 + this.field_73012_v.nextInt(n4) - this.field_73012_v.nextInt(n4);
        \u2603 = n3 + this.field_73012_v.nextInt(n4) - this.field_73012_v.nextInt(n4);
        mutableBlockPos.func_181079_c(n5, \u2603, \u2603);
        IBlockState \u26032 = this.func_180495_p(mutableBlockPos);
        \u26032.func_177230_c().func_180655_c(\u26032, this, mutableBlockPos, random);
        if (bl && \u26032.func_177230_c() == Blocks.field_180401_cv) {
            this.func_175688_a(EnumParticleTypes.BARRIER, (float)n5 + 0.5f, (float)\u2603 + 0.5f, (float)\u2603 + 0.5f, 0.0, 0.0, 0.0, new int[0]);
        }
    }

    public void func_73022_a() {
        int \u26033;
        Entity entity;
        int n;
        this.field_72996_f.removeAll(this.field_72997_g);
        for (n = 0; n < this.field_72997_g.size(); ++n) {
            entity = (Entity)this.field_72997_g.get(n);
            int \u26032 = entity.field_70176_ah;
            \u26033 = entity.field_70164_aj;
            if (!entity.field_70175_ag || !this.func_175680_a(\u26032, \u26033, true)) continue;
            this.func_72964_e(\u26032, \u26033).func_76622_b(entity);
        }
        for (n = 0; n < this.field_72997_g.size(); ++n) {
            this.func_72847_b((Entity)this.field_72997_g.get(n));
        }
        this.field_72997_g.clear();
        for (n = 0; n < this.field_72996_f.size(); ++n) {
            entity = (Entity)this.field_72996_f.get(n);
            \u2603 = entity.func_184187_bx();
            if (\u2603 != null) {
                if (!\u2603.field_70128_L && \u2603.func_184196_w(entity)) continue;
                entity.func_184210_p();
            }
            if (!entity.field_70128_L) continue;
            \u26033 = entity.field_70176_ah;
            int \u26034 = entity.field_70164_aj;
            if (entity.field_70175_ag && this.func_175680_a(\u26033, \u26034, true)) {
                this.func_72964_e(\u26033, \u26034).func_76622_b(entity);
            }
            this.field_72996_f.remove(n--);
            this.func_72847_b(entity);
        }
    }

    @Override
    public CrashReportCategory func_72914_a(CrashReport crashReport) {
        CrashReportCategory crashReportCategory = super.func_72914_a(crashReport);
        crashReportCategory.func_189529_a("Forced entities", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return WorldClient.this.field_73032_d.size() + " total; " + WorldClient.this.field_73032_d;
            }

            @Override
            public /* synthetic */ Object call() throws Exception {
                return this.call();
            }
        });
        crashReportCategory.func_189529_a("Retry entities", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return WorldClient.this.field_73036_L.size() + " total; " + WorldClient.this.field_73036_L;
            }

            @Override
            public /* synthetic */ Object call() throws Exception {
                return this.call();
            }
        });
        crashReportCategory.func_189529_a("Server brand", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return ((WorldClient)WorldClient.this).field_73037_M.field_71439_g.func_142021_k();
            }

            @Override
            public /* synthetic */ Object call() throws Exception {
                return this.call();
            }
        });
        crashReportCategory.func_189529_a("Server type", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return WorldClient.this.field_73037_M.func_71401_C() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
            }

            @Override
            public /* synthetic */ Object call() throws Exception {
                return this.call();
            }
        });
        return crashReportCategory;
    }

    @Override
    public void func_184148_a(@Nullable EntityPlayer entityPlayer, double d, double d2, double d3, SoundEvent soundEvent, SoundCategory soundCategory, float f, float f2) {
        if (entityPlayer == this.field_73037_M.field_71439_g) {
            this.func_184134_a(d, d2, d3, soundEvent, soundCategory, f, f2, false);
        }
    }

    public void func_184156_a(BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float f2, boolean bl) {
        this.func_184134_a((double)blockPos.func_177958_n() + 0.5, (double)blockPos.func_177956_o() + 0.5, (double)blockPos.func_177952_p() + 0.5, soundEvent, soundCategory, f, f2, bl);
    }

    @Override
    public void func_184134_a(double d, double d2, double d3, SoundEvent soundEvent, SoundCategory soundCategory, float f, float f2, boolean bl) {
        double d4 = this.field_73037_M.func_175606_aa().func_70092_e(d, d2, d3);
        PositionedSoundRecord \u26032 = new PositionedSoundRecord(soundEvent, soundCategory, f, f2, (float)d, (float)d2, (float)d3);
        if (bl && d4 > 100.0) {
            \u2603 = Math.sqrt(d4) / 40.0;
            this.field_73037_M.func_147118_V().func_147681_a(\u26032, (int)(\u2603 * 20.0));
        } else {
            this.field_73037_M.func_147118_V().func_147682_a(\u26032);
        }
    }

    @Override
    public void func_92088_a(double d, double d2, double d3, double d4, double d5, double d6, @Nullable NBTTagCompound nBTTagCompound) {
        this.field_73037_M.field_71452_i.func_78873_a(new ParticleFirework.Starter(this, d, d2, d3, d4, d5, d6, this.field_73037_M.field_71452_i, nBTTagCompound));
    }

    @Override
    public void func_184135_a(Packet<?> packet) {
        this.field_73035_a.func_147297_a(packet);
    }

    public void func_96443_a(Scoreboard scoreboard) {
        this.field_96442_D = scoreboard;
    }

    @Override
    public void func_72877_b(long l) {
        if (l < 0L) {
            l = -l;
            this.func_82736_K().func_82764_b("doDaylightCycle", "false");
        } else {
            this.func_82736_K().func_82764_b("doDaylightCycle", "true");
        }
        super.func_72877_b(l);
    }

    @Override
    public ChunkProviderClient func_72863_F() {
        return (ChunkProviderClient)super.func_72863_F();
    }

    @Override
    public /* synthetic */ IChunkProvider func_72863_F() {
        return this.func_72863_F();
    }
}

