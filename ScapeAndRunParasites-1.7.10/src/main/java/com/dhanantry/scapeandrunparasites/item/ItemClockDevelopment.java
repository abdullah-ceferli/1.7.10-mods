/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.item.ItemBase;
import com.dhanantry.scapeandrunparasites.network.SRPPacketClock;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import com.dhanantry.scapeandrunparasites.backport.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemClockDevelopment
extends ItemBase {
    public static int level = 0;

    public ItemClockDevelopment(String name, int maxStack, byte v) {
        super(name, maxStack, v);
        com.dhanantry.scapeandrunparasites.backport.item.ItemProperties.add(this, new ResourceLocation("level"), new IItemPropertyGetter(){

            @SideOnly(value=Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (entityIn == null) {
                    return 0.0f;
                }
                ItemStack main = entityIn.getHeldItemMainhand();
                ItemStack off = entityIn.getHeldItemOffhand();
                if (main == stack || off == stack || ItemStack.areItemStacksEqual((ItemStack)main, (ItemStack)stack) || ItemStack.areItemStacksEqual((ItemStack)off, (ItemStack)stack)) {
                    return level;
                }
                return 0.0f;
            }
        });
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        SRPSaveData data;
        if (!worldIn.isRemote && entityIn.ticksExisted % 20 == 0 && (data = SRPSaveData.get(worldIn, -11)) != null) {
            SRPMain.network.sendToAll((IMessage)new SRPPacketClock(data.getCooldown(worldIn, worldIn.provider.dimensionId), data.getEvolutionPhase(worldIn.provider.dimensionId), data.getDeveLevel(), 2));
        }
    }
}

