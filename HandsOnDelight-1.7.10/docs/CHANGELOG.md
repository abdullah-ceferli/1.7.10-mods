# Changes

## 1.5.1-backport.4

Changed rendering-state restoration for the mobile report of damaged HUD and menu text after leaving a station. Texture combiners, matrices, bindings, active texture units and separate alpha blend factors now restore explicitly. Cookbook opacity and original UI artwork are retained.

Added native state-isolation and font/item pixel-comparison checks plus post-exit HUD/menu captures. See MOBILE-RENDERING.md for the GL4ES finding and the remaining phone-validation limit. No world data or network packet format changed.

## 1.5.1-backport.3

Fixed ordinary world right-clicks failing to open cooking stations. The client interaction event now proceeds so that Minecraft sends its native use-block packet. The server intercepts the click and opens the station screen. Block-use denials, existing event cancellations and the native sneak-use bypass are respected.

Added regression checks invoking Minecraft's real right-click method for a pot and cutting board. The previous build fails this test before any cooking screen opens; the corrected build must open both stations and complete the existing cooking, cutting, cookbook and NEI checks. Added dedicated checks for protected and sneaking interactions.

No save format or network packet format changed. Use the updated JAR on the host/server and each client, and restart Minecraft after replacing the older Hands-On JAR.
