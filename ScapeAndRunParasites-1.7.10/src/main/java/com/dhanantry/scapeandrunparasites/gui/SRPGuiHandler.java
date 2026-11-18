/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.IGuiHandler
 */
package com.dhanantry.scapeandrunparasites.gui;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.client.gui.ContainerConsumedWorkbench;
import com.dhanantry.scapeandrunparasites.client.gui.GuiConsumedWorkbench;
import com.dhanantry.scapeandrunparasites.client.gui.GuiInfuserFurnace;
import com.dhanantry.scapeandrunparasites.container.ContainerInfuserFurnace;
import com.dhanantry.scapeandrunparasites.container.ContainerParasiteLoot;
import com.dhanantry.scapeandrunparasites.container.ScannerContainer;
import com.dhanantry.scapeandrunparasites.gui.GuiParasiteLoot;
import com.dhanantry.scapeandrunparasites.gui.ScannerGui;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityInfuserFurnace;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityParasiteLoot;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class SRPGuiHandler
implements IGuiHandler {
    public static final int SCANNER_GUI_ID = 0;
    public static final int PARASITE_LOOT_GUI_ID = 101;
    public static final int CONSUMED_WORKBENCH_GUI_ID = 102;
    public static final int INFUSER_FURNACE_GUI_ID = 7;

    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (id == 102) {
            return new ContainerConsumedWorkbench(player.inventory, world, pos);
        }
        TileEntity te = WorldAccess.getTileEntity(world, pos);
        if (te == null) {
            return null;
        }
        switch (id) {
            case 0: {
                if (te instanceof TileEntityRelayController) {
                    return new ScannerContainer(player.inventory, (TileEntityRelayController)te);
                }
                return null;
            }
            case 101: {
                if (te instanceof TileEntityParasiteLoot) {
                    return new ContainerParasiteLoot(player.inventory, (TileEntityParasiteLoot)te, player);
                }
                return null;
            }
            case 7: {
                if (te instanceof TileEntityInfuserFurnace) {
                    return new ContainerInfuserFurnace(player.inventory, (TileEntityInfuserFurnace)te, player);
                }
                return null;
            }
        }
        return null;
    }

    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (id == 102) {
            return new GuiConsumedWorkbench(player.inventory, world, pos);
        }
        TileEntity te = WorldAccess.getTileEntity(world, pos);
        if (te == null) {
            return null;
        }
        switch (id) {
            case 0: {
                if (te instanceof TileEntityRelayController) {
                    return new ScannerGui(player.inventory, (TileEntityRelayController)te);
                }
                return null;
            }
            case 101: {
                if (te instanceof TileEntityParasiteLoot) {
                    return new GuiParasiteLoot(player.inventory, (TileEntityParasiteLoot)te);
                }
                return null;
            }
            case 7: {
                if (te instanceof TileEntityInfuserFurnace) {
                    return new GuiInfuserFurnace(player.inventory, (TileEntityInfuserFurnace)te);
                }
                return null;
            }
        }
        return null;
    }
}

