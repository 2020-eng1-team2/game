package marlin.auber.systems;

import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.Position;
import marlin.auber.components.ViewportTarget;
import marlin.auber.models.World;

import java.util.Iterator;

public class ViewportTargetSystem implements System {
    @Override
    @SuppressWarnings("unchecked")
    public void tick() {
        Iterator<Entity> potentialTargets = Entity.getAllEntitiesWithComponents(Position.class, ViewportTarget.class).iterator();
        if (potentialTargets.hasNext()){
            Entity target = potentialTargets.next();
            Position pos = target.getComponent(Position.class);
            World.getWorld().viewport.getCamera().position.set(pos.position.x, pos.position.y, 0);
            // Apply it to OpenGL
            World.getWorld().viewport.apply();
        }
    }
}
