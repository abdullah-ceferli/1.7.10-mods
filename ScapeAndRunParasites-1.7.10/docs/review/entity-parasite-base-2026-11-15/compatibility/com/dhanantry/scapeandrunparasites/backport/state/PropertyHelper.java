package com.dhanantry.scapeandrunparasites.backport.state;
public abstract class PropertyHelper<T extends Comparable<T>> implements IProperty<T> {
    private final String name;
    private final Class<T> valueClass;
    protected PropertyHelper(String name,Class<T> valueClass){this.name=name;this.valueClass=valueClass;}
    public String getName(){return name;}
    public Class<T> getValueClass(){return valueClass;}
    @Override public boolean equals(Object other){if(this==other)return true;if(other==null||getClass()!=other.getClass())return false;PropertyHelper<?> p=(PropertyHelper<?>)other;return valueClass.equals(p.valueClass)&&name.equals(p.name);}
    @Override public int hashCode(){return 31*valueClass.hashCode()+name.hashCode();}
    @Override public String toString(){return "Property{name="+name+", class="+valueClass.getName()+", values="+getAllowedValues()+"}";}
}
