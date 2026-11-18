"""Render the actual model JSON and PNG assets with a small orthographic Z-buffer."""
from pathlib import Path
from PIL import Image,ImageDraw,ImageFont
import numpy as np,json,math

project=Path(__file__).resolve().parents[1];root=project/'src/main/resources/assets/truehorror'
W,H=400,500;sheet=Image.new('RGB',(W*4,H*2),'#10151b')
fontPath='C:/Windows/Fonts/segoeui.ttf'
title=ImageFont.truetype(fontPath,23);small=ImageFont.truetype(fontPath,15)
kinds=[('herobrine','Herobrine','Your supplied skin'),('watcher','The Watcher','Still under your gaze'),('mimic','The Mimic','A familiar voice. The wrong face.'),('crawler','The Crawler','Low profile. Long reach.'),('pale_man','The Pale Man','Slow steps. Sudden pursuit.'),('shadow','The Shadow','Your supplied texture'),('dweller','The Dweller','Something below the stone')]
camera=np.array([.48,.23,-1.]);camera/=np.linalg.norm(camera)
right=np.array([1.,0,.48]);right/=np.linalg.norm(right);up=np.cross(right,camera);up/=np.linalg.norm(up)
light=np.array([-.5,.9,-.7]);light/=np.linalg.norm(light)

def matrix(p):
    x,y,z=p['rotation'];cx,sx=math.cos(x),math.sin(x);cy,sy=math.cos(y),math.sin(y);cz,sz=math.cos(z),math.sin(z)
    rx=np.array([[1,0,0],[0,cx,-sx],[0,sx,cx]])
    ry=np.array([[cy,0,sy],[0,1,0],[-sy,0,cy]])
    rz=np.array([[cz,-sz,0],[sz,cz,0],[0,0,1]])
    m=np.eye(4);m[:3,:3]=rz@ry@rx;m[:3,3]=p['pivot'];return m

def render(kind):
    descriptor=json.loads((root/'models'/f'{kind}.json').read_text())
    tex=np.array(Image.open(root/'textures/entity'/f'{kind}.png').convert('RGBA'))
    im=Image.new('RGB',(W,H),'#171f28');draw=ImageDraw.Draw(im)
    floor=H-62
    draw.ellipse((75,floor-13,325,floor+24),fill='#10161d');rgb=np.array(im).astype(float);depth=np.full((H,W),-1e9)
    transforms={};scale=5.45
    for part in descriptor['parts']:
        transform=matrix(part)
        if part['parent']:transform=transforms[part['parent']]@transform
        transforms[part['name']]=transform
        x,y,z=part['origin'];w,h,d=part['size'];infl=part.get('inflate',0)
        a,b,c=x-infl,y-infl,z-infl;X,Y,Z=x+w+infl,y+h+infl,z+d+infl
        vertices=np.array([[a,b,c],[X,b,c],[X,Y,c],[a,Y,c],[a,b,Z],[X,b,Z],[X,Y,Z],[a,Y,Z]])
        points=(transform@np.column_stack([vertices,np.ones(8)]).T).T[:,:3]
        points[:,1]=24-points[:,1]
        screen=np.column_stack([W/2+(points@right)*scale,floor-(points@up)*scale,points@camera])
        u,v=part['u'],part['v']
        faces=[([5,1,2,6],(u+d+w,v+d,u+2*d+w,v+d+h)),([0,4,7,3],(u,v+d,u+d,v+d+h)),
               ([5,4,0,1],(u+d,v,u+d+w,v+d)),([2,3,7,6],(u+d+w,v+d,u+d+2*w,v)),
               ([1,0,3,2],(u+d,v+d,u+d+w,v+d+h)),([4,5,6,7],(u+2*d+w,v+d,u+2*d+2*w,v+d+h))]
        for ids,(u1,v1,u2,v2) in faces:
            q=screen[ids];wp=points[ids];normal=np.cross(wp[1]-wp[0],wp[2]-wp[0]);norm=np.linalg.norm(normal)
            if norm==0:continue
            normal/=norm
            brightness=.58+.42*abs(float(normal@light))
            uv=np.array([[u2-.001,v1+.001],[u1+.001,v1+.001],[u1+.001,v2-.001],[u2-.001,v2-.001]])
            for tri in [[0,1,2],[0,2,3]]:
                p=q[tri];t=uv[tri]
                lo=np.maximum([0,50],np.floor(p[:,:2].min(axis=0)).astype(int));hi=np.minimum([W-1,H-26],np.ceil(p[:,:2].max(axis=0)).astype(int))
                if np.any(hi<lo):continue
                xx,yy=np.meshgrid(np.arange(lo[0],hi[0]+1),np.arange(lo[1],hi[1]+1))
                denominator=(p[1,1]-p[2,1])*(p[0,0]-p[2,0])+(p[2,0]-p[1,0])*(p[0,1]-p[2,1])
                if abs(denominator)<1e-7:continue
                A=((p[1,1]-p[2,1])*(xx-p[2,0])+(p[2,0]-p[1,0])*(yy-p[2,1]))/denominator
                B=((p[2,1]-p[0,1])*(xx-p[2,0])+(p[0,0]-p[2,0])*(yy-p[2,1]))/denominator;C=1-A-B
                zbuf=A*p[0,2]+B*p[1,2]+C*p[2,2]
                uu=np.clip((A*t[0,0]+B*t[1,0]+C*t[2,0]).astype(int),0,tex.shape[1]-1)
                vv=np.clip((A*t[0,1]+B*t[1,1]+C*t[2,1]).astype(int),0,tex.shape[0]-1)
                colors=tex[vv,uu]
                alpha=colors[:,:,3]/255
                valid=(A>=-1e-5)&(B>=-1e-5)&(C>=-1e-5)&(zbuf>=depth[yy,xx])&(alpha>.05)
                ys,xs=yy[valid],xx[valid];mix=alpha[valid,None]
                rgb[ys,xs]=colors[valid,:3]*brightness*mix+rgb[ys,xs]*(1-mix);depth[ys,xs]=zbuf[valid]
    return Image.fromarray(np.clip(rgb,0,255).astype('uint8'))

for index,(kind,name,note) in enumerate(kinds):
    panel=render(kind);d=ImageDraw.Draw(panel)
    d.text((20,14),name,font=title,fill='#e0e8e7');d.text((20,H-31),note,font=small,fill='#9daeb6')
    sheet.paste(panel,((index%4)*W,(index//4)*H))
d=ImageDraw.Draw(sheet);d.text((W*3+25,H+100),'TRUE HORROR',font=title,fill='#dce4df')
d.text((W*3+25,H+145),'Textured model preview',font=small,fill='#a6b6bb')
d.text((W*3+25,H+175),'Actual mesh + UV assets',font=small,fill='#a6b6bb')
d.text((W*3+25,H+205),'Minecraft 1.7.10',font=small,fill='#a6b6bb')
target=project/'development-assets/model-preview.png';sheet.save(target)
print(target)
