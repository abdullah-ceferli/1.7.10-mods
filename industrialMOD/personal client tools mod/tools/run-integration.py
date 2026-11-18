"""Exercise both features from one combined production JAR in an isolated client."""
from pathlib import Path
from zipfile import ZipFile
import hashlib
import json
import shutil
import subprocess
import sys

project = Path(__file__).resolve().parents[1]
workspace = project.parent
run = project / 'build' / sys.argv[1]
assert run.parent == project / 'build' and not run.exists()
run.mkdir(parents=True)
game = Path(r'D:\programs\Maincraft\Minecraft\game')
java = Path(r'D:\programs\Maincraft\Minecraft\jre\jre-legacy\windows-x64\jre-legacy\bin\java.exe')
runtime = (workspace / '.toolchain/runtime-classpath.txt').read_text().strip()
cp = (workspace / 'build/youtube-nei-ae2-compare-20261025/client-classpath.txt').read_text(encoding='utf-8-sig').strip()
(run / 'mods').mkdir()
hashes = {}
for jar in (game / 'mods').glob('*.jar'):
    if not jar.name.lower().startswith(('blocklinecounter', 'controlpresets', 'personalclienttools')):
        shutil.copy2(jar, run / 'mods')
for jar in [project / 'build/libs/PersonalClientTools-1.7.10-1.1.0.jar',
            project / 'build/libs/personal-tools-test-harness-1.1.0.jar']:
    shutil.copy2(jar, run / 'mods')
    hashes[jar.name] = hashlib.sha256(jar.read_bytes()).hexdigest()
(run / 'options.txt').write_text('renderDistance:2\nmaxFps:30\npauseOnLostFocus:false\nfullscreen:false\nmusic:0\nsound:0\n')
(run / 'optionsof.txt').write_text('ofRenderDistanceChunks:2\nofChunkUpdates:1\n')
(run / 'config').mkdir()
# Disable EasyLAN's address/UPnP helpers in this fixture only. No actual LAN is opened.
(run / 'config/easylan.cfg').write_text('online-mode=false\nHttp-Api=false\nPort=0\n')
natives = run / 'natives'
natives.mkdir()
for entry in runtime.split(';'):
    if 'natives-windows' in entry:
        with ZipFile(entry) as archive:
            for name in archive.namelist():
                if name.lower().endswith('.dll'):
                    (natives / Path(name).name).write_bytes(archive.read(name))
command = [str(java), '-Xms256M', '-Xmx3G', f'-Djava.library.path={natives}', f'-Dtools.test.root={run}',
           '-cp', cp, 'net.minecraft.launchwrapper.Launch', '--username', 'ToolsTest', '--version', '1.7.10',
           '--gameDir', str(run), '--assetsDir', str(game / 'assets'), '--assetIndex', '1.7.10',
           '--accessToken', '0', '--userProperties', '{}', '--tweakClass', 'cpw.mods.fml.common.launcher.FMLTweaker',
           '--width', '854', '--height', '480']
with (run / 'console.log').open('w', encoding='utf-8') as log:
    process = subprocess.Popen(command, cwd=run, stdout=log, stderr=subprocess.STDOUT,
                               creationflags=subprocess.CREATE_NO_WINDOW)
    try:
        code = process.wait(timeout=240)
    finally:
        if process.poll() is None:
            process.terminate()
            process.wait(timeout=15)
result = {'exit_code': code, 'passed': code == 0 and (run / 'complete.txt').exists() and not (run / 'FAILED.txt').exists(),
          'jars_sha256': hashes, 'markers': sorted(p.stem for p in run.glob('*.txt') if p.name != 'options.txt')}
(run / 'RESULT.json').write_text(json.dumps(result, indent=2))
print(json.dumps(result, indent=2), flush=True)
assert result['passed'], f'Integration failed; inspect {run / "console.log"}'
