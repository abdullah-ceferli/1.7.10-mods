"""Build only the real compatibility core, then compare to official 1.12.2 implementations."""
from pathlib import Path
import hashlib,json,subprocess,re,collections
ROOT=Path(__file__).resolve().parents[1]
def main():
 config=json.loads((ROOT/'.toolchain/toolchain.json').read_text());version=json.loads((ROOT/'.toolchain/minecraft-1.12.2-version.json').read_text())
 original=ROOT/'.toolchain/minecraft-1.12.2-client.jar';assert hashlib.sha1(original.read_bytes()).hexdigest()==version['downloads']['client']['sha1']
 routes=json.loads((ROOT/'docs/foundation-conversion.json').read_text())['routes'];csv=json.loads((ROOT/'.toolchain/mcp112-members.json').read_text())
 legacy={}
 for line in Path(config['srgToMcp']).read_text().splitlines():
  p=line.split()
  if p[0]=='MD:':legacy[p[1].rsplit('/',1)[-1]]=p[3].rsplit('/',1)[-1]
  if p[0]=='FD:':legacy[p[1].rsplit('/',1)[-1]]=p[2].rsplit('/',1)[-1]
 classes={};methods=collections.defaultdict(set);fields=collections.defaultdict(set)
 for line in (ROOT/'.toolchain/joined-1.12.2.srg').read_text().splitlines():
  p=line.split()
  if not p:continue
  if p[0]=='CL:':classes[p[2]]=p[1]
  elif p[0]=='MD:' and p[3].rsplit('/',1)[0].replace('/','.').split('$')[0] in routes:methods[p[3].rsplit('/',1)[-1]].add(p[1].rsplit('/',1)[-1])
  elif p[0]=='FD:' and p[2].rsplit('/',1)[0].replace('/','.').split('$')[0] in routes:fields[p[2].rsplit('/',1)[-1]].add(p[1].rsplit('/',1)[-1])
 props=[]
 for line in (ROOT/'.toolchain/joined-1.12.2.srg').read_text().splitlines():
  row=line.split()
  if row and row[0]=='MD:' and (row[3].rsplit('/',1)[0].replace('/','.').split('$')[0] in routes or row[3].startswith('net/minecraft/block/state/BlockStateBase/')):
   owner,method=row[1].rsplit('/',1);srg=row[3].rsplit('/',1)[-1]
   props.append('method.'+owner.replace('/','.')+'.'+srg+'='+method)
 for short,full in {'BlockPos':'net/minecraft/util/math/BlockPos','MutableBlockPos':'net/minecraft/util/math/BlockPos$MutableBlockPos','Vec3d':'net/minecraft/util/math/Vec3d','EnumFacing':'net/minecraft/util/EnumFacing','Rotation':'net/minecraft/util/Rotation','Mirror':'net/minecraft/util/Mirror','NonNullList':'net/minecraft/util/NonNullList','IProperty':'net/minecraft/block/properties/IProperty','PropertyHelper':'net/minecraft/block/properties/PropertyHelper','PropertyBool':'net/minecraft/block/properties/PropertyBool','PropertyInteger':'net/minecraft/block/properties/PropertyInteger','PropertyEnum':'net/minecraft/block/properties/PropertyEnum','PropertyDirection':'net/minecraft/block/properties/PropertyDirection','EnumFacing$Plane':'net/minecraft/util/EnumFacing$Plane','EnumFacing$Axis':'net/minecraft/util/EnumFacing$Axis','IBlockState':'net/minecraft/block/state/IBlockState','BlockStateContainer':'net/minecraft/block/state/BlockStateContainer','BlockFaceShape':'net/minecraft/block/state/BlockFaceShape','GlStateManager':'net/minecraft/client/renderer/GlStateManager','GlStateManager$SourceFactor':'net/minecraft/client/renderer/GlStateManager$SourceFactor','GlStateManager$DestFactor':'net/minecraft/client/renderer/GlStateManager$DestFactor','GlStateManager$FogMode':'net/minecraft/client/renderer/GlStateManager$FogMode','GlStateManager$Profile':'net/minecraft/client/renderer/GlStateManager$Profile'}.items():props.append('class.'+short+'='+classes[full].replace('/','.'))
 for group,values in [('method',methods),('field',fields)]:
  for srg,names in values.items():
   if not srg.startswith(('func_','field_')):continue
   if len(names)!=1:continue
   props.append(group+'.'+srg+'='+next(iter(names)))
 propertyFile=ROOT/'.toolchain/reference112.properties';propertyFile.write_text('\n'.join(props)+'\n',encoding='utf-8')
 contract=[]
 for owner,entries in json.loads((ROOT/'docs/compatibility-members.json').read_text()).items():
  if owner.replace('/','.').split('$')[0] not in routes:continue
  newOwner=owner
  for old,new in routes.items():newOwner=newOwner.replace(old.replace('.','/'),new.replace('.','/'))
  for row in entries:
   srg,desc=row.split(' ',1);name=legacy.get(srg,csv.get(srg,{}).get('name',srg))
   for old,new in routes.items():desc=desc.replace(old.replace('.','/'),new.replace('.','/'))
   desc=desc.replace('net/minecraft/util/math/AxisAlignedBB','net/minecraft/util/AxisAlignedBB')
   contract.append('\t'.join([newOwner.replace('/','.'),'M' if desc.startswith('(') else 'F',name,desc]))
 contractFile=ROOT/'docs/foundation-member-contract.tsv';contractFile.write_text('\n'.join(contract)+'\n',encoding='utf-8')
 out=ROOT/'build/foundation-checks';out.mkdir(parents=True,exist_ok=True)
 cp=';'.join([config['forgeJar'],Path(config['runtimeClasspathFile']).read_text().strip()])
 files=sorted((ROOT/'src/main/java/com/dhanantry/scapeandrunparasites/backport').rglob('*.java'))+[ROOT/'src/compatTest/java/FoundationParityChecks.java']
 args=['-proc:none','-encoding','UTF-8','-source','8','-target','8','-classpath',cp,'-d',str(out)]+[str(p) for p in files]
 argfile=ROOT/'.toolchain/javac-foundation.args';argfile.write_text('\n'.join('"'+v.replace('\\','/')+'"' for v in args)+'\n',encoding='utf-8')
 subprocess.run([config['javac'],'@'+str(argfile)],cwd=ROOT,check=True)
 runCp=str(out)+';'+cp+';'+str(original)
 result=subprocess.run([config['java'],'-cp',runCp,'dev.srpbackport.tests.FoundationParityChecks',str(propertyFile),str(contractFile),str(ROOT/'docs/foundation-checks.txt')],cwd=ROOT)
 return result.returncode
if __name__=='__main__':raise SystemExit(main())
