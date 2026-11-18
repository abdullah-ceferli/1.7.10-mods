from pathlib import Path
import json,subprocess
R=Path(__file__).resolve().parents[1]
def main():
 c=json.loads((R/'.toolchain/toolchain.json').read_text());out=R/'build/foundation-checks';cp=str(out)+';'+c['forgeJar']+';'+Path(c['runtimeClasspathFile']).read_text().strip()
 props=(R/'.toolchain/reference112.properties').read_text(encoding='utf-8');lines=[]
 for line in (R/'.toolchain/joined-1.12.2.srg').read_text().splitlines():
  p=line.split()
  if not p:continue
  if p[0]=='CL:' and p[2]=='net/minecraft/entity/Entity':lines.append('class.Entity='+p[1])
  if p[0]=='FD:' and p[2].startswith('net/minecraft/entity/Entity/'):lines.append('field.'+p[2].rsplit('/',1)[1]+'='+p[1].rsplit('/',1)[1])
  if p[0]=='MD:' and p[3]=='net/minecraft/entity/Entity/func_180425_c':lines.append('method.func_180425_c='+p[1].rsplit('/',1)[1])
 path=R/'.toolchain/entity-position112.properties';path.write_text(props+'\n'.join(lines)+'\n',encoding='utf-8')
 subprocess.run([c['javac'],'-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out),str(R/'src/compatTest/java/EntityPositionParityChecks.java')],cwd=R,check=True)
 subprocess.run([c['java'],'-cp',cp+';'+str(R/'.toolchain/minecraft-1.12.2-client.jar'),'dev.srpbackport.tests.EntityPositionParityChecks',str(path),str(R/'docs/entity-position-checks.txt')],cwd=R,check=True)
if __name__=='__main__':main()
