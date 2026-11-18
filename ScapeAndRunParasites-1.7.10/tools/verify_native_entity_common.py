"""Build a test-only mod and run real Forge 1.7.10 plus a genuine restart in a fresh world."""
from pathlib import Path
import json,subprocess,zipfile,datetime,shutil
from verify_entity_query_calls import generate
R=Path(__file__).resolve().parents[1]
def invoke(args,**kw):
 p=subprocess.run(args,cwd=R,**kw)
 if p.returncode:raise SystemExit(p.returncode)
def main():
 c=json.loads((R/'.toolchain/toolchain.json').read_text());cache=Path(c['existingGradleCache'])
 task=R/'.toolchain/entity-common-tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S');classes=task/'classes';run=task/'run';classes.mkdir(parents=True);(run/'mods').mkdir(parents=True)
 runtime=Path(c['runtimeClasspathFile']).read_text().strip();cp=c.get('forgeCompileJar',c['forgeJar'])+';'+runtime
 files=sorted((R/'src/main/java/com/dhanantry/scapeandrunparasites/backport').rglob('*.java'))+[R/'src/compatTest/java/NativeEntityCommonChecks.java',R/'src/compatTest/java/NativeEntityCommonClientChecks.java',generate(task)]
 args=['-proc:none','-encoding','UTF-8','-source','8','-target','8','-classpath',cp,'-d',str(classes)]+[str(p) for p in files]
 argfile=task/'javac.args';argfile.write_text('\n'.join('"'+v.replace('\\','/')+'"' for v in args)+'\n')
 invoke([c['javac'],'@'+str(argfile)])
 dev=task/'entity-common-checks-dev.jar'
 with zipfile.ZipFile(dev,'w',zipfile.ZIP_DEFLATED) as jar:
  jar.writestr('META-INF/MANIFEST.MF','Manifest-Version: 1.0\nFMLAT: srp_at.cfg\n\n')
  jar.write(R/'src/main/resources/META-INF/srp_at.cfg','META-INF/srp_at.cfg')
  for p in classes.rglob('*.class'):jar.write(p,p.relative_to(classes).as_posix())
 base=cache/'caches/modules-2/files-2.1'
 def dependency(group,artifact,version):return next((base/group/artifact/version).rglob('*.jar'))
 deps=[dependency('com.anatawa12.forge','SpecialSource','1.11.1'),dependency('org.ow2.asm','asm','9.5'),dependency('org.ow2.asm','asm-commons','9.4'),dependency('org.ow2.asm','asm-tree','9.5'),dependency('net.sf.jopt-simple','jopt-simple','5.0.4'),dependency('com.google.guava','guava','31.1-jre'),dependency('com.opencsv','opencsv','5.7.1')]
 remapCp=';'.join(map(str,deps))+';'+str(classes)+';'+cp
 jar=run/'mods/srp-entity-common-checks.jar';mapping=Path(c['srgToMcp']).with_name('mcp-srg.srg')
 invoke([c['java'],'-cp',remapCp,'net.md_5.specialsource.SpecialSource','--live','--stable','--in-jar',str(dev),'--out-jar',str(jar),'--srg-in',str(mapping)])
 (run/'eula.txt').write_text('eula=true\n')
 (run/'server.properties').write_text('level-name=disposable-entity-common\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nspawn-npcs=false\nmax-players=1\nallow-nether=false\nview-distance=3\nsnooper-enabled=false\n'.replace('\\n','\n'))
 native=Path(c['java']).parents[3]/'forge-universal.jar'
 server=cache/'caches/minecraft/net/minecraft/minecraft_server/1.7.10/minecraft_server-1.7.10.jar'
 nativeCp=str(native)+';'+str(server)+';'+runtime
 for phase in ['initial']:
  log=task/(phase+'.log');print('Running native Forge phase:',phase,flush=True)
  with log.open('w') as output:
   process=subprocess.run([c['java'],'-Xms256M','-Xmx2G','-cp',nativeCp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLServerTweaker','nogui'],cwd=run,stdout=output,stderr=subprocess.STDOUT,timeout=180)
  report=run/('entity-common-'+phase+'.txt')
  if process.returncode or not report.exists():print(log.read_text(errors='replace')[-10000:]);raise SystemExit('Native entity-common server failed; see '+str(log))
  print(report.read_text(),flush=True)
  shutil.copyfile(report,R/'docs'/('native-entity-common-'+phase+'-checks.txt'))
  if report.read_text().splitlines()[-1]!='Failed: 0':raise SystemExit('Native entity-common checks failed; see '+str(log))
 (R/'docs/native-entity-common-run.json').write_text(json.dumps({'runDirectory':str(run),'logs':[str(task/(p+'.log')) for p in ['initial']],'testJar':str(jar),'installed':False},indent=2)+'\n')
 print('Native entity-common tests passed in:',run)
if __name__=='__main__':main()
