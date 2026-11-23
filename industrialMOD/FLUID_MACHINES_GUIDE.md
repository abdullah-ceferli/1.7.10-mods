# Sucker and Puller вЂ” 2.12.1

Two machines connect world water/lava to HBM fluid pipes. They have no screen, inventory or energy requirement. They start **unconfigured**, even when covered by liquid.

## Setup

1. Place a **Sucker** with its hopper-textured intake touching the connected water/lava body. It can face any of the six directions.
2. Select **Water** or **Lava** as the primary fluid on HBM's **Fluid Identifier**. Right-click the machine with it. The chat message confirms the selection; no machine GUI opens.
3. Attach matching HBM fluid pipes to any face except the intake. A sucker provides the collected fluid to those pipes.
4. Place a **Puller**, configure it with the same identifier, and connect the pipes to any face except its dispenser-textured outlet. Leave the adjacent outlet space clear. It places real liquid source blocks there.

A normal identifier right-click on a puller also configures its **face-connected unconfigured pullers** with the chosen Water or Lava. Pullers already using the chosen fluid can carry the selection through the group; their contents are preserved. A puller using another fluid, a sucker, an air gap, or a denied edit stops that branch. Diagonal contact does not count. HBM pipes keep their own configuration and do not carry this puller-group selection.

Large groups update over several server ticks. Selection shares **128 checked cells per server tick**, with at most **8,192 queued/visited cells**, **64 face steps** from the clicked puller and **16 pending batches**. A new click replaces that player's previous pending batch. The chosen tool fluid is captured at click time. Only loaded chunks are inspected. Right-click again at the far end for groups beyond these bounds. Selecting None only clears the clicked empty machine; it does not clear a group.

Each additional change checks player edit permission, spawn protection and Forge right-click cancellation. A canceled or replaced tile is retained. Batches are discarded on world unload. These bounds affect configuration only; the existing collection/output budgets still apply.

Placement uses the same six-direction facing rules as a piston. Compatible wrench rotation changes the working face. The working face is the differently textured face, not every side of the block. Right-clicking with an empty hand does nothing.

An identifier selecting another fluid is rejected. A machine with fluid inside cannot switch type. An empty machine can be cleared using an identifier whose primary fluid is None. Merely covering a machine in water/lava never configures it. HBM's existing identifier selection interface belongs to the tool; these machines add no UI.

## Collection and output

- The sucker follows matching, face-connected liquid within **16 blocks in each coordinate direction** from the machine. It does not jump through air or solid blocks.
- Only source blocks produce fluid: **one source = 1,000 mB**. Flowing blocks can connect the search but are never converted into extra fluid.
- Each machine stores **8,000 mB** and attempts at most one source operation every **20 server ticks**. Full suckers stop scanning; full pullers stop accepting more fluid. A blocked outlet retains its buffer.
- When the body runs out, the sucker stops collecting and checks again after a short delay. Vanilla source regeneration still applies to water; a regenerating water pool can supply more water normally.
- The puller places one source at its adjacent outlet. Vanilla liquid flow spreads it. An existing source, solid block, tile entity or different liquid blocks output without consuming the buffer. A matching flowing cell can become a source.
- Water output is blocked in dimensions marked as Nether-like. Lava remains supported.
- Buffers, selected type and ownership survive saves. Breaking a machine drops a machine item carrying its remaining fluid and selection; placing it restores those contents.

HBM is optional for loading the rest of Industrial Technology. These two machines need HBM to operate, and target its standard pressure-0 water/lava network. Compatibility is tested against **HBM 1.0.27_X5771**. They do not emulate an HBM tank through Forge FluidRegistry names.

## Work limits

One shared server queue services all loaded machines. Per server tick it visits at most **64 machines**, examines at most **256 search cells** (at most **32 per machine visit**), and attempts at most **4 world changes**, including canceled/blocked attempts. Adding machines shares that budget rather than multiplying it. Pipe-neighbor polling is limited to once per 20 ticks per machine.

The search is incremental and deduplicated, with a fixed spatial bound and capped frontier. Full buffers do not keep scanning. Unconfigured machines stay outside the work queue. The machines never request chunk tickets or force neighboring chunks to load. Searches resume/rebuild in loaded areas; the transient frontier is not saved.

World edits use the placer identity, spawn protection and Forge break/place events. A canceled placement restores the previous cell and retains the fluid. These limits bound the machines' own work; normal Minecraft liquid flow and HBM's pipe network still have their own tick costs. Large-scale FPS/TPS measurements and Android/Zalith testing remain separate from the correctness checks.

## Crafting

Find `Sucker` and `Puller` in the Industrial Technology tab or NEI. Both use these ordinary 3x3 crafting recipes:

```text
Sucker       Puller
I G I        I G I
C B C        C B C
I H I        I D I
```

`I` iron ingot; `G` Industrial Technology gear; `C` Industrial Technology circuit; `B` empty bucket; `H` hopper; `D` dispenser.

Install the same Industrial Technology version on the client and server. Replace the previous JAR, keep Baubles, and keep the existing HBM installation. OpenComputers 1.8.10 remains bundled; use only one copy.
