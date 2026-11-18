"""Original layered sound design; deterministic DSP source, not borrowed recordings."""
from pathlib import Path
import numpy as np
import subprocess,wave,json,hashlib

project=Path(__file__).resolve().parents[1];root=project/'src/main/resources/assets/truehorror'
audio=root/'sounds';audio.mkdir(parents=True,exist_ok=True)
dev=project/'development-assets/audio';dev.mkdir(parents=True,exist_ok=True)
ffmpeg=project.parent/'.toolchain/audio-python/imageio_ffmpeg/binaries/ffmpeg-win-x86_64-v7.1.exe'
rate=44100;rng=np.random.default_rng(733071)
def tone(t,f,depth=0):return np.sin(2*np.pi*(f*t+depth*np.sin(2*np.pi*.7*t)))
def filtered(n,low,high):
    bins=np.fft.rfftfreq(len(n),1/rate);v=np.fft.rfft(n)
    shape=np.minimum(1,np.maximum(0,(bins-low)/max(1,low*.45+30)))*np.minimum(1,np.maximum(0,(high-bins)/max(1,high*.25)))
    return np.fft.irfft(v*shape,len(n))
def envelope(t,d,attack=.08,release=.45):return np.minimum(1,t/max(.001,attack))*np.minimum(1,np.maximum(0,(d-t)/max(.001,release)))
def echo(s,delay=.18,amount=.25):
    out=s.copy()
    for i in range(1,5):
        n=int(delay*rate*i)
        if n<len(out):out[n:]+=s[:-n]*amount**i
    return out
def pulse(t,at,duration=.13):return np.exp(-np.maximum(0,t-at)*40)*((t>=at)&(t<at+duration))
def sound(style,d=3,base=70,variant=0):
    t=np.arange(int(d*rate))/rate;n=rng.normal(0,1,len(t));e=envelope(t,d);phase=variant*.13
    if style=='steps':
        s=np.zeros_like(t)
        for a in (.2,.7,1.18,1.65,2.06):s+=pulse(t,a)*(.24*filtered(n,140,1500)+.25*tone(t,base))
    elif style=='knock':
        s=np.zeros_like(t)
        for a in (.18,.43,1.28):s+=pulse(t,a,.2)*(.27*tone(t,base)+.18*tone(t,base*2.71)+.08*filtered(n,400,2500))
    elif style=='scratch':
        s=filtered(n,600,4200)*(np.maximum(0,np.sin(2*np.pi*(1.5*t+.18*np.sin(t*5))))**5)*e
        s=.2*s+.05*tone(t,base)*e
    elif style=='breath':
        airflow=filtered(n,200,2100);s=airflow*(.25+.5*np.maximum(0,np.sin(2*np.pi*.47*t+phase)))*e
        s+=.04*tone(t,base,1.5)*e
    elif style=='whisper':
        airy=filtered(n,800,5500);syllables=np.maximum(0,np.sin(2*np.pi*(1.7*t+.09*np.sin(t*8))))**2
        formant=(.45+.2*np.sin(2*np.pi*740*t)+.15*np.sin(2*np.pi*1630*t))
        s=airy*syllables*formant*e*.55+.035*tone(t,base,2)*e
    elif style=='laugh':
        bursts=np.maximum(0,np.sin(2*np.pi*(2.2*t-.13*t*t)))**3
        voice=sum(tone(t,base*i,.15*i)/i for i in range(1,7))
        s=(voice*.10+filtered(n,400,1800)*.12)*bursts*e
    elif style=='snap':
        s=filtered(n,500,7000)*pulse(t,.18,.15)*.8+filtered(n,80,450)*pulse(t,.2,.3)*.4
        s=echo(s,.12,.36)
    elif style=='sting':
        sweep=np.sin(2*np.pi*(90*t+260*t*t/(d+1)))
        scrape=filtered(n,500,3800)
        hit=pulse(t,.08,.8)
        s=(.19*sweep+.10*tone(t,base*1.057,1)+.16*scrape)*envelope(t,d,.025,.9)
        s+=hit*(.5*tone(t,45)+.25*filtered(n,100,1300))
    else:
        # Formant-like creature throat with a rising then falling pitch contour.
        contour=base*(1+.12*np.sin(2*np.pi*.4*t)+.035*np.sin(2*np.pi*7*t))
        p=np.cumsum(contour)/rate
        voice=sum(np.sin(2*np.pi*p*i)/(i**1.25) for i in range(1,10))
        s=(.19*voice+.19*filtered(n,200,2300))*(.45+.55*np.sin(np.pi*t/d)**2)*e
    s=echo(s,.17+variant*.013,.30 if style not in ('steps','snap') else .15)
    s*=envelope(t,d,.012,.12)
    s=np.tanh(s*1.5)
    peak=max(.001,float(np.max(np.abs(s))));target=.63 if style=='sting' else .48
    s=s*(target/peak)
    return s

definitions={
 'footsteps':('steps',2.8,82),'running':('steps',2.5,54),'branches':('snap',1.6,150),
 'breathing':('breath',4.2,62),'whispers':('whisper',4.5,83),'laughter':('laugh',3.4,118),
 'knocking':('knock',2.8,175),'mining':('knock',3.2,410),'scratching':('scratch',3.2,92),
 'distant_scream':('voice',5.1,170),'tunnel_echo':('voice',4.8,44),'torch':('snap',1.5,310),
 'wake':('whisper',3.8,52),'sting_behind':('sting',2.8,61),'sting_mining':('sting',2.3,126),
 'sting_rush':('sting',3.5,47),'sting_window':('sting',2.7,93)
}
for j,kind in enumerate(['herobrine','watcher','mimic','crawler','pale_man','shadow','dweller']):
    styles=['whisper','breath','laugh','scratch','breath','whisper','voice']
    bases=[54,37,133,89,67,46,38]
    definitions['creatures/'+kind+'_ambient']=(styles[j],4 if j in (0,5,6) else 3.4,bases[j])
    definitions['creatures/'+kind+'_hunt']=('voice' if j!=3 else 'scratch',2.8,bases[j]*1.4)
    definitions['creatures/'+kind+'_hurt']=('voice',.95,bases[j]*1.9)
registry={};manifest=[]
for index,(name,(style,d,base)) in enumerate(definitions.items()):
    samples=sound(style,d,base,index%4)
    wav=dev/(name.replace('/','_')+'.wav')
    with wave.open(str(wav),'wb') as w:
        w.setnchannels(1);w.setsampwidth(2);w.setframerate(rate);w.writeframes((samples*32767).astype('<i2').tobytes())
    target=audio/(name+'.ogg');target.parent.mkdir(exist_ok=True)
    subprocess.run([str(ffmpeg),'-hide_banner','-loglevel','error','-y','-i',str(wav),'-c:a','libvorbis','-q:a','5',str(target)],check=True)
    event=name.replace('/','.').replace('_ambient','.ambient').replace('_hunt','.hunt').replace('_hurt','.hurt') if name.startswith('creatures/') else name
    registry[event]={'category':'hostile' if name.startswith('creatures/') or name.startswith('sting') else 'ambient','sounds':[{'name':'truehorror:'+name,'stream':False}]}
    manifest.append(dict(event=event,duration_seconds=d,style=style,peak=float(np.max(np.abs(samples))),rms=float(np.sqrt(np.mean(samples*samples))),sha256=hashlib.sha256(target.read_bytes()).hexdigest()))
(root/'sounds.json').write_text(json.dumps(registry,indent=2),encoding='utf-8')
(dev/'manifest.json').write_text(json.dumps(manifest,indent=2),encoding='utf-8')
print('Rendered',len(registry),'original mono positional Vorbis sound designs.')
