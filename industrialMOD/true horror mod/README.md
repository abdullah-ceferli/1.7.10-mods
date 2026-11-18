# True Horror - Minecraft 1.7.10

A standalone Forge / Java 8 horror mod with a slow escalation, Herobrine and six other creatures. No additional mod dependencies. Install the same version on the client and dedicated server; single-player and integrated LAN include the server side automatically.

## Playing

Install `TrueHorror-1.7.10-1.0.0.jar` in `mods` and start Minecraft. A fresh test world is a useful first place to learn the pacing. The normal first ten active minutes have only restrained sounds and minor anomalies. Distant encounters begin after that, with following/chases introduced as progression rises around 20 minutes and more dangerous encounters around 40 minutes.

Progress and cooldowns are saved separately in each player's Forge persisted NBT. Death, quitting, reconnecting and reloading do not restart the grace period. Dead/sleeping players do not progress; inactivity beyond one minute pauses progression until movement, looking around, mining or item use resumes. Creative players are excluded by default. Menus that pause an integrated world also pause its server clock.

## Creatures

| Creature | Behavior |
| --- | --- |
| Herobrine | Watches, follows out of sight and vanishes at close approach or sustained gaze. Repeatedly looking behind makes him cautious; ignoring successive stalking warnings can provoke a short pursuit later in progression. Includes distant, mining, window and rush-feint appearances. Uses the user's exact supplied skin. |
| The Watcher | Four blocks tall. Freezes when any nearby player watches it, then approaches when nobody watches. Close approach or bright light sends it away. Almost silent. |
| The Mimic | Asymmetric humanoid with a hinged jaw. Imitates animals/steps while waiting, then reveals itself and pursues when approached. |
| The Crawler | Low, four-limbed cave ambusher with articulated crawl motion and collision-based wall climbing. Quickly pursues close targets. |
| The Pale Man | Three and a half blocks tall. Starts slowly, with a drifting head and delayed sudden sprint. |
| The Shadow | Uses the user's replacement skin on a standard player model. Moves across peripheral space, retreats from bright light/gaze, and becomes more dangerous after sustained darkness. |
| The Dweller | Broad underground quadruped, with a jaw bite and forelimb swipe. Stalks before emerging; may retreat when hurt or exposed to light. |

Encounters use explicit observing/stalking/following/manifesting/hunting/attacking/disappearing states. Chases have a limited duration and temporary entities clean themselves up. Navigation uses existing loaded terrain; pursuit does not teleport through walls. The director limits overlapping creatures in the same region, with a default dimension-wide cap of eight.

## Events, sound and visuals

There are 30 contextual event types; see EVENTS.md. Weighted selection respects environment, progression, recent events, quiet periods and separate jumpscare cooldowns. Positional sound designs include footsteps, knocks, breaths, scratches, whispers, mimic lures, creature voices and four different scare stings. Some events are harmless evidence or distant sightings.

Screen effects use a short-lived vignette and fog tint. No forced camera rotation or fullscreen face image. Overlapping messages are bounded and effects expire automatically. Flashing is off by default. Torch flicker is a client-side lighting impression rather than repeated torch block updates.

## Build and item protection

**Block-changing events are OFF by default.** Creatures do not break blocks, open inventories, drop custom loot, trigger pressure plates or trample crops. Ordinary player combat/death rules still apply.

If explicitly enabled, only door/sign/leaf/cairn/shallow-tunnel events change blocks. Edits have a bounded saved undo list and never touch an existing tile entity or stored items. They revert only if the event's result is still unchanged, so a later player edit is preserved. Undo resumes when its chunk is loaded; chunks are never force-loaded for it. Optional effects may interact with normal Minecraft terrain/lighting rules, so leave this disabled around valuable builds.

## Configuration

Generated file: `config/truehorror.cfg`. The Mods -> True Horror -> Config screen exposes its categories. Server settings are authoritative; use `/truehorror reload` on a dedicated server or restart after editing the file. Client visual/audio preferences are local.

- `director`: master enable, initial delay, progression speed, event frequency, random spacing, creature encounter probability, regional/dimension caps, lifetime, creative/daylight behavior.
- `creatures`: enable Herobrine or each other creature individually, and damage multiplier. Damage 0 keeps encounters atmospheric.
- `jumpscares`: chance, persisted minimum cooldown, intensity.
- `client`: screen effects, sound effects, volume, flashing (off by default).
- `world`: block modifications (off by default).

Defaults: 10-minute major-encounter delay, randomized 45-150 second event spacing plus encounter recovery, 180+ second major-scare cooldown, 65% custom audio volume, one horror creature in a 48-block region. Sparse terrain can skip an event rather than place a monster inside blocks or load new chunks.

## Commands

`/truehorror status` is available to players. The following require operator permission; they are useful in a separate test world:

```
/truehorror gallery
/truehorror clear
/truehorror spawn watcher
/truehorror event FOOTSTEPS
/truehorror event WINDOW_VISITOR
/truehorror time 40
/truehorror reload
```

Gallery creates temporary non-attacking models, and clear removes only nearby horror entities. Commands can preview progression-restricted events, but world changes still require the config and terrain/spawn checks remain active. A console operator can append a player name after the command arguments.

## Development

Run `./build.ps1` with a Java 8 JDK; outputs the normal and source JARs. Source includes original mesh/pixel-art/audio authoring tools. See ASSET_CREDITS.md for the two user-supplied skins. No modern rendering, animation, shader or mixin library is required.

Verification results and their scope are recorded in VERIFICATION.md after the release checks. Automated fixtures use separate directories and never open a real player save.
