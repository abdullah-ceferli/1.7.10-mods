#!/usr/bin/env python3
"""Compile original sources and verify the renderer with pinned Zalith test libraries."""
from pathlib import Path
import argparse,hashlib,json,os,shutil,subprocess,sys,urllib.request,zipfile

ROOT=Path(__file__).resolve().parent
VERSION='0.2.0'
STAMP=(2026,11,22,0,0,0)

def digest(path):return hashlib.sha256(path.read_bytes()).hexdigest()
def invoke(command,label):
    result=subprocess.run([str(x) for x in command],cwd=ROOT,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,encoding='utf-8',errors='replace')
    (ROOT/'build'/f'{label}.log').write_text(result.stdout,encoding='utf-8')
    if result.returncode:raise RuntimeError(label+' failed:\n'+result.stdout[-10000:])
    print(result.stdout.strip())
    return result.stdout.strip()
def entry(archive,name,data):
    info=zipfile.ZipInfo(name,STAMP);info.compress_type=zipfile.ZIP_DEFLATED;info.external_attr=0o100644<<16;archive.writestr(info,data)
def main():
    parser=argparse.ArgumentParser();parser.add_argument('--java-home',type=Path,required=True);parser.add_argument('--cache',type=Path,required=True);parser.add_argument('--offline',action='store_true');parser.add_argument('--skip-native',action='store_true');args=parser.parse_args()
    for name in ['build','dist','build/classes','build/tests']:(ROOT/name).mkdir(parents=True,exist_ok=True)
    exe='.exe' if os.name=='nt' else '';java=args.java_home/('bin/java'+exe);javac=args.java_home/('bin/javac'+exe)
    if 'version "1.8.' not in invoke([java,'-version'],'java-version'):raise RuntimeError('Use a Java 8 JDK')
    forge=args.cache/'caches/minecraft/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10'
    api=forge/'forgeSrc-1.7.10-10.13.4.1614-1.7.10.jar';srg=forge/'forge-1.7.10-10.13.4.1614-1.7.10-srg.jar'
    if not api.exists() or not srg.exists():raise RuntimeError('Generate the ForgeGradle 1.7.10 workspace first (setupDecompWorkspace)')
    modules=args.cache/'caches/modules-2/files-2.1'
    def cached(name):
        match=next(modules.rglob(name),None)
        if match is None:raise RuntimeError('Missing Forge dependency '+name)
        return match
    libraries=[cached(name) for name in ['lwjgl-2.9.1.jar','asm-debug-all-5.0.3.jar','launchwrapper-1.12.jar','guava-17.0.jar','log4j-api-2.0-beta9.jar']]
    classes=ROOT/'build/classes';tests=ROOT/'build/tests'
    compile_cp=os.pathsep.join(str(p) for p in [api,*libraries])
    invoke([javac,'-proc:none','-encoding','UTF-8','-g','-cp',compile_cp,'-d',classes,*sorted((ROOT/'src/main/java').rglob('*.java'))],'compile-main')
    runtime=ROOT/f'dist/ZalithRender-1.7.10-{VERSION}.jar'
    manifest=('Manifest-Version: 1.0\r\nFMLCorePlugin: dev.zahin.zalithrender.core.RenderCore\r\nFMLCorePluginContainsFMLMod: true\r\nImplementation-Version: '+VERSION+'\r\n\r\n').encode()
    with zipfile.ZipFile(runtime,'w') as z:
        entry(z,'META-INF/MANIFEST.MF',manifest)
        for p in sorted(classes.rglob('*.class')):
            if int.from_bytes(p.read_bytes()[6:8],'big')!=52:raise AssertionError('Non-Java-8 bytecode')
            entry(z,p.relative_to(classes).as_posix(),p.read_bytes())
        for p in sorted((ROOT/'src/main/resources').rglob('*')):
            if p.is_file():entry(z,p.relative_to(ROOT/'src/main/resources').as_posix(),p.read_bytes())
        entry(z,'META-INF/LICENSE', (ROOT/'LICENSE').read_bytes())
    test_cp=os.pathsep.join(str(p) for p in [runtime,api,*libraries])
    # Native fixture classes are compiled below with the launcher's GL libraries.
    invoke([javac,'-proc:none','-encoding','UTF-8','-cp',test_cp,'-d',tests,ROOT/'tests/Checks.java',ROOT/'tests/FrameChecks.java',*sorted((ROOT/'tests/fixtures').rglob('*.java'))],'compile-checks')
    unit=invoke([java,'-Xverify:all','-cp',str(tests)+os.pathsep+test_cp,'dev.zahin.zalithrender.test.Checks',api,srg],'unit-checks')
    dep_lock=json.loads((ROOT/'test-dependencies.lock.json').read_text())
    deps={}
    for dep in dep_lock:
        path=ROOT/dep['path']
        if not path.exists():
            if args.offline:raise RuntimeError('Missing test cache '+dep['path'])
            path.parent.mkdir(parents=True,exist_ok=True)
            req=urllib.request.Request(dep['url'],headers={'User-Agent':'ZalithRender-build'})
            with urllib.request.urlopen(req,timeout=120) as response:data=response.read()
            if hashlib.sha256(data).hexdigest()!=dep['sha256']:raise RuntimeError('Test dependency checksum differs')
            path.write_bytes(data)
        if digest(path)!=dep['sha256']:raise RuntimeError('Test cache checksum differs: '+dep['id'])
        deps[dep['id']]=path
    native=[]
    if not args.skip_native:
        if sys.platform!='win32':raise RuntimeError('Use --skip-native outside the Windows test host')
        asm=next(p for p in libraries if p.name.startswith('asm-'));launch=next(p for p in libraries if p.name.startswith('launchwrapper-'))
        for edition,version in [('zalith1','3.3.6'),('zalith2','3.3.3')]:
            natives=ROOT/f'build/natives-{version}';natives.mkdir(exist_ok=True)
            for module in ['lwjgl','lwjgl-opengl','lwjgl-glfw']:
                with zipfile.ZipFile(deps[f'native-{version}-{module}']) as z:
                    for name in z.namelist():
                        if name.endswith('.dll'):(natives/Path(name).name).write_bytes(z.read(name))
            cp=os.pathsep.join(str(p) for p in [tests,runtime,api,deps[edition+'-lwjgl'],deps[edition+'-lwjgl-opengl'],deps['glfw-'+version],deps[edition+'-lwjglx'],asm,launch])
            invoke([javac,'-proc:none','-encoding','UTF-8','-cp',cp,'-d',tests,ROOT/'tests/NativeRenderChecks.java'],'compile-native-'+edition)
            result=invoke([java,'-Xverify:all','-Dorg.lwjgl.librarypath='+str(natives),'-cp',cp,'dev.zahin.zalithrender.test.NativeRenderChecks',edition,api],'native-'+edition)
            native.append({'library':edition,'lwjgl':version,'result':result})
    sources=ROOT/f'dist/ZalithRender-1.7.10-{VERSION}-sources.zip'
    with zipfile.ZipFile(sources,'w') as z:
        for p in sorted(ROOT.rglob('*')):
            if not p.is_file():continue
            rel=p.relative_to(ROOT)
            if rel.parts[0] in {'upstream','build','dist','releases','.gradle','.gradle-user','__pycache__'} or p.suffix=='.pyc':continue
            entry(z,rel.as_posix(),p.read_bytes())
    report={'version':VERSION,'origin':'original renderer implementation; no Angelica or Beddium runtime code bundled','unit_checks':unit,'native_checks':native,'physical_android_tested':False,'gameplay_fps_measured':False,'artifacts':[{'file':p.name,'bytes':p.stat().st_size,'sha256':digest(p)} for p in [runtime,sources]]}
    (ROOT/'dist/verification.json').write_text(json.dumps(report,indent=2)+'\n',encoding='utf-8')
    (ROOT/'dist/SHA256SUMS.txt').write_text(''.join(a['sha256']+'  '+a['file']+'\n' for a in report['artifacts']),encoding='utf-8')
    print('Built:',runtime)
if __name__=='__main__':main()
