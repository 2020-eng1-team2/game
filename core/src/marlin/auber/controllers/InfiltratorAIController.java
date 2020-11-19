package marlin.auber.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Controller;
import marlin.auber.common.DebugRenderer;
import marlin.auber.models.Infiltrator;

import java.util.List;

public class InfiltratorAIController implements Controller, DebugRenderer {
    private final Infiltrator infiltrator;

    private Vector2 target;
    private List<Vector2> path;
    private Vector2 next;

    private final Vector2 delta = new Vector2(0, 0);

    public InfiltratorAIController(Infiltrator infiltrator) {
        this.infiltrator = infiltrator;
    }

    @Override
    public void tick() {
        if (this.target == null || this.path.isEmpty()) {
            // find a new path
            Vector2 newTarget = new Vector2(0, 0);
            do {
                int x = (int) Math.round(Math.random() * infiltrator.world.map.width);
                int y = (int) Math.round(Math.random() * infiltrator.world.map.height);
                newTarget.set(x, y);
            } while (!this.infiltrator.world.inBounds(newTarget));
            this.target = newTarget;
            this.path = this.infiltrator.world.findPathTo(this.infiltrator.position, this.target);
            this.path.add(this.target);
            this.next = this.path.remove(0);
        } else {
            if (this.next.epsilonEquals(this.infiltrator.position)) {
                this.next = path.remove(0);
            }

            // Move towards the next point on the path
            this.delta.set(this.next.x, this.next.y);
            this.delta.sub(this.infiltrator.position);
            this.delta.clamp(0, this.infiltrator.movementSpeed * Gdx.graphics.getDeltaTime());
            this.infiltrator.position.add(this.delta);
        }
    }

    @Override
    public void renderDebug(ShapeRenderer shapeRenderer) {
        if (this.target != null && !this.target.equals(Vector2.Zero)) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(
                    this.infiltrator.position,
                    this.target
            );
        }
    }
}
