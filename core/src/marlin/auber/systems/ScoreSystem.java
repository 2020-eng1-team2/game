package marlin.auber.systems;

import marlin.auber.common.Entity;
import marlin.auber.common.System;
import marlin.auber.components.Score;

public class ScoreSystem implements System {
    private float score = 0;

    /**
     * As per requirements of the game, 8 Infiltrators must be arrested for the player to win
     */
    private final float maxScore = 8;
    public void tick() {
        this.score = Entity.getAllEntitiesWithComponents(Score.class).get(0).getComponent(Score.class).getScore();
    }

    public boolean gameWin() {
        if (score >= maxScore) {
            return true;
        }
        return false;
    }
}
