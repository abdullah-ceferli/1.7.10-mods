package dev.srpbackport.tools;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import java.util.jar.*;
import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
import cpw.mods.fml.common.asm.transformers.AccessTransformer;
/** Apply actual FML rules to a private development copy; never edit the shared Minecraft cache. */
public final class ApplyCompileAccess extends AccessTransformer{
 ApplyCompileAccess(String path)throws IOException{super(path);}
 public static void main(String[] args){try{run(args);}catch(Exception e){throw new RuntimeException(e);}}
 static void run(String[] args)throws Exception{
  java.lang.reflect.Field side=cpw.mods.fml.relauncher.FMLRelaunchLog.class.getDeclaredField("side");side.setAccessible(true);side.set(null,cpw.mods.fml.relauncher.Side.SERVER);
  ApplyCompileAccess at=new ApplyCompileAccess(args[2]);Map<String,Set<String>> targets=new LinkedHashMap<String,Set<String>>();Set<String> mutable=new HashSet<String>();
  for(String line:Files.readAllLines(Paths.get(args[2]),StandardCharsets.UTF_8)){line=line.split("#",2)[0].trim();if(line.isEmpty())continue;String[] p=line.split("\\s+");targets.computeIfAbsent(p[1],k->new LinkedHashSet<String>()).add(p[2]);if(p[0].endsWith("-f"))mutable.add(p[1]+"."+p[2]);}
  int fields=0;Set<String> touched=new HashSet<String>();
  try(JarFile input=new JarFile(args[0]);JarOutputStream output=new JarOutputStream(new FileOutputStream(args[1]))){
   Enumeration<JarEntry> entries=input.entries();while(entries.hasMoreElements()){JarEntry entry=entries.nextElement();byte[] bytes;try(InputStream in=input.getInputStream(entry)){ByteArrayOutputStream buffer=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1)buffer.write(b,0,n);bytes=buffer.toByteArray();}
    if(entry.getName().endsWith(".class")){String name=entry.getName().substring(0,entry.getName().length()-6).replace('/','.');if(targets.containsKey(name)){
     byte[] changed=at.transform(name,name,bytes);ClassNode before=new ClassNode(),after=new ClassNode();new ClassReader(bytes).accept(before,0);new ClassReader(changed).accept(after,0);
     for(int i=0;i<before.fields.size();i++){FieldNode old=(FieldNode)before.fields.get(i),now=(FieldNode)after.fields.get(i);boolean selected=targets.get(name).contains(old.name);int wanted=selected?(old.access&~7)|Opcodes.ACC_PUBLIC:old.access;if(selected&&mutable.contains(name+"."+old.name))wanted&=~Opcodes.ACC_FINAL;if(now.access!=wanted)throw new AssertionError("Unexpected access change: "+name+"."+old.name);if(selected)fields++;}
     bytes=changed;touched.add(name);
    }}
    output.putNextEntry(new JarEntry(entry.getName()));output.write(bytes);output.closeEntry();
   }
  }
  if(fields!=7||touched.size()!=4)throw new AssertionError("Expected exactly seven fields in four classes, got "+fields+"/"+touched.size());
  System.out.println("PASS: actual FML access transformer changed exactly seven required fields; only the selected potion table final flag may change.");
 }
}
