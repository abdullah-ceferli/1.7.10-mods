/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.SRPMain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBeholderPearl
extends Item {
    private static final ResourceLocation ID_SIM = new ResourceLocation("srparasites", "sim_enderman");
    private static final ResourceLocation ID_SIM_HEAD = new ResourceLocation("srparasites", "sim_endermanhead");
    private static final ResourceLocation ID_FERAL = new ResourceLocation("srparasites", "fer_enderman");
    private static final ResourceLocation ID_ASSIMARA = new ResourceLocation("srparasites", "mar_enderman");
    private static final ResourceLocation KEY_PEARL_STATE = new ResourceLocation("srparasites", "pearl_state");
    private static final double SCAN_RADIUS = 100.0;
    private static final int UPDATE_INTERVAL = 6;
    private static final Map<Integer, Cache> CACHE = new HashMap<Integer, Cache>();

    public ItemBeholderPearl() {
        RegistryNames.set(this, "srparasites", "pearl");
        this.setUnlocalizedName("srparasites.pearl");
        this.setMaxStackSize(64);
        this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
        this.addPropertyOverride(KEY_PEARL_STATE, (stack, worldIn, entityIn) -> {
            float out;
            boolean holding;
            if (worldIn == null || entityIn == null) {
                return 0.0f;
            }
            boolean bl = holding = !entityIn.getHeldItemMainhand().isEmpty() && entityIn.getHeldItemMainhand().getItem() == this || !entityIn.getHeldItemOffhand().isEmpty() && entityIn.getHeldItemOffhand().getItem() == this;
            if (!holding) {
                CACHE.remove(entityIn.getEntityId());
                return 0.0f;
            }
            long now = worldIn.getTotalWorldTime();
            Cache c = CACHE.get(entityIn.getEntityId());
            if (c == null) {
                c = new Cache();
                CACHE.put(entityIn.getEntityId(), c);
            }
            c.lastAccessTick = now;
            if (now < c.nextScanTick) {
                return c.lastVal;
            }
            c.nextScanTick = now + 6L;
            if ((now & 0xFFL) == 0L) {
                long cutoff = now - 600L;
                CACHE.entrySet().removeIf(e -> ((Cache)e.getValue()).lastAccessTick < cutoff);
            }
            double R = 100.0;
            AxisAlignedBB box = Boxes.create(entityIn.posX - 100.0, entityIn.posY - 100.0, entityIn.posZ - 100.0, entityIn.posX + 100.0, entityIn.posY + 100.0, entityIn.posZ + 100.0);
            List matches = EntityQueries.entities(worldIn, Entity.class, box, e -> {
                if (e == null || e.isDead || e == entityIn) {
                    return false;
                }
                ResourceLocation id = EntityList.getKey((Entity)e);
                return id != null && (id.equals((Object)ID_ASSIMARA) || id.equals((Object)ID_FERAL) || id.equals((Object)ID_SIM) || id.equals((Object)ID_SIM_HEAD));
            });
            int tier = 0;
            double nearestSq = Double.POSITIVE_INFINITY;
            for (Entity e2 : matches) {
                int candidate;
                ResourceLocation id = EntityList.getKey((Entity)e2);
                if (id == null) continue;
                int n = id.equals((Object)ID_ASSIMARA) ? 3 : (id.equals((Object)ID_FERAL) ? 2 : (candidate = id.equals((Object)ID_SIM) || id.equals((Object)ID_SIM_HEAD) ? 1 : 0));
                if (candidate == 0) continue;
                double d2 = e2.getDistanceSqToEntity((Entity)entityIn);
                if (candidate <= tier && (candidate != tier || !(d2 < nearestSq))) continue;
                tier = candidate;
                nearestSq = d2;
                if (tier != 3) continue;
                break;
            }
            if (tier == 0) {
                out = 0.0f;
            } else {
                double dist = Math.sqrt(nearestSq);
                float proximity = (float)Math.max(0.0, Math.min(1.0, (100.0 - dist) / 100.0));
                long t = now + (long)entityIn.ticksExisted;
                float freq = 1.5f + 1.0f * (float)tier + 4.0f * proximity;
                float omega = (float)((double)freq * 2.0 * Math.PI / 20.0);
                float osc = (float)Math.sin((float)t * omega);
                float amp = 0.12f + 0.15f * (float)tier + 0.25f * proximity;
                float base = tier;
                float val = base + 0.1f + amp * osc;
                out = val = Math.max(base + 0.05f, Math.min(base + 0.45f, val));
            }
            c.lastVal = out;
            return out;
        });
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(EnumChatFormatting.GRAY + I18n.format((String)"tooltip.srparasites.pearl.desc", (Object[])new Object[0]));
        tooltip.add("");
        tooltip.add(EnumChatFormatting.LIGHT_PURPLE + I18n.format((String)"tooltip.srparasites.pearl.assimilated", (Object[])new Object[0]));
        tooltip.add(EnumChatFormatting.RED + I18n.format((String)"tooltip.srparasites.pearl.feral", (Object[])new Object[0]));
        tooltip.add(EnumChatFormatting.BLUE + I18n.format((String)"tooltip.srparasites.pearl.assimara", (Object[])new Object[0]));
    }

    private static final class Cache {
        long nextScanTick;
        long lastAccessTick;
        float lastVal;

        private Cache() {
        }
    }
}

