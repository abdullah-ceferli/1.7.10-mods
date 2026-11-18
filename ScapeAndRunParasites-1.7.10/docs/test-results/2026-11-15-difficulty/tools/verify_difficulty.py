"""Compile the shared adapters and compare difficulty with original 1.12 bytecode."""
from pathlib import Path
import json,subprocess
R=Path(__file__).resolve().parents[1]

def main():
 c=json.loads((R/'.toolchain/toolchain.json').read_text(encoding='utf-8'));out=R/'build/difficulty-checks';out.mkdir(parents=True,exist_ok=True)
 cp=c.get('forgeCompileJar',c['forgeJar'])+';'+Path(c['runtimeClasspathFile']).read_text(encoding='utf-8').strip()
 files=sorted((R/'src/main/java/com/dhanantry/scapeandrunparasites/backport').rglob('*.java'))+[R/'src/compatTest/java/DifficultyParityChecks.java']
 args=['-proc:none','-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out)]+list(map(str,files))
 argfile=out/'javac.args';argfile.write_text('\n'.join('"'+a.replace('\\','/')+'"' for a in args)+'\n',encoding='utf-8')
 subprocess.run([c['javac'],'@'+str(argfile)],cwd=R,check=True)
 mappings=[x.split() for x in (R/'.toolchain/joined-1.12.2.srg').read_text(encoding='utf-8').splitlines() if x.strip()]
 names={}
 for key,owner in [('class','net/minecraft/world/DifficultyInstance'),('enum','net/minecraft/world/EnumDifficulty')]:names[key]=next(x[1].replace('/','.') for x in mappings if x[0]=='CL:' and x[2]==owner)
 for key,srg in [('additional','func_180168_b'),('clamped','func_180170_c'),('harder','func_193845_a')]:names[key]=next(x[1].rsplit('/',1)[1] for x in mappings if x[0]=='MD:' and x[3]=='net/minecraft/world/DifficultyInstance/'+srg)
 props=out/'reference.properties';props.write_text('\n'.join(k+'='+v for k,v in names.items())+'\n',encoding='utf-8')
 subprocess.run([c['java'],'-cp',str(out)+';'+cp+';'+str(R/'.toolchain/minecraft-1.12.2-client.jar'),'dev.srpbackport.tests.DifficultyParityChecks',str(props),str(R/'docs/difficulty-parity-checks.txt')],cwd=R,check=True)

if __name__=='__main__':main()
