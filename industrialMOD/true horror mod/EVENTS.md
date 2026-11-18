# Contextual event catalogue

The director chooses only eligible events, avoids the last four event IDs and schedules a randomized quiet period. Environmental requirements refer to the player's surroundings. Operator previews bypass progression/chance gates, while retaining space, loaded-chunk, creature-enable and optional-edit checks.

| Event ID | Starts | Conditions / effect |
| --- | --- | --- |
| FOOTSTEPS | Uneasiness | Footfalls behind the player |
| BRANCH_SNAP | Uneasiness | A branch snaps in the forest behind/above |
| CAVE_BREATH | Uneasiness | Positional breath or distant tunnel resonance underground |
| TUNNEL_MINING | Uneasiness | Mining impacts beyond the cave wall |
| WHISPERS | Uneasiness | Quiet positional whispers in darkness |
| DISTANT_WAIL | Uneasiness | A distant night-time cry |
| SHELTER_KNOCK | Uneasiness | Knocking outside an enclosed shelter |
| TORCH_FLICKER | Uneasiness | Finds a real nearby torch; temporary client darkening and a quiet crackle |
| ANIMAL_STARE | Uneasiness | Up to six nearby animals turn toward the player |
| ANIMAL_HUSH | Uneasiness | Nearby animals fall silent; a brief local ambient/music hush |
| WAKE_ECHO | Uneasiness | A strange echo after waking |
| GHOST_LAUGHTER | First encounters | Quiet laughter from darkness |
| HILL_SIGHTING | First encounters | Herobrine observes from a distant safe surface |
| TREE_SENTINEL | First encounters | Watcher sighting in a night forest |
| WINDOW_VISITOR | First encounters | Finds glass and places Herobrine in valid space on its far side; rare scare gate |
| BED_SENTINEL | First encounters | Brief night-time sighting near a bed/shelter |
| SILHOUETTE | First encounters | Pale Man stands at a distance |
| STALKER_TRAIL | First encounters | A silent follower; ignored repeated warnings can escalate later |
| CAVE_ENTRANCE_MOVEMENT | First encounters | A Crawler moves near a cave passage |
| CORRIDOR_LURE | First encounters | Mimic waits in a dark cave and imitates familiar sounds |
| SHADOW_CROSSING | First encounters | A peripheral Shadow in darkness |
| MINING_APPARITION | First encounters | Brief Herobrine encounter after recent mining; rare scare gate |
| RUSH_FEINT | Escalation | Herobrine runs toward the player, then fades before contact |
| DARKENED_APPROACH | Escalation | A darkened approach by the Pale Man |
| RARE_CHASE | Full horror | A limited Dweller/Pale Man pursuit in darkness |
| DOOR_OPEN | First encounters, optional edits | Opens an actual nearby wooden door, then conditionally restores it |
| MYSTERY_SIGN | First encounters, optional edits | A temporary unsettling sign on suitable natural ground |
| LEAF_TRACE | First encounters, optional edits | Briefly removes natural leaves, skipping non-decaying/placed leaves |
| WILDERNESS_CAIRN | First encounters, optional edits | A small temporary stone cairn on suitable natural ground |
| SHALLOW_TUNNEL | Escalation, optional edits | Opens and later restores a 3-long, 2-high natural-stone niche; rejects liquids and tile entities |

First encounters begin after the configured grace period (default 10 active minutes); escalation is ten minutes later; full horror is thirty minutes after the grace period. Not all events can occur in every biome, building or passage. A failed search produces a later retry, not forced placement.
