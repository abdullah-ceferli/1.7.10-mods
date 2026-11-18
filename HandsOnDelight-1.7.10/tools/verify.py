"""Exercise the reobfuscated JAR under native Forge, never a user save."""
from pathlib import Path
import argparse,datetime,json,os,shutil,subprocess,hashlib
R=Path(__file__).resolve().parents[1]

def main():
    ap=argparse.ArgumentParser();ap.add_argument('--client',action='store_true');ap.add_argument('--modpack',action='store_true');a=ap.parse_args()
    c=json.loads((R/'.toolchain/toolchain.json').read_text());cache=Path(c['existingGradleCache']);native=Path(c['java']).parents[3];runtime=Path(c['runtimeClasspathFile']).read_text().strip()
    task=R/'.toolchain/tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S-%f');run=task/'run';(run/'mods').mkdir(parents=True)
    for p in (R/'vendor').glob('*.jar'):
        if not p.name.startswith('joml-'):shutil.copy2(p,run/'mods'/p.name)
    if a.modpack:
        for p in Path(r'D:/programs/Maincraft/Minecraft/game/mods').glob('*.jar'):
            if not p.name.startswith('handson-delight'):shutil.copy2(p,run/'mods'/p.name)
    shutil.copy2(R/'build/libs/handson-delight-tests.jar',run/'mods/handson-delight-tests.jar')
    display=Path(r'D:/IndustrialTechnology/DisplayDelight-1.7.10/build/libs/displaydelight-1.7.10-1.7.0-backport.1.jar')
    if display.exists():shutil.copy2(display,run/'mods'/display.name)
    if a.client:
        (run/'options.txt').write_text('renderDistance:4\nmaxFps:60\npauseOnLostFocus:false\n')
        cp=str(native/'forge-universal.jar')+';'+str(cache/'caches/minecraft/net/minecraft/minecraft/1.7.10/minecraft-1.7.10.jar')+';'+runtime
        cmd=[c['java'],'-Xms512M','-Xmx3G','-Djava.library.path='+str(cache/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10'),'-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLTweaker','--username','KitchenQA','--version','1.7.10','--gameDir',str(run),'--assetsDir',str(cache/'caches/minecraft/assets'),'--assetIndex','1.7.10','--uuid','e3ed840c9de63dfeacfbf7dfaf270001','--accessToken','0','--userProperties','{}','--userType','legacy','--width','1100','--height','740']
        env=os.environ.copy();env['ALSOFT_DRIVERS']='null'
        with (task/'client.log').open('w',encoding='utf-8') as f:result=subprocess.run(cmd,cwd=run,env=env,stdout=f,stderr=subprocess.STDOUT,timeout=240)
        phases=['client']
    else:
        (run/'eula.txt').write_text('eula=true\n');(run/'server.properties').write_text('level-name=kitchen-checks\nlevel-type=FLAT\ngenerator-settings=2;7;1;\nserver-ip=127.0.0.1\nserver-port=0\nonline-mode=false\nspawn-protection=0\nspawn-animals=false\nspawn-monsters=false\nspawn-npcs=false\nallow-nether=false\nview-distance=3\nsnooper-enabled=false\n')
        cp=str(native/'forge-universal.jar')+';'+str(cache/'caches/minecraft/net/minecraft/minecraft_server/1.7.10/minecraft_server-1.7.10.jar')+';'+runtime;phases=['initial','restart']
        for phase in phases:
            cmd=[c['java'],'-Xms256M','-Xmx2G','-Dhandson.qa.phase='+phase,'-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLServerTweaker','nogui']
            with (task/(phase+'.log')).open('w',encoding='utf-8') as f:result=subprocess.run(cmd,cwd=run,stdout=f,stderr=subprocess.STDOUT,timeout=180)
            check(task,run,phase,result)
    if a.client:check(task,run,'client',result)
    (R/'docs'/('modpack-run.json' if a.modpack else 'client-run.json' if a.client else 'native-run.json')).write_text(json.dumps({'runDirectory':str(run),'phases':phases,'userWorldOpened':False,'fullUserModpackTested':a.modpack,'configuration':'fresh disposable defaults','testedJarSha256':hashlib.sha256((run/'mods/handson-delight-tests.jar').read_bytes()).hexdigest()},indent=2)+'\n');print('Verified:',run)

def check(task,run,phase,result):
    report=run/('checks-'+phase+'.txt')
    if result.returncode or not report.exists():
        print((task/(phase+'.log')).read_text(encoding='utf-8',errors='replace')[-12000:]);raise SystemExit('Native run failed: '+str(task))
    text=report.read_text(encoding='utf-8');print(text);shutil.copy2(report,R/'docs'/report.name)
    if text.splitlines()[-1]!='Failed: 0':raise SystemExit('Native checks failed: '+str(task))

if __name__=='__main__':main()
