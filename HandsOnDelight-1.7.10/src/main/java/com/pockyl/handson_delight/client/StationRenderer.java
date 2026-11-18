package com.pockyl.handson_delight.client;

import com.pockyl.handson_delight.*;
import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.joml.Vector3f;
import org.joml.Matrix4f;
import vectorwing.farmersdelight.legacy.*;

/** Original station models and motions on the native 1.7.10 rendering pipeline. */
public final class StationRenderer extends TileEntitySpecialRenderer {
    private final LiquidSurface surface=new LiquidSurface();private final RenderBlocks blocks=new RenderBlocks();
    public void renderTileEntityAt(TileEntity raw,double x,double y,double z,float partial){
        InventoryTile tile=(InventoryTile)raw;StationState state=StationHooks.state(tile);Minecraft mc=Minecraft.getMinecraft();StationScreen focus=mc.currentScreen instanceof StationScreen&&((StationScreen)mc.currentScreen).tile==tile?(StationScreen)mc.currentScreen:null;
        StationVisuals v=StationVisuals.of(tile);v.frame(tile,focus,partial);float yaw=tile instanceof CookingPotTile&&focus!=null?focus.scene.yaw:90*(tile.getBlockMetadata()&3);
        RenderState saved=RenderState.capture();GL11.glPushMatrix();GL11.glTranslated(x+.5,y,z+.5);GL11.glRotatef(-yaw,0,1,0);GL11.glScalef(1F/16,1F/16,1F/16);GL11.glColor4f(1,1,1,1);
        try{
            if(tile instanceof CookingPotTile)pot(tile,state,v,focus);
            else if(tile instanceof CuttingBoardTile)board((CuttingBoardTile)tile,state,v,focus);
            else grill((GrillingTile)tile,state,v,partial);
            if(v.ghost!=null&&v.ghostAge<.3F){float t=v.ghostAge/.3F;GL11.glPushMatrix();GL11.glTranslatef(0,tile instanceof StoveTile?16:tile instanceof CookingPotTile?v.level():1,0);GL11.glTranslatef(0,2.5F*(float)Math.sin(t*Math.PI),-12*t);GL11.glScalef(1-.5F*t,1-.5F*t,1-.5F*t);drawStack(v.ghost,6);GL11.glPopMatrix();}
            v.particles.render(yaw,tile instanceof CookingPotTile?v.liquid:null);
        }finally{GL11.glPopMatrix();saved.close();}
    }
    private void pot(InventoryTile tile,StationState state,StationVisuals v,StationScreen focus){
        SceneModels.draw("pot/body");int support=(tile.getBlockMetadata()>>2)&3;if(support==1)SceneModels.draw("pot/tray");else if(support==2)SceneModels.draw("pot/hoop");
        GL11.glPushMatrix();matrix(v.spoonPose);SceneModels.draw("pot/ladle");GL11.glPopMatrix();
        // Opaque ingredients are drawn before the depth-writing-free liquid and its wakes.
        for(int i=0;i<6;i++){ItemStack item=tile.getStackInSlot(i);float lift=v.landLift(i),tumble=v.tumble[i];if(!Stacks.empty(item)){GL11.glPushMatrix();GL11.glTranslatef(v.itemX[i],v.level()+.25F+lift+(float)Math.sin(tumble*Math.PI)*.7F,v.itemZ[i]);GL11.glRotatef((v.turned+i)*57.3F,0,1,0);GL11.glRotatef(tumble*tumble*(3-2*tumble)*360,1,0,0);drawStack(item,3.2F);GL11.glPopMatrix();}ItemStack melting=v.melted(i);float scale=v.meltScale(i);if(!Stacks.empty(melting)&&scale>.001F){GL11.glPushMatrix();GL11.glTranslatef(v.itemX[i],v.level()+.25F,v.itemZ[i]);GL11.glScalef(scale,scale,scale);drawStack(melting,3.2F);GL11.glPopMatrix();}}
        surface.draw(v.liquid);
    }
    private void board(CuttingBoardTile tile,StationState state,StationVisuals v,StationScreen focus){
        GL11.glPushMatrix();GL11.glTranslatef(-1.5F,1.01F,0);boolean finishing=AssemblyAnimation.draw(v,state);ItemStack input=tile.getStackInSlot(0);if(Stacks.empty(input)&&v.cutItem!=null&&!Float.isNaN(v.knifeAge)&&v.knifeAge<.19F)input=v.cutItem;
        if(!finishing){if(!Stacks.empty(input)){
            GL11.glPushMatrix();GL11.glTranslatef(0,v.landLift(0),0);if(tile.carved){GL11.glTranslatef(1.5F,2.67F,0);GL11.glRotatef(-35,1,0,0);GL11.glRotatef(-90,1,0,0);drawStack(input,9.6F);}else if(input.getItem() instanceof ItemBlock){GL11.glTranslatef(0,2.6F,0);drawStack(input,5.2F);}else{
                CuttingRecipes.Recipe recipe=null;for(CuttingRecipes.Recipe r:CuttingRecipes.ALL)if(r.matchesInput(input)){recipe=r;break;}int marks=recipe==null?1:CutPlan.marks(input,recipe);ItemVoxels.draw(input,10,state.cuts,marks,CutPlan.style(input));
            }GL11.glPopMatrix();}
            for(int i=0;i<state.layers.size();i++){GL11.glPushMatrix();GL11.glTranslatef(0,(i+1)*.65F,0);GL11.glRotatef(17,0,1,0);drawStack(state.layers.get(i),9);GL11.glPopMatrix();}
        }
        if(!tile.carved&&state.layers.isEmpty())tool(tile,state,v,focus,input);
        GL11.glPopMatrix();
    }
    private void tool(CuttingBoardTile tile,StationState state,StationVisuals v,StationScreen focus,ItemStack input){
        boolean animate=!Float.isNaN(v.knifeAge)&&v.cutTool!=null&&v.cutItem!=null&&v.knifeAge<Math.max(.19F,ToolStroke.of(v.cutItem,v.cutTool).length);if(animate)input=v.cutItem;if(Stacks.empty(input))return;ItemStack tool=null;
        if(animate)tool=v.cutTool;else if(focus!=null){Minecraft mc=Minecraft.getMinecraft();tool=mc.thePlayer.inventory.getItemStack();if(CuttingRecipes.find(input,tool)==null){tool=null;ItemStack held=mc.thePlayer.getHeldItem();if(CuttingRecipes.find(input,held)!=null)tool=held;else for(int i=0;i<36;i++)if(CuttingRecipes.find(input,mc.thePlayer.inventory.getStackInSlot(i))!=null){tool=mc.thePlayer.inventory.getStackInSlot(i);break;}}}
        CuttingRecipes.Recipe recipe=CuttingRecipes.find(input,tool);if(recipe==null)return;ToolStroke stroke=ToolStroke.of(recipe);int marks=CutPlan.marks(input,recipe);ItemVoxels.Line[] lines=ItemVoxels.lines(input,10,marks,CutPlan.style(input));ItemVoxels.Line line=lines[Math.min(marks-1,animate?state.visualSlot:state.cuts)];float age=animate?v.knifeAge:Float.NaN;float top=input.getItem() instanceof ItemBlock?5.2F:10F/16;
        if(stroke==ToolStroke.CUT){
            if(focus!=null&&state.cuts<marks){GL11.glDisable(GL11.GL_TEXTURE_2D);GL11.glDisable(GL11.GL_LIGHTING);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);GL11.glColor4f(1,1,1,.63F);for(float along=-5;along<5;along+=1.05F){float a=along,b=Math.min(along+.6F,5);GL11.glBegin(GL11.GL_QUADS);GL11.glVertex3f(line.x+line.dx*a+line.nx*.08F,top+.03F,line.z+line.dz*a+line.nz*.08F);GL11.glVertex3f(line.x+line.dx*b+line.nx*.08F,top+.03F,line.z+line.dz*b+line.nz*.08F);GL11.glVertex3f(line.x+line.dx*b-line.nx*.08F,top+.03F,line.z+line.dz*b-line.nz*.08F);GL11.glVertex3f(line.x+line.dx*a-line.nx*.08F,top+.03F,line.z+line.dz*a-line.nz*.08F);GL11.glEnd();}GL11.glColor4f(1,1,1,1);GL11.glEnable(GL11.GL_TEXTURE_2D);}
            float lift=2.4F;if(animate){if(age<.05F)lift*=1-age/.05F;else if(age<.19F)lift*=Easing.outCubic((age-.05F)/.14F);}
            GL11.glPushMatrix();GL11.glTranslatef(line.x,top+lift+1,line.z);GL11.glRotatef((float)Math.toDegrees(Math.atan2(line.dz,line.dx)),0,1,0);GL11.glRotatef(20,1,0,0);GL11.glRotatef(-135,0,0,1);GL11.glScalef(.45F,.45F,.45F);GL11.glRotatef(-90,1,0,0);drawStack(tool,16);GL11.glPopMatrix();
        }else{ToolStroke.Pose pose=stroke.pose(new Vector3f(line.x,top,line.z),animate&&age<stroke.length?age:Float.NaN,-4,4);Vector3f position=pose.nodePosition(stroke.icon);GL11.glPushMatrix();GL11.glTranslatef(position.x,position.y,position.z);matrix(new Matrix4f().rotation(pose.rotation));GL11.glScalef(.45F,.45F,.45F);GL11.glRotatef(-90,1,0,0);drawStack(tool,16);GL11.glPopMatrix();}
    }
    private void grill(GrillingTile tile,StationState state,StationVisuals v,float partial){
        boolean stove=tile instanceof StoveTile;float age=(tile.getWorldObj().getTotalWorldTime()+partial-state.turned)/20F;
        if(!stove){float jerk=age>=0&&age<.27F?(float)Math.sin(age/.27F*Math.PI):0;GL11.glPushMatrix();GL11.glTranslatef(0,jerk*1.2F,jerk*.9F);GL11.glRotatef(-11*jerk,1,0,0);SceneModels.draw("skillet/pan");SceneModels.draw("skillet/handle");GL11.glPopMatrix();}
        for(int slot=0;slot<tile.getSizeInventory();slot++){ItemStack raw=tile.getStackInSlot(slot);if(Stacks.empty(raw))continue;GrillingRecipes.Recipe recipe=GrillingRecipes.find(raw);ItemStack look=recipe!=null&&state.frying[slot].up()>=1?recipe.output:raw;
            int count=stove?1:Math.min(4,raw.stackSize),under=stove?0:Math.min(4,Math.max(0,raw.stackSize-4));float size=stove?4.8F:count<=1?9:count==2?7.5F:6.5F;
            float ownAge=(tile.getWorldObj().getTotalWorldTime()+partial-state.turnedSlots[slot])/20F;float turn=stove?NativeMath.clamp(ownAge/.3F,0,1):0;
            for(int i=0;i<count;i++){float[] at=stove?new float[]{4.8F-slot%3*4.8F,slot<3?3.2F:-3.2F}:panPlace(i,count);float yaw=((i*7919)%41-20)*1.2F;
                if(i<under){GL11.glPushMatrix();GL11.glTranslatef(at[0],1.01F,at[1]);GL11.glRotatef(yaw+35,0,1,0);drawStack(look,size);GL11.glPopMatrix();}
                float toss=stove?turn:NativeMath.clamp((age-.07F-i*.03F)/.368F,0,1),lift=(float)Math.sin(toss*Math.PI)*(stove?3:6.9F);
                GL11.glPushMatrix();GL11.glTranslatef(at[0],(stove?16.02F:1.01F)+(under>0?.6F:0)+lift+v.landLift(slot),at[1]);GL11.glRotatef(yaw,0,1,0);GL11.glRotatef(stove?(toss<.5F?toss*180:(1-toss)*180):toss*360,1,0,0);
                if(recipe!=null&&!state.frying[slot].ready()){float sear=state.frying[slot].up()*.7F;GL11.glColor4f(1+(0xb8/255F-1)*sear,1+(0x87/255F-1)*sear,1+(0x5c/255F-1)*sear,1);}drawStack(look,size);GL11.glColor4f(1,1,1,1);GL11.glPopMatrix();
            }
        }
    }
    private static float[] panPlace(int i,int count){if(count==1)return new float[]{0,0};if(count==2)return i==0?new float[]{2.6F,.4F}:new float[]{-2.6F,-.4F};if(count==3)return new float[][]{{2.8F,2.2F},{-2.8F,2F},{.2F,-2.8F}}[i];return new float[][]{{2.9F,2.9F},{-2.9F,2.7F},{2.7F,-2.9F},{-2.8F,-2.8F}}[i];}
    private void drawStack(ItemStack stack,float size){if(Stacks.empty(stack))return;if(stack.getItem() instanceof ItemBlock){GL11.glPushMatrix();GL11.glScalef(size,size,size);blocks.renderBlockAsItem(net.minecraft.block.Block.getBlockFromItem(stack.getItem()),stack.getItemDamage(),1);GL11.glPopMatrix();}else ItemVoxels.draw(stack,size);}
    private static void matrix(Matrix4f matrix){FloatBuffer b=BufferUtils.createFloatBuffer(16);matrix.get(b);GL11.glMultMatrix(b);}
}
