"""Snapshot verified reports and source hashes; do not turn partial compatibility into a release claim."""
from pathlib import Path
import json,hashlib,shutil,zipfile,collections
R=Path(__file__).resolve().parents[1]
def digest(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
 prior=R/'docs/test-results/2026-11-14-compatibility';old=json.loads((prior/'manifest.json').read_text())
 for path,expected in old['coreSources'].items():assert digest(R/path)==expected,'Previously tested core changed: '+path
 for name in ['rendering-checks.txt','sound-checks.txt']:assert digest(prior/name)==digest(R/'docs'/name),'Previous rendering/sound evidence changed'
 names=['foundation-checks.txt','rendering-checks.txt','sound-checks.txt','state-checks.txt','native-state-initial-checks.txt','native-state-restart-checks.txt']
 groups={}
 for name in names:
  text=(R/'docs'/name).read_text();assert text.rstrip().endswith('Failed: 0'),name
  groups[name]=sum(line.startswith('PASS:') for line in text.splitlines())
 assert sum(groups.values())==40,groups
 probe=json.loads((R/'docs/compile-probe.json').read_text());assert probe['exitCode']!=0 and probe['reportedErrors']==2000
 sources=sorted((R/'src/main/java').rglob('*.java'));core=sorted((R/'src/main/java/com/dhanantry/scapeandrunparasites/backport').rglob('*.java'));assert len(sources)==1115 and len(core)==21
 native=json.loads((R/'docs/native-state-run.json').read_text());jar=Path(native['testJar']);assert '.toolchain/block-tests/' in jar.as_posix()
 with zipfile.ZipFile(jar) as archive:assert not any(x.startswith('net/minecraft/') for x in archive.namelist()),'Minecraft classes bundled in test mod'
 output=R/'docs/test-results/2026-11-14-block-states';output.mkdir(parents=True,exist_ok=True)
 files=names+['world-state-conversion.json','world-state-calls.tsv','native-state-run.json','foundation-member-contract.tsv','compile-probe.json','compile-probe.log']
 for name in files:shutil.copyfile(R/'docs'/name,output/name)
 for phase,source in zip(['initial','restart'],native['logs']):shutil.copyfile(source,output/('native-server-'+phase+'.log'))
 manifest={'date':'2026-11-14','stage':'block-state-metadata-integration','verificationGroupsPassed':sum(groups.values()),'groupsByReport':groups,'compatibilitySourceUnits':len(core),'javaSourceUnits':len(sources),'originalSourceUnitsPreserved':1094,'originalAssetsPreserved':4501,'referencedFoundationMembersVerified':210,'nativeTarget':'Minecraft 1.7.10 / Forge 10.13.4.1614 / Java 8','nativeMetadataValuesTested':16,'nativeWorldReloadedInSecondJvm':True,'nativeTestJarSha256':digest(jar),'nativeRunDirectory':native['runDirectory'],'referenceMinecraft112Sha1':'0f275bc1547d01fa5f56ba34bdc87d981ee12daf','referenceGuava21Sha1':'3a3d111be1be1b745edfa7d91678a12d7ed38709','worldStateCallsConverted':1740,'sourceUnitsWithConvertedCalls':222,'fullModCompiles':False,'playable':False,'installedIntoMinecraft':False,'fullCompilerExitCode':probe['exitCode'],'reportedCompilerDiagnostics':probe['reportedErrors'],'compilerDiagnosticLimit':2000,'compilerCountIsCapped':True,'coreSources':{p.relative_to(R).as_posix():digest(p) for p in core},'evidenceFiles':{p.name:digest(p) for p in output.iterdir() if p.is_file() and p.name!='manifest.json'},'limitations':['Vanilla named/derived states, rotations and mirrors need explicit codecs.','Specialized SRP block parents and neighbor source positions are not ported.','Read-only legacy collision/drop defaults currently require a World.','Remaining registries, entities, items, persistence, world/client APIs and mixins prevent a full mod build.'],'previousRenderingAndSoundEvidence':str(prior/'manifest.json')}
 (output/'manifest.json').write_text(json.dumps(manifest,indent=2)+'\n',encoding='utf-8')
 print('Recorded',sum(groups.values()),'passing groups; full-mod compilation remains unsuccessful.')
 print('Evidence:',output/'manifest.json')
if __name__=='__main__':main()
