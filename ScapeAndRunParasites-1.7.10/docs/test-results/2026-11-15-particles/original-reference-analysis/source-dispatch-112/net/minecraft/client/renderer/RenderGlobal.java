/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.util.vector.Matrix4f
 *  org.lwjgl.util.vector.Vector3f
 *  org.lwjgl.util.vector.Vector4f
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class RenderGlobal
implements IWorldEventListener,
IResourceManagerReloadListener {
    private static final Logger field_147599_m = LogManager.getLogger();
    private static final ResourceLocation field_110927_h = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation field_110928_i = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation field_110925_j = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation field_110926_k = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation field_175006_g = new ResourceLocation("textures/misc/forcefield.png");
    private final Minecraft field_72777_q;
    private final TextureManager field_72770_i;
    private final RenderManager field_175010_j;
    private WorldClient field_72769_h;
    private Set<RenderChunk> field_175009_l = Sets.newLinkedHashSet();
    private List<ContainerLocalRenderInformation> field_72755_R = Lists.newArrayListWithCapacity((int)69696);
    private final Set<TileEntity> field_181024_n = Sets.newHashSet();
    private ViewFrustum field_175008_n;
    private int field_72772_v = -1;
    private int field_72771_w = -1;
    private int field_72781_x = -1;
    private final VertexFormat field_175014_r;
    private VertexBuffer field_175013_s;
    private VertexBuffer field_175012_t;
    private VertexBuffer field_175011_u;
    private int field_72773_u;
    private final Map<Integer, DestroyBlockProgress> field_72738_E = Maps.newHashMap();
    private final Map<BlockPos, ISound> field_147593_P = Maps.newHashMap();
    private final TextureAtlasSprite[] field_94141_F = new TextureAtlasSprite[10];
    private Framebuffer field_175015_z;
    private ShaderGroup field_174991_A;
    private double field_174992_B = Double.MIN_VALUE;
    private double field_174993_C = Double.MIN_VALUE;
    private double field_174987_D = Double.MIN_VALUE;
    private int field_174988_E = Integer.MIN_VALUE;
    private int field_174989_F = Integer.MIN_VALUE;
    private int field_174990_G = Integer.MIN_VALUE;
    private double field_174997_H = Double.MIN_VALUE;
    private double field_174998_I = Double.MIN_VALUE;
    private double field_174999_J = Double.MIN_VALUE;
    private double field_175000_K = Double.MIN_VALUE;
    private double field_174994_L = Double.MIN_VALUE;
    private ChunkRenderDispatcher field_174995_M;
    private ChunkRenderContainer field_174996_N;
    private int field_72739_F = -1;
    private int field_72740_G = 2;
    private int field_72748_H;
    private int field_72749_I;
    private int field_72750_J;
    private boolean field_175002_T;
    private ClippingHelper field_175001_U;
    private final Vector4f[] field_175004_V = new Vector4f[8];
    private final Vector3d field_175003_W = new Vector3d();
    private boolean field_175005_X;
    IRenderChunkFactory field_175007_a;
    private double field_147596_f;
    private double field_147597_g;
    private double field_147602_h;
    private boolean field_147595_R = true;
    private boolean field_184386_ad;
    private final Set<BlockPos> field_184387_ae = Sets.newHashSet();

    public RenderGlobal(Minecraft minecraft) {
        this.field_72777_q = minecraft;
        this.field_175010_j = minecraft.func_175598_ae();
        this.field_72770_i = minecraft.func_110434_K();
        this.field_72770_i.func_110577_a(field_175006_g);
        GlStateManager.func_187421_b(3553, 10242, 10497);
        GlStateManager.func_187421_b(3553, 10243, 10497);
        GlStateManager.func_179144_i(0);
        this.func_174971_n();
        this.field_175005_X = OpenGlHelper.func_176075_f();
        if (this.field_175005_X) {
            this.field_174996_N = new VboRenderList();
            this.field_175007_a = new VboChunkFactory();
        } else {
            this.field_174996_N = new RenderList();
            this.field_175007_a = new ListChunkFactory();
        }
        this.field_175014_r = new VertexFormat();
        this.field_175014_r.func_181721_a(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        this.func_174963_q();
        this.func_174980_p();
        this.func_174964_o();
    }

    @Override
    public void func_110549_a(IResourceManager iResourceManager) {
        this.func_174971_n();
    }

    private void func_174971_n() {
        TextureMap textureMap = this.field_72777_q.func_147117_R();
        for (int i = 0; i < this.field_94141_F.length; ++i) {
            this.field_94141_F[i] = textureMap.func_110572_b("minecraft:blocks/destroy_stage_" + i);
        }
    }

    public void func_174966_b() {
        if (OpenGlHelper.field_148824_g) {
            if (ShaderLinkHelper.func_148074_b() == null) {
                ShaderLinkHelper.func_148076_a();
            }
            ResourceLocation resourceLocation = new ResourceLocation("shaders/post/entity_outline.json");
            try {
                this.field_174991_A = new ShaderGroup(this.field_72777_q.func_110434_K(), this.field_72777_q.func_110442_L(), this.field_72777_q.func_147110_a(), resourceLocation);
                this.field_174991_A.func_148026_a(this.field_72777_q.field_71443_c, this.field_72777_q.field_71440_d);
                this.field_175015_z = this.field_174991_A.func_177066_a("final");
            }
            catch (IOException \u26032) {
                field_147599_m.warn("Failed to load shader: {}", (Object)resourceLocation, (Object)\u26032);
                this.field_174991_A = null;
                this.field_175015_z = null;
            }
            catch (JsonSyntaxException \u26033) {
                field_147599_m.warn("Failed to load shader: {}", (Object)resourceLocation, (Object)\u26033);
                this.field_174991_A = null;
                this.field_175015_z = null;
            }
        } else {
            this.field_174991_A = null;
            this.field_175015_z = null;
        }
    }

    public void func_174975_c() {
        if (this.func_174985_d()) {
            GlStateManager.func_179147_l();
            GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.field_175015_z.func_178038_a(this.field_72777_q.field_71443_c, this.field_72777_q.field_71440_d, false);
            GlStateManager.func_179084_k();
        }
    }

    protected boolean func_174985_d() {
        return this.field_175015_z != null && this.field_174991_A != null && this.field_72777_q.field_71439_g != null;
    }

    private void func_174964_o() {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder \u26032 = tessellator.func_178180_c();
        if (this.field_175011_u != null) {
            this.field_175011_u.func_177362_c();
        }
        if (this.field_72781_x >= 0) {
            GLAllocation.func_74523_b(this.field_72781_x);
            this.field_72781_x = -1;
        }
        if (this.field_175005_X) {
            this.field_175011_u = new VertexBuffer(this.field_175014_r);
            this.func_174968_a(\u26032, -16.0f, true);
            \u26032.func_178977_d();
            \u26032.func_178965_a();
            this.field_175011_u.func_181722_a(\u26032.func_178966_f());
        } else {
            this.field_72781_x = GLAllocation.func_74526_a(1);
            GlStateManager.func_187423_f(this.field_72781_x, 4864);
            this.func_174968_a(\u26032, -16.0f, true);
            tessellator.func_78381_a();
            GlStateManager.func_187415_K();
        }
    }

    private void func_174980_p() {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder \u26032 = tessellator.func_178180_c();
        if (this.field_175012_t != null) {
            this.field_175012_t.func_177362_c();
        }
        if (this.field_72771_w >= 0) {
            GLAllocation.func_74523_b(this.field_72771_w);
            this.field_72771_w = -1;
        }
        if (this.field_175005_X) {
            this.field_175012_t = new VertexBuffer(this.field_175014_r);
            this.func_174968_a(\u26032, 16.0f, false);
            \u26032.func_178977_d();
            \u26032.func_178965_a();
            this.field_175012_t.func_181722_a(\u26032.func_178966_f());
        } else {
            this.field_72771_w = GLAllocation.func_74526_a(1);
            GlStateManager.func_187423_f(this.field_72771_w, 4864);
            this.func_174968_a(\u26032, 16.0f, false);
            tessellator.func_78381_a();
            GlStateManager.func_187415_K();
        }
    }

    private void func_174968_a(BufferBuilder bufferBuilder, float f, boolean bl) {
        int n = 64;
        \u2603 = 6;
        bufferBuilder.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        for (\u2603 = -384; \u2603 <= 384; \u2603 += 64) {
            for (\u2603 = -384; \u2603 <= 384; \u2603 += 64) {
                float f2 = \u2603;
                \u2603 = \u2603 + 64;
                if (bl) {
                    \u2603 = \u2603;
                    f2 = \u2603 + 64;
                }
                bufferBuilder.func_181662_b(f2, f, \u2603).func_181675_d();
                bufferBuilder.func_181662_b(\u2603, f, \u2603).func_181675_d();
                bufferBuilder.func_181662_b(\u2603, f, \u2603 + 64).func_181675_d();
                bufferBuilder.func_181662_b(f2, f, \u2603 + 64).func_181675_d();
            }
        }
    }

    private void func_174963_q() {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder \u26032 = tessellator.func_178180_c();
        if (this.field_175013_s != null) {
            this.field_175013_s.func_177362_c();
        }
        if (this.field_72772_v >= 0) {
            GLAllocation.func_74523_b(this.field_72772_v);
            this.field_72772_v = -1;
        }
        if (this.field_175005_X) {
            this.field_175013_s = new VertexBuffer(this.field_175014_r);
            this.func_180444_a(\u26032);
            \u26032.func_178977_d();
            \u26032.func_178965_a();
            this.field_175013_s.func_181722_a(\u26032.func_178966_f());
        } else {
            this.field_72772_v = GLAllocation.func_74526_a(1);
            GlStateManager.func_179094_E();
            GlStateManager.func_187423_f(this.field_72772_v, 4864);
            this.func_180444_a(\u26032);
            tessellator.func_78381_a();
            GlStateManager.func_187415_K();
            GlStateManager.func_179121_F();
        }
    }

    private void func_180444_a(BufferBuilder bufferBuilder) {
        Random random = new Random(10842L);
        bufferBuilder.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        for (int i = 0; i < 1500; ++i) {
            double d = random.nextFloat() * 2.0f - 1.0f;
            \u2603 = random.nextFloat() * 2.0f - 1.0f;
            \u2603 = random.nextFloat() * 2.0f - 1.0f;
            \u2603 = 0.15f + random.nextFloat() * 0.1f;
            \u2603 = d * d + \u2603 * \u2603 + \u2603 * \u2603;
            if (!(\u2603 < 1.0) || !(\u2603 > 0.01)) continue;
            \u2603 = 1.0 / Math.sqrt(\u2603);
            \u2603 = (d *= \u2603) * 100.0;
            \u2603 = (\u2603 *= \u2603) * 100.0;
            \u2603 = (\u2603 *= \u2603) * 100.0;
            \u2603 = Math.atan2(d, \u2603);
            \u2603 = Math.sin(\u2603);
            \u2603 = Math.cos(\u2603);
            \u2603 = Math.atan2(Math.sqrt(d * d + \u2603 * \u2603), \u2603);
            \u2603 = Math.sin(\u2603);
            \u2603 = Math.cos(\u2603);
            \u2603 = random.nextDouble() * Math.PI * 2.0;
            \u2603 = Math.sin(\u2603);
            \u2603 = Math.cos(\u2603);
            for (int j = 0; j < 4; ++j) {
                double d2 = 0.0;
                \u2603 = (double)((j & 2) - 1) * \u2603;
                \u2603 = (double)((j + 1 & 2) - 1) * \u2603;
                \u2603 = 0.0;
                \u2603 = \u2603 * \u2603 - \u2603 * \u2603;
                \u2603 = \u2603 = \u2603 * \u2603 + \u2603 * \u2603;
                \u2603 = \u2603 * \u2603 + 0.0 * \u2603;
                \u2603 = 0.0 * \u2603 - \u2603 * \u2603;
                \u2603 = \u2603 * \u2603 - \u2603 * \u2603;
                \u2603 = \u2603;
                \u2603 = \u2603 * \u2603 + \u2603 * \u2603;
                bufferBuilder.func_181662_b(\u2603 + \u2603, \u2603 + \u2603, \u2603 + \u2603).func_181675_d();
            }
        }
    }

    public void func_72732_a(@Nullable WorldClient worldClient) {
        if (this.field_72769_h != null) {
            this.field_72769_h.func_72848_b(this);
        }
        this.field_174992_B = Double.MIN_VALUE;
        this.field_174993_C = Double.MIN_VALUE;
        this.field_174987_D = Double.MIN_VALUE;
        this.field_174988_E = Integer.MIN_VALUE;
        this.field_174989_F = Integer.MIN_VALUE;
        this.field_174990_G = Integer.MIN_VALUE;
        this.field_175010_j.func_78717_a(worldClient);
        this.field_72769_h = worldClient;
        if (worldClient != null) {
            worldClient.func_72954_a(this);
            this.func_72712_a();
        } else {
            this.field_175009_l.clear();
            this.field_72755_R.clear();
            if (this.field_175008_n != null) {
                this.field_175008_n.func_178160_a();
                this.field_175008_n = null;
            }
            if (this.field_174995_M != null) {
                this.field_174995_M.func_188244_g();
            }
            this.field_174995_M = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void func_72712_a() {
        if (this.field_72769_h == null) {
            return;
        }
        if (this.field_174995_M == null) {
            this.field_174995_M = new ChunkRenderDispatcher();
        }
        this.field_147595_R = true;
        Blocks.field_150362_t.func_150122_b(this.field_72777_q.field_71474_y.field_74347_j);
        Blocks.field_150361_u.func_150122_b(this.field_72777_q.field_71474_y.field_74347_j);
        this.field_72739_F = this.field_72777_q.field_71474_y.field_151451_c;
        boolean bl = this.field_175005_X;
        this.field_175005_X = OpenGlHelper.func_176075_f();
        if (bl && !this.field_175005_X) {
            this.field_174996_N = new RenderList();
            this.field_175007_a = new ListChunkFactory();
        } else if (!bl && this.field_175005_X) {
            this.field_174996_N = new VboRenderList();
            this.field_175007_a = new VboChunkFactory();
        }
        if (bl != this.field_175005_X) {
            this.func_174963_q();
            this.func_174980_p();
            this.func_174964_o();
        }
        if (this.field_175008_n != null) {
            this.field_175008_n.func_178160_a();
        }
        this.func_174986_e();
        Set<TileEntity> set = this.field_181024_n;
        synchronized (set) {
            this.field_181024_n.clear();
        }
        this.field_175008_n = new ViewFrustum(this.field_72769_h, this.field_72777_q.field_71474_y.field_151451_c, this, this.field_175007_a);
        if (this.field_72769_h != null && (\u2603 = this.field_72777_q.func_175606_aa()) != null) {
            this.field_175008_n.func_178163_a(\u2603.field_70165_t, \u2603.field_70161_v);
        }
        this.field_72740_G = 2;
    }

    protected void func_174986_e() {
        this.field_175009_l.clear();
        this.field_174995_M.func_178514_b();
    }

    public void func_72720_a(int n, int n2) {
        if (!OpenGlHelper.field_148824_g) {
            return;
        }
        if (this.field_174991_A != null) {
            this.field_174991_A.func_148026_a(n, n2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    public void func_180446_a(Entity entity, ICamera iCamera, float f2) {
        Object object;
        if (this.field_72740_G > 0) {
            --this.field_72740_G;
            return;
        }
        double d = entity.field_70169_q + (entity.field_70165_t - entity.field_70169_q) * (double)f2;
        double d2 = entity.field_70167_r + (entity.field_70163_u - entity.field_70167_r) * (double)f2;
        \u2603 = entity.field_70166_s + (entity.field_70161_v - entity.field_70166_s) * (double)f2;
        this.field_72769_h.field_72984_F.func_76320_a("prepare");
        TileEntityRendererDispatcher.field_147556_a.func_190056_a(this.field_72769_h, this.field_72777_q.func_110434_K(), this.field_72777_q.field_71466_p, this.field_72777_q.func_175606_aa(), this.field_72777_q.field_71476_x, f2);
        this.field_175010_j.func_180597_a(this.field_72769_h, this.field_72777_q.field_71466_p, this.field_72777_q.func_175606_aa(), this.field_72777_q.field_147125_j, this.field_72777_q.field_71474_y, f2);
        this.field_72748_H = 0;
        this.field_72749_I = 0;
        this.field_72750_J = 0;
        Entity \u26032 = this.field_72777_q.func_175606_aa();
        \u2603 = \u26032.field_70142_S + (\u26032.field_70165_t - \u26032.field_70142_S) * (double)f2;
        \u2603 = \u26032.field_70137_T + (\u26032.field_70163_u - \u26032.field_70137_T) * (double)f2;
        \u2603 = \u26032.field_70136_U + (\u26032.field_70161_v - \u26032.field_70136_U) * (double)f2;
        TileEntityRendererDispatcher.field_147554_b = \u2603;
        TileEntityRendererDispatcher.field_147555_c = \u2603;
        TileEntityRendererDispatcher.field_147552_d = \u2603;
        this.field_175010_j.func_178628_a(\u2603, \u2603, \u2603);
        this.field_72777_q.field_71460_t.func_180436_i();
        this.field_72769_h.field_72984_F.func_76318_c("global");
        List<Entity> \u26033 = this.field_72769_h.func_72910_y();
        this.field_72748_H = \u26033.size();
        for (int i = 0; i < this.field_72769_h.field_73007_j.size(); ++i) {
            Entity \u26034 = (Entity)this.field_72769_h.field_73007_j.get(i);
            ++this.field_72749_I;
            if (!\u26034.func_145770_h(d, d2, \u2603)) continue;
            this.field_175010_j.func_188388_a(\u26034, f2, false);
        }
        this.field_72769_h.field_72984_F.func_76318_c("entities");
        ArrayList arrayList = Lists.newArrayList();
        ArrayList \u26037 = Lists.newArrayList();
        BlockPos.PooledMutableBlockPos \u26035 = BlockPos.PooledMutableBlockPos.func_185346_s();
        for (ContainerLocalRenderInformation containerLocalRenderInformation : this.field_72755_R) {
            Chunk chunk = this.field_72769_h.func_175726_f(containerLocalRenderInformation.field_178036_a.func_178568_j());
            object = chunk.func_177429_s()[containerLocalRenderInformation.field_178036_a.func_178568_j().func_177956_o() / 16];
            if (((AbstractCollection)object).isEmpty()) continue;
            Iterator<Entity> iterator = ((ClassInheritanceMultiMap)object).iterator();
            while (iterator.hasNext()) {
                Entity entity2 = iterator.next();
                boolean \u26036 = this.field_175010_j.func_178635_a(entity2, iCamera, d, d2, \u2603) || entity2.func_184215_y(this.field_72777_q.field_71439_g);
                if (!\u26036) continue;
                boolean bl = \u2603 = this.field_72777_q.func_175606_aa() instanceof EntityLivingBase ? ((EntityLivingBase)this.field_72777_q.func_175606_aa()).func_70608_bn() : false;
                if (entity2 == this.field_72777_q.func_175606_aa() && this.field_72777_q.field_71474_y.field_74320_O == 0 && !\u2603 || entity2.field_70163_u >= 0.0 && entity2.field_70163_u < 256.0 && !this.field_72769_h.func_175667_e(\u26035.func_189535_a(entity2))) continue;
                ++this.field_72749_I;
                this.field_175010_j.func_188388_a(entity2, f2, false);
                if (this.func_184383_a(entity2, \u26032, iCamera)) {
                    arrayList.add(entity2);
                }
                if (!this.field_175010_j.func_188390_b(entity2)) continue;
                \u26037.add(entity2);
            }
        }
        \u26035.func_185344_t();
        if (!\u26037.isEmpty()) {
            for (Entity entity2 : \u26037) {
                this.field_175010_j.func_188389_a(entity2, f2);
            }
        }
        if (this.func_174985_d() && (!arrayList.isEmpty() || this.field_184386_ad)) {
            this.field_72769_h.field_72984_F.func_76318_c("entityOutlines");
            this.field_175015_z.func_147614_f();
            boolean bl = this.field_184386_ad = !arrayList.isEmpty();
            if (!arrayList.isEmpty()) {
                GlStateManager.func_179143_c(519);
                GlStateManager.func_179106_n();
                this.field_175015_z.func_147610_a(false);
                RenderHelper.func_74518_a();
                this.field_175010_j.func_178632_c(true);
                for (int i = 0; i < arrayList.size(); ++i) {
                    this.field_175010_j.func_188388_a((Entity)arrayList.get(i), f2, false);
                }
                this.field_175010_j.func_178632_c(false);
                RenderHelper.func_74519_b();
                GlStateManager.func_179132_a(false);
                this.field_174991_A.func_148018_a(f2);
                GlStateManager.func_179145_e();
                GlStateManager.func_179132_a(true);
                GlStateManager.func_179127_m();
                GlStateManager.func_179147_l();
                GlStateManager.func_179142_g();
                GlStateManager.func_179143_c(515);
                GlStateManager.func_179126_j();
                GlStateManager.func_179141_d();
            }
            this.field_72777_q.func_147110_a().func_147610_a(false);
        }
        this.field_72769_h.field_72984_F.func_76318_c("blockentities");
        RenderHelper.func_74519_b();
        for (ContainerLocalRenderInformation containerLocalRenderInformation : this.field_72755_R) {
            List<TileEntity> list = containerLocalRenderInformation.field_178036_a.func_178571_g().func_178485_b();
            if (list.isEmpty()) continue;
            for (Object object2 : list) {
                TileEntityRendererDispatcher.field_147556_a.func_180546_a((TileEntity)object2, f2, -1);
            }
        }
        Set<TileEntity> i = this.field_181024_n;
        synchronized (i) {
            for (TileEntity tileEntity : this.field_181024_n) {
                TileEntityRendererDispatcher.field_147556_a.func_180546_a(tileEntity, f2, -1);
            }
        }
        this.func_180443_s();
        for (DestroyBlockProgress destroyBlockProgress : this.field_72738_E.values()) {
            void var23_36;
            BlockPos blockPos = destroyBlockProgress.func_180246_b();
            if (!this.field_72769_h.func_180495_p(blockPos).func_177230_c().func_149716_u()) continue;
            object = this.field_72769_h.func_175625_s(blockPos);
            if (object instanceof TileEntityChest) {
                object2 = (TileEntityChest)object;
                if (((TileEntityChest)object2).field_145991_k != null) {
                    BlockPos blockPos2 = blockPos.func_177972_a(EnumFacing.WEST);
                    object = this.field_72769_h.func_175625_s(blockPos2);
                } else if (((TileEntityChest)object2).field_145992_i != null) {
                    BlockPos blockPos3 = blockPos.func_177972_a(EnumFacing.NORTH);
                    object = this.field_72769_h.func_175625_s(blockPos3);
                }
            }
            object2 = this.field_72769_h.func_180495_p((BlockPos)var23_36);
            if (object == null || !object2.func_191057_i()) continue;
            TileEntityRendererDispatcher.field_147556_a.func_180546_a((TileEntity)object, f2, destroyBlockProgress.func_73106_e());
        }
        this.func_174969_t();
        this.field_72777_q.field_71460_t.func_175072_h();
        this.field_72777_q.field_71424_I.func_76319_b();
    }

    private boolean func_184383_a(Entity entity, Entity entity2, ICamera iCamera) {
        boolean bl = \u2603 = entity2 instanceof EntityLivingBase && ((EntityLivingBase)entity2).func_70608_bn();
        if (entity == entity2 && this.field_72777_q.field_71474_y.field_74320_O == 0 && !\u2603) {
            return false;
        }
        if (entity.func_184202_aL()) {
            return true;
        }
        if (this.field_72777_q.field_71439_g.func_175149_v() && this.field_72777_q.field_71474_y.field_178883_an.func_151470_d() && entity instanceof EntityPlayer) {
            return entity.field_70158_ak || iCamera.func_78546_a(entity.func_174813_aQ()) || entity.func_184215_y(this.field_72777_q.field_71439_g);
        }
        return false;
    }

    public String func_72735_c() {
        int n = this.field_175008_n.field_178164_f.length;
        \u2603 = this.func_184382_g();
        return String.format("C: %d/%d %sD: %d, L: %d, %s", \u2603, n, this.field_72777_q.field_175612_E ? "(s) " : "", this.field_72739_F, this.field_184387_ae.size(), this.field_174995_M == null ? "null" : this.field_174995_M.func_178504_a());
    }

    protected int func_184382_g() {
        int n = 0;
        for (ContainerLocalRenderInformation containerLocalRenderInformation : this.field_72755_R) {
            CompiledChunk compiledChunk = containerLocalRenderInformation.field_178036_a.field_178590_b;
            if (compiledChunk == CompiledChunk.field_178502_a || compiledChunk.func_178489_a()) continue;
            ++n;
        }
        return n;
    }

    public String func_72723_d() {
        return "E: " + this.field_72749_I + "/" + this.field_72748_H + ", B: " + this.field_72750_J;
    }

    public void func_174970_a(Entity entity, double d, ICamera object, int n, boolean bl2) {
        if (this.field_72777_q.field_71474_y.field_151451_c != this.field_72739_F) {
            this.func_72712_a();
        }
        this.field_72769_h.field_72984_F.func_76320_a("camera");
        double d3 = entity.field_70165_t - this.field_174992_B;
        double d2 = entity.field_70163_u - this.field_174993_C;
        \u2603 = entity.field_70161_v - this.field_174987_D;
        if (this.field_174988_E != entity.field_70176_ah || this.field_174989_F != entity.field_70162_ai || this.field_174990_G != entity.field_70164_aj || d3 * d3 + d2 * d2 + \u2603 * \u2603 > 16.0) {
            this.field_174992_B = entity.field_70165_t;
            this.field_174993_C = entity.field_70163_u;
            this.field_174987_D = entity.field_70161_v;
            this.field_174988_E = entity.field_70176_ah;
            this.field_174989_F = entity.field_70162_ai;
            this.field_174990_G = entity.field_70164_aj;
            this.field_175008_n.func_178163_a(entity.field_70165_t, entity.field_70161_v);
        }
        this.field_72769_h.field_72984_F.func_76318_c("renderlistcamera");
        double d22 = entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * d;
        d4 = entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * d;
        \u2603 = entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * d;
        this.field_174996_N.func_178004_a(d22, d4, \u2603);
        this.field_72769_h.field_72984_F.func_76318_c("cull");
        if (this.field_175001_U != null) {
            Frustum \u26032 = new Frustum(this.field_175001_U);
            \u26032.func_78547_a(this.field_175003_W.field_181059_a, this.field_175003_W.field_181060_b, this.field_175003_W.field_181061_c);
            object = \u26032;
        }
        this.field_72777_q.field_71424_I.func_76318_c("culling");
        BlockPos \u260315 = new BlockPos(d22, d4 + (double)entity.func_70047_e(), \u2603);
        RenderChunk \u26033 = this.field_175008_n.func_178161_a(\u260315);
        BlockPos \u26034 = new BlockPos(MathHelper.func_76128_c(d22 / 16.0) * 16, MathHelper.func_76128_c(d4 / 16.0) * 16, MathHelper.func_76128_c(\u2603 / 16.0) * 16);
        this.field_147595_R = this.field_147595_R || !this.field_175009_l.isEmpty() || entity.field_70165_t != this.field_174997_H || entity.field_70163_u != this.field_174998_I || entity.field_70161_v != this.field_174999_J || (double)entity.field_70125_A != this.field_175000_K || (double)entity.field_70177_z != this.field_174994_L;
        this.field_174997_H = entity.field_70165_t;
        this.field_174998_I = entity.field_70163_u;
        this.field_174999_J = entity.field_70161_v;
        this.field_175000_K = entity.field_70125_A;
        this.field_174994_L = entity.field_70177_z;
        boolean \u26035 = this.field_175001_U != null;
        this.field_72777_q.field_71424_I.func_76318_c("update");
        if (!\u26035 && this.field_147595_R) {
            this.field_147595_R = false;
            this.field_72755_R = Lists.newArrayList();
            ArrayDeque collection = Queues.newArrayDeque();
            Entity.func_184227_b(MathHelper.func_151237_a((double)this.field_72777_q.field_71474_y.field_151451_c / 8.0, 1.0, 2.5));
            boolean \u26036 = this.field_72777_q.field_175612_E;
            if (\u26033 == null) {
                int n2 = \u260315.func_177956_o() > 0 ? 248 : 8;
                for (\u2603 = -this.field_72739_F; \u2603 <= this.field_72739_F; ++\u2603) {
                    for (\u2603 = -this.field_72739_F; \u2603 <= this.field_72739_F; ++\u2603) {
                        RenderChunk renderChunk = this.field_175008_n.func_178161_a(new BlockPos((\u2603 << 4) + 8, n2, (\u2603 << 4) + 8));
                        if (renderChunk == null || !object.func_78546_a(renderChunk.field_178591_c)) continue;
                        renderChunk.func_178577_a(n);
                        collection.add(new ContainerLocalRenderInformation(renderChunk, null, 0));
                    }
                }
            } else {
                boolean \u260310 = false;
                ContainerLocalRenderInformation \u26037 = new ContainerLocalRenderInformation(\u26033, null, 0);
                Set<EnumFacing> \u26038 = this.func_174978_c(\u260315);
                if (\u26038.size() == 1) {
                    Vector3f vector3f = this.func_174962_a(entity, d);
                    EnumFacing \u26039 = EnumFacing.func_176737_a(vector3f.x, vector3f.y, vector3f.z).func_176734_d();
                    \u26038.remove(\u26039);
                }
                if (\u26038.isEmpty()) {
                    \u260310 = true;
                }
                if (!\u260310 || bl2) {
                    if (bl2 && this.field_72769_h.func_180495_p(\u260315).func_185914_p()) {
                        \u26036 = false;
                    }
                    \u26033.func_178577_a(n);
                    collection.add(\u26037);
                } else {
                    this.field_72755_R.add(\u26037);
                }
            }
            this.field_72777_q.field_71424_I.func_76320_a("iteration");
            while (!collection.isEmpty()) {
                ContainerLocalRenderInformation containerLocalRenderInformation = (ContainerLocalRenderInformation)collection.poll();
                RenderChunk \u260311 = containerLocalRenderInformation.field_178036_a;
                EnumFacing \u260312 = containerLocalRenderInformation.field_178034_b;
                this.field_72755_R.add(containerLocalRenderInformation);
                for (EnumFacing enumFacing : EnumFacing.values()) {
                    RenderChunk renderChunk = this.func_181562_a(\u26034, \u260311, enumFacing);
                    if (\u26036 && containerLocalRenderInformation.func_189560_a(enumFacing.func_176734_d()) || \u26036 && \u260312 != null && !\u260311.func_178571_g().func_178495_a(\u260312.func_176734_d(), enumFacing) || renderChunk == null || !renderChunk.func_178577_a(n) || !object.func_78546_a(renderChunk.field_178591_c)) continue;
                    ContainerLocalRenderInformation \u260313 = new ContainerLocalRenderInformation(renderChunk, enumFacing, containerLocalRenderInformation.field_178032_d + 1);
                    \u260313.func_189561_a(containerLocalRenderInformation.field_178035_c, enumFacing);
                    collection.add(\u260313);
                }
            }
            this.field_72777_q.field_71424_I.func_76319_b();
        }
        this.field_72777_q.field_71424_I.func_76318_c("captureFrustum");
        if (this.field_175002_T) {
            double d4;
            this.func_174984_a(d22, d4, \u2603);
            this.field_175002_T = false;
        }
        this.field_72777_q.field_71424_I.func_76318_c("rebuildNear");
        Set<RenderChunk> set = this.field_175009_l;
        this.field_175009_l = Sets.newLinkedHashSet();
        for (ContainerLocalRenderInformation containerLocalRenderInformation : this.field_72755_R) {
            boolean bl;
            RenderChunk renderChunk = containerLocalRenderInformation.field_178036_a;
            if (!renderChunk.func_178569_m() && !set.contains(renderChunk)) continue;
            this.field_147595_R = true;
            BlockPos \u260314 = renderChunk.func_178568_j().func_177982_a(8, 8, 8);
            boolean bl3 = bl = \u260314.func_177951_i(\u260315) < 768.0;
            if (renderChunk.func_188281_o() || bl) {
                this.field_72777_q.field_71424_I.func_76320_a("build near");
                this.field_174995_M.func_178505_b(renderChunk);
                renderChunk.func_188282_m();
                this.field_72777_q.field_71424_I.func_76319_b();
                continue;
            }
            this.field_175009_l.add(renderChunk);
        }
        this.field_175009_l.addAll(set);
        this.field_72777_q.field_71424_I.func_76319_b();
    }

    private Set<EnumFacing> func_174978_c(BlockPos blockPos) {
        VisGraph visGraph = new VisGraph();
        BlockPos \u26032 = new BlockPos(blockPos.func_177958_n() >> 4 << 4, blockPos.func_177956_o() >> 4 << 4, blockPos.func_177952_p() >> 4 << 4);
        Chunk \u26033 = this.field_72769_h.func_175726_f(\u26032);
        for (BlockPos.MutableBlockPos mutableBlockPos : BlockPos.func_177975_b(\u26032, \u26032.func_177982_a(15, 15, 15))) {
            if (!\u26033.func_177435_g(mutableBlockPos).func_185914_p()) continue;
            visGraph.func_178606_a(mutableBlockPos);
        }
        return visGraph.func_178609_b(blockPos);
    }

    @Nullable
    private RenderChunk func_181562_a(BlockPos blockPos, RenderChunk renderChunk, EnumFacing enumFacing) {
        BlockPos blockPos2 = renderChunk.func_181701_a(enumFacing);
        if (MathHelper.func_76130_a(blockPos.func_177958_n() - blockPos2.func_177958_n()) > this.field_72739_F * 16) {
            return null;
        }
        if (blockPos2.func_177956_o() < 0 || blockPos2.func_177956_o() >= 256) {
            return null;
        }
        if (MathHelper.func_76130_a(blockPos.func_177952_p() - blockPos2.func_177952_p()) > this.field_72739_F * 16) {
            return null;
        }
        return this.field_175008_n.func_178161_a(blockPos2);
    }

    private void func_174984_a(double d, double d2, double d3) {
        this.field_175001_U = new ClippingHelperImpl();
        ((ClippingHelperImpl)this.field_175001_U).func_78560_b();
        Matrix4f matrix4f = new Matrix4f(this.field_175001_U.field_178626_c);
        matrix4f.transpose();
        \u2603 = new Matrix4f(this.field_175001_U.field_178625_b);
        \u2603.transpose();
        \u2603 = new Matrix4f();
        Matrix4f.mul((org.lwjgl.util.vector.Matrix4f)\u2603, (org.lwjgl.util.vector.Matrix4f)matrix4f, (org.lwjgl.util.vector.Matrix4f)\u2603);
        \u2603.invert();
        this.field_175003_W.field_181059_a = d;
        this.field_175003_W.field_181060_b = d2;
        this.field_175003_W.field_181061_c = d3;
        this.field_175004_V[0] = new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f);
        this.field_175004_V[1] = new Vector4f(1.0f, -1.0f, -1.0f, 1.0f);
        this.field_175004_V[2] = new Vector4f(1.0f, 1.0f, -1.0f, 1.0f);
        this.field_175004_V[3] = new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f);
        this.field_175004_V[4] = new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f);
        this.field_175004_V[5] = new Vector4f(1.0f, -1.0f, 1.0f, 1.0f);
        this.field_175004_V[6] = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.field_175004_V[7] = new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < 8; ++i) {
            Matrix4f.transform((org.lwjgl.util.vector.Matrix4f)\u2603, (Vector4f)this.field_175004_V[i], (Vector4f)this.field_175004_V[i]);
            this.field_175004_V[i].x /= this.field_175004_V[i].w;
            this.field_175004_V[i].y /= this.field_175004_V[i].w;
            this.field_175004_V[i].z /= this.field_175004_V[i].w;
            this.field_175004_V[i].w = 1.0f;
        }
    }

    protected Vector3f func_174962_a(Entity entity, double d) {
        float f = (float)((double)entity.field_70127_C + (double)(entity.field_70125_A - entity.field_70127_C) * d);
        \u2603 = (float)((double)entity.field_70126_B + (double)(entity.field_70177_z - entity.field_70126_B) * d);
        if (Minecraft.func_71410_x().field_71474_y.field_74320_O == 2) {
            f += 180.0f;
        }
        \u2603 = MathHelper.func_76134_b(-\u2603 * ((float)Math.PI / 180) - (float)Math.PI);
        \u2603 = MathHelper.func_76126_a(-\u2603 * ((float)Math.PI / 180) - (float)Math.PI);
        \u2603 = -MathHelper.func_76134_b(-f * ((float)Math.PI / 180));
        \u2603 = MathHelper.func_76126_a(-f * ((float)Math.PI / 180));
        return new Vector3f(\u2603 * \u2603, \u2603, \u2603 * \u2603);
    }

    public int func_174977_a(BlockRenderLayer blockRenderLayer2, double d, int n, Entity entity) {
        BlockRenderLayer blockRenderLayer2;
        RenderHelper.func_74518_a();
        if (blockRenderLayer2 == BlockRenderLayer.TRANSLUCENT) {
            this.field_72777_q.field_71424_I.func_76320_a("translucent_sort");
            double d2 = entity.field_70165_t - this.field_147596_f;
            \u2603 = entity.field_70163_u - this.field_147597_g;
            \u2603 = entity.field_70161_v - this.field_147602_h;
            if (d2 * d2 + \u2603 * \u2603 + \u2603 * \u2603 > 1.0) {
                this.field_147596_f = entity.field_70165_t;
                this.field_147597_g = entity.field_70163_u;
                this.field_147602_h = entity.field_70161_v;
                int n2 = 0;
                for (ContainerLocalRenderInformation containerLocalRenderInformation : this.field_72755_R) {
                    if (!containerLocalRenderInformation.field_178036_a.field_178590_b.func_178492_d(blockRenderLayer2) || n2++ >= 15) continue;
                    this.field_174995_M.func_178509_c(containerLocalRenderInformation.field_178036_a);
                }
            }
            this.field_72777_q.field_71424_I.func_76319_b();
        }
        this.field_72777_q.field_71424_I.func_76320_a("filterempty");
        int n3 = 0;
        boolean \u26032 = blockRenderLayer2 == BlockRenderLayer.TRANSLUCENT;
        \u2603 = \u26032 ? this.field_72755_R.size() - 1 : 0;
        \u2603 = \u26032 ? -1 : this.field_72755_R.size();
        \u2603 = \u26032 ? -1 : 1;
        for (\u2603 = \u2603; \u2603 != \u2603; \u2603 += \u2603) {
            RenderChunk renderChunk = this.field_72755_R.get((int)\u2603).field_178036_a;
            if (renderChunk.func_178571_g().func_178491_b(blockRenderLayer2)) continue;
            ++n3;
            this.field_174996_N.func_178002_a(renderChunk, blockRenderLayer2);
        }
        this.field_72777_q.field_71424_I.func_194339_b(() -> "render_" + (Object)((Object)blockRenderLayer2));
        this.func_174982_a(blockRenderLayer2);
        this.field_72777_q.field_71424_I.func_76319_b();
        return n3;
    }

    private void func_174982_a(BlockRenderLayer blockRenderLayer) {
        this.field_72777_q.field_71460_t.func_180436_i();
        if (OpenGlHelper.func_176075_f()) {
            GlStateManager.func_187410_q(32884);
            OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
            GlStateManager.func_187410_q(32888);
            OpenGlHelper.func_77472_b(OpenGlHelper.field_77476_b);
            GlStateManager.func_187410_q(32888);
            OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
            GlStateManager.func_187410_q(32886);
        }
        this.field_174996_N.func_178001_a(blockRenderLayer);
        if (OpenGlHelper.func_176075_f()) {
            List<VertexFormatElement> list = DefaultVertexFormats.field_176600_a.func_177343_g();
            for (VertexFormatElement vertexFormatElement : list) {
                VertexFormatElement.EnumUsage enumUsage = vertexFormatElement.func_177375_c();
                int \u26032 = vertexFormatElement.func_177369_e();
                switch (enumUsage) {
                    case POSITION: {
                        GlStateManager.func_187429_p(32884);
                        break;
                    }
                    case UV: {
                        OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a + \u26032);
                        GlStateManager.func_187429_p(32888);
                        OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
                        break;
                    }
                    case COLOR: {
                        GlStateManager.func_187429_p(32886);
                        GlStateManager.func_179117_G();
                    }
                }
            }
        }
        this.field_72777_q.field_71460_t.func_175072_h();
    }

    private void func_174965_a(Iterator<DestroyBlockProgress> iterator) {
        while (iterator.hasNext()) {
            DestroyBlockProgress destroyBlockProgress = iterator.next();
            int \u26032 = destroyBlockProgress.func_82743_f();
            if (this.field_72773_u - \u26032 <= 400) continue;
            iterator.remove();
        }
    }

    public void func_72734_e() {
        ++this.field_72773_u;
        if (this.field_72773_u % 20 == 0) {
            this.func_174965_a(this.field_72738_E.values().iterator());
        }
        if (!this.field_184387_ae.isEmpty() && !this.field_174995_M.func_188248_h() && this.field_175009_l.isEmpty()) {
            Iterator<BlockPos> iterator = this.field_184387_ae.iterator();
            while (iterator.hasNext()) {
                BlockPos blockPos = iterator.next();
                iterator.remove();
                int \u26032 = blockPos.func_177958_n();
                int \u26033 = blockPos.func_177956_o();
                int \u26034 = blockPos.func_177952_p();
                this.func_184385_a(\u26032 - 1, \u26033 - 1, \u26034 - 1, \u26032 + 1, \u26033 + 1, \u26034 + 1, false);
            }
        }
    }

    private void func_180448_r() {
        GlStateManager.func_179106_n();
        GlStateManager.func_179118_c();
        GlStateManager.func_179147_l();
        GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.func_74518_a();
        GlStateManager.func_179132_a(false);
        this.field_72770_i.func_110577_a(field_110926_k);
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder \u26032 = tessellator.func_178180_c();
        for (int i = 0; i < 6; ++i) {
            GlStateManager.func_179094_E();
            if (i == 1) {
                GlStateManager.func_179114_b(90.0f, 1.0f, 0.0f, 0.0f);
            }
            if (i == 2) {
                GlStateManager.func_179114_b(-90.0f, 1.0f, 0.0f, 0.0f);
            }
            if (i == 3) {
                GlStateManager.func_179114_b(180.0f, 1.0f, 0.0f, 0.0f);
            }
            if (i == 4) {
                GlStateManager.func_179114_b(90.0f, 0.0f, 0.0f, 1.0f);
            }
            if (i == 5) {
                GlStateManager.func_179114_b(-90.0f, 0.0f, 0.0f, 1.0f);
            }
            \u26032.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            \u26032.func_181662_b(-100.0, -100.0, -100.0).func_187315_a(0.0, 0.0).func_181669_b(40, 40, 40, 255).func_181675_d();
            \u26032.func_181662_b(-100.0, -100.0, 100.0).func_187315_a(0.0, 16.0).func_181669_b(40, 40, 40, 255).func_181675_d();
            \u26032.func_181662_b(100.0, -100.0, 100.0).func_187315_a(16.0, 16.0).func_181669_b(40, 40, 40, 255).func_181675_d();
            \u26032.func_181662_b(100.0, -100.0, -100.0).func_187315_a(16.0, 0.0).func_181669_b(40, 40, 40, 255).func_181675_d();
            tessellator.func_78381_a();
            GlStateManager.func_179121_F();
        }
        GlStateManager.func_179132_a(true);
        GlStateManager.func_179098_w();
        GlStateManager.func_179141_d();
    }

    public void func_174976_a(float f2, int n) {
        float f2;
        int \u26039;
        int \u26037;
        if (this.field_72777_q.field_71441_e.field_73011_w.func_186058_p().func_186068_a() == 1) {
            this.func_180448_r();
            return;
        }
        if (!this.field_72777_q.field_71441_e.field_73011_w.func_76569_d()) {
            return;
        }
        GlStateManager.func_179090_x();
        Vec3d vec3d = this.field_72769_h.func_72833_a(this.field_72777_q.func_175606_aa(), f2);
        float \u26032 = (float)vec3d.field_72450_a;
        float \u26033 = (float)vec3d.field_72448_b;
        float \u26034 = (float)vec3d.field_72449_c;
        if (n != 2) {
            float f3 = (\u26032 * 30.0f + \u26033 * 59.0f + \u26034 * 11.0f) / 100.0f;
            \u2603 = (\u26032 * 30.0f + \u26033 * 70.0f) / 100.0f;
            \u2603 = (\u26032 * 30.0f + \u26034 * 70.0f) / 100.0f;
            \u26032 = f3;
            \u26033 = \u2603;
            \u26034 = \u2603;
        }
        GlStateManager.func_179124_c(\u26032, \u26033, \u26034);
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder \u26035 = tessellator.func_178180_c();
        GlStateManager.func_179132_a(false);
        GlStateManager.func_179127_m();
        GlStateManager.func_179124_c(\u26032, \u26033, \u26034);
        if (this.field_175005_X) {
            this.field_175012_t.func_177359_a();
            GlStateManager.func_187410_q(32884);
            GlStateManager.func_187420_d(3, 5126, 12, 0);
            this.field_175012_t.func_177358_a(7);
            this.field_175012_t.func_177361_b();
            GlStateManager.func_187429_p(32884);
        } else {
            GlStateManager.func_179148_o(this.field_72771_w);
        }
        GlStateManager.func_179106_n();
        GlStateManager.func_179118_c();
        GlStateManager.func_179147_l();
        GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.func_74518_a();
        float[] \u26036 = this.field_72769_h.field_73011_w.func_76560_a(this.field_72769_h.func_72826_c(f2), f2);
        if (\u26036 != null) {
            GlStateManager.func_179090_x();
            GlStateManager.func_179103_j(7425);
            GlStateManager.func_179094_E();
            GlStateManager.func_179114_b(90.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.func_179114_b(MathHelper.func_76126_a(this.field_72769_h.func_72929_e(f2)) < 0.0f ? 180.0f : 0.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.func_179114_b(90.0f, 0.0f, 0.0f, 1.0f);
            float f4 = \u26036[0];
            \u2603 = \u26036[1];
            \u2603 = \u26036[2];
            if (n != 2) {
                \u2603 = (f4 * 30.0f + \u2603 * 59.0f + \u2603 * 11.0f) / 100.0f;
                \u2603 = (f4 * 30.0f + \u2603 * 70.0f) / 100.0f;
                f5 = (f4 * 30.0f + \u2603 * 70.0f) / 100.0f;
                f4 = \u2603;
                \u2603 = \u2603;
                \u2603 = f5;
            }
            \u26035.func_181668_a(6, DefaultVertexFormats.field_181706_f);
            \u26035.func_181662_b(0.0, 100.0, 0.0).func_181666_a(f4, \u2603, \u2603, \u26036[3]).func_181675_d();
            \u26037 = 16;
            for (\u26039 = 0; \u26039 <= 16; ++\u26039) {
                float f5 = (float)\u26039 * ((float)Math.PI * 2) / 16.0f;
                \u2603 = MathHelper.func_76126_a(f5);
                \u2603 = MathHelper.func_76134_b(f5);
                \u26035.func_181662_b(\u2603 * 120.0f, \u2603 * 120.0f, -\u2603 * 40.0f * \u26036[3]).func_181666_a(\u26036[0], \u26036[1], \u26036[2], 0.0f).func_181675_d();
            }
            tessellator.func_78381_a();
            GlStateManager.func_179121_F();
            GlStateManager.func_179103_j(7424);
        }
        GlStateManager.func_179098_w();
        GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.func_179094_E();
        f4 = 1.0f - this.field_72769_h.func_72867_j(f2);
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, f4);
        GlStateManager.func_179114_b(-90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.func_179114_b(this.field_72769_h.func_72826_c(f2) * 360.0f, 1.0f, 0.0f, 0.0f);
        \u2603 = 30.0f;
        this.field_72770_i.func_110577_a(field_110928_i);
        \u26035.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        \u26035.func_181662_b(-\u2603, 100.0, -\u2603).func_187315_a(0.0, 0.0).func_181675_d();
        \u26035.func_181662_b(\u2603, 100.0, -\u2603).func_187315_a(1.0, 0.0).func_181675_d();
        \u26035.func_181662_b(\u2603, 100.0, \u2603).func_187315_a(1.0, 1.0).func_181675_d();
        \u26035.func_181662_b(-\u2603, 100.0, \u2603).func_187315_a(0.0, 1.0).func_181675_d();
        tessellator.func_78381_a();
        \u2603 = 20.0f;
        this.field_72770_i.func_110577_a(field_110927_h);
        int \u26038 = this.field_72769_h.func_72853_d();
        \u26037 = \u26038 % 4;
        \u26039 = \u26038 / 4 % 2;
        f5 = (float)(\u26037 + 0) / 4.0f;
        \u2603 = (float)(\u26039 + 0) / 2.0f;
        \u2603 = (float)(\u26037 + 1) / 4.0f;
        \u2603 = (float)(\u26039 + 1) / 2.0f;
        \u26035.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        \u26035.func_181662_b(-\u2603, -100.0, \u2603).func_187315_a(\u2603, \u2603).func_181675_d();
        \u26035.func_181662_b(\u2603, -100.0, \u2603).func_187315_a(f5, \u2603).func_181675_d();
        \u26035.func_181662_b(\u2603, -100.0, -\u2603).func_187315_a(f5, \u2603).func_181675_d();
        \u26035.func_181662_b(-\u2603, -100.0, -\u2603).func_187315_a(\u2603, \u2603).func_181675_d();
        tessellator.func_78381_a();
        GlStateManager.func_179090_x();
        \u2603 = this.field_72769_h.func_72880_h(f2) * f4;
        if (\u2603 > 0.0f) {
            GlStateManager.func_179131_c(\u2603, \u2603, \u2603, \u2603);
            if (this.field_175005_X) {
                this.field_175013_s.func_177359_a();
                GlStateManager.func_187410_q(32884);
                GlStateManager.func_187420_d(3, 5126, 12, 0);
                this.field_175013_s.func_177358_a(7);
                this.field_175013_s.func_177361_b();
                GlStateManager.func_187429_p(32884);
            } else {
                GlStateManager.func_179148_o(this.field_72772_v);
            }
        }
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.func_179084_k();
        GlStateManager.func_179141_d();
        GlStateManager.func_179127_m();
        GlStateManager.func_179121_F();
        GlStateManager.func_179090_x();
        GlStateManager.func_179124_c(0.0f, 0.0f, 0.0f);
        double \u260310 = this.field_72777_q.field_71439_g.func_174824_e((float)f2).field_72448_b - this.field_72769_h.func_72919_O();
        if (\u260310 < 0.0) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(0.0f, 12.0f, 0.0f);
            if (this.field_175005_X) {
                this.field_175011_u.func_177359_a();
                GlStateManager.func_187410_q(32884);
                GlStateManager.func_187420_d(3, 5126, 12, 0);
                this.field_175011_u.func_177358_a(7);
                this.field_175011_u.func_177361_b();
                GlStateManager.func_187429_p(32884);
            } else {
                GlStateManager.func_179148_o(this.field_72781_x);
            }
            GlStateManager.func_179121_F();
            \u2603 = 1.0f;
            \u2603 = -((float)(\u260310 + 65.0));
            \u2603 = -1.0f;
            f5 = \u2603;
            \u26035.func_181668_a(7, DefaultVertexFormats.field_181706_f);
            \u26035.func_181662_b(-1.0, f5, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(1.0, f5, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(-1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(-1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(1.0, f5, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(-1.0, f5, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(1.0, f5, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(1.0, f5, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(-1.0, f5, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(-1.0, f5, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(-1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(-1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(-1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(-1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            \u26035.func_181662_b(1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            tessellator.func_78381_a();
        }
        if (this.field_72769_h.field_73011_w.func_76561_g()) {
            GlStateManager.func_179124_c(\u26032 * 0.2f + 0.04f, \u26033 * 0.2f + 0.04f, \u26034 * 0.6f + 0.1f);
        } else {
            GlStateManager.func_179124_c(\u26032, \u26033, \u26034);
        }
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b(0.0f, -((float)(\u260310 - 16.0)), 0.0f);
        GlStateManager.func_179148_o(this.field_72781_x);
        GlStateManager.func_179121_F();
        GlStateManager.func_179098_w();
        GlStateManager.func_179132_a(true);
    }

    public void func_180447_b(float f, int n, double d, double d2, double d3) {
        float f2;
        if (!this.field_72777_q.field_71441_e.field_73011_w.func_76569_d()) {
            return;
        }
        if (this.field_72777_q.field_71474_y.func_181147_e() == 2) {
            this.func_180445_c(f, n, d, d2, d3);
            return;
        }
        GlStateManager.func_179129_p();
        int n2 = 32;
        \u2603 = 8;
        Tessellator \u26032 = Tessellator.func_178181_a();
        BufferBuilder \u26033 = \u26032.func_178180_c();
        this.field_72770_i.func_110577_a(field_110925_j);
        GlStateManager.func_179147_l();
        GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Vec3d \u26034 = this.field_72769_h.func_72824_f(f);
        float \u26035 = (float)\u26034.field_72450_a;
        float \u26036 = (float)\u26034.field_72448_b;
        float \u26037 = (float)\u26034.field_72449_c;
        if (n != 2) {
            f2 = (\u26035 * 30.0f + \u26036 * 59.0f + \u26037 * 11.0f) / 100.0f;
            \u2603 = (\u26035 * 30.0f + \u26036 * 70.0f) / 100.0f;
            \u2603 = (\u26035 * 30.0f + \u26037 * 70.0f) / 100.0f;
            \u26035 = f2;
            \u26036 = \u2603;
            \u26037 = \u2603;
        }
        f2 = 4.8828125E-4f;
        double \u26038 = (float)this.field_72773_u + f;
        double \u26039 = d + \u26038 * (double)0.03f;
        double \u260310 = d3;
        int \u260311 = MathHelper.func_76128_c(\u26039 / 2048.0);
        int \u260312 = MathHelper.func_76128_c(\u260310 / 2048.0);
        \u2603 = this.field_72769_h.field_73011_w.func_76571_f() - (float)d2 + 0.33f;
        \u2603 = (float)((\u26039 -= (double)(\u260311 * 2048)) * 4.8828125E-4);
        \u2603 = (float)((\u260310 -= (double)(\u260312 * 2048)) * 4.8828125E-4);
        \u26033.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        for (int i = -256; i < 256; i += 32) {
            for (\u2603 = -256; \u2603 < 256; \u2603 += 32) {
                \u26033.func_181662_b(i + 0, \u2603, \u2603 + 32).func_187315_a((float)(i + 0) * 4.8828125E-4f + \u2603, (float)(\u2603 + 32) * 4.8828125E-4f + \u2603).func_181666_a(\u26035, \u26036, \u26037, 0.8f).func_181675_d();
                \u26033.func_181662_b(i + 32, \u2603, \u2603 + 32).func_187315_a((float)(i + 32) * 4.8828125E-4f + \u2603, (float)(\u2603 + 32) * 4.8828125E-4f + \u2603).func_181666_a(\u26035, \u26036, \u26037, 0.8f).func_181675_d();
                \u26033.func_181662_b(i + 32, \u2603, \u2603 + 0).func_187315_a((float)(i + 32) * 4.8828125E-4f + \u2603, (float)(\u2603 + 0) * 4.8828125E-4f + \u2603).func_181666_a(\u26035, \u26036, \u26037, 0.8f).func_181675_d();
                \u26033.func_181662_b(i + 0, \u2603, \u2603 + 0).func_187315_a((float)(i + 0) * 4.8828125E-4f + \u2603, (float)(\u2603 + 0) * 4.8828125E-4f + \u2603).func_181666_a(\u26035, \u26036, \u26037, 0.8f).func_181675_d();
            }
        }
        \u26032.func_78381_a();
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.func_179084_k();
        GlStateManager.func_179089_o();
    }

    public boolean func_72721_a(double d, double d2, double d3, float f) {
        return false;
    }

    private void func_180445_c(float f, int n, double d, double d2, double d3) {
        float f2;
        GlStateManager.func_179129_p();
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder \u26032 = tessellator.func_178180_c();
        float \u26033 = 12.0f;
        float \u26034 = 4.0f;
        double \u26035 = (float)this.field_72773_u + f;
        double \u26036 = (d + \u26035 * (double)0.03f) / 12.0;
        double \u26037 = d3 / 12.0 + (double)0.33f;
        float \u26038 = this.field_72769_h.field_73011_w.func_76571_f() - (float)d2 + 0.33f;
        int \u26039 = MathHelper.func_76128_c(\u26036 / 2048.0);
        int \u260310 = MathHelper.func_76128_c(\u26037 / 2048.0);
        \u26036 -= (double)(\u26039 * 2048);
        \u26037 -= (double)(\u260310 * 2048);
        this.field_72770_i.func_110577_a(field_110925_j);
        GlStateManager.func_179147_l();
        GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Vec3d \u260311 = this.field_72769_h.func_72824_f(f);
        float \u260312 = (float)\u260311.field_72450_a;
        float \u260313 = (float)\u260311.field_72448_b;
        float \u260314 = (float)\u260311.field_72449_c;
        if (n != 2) {
            f2 = (\u260312 * 30.0f + \u260313 * 59.0f + \u260314 * 11.0f) / 100.0f;
            \u2603 = (\u260312 * 30.0f + \u260313 * 70.0f) / 100.0f;
            \u2603 = (\u260312 * 30.0f + \u260314 * 70.0f) / 100.0f;
            \u260312 = f2;
            \u260313 = \u2603;
            \u260314 = \u2603;
        }
        f2 = \u260312 * 0.9f;
        \u2603 = \u260313 * 0.9f;
        \u2603 = \u260314 * 0.9f;
        \u2603 = \u260312 * 0.7f;
        \u2603 = \u260313 * 0.7f;
        \u2603 = \u260314 * 0.7f;
        \u2603 = \u260312 * 0.8f;
        \u2603 = \u260313 * 0.8f;
        \u2603 = \u260314 * 0.8f;
        \u2603 = 0.00390625f;
        \u2603 = (float)MathHelper.func_76128_c(\u26036) * 0.00390625f;
        \u2603 = (float)MathHelper.func_76128_c(\u26037) * 0.00390625f;
        \u2603 = (float)(\u26036 - (double)MathHelper.func_76128_c(\u26036));
        \u2603 = (float)(\u26037 - (double)MathHelper.func_76128_c(\u26037));
        int \u260315 = 8;
        int \u260316 = 4;
        \u2603 = 9.765625E-4f;
        GlStateManager.func_179152_a(12.0f, 1.0f, 12.0f);
        for (int i = 0; i < 2; ++i) {
            if (i == 0) {
                GlStateManager.func_179135_a(false, false, false, false);
            } else {
                switch (n) {
                    case 0: {
                        GlStateManager.func_179135_a(false, true, true, true);
                        break;
                    }
                    case 1: {
                        GlStateManager.func_179135_a(true, false, false, true);
                        break;
                    }
                    case 2: {
                        GlStateManager.func_179135_a(true, true, true, true);
                    }
                }
            }
            for (n2 = -3; n2 <= 4; ++n2) {
                for (\u2603 = -3; \u2603 <= 4; ++\u2603) {
                    int n2;
                    \u26032.func_181668_a(7, DefaultVertexFormats.field_181712_l);
                    float f3 = n2 * 8;
                    \u2603 = \u2603 * 8;
                    \u2603 = f3 - \u2603;
                    \u2603 = \u2603 - \u2603;
                    if (\u26038 > -5.0f) {
                        \u26032.func_181662_b(\u2603 + 0.0f, \u26038 + 0.0f, \u2603 + 8.0f).func_187315_a((f3 + 0.0f) * 0.00390625f + \u2603, (\u2603 + 8.0f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, -1.0f, 0.0f).func_181675_d();
                        \u26032.func_181662_b(\u2603 + 8.0f, \u26038 + 0.0f, \u2603 + 8.0f).func_187315_a((f3 + 8.0f) * 0.00390625f + \u2603, (\u2603 + 8.0f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, -1.0f, 0.0f).func_181675_d();
                        \u26032.func_181662_b(\u2603 + 8.0f, \u26038 + 0.0f, \u2603 + 0.0f).func_187315_a((f3 + 8.0f) * 0.00390625f + \u2603, (\u2603 + 0.0f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, -1.0f, 0.0f).func_181675_d();
                        \u26032.func_181662_b(\u2603 + 0.0f, \u26038 + 0.0f, \u2603 + 0.0f).func_187315_a((f3 + 0.0f) * 0.00390625f + \u2603, (\u2603 + 0.0f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, -1.0f, 0.0f).func_181675_d();
                    }
                    if (\u26038 <= 5.0f) {
                        \u26032.func_181662_b(\u2603 + 0.0f, \u26038 + 4.0f - 9.765625E-4f, \u2603 + 8.0f).func_187315_a((f3 + 0.0f) * 0.00390625f + \u2603, (\u2603 + 8.0f) * 0.00390625f + \u2603).func_181666_a(\u260312, \u260313, \u260314, 0.8f).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
                        \u26032.func_181662_b(\u2603 + 8.0f, \u26038 + 4.0f - 9.765625E-4f, \u2603 + 8.0f).func_187315_a((f3 + 8.0f) * 0.00390625f + \u2603, (\u2603 + 8.0f) * 0.00390625f + \u2603).func_181666_a(\u260312, \u260313, \u260314, 0.8f).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
                        \u26032.func_181662_b(\u2603 + 8.0f, \u26038 + 4.0f - 9.765625E-4f, \u2603 + 0.0f).func_187315_a((f3 + 8.0f) * 0.00390625f + \u2603, (\u2603 + 0.0f) * 0.00390625f + \u2603).func_181666_a(\u260312, \u260313, \u260314, 0.8f).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
                        \u26032.func_181662_b(\u2603 + 0.0f, \u26038 + 4.0f - 9.765625E-4f, \u2603 + 0.0f).func_187315_a((f3 + 0.0f) * 0.00390625f + \u2603, (\u2603 + 0.0f) * 0.00390625f + \u2603).func_181666_a(\u260312, \u260313, \u260314, 0.8f).func_181663_c(0.0f, 1.0f, 0.0f).func_181675_d();
                    }
                    if (n2 > -1) {
                        for (int j = 0; j < 8; ++j) {
                            \u26032.func_181662_b(\u2603 + (float)j + 0.0f, \u26038 + 0.0f, \u2603 + 8.0f).func_187315_a((f3 + (float)j + 0.5f) * 0.00390625f + \u2603, (\u2603 + 8.0f) * 0.00390625f + \u2603).func_181666_a(f2, \u2603, \u2603, 0.8f).func_181663_c(-1.0f, 0.0f, 0.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + (float)j + 0.0f, \u26038 + 4.0f, \u2603 + 8.0f).func_187315_a((f3 + (float)j + 0.5f) * 0.00390625f + \u2603, (\u2603 + 8.0f) * 0.00390625f + \u2603).func_181666_a(f2, \u2603, \u2603, 0.8f).func_181663_c(-1.0f, 0.0f, 0.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + (float)j + 0.0f, \u26038 + 4.0f, \u2603 + 0.0f).func_187315_a((f3 + (float)j + 0.5f) * 0.00390625f + \u2603, (\u2603 + 0.0f) * 0.00390625f + \u2603).func_181666_a(f2, \u2603, \u2603, 0.8f).func_181663_c(-1.0f, 0.0f, 0.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + (float)j + 0.0f, \u26038 + 0.0f, \u2603 + 0.0f).func_187315_a((f3 + (float)j + 0.5f) * 0.00390625f + \u2603, (\u2603 + 0.0f) * 0.00390625f + \u2603).func_181666_a(f2, \u2603, \u2603, 0.8f).func_181663_c(-1.0f, 0.0f, 0.0f).func_181675_d();
                        }
                    }
                    if (n2 <= 1) {
                        for (j = 0; j < 8; ++j) {
                            \u26032.func_181662_b(\u2603 + (float)j + 1.0f - 9.765625E-4f, \u26038 + 0.0f, \u2603 + 8.0f).func_187315_a((f3 + (float)j + 0.5f) * 0.00390625f + \u2603, (\u2603 + 8.0f) * 0.00390625f + \u2603).func_181666_a(f2, \u2603, \u2603, 0.8f).func_181663_c(1.0f, 0.0f, 0.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + (float)j + 1.0f - 9.765625E-4f, \u26038 + 4.0f, \u2603 + 8.0f).func_187315_a((f3 + (float)j + 0.5f) * 0.00390625f + \u2603, (\u2603 + 8.0f) * 0.00390625f + \u2603).func_181666_a(f2, \u2603, \u2603, 0.8f).func_181663_c(1.0f, 0.0f, 0.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + (float)j + 1.0f - 9.765625E-4f, \u26038 + 4.0f, \u2603 + 0.0f).func_187315_a((f3 + (float)j + 0.5f) * 0.00390625f + \u2603, (\u2603 + 0.0f) * 0.00390625f + \u2603).func_181666_a(f2, \u2603, \u2603, 0.8f).func_181663_c(1.0f, 0.0f, 0.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + (float)j + 1.0f - 9.765625E-4f, \u26038 + 0.0f, \u2603 + 0.0f).func_187315_a((f3 + (float)j + 0.5f) * 0.00390625f + \u2603, (\u2603 + 0.0f) * 0.00390625f + \u2603).func_181666_a(f2, \u2603, \u2603, 0.8f).func_181663_c(1.0f, 0.0f, 0.0f).func_181675_d();
                        }
                    }
                    if (\u2603 > -1) {
                        for (j = 0; j < 8; ++j) {
                            \u26032.func_181662_b(\u2603 + 0.0f, \u26038 + 4.0f, \u2603 + (float)j + 0.0f).func_187315_a((f3 + 0.0f) * 0.00390625f + \u2603, (\u2603 + (float)j + 0.5f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, 0.0f, -1.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + 8.0f, \u26038 + 4.0f, \u2603 + (float)j + 0.0f).func_187315_a((f3 + 8.0f) * 0.00390625f + \u2603, (\u2603 + (float)j + 0.5f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, 0.0f, -1.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + 8.0f, \u26038 + 0.0f, \u2603 + (float)j + 0.0f).func_187315_a((f3 + 8.0f) * 0.00390625f + \u2603, (\u2603 + (float)j + 0.5f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, 0.0f, -1.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + 0.0f, \u26038 + 0.0f, \u2603 + (float)j + 0.0f).func_187315_a((f3 + 0.0f) * 0.00390625f + \u2603, (\u2603 + (float)j + 0.5f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, 0.0f, -1.0f).func_181675_d();
                        }
                    }
                    if (\u2603 <= 1) {
                        for (j = 0; j < 8; ++j) {
                            \u26032.func_181662_b(\u2603 + 0.0f, \u26038 + 4.0f, \u2603 + (float)j + 1.0f - 9.765625E-4f).func_187315_a((f3 + 0.0f) * 0.00390625f + \u2603, (\u2603 + (float)j + 0.5f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, 0.0f, 1.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + 8.0f, \u26038 + 4.0f, \u2603 + (float)j + 1.0f - 9.765625E-4f).func_187315_a((f3 + 8.0f) * 0.00390625f + \u2603, (\u2603 + (float)j + 0.5f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, 0.0f, 1.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + 8.0f, \u26038 + 0.0f, \u2603 + (float)j + 1.0f - 9.765625E-4f).func_187315_a((f3 + 8.0f) * 0.00390625f + \u2603, (\u2603 + (float)j + 0.5f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, 0.0f, 1.0f).func_181675_d();
                            \u26032.func_181662_b(\u2603 + 0.0f, \u26038 + 0.0f, \u2603 + (float)j + 1.0f - 9.765625E-4f).func_187315_a((f3 + 0.0f) * 0.00390625f + \u2603, (\u2603 + (float)j + 0.5f) * 0.00390625f + \u2603).func_181666_a(\u2603, \u2603, \u2603, 0.8f).func_181663_c(0.0f, 0.0f, 1.0f).func_181675_d();
                        }
                    }
                    tessellator.func_78381_a();
                }
            }
        }
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.func_179084_k();
        GlStateManager.func_179089_o();
    }

    public void func_174967_a(long l) {
        this.field_147595_R |= this.field_174995_M.func_178516_a(l);
        if (!this.field_175009_l.isEmpty()) {
            Iterator<RenderChunk> iterator = this.field_175009_l.iterator();
            while (iterator.hasNext() && (\u2603 = (\u2603 = iterator.next()).func_188281_o() ? this.field_174995_M.func_178505_b(\u2603) : this.field_174995_M.func_178507_a(\u2603))) {
                \u2603.func_188282_m();
                iterator.remove();
                long l2 = l - System.nanoTime();
                if (l2 >= 0L) continue;
                break;
            }
        }
    }

    public void func_180449_a(Entity entity, float f) {
        float \u260320;
        double d;
        double \u260319;
        float f2;
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder \u26032 = tessellator.func_178180_c();
        WorldBorder \u26033 = this.field_72769_h.func_175723_af();
        double \u26034 = this.field_72777_q.field_71474_y.field_151451_c * 16;
        if (entity.field_70165_t < \u26033.func_177728_d() - \u26034 && entity.field_70165_t > \u26033.func_177726_b() + \u26034 && entity.field_70161_v < \u26033.func_177733_e() - \u26034 && entity.field_70161_v > \u26033.func_177736_c() + \u26034) {
            return;
        }
        double \u26035 = 1.0 - \u26033.func_177745_a(entity) / \u26034;
        \u26035 = Math.pow(\u26035, 4.0);
        double \u26036 = entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double)f;
        double \u26037 = entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double)f;
        double \u26038 = entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double)f;
        GlStateManager.func_179147_l();
        GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.field_72770_i.func_110577_a(field_175006_g);
        GlStateManager.func_179132_a(false);
        GlStateManager.func_179094_E();
        int \u26039 = \u26033.func_177734_a().func_177766_a();
        float \u260310 = (float)(\u26039 >> 16 & 0xFF) / 255.0f;
        float \u260311 = (float)(\u26039 >> 8 & 0xFF) / 255.0f;
        float \u260312 = (float)(\u26039 & 0xFF) / 255.0f;
        GlStateManager.func_179131_c(\u260310, \u260311, \u260312, (float)\u26035);
        GlStateManager.func_179136_a(-3.0f, -3.0f);
        GlStateManager.func_179088_q();
        GlStateManager.func_179092_a(516, 0.1f);
        GlStateManager.func_179141_d();
        GlStateManager.func_179129_p();
        float \u260313 = (float)(Minecraft.func_71386_F() % 3000L) / 3000.0f;
        float \u260314 = 0.0f;
        float \u260315 = 0.0f;
        float \u260316 = 128.0f;
        \u26032.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        \u26032.func_178969_c(-\u26036, -\u26037, -\u26038);
        double \u260317 = Math.max((double)MathHelper.func_76128_c(\u26038 - \u26034), \u26033.func_177736_c());
        double \u260318 = Math.min((double)MathHelper.func_76143_f(\u26038 + \u26034), \u26033.func_177733_e());
        if (\u26036 > \u26033.func_177728_d() - \u26034) {
            f2 = 0.0f;
            \u260319 = \u260317;
            while (\u260319 < \u260318) {
                d = Math.min(1.0, \u260318 - \u260319);
                \u260320 = (float)d * 0.5f;
                \u26032.func_181662_b(\u26033.func_177728_d(), 256.0, \u260319).func_187315_a(\u260313 + f2, \u260313 + 0.0f).func_181675_d();
                \u26032.func_181662_b(\u26033.func_177728_d(), 256.0, \u260319 + d).func_187315_a(\u260313 + \u260320 + f2, \u260313 + 0.0f).func_181675_d();
                \u26032.func_181662_b(\u26033.func_177728_d(), 0.0, \u260319 + d).func_187315_a(\u260313 + \u260320 + f2, \u260313 + 128.0f).func_181675_d();
                \u26032.func_181662_b(\u26033.func_177728_d(), 0.0, \u260319).func_187315_a(\u260313 + f2, \u260313 + 128.0f).func_181675_d();
                \u260319 += 1.0;
                f2 += 0.5f;
            }
        }
        if (\u26036 < \u26033.func_177726_b() + \u26034) {
            f2 = 0.0f;
            \u260319 = \u260317;
            while (\u260319 < \u260318) {
                d = Math.min(1.0, \u260318 - \u260319);
                \u260320 = (float)d * 0.5f;
                \u26032.func_181662_b(\u26033.func_177726_b(), 256.0, \u260319).func_187315_a(\u260313 + f2, \u260313 + 0.0f).func_181675_d();
                \u26032.func_181662_b(\u26033.func_177726_b(), 256.0, \u260319 + d).func_187315_a(\u260313 + \u260320 + f2, \u260313 + 0.0f).func_181675_d();
                \u26032.func_181662_b(\u26033.func_177726_b(), 0.0, \u260319 + d).func_187315_a(\u260313 + \u260320 + f2, \u260313 + 128.0f).func_181675_d();
                \u26032.func_181662_b(\u26033.func_177726_b(), 0.0, \u260319).func_187315_a(\u260313 + f2, \u260313 + 128.0f).func_181675_d();
                \u260319 += 1.0;
                f2 += 0.5f;
            }
        }
        \u260317 = Math.max((double)MathHelper.func_76128_c(\u26036 - \u26034), \u26033.func_177726_b());
        \u260318 = Math.min((double)MathHelper.func_76143_f(\u26036 + \u26034), \u26033.func_177728_d());
        if (\u26038 > \u26033.func_177733_e() - \u26034) {
            f2 = 0.0f;
            \u260319 = \u260317;
            while (\u260319 < \u260318) {
                d = Math.min(1.0, \u260318 - \u260319);
                \u260320 = (float)d * 0.5f;
                \u26032.func_181662_b(\u260319, 256.0, \u26033.func_177733_e()).func_187315_a(\u260313 + f2, \u260313 + 0.0f).func_181675_d();
                \u26032.func_181662_b(\u260319 + d, 256.0, \u26033.func_177733_e()).func_187315_a(\u260313 + \u260320 + f2, \u260313 + 0.0f).func_181675_d();
                \u26032.func_181662_b(\u260319 + d, 0.0, \u26033.func_177733_e()).func_187315_a(\u260313 + \u260320 + f2, \u260313 + 128.0f).func_181675_d();
                \u26032.func_181662_b(\u260319, 0.0, \u26033.func_177733_e()).func_187315_a(\u260313 + f2, \u260313 + 128.0f).func_181675_d();
                \u260319 += 1.0;
                f2 += 0.5f;
            }
        }
        if (\u26038 < \u26033.func_177736_c() + \u26034) {
            f2 = 0.0f;
            \u260319 = \u260317;
            while (\u260319 < \u260318) {
                d = Math.min(1.0, \u260318 - \u260319);
                \u260320 = (float)d * 0.5f;
                \u26032.func_181662_b(\u260319, 256.0, \u26033.func_177736_c()).func_187315_a(\u260313 + f2, \u260313 + 0.0f).func_181675_d();
                \u26032.func_181662_b(\u260319 + d, 256.0, \u26033.func_177736_c()).func_187315_a(\u260313 + \u260320 + f2, \u260313 + 0.0f).func_181675_d();
                \u26032.func_181662_b(\u260319 + d, 0.0, \u26033.func_177736_c()).func_187315_a(\u260313 + \u260320 + f2, \u260313 + 128.0f).func_181675_d();
                \u26032.func_181662_b(\u260319, 0.0, \u26033.func_177736_c()).func_187315_a(\u260313 + f2, \u260313 + 128.0f).func_181675_d();
                \u260319 += 1.0;
                f2 += 0.5f;
            }
        }
        tessellator.func_78381_a();
        \u26032.func_178969_c(0.0, 0.0, 0.0);
        GlStateManager.func_179089_o();
        GlStateManager.func_179118_c();
        GlStateManager.func_179136_a(0.0f, 0.0f);
        GlStateManager.func_179113_r();
        GlStateManager.func_179141_d();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
        GlStateManager.func_179132_a(true);
    }

    private void func_180443_s() {
        GlStateManager.func_187428_a(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.func_179147_l();
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 0.5f);
        GlStateManager.func_179136_a(-3.0f, -3.0f);
        GlStateManager.func_179088_q();
        GlStateManager.func_179092_a(516, 0.1f);
        GlStateManager.func_179141_d();
        GlStateManager.func_179094_E();
    }

    private void func_174969_t() {
        GlStateManager.func_179118_c();
        GlStateManager.func_179136_a(0.0f, 0.0f);
        GlStateManager.func_179113_r();
        GlStateManager.func_179141_d();
        GlStateManager.func_179132_a(true);
        GlStateManager.func_179121_F();
    }

    public void func_174981_a(Tessellator tessellator, BufferBuilder bufferBuilder, Entity entity, float f) {
        double d = entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double)f;
        \u2603 = entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double)f;
        \u2603 = entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double)f;
        if (!this.field_72738_E.isEmpty()) {
            this.field_72770_i.func_110577_a(TextureMap.field_110575_b);
            this.func_180443_s();
            bufferBuilder.func_181668_a(7, DefaultVertexFormats.field_176600_a);
            bufferBuilder.func_178969_c(-d, -\u2603, -\u2603);
            bufferBuilder.func_78914_f();
            Iterator<DestroyBlockProgress> iterator = this.field_72738_E.values().iterator();
            while (iterator.hasNext()) {
                DestroyBlockProgress destroyBlockProgress = iterator.next();
                BlockPos \u26032 = destroyBlockProgress.func_180246_b();
                double \u26033 = (double)\u26032.func_177958_n() - d;
                double \u26034 = (double)\u26032.func_177956_o() - \u2603;
                double \u26035 = (double)\u26032.func_177952_p() - \u2603;
                Block \u26036 = this.field_72769_h.func_180495_p(\u26032).func_177230_c();
                if (\u26036 instanceof BlockChest || \u26036 instanceof BlockEnderChest || \u26036 instanceof BlockSign || \u26036 instanceof BlockSkull) continue;
                if (\u26033 * \u26033 + \u26034 * \u26034 + \u26035 * \u26035 > 1024.0) {
                    iterator.remove();
                    continue;
                }
                IBlockState \u26037 = this.field_72769_h.func_180495_p(\u26032);
                if (\u26037.func_185904_a() == Material.field_151579_a) continue;
                int \u26038 = destroyBlockProgress.func_73106_e();
                TextureAtlasSprite \u26039 = this.field_94141_F[\u26038];
                BlockRendererDispatcher \u260310 = this.field_72777_q.func_175602_ab();
                \u260310.func_175020_a(\u26037, \u26032, \u26039, this.field_72769_h);
            }
            tessellator.func_78381_a();
            bufferBuilder.func_178969_c(0.0, 0.0, 0.0);
            this.func_174969_t();
        }
    }

    public void func_72731_b(EntityPlayer entityPlayer, RayTraceResult rayTraceResult, int n, float f) {
        if (n == 0 && rayTraceResult.field_72313_a == RayTraceResult.Type.BLOCK) {
            GlStateManager.func_179147_l();
            GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.func_187441_d(2.0f);
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a(false);
            BlockPos blockPos = rayTraceResult.func_178782_a();
            IBlockState \u26032 = this.field_72769_h.func_180495_p(blockPos);
            if (\u26032.func_185904_a() != Material.field_151579_a && this.field_72769_h.func_175723_af().func_177746_a(blockPos)) {
                double d = entityPlayer.field_70142_S + (entityPlayer.field_70165_t - entityPlayer.field_70142_S) * (double)f;
                \u2603 = entityPlayer.field_70137_T + (entityPlayer.field_70163_u - entityPlayer.field_70137_T) * (double)f;
                \u2603 = entityPlayer.field_70136_U + (entityPlayer.field_70161_v - entityPlayer.field_70136_U) * (double)f;
                RenderGlobal.func_189697_a(\u26032.func_185918_c(this.field_72769_h, blockPos).func_186662_g(0.002f).func_72317_d(-d, -\u2603, -\u2603), 0.0f, 0.0f, 0.0f, 0.4f);
            }
            GlStateManager.func_179132_a(true);
            GlStateManager.func_179098_w();
            GlStateManager.func_179084_k();
        }
    }

    public static void func_189697_a(AxisAlignedBB axisAlignedBB, float f, float f2, float f3, float f4) {
        RenderGlobal.func_189694_a(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, f, f2, f3, f4);
    }

    public static void func_189694_a(double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder \u26032 = tessellator.func_178180_c();
        \u26032.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        RenderGlobal.func_189698_a(\u26032, d, d2, d3, d4, d5, d6, f, f2, f3, f4);
        tessellator.func_78381_a();
    }

    public static void func_189698_a(BufferBuilder bufferBuilder, double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4) {
        bufferBuilder.func_181662_b(d, d2, d3).func_181666_a(f, f2, f3, 0.0f).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d6).func_181666_a(f, f2, f3, 0.0f).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d6).func_181666_a(f, f2, f3, 0.0f).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d3).func_181666_a(f, f2, f3, 0.0f).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d3).func_181666_a(f, f2, f3, 0.0f).func_181675_d();
    }

    public static void func_189696_b(AxisAlignedBB axisAlignedBB, float f, float f2, float f3, float f4) {
        RenderGlobal.func_189695_b(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, f, f2, f3, f4);
    }

    public static void func_189695_b(double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder \u26032 = tessellator.func_178180_c();
        \u26032.func_181668_a(5, DefaultVertexFormats.field_181706_f);
        RenderGlobal.func_189693_b(\u26032, d, d2, d3, d4, d5, d6, f, f2, f3, f4);
        tessellator.func_78381_a();
    }

    public static void func_189693_b(BufferBuilder bufferBuilder, double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4) {
        bufferBuilder.func_181662_b(d, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d2, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d, d5, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d3).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
        bufferBuilder.func_181662_b(d4, d5, d6).func_181666_a(f, f2, f3, f4).func_181675_d();
    }

    private void func_184385_a(int n, int n2, int n3, int n4, int n5, int n6, boolean bl) {
        this.field_175008_n.func_187474_a(n, n2, n3, n4, n5, n6, bl);
    }

    @Override
    public void func_184376_a(World world, BlockPos blockPos, IBlockState iBlockState, IBlockState iBlockState2, int n) {
        \u2603 = blockPos.func_177958_n();
        \u2603 = blockPos.func_177956_o();
        \u2603 = blockPos.func_177952_p();
        this.func_184385_a(\u2603 - 1, \u2603 - 1, \u2603 - 1, \u2603 + 1, \u2603 + 1, \u2603 + 1, (n & 8) != 0);
    }

    @Override
    public void func_174959_b(BlockPos blockPos) {
        this.field_184387_ae.add(blockPos.func_185334_h());
    }

    @Override
    public void func_147585_a(int n, int n2, int n3, int n4, int n5, int n6) {
        this.func_184385_a(n - 1, n2 - 1, n3 - 1, n4 + 1, n5 + 1, n6 + 1, false);
    }

    @Override
    public void func_184377_a(@Nullable SoundEvent soundEvent, BlockPos blockPos) {
        ISound \u26032 = this.field_147593_P.get(blockPos);
        if (\u26032 != null) {
            this.field_72777_q.func_147118_V().func_147683_b(\u26032);
            this.field_147593_P.remove(blockPos);
        }
        if (soundEvent != null) {
            ItemRecord itemRecord = ItemRecord.func_185074_a(soundEvent);
            if (itemRecord != null) {
                this.field_72777_q.field_71456_v.func_73833_a(itemRecord.func_150927_i());
            }
            \u26032 = PositionedSoundRecord.func_184372_a(soundEvent, blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p());
            this.field_147593_P.put(blockPos, \u26032);
            this.field_72777_q.func_147118_V().func_147682_a(\u26032);
        }
        this.func_193054_a(this.field_72769_h, blockPos, soundEvent != null);
    }

    private void func_193054_a(World world, BlockPos blockPos, boolean bl) {
        List<EntityLivingBase> list = world.func_72872_a(EntityLivingBase.class, new AxisAlignedBB(blockPos).func_186662_g(3.0));
        for (EntityLivingBase entityLivingBase : list) {
            entityLivingBase.func_191987_a(blockPos, bl);
        }
    }

    @Override
    public void func_184375_a(@Nullable EntityPlayer entityPlayer, SoundEvent soundEvent, SoundCategory soundCategory, double d, double d2, double d3, float f, float f2) {
    }

    @Override
    public void func_180442_a(int n, boolean bl, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
        this.func_190570_a(n, bl, false, d, d2, d3, d4, d5, d6, nArray);
    }

    @Override
    public void func_190570_a(int n, boolean bl, boolean bl2, final double d, final double d2, final double d3, double d4, double d5, double d6, int ... nArray) {
        try {
            this.func_190571_b(n, bl, bl2, d, d2, d3, d4, d5, d6, nArray);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.func_85055_a(throwable, "Exception while adding particle");
            CrashReportCategory \u26032 = crashReport.func_85058_a("Particle being added");
            \u26032.func_71507_a("ID", n);
            if (nArray != null) {
                \u26032.func_71507_a("Parameters", nArray);
            }
            \u26032.func_189529_a("Position", new ICrashReportDetail<String>(){

                @Override
                public String call() throws Exception {
                    return CrashReportCategory.func_85074_a(d, d2, d3);
                }

                @Override
                public /* synthetic */ Object call() throws Exception {
                    return this.call();
                }
            });
            throw new ReportedException(crashReport);
        }
    }

    private void func_174972_a(EnumParticleTypes enumParticleTypes, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
        this.func_180442_a(enumParticleTypes.func_179348_c(), enumParticleTypes.func_179344_e(), d, d2, d3, d4, d5, d6, nArray);
    }

    @Nullable
    private Particle func_174974_b(int n, boolean bl, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
        return this.func_190571_b(n, bl, false, d, d2, d3, d4, d5, d6, nArray);
    }

    @Nullable
    private Particle func_190571_b(int n, boolean bl, boolean bl2, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
        Entity entity = this.field_72777_q.func_175606_aa();
        if (this.field_72777_q == null || entity == null || this.field_72777_q.field_71452_i == null) {
            return null;
        }
        int \u26032 = this.func_190572_a(bl2);
        double \u26033 = entity.field_70165_t - d;
        double \u26034 = entity.field_70163_u - d2;
        double \u26035 = entity.field_70161_v - d3;
        if (bl) {
            return this.field_72777_q.field_71452_i.func_178927_a(n, d, d2, d3, d4, d5, d6, nArray);
        }
        if (\u26033 * \u26033 + \u26034 * \u26034 + \u26035 * \u26035 > 1024.0) {
            return null;
        }
        if (\u26032 > 1) {
            return null;
        }
        return this.field_72777_q.field_71452_i.func_178927_a(n, d, d2, d3, d4, d5, d6, nArray);
    }

    private int func_190572_a(boolean bl) {
        int n = this.field_72777_q.field_71474_y.field_74362_aa;
        if (bl && n == 2 && this.field_72769_h.field_73012_v.nextInt(10) == 0) {
            n = 1;
        }
        if (n == 1 && this.field_72769_h.field_73012_v.nextInt(3) == 0) {
            n = 2;
        }
        return n;
    }

    @Override
    public void func_72703_a(Entity entity) {
    }

    @Override
    public void func_72709_b(Entity entity) {
    }

    public void func_72728_f() {
    }

    @Override
    public void func_180440_a(int n, BlockPos blockPos, int n2) {
        switch (n) {
            case 1023: 
            case 1028: 
            case 1038: {
                Entity entity = this.field_72777_q.func_175606_aa();
                if (entity == null) break;
                double \u26032 = (double)blockPos.func_177958_n() - entity.field_70165_t;
                double \u26033 = (double)blockPos.func_177956_o() - entity.field_70163_u;
                double \u26034 = (double)blockPos.func_177952_p() - entity.field_70161_v;
                double \u26035 = Math.sqrt(\u26032 * \u26032 + \u26033 * \u26033 + \u26034 * \u26034);
                double \u26036 = entity.field_70165_t;
                double \u26037 = entity.field_70163_u;
                double \u26038 = entity.field_70161_v;
                if (\u26035 > 0.0) {
                    \u26036 += \u26032 / \u26035 * 2.0;
                    \u26037 += \u26033 / \u26035 * 2.0;
                    \u26038 += \u26034 / \u26035 * 2.0;
                }
                if (n == 1023) {
                    this.field_72769_h.func_184134_a(\u26036, \u26037, \u26038, SoundEvents.field_187855_gD, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (n == 1038) {
                    this.field_72769_h.func_184134_a(\u26036, \u26037, \u26038, SoundEvents.field_193782_bq, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.field_72769_h.func_184134_a(\u26036, \u26037, \u26038, SoundEvents.field_187522_aL, SoundCategory.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    @Override
    public void func_180439_a(EntityPlayer entityPlayer, int n, BlockPos blockPos2, int n22) {
        Random random = this.field_72769_h.field_73012_v;
        switch (n) {
            case 1035: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187621_J, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187542_ac, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187540_ab, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.field_72777_q.func_147118_V().func_147682_a(PositionedSoundRecord.func_184371_a(SoundEvents.field_187812_eh, random.nextFloat() * 0.4f + 0.8f));
                break;
            }
            case 1001: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187576_at, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187574_as, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1003: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187528_aR, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187634_bp, SoundCategory.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187578_au, SoundCategory.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 2000: {
                int n3 = n22 % 3 - 1;
                \u2603 = n22 / 3 % 3 - 1;
                double \u26032 = (double)blockPos2.func_177958_n() + (double)n3 * 0.6 + 0.5;
                double \u26033 = (double)blockPos2.func_177956_o() + 0.5;
                double \u26034 = (double)blockPos2.func_177952_p() + (double)\u2603 * 0.6 + 0.5;
                for (\u2603 = 0; \u2603 < 10; ++\u2603) {
                    double d = random.nextDouble() * 0.2 + 0.01;
                    \u2603 = \u26032 + (double)n3 * 0.01 + (random.nextDouble() - 0.5) * (double)\u2603 * 0.5;
                    \u2603 = \u26033 + (random.nextDouble() - 0.5) * 0.5;
                    \u2603 = \u26034 + (double)\u2603 * 0.01 + (random.nextDouble() - 0.5) * (double)n3 * 0.5;
                    \u2603 = (double)n3 * d + random.nextGaussian() * 0.01;
                    \u2603 = -0.03 + random.nextGaussian() * 0.01;
                    \u2603 = (double)\u2603 * d + random.nextGaussian() * 0.01;
                    this.func_174972_a(EnumParticleTypes.SMOKE_NORMAL, \u2603, \u2603, \u2603, \u2603, \u2603, \u2603, new int[0]);
                }
                break;
            }
            case 2003: {
                BlockPos blockPos2;
                double \u26035 = (double)blockPos2.func_177958_n() + 0.5;
                double \u26036 = blockPos2.func_177956_o();
                double \u26037 = (double)blockPos2.func_177952_p() + 0.5;
                for (int i = 0; i < 8; ++i) {
                    this.func_174972_a(EnumParticleTypes.ITEM_CRACK, \u26035, \u26036, \u26037, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15, Item.func_150891_b(Items.field_151061_bv));
                }
                for (double d = 0.0; d < Math.PI * 2; d += 0.15707963267948966) {
                    this.func_174972_a(EnumParticleTypes.PORTAL, \u26035 + Math.cos(d) * 5.0, \u26036 - 0.4, \u26037 + Math.sin(d) * 5.0, Math.cos(d) * -5.0, 0.0, Math.sin(d) * -5.0, new int[0]);
                    this.func_174972_a(EnumParticleTypes.PORTAL, \u26035 + Math.cos(d) * 5.0, \u26036 - 0.4, \u26037 + Math.sin(d) * 5.0, Math.cos(d) * -7.0, 0.0, Math.sin(d) * -7.0, new int[0]);
                }
                break;
            }
            case 2002: 
            case 2007: {
                int n22;
                BlockPos blockPos2;
                double \u26038 = blockPos2.func_177958_n();
                double \u26039 = blockPos2.func_177956_o();
                double \u260310 = blockPos2.func_177952_p();
                for (int i = 0; i < 8; ++i) {
                    this.func_174972_a(EnumParticleTypes.ITEM_CRACK, \u26038, \u26039, \u260310, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15, Item.func_150891_b(Items.field_185155_bH));
                }
                float \u260311 = (float)(n22 >> 16 & 0xFF) / 255.0f;
                float \u260312 = (float)(n22 >> 8 & 0xFF) / 255.0f;
                float \u260313 = (float)(n22 >> 0 & 0xFF) / 255.0f;
                EnumParticleTypes \u260314 = n == 2007 ? EnumParticleTypes.SPELL_INSTANT : EnumParticleTypes.SPELL;
                for (\u2603 = 0; \u2603 < 100; ++\u2603) {
                    double d = random.nextDouble() * 4.0;
                    \u2603 = random.nextDouble() * Math.PI * 2.0;
                    \u2603 = Math.cos(\u2603) * d;
                    \u2603 = 0.01 + random.nextDouble() * 0.5;
                    \u2603 = Math.sin(\u2603) * d;
                    Particle \u260315 = this.func_174974_b(\u260314.func_179348_c(), \u260314.func_179344_e(), \u26038 + \u2603 * 0.1, \u26039 + 0.3, \u260310 + \u2603 * 0.1, \u2603, \u2603, \u2603, new int[0]);
                    if (\u260315 == null) continue;
                    float \u260316 = 0.75f + random.nextFloat() * 0.25f;
                    \u260315.func_70538_b(\u260311 * \u260316, \u260312 * \u260316, \u260313 * \u260316);
                    \u260315.func_70543_e((float)d);
                }
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187825_fO, SoundCategory.NEUTRAL, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                BlockPos blockPos2;
                Block block = Block.func_149729_e(n22 & 0xFFF);
                if (block.func_176223_P().func_185904_a() != Material.field_151579_a) {
                    SoundType soundType = block.func_185467_w();
                    this.field_72769_h.func_184156_a(blockPos2, soundType.func_185845_c(), SoundCategory.BLOCKS, (soundType.func_185843_a() + 1.0f) / 2.0f, soundType.func_185847_b() * 0.8f, false);
                }
                this.field_72777_q.field_71452_i.func_180533_a(blockPos2, block.func_176203_a(n22 >> 12 & 0xFF));
                break;
            }
            case 2004: {
                for (int i = 0; i < 20; ++i) {
                    double d = (double)blockPos2.func_177958_n() + 0.5 + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5) * 2.0;
                    \u2603 = (double)blockPos2.func_177956_o() + 0.5 + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5) * 2.0;
                    \u2603 = (double)blockPos2.func_177952_p() + 0.5 + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5) * 2.0;
                    this.field_72769_h.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, d, \u2603, \u2603, 0.0, 0.0, 0.0, new int[0]);
                    this.field_72769_h.func_175688_a(EnumParticleTypes.FLAME, d, \u2603, \u2603, 0.0, 0.0, 0.0, new int[0]);
                }
                break;
            }
            case 2005: {
                BlockPos blockPos2;
                ItemDye.func_180617_a(this.field_72769_h, blockPos2, n22);
                break;
            }
            case 2006: {
                for (int i = 0; i < 200; ++i) {
                    float f = random.nextFloat() * 4.0f;
                    \u2603 = random.nextFloat() * ((float)Math.PI * 2);
                    double \u260317 = MathHelper.func_76134_b(\u2603) * f;
                    double \u260318 = 0.01 + random.nextDouble() * 0.5;
                    double \u260319 = MathHelper.func_76126_a(\u2603) * f;
                    Particle \u260320 = this.func_174974_b(EnumParticleTypes.DRAGON_BREATH.func_179348_c(), false, (double)blockPos2.func_177958_n() + \u260317 * 0.1, (double)blockPos2.func_177956_o() + 0.3, (double)blockPos2.func_177952_p() + \u260319 * 0.1, \u260317, \u260318, \u260319, new int[0]);
                    if (\u260320 == null) continue;
                    \u260320.func_70543_e(f);
                }
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187523_aM, SoundCategory.HOSTILE, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1012: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187873_gM, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1036: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187614_cJ, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1013: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187877_gO, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1014: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187610_bh, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1011: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187608_cH, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1006: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187875_gN, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1007: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187879_gP, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1037: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187617_cK, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1008: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187613_bi, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1005: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187611_cI, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1009: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187646_bt, SoundCategory.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                break;
            }
            case 1029: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187680_c, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187698_i, SoundCategory.BLOCKS, 1.0f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187689_f, SoundCategory.BLOCKS, 0.3f, this.field_72769_h.field_73012_v.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                if (Item.func_150899_d(n22) instanceof ItemRecord) {
                    this.field_72769_h.func_184149_a(blockPos2, ((ItemRecord)Item.func_150899_d(n22)).func_185075_h());
                    break;
                }
                this.field_72769_h.func_184149_a(blockPos2, null);
                break;
            }
            case 1015: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187559_bL, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187527_aQ, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187557_bK, SoundCategory.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187927_ha, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187926_gz, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187929_hc, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187928_hb, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187606_E, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187853_gC, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187945_hs, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187941_ho, SoundCategory.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187744_z, SoundCategory.NEUTRAL, 0.05f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 3000: {
                this.field_72769_h.func_175682_a(EnumParticleTypes.EXPLOSION_HUGE, true, (double)blockPos2.func_177958_n() + 0.5, (double)blockPos2.func_177956_o() + 0.5, (double)blockPos2.func_177952_p() + 0.5, 0.0, 0.0, 0.0, new int[0]);
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187598_bd, SoundCategory.BLOCKS, 10.0f, (1.0f + (this.field_72769_h.field_73012_v.nextFloat() - this.field_72769_h.field_73012_v.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.field_72769_h.func_184156_a(blockPos2, SoundEvents.field_187525_aO, SoundCategory.HOSTILE, 64.0f, 0.8f + this.field_72769_h.field_73012_v.nextFloat() * 0.3f, false);
            }
        }
    }

    @Override
    public void func_180441_b(int n, BlockPos blockPos, int n2) {
        if (n2 < 0 || n2 >= 10) {
            this.field_72738_E.remove(n);
        } else {
            DestroyBlockProgress destroyBlockProgress = this.field_72738_E.get(n);
            if (destroyBlockProgress == null || destroyBlockProgress.func_180246_b().func_177958_n() != blockPos.func_177958_n() || destroyBlockProgress.func_180246_b().func_177956_o() != blockPos.func_177956_o() || destroyBlockProgress.func_180246_b().func_177952_p() != blockPos.func_177952_p()) {
                destroyBlockProgress = new DestroyBlockProgress(n, blockPos);
                this.field_72738_E.put(n, destroyBlockProgress);
            }
            destroyBlockProgress.func_73107_a(n2);
            destroyBlockProgress.func_82744_b(this.field_72773_u);
        }
    }

    public boolean func_184384_n() {
        return this.field_175009_l.isEmpty() && this.field_174995_M.func_188247_f();
    }

    public void func_174979_m() {
        this.field_147595_R = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void func_181023_a(Collection<TileEntity> collection, Collection<TileEntity> collection2) {
        Set<TileEntity> set = this.field_181024_n;
        synchronized (set) {
            this.field_181024_n.removeAll(collection);
            this.field_181024_n.addAll(collection2);
        }
    }

    class ContainerLocalRenderInformation {
        final RenderChunk field_178036_a;
        final EnumFacing field_178034_b;
        byte field_178035_c;
        final int field_178032_d;

        private ContainerLocalRenderInformation(RenderChunk renderChunk, @Nullable EnumFacing enumFacing, int n) {
            this.field_178036_a = renderChunk;
            this.field_178034_b = enumFacing;
            this.field_178032_d = n;
        }

        public void func_189561_a(byte by, EnumFacing enumFacing) {
            this.field_178035_c = (byte)(this.field_178035_c | (by | 1 << enumFacing.ordinal()));
        }

        public boolean func_189560_a(EnumFacing enumFacing) {
            return (this.field_178035_c & 1 << enumFacing.ordinal()) > 0;
        }
    }
}

