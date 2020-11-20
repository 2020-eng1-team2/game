package marlin.auber.common;

import com.badlogic.gdx.Gdx;

public class Helpers {

    /**
     * Horizontal percentage of the screen to X-axis pixels
     * @param horizontalPercentage
     * @return
     */
    public static float hptw(float horizontalPercentage) {
        return Gdx.graphics.getWidth() * horizontalPercentage;
    }

    /**
     * Vertical percentage to height
     * @param verticalPercentage
     * @return
     */
    public static float vpth(float verticalPercentage) {
        return Gdx.graphics.getHeight() * verticalPercentage;
    }
}
