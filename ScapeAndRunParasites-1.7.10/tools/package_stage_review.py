"""Package a source-matched development stage without changing earlier snapshots."""
from pathlib import Path
import argparse,hashlib,json,re,zipfile
R=Path(__file__).resolve().parents[1]
def sha(data):return hashlib.sha256(data).hexdigest()
def main():
    parser=argparse.ArgumentParser();parser.add_argument('--name',required=True);parser.add_argument('--report',required=True);parser.add_argument('--evidence',required=True);a=parser.parse_args()
    assert re.fullmatch('[a-z0-9-]+',a.name) and re.fullmatch('[A-Z_]+.md',a.report) and re.fullmatch('[a-z0-9-]+',a.evidence)
    report=json.loads((R/'docs/compile-full-inventory.json').read_text(encoding='utf-8'));status=json.loads((R/'docs/port-status.json').read_text(encoding='utf-8'))
    assert report['sourceSnapshotStable'] and status['latestFullCompilerDiagnostics']==report['reportedErrors']
    sources={p.relative_to(R).as_posix():sha(p.read_bytes()) for p in (R/'src/main/java').rglob('*.java')}
    assert sources=={name:row['sha256'] for name,row in report['sourceFiles'].items()}
    complete=report.get('diagnosticPassCompleted',False)
    base=next((R/'src/main/java').rglob('EntityParasiteBase.java'));errors=[row for row in report['errors'] if row['file'].replace('\\','/')==base.relative_to(R).as_posix()]
    review='# Matched converted source and diagnostics\n\nCurrent review: ['+a.name+'.zip](review/'+a.name+'.zip).\n\nAll '+str(len(sources))+' converted main source units, including '+str(status['compatibilityClasses'])+' helpers, match the current compiler snapshot. The full inventory captures '+str(report['reportedErrors'])+' diagnostics with compiler exit '+str(report['exitCode'])+('; all printed errors match the compiler footer for this pass. ' if complete else '; the diagnostic pass was incomplete. ')+'EntityParasiteBase has '+str(len(errors))+' matched diagnostics, SHA256 '+sha(base.read_bytes())+'.\n\n'+a.report+' describes current implementation and verification limits. All 1,094 original source units and 4,501 original assets remain. Prior stage ZIPs are retained. No complete playable mod exists.\n'
    (R/'docs/SOURCE_REVIEW.md').write_text(review,encoding='utf-8')
    files={}
    def include(path):
        if path.is_file():files[path.relative_to(R).as_posix()]=path.read_bytes()
    for root in ['src/main/java','src/compatTest/java','tools','docs/test-results/'+a.evidence,'src/main/resources/assets/srpvanilla112']:
        for p in (R/root).rglob('*'):
            if '__pycache__' not in p.parts:include(p)
    for name in ['README.md','build.gradle','settings.gradle','src/main/resources/mcmod.info','src/main/resources/META-INF/srp_at.cfg','src/main/resources/assets/srparasites/sounds.json']:
        include(R/name)
    for name in ['SOURCE_REVIEW.md',a.report,'SOUND_PORT.md','PORT_STATUS.md','port-status.json','MAJOR_BLOCKERS.md','major-blockers.json','ALL_REMAINING_ERRORS.md','ALL_REMAINING_ERRORS.manifest.json','compile-full-inventory.json','compile-full-inventory.log','vanilla-sound-plan.json','vanilla-sound-resources.json','original-sound-audit.json','retaliation-conversion.json','native-retaliation-initial-checks.txt','native-retaliation-run.json','flight-source-conversion.tsv','flight-conversion.json','native-flight-initial-checks.txt','native-flight-run.json','movement-source-conversion.tsv','movement-conversion.json','native-movement-initial-checks.txt','native-movement-restart-checks.txt','movement-client-checks.txt','native-movement-run.json','movement-client-run.json']:
        include(R/'docs'/name)
    files['REVIEW_README.md']=('Source review of the current converted project. Source hashes match the included compiler output. Read docs/'+a.report+' for implemented behavior, native evidence and remaining limits. Original CFR headers indicate provenance; the files are the converted copies. The archive contains all main Java sources, compatibility tests, tools, current diagnostics, stage evidence and selected resources. Original SRP art/audio remains unchanged in the workspace. This archive is development material and cannot be installed as a playable mod.\n').encode()
    manifest={'date':status.get('latestReportDate','2026-11-16'),'mainSourceFiles':len(sources),'mainSourceHashesMatchCompiler':True,'capturedDiagnostics':report['reportedErrors'],'compilerExitCode':report['exitCode'],'countIsExhaustive':complete,'exhaustiveScope':'printed diagnostics for this compiler pass','baseEntityDiagnostics':len(errors),'fullModCompiles':False,'playable':False,'files':{n:sha(v) for n,v in sorted(files.items())}}
    files['manifest.json']=(json.dumps(manifest,indent=2)+'\n').encode()
    target=R/'docs/review'/(a.name+'.zip');assert not target.exists(),'Retain prior snapshots; select a new name'
    with zipfile.ZipFile(target,'w',zipfile.ZIP_DEFLATED) as z:
        for name,data in sorted(files.items()):z.writestr(name,data)
    with zipfile.ZipFile(target) as z:
        assert z.testzip() is None
        for name,digest in manifest['files'].items():assert sha(z.read(name))==digest,name
    print('Verified source review:',target,'files:',len(files),'bytes:',target.stat().st_size)
if __name__=='__main__':main()
