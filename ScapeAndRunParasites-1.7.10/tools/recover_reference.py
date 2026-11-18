"""Recreate the local analysis reference using the existing CFR 0.152 toolchain."""
from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
import json,subprocess
ROOT=Path(__file__).resolve().parents[1]
def main():
 config=json.loads((ROOT/'.toolchain/toolchain.json').read_text());out=ROOT/'.toolchain/reference-1.12.2'
 assert not out.exists(),'Reference already exists; do not overwrite analysis or source edits.'
 original=ROOT/'upstream/SRParasites-1.10.9-forgecdn.jar';subset=ROOT/'upstream/srp-1.10.9-code-only.jar'
 with ZipFile(original) as source,ZipFile(subset,'w',compression=ZIP_DEFLATED) as target:
  for name in source.namelist():
   if name.startswith('com/dhanantry/scapeandrunparasites/') and name.endswith('.class'):target.writestr(name,source.read(name))
 cfr=Path(config['existingGradleCache']).parent/'.toolchain/cfr-0.152.jar'
 with (ROOT/'docs/decompiler.log').open('w') as log:subprocess.run([config['java'],'-Xmx3G','-jar',str(cfr),str(subset),'--outputdir',str(out),'--silent','true','--caseinsensitivefs','true'],cwd=ROOT,stdout=log,stderr=subprocess.STDOUT,check=True)
 print('Recovered analysis reference. It still targets 1.12.2 and is not compiled backport source.')
if __name__=='__main__':main()
