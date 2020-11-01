package marlin.auber.models;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class World {
    public Viewport viewport;

    public Map map;

    public boolean debugMode = true;

    public World(Map map) {
        this.map = map;
        this.viewport = new ExtendViewport(20, 20);
    }
}
