package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.AABB;
import marlin.auber.components.KeyboardMovement;
import marlin.auber.components.Position;
import marlin.auber.components.Walking;
import marlin.auber.models.World;

public class KeyboardMovementSystem implements System {
    // Normally these would be declared inside tick(), but we create them here to avoid
    // allocating objects each frame
    private final Vector2 delta = new Vector2(0, 0);
    private final Vector2 scaledDelta = new Vector2(0, 0);
    private final Vector2 futurePositionTest = new Vector2(0, 0);
    @Override
    @SuppressWarnings("unchecked")
    public void tick() {
        delta.set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            delta.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            delta.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            delta.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            delta.x += 1;
        }
        for (Entity e : Entity.getAllEntitiesWithComponents(Position.class, KeyboardMovement.class)) {
            KeyboardMovement kbm = e.getComponent(KeyboardMovement.class);
            if (kbm.frozen) {
                continue;
            }
            Position pos = e.getComponent(Position.class);
            // may be null!
            Walking wal = e.getComponent(Walking.class);
            // may be null
            AABB aabb = e.getComponent(AABB.class);
            scaledDelta.set(delta);
            scaledDelta.scl(kbm.movementSpeed * Gdx.graphics.getDeltaTime());
            futurePositionTest.set(pos.position);
            futurePositionTest.add(scaledDelta);
            if (!delta.epsilonEquals(Vector2.Zero) && isLegalMovement(futurePositionTest, aabb)) {
                pos.position.set(futurePositionTest);
                if (wal != null) {
                    wal.direction = scaledDelta.x > 0 ? Walking.WalkDirection.RIGHT : Walking.WalkDirection.LEFT;
                }
            } else {
                if (wal != null) {
                    wal.direction = Walking.WalkDirection.IDLE;
                }
            }
        }
    }

    private boolean isLegalMovement(Vector2 position, AABB aabbOrNull) {
        if (aabbOrNull != null) {
            if (aabbOrNull.hasTag(AABB.TAG_COLLISION)) {
                return World.getWorld().inBounds(position, aabbOrNull.size);
            } else if (aabbOrNull.hasTag(AABB.TAG_COLLISION_X_ONLY)) {
                // TODO allocation
                return World.getWorld().inBounds(position)
                        && World.getWorld().inBounds(new Vector2(position.x + aabbOrNull.size.x, position.y));
            } else {
                return World.getWorld().inBounds(position);
            }
        } else {
            return World.getWorld().inBounds(position);
        }
    }
}
