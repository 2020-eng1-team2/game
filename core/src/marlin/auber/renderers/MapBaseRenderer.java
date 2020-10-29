package marlin.auber.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Renderer;
import marlin.auber.models.World;

public class MapBaseRenderer implements Renderer {
    private World world;

    public MapBaseRenderer(World world) {
        this.world = world;
    }

    @Override
    public void render(SpriteBatch batch) {
        // Render the map at the appropriate size
        batch.draw(
            world.map.mapTexture,
            0,
            0,
            world.map.width,
            world.map.height
        );
    }
}
