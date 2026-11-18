# Scape and Run: Parasites — Minecraft 1.7.10

A port of Scape and Run: Parasites 1.10.9 alpha to Minecraft 1.7.10 and Forge 10.13.4.1614. The aim is to bring SRP to modpacks alongside HBM and other 1.7.10 mods. Compatibility with a complete HBM modpack has not been tested.

Original mod by Dhanantry. **1.7.10 port by Zahin.** Original sound credits are retained in the mod metadata.

The port is in development and does not yet build or run as a complete mod. Verified components and remaining work are documented in [PORT_STATUS.md](docs/PORT_STATUS.md). [ALL_REMAINING_ERRORS.md](docs/ALL_REMAINING_ERRORS.md) contains the complete printed diagnostics from the latest compiler pass. Missing APIs can cause cascading errors and conceal later defects.

Editable source is under src/main/java. The original input and its published hashes are retained under upstream; the recovered reference source is kept separately under .toolchain/reference-1.12.2. All 1,094 original source units and 4,501 original assets are preserved.

Use tools/build.ps1 for the Java 8 Forge build, or tools/compile_probe.py to inspect compatibility errors against the local Forge API. Local toolchain paths are configured in .toolchain/toolchain.json.

Original code, art and audio belong to their respective upstream authors. The original project lists All Rights Reserved. No playable release has been published or installed from this development workspace.
