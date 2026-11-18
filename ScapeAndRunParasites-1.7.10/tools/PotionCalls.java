package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import javax.tools.*;import com.sun.source.tree.*;import com.sun.source.util.*;

/** Map only vanilla effects that really exist in the native target; leave newer effects explicit. */
public final class PotionCalls {
    static class Edit {int a,b;String text;Edit(int a,int b,String t){this.a=a;this.b=b;text=t;}}
    public static void main(String[] args)throws Exception {
        Path root=Paths.get(args[0]).toAbsolutePath().normalize();boolean apply=args[1].equals("--apply");
        List<File> files=new ArrayList<File>();try(java.util.stream.Stream<Path> walk=Files.walk(root)){walk.filter(p->p.toString().endsWith(".java")&&!p.toString().replace('\\','/').contains("/backport/")).sorted().forEach(p->files.add(p.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();DiagnosticCollector<JavaFileObject> errors=new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fm=compiler.getStandardFileManager(errors,null,StandardCharsets.UTF_8);
        JavacTask task=(JavacTask)compiler.getTask(null,fm,errors,Arrays.asList("-proc:none","-source","8"),null,fm.getJavaFileObjectsFromFiles(files));
        List<CompilationUnitTree> units=new ArrayList<CompilationUnitTree>();for(CompilationUnitTree unit:task.parse())units.add(unit);
        SourcePositions positions=Trees.instance(task).getSourcePositions();Map<String,String> names=new LinkedHashMap<String,String>();
        for(String n:"blindness confusion damageBoost digSlowdown digSpeed fireResistance harm hunger invisibility moveSlowdown moveSpeed poison regeneration weakness wither".split(" "))names.put(n,n);
        names.put("ABSORPTION","field_76444_x");
        Map<Path,String> pending=new LinkedHashMap<Path,String>();List<String> audit=new ArrayList<String>();
        for(CompilationUnitTree unit:units) {
            Path path=Paths.get(unit.getSourceFile().toUri());String source=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);
            List<Edit> edits=new ArrayList<Edit>();boolean[] unresolved={false};
            new TreeScanner<Void,Void>() {
                @Override public Void visitMemberSelect(MemberSelectTree tree,Void unused) {
                    if(tree.getExpression().toString().equals("MobEffects")) {
                        String name=tree.getIdentifier().toString(),target=names.get(name);
                        if(target==null)unresolved[0]=true;
                        else {int start=(int)positions.getStartPosition(unit,tree),end=(int)positions.getEndPosition(unit,tree);String replacement="net.minecraft.potion.Potion."+target;
                            edits.add(new Edit(start,end,replacement));audit.add(root.relativize(path)+"\t"+unit.getLineMap().getLineNumber(start)+"\t"+name+"\t"+replacement);}
                    }
                    return super.visitMemberSelect(tree,unused);
                }
            }.scan(unit,null);
            if(edits.isEmpty())continue;
            if(!unresolved[0])for(ImportTree i:unit.getImports())if(i.getQualifiedIdentifier().toString().equals("net.minecraft.init.MobEffects"))edits.add(new Edit((int)positions.getStartPosition(unit,i),(int)positions.getEndPosition(unit,i),""));
            edits.sort((a,b)->Integer.compare(b.a,a.a));StringBuilder changed=new StringBuilder(source);for(Edit e:edits)changed.replace(e.a,e.b,e.text);pending.put(path,changed.toString());
        }
        for(Diagnostic<?> e:errors.getDiagnostics())if(e.getKind()==Diagnostic.Kind.ERROR)throw new IllegalStateException("Parse error: "+e);
        fm.close();if(apply)for(Map.Entry<Path,String> entry:pending.entrySet())Files.write(entry.getKey(),entry.getValue().getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(args[2]),audit,StandardCharsets.UTF_8);System.out.println("Native vanilla potion references: "+audit.size()+" in "+pending.size()+" files; applied="+apply);
    }
}
