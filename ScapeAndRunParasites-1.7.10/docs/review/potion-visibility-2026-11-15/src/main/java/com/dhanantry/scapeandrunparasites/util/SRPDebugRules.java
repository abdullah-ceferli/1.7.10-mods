/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.GameRules
 *  net.minecraft.world.GameRules$ValueType
 *  net.minecraftforge.event.world.WorldEvent$Load
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util;

import net.minecraft.world.GameRules;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class SRPDebugRules {
    public static final String RULE_FORCE_HARLEQUIN = "srpForceHarlequin";

    private SRPDebugRules() {
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load e) {
        if (e.getWorld().isRemote) {
            return;
        }
        GameRules rules = e.getWorld().getGameRules();
        if (!rules.hasRule(RULE_FORCE_HARLEQUIN)) {
            rules.addGameRule(RULE_FORCE_HARLEQUIN, "false", GameRules.ValueType.BOOLEAN_VALUE);
        }
    }
}

