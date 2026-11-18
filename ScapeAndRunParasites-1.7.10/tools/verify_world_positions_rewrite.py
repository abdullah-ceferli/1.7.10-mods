"""Compile source forms used by SRP and exercise them on native worlds."""
from pathlib import Path
import json,subprocess,re
R=Path(__file__).resolve().parents[1]
FIXTURE=r"""package dev.srpbackport.tests;
import net.minecraft.world.World;import net.minecraft.world.chunk.Chunk;import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
public final class WorldPositionFixture{
 static final String literal="w.getHeight(p), w.setBlockToAir(p), state.isSideSolid(w,p,f)";
 // Keep this comment: w.getHeight(p).
 static void state(IBlockState state,World world,BlockPos pos){state.isSideSolid(world,pos,EnumFacing.UP);}
 static final StringBuilder order=new StringBuilder();
 static World world(World value){order.append("world,");return value;}
 static BlockPos pos(BlockPos value){order.append("pos,");return value;}
 static void check(boolean ok,String why){if(!ok)throw new AssertionError(why+": "+order);}
 public static void run(World w){
  BlockPos p=new BlockPos(45,90,4);w.setBlock(45,90,4,Blocks.stone,0,3);order.setLength(0);
  check(!world(w).isAirBlock(pos(p))&&order.toString().equals("world,pos,"),"Receiver/argument evaluation order");
  check(w.isBlockLoaded(p)&&w.isAreaLoaded(p,1),"Loaded world position");
  Chunk c=w.getChunk(p);check(c.getHeight(p)==w.getHeight(p).getY(),"World/chunk return overload");
  check(w.isSideSolid(p,EnumFacing.UP,true),"Position side-solid conversion");
  check(w.setBlockToAir(p)&&w.isAirBlock(p),"Mutation conversion");
  // An argument comment must also survive: w.isBlockLoaded(p, /* preserve argument comment */ true).
  check(w.isBlockLoaded(p, /* preserve argument comment */ true),"Argument comment");
 }
}
"""
def generate(task):
 c=json.loads((R/'.toolchain/toolchain.json').read_text());tools=Path(c['javac']).parents[1]/'lib/tools.jar';out=R/'.toolchain/source-tools'
 subprocess.run([c['javac'],'-encoding','UTF-8','-source','8','-target','8','-cp',str(tools),'-d',str(out),str(R/'tools/WorldPositionCalls.java')],cwd=R,check=True)
 path=task/'world-position-fixture/WorldPositionFixture.java';path.parent.mkdir();path.write_text(FIXTURE)
 base=[c['java'],'-cp',str(out)+';'+str(tools),'dev.srpbackport.tools.WorldPositionCalls',str(path.parent)]
 subprocess.run(base+['--apply',str(task/'world-position-fixture.tsv')],check=True,cwd=R)
 text=path.read_text();assert 'state.isSideSolid(world,pos,EnumFacing.UP)' in text
 tokens=re.compile(r'"(?:\\.|[^"\\])*"|//[^\n]*|/\*[\s\S]*?\*/')
 assert tokens.findall(FIXTURE)==tokens.findall(text),'Comments or literals changed'
 subprocess.run(base+['--preview',str(task/'world-position-after.tsv')],check=True,cwd=R)
 assert not (task/'world-position-after.tsv').read_text().strip(),'Conversion is not idempotent'
 return path
