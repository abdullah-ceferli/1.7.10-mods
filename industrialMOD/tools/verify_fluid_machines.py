from pathlib import Path
import argparse,datetime,hashlib,json,shutil,subprocess

ROOT=Path(__file__).resolve().parents[1]
def main():
 p=argparse.ArgumentParser();p.add_argument('--hbm',action='store_true');p.add_argument('--pack',action='store_true');p.add_argument('--mods-dir',type=Path,default=Path('D:/programs/Maincraft/Minecraft/game/mods'));args=p.parse_args()
 mode='modpack' if args.pack else 'hbm' if args.hbm else 'standalone'
 task=ROOT/'.toolchain/fluid-tests'/('2.12.1-'+mode+'-'+datetime.datetime.now().strftime('%Y%m%d-%H%M%S-%f'));run=task/'run';(run/'mods').mkdir(parents=True)
 runtime=ROOT/'build/libs/IndustrialTechnology-1.7.10-2.12.1.jar'
 harness=ROOT/'.toolchain/fluid-tests/build-run/mods/industrialtech-test-harness-2.12.1.jar'
 game=args.mods_dir.resolve()
 for jar in (runtime,harness,ROOT/'vendor/baubles/Baubles-1.7.10-1.0.1.10.jar'):shutil.copy2(jar,run/'mods'/jar.name)
 if args.hbm or args.pack:
  hbm=game/'HBM-NTM-.1.0.27_X5771.jar';shutil.copy2(hbm,run/'mods'/hbm.name)
 excluded=[]
 if args.pack:
  for jar in game.glob('*.jar'):
   if jar.name.lower().startswith(('industrialtechnology','opencomputers','easylan','optifine','offlineskins','personalclienttools','controlpresets','blocklinecounter')):excluded.append(jar.name);continue
   shutil.copy2(jar,run/'mods'/jar.name)
 (run/'eula.txt').write_text('eula=true\n')
 (run/'server.properties').write_text('level-name=fluid-machine-checks\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nspawn-npcs=false\nallow-nether=false\nview-distance=3\nsnooper-enabled=false\n')
 java=ROOT/'.toolchain/jdk8/jdk8u504-b01/bin/java.exe';cp=str(ROOT/'.toolchain/forge-universal.jar')+';'+str(ROOT/'.gradle-user/caches/minecraft/net/minecraft/minecraft_server/1.7.10/minecraft_server-1.7.10.jar')+';'+(ROOT/'.toolchain/runtime-classpath.txt').read_text().strip()
 print('Native fluid run: '+str(run),flush=True)
 for phase in ('initial','restart'):
  log=task/(phase+'.log')
  with log.open('w',encoding='utf-8') as stream:result=subprocess.run([str(java),'-Xms512M','-Xmx3G','-Dindustrialtech.fluidTest=true','-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLServerTweaker','nogui'],cwd=run,stdout=stream,stderr=subprocess.STDOUT,timeout=240)
  report=run/('fluids-'+phase+'.txt')
  if result.returncode or not report.exists():print(log.read_text(errors='replace')[-10000:]);raise SystemExit('Native fluid launch failed')
  text=report.read_text();print(text,flush=True)
  if text.splitlines()[-1]!='Failed: 0':print(log.read_text(errors='replace')[-10000:]);raise SystemExit('Fluid checks failed')
 out=ROOT/'docs/test-results/2.12.1';out.mkdir(parents=True,exist_ok=True)
 for phase in ('initial','restart'):shutil.copy2(run/('fluids-'+phase+'.txt'),out/(mode+'-'+phase+'.txt'))
 info={'mode':mode,'directory':str(run),'runtimeSha256':hashlib.sha256(runtime.read_bytes()).hexdigest(),'harnessSha256':hashlib.sha256(harness.read_bytes()).hexdigest(),'userWorldOpened':False,'excludedClientMods':excluded,'phases':['initial','restart'],'failed':0}
 (out/(mode+'.json')).write_text(json.dumps(info,indent=2)+'\n');print('Verified '+mode,flush=True)
if __name__=='__main__':main()
