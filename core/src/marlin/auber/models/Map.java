package marlin.auber.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.*;

public class Map implements Json.Serializable {
    public Texture mapTexture;
    public Texture topCoatTexture;
    public Texture collisionTexture;

    Pixmap collisionPixmap;

    public float width;
    public float height;

    public Vector2 auberSpawn;
    public Vector2 healPoint;

    public List<Vector2> keypads = new ArrayList<>();
    public List<Vector2> teleportPads = new ArrayList<>();
    public static final float TELEPORT_PAD_USE_RANGE = 2f;
    public static final float KEYPAD_USE_RANGE = 4f;

    final java.util.Map<String, World.NavNode> navMesh = new HashMap<>();

    public Vector2 pixelSpaceToGameSpace(Vector2 pixel) {
        return pixelSpaceToGameSpace(pixel.x, pixel.y);
    }

    public Vector2 pixelSpaceToGameSpace(float x, float y) {
        // note that pixel-space has its origin top-left, while world-space is bottom-left
        return new Vector2(
                (width / mapTexture.getWidth()) * x,
                (height / mapTexture.getHeight()) * (mapTexture.getHeight() - y)
        );
    }

    public Vector2 gameSpaceToPixelSpace(Vector2 game) {
        return gameSpaceToPixelSpace(game.x, game.y);
    }

    public Vector2 gameSpaceToPixelSpace(float x, float y) {
        // note that pixel-space has its origin top-left, while world-space is bottom-left
        return new Vector2(
                (mapTexture.getWidth() / width) * x,
                mapTexture.getHeight() - ((mapTexture.getHeight() / height) * y)
        );
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
        auberSpawn = pixelSpaceToGameSpace(
            auberSpawnLoc.getFloat(0),
            auberSpawnLoc.getFloat(1)
        );

        JsonValue healPointLoc = val.get("healPoint");
        healPoint = pixelSpaceToGameSpace(
                healPointLoc.getFloat(0),
                healPointLoc.getFloat(1)
        );

        JsonValue navNodes = val.get("navNodes");
        for (JsonValue child = navNodes.child; child != null; child = child.next) {
            float[] pos = child.asFloatArray();
            World.NavNode node = new World.NavNode(child.name, pixelSpaceToGameSpace(
                    pos[0],
                    pos[1]
            ));
            this.navMesh.put(node.name, node);
        }

        JsonValue navLinks = val.get("navLinks");
        for (JsonValue link = navLinks.child; link != null; link = link.next) {
            World.NavNode source = this.navMesh.get(link.name);
            if (source == null) {
                throw new RuntimeException(String.format(
                        "Tried to build link from %s but it doesn't exist",
                        link.name
                ));
            }
            for (JsonValue child = link.child; child != null; child = child.next) {
                World.NavNode target = this.navMesh.get(child.asString());
                if (target == null) {
                    throw new RuntimeException(String.format(
                            "Tried to build link from %s to %s but the target doesn't exist",
                            link.name,
                            child.asString()
                    ));
                }
                source.links.add(target);
            }
        }

        JsonValue teleportPads = val.get("teleportPads");
        for (JsonValue child = teleportPads.child; child != null; child = child.next) {
            float[] pos = child.asFloatArray();
            this.teleportPads.add(
                    this.pixelSpaceToGameSpace(
                            pos[0],
                            pos[1]
                    )
            );
        }

        JsonValue keypads = val.get("keypads");
        for (JsonValue child = keypads.child; child != null; child = child.next) {
            float[] pos = child.asFloatArray();
            this.keypads.add(
                    this.pixelSpaceToGameSpace(
                            pos[0],
                            pos[1]
                    )
            );
        }
    }

    public void scaleGui(Texture texture, float cover, SpriteBatch batch) {
        float mapAspectRatio = (texture.getWidth() * 1f)/(texture.getHeight() * 1f); // texture width / texture height
        float currentAspectRatio = (Gdx.graphics.getWidth() * 1f)/(Gdx.graphics.getHeight() * 1f);
        float defaultAspectRatio = 16f/9f;
        // If aspect ratio of screen is less than the aspect ratio of map texture, then the width of the texture
        // needs to be 90% the width of the screen and vice versa
        if (currentAspectRatio > mapAspectRatio) {
            // float drawMapWidth is used to store the draw width of the texture. This is calculated using the draw height, this is then divided
            // by the ratios of the current and default aspect ratios before being converted into the draw width
            float drawMapHeight = 720f * cover;
            float drawMapWidth = (drawMapHeight/(currentAspectRatio/defaultAspectRatio)) * mapAspectRatio;
            Vector2 drawMapOrigin = new Vector2((1280f / 2f) - (0.5f * drawMapWidth), 720f * ((1f - cover)/2f));
            batch.draw(
                    texture,
                    drawMapOrigin.x,
                    drawMapOrigin.y,
                    drawMapWidth,
                    drawMapHeight
            );
        }
        else{
            // same as the process to calculate float drawMapWidth, except we know the draw width this time, and so are calculating the draw height
            float drawMapWidth = 1280f * cover;
            float drawMapHeight = (drawMapWidth*(currentAspectRatio/defaultAspectRatio)) / mapAspectRatio;
            Vector2 drawMapOrigin = new Vector2(1280f * ((1f - cover)/2f), (720f / 2f) - (0.5f * drawMapHeight));
            batch.draw(
                    texture,
                    drawMapOrigin.x,
                    drawMapOrigin.y,
                    drawMapWidth,
                    drawMapHeight
            );
        }
    }

}
