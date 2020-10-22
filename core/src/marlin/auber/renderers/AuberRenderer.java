package marlin.auber.renderers;

import com.badlogic.gdx.graphics.g2d.Batch;
import marlin.auber.common.Renderer;
import marlin.auber.models.Auber;

public class AuberRenderer implements Renderer {
    private Auber auber;

    public AuberRenderer(Auber auber) {
        this.auber = auber;
    }

    @Override
    public void tick(Batch batch) {

    }
}
