package com.dhanantry.scapeandrunparasites.backport.item;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.item.Item;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;

/** Per-item property callbacks, independent of item inheritance and native model rendering. */
public final class ItemProperties {
    private static final Map<Item,RegistrySimple> properties=new IdentityHashMap<Item,RegistrySimple>();
    private ItemProperties(){}
    public static synchronized void add(Item item,ResourceLocation name,IItemPropertyGetter getter){
        Objects.requireNonNull(item,"item");Objects.requireNonNull(name,"property name");Objects.requireNonNull(getter,"property getter");
        RegistrySimple registry=properties.get(item);
        if(registry==null){registry=new RegistrySimple();properties.put(item,registry);}
        registry.putObject(name,getter);
    }
    public static synchronized IItemPropertyGetter get(Item item,ResourceLocation name){
        RegistrySimple registry=properties.get(Objects.requireNonNull(item,"item"));
        return registry==null?null:(IItemPropertyGetter)registry.getObject(name);
    }
}
