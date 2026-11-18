package com.dhanantry.scapeandrunparasites.backport.state;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
public final class PropertyBool extends PropertyHelper<Boolean> {
    private final ImmutableSet<Boolean> allowed=ImmutableSet.of(Boolean.TRUE,Boolean.FALSE);
    private PropertyBool(String name){super(name,Boolean.class);}
    public static PropertyBool create(String name){return new PropertyBool(name);}
    public Collection<Boolean> getAllowedValues(){return allowed;}
    public Optional<Boolean> parseValue(String value){return "true".equals(value)||"false".equals(value)?Optional.of(Boolean.valueOf(value)):Optional.<Boolean>absent();}
    public String getName(Boolean value){return value.toString();}
}
