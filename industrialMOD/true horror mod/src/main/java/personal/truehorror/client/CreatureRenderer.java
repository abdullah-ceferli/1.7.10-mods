package personal.truehorror.client;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import personal.truehorror.core.CreatureKind;
import personal.truehorror.entity.DreadEntity;

public final class CreatureRenderer extends RenderLiving {
    private final ResourceLocation texture,eyes;
    private final CreatureKind kind;
    public CreatureRenderer(CreatureKind kind){
        super(new CreatureModel(kind),kind==CreatureKind.CRAWLER?.4F:.3F);this.kind=kind;
        texture=new ResourceLocation("truehorror","textures/entity/"+kind.id+".png");
        eyes=new ResourceLocation("truehorror","textures/entity/"+kind.id+"_eyes.png");
        setRenderPassModel(mainModel);
    }
    @Override protected ResourceLocation getEntityTexture(Entity entity){return texture;}
    @Override protected void renderModel(EntityLivingBase entity,float limb,float amount,float age,float yaw,float pitch,float scale){
        DreadEntity dread=(DreadEntity)entity;
        float alpha=(1-dread.fade())*Math.min(1,(entity.ticksExisted+age%1)/12F);
        if(kind==CreatureKind.SHADOW)alpha*=.86F;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_COLOR_BUFFER_BIT|GL11.GL_CURRENT_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        try{
            GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1,1,1,alpha);bindTexture(texture);
            mainModel.render(entity,limb,amount,age,yaw,pitch,scale);
        }finally{GL11.glPopAttrib();}
    }
    @Override protected int shouldRenderPass(EntityLivingBase entity,int pass,float partial){
        if(pass==0){
            bindTexture(eyes);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);GL11.glDepthMask(false);OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240,240);
            float alpha=(1-((DreadEntity)entity).fade())*(kind==CreatureKind.WATCHER?.4F:.8F);
            GL11.glColor4f(alpha,alpha,alpha,1);return 1;
        }
        int light=entity.getBrightnessForRender(partial);OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,light&65535,light>>16);
        GL11.glDepthMask(true);GL11.glEnable(GL11.GL_ALPHA_TEST);GL11.glDisable(GL11.GL_BLEND);GL11.glColor4f(1,1,1,1);return -1;
    }
}
