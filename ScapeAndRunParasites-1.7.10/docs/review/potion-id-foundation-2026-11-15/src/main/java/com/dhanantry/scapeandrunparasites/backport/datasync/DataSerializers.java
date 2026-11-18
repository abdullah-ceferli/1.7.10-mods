package com.dhanantry.scapeandrunparasites.backport.datasync;
import java.io.IOException;import net.minecraft.network.PacketBuffer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
/** The five serializers used by SRP. Their IDs and scalar wire encoding match the original implementation. */
public final class DataSerializers {
    private DataSerializers(){}
    private abstract static class Scalar<T> implements DataSerializer<T>{
        private final Class<T> type;Scalar(Class<T> type){this.type=type;}
        public DataParameter<T> createKey(int id){return new DataParameter<T>(id,this);}
        public T copyValue(T value){return value;}
        public T validate(Object value,boolean registration){if(!type.isInstance(value))throw new IllegalArgumentException("Expected tracked "+type.getSimpleName()+", got "+value);return type.cast(value);}
    }
    public static final DataSerializer<Byte> BYTE=new Scalar<Byte>(Byte.class){
        public void write(PacketBuffer b,Byte v){b.writeByte(v);}public Byte read(PacketBuffer b){return b.readByte();}
        @Override public Byte validate(Object v,boolean registration){
            // CFR loses Byte.valueOf for integer literals boxed through the erased register API.
            if(registration&&v instanceof Integer&&((Integer)v)>=-128&&((Integer)v)<=127)return ((Integer)v).byteValue();
            return super.validate(v,registration);
        }
    };
    public static final DataSerializer<Integer> VARINT=new Scalar<Integer>(Integer.class){public void write(PacketBuffer b,Integer v){b.writeVarIntToBuffer(v);}public Integer read(PacketBuffer b){return b.readVarIntFromBuffer();}};
    public static final DataSerializer<Float> FLOAT=new Scalar<Float>(Float.class){public void write(PacketBuffer b,Float v){b.writeFloat(v);}public Float read(PacketBuffer b){return b.readFloat();}};
    public static final DataSerializer<Boolean> BOOLEAN=new Scalar<Boolean>(Boolean.class){public void write(PacketBuffer b,Boolean v){b.writeBoolean(v);}public Boolean read(PacketBuffer b){return b.readBoolean();}};
    public static final DataSerializer<BlockPos> BLOCK_POS=new Scalar<BlockPos>(BlockPos.class){public void write(PacketBuffer b,BlockPos v){b.writeLong(v.toLong());}public BlockPos read(PacketBuffer b){return BlockPos.fromLong(b.readLong());}@Override public BlockPos copyValue(BlockPos value){return value;}};
    public static int getSerializerId(DataSerializer<?> serializer){if(serializer==BYTE)return 0;if(serializer==VARINT)return 1;if(serializer==FLOAT)return 2;if(serializer==BOOLEAN)return 6;if(serializer==BLOCK_POS)return 8;return -1;}
    public static DataSerializer<?> getSerializer(int id){switch(id){case 0:return BYTE;case 1:return VARINT;case 2:return FLOAT;case 6:return BOOLEAN;case 8:return BLOCK_POS;default:return null;}}
}
