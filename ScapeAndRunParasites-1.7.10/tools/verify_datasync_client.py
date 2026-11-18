"""Run a real production Forge 1.7.10 client with only the disposable tracked-data test module."""
from pathlib import Path
import json,subprocess,datetime,shutil,argparse
R=Path(__file__).resolve().parents[1]
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--world-positions',action='store_true');options=parser.parse_args()
 c=json.loads((R/'.toolchain/toolchain.json').read_text());cache=Path(c['existingGradleCache']);native=Path(c['java']).parents[3];runtime=Path(c['runtimeClasspathFile']).read_text().strip()
 metadata=R/'docs/datasync-client-build.json';last=json.loads((metadata if metadata.exists() else R/'docs/native-datasync-run.json').read_text());jar=Path(last['testJar']);task=R/'.toolchain/entity-data-client-tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S');run=task/'run';(run/'mods').mkdir(parents=True);shutil.copyfile(jar,run/'mods/srp-datasync-checks.jar')
 (run/'options.txt').write_text('renderDistance:3\nmaxFps:60\npauseOnLostFocus:false\nfboEnable:true\n')
 cp=str(native/'forge-universal.jar')+';'+str(cache/'caches/minecraft/net/minecraft/minecraft/1.7.10/minecraft-1.7.10.jar')+';'+runtime
 args=[c['java'],'-Xms512M','-Xmx3G','-Dsrp.datasyncClientTest=true','-Djava.library.path='+str(cache/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10'),'-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLTweaker','--username','SRPSyncQA','--version','1.7.10','--gameDir',str(run),'--assetsDir',str(cache/'caches/minecraft/assets'),'--assetIndex','1.7.10','--uuid','e3ed840c9de63dfeacfbf7dfaf270000','--accessToken','0','--userProperties','{}','--userType','legacy','--width','640','--height','360']
 if options.world_positions:args.insert(4,'-Dsrp.worldPositionClientTest=true')
 log=task/'client.log'
 with log.open('w') as out:p=subprocess.run(args,cwd=run,stdout=out,stderr=subprocess.STDOUT,timeout=240)
 report=run/'datasync-client.txt'
 if p.returncode or not report.exists():print(log.read_text(errors='replace')[-12000:]);raise SystemExit('Actual tracked-data client check failed; see '+str(log))
 print(report.read_text());shutil.copyfile(report,R/'docs/datasync-client-checks.txt')
 if report.read_text().splitlines()[-1]!='Failed: 0':print(log.read_text(errors='replace')[-12000:]);raise SystemExit(1)
 (R/'docs/datasync-client-run.json').write_text(json.dumps({'runDirectory':str(run),'log':str(log),'testJar':str(jar),'nativeClientIntegrationPassed':True,'installedIntoUserGame':False},indent=2)+'\n')
 if options.world_positions:
  worldReport=run/'world-position-client.txt';assert worldReport.exists() and worldReport.read_text().splitlines()[-1]=='Failed: 0'
  shutil.copyfile(worldReport,R/'docs/world-position-client-checks.txt')
  (R/'docs/world-position-client-run.json').write_text(json.dumps({'runDirectory':str(run),'log':str(log),'testJar':str(jar),'nativeClientIntegrationPassed':True,'installedIntoUserGame':False},indent=2)+'\n')
  print(worldReport.read_text())
 print('Actual Forge client/server sync verified:',run)
if __name__=='__main__':main()
