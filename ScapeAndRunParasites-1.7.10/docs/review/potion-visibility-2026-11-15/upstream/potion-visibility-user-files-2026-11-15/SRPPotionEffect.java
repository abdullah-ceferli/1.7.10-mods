package com.dhanantry.scapeandrunparasites.backport.potion;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Native PotionEffect carrying the modern show-particles flag. The flag rides on the effect object only until the
 * engine accepts it; PotionFxHooks moves it into the per-entity PotionVisibility state at that moment.
 * Plain PotionEffect instances (vanilla drinks, commands, other mods) count as showing particles.
 */
public class SRPPotionEffect extends PotionEffect {
    private final boolean showParticles;

    public SRPPotionEffect(Potion potion, int duration, int amplifier, boolean ambient, boolean showParticles) {
        super(potion.id, duration, amplifier, ambient);
        this.showParticles = showParticles;
    }
    public SRPPotionEffect(Potion potion, int duration, int amplifier) { this(potion, duration, amplifier, false, true); }
    public SRPPotionEffect(Potion potion, int duration) { this(potion, duration, 0, false, true); }

    public boolean doesShowParticles() { return this.showParticles; }
}
