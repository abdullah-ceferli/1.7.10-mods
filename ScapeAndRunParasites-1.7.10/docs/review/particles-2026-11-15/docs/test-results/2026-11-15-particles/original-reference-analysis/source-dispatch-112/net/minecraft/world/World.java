/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.pathfinding.PathWorldListener;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.loot.LootTableManager;

public abstract class World
implements IBlockAccess {
    private int field_181546_a = 63;
    protected boolean field_72999_e;
    public final List<Entity> field_72996_f = Lists.newArrayList();
    protected final List<Entity> field_72997_g = Lists.newArrayList();
    public final List<TileEntity> field_147482_g = Lists.newArrayList();
    public final List<TileEntity> field_175730_i = Lists.newArrayList();
    private final List<TileEntity> field_147484_a = Lists.newArrayList();
    private final List<TileEntity> field_147483_b = Lists.newArrayList();
    public final List<EntityPlayer> field_73010_i = Lists.newArrayList();
    public final List<Entity> field_73007_j = Lists.newArrayList();
    protected final IntHashMap<Entity> field_175729_l = new IntHashMap();
    private final long field_73001_c = 0xFFFFFFL;
    private int field_73008_k;
    protected int field_73005_l = new Random().nextInt();
    protected final int field_73006_m = 1013904223;
    protected float field_73003_n;
    protected float field_73004_o;
    protected float field_73018_p;
    protected float field_73017_q;
    private int field_73016_r;
    public final Random field_73012_v = new Random();
    public final WorldProvider field_73011_w;
    protected PathWorldListener field_184152_t = new PathWorldListener();
    protected List<IWorldEventListener> field_73021_x = Lists.newArrayList((Object[])new IWorldEventListener[]{this.field_184152_t});
    protected IChunkProvider field_73020_y;
    protected final ISaveHandler field_73019_z;
    protected WorldInfo field_72986_A;
    protected boolean field_72987_B;
    protected MapStorage field_72988_C;
    protected VillageCollection field_72982_D;
    protected LootTableManager field_184151_B;
    protected AdvancementManager field_191951_C;
    protected FunctionManager field_193036_D;
    public final Profiler field_72984_F;
    private final Calendar field_83016_L = Calendar.getInstance();
    protected Scoreboard field_96442_D = new Scoreboard();
    public final boolean field_72995_K;
    protected boolean field_72985_G = true;
    protected boolean field_72992_H = true;
    private boolean field_147481_N;
    private final WorldBorder field_175728_M;
    int[] field_72994_J = new int[32768];

    protected World(ISaveHandler iSaveHandler, WorldInfo worldInfo, WorldProvider worldProvider, Profiler profiler, boolean bl) {
        this.field_73019_z = iSaveHandler;
        this.field_72984_F = profiler;
        this.field_72986_A = worldInfo;
        this.field_73011_w = worldProvider;
        this.field_72995_K = bl;
        this.field_175728_M = worldProvider.func_177501_r();
    }

    public World func_175643_b() {
        return this;
    }

    @Override
    public Biome func_180494_b(final BlockPos blockPos) {
        if (this.func_175667_e(blockPos)) {
            Chunk chunk = this.func_175726_f(blockPos);
            try {
                return chunk.func_177411_a(blockPos, this.field_73011_w.func_177499_m());
            }
            catch (Throwable \u26032) {
                CrashReport crashReport = CrashReport.func_85055_a(\u26032, "Getting biome");
                CrashReportCategory \u26033 = crashReport.func_85058_a("Coordinates of biome request");
                \u26033.func_189529_a("Location", new ICrashReportDetail<String>(){

                    @Override
                    public String call() throws Exception {
                        return CrashReportCategory.func_180522_a(blockPos);
                    }

                    @Override
                    public /* synthetic */ Object call() throws Exception {
                        return this.call();
                    }
                });
                throw new ReportedException(crashReport);
            }
        }
        return this.field_73011_w.func_177499_m().func_180300_a(blockPos, Biomes.field_76772_c);
    }

    public BiomeProvider func_72959_q() {
        return this.field_73011_w.func_177499_m();
    }

    protected abstract IChunkProvider func_72970_h();

    public void func_72963_a(WorldSettings worldSettings) {
        this.field_72986_A.func_76091_d(true);
    }

    @Nullable
    public MinecraftServer func_73046_m() {
        return null;
    }

    public void func_72974_f() {
        this.func_175652_B(new BlockPos(8, 64, 8));
    }

    public IBlockState func_184141_c(BlockPos blockPos) {
        \u2603 = new BlockPos(blockPos.func_177958_n(), this.func_181545_F(), blockPos.func_177952_p());
        while (!this.func_175623_d(\u2603.func_177984_a())) {
            \u2603 = \u2603.func_177984_a();
        }
        return this.func_180495_p(\u2603);
    }

    private boolean func_175701_a(BlockPos blockPos) {
        return !this.func_189509_E(blockPos) && blockPos.func_177958_n() >= -30000000 && blockPos.func_177952_p() >= -30000000 && blockPos.func_177958_n() < 30000000 && blockPos.func_177952_p() < 30000000;
    }

    private boolean func_189509_E(BlockPos blockPos) {
        return blockPos.func_177956_o() < 0 || blockPos.func_177956_o() >= 256;
    }

    @Override
    public boolean func_175623_d(BlockPos blockPos) {
        return this.func_180495_p(blockPos).func_185904_a() == Material.field_151579_a;
    }

    public boolean func_175667_e(BlockPos blockPos) {
        return this.func_175668_a(blockPos, true);
    }

    public boolean func_175668_a(BlockPos blockPos, boolean bl) {
        return this.func_175680_a(blockPos.func_177958_n() >> 4, blockPos.func_177952_p() >> 4, bl);
    }

    public boolean func_175697_a(BlockPos blockPos, int n) {
        return this.func_175648_a(blockPos, n, true);
    }

    public boolean func_175648_a(BlockPos blockPos, int n, boolean bl) {
        return this.func_175663_a(blockPos.func_177958_n() - n, blockPos.func_177956_o() - n, blockPos.func_177952_p() - n, blockPos.func_177958_n() + n, blockPos.func_177956_o() + n, blockPos.func_177952_p() + n, bl);
    }

    public boolean func_175707_a(BlockPos blockPos, BlockPos blockPos2) {
        return this.func_175706_a(blockPos, blockPos2, true);
    }

    public boolean func_175706_a(BlockPos blockPos, BlockPos blockPos2, boolean bl) {
        return this.func_175663_a(blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p(), blockPos2.func_177958_n(), blockPos2.func_177956_o(), blockPos2.func_177952_p(), bl);
    }

    public boolean func_175711_a(StructureBoundingBox structureBoundingBox) {
        return this.func_175639_b(structureBoundingBox, true);
    }

    public boolean func_175639_b(StructureBoundingBox structureBoundingBox, boolean bl) {
        return this.func_175663_a(structureBoundingBox.field_78897_a, structureBoundingBox.field_78895_b, structureBoundingBox.field_78896_c, structureBoundingBox.field_78893_d, structureBoundingBox.field_78894_e, structureBoundingBox.field_78892_f, bl);
    }

    private boolean func_175663_a(int n, int n2, int n3, int n4, int n5, int n6, boolean bl) {
        if (n5 < 0 || n2 >= 256) {
            return false;
        }
        n3 >>= 4;
        n4 >>= 4;
        n6 >>= 4;
        for (int i = n >>= 4; i <= n4; ++i) {
            for (\u2603 = n3; \u2603 <= n6; ++\u2603) {
                if (this.func_175680_a(i, \u2603, bl)) continue;
                return false;
            }
        }
        return true;
    }

    protected abstract boolean func_175680_a(int var1, int var2, boolean var3);

    public Chunk func_175726_f(BlockPos blockPos) {
        return this.func_72964_e(blockPos.func_177958_n() >> 4, blockPos.func_177952_p() >> 4);
    }

    public Chunk func_72964_e(int n, int n2) {
        return this.field_73020_y.func_186025_d(n, n2);
    }

    public boolean func_190526_b(int n, int n2) {
        if (this.func_175680_a(n, n2, false)) {
            return true;
        }
        return this.field_73020_y.func_191062_e(n, n2);
    }

    public boolean func_180501_a(BlockPos blockPos, IBlockState iBlockState, int n) {
        if (this.func_189509_E(blockPos)) {
            return false;
        }
        if (!this.field_72995_K && this.field_72986_A.func_76067_t() == WorldType.field_180272_g) {
            return false;
        }
        Chunk chunk = this.func_175726_f(blockPos);
        Block \u26032 = iBlockState.func_177230_c();
        IBlockState \u26033 = chunk.func_177436_a(blockPos, iBlockState);
        if (\u26033 != null) {
            if (iBlockState.func_185891_c() != \u26033.func_185891_c() || iBlockState.func_185906_d() != \u26033.func_185906_d()) {
                this.field_72984_F.func_76320_a("checkLight");
                this.func_175664_x(blockPos);
                this.field_72984_F.func_76319_b();
            }
            if ((n & 2) != 0 && (!this.field_72995_K || (n & 4) == 0) && chunk.func_150802_k()) {
                this.func_184138_a(blockPos, \u26033, iBlockState, n);
            }
            if (!this.field_72995_K && (n & 1) != 0) {
                this.func_175722_b(blockPos, \u26033.func_177230_c(), true);
                if (iBlockState.func_185912_n()) {
                    this.func_175666_e(blockPos, \u26032);
                }
            } else if (!this.field_72995_K && (n & 0x10) == 0) {
                this.func_190522_c(blockPos, \u26032);
            }
            return true;
        }
        return false;
    }

    public boolean func_175698_g(BlockPos blockPos) {
        return this.func_180501_a(blockPos, Blocks.field_150350_a.func_176223_P(), 3);
    }

    public boolean func_175655_b(BlockPos blockPos, boolean bl) {
        IBlockState iBlockState = this.func_180495_p(blockPos);
        Block \u26032 = iBlockState.func_177230_c();
        if (iBlockState.func_185904_a() == Material.field_151579_a) {
            return false;
        }
        this.func_175718_b(2001, blockPos, Block.func_176210_f(iBlockState));
        if (bl) {
            \u26032.func_176226_b(this, blockPos, iBlockState, 0);
        }
        return this.func_180501_a(blockPos, Blocks.field_150350_a.func_176223_P(), 3);
    }

    public boolean func_175656_a(BlockPos blockPos, IBlockState iBlockState) {
        return this.func_180501_a(blockPos, iBlockState, 3);
    }

    public void func_184138_a(BlockPos blockPos, IBlockState iBlockState, IBlockState iBlockState2, int n) {
        for (\u2603 = 0; \u2603 < this.field_73021_x.size(); ++\u2603) {
            this.field_73021_x.get(\u2603).func_184376_a(this, blockPos, iBlockState, iBlockState2, n);
        }
    }

    public void func_175722_b(BlockPos blockPos, Block block, boolean bl) {
        if (this.field_72986_A.func_76067_t() != WorldType.field_180272_g) {
            this.func_175685_c(blockPos, block, bl);
        }
    }

    public void func_72975_g(int n, int n2, int n3, int n4) {
        if (n3 > n4) {
            \u2603 = n4;
            n4 = n3;
            n3 = \u2603;
        }
        if (this.field_73011_w.func_191066_m()) {
            for (\u2603 = n3; \u2603 <= n4; ++\u2603) {
                this.func_180500_c(EnumSkyBlock.SKY, new BlockPos(n, \u2603, n2));
            }
        }
        this.func_147458_c(n, n3, n2, n, n4, n2);
    }

    public void func_175704_b(BlockPos blockPos, BlockPos blockPos2) {
        this.func_147458_c(blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p(), blockPos2.func_177958_n(), blockPos2.func_177956_o(), blockPos2.func_177952_p());
    }

    public void func_147458_c(int n, int n2, int n3, int n4, int n5, int n6) {
        for (\u2603 = 0; \u2603 < this.field_73021_x.size(); ++\u2603) {
            this.field_73021_x.get(\u2603).func_147585_a(n, n2, n3, n4, n5, n6);
        }
    }

    public void func_190522_c(BlockPos blockPos, Block block) {
        this.func_190529_b(blockPos.func_177976_e(), block, blockPos);
        this.func_190529_b(blockPos.func_177974_f(), block, blockPos);
        this.func_190529_b(blockPos.func_177977_b(), block, blockPos);
        this.func_190529_b(blockPos.func_177984_a(), block, blockPos);
        this.func_190529_b(blockPos.func_177978_c(), block, blockPos);
        this.func_190529_b(blockPos.func_177968_d(), block, blockPos);
    }

    public void func_175685_c(BlockPos blockPos, Block block, boolean bl) {
        this.func_190524_a(blockPos.func_177976_e(), block, blockPos);
        this.func_190524_a(blockPos.func_177974_f(), block, blockPos);
        this.func_190524_a(blockPos.func_177977_b(), block, blockPos);
        this.func_190524_a(blockPos.func_177984_a(), block, blockPos);
        this.func_190524_a(blockPos.func_177978_c(), block, blockPos);
        this.func_190524_a(blockPos.func_177968_d(), block, blockPos);
        if (bl) {
            this.func_190522_c(blockPos, block);
        }
    }

    public void func_175695_a(BlockPos blockPos, Block block, EnumFacing enumFacing) {
        if (enumFacing != EnumFacing.WEST) {
            this.func_190524_a(blockPos.func_177976_e(), block, blockPos);
        }
        if (enumFacing != EnumFacing.EAST) {
            this.func_190524_a(blockPos.func_177974_f(), block, blockPos);
        }
        if (enumFacing != EnumFacing.DOWN) {
            this.func_190524_a(blockPos.func_177977_b(), block, blockPos);
        }
        if (enumFacing != EnumFacing.UP) {
            this.func_190524_a(blockPos.func_177984_a(), block, blockPos);
        }
        if (enumFacing != EnumFacing.NORTH) {
            this.func_190524_a(blockPos.func_177978_c(), block, blockPos);
        }
        if (enumFacing != EnumFacing.SOUTH) {
            this.func_190524_a(blockPos.func_177968_d(), block, blockPos);
        }
    }

    public void func_190524_a(BlockPos blockPos, final Block block, BlockPos blockPos2) {
        if (this.field_72995_K) {
            return;
        }
        IBlockState iBlockState = this.func_180495_p(blockPos);
        try {
            iBlockState.func_189546_a(this, blockPos, block, blockPos2);
        }
        catch (Throwable \u26032) {
            CrashReport crashReport = CrashReport.func_85055_a(\u26032, "Exception while updating neighbours");
            CrashReportCategory \u26033 = crashReport.func_85058_a("Block being updated");
            \u26033.func_189529_a("Source block type", new ICrashReportDetail<String>(){

                @Override
                public String call() throws Exception {
                    try {
                        return String.format("ID #%d (%s // %s)", Block.func_149682_b(block), block.func_149739_a(), block.getClass().getCanonicalName());
                    }
                    catch (Throwable throwable) {
                        return "ID #" + Block.func_149682_b(block);
                    }
                }

                @Override
                public /* synthetic */ Object call() throws Exception {
                    return this.call();
                }
            });
            CrashReportCategory.func_175750_a(\u26033, blockPos, iBlockState);
            throw new ReportedException(crashReport);
        }
    }

    public void func_190529_b(BlockPos blockPos, final Block block, BlockPos blockPos2) {
        if (this.field_72995_K) {
            return;
        }
        IBlockState iBlockState = this.func_180495_p(blockPos);
        if (iBlockState.func_177230_c() != Blocks.field_190976_dk) {
            return;
        }
        try {
            ((BlockObserver)iBlockState.func_177230_c()).func_190962_b(iBlockState, this, blockPos, block, blockPos2);
        }
        catch (Throwable \u26032) {
            CrashReport crashReport = CrashReport.func_85055_a(\u26032, "Exception while updating neighbours");
            CrashReportCategory \u26033 = crashReport.func_85058_a("Block being updated");
            \u26033.func_189529_a("Source block type", new ICrashReportDetail<String>(){

                @Override
                public String call() throws Exception {
                    try {
                        return String.format("ID #%d (%s // %s)", Block.func_149682_b(block), block.func_149739_a(), block.getClass().getCanonicalName());
                    }
                    catch (Throwable throwable) {
                        return "ID #" + Block.func_149682_b(block);
                    }
                }

                @Override
                public /* synthetic */ Object call() throws Exception {
                    return this.call();
                }
            });
            CrashReportCategory.func_175750_a(\u26033, blockPos, iBlockState);
            throw new ReportedException(crashReport);
        }
    }

    public boolean func_175691_a(BlockPos blockPos, Block block) {
        return false;
    }

    public boolean func_175678_i(BlockPos blockPos) {
        return this.func_175726_f(blockPos).func_177444_d(blockPos);
    }

    public boolean func_175710_j(BlockPos blockPos) {
        if (blockPos.func_177956_o() >= this.func_181545_F()) {
            return this.func_175678_i(blockPos);
        }
        \u26032 = new BlockPos(blockPos.func_177958_n(), this.func_181545_F(), blockPos.func_177952_p());
        if (!this.func_175678_i(\u26032)) {
            return false;
        }
        \u26032 = \u26032.func_177977_b();
        while (\u26032.func_177956_o() > blockPos.func_177956_o()) {
            IBlockState iBlockState = this.func_180495_p(\u26032);
            if (iBlockState.func_185891_c() > 0 && !iBlockState.func_185904_a().func_76224_d()) {
                return false;
            }
            BlockPos \u26032 = \u26032.func_177977_b();
        }
        return true;
    }

    public int func_175699_k(BlockPos blockPos) {
        if (blockPos.func_177956_o() < 0) {
            return 0;
        }
        if (blockPos.func_177956_o() >= 256) {
            blockPos = new BlockPos(blockPos.func_177958_n(), 255, blockPos.func_177952_p());
        }
        return this.func_175726_f(blockPos).func_177443_a(blockPos, 0);
    }

    public int func_175671_l(BlockPos blockPos) {
        return this.func_175721_c(blockPos, true);
    }

    public int func_175721_c(BlockPos blockPos2, boolean bl) {
        BlockPos blockPos2;
        if (blockPos2.func_177958_n() < -30000000 || blockPos2.func_177952_p() < -30000000 || blockPos2.func_177958_n() >= 30000000 || blockPos2.func_177952_p() >= 30000000) {
            return 15;
        }
        if (bl && this.func_180495_p(blockPos2).func_185916_f()) {
            int n = this.func_175721_c(blockPos2.func_177984_a(), false);
            \u2603 = this.func_175721_c(blockPos2.func_177974_f(), false);
            \u2603 = this.func_175721_c(blockPos2.func_177976_e(), false);
            \u2603 = this.func_175721_c(blockPos2.func_177968_d(), false);
            \u2603 = this.func_175721_c(blockPos2.func_177978_c(), false);
            if (\u2603 > n) {
                n = \u2603;
            }
            if (\u2603 > n) {
                n = \u2603;
            }
            if (\u2603 > n) {
                n = \u2603;
            }
            if (\u2603 > n) {
                n = \u2603;
            }
            return n;
        }
        if (blockPos2.func_177956_o() < 0) {
            return 0;
        }
        if (blockPos2.func_177956_o() >= 256) {
            blockPos2 = new BlockPos(blockPos2.func_177958_n(), 255, blockPos2.func_177952_p());
        }
        Chunk \u26032 = this.func_175726_f(blockPos2);
        return \u26032.func_177443_a(blockPos2, this.field_73008_k);
    }

    public BlockPos func_175645_m(BlockPos blockPos) {
        return new BlockPos(blockPos.func_177958_n(), this.func_189649_b(blockPos.func_177958_n(), blockPos.func_177952_p()), blockPos.func_177952_p());
    }

    public int func_189649_b(int n, int n2) {
        \u2603 = n < -30000000 || n2 < -30000000 || n >= 30000000 || n2 >= 30000000 ? this.func_181545_F() + 1 : (this.func_175680_a(n >> 4, n2 >> 4, true) ? this.func_72964_e(n >> 4, n2 >> 4).func_76611_b(n & 0xF, n2 & 0xF) : 0);
        return \u2603;
    }

    @Deprecated
    public int func_82734_g(int n, int n2) {
        if (n < -30000000 || n2 < -30000000 || n >= 30000000 || n2 >= 30000000) {
            return this.func_181545_F() + 1;
        }
        if (!this.func_175680_a(n >> 4, n2 >> 4, true)) {
            return 0;
        }
        Chunk chunk = this.func_72964_e(n >> 4, n2 >> 4);
        return chunk.func_177442_v();
    }

    public int func_175705_a(EnumSkyBlock enumSkyBlock, BlockPos blockPos2) {
        BlockPos blockPos2;
        if (!this.field_73011_w.func_191066_m() && enumSkyBlock == EnumSkyBlock.SKY) {
            return 0;
        }
        if (blockPos2.func_177956_o() < 0) {
            blockPos2 = new BlockPos(blockPos2.func_177958_n(), 0, blockPos2.func_177952_p());
        }
        if (!this.func_175701_a(blockPos2)) {
            return enumSkyBlock.field_77198_c;
        }
        if (!this.func_175667_e(blockPos2)) {
            return enumSkyBlock.field_77198_c;
        }
        if (this.func_180495_p(blockPos2).func_185916_f()) {
            int n = this.func_175642_b(enumSkyBlock, blockPos2.func_177984_a());
            \u2603 = this.func_175642_b(enumSkyBlock, blockPos2.func_177974_f());
            \u2603 = this.func_175642_b(enumSkyBlock, blockPos2.func_177976_e());
            \u2603 = this.func_175642_b(enumSkyBlock, blockPos2.func_177968_d());
            \u2603 = this.func_175642_b(enumSkyBlock, blockPos2.func_177978_c());
            if (\u2603 > n) {
                n = \u2603;
            }
            if (\u2603 > n) {
                n = \u2603;
            }
            if (\u2603 > n) {
                n = \u2603;
            }
            if (\u2603 > n) {
                n = \u2603;
            }
            return n;
        }
        Chunk \u26032 = this.func_175726_f(blockPos2);
        return \u26032.func_177413_a(enumSkyBlock, blockPos2);
    }

    public int func_175642_b(EnumSkyBlock enumSkyBlock, BlockPos blockPos) {
        if (blockPos.func_177956_o() < 0) {
            blockPos = new BlockPos(blockPos.func_177958_n(), 0, blockPos.func_177952_p());
        }
        if (!this.func_175701_a(blockPos)) {
            return enumSkyBlock.field_77198_c;
        }
        if (!this.func_175667_e(blockPos)) {
            return enumSkyBlock.field_77198_c;
        }
        Chunk chunk = this.func_175726_f(blockPos);
        return chunk.func_177413_a(enumSkyBlock, blockPos);
    }

    public void func_175653_a(EnumSkyBlock enumSkyBlock, BlockPos blockPos, int n) {
        if (!this.func_175701_a(blockPos)) {
            return;
        }
        if (!this.func_175667_e(blockPos)) {
            return;
        }
        Chunk chunk = this.func_175726_f(blockPos);
        chunk.func_177431_a(enumSkyBlock, blockPos, n);
        this.func_175679_n(blockPos);
    }

    public void func_175679_n(BlockPos blockPos) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            this.field_73021_x.get(i).func_174959_b(blockPos);
        }
    }

    @Override
    public int func_175626_b(BlockPos blockPos, int n) {
        \u2603 = this.func_175705_a(EnumSkyBlock.SKY, blockPos);
        \u2603 = this.func_175705_a(EnumSkyBlock.BLOCK, blockPos);
        if (\u2603 < n) {
            \u2603 = n;
        }
        return \u2603 << 20 | \u2603 << 4;
    }

    public float func_175724_o(BlockPos blockPos) {
        return this.field_73011_w.func_177497_p()[this.func_175671_l(blockPos)];
    }

    @Override
    public IBlockState func_180495_p(BlockPos blockPos) {
        if (this.func_189509_E(blockPos)) {
            return Blocks.field_150350_a.func_176223_P();
        }
        Chunk chunk = this.func_175726_f(blockPos);
        return chunk.func_177435_g(blockPos);
    }

    public boolean func_72935_r() {
        return this.field_73008_k < 4;
    }

    @Nullable
    public RayTraceResult func_72933_a(Vec3d vec3d, Vec3d vec3d2) {
        return this.func_147447_a(vec3d, vec3d2, false, false, false);
    }

    @Nullable
    public RayTraceResult func_72901_a(Vec3d vec3d, Vec3d vec3d2, boolean bl) {
        return this.func_147447_a(vec3d, vec3d2, bl, false, false);
    }

    @Nullable
    public RayTraceResult func_147447_a(Vec3d \u2603142, Vec3d vec3d, boolean bl, boolean bl2, boolean bl32) {
        boolean bl32;
        if (Double.isNaN(\u2603142.field_72450_a) || Double.isNaN(\u2603142.field_72448_b) || Double.isNaN(\u2603142.field_72449_c)) {
            return null;
        }
        if (Double.isNaN(vec3d.field_72450_a) || Double.isNaN(vec3d.field_72448_b) || Double.isNaN(vec3d.field_72449_c)) {
            return null;
        }
        int n = MathHelper.func_76128_c(vec3d.field_72450_a);
        n2 = MathHelper.func_76128_c(vec3d.field_72448_b);
        \u2603 = MathHelper.func_76128_c(vec3d.field_72449_c);
        \u2603 = MathHelper.func_76128_c(\u2603142.field_72450_a);
        \u2603 = MathHelper.func_76128_c(\u2603142.field_72448_b);
        \u2603 = MathHelper.func_76128_c(\u2603142.field_72449_c);
        BlockPos \u26032 = new BlockPos(\u2603, \u2603, \u2603);
        Object \u26033 = this.func_180495_p(\u26032);
        Block \u26034 = \u26033.func_177230_c();
        if ((!bl2 || \u26033.func_185890_d(this, \u26032) != Block.field_185506_k) && \u26034.func_176209_a((IBlockState)\u26033, bl) && (\u2603 = \u26033.func_185910_a(this, \u26032, \u2603142, vec3d)) != null) {
            return \u2603;
        }
        \u26033 = null;
        \u2603 = 200;
        while (\u2603-- >= 0) {
            Vec3d \u2603142;
            EnumFacing \u260315;
            if (Double.isNaN(\u2603142.field_72450_a) || Double.isNaN(\u2603142.field_72448_b) || Double.isNaN(\u2603142.field_72449_c)) {
                return null;
            }
            if (\u2603 == n && \u2603 == n2 && \u2603 == \u2603) {
                return bl32 ? \u26033 : null;
            }
            boolean bl4 = true;
            \u2603 = true;
            \u2603 = true;
            double \u26035 = 999.0;
            double \u26036 = 999.0;
            double \u26037 = 999.0;
            if (n > \u2603) {
                \u26035 = (double)\u2603 + 1.0;
            } else if (n < \u2603) {
                \u26035 = (double)\u2603 + 0.0;
            } else {
                bl4 = false;
            }
            if (n2 > \u2603) {
                \u26036 = (double)\u2603 + 1.0;
            } else if (n2 < \u2603) {
                \u26036 = (double)\u2603 + 0.0;
            } else {
                \u2603 = false;
            }
            if (\u2603 > \u2603) {
                \u26037 = (double)\u2603 + 1.0;
            } else if (\u2603 < \u2603) {
                \u26037 = (double)\u2603 + 0.0;
            } else {
                \u2603 = false;
            }
            double \u26038 = 999.0;
            double \u26039 = 999.0;
            double \u260310 = 999.0;
            double \u260311 = vec3d.field_72450_a - \u2603142.field_72450_a;
            double \u260312 = vec3d.field_72448_b - \u2603142.field_72448_b;
            double \u260313 = vec3d.field_72449_c - \u2603142.field_72449_c;
            if (bl4) {
                \u26038 = (\u26035 - \u2603142.field_72450_a) / \u260311;
            }
            if (\u2603) {
                \u26039 = (\u26036 - \u2603142.field_72448_b) / \u260312;
            }
            if (\u2603) {
                \u260310 = (\u26037 - \u2603142.field_72449_c) / \u260313;
            }
            if (\u26038 == -0.0) {
                \u26038 = -1.0E-4;
            }
            if (\u26039 == -0.0) {
                \u26039 = -1.0E-4;
            }
            if (\u260310 == -0.0) {
                \u260310 = -1.0E-4;
            }
            if (\u26038 < \u26039 && \u26038 < \u260310) {
                \u260315 = n > \u2603 ? EnumFacing.WEST : EnumFacing.EAST;
                \u2603142 = new Vec3d(\u26035, \u2603142.field_72448_b + \u260312 * \u26038, \u2603142.field_72449_c + \u260313 * \u26038);
            } else if (\u26039 < \u260310) {
                int n2;
                \u260315 = n2 > \u2603 ? EnumFacing.DOWN : EnumFacing.UP;
                \u2603142 = new Vec3d(\u2603142.field_72450_a + \u260311 * \u26039, \u26036, \u2603142.field_72449_c + \u260313 * \u26039);
            } else {
                \u260315 = \u2603 > \u2603 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                \u2603142 = new Vec3d(\u2603142.field_72450_a + \u260311 * \u260310, \u2603142.field_72448_b + \u260312 * \u260310, \u26037);
            }
            \u2603 = MathHelper.func_76128_c(\u2603142.field_72450_a) - (\u260315 == EnumFacing.EAST ? 1 : 0);
            \u2603 = MathHelper.func_76128_c(\u2603142.field_72448_b) - (\u260315 == EnumFacing.UP ? 1 : 0);
            \u2603 = MathHelper.func_76128_c(\u2603142.field_72449_c) - (\u260315 == EnumFacing.SOUTH ? 1 : 0);
            \u26032 = new BlockPos(\u2603, \u2603, \u2603);
            IBlockState iBlockState = this.func_180495_p(\u26032);
            Block \u260316 = iBlockState.func_177230_c();
            if (bl2 && iBlockState.func_185904_a() != Material.field_151567_E && iBlockState.func_185890_d(this, \u26032) == Block.field_185506_k) continue;
            if (\u260316.func_176209_a(iBlockState, bl)) {
                RayTraceResult rayTraceResult = iBlockState.func_185910_a(this, \u26032, \u2603142, vec3d);
                if (rayTraceResult == null) continue;
                return rayTraceResult;
            }
            \u26033 = new RayTraceResult(RayTraceResult.Type.MISS, \u2603142, \u260315, \u26032);
        }
        return bl32 ? \u26033 : null;
    }

    public void func_184133_a(@Nullable EntityPlayer entityPlayer, BlockPos blockPos, SoundEvent soundEvent, SoundCategory soundCategory, float f, float f2) {
        this.func_184148_a(entityPlayer, (double)blockPos.func_177958_n() + 0.5, (double)blockPos.func_177956_o() + 0.5, (double)blockPos.func_177952_p() + 0.5, soundEvent, soundCategory, f, f2);
    }

    public void func_184148_a(@Nullable EntityPlayer entityPlayer, double d, double d2, double d3, SoundEvent soundEvent, SoundCategory soundCategory, float f, float f2) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            this.field_73021_x.get(i).func_184375_a(entityPlayer, soundEvent, soundCategory, d, d2, d3, f, f2);
        }
    }

    public void func_184134_a(double d, double d2, double d3, SoundEvent soundEvent, SoundCategory soundCategory, float f, float f2, boolean bl) {
    }

    public void func_184149_a(BlockPos blockPos, @Nullable SoundEvent soundEvent) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            this.field_73021_x.get(i).func_184377_a(soundEvent, blockPos);
        }
    }

    public void func_175688_a(EnumParticleTypes enumParticleTypes, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
        this.func_175720_a(enumParticleTypes.func_179348_c(), enumParticleTypes.func_179344_e(), d, d2, d3, d4, d5, d6, nArray);
    }

    public void func_190523_a(int n, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            this.field_73021_x.get(i).func_190570_a(n, false, true, d, d2, d3, d4, d5, d6, nArray);
        }
    }

    public void func_175682_a(EnumParticleTypes enumParticleTypes, boolean bl, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
        this.func_175720_a(enumParticleTypes.func_179348_c(), enumParticleTypes.func_179344_e() || bl, d, d2, d3, d4, d5, d6, nArray);
    }

    private void func_175720_a(int n, boolean bl, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            this.field_73021_x.get(i).func_180442_a(n, bl, d, d2, d3, d4, d5, d6, nArray);
        }
    }

    public boolean func_72942_c(Entity entity) {
        this.field_73007_j.add(entity);
        return true;
    }

    public boolean func_72838_d(Entity entity) {
        int n = MathHelper.func_76128_c(entity.field_70165_t / 16.0);
        \u2603 = MathHelper.func_76128_c(entity.field_70161_v / 16.0);
        boolean \u26032 = entity.field_98038_p;
        if (entity instanceof EntityPlayer) {
            \u26032 = true;
        }
        if (\u26032 || this.func_175680_a(n, \u2603, false)) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer)entity;
                this.field_73010_i.add(entityPlayer);
                this.func_72854_c();
            }
            this.func_72964_e(n, \u2603).func_76612_a(entity);
            this.field_72996_f.add(entity);
            this.func_72923_a(entity);
            return true;
        }
        return false;
    }

    protected void func_72923_a(Entity entity) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            this.field_73021_x.get(i).func_72703_a(entity);
        }
    }

    protected void func_72847_b(Entity entity) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            this.field_73021_x.get(i).func_72709_b(entity);
        }
    }

    public void func_72900_e(Entity entity) {
        if (entity.func_184207_aI()) {
            entity.func_184226_ay();
        }
        if (entity.func_184218_aH()) {
            entity.func_184210_p();
        }
        entity.func_70106_y();
        if (entity instanceof EntityPlayer) {
            this.field_73010_i.remove(entity);
            this.func_72854_c();
            this.func_72847_b(entity);
        }
    }

    public void func_72973_f(Entity entity) {
        entity.func_184174_b(false);
        entity.func_70106_y();
        if (entity instanceof EntityPlayer) {
            this.field_73010_i.remove(entity);
            this.func_72854_c();
        }
        int n = entity.field_70176_ah;
        \u2603 = entity.field_70164_aj;
        if (entity.field_70175_ag && this.func_175680_a(n, \u2603, true)) {
            this.func_72964_e(n, \u2603).func_76622_b(entity);
        }
        this.field_72996_f.remove(entity);
        this.func_72847_b(entity);
    }

    public void func_72954_a(IWorldEventListener iWorldEventListener) {
        this.field_73021_x.add(iWorldEventListener);
    }

    public void func_72848_b(IWorldEventListener iWorldEventListener) {
        this.field_73021_x.remove(iWorldEventListener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean func_191504_a(@Nullable Entity entity, AxisAlignedBB axisAlignedBB, boolean bl, @Nullable List<AxisAlignedBB> list2) {
        List<AxisAlignedBB> list2;
        int n = MathHelper.func_76128_c(axisAlignedBB.field_72340_a) - 1;
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72336_d) + 1;
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72338_b) - 1;
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72337_e) + 1;
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72339_c) - 1;
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72334_f) + 1;
        WorldBorder \u26032 = this.func_175723_af();
        boolean \u26033 = entity != null && entity.func_174832_aS();
        boolean \u26034 = entity != null && this.func_191503_g(entity);
        IBlockState \u26035 = Blocks.field_150348_b.func_176223_P();
        BlockPos.PooledMutableBlockPos \u26036 = BlockPos.PooledMutableBlockPos.func_185346_s();
        try {
            for (\u2603 = n; \u2603 < \u2603; ++\u2603) {
                for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                    boolean bl2 = \u2603 == n || \u2603 == \u2603 - 1;
                    boolean bl3 = \u2603 = \u2603 == \u2603 || \u2603 == \u2603 - 1;
                    if (bl2 && \u2603 || !this.func_175667_e(\u26036.func_181079_c(\u2603, 64, \u2603))) continue;
                    for (int i = \u2603; i < \u2603; ++i) {
                        if ((bl2 || \u2603) && i == \u2603 - 1) continue;
                        if (bl) {
                            if (\u2603 < -30000000 || \u2603 >= 30000000 || \u2603 < -30000000 || \u2603 >= 30000000) {
                                boolean bl4 = true;
                                return bl4;
                            }
                        } else if (entity != null && \u26033 == \u26034) {
                            entity.func_174821_h(!\u26034);
                        }
                        \u26036.func_181079_c(\u2603, i, \u2603);
                        IBlockState iBlockState = bl || \u26032.func_177746_a(\u26036) || !\u26034 ? this.func_180495_p(\u26036) : \u26035;
                        iBlockState.func_185908_a(this, \u26036, axisAlignedBB, list2, entity, false);
                        if (!bl || list2.isEmpty()) continue;
                        boolean bl5 = true;
                        return bl5;
                    }
                }
            }
        }
        finally {
            \u26036.func_185344_t();
        }
        return !list2.isEmpty();
    }

    public List<AxisAlignedBB> func_184144_a(@Nullable Entity entity, AxisAlignedBB axisAlignedBB) {
        ArrayList arrayList = Lists.newArrayList();
        this.func_191504_a(entity, axisAlignedBB, false, arrayList);
        if (entity != null) {
            List<Entity> list = this.func_72839_b(entity, axisAlignedBB.func_186662_g(0.25));
            for (int i = 0; i < list.size(); ++i) {
                Entity entity2 = list.get(i);
                if (entity.func_184223_x(entity2)) continue;
                AxisAlignedBB \u26032 = entity2.func_70046_E();
                if (\u26032 != null && \u26032.func_72326_a(axisAlignedBB)) {
                    arrayList.add(\u26032);
                }
                if ((\u26032 = entity.func_70114_g(entity2)) == null || !\u26032.func_72326_a(axisAlignedBB)) continue;
                arrayList.add(\u26032);
            }
        }
        return arrayList;
    }

    public boolean func_191503_g(Entity entity) {
        double d = this.field_175728_M.func_177726_b();
        \u2603 = this.field_175728_M.func_177736_c();
        \u2603 = this.field_175728_M.func_177728_d();
        \u2603 = this.field_175728_M.func_177733_e();
        if (entity.func_174832_aS()) {
            d += 1.0;
            \u2603 += 1.0;
            \u2603 -= 1.0;
            \u2603 -= 1.0;
        } else {
            d -= 1.0;
            \u2603 -= 1.0;
            \u2603 += 1.0;
            \u2603 += 1.0;
        }
        return entity.field_70165_t > d && entity.field_70165_t < \u2603 && entity.field_70161_v > \u2603 && entity.field_70161_v < \u2603;
    }

    public boolean func_184143_b(AxisAlignedBB axisAlignedBB) {
        return this.func_191504_a(null, axisAlignedBB, true, Lists.newArrayList());
    }

    public int func_72967_a(float f) {
        \u2603 = this.func_72826_c(f);
        \u2603 = 1.0f - (MathHelper.func_76134_b(\u2603 * ((float)Math.PI * 2)) * 2.0f + 0.5f);
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        \u2603 = 1.0f - \u2603;
        \u2603 = (float)((double)\u2603 * (1.0 - (double)(this.func_72867_j(f) * 5.0f) / 16.0));
        \u2603 = (float)((double)\u2603 * (1.0 - (double)(this.func_72819_i(f) * 5.0f) / 16.0));
        \u2603 = 1.0f - \u2603;
        return (int)(\u2603 * 11.0f);
    }

    public float func_72971_b(float f) {
        \u2603 = this.func_72826_c(f);
        \u2603 = 1.0f - (MathHelper.func_76134_b(\u2603 * ((float)Math.PI * 2)) * 2.0f + 0.2f);
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        \u2603 = 1.0f - \u2603;
        \u2603 = (float)((double)\u2603 * (1.0 - (double)(this.func_72867_j(f) * 5.0f) / 16.0));
        \u2603 = (float)((double)\u2603 * (1.0 - (double)(this.func_72819_i(f) * 5.0f) / 16.0));
        return \u2603 * 0.8f + 0.2f;
    }

    public Vec3d func_72833_a(Entity entity, float f) {
        float f2;
        \u2603 = this.func_72826_c(f);
        \u2603 = MathHelper.func_76134_b(\u2603 * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        int n = MathHelper.func_76128_c(entity.field_70165_t);
        \u2603 = MathHelper.func_76128_c(entity.field_70163_u);
        \u2603 = MathHelper.func_76128_c(entity.field_70161_v);
        BlockPos \u26032 = new BlockPos(n, \u2603, \u2603);
        Biome \u26033 = this.func_180494_b(\u26032);
        float \u26034 = \u26033.func_180626_a(\u26032);
        \u2603 = \u26033.func_76731_a(\u26034);
        float \u26035 = (float)(\u2603 >> 16 & 0xFF) / 255.0f;
        float \u26036 = (float)(\u2603 >> 8 & 0xFF) / 255.0f;
        float \u26037 = (float)(\u2603 & 0xFF) / 255.0f;
        \u26035 *= \u2603;
        \u26036 *= \u2603;
        \u26037 *= \u2603;
        float \u26038 = this.func_72867_j(f);
        if (\u26038 > 0.0f) {
            f2 = (\u26035 * 0.3f + \u26036 * 0.59f + \u26037 * 0.11f) * 0.6f;
            \u2603 = 1.0f - \u26038 * 0.75f;
            \u26035 = \u26035 * \u2603 + f2 * (1.0f - \u2603);
            \u26036 = \u26036 * \u2603 + f2 * (1.0f - \u2603);
            \u26037 = \u26037 * \u2603 + f2 * (1.0f - \u2603);
        }
        if ((f2 = this.func_72819_i(f)) > 0.0f) {
            \u2603 = (\u26035 * 0.3f + \u26036 * 0.59f + \u26037 * 0.11f) * 0.2f;
            \u2603 = 1.0f - f2 * 0.75f;
            \u26035 = \u26035 * \u2603 + \u2603 * (1.0f - \u2603);
            \u26036 = \u26036 * \u2603 + \u2603 * (1.0f - \u2603);
            \u26037 = \u26037 * \u2603 + \u2603 * (1.0f - \u2603);
        }
        if (this.field_73016_r > 0) {
            \u2603 = (float)this.field_73016_r - f;
            if (\u2603 > 1.0f) {
                \u2603 = 1.0f;
            }
            \u26035 = \u26035 * (1.0f - (\u2603 *= 0.45f)) + 0.8f * \u2603;
            \u26036 = \u26036 * (1.0f - \u2603) + 0.8f * \u2603;
            \u26037 = \u26037 * (1.0f - \u2603) + 1.0f * \u2603;
        }
        return new Vec3d(\u26035, \u26036, \u26037);
    }

    public float func_72826_c(float f) {
        return this.field_73011_w.func_76563_a(this.field_72986_A.func_76073_f(), f);
    }

    public int func_72853_d() {
        return this.field_73011_w.func_76559_b(this.field_72986_A.func_76073_f());
    }

    public float func_130001_d() {
        return WorldProvider.field_111203_a[this.field_73011_w.func_76559_b(this.field_72986_A.func_76073_f())];
    }

    public float func_72929_e(float f) {
        \u2603 = this.func_72826_c(f);
        return \u2603 * ((float)Math.PI * 2);
    }

    public Vec3d func_72824_f(float f) {
        \u2603 = this.func_72826_c(f);
        \u2603 = MathHelper.func_76134_b(\u2603 * ((float)Math.PI * 2)) * 2.0f + 0.5f;
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        \u2603 = 1.0f;
        \u2603 = 1.0f;
        \u2603 = 1.0f;
        \u2603 = this.func_72867_j(f);
        if (\u2603 > 0.0f) {
            \u2603 = (\u2603 * 0.3f + \u2603 * 0.59f + \u2603 * 0.11f) * 0.6f;
            \u2603 = 1.0f - \u2603 * 0.95f;
            \u2603 = \u2603 * \u2603 + \u2603 * (1.0f - \u2603);
            \u2603 = \u2603 * \u2603 + \u2603 * (1.0f - \u2603);
            \u2603 = \u2603 * \u2603 + \u2603 * (1.0f - \u2603);
        }
        \u2603 *= \u2603 * 0.9f + 0.1f;
        \u2603 *= \u2603 * 0.9f + 0.1f;
        \u2603 *= \u2603 * 0.85f + 0.15f;
        \u2603 = this.func_72819_i(f);
        if (\u2603 > 0.0f) {
            \u2603 = (\u2603 * 0.3f + \u2603 * 0.59f + \u2603 * 0.11f) * 0.2f;
            \u2603 = 1.0f - \u2603 * 0.95f;
            \u2603 = \u2603 * \u2603 + \u2603 * (1.0f - \u2603);
            \u2603 = \u2603 * \u2603 + \u2603 * (1.0f - \u2603);
            \u2603 = \u2603 * \u2603 + \u2603 * (1.0f - \u2603);
        }
        return new Vec3d(\u2603, \u2603, \u2603);
    }

    public Vec3d func_72948_g(float f) {
        \u2603 = this.func_72826_c(f);
        return this.field_73011_w.func_76562_b(\u2603, f);
    }

    public BlockPos func_175725_q(BlockPos blockPos) {
        return this.func_175726_f(blockPos).func_177440_h(blockPos);
    }

    public BlockPos func_175672_r(BlockPos blockPos) {
        Chunk chunk = this.func_175726_f(blockPos);
        BlockPos \u26032 = new BlockPos(blockPos.func_177958_n(), chunk.func_76625_h() + 16, blockPos.func_177952_p());
        while (!(\u26032.func_177956_o() < 0 || (\u2603 = chunk.func_177435_g(\u2603 = \u26032.func_177977_b()).func_185904_a()).func_76230_c() && \u2603 != Material.field_151584_j)) {
            \u26032 = \u2603;
        }
        return \u26032;
    }

    public float func_72880_h(float f) {
        \u2603 = this.func_72826_c(f);
        \u2603 = 1.0f - (MathHelper.func_76134_b(\u2603 * ((float)Math.PI * 2)) * 2.0f + 0.25f);
        \u2603 = MathHelper.func_76131_a(\u2603, 0.0f, 1.0f);
        return \u2603 * \u2603 * 0.5f;
    }

    public boolean func_184145_b(BlockPos blockPos, Block block) {
        return true;
    }

    public void func_175684_a(BlockPos blockPos, Block block, int n) {
    }

    public void func_175654_a(BlockPos blockPos, Block block, int n, int n2) {
    }

    public void func_180497_b(BlockPos blockPos, Block block, int n, int n2) {
    }

    public void func_72939_s() {
        int \u26035;
        Object object;
        int n;
        this.field_72984_F.func_76320_a("entities");
        this.field_72984_F.func_76320_a("global");
        for (n = 0; n < this.field_73007_j.size(); ++n) {
            object = this.field_73007_j.get(n);
            try {
                ++((Entity)object).field_70173_aa;
                ((Entity)object).func_70071_h_();
            }
            catch (Throwable \u26032) {
                CrashReport crashReport = CrashReport.func_85055_a(\u26032, "Ticking entity");
                CrashReportCategory \u26033 = crashReport.func_85058_a("Entity being ticked");
                if (object == null) {
                    \u26033.func_71507_a("Entity", "~~NULL~~");
                } else {
                    ((Entity)object).func_85029_a(\u26033);
                }
                throw new ReportedException(crashReport);
            }
            if (!((Entity)object).field_70128_L) continue;
            this.field_73007_j.remove(n--);
        }
        this.field_72984_F.func_76318_c("remove");
        this.field_72996_f.removeAll(this.field_72997_g);
        for (n = 0; n < this.field_72997_g.size(); ++n) {
            object = this.field_72997_g.get(n);
            int \u26034 = ((Entity)object).field_70176_ah;
            \u26035 = ((Entity)object).field_70164_aj;
            if (!((Entity)object).field_70175_ag || !this.func_175680_a(\u26034, \u26035, true)) continue;
            this.func_72964_e(\u26034, \u26035).func_76622_b((Entity)object);
        }
        for (n = 0; n < this.field_72997_g.size(); ++n) {
            this.func_72847_b(this.field_72997_g.get(n));
        }
        this.field_72997_g.clear();
        this.func_184147_l();
        this.field_72984_F.func_76318_c("regular");
        for (n = 0; n < this.field_72996_f.size(); ++n) {
            object = this.field_72996_f.get(n);
            Entity \u26036 = ((Entity)object).func_184187_bx();
            if (\u26036 != null) {
                if (!\u26036.field_70128_L && \u26036.func_184196_w((Entity)object)) continue;
                ((Entity)object).func_184210_p();
            }
            this.field_72984_F.func_76320_a("tick");
            if (!((Entity)object).field_70128_L && !(object instanceof EntityPlayerMP)) {
                try {
                    this.func_72870_g((Entity)object);
                }
                catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.func_85055_a(throwable, "Ticking entity");
                    CrashReportCategory \u26037 = crashReport.func_85058_a("Entity being ticked");
                    ((Entity)object).func_85029_a(\u26037);
                    throw new ReportedException(crashReport);
                }
            }
            this.field_72984_F.func_76319_b();
            this.field_72984_F.func_76320_a("remove");
            if (((Entity)object).field_70128_L) {
                \u26035 = ((Entity)object).field_70176_ah;
                int n2 = ((Entity)object).field_70164_aj;
                if (((Entity)object).field_70175_ag && this.func_175680_a(\u26035, n2, true)) {
                    this.func_72964_e(\u26035, n2).func_76622_b((Entity)object);
                }
                this.field_72996_f.remove(n--);
                this.func_72847_b((Entity)object);
            }
            this.field_72984_F.func_76319_b();
        }
        this.field_72984_F.func_76318_c("blockEntities");
        if (!this.field_147483_b.isEmpty()) {
            this.field_175730_i.removeAll(this.field_147483_b);
            this.field_147482_g.removeAll(this.field_147483_b);
            this.field_147483_b.clear();
        }
        this.field_147481_N = true;
        Iterator<TileEntity> iterator = this.field_175730_i.iterator();
        while (iterator.hasNext()) {
            object = iterator.next();
            if (!((TileEntity)object).func_145837_r() && ((TileEntity)object).func_145830_o() && this.func_175667_e(\u2603 = ((TileEntity)object).func_174877_v()) && this.field_175728_M.func_177746_a(\u2603)) {
                try {
                    this.field_72984_F.func_194340_a(() -> World.func_194305_c((TileEntity)object));
                    ((ITickable)object).func_73660_a();
                    this.field_72984_F.func_76319_b();
                }
                catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.func_85055_a(throwable, "Ticking block entity");
                    CrashReportCategory \u26038 = crashReport.func_85058_a("Block entity being ticked");
                    ((TileEntity)object).func_145828_a(\u26038);
                    throw new ReportedException(crashReport);
                }
            }
            if (!((TileEntity)object).func_145837_r()) continue;
            iterator.remove();
            this.field_147482_g.remove(object);
            if (!this.func_175667_e(((TileEntity)object).func_174877_v())) continue;
            this.func_175726_f(((TileEntity)object).func_174877_v()).func_177425_e(((TileEntity)object).func_174877_v());
        }
        this.field_147481_N = false;
        this.field_72984_F.func_76318_c("pendingBlockEntities");
        if (!this.field_147484_a.isEmpty()) {
            for (int i = 0; i < this.field_147484_a.size(); ++i) {
                TileEntity tileEntity = this.field_147484_a.get(i);
                if (tileEntity.func_145837_r()) continue;
                if (!this.field_147482_g.contains(tileEntity)) {
                    this.func_175700_a(tileEntity);
                }
                if (!this.func_175667_e(tileEntity.func_174877_v())) continue;
                Chunk \u26039 = this.func_175726_f(tileEntity.func_174877_v());
                IBlockState \u260310 = \u26039.func_177435_g(tileEntity.func_174877_v());
                \u26039.func_177426_a(tileEntity.func_174877_v(), tileEntity);
                this.func_184138_a(tileEntity.func_174877_v(), \u260310, \u260310, 3);
            }
            this.field_147484_a.clear();
        }
        this.field_72984_F.func_76319_b();
        this.field_72984_F.func_76319_b();
    }

    protected void func_184147_l() {
    }

    public boolean func_175700_a(TileEntity tileEntity) {
        boolean bl = this.field_147482_g.add(tileEntity);
        if (bl && tileEntity instanceof ITickable) {
            this.field_175730_i.add(tileEntity);
        }
        if (this.field_72995_K) {
            BlockPos blockPos = tileEntity.func_174877_v();
            IBlockState \u26032 = this.func_180495_p(blockPos);
            this.func_184138_a(blockPos, \u26032, \u26032, 2);
        }
        return bl;
    }

    public void func_147448_a(Collection<TileEntity> collection) {
        if (this.field_147481_N) {
            this.field_147484_a.addAll(collection);
        } else {
            for (TileEntity tileEntity : collection) {
                this.func_175700_a(tileEntity);
            }
        }
    }

    public void func_72870_g(Entity entity) {
        this.func_72866_a(entity, true);
    }

    public void func_72866_a(Entity entity2, boolean bl) {
        Entity entity2;
        int \u26032;
        if (!(entity2 instanceof EntityPlayer)) {
            \u26032 = MathHelper.func_76128_c(entity2.field_70165_t);
            \u26033 = MathHelper.func_76128_c(entity2.field_70161_v);
            \u26034 = 32;
            if (bl && !this.func_175663_a(\u26032 - 32, 0, \u26033 - 32, \u26032 + 32, 0, \u26033 + 32, true)) {
                return;
            }
        }
        entity2.field_70142_S = entity2.field_70165_t;
        entity2.field_70137_T = entity2.field_70163_u;
        entity2.field_70136_U = entity2.field_70161_v;
        entity2.field_70126_B = entity2.field_70177_z;
        entity2.field_70127_C = entity2.field_70125_A;
        if (bl && entity2.field_70175_ag) {
            ++entity2.field_70173_aa;
            if (entity2.func_184218_aH()) {
                entity2.func_70098_U();
            } else {
                entity2.func_70071_h_();
            }
        }
        this.field_72984_F.func_76320_a("chunkCheck");
        if (Double.isNaN(entity2.field_70165_t) || Double.isInfinite(entity2.field_70165_t)) {
            entity2.field_70165_t = entity2.field_70142_S;
        }
        if (Double.isNaN(entity2.field_70163_u) || Double.isInfinite(entity2.field_70163_u)) {
            entity2.field_70163_u = entity2.field_70137_T;
        }
        if (Double.isNaN(entity2.field_70161_v) || Double.isInfinite(entity2.field_70161_v)) {
            entity2.field_70161_v = entity2.field_70136_U;
        }
        if (Double.isNaN(entity2.field_70125_A) || Double.isInfinite(entity2.field_70125_A)) {
            entity2.field_70125_A = entity2.field_70127_C;
        }
        if (Double.isNaN(entity2.field_70177_z) || Double.isInfinite(entity2.field_70177_z)) {
            entity2.field_70177_z = entity2.field_70126_B;
        }
        \u26032 = MathHelper.func_76128_c(entity2.field_70165_t / 16.0);
        int \u26033 = MathHelper.func_76128_c(entity2.field_70163_u / 16.0);
        int \u26034 = MathHelper.func_76128_c(entity2.field_70161_v / 16.0);
        if (!entity2.field_70175_ag || entity2.field_70176_ah != \u26032 || entity2.field_70162_ai != \u26033 || entity2.field_70164_aj != \u26034) {
            if (entity2.field_70175_ag && this.func_175680_a(entity2.field_70176_ah, entity2.field_70164_aj, true)) {
                this.func_72964_e(entity2.field_70176_ah, entity2.field_70164_aj).func_76608_a(entity2, entity2.field_70162_ai);
            }
            if (entity2.func_184189_br() || this.func_175680_a(\u26032, \u26034, true)) {
                this.func_72964_e(\u26032, \u26034).func_76612_a(entity2);
            } else {
                entity2.field_70175_ag = false;
            }
        }
        this.field_72984_F.func_76319_b();
        if (bl && entity2.field_70175_ag) {
            for (Entity entity3 : entity2.func_184188_bt()) {
                if (entity3.field_70128_L || entity3.func_184187_bx() != entity2) {
                    entity3.func_184210_p();
                    continue;
                }
                this.func_72870_g(entity3);
            }
        }
    }

    public boolean func_72855_b(AxisAlignedBB axisAlignedBB) {
        return this.func_72917_a(axisAlignedBB, null);
    }

    public boolean func_72917_a(AxisAlignedBB axisAlignedBB, @Nullable Entity entity) {
        List<Entity> list = this.func_72839_b(null, axisAlignedBB);
        for (int i = 0; i < list.size(); ++i) {
            Entity entity2 = list.get(i);
            if (entity2.field_70128_L || !entity2.field_70156_m || entity2 == entity || entity != null && !entity2.func_184223_x(entity)) continue;
            return false;
        }
        return true;
    }

    public boolean func_72829_c(AxisAlignedBB axisAlignedBB) {
        int n = MathHelper.func_76128_c(axisAlignedBB.field_72340_a);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72336_d);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72338_b);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72337_e);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72339_c);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72334_f);
        BlockPos.PooledMutableBlockPos \u26032 = BlockPos.PooledMutableBlockPos.func_185346_s();
        for (\u2603 = n; \u2603 < \u2603; ++\u2603) {
            for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                    IBlockState iBlockState = this.func_180495_p(\u26032.func_181079_c(\u2603, \u2603, \u2603));
                    if (iBlockState.func_185904_a() == Material.field_151579_a) continue;
                    \u26032.func_185344_t();
                    return true;
                }
            }
        }
        \u26032.func_185344_t();
        return false;
    }

    public boolean func_72953_d(AxisAlignedBB axisAlignedBB) {
        int n = MathHelper.func_76128_c(axisAlignedBB.field_72340_a);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72336_d);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72338_b);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72337_e);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72339_c);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72334_f);
        BlockPos.PooledMutableBlockPos \u26032 = BlockPos.PooledMutableBlockPos.func_185346_s();
        for (\u2603 = n; \u2603 < \u2603; ++\u2603) {
            for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                    IBlockState iBlockState = this.func_180495_p(\u26032.func_181079_c(\u2603, \u2603, \u2603));
                    if (!iBlockState.func_185904_a().func_76224_d()) continue;
                    \u26032.func_185344_t();
                    return true;
                }
            }
        }
        \u26032.func_185344_t();
        return false;
    }

    public boolean func_147470_e(AxisAlignedBB axisAlignedBB) {
        int n = MathHelper.func_76128_c(axisAlignedBB.field_72340_a);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72336_d);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72338_b);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72337_e);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72339_c);
        if (this.func_175663_a(n, \u2603, \u2603, \u2603, \u2603, \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72334_f), true)) {
            BlockPos.PooledMutableBlockPos pooledMutableBlockPos = BlockPos.PooledMutableBlockPos.func_185346_s();
            for (int i = n; i < \u2603; ++i) {
                for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                    for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                        Block block = this.func_180495_p(pooledMutableBlockPos.func_181079_c(i, \u2603, \u2603)).func_177230_c();
                        if (block != Blocks.field_150480_ab && block != Blocks.field_150356_k && block != Blocks.field_150353_l) continue;
                        pooledMutableBlockPos.func_185344_t();
                        return true;
                    }
                }
            }
            pooledMutableBlockPos.func_185344_t();
        }
        return false;
    }

    public boolean func_72918_a(AxisAlignedBB axisAlignedBB, Material material, Entity entity) {
        int n = MathHelper.func_76128_c(axisAlignedBB.field_72340_a);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72336_d);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72338_b);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72337_e);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72339_c);
        if (!this.func_175663_a(n, \u2603, \u2603, \u2603, \u2603, \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72334_f), true)) {
            return false;
        }
        boolean \u26032 = false;
        Vec3d \u26033 = Vec3d.field_186680_a;
        BlockPos.PooledMutableBlockPos \u26034 = BlockPos.PooledMutableBlockPos.func_185346_s();
        for (\u2603 = n; \u2603 < \u2603; ++\u2603) {
            for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                    \u26034.func_181079_c(\u2603, \u2603, \u2603);
                    IBlockState iBlockState = this.func_180495_p(\u26034);
                    Block \u26035 = iBlockState.func_177230_c();
                    if (iBlockState.func_185904_a() != material || !((double)\u2603 >= (\u2603 = (double)((float)(\u2603 + 1) - BlockLiquid.func_149801_b(iBlockState.func_177229_b(BlockLiquid.field_176367_b)))))) continue;
                    \u26032 = true;
                    \u26033 = \u26035.func_176197_a(this, \u26034, entity, \u26033);
                }
            }
        }
        \u26034.func_185344_t();
        if (\u26033.func_72433_c() > 0.0 && entity.func_96092_aw()) {
            \u26033 = \u26033.func_72432_b();
            double d = 0.014;
            entity.field_70159_w += \u26033.field_72450_a * 0.014;
            entity.field_70181_x += \u26033.field_72448_b * 0.014;
            entity.field_70179_y += \u26033.field_72449_c * 0.014;
        }
        return \u26032;
    }

    public boolean func_72875_a(AxisAlignedBB axisAlignedBB, Material material) {
        int n = MathHelper.func_76128_c(axisAlignedBB.field_72340_a);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72336_d);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72338_b);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72337_e);
        \u2603 = MathHelper.func_76128_c(axisAlignedBB.field_72339_c);
        \u2603 = MathHelper.func_76143_f(axisAlignedBB.field_72334_f);
        BlockPos.PooledMutableBlockPos \u26032 = BlockPos.PooledMutableBlockPos.func_185346_s();
        for (\u2603 = n; \u2603 < \u2603; ++\u2603) {
            for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                    if (this.func_180495_p(\u26032.func_181079_c(\u2603, \u2603, \u2603)).func_185904_a() != material) continue;
                    \u26032.func_185344_t();
                    return true;
                }
            }
        }
        \u26032.func_185344_t();
        return false;
    }

    public Explosion func_72876_a(@Nullable Entity entity, double d, double d2, double d3, float f, boolean bl) {
        return this.func_72885_a(entity, d, d2, d3, f, false, bl);
    }

    public Explosion func_72885_a(@Nullable Entity entity, double d, double d2, double d3, float f, boolean bl, boolean bl2) {
        Explosion explosion = new Explosion(this, entity, d, d2, d3, f, bl, bl2);
        explosion.func_77278_a();
        explosion.func_77279_a(true);
        return explosion;
    }

    public float func_72842_a(Vec3d vec3d, AxisAlignedBB axisAlignedBB) {
        double d = 1.0 / ((axisAlignedBB.field_72336_d - axisAlignedBB.field_72340_a) * 2.0 + 1.0);
        \u2603 = 1.0 / ((axisAlignedBB.field_72337_e - axisAlignedBB.field_72338_b) * 2.0 + 1.0);
        \u2603 = 1.0 / ((axisAlignedBB.field_72334_f - axisAlignedBB.field_72339_c) * 2.0 + 1.0);
        \u2603 = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        \u2603 = (1.0 - Math.floor(1.0 / \u2603) * \u2603) / 2.0;
        if (d < 0.0 || \u2603 < 0.0 || \u2603 < 0.0) {
            return 0.0f;
        }
        int \u26032 = 0;
        int \u26033 = 0;
        float \u26034 = 0.0f;
        while (\u26034 <= 1.0f) {
            float f = 0.0f;
            while (f <= 1.0f) {
                \u26035 = 0.0f;
                while (\u26035 <= 1.0f) {
                    double d2 = axisAlignedBB.field_72340_a + (axisAlignedBB.field_72336_d - axisAlignedBB.field_72340_a) * (double)\u26034;
                    \u2603 = axisAlignedBB.field_72338_b + (axisAlignedBB.field_72337_e - axisAlignedBB.field_72338_b) * (double)f;
                    \u2603 = axisAlignedBB.field_72339_c + (axisAlignedBB.field_72334_f - axisAlignedBB.field_72339_c) * (double)\u26035;
                    if (this.func_72933_a(new Vec3d(d2 + \u2603, \u2603, \u2603 + \u2603), vec3d) == null) {
                        ++\u26032;
                    }
                    ++\u26033;
                    float \u26035 = (float)((double)\u26035 + \u2603);
                }
                f = (float)((double)f + \u2603);
            }
            \u26034 = (float)((double)\u26034 + d);
        }
        return (float)\u26032 / (float)\u26033;
    }

    public boolean func_175719_a(@Nullable EntityPlayer entityPlayer, BlockPos blockPos, EnumFacing enumFacing) {
        if (this.func_180495_p(blockPos = blockPos.func_177972_a(enumFacing)).func_177230_c() == Blocks.field_150480_ab) {
            this.func_180498_a(entityPlayer, 1009, blockPos, 0);
            this.func_175698_g(blockPos);
            return true;
        }
        return false;
    }

    public String func_72981_t() {
        return "All: " + this.field_72996_f.size();
    }

    public String func_72827_u() {
        return this.field_73020_y.func_73148_d();
    }

    @Override
    @Nullable
    public TileEntity func_175625_s(BlockPos blockPos) {
        if (this.func_189509_E(blockPos)) {
            return null;
        }
        TileEntity tileEntity = null;
        if (this.field_147481_N) {
            tileEntity = this.func_189508_F(blockPos);
        }
        if (tileEntity == null) {
            tileEntity = this.func_175726_f(blockPos).func_177424_a(blockPos, Chunk.EnumCreateEntityType.IMMEDIATE);
        }
        if (tileEntity == null) {
            tileEntity = this.func_189508_F(blockPos);
        }
        return tileEntity;
    }

    @Nullable
    private TileEntity func_189508_F(BlockPos blockPos) {
        for (int i = 0; i < this.field_147484_a.size(); ++i) {
            TileEntity tileEntity = this.field_147484_a.get(i);
            if (tileEntity.func_145837_r() || !tileEntity.func_174877_v().equals(blockPos)) continue;
            return tileEntity;
        }
        return null;
    }

    public void func_175690_a(BlockPos blockPos2, @Nullable TileEntity tileEntity2) {
        if (this.func_189509_E(blockPos2)) {
            return;
        }
        if (tileEntity2 != null && !tileEntity2.func_145837_r()) {
            if (this.field_147481_N) {
                TileEntity tileEntity2;
                tileEntity2.func_174878_a(blockPos2);
                Iterator<TileEntity> iterator = this.field_147484_a.iterator();
                while (iterator.hasNext()) {
                    TileEntity tileEntity3 = iterator.next();
                    if (!tileEntity3.func_174877_v().equals(blockPos2)) continue;
                    tileEntity3.func_145843_s();
                    iterator.remove();
                }
                this.field_147484_a.add(tileEntity2);
            } else {
                BlockPos blockPos2;
                this.func_175726_f(blockPos2).func_177426_a(blockPos2, tileEntity2);
                this.func_175700_a(tileEntity2);
            }
        }
    }

    public void func_175713_t(BlockPos blockPos) {
        TileEntity tileEntity = this.func_175625_s(blockPos);
        if (tileEntity != null && this.field_147481_N) {
            tileEntity.func_145843_s();
            this.field_147484_a.remove(tileEntity);
        } else {
            if (tileEntity != null) {
                this.field_147484_a.remove(tileEntity);
                this.field_147482_g.remove(tileEntity);
                this.field_175730_i.remove(tileEntity);
            }
            this.func_175726_f(blockPos).func_177425_e(blockPos);
        }
    }

    public void func_147457_a(TileEntity tileEntity) {
        this.field_147483_b.add(tileEntity);
    }

    public boolean func_175665_u(BlockPos blockPos) {
        AxisAlignedBB axisAlignedBB = this.func_180495_p(blockPos).func_185890_d(this, blockPos);
        return axisAlignedBB != Block.field_185506_k && axisAlignedBB.func_72320_b() >= 1.0;
    }

    public boolean func_175677_d(BlockPos blockPos, boolean bl) {
        if (this.func_189509_E(blockPos)) {
            return false;
        }
        Chunk chunk = this.field_73020_y.func_186026_b(blockPos.func_177958_n() >> 4, blockPos.func_177952_p() >> 4);
        if (chunk == null || chunk.func_76621_g()) {
            return bl;
        }
        IBlockState \u26032 = this.func_180495_p(blockPos);
        return \u26032.func_185904_a().func_76218_k() && \u26032.func_185917_h();
    }

    public void func_72966_v() {
        int n = this.func_72967_a(1.0f);
        if (n != this.field_73008_k) {
            this.field_73008_k = n;
        }
    }

    public void func_72891_a(boolean bl, boolean bl2) {
        this.field_72985_G = bl;
        this.field_72992_H = bl2;
    }

    public void func_72835_b() {
        this.func_72979_l();
    }

    protected void func_72947_a() {
        if (this.field_72986_A.func_76059_o()) {
            this.field_73004_o = 1.0f;
            if (this.field_72986_A.func_76061_m()) {
                this.field_73017_q = 1.0f;
            }
        }
    }

    protected void func_72979_l() {
        if (!this.field_73011_w.func_191066_m()) {
            return;
        }
        if (this.field_72995_K) {
            return;
        }
        boolean bl = this.func_82736_K().func_82766_b("doWeatherCycle");
        if (bl) {
            int n = this.field_72986_A.func_176133_A();
            if (n > 0) {
                this.field_72986_A.func_176142_i(--n);
                this.field_72986_A.func_76090_f(this.field_72986_A.func_76061_m() ? 1 : 2);
                this.field_72986_A.func_76080_g(this.field_72986_A.func_76059_o() ? 1 : 2);
            }
            if ((\u2603 = this.field_72986_A.func_76071_n()) <= 0) {
                if (this.field_72986_A.func_76061_m()) {
                    this.field_72986_A.func_76090_f(this.field_73012_v.nextInt(12000) + 3600);
                } else {
                    this.field_72986_A.func_76090_f(this.field_73012_v.nextInt(168000) + 12000);
                }
            } else {
                this.field_72986_A.func_76090_f(--\u2603);
                if (\u2603 <= 0) {
                    this.field_72986_A.func_76069_a(!this.field_72986_A.func_76061_m());
                }
            }
            \u2603 = this.field_72986_A.func_76083_p();
            if (\u2603 <= 0) {
                if (this.field_72986_A.func_76059_o()) {
                    this.field_72986_A.func_76080_g(this.field_73012_v.nextInt(12000) + 12000);
                } else {
                    this.field_72986_A.func_76080_g(this.field_73012_v.nextInt(168000) + 12000);
                }
            } else {
                this.field_72986_A.func_76080_g(--\u2603);
                if (\u2603 <= 0) {
                    this.field_72986_A.func_76084_b(!this.field_72986_A.func_76059_o());
                }
            }
        }
        this.field_73018_p = this.field_73017_q;
        this.field_73017_q = this.field_72986_A.func_76061_m() ? (float)((double)this.field_73017_q + 0.01) : (float)((double)this.field_73017_q - 0.01);
        this.field_73017_q = MathHelper.func_76131_a(this.field_73017_q, 0.0f, 1.0f);
        this.field_73003_n = this.field_73004_o;
        this.field_73004_o = this.field_72986_A.func_76059_o() ? (float)((double)this.field_73004_o + 0.01) : (float)((double)this.field_73004_o - 0.01);
        this.field_73004_o = MathHelper.func_76131_a(this.field_73004_o, 0.0f, 1.0f);
    }

    protected void func_147467_a(int n, int n2, Chunk chunk) {
        chunk.func_76594_o();
    }

    protected void func_147456_g() {
    }

    public void func_189507_a(BlockPos blockPos, IBlockState iBlockState, Random random) {
        this.field_72999_e = true;
        iBlockState.func_177230_c().func_180650_b(this, blockPos, iBlockState, random);
        this.field_72999_e = false;
    }

    public boolean func_175675_v(BlockPos blockPos) {
        return this.func_175670_e(blockPos, false);
    }

    public boolean func_175662_w(BlockPos blockPos) {
        return this.func_175670_e(blockPos, true);
    }

    public boolean func_175670_e(BlockPos blockPos, boolean bl) {
        Biome biome = this.func_180494_b(blockPos);
        float \u26032 = biome.func_180626_a(blockPos);
        if (\u26032 >= 0.15f) {
            return false;
        }
        if (blockPos.func_177956_o() >= 0 && blockPos.func_177956_o() < 256 && this.func_175642_b(EnumSkyBlock.BLOCK, blockPos) < 10 && ((\u2603 = (\u2603 = this.func_180495_p(blockPos)).func_177230_c()) == Blocks.field_150355_j || \u2603 == Blocks.field_150358_i) && \u2603.func_177229_b(BlockLiquid.field_176367_b) == 0) {
            if (!bl) {
                return true;
            }
            boolean bl2 = \u2603 = this.func_175696_F(blockPos.func_177976_e()) && this.func_175696_F(blockPos.func_177974_f()) && this.func_175696_F(blockPos.func_177978_c()) && this.func_175696_F(blockPos.func_177968_d());
            if (!\u2603) {
                return true;
            }
        }
        return false;
    }

    private boolean func_175696_F(BlockPos blockPos) {
        return this.func_180495_p(blockPos).func_185904_a() == Material.field_151586_h;
    }

    public boolean func_175708_f(BlockPos blockPos, boolean bl) {
        Biome biome = this.func_180494_b(blockPos);
        float \u26032 = biome.func_180626_a(blockPos);
        if (\u26032 >= 0.15f) {
            return false;
        }
        if (!bl) {
            return true;
        }
        return blockPos.func_177956_o() >= 0 && blockPos.func_177956_o() < 256 && this.func_175642_b(EnumSkyBlock.BLOCK, blockPos) < 10 && (\u2603 = this.func_180495_p(blockPos)).func_185904_a() == Material.field_151579_a && Blocks.field_150431_aC.func_176196_c(this, blockPos);
    }

    public boolean func_175664_x(BlockPos blockPos) {
        boolean bl = false;
        if (this.field_73011_w.func_191066_m()) {
            bl |= this.func_180500_c(EnumSkyBlock.SKY, blockPos);
        }
        return bl |= this.func_180500_c(EnumSkyBlock.BLOCK, blockPos);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int func_175638_a(BlockPos blockPos, EnumSkyBlock enumSkyBlock) {
        if (enumSkyBlock == EnumSkyBlock.SKY && this.func_175678_i(blockPos)) {
            return 15;
        }
        IBlockState iBlockState = this.func_180495_p(blockPos);
        int \u26032 = enumSkyBlock == EnumSkyBlock.SKY ? 0 : iBlockState.func_185906_d();
        int \u26033 = iBlockState.func_185891_c();
        if (\u26033 >= 15 && iBlockState.func_185906_d() > 0) {
            \u26033 = 1;
        }
        if (\u26033 < 1) {
            \u26033 = 1;
        }
        if (\u26033 >= 15) {
            return 0;
        }
        if (\u26032 >= 14) {
            return \u26032;
        }
        BlockPos.PooledMutableBlockPos \u26034 = BlockPos.PooledMutableBlockPos.func_185346_s();
        try {
            for (EnumFacing enumFacing : EnumFacing.values()) {
                \u26034.func_189533_g(blockPos).func_189536_c(enumFacing);
                int n = this.func_175642_b(enumSkyBlock, \u26034) - \u26033;
                if (n > \u26032) {
                    \u26032 = n;
                }
                if (\u26032 < 14) continue;
                int n2 = \u26032;
                return n2;
            }
        }
        finally {
            \u26034.func_185344_t();
        }
        return \u26032;
    }

    public boolean func_180500_c(EnumSkyBlock enumSkyBlock, BlockPos blockPos) {
        int \u26032;
        if (!this.func_175648_a(blockPos, 17, false)) {
            return false;
        }
        int n = 0;
        \u2603 = 0;
        this.field_72984_F.func_76320_a("getBrightness");
        \u2603 = this.func_175642_b(enumSkyBlock, blockPos);
        \u2603 = this.func_175638_a(blockPos, enumSkyBlock);
        \u2603 = blockPos.func_177958_n();
        \u2603 = blockPos.func_177956_o();
        \u2603 = blockPos.func_177952_p();
        if (\u2603 > \u2603) {
            this.field_72994_J[\u2603++] = 133152;
        } else if (\u2603 < \u2603) {
            this.field_72994_J[\u2603++] = 0x20820 | \u2603 << 18;
            while (n < \u2603) {
                \u2603 = this.field_72994_J[n++];
                \u2603 = (\u2603 & 0x3F) - 32 + \u2603;
                \u2603 = (\u2603 >> 6 & 0x3F) - 32 + \u2603;
                \u2603 = (\u2603 >> 12 & 0x3F) - 32 + \u2603;
                \u2603 = \u2603 >> 18 & 0xF;
                BlockPos blockPos2 = new BlockPos(\u2603, \u2603, \u2603);
                \u26032 = this.func_175642_b(enumSkyBlock, blockPos2);
                if (\u26032 != \u2603) continue;
                this.func_175653_a(enumSkyBlock, blockPos2, 0);
                if (\u2603 <= 0 || (\u26035 = MathHelper.func_76130_a(\u2603 - \u2603)) + (\u26036 = MathHelper.func_76130_a(\u2603 - \u2603)) + (\u26037 = MathHelper.func_76130_a(\u2603 - \u2603)) >= 17) continue;
                BlockPos.PooledMutableBlockPos \u26033 = BlockPos.PooledMutableBlockPos.func_185346_s();
                for (EnumFacing enumFacing : EnumFacing.values()) {
                    int n2 = \u2603 + enumFacing.func_82601_c();
                    \u2603 = \u2603 + enumFacing.func_96559_d();
                    \u2603 = \u2603 + enumFacing.func_82599_e();
                    \u26033.func_181079_c(n2, \u2603, \u2603);
                    \u2603 = Math.max(1, this.func_180495_p(\u26033).func_185891_c());
                    \u26032 = this.func_175642_b(enumSkyBlock, \u26033);
                    if (\u26032 != \u2603 - \u2603 || \u2603 >= this.field_72994_J.length) continue;
                    this.field_72994_J[\u2603++] = n2 - \u2603 + 32 | \u2603 - \u2603 + 32 << 6 | \u2603 - \u2603 + 32 << 12 | \u2603 - \u2603 << 18;
                }
                \u26033.func_185344_t();
            }
            n = 0;
        }
        this.field_72984_F.func_76319_b();
        this.field_72984_F.func_76320_a("checkedPosition < toCheckCount");
        while (n < \u2603) {
            \u2603 = this.field_72994_J[n++];
            \u2603 = (\u2603 & 0x3F) - 32 + \u2603;
            \u2603 = (\u2603 >> 6 & 0x3F) - 32 + \u2603;
            \u2603 = (\u2603 >> 12 & 0x3F) - 32 + \u2603;
            BlockPos blockPos3 = new BlockPos(\u2603, \u2603, \u2603);
            int \u26034 = this.func_175642_b(enumSkyBlock, blockPos3);
            \u26032 = this.func_175638_a(blockPos3, enumSkyBlock);
            if (\u26032 == \u26034) continue;
            this.func_175653_a(enumSkyBlock, blockPos3, \u26032);
            if (\u26032 <= \u26034) continue;
            int \u26035 = Math.abs(\u2603 - \u2603);
            int \u26036 = Math.abs(\u2603 - \u2603);
            int \u26037 = Math.abs(\u2603 - \u2603);
            boolean bl = \u2603 = \u2603 < this.field_72994_J.length - 6;
            if (\u26035 + \u26036 + \u26037 >= 17 || !\u2603) continue;
            if (this.func_175642_b(enumSkyBlock, blockPos3.func_177976_e()) < \u26032) {
                this.field_72994_J[\u2603++] = \u2603 - 1 - \u2603 + 32 + (\u2603 - \u2603 + 32 << 6) + (\u2603 - \u2603 + 32 << 12);
            }
            if (this.func_175642_b(enumSkyBlock, blockPos3.func_177974_f()) < \u26032) {
                this.field_72994_J[\u2603++] = \u2603 + 1 - \u2603 + 32 + (\u2603 - \u2603 + 32 << 6) + (\u2603 - \u2603 + 32 << 12);
            }
            if (this.func_175642_b(enumSkyBlock, blockPos3.func_177977_b()) < \u26032) {
                this.field_72994_J[\u2603++] = \u2603 - \u2603 + 32 + (\u2603 - 1 - \u2603 + 32 << 6) + (\u2603 - \u2603 + 32 << 12);
            }
            if (this.func_175642_b(enumSkyBlock, blockPos3.func_177984_a()) < \u26032) {
                this.field_72994_J[\u2603++] = \u2603 - \u2603 + 32 + (\u2603 + 1 - \u2603 + 32 << 6) + (\u2603 - \u2603 + 32 << 12);
            }
            if (this.func_175642_b(enumSkyBlock, blockPos3.func_177978_c()) < \u26032) {
                this.field_72994_J[\u2603++] = \u2603 - \u2603 + 32 + (\u2603 - \u2603 + 32 << 6) + (\u2603 - 1 - \u2603 + 32 << 12);
            }
            if (this.func_175642_b(enumSkyBlock, blockPos3.func_177968_d()) >= \u26032) continue;
            this.field_72994_J[\u2603++] = \u2603 - \u2603 + 32 + (\u2603 - \u2603 + 32 << 6) + (\u2603 + 1 - \u2603 + 32 << 12);
        }
        this.field_72984_F.func_76319_b();
        return true;
    }

    public boolean func_72955_a(boolean bl) {
        return false;
    }

    @Nullable
    public List<NextTickListEntry> func_72920_a(Chunk chunk, boolean bl) {
        return null;
    }

    @Nullable
    public List<NextTickListEntry> func_175712_a(StructureBoundingBox structureBoundingBox, boolean bl) {
        return null;
    }

    public List<Entity> func_72839_b(@Nullable Entity entity, AxisAlignedBB axisAlignedBB) {
        return this.func_175674_a(entity, axisAlignedBB, EntitySelectors.field_180132_d);
    }

    public List<Entity> func_175674_a(@Nullable Entity entity, AxisAlignedBB axisAlignedBB, @Nullable Predicate<? super Entity> predicate) {
        ArrayList arrayList = Lists.newArrayList();
        int \u26032 = MathHelper.func_76128_c((axisAlignedBB.field_72340_a - 2.0) / 16.0);
        int \u26033 = MathHelper.func_76128_c((axisAlignedBB.field_72336_d + 2.0) / 16.0);
        int \u26034 = MathHelper.func_76128_c((axisAlignedBB.field_72339_c - 2.0) / 16.0);
        int \u26035 = MathHelper.func_76128_c((axisAlignedBB.field_72334_f + 2.0) / 16.0);
        for (int i = \u26032; i <= \u26033; ++i) {
            for (\u2603 = \u26034; \u2603 <= \u26035; ++\u2603) {
                if (!this.func_175680_a(i, \u2603, true)) continue;
                this.func_72964_e(i, \u2603).func_177414_a(entity, axisAlignedBB, arrayList, predicate);
            }
        }
        return arrayList;
    }

    public <T extends Entity> List<T> func_175644_a(Class<? extends T> clazz, Predicate<? super T> predicate) {
        ArrayList arrayList = Lists.newArrayList();
        for (Entity entity : this.field_72996_f) {
            if (!clazz.isAssignableFrom(entity.getClass()) || !predicate.apply((Object)entity)) continue;
            arrayList.add(entity);
        }
        return arrayList;
    }

    public <T extends Entity> List<T> func_175661_b(Class<? extends T> clazz, Predicate<? super T> predicate) {
        ArrayList arrayList = Lists.newArrayList();
        for (Entity entity : this.field_73010_i) {
            if (!clazz.isAssignableFrom(entity.getClass()) || !predicate.apply((Object)entity)) continue;
            arrayList.add(entity);
        }
        return arrayList;
    }

    public <T extends Entity> List<T> func_72872_a(Class<? extends T> clazz, AxisAlignedBB axisAlignedBB) {
        return this.func_175647_a(clazz, axisAlignedBB, EntitySelectors.field_180132_d);
    }

    public <T extends Entity> List<T> func_175647_a(Class<? extends T> clazz, AxisAlignedBB axisAlignedBB, @Nullable Predicate<? super T> predicate) {
        int n = MathHelper.func_76128_c((axisAlignedBB.field_72340_a - 2.0) / 16.0);
        \u2603 = MathHelper.func_76143_f((axisAlignedBB.field_72336_d + 2.0) / 16.0);
        \u2603 = MathHelper.func_76128_c((axisAlignedBB.field_72339_c - 2.0) / 16.0);
        \u2603 = MathHelper.func_76143_f((axisAlignedBB.field_72334_f + 2.0) / 16.0);
        ArrayList \u26032 = Lists.newArrayList();
        for (\u2603 = n; \u2603 < \u2603; ++\u2603) {
            for (\u2603 = \u2603; \u2603 < \u2603; ++\u2603) {
                if (!this.func_175680_a(\u2603, \u2603, true)) continue;
                this.func_72964_e(\u2603, \u2603).func_177430_a(clazz, axisAlignedBB, \u26032, predicate);
            }
        }
        return \u26032;
    }

    @Nullable
    public <T extends Entity> T func_72857_a(Class<? extends T> clazz, AxisAlignedBB axisAlignedBB, T t) {
        List<T> list = this.func_72872_a(clazz, axisAlignedBB);
        Entity \u26032 = null;
        double \u26033 = Double.MAX_VALUE;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity)list.get(i);
            if (entity == t || !EntitySelectors.field_180132_d.apply((Object)entity) || (\u2603 = t.func_70068_e(entity)) > \u26033) continue;
            \u26032 = entity;
            \u26033 = \u2603;
        }
        return (T)\u26032;
    }

    @Nullable
    public Entity func_73045_a(int n) {
        return this.field_175729_l.func_76041_a(n);
    }

    public List<Entity> func_72910_y() {
        return this.field_72996_f;
    }

    public void func_175646_b(BlockPos blockPos, TileEntity tileEntity) {
        if (this.func_175667_e(blockPos)) {
            this.func_175726_f(blockPos).func_76630_e();
        }
    }

    public int func_72907_a(Class<?> clazz) {
        int n = 0;
        for (Entity entity : this.field_72996_f) {
            if (entity instanceof EntityLiving && ((EntityLiving)entity).func_104002_bU() || !clazz.isAssignableFrom(entity.getClass())) continue;
            ++n;
        }
        return n;
    }

    public void func_175650_b(Collection<Entity> collection) {
        this.field_72996_f.addAll(collection);
        for (Entity entity : collection) {
            this.func_72923_a(entity);
        }
    }

    public void func_175681_c(Collection<Entity> collection) {
        this.field_72997_g.addAll(collection);
    }

    public boolean func_190527_a(Block block, BlockPos blockPos, boolean bl, EnumFacing enumFacing, @Nullable Entity entity) {
        IBlockState iBlockState = this.func_180495_p(blockPos);
        AxisAlignedBB axisAlignedBB = \u2603 = bl ? null : block.func_176223_P().func_185890_d(this, blockPos);
        if (\u2603 != Block.field_185506_k && !this.func_72917_a(\u2603.func_186670_a(blockPos), entity)) {
            return false;
        }
        if (iBlockState.func_185904_a() == Material.field_151594_q && block == Blocks.field_150467_bQ) {
            return true;
        }
        return iBlockState.func_185904_a().func_76222_j() && block.func_176198_a(this, blockPos, enumFacing);
    }

    public int func_181545_F() {
        return this.field_181546_a;
    }

    public void func_181544_b(int n) {
        this.field_181546_a = n;
    }

    @Override
    public int func_175627_a(BlockPos blockPos, EnumFacing enumFacing) {
        return this.func_180495_p(blockPos).func_185893_b(this, blockPos, enumFacing);
    }

    @Override
    public WorldType func_175624_G() {
        return this.field_72986_A.func_76067_t();
    }

    public int func_175676_y(BlockPos blockPos) {
        int n = 0;
        if ((n = Math.max(n, this.func_175627_a(blockPos.func_177977_b(), EnumFacing.DOWN))) >= 15) {
            return n;
        }
        if ((n = Math.max(n, this.func_175627_a(blockPos.func_177984_a(), EnumFacing.UP))) >= 15) {
            return n;
        }
        if ((n = Math.max(n, this.func_175627_a(blockPos.func_177978_c(), EnumFacing.NORTH))) >= 15) {
            return n;
        }
        if ((n = Math.max(n, this.func_175627_a(blockPos.func_177968_d(), EnumFacing.SOUTH))) >= 15) {
            return n;
        }
        if ((n = Math.max(n, this.func_175627_a(blockPos.func_177976_e(), EnumFacing.WEST))) >= 15) {
            return n;
        }
        if ((n = Math.max(n, this.func_175627_a(blockPos.func_177974_f(), EnumFacing.EAST))) >= 15) {
            return n;
        }
        return n;
    }

    public boolean func_175709_b(BlockPos blockPos, EnumFacing enumFacing) {
        return this.func_175651_c(blockPos, enumFacing) > 0;
    }

    public int func_175651_c(BlockPos blockPos, EnumFacing enumFacing) {
        IBlockState iBlockState = this.func_180495_p(blockPos);
        if (iBlockState.func_185915_l()) {
            return this.func_175676_y(blockPos);
        }
        return iBlockState.func_185911_a(this, blockPos, enumFacing);
    }

    public boolean func_175640_z(BlockPos blockPos) {
        if (this.func_175651_c(blockPos.func_177977_b(), EnumFacing.DOWN) > 0) {
            return true;
        }
        if (this.func_175651_c(blockPos.func_177984_a(), EnumFacing.UP) > 0) {
            return true;
        }
        if (this.func_175651_c(blockPos.func_177978_c(), EnumFacing.NORTH) > 0) {
            return true;
        }
        if (this.func_175651_c(blockPos.func_177968_d(), EnumFacing.SOUTH) > 0) {
            return true;
        }
        if (this.func_175651_c(blockPos.func_177976_e(), EnumFacing.WEST) > 0) {
            return true;
        }
        return this.func_175651_c(blockPos.func_177974_f(), EnumFacing.EAST) > 0;
    }

    public int func_175687_A(BlockPos blockPos) {
        int n = 0;
        for (EnumFacing enumFacing : EnumFacing.values()) {
            int n2 = this.func_175651_c(blockPos.func_177972_a(enumFacing), enumFacing);
            if (n2 >= 15) {
                return 15;
            }
            if (n2 <= n) continue;
            n = n2;
        }
        return n;
    }

    @Nullable
    public EntityPlayer func_72890_a(Entity entity, double d) {
        return this.func_184137_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, d, false);
    }

    @Nullable
    public EntityPlayer func_184136_b(Entity entity, double d) {
        return this.func_184137_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, d, true);
    }

    @Nullable
    public EntityPlayer func_184137_a(double d, double d2, double d3, double d4, boolean bl) {
        Predicate<Entity> predicate = bl ? EntitySelectors.field_188444_d : EntitySelectors.field_180132_d;
        return this.func_190525_a(d, d2, d3, d4, predicate);
    }

    @Nullable
    public EntityPlayer func_190525_a(double d, double d2, double d3, double d4, Predicate<Entity> predicate) {
        double \u26034 = -1.0;
        EntityPlayer \u26032 = null;
        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            EntityPlayer entityPlayer = this.field_73010_i.get(i);
            if (!predicate.apply((Object)entityPlayer)) continue;
            double \u26033 = entityPlayer.func_70092_e(d, d2, d3);
            if (!(d4 < 0.0) && !(\u26033 < d4 * d4) || \u26034 != -1.0 && !(\u26033 < \u26034)) continue;
            \u26034 = \u26033;
            \u26032 = entityPlayer;
        }
        return \u26032;
    }

    public boolean func_175636_b(double d, double d2, double d3, double d4) {
        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            EntityPlayer entityPlayer = this.field_73010_i.get(i);
            if (!EntitySelectors.field_180132_d.apply((Object)entityPlayer)) continue;
            double \u26032 = entityPlayer.func_70092_e(d, d2, d3);
            if (!(d4 < 0.0) && !(\u26032 < d4 * d4)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public EntityPlayer func_184142_a(Entity entity, double d, double d2) {
        return this.func_184150_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, d, d2, null, null);
    }

    @Nullable
    public EntityPlayer func_184139_a(BlockPos blockPos, double d, double d2) {
        return this.func_184150_a((float)blockPos.func_177958_n() + 0.5f, (float)blockPos.func_177956_o() + 0.5f, (float)blockPos.func_177952_p() + 0.5f, d, d2, null, null);
    }

    @Nullable
    public EntityPlayer func_184150_a(double d, double d2, double d3, double d4, double d5, @Nullable Function<EntityPlayer, Double> function2, @Nullable Predicate<EntityPlayer> predicate) {
        double \u26035 = -1.0;
        EntityPlayer \u26032 = null;
        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            Function<EntityPlayer, Double> function2;
            EntityPlayer entityPlayer = this.field_73010_i.get(i);
            if (entityPlayer.field_71075_bZ.field_75102_a || !entityPlayer.func_70089_S() || entityPlayer.func_175149_v() || predicate != null && !predicate.apply((Object)entityPlayer)) continue;
            double \u26033 = entityPlayer.func_70092_e(d, entityPlayer.field_70163_u, d3);
            double \u26034 = d4;
            if (entityPlayer.func_70093_af()) {
                \u26034 *= (double)0.8f;
            }
            if (entityPlayer.func_82150_aj()) {
                float f = entityPlayer.func_82243_bO();
                if (f < 0.1f) {
                    f = 0.1f;
                }
                \u26034 *= (double)(0.7f * f);
            }
            if (function2 != null) {
                \u26034 *= ((Double)MoreObjects.firstNonNull((Object)function2.apply((Object)entityPlayer), (Object)1.0)).doubleValue();
            }
            if (!(d5 < 0.0) && !(Math.abs(entityPlayer.field_70163_u - d2) < d5 * d5) || !(d4 < 0.0) && !(\u26033 < \u26034 * \u26034) || \u26035 != -1.0 && !(\u26033 < \u26035)) continue;
            \u26035 = \u26033;
            \u26032 = entityPlayer;
        }
        return \u26032;
    }

    @Nullable
    public EntityPlayer func_72924_a(String string) {
        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            EntityPlayer entityPlayer = this.field_73010_i.get(i);
            if (!string.equals(entityPlayer.func_70005_c_())) continue;
            return entityPlayer;
        }
        return null;
    }

    @Nullable
    public EntityPlayer func_152378_a(UUID uUID) {
        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            EntityPlayer entityPlayer = this.field_73010_i.get(i);
            if (!uUID.equals(entityPlayer.func_110124_au())) continue;
            return entityPlayer;
        }
        return null;
    }

    public void func_72882_A() {
    }

    public void func_72906_B() throws MinecraftException {
        this.field_73019_z.func_75762_c();
    }

    public void func_82738_a(long l) {
        this.field_72986_A.func_82572_b(l);
    }

    public long func_72905_C() {
        return this.field_72986_A.func_76063_b();
    }

    public long func_82737_E() {
        return this.field_72986_A.func_82573_f();
    }

    public long func_72820_D() {
        return this.field_72986_A.func_76073_f();
    }

    public void func_72877_b(long l) {
        this.field_72986_A.func_76068_b(l);
    }

    public BlockPos func_175694_M() {
        BlockPos blockPos = new BlockPos(this.field_72986_A.func_76079_c(), this.field_72986_A.func_76075_d(), this.field_72986_A.func_76074_e());
        if (!this.func_175723_af().func_177746_a(blockPos)) {
            blockPos = this.func_175645_m(new BlockPos(this.func_175723_af().func_177731_f(), 0.0, this.func_175723_af().func_177721_g()));
        }
        return blockPos;
    }

    public void func_175652_B(BlockPos blockPos) {
        this.field_72986_A.func_176143_a(blockPos);
    }

    public void func_72897_h(Entity entity) {
        int n = MathHelper.func_76128_c(entity.field_70165_t / 16.0);
        \u2603 = MathHelper.func_76128_c(entity.field_70161_v / 16.0);
        \u2603 = 2;
        for (\u2603 = -2; \u2603 <= 2; ++\u2603) {
            for (\u2603 = -2; \u2603 <= 2; ++\u2603) {
                this.func_72964_e(n + \u2603, \u2603 + \u2603);
            }
        }
        if (!this.field_72996_f.contains(entity)) {
            this.field_72996_f.add(entity);
        }
    }

    public boolean func_175660_a(EntityPlayer entityPlayer, BlockPos blockPos) {
        return true;
    }

    public void func_72960_a(Entity entity, byte by) {
    }

    public IChunkProvider func_72863_F() {
        return this.field_73020_y;
    }

    public void func_175641_c(BlockPos blockPos, Block block, int n, int n2) {
        this.func_180495_p(blockPos).func_189547_a(this, blockPos, n, n2);
    }

    public ISaveHandler func_72860_G() {
        return this.field_73019_z;
    }

    public WorldInfo func_72912_H() {
        return this.field_72986_A;
    }

    public GameRules func_82736_K() {
        return this.field_72986_A.func_82574_x();
    }

    public void func_72854_c() {
    }

    public float func_72819_i(float f) {
        return (this.field_73018_p + (this.field_73017_q - this.field_73018_p) * f) * this.func_72867_j(f);
    }

    public void func_147442_i(float f) {
        this.field_73018_p = f;
        this.field_73017_q = f;
    }

    public float func_72867_j(float f) {
        return this.field_73003_n + (this.field_73004_o - this.field_73003_n) * f;
    }

    public void func_72894_k(float f) {
        this.field_73003_n = f;
        this.field_73004_o = f;
    }

    public boolean func_72911_I() {
        return (double)this.func_72819_i(1.0f) > 0.9;
    }

    public boolean func_72896_J() {
        return (double)this.func_72867_j(1.0f) > 0.2;
    }

    public boolean func_175727_C(BlockPos blockPos) {
        if (!this.func_72896_J()) {
            return false;
        }
        if (!this.func_175678_i(blockPos)) {
            return false;
        }
        if (this.func_175725_q(blockPos).func_177956_o() > blockPos.func_177956_o()) {
            return false;
        }
        Biome biome = this.func_180494_b(blockPos);
        if (biome.func_76746_c()) {
            return false;
        }
        if (this.func_175708_f(blockPos, false)) {
            return false;
        }
        return biome.func_76738_d();
    }

    public boolean func_180502_D(BlockPos blockPos) {
        Biome biome = this.func_180494_b(blockPos);
        return biome.func_76736_e();
    }

    @Nullable
    public MapStorage func_175693_T() {
        return this.field_72988_C;
    }

    public void func_72823_a(String string, WorldSavedData worldSavedData) {
        this.field_72988_C.func_75745_a(string, worldSavedData);
    }

    @Nullable
    public WorldSavedData func_72943_a(Class<? extends WorldSavedData> clazz, String string) {
        return this.field_72988_C.func_75742_a(clazz, string);
    }

    public int func_72841_b(String string) {
        return this.field_72988_C.func_75743_a(string);
    }

    public void func_175669_a(int n, BlockPos blockPos, int n2) {
        for (\u2603 = 0; \u2603 < this.field_73021_x.size(); ++\u2603) {
            this.field_73021_x.get(\u2603).func_180440_a(n, blockPos, n2);
        }
    }

    public void func_175718_b(int n, BlockPos blockPos, int n2) {
        this.func_180498_a(null, n, blockPos, n2);
    }

    public void func_180498_a(@Nullable EntityPlayer entityPlayer, int n, BlockPos blockPos, int n2) {
        try {
            for (\u2603 = 0; \u2603 < this.field_73021_x.size(); ++\u2603) {
                this.field_73021_x.get(\u2603).func_180439_a(entityPlayer, n, blockPos, n2);
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.func_85055_a(throwable, "Playing level event");
            CrashReportCategory \u26032 = crashReport.func_85058_a("Level event being played");
            \u26032.func_71507_a("Block coordinates", CrashReportCategory.func_180522_a(blockPos));
            \u26032.func_71507_a("Event source", entityPlayer);
            \u26032.func_71507_a("Event type", n);
            \u26032.func_71507_a("Event data", n2);
            throw new ReportedException(crashReport);
        }
    }

    public int func_72800_K() {
        return 256;
    }

    public int func_72940_L() {
        return this.field_73011_w.func_177495_o() ? 128 : 256;
    }

    public Random func_72843_D(int n, int n2, int n3) {
        long l = (long)n * 341873128712L + (long)n2 * 132897987541L + this.func_72912_H().func_76063_b() + (long)n3;
        this.field_73012_v.setSeed(l);
        return this.field_73012_v;
    }

    public double func_72919_O() {
        if (this.field_72986_A.func_76067_t() == WorldType.field_77138_c) {
            return 0.0;
        }
        return 63.0;
    }

    public CrashReportCategory func_72914_a(CrashReport crashReport) {
        CrashReportCategory crashReportCategory = crashReport.func_85057_a("Affected level", 1);
        crashReportCategory.func_71507_a("Level name", this.field_72986_A == null ? "????" : this.field_72986_A.func_76065_j());
        crashReportCategory.func_189529_a("All players", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return World.this.field_73010_i.size() + " total; " + World.this.field_73010_i;
            }

            @Override
            public /* synthetic */ Object call() throws Exception {
                return this.call();
            }
        });
        crashReportCategory.func_189529_a("Chunk stats", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return World.this.field_73020_y.func_73148_d();
            }

            @Override
            public /* synthetic */ Object call() throws Exception {
                return this.call();
            }
        });
        try {
            this.field_72986_A.func_85118_a(crashReportCategory);
        }
        catch (Throwable \u26032) {
            crashReportCategory.func_71499_a("Level Data Unobtainable", \u26032);
        }
        return crashReportCategory;
    }

    public void func_175715_c(int n, BlockPos blockPos, int n2) {
        for (\u2603 = 0; \u2603 < this.field_73021_x.size(); ++\u2603) {
            IWorldEventListener iWorldEventListener = this.field_73021_x.get(\u2603);
            iWorldEventListener.func_180441_b(n, blockPos, n2);
        }
    }

    public Calendar func_83015_S() {
        if (this.func_82737_E() % 600L == 0L) {
            this.field_83016_L.setTimeInMillis(MinecraftServer.func_130071_aq());
        }
        return this.field_83016_L;
    }

    public void func_92088_a(double d, double d2, double d3, double d4, double d5, double d6, @Nullable NBTTagCompound nBTTagCompound) {
    }

    public Scoreboard func_96441_U() {
        return this.field_96442_D;
    }

    public void func_175666_e(BlockPos blockPos, Block block) {
        for (EnumFacing enumFacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.func_177972_a(enumFacing);
            if (!this.func_175667_e(blockPos2)) continue;
            IBlockState \u26032 = this.func_180495_p(blockPos2);
            if (Blocks.field_150441_bU.func_185547_C(\u26032)) {
                \u26032.func_189546_a(this, blockPos2, block, blockPos);
                continue;
            }
            if (!\u26032.func_185915_l() || !Blocks.field_150441_bU.func_185547_C(\u26032 = this.func_180495_p(blockPos2 = blockPos2.func_177972_a(enumFacing)))) continue;
            \u26032.func_189546_a(this, blockPos2, block, blockPos);
        }
    }

    public DifficultyInstance func_175649_E(BlockPos blockPos) {
        long l = 0L;
        float \u26032 = 0.0f;
        if (this.func_175667_e(blockPos)) {
            \u26032 = this.func_130001_d();
            l = this.func_175726_f(blockPos).func_177416_w();
        }
        return new DifficultyInstance(this.func_175659_aa(), this.func_72820_D(), l, \u26032);
    }

    public EnumDifficulty func_175659_aa() {
        return this.func_72912_H().func_176130_y();
    }

    public int func_175657_ab() {
        return this.field_73008_k;
    }

    public void func_175692_b(int n) {
        this.field_73008_k = n;
    }

    public int func_175658_ac() {
        return this.field_73016_r;
    }

    public void func_175702_c(int n) {
        this.field_73016_r = n;
    }

    public VillageCollection func_175714_ae() {
        return this.field_72982_D;
    }

    public WorldBorder func_175723_af() {
        return this.field_175728_M;
    }

    public boolean func_72916_c(int n, int n2) {
        BlockPos blockPos = this.func_175694_M();
        int \u26032 = n * 16 + 8 - blockPos.func_177958_n();
        int \u26033 = n2 * 16 + 8 - blockPos.func_177952_p();
        int \u26034 = 128;
        return \u26032 >= -128 && \u26032 <= 128 && \u26033 >= -128 && \u26033 <= 128;
    }

    public void func_184135_a(Packet<?> packet) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    public LootTableManager func_184146_ak() {
        return this.field_184151_B;
    }

    @Nullable
    public BlockPos func_190528_a(String string, BlockPos blockPos, boolean bl) {
        return null;
    }

    private static /* synthetic */ String func_194305_c(TileEntity tileEntity) {
        return String.valueOf(TileEntity.func_190559_a(tileEntity.getClass()));
    }
}

