"""Encode PNG color-key transparency as explicit alpha for the Java 8 texture loader.

Upstream resources remain untouched. Only RGB/grayscale tRNS files need this
lossless packaging step; palette, RGBA and opaque images pass through unchanged.
Uses the Python standard library so builds need no image-processing dependency.
"""
import struct
import zlib

SIGNATURE=b'\x89PNG\r\n\x1a\n'

def _chunk(kind,data):
    return struct.pack('>I',len(data))+kind+data+struct.pack('>I',zlib.crc32(kind+data)&0xffffffff)

def _paeth(a,b,c):
    p=a+b-c
    distances=abs(p-a),abs(p-b),abs(p-c)
    return a if distances[0]<=min(distances[1:]) else b if distances[1]<=distances[2] else c

def legacy_png(raw):
    if not raw.startswith(SIGNATURE):
        raise ValueError('Not a PNG image')
    chunks=[]
    offset=8
    while offset<len(raw):
        length=struct.unpack_from('>I',raw,offset)[0]
        kind=raw[offset+4:offset+8]
        data=raw[offset+8:offset+8+length]
        crc=struct.unpack_from('>I',raw,offset+8+length)[0]
        if zlib.crc32(kind+data)&0xffffffff!=crc:
            raise ValueError('Invalid PNG chunk checksum')
        chunks.append((kind,data))
        offset+=length+12
    header=next(data for kind,data in chunks if kind==b'IHDR')
    width,height,depth,color,compression,filtering,interlace=struct.unpack('>IIBBBBB',header)
    transparency=next((data for kind,data in chunks if kind==b'tRNS'),None)
    if transparency is None or color not in (0,2):
        return raw
    if depth!=8 or interlace or compression or filtering:
        raise ValueError('Expected non-interlaced 8-bit RGB/grayscale color-key PNG')
    channels=1 if color==0 else 3
    key=struct.unpack('>'+'H'*channels,transparency)
    pixels=zlib.decompress(b''.join(data for kind,data in chunks if kind==b'IDAT'))
    stride=width*channels
    if len(pixels)!=(stride+1)*height:
        raise ValueError('Invalid PNG scanline length')
    previous=bytearray(stride)
    rgba=bytearray()
    for y in range(height):
        start=y*(stride+1)
        method=pixels[start]
        row=bytearray(pixels[start+1:start+1+stride])
        for x in range(stride):
            left=row[x-channels] if x>=channels else 0
            above=previous[x]
            corner=previous[x-channels] if x>=channels else 0
            if method==0:predictor=0
            elif method==1:predictor=left
            elif method==2:predictor=above
            elif method==3:predictor=(left+above)//2
            elif method==4:predictor=_paeth(left,above,corner)
            else:raise ValueError('Unknown PNG scanline filter')
            row[x]=(row[x]+predictor)&255
        rgba.append(0)
        for x in range(0,stride,channels):
            sample=tuple(row[x:x+channels])
            rgb=sample*3 if channels==1 else sample
            rgba.extend((*rgb,0 if sample==key else 255))
        previous=row
    output=bytearray(SIGNATURE)
    written=False
    for kind,data in chunks:
        if kind==b'IHDR':data=struct.pack('>IIBBBBB',width,height,8,6,0,0,0)
        elif kind==b'tRNS' or kind==b'hIST':continue
        elif kind==b'IDAT':
            if written:continue
            data=zlib.compress(rgba,9)
            written=True
        elif kind==b'bKGD' and channels==1:data=data*3
        elif kind==b'sBIT':data=(data*3 if channels==1 else data)+b'\x08'
        output.extend(_chunk(kind,data))
    return bytes(output)
