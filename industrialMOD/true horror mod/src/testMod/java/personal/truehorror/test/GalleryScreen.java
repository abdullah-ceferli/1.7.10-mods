package personal.truehorror.test;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.lwjgl.opengl.GL11;
import personal.truehorror.entity.DreadEntity;

public final class GalleryScreen extends GuiScreen {
    private final List<DreadEntity> entities;
    public GalleryScreen(List<DreadEntity> entities){this.entities=entities;}
    @Override public void drawScreen(int x,int y,float partial){
        drawRect(0,0,width,height,0xff171f28);
        drawCenteredString(fontRendererObj,"TRUE HORROR - actual in-game models",width/2,10,0xdde5df);
        int columns=4,rowHeight=(height-32)/2;
        for(DreadEntity entity:entities){
            int i=entity.kind().ordinal(),left=(i%columns)*width/columns,top=30+(i/columns)*rowHeight;
            int cx=left+width/columns/2;
            drawCenteredString(fontRendererObj,entity.kind().title,cx,top,0xe1e6e4);
            GL11.glColor4f(1,1,1,1);
            GuiInventory.func_147046_a(cx,top+rowHeight-20,36,-20,0,entity);
        }
        drawCenteredString(fontRendererObj,"User skins: Herobrine + standard-player Shadow",width/2,height-12,0x9daeb6);
    }
}
