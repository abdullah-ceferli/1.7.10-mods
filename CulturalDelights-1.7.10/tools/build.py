"""Compile against Java 8 / Forge 1614 and reobfuscate native release classes."""
from pathlib import Path
import argparse,hashlib,json,shutil,subprocess,zipfile
R=Path(__file__).resolve().parents[1]
def main():
 p=argparse.ArgumentParser();p.add_argument('--tests',action='store_true');a=p.parse_args()
 c=json.loads((R/'.toolchain/toolchain.json').read_text());meta=json.loads((R/'docs/upstream.json').read_text());runtime=Path(c['runtimeClasspathFile']).read_text().strip();cache=Path(c['existingGradleCache'])/'caches/modules-2/files-2.1'
 def dep(g,n,v):return next((cache/g/n/v).rglob('*.jar'))
 deps=[dep('com.anatawa12.forge','SpecialSource','1.11.1'),dep('org.ow2.asm','asm','9.5'),dep('org.ow2.asm','asm-commons','9.4'),dep('org.ow2.asm','asm-tree','9.5'),dep('net.sf.jopt-simple','jopt-simple','5.0.4'),dep('com.google.guava','guava','31.1-jre'),dep('com.opencsv','opencsv','5.7.1')]
 ss=';'.join(map(str,deps));fd=Path(c.get('farmersDelightDevJar',str(R/'.toolchain/farmersdelight-dev.jar')));fd=fd if fd.exists() or c.get('farmersDelightDevJar') else R.parent/'HandsOnDelight-1.7.10/.toolchain/farmersdelight-dev.jar';cp=c['forgeJar']+';'+runtime+';'+str(fd)
 classes=R/'build'/('test-classes' if a.tests else 'classes');classes=classes.resolve()
 if not classes.is_relative_to((R/'build').resolve()):raise RuntimeError('Build output escaped project')
 if classes.exists():shutil.rmtree(classes)
 classes.mkdir(parents=True)
 sources=sorted((R/'src/main/java').rglob('*.java'))+(sorted((R/'src/test/java').rglob('*.java')) if a.tests else [])
 args=['-encoding','UTF-8','-proc:none','-source','8','-target','8','-Xlint:unchecked','-cp',cp,'-d',str(classes)]+list(map(str,sources))
 f=R/'.toolchain/javac.args';f.write_text('\n'.join('"'+v.replace('\\','/')+'"' for v in args)+'\n')
 subprocess.run([c['javac'],'@'+str(f)],cwd=R,check=True)
 dev=R/'build/dev.jar';mod=meta['mod'];pkg='com.epherical.croptopia.legacy' if mod=='croptopia' else 'com.baisylia.culturaldelights.legacy'
 with zipfile.ZipFile(dev,'w',zipfile.ZIP_DEFLATED) as z:
  manifest='Manifest-Version: 1.0\nImplementation-Version: '+meta['version']+'-backport.1\n'
  if mod=='croptopia':manifest+='FMLCorePlugin: '+pkg+'.LoadingPlugin\nFMLCorePluginContainsFMLMod: true\n'
  z.writestr('META-INF/MANIFEST.MF',manifest+'\n')
  for root in [classes,R/'src/main/resources']:
   for source in sorted(root.rglob('*')):
    if source.is_file():z.write(source,source.relative_to(root).as_posix())
  for file in (R/'licenses').glob('*'):z.write(file,'META-INF/'+file.name)
 out=R/'build/libs'/(mod+('-tests' if a.tests else '-1.7.10-'+meta['version']+'-backport.1')+'.jar');out.parent.mkdir(exist_ok=True)
 subprocess.run([c['java'],'-cp',ss+';'+str(classes)+';'+cp,'net.md_5.specialsource.SpecialSource','--live','--stable','--in-jar',str(dev),'--out-jar',str(out),'--srg-in',str(Path(c['srgToMcp']).with_name('mcp-srg.srg'))],cwd=R,check=True)
 record={'jar':str(out),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'javaUnits':len(sources),'tests':a.tests,'nativeApi':c['forgeJar']};(R/'docs'/('test-build.json' if a.tests else 'build.json')).write_text(json.dumps(record,indent=2)+'\n');print(json.dumps(record,indent=2))
if __name__=='__main__':main()
