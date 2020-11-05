package marlin.auber.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Assets;
import marlin.auber.common.Controller;
import marlin.auber.common.GuiRenderer;
import marlin.auber.models.Auber;
import marlin.auber.models.Map;

import static marlin.auber.common.Helpers.*;

public class AuberKeyboardController implements Controller, GuiRenderer {
    private final Auber auber;
    private final Vector2 delta = new Vector2(0, 0);
    private final Vector2 futurePositionTest = new Vector2(0, 0);

    public AuberKeyboardController(Auber auber) {
        this.auber = auber;
    }

    @Override
    public void tick() {
        // Debug
        if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) {
            System.out.println("Set a breakpoint here!");
        }
        // Reset delta
        delta.set(0, 0);
        futurePositionTest.set(auber.position.x, auber.position.y);
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
        // Check collision
        // Note that we check collision with the *middle* of the character
        futurePositionTest.add(Auber.WIDTH / 2, Auber.HEIGHT/3);
        futurePositionTest.add(delta);
        if (auber.world.map.inBounds(futurePositionTest)) {
            // And move Auber
            auber.position = auber.position.add(delta);
        }
    }

    private boolean isTeleportGuiOpen;

    @Override
    public void renderGui(SpriteBatch batch) {
        if (isAtPad()) {
            if (isTeleportGuiOpen) {
                float screenWidth = Gdx.graphics.getWidth() * 1f;
                float screenHeight = Gdx.graphics.getHeight() * 1f;

                float mapTexWidth = auber.world.map.mapTexture.getWidth() * 1f;
                float mapTexHeight = auber.world.map.mapTexture.getHeight() * 1f;

                // TODO: calculate coordinates better
                /*
                 * What would be ideal:
                 * The map should be as close to 90% of width and height as possible
                 * but still maintain its aspect ratio
                 */
                float mapLeft = (screenWidth / 2) - (mapTexWidth / 2);
                float mapBot = (screenWidth / 2) - (mapTexHeight / 2);
//                float mapRight = (screenWidth / 2) + (mapTexWidth / 2);
//                float mapTop = (screenHeight / 2) + (mapTexHeight / 2);

                batch.draw(
                    auber.world.map.mapTexture,
                        mapLeft,
                        mapBot
                );
                Assets.fonts.fixedsys18.draw(
                        batch,
                        "Click to teleport",
                        hptw(0.2f),
                        vpth(0.2f)
                );
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    this.isTeleportGuiOpen = false;
                }
            } else {
                Assets.fonts.fixedsys18.draw(
                        batch,
                        "Press F to teleport",
                        50, 50
                );
                if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                    this.isTeleportGuiOpen = true;
                }
            }
        } else {
            if (isTeleportGuiOpen) {
                this.isTeleportGuiOpen = false;
            }
        }
    }

    private boolean isAtPad() {
        for (Vector2 pad : this.auber.world.map.teleportPads) {
            if (pad.dst2(this.auber.position) <= Math.pow(Map.TELEPORT_PAD_USE_RANGE, 2)) {
                return true;
            }
        }
        return false;
    }
}
