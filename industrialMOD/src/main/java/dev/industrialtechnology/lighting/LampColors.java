package dev.industrialtechnology.lighting;

/** Metadata follows native wool order; native dye damage is 15 minus the color index. */
public final class LampColors {
    public static final String[] NAMES={"white","orange","magenta","light_blue","yellow","lime","pink","gray","light_gray","cyan","purple","blue","brown","green","red","black"};
    private static final int[] RGB={0xF9FFFE,0xF9801D,0xC74EBD,0x3AB3DA,0xFED83D,0x80C71F,0xF38BAA,0x474F52,0x9D9D97,0x169C9C,0x8932B8,0x3C44AA,0x835432,0x5E7C16,0xB02E26,0x1D1D21};
    public static int color(int metadata,boolean lit){int rgb=RGB[metadata&15];if(lit)return rgb;return ((rgb>>16&255)*45/100<<16)|((rgb>>8&255)*45/100<<8)|((rgb&255)*45/100);}
    private LampColors(){}
}
