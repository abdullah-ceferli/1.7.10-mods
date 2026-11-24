"""Verify production Carpenter block copying in native disposable Forge processes."""
from pathlib import Path
import argparse,datetime,hashlib,json,os,re,shutil,subprocess
ROOT=Path(__file__).resolve().parents[1]

def main():
 p=argparse.ArgumentParser();p.add_argument('--client',action='store_true');p.add_argument('--hbm',action='store_true');p.add_argument('--ordinary',action='store_true');a=p.parse_args()
 if a.ordinary and a.client:p.error('--ordinary is a dedicated-server regression check')
 version=re.search(r"^version = '([^']+)'",(ROOT/'build.gradle').read_text(),re.M)[1]
 mode=('ordinary-server' if a.ordinary else 'client' if a.client else 'server')+('-hbm' if a.hbm else '-base');task=ROOT/'.toolchain/carpenter-builder-tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S-%f');run=task/'run';(run/'mods').mkdir(parents=True)
 jar=ROOT/'build/libs'/('IndustrialTechnology-1.7.10-'+version+'.jar');harness=ROOT/'.toolchain/carpenter-builder-tests/build-run/mods'/('industrialtech-test-harness-'+version+'.jar')
 for source in [jar,harness,ROOT/'vendor/baubles/Baubles-1.7.10-1.0.1.10.jar']:shutil.copy2(source,run/'mods'/source.name)
 if a.hbm:
  source=Path('D:/programs/Maincraft/Minecraft/game/mods/HBM-NTM-.1.0.27_X5771.jar');shutil.copy2(source,run/'mods'/source.name)
 if not a.ordinary:
  source=Path('D:/programs/Maincraft/Minecraft/game/mods/Chisel-2.9.5.11.jar');shutil.copy2(source,run/'mods'/source.name)
  source=Path("D:/programs/Maincraft/Minecraft/game/mods/Carpenter's Blocks v3.3.8.2 - MC 1.7.10.jar");shutil.copy2(source,run/'mods'/source.name)
 if a.client:
  vendor=ROOT.parent/'HandsOnDelight-1.7.10/vendor'
  for name in ['codechickencore-1.4.10.jar','notenoughitems-2.3.54-gtnh.jar']:shutil.copy2(vendor/name,run/'mods'/name)
 cache=ROOT/'.gradle-user';cp=str(ROOT/'.toolchain/forge-universal.jar')+';'+str(cache/('caches/minecraft/net/minecraft/minecraft/1.7.10/minecraft-1.7.10.jar' if a.client else 'caches/minecraft/net/minecraft/minecraft_server/1.7.10/minecraft_server-1.7.10.jar'))+';'+(ROOT/'.toolchain/runtime-classpath.txt').read_text().strip()
 cmd=[str(ROOT/'.toolchain/jdk8/jdk8u504-b01/bin/java.exe'),'-Xms512M','-Xmx3G','-Dindustrialtech.builderTest=true' if a.ordinary else '-Dindustrialtech.carpenterBuilderTest=true']
 if a.client:cmd+=['-Djava.library.path='+str(cache/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10')]
 cmd+=['-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.'+('FMLTweaker' if a.client else 'FMLServerTweaker')]
 if a.client:
  (run/'options.txt').write_text('renderDistance:4\nmaxFps:60\npauseOnLostFocus:false\nmipmapLevels:0\nclouds:false\n')
  cmd+=['--username','CarpChecks','--version','1.7.10','--gameDir',str(run),'--assetsDir',str(cache/'caches/minecraft/assets'),'--assetIndex','1.7.10','--uuid','954be44ecea44b2ca09f62d191ad45f2','--accessToken','0','--userProperties','{}','--userType','legacy','--width','1280','--height','760']
 else:
  (run/'eula.txt').write_text('eula=true\n');(run/'server.properties').write_text('level-name=carpenter-builder\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nallow-nether=false\nview-distance=3\nsnooper-enabled=false\n');cmd+=['nogui']
 env=os.environ.copy();env['ALSOFT_DRIVERS']='null';out=ROOT/'docs/test-results'/version;out.mkdir(parents=True,exist_ok=True)
 print(mode+': '+str(run),flush=True)
 phases=['initial'] if a.ordinary else ['client'] if a.client else ['initial','restart']
 for phase in phases:
  command=cmd[:];command.insert(1,'-Dindustrialtech.carpenterBuilderPhase='+phase)
  with (task/(phase+'.log')).open('w',encoding='utf-8') as log:result=subprocess.run(command,cwd=run,env=env,stdout=log,stderr=subprocess.STDOUT,timeout=300)
  report=run/('builder-server.txt' if a.ordinary else 'carpenter-builder-'+phase+'.txt')
  if result.returncode or not report.exists() or not report.read_text().strip().endswith('Failed: 0'):
   print(report.read_text() if report.exists() else 'No native report');print((task/(phase+'.log')).read_text(errors='replace')[-12000:]);raise SystemExit('Carpenter building checks failed: '+str(run))
  print(report.read_text(),flush=True);shutil.copy2(report,out/(mode+'-'+phase+'.txt'))
 if a.client:
  images=out/(mode+'-images');images.mkdir(exist_ok=True)
  for path in (run/'screenshots').glob('carpenter-builder-*.png'):shutil.copy2(path,images/path.name)
 record={'mode':mode,'runDirectory':str(run),'phases':phases,'sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'harnessSha256':hashlib.sha256(harness.read_bytes()).hexdigest(),'failed':0,'userWorldOpened':False,'androidTested':False,'mods':{p.name:hashlib.sha256(p.read_bytes()).hexdigest() for p in (run/'mods').glob('*.jar')}}
 (out/(mode+'.json')).write_text(json.dumps(record,indent=2)+'\n');print('Verified '+mode,flush=True)
if __name__=='__main__':main()
