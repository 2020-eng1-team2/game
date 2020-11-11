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
        // DEBUG DAMAGE DEALER
        if(Gdx.input.isKeyJustPressed(Input.Keys.L)){
            auber.decrementHealth(5f);
        }
    }

    private boolean isTeleportGuiOpen;

    @Override
    public void renderGui(SpriteBatch batch) {
        float screenWidth = Gdx.graphics.getWidth() * 1f;
        float screenHeight = Gdx.graphics.getHeight() * 1f;
        if (isAtPad()) {
            if (isTeleportGuiOpen) {

                float mapTexWidth = auber.world.map.mapTexture.getWidth() * 1f;
                float mapTexHeight = auber.world.map.mapTexture.getHeight() * 1f;

                // TODO: calculate coordinates better
                /*
                 * What would be ideal:
                 * The map should be as close to 90% of width and height as possible
                 * but still maintain its aspect ratio
                 */
                float mapLeft;
                float mapBot;
                float mapRight;
                float mapTop;

                float screenCentreX = (screenWidth / 2f);
                float screenCentreY = (screenHeight / 2f);

//                if (screenWidth > screenHeight) {
                    // screen is wider than it's tall, so base our calculations
                    // off of the map's height
                    float scaledMapHeight = (mapTexHeight / (screenHeight * 0.9f)) * mapTexHeight;
                    float scaledMapWidth = (mapTexWidth / (screenHeight * 0.9f)) * mapTexWidth;

                    mapLeft = screenCentreX - (scaledMapWidth / 2);
                    mapRight = screenCentreX + (scaledMapWidth / 2);
                    mapBot = screenCentreY - (scaledMapHeight / 2);
                    mapTop = screenCentreY + (scaledMapHeight / 2);
//                }

                // JJs Code VVV
                float drawHeight = screenHeight * 0.9f;
                float drawWidth = drawHeight * (mapTexWidth / mapTexHeight);
                Gdx.app.log("draw dimensions", "drawHeight = " + Float.toString(drawHeight) + ", drawWidth = " + Float.toString(drawWidth));
                Gdx.app.log("screen dimensions", "X = " + Float.toString(screenWidth) + ", Y: " + Float.toString(screenHeight));
                batch.draw(
                        auber.world.map.mapTexture,
                        ((screenWidth) * 0.5f - (drawWidth * 0.5f)),
                        (screenHeight * 0.05f),
                        drawWidth,
                        drawHeight
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
        //Gdx.app.log("Remaining Health", Float.toString(auber.getHealth()));
        Assets.fonts.fixedsys18.draw(
                batch,
                "Health: " + Float.toString(auber.getHealth()),
                10, screenHeight - 50
        );

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
