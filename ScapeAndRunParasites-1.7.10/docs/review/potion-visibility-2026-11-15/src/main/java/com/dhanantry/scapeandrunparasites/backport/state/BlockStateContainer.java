package com.dhanantry.scapeandrunparasites.backport.state;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.util.*;
import com.google.common.collect.*;
import java.util.*;import java.util.regex.Pattern;
import net.minecraft.block.Block;import net.minecraft.block.material.Material;import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;import net.minecraft.world.*;
/** Immutable canonical state graph; state changes select existing nodes instead of mutating shared states. */
public class BlockStateContainer {
    private static final Pattern VALID_NAME=Pattern.compile("^[a-z0-9_]+$");
    private final Block block;
    private final ImmutableSortedMap<String,IProperty<?>> properties;
    private final ImmutableList<IBlockState> states;
    private final Map<ImmutableMap<IProperty<?>,Comparable<?>>,IBlockState> byValues=new HashMap<ImmutableMap<IProperty<?>,Comparable<?>>,IBlockState>();
    public BlockStateContainer(Block block,IProperty<?>... supplied){
        this.block=block;TreeMap<String,IProperty<?>> sorted=new TreeMap<String,IProperty<?>>();
        for(IProperty<?> property:supplied){
            if(!VALID_NAME.matcher(property.getName()).matches())throw new IllegalArgumentException("Invalid property name: "+property.getName());
            sorted.put(property.getName(),property);
            for(Comparable<?> value:property.getAllowedValues())if(!VALID_NAME.matcher(serialized(property,value)).matches())throw new IllegalArgumentException("Invalid property value name: "+serialized(property,value));
        }
        properties=ImmutableSortedMap.copyOf(sorted);List<IBlockState> values=new ArrayList<IBlockState>();
        build(new ArrayList<IProperty<?>>(properties.values()),0,new LinkedHashMap<IProperty<?>,Comparable<?>>(),values);states=ImmutableList.copyOf(values);
    }
    @SuppressWarnings({"unchecked","rawtypes"}) private static String serialized(IProperty property,Comparable value){return property.getName(value);}
    private void build(List<IProperty<?>> keys,int index,LinkedHashMap<IProperty<?>,Comparable<?>> current,List<IBlockState> values){
        if(index==keys.size()){ImmutableMap<IProperty<?>,Comparable<?>> map=ImmutableMap.copyOf(current);IBlockState state=new StateImplementation(this,block,map);byValues.put(map,state);values.add(state);return;}
        IProperty<?> key=keys.get(index);for(Comparable<?> value:key.getAllowedValues()){current.put(key,value);build(keys,index+1,current,values);}current.remove(key);
    }
    public Block getBlock(){return block;}
    public IBlockState getBaseState(){return states.get(0);}
    public ImmutableList<IBlockState> getValidStates(){return states;}
    public Collection<IProperty<?>> getProperties(){return properties.values();}
    public IProperty<?> getProperty(String name){return properties.get(name);}
    public static class StateImplementation implements IBlockState {
        protected final BlockStateContainer container;protected final Block block;protected final ImmutableMap<IProperty<?>,Comparable<?>> properties;
        protected StateImplementation(BlockStateContainer container,Block block,ImmutableMap<IProperty<?>,Comparable<?>> properties){this.container=container;this.block=block;this.properties=properties;}
        public Block getBlock(){return block;}
        public Collection<IProperty<?>> getPropertyKeys(){return properties.keySet();}
        @SuppressWarnings("unchecked") public <T extends Comparable<T>> T getValue(IProperty<T> property){Comparable<?> value=properties.get(property);if(value==null)throw new IllegalArgumentException("Property is not present: "+property.getName());return (T)value;}
        public IBlockState withProperty(IProperty<?> property,Comparable<?> value){
            Comparable<?> previous=properties.get(property);if(previous==null)throw new IllegalArgumentException("Property is not present: "+property.getName());
            if(!property.getAllowedValues().contains(value))throw new IllegalArgumentException("Value is not allowed for "+property.getName()+": "+value);
            if(previous.equals(value))return this;
            Map<IProperty<?>,Comparable<?>> copy=new LinkedHashMap<IProperty<?>,Comparable<?>>(properties);copy.put(property,value);IBlockState result=container.byValues.get(ImmutableMap.copyOf(copy));
            if(result==null)throw new IllegalStateException("Missing canonical state transition");return result;
        }
        public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property){T value=getValue(property);Iterator<T> iterator=property.getAllowedValues().iterator();while(iterator.hasNext())if(iterator.next().equals(value))return withProperty(property,iterator.hasNext()?iterator.next():property.getAllowedValues().iterator().next());throw new IllegalArgumentException("Current value is not allowed");}
        public ImmutableMap<IProperty<?>,Comparable<?>> getProperties(){return properties;}
        public Material getMaterial(){return BlockStates.material(this);}
        public boolean isFullBlock(){return BlockStates.fullBlock(this);}public boolean isFullCube(){return BlockStates.fullCube(this);}public boolean isOpaqueCube(){return BlockStates.opaque(this);}
        public boolean isNormalCube(){return BlockStates.normalCube(this);}public boolean isBlockNormalCube(){return BlockStates.blockNormalCube(this);}
        public boolean canProvidePower(){return BlockStates.providesPower(this);}public boolean canEntitySpawn(Entity entity){return BlockStates.canEntitySpawn(this,entity);}
        public int getLightValue(){return BlockStates.light(this);}public float getBlockHardness(World world,BlockPos pos){return BlockStates.hardness(this,world,pos);}
        public boolean isSideSolid(IBlockAccess access,BlockPos pos,EnumFacing side){return BlockStates.sideSolid(this,access,pos,side);}
        public BlockFaceShape getBlockFaceShape(IBlockAccess access,BlockPos pos,EnumFacing side){return BlockStates.face(this,access,pos,side);}
        public AxisAlignedBB getCollisionBoundingBox(IBlockAccess access,BlockPos pos){return BlockStates.collision(this,access,pos);}
        public IBlockState getActualState(IBlockAccess access,BlockPos pos){return block instanceof StatefulBlock?((StatefulBlock)block).getActualState(this,access,pos):this;}
        public IBlockState withRotation(Rotation rotation){return block instanceof StatefulBlock?((StatefulBlock)block).withRotation(this,rotation):this;}
        public IBlockState withMirror(Mirror mirror){return block instanceof StatefulBlock?((StatefulBlock)block).withMirror(this,mirror):this;}
        @Override public String toString(){StringBuilder text=new StringBuilder(block==null?"unbound":block.getUnlocalizedName());if(!properties.isEmpty()){text.append('[');boolean first=true;for(Map.Entry<IProperty<?>,Comparable<?>> p:properties.entrySet()){if(!first)text.append(',');first=false;text.append(p.getKey().getName()).append('=').append(serialized(p.getKey(),p.getValue()));}text.append(']');}return text.toString();}
    }
}
