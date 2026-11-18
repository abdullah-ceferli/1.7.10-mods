/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Barrier;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBlockDust;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.particle.ParticleBubble;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.client.particle.ParticleCrit;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleDragonBreath;
import net.minecraft.client.particle.ParticleDrip;
import net.minecraft.client.particle.ParticleEmitter;
import net.minecraft.client.particle.ParticleEnchantmentTable;
import net.minecraft.client.particle.ParticleEndRod;
import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.client.particle.ParticleExplosionHuge;
import net.minecraft.client.particle.ParticleExplosionLarge;
import net.minecraft.client.particle.ParticleFallingDust;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleFootStep;
import net.minecraft.client.particle.ParticleHeart;
import net.minecraft.client.particle.ParticleLava;
import net.minecraft.client.particle.ParticleMobAppearance;
import net.minecraft.client.particle.ParticleNote;
import net.minecraft.client.particle.ParticlePortal;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.client.particle.ParticleSmokeLarge;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.particle.ParticleSnowShovel;
import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.client.particle.ParticleSpit;
import net.minecraft.client.particle.ParticleSplash;
import net.minecraft.client.particle.ParticleSuspend;
import net.minecraft.client.particle.ParticleSuspendedTown;
import net.minecraft.client.particle.ParticleSweepAttack;
import net.minecraft.client.particle.ParticleTotem;
import net.minecraft.client.particle.ParticleWaterWake;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleManager {
    private static final ResourceLocation field_110737_b = new ResourceLocation("textures/particle/particles.png");
    protected World field_78878_a;
    private final ArrayDeque<Particle>[][] field_78876_b = new ArrayDeque[4][];
    private final Queue<ParticleEmitter> field_178933_d = Queues.newArrayDeque();
    private final TextureManager field_78877_c;
    private final Random field_78875_d = new Random();
    private final Map<Integer, IParticleFactory> field_178932_g = Maps.newHashMap();
    private final Queue<Particle> field_187241_h = Queues.newArrayDeque();

    public ParticleManager(World world, TextureManager textureManager) {
        this.field_78878_a = world;
        this.field_78877_c = textureManager;
        for (int i = 0; i < 4; ++i) {
            this.field_78876_b[i] = new ArrayDeque[2];
            for (\u2603 = 0; \u2603 < 2; ++\u2603) {
                this.field_78876_b[i][\u2603] = Queues.newArrayDeque();
            }
        }
        this.func_178930_c();
    }

    private void func_178930_c() {
        this.func_178929_a(EnumParticleTypes.EXPLOSION_NORMAL.func_179348_c(), new ParticleExplosion.Factory());
        this.func_178929_a(EnumParticleTypes.SPIT.func_179348_c(), new ParticleSpit.Factory());
        this.func_178929_a(EnumParticleTypes.WATER_BUBBLE.func_179348_c(), new ParticleBubble.Factory());
        this.func_178929_a(EnumParticleTypes.WATER_SPLASH.func_179348_c(), new ParticleSplash.Factory());
        this.func_178929_a(EnumParticleTypes.WATER_WAKE.func_179348_c(), new ParticleWaterWake.Factory());
        this.func_178929_a(EnumParticleTypes.WATER_DROP.func_179348_c(), new ParticleRain.Factory());
        this.func_178929_a(EnumParticleTypes.SUSPENDED.func_179348_c(), new ParticleSuspend.Factory());
        this.func_178929_a(EnumParticleTypes.SUSPENDED_DEPTH.func_179348_c(), new ParticleSuspendedTown.Factory());
        this.func_178929_a(EnumParticleTypes.CRIT.func_179348_c(), new ParticleCrit.Factory());
        this.func_178929_a(EnumParticleTypes.CRIT_MAGIC.func_179348_c(), new ParticleCrit.MagicFactory());
        this.func_178929_a(EnumParticleTypes.SMOKE_NORMAL.func_179348_c(), new ParticleSmokeNormal.Factory());
        this.func_178929_a(EnumParticleTypes.SMOKE_LARGE.func_179348_c(), new ParticleSmokeLarge.Factory());
        this.func_178929_a(EnumParticleTypes.SPELL.func_179348_c(), new ParticleSpell.Factory());
        this.func_178929_a(EnumParticleTypes.SPELL_INSTANT.func_179348_c(), new ParticleSpell.InstantFactory());
        this.func_178929_a(EnumParticleTypes.SPELL_MOB.func_179348_c(), new ParticleSpell.MobFactory());
        this.func_178929_a(EnumParticleTypes.SPELL_MOB_AMBIENT.func_179348_c(), new ParticleSpell.AmbientMobFactory());
        this.func_178929_a(EnumParticleTypes.SPELL_WITCH.func_179348_c(), new ParticleSpell.WitchFactory());
        this.func_178929_a(EnumParticleTypes.DRIP_WATER.func_179348_c(), new ParticleDrip.WaterFactory());
        this.func_178929_a(EnumParticleTypes.DRIP_LAVA.func_179348_c(), new ParticleDrip.LavaFactory());
        this.func_178929_a(EnumParticleTypes.VILLAGER_ANGRY.func_179348_c(), new ParticleHeart.AngryVillagerFactory());
        this.func_178929_a(EnumParticleTypes.VILLAGER_HAPPY.func_179348_c(), new ParticleSuspendedTown.HappyVillagerFactory());
        this.func_178929_a(EnumParticleTypes.TOWN_AURA.func_179348_c(), new ParticleSuspendedTown.Factory());
        this.func_178929_a(EnumParticleTypes.NOTE.func_179348_c(), new ParticleNote.Factory());
        this.func_178929_a(EnumParticleTypes.PORTAL.func_179348_c(), new ParticlePortal.Factory());
        this.func_178929_a(EnumParticleTypes.ENCHANTMENT_TABLE.func_179348_c(), new ParticleEnchantmentTable.EnchantmentTable());
        this.func_178929_a(EnumParticleTypes.FLAME.func_179348_c(), new ParticleFlame.Factory());
        this.func_178929_a(EnumParticleTypes.LAVA.func_179348_c(), new ParticleLava.Factory());
        this.func_178929_a(EnumParticleTypes.FOOTSTEP.func_179348_c(), new ParticleFootStep.Factory());
        this.func_178929_a(EnumParticleTypes.CLOUD.func_179348_c(), new ParticleCloud.Factory());
        this.func_178929_a(EnumParticleTypes.REDSTONE.func_179348_c(), new ParticleRedstone.Factory());
        this.func_178929_a(EnumParticleTypes.FALLING_DUST.func_179348_c(), new ParticleFallingDust.Factory());
        this.func_178929_a(EnumParticleTypes.SNOWBALL.func_179348_c(), new ParticleBreaking.SnowballFactory());
        this.func_178929_a(EnumParticleTypes.SNOW_SHOVEL.func_179348_c(), new ParticleSnowShovel.Factory());
        this.func_178929_a(EnumParticleTypes.SLIME.func_179348_c(), new ParticleBreaking.SlimeFactory());
        this.func_178929_a(EnumParticleTypes.HEART.func_179348_c(), new ParticleHeart.Factory());
        this.func_178929_a(EnumParticleTypes.BARRIER.func_179348_c(), new Barrier.Factory());
        this.func_178929_a(EnumParticleTypes.ITEM_CRACK.func_179348_c(), new ParticleBreaking.Factory());
        this.func_178929_a(EnumParticleTypes.BLOCK_CRACK.func_179348_c(), new ParticleDigging.Factory());
        this.func_178929_a(EnumParticleTypes.BLOCK_DUST.func_179348_c(), new ParticleBlockDust.Factory());
        this.func_178929_a(EnumParticleTypes.EXPLOSION_HUGE.func_179348_c(), new ParticleExplosionHuge.Factory());
        this.func_178929_a(EnumParticleTypes.EXPLOSION_LARGE.func_179348_c(), new ParticleExplosionLarge.Factory());
        this.func_178929_a(EnumParticleTypes.FIREWORKS_SPARK.func_179348_c(), new ParticleFirework.Factory());
        this.func_178929_a(EnumParticleTypes.MOB_APPEARANCE.func_179348_c(), new ParticleMobAppearance.Factory());
        this.func_178929_a(EnumParticleTypes.DRAGON_BREATH.func_179348_c(), new ParticleDragonBreath.Factory());
        this.func_178929_a(EnumParticleTypes.END_ROD.func_179348_c(), new ParticleEndRod.Factory());
        this.func_178929_a(EnumParticleTypes.DAMAGE_INDICATOR.func_179348_c(), new ParticleCrit.DamageIndicatorFactory());
        this.func_178929_a(EnumParticleTypes.SWEEP_ATTACK.func_179348_c(), new ParticleSweepAttack.Factory());
        this.func_178929_a(EnumParticleTypes.TOTEM.func_179348_c(), new ParticleTotem.Factory());
    }

    public void func_178929_a(int n, IParticleFactory iParticleFactory) {
        this.field_178932_g.put(n, iParticleFactory);
    }

    public void func_178926_a(Entity entity, EnumParticleTypes enumParticleTypes) {
        this.field_178933_d.add(new ParticleEmitter(this.field_78878_a, entity, enumParticleTypes));
    }

    public void func_191271_a(Entity entity, EnumParticleTypes enumParticleTypes, int n) {
        this.field_178933_d.add(new ParticleEmitter(this.field_78878_a, entity, enumParticleTypes, n));
    }

    @Nullable
    public Particle func_178927_a(int n, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
        IParticleFactory iParticleFactory = this.field_178932_g.get(n);
        if (iParticleFactory != null && (\u2603 = iParticleFactory.func_178902_a(n, this.field_78878_a, d, d2, d3, d4, d5, d6, nArray)) != null) {
            this.func_78873_a(\u2603);
            return \u2603;
        }
        return null;
    }

    public void func_78873_a(Particle particle) {
        this.field_187241_h.add(particle);
    }

    public void func_78868_a() {
        for (int i = 0; i < 4; ++i) {
            this.func_178922_a(i);
        }
        if (!this.field_178933_d.isEmpty()) {
            ArrayList arrayList = Lists.newArrayList();
            for (ParticleEmitter particleEmitter : this.field_178933_d) {
                particleEmitter.func_189213_a();
                if (particleEmitter.func_187113_k()) continue;
                arrayList.add(particleEmitter);
            }
            this.field_178933_d.removeAll(arrayList);
        }
        if (!this.field_187241_h.isEmpty()) {
            Particle \u26032 = this.field_187241_h.poll();
            while (\u26032 != null) {
                int n = \u26032.func_70537_b();
                int n2 = \u2603 = \u26032.func_187111_c() ? 0 : 1;
                if (this.field_78876_b[n][\u2603].size() >= 16384) {
                    this.field_78876_b[n][\u2603].removeFirst();
                }
                this.field_78876_b[n][\u2603].add(\u26032);
                \u26032 = this.field_187241_h.poll();
            }
        }
    }

    private void func_178922_a(int n) {
        this.field_78878_a.field_72984_F.func_76320_a(String.valueOf(n));
        for (\u2603 = 0; \u2603 < 2; ++\u2603) {
            this.field_78878_a.field_72984_F.func_76320_a(String.valueOf(\u2603));
            this.func_187240_a(this.field_78876_b[n][\u2603]);
            this.field_78878_a.field_72984_F.func_76319_b();
        }
        this.field_78878_a.field_72984_F.func_76319_b();
    }

    private void func_187240_a(Queue<Particle> queue) {
        if (!queue.isEmpty()) {
            Iterator iterator = queue.iterator();
            while (iterator.hasNext()) {
                Particle particle = (Particle)iterator.next();
                this.func_178923_d(particle);
                if (particle.func_187113_k()) continue;
                iterator.remove();
            }
        }
    }

    private void func_178923_d(final Particle particle) {
        try {
            particle.func_189213_a();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.func_85055_a(throwable, "Ticking Particle");
            CrashReportCategory \u26032 = crashReport.func_85058_a("Particle being ticked");
            final int \u26033 = particle.func_70537_b();
            \u26032.func_189529_a("Particle", new ICrashReportDetail<String>(){

                @Override
                public String call() throws Exception {
                    return particle.toString();
                }

                @Override
                public /* synthetic */ Object call() throws Exception {
                    return this.call();
                }
            });
            \u26032.func_189529_a("Particle Type", new ICrashReportDetail<String>(){

                @Override
                public String call() throws Exception {
                    if (\u26033 == 0) {
                        return "MISC_TEXTURE";
                    }
                    if (\u26033 == 1) {
                        return "TERRAIN_TEXTURE";
                    }
                    if (\u26033 == 3) {
                        return "ENTITY_PARTICLE_TEXTURE";
                    }
                    return "Unknown - " + \u26033;
                }

                @Override
                public /* synthetic */ Object call() throws Exception {
                    return this.call();
                }
            });
            throw new ReportedException(crashReport);
        }
    }

    public void func_78874_a(Entity entity, float f) {
        \u2603 = ActiveRenderInfo.func_178808_b();
        \u2603 = ActiveRenderInfo.func_178803_d();
        \u2603 = ActiveRenderInfo.func_178805_e();
        \u2603 = ActiveRenderInfo.func_178807_f();
        \u2603 = ActiveRenderInfo.func_178809_c();
        Particle.field_70556_an = entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double)f;
        Particle.field_70554_ao = entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double)f;
        Particle.field_70555_ap = entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double)f;
        Particle.field_190016_K = entity.func_70676_i(f);
        GlStateManager.func_179147_l();
        GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.func_179092_a(516, 0.003921569f);
        for (int i = 0; i < 3; ++i) {
            for (\u2603 = 0; \u2603 < 2; ++\u2603) {
                if (this.field_78876_b[i][\u2603].isEmpty()) continue;
                switch (\u2603) {
                    case 0: {
                        GlStateManager.func_179132_a(false);
                        break;
                    }
                    case 1: {
                        GlStateManager.func_179132_a(true);
                    }
                }
                switch (i) {
                    default: {
                        this.field_78877_c.func_110577_a(field_110737_b);
                        break;
                    }
                    case 1: {
                        this.field_78877_c.func_110577_a(TextureMap.field_110575_b);
                    }
                }
                GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
                Tessellator tessellator = Tessellator.func_178181_a();
                BufferBuilder \u26032 = tessellator.func_178180_c();
                \u26032.func_181668_a(7, DefaultVertexFormats.field_181704_d);
                for (final Particle particle : this.field_78876_b[i][\u2603]) {
                    try {
                        particle.func_180434_a(\u26032, entity, f, \u2603, \u2603, \u2603, \u2603, \u2603);
                    }
                    catch (Throwable throwable) {
                        CrashReport crashReport = CrashReport.func_85055_a(throwable, "Rendering Particle");
                        CrashReportCategory \u26033 = crashReport.func_85058_a("Particle being rendered");
                        final int \u26034 = i;
                        \u26033.func_189529_a("Particle", new ICrashReportDetail<String>(){

                            @Override
                            public String call() throws Exception {
                                return particle.toString();
                            }

                            @Override
                            public /* synthetic */ Object call() throws Exception {
                                return this.call();
                            }
                        });
                        \u26033.func_189529_a("Particle Type", new ICrashReportDetail<String>(){

                            @Override
                            public String call() throws Exception {
                                if (\u26034 == 0) {
                                    return "MISC_TEXTURE";
                                }
                                if (\u26034 == 1) {
                                    return "TERRAIN_TEXTURE";
                                }
                                if (\u26034 == 3) {
                                    return "ENTITY_PARTICLE_TEXTURE";
                                }
                                return "Unknown - " + \u26034;
                            }

                            @Override
                            public /* synthetic */ Object call() throws Exception {
                                return this.call();
                            }
                        });
                        throw new ReportedException(crashReport);
                    }
                }
                tessellator.func_78381_a();
            }
        }
        GlStateManager.func_179132_a(true);
        GlStateManager.func_179084_k();
        GlStateManager.func_179092_a(516, 0.1f);
    }

    public void func_78872_b(Entity entity, float f) {
        \u2603 = (float)Math.PI / 180;
        \u2603 = MathHelper.func_76134_b(entity.field_70177_z * ((float)Math.PI / 180));
        \u2603 = MathHelper.func_76126_a(entity.field_70177_z * ((float)Math.PI / 180));
        \u2603 = -\u2603 * MathHelper.func_76126_a(entity.field_70125_A * ((float)Math.PI / 180));
        \u2603 = \u2603 * MathHelper.func_76126_a(entity.field_70125_A * ((float)Math.PI / 180));
        \u2603 = MathHelper.func_76134_b(entity.field_70125_A * ((float)Math.PI / 180));
        for (int i = 0; i < 2; ++i) {
            ArrayDeque<Particle> arrayDeque = this.field_78876_b[3][i];
            if (arrayDeque.isEmpty()) continue;
            Tessellator \u26032 = Tessellator.func_178181_a();
            BufferBuilder \u26033 = \u26032.func_178180_c();
            for (Particle particle : arrayDeque) {
                particle.func_180434_a(\u26033, entity, f, \u2603, \u2603, \u2603, \u2603, \u2603);
            }
        }
    }

    public void func_78870_a(@Nullable World world) {
        this.field_78878_a = world;
        for (int i = 0; i < 4; ++i) {
            for (\u2603 = 0; \u2603 < 2; ++\u2603) {
                this.field_78876_b[i][\u2603].clear();
            }
        }
        this.field_178933_d.clear();
    }

    public void func_180533_a(BlockPos blockPos, IBlockState iBlockState) {
        if (iBlockState.func_185904_a() == Material.field_151579_a) {
            return;
        }
        iBlockState = iBlockState.func_185899_b(this.field_78878_a, blockPos);
        int n = 4;
        for (\u2603 = 0; \u2603 < 4; ++\u2603) {
            for (\u2603 = 0; \u2603 < 4; ++\u2603) {
                for (\u2603 = 0; \u2603 < 4; ++\u2603) {
                    double d = ((double)\u2603 + 0.5) / 4.0;
                    \u2603 = ((double)\u2603 + 0.5) / 4.0;
                    \u2603 = ((double)\u2603 + 0.5) / 4.0;
                    this.func_78873_a(new ParticleDigging(this.field_78878_a, (double)blockPos.func_177958_n() + d, (double)blockPos.func_177956_o() + \u2603, (double)blockPos.func_177952_p() + \u2603, d - 0.5, \u2603 - 0.5, \u2603 - 0.5, iBlockState).func_174846_a(blockPos));
                }
            }
        }
    }

    public void func_180532_a(BlockPos blockPos, EnumFacing enumFacing) {
        IBlockState iBlockState = this.field_78878_a.func_180495_p(blockPos);
        if (iBlockState.func_185901_i() == EnumBlockRenderType.INVISIBLE) {
            return;
        }
        int \u26032 = blockPos.func_177958_n();
        int \u26033 = blockPos.func_177956_o();
        int \u26034 = blockPos.func_177952_p();
        float \u26035 = 0.1f;
        AxisAlignedBB \u26036 = iBlockState.func_185900_c(this.field_78878_a, blockPos);
        double \u26037 = (double)\u26032 + this.field_78875_d.nextDouble() * (\u26036.field_72336_d - \u26036.field_72340_a - (double)0.2f) + (double)0.1f + \u26036.field_72340_a;
        double \u26038 = (double)\u26033 + this.field_78875_d.nextDouble() * (\u26036.field_72337_e - \u26036.field_72338_b - (double)0.2f) + (double)0.1f + \u26036.field_72338_b;
        double \u26039 = (double)\u26034 + this.field_78875_d.nextDouble() * (\u26036.field_72334_f - \u26036.field_72339_c - (double)0.2f) + (double)0.1f + \u26036.field_72339_c;
        if (enumFacing == EnumFacing.DOWN) {
            \u26038 = (double)\u26033 + \u26036.field_72338_b - (double)0.1f;
        }
        if (enumFacing == EnumFacing.UP) {
            \u26038 = (double)\u26033 + \u26036.field_72337_e + (double)0.1f;
        }
        if (enumFacing == EnumFacing.NORTH) {
            \u26039 = (double)\u26034 + \u26036.field_72339_c - (double)0.1f;
        }
        if (enumFacing == EnumFacing.SOUTH) {
            \u26039 = (double)\u26034 + \u26036.field_72334_f + (double)0.1f;
        }
        if (enumFacing == EnumFacing.WEST) {
            \u26037 = (double)\u26032 + \u26036.field_72340_a - (double)0.1f;
        }
        if (enumFacing == EnumFacing.EAST) {
            \u26037 = (double)\u26032 + \u26036.field_72336_d + (double)0.1f;
        }
        this.func_78873_a(new ParticleDigging(this.field_78878_a, \u26037, \u26038, \u26039, 0.0, 0.0, 0.0, iBlockState).func_174846_a(blockPos).func_70543_e(0.2f).func_70541_f(0.6f));
    }

    public String func_78869_b() {
        int n = 0;
        for (\u2603 = 0; \u2603 < 4; ++\u2603) {
            for (\u2603 = 0; \u2603 < 2; ++\u2603) {
                n += this.field_78876_b[\u2603][\u2603].size();
            }
        }
        return "" + n;
    }
}

