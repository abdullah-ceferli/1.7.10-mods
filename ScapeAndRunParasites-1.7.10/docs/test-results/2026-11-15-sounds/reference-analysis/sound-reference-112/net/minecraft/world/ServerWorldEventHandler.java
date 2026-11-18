/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ServerWorldEventHandler
implements IWorldEventListener {
    private final MinecraftServer field_72783_a;
    private final WorldServer field_72782_b;

    public ServerWorldEventHandler(MinecraftServer minecraftServer, WorldServer worldServer) {
        this.field_72783_a = minecraftServer;
        this.field_72782_b = worldServer;
    }

    @Override
    public void func_180442_a(int n, boolean bl, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
    }

    @Override
    public void func_190570_a(int n, boolean bl, boolean bl2, double d, double d2, double d3, double d4, double d5, double d6, int ... nArray) {
    }

    @Override
    public void func_72703_a(Entity entity) {
        this.field_72782_b.func_73039_n().func_72786_a(entity);
        if (entity instanceof EntityPlayerMP) {
            this.field_72782_b.field_73011_w.func_186061_a((EntityPlayerMP)entity);
        }
    }

    @Override
    public void func_72709_b(Entity entity) {
        this.field_72782_b.func_73039_n().func_72790_b(entity);
        this.field_72782_b.func_96441_U().func_181140_a(entity);
        if (entity instanceof EntityPlayerMP) {
            this.field_72782_b.field_73011_w.func_186062_b((EntityPlayerMP)entity);
        }
    }

    @Override
    public void func_184375_a(@Nullable EntityPlayer entityPlayer, SoundEvent soundEvent, SoundCategory soundCategory, double d, double d2, double d3, float f, float f2) {
        this.field_72783_a.func_184103_al().func_148543_a(entityPlayer, d, d2, d3, f > 1.0f ? (double)(16.0f * f) : 16.0, this.field_72782_b.field_73011_w.func_186058_p().func_186068_a(), new SPacketSoundEffect(soundEvent, soundCategory, d, d2, d3, f, f2));
    }

    @Override
    public void func_147585_a(int n, int n2, int n3, int n4, int n5, int n6) {
    }

    @Override
    public void func_184376_a(World world, BlockPos blockPos, IBlockState iBlockState, IBlockState iBlockState2, int n) {
        this.field_72782_b.func_184164_w().func_180244_a(blockPos);
    }

    @Override
    public void func_174959_b(BlockPos blockPos) {
    }

    @Override
    public void func_184377_a(SoundEvent soundEvent, BlockPos blockPos) {
    }

    @Override
    public void func_180439_a(EntityPlayer entityPlayer, int n, BlockPos blockPos, int n2) {
        this.field_72783_a.func_184103_al().func_148543_a(entityPlayer, blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p(), 64.0, this.field_72782_b.field_73011_w.func_186058_p().func_186068_a(), new SPacketEffect(n, blockPos, n2, false));
    }

    @Override
    public void func_180440_a(int n, BlockPos blockPos, int n2) {
        this.field_72783_a.func_184103_al().func_148540_a(new SPacketEffect(n, blockPos, n2, true));
    }

    @Override
    public void func_180441_b(int n, BlockPos blockPos, int n2) {
        for (EntityPlayerMP entityPlayerMP : this.field_72783_a.func_184103_al().func_181057_v()) {
            if (entityPlayerMP == null || entityPlayerMP.field_70170_p != this.field_72782_b || entityPlayerMP.func_145782_y() == n || !((\u2603 = (double)blockPos.func_177958_n() - entityPlayerMP.field_70165_t) * \u2603 + (\u2603 = (double)blockPos.func_177956_o() - entityPlayerMP.field_70163_u) * \u2603 + (\u2603 = (double)blockPos.func_177952_p() - entityPlayerMP.field_70161_v) * \u2603 < 1024.0)) continue;
            entityPlayerMP.field_71135_a.func_147359_a(new SPacketBlockBreakAnim(n, blockPos, n2));
        }
    }
}

