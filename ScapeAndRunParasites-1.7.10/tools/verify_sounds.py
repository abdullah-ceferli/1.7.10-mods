"""Verify original sound identifiers after porting to the real 1.7 string sound API."""
from pathlib import Path
import json,subprocess
ROOT=Path(__file__).resolve().parents[1]
def main():
 c=json.loads((ROOT/'.toolchain/toolchain.json').read_text());original=json.loads((ROOT/'docs/original-sound-identifiers.json').read_text());out=ROOT/'build/sound-checks';out.mkdir(parents=True,exist_ok=True)
 expected=ROOT/'.toolchain/sound-identifiers.properties';expected.write_text('\n'.join(k+'='+('__UPSTREAM_NULL__' if v is None else 'srparasites:'+v) for k,v in original['fields'].items())+'\n',encoding='ascii')
 cp=';'.join([c['forgeJar'],Path(c['runtimeClasspathFile']).read_text().strip()]);source=ROOT/'src/main/java/com/dhanantry/scapeandrunparasites/init/SRPSounds.java';test=ROOT/'src/compatTest/java/SoundCatalogChecks.java'
 subprocess.run([c['javac'],'-proc:none','-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out),str(source),str(test)],cwd=ROOT,check=True)
 result=subprocess.run([c['java'],'-cp',str(out)+';'+cp,'dev.srpbackport.tests.SoundCatalogChecks',str(expected),str(ROOT/'docs/sound-checks.txt')],cwd=ROOT)
 if result.returncode:return result.returncode
 definitions=json.loads((ROOT/'src/main/resources/assets/srparasites/sounds.json').read_text());missing=sorted(v for v in original['fields'].values() if v is not None and v not in definitions)
 (ROOT/'docs/sound-resource-check.json').write_text(json.dumps({'originalAssignedIdentifiers':original['assignedFields'],'identifiersAbsentFromUnchangedUpstreamSoundsJson':missing,'note':'This checks the original catalog against the unchanged original resource. Missing upstream definitions are recorded and are not replaced by invented assets.'},indent=2)+'\n',encoding='utf-8')
 print('Unchanged upstream sound definitions absent for',len(missing),'original identifiers; details recorded in docs/sound-resource-check.json.')
 return 0
if __name__=='__main__':raise SystemExit(main())
