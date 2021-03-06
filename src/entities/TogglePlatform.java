package entities;

import map.MapManager;
import mote4.scenegraph.Window;
import mote4.util.matrix.TransformationMatrix;
import mote4.util.shader.Uniform;
import mote4.util.texture.TextureMap;
import mote4.util.vertex.builder.StaticMeshBuilder;
import mote4.util.vertex.mesh.Mesh;
import org.lwjgl.opengl.GL11;

/**
 * Will either be walkable or an empty tile depending on the corresponding
 * global value.
 * @author Peter
 */
public class TogglePlatform extends Entity {
    
    private static Mesh mesh;
    
    private final int index, rotation;
    private final boolean inverted;
    private float position;
    
    static {
        mesh = StaticMeshBuilder.constructVAO(GL11.GL_TRIANGLE_FAN, 
                3, new float[] {0,0, 0, 
                                0,1, 0, 
                                1,1, 0,
                                1,0, 0}, 
                2, new float[] {0,1,
                                1,1, 
                                1,0, 
                                0,0}, 
                0, null, new float[] {0,0,1, 0,0,1, 0,0,1, 0,0,1});
    }

    public TogglePlatform(int x, int y, int h, int i, int r, boolean inv) {
        posX = x+.5f;
        posY = y+.5f;
        tileHeight = h;
        index = i;
        rotation = r;
        inverted = inv;
    }
    
    @Override
    public void onRoomInit() {
        if (!MapManager.getTimelineState().getMapState(index) ^ inverted) {
            position = .9f;
        } else {
            position = 0;
        }
    }
    
    @Override
    public boolean isWalkable() {
        return MapManager.getTimelineState().getMapState(index) ^ inverted;
    }
    
    @Override
    public void update() {
        if (!MapManager.getTimelineState().getMapState(index) ^ inverted) {
            if (position < 1)
                position += Window.delta()*3.5;
            else
                position = 1;
        } else {
            if (position > 0)
                position -= Window.delta()*3.5;
            else
                position = 0;
        }
    }

    @Override
    public void render(TransformationMatrix model) {
        model.setIdentity();
        model.translate(posX-.5f, (float)posY-.5f+.075f*position, tileHeight);
        // rotate to flip towards a certain wall
        //model.rotate((float)Math.PI/2*rotation, 0, 0, 1);
        // flip down if deactivated
        model.rotate(-(float)Math.PI/2*position, 1, 0, 0);
        
        model.bind();
        
        Uniform.vec("emissiveMult", 1);
        Uniform.vec("spriteInfo", 3,2,0);
        Uniform.vec("spriteInfoEmissive", 3,2,index+1);
        TextureMap.bindUnfiltered("entity_togglePlatform");
        mesh.render();
        Uniform.vec("emissiveMult", 0);
    }

    @Override
    public String getName() { return "Toggle Platform"; }
    @Override
    public String getAttributeString() {
        return super.getAttributeString()+"\nindex:"+index+", rotation:"+rotation+", inverted:"+inverted;
    }

    @Override
    public boolean hasLight() { return true; }
    @Override
    public float[] lightPos() { return new float[] {posX,posY,tileHeight}; }
    @Override
    public float[] lightColor() {
        switch (index) {
            case 0:
                return new float[] {1.5f,0,0};
            case 1:
                return new float[] {0,0,1.5f};
            case 2:
                return new float[] {1.5f,1.5f,0};
            case 3:
                return new float[] {0,1.5f,0};
            default:
                return new float[] {0,0,0};
        }
    }
    @Override
    public String serialize() {
        return this.getClass().getSimpleName() +","+ (int)(posX-.5) +","+ (int)(posY-.5) +","+
                tileHeight +","+ index +","+ rotation +","+ inverted;
    }
}