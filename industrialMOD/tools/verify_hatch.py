"""Run native builder checks in disposable production Forge processes."""
from pathlib import Path
import argparse,datetime,hashlib,json,os,shutil,subprocess
ROOT=Path(__file__).resolve().parents[1]
def main():
 p=argparse.ArgumentParser();p.add_argument('--client',action='store_true');p.add_argument('--pack',action='store_true');p.add_argument('--mods-dir',type=Path,default=Path('D:/programs/Maincraft/Minecraft/game/mods'));a=p.parse_args()
 cache=ROOT/'.gradle-user';mode='hatch-'+('client' if a.client else 'server')+('-pack' if a.pack else '-base');task=ROOT/'.toolchain/hatch-tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S-%f');run=task/'run';(run/'mods').mkdir(parents=True)
 jar=ROOT/'build/libs/IndustrialTechnology-1.7.10-2.13.0.jar';harness=ROOT/'.toolchain/hatch-tests/build-run/mods/industrialtech-test-harness-2.13.0.jar'
 for f in (jar,harness,ROOT/'vendor/baubles/Baubles-1.7.10-1.0.1.10.jar'):shutil.copy2(f,run/'mods'/f.name)
 excluded=[]
 if a.pack:
  for f in a.mods_dir.glob('*.jar'):
   if f.name.lower().startswith(('industrialtechnology','opencomputers')) or not a.client and f.name.lower().startswith(('easylan','optifine','offlineskins','personalclienttools','controlpresets','blocklinecounter','xaerosworldmap')):excluded.append(f.name);continue
   shutil.copy2(f,run/'mods'/f.name)
 cp=str(ROOT/'.toolchain/forge-universal.jar')+';'+str(cache/('caches/minecraft/net/minecraft/minecraft/1.7.10/minecraft-1.7.10.jar' if a.client else 'caches/minecraft/net/minecraft/minecraft_server/1.7.10/minecraft_server-1.7.10.jar'))+';'+(ROOT/'.toolchain/runtime-classpath.txt').read_text().strip()
 cmd=[str(ROOT/'.toolchain/jdk8/jdk8u504-b01/bin/java.exe'),'-Xms512M','-Xmx3G','-Dindustrialtech.hatchTest=true']
 if a.client:cmd+=['-Djava.library.path='+str(cache/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10')]
 cmd+=['-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.'+('FMLTweaker' if a.client else 'FMLServerTweaker')]
 if a.client:
  (run/'options.txt').write_text('renderDistance:4\nmaxFps:60\npauseOnLostFocus:false\nmipmapLevels:0\n');cmd+=['--username','BuildChecks','--version','1.7.10','--gameDir',str(run),'--assetsDir',str(cache/'caches/minecraft/assets'),'--assetIndex','1.7.10','--uuid','954be44ecea44b2ca09f62d191ad45f2','--accessToken','0','--userProperties','{}','--userType','legacy','--width','1100','--height','740']
 else:
  (run/'eula.txt').write_text('eula=true\n');(run/'server.properties').write_text('level-name=builder-checks\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nallow-nether=false\nview-distance=3\n');cmd+=['nogui']
 env=os.environ.copy();env['ALSOFT_DRIVERS']='null';print(mode+': '+str(run),flush=True)
 with (task/'launch.log').open('w',encoding='utf-8') as out:result=subprocess.run(cmd,cwd=run,env=env,stdout=out,stderr=subprocess.STDOUT,timeout=240)
 report=run/'hatch-initial.txt'
 if result.returncode or not report.exists():print((task/'launch.log').read_text(errors='replace')[-10000:]);raise SystemExit('Builder launch failed')
 text=report.read_text();print(text,flush=True)
 if text.splitlines()[-1]!='Failed: 0':print((task/'launch.log').read_text(errors='replace')[-9000:]);raise SystemExit('Builder checks failed')
 restart_cmd=cmd[:];restart_cmd.insert(1,'-Dindustrialtech.hatchPhase=restart')
 with (task/'restart.log').open('w',encoding='utf-8') as log: restart=subprocess.run(restart_cmd,cwd=run,env=env,stdout=log,stderr=subprocess.STDOUT,timeout=240)
 restart_report=run/'hatch-restart.txt'
 if restart.returncode or not restart_report.exists() or restart_report.read_text().splitlines()[-1]!='Failed: 0':
  print((task/'restart.log').read_text(errors='replace')[-10000:]);raise SystemExit('Hatch reload failed')
 print(restart_report.read_text())
 out=ROOT/'docs/test-results/2.13.0';out.mkdir(parents=True,exist_ok=True);shutil.copy2(report,out/(mode+'.txt'));shutil.copy2(restart_report,out/'hatch-restart.txt');info={'mode':mode,'runDirectory':str(run),'sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'harnessSha256':hashlib.sha256(harness.read_bytes()).hexdigest(),'userWorldOpened':False,'androidTested':False,'excluded':excluded,'failed':0}
 if a.client:
  images=out/(mode+'-images');images.mkdir(exist_ok=True)
  for f in (run/'screenshots').glob('builder-*.png'):shutil.copy2(f,images/f.name)
 (out/(mode+'.json')).write_text(json.dumps(info,indent=2)+'\n');print('Verified '+mode,flush=True)
if __name__=='__main__':main()
