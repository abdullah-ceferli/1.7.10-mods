# CODEX PROMPT — Minecraft 1.7.10: True Horror Mod

## 1. Main Goal

Create a complete, high-quality **Minecraft Forge 1.7.10 horror mod** written in Java.

I want a genuinely terrifying horror experience featuring **Herobrine, multiple original horror creatures, mysterious events, stalking mechanics, scary sounds, jumpscares, environmental changes, and intelligent AI**.

The mod must feel like a professionally developed Minecraft horror mod, with carefully designed atmosphere, textures, models, animations, sounds, and gameplay.

**IMPORTANT: NO AI SLOP.**

Do not create ugly, random, generic, low-effort textures, models, or animations. Everything should look intentional, polished, and appropriate for Minecraft 1.7.10.

This should not be a simple mod where Herobrine spawns, runs at the player, and attacks. I want a complete psychological horror experience.

---

## 2. Herobrine — Main Horror Entity

Herobrine must be the most mysterious and intelligent entity in the mod.

### First 10 Minutes

When the player first enters a world:

- Herobrine should NOT immediately attack or constantly appear.
- For approximately the first 10 minutes of active gameplay, everything should feel mostly normal.
- Occasionally, the player may hear strange noises or notice something unusual.
- Small paranormal events can happen before the first major encounter.

**After approximately 10 minutes, Herobrine begins actively interacting with the player.**

Track time separately for each player and save it between sessions. Do not reset the timer every time the world reloads.

### Herobrine's Behavior

Implement multiple behavior systems:

**1. Stalking**
- Appears behind trees.
- Watches the player from mountains.
- Appears inside caves.
- Stands at a distance looking directly at the player.
- Disappears when the player approaches.
- Sometimes disappears when the player looks directly at him.
- Can silently follow the player without being detected.
- Occasionally appears outside the player's house at night.

**2. Psychological Horror**
- Places mysterious signs with unsettling messages.
- Creates small unusual structures.
- Occasionally removes leaves from trees.
- Opens wooden doors.
- Makes footsteps behind the player.
- Causes torches to flicker through temporary lighting effects.
- Sometimes makes nearby animals stare at the player.
- Creates mysterious tunnel noises.
- Occasionally leaves strange symbols or evidence of his presence.

**3. Intelligent AI**
Herobrine should react to player behavior.

Examples:
- If the player repeatedly looks behind them, Herobrine becomes more cautious.
- If the player stays in their house, unusual events can happen outside.
- If the player explores caves, Herobrine may stalk them from darkness.
- If the player ignores warning signs, his behavior can gradually become more aggressive.
- If the player looks through a window, Herobrine might be standing outside.

Avoid simple random teleportation and constant spawning.

Create a proper AI state machine with states such as:

- Dormant
- Observing
- Stalking
- Following
- Manifesting
- Hunting
- Attacking
- Disappearing

### Herobrine Jumpscares

Herobrine should have multiple unique jumpscare events.

Examples:
- Suddenly appears directly behind the player with a terrifying sound.
- Appears briefly in front of the player while mining.
- Runs toward the player extremely fast before disappearing.
- Appears in a dark corridor and slowly approaches.
- Looks through a window and vanishes.
- Causes the screen to darken briefly before appearing.

Make jumpscares genuinely frightening using sound, animation, timing, and lighting.

**Do not spam jumpscares.** Make them unpredictable, rare enough to remain effective, and configurable.

---

## 3. Additional Horror Creatures

Herobrine must NOT be the only horror entity.

Create at least **6 additional original horror creatures**, each with unique textures, models, AI, sounds, animations, and behavior.

### Creature 1 — The Watcher

A tall, thin, dark humanoid creature.

- Appears in forests at night.
- Watches the player from a distance.
- Moves when the player is not watching.
- Occasionally appears closer after the player looks away.
- Has faint glowing eyes.
- Makes almost no noise.
- Can disappear into shadows.

### Creature 2 — The Mimic

A creature that imitates familiar Minecraft sounds.

- Imitates footsteps, animals, and other recognizable sounds.
- Attempts to lure players into dark locations.
- Can appear as an unsettling humanoid figure.
- Reveals its terrifying appearance when close.
- May chase players who get too close.

### Creature 3 — The Crawler

A fast creature that moves on four limbs.

- Lives in caves and abandoned mineshafts.
- Crawls through dark tunnels.
- Can climb certain surfaces.
- Makes scratching and crawling sounds.
- May hide until the player approaches.
- Has a special chase animation.

### Creature 4 — The Pale Man

A tall, pale humanoid with unnatural movement.

- Appears in abandoned buildings, forests, and dark areas.
- Walks slowly at first.
- Sometimes turns its head toward the player.
- Can suddenly begin sprinting.
- Makes eerie breathing sounds.
- Uses disturbing but carefully animated movement.

### Creature 5 — The Shadow

A dark entity that is difficult to see.

- Appears in low-light areas.
- Can briefly move across the player's peripheral vision.
- Produces whispers and subtle visual effects.
- Attempts to confuse the player.
- Becomes more threatening when the player remains in darkness.

### Creature 6 — The Dweller

A dangerous underground monster.

- Spawns deep in caves.
- Hides in darkness.
- Makes distant screams and echoing noises.
- Can crawl, stalk, and chase the player.
- Has multiple attack animations.
- Sometimes retreats instead of fighting.

Each creature must have distinct behavior. Do not give every monster the same generic AI.

Add more original horror creatures if they meaningfully improve the mod.

---

## 4. Global Horror Director

Implement an intelligent **Horror Director System** controlling the entire experience.

The system should:

- Monitor how long each player has survived.
- Track the player's location and current environment.
- React to day/night cycles.
- Detect whether the player is underground, in a forest, or inside a building.
- Monitor recent encounters.
- Adjust horror intensity gradually.
- Prevent multiple creatures from constantly attacking simultaneously.
- Create periods of silence and safety between intense events.

### Horror Progression

**0–10 minutes: Uneasiness**
- Mostly normal Minecraft gameplay.
- Occasional mysterious sounds.
- Rare strange environmental events.

**10–20 minutes: First Encounters**
- Herobrine starts stalking.
- Distant sightings become possible.
- First unusual creature encounters.
- Rare jumpscares.

**20–40 minutes: Escalation**
- More frequent paranormal events.
- Creatures begin following the player.
- Occasional chases.
- More unsettling nighttime activity.

**40+ minutes: Full Horror**
- More complex stalking.
- Dangerous entities can appear more often.
- Multiple types of events.
- Rare, intense encounters and special sequences.

Do not use constant fixed intervals for every event. Use carefully controlled randomness, cooldowns, context, and event probabilities.

The player should never know exactly when something will happen.

---

## 5. High-Quality Textures and Models — EXTREMELY IMPORTANT

**I DO NOT WANT AI-SLOP TEXTURES OR MODELS.**

Do not create:

- Random colored boxes pretending to be monsters.
- One generic texture reused on every surface.
- Flat-colored cubes without details.
- Ugly, poorly proportioned models.
- Randomly generated pixel noise.
- Inconsistent texture resolution.
- Generic Steve models for every creature.
- Monsters that look like unfinished prototypes.

### Art Direction

Make the assets feel like they belong to Minecraft 1.7.10, while providing high-quality horror designs.

Use:

- Original, carefully constructed pixel-art textures.
- Consistent colors and shading.
- Recognizable, distinctive creature silhouettes.
- Detailed monster faces, limbs, and bodies.
- Proper UV mapping.
- Different textures where appropriate for different faces and body parts.
- Carefully designed glowing eyes where suitable.
- High-quality animations.

Use traditional Minecraft-style textures, typically 16×16 or 32×32 per appropriate texture area, with consistent pixel density.

For complex creatures, create proper custom models.

You may use Blender or compatible modeling tools where useful, but ensure assets work with the Minecraft 1.7.10 rendering system.

Use compatible custom renderers, model classes, or an appropriate verified legacy animation pipeline.

Do not assume modern Minecraft rendering APIs exist in 1.7.10.

All assets should be original or legitimately licensed.

**Take inspiration from the quality and atmosphere of established Minecraft horror mods, but do not simply copy their models or textures.**

If a graphics tool is unavailable, create carefully authored pixel-art and model assets using available tools rather than leaving generic placeholders.

---

## 6. Animations

Creatures must have natural and scary animations.

Implement:

- Idle animations
- Walking animations
- Running animations
- Crawling animations
- Head rotation
- Stalking poses
- Attack animations
- Jumpscare animations
- Disappearing animations
- Special animations for individual creatures

Examples:

The Crawler should actually crawl rather than walk like a zombie.

The Watcher should sometimes slowly turn its head toward the player.

The Pale Man should have unsettling movement transitions.

Herobrine should sometimes stand completely motionless, then suddenly move.

Make animations smooth and appropriate for Minecraft 1.7.10.

---

## 7. Horror Audio System

Sound design is extremely important.

Add a complete environmental horror sound system.

Examples:

- Footsteps behind the player
- Distant screaming
- Whispering
- Branches breaking
- Scratching inside caves
- Unusual breathing
- Quiet laughter
- Sudden silence
- Distant knocking
- Running footsteps
- Distorted ambient noises
- Mysterious cave echoes

Implement positional audio so sounds come from believable locations.

Sounds should not play randomly every few seconds.

Use weighted event selection and sensible cooldowns.

Include custom creature sounds and multiple high-quality jumpscare sounds.

Use properly licensed or original audio assets, not missing sound references or generic placeholders.

---

## 8. Special Paranormal Events

Create at least 20 different possible paranormal events.

Examples:

1. A door opens by itself.
2. Footsteps approach from behind.
3. Herobrine appears on a distant hill.
4. A creature briefly appears between trees.
5. The player hears mining sounds while alone.
6. A mysterious sign appears.
7. A nearby torch flickers.
8. The player hears breathing in a cave.
9. A creature stares through a window.
10. A strange tunnel appears in an appropriate location.
11. An eerie sound plays after the player wakes up.
12. Herobrine stands near the player's bed at night.
13. A creature suddenly runs across a corridor.
14. A distant humanoid silhouette disappears.
15. The player hears knocking from outside their shelter.
16. Something briefly moves behind a cave entrance.
17. An unusual structure appears in the wilderness.
18. Nearby animals become unusually silent.
19. A monster stalks the player without attacking.
20. A rare, terrifying chase event begins.

Not every event should be dangerous.

Some should simply make players question whether they saw something.

**Protect player builds and stored items by default.** World changes should be controlled, reversible where appropriate, and configurable.

---

## 9. Jumpscare System

Create a professional jumpscare manager.

Requirements:

- Multiple types of jumpscares.
- Context-sensitive triggers.
- Appropriate animations.
- Custom sound effects.
- Client-side visual effects.
- Cooldowns to prevent repetition.
- No constant unavoidable jumpscares.
- No broken camera movement.
- No permanently stuck screen effects.
- No crashes from overlapping events.

Include configuration options to adjust jumpscare frequency, intensity, volume, and flashing effects.

Make the horror immersive rather than just displaying a loud image on the screen.

---

## 10. Minecraft Forge 1.7.10 Compatibility

Technical requirements:

- Minecraft version: **1.7.10**
- Mod loader: **Minecraft Forge**
- Programming language: **Java 8**
- Use Forge APIs compatible with Minecraft 1.7.10.
- Use a compatible ForgeGradle version.
- No modern Java features unsupported by Java 8.
- No Minecraft 1.12+, 1.16+, or 1.20+ APIs.
- Proper client/server separation.
- Multiplayer-compatible entity behavior.
- Dedicated server support.
- Correct entity registration and networking.
- Persistent player-specific progression.
- Proper world save/load handling.
- Configurable mob spawn rates and horror settings.
- Avoid excessive chunk loading, entity scanning, and constant world modifications.

Server-side logic should control actual gameplay. Client-side logic should handle visuals, camera effects, particles, and appropriate sounds.

Ensure monsters do not teleport incorrectly, desynchronize, or get stuck in blocks.

---

## 11. Performance Optimization

The mod must run smoothly on ordinary Minecraft 1.7.10 installations.

- Avoid excessive AI calculations every tick.
- Limit active horror events.
- Use efficient entity detection.
- Optimize animations.
- Avoid spawning too many creatures.
- Avoid unnecessary chunk generation or loading.
- Clean up temporary entities and effects.
- Avoid memory leaks.
- Keep client-side effects lightweight.

Do not sacrifice all horror effects for performance, but optimize carefully.

---

## 12. Configuration

Provide a complete mod configuration system.

Let users configure:

- Enable/disable Herobrine
- Enable/disable individual creatures
- First major encounter delay (default 10 minutes)
- Horror event frequency
- Jumpscare frequency
- Jumpscare intensity
- Creature spawn frequency
- Creature damage
- Screen effects
- Sound effects
- World modifications
- Horror progression speed

Provide sensible defaults.

---

## 13. Final Development Requirements

I want a fully working horror mod, not a prototype or a collection of unfinished systems.

Please:

1. Inspect the existing project first if one exists.
2. Set up or preserve the correct Minecraft Forge 1.7.10 development environment.
3. Plan a proper modular architecture.
4. Implement Herobrine and the global Horror Director.
5. Implement all six additional creatures.
6. Create their custom models, textures, and animations.
7. Implement sound effects and paranormal events.
8. Implement jumpscares and progression.
9. Integrate everything into the actual game.
10. Build the mod and fix compilation errors.
11. Test client and dedicated-server compatibility where possible.
12. Ensure the mod produces a usable JAR.

Do not mark a feature as complete if it consists only of a placeholder or unused code.

Do not silently skip difficult features. If something cannot be created with the available tools, explain the limitation and implement the best genuinely functional alternative.

### Quality Rules

- No AI-slop textures.
- No generic recolored cubes for every mob.
- No unfinished prototype assets in the final build.
- No repeating the same jumpscare constantly.
- No broken creature animations.
- No fake AI systems that only teleport mobs randomly.
- No unnecessary modern Minecraft dependencies.
- No unnecessary libraries.
- No empty classes pretending to implement features.
- No broken texture paths or missing assets.
- No performance-destroying code.

**FINAL GOAL:** When I play the mod, I want to feel like something is watching me even when nothing is visible.

Herobrine should be mysterious, the monsters should have believable and distinctive behaviors, and the world should slowly become more terrifying.

Build a complete, atmospheric, polished Minecraft 1.7.10 horror experience with original, high-quality artwork, frightening sound design, intelligent horror progression, and memorable encounters.

**Prioritize actual quality and working gameplay over simply generating large amounts of code.**
