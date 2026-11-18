"""Export the pinned Display Delight registry and JSON geometry for native 1.7.10."""
from pathlib import Path
import re,json,gzip,shutil,math,hashlib,collections,subprocess
R=Path(__file__).resolve().parents[1];U=R/'upstream/displaydelight-1.20.1';A=U/'src/main/resources/assets/displaydelight';OUT=R/'src/main/resources/assets/displaydelight'
def js(p):return json.loads(p.read_text(encoding='utf-8'))
def write(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_text(json.dumps(d,indent=2)+'\n',encoding='utf-8')
def compressed(p,d):p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(gzip.compress(json.dumps(d,separators=(',',':')).encode(),mtime=0))
rows=[]
prefix={'cd':'corn_delight','ed':'expandeddelight','df':'delightful','pd':'pineapple_delight','od':'oceansdelight','ad':'alexsdelight','ctd':'culturaldelights','lm':'largemeals','fd':'festive_delight','bnc':'brewinandchewin','acd':'aquaculturedelight','erd':'endersdelight','edd':'ends_delight','mnd':'mynethersdelight','nd':'nethersdelight','crd':'crabbersdelight','abd':'abnormals_delight','fr':'farmersrespite','md':'minersdelight'}
for file in ['DisplayBlocks.java','PlatedBlocks.java','SmallPlatedBlocks.java','compat/ExpandedDelightDisplayBlocks.java']:
 t=(U/'src/main/java/com/jkvin114/displaydelight/init'/file).read_text(encoding='utf-8');t=re.sub(r'/\*.*?\*/|//[^\n]*','',t,flags=re.S)
 for m in re.finditer(r'public static final RegistryObject<Block>\s+\w+\s*=\s*(?:RegisterHelper\.)?(create\w+)\("([^"]+)"(?:,\s*(\d+))?\)',t):
  method,id,count=m.groups();small='Small' in method or 'SmallPlated' in file;plated='Plated' in method or 'PlatedBlocks' in file
  kind='small' if small else 'plate' if plated else 'drink' if 'Drink' in method else 'wide' if 'Wide' in method else 'food'
  name=id;parts=name.split('_',1);ns=prefix.get(parts[0],'farmersdelight')
  if parts[0] in prefix:name=parts[1]
  for p in ['small_plated_','plated_']:
   if name.startswith(p):name=name[len(p):];break
  if name in ['cookie','pumpkin_pie','mushroom_stew','rabbit_stew','beetroot_soup'] and ns=='farmersdelight':ns='minecraft'
  rows.append({'id':id,'kind':kind,'max':int(count or 1),'food':ns+':'+name,'steam':'Hot' in method,'fiery':'Fiery' in method})
rows.extend([{'id':'food_plate','kind':'empty','max':1,'food':'','steam':False,'fiery':False},{'id':'small_food_plate','kind':'empty_small','max':1,'food':'','steam':False,'fiery':False}])
assert len({d['id'] for d in rows})==len(rows)
for d in rows:assert (A/'blockstates'/(d['id']+'.json')).exists(),d
for folder in ['textures','models','blockstates']:shutil.copytree(A/folder,OUT/folder,dirs_exist_ok=True)
shutil.copytree(A/'textures/block',OUT/'textures/blocks',dirs_exist_ok=True)
(OUT/'lang').mkdir(parents=True,exist_ok=True)
for p in (A/'lang').glob('*.json'):
 values=js(p);locale=p.stem[:3]+p.stem[3:].upper();target=OUT/'lang'/(locale+'.lang')
 if target.exists():
  temporary=target.with_suffix('.case-tmp');target.rename(temporary);temporary.rename(target)
 target.write_text('\n'.join(('tile.'+k[6:]+'.name' if k.startswith('block.') else k)+'='+v.replace('\n','\\n') for k,v in values.items())+'\nitemGroup.displaydelight=Display Delight\n',encoding='utf-8')
cache={};used=set();missing=set();quads=0;repaired=collections.Counter()
def model(name):
 if ':' not in name:name='minecraft:'+name
 if name in cache:return cache[name]
 ns,path=name.split(':',1);p=U/'src/main/resources/assets'/ns/'models'/(path+'.json')
 if not p.exists():raise ValueError('Missing model '+name)
 d=js(p);base={}
 if d.get('parent'):
  par=d['parent']
  if not (par.startswith('minecraft:') or ':' not in par):base=model(par)
 result=dict(base);result.update(d);result['textures']={**base.get('textures',{}),**d.get('textures',{})};cache[name]=result;return result
def rotate(v,axis,angle,origin):
 a=math.radians(angle);c=math.cos(a);s=math.sin(a);p=[v[i]-origin[i] for i in range(3)];i,j={'x':(1,2),'y':(2,0),'z':(0,1)}[axis];p[i],p[j]=p[i]*c-p[j]*s,p[i]*s+p[j]*c;return [p[k]+origin[k] for k in range(3)]
def matches(condition,props):
 if not condition:return True
 if 'AND' in condition:return all(matches(c,props) for c in condition['AND'])
 if 'OR' in condition:return any(matches(c,props) for c in condition['OR'])
 return all(str(props.get(k,'')) in str(v).split('|') for k,v in condition.items())
def bake(application,hidden,kind):
 global quads
 if isinstance(application,list):application=application[0]
 d=model(application['model']);textures=d['textures'];result=[]
 for e in d.get('elements',[]):
  lo=e['from'];hi=e['to'];x0,y0,z0=lo;x1,y1,z1=hi
  verts={'down':[[x0,y0,z1],[x0,y0,z0],[x1,y0,z0],[x1,y0,z1]],'up':[[x0,y1,z0],[x0,y1,z1],[x1,y1,z1],[x1,y1,z0]],'north':[[x1,y1,z0],[x1,y0,z0],[x0,y0,z0],[x0,y1,z0]],'south':[[x0,y1,z1],[x0,y0,z1],[x1,y0,z1],[x1,y1,z1]],'west':[[x0,y1,z0],[x0,y0,z0],[x0,y0,z1],[x0,y1,z1]],'east':[[x1,y1,z1],[x1,y0,z1],[x1,y0,z0],[x1,y1,z0]]}
  for side,face in e.get('faces',{}).items():
   texture=face['texture'];seen=set()
   while texture.startswith('#'):
    if texture in seen:raise ValueError('Texture reference cycle')
    seen.add(texture)
    if texture[1:] not in textures:
     repaired[application['model']]+=1
     texture=next((f['texture'] for f in e.get('faces',{}).values() if f['texture'].startswith('#') and f['texture'][1:] in textures),next((v for k,v in textures.items() if k!='particle' and not v.startswith('#'))))
    else:texture=textures[texture[1:]]
   if ':' not in texture:texture='minecraft:'+texture
   # Native glass has moved from block/glass to blocks/glass.
   if texture=='minecraft:block/glass':texture='minecraft:blocks/glass'
   ns,path=texture.split(':',1)
   if ns!='minecraft' and not (U/'src/main/resources/assets'/ns/'textures'/(path+'.png')).exists():missing.add(texture)
   used.add(texture);v=[list(p) for p in verts[side]];rotation=e.get('rotation')
   if rotation:
    v=[rotate(p,rotation['axis'],rotation['angle'],rotation['origin']) for p in v]
    if rotation.get('rescale'):
     axis='xyz'.index(rotation['axis']);factor=1/math.cos(math.radians(rotation['angle']));origin=rotation['origin'];v=[[p[k] if k==axis else origin[k]+(p[k]-origin[k])*factor for k in range(3)] for p in v]
   if application.get('y'):v=[rotate(p,'y',-application['y'],[8,8,8]) for p in v]
   if application.get('x'):v=[rotate(p,'x',-application['x'],[8,8,8]) for p in v]
   if hidden:
    bounds=[4,0,4,12,1,12] if kind=='small' else [1,0,1,15,2,15]
    if all(all(bounds[k]-.016<=p[k]<=bounds[k+3]+.016 for k in range(3)) for p in v):continue
    if texture!='displaydelight:block/wood_support_oak':v=[[p[0],p[1]-(1 if kind=='small' else 2),p[2]] for p in v]
   uv=face.get('uv',[0,0,16,16]);u0,v0,u1,v1=uv;uvs=[[u0,v0],[u0,v1],[u1,v1],[u1,v0]]
   turn=(face.get('rotation',0)//90)%4;uvs=uvs[turn:]+uvs[:turn]
   result.append({'texture':texture,'side':side,'vertices':[[round(p[0]/16,6),round(p[1]/16,6),round(p[2]/16,6),*uvs[i]] for i,p in enumerate(v)]});quads+=1
 return result
meshes={}
for row in rows:
 state=js(A/'blockstates'/(row['id']+'.json'));variants={}
 for count in range(1,row['max']+1):
  for hidden in ([False,True] if row['kind'] in ['plate','small'] else [False]):
   for support in [False,True]:
    props={'facing':'north','stacks':str(count),'support':str(support).lower(),'plate_hidden':str(hidden).lower()};parts=[]
    for part in state.get('multipart',[]):
     if matches(part.get('when'),props):parts+=bake(part['apply'],hidden,row['kind'])
    for key,app in state.get('variants',{}).items():
     if matches(dict(p.split('=') for p in key.split(',') if p),props):parts+=bake(app,hidden,row['kind'])
    assert parts,(row,props);variants[f'{count}:{int(hidden)}:{int(support)}']=parts
 meshes[row['id']]=variants
assert not missing,sorted(missing)
compressed(OUT/'legacy/meshes.json.gz',meshes);write(OUT/'legacy/catalog.json',rows)
shutil.copy2(U/'LICENSE.txt',R/'LICENSE.txt')
(R/'docs').mkdir(exist_ok=True)
write(R/'docs/asset-export.json',{'sourceJavaUnits':len(list((U/'src/main/java').rglob('*.java'))),'registeredDisplays':len(rows),'kinds':dict(collections.Counter(x['kind'] for x in rows)),'meshStates':sum(map(len,meshes.values())),'bakedQuads':quads,'usedTextures':len(used),'unresolvedUsedTextures':sorted(missing),'sourceUndefinedFacesRepairedUsingAdjacentTexture':dict(repaired),'copiedTextureSha256':{p.relative_to(OUT).as_posix():hashlib.sha256(p.read_bytes()).hexdigest() for p in (OUT/'textures').rglob('*') if p.is_file()},'meshSha256':hashlib.sha256((OUT/'legacy/meshes.json.gz').read_bytes()).hexdigest()})
print('Exported',len(rows),'display types,',sum(map(len,meshes.values())),'mesh states and',quads,'quads; all textures resolved')
