package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import javax.tools.*;
import com.sun.source.tree.*;import com.sun.source.util.*;
/** Parse-only edits: does not resolve against unavailable 1.12 types or rewrite strings/comments. */
public final class WorldStateCalls {
    static final class Edit{int start,end;String value;Edit(int a,int b,String v){start=a;end=b;value=v;}}
    static String render(String source,int start,int end,List<Edit> edits){StringBuilder value=new StringBuilder(source.substring(start,end));List<Edit> selected=new ArrayList<Edit>();for(Edit e:edits)if(e.start>=start&&e.end<=end)selected.add(e);Collections.sort(selected,(a,b)->Integer.compare(b.start,a.start));for(Edit e:selected)value.replace(e.start-start,e.end-start,e.value);return value.toString();}
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]).toAbsolutePath().normalize();boolean apply=args.length>1&&args[1].equals("--apply");List<File> files=new ArrayList<File>();
        try(java.util.stream.Stream<Path> paths=Files.walk(root)){paths.filter(p->p.toString().endsWith(".java")&&!p.toString().replace('\\','/').contains("/backport/")).sorted().forEach(p->files.add(p.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<JavaFileObject>();StandardJavaFileManager manager=compiler.getStandardFileManager(diagnostics,null,StandardCharsets.UTF_8);
        JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,Arrays.asList("-proc:none","-source","8"),null,manager.getJavaFileObjectsFromFiles(files));Iterable<? extends CompilationUnitTree> units=task.parse();SourcePositions positions=Trees.instance(task).getSourcePositions();List<String> audit=new ArrayList<String>();Map<Path,String> pending=new LinkedHashMap<Path,String>();int changed=0;
        for(CompilationUnitTree unit:units){Path path=Paths.get(unit.getSourceFile().toUri());String source=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);List<Edit> edits=new ArrayList<Edit>();
            new TreeScanner<Void,Void>(){
                int start(Tree t){return (int)positions.getStartPosition(unit,t);}int end(Tree t){return (int)positions.getEndPosition(unit,t);}
                @Override public Void visitMethodInvocation(MethodInvocationTree call,Void ignored){
                    super.visitMethodInvocation(call,ignored);if(!(call.getMethodSelect() instanceof MemberSelectTree))return null;MemberSelectTree select=(MemberSelectTree)call.getMethodSelect();String name=select.getIdentifier().toString(),receiver=select.getExpression().toString();int arity=call.getArguments().size();String target=null;boolean passReceiver=true;
                    if(name.equals("getBlockState")&&arity==1)target="get";
                    else if(name.equals("setBlockState")&&(arity==2||arity==3))target="set";
                    else if(name.equals("getDefaultState")&&arity==0)target="defaultState";
                    else if(name.equals("getStateFromMeta")&&arity==1)target="fromMeta";
                    else if(name.equals("getMetaFromState")&&arity==1)target="meta";
                    else if(name.equals("getStateId")&&arity==1&&(receiver.equals("Block")||receiver.equals("net.minecraft.block.Block"))){target="stateId";passReceiver=false;}
                    else if(name.equals("getStateById")&&arity==1&&(receiver.equals("Block")||receiver.equals("net.minecraft.block.Block"))){target="stateById";passReceiver=false;}
                    if(target==null||receiver.equals("super")||receiver.endsWith(".super"))return null;
                    int a=start(call),b=end(call),paren=source.indexOf('(',end(select));if(a<0||b<0||paren<0||paren>=b)throw new IllegalStateException("Invalid parser positions");
                    String receiverCode=render(source,start(select.getExpression()),end(select.getExpression()),edits),arguments=render(source,paren+1,b-1,edits);
                    String result="BlockStates."+target+source.substring(end(select),paren)+"("+(passReceiver?receiverCode+(arity>0?", ":""):"")+arguments+")";
                    edits.removeIf(e->e.start>=a&&e.end<=b);edits.add(new Edit(a,b,result));audit.add(root.relativize(path)+"\t"+unit.getLineMap().getLineNumber(a)+"\t"+name+"\t"+target);return null;
                }
            }.scan(unit,null);
            if(edits.isEmpty())continue;changed++;boolean imported=false;for(ImportTree i:unit.getImports())if(i.getQualifiedIdentifier().toString().equals("com.dhanantry.scapeandrunparasites.backport.state.BlockStates"))imported=true;
            if(!imported){int at=source.indexOf(';',(int)positions.getEndPosition(unit,unit.getPackageName()))+1;edits.add(new Edit(at,at,"\n\nimport com.dhanantry.scapeandrunparasites.backport.state.BlockStates;"));}
            if(apply)pending.put(path,render(source,0,source.length(),edits));
        }
        for(Diagnostic<?> d:diagnostics.getDiagnostics())if(d.getKind()==Diagnostic.Kind.ERROR)throw new IllegalStateException("Source parse failed: "+d);
        manager.close();for(Map.Entry<Path,String> entry:pending.entrySet())Files.write(entry.getKey(),entry.getValue().getBytes(StandardCharsets.UTF_8));Path report=Paths.get(args.length>2?args[2]:"world-state-calls.tsv");Files.write(report,audit,StandardCharsets.UTF_8);System.out.println((apply?"Converted ":"Would convert ")+audit.size()+" state calls in "+changed+" source units.");
    }
}
