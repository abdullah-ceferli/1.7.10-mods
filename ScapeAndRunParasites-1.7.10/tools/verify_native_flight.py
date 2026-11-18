"""Compare extracted SRP flying controllers and math against original bytecode on native Forge."""
from pathlib import Path
import json,subprocess,zipfile,datetime,shutil,argparse
R=Path(__file__).resolve().parents[1]
def invoke(args,**kw):
 p=subprocess.run(args,cwd=R,**kw)
 if p.returncode:raise SystemExit(p.returncode)
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--build-only',action='store_true');options=parser.parse_args()
 c=json.loads((R/'.toolchain/toolchain.json').read_text());cache=Path(c['existingGradleCache'])
 task=R/'.toolchain/flight-tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S');classes=task/'classes';run=task/'run';classes.mkdir(parents=True);(run/'mods').mkdir(parents=True)
 runtime=Path(c['runtimeClasspathFile']).read_text().strip();cp=c.get('forgeCompileJar',c['forgeJar'])+';'+runtime
 tools=Path(c['java']).parent.parent/'lib/tools.jar';sourceTools=R/'.toolchain/source-tools'
 invoke([c['javac'],'-cp',str(tools),'-d',str(sourceTools),str(R/'tools/FlightCalls.java')])
 generated=task/'generated'
 invoke([c['java'],'-cp',str(sourceTools)+';'+str(tools),'dev.srpbackport.tools.FlightCalls',str(R/'src/main/java'),str(generated),str(task/'unused-audit.tsv'),'--extract-only'])
 files=sorted((R/'src/main/java/com/dhanantry/scapeandrunparasites/backport').rglob('*.java'))+list(generated.glob('*.java'))+[R/'src/compatTest/java/NativeFlightChecks.java',R/'src/compatTest/java/FlightActor.java',R/'src/compatTest/java/FlightReferenceAccess.java',R/'src/compatTest/java/FlightReferenceBuilder.java']
 args=['-proc:none','-encoding','UTF-8','-source','8','-target','8','-classpath',cp,'-d',str(classes)]+[str(p) for p in files]
 argfile=task/'javac.args';argfile.write_text('\n'.join('"'+v.replace('\\','/')+'"' for v in args)+'\n')
 invoke([c['javac'],'@'+str(argfile)])

 import csv,io
 with zipfile.ZipFile(R/'.toolchain/mcp_stable-39-1.12.zip') as names:
  converted={row['searge']:row['name'] for f in ['fields.csv','methods.csv'] for row in csv.DictReader(io.StringIO(names.read(f).decode()))}
 props=task/'reference-names.properties';props.write_text('\n'.join(k+'='+v for k,v in converted.items())+'\n',encoding='utf-8')
 invoke([c['java'],'-cp',str(classes)+';'+cp,'dev.srpbackport.tests.FlightReferenceBuilder',str(R/'.toolchain/minecraft-1.12.2-srg-reference.jar'),str(R/'upstream/SRParasites-1.10.9-forgecdn.jar'),str(props),str(generated/'controllers.properties'),str(classes)])

 dev=task/'flight-checks-dev.jar'
 with zipfile.ZipFile(dev,'w',zipfile.ZIP_DEFLATED) as jar:
  jar.writestr('META-INF/MANIFEST.MF','Manifest-Version: 1.0\nFMLAT: srp_at.cfg\nFMLCorePlugin: com.dhanantry.scapeandrunparasites.backport.potion.core.PotionFxLoadingPlugin\nFMLCorePluginContainsFMLMod: true\n\n')
  jar.write(R/'src/main/resources/META-INF/srp_at.cfg','META-INF/srp_at.cfg')
  for p in classes.rglob('*.class'):jar.write(p,p.relative_to(classes).as_posix())
  jar.write(generated/'controllers.properties','flight-controllers.properties')

 base=cache/'caches/modules-2/files-2.1'
 def dependency(group,artifact,version):return next((base/group/artifact/version).rglob('*.jar'))
 deps=[dependency('com.anatawa12.forge','SpecialSource','1.11.1'),dependency('org.ow2.asm','asm','9.5'),dependency('org.ow2.asm','asm-commons','9.4'),dependency('org.ow2.asm','asm-tree','9.5'),dependency('net.sf.jopt-simple','jopt-simple','5.0.4'),dependency('com.google.guava','guava','31.1-jre'),dependency('com.opencsv','opencsv','5.7.1')]
 remapCp=';'.join(map(str,deps))+';'+str(classes)+';'+cp
 jar=run/'mods/srp-flight-checks.jar';mapping=Path(c['srgToMcp']).with_name('mcp-srg.srg')
 invoke([c['java'],'-cp',remapCp,'net.md_5.specialsource.SpecialSource','--live','--stable','--in-jar',str(dev),'--out-jar',str(jar),'--srg-in',str(mapping)])
 (R/'docs/native-flight-build.json').write_text(json.dumps({'testJar':str(jar),'buildOnly':options.build_only,'installed':False},indent=2)+'\n',encoding='utf-8')
 if options.build_only:
  print('Disposable native flight fixture built:',jar);return
 (run/'eula.txt').write_text('eula=true\n')
 (run/'server.properties').write_text('level-name=disposable-flight\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nspawn-npcs=false\nmax-players=1\nallow-nether=false\nview-distance=3\nsnooper-enabled=false\n'.replace('\\n','\n'))
 native=Path(c['java']).parents[3]/'forge-universal.jar'
 server=cache/'caches/minecraft/net/minecraft/minecraft_server/1.7.10/minecraft_server-1.7.10.jar'
 nativeCp=str(native)+';'+str(server)+';'+runtime
 logs=[]
 for phase in ['initial']:
  active=run
  log=task/(phase+'.log');logs.append(str(log));print('Running native Forge phase:',phase,flush=True)
  command=[c['java'],'-Xms256M','-Xmx2G']
  command+=['-cp',nativeCp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLServerTweaker','nogui']
  with log.open('w',encoding='utf-8') as output:
   process=subprocess.run(command,cwd=active,stdout=output,stderr=subprocess.STDOUT,timeout=180)
  report=active/('flight-'+phase+'.txt')
  if process.returncode or not report.exists():print(log.read_text(encoding='utf-8',errors='replace')[-10000:]);raise SystemExit('Native flight server failed; see '+str(log))
  result=report.read_text(encoding='utf-8');print(result,flush=True)
  shutil.copyfile(report,R/'docs'/('native-flight-'+phase+'-checks.txt'))
  if result.splitlines()[-1]!='Failed: 0':raise SystemExit('Native flight checks failed; see '+str(log))
 (R/'docs/native-flight-run.json').write_text(json.dumps({'runDirectory':str(run),'logs':logs,'testJar':str(jar),'installed':False},indent=2)+'\n',encoding='utf-8')
 print('Native flight tests passed in:',run)
if __name__=='__main__':main()
