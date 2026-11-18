package com.dhanantry.scapeandrunparasites.backport.potion;

import java.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/** Per-entity set of effect IDs whose swirl particles are hidden, saved with the entity. */
public final class PotionVisibility implements IExtendedEntityProperties {
    public static final String KEY = "srparasites_potion_visibility";
    /** Server thread only. Entities that currently have a hidden effect and so need their synced colour filtered. */
    private static final Set<EntityLivingBase> ACTIVE = Collections.newSetFromMap(new WeakHashMap<EntityLivingBase, Boolean>());

    private final Set<Integer> hidden = new HashSet<Integer>();
    private EntityLivingBase owner;

    public static PotionVisibility get(EntityLivingBase entity) {
        return (PotionVisibility) entity.getExtendedProperties(KEY);
    }

    /** Called when the engine accepts an application. The latest accepted application decides, as in 1.12 combine. */
    void set(int effectId, boolean hide) {
        if (hide) { hidden.add(effectId); ACTIVE.add(owner); } else { hidden.remove(effectId); }
    }

    @Override public void init(Entity entity, World world) { this.owner = (EntityLivingBase) entity; }

    // Native readFromNBT calls loadNBTData BEFORE ActiveEffects are restored, and saveNBTData BEFORE they are written,
    // so neither callback may look at active effects. Pruning happens later in tick().
    @Override public void saveNBTData(NBTTagCompound compound) {
        int[] ids = new int[hidden.size()]; int i = 0;
        for (Integer id : hidden) ids[i++] = id;
        NBTTagCompound tag = new NBTTagCompound();
        tag.setIntArray("Hidden", ids);
        compound.setTag(KEY, tag);
    }

    @Override public void loadNBTData(NBTTagCompound compound) {
        hidden.clear();
        if (compound.hasKey(KEY, 10)) {
            for (int id : compound.getCompoundTag(KEY).getIntArray("Hidden")) hidden.add(id);
        }
        if (!hidden.isEmpty() && owner != null) ACTIVE.add(owner);
    }

    /** Runs at server WorldTickEvent END, before EntityTracker broadcasts this tick's metadata. */
    static void tick(World world) {
        if (ACTIVE.isEmpty()) return;
        for (EntityLivingBase e : new ArrayList<EntityLivingBase>(ACTIVE)) {
            if (e.worldObj != world) continue;
            PotionVisibility v = get(e);
            if (v == null || e.isDead) { ACTIVE.remove(e); continue; }
            Collection active = e.getActivePotionEffects();
            List<PotionEffect> visible = new ArrayList<PotionEffect>();
            Set<Integer> activeIds = new HashSet<Integer>();
            for (Object o : active) {
                PotionEffect fx = (PotionEffect) o;
                activeIds.add(fx.getPotionID());
                if (!v.hidden.contains(fx.getPotionID())) visible.add(fx);
            }
            v.hidden.retainAll(activeIds); // safe here: ActiveEffects are fully restored by now
            // calcPotionLiquidColor(empty) returns 0x385DC6, so an empty visible list must be written as 0 explicitly.
            int color = visible.isEmpty() ? 0 : PotionHelper.calcPotionLiquidColor(visible);
            e.getDataWatcher().updateObject(7, Integer.valueOf(color)); // watcher 8 (ambient aggregate) stays native
            if (v.hidden.isEmpty()) ACTIVE.remove(e);
        }
    }
}
