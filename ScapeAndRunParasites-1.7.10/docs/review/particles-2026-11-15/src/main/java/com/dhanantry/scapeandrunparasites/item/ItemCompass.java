/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItemFrame
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.network.SRPPacketCompass;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemCompass
extends Item {
    protected byte type;

    public ItemCompass(String n, int t) {
        RegistryNames.set(this, n);
        this.setUnlocalizedName("srparasites." + n);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.type = (byte)t;
        this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter(){
            @SideOnly(value=Side.CLIENT)
            double rotation;
            @SideOnly(value=Side.CLIENT)
            double rota;
            @SideOnly(value=Side.CLIENT)
            long lastUpdateTick;

            @SideOnly(value=Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                double d0;
                EntityLivingBase entity;
                if (entityIn == null && !stack.isOnItemFrame()) {
                    return 0.0f;
                }
                boolean flag = entityIn != null;
                Object object = entity = flag ? entityIn : stack.getItemFrame();
                if (worldIn == null) {
                    worldIn = entity.worldObj;
                }
                if (worldIn.provider.isSurfaceWorld()) {
                    double d1 = flag ? (double)entity.rotationYaw : this.getFrameRotation((EntityItemFrame)entity);
                    d1 = MathHelper.positiveModulo((double)(d1 / 360.0), (double)1.0);
                    double d2 = this.getSpawnToAngle(worldIn, (Entity)entity) / (Math.PI * 2);
                    d0 = 0.5 - (d1 - 0.25 - d2);
                } else {
                    d0 = Math.random();
                }
                if (flag) {
                    d0 = this.wobble(worldIn, d0);
                }
                return MathHelper.positiveModulo((float)((float)d0), (float)1.0f);
            }

            @SideOnly(value=Side.CLIENT)
            private double wobble(World worldIn, double ddd) {
                if (worldIn.getTotalWorldTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = worldIn.getTotalWorldTime();
                    double d0 = ddd - this.rotation;
                    d0 = MathHelper.positiveModulo((double)(d0 + 0.5), (double)1.0) - 0.5;
                    this.rota += d0 * 0.1;
                    this.rota *= 0.8;
                    this.rotation = MathHelper.positiveModulo((double)(this.rotation + this.rota), (double)1.0);
                }
                return this.rotation;
            }

            @SideOnly(value=Side.CLIENT)
            private double getFrameRotation(EntityItemFrame en) {
                return MathHelper.wrapDegrees((int)(180 + en.facingDirection.getHorizontalIndex() * 90));
            }

            @SideOnly(value=Side.CLIENT)
            private double getSpawnToAngle(World world, Entity en) {
                if (ItemCompass.this.getOri() == null) {
                    return world.rand.nextDouble();
                }
                return Math.atan2((double)ItemCompass.this.getOri().getZ() - en.posZ, (double)ItemCompass.this.getOri().getX() - en.posX);
            }
        });
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote && entityIn.ticksExisted % 20 == 0) {
            SRPWorldData aaa = SRPWorldData.get(worldIn);
            BlockPos posss = this.getOrigin(entityIn, worldIn);
            if (posss != null) {
                SRPMain.network.sendToAll((IMessage)new SRPPacketCompass(posss.getX(), posss.getY(), posss.getZ(), this.type));
            } else {
                SRPMain.network.sendToAll((IMessage)new SRPPacketCompass(worldIn.rand.nextInt(), worldIn.rand.nextInt(), worldIn.rand.nextInt(), this.type));
            }
        }
    }

    public abstract BlockPos getOrigin(Entity var1, World var2);

    public abstract BlockPos getOri();
}

