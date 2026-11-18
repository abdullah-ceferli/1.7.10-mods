"""Run a real production Forge 1.7.10 client with only the disposable entity-common test module."""
from pathlib import Path
import json,subprocess,datetime,shutil,argparse
R=Path(__file__).resolve().parents[1]
def main():
 c=json.loads((R/'.toolchain/toolchain.json').read_text());cache=Path(c['existingGradleCache']);native=Path(c['java']).parents[3];runtime=Path(c['runtimeClasspathFile']).read_text().strip()
 metadata=R/'docs/native-entity-common-run.json';last=json.loads((metadata if metadata.exists() else R/'docs/native-entity-common-run.json').read_text());jar=Path(last['testJar']);task=R/'.toolchain/entity-common-client-tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S');run=task/'run';(run/'mods').mkdir(parents=True);shutil.copyfile(jar,run/'mods/srp-entity-common-checks.jar')
 (run/'options.txt').write_text('renderDistance:3\nmaxFps:60\npauseOnLostFocus:false\nfboEnable:true\n')
 cp=str(native/'forge-universal.jar')+';'+str(cache/'caches/minecraft/net/minecraft/minecraft/1.7.10/minecraft-1.7.10.jar')+';'+runtime
 args=[c['java'],'-Xms512M','-Xmx3G','-Djava.library.path='+str(cache/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10'),'-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLTweaker','--username','SRPRegistryQA','--version','1.7.10','--gameDir',str(run),'--assetsDir',str(cache/'caches/minecraft/assets'),'--assetIndex','1.7.10','--uuid','e3ed840c9de63dfeacfbf7dfaf270000','--accessToken','0','--userProperties','{}','--userType','legacy','--width','640','--height','360']
 log=task/'client.log'
 with log.open('w') as out:p=subprocess.run(args,cwd=run,stdout=out,stderr=subprocess.STDOUT,timeout=240)
 report=run/'entity-common-client.txt'
 if p.returncode or not report.exists():print(log.read_text(errors='replace')[-12000:]);raise SystemExit('Actual entity-common client check failed; see '+str(log))
 print(report.read_text());shutil.copyfile(report,R/'docs/entity-common-client-checks.txt')
 if report.read_text().splitlines()[-1]!='Failed: 0':print(log.read_text(errors='replace')[-12000:]);raise SystemExit(1)
 (R/'docs/entity-common-client-run.json').write_text(json.dumps({'runDirectory':str(run),'log':str(log),'testJar':str(jar),'nativeClientIntegrationPassed':True,'installedIntoUserGame':False},indent=2)+'\n')
 print('Actual Forge client/server registration verified:',run)
if __name__=='__main__':main()
