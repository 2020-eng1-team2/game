package marlin.auber.components;

import marlin.auber.common.Component;

public class Score extends Component {
    private int score = 0;
    public int getScore() {
        return this.score;
    }

    public void addScore() {
        this.score++;
    }
}
