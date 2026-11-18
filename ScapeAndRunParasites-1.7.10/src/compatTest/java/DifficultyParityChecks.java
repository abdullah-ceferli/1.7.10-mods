package dev.srpbackport.tests;
import java.io.*;import java.lang.reflect.*;import java.util.*;
import net.minecraft.world.EnumDifficulty;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;

/** Compare against the original client bytecode, not a second copy of the formula. */
public final class DifficultyParityChecks {
    static int cases;
    static void same(float expected,float actual,String label) {
        if(Float.floatToIntBits(expected)!=Float.floatToIntBits(actual))throw new AssertionError(label+": "+expected+" != "+actual);
    }
    static void compare(Constructor<?> ctor,Object[] enums,Method additional,Method clamped,Method harder,int level,long time,long inhabited,float moon)throws Exception {
        Object original=ctor.newInstance(enums[level],time,inhabited,moon);
        DifficultyInstance actual=new DifficultyInstance(EnumDifficulty.getDifficultyEnum(level),time,inhabited,moon);
        same((Float)additional.invoke(original),actual.getAdditionalDifficulty(),"Additional case "+cases);
        same((Float)clamped.invoke(original),actual.getClampedAdditionalDifficulty(),"Clamped case "+cases);
        for(float threshold:new float[]{Float.NaN,-1,0,1,2,3,4,6,Float.POSITIVE_INFINITY}) {
            if((Boolean)harder.invoke(original,threshold)!=actual.isHarderThan(threshold))throw new AssertionError("Threshold case "+cases);
        }
        cases++;
    }
    public static void main(String[] args)throws Exception {
        Properties names=new Properties();try(InputStream in=new FileInputStream(args[0])){names.load(in);}
        Class<?> original=Class.forName(names.getProperty("class")),difficulty=Class.forName(names.getProperty("enum"));
        Constructor<?> ctor=original.getConstructor(difficulty,long.class,long.class,float.class);
        Method additional=original.getMethod(names.getProperty("additional")),clamped=original.getMethod(names.getProperty("clamped")),harder=original.getMethod(names.getProperty("harder"),float.class);
        Object[] enums=difficulty.getEnumConstants();
        long[] times={Long.MIN_VALUE,-1,0,71999,72000,72001,1511999,1512000,1512001,Long.MAX_VALUE};
        long[] inhabited={Long.MIN_VALUE,-1,0,1,3599999,3600000,3600001,Long.MAX_VALUE};
        float[] moons={Float.NEGATIVE_INFINITY,-1,0,0.25F,0.5F,0.75F,1,2,Float.POSITIVE_INFINITY,Float.NaN};
        for(int d=0;d<4;d++)for(long t:times)for(long i:inhabited)for(float m:moons)compare(ctor,enums,additional,clamped,harder,d,t,i,m);
        Random random=new Random(171012);
        for(int n=0;n<20000;n++)compare(ctor,enums,additional,clamped,harder,random.nextInt(4),random.nextLong(),random.nextLong(),random.nextFloat()*4-1);
        String result="PASS: Original 1.12 local difficulty matches bit-for-bit across "+cases+" boundary/random cases, clamped values and strict threshold comparisons\nFailed: 0\n";
        try(Writer out=new OutputStreamWriter(new FileOutputStream(args[1]),"UTF-8")){out.write(result);}System.out.print(result);
    }
}
