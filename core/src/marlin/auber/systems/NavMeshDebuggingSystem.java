package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import marlin.auber.common.Assets;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.ActivePlayerCharacter;
import marlin.auber.components.Position;
import marlin.auber.models.World;

import java.io.StringWriter;
import java.util.Map;

public class NavMeshDebuggingSystem implements System {
    private final ShapeRenderer renderer = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();

    private boolean isEditActive = false;
    private World.NavNode activeNode = null;

    @Override
    public void tick() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F7)) {
            isEditActive = true;
        }
        if (isEditActive) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) {
                isEditActive = false;
            }
            Vector2 auberPos = Entity
                    .getAllEntitiesWithComponents(ActivePlayerCharacter.class)
                    .get(0)
                    .getComponent(Position.class)
                    .position;
            if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
                activeNode = World.getWorld().map.navMesh.values()
                        .stream().min((o1, o2) -> Float.compare(o1.position.dst2(auberPos), o2.position.dst2(auberPos)))
                        .orElse(null);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                World.NavNode node = new World.NavNode(
                    "node" + Integer.toHexString(World.getWorld().map.navMesh.size()),
                    new Vector2(auberPos)
                );
                World.getWorld().map.navMesh.put(node.name, node);
                activeNode = node;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.L) && activeNode != null) {
                World.NavNode nearest = World.getWorld().map.navMesh.values()
                        .stream().min((o1, o2) -> Float.compare(o1.position.dst2(auberPos), o2.position.dst2(auberPos)))
                        .orElse(null);
                if (nearest != null && !nearest.equals(activeNode)) {
                    activeNode.links.add(nearest);
                    nearest.links.add(activeNode);
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.C) && activeNode != null) {
                for (World.NavNode node : World.getWorld().map.navMesh.values()) {
                    node.links.remove(activeNode);
                }
                World.getWorld().map.navMesh.remove(activeNode.name);
                activeNode = null;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
                World.getWorld().map.navMesh.clear();
                activeNode = null;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) {
                JsonValue json = new JsonValue(JsonValue.ValueType.object);
                JsonValue nodes = new JsonValue(JsonValue.ValueType.object);
                for (Map.Entry<String, World.NavNode> pair : World.getWorld().map.navMesh.entrySet()) {
                    JsonValue coord = new JsonValue(JsonValue.ValueType.array);
                    coord.addChild(new JsonValue(Math.round(pair.getValue().position.x)));
                    coord.addChild(new JsonValue(Math.round(pair.getValue().position.y)));
                    nodes.addChild(
                        pair.getKey(),
                        coord
                    );
                }
                json.addChild("navNodes", nodes);
                JsonValue links = new JsonValue(JsonValue.ValueType.object);
                for (World.NavNode node : World.getWorld().map.navMesh.values()) {
                    JsonValue nodeLinks = new JsonValue(JsonValue.ValueType.array);
                    for (World.NavNode link : node.links) {
                        nodeLinks.addChild(new JsonValue(link.name));
                    }
                    links.addChild(node.name, nodeLinks);
                }
                json.addChild("navLinks", links);
                Gdx.app.log("NavMeshDebuggingSystem", json.toJson(JsonWriter.OutputType.json));
            }
        }
        renderer.setProjectionMatrix(World.getWorld().viewport.getCamera().combined);
        renderer.setAutoShapeType(true);
        renderer.begin();
        renderer.setColor(Color.PINK);
        for (World.NavNode node : World.getWorld().map.navMesh.values()) {
            renderer.x(
                node.position,
                1
            );
            if (node.equals(activeNode)) {
                renderer.setColor(Color.ORANGE);
                renderer.x(
                        node.position,
                        3
                );
                renderer.setColor(Color.PINK);
            }
            for (World.NavNode link : node.links) {
                renderer.line(
                    node.position.x,
                    node.position.y,
                    link.position.x,
                    link.position.y
                );
            }
        }
        renderer.end();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        if (isEditActive) {
            String draw = "Press N to create nav node at current position. " +
                    "Then walk up to another nav node and press L to link to it. " +
                    "Press K to switch active nav node to the one you're nearest to. " +
                    "Press B to delete the active node. " +
                    "Press C to clear the nav mesh (dangerous!). " +
                    "Press F9 to print the nav mesh to the console. " +
                    "Press F8 to exit edit mode.";
            Assets.fonts.fixedsys18.draw(
                batch,
                draw,
                50,
                200,
                0,
                draw.length(),
                600f,
                Align.left,
                true
            );
        }
        batch.end();
    }
}
