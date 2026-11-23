# External tools and APIs

The runtime JAR contains only this project's original classes, metadata and license. It does not redistribute Angelica, Beddium, Minecraft, Forge, LWJGL or ASM classes.

Minecraft Forge, FML, LaunchWrapper and LWJGL supply the runtime modding and graphics APIs. ASM supplied by Forge is used to attach hooks to the original Minecraft pipeline. Minecraft bytecode used in verification is read from the user's Forge development cache and is not shipped in the source or release archive.

The unmodified Gradle wrapper is a build tool from Gradle 7.4.2, licensed under Apache License 2.0. Its notice is retained in the wrapper scripts and its license is included under `gradle/wrapper/LICENSE`.

Native graphics tests use the official Zalith Launcher 1 and 2 Java libraries and the corresponding LWJGL/GLFW Windows native libraries. Their upstream licenses remain applicable. These test inputs are downloaded or cached separately; they are not included in the runtime JAR or tracked release artifacts. URLs and SHA-256 hashes are recorded in `test-dependencies.lock.json`.

This project is not affiliated with Mojang, Minecraft Forge, either Zalith project, Angelica or Beddium.
