"""Compare original Minecraft world query implementations with the native boundary."""
from pathlib import Path
import subprocess,json
R=Path(__file__).resolve().parents[1]
def main():
 c=json.loads((R/'.toolchain/toolchain.json').read_text());out=R/'build/foundation-checks';cp=str(out)+';'+c['forgeJar']+';'+Path(c['runtimeClasspathFile']).read_text().strip()
 props=(R/'.toolchain/reference112.properties').read_text();rows=[]
 wanted={'net/minecraft/world/World','net/minecraft/util/math/AxisAlignedBB','net/minecraft/util/math/RayTraceResult'}
 for line in (R/'.toolchain/joined-1.12.2.srg').read_text().splitlines():
  row=line.split()
  if not row:continue
  if row[0]=='CL:' and row[2] in wanted:rows.append('class.'+row[2].rsplit('/',1)[1]+'='+row[1])
  if row[0]=='MD:' and row[3].rsplit('/',1)[0] in wanted:rows.append('method.'+row[1].rsplit('/',1)[0]+'.'+row[3].rsplit('/',1)[1]+'='+row[1].rsplit('/',1)[1])
  if row[0]=='FD:' and row[2].startswith('net/minecraft/util/math/Vec3d/'):rows.append('field.'+row[2].rsplit('/',1)[1]+'='+row[1].rsplit('/',1)[1])
 target=R/'.toolchain/world112.properties';target.write_text(props+'\n'.join(rows)+'\n')
 subprocess.run([c['javac'],'-proc:none','-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out),str(R/'src/compatTest/java/WorldPositionParityChecks.java')],cwd=R,check=True)
 return subprocess.run([c['java'],'-cp',cp+';'+str(R/'.toolchain/minecraft-1.12.2-client.jar'),'dev.srpbackport.tests.WorldPositionParityChecks',str(target),str(R/'docs/world-position-reference-checks.txt')],cwd=R).returncode
if __name__=='__main__':raise SystemExit(main())
