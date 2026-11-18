package com.dhanantry.scapeandrunparasites.backport.state;
import com.dhanantry.scapeandrunparasites.backport.util.IStringSerializable;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import java.util.*;
public class PropertyEnum<T extends Enum<T> & IStringSerializable> extends PropertyHelper<T> {
    private final ImmutableSet<T> allowed;
    private final Map<String,T> byName=new HashMap<String,T>();
    protected PropertyEnum(String name,Class<T> valueClass,Collection<T> values){
        super(name,valueClass);allowed=ImmutableSet.copyOf(values);
        for(T value:values){String key=value.getName();if(byName.containsKey(key))throw new IllegalArgumentException("Duplicate serialized property value: "+key);byName.put(key,value);}
    }
    public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name,Class<T> type){return new PropertyEnum<T>(name,type,Arrays.asList(type.getEnumConstants()));}
    public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name,Class<T> type,Predicate<T> predicate){List<T> values=new ArrayList<T>();for(T value:type.getEnumConstants())if(predicate.apply(value))values.add(value);return new PropertyEnum<T>(name,type,values);}
    public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name,Class<T> type,Collection<T> values){return new PropertyEnum<T>(name,type,values);}
    @SafeVarargs public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name,Class<T> type,T... values){return create(name,type,Arrays.asList(values));}
    public Collection<T> getAllowedValues(){return allowed;}
    public Optional<T> parseValue(String value){return Optional.fromNullable(byName.get(value));}
    public String getName(T value){return value.getName();}
    @Override public boolean equals(Object other){return super.equals(other)&&allowed.equals(((PropertyEnum<?>)other).allowed)&&byName.equals(((PropertyEnum<?>)other).byName);}
    @Override public int hashCode(){return 31*(31*super.hashCode()+allowed.hashCode())+byName.hashCode();}
}
