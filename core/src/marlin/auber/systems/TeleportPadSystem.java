package marlin.auber.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Assets;
import marlin.auber.common.Entity;
import marlin.auber.common.Globals;
import marlin.auber.common.System;
import marlin.auber.components.ActivePlayerCharacter;
import marlin.auber.components.Position;
import marlin.auber.components.TeleportTarget;
import marlin.auber.models.World;

import java.util.List;
import java.util.stream.Collectors;

public class TeleportPadSystem implements System {
    private final SpriteBatch guiBatch = new SpriteBatch();
    private boolean isTeleportGuiOpen = false;

    private final Texture map = World.getWorld().map.mapTexture;
    private final Texture pad = new Texture(Gdx.files.internal("graphics/teleportHighlight.png"));
    private final Texture highlight = new Texture(Gdx.files.internal("graphics/teleportHighlightActive.png"));

    @Override
    @SuppressWarnings("unchecked")
    public void tick() {
        ActivePlayerCharacter player = Entity
                .getAllEntitiesWithComponents(ActivePlayerCharacter.class)
                .get(0)
                .getComponent(ActivePlayerCharacter.class);
        if (isInRangeOfPad()) {
            if (!guiBatch.isDrawing()) {
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                guiBatch.begin();
            }
            if (player.teleportCooldown.isOver()) {
                if (isTeleportGuiOpen) {
                    drawTeleportGui(
                            Entity.getAllEntitiesWithComponents(Position.class, TeleportTarget.class)
                                    .stream()
                                    .map(x -> x.getComponent(Position.class).position)
                                    .collect(Collectors.toList())
                    );
                } else {
                    Assets.fonts.cnr.draw(
                            guiBatch,
                            "Press F to teleport",
                            50, 50
                    );
                    if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                        this.isTeleportGuiOpen = true;
                    }
                }
            } else {
                Assets.fonts.cnr.draw(
                        guiBatch,
                        String.format("Teleport recharged in %.1f", player.teleportCooldown.getRemaining()),
                        50, 50
                );
            }
            if (guiBatch.isDrawing()) {
                guiBatch.end();
            }
        } else {this.isTeleportGuiOpen = false;}
        if (!Globals.blockClicks && isTeleportGuiOpen) {
            Globals.blockClicks = true;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isInRangeOfPad() {
        Entity pc = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class).get(0);
        assert pc != null;
        Vector2 pcPosition = pc.getComponent(Position.class).position;
        assert pcPosition != null;
        for (Entity ent : Entity.getAllEntitiesWithComponents(TeleportTarget.class, Position.class)) {
            Vector2 padPosition = ent.getComponent(Position.class).position;
            TeleportTarget tgt = ent.getComponent(TeleportTarget.class);
            if (pcPosition.dst2(padPosition) <= Math.pow(tgt.activationRange, 2)) {
                return true;
            }
        }
        return false;
    }

    private void drawTeleportGui(List<Vector2> padPositions) {
        /* pad: pad texture to be drawn on screen
        map: texture of the map (not drawn on screen)
        highlight: highlight pad texture to be drawn
        cover: amount of screen covered by map
        offset: the offset of the texture from the top left of the map (screen space)
        batch: SpriteBatch renderer*/
        float mapAspectRatio = (map.getWidth() * 1f)/(map.getHeight() * 1f); // texture width / texture height
        float currentAspectRatio = (Gdx.graphics.getWidth() * 1f)/(Gdx.graphics.getHeight() * 1f);
        float defaultAspectRatio = 16f/9f;

        float drawMapWidth;
        float drawMapHeight;
        float padDrawWidth;
        float padDrawHeight;

        Vector2 drawMapTL;
        Vector2 drawPosition;
        Vector2 drawMapOrigin;

        float ssMouseX = Gdx.input.getX();
        float ssMouseY = Gdx.input.getY();
        float gsMouseY = Gdx.graphics.getHeight() - ssMouseY;
        // If aspect ratio of screen is less than the aspect ratio of map texture, then the width of the texture
        // needs to be 90% the width of the screen and vice versa
        float cover = 0.9f;
        if (currentAspectRatio > mapAspectRatio) {
            // Height of map is cover (90%) of screen height
            drawMapHeight = 720f * cover;
            drawMapWidth = (drawMapHeight/(currentAspectRatio/defaultAspectRatio)) * mapAspectRatio;
            drawMapTL = new Vector2((1280f / 2f) - (0.5f * drawMapWidth), 720f - (720f * ((1f - cover)/2f)));
            // pad diameter is 1/15th of the default screen height
            padDrawHeight = 720f / 15f;
            padDrawWidth = padDrawHeight/(currentAspectRatio/defaultAspectRatio);
            drawMapOrigin = new Vector2((1280f / 2f) - (0.5f * drawMapWidth), 720f * ((1f - cover)/2f));
        }
        else{
            drawMapWidth = 1280f * cover;
            drawMapHeight = (drawMapWidth*(currentAspectRatio/defaultAspectRatio)) / mapAspectRatio;
            drawMapTL = new Vector2(1280f * ((1f - cover)/2f), (720f / 2f) + (0.5f * drawMapHeight));
            // pad diameter is 1/15th of the default screen width
            padDrawWidth = 1280f / 15f;
            padDrawHeight = padDrawWidth * (currentAspectRatio/defaultAspectRatio);
            drawMapOrigin = new Vector2(1280f * ((1f - cover)/2f), (720f / 2f) - (0.5f * drawMapHeight));
        }
        guiBatch.draw(
                map,
                drawMapOrigin.x,
                drawMapOrigin.y,
                drawMapWidth,
                drawMapHeight
        );
        for (Vector2 wsPad : padPositions) {
            Vector2 ssOffset = World.getWorld().map.gameSpaceToPixelSpace(wsPad);
            drawPosition = new Vector2(drawMapTL.x + (drawMapWidth * (ssOffset.x / map.getWidth())), drawMapTL.y - (drawMapHeight * (ssOffset.y / map.getHeight())));
            // draw pad
            if (Vector2.dst2(drawPosition.x, drawPosition.y, ssMouseX * (1280f / Gdx.graphics.getWidth()), gsMouseY * (720f / Gdx.graphics.getHeight())) < Math.pow(32, 2)) {
                guiBatch.draw(
                        highlight,
                        drawPosition.x - (padDrawWidth / 2f),
                        drawPosition.y - (padDrawHeight / 2f),
                        padDrawWidth,
                        padDrawHeight
                );
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    // Teleport!
                    teleportTo(wsPad);
                    isTeleportGuiOpen = false;
                }
            } else {
                guiBatch.draw(
                        pad,
                        drawPosition.x - (padDrawWidth / 2f),
                        drawPosition.y - (padDrawHeight / 2f),
                        padDrawWidth,
                        padDrawHeight
                );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void teleportTo(Vector2 where) {
        Entity player = Entity.getAllEntitiesWithComponents(ActivePlayerCharacter.class, Position.class).get(0);
        player.getComponent(Position.class).position.set(where);
        player.getComponent(ActivePlayerCharacter.class).teleportCooldown.reset(5f);
        Globals.blockClicks = false;
    }
}
