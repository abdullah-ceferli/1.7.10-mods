package com.dhanantry.scapeandrunparasites.backport.datasync;
import net.minecraft.entity.Entity;
public final class DataParameter<T> {
    private final int id;private final DataSerializer<T> serializer;private final Class<? extends Entity> declaringClass;
    public DataParameter(int id,DataSerializer<T> serializer){this(id,serializer,null);}
    DataParameter(int id,DataSerializer<T> serializer,Class<? extends Entity> owner){if(id<0||id>254)throw new IllegalArgumentException("Tracked data ID must be 0..254");if(serializer==null)throw new NullPointerException("serializer");this.id=id;this.serializer=serializer;this.declaringClass=owner;}
    public int getId(){return id;}public DataSerializer<T> getSerializer(){return serializer;}Class<? extends Entity> declaringClass(){return declaringClass;}
    @Override public boolean equals(Object value){return this==value||value instanceof DataParameter&&id==((DataParameter<?>)value).id;}
    @Override public int hashCode(){return id;}
}
