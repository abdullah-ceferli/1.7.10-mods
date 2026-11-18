# Minecraft 1.7.10 build collection — 18 November 2026

Java 8 / Forge 10.13.4.1614. This collection is a **prerelease** because it includes an experimental Croptopia backport.

## Included builds

| Mod | Version | Status |
| --- | --- | --- |
| Croptopia | 4.0.1-backport.1 | initial test build |
| Display Delight | 1.7.0-backport.1 | built backport |
| Hands-On Delight | 1.5.1-backport.4 | built backport |
| Industrial Technology | 2.10.1 | built |
| Block Line Counter | 1.0.0 | standalone alternative |
| Control Presets | 1.0.0 | standalone alternative |
| Offline Skins | 1.2.0 | built |
| Personal Client Tools | 1.1.0 | combined client tools |
| True Horror | 1.0.0 | built |

The collection ZIP keeps primary mods, standalone alternatives, dependencies, optional NEI and the Offline Skins companion tool in separate folders. Choose the files you want; do not copy every folder into Minecraft's mods directory.

## Important combinations

- Use the provided Farmer's Delight Classic build and UniMixins for the food/cooking backports. Keep UniMixins' leading `+`.
- Industrial Technology needs Baubles for accessories and already bundles OpenComputers 1.8.10. Do not add a second OpenComputers JAR.
- Personal Client Tools combines Block Line Counter and Control Presets; the standalone alternatives replace it.
- The Offline Skins Friends Service is an external optional program, not a mod JAR.
- HBM itself is not included. Install the separate HBM mod when you want its integrations.

## Experimental and unfinished work

Croptopia has remaining modern-ingredient, mob-drop, guide and optional-integration gaps. Check its source status page before playing an important world. PC checks with HBM passed; Android/Zalith has not been validated.

SRP has no playable JAR and is not included.

Cultural Delights and SRP are excluded from the current public source tree and release downloads. The maintainer has not contacted their original mod owners, and redistribution permission has not been confirmed. The development projects are kept locally; existing Git history is unchanged.

Original authors, licenses and third-party notices are retained. Backport maintenance: Zahin.
