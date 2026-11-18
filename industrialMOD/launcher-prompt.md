# Codex prompt: build an actual Minecraft desktop launcher

Build a complete, usable desktop launcher for Minecraft Java Edition. Do the implementation, run it, test the important behavior, and give me the source and a runnable release. Do not stop after a plan, wireframe, or attractive shell with fake features.

Use `BlockDock` as a temporary project name. Keep branding in one place so I can rename it later. Launcher release numbers and Minecraft version numbers must be separate.

## Product direction and reference

I like Legacy Launcher's familiar, compact desktop interface: straightforward account selection, version selection, settings, and an obvious launch action. Study its actual reference screens before designing:

- Main page and screenshots: https://llaun.ch/en
- Main screen: https://llaun.ch/_image/_next/static/media/en-light-main.f82b7b7f.png/w3840/q75
- Settings: https://llaun.ch/_image/_next/static/media/en-light-settings.5870be27.png/w3840/q75
- Accounts: https://llaun.ch/_image/_next/static/media/en-light-accounts.a2244d4d.png/w3840/q75

Use comparable workflow, information density, discoverability, and feature coverage. Create original branding, visual assets, component styling, background, and wording. Changing a background is not a legal clearance strategy; do not promise that superficial changes prevent claims. Do not imply endorsement by Legacy Launcher, Mojang, or Microsoft. If you reuse any third-party code or assets, verify the license and preserve required notices. Do not bundle copyrighted Minecraft game files in the launcher release.

If the reference links fail, inspect any user-provided screenshots or an available local installation read-only. Explain any reference you could not inspect instead of inventing what it looks like.

## Technical direction

- For a fresh project, use C# with Avalonia and a supported .NET LTS release. Windows x64 is the first release target. Use a desktop application, not a website in an Electron wrapper. Verify framework APIs and dependency versions against current official documentation.
- Separate UI, account/authentication, instance storage, metadata/downloads, Java selection, skin providers, process management, and logging. Use asynchronous work and cancellation so networking and file operations never freeze the UI. Avoid unnecessary abstractions and dependencies.
- Launch normal Java Minecraft now. Put process preparation and launching behind a small backend interface so a separate Rust engine could be added later. This project does not translate Minecraft JARs or Java mods into Rust. Do not advertise a working Rust engine or an FPS improvement that you have not built and measured.
- Keep game data in isolated launcher-owned instances. Never modify my current Minecraft installation, modpack, accounts, or worlds while testing. Import existing profiles by copying selected data into a new instance; show what will be copied and leave the source intact. Never silently import credentials.

## Visual quality: mandatory requirements

The result should feel like a carefully designed desktop utility for playing Minecraft. It must not look like a generic AI-generated SaaS dashboard.

Use a light theme by default: warm gray chrome, white inputs, dark readable text, restrained borders, and one muted green accent for the main action. Establish explicit color, spacing, typography, and component tokens. Use Segoe UI on Windows with appropriate fallbacks. Body text should generally be 13–14 logical pixels, with a clear but modest heading hierarchy. Use a consistent 4/8-pixel spacing rhythm and small, consistent corner radii.

Start around 1000 × 650 logical pixels, with a usable minimum around 800 × 560. Check 100%, 125%, 150%, and 200% display scaling. Nothing essential may overlap, clip, disappear, or depend on hover to be discovered. Provide visible keyboard focus, proper tab order, accessible labels, sufficient contrast, and working window controls.

Home screen composition:

1. A compact header for the original logo/name, primary navigation, and selected account.
2. A central background/content area using an original or properly licensed image, or a tasteful procedural scene. Keep it subdued and allow the user to replace it. A simple well-made background is preferable to decorative clutter. Do not copy Legacy's artwork.
3. A persistent bottom launch strip with clearly labeled instance/version selection, account context, an instance-folder shortcut, and one prominent Play button. Put secondary actions in consistent, discoverable locations.
4. A compact status area showing real download/launch progress and access to logs. Long operations must not obscure the launch strip or move unrelated controls around.

Provide coherent screens for Home, Instances/Versions, Accounts, Skins, Downloads, Logs, Settings, and About/Credits. Use compact forms, tables, lists, and dialogs where appropriate. Navigation must preserve selection and avoid unnecessary modal dialogs.

Avoid oversized rounded cards, purple/blue gradient defaults, glassmorphism, neon glows, decorative statistics, emoji icons, arbitrary badges, huge empty headings, excessive padding, fake activity feeds, and stock marketing copy. Do not add a giant sidebar just to fill space. Use one consistent, properly licensed icon set. Keep error messages concise and useful. Every enabled control must do something real.

Create a short DESIGN.md with the layout and visual tokens, then proceed with implementation. Do not turn it into a request for routine design approval.

## Accounts

Support multiple saved accounts, switching the selected account, editing local account details, removing an account, and clearly distinguishing account types.

### Local/offline accounts

- Allow a local player name and use the correct Minecraft offline UUID derivation, with validation suitable for supported versions.
- Persist identity and the selected skin across launcher restarts.
- Support single-player and servers that explicitly accept offline authentication. A local identity cannot authenticate to online-mode servers; present that limitation plainly without claiming it can bypass ownership or authentication checks.

### Microsoft accounts with Minecraft Java access

- Use a legitimate, currently supported browser-based Microsoft sign-in flow, with the required Xbox/Minecraft authentication and entitlement/profile checks. Microsoft sign-in alone is not proof of Minecraft ownership.
- Verify current requirements for a third-party launcher application registration and Minecraft service access. Use this project's legitimate client registration. Do not borrow another launcher's application ID or put a confidential client secret into a distributed desktop binary.
- Never ask for a Microsoft password inside our UI. Handle cancellation, expired sessions, consent problems, missing entitlement, missing Java profile, and service outages accurately.
- Store credentials/tokens using suitable OS-protected storage and library-supported protected token persistence. Never store plaintext refresh tokens in JSON, logs, exports, Git, or crash reports. Redact launch arguments and HTTP diagnostics before writing them to logs.
- Implement silent refresh where supported and offer interactive sign-in when needed. Signing out removes this launcher's stored credentials without deleting game files.
- If real authentication requires a developer client ID or approval I have not supplied, finish the other work, provide exact setup instructions, and clearly show the Microsoft feature as awaiting configuration. Do not simulate successful login or claim an end-to-end sign-in test you did not perform. Let me perform interactive sign-in myself when it is available.

## Offline skins

Give each local account a skin page with Import PNG, Preview, Apply, Reset, and Open Skin Folder. Store a managed copy so deleting the original import does not break the selection. Use nearest-neighbor rendering and correct UV mapping; do not blur, stretch, or invent missing skin layers. Validate PNG contents and dimensions, and support classic 64 × 32 and modern 64 × 64 inputs where the selected provider supports them. Show Steve/Alex compatibility accurately for the target version.

A launcher cannot make a local PNG appear in every unmodified game or on every server. Build an explicit skin-provider system and display the supported game versions and multiplayer behavior. Selecting a PNG must not falsely imply that it has been applied in-game.

For Minecraft 1.7.10, I already have an OfflineSkins mod. If its source is available, inspect and integrate its actual configuration/protocol rather than guessing paths or bundling a second conflicting skin mod. The existing source may be at `D:\IndustrialTechnology\industrialMOD\offline skin mod`. Read it without changing the original project. This provider currently supports classic player arms and has its own PNG size limits; validate against the actual implementation.

Offer companion-mod installation only when needed, with its license and supported versions visible. Install/configure it only inside the selected launcher-owned instance. For LAN skin visibility, both players need compatible in-game support; verify that flow using the actual mod. Do not promise visibility on arbitrary servers or upload skins to an external sharing service by default. Keep Microsoft profile skins distinct from local skin overrides.

## Instances, versions, downloads, and launching

- Provide create, rename, duplicate, select, import, and remove-instance actions. Instance removal requires confirmation and a clear explanation of the files affected, with a recoverable approach where available.
- Each instance has its own game directory, saves, mods, config, logs, version/loader selection, Java path, memory settings, resolution, and JVM arguments. Show effective inherited settings when using global defaults.
- Fetch version metadata from appropriate official sources. Support the manifest/inheritance, libraries, OS/architecture rules, natives, assets, and launch-argument formats of the versions actually advertised. Never implement a version picker that lists unsupported versions as ready to play.
- Prioritize vanilla Minecraft 1.0 and 1.7.10, and Forge 1.7.10 build 10.13.4.1614 for my modpack. Add broader versions/loaders through the same architecture. Clearly distinguish tested, untested, and unsupported combinations. Do not claim every Minecraft version or every mod loader works without verification.
- Detect Java installations, show their version and architecture, allow browsing to an executable, and validate suitability before launching. Do not assume the newest Java can run every old Forge pack. Offer runtime downloads only from verified providers, with source and license information.
- Download with bounded concurrency, real byte/file progress, checksum/size validation where provided, cancellation, retry, and atomic finalization. Handle interrupted or corrupt downloads without marking them complete. Extract archives safely and reject paths escaping the intended directory.
- Download game files at runtime from their legitimate sources, subject to the relevant account/access requirements. Do not bundle another person's installation, account, tokens, worlds, or a repackaged game.
- Build the actual launch classpath, native paths, JVM arguments, and game arguments for the selected version. Handle spaces and Unicode in paths using process APIs rather than unsafe shell command concatenation.
- Disable conflicting duplicate launch/download actions. Support keep-open, minimize, or close-after-launch preferences. Explain what happens to a running game if the launcher is closed. Do not unexpectedly terminate the game.
- Include an instance mod list with enable/disable and open-folder actions. Avoid running or installing arbitrary modpack scripts. Do not advertise a hosted mod marketplace unless its API integration is actually implemented.

## Logs and diagnostics

Make logs a first-class feature, not a decorative terminal.

- Capture launcher events and the real game process stdout/stderr, with timestamps, severity where available, and session/instance association. Preserve full exception stack traces and process exit codes.
- Provide search, severity filters where reliable, pause auto-scroll, copy selection, copy a redacted session, clear the current view, open the log folder, and export a redacted diagnostic bundle. Clearing the view should not silently delete saved log files.
- Use a virtualized/bounded view so a noisy modpack cannot consume unlimited UI memory. Keep disk rotation/retention configurable and do not invent severity for unparsed lines.
- Show useful launch stages and failures: Java not found, unsuitable runtime, metadata/download failure, missing files, authentication failure, permission error, and game crash. Link to the relevant saved log/crash report.
- Never log access/refresh tokens, authorization headers, cookies, or unredacted authenticated URLs. Apply redaction before writing files as well as before displaying or exporting data. Make optional personal-path redaction available for shared diagnostics.
- Never upload logs or crash reports automatically. No telemetry, advertisements, or background account services by default.

## Settings, storage, and packaging

Include game/data directory, per-instance/global Java and memory defaults, JVM arguments, window size, launch behavior, background selection, theme, language-ready strings, download limits, and log retention. Save settings atomically with a schema version, sensible defaults, and recoverable handling of malformed files. Cancel must discard unsaved dialog edits. Dangerous reset/delete actions must state their scope.

Provide a working Windows x64 portable release. Document normal app-data storage and an explicit portable mode. Do not install system-wide components, alter firewall rules, or replace existing Java installations without a separate user action. Do not add a fake updater; if updating is implemented, use a verified release source and verify package integrity.

Include README, build/run/package commands, architecture notes, supported-version matrix, authentication setup, skin support matrix, dependency licenses/credits, and a .gitignore covering build outputs, local game data, logs, account/token caches, and secrets. Include a clear unofficial-product disclaimer consistent with current Minecraft usage guidelines. Never invent a publisher contact address; identify any release metadata I must supply.

## Verification and completion criteria

Build and run the actual desktop app. Inspect screenshots of every main screen and fix visual defects before declaring completion. A successful compile alone is insufficient. Check the minimum window size, common scaling levels, empty states, a long instance/account name, a long file path, download/error states, and keyboard navigation. Keep comparison screenshots or a short visual verification report.

Use meaningful automated tests for offline UUIDs, version/argument resolution, secret redaction, safe archive extraction, interrupted downloads, settings recovery, and launch-state transitions. Keep any fake auth/download implementations restricted to tests; production screens must use real services and persisted data.

Verify at least these real workflows in isolated test instances:

1. Create a local account and instance, restart the app, and confirm persistence.
2. Import a skin, apply it through a supported provider, and confirm the in-game result. Report multiplayer visibility separately from local preview success.
3. Download and launch each primary supported vanilla version with the appropriate Java runtime; capture real process output and detect exit.
4. Install and launch the primary Forge configuration in a disposable instance. Never use my main world as a test fixture.
5. Exercise a canceled download, invalid Java selection, failed launch, and corrupted settings file; confirm understandable recovery.
6. Verify that exported diagnostics contain no credentials. Check app responsiveness while streaming a large log.
7. Test Microsoft sign-in and entitlement with my interactive participation only when legitimate registration is configured. Otherwise report exactly what is externally blocked.

Measure startup time and idle resource use on the actual test machine and report how you measured them. Do not invent benchmark numbers or claim that a lightweight launcher fixes in-game rendering bottlenecks.

Work autonomously through routine choices. Keep progress updates concise. Finish all independently testable work even if external account registration is pending. At the end give me the runnable build path, source path, screenshots, tests actually run, and a short honest list of remaining limitations. Do not label missing features complete or hide unfinished work behind enabled buttons.

## Primary references to verify during implementation

- Legacy visual reference: https://llaun.ch/en
- Minecraft usage/branding guidelines: https://www.minecraft.net/en-us/usage-guidelines
- Microsoft desktop authentication guidance: https://learn.microsoft.com/en-us/entra/identity-platform/scenario-desktop-acquire-token
- Microsoft authentication library token persistence: https://learn.microsoft.com/en-us/entra/msal/dotnet/how-to/token-cache-serialization
- Avalonia documentation: https://docs.avaloniaui.net/

The general Microsoft identity documentation does not, by itself, establish eligibility to call Minecraft services. Verify the current Minecraft/Xbox-specific requirements and application permissions before claiming paid-account support is operational.
