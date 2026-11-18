package personal.truehorror.core;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import personal.truehorror.network.EffectMessage;
import static org.junit.Assert.*;

public class EffectPacketTest {
    @Test public void fixedSizeRoundTrip(){
        ByteBuf buffer=Unpooled.buffer();try{
            new EffectMessage(1,55,.65F).toBytes(buffer);assertEquals(7,buffer.readableBytes());
            EffectMessage read=new EffectMessage();read.fromBytes(buffer);assertEquals(1,read.type);assertEquals(55,read.duration);assertEquals(.65F,read.intensity,.00001F);
        }finally{buffer.release();}
    }
    @Test public void rejectsOversizedAndMalformedEffects(){
        for(EffectMessage bad:new EffectMessage[]{new EffectMessage(8,30,.5F),new EffectMessage(0,10000,.5F),new EffectMessage(1,30,Float.NaN),new EffectMessage(1,30,Float.POSITIVE_INFINITY)}){
            ByteBuf buffer=Unpooled.buffer();try{bad.toBytes(buffer);try{new EffectMessage().fromBytes(buffer);fail();}catch(IllegalArgumentException expected){}}finally{buffer.release();}
        }
        ByteBuf buffer=Unpooled.buffer();try{buffer.writeLong(0);try{new EffectMessage().fromBytes(buffer);fail();}catch(IllegalArgumentException expected){}}finally{buffer.release();}
    }
}
