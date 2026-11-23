# Industrial Technology 2.12.1

One normal HBM Fluid Identifier right-click now selects Water or Lava for face-connected unconfigured Pullers. The clicked puller retains its existing direct selection rules. Matching selected pullers can bridge the group; other selected fluids, suckers, diagonal gaps and protected blocks stop it. Existing buffers are preserved. None clearing remains local.

Configuration runs on the server with 128 cell checks per tick, 64 face steps, 8,192 checked/frontier cells per batch and 16 pending batches. Each player keeps only their latest pending batch. The scan never loads a chunk, checks permissions and Forge interaction cancellation for additional edits, and clears pending work on world unload. HBM pipes and the existing source collection/output logic retain their behavior.

Install the matching version on client and server. The separate test harness is not part of the release JAR.

Tile persistence now retains its save/dirty state at chunk edges while suppressing vanilla neighbour notifications when their two-block margin is unloaded. Loaded areas retain the normal notifications. This prevents identifier configuration from indirectly loading a neighbour chunk.

Validation: 141 JUnit tests, 25 native HBM scenarios, 3 standalone scenarios, and a fresh-JVM save/reload check in both modes passed. Native reports and tested JAR hashes are in `docs/test-results/2.12.1`. The server tests use disposable worlds.
