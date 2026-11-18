# ChunkShift: correct the existing UI to match Legacy Launcher

The current ChunkShift interface does not match what I requested. Work on the existing project and replace its UI with a faithful recreation of Legacy Launcher's actual light-theme interface. Implement the correction; do not stop after suggestions or a mockup. Preserve the working launcher backend, accounts, skins, downloads, instance management, settings, logs, and game launching.

**My target is a 100% visual match to Legacy Launcher's actual light-theme interface: layout, proportions, control placement, spacing, typography, colors, panel treatment, and interaction flow.** Keep the ChunkShift application name and values/content reflecting our real accounts and supported features. Do not redesign the interface or commission a different background. I will customize the UI myself later. This correction supersedes the previous prompt's instructions for a different background, original UI design, header/navigation bar, bottom launch strip, green primary button, or avoiding translucent panels.

## 1. Inspect the actual references first

Open these screenshots with an image viewer and study their geometry, not just their filenames:

- Main: https://llaun.ch/_image/_next/static/media/en-light-main.f82b7b7f.png/w3840/q75
- Settings: https://llaun.ch/_image/_next/static/media/en-light-settings.5870be27.png/w3840/q75
- Accounts: https://llaun.ch/_image/_next/static/media/en-light-accounts.a2244d4d.png/w3840/q75
- Reference page: https://llaun.ch/en

Local copies are also available on this PC:

- `D:\IndustrialTechnology\industrialMOD\launcher-ui-reference\legacy-main.png`
- `D:\IndustrialTechnology\industrialMOD\launcher-ui-reference\legacy-settings.png`
- `D:\IndustrialTechnology\industrialMOD\launcher-ui-reference\legacy-accounts.png`

If none can be viewed, report that and request the reference screenshots before redesigning. Do not claim to have matched a reference you did not see. Reimplement the interface using our code and appropriately licensed assets; do not copy proprietary source or rip third-party textures/icons.

## 2. Main screen: match this composition

- A simple desktop title bar with the block icon and normal window controls.
- A background image filling the client area.
- A compact translucent launch panel centered horizontally and vertically in the client area. The reference main screen has no dashboard header, permanent sidebar, large logo banner, or bottom-wide launch dock.
- Inside that panel, keep this exact vertical order: account dropdown; version dropdown; thin separator; Delayed start checkbox; Force update checkbox; thin separator; large light-colored **Enter the game** button with bold black text; four equal-width icon buttons along the bottom.
- Match the bottom icon row's appearance: blue chat/help glyph, orange folder, green refresh, black menu glyph. Use properly licensed or original glyphs with matching visual weight and positioning. Wire them to real existing actions. Do not invent a community/support destination; use the real help/logs action if none is configured.
- Use pale rectangular controls, thin gray outlines, small corner radii, black text, and the reference's translucent white panels with soft inset/highlight edges. Do not replace these with a dark theme, pill controls, or modern marketing cards.

For initial sizing, the reference is approximately a 1000 × 600 logical-pixel window, with a roughly 285 × 240 launch panel, 24-pixel horizontal inner padding, 237-pixel-wide selectors, a 46-pixel-high launch button, and a 30-pixel-high icon row. These are starting measurements: derive the final dimensions from the actual screenshots at the same scale. Do not treat a roughly 2× screenshot as logical pixels and double every control size. Center within the client area, excluding the title bar.

## 3. Settings and accounts: preserve the reference arrangement

Settings: move the same compact launch panel to the left. Put the large translucent settings panel on the right. Match its top horizontal tabs (**Minecraft**, **Launcher**, **About**), aligned field labels, directory field and Browse button, resolution fields, fullscreen checkbox, two-column version filters, Java selector/configure action, RAM slider plus MiB input/Auto option, and bottom Save/Reset/Home arrangement. Keep working extra settings within suitable tabs or subpages, using the same visual language. Display actual supported filters and options; do not implement fake controls to fill the screenshot.

Accounts: use the reference's two adjacent translucent panels. The left contains the saved-account list with compact bottom actions. The right contains the add/edit account content with a back arrow, heading, and stacked wide account-type buttons. Support our real Microsoft and Local/offline accounts and existing skin controls. Do not add a fake Ely.by service merely because it appears in the reference. Show actual usernames and account status instead of copying the screenshot's example identity.

Keep logs, skins, downloads, and instance tools accessible through the compact menus or matching subpages. Do not remove functioning features to achieve a screenshot, and do not put a new navigation system across the main screen.

## 4. Background and application icon

There is no requirement to change the background. Follow the reference appearance using the available appropriately licensed reference/background assets; do not create a new landscape or introduce a separate art/design task. I will handle later visual customization. Use one static image or cached effect; do not render an animated 3D world or recompute full-window blur continuously. Do not let background asset selection become an excuse to invent a different interface or stop the rest of the UI correction.

Replace every generic .NET/Avalonia program icon with **an original pixel-art isometric grass-and-dirt cube**: green grass on top, brown dirt sides, transparent outside the cube, readable at small sizes. It should immediately look like a block from a voxel game. Do not use letters, a C#/.NET logo, or a copied official Minecraft launcher icon/texture.

Create a real multi-resolution `.ico` with at least 16, 24, 32, 48, 64, 128, and 256-pixel sizes, plus the PNG assets needed by the UI. Configure the Windows executable's embedded application icon, Avalonia Window.Icon, taskbar/Alt-Tab identity, and packaged shortcuts. Check the published executable in Explorer and when running it directly. Do not consider it fixed just because a block picture appears inside the window. If an old pinned shortcut retains a cached icon, explain that separately and verify the new executable/shortcut rather than clearing global system caches.

## 5. Verify before calling this finished

Keep the existing C#/Avalonia stack and existing launcher logic. Inspect the repository before editing and avoid unrelated changes. Make all controls functional, preserve keyboard focus and dropdown behavior, and keep the UI responsive during downloads and log streaming.

Run the application and capture Main, Settings, and Accounts at the reference aspect ratio and matching scale. Compare them side by side or with overlays. Match major panel and control bounds within roughly 3 logical pixels at the comparison size as an initial check, then refine the remaining differences toward a visually 1:1 result. Correct spacing, alignment, fonts, borders, colors, and opacity. Exclude only deliberate application-name/account-value differences from comparison, and assess the controls separately so a matching background cannot conceal a mismatched interface. Do not claim a 100% match without inspecting actual output.

Also check 100%, 125%, 150%, and 200% display scaling, resizing, dropdowns, a long username, settings save/cancel, account switching, the existing skin screen, launch/log access, and the executable/taskbar icons. Exercise launcher actions only in disposable instances; never use or alter my main world for testing.

Finish with a runnable build, its exact path, actual screenshots, the icon assets, and an honest list of any visible differences still remaining. Continue through routine implementation decisions without asking me to approve a completely different design.
