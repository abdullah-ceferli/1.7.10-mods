package com.dhanantry.scapeandrunparasites.backport.util;
import java.util.*;
/** Non-null collections, including fixed-size inventory lists reset by clear(). */
public final class NonNullList<E> extends AbstractList<E> implements RandomAccess {
    private final List<E> delegate;private final E defaultValue;
    private NonNullList(List<E> delegate,E defaultValue){this.delegate=delegate;this.defaultValue=defaultValue;}
    public static <E> NonNullList<E> create(){return new NonNullList<E>(new ArrayList<E>(),null);}
    public static <E> NonNullList<E> withSize(int size,E value){Objects.requireNonNull(value);if(size<0)throw new NegativeArraySizeException();@SuppressWarnings("unchecked") E[] a=(E[])new Object[size];Arrays.fill(a,value);return new NonNullList<E>(Arrays.asList(a),value);}
    @SafeVarargs public static <E> NonNullList<E> from(E defaultValue,E... values){return new NonNullList<E>(Arrays.asList(values),defaultValue);}
    public E get(int index){return delegate.get(index);}public int size(){return delegate.size();}
    public E set(int index,E value){return delegate.set(index,Objects.requireNonNull(value));}
    public void add(int index,E value){delegate.add(index,Objects.requireNonNull(value));}
    public E remove(int index){return delegate.remove(index);}
    @Override public void clear(){if(defaultValue==null)delegate.clear();else for(int i=0;i<size();i++)delegate.set(i,defaultValue);}
}
