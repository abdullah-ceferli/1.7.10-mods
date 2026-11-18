/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.gui.GuiVectorMapReport;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemVectorMapReport
extends Item {
    public static final String NBT_CX = "CenterX";
    public static final String NBT_CZ = "CenterZ";
    public static final String NBT_VX = "VectorX";
    public static final String NBT_VZ = "VectorZ";
    public static final String NBT_R = "Radius";
    public static final String NBT_DAY = "Day";
    public static final String NBT_INDEX = "Index";
    public static final String NBT_TOTAL = "Total";
    public static final String NBT_PRINT_DAY = "PrintDay";
    public static final String NBT_PRINT_TIME = "PrintTime";

    public ItemVectorMapReport(String name) {
        RegistryNames.set(this, "srparasites", name);
        this.setUnlocalizedName("srparasites." + name);
        this.setMaxStackSize(1);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) {
            this.openGui(stack);
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    @SideOnly(value=Side.CLIENT)
    private void openGui(ItemStack stack) {
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiVectorMapReport(stack));
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) {
            return;
        }
        items.add((Object)new ItemStack((Item)this));
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(EnumChatFormatting.GRAY + I18n.format((String)"tooltip.srparasites.vector_map", (Object[])new Object[0]));
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(NBT_PRINT_DAY)) {
            int day = tag.getInteger(NBT_PRINT_DAY);
            int time = tag.getInteger(NBT_PRINT_TIME);
            tooltip.add(EnumChatFormatting.DARK_GRAY + I18n.format((String)"tooltip.srparasites.printed", (Object[])new Object[]{day, time}));
        }
    }

    public static NBTTagCompound getOrCreate(ItemStack s) {
        NBTTagCompound tag = s.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            s.setTagCompound(tag);
        }
        return tag;
    }
}

