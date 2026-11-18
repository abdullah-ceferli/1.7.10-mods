"""One production dedicated server and two real Forge clients; loopback only."""
from pathlib import Path
from zipfile import ZipFile
import subprocess,shutil,socket,json,hashlib,uuid,time,sys
project=Path(__file__).resolve().parents[1];workspace=project.parent
run=project/'build'/sys.argv[1];assert run.parent==project/'build' and not run.exists();run.mkdir(parents=True)
game=Path(r'D:\programs\Maincraft\Minecraft\game');java=game.parent/'jre/jre-legacy/windows-x64/jre-legacy/bin/java.exe'
runtime=(workspace/'.toolchain/runtime-classpath.txt').read_text().strip()
old=workspace/'build/youtube-nei-ae2-compare-20261025'
servercp=(old/'server-classpath.txt').read_text(encoding='utf-8-sig').strip();clientcp=(old/'client-classpath.txt').read_text(encoding='utf-8-sig').strip()
with socket.socket() as s:s.bind(('127.0.0.1',0));port=s.getsockname()[1]
hashes={}
for name in ['server','client-a','client-b']:
    directory=run/name;(directory/'mods').mkdir(parents=True)
    for filename in ['TrueHorror-1.7.10-1.0.0.jar','true-horror-test-harness-1.0.0.jar']:
        source=project/'build/libs'/filename;shutil.copy2(source,directory/'mods');hashes[filename]=hashlib.sha256(source.read_bytes()).hexdigest()
    if name!='server':
        shutil.copy2(game/'mods/OptiFine_1.7.10_HD_U_E7_MOD.jar',directory/'mods')
        (directory/'options.txt').write_text('renderDistance:3\nmaxFps:30\nguiScale:2\npauseOnLostFocus:false\nfullscreen:false\nmusic:0\nsound:0\n')
        (directory/'optionsof.txt').write_text('ofRenderDistanceChunks:3\nofChunkUpdates:1\n')
        natives=directory/'natives';natives.mkdir()
        for path in runtime.split(';'):
            if 'natives-windows' in path:
                with ZipFile(path) as z:
                    for member in z.namelist():
                        if member.endswith('.dll'):(natives/Path(member).name).write_bytes(z.read(member))
serverdir=run/'server';(serverdir/'eula.txt').write_text('eula=true\n')
profile=str(uuid.UUID(bytes=hashlib.md5(b'OfflinePlayer:HorrorTestA').digest(),version=3))
(serverdir/'ops.json').write_text(json.dumps([{'uuid':profile,'name':'HorrorTestA','level':4}]))
(serverdir/'server.properties').write_text(f'online-mode=false\nserver-ip=127.0.0.1\nserver-port={port}\nlevel-name=horror-test\nlevel-type=FLAT\ngamemode=1\ngenerate-structures=false\nspawn-animals=false\nspawn-monsters=false\nview-distance=3\nmax-players=4\n')
processes=[];logs=[]
def launch(command,directory):
    log=(directory/'console.log').open('w',encoding='utf-8');logs.append(log)
    process=subprocess.Popen(command,cwd=directory,stdin=subprocess.PIPE,stdout=log,stderr=subprocess.STDOUT,creationflags=subprocess.CREATE_NO_WINDOW)
    processes.append(process);return process
try:
    server=launch([str(java),'-Xms128M','-Xmx1G',f'-Dhorror.test.root={run}','-Dhorror.test.two=true','-cp',servercp,'cpw.mods.fml.relauncher.ServerLaunchWrapper','nogui'],serverdir)
    deadline=time.monotonic()+360
    while 'Done (' not in (serverdir/'console.log').read_text(errors='replace'):
        assert server.poll() is None and time.monotonic()<deadline,'Dedicated server did not start'
        time.sleep(1)
    print('Dedicated server ready on loopback port',port,flush=True)
    clients=[]
    for role in ['A','B']:
        directory=run/('client-'+role.lower())
        command=[str(java),'-Xms128M','-Xmx1G',f'-Djava.library.path={directory/"natives"}',f'-Dhorror.test.root={run}',
                 '-Dhorror.test.two=true',f'-Dhorror.test.role={role}',f'-Dhorror.test.port={port}',
                 '-cp',clientcp,'net.minecraft.launchwrapper.Launch','--username','HorrorTest'+role,'--version','1.7.10',
                 '--gameDir',str(directory),'--assetsDir',str(game/'assets'),'--assetIndex','1.7.10','--accessToken','0','--userProperties','{}',
                 '--tweakClass','cpw.mods.fml.common.launcher.FMLTweaker','--width','1280','--height','800','--server','127.0.0.1','--port',str(port)]
        clients.append(launch(command,directory))
        if role=='A':
            while 'HorrorTestA joined the game' not in (serverdir/'console.log').read_text(errors='replace'):
                assert clients[0].poll() is None and time.monotonic()<deadline,'First client login failed'
                time.sleep(1)
    while any(c.poll() is None for c in clients):
        assert not (run/'FAILED.txt').exists(),(run/'FAILED.txt').read_text() if (run/'FAILED.txt').exists() else ''
        assert server.poll() is None and time.monotonic()<deadline,'Dedicated test timed out'
        time.sleep(1)
    assert all(c.returncode==0 for c in clients) and (run/'complete.txt').exists() and (run/'b-complete.txt').exists()
    assert (run/'two-player-progression.txt').exists() and (run/'navigation-gaze-checks.txt').exists()
    server.stdin.write(b'stop\n');server.stdin.flush();server.wait(timeout=40);assert server.returncode==0
    result={'passed':True,'dedicated_server':True,'clients':2,'production_sha256':hashes,'checks':sorted(p.stem for p in run.glob('*.txt'))}
    (run/'RESULT.json').write_text(json.dumps(result,indent=2),encoding='utf-8');print(json.dumps(result,indent=2),flush=True)
finally:
    for process in processes:
        if process.poll() is None:process.terminate();process.wait(timeout=15)
    for log in logs:log.close()
