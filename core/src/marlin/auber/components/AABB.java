package marlin.auber.components;

import com.badlogic.gdx.math.Vector2;
import marlin.auber.common.Component;

/**
 * An axis-aligned bounding box.
 *
 * The {@code tags} control what roles the AABB plays in-game.
 */
public class AABB extends Component {
    public Vector2 size;
    /**
     * A bitmask of tags for this AABB. To check if this AABB has a given tag, use {@code hasTag}.
     */
    public int tags;

    /**
     * This AABB should be used to determine the rendered size of an object.
     */
    public static final int TAG_RENDER = 1 << 1;
    /**
     * This AABB should be used for precise collision.
     */
    public static final int TAG_COLLISION = 1 << 2;
    /**
     * This AABB should be used for collision on the X axis - in other words,
     * the collision system will test the bottom-left and bottom-right corners of the AABB
     * for collision.
     */
    public static final int TAG_COLLISION_X_ONLY = 1 << 3;

    public AABB(float w, float h) {
        this(w, h, 0);
    }
    public AABB(float w, float h, int tags) {
        this.size = new Vector2(w, h);
        this.tags = tags;
    }

    /**
     * Whether this AABB has {@code tag}.
     * @param tag the tag to check
     * @return whether this AABB has that tag
     */
    public boolean hasTag(int tag) {
        return (this.tags & tag) == tag;
    }
}
