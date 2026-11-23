# Compatibility changes

## 2.2.46-zalith.2

- Prevent the native `LWJGL3Service` binding from being redirected back into Angelica. Fixes the `glFenceSync -> fenceSync -> GLSMBuffers.glFenceSync` stack overflow seen after shader initialization on Zalith 2 / MobileGlues.
- Keep real synchronization, streaming, batching and regular caller tracking enabled.
- Add an original-versus-patched redirector regression and native GPU fence lifecycle checks for both launcher libraries.
- Retain the shader-query buffer fix from `zalith.1`.

Android device validation is still required. GL4ES configurations exposing only OpenGL 2.1 remain below Angelica's OpenGL 3.3 core requirement.

## 2.2.46-zalith.1

- Preserve shader query output size, type and buffer position across repeated calls in the Zalith 1 and 2 LWJGL bridges.
- Retain upstream performance features and configuration defaults.
