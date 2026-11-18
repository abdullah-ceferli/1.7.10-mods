package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import javax.tools.*;import com.sun.source.tree.*;import com.sun.source.util.*;

/** Rebind property storage/evaluation while retaining original callback bodies and side annotations. */
public final class ItemPropertyCalls {
    static final String PREFIX="com.dhanantry.scapeandrunparasites.backport.item.";
    static class Edit{int a,b;String value;Edit(int x,int y,String text){a=x;b=y;value=text;}}
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]).toAbsolutePath().normalize();List<File> files=new ArrayList<File>();
        try(java.util.stream.Stream<Path> walk=Files.walk(root)){walk.filter(p->p.toString().endsWith(".java")&&!p.toString().replace('\\','/').contains("/backport/")).sorted().forEach(p->files.add(p.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();StandardJavaFileManager fm=compiler.getStandardFileManager(null,null,StandardCharsets.UTF_8);
        JavacTask task=(JavacTask)compiler.getTask(null,fm,null,Arrays.asList("-proc:none","-source","8"),null,fm.getJavaFileObjectsFromFiles(files));SourcePositions positions=Trees.instance(task).getSourcePositions();List<String> audit=new ArrayList<String>();int changed=0;
        for(CompilationUnitTree unit:task.parse()){
            Path file=Paths.get(unit.getSourceFile().toUri());String source=new String(Files.readAllBytes(file),StandardCharsets.UTF_8);List<Edit> edits=new ArrayList<Edit>();
            new TreeScanner<Void,Void>(){
                int at(Tree tree){return(int)positions.getStartPosition(unit,tree);}int end(Tree tree){return(int)positions.getEndPosition(unit,tree);}
                void audit(Tree tree,String kind){audit.add(root.relativize(file)+"\t"+unit.getLineMap().getLineNumber(at(tree))+"\t"+kind);}
                @Override public Void visitImport(ImportTree tree,Void ignored){if(tree.getQualifiedIdentifier().toString().equals("net.minecraft.item.IItemPropertyGetter")){edits.add(new Edit(at(tree.getQualifiedIdentifier()),end(tree.getQualifiedIdentifier()),PREFIX+"IItemPropertyGetter"));audit(tree,"import");}return null;}
                @Override public Void visitMethodInvocation(MethodInvocationTree tree,Void ignored){
                    ExpressionTree select=tree.getMethodSelect();if(!(select instanceof MemberSelectTree))return super.visitMethodInvocation(tree,ignored);
                    MemberSelectTree member=(MemberSelectTree)select;String name=member.getIdentifier().toString();
                    if(!name.equals("addPropertyOverride")&&!name.equals("getPropertyGetter"))return super.visitMethodInvocation(tree,ignored);
                    int expected=name.equals("addPropertyOverride")?2:1;if(tree.getArguments().size()!=expected)throw new AssertionError("Unexpected property arity");
                    String receiver=source.substring(at(member.getExpression()),end(member.getExpression()));edits.add(new Edit(at(select),end(select),PREFIX+"ItemProperties."+(expected==2?"add":"get")));
                    int first=at(tree.getArguments().get(0));edits.add(new Edit(first,first,receiver+", "));audit(tree,name);
                    for(ExpressionTree value:tree.getArguments())scan(value,ignored);return null;
                }
            }.scan(unit,null);
            if(!edits.isEmpty()){
                edits.sort((a,b)->Integer.compare(b.a,a.a));StringBuilder out=new StringBuilder(source);int edge=source.length();for(Edit e:edits){if(e.b>edge)throw new AssertionError("Overlapping property conversion");out.replace(e.a,e.b,e.value);edge=e.a;}Files.write(file,out.toString().getBytes(StandardCharsets.UTF_8));changed++;
            }
        }
        Files.write(Paths.get(args[1]),audit,StandardCharsets.UTF_8);fm.close();System.out.println("Converted "+audit.size()+" property boundaries across "+changed+" source units");
    }
}
