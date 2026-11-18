package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import javax.tools.*;import com.sun.source.tree.*;import com.sun.source.util.*;

/** Rebind only the original vanilla melee imports; constructor arguments and custom SRP goals remain intact. */
public final class MeleeCalls {
    public static void main(String[] args)throws Exception{
        Path root=Paths.get(args[0]).toAbsolutePath().normalize();List<File> files=new ArrayList<File>();
        try(java.util.stream.Stream<Path> walk=Files.walk(root)){walk.filter(p->p.toString().endsWith(".java")&&!p.toString().replace('\\','/').contains("/backport/")).sorted().forEach(p->files.add(p.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();StandardJavaFileManager fm=compiler.getStandardFileManager(null,null,StandardCharsets.UTF_8);
        JavacTask task=(JavacTask)compiler.getTask(null,fm,null,Arrays.asList("-proc:none","-source","8"),null,fm.getJavaFileObjectsFromFiles(files));SourcePositions positions=Trees.instance(task).getSourcePositions();List<String> audit=new ArrayList<String>();int changed=0;
        for(CompilationUnitTree unit:task.parse()){
            Path file=Paths.get(unit.getSourceFile().toUri());String source=new String(Files.readAllBytes(file),StandardCharsets.UTF_8);List<int[]> edits=new ArrayList<int[]>();
            new TreeScanner<Void,Void>(){
                @Override public Void visitImport(ImportTree tree,Void ignored){
                    if(tree.getQualifiedIdentifier().toString().equals("net.minecraft.entity.ai.EntityAIAttackMelee")){
                        int a=(int)positions.getStartPosition(unit,tree.getQualifiedIdentifier()),b=(int)positions.getEndPosition(unit,tree.getQualifiedIdentifier());edits.add(new int[]{a,b});audit.add(root.relativize(file)+"\t"+unit.getLineMap().getLineNumber(a)+"\timport");
                    }return null;
                }
            }.scan(unit,null);
            if(!edits.isEmpty()){edits.sort((a,b)->Integer.compare(b[0],a[0]));StringBuilder out=new StringBuilder(source);for(int[] e:edits)out.replace(e[0],e[1],"com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIAttackMelee");Files.write(file,out.toString().getBytes(StandardCharsets.UTF_8));changed++;}
        }
        Files.write(Paths.get(args[1]),audit,StandardCharsets.UTF_8);fm.close();System.out.println("Converted "+audit.size()+" melee imports across "+changed+" source units");
    }
}
