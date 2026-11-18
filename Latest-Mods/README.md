# Latest built mods — Minecraft 1.7.10

Latest existing production artifacts collected from this workspace. Older versions, source JARs and test harnesses are omitted. SHA-256 hashes and original build paths are in manifest.json and SHA256SUMS.txt. Collection does not mean every mod is finished or that every combination has been tested.

| Mod | Version | File | Status |
| --- | --- | --- | --- |
| Croptopia | 4.0.1-backport.1 | `mods/croptopia-1.7.10-4.0.1-backport.1.jar` | initial test build |
| Cultural Delights | 0.16.7-backport.1 | `mods/culturaldelights-1.7.10-0.16.7-backport.1.jar` | initial test build |
| Display Delight | 1.7.0-backport.1 | `mods/displaydelight-1.7.10-1.7.0-backport.1.jar` | built backport |
| Hands-On Delight | 1.5.1-backport.4 | `mods/handson-delight-1.7.10-1.5.1-backport.4.jar` | built backport |
| Industrial Technology | 2.10.1 | `mods/IndustrialTechnology-1.7.10-2.10.1.jar` | built |
| Block Line Counter | 1.0.0 | `alternatives/BlockLineCounter-1.7.10-1.0.0.jar` | standalone alternative |
| Control Presets | 1.0.0 | `alternatives/ControlPresets-1.7.10-1.0.0.jar` | standalone alternative |
| Offline Skins | 1.2.0 | `mods/OfflineSkins-1.7.10-1.2.0.jar` | built |
| Personal Client Tools | 1.1.0 | `mods/PersonalClientTools-1.7.10-1.1.0.jar` | combined client tools |
| True Horror | 1.0.0 | `mods/TrueHorror-1.7.10-1.0.0.jar` | built |

## Choosing files

- `mods/`: latest builds of the main mods and the combined Personal Client Tools mod.
- `alternatives/`: standalone Block Line Counter and Control Presets. Use these instead of Personal Client Tools; do not install both forms together.
- `dependencies/`: tested Farmer's Delight Classic, UniMixins and Baubles. Keep UniMixins' leading `+`. The food backports need this exact tested Farmer's Delight addon API build. Industrial Technology uses Baubles for its accessories.
- `optional-nei/`: the tested NEI and CodeChickenCore versions, for recipe browsing.
- `tools/offline-skins-friends-service/`: optional external companion service; do not put its JAR into Minecraft's mods folder.

Industrial Technology 2.10.1 already embeds OpenComputers 1.8.10. Do not add another OpenComputers JAR beside it. Offline Skins and Personal Client Tools have their own installation instructions; client-only tools do not belong on a dedicated server.

Croptopia and Cultural Delights are initial tested backports with remaining compatibility work. See their project docs/STATUS.md. Scape and Run: Parasites is still unfinished, so there is no SRP JAR in this collection. Existing HBM and the rest of a player's modpack are separate dependencies; this folder is not a complete launcher instance.

This local collection preserves upstream licenses and does not grant new public redistribution rights. No files were installed into the live game.
