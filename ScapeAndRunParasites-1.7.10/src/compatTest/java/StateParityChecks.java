package dev.srpbackport.tests;
import java.io.*;import java.lang.reflect.*;import java.util.*;
import com.google.common.base.Optional;import com.google.common.base.Predicate;
import com.dhanantry.scapeandrunparasites.backport.state.*;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import static dev.srpbackport.tests.FoundationParityChecks.*;

/** Real original Minecraft classes are loaded only for reference, never included in the port. */
public final class StateParityChecks {
    static Object create(String type,String srg,Class<?>[] params,Object... args)throws Exception{return call(ref(type),srg,params,args);}
    static String names(Collection<?> values){List<String> out=new ArrayList<String>();for(Object value:values)out.add(value instanceof Enum?((Enum<?>)value).name():String.valueOf(value));return out.toString();}
    static Collection<?> allowed(Object property)throws Exception{return (Collection<?>)no(property,"func_177700_c");}
    static void property(Object original,IProperty<?> own)throws Exception {
        check(no(original,"func_177701_a").equals(own.getName()),"property name");
        check(names(allowed(original)).equals(names(own.getAllowedValues())),"allowed value order: "+names(allowed(original))+" != "+names(own.getAllowedValues()));
        for(String value:new String[]{"true","false","TRUE","False","0","1","+1","01","-1","3","9","128","135","north","south","up","down","x","y","z","","missing"}){
            Optional<?> expected=(Optional<?>)call(original,"func_185929_b",new Class<?>[]{String.class},value),actual=own.parseValue(value);
            check(expected.isPresent()==actual.isPresent(),"property parse presence: "+value);
            if(expected.isPresent())check(names(Arrays.asList(expected.get())).equals(names(Arrays.asList(actual.get()))),"property parse value: "+value);
        }
    }
    interface Op {Object run()throws Exception;}
    static Class<?> failure(Op op){try{op.run();return null;}catch(InvocationTargetException e){if(e.getCause() instanceof LinkageError)e.getCause().printStackTrace();return e.getCause().getClass();}catch(Throwable e){return e.getClass();}}
    static void failures(Op reference,Op actual,String reason){Class<?> expected=failure(reference),found=failure(actual);check(expected==found,reason+": "+expected+" != "+found);}
    static Object integer(String name,int min,int max)throws Exception{return create("PropertyInteger","func_177719_a",new Class<?>[]{String.class,int.class,int.class},name,min,max);}
    static Object bool(String name)throws Exception{return create("PropertyBool","func_177716_a",new Class<?>[]{String.class},name);}
    static void scalarProperties()throws Exception{
        property(bool("active"),PropertyBool.create("active"));
        for(int[] range:new int[][]{{0,1},{0,3},{1,7},{0,15},{3,19},{128,135},{15,44},{8,19}})property(integer("stage",range[0],range[1]),PropertyInteger.create("stage",range[0],range[1]));
        for(int[] range:new int[][]{{-1,3},{1,1},{5,2}})failures(()->integer("bad",range[0],range[1]),()->PropertyInteger.create("bad",range[0],range[1]),"invalid integer range");
        Object a=integer("age",0,3),b=integer("age",0,3),c=integer("age",0,4);PropertyInteger x=PropertyInteger.create("age",0,3),y=PropertyInteger.create("age",0,3),z=PropertyInteger.create("age",0,4);
        check(a.equals(b)==x.equals(y)&&a.equals(c)==x.equals(z),"integer structural equality");check(x.equals(y)&&x.hashCode()==y.hashCode(),"equal property hash contract");
        report.add("PASS: original boolean/integer properties, parsing, invalid ranges, allowed ordering and equality");groups++;
    }
    @SuppressWarnings({"unchecked","rawtypes"}) static void enumProperties()throws Exception{
        Object d=create("PropertyDirection","func_177714_a",new Class<?>[]{String.class},"facing");property(d,PropertyDirection.create("facing"));
        Object plane=ref("EnumFacing$Plane").getEnumConstants()[0];
        // Select by name, independent of enum ordinal.
        for(Object v:ref("EnumFacing$Plane").getEnumConstants())if(((Enum<?>)v).name().equals("HORIZONTAL"))plane=v;
        Object h=create("PropertyDirection","func_177712_a",new Class<?>[]{String.class,Predicate.class},"facing",plane);property(h,PropertyDirection.create("facing",EnumFacing.Plane.HORIZONTAL));
        List<Object> originalSubset=Arrays.asList(ref("EnumFacing").getEnumConstants()[1],ref("EnumFacing").getEnumConstants()[3]);
        Object subset=create("PropertyDirection","func_177713_a",new Class<?>[]{String.class,Collection.class},"side",originalSubset);property(subset,PropertyDirection.create("side",Arrays.asList(EnumFacing.UP,EnumFacing.SOUTH)));
        Object axis=create("PropertyEnum","func_177709_a",new Class<?>[]{String.class,Class.class},"axis",ref("EnumFacing$Axis"));property(axis,PropertyEnum.create("axis",EnumFacing.Axis.class));
        failures(()->create("PropertyEnum","func_177708_a",new Class<?>[]{String.class,Class.class,Collection.class},"axis",ref("EnumFacing$Axis"),Arrays.asList(ref("EnumFacing$Axis").getEnumConstants()[0],ref("EnumFacing$Axis").getEnumConstants()[0])),()->PropertyEnum.create("axis",EnumFacing.Axis.class,Arrays.asList(EnumFacing.Axis.X,EnumFacing.Axis.X)),"duplicate identical enum entry");
        report.add("PASS: original enum/direction properties, horizontal predicates, subsets, serialized names and parsing");groups++;
    }
    static Object container(Object... values)throws Exception{return containerForBlock(null,values);}
    static Object containerForBlock(Object block,Object... values)throws Exception{
        Object properties=Array.newInstance(ref("IProperty"),values.length);for(int i=0;i<values.length;i++)Array.set(properties,i,values[i]);
        return ref("BlockStateContainer").getConstructor(Class.forName("aow"),properties.getClass()).newInstance(block,properties);
    }
    @SuppressWarnings("unchecked") static void graph()throws Exception{
        Object oa=bool("active"),ol=integer("level",0,3),of=create("PropertyDirection","func_177712_a",new Class<?>[]{String.class,Predicate.class},"facing",Arrays.stream(ref("EnumFacing$Plane").getEnumConstants()).filter(v->((Enum<?>)v).name().equals("HORIZONTAL")).findFirst().get());
        PropertyBool a=PropertyBool.create("active");PropertyInteger l=PropertyInteger.create("level",0,3);PropertyDirection f=PropertyDirection.create("facing",EnumFacing.Plane.HORIZONTAL);
        Object original=container(ol,of,oa);BlockStateContainer own=new BlockStateContainer(null,l,f,a);
        List<?> refs=(List<?>)no(original,"func_177619_a");List<IBlockState> states=own.getValidStates();check(refs.size()==states.size()&&states.size()==32,"cartesian state count");
        Object[] props={oa,of,ol};IProperty<?>[] ownProps={a,f,l};Class<?> ip=ref("IProperty");
        for(int i=0;i<states.size();i++){
            Object refState=refs.get(i);IBlockState state=states.get(i);check(state.getBlock()==null,"state retains owning block");
            List<String> keyNames=new ArrayList<String>();for(Object p:(Collection<?>)no(refState,"func_177227_a"))keyNames.add((String)no(p,"func_177701_a"));
            List<String> ownNames=new ArrayList<String>();for(IProperty<?> p:state.getPropertyKeys())ownNames.add(p.getName());check(keyNames.equals(ownNames),"sorted property key order");
            for(int p=0;p<props.length;p++){
                Object value=call(refState,"func_177229_b",new Class<?>[]{ip},props[p]);Comparable<?> ownValue=state.getProperties().get(ownProps[p]);check(names(Arrays.asList(value)).equals(names(Arrays.asList(ownValue))),"cartesian enumeration order");
                List<?> rv=new ArrayList<Object>(allowed(props[p]));List<?> ov=new ArrayList<Object>(ownProps[p].getAllowedValues());
                for(int j=0;j<rv.size();j++){
                    Object changed=call(refState,"func_177226_a",new Class<?>[]{ip,Comparable.class},props[p],rv.get(j));IBlockState changedOwn=state.withProperty(ownProps[p],(Comparable<?>)ov.get(j));
                    check(refs.indexOf(changed)==states.indexOf(changedOwn),"canonical transition index");check((changed==refState)==(changedOwn==state),"unchanged state identity");
                    check(changedOwn.withProperty(ownProps[p],ownValue)==state,"canonical round trip");
                }
                Object cycled=call(refState,"func_177231_a",new Class<?>[]{ip},props[p]);IBlockState cycledOwn=state.cycleProperty((IProperty)ownProps[p]);check(refs.indexOf(cycled)==states.indexOf(cycledOwn),"property cycle order");
            }
            failures(()->call(refState,"func_177226_a",new Class<?>[]{ip,Comparable.class},ol,20),()->state.withProperty(l,20),"reject invalid property value");
            failures(()->{((Map<?,?>)no(refState,"func_177228_b")).clear();return null;},()->{state.getProperties().clear();return null;},"state map immutable");
        }
        check(failure(()->new BlockStateContainer(null,PropertyBool.create("Bad-Name")))==IllegalArgumentException.class,"invalid property name rejected");
        report.add("PASS: original 32-state Cartesian graph, sorted keys, every value transition, cycle order, canonical identity and immutability");groups++;
    }
    public static void main(String[] args)throws Exception{
        try(InputStream in=new FileInputStream(args[0])){mappings.load(in);}
        test("scalar properties",()->scalarProperties());test("enum properties",()->enumProperties());test("state graph",()->graph());
        report.add("Assertions: "+assertions);report.add("Passed groups: "+groups);report.add("Failed: "+FoundationParityChecks.failures);
        for(String line:report)System.out.println(line);try(PrintWriter out=new PrintWriter(args[1],"UTF-8")){for(String line:report)out.println(line);}if(FoundationParityChecks.failures!=0)System.exit(1);
    }
}
