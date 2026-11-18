"""Validate navigation on native Forge on an actual Forge 1.7.10 dedicated server."""
from pathlib import Path
import json,subprocess,zipfile,datetime,shutil,argparse
R=Path(__file__).resolve().parents[1]
def invoke(args,**kw):
 p=subprocess.run(args,cwd=R,**kw)
 if p.returncode:raise SystemExit(p.returncode)
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--build-only',action='store_true');options=parser.parse_args()
 c=json.loads((R/'.toolchain/toolchain.json').read_text());cache=Path(c['existingGradleCache'])
 task=R/'.toolchain/navigation-tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S');classes=task/'classes';run=task/'run';classes.mkdir(parents=True);(run/'mods').mkdir(parents=True)
 runtime=Path(c['runtimeClasspathFile']).read_text().strip();cp=c.get('forgeCompileJar',c['forgeJar'])+';'+runtime
 files=sorted((R/'src/main/java/com/dhanantry/scapeandrunparasites/backport').rglob('*.java'))+[R/'src/compatTest/java'/name for name in ['NativeNavigationChecks.java', 'NativeNavigationClientChecks.java', 'NavigationTransformerChecks.java', 'NativeNavigationWorldChecks.java']]
 args=['-proc:none','-encoding','UTF-8','-source','8','-target','8','-classpath',cp,'-d',str(classes)]+[str(p) for p in files]
 argfile=task/'javac.args';argfile.write_text('\n'.join('"'+v.replace('\\','/')+'"' for v in args)+'\n')
 invoke([c['javac'],'@'+str(argfile)])
 invoke([c['java'],'-cp',str(classes)+';'+cp,'dev.srpbackport.tests.NavigationTransformerChecks',c['forgeJar'],str(R/'docs/navigation-transformer-checks.txt')])

 dev=task/'navigation-checks-dev.jar'
 with zipfile.ZipFile(dev,'w',zipfile.ZIP_DEFLATED) as jar:
  jar.writestr('META-INF/MANIFEST.MF','Manifest-Version: 1.0\nFMLAT: srp_at.cfg\nFMLCorePlugin: com.dhanantry.scapeandrunparasites.backport.potion.core.PotionFxLoadingPlugin\nFMLCorePluginContainsFMLMod: true\n\n')
  jar.write(R/'src/main/resources/META-INF/srp_at.cfg','META-INF/srp_at.cfg')
  for p in classes.rglob('*.class'):jar.write(p,p.relative_to(classes).as_posix())

 base=cache/'caches/modules-2/files-2.1'
 def dependency(group,artifact,version):return next((base/group/artifact/version).rglob('*.jar'))
 deps=[dependency('com.anatawa12.forge','SpecialSource','1.11.1'),dependency('org.ow2.asm','asm','9.5'),dependency('org.ow2.asm','asm-commons','9.4'),dependency('org.ow2.asm','asm-tree','9.5'),dependency('net.sf.jopt-simple','jopt-simple','5.0.4'),dependency('com.google.guava','guava','31.1-jre'),dependency('com.opencsv','opencsv','5.7.1')]
 remapCp=';'.join(map(str,deps))+';'+str(classes)+';'+cp
 jar=run/'mods/srp-navigation-checks.jar';mapping=Path(c['srgToMcp']).with_name('mcp-srg.srg')
 invoke([c['java'],'-cp',remapCp,'net.md_5.specialsource.SpecialSource','--live','--stable','--in-jar',str(dev),'--out-jar',str(jar),'--srg-in',str(mapping)])
 (R/'docs/native-navigation-build.json').write_text(json.dumps({'testJar':str(jar),'buildOnly':options.build_only,'installed':False},indent=2)+'\n',encoding='utf-8')
 if options.build_only:
  print('Disposable native navigation fixture built:',jar);return
 (run/'eula.txt').write_text('eula=true\n')
 (run/'server.properties').write_text('level-name=disposable-navigation\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nspawn-npcs=false\nmax-players=1\nallow-nether=false\nview-distance=3\nsnooper-enabled=false\n'.replace('\\n','\n'))
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
  report=active/('navigation-'+phase+'.txt')
  if process.returncode or not report.exists():print(log.read_text(encoding='utf-8',errors='replace')[-10000:]);raise SystemExit('Native navigation server failed; see '+str(log))
  result=report.read_text(encoding='utf-8');print(result,flush=True)
  shutil.copyfile(report,R/'docs'/('native-navigation-'+phase+'-checks.txt'))
  if result.splitlines()[-1]!='Failed: 0':raise SystemExit('Native navigation checks failed; see '+str(log))
 (R/'docs/native-navigation-run.json').write_text(json.dumps({'runDirectory':str(run),'logs':logs,'testJar':str(jar),'installed':False},indent=2)+'\n',encoding='utf-8')
 print('Native navigation tests passed in:',run)
if __name__=='__main__':main()
