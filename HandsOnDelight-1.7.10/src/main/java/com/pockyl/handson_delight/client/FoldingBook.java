package com.pockyl.handson_delight.client;

import com.pockyl.handson_delight.*;
import java.util.*;
import java.nio.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.input.Keyboard;
import vectorwing.farmersdelight.legacy.*;

/** Native rendering and picking for the upstream cookbook's model, paper and 72 by 96 page layout. */
public final class FoldingBook {
    private static final ResourceLocation PAPER=new ResourceLocation("handson_delight:textures/entity/recipe_book_paper.png");
    private static final int INK=0xff4a3418,WELL=0x1e5a3c18,READY=0x70e0a83c,PICKED=0xff8a5a2b;
    private static final List<String> RECENT=new ArrayList<String>();
    private final StationScreen screen;private final Minecraft mc=Minecraft.getMinecraft();private final RecipeBookModel model=new RecipeBookModel();
    private final RenderItem items=new RenderItem();private final Canvas[] pages={new Canvas(),new Canvas()};
    private final Canvas cover=new Canvas();private final String memory;
    private final List<RecipeBook.Entry> all;private List<RecipeBook.Entry> shown=new ArrayList<RecipeBook.Entry>();
    private RecipeBook.Entry selected,hovered;private ItemStack pointed;private String query="",tab="all",clicked="";
    private int page,oldPage,heldTurns,heldWay;private boolean filter,typing,closed;private float open,flip=1,heldFrom;
    private long started=System.nanoTime(),last=started;private double seconds;private int mouseX,mouseY;private float[] left,right;
    public String lastFillRecipe="";public boolean lastFillSuccess;
    private float size,offsetX,offsetY,dragSize,dragX,dragY;private int dragMode,grabX,grabY;
    private final Map<String,Integer> rowMemory=new HashMap<String,Integer>();private String rowsFor="";
    private int firstRow,totalRows,visibleRows;private float rowTop,barHigh,gripTop,gripHigh,gripGrab;private boolean scrolling;
    public FoldingBook(StationScreen s){screen=s;all=RecipeBook.list(s.tile);memory=s.tile.getClass().getSimpleName();String saved=BookTabs.get(memory);if(saved!=null)tab=saved;filter="true".equals(BookTabs.get(memory+".ready"));size=NativeMath.clamp(BookTabs.number("book.size",1),.75F,1.6F);offsetX=BookTabs.number("book.x",0);offsetY=BookTabs.number("book.y",0);refresh();}
    public int recipes(){return all.size();}public boolean searching(){return typing;}
    private boolean hasSearch(){return typing||!query.isEmpty();}
    public int visibleRecipes(){return shown.size();}
    public RecipeBook.Entry selected(){return selected;}
    public ItemStack pointed(){return pointed;}
    private boolean ready(RecipeBook.Entry e){
        ItemStack[] inventory=new ItemStack[36];for(int i=0;i<36;i++)inventory[i]=Stacks.empty(mc.thePlayer.inventory.getStackInSlot(i))?null:mc.thePlayer.inventory.getStackInSlot(i).copy();
        for(List<ItemStack> options:e.displayInputs()){boolean found=false;for(int i=0;i<36;i++)if(!Stacks.empty(inventory[i])&&Stacks.matches(options,inventory[i])){inventory[i].stackSize--;found=true;break;}if(!found)return false;}return true;
    }
    private void refresh(){
        shown.clear();for(RecipeBook.Entry e:all){
            if(!BookMath.matches(query,e.output.getDisplayName(),e.id)||filter&&!ready(e))continue;
            if(tab.equals("recent")&&!RECENT.contains(e.id)||tab.equals("assembly")&&e.assembly==null||tab.equals("cut")&&!e.id.startsWith("cut/"))continue;shown.add(e);
        }
        Collections.sort(shown,new Comparator<RecipeBook.Entry>(){public int compare(RecipeBook.Entry a,RecipeBook.Entry b){int c=Boolean.compare(ready(b),ready(a));if(c!=0)return c;return a.output.getDisplayName().compareToIgnoreCase(b.output.getDisplayName());}});
        page=BookMath.turned(page,0,(shown.size()+11)/12,false);if(selected==null&&!shown.isEmpty())selected=shown.get(0);
    }
    public void tick(){if(screen.clock()%20==0)refresh();if(heldWay!=0){float elapsed=(float)seconds-heldFrom;int due=BookMath.turnsDue(elapsed);if(due>heldTurns){turn(heldWay*(due-heldTurns));heldTurns=due;}}}
    public void draw(int mx,int my,float partial){
        mouseX=mx;mouseY=my;long now=System.nanoTime();float dt=Math.min(.1F,(now-last)/1e9F);last=now;seconds=(now-started)/1e9;
        if(dragMode==1){float[] p=screen.scene.bookPlane(mx,my,screen.width,screen.height),grab=screen.scene.bookPlane(grabX,grabY,screen.width,screen.height);offsetX=dragX+p[0]-grab[0];offsetY=dragY+p[1]-grab[1];}
        if(dragMode==2)size=NativeMath.clamp(dragSize*(1+(mx-grabX-my+grabY)/(screen.height*.35F)),.75F,1.6F);
        float[] corners=new float[8];for(int i=0;i<4;i++){float[] p=screen.scene.bookPlane(i==1||i==2?screen.width:0,i>=2?screen.height:0,screen.width,screen.height);corners[i*2]=p[0];corners[i*2+1]=p[1];}
        float[] fitted=fit(corners,size,offsetX,offsetY,0,0);size=fitted[0];offsetX=fitted[1];offsetY=fitted[2];
        open=NativeMath.clamp(open+(closed||screen.appear()<.98F?-dt:dt)/.31F,0,1);flip=Math.min(1,flip+dt/.4F);float eased=open*open*(3-2*open),angle=1.25F*1.17F*eased;
        left=pages[0].point(mx,my);right=pages[1].point(mx,my);hovered=null;pointed=null;
        if(scrolling&&right!=null&&barHigh>gripHigh)scrollTo(Math.round((right[1]-gripGrab-rowTop)*(totalRows-visibleRows)/(barHigh-gripHigh)));
        if(open>.98F&&left!=null&&left[1]>=5&&left[1]<77&&left[0]>=7&&left[0]<61){int col=(int)(left[0]-7)/18,row=(int)(left[1]-5)/18,index=page*12+row*3+col;if(col<3&&row<4&&index<shown.size()){hovered=shown.get(index);pointed=hovered.output;}}
        if(hovered!=null)selected=hovered;
        RenderState saved=RenderState.capture();screen.scene.begin(screen.width,screen.height);
        try{
            GL11.glEnable(GL11.GL_DEPTH_TEST);GL11.glDepthMask(true);GL11.glDisable(GL11.GL_LIGHTING);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            // The original scene node and RecipeBook.SIZE, with the native model axes turned into the authored lying-book axes.
            screen.scene.node("book_open");GL11.glTranslatef(offsetX,0,offsetY);float appear=Math.max(.001F,screen.appear());GL11.glScalef(.72F*size*appear,.72F*size*appear,.72F*size*appear);
            FloatBuffer lying=BufferUtils.createFloatBuffer(16);lying.put(new float[]{-1,0,0,0,0,0,1,0,0,1,0,0,0,0,0,1}).flip();GL11.glMultMatrix(lying);
            GL11.glTranslatef(-3*(1-eased),0,0);GL11.glRotatef(90*(1-eased),0,1,0);
            GL11.glPushMatrix();GL11.glRotatef(-90,0,1,0);cover.capture();model.render(angle);GL11.glPopMatrix();
            if(open>0.01F){
                drawPage(true,angle,0,true,page);drawPage(false,angle,0,true,page);
                if(flip<1){float f=flip*flip*(3-2*flip);drawPage(f>.5F,angle,(float)Math.PI*(1-f),false,f>.5F?page:oldPage);}
            }
        }finally{
            screen.scene.end();saved.close();
        }
        if(!Stacks.empty(pointed))screen.tooltip(pointed,mx,my);
    }
    private void drawPage(boolean leftPage,float angle,float turning,boolean resting,int listPage){
        GL11.glPushMatrix();float sin=(float)Math.sin(angle),cos=(float)Math.cos(angle),gap=.09F;
        GL11.glTranslatef(leftPage?-6*sin+cos*gap:-cos*gap,4,sin*(1+gap)+(leftPage?6*cos:0));
        FloatBuffer matrix=BufferUtils.createFloatBuffer(16);
        float[] m={sin/12,0,(leftPage?-cos:cos)/12,0, 0,-1F/12,0,0, (leftPage?cos:-cos)*.015F,0,sin*.015F,0, 0,0,0,1};matrix.put(m).flip();GL11.glMultMatrix(matrix);
        if(turning!=0){GL11.glTranslatef(leftPage?72:0,0,8);GL11.glRotatef(turning*180F/(float)Math.PI,0,1,0);GL11.glTranslatef(leftPage?-72:0,0,0);}
        Canvas c=pages[leftPage?0:1];if(resting)c.capture();c.paper(leftPage);c.rect(leftPage?67:0,0,5,96,.1F,0x285a3c18);c.rect(leftPage?70:0,0,2,96,.15F,0x285a3c18);
        if(leftPage){
            if(resting&&!hasSearch()){tab(c,7,"all",new ItemStack(Items.compass));tab(c,25,"recent",new ItemStack(Items.clock));if(screen.tile instanceof CuttingBoardTile){tab(c,43,"assembly",new ItemStack(Content.ITEMS.get("hamburger")));tab(c,61,"cut",new ItemStack(Content.ITEMS.get("iron_knife")));}}
            if(hasSearch()){c.slip(2,68,11);c.magnifier(4,-9);String shown=query;while(!shown.isEmpty()&&mc.fontRenderer.getStringWidth(shown)>54)shown=shown.substring(1);c.text(shown,13,-9,1);if(typing&&((int)(seconds*2)&1)==0)c.rect(13+mc.fontRenderer.getStringWidth(shown),-10,1,9,1.9F,INK);}
            for(int i=0;i<12&&listPage*12+i<shown.size();i++){
                RecipeBook.Entry e=shown.get(listPage*12+i);int x=7+i%3*18,y=5+i/3*18;boolean available=ready(e);c.rect(x,y,17,17,.3F,available?READY:WELL);c.item(e.output,x+.5F,y+.5F,16,!available);if(e==selected)c.frame(x,y,17);
            }
            int n=Math.max(1,(shown.size()+11)/12);if(n>1){c.arrow(7,81,false);c.arrow(48,81,true);String number=(listPage+1)+"/"+n;c.text(number,33.5F-mc.fontRenderer.getStringWidth(number)/2F,83,1);}
        }else{
            tab(c,6,"filter",new ItemStack(net.minecraft.init.Blocks.crafting_table));tab(c,24,"search",null);c.magnifier(28.5F,hasSearch()?-11:-8);c.ribbon(right!=null&&right[0]>=60&&right[0]<70&&right[1]>=-2&&right[1]<15);
            RecipeBook.Entry e=selected;if(e!=null){
                c.item(e.output,28.5F,3,20);List<String> lines=mc.fontRenderer.listFormattedStringToWidth(e.output.getDisplayName(),61);float line=25;
                for(int i=0;i<Math.min(2,lines.size());i++){String text=lines.get(i);c.text(text,38.5F-mc.fontRenderer.getStringWidth(text)/2F,line,1);line+=9;}
                if(screen.tile instanceof CookingPotTile){drawStars(c,e,38.5F,line);line+=9;}
                c.rect(12,line+.5F,53,.6F,.3F,0x405a3c18);line+=3;
                List<List<ItemStack>> inputs=e.displayInputs();int rows=(inputs.size()+2)/3,overFoot=Math.max(0,(int)Math.floor((80.5F-line+1)/18));boolean withFoot=rows<=overFoot||overFoot>=2;
                int visible=withFoot?Math.min(rows,overFoot):Math.max(1,Math.min(rows,(int)Math.floor((93-line+1)/18)));
                if(!e.id.equals(rowsFor)){rowsFor=e.id;firstRow=rowMemory.containsKey(e.id)?rowMemory.get(e.id):0;}firstRow=Math.max(0,Math.min(rows-visible,firstRow));
                totalRows=rows;visibleRows=visible;rowTop=line;barHigh=visible*18-1;gripHigh=Math.max(6,barHigh*visible/Math.max(1,rows));gripTop=line+(barHigh-gripHigh)*firstRow/Math.max(1,rows-visible);
                if(visible<rows){c.rect(66.5F,line,2,barHigh,.3F,0x285a3c18);c.rect(66.5F,gripTop,2,gripHigh,.35F,scrolling?PICKED:0xa08a5a2b);}
                for(int i=firstRow*3;i<Math.min(inputs.size(),(firstRow+visible)*3);i++){int inRow=Math.min(3,inputs.size()-i/3*3);float x=38.5F-(inRow*18-1)/2F+i%3*18,y=line+(i/3-firstRow)*18;
                    List<ItemStack> choices=inputs.get(i);if(choices.isEmpty())continue;ItemStack shown=choices.get(((int)seconds)%choices.size());boolean found=has(shown);c.rect(x,y,17,17,.3F,found?READY:WELL);c.item(shown,x+.5F,y+.5F,16,!found);
                    if(resting&&right!=null&&right[0]>=x&&right[0]<x+17&&right[1]>=y&&right[1]<y+17)pointed=shown;
                }
                if(withFoot){float width=e.foot.size()*15+(e.note.isEmpty()?0:mc.fontRenderer.getStringWidth(e.note));float x=38.5F-width/2;
                    for(ItemStack small:e.foot){c.item(small,x,80.5F,12);if(resting&&right!=null&&right[0]>=x&&right[0]<x+12&&right[1]>=80.5F&&right[1]<92.5F)pointed=small;x+=15;}if(!e.note.isEmpty())c.text(e.note,x,83,1);
                }
                if(resting&&right!=null&&right[0]>=28.5F&&right[0]<48.5F&&right[1]>=3&&right[1]<23)pointed=e.output;
            }
        }
        GL11.glPopMatrix();
    }
    private static final float HALF_WIDE=7*.72F,HALF_HIGH=5*.72F,SCREEN_MARGIN=.3F,SIZE_LEAST=.75F,MOVE_WIDE=.75F,MOVE_HIGH=.5F;
    private static float[] limited(float size, float x, float y) {
        return new float[] {size, NativeMath.clamp(x, -MOVE_WIDE * 2.0F * HALF_WIDE, MOVE_WIDE * 2.0F * HALF_WIDE),
                NativeMath.clamp(y, -MOVE_HIGH * 2.0F * HALF_HIGH, MOVE_HIGH * 2.0F * HALF_HIGH)};
    }

    /**
     * The book at {@code size} and {@code x, y} put where all of it is on the screen ({@code screen}: the screen's corners on the book's
     * plane), within its reach. A book too big for the screen is made smaller: from the corner across from {@code cornerX, cornerY}
     * when one is held, else about its middle.
     */
    private static float[] fit(float[] screen, float size, float x, float y, int cornerX, int cornerY) {
        float stayX = x - cornerX * HALF_WIDE * size;
        float stayY = y - cornerY * HALF_HIGH * size;
        float[] place = limited(size, x, y);
        for (int tries = 0; tries < 40; tries++) {
            place = limited(size, x, y);
            if (pushIn(screen, place) || size <= SIZE_LEAST) {
                return place;
            }
            size = Math.max(SIZE_LEAST, size - 0.02F);
            x = cornerX != 0 ? stayX + cornerX * HALF_WIDE * size : x;
            y = cornerY != 0 ? stayY + cornerY * HALF_HIGH * size : y;
        }
        return place;
    }

    /** Moves {@code place} (size, x, y) in from every edge of the screen it reaches over; true when all of it is on the screen then. */
    private static boolean pushIn(float[] screen, float[] place) {
        for (int pass = 0; pass < 3; pass++) {
            for (int edge = 0; edge < 4; edge++) {
                float[] in = inward(screen, edge);
                float clear = in == null ? SCREEN_MARGIN : clearance(in, place);
                if (clear < SCREEN_MARGIN) {
                    place[1] += in[0] * (SCREEN_MARGIN - clear);
                    place[2] += in[1] * (SCREEN_MARGIN - clear);
                }
            }
        }
        // Pushed in from one side and out again over the other: it does not fit.
        for (int edge = 0; edge < 4; edge++) {
            float[] in = inward(screen, edge);
            if (in != null && clearance(in, place) < SCREEN_MARGIN * 0.5F) {
                return false;
            }
        }
        return true;
    }

    /** An edge of the screen on the book's plane: its normal turned to the inside, and a point of it; null when it has no length. */
    private static float[] inward(float[] screen, int edge) {
        float fromX = screen[edge * 2];
        float fromY = screen[edge * 2 + 1];
        float alongX = screen[(edge + 1) % 4 * 2] - fromX;
        float alongY = screen[(edge + 1) % 4 * 2 + 1] - fromY;
        float length = (float)Math.sqrt(alongX * alongX + alongY * alongY);
        if (length < 1.0E-4F) {
            return null;
        }
        float inX = -alongY / length;
        float inY = alongX / length;
        float middleX = (screen[0] + screen[2] + screen[4] + screen[6]) / 4.0F;
        float middleY = (screen[1] + screen[3] + screen[5] + screen[7]) / 4.0F;
        float sign = inX * (middleX - fromX) + inY * (middleY - fromY) < 0.0F ? -1.0F : 1.0F;
        return new float[] {inX * sign, inY * sign, fromX, fromY};
    }

    /** How far all of the book at {@code place} is inside the edge {@code in}; negative: it reaches over. */
    private static float clearance(float[] in, float[] place) {
        return in[0] * (place[1] - in[2]) + in[1] * (place[2] - in[3]) - Math.abs(in[0]) * HALF_WIDE * place[0]
                - Math.abs(in[1]) * HALF_HIGH * place[0];
    }

    private boolean has(ItemStack s){for(int i=0;i<36;i++)if(Stacks.same(mc.thePlayer.inventory.getStackInSlot(i),s))return true;return false;}
    private void tab(Canvas c,int x,String name,ItemStack icon){boolean on=name.equals(tab)||name.equals("filter")&&filter||name.equals("search")&&hasSearch();float high=on?14:11,bottom=on?1:0;float[] pointer=c==pages[0]?left:right;boolean lit=pointer!=null&&pointer[0]>=x&&pointer[0]<x+16&&pointer[1]>=-high&&pointer[1]<1;
        mc.getTextureManager().bindTexture(PAPER);c.quad(x,-high,x+16,bottom,on?.05F:0,on?0xffffffff:lit?0xffede4d0:0xffd6c9ac,8/128F,24/128F,8/128F,(8+high+bottom)/128F);if(name.equals("filter")&&filter)c.rect(x+1,-high+1,14,high-1+bottom,.08F,READY);
        c.rect(x+1,-high,14,1,.1F,0x905a3c18);c.rect(x,-high+1,1,high-1,.1F,0x905a3c18);c.rect(x+15,-high+1,1,high-1,.1F,0x905a3c18);if(icon!=null)c.item(icon,x+2.5F,-high+2,11);}
    public boolean click(int x,int y,int button){
        if(open<.98F){if(closed){int[] a=cover.project(-.5F,-5.5F),b=cover.project(6.5F,5.5F);if(a!=null&&b!=null&&x>=Math.min(a[0],b[0])&&x<=Math.max(a[0],b[0])&&y>=Math.min(a[1],b[1])&&y<=Math.max(a[1],b[1])){closed=false;return true;}}return false;}
        float[] lp=pages[0].point(x,y),rp=pages[1].point(x,y);int[] corner=pages[1].project(75,-10);
        if(totalRows>visibleRows&&rp!=null&&rp[0]>=65.5F&&rp[0]<=69.5F&&rp[1]>=rowTop&&rp[1]<=rowTop+barHigh){if(button==0&&rp[1]>=gripTop&&rp[1]<=gripTop+gripHigh){scrolling=true;gripGrab=rp[1]-gripTop;}else if(button==0)scrollTo(firstRow+(rp[1]<gripTop?-visibleRows:visibleRows));return true;}
        if(button==0&&corner!=null&&Math.hypot(x-corner[0],y-corner[1])<9){beginDrag(2,x,y);return true;}
        boolean border=border(lp)||border(rp);if(button==0&&border||button==2&&(inside(lp)||inside(rp))){beginDrag(1,x,y);return true;}
        if(button!=0&&button!=1)return false;
        float[] l=pages[0].point(x,y),r=pages[1].point(x,y);if(l!=null&&l[0]>=0&&l[0]<=78&&l[1]>=-14&&l[1]<=96){
            if(l[1]<0){if(hasSearch()){typing=true;return true;}tab=l[0]<23?"all":l[0]<41?"recent":l[0]<59?"assembly":"cut";BookTabs.put(memory,tab);page=0;refresh();return true;}
            if(l[1]>=78){heldWay=l[0]<33.5F?-1:1;heldFrom=(float)seconds;heldTurns=1;turn(heldWay);return true;}
            int col=(int)(l[0]-7)/18,row=(int)(l[1]-5)/18,index=page*12+row*3+col;
            if(l[0]>=7&&l[0]<61&&l[1]>=5&&l[1]<77&&col<3&&row<4&&index<shown.size()){pick(shown.get(index));}else if(button==1)resetPlace();else beginDrag(1,x,y);return true;
        }
        if(r!=null&&r[0]>=0&&r[0]<=72&&r[1]>=-14&&r[1]<=96){
            if(r[1]<0){if(r[0]<23){filter=!filter;BookTabs.put(memory+".ready",filter?"true":null);page=0;refresh();}else if(r[0]<42){if(hasSearch()){typing=false;query="";refresh();}else typing=true;}return true;}
            if(r[0]>=60&&r[1]<15){closed=true;typing=false;query="";refresh();return true;}
            if(r[0]>=28.5F&&r[0]<48.5F&&r[1]>=3&&r[1]<23&&selected!=null)pick(selected);
            else if(!Stacks.empty(pointed)){for(RecipeBook.Entry e:all)if(e!=selected&&Stacks.same(e.output,pointed)){tab="all";query="";filter=false;refresh();selected=e;page=Math.max(0,shown.indexOf(e)/12);return true;}}
            else if(button==1)resetPlace();else beginDrag(1,x,y);return true;
        }
        return false;
    }
    private static void remember(String id){RECENT.remove(id);RECENT.add(0,id);while(RECENT.size()>24)RECENT.remove(RECENT.size()-1);}
    public void confirm(String id,boolean success){lastFillRecipe=id;lastFillSuccess=success;if(success){remember(id);refresh();}}
    private void drawStars(Canvas c,RecipeBook.Entry e,float middle,float line){
        int made=ClientProxy.practice.getInteger(StationHooks.key(e.output)),open=made>=32?3:made>=8?2:1;float next=made>=32?1:made>=8?(made-8)/24F:made/8F;
        String bonus="+"+(open>=3?50:open==2?25:0)+"%";float left=middle-(17+3+mc.fontRenderer.getStringWidth(bonus))/2F;
        String[] star={"..#..",".###.","#####",".###.",".#.#."};
        for(int n=0;n<3;n++){int earning=n==open?Math.round(next*5):0;for(int row=0;row<5;row++){int color=n<open?0xffd08a1e:row>=5-earning?0x90d08a1e:0x305a3c18;for(int col=0;col<5;col++)if(star[row].charAt(col)=='#')c.rect(left+n*6+col,line+1+row,1,1,.3F,color);}}
        c.text(bonus,left+20,line,1);
    }
    private static boolean inside(float[] p){return p!=null&&p[0]>=0&&p[0]<=72&&p[1]>=0&&p[1]<=96;}
    private static boolean border(float[] p){return p!=null&&(p[0]>=-5&&p[0]<0||p[0]>72&&p[0]<78)&&p[1]>=0&&p[1]<=108||p!=null&&p[0]>=-5&&p[0]<=78&&p[1]>96&&p[1]<=108;}
    private void beginDrag(int mode,int x,int y){dragMode=mode;grabX=x;grabY=y;dragX=offsetX;dragY=offsetY;dragSize=size;}
    private void resetPlace(){size=1;offsetX=offsetY=0;BookTabs.putNumbers(new String[]{"book.size","book.x","book.y"},new Float[]{null,null,null});}
    private void pick(RecipeBook.Entry e){boolean again=e.id.equals(clicked);selected=e;clicked=e.id;if(again){screen.fillRecipe(e.id,StationScreen.isShiftKeyDown());remember(e.id);}else mc.theWorld.playSound(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,"random.click",.12F,1.4F,false);}
    public void release(){heldWay=0;scrolling=false;if(dragMode!=0){BookTabs.putNumbers(new String[]{"book.size","book.x","book.y"},new Float[]{size,offsetX,offsetY});dragMode=0;}}
    private void turn(int by){oldPage=page;int next=BookMath.turned(page,by,(shown.size()+11)/12,true);if(next!=page){page=next;flip=0;mc.theWorld.playSound(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,"random.click",.12F,1.1F,false);}}
    private void scrollTo(int row){firstRow=Math.max(0,Math.min(Math.max(0,totalRows-visibleRows),row));rowMemory.put(rowsFor,firstRow);}
    public boolean wheel(int by){if(totalRows>visibleRows&&right!=null&&right[0]>=0&&right[0]<=72&&right[1]>=rowTop&&right[1]<=rowTop+barHigh)scrollTo(firstRow+by);else turn(by);return true;}
    public boolean key(char c,int key){
        if(typing){if(key==Keyboard.KEY_ESCAPE||key==Keyboard.KEY_RETURN){typing=false;return true;}if(key==Keyboard.KEY_BACK){if(StationScreen.isCtrlKeyDown())query="";else if(!query.isEmpty())query=query.substring(0,query.length()-1);}else if(key==Keyboard.KEY_V&&StationScreen.isCtrlKeyDown()){String paste=StationScreen.getClipboardString();for(char letter:paste.toCharArray())if(letter>=32&&letter!=127&&query.length()<50)query+=letter;}else if(c>=32&&c!=127&&query.length()<50)query+=c;page=0;refresh();return true;}
        if(key==Keyboard.KEY_NEXT){turn(1);return true;}if(key==Keyboard.KEY_PRIOR){turn(-1);return true;}if(key==Keyboard.KEY_HOME){page=0;return true;}if(key==Keyboard.KEY_END){page=Math.max(0,(shown.size()-1)/12);return true;}return false;
    }
    public int[] project(boolean leftPage,float x,float y){return pages[leftPage?0:1].project(x,y);}
    public float[] unproject(boolean leftPage,int x,int y){return pages[leftPage?0:1].point(x,y);}
    public boolean ownsPointer(int x,int y){float[] l=pages[0].point(x,y),r=pages[1].point(x,y);return dragMode!=0||scrolling||bookArea(l)||bookArea(r);}
    private static boolean bookArea(float[] p){return p!=null&&p[0]>=-5&&p[0]<=80&&p[1]>=-15&&p[1]<=108;}
    private final class Canvas {
        private final FloatBuffer model=BufferUtils.createFloatBuffer(16),projection=BufferUtils.createFloatBuffer(16);private final IntBuffer viewport=BufferUtils.createIntBuffer(16);private boolean valid;
        void capture(){model.clear();projection.clear();viewport.clear();GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX,model);GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX,projection);GL11.glGetInteger(GL11.GL_VIEWPORT,viewport);valid=true;}
        float[] point(int x,int y){if(!valid)return null;FloatBuffer a=BufferUtils.createFloatBuffer(3),b=BufferUtils.createFloatBuffer(3);float px=x*(float)mc.displayWidth/screen.width,py=(screen.height-y)*(float)mc.displayHeight/screen.height;
            if(!GLU.gluUnProject(px,py,0,model,projection,viewport,a)||!GLU.gluUnProject(px,py,1,model,projection,viewport,b))return null;float dz=b.get(2)-a.get(2);if(Math.abs(dz)<1e-8)return null;float t=-a.get(2)/dz;return new float[]{a.get(0)+(b.get(0)-a.get(0))*t,a.get(1)+(b.get(1)-a.get(1))*t};}
        int[] project(float x,float y){FloatBuffer p=BufferUtils.createFloatBuffer(3);if(!valid||!GLU.gluProject(x,y,0,model,projection,viewport,p))return null;return new int[]{Math.round(p.get(0)*screen.width/mc.displayWidth),Math.round(screen.height-p.get(1)*screen.height/mc.displayHeight)};}
        void paper(boolean leftPage){mc.getTextureManager().bindTexture(PAPER);quad(0,0,72,96,0,0xffffffff,leftPage?72F/128:0,leftPage?0:72F/128,0,96F/128);}
        void slip(float x,float w,float h){mc.getTextureManager().bindTexture(PAPER);quad(x,-h,x+w,1,.05F,0xffffffff,8/128F,(8+w)/128F,8/128F,(9+h)/128F);rect(x+1,-h,w-2,1,.1F,0x905a3c18);rect(x,-h+1,1,h-1,.1F,0x905a3c18);rect(x+w-1,-h+1,1,h-1,.1F,0x905a3c18);}
        void rect(float x,float y,float w,float h,float lift,int color){GL11.glDisable(GL11.GL_TEXTURE_2D);quad(x,y,x+w,y+h,lift,color,0,0,0,0);GL11.glEnable(GL11.GL_TEXTURE_2D);}
        void quad(float x,float y,float X,float Y,float z,int color,float u,float U,float v,float V){GL11.glColor4ub((byte)(color>>16),(byte)(color>>8),(byte)color,(byte)(color>>>24));Tessellator t=Tessellator.instance;t.startDrawingQuads();t.addVertexWithUV(x,y,z,u,v);t.addVertexWithUV(x,Y,z,u,V);t.addVertexWithUV(X,Y,z,U,V);t.addVertexWithUV(X,y,z,U,v);t.draw();GL11.glColor4f(1,1,1,1);}
        void frame(float x,float y,float n){rect(x,y,n,1,1.9F,PICKED);rect(x,y+n-1,n,1,1.9F,PICKED);rect(x,y,1,n,1.9F,PICKED);rect(x+n-1,y,1,n,1.9F,PICKED);}
        void item(ItemStack s,float x,float y,float size){item(s,x,y,size,false);}
        void item(ItemStack s,float x,float y,float size,boolean faded){if(Stacks.empty(s))return;GL11.glPushMatrix();GL11.glTranslatef(x,y,1.2F);GL11.glScalef(size/16,size/16,.01F);
            RenderState.Texture opacity=faded?RenderState.fade():null;
            try{RenderHelper.enableGUIStandardItemLighting();items.renderItemAndEffectIntoGUI(mc.fontRenderer,mc.getTextureManager(),s,0,0);}
            finally{RenderHelper.disableStandardItemLighting();if(opacity!=null)opacity.close();GL11.glPopMatrix();}}
        void text(String s,float x,float y,float scale){GL11.glPushMatrix();GL11.glTranslatef(x,y,1.2F);GL11.glScalef(scale,scale,1);mc.fontRenderer.drawString(s,0,0,INK);GL11.glPopMatrix();}
        void arrow(float x,float y,boolean next){for(int row=0;row<11;row++){int off=Math.abs(row-5);float from=off<=1?1:5,to=11-off;if(to>from)rect(next?x+from:x+12-to,y+row,to-from,1,.3F,INK);}}
        void magnifier(float x,float y){rect(x+1,y,3,1,1.9F,INK);rect(x+1,y+4,3,1,1.9F,INK);rect(x,y+1,1,3,1.9F,INK);rect(x+4,y+1,1,3,1.9F,INK);rect(x+4,y+4,1,1,1.9F,INK);rect(x+5,y+5,2,1,1.9F,INK);rect(x+6,y+6,1,1,1.9F,INK);}
        void ribbon(boolean lit){mc.getTextureManager().bindTexture(PAPER);float u=(lit?88:80)/128F;quad(62,0,68,13,1.9F,0xffffffff,u,u+6/128F,0,13/128F);}
    }
}
