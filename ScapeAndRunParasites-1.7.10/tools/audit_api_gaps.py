"""Compare referenced platform types with actual Forge/Minecraft 1.7.10 classes."""
from pathlib import Path
from zipfile import ZipFile
import collections,json,re,struct
ROOT=Path(__file__).resolve().parents[1]
def class_types(data):
 assert data[:4]==b'\xca\xfe\xba\xbe'
 size=struct.unpack_from('>H',data,8)[0];cp=[None]*size;off=10;i=1
 while i<size:
  tag=data[off];off+=1
  if tag==1:
   length=struct.unpack_from('>H',data,off)[0];off+=2;cp[i]=data[off:off+length].decode('utf-8',errors='replace');off+=length
  elif tag in (3,4):off+=4
  elif tag in (5,6):off+=8;i+=1
  elif tag in (7,8,16,19,20):cp[i]=(tag,struct.unpack_from('>H',data,off)[0]);off+=2
  elif tag in (9,10,11,12,17,18):off+=4
  elif tag==15:off+=3
  else:raise ValueError('Unknown constant-pool tag '+str(tag))
  i+=1
 refs=set()
 for v in cp:
  if isinstance(v,tuple) and v[0]==7:
   value=cp[v[1]]
   if value.startswith('['):refs.update(re.findall(r'L([^;]+);',value))
   else:refs.add(value)
  elif isinstance(v,str) and v.startswith(('(','L','[','<')):refs.update(re.findall(r'L([\w/$]+);',v))
 return {n for n in refs if n.startswith(('net/minecraft/','net/minecraftforge/','cpw/mods/fml/'))}
def main():
 conf=json.loads((ROOT/'.toolchain/toolchain.json').read_text())
 with ZipFile(conf['forgeJar']) as z:available={n[:-6] for n in z.namelist() if n.endswith('.class')}
 aliases={'net/minecraft/util/math/MathHelper':'net/minecraft/util/MathHelper','net/minecraft/util/math/AxisAlignedBB':'net/minecraft/util/AxisAlignedBB','net/minecraft/util/text/TextComponentString':'net/minecraft/util/ChatComponentText','net/minecraft/util/text/ITextComponent':'net/minecraft/util/IChatComponent','net/minecraft/util/text/TextFormatting':'net/minecraft/util/EnumChatFormatting','net/minecraft/util/text/TextComponentTranslation':'net/minecraft/util/ChatComponentTranslation'}
 usage=collections.defaultdict(list);classes=0
 with ZipFile(ROOT/'upstream/srp-1.10.9-code-only.jar') as z:
  for name in z.namelist():
   if not name.endswith('.class'):continue
   classes+=1
   for value in class_types(z.read(name)):usage[value].append(name[:-6].replace('/','.'))
 missing=[];existing=0
 for name,users in sorted(usage.items()):
  relocated=aliases.get(name,name.replace('net/minecraftforge/fml/','cpw/mods/fml/'))
  if relocated in available:existing+=1;continue
  missing.append({'type':name.replace('/','.'),'candidateTarget':relocated.replace('/','.'),'usedByClasses':len(users),'examples':users[:6]})
 missing.sort(key=lambda d:(-d['usedByClasses'],d['type']))
 report={'sourceVersion':'1.12.2','targetVersion':'1.7.10','modClassesInspected':classes,'distinctReferencedPlatformTypes':len(usage),'typeNamesPresentAfterInitialRelocations':existing,'missingPlatformTypes':len(missing),'note':'Existing class names do not imply compatible methods, fields, arguments or behavior. Missing-type count is not a total compiler-error count.','missing':missing}
 (ROOT/'docs/api-gap-report.json').write_text(json.dumps(report,indent=2)+'\n',encoding='utf-8')
 print(json.dumps({k:v for k,v in report.items() if k!='missing'},indent=2))
 print('Most used missing platform types:')
 for row in missing[:15]:print(str(row['usedByClasses']).rjust(4),row['type'])
if __name__=='__main__':main()
