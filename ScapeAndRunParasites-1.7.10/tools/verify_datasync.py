"""Original 1.12 reference comparisons for entity tracked values and metadata bytes."""
from pathlib import Path
import json,subprocess,hashlib
R=Path(__file__).resolve().parents[1]
def main():
 c=json.loads((R/'.toolchain/toolchain.json').read_text());out=R/'build/foundation-checks';guava=R/'.toolchain/reference-libraries/guava-21.0.jar'
 assert hashlib.sha1(guava.read_bytes()).hexdigest()=='3a3d111be1be1b745edfa7d91678a12d7ed38709'
 cp=str(out)+';'+c['forgeJar']+';'+Path(c['runtimeClasspathFile']).read_text().strip();mappings=json.loads((R/'.toolchain/mcp112-members.json').read_text());classes={};rows=[]
 for line in (R/'.toolchain/joined-1.12.2.srg').read_text().splitlines():
  p=line.split()
  if not p:continue
  if p[0]=='CL:':classes[p[2]]=p[1]
  if p[0]=='FD:' and p[2].startswith('net/minecraft/network/datasync/DataSerializers/'):rows.append('serializer.'+mappings[p[2].rsplit('/',1)[-1]]['name']+'='+p[1].rsplit('/',1)[-1])
 for short,name in {'DataSerializer':'net/minecraft/network/datasync/DataSerializer','DataParameter':'net/minecraft/network/datasync/DataParameter','DataSerializers':'net/minecraft/network/datasync/DataSerializers','EntityDataManager':'net/minecraft/network/datasync/EntityDataManager','PacketBuffer':'net/minecraft/network/PacketBuffer','Entity':'net/minecraft/entity/Entity','EntityLivingBase':'net/minecraft/entity/EntityLivingBase','EntityMob':'net/minecraft/entity/monster/EntityMob'}.items():rows.append('class.'+short+'='+classes[name].replace('/','.'))
 properties=R/'.toolchain/datasync112.properties';properties.write_text((R/'.toolchain/reference112.properties').read_text()+'\n'.join(rows)+'\n')
 result=subprocess.run([c['javac'],'-proc:none','-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out),str(R/'src/compatTest/java/EntityDataParityChecks.java')],cwd=R)
 if result.returncode:return result.returncode
 return subprocess.run([c['java'],'-Dlog4j.configurationFile='+str(R/'.toolchain/reference-data-log4j.xml'),'-cp',str(guava)+';'+cp+';'+str(R/'.toolchain/minecraft-1.12.2-client.jar'),'dev.srpbackport.tests.EntityDataParityChecks',str(properties),str(R/'docs/datasync-reference-checks.txt')],cwd=R).returncode
if __name__=='__main__':raise SystemExit(main())
