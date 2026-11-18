/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import java.util.Random;
import java.util.UUID;
import net.minecraft.util.math.Vec3i;

public class MathHelper {
    public static final float field_180189_a;
    private static final float[] field_76144_a;
    private static final Random field_188211_c;
    private static final int[] field_151242_b;
    private static final double field_181163_d;
    private static final double[] field_181164_e;
    private static final double[] field_181165_f;

    public static float func_76126_a(float f) {
        return field_76144_a[(int)(f * 10430.378f) & 0xFFFF];
    }

    public static float func_76134_b(float f) {
        return field_76144_a[(int)(f * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static float func_76129_c(float f) {
        return (float)Math.sqrt(f);
    }

    public static float func_76133_a(double d) {
        return (float)Math.sqrt(d);
    }

    public static int func_76141_d(float f) {
        int n = (int)f;
        return f < (float)n ? n - 1 : n;
    }

    public static int func_76140_b(double d) {
        return (int)(d + 1024.0) - 1024;
    }

    public static int func_76128_c(double d) {
        int n = (int)d;
        return d < (double)n ? n - 1 : n;
    }

    public static long func_76124_d(double d) {
        long l = (long)d;
        return d < (double)l ? l - 1L : l;
    }

    public static int func_154353_e(double d) {
        return (int)(d >= 0.0 ? d : -d + 1.0);
    }

    public static float func_76135_e(float f) {
        return f >= 0.0f ? f : -f;
    }

    public static int func_76130_a(int n) {
        return n >= 0 ? n : -n;
    }

    public static int func_76123_f(float f) {
        int n = (int)f;
        return f > (float)n ? n + 1 : n;
    }

    public static int func_76143_f(double d) {
        int n = (int)d;
        return d > (double)n ? n + 1 : n;
    }

    public static int func_76125_a(int n, int n2, int n3) {
        if (n < n2) {
            return n2;
        }
        if (n > n3) {
            return n3;
        }
        return n;
    }

    public static float func_76131_a(float f, float f2, float f3) {
        if (f < f2) {
            return f2;
        }
        if (f > f3) {
            return f3;
        }
        return f;
    }

    public static double func_151237_a(double d, double d2, double d3) {
        if (d < d2) {
            return d2;
        }
        if (d > d3) {
            return d3;
        }
        return d;
    }

    public static double func_151238_b(double d, double d2, double d3) {
        if (d3 < 0.0) {
            return d;
        }
        if (d3 > 1.0) {
            return d2;
        }
        return d + (d2 - d) * d3;
    }

    public static double func_76132_a(double d, double d2) {
        if (d < 0.0) {
            d = -d;
        }
        if (d2 < 0.0) {
            d2 = -d2;
        }
        return d > d2 ? d : d2;
    }

    public static int func_76137_a(int n, int n2) {
        if (n < 0) {
            return -((-n - 1) / n2) - 1;
        }
        return n / n2;
    }

    public static int func_76136_a(Random random, int n, int n2) {
        if (n >= n2) {
            return n;
        }
        return random.nextInt(n2 - n + 1) + n;
    }

    public static float func_151240_a(Random random, float f, float f2) {
        if (f >= f2) {
            return f;
        }
        return random.nextFloat() * (f2 - f) + f;
    }

    public static double func_82716_a(Random random, double d, double d2) {
        if (d >= d2) {
            return d;
        }
        return random.nextDouble() * (d2 - d) + d;
    }

    public static double func_76127_a(long[] lArray) {
        long l = 0L;
        for (long l2 : lArray) {
            l += l2;
        }
        return (double)l / (double)lArray.length;
    }

    public static boolean func_180185_a(float f, float f2) {
        return MathHelper.func_76135_e(f2 - f) < 1.0E-5f;
    }

    public static int func_180184_b(int n, int n2) {
        return (n % n2 + n2) % n2;
    }

    public static float func_188207_b(float f, float f2) {
        return (f % f2 + f2) % f2;
    }

    public static double func_191273_b(double d, double d2) {
        return (d % d2 + d2) % d2;
    }

    public static float func_76142_g(float f) {
        if ((f %= 360.0f) >= 180.0f) {
            f -= 360.0f;
        }
        if (f < -180.0f) {
            f += 360.0f;
        }
        return f;
    }

    public static double func_76138_g(double d) {
        if ((d %= 360.0) >= 180.0) {
            d -= 360.0;
        }
        if (d < -180.0) {
            d += 360.0;
        }
        return d;
    }

    public static int func_188209_b(int n) {
        if ((n %= 360) >= 180) {
            n -= 360;
        }
        if (n < -180) {
            n += 360;
        }
        return n;
    }

    public static int func_82715_a(String string, int n) {
        try {
            return Integer.parseInt(string);
        }
        catch (Throwable throwable) {
            return n;
        }
    }

    public static int func_82714_a(String string, int n, int n2) {
        return Math.max(n2, MathHelper.func_82715_a(string, n));
    }

    public static double func_82712_a(String string, double d) {
        try {
            return Double.parseDouble(string);
        }
        catch (Throwable throwable) {
            return d;
        }
    }

    public static double func_82713_a(String string, double d, double d2) {
        return Math.max(d2, MathHelper.func_82712_a(string, d));
    }

    public static int func_151236_b(int n) {
        \u2603 = n - 1;
        \u2603 |= \u2603 >> 1;
        \u2603 |= \u2603 >> 2;
        \u2603 |= \u2603 >> 4;
        \u2603 |= \u2603 >> 8;
        \u2603 |= \u2603 >> 16;
        return \u2603 + 1;
    }

    private static boolean func_151235_d(int n) {
        return n != 0 && (n & n - 1) == 0;
    }

    public static int func_151241_e(int n) {
        n = MathHelper.func_151235_d(n) ? n : MathHelper.func_151236_b(n);
        return field_151242_b[(int)((long)n * 125613361L >> 27) & 0x1F];
    }

    public static int func_151239_c(int n) {
        return MathHelper.func_151241_e(n) - (MathHelper.func_151235_d(n) ? 0 : 1);
    }

    public static int func_154354_b(int n, int n2) {
        if (n2 == 0) {
            return 0;
        }
        if (n == 0) {
            return n2;
        }
        if (n < 0) {
            n2 *= -1;
        }
        if ((\u2603 = n % n2) == 0) {
            return n;
        }
        return n + n2 - \u2603;
    }

    public static int func_180183_b(float f, float f2, float f3) {
        return MathHelper.func_180181_b(MathHelper.func_76141_d(f * 255.0f), MathHelper.func_76141_d(f2 * 255.0f), MathHelper.func_76141_d(f3 * 255.0f));
    }

    public static int func_180181_b(int n, int n2, int n3) {
        \u2603 = n;
        \u2603 = (\u2603 << 8) + n2;
        \u2603 = (\u2603 << 8) + n3;
        return \u2603;
    }

    public static int func_180188_d(int n, int n2) {
        \u2603 = (n & 0xFF0000) >> 16;
        \u2603 = (n2 & 0xFF0000) >> 16;
        \u2603 = (n & 0xFF00) >> 8;
        \u2603 = (n2 & 0xFF00) >> 8;
        \u2603 = (n & 0xFF) >> 0;
        \u2603 = (n2 & 0xFF) >> 0;
        \u2603 = (int)((float)\u2603 * (float)\u2603 / 255.0f);
        \u2603 = (int)((float)\u2603 * (float)\u2603 / 255.0f);
        \u2603 = (int)((float)\u2603 * (float)\u2603 / 255.0f);
        return n & 0xFF000000 | \u2603 << 16 | \u2603 << 8 | \u2603;
    }

    public static double func_181162_h(double d) {
        return d - Math.floor(d);
    }

    public static long func_180186_a(Vec3i vec3i) {
        return MathHelper.func_180187_c(vec3i.func_177958_n(), vec3i.func_177956_o(), vec3i.func_177952_p());
    }

    public static long func_180187_c(int n, int n2, int n3) {
        long l = (long)(n * 3129871) ^ (long)n3 * 116129781L ^ (long)n2;
        l = l * l * 42317861L + l * 11L;
        return l;
    }

    public static UUID func_180182_a(Random random) {
        long l = random.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
        \u2603 = random.nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
        return new UUID(l, \u2603);
    }

    public static UUID func_188210_a() {
        return MathHelper.func_180182_a(field_188211_c);
    }

    public static double func_181160_c(double d, double d2, double d3) {
        return (d - d2) / (d3 - d2);
    }

    public static double func_181159_b(double d, double d2) {
        \u2603 = d2 * d2 + d * d;
        if (Double.isNaN(\u2603)) {
            return Double.NaN;
        }
        boolean bl = \u2603 = d < 0.0;
        if (\u2603) {
            d = -d;
        }
        boolean bl2 = \u2603 = d2 < 0.0;
        if (\u2603) {
            d2 = -d2;
        }
        boolean bl3 = \u2603 = d > d2;
        if (\u2603) {
            \u2603 = d2;
            d2 = d;
            d = \u2603;
        }
        \u2603 = MathHelper.func_181161_i(\u2603);
        d2 *= \u2603;
        \u2603 = field_181163_d + (d *= \u2603);
        int n = (int)Double.doubleToRawLongBits(\u2603);
        double \u26032 = field_181164_e[n];
        double \u26033 = field_181165_f[n];
        double \u26034 = \u2603 - field_181163_d;
        double \u26035 = d * \u26033 - d2 * \u26034;
        double \u26036 = (6.0 + \u26035 * \u26035) * \u26035 * 0.16666666666666666;
        double \u26037 = \u26032 + \u26036;
        if (\u2603) {
            \u26037 = 1.5707963267948966 - \u26037;
        }
        if (\u2603) {
            \u26037 = Math.PI - \u26037;
        }
        if (\u2603) {
            \u26037 = -\u26037;
        }
        return \u26037;
    }

    public static double func_181161_i(double \u260322) {
        double d;
        d = 0.5 * \u260322;
        long l = Double.doubleToRawLongBits(\u260322);
        l = 6910469410427058090L - (l >> 1);
        double \u260322 = Double.longBitsToDouble(l);
        \u260322 *= 1.5 - d * \u260322 * \u260322;
        return \u260322;
    }

    public static int func_181758_c(float f5, float f2, float f32) {
        float f4;
        int n = (int)(f5 * 6.0f) % 6;
        float \u26032 = f5 * 6.0f - (float)n;
        float \u26033 = f32 * (1.0f - f2);
        float \u26034 = f32 * (1.0f - \u26032 * f2);
        float \u26035 = f32 * (1.0f - (1.0f - \u26032) * f2);
        switch (n) {
            case 0: {
                f4 = f32;
                \u2603 = \u26035;
                \u2603 = \u26033;
                break;
            }
            case 1: {
                f4 = \u26034;
                \u2603 = f32;
                \u2603 = \u26033;
                break;
            }
            case 2: {
                f4 = \u26033;
                \u2603 = f32;
                \u2603 = \u26035;
                break;
            }
            case 3: {
                f4 = \u26033;
                \u2603 = \u26034;
                \u2603 = f32;
                break;
            }
            case 4: {
                f4 = \u26035;
                \u2603 = \u26033;
                \u2603 = f32;
                break;
            }
            case 5: {
                float f32;
                f4 = f32;
                \u2603 = \u26033;
                \u2603 = \u26034;
                break;
            }
            default: {
                float f5;
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + f5 + ", " + f2 + ", " + f32);
            }
        }
        int \u26036 = MathHelper.func_76125_a((int)(f4 * 255.0f), 0, 255);
        int \u26037 = MathHelper.func_76125_a((int)(\u2603 * 255.0f), 0, 255);
        int \u26038 = MathHelper.func_76125_a((int)(\u2603 * 255.0f), 0, 255);
        return \u26036 << 16 | \u26037 << 8 | \u26038;
    }

    public static int func_188208_f(int n) {
        n ^= n >>> 16;
        n *= -2048144789;
        n ^= n >>> 13;
        n *= -1028477387;
        n ^= n >>> 16;
        return n;
    }

    static {
        int n;
        field_180189_a = MathHelper.func_76129_c(2.0f);
        field_188211_c = new Random();
        field_76144_a = new float[65536];
        for (n = 0; n < 65536; ++n) {
            MathHelper.field_76144_a[n] = (float)Math.sin((double)n * Math.PI * 2.0 / 65536.0);
        }
        field_151242_b = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
        field_181163_d = Double.longBitsToDouble(4805340802404319232L);
        field_181164_e = new double[257];
        field_181165_f = new double[257];
        for (n = 0; n < 257; ++n) {
            double d = (double)n / 256.0;
            \u2603 = Math.asin(d);
            MathHelper.field_181165_f[n] = Math.cos(\u2603);
            MathHelper.field_181164_e[n] = \u2603;
        }
    }
}

