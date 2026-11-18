package com.dhanantry.scapeandrunparasites.backport.math;
/** Original lookup-table steering calculation, including its inverse-square-root approximation. */
public final class ModernMath {
    private static final double FRACTION_BIAS=Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ASIN=new double[257],COS=new double[257];
    static{for(int i=0;i<257;i++){double angle=Math.asin((double)i/256.0);COS[i]=Math.cos(angle);ASIN[i]=angle;}}
    private ModernMath(){}
    public static double atan2(double y,double x){
        double squared=x*x+y*y;if(Double.isNaN(squared))return Double.NaN;
        boolean negativeY=y<0;if(negativeY)y=-y;boolean negativeX=x<0;if(negativeX)x=-x;
        boolean swap=y>x;if(swap){double old=x;x=y;y=old;}
        double inverse=fastInvSqrt(squared);x*=inverse;y*=inverse;
        double biased=FRACTION_BIAS+y;int index=(int)Double.doubleToRawLongBits(biased);
        double asin=ASIN[index],cos=COS[index],fraction=biased-FRACTION_BIAS;
        double error=y*cos-x*fraction;
        double correction=(6.0+error*error)*error*0.16666666666666666;
        double angle=asin+correction;if(swap)angle=1.5707963267948966-angle;
        if(negativeX)angle=Math.PI-angle;if(negativeY)angle=-angle;return angle;
    }
    public static double fastInvSqrt(double value){
        double half=0.5*value;long bits=Double.doubleToRawLongBits(value);
        bits=6910469410427058090L-(bits>>1);value=Double.longBitsToDouble(bits);
        value*=1.5-half*value*value;return value;
    }
}
