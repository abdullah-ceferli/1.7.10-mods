# Industrial Technology 2.15.0

Edge and corner hatches can now be inset across existing floor joins. Remaining floor pieces retain the original blocks and metadata, so adjacent floor fills the space around the one-block opening. The nearest clicked edge/corner selects placement. Empty legacy offset parts accept paid floor infill; removal restores retained floors.

All three variants now animate both opening and closing over 12 ticks. Two panels retract sideways into the rails, collision follows motion, reversal is continuous, and occupied openings prevent/reverse closing. Animation timestamps and backing floor data persist and synchronize without idle tile ticking or per-frame packets.

Forge cancellation restores source floors and hatch inventory, group removal returns one hatch, and direct external replacement returns the saved backing for that cell without overwriting the external replacement. Native client/server/reload checks use actual HBM and Chisel; client checks include NEI and observed intermediate frames. See [the hatch guide](../HATCH_GUIDE.md) and [release evidence](test-results/2.15.0/release.json). Android testing is pending.

The colored lamps, Building Stick and all unrelated runtime classes are retained unchanged. Installation is manual; replace Industrial Technology on both sides. No user world is opened.
