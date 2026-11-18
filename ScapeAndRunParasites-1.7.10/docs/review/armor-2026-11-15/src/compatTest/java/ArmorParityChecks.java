package dev.srpbackport.tests;
import java.lang.reflect.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import com.dhanantry.scapeandrunparasites.backport.entity.ArmorMath;
public final class ArmorParityChecks {
    public static void main(String[] args)throws Exception {
        Method original=Class.forName("up").getMethod("a",float.class,float.class,float.class);int cases=0;
        float[] damage={0,0.01F,1,10,20,100,1000,Float.MIN_VALUE,Float.MAX_VALUE};
        float[] armor={0,0.5F,1,5,19.999F,20,25,30};float[] toughness={0,0.5F,2,8,12,20};
        Random random=new Random(171012);
        for(int n=0;n<30000+damage.length*armor.length*toughness.length;n++) {
            float d,a,t;
            if(n<damage.length*armor.length*toughness.length){int k=n;t=toughness[k%toughness.length];k/=toughness.length;a=armor[k%armor.length];d=damage[k/armor.length];}
            else{d=random.nextFloat()*2000;a=random.nextFloat()*30;t=random.nextFloat()*20;}
            float expected=(Float)original.invoke(null,d,a,t),actual=ArmorMath.afterAbsorb(d,a,t);
            if(Float.floatToIntBits(expected)!=Float.floatToIntBits(actual))throw new AssertionError(d+"/"+a+"/"+t+": "+expected+" != "+actual);
            cases++;
        }
        String result="PASS: Supplied armor formula matches original 1.12.2 CombatRules bytecode bit-for-bit across "+cases+" boundary/random cases\nFailed: 0\n";
        Files.write(Paths.get(args[0]),result.getBytes(StandardCharsets.UTF_8));System.out.print(result);
    }
}
