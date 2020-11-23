package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Entity;
import marlin.auber.common.Helpers;
import marlin.auber.common.System;
import marlin.auber.components.CellNPCAI;
import marlin.auber.components.Position;
import marlin.auber.models.World;

public class CellNPCAISystem implements System {
    private final Vector2 delta = new Vector2(0, 0);

    @Override
    public void tick() {
        for (Entity ent : Entity.getAllEntitiesWithComponents(CellNPCAI.class)) {
            delta.set(0, 0);
            CellNPCAI ai = ent.getComponent(CellNPCAI.class);
            Position currentPosition = ent.getComponent(Position.class);
            switch (ai.state) {
                case STANDING_AROUND:
                    if (ai.standingAroundTimer.getRemaining() == 0f) {
                        // move to walking
                        Vector2 newTarget = new Vector2(0, 0);
                        do {
                            newTarget.set(Helpers.randomCollectionElement(World.getWorld().map.cellNavMesh.values()).position);
                        } while (newTarget.epsilonEquals(currentPosition.position));
                        ai.target.set(newTarget);
                        ai.state = CellNPCAI.State.WALKING;
                    }
                    break;
                case WALKING:
                    if (ai.target.epsilonEquals(currentPosition.position)) {
                        // reached the goal, stand around
                        ai.state = CellNPCAI.State.STANDING_AROUND;
                        ai.standingAroundTimer.reset(5f);
                    } else {
                        // Move towards the next point on the path
                        delta.set(ai.target.x, ai.target.y);
                        delta.sub(currentPosition.position);
                        delta.clamp(0, ai.movementSpeed * Gdx.graphics.getDeltaTime());
                        currentPosition.position.add(delta);
                    }
                    break;
            }
        }
    }
}
