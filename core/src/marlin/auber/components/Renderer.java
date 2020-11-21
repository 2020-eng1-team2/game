package marlin.auber.components;

import marlin.auber.common.Component;

/**
 * An entity that should be rendered on screen.
 *
 * Entities should have this interface to determine the render order, and also one of
 * * {@link StaticRenderer} to render a static texture at the entity's {@link Position}
 * * {@link WalkingRenderer} to render a texture or animation depending on the entity's walking state
 */
public class Renderer extends Component {
    public final int order;

    public Renderer(int order) {
        this.order = order;
    }
}
