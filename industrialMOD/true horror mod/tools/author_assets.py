"""Original model meshes + deliberate, face-specific pixel art. No texture noise."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import json
import math
import shutil

project=Path(__file__).resolve().parents[1]
root=project/'src/main/resources/assets/truehorror'
for p in ['models','textures/entity','textures/gui','lang']:(root/p).mkdir(parents=True,exist_ok=True)
parts=[]
def box(name,size,pivot,origin=None,material='skin',role='',parent='',rotation=(0,0,0)):
    w,h,d=size
    parts.append(dict(name=name,size=size,pivot=pivot,origin=origin or [-w/2,0,-d/2],material=material,
                      role=role,parent=parent,rotation=rotation))
def limb(name,pivot,length=12,width=3,material='skin',role='',parent='',rotation=(0,0,0)):
    box(name,(width,length,width),pivot,material=material,role=role or name,parent=parent,rotation=rotation)
def fingers(parent,width=1,length=4):
    base=next(p['size'][1] for p in parts if p['name']==parent)
    for j in range(3):limb(parent+'_finger'+str(j),(-1+j,base-.5,-1),length,width,'claw','',parent,(-.12+j*.08,0,0))

def model(kind):
    global parts; parts=[]
    if kind in ('herobrine','shadow'):
        box('body',(8,12,4),(0,0,0),material='cloth',role='torso')
        box('head',(8,8,8),(0,0,0),(-4,-8,-4),'skin','head')
        limb('armL',(-6,1,0),12,4,'skin','armL');limb('armR',(6,1,0),12,4,'skin','armR')
        limb('legL',(-2,12,0),12,4,'trouser','legL');limb('legR',(2,12,0),12,4,'trouser','legR')
    elif kind=='watcher':
        box('ribcage',(5,22,4),(0,-26,0),material='hide',role='torso',rotation=(.12,0,0))
        box('shoulders',(10,3,4),(0,-26,0),material='hide')
        box('neck',(2,6,2),(0,-26,0),(-1,-6,-1),'hide')
        box('head',(5,8,5),(0,-32,-.5),(-2.5,-8,-2.5),'skin','head')
        limb('armL',(-5,-24,0),24,2,'hide','armL');limb('armR',(5,-24,0),24,2,'hide','armR')
        limb('foreL',(0,24,0),17,2,'hide','foreL','armL',(-.12,0,-.08))
        limb('foreR',(0,24,0),17,2,'hide','foreR','armR',(-.12,0,.08))
        fingers('foreL',1,5);fingers('foreR',1,5)
        limb('legL',(-1.7,-4,0),28,2,'hide','legL');limb('legR',(1.7,-4,0),28,2,'hide','legR')
    elif kind=='mimic':
        box('body',(9,10,6),(0,1,1),material='cloth',role='torso',rotation=(.27,0,-.05))
        box('hump',(7,6,5),(0,1,3),material='hide')
        box('head',(7,7,6),(0,1,-2),(-3.5,-7,-3),'skin','head')
        box('jaw',(7,3,5),(0,0,-2),(-3.5,0,-2.5),'jaw','jaw','head')
        limb('armL',(-6,2,0),11,3,'cloth','armL',rotation=(0,0,.14))
        limb('armR',(6,3,0),13,3,'skin','armR',rotation=(0,0,-.18))
        limb('foreL',(0,11,0),6,2,'skin','foreL','armL',(-.4,0,0));fingers('foreL',1,3)
        limb('foreR',(0,13,0),6,2,'skin','foreR','armR',(-.55,0,0));fingers('foreR',1,3)
        limb('legL',(-2.5,11,2),13,3,'trouser','legL');limb('legR',(2.5,11,1),13,3,'trouser','legR')
    elif kind=='crawler':
        box('body',(8,4,12),(0,15,1),material='hide',role='torso')
        box('head',(6,4,7),(0,16,-5),(-3,-2,-6),'skin','head')
        box('jaw',(6,2,5),(0,1,-4),(-3,0,-2.5),'jaw','jaw','head')
        for side,x in [('L',-5),('R',5)]:
            limb('arm'+side,(x,16,-3),7,2,'hide','arm'+side,rotation=(.2,0,.65 if x<0 else -.65))
            limb('fore'+side,(0,7,0),6,2,'skin','fore'+side,'arm'+side,(-.85,0,0));fingers('fore'+side,1,3)
            limb('leg'+side,(x,17,6),6,3,'hide','leg'+side,rotation=(-.9,0,.5 if x<0 else -.5))
            limb('shin'+side,(0,6,0),6,2,'skin','shin'+side,'leg'+side,(1.2,0,0))
        for j in range(5):box('spine'+str(j),(2,2,2),(0,13,j*2-2),material='claw')
    elif kind=='pale_man':
        box('body',(6,17,4),(0,-15,0),material='skin',role='torso',rotation=(.16,0,.025))
        box('shoulders',(9,3,4),(0,-16,0),material='skin')
        box('neck',(3,9,3),(0,-15,-1),(-1.5,-9,-1.5),'skin')
        box('head',(5,8,5),(0,-24,-1.5),(-2.5,-8,-2.5),'skin','head',rotation=(0,0,.08))
        box('jaw',(4,2,4),(0,-1,-1),(-2,0,-2),'jaw','jaw','head')
        for side,x in [('L',-5),('R',5)]:
            limb('arm'+side,(x,-14,0),21,2,'skin','arm'+side)
            limb('fore'+side,(0,21,0),12,2,'skin','fore'+side,'arm'+side,(-.12,0,0));fingers('fore'+side,1,4)
            limb('leg'+side,(x*.35,2,0),22,3,'skin','leg'+side)
        box('chestScar',(1,12,1),(0,-13,-2.1),material='scar')
    else:
        box('body',(12,7,11),(0,9,2),material='hide',role='torso',rotation=(.1,0,0))
        box('hump',(10,4,8),(0,6,4),material='hide')
        box('head',(8,7,8),(0,10,-4),(-4,-5,-6),'skin','head')
        box('jaw',(8,4,7),(0,1,-3),(-4,0,-3.5),'jaw','jaw','head')
        for side,x in [('L',-7),('R',7)]:
            limb('arm'+side,(x,10,-2),8,4,'hide','arm'+side,rotation=(.2,0,.3 if x<0 else -.3))
            limb('fore'+side,(0,8,0),7,3,'skin','fore'+side,'arm'+side,(-.4,0,0));fingers('fore'+side,1,4)
            limb('leg'+side,(x*.7,13,7),6,4,'hide','leg'+side,rotation=(-.7,0,.12 if x<0 else -.12))
            limb('shin'+side,(0,6,0),6,3,'skin','shin'+side,'leg'+side,(.8,0,0))
        for j in range(4):box('crest'+str(j),(3,3,2),(0,4,j*2+1),material='claw')
    return parts

palettes={
 'herobrine':{'skin':('#382520','#714a36','#ad7654','#c58e63'),'cloth':('#133c43','#175b63','#24818a','#33969a'),'trouser':('#242433','#35324d','#474064','#575073')},
 'watcher':{'skin':('#0b1112','#152023','#253136','#384449'),'hide':('#080d0f','#10191d','#1d292d','#2c393c')},
 'mimic':{'skin':('#272723','#5a5946','#8f8d6f','#bab493'),'cloth':('#202729','#3c4745','#58615a','#6a7368'),'hide':('#262c27','#4b5142','#6e725a','#8d9074'),'trouser':('#1b2026','#2c3440','#414b54','#5c6269')},
 'crawler':{'skin':('#231d1b','#5d4f42','#9b8c75','#b9ad91'),'hide':('#191c1b','#313b37','#525e52','#727d67')},
 'pale_man':{'skin':('#54554e','#91958a','#c2c6b6','#e0e0cb')},
 'shadow':{'skin':('#07080e','#0f111e','#212236','#34364b'),'void':('#04050a','#080b13','#101625','#1b2331'),'cloth':('#090b13','#121825','#202b3c','#334353')},
 'dweller':{'skin':('#242724','#4c5147','#7a8270','#abb09a'),'hide':('#181e20','#303b3c','#4b5b56','#728074')}
}
def colors(kind,mat):
    if mat=='claw':return ['#37352b','#77725d','#b3ab85','#d0c49a']
    if mat=='jaw':return ['#170f13','#39232a','#77514d','#c8bca0']
    if mat=='scar':return ['#33282a','#694e4b','#947368','#bb9683']
    return palettes[kind].get(mat,palettes[kind]['skin'])

def face(kind,part,orientation,size):
    w,h=size; palette=colors(kind,part['material']); im=Image.new('RGBA',(w,h),palette[1]);d=ImageDraw.Draw(im)
    # Fixed light direction and designed material edges, rather than random texture variation.
    for y in range(h):
        c=palette[2] if y<h//2 else palette[1]
        if orientation=='bottom':c=palette[0]
        if orientation=='top':c=palette[2]
        d.line((0,y,w-1,y),fill=c)
    d.line((0,0,0,h-1),fill=palette[0]);d.line((0,0,w-1,0),fill=palette[3])
    if w>=4 and h>=5:
        d.line((w-1,1,w-1,h-1),fill=palette[0]);d.line((1,h-1,w-2,h-1),fill=palette[0])
    mat=part['material'];name=part['name']
    if mat in ('cloth','trouser'):
        if w>3:d.line((1,2,1,h-2),fill=palette[3])
        if h>6:d.line((1,h-3,w-2,h-3),fill=palette[0])
        if w>5 and h>7:d.line((w//2,3,w//2,h-3),fill=palette[1])
    if mat in ('skin','hide') and h>=7 and w>=3 and name not in ('head','face'):
        d.line((w//2,2,w//2,min(h-2,5)),fill=palette[0])
        if h>=12:
            for y in (h//2,h//2+2):d.line((1,y,w-2,y),fill=palette[3])
    if mat=='claw':
        d.line((w//2,1,w//2,h-1),fill=palette[3])
    if mat=='jaw' and orientation=='front':
        d.rectangle((1,0,max(1,w-2),max(0,h-2)),fill='#130e12')
        for x in range(1,w-1,2):d.point((x,0),fill='#ddd0ac');d.point((x,h-2),fill='#a79b82')
    if name=='head' and orientation=='front' or name=='face' and kind=='shadow' and orientation=='front':
        if kind=='herobrine':
            d.rectangle((0,0,w-1,1),fill='#32251e');d.point((0,2),fill='#32251e');d.point((w-1,2),fill='#32251e')
            d.rectangle((1,3,2,3),fill='#edf7ef');d.rectangle((5,3,6,3),fill='#edf7ef')
            d.rectangle((3,4,4,5),fill='#87563c');d.rectangle((2,6,5,7),fill='#4c3328');d.line((3,6,4,6),fill='#a46f4c')
        elif kind=='watcher':
            d.rectangle((1,2,w-2,4),fill='#05090b');d.point((1,3),fill='#a1c7b9');d.point((w-2,3),fill='#a1c7b9')
            d.line((w//2,5,w//2,h-1),fill='#080c0d')
        elif kind=='mimic':
            d.line((1,2,2,2),fill='#191b16');d.point((w-2,2),fill='#dac997')
            d.line((1,h-2,w-2,h-1),fill='#312a26');d.line((w//2,0,w//2,2),fill='#4e5141')
        elif kind=='crawler':
            for x in (1,2,w-3,w-2):d.point((x,1),fill='#c1c2a4')
            d.line((1,h-1,w-2,h-1),fill='#29201e')
        elif kind=='pale_man':
            d.point((1,3),fill='#292f2e');d.point((w-2,3),fill='#292f2e');d.line((w//2,5,w//2,7),fill='#514a48')
            d.point((0,4),fill='#a3aa9d');d.point((w-1,4),fill='#a3aa9d')
        elif kind=='shadow':
            d.rectangle((1,1,w-2,h-1),fill='#03050a');d.point((1,2),fill='#929baa');d.point((w-2,2),fill='#929baa')
        else:
            d.rectangle((1,2,2,2),fill='#b9b384');d.rectangle((w-3,2,w-2,2),fill='#b9b384')
            d.line((w//2,0,w//2-1,4),fill='#b6b8a0');d.line((1,h-1,w-2,h-1),fill='#20191a')
    return im

contact=Image.new('RGB',(7*260,300),'#171b20');cd=ImageDraw.Draw(contact)
manifest=[]
for index,kind in enumerate(palettes):
    mesh=model(kind); atlas=Image.new('RGBA',(128,128)); glow=Image.new('RGBA',(128,128));x=y=0;row=0
    if kind in ('herobrine','shadow'):
        skin=project/'development-assets'/f'{kind}-user-skin.png'
        atlas=Image.open(skin).convert('RGBA')
        glow=Image.new('RGBA',atlas.size)
        uv={'body':(16,16),'head':(0,0),'armL':(40,16),'armR':(32,48),'legL':(0,16),'legR':(16,48)}
        for p in mesh:p['u'],p['v']=uv[p['name']]
        base=list(mesh)
        for source in base:
            overlay=dict(source);overlay['name']=source['name']+'_overlay';overlay['parent']=source['name'];overlay['role']=''
            overlay['pivot']=[0,0,0];overlay['rotation']=[0,0,0];overlay['inflate']=.25 if source['name']=='head' else .15
            overlayUV={'body':(16,32),'head':(32,0),'armL':(40,32),'armR':(48,48),'legL':(0,32),'legR':(0,48)}
            overlay['u'],overlay['v']=overlayUV[source['name']]
            mesh.append(overlay)
        for yy in range(8,16):
            for xx in range(8,16):
                r,g,b,a=atlas.getpixel((xx,yy))
                if a>0 and min(r,g,b)>185 and max(r,g,b)-min(r,g,b)<45:glow.putpixel((xx,yy),(r,g,b,a))
        (root/'models'/f'{kind}.json').write_text(json.dumps({'textureWidth':64,'textureHeight':64,'parts':mesh},indent=2),encoding='utf-8')
        shutil.copy2(skin,root/'textures/entity'/f'{kind}.png')
        glow.save(root/'textures/entity'/f'{kind}_eyes.png')
        preview=atlas.resize((256,256),Image.Resampling.NEAREST)
        contact.paste(preview,(index*260,28),preview);cd.text((index*260+4,7),kind+' (user skin)',fill='#d7dfd7')
        manifest.append({'creature':kind,'parts':len(mesh),'texture':'64x64','authorship':'user-supplied PNG, preserved byte-for-byte; canonical skin UV layout including clothing layers'})
        continue
    for p in mesh:
        w,h,d=p['size']; tw,th=2*(w+d),h+d
        if x+tw>128:x=0;y+=row+1;row=0
        if y+th>128:raise ValueError('Atlas overflow '+kind)
        p['u']=x;p['v']=y;row=max(row,th)
        for orient,ox,oy,fw,fh in [('left',0,d,d,h),('front',d,d,w,h),('right',d+w,d,d,h),('back',d+w+d,d,w,h),('top',d,0,w,d),('bottom',d+w,0,w,d)]:
            texture=face(kind,p,orient,(fw,fh));atlas.paste(texture,(x+ox,y+oy))
            if orient=='front' and (p['name']=='head' or kind=='shadow' and p['name']=='face'):
                gd=ImageDraw.Draw(glow)
                for yy in range(fh):
                    for xx in range(fw):
                        r,g,b,a=texture.getpixel((xx,yy))
                        if (kind=='herobrine' and g>220) or (kind=='watcher' and g>170) or (kind=='mimic' and r>200 and g>180) or (kind=='shadow' and r>130 and g>140) or (kind=='dweller' and r>170 and g>160 and yy==2):
                            gd.point((x+ox+xx,y+oy+yy),fill=(r,g,b,255))
        x+=tw+1
    # Origin and palette/UV descriptions are the editable source of the original artwork.
    descriptor={'textureWidth':128,'textureHeight':128,'parts':mesh}
    (root/'models'/f'{kind}.json').write_text(json.dumps(descriptor,indent=2),encoding='utf-8')
    atlas.save(root/'textures/entity'/f'{kind}.png');glow.save(root/'textures/entity'/f'{kind}_eyes.png')
    contact.paste(atlas.resize((256,256),Image.Resampling.NEAREST),(index*260,28),atlas.resize((256,256),Image.Resampling.NEAREST))
    cd.text((index*260+4,7),kind,fill='#d7dfd7')
    manifest.append({'creature':kind,'parts':len(mesh),'texture':'128x128','pixel_density':'one pixel per model unit','authorship':'original deliberate mesh and palette/face painting'})
preview=project/'development-assets';preview.mkdir(exist_ok=True)
contact.save(preview/'texture-atlases.png')
(preview/'art-manifest.json').write_text(json.dumps(manifest,indent=2),encoding='utf-8')
vignette=Image.new('RGBA',(128,128))
for y in range(128):
    for x in range(128):
        r=math.sqrt(((x-63.5)/64)**2+((y-63.5)/64)**2)
        vignette.putpixel((x,y),(9,8,18,int(min(0.92,max(0,(r-.3)/.95))*220)))
vignette.save(root/'textures/gui/vignette.png')
print('Authored seven original UV meshes/atlases:',', '.join(x['creature']+' '+str(x['parts'])+' parts' for x in manifest))
