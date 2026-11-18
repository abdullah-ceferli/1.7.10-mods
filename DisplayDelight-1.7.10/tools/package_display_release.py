"""Save a verified native test release, source and immutable evidence."""
from pathlib import Path
import json,hashlib,shutil,subprocess,sys,zipfile
R=Path(__file__).resolve().parents[1];D=R/'docs'
def read(p):return json.loads(p.read_text(encoding='utf-8'))
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def write(p,d):p.write_text(json.dumps(d,indent=2)+'\n',encoding='utf-8')
def classes(path):
 with zipfile.ZipFile(path) as z:
  return {n:hashlib.sha256(z.read(n)).hexdigest() for n in z.namelist() if n.startswith('com/jkvin114/displaydelight/') and '/tests/' not in n and n.endswith('.class')}
for name,count in [('checks-initial.txt',12),('checks-restart.txt',1),('checks-client.txt',6)]:
 text=(D/name).read_text(encoding='utf-8');assert text.splitlines()[-1]=='Failed: 0';assert sum(x.startswith('PASS:') for x in text.splitlines())==count
native=read(D/'native-run.json');client=read(D/'client-run.json');export=read(D/'asset-export.json');upstream=read(D/'upstream.json')
nr=Path(native['runDirectory']);cr=Path(client['runDirectory']);clientlog=(cr.parent/'client.log').read_text(encoding='utf-8',errors='replace');assert '[TEXTURE ERRORS]' not in clientlog and 'OpenGL Error' not in clientlog
original=R/'upstream/displaydelight-1.20.1/src/main/resources/assets/displaydelight'
original_textures={p.relative_to(original).as_posix():sha(p) for p in (original/'textures').rglob('*') if p.is_file()}
for n,h in original_textures.items():assert sha(R/'src/main/resources/assets/displaydelight'/n)==h,n
assert (R/'LICENSE.txt').read_bytes()==(R/'upstream/displaydelight-1.20.1/LICENSE.txt').read_bytes()
(D/'TESTING.md').write_text('''# Native verification — 2026-11-17

This first backport test build compiles against the unmodified Forge 1.7.10 API and passes **19 native test groups**: twelve dedicated-server cases, one actual process restart, and six actual-client checks.

The dedicated cases cover all 382 registrations without optional mods, native vanilla item links, single/bulk insertion, capacity/consumption, real edible-item retrieval, hidden plates and correct plate quantities, small-plate subtype mappings, missing-food decorations, direct placement through the Forge event, normal/Silk Touch drop rules plus native harvesting, saved count/hidden state and non-ticking tiles, support exceptions, adventure edit permissions and farmer-villager trades. A second dedicated-server process reloads a placed stack and verifies count, hidden state and facing.

Client checks resolve every used native texture path and a translated plate name, load all model types, receive 382 placed displays/full counts, receive changed count/hidden state, render the world gallery, and render all display items through the native inventory pipeline. Both screenshots were inspected. Native client logs contain no texture errors or OpenGL errors. Client QA uses process-only `ALSOFT_DRIVERS=null`; audio hardware was not tested.

Production class hashes in the runtime JAR are compared with the classes loaded by both successful server and client runs. The test module is absent from the packaged runtime JAR. Original textures and license are byte-identical to the pinned upstream source; derived native atlas copies and meshes are audited separately.

These are disposable clean Forge worlds. HBM, Industrial Technology, external food-mod combinations, region-protection plugins, large-placement performance and upgraded user saves were not tested. Native sounds, particles, trader replacement and item transforms are adaptations, not a claim of exact modern-renderer equivalence. See README.md for the intentional tile-state and trader differences.

The `docs/test-results/2026-11-17/` directory retains successful logs, checks, screenshots, source/class hashes and a manifest. Initial failures were corrected before packaging: identical-block stack updates, native texture paths, native language names, and adventure edit gating. No existing user world or instance was opened.
''',encoding='utf-8')
subprocess.run([sys.executable,str(R/'tools/build_display.py')],cwd=R,check=True)
build=read(D/'build.json');runtime=Path(build['jar']);runtime_classes=classes(runtime)
for run in [nr,cr]:assert classes(run/'mods/displaydelight-1.7.10-tests.jar')==runtime_classes
with zipfile.ZipFile(runtime) as z:
 assert z.testzip() is None
 assert not any('/tests/' in n or 'farmersdelight/' in n or n.startswith('net/minecraft/') for n in z.namelist() if n.endswith('.class'))
 for n in runtime_classes:
  data=z.read(n);assert b'net/minecraft/world/level/' not in data and b'net/neoforged/' not in data
 assert z.read('assets/displaydelight/legacy/catalog.json')==(R/'src/main/resources/assets/displaydelight/legacy/catalog.json').read_bytes()
E=D/'test-results/2026-11-17';assert not E.exists(),'Retain immutable evidence; select a new stage name';E.mkdir(parents=True)
for n in ['upstream.json','asset-export.json','build.json','test-build.json','native-run.json','client-run.json','checks-initial.txt','checks-restart.txt','checks-client.txt','TESTING.md']:
 shutil.copy2(D/n,E/n)
for n,p in [('server.log',nr.parent/'initial.log'),('restart.log',nr.parent/'restart.log'),('client.log',cr.parent/'client.log')]:shutil.copy2(p,E/n)
for name in ['displaydelight-gallery.png','displaydelight-inventory.png']:shutil.copy2(cr/'screenshots'/name,E/name)
write(E/'source-hashes.json',{p.relative_to(R).as_posix():sha(p) for p in sorted((R/'src/main/java').rglob('*.java'))})
write(E/'production-class-hashes.json',runtime_classes)
write(E/'manifest.json',{'date':'2026-11-17','nativeGroups':19,'serverGroups':12,'restartGroups':1,'clientGroups':6,'registeredDisplayTypes':382,'meshStates':1706,'unresolvedUsedTextures':[],'originalTextureFilesVerified':len(original_textures),'productionClassesIdenticalAcrossAllTestModules':len(runtime_classes),'runtimeJarSha256':sha(runtime),'serverTestJarSha256':sha(nr/'mods/displaydelight-1.7.10-tests.jar'),'clientTestJarSha256':sha(cr/'mods/displaydelight-1.7.10-tests.jar'),'hbmIntegrationTested':False,'installedIntoUserGame':False,'files':{p.name:sha(p) for p in E.iterdir() if p.is_file()}})
dist=R/'dist';dist.mkdir(exist_ok=True)
source=R/'build/libs/displaydelight-1.7.10-1.7.0-backport.1-sources.zip'
with zipfile.ZipFile(source) as z:assert z.testzip() is None
for p in [runtime,source,R/'README.md',R/'LICENSE.txt',R/'COPYING',R/'THIRD_PARTY.md',D/'TESTING.md']:shutil.copy2(p,dist/p.name)
manifest={'date':'2026-11-17','version':'1.7.0-backport.1','minecraft':'1.7.10','forge':'10.13.4.1614','java':8,'upstream':upstream,'registeredDisplays':382,'buildStatus':'Native test build; 19 groups pass. Modpack integration unverified.','runtimeJar':runtime.name,'sources':source.name,'sourceHashes':{p.relative_to(R).as_posix():sha(p) for p in sorted((R/'src/main/java').rglob('*.java'))},'files':{p.name:sha(p) for p in dist.iterdir() if p.is_file()}}
write(D/'release-manifest.json',manifest);write(dist/'release-manifest.json',manifest)
print('Verified test release:',dist,'production classes:',len(runtime_classes),'native groups: 19')
