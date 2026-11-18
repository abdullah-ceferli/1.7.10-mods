"""Audit original SRP sound declarations against the exact upstream archive, without inventing replacements."""
from pathlib import Path
from zipfile import ZipFile
import hashlib,json
R=Path(__file__).resolve().parents[1]
def main():
    archive=R/'upstream/SRParasites-1.10.9-forgecdn.jar'
    with ZipFile(archive) as jar:
        names=set(jar.namelist());folded={n.lower():n for n in names}
        original=jar.read('assets/srparasites/sounds.json')
        assert original==(R/'src/main/resources/assets/srparasites/sounds.json').read_bytes()
        events=json.loads(original);missing=[];empty=[];refs=[];files=0
        for event,entry in events.items():
            if not entry['sounds']:empty.append(event)
            for sound in entry['sounds']:
                value={'name':sound} if isinstance(sound,str) else sound
                domain,sep,path=value['name'].partition(':')
                if not sep:domain,path='srparasites',domain
                if value.get('type')=='event':
                    refs.append({'event':event,'reference':domain+':'+path});continue
                files+=1;name='assets/'+domain+'/sounds/'+path+'.ogg'
                if name not in names:missing.append({'event':event,'declaredResource':name,'caseInsensitiveMatch':folded.get(name.lower())})
        result={'inputSha256':hashlib.sha256(archive.read_bytes()).hexdigest(),'originalRegistryUnchanged':True,'declaredEvents':len(events),'fileDeclarations':files,'originalAudioFiles':sum(n.startswith('assets/srparasites/sounds/') and n.endswith('.ogg') for n in names),'missingFileDeclarations':len(missing),'uniqueMissingFiles':len({m['declaredResource'] for m in missing}),'missing':missing,'emptyEvents':empty,'eventReferences':refs,'note':'These declarations are already missing from the exact original 1.12.2 archive. No replacement audio or altered original registry is supplied.'}
        (R/'docs/original-sound-audit.json').write_text(json.dumps(result,indent=2)+'\n',encoding='utf-8')
        print(json.dumps({k:v for k,v in result.items() if k not in ('missing','emptyEvents','eventReferences')},indent=2))
if __name__=='__main__':main()
