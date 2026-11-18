"""Verify particle against original particle handler and actual native Forge damage/restart paths."""
from pathlib import Path
import json,subprocess,zipfile,datetime,shutil,argparse
R=Path(__file__).resolve().parents[1]
def invoke(args,**kw):
 p=subprocess.run(args,cwd=R,**kw)
 if p.returncode:raise SystemExit(p.returncode)
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--build-only',action='store_true');options=parser.parse_args()
 c=json.loads((R/'.toolchain/toolchain.json').read_text());cache=Path(c['existingGradleCache'])
 task=R/'.toolchain/particle-tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S');classes=task/'classes';run=task/'run';classes.mkdir(parents=True);(run/'mods').mkdir(parents=True)
 runtime=Path(c['runtimeClasspathFile']).read_text().strip();cp=c.get('forgeCompileJar',c['forgeJar'])+';'+runtime
 files=sorted((R/'src/main/java/com/dhanantry/scapeandrunparasites/backport').rglob('*.java'))+[R/'src/compatTest/java/NativeParticleChecks.java',R/'src/compatTest/java/NativeParticleClientChecks.java',R/'src/compatTest/java/ParticleTransformerChecks.java',R/'src/compatTest/java/ParticleReferenceChecks.java',R/'src/compatTest/java/PotionFxTransformerChecks.java']
 args=['-proc:none','-encoding','UTF-8','-source','8','-target','8','-classpath',cp,'-d',str(classes)]+[str(p) for p in files]
 argfile=task/'javac.args';argfile.write_text('\n'.join('"'+v.replace('\\','/')+'"' for v in args)+'\n')
 invoke([c['javac'],'@'+str(argfile)])
 mappings=[x.split() for x in (R/'.toolchain/joined-1.12.2.srg').read_text(encoding='utf-8').splitlines() if x.strip()]
 class_names={x[2]:x[1] for x in mappings if x[0]=='CL:'}
 values={'enum':class_names['net/minecraft/util/EnumParticleTypes'],'handler':class_names['net/minecraft/client/network/NetHandlerPlayClient'],'packet':class_names['net/minecraft/network/play/server/SPacketParticles']}
 for key,srg,owner in [('handle','func_147289_a','net/minecraft/client/network/NetHandlerPlayClient'),('name','func_179346_b','net/minecraft/util/EnumParticleTypes'),('id','func_179348_c','net/minecraft/util/EnumParticleTypes'),('ignore','func_179344_e','net/minecraft/util/EnumParticleTypes'),('arguments','func_179345_d','net/minecraft/util/EnumParticleTypes')]:
  values[key]=next(x[1].rsplit('/',1)[1] for x in mappings if x[0]=='MD:' and x[3]==owner+'/'+srg)
 props=task/'reference.properties';props.write_text('\n'.join(k+'='+v for k,v in values.items())+'\n',encoding='utf-8')
 invoke([c['java'],'-cp',str(classes)+';'+cp+';'+str(R/'.toolchain/minecraft-1.12.2-client.jar'),'dev.srpbackport.tests.ParticleReferenceChecks',str(props),str(R/'.toolchain/minecraft-1.12.2-client.jar'),str(R/'docs/particle-reference-checks.txt')])
 invoke([c['java'],'-cp',str(classes)+';'+cp,'dev.srpbackport.tests.ParticleTransformerChecks',c.get('forgeCompileJar',c['forgeJar']),str(R/'docs/particle-transformer-checks.txt')])
 dev=task/'particle-checks-dev.jar'
 with zipfile.ZipFile(dev,'w',zipfile.ZIP_DEFLATED) as jar:
  jar.writestr('META-INF/MANIFEST.MF','Manifest-Version: 1.0\nFMLAT: srp_at.cfg\nFMLCorePlugin: com.dhanantry.scapeandrunparasites.backport.potion.core.PotionFxLoadingPlugin\nFMLCorePluginContainsFMLMod: true\n\n')
  jar.write(R/'src/main/resources/META-INF/srp_at.cfg','META-INF/srp_at.cfg')
  for p in classes.rglob('*.class'):jar.write(p,p.relative_to(classes).as_posix())
  textures=json.loads((R/'docs/particle-texture-provenance.json').read_text(encoding='utf-8'))['resources']
  for name in textures:jar.write(R/name,str(Path(name).relative_to('src/main/resources')).replace('\\','/'))
  jar.writestr('particle-assets.properties','\n'.join(Path(name).name+'='+data['sha256'] for name,data in textures.items())+'\n')
 base=cache/'caches/modules-2/files-2.1'
 def dependency(group,artifact,version):return next((base/group/artifact/version).rglob('*.jar'))
 deps=[dependency('com.anatawa12.forge','SpecialSource','1.11.1'),dependency('org.ow2.asm','asm','9.5'),dependency('org.ow2.asm','asm-commons','9.4'),dependency('org.ow2.asm','asm-tree','9.5'),dependency('net.sf.jopt-simple','jopt-simple','5.0.4'),dependency('com.google.guava','guava','31.1-jre'),dependency('com.opencsv','opencsv','5.7.1')]
 remapCp=';'.join(map(str,deps))+';'+str(classes)+';'+cp
 jar=run/'mods/srp-particle-checks.jar';mapping=Path(c['srgToMcp']).with_name('mcp-srg.srg')
 invoke([c['java'],'-cp',remapCp,'net.md_5.specialsource.SpecialSource','--live','--stable','--in-jar',str(dev),'--out-jar',str(jar),'--srg-in',str(mapping)])
 (R/'docs/native-particle-build.json').write_text(json.dumps({'testJar':str(jar),'buildOnly':options.build_only,'installed':False},indent=2)+'\n',encoding='utf-8')
 if options.build_only:
  print('Disposable native particle fixture built:',jar);return
 (run/'eula.txt').write_text('eula=true\n')
 (run/'server.properties').write_text('level-name=disposable-particle\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nspawn-npcs=false\nmax-players=1\nallow-nether=false\nview-distance=3\nsnooper-enabled=false\n'.replace('\\n','\n'))
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
  report=active/('particle-'+phase+'.txt')
  if process.returncode or not report.exists():print(log.read_text(encoding='utf-8',errors='replace')[-10000:]);raise SystemExit('Native particle server failed; see '+str(log))
  result=report.read_text(encoding='utf-8');print(result,flush=True)
  shutil.copyfile(report,R/'docs'/('native-particle-'+phase+'-checks.txt'))
  if result.splitlines()[-1]!='Failed: 0':raise SystemExit('Native particle checks failed; see '+str(log))
 (R/'docs/native-particle-run.json').write_text(json.dumps({'runDirectory':str(run),'logs':logs,'testJar':str(jar),'installed':False},indent=2)+'\n',encoding='utf-8')
 print('Native particle tests passed in:',run)
if __name__=='__main__':main()
