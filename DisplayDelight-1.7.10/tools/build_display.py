"""Compile and reobfuscate against a configured native Forge 1.7.10 SDK."""
from pathlib import Path
import argparse,json,subprocess,zipfile,hashlib
R=Path(__file__).resolve().parents[1]
def call(args):subprocess.run(list(map(str,args)),cwd=R,check=True)
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--tests',action='store_true');a=parser.parse_args()
 c=json.loads((R/'.toolchain/toolchain.json').read_text(encoding='utf-8'));runtime=Path(c['runtimeClasspathFile']).read_text().strip();cp=c['forgeJar']+';'+runtime
 classes=R/'build'/('test-classes' if a.tests else 'classes');classes.mkdir(parents=True,exist_ok=True)
 sources=sorted((R/'src/main/java').rglob('*.java'))+(sorted((R/'src/test/java').rglob('*.java')) if a.tests else [])
 args=['-proc:none','-encoding','UTF-8','-source','8','-target','8','-classpath',cp,'-d',str(classes)]+list(map(str,sources))
 f=R/'.toolchain/javac.args';f.write_text('\n'.join('"'+v.replace('\\','/')+'"' for v in args)+'\n',encoding='utf-8');call([c['javac'],'@'+str(f)])
 dev=R/'build'/('displaydelight-tests-dev.jar' if a.tests else 'displaydelight-dev.jar')
 with zipfile.ZipFile(dev,'w',zipfile.ZIP_DEFLATED) as z:
  z.writestr('META-INF/MANIFEST.MF','Manifest-Version: 1.0\nImplementation-Title: Display Delight 1.7.10\nImplementation-Version: 1.7.0-backport.1\n\n')
  for root in [classes,R/'src/main/resources']:
   for p in sorted(root.rglob('*')):
    if p.is_file():z.write(p,p.relative_to(root).as_posix())
  for n in ['LICENSE.txt','COPYING','THIRD_PARTY.md']:z.write(R/n,'META-INF/'+n)
 base=Path(c['existingGradleCache'])/'caches/modules-2/files-2.1'
 def dep(g,n,v):return next((base/g/n/v).rglob('*.jar'))
 deps=[dep('com.anatawa12.forge','SpecialSource','1.11.1'),dep('org.ow2.asm','asm','9.5'),dep('org.ow2.asm','asm-commons','9.4'),dep('org.ow2.asm','asm-tree','9.5'),dep('net.sf.jopt-simple','jopt-simple','5.0.4'),dep('com.google.guava','guava','31.1-jre'),dep('com.opencsv','opencsv','5.7.1')]
 mapping=Path(c['srgToMcp']).with_name('mcp-srg.srg');out=R/'build/libs'/('displaydelight-1.7.10-tests.jar' if a.tests else 'displaydelight-1.7.10-1.7.0-backport.1.jar');out.parent.mkdir(exist_ok=True)
 call([c['java'],'-cp',';'.join(map(str,deps))+';'+str(classes)+';'+cp,'net.md_5.specialsource.SpecialSource','--live','--stable','--in-jar',dev,'--out-jar',out,'--srg-in',mapping])
 if not a.tests:
  with zipfile.ZipFile(R/'build/libs/displaydelight-1.7.10-1.7.0-backport.1-sources.zip','w',zipfile.ZIP_DEFLATED) as z:
   for root in [R/'src',R/'tools',R/'upstream/displaydelight-1.20.1/src']:
    for p in sorted(root.rglob('*')):
     if p.is_file() and '__pycache__' not in p.parts:z.write(p,p.relative_to(R).as_posix())
   for n in ['LICENSE.txt','COPYING','THIRD_PARTY.md','README.md','build.gradle','settings.gradle','gradle.properties','gradlew','gradlew.bat','gradle/wrapper/gradle-wrapper.jar','gradle/wrapper/gradle-wrapper.properties','build.ps1','docs/upstream.json','docs/asset-export.json','docs/TESTING.md']:
    if (R/n).exists():z.write(R/n,n)
 write={'jar':str(out),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'javaUnits':len(sources),'tests':a.tests,'compilerApi':c['forgeJar']}
 (R/'docs'/('test-build.json' if a.tests else 'build.json')).write_text(json.dumps(write,indent=2)+'\n');print(json.dumps(write,indent=2))
if __name__=='__main__':main()
