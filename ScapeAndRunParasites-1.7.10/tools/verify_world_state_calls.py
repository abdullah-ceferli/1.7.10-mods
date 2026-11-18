"""Generate a syntax-tree conversion fixture for execution in the real native Forge harness."""
from pathlib import Path
import json,subprocess,re
R=Path(__file__).resolve().parents[1]
FIXTURE=r"""package dev.srpbackport.tests;
import net.minecraft.world.World;import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
public final class StateCallFixture {
 static final String literal="world.getBlockState(pos), text with commas (and parentheses)";
 // Preserve this comment: world.setBlockState(pos, block.getDefaultState()).
 static final StringBuilder order=new StringBuilder();
 static World world(World value){order.append("world,");return value;}
 static BlockPos pos(BlockPos value){order.append("pos,");return value;}
 static Block block(Block value){order.append("block,");return value;}
 static int meta(int value){order.append("meta,");return value;}
 static int flags(){order.append("flags,");return 3;}
 static void check(boolean ok,String why){if(!ok)throw new AssertionError(why+": "+order);}
 public static void run(World w,Block b){
  BlockPos p=new BlockPos(35,80,3);order.setLength(0);
  check(world(w).setBlockState(pos(p),block(b).getStateFromMeta(meta(7)),flags()),"Converted write failed");
  check(order.toString().equals("world,pos,block,meta,flags,"),"Receiver/argument evaluation order changed");
  order.setLength(0);IBlockState state=(true?world(w):null).getBlockState(pos(p));
  check(order.toString().equals("world,pos,"),"Conditional receiver evaluated twice");
  order.setLength(0);int n=block(b).getMetaFromState(world(w).getBlockState(pos(p)));
  check(n==7&&order.toString().equals("block,world,pos,"),"Nested receiver/argument conversion changed");
  int id=Block.getStateId(world(w).getBlockState(pos(p)));
  check(Block.getStateById(id)==state&&id==Block.getIdFromBlock(b)+(7<<12),"Nested static state ID conversion");
  order.setLength(0);check(world(w).setBlockState(pos(p),b.getDefaultState()),"Default-flags conversion failed");
  check(order.toString().equals("world,pos,")&&w.getBlockMetadata(35,80,3)==0,"Default state/write conversion");
  check(literal.equals("world.getBlockState(pos), text with commas (and parentheses)"),"Literal was rewritten");
 }
}
"""
def generate(task):
 c=json.loads((R/'.toolchain/toolchain.json').read_text());tools=Path(c['javac']).parents[1]/'lib/tools.jar';out=R/'.toolchain/source-tools';out.mkdir(exist_ok=True)
 subprocess.run([c['javac'],'-encoding','UTF-8','-source','8','-target','8','-cp',str(tools),'-d',str(out),str(R/'tools/WorldStateCalls.java')],cwd=R,check=True)
 source=task/'fixture-src/StateCallFixture.java';source.parent.mkdir(parents=True);source.write_text(FIXTURE,encoding='utf-8')
 command=[c['java'],'-cp',str(out)+';'+str(tools),'dev.srpbackport.tools.WorldStateCalls',str(source.parent)]
 subprocess.run(command+['--apply',str(task/'fixture-conversion.tsv')],cwd=R,check=True)
 result=source.read_text();tokens=re.compile(r'"(?:\\.|[^"\\])*"|//[^\n]*|/\*[\s\S]*?\*/')
 assert tokens.findall(result)==tokens.findall(FIXTURE),'Conversion modified a string or comment'
 subprocess.run(command+['--preview',str(task/'fixture-after.tsv')],cwd=R,check=True)
 assert not (task/'fixture-after.tsv').read_text().strip(),'Conversion is not idempotent'
 return source
