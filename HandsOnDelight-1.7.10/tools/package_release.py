"""Package only a production JAR whose contents match the exercised native fixture."""
from pathlib import Path
import hashlib,json,shutil,zipfile

R=Path(__file__).resolve().parents[1]
DIST=R/'dist'
NAME='handson-delight-1.7.10-1.5.1-backport.4'

def sha(path):return hashlib.sha256(path.read_bytes()).hexdigest()

def main():
    tested=R/'build/libs/handson-delight-tests.jar'
    release=R/'build/libs'/(NAME+'.jar')
    for report in ['checks-client.txt','checks-initial.txt','checks-restart.txt']:
        if (R/'docs'/report).read_text().splitlines()[-1]!='Failed: 0':raise RuntimeError('Unsuccessful checks: '+report)
    runs={}
    for name in ['native-run.json','modpack-run.json']:
        run=json.loads((R/'docs'/name).read_text())
        if run.get('testedJarSha256')!=sha(tested):raise RuntimeError('Checks do not describe the current fixture: '+name)
        runs[name]=run
    with zipfile.ZipFile(tested) as fixture,zipfile.ZipFile(release) as production:
        main_entries=[n for n in production.namelist() if not n.startswith('META-INF/')]
        if any('/tests/' in n for n in main_entries):raise RuntimeError('Production JAR contains fixture classes')
        for name in main_entries:
            if production.read(name)!=fixture.read(name):raise RuntimeError('Untested production content: '+name)
        assets=[]
        original=R/'upstream/handson-delight/common/src/main/resources'
        for folder in ['assets/handson_delight/diegesis/scenes','assets/handson_delight/models/diegesis']:
            for path in sorted((original/folder).rglob('*.json')):
                name=path.relative_to(original).as_posix()
                if production.read(name)!=path.read_bytes():raise RuntimeError('Original scene/model differs: '+name)
                assets.append({'path':name,'sha256':sha(path),'repository':'handson-delight'})
    (R/'docs/ui-assets.json').write_text(json.dumps(assets,indent=2)+'\n')
    DIST.mkdir(exist_ok=True)
    shutil.copy2(release,DIST/release.name)
    for name in ['README.md','TESTING.md','UI-SOURCES.md']:
        shutil.copy2(R/'docs'/name,DIST/name)
    shutil.copy2(R/'docs/README.md',R/'README.md')
    with zipfile.ZipFile(DIST/(NAME+'-sources.zip'),'w',zipfile.ZIP_DEFLATED) as archive:
        for folder in ['src','tools','docs']:
            for path in sorted((R/folder).rglob('*')):
                if path.is_file() and '__pycache__' not in path.parts:archive.write(path,path.relative_to(R).as_posix())
        archive.write(R/'LICENSE','LICENSE')
        archive.write(R/'README.md','README.md')
        for name in ['DIEGESIS-LICENSE','FARMERS-DELIGHT-LICENSE','FARMERS-DELIGHT-ORIGINAL-LICENSE']:
            archive.write(R/'vendor'/name,'licenses/'+name)
        archive.write(R/'vendor/joml-1.10.5.jar','vendor/joml-1.10.5.jar')
        with zipfile.ZipFile(R/'vendor/joml-1.10.5.jar') as library:
            archive.writestr('licenses/JOML-LICENSE',library.read('META-INF/LICENSE'))
    groups={name:sum(line.startswith('PASS:') for line in (R/'docs'/('checks-'+name+'.txt')).read_text().splitlines()) for name in ['initial','restart','client']}
    proofs={'version':'1.5.1-backport.4','status':'native four-station release','jar':release.name,'sha256':sha(release),'mainEntriesMatchTestedFixture':len(main_entries),'testJarSha256':sha(tested),'originalSceneAndModelFilesVerified':len(assets),'checkGroups':groups,'runs':runs,'userWorldOpened':False,'androidRendererTested':False,'androidConfirmation':'pending affected-device confirmation'}
    (DIST/'verification.json').write_text(json.dumps(proofs,indent=2)+'\n')
    (DIST/'SHA256SUMS.txt').write_text(''.join(sha(path)+'  '+path.name+'\n' for path in sorted(DIST.iterdir()) if path.is_file() and path.name!='SHA256SUMS.txt'))
    print(json.dumps({key:value for key,value in proofs.items() if key!='runs'},indent=2))

if __name__=='__main__':main()
