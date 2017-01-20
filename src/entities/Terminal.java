package entities;

import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.mesh.Mesh;
import mote4.util.vertex.builder.StaticMeshBuilder;
import org.lwjgl.opengl.GL11;
import scenes.TerminalScene;
import terminal.TerminalSession;

/**
 *
 * @author Peter
 */
public class Terminal extends Entity {
    
    private static Mesh mesh;
    private int index, delay;
    private TerminalSession session; // the terminal session associated with this terminal
    
    static {
        mesh = StaticMeshBuilder.constructVAO(GL11.GL_TRIANGLE_FAN, 
                3, new float[] {0, .1f, .5f, 
                                1, .1f, .5f, 
                                1, .1f, 1.5f,
                                0, .1f, 1.5f}, 
                2, new float[] {0,1,
                                1,1, 
                                1,0, 
                                0,0}, 
                0, null, new float[] {0,1,0, 0,1,0, 0,1,0, 0,1,0});
    }
    
    public Terminal(int x, int y) {
        posX = x+.5f;
        posY = y+.25f;
        hitboxH = .4f;
        index = 0;
        delay = 0;
    }
    
    @Override
    public void render(TransformationMatrix model) {
        
        model.translate((float)posX-.5f, (float)posY-.05f, tileHeight());
        //model.rotate(.78f, 1, 0, 0);
        model.makeCurrent();
        Uniform.varFloat("spriteInfo", 2,2,index);
        Uniform.varFloat("spriteInfoEmissive", 2,2,index+2);
        Uniform.varFloat("emissiveMult", 1);
        TextureMap.bindUnfiltered("entity_terminal");
        mesh.render();
        Uniform.varFloat("emissiveMult", 0);
    }
    
    @Override
    public void playerUse() {
        if (session == null)
            session = new TerminalSession();
        TerminalScene.openTerminal(session);
    }

    @Override
    public void update() {
        delay--;
        if (delay <= 0) {
            index++;
            index %= 2;
            delay = 15;
        }
    }

    @Override
    public String getName() { return "Terminal"; }
}
