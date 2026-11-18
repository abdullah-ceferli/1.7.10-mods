"""Production-JAR integrated-server test in a fresh disposable full-modpack world."""
from pathlib import Path
from zipfile import ZipFile
import subprocess,shutil,json,hashlib,sys
project=Path(__file__).resolve().parents[1];workspace=project.parent
run=project/'build'/sys.argv[1]
assert run.parent==project/'build' and not run.exists();run.mkdir(parents=True)
game=Path(r'D:\programs\Maincraft\Minecraft\game')
java=game.parent/'jre/jre-legacy/windows-x64/jre-legacy/bin/java.exe'
cp=(workspace/'build/youtube-nei-ae2-compare-20261025/client-classpath.txt').read_text(encoding='utf-8-sig').strip()
runtime=(workspace/'.toolchain/runtime-classpath.txt').read_text().strip()
(run/'mods').mkdir();(run/'config').mkdir()
for jar in (game/'mods').glob('*.jar'):
    if not jar.name.lower().startswith('truehorror'):shutil.copy2(jar,run/'mods')
hashes={}
for name in ['TrueHorror-1.7.10-1.0.0.jar','true-horror-test-harness-1.0.0.jar']:
    source=project/'build/libs'/name;shutil.copy2(source,run/'mods');hashes[name]=hashlib.sha256(source.read_bytes()).hexdigest()
(run/'options.txt').write_text('renderDistance:3\nmaxFps:30\nguiScale:2\npauseOnLostFocus:false\nfullscreen:false\nmusic:0\nsound:0\n')
(run/'optionsof.txt').write_text('ofRenderDistanceChunks:3\nofChunkUpdates:1\n')
(run/'config/easylan.cfg').write_text('online-mode=false\nHttp-Api=false\nPort=0\n')
natives=run/'natives';natives.mkdir()
for path in runtime.split(';'):
    if 'natives-windows' in path:
        with ZipFile(path) as z:
            for name in z.namelist():
                if name.endswith('.dll'):(natives/Path(name).name).write_bytes(z.read(name))
command=[str(java),'-Xms256M','-Xmx3G',f'-Djava.library.path={natives}',f'-Dhorror.test.root={run}',
         '-cp',cp,'net.minecraft.launchwrapper.Launch','--username','HorrorTestA','--version','1.7.10','--gameDir',str(run),
         '--assetsDir',str(game/'assets'),'--assetIndex','1.7.10','--accessToken','0','--userProperties','{}',
         '--tweakClass','cpw.mods.fml.common.launcher.FMLTweaker','--width','1280','--height','800']
with (run/'console.log').open('w',encoding='utf-8') as log:
    process=subprocess.Popen(command,cwd=run,stdout=log,stderr=subprocess.STDOUT,creationflags=subprocess.CREATE_NO_WINDOW)
    try:code=process.wait(timeout=280)
    finally:
        if process.poll() is None:process.terminate();process.wait(timeout=15)
result={'passed':code==0 and (run/'complete.txt').exists() and not (run/'FAILED.txt').exists(),'exit_code':code,
        'production_sha256':hashes,'checks':sorted(p.stem for p in run.glob('*.txt'))}
(run/'RESULT.json').write_text(json.dumps(result,indent=2),encoding='utf-8');print(json.dumps(result,indent=2),flush=True)
assert result['passed'],f'Inspect {run/"console.log"}'
