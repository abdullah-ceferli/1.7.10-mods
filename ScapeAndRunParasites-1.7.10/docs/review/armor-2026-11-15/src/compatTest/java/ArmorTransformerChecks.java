package dev.srpbackport.tests;
import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
import com.dhanantry.scapeandrunparasites.backport.entity.core.ArmorAttributeTransformer;
import com.dhanantry.scapeandrunparasites.backport.potion.core.PotionFxTransformer;
public final class ArmorTransformerChecks {
    static final String TARGET="net.minecraft.entity.EntityLivingBase",HOOK="com/dhanantry/scapeandrunparasites/backport/entity/ModernAttributes";
    static byte[] rename(byte[] bytes,boolean corrupt) {
        ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);
        for(Object o:c.methods){MethodNode m=(MethodNode)o;if(m.name.equals("applyEntityAttributes"))m.name=corrupt?"unexpected_attribute_method":"func_110147_ax";}
        ClassWriter w=new ClassWriter(0);c.accept(w);return w.toByteArray();
    }
    static void check(byte[] bytes,boolean chained) {
        ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);int armor=0,potion=0;
        for(Object o:c.methods)for(AbstractInsnNode i=((MethodNode)o).instructions.getFirst();i!=null;i=i.getNext())if(i instanceof MethodInsnNode){MethodInsnNode call=(MethodInsnNode)i;if(call.owner.equals(HOOK)&&call.name.equals("register"))armor++;if(call.owner.endsWith("/PotionFxHooks"))potion++;}
        if(armor!=1||potion!=(chained?3:0))throw new AssertionError("Hook counts "+armor+"/"+potion);
    }
    public static void main(String[] args)throws Exception {
        byte[] nativeBytes=PotionFxTransformerChecks.nativeClass(args[0]);ArmorAttributeTransformer t=new ArmorAttributeTransformer();List<String> report=new ArrayList<String>();
        for(boolean srg:new boolean[]{false,true}){byte[] in=srg?rename(nativeBytes,false):nativeBytes,out=t.transform("",TARGET,in);check(out,false);if(t.transform("",TARGET,out)!=out)throw new AssertionError("Not idempotent");}
        report.add("PASS: Actual MCP/SRG native initialization gets one idempotent attribute-registration hook");
        PotionFxTransformer p=new PotionFxTransformer();check(t.transform("",TARGET,p.transform("",TARGET,nativeBytes)),true);check(p.transform("",TARGET,t.transform("",TARGET,nativeBytes)),true);
        report.add("PASS: Armor and potion transformers compose in both orders without losing existing hooks");
        if(t.transform("","other",nativeBytes)!=nativeBytes||t.transform("",TARGET,null)!=null)throw new AssertionError("Unrelated/null bytes changed");
        boolean refused=false;try{t.transform("",TARGET,rename(nativeBytes,true));}catch(IllegalStateException expected){refused=true;}if(!refused)throw new AssertionError("Unexpected method shape accepted");
        report.add("PASS: Unexpected native attribute signatures fail explicitly and unrelated classes stay unchanged");report.add("Failed: 0");Files.write(Paths.get(args[1]),report,StandardCharsets.UTF_8);for(String s:report)System.out.println(s);
    }
}
