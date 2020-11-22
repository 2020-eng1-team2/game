package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.NPCAI;
import marlin.auber.components.Position;
import marlin.auber.components.Walking;
import marlin.auber.models.World;

public class NPCAISystem implements System {
    private final Vector2 delta = new Vector2(0, 0);

    @Override
    public void tick() {
        for (Entity ent : Entity.getAllEntitiesWithComponents(NPCAI.class)) {
            delta.set(0, 0);
            NPCAI ai = ent.getComponent(NPCAI.class);
            Position pos = ent.getComponent(Position.class);
            Walking wal = ent.getComponent(Walking.class);
            switch (ai.state) {
                case STANDING_AROUND:
                    if (ai.standingAroundTimer.getRemaining() == 0f) {
                        // move to walking
                        Vector2 newTarget = new Vector2(0, 0);
                        do {
                            int x = (int) Math.round(Math.random() * World.getWorld().map.width);
                            int y = (int) Math.round(Math.random() * World.getWorld().map.height);
                            newTarget.set(x, y);
                        } while (!World.getWorld().inBounds(newTarget));
                        ai.path = World.getWorld().findPathTo(pos.position, newTarget, World.getWorld().map.navMesh);
                        ai.path.add(newTarget);
                        ai.next = ai.path.remove(0);
                        ai.state = NPCAI.State.WALKING;
                    }
                    break;
                case WALKING:
                    if (ai.path.isEmpty()) {
                        // reached the goal, stand around
                        wal.direction = Walking.WalkDirection.IDLE;
                        ai.state = NPCAI.State.STANDING_AROUND;
                        ai.standingAroundTimer.reset(5f);
                    } else {
                        if (ai.next.epsilonEquals(pos.position)) {
                            ai.next = ai.path.remove(0);
                        }

                        // Move towards the next point on the path
                        delta.set(ai.next.x, ai.next.y);
                        delta.sub(pos.position);
                        delta.clamp(0, ai.movementSpeed * Gdx.graphics.getDeltaTime());
                        pos.position.add(delta);
                        wal.direction = delta.x > 0 ? Walking.WalkDirection.RIGHT : Walking.WalkDirection.LEFT;
                    }
                    break;
            }
        }
    }
}
