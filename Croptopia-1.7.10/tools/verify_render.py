"""Check food alpha, leaf inventory rendering and NEI recipe colors in native Forge.

Runs the existing cooking client checks with the full local modpack in a new
disposable world. No player saves or launcher files are opened or modified.
"""
from pathlib import Path
import datetime,hashlib,json,os,shutil,subprocess

R=Path(__file__).resolve().parents[1]
ROOT=R.parent

def main():
    c=json.loads((R/'.toolchain/toolchain.json').read_text())
    cache=Path(c['existingGradleCache']);native=Path(c['java']).parents[3]
    runtime=Path(c['runtimeClasspathFile']).read_text().strip()
    task=R/'.toolchain/render-tests'/datetime.datetime.now().strftime('%Y%m%d-%H%M%S-%f')
    run=task/'run';(run/'mods').mkdir(parents=True)
    for p in (ROOT/'HandsOnDelight-1.7.10/vendor').glob('*.jar'):
        if not p.name.startswith('joml-'):shutil.copy2(p,run/'mods'/p.name)
    for p in Path('D:/programs/Maincraft/Minecraft/game/mods').glob('*.jar'):
        if not p.name.lower().startswith(('croptopia','handson-delight')):shutil.copy2(p,run/'mods'/p.name)
    mod=R/'build/libs/croptopia-1.7.10-4.0.1-backport.2.jar'
    harness=ROOT/'HandsOnDelight-1.7.10/build/libs/handson-delight-tests.jar'
    for p in (mod,harness):shutil.copy2(p,run/'mods'/p.name)
    (run/'options.txt').write_text('renderDistance:4\nmaxFps:60\npauseOnLostFocus:false\nfancyGraphics:true\nmipmapLevels:0\n')
    cp=str(native/'forge-universal.jar')+';'+str(cache/'caches/minecraft/net/minecraft/minecraft/1.7.10/minecraft-1.7.10.jar')+';'+runtime
    cmd=[c['java'],'-Xms512M','-Xmx3G','-Djava.library.path='+str(cache/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10'),'-cp',cp,'net.minecraft.launchwrapper.Launch','--tweakClass','cpw.mods.fml.common.launcher.FMLTweaker','--username','KitchenQA','--version','1.7.10','--gameDir',str(run),'--assetsDir',str(cache/'caches/minecraft/assets'),'--assetIndex','1.7.10','--uuid','e3ed840c9de63dfeacfbf7dfaf270001','--accessToken','0','--userProperties','{}','--userType','legacy','--width','1100','--height','740']
    env=os.environ.copy();env['ALSOFT_DRIVERS']='null'
    print('Native test directory: '+str(run),flush=True)
    with (task/'client.log').open('w',encoding='utf-8') as log:
        result=subprocess.run(cmd,cwd=run,env=env,stdout=log,stderr=subprocess.STDOUT,timeout=300)
    report=run/'checks-client.txt'
    if result.returncode or not report.exists():
        print((task/'client.log').read_text(errors='replace')[-10000:])
        raise SystemExit('Native client did not complete: '+str(task))
    text=report.read_text();print(text)
    (R/'docs/checks-render-client.txt').write_text(text)
    if text.splitlines()[-1]!='Failed: 0':
        print((task/'client.log').read_text(errors='replace')[-10000:])
        raise SystemExit('Native rendering check failed: '+str(task))
    record={'runDirectory':str(run),'fullLocalModpack':True,'playerWorldOpened':False,'croptopiaSha256':hashlib.sha256(mod.read_bytes()).hexdigest(),'handsOnTestSha256':hashlib.sha256(harness.read_bytes()).hexdigest(),'failed':0}
    (R/'docs/render-client-run.json').write_text(json.dumps(record,indent=2)+'\n')
    print('Verified: '+str(run))

if __name__=='__main__':main()
