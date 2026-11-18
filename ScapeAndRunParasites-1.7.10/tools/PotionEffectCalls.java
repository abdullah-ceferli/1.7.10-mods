package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import javax.tools.*;import com.sun.source.tree.*;import com.sun.source.util.*;

/** Change constructor targets only; keep native PotionEffect variable and callback types. */
public final class PotionEffectCalls {
    static class Edit {int a,b;String text;Edit(int a,int b,String t){this.a=a;this.b=b;text=t;}}
    public static void main(String[] args)throws Exception {
        Path root=Paths.get(args[0]).toAbsolutePath().normalize();boolean apply=args[1].equals("--apply");
        List<File> files=new ArrayList<File>();try(java.util.stream.Stream<Path> paths=Files.walk(root)){paths.filter(p->p.toString().endsWith(".java")&&!p.toString().replace('\\','/').contains("/backport/")).sorted().forEach(p->files.add(p.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<JavaFileObject>();StandardJavaFileManager fm=compiler.getStandardFileManager(diagnostics,null,StandardCharsets.UTF_8);
        JavacTask task=(JavacTask)compiler.getTask(null,fm,diagnostics,Arrays.asList("-proc:none","-source","8"),null,fm.getJavaFileObjectsFromFiles(files));
        List<CompilationUnitTree> units=new ArrayList<CompilationUnitTree>();for(CompilationUnitTree unit:task.parse())units.add(unit);SourcePositions positions=Trees.instance(task).getSourcePositions();
        Map<Path,String> pending=new LinkedHashMap<Path,String>();List<String> audit=new ArrayList<String>();
        for(CompilationUnitTree unit:units){Path path=Paths.get(unit.getSourceFile().toUri());String source=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);List<Edit> edits=new ArrayList<Edit>();
            new TreeScanner<Void,Void>(){
                @Override public Void visitNewClass(NewClassTree tree,Void p){
                    String type=tree.getIdentifier().toString();if(type.equals("PotionEffect")||type.equals("net.minecraft.potion.PotionEffect")){
                        int start=(int)positions.getStartPosition(unit,tree.getIdentifier()),end=(int)positions.getEndPosition(unit,tree.getIdentifier());
                        edits.add(new Edit(start,end,"com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect"));
                        List<String> values=new ArrayList<String>();for(ExpressionTree arg:tree.getArguments())values.add(arg.toString().replace('\n',' '));
                        audit.add(root.relativize(path)+"\t"+unit.getLineMap().getLineNumber(start)+"\t"+tree.getArguments().size()+"\t"+String.join(" | ",values));
                    }return super.visitNewClass(tree,p);
                }
            }.scan(unit,null);
            if(!edits.isEmpty()){edits.sort((a,b)->Integer.compare(b.a,a.a));StringBuilder result=new StringBuilder(source);for(Edit e:edits)result.replace(e.a,e.b,e.text);pending.put(path,result.toString());}
        }
        for(Diagnostic<?> d:diagnostics.getDiagnostics())if(d.getKind()==Diagnostic.Kind.ERROR)throw new IllegalStateException("Parse error: "+d);fm.close();
        if(apply)for(Map.Entry<Path,String> entry:pending.entrySet())Files.write(entry.getKey(),entry.getValue().getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(args[2]),audit,StandardCharsets.UTF_8);System.out.println("Potion constructors: "+audit.size()+" in "+pending.size()+" files; applied="+apply);
    }
}
