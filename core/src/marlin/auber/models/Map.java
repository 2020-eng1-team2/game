package marlin.auber.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Map {
    public Texture mapTexture;
    public Texture topCoatTexture;
    public Texture collisionTexture;

    /**
     * Returns if a character can walk to <i>position</i>.
     * @param position a coordinate in world space
     * @return whether it's walkable
     */
    public boolean canWalk(Vector2 position) {
        return true;
    }
}
