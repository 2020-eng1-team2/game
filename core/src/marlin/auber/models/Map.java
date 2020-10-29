package marlin.auber.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Map implements Json.Serializable {
    public Texture mapTexture;
    public Texture topCoatTexture;
    public Texture collisionTexture;

    private Pixmap collisionPixmap;

    public float width;
    public float height;

    public Vector2 auberSpawn;

    /**
     * Returns if a character would not collide with the world at <i>position</i>.
     * @param position a coordinate in world space
     * @return whether it's walkable
     */
    public boolean inBounds(Vector2 position) {
        // Convert game-space to pixel-space
        // note that pixel-space has its origin top-left, while world-space is bottom-left
        int u = Math.round((collisionTexture.getWidth() / width) * position.x);
        int v = collisionTexture.getHeight() - Math.round((collisionTexture.getHeight() / height) * position.y);
        // And get the alpha
        return (collisionPixmap.getPixel(u, v) & 0x000F) != 0x000F;
    }

    public static Map loadMap(FileHandle file) {
        Json json = new Json();
        return json.fromJson(Map.class, file);
    }

    @Override
    public void write(Json json) {
        throw new RuntimeException("Don't serialize Map!");
    }

    @Override
    public void read(Json json, JsonValue val) {
        mapTexture = new Texture(Gdx.files.internal(val.getString("mapTexture")));
        topCoatTexture = new Texture(Gdx.files.internal(val.getString("topcoatTexture")));
        collisionTexture = new Texture(Gdx.files.internal(val.getString("collisionTexture")));

        TextureData texData = collisionTexture.getTextureData();
        texData.prepare();
        collisionPixmap = texData.consumePixmap();

        width = val.getFloat("width");
        height = val.getFloat("height");

        JsonValue auberSpawnLoc = val.get("auberSpawn");
        auberSpawn = new Vector2(
            auberSpawnLoc.getFloat(0),
            auberSpawnLoc.getFloat(1)
        );
    }
}
