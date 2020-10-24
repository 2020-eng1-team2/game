package marlin.auber.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Controller;
import marlin.auber.models.Auber;

public class AuberKeyboardController implements Controller {
    private final Auber auber;
    private final Vector2 delta = new Vector2(0, 0);

    public AuberKeyboardController(Auber auber) {
        this.auber = auber;
    }

    @Override
    public void tick() {
        // Reset delta
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
        // Scale it by movement speed and delta time
        delta.scl(auber.movementSpeed * Gdx.graphics.getDeltaTime());
        // And move Auber
        auber.position = auber.position.add(delta);
    }
}
