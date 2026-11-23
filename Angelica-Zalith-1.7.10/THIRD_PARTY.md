# License and provenance

Angelica is developed by the GTNewHorizons / Angelica contributors. This unofficial edition adds compatibility changes maintained by Zahin. It is not endorsed by Angelica or either Zalith project.

The original `LICENSE`, `META-INF/LICENSE.txt`, `META-INF/NOTICE.txt` and all other upstream binary entries are retained. The complete upstream source project and its notices are in the corresponding source ZIP. New compatibility code and build tools are provided under GNU LGPL version 3, matching the original project's `LICENSE`. Keep the source ZIP and notices available when distributing this modified build.

## Pinned inputs

- Angelica release: `2.2.46`, upstream commit `996fb54`.
- Original runtime SHA-256: `4561602f0594ec84dd76fcfe7606d2ea47bb12fffd7f02a9a411ecd187b06f7a`, verified against the official release asset digest.
- Zalith Launcher 1 test libraries: revision `ed8a5c873b25f19d08aef5bdfc1084fd9aff836c`, LWJGL 3.3.6.
- Zalith Launcher 2 test libraries: release `2.8.2`, revision `dbf5a414504cdeb584d9e8fb437078b77b512ba4`, LWJGL 3.3.3.
- ASM 9.5 and ASM Tree 9.5: BSD-3-Clause; build-time tools, not added to the runtime JAR.
- LWJGL / GLFW Windows native test dependencies: original upstream licenses; used only for local tests, not added to the runtime JAR.

Dependency URLs and full SHA-256 hashes are in `dependencies.lock.json`. The full-source archive retains upstream build infrastructure and the original authors' credit list. Separate components bundled by upstream retain their individual licenses.
