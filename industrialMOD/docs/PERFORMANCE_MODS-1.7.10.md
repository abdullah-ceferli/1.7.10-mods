# Forge 1.7.10 performance candidates

Checked against the original project pages on 2026-11-19. This is a research shortlist, not a benchmark of this HBM modpack. None of these recommendations has been installed automatically. Select the **Forge 1.7.10** file and the dependencies required by that exact release.

| Mod | Main purpose | Where to use it / notes |
| --- | --- | --- |
| [ArchaicFix](https://github.com/embeddedt/ArchaicFix) | Lighting improvements, stutter/memory fixes and optional culling | A candidate for client and server testing. Needs a compatible mixin loader; the author recommends UniMixins. Culling is optional. |
| [Hodgepodge](https://github.com/GTNewHorizons/Hodgepodge) | A broad collection of vanilla/mod fixes and selected optimizations | Requires UniMixins and GTNHLib. Review its configurable gameplay/UI tweaks as well as performance fixes. |
| [CoreTweaks, GTNH fork](https://github.com/GTNewHorizons/CoreTweaks) | Startup transformer caching and assorted fixes | Most useful to investigate slow launch times. Use the maintained fork and its release-specific mixin compatibility requirements. |
| [BugTorch](https://modrinth.com/mod/bugtorch) | Targeted vanilla/Forge/mod fixes, resource leaks and small optimizations | Forge 1.7.10 client/server. Features are configurable; check overlaps when combining patch mods. |
| [AI Improvements](https://modrinth.com/mod/ai-improvements) | Reduces some vanilla mob AI costs | Server/singleplayer, with a 1.7.10 build. The author explicitly says it does not help every case. Optional behavior-removal settings change mob behavior. |
| [Entity Culling 1.6.3 for 1.7.10](https://modrinth.com/mod/entityculling/version/1.6.3-1.7.10) | Avoids drawing hidden entities and block entities | Client only; this is an old legacy backport. Check HBM multiblock/special-renderer visibility and whitelist renderers if needed. |
| [FalseTweaks](https://github.com/FalsePattern/FalseTweaks) | Client rendering, textures and chunk rendering improvements | Requires FalsePatternLib and UniMixins. Current multithreaded chunk rendering requires Beddium. Read the selected release's renderer compatibility notes. |
| [Angelica](https://github.com/GTNewHorizons/Angelica) | A replacement client renderer, including threaded terrain rendering | An alternative renderer setup. Requires matching UniMixins/GTNHLib. It conflicts with OptiFine and FastCraft, and its author lists FalseTweaks as mostly incompatible. |

My first candidates to evaluate for this pack are ArchaicFix, Hodgepodge and CoreTweaks, checking one addition at a time in a copied instance. Add AI Improvements if profiling points to mob AI. That ordering is a recommendation based on their documented scope, not a measured result on this pack.

Treat Angelica and FalseTweaks as separate renderer choices. The installed OptiFine E7 stack and an Android/Zalith renderer need their own compatibility test before switching. Angelica's README also lists BetterFPS and OptimizationsAndTweaks as incompatible. Do not assemble every entry in this table into one stack.

For an honest comparison, use the same copied world, camera, render distance and machine count before/after; measure frame times for client rendering and server tick time for world simulation. A rendering mod does not fix an overloaded pipe network or an expensive world-generation task.
