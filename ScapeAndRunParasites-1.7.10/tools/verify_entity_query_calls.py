from pathlib import Path
import subprocess,json,re
R=Path(__file__).resolve().parents[1]
FIXTURE=r"""package dev.srpbackport.tests;
import net.minecraft.world.World;import net.minecraft.entity.*;import net.minecraft.entity.monster.EntityZombie;import net.minecraft.util.AxisAlignedBB;import java.util.*;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
public final class EntityQueryFixture{
 static final String literal="entity.getPosition(), world.loadedEntityList";
 // Keep this: world.getEntitiesWithinAABB(EntityZombie.class,box).
 static final class Other{String getPosition(){return "other";}}
 public static void run(World world,EntityZombie actor){
  AxisAlignedBB box=AxisAlignedBB.getBoundingBox(16,75,16,28,90,28);
  List<EntityZombie> list=world.getEntitiesWithinAABB(EntityZombie.class,box);
  if(!list.contains(actor))throw new AssertionError("Typed entity query");
  List<EntityZombie> selected=world.getEntitiesWithinAABB(EntityZombie.class,box,e->e==actor);
  if(selected.size()!=1||selected.get(0)!=actor)throw new AssertionError("Typed predicate query");
  boolean found=false;for(Entity entity:world.loadedEntityList)if(entity==actor)found=true;
  if(!found)throw new AssertionError("Typed live list");
  BlockPos pos=actor.getPosition();if(pos.getX()!=20||pos.getY()!=80)throw new AssertionError("Modern block position");
  if(!new Other().getPosition().equals("other"))throw new AssertionError("Unrelated method");
 }
}
"""
def generate(task):
 c=json.loads((R/'.toolchain/toolchain.json').read_text());tools=Path(c['javac']).parents[1]/'lib/tools.jar';cp=str(tools)+';'+Path(c['runtimeClasspathFile']).read_text().strip();out=R/'.toolchain/source-tools'
 subprocess.run([c['javac'],'-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out),str(R/'tools/EntityQueryCalls.java')],check=True,cwd=R)
 source=task/'entity-fixture/EntityQueryFixture.java';source.parent.mkdir();source.write_text(FIXTURE,encoding='utf-8')
 args=[c['java'],'-cp',str(out)+';'+cp,'dev.srpbackport.tools.EntityQueryCalls',str(source.parent)]
 subprocess.run(args+['--apply',str(task/'entity-fixture.tsv'),c['forgeJar']],check=True,cwd=R)
 tokens=re.compile(r'"(?:\\.|[^"\\])*"|//[^\n]*|/\*[\s\S]*?\*/');text=source.read_text(encoding='utf-8')
 assert tokens.findall(text)==tokens.findall(FIXTURE),'Strings/comments changed'
 assert 'new Other().getPosition()' in text,'Unrelated position method changed'
 subprocess.run(args+['--preview',str(task/'entity-fixture-after.tsv'),c['forgeJar']],check=True,cwd=R)
 assert not (task/'entity-fixture-after.tsv').read_text().strip()
 return source
