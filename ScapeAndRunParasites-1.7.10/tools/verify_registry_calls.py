"""Validate typed name rewrites and generate the fixture executed by real Forge."""
from pathlib import Path
import json,subprocess,re
R=Path(__file__).resolve().parents[1]
FIXTURE=r"""package dev.srpbackport.tests;
import net.minecraft.block.*;import net.minecraft.block.material.Material;import net.minecraft.item.*;import net.minecraft.util.ResourceLocation;
public final class RegistryFixture{
 public static class Parent extends Block{Parent(){super(Material.rock);}public int damageDropped(int meta){return meta;}}
 public static final class NamedBlock extends Parent{NamedBlock(){setRegistryName("variant");setBlockName("srp_registry_checks.variant");}}
 public static final class VariantItem extends ItemBlock{VariantItem(Block block){super(block);setHasSubtypes(true);}public int getMetadata(int damage){return damage;}public String getUnlocalizedName(ItemStack stack){return super.getUnlocalizedName()+"."+stack.getItemDamage();}}
 public static final class Plain extends Item{Plain(String name){this.setRegistryName(name);}}
 public static final class Unrelated{String getRegistryName(){return "unrelated";}void setRegistryName(String value){}}
 public static final String literal="item.setRegistryName(name), block.getRegistryName()";
 // Keep this: block.setRegistryName(name).
 public static Block block(){return new NamedBlock();}
 public static Item form(Block block){return new VariantItem(block).setRegistryName(block.getRegistryName());}
 public static Item ordinary(){return new Plain("ordinary");}
 public static String untouched(Unrelated other){other.setRegistryName("unused");return other.getRegistryName();}
 static final StringBuilder order=new StringBuilder();
 static Block receiver(Block block){order.append("receiver,");return block;}
 static String argument(){order.append("argument,");return "ordered";}
 public static Block ordered(){Block b=new Parent();order.setLength(0);Block result=receiver(b).setRegistryName(/* argument preserved */ argument());if(result!=b||!order.toString().equals("receiver,argument,"))throw new AssertionError("Fluent identity or evaluation order changed");return result;}
 public static ResourceLocation name(Block block){return block.getRegistryName();}
 public static ResourceLocation name(Item item){return item.getRegistryName();}
}
"""
def generate(task):
 c=json.loads((R/'.toolchain/toolchain.json').read_text(encoding='utf-8'));tools=Path(c['javac']).parents[1]/'lib/tools.jar';out=R/'.toolchain/source-tools';cp=str(tools)+';'+Path(c['runtimeClasspathFile']).read_text().strip()
 subprocess.run([c['javac'],'-encoding','UTF-8','-source','8','-target','8','-cp',cp,'-d',str(out),str(R/'tools/RegistryCalls.java')],cwd=R,check=True)
 source=task/'registry-fixture/RegistryFixture.java';source.parent.mkdir();source.write_text(FIXTURE,encoding='utf-8')
 base=[c['java'],'-cp',str(out)+';'+cp,'dev.srpbackport.tools.RegistryCalls',str(source.parent)]
 subprocess.run(base+['--apply',str(task/'fixture-registry.tsv'),c['forgeJar']],cwd=R,check=True)
 text=source.read_text(encoding='utf-8');tokens=re.compile(r'"(?:\\.|[^"\\])*"|//[^\n]*|/\*[\s\S]*?\*/')
 assert tokens.findall(text)==tokens.findall(FIXTURE),'A string or comment changed'
 assert 'other.setRegistryName("unused");return other.getRegistryName();' in text,'Unrelated name methods changed'
 assert 'RegistryNames.set(this, "variant")' in text,'Inherited implicit receiver was skipped'
 subprocess.run(base+['--preview',str(task/'fixture-registry-after.tsv'),c['forgeJar']],cwd=R,check=True)
 assert not (task/'fixture-registry-after.tsv').read_text().strip(),'Registry rewrite not idempotent'
 return source
