"""Build a private native API copy using the same rules as the production FML manifest."""
from pathlib import Path
import json,hashlib,subprocess,zipfile
R=Path(__file__).resolve().parents[1]
def main():
 c=json.loads((R/'.toolchain/toolchain.json').read_text(encoding='utf-8'));source=Path(c['forgeJar']);rules=R/'src/main/resources/META-INF/srp_at.cfg';out=R/'.toolchain/compile-access';out.mkdir(exist_ok=True)
 mapping={}
 for line in Path(c['srgToMcp']).read_text().splitlines():
  p=line.split()
  if p and p[0]=='FD:':mapping[p[1]]=p[2]
 result=[]
 for line in rules.read_text().splitlines():
  code=line.split('#',1)[0].strip()
  if not code:continue
  access,owner,field=code.split();key=owner.replace('.','/')+'/'+field;member=mapping[key];result.append(access+' '+owner+' '+member.rsplit('/',1)[1])
 dev=out/'srp-development.cfg';dev.write_text('\n'.join(result)+'\n',encoding='utf-8')
 cp=c['forgeJar']+';'+Path(c['runtimeClasspathFile']).read_text().strip()
 subprocess.run([c['javac'],'-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out),str(R/'tools/ApplyCompileAccess.java')],cwd=R,check=True)
 target=out/'forgeSrc-access.jar'
 subprocess.run([c['java'],'-cp',str(out)+';'+cp,'dev.srpbackport.tools.ApplyCompileAccess',str(source),str(target),str(dev)],cwd=R,check=True)
 changed=[]
 with zipfile.ZipFile(source) as old,zipfile.ZipFile(target) as new:
  assert set(old.namelist())==set(new.namelist())
  changed=[name for name in old.namelist() if old.read(name)!=new.read(name)]
 assert set(changed)=={'net/minecraft/entity/EntityLiving.class','net/minecraft/entity/ai/EntityMoveHelper.class','net/minecraft/client/gui/GuiSlot.class','net/minecraft/potion/Potion.class'}
 digest=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
 record={'nativeSourceJar':str(source),'nativeSourceSha256':digest(source),'compileJar':str(target),'compileJarSha256':digest(target),'runtimeRulesSha256':digest(rules),'developmentRulesSha256':digest(dev),'changedEntries':changed,'finalFlagsPreservedExcept':['net.minecraft.potion.Potion.potionTypes'],'sharedCacheModified':False}
 (R/'docs/compile-access.json').write_text(json.dumps(record,indent=2)+'\n',encoding='utf-8')
 c['forgeCompileJar']=str(target);(R/'.toolchain/toolchain.json').write_text(json.dumps(c,indent=2)+'\n',encoding='utf-8')
 print('Verified private access-transformed compile API:',target)
if __name__=='__main__':main()
