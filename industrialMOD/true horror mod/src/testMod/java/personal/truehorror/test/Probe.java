package personal.truehorror.test;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
public class Probe {
    protected final File root=new File(System.getProperty("horror.test.root"));
    public void init(){ }
    protected boolean has(String name){return new File(root,name+".txt").exists();}
    protected void mark(String name)throws Exception{Files.write(new File(root,name+".txt").toPath(),("PASS "+name).getBytes(StandardCharsets.UTF_8));System.out.println("HORROR_TEST PASS "+name);}
    protected void check(boolean value,String message){if(!value)throw new AssertionError(message);System.out.println("HORROR_TEST CHECK "+message);}
    protected void failed(Throwable ex){try{Files.write(new File(root,"FAILED.txt").toPath(),ex.toString().getBytes(StandardCharsets.UTF_8));}catch(Exception ignored){}ex.printStackTrace();}
}
