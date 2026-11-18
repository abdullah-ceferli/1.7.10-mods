package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import javax.tools.*;import com.sun.source.tree.*;import com.sun.source.util.*;
/** Rewrite only calls whose first argument is the vanilla particle enum; custom SRP particles remain separate. */
public final class ParticleCalls {
    static final String ACCESS="com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess";
    static class Edit{int a,b;String text;Edit(int a,int b,String text){this.a=a;this.b=b;this.text=text;}}
    static boolean particle(String type){return type.equals("EnumParticleTypes")||type.endsWith(".EnumParticleTypes");}
    public static void main(String[] args)throws Exception {
        Path root=Paths.get(args[0]).toAbsolutePath().normalize();boolean apply=args[1].equals("--apply");List<File> files=new ArrayList<File>();
        try(java.util.stream.Stream<Path> paths=Files.walk(root)){paths.filter(p->p.toString().endsWith(".java")&&!p.toString().replace('\\','/').contains("/backport/")).sorted().forEach(p->files.add(p.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<JavaFileObject>();StandardJavaFileManager fm=compiler.getStandardFileManager(diagnostics,null,StandardCharsets.UTF_8);
        JavacTask task=(JavacTask)compiler.getTask(null,fm,diagnostics,Arrays.asList("-proc:none","-source","8"),null,fm.getJavaFileObjectsFromFiles(files));
        List<CompilationUnitTree> units=new ArrayList<CompilationUnitTree>();for(CompilationUnitTree u:task.parse())units.add(u);SourcePositions positions=Trees.instance(task).getSourcePositions();
        Set<String> returns=new HashSet<String>();for(CompilationUnitTree u:units)new TreeScanner<Void,Void>(){@Override public Void visitMethod(MethodTree m,Void p){if(m.getReturnType()!=null&&particle(m.getReturnType().toString()))returns.add(m.getName()+"/"+m.getParameters().size());return super.visitMethod(m,p);}}.scan(u,null);
        Map<Path,String> pending=new LinkedHashMap<Path,String>();List<String> audit=new ArrayList<String>();
        for(CompilationUnitTree unit:units){Path path=Paths.get(unit.getSourceFile().toUri());String source=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);List<Edit> edits=new ArrayList<Edit>();
            new TreeScanner<Void,Void>() {
                final Deque<Map<String,String>> scope=new ArrayDeque<Map<String,String>>();
                int a(Tree t){return (int)positions.getStartPosition(unit,t);}int b(Tree t){return (int)positions.getEndPosition(unit,t);}String raw(Tree t){return source.substring(a(t),b(t));}
                boolean isParticle(ExpressionTree t){
                    if(t instanceof ParenthesizedTree)return isParticle(((ParenthesizedTree)t).getExpression());
                    if(t instanceof TypeCastTree)return particle(((TypeCastTree)t).getType().toString());
                    if(t instanceof IdentifierTree){for(Map<String,String> row:scope)if(row.containsKey(t.toString()))return particle(row.get(t.toString()));}
                    if(t instanceof MemberSelectTree){MemberSelectTree s=(MemberSelectTree)t;return particle(s.getExpression().toString());}
                    if(t instanceof ConditionalExpressionTree)return isParticle(((ConditionalExpressionTree)t).getTrueExpression())&&isParticle(((ConditionalExpressionTree)t).getFalseExpression());
                    if(t instanceof MethodInvocationTree){MethodInvocationTree m=(MethodInvocationTree)t;String name=m.getMethodSelect() instanceof MemberSelectTree?((MemberSelectTree)m.getMethodSelect()).getIdentifier().toString():m.getMethodSelect().toString();return returns.contains(name+"/"+m.getArguments().size());}
                    return false;
                }
                @Override public Void visitImport(ImportTree t,Void p){if(t.getQualifiedIdentifier().toString().equals("net.minecraft.util.EnumParticleTypes"))edits.add(new Edit(a(t.getQualifiedIdentifier()),b(t.getQualifiedIdentifier()),"com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes"));return super.visitImport(t,p);}
                @Override public Void visitClass(ClassTree t,Void p){Map<String,String> fields=new HashMap<String,String>();for(Tree m:t.getMembers())if(m instanceof VariableTree){VariableTree v=(VariableTree)m;if(v.getType()!=null)fields.put(v.getName().toString(),v.getType().toString());}scope.push(fields);super.visitClass(t,p);scope.pop();return null;}
                @Override public Void visitMethod(MethodTree t,Void p){Map<String,String> vars=new HashMap<String,String>();for(VariableTree v:t.getParameters())vars.put(v.getName().toString(),v.getType().toString());scope.push(vars);super.visitMethod(t,p);scope.pop();return null;}
                @Override public Void visitBlock(BlockTree t,Void p){scope.push(new HashMap<String,String>());super.visitBlock(t,p);scope.pop();return null;}
                @Override public Void visitVariable(VariableTree t,Void p){if(!scope.isEmpty()&&t.getType()!=null)scope.peek().put(t.getName().toString(),t.getType().toString());return super.visitVariable(t,p);}
                @Override public Void visitMethodInvocation(MethodInvocationTree t,Void p){
                    super.visitMethodInvocation(t,p);if(!(t.getMethodSelect() instanceof MemberSelectTree)||t.getArguments().isEmpty())return null;
                    MemberSelectTree select=(MemberSelectTree)t.getMethodSelect();boolean always=select.getIdentifier().contentEquals("spawnAlwaysVisibleParticle");if(!always&&(!select.getIdentifier().contentEquals("spawnParticle")||!isParticle(t.getArguments().get(0))))return null;
                    List<? extends ExpressionTree> values=t.getArguments();String arguments=source.substring(a(values.get(0)),b(values.get(values.size()-1)));
                    edits.add(new Edit(a(t),b(t),ACCESS+(always?".alwaysVisible(":".spawn(")+raw(select.getExpression())+", "+arguments+")"));
                    audit.add(root.relativize(path)+"\t"+unit.getLineMap().getLineNumber(a(t))+"\t"+values.size()+"\t"+raw(t).replace('\n',' '));return null;
                }
            }.scan(unit,null);
            if(!edits.isEmpty()){edits.sort((x,y)->Integer.compare(y.a,x.a));StringBuilder result=new StringBuilder(source);for(Edit e:edits)result.replace(e.a,e.b,e.text);pending.put(path,result.toString());}
        }
        Files.write(Paths.get(args[2]),audit,StandardCharsets.UTF_8);if(apply)for(Map.Entry<Path,String> e:pending.entrySet())Files.write(e.getKey(),e.getValue().getBytes(StandardCharsets.UTF_8));
        System.out.println((apply?"Converted ":"Found ")+audit.size()+" vanilla particle calls in "+pending.size()+" files (including enum import changes)");fm.close();
    }
}
