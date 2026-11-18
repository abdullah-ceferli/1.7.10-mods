package personal.truehorror.core;

import java.util.Random;

/** All time values use active ticks, so saves and reconnects cannot reset cooldowns. */
public final class PlayerProgress {
    public long activeTicks,nextEvent,nextScare;
    public int encounters,warnings,caution;
    public String recent="";
    public int stage(int graceMinutes,double speed) {
        double minutes=activeTicks*Math.max(0.1,speed)/1200.0;
        if(minutes<graceMinutes)return 0;
        if(minutes<graceMinutes+10)return 1;
        if(minutes<graceMinutes+30)return 2;
        return 3;
    }
    public void advance(int ticks,boolean active){if(active)activeTicks=Math.min(Long.MAX_VALUE-10000,activeTicks+Math.max(0,ticks));}
    public void schedule(Random random,int minSeconds,int maxSeconds,double frequency) {
        int span=Math.max(0,maxSeconds-minSeconds);
        long delay=Math.round((Math.max(10,minSeconds)+(span==0?0:random.nextInt(span+1)))*20/Math.max(0.05,frequency));
        nextEvent=activeTicks+Math.max(200,delay);
    }
    public boolean sawRecently(String id){return ("|"+recent+"|").contains("|"+id+"|");}
    public void remember(String id){
        String[] old=recent.isEmpty()?new String[0]:recent.split("\\|");
        StringBuilder result=new StringBuilder(id);
        for(int i=0;i<old.length&&i<3;i++)result.append('|').append(old[i]);
        recent=result.toString();
    }
}
