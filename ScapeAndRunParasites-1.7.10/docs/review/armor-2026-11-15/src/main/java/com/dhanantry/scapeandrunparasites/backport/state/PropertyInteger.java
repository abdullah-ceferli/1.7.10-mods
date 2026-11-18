package com.dhanantry.scapeandrunparasites.backport.state;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
public final class PropertyInteger extends PropertyHelper<Integer> {
    private final ImmutableSet<Integer> allowed;
    private PropertyInteger(String name,int min,int max){
        super(name,Integer.class);
        if(min<0)throw new IllegalArgumentException("Minimum must be nonnegative: "+name);
        if(max<=min)throw new IllegalArgumentException("Maximum must exceed minimum: "+name);
        java.util.Set<Integer> values=new java.util.HashSet<Integer>();for(int value=min;;value++){values.add(value);if(value==max)break;}allowed=ImmutableSet.copyOf(values);
    }
    public static PropertyInteger create(String name,int min,int max){return new PropertyInteger(name,min,max);}
    public Collection<Integer> getAllowedValues(){return allowed;}
    public Optional<Integer> parseValue(String value){try{Integer parsed=Integer.valueOf(value);return allowed.contains(parsed)?Optional.of(parsed):Optional.<Integer>absent();}catch(NumberFormatException e){return Optional.absent();}}
    public String getName(Integer value){return value.toString();}
    @Override public boolean equals(Object other){return super.equals(other)&&allowed.equals(((PropertyInteger)other).allowed);}
    @Override public int hashCode(){return 31*super.hashCode()+allowed.hashCode();}
}
