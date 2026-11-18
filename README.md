# Minecraft 1.7.10 Mods & Backports

Factory automation, hands-on cooking, farming, 3D food displays, client tools and horror for **Minecraft 1.7.10**. Maintained by **Zahin**, with original upstream authors and credits preserved.

**Requires Java 8 and Minecraft Forge 10.13.4.1614.**

**[Download the build collection](https://github.com/abdullah-ceferli/1.7.10-mods/releases/tag/builds-2026-11-18)** · **[Browse individual JARs](Latest-Mods/README.md)** · **[Report a bug](https://github.com/abdullah-ceferli/1.7.10-mods/issues)**

## Pick a mod

| Mod | Current build | What it adds | Status | Download |
| --- | --- | --- | --- | --- |
| [Industrial Technology](<industrialMOD/README.md>) | 2.10.1 | Quarries, energy storage, railways, automation, HBM integration and bundled OpenComputers. | Built | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/IndustrialTechnology-1.7.10-2.10.1.jar) |
| [Hands-On Delight](<HandsOnDelight-1.7.10/README.md>) | 1.5.1-backport.4 | Stir cooking pots, turn food in skillets and use the cutting board through the original 3D station interface. | Built | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/handson-delight-1.7.10-1.5.1-backport.4.jar) |
| [Display Delight](<DisplayDelight-1.7.10/README.md>) | 1.7.0-backport.1 | Place 3D food displays and plates linked to supported food items. | Built | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/displaydelight-1.7.10-1.7.0-backport.1.jar) |
| [Croptopia](<Croptopia-1.7.10/README.md>) | 4.0.1-backport.1 | An experimental backport with 300 foods, 58 crops and 27 fruit-tree families. | Experimental | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/croptopia-1.7.10-4.0.1-backport.1.jar) |
| [Offline Skins](<industrialMOD/offline skin mod/README.md>) | 1.2.0 | Local skin selection, LAN sharing and optional accepted-friends sharing. | Built | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/OfflineSkins-1.7.10-1.2.0.jar) |
| [Personal Client Tools](<industrialMOD/personal client tools mod/README.md>) | 1.1.0 | Block Line Counter and saved control presets together in one client mod. | Built | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/PersonalClientTools-1.7.10-1.1.0.jar) |
| [True Horror](<industrialMOD/true horror mod/README.md>) | 1.0.0 | A horror director, Herobrine and six other creatures with gradual encounter progression. | Built | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/TrueHorror-1.7.10-1.0.0.jar) |
| [Block Line Counter](<industrialMOD/block line counter mod/README.md>) | 1.0.0 | Standalone straight-row block counting; an alternative to the combined client tools. | Standalone alternative | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/BlockLineCounter-1.7.10-1.0.0.jar) |
| [Control Presets](<industrialMOD/control presets mod/README.md>) | 1.0.0 | Standalone saved control layouts; an alternative to the combined client tools. | Standalone alternative | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/ControlPresets-1.7.10-1.0.0.jar) |

The collection is marked as a prerelease because it includes an experimental Croptopia backport. Its remaining compatibility work is listed on the project status page.

## Install

1. Set up Minecraft **1.7.10**, Forge **10.13.4.1614** and **Java 8**.
2. Download the mods you want and put their JARs in your instance's `mods` folder. You can choose individual mods; the collection is not a complete launcher instance.
3. Add the required dependencies from the release or [dependency folder](Latest-Mods/dependencies/). See the notes below before combining mods.
4. Install matching gameplay-mod versions on the client and server. Follow each project's README for client-only tools and optional server features.

### Dependencies and combinations

- **Hands-On Delight and Croptopia:** use the tested **Farmer's Delight Classic 1.1.1** build and **UniMixins 0.3.1**. Keep the leading `+` on the UniMixins filename. The tested Farmer's Delight JAR contains the addon catalog API these builds require.
- **Industrial Technology:** use **Baubles 1.0.1.10** for the accessories. HBM and the rest of your modpack are separate downloads. Industrial Technology already includes **OpenComputers 1.8.10**; keep a separate OpenComputers JAR out of the same instance.
- **Personal Client Tools:** already combines Block Line Counter and Control Presets. Use the two standalone alternatives instead if you prefer; choose one form to avoid duplicate features.
- **NEI recipe browsing:** the tested optional pair is **NotEnoughItems 2.3.54-GTNH** and **CodeChickenCore 1.4.10**.
- **Offline Skins Friends Service:** is an optional external companion program. Its JAR belongs outside Minecraft's `mods` folder.

## Current backport limits

- [Croptopia status](Croptopia-1.7.10/docs/STATUS.md): some modern-only ingredients and mob drops, the Patchouli guide and optional machine integrations are unavailable.

The food backports were checked on PC with the existing HBM/Hands-On Delight modpack in disposable worlds. That is not a guarantee for every mod combination. Android/Zalith validation is still outstanding.

## Help and bug reports

Check that you are using the current build, then [open an issue](https://github.com/abdullah-ceferli/1.7.10-mods/issues) with the mod version, Forge and Java versions, a short description of what happened, steps to reproduce it, and the relevant crash report or log. Do not include account tokens or personal server credentials.

## Credits and source

Original mods, code, models, textures and audio retain their original authors and licenses. Backport maintenance: **Zahin**. Each project has its own README and license/provenance files; the workspace has no blanket license covering every included project.

**Cultural Delights and Scape and Run: Parasites are excluded from the current public source tree and release downloads.** The maintainer has not contacted their original mod owners, and redistribution permission has not been confirmed. The development projects are kept locally; existing Git history is unchanged.

For development setup and local build prerequisites, see the project READMEs and [repository guide](GITHUB_UPLOAD.md).
