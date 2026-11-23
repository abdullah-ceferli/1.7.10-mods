package dev.zahin.mobileperformance.client;

import java.util.Arrays;

public final class FrameStats {
    private final long[] frames=new long[600];private int count,index;private long previous;
    public void reset(){count=index=0;previous=0;}
    public void pause(){previous=0;}
    public void frame(long now){if(previous!=0&&now>previous){frames[index]=now-previous;index=(index+1)%frames.length;count=Math.min(frames.length,count+1);}previous=now;}
    public int samples(){return count;}
    public double averageMs(){if(count==0)return 0;double sum=0;for(int i=0;i<count;i++)sum+=frames[i];return sum/count/1e6;}
    public double p95Ms(){if(count==0)return 0;long[] copy=Arrays.copyOf(frames,count);Arrays.sort(copy);return copy[(int)Math.ceil(count*.95)-1]/1e6;}
    public double fps(){double ms=averageMs();return ms==0?0:1000/ms;}
}
