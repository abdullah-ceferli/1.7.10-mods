"""Recover the referenced 1.12 sound catalog and hash-verified official audio resources."""
from pathlib import Path
from zipfile import ZipFile
from concurrent.futures import ThreadPoolExecutor
import csv, io, re, json, hashlib, urllib.request
R=Path(__file__).resolve().parents[1]

def main():
    reference=R/'.toolchain/sound-reference-112'
    with ZipFile(R/'.toolchain/mcp_stable-39-1.12.zip') as archive:
        fields={row['searge']:row['name'] for row in csv.DictReader(io.StringIO(archive.read('fields.csv').decode('utf-8')))}
    text=(reference/'net/minecraft/init/SoundEvents.java').read_text(encoding='utf-8')
    events={fields.get(field,field):event for field,event in re.findall(r'(field_\w+)\s*=\s*[\w.]+\("([^"]+)"\)',text)}
    assert len(events)==549,len(events)
    text=(reference/'net/minecraft/block/SoundType.java').read_text(encoding='utf-8');types={}
    for name,volume,pitch,sounds in re.findall(r'SoundType (field_\w+) = new SoundType\(([\d.]+)f, ([\d.]+)f, ([^;]+)\);',text):
        types[fields[name]]={'volume':float(volume),'pitch':float(pitch),'sounds':[events[fields[s]] for s in re.findall(r'SoundEvents\.(field_\w+)',sounds)]}
    used=set();sources=[]
    for path in (R/'src/main/java').rglob('*.java'):
        if '/backport/' in path.as_posix():continue
        source=path.read_text(encoding='utf-8');sources.append(source);used.update(re.findall(r'SoundEvents\.([A-Z_0-9]+)',source))
    assert used<=events.keys(),used-events.keys()
    registry=json.loads((reference/'sounds.json').read_text(encoding='utf-8'))
    roots={events[name] for name in used}|{sound for entry in types.values() for sound in entry['sounds']}
    for source in sources:
        roots.update(value.removeprefix('minecraft:') for value in re.findall(r'"([^"\\]+)"',source) if value.removeprefix('minecraft:') in registry)
    closure=set();clips=set();pending=list(roots)
    while pending:
        event=pending.pop()
        if event in closure:continue
        assert event in registry,event
        closure.add(event)
        for sound in registry[event]['sounds']:
            if isinstance(sound,str):clips.add(sound.removeprefix('minecraft:'))
            elif sound.get('type')=='event':pending.append(sound['name'].removeprefix('minecraft:'))
            else:clips.add(sound['name'].removeprefix('minecraft:'))
    index=json.loads((reference/'asset-index.json').read_text(encoding='utf-8'))['objects']
    size=sum(index['minecraft/sounds/'+clip+'.ogg']['size'] for clip in clips)
    plan={'allOriginalConstants':events,'usedConstants':sorted(used),'soundTypes':types,'events':sorted(closure),'rootEvents':sorted(roots),'clips':sorted(clips),'clipBytes':size}
    (R/'docs/vanilla-sound-plan.json').write_text(json.dumps(plan,indent=2)+'\n',encoding='utf-8')
    print('Selected',len(closure),'original events and',len(clips),'clips;',size,'bytes',flush=True)
    provenance={}
    def obtain(clip):
        key='minecraft/sounds/'+clip+'.ogg';record=index[key];path=R/'src/main/resources/assets/srpvanilla112/sounds'/(clip+'.ogg')
        if path.exists():data=path.read_bytes()
        else:
            digest=record['hash'];url='https://resources.download.minecraft.net/'+digest[:2]+'/'+digest
            data=urllib.request.urlopen(url,timeout=45).read()
            assert len(data)==record['size'] and hashlib.sha1(data).hexdigest()==digest,key
            path.parent.mkdir(parents=True,exist_ok=True);path.write_bytes(data)
        assert len(data)==record['size'] and hashlib.sha1(data).hexdigest()==record['hash'],key
        return path.relative_to(R).as_posix(),{'originalAsset':key,'sha1':record['hash'],'sha256':hashlib.sha256(data).hexdigest(),'bytes':len(data)}
    with ThreadPoolExecutor(max_workers=8) as pool:
        for path,record in pool.map(obtain,sorted(clips)):provenance[path]=record
    converted={}
    for event in sorted(closure):
        original=registry[event];entry={'category':'master','sounds':[]}
        for sound in original['sounds']:
            if isinstance(sound,str):entry['sounds'].append('srpvanilla112:'+sound.removeprefix('minecraft:'))
            else:
                copy=dict(sound);copy['name']='srpvanilla112:'+copy['name'].removeprefix('minecraft:');entry['sounds'].append(copy)
        converted[event]=entry
    output=R/'src/main/resources/assets/srpvanilla112/sounds.json'
    output.write_text(json.dumps(converted,indent=2)+'\n',encoding='utf-8')
    (R/'docs/vanilla-sound-resources.json').write_text(json.dumps({'assetIndexSha1':'a21e1ded1a24ea1548dd8db0cf30b6acb02655a9','soundRegistrySha1':hashlib.sha1((reference/'sounds.json').read_bytes()).hexdigest(),'selectedEvents':len(closure),'clips':len(clips),'totalClipBytes':size,'generatedSoundRegistrySha256':hashlib.sha256(output.read_bytes()).hexdigest(),'resources':provenance},indent=2)+'\n',encoding='utf-8')
    print('Verified all selected original clips; no audio was synthesized or substituted.')

if __name__=='__main__':main()
