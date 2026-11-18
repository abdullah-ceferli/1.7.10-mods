# GitHub repository

Use this workspace folder as the repository root. Public mod projects stay in their existing folders. Keep the existing `.git` directory when renaming the workspace or adding it to GitHub Desktop.

## Public builds

The current public builds, dependencies, alternatives and checksums are listed in [Latest-Mods](Latest-Mods/README.md). Download them from [GitHub Releases](https://github.com/abdullah-ceferli/1.7.10-mods/releases). Croptopia is an experimental backport; read its status page before playing.

## Local-only projects

`CulturalDelights-1.7.10/` and `ScapeAndRunParasites-1.7.10/`, together with their mod JARs, are kept on the maintainer's PC and excluded from the current public tree and release downloads. The original owners have not been contacted and redistribution permission has not been confirmed. Existing Git history has not been rewritten, so earlier commits may still contain those files.

The root `.gitignore` excludes those folders and their collected JARs. Leave these rules in place and do not use `git add -f` to upload them. Removing a file from Git tracking with `git rm --cached` preserves the local working copy. GitHub cannot make individual folders in a public repository private.

## Uploading updates

Review the changes in GitHub Desktop, commit the intended public updates and push to `origin`. Each public project keeps its own README, license notices and build prerequisites. The root project list and latest-build manifest cover public projects only.

Local toolchains, caches, test worlds and generated build folders are covered by ignore rules. Ignore rules do not retroactively remove other tracked files or old commits. Original authors and licenses remain applicable; this repository grants no new redistribution rights to restricted upstream material.
