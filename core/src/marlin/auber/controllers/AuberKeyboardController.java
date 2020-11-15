package marlin.auber.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Assets;
import marlin.auber.common.Controller;
import marlin.auber.common.DebugRenderer;
import marlin.auber.common.GuiRenderer;
import marlin.auber.models.Auber;
import marlin.auber.models.Map;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static marlin.auber.common.Helpers.*;

public class AuberKeyboardController implements Controller, GuiRenderer {
    private final Auber auber;
    private final Vector2 delta = new Vector2(0, 0);
    private final Vector2 futurePositionTest = new Vector2(0, 0);

    private final Texture padHighlight = new Texture(Gdx.files.internal("graphics/teleportHighlight.png"));
    private final Texture padHighlightActive = new Texture(Gdx.files.internal("graphics/teleportHighlightActive.png"));

    ShapeRenderer healthShapeRenderer;

    public AuberKeyboardController(Auber auber) {
        this.auber = auber;
    }

    // TODO: Bounding Box Collision (For side walls)

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
        futurePositionTest.add(Auber.WIDTH / 2, Auber.HEIGHT / 2);
        futurePositionTest.add(delta);
        if (delta.epsilonEquals(0, 0)) {
            auber.walkDirection = Auber.WalkDirection.IDLE;
        } else {
            auber.walkDirection = delta.x > 0 ? Auber.WalkDirection.RIGHT : Auber.WalkDirection.LEFT;
            if (auber.world.map.inBounds(futurePositionTest)) {
                // And move Auber
                auber.position = auber.position.add(delta);
            } else if (auber.world.map.inBounds(new Vector2(futurePositionTest.x, futurePositionTest.y - delta.y))) {
                // Y is Out of bounds
                auber.position = auber.position.add(new Vector2(delta.x, 0f));
            } else if (auber.world.map.inBounds(new Vector2(futurePositionTest.x - delta.x, futurePositionTest.y))) {
                // X is Out of bounds
                auber.position = auber.position.add(new Vector2(0f, delta.y));
            }
        }
        // DEBUG DAMAGE DEALER
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            auber.decrementHealth(5f);
        }
    }

    private boolean isTeleportGuiOpen;
    private boolean isKeypadGuiOpen;

    @Override
    public void renderGui(SpriteBatch batch) {
        /* Abbreviations:
         * SS - screen space - origin top-left
         * GS - GUI-Space - origin BL
         * WS - World-Space - origin BL
         * UV - UV-space - origin idk lmao
         */
        float ssScreenW = Gdx.graphics.getWidth() * 1f;
        float ssScreenH = Gdx.graphics.getHeight() * 1f;
        // GS and SS are equivalent except for the origin
        if (isAtHealPoint()) {
            Assets.fonts.fixedsys18.draw(
                    batch,
                    "Press F to heal",
                    50, 50
            );
            if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                this.auber.resetHealth();
            }
        }
        batch.end();
        // DRAW HEALTH BAR
        drawHealthBar(healthShapeRenderer);
        batch.begin();
        Assets.fonts.fixedsys18.draw(
                batch,
                "Health: " + (int) auber.getHealth(),
                ssScreenW * (25f/1280f), ssScreenH - (ssScreenH * (30f/720f))
        );
        if (isAtPad()) {
            if (isTeleportGuiOpen) {
                float uvMapTexW = auber.world.map.mapTexture.getWidth() * 1f;
                float uvMapTexH = auber.world.map.mapTexture.getHeight() * 1f;

                float gsDrawH = ssScreenH * 0.9f;
                float gsDrawW = gsDrawH * (uvMapTexW / uvMapTexH);
                float gsDrawX = (ssScreenW) * 0.5f - (gsDrawH * 0.5f);
                float gsDrawY = ssScreenH * 0.05f;
                scaleGui(auber.world.map.mapTexture, 0.9f, batch);

                // TODO: Fix the draw locations of the pads
                // Draw the pads
                for (Vector2 wsPad : auber.world.map.teleportPads) {
                    float gsPadX = (wsPad.x / auber.world.map.width) * gsDrawW + gsDrawX;
                    float gsPadY = (wsPad.y / auber.world.map.height) * gsDrawH + gsDrawY;

                    float ssMouseX = Gdx.input.getX();
                    float ssMouseY = Gdx.input.getY();

                    float gsMouseY = ssScreenH - ssMouseY;

                    if (Vector2.dst2(gsPadX, gsPadY, ssMouseX, gsMouseY) < Math.pow(64, 2)) {
                        batch.draw(
                                padHighlightActive,
                                gsPadX - 32,
                                gsPadY - 32,
                                64,
                                64
                        );
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                            // Teleport!
                            auber.teleport(wsPad);
                            isTeleportGuiOpen = false;
                        }
                    } else {
                        batch.draw(
                                padHighlight,
                                gsPadX - 32,
                                gsPadY - 32,
                                64,
                                64
                        );
                    }
                }

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
                if (auber.canTeleport()) {
                    Assets.fonts.fixedsys18.draw(
                            batch,
                            "Press F to teleport",
                            50, 50
                    );
                    if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                        this.isTeleportGuiOpen = true;
                    }
                } else {
                    Assets.fonts.fixedsys18.draw(
                            batch,
                            "Can't teleport!",
                            50, 50
                    );
                }
            }
        } else {
            if (isTeleportGuiOpen) {
                this.isTeleportGuiOpen = false;
            }
        }
        // KEYPAD UI START
        if (isAtKeypad()) {
            if (this.isKeypadGuiOpen) {
                // draw keypad like we did with the teleport gui
                scaleGui(auber.world.map.keypadTexture, 0.9f, batch);
            }
            else {
                Assets.fonts.fixedsys18.draw(
                        batch,
                        "Press F to use keypad",
                        50, 50
                );
                if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                    this.isKeypadGuiOpen = true;
                }
            }
        }
        else {
            this.isKeypadGuiOpen = false;
        }
        // KEYPAD UI END
    }

    private boolean isAtPad() {
        for (Vector2 pad : this.auber.world.map.teleportPads) {
            if (pad.dst2(this.auber.position) <= Math.pow(Map.TELEPORT_PAD_USE_RANGE, 2)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAtHealPoint() {
        return this.auber.world.map.healPoint.dst2(this.auber.position) <= Math.pow(Map.KEYPAD_USE_RANGE, 2);
    }

    private boolean isAtKeypad() {
        for (Vector2 pad : this.auber.world.map.keypads) {
            if (pad.dst2(this.auber.position) <= Math.pow(Map.KEYPAD_USE_RANGE, 2)) {
                return true;
            }
        }
        return false;
    }

    private void scaleGui(Texture texture, float cover, SpriteBatch batch) {
        float mapAspectRatio = (texture.getWidth() * 1f)/(texture.getHeight() * 1f); // texture width / texture height
        float currentAspectRatio = (Gdx.graphics.getWidth() * 1f)/(Gdx.graphics.getHeight() * 1f);
        float defaultAspectRatio = 16f/9f;
        // If aspect ratio of screen is less than the aspect ratio of map texture, then the width of the texture
        // needs to be 90% the width of the screen and vice versa
        if (currentAspectRatio > mapAspectRatio) {
            // float drawMapWidth is used to store the draw width of the texture. This is calculated using the draw height, this is then divided
            // by the ratios of the current and default aspect ratios before being converted into the draw width
            float drawMapHeight = 720f * cover;
            float drawMapWidth = (drawMapHeight/(currentAspectRatio/defaultAspectRatio)) * mapAspectRatio;
            Vector2 drawMapOrigin = new Vector2((1280f / 2f) - (0.5f * drawMapWidth), 720f * ((1f - cover)/2f));
            batch.draw(
                    texture,
                    drawMapOrigin.x,
                    drawMapOrigin.y,
                    drawMapWidth,
                    drawMapHeight
            );
        }
        else{
            // same as the process to calculate float drawMapWidth, except we know the draw width this time, and so are calculating the draw height
            float drawMapWidth = 1280f * cover;
            float drawMapHeight = (drawMapWidth*(currentAspectRatio/defaultAspectRatio)) / mapAspectRatio;
            Vector2 drawMapOrigin = new Vector2(1280f * ((1f - cover)/2f), (720f / 2f) - (0.5f * drawMapHeight));
            batch.draw(
                    texture,
                    drawMapOrigin.x,
                    drawMapOrigin.y,
                    drawMapWidth,
                    drawMapHeight
            );
        }
    }

    private void drawHealthBar(ShapeRenderer shapeRender){
        float ssScreenW = Gdx.graphics.getWidth();
        float ssScreenH = Gdx.graphics.getHeight();
        float xRatio =  ssScreenW * (200f/1280f);
        float yRatio = ssScreenH * (40f/720f);
        shapeRender = new ShapeRenderer();
        shapeRender.setAutoShapeType(true);
        shapeRender.begin(ShapeRenderer.ShapeType.Filled);
        // Draw bar background
        shapeRender.setColor(Color.GRAY);
        shapeRender.rect(
                ssScreenW * (15f/1280f),
                ssScreenH - (ssScreenH * (60f/720f)),
                ssScreenW * (210f/1280f),
                ssScreenH * (50f/720f)
        );
        // Draw the health bar
        shapeRender.setColor(Color.GREEN);
        shapeRender.rect(
                ssScreenW * (20f/1280f),
                ssScreenH - (ssScreenH * (55f/720f)),
                (auber.getHealth()/100f) * ssScreenW * (200f/1280f),
                ssScreenH * (40f/720f)
        );
        shapeRender.end();
    }
}
