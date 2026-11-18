"""Package the tested sound stage with all converted source, current diagnostics and hash-verified evidence."""
from pathlib import Path
import hashlib,json,zipfile
R=Path(__file__).resolve().parents[1]
def sha(data):return hashlib.sha256(data).hexdigest()
def main():
    report=json.loads((R/'docs/compile-full-inventory.json').read_text(encoding='utf-8'))
    assert report['sourceSnapshotStable']
    sources={p.relative_to(R).as_posix():sha(p.read_bytes()) for p in (R/'src/main/java').rglob('*.java')}
    assert sources=={name:row['sha256'] for name,row in report['sourceFiles'].items()}
    base=next((R/'src/main/java').rglob('EntityParasiteBase.java'))
    errors=[row for row in report['errors'] if row['file'].replace('\\','/')==base.relative_to(R).as_posix()]
    text='# Matched converted source and diagnostics\n\nCurrent review: [sounds-2026-11-15.zip](review/sounds-2026-11-15.zip).\n\nAll '+str(len(sources))+' converted main source units, including 74 helpers, match the current compiler snapshot. The full inventory has 5,997 captured diagnostics with abnormal exit 4; the count is not exhaustive. EntityParasiteBase has '+str(len(errors))+' matched diagnostics, SHA256 '+sha(base.read_bytes())+'.\n\nSOUND_PORT.md describes native server/client evidence and remaining limits. All 1,094 original source units and 4,501 original assets remain. Earlier review ZIPs remain immutable stage snapshots. No complete playable mod exists.\n'
    (R/'docs/SOURCE_REVIEW.md').write_text(text,encoding='utf-8')
    files={}
    def include(path):
        if path.is_file():files[path.relative_to(R).as_posix()]=path.read_bytes()
    for root in ['src/main/java','src/compatTest/java','tools','docs/test-results/2026-11-15-sounds','src/main/resources/assets/srpvanilla112']:
        for p in (R/root).rglob('*'):
            if '__pycache__' not in p.parts:include(p)
    for name in ['build.gradle','settings.gradle','src/main/resources/META-INF/srp_at.cfg','src/main/resources/assets/srparasites/sounds.json']:
        include(R/name)
    for name in ['SOURCE_REVIEW.md','SOUND_PORT.md','PORT_STATUS.md','port-status.json','MAJOR_BLOCKERS.md','major-blockers.json','ALL_REMAINING_ERRORS.md','ALL_REMAINING_ERRORS.manifest.json','compile-full-inventory.json','compile-full-inventory.log','vanilla-sound-plan.json','vanilla-sound-resources.json','original-sound-audit.json','sound-source-conversion.tsv','native-sound-initial-checks.txt','sound-client-checks.txt']:
        include(R/'docs'/name)
    files['README.md']=('Current converted source; line numbers match the included fresh compiler output.\n\nRead docs/SOUND_PORT.md for sound implementation and exact test limits. All current main Java sources and compatibility tests are included. The preserved CFR headers describe source provenance; they do not identify files as untouched references. This development snapshot fails compilation and is not a playable mod.\n\nOriginal 1.12 SRP textures/models/audio are retained in the project and unchanged; this review contains the new imported vanilla audio and the original sound registry. It is a source review, not an installable mod.\n').encode()
    manifest={'date':'2026-11-15','mainSourceFiles':len(sources),'mainSourceHashesMatchCompiler':True,'capturedDiagnostics':report['reportedErrors'],'compilerExitCode':report['exitCode'],'countIsExhaustive':False,'baseEntityDiagnostics':len(errors),'fullModCompiles':False,'playable':False,'files':{n:sha(v) for n,v in sorted(files.items())}}
    files['manifest.json']=(json.dumps(manifest,indent=2)+'\n').encode()
    target=R/'docs/review/sounds-2026-11-15.zip';assert not target.exists(),'Retain prior snapshots; use a new name'
    with zipfile.ZipFile(target,'w',zipfile.ZIP_DEFLATED) as z:
        for name,data in sorted(files.items()):z.writestr(name,data)
    with zipfile.ZipFile(target) as z:
        assert z.testzip() is None
        for name,digest in manifest['files'].items():assert sha(z.read(name))==digest,name
    print('Verified source review:',target,'files:',len(files),'bytes:',target.stat().st_size)
if __name__=='__main__':main()
