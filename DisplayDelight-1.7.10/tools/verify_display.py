"""Run disposable native Forge server/restart or actual client checks."""
from pathlib import Path
import json,subprocess,shutil,datetime,argparse,os
R=Path(__file__).resolve().parents[1]
def main():
 p=argparse.ArgumentParser();p.add_argument('--client',action='store_true');o=p.parse_args()
 c=json.loads((R/'.toolchain/toolchain.json').read_text());cache=Path(c['existingGradleCache']);native=Path(c['java']).parents[3];runtime=Path(c['runtimeClasspathFile']).read_text().strip()
 task=R/'.toolchain/tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S');run=task/'run';(run/'mods').mkdir(parents=True)
 jar=R/'build/libs/displaydelight-1.7.10-tests.jar';shutil.copy2(jar,run/'mods'/jar.name)
 if o.client:
  (run/'options.txt').write_text('renderDistance:6\nmaxFps:60\npauseOnLostFocus:false\n')
  cp=str(native/'forge-universal.jar')+';'+str(cache/'caches/minecraft/net/minecraft/minecraft/1.7.10/minecraft-1.7.10.jar')+';'+runtime
  cmd=[c['java'],'-Xms512M','-Xmx3G','-Djava.library.path='+str(cache/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10'),'-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLTweaker','--username','FoodQA','--version','1.7.10','--gameDir',str(run),'--assetsDir',str(cache/'caches/minecraft/assets'),'--assetIndex','1.7.10','--uuid','e3ed840c9de63dfeacfbf7dfaf270000','--accessToken','0','--userProperties','{}','--userType','legacy','--width','1100','--height','740']
  env=os.environ.copy();env['ALSOFT_DRIVERS']='null'
  with (task/'client.log').open('w',encoding='utf-8') as f:result=subprocess.run(cmd,cwd=run,env=env,stdout=f,stderr=subprocess.STDOUT,timeout=240)
  phases=['client']
 else:
  (run/'eula.txt').write_text('eula=true\n');(run/'server.properties').write_text('level-name=display-tests\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nspawn-npcs=false\nallow-nether=false\nview-distance=3\nsnooper-enabled=false\n')
  cp=str(native/'forge-universal.jar')+';'+str(cache/'caches/minecraft/net/minecraft/minecraft_server/1.7.10/minecraft_server-1.7.10.jar')+';'+runtime
  phases=['initial','restart']
  for phase in phases:
   cmd=[c['java'],'-Xms256M','-Xmx2G','-Ddisplay.qa.phase='+phase,'-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLServerTweaker','nogui']
   with (task/(phase+'.log')).open('w',encoding='utf-8') as f:result=subprocess.run(cmd,cwd=run,stdout=f,stderr=subprocess.STDOUT,timeout=180)
   report=run/('checks-'+phase+'.txt')
   if result.returncode or not report.exists():raise SystemExit('Native server failed: '+str(task/(phase+'.log')))
   print(report.read_text(encoding='utf-8'));shutil.copy2(report,R/'docs'/report.name)
   if report.read_text(encoding='utf-8').splitlines()[-1]!='Failed: 0':raise SystemExit('Native checks failed: '+str(task))
 if o.client:
  report=run/'checks-client.txt'
  if result.returncode or not report.exists():raise SystemExit('Client failed: '+str(task/'client.log'))
  print(report.read_text(encoding='utf-8'));shutil.copy2(report,R/'docs'/report.name)
  if report.read_text(encoding='utf-8').splitlines()[-1]!='Failed: 0':raise SystemExit('Client checks failed: '+str(task))
 (R/'docs'/('client-run.json' if o.client else 'native-run.json')).write_text(json.dumps({'runDirectory':str(run),'testJar':str(jar),'phases':phases,'installedIntoUserGame':False},indent=2)+'\n')
 print('Verified:',run)
if __name__=='__main__':main()
