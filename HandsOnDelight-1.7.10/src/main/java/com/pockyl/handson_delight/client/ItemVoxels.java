package com.pockyl.handson_delight.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

/** ItemVoxels.build's flat icon and exposed pixel edges, on the native texture atlas. */
final class ItemVoxels {
    static void draw(ItemStack stack,float size){draw(stack,size,0,1,0);}
    static void draw(ItemStack stack,float size,int cuts,int marks,int style){
        Minecraft mc=Minecraft.getMinecraft();IIcon icon=stack.getIconIndex();if(icon==null)return;
        net.minecraft.util.ResourceLocation atlas=stack.getItemSpriteNumber()==0?TextureMap.locationBlocksTexture:TextureMap.locationItemsTexture;TextureAtlasSprite sprite=((TextureMap)mc.getTextureManager().getTexture(atlas)).getAtlasSprite(icon.getIconName());
        SpritePixels pixels=SpritePixels.read(stack);int width=pixels.width,height=pixels.height;int[] data=pixels.argb;float pixel=size/Math.max(width,height);
        Line[] lines=lines(stack,size,marks,style);int[] pieces=new int[width*height];float[] shiftX=new float[pieces.length],shiftZ=new float[pieces.length];for(int r=0;r<height;r++)for(int c=0;c<width;c++){int index=r*width+c;float x=(width/2F-c-.5F)*pixel,z=(height/2F-r-.5F)*pixel;for(int cut=0;cut<Math.min(cuts,lines.length);cut++){Line line=lines[cut];boolean beyond=line.distance(x,z)>0;pieces[index]|=beyond?1<<cut:0;float half=(beyond?.8F:-.8F)/2/16;shiftX[index]+=line.nx*half;shiftZ[index]+=line.nz*half;}}
        mc.getTextureManager().bindTexture(atlas);GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glEnable(GL11.GL_ALPHA_TEST);GL11.glDisable(GL11.GL_CULL_FACE);Tessellator t=Tessellator.instance;t.startDrawingQuads();
        for(int row=0;row<height;row++)for(int column=0;column<width;column++){
            if(!solid(data,width,height,column,row))continue;
            int at=row*width+column,own=pieces[at];float left=(width/2F-column)*pixel+shiftX[at],right=left-pixel,far=(height/2F-row)*pixel+shiftZ[at],near=far-pixel;
            double u=icon.getInterpolatedU(column*16F/width),U=icon.getInterpolatedU((column+1)*16F/width),v=icon.getInterpolatedV(row*16F/height),V=icon.getInterpolatedV((row+1)*16F/height),cu=(u+U)/2,cv=(v+V)/2;
            t.setNormal(0,1,0);t.addVertexWithUV(left,pixel,far,u,v);t.addVertexWithUV(right,pixel,far,U,v);t.addVertexWithUV(right,pixel,near,U,V);t.addVertexWithUV(left,pixel,near,u,V);
            if(!joined(data,pieces,width,height,column-1,row,own))side(t,left,near,left,far,pixel,1,0,cu,cv);
            if(!joined(data,pieces,width,height,column+1,row,own))side(t,right,far,right,near,pixel,-1,0,cu,cv);
            if(!joined(data,pieces,width,height,column,row-1,own))side(t,left,far,right,far,pixel,0,1,cu,cv);
            if(!joined(data,pieces,width,height,column,row+1,own))side(t,right,near,left,near,pixel,0,-1,cu,cv);
        }
        t.draw();
    }
    private static boolean joined(int[] data,int[] pieces,int width,int height,int x,int y,int own){return solid(data,width,height,x,y)&&pieces[y*width+x]==own;}
    static final class Line {final float x,z,dx,dz,nx,nz;Line(float x,float z,float dx,float dz){this.x=x;this.z=z;this.dx=dx;this.dz=dz;nx=-dz;nz=dx;}float distance(float px,float pz){return nx*(px-x)+nz*(pz-z);}}
    /** ItemVoxels.Icon.axis: principal spread of solid icon pixels, facing its far end. */
    static Line[] lines(ItemStack stack,float size,int marks,int style){SpritePixels p=SpritePixels.read(stack);int w=p.width,h=p.height,count=0;double cx=0,cz=0,xx=0,zz=0,xz=0;for(int r=0;r<h;r++)for(int c=0;c<w;c++)if(solid(p.argb,w,h,c,r)){cx+=w/2F-c-.5F;cz+=h/2F-r-.5F;count++;}if(count==0)count=1;cx/=count;cz/=count;for(int r=0;r<h;r++)for(int c=0;c<w;c++)if(solid(p.argb,w,h,c,r)){double x=w/2F-c-.5-cx,z=h/2F-r-.5-cz;xx+=x*x;zz+=z*z;xz+=x*z;}double angle=.5*Math.atan2(2*xz,xx-zz);float dx=(float)Math.cos(angle),dz=(float)Math.sin(angle);if(dz<-.001F||Math.abs(dz)<=.001F&&dx<0){dx=-dx;dz=-dz;}float from=Float.MAX_VALUE,to=-Float.MAX_VALUE;for(int r=0;r<h;r++)for(int c=0;c<w;c++)if(solid(p.argb,w,h,c,r)){float along=(float)((w/2F-c-.5-cx)*dx+(h/2F-r-.5-cz)*dz);from=Math.min(from,along-.5F);to=Math.max(to,along+.5F);}if(from==Float.MAX_VALUE){from=-w/2F;to=w/2F;}float pixel=size/Math.max(w,h);Line[] lines=new Line[Math.max(1,marks)];for(int i=0;i<lines.length;i++){float spot=(i+1F)/(marks+1F),at=from+(to-from)*spot;if(style==2){float a=(float)Math.PI*i/lines.length;lines[i]=new Line((float)cx*pixel,(float)cz*pixel,(float)Math.cos(a),(float)Math.sin(a));}else lines[i]=new Line(((float)cx+dx*at)*pixel,((float)cz+dz*at)*pixel,-dz,dx);}return lines;}
    private static boolean solid(int[] data,int width,int height,int x,int y){return x>=0&&x<width&&y>=0&&y<height&&(data[y*width+x]>>>24)>=128;}
    private static void side(Tessellator t,float x,float z,float X,float Z,float top,float nx,float nz,double u,double v){t.setNormal(nx,0,nz);t.addVertexWithUV(x,0,z,u,v);t.addVertexWithUV(x,top,z,u,v);t.addVertexWithUV(X,top,Z,u,v);t.addVertexWithUV(X,0,Z,u,v);}
}
