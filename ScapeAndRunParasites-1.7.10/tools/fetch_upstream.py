"""Retrieve the pinned official input; never install it into a Minecraft mods folder."""
from pathlib import Path
import hashlib,json,subprocess,os
ROOT=Path(__file__).resolve().parents[1]
def main():
 lock=json.loads((ROOT/'upstream/upstream-lock.json').read_text());out=ROOT/'upstream/SRParasites-1.10.9-forgecdn.jar'
 if out.exists():
  assert hashlib.sha256(out.read_bytes()).hexdigest()==lock['sha256'];print('Pinned upstream archive already verified.');return
 part=out.with_suffix('.jar.part')
 try:
  subprocess.run(['curl.exe','--fail','--location','--connect-timeout','15','--max-time','120','--output',str(part),lock['origin']],check=True)
  assert hashlib.sha256(part.read_bytes()).hexdigest()==lock['sha256'],'Download hash mismatch'
  os.replace(part,out)
 finally:
  if part.exists():part.unlink()
 print('Downloaded and verified original 1.12.2 archive; this is not a backported JAR.')
if __name__=='__main__':main()
