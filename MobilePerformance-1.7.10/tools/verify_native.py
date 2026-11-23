"""Exercise the production JAR in disposable Forge client/server processes."""
from pathlib import Path
import argparse,datetime,hashlib,json,os,shutil,subprocess

ROOT=Path(__file__).resolve().parents[1]
def main():
 p=argparse.ArgumentParser();p.add_argument('--pack',action='store_true');p.add_argument('--server',action='store_true');p.add_argument('--mods-dir',type=Path,default=Path('D:/programs/Maincraft/Minecraft/game/mods'));a=p.parse_args()
 shared=ROOT.parent/'industrialMOD';cache=shared/'.gradle-user';java=shared/'.toolchain/jdk8/jdk8u504-b01/bin/java.exe'
 mode=('server' if a.server else 'client')+('-pack' if a.pack else '-vanilla')
 task=ROOT/'.toolchain/native'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S-%f');run=task/'run';(run/'mods').mkdir(parents=True)
 artifact=ROOT/'build/libs/MobilePerformance-1.7.10-1.0.0.jar';harness=ROOT/'build/libs/mobile-performance-tests-1.0.0.jar'
 for jar in (artifact,harness):shutil.copy2(jar,run/'mods'/jar.name)
 excluded=[]
 if a.pack:
  for jar in a.mods_dir.glob('*.jar'):
   if jar.name.lower().startswith(('mobileperformance','opencomputers')) or a.server and jar.name.lower().startswith(('easylan','optifine','offlineskins','personalclienttools','controlpresets','blocklinecounter')):excluded.append(jar.name);continue
   shutil.copy2(jar,run/'mods'/jar.name)
 runtime=(shared/'.toolchain/runtime-classpath.txt').read_text().strip()
 cp=str(shared/'.toolchain/forge-universal.jar')+';'+str(cache/('caches/minecraft/net/minecraft/minecraft_server/1.7.10/minecraft_server-1.7.10.jar' if a.server else 'caches/minecraft/net/minecraft/minecraft/1.7.10/minecraft-1.7.10.jar'))+';'+runtime
 cmd=[str(java),'-Xms512M','-Xmx3G','-Dmobileperformance.test=true']
 if not a.server:cmd+=['-Djava.library.path='+str(cache/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10')]
 cmd+=['-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.'+('FMLServerTweaker' if a.server else 'FMLTweaker')]
 if a.server:
  (run/'eula.txt').write_text('eula=true\n');(run/'server.properties').write_text('level-name=mobile-performance-checks\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nallow-nether=false\nview-distance=3\n');cmd+=['nogui']
 else:
  (run/'options.txt').write_text('renderDistance:5\nmaxFps:120\npauseOnLostFocus:false\nfancyGraphics:true\nmipmapLevels:0\nenableVsync:false\n')
  cmd+=['--username','MobileChecks','--version','1.7.10','--gameDir',str(run),'--assetsDir',str(cache/'caches/minecraft/assets'),'--assetIndex','1.7.10','--uuid','954be44ecea44b2ca09f62d191ad45f2','--accessToken','0','--userProperties','{}','--userType','legacy','--width','960','--height','540']
 env=os.environ.copy();env['ALSOFT_DRIVERS']='null';print(mode+': '+str(run),flush=True)
 with (task/'launch.log').open('w',encoding='utf-8') as out:result=subprocess.run(cmd,cwd=run,env=env,stdout=out,stderr=subprocess.STDOUT,timeout=600)
 report=run/('checks-server.txt' if a.server else 'checks-client.txt')
 if result.returncode or not report.exists():print((task/'launch.log').read_text(errors='replace')[-9000:]);raise SystemExit('Native run failed')
 text=report.read_text();print(text,flush=True)
 if text.splitlines()[-1]!='Failed: 0':print((task/'launch.log').read_text(errors='replace')[-9000:]);raise SystemExit('Native checks failed')
 out=ROOT/'docs/tests';out.mkdir(parents=True,exist_ok=True);shutil.copy2(report,out/(mode+'.txt'))
 info={'mode':mode,'runDirectory':str(run),'sha256':hashlib.sha256(artifact.read_bytes()).hexdigest(),'harnessSha256':hashlib.sha256(harness.read_bytes()).hexdigest(),'userWorldOpened':False,'androidTested':False,'excluded':excluded,'failed':0}
 if not a.server:
  info['desktopBenchmark']=json.loads((run/'benchmark.json').read_text());images=out/(mode+'-images');images.mkdir(exist_ok=True)
  for image in (run/'screenshots').glob('performance-*.png'):shutil.copy2(image,images/image.name)
 (out/(mode+'.json')).write_text(json.dumps(info,indent=2)+'\n');print('Verified '+mode,flush=True)
if __name__=='__main__':main()
