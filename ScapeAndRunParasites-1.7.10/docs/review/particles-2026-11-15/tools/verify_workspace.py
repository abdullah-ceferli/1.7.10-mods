"""Verify the downloaded input, original assets, source inventory and explicit unfinished status."""
from pathlib import Path
from zipfile import ZipFile
import hashlib,json
ROOT=Path(__file__).resolve().parents[1]
def main():
 lock=json.loads((ROOT/'upstream/upstream-lock.json').read_text());archive=ROOT/'upstream/SRParasites-1.10.9-forgecdn.jar'
 assert hashlib.sha256(archive.read_bytes()).hexdigest()==lock['sha256']
 selected=json.loads((ROOT/'upstream/selected-version.json').read_text());primary=next(f for f in selected['files'] if f['primary'])
 assert hashlib.sha512(archive.read_bytes()).hexdigest()==primary['hashes']['sha512']
 assets=json.loads((ROOT/'docs/asset-provenance.json').read_text())['assets'];assert len(assets)==4501
 for row in assets:
  path=ROOT/'src/main/resources'/row['path'];assert path.stat().st_size==row['bytes'] and hashlib.sha256(path.read_bytes()).hexdigest()==row['sha256'],row['path']
 reference=ROOT/'.toolchain/reference-1.12.2'
 originals={p.relative_to(reference) for p in reference.rglob('*.java')};assert len(originals)==1094
 current={p.relative_to(ROOT/'src/main/java') for p in (ROOT/'src/main/java').rglob('*.java')}
 assert originals<=current,'Original source units were removed'
 assert all(p.as_posix().startswith('com/dhanantry/scapeandrunparasites/backport/') for p in current-originals),'Untracked source addition'
 with ZipFile(ROOT/'.toolchain/upstream-libraries.jar') as z:assert not any(n.startswith('com/dhanantry/') for n in z.namelist()),'Old 1.12 mod classes accidentally supplied as a compile dependency'
 probe=json.loads((ROOT/'docs/compile-probe.json').read_text());assert probe['exitCode']!=0
 assert not list((ROOT/'build/libs').glob('*.jar')),'Unexpected artifact: the conversion is not a release'
 print('PASS: official input hashes, 4501 unchanged assets, all 1094 source units and no concealed 1.12 mod-class dependency.')
 print('STATUS: compilation fails; no playable 1.7.10 JAR has been produced or installed.')
if __name__=='__main__':main()
