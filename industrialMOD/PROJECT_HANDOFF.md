# Industrial Technology handoff - 2.15.0

## Current update: embedded animated hatches (2.15.0)

Top-click on inert opaque full floor cubes insets the hatch at the same Y. Edge placement selects the nearest clicked seam (including negative X/Z); corner selects the nearest shared corner. TileSlidingHatch stores each part's original backing Block/metadata. HatchGeometry.floor subtracts the one-block opening from each host cell; static frame/floor rendering uses a read-only floor context so real Chisel icons/metadata remain intact. Legacy air parts accept paid floor infill. Ordinary held ItemBlocks on filled parts fall through to native adjacent placement. Player removal restores floors; external replacement returns the replaced cell's saved floor item while preserving the replacement. Never carve tile entities, bedrock or active/falling/random-ticking blocks.

Two lid panels animate using persisted/synchronized world-clock motionFrom/motionStart and open target. All variants share the 12-tick smooth transition; reversals are continuous. Lid TESR uses the native block atlas, one draw, and restores GL state. Only the anchor renders the whole lid; unloaded-anchor fallback clips locally without loading chunks. Frame/floor remains cached chunk geometry. Non-ticking tiles schedule only short transition safety checks. Living/collidable entities prevent/reverse closing. Old NBT defaults to empty backing and a settled endpoint.

Build the separate reobfuscated native harness with testModJar build -PproductionTest -PtestRunDir=.toolchain/embedded-hatch-tests/build-run. tools/verify_embedded_hatch.py --hbm runs server + fresh-process reload; --client --hbm verifies native click packets, intermediate animation, Chisel textures and UI with NEI. General test suite excludes industrialtech.embeddedHatchTest. Proof/screenshots are in docs/test-results/2.15.0. Keep delivery manual, then commit/push per user instruction; no GitHub release. All unrelated runtime entries, including colored lamps and bundled OpenComputers, must match 2.14.0 exactly.


## Current update: colored lamps (2.14.0)

See COLORED_LAMPS_GUIDE.md. lighting/ adds LampColors, BlockColoredLamp, ItemBlockColoredLamp and ColoredLampRegistry. Registration is appended after hatches; old green_redstone_lamp and its original class/recipe/assets are untouched. Two obtainable blocks each use all 16 metadata colors. The redstone ON state is a third block registered without an ItemBlock. All drops, Silk Touch and pick-block paths map ON back to the OFF item while retaining color. Color order matches native wool; dye damage is 15 minus the metadata.

Rendering stays in the native full-cube renderer with a quartz top icon and block/item color multipliers. No custom textures, GL state, TESR, tile entity, packet or continuously serviced tick list is introduced. Native block-light level 15 is uncolored. Redstone state swaps preserve metadata and schedule only a four-tick off check; a repowered lamp stays lit. Full solid faces retain attached levers through swaps.

tools/verify_colored_lamps.py derives the current build version. Build its separate reobfuscated test harness with testModJar build -PproductionTest -PtestRunDir=.toolchain/colored-lamp-tests/build-run. Run --hbm for native recipes/power/light/restart and --client for inventory/world/NEI checks. The general integration suite excludes industrialtech.colorLampTest. Native proof and screenshots are in docs/test-results/2.14.0. Keep delivery manual; Git commit/push follows the user's existing instruction, without creating a GitHub release.

## Current update: Sliding Hatch and liquid Building Stick (2.13.0)

See HATCH_GUIDE.md. hatch/ adds an appended registry identity, three ItemBlock variants and non-ticking description-synchronized parts. Geometry is clipped per native cell and statically rendered by ISBRH; no TESR, shaders or tick loop. Normal/edge/corner conversion uses a one-item shapeless recipe. Offset models remain one block wide and require empty cell footprints; they do not carve solid native blocks. Native ItemStack placement supplies Forge MultiPlaceEvent rollback. Group removal/interaction validates all loaded parts before mutation. The open state retracts horizontally without interpolated animation.

BuilderLiquids handles only water/lava buckets after the usual chunk margin and job validation. Payment happens after an accepted Forge event and returns an empty bucket. Native flowing blocks are scheduled normally; there is no separate fluid simulator. Sneak picking uses a stop-on-liquid native ray in both right-click paths. Keep the original budgets and block material rules unchanged.

tools/verify_hatch.py runs production server/restart checks. tools/verify_builder.py now uses 2.13.0 and the hatch-tests/build-run harness, retaining the existing builder controls and adding actual client hatch clicks/screenshots. The general test harness must exclude industrialtech.hatchTest. Test cancellation listeners are explicitly disabled before unregistering because Forge can retain inherited-event listener caches. No temporary diagnostic code belongs in production. Delivery remains manual; the user has separately authorized a final Git commit/push, without GitHub release creation.

## Current update: connected puller selection (2.12.1)

PullerSelection starts after a successful normal identifier click on a puller. It visits face-connected pullers whose selection is NONE or the chosen fluid, and changes only NONE. Different selections, suckers, protection and unloaded chunks stop traversal. HBM pipes are not rewritten. A captured item copy preserves the fluid choice; a new click replaces the same player's pending job. None clearing and nonempty-tank rejection retain their original single-block behavior.

The shared configuration queue has 128 cell visits/tick, 8,192 visited cells/job, 64 face steps and 16 jobs. FluidMachineScheduler services it and clears it on server-world unload. Every additional edit checks native permissions plus Forge RIGHT_CLICK_BLOCK cancellation and rechecks the tile after listeners. The production water/lava and no-HBM tests are run by tools/verify_fluid_machines.py; the separate test harness must be rebuilt/reobfuscated for 2.12.1. No game instance is automatically updated. TileFluidMachine.markDirty now persists metadata and chunk dirty state first, then only runs vanilla neighbour callbacks when their two-block chunk margin is already loaded. Native tests cover this identifier boundary path and fresh-JVM persistence.

## Current update: Building Stick (2.12.0)

See BUILDING_STICK_GUIDE.md. New item registration appends `building_stick`; packet 14 is BuildCommand. Server mutations stay in builder/BuilderService, with bounded network queue, geometry validation, inventory reservation/refund and real Forge placement. OnItemUseFirst must return false on the client so vanilla sends C08; client onItemUse claims the interaction, while server onItemUseFirst handles it before container activation. Do not restore a true client first-use return without also supplying a block-use packet.

The Alt-hold wheel and right-click-air touch wheel live in builder/client. Their original Effortless Building icon atlas is byte-identical to commit 5e38603322cf30b120283a06e796b36dc43c1509; the atlas and GUI integration have LGPL notices, with provenance and source included. Keep attribution, license and the corresponding source archive together when distributing. The placement engine remains separately implemented.

Shared scan/placement limits are 2048/64 per server tick, per-job limits 1024/32, 16 jobs, 4096 accepted blocks and 33 cells per axis. Preview emits at most 128 ghost cells. Blocks with tile state/NBT are excluded; placement-event guards cancel changed cells outside the intended target. No user-world testing or automatic game installation is authorized. Native tests use tools/verify_builder.py with --pack/--client and separate disposable worlds.

## Current update: water/lava machines (2.11.0)

See FLUID_MACHINES_GUIDE.md and docs/CHANGES-2.11.0.md. The two new blocks use real HBM MK2 standard provider/receiver interfaces and a single native tank backing saved amounts. No GUI; initial NONE; HBM multi-fluid identifier primary selection controls water/lava only. Full/nonempty buffers cannot be retuned. No energy requirement was requested.

FluidMachineScheduler shares 64 visits, 256 probes and 4 action attempts per server tick; each machine scans at most 32 cells per visit and attempts one source change per 20 ticks. Flood radius is 16 in each coordinate, deduplicated and incremental. Full buffers pause; empty searches retry after 100 ticks. No chunk tickets or forced loads. Owner-backed FakePlayer edits respect protection and Forge cancellation.

`tools/verify_fluid_machines.py` runs the reobfuscated production JAR plus a separate test-only mod in fresh disposable dedicated servers, then restarts each JVM to check saved contents. Use `--hbm` for actual HBM X5771 pipe/identifier checks or `--pack` for the installed server-compatible mod set (client-only mods excluded). Build the harness with `testModJar build -PproductionTest -PtestRunDir=.toolchain/fluid-tests/build-run` so it is reobfuscated. Reports include the production SHA-256. Android/Zalith and large installation benchmarks are not covered by the server checks.

The current delivery is local only. The user prefers to install manually; do not replace game JARs or publish GitHub assets without a new request.

## Current fix: wearable right-click crash (2.10.1)

Minecraft 1.7.10 ItemInWorldManager.tryUseItem dereferences the returned ItemStack before clearing an empty hand. ItemWearable must return the consumed, zero-count stack rather than null. Client-side prediction remains unchanged; vanilla sends the authoritative inventory update. Both amulet and flight ring share the fix. Do not restore Baublelicious to mask this bug. Native regression checks invoke the real vanilla manager, and graphical checks send real item-use packets in a disposable Survival LAN world with ProjectE present and Baublelicious absent.

The main @SidedProxy now declares modId=MODID. Forge explicitly requires this ownership selector for mixed Java/Scala mods in one JAR; it prevents OpenComputers' Scala adapter from trying to inject our Java proxy. Original OC classes/resources remain unchanged.

Release evidence: docs/test-results/2.10.1. Back up installed 2.10.0 outside mods, install only the sealed production 2.10.1 JAR after Minecraft is closed, preserve the current mod/config hashes, and never open or modify user worlds.


## Previous update: independent accessories / catalysts (2.10.0)

User requests original Baublelicious Flight Ring/Nightvision Amulet, real gold-ring Baubles equipment UI, and three pictured ProjectE tools. Exact color comparison identifies the pictured items as Destruction Catalyst, Hyperkinetic Lens, Catalytic Lens; do not add Life/Soul/Body stones. User explicitly wants independent copies and will remove ProjectE/Baublelicious later, but forbids us deleting them now. Their final corrections require alternative ordinary-material recipes for the three tools, no colored ProjectE fuels/matter chain, and coal-family-only runtime fuel including HBM Petroleum Coke; no trees/wood/other fuels.

accessories/ adds item registration after previous legacy parts, real IBauble wearables, passive effects, ItemCatalyst charge/actions, CoalFuel atomic inventory draw/change NBT, original adapted NovaExplosion, lens projectile and persisted collected loot entities. It does not import any original mod entry points. Standard Baubles is required for the original equipment slots/button and remains separately installed. ProjectE/Baublelicious are optional; after-order preserves original V/R key bindings when present. HBM coal identities come from optional cached reflection on actual ModItems fields, because registry names are not plain field names. Ore names are narrowly coal/coke only; furnace burnability alone never qualifies an item.

Packet 13 is AccessoryCommand, server-thread held-slot validation and a short action bound. Entity IDs 2/3 are new lens projectile/loot ball; rolling stock stays 1. Item IDs and existing classes are preserved except registration/init/client input/version. Original sprites/audio live under industrialtech/accessories; source/hashes in development-assets/accessories/original-assets.json. Existing ProjectE MC17 credits are retained, and original-source adaptation/asset credits are documented.

tools/prepare_accessory_development.py verifies vendor/baubles original API and translates globally unique SRG names in a development-only JAR, never bundled. compileJava prepares it; runtime development uses the real Baubles mod. Original ProjectE/Baublelicious development copies are test-only.

Tests: AccessoryServerChecks and AccessoryClientChecks, tools/verify-accessories-{production,client}.ps1; new worlds exclusively .toolchain/accessory-tests. Native checks cover no-original-mod loading, crafting, effects, coal rejection/credit, actual HBM coke, mining/break protection, original charge tables, loot partial pickup/NBT, entity-safe Nova blast, firing and actual JVM persistence. Client uses the original Baubles button/container packets, effect sync/removal, our command packets and all five NEI pages. Use a final production build after graphical tests. Never open/edit/delete installed saves; never remove original mods without a later explicit request. Keep first/last-three run-test folders untouched.

Guide: ACCESSORIES_GUIDE.md. Release evidence: docs/test-results/2.10.0. Deploy only the final tested combined IT JAR after verifying Minecraft is closed, backing up 2.9.0 outside mods, and checking all existing mod/config hashes.

## Previous update: full OpenComputers (2.9.0)

User explicitly requested complete original OpenComputers inside our mod, with no AI. Official MC1.7.10 1.8.10 commit 667626d is vendored with pinned hashes and original sources/notices under vendor/opencomputers. tools/bundle_opencomputers.py merges the original universal archive AFTER reobf, preserving original classes/resources unchanged and explicit parent directories for OC ZIP filesystems. Only manifest, mcmod.info and pack.mcmeta metadata are combined. Original OpenComputers mod ID/registry namespaces remain. CombinedCorePlugin supplies original OC ClassTransformer/CoreModContainer plus the unchanged battery transformer. FMLAT remains oc_at.cfg. Do not also install a standalone OC JAR.

FactoryComputerDriver is a passive read-only original Adapter driver for capacitor banks, physically valid Green monitors and consoles. It uses existing snapshots/cached channels, validates tile identity/loaded access and Green placer ownership, and exposes industrialtech methods getInfo/getEnergy/getTanks/getChannels. Server-thread callbacks, no tick hook. Large energy values have exact decimal strings alongside numeric fields. No wireless action/control methods.

Build tools/build.ps1; original OC development jar is stripped ONLY of its competing coreloader manifest via tools/prepare_oc_development.py. Client/server development use CombinedCorePlugin once. Production must be rebuilt after runClient. New disposable checks go ONLY in .toolchain/oc-tests (not more run-test folders). tools/verify-opencomputers-production.ps1 supports stock Reference, WithPack and Restart; tools/verify-opencomputers-client.ps1 exercises original OpenOS and original GUI/input. Reports and final package comparison are recorded in docs/test-results/2.9.0.

Guide: OPENCOMPUTERS_GUIDE.md. The user previously authorized pruning old run-test folders; retain exactly run-test and the last three 2.8.0 battery test folders. No user worlds may be opened, edited or deleted. Before deployment ensure Minecraft is closed, back up installed 2.8.0 outside mods, preserve existing configs/HBM JAR and install only the verified combined 2.9.0 runtime.

## Previous update: Green battery energy and row totals (2.8.0)

User wants a Green stick on a battery to show stored energy, incoming energy in green, outgoing energy in red, and a terminal button combining a straight touching row through the center battery. User confirmed the main target is HBM Energy Storage Blocks.

BatteryMonitor creates the Green endpoint's Energy snapshot, discovers the longest bounded X/Z/Y row, sums 64-bit amounts/capacities and real per-battery flow, marks partial scans, and never follows cables or loads chunks. WirelessEndpoint.BatteryRow is saved and defaults false on old NBT. WirelessCommand.ENERGY_ROW is 25 (reactor actions already occupy 16Р Р†Р вЂљРІР‚Сљ24); server validates endpoint owner/network, type, dimension, status and mode. GUI buttons 28/29 add Energy and row toggle without changing GUI IDs or packet IDs. Item/Tank tabs are retained. Existing capacitor network totals are counted once.

Exact HBM transfer rates cannot be obtained from HBM's net log or charge limits. energy/core/BatteryMeterPlugin registers a narrow, idempotent ASM transformer for HBM's TileEntityMachineBattery and TileEntityBatterySocket. Original calculations/writes/calls remain; observations are inserted afterward. HBM tick method is updateEntity in development and func_145845_h in production. This captures direct item-charging assignments as well as network setPower calls. MeteredBattery marker advertises installed hooks; optional classes are isolated. BatteryFlowMeter watches only viewed batteries with weak keys and 40-tick expiry. FlowWindow holds 21 buckets so the current tick cannot overwrite the oldest of the last 20 completed ticks. Unsupported flow APIs report unavailable rather than disguising net charge as separate I/O. Row totals include internal member-to-member transfers in both directions; see BATTERY_MONITOR_GUIDE.md.

Targeted checks: FlowWindowTest, BatteryServerChecks/BatteryHbmChecks, BatteryClientChecks; launchers tools/verify-battery-production.ps1 and tools/prepare-battery-client.ps1. Client runClient overwrites runtime output with a dev JAR: perform a final production build/reobf before release/install. No user worlds may be opened, edited, or deleted. Documentation: docs/CHANGES-2.8.0.md and BATTERY_MONITOR_GUIDE.md.

## Previous patch: clean restored-item display names (2.7.2)

User requested removing LEGACY from the names of the newly craftable items, not removing items. Client RestoredStorageNames runs after LanguageManager through the normal resource-reload listener list. It strips the literal parenthesized marker from exactly eight current translated item names and updates Forge's StringTranslate table using its public inject API. It preserves the selected language and reruns on resource/language reloads. Initial cross-domain language resources lost to HBM load order, so those attempted files were removed; no language assets are added in the final patch. Gameplay/integration code and all item/recipe IDs remain unchanged.

The existing LegacyStorageClientChecks logs each real displayed item name on the original NEI pages. No new gameplay tests are introduced for this text patch. Package comparison verifies unchanged gameplay code/assets and only the new client listener/proxy registration plus version. See docs/CHANGES-2.7.2.md and docs/TESTING.md.

## Previous patch: original legacy HBM storage recipes (2.7.1)

User requested the four Energy Storage Blocks made craftable through HBM config with their last original recipes, then selected exact crafting-grid restoration through our mod for necessary Spark parts and explicitly original NEI presentation. Historical source: ef2b20d357ce159f7c3861cfecf8169796023a83, parent of the battery rework. Original four block recipes use HBM's assembler JSON, with all installed template entries retained. No new NEI handler; HBM displays them natively.

HBM X5771 actually removed four prerequisite Spark cell items, including their icons. LegacyHbmRecipes/LegacyHbmSparkBridge restore those ItemBattery items after bags, with original capacities, translated names and original historical PNGs under industrialtech. HBM is conditionally linked. `compatibility.restoreLegacyHbmSparkCellRecipes` defaults false; when true, five original shaped ore grids create empty Spark battery and 6/25/100/1000 cells. Existing modern Spark recipe remains. Original `ItemBattery.getEmptyBattery` returns null for the current Spark base item type, so a common empty(Item) helper writes identical zero-charge NBT without assuming an obsolete class.

LegacyStorageServerChecks/HbmChecks generate exact JSON through the real HBM serializer, verify 617 untouched defaults plus four recipes, material resolution, actual assembler module crafting and HE/t, original grid outputs/empty NBT and capacities, then repeat after a genuine restart. HBM's own double progress can take 401 ticks for a 400-tick recipe; the config remains 400 and tests accept native rounding with actual per-tick HE. LegacyStorageClientChecks exercises all four native HBM assembler pages and five NEI shaped crafting pages. No-HBM native run checks enabled-option loading safely. Test fixtures use only fresh run-test-* worlds.

Setup/recipes/source references: LEGACY_HBM_STORAGE.md. Exact deployment files/history: config-updates/hbm-legacy-storage-20261030. The installed game update must back up the old mod JAR and config outside mods, check Minecraft is closed, and leave saves untouched. Final evidence/deployment manifest is recorded with docs/test-results/2.7.1. Rebuild production after client QA.

## Previous expansion: colored Storage Bags (2.7.0)

User asked for bags/colors/capacity like ProjectE and explicitly original ProjectE textures. bag/ package adds BagRegistry, ItemStorageBag, BagData, BagInventory and BagContainer; client/GuiStorageBag uses the unchanged legacy atlas and 255x230 layout. Sixteen metadata variants follow wool order; 104 storage slots, 36 player slots; GUI 8. Registry additions follow consoles, no custom packet. Existing machines and ProjectE contents are not modified.

BagData is overworld WorldSavedData keyed industrialtech_bags, mapping player UUID/color to canonical BagInventory objects. No inventory data lives on items, preventing stale item-copy overwrites. Same-color access shares one object; owner and color isolation remain across death/dimensions. BagContainer validates held slot/color, locks opening hotbar slot, guards number-key/throw/click/shift/drag paths and rejects nested Storage Bags. Standard container packets synchronize NBT stacks; no new tick handler. Recipes: four matching wool/four leather/Ender Chest, plus sixteen dye recipes. Recoloring changes bank selection, not its contents; anvil names do not transfer through crafting.

Seventeen BagServerChecks cover recipes, slots, isolation, click/drag/shift behavior, full/partial stacks, nesting, active-slot protection, death/dimensions, NBT and real JVM persistence. BagClientChecks uses actual item open and vanilla inventory packets, all-color icons and a fully populated 104-slot screenshot. Test runner tools/verify-bag-production.ps1 supports WithProjectE for optional coexistence. Native and client fixtures always use new run-test-* worlds. Rebuild/reobf after runClient before delivery. Current evidence: docs/TESTING.md; user setup: BAG_GUIDE.md.

Original 16 icons and alchchest.png are copied byte-for-byte from the user's installed ProjectE-1.7.10-PE1.10.1.jar into industrialtech paths. Hash/source mapping: development-assets/bags/projecte-assets.json. ProjectE MC17 README/credits included unchanged under META-INF/notices, with attribution in ASSET_CREDITS.md. No ProjectE code or inventory data is bundled/imported.

## Previous patch: monitor screen depth overlap (2.6.1)

User reported broken striped/flickering monitor displays facing left/right. ConsoleBlockRenderer formerly drew stacked full background/frame/glyph rectangles with tiny depth offsets. Replaced these with pure-Java ConsoleScreenMesh: original native palette painted into a 32x32 logical grid, greedily merged once into non-overlapping rectangles, rendered on one exact plane 1/64 block clear of the casing. Shared orientation math handles north/south/east/west with outward winding and upright pixels. Invalid metadata falls back safely. No texture changes, AI assets or continuous renderers. Only this client renderer/new mesh and runtime version change; all machinery/wireless code remains unchanged.

ConsoleScreenMeshTest checks complete single coverage, one exact plane/casing clearance, outward winding in all four directions, and bounded immutable cached meshes. ConsoleFacingClientChecks places both blocks through actual placement-facing logic and captures near/oblique native chunk rendering in all four directions with framebuffer accent visibility and GL checks. Build property consoleFacingClient selects it. Tests use disposable run-test-* worlds, never installed saves. See docs/TESTING.md and docs/CHANGES-2.6.1.md for the final release record.

## Previous expansion: tank and factory consoles (2.6.0)

Final recorded verification: 193 passing checks; production SHA256 `009cde7843eca31b7b1b1658e1114c78591dac5560b494752a15969b7610aef9`. Evidence: docs/test-results/2.6.0.



User requested a history block for linked Green sticks, a Refinery Controller block, battery/capacitor monitoring on Yellow sticks and the new monitor, and no AI textures. New console/ package, client GuiConsole/ConsoleBlockRenderer; see CONSOLE_GUIDE.md and docs/CHANGES-2.6.0.md. GUI 7, packets 11/12. Registry additions follow steel so existing IDs remain stable. New artwork is code geometry on vanilla iron/wool atlas entries; no image generation or new raster assets.

TrendSeries stores 720 long tick/value pairs, gaps -1 and net-rate estimates. ConsoleChannel persists these via paired int-array words for 1.7.10 NBT. History and controller have 16 channels. SensorReading shares MachineInspector for fluid and loaded HBM proxy resolution for genuine HE interfaces; bank snapshots keep full long RF network values. Yellow terminal Controls now includes passive energy telemetry. No energy transfer/conversion.

ConsoleService bounds work to eight console visits per server tick and indexes loaded blocks without chunk tickets. Controller release/refill hysteresis, manual override and exclusive Yellow claims prevent conflicting console rules; active existing Blue programs take precedence. Offline/disabled/mismatched fluid fails closed. Owner/network/dimension/range/window/rate checks guard command packets on ServerWork. Break/unload releases owned outputs. World saves retain history and settings; block items retain settings, omit history and restart paused.

New checks: ConsoleMathTest, ConsoleServerChecks, ConsoleHbmChecks, ConsoleClientChecks; tools/verify-console-production.ps1 uses only fresh run-test-* worlds. Never test in user saves. runClient can leave a development JAR: rebuild/reobf after visual QA before delivering. See docs/TESTING.md for final recorded evidence.

## Previous patch: two HBM upgrades, furnace UI and NEI (2.5.1)

User asked for max two HBM upgrades and the reference legacy blast-furnace GUI, replacing lava with an HBM-style HE bar, plus the factory recipe in NEI when looking up steel. SteelUpgradeProfile is pure bounded upgrade arithmetic. The actual HBM ItemMachineUpgrade type/tier is classified by the optional bridge. Slots 8/9 hold one item each, with Speed/Power/Effectiveness/Overdrive I-III and combined type cap III. Original inventory indexes remain for save compatibility; usable IDs are 0 iron, 2 dust, 4 output, 8/9 upgrades. IDs 1/3/5/6/7 are retired overflow from old saves only, noninsertable and absent from GUI/pipes. drainLegacy moves their contents into the visible inputs/output before processing, preserving old materials. Container GUI indices 0..4 map through SteelContainer.INVENTORY; player inventory starts at 5. Recipe profile locks on first paid tick and is saved as BatchTicks/BatchHE/BatchCoal; legacy NBT uses the base profile for unfinished work. HE intake is 20k/t to sustain all upgrade combinations.

Latest user correction removes buffers and the connector beside the upgrades. GuiSteelFactory and SteelGuiArt compose unmodified HBM atlas regions in a compact 204x196 screen: coal above iron, flame to their left, progress arrow and large output, electrical bar at the far right, with two upgrade slots below it separated by small gaps. Flame and progress overlays exactly share base sprite positions. Framebuffer checks cover zero/half/full arrow and prohibit drawing beyond its base. Packet IDs unchanged; bounded HE, progress, duration, HE/t and coal requirement use standard container progress words. NEI discovers integration/nei/NEIIndustrialTechnologyConfig; BulkSteelRecipeHandler registers both recipe and usage lookup. NEI dependencies are compileOnly, with no explicit reference from ordinary client/server startup. See STEEL_FACTORY_GUIDE.md and docs/TESTING.md.

Current 2.5.1 validation: 160 passing recorded checks; production SHA256 `60cc4bf2092e062fdf1210bf5ddf1cd50ba36463e9df723c5f355c07d13d704b`. Evidence and final UI snapshots: docs/test-results/2.5.1. Final layout: upgrade items at (174,76) and (174,96), energy at (174,18), screen 204x196. Arrow uses a code mask of the original gray arrow spans so its white fill cannot spill beyond the shape; sprite rendering resets color after slot borders.

## Previous expansion (2.5.0)

## Current update: bulk HBM steel and bank colors (2.5.0)

The user clarified bank input green, output red, full charge gold; no portable battery. Added steel/ package and GuiSteelFactory, GUI ID 6. Optional HBM bridge/subclass implement the installed X5771 HE API, compileOnly from .toolchain/hbm-api/HBM-X5771-dev.jar. Registry additions come after RailwayRegistry; existing IDs stay stable. Factory runs 16 iron + 4 coal dust -> 16 genuine HBM steel / 100 ticks / 500 HE per working tick, 1M HE capacity. All six faces accept validated ingredients and extract outputs only; AE2 real InventoryAdaptor and real Thermal servo/itemduct routes are covered. Save and real JVM restart preserve paid partial work. Expensive core and factory recipes are in STEEL_FACTORY_GUIDE.md.

Latest visual request: use HBM Nuclear Blast Furnace (LEGACY). BlockSteelFactory resolves its five original texture names in the installed HBM atlas; no copied HBM assets. Metadata low 3 bits are facing and bit 8 is actual working state; changes never replace the tile. GuiSteelFactory uses standard ICrafting progress words for its bounded HE buffer and normal inventory packets. Capacitor side-mode colors/glyphs and full-charge display changed; original casing textures stay byte-identical. BankManager reserves meter level 16 for exact full capacity. Other bank ledger/transfer logic is unchanged.

No user worlds were edited. Executed evidence and distribution identity: docs/TESTING.md and docs/test-results/2.5.0. Do not ship the dev JAR left by runClient; build/reobf after client QA.

Historical 2.5.0 validation: 161 passing recorded checks; SHA256 `feada01d7d32489a2608617dd73f30deabd386c9aa8e6b9d630641beb69d761c`. Exact production bytes passed the HBM/AE2/Thermal server, standalone server and bank regression worlds. Screenshots and machine-readable release identity are archived in docs/test-results/2.5.0.

## Previous patch (2.4.2)

## Current patch: capacitor item stacking (2.4.2)

Empty bank items stack to 64, charged items stay at one. Default empty drop NBT is normalized to match crafted banks; player inventory ticks normalize legacy default-empty items. Configured empty stack placement restores its saved settings. Charged restoration still requires a single item. Item settings revision, non-default settings and foreign/custom-name tags are preserved. No bank ledger/network changes. See docs/CHANGES-2.4.2.md and docs/TESTING.md.

Historical 2.4.2 validation: 128 passing checks; SHA256 `bc3b34c176801c32285ba54fc52de47ded1a8cedaa95417aa8bd8e7dafe58fab`. See docs/TESTING.md and docs/test-results/2.4.2.

## Previous patch: solar panels have no UI (2.4.1)

Removed panel activation and the server/client GUI ID 3 routes. ID 3 remains reserved; railway GUIs 4/5 retain their IDs. Removed the solar presentation/container mode. Solar generation, RF, array caching, textures, recipes and saved tile data are unchanged. Current guide: SOLAR_AND_RAILWAY_GUIDE.md. Previous 2.4.0 evidence remains archived; current patch verification is recorded in docs/TESTING.md.

Actual checkout: `D:\IndustrialTechnology\industrialMOD` (the configured `D:\industrialMOD` path is stale). Minecraft 1.7.10 / Forge 10.13.4.1614 / Java 8. Mod ID remains `industrialtech`.

Current patch validation: 144 passing checks. Production SHA256 `1851a14260605de1a0fa2980e1d7f12e024e8d15791220f65bcb1f91e0592721`. See docs/test-results/2.4.1 and docs/TESTING.md.

## Current expansion: solar and railway (2.4.0)

See SOLAR_AND_RAILWAY_GUIDE.md, ASSET_CREDITS.md and docs/CHANGES-2.4.0.md. New packages solar/, railway/, expansion/ plus legacy client renderers; registry additions come after the bank. Common GUI IDs 3/4/5 and packet IDs 9/10. SolarArrays caches loaded horizontal components and reuses the existing unchanged BankLedger for physical RF ownership/reservations. Trains use measured TrackWorld cursors, original OBJ meshes, server physics, logical counted cargo, UUID coupling, loaded-entity indexing, entity-bound Forge tickets and station adapters. RailwayTickets is handled before existing machine tickets in the shared callback. Tools/create_railway_assets.py preserves reproducible model sources. Disposable native/client tests are RailServerChecks and RailClientChecks; never use installed saves as fixtures. Final executed evidence is recorded in docs/TESTING.md.

Final 2.4.0 validation: 533 recorded passing checks (101 unit, 415 native, 17 client), archived in docs/test-results/2.4.0. Production SHA256 `8b50576a3f61e0e34e3e0d8e36a71a4b6e5f26ff84df5cc8472bca8b5b86d650`. The native matrix runs exactly these bytes; see docs/TESTING.md for coverage and its limits.

## Previous patch: capacitor default appearance (2.3.1)

The user correctly identified 2.3.0's permanent front as the optional display. The exterior atlas
was also incorrectly stretched as if its four alternative strips were one face texture. Defaults now
show the normal plain casing, including old 2.3.0 saves. Sneak + right-click with an empty hand toggles
the clicked Both face's meter. `DisplayFaces` is a local six-bit NBT field retained in item drops and
visual packets. No ledger, network or RF logic is changed. The meter now samples the original SINGLE
atlas quarter at 0.25 width and 0.8 height. Inventory rendering stays plain; hidden meters suppress
unnecessary charge redraw packets. See `docs/CHANGES-2.3.1.md` and current test evidence.

Historical 2.3.1 validation: 114 successful recorded checks (78 unit, 20 native initial,
1 genuine JVM restart, 15 Ender IO/NEI client). The exact distribution is sealed in
`.toolchain/capacitor-release-2.3.1/`; its only payload differences from 2.3.0 are three capacitor
classes and version metadata. All texture bytes and RF/network/other-machine classes remain identical.
The comparison screenshots show our banks on the left and actual Ender IO banks on the right.

## Previous update: Advanced Capacitor Bank (2.3.0)

The latest task adds one block `industrialtech:advanced_capacitor_bank` after all existing registrations.
Each bank contributes 25M RF and shared 25k RF/t input/output. `capacitor/BankLedger` keeps per-block
balances and same-tick quota ownership; `BankManager` caches loaded six-face components, RF receivers
and redstone counts. No chunk forcing or per-tick graph searches. `LoadedBankAccess` prevents vanilla
redstone queries from incidentally loading a neighbor chunk. Harvested items retain only their own RF.
The GUI and server-authoritative commands use GUI ID 2 and packet IDs 7/8; existing IDs stay stable.

The user explicitly changed the artwork request on Oct 24: use the exact Ender IO Vibrant textures for
personal play. Seven PNGs are copied byte-for-byte from their installed 2.3.0.430_beta JAR. Original
LICENSE, NOTICE and attribution are bundled. `client/CapacitorBankRenderer` composes them in cached
chunk meshes with connected borders and the original cyan gauge, with no TileEntity renderer.
The earlier authored artwork is archived in `.toolchain/authored-capacitor-before-EnderIO.zip`.
`tools/create_capacitor_assets.py` now regenerates only the independent GUI atlas.

Setup, recipe, config, split/merge settings policy and honest limitations: `docs/CAPACITOR_BANK.md`.
Prior 2.3.0 release: all 78 unit tests and the archived native/client suites pass. Four exact-JAR native configurations each pass an actual JVM restart. Current counts and tested limits are in `docs/TESTING.md`; the audit checks original-bytecode preservation and exact texture provenance.

Tests: `BankLedgerTest`, `BankServerChecks`, `BankPipeChecks`, `BankClientChecks`; the harness is
separate from the distribution JAR. Native runner: `tools/verify-capacitor-production.ps1`.
Never install a dev JAR: runClient rewrites build/libs; rebuild with `-PproductionTest` after client QA.
Pre-update backup: `.toolchain/pre-capacitor-2.2.1/`. Installed game and user worlds are not modified.

## Retained removal/migration context

The user requested removal of the entire space expansion, then explicitly asked to **keep the green block and allow a lever on it**. The retained block is the Green Redstone Lamp. Its old registry name and four-block crafting recipe remain. Version 2.2.1 replaces its decorative textures with plain solid green faces: darker green off, bright green on. Regenerate these two flat tiles with `tools/create_lamp_assets.py`. Its six faces now support vanilla levers and torches even though its material is glass. Redstone controls actual light 0/15.

Space Java/client code, providers, dimensions, machines, suits, rockets, launchpads, minerals, recipes, fluids, artwork and live instructions are removed. The exact pre-space edits in eight original classes were restored from the verified 1.6.3 baseline; all other original quarry/wireless/reactor code remains. Space-only chunk-loading suspension was removed. The compile-only HBM development dependency is no longer needed; optional reactor integration remains.

`core/ContentMigration.java` accepts only the exact retired registry names, allowing a previous 2.x Overworld to load without the removed-content prompt. Existing green lamps survive. `core/RetiredDimensionRecovery.java` corrects old Moon/Mars player NBT before the login packet, preserving inventory/XP. It reads existing legacy configured IDs without creating or saving a space config; registered dimensions are left alone. Removed space contents disappear when the user opens and saves a world; old world/dimension/config files are not deleted. See `docs/CHANGES-2.2.0.md`.

Previous 2.1.0 source/docs and JARs were backed up before deletion in `.toolchain/pre-space-removal-2.1.0/`. Earlier test worlds and versioned evidence remain as history. Never delete or use the human user's actual Minecraft worlds as fixtures. No automatic installation into the user's game is part of this update.

Build: `tools/build.ps1`; distribution: `build/libs/IndustrialTechnology-1.7.10-2.3.1.jar`. Current validation is in `docs/TESTING.md` and `docs/test-results/2.3.1/`. The original automation reference is `AUTOMATION_CODES.md`; reactor instructions remain in `docs/REACTORS.md`.
