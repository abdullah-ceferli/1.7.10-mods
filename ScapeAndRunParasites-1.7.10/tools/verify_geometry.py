"""Compare legacy-compatible immutable box operations against original Minecraft 1.12.2."""
from pathlib import Path
import json,subprocess
R=Path(__file__).resolve().parents[1]
def main():
 c=json.loads((R/'.toolchain/toolchain.json').read_text());out=R/'build/foundation-checks';cp=str(out)+';'+c['forgeJar']+';'+Path(c['runtimeClasspathFile']).read_text().strip()
 props=(R/'.toolchain/reference112.properties').read_text();rows=[]
 for line in (R/'.toolchain/joined-1.12.2.srg').read_text().splitlines():
  p=line.split()
  if not p:continue
  if p[0]=='MD:' and p[3].startswith(('net/minecraft/util/math/AxisAlignedBB/','net/minecraft/util/math/RayTraceResult/')):
   owner,name=p[1].rsplit('/',1);rows.append('method.'+owner+'.'+p[3].rsplit('/',1)[-1]+'='+name)
  if p[0]=='FD:' and p[2].startswith('net/minecraft/util/math/RayTraceResult/'):rows.append('field.'+p[2].rsplit('/',1)[-1]+'='+p[1].rsplit('/',1)[-1])
 target=R/'.toolchain/geometry112.properties';target.write_text(props+'\n'.join(rows)+'\n')
 result=subprocess.run([c['javac'],'-proc:none','-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out),str(R/'src/compatTest/java/GeometryParityChecks.java')],cwd=R)
 if result.returncode:return result.returncode
 return subprocess.run([c['java'],'-cp',cp+';'+str(R/'.toolchain/minecraft-1.12.2-client.jar'),'dev.srpbackport.tests.GeometryParityChecks',str(target),str(R/'docs/geometry-checks.txt')],cwd=R).returncode
if __name__=='__main__':raise SystemExit(main())
