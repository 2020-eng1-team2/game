package marlin.auber.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import marlin.auber.common.Renderer;
import marlin.auber.models.World;

public class MapTopRenderer implements Renderer {
    private World world;

    public MapTopRenderer(World world) {
        this.world = world;
    }

    @Override
    public void render(SpriteBatch batch) {
        // Render the map at the appropriate size
        batch.draw(
                world.map.topCoatTexture,
                0,
                0,
                world.map.width,
                world.map.height
        );
    }
}