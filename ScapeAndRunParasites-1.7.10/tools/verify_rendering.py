"""Run real offscreen GL state parity after the foundation compile has passed."""
from pathlib import Path
import json,subprocess
ROOT=Path(__file__).resolve().parents[1]
def main():
 c=json.loads((ROOT/'.toolchain/toolchain.json').read_text());out=ROOT/'build/foundation-checks'
 cp=';'.join([str(out),c['forgeJar'],Path(c['runtimeClasspathFile']).read_text().strip(),str(ROOT/'.toolchain/minecraft-1.12.2-client.jar')])
 source=ROOT/'src/compatTest/java/RenderingParityChecks.java'
 subprocess.run([c['javac'],'-proc:none','-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out),str(source)],cwd=ROOT,check=True)
 natives=Path(c['existingGradleCache'])/'caches/minecraft/net/minecraft/minecraft_natives/1.7.10'
 result=subprocess.run([c['java'],'-Djava.library.path='+str(natives),'-cp',cp,'dev.srpbackport.tests.RenderingParityChecks',str(ROOT/'.toolchain/reference112.properties'),str(ROOT/'docs/rendering-checks.txt')],cwd=ROOT)
 return result.returncode
if __name__=='__main__':raise SystemExit(main())
