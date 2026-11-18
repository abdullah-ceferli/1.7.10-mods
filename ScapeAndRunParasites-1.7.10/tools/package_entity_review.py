"""Create a source/diagnostic review snapshot only when source hashes and javac lines match."""
from pathlib import Path
import argparse,collections,hashlib,json,re,shutil,zipfile
R=Path(__file__).resolve().parents[1]
def digest(path):return hashlib.sha256(path.read_bytes()).hexdigest()
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--name',default='entity-parasite-base-2026-11-15');a=parser.parse_args();assert re.fullmatch('[a-z0-9-]+',a.name)
 report_path=R/'docs/compile-full-inventory.json';report=json.loads(report_path.read_text(encoding='utf-8'))
 assert report.get('sourceSnapshotStable') is True,'Re-run the full probe with stable source hashes first.'
 source=next((R/'src/main/java').rglob('EntityParasiteBase.java'));relative=source.relative_to(R).as_posix();sha=digest(source)
 assert report['sourceFiles'][relative]['sha256']==sha,'Source changed since compilation. Re-run the probe.'
 lines=source.read_text(encoding='utf-8').splitlines();rows=[r for r in report['errors'] if r['file'].replace('\\','/')==relative];assert rows
 enriched=[]
 for row in rows:
  index=row['line']-1;assert row['details'].splitlines()[0].strip()==lines[index].strip(),'Printed javac line differs from source at '+str(row['line'])
  copy=dict(row);copy['sourceLine']=lines[index];copy['context']=[{'line':i+1,'text':lines[i]} for i in range(max(0,index-3),min(len(lines),index+4))];enriched.append(copy)
 out=R/'docs/review'/a.name;out.mkdir(parents=True,exist_ok=False)
 shutil.copyfile(source,out/'EntityParasiteBase.java')
 (out/'EntityParasiteBase.numbered.txt').write_text('\n'.join(str(n).rjust(5)+'  '+line for n,line in enumerate(lines,1))+'\n',encoding='utf-8')
 specific={'sourcePath':relative,'snapshotFile':'EntityParasiteBase.java','sourceSha256':sha,'lineCount':len(lines),'diagnosticSchemaVersion':report['diagnosticSchemaVersion'],'reportedErrorsForFile':len(rows),'sourceLineMatchesVerified':True,'errors':enriched}
 (out/'EntityParasiteBase.errors.json').write_text(json.dumps(specific,indent=2)+'\n',encoding='utf-8')
 (out/'EntityParasiteBase.errors.log').write_text('\n\n'.join(row['file']+':'+str(row['line'])+': error: '+row['message']+'\n'+row['details'] for row in rows)+'\n',encoding='utf-8')
 for name in ['compile-full-inventory.json','compile-full-inventory.log','ENTITY_REVIEW.md','entity-common-diagnostic-comparison.json','compile-access.json','DIFFICULTY_PORT.md','difficulty-conversion.json']:
  shutil.copyfile(R/'docs'/name,out/name)
 for name in ['compile_probe.py','rank_diagnostics.py']:
  (out/'tools').mkdir(exist_ok=True);shutil.copyfile(R/'tools'/name,out/'tools'/name)
 for path in (R/'src/main/java/com/dhanantry/scapeandrunparasites/backport').rglob('*.java'):
  target=out/'compatibility'/path.relative_to(R/'src/main/java');target.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(path,target)
 (out/'META-INF').mkdir();shutil.copyfile(R/'src/main/resources/META-INF/srp_at.cfg',out/'META-INF/srp_at.cfg')
 shutil.copyfile(R/'build.gradle',out/'build.gradle')
 named=sum('symbol' in row for row in rows);locations=sum('location' in row for row in rows)
 (out/'README.md').write_text("""# Matched converted-source review snapshot

This is the current converted EntityParasiteBase, not the preserved original reference source.

Original project path: """+relative+"""

The CFR header remains as provenance in the converted source. It must not be used to classify the file as original. The actual imports and method calls below the package statement contain the conversion. No lines were removed from the snapshot.

The source SHA256 is """+sha+""".

All """+str(len(rows))+""" diagnostics for this file were checked against the exact source line printed by javac. The raw log and JSON come from the same fresh full compile. Compiler source hashes were identical before and after that run.

Files:

- EntityParasiteBase.java: byte-for-byte copy of the compiled converted file.
- EntityParasiteBase.numbered.txt: human-readable numbered copy; do not compile it.
- EntityParasiteBase.errors.json: only this file's diagnostics, including symbol/location/overload details, exact source lines and context.
- EntityParasiteBase.errors.log: the same filtered raw diagnostics.
- compile-full-inventory.json and .log: full fresh compiler output with source hashes and complete details.
- compatibility/: current shared adapters, included to prevent duplicating or replacing work already implemented.
- ENTITY_REVIEW.md: source-checked assessment of the suggested conversions.

The position, block-state and entity-data boundaries are already connected. Entity data uses a tested typed manager and Forge transport, separate from native DataWatcher's limited ID space. Do not redesign those fields from the original reference file.

Local difficulty and the shared native spawn bridge are implemented; see DIFFICULTY_PORT.md. Remaining effects and advancement/progression code still require behavior-preserving conversion. Passing potion.id alone loses the fifth particle-visibility flag; the global difficulty enum is not a complete replacement for local difficulty. Keep these limitations visible.

The full mod still fails compilation. The compiler reports """+str(report['reportedErrors'])+""" diagnostics and exits abnormally with code """+str(report['exitCode'])+"""; the count is not exhaustive. This snapshot is development material, not a playable mod.
""",encoding='utf-8')
 manifest={'sourcePath':relative,'sourceSha256':sha,'sourceLineCount':len(lines),'reportedErrorsForFile':len(rows),'diagnosticsWithSymbol':named,'diagnosticsWithLocation':locations,'allDiagnosticSourceLinesMatch':True,'compilerSourceSnapshotStable':True,'fullCompilerReportedDiagnostics':report['reportedErrors'],'compilerExitCode':report['exitCode'],'fullModCompiles':False,'playable':False,'files':{p.relative_to(out).as_posix():digest(p) for p in sorted(out.rglob('*')) if p.is_file()}}
 (out/'manifest.json').write_text(json.dumps(manifest,indent=2)+'\n',encoding='utf-8')
 archive=out.with_suffix('.zip')
 with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as z:
  for path in sorted(out.rglob('*')):
   if path.is_file():z.write(path,path.relative_to(out).as_posix())
 with zipfile.ZipFile(archive) as z:
  assert hashlib.sha256(z.read('EntityParasiteBase.java')).hexdigest()==sha
  for name,expected in manifest['files'].items():assert hashlib.sha256(z.read(name)).hexdigest()==expected,name
 print('Matched source:',source);print('Errors:',len(rows),'Symbols:',named,'Locations:',locations);print('Review bundle:',archive)
if __name__=='__main__':main()
