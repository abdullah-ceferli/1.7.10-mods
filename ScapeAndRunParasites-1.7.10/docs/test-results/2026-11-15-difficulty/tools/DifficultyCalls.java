package dev.srpbackport.tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import javax.tools.*;
import com.sun.source.tree.*;
import com.sun.source.util.*;

/** Parse-only edits keep strings/comments intact and record every migrated expression. */
public final class DifficultyCalls {
    static final String PREFIX = "com.dhanantry.scapeandrunparasites.backport.";
    static final class Edit {
        int a,b; String text;
        Edit(int a,int b,String text){this.a=a;this.b=b;this.text=text;}
    }
    static String render(String source,int start,int end,List<Edit> edits) {
        StringBuilder result=new StringBuilder(source.substring(start,end));
        List<Edit> inside=new ArrayList<Edit>();
        for(Edit e:edits)if(e.a>=start&&e.b<=end)inside.add(e);
        inside.sort((a,b)->Integer.compare(b.a,a.a));
        for(Edit e:inside)result.replace(e.a-start,e.b-start,e.text);
        return result.toString();
    }
    public static void main(String[] args)throws Exception {
        Path root=Paths.get(args[0]).toAbsolutePath().normalize(); boolean apply=args[1].equals("--apply");
        List<File> files=new ArrayList<File>();
        try(java.util.stream.Stream<Path> paths=Files.walk(root)) {
            paths.filter(p->p.toString().endsWith(".java")&&!p.toString().replace('\\','/').contains("/backport/")).sorted().forEach(p->files.add(p.toFile()));
        }
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fm=compiler.getStandardFileManager(diagnostics,null,StandardCharsets.UTF_8);
        JavacTask task=(JavacTask)compiler.getTask(null,fm,diagnostics,Arrays.asList("-proc:none","-source","8"),null,fm.getJavaFileObjectsFromFiles(files));
        List<CompilationUnitTree> units=new ArrayList<CompilationUnitTree>(); for(CompilationUnitTree unit:task.parse())units.add(unit);
        SourcePositions positions=Trees.instance(task).getSourcePositions();
        Map<Path,String> pending=new LinkedHashMap<Path,String>(); List<String> audit=new ArrayList<String>();
        for(CompilationUnitTree unit:units) {
            Path path=Paths.get(unit.getSourceFile().toUri()); String source=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);
            List<Edit> edits=new ArrayList<Edit>(); Set<String> imports=new LinkedHashSet<String>();
            new TreeScanner<Void,Void>() {
                int a(Tree t){return (int)positions.getStartPosition(unit,t);} int b(Tree t){return (int)positions.getEndPosition(unit,t);}
                String text(Tree t){return render(source,a(t),b(t),edits);}
                void replace(Tree t,String value,String why) {
                    int start=a(t),end=b(t); edits.removeIf(e->e.a>=start&&e.b<=end); edits.add(new Edit(start,end,value));
                    audit.add(root.relativize(path)+"\t"+unit.getLineMap().getLineNumber(start)+"\t"+why+"\t"+source.substring(start,end).replace('\n',' ')+"\t"+value.replace('\n',' '));
                }
                @Override public Void visitImport(ImportTree tree,Void p) {
                    if(tree.getQualifiedIdentifier().toString().equals("net.minecraft.world.DifficultyInstance"))
                        replace(tree,"import "+PREFIX+"world.DifficultyInstance;","DifficultyInstance import");
                    return null;
                }
                @Override public Void visitMethodInvocation(MethodInvocationTree tree,Void p) {
                    super.visitMethodInvocation(tree,p);
                    if(!(tree.getMethodSelect() instanceof MemberSelectTree))return null;
                    MemberSelectTree select=(MemberSelectTree)tree.getMethodSelect(); String name=select.getIdentifier().toString();
                    if(name.equals("getDifficulty")&&tree.getArguments().isEmpty()) {
                        imports.add(PREFIX+"world.DifficultyAccess"); replace(tree,"DifficultyAccess.difficulty("+text(select.getExpression())+")","world.getDifficulty/0");
                    } else if(name.equals("getDifficultyForLocation")&&tree.getArguments().size()==1) {
                        Tree arg=tree.getArguments().get(0);String position=text(arg);
                        if(arg instanceof NewClassTree) {
                            NewClassTree ctor=(NewClassTree)arg;
                            if(ctor.getIdentifier().toString().equals("BlockPos")&&ctor.getArguments().size()==1) {
                                Tree entity=ctor.getArguments().get(0);
                                if(!(entity instanceof TypeCastTree)||!((TypeCastTree)entity).getType().toString().equals("Entity"))throw new IllegalStateException("Review ambiguous position constructor: "+path+":"+position);
                                imports.add(PREFIX+"entity.EntityQueries");position="EntityQueries.position("+text(((TypeCastTree)entity).getExpression())+")";
                            }
                        }
                        imports.add(PREFIX+"world.DifficultyAccess");replace(tree,"DifficultyAccess.at("+text(select.getExpression())+", "+position+")","world.getDifficultyForLocation/1");
                    }
                    return null;
                }
            }.scan(unit,null);
            if(edits.isEmpty())continue;
            for(ImportTree i:unit.getImports())imports.remove(i.getQualifiedIdentifier().toString());
            if(!imports.isEmpty()) {
                int at=source.indexOf(';',(int)positions.getEndPosition(unit,unit.getPackageName()))+1;StringBuilder added=new StringBuilder("\n");
                for(String i:imports)added.append("\nimport ").append(i).append(';');edits.add(new Edit(at,at,added.toString()));
            }
            pending.put(path,render(source,0,source.length(),edits));
        }
        for(Diagnostic<?> d:diagnostics.getDiagnostics())if(d.getKind()==Diagnostic.Kind.ERROR)throw new IllegalStateException("Parse error: "+d);
        fm.close();if(apply)for(Map.Entry<Path,String> e:pending.entrySet())Files.write(e.getKey(),e.getValue().getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(args[2]),audit,StandardCharsets.UTF_8);
        System.out.println("Difficulty edits: "+audit.size()+" in "+pending.size()+" source files; applied="+apply);
    }
}
