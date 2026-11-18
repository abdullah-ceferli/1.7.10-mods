# Rendering state after leaving a station

Backport.4 addresses texture-state isolation after a station and its cookbook render. The report from Zalith showed damaged text and HUD/menu rendering after the cooking screen closed. That device's earlier launcher log selected GL4ES.

The previous faded-icon path changed texture-combiner settings and used `glPushAttrib(GL_TEXTURE_BIT)` to restore them. [GL4ES's attribute-stack implementation](https://github.com/ptitSeb/gl4es/blob/master/src/gl/stack.c) marks texture handling incomplete and does not save the combine sources, operands or scales. This makes that path unsafe even when it behaves correctly on desktop OpenGL.

The native adapter now saves and restores those values explicitly, along with texture environment colours, bindings, matrices, active/client texture units and separate RGB/alpha blend factors. Texture restoration uses the public OpenGL setters so an emulation layer updates its shader state as well as its tracked values. Faded icons keep the original one-third opacity. Station scenes, artwork and layouts are unchanged.

Native checks deliberately avoid relying on texture attribute-stack restoration. They cover nested faded-icon settings, both texture units and matrices, separate alpha blending, cleanup after an exception, and byte-for-byte comparisons of subsequently rendered vanilla font and item pixels. The client fixture also captures the world HUD and main menu after station exit.

These checks run against a real Windows OpenGL client with the modpack. Android GL4ES is not available in this workspace. The phone screenshots identify the reported symptoms; the compatibility change still needs confirmation on the affected phone. This release does not change the launcher's renderer or device settings.
