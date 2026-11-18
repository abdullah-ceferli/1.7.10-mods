"""Generate native geometry code from the same source forms used by the recovered mod."""
from pathlib import Path
import json,subprocess,re
R=Path(__file__).resolve().parents[1]
MIXED=r"""package dev.srpbackport.tests;
import net.minecraft.util.AxisAlignedBB;import net.minecraft.item.ItemStack;
class MixedGeometryScope {
 static void box(AxisAlignedBB value){value.grow(2);value.shrink(0.5);}
 static void item(ItemStack value){value.grow(2);value.shrink(1);}
 static final String literal="value.grow(2), new AxisAlignedBB(0,0,0,1,1,1)";
 // Keep this: value.shrink(1).
}
"""
RUNTIME=r"""package dev.srpbackport.tests;
import net.minecraft.entity.Entity;import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
public final class GeometryRuntimeFixture {
 static final AxisAlignedBB FIXED=new AxisAlignedBB(0,0,0,1,1,1);
 static AxisAlignedBB box(Entity entity){return entity.getEntityBoundingBox();}
 static void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
 public static void run(Entity actor){
  actor.setPosition(10,74,10);AxisAlignedBB original=box(actor);double min=original.minX;
  AxisAlignedBB grown=box(actor).grow(2);check(grown.minX==min-2&&original.minX==min,"Growth mutated its source");
  actor.setPosition(30,74,30);check(original.minX==min,"Snapshot changed after native movement");
  AxisAlignedBB moved=FIXED.offset(new BlockPos(2,3,4));check(moved.minX==2&&moved.minY==3&&moved.minZ==4&&FIXED.minX==0,"Immutable offset changed fixed geometry");
  AxisAlignedBB normalized=new AxisAlignedBB(4,5,6,1,2,3);check(normalized.minX==1&&normalized.maxX==4,"Constructor did not normalize");
  AxisAlignedBB symmetric=FIXED.expand(1,2,3);check(symmetric.minX==-1&&symmetric.maxX==2&&symmetric.minY==-2,"Preferred legacy SRG expand alias was misinterpreted");
  AxisAlignedBB directional=FIXED.addCoord(-2,3,0);check(directional.minX==-2&&directional.maxX==1&&directional.maxY==4,"Directional SRG addCoord alias was misinterpreted");
  actor.setEntityBoundingBox(normalized);check(actor.getEntityBoundingBox().minX==1&&actor.getEntityBoundingBox().maxY==5,"Native final-box setter bridge");
  actor.setPosition(40,75,40);check(normalized.minX==1,"Native physics mutated a setter argument");
 }
}
"""
def generate(task):
 c=json.loads((R/'.toolchain/toolchain.json').read_text());tools=Path(c['javac']).parents[1]/'lib/tools.jar';out=R/'.toolchain/source-tools'
 subprocess.run([c['javac'],'-encoding','UTF-8','-source','8','-target','8','-cp',str(tools),'-d',str(out),str(R/'tools/GeometryCalls.java')],check=True)
 folder=task/'geometry-fixture';folder.mkdir();mixed=folder/'MixedGeometryScope.java';mixed.write_text(MIXED);runtime=folder/'GeometryRuntimeFixture.java';runtime.write_text(RUNTIME)
 args=[c['java'],'-cp',str(out)+';'+str(tools),'dev.srpbackport.tools.GeometryCalls',str(folder)]
 subprocess.run(args+['--apply',str(task/'geometry-fixture-calls.tsv'),str(R/'.toolchain/geometry-call-rules.tsv')],cwd=R,check=True)
 converted=mixed.read_text();assert 'value.grow(2);value.shrink(1);' in converted,'Item operations were changed'
 assert 'Boxes.grow(value, 2)' in converted and 'Boxes.shrink(value, 0.5)' in converted,'Scoped box operations were skipped'
 tokens=re.compile(r'"(?:\\.|[^"\\])*"|//[^\n]*|/\*[\s\S]*?\*/')
 assert tokens.findall(MIXED)==tokens.findall(converted),'Geometry edit changed strings/comments'
 subprocess.run(args+['--preview',str(task/'geometry-fixture-after.tsv'),str(R/'.toolchain/geometry-call-rules.tsv')],cwd=R,check=True)
 assert not (task/'geometry-fixture-after.tsv').read_text().strip(),'Geometry conversion is not idempotent'
 return runtime
