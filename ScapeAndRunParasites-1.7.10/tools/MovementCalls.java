package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import javax.tools.*;import com.sun.source.tree.*;import com.sun.source.util.*;
/** Migrate native receiver APIs by syntax tree; modern super calls retain their virtual extension point. */
public final class MovementCalls {
    static final String P="com.dhanantry.scapeandrunparasites.backport.entity.movement.";
    static class Edit{int a,b;String value;Edit(int a,int b,String v){this.a=a;this.b=b;value=v;}}
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]);List<File> files=new ArrayList<File>();try(java.util.stream.Stream<Path> walk=Files.walk(root)){walk.filter(p->p.toString().endsWith(".java")&&!p.toString().replace('\\','/').contains("/backport/")).sorted().forEach(p->files.add(p.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();StandardJavaFileManager fm=compiler.getStandardFileManager(null,null,StandardCharsets.UTF_8);JavacTask task=(JavacTask)compiler.getTask(null,fm,null,Arrays.asList("-proc:none","-source","8"),null,fm.getJavaFileObjectsFromFiles(files));SourcePositions positions=Trees.instance(task).getSourcePositions();List<String> audit=new ArrayList<String>();int changed=0;
        for(CompilationUnitTree u:task.parse()){
            Path path=Paths.get(u.getSourceFile().toUri());String source=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);List<Edit> edits=new ArrayList<Edit>();
            new TreeScanner<Void,Void>(){
                int a(Tree t){return (int)positions.getStartPosition(u,t);}int b(Tree t){return (int)positions.getEndPosition(u,t);}
                @Override public Void visitImport(ImportTree t,Void p){if(t.getQualifiedIdentifier().toString().equals("net.minecraft.entity.MoverType")){edits.add(new Edit(a(t.getQualifiedIdentifier()),b(t.getQualifiedIdentifier()),P+"MoverType"));audit.add(root.relativize(path)+"\t"+u.getLineMap().getLineNumber(a(t))+"\timport");}return null;}
                @Override public Void visitMethodInvocation(MethodInvocationTree t,Void p){
                    String receiver="this",name=t.getMethodSelect().toString();if(t.getMethodSelect() instanceof MemberSelectTree){MemberSelectTree m=(MemberSelectTree)t.getMethodSelect();receiver=source.substring(a(m.getExpression()),b(m.getExpression()));name=m.getIdentifier().toString();}
                    String target=null;if(name.equals("setNoGravity")&&t.getArguments().size()==1)target=P+"GravityAccess.set";
                    if(name.equals("hasNoGravity")&&t.getArguments().isEmpty())target=P+"GravityAccess.get";
                    if(name.equals("doBlockCollisions")&&t.getArguments().isEmpty())target=P+"MovementAccess.blocks";
                    if(name.equals("moveEntity")&&t.getArguments().size()==4&&!receiver.equals("super"))target=P+"MovementAccess.move";
                    if(target==null)return super.visitMethodInvocation(t,p);
                    if(t.getArguments().isEmpty()){edits.add(new Edit(a(t),b(t),target+"("+receiver+")"));}
                    else{edits.add(new Edit(a(t.getMethodSelect()),b(t.getMethodSelect()),target));int at=a(t.getArguments().get(0));edits.add(new Edit(at,at,receiver+", "));for(ExpressionTree v:t.getArguments())scan(v,p);}
                    audit.add(root.relativize(path)+"\t"+u.getLineMap().getLineNumber(a(t))+"\t"+name);return null;
                }
            }.scan(u,null);
            if(!edits.isEmpty()){edits.sort((x,y)->Integer.compare(y.a,x.a));StringBuilder out=new StringBuilder(source);int edge=source.length();for(Edit e:edits){if(e.b>edge)throw new IllegalStateException("Overlapping movement edit");out.replace(e.a,e.b,e.value);edge=e.a;}Files.write(path,out.toString().getBytes(StandardCharsets.UTF_8));changed++;}
        }
        Files.write(Paths.get(args[1]),audit,StandardCharsets.UTF_8);System.out.println("Converted "+audit.size()+" movement/gravity boundaries across "+changed+" source units");fm.close();
    }
}
