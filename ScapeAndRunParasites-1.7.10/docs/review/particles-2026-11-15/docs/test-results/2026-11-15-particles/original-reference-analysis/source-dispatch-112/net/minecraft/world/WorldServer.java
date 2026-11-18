/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.util.concurrent.ListenableFuture
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ReportedException;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataCallableSave;
import net.minecraft.world.storage.loot.LootTableManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer
extends World
implements IThreadListener {
    private static final Logger field_147491_a = LogManager.getLogger();
    private final MinecraftServer field_73061_a;
    private final EntityTracker field_73062_L;
    private final PlayerChunkMap field_73063_M;
    private final Set<NextTickListEntry> field_73064_N = Sets.newHashSet();
    private final TreeSet<NextTickListEntry> field_73065_O = new TreeSet();
    private final Map<UUID, Entity> field_175741_N = Maps.newHashMap();
    public boolean field_73058_d;
    private boolean field_73068_P;
    private int field_80004_Q;
    private final Teleporter field_85177_Q;
    private final WorldEntitySpawner field_175742_R = new WorldEntitySpawner();
    protected final VillageSiege field_175740_d = new VillageSiege(this);
    private final ServerBlockEventList[] field_147490_S = new ServerBlockEventList[]{new ServerBlockEventList(), new ServerBlockEventList()};
    private int field_147489_T;
    private final List<NextTickListEntry> field_94579_S = Lists.newArrayList();

    public WorldServer(MinecraftServer minecraftServer, ISaveHandler iSaveHandler, WorldInfo worldInfo, int n, Profiler profiler) {
        super(iSaveHandler, worldInfo, DimensionType.func_186069_a(n).func_186070_d(), profiler, false);
        this.field_73061_a = minecraftServer;
        this.field_73062_L = new EntityTracker(this);
        this.field_73063_M = new PlayerChunkMap(this);
        this.field_73011_w.func_76558_a(this);
        this.field_73020_y = this.func_72970_h();
        this.field_85177_Q = new Teleporter(this);
        this.func_72966_v();
        this.func_72947_a();
        this.func_175723_af().func_177725_a(minecraftServer.func_175580_aG());
    }

    @Override
    public World func_175643_b() {
        this.field_72988_C = new MapStorage(this.field_73019_z);
        String string = VillageCollection.func_176062_a(this.field_73011_w);
        VillageCollection \u26032 = (VillageCollection)this.field_72988_C.func_75742_a(VillageCollection.class, string);
        if (\u26032 == null) {
            this.field_72982_D = new VillageCollection(this);
            this.field_72988_C.func_75745_a(string, this.field_72982_D);
        } else {
            this.field_72982_D = \u26032;
            this.field_72982_D.func_82566_a(this);
        }
        this.field_96442_D = new ServerScoreboard(this.field_73061_a);
        ScoreboardSaveData \u26033 = (ScoreboardSaveData)this.field_72988_C.func_75742_a(ScoreboardSaveData.class, "scoreboard");
        if (\u26033 == null) {
            \u26033 = new ScoreboardSaveData();
            this.field_72988_C.func_75745_a("scoreboard", \u26033);
        }
        \u26033.func_96499_a(this.field_96442_D);
        ((ServerScoreboard)this.field_96442_D).func_186684_a(new WorldSavedDataCallableSave(\u26033));
        this.field_184151_B = new LootTableManager(new File(new File(this.field_73019_z.func_75765_b(), "data"), "loot_tables"));
        this.field_191951_C = new AdvancementManager(new File(new File(this.field_73019_z.func_75765_b(), "data"), "advancements"));
        this.field_193036_D = new FunctionManager(new File(new File(this.field_73019_z.func_75765_b(), "data"), "functions"), this.field_73061_a);
        this.func_175723_af().func_177739_c(this.field_72986_A.func_176120_C(), this.field_72986_A.func_176126_D());
        this.func_175723_af().func_177744_c(this.field_72986_A.func_176140_I());
        this.func_175723_af().func_177724_b(this.field_72986_A.func_176138_H());
        this.func_175723_af().func_177747_c(this.field_72986_A.func_176131_J());
        this.func_175723_af().func_177723_b(this.field_72986_A.func_176139_K());
        if (this.field_72986_A.func_176134_F() > 0L) {
            this.func_175723_af().func_177738_a(this.field_72986_A.func_176137_E(), this.field_72986_A.func_176132_G(), this.field_72986_A.func_176134_F());
        } else {
            this.func_175723_af().func_177750_a(this.field_72986_A.func_176137_E());
        }
        return this;
    }

    @Override
    public void func_72835_b() {
        super.func_72835_b();
        if (this.func_72912_H().func_76093_s() && this.func_175659_aa() != EnumDifficulty.HARD) {
            this.func_72912_H().func_176144_a(EnumDifficulty.HARD);
        }
        this.field_73011_w.func_177499_m().func_76938_b();
        if (this.func_73056_e()) {
            if (this.func_82736_K().func_82766_b("doDaylightCycle")) {
                long l = this.field_72986_A.func_76073_f() + 24000L;
                this.field_72986_A.func_76068_b(l - l % 24000L);
            }
            this.func_73053_d();
        }
        this.field_72984_F.func_76320_a("mobSpawner");
        if (this.func_82736_K().func_82766_b("doMobSpawning") && this.field_72986_A.func_76067_t() != WorldType.field_180272_g) {
            this.field_175742_R.func_77192_a(this, this.field_72985_G, this.field_72992_H, this.field_72986_A.func_82573_f() % 400L == 0L);
        }
        this.field_72984_F.func_76318_c("chunkSource");
        this.field_73020_y.func_73156_b();
        int n = this.func_72967_a(1.0f);
        if (n != this.func_175657_ab()) {
            this.func_175692_b(n);
        }
        this.field_72986_A.func_82572_b(this.field_72986_A.func_82573_f() + 1L);
        if (this.func_82736_K().func_82766_b("doDaylightCycle")) {
            this.field_72986_A.func_76068_b(this.field_72986_A.func_76073_f() + 1L);
        }
        this.field_72984_F.func_76318_c("tickPending");
        this.func_72955_a(false);
        this.field_72984_F.func_76318_c("tickBlocks");
        this.func_147456_g();
        this.field_72984_F.func_76318_c("chunkMap");
        this.field_73063_M.func_72693_b();
        this.field_72984_F.func_76318_c("village");
        this.field_72982_D.func_75544_a();
        this.field_175740_d.func_75528_a();
        this.field_72984_F.func_76318_c("portalForcer");
        this.field_85177_Q.func_85189_a(this.func_82737_E());
        this.field_72984_F.func_76319_b();
        this.func_147488_Z();
    }

    @Nullable
    public Biome.SpawnListEntry func_175734_a(EnumCreatureType enumCreatureType, BlockPos blockPos) {
        List<Biome.SpawnListEntry> list = this.func_72863_F().func_177458_a(enumCreatureType, blockPos);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return WeightedRandom.func_76271_a(this.field_73012_v, list);
    }

    public boolean func_175732_a(EnumCreatureType enumCreatureType, Biome.SpawnListEntry spawnListEntry, BlockPos blockPos) {
        List<Biome.SpawnListEntry> list = this.func_72863_F().func_177458_a(enumCreatureType, blockPos);
        if (list == null || list.isEmpty()) {
            return false;
        }
        return list.contains(spawnListEntry);
    }

    @Override
    public void func_72854_c() {
        this.field_73068_P = false;
        if (!this.field_73010_i.isEmpty()) {
            int n;
            int n2 = 0;
            n = 0;
            for (EntityPlayer entityPlayer : this.field_73010_i) {
                if (entityPlayer.func_175149_v()) {
                    ++n2;
                    continue;
                }
                if (!entityPlayer.func_70608_bn()) continue;
                ++n;
            }
            this.field_73068_P = n > 0 && n >= this.field_73010_i.size() - n2;
        }
    }

    protected void func_73053_d() {
        this.field_73068_P = false;
        List list = this.field_73010_i.stream().filter(EntityPlayer::func_70608_bn).collect(Collectors.toList());
        for (EntityPlayer entityPlayer : list) {
            entityPlayer.func_70999_a(false, false, true);
        }
        if (this.func_82736_K().func_82766_b("doWeatherCycle")) {
            this.func_73051_P();
        }
    }

    private void func_73051_P() {
        this.field_72986_A.func_76080_g(0);
        this.field_72986_A.func_76084_b(false);
        this.field_72986_A.func_76090_f(0);
        this.field_72986_A.func_76069_a(false);
    }

    public boolean func_73056_e() {
        if (this.field_73068_P && !this.field_72995_K) {
            for (EntityPlayer entityPlayer : this.field_73010_i) {
                if (entityPlayer.func_175149_v() || entityPlayer.func_71026_bH()) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void func_72974_f() {
        if (this.field_72986_A.func_76075_d() <= 0) {
            this.field_72986_A.func_76056_b(this.func_181545_F() + 1);
        }
        int n = this.field_72986_A.func_76079_c();
        \u2603 = this.field_72986_A.func_76074_e();
        \u2603 = 0;
        while (this.func_184141_c(new BlockPos(n, 0, \u2603)).func_185904_a() == Material.field_151579_a) {
            n += this.field_73012_v.nextInt(8) - this.field_73012_v.nextInt(8);
            \u2603 += this.field_73012_v.nextInt(8) - this.field_73012_v.nextInt(8);
            if (++\u2603 != 10000) continue;
        }
        this.field_72986_A.func_76058_a(n);
        this.field_72986_A.func_76087_c(\u2603);
    }

    @Override
    protected boolean func_175680_a(int n, int n2, boolean bl) {
        return this.func_72863_F().func_73149_a(n, n2);
    }

    protected void func_184162_i() {
        this.field_72984_F.func_76320_a("playerCheckLight");
        if (!this.field_73010_i.isEmpty()) {
            int n = this.field_73012_v.nextInt(this.field_73010_i.size());
            EntityPlayer \u26032 = (EntityPlayer)this.field_73010_i.get(n);
            \u2603 = MathHelper.func_76128_c(\u26032.field_70165_t) + this.field_73012_v.nextInt(11) - 5;
            \u2603 = MathHelper.func_76128_c(\u26032.field_70163_u) + this.field_73012_v.nextInt(11) - 5;
            \u2603 = MathHelper.func_76128_c(\u26032.field_70161_v) + this.field_73012_v.nextInt(11) - 5;
            this.func_175664_x(new BlockPos(\u2603, \u2603, \u2603));
        }
        this.field_72984_F.func_76319_b();
    }

    @Override
    protected void func_147456_g() {
        this.func_184162_i();
        if (this.field_72986_A.func_76067_t() == WorldType.field_180272_g) {
            Iterator<Chunk> iterator = this.field_73063_M.func_187300_b();
            while (iterator.hasNext()) {
                iterator.next().func_150804_b(false);
            }
            return;
        }
        int n = this.func_82736_K().func_180263_c("randomTickSpeed");
        boolean \u26032 = this.func_72896_J();
        boolean \u26033 = this.func_72911_I();
        this.field_72984_F.func_76320_a("pollingChunks");
        Iterator<Chunk> \u26034 = this.field_73063_M.func_187300_b();
        while (\u26034.hasNext()) {
            this.field_72984_F.func_76320_a("getChunk");
            Chunk chunk = \u26034.next();
            int \u26035 = chunk.field_76635_g * 16;
            int \u26036 = chunk.field_76647_h * 16;
            this.field_72984_F.func_76318_c("checkNextLight");
            chunk.func_76594_o();
            this.field_72984_F.func_76318_c("tickChunk");
            chunk.func_150804_b(false);
            this.field_72984_F.func_76318_c("thunder");
            if (\u26032 && \u26033 && this.field_73012_v.nextInt(100000) == 0) {
                this.field_73005_l = this.field_73005_l * 3 + 1013904223;
                int n2 = this.field_73005_l >> 2;
                BlockPos \u26037 = this.func_175736_a(new BlockPos(\u26035 + (n2 & 0xF), 0, \u26036 + (n2 >> 8 & 0xF)));
                if (this.func_175727_C(\u26037)) {
                    DifficultyInstance difficultyInstance = this.func_175649_E(\u26037);
                    if (this.func_82736_K().func_82766_b("doMobSpawning") && this.field_73012_v.nextDouble() < (double)difficultyInstance.func_180168_b() * 0.01) {
                        EntitySkeletonHorse entitySkeletonHorse = new EntitySkeletonHorse(this);
                        entitySkeletonHorse.func_190691_p(true);
                        entitySkeletonHorse.func_70873_a(0);
                        entitySkeletonHorse.func_70107_b(\u26037.func_177958_n(), \u26037.func_177956_o(), \u26037.func_177952_p());
                        this.func_72838_d(entitySkeletonHorse);
                        this.func_72942_c(new EntityLightningBolt(this, \u26037.func_177958_n(), \u26037.func_177956_o(), \u26037.func_177952_p(), true));
                    } else {
                        this.func_72942_c(new EntityLightningBolt(this, \u26037.func_177958_n(), \u26037.func_177956_o(), \u26037.func_177952_p(), false));
                    }
                }
            }
            this.field_72984_F.func_76318_c("iceandsnow");
            if (this.field_73012_v.nextInt(16) == 0) {
                this.field_73005_l = this.field_73005_l * 3 + 1013904223;
                int n2 = this.field_73005_l >> 2;
                BlockPos \u260310 = this.func_175725_q(new BlockPos(\u26035 + (n2 & 0xF), 0, \u26036 + (n2 >> 8 & 0xF)));
                BlockPos \u26038 = \u260310.func_177977_b();
                if (this.func_175662_w(\u26038)) {
                    this.func_175656_a(\u26038, Blocks.field_150432_aD.func_176223_P());
                }
                if (\u26032 && this.func_175708_f(\u260310, true)) {
                    this.func_175656_a(\u260310, Blocks.field_150431_aC.func_176223_P());
                }
                if (\u26032 && this.func_180494_b(\u26038).func_76738_d()) {
                    this.func_180495_p(\u26038).func_177230_c().func_176224_k(this, \u26038);
                }
            }
            this.field_72984_F.func_76318_c("tickBlocks");
            if (n > 0) {
                for (ExtendedBlockStorage extendedBlockStorage : chunk.func_76587_i()) {
                    if (extendedBlockStorage == Chunk.field_186036_a || !extendedBlockStorage.func_76675_b()) continue;
                    for (int i = 0; i < n; ++i) {
                        this.field_73005_l = this.field_73005_l * 3 + 1013904223;
                        \u2603 = this.field_73005_l >> 2;
                        \u2603 = \u2603 & 0xF;
                        \u2603 = \u2603 >> 8 & 0xF;
                        \u2603 = \u2603 >> 16 & 0xF;
                        IBlockState iBlockState = extendedBlockStorage.func_177485_a(\u2603, \u2603, \u2603);
                        Block \u26039 = iBlockState.func_177230_c();
                        this.field_72984_F.func_76320_a("randomTick");
                        if (\u26039.func_149653_t()) {
                            \u26039.func_180645_a(this, new BlockPos(\u2603 + \u26035, \u2603 + extendedBlockStorage.func_76662_d(), \u2603 + \u26036), iBlockState, this.field_73012_v);
                        }
                        this.field_72984_F.func_76319_b();
                    }
                }
            }
            this.field_72984_F.func_76319_b();
        }
        this.field_72984_F.func_76319_b();
    }

    protected BlockPos func_175736_a(BlockPos blockPos) {
        BlockPos blockPos2;
        blockPos2 = this.func_175725_q(blockPos);
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(blockPos2, new BlockPos(blockPos2.func_177958_n(), this.func_72800_K(), blockPos2.func_177952_p())).func_186662_g(3.0);
        List<EntityLivingBase> \u26032 = this.func_175647_a(EntityLivingBase.class, axisAlignedBB, new Predicate<EntityLivingBase>(){

            public boolean apply(@Nullable EntityLivingBase entityLivingBase) {
                return entityLivingBase != null && entityLivingBase.func_70089_S() && WorldServer.this.func_175678_i(entityLivingBase.func_180425_c());
            }

            public /* synthetic */ boolean apply(@Nullable Object object) {
                return this.apply((EntityLivingBase)object);
            }
        });
        if (!\u26032.isEmpty()) {
            return \u26032.get(this.field_73012_v.nextInt(\u26032.size())).func_180425_c();
        }
        if (blockPos2.func_177956_o() == -1) {
            blockPos2 = blockPos2.func_177981_b(2);
        }
        return blockPos2;
    }

    @Override
    public boolean func_175691_a(BlockPos blockPos, Block block) {
        NextTickListEntry nextTickListEntry = new NextTickListEntry(blockPos, block);
        return this.field_94579_S.contains(nextTickListEntry);
    }

    @Override
    public boolean func_184145_b(BlockPos blockPos, Block block) {
        NextTickListEntry nextTickListEntry = new NextTickListEntry(blockPos, block);
        return this.field_73064_N.contains(nextTickListEntry);
    }

    @Override
    public void func_175684_a(BlockPos blockPos, Block block, int n) {
        this.func_175654_a(blockPos, block, n, 0);
    }

    @Override
    public void func_175654_a(BlockPos blockPos2, Block block, int n3, int n2) {
        BlockPos blockPos2;
        Material material = block.func_176223_P().func_185904_a();
        if (this.field_72999_e && material != Material.field_151579_a) {
            if (block.func_149698_L()) {
                if (this.func_175707_a(blockPos2.func_177982_a(-8, -8, -8), blockPos2.func_177982_a(8, 8, 8)) && (\u2603 = this.func_180495_p(blockPos2)).func_185904_a() != Material.field_151579_a && \u2603.func_177230_c() == block) {
                    \u2603.func_177230_c().func_180650_b(this, blockPos2, \u2603, this.field_73012_v);
                }
                return;
            }
            int n3 = 1;
        }
        NextTickListEntry \u26032 = new NextTickListEntry(blockPos2, block);
        if (this.func_175667_e(blockPos2)) {
            if (material != Material.field_151579_a) {
                \u26032.func_77176_a((long)n3 + this.field_72986_A.func_82573_f());
                \u26032.func_82753_a(n2);
            }
            if (!this.field_73064_N.contains(\u26032)) {
                this.field_73064_N.add(\u26032);
                this.field_73065_O.add(\u26032);
            }
        }
    }

    @Override
    public void func_180497_b(BlockPos blockPos, Block block, int n, int n2) {
        NextTickListEntry nextTickListEntry = new NextTickListEntry(blockPos, block);
        nextTickListEntry.func_82753_a(n2);
        Material \u26032 = block.func_176223_P().func_185904_a();
        if (\u26032 != Material.field_151579_a) {
            nextTickListEntry.func_77176_a((long)n + this.field_72986_A.func_82573_f());
        }
        if (!this.field_73064_N.contains(nextTickListEntry)) {
            this.field_73064_N.add(nextTickListEntry);
            this.field_73065_O.add(nextTickListEntry);
        }
    }

    @Override
    public void func_72939_s() {
        if (this.field_73010_i.isEmpty()) {
            if (this.field_80004_Q++ >= 300) {
                return;
            }
        } else {
            this.func_82742_i();
        }
        this.field_73011_w.func_186059_r();
        super.func_72939_s();
    }

    @Override
    protected void func_184147_l() {
        super.func_184147_l();
        this.field_72984_F.func_76318_c("players");
        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            Entity entity = (Entity)this.field_73010_i.get(i);
            \u2603 = entity.func_184187_bx();
            if (\u2603 != null) {
                if (!\u2603.field_70128_L && \u2603.func_184196_w(entity)) continue;
                entity.func_184210_p();
            }
            this.field_72984_F.func_76320_a("tick");
            if (!entity.field_70128_L) {
                try {
                    this.func_72870_g(entity);
                }
                catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.func_85055_a(throwable, "Ticking player");
                    CrashReportCategory \u26032 = crashReport.func_85058_a("Player being ticked");
                    entity.func_85029_a(\u26032);
                    throw new ReportedException(crashReport);
                }
            }
            this.field_72984_F.func_76319_b();
            this.field_72984_F.func_76320_a("remove");
            if (entity.field_70128_L) {
                int n = entity.field_70176_ah;
                \u2603 = entity.field_70164_aj;
                if (entity.field_70175_ag && this.func_175680_a(n, \u2603, true)) {
                    this.func_72964_e(n, \u2603).func_76622_b(entity);
                }
                this.field_72996_f.remove(entity);
                this.func_72847_b(entity);
            }
            this.field_72984_F.func_76319_b();
        }
    }

    public void func_82742_i() {
        this.field_80004_Q = 0;
    }

    @Override
    public boolean func_72955_a(boolean bl) {
        NextTickListEntry nextTickListEntry;
        if (this.field_72986_A.func_76067_t() == WorldType.field_180272_g) {
            return false;
        }
        int n = this.field_73065_O.size();
        if (n != this.field_73064_N.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        if (n > 65536) {
            n = 65536;
        }
        this.field_72984_F.func_76320_a("cleaning");
        for (\u2603 = 0; \u2603 < n; ++\u2603) {
            nextTickListEntry = this.field_73065_O.first();
            if (!bl && nextTickListEntry.field_77180_e > this.field_72986_A.func_82573_f()) break;
            this.field_73065_O.remove(nextTickListEntry);
            this.field_73064_N.remove(nextTickListEntry);
            this.field_94579_S.add(nextTickListEntry);
        }
        this.field_72984_F.func_76319_b();
        this.field_72984_F.func_76320_a("ticking");
        Iterator<NextTickListEntry> iterator = this.field_94579_S.iterator();
        while (iterator.hasNext()) {
            nextTickListEntry = iterator.next();
            iterator.remove();
            boolean \u26032 = false;
            if (this.func_175707_a(nextTickListEntry.field_180282_a.func_177982_a(0, 0, 0), nextTickListEntry.field_180282_a.func_177982_a(0, 0, 0))) {
                IBlockState iBlockState = this.func_180495_p(nextTickListEntry.field_180282_a);
                if (iBlockState.func_185904_a() == Material.field_151579_a || !Block.func_149680_a(iBlockState.func_177230_c(), nextTickListEntry.func_151351_a())) continue;
                try {
                    iBlockState.func_177230_c().func_180650_b(this, nextTickListEntry.field_180282_a, iBlockState, this.field_73012_v);
                    continue;
                }
                catch (Throwable \u26033) {
                    CrashReport crashReport = CrashReport.func_85055_a(\u26033, "Exception while ticking a block");
                    CrashReportCategory \u26034 = crashReport.func_85058_a("Block being ticked");
                    CrashReportCategory.func_175750_a(\u26034, nextTickListEntry.field_180282_a, iBlockState);
                    throw new ReportedException(crashReport);
                }
            }
            this.func_175684_a(nextTickListEntry.field_180282_a, nextTickListEntry.func_151351_a(), 0);
        }
        this.field_72984_F.func_76319_b();
        this.field_94579_S.clear();
        return !this.field_73065_O.isEmpty();
    }

    @Override
    @Nullable
    public List<NextTickListEntry> func_72920_a(Chunk chunk, boolean bl) {
        ChunkPos chunkPos = chunk.func_76632_l();
        int \u26032 = (chunkPos.field_77276_a << 4) - 2;
        int \u26033 = \u26032 + 16 + 2;
        int \u26034 = (chunkPos.field_77275_b << 4) - 2;
        int \u26035 = \u26034 + 16 + 2;
        return this.func_175712_a(new StructureBoundingBox(\u26032, 0, \u26034, \u26033, 256, \u26035), bl);
    }

    @Override
    @Nullable
    public List<NextTickListEntry> func_175712_a(StructureBoundingBox structureBoundingBox, boolean bl) {
        ArrayList arrayList = null;
        for (int i = 0; i < 2; ++i) {
            Iterator<NextTickListEntry> iterator = i == 0 ? this.field_73065_O.iterator() : this.field_94579_S.iterator();
            while (iterator.hasNext()) {
                NextTickListEntry nextTickListEntry = iterator.next();
                BlockPos \u26032 = nextTickListEntry.field_180282_a;
                if (\u26032.func_177958_n() < structureBoundingBox.field_78897_a || \u26032.func_177958_n() >= structureBoundingBox.field_78893_d || \u26032.func_177952_p() < structureBoundingBox.field_78896_c || \u26032.func_177952_p() >= structureBoundingBox.field_78892_f) continue;
                if (bl) {
                    if (i == 0) {
                        this.field_73064_N.remove(nextTickListEntry);
                    }
                    iterator.remove();
                }
                if (arrayList == null) {
                    arrayList = Lists.newArrayList();
                }
                arrayList.add(nextTickListEntry);
            }
        }
        return arrayList;
    }

    @Override
    public void func_72866_a(Entity entity, boolean bl) {
        if (!this.func_175735_ai() && (entity instanceof EntityAnimal || entity instanceof EntityWaterMob)) {
            entity.func_70106_y();
        }
        if (!this.func_175738_ah() && entity instanceof INpc) {
            entity.func_70106_y();
        }
        super.func_72866_a(entity, bl);
    }

    private boolean func_175738_ah() {
        return this.field_73061_a.func_71220_V();
    }

    private boolean func_175735_ai() {
        return this.field_73061_a.func_71268_U();
    }

    @Override
    protected IChunkProvider func_72970_h() {
        IChunkLoader iChunkLoader = this.field_73019_z.func_75763_a(this.field_73011_w);
        return new ChunkProviderServer(this, iChunkLoader, this.field_73011_w.func_186060_c());
    }

    @Override
    public boolean func_175660_a(EntityPlayer entityPlayer, BlockPos blockPos) {
        return !this.field_73061_a.func_175579_a(this, blockPos, entityPlayer) && this.func_175723_af().func_177746_a(blockPos);
    }

    @Override
    public void func_72963_a(WorldSettings worldSettings) {
        if (!this.field_72986_A.func_76070_v()) {
            try {
                this.func_73052_b(worldSettings);
                if (this.field_72986_A.func_76067_t() == WorldType.field_180272_g) {
                    this.func_175737_aj();
                }
                super.func_72963_a(worldSettings);
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.func_85055_a(throwable, "Exception initializing level");
                try {
                    this.func_72914_a(crashReport);
                }
                catch (Throwable throwable2) {
                    // empty catch block
                }
                throw new ReportedException(crashReport);
            }
            this.field_72986_A.func_76091_d(true);
        }
    }

    private void func_175737_aj() {
        this.field_72986_A.func_176128_f(false);
        this.field_72986_A.func_176121_c(true);
        this.field_72986_A.func_76084_b(false);
        this.field_72986_A.func_76069_a(false);
        this.field_72986_A.func_176142_i(1000000000);
        this.field_72986_A.func_76068_b(6000L);
        this.field_72986_A.func_76060_a(GameType.SPECTATOR);
        this.field_72986_A.func_176119_g(false);
        this.field_72986_A.func_176144_a(EnumDifficulty.PEACEFUL);
        this.field_72986_A.func_180783_e(true);
        this.func_82736_K().func_82764_b("doDaylightCycle", "false");
    }

    private void func_73052_b(WorldSettings worldSettings) {
        if (!this.field_73011_w.func_76567_e()) {
            this.field_72986_A.func_176143_a(BlockPos.field_177992_a.func_177981_b(this.field_73011_w.func_76557_i()));
            return;
        }
        if (this.field_72986_A.func_76067_t() == WorldType.field_180272_g) {
            this.field_72986_A.func_176143_a(BlockPos.field_177992_a.func_177984_a());
            return;
        }
        this.field_72987_B = true;
        BiomeProvider biomeProvider = this.field_73011_w.func_177499_m();
        List<Biome> \u26032 = biomeProvider.func_76932_a();
        Random \u26033 = new Random(this.func_72905_C());
        BlockPos \u26034 = biomeProvider.func_180630_a(0, 0, 256, \u26032, \u26033);
        int \u26035 = 8;
        int \u26036 = this.field_73011_w.func_76557_i();
        int \u26037 = 8;
        if (\u26034 != null) {
            \u26035 = \u26034.func_177958_n();
            \u26037 = \u26034.func_177952_p();
        } else {
            field_147491_a.warn("Unable to find spawn biome");
        }
        int \u26038 = 0;
        while (!this.field_73011_w.func_76566_a(\u26035, \u26037)) {
            \u26035 += \u26033.nextInt(64) - \u26033.nextInt(64);
            \u26037 += \u26033.nextInt(64) - \u26033.nextInt(64);
            if (++\u26038 != 1000) continue;
        }
        this.field_72986_A.func_176143_a(new BlockPos(\u26035, \u26036, \u26037));
        this.field_72987_B = false;
        if (worldSettings.func_77167_c()) {
            this.func_73047_i();
        }
    }

    protected void func_73047_i() {
        WorldGeneratorBonusChest worldGeneratorBonusChest = new WorldGeneratorBonusChest();
        for (int i = 0; i < 10 && !worldGeneratorBonusChest.func_180709_b(this, this.field_73012_v, \u2603 = this.func_175672_r(new BlockPos(\u2603 = this.field_72986_A.func_76079_c() + this.field_73012_v.nextInt(6) - this.field_73012_v.nextInt(6), 0, \u2603 = this.field_72986_A.func_76074_e() + this.field_73012_v.nextInt(6) - this.field_73012_v.nextInt(6))).func_177984_a()); ++i) {
        }
    }

    @Nullable
    public BlockPos func_180504_m() {
        return this.field_73011_w.func_177496_h();
    }

    public void func_73044_a(boolean bl, @Nullable IProgressUpdate iProgressUpdate) throws MinecraftException {
        ChunkProviderServer chunkProviderServer = this.func_72863_F();
        if (!chunkProviderServer.func_73157_c()) {
            return;
        }
        if (iProgressUpdate != null) {
            iProgressUpdate.func_73720_a("Saving level");
        }
        this.func_73042_a();
        if (iProgressUpdate != null) {
            iProgressUpdate.func_73719_c("Saving chunks");
        }
        chunkProviderServer.func_186027_a(bl);
        ArrayList \u26032 = Lists.newArrayList(chunkProviderServer.func_189548_a());
        for (Chunk chunk : \u26032) {
            if (chunk == null || this.field_73063_M.func_152621_a(chunk.field_76635_g, chunk.field_76647_h)) continue;
            chunkProviderServer.func_189549_a(chunk);
        }
    }

    public void func_104140_m() {
        ChunkProviderServer chunkProviderServer = this.func_72863_F();
        if (!chunkProviderServer.func_73157_c()) {
            return;
        }
        chunkProviderServer.func_104112_b();
    }

    protected void func_73042_a() throws MinecraftException {
        this.func_72906_B();
        for (WorldServer worldServer : this.field_73061_a.field_71305_c) {
            if (!(worldServer instanceof WorldServerMulti)) continue;
            ((WorldServerMulti)worldServer).func_184166_c();
        }
        this.field_72986_A.func_176145_a(this.func_175723_af().func_177741_h());
        this.field_72986_A.func_176124_d(this.func_175723_af().func_177731_f());
        this.field_72986_A.func_176141_c(this.func_175723_af().func_177721_g());
        this.field_72986_A.func_176129_e(this.func_175723_af().func_177742_m());
        this.field_72986_A.func_176125_f(this.func_175723_af().func_177727_n());
        this.field_72986_A.func_176122_j(this.func_175723_af().func_177748_q());
        this.field_72986_A.func_176136_k(this.func_175723_af().func_177740_p());
        this.field_72986_A.func_176118_b(this.func_175723_af().func_177751_j());
        this.field_72986_A.func_176135_e(this.func_175723_af().func_177732_i());
        this.field_73019_z.func_75755_a(this.field_72986_A, this.field_73061_a.func_184103_al().func_72378_q());
        this.field_72988_C.func_75744_a();
    }

    @Override
    public boolean func_72838_d(Entity entity) {
        if (this.func_184165_i(entity)) {
            return super.func_72838_d(entity);
        }
        return false;
    }

    @Override
    public void func_175650_b(Collection<Entity> collection) {
        ArrayList arrayList = Lists.newArrayList(collection);
        for (Entity entity : arrayList) {
            if (!this.func_184165_i(entity)) continue;
            this.field_72996_f.add(entity);
            this.func_72923_a(entity);
        }
    }

    private boolean func_184165_i(Entity entity) {
        if (entity.field_70128_L) {
            field_147491_a.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityList.func_191301_a(entity));
            return false;
        }
        UUID uUID = entity.func_110124_au();
        if (this.field_175741_N.containsKey(uUID)) {
            Entity entity2 = this.field_175741_N.get(uUID);
            if (this.field_72997_g.contains(entity2)) {
                this.field_72997_g.remove(entity2);
            } else {
                if (!(entity instanceof EntityPlayer)) {
                    field_147491_a.warn("Keeping entity {} that already exists with UUID {}", (Object)EntityList.func_191301_a(entity2), (Object)uUID.toString());
                    return false;
                }
                field_147491_a.warn("Force-added player with duplicate UUID {}", (Object)uUID.toString());
            }
            this.func_72973_f(entity2);
        }
        return true;
    }

    @Override
    protected void func_72923_a(Entity entity) {
        super.func_72923_a(entity);
        this.field_175729_l.func_76038_a(entity.func_145782_y(), entity);
        this.field_175741_N.put(entity.func_110124_au(), entity);
        Entity[] entityArray = entity.func_70021_al();
        if (entityArray != null) {
            for (Entity entity2 : entityArray) {
                this.field_175729_l.func_76038_a(entity2.func_145782_y(), entity2);
            }
        }
    }

    @Override
    protected void func_72847_b(Entity entity) {
        super.func_72847_b(entity);
        this.field_175729_l.func_76049_d(entity.func_145782_y());
        this.field_175741_N.remove(entity.func_110124_au());
        Entity[] entityArray = entity.func_70021_al();
        if (entityArray != null) {
            for (Entity entity2 : entityArray) {
                this.field_175729_l.func_76049_d(entity2.func_145782_y());
            }
        }
    }

    @Override
    public boolean func_72942_c(Entity entity) {
        if (super.func_72942_c(entity)) {
            this.field_73061_a.func_184103_al().func_148543_a(null, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, 512.0, this.field_73011_w.func_186058_p().func_186068_a(), new SPacketSpawnGlobalEntity(entity));
            return true;
        }
        return false;
    }

    @Override
    public void func_72960_a(Entity entity, byte by) {
        this.func_73039_n().func_151248_b(entity, new SPacketEntityStatus(entity, by));
    }

    @Override
    public ChunkProviderServer func_72863_F() {
        return (ChunkProviderServer)super.func_72863_F();
    }

    @Override
    public Explosion func_72885_a(@Nullable Entity entity, double d, double d2, double d3, float f, boolean bl, boolean bl2) {
        Explosion explosion = new Explosion(this, entity, d, d2, d3, f, bl, bl2);
        explosion.func_77278_a();
        explosion.func_77279_a(false);
        if (!bl2) {
            explosion.func_180342_d();
        }
        for (EntityPlayer entityPlayer : this.field_73010_i) {
            if (!(entityPlayer.func_70092_e(d, d2, d3) < 4096.0)) continue;
            ((EntityPlayerMP)entityPlayer).field_71135_a.func_147359_a(new SPacketExplosion(d, d2, d3, f, explosion.func_180343_e(), explosion.func_77277_b().get(entityPlayer)));
        }
        return explosion;
    }

    @Override
    public void func_175641_c(BlockPos blockPos, Block block, int n, int n2) {
        BlockEventData blockEventData = new BlockEventData(blockPos, block, n, n2);
        for (BlockEventData blockEventData2 : this.field_147490_S[this.field_147489_T]) {
            if (!blockEventData2.equals(blockEventData)) continue;
            return;
        }
        this.field_147490_S[this.field_147489_T].add(blockEventData);
    }

    private void func_147488_Z() {
        while (!this.field_147490_S[this.field_147489_T].isEmpty()) {
            int n = this.field_147489_T;
            this.field_147489_T ^= 1;
            for (BlockEventData blockEventData : this.field_147490_S[n]) {
                if (!this.func_147485_a(blockEventData)) continue;
                this.field_73061_a.func_184103_al().func_148543_a(null, blockEventData.func_180328_a().func_177958_n(), blockEventData.func_180328_a().func_177956_o(), blockEventData.func_180328_a().func_177952_p(), 64.0, this.field_73011_w.func_186058_p().func_186068_a(), new SPacketBlockAction(blockEventData.func_180328_a(), blockEventData.func_151337_f(), blockEventData.func_151339_d(), blockEventData.func_151338_e()));
            }
            this.field_147490_S[n].clear();
        }
    }

    private boolean func_147485_a(BlockEventData blockEventData) {
        IBlockState iBlockState = this.func_180495_p(blockEventData.func_180328_a());
        if (iBlockState.func_177230_c() == blockEventData.func_151337_f()) {
            return iBlockState.func_189547_a(this, blockEventData.func_180328_a(), blockEventData.func_151339_d(), blockEventData.func_151338_e());
        }
        return false;
    }

    public void func_73041_k() {
        this.field_73019_z.func_75759_a();
    }

    @Override
    protected void func_72979_l() {
        boolean bl = this.func_72896_J();
        super.func_72979_l();
        if (this.field_73003_n != this.field_73004_o) {
            this.field_73061_a.func_184103_al().func_148537_a(new SPacketChangeGameState(7, this.field_73004_o), this.field_73011_w.func_186058_p().func_186068_a());
        }
        if (this.field_73018_p != this.field_73017_q) {
            this.field_73061_a.func_184103_al().func_148537_a(new SPacketChangeGameState(8, this.field_73017_q), this.field_73011_w.func_186058_p().func_186068_a());
        }
        if (bl != this.func_72896_J()) {
            if (bl) {
                this.field_73061_a.func_184103_al().func_148540_a(new SPacketChangeGameState(2, 0.0f));
            } else {
                this.field_73061_a.func_184103_al().func_148540_a(new SPacketChangeGameState(1, 0.0f));
            }
            this.field_73061_a.func_184103_al().func_148540_a(new SPacketChangeGameState(7, this.field_73004_o));
            this.field_73061_a.func_184103_al().func_148540_a(new SPacketChangeGameState(8, this.field_73017_q));
        }
    }

    @Override
    @Nullable
    public MinecraftServer func_73046_m() {
        return this.field_73061_a;
    }

    public EntityTracker func_73039_n() {
        return this.field_73062_L;
    }

    public PlayerChunkMap func_184164_w() {
        return this.field_73063_M;
    }

    public Teleporter func_85176_s() {
        return this.field_85177_Q;
    }

    public TemplateManager func_184163_y() {
        return this.field_73019_z.func_186340_h();
    }

    public void func_175739_a(EnumParticleTypes enumParticleTypes, double d, double d2, double d3, int n, double d4, double d5, double d6, double d7, int ... nArray) {
        this.func_180505_a(enumParticleTypes, false, d, d2, d3, n, d4, d5, d6, d7, nArray);
    }

    public void func_180505_a(EnumParticleTypes enumParticleTypes, boolean bl, double d, double d2, double d3, int n, double d4, double d5, double d6, double d7, int ... nArray) {
        SPacketParticles sPacketParticles = new SPacketParticles(enumParticleTypes, bl, (float)d, (float)d2, (float)d3, (float)d4, (float)d5, (float)d6, (float)d7, n, nArray);
        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.field_73010_i.get(i);
            this.func_184159_a(entityPlayerMP, bl, d, d2, d3, sPacketParticles);
        }
    }

    public void func_184161_a(EntityPlayerMP entityPlayerMP, EnumParticleTypes enumParticleTypes, boolean bl, double d, double d2, double d3, int n, double d4, double d5, double d6, double d7, int ... nArray) {
        SPacketParticles sPacketParticles = new SPacketParticles(enumParticleTypes, bl, (float)d, (float)d2, (float)d3, (float)d4, (float)d5, (float)d6, (float)d7, n, nArray);
        this.func_184159_a(entityPlayerMP, bl, d, d2, d3, sPacketParticles);
    }

    private void func_184159_a(EntityPlayerMP entityPlayerMP, boolean bl, double d, double d2, double d3, Packet<?> packet) {
        BlockPos blockPos = entityPlayerMP.func_180425_c();
        double \u26032 = blockPos.func_177954_c(d, d2, d3);
        if (\u26032 <= 1024.0 || bl && \u26032 <= 262144.0) {
            entityPlayerMP.field_71135_a.func_147359_a(packet);
        }
    }

    @Nullable
    public Entity func_175733_a(UUID uUID) {
        return this.field_175741_N.get(uUID);
    }

    @Override
    public ListenableFuture<Object> func_152344_a(Runnable runnable) {
        return this.field_73061_a.func_152344_a(runnable);
    }

    @Override
    public boolean func_152345_ab() {
        return this.field_73061_a.func_152345_ab();
    }

    @Override
    @Nullable
    public BlockPos func_190528_a(String string, BlockPos blockPos, boolean bl) {
        return this.func_72863_F().func_180513_a(this, string, blockPos, bl);
    }

    public AdvancementManager func_191952_z() {
        return this.field_191951_C;
    }

    public FunctionManager func_193037_A() {
        return this.field_193036_D;
    }

    @Override
    public /* synthetic */ IChunkProvider func_72863_F() {
        return this.func_72863_F();
    }

    static class ServerBlockEventList
    extends ArrayList<BlockEventData> {
        private ServerBlockEventList() {
        }
    }
}

