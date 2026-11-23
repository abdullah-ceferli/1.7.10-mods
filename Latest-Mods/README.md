# Latest builds — Minecraft 1.7.10

**[Download from GitHub Releases](https://github.com/abdullah-ceferli/1.7.10-mods/releases/tag/builds-2026-11-18)**. Choose individual JARs or use the collection ZIP. Experimental builds are identified below.

The local collection includes newer Industrial Technology, Croptopia and Hands-On Delight builds than the published 2026-11-18 release. Use the local collection links below for those versions.

These are the latest existing built artifacts from the workspace. SHA-256 hashes and original build paths are recorded in [manifest.json](manifest.json) and [SHA256SUMS.txt](SHA256SUMS.txt).

| Mod | Version | Status | Download | Local collection path |
| --- | --- | --- | --- | --- |
| Croptopia | 4.0.1-backport.2 | initial test build | [Download JAR](mods/croptopia-1.7.10-4.0.1-backport.2.jar) | [croptopia-1.7.10-4.0.1-backport.2.jar](<mods/croptopia-1.7.10-4.0.1-backport.2.jar>) |
| Display Delight | 1.7.0-backport.1 | built backport | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/displaydelight-1.7.10-1.7.0-backport.1.jar) | [displaydelight-1.7.10-1.7.0-backport.1.jar](<mods/displaydelight-1.7.10-1.7.0-backport.1.jar>) |
| Hands-On Delight | 1.5.1-backport.7 | built backport | [Download JAR](mods/handson-delight-1.7.10-1.5.1-backport.7.jar) | [handson-delight-1.7.10-1.5.1-backport.7.jar](<mods/handson-delight-1.7.10-1.5.1-backport.7.jar>) |
| Industrial Technology | 2.15.0 | built; embedded hatches and animations verified | [Download JAR](mods/IndustrialTechnology-1.7.10-2.15.0.jar) | [IndustrialTechnology-1.7.10-2.15.0.jar](<mods/IndustrialTechnology-1.7.10-2.15.0.jar>) |
| Mobile Performance | 1.0.0 | client only; Android test build | [Download JAR](mods/MobilePerformance-1.7.10-1.0.0.jar) | [MobilePerformance-1.7.10-1.0.0.jar](<mods/MobilePerformance-1.7.10-1.0.0.jar>) |
| Block Line Counter | 1.0.0 | standalone alternative | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/BlockLineCounter-1.7.10-1.0.0.jar) | [BlockLineCounter-1.7.10-1.0.0.jar](<alternatives/BlockLineCounter-1.7.10-1.0.0.jar>) |
| Control Presets | 1.0.0 | standalone alternative | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/ControlPresets-1.7.10-1.0.0.jar) | [ControlPresets-1.7.10-1.0.0.jar](<alternatives/ControlPresets-1.7.10-1.0.0.jar>) |
| Offline Skins | 1.2.0 | built | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/OfflineSkins-1.7.10-1.2.0.jar) | [OfflineSkins-1.7.10-1.2.0.jar](<mods/OfflineSkins-1.7.10-1.2.0.jar>) |
| Personal Client Tools | 1.1.0 | combined client tools | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/PersonalClientTools-1.7.10-1.1.0.jar) | [PersonalClientTools-1.7.10-1.1.0.jar](<mods/PersonalClientTools-1.7.10-1.1.0.jar>) |
| True Horror | 1.0.0 | built | [Download JAR](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/TrueHorror-1.7.10-1.0.0.jar) | [TrueHorror-1.7.10-1.0.0.jar](<mods/TrueHorror-1.7.10-1.0.0.jar>) |

## Required dependencies and optional tools

| File | Purpose | Download |
| --- | --- | --- |
| farmersdelight-1.1.1-mc1.7.10.jar | dependencies | [Download](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/farmersdelight-1.1.1-mc1.7.10.jar) |
| +unimixins-all-1.7.10-0.3.1.jar | dependencies | [Download](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/%2Bunimixins-all-1.7.10-0.3.1.jar) |
| Baubles-1.7.10-1.0.1.10.jar | dependencies | [Download](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/Baubles-1.7.10-1.0.1.10.jar) |
| codechickencore-1.4.10.jar | optional-nei | [Download](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/codechickencore-1.4.10.jar) |
| notenoughitems-2.3.54-gtnh.jar | optional-nei | [Download](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/notenoughitems-2.3.54-gtnh.jar) |
| OfflineSkins-Friends-Service-1.2.0.jar | companion tool; not a Minecraft mod | [Download](https://github.com/abdullah-ceferli/1.7.10-mods/releases/download/builds-2026-11-18/OfflineSkins-Friends-Service-1.2.0.jar) |

## Installation notes

Industrial Technology 2.15.0 includes the original Effortless Building menu icons and LGPL notices. Its [corresponding source archive](sources/IndustrialTechnology-1.7.10-2.15.0-sources.jar) is included; keep it with the binary when redistributing. See the [Building Stick guide](../industrialMOD/BUILDING_STICK_GUIDE.md) for controls and limits.

- **Mobile Performance is client-only.** Its optional phone preset lowers visual settings and can be restored. See [the guide](../MobilePerformance-1.7.10/README.md). Android FPS gains have not been measured.

- `mods/` contains the primary mod JARs. See each mod's README for client/server installation.
- `alternatives/` contains standalone Block Line Counter and Control Presets. Use these instead of Personal Client Tools.
- `dependencies/` contains the tested Farmer's Delight Classic, UniMixins and Baubles versions. Keep UniMixins' leading `+`.
- `optional-nei/` contains the tested NEI/CodeChickenCore pair.
- `tools/offline-skins-friends-service/` is a companion program, not a Minecraft mod.
- Industrial Technology includes OpenComputers 1.8.10. Do not also install another OpenComputers JAR.

Croptopia remains an experimental backport. Cultural Delights and SRP are excluded from the current public source tree and release downloads. HBM itself and the rest of a player's modpack are separate downloads. Original licenses and third-party notices remain applicable.

New in these builds: [Sliding Hatch and liquid building](../industrialMOD/HATCH_GUIDE.md), plus [knife recipes, sushi/roll ingredients and sheep mutton](../HandsOnDelight-1.7.10/docs/FOOD_COMPATIBILITY.md). Both client and server need matching versions.

Industrial Technology 2.15.0 also includes 16 always-on and 16 redstone-colored lamp variants. [Lamp recipes and controls](../industrialMOD/COLORED_LAMPS_GUIDE.md).

Industrial Technology 2.15.0 insets hatches across floor joins while retaining surrounding material, with smooth opening and closing on all variants. [Placement and animation guide](../industrialMOD/HATCH_GUIDE.md).
