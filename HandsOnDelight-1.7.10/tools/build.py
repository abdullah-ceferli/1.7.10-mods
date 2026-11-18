"""Native Java 8 / Forge 1614 build. Game dependencies stay separate; JOML is bundled under a private package."""
from pathlib import Path
import argparse, hashlib, json, shutil, subprocess, zipfile
R=Path(__file__).resolve().parents[1]

def main():
    parser=argparse.ArgumentParser();parser.add_argument('--tests',action='store_true');opt=parser.parse_args()
    math=json.loads((R/'docs/math-dependency.json').read_text())
    if hashlib.sha256((R/'vendor/joml-1.10.5.jar').read_bytes()).hexdigest()!=math['sha256']:raise RuntimeError('JOML dependency does not match the pinned original')
    c=json.loads((R/'.toolchain/toolchain.json').read_text());runtime=Path(c['runtimeClasspathFile']).read_text().strip()
    base=Path(c['existingGradleCache'])/'caches/modules-2/files-2.1'
    def dep(g,n,v):return next((base/g/n/v).rglob('*.jar'))
    deps=[dep('com.anatawa12.forge','SpecialSource','1.11.1'),dep('org.ow2.asm','asm','9.5'),dep('org.ow2.asm','asm-commons','9.4'),dep('org.ow2.asm','asm-tree','9.5'),dep('net.sf.jopt-simple','jopt-simple','5.0.4'),dep('com.google.guava','guava','31.1-jre'),dep('com.opencsv','opencsv','5.7.1')]
    ss=';'.join(map(str,deps));fd=R/'vendor/farmersdelight-1.1.1-mc1.7.10.jar';devfd=R/'.toolchain/farmersdelight-dev.jar'
    def call(args):subprocess.run(list(map(str,args)),cwd=R,check=True)
    if not devfd.exists():
        call([c['java'],'-cp',ss+';'+str(fd)+';'+c['forgeJar']+';'+runtime,'net.md_5.specialsource.SpecialSource','--live','--stable','--in-jar',fd,'--out-jar',devfd,'--srg-in',c['srgToMcp']])
    cp=c['forgeJar']+';'+runtime+';'+str(devfd)+';'+';'.join(str(p) for p in (R/'vendor').glob('*.jar') if p.name.startswith(('notenoughitems','codechickencore','joml')))
    classes=R/'build'/('test-classes' if opt.tests else 'classes')
    if not classes.resolve().is_relative_to((R/'build').resolve()):raise RuntimeError('Class output escaped the project build directory')
    if classes.exists():shutil.rmtree(classes)
    classes.mkdir(parents=True)
    sources=sorted((R/'src/main/java').rglob('*.java'))+(sorted((R/'src/test/java').rglob('*.java')) if opt.tests else [])
    args=['-encoding','UTF-8','-proc:none','-source','8','-target','8','-cp',cp,'-d',str(classes)]+list(map(str,sources))
    argfile=R/'.toolchain/javac.args';argfile.write_text('\n'.join('"'+a.replace('\\','/')+'"' for a in args)+'\n')
    call([c['javac'],'@'+str(argfile)])
    dev=R/'build/dev.jar'
    with zipfile.ZipFile(dev,'w',zipfile.ZIP_DEFLATED) as z:
        z.writestr('META-INF/MANIFEST.MF','Manifest-Version: 1.0\nFMLCorePlugin: com.pockyl.handson_delight.launch.LoadingPlugin\nFMLCorePluginContainsFMLMod: true\nImplementation-Version: 1.5.1-backport.4\n\n')
        for root in [classes,R/'src/main/resources']:
            for p in sorted(root.rglob('*')):
                if p.is_file():z.write(p,p.relative_to(root).as_posix())
        with zipfile.ZipFile(R/'vendor/joml-1.10.5.jar') as library:
            for name in library.namelist():
                if name.startswith('org/joml/') and name.endswith('.class'):z.writestr(name,library.read(name))
                if 'LICENSE' in name.upper():z.writestr('META-INF/LICENSE-JOML',library.read(name))
        z.write(R/'vendor/DIEGESIS-LICENSE','META-INF/LICENSE-Diegesis');z.write(R/'LICENSE','META-INF/LICENSE-HandsOnDelight');z.write(R/'vendor/FARMERS-DELIGHT-LICENSE','META-INF/LICENSE-FarmersDelight')
        z.write(R/'vendor/FARMERS-DELIGHT-ORIGINAL-LICENSE','META-INF/LICENSE-FarmersDelight-original')
    out=R/'build/libs'/('handson-delight-tests.jar' if opt.tests else 'handson-delight-1.7.10-1.5.1-backport.4.jar');out.parent.mkdir(exist_ok=True)
    mapping=R/'.toolchain/release.srg';mapping.write_text(Path(c['srgToMcp']).with_name('mcp-srg.srg').read_text()+'\nPK: org/joml com/pockyl/handson_delight/math/joml\n')
    call([c['java'],'-cp',ss+';'+str(classes)+';'+cp,'net.md_5.specialsource.SpecialSource','--live','--stable','--in-jar',dev,'--out-jar',out,'--srg-in',mapping])
    record={'jar':str(out),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'javaUnits':len(sources),'nativeCompilerApi':c['forgeJar'],'tests':opt.tests}
    (R/'docs'/('test-build.json' if opt.tests else 'build.json')).write_text(json.dumps(record,indent=2)+'\n');print(json.dumps(record,indent=2))

if __name__=='__main__':main()
