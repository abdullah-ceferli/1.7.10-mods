/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.text.TextFormatting
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
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemClockEvolution
extends ItemBase {
    public static int cooldown = 0;
    public static int phase = 0;

    public ItemClockEvolution(String name, int maxStack, byte v) {
        super(name, maxStack, v);
        this.addPropertyOverride(new ResourceLocation("phase"), new IItemPropertyGetter(){

            @SideOnly(value=Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (entityIn == null) {
                    return 0.0f;
                }
                ItemStack main = entityIn.getHeldItemMainhand();
                ItemStack off = entityIn.getHeldItemOffhand();
                if (main == stack || off == stack || ItemStack.areItemStacksEqual((ItemStack)main, (ItemStack)stack) || ItemStack.areItemStacksEqual((ItemStack)off, (ItemStack)stack)) {
                    return phase;
                }
                return 0.0f;
            }
        });
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        SRPSaveData data;
        if (!worldIn.isRemote && entityIn.ticksExisted % 20 == 0 && (data = SRPSaveData.get(worldIn, -11)) != null) {
            cooldown = data.getCooldown(worldIn, worldIn.provider.dimensionId);
            phase = data.getEvolutionPhase(worldIn.provider.dimensionId);
            SRPMain.network.sendToAll((IMessage)new SRPPacketClock(cooldown, phase, data.getDeveLevel(), 2));
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        SRPSaveData data = SRPSaveData.get(worldIn, -11);
        tooltip.add(EnumChatFormatting.AQUA + I18n.format((String)("-> " + cooldown), (Object[])new Object[0]));
    }
}

