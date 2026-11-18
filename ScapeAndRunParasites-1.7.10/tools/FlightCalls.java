package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import javax.tools.*;import com.sun.source.tree.*;import com.sun.source.util.*;
/** Parsed flight-state and atan2 edits; extracted test controllers retain the actual converted method bodies. */
public final class FlightCalls {
    static final String HELPER="com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper";
    static final String MATH="com.dhanantry.scapeandrunparasites.backport.math.ModernMath";
    static class Edit{int a,b;String text;Edit(int a,int b,String text){this.a=a;this.b=b;this.text=text;}}
    static class Controller{int a,b;String owner;Controller(int a,int b,String owner){this.a=a;this.b=b;this.owner=owner;}}
    static String apply(String source,int start,int end,List<Edit> edits){
        StringBuilder out=new StringBuilder(source.substring(start,end));List<Edit> selected=new ArrayList<Edit>();
        for(Edit e:edits)if(e.a>=start&&e.b<=end)selected.add(e);
        selected.sort((x,y)->Integer.compare(y.a,x.a));int edge=end;
        for(Edit e:selected){if(e.b>edge)throw new IllegalStateException("Overlapping flight edit");out.replace(e.a-start,e.b-start,e.text);edge=e.a;}
        return out.toString();
    }
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]),generated=Paths.get(args[1]);boolean extractOnly=args.length>3&&args[3].equals("--extract-only");Files.createDirectories(generated);
        List<File> files=new ArrayList<File>();try(java.util.stream.Stream<Path> paths=Files.walk(root)){paths.filter(p->p.toString().endsWith(".java")&&!p.toString().replace('\\','/').contains("/backport/")).sorted().forEach(p->files.add(p.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();StandardJavaFileManager fm=compiler.getStandardFileManager(null,null,StandardCharsets.UTF_8);
        JavacTask task=(JavacTask)compiler.getTask(null,fm,null,Arrays.asList("-proc:none","-source","8"),null,fm.getJavaFileObjectsFromFiles(files));SourcePositions positions=Trees.instance(task).getSourcePositions();
        List<String> audit=new ArrayList<String>(),catalog=new ArrayList<String>();int changed=0,controllers=0,math=0;
        for(CompilationUnitTree unit:task.parse()){
            Path path=Paths.get(unit.getSourceFile().toUri());String source=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);List<Edit> edits=new ArrayList<Edit>();List<Controller> snippets=new ArrayList<Controller>();
            new TreeScanner<Void,Void>(){
                String outer;int a(Tree t){return (int)positions.getStartPosition(unit,t);}int b(Tree t){return (int)positions.getEndPosition(unit,t);}
                void edit(Tree t,String value,String kind){edits.add(new Edit(a(t),b(t),value));audit.add(root.relativize(path)+"\t"+unit.getLineMap().getLineNumber(a(t))+"\t"+kind);}
                @Override public Void visitClass(ClassTree t,Void p){
                    String old=outer;if(outer==null)outer=t.getSimpleName().toString();
                    String parent=t.getExtendsClause()==null?"":t.getExtendsClause().toString();
                    if(parent.equals("EntityMoveHelper")||parent.equals(HELPER)){
                        if(!parent.equals(HELPER))edit(t.getExtendsClause(),HELPER,"flight-parent");
                        boolean update=false;for(Tree member:t.getMembers())if(member instanceof MethodTree&&((MethodTree)member).getName().contentEquals("onUpdateMoveHelper"))update=true;
                        if(!update)throw new IllegalStateException("Flight controller has no original update body: "+path);
                        snippets.add(new Controller(a(t),b(t),outer));
                    }
                    super.visitClass(t,p);outer=old;return null;
                }
                @Override public Void visitMemberSelect(MemberSelectTree t,Void p){
                    if(t.getExpression().toString().equals("EntityMoveHelper")&&t.getIdentifier().contentEquals("Action")){edit(t.getExpression(),HELPER,"flight-action");return null;}
                    return super.visitMemberSelect(t,p);
                }
                @Override public Void visitMethodInvocation(MethodInvocationTree t,Void p){
                    if(t.getMethodSelect() instanceof MemberSelectTree){MemberSelectTree m=(MemberSelectTree)t.getMethodSelect();
                        if(m.getIdentifier().contentEquals("atan2")&&(m.getExpression().toString().equals("MathHelper")||m.getExpression().toString().equals("net.minecraft.util.MathHelper")))edit(m.getExpression(),MATH,"atan2");
                    }return super.visitMethodInvocation(t,p);
                }
            }.scan(unit,null);
            for(Controller c:snippets){
                String body=apply(source,c.a,c.b,edits);
                String text="package dev.srpbackport.tests.flight.samples;\nimport net.minecraft.entity.*;import net.minecraft.entity.ai.EntityMoveHelper;import net.minecraft.util.MathHelper;import com.dhanantry.scapeandrunparasites.backport.math.Boxes;\n"+
                    "public class "+c.owner+" extends dev.srpbackport.tests.FlightActor {\npublic "+c.owner+"(net.minecraft.world.World world){super(world);moveHelper=new AIMoveControl(this);}\n";
                if(c.owner.equals("EntityProjectileHomming"))text+="public static Entity access$000("+c.owner+" actor){return actor.target;}\n";
                if(c.owner.equals("EntityFlam"))text+="public static boolean access$000("+c.owner+" actor){return actor.moveM;}\n";
                text+=body+"\n}\n";Files.write(generated.resolve(c.owner+".java"),text.getBytes(StandardCharsets.UTF_8));
                String original=unit.getPackageName().toString().replace('.','/')+"/"+c.owner;
                catalog.add(original+"=dev/srpbackport/tests/flight/samples/"+c.owner);controllers++;
            }
            if(!edits.isEmpty()){if(!extractOnly)Files.write(path,apply(source,0,source.length(),edits).getBytes(StandardCharsets.UTF_8));changed++;}
        }
        for(String row:audit)if(row.endsWith("\tatan2"))math++;
        Files.write(generated.resolve("controllers.properties"),catalog,StandardCharsets.UTF_8);if(!extractOnly)Files.write(Paths.get(args[2]),audit,StandardCharsets.UTF_8);
        if(controllers!=25)throw new IllegalStateException("Expected all 25 original controllers, got "+controllers);
        System.out.println((extractOnly?"Extracted ":"Converted ")+controllers+" flight controllers; "+math+" atan2 calls in "+changed+" modified source units");fm.close();
    }
}
