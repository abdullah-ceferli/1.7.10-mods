/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.item.ItemBase;
import com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLure
extends ItemBase {
    int sound = 0;

    public ItemLure(String name, int maxStack, byte version) {
        super(name, maxStack, version);
        this.version = version;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            MinecraftServer ser = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (ser.worldServers.length > 0) {
                SRPSaveData dat = SRPSaveData.get((World)ser.worldServers[0], 89);
                dat.addUpdateNumber(1);
                System.out.println("cccccc  dat ");
            }
            for (int i = 0; i < playerIn.inventory.mainInventory.size(); ++i) {
            }
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.posX + 1.0, playerIn.posY + 1.0, playerIn.posZ + 1.0), 14.0);
            List moblist = EntityQueries.entities(playerIn.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                if (mob != playerIn) continue;
            }
        } else {
            System.out.println("client phase " + SRPEventHandlerBus.clientCurrentEvoPhase + " client scent " + SRPEventHandlerBus.clientScent + " client vector " + SRPEventHandlerBus.clientVector + " music " + SRPEventHandlerBus.musicTimer);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(EnumChatFormatting.AQUA + I18n.format((String)("tootip.srparasites.lurecomp." + this.version), (Object[])new Object[0]));
    }
}

