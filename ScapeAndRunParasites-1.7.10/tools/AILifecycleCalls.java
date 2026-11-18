package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import javax.tools.*;import com.sun.source.tree.*;import com.sun.source.util.*;

/** Move the modern mob-tick extension to its native callback; preserve scheduler ownership. */
public final class AILifecycleCalls {
    static class Edit { int start,end; Edit(int a,int b){start=a;end=b;} }
    public static void main(String[] args) throws Exception {
        Path root=Paths.get(args[0]).toAbsolutePath().normalize(); List<File> files=new ArrayList<File>();
        try(java.util.stream.Stream<Path> walk=Files.walk(root)){walk.filter(p->p.toString().endsWith(".java")).sorted().forEach(p->files.add(p.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fm=compiler.getStandardFileManager(null,null,StandardCharsets.UTF_8);
        JavacTask task=(JavacTask)compiler.getTask(null,fm,null,Arrays.asList("-proc:none","-source","8"),null,fm.getJavaFileObjectsFromFiles(files));
        SourcePositions positions=Trees.instance(task).getSourcePositions();List<String> audit=new ArrayList<String>();int changed=0;
        for(CompilationUnitTree unit:task.parse()){
            Path file=Paths.get(unit.getSourceFile().toUri());String source=new String(Files.readAllBytes(file),StandardCharsets.UTF_8);List<Edit> edits=new ArrayList<Edit>();
            new TreeScanner<Void,Void>(){
                int at(Tree tree){return(int)positions.getStartPosition(unit,tree);}int end(Tree tree){return(int)positions.getEndPosition(unit,tree);}
                void add(int a,int b,String kind){if(!source.substring(a,b).equals("updateAITasks"))throw new AssertionError("Unexpected callback token");edits.add(new Edit(a,b));audit.add(root.relativize(file)+"\t"+unit.getLineMap().getLineNumber(a)+"\t"+kind);}
                @Override public Void visitMethod(MethodTree tree,Void value){
                    if(tree.getName().contentEquals("updateAITasks")){
                        if(!tree.getParameters().isEmpty()||!tree.getReturnType().toString().equals("void")||tree.getBody()==null)throw new AssertionError("Unexpected mob tick signature");
                        int start=end(tree.getReturnType());while(Character.isWhitespace(source.charAt(start)))start++;
                        add(start,start+"updateAITasks".length(),"mob-tick-declaration");
                    }return super.visitMethod(tree,value);
                }
                @Override public Void visitMethodInvocation(MethodInvocationTree tree,Void value){
                    ExpressionTree select=tree.getMethodSelect();
                    if(select instanceof MemberSelectTree&&((MemberSelectTree)select).getIdentifier().contentEquals("updateAITasks")){
                        if(!tree.getArguments().isEmpty()||!((MemberSelectTree)select).getExpression().toString().equals("super"))throw new AssertionError("Review non-super mob tick receiver");
                        add(end(select)-"updateAITasks".length(),end(select),"mob-tick-super-call");
                    }return super.visitMethodInvocation(tree,value);
                }
            }.scan(unit,null);
            if(!edits.isEmpty()){
                edits.sort((a,b)->Integer.compare(b.start,a.start));StringBuilder converted=new StringBuilder(source);
                for(Edit edit:edits)converted.replace(edit.start,edit.end,"updateAITick");
                Files.write(file,converted.toString().getBytes(StandardCharsets.UTF_8));changed++;
            }
        }
        Files.write(Paths.get(args[1]),audit,StandardCharsets.UTF_8);fm.close();
        System.out.println("Converted "+audit.size()+" mob-tick boundaries across "+changed+" source units");
    }
}
