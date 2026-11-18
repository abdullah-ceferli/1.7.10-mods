"""Compare properties and canonical state graphs against the original official 1.12.2 JVM classes."""
from pathlib import Path
import json,subprocess,hashlib
R=Path(__file__).resolve().parents[1]
def main():
 c=json.loads((R/'.toolchain/toolchain.json').read_text());out=R/'build/foundation-checks'
 reference=json.loads((R/'.toolchain/minecraft-1.12.2-version.json').read_text())
 library=next(v['downloads']['artifact'] for v in reference['libraries'] if v['name']=='com.google.guava:guava:21.0')
 guava=R/'.toolchain/reference-libraries/guava-21.0.jar'
 assert hashlib.sha1(guava.read_bytes()).hexdigest()==library['sha1'],'Reference Guava hash mismatch'
 cp=str(out)+';'+c['forgeJar']+';'+Path(c['runtimeClasspathFile']).read_text().strip()
 subprocess.run([c['javac'],'-proc:none','-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out),str(R/'src/compatTest/java/StateParityChecks.java')],cwd=R,check=True)
 return subprocess.run([c['java'],'-cp',str(R/'.toolchain/reference-libraries/guava-21.0.jar')+';'+cp+';'+str(R/'.toolchain/minecraft-1.12.2-client.jar'),'dev.srpbackport.tests.StateParityChecks',str(R/'.toolchain/reference112.properties'),str(R/'docs/state-checks.txt')],cwd=R).returncode
if __name__=='__main__':raise SystemExit(main())
