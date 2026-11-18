"""Compile the conversion against actual Forge 1.7.10 and preserve honest diagnostics."""
from pathlib import Path
import subprocess,json,re,argparse,hashlib
ROOT=Path(__file__).resolve().parents[1]
HEADER=re.compile(r'^(.+\.java):(\d+): error: (.+)$',re.M)

def parse_diagnostics(data,root=ROOT):
 matches=list(HEADER.finditer(data));results=[]
 for i,match in enumerate(matches):
  path,line,message=match.groups();end=matches[i+1].start() if i+1<len(matches) else len(data)
  details=data[match.end():end].strip('\r\n')
  row={'file':str(Path(path).relative_to(root)),'line':int(line),'message':message,'details':details}
  for label in ('symbol','location','required','found','reason'):
   value=re.search(r'^\s*'+label+r':\s*(.+)$',details,re.M)
   if value:row[label]=value.group(1).strip()
  if 'symbol' in row:
   kind,_,name=row['symbol'].partition(' ');row['symbolKind']=kind;row['symbolName']=name
  results.append(row)
 return results

def compiler_completion(returncode,parsed_count,limit,data):
 totals=re.findall(r'^(\d+) errors?\s*$',data,re.M)
 total=int(totals[-1]) if totals else (0 if returncode==0 and parsed_count==0 else None)
 matched=total is not None and total==parsed_count
 complete=returncode in (0,1) and parsed_count<limit and matched
 failure=re.search(r'^(java\.[\w.]+(?:Error|Exception)(?::.*)?)$',data,re.M)
 return {'compilerReportedErrorTotal':total,'allPrintedErrorsParsed':matched,'diagnosticPassCompleted':complete,'countIsExhaustiveForThisCompilerPass':complete,'compilerFailure':failure.group(1) if failure else None}

def snapshot_sources(files,root=ROOT):
 snapshot={}
 for path in files:
  data=path.read_bytes()
  snapshot[path.relative_to(root).as_posix()]={'sha256':hashlib.sha256(data).hexdigest(),'lineCount':len(data.decode('utf-8').splitlines())}
 return snapshot

def main():
 parser=argparse.ArgumentParser();parser.add_argument('--limit',type=int,default=2000);parser.add_argument('--name',default='compile-probe');parser.add_argument('--parse-only',action='store_true',help='Enrich the saved JSON from its existing complete log without recompiling.');options=parser.parse_args()
 assert 1<=options.limit<=100000 and re.fullmatch(r'[a-z][a-z0-9-]*',options.name),'Invalid diagnostic limit or report name'
 config=json.loads((ROOT/'.toolchain/toolchain.json').read_text(encoding='utf-8'))
 log=ROOT/'docs'/(options.name+'.log');report_path=ROOT/'docs'/(options.name+'.json')
 if options.parse_only:
  report=json.loads(report_path.read_text(encoding='utf-8'));returncode=report['exitCode']
 else:
  files=sorted((ROOT/'src/main/java').rglob('*.java'));assert files
  sources_before=snapshot_sources(files)
  out=ROOT/'build/compatibility-probe';out.mkdir(parents=True,exist_ok=True)
  cp=[config.get('forgeCompileJar',config['forgeJar']),Path(config['runtimeClasspathFile']).read_text().strip(),str(ROOT/'.toolchain/upstream-libraries.jar')]
  args=['-XDdev','-proc:none','-encoding','UTF-8','-source','8','-target','8','-Xmaxerrs',str(options.limit),'-Xmaxwarns','10','-classpath',';'.join(cp),'-d',str(out)]+[str(p) for p in files]
  argfile=ROOT/'.toolchain/javac-probe.args';argfile.write_text('\n'.join('"'+a.replace('\\','/')+'"' for a in args)+'\n',encoding='utf-8')
  with log.open('w',encoding='utf-8') as f:result=subprocess.run([config['javac'],'-J-Xmx4G','@'+str(argfile)],cwd=ROOT,stdout=f,stderr=subprocess.STDOUT)
  returncode=result.returncode
  sources_after=snapshot_sources(files)
  report={'exitCode':returncode,'javaSourceUnits':len(files),'maximumErrorsRequested':options.limit,'compileApiJar':config.get('forgeCompileJar',config['forgeJar']),'sourceSnapshotStable':sources_before==sources_after,'sourceFiles':sources_before}
 data=log.read_text(encoding='utf-8',errors='replace');diagnostics=parse_diagnostics(data)
 report.update({'diagnosticSchemaVersion':4,'reportedErrors':len(diagnostics),'reachedDiagnosticLimit':len(diagnostics)>=report['maximumErrorsRequested'],'compilerStoppedAbnormally':returncode not in (0,1),'note':'Diagnostic probe, not a release build. Counts can be capped or interrupted by abnormal compiler termination and are not a remaining-work estimate.','errors':diagnostics})
 report.update(compiler_completion(returncode,len(diagnostics),report['maximumErrorsRequested'],data))
 report_path.write_text(json.dumps(report,indent=2)+'\n',encoding='utf-8')
 print('Compiler exit:',returncode,'Source units:',report['javaSourceUnits'],'Reported errors:',len(diagnostics));print('Full diagnostics:',log)
 print('Named symbols:',sum('symbol' in row for row in diagnostics),'Locations:',sum('location' in row for row in diagnostics))
 return 0 if options.parse_only else returncode
if __name__=='__main__':raise SystemExit(main())
