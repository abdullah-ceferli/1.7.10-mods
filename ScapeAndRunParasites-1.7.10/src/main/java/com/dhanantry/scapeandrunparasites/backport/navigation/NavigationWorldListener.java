package com.dhanantry.scapeandrunparasites.backport.navigation;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;

/** Connect native world notifications to active ported navigators on the server. */
final class NavigationWorldListener implements IWorldAccess {
    // The world owns its listener. Weak keys AND values avoid retaining unloaded worlds.
    private static final Map<World, WeakReference<NavigationWorldListener>> listeners =
            new WeakHashMap<World, WeakReference<NavigationWorldListener>>();
    private final World world;
    private final Map<EntityLiving, Boolean> entities = new WeakHashMap<EntityLiving, Boolean>();

    private NavigationWorldListener(World world) { this.world = world; }

    static synchronized void attach(World world) {
        if (world.isRemote) return;
        WeakReference<NavigationWorldListener> ref = listeners.get(world);
        if (ref != null && ref.get() != null) return;
        NavigationWorldListener listener = new NavigationWorldListener(world);
        listeners.put(world, new WeakReference<NavigationWorldListener>(listener));
        world.addWorldAccess(listener);
        for (Object entity : world.loadedEntityList) listener.onEntityCreate((Entity) entity);
    }

    @Override public void onEntityCreate(Entity entity) {
        if (entity.worldObj == world && entity instanceof EntityLiving
                && ((EntityLiving) entity).getNavigator() instanceof PathNavigate) {
            entities.put((EntityLiving) entity, Boolean.TRUE);
        }
    }

    @Override public void onEntityDestroy(Entity entity) { entities.remove(entity); }

    private void changed(int x0, int y0, int z0, int x1, int y1, int z1) {
        // Recalculation is deferred to each navigator's tick. A multi-block update
        // cannot trigger a search halfway through its own world mutation.
        for (EntityLiving entity : entities.keySet()) {
            if (entity == null || entity.isDead || entity.worldObj != world) continue;
            net.minecraft.pathfinding.PathNavigate navigator = entity.getNavigator();
            if (navigator instanceof PathNavigate) {
                ((PathNavigate) navigator).onWorldBlockChange(x0, y0, z0, x1, y1, z1);
            }
        }
    }

    @Override public void markBlockForUpdate(int x, int y, int z) { changed(x,y,z,x,y,z); }

    @Override public void markBlockRangeForRenderUpdate(int x0,int y0,int z0,int x1,int y1,int z1) {
        // Native doors also use this notification for a metadata/shape change.
        changed(Math.min(x0,x1),Math.min(y0,y1),Math.min(z0,z1),
                Math.max(x0,x1),Math.max(y0,y1),Math.max(z0,z1));
    }

    // Light-only updates and audiovisual events do not change traversal.
    @Override public void markBlockForRenderUpdate(int x,int y,int z) {}
    @Override public void playSound(String sound,double x,double y,double z,float volume,float pitch) {}
    @Override public void playSoundToNearExcept(EntityPlayer player,String sound,double x,double y,double z,float volume,float pitch) {}
    @Override public void spawnParticle(String particle,double x,double y,double z,double vx,double vy,double vz) {}
    @Override public void playRecord(String record,int x,int y,int z) {}
    @Override public void broadcastSound(int event,int x,int y,int z,int data) {}
    @Override public void playAuxSFX(EntityPlayer player,int event,int x,int y,int z,int data) {}
    @Override public void destroyBlockPartially(int entity,int x,int y,int z,int progress) {}
    @Override public void onStaticEntitiesChanged() {}
}
