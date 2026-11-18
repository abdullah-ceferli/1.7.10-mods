package com.dhanantry.scapeandrunparasites.backport.sound;
/** Modern five-sound material backed by the native placement/break/step callbacks. */
public class SoundType extends net.minecraft.block.Block.SoundType {
    public static final SoundType WOOD = new SoundType(1.0F,1.0F,"srpvanilla112:block.wood.break","srpvanilla112:block.wood.step","srpvanilla112:block.wood.place","srpvanilla112:block.wood.hit","srpvanilla112:block.wood.fall");
    public static final SoundType GROUND = new SoundType(1.0F,1.0F,"srpvanilla112:block.gravel.break","srpvanilla112:block.gravel.step","srpvanilla112:block.gravel.place","srpvanilla112:block.gravel.hit","srpvanilla112:block.gravel.fall");
    public static final SoundType PLANT = new SoundType(1.0F,1.0F,"srpvanilla112:block.grass.break","srpvanilla112:block.grass.step","srpvanilla112:block.grass.place","srpvanilla112:block.grass.hit","srpvanilla112:block.grass.fall");
    public static final SoundType STONE = new SoundType(1.0F,1.0F,"srpvanilla112:block.stone.break","srpvanilla112:block.stone.step","srpvanilla112:block.stone.place","srpvanilla112:block.stone.hit","srpvanilla112:block.stone.fall");
    public static final SoundType METAL = new SoundType(1.0F,1.5F,"srpvanilla112:block.metal.break","srpvanilla112:block.metal.step","srpvanilla112:block.metal.place","srpvanilla112:block.metal.hit","srpvanilla112:block.metal.fall");
    public static final SoundType GLASS = new SoundType(1.0F,1.0F,"srpvanilla112:block.glass.break","srpvanilla112:block.glass.step","srpvanilla112:block.glass.place","srpvanilla112:block.glass.hit","srpvanilla112:block.glass.fall");
    public static final SoundType CLOTH = new SoundType(1.0F,1.0F,"srpvanilla112:block.cloth.break","srpvanilla112:block.cloth.step","srpvanilla112:block.cloth.place","srpvanilla112:block.cloth.hit","srpvanilla112:block.cloth.fall");
    public static final SoundType SAND = new SoundType(1.0F,1.0F,"srpvanilla112:block.sand.break","srpvanilla112:block.sand.step","srpvanilla112:block.sand.place","srpvanilla112:block.sand.hit","srpvanilla112:block.sand.fall");
    public static final SoundType SNOW = new SoundType(1.0F,1.0F,"srpvanilla112:block.snow.break","srpvanilla112:block.snow.step","srpvanilla112:block.snow.place","srpvanilla112:block.snow.hit","srpvanilla112:block.snow.fall");
    public static final SoundType LADDER = new SoundType(1.0F,1.0F,"srpvanilla112:block.ladder.break","srpvanilla112:block.ladder.step","srpvanilla112:block.ladder.place","srpvanilla112:block.ladder.hit","srpvanilla112:block.ladder.fall");
    public static final SoundType ANVIL = new SoundType(0.3F,1.0F,"srpvanilla112:block.anvil.break","srpvanilla112:block.anvil.step","srpvanilla112:block.anvil.place","srpvanilla112:block.anvil.hit","srpvanilla112:block.anvil.fall");
    public static final SoundType SLIME = new SoundType(1.0F,1.0F,"srpvanilla112:block.slime.break","srpvanilla112:block.slime.step","srpvanilla112:block.slime.place","srpvanilla112:block.slime.hit","srpvanilla112:block.slime.fall");
    private final String broken,step,place,hit,fall;
    public SoundType(float volume,float pitch,String broken,String step,String place,String hit,String fall){super("srp",volume,pitch);this.broken=broken;this.step=step;this.place=place;this.hit=hit;this.fall=fall;}
    @Override public String getBreakSound(){return SoundIds.category(broken,ModernSoundCategory.BLOCKS).toString();}
    @Override public String getStepResourcePath(){return SoundIds.category(step,ModernSoundCategory.BLOCKS).toString();}
    @Override public String func_150496_b(){return SoundIds.category(place,ModernSoundCategory.BLOCKS).toString();}
    public String getStepSound(){return step;} public String getPlaceSound(){return place;} public String getHitSound(){return hit;} public String getFallSound(){return fall;}
    public static SoundType get(net.minecraft.block.Block block,com.dhanantry.scapeandrunparasites.backport.state.IBlockState state,net.minecraft.world.World world,com.dhanantry.scapeandrunparasites.backport.math.BlockPos pos,net.minecraft.entity.Entity entity){return block instanceof com.dhanantry.scapeandrunparasites.backport.state.StatefulBlock?((com.dhanantry.scapeandrunparasites.backport.state.StatefulBlock)block).getSoundType(state,world,pos,entity):of(block);}
    public static SoundType of(net.minecraft.block.Block block){net.minecraft.block.Block.SoundType nativeType=block.stepSound;if(nativeType instanceof SoundType)return (SoundType)nativeType;
        if(nativeType==net.minecraft.block.Block.soundTypeStone)return STONE;
        if(nativeType==net.minecraft.block.Block.soundTypePiston)return STONE;
        if(nativeType==net.minecraft.block.Block.soundTypeWood)return WOOD;
        if(nativeType==net.minecraft.block.Block.soundTypeGravel)return GROUND;
        if(nativeType==net.minecraft.block.Block.soundTypeGrass)return PLANT;
        if(nativeType==net.minecraft.block.Block.soundTypeMetal)return METAL;
        if(nativeType==net.minecraft.block.Block.soundTypeGlass)return GLASS;
        if(nativeType==net.minecraft.block.Block.soundTypeCloth)return CLOTH;
        if(nativeType==net.minecraft.block.Block.soundTypeSand)return SAND;
        if(nativeType==net.minecraft.block.Block.soundTypeSnow)return SNOW;
        if(nativeType==net.minecraft.block.Block.soundTypeLadder)return LADDER;
        if(nativeType==net.minecraft.block.Block.soundTypeAnvil)return ANVIL;
        return new SoundType(nativeType.getVolume(),nativeType.getPitch(),nativeType.getBreakSound(),nativeType.getStepResourcePath(),nativeType.func_150496_b(),nativeType.getBreakSound(),nativeType.getStepResourcePath());}
}
