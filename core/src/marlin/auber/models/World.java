package marlin.auber.models;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class World {
    public Viewport viewport;

    public boolean debugMode = true;

    public World() {
        this.viewport = new ExtendViewport(10, 10);
    }
}
