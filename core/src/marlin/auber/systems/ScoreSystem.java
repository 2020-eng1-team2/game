package marlin.auber.systems;

import marlin.auber.common.System;

public class ScoreSystem implements System {
    private float score = 0;
    /**
     * As per requirements of the game, 8 Infiltrators must be arrested for the player to win
     */
    private final float maxScore = 8;
    public void tick() {

    }

    public void arrestedInfiltrator() {
        this.score++;
    }

    public boolean gameWin() {
        if (score >= maxScore) {
            return true;
        }
        return false;
    }
}
