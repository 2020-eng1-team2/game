package marlin.auber.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import marlin.auber.common.AnimSheet;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.*;
import marlin.auber.models.Map;
import marlin.auber.models.World;

import java.util.Comparator;
import java.util.List;

public class RenderSystem implements System, Disposable {
    private final SpriteBatch batch = new SpriteBatch();
    @Override
    @SuppressWarnings("unchecked")
    public void tick() {
        batch.setProjectionMatrix(World.getWorld().viewport.getCamera().combined);
        List<Entity> renderables = Entity.getAllEntitiesWithComponents(Renderer.class);
        renderables.sort(Comparator.comparingInt(o -> o.getComponent(Renderer.class).order));

        batch.begin();
        // Draw the world
        Map map = World.getWorld().map;
        batch.draw(
            map.mapTexture,
            0,
            0,
            map.width,
            map.height
        );
        for (Entity ent : renderables) {
            Position pos = ent.getComponent(Position.class);
            assert pos != null;
            AABB aabb = ent.getComponent(AABB.class);
            assert aabb != null; // TODO AABB-less entities?
            if (ent.hasComponent(WalkingRenderer.class)) {
                WalkingRenderer wr = ent.getComponent(WalkingRenderer.class);
                Walking wlk = ent.getComponent(Walking.class);
                switch (wlk.direction) {
                    case IDLE:
                        batch.draw(
                            wr.idle,
                            pos.position.x,
                            pos.position.y,
                            aabb.size.x,
                            aabb.size.y
                        );
                        break;
                    case LEFT:
                    case RIGHT:
                        AnimSheet sht = wlk.direction == Walking.WalkDirection.RIGHT ? wr.walkRight : wr.walkLeft;
                        batch.draw(
                                sht.tickAndGet(true),
                                pos.position.x,
                                pos.position.y,
                                aabb.size.x,
                                aabb.size.y
                        );
                        break;
                }
            } else if (ent.hasComponent(StaticRenderer.class)) {
                StaticRenderer sr = ent.getComponent(StaticRenderer.class);
                batch.draw(
                    sr.tex,
                    pos.position.x,
                    pos.position.y,
                    aabb.size.x,
                    aabb.size.y
                );
            }
        }
        batch.draw(
                map.topCoatTexture,
                0,
                0,
                map.width,
                map.height
        );
        batch.end();
    }

    @Override
    public void dispose() {
        this.batch.dispose();
    }
}
