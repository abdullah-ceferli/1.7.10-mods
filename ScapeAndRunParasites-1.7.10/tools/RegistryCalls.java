package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import javax.tools.*;import org.objectweb.asm.*;import java.util.jar.*;import com.sun.source.tree.*;import com.sun.source.util.*;
/** Scope/inheritance-aware edits for real Block and Item names; other registries remain untouched. */
public final class RegistryCalls {
 static final String BOX="AxisAlignedBB";static final class Edit{int a,b;String text;Edit(int a,int b,String t){this.a=a;this.b=b;text=t;}}
 static String simple(String text){if(text==null)return "";int generic=text.indexOf('<');if(generic>=0)text=text.substring(0,generic);return text.substring(text.lastIndexOf('.')+1);}
 static String render(String source,int a,int b,List<Edit> edits){StringBuilder result=new StringBuilder(source.substring(a,b));List<Edit> selected=new ArrayList<Edit>();for(Edit e:edits)if(e.a>=a&&e.b<=b)selected.add(e);selected.sort((x,y)->Integer.compare(y.a,x.a));for(Edit e:selected)result.replace(e.a-a,e.b-a,e.text);return result.toString();}
 public static void main(String[] args)throws Exception{
  Path root=Paths.get(args[0]).toAbsolutePath().normalize();boolean apply=args[1].equals("--apply");
  List<File> files=new ArrayList<File>();try(java.util.stream.Stream<Path> paths=Files.walk(root)){paths.filter(p->p.toString().endsWith(".java")&&!p.toString().replace('\\','/').contains("/backport/")).sorted().forEach(p->files.add(p.toFile()));}
  JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<JavaFileObject>();StandardJavaFileManager manager=compiler.getStandardFileManager(diagnostics,null,StandardCharsets.UTF_8);JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,Arrays.asList("-proc:none","-source","8"),null,manager.getJavaFileObjectsFromFiles(files));List<CompilationUnitTree> units=new ArrayList<CompilationUnitTree>();for(CompilationUnitTree unit:task.parse())units.add(unit);SourcePositions positions=Trees.instance(task).getSourcePositions();
  Map<String,Map<String,String>> allFields=new HashMap<String,Map<String,String>>();Map<String,Map<String,String>> returns=new HashMap<String,Map<String,String>>();Map<String,String> parents=new HashMap<String,String>();
  try(JarFile archive=new JarFile(args[3])){Enumeration<JarEntry> entries=archive.entries();while(entries.hasMoreElements()){JarEntry entry=entries.nextElement();if(!entry.getName().endsWith(".class"))continue;try(InputStream input=archive.getInputStream(entry)){ClassReader reader=new ClassReader(input);final String owner=simple(reader.getClassName().replace('/','.'));String parent=reader.getSuperName();if(parent!=null)parents.put(owner,simple(parent.replace('/','.')));final Map<String,String> fs=new HashMap<String,String>(),ms=new HashMap<String,String>();reader.accept(new ClassVisitor(Opcodes.ASM5){@Override public FieldVisitor visitField(int a,String n,String d,String signature,Object value){fs.put(n,simple(Type.getType(d).getClassName()));return null;}@Override public MethodVisitor visitMethod(int a,String n,String d,String sig,String[] exceptions){String key=n+"/"+Type.getArgumentTypes(d).length,value=simple(Type.getReturnType(d).getClassName());ms.put(key,ms.containsKey(key)&&!ms.get(key).equals(value)?"":value);return null;}},ClassReader.SKIP_CODE|ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);allFields.put(owner,fs);returns.put(owner,ms);}}}
  parents.put("StatefulBlock","Block");
  for(CompilationUnitTree unit:units)new TreeScanner<Void,Void>(){@Override public Void visitClass(ClassTree type,Void p){String name=type.getSimpleName().toString();if(!name.isEmpty()){if(type.getExtendsClause()!=null)parents.put(name,simple(type.getExtendsClause().toString()));Map<String,String> fields=new HashMap<String,String>(),methods=new HashMap<String,String>();for(Tree member:type.getMembers()){if(member instanceof VariableTree){VariableTree v=(VariableTree)member;fields.put(v.getName().toString(),simple(v.getType().toString()));}if(member instanceof MethodTree){MethodTree m=(MethodTree)member;if(m.getReturnType()!=null){String key=m.getName()+"/"+m.getParameters().size(),value=simple(m.getReturnType().toString());methods.put(key,methods.containsKey(key)&&!methods.get(key).equals(value)?"":value);}}}allFields.put(name,fields);returns.put(name,methods);}return super.visitClass(type,p);}}.scan(unit,null);
  Map<Path,String> pending=new LinkedHashMap<Path,String>();List<String> audit=new ArrayList<String>();
  for(CompilationUnitTree unit:units){Path path=Paths.get(unit.getSourceFile().toUri());String source=new String(Files.readAllBytes(path),StandardCharsets.UTF_8);List<Edit> edits=new ArrayList<Edit>();
   new TreeScanner<Void,Void>(){
    final Deque<Map<String,String>> scopes=new ArrayDeque<Map<String,String>>();final Deque<Map<String,String>> fields=new ArrayDeque<Map<String,String>>();final Deque<String> classes=new ArrayDeque<String>();
    int a(Tree t){return (int)positions.getStartPosition(unit,t);}int b(Tree t){return (int)positions.getEndPosition(unit,t);}
    String inherited(Map<String,Map<String,String>> table,String owner,String name){Set<String> seen=new HashSet<String>();while(owner!=null&&seen.add(owner)){Map<String,String> values=table.get(owner);if(values!=null&&values.containsKey(name))return values.get(name);owner=parents.get(owner);}return "";}
    boolean content(String owner){Set<String> seen=new HashSet<String>();while(owner!=null&&seen.add(owner)){if(owner.equals("Item")||owner.equals("Block"))return true;owner=parents.get(owner);}return false;}
    String lookup(String name){for(Map<String,String> scope:scopes)if(scope.containsKey(name))return scope.get(name);return allFields.containsKey(name)?name:"";}
    String type(Tree t){if(t==null)return "";switch(t.getKind()){
     case IDENTIFIER:{String n=((IdentifierTree)t).getName().toString();return n.equals("this")&&!classes.isEmpty()?classes.peek():lookup(n);}
     case PARENTHESIZED:return type(((ParenthesizedTree)t).getExpression());
     case TYPE_CAST:return simple(((TypeCastTree)t).getType().toString());
     case NEW_CLASS:return simple(((NewClassTree)t).getIdentifier().toString());
     case ASSIGNMENT:{AssignmentTree assignment=(AssignmentTree)t;String value=type(assignment.getVariable());return value.isEmpty()?type(assignment.getExpression()):value;}
     case ARRAY_ACCESS:{String value=type(((ArrayAccessTree)t).getExpression());return value.endsWith("[]")?value.substring(0,value.length()-2):"";}
     case CONDITIONAL_EXPRESSION:{ConditionalExpressionTree c=(ConditionalExpressionTree)t;String x=type(c.getTrueExpression()),y=type(c.getFalseExpression());return x.equals(y)?x:c.getTrueExpression().getKind()==Tree.Kind.NULL_LITERAL?y:c.getFalseExpression().getKind()==Tree.Kind.NULL_LITERAL?x:"";}
     case MEMBER_SELECT:{MemberSelectTree s=(MemberSelectTree)t;String n=s.getIdentifier().toString();if(n.equals("block")&&content(type(s.getExpression())))return "Block";if(s.getExpression().toString().equals("this"))for(Map<String,String> row:fields)if(row.containsKey(n))return row.get(n);return inherited(allFields,type(s.getExpression()),n);}
     case METHOD_INVOCATION:{MethodInvocationTree m=(MethodInvocationTree)t;if(m.getMethodSelect() instanceof IdentifierTree){String key=((IdentifierTree)m.getMethodSelect()).getName()+"/"+m.getArguments().size();for(String owner:classes){Map<String,String> row=returns.get(owner);if(row!=null&&row.containsKey(key))return row.get(key);}return "";}if(!(m.getMethodSelect() instanceof MemberSelectTree))return "";MemberSelectTree s=(MemberSelectTree)m.getMethodSelect();String n=s.getIdentifier().toString(),signature=n+"/"+m.getArguments().size();String receiver=type(s.getExpression());if(n.equals("setRegistryName"))return receiver;if(n.equals("getBlock")&&m.getArguments().isEmpty())return "Block";if(n.equals("getItem")&&m.getArguments().isEmpty())return "Item";return inherited(returns,receiver,signature);}

     default:return "";
    }}
    void replace(Tree tree,String text,String reason){int x=a(tree),y=b(tree);edits.removeIf(e->e.a>=x&&e.b<=y);edits.add(new Edit(x,y,text));audit.add(root.relativize(path)+"\t"+unit.getLineMap().getLineNumber(x)+"\t"+reason);}
    String arguments(List<? extends ExpressionTree> values){List<String> result=new ArrayList<String>();for(Tree value:values)result.add(render(source,a(value),b(value),edits));return String.join(", ",result);}
    @Override public Void visitClass(ClassTree tree,Void p){Map<String,String> row=new HashMap<String,String>();for(Tree member:tree.getMembers())if(member instanceof VariableTree){VariableTree v=(VariableTree)member;row.put(v.getName().toString(),simple(v.getType().toString()));}scopes.push(row);fields.push(row);classes.push(tree.getSimpleName().toString());super.visitClass(tree,p);classes.pop();fields.pop();scopes.pop();return null;}
    @Override public Void visitMethod(MethodTree tree,Void p){scopes.push(new HashMap<String,String>());for(VariableTree v:tree.getParameters())scopes.peek().put(v.getName().toString(),simple(v.getType().toString()));super.visitMethod(tree,p);scopes.pop();return null;}
    @Override public Void visitBlock(BlockTree tree,Void p){scopes.push(new HashMap<String,String>());super.visitBlock(tree,p);scopes.pop();return null;}
    @Override public Void visitForLoop(ForLoopTree tree,Void p){scopes.push(new HashMap<String,String>());super.visitForLoop(tree,p);scopes.pop();return null;}
    @Override public Void visitEnhancedForLoop(EnhancedForLoopTree tree,Void p){scopes.push(new HashMap<String,String>());super.visitEnhancedForLoop(tree,p);scopes.pop();return null;}
    @Override public Void visitVariable(VariableTree tree,Void p){if(!scopes.isEmpty()&&tree.getType()!=null)scopes.peek().put(tree.getName().toString(),simple(tree.getType().toString()));return super.visitVariable(tree,p);}
    @Override public Void visitMethodInvocation(MethodInvocationTree tree,Void p){
     super.visitMethodInvocation(tree,p);Tree select=tree.getMethodSelect();String name,receiver,owner;
     ExpressionTree expression=null;
     if(select instanceof MemberSelectTree){MemberSelectTree member=(MemberSelectTree)select;expression=member.getExpression();receiver=expression.toString();owner=type(expression);name=member.getIdentifier().toString();}
     else if(select instanceof IdentifierTree){name=((IdentifierTree)select).getName().toString();receiver="this";owner=classes.isEmpty()?"":classes.peek();}
     else return null;
     int count=tree.getArguments().size();
     if(receiver.equals("RegistryNames")||receiver.endsWith(".RegistryNames")||receiver.equals("NativeContentRegistry"))return null;
     String target=null,helper="RegistryNames";boolean passReceiver=true;
     if(content(owner)){if(name.equals("setRegistryName")&&(count==1||count==2))target="set";if(name.equals("getRegistryName")&&count==0)target="get";}
     if(receiver.equals("GameRegistry")&&name.equals("registerTileEntity")&&count==2&&type(tree.getArguments().get(1)).equals("ResourceLocation")){target="tile";helper="NativeContentRegistry";passReceiver=false;}
     if(target==null)return null;
     String recv=expression==null?"this":render(source,a(expression),b(expression),edits);int paren=source.indexOf('(',b(select));String arguments=render(source,paren+1,b(tree)-1,edits);
     replace(tree,helper+"."+target+"("+(passReceiver?recv+(count==0?"":", "):"")+arguments+")",name+"/"+count);return null;
    }
   }.scan(unit,null);
   if(edits.isEmpty())continue;boolean imported=false;for(ImportTree i:unit.getImports())if(i.getQualifiedIdentifier().toString().equals("com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames"))imported=true;if(!imported){int at=source.indexOf(';',(int)positions.getEndPosition(unit,unit.getPackageName()))+1;edits.add(new Edit(at,at,"\n\nimport com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;"));}if(source.contains("GameRegistry.registerTileEntity")){int at=source.indexOf(';',(int)positions.getEndPosition(unit,unit.getPackageName()))+1;edits.add(new Edit(at,at,"\nimport com.dhanantry.scapeandrunparasites.backport.registry.NativeContentRegistry;"));}pending.put(path,render(source,0,source.length(),edits));
  }
  for(Diagnostic<?> d:diagnostics.getDiagnostics())if(d.getKind()==Diagnostic.Kind.ERROR)throw new IllegalStateException("Parse failed: "+d);manager.close();if(apply)for(Map.Entry<Path,String> row:pending.entrySet())Files.write(row.getKey(),row.getValue().getBytes(StandardCharsets.UTF_8));Files.write(Paths.get(args[2]),audit,StandardCharsets.UTF_8);System.out.println("Registry expressions: "+audit.size()+" in "+pending.size()+" source units");
 }
}
