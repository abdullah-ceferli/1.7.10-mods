# GitHub source package: Croptopia

Maintainer: Zahin. Status: initial test build.

Preserve the existing LICENSE files, asset credits and third-party notices. The project license does not automatically relicense bundled or reused third-party material.

Source and asset credits stay with their original authors. See the project's README and existing license/provenance files.

## Upload

Use the existing workspace root as the GitHub repository. This mod stays in its current project folder; a separate exported repository is not needed. Keep the existing root Git history and review changes before publishing.

Ignore rules cover local caches, toolchains, test worlds and dependency binaries. Already tracked files remain tracked; ignore rules do not remove them from the index or history. Preserve all license and credit files.

## Build prerequisites

Use Java 8 and Forge 10.13.4.1614. Projects with Gradle retain their wrapper and build scripts. Native Python build tools read `.toolchain/toolchain.json`; start from `tools/toolchain.example.json` when supplied and enter your own Java/Forge/cache paths. Local SDKs and dependency binaries are intentionally not uploaded. Restore exact dependency versions from the existing README, vendor notices and docs manifests. Dependency restoration may require the original upstream download; this source packaging does not claim an unattended clean-room build.

Production JARs are collected separately in the workspace's Latest-Mods folder. This packaging step does not rebuild or change gameplay code.
