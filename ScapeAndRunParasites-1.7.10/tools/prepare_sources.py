"""Prepare a reproducible initial conversion; refuses to overwrite edited port sources."""
from pathlib import Path
from zipfile import ZipFile,ZIP_DEFLATED
import argparse,hashlib,json,re,collections
ROOT=Path(__file__).resolve().parents[1]
def main():
 parser=argparse.ArgumentParser();parser.add_argument('--code-only',action='store_true');args=parser.parse_args()
 config=json.loads((ROOT/'.toolchain/toolchain.json').read_text())
 reference=ROOT/'.toolchain/reference-1.12.2';dest=ROOT/'src/main/java'
 assert not dest.exists(),'Port sources exist: do not overwrite manual changes.'
 symbols={}
 for line in Path(config['srgToMcp']).read_text().splitlines():
  p=line.split()
  if p and p[0]=='MD:':old=p[1].rsplit('/',1)[-1];new=p[3].rsplit('/',1)[-1]
  elif p and p[0]=='FD:':old=p[1].rsplit('/',1)[-1];new=p[2].rsplit('/',1)[-1]
  else:continue
  assert old not in symbols or symbols[old]==new,(old,new)
  symbols[old]=new
 # Protect strings, character literals and comments: reflection/NBT identifiers must stay exact.
 lexical=re.compile(r'("(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|//[^\n]*|/\*[\s\S]*?\*/)')
 identifier=re.compile(r'\b(?:func_\d+_[A-Za-z0-9_]+|field_\d+_[A-Za-z0-9_]+)\b')
 relocations={'net.minecraftforge.fml.':'cpw.mods.fml.','net.minecraft.util.math.MathHelper':'net.minecraft.util.MathHelper','net.minecraft.util.math.AxisAlignedBB':'net.minecraft.util.AxisAlignedBB','net.minecraft.util.text.TextComponentString':'net.minecraft.util.ChatComponentText','net.minecraft.util.text.ITextComponent':'net.minecraft.util.IChatComponent','net.minecraft.util.text.TextFormatting':'net.minecraft.util.EnumChatFormatting','net.minecraft.util.text.TextComponentTranslation':'net.minecraft.util.ChatComponentTranslation'}
 simple={'TextComponentString':'ChatComponentText','ITextComponent':'IChatComponent','TextFormatting':'EnumChatFormatting','TextComponentTranslation':'ChatComponentTranslation'}
 counts=collections.Counter();files=sorted(reference.rglob('*.java'))
 assert len(files)==1094,len(files)
 for p in files:
  parts=lexical.split(p.read_text(encoding='utf-8'))
  for i in range(0,len(parts),2):
   def replace(m):
    key=m.group(0)
    if key in symbols:counts['mappedIdentifiers']+=1;return symbols[key]
    return key
   text=identifier.sub(replace,parts[i])
   for a,b in relocations.items():text=text.replace(a,b)
   for a,b in simple.items():text=re.sub(r'\b'+a+r'\b',b,text)
   parts[i]=text
  text=''.join(parts)
  if p.name=='SRPCoreMod.java':text=text.replace('@IFMLLoadingPlugin.MCVersion(value="1.12.2")','@IFMLLoadingPlugin.MCVersion(value="1.7.10")')
  if p.name=='SRPMain.java':text=text.replace('version="1.10.9", dependencies=', 'version="1.10.9-backport-dev", acceptedMinecraftVersions="[1.7.10]", dependencies=')
  out=dest/p.relative_to(reference);out.parent.mkdir(parents=True,exist_ok=True);out.write_text(text,encoding='utf-8')
 print('Prepared',len(files),'original Java source units;',counts['mappedIdentifiers'],'shared SRG identifiers converted. These sources still require semantic API porting.')
 archive=ROOT/'upstream/SRParasites-1.10.9-forgecdn.jar';lock=json.loads((ROOT/'upstream/upstream-lock.json').read_text())
 assert hashlib.sha256(archive.read_bytes()).hexdigest()==lock['sha256']
 assets=[]
 with ZipFile(archive) as z,ZipFile(ROOT/'.toolchain/upstream-libraries.jar','w',compression=ZIP_DEFLATED) as libs:
  for name in z.namelist():
   if name.endswith('/'):continue
   if name.startswith(('org/','com/llamalad7/')) and name.endswith('.class'):libs.writestr(name,z.read(name))
   if name.startswith('assets/') and not args.code_only:
    target=ROOT/'src/main/resources'/name
    assert target.resolve().is_relative_to((ROOT/'src/main/resources').resolve())
    data=z.read(name);target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(data)
    assets.append({'path':name,'sha256':hashlib.sha256(data).hexdigest(),'bytes':len(data)})
  meta=json.loads(z.read('mcmod.info'));meta[0]['version']='${version}';meta[0]['mcversion']='1.7.10';meta[0]['description']='Minecraft 1.7.10 port of Scape and Run: Parasites, intended for play alongside HBM and other 1.7.10 mods. Development build; not yet playable.';meta[0]['credits']='Minecraft 1.7.10 port by Zahin. '+meta[0].get('credits','')
  target=ROOT/'src/main/resources/mcmod.info';target.parent.mkdir(parents=True,exist_ok=True);target.write_text(json.dumps(meta,indent=2)+'\n',encoding='utf-8')
 (ROOT/'docs/asset-provenance.json').write_text(json.dumps({'upstreamSha256':lock['sha256'],'assets':assets},indent=2)+'\n',encoding='utf-8')
 print('Preserved',len(assets),'original assets; no replacement artwork or missing-behavior stubs generated.')
if __name__=='__main__':main()
