"""Run a real production Forge 1.7.10 client with only the disposable navigation test module."""
from pathlib import Path
import json,subprocess,datetime,shutil,argparse,os
R=Path(__file__).resolve().parents[1]
def main():
 c=json.loads((R/'.toolchain/toolchain.json').read_text());cache=Path(c['existingGradleCache']);native=Path(c['java']).parents[3];runtime=Path(c['runtimeClasspathFile']).read_text().strip()
 metadata=R/'docs/native-navigation-build.json';last=json.loads((metadata if metadata.exists() else R/'docs/native-navigation-run.json').read_text(encoding='utf-8'));jar=Path(last['testJar']);task=R/'.toolchain/navigation-client-tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S');run=task/'run';(run/'mods').mkdir(parents=True);shutil.copyfile(jar,run/'mods/srp-navigation-checks.jar')
 (run/'options.txt').write_text('renderDistance:3\nmaxFps:60\npauseOnLostFocus:false\nfboEnable:true\n')
 cp=str(native/'forge-universal.jar')+';'+str(cache/'caches/minecraft/net/minecraft/minecraft/1.7.10/minecraft-1.7.10.jar')+';'+runtime
 args=[c['java'],'-Xms512M','-Xmx3G','-Djava.library.path='+str(cache/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10'),'-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLTweaker','--username','SRPNavigationQA','--version','1.7.10','--gameDir',str(run),'--assetsDir',str(cache/'caches/minecraft/assets'),'--assetIndex','1.7.10','--uuid','e3ed840c9de63dfeacfbf7dfaf270000','--accessToken','0','--userProperties','{}','--userType','legacy','--width','640','--height','360']
 log=task/'client.log'
 env=os.environ.copy();env["ALSOFT_DRIVERS"]="null" # Navigation QA does not exercise audio hardware.
 with log.open('w',encoding='utf-8') as out:p=subprocess.run(args,cwd=run,stdout=out,stderr=subprocess.STDOUT,timeout=240,env=env)
 report=run/'navigation-client.txt'
 if p.returncode or not report.exists():print(log.read_text(encoding='utf-8',errors='replace')[-12000:]);raise SystemExit('Actual navigation client check failed; see '+str(log))
 print(report.read_text(encoding='utf-8'));shutil.copyfile(report,R/'docs/navigation-client-checks.txt')
 if report.read_text(encoding='utf-8').splitlines()[-1]!='Failed: 0':print(log.read_text(encoding='utf-8',errors='replace')[-12000:]);raise SystemExit(1)
 (R/'docs/navigation-client-run.json').write_text(json.dumps({'runDirectory':str(run),'log':str(log),'testJar':str(jar),'nativeClientIntegrationPassed':True,'installedIntoUserGame':False},indent=2)+'\n')
 print('Actual Forge navigation client/server verified:',run)
if __name__=='__main__':main()
