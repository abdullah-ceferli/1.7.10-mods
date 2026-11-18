# Native verification — 2026-11-17

This first backport test build compiles against the unmodified Forge 1.7.10 API and passes **19 native test groups**: twelve dedicated-server cases, one actual process restart, and six actual-client checks.

The dedicated cases cover all 382 registrations without optional mods, native vanilla item links, single/bulk insertion, capacity/consumption, real edible-item retrieval, hidden plates and correct plate quantities, small-plate subtype mappings, missing-food decorations, direct placement through the Forge event, normal/Silk Touch drop rules plus native harvesting, saved count/hidden state and non-ticking tiles, support exceptions, adventure edit permissions and farmer-villager trades. A second dedicated-server process reloads a placed stack and verifies count, hidden state and facing.

Client checks resolve every used native texture path and a translated plate name, load all model types, receive 382 placed displays/full counts, receive changed count/hidden state, render the world gallery, and render all display items through the native inventory pipeline. Both screenshots were inspected. Native client logs contain no texture errors or OpenGL errors. Client QA uses process-only `ALSOFT_DRIVERS=null`; audio hardware was not tested.

Production class hashes in the runtime JAR are compared with the classes loaded by both successful server and client runs. The test module is absent from the packaged runtime JAR. Original textures and license are byte-identical to the pinned upstream source; derived native atlas copies and meshes are audited separately.

These are disposable clean Forge worlds. HBM, Industrial Technology, external food-mod combinations, region-protection plugins, large-placement performance and upgraded user saves were not tested. Native sounds, particles, trader replacement and item transforms are adaptations, not a claim of exact modern-renderer equivalence. See README.md for the intentional tile-state and trader differences.

The `docs/test-results/2026-11-17/` directory retains successful logs, checks, screenshots, source/class hashes and a manifest. Initial failures were corrected before packaging: identical-block stack updates, native texture paths, native language names, and adventure edit gating. No existing user world or instance was opened.
