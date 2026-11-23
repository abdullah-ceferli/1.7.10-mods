# 2.11.0 — water/lava fluid machines

- Add Sucker and Puller with normal crafting recipes and native HBM fluid pipe integration.
- Require right-click configuration using HBM's Fluid Identifier; new machines start inactive and neither block opens a GUI.
- Collect face-connected water/lava source blocks within a 16-block coordinate radius, or place one source at the outlet. Preserve partial buffers in saves and dropped machine items.
- Share fixed scan/action limits across the server, stop scanning full buffers, and avoid chunk forcing. Respect spawn protection and Forge break/place cancellation.
- Use the real HBM tank as the saved buffer so native container/tank edits and pipe transfers share the same contents. Restrict selection to water/lava and reject retuning nonempty machines.
- Add bounded-frontier unit checks and disposable native Forge/HBM checks covering real identifiers, water/lava pipe routes, conservation, protection, chunk boundaries and JVM reloads.

See [usage and work limits](../FLUID_MACHINES_GUIDE.md), [performance mod research](PERFORMANCE_MODS-1.7.10.md) and the reports in `test-results/2.11.0/`. No user world is opened by the verification runner.

Complete OpenComputers 1.8.10 remains bundled, with original runtime entries preserved. Existing accessory fixes remain included.
