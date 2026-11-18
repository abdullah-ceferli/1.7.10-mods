# Potion particle visibility: verified native facts — 2026-11-15

This is a read-only check against the actual Forge 1.7.10 / 10.13.4.1614 compile API and original Minecraft 1.12.2 bytecode. No Java source was changed and no new helper/gameplay behavior was tested in this review. Full extracted bytecode: potion-fx-native-api.log. Machine-readable facts and input hashes: potion-fx-native-api.json.

## The four requested facts

| Question | Verified answer |
| --- | --- |
| Metadata method | There is no separate updatePotionMetadata() in this native API. The method is protected void EntityLivingBase.updatePotionEffects(). It ticks/removes effects and updates the metadata in the same method. |
| DataWatcher fields | Index 7 is an Integer particle RGB color. Index 8 is a Byte ambient marker, written as byte 0 or 1. The client reads those same entries to choose ordinary mobSpell/mobSpellAmbient particles. |
| Color calculation | The native server calls public static int PotionHelper.calcPotionLiquidColor(java.util.Collection) with activePotionsMap.values(). The public accessor public java.util.Collection EntityLivingBase.getActivePotionEffects() exposes a raw Collection in 1.7.10. |
| Add/combine and extended properties | public void EntityLivingBase.addPotionEffect(PotionEffect) first checks isPotionApplicable. If the ID already exists it calls existing.combine(incoming), then onChangedPotionEffect(existing,true); otherwise it inserts the effect and calls onNewPotionEffect. Forge's IExtendedEntityProperties is available, with automatic native entity NBT callbacks. |

Exact available signatures:

~~~java
protected void EntityLivingBase.updatePotionEffects();
public void EntityLivingBase.addPotionEffect(PotionEffect effect);
public Collection EntityLivingBase.getActivePotionEffects();
public static int PotionHelper.calcPotionLiquidColor(Collection effects);
public static boolean PotionHelper.func_82817_b(Collection effects);
public void PotionEffect.combine(PotionEffect incoming);

public interface IExtendedEntityProperties {
    void saveNBTData(NBTTagCompound compound);
    void loadNBTData(NBTTagCompound compound);
    void init(Entity entity, World world);
}

public String Entity.registerExtendedProperties(
    String identifier, IExtendedEntityProperties properties);
public IExtendedEntityProperties Entity.getExtendedProperties(String identifier);
~~~

These qualified-method declarations are a signature summary, not a Java source file to compile.

## Tick placement

Native server call order is:

1. WorldServer.updateEntities().
2. FMLCommonHandler.onPostWorldTick(world), which posts SERVER WorldTickEvent with Phase.END.
3. EntityTracker.updateTrackedEntities(), which sends changed entity metadata.
4. Later, FMLCommonHandler.onPostServerTick(), which posts ServerTickEvent.END.

Therefore server WorldTickEvent.END is the useful normal boundary for recoloring before that tick's tracker broadcast. Register FML tick listeners on FMLCommonHandler.instance().bus(). EntityConstructing listeners for property registration belong on MinecraftForge.EVENT_BUS.

LivingUpdateEvent fires at the start of EntityLivingBase.onUpdate, before the native base update path reaches updatePotionEffects, so simply writing the color there can be overwritten. ServerTickEvent.END is after the tracker broadcast and can allow the engine's unfiltered value to be sent first.

Do not call updatePotionEffects() a second time merely to refresh color: it also advances durations and processes expiry. Recompute the color only.

## Important corrections for the proposed design

### Empty visible list must mean color zero

PotionHelper.calcPotionLiquidColor(emptyCollection) returns 3694022 (0x385DC6), not zero. Passing an empty filtered list to it would create a visible fallback color. When no visible effects remain, explicitly write Integer.valueOf(0) to watcher 7. Otherwise pass the visible effects to the real native color helper, retaining its amplifier-weighted color calculation.

Watcher 8 should retain the ambient aggregate over all active effects. Original 1.12 EntityLivingBase.areAllPotionsAmbient also checks every active effect, including hidden ones. Filtering the ambient aggregate as well can change particle frequency when a hidden non-ambient effect coexists with a visible ambient effect. Leaving native watcher 8 intact avoids that mismatch.

### Property loading happens before active-effect NBT is rebuilt

Native Entity.readFromNBT invokes IExtendedEntityProperties.loadNBTData before the virtual readEntityFromNBT call that restores the living entity's ActiveEffects. Load the saved ID set at that point, but defer pruning against active effects until restoration is complete, such as the world-end pass. Otherwise the load callback can erase valid hidden state.

Likewise, saveNBTData runs before writeEntityToNBT. A separate namespaced tag is appropriate; do not assume ActiveEffects already exists in that callback.

### A helper used only at 164 sites cannot observe every application

Original 1.12 combine unconditionally copies the incoming showParticles flag after its amplifier/duration logic. The latest accepted application wins even when its duration is shorter or its amplifier is weaker.

The native add path can reject an application via isPotionApplicable; a rejected incoming effect must not change the current visibility. The native method returns void, and the presence of an older active effect alone is not proof that the incoming application was accepted.

Applications outside the new helper also matter: vanilla drinking/splash effects, commands, other mods and shorter SRP constructors can reapply one of the same vanilla IDs. Native combine keeps the existing PotionEffect object, and a weaker/shorter incoming visible effect can leave its duration/amplifier unchanged. A tick-only comparison or an ID set cannot reliably detect that application. For full parity, hook the accepted native add/combine path or provide equivalent coverage; rewriting only the five-argument calls is not sufficient.

Clear hidden state on removal, cure and expiry. Also cover remove-then-readd between ticks, rather than waiting for a tick to observe a missing effect. Native copy/NBT-based effect applications need an explicit rule for retaining or resetting the flag.

### Server-only color is enough for ordinary swirls, not every original consumer

The ordinary native swirl particles consume synced watcher color/ambient metadata, so no new client flag is needed for that limited purpose.

However, original 1.12 GuiIngame.renderPotionEffects explicitly calls PotionEffect.doesShowParticles() before drawing its HUD effect entry. That was confirmed from original bytecode: GuiIngame/func_184048_a calls PotionEffect/func_188418_e and skips the entry when false. Preserving that HUD behavior will need client-visible per-effect state or an equivalent client representation; a single RGB value cannot recover which effect was hidden.

Custom particles explicitly emitted by SRP effect logic are separate from vanilla swirl metadata and should not be disabled indiscriminately.

## What to implement and verify

The per-entity saved state is a reasonable foundation. The complete design still needs coverage for all accepted native applications and the later original HUD behavior.

Useful cases for the implementation:

- Hidden-only, shown-only and mixed-color effects, including amplifier weighting.
- Stronger, weaker and shorter reapplications with opposite visibility flags.
- Rejected applications and vanilla reapplications of a previously hidden effect.
- Removal/cure/expiry followed by reapplication, including within the same tick.
- Save/reload with properties restored before native ActiveEffects.
- Metadata received by a tracking client and tracker re-entry, without an unfiltered-color flash.
- Hidden effects still apply their gameplay and attribute behavior.
- Hidden non-ambient plus visible ambient effects preserve the original ambient aggregate.

Potion items and all original recipes remain in scope. No new decision to cut them is required; PotionType/native brewing work is still outstanding.
