package com.dhanantry.scapeandrunparasites.backport.potion;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/** Modern constructor/visibility semantics with native effect IDs and native callback types. */
public class SRPPotionEffect extends PotionEffect {
    private boolean showParticles;

    public SRPPotionEffect(Potion potion, int duration, int amplifier, boolean ambient, boolean showParticles) {
        this(potion.id, duration, amplifier, ambient, showParticles);
    }
    public SRPPotionEffect(Potion potion, int duration, int amplifier, boolean ambient) { this(potion, duration, amplifier, ambient, true); }
    public SRPPotionEffect(Potion potion, int duration, int amplifier) { this(potion, duration, amplifier, false, true); }
    public SRPPotionEffect(Potion potion, int duration) { this(potion, duration, 0, false, true); }
    public SRPPotionEffect(int id, int duration, int amplifier, boolean ambient, boolean showParticles) {
        super(id, duration, amplifier, ambient);this.showParticles=showParticles;
    }
    public SRPPotionEffect(int id, int duration, int amplifier, boolean ambient) { this(id,duration,amplifier,ambient,true); }
    public SRPPotionEffect(int id, int duration, int amplifier) { this(id,duration,amplifier,false,true); }
    public SRPPotionEffect(int id, int duration) { this(id,duration,0,false,true); }
    public SRPPotionEffect(PotionEffect other) {
        this(other.getPotionID(),other.getDuration(),other.getAmplifier(),other.getIsAmbient(),
            !(other instanceof SRPPotionEffect)||((SRPPotionEffect)other).doesShowParticles());
        setCurativeItems(other.getCurativeItems());
    }
    @Override public void combine(PotionEffect incoming) {
        super.combine(incoming);
        showParticles=!(incoming instanceof SRPPotionEffect)||((SRPPotionEffect)incoming).doesShowParticles();
    }
    public boolean doesShowParticles() { return showParticles; }
}
