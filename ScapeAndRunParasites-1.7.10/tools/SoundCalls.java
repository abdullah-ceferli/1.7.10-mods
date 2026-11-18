package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import javax.tools.*;import com.sun.source.tree.*;import com.sun.source.util.*;
/** Parsed source edits only: nested calls are edited at disjoint method-select/argument boundaries. */
public final class SoundCalls {
    static final String ROOT="com.dhanantry.scapeandrunparasites.backport.sound.";
    static class Edit{int a,b;String text;Edit(int a,int b,String text){this.a=a;this.b=b;this.text=text;}}
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]);List<File> files=new ArrayList<File>();
        try(java.util.stream.Stream<Path> p=Files.walk(root)){p.filter(x->x.toString().endsWith(".java")&&!x.toString().replace('\\','/').contains("/backport/")).sorted().forEach(x->files.add(x.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<JavaFileObject>();StandardJavaFileManager fm=compiler.getStandardFileManager(diagnostics,null,StandardCharsets.UTF_8);
        JavacTask task=(JavacTask)compiler.getTask(null,fm,diagnostics,Arrays.asList("-proc:none","-source","8"),null,fm.getJavaFileObjectsFromFiles(files));
        SourcePositions positions=Trees.instance(task).getSourcePositions();List<String> audit=new ArrayList<String>();int count=0;
        for(CompilationUnitTree u:task.parse()){
            Path path=Paths.get(u.getSourceFile().toUri());String source=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);List<Edit> edits=new ArrayList<Edit>();
            new TreeScanner<Void,Void>(){
                int a(Tree t){return (int)positions.getStartPosition(u,t);}int b(Tree t){return (int)positions.getEndPosition(u,t);}
                void edit(int a,int b,String value){edits.add(new Edit(a,b,value));}
                void call(MethodInvocationTree t,String target,String receiver){
                    edit(a(t.getMethodSelect()),b(t.getMethodSelect()),target);
                    if(!receiver.isEmpty())edit(a(t.getArguments().get(0)),a(t.getArguments().get(0)),receiver+", ");
                    audit.add(root.relativize(path)+"\t"+u.getLineMap().getLineNumber(a(t))+"\t"+t.getMethodSelect()+"\t"+target);
                }
                @Override public Void visitImport(ImportTree t,Void p){
                    String old=t.getQualifiedIdentifier().toString(),value=null;
                    if(old.equals("net.minecraft.init.SoundEvents"))value=ROOT+"SoundEvents";
                    if(old.equals("net.minecraft.util.SoundCategory"))value=ROOT+"ModernSoundCategory";
                    if(old.equals("net.minecraft.block.SoundType"))value=ROOT+"SoundType";
                    if(old.equals("net.minecraft.network.play.server.SPacketSoundEffect"))value="net.minecraft.network.Packet";
                    if(old.equals("net.minecraft.client.audio.PositionedSoundRecord"))value=ROOT+"client.SoundRecords";
                    if(value!=null)edit(a(t.getQualifiedIdentifier()),b(t.getQualifiedIdentifier()),value);return null;
                }
                @Override public Void visitIdentifier(IdentifierTree t,Void p){
                    String name=t.getName().toString();
                    if(name.equals("SoundCategory"))edit(a(t),b(t),ROOT+"ModernSoundCategory");
                    if(name.equals("SoundType"))edit(a(t),b(t),ROOT+"SoundType");
                    if(name.equals("PositionedSoundRecord"))edit(a(t),b(t),ROOT+"client.SoundRecords");
                    if(name.equals("SPacketSoundEffect"))edit(a(t),b(t),"net.minecraft.network.Packet");
                    return null;
                }
                @Override public Void visitNewClass(NewClassTree t,Void p){
                    if(t.getIdentifier().toString().equals("SPacketSoundEffect")){
                        edit(a(t),b(t.getIdentifier()),ROOT+"SoundAccess.packet");
                        for(ExpressionTree arg:t.getArguments())scan(arg,p);return null;
                    }return super.visitNewClass(t,p);
                }
                @Override public Void visitMethodInvocation(MethodInvocationTree t,Void p){
                    Tree select=t.getMethodSelect();String receiver="this",name=select.toString();
                    if(select instanceof MemberSelectTree){MemberSelectTree m=(MemberSelectTree)select;receiver=source.substring(a(m.getExpression()),b(m.getExpression()));name=m.getIdentifier().toString();}
                    if(name.equals("getSoundCategory")&&t.getArguments().isEmpty()){
                        edit(a(t),b(t),ROOT+"SoundAccess.category("+receiver+")");return null;
                    }
                    if(name.equals("playSound")&&t.getArguments().size()>=3){
                        call(t,ROOT+"SoundAccess.play",receiver);for(ExpressionTree arg:t.getArguments())scan(arg,p);return null;
                    }
                    if(name.equals("getSoundType")&&t.getArguments().size()==4){
                        call(t,ROOT+"SoundType.get",receiver);for(ExpressionTree arg:t.getArguments())scan(arg,p);return null;
                    }
                    return super.visitMethodInvocation(t,p);
                }
            }.scan(u,null);
            if(!edits.isEmpty()){edits.sort((x,y)->Integer.compare(y.a,x.a));StringBuilder result=new StringBuilder(source);int edge=source.length()+1;
                for(Edit e:edits){if(e.b>edge)throw new IllegalStateException("Overlapping sound edit "+path+":"+e.a);result.replace(e.a,e.b,e.text);edge=e.a;}
                Files.write(path,result.toString().getBytes(StandardCharsets.UTF_8));count++;
            }
        }
        Files.write(Paths.get(args[1]),audit,StandardCharsets.UTF_8);System.out.println("Converted "+audit.size()+" calls across "+count+" source units including sound imports/types");fm.close();
    }
}
