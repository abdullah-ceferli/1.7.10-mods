"""Run both reobfuscated ports together under native Forge in disposable worlds."""
from pathlib import Path
import argparse,datetime,json,os,shutil,subprocess,hashlib
ROOT=Path(__file__).resolve().parents[2];R=ROOT/'Croptopia-1.7.10'
def main():
 ap=argparse.ArgumentParser();ap.add_argument('--client',action='store_true');ap.add_argument('--modpack',action='store_true');a=ap.parse_args();c=json.loads((R/'.toolchain/toolchain.json').read_text());cache=Path(c['existingGradleCache']);native=Path(c['java']).parents[3];runtime=Path(c['runtimeClasspathFile']).read_text().strip()
 task=R/'.toolchain/tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S-%f');run=task/'run';(run/'mods').mkdir(parents=True)
 vendor=ROOT/'HandsOnDelight-1.7.10/vendor'
 for p in vendor.glob('*.jar'):
  if not p.name.startswith('joml-'):shutil.copy2(p,run/'mods'/p.name)
 excluded=[]
 if a.modpack:
  for p in Path('D:/programs/Maincraft/Minecraft/game/mods').glob('*.jar'):
   if not a.client and p.name.lower().startswith('easylan'):excluded.append(p.name);continue
   if not p.name.startswith(('croptopia','culturaldelights')):shutil.copy2(p,run/'mods'/p.name)
 for mod,folder in [('croptopia','Croptopia-1.7.10'),('culturaldelights','CulturalDelights-1.7.10')]:shutil.copy2(ROOT/folder/'build/libs'/(mod+'-tests.jar'),run/'mods'/(mod+'-tests.jar'))
 (run/'eula.txt').write_text('eula=true\n');(run/'server.properties').write_text('level-name=garden-checks\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nspawn-npcs=false\nallow-nether=false\nview-distance=3\nsnooper-enabled=false\n')
 if a.client:
  (run/'options.txt').write_text('renderDistance:4\nmaxFps:60\npauseOnLostFocus:false\n')
  cp=str(native/'forge-universal.jar')+';'+str(cache/'caches/minecraft/net/minecraft/minecraft/1.7.10/minecraft-1.7.10.jar')+';'+runtime
  cmd=[c['java'],'-Xms512M','-Xmx3G','-Dfoodports.qa=true','-Djava.library.path='+str(cache/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10'),'-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLTweaker','--username','GardenQA','--version','1.7.10','--gameDir',str(run),'--assetsDir',str(cache/'caches/minecraft/assets'),'--assetIndex','1.7.10','--uuid','e3ed840c9de63dfeacfbf7dfaf270001','--accessToken','0','--userProperties','{}','--userType','legacy','--width','1100','--height','740'];phases=['client'];env=os.environ.copy();env['ALSOFT_DRIVERS']='null'
  with (task/'client.log').open('w',encoding='utf-8') as log:result=subprocess.run(cmd,cwd=run,env=env,stdout=log,stderr=subprocess.STDOUT,timeout=240)
  if result.returncode or not (run/'checks-client.txt').exists():print((task/'client.log').read_text(errors='replace')[-10000:]);raise SystemExit('Client failed: '+str(run))
  client_report=(run/'checks-client.txt').read_text();print(client_report);shutil.copy2(run/'checks-client.txt',R/'docs/checks-client.txt')
  if client_report.splitlines()[-1]!='Failed: 0':raise SystemExit('Client checks failed: '+str(run))
 else:
  cp=str(native/'forge-universal.jar')+';'+str(cache/'caches/minecraft/net/minecraft/minecraft_server/1.7.10/minecraft_server-1.7.10.jar')+';'+runtime;phases=['initial','restart']
  for phase in phases:
   cmd=[c['java'],'-Xms256M','-Xmx2G','-Dfoodports.qa=true','-Dfoodports.phase='+phase,'-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLServerTweaker','nogui']
   with (task/(phase+'.log')).open('w',encoding='utf-8') as log:result=subprocess.run(cmd,cwd=run,stdout=log,stderr=subprocess.STDOUT,timeout=180)
   for mod,folder in [('croptopia','Croptopia-1.7.10'),('culturaldelights','CulturalDelights-1.7.10')]:
    report=run/('checks-'+mod+'-'+phase+'.txt')
    if result.returncode or not report.exists():print((task/(phase+'.log')).read_text(errors='replace')[-12000:]);raise SystemExit('Native run failed: '+str(run))
    text=report.read_text();print(text);shutil.copy2(report,ROOT/folder/'docs'/report.name)
    if text.splitlines()[-1]!='Failed: 0':raise SystemExit('Native checks failed: '+str(run))
 record={'runDirectory':str(run),'phases':phases,'userWorldOpened':False,'modpack':a.modpack,'excludedClientMods':excluded,'testJarHashes':{p.name:hashlib.sha256(p.read_bytes()).hexdigest() for p in (run/'mods').glob('*-tests.jar')}};(R/'docs'/('modpack-client-run.json' if a.modpack and a.client else 'modpack-run.json' if a.modpack else 'client-run.json' if a.client else 'native-run.json')).write_text(json.dumps(record,indent=2)+'\n');print('Verified:',run)
if __name__=='__main__':main()
