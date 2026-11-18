package personal.truehorror.client;

import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import personal.truehorror.core.*;
import personal.truehorror.entity.DreadEntity;

/** Legacy ModelRenderer meshes loaded from our own original part descriptors. */
public final class CreatureModel extends ModelBase {
    private static final class Mesh {int textureWidth,textureHeight;Part[] parts;}
    private static final class Part {String name,parent,role;int u,v;int[] size;float[] origin,pivot,rotation;float inflate;}
    private static final class Animated {
        ModelRenderer model;Part part;
        Animated(ModelRenderer model,Part part){this.model=model;this.part=part;}
    }
    private final List<Animated> parts=new ArrayList<Animated>();
    private final List<ModelRenderer> roots=new ArrayList<ModelRenderer>();
    private final CreatureKind kind;
    public CreatureModel(CreatureKind kind){
        this.kind=kind;
        String path="/assets/truehorror/models/"+kind.id+".json";
        try(InputStream stream=CreatureModel.class.getResourceAsStream(path)){
            if(stream==null)throw new IOException("Missing mesh "+path);
            Mesh mesh=new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Mesh.class);
            textureWidth=mesh.textureWidth;textureHeight=mesh.textureHeight;
            Map<String,ModelRenderer> named=new HashMap<String,ModelRenderer>();
            for(Part p:mesh.parts){
                ModelRenderer model=new ModelRenderer(this,p.u,p.v);
                model.setRotationPoint(p.pivot[0],p.pivot[1],p.pivot[2]);
                model.addBox(p.origin[0],p.origin[1],p.origin[2],p.size[0],p.size[1],p.size[2],p.inflate);
                if(p.parent==null||p.parent.isEmpty())roots.add(model);else named.get(p.parent).addChild(model);
                named.put(p.name,model);parts.add(new Animated(model,p));
            }
        }catch(IOException ex){throw new IllegalStateException("Cannot load "+kind.title+" model",ex);}
    }
    @Override public void render(Entity entity,float limb,float amount,float age,float yaw,float pitch,float scale){
        DreadEntity dread=(DreadEntity)entity;
        boolean pursuit=dread.state()==EncounterState.HUNTING||dread.state()==EncounterState.ATTACKING;
        boolean crawling=kind==CreatureKind.CRAWLER||kind==CreatureKind.DWELLER;
        float attack=dread.attackPose(age-(int)age),fade=dread.fade();
        for(Animated item:parts){
            ModelRenderer m=item.model;Part p=item.part;
            m.rotateAngleX=p.rotation[0];m.rotateAngleY=p.rotation[1];m.rotateAngleZ=p.rotation[2];
            String role=p.role==null?"":p.role;
            float phase=limb*(crawling?.9F:.6662F),speed=Math.min(1,amount)*(pursuit?1.2F:.7F);
            float wave=MathHelper.cos(phase+(role.endsWith("R")?(float)Math.PI:0));
            if(role.equals("head")){
                m.rotateAngleY+=MathHelper.clamp_float(yaw,-65,65)*(float)Math.PI/180;
                m.rotateAngleX+=MathHelper.clamp_float(pitch,-35,40)*(float)Math.PI/180;
                if(kind==CreatureKind.PALE_MAN)m.rotateAngleZ+=MathHelper.sin(age*.027F)*.07F;
                if(kind==CreatureKind.WATCHER)m.rotateAngleY+=MathHelper.sin(age*.018F)*.025F;
                if(kind==CreatureKind.DWELLER)m.rotateAngleX-=attack*.45F;
            }else if(role.startsWith("leg")){
                m.rotateAngleX+=wave*speed*(crawling?-.65F:1.1F);
                if(kind==CreatureKind.PALE_MAN&&pursuit)m.rotateAngleX+=MathHelper.sin(phase*2)*speed*.12F;
            }else if(role.startsWith("arm")){
                m.rotateAngleX-=wave*speed*(crawling?.8F:.55F);
                m.rotateAngleZ+=MathHelper.sin(age*.055F)*.018F+(role.endsWith("R")?-fade:fade)*.2F;
                if(attack>0&&(dread.attackVariant()==0||!crawling))m.rotateAngleX-=attack*(role.endsWith("R")?1.9F:.7F);
            }else if(role.startsWith("fore")||role.startsWith("shin")){
                if(crawling)m.rotateAngleX+=Math.max(0,MathHelper.sin(phase))*speed*.55F;
            }else if(role.equals("torso")){
                m.rotateAngleX+=MathHelper.sin(age*.065F)*(crawling?.025F:.012F)+fade*.12F;
                if(pursuit&&!crawling)m.rotateAngleX+=.12F;
                if(kind==CreatureKind.DWELLER)m.rotateAngleY+=MathHelper.sin(phase)*amount*.04F;
            }else if(role.equals("jaw")){
                m.rotateAngleX+=(kind==CreatureKind.MIMIC?(pursuit?.6F:.03F):.05F)
                        +MathHelper.sin(age*.12F)*.025F+attack*(dread.attackVariant()==1?.8F:.25F);
            }
        }
        for(ModelRenderer root:roots)root.render(scale);
    }
}
