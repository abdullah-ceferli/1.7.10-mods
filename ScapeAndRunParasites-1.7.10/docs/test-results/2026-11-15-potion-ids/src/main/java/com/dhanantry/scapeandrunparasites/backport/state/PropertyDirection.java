package com.dhanantry.scapeandrunparasites.backport.state;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.google.common.base.Predicate;
import java.util.*;
public final class PropertyDirection extends PropertyEnum<EnumFacing> {
    private PropertyDirection(String name,Collection<EnumFacing> values){super(name,EnumFacing.class,values);}
    public static PropertyDirection create(String name){return new PropertyDirection(name,Arrays.asList(EnumFacing.values()));}
    public static PropertyDirection create(String name,Predicate<EnumFacing> filter){List<EnumFacing> values=new ArrayList<EnumFacing>();for(EnumFacing value:EnumFacing.values())if(filter.apply(value))values.add(value);return new PropertyDirection(name,values);}
    public static PropertyDirection create(String name,Collection<EnumFacing> values){return new PropertyDirection(name,values);}
}
