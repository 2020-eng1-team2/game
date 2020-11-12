package marlin.auber.common;

import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Timer is a generic abstraction for one-off timers (e.g. cooldowns).
 */
public class Timer {
    // TODO: consider object pools?
    private static final Map<String, Timer> timers = new HashMap<>();

    private String id;
    private float remaining;

    public static Timer createTimer(float timeout) {
        Timer timer = new Timer();
        timer.id = UUID.randomUUID().toString();
        timer.remaining = timeout;

        Timer.timers.put(timer.id, timer);

        return timer;
    }

    public static void tickAll() {
        float delta = Gdx.graphics.getDeltaTime();
        for (Timer timer : timers.values()) {
            timer.remaining = Math.max(0, timer.remaining - delta);
        }
    }

    public float getRemaining() {
        return this.remaining;
    }

    public void reset(float timeout) {
        this.remaining = timeout;
    }
}
