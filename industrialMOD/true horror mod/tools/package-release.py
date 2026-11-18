"""Package only a JAR that passed the full-pack and two-client dedicated checks."""
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED
import json,hashlib,xml.etree.ElementTree as ET

project=Path(__file__).resolve().parents[1]
jar=project/'build/libs/TrueHorror-1.7.10-1.0.0.jar'
digest=hashlib.sha256(jar.read_bytes()).hexdigest()
results={}
for name in ['client-safe-final','dedicated-safe-final']:
    result=json.loads((project/'build'/name/'RESULT.json').read_text())
    assert result['passed'] and result['production_sha256'][jar.name]==digest
    results[name]=result
    if name=='client-safe-final':assert (project/'build'/name/'mods'/jar.name).read_bytes()==jar.read_bytes()
    else:
        for part in ['server','client-a','client-b']:assert (project/'build'/name/part/'mods'/jar.name).read_bytes()==jar.read_bytes()
testRoots=[ET.parse(file).getroot() for file in (project/'build/test-results/test').glob('TEST-*.xml')]
assert sum(int(t.get('tests')) for t in testRoots)==18
assert all(t.get('failures')=='0' and t.get('errors')=='0' for t in testRoots)
audit=json.loads((project/'build/asset-audit.json').read_text())
assert audit['audio_files_decoded']==38 and audit['user_skins_exact']
(project/jar.name).write_bytes(jar.read_bytes())
report=f'''# Release verification - 2026-11-09

Release SHA-256: `{digest}`.

## Automated core tests

18 tests passed: exact progression boundaries, activity/time scaling, randomized quiet periods, recent-event memory, warmup exclusion of creatures/scares, configuration/context event gates, distinct creature policies, gaze/caution, reveal/chase/retreat behavior, chase bounds, scare probability and cooldown semantics, and strict bounded effect packet decoding.

## Actual Minecraft checks

- Java 8u51, Minecraft 1.7.10, Forge 10.13.4.1614.
- A full current-modpack integrated client passed: seven rendered/registered entities, 30 forced event operations in suitable fixture terrain, real torch/window/animal/terrain effects, conditional saved undo, container/item protection, subsequent-player-edit preservation, region encounter limits, actual hunting navigation, Watcher stillness under gaze and movement after looking away, client effect receipt/expiry, actual death/respawn and full world reload persistence.
- A separate production dedicated server and two real Forge clients passed the same checks, including independent per-player progression and synchronized models/effects. The tested production bytes match in all four fixture installations.
- Runtime screenshots are in `build/client-safe-final/screenshots/` and `build/dedicated-safe-final/screenshots/`. The first model review used an independent renderer of the actual meshes/UV textures; final checks use Minecraft's legacy renderer.
- Navigation uses local waypoints with a loaded-area guard around vanilla ChunkCache, and gaze ray tests also reject unloaded areas. A far-away missing-chunk navigation request was tested without increasing the server's loaded chunk count.
- 38 original Vorbis files decode successfully; model UV bounds, parent references, skin byte identity and Java 8 bytecode were audited. The release contains no harness/Minecraft classes.

## Scope and limitations

The tests use accelerated state setup and prepared disposable terrain; they are not a multi-hour natural survival/balance playtest. The ten-minute grace/progression boundaries and random selection were checked directly, not by waiting forty real minutes in every world. Audio files and references were validated; subjective listening quality and physical Android/Zalith behavior have not been validated. Test client master audio was muted. Legacy Forge/OptiFine version checks, OpenAL initialization and reconnects can emit upstream warnings in the minimal two-client fixture; both clients still completed and exited. No permanent screen effect or mod crash occurred in the final tests.

No main save was opened. Optional block changes default off. User-supplied Herobrine and replacement standard-player Shadow skins are preserved unchanged; Watcher is 4 blocks tall and Pale Man 3.5. See ASSET_CREDITS.md.

Raw evidence: `build/client-safe-final/RESULT.json`, `build/dedicated-safe-final/RESULT.json`, corresponding console logs and `build/asset-audit.json`.
'''
(project/'VERIFICATION.md').write_text(report,encoding='utf-8')
distribution=project/'build/distributions';distribution.mkdir(exist_ok=True)
with ZipFile(distribution/'TrueHorror-1.7.10-1.0.0.zip','w',ZIP_DEFLATED) as z:
    z.write(jar,jar.name)
    for name in ['README.md','EVENTS.md','ASSET_CREDITS.md','VERIFICATION.md','LICENSE']:z.write(project/name,name)
with ZipFile(distribution/'TrueHorror-1.7.10-1.0.0-source.zip','w',ZIP_DEFLATED) as z:
    for name in ['src','tools','gradle','docs','development-assets']:
        for file in (project/name).rglob('*'):
            if file.is_file() and '__pycache__' not in file.parts and file.suffix!='.wav':z.write(file,file.relative_to(project))
    for name in ['build.gradle','settings.gradle','gradle.properties','gradlew','gradlew.bat','build.ps1','install.ps1','README.md','EVENTS.md','ASSET_CREDITS.md','VERIFICATION.md','LICENSE','.gitignore']:
        z.write(project/name,name)
files=[jar,project/'build/libs/TrueHorror-1.7.10-1.0.0-sources.jar',*distribution.glob('*.zip')]
(project/'SHA256SUMS.txt').write_text(''.join(hashlib.sha256(f.read_bytes()).hexdigest()+'  '+f.name+'\n' for f in files),encoding='utf-8')
print('Packaged verified release:',jar.name,digest)
