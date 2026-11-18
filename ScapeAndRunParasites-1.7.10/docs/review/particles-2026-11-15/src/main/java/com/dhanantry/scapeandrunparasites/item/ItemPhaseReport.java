/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.util.text.translation.I18n
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiPhaseReport;
import com.dhanantry.scapeandrunparasites.item.ItemBase;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPhaseReport
extends ItemBase {
    public static final String NBT_PRINT_DAY = "PrintDay";
    public static final String NBT_PRINT_TIME = "PrintTime";
    public static final String NBT_DIMENSION = "PhaseDimension";
    public static final String NBT_PHASE = "PhaseValue";
    public static final String NBT_TOTAL_POINTS = "PhaseTotalPoints";
    public static final String NBT_POINTS_NEXT = "PhasePointsNext";
    public static final String NBT_PROGRESS = "PhaseProgress";
    public static final String NBT_COOLDOWN = "PhaseCooldown";
    public static final String NBT_CAN_GAIN = "PhaseCanGain";
    public static final String NBT_CAN_LOSS = "PhaseCanLoss";
    public static final String NBT_MOBCAP = "PhaseMobcap";
    public static final String NBT_GENERATION = "PhaseGeneration";
    public static final String NBT_GEN_TICKS = "PhaseGenTicks";
    public static final String NBT_PARASITE_COUNT = "PhaseParasiteCount";
    public static final String NBT_COTH_COUNT = "PhaseCothCount";
    public static final String NBT_TOTAL_MOBS = "PhaseTotalMobs";

    public ItemPhaseReport(String name, int maxStack, byte id) {
        super(name, maxStack, id);
        this.setMaxStackSize(1);
    }

    public static NBTTagCompound getOrCreate(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        return tag;
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
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiPhaseReport(stack));
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (tooltip == null) {
            return;
        }
        tooltip.add(EnumChatFormatting.GRAY + net.minecraft.client.resources.I18n.format((String)"tooltip.srparasites.phase_report", (Object[])new Object[0]));
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(NBT_PRINT_DAY)) {
            int day = tag.getInteger(NBT_PRINT_DAY);
            int time = tag.getInteger(NBT_PRINT_TIME);
            tooltip.add(EnumChatFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format((String)"tooltip.srparasites.printed", (Object[])new Object[]{day, time}));
        }
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocal((String)"item.srparasites.phase_report.name");
    }
}

