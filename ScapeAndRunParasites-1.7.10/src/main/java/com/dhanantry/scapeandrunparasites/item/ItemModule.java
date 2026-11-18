/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.NonNullList
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.SRPMain;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemModule
extends Item {
    private final Kind kind;

    public ItemModule(String name, Kind kind) {
        this.kind = kind;
        RegistryNames.set(this, name);
        this.setUnlocalizedName("srparasites." + name);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.setMaxStackSize(1);
    }

    public ItemModule() {
        this.kind = Kind.INBORN;
        this.setMaxStackSize(1);
    }

    public Kind getKind() {
        return this.kind;
    }

    public Kind getKind(ItemStack ignored) {
        return this.kind;
    }

    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add((Object)new ItemStack((Item)this));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (RegistryNames.get(this) == null) {
            return;
        }
        String key = "tooltip." + RegistryNames.get(this).getResourcePath();
        String translated = I18n.format((String)key, (Object[])new Object[0]);
        if (!translated.equals(key) && !translated.isEmpty()) {
            if (translated.contains("\\n")) {
                for (String line : translated.split("\\\\n")) {
                    tooltip.add(line);
                }
            } else {
                tooltip.add(translated);
            }
        }
        tooltip.add(I18n.format((String)"tooltip.module.profile", (Object[])new Object[]{this.kind.name()}));
    }

    public static enum Kind {
        INBORN,
        ASSIMILATED,
        HIJACKED,
        FERAL,
        CRUDE,
        PRIMITIVE,
        ADAPTED,
        NEXUS,
        DETERRENT,
        PURE,
        PREEMINENT,
        ANCIENT,
        DESMOID,
        ESCHAR,
        RESISTANCE,
        IDEAL,
        ORIGIN,
        ASSIMARA,
        DERIVED,
        VECTORS,
        PHASE,
        DISLODGEMENT;

    }
}

